package com.nahuo.buyertool;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.internal.LinkedTreeMap;
import com.gprinter.aidl.GpService;
import com.gprinter.io.GpDevice;
import com.gprinter.service.GpPrintService;
import com.luck.picture.lib.permissions.RxPermissions;
import com.luck.picture.lib.tools.ScreenUtils;
import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.Bean.AccountBean;
import com.nahuo.buyertool.Bean.LoginBean;
import com.nahuo.buyertool.Bean.TxUserInfo;
import com.nahuo.buyertool.Bean.UploadBean;
import com.nahuo.buyertool.activity.AlbumActivity;
import com.nahuo.buyertool.activity.BlueToothActivity;
import com.nahuo.buyertool.activity.MyStyleActivity;
import com.nahuo.buyertool.activity.SuppliersActivity;
import com.nahuo.buyertool.activity.UploadProgressActivity;
import com.nahuo.buyertool.adapter.CommodityAdatper;
import com.nahuo.buyertool.adapter.MeItemAdatper;
import com.nahuo.buyertool.api.AccountAPI;
import com.nahuo.buyertool.api.ApiHelper;
import com.nahuo.buyertool.api.CommodityAPI;
import com.nahuo.buyertool.api.ShopSetAPI;
import com.nahuo.buyertool.base.BaseAppCompatActivity;
import com.nahuo.buyertool.common.BlurTransform;
import com.nahuo.buyertool.common.Const;
import com.nahuo.buyertool.common.FileUtils;
import com.nahuo.buyertool.common.ListUtils;
import com.nahuo.buyertool.common.SpManager;
import com.nahuo.buyertool.db.ToolUploadDbHelper;
import com.nahuo.buyertool.dialog.AccountDialog;
import com.nahuo.buyertool.dialog.InfoDialog;
import com.nahuo.buyertool.eventbus.BusEvent;
import com.nahuo.buyertool.eventbus.EventBusId;
import com.nahuo.buyertool.exceptions.CatchedException;
import com.nahuo.buyertool.http.CommonSubscriber;
import com.nahuo.buyertool.http.HttpManager;
import com.nahuo.buyertool.http.OkHttpUtils;
import com.nahuo.buyertool.http.RxUtil;
import com.nahuo.buyertool.http.response.PinHuoResponse;
import com.nahuo.buyertool.iosdialog.widget.ActionSheetDialog;
import com.nahuo.buyertool.model.CommodityInfo;
import com.nahuo.buyertool.model.ImageViewModel;
import com.nahuo.buyertool.model.MeItemModel;
import com.nahuo.buyertool.model.PageraModel.BaseInfoList;
import com.nahuo.buyertool.model.PublicData;
import com.nahuo.buyertool.model.ShopInfoModel;
import com.nahuo.buyertool.model.UserModel;
import com.nahuo.buyertool.service.UploadManager;
import com.nahuo.buyertool.task.CheckUpdateTask;
import com.nahuo.buyertool.utils.JPushUtls;
import com.nahuo.buyertool.utils.JsonKit;
import com.nahuo.buyertool.utils.ListDataSave;
import com.nahuo.buyertool.utils.ListviewUtls;
import com.nahuo.library.controls.BottomMenuList;
import com.nahuo.library.controls.LightPopDialog;
import com.nahuo.library.controls.LoadingDialog;
import com.nahuo.library.controls.NoScrollListView;
import com.nahuo.library.helper.DisplayUtil;
import com.nahuo.library.helper.FunctionHelper;
import com.nahuo.library.helper.GsonHelper;
import com.nahuo.library.helper.ImageUrlExtends;
import com.nahuo.library.helper.MD5Utils;
import com.nahuo.library.helper.SDCardHelper;
import com.nahuo.live.xiaozhibo.login.TCUserMgr;
import com.nahuo.live.xiaozhibo.mainui.LiveListActivity;
import com.nahuo.service.autoupdate.AppUpdate;
import com.nahuo.service.autoupdate.AppUpdateService;
import com.squareup.picasso.Picasso;
import com.tencent.bugly.crashreport.CrashReport;
import com.xinlan.imageeditlibrary.editimage.utils.ListUtil;

