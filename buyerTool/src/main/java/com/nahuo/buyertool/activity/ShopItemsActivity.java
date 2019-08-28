package com.nahuo.buyertool.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.ViewHub;
import com.nahuo.buyertool.adapter.AllItemGridAdapter;
import com.nahuo.buyertool.adapter.ShopItemAdapter;
import com.nahuo.buyertool.api.ApiHelper;
import com.nahuo.buyertool.api.BuyOnlineAPI;
import com.nahuo.buyertool.api.HttpRequestHelper;
import com.nahuo.buyertool.api.HttpRequestListener;
import com.nahuo.buyertool.api.RequestMethod;
import com.nahuo.buyertool.api.ShopSetAPI;
import com.nahuo.buyertool.common.Const;
import com.nahuo.buyertool.common.ListUtils;
import com.nahuo.buyertool.common.SpManager;
import com.nahuo.buyertool.controls.HeaderGridView;
import com.nahuo.buyertool.dialog.ClonesDialog;
import com.nahuo.buyertool.dialog.ShopCategoryDialog;
import com.nahuo.buyertool.eventbus.BusEvent;
import com.nahuo.buyertool.eventbus.EventBusId;
import com.nahuo.buyertool.model.GoodBaseInfo;
import com.nahuo.buyertool.model.ItemShopCategory;
import com.nahuo.buyertool.model.Params;
import com.nahuo.buyertool.model.PublicData;
import com.nahuo.buyertool.model.Share2WPItem;
import com.nahuo.buyertool.model.ShopCustomInfo;
import com.nahuo.buyertool.model.ShopInfoModel;
import com.nahuo.buyertool.model.ShopItemListModelX;
import com.nahuo.buyertool.model.ShopItemListResultModel;
import com.nahuo.library.controls.AutoCompleteTextViewEx;
import com.nahuo.library.controls.BottomMenu;
import com.nahuo.library.controls.CircleTextView;
import com.nahuo.library.controls.LoadingDialog;
import com.nahuo.library.controls.pulltorefresh.PullToRefreshBase;
import com.nahuo.library.controls.pulltorefresh.PullToRefreshListView.OnLoadMoreListener;
import com.nahuo.library.controls.pulltorefresh.PullToRefreshListView.OnRefreshListener;
import com.nahuo.library.helper.DisplayUtil;
import com.nahuo.library.helper.ImageUrlExtends;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

import static com.nahuo.buyer.tool.R.id.checkbox_all;
import static com.nahuo.buyertool.activity.Share2WPActivity.REQUEST_SELECT_ITEM_SHOP_CAT;

/**
 * 供应商商品列表
 *
 * @author James Chen
 * @create time in 2017/7/5 16:13
 */
public class ShopItemsActivity extends FragmentActivity implements OnClickListener, OnLoadMoreListener,
        OnRefreshListener, HttpRequestListener,
        ShopCategoryDialog.PopDialogListener,
        ClonesDialog.PopDialogListener {
    private CheckBox mCbSelectAll;
    public static final String EXTRA_SHOP_CAT_ID = "EXTRA_SHOP_CAT_ID";
    public static final int REQUEST_SHARE_TO_WP = 15487;
    private static final String TAG = "AblumdetailActivity";
    public static final int REQUEST_ITEM_DETAILS = 1;
    private ShopItemsActivity vThis = this;
    private TextView tvEmptyMessage;
    private ShopItemAdapter mAdapterList;
    private List<ShopItemListModelX> itemList =new ArrayList<>();
    private View emptyView;
    private int mPageIndex = 1, mPageSize = 20;
    private String mUserID, mDomain, mUserName, mShopName, mBanner, mShopID;
    private AutoCompleteTextViewEx mEtSearch;
    private TextView signtrueTV, itemAgentTV, shopNameTV, mTvApplyStatu;
    private TextView signtrueTV2, itemAgentTV2, shopNameTV2, mTvApplyStatu2;

    private ImageView shopLogoImg, bannerImg, mIvAgent;
    private ImageView shopLogoImg2, bannerImg2, mIvAgent2;
    private LinearLayout mLayoutCredit;                                                      // 信誉
    private LinearLayout mLayoutCredit2;                                                     // 信誉

    private View weixunThisShop;
    private View weixunThisShop2;

    private int currentUserApplyStatuID;
    private Context mContext = this;
    private int mShopCatId = -1;
    private List<ItemShopCategory> mItemShopCategories;
    private CircleTextView mTvCartCount;
    private View mRootView;
    private Map<Integer, GoodBaseInfo> mGoodBaseInfos = new HashMap<Integer, GoodBaseInfo>();
    private HttpRequestHelper mRequestHelper = new HttpRequestHelper();
    private EventBus mEventBus = EventBus.getDefault();
    private HeaderGridView mGridView;
    private AllItemGridAdapter mAdapterGrid;
    String keyword = "";
    int height;
    /**
     * 2表示供货价低到高，3表示供货价高到低，4表示转发数少到多，5表示转发数多到少，不设置或1表示默认
     */
    private int mSort = 1;
    /**
     * 时间段：2表示最近三天，3表示最近七天，4表示最近一个月，5表示一个月之前，不设置或1表示默认
     */
    private int mTimeBucket = 1;
    private TextView mTvPrice, mTvPrice2, mTvPrice3;
    private TextView mTvDefault, mTvDefault2, mTvDefault3, tv_title;
    private Spinner mSpnTimes, mSpnTimes2, mSpnTimes3;
    private ImageView mIvMode, mIvMode2, mIvMode3;
    private String[] mTimeBucketTexts;
    private View mLlSort;
    private boolean mSpHadClicked;

    private final static String PREF_KEY_SHOP_SCAN_MODE_GRID = "pref_key_shop_scan_mode_grid";

    private int mColorBlue = Color.parseColor("#38A8FE");
    private int mColorGray = Color.parseColor("#717171");
    private int mCurrentPs = 0;
    private LoadingDialog mLoadingDialog;
    private int Total;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);// 设置自定义标题栏
        setContentView(R.layout.activity_shop_items);
        // getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.layout_titlebar_shopitem);// 更换自定义标题栏布局
        mEventBus.register(this);
        mTimeBucketTexts = getResources().getStringArray(R.array.time_bucket_texts);
        Intent intent = getIntent();
        mShopCatId = intent.getIntExtra(EXTRA_SHOP_CAT_ID, -1);
        mLoadingDialog = new LoadingDialog(this);
        /* 取出Intent中附加的数据 */
        mShopID = intent.hasExtra("shopid") ? intent.getStringExtra("shopid") : "";
        mUserID = intent.hasExtra("Userid") ? intent.getStringExtra("Userid") : "";
        mUserName = intent.hasExtra("Username") ? intent.getStringExtra("Username") : "";
        mDomain = intent.hasExtra("Domain") ? intent.getStringExtra("Domain") : "";
        initView();
        init();
