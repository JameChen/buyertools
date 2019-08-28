package com.nahuo.buyertool.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.BaseSlideBackActivity;
import com.nahuo.buyertool.ViewHub;
import com.nahuo.buyertool.api.BuyOnlineAPI;
import com.nahuo.buyertool.common.ListUtils;
import com.nahuo.buyertool.common.SpManager;
import com.nahuo.buyertool.common.StringUtils;
import com.nahuo.buyertool.controls.PromptDialog;
import com.nahuo.buyertool.event.PriceToRateTextWatcher;
import com.nahuo.buyertool.event.RateToPriceTextWatcher;
import com.nahuo.buyertool.event.SimpleTextWatcher;
import com.nahuo.buyertool.eventbus.BusEvent;
import com.nahuo.buyertool.eventbus.EventBusId;
import com.nahuo.buyertool.model.ImportItemInfo;
import com.nahuo.buyertool.model.ItemShopCategory;
import com.nahuo.buyertool.model.Share2WPItem;
import com.nahuo.library.controls.LoadingDialog;

import java.text.DecimalFormat;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

import static com.nahuo.buyer.tool.R.id.tv_item_shop_category;

public class Share2WPActivity extends BaseSlideBackActivity implements View.OnClickListener {
    /**
     * 分享成功之后的item，回调使用
     */
    public static final String EXTRA_SHARED_ITEM = "EXTRA_SHARED_ITEM";
    /**
     * 分享前传进来的item
     */
    public static final String EXTRA_SHARE_ITEM = "EXTRA_SHARE_ITEM";
    /**
     * 更新商品分组的结果码
     */
    public static final int RESULT_CODE_ITEM_GROUP = 1;
    /**
     * 选择店铺的商品分类
     */
    public static final int REQUEST_SELECT_ITEM_SHOP_CAT = 432;
    private static final String SP_I_SHIP = "我来发货";
    private static final String SP_SUPPLIER_SHIP = "供货商发货";
    /**
     * 商品分组字符串
     */
    private String mItemGroupNames, mItemGroupIds;
    private Share2WPItem mItem;
    private Context mContext = this;
    private LoadingDialog mLoading;
    /**
     * 供货价
     */
    private TextView mTVSupplyPrice, mTvRetailPriceKey, mTvVisibleRange, mTvItemShopCategories;
    /**
     * 零售价
     */
    private EditText mEtRetailPrice, mETAgentPrice, mEtAgentAddRate, mEtItemDesc, mEtItemTitle, et_single_row, et_into_group;
    // 谁来发货
    private View mLayoutWhoShip;
    private Spinner mSpWhoShip;
    private ImportItemInfo mImportItemInfo;
    private ArrayList<ItemShopCategory> mSelectedShopCategories;
    private boolean mIsItemTop, mIsItemOnSale;
    private String waitDays, groupDealCount;

