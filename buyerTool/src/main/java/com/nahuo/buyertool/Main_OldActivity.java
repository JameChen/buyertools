package com.nahuo.buyertool;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.nahuo.buyer.tool.R;
import com.nahuo.library.controls.LoadingDialog;
import com.nahuo.library.helper.MD5Utils;
import com.nahuo.service.autoupdate.AppUpdate;
import com.nahuo.service.autoupdate.AppUpdateService;
import com.nahuo.buyertool.api.AccountAPI;
import com.nahuo.buyertool.api.ApiHelper;
import com.nahuo.buyertool.api.HttpUtils;
import com.nahuo.buyertool.api.RequestMethod.ShopMethod;
import com.nahuo.buyertool.broadcast.ConnectionChangeReceiver;
import com.nahuo.buyertool.broadcast.ConnectionChangeReceiver.Listener;
import com.nahuo.buyertool.broadcast.NahuoBroadcastReceiver;
import com.nahuo.buyertool.common.Const;
import com.nahuo.buyertool.common.LastActivitys;
import com.nahuo.buyertool.common.SpManager;
import com.nahuo.buyertool.eventbus.BusEvent;
import com.nahuo.buyertool.eventbus.EventBusId;
import com.nahuo.buyertool.model.PublicData;
import com.nahuo.buyertool.model.UserModel;
import com.nahuo.buyertool.task.CheckUpdateTask;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import de.greenrobot.event.EventBus;

public class Main_OldActivity extends TabActivity implements View.OnClickListener {

    public static final String TAG_CHAT = "TAG_CHAT";
    public static final String TAG_MYITEMS = "TAG_MYITEMS";
    public static final String TAG_ALLITEMS = "TAG_ALLITEMS";
    public static final String TAG_MYGROUP = "TAG_MYGROUP";
    private static final String TAG_SETTING = "TAG_SETTING";
    private static final int REQUEST_OPEN_CAMERA = 1;
    private static final String TAG = "Wp_MainActivity";
    public static final String SELECT_MY_ITEM = "com.nahuo.wp.MainOldActivity.toMyItem";
    public static final String SELECT_ALL_ITEM = "com.nahuo.wp.MainOldActivity.toAllItem";
    public static final String RELOAD_NEWS_LOADED = "com.nahuo.wp.MainOldActivity.reloadNewsLoaded";
    private Main_OldActivity vThis = this;
    private TabHost mtabHost;
    private AppUpdate mAppUpdate;
    private MyBroadcast mybroadcast;
    private LoadingDialog loadingDialog = null;
    private boolean viewIsLoaded = false;
    private static final String ERROR_PREFIX = "error:";
    // public static final String EXTRA_TAG_VIEW_ID = "EXTRA_TAG_RES_ID";
//    private ImageButton scanBtn;
    // private PopupWindow popupWindow;
    // IM是否已经初始化
    private boolean mIMInited;
    private ConnectionChangeReceiver mConnectionChangeReceiver;
    private int mLastNetworkType = -100;
    // private boolean hasNews = false;

    private TextView mTvChatNew;
    private View mAllTab;
    private EventBus mEventBus = EventBus.getDefault();

    private TextView txtMsgNumber;

    private static enum Step {
        LOAD_USER_INFO, LOAD_PAY_USER_INFO, LOAD_SHOP_INFO, LOAD_BASE_SET,
        // LOAD_HAS_NEWS,
        TO_USERINFO_ACTIVITY_BY_DOMAIN, TO_USERINFO_ACTIVITY_BY_SHOPID
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_oldmain);
        mEventBus.registerSticky(this);
        loadingDialog = new LoadingDialog(vThis);
        // 初始化版本自动更新组件
        mAppUpdate = AppUpdateService.getAppUpdate(this);

        if (SpManager.getShopId(this) > 0) {
            initView();
        } else {
            viewIsLoaded = false;
        }
        initData();
        initScan();
        mConnectionChangeReceiver = new ConnectionChangeReceiver();
        mConnectionChangeReceiver.setListener(new Listener() {
            @Override
            public void onChange(boolean networkAvailable) {
                initNetworkTactics();
            }
        });

