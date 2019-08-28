package com.nahuo.buyertool.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.EdgeEffectCompat;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luck.picture.lib.tools.ScreenUtils;
import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.BaseActivity;
import com.nahuo.buyertool.PicGalleryActivity;
import com.nahuo.buyertool.adapter.SlidePicPagerAdapter1;
import com.nahuo.buyertool.adapter.VideoAdapter;
import com.nahuo.buyertool.api.BuyOnlineAPI;
import com.nahuo.buyertool.common.Const;
import com.nahuo.buyertool.common.SpManager;
import com.nahuo.buyertool.common.TuanPiUtil;
import com.nahuo.buyertool.model.ItemDetailShopInfo;
import com.nahuo.buyertool.model.ItemShopCategory;
import com.nahuo.buyertool.model.OrderButton;
import com.nahuo.buyertool.model.ProductModel;
import com.nahuo.buyertool.model.PublicData;
import com.nahuo.buyertool.model.ShopItemListModel;
import com.nahuo.buyertool.model.ShopItemModelx;
import com.nahuo.buyertool.model.UpdateItem;
import com.nahuo.library.controls.CircleTextView;
import com.nahuo.library.controls.FlowIndicator;
import com.nahuo.library.controls.FlowLayout;
import com.nahuo.library.controls.LoadingDialog;
import com.nahuo.library.controls.PagerScrollView;
import com.nahuo.library.helper.DisplayUtil;
import com.nahuo.library.helper.FunctionHelper;
import com.nahuo.library.helper.ImageUrlExtends;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.greenrobot.event.EventBus;
/**
 * 商品详情
 *@author  James Chen
 *@create time in 2017/7/11 16:36
 */
public class ShopDetailsActivity extends BaseActivity {
    TextView tv_title;
    private static final int REQUEST_SHARE_TO_WP = 1;
    private static final int REQUEST_UPDATE_MY_ITEM = 2;
    private Context mContext = this;
    private LoadingDialog mDialog;
    private ShopDetailsActivity vThis = this;
    /** 判断是否转发 */
    // public static final String EXTRA_SHARE_STATU = "EXTRA_SHARE_STATU";
    /**
     * 判断是否是自己上传的款
     */
    // public static final String EXTRA_IS_SOURCE = "EXTRA_IS_SOURCE";
    /**
     * 必须传int类型，否则会找不到商品！
     */
    public static final String EXTRA_ID = "EXTRA_ID";
    public static final String EXTRA_NAME = "EXTRA_NAME";
    public static final String EXTRA_PIN_HUO_ID = "EXTRA_PIN_HUO_ID";
    public static final String EXTRA_UPDATE_ITEM = "EXTRA_UPDATE_ITEM";
    /**
     * 成功转发到微铺的item id
     */
    public static final String EXTRA_SHARED_ITEM_ID = "EXTRA_SHARED_ITEM_ID";
    private DecimalFormat df = new DecimalFormat("#0.00");
    private ViewPager mPager;
    private SlidePicPagerAdapter1 mPagerAdapter;
    private FlowIndicator mLayoutIndicator;
    private TextView mTvDesc, mTvRetailPrice, mTvAgentPrice, mTvShopName, mTvSignature, mTvShare2Wp,
            mTvFavorite, mTvEnterWp, mTvTitle, mTvViewOrgItem, tv_rigt_bg, tv_left_bg;
    private View mBtnApplyAgent;
    private ImageView mIvShopLogo;
    private FlowLayout mLayoutCreditBages;
    private FlowLayout mGvTags;
    private int mId;
    private int chengtuanCount;
    private int dealCount;
    private boolean tuanpiOver;
    // 自己上传和转发的商品
    private boolean mIsSelf;
    private ItemDetailShopInfo mShopInfo;
    // private ItemDetailUserInfo mUserInfo;
    private boolean mLoadingShopInfo = true;
    private boolean mLoadingUserInfo = true;
    // 颜色对应的尺码
    private Map<String, String> mColorSizeMap = new HashMap<String, String>();
    // 尺码对应的颜色
    private Map<String, String> mSizeColorMap = new HashMap<String, String>();

