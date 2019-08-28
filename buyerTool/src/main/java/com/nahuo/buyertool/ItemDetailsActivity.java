package com.nahuo.buyertool;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gprinter.aidl.GpService;
import com.gprinter.io.GpDevice;
import com.gprinter.service.GpPrintService;
import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.Bean.MyStyleBean;
import com.nahuo.buyertool.Bean.SelectBean;
import com.nahuo.buyertool.activity.MyStyleActivity;
import com.nahuo.buyertool.activity.PrintActivity;
import com.nahuo.buyertool.activity.ShopDetailsActivity;
import com.nahuo.buyertool.adapter.DetailProductAdapter;
import com.nahuo.buyertool.adapter.DetailRemarkAdapter;
import com.nahuo.buyertool.adapter.IteamColorPicsAdapter;
import com.nahuo.buyertool.api.BuyerToolAPI;
import com.nahuo.buyertool.api.ModifylistAPI;
import com.nahuo.buyertool.api.ShopSetAPI;
import com.nahuo.buyertool.api.UpdateItemOutSupplyrAPI;
import com.nahuo.buyertool.base.BaseActivty;
import com.nahuo.buyertool.common.FileUtils;
import com.nahuo.buyertool.common.ListUtils;
import com.nahuo.buyertool.common.SpManager;
import com.nahuo.buyertool.controls.DropDownView;
import com.nahuo.buyertool.customview.MyDatePickerDialog;
import com.nahuo.buyertool.dialog.BillingDialog;
import com.nahuo.buyertool.dialog.MyStyleDialog;
import com.nahuo.buyertool.dialog.ReasonChooseDialog;
import com.nahuo.buyertool.eventbus.BusEvent;
import com.nahuo.buyertool.eventbus.EventBusId;
import com.nahuo.buyertool.http.CommonSubscriber;
import com.nahuo.buyertool.http.HttpManager;
import com.nahuo.buyertool.http.RxUtil;
import com.nahuo.buyertool.http.response.PinHuoResponse;
import com.nahuo.buyertool.model.CheckItemOutSupplyBean;
import com.nahuo.buyertool.model.ProductModel;
import com.nahuo.buyertool.model.PurchaseModel;
import com.nahuo.buyertool.model.ShopItemListModel;
import com.nahuo.buyertool.newcode.RefreshBean;
import com.nahuo.buyertool.utils.DateUtls;
import com.nahuo.buyertool.utils.ListviewUtls;
import com.nahuo.library.controls.LightPopDialog;
import com.nahuo.library.controls.LoadingDialog;
import com.nahuo.library.helper.FunctionHelper;
import com.nahuo.library.helper.ImageTools;
import com.nahuo.library.helper.ImageUrlExtends;
import com.nahuo.library.helper.SDCardHelper;
import com.nahuo.library.utils.TimeUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;

import static com.nahuo.buyertool.activity.MyStyleActivity.Style.DOWN_SHELVE_TYPE;
import static com.nahuo.buyertool.activity.MyStyleActivity.Style.ON_SHELVE_TYPE;
import static com.zhy.http.okhttp.log.LoggerInterceptor.TAG;

;

/**
 * @author ZZB
 * @description 商品详情
 * @created 2014-10-17 上午10:38:19
 */

public class ItemDetailsActivity extends BaseActivty implements MyStyleDialog.PopDialogListener, View.OnClickListener, ReasonChooseDialog.PopDialogListener {
    private ItemDetailsActivity Vthis = this;
    public static String downloadDirPath = SDCardHelper.getSDCardRootDirectory()
            + "/buyertool/share";
    private Context mContext = this;
    private LoadingDialog mDialog;
    public static final String EXTRA_ID = "EXTRA_ID";
    public static final String EXTRA_QSID = "EXTRA_QSID";
    public static final String EXTRA_STAT = "EXTRA_STAT";
    private DecimalFormat df = new DecimalFormat("#0.00");
    private TextView mTitle, mText1, mRemarkTv, mText2, mText3, mText4, mBtnGreen, mBtnRed, mBtnRed2, mTotal, mGH_select, mGH_text, mCGD_tv1, mCGD_tv2, mCGD_tv3;
    private ImageView mCover;
    private ListView mSizeColorList, mRemarkList;
    private LinearLayout mWarehouseView;
    private EditText mWarehouse;
    private View mGH_view, mCGD_view;
    private int mId, mQsID, itemID;
    private String ItemOutSupply;
    private LoadingDialog mloadingDialog;
    private ShopItemListModel mShopItem;
    //    private List<ItemRemarkModel> mRemarkItem;
    private ArrayList<String> mBasePicUrls = new ArrayList<String>();
    private CommonListActivity.ListType mType;
    private DetailProductAdapter productAdapter;
    private PurchaseModel selectPurchase;
    private int StorageTimeApplyType = -1;
    private String date;
    private CheckBox checkbox;
    private TextView tv_qu_xiao_pai_dan, tv_source_statuid, tv_xiu_gai_pai_dan, mTextView, tv_que_ren_duan_huo,
            item_detail_red_btn, tv_pai_dan_zhuang_tai, tv_pai_dan_remark, item_detail_print, item_detail_revoke_btn, tv_print_log;

    private int days;
    private Long agentItemId;
    private Spinner item_detail_sizecolor_sp;
    public String backjson;
    public String ModifyStr;
    public Map<Integer, Boolean> isCheck;
    public View mListHeader = null;
    private ScrollView scrollView;

    private int UpdateItemOutSupplyType = -1;
    private boolean ShowCancelKDBtn, ShowApplyRefundBtn, ShowSetExceptionalBtn,ShowCancelExceptionalBtn;
    private String obj[] = {
            "请选择断货方式",
            "已选的本期商品断货",
            "已选的所有期商品断货",
            "所有颜色尺码本期断货",
            "所有颜色尺码全部期数断货"};

    private String colorAndSizeJson = "", add_price = "";
    private int StatusID;
    private String name = "";
    private RecyclerView recycler_pics;
    private IteamColorPicsAdapter adapter;
    private List<ShopItemListModel.ColorPicsBean> colorPicsList = new ArrayList<>();
    private int SourceID,ID;
    private String from;
    private DropDownView eXDropView;
    private TextView tv_set_exceptional_btn, tv_cancel_exceptional_btn,tv_exceptional_msg;
    private String reason = "";
    private View layout_set_exception, layout_cancel_exception;

    @Override
    public void onReasonChooseOnClick(ShopItemListModel.ReasonListBean bean, ReasonChooseDialog.ReasonType type) {
        switch (type) {
            case 取消排单:
                cancelPaiDan(bean);
                break;
            case 修改排单:
                changPaiDandialog(bean);
                break;
            case 断货:
                dialog1(bean);
                break;
            case 延时截止入库:
                SubmitStorageTimeApply(bean);
                break;
        }
    }

    private void cancelPaiDan(ShopItemListModel.ReasonListBean bean) {
        days = 0;
        agentItemId = Long.parseLong(mShopItem.getItemID() + "");
        new Task(Step.UpdateItemScheduleInfo, bean).execute();
        String str = main(mShopItem.getStockStatus().toString(), days + "");
        mTextView.setText(str);
        Log.e("str", "str=" + str);
    }

    private void changPaiDandialog(final ShopItemListModel.ReasonListBean bean) {
        agentItemId = Long.parseLong(mShopItem.getItemID() + "");
        NumberPicker mPicker = new NumberPicker(ItemDetailsActivity.this);
        mPicker.setMinValue(mShopItem.getWaitDaysMinRange());
        mPicker.setMaxValue(mShopItem.getWaitDaysMaxRange());
        mPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mTextView.setVisibility(View.VISIBLE);
                String str = tv_pai_dan_zhuang_tai.getText().toString();
                String regEx = "[^0-9]";
                if (str.contains(regEx)) {
                    str.replace(days + "", regEx);
                }
                mTextView.setText(String.valueOf(newVal));
                days = newVal;
            }
        });
        android.app.AlertDialog mAlertDialog = new android.app.AlertDialog.Builder(ItemDetailsActivity.this)
                .setTitle("").setView(mPicker).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Log.e("修改排单", "days=" + days);
                        new Task(Step.UpdateItemScheduleInfo, bean).execute();
                    }
                }).create();
        mAlertDialog.show();
    }

    @Override
    public void onPopDialogButtonClick(MyStyleActivity.Style action, MyStyleBean bean) {
        new OnOrOffShelfItemsTask(SourceID, DOWN_SHELVE_TYPE).execute();

    }

    protected boolean isDialogShowing() {
        return (mloadingDialog != null && mloadingDialog.isShowing());
    }

    protected void hideDialog() {
        if (isDialogShowing()) {
            mloadingDialog.stop();
        }
    }

    public class OnOrOffShelfItemsTask extends AsyncTask<Object, Void, Object> {
        int SourceID;
        MyStyleActivity.Style action;

        public OnOrOffShelfItemsTask(int SourceID, MyStyleActivity.Style action) {
            this.SourceID = SourceID;
            this.action = action;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (action == ON_SHELVE_TYPE) {
                mloadingDialog.start("上架中...");
            } else {
                mloadingDialog.start("下架中...");
            }

        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                if (SourceID <= 0) {
                    return "error:商品ID为空";
                }
                if (action == ON_SHELVE_TYPE) {
                    ShopSetAPI.onShelfItems(mContext, SourceID + "");
                } else {
                    ShopSetAPI.offShelfItems(mContext, SourceID + "");
                }


            } catch (Exception e) {
                e.printStackTrace();
                return "error:" + e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            hideDialog();
            if (result instanceof String && ((String) result).startsWith("error:")) {
                ViewHub.showLongToast(getApplicationContext(), ((String) result).replace("error:", ""));
            } else {
                if (action == ON_SHELVE_TYPE) {
                    //上架
                } else {
                    //下架
                    tv_source_statuid.setEnabled(false);
                    tv_source_statuid.setTextColor(getResources().getColor(R.color.gray_btn_linght));
                    tv_source_statuid.setBackgroundColor(getResources().getColor(R.color.cannot_click));
                    tv_source_statuid.setText("已下架");
                }

            }
        }
    }

    private enum Step {
        CANCEL_KD, LOAD_ITEM_INFO, NO_SUBMIT_KD, SUBMIT_KD, SUBMIT_RC, SUBMIT_REMARK, SUBMIT_ZH, SUBMIT_TK, SUBMIT_STOCKINFO,
        SAVE_ITEM_PHOTO, UpdateItemScheduleInfo, UpdateItemOutSupply, CheckItemOutSupply, StorageTimeApply;
    }

    private GpService mGpService = null;
    private PrinterServiceConnection conn = null;

    //    private List<ShopItemListModel.ReasonListBean> mWaitDaysReasonList;
