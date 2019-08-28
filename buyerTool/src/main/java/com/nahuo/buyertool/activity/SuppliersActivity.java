package com.nahuo.buyertool.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.adapter.VendorListAdapter;
import com.nahuo.buyertool.api.ApiHelper;
import com.nahuo.buyertool.api.ReceiveAccountAPI;
import com.nahuo.buyertool.api.VendorAPI;
import com.nahuo.buyertool.event.OnVendorMenuClickListener;
import com.nahuo.buyertool.eventbus.BusEvent;
import com.nahuo.buyertool.eventbus.EventBusId;
import com.nahuo.buyertool.model.PublicData;
import com.nahuo.buyertool.model.ReceiveAccountModel;
import com.nahuo.buyertool.model.VendorListModel;
import com.nahuo.library.controls.AutoCompleteTextViewEx;
import com.nahuo.library.controls.LoadingDialog;
import com.nahuo.library.controls.pulltorefresh.PullToRefreshListView;
import com.nahuo.library.controls.pulltorefresh.PullToRefreshListViewEx;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 供应商
 *
 * @author James Chen
 * @create time in 2017/7/4 11:18
 */
public class SuppliersActivity extends FragmentActivity implements PullToRefreshListView.OnRefreshListener, PullToRefreshListView.OnLoadMoreListener,
        View.OnClickListener {
    private static final String TAG = "vendorsActivity";
    private Context mContext;
    private PullToRefreshListViewEx pullRefreshListView;
    private ReadReceiveAccountTask readReceiveAccountTask;
    private LoadDataTask loadDataTask;
    private LoadingDialog mloadingDialog;
    private TextView tvEmptyMessage;
    private VendorListAdapter adapter;
    private List<VendorListModel> itemList = null;
    private int mPageIndex = 1;
    private int mPageSize = 20;
    private EventBus mEventBus = EventBus.getDefault();
    private AutoCompleteTextViewEx mEtSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suppliers);
        initView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mEventBus.unregister(this);
    }

    private void initView() {
        mEventBus.registerSticky(this);
        mContext=this;
        Button backBtn = (Button) findViewById(R.id.titlebar_btnLeft);
        ((TextView) findViewById(R.id.tv_title)).setText("供应商");
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(this);
        ImageView mIvSearch = (ImageView) findViewById(R.id.titlebar_icon_loading);
        mIvSearch.setImageResource(R.drawable.find);
        mIvSearch.setOnClickListener(this);
        mIvSearch.setVisibility(View.VISIBLE);
        View emptyView = LayoutInflater.from(mContext).inflate(R.layout.layout_vendors_empty, null);
        emptyView.findViewById(R.id.btn_have_a_try).setOnClickListener(this);
        itemList = new ArrayList<VendorListModel>();
        mloadingDialog = new LoadingDialog(mContext);
        pullRefreshListView = (PullToRefreshListViewEx)findViewById(R.id.vendors_pull_refresh_listview_items);
       // pullRefreshListView.setEmptyView(emptyView);
        pullRefreshListView.setCanLoadMore(true);
        pullRefreshListView.setCanRefresh(true);
        pullRefreshListView.setMoveToFirstItemAfterRefresh(true);
        pullRefreshListView.setOnRefreshListener(this);
        pullRefreshListView.setOnLoadListener(this);
        initItemAdapter();
        loadData();

    }

    public void onEventMainThread(BusEvent event){
        switch (event.id) {
            case EventBusId.FOLLOW_VENDORS_CHANGED://关注供货商改变，这里刷新
            case EventBusId.AGENT_VENDOR_CHANGED://代理供货商变化
                pullRefreshListView.pull2RefreshManually();
                break;
        }
    }
    /**
     * 获取商品分类数据
     * */
    private void loadData() {
        pullRefreshListView.pull2RefreshManually();
    }

    // 初始化数据
    private void initItemAdapter() {
        if (itemList == null)
            itemList = new ArrayList<VendorListModel>();

        adapter = new VendorListAdapter(mContext, itemList, pullRefreshListView);
        adapter.setOnVendorMenuClickListener(new OnVendorMenuClickListener() {

            @Override
            public void onUnJoinClick(View v, int position) {
                VendorListModel item = itemList.get(position);

                // 执行取消关注
                UnjoinTask unJoinTask = new UnjoinTask(item.getUserID());
                unJoinTask.execute((Void)null);
            }

            @Override
            public void onReceiveAccountClick(View v, int position) {
                VendorListModel item = itemList.get(position);

                readReceiveAccountTask = new ReadReceiveAccountTask(item.getUserID(), item.getUserName());
                readReceiveAccountTask.execute((Void)null);
            }

            @Override
            public void onItemClick(View v, int position) {
                VendorListModel item = itemList.get(position);
                Intent detail = new Intent(mContext, ShopItemsActivity.class);
                detail.putExtra("Userid", item.getUserID() +"");
                detail.putExtra("Username", item.getUserName()+"");
                mContext.startActivity(detail);
//                Intent userInfoIntent = new Intent(mContext, UserInfoActivity.class);
//                userInfoIntent.putExtra(UserInfoActivity.EXTRA_USER_ID, item.getUserID());
//                startActivity(userInfoIntent);
                // 查看供货商下的所有款
                // Intent detail = new Intent(vThis, ShopItemsActivity.class);
                // detail.putExtra("Userid", String.valueOf(item.getUserID()));
                // detail.putExtra("Username", item.getUserName());
                // vThis.startActivity(detail);
            }

            @Override
            public void onChangeRateClick(View v, int position, float newPriceRate) {
                VendorListModel item = itemList.get(position);

                // 修改加价率
                ChangeRateTask changeRateTask = new ChangeRateTask(item.getUserID(), newPriceRate);
                changeRateTask.execute((Void)null);
            }
        });
        pullRefreshListView.setAdapter(adapter);

    }

    // 读取指定店铺收款信息
    private class ReadReceiveAccountTask extends AsyncTask<Void, Void, String> {

        private int                 UserID;
        private String              UserName;
        private ReceiveAccountModel userReceiveAccount;

        public ReadReceiveAccountTask(int userID, String name) {
            UserID = userID;
            UserName = name;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.equals("OK")) {
                Intent receiveAccount = new Intent(mContext, ReceiveAccountActivity.class);
                receiveAccount.putExtra("userID", UserID);
                receiveAccount.putExtra("userName", UserName);

                Bundle vendorBundle = new Bundle();
                vendorBundle.putSerializable("data", (Serializable)userReceiveAccount);
                receiveAccount.putExtras(vendorBundle);
                mContext.startActivity(receiveAccount);
            } else {
                Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
            }

            mloadingDialog.stop();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mloadingDialog.start(getString(R.string.items_loadData_loading));
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String cookie = PublicData.getCookie(mContext);

                userReceiveAccount = ReceiveAccountAPI.getInstance().getReceiveAccount(String.valueOf(UserID), cookie);

                return "OK";
            } catch (Exception ex) {
                ex.printStackTrace();
                if (ex.getMessage().contains("未设置")) {
                    return "供货商未设置收款信息";
                } else {
                    return ex.getMessage();
                }
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_have_a_try://体验一下
                Intent shopSearchIntent = new Intent(mContext, ShopSearchActivity.class);
                shopSearchIntent.putExtra("showDefaultShop", true);
                startActivity(shopSearchIntent);
                break;
            case R.id.titlebar_btnLeft:
                finish();
                break;
            case R.id.titlebar_icon_loading:
                Intent searchIntent = new Intent(this, SearchVendorsActivity.class);
                startActivity(searchIntent);
                break;
        }
    }


    @Override
    public void onRefresh() {
        bindItemData(true);

    }

    public class LoadDataTask extends AsyncTask<Void, Void, Object> {
        private boolean mIsRefresh = false;

        public LoadDataTask(boolean isRefresh) {
            mIsRefresh = isRefresh;
            if(isRefresh) {
                pullRefreshListView.setCanLoadMore(true);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            mloadingDialog.start(getString(R.string.items_loadData_loading));
        }

        @Override
        protected void onPostExecute(Object result) {
            mloadingDialog.stop();
            pullRefreshListView.onRefreshComplete();
            pullRefreshListView.onLoadMoreComplete();
            if(result instanceof List) {
                @SuppressWarnings("unchecked")
                List<VendorListModel> data = (List<VendorListModel>)result;
                if (mIsRefresh) {
                    itemList = data;
                } else {
                    if (data.size() < mPageSize) {
                        pullRefreshListView.setCanLoadMore(false);
                    }
                    itemList.addAll(data);
                }
                adapter.mList = itemList;
                adapter.notifyDataSetChanged();
            }else if(result instanceof String){
                // 验证result
                String error = result.toString();
                if (error.startsWith("401") || error.startsWith("not_registered")) {
                    Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();
                    ApiHelper.checkResult(error, SuppliersActivity.this);

                } else {
                    Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected Object doInBackground(Void... params) {

            try {
                List<VendorListModel> result = VendorAPI.getInstance().getMySuppliers(mPageIndex, mPageSize,
                        PublicData.getCookie(mContext));


                return result;
            } catch (Exception ex) {
                Log.e(TAG, "获取供货商列表发生异常");
                ex.printStackTrace();
                return ex.getMessage() == null ? "未知异常" : ex.getMessage();
            }
        }

    }

    /**
     * /** 绑定款式列表
     * */
    private void bindItemData(boolean isRefresh) {
        if (isRefresh) {
            mPageIndex = 1;
            loadDataTask = new LoadDataTask(isRefresh);
            loadDataTask.execute((Void)null);
        } else {

            mPageIndex++;
            loadDataTask = new LoadDataTask(isRefresh);
            loadDataTask.execute((Void)null);
        }

    }


    @Override
    public void onLoadMore() {
        bindItemData(false);
    }

    // 取消代理的task
    private class UnjoinTask extends AsyncTask<Void, Void, Boolean> {

        private int unJoinUserID;

        public UnjoinTask(int userID) {
            unJoinUserID = userID;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            mloadingDialog.stop();
            loadDataTask = new LoadDataTask(true);
            loadDataTask.execute((Void)null);
            if (result) {// 取消代理后刷新上新页
                EventBus.getDefault().postSticky(BusEvent.getEvent(EventBusId.AGENT_VENDOR_CHANGED));
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mloadingDialog.start(getString(R.string.items_loadData_loading));
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String cookie = PublicData.getCookie(mContext);

                Boolean result = VendorAPI.getInstance().unJoin(unJoinUserID, cookie);

                return result;
            } catch (Exception ex) {
                Log.e(TAG, "取消关注发生异常");
                ex.printStackTrace();
                return false;
            }
        }

    }

    // 更改加价率
    private class ChangeRateTask extends AsyncTask<Void, Void, Boolean> {

        private int   unJoinUserID;
        private float rate;

        public ChangeRateTask(int userID, float newRate) {
            unJoinUserID = userID;
            rate = newRate;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            mloadingDialog.stop();
            loadDataTask = new LoadDataTask(true);
            loadDataTask.execute((Void)null);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mloadingDialog.start(getString(R.string.items_loadData_loading));
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String cookie = PublicData.getCookie(mContext);

                Boolean result = VendorAPI.getInstance().setPriceRate(unJoinUserID, rate, cookie);

                return result;
            } catch (Exception ex) {
                Log.e(TAG, "修改加价率发生异常");
                ex.printStackTrace();
                return false;
            }
        }

    }

    public void OnRithtButtonMoreClick(View view, MotionEvent event) {

    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.onResume(this);
    }
}