    private static enum Step {
        LOAD_DATA, SUBMIT_DATA
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_share_2_wp);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.layout_titlebar_default);
        initItem();
        initView();

        new Task(Step.LOAD_DATA).execute();

    }

    private void initItem() {
        mItemGroupNames = SpManager.getUploadNewItemVisibleRangeNames(mContext);
        mItemGroupIds = SpManager.getUploadNewItemVisibleRanageIds(mContext);
        Intent intent = getIntent();
        mItem = (Share2WPItem) intent.getSerializableExtra(EXTRA_SHARE_ITEM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CODE_ITEM_GROUP) {// 选择商品分组
            onItemGroupSelected(data);
        }
        switch (requestCode) {
            case REQUEST_SELECT_ITEM_SHOP_CAT:// 选择商品店铺分类
                if (data != null) {
                    onShopCategorySelected(data);
                }else {
                    mTvItemShopCategories.setText("默认分类");
                }
                break;
        }
    }

    /**
     * @description 商品分组选择后
     * @created 2015-3-17 下午4:51:23
     * @author ZZB
     */
    private void onItemGroupSelected(Intent data) {
        mItemGroupIds = data.getStringExtra("GROUP_IDS");
        mItemGroupNames = data.getStringExtra("GROUP_NAMES");
        mItemGroupIds = StringUtils.deleteEndStr(mItemGroupIds, ",");
        mItemGroupNames = StringUtils.deleteEndStr(mItemGroupNames, ",");
        mTvVisibleRange.setText(mItemGroupNames);
    }

    /**
     * @description 店铺分类选择后
     * @created 2015-3-17 下午4:50:29
     * @author ZZB
     */
    @SuppressWarnings("unchecked")
    private void onShopCategorySelected(Intent data) {
        mIsItemOnSale = data.getBooleanExtra(SelectItemShopCategoryActivity.EXTRA_IS_ON_SALE, false);
        mIsItemTop = data.getBooleanExtra(SelectItemShopCategoryActivity.EXTRA_IS_TOP, false);
        mSelectedShopCategories = (ArrayList<ItemShopCategory>) data
                .getSerializableExtra(SelectItemShopCategoryActivity.EXTRA_SELECTED_CATS);
        if (!ListUtils.isEmpty(mSelectedShopCategories)){
            mTvItemShopCategories.setText(mSelectedShopCategories.get(0).getName());
        }else {
            mTvItemShopCategories.setText("默认分类");
        }
//        StringBuilder cats = new StringBuilder();
//        for (int i = 0; i < mSelectedShopCategories.size(); i++) {
//            if (i == 2) {
//                break;
//            }
//            ItemShopCategory cat = mSelectedShopCategories.get(i);
//            cats.append(cat.getName()).append("/");
//        }
//        String catsStr = StringUtils.deleteEndStr(cats.toString(), "/");
//
//        mTvItemShopCategories.setText(catsStr);
    }

    private void initTitleBar() {
        TextView tvTitle = (TextView) findViewById(R.id.titlebar_tvTitle);
        tvTitle.setText("上架商品");

        Button btnLeft = (Button) findViewById(R.id.titlebar_btnLeft);
        btnLeft.setText(R.string.titlebar_btnBack);
        btnLeft.setVisibility(View.VISIBLE);
        btnLeft.setOnClickListener(this);

        Button btnRight = (Button) findViewById(R.id.titlebar_btnRight);
        btnRight.setVisibility(View.VISIBLE);
        btnRight.setOnClickListener(this);
        btnRight.setText("完成");
    }

    private void initView() {
        initTitleBar();
        mEtItemDesc = (EditText) findViewById(R.id.et_item_desc);
        mEtItemTitle = (EditText) findViewById(R.id.et_item_title);
        mTvItemShopCategories = (TextView) findViewById(tv_item_shop_category);
        mLoading = new LoadingDialog(mContext);
        // mOklading = new OkDialog(mContext);
        // 代理价
        mETAgentPrice = (EditText) findViewById(R.id.et_agent_price);
        // 代理添加率
        mEtAgentAddRate = (EditText) findViewById(R.id.et_add_rate);
        // 供货价
        mTVSupplyPrice = (TextView) findViewById(R.id.tv_supply_price);
        mTVSupplyPrice.setText(mItem.supplyPrice);
        // 零售价
        mEtRetailPrice = (EditText) findViewById(R.id.et_retail_price);
        et_into_group = (EditText) findViewById(R.id.et_into_group);
        et_single_row = (EditText) findViewById(R.id.et_single_row);
        mEtRetailPrice.setText(mItem.retailPrice + "");
        mTvRetailPriceKey = (TextView) findViewById(R.id.tv_retail_price_key);

        mTvVisibleRange = (TextView) findViewById(R.id.tv_visible_range);
        mItemGroupNames = StringUtils.deleteEndStr(mItemGroupNames, ",");
        mLayoutWhoShip = findViewById(R.id.layout_who_ship);
        mSpWhoShip = (Spinner) findViewById(R.id.sp_who_ship);

        // 代理价格控制
        mETAgentPrice.addTextChangedListener(new PriceToRateTextWatcher(mTVSupplyPrice.getText().toString(),
                mETAgentPrice, mEtAgentAddRate));
        // 代理加价率
        mEtAgentAddRate.addTextChangedListener(new RateToPriceTextWatcher(mTVSupplyPrice.getText().toString(),
                mETAgentPrice));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.layout_spinner_item_share2wp,
                new String[]{SP_SUPPLIER_SHIP, SP_I_SHIP});
        adapter.setDropDownViewResource(R.layout.drop_down_item);
        mSpWhoShip.setAdapter(adapter);
        mSpWhoShip.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpManager.setWhoShipOnShare2WP(mContext, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpWhoShip.setSelection(SpManager.getWhoShipOnShare2WP(mContext));
        mTvVisibleRange.setText(mItemGroupNames);
        mEtItemDesc.setText(mItem.getIntro());
        mEtItemDesc.setSelection(TextUtils.isEmpty(mItem.getIntro()) ? 0 : mItem.getIntro().length());
        mEtItemTitle.setText(mItem.name);
        mEtItemDesc.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (mEtItemTitle.getText().toString().length() < 100) {
                    int endPos = 100;
                    if (s.length() >= 100 && StringUtils.isEmojiCharacter(s.charAt(99))) {
                        endPos = 98;
                    }
                    mEtItemTitle.setText(StringUtils.substring(s.toString(), 0, endPos));
                }
            }
        });
        mEtItemTitle.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 100) {
                    int endPos = 100;
                    if (s.length() >= 100 && StringUtils.isEmojiCharacter(s.charAt(99))) {
                        endPos = 98;
                    }
                    String text = StringUtils.substring(s.toString(), 0, endPos);
                    mEtItemTitle.setText(text);
                    mEtItemTitle.setSelection(text.length());
                    ViewHub.showShortToast(mContext, "商品名称只允许输入100个字");
                }
            }
        });
    }

    private class Task extends AsyncTask<Void, Void, Object> {

        private Step mStep;

        public Task(Step step) {
            mStep = step;
        }

        @Override
        protected void onPreExecute() {
            switch (mStep) {
                case LOAD_DATA:
                    mLoading.start("加载中...");
                    break;
                case SUBMIT_DATA:
                    mLoading.start("提交数据中...");
                    break;
            }
        }

        @Override
        protected Object doInBackground(Void... params) {
            try {
                switch (mStep) {
                    case LOAD_DATA:
                        mImportItemInfo = BuyOnlineAPI.getImportItemInfo(mContext, Long.valueOf(mItem.id));
                        break;
                    case SUBMIT_DATA:
                        waitDays = et_single_row.getText().toString();
                        if (TextUtils.isEmpty(waitDays))
                            waitDays = "0";
                        groupDealCount = et_into_group.getText().toString();
                        if (TextUtils.isEmpty(groupDealCount))
                            groupDealCount = "0";
                        Share2WPItem wpitem = new Share2WPItem();
                        wpitem.id = mItem.id;
                        wpitem.setWaitDays(waitDays);
                        wpitem.setGroupDealCount(groupDealCount);
                        wpitem.name = mEtItemTitle.getText().toString();
                        wpitem.agentPrice = mETAgentPrice.getText().toString();
                        wpitem.mGroupIds = mItemGroupIds;
                        wpitem.mGroupNames = mItemGroupNames;
                        wpitem.supplyPrice = mItem.supplyPrice;
                        wpitem.retailPrice = mEtRetailPrice.getText().toString();
                        wpitem.setCloneable(mImportItemInfo.isItemCloneable() ? (getItemCloneable()) : false);
                        wpitem.setIntro(mEtItemDesc.getText().toString());
                        wpitem.isTop = mIsItemTop;
                        wpitem.attrIds = mIsItemOnSale ? "1" : "";
                        wpitem.isClone = getItemCloneable();
                        if (!ListUtils.isEmpty(mSelectedShopCategories)){
                            wpitem.shopCatIds=mSelectedShopCategories.get(0).getId()+"";
                        }else {
                            wpitem.shopCatIds="0";
                        }
//                        if (mSelectedShopCategories != null) {
//                            String shopCatStr = "";
//                            for (ItemShopCategory cat : mSelectedShopCategories) {
//                                    shopCatStr = StringUtils.append(shopCatStr, cat.getId() + "", ",");
//                            }
//                            wpitem.shopCatIds = shopCatStr;
//                        }
                        int myItemId = BuyOnlineAPI.shareToWP(mContext, wpitem);
                        wpitem.myItemId = myItemId;
                        wpitem.imgUrls = mItem.imgUrls;
                        return wpitem;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "error:" + e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Object result) {
            if (mLoading.isShowing()) {
                mLoading.stop();
            }
            if (result instanceof String && ((String) result).startsWith("error:")) {
                ViewHub.showLongToast(mContext, ((String) result).replace("error:", ""));
            } else {
                switch (mStep) {
                    case LOAD_DATA:
                        afterDataLoaded();
                        break;
                    case SUBMIT_DATA:
                        ViewHub.showShortToast(mContext, "上架成功");
                        EventBus.getDefault().postSticky(BusEvent.getEvent(EventBusId.BB_SELECT_ITEM_SHOP_UP, mItem));
                        finish();
//                        EventBus.getDefault().postSticky(BusEvent.getEvent(EventBusId.ON_SHARE_2_WP_SUCCESS, result));
//                        UploadItemFinishedPopup pw = new UploadItemFinishedPopup(Share2WPActivity.this);
//                        pw.setShare2wpItem((Share2WPItem) result);
//                        pw.setListener(new Listener() {
//
//                            @Override
//                            public void onOtherClickDismiss() {
//                                // ShopCategoryCacheManager.addCategoryNumByOne(mContext, mSelectedShopCategories);
//                                finish();
//                            }
//
//                            @Override
//                            public void goOnUpload() {
//                                finish();
//                            }
//                        });
//                        pw.show();
                        break;

                }
            }
        }
    }

    private void afterDataLoaded() {

        DecimalFormat df = new DecimalFormat("0");
        double value = mImportItemInfo.getMyPriceAddRate() * 100;
        mEtAgentAddRate.setText(df.format(value));
        // 如果可修改零售价，显示零售加价率
        if (mImportItemInfo.isRetailPriceUnified()) {// 如果不可修改零售价,零售价不可编辑
            mTvRetailPriceKey.setText("统一零售价");
            mEtRetailPrice.setKeyListener(null);
            mEtRetailPrice.setTextColor(getResources().getColor(R.color.hint));
            mEtRetailPrice.setOnClickListener(this);
        }
        // mLayoutWhoShip.setVisibility(mImportItemInfo.isItemCloneable() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_who_ship_key:// 谁来发货问号
                PromptDialog dialog = new PromptDialog(this);
                dialog.setMessage("供货商发货即由供货商代发货给你的顾客；我来发货则供货商先寄给你，你再寄给顾客");
                dialog.setPositive("知道了", null);
                dialog.show();
                // ViewHub.showOkDialog(mContext, "", "供货商发货即由供货商代发货给你的顾客；我来发货则供货商先寄给你，你再寄给顾客", "知道了");
                break;
            case R.id.tv_visible_range_key:// 谁可看货问号
                // ViewHub.showOkDialog(mContext, "", "选中的分组用户才能看到该款商品，可用来对下级代理进行分组分类管理", "知道了");
                dialog = new PromptDialog(this);
                dialog.setMessage("选中的分组用户才能看到该款商品，可用来对下级代理进行分组分类管理");
                dialog.setPositive("知道了", null);
                dialog.show();
                break;
            case R.id.layout_item_shop_category:// 选择本店分类
                Intent shopCatIntent = new Intent(this, SelectItemShopCategoryActivity.class);
                shopCatIntent.putExtra(SelectItemShopCategoryActivity.EXTRA_SELECTED_CATS, mSelectedShopCategories);
                shopCatIntent.putExtra(SelectItemShopCategoryActivity.EXTRA_IS_TOP, mIsItemTop);
                shopCatIntent.putExtra(SelectItemShopCategoryActivity.EXTRA_IS_ON_SALE, mIsItemOnSale);
                startActivityForResult(shopCatIntent, REQUEST_SELECT_ITEM_SHOP_CAT);
                break;
            case R.id.titlebar_btnLeft:
                finish();
                break;
            case R.id.titlebar_btnRight:
                onSubmitClick();
                break;
            case R.id.layout_visible_range:// 可视范围
//                BaiduStats.log(mContext, BaiduStats.EventId.SHARE_2_WP_CLICK_GROUP);
//                Intent intent = new Intent(mContext, SelectItemGroupActivity.class);
//                // intent.putExtra("ITEM_ID", mItem.itemId);
//                intent.putExtra(SelectItemGroupActivity.KEY_RESULT_CODE, RESULT_CODE_ITEM_GROUP);
//                intent.putExtra(SelectItemGroupActivity.EXTRA_SELECTED_ITEM_IDS, mItemGroupIds);
//                startActivityForResult(intent, RESULT_CODE_ITEM_GROUP);
                break;
            case R.id.et_retail_price:
                if (mImportItemInfo.isRetailPriceUnified()) {
                    ViewHub.showShortToast(mContext, "供货商不允许修改");
                }
                break;
            // default:
            // showToast("on click missed");
            // break;
        }
    }

    private void onSubmitClick() {

        if (TextUtils.isEmpty(mEtItemDesc.getText().toString())) {
            ViewHub.setEditError(mEtItemDesc, "商品描述不能为空");
            return;
        }
//        if (TextUtils.isEmpty(mEtItemTitle.getText().toString())) {
//            ViewHub.setEditError(mEtItemTitle, "商品标题不能为空");
//            return;
//        }
        if (TextUtils.isEmpty(mETAgentPrice.getText().toString())) {
            ViewHub.setEditError(mETAgentPrice, "价格不能为空");
            return;
        }
//        if (TextUtils.isEmpty(mEtRetailPrice.getText().toString())) {
//            ViewHub.setEditError(mEtRetailPrice, "零售价不能为空");
//            return;
//        }
        Double ordvalue = Double.valueOf(mTVSupplyPrice.getText().toString()).doubleValue();
        Double agentPrice = Double.valueOf(mETAgentPrice.getText().toString()).doubleValue();
        double retailPrice = Double.valueOf((mEtRetailPrice.getText().toString())).doubleValue();
        if (ordvalue > agentPrice) {
            ViewHub.setEditError(mETAgentPrice, "给下家的价格不能小于供货价");
            return;
        }
//        if (retailPrice < agentPrice) {
//            ViewHub.setEditError(mEtRetailPrice, "零售价不能小于给下家的价格");
//            return;
//        }

        new Task(Step.SUBMIT_DATA).execute();

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

    /**
     * @description 商品是否可复制
     * @created 2014-11-6 下午4:17:27
     * @author ZZB
     */
    private boolean getItemCloneable() {
        String text = mSpWhoShip.getSelectedItem().toString();
        // 如果我来发货，则商品要复制一份
        return text.equals(SP_I_SHIP);

    }
}