//    private List<ShopItemListModel.ReasonListBean> mOutSupplyReasonList;
//    private List<ShopItemListModel.ReasonListBean> mCloseStorageReasonList;
    class PrinterServiceConnection implements ServiceConnection {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("ServiceConnection", "onServiceDisconnected() called");
            mGpService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mGpService = GpService.Stub.asInterface(service);
        }
    }

    private void connection() {
        conn = new PrinterServiceConnection();
        Intent intent = new Intent(this, GpPrintService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        selectPurchase = SpManager.getSELECT_PURCHASE_DATA(mContext);
        mloadingDialog = new LoadingDialog(this);
        Vthis = this;
//        new MyThread().start();
        connection();
        initExtras();
        initView();
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initExtras() {
        Intent intent = getIntent();
        mId = intent.getIntExtra(EXTRA_ID, -1);
        mQsID = intent.getIntExtra(EXTRA_QSID, -1);
        mType = (CommonListActivity.ListType) getIntent().getSerializableExtra("type");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (conn != null) {
            unbindService(conn); // unBindService
        }
    }

    private void loadData() {
        // 加载商品信息
        new Task(Step.LOAD_ITEM_INFO).execute();
//        new Task(Step.LOAD_ITEM_REMARK).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        ShopItemListModel silm = new ShopItemListModel();
//        silm.setCover("upyun:item-img:/33306/item/m1446522536891.jpg");
//        silm.setName("asdasdjaskldjkasd");
//        ProductModel pm = new ProductModel();
//        pm.setColor("颜色");
//        pm.setSize("尺码");
//        pm.setKDQty(123);
//        List<ProductModel> asd = new ArrayList<ProductModel>();
//        asd.add(pm);
//        silm.setColorSize(asd);
//        mShopItem = silm;
//
//        onItemInfoLoaded(mShopItem);
    }

    private void initView() {
        initTitleBar();
        eXDropView = (DropDownView) findViewById(R.id.tv_sort_exception);
        eXDropView.setNUSELETED_SHOW_NAME("选择异常原因");
        eXDropView.setOnItemClickListener(new DropDownView.OnItemClickListener() {
            @Override
            public void onItemClick(SelectBean map, int pos, int realPos, int Type) {
                if (map != null) {
                    reason = map.getName();
                } else {
                    reason = "";
                }
            }
        });
        layout_cancel_exception = findViewById(R.id.layout_cancel_exception);
        layout_set_exception = findViewById(R.id.layout_set_exception);
        tv_set_exceptional_btn = (TextView) findViewById(R.id.tv_set_exceptional_btn);
        tv_set_exceptional_btn.setOnClickListener(this);
        tv_cancel_exceptional_btn = (TextView) findViewById(R.id.tv_cancel_exceptional_btn);
        tv_cancel_exceptional_btn.setOnClickListener(this);
        tv_exceptional_msg= (TextView) findViewById(R.id.tv_exceptional_msg);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        recycler_pics = (RecyclerView) findViewById(R.id.recycler_pics);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recycler_pics.setLayoutManager(manager);
        adapter = new IteamColorPicsAdapter(this, null);
        recycler_pics.setAdapter(adapter);
        mCover = (ImageView) findViewById(R.id.item_cover);
        mCover.setOnClickListener(this);
        findViewById(R.id.layout_head).setOnClickListener(this);
        mTitle = (TextView) findViewById(R.id.item_title);
        mText1 = (TextView) findViewById(R.id.item_text_1);
        mText2 = (TextView) findViewById(R.id.item_text_2);
        mText3 = (TextView) findViewById(R.id.item_text_3);
        mText4 = (TextView) findViewById(R.id.item_text_4);
        mTotal = (TextView) findViewById(R.id.item_detail_total);
        mTotal.setVisibility(View.GONE);
        mSizeColorList = (ListView) findViewById(R.id.item_detail_sizecolor_list);
        mRemarkList = (ListView) findViewById(R.id.item_detail_remark_list);
        mWarehouseView = (LinearLayout) findViewById(R.id.item_detail_warehouse_view);
        mWarehouse = (EditText) findViewById(R.id.item_detail_warehouse);
        mCGD_view = findViewById(R.id.item_detail_cgd_view);
        mCGD_tv1 = (TextView) findViewById(R.id.item_detail_cgd_tv1);
        mCGD_tv2 = (TextView) findViewById(R.id.item_detail_cgd_tv2);
        mCGD_tv3 = (TextView) findViewById(R.id.item_detail_cgd_tv3);
        findViewById(R.id.item_detail_cgd_more).setOnClickListener(this);
        mGH_view = findViewById(R.id.item_detail_gh_view);
        mGH_select = (TextView) findViewById(R.id.item_detail_gh_select);
        mGH_select.setOnClickListener(this);
        mGH_text = (TextView) findViewById(R.id.item_detail_gh_text);
        mRemarkTv = (TextView) findViewById(R.id.item_detail_remarkTv);
        mRemarkTv.setOnClickListener(this);
        mBtnGreen = (TextView) findViewById(R.id.item_detail_green_btn);
        mBtnGreen.setOnClickListener(this);
        mBtnRed = (TextView) findViewById(R.id.item_detail_red_btn);
        mBtnRed.setOnClickListener(this);
        mBtnRed2 = (TextView) findViewById(R.id.item_detail_red_btn2);
        mBtnRed2.setOnClickListener(this);
        tv_qu_xiao_pai_dan = (TextView) findViewById(R.id.tv_qu_xiao_pai_dan);
        tv_source_statuid = (TextView) findViewById(R.id.tv_source_statuid);
        tv_source_statuid.setOnClickListener(this);
        tv_qu_xiao_pai_dan.setOnClickListener(this);
        tv_xiu_gai_pai_dan = (TextView) findViewById(R.id.tv_xiu_gai_pai_dan);
        tv_xiu_gai_pai_dan.setOnClickListener(this);
        mTextView = (TextView) findViewById(R.id.tv_pai_dan_zhuang_tai);
        tv_que_ren_duan_huo = (TextView) findViewById(R.id.tv_que_ren_duan_huo);
        tv_que_ren_duan_huo.setOnClickListener(this);
        tv_pai_dan_zhuang_tai = (TextView) findViewById(R.id.tv_pai_dan_zhuang_tai);
        tv_pai_dan_remark = (TextView) findViewById(R.id.tv_pai_dan_remark);
        item_detail_print = (TextView) findViewById(R.id.item_detail_print);
        tv_print_log = (TextView) findViewById(R.id.tv_print_log);
        item_detail_revoke_btn = (TextView) findViewById(R.id.item_detail_revoke_btn);
        item_detail_revoke_btn.setOnClickListener(this);
        item_detail_print.setOnClickListener(this);
        item_detail_sizecolor_sp = (Spinner) findViewById(R.id.item_detail_sizecolor_sp);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, R.layout.my_simple_spinner_self_item, obj);
        //adapter2.setDropDownViewResource(R.layout.my_simple_spinner_dropdown_item);
        item_detail_sizecolor_sp.setAdapter(adapter2);
        item_detail_sizecolor_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                String str = (String) item_detail_sizecolor_sp.getSelectedItem();
                if (str.contains("已选的本期商品断货")) {
                    UpdateItemOutSupplyType = 1;
                } else if (str.contains("已选的所有期商品断货")) {
                    UpdateItemOutSupplyType = 2;
                } else if (str.contains("所有颜色尺码本期断货")) {
                    UpdateItemOutSupplyType = 3;
                } else if (str.contains("所有颜色尺码全部期数断货")) {
                    UpdateItemOutSupplyType = 4;
                } else if (str.contains("请选择断货方式")) {
                    UpdateItemOutSupplyType = -1;
                } else {
                    UpdateItemOutSupplyType = -1;
                }
                if (productAdapter != null) {
                    productAdapter.setUpdateItemOutSupplyType(UpdateItemOutSupplyType);
                    productAdapter.notifyDataSetChanged();
                }
                ListviewUtls.setListViewHeightBasedOnChildren(mSizeColorList);
                checkBox();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
//        新修改
        intviews();

    }

    String print_log = "";

    private void judePrintLog() {
        long print_time = SpManager.getBlueToolPrintTime(this, mId);
        if (print_time > 0) {
            print_log = DateUtls.compareChatTime(print_time, System.currentTimeMillis());
            if (TextUtils.isEmpty(print_log)) {
                tv_print_log.setVisibility(View.GONE);
            } else {
                tv_print_log.setVisibility(View.VISIBLE);
                tv_print_log.setText(print_log);
            }
        } else {
            tv_print_log.setVisibility(View.GONE);
        }

    }

    private void initTitleBar() {
        Button btnLeft = (Button) findViewById(R.id.titlebar_btnLeft);
        btnLeft.setVisibility(View.VISIBLE);
        btnLeft.setOnClickListener(this);
        switch (mType) {
            case 待开单:
                showRight();
                setTitle("待开单商品");
                from = UpdateItemOutSupplyrAPI.DETAIL_PAID;
                break;
            case 已开单:
                setTitle("已开单商品");
                from = UpdateItemOutSupplyrAPI.DETAIL_BILLING;
                break;
            case 入库单:
                setTitle("入库明细");
                from = UpdateItemOutSupplyrAPI.DETAIL_STORE;
                break;
            case 欠货单:
                showRight();
                setTitle("追货或退款");
                from = UpdateItemOutSupplyrAPI.DETAIL_OWES;
                break;
            case 退款单:
                setTitle("退款明细");
                from = UpdateItemOutSupplyrAPI.DETAIL_REFUND;
                break;
            case 异常单:
                setTitle("异常单");
                from = UpdateItemOutSupplyrAPI.DETAIL_EXCEPTION;
                break;
            default:
                setTitle("");
                from = UpdateItemOutSupplyrAPI.DETAIL_PAID;
                break;
        }
    }

    private void showRight() {
        ImageView btnRight = (ImageView) findViewById(R.id.titlebar_icon_loading);
        btnRight.setImageResource(R.drawable.share);
        btnRight.setVisibility(View.VISIBLE);
        btnRight.setOnClickListener(this);
    }

    private void setTitle(String text) {
        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText(text);
    }

    private void setExceptionalBill() {
        Map<String, Object> params = new HashMap<>();
        params.put("billID", ID);
        params.put("reason", reason);
        addSubscribe(HttpManager.getInstance().getPinHuoNoCacheApi(TAG
        ).setExceptionalBill(params)
                .compose(RxUtil.<PinHuoResponse<Object>>rxSchedulerHelper())
                .compose(RxUtil.<Object>handleResult())
                .subscribeWith(new CommonSubscriber<Object>(Vthis, true, R.string.loading) {
                    @Override
                    public void onNext(Object bean) {
                        super.onNext(bean);
                        showTipsAndFinish("设置成功");
                        EventBus.getDefault().post(BusEvent.getEvent(EventBusId.COMMON_LIST_RELOAD, ""));
                    }


                }));
    }

    private void cancelExceptionalBill() {
        Map<String, Object> params = new HashMap<>();
        params.put("billID", ID);
        // params.put("reason", reason);
        addSubscribe(HttpManager.getInstance().getPinHuoNoCacheApi(TAG
        ).cancelExceptionalBill(params)
                .compose(RxUtil.<PinHuoResponse<Object>>rxSchedulerHelper())
                .compose(RxUtil.<Object>handleResult())
                .subscribeWith(new CommonSubscriber<Object>(Vthis, true, R.string.loading) {
                    @Override
                    public void onNext(Object bean) {
                        super.onNext(bean);
                        showTipsAndFinish("取消成功");
                        EventBus.getDefault().post(BusEvent.getEvent(EventBusId.COMMON_LIST_RELOAD, ""));
                    }


                }));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel_exceptional_btn:
                cancelExceptionalBill();
                break;
            case R.id.tv_set_exceptional_btn:
                setExceptionalBill();
                break;
            case R.id.tv_source_statuid:
                //是否下架
                MyStyleDialog.getInstance(this).setTitle("是否要下架该商品").setAction(DOWN_SHELVE_TYPE).setPositive(this).showDialog();

                break;
            case R.id.item_detail_revoke_btn:
                ViewHub.showLightPopDialog(this, getString(R.string.dialog_title),
                        "是否撤销该单", "取消", "确定", new LightPopDialog.PopDialogListener() {
                            @Override
                            public void onPopDialogButtonClick(int which) {
                                new Task(Step.CANCEL_KD).execute();
                            }
                        });
                break;
            case R.id.item_detail_print:
                try {
                    if (productAdapter == null)
                        return;
                    org.json.JSONArray jsonArray = new org.json.JSONArray();
                    for (ProductModel pm : productAdapter.mList) {
                        org.json.JSONObject jsonObject = new org.json.JSONObject();
                        jsonObject.put("detailid", pm.getDetailID());
                        jsonObject.put("realqty", pm.getDHQty());
                        jsonObject.put("qty", pm.getKDQty());
                        jsonArray.put(jsonObject);
                    }
                    Intent intent = new Intent(this, PrintActivity.class);
                    intent.putExtra(PrintActivity.ETRA_PRINT_ID, mId);
                    intent.putExtra(PrintActivity.ETRA_PRINT, jsonArray.toString());
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.item_detail_purchase_img: {
                ArrayList<String> imgs = new ArrayList<String>();
                String url = ImageUrlExtends.getImageUrl(selectPurchase.getBillPic());
                imgs.add(url);
                Intent intent = new Intent(mContext, PicGalleryActivity.class);
                intent.putExtra(PicGalleryActivity.EXTRA_CUR_POS, 0);
                intent.putStringArrayListExtra(PicGalleryActivity.EXTRA_URLS, imgs);
                startActivity(intent);
                break;
            }
            case R.id.item_detail_purchase_edit: {
                Intent it = new Intent(getApplicationContext(), PurchaseListActivity.class);
                startActivityForResult(it, PurchaseListActivity.SELECT_ORDERED);
                break;
            }
            case R.id.item_detail_cgd_more:
                if (mShopItem != null && mShopItem.getPurchase() != null) {
                    Intent it = new Intent(getApplicationContext(), ItemPreviewActivity.class);
                    it.putExtra("url", "http://wap.admin.nahuo.com/Purchase/Detail?oid=" + mShopItem.getPurchase().getOrderID());
                    it.putExtra("name", "采购单");
                    startActivity(it);
                }
                break;
            case R.id.item_cover:
                Intent intent = new Intent(mContext, PicGalleryActivity.class);
                intent.putExtra(PicGalleryActivity.EXTRA_CUR_POS, 0);
                intent.putStringArrayListExtra(PicGalleryActivity.EXTRA_URLS, mBasePicUrls);
                startActivity(intent);
//                Intent intent = new Intent(mContext, ShopDetailsActivity.class);
//                intent.putExtra(ShopDetailsActivity.EXTRA_ID, itemID);
//                intent.putExtra(ShopDetailsActivity.EXTRA_NAME, name);
//                mContext.startActivity(intent);
                break;
            case R.id.layout_head:
                Intent hintent = new Intent(mContext, ShopDetailsActivity.class);
                hintent.putExtra(ShopDetailsActivity.EXTRA_ID, itemID);
                hintent.putExtra(ShopDetailsActivity.EXTRA_NAME, name);
                mContext.startActivity(hintent);
                break;
            case R.id.titlebar_icon_loading:
                if (mShopItem == null) {
                    ViewHub.showShortToast(mContext, "数据加载中，请稍候再试");
                    return;
                }
                new Task(Step.SAVE_ITEM_PHOTO).execute();
                break;
            case R.id.titlebar_btnLeft:
                finish();
                break;
            case R.id.tv_qu_xiao_pai_dan:
//                int agentItemId, int qsid, int days
                changPaiDan(ReasonChooseDialog.ReasonType.取消排单);

                break;
            case R.id.tv_xiu_gai_pai_dan:
//               新修改 AlertDialog.Builder builder=new AlertDialog.Builder(this);  //先得到构造器
                changPaiDan(ReasonChooseDialog.ReasonType.修改排单);

                break;

            case R.id.item_detail_remarkTv: {
                switch (mType) {
                    case 待开单:
                    case 欠货单:
                    case 已开单:
                    case 异常单:
                        ViewHub.showEditDialog(mContext, "请输入备注", "", new ViewHub.EditDialogListener() {
                            @Override
                            public void onOkClick(DialogInterface dialog, EditText editText) {
                                if (editText.getText().toString().length() <= 0) {
                                    ViewHub.showLongToast(mContext, "请输入备注");
                                    return;
                                }
                                new Task(Step.SUBMIT_REMARK, editText.getText().toString()).execute();
                            }

                            @Override
                            public void onOkClick(EditText editText) {
                                if (editText.getText().toString().length() <= 0) {
                                    ViewHub.showLongToast(mContext, "请输入备注");
                                    return;
                                }
                                new Task(Step.SUBMIT_REMARK, editText.getText().toString()).execute();
                            }
                        });
                        break;
                    case 入库单:
                    case 退款单: {
                        break;
                    }
                }
                break;
            }
            case R.id.item_detail_green_btn:// 绿色按钮点击
            {
                String text = ((TextView) v).getText().toString();
                if (text == "确认开单") {
                    if (selectPurchase == null) {
                        ViewHub.showShortToast(ItemDetailsActivity.this, "请先选择采购单");
                        return;
                    }
                    postOrder(true);
                }
                if (text == "确认入仓") {
                    if (mWarehouse.getText().toString().length() > 0) {
                        ViewHub.showOkDialog(mContext, "提示", "是否确认入仓？", "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new Task(Step.SUBMIT_RC).execute();
                            }
                        });
                    } else {
                        ViewHub.showLongToast(mContext, "请输入仓位");
                    }
                }
                if (text == "确认追货") {
                    ViewHub.showOkDialog(mContext, "提示", "是否确认追货？", "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new Task(Step.SUBMIT_ZH).execute();
                        }
                    });
                }
                if (text.contains("入库超时申请")) {
                    setYanShiRiKu();
                }
                break;
            }
            case R.id.item_detail_red_btn://红色按钮点击
                if (mType == CommonListActivity.ListType.欠货单) {
                    String text = ((TextView) v).getText().toString();
                    if (text == "确认退款") {
                        ViewHub.showOkDialog(mContext, "提示", "是否确认退款？", "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new Task(Step.SUBMIT_TK).execute();
                            }
                        });
                    }
                } else if (mType == CommonListActivity.ListType.待开单) {
                    postOrder(false);//直接开单
                } else if (mType == CommonListActivity.ListType.已开单||mType == CommonListActivity.ListType.异常单) {
                    ViewHub.showLightPopDialog(this, getString(R.string.dialog_title),
                            "是否申请退款？(如改单没付过钱，财务对账是不会真实退钱)", "取消", "确定", new LightPopDialog.PopDialogListener() {
                                @Override
                                public void onPopDialogButtonClick(int which) {
                                    new Task(Step.SUBMIT_TK).execute();
                                }
                            });

                }
                break;
            case R.id.item_detail_red_btn2://红色按钮点击
                postOrder(false);//直接开单
                break;

            case R.id.item_detail_gh_select: {
                final String items[] = {"正常", "断货"};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("选择供货状态");
                final int checkedItem = StockNameToIndex(mShopItem.getStockInfo().getStockStatusStr());
                builder.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mShopItem.getStockInfo().setStockStatusWithName(items[which]);//更改存储的状态
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mGH_select.setText(mShopItem.getStockInfo().getStockStatusStr());
                        if (mShopItem.getStockInfo().getStockStatusStr().equals("排单")) {
                            dialogChangeStockDay();
                        } else {
                            if (checkedItem == StockNameToIndex(mShopItem.getStockInfo().getStockStatusStr()))
                                return;//选择未改变,不向下执行
                            mShopItem.getStockInfo().setStockDays(0);
                            mShopItem.getStockInfo().setStackDate("");
                            loadStockInfoDataView();
                            new Task(Step.SUBMIT_STOCKINFO).execute();
                        }
                    }
                });
                builder.create().show();
                break;
            }
        }
    }


    /**
     * @param needId 开单是否需要采购单id(预先开单不需要id)
     */
    private void postOrder(final boolean needId) {
        //   String str1 = "必须确定要开据的付款采购单有此货物,且数量必须正确,避免多开单!开单后,必须补填采购单,否则货物无法入库!";
        String str = "是否确认开单?";
        int count = 0;
        if (productAdapter != null && !ListUtils.isEmpty(productAdapter.mList)) {
            for (ProductModel pm : productAdapter.mList) {
                count = count + pm.getKDQty();
            }
        }
        String str1 = "确定开单" + count + "件吗";
//        if (selectPurchase != null && selectPurchase.getCode().length() > 0) {
//            str += "\n日期：" + selectPurchase.getDate() + " " + selectPurchase.getTime() + "\n" +
//                    "市场：" + selectPurchase.getMarketName() + "\n" +
//                    "楼层：" + selectPurchase.getFloorName() + "\n" +
//                    "档口：" + selectPurchase.getStallsName() + "\n";
////                    "店主：" + "\n" +
////                    "店铺：" + "\n" +
////                    "数量：" + mShopItem.getPayQty() + "件";
//        }
        if (count <= 0) {
            ViewHub.showShortToast(this, "请选择开单数量");
            return;
        }
        BillingDialog.getInstance(this).setRightStr("确定开单").setLeftStr("取消").setContent(needId ? str : str1).setPositive(new BillingDialog.PopDialogListener() {
            @Override
            public void onPopDialogButtonClick(boolean ischeck, boolean company_isCheck) {
                new Task(needId ? Step.SUBMIT_KD : Step.NO_SUBMIT_KD, ischeck, company_isCheck).execute();
            }
        }).showDialog();
//        ViewHub.showLightPopDialog(this, getString(R.string.dialog_title),
//                needId ? str : str1, "取消", "确定", new LightPopDialog.PopDialogListener() {
//                    @Override
//                    public void onPopDialogButtonClick(int which) {
//
//                        new Task(needId ? Step.SUBMIT_KD : Step.NO_SUBMIT_KD).execute();
//                    }
//                });
//        ViewHub.showOkDialog(mContext, "提示", needId ? str : str1, "确定", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                new Task(needId ? Step.SUBMIT_KD : Step.NO_SUBMIT_KD).execute();
//            }
//        });
    }

    private int StockNameToIndex(String name) {
        if ("正常".endsWith(name)) {
            return 0;
        }
        if ("断货".endsWith(name)) {
            return 1;
        }
        return -1;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PurchaseListActivity.SELECT_ORDERED:
                if (resultCode == PurchaseListActivity.SELECT_ORDERED) {
                    selectPurchase = SpManager.getSELECT_PURCHASE_DATA(mContext);
                    purchaseDataLoaded();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }

    }

    private void dialogChangeStockDay() {
        ViewHub.showEditDialog(mContext, "请输入排单天数", mShopItem.getStockInfo().getStockDays() + "", new ViewHub.EditDialogListener() {
            @Override
            public void onOkClick(DialogInterface dialog, EditText editText) {
                String dayStr = editText.getText().toString();
                if (TextUtils.isEmpty(dayStr)) {
                    ViewHub.setDialogDismissable(dialog, false);
                    ViewHub.setEditError(editText, "请输入排单天数");
                } else {
                    int day = 0;
                    try {
                        day = Integer.parseInt(dayStr);
                        ViewHub.setDialogDismissable(dialog, true);
                        if (day > 0) {
                            mShopItem.getStockInfo().setStockDays(day);
                            if (day == 1) {
                                mShopItem.getStockInfo().setStackDate(day + "天后有货");
                            } else {
                                Date dtDay = FunctionHelper.GetDateTime(day);
                                String strDay = TimeUtils.dateToTimeStamp(dtDay, "yyyy/MM/dd");
                                mShopItem.getStockInfo().setStackDate("排单到" + strDay.substring(5, 10));
                            }
                        } else {
                            mShopItem.getStockInfo().setStockStatusID(0);
                            mShopItem.getStockInfo().setStockDays(0);
                            mShopItem.getStockInfo().setStackDate("");
                        }
                        loadStockInfoDataView();
                        ViewHub.hideKeyboard((Activity) mContext);
                        new Task(Step.SUBMIT_STOCKINFO).execute();
                    } catch (Exception ex) {
                        ViewHub.setDialogDismissable(dialog, false);
                        ViewHub.setEditError(editText, "请输入数字");
                    }
                }
            }

            @Override
            public void onOkClick(EditText editText) {

            }

        });
    }

    private void loadStockInfoDataView() {
        mGH_select.setText(mShopItem.getStockInfo().getStockStatusStr());
        mGH_text.setText(mShopItem.getStockInfo().getStackDate());
    }

    private class Task extends AsyncTask<Object, Void, Object> {
        private Step mStep;
        private String warehouse = "";
        private String remark = "";
        boolean IsStock;
        boolean IsCompanyStock;
        ShopItemListModel.ReasonListBean reasonListBean;

        public Task(Step step) {
            mStep = step;
            warehouse = mWarehouse.getText().toString();
        }

        public Task(Step step, ShopItemListModel.ReasonListBean reasonListBean) {
            mStep = step;
            this.reasonListBean = reasonListBean;
            warehouse = mWarehouse.getText().toString();
        }

        public Task(Step step, boolean IsStock, boolean IsCompanyStock) {
            mStep = step;
            warehouse = mWarehouse.getText().toString();
            this.IsStock = IsStock;
            this.IsCompanyStock = IsCompanyStock;
        }

        public Task(Step step, String rmk) {
            mStep = step;
            warehouse = mWarehouse.getText().toString();
            remark = rmk;
        }

        @Override
        protected void onPreExecute() {
            switch (mStep) {
                case LOAD_ITEM_INFO:
                    mDialog.start("加载数据中");
                    break;
                case SUBMIT_STOCKINFO:
                    mDialog.start("保存排单数据中");
                    break;
                case CANCEL_KD:
                    mDialog.start("提交中");
                    break;
                case SUBMIT_KD:
                    mDialog.start("提交中");
                    break;
                case NO_SUBMIT_KD:
                    mDialog.start("提交中");
                    break;
                case SUBMIT_RC:
                    mDialog.start("提交中");
                    break;
                case SUBMIT_REMARK:
                    mDialog.start("提交中");
                    break;
                case SUBMIT_ZH:
                    mDialog.start("提交中");
                    break;
                case SUBMIT_TK:
                    mDialog.start("提交中");
                    break;
                case SAVE_ITEM_PHOTO:
                    mDialog.start("保存中");
                    break;
                case UpdateItemScheduleInfo:
                    mDialog.start("提交中");
                    break;
                case UpdateItemOutSupply:
                    mDialog.start("提交中");
                    break;
                case StorageTimeApply:
                    mDialog.start("提交中");
                    break;
                case CheckItemOutSupply:
                    mDialog.start("检测中");
                    break;

            }
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                switch (mStep) {

                    case SAVE_ITEM_PHOTO:
                        String url = ImageUrlExtends.getImageUrl(mShopItem.getCover());
                        Bitmap bitmap = Picasso.with(mContext).load(url).get();
                        return bitmap;
                    case LOAD_ITEM_INFO:// 加载商品信息
                        mShopItem = BuyerToolAPI.getInstance().getDetail(mContext, mType, mId, mQsID);
                        StorageTimeApplyType = mShopItem.getStorageTimeApplyType();

                        return mShopItem;
                    case SUBMIT_STOCKINFO:
                        BuyerToolAPI.getInstance().AddStockRecord(mContext, mShopItem.getItemID(), mShopItem.getStockInfo().getStockStatusID(), mShopItem.getStockInfo().getStockDays(), mQsID);
                        return "OK";
                    case SUBMIT_KD:
                        int orderid = -1;
                        if (selectPurchase != null && selectPurchase.getOrderID() >= 0) {
                            orderid = selectPurchase.getID();
                        }
                        BuyerToolAPI.getInstance().submitBilling(mContext, orderid, mId, mQsID, productAdapter.mList, remark, IsStock, IsCompanyStock);
                        break;
                    case NO_SUBMIT_KD:
                        int orderid1 = 0;
                        BuyerToolAPI.getInstance().submitBilling(mContext, orderid1, mId, mQsID, productAdapter.mList, remark, IsStock, IsCompanyStock);
                        break;
                    case CANCEL_KD:
                        BuyerToolAPI.getInstance().cancelKD(mContext, mId);
                        return "OK";
                    case SUBMIT_RC:
                        if (mShopItem.getStatusID() == 2) {
                            BuyerToolAPI.getInstance().submitStore4Owe(mContext, mId, mQsID, productAdapter.mList, warehouse, remark);
                        } else {
                            BuyerToolAPI.getInstance().submitStore(mContext, mId, mQsID, productAdapter.mList, warehouse, remark);
                        }
                        break;
                    case SUBMIT_REMARK:
                        BuyerToolAPI.getInstance().submitRemark(mContext, mId, mQsID, remark);
                        break;
                    case SUBMIT_ZH:
                        BuyerToolAPI.getInstance().submit4Chasing(mContext, mId, mQsID, productAdapter.mList, warehouse, remark);
                        break;
                    case SUBMIT_TK:
                        BuyerToolAPI.getInstance().submit4Refund(mContext, mId, mQsID, productAdapter.mList, warehouse, remark);
                        break;
                    case UpdateItemScheduleInfo:
                        ModifyStr = ModifylistAPI.getInstance().getModify(mContext, agentItemId, mQsID, days, reasonListBean);
                        break;
                    case CheckItemOutSupply:
                        agentItemId = Long.parseLong(mShopItem.getItemID() + "");
//                        Map<String, String> map = productAdapter.getMaps();
                        List<Map<String, String>> mvalue1 = new ArrayList<>();
                        CheckItemOutSupplyBean resultData = null;
                        mvalue1 = productAdapter.getmvalue();
//                        mvalue.add(map);
                        Log.e("mvalue", "mvalue=" + mvalue1);
                        colorAndSizeJson = JSONArray.toJSONString(mvalue1).toString();
                        Log.e("colorAndSizeJson", "colorAndSizeJson=" + colorAndSizeJson);
                        if (colorAndSizeJson.equals("[{}]") || colorAndSizeJson.equals("[]")) {
                            if (UpdateItemOutSupplyType == 3 || UpdateItemOutSupplyType == 4) {
                                resultData = UpdateItemOutSupplyrAPI.getInstance().checkItemOutSupply(mContext,
                                        agentItemId, mQsID, UpdateItemOutSupplyType, colorAndSizeJson + "", reasonListBean, from);
                                //  Log.e("UpdateItemOutSupplyType", "UpdateItemOutSupplyType=" + UpdateItemOutSupplyType);

                            } else {
                                resultData = null;
                            }
                        } else {
                            resultData = UpdateItemOutSupplyrAPI.getInstance().checkItemOutSupply(mContext,
                                    agentItemId, mQsID, UpdateItemOutSupplyType, colorAndSizeJson + "", reasonListBean, from);
                            //  Log.e("ItemOutSupply", "ItemOutSupply=" + ItemOutSupply);
                        }
                        return resultData;
                    case UpdateItemOutSupply:
                        agentItemId = Long.parseLong(mShopItem.getItemID() + "");
//                        Map<String, String> map = productAdapter.getMaps();
                        List<Map<String, String>> mvalue = new ArrayList<>();
                        mvalue = productAdapter.getmvalue();
//                        mvalue.add(map);
                        Log.e("mvalue", "mvalue=" + mvalue);
                        colorAndSizeJson = JSONArray.toJSONString(mvalue).toString();
                        Log.e("colorAndSizeJson", "colorAndSizeJson=" + colorAndSizeJson);
                        if (colorAndSizeJson.equals("[{}]") || colorAndSizeJson.equals("[]")) {
                            if (UpdateItemOutSupplyType == 3 || UpdateItemOutSupplyType == 4) {
                                ItemOutSupply = UpdateItemOutSupplyrAPI.getInstance().getUpdateItemOutSupply(mContext,
                                        agentItemId, mQsID, UpdateItemOutSupplyType, colorAndSizeJson + "", reasonListBean, from);
                                Log.e("UpdateItemOutSupplyType", "UpdateItemOutSupplyType=" + UpdateItemOutSupplyType);

                            } else {
                                ItemOutSupply = "";
                            }
                            break;
                        } else {
                            ItemOutSupply = UpdateItemOutSupplyrAPI.getInstance().getUpdateItemOutSupply(mContext,
                                    agentItemId, mQsID, UpdateItemOutSupplyType, colorAndSizeJson + "", reasonListBean, from);
                            Log.e("ItemOutSupply", "ItemOutSupply=" + ItemOutSupply);
                        }

                        break;
                    case StorageTimeApply:
                        agentItemId = Long.parseLong(mShopItem.getItemID() + "");
                        String time = date;
                        backjson = BuyerToolAPI.getInstance().SubmitStorageTimeApply(mContext, agentItemId, mQsID, time, reasonListBean);
                        break;
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
                ViewHub.showLongToast(mContext, ((String) result).replace("error:", ""));
            } else {
                switch (mStep) {

                    case CANCEL_KD:
                        showTipsAndFinish("撤销完成");
                        int num1 = 0;
                        for (ProductModel item : productAdapter.mList)
                            num1 += item.getKDQty();
                        RefreshBean data1 = new RefreshBean(mId, num1);
                        EventBus.getDefault().post(BusEvent.getEvent(EventBusId.COMMON_LIST_CALCEL_KB, data1));
                        break;
                    case SAVE_ITEM_PHOTO:
                        GenBuyerImageToFile(mShopItem, (Bitmap) result);
                        break;
                    case LOAD_ITEM_INFO:
                        onItemInfoLoaded((ShopItemListModel) result);
                        intviews();
                        break;
                    case SUBMIT_STOCKINFO:
                        ViewHub.showLongToast(mContext, "已修改供货信息");
                        break;
                    case SUBMIT_KD:
                    case NO_SUBMIT_KD:
                        showTipsAndFinish("开单完成");
                        int num = 0;
                        for (ProductModel item : productAdapter.mList)
                            num += item.getKDQty();
                        RefreshBean data = new RefreshBean(mId, num);
                        EventBus.getDefault().post(BusEvent.getEvent(EventBusId.NOTE_POP_F5_DAIKAIDAN, data));
                        break;
                    case SUBMIT_RC:
                        showTipsAndFinish("入仓完成");
                        EventBus.getDefault().post(BusEvent.getEvent(EventBusId.COMMON_LIST_RELOAD, ""));
                        break;
                    case SUBMIT_REMARK:
                        ViewHub.showLongToast(mContext, "已备注，再次进入此单可查看备注");
                        break;
                    case SUBMIT_ZH:
                        showTipsAndFinish("追货完成");
                        EventBus.getDefault().post(BusEvent.getEvent(EventBusId.COMMON_LIST_RELOAD, ""));
                        break;
                    case SUBMIT_TK:
                        showTipsAndFinish("退款完成");
                        EventBus.getDefault().post(BusEvent.getEvent(EventBusId.COMMON_LIST_RELOAD, ""));
                        break;
                    case CheckItemOutSupply:
                        if (result instanceof CheckItemOutSupplyBean) {
                            CheckItemOutSupplyBean resultData = (CheckItemOutSupplyBean) result;
                            if (resultData == null) {
                                Toast.makeText(mContext, "操作失败", Toast.LENGTH_SHORT).show();
                            } else {
                                if (resultData.getType() == 1) {
                                    new Task(Step.UpdateItemOutSupply, reasonListBean).execute();

                                } else if (resultData.getType() == 2) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);  //先得到构造器
                                    builder.setTitle("提示"); //设置标题
                                    builder.setMessage(resultData.getTips()); //设置内容
                                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            new Task(Step.UpdateItemOutSupply, reasonListBean).execute();
                                            dialog.dismiss();
                                            Log.e("子线程", "在上面");

                                        }
                                    });
                                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    //参数都设置完成了，创建并显示出来
                                    builder.create().show();
                                }
                            }
                        }