        // 注册通知
        mybroadcast = new MyBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SELECT_MY_ITEM);
        filter.addAction(SELECT_ALL_ITEM);
        filter.addAction(RELOAD_NEWS_LOADED);

        registerReceiver(mybroadcast, filter);
        registerReceiver(mConnectionChangeReceiver, new IntentFilter(NahuoBroadcastReceiver.ACTION_NETWORK_CHANGED));

    }

    private void initNetworkTactics() {
        int bNetworkWeight = 0;// 网络环境加权值，>0表示增加配置增加，=0表示配置不变，<0表示配置降级

        ConnectivityManager mConnectivity = (ConnectivityManager) vThis.getSystemService(Context.CONNECTIVITY_SERVICE);

        TelephonyManager mTelephony = (TelephonyManager) vThis.getSystemService(Context.TELEPHONY_SERVICE); // 检查网络连接，如果无网络可用，就不需要进行连网操作等
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if (info == null || !mConnectivity.getBackgroundDataSetting()) {
            // 无网络
            Toast.makeText(vThis, "网络未连接", Toast.LENGTH_LONG).show();
            return;
        }

        // 判断网络连接类型，只有在2G/3G/wifi里进行一些数据更新。
        int netType = info.getType();
        int netSubtype = info.getSubtype();
        if (mLastNetworkType == -100) {
            mLastNetworkType = netSubtype;
        }
        if (netType == ConnectivityManager.TYPE_WIFI) {
            bNetworkWeight = 1;
            if (mLastNetworkType != netSubtype) {
                Toast.makeText(vThis, "当前使用 WIFI 环境", Toast.LENGTH_LONG).show();
            }
        } else if (netType == ConnectivityManager.TYPE_MOBILE && netSubtype == TelephonyManager.NETWORK_TYPE_UMTS
                && !mTelephony.isNetworkRoaming()) {
            bNetworkWeight = 0;
            if (mLastNetworkType != netSubtype) {
                Toast.makeText(vThis, "当前使用 3G 环境", Toast.LENGTH_LONG).show();
            }
        } else if (netSubtype == TelephonyManager.NETWORK_TYPE_GPRS || netSubtype == TelephonyManager.NETWORK_TYPE_CDMA
                || netSubtype == TelephonyManager.NETWORK_TYPE_EDGE) {
            bNetworkWeight = -1;
            if (mLastNetworkType != netSubtype) {
                Toast.makeText(vThis, "当前使用 2G 环境", Toast.LENGTH_LONG).show();
            }
        } else {
            bNetworkWeight = 0;
            if (mLastNetworkType != netSubtype) {
                Toast.makeText(vThis, "未识别网络环境", Toast.LENGTH_LONG).show();
            }
        }

        // 根据加权值重配置
        switch (bNetworkWeight) {
            case 0:
                Const.UPLOAD_ITEM_MAX_SIZE = 180;
                Const.DOWNLOAD_ITEM_SIZE = 24;
                break;
            case -1:
                Const.UPLOAD_ITEM_MAX_SIZE = 160;
                Const.DOWNLOAD_ITEM_SIZE = 18;
                break;
            case 1:
                Const.UPLOAD_ITEM_MAX_SIZE = 200;
                Const.DOWNLOAD_ITEM_SIZE = 24;
                break;

            default:
                break;
        }
        mLastNetworkType = netSubtype;

    }

    private void initScan() {
//        scanBtn = (ImageButton) findViewById(R.id.home_scan);
//        scanBtn.setOnClickListener(this);
    }

    /**
     * 加载消息推送对象
     */
    private void InitJPush() {
        try {
            // JPushInterface.setDebugMode(true);
            JPushInterface.init(this);
            JPushInterface.resumePush(this);
//            JPushInterface.setAliasAndTags(this, String.valueOf(SpManager.getUserId(this)), null);
            JPushInterface.setAlias(this, String.valueOf(SpManager.getUserId(this)), null);
            //测试时，tag标志与发布版本区分开
            Set set = new HashSet();
            if (com.nahuo.buyertool.common.Debug.CONST_DEBUG) {
                set.add("test");
            } else {
                set.add("product");
            }
            JPushInterface.setTags(this, set, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        try {
            mAppUpdate.callOnResume();
            JPushInterface.onResume(vThis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onPause() {
        super.onPause();
        try {
            mAppUpdate.callOnPause();
            // JPushInterface.onPause(vThis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onEventMainThread(BusEvent event) {
        switch (event.id) {
            case EventBusId.MANAGER_MSG_NUMBER:
//                updateManagerNumber((Integer) event.data);
                break;
            case EventBusId.ON_APP_EXIT:
                finish();
                break;
            case EventBusId.WEIXUN_NEW_MSG:
                if (mTvChatNew == null) {
                    return;
                }
                String num = event.data.toString();
                if (TextUtils.isEmpty(num)) {
                    mTvChatNew.setVisibility(View.GONE);
                } else {
                    mTvChatNew.setVisibility(View.VISIBLE);
                    mTvChatNew.setText(event.data.toString());
                }

                break;
            case EventBusId.MAIN_CHANGE_CURRENT_TAB:
                String tag = event.data.toString();
                mtabHost.setCurrentTabByTag(tag);

                break;

        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingDialog.isShowing()) {
            loadingDialog.stop();
        }
        mEventBus.unregister(this);
        // 注销通知
        unregisterReceiver(mybroadcast);
        unregisterReceiver(mConnectionChangeReceiver);
        LastActivitys.getInstance().clear();
    }

    public class MyBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String actionStr = intent.getAction();
            if (actionStr.equals(SELECT_MY_ITEM)) {
                if (!mtabHost.getCurrentTabTag().equals(TAG_MYITEMS)) {
                    mtabHost.setCurrentTabByTag(TAG_MYITEMS);
                }
            } else if (actionStr.equals(SELECT_ALL_ITEM)) {
                if (!mtabHost.getCurrentTabTag().equals(TAG_ALLITEMS)) {
                    mtabHost.setCurrentTabByTag(TAG_ALLITEMS);
                }
            }
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        new Task(Step.LOAD_SHOP_INFO).execute();
        new Task(Step.LOAD_USER_INFO).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new Task(Step.LOAD_BASE_SET).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        // new Task(Step.LOAD_HAS_NEWS).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        initManagerMsgNumber();
    }

    /**
     * 第一次进入app需要在这里初始化一下消息数 created by 陈智勇 2015-5-26 下午4:55:49
     */
    private void initManagerMsgNumber() {
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... p) {
                Map<String, String> params = new HashMap<String, String>();
                String json = "";
                try {
                    json = HttpUtils.httpGet(ShopMethod.SHOP_AGENT_ORDER_GET_PENDING_ORDER_COUNT, params,
                            PublicData.getCookie(vThis));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int count = 0;
                JSONObject jo;
                try {
                    jo = new JSONObject(json);
                    count += jo.getInt("BuyCount");
                    count += jo.getInt("SellCount");
                    count += jo.getInt("AgentCount");
                    count += jo.getInt("ShipCount");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    json = HttpUtils.httpGet("shop/agent/getapplyusercount", params, PublicData.getCookie(vThis));
                    count += Double.valueOf(json).intValue();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return count;
            }

            @Override
            protected void onPostExecute(Integer result) {
                updateManagerNumber(result);
            }
        }.execute();
    }

    // 初始化数据
    private void initView() {
        if (viewIsLoaded) {
            return;
        }

        mtabHost = getTabHost();
//        mtabHost.setOnTabChangedListener(this);
        // // 添加选项卡

//        addTab(TAG_ALLITEMS, R.drawable.tabicon_quick_sale, "拼货", PinHuoActivity.class);
//        addTab(TAG_MYGROUP, R.drawable.tabicon_my_group, "我的团", MyMainCollectionActivity.class);
//
//        addTab(TAG_MYITEMS, R.drawable.tabicon_yue_pin, "约拼", CommonListActivity.class);
//        addTab(TAG_CHAT, R.drawable.tabicon_chat, "微询", ChatMainActivity.class);
        addTab(TAG_SETTING, R.drawable.tabicon_me, "我的", MainAcivity.class);
        // addManagerTab();

        mtabHost.setCurrentTabByTag(TAG_ALLITEMS);
        initAllItemTab();

        // 检查更新
        mtabHost.postDelayed(new Runnable() {
            @Override
            public void run() {
                new CheckUpdateTask(vThis, mAppUpdate, false, false).execute();
            }
        }, 30000);
        //
        viewIsLoaded = true;

    }

    // private void addManagerTab() {
    // Intent intent = new Intent().setClass(this, WPManageActivity.class);
    // View view = View.inflate(this, R.layout.tab_main_indicator_point, null);
    // ImageView imageView = (ImageView)view.findViewById(R.id.tab_icon);
    // imageView.setImageResource(R.drawable.tabitem_setting_item);
    // TextView tView = ((TextView)view.findViewById(R.id.tab_title));
    // tView.setText("管理");
    // TextView tvNew = ((TextView)view.findViewById(R.id.tab_new));
    // if (TAG_CHAT.equals(TAG_SETTING)) {
    // mTvChatNew = tvNew;
    // }
    // txtMsgNumber = (TextView)view.findViewById(R.id.main_msg_numbrer);
    // mtabHost.addTab(mtabHost.newTabSpec(TAG_SETTING).setIndicator(view).setContent(intent));
    // }

    /**
     * 显示管理图标的红点数量 created by 陈智勇 2015-5-26 下午4:36:36
     *
     * @param number
     */
    private void updateManagerNumber(int number) {
//        if (txtMsgNumber != null) {
//            txtMsgNumber.setVisibility(number > 0 ? View.VISIBLE : View.GONE);
//            txtMsgNumber.setText(String.valueOf(number > 99 ? "99+" : number));
//        }
    }

    private void initAllItemTab() {
        if (mAllTab == null) {
            if (TAG_ALLITEMS.equals(mtabHost.getCurrentTabTag())) {
                mAllTab = mtabHost.getCurrentTabView();
            }
            mAllTab.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP && TAG_ALLITEMS.equals(mtabHost.getCurrentTabTag())) {
                            if(System.currentTimeMillis() - mPinHuoLastClickMillis< 700){
                                EventBus.getDefault().postSticky(BusEvent.getEvent(EventBusId.REFRESH_ALL_ITEMS));
                            }
                            mPinHuoLastClickMillis = System.currentTimeMillis();
                            // EventBus.getDefault().postSticky(QuickSellFragment.EVENT_RELOAD);

                    }
                    return false;
                }
            });
        }
    }
    private long mPinHuoLastClickMillis;
    int count = 1;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.home_scan:
//                showScanPopUp(v);
//                break;
        }
    }

    private class Task extends AsyncTask<Object, Void, Object> {
        private Step mStep;

        public Task(Step step) {
            mStep = step;
        }

        @Override
        protected void onPreExecute() {
            switch (mStep) {
                case LOAD_USER_INFO:
                    // loadingDialog.start("加载数据中...");
                    break;
                case LOAD_BASE_SET:
                    // loadingDialog.start("加载数据中...");
                    break;
                // case LOAD_HAS_NEWS:
                // break;
                case LOAD_SHOP_INFO:
                    if (!viewIsLoaded) {
                        loadingDialog.start(getString(R.string.main_shopinfo));
                    }
                    break;
                case LOAD_PAY_USER_INFO:
                    break;
                case TO_USERINFO_ACTIVITY_BY_DOMAIN:
                    loadingDialog.start("加载数据中...");
                    break;
                case TO_USERINFO_ACTIVITY_BY_SHOPID:
                    loadingDialog.start("加载数据中...");
                    break;
            }
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                switch (mStep) {
                    case LOAD_USER_INFO:
                        return loadUserInfo();
                    case LOAD_BASE_SET:
                        return loadBaseSet();
                    // case LOAD_HAS_NEWS:
                    // return loadHasNews();
//                    case LOAD_SHOP_INFO:
//                        return loadShopInfo();
//                    case LOAD_PAY_USER_INFO:
//                        return loadPayUserInfo();
//                    case TO_USERINFO_ACTIVITY_BY_DOMAIN:// 根据domain跳转到名片页
//                        String domain = params[0].toString();
//                        ShopInfoModel shopInfo = ShopSetAPI.getShopInfoByDomain(domain, PublicData.getCookie(vThis));
//                        return shopInfo.getUserID();
//                    case TO_USERINFO_ACTIVITY_BY_SHOPID:
//                        String shopid = params[0].toString();
//                        ShopInfoModel shopInfoById = ShopSetAPI
//                                .getShopInfoByShopID(shopid, PublicData.getCookie(vThis));
//                        return shopInfoById.getUserID();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "error:" + e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (loadingDialog.isShowing()) {
                loadingDialog.stop();
            }
            if (result instanceof String && ((String) result).startsWith(ERROR_PREFIX)) {
                String msg = ((String) result).replace(ERROR_PREFIX, "");
                ViewHub.showLongToast(vThis, msg);
                // 验证result
                if (msg.toString().startsWith("401") || msg.toString().startsWith("not_registered")) {
                    ApiHelper.checkResult(msg, vThis);
                }

                return;
            }
            switch (mStep) {
                case LOAD_USER_INFO:
                    userInfoLoaded(result);
                    break;
                case LOAD_BASE_SET:
                    baseSetLoaded(result);
                    break;
                // case LOAD_HAS_NEWS:
                // hasNewsLoaded(result);
                // break;
//                case LOAD_SHOP_INFO:
//                    shopInfoLoaded(result);
//                    break;
//                case LOAD_PAY_USER_INFO:
//                    payUserInfoLoaded((JPayUser) result);
//                    break;
                case TO_USERINFO_ACTIVITY_BY_DOMAIN:
                case TO_USERINFO_ACTIVITY_BY_SHOPID:
//                    int userId = (Integer) result;
//                    Intent userInfoIntent = new Intent(vThis, UserInfoActivity.class);
//                    userInfoIntent.putExtra(UserInfoActivity.EXTRA_USER_ID, userId);
//                    startActivity(userInfoIntent);
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        // ViewHub.showExitDialog(this);
        ViewHub.showExitLightPopDialog(vThis);
    }

    /**
     * @description 加载基础数据
     */
    private Object loadBaseSet() {
        try {
            AccountAPI.getMyShopBaseConfig(PublicData.getCookie(vThis), vThis);
            return "";
        } catch (Exception ex) {
            Log.e(TAG, "加载基础信息发生异常");
            ex.printStackTrace();
            return "error:" + ex.getMessage();
        }
    }

    /**
     * @description 基础数据加载完成
     */
    private void baseSetLoaded(Object result) {
    }

    /**
     * @description 读取完了是否有新广播帖子
     */
    // private void hasNewsLoaded(Object result) {
    // hasNews = Boolean.valueOf(result.toString());
    // }

    /**
     * @description 加载用户数据
     * @created 2014-9-3 下午1:42:46
     * @author ZZB
     */
    private Object loadUserInfo() {
        try {
            UserModel userinfo = AccountAPI.getInstance().getUserInfo(PublicData.getCookie(vThis));
            SpManager.setUserInfo(this, userinfo);

            String username = String.valueOf(userinfo.getUserID());
            String pwd = MD5Utils.encrypt32bit(username);


            if (userinfo != null) {
                return userinfo;
            } else {
                return "error:" + "没有找到个人";
            }
        } catch (Exception ex) {
            Log.e(TAG, "加载个人信息发生异常");
            ex.printStackTrace();
            return "error:" + ex.getMessage();
        }
    }

    /**
     * @description 用户数据加载完成
     * @created 2014-9-3 下午1:45:19
     * @author ZZB
     */
    private void userInfoLoaded(Object result) {
        PublicData.mUserInfo = (UserModel) result;
    }

    private void addTab(String tag, int tabImageSelector, String tabText, Class<?> fClass) {
        Intent intent = new Intent().setClass(this, fClass);
        View view = View.inflate(this, R.layout.tab_main_indicator, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.tab_icon);
        imageView.setImageResource(tabImageSelector);
        TextView tView = ((TextView) view.findViewById(R.id.tab_title));
        tView.setText(tabText);
        TextView tvNew = ((TextView) view.findViewById(R.id.tab_new));


        if (TAG_CHAT.equals(tag)) {
            mTvChatNew = tvNew;
        } else if (TAG_MYITEMS.equals(tag)) {
            intent.putExtra("gid", 60033);
        } else if (TAG_SETTING.equals(tag)) {
            txtMsgNumber = tvNew;
        }


        mtabHost.addTab(mtabHost.newTabSpec(tag).setIndicator(view).setContent(intent));
    }
}