package com.nahuo.buyertool.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.BaseSlideBackActivity;
import com.nahuo.buyertool.Bean.SearchStallBean;
import com.nahuo.buyertool.Bean.StallsBean;
import com.nahuo.buyertool.Bean.WheelBean;
import com.nahuo.buyertool.ViewHub;
import com.nahuo.buyertool.api.ShopSetAPI;
import com.nahuo.buyertool.common.ListUtils;
import com.nahuo.buyertool.common.SpManager;
import com.nahuo.buyertool.dialog.WheelDialog;
import com.nahuo.library.controls.AutoCompleteTextViewEx;
import com.nahuo.library.controls.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择档口
 *
 * @author James Chen
 * @create time in 2017/7/19 16:25
 */
public class ChooseStallsActivity extends BaseSlideBackActivity implements View.OnClickListener, WheelDialog.PopDialogListener {
    TextView tv_search, tv_market, tv_floor, tv_stall;
    List<StallsBean> data;
    List<WheelBean> marketList = new ArrayList<>();
    List<WheelBean> floorList = new ArrayList<>();
    List<WheelBean> stallList = new ArrayList<>();
    WheelBean bean = null;
    int market_id = 0, foor_id = 0, stall_id = 0;
    private LoadingDialog mloadingDialog;
    private AutoCompleteTextViewEx mEtSearch;
    String keyword = "";
    SearchStallBean mSearchStallBean = null;
    public static final String EXTRA_STALLID = "Extra_StallID";
    public static final String EXTRA_MARKETID = "EXTRA_MARKETID";
    public static final String EXTRA_FLOORID = "EXTRA_FLOORID";
    public static final String EXTRA_STALL_ALL_NAME = "EXTRA_STALL_ALL_NAME";
    public String stall_all_name = "";
    String marketstr = "", foorstr = "", stallstr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);// 设置自定义标题栏
        setContentView(R.layout.activity_choose_stalls);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.layout_titlebar_default);// 更换自定义标题栏布局
        // 标题栏
        mloadingDialog = new LoadingDialog(this);
        TextView btnLeft = (Button) findViewById(R.id.titlebar_btnLeft);
        btnLeft.setOnClickListener(this);
        TextView btnRight = (Button) findViewById(R.id.titlebar_btnRight);
        btnRight.setOnClickListener(this);
        TextView tvTitle = (TextView) findViewById(R.id.titlebar_tvTitle);
        tvTitle.setText("选择档口");
        btnLeft.setText("");
        btnRight.setText(R.string.titlebar_btnComplete);
        btnLeft.setVisibility(View.VISIBLE);
        btnRight.setVisibility(View.VISIBLE);
        tv_search = (TextView) findViewById(R.id.tv_search);
        tv_market = (TextView) findViewById(R.id.tv_market);
        tv_floor = (TextView) findViewById(R.id.tv_floor);
        tv_stall = (TextView) findViewById(R.id.tv_stall);
        tv_search.setOnClickListener(this);
        tv_market.setOnClickListener(this);
        tv_floor.setOnClickListener(this);
        tv_stall.setOnClickListener(this);
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
        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {//actionSearch搜索，对应常量EditorInfo.IME_ACTION_SEARCH效果
                    ViewHub.hideKeyboard(ChooseStallsActivity.this);
                    keyword = mEtSearch.getText().toString();
                    if (TextUtils.isEmpty(keyword)) {
                        ViewHub.setEditError(mEtSearch, "请输入搜索内容");
                        return false;
                    }
                    gotoSearch();
                }
                return false;
            }
        });
        if (getIntent() != null) {
            market_id = getIntent().getIntExtra(EXTRA_MARKETID, 0);
            foor_id = getIntent().getIntExtra(EXTRA_FLOORID, 0);
            stall_id = getIntent().getIntExtra(EXTRA_STALLID, 0);
        }
        new Task().execute();
    }

    private void gotoSearch() {
        new SearchTask().execute();

    }

    private class SearchTask extends AsyncTask<Object, Void, Object> {

        public SearchTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mloadingDialog.start("搜索数据..");

        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                SearchStallBean mSearchStallBean = ShopSetAPI.getStallsByKey(ChooseStallsActivity.this, keyword, market_id, foor_id);
                return mSearchStallBean;

            } catch (Exception e) {
                e.printStackTrace();
                return "error:" + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            hideDialog();
            if (result instanceof String && ((String) result).startsWith("error:")) {
                ViewHub.showLongToast(getApplicationContext(), ((String) result).replace("error:", ""));
            } else {
                mSearchStallBean = (SearchStallBean) result;
                if (mSearchStallBean != null) {
                    if (mSearchStallBean.getMarketID() > 0) {
                        market_id = mSearchStallBean.getMarketID();
                        if (!TextUtils.isEmpty(mSearchStallBean.getMarketName())) {
                            tv_market.setText(mSearchStallBean.getMarketName());
                            marketstr=mSearchStallBean.getMarketName();
                        }
                    }
                    if (mSearchStallBean.getFloorID() > 0) {
                        foor_id = mSearchStallBean.getFloorID();
                        if (!TextUtils.isEmpty(mSearchStallBean.getFloorName())) {
                            tv_floor.setText(mSearchStallBean.getFloorName());
                            foorstr=mSearchStallBean.getFloorName();
                        }
                    }
                    if (mSearchStallBean.getStallsID() > 0) {
                        stall_id = mSearchStallBean.getStallsID();
                        if (!TextUtils.isEmpty(mSearchStallBean.getStallsName())) {
                            tv_stall.setText(mSearchStallBean.getStallsName());
                            stallstr=mSearchStallBean.getStallsName();
                        }
                    }
                }
            }
        }
    }

    private class Task extends AsyncTask<Object, Void, Object> {

        public Task() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mloadingDialog.start("加载档口数据..");

        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                List<StallsBean> list = ShopSetAPI.getAllStallsForSearch(ChooseStallsActivity.this);
                return list;

            } catch (Exception e) {
                e.printStackTrace();
                return "error:" + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            hideDialog();
            if (result instanceof String && ((String) result).startsWith("error:")) {
                ViewHub.showLongToast(getApplicationContext(), ((String) result).replace("error:", ""));
            } else {
                data = (List<StallsBean>) result;
                marketList.clear();
                if (data != null) {
                    for (StallsBean bean : data) {
                        WheelBean wheelBean = new WheelBean();
                        wheelBean.setName(bean.getName());
                        wheelBean.setID(bean.getID());
                        marketList.add(wheelBean);
                        if (market_id > 0 && market_id == bean.getID()) {
                            if (!TextUtils.isEmpty(bean.getName())) {
                                tv_market.setText(bean.getName());
                                marketstr=bean.getName();
                            }
                            if (!ListUtils.isEmpty(bean.getFloorList())) {
                                for (StallsBean.FloorListBean floorListBean : bean.getFloorList()) {
                                    if (foor_id > 0 && foor_id == floorListBean.getID()) {
                                        if (!TextUtils.isEmpty(floorListBean.getName())) {
                                            tv_floor.setText(floorListBean.getName());
                                            foorstr=floorListBean.getName();
                                        }
                                        if (!ListUtils.isEmpty(floorListBean.getStallsList())) {
                                            for (StallsBean.FloorListBean.StallsListBean stallsListBean : floorListBean.getStallsList()) {
                                                if (stall_id > 0 && stall_id == stallsListBean.getID()) {
                                                    if (!TextUtils.isEmpty(stallsListBean.getName())) {
                                                        tv_stall.setText(stallsListBean.getName());
                                                        stallstr=stallsListBean.getName();
                                                    }
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }
                } else {
                }
            }
        }
    }

    protected boolean isDialogShowing() {
        return (mloadingDialog != null && mloadingDialog.isShowing());
    }

    protected void hideDialog() {
        if (isDialogShowing()) {
            mloadingDialog.stop();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlebar_btnLeft:
                finish();
                break;
            case R.id.titlebar_btnRight:
                if (stall_id > 0)
                    stall_all_name = marketstr + "-" + foorstr + "-" + stallstr;
                Bundle bundle = new Bundle();
                bundle.putInt(EXTRA_STALLID, stall_id);
                bundle.putString(EXTRA_STALL_ALL_NAME, stall_all_name);
                bundle.putInt(EXTRA_MARKETID,market_id);
                bundle.putInt(EXTRA_FLOORID,foor_id);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(UploadItemActivity.RESULTCODE_OK, intent);
                finish();
                break;
            case R.id.tv_search:
                ViewHub.hideKeyboard(ChooseStallsActivity.this);
                keyword = mEtSearch.getText().toString();
                if (TextUtils.isEmpty(keyword)) {
                    ViewHub.setEditError(mEtSearch, "请输入搜索内容");
                    return;
                }
                gotoSearch();
                break;
            case R.id.tv_market:
                WheelDialog.getInstance(this).setType(1).setList(marketList).setPositive(this).showDialog();
                break;
            case R.id.tv_floor:
                if (market_id <= 0) {
                    ViewHub.showShortToast(ChooseStallsActivity.this, "请先选择市场");
                    return;
                }
                floorList.clear();
                if (!ListUtils.isEmpty(data)) {
                    for (StallsBean bean : data) {
                        if (market_id == bean.getID()) {
                            if (bean != null) {
                                if (!ListUtils.isEmpty(bean.getFloorList())) {

                                    for (StallsBean.FloorListBean fbean : bean.getFloorList()) {
                                        WheelBean wheelBean = new WheelBean();
                                        wheelBean.setName(fbean.getName());
                                        wheelBean.setID(fbean.getID());
                                        floorList.add(wheelBean);
                                    }
                                }
                            }
                            break;
                        }

                    }
                }
                WheelDialog.getInstance(this).setType(2).setList(floorList).setPositive(this).showDialog();
                break;
            case R.id.tv_stall:
                if (foor_id <= 0) {
                    ViewHub.showShortToast(ChooseStallsActivity.this, "请先选择楼层");
                    return;
                }
                stallList.clear();
                if (!ListUtils.isEmpty(data)) {
                    for (StallsBean bean : data) {
                        if (market_id == bean.getID()) {
                            if (bean != null) {
                                if (!ListUtils.isEmpty(bean.getFloorList())) {
                                    for (StallsBean.FloorListBean fbean : bean.getFloorList()) {
                                        if (fbean.getID() == foor_id) {
                                            if (!ListUtils.isEmpty(fbean.getStallsList())) {

                                                for (StallsBean.FloorListBean.StallsListBean sBean : fbean.getStallsList()) {
                                                    WheelBean wheelBean = new WheelBean();
                                                    wheelBean.setName(sBean.getName());
                                                    wheelBean.setID(sBean.getID());
                                                    stallList.add(wheelBean);
                                                }
                                            }
                                            break;
                                        }

                                    }
                                }
                            }
                            break;
                        }

                    }
                }
                WheelDialog.getInstance(this).setType(3).setList(stallList).setPositive(this).showDialog();
                break;


        }
    }


    @Override
    public void onCopyDialogButtonClick(WheelBean xbean, int type) {
        bean = xbean;
        switch (type) {
            case 1:
                if (bean != null) {
                    market_id = bean.getID();
                    marketstr = bean.getName();
                    if (TextUtils.isEmpty(marketstr)) {
                        tv_market.setText("市场");
                    } else {
                        tv_market.setText(marketstr);
                        tv_floor.setText("楼层");
                        tv_stall.setText("档口");
                        foor_id = 0;
                        stall_id = 0;
                    }
                }


                break;
            case 2:
                if (bean != null) {
                    foor_id = bean.getID();
                    foorstr = bean.getName();
                    if (TextUtils.isEmpty(foorstr)) {
                        tv_floor.setText("楼层");
                    } else {
                        tv_floor.setText(foorstr);
                    }
                }

                break;
            case 3:
                if (bean != null) {
                    stall_id = bean.getID();
                    stallstr = bean.getName();
                    if (TextUtils.isEmpty(stallstr)) {
                        tv_stall.setText("档口");
                    } else {
                        tv_stall.setText(stallstr);
                    }
                }

                break;
        }
    }
}