//                        EventBus.getDefault().post(BusEvent.getEvent(EventBusId.COMMON_LIST_RELOAD, ""));
//                        isCheck = productAdapter.getMap();
//                        productAdapter.initCheck(false);
//                        productAdapter.notifyDataSetChanged();
//                        if (ItemOutSupply.toString().contains("true")) {
//                            Log.e("ItemOutSupply", "ItemOutSupply=" + ItemOutSupply.toString());
//                            Toast.makeText(mContext, "设置成功", Toast.LENGTH_SHORT).show();
//                            loadData();
//                        } else if (ItemOutSupply.contains("false")) {
//                            Log.e("ItemOutSupply", "ItemOutSupply=" + ItemOutSupply.toString());
//                            Toast.makeText(mContext, "设置失败", Toast.LENGTH_SHORT).show();
//                            loadData();
//                        } else if (ItemOutSupply.contains("Message")) {
//                            try {
//                                org.json.JSONObject jsonObject = new org.json.JSONObject(ItemOutSupply);
//                                String mess = jsonObject.optString("Message");
//                                ViewHub.showOkDialog(mContext, "提示", mess, "确定", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                    }
//                                });
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                        } else {
//
//                            Toast.makeText(mContext, "操作失败", Toast.LENGTH_SHORT).show();
//                            loadData();
//                        }
                        break;
                    case UpdateItemOutSupply:
                        EventBus.getDefault().post(BusEvent.getEvent(EventBusId.COMMON_LIST_RELOAD, ""));
                        isCheck = productAdapter.getMap();
                        productAdapter.initCheck(false);
                        productAdapter.notifyDataSetChanged();
                        Log.e("", "");
                        if (ItemOutSupply.toString().contains("true")) {
                            Log.e("ItemOutSupply", "ItemOutSupply=" + ItemOutSupply.toString());
                            Toast.makeText(mContext, "设置成功", Toast.LENGTH_SHORT).show();
                            loadData();
                        } else if (ItemOutSupply.contains("false")) {
                            Log.e("ItemOutSupply", "ItemOutSupply=" + ItemOutSupply.toString());
                            Toast.makeText(mContext, "设置失败", Toast.LENGTH_SHORT).show();
                            loadData();
                        } else if (ItemOutSupply.contains("Message")) {
                            try {
                                org.json.JSONObject jsonObject = new org.json.JSONObject(ItemOutSupply);
                                String mess = jsonObject.optString("Message");
                                ViewHub.showOkDialog(mContext, "提示", mess, "确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {

                            Toast.makeText(mContext, "操作失败", Toast.LENGTH_SHORT).show();
                            loadData();
                        }

                        break;
                    case UpdateItemScheduleInfo:
                        EventBus.getDefault().post(BusEvent.getEvent(EventBusId.COMMON_LIST_RELOAD, ""));
                        if (ModifyStr.contains("true")) {
                            Log.e("修改设置成功", "" + ModifyStr);
                            Toast.makeText(mContext, "修改设置成功", Toast.LENGTH_SHORT).show();
                            loadData();
                        } else {
                            Log.e("修改设置失败", "" + ModifyStr);
                            Toast.makeText(mContext, "修改设置失败", Toast.LENGTH_SHORT).show();
                            loadData();
                        }
                        break;
                    case StorageTimeApply:
                        //EventBus.getDefault().post(BusEvent.getEvent(EventBusId.COMMON_LIST_RELOAD, ""));
                        if (backjson.contains("true")) {
                            mBtnGreen.setText("正在申请");
                            mBtnGreen.setClickable(false);
                        }
                        break;

                }

            }
        }

    }

    private void showTipsAndFinish(String msg) {
        ViewHub.showLongToast(mContext, msg);
        finish();
    }

    private void onItemRemarkLoaded() {
        DetailRemarkAdapter dra = new DetailRemarkAdapter(mContext, mShopItem.getRecord());
        mRemarkList.setAdapter(dra);
    }

    private void onItemInfoLoaded(ShopItemListModel item) {
        if (selectPurchase.getCode().length() <= 0) {
            new LoadFirstPurchaseOrderTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        String url = ImageUrlExtends.getImageUrl(item.getCover());
        mBasePicUrls.add(url);
        Picasso.with(mContext).load(url).placeholder(R.drawable.empty_photo).into(mCover);
        String title = "";
        if (item != null) {
//            mWaitDaysReasonList=item.getWaitDaysReasonList();
//            mOutSupplyReasonList=item.getOutSupplyReasonList();
//            mCloseStorageReasonList=item.getCloseStorageReasonList();
            if (TextUtils.isEmpty(item.getItemRemark())) {
                tv_pai_dan_remark.setVisibility(View.GONE);
            } else {
                tv_pai_dan_remark.setVisibility(View.VISIBLE);
                tv_pai_dan_remark.setText(item.getItemRemark());
            }
           ID = item.getID();
            SourceID = item.getSourceID();
            if (item.getSourceStatuID() == 1) {
                tv_source_statuid.setVisibility(View.VISIBLE);
                tv_source_statuid.setEnabled(true);
                tv_source_statuid.setBackgroundColor(getResources().getColor(R.color.bg_red));
                tv_source_statuid.setTextColor(getResources().getColor(R.color.white));
                tv_source_statuid.setText("下架商品");

            } else if (item.getSourceStatuID() == 2) {
                tv_source_statuid.setVisibility(View.VISIBLE);
                tv_source_statuid.setEnabled(false);
                tv_source_statuid.setTextColor(getResources().getColor(R.color.gray_btn_linght));
                tv_source_statuid.setBackgroundColor(getResources().getColor(R.color.cannot_click));
                tv_source_statuid.setText("已下架");

            } else {
                tv_source_statuid.setVisibility(View.GONE);
            }
            if (!ListUtils.isEmpty(item.getColorPics())) {
                colorPicsList.clear();
                colorPicsList.addAll(item.getColorPics());
                recycler_pics.setVisibility(View.VISIBLE);
                if (adapter != null) {
                    adapter.setList(colorPicsList);
                    adapter.notifyDataSetChanged();
                }

            } else {
                recycler_pics.setVisibility(View.GONE);
            }
            name = item.getName();
            itemID = item.getItemID();
            ShowCancelKDBtn = item.isShowCancelKDBtn();
            ShowApplyRefundBtn = item.ShowApplyRefundBtn;
            ShowSetExceptionalBtn=item.ShowSetExceptionalBtn;
            ShowCancelExceptionalBtn=item.ShowCancelExceptionalBtn;
            if (ShowSetExceptionalBtn){
                layout_set_exception.setVisibility(View.VISIBLE);
            }else {
                layout_set_exception.setVisibility(View.GONE);
            }
            if (ShowCancelExceptionalBtn){
                layout_cancel_exception.setVisibility(View.VISIBLE);
            }else {
                layout_cancel_exception.setVisibility(View.GONE);
            }
            tv_exceptional_msg.setText(item.getExceptionalReason());
            if (eXDropView != null) {
                if (!ListUtils.isEmpty(item.getExceptionalReasonList())) {
                    reason=item.getExceptionalReasonList().get(0);
                    List<SelectBean> eList = new ArrayList<>();
                    for (String e : item.getExceptionalReasonList()) {
                        SelectBean selectBean = new SelectBean();
                        selectBean.setName(e);
                        eList.add(selectBean);
                    }
                    eXDropView.setupDataList(eList);
                    eXDropView.setText(eList.get(0).getName());
                }

            }
            if (ShowCancelKDBtn) {
                item_detail_revoke_btn.setVisibility(View.VISIBLE);
            } else {
                item_detail_revoke_btn.setVisibility(View.GONE);
            }
        }
        if (item.isIsWarnTag()) {
            title = "<b><font color=\"#FF0000\">" + item.getNameTag() + "</font></b>";
        } else {
            title = "<b><font color=\"#150ff4\">" + item.getNameTag() + "</font></b>";
        }
        if (item.getMarkUpValue() > 0) {
            add_price = "<font color=\"#FF0000\">(加价：¥" + item.getMarkUpValue() + ")</font>";
        } else {
            add_price = "";
        }
        mTitle.setText(Html.fromHtml(title + "<font color=\"#586B95\">" + item.getIntroOrName() + "</font>"));

        mGH_select.setText(item.getStockInfo().getStockStatusStr());
        mGH_text.setText(item.getStockInfo().getStackDateFrendy());

        onItemRemarkLoaded();
//      新修改。设置排单
        Schedulingsetting();

        mText4.setText(item.getQsName());
//      新修改。设置listview头部信息
        Headset(item);

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    productAdapter.initCheck(true);
                    productAdapter.notifyDataSetChanged();
                    isCheck = productAdapter.getMap();


                } else {
                    productAdapter.initCheck(false);
                    productAdapter.notifyDataSetChanged();
                    isCheck = productAdapter.getMap();
                }
            }
        });