import org.apache.http.util.EncodingUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class MainAcivity extends BaseAppCompatActivity implements AccountDialog.PopDialogListener, View.OnClickListener, MeItemAdatper.OnMeItemListener, InfoDialog.PopDialogListener {//, OnTitleBarClickListener
    private static final String TAG = MainAcivity.class.getSimpleName();
    private static final String DEBUG_TAG = "MainAcivity";
    private MainAcivity Vthis = this;
    private GridView mGrdClass;
    private MeItemAdatper adatper;
    private EventBus mEventBus = EventBus.getDefault();
    private CircleImageView mIvAvatar;
    private AppUpdate mAppUpdate;
    private boolean viewIsLoaded = false;
    private LoadingDialog loadingDialog = null;
    public static final String ERROR_PREFIX = "error:";
    private ListView typeList;
    private List<CommodityInfo> allCommodity;
    private CommodityAdatper adatperCommo;
    private boolean ControlBack = false;
    private ListDataSave save;
    private MainAcivity vThis;
    private ImageView my_bluetool_set;
    private TextView my_bluetool_info;
    private List<BaseInfoList.BtnListBean> BtnList1;
    private List<BaseInfoList.BtnListBean> BtnList2;
    private List<BaseInfoList.BtnListBean> BtnList3;
    private int numColumns = 3;
    private RelativeLayout rlTContent;

    public boolean GetControlBack() {
        return ControlBack;
    }

    public void setControlBack(boolean controlBack) {
        ControlBack = controlBack;
    }

    public String user_phone = "", user_passWord = "";
    long userId;
    private GpService mGpService;
    private PrinterServiceConnection conn = null;


    class PrinterServiceConnection implements ServiceConnection {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(DEBUG_TAG, "onServiceDisconnected() called");
            mGpService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mGpService = GpService.Stub.asInterface(service);
        }
    }

    private void connection() {
        conn = new PrinterServiceConnection();
        Log.i(DEBUG_TAG, "connection");
        Intent intent = new Intent(this, GpPrintService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService
    }

    private class ChangeLoginTask extends AsyncTask<Object, Void, Object> {

        private String phoneNo, password;
        long userId;

        public ChangeLoginTask(String phoneNo, String password, long userId) {
            this.phoneNo = phoneNo;
            this.password = password;
            this.userId = userId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog.start("切换登录中...");


        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                return AccountAPI.getInstance().userChangeLogin(phoneNo, password);

            } catch (Exception e) {
                e.printStackTrace();
                return "error:" + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            if (loadingDialog.isShowing()) {
                loadingDialog.stop();
            }
            if (result instanceof LoginBean) {
                LoginBean resultData = (LoginBean) result;
                if (resultData.isResult()) {
//                    if (resultData!=null){
//                        AccountBean accountBean=  resultData.getData();
//
//                    }
                    if (!TextUtils.isEmpty(PublicData.getCookie(MainAcivity.this))) {
                        SpManager.setCookie(vThis, PublicData.getCookie(vThis));
                    }
                    SpManager.setLoginAccount(vThis, phoneNo);
                    SpManager.setLoginPwd(vThis, password);
                    SpManager.setUserId(vThis, (int) userId);
                    SpManager.setSELECT_PURCHASE_DATA_Empty(vThis);
                    if (SpManager.getUserId(vThis) > -1) {
                        viewIsLoaded = false;
                        initView();
                    } else {
                        viewIsLoaded = false;
                    }
                    getTXUserInfo();
                    new Task(Step.LOAD_LIST_FILTER).execute();
                    new Task(Step.LOAD_SHOP_INFO).execute();
                    new Task(Step.LOAD_USER_INFO).execute();
                    new Task(Step.LOAD_BASE_SET).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    // new Task(Step.LOAD_MAIN_LIST_VIEW).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    load_main_list_view();
                } else {
                    ViewHub.showLongToast(vThis, ((LoginBean) result).getMessage());
                }
            } else if (result instanceof String && ((String) result).startsWith("error:")) {
                ViewHub.showLongToast(vThis, ((String) result).replace("error:", ""));

            } else {
                ViewHub.showLongToast(vThis, result.toString());
            }

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_bluetool_set:
                startActivity(new Intent(vThis, BlueToothActivity.class));
                break;
            case R.id.iv_userhead:
                final List<AccountBean> list = save.getDataList(Const.LOGIN_PRE_LIST_KEY);
                if (ListUtils.isEmpty(list)) {
                    AccountDialog.getInstance(this).setTitle("是否添加账号").setPositive(this).showDialog();
                } else {
                    if (list.size() > 1) {
                        ActionSheetDialog dialog = new ActionSheetDialog(MainAcivity.this)
                                .builder()
                                .setTitle("切换账号")
                                .setCancelable(true)
                                .setCanceledOnTouchOutside(true);
                        for (AccountBean xbean : list) {
                            dialog.addSheetItem(xbean.getUserName(), ActionSheetDialog.SheetItemColor.Blue
                                    , new ActionSheetDialog.OnSheetItemClickListener() {
                                        @Override
                                        public void onClick(int which) {
                                            //填写事件
                                            user_phone = list.get(which - 1).getPhoneNo();
                                            user_passWord = list.get(which - 1).getPwd();
                                            userId = list.get(which - 1).getUserID();
                                            // ViewHub.showShortToast(vThis,list.get(which - 1).getUserName());
                                            new ChangeLoginTask(user_phone, user_passWord, userId).execute();
                                        }
                                    });
                        }
                        dialog.show();
                    } else {
                        AccountDialog.getInstance(this).setTitle("是否添加账号").setPositive(this).showDialog();
                    }
                }

                break;
        }
    }

    @Override
    public void onAccountDialogButtonClick() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();// 需要finish这个页面，否则无法退出应
    }

    private enum Step {
        LOAD_USER_INFO, LOAD_BASE_SET, LOAD_MAIN_LIST_VIEW, LOAD_SHOP_INFO, LOAD_LIST_FILTER
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        my_bluetool_info = (TextView) findViewById(R.id.my_bluetool_info);
        rlTContent = (RelativeLayout) findViewById(R.id.rlTContent);
        connection();
        mEventBus.registerSticky(this);
        BWApplication.getInstance().addActivity(this);
        vThis = this;
        save = new ListDataSave(this, Const.LOGIN_PRE_KEY);
        loadingDialog = new LoadingDialog(Vthis);
        // 初始化版本自动更新组件
        mAppUpdate = AppUpdateService.getAppUpdate(this);
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            }
        }
        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Boolean aBoolean) {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
        //改为true条件，必须初始化布局
        if (SpManager.getUserId(this) > -1) {
            InitJPush();
            initView();
        } else {
            viewIsLoaded = false;
        }
        my_bluetool_set = (ImageView) findViewById(R.id.my_bluetool_set);
        my_bluetool_set.setOnClickListener(this);
        // 检查更新
        findViewById(R.id.my_setting).postDelayed(new Runnable() {
            @Override
            public void run() {
                new CheckUpdateTask(Vthis, mAppUpdate, false, false).execute();
            }
        }, 10000);
        getTXUserInfo();
        new Task(Step.LOAD_LIST_FILTER).execute();
        new Task(Step.LOAD_SHOP_INFO).execute();
        new Task(Step.LOAD_USER_INFO).execute();
        new Task(Step.LOAD_BASE_SET).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        // new Task(Step.LOAD_MAIN_LIST_VIEW).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        load_main_list_view();

    }

    /**
     * 加载消息推送对象
     */
    private void InitJPush() {
        Set<String> tagSet = new LinkedHashSet<>();
        tagSet.add("login");
        JPushUtls.setJpushTagAndAlias(this, 1, SpManager.getUserId(this) + "", tagSet);

    }

    private void getTXUserInfo() {
        Map<String, Object> params = new HashMap<>();
        params.put("deviceID", FunctionHelper.GetAndroidID(vThis));
        params.put("from", "buyertool");
        //  Log.v(TAG,"pwd : "+ SpManager.getLoginPwd(context));
        addSubscribe(HttpManager.getInstance().getPinHuoNoCacheApi(TAG
        ).getTXUserInfo(params)
                .compose(RxUtil.<PinHuoResponse<Object>>rxSchedulerHelper())
                .compose(RxUtil.<Object>handleResult())
                .subscribeWith(new CommonSubscriber<Object>(Vthis, true, "获取直播信息") {
                    @Override
                    public void onNext(Object o) {
                        super.onNext(o);
                        if (o != null) {
                            if (o instanceof LinkedTreeMap) {
                                TxUserInfo bean = GsonHelper.jsonToMyObject(JsonKit.mapToJson((LinkedTreeMap) o, null).toString(), TxUserInfo.class);
                                SpManager.setUserSig(vThis, bean.getUserSig());
                                SpManager.setIdentifier(vThis, bean.getIdentifier());
                                if (!TextUtils.isEmpty(bean.getIdentifier()))
                                    TCUserMgr.getInstance().loginTxLive(Vthis, bean.getIdentifier(), bean.getUserSig());
                            }
                        }
                    }


                }));
//        OkHttpUtils.getInstance().get(TAG, vThis, "user/ImUser/GetTXUserInfo",
//                OkHttpUtils.POST_TYPE_ASYN, params, true, "获取直播信息", true, new OkHttpUtils.BuyerToolCallback() {
//                    @Override
//                    public void onSuccess(Object data) {
//                        if (data instanceof LinkedTreeMap) {
//                            LinkedTreeMap map = (LinkedTreeMap) data;
//                            final TxUserInfo bean = GsonHelper.jsonToMyObject(JsonKit.mapToJson(map, null).toString(), TxUserInfo.class);
//                            if (bean != null) {
//                                SpManager.setUserSig(vThis, bean.getUserSig());
//                                SpManager.setIdentifier(vThis, bean.getIdentifier());
//                                if (!TextUtils.isEmpty(bean.getIdentifier()))
//                                    TCUserMgr.getInstance().loginTxLive(bean.getIdentifier(), bean.getUserSig());
//                            }
//                        }
//
//                    }
//
//                    @Override
//                    public void showDialog(final LoadingDialog dialog) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (dialog != null&&!vThis.isFinishing())
//                                    dialog.show();
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void closeDialog(final LoadingDialog dialog) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (dialog != null&&!vThis.isFinishing())
//                                    dialog.dismiss();
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onFailure(String code, final String msg) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                ViewHub.showShortToast(vThis, msg);
//                            }
//                        });
//
//                    }
//                });

    }

    private void load_main_list_view() {
        Map<String, Object> params = new HashMap<>();
        params.put("pwd", SpManager.getLoginPwd(vThis));
        params.put("from", "buyerandroid");
        String versionName = OkHttpUtils.getVersionName(vThis);
        // Log.v(TAG," 版本号 :"+versionName);
        if (!TextUtils.isEmpty(versionName)) {
            params.put("ver", versionName);
        }
        addSubscribe(HttpManager.getInstance().getPinHuoNoCacheApi(TAG
        ).GetBaseInfo2(params)
                .compose(RxUtil.<PinHuoResponse<BaseInfoList>>rxSchedulerHelper())
                .compose(RxUtil.<BaseInfoList>handleResult())
                .subscribeWith(new CommonSubscriber<BaseInfoList>(Vthis, true, R.string.loading) {
                    @Override
                    public void onNext(BaseInfoList bean) {
                        super.onNext(bean);
                        setListView(bean);
                    }


                }));
        //  Log.v(TAG,"pwd : "+ SpManager.getLoginPwd(context));
//        OkHttpUtils.getInstance().get(TAG, vThis, "buyertool/BuyerV2/GetBaseInfo2",
//                OkHttpUtils.POST_TYPE_ASYN, params, false, "", true, new OkHttpUtils.BuyerToolCallback() {
//                    @Override
//                    public void onSuccess(Object data) {
//                        if (data instanceof LinkedTreeMap) {
//                            LinkedTreeMap map = (LinkedTreeMap) data;
//                            final BaseInfoList bean = GsonHelper.jsonToMyObject(JsonKit.mapToJson(map, null).toString(), BaseInfoList.class);
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    setListView(bean);
//                                }
//                            });
//
//                        }
//
//                    }
//
//                    @Override
//                    public void showDialog(final LoadingDialog dialog) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (dialog != null&&!vThis.isFinishing())
//                                    dialog.show();
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void closeDialog(final LoadingDialog dialog) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (dialog != null&&!vThis.isFinishing())
//                                    dialog.dismiss();
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onFailure(String code, final String msg) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                ViewHub.showShortToast(vThis, msg);
//                            }
//                        });
//
//                    }
//                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            JPushInterface.resumePush(getApplicationContext());
            SpManager.setStallKeyWord(this, "");
            if (mGpService != null) {
                if (mGpService.getPrinterConnectStatus(BWApplication.getInstance().PrinterId) == GpDevice.STATE_CONNECTED) {
                    if (my_bluetool_info != null) {
                        my_bluetool_info.setText("蓝牙连接状态：已连接");
                    }
                } else {
                    if (my_bluetool_info != null) {
                        my_bluetool_info.setText("蓝牙连接状态：未连接");
                    }
                }
            } else {
                if (my_bluetool_info != null) {
                    my_bluetool_info.setText("蓝牙连接状态：未连接");
                }
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
        //  new Task(Step.LOAD_MAIN_LIST_VIEW).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onPopDialogButtonClick(int ok_cancel, UploadBean bean) {
        startActivity(new Intent(MainAcivity.this, UploadProgressActivity.class));
    }

    // 初始化数据
    private void initView() {
//        if (viewIsLoaded) {
//            return;
//        }
        mGrdClass = (GridView) Vthis.findViewById(R.id.grd_class);
        findViewById(R.id.my_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Vthis, MeSettingActivity.class);
                startActivity(intent);
            }
        });
        typeList = (NoScrollListView) findViewById(R.id.list_class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            rlTContent.setPadding(0, ScreenUtils.getStatusBarHeight(vThis), 0, 0);
        }
        // initgrid();
        initData();
        //
        viewIsLoaded = true;