//        if (mUserID.length() > 0) {
//            tv_title.setText(mUserName);
//        } else {
//            if (mDomain.length() > 0) {
//                // 根据domain方式加载，先读取数据，再加载页面
//                GetShopInfoByDomainTask gdt = new GetShopInfoByDomainTask();
//                gdt.execute();
//            } else {
//                // 根据shopid方式加载，先读取数据，再加载页面
//                GetShopInfoByShopIDTask gdt = new GetShopInfoByShopIDTask();
//                gdt.execute();
//            }
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEventBus.unregister(this);
    }

    public void onEventMainThread(BusEvent event) {

        switch (event.id) {
            case EventBusId.BB_SELECT_ITEM_SHOP_UP:
                Share2WPItem item = (Share2WPItem) event.data;
                if (item != null) {
                    int itemId = Integer.valueOf(item.id);
                    List<ShopItemListModelX> gridDatas = mAdapterGrid.getList();
                    for (ShopItemListModelX model : gridDatas) {
                        if (model.getID() == itemId) {
                            model.IsCopy = true;
                            mAdapterGrid.notifyDataSetChanged();
                            break;
                        }
                    }
                }
                break;
            case EventBusId.ON_SHARE_2_WP_SUCCESS:// 分享到微铺成功
//                Share2WPItem item = (Share2WPItem) event.data;
//                if (item != null) {
//                    int itemId = Integer.valueOf(item.id);
//                    List<ShopItemListModelX> datas = mAdapterList.getList();
//                    for (ShopItemListModelX model : datas) {
//                        if (model.getID() == itemId) {
//                            model.setMyID(item.myItemId);
//                            if (item.isClone) {
//                                model.IsCopy = true;//更改为已复制
//                            } else {
//                                model.setMyStatuID(1);//更改为已上架状态
//                            }
//                            model.setMyRetailPrice(Double.valueOf(item.retailPrice));
//                            model.setMyPrice(Double.valueOf(item.agentPrice));
//                            mAdapterList.notifyDataSetChanged();
//                            break;
//                        }
//                    }
//
//                    List<ShopItemListModelX> gridDatas = mAdapterGrid.getList();
//                    for (ShopItemListModelX model : gridDatas) {
//                        if (model.getID() == itemId) {
//                            model.setMyID(item.myItemId);
//                            model.setMyRetailPrice(Double.valueOf(item.retailPrice));
//                            model.setMyPrice(Double.valueOf(item.agentPrice));
//                            if (item.isClone) {
//                                model.IsCopy = true;//更改为已复制
//                            } else {
//                                model.setMyStatuID(1);//更改为已上架状态
//                            }
//                            mAdapterGrid.notifyDataSetChanged();
//                            break;
//                        }
//                    }
                //  }
//                loadData();
                break;
        }
    }

    private ArrayList<ItemShopCategory> mSelectedShopCategories = null;

    /**
     * @description 店铺分类选择后
     * @created 2015-3-17 下午4:50:29
     * @author ZZB
     */
    @SuppressWarnings("unchecked")
    private void onShopCategorySelected(Intent data) {
//        mIsItemOnSale = data.getBooleanExtra(SelectItemShopCategoryActivity.EXTRA_IS_ON_SALE, false);
//        mIsItemTop = data.getBooleanExtra(SelectItemShopCategoryActivity.EXTRA_IS_TOP, false);
        mSelectedShopCategories = (ArrayList<ItemShopCategory>) data
                .getSerializableExtra(SelectItemShopCategoryActivity.EXTRA_SELECTED_CATS);
        ShopCategoryDialog dialog = ShopCategoryDialog.getInstance(this);
        dialog.setList(mSelectedShopCategories);
        dialog.showDialog();
//        if (!ListUtils.isEmpty(mSelectedShopCategories)){
//
//           // mTvItemShopCategories.setText(mSelectedShopCategories.get(0).getName());
//        }else {
//           // mTvItemShopCategories.setText("默认分类");
//        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_SELECT_ITEM_SHOP_CAT:// 选择商品店铺分类
                if (data != null) {
                    onShopCategorySelected(data);
                } else {
                    // mTvItemShopCategories.setText("默认分类");
                }
                break;
        }
//        if (data != null) {
//            switch (requestCode) {
//                case UpdateSharedItemActivity.REQUEST_CODE_UPDATE_WP_ITEM:
//                    UpdateItem item = (UpdateItem) data
//                            .getSerializableExtra(UpdateSharedItemActivity.EXTRA_UPDATED_ITEM);
//                    mAdapterList.updateMyItem(item);
//                    break;
//
//                case REQUEST_ITEM_DETAILS:
//                    int id = data.getIntExtra(ItemDetailsActivity.EXTRA_SHARED_ITEM_ID, -1);
//                    mAdapterList.remove(id);
//                    break;
//
//                case REQUEST_SHARE_TO_WP:
//                    Share2WPItem item2 = (Share2WPItem) data.getSerializableExtra(Share2WPActivity.EXTRA_SHARED_ITEM);
//                    mAdapterList.remove(Integer.valueOf(item2.id));
//                    break;
//            }
//        }
//        if (data != null && requestCode == REQUEST_ITEM_DETAILS) {
//            int id = data.getIntExtra(ItemDetailsActivity.EXTRA_SHARED_ITEM_ID, -1);
//            mAdapterList.remove(id);
//        } else if (data != null && requestCode == REQUEST_SHARE_TO_WP) {// 分享到微铺回调
//            Share2WPItem item = (Share2WPItem) data.getSerializableExtra(Share2WPActivity.EXTRA_SHARED_ITEM);
//            mAdapterList.remove(Integer.valueOf(item.id));
//        } else if (data != null && requestCode == UpdateSharedItemActivity.REQUEST_CODE_UPDATE_WP_ITEM) {
//            UpdateItem updateItem = (UpdateItem) data.getSerializableExtra(UpdateSharedItemActivity.EXTRA_UPDATED_ITEM);
//            mAdapterList.update(updateItem);
//        }
    }


    /**
     * 获取商品分类数据
     */
    private void loadData() {
        mPageIndex = 1;
        new LoadDataTask(true).execute();
    }

    private void initView() {
        findViewById(R.id.iv_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mCbSelectAll = (CheckBox) findViewById(checkbox_all);
        mCbSelectAll.setOnClickListener(this);
        findViewById(R.id.tv_batch_shelves).setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        if (!TextUtils.isEmpty(mUserName))
            tv_title.setText(mUserName);
        // initFilterCategoryPopup();
        emptyView = findViewById(R.id.home_layoutdetail_empty);
        // titleBar = (com.nahuo.library.controls.TitleBar)
        // findViewById(R.id.ablumdetail_titlebar);
        // 初始化适配器
        tvEmptyMessage = (TextView) findViewById(R.id.layout_empty_tvMessage);
        findViewById(R.id.tv_batch_clone).setOnClickListener(this);
        mLlSort = findViewById(R.id.ll_sort);
        mTvDefault3 = (TextView) mLlSort.findViewById(R.id.tv_default);
       // mTvDefault3.setOnClickListener(this);
        mTvPrice3 = (TextView) mLlSort.findViewById(R.id.tv_price);
       // mTvPrice3.setOnClickListener(this);
        mSpnTimes3 = (Spinner) mLlSort.findViewById(R.id.sp_time);
       // mSpnTimes3.setOnItemSelectedListener(this);
       // mSpnTimes3.setOnTouchListener(this);
        mIvMode3 = (ImageView) mLlSort.findViewById(R.id.iv_mode);
      //  mIvMode3.setOnClickListener(this);
        mGridView = (HeaderGridView) findViewById(R.id.gv_items);
        mGridView.setMode(PullToRefreshBase.MODE_BOTH);
       // mGridView.setDisableScrollingWhileRefreshing(false);
        mGridView.setVisibility(View.VISIBLE);
        height = (DisplayUtil.getScreenHeight() - DisplayUtil.dip2px(mContext, 109)) / 2;
        mAdapterGrid = new AllItemGridAdapter(this, height);
        mGridView.setAdapter(mAdapterGrid);
        mGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {

            @Override
            public void onRefresh() {
                ShopItemsActivity.this.onRefresh();
            }

            @Override
            public void onLoadMore() {
                ShopItemsActivity.this.onLoadMore();
            }
        });
//        mGridView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ViewHub.showShortToast(vThis,"dasd");
////                ShopItemListModelX item = (ShopItemListModelX) parent.getAdapter().getItem(position);
////                Intent intent = new Intent(ShopItemsActivity.this, ShopDetailsActivity.class);
////                intent.putExtra(ShopDetailsActivity.EXTRA_ID, item.getID());
////                startActivity(intent);
//                try {
////                    Object obj = parent.getAdapter().getItem(position);
////                    ShopItemListModelX item = null;
////                    if (obj != null && (item = (ShopItemListModelX) obj) != null) {
////                        Intent intent = new Intent(mContext, ItemDetailsActivity.class);
////                        intent.putExtra(ItemDetailsActivity.EXTRA_ID, item.getID());
////                        if (item.getMyID() > 0) {
////                            // 已经转发
////                            item.setID(item.getMyID());
////                            UpdateItem updateItem = ShopItemListModelX.toUpdateItem(item);
////                            intent.putExtra(ItemDetailsActivity.EXTRA_UPDATE_ITEM, updateItem);
////                        }
////                        startActivityForResult(intent, ShopItemsActivity.REQUEST_ITEM_DETAILS);
//                    // }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });

//        mGridView.setOnScrollListener(new OnScrollListener() {
//
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
////                if (firstVisibleItem > 1) {
////                    mLlSort.setVisibility(View.VISIBLE);
////                } else {
////                    mLlSort.setVisibility(View.GONE);
////                }
//            }
//        });
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
                ViewHub.hideKeyboard(ShopItemsActivity.this);
                keyword = mEtSearch.getText().toString();
                if (TextUtils.isEmpty(keyword)) {
                    ViewHub.setEditError(mEtSearch, "请输入搜索内容");
                    return;
                }
                onRefresh();
            }
        });
        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {//actionSearch搜索，对应常量EditorInfo.IME_ACTION_SEARCH效果
                    ViewHub.hideKeyboard(ShopItemsActivity.this);
                    keyword = mEtSearch.getText().toString();
                    if (TextUtils.isEmpty(keyword)) {
                        ViewHub.setEditError(mEtSearch, "请输入搜索内容");
                        return false;
                    }
                    onRefresh();
                }
                return false;
            }
        });
        // 刷新数据
        showEmptyView(false, "");
        emptyView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mGridView.setMode(PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH);
                mGridView.getRefreshableView().setSelection(0);
                mGridView.setRefreshing();
                onRefresh();

//                boolean isGridMode = SpManager.getBoolean(getApplicationContext(), PREF_KEY_SHOP_SCAN_MODE_GRID, false);
//                if (isGridMode) {
//                    mGridView.setMode(PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH);
//                    mGridView.getRefreshableView().setSelection(0);
//                    mGridView.setRefreshing();
//                    onRefresh();
//                } else {
//                    mListView.pull2RefreshManually();
//                    if (mListView != null) {
//                        if (mListView.isCanRefresh())
//                            mListView.onRefreshComplete();
//                        if (mListView.isCanLoadMore())
//                            mListView.onLoadMoreComplete();
//                    }
//                }
            }
        });

        // 添加店铺头信息
     /*   View headview = LayoutInflater.from(vThis).inflate(R.layout.layout_shop_item_head, null);
        View headview_sort = LayoutInflater.from(vThis).inflate(R.layout.layout_shop_item_header_sort, null);

        mLayoutCredit = (LinearLayout) headview.findViewById(R.id.layout_credit);
        mLayoutCredit.setOnClickListener(this);
        mTvApplyStatu = (TextView) headview.findViewById(R.id.tv_apply_statu);
        mIvAgent = (ImageView) headview.findViewById(R.id.iv_agent);
        itemAgentTV = (TextView) headview.findViewById(R.id.shop_item_head_item_agent_counts);
        signtrueTV = (TextView) headview.findViewById(R.id.shop_item_head_signture);
        weixunThisShop = (View) headview.findViewById(R.id.shop_item_head_weixun);
        weixunThisShop.setOnClickListener(vThis);
        shopNameTV = (TextView) headview.findViewById(R.id.shop_item_head_shop_name);
        shopLogoImg = (ImageView) headview.findViewById(R.id.shop_item_head_shop_logo);
        bannerImg = (ImageView) headview.findViewById(R.id.shop_item_head_banner);

        mTvDefault = (TextView) headview_sort.findViewById(R.id.tv_default);
        mTvDefault.setOnClickListener(this);
        mTvPrice = (TextView) headview_sort.findViewById(R.id.tv_price);
        mTvPrice.setOnClickListener(this);
        mSpnTimes = (Spinner) headview_sort.findViewById(R.id.sp_time);
        mSpnTimes.setOnTouchListener(this);
        mSpnTimes.setOnItemSelectedListener(this);

        mIvMode = (ImageView) headview_sort.findViewById(R.id.iv_mode);
        mIvMode.setOnClickListener(this);

//        mListView.addHeaderView(headview);
//        mListView.addHeaderView(headview_sort);

        View headview2 = LayoutInflater.from(this).inflate(R.layout.layout_shop_item_head, null);
        View headview2_sort = LayoutInflater.from(this).inflate(R.layout.layout_shop_item_header_sort, null);

        mLayoutCredit2 = (LinearLayout) headview2.findViewById(R.id.layout_credit);
        mLayoutCredit2.setOnClickListener(this);
        mTvApplyStatu2 = (TextView) headview2.findViewById(R.id.tv_apply_statu);
        mIvAgent2 = (ImageView) headview2.findViewById(R.id.iv_agent);
        itemAgentTV2 = (TextView) headview2.findViewById(R.id.shop_item_head_item_agent_counts);
        signtrueTV2 = (TextView) headview2.findViewById(R.id.shop_item_head_signture);
        weixunThisShop2 = (View) headview2.findViewById(R.id.shop_item_head_weixun);
        weixunThisShop2.setOnClickListener(vThis);
        shopNameTV2 = (TextView) headview2.findViewById(R.id.shop_item_head_shop_name);
        shopLogoImg2 = (ImageView) headview2.findViewById(R.id.shop_item_head_shop_logo);
        bannerImg2 = (ImageView) headview2.findViewById(R.id.shop_item_head_banner);

        *//***//*
        mTvDefault2 = (TextView) headview2_sort.findViewById(R.id.tv_default);
        mTvDefault2.setOnClickListener(this);
        mTvPrice2 = (TextView) headview2_sort.findViewById(R.id.tv_price);
        mTvPrice2.setOnClickListener(this);
        mSpnTimes2 = (Spinner) headview2_sort.findViewById(R.id.sp_time);
        mSpnTimes2.setOnItemSelectedListener(this);
        mSpnTimes2.setOnTouchListener(this);
        mIvMode2 = (ImageView) headview2_sort.findViewById(R.id.iv_mode);
        mIvMode2.setOnClickListener(this);

//        mGridView.addHeaderView(headview2, null, false);
//        mGridView.addHeaderView(headview2_sort, null, false);

        mTvCartCount = (CircleTextView) findViewById(R.id.tv_cart_count);*/

    }

    private void init() {
        loadData();
//        Picasso.with(vThis).load(UpYunConst.getShopLogo(mUserID)).placeholder(R.drawable.empty_photo).into(shopLogoImg);
//        shopNameTV.setText(mShopName);
//
//        Picasso.with(vThis).load(UpYunConst.getShopLogo(mUserID)).placeholder(R.drawable.empty_photo).into(shopLogoImg2);
//        shopNameTV2.setText(mShopName);

        //  boolean isGridMode = SpManager.getBoolean(getApplicationContext(), PREF_KEY_SHOP_SCAN_MODE_GRID, false);
//        if (isGridMode) {
//            mGridView.setVisibility(View.VISIBLE);
//            mListView.setVisibility(View.GONE);
//            mIvMode.setImageResource(R.drawable.ic_browse_mode_grid);
//            mIvMode2.setImageResource(R.drawable.ic_browse_mode_grid);
//            mIvMode3.setImageResource(R.drawable.ic_browse_mode_grid);
//        } else {
//            mGridView.setVisibility(View.GONE);
//            mListView.setVisibility(View.VISIBLE);
//            mIvMode.setImageResource(R.drawable.ic_browse_mode_list);
//            mIvMode2.setImageResource(R.drawable.ic_browse_mode_list);
//            mIvMode3.setImageResource(R.drawable.ic_browse_mode_list);
//        }

//        initTimeBucketSpiner();

//        GetShopCustomInfoByUserIDTask gcbt = new GetShopCustomInfoByUserIDTask();
//        gcbt.execute();
    }

    // 初始化数据
    private void initItemAdapter() {
//        if (itemList == null)
//            itemList = new ArrayList<ShopItemListModelX>();
//        mAdapterList = new ShopItemAdapter(vThis);
//        mListView.setAdapter(mAdapterList);
//        mAdapterList.setListener(new ShopItemAdapter.Listener() {
//            @Override
//            public void onApplyStatuChanged(int applyStatuId) {
//                currentUserApplyStatuID = applyStatuId;
//                updateApplyAgentBtn();
//            }
//        });

        // mAdapterList.setOnBuyClickListener(this);
//        mAdapterGrid = new AllItemGridAdapter(this, height);
//        mGridView.setAdapter(mAdapterGrid);

    }

    @Override
    public void onRefresh() {
        showEmptyView(false, "");// 开始执行刷新操作时，不显示空数据视图
        mPageIndex = 1;
        new LoadDataTask(false).execute();
        if (itemList.size() == 0) {
            showEmptyView(false, "没有数据");
        }
    }

    /**
     * 显示空数据视图
     */
    private void showEmptyView(boolean show, String msg) {
        emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
        if (TextUtils.isEmpty(msg)) {
            tvEmptyMessage.setText(getString(R.string.layout_empty_message));
        } else {
            tvEmptyMessage.setText(msg);
        }
    }

//    /**
//     * /** 绑定款式列表
//     */
//    private void bindItemData(boolean isRefresh) {
//        if (isRefresh) {
//            showEmptyView(false, "");// 开始执行刷新操作时，不显示空数据视图
//            mPageIndex = 1;
//            new LoadDataTask(isRefresh).execute();
//        } else {
//            mPageIndex++;
//            new LoadDataTask(isRefresh).execute();
//        }
//
//    }

//    @Override
//    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//        showEmptyView(true, "");
//    }

    @Override
    public void onLoadMore() {
        mPageIndex++;
        new LoadDataTask(false).execute();
        if (itemList.size() == 0) {
            showEmptyView(true, "您还没有数据");
        } else {
            showEmptyView(false, "");
        }
    }

    /**
     * @description 过滤分类
     * @created 2015-3-19 上午10:16:10
     * @author ZZB
     */
    private void onFileterCategoryClicked(View anchorView) {
        if (mItemShopCategories != null && mItemShopCategories.size() > 0) {
            String[] items = new String[mItemShopCategories.size()];
            for (int i = 0; i < items.length; i++) {
                items[i] = mItemShopCategories.get(i).getName();
                if (mItemShopCategories.get(i).getId() == mShopCatId) {
                    mCurrentPs = i;
                }
            }
            BottomMenu menu = new BottomMenu(vThis);
            menu.setSelectedItemPosition(mCurrentPs);
            menu.setItems(items).setOnMenuItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mCurrentPs = position;
                    filterCategory(mItemShopCategories.get(position).getId());
                }
            });
            menu.show(anchorView);
        } else {
            ViewHub.showShortToast(mContext, "暂无分类");
        }
    }

    /**
     * @description 过滤分类
     * @created 2015-3-24 下午6:33:32
     * @author ZZB
     */
    private void filterCategory(int catId) {
        mShopCatId = catId;
        new LoadDataTask(true).execute();
    }

    //    @Override
//    public void onRightClick(View v) {
//        showPopUp(v);
//        super.onRightClick(v);
//    }
//
//    @Override
//    public void onSearchClick(View v) {
////        Intent searchIntent = new Intent(this, SearchItemsActivity.class);
////        searchIntent.putExtra(SearchItemsActivity.EXTRA_SEARCH_TYPE, SearchItemsActivity.TYPE_SHOP_ITEMS);
////        searchIntent.putExtra(SearchItemsActivity.EXTRA_USER_ID, Integer.valueOf(mUserID));
////        startActivity(searchIntent);
//
////        Intent searchIntent = new Intent(this, CommonSearchActivity.class);
////        searchIntent.putExtra(CommonSearchActivity.EXTRA_SEARCH_TYPE, CommonSearchActivity.SearchType.SUPPLIER_ITEMS);
////        searchIntent.putExtra(CommonSearchActivity.EXTRA_SUPPLIER_ID, Integer.valueOf(mUserID));
////        startActivity(searchIntent);
//
//        super.onSearchClick(v);
//    }
    List<ShopItemListModelX> bSList = new ArrayList<>();

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case checkbox_all:
                mAdapterGrid.setAllCheck();
                break;
            case R.id.tv_batch_shelves:
                //批量上架
                bSList.clear();
                List<ShopItemListModelX> bean = mAdapterGrid.getBatchShelvesCheck();
                if (ListUtils.isEmpty(bean)) {
                    ViewHub.showLongToast(mContext, "请选择商品");
                } else {
                    bSList.addAll(bean);
                    ShopCategoryDialog.getInstance(this).setPositive(this).showDialog();

                }
                break;
            case R.id.tv_batch_clone:
                //批量克隆
                bSList.clear();
                List<ShopItemListModelX> bean2 = mAdapterGrid.getBatchShelvesCheck();
                if (ListUtils.isEmpty(bean2)) {
                    ViewHub.showLongToast(mContext, "请选择商品");
                } else {
                    bSList.addAll(bean2);
                    ClonesDialog.getInstance(this).setPositive(this).showDialog();
                   // CloneDialog.getInstance(this).setPositive(this).showDialog();
                }
                break;
            case R.id.tv_weixun:// 微询
                //ChatHelper.chat(mContext, Integer.valueOf(mUserID), mUserName, null, currentUserApplyStatuID);
                break;
            case R.id.tv_category:// 过滤分类
                onFileterCategoryClicked((View) v.getParent());
                break;
            case R.id.tv_shopping_cart:// 购物车
                //  Utils.gotoShopcart(getApplicationContext());
                // Intent intent = new Intent(mContext, ItemPreviewActivity.class);
                // intent.putExtra("url", "http://" + mShopID + ".weipushop.com/shoppingcart?from=wpapp&userID="
                // + SpManager.getUserId(mContext));
                // intent.putExtra("name", "购物车");
                // startActivity(intent);
                break;
            case R.id.layout_credit:// 商家信誉
//                Intent creditIntent = new Intent(this, ShopCreditActivity.class);
//                creditIntent.putExtra(ShopCreditActivity.EXTRA_USER_ID, Integer.valueOf(mUserID));
//                startActivity(creditIntent);
                break;

//            case R.id.shop_item_head_weixun:// 咨询买家
//                ChatHelper.chat(mContext, Integer.valueOf(mUserID), mUserName, null, currentUserApplyStatuID);
//                break;

//            case R.id.shop_item_head_agent_this_shop:// 代理店铺等
//                switch (currentUserApplyStatuID) {
//                    case ApplyAgentStatu.ACCEPT:
//                        ViewHub.showOkDialog(mContext, "提示", "真的要取消代理？", "取消代理", "继续代理",
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        unAgent(UnAgentShopTask.Step.UNAGENT);
//                                    }
//                                });
//                        break;
//                    case ApplyAgentStatu.APPLYING:
//                        ViewHub.showOkDialog(mContext, "提示", "真的要取消代理申请？", "取消代理申请", "继续等待",
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        unAgent(UnAgentShopTask.Step.CANCEL_APPLY);
//                                    }
//                                });
//                        break;
//                    case ApplyAgentStatu.NOT_APPLY:
//                    case ApplyAgentStatu.REJECT:
//                        // 代理这家店
//                        AgentShopTask task = new AgentShopTask(mContext, Integer.valueOf(mUserID),
//                                currentUserApplyStatuID);
//                        task.setCallback(new AgentShopTask.Callback() {
//                            @Override
//                            public void onAgentFinished() {
//                                currentUserApplyStatuID = UpYunConst.ApplyAgentStatu.APPLYING;
//                                updateApplyAgentBtn();
//                            }
//                        });
//                        task.execute();
//                        break;
//                }
//
//                break;
            case R.id.iv_mode:
                break;

            default:
                setSortStatus(v.getId());
                break;
        }
    }

    private void switchMode() {
        boolean isGridMode = SpManager.getBoolean(getApplicationContext(), PREF_KEY_SHOP_SCAN_MODE_GRID, false);
        SpManager.setBoolean(getApplicationContext(), PREF_KEY_SHOP_SCAN_MODE_GRID, !isGridMode);
        if (isGridMode) {// switch to list mode
            mGridView.setVisibility(View.GONE);
            mIvMode.setImageResource(R.drawable.ic_browse_mode_list);
            mIvMode2.setImageResource(R.drawable.ic_browse_mode_list);
            mIvMode3.setImageResource(R.drawable.ic_browse_mode_list);

        } else {
            mGridView.setVisibility(View.VISIBLE);
            mIvMode.setImageResource(R.drawable.ic_browse_mode_grid);
            mIvMode2.setImageResource(R.drawable.ic_browse_mode_grid);
            mIvMode3.setImageResource(R.drawable.ic_browse_mode_grid);
        }
        mGridView.getRefreshableView().setSelection(mGridView.getHeaderViewCount());

    }

    /**
     * @description 取消代理或者取消申请代理
     * @created 2015-2-3 上午11:10:25
     * @author ZZB
     */