    private Set<String> mColors = new LinkedHashSet<String>();
    private Set<String> mSizes = new LinkedHashSet<String>();
    private WebView mWebView, wv_item_detail_des;
    private ShopItemModelx mShopItem;
    // 分享到微铺之后的item
    private UpdateItem mUpdateItem;
    private ArrayList<String> mBasePicUrls = new ArrayList<String>();
    private Html.ImageGetter imageGetter;
    protected String mSelectedColor, mSelectedSize;
    protected int mSelectedBuyCount;
    private List<ItemShopCategory> mItemShopCategories;
    private View mRootView;
    private ShopItemModelx mShopItemModel;
    private TextView mTvWeixun;
    private TextView mTvShare, tv_title_top;
    private TextView tvChengTuanTips, tvChengTuanTipsDetail;
    private ProgressBar progress;
    private EventBus mEventBus = EventBus.getDefault();
    private RelativeLayout mBtnBuy, rlTContent, rl_scend;
    private FlowLayout buttons;
    private int mCurrentPs = 0;
    private int mPinHuoId;
    private CircleTextView circle_car_text;
    private ColorDrawable mDrawable;
    private LinearLayout ll_time;
    private TextView mTvHH, mTvH, mTvMM, mTvM, mTvSS, mTvS, mTvF, mTvDay, mTvDayDesc;
    private long mEndMillis = 0;
    private int sc_y = 0;
    private ArrayList<String> urls = new ArrayList<String>();
    private int totalSaleCount = 0;
    private boolean isOnPause = false;
    private ListView lv_videos;
    private VideoAdapter adapter;
    private LinearLayout ll_propertys_tags;
    private View layout_explain;
    List<String> vList = new ArrayList<>();
    List<String> result = new ArrayList<String>();
    String name="";
    EdgeEffectCompat leftEdge, rightEdge;
    private static enum Step {
        SHOP_RECORD, PINHUOITEM_DETAIL, LOAD_SHOP_ITEM, APPLY_AGENT, LOAD_SHOP_INFO, BUY, FAVORITE_ITEM, CHECKMYITEMFAVORITE, REMOVE_FAVORITE_ITEM, TUANPI, SaveReplenishmentRecord
    }

    int useId;
    private boolean current_flag = false;
    boolean CanDownLoadPicAndVideo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);
        initView();

    }

    private void initView() {
        Intent intent = getIntent();
        mId = intent.getIntExtra(EXTRA_ID, -1);
        name=intent.getStringExtra(EXTRA_NAME);
        findViewById(R.id.titlebar_btnLeft).setVisibility(View.VISIBLE);
        findViewById(R.id.titlebar_btnLeft).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_title = ((TextView) findViewById(R.id.tv_title));
        tv_title.setText(name);
        imageGetter = new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                Drawable drawable = null;
                int rId = Integer.parseInt(source);
                drawable = mContext.getResources().getDrawable(rId);
                drawable.setBounds(0, 0, Const.getQQFaceWidth(mContext), Const.getQQFaceWidth(mContext));
                return drawable;
            }

        };
        init();
        new Task(Step.PINHUOITEM_DETAIL).execute();
    }

    private void init() {
        mRootView = findViewById(R.id.rootView);
        ll_propertys_tags = (LinearLayout) findViewById(R.id.propertys_tags);
        lv_videos = (ListView) findViewById(R.id.lv_videos);
//        tv_title_top = (TextView) findViewById(R.id.tv_title_top);
//        tv_left_bg = (TextView) findViewById(R.id.tv_left_bg);
//        tv_rigt_bg = (TextView) findViewById(R.id.tv_rigt_bg);
//        mTvHH = (TextView) findViewById(R.id.tv_hh);
//        mTvH = (TextView) findViewById(R.id.tv_h);
//        mTvMM = (TextView) findViewById(R.id.tv_mm);
//        mTvM = (TextView) findViewById(R.id.tv_m);
//        mTvSS = (TextView) findViewById(R.id.tv_ss);
//        mTvS = (TextView) findViewById(R.id.tv_s);
//        mTvF = (TextView) findViewById(R.id.tv_f);
//        mTvDay = (TextView) findViewById(R.id.tv_day);
//        mTvDayDesc = (TextView) findViewById(R.id.tv_day_desc);
//        circle_car_text = (CircleTextView) findViewById(R.id.circle_car_text);
        mLayoutCreditBages = (FlowLayout) findViewById(R.id.layout_credit_bage);
        mTvEnterWp = (TextView) findViewById(R.id.tv_enter_wp);
        mIvShopLogo = (ImageView) findViewById(R.id.iv_shop_logo);
        mTvDesc = (TextView) findViewById(R.id.tv_item_desc);
//        mTvDesc.setOnLongClickListener(new OnLongClickCopyListener());

        tvChengTuanTips = (TextView) findViewById(R.id.chengtuan_tips);
        tvChengTuanTipsDetail = (TextView) findViewById(R.id.chengtuan_tips_detail);
        progress = (ProgressBar) findViewById(R.id.chengtuan_progress);

        mTvTitle = (TextView) findViewById(R.id.tv_item_title);
        //   mTvTitle.setOnLongClickListener(new OnLongClickCopyListener());
        mGvTags = (FlowLayout) findViewById(R.id.gv_tags);
        mTvRetailPrice = (TextView) findViewById(R.id.tv_retail_price);
//        mTvAgentPrice = (TextView) findViewById(R.id.tv_agent_price);
//        mTvAgentPrice.setVisibility(View.INVISIBLE);
        mTvShopName = (TextView) findViewById(R.id.tv_shop_name);
        mTvShare2Wp = (TextView) findViewById(R.id.tv_share_to_wp);
        mTvFavorite = (TextView) findViewById(R.id.tv_favorite);
        mTvViewOrgItem = (TextView) findViewById(R.id.tv_view_org_item);
        mWebView = (WebView) findViewById(R.id.wv_item_detail);
        wv_item_detail_des = (WebView) findViewById(R.id.wv_item_detail_des);
        mBtnApplyAgent = findViewById(R.id.btn_apply_agent);
        mTvSignature = (TextView) findViewById(R.id.tv_signature);
        buttons = (FlowLayout) findViewById(R.id.ll_order_info_btn_parent);
        mPager = (ViewPager) findViewById(R.id.pager);
        FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(ScreenUtils.getScreenWidth(this),ScreenUtils.getScreenWidth(this)*5/4);
        mPager.setLayoutParams(params);
        mLayoutIndicator = (FlowIndicator) findViewById(R.id.layout_indicator);
        try {
            Field leftEdgeField = mPager.getClass().getDeclaredField("mLeftEdge");
            Field rightEdgeField = mPager.getClass().getDeclaredField("mRightEdge");
            if (leftEdgeField != null && rightEdgeField != null) {
                leftEdgeField.setAccessible(true);
                rightEdgeField.setAccessible(true);
                leftEdge = (EdgeEffectCompat) leftEdgeField.get(mPager);
                rightEdge = (EdgeEffectCompat) rightEdgeField.get(mPager);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mLayoutIndicator.setSelectedPos(position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (rightEdge != null && !rightEdge.isFinished()) {
                    //最后一个项继续拉的话,下拉到详情页
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            // ((PagerScrollView) mRootView).scrollTo(0, FunctionHelper.getScreenHeight((Activity) mContext));
                            ((PagerScrollView) mRootView).smoothScrollToSlow(0, FunctionHelper.getScreenHeight((Activity) mContext), 1000);
                        }
                    });

                }
            }
        });
        mPagerAdapter = new SlidePicPagerAdapter1(this.getSupportFragmentManager());
        mPagerAdapter.setPicZoomable(false);
        mPager.setAdapter(mPagerAdapter);
        mPagerAdapter.setOnItemClickLitener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String url = (String) v.getTag();
                if (url.contains(".mp4")) {
                    Intent intent = new Intent(ShopDetailsActivity.this, VideoActivity1.class);
                    intent.putExtra(VideoActivity1.VIEDEO_URL_EXTR, url);
                    intent.putExtra(VideoActivity1.VIEDEO_ISHOW_DOWN_EXTR, CanDownLoadPicAndVideo);
                    startActivity(intent);

                } else {
                    int pos = 0;
                    if (current_flag) {
                        if (mPager.getCurrentItem() > 0)
                            pos = mPager.getCurrentItem() - 1;
                    } else {
                        pos = mPager.getCurrentItem();
                    }
                    Intent intent = new Intent(mContext, PicGalleryActivity.class);
                    intent.putExtra(PicGalleryActivity.EXTRA_CUR_POS, pos);
                    intent.putStringArrayListExtra(PicGalleryActivity.EXTRA_URLS, mBasePicUrls);
                    startActivity(intent);
                }
            }
        });

        mTvWeixun = (TextView) findViewById(R.id.tv_weixun);
        mTvShare = (TextView) findViewById(R.id.tv_share);
        mDialog = new LoadingDialog(this);

        //默认是灰色
        findViewById(R.id.btn_buynow).setClickable(false);
        TextView textView = (TextView) findViewById(R.id.btn_buynow_txt);
        textView.setText("正在加载");
        findViewById(R.id.btn_buynow).setBackgroundColor(getResources().getColor(R.color.lightgray));

//        findViewById(R.id.tvTLeft).setOnClickListener(this);
//        //tvRight.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.toolbar_cart, 0);
//        findViewById(R.id.tl_right).setOnClickListener(this);
//        rlTContent = (RelativeLayout) findViewById(R.id.rlTContent);
//        ll_time = (LinearLayout) findViewById(R.id.ll_time);
        // initScollview();
    }

    private class Task extends AsyncTask<Object, Void, Object> {
        private Step mStep;

        public Task(Step step) {
            mStep = step;
        }

        @Override
        protected void onPreExecute() {
            switch (mStep) {
                case SHOP_RECORD:
                    break;
                case PINHUOITEM_DETAIL:
                    mDialog.start("加载数据中");
                    break;

            }
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                switch (mStep) {
                    case PINHUOITEM_DETAIL:
                        //拼货商品信息
                        mShopItem = BuyOnlineAPI.getInstance().getPiHuoItemDetailNew(mId, mPinHuoId, PublicData.getCookie(mContext));
                        // Log.e("Itemyu", "===>" + mShopItem.isFavorite() + "加载商品信息");
//                        if (mUpdateItem == null) {
//                            setUpUpdateItem();
//                        }
                        return mShopItem;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "error:" + e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (mDialog.isShowing()) {
                mDialog.stop();
            }
            if (result instanceof String && ((String) result).startsWith("error:")) {
                // ViewHub.showLongToast(mContext, ((String) result).replace("error:", ""));
            } else {
                switch (mStep) {
                    case PINHUOITEM_DETAIL:
                        try {
                            ShopItemModelx bean = (ShopItemModelx) result;
                            onShopItemLoaded(bean);
                            boolean isMyFavorite1 = false;
                            isMyFavorite1 = bean.isFavorite();
                            CanDownLoadPicAndVideo=bean.CanDownLoadPicAndVideo;
                            Log.e("Itemyu", "===>" + isMyFavorite1 + "");
                            // itemFavoriteChange(isMyFavorite1);
                            ShopItemModelx.ActivityBean mAbean = bean.getActivity();
                            String endTime = "";
                            if (mAbean != null) {
                                chengtuanCount = mAbean.getChengTuanCount();
                                dealCount = mAbean.getTransCount();
                                totalSaleCount = mAbean.getTotalSaleCount();
                                tuanpiOver = !mAbean.isIsStart();
                                endTime = mAbean.getEndTime();
                            }
                            onTuanPiDataLoaded();
//                            long costs1 = mLoadItemTc.end();
//                            mEndMillis = getMillis(endTime);
//                            loadCountDown();
                            //   new Task(Step.SHOP_RECORD).execute();
                            //   BaiduStats.log(mContext, BaiduStats.EventId.ENTER_ITEM_DETAIL_TIME, costs1 + "-10倍", costs1 * 10);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                }

            }
        }

    }

    /**
     * @description 团批数据加载完成
     * @author pj
     */
    private void onTuanPiDataLoaded() {
        progress.setMax(chengtuanCount);
        progress.setProgress(dealCount);
        tvChengTuanTips.setText(TuanPiUtil.getChengTuanTips(tuanpiOver, chengtuanCount, dealCount));
        tvChengTuanTipsDetail.setText(TuanPiUtil.getChengTuanDetailTips(tuanpiOver, chengtuanCount, dealCount));
        if (tuanpiOver) {

            if (chengtuanCount >= dealCount) {
                progress.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.style_progressbar_nostatus));
            } else {
                progress.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.style_progressbar));
            }
            int invCount = 0;
            List<ProductModel> modelList = mShopItemModel.getProducts();
            if (modelList != null) {
                for (ProductModel pm : modelList) {
                    invCount += pm.getStock();
                }
            }
            if (invCount == 0) {
                findViewById(R.id.btn_buynow).setClickable(false);
                TextView textView1 = (TextView) findViewById(R.id.btn_buynow_txt);
                textView1.setText("已抢完");
                findViewById(R.id.btn_buynow).setBackgroundColor(getResources().getColor(R.color.lightgray));

            } else {
                findViewById(R.id.btn_buynow).setClickable(true);
                TextView textView = (TextView) findViewById(R.id.btn_buynow_txt);
                textView.setText("发起补拼");
              //  mTvShare.setVisibility(View.VISIBLE);
                mTvShare.setText("约店主一起拼");
                findViewById(R.id.btn_buynow).setBackgroundResource(R.drawable.btn_red);
            }

            if (mShopItem.getButtons() != null && mShopItem.getButtons().size() > 0) {
                for (OrderButton b : mShopItem.getButtons()) {
                    if (b.getAction().equals("补货")) {
                        buttons.setVisibility(View.VISIBLE);
                    } else {
                        buttons.setVisibility(View.GONE);
                    }
                }
            }

        } else {
            //判断无库存时，灰掉购买按钮
            int invCount = 0;
            for (ProductModel pm : mShopItemModel.getProducts()) {
                invCount += pm.getStock();
            }
            if (invCount == 0) {
                findViewById(R.id.btn_buynow).setClickable(false);
                TextView textView1 = (TextView) findViewById(R.id.btn_buynow_txt);
                textView1.setText("已抢完");
                findViewById(R.id.btn_buynow).setBackgroundColor(getResources().getColor(R.color.lightgray));

            } else {
                findViewById(R.id.btn_buynow).setClickable(true);
                TextView textView = (TextView) findViewById(R.id.btn_buynow_txt);
                findViewById(R.id.btn_buynow).setBackgroundResource(R.drawable.btn_red);

                if (mShopItemModel.getDisplayStatu().equals("新款") ||
                        mShopItemModel.getDisplayStatu().equals("补货中")) {
                    textView.setText("我要拼单");
                } else {
                    textView.setText("发起补拼");
                }
            }
            //mTvShare.setVisibility(View.VISIBLE);
            mTvShare.setText("约店主一起拼");
            buttons.setVisibility(View.GONE);
            progress.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.style_progressbar));
        }

        if (mShopItem.getItemStatu().equals("已下架")) {
            findViewById(R.id.btn_buynow).setClickable(false);
            TextView textView1 = (TextView) findViewById(R.id.btn_buynow_txt);
            textView1.setText("已下架");
            findViewById(R.id.btn_buynow).setBackgroundColor(getResources().getColor(R.color.lightgray));
        }
    }

    /**
     * @description 零售价格式化
     * @created 2014-10-23 下午6:06:40
     * @author ZZB
     */
    private void formatAgentPrice(double retailPrice) {
        String retailPriceStr = "拼货价 ￥" + df.format(retailPrice);
        int dotPos = retailPriceStr.indexOf(".");
        SpannableString spRetailPrice = new SpannableString(retailPriceStr);
        // 设置字体大小（相对值,单位：像素） 参数表示为默认字体大小的多少倍 //0.5f表示默认字体大小的一半
        spRetailPrice.setSpan(new RelativeSizeSpan(2.0f), 5, dotPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置字体前景色
        spRetailPrice.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.orange1)), 3,
                retailPriceStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mTvRetailPrice.setText(spRetailPrice);
    }

    private void onShopItemLoaded(ShopItemModelx item) {

        try {
            mShopItemModel = item;
            mIsSelf = item.getUserId() == SpManager.getUserId(mContext);
            tv_title.setText(item.getName());
            //  initColorSizeMap(item.getProducts());
            if (item.getTags() != null) {
                mGvTags.setVisibility(View.VISIBLE);

                int padding = DisplayUtil.dip2px(this, 8);
                int paddingTB = DisplayUtil.dip2px(this, 2);

                FlowLayout.LayoutParams params2 = new FlowLayout.LayoutParams(padding, padding);

                for (ShopItemModelx.ItemTagModel tagModel : item.getTags()) {
                    TextView textView = new TextView(this);
                    textView.setSingleLine(true);
                    textView.setTextColor(Color.parseColor("#FF0000"));
                    textView.setText(tagModel.getName());
                    textView.setGravity(Gravity.CENTER);
                    textView.setPadding(padding, paddingTB, padding, paddingTB);
                    textView.setBackgroundResource(R.drawable.bg_rect_gray_corner);
                    mGvTags.addView(textView, params2);
                }
            } else {
                mGvTags.setVisibility(View.GONE);
            }

            mTvDesc.setText(ShopItemListModel.getTextHtml(item.getIntroOrName(), mContext, imageGetter));
            mTvDesc.setTag(item.getIntroOrName());

            mTvTitle.setText(ShopItemListModel.getTextHtml(item.getName(), mContext, imageGetter));
            mTvTitle.setTag(item.getName());

            formatAgentPrice(item.getPrice());
            //属性 if (item.getPropertys() != null)
            if (false) {
                List<ShopItemModelx.PropertysBeanX> parePropertys = item.getPropertys();
                if (parePropertys != null && parePropertys.size() > 0) {
                    ll_propertys_tags.setVisibility(View.VISIBLE);
                    for (ShopItemModelx.PropertysBeanX beanX : parePropertys) {
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        LinearLayout pLayout = new LinearLayout(mContext);
                        pLayout.setPadding(0, 10, 0, 10);
                        pLayout.setOrientation(LinearLayout.HORIZONTAL);
                        int sel_id = beanX.getSelectedID();
                        String first_name = beanX.getName();
                        TextView tv = setLeftTextview(first_name);
                        pLayout.addView(tv);
                        if (beanX.getPropertys() != null && beanX.getPropertys().size() > 0) {
                            if (beanX.getPropertys().size() > 3) {
                                if (beanX.getPropertys().subList(0, 3).contains(sel_id)) {
                                    for (int i = 0; i < 3; i++) {
                                        TextView textView = null;
                                        if (beanX.getPropertys().get(i).getID() == sel_id) {
                                            textView = setRightTextview(beanX.getPropertys().get(i).getName(), true);
                                        } else {
                                            textView = setRightTextview(beanX.getPropertys().get(i).getName(), false);
                                        }
                                        if (textView != null)
                                            pLayout.addView(textView);
                                    }
                                } else {
                                    int index = 0;
                                    for (int i = 0; i < beanX.getPropertys().size(); i++) {
                                        if (beanX.getPropertys().get(i).getID() == sel_id) {
                                            index = i;
                                        }
                                    }
                                    List<ShopItemModelx.PropertysBeanX.PropertysBean> list = null;
                                    if (index < beanX.getPropertys().size() - 1) {
                                        list = beanX.getPropertys().subList(index - 1, index + 2);
                                    } else {
                                        list = beanX.getPropertys().subList(index - 2, index + 1);
                                    }
                                    for (ShopItemModelx.PropertysBeanX.PropertysBean bean : list) {
                                        TextView textView = null;
                                        if (bean.getID() == sel_id) {
                                            textView = setRightTextview(bean.getName(), true);
                                        } else {
                                            textView = setRightTextview(bean.getName(), false);
                                        }
                                        if (textView != null)
                                            pLayout.addView(textView);
                                    }
                                }
                            } else {

                                for (ShopItemModelx.PropertysBeanX.PropertysBean bean : beanX.getPropertys()) {
                                    TextView textView = null;
                                    if (bean.getID() == sel_id) {
                                        textView = setRightTextview(bean.getName(), true);
                                    } else {
                                        textView = setRightTextview(bean.getName(), false);
                                    }
                                    if (textView != null)
                                        pLayout.addView(textView);
                                }
                                if (beanX.getPropertys().size() == 2) {
                                    pLayout.addView(setEmptyView());
                                }
                                if (beanX.getPropertys().size() == 1) {
                                    pLayout.addView(setEmptyView());
                                    pLayout.addView(setEmptyView());
                                }
                            }
                        }

                        ll_propertys_tags.addView(pLayout);

                    }
                }
            }

            //   formatServicePrice(item.getRetailPrice());
            //商品详情
            //populateWebView(item.getDescriptionFull());
            String viewosString = "";
            String firtVideo = "";
            if (item.getVideos() != null) {
                if (item.getVideos().size() > 0) {
                    current_flag = true;
                    vList.clear();
                    result.clear();
                    lv_videos.setVisibility(View.VISIBLE);
                    for (String s : item.getVideos()) {
                        if (Collections.frequency(result, s) < 1)
                            result.add(s);
                    }
                    if (result.size() > 4) {
                        for (int i = 0; i < 4; i++) {
                            vList.add(result.get(i));
                        }
                    } else {
                        vList.addAll(result);
                    }
                    firtVideo = vList.get(0);
                    adapter = new VideoAdapter(this, vList, lv_videos);
                    lv_videos.setAdapter(adapter);
                    setListViewHeightBasedOnChildren(lv_videos);
                    ((PagerScrollView) mRootView).post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            ((PagerScrollView) mRootView).scrollTo(0, 0);
                        }
                    });
                    lv_videos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            // Toast.makeText(ItemDetailsActivity.this, i + "", Toast.LENGTH_SHORT).show();
                            String url = vList.get(i);
                            Intent intent = new Intent(ShopDetailsActivity.this, VideoActivity1.class);
                            intent.putExtra(VideoActivity1.VIEDEO_URL_EXTR, url);
                            intent.putExtra(VideoActivity1.VIEDEO_ISHOW_DOWN_EXTR, CanDownLoadPicAndVideo);
                            startActivity(intent);
                        }
                    });
                } else {
                    lv_videos.setVisibility(View.GONE);
                    current_flag = false;
                }


                //            StringBuilder sb = new StringBuilder();
                //            for (int i = 0; i < vList.size(); i++) {
                //
                //                if (i == 0) {
                //                    firtVideo = vList.get(i);
                //                }
                //                sb.append("<video width=\"100%\" height=\"211\" controls=\"controls\" preload=\"preload\"style=\"background-color: black;\"  > <source src=\" ");
                //                sb.append(vList.get(i));
                //                sb.append("\"type=\"video/mp4\"></video>");
                //                sb.append("<div style=\"height:20px;\"></div>");
                //            }
                //            viewosString = sb.toString();
            } else {
                lv_videos.setVisibility(View.GONE);
                current_flag = false;
            }
//        final List<String> sList = new ArrayList<>();
//        sList.add("http://nahuo-img-server.b0.upaiyun.com//33306/item/1493014819.mp4");
//        sList.add("http://nahuo-img-server.b0.upaiyun.com//33306/item/1493014819.mp4");
//        sList.add("http://nahuo-img-server.b0.upaiyun.com//33306/item/1493018386.mp4");
//        sList.add("http://nahuo-img-server.b0.upaiyun.com//33306/item/1493014819.mp4");
            String Description = "";
            if (item.getImages() != null) {
                if (item.getImages().length > 0) {
                    StringBuilder imges = new StringBuilder();
                    for (int i = 0; i < item.getImages().length; i++) {
                        if (!TextUtils.isEmpty(item.getImages()[i])) {
                            imges.append("<img src=\"" + ImageUrlExtends.getImageUrl(item.getImages()[i], 21) + "\"/><br/>");
                            if (i < item.getImages().length - 1)
                                imges.append("<div style=\"height:5px;\"></div>");
                        }

                    }
                    Description = imges.toString();
                }
            }

            populateWebView(item.getDescriptionHead(), Description, item.getDescriptionFoot(), viewosString);
            int url_index = 0;
            if (item.getImages() != null && item.getImages().length > 0) {

                urls.clear();
                if (!TextUtils.isEmpty(firtVideo)) {
                    urls.add(firtVideo);
                }
                for (String url : item.getImages()) {
                    Log.i("ItemDetail", "url:" + url);
                    if (!TextUtils.isEmpty(url)) {
                        urls.add(ImageUrlExtends.getImageUrl(url));
                        mBasePicUrls.add(ImageUrlExtends.getImageUrl(url));
                        url_index++;
                        if (url_index > 4) {
                            break;
                        }// 只读取前五张
                    }
                }
                if (urls.size() > 0) {
                    mPagerAdapter.setData(urls);
                    mPagerAdapter.notifyDataSetChanged();
                    mLayoutIndicator.setMaxNum(urls.size());
                }
            }