//        设置确认断货按钮UI展示
        List<Map<Integer, Boolean>> IsOutSupplys = new ArrayList<>();
        for (int i = 0; i < mShopItem.getColorSize().size(); i++) {
            boolean IsOutSupply = mShopItem.getColorSize().get(i).isOutSupply();
            Map<Integer, Boolean> isCheck = new HashMap<Integer, Boolean>();
            isCheck.put(i, IsOutSupply);
            IsOutSupplys.add(isCheck);
        }
        String Str = JSONArray.toJSONString(IsOutSupplys);
        Log.e("是否断货", "Str=" + Str);
        if (Str.contains("false")) {
            tv_que_ren_duan_huo.setClickable(true);
        } else {
            tv_que_ren_duan_huo.setEnabled(false);
            tv_que_ren_duan_huo.setClickable(false);
            tv_que_ren_duan_huo.setBackgroundColor(getResources().getColor(R.color.cannot_click));
        }

        StatusID = item.getStatusID();
        productAdapter = new DetailProductAdapter(mContext, item.getColorSize(), mType, item.getStatusID());
        productAdapter.setUpdateItemOutSupplyType(UpdateItemOutSupplyType);
        productAdapter.setListener(new DetailProductAdapter.OnChangeQtyListener() {
            @Override
            public void OnQtyChange() {
                changeQty();
            }
        });
        mSizeColorList.setAdapter(productAdapter);
        ListviewUtls.setListViewHeightBasedOnChildren(mSizeColorList);
        changeQty();

        loadPurchaseInfo();

        tv_que_ren_duan_huo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UpdateItemOutSupplyType == -1) {
                    ViewHub.showShortToast(ItemDetailsActivity.this, "请选择断货方式");
                    return;
                }
                List<Map<String, String>> mvalue = new ArrayList<>();
                mvalue = productAdapter.getmvalue();
                if (mvalue.size() <= 0 && (UpdateItemOutSupplyType != 3 && UpdateItemOutSupplyType != 4)) {
                    ViewHub.showShortToast(ItemDetailsActivity.this, "请选择要断货的款式");
                    return;
                }
                setDuanHuo();
                isCheck = productAdapter.getMap();
                productAdapter.notifyDataSetChanged();
            }
        });
        if (scrollView != null)
            scrollView.smoothScrollTo(0, 0);
    }

    private void loadPurchaseInfo() {
        String Purchase = JSONObject.toJSONString(mShopItem.getPurchase()).toString();
        if (mShopItem != null && mShopItem.getPurchase() != null) {
        /*
        单号：160302-8F03FFS       状态：待收包
        总金额：¥500.00               已开金额：¥400.00
        市场：广州十三行     楼层：2      档口：A105
        * */
            if (mShopItem.getPurchase().getCode() == null) {
                mCGD_view.setVisibility(View.GONE);
            } else {
                mCGD_tv1.setText("单号：" + mShopItem.getPurchase().getCode() + "       状态：" + mShopItem.getPurchase().getStatusName());
                mCGD_tv2.setText("总金额：¥" + df.format(mShopItem.getPurchase().getTotalMoney()) + "               已开金额：¥" + mShopItem.getPurchase().getKdMoney());
                mCGD_tv3.setText("市场：" + mShopItem.getPurchase().getMarketName() + "     楼层：" + mShopItem.getPurchase().getFloorName() + "      档口：" + mShopItem.getPurchase().getStallsName());
            }
        } else {
        }
    }

    int totalQty = 0;

    private void changeQty() {
        switch (mType) {
            case 待开单: {
                int totalQty = 0;
                for (ProductModel pm : productAdapter.mList) {
                    totalQty += pm.getKDQty();
                }
                mTotal.setText("确认开单总数：" + totalQty);
                mTotal.setVisibility(View.VISIBLE);
                break;
            }
            case  异常单:
            case 已开单: {
                totalQty = 0;
                for (ProductModel pm : productAdapter.mList) {
                    totalQty += pm.getDHQty();
                }
                try {
                    if (StorageTimeApplyType == 1) {
                        if (totalQty > 0 && mGpService.getPrinterConnectStatus(BWApplication.getInstance().PrinterId) == GpDevice.STATE_CONNECTED) {
                            item_detail_print.setBackground(getResources().getDrawable(R.drawable.btn_red_3));
                            item_detail_print.setEnabled(true);
                        } else {
                            item_detail_print.setEnabled(false);
                            item_detail_print.setBackground(getResources().getDrawable(R.drawable.btn_d_gray));
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                mTotal.setVisibility(View.GONE);
                break;
            }
            case 入库单:
                break;
            case 欠货单: {
                int totalQty = 0;
                for (ProductModel pm : productAdapter.mList) {
                    totalQty += pm.getTKQty();
                }
                mTotal.setText("确认追货/退款总数：" + totalQty + "件 ； ￥" + (new DecimalFormat("##0.00").format(mShopItem.getPrice() * totalQty) + "元"));
                mTotal.setVisibility(View.VISIBLE);
                break;
            }
            case 退款单:
                break;
            default:
                setTitle("");
                break;
        }
    }

    private void GenBuyerImageToFile(ShopItemListModel item, Bitmap coverData) {
        List<ProductModel> product = new ArrayList<ProductModel>();
        //排个序
        List<ProductModel> temp_product = productAdapter.mList;
        String color = "";
        while (temp_product.size() > 0) {
            List<ProductModel> temp1_product = new ArrayList<ProductModel>();
            color = temp_product.get(0).getColor();
            //把当前颜色加入到数组中
            for (int i = 0; i < temp_product.size(); i++) {
                if (color.equals(temp_product.get(i).getColor())) {
                    product.add(temp_product.get(i));
                } else {
                    temp1_product.add(temp_product.get(i));
                }
            }
            temp_product = temp1_product;
        }

        int width = 1000;
        int height = 1500;
        int x1 = 50;
        int x2 = 200;
        int x3 = 350;
        int fontsize = 30;
        int ytitle = height - (product.size() + 4) * fontsize;
        int ycolorsize = height - (product.size() - 2) * fontsize;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        //创建视图
        Bitmap newbmp = Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newbmp);
        canvas.drawColor(Color.BLACK);

        //放入背景图
//        mCover.setDrawingCacheEnabled(true);
//        Bitmap bmpCover = Bitmap.createBitmap(mCover.getDrawingCache());
//        mCover.setDrawingCacheEnabled(false);
        Bitmap bmpCover = coverData;
        double scaleHeight =
                Double.valueOf(bmpCover.getWidth()) / Double.valueOf(bmpCover.getHeight()) > Double.valueOf(width) / Double.valueOf(height) ?
                        Double.valueOf(width) / Double.valueOf(bmpCover.getWidth()) :
                        Double.valueOf(height) / Double.valueOf(bmpCover.getHeight());
        Matrix mtx = new Matrix();
        mtx.postScale((float) scaleHeight, (float) scaleHeight);
        canvas.drawBitmap(bmpCover, mtx, null);

        //文字背景
        paint.setColor(Color.argb(60, 0, 0, 0));
        canvas.drawRect(0, ytitle - fontsize, width, height, paint);

        //写文字
        paint.setColor(Color.WHITE);
        paint.setTextSize(fontsize - 2);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        canvas.drawText(item.getIntroOrName(), x1, ytitle + fontsize, paint);

        paint.setColor(Color.WHITE);
        canvas.drawText("颜色", x1, ycolorsize - fontsize * 2, paint);
        paint.setColor(Color.WHITE);
        canvas.drawText("尺码", x2, ycolorsize - fontsize * 2, paint);
        if (mType == CommonListActivity.ListType.待开单) {
            paint.setColor(Color.WHITE);
            canvas.drawText("开单数量", x3, ycolorsize - fontsize * 2, paint);
        } else if (mType == CommonListActivity.ListType.欠货单) {
            paint.setColor(Color.WHITE);
            canvas.drawText("欠货数量", x3, ycolorsize - fontsize * 2, paint);
        }

        int kdCount = 0;
        int oweCount = 0;
        for (int i = 0; i < product.size(); i++) {
            paint.setColor(Color.WHITE);
            canvas.drawText(product.get(i).getColor(), x1, ycolorsize + (i - 1) * fontsize, paint);
            paint.setColor(Color.WHITE);
            canvas.drawText(product.get(i).getSize(), x2, ycolorsize + (i - 1) * fontsize, paint);
            if (mType == CommonListActivity.ListType.待开单) {
                paint.setColor(Color.WHITE);
                canvas.drawText(product.get(i).getKDQty() + "", x3, ycolorsize + (i - 1) * fontsize, paint);
                kdCount += product.get(i).getKDQty();
            } else if (mType == CommonListActivity.ListType.欠货单) {
                paint.setColor(Color.WHITE);
                canvas.drawText(product.get(i).getQHQty() + "", x3, ycolorsize + (i - 1) * fontsize, paint);
                oweCount += product.get(i).getQHQty();
            }
        }
        if (mType == CommonListActivity.ListType.待开单) {
            canvas.drawText("开单总数：" + kdCount + "    单价：¥" + item.getOriPrice(), x1, ytitle + fontsize * 2, paint);
        } else if (mType == CommonListActivity.ListType.欠货单) {
            canvas.drawText("欠货总数：" + oweCount + "    单价：¥" + item.getOriPrice(), x1, ytitle + fontsize * 2, paint);
        }

        try {
            String qrCodeSavePath = downloadDirPath;
            String qrCodeSaveFileName = item.getIntroOrName() + ".jpg";
            qrCodeSavePath += "/" + TimeUtils.getDefaultTimeStampDate() + "/" + qrCodeSaveFileName;
            FileUtils.saveBitmap(qrCodeSavePath, newbmp);
            String bucket = "买手工具-" + TimeUtils.getDefaultTimeStampDate() + "";
            int bucket_id = 111111;
            ImageTools.saveImageExternal(mContext, qrCodeSavePath, 100, qrCodeSaveFileName,
                    bucket, bucket_id, 0, 0);
            ViewHub.showLongToast(mContext, "已保存分享图片至：" + qrCodeSavePath);
        } catch (Exception e) {
            e.printStackTrace();
            ViewHub.showLongToast(mContext, "保存文件出错，错误原因：" + e.getMessage());
        }
    }


    public class LoadFirstPurchaseOrderTask extends AsyncTask<Void, Void, Object> {

        public LoadFirstPurchaseOrderTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object result) {
            purchaseDataLoaded();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

        @Override
        protected Object doInBackground(Void... params) {
            try {
                List<PurchaseModel> result = BuyerToolAPI.getPurchaseList(mContext,
                        "", -1, -1, -1,
                        0, 20, "");
                if (result.size() > 0) {
                    selectPurchase = result.get(0);
                    SpManager.setSELECT_PURCHASE_DATA(mContext, selectPurchase);
                }
                return result;
            } catch (Exception ex) {
                ex.printStackTrace();
                return ex.getMessage() == null ? "未知异常" : ex.getMessage();
            }
        }
    }

    private void purchaseDataLoaded() {
        //选择的采购单数据
        if (selectPurchase != null && selectPurchase.getCode().length() > 0) {
            //findViewById(R.id.item_detail_purchase_v).setVisibility(View.VISIBLE);
            findViewById(R.id.item_detail_purchase_img).setOnClickListener(this);
            findViewById(R.id.item_detail_purchase_edit).setOnClickListener(this);

            ((TextView) findViewById(R.id.item_detail_purchase_tv1)).setText
                    ("单号：" + selectPurchase.getCode() + "       总金额：¥" + df.format(selectPurchase
                            .getTotalMoney()));
            ((TextView) findViewById(R.id.item_detail_purchase_tv2)).setText
                    ("市场：" + selectPurchase.getMarketName() + "     楼层：" + selectPurchase
                            .getFloorName() + "      档口：" + selectPurchase.getStallsName());
        }
    }

    private void setYanShiRiKu() {
        ReasonChooseDialog.getInstance(this).setModel(mShopItem).setType(ReasonChooseDialog.ReasonType.延时截止入库).setPositive(this).showDialog();
    }

    private void SubmitStorageTimeApply(final ShopItemListModel.ReasonListBean bean) {
        Calendar calendar = Calendar.getInstance();
        MyDatePickerDialog datePickerDialog = new MyDatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth + "";
                new Task(Step.StorageTimeApply, bean).execute();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        //  showDialog(SING_CHOICE_DIALOG);
//        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE,"确定",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface arg0, int arg1) {
//                        // TODO Auto-generated method stub
//                        DatePicker mDatePicker = d.getDatePicker();
//                        final Activity activity = getActivity();
//                        if (activity != null) {
//                            setDate(activity, mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth());
//                            updateTimeAndDateDisplay(activity);
//                        }
//                    }
//
//                });
        datePickerDialog.show();
    }

    public void changPaiDan(ReasonChooseDialog.ReasonType type) {
        ReasonChooseDialog.getInstance(this).setModel(mShopItem).setType(type).setPositive(this).showDialog();
    }

    public void setDuanHuo() {
        ReasonChooseDialog.getInstance(this).setModel(mShopItem).setType(ReasonChooseDialog.ReasonType.断货).setPositive(this).showDialog();
    }

    private void dialog1(final ShopItemListModel.ReasonListBean bean) {
        new Task(Step.CheckItemOutSupply, bean).execute();
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器
//        builder.setTitle("提示"); //设置标题
//        builder.setMessage("确认设置断货?"); //设置内容
//        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                new Task(Step.UpdateItemOutSupply, bean).execute();
//
//                dialog.dismiss();
//                Log.e("子线程", "在上面");
//
//            }
//        });
//        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        //参数都设置完成了，创建并显示出来
//        builder.create().show();
    }

    private void intviews() {

        mDialog = new LoadingDialog(this);
//

        Log.e("mType", "mType=" + mType);
        switch (mType) {
            case 待开单:
                mCGD_view.setVisibility(View.GONE);
                mWarehouseView.setVisibility(View.GONE);
                mWarehouse.setEnabled(false);
                mBtnRed.setVisibility(View.GONE);
                mBtnRed2.setVisibility(View.GONE);
                setTitle("待开单商品");
                if (StorageTimeApplyType == 1) {
                    mBtnGreen.setText("确认开单");
                    mBtnGreen.setVisibility(View.GONE);
                    mBtnRed2.setVisibility(View.VISIBLE);
                    // mBtnRed2.setText("无采购单,预先开单");
                    mBtnRed2.setText("确定开单");
                    mBtnRed2.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_red_3));
                } else if (StorageTimeApplyType == 2) {
                    mBtnGreen.setText("正在申请");
                    mBtnRed2.setVisibility(View.GONE);
                    mBtnGreen.setVisibility(View.VISIBLE);
                    mBtnGreen.setClickable(false);
                } else if (StorageTimeApplyType == 3) {
                    mBtnGreen.setText("入库超时申请");
                    mBtnRed2.setVisibility(View.GONE);
                    mBtnGreen.setClickable(true);
                    mBtnGreen.setVisibility(View.VISIBLE);
                }
//                mGH_view.setVisibility(View.VISIBLE);
                break;
            case 已开单:

                mCGD_view.setVisibility(View.VISIBLE);
                mWarehouseView.setVisibility(View.GONE);
                mWarehouse.setEnabled(true);
                mBtnGreen.setVisibility(View.VISIBLE);
                mBtnRed2.setVisibility(View.GONE);
                if (ShowApplyRefundBtn)
                    mBtnRed.setVisibility(View.VISIBLE);
                else
                    mBtnRed.setVisibility(View.GONE);
                if (StorageTimeApplyType == 1) {
                    mBtnGreen.setVisibility(View.GONE);
                    item_detail_print.setVisibility(View.VISIBLE);
                    item_detail_print.setText("打印");
                    judePrintLog();
                } else if (StorageTimeApplyType == 2) {
                    mBtnGreen.setText("正在申请");
                    mBtnGreen.setClickable(false);
                } else if (StorageTimeApplyType == 3) {
                    mBtnGreen.setText("入库超时申请");
                    mBtnGreen.setClickable(true);
                    mBtnGreen.setVisibility(View.VISIBLE);
                }

                mBtnRed.setText("申请退款");
//                mBtnGreen.setVisibility(View.GONE);
//                mBtnRed.setVisibility(View.GONE);
                setTitle("已开单商品");
//                mGH_view.setVisibility(View.VISIBLE);
                break;
            case 异常单:

                mCGD_view.setVisibility(View.VISIBLE);
                mWarehouseView.setVisibility(View.GONE);
                mWarehouse.setEnabled(true);
                mBtnGreen.setVisibility(View.VISIBLE);
                mBtnRed2.setVisibility(View.GONE);
                if (ShowApplyRefundBtn)
                    mBtnRed.setVisibility(View.VISIBLE);
                else
                    mBtnRed.setVisibility(View.GONE);
                if (StorageTimeApplyType == 1) {
                    mBtnGreen.setVisibility(View.GONE);
                    item_detail_print.setVisibility(View.GONE);
                    item_detail_print.setText("打印");
                    judePrintLog();
                } else if (StorageTimeApplyType == 2) {
                    mBtnGreen.setText("正在申请");
                    mBtnGreen.setClickable(false);
                } else if (StorageTimeApplyType == 3) {
                    mBtnGreen.setText("入库超时申请");
                    mBtnGreen.setClickable(true);
                    mBtnGreen.setVisibility(View.VISIBLE);
                }

                mBtnRed.setText("申请退款");
//                mBtnGreen.setVisibility(View.GONE);
//                mBtnRed.setVisibility(View.GONE);
                setTitle("异常单");
//                mGH_view.setVisibility(View.VISIBLE);
                break;
            case 入库单:
                mCGD_view.setVisibility(View.VISIBLE);
                mWarehouseView.setVisibility(View.VISIBLE);
                mWarehouse.setEnabled(false);
                mBtnGreen.setVisibility(View.GONE);
                mBtnRed.setVisibility(View.GONE);
                setTitle("入库明细");
                mBtnRed2.setVisibility(View.GONE);
//                mGH_view.setVisibility(View.VISIBLE);
                break;
            case 欠货单:
                mCGD_view.setVisibility(View.VISIBLE);
                mWarehouseView.setVisibility(View.VISIBLE);
                mBtnRed2.setVisibility(View.GONE);
                mWarehouse.setEnabled(false);
                mBtnGreen.setVisibility(View.VISIBLE);
                if (ShowApplyRefundBtn)
                    mBtnRed.setVisibility(View.VISIBLE);
                else
                    mBtnRed.setVisibility(View.GONE);
                mBtnGreen.setText("确认追货");
                mBtnRed.setText("确认退款");
                setTitle("追货或退款");
//                mGH_view.setVisibility(View.VISIBLE);
                break;
            case 退款单:
                mCGD_view.setVisibility(View.VISIBLE);
                mWarehouseView.setVisibility(View.GONE);
                mWarehouse.setEnabled(false);
                mBtnGreen.setVisibility(View.GONE);
                mBtnRed.setVisibility(View.GONE);
                setTitle("退款明细");
                break;
            default:
                setTitle("");
                break;
        }
    }

    private final int SING_CHOICE_DIALOG = 1;
    String[] str = {"今天", "明天"};

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
            case SING_CHOICE_DIALOG:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("");
                final ChoiceOnClickListener choiceListener =
                        new ChoiceOnClickListener();
                builder.setSingleChoiceItems(str, 0, choiceListener);

                DialogInterface.OnClickListener btnListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {

                                EditText editText = (EditText) findViewById(R.id.et_dialog);
                                int choiceWhich = choiceListener.getWhich();
                                String hobbyStr = str[choiceWhich];
                                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                                Calendar c = Calendar.getInstance();
                                if (hobbyStr.contains("今天")) {
                                    date = sf.format(c.getTime());
                                } else {
                                    c.add(Calendar.DAY_OF_MONTH, 1);
                                    date = sf.format(c.getTime());
                                }
                                new Task(Step.StorageTimeApply).execute();
                            }
                        };
                builder.setPositiveButton("确定", btnListener);
                dialog = builder.create();
                break;
        }
        return dialog;
    }

    private class ChoiceOnClickListener implements DialogInterface.OnClickListener {

        private int which = 0;

        @Override
        public void onClick(DialogInterface dialogInterface, int which) {
            this.which = which;
        }

        public int getWhich() {
            return which;
        }
    }

    public static String main(String str, String day) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        String L = m.replaceAll("" + day).trim();
        return L;
    }

    //    排单设置
    public void Schedulingsetting() {
        if (mShopItem.isCanChangeWaitDays()) {
            Log.e("是否能排单", "" + mShopItem.isCanChangeWaitDays());
            tv_xiu_gai_pai_dan.setClickable(true);
            tv_qu_xiao_pai_dan.setClickable(true);

        } else {
            Log.e("是否能排单", "" + mShopItem.isCanChangeWaitDays());
            tv_xiu_gai_pai_dan.setClickable(false);
            tv_qu_xiao_pai_dan.setClickable(false);
            tv_xiu_gai_pai_dan.setBackgroundColor(getResources().getColor(R.color.cannot_click));
            tv_qu_xiao_pai_dan.setBackgroundColor(getResources().getColor(R.color.cannot_click));
        }
        if (mShopItem.getStockStatus() != null) {
            tv_pai_dan_zhuang_tai.setText("供货状态：" + mShopItem.getStockStatus());
        }
    }

    //    设置listview头部信息
    public void Headset(ShopItemListModel item) {
        switch (mType) {
            case 待开单:
                if (mListHeader == null) {
                    mListHeader = LayoutInflater.from(this).inflate(R.layout.item_item_detail_sizecolor4_work_header_wait, null);
                } else {
                }

                ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_1)).setText("颜色");
                ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_2)).setText("尺码");
                ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_3)).setText("拼货数");
                ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_4)).setText("开单数量");
                checkbox = (CheckBox) mListHeader.findViewById(R.id.item_detail_sizecolor_cb_1);

                mText1.setText(Html.fromHtml("进价：<font color=\"#FF0000\">￥</font>" + item.getOriPrice() + add_price + "&nbsp;&nbsp;&nbsp;&nbsp;总进价：<font color=\"#FF0000\">￥</font>" + (new DecimalFormat("##0.00").format(item.getPayQty() * item.getOriPrice()))));
                mText2.setText(Html.fromHtml("已拼" + item.getPayQty() + "件&nbsp;&nbsp;&nbsp;&nbsp;售价：<font color=\"#FF0000\">￥</font>" + item.getPrice()));
                mText3.setText("拼货时间：" + item.getStartTime());
                mWarehouseView.setVisibility(View.GONE);
                mWarehouse.setEnabled(false);
                mBtnGreen.setVisibility(View.GONE);
                mBtnRed.setVisibility(View.VISIBLE);
                mBtnRed.setText("无采购单,预先开单");
                mBtnGreen.setText("确认开单");
                setTitle("待开单商品");
                //开单数量默认为拼货数量
                for (ProductModel pm : item.getColorSize()) {
                    pm.setKDQty(pm.getPHQty());
                }
                purchaseDataLoaded();
                break;
            case 异常单:
            case 已开单:
                if (mListHeader == null) {
                    mListHeader = LayoutInflater.from(this).inflate(R.layout.item_item_detail_sizecolor4_header, null);
                }
