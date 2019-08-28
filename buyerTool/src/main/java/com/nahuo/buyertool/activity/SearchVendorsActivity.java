package com.nahuo.buyertool.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.BaseSlideBackActivity;
import com.nahuo.buyertool.Bean.VendorsBean;
import com.nahuo.buyertool.ViewHub;
import com.nahuo.buyertool.adapter.VendorListAdapterx;
import com.nahuo.buyertool.api.AgentAPI;
import com.nahuo.buyertool.api.HttpRequestHelper;
import com.nahuo.buyertool.api.HttpRequestListener;
import com.nahuo.buyertool.api.ReceiveAccountAPI;
import com.nahuo.buyertool.api.RequestMethod;
import com.nahuo.buyertool.api.VendorAPI;
import com.nahuo.buyertool.common.ListUtils;
import com.nahuo.buyertool.common.SpManager;
import com.nahuo.buyertool.event.OnVendorMenuClickListener;
import com.nahuo.buyertool.model.PublicData;
import com.nahuo.buyertool.model.ReceiveAccountModel;
import com.nahuo.library.controls.AutoCompleteTextViewEx;
import com.nahuo.library.controls.LoadingDialog;
import com.nahuo.library.controls.pulltorefresh.PullToRefreshListView.OnLoadMoreListener;
import com.nahuo.library.controls.pulltorefresh.PullToRefreshListView.OnRefreshListener;
import com.nahuo.library.controls.pulltorefresh.PullToRefreshListViewEx;

import java.io.Serializable;
import java.util.List;

/**
 * @author ZZB
 * @description 搜索供货商
 * @created 2015-4-22 下午7:35:39
 */