//        Intent intent = new Intent(this, ItemDetailsActivity.class);
//            intent.putExtra(ItemDetailsActivity.EXTRA_ID, 123);
//        intent.putExtra(ItemDetailsActivity.EXTRA_QSID, 123);
//        intent.putExtra("type", CommonListActivity.ListType.待开单);
//        startActivity(intent);
    }

    private void showNoUplaodDialog() {
        UploadManager.getInstance(this).setList(ToolUploadDbHelper.getInstance(this).getAllUploadItems(SpManager.getUserId(this)));
        List<UploadBean> list = UploadManager.getInstance(this).getAllTask();
        if (!ListUtils.isEmpty(list)) {
            int count = list.size();
            if (count > 0) {
                InfoDialog.getInstance(this).
                        setTitle("发现未上传款式").setContent("发现上次有" + count + "个款式未上传成功，点击确定查看").setRightStr("确定").setPositive(this).showDialog();
            }
        }
    }

    public String readFileData(String fileName) {

        String res = "";

        try {

            FileInputStream fin = openFileInput(fileName);

            int length = fin.available();

            byte[] buffer = new byte[length];

            fin.read(buffer);

            res = EncodingUtils.getString(buffer, "UTF-8");

            fin.close();

        } catch (Exception e) {

            e.printStackTrace();

        }

        return res;

    }

    /**
     * 初始化数据
     */
    private void initData() {
        String userName = SpManager.getUserName(BWApplication.getInstance());
        String logo = SpManager.getShopLogo(BWApplication.getInstance()).trim();// SpManager.getUserLogo(NHApplication.getInstance());
        TextView name = (TextView) Vthis.findViewById(R.id.txt_name);
        mIvAvatar = (CircleImageView) Vthis.findViewById(R.id.iv_userhead);
        mIvAvatar.setBorderWidth(DisplayUtil.dip2px(this, 2));
        mIvAvatar.setBorderColor(getResources().getColor(R.color.white));
        mIvAvatar.setOnClickListener(this);
        //name.setText(userName);
        String Account = SpManager.getLoginAccount(BWApplication.getInstance());
        if (!TextUtils.isEmpty(Account)) {
            name.setText(Account);
        }
        try {
            String url = "";
            if (TextUtils.isEmpty(logo)) {
                mIvAvatar.setImageResource(R.drawable.bg_main);
            } else {
                url = ImageUrlExtends.getImageUrl(logo, 8);
                Picasso.with(BWApplication.getInstance()).load(url)
                        .placeholder(R.drawable.empty_photo).into(mIvAvatar);
            }
            ///data/user/0/com.nahuo.buyer.tool/cache/1511935006846.jpg
            initLogoBgView(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if (TextUtils.isEmpty(logo)) {
//            url = Const.getShopLogo(SpManager.getUserId(BWApplication.getInstance()));
//        } else {
//            url = ImageUrlExtends.getImageUrl(logo, 8);
//        }
//        Picasso.with(BWApplication.getInstance()).load(url)
//                .placeholder(R.drawable.empty_photo).into(mIvAvatar);

    }

    private void togglePopupWindow() {
        BottomMenuList menu = new BottomMenuList(this);
        menu.setItems(getResources().getStringArray(R.array.menu_upload_image_texts))
                .setOnMenuItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0:
                                takePhoto();
                                break;
                            case 1:
                                fromAblum();
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    private Uri mPhotoUri;                                              // 拍照图片文件Uri
    private static final int REQUESTCODE_TAKEPHOTO = 11;                             // 拍照
    private static final int REQUESTCODE_FROMALBUM = 12;                              // 从手机相册选择
    private static final int REQUESTCODE_CUT = 3;                              // 裁剪
    private String mLogoPath = "";                             // 店标图片文件路径

    /**
     * 调用系统照相机拍照
     */
    private void takePhoto() {
        // 文件名
        String fileName = "logo" + System.currentTimeMillis() + ".jpg";

        // 封装Uri
        String imgDirPath = SDCardHelper.getDCIMDirectory() + File.separator + "weipu";
        SDCardHelper.createDirectory(imgDirPath);
        File file = new File(imgDirPath, fileName);
        Uri imageUri = Uri.fromFile(file);
        mPhotoUri = imageUri;
        // 调用系统照相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
        startActivityForResult(intent, REQUESTCODE_TAKEPHOTO);
    }

    /**
     * 从相册选择照片并裁剪
     */
    private void fromAblum() {
        // // 文件名
        // String fileName = "";
        // // 宽度相对高度的比例
        // int aspectX = 1;
        // // 高度相对宽度的比例
        // int aspectY = 1;
        // // 高度、宽度的最大像素值
        // int size = 120;
        //
        // fileName = "logo" + System.currentTimeMillis() + ".jpg";
        // aspectX = 1;
        // aspectY = 1;
        // size = 640;
        //
        // // 封装Uri
        // String imgDirPath = SDCardHelper.getDCIMDirectory() + File.separator + "weipu";
        // SDCardHelper.createDirectory(imgDirPath);
        // File file = new File(imgDirPath, fileName);
        // Uri imageUri = Uri.fromFile(file);
        // mPhotoUri = imageUri;
        //
        // // 调用系统相册的相关设置
        // Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // intent.addCategory(Intent.CATEGORY_OPENABLE);
        // intent.setType("image/*");
        // // 输出Uri
        // intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
        // intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        // intent.putExtra("crop", "true");
        // // 输出高宽比例
        // intent.putExtra("aspectX", aspectX);
        // intent.putExtra("aspectY", aspectY);
        // // 根据高宽比列计算高、宽
        // int width = aspectX > aspectY ? size : (size / aspectY) * aspectX;
        // int height = aspectX < aspectY ? size : (size / aspectX) * aspectY;
        // intent.putExtra("outputX", width);
        // intent.putExtra("outputY", height);
        // intent.putExtra("return-data", false);
        // startActivityForResult(Intent.createChooser(intent, "选择图片"), REQUESTCODE_FROMALBUM);

        Intent intent = new Intent(MainAcivity.this, AlbumActivity.class);
        intent.putExtra(AlbumActivity.EXTRA_MAX_PIC_COUNT, 1);
        startActivityForResult(intent, REQUESTCODE_FROMALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUESTCODE_TAKEPHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        if (!SDCardHelper.checkFileExists(mPhotoUri.getPath())) {
                            ViewHub.showShortToast(getApplicationContext(), "未找到图片：" + mPhotoUri.getPath());
                            return;
                        }
//                        int aspectX = 1;// 宽度相对高度的比例
//                        int aspectY = 1;// 高度相对宽度的比例
//                        int size = 500;// 高度、宽度的最大像素值
                        // 进入裁剪界面
//                        gotoCut(mPhotoUri, aspectX, aspectY, size);
                        cropPic(Uri.fromFile(new File(mPhotoUri.getPath())));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case REQUESTCODE_CUT:// 裁剪
                if (data != null)//要檢查,因為裁剪時按cancel回來也會來此,但result == null
                {
                    //取得裁剪後的照片
                    Bitmap cameraBitmap = (Bitmap) data.getExtras().get("data");
                    String imgDirPath = SDCardHelper.getSDCardRootDirectory() + "/weipu/upload_tmp/logo_temp";
                    SDCardHelper.createDirectory(imgDirPath);
                    String filePath = imgDirPath + "/" + System.currentTimeMillis() + ".jpg";
                    try {
                        FileUtils.saveBitmap(filePath, cameraBitmap);
                        displayImage(filePath, cameraBitmap);
                    } catch (Exception e) {
                        ViewHub.showLongToast(getApplicationContext(), "保存图片发生异常...");
                        Log.e(getClass().getSimpleName(), "保存图片发生异常 Exception:" + e.toString());
                        CrashReport.postCatchedException(new CatchedException("保存图片发生异常 Exception:" + e.toString()));
                    }
                } else {
                    Log.w(getClass().getSimpleName(), "crop image get data is null");
                    CrashReport.postCatchedException(new CatchedException("裁剪图片 return data is null"));
                }

//                if (resultCode == RESULT_OK) {//
//                try {
//                    if (!SDCardHelper.checkFileExists(mPhotoUri.getPath())) {
//                        ViewHub.showShortToast(getApplicationContext(), "未找到图片：" + mPhotoUri.getPath());
//                        return;
//                    }
//                    // 展示图片
//                    String srcPath = mPhotoUri.toString();
//                    displayImage(srcPath);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                }
                break;
            case REQUESTCODE_FROMALBUM:// 从手机相册选择
                if (resultCode == AlbumActivity.RESULTCODE_OK) {
                    // try {
                    // String srcPath = mPhotoUri.toString();
                    // if (data.getData() != null) {
                    // srcPath = data.getData().toString();
                    // }
                    // if (srcPath.startsWith("file://")) {
                    // if (!SDCardHelper.checkFileExists(srcPath.replace("file://", ""))) {
                    // Toast.makeText(vThis, "未找到图片：" + srcPath, Toast.LENGTH_SHORT).show();
                    // return;
                    // }
                    // displayImage(srcPath);
                    // } else if (srcPath.startsWith("content:")) {
                    // Uri selectedImage = data.getData();
                    // String picturePath = ImageTools.getFilePath4Uri(vThis,selectedImage);
                    // if (picturePath.startsWith("/storage"))
                    // {
                    // picturePath = "file://"+picturePath;
                    // }
                    // displayImage(picturePath);
                    // } else {
                    // if (!SDCardHelper.checkFileExists(srcPath)) {
                    // Toast.makeText(vThis, "未找到图片：" + srcPath, Toast.LENGTH_SHORT).show();
                    // return;
                    // }
                    // displayImage(srcPath);
                    // }
                    // } catch (Exception e) {
                    // e.printStackTrace();
                    // }
                    ArrayList<ImageViewModel> imgs = (ArrayList<ImageViewModel>) data
                            .getSerializableExtra(AlbumActivity.EXTRA_SELECTED_PIC_MODEL);
                    if (imgs.size() > 0) {
                        String picturePath = imgs.get(0).getOriginalUrl();
//                        int aspectX = 1;// 宽度相对高度的比例
//                        int aspectY = 1;// 高度相对宽度的比例
//                        int size = 500;// 高度、宽度的最大像素值

                        try {
//                            String imgDirPath = SDCardHelper.getSDCardRootDirectory() + "/weipu/upload_tmp/logo_temp";
//                            SDCardHelper.createDirectory(imgDirPath);
//                            String filePath = imgDirPath + "/" + System.currentTimeMillis() + ".jpg";
//                            // 拷贝一份选择的文件，进行裁切
//                            SDCardHelper.copyFile(picturePath, filePath);
//                            File file = new File(filePath);
////                            mPhotoUri = Uri.parse("file://" + (file.exists() && file.length() > 0 ? filePath : picturePath));
//                            mPhotoUri = Uri.fromFile(file.exists() && file.length() > 0 ? file : new File(picturePath));
//                            // 进入裁剪界面
//                            gotoCut(mPhotoUri, aspectX, aspectY, size);
                            cropPic(Uri.fromFile(new File(picturePath)));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        ViewHub.showShortToast(MainAcivity.this, "未选择图片");
                    }
                }
        }
    }

    /**
     * 展示图片
     */
    private void displayImage(String srcPath, Bitmap bitmap) {
        // 展示图片

        mLogoPath = srcPath;
//        Picasso.with(vThis).load(srcPath).placeholder(R.drawable.empty_photo).into(mIvShopLogo);
        mIvAvatar.setImageBitmap(bitmap);
        new SaveDataTask(mLogoPath).execute();
    }

    /**
     * 保存个性设置
     */
    private class SaveDataTask extends AsyncTask<Void, Void, String> {

        private String mLogo = "";

        public SaveDataTask(String logo) {
            mLogo = logo;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                // 店铺ID
                String shopId = String.valueOf(SpManager.getShopId(Vthis));
                // 保存店标设置
                if (!TextUtils.isEmpty(mLogo)) {
                    // 本地文件名
                    if (mLogo.startsWith("file://")) {
                        mLogo = mLogo.substring(7);// 去除file://
                    }
                    // 服务器文件名
                    String fileName = "shoplogo" + System.currentTimeMillis() + ".jpg";
                    // 上传店标
                    String serverPath = ShopSetAPI.getInstance().uploadImage(shopId, fileName, mLogo);
                    if (TextUtils.isEmpty(serverPath))
                        throw new Exception("更新店标失败，操作无法完成");

                    // 保存店铺资料
                    String serverPath_logo = serverPath;//"upyun:" + PublicData.UPYUN_BUCKET + ":" + serverPath;
                    String cookie = PublicData.getCookie(Vthis);
                    boolean success = ShopSetAPI.getInstance().updateLogo(serverPath_logo, cookie);

                    if (success) {
                        // 店标更新成功时，更新logo路径
                        SpManager.setShopLogo(Vthis, serverPath_logo);
                        return "OK";
                    } else {
                        throw new Exception("店标更新失败，操作无法完成");
                    }
                }
                return "OK";
            } catch (Exception ex) {
                Log.e(TAG, "无法更新店标");
                ex.printStackTrace();
                return ex.getMessage() == null ? "未知异常" : ex.getMessage();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog.start(getString(R.string.me_loading));
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            loadingDialog.stop();

            if (result.equals("OK")) {
                ViewHub.showShortToast(getApplicationContext(), "店标保存成功");

                // 通知myitem更新店标
                EventBus.getDefault().postSticky(BusEvent.getEvent(EventBusId.SHOP_LOGO_UPDATED));
                // Intent changeLogoIntent = new Intent();
                // changeLogoIntent.setAction(MyItemsActivity.MyItemsActivityChangeLogoBroadcaseName);
                // sendBroadcast(changeLogoIntent);
            } else {
                // 验证result
                if (result.toString().startsWith("401") || result.toString().startsWith("not_registered")) {
                    ViewHub.showShortToast(getApplicationContext(), result.toString());
                    ApiHelper.checkResult(result, Vthis);
                } else {
                    ViewHub.showShortToast(getApplicationContext(), result);
                }
            }
        }
    }

    private void cropPic(Uri uri) {
        final Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");//裁剪功能
        //裁剪後輸出圖片的尺寸大小
        intent.putExtra("outputX", 300);//這會限定圖片為
        intent.putExtra("outputY", 300);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("circleCrop", true);
        //切大照片,有可能因為超過傳回內存的容量16MB,會有問題,(Bitmap預設是ARGB_8888，1920x1080x4=8294400=8MB)
        //原因是因為Android允許你使用return-data拿資料回來,再用(Bitmap)extras.getParcelable("data")拿到圖片
        //檔案太大的解決辦法:不要讓Intent帶檔案回來,自創建檔案,使用uri方法去連結它
        intent.putExtra("return-data", true);//要帶檔案回來
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        this.startActivityForResult(intent, REQUESTCODE_CUT);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEventBus.unregister(this);
        if (conn != null) {
            unbindService(conn); // unBindService
        }

    }

    public void onEventMainThread(BusEvent event) {
        switch (event.id) {

            case EventBusId.SHOP_LOGO_UPDATED:// 店铺logo有修改
                String logo = SpManager.getShopLogo(Vthis);
                if (!TextUtils.isEmpty(logo)) {
                    String url = ImageUrlExtends.getImageUrl(logo, Const.LIST_HEADER_COVER_SIZE);
                    Picasso.with(Vthis).load(url).skipMemoryCache().placeholder(R.drawable.empty_photo)
                            .into(mIvAvatar);
                    initLogoBgView(url);
                }
                break;
            case EventBusId.ON_APP_EXIT:
                finish();
                break;
        }
    }

    private void initLogoBgView(String url) {
        if (TextUtils.isEmpty(url)) {
            ((ImageView) Vthis.findViewById(R.id.iv_logobg)).setImageResource(R.drawable.bg_main);
        } else {
            Picasso.with(BWApplication.getInstance())
                    .load(url)
                    .transform(new BlurTransform(80))
                    .placeholder(R.drawable.bg_main).into((ImageView) Vthis.findViewById(R.id.iv_logobg));
        }

    }

    private void initgrid() {
        List<MeItemModel> list = new ArrayList<>();
        if (!ListUtil.isEmpty(BtnList1)) {
            for (int i = 0; i < BtnList1.size(); i++) {
                BaseInfoList.BtnListBean btnListBean = BtnList1.get(i);
                MeItemModel model = new MeItemModel();
                if (i > numColumns - 1) {
                    model.setIs_Show(false);
                } else {
                    model.setIs_Show(true);
                }
                model.setText(btnListBean.getText());
                model.setValue(btnListBean.getValue());
                model.setType(btnListBean.getType());
                list.add(model);
            }
            int size1;
            if (BtnList1.size() < numColumns) {
                size1 = numColumns - BtnList1.size();
                if (size1 > 0) {
                    for (int i = 0; i < size1; i++) {
                        MeItemModel empty = new MeItemModel();
                        empty.setType(-100);
                        empty.setIs_Show(true);
                        list.add(empty);
                    }
                }
            } else {
                size1 = numColumns - BtnList1.size() % numColumns;
                if (BtnList1.size() % numColumns > 0) {
                    for (int i = 0; i < size1; i++) {
                        MeItemModel empty = new MeItemModel();
                        empty.setType(-100);
                        empty.setIs_Show(false);
                        list.add(empty);
                    }
                }
            }

        }
        if (!ListUtil.isEmpty(BtnList2)) {
            for (int i = 0; i < BtnList2.size(); i++) {
                BaseInfoList.BtnListBean btnListBean = BtnList2.get(i);
                MeItemModel model = new MeItemModel();
                if (i > numColumns - 1) {
                    model.setIs_Show(false);
                } else {
                    model.setIs_Show(true);
                }
                model.setText(btnListBean.getText());
                model.setValue(btnListBean.getValue());
                model.setType(btnListBean.getType());
                list.add(model);
            }
            int size1;
            if (BtnList2.size() < numColumns) {
                size1 = numColumns - BtnList2.size();
                if (size1 > 0) {
                    for (int i = 0; i < size1; i++) {
                        MeItemModel empty = new MeItemModel();
                        empty.setType(-100);
                        empty.setIs_Show(true);
                        list.add(empty);
                    }
                }
            } else {
                size1 = numColumns - BtnList2.size() % numColumns;
                if (BtnList2.size() % numColumns > 0) {
                    for (int i = 0; i < size1; i++) {
                        MeItemModel empty = new MeItemModel();
                        empty.setType(-100);
                        empty.setIs_Show(false);
                        list.add(empty);
                    }
                }
            }
        }
        if (!ListUtil.isEmpty(BtnList3)) {
            for (int i = 0; i < BtnList3.size(); i++) {
                BaseInfoList.BtnListBean btnListBean = BtnList3.get(i);
                MeItemModel model = new MeItemModel();
                if (i > numColumns - 1) {
                    model.setIs_Show(false);
                } else {
                    model.setIs_Show(true);
                }
                model.setText(btnListBean.getText());
                model.setValue(btnListBean.getValue());
                model.setType(btnListBean.getType());
                list.add(model);
            }
            int size1;
            if (BtnList3.size() < numColumns) {
                size1 = numColumns - BtnList3.size();
                if (size1 > 0) {
                    for (int i = 0; i < size1; i++) {
                        MeItemModel empty = new MeItemModel();
                        empty.setType(-100);
                        empty.setIs_Show(true);
                        list.add(empty);
                    }
                }
            } else {
                size1 = numColumns - BtnList3.size() % numColumns;
                if (BtnList3.size() % numColumns > 0) {
                    for (int i = 0; i < size1; i++) {
                        MeItemModel empty = new MeItemModel();
                        empty.setType(-100);
                        empty.setIs_Show(false);
                        list.add(empty);
                    }
                }
            }
        }
//        MeItemModel emptyModel = new MeItemModel();
//        emptyModel.setName("");
//        MeItemModel model1 = new MeItemModel();
//        model1.setName("待开单");
////        model1.setSourceId(R.drawable.wodedingdan);
//        MeItemModel model2 = new MeItemModel();
//        model2.setName("已开单");
//        MeItemModel model3 = new MeItemModel();
//        model3.setName("入仓单");
//        MeItemModel model4 = new MeItemModel();
//        model4.setName("欠货单");
//        MeItemModel model5 = new MeItemModel();
//        model5.setName("退款单");
//        MeItemModel model6 = new MeItemModel();
//        model6.setName("采购单");
//        MeItemModel model7 = new MeItemModel();
//        model7.setName("次品管理");
//        MeItemModel model8 = new MeItemModel();
//        model8.setName("我的款式");
//        MeItemModel model9 = new MeItemModel();
//        model9.setName("供应商");
//        MeItemModel model10 = new MeItemModel();
//        model10.setName("其他汇总");
//        MeItemModel model11 = new MeItemModel();
//        model11.setName("待开单汇总");
//        MeItemModel model12 = new MeItemModel();
//        model12.setName("已开单汇总");
//        MeItemModel model13 = new MeItemModel();
//        model13.setName("未分配汇总");
//        MeItemModel model14 = new MeItemModel();
//        model14.setName("已开单/欠货单");
//        MeItemModel model15 = new MeItemModel();
//        model15.setName("销售业绩");
//        MeItemModel model16 = new MeItemModel();
//        model16.setName("断货款");
//        MeItemModel model17 = new MeItemModel();
//        model17.setName("我的库存");
//        list.add(model11);
//        list.add(model12);
//        list.add(model10);
//        list.add(model13);
//        list.add(model6);
//        list.add(emptyModel);

//        list.add(model1);
//        list.add(model2);
//        list.add(model4);
//        list.add(model3);
//        list.add(model5);
//        list.add(model14);
//
//        list.add(model8);
//        list.add(model17);
//        list.add(model9);
//        list.add(model15);
//        list.add(model16);
//        list.add(emptyModel);
        adatper = new MeItemAdatper(Vthis, list);
        mGrdClass.setAdapter(adatper);
        adatper.setStyleListener(this);
        ListviewUtls.setGridViewHeight(mGrdClass, 3);
        adatper.notifyDataSetChanged();
    }

    @Override
    public void OnMeItemClick(MeItemModel item) {
        Intent intent = new Intent(Vthis, CommonListActivity.class);
        Intent intentPre = new Intent(Vthis, ItemPreviewActivity.class);
        switch (item.getType()) {
            case 1:
                if (item.getValue().equals("Live")) {
                    //直播
                    startActivity(new Intent(vThis, LiveListActivity.class));
//                    if (!TextUtils.isEmpty(SpManager.getIdentifier(vThis))) {
//                        startActivity(new Intent(vThis, TCPublishSettingActivity.class));
//                    } else {
//                        ViewHub.showShortToast(vThis, "直播账号为空");
//                    }
                    return;
                } else if (item.getValue().equals("WAIT")) {
                    //待开单
                    intent.putExtra("type", 1);//CommonListActivity.ListType.待开单;
                } else if (item.getValue().equals("BILLING")) {
                    intent.putExtra("type", 2);//CommonListActivity.ListType.已开单;
                } else if (item.getValue().equals("STORE")) {
                    intent.putExtra("type", 3);//CommonListActivity.ListType.入库单;
                } else if (item.getValue().equals("OWE")) {
                    intent.putExtra("type", 4);//CommonListActivity.ListType.欠货单;
                } else if (item.getValue().equals("REFUND")) {
                    intent.putExtra("type", 5);//CommonListActivity.ListType.退款单;
                } else if (item.getValue().equals("BILLINGOWE")) {
                    //已开单/欠货单
                    intent.putExtra("type", 6);//CommonListActivity.ListType.退款单;
                } else if (item.getValue().equals("Goods")) {
                    //我的款式
                    startActivity(new Intent(getApplicationContext(), MyStyleActivity.class));
                    return;
                } else if (item.getValue().equals("SUPPLIER")) {
                    //供应商
                    startActivity(new Intent(getApplicationContext(), SuppliersActivity.class));
                    return;
                } else if (item.getValue().equals("Exception")) {
                    intent.putExtra("type", 7);//CommonListActivity.ListType.異常单;
                }
                intent.putExtra("Text", item.getText());
                startActivity(intent);
                break;
            case 2:
                intentPre.putExtra("url", item.getValue());
                intentPre.putExtra("name", item.getText());
                startActivity(intentPre);
                break;
        }
//        if (item.getName().equals("待开单")) {
//            intent.putExtra("type", 1);//CommonListActivity.ListType.待开单;
//        } else if (item.getName().equals("已开单")) {
//            intent.putExtra("type", 2);//CommonListActivity.ListType.已开单;
//        } else if (item.getName().equals("入仓单")) {
//            intent.putExtra("type", 3);//CommonListActivity.ListType.入库单;
//        } else if (item.getName().equals("欠货单")) {
//            intent.putExtra("type", 4);//CommonListActivity.ListType.欠货单;
//        } else if (item.getName().equals("退款单")) {
//            intent.putExtra("type", 5);//CommonListActivity.ListType.退款单;
//        } else if (item.getName().equals("已开单/欠货单")) {
//            intent.putExtra("type", 6);//CommonListActivity.ListType.退款单;
//        } else if (item.getName().equals("采购单")) {
//            Log.e("确实经过这里1", "OnMeItemClick++ItemPreviewActivity");
//            Intent it = new Intent(getApplicationContext(), ItemPreviewActivity.class);
//            it.putExtra("url", "http://wap.admin.nahuo.com/Purchase/List");
//            it.putExtra("name", "采购单");
//            startActivity(it);
//            return;
//        } else if (item.getName().equals("次品管理")) {
//
//            Log.e("确实经过这里1", "OnMeItemClick++ItemPreviewActivity");
//            Intent it = new Intent(getApplicationContext(), ItemPreviewActivity.class);
//            it.putExtra("url", "http://wap.admin.nahuo.com/cs/list");
//            it.putExtra("name", "次品管理");
//            startActivity(it);
//            return;
//
//        } else if (item.getName().equals("我的款式")) {
//            startActivity(new Intent(getApplicationContext(), MyStyleActivity.class));
//
//            return;
//
//        } else if (item.getName().equals("供应商")) {
//            startActivity(new Intent(getApplicationContext(), SuppliersActivity.class));
//            return;
//        } else if (item.getName().equals("其他汇总")) {
//
//            intentPre.putExtra("url", "http://wap.admin.nahuo.com/buyertool/index");
//            intentPre.putExtra("name", "其他汇总");
//            startActivity(intentPre);
//            return;
//        } else if (item.getName().equals("待开单汇总")) {
//
//            intentPre.putExtra("url", "http://wap.admin.nahuo.com/buyertool/billlist?statu=1");
//            intentPre.putExtra("name", "待开单汇总");
//            startActivity(intentPre);
//            return;
//        } else if (item.getName().equals("已开单汇总")) {
//
//            intentPre.putExtra("url", "http://wap.admin.nahuo.com/buyertool/billlist?statu=3");
//            intentPre.putExtra("name", "已开单汇总");
//            startActivity(intentPre);
//            return;
//        } else if (item.getName().equals("未分配汇总")) {
//
//            intentPre.putExtra("url", "http://wap.admin.nahuo.com/buyertool/billlist?statu=7");
//            intentPre.putExtra("name", "未分配汇总");
//            startActivity(intentPre);
//            return;
//        } else if (item.getName().equals("销售业绩")) {
//
//            intentPre.putExtra("url", "http://wap.admin.nahuo.com/buyertool/billlist?statu=5");
//            intentPre.putExtra("name", "销售业绩");
//            startActivity(intentPre);
//            return;
//        } else if (item.getName().equals("断货款")) {
//
//            intentPre.putExtra("url", "http://wap.admin.nahuo.com/cs/list?statuid=4");
//            intentPre.putExtra("name", "断货款");
//            startActivity(intentPre);
//            return;
//        } else if (item.getName().equals("我的库存")) {
//
//            intentPre.putExtra("url", "http://wap.admin.nahuo.com/stock/index");
//            intentPre.putExtra("name", "我的库存");
//            startActivity(intentPre);
//            return;
//        } else {
//            return;
//        }
//        startActivity(intent);
    }

    /**
     * @description 店铺数据加载完毕
     * @created 2014-9-3 下午2:01:14
     * @author ZZB
     */
    private void shopInfoLoaded(Object result) {
        try {
            ShopInfoModel shopinfo = (ShopInfoModel) result;
            // 保存shopInfo值到SharedPreferences文件
            SpManager.setShopInfo(vThis, shopinfo);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @description 加载店铺数据
     * @created 2014-9-3 下午1:52:24
     * @author ZZB
     */
    private Object loadShopInfo() {
        try {
            ShopInfoModel shopInfo = ShopSetAPI.getInstance().getShopInfo(PublicData.getCookie(vThis));
            if (shopInfo != null) {
                return shopInfo;
            } else {
                return ERROR_PREFIX + "没有找到店铺信息";
            }
        } catch (Exception ex) {
            Log.e(TAG, "加载店铺信息发生异常");
            ex.printStackTrace();
            return ERROR_PREFIX + ex.getMessage();
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
                    loadingDialog.start("加载数据中...");
                    break;
                case LOAD_BASE_SET:
                    // loadingDialog.start("加载数据中...");
                    break;
                // case LOAD_HAS_NEWS:
                // break;
                case LOAD_MAIN_LIST_VIEW:
                    //loadingDialog.start("加载数据中...");
                    break;
            }
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                switch (mStep) {
                    case LOAD_LIST_FILTER:
                        getListFilter();
                        break;
                    case LOAD_USER_INFO:
                        return loadUserInfo();
                    case LOAD_BASE_SET:
                        return loadBaseSet();
                    case LOAD_MAIN_LIST_VIEW:
                        return loadTypeInfo();
                    case LOAD_SHOP_INFO:

                        return loadShopInfo();
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
                ViewHub.showLongToast(Vthis, msg);
                // 验证result
                if (msg.toString().startsWith("401") || msg.toString().startsWith("not_registered")) {
                    ApiHelper.checkResult(msg, Vthis);
                }
                return;
            }
            switch (mStep) {
                case LOAD_SHOP_INFO:
                    shopInfoLoaded(result);
                    break;
                case LOAD_USER_INFO:
                    userInfoLoaded(result);
                    break;
                case LOAD_BASE_SET:
                    baseSetLoaded(result);
                    break;
                case LOAD_MAIN_LIST_VIEW:
                    setListView(result);
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        // ViewHub.showExitDialog(this);
        if (GetControlBack()) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);
            System.exit(0);
        } else {
            ViewHub.showExitLightPopDialog(Vthis);
        }
    }

    /**
     * @description 加载基础数据
     */
    private Object loadBaseSet() {
        try {
            AccountAPI.getMyShopBaseConfig(PublicData.getCookie(Vthis), Vthis);
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
     * @description 商品类型加载完成
     */
    private void setListView(Object result) {
        if (result != null) {
            BaseInfoList baseInfoList = (BaseInfoList) result;
            if (!TextUtils.isEmpty(baseInfoList.getLoginErrorMsg())) {
                Log.v(TAG, "go start login");
                setControlBack(true);
                MainAcivity.this.setFinishOnTouchOutside(false);
                ViewHub.showOKLightPopDialogNoDismiss(this, getString(R.string.dialog_title_prompt),
                        baseInfoList.getLoginErrorMsg(), getString(R.string.dialog_msg_switch_account), false, new LightPopDialog.PopDialogListener() {
                            @Override
                            public void onPopDialogButtonClick(int which) {
                                Log.v(TAG, "go change account");
                                Intent changeIntent = new Intent(MainAcivity.this, LoginActivity.class);
                                startActivity(changeIntent);
                                finish();
                            }
                        });
            } else {
                allCommodity = new ArrayList<CommodityInfo>();
                allCommodity = baseInfoList.getWapList();
                BtnList1 = baseInfoList.getBtnList1();
                BtnList2 = baseInfoList.getBtnList2();
                BtnList3 = baseInfoList.getBtnList3();
                initgrid();
                adatperCommo = new CommodityAdatper(this, allCommodity);
                typeList.setAdapter(adatperCommo);
                Log.v(TAG, " size " + allCommodity.size());
                typeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        CommodityInfo typeInfo = allCommodity.get(position);
                        if (!TextUtils.isEmpty(typeInfo.getText())) {
                            Intent it = new Intent(getApplicationContext(), ItemPreviewActivity.class);
                            it.putExtra("url", typeInfo.getUrl());
                            it.putExtra("name", typeInfo.getText());
                            startActivity(it);
                        }
                    }
                });
            }

        }
    }

    /**
     * @description 读取完了是否有新广播帖子
     */
    // private void hasNewsLoaded(Object result) {
    // hasNews = Boolean.valueOf(result.toString());
    // }
    private Object getListFilter() {
        try {
            return CommodityAPI.getListFilter(vThis);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @description 加载用户数据
     * @created 2014-9-3 下午1:42:46
     * @author ZZB
     */
    private Object loadUserInfo() {
        try {
            UserModel userinfo = AccountAPI.getInstance().getUserInfo(PublicData.getCookie(Vthis));
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
     * 获取商品类型
     */
    private BaseInfoList loadTypeInfo() {
        try {
            BaseInfoList baseInfo = CommodityAPI.getInstance().getCommodityAPI(this);
            return baseInfo;
        } catch (Exception ex) {
            Log.e(TAG, "加载商品类型发生异常");
            ex.printStackTrace();
            return null;
        }
    }


    /**
     * @description 用户数据加载完成
     * @created 2014-9-3 下午1:45:19
     * @author ZZB
     */
    private void userInfoLoaded(Object result) {
        PublicData.mUserInfo = (UserModel) result;
        initView();
        showNoUplaodDialog();
    }

    //获取首页menu数据

}
