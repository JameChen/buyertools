package com.nahuo.buyertool.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.BaseSlideBackActivity;
import com.nahuo.buyertool.ViewHub;
import com.nahuo.buyertool.adapter.SelectItemShopCatAdapter;
import com.nahuo.buyertool.api.ShopSetAPI;
import com.nahuo.buyertool.common.ListUtils;
import com.nahuo.buyertool.common.SpManager;
import com.nahuo.buyertool.model.ItemShopCategory;
import com.nahuo.buyertool.utils.ShopCategoryCacheManager;
import com.nahuo.buyertool.utils.SortHelper;
import com.nahuo.library.controls.LoadingDialog;
import com.nahuo.library.controls.pulltorefresh.PullToRefreshListView.OnRefreshListener;
import com.nahuo.library.controls.pulltorefresh.PullToRefreshListViewEx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ZZB
 * @description 选择商品店铺分类
 * @created 2015-3-12 下午2:53:47
 */
public class SelectItemShopCategoryActivity extends BaseSlideBackActivity implements View.OnClickListener, OnRefreshListener {

    /**
     * 已选择分类
     */
    public static final String EXTRA_SELECTED_CATS = "EXTRA_SELECTED_CATS";
    /**
     * 物价商品
     */
    public static final String EXTRA_IS_ON_SALE = "EXTRA_IS_ON_SALE";
    /**
     * 置顶商品
     */
    public static final String EXTRA_IS_TOP = "EXTRA_IS_TOP";
    public ArrayList<ItemShopCategory> mSelectedCategories;
    private Context mContext = this;
    private LoadingDialog mLoadingDialog;
    private PullToRefreshListViewEx mListView;
    private SelectItemShopCatAdapter mAdapter;
    private boolean mIsLoading = false, mIsOnSale, mIsTop;
    private CheckBox mCbOnSale, mCbTop;