//        addOrderDetailButton(buttons, mShopItem.getButtons(), ol);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TextView setLeftTextview(String name) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DisplayUtil.dip2px(mContext, 80), LinearLayout.LayoutParams.MATCH_PARENT);
        TextView tv = new TextView(mContext);
        if (!TextUtils.isEmpty(name))
            tv.setText(name + "：");
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(15);
        //tv.setPadding(30, 20, 30, 20);
        tv.setLayoutParams(params);
        tv.setTextColor(Color.parseColor("#717171"));
        return tv;
    }

    private View setEmptyView() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        params.leftMargin = 10;
        params.rightMargin = 10;
        TextView tv = new TextView(mContext);
        tv.setLayoutParams(params);
        return tv;
    }

    /**
     * @description 商品详情webview
     * @created 2014-10-23 下午6:49:44
     * @author ZZB
     */
    private void populateWebView(String descriptionhead, String description, String descriptionfoot, String sViedos) {
        StringBuilder html = new StringBuilder();
        html.append("<html>"
                + "<head>"
                + "<style type=\"text/css\">"
                + ".wp-item-detail-format img,.wp-item-detail-format table{ max-width:100%;width:100%!important;height:auto!important; }"
                + "*{margin:0px; padding:0px;}" + "</style>" + "</head>" + "<body>"
                + "<div class=wp-item-detail-format>");
        // html.append(descriptionhead);
        //html.append(sViedos);
        html.append(description);
        html.append("<div style=\"height:5px;\"></div>");
        html.append(descriptionfoot);
        html.append("</div>" + "</body>" + "</html>");
        wv_item_detail_des.loadData("<html>"
                + "<head>"
                + "<style type=\"text/css\">"
                + ".wp-item-detail-format img,.wp-item-detail-format table{ max-width:100%;width:100%!important;height:auto!important; }"
                + "*{margin:0px; padding:0px;}" + "</style>" + "</head>" + "<body>"
                + "<div class=wp-item-detail-format>" + descriptionhead + "</div>" + "</body>" + "</html>", "text/html; charset=UTF-8", null);
        // mWebView.requestFocus();
//        mWebView.setScrollBarStyle(0);

//        WebChromeClient wvcc = new WebChromeClient();
        WebSettings webSettings = mWebView.getSettings();
////        webSettings.setBuiltInZoomControls(true);
////        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
////        webSettings.setUseWideViewPort(true);
////        webSettings.setLoadWithOverviewMode(true);
//        // webSettings.setJavaScriptEnabled(true);
////        webSettings.setUseWideViewPort(true); // 关键点
////        webSettings.setAllowFileAccess(true); // 允许访问文件
////        webSettings.setSupportZoom(true); // 支持缩放
////        webSettings.setLoadWithOverviewMode(true);
//        webSettings.setLoadsImagesAutomatically(true);
//        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
//        webSettings.setPluginState(WebSettings.PluginState.ON);
//        // webSettings.setDefaultFontSize(50);
//        // webSettings.setTextZoom(200);
//        //webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webSettings.setDefaultTextEncodingName("utf-8");
//        mWebView.setWebChromeClient(wvcc);
//        mWebView.setFocusable(false);
        WebViewClient wvc = new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!mWebView.getSettings().getLoadsImagesAutomatically()) {
                    mWebView.getSettings().setLoadsImagesAutomatically(true);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                mWebView.loadUrl(url);
                return true;
            }
        };
        mWebView.setWebViewClient(wvc);