//                mListHeader = LayoutInflater.from(this).inflate(R.layout.item_item_detail_sizecolor4_header, null);

                mWarehouseView.setVisibility(View.GONE);
                mWarehouse.setEnabled(false);
                if (item.getStatusID() == 2) {
                    mText1.setText(Html.fromHtml("进价：<font color=\"#FF0000\">￥</font>" + item.getOriPrice() + add_price + "&nbsp;&nbsp;&nbsp;&nbsp;总进价：<font color=\"#FF0000\">￥</font>" + (new DecimalFormat("##0.00").format(item.getBillingQty() * item.getOriPrice()))));
                    mText2.setText(Html.fromHtml("开单" + item.getBillingQty() + "件&nbsp;&nbsp;&nbsp;&nbsp;售价：<font color=\"#FF0000\">￥</font>" + item.getPrice()));
                    mText3.setText("已欠货：" + item.getOweDays() + "天");
                    mWarehouse.setText(item.getWarehouse() + "");
                    checkbox = (CheckBox) mListHeader.findViewById(R.id.item_detail_sizecolor_cb);
                    ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_1)).setText("颜色");
                    ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_2)).setText("尺码");
                    ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_3)).setText("已入库");
                    ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_4)).setText("欠货");
                    setTitle("欠货入库");

                } else {
                    mText1.setText(Html.fromHtml("进价：<font color=\"#FF0000\">￥</font>" + item.getOriPrice() + add_price + "&nbsp;&nbsp;&nbsp;&nbsp;总进价：<font color=\"#FF0000\">￥</font>" + (new DecimalFormat("##0.00").format(item.getBillingQty() * item.getOriPrice()))));
                    mText2.setText(Html.fromHtml("开单" + item.getBillingQty() + "件&nbsp;&nbsp;&nbsp;&nbsp;售价：<font color=\"#FF0000\">￥</font>" + item.getPrice()));
                    mText3.setText("开单时间：" + item.getBillingTime());
                    mWarehouse.setText(item.getWarehouse() + "");
                    checkbox = (CheckBox) mListHeader.findViewById(R.id.item_detail_sizecolor_cb);
                    ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_1)).setText("颜色");
                    ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_2)).setText("尺码");
                    ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_3)).setText("已拼货");
                    ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_4)).setText("已开单");
                    setTitle("已开单商品");
                }

                break;
            case 入库单:

                if (mListHeader == null) {
                    mListHeader = LayoutInflater.from(this).inflate(R.layout.item_item_detail_sizecolor5_header, null);
                }