public class SearchVendorsActivity extends BaseSlideBackActivity implements HttpRequestListener, OnLoadMoreListener,
        OnRefreshListener, OnClickListener {
    private Context mContext = this;
    private AutoCompleteTextViewEx mEtSearch;
    private PullToRefreshListViewEx mListView;
    private boolean mIsTasking;
    private static int PAGE_INDEX = 0;//分页起始下标
    private static final int PAGE_SIZE = 10;//分页大小
    private VendorListAdapterx mAdapter;               //供货商本人adapter
    private boolean mIsRefresh;
    private HttpRequestHelper mHttpRequestHelper = new HttpRequestHelper();
    private LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //不要标题栏
        setContentView(R.layout.activity_search_my_vendors);
        initView();

    }

    private void initView() {
        mLoadingDialog = new LoadingDialog(this);
        mEtSearch = (AutoCompleteTextViewEx) findViewById(R.id.et_search);
        mEtSearch.setOnSearchLogDeleteListener(new AutoCompleteTextViewEx.OnSearchLogDeleteListener() {
            @Override
            public void onSearchLogDeleted(String text) {
                String newChar = SpManager.deleteSearchVendorsHistories(getApplicationContext(), text);
                mEtSearch.populateData(newChar, ",");
                mEtSearch.getFilter().filter(mEtSearch.getText());
            }
        });

        mEtSearch.populateData(SpManager.getSearchVendorsHistories(getApplicationContext()), ",");
        findViewById(R.id.tv_search).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewHub.hideKeyboard(SearchVendorsActivity.this);
                String keyword = mEtSearch.getText().toString();
                if (mIsTasking) {
                    return;
                } else if (TextUtils.isEmpty(keyword)) {
                    ViewHub.setEditError(mEtSearch, "请输入搜索内容");
                    return;
                }
                mListView.pull2RefreshManually();
            }
        });
        mEtSearch.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {//actionSearch搜索，对应常量EditorInfo.IME_ACTION_SEARCH效果
                    ViewHub.hideKeyboard(SearchVendorsActivity.this);
                    String keyword = mEtSearch.getText().toString();
                    if (mIsTasking) {
                        return false;
                    } else if (TextUtils.isEmpty(keyword)) {
                        ViewHub.setEditError(mEtSearch, "请输入搜索内容");
                        return false;
                    }
                    mListView.pull2RefreshManually();
                }
                return false;
            }
        });
        mListView = (PullToRefreshListViewEx) findViewById(R.id.lv_search_result);
        mAdapter = new VendorListAdapterx(mContext);
        mAdapter.setOnVendorMenuClickListener(new OnVendorMenuClickListener() {

            @Override
            public void onUnJoinClick(View v, int position) {
//                VendorListModel item = (VendorListModel) mAdapter.getItem(position);
//                new UnjoinTask(item.getUserID()).execute();
                try {
                    VendorsBean.DatasBean datasBean = (VendorsBean.DatasBean) mAdapter.getItem(position);
                    new Task(datasBean.getUserID()).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onReceiveAccountClick(View v, int position) {
//                VendorListModel item = (VendorListModel) mAdapter.getItem(position);
//                new ReadReceiveAccountTask(item.getUserID(), item.getUserName()).execute();
            }

            @Override
            public void onItemClick(View v, int position) {
                try {
                    VendorsBean.DatasBean item = (VendorsBean.DatasBean) mAdapter.getItem(position);
                    Intent detail = new Intent(mContext, ShopItemsActivity.class);
                    detail.putExtra("Userid", item.getUserID() + "");
                    detail.putExtra("Username", item.getUserName() + "");
                    mContext.startActivity(detail);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                Intent userInfoIntent = new Intent(mContext, UserInfoActivity.class);
//                userInfoIntent.putExtra(UserInfoActivity.EXTRA_USER_ID, item.getUserID());
//                startActivity(userInfoIntent);
            }

            @Override
            public void onChangeRateClick(View v, int position, float newPriceRate) {
//                VendorListModel item = (VendorListModel) mAdapter.getItem(position);
//                // 修改加价率
//                ChangeRateTask changeRateTask = new ChangeRateTask(item.getUserID(), newPriceRate);
//                changeRateTask.execute((Void) null);
            }
        });
        mListView.setCanLoadMore(true);
        mListView.setCanRefresh(true);
        mListView.setMoveToFirstItemAfterRefresh(true);
        mListView.setOnRefreshListener(this);
        mListView.setOnLoadListener(this);
        mListView.setEmptyViewText("没有搜索结果哦");
        mListView.setAdapter(mAdapter);

    }

    private void loadData(boolean isRefresh) {
        String keyword = mEtSearch.getText().toString();
        if (mIsTasking) {
            return;
        } else if (TextUtils.isEmpty(keyword)) {
            ViewHub.setEditError(mEtSearch, "请输入搜索内容");
            return;
        }
        mIsTasking = true;
        mIsRefresh = isRefresh;
        PAGE_INDEX = isRefresh ? 1 : ++PAGE_INDEX;

        if (isRefresh) {
            mListView.setCanLoadMore(true);
            mAdapter.clear();
            mAdapter.notifyDataSetChanged();
        }

        mHttpRequestHelper.getRequest(mContext, RequestMethod.SearchMethod.GET_SHOP_INFO_LIST, this)
                .addParam("PageIndex", String.valueOf(PAGE_INDEX)).addParam("PAGE_SIZE", String.valueOf(PAGE_SIZE))
                .addParam("userNameKeyword", keyword).setConvert2Class(VendorsBean.class).doPost();
        if (!TextUtils.isEmpty(keyword)) {
            SpManager.addSearchVendorsHistories(getApplicationContext(), keyword);
            mEtSearch.populateData(SpManager.getSearchVendorsHistories(getApplicationContext()), ",");
        }
    }

    private void onDataLoaded(Object obj) {
        loadFinished();
        VendorsBean pageResult = (VendorsBean) obj;
        List<VendorsBean.DatasBean> data = pageResult.getDatas();
        if (mIsRefresh) {
            mAdapter.setData(data);
        } else {
            mListView.setCanLoadMore(!ListUtils.isEmpty(data));
            mAdapter.addDataToTail(data);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void loadFinished() {
        mIsTasking = false;
        mListView.onRefreshComplete();
        mListView.onLoadMoreComplete();
    }

    @Override
    public void onRequestStart(String method) {

    }

    @Override
    public void onRequestSuccess(String method, Object object) {
        if (RequestMethod.SearchMethod.GET_SHOP_INFO_LIST.equals(method)) {
            onDataLoaded(object);
        }
    }

    @Override
    public void onRequestFail(String method, int statusCode, String msg) {
        if (RequestMethod.SearchMethod.GET_SHOP_INFO_LIST.equals(method)) {
            loadFinished();
        }
    }

    @Override
    public void onRequestExp(String method, String msg) {
        if (RequestMethod.SearchMethod.GET_SHOP_INFO_LIST.equals(method)) {
            loadFinished();
        }
    }

    @Override
    public void onRefresh() {
        loadData(true);
    }

    @Override
    public void onLoadMore() {
        loadData(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;

        }
    }

    private class Task extends AsyncTask<Object, Void, Object> {
        int UserID;

        Task(int UserID) {
            this.UserID = UserID;
        }

        @Override
        protected void onPreExecute() {
            mLoadingDialog.start("申请代理中...");
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                AgentAPI.applyAgent(mContext, UserID, "");

            } catch (Exception e) {
                e.printStackTrace();
                return "error:" + e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (mLoadingDialog.isShowing()) {
                mLoadingDialog.stop();
            }
            if (result instanceof String && ((String) result).startsWith("error:")) {
                ViewHub.showLongToast(mContext, ((String) result).replace("error:", ""));
            } else {
                ViewHub.showOkDialog(mContext, "提示", "申请代理发送成功", "OK");

            }
        }

    }

    // 更改加价率
    private class ChangeRateTask extends AsyncTask<Void, Void, Boolean> {
        private int unJoinUserID;
        private float rate;

        public ChangeRateTask(int userID, float newRate) {
            unJoinUserID = userID;
            rate = newRate;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            mLoadingDialog.stop();
            loadData(true);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingDialog.start(getString(R.string.items_loadData_loading));
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String cookie = PublicData.getCookie(mContext);
                Boolean result = VendorAPI.getInstance().setPriceRate(unJoinUserID, rate, cookie);
                return result;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    //收款信息
    private class ReadReceiveAccountTask extends AsyncTask<Void, Void, String> {
        private int UserID;
        private String UserName;
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
                vendorBundle.putSerializable("data", (Serializable) userReceiveAccount);
                receiveAccount.putExtras(vendorBundle);
                mContext.startActivity(receiveAccount);
            } else {
                Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
            }
            mLoadingDialog.stop();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingDialog.start(getString(R.string.items_loadData_loading));
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

    //取消代理
    private class UnjoinTask extends AsyncTask<Void, Void, Boolean> {
        private int unJoinUserID;

        public UnjoinTask(int userID) {
            unJoinUserID = userID;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            mLoadingDialog.stop();
            loadData(true);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingDialog.start(getString(R.string.items_loadData_loading));
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String cookie = PublicData.getCookie(mContext);
                Boolean result = VendorAPI.getInstance().unJoin(unJoinUserID, cookie);
                return result;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }
}