//        mWebView.setWebChromeClient(new WebChromeClient() {
//
//            /*** 视频播放相关的方法 **/
//
//            @Override
//            public View getVideoLoadingProgressView() {
//                FrameLayout frameLayout = new FrameLayout(ItemDetailsActivity.this);
//                frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
//                return frameLayout;
//            }
//
//            @Override
//            public void onShowCustomView(View view, CustomViewCallback callback) {
//                showCustomView(view, callback);
//
//            }
//
//            @Override
//            public void onHideCustomView() {
//                hideCustomView();
//            }
//        });

        // 加载Web地址
        // mWebView.loadUrl("http://look.appjx.cn/mobile_api.php?mod=news&id=12603");
        mWebView.loadData(html.toString(), "text/html; charset=UTF-8", null);
        //mWebView.setVerticalScrollBarEnabled(false);
    }

    private TextView setRightTextview(String name, boolean is_sel) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        params.leftMargin = 10;
        params.rightMargin = 10;
        TextView tv = new TextView(mContext);
        if (!TextUtils.isEmpty(name))
            tv.setText(name);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(14);
        //tv.setPadding(30, 20, 30, 20);
        tv.setLayoutParams(params);
        if (is_sel) {
            tv.setTextColor(Color.parseColor("#ffffff"));
            tv.setBackgroundColor(Color.parseColor("#e48e8f"));
        } else {
            tv.setTextColor(Color.parseColor("#979696"));
            tv.setBackgroundColor(Color.parseColor("#b3b1b1"));
        }
        return tv;
    }

    private void setListViewHeightBasedOnChildren(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


}