//                mListHeader = LayoutInflater.from(this).inflate(R.layout.item_item_detail_sizecolor5_header, null);
                checkbox = (CheckBox) mListHeader.findViewById(R.id.item_detail_sizecolor_cb_1);
                ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_1)).setText("颜色");
                ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_2)).setText("尺码");
                ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_3)).setText("已拼");
                ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_4)).setText("开单");
                ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_5)).setText("入仓");

                mText1.setText(Html.fromHtml("进价：<font color=\"#FF0000\">￥</font>" + item.getOriPrice() + add_price + "&nbsp;&nbsp;&nbsp;&nbsp;总进价：<font color=\"#FF0000\">￥</font>" + (new DecimalFormat("##0.00").format(item.getInvInCount() * item.getOriPrice()))));
                mText2.setText(Html.fromHtml("入库" + item.getInvInCount() + "件&nbsp;&nbsp;&nbsp;&nbsp;售价：<font color=\"#FF0000\">￥</font>" + item.getPrice()));
                mText3.setText("入库时间：" + item.getCreateTime());
                mWarehouseView.setVisibility(View.VISIBLE);
                mWarehouse.setEnabled(false);
                mWarehouse.setText(item.getWarehouse() + "");
                mBtnGreen.setVisibility(View.GONE);
                mBtnRed.setVisibility(View.GONE);
                setTitle("入库明细");
                break;
            case 欠货单:
                if (mListHeader == null) {
                    mListHeader = LayoutInflater.from(this).inflate(R.layout.item_item_detail_sizecolor4_work_header, null);
                }