//    private void unAgent(UnAgentShopTask.Step step) {
//        UnAgentShopTask unAgentTask = new UnAgentShopTask(mContext, step, Integer.parseInt(mUserID));
//        unAgentTask.setCallback(new UnAgentShopTask.Callback() {
//            @Override
//            public void onAgentFinished() {
//                currentUserApplyStatuID = UpYunConst.ApplyAgentStatu.NOT_APPLY;
//                updateApplyAgentBtn();
//                List<ShopItemListModelX> data = mAdapterList.getData();
//                for (ShopItemListModelX item : data) {
//                    item.setApplyStatuID(currentUserApplyStatuID);
//                }
//                mAdapterList.notifyDataSetChanged();
//
//            }
//        });
//        unAgentTask.execute();
//    }

    // 弹出
    private void showPopUp(View v) {
//        VerticalPopMenu menu = new VerticalPopMenu(this);
//        menu.setDrawableDivider(R.drawable.line1);
//        menu.addMenuItem(new VerticalPopMenuItem(0, "供货商列表")).addMenuItem(new VerticalPopMenuItem(0, "分享他的微铺"))
//                .setMenuItemClickListener(new OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        switch (position) {
//                            case 0:// 供货商列表
//                                Intent vendorListIntent = new Intent(vThis, VendorListActivity.class);
//                                startActivity(vendorListIntent);
//                                break;
//                            case 1:// 分享他的微铺
//                                String shopUrl = "http://" + mShopID + ".weipushop.com/";
//                                String shopName = mShopName;
//                                String userName = mUserName;
//                                String imageUrl_upyun = UpYunConst.getShopLogo(mUserID);
//                                String imageUrl = ImageUrlExtends.getImageUrl(imageUrl_upyun, UpYunConst.LIST_COVER_SIZE);
//                                ShareEntity mShareData = new ShareEntity();
//                                mShareData.setTitle(shopName);
//                                mShareData.setSummary("微铺号：" + userName + "\n" + signtrueTV.getText().toString());
//                                mShareData.setTargetUrl(shopUrl);
//                                mShareData.setImgUrl(imageUrl);
//                                if (TextUtils.isEmpty(imageUrl)) {
//                                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.app_logo);
//                                    mShareData.setThumData(Utils.bitmapToByteArray(bitmap, true));
//                                }
//                                NahuoShare share = new NahuoShare(vThis, mShareData);
//                                share.showCopyLink(true).show();
//                                break;
//                        }
//                    }
//                }).show(v);
    }

    protected boolean isDialogShowing() {
        return (mLoadingDialog != null && mLoadingDialog.isShowing());
    }

    protected void hideDialog() {
        if (isDialogShowing()) {
            mLoadingDialog.stop();
        }
    }

    @Override
    public void onPopDialogButtonClick(int shopCats, String rate) {
        //批量上架
        new BatchTask(shopCats, rate).execute();
    }