    private static enum Step {
        LOAD_DATA, CREATE_CATEGORY
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_select_item_shop_catetory);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.layout_titlebar_default);
        initExtras();
        initView();
    }

    private void initExtras() {
        Intent intent = getIntent();
        mSelectedCategories = (ArrayList<ItemShopCategory>) intent.getSerializableExtra(EXTRA_SELECTED_CATS);
        mIsOnSale = intent.getBooleanExtra(EXTRA_IS_ON_SALE, false);
        mIsTop = intent.getBooleanExtra(EXTRA_IS_TOP, false);
    }

    @Override
    public void onBackPressed() {
        ArrayList<ItemShopCategory> selectedCats = mAdapter.getSelectedCats();
        Intent data = new Intent();
        data.putExtra(EXTRA_SELECTED_CATS, selectedCats);
        data.putExtra(EXTRA_IS_ON_SALE, mCbOnSale.isChecked());
        data.putExtra(EXTRA_IS_TOP, mCbTop.isChecked());
        setResult(RESULT_OK, data);
        super.onBackPressed();
    }

    private void initView() {
        initTitleBar();
        mLoadingDialog = new LoadingDialog(this);
        mCbOnSale = (CheckBox) findViewById(R.id.cb_on_sale);
        mCbTop = (CheckBox) findViewById(R.id.cb_top);
        mCbOnSale.setChecked(mIsOnSale);
        mCbTop.setChecked(mIsTop);
        initListView();
    }

    private void initListView() {
        mListView = (PullToRefreshListViewEx) findViewById(R.id.lv_shop_cat);
        mListView.setCanRefresh(true);
        mListView.setMoveToFirstItemAfterRefresh(true);
        mListView.setOnRefreshListener(this);
        mListView.setCanLoadMore(false);
        mAdapter = new SelectItemShopCatAdapter(mContext);
        if (ListUtils.isEmpty(mSelectedCategories)) {
            mAdapter.setDefaultChecked(true);
        }else {
            if (mSelectedCategories.get(0).getId()==0){
                mAdapter.setDefaultChecked(true);
            }
        }
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemShopCategory item = (ItemShopCategory) mAdapter.getItem(position - 1);
                // item.setCheck(!item.isCheck());
                mAdapter.setSingleCheck(item);
                mAdapter.setDefaultChecked(position == 1 ? true : false);
                mAdapter.notifyDataSetChanged();
            }

        });
        loadData();
    }

    private void loadData() {
        String json = SpManager.getItemShopCategory(mContext);
        mListView.pull2RefreshManually();
//        if (TextUtils.isEmpty(json)) {
//            mListView.pull2RefreshManually();
//        } else {
//            List<ItemShopCategory> data = GsonHelper
//                    .jsonToObject(json, new TypeToken<ArrayList<ItemShopCategory>>() {});
//            mAdapter.setData(data);
//            mAdapter.setSelectedCats(mSelectedCategories);
//            mAdapter.notifyDataSetChanged();
//        }
    }

    private void initTitleBar() {
        TextView tvTitle = (TextView) findViewById(R.id.titlebar_tvTitle);
        tvTitle.setText("商品分类");

        Button btnLeft = (Button) findViewById(R.id.titlebar_btnLeft);
        btnLeft.setText(R.string.titlebar_btnBack);
        btnLeft.setVisibility(View.VISIBLE);
        btnLeft.setOnClickListener(this);

        Button btnRight = (Button) findViewById(R.id.titlebar_btnRight);
        btnRight.setVisibility(View.VISIBLE);
        btnRight.setOnClickListener(this);
        btnRight.setText("新建分类");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlebar_btnRight:// 新建分类
                createNewGroup();
                break;
            case R.id.titlebar_btnLeft:
                onBackPressed();
                break;
        }
    }

    /**
     * @description 新建分类
     * @created 2015-3-12 下午5:58:05
     * @author ZZB
     */
    private void createNewGroup() {
        ViewHub.showEditDialog(mContext, "新建分类", "", new ViewHub.EditDialogListener() {
            @Override
            public void onOkClick(DialogInterface dialog, EditText editText) {
                String catName = editText.getText().toString();
                if (TextUtils.isEmpty(catName)) {
                    ViewHub.setDialogDismissable(dialog, false);
                    ViewHub.setEditError(editText, "分类名称不能为空");
                } else if (catName.contains("%") || catName.contains(" ")) {
                    ViewHub.setDialogDismissable(dialog, false);
                    ViewHub.setEditError(editText, "分类名称存在非法字符，请重新输入");
                } else if (catName.length() > 50) {
                    ViewHub.setDialogDismissable(dialog, false);
                    ViewHub.setEditError(editText, "分类名称超过50个字符，请重新输入");
                } else {
                    ViewHub.setDialogDismissable(dialog, true);
                    new Task(Step.CREATE_CATEGORY).execute(catName);
                }
            }

            @Override
            public void onOkClick(EditText editText) {

            }
        });
    }

    private class Task extends AsyncTask<Object, Void, Object> {

        private Step mStep;

        public Task(Step step) {
            mStep = step;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            switch (mStep) {
                case LOAD_DATA:
                    break;
                case CREATE_CATEGORY:
                    mLoadingDialog.start("提交中...");
                    break;
            }
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                switch (mStep) {
                    case LOAD_DATA:
                        List<ItemShopCategory> data = ShopSetAPI.getItemShopCategory(mContext);
                        return data;
                    case CREATE_CATEGORY:
                        String catName = params[0].toString();
                        ItemShopCategory cat = new ItemShopCategory();
                        cat.setName(catName);
                        List<ItemShopCategory> cats = mAdapter.getData();
                        Collections.sort(cats, SortHelper.CMP_ITEM_SHOP_CATEGORY);// 排序获取最大的sort
                        cat.setSort(cats.get(0).getSort() + 1);
                        int id = ShopSetAPI.createItemShopCategory(mContext, cat);
                        cat.setId(id);
                        return cat;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "error:" + e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            if (mLoadingDialog.isShowing()) {
                mLoadingDialog.stop();
            }
            if (result instanceof String && ((String) result).startsWith("error:")) {
                ViewHub.showLongToast(mContext, ((String) result).replace("error:", ""));
                switch (mStep) {
                    case LOAD_DATA:
                        mListView.showErrorView();
                        mIsLoading = false;
                        mListView.onRefreshComplete();
                        break;
                }
            } else {
                switch (mStep) {
                    case LOAD_DATA:
                        @SuppressWarnings("unchecked")
                        List<ItemShopCategory> data = (List<ItemShopCategory>) result;
                        mAdapter.setData(data);
                        mAdapter.setSelectedCats(mSelectedCategories);
                        mAdapter.notifyDataSetChanged();
                        mListView.onRefreshComplete();
                        ShopCategoryCacheManager.cacheItemShopCategory(mContext, data);
                        mIsLoading = false;
                        break;
                    case CREATE_CATEGORY:
                        ItemShopCategory cat = (ItemShopCategory) result;
                        mAdapter.add(cat, 1);
                        mAdapter.notifyDataSetChanged();
                        ShopCategoryCacheManager.cacheItemShopCategory(mContext, mAdapter.getData());
                        break;

                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    public void onRefresh() {
        if (!mIsLoading) {
            new Task(Step.LOAD_DATA).execute();
        }
    }
}