//                mListHeader = LayoutInflater.from(this).inflate(R.layout.item_item_detail_sizecolor4_work_header, null);
                checkbox = (CheckBox) mListHeader.findViewById(R.id.item_detail_sizecolor_cb_1);
                ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_1)).setText("颜色");
                ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_2)).setText("尺码");
                ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_3)).setText("欠货");
                ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_4)).setText("追货/退款数量");
                ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_stock)).setText("标识");
                mText1.setText(Html.fromHtml("进价：<font color=\"#FF0000\">￥</font>" + item.getOriPrice() + add_price + "&nbsp;&nbsp;&nbsp;&nbsp;总进价：<font color=\"#FF0000\">￥</font>" + (new DecimalFormat("##0.00").format(item.getOweQty() * item.getOriPrice()))));
                mText2.setText(Html.fromHtml("欠货" + item.getOweQty() + "件&nbsp;&nbsp;&nbsp;&nbsp;售价：<font color=\"#FF0000\">￥</font>" + item.getPrice()));
                mText3.setText("欠货：" + item.getOweDays() + "天");
                mWarehouseView.setVisibility(View.VISIBLE);
                mWarehouse.setEnabled(false);
                mWarehouse.setText(item.getWarehouse() + "");
                mBtnGreen.setVisibility(View.VISIBLE);
                mBtnRed.setVisibility(View.VISIBLE);
                mBtnGreen.setText("确认追货");
                mBtnRed.setText("确认退款");
                setTitle("追货或退款");
                break;
            case 退款单:
                if (mListHeader == null) {
                    mListHeader = LayoutInflater.from(this).inflate(R.layout.item_item_detail_sizecolor5_header, null);
                }