//    @Override
//    public void onCloneDialogButtonClick(int shopCats, String rate) {
//        //批量克隆
//       new CloneTask().execute();
//    }

    @Override
    public void onCopyDialogButtonClick(ShopItemListModelX bean, String oldStr, String newStr) {
        new CloneTask(oldStr,newStr).execute();
    }

    public class BatchTask extends AsyncTask<Void, Void, String> {
        int shopCats;
        String rate;

        BatchTask(int shopCats, String rate) {
            this.shopCats = shopCats;
            this.rate = rate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingDialog.start("正在上架中");
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                hideDialog();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (!result.equals("OK")) {
                // 验证result
                /*if (result.startsWith("401") || result.startsWith("not_registered")) {
                    ApiHelper.checkResult(result, vThis);
                }*/
                ViewHub.showShortToast(getApplicationContext(), result);
            } else {
                ViewHub.showShortToast(getApplicationContext(), "批量上架成功");
                if (mAdapterGrid != null)
                    mAdapterGrid.setHasCopy(bSList);
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            try {
//                Params.GetShopItems param = new Params.GetShopItems();
//                param.page = mPageIndex;
//                param.size = mPageSize;
//                param.userId = Integer.valueOf(mUserID);
//                param.shopCatId = mShopCatId;
//                param.sort = mSort;
//                param.timeBucket = mTimeBucket;
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < bSList.size(); i++) {
                    sb.append(bSList.get(i).getID());
                    if (i < bSList.size() - 1) {
                        sb.append(",");
                    }
                }

                BuyOnlineAPI.getInstance().batchShelves(sb.toString(), shopCats + "",
                        rate, PublicData.getCookie(mContext));

                return "OK";
            } catch (Exception ex) {
                ex.printStackTrace();
                return ex.getMessage() == null ? "未知异常" : ex.getMessage();
            }

        }

    }
    public class CloneTask extends AsyncTask<Void, Void, String> {

        String oldStr = "";
        String newStr = "";

        CloneTask(String oldStr, String newStr){
            this.oldStr = oldStr;
            this.newStr = newStr;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingDialog.start("正在克隆中..");
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                hideDialog();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (!result.equals("OK")) {
                // 验证result
                /*if (result.startsWith("401") || result.startsWith("not_registered")) {
                    ApiHelper.checkResult(result, vThis);
                }*/
                ViewHub.showShortToast(getApplicationContext(), result);
            } else {
                ViewHub.showShortToast(getApplicationContext(), "批量克隆成功");
                if (mAdapterGrid != null)
                    mAdapterGrid.setNoCheck(bSList);
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < bSList.size(); i++) {
                    sb.append(bSList.get(i).getID());
                    if (i < bSList.size() - 1) {
                        sb.append(",");
                    }
                }
                BuyOnlineAPI.getInstance().batchClone(sb.toString(), PublicData.getCookie(mContext),oldStr,newStr);

                return "OK";
            } catch (Exception ex) {
                ex.printStackTrace();
                return ex.getMessage() == null ? "未知异常" : ex.getMessage();
            }

        }

    }

    // 加载数据的task
    public class LoadDataTask extends AsyncTask<Void, Void, String> {
        private boolean mIsRefresh = false;

        public LoadDataTask(boolean isRefresh) {
            mIsRefresh = isRefresh;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mIsRefresh)
                mLoadingDialog.start(getString(R.string.items_loadData_loading));
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                hideDialog();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (Total > 0 ) {
                if ( mCbSelectAll != null)
                mCbSelectAll.setText(Html.fromHtml("<font color=\"#6AB5EA\">全部</font><br/>" + "<font color=\"#C60A1E\">" + Total + "款</font>"));
            }else {
                if ( mCbSelectAll != null)
                mCbSelectAll.setText("全部");
            }

//            mListView.onRefreshComplete();
//            mListView.onLoadMoreComplete();

            mGridView.onRefreshComplete();

//            mAdapterList.setData(itemList);
//            mAdapterList.notifyDataSetChanged();

            mAdapterGrid.setData(itemList);
            mAdapterGrid.notifyDataSetChanged();

            if (itemList.size() == 0) {
                showEmptyView(true, "没有数据");
            } else {
                showEmptyView(false, "");
            }

            if (!result.equals("OK")) {
                // 验证result
                if (result.startsWith("401") || result.startsWith("not_registered")) {
                    ApiHelper.checkResult(result, vThis);
                }
                ViewHub.showShortToast(getApplicationContext(), result);

            }
        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                Params.GetShopItems param = new Params.GetShopItems();
                param.page = mPageIndex;
                param.size = mPageSize;
                param.userId = Integer.valueOf(mUserID);
                param.shopCatId = mShopCatId;
                param.sort = mSort;
                param.timeBucket = mTimeBucket;
                ShopItemListResultModel shopItemListResultModel= BuyOnlineAPI.getInstance().getshopitems(keyword, mPageIndex, mPageSize, mUserID, PublicData.getCookie(mContext));
                List<ShopItemListModelX> result=new ArrayList<>();
                if (shopItemListResultModel!=null){
                    result=shopItemListResultModel.getDatas();
                    Total=shopItemListResultModel.getTotal();
                }
                if (mIsRefresh || mPageIndex == 1) {
                    itemList = result;
                } else {
                    itemList.addAll(result);
                }

                return "OK";
            } catch (Exception ex) {
                Log.e(TAG, "获取款式列表发生异常");
                ex.printStackTrace();
                return ex.getMessage() == null ? "未知异常" : ex.getMessage();
            }

        }

    }

    // 根据domain获取shopinfo的task
    private class GetShopInfoByDomainTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            hideDialog();
            if (!result) {
                ViewHub.showShortToast(getApplicationContext(), "未识别店铺");

                if (mUserID.equals(String.valueOf(SpManager.getUserId(mContext)))) {
                    // 主页面选择微铺菜单
//                    Intent serviceIntent = new Intent();
//                    serviceIntent.setAction(MainActivity.SELECT_MY_ITEM);
//                    sendBroadcast(serviceIntent);
//                    finish();
                }

            } else {
                // initTitlebard();
                //setTitle(mUserName);
                // initView();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingDialog.start(getString(R.string.items_loadData_loading));

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String cookie = PublicData.getCookie(vThis);

                ShopInfoModel result = ShopSetAPI.getShopInfoByDomain(mDomain, cookie);
                mUserName = result.getName();
                mUserID = String.valueOf(result.getUserID());

                return true;
            } catch (Exception ex) {
                Log.e(TAG, "GetShopInfoByDomainTask发生异常");
                ex.printStackTrace();
                return false;
            }
        }

    }

    // 根据shopid获取shopinfo的task
    private class GetShopInfoByShopIDTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            mLoadingDialog.stop();
            if (!result) {
                ViewHub.showShortToast(getApplicationContext(), "未识别店铺");
                // initView();

                if (mUserID.equals(String.valueOf(SpManager.getUserId(mContext)))) {
                    // 主页面选择微铺菜单
//                    Intent serviceIntent = new Intent();
//                    serviceIntent.setAction(MainActivity.SELECT_MY_ITEM);
//                    sendBroadcast(serviceIntent);
//                    finish();
                }

            } else {
                // initTitlebard();
                setTitle(mUserName);
                // initView();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingDialog.start(getString(R.string.items_loadData_loading));
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String cookie = PublicData.getCookie(vThis);

                ShopInfoModel result = ShopSetAPI.getShopInfoByShopID(mShopID, cookie);
                mUserName = result.getName();
                mUserID = String.valueOf(result.getUserID());

                return true;
            } catch (Exception ex) {
                Log.e(TAG, "GetShopInfoByShopIDTask发生异常");
                ex.printStackTrace();
                return false;
            }
        }

    }

    // 根据userid获取首页数据
    private class GetShopCustomInfoByUserIDTask extends AsyncTask<Void, Void, Object> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingDialog.start(getString(R.string.items_loadData_loading));
        }

        @Override
        protected Object doInBackground(Void... params) {
            try {
                ShopCustomInfo shopCustomInfo = ShopSetAPI
                        .getShopCustomInfoByUserId(mContext, Integer.valueOf(mUserID));
                return shopCustomInfo;
            } catch (Exception e) {
                e.printStackTrace();
                return "error:" + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            hideDialog();
            if (result instanceof String && ((String) result).startsWith("error:")) {
                ViewHub.showLongToast(mContext, ((String) result).replace("error:", ""));
            } else {
                try {
                    ShopCustomInfo shopInfo = (ShopCustomInfo) result;
                    mItemShopCategories = shopInfo.getShopCats();
                    // mItemShopCatGridAdapter.setData(mItemShopCategories);
                    mBanner = shopInfo.getBanner();
                    mShopID = shopInfo.getShopId() + "";
                    mShopName = shopInfo.getShopTitle();
                    shopNameTV.setText(mShopName);
                    shopNameTV2.setText(mShopName);
                    int itemCount = shopInfo.getShopItemCount();
                    int agentCount = shopInfo.getAgentUserCount();
                    String signature = shopInfo.getSignature();

                    currentUserApplyStatuID = shopInfo.getCurrentUserApplyStatuId();
                    ViewHub.drawSellerCreditLevel(mContext, mLayoutCredit, shopInfo.getShopCredit().getId());
                    ViewHub.drawSellerCreditLevel(mContext, mLayoutCredit2, shopInfo.getShopCredit().getId());

                    updateApplyAgentBtn();
                    itemAgentTV.setText("商品" + itemCount + "  /  代理" + agentCount);
                    itemAgentTV2.setText("商品" + itemCount + "  /  代理" + agentCount);
                    signtrueTV.setText(signature);
                    signtrueTV2.setText(signature);
                    mTvCartCount.setVisibility(shopInfo.getCartItemCount() > 0 ? View.VISIBLE : View.GONE);
                    mTvCartCount.setText(shopInfo.getCartItemCount() + "");

                    // banner图
                    if (!TextUtils.isEmpty(mBanner)) {
                        Resources res = mContext.getResources();
                        String url = ImageUrlExtends.getImageUrl(mBanner, Const.HEADER_BG_SIZE);
                        Picasso.with(vThis).load(url).placeholder(R.drawable.empty_photo).into(bannerImg);
                        Picasso.with(vThis).load(url).placeholder(R.drawable.empty_photo).into(bannerImg2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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

    /**
     * @description 更新申请代理按钮的可视性
     * @created 2014-11-18 上午11:14:44
     * @author ZZB
     */
    private void updateApplyAgentBtn() {
//        switch (currentUserApplyStatuID) {
//            case ApplyAgentStatu.NOT_APPLY:
//            case ApplyAgentStatu.REJECT:
//                mIvAgent.setImageResource(R.drawable.add_agent_circle);
//                mIvAgent2.setImageResource(R.drawable.add_agent_circle);
//
//                mTvApplyStatu.setText("申请代理");
//                mTvApplyStatu2.setText("申请代理");
//
//                break;
//            case ApplyAgentStatu.APPLYING:
//                mTvApplyStatu.setText("申请中");
//                mTvApplyStatu2.setText("申请中");
//
//                mIvAgent.setImageResource(R.drawable.ydl);
//                mIvAgent2.setImageResource(R.drawable.ydl);
//
//                break;
//            case ApplyAgentStatu.ACCEPT:
//                mTvApplyStatu.setText("已代理");
//                mTvApplyStatu2.setText("已代理");
//                mIvAgent.setImageResource(R.drawable.ydl);
//                mIvAgent2.setImageResource(R.drawable.ydl);
//
//                break;
//        }
    }

//    @Override
//    public void onBuyClickListener(ShopItemListModelX model) {
//        if (mGoodBaseInfos.containsKey(model.getID())) {
//            showMenu(mGoodBaseInfos.get(model.getID()));
//        } else {
//            getItemInfo(model.getID());
//        }
//    }

//    private void showMenu(final GoodBaseInfo info) {
//        mRootView = findViewById(R.id.rootView);
//        SelectSizeColorMenu menu = new SelectSizeColorMenu(this, info);
//        menu.setSelectMenuDismissListener(new SelectMenuDismissListener() {
//            @Override
//            public void dismissStart(long duration) {
//                ScaleAnimation animation = new ScaleAnimation(0.8f, 1.0f, 0.8f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
//                        Animation.RELATIVE_TO_SELF, 0.5f);
//                animation.setFillAfter(true);
//                animation.setDuration(duration);
//                mRootView.startAnimation(animation);
//            }
//
//            @Override
//            public void dismissEnd() {
//            }
//        });
//        menu.show();
//        ScaleAnimation animation = new ScaleAnimation(1.0f, 0.8f, 1.0f, 0.8f, Animation.RELATIVE_TO_SELF, 0.5f,
//                Animation.RELATIVE_TO_SELF, 0.5f);
//        animation.setFillAfter(true);
//        animation.setDuration(400);
//        mRootView.startAnimation(animation);
//    }

    private void getItemInfo(int id) {

        HttpRequestHelper.HttpRequest httpRequest = mRequestHelper.getRequest(getApplicationContext(),
                RequestMethod.ShopMethod.SHOP_GET_ITEM_BASE_INFO, this);

        httpRequest.addParam("id", id + "");
        httpRequest.setConvert2Class(GoodBaseInfo.class);
        httpRequest.doPost();
    }

    @Override
    public void onRequestStart(String method) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(vThis);
        }
        if (RequestMethod.ShopMethod.SHOP_GET_ITEM_BASE_INFO.equals(method)) {
            mLoadingDialog.setMessage("正在读取商品信息....");
        }
        mLoadingDialog.show();
    }

    @Override
    public void onRequestSuccess(String method, Object object) {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
        if (RequestMethod.ShopMethod.SHOP_GET_ITEM_BASE_INFO.equals(method)) {
            Log.i(TAG, "onRequestSuccess :" + object.toString());
            GoodBaseInfo info = (GoodBaseInfo) object;
            if (info != null) {
                mGoodBaseInfos.put(info.ItemID, info);
                // showMenu(info);
            }
        }
    }

    @Override
    public void onRequestFail(String method, int statusCode, String msg) {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public void onRequestExp(String method, String msg) {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    private void initTimeBucketSpiner() {
        ArrayAdapter<String> mSpAdapter = new ArrayAdapter<String>(this, R.layout.layout_my_items_head_spinner_item,
                android.R.id.text1, mTimeBucketTexts);
        // mSpAdapter.setDropDownViewResource(R.layout.drop_down_item);
        mSpnTimes.setAdapter(mSpAdapter);
        mSpnTimes2.setAdapter(mSpAdapter);
        mSpnTimes3.setAdapter(mSpAdapter);

    }

//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//        mSpnTimes.setSelection(position);
//        mSpnTimes2.setSelection(position);
//        mSpnTimes3.setSelection(position);
//
//        mTimeBucket = position + 1;
//
//        Log.i(TAG, "mTimeBucket:" + mTimeBucket);
//        if (mSpHadClicked) {
//            mPageIndex = 1;
//            new LoadDataTask(true).execute();
//        }
//        mSpHadClicked = false;
//    }

//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//    }

    private void setSortStatus(int resId) {

        if (R.id.tv_default == resId) {
            mTvDefault.setTextColor(mColorBlue);
            mTvDefault2.setTextColor(mColorBlue);
            mTvDefault3.setTextColor(mColorBlue);

            mTvPrice2.setTextColor(mColorGray);
            mTvPrice.setTextColor(mColorGray);
            mTvPrice3.setTextColor(mColorGray);

            mTvPrice.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up_gray, 0);
            mTvPrice2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up_gray, 0);
            mTvPrice3.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up_gray, 0);

            mSort = 1;

            loadData();

        } else if (R.id.tv_price == resId) {
            mTvPrice.setTextColor(mColorBlue);
            mTvPrice2.setTextColor(mColorBlue);
            mTvPrice3.setTextColor(mColorBlue);

            mTvDefault.setTextColor(mColorGray);
            mTvDefault2.setTextColor(mColorGray);
            mTvDefault3.setTextColor(mColorGray);

            if (2 == mSort) {
                mSort = 3;
                mTvPrice.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
                mTvPrice2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
                mTvPrice3.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);

            } else {

                mSort = 2;
                mTvPrice.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up, 0);
                mTvPrice2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up, 0);
                mTvPrice3.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up, 0);

            }

            loadData();

        }

    }

//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        mSpHadClicked = true;
//        return false;
//    }
}