//                mListHeader = LayoutInflater.from(this).inflate(R.layout.item_item_detail_sizecolor5_header, null);
                checkbox = (CheckBox) mListHeader.findViewById(R.id.item_detail_sizecolor_cb_1);
                ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_1)).setText("颜色");
                ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_2)).setText("尺码");
                ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_3)).setText("开单");
                ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_4)).setText("欠货");
                ((TextView) mListHeader.findViewById(R.id.item_detail_sizecolor_item_5)).setText("退款数量");

                mText1.setText(Html.fromHtml("进价：<font color=\"#FF0000\">￥</font>" + item.getOriPrice() + add_price + "&nbsp;&nbsp;&nbsp;&nbsp;总进价：<font color=\"#FF0000\">￥</font>" + (new DecimalFormat("##0.00").format(item.getRefundQty() * item.getOriPrice()))));
                mText2.setText(Html.fromHtml("退款" + item.getRefundQty() + "件&nbsp;&nbsp;&nbsp;&nbsp;售价：<font color=\"#FF0000\">￥</font>" + item.getPrice()));
                mText3.setText("退款时间：" + item.getRefundTime());
                mWarehouseView.setVisibility(View.GONE);
                mWarehouse.setEnabled(false);
                mBtnGreen.setVisibility(View.GONE);
                mBtnRed.setVisibility(View.GONE);
                mBtnRed2.setVisibility(View.GONE);
                setTitle("退款明细");
                break;
            default:
                setTitle("");
                break;
        }
        checkBox();
        if (mListHeader != null) {
            mSizeColorList.removeHeaderView(mListHeader);
            mSizeColorList.addHeaderView(mListHeader);
        }
    }

    private void checkBox() {
        if (checkbox != null) {
            if (UpdateItemOutSupplyType == 1 || UpdateItemOutSupplyType == 2) {
                checkbox.setVisibility(View.VISIBLE);
            } else {
                checkbox.setVisibility(View.INVISIBLE);
            }
        }
    }
}
