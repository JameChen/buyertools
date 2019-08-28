package com.nahuo.buyertool.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.RxPermissions;
import com.luck.picture.lib.tools.DebugUtil;
import com.luck.picture.lib.tools.DoubleUtils;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.mabeijianxi.smallvideorecord2.LocalMediaCompress;
import com.mabeijianxi.smallvideorecord2.model.AutoVBRMode;
import com.mabeijianxi.smallvideorecord2.model.BaseMediaBitrateConfig;
import com.mabeijianxi.smallvideorecord2.model.LocalMediaConfig;
import com.mabeijianxi.smallvideorecord2.model.OnlyCompressOverBean;
import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.Bean.AttributeBean;
import com.nahuo.buyertool.Bean.CategoryBean;
import com.nahuo.buyertool.Bean.UploadBean;
import com.nahuo.buyertool.ItemImageViewActivity;
import com.nahuo.buyertool.ViewHub;
import com.nahuo.buyertool.adapter.ExtendPropertyTypeListAdapter;
import com.nahuo.buyertool.adapter.GridImageAdapter;
import com.nahuo.buyertool.adapter.LabelGridItemAdapter;
import com.nahuo.buyertool.adapter.SimpleItemTouchHelperCallback;
import com.nahuo.buyertool.api.ApiHelper;
import com.nahuo.buyertool.api.ShopSetAPI;
import com.nahuo.buyertool.api.UploadItemAPI;
import com.nahuo.buyertool.common.Const;
import com.nahuo.buyertool.common.Constant;
import com.nahuo.buyertool.common.FourListBean;
import com.nahuo.buyertool.common.ListUtils;
import com.nahuo.buyertool.common.MediaStoreUtils;
import com.nahuo.buyertool.common.SpManager;
import com.nahuo.buyertool.common.StringUtils;
import com.nahuo.buyertool.controls.ColorSizeSelectMenu;
import com.nahuo.buyertool.controls.NoScrollView;
import com.nahuo.buyertool.controls.PromptDialog;
import com.nahuo.buyertool.db.ToolUploadDbHelper;
import com.nahuo.buyertool.dialog.AttributeDialog;
import com.nahuo.buyertool.dialog.CategoryChooseDialog;
import com.nahuo.buyertool.dialog.ColorPicsSelectDialog;
import com.nahuo.buyertool.dialog.CommDialog;
import com.nahuo.buyertool.dialog.ExtendPropertyDialog;
import com.nahuo.buyertool.dialog.FourListDialog;
import com.nahuo.buyertool.event.SimpleTextWatcher;
import com.nahuo.buyertool.eventbus.BusEvent;
import com.nahuo.buyertool.eventbus.EventBusId;
import com.nahuo.buyertool.iosdialog.widget.ActionSheetDialog;
import com.nahuo.buyertool.model.ColorModel;
import com.nahuo.buyertool.model.ColorSizeModel;
import com.nahuo.buyertool.model.CustomModel;
import com.nahuo.buyertool.model.ImageViewModel;
import com.nahuo.buyertool.model.ItemShopCategory;
import com.nahuo.buyertool.model.ItemStyle;
import com.nahuo.buyertool.model.LabelItemModel;
import com.nahuo.buyertool.model.LabelModel;
import com.nahuo.buyertool.model.ProductModel;
import com.nahuo.buyertool.model.PublicData;
import com.nahuo.buyertool.model.ShopItemModel;
import com.nahuo.buyertool.model.SizeModel;
import com.nahuo.buyertool.model.TagModel;
import com.nahuo.buyertool.service.UploadItemService;
import com.nahuo.buyertool.service.UploadManager;
import com.nahuo.buyertool.utils.FileUtils;
import com.nahuo.buyertool.utils.ShopCategoryCacheManager;
import com.nahuo.library.controls.LoadingDialog;
import com.nahuo.library.dynamicgrid.DynamicGridView;
import com.nahuo.library.helper.FunctionHelper;
import com.nahuo.library.helper.GsonHelper;
import com.nahuo.library.helper.ImageUrlExtends;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.yalantis.ucrop.UCropMulti;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.nahuo.buyertool.activity.UploadItemActivity.Step.ATTRIBUTE_TAG;
import static com.nahuo.buyertool.activity.UploadItemActivity.Step.CATEGORY_TAGS;
import static com.nahuo.buyertool.activity.UploadItemActivity.Step.EXTEND_PROPERTY_LIST_TAG;
import static com.nahuo.buyertool.activity.UploadItemActivity.Step.SetData;
import static com.yalantis.ucrop.UCropMulti.REQUEST_BUYER_CROP;
import static java.lang.Double.parseDouble;

public class UploadItemActivity extends UploadPictureBaseActivity implements ExtendPropertyDialog.PopDialogListener, ExtendPropertyTypeListAdapter.Listener, FourListDialog.PopDialogListener, OnClickListener, ColorPicsSelectDialog.PopDialogListener, CommDialog.PopDialogListener, CategoryChooseDialog.PopDialogListener, AttributeDialog.PopDialogListener {
    private static final String TAG = "UploadItemActivity";
    public static final String EXTRA_UPLOAD_ITEM_ID = "EXTRA_UPLOAD_ITEM_ID";
    public static final String EXTRA_UPLOADED_ITEM = "EXTRA_UPLOADED_ITEM";                                      // 已保存入数据库商品
    public static final String EXTRA_UPLOAD_ITEM_CREATE_TIME = "EXTRA_UPLOAD_ITEM_CREATE_TIME";
    public static final String COLOR_SIZE_LIST = "com.nahuo.bw.b.UploadItemActivity.colorSizeList";
    public static final String IS_WAIT_ORDER = "com.nahuo.bw.b.UploadItemActivity.IS_WAIT_ORDER";
    public static final String WAIT_DAY = "com.nahuo.bw.b.UploadItemActivity.WAIT_DAY";
    public static final String IMAGE_URL = "com.nahuo.bw.b.UploadItemActivity.image_url";
    // 设置规格数量
    private static final int REQUESTCODE_TAKEPHOTO = 2;                                                          // 拍照
    private static final int REQUESTCODE_FROMALBUM = 3;                                                          // 从手机相册选择
    public static final int REQUESTCODE_ITEM_GROUPS_CHANGED = 5;                                                          // 商品分组改变
    public static final int RESULTCODE_ITEM_GROUPS_CHANGED = 6;
    private static final int REQUEST_SELECT_ITEM_SYS_CATEGORY = 7;                                                          // 选择商品分类
    private static final int REQUEST_SELECT_ITEM_SHOP_CATEGORY = 8;                                                          // 选择本店分类
    private String mItemGroupNames, mItemGroupIds;
    private UploadItemActivity vThis = this;
    private LoadingDialog loadingDialog;
    private Button btnAddImage, btnSpecQty;
    private DynamicGridView gvUploadImage;
    private TextView tvTitle, mTagsTwo, tv_stalls_name;
    private EditText edtPrice, edtRetailPrice, et_into_group, et_single_row, uploaditem_remark;                                                                       // edtItemCat，edtRaisePrice
    private EditText edtDescription, mEtTitle, add_price_edtPrice, add_stall_price_edtPrice, add_discount_edtPrice, add_original_price_edtPrice;
    TextView mEtItemShopCategories, tv_item_shop_attribute;
    private EditText mEtItemGroup, mEtItemCategory, et_item_Summary;
    private Uri mPhotoUri;
    private String mItemCreateTime;
    private boolean mIsItemTop, mIsItemOnSale;
    // 规格数量集合
    private List<ColorSizeModel> mColorSizeList = new ArrayList<ColorSizeModel>();
    private List<TagModel> mTagList = new ArrayList<TagModel>();
    private View mRootView;
    //  private UploadItemDBHelper dbHelper;
    private ToolUploadDbHelper toolUploadDbHelper;
    private ShopItemModel editItem;
    private int editID;
    //  private UploadItemPicGridAdapter mAdapter;
    private List<ImageViewModel> mPicModels = new ArrayList<ImageViewModel>();
    public static final String EXTRA_UPLOAD_PIC_MODELS = "EXTRA_UPLOAD_PIC_MODELS";
    private static final int MAX_IMG_COUNT = 9;
    private ImageViewModel mCurrentClickedViewModel;
    private boolean mIsKeyboardShowing;                                                                             // ,
    private List<String> mEmotionResList;                                                                                // 表情资源id
    private ItemStyle mItemStyle;                                                                                     // 商品分类
    private ArrayList<ItemShopCategory> mSelectedShopCategories;
    private EventBus mEventBus = EventBus.getDefault();                                                                      // 已选择的本店分类
    private List<LabelItemModel> mLabelList = new ArrayList<LabelItemModel>();
    private LabelGridItemAdapter mLabelAdapter;
    private PopupWindowType mPupupWindowType = PopupWindowType.LABEL;
    private LoadDataTask loadDataTask;
    private ColorSizeSelectMenu mDataMenu;
    private DeleteLabelTask deleteLabelTask;
    private AddLabelTask addLabelTask;
    private View ll_add_price, ll_discount_price;
    //修改后的List<LabelModel>
    private ProgressDialog mProgressDialog;
    private List<LabelModel> updateList = new ArrayList<>();
    private TextView et_item_shop_materia, et_item_shop_ager, et_item_shop_style, et_item_shop_season;
    private List<FourListBean> SeasonList = new ArrayList<>();
    private List<FourListBean> StyleList = new ArrayList<>();
    private List<FourListBean> AgeList = new ArrayList<>();
    private List<FourListBean> MaterialList = new ArrayList<>();
    private ExtendPropertyTypeListAdapter propertyTypeListAdapter;
    private List<UploadBean.ExtendPropertyTypeListV2Bean> eList = new ArrayList<>();
    private List<UploadBean.ExtendPropertyTypeListV2Bean> extendHasList = new ArrayList<>();

    /**
     * 退出窗口
     */
    @Override
    public void onPopDialogButtonClick(int ok_cancel, CommDialog.DialogType type) {
        switch (type) {
            case D_EXIT:
                if (ok_cancel == CommDialog.BUTTON_POSITIVIE) {
                    finish();
                }
                break;
            case D_FINISH:
                if (ok_cancel == CommDialog.BUTTON_POSITIVIE) {
                    UploadBean newUploadBean = new UploadBean();
                    UploadBean.StallInfoBean stallInfoBean = new UploadBean.StallInfoBean();
                    stallInfoBean.setName(stall_all_name);
                    stallInfoBean.setStallID(stallID);
                    stallInfoBean.setMarketID(marketID);
                    stallInfoBean.setFloorID(floorID);
                    newUploadBean.setStallInfo(stallInfoBean);
                    Intent intent = new Intent(vThis, UploadItemActivity.class);
                    intent.putExtra(UploadItemActivity.EXTRA_UPLOAD_TYPE, UploadItemActivity.UPLOAD_TYPE);
                    intent.putExtra(UploadItemActivity.EXTRA_UPLOAD_SHOP_ITEM, newUploadBean);
                    startActivity(intent);
                    finish();
                } else if (ok_cancel == CommDialog.BUTTON_NEGATIVE) {
                    finish();
                }
                break;
        }
    }

    @Override
    public void onColorPicsDialogButtonClick(String color) {
        if (TextUtils.isEmpty(color)) {
            if (!ListUtils.isEmpty(picAllList)) {
                for (int i = 0; i < picAllList.size(); i++) {
                    if (mCurrentPos == i) {
                        picAllList.remove(i);
                        if (adapter != null)
                            adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onGetFourDialogButtonClick(FourListBean bean, int type) {
        if (bean != null) {
            switch (type) {
                case Constant.FourList.Type_MaterialList:
                    mMaterId = bean.getID();
                    if (et_item_shop_materia != null)
                        et_item_shop_materia.setText(bean.getName());
                    MaterialList.clear();
                    MaterialList.add(bean);
                    break;
                case Constant.FourList.Type_GetAgeList:
                    mAgeId = bean.getID();
                    if (et_item_shop_ager != null)
                        et_item_shop_ager.setText(bean.getName());
                    AgeList.clear();
                    AgeList.add(bean);
                    break;
                case Constant.FourList.Type_StyleList:
                    mStyleId = bean.getID();
                    if (et_item_shop_style != null)
                        et_item_shop_style.setText(bean.getName());
                    StyleList.clear();
                    StyleList.add(bean);
                    break;
                case Constant.FourList.Type_SeasonList:
                    mSeasonId = bean.getID();
                    if (et_item_shop_season != null)
                        et_item_shop_season.setText(bean.getName());
                    SeasonList.clear();
                    SeasonList.add(bean);
                    break;
            }
        }
    }

    @Override
    public void onParePropItemClick(UploadBean.ExtendPropertyTypeListV2Bean item) {
        //拓展的属性
        edtDescription.setFocusable(false);
        et_single_row.setFocusable(false);
        et_into_group.setFocusable(false);
        edtRetailPrice.setFocusable(false);
        edtPrice.setFocusable(false);
        if (item != null) {
            ExtendPropertyDialog.getInstance(vThis).setPositive(this).setData(item).showDialog();
        }
    }

    @Override
    public void onExtendPropertyDialogButtonClick(UploadBean.ExtendPropertyTypeListV2Bean bean) {
        // 拓展的属性dialog点确定
        ViewHub.KeyBoardCancle(this);
        if (!ListUtils.isEmpty(eList)) {
            for (int h = 0; h < eList.size(); h++) {
                UploadBean.ExtendPropertyTypeListV2Bean ee = eList.get(h);
                StringBuffer sb = new StringBuffer();
                sb.append("");
                StringBuffer sb2 = new StringBuffer();
                sb2.append("");
                boolean isGrouping = ee.isGrouping();
                if (!ListUtils.isEmpty(ee.getExtendPropertyList())) {

                    for (int i = 0; i < ee.getExtendPropertyList().size(); i++) {
                        List<UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean> v2list = ee.getExtendPropertyList().get(i);
                        if (!ListUtils.isEmpty(v2list)) {
                            for (int j = 0; j < v2list.size(); j++) {
                                UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean aa = v2list.get(j);
                                if (aa.isSelect) {
                                    sb.append(aa.getName());
                                    if (!TextUtils.isEmpty(aa.getValue()))
                                        sb.append("(" + aa.getValue() + ")");
                                    sb.append(",");
                                }
                            }
                        }
                        if (!TextUtils.isEmpty(sb.toString())) {
                            if (isGrouping) {
                                sb.insert(0, (i + 1) + "：");
                            }
                            sb2.append(sb.toString());
                        }
                        sb.setLength(0);
                    }

                }
                if (TextUtils.isEmpty(sb2.toString())) {
                    ee.setSelContent("");
                } else {
                    ee.setSelContent(sb2.substring(0, sb2.length() - 1));
                }
            }
//            for (UploadBean.ExtendPropertyTypeListV2Bean ee : eList) {
//                StringBuffer sb = new StringBuffer();
//                sb.append("");
//                for (int i = 0; i < ee.getExtendPropertyList().size(); i++) {
//                    UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean aa = ee.getExtendPropertyList().get(i);
//                    if (aa.isSelect) {
//                        sb.append(aa.getName());
//                        if (!TextUtils.isEmpty(aa.getValue()))
//                            sb.append("(" + aa.getValue() + ")");
//                        sb.append(",");
//                    }
//                }
//                if (TextUtils.isEmpty(sb.toString())) {
//                    ee.setSelContent("");
//                } else {
//                    ee.setSelContent(sb.substring(0, sb.length() - 1));
//                }
//            }
            if (propertyTypeListAdapter != null) {
                propertyTypeListAdapter.notifyDataSetChanged();
            }
        }
    }


    public enum PopupWindowType {
        LABEL
    }

    enum Step {
        GET_TAGS, ADD_TAGS, DELETE_TAGS, CATEGORY_TAGS, ATTRIBUTE_TAG, EXTEND_PROPERTY_LIST_TAG, SetData
    }

    public static final String EXTRA_UPLOAD_EXTEND_ITEM = "EXTRA_UPLOAD_EXTEND_ITEM";
    public static final String EXTRA_UPLOAD_COLORPICS_ITEM = "EXTRA_UPLOAD_COLORPICS_ITEM";
    public static final String EXTRA_UPLOAD_SHOP_ITEM = "EXTRA_UPLOAD_SHOP_ITEM";
    public static final String EXTRA_UPLOAD_TYPE = "EXTRA_UPLOAD_TYPE";
    public static final String EXTRA_UPLOAD_CHANG_TYPE = "EXTRA_UPLOAD_CHANG_TYPE";
    public static final String EXTRA_CHANGE_ERROR_TYPE = "EXTRA_CHANGE_ERROR_TYPE";
    public static final int EDIT_TYPE = 1;
    public static final int UPLOAD_TYPE = 2;
    public static final int CHANG_TYPE = 3;
    public int chang_type = -1;
    private int waitDay;
    private boolean isWaitDay;

    public static final int RESULTCODE_OK = 110;
    private static final int REQUESTCODE_SETSPECQTY = 11;
    public static final int REQUEST_STALL = 12;
    //相册
    RecyclerView recycler_pics, recycler_vidieos, recycler_extend_propert_list;
    private List<LocalMedia> selectList = new ArrayList<>();
    private GridImageAdapter adapter;
    private List<LocalMedia> videoSelectList = new ArrayList<>();
    private List<LocalMedia> picAllList = new ArrayList<>();
    private List<LocalMedia> videoAllList = new ArrayList<>();
    private GridImageAdapter videoAdapter;
    private int maxVidieoSelectNum = 4;
    private int picSelectMaxNum, videoSelectMaxNum;
    private int maxSelectNum = 30;
    public static final int REQUEST_VIDIEO = 200;
    //参数
    int stallID = 0, marketID = 0, floorID = 0, parentID = 0, subID = 0;
    List<Integer> propertyIDS = new ArrayList<>();
    public String stall_all_name = "", p_name = "", s_name = "";
    StringBuffer pro_name = new StringBuffer("");
    Intent intent = null;
    UploadBean uploadBean;
    int Type;
    UploadBean.StallInfoBean stallInfoBean;
    List<UploadBean.ColorPicsBean> colorPicsBeanList = new ArrayList<>();
    List<UploadBean.ColorPicsBean> firstColorPicsBeanList = new ArrayList<>();
    List<String> firstStrList = new ArrayList<>();
    ArrayList<String> mPicsList = new ArrayList<>();
    // List<UploadBean.ColorPicsBean> cPicsList = new ArrayList<>();
    UploadManager uploadManager;
    String Dcim_Path = "";
    private loadDataThread loadDataThread;
    private int mCurrentPos = 0;
    private int videoSelectionMode = PictureConfig.SINGLE;
    private int mMaterId = -1, mAgeId = -1, mStyleId = -1, mSeasonId = -1;
    private NoScrollView nestedScrollView;
   // private List<UploadBean.ExtendPropertyTypeListBean> extendPList = new ArrayList<>();

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);// 设置自定义标题栏
        setContentView(R.layout.activity_uploaditem);
        Dcim_Path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        // getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.layout_titlebar_default);// 更换自定义标题栏布局
        mRootView = findViewById(R.id.uploaditem);
        mTagsTwo = (TextView) findViewById(R.id.uploaditem_btnSpecTag);
        findViewById(R.id.ll_uploaditem_btnSpecTag).setOnClickListener(this);
        nestedScrollView = (NoScrollView) findViewById(R.id.scrollview);
        //mTagsTwo.setOnClickListener(this);
        mEventBus.register(this);
        if (SpManager.getUploadCompressVideo(vThis)) {
            videoSelectionMode = PictureConfig.SINGLE;
        } else {
            videoSelectionMode = PictureConfig.MULTIPLE;
        }
        uploadManager = UploadItemService.getDownloadManager();
        toolUploadDbHelper = ToolUploadDbHelper.getInstance(vThis);
        //  dbHelper = UploadItemDBHelper.getInstance(vThis);
//        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
//
//            @Override
//            public void onGlobalLayout() {
////                boolean isFocused = edtDescription.isFocused();
////                int heightDiff = mRootView.getRootView().getHeight() - mRootView.getHeight();
//
//            }
//        });
        intent = getIntent();
        if (intent != null) {
            Type = intent.getIntExtra(EXTRA_UPLOAD_TYPE, UPLOAD_TYPE);
            chang_type = intent.getIntExtra(EXTRA_UPLOAD_CHANG_TYPE, -1);
            uploadBean = (UploadBean) intent.getSerializableExtra(EXTRA_UPLOAD_SHOP_ITEM);
        }
        if (savedInstanceState != null) {
            uploadBean = (UploadBean) savedInstanceState.getSerializable(EXTRA_UPLOAD_SHOP_ITEM);
        }

        // editID = getIntent().getIntExtra("itemID", 0);
//        if (editID > 0) {// 修改状态
//            editItem = (ShopItemModel) getIntent().getSerializableExtra("itemData");
//            mIsItemOnSale = editItem.isOnSale();
//            mIsItemTop = editItem.isTop();
//            mSelectedShopCategories = editItem.getItemShopCats();
//            waitDay = editItem.getWaitDays();
//            isWaitDay = editItem.isWaitOrder();
//
//            if (editItem.getItemStyle4PC().size() > 0) {
//                CustomModel style = editItem.getItemStyle4PC().get(0);
//                mItemStyle = new ItemStyle();
//                mItemStyle.setId(style.getId());
//                mItemStyle.setName(style.getName());
//                mItemStyle.setParentName(editItem.getItemCat4PC().getParentName());
//                mItemStyle.setParentId(editItem.getItemCat4PC().getParentID());
//            }
//        } else {// 新增
//            mItemGroupIds = SpManager.getUploadNewItemVisibleRanageIds(this);
//            mItemGroupNames = StringUtils.deleteEndStr(SpManager.getUploadNewItemVisibleRangeNames(this), ",");
//            mPicModels = (List<ImageViewModel>) getIntent().getSerializableExtra(EXTRA_UPLOAD_PIC_MODELS);
//            if (mPicModels == null) {
//                mPicModels = new ArrayList<ImageViewModel>();
//            }
//        }
        if (mItemStyle == null) {
            String json = SpManager.getItemSelectedStyle(this);
            if (!TextUtils.isEmpty(json)) {
                mItemStyle = GsonHelper.jsonToObject(json, ItemStyle.class);
            }
        }
        initView();
//        if (editID > 0) {
//            initEditItem();
//        }
        initData();
        if (chang_type == CHANG_TYPE) {
            tvTitle.setText("错误更改");
            // setData();
        } else {
            if (Type == UPLOAD_TYPE) {
                tvTitle.setText("发布新款");
            } else if (Type == EDIT_TYPE) {
                tvTitle.setText("修改款式");
                //  setData();
            }
        }
        setData();
        initRecyView();
        if (mColorSizeList.size() == 0) {
            ColorSizeModel colorsizeModel = new ColorSizeModel();
            // 颜色
            ColorModel colorModel = new ColorModel();
            colorModel.setName("如图");
            colorsizeModel.setColor(colorModel);
            // 尺码
            SizeModel sizeModel = new SizeModel();
            sizeModel.setName("均码");
            colorsizeModel.setSize(sizeModel);
            // 库存
            colorsizeModel.setQty(2000);
            mColorSizeList.add(colorsizeModel);
            UploadBean.ColorPicsBean bean = new UploadBean.ColorPicsBean();
            bean.setColor("如图");
            UploadBean.ColorPicsBean colorPicsBean = new UploadBean.ColorPicsBean();
            colorPicsBean.setUrl("");
            colorPicsBean.setColor("");
            colorPicsBeanList.add(colorPicsBean);
            colorPicsBeanList.add(bean);
            //  cPicsList.add(bean);
        }
        if (savedInstanceState != null) {
            List<UploadBean.ExtendPropertyTypeListV2Bean> list = (List<UploadBean.ExtendPropertyTypeListV2Bean>) savedInstanceState.getSerializable(EXTRA_UPLOAD_EXTEND_ITEM);
            eList.clear();
            eList.addAll(list);
        }
        if (ListUtils.isEmpty(eList)) {
            new Task(EXTEND_PROPERTY_LIST_TAG).execute();
        } else {
            setExtendDapter();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        onSaveData(outState);
    }

    private void onSaveData(Bundle outState) {
        UploadBean newUploadBean = new UploadBean();
        newUploadBean.setType(Type);
        String day = et_single_row.getText().toString();
        if (TextUtils.isEmpty(day))
            day = "0";
        String name = edtDescription.getText().toString().trim();
        String summary = et_item_Summary.getText().toString().trim();
        String price = edtPrice.getText().toString().trim();
        String add_price = add_price_edtPrice.getText().toString();
        String discount = add_discount_edtPrice.getText().toString();
        String dealcount = et_into_group.getText().toString().trim();
        String mark = uploaditem_remark.getText().toString().trim();
        if (TextUtils.isEmpty(add_price))
            add_price = "0.00";
        if (TextUtils.isEmpty(discount))
            discount = "0.00";
        if (TextUtils.isEmpty(dealcount))
            dealcount = "0";
        newUploadBean.setName(name);
        newUploadBean.setSummary(summary);
        newUploadBean.setRemark(mark);
        newUploadBean.setPrice(price);
        newUploadBean.setGroupDealCount(Integer.parseInt(dealcount));
        newUploadBean.setMarkUpValue(parseDouble(add_price));
        newUploadBean.setDiscount(Double.parseDouble(discount));
        UploadBean.StallInfoBean stallInfoBean = new UploadBean.StallInfoBean();
        stallInfoBean.setName(stall_all_name);
        stallInfoBean.setStallID(stallID);
        stallInfoBean.setMarketID(marketID);
        stallInfoBean.setFloorID(floorID);
        newUploadBean.setStallInfo(stallInfoBean);
//        if (adapter!=null){
//          List<LocalMedia> list= adapter.getList();
//            picAllList=list;
//        }
        if (!ListUtils.isEmpty(MaterialList)) {
            newUploadBean.setMaterialList(MaterialList);
        }
        if (!ListUtils.isEmpty(AgeList)) {
            newUploadBean.setAgeList(AgeList);
        }
        if (!ListUtils.isEmpty(StyleList)) {
            newUploadBean.setStyleList(StyleList);
        }
        if (!ListUtils.isEmpty(SeasonList)) {
            newUploadBean.setSeasonList(SeasonList);
        }
        if (!ListUtils.isEmpty(colorPicsBeanList)) {
            newUploadBean.setColorPics(colorPicsBeanList);
        }
        if (!ListUtils.isEmpty(picAllList)) {
            List<UploadBean.MediaBean> list = new ArrayList<>();
            for (int i = 0; i < picAllList.size(); i++) {
                LocalMedia media = picAllList.get(i);
                if (i == 0) {
                    newUploadBean.setCover(media.getPath());
                }
                UploadBean.MediaBean bean = new UploadBean.MediaBean();
                bean.setPath(media.getPath());
                bean.setIs_upload(media.is_upload());
                list.add(bean);
            }
            newUploadBean.setLocal_pics(list);
        }
        if (!ListUtils.isEmpty(videoAllList)) {
            List<UploadBean.MediaBean> list = new ArrayList<>();
            for (LocalMedia media : videoAllList) {
                UploadBean.MediaBean bean = new UploadBean.MediaBean();
                bean.setPath(media.getPath());
                bean.setIs_upload(media.is_upload());
                list.add(bean);
            }
            newUploadBean.setLocal_videos(list);
        }
        UploadBean.CategoryListBean listBean = new UploadBean.CategoryListBean();
        listBean.setID(subID);
        listBean.setParentID(parentID);
        listBean.setName(s_name);
        listBean.setParentName(p_name);
        List<UploadBean.CategoryListBean> categoryListBeen = new ArrayList<>();
        categoryListBeen.add(listBean);
        newUploadBean.setCategoryList(categoryListBeen);
        if (!ListUtils.isEmpty(pListBeanList))
            newUploadBean.setPropertyList(pListBeanList);
        if (!ListUtils.isEmpty(updateList)) {
            List<UploadBean.TagsBean> Tags = new ArrayList<>();
            for (LabelModel LabelModel : updateList) {
                UploadBean.TagsBean tagsBean = new UploadBean.TagsBean();
                tagsBean.setID(LabelModel.getID());
                tagsBean.setName(LabelModel.getName());
                Tags.add(tagsBean);
            }
            newUploadBean.setTags(Tags);
        }
        if (!ListUtils.isEmpty(mColorSizeList)) {
            List<UploadBean.ProductsBean> productsList = new ArrayList<>();
            for (ColorSizeModel colorSizeModel : mColorSizeList) {
                UploadBean.ProductsBean productsBean = new UploadBean.ProductsBean();
                productsBean.setColor(colorSizeModel.getColor().getName());
                productsBean.setSize(colorSizeModel.getSize().getName());
                productsBean.setStock(colorSizeModel.getQty());
                productsList.add(productsBean);
            }
            newUploadBean.setProducts(productsList);
        }
        newUploadBean.setUserId(SpManager.getUserId(this));
        if (chang_type == CHANG_TYPE) {
            if (uploadBean != null) {
                newUploadBean.setCopy(uploadBean.isCopy());
                newUploadBean.setItemID(uploadBean.getItemID());
                if (uploadBean.getSupplyInfo() != null) {
                    UploadBean.SupplyInfoBean supplyInfoBean = new UploadBean.SupplyInfoBean();
                    supplyInfoBean.setDays(Integer.parseInt(day));
                    supplyInfoBean.setTypeID(uploadBean.getSupplyInfo().getTypeID());
                    supplyInfoBean.setUpdateWaitOrderType(uploadBean.getSupplyInfo().getUpdateWaitOrderType());
                    newUploadBean.setSupplyInfo(supplyInfoBean);
                }
            }
            // newUploadBean.setCreat_time(uploadBean.getCreat_time());
//            uploadManager.removeTask(uploadBean.getCreat_time());
//            toolUploadDbHelper.changeUploadItem(newUploadBean);
//            uploadManager.addTask(newUploadBean.getCreat_time(), newUploadBean, null);

        } else {
            if (Type == UPLOAD_TYPE) {
                newUploadBean.setCopy(false);
                newUploadBean.setItemID(0);
                UploadBean.SupplyInfoBean supplyInfoBean = new UploadBean.SupplyInfoBean();
                supplyInfoBean.setDays(Integer.parseInt(day));
                supplyInfoBean.setTypeID(0);
                supplyInfoBean.setUpdateWaitOrderType(1);
                newUploadBean.setSupplyInfo(supplyInfoBean);
                //  newUploadBean.setCreat_time(System.currentTimeMillis() + "");
                //插入数据库
//                toolUploadDbHelper.addUploadItem(newUploadBean);
//                uploadManager.addTask(newUploadBean.getCreat_time(), newUploadBean, null);
            } else if (Type == EDIT_TYPE) {
                if (uploadBean != null) {
                    newUploadBean.setCopy(uploadBean.isCopy());
                    newUploadBean.setItemID(uploadBean.getItemID());
                    if (uploadBean.getSupplyInfo() != null) {
                        UploadBean.SupplyInfoBean supplyInfoBean = new UploadBean.SupplyInfoBean();
                        supplyInfoBean.setDays(Integer.parseInt(day));
                        supplyInfoBean.setTypeID(uploadBean.getSupplyInfo().getTypeID());
                        supplyInfoBean.setUpdateWaitOrderType(uploadBean.getSupplyInfo().getUpdateWaitOrderType());
                        newUploadBean.setSupplyInfo(supplyInfoBean);
                    }
                }
                //  newUploadBean.setCreat_time(System.currentTimeMillis() + "");
                //插入数据库
//                toolUploadDbHelper.addUploadItem(newUploadBean);
//                uploadManager.addTask(newUploadBean.getCreat_time(), newUploadBean, null);
            }

        }
        outState.putSerializable(EXTRA_UPLOAD_SHOP_ITEM, newUploadBean);
        outState.putSerializable(EXTRA_UPLOAD_EXTEND_ITEM, (Serializable) eList);
    }

    private void setData() {
        if (uploadBean != null) {
            add_price_edtPrice.setText(uploadBean.getMarkUpValue() + "");
            if (uploadBean.getDiscount() > 0) {
                add_discount_edtPrice.setText(uploadBean.getDiscount() + "");
            }
            if (!TextUtils.isEmpty(uploadBean.getPrice())) {
                if (Double.parseDouble(uploadBean.getPrice()) > 0) {
                    edtPrice.setText(uploadBean.getPrice());
                    add_stall_price_edtPrice.setText(FunctionHelper.DoubleFormat(
                            Double.parseDouble(uploadBean.getPrice()) - uploadBean.getMarkUpValue()));
                    if (uploadBean.getDiscount() > 0)
                        add_original_price_edtPrice.setText(FunctionHelper.DoubleFormat(Double.parseDouble(uploadBean.getPrice()) / uploadBean.getDiscount() * 10));
                } else {
                    edtPrice.setText("");
                }
            }
            stallInfoBean = uploadBean.getStallInfo();
            extendHasList = uploadBean.getExtendPropertyTypeListV2();
            if (!ListUtils.isEmpty(uploadBean.getColorPics()))
                colorPicsBeanList = uploadBean.getColorPics();
            if (stallInfoBean != null) {
                marketID = stallInfoBean.getMarketID();
                floorID = stallInfoBean.getFloorID();
                stallID = stallInfoBean.getStallID();
                stall_all_name = stallInfoBean.getName();
                if (!TextUtils.isEmpty(stall_all_name)) {
                    tv_stalls_name.setText(stall_all_name);
                }
            }
            if (!ListUtils.isEmpty(uploadBean.getImages())) {
                List<UploadBean.MediaBean> list = new ArrayList<>();
                for (String path : uploadBean.getImages()) {
                    UploadBean.MediaBean bean = new UploadBean.MediaBean();
                    bean.setIs_upload(true);
                    bean.setPath(path);
                    //  bean.setPath(ImageUrlExtends.getImageUrl(path));
                    list.add(bean);
                }
                uploadBean.setLocal_pics(list);
            }
            if (!ListUtils.isEmpty(uploadBean.getVideos())) {
                List<UploadBean.MediaBean> list = new ArrayList<>();
                for (String path : uploadBean.getVideos()) {
                    UploadBean.MediaBean bean = new UploadBean.MediaBean();
                    bean.setIs_upload(true);
                    bean.setPath(path);
                    list.add(bean);
                }
                uploadBean.setLocal_videos(list);
            }
            if (!ListUtils.isEmpty(uploadBean.getMaterialList())) {
                MaterialList = uploadBean.getMaterialList();
                FourListBean fourListBean = uploadBean.getMaterialList().get(0);
                if (fourListBean != null) {
                    et_item_shop_materia.setText(fourListBean.getName());
                    mMaterId = fourListBean.getID();
                }
            }
            if (!ListUtils.isEmpty(uploadBean.getAgeList())) {
                AgeList = uploadBean.getAgeList();
                FourListBean fourListBean = uploadBean.getAgeList().get(0);
                if (fourListBean != null) {
                    et_item_shop_ager.setText(fourListBean.getName());
                    mAgeId = fourListBean.getID();
                }
            }
            if (!ListUtils.isEmpty(uploadBean.getStyleList())) {
                StyleList = uploadBean.getStyleList();
                FourListBean fourListBean = uploadBean.getStyleList().get(0);
                if (fourListBean != null) {
                    et_item_shop_style.setText(fourListBean.getName());
                    mStyleId = fourListBean.getID();
                }
            }
            if (!ListUtils.isEmpty(uploadBean.getSeasonList())) {
                SeasonList = uploadBean.getSeasonList();
                FourListBean fourListBean = uploadBean.getSeasonList().get(0);
                if (fourListBean != null) {
                    et_item_shop_season.setText(fourListBean.getName());
                    mSeasonId = fourListBean.getID();
                }
            }
            if (uploadBean.isCopy()) {
                ll_add_price.setVisibility(View.GONE);
                ll_discount_price.setVisibility(View.GONE);
                findViewById(R.id.line_add_price).setVisibility(View.GONE);
            } else {
                ll_add_price.setVisibility(View.VISIBLE);
                ll_discount_price.setVisibility(View.VISIBLE);
                findViewById(R.id.line_add_price).setVisibility(View.VISIBLE);
            }
            if (uploadBean.getSupplyInfo() != null) {
                int updateWaitOrderType = uploadBean.getSupplyInfo().getUpdateWaitOrderType();

                //1表示可以任意修改,2表示值只能修改为0，3表示不能修改
                if (updateWaitOrderType == 2) {
                    et_single_row.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            String ss = "";
                            if (TextUtils.isEmpty(s)) {
                                ss = "0.0";
                            } else {
                                ss = s.toString();
                            }
                            if (Integer.parseInt(ss) != 0) {
                                ViewHub.showShortToast(vThis, "只能修改为0");
                                et_single_row.setText(uploadBean.getSupplyInfo().getDays() + "");
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                } else if (updateWaitOrderType == 3) {
                    et_single_row.setEnabled(false);
                    et_single_row.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ViewHub.showShortToast(vThis, "不能修改");
                        }
                    });
                }
                et_single_row.setText(uploadBean.getSupplyInfo().getDays() + "");
            }
            if (!ListUtils.isEmpty(uploadBean.getTags())) {
                String tags = "";
                for (UploadBean.TagsBean tagsBean : uploadBean.getTags()) {
                    LabelModel model = new LabelModel();
                    tags += tagsBean.getName() + ",";
                    model.setName(tagsBean.getName());
                    model.setID(tagsBean.getID() + "");
                    updateList.add(model);
                }
                mTagsTwo.setText(tags.substring(0, tags.length() - 1));
            }
            if (!ListUtils.isEmpty(uploadBean.getProducts())) {
                String color = "";
                String size = "";
                //cPicsList.clear();
                firstColorPicsBeanList.clear();
                firstStrList.clear();
                firstColorPicsBeanList.addAll(colorPicsBeanList);
                for (UploadBean.ColorPicsBean xBean : colorPicsBeanList) {
                    firstStrList.add(xBean.getColor());
                }
                for (int i = 0; i < uploadBean.getProducts().size(); i++) {

                    UploadBean.ProductsBean item = uploadBean.getProducts().get(i);
                    if (color.indexOf("," + item.getColor()) == -1) {
                        color += "," + item.getColor();
                        UploadBean.ColorPicsBean bean = new UploadBean.ColorPicsBean();
                        bean.setColor(item.getColor());
                        if (ListUtils.isEmpty(firstColorPicsBeanList)) {
                            if (i == 0) {
                                UploadBean.ColorPicsBean colorPicsBean = new UploadBean.ColorPicsBean();
                                colorPicsBean.setUrl("");
                                colorPicsBean.setColor("");
                                colorPicsBeanList.add(colorPicsBean);
                            }
                            colorPicsBeanList.add(bean);
                        } else {
                            UploadBean.ColorPicsBean colorPicsBean = new UploadBean.ColorPicsBean();
                            colorPicsBean.setUrl("");
                            colorPicsBean.setColor("");
                            for (UploadBean.ColorPicsBean xPicsBean : firstColorPicsBeanList) {
                                if (xPicsBean.getColor().equals("")) {
                                    colorPicsBean.setUrl(xPicsBean.getUrl());
                                }
                            }
                            if (i == 0) {
                                if (!firstStrList.contains(""))
                                    colorPicsBeanList.add(colorPicsBean);
                            }
                            if (!firstStrList.contains(item.getColor())) {
                                UploadBean.ColorPicsBean xPBean = new UploadBean.ColorPicsBean();
                                xPBean.setColor(item.getColor());
                                colorPicsBeanList.add(xPBean);
                                firstStrList.add(item.getColor());
                            }

                        }
                        //cPicsList.add(bean);
                    }
                    if (size.indexOf("," + item.getSize()) == -1) {
                        size += "," + item.getSize();
                    }
                    ColorSizeModel colorsizeModel = new ColorSizeModel();
                    // 颜色
                    ColorModel colorModel = new ColorModel();
                    colorModel.setName(item.getColor());
                    colorsizeModel.setColor(colorModel);
                    // 尺码
                    SizeModel sizeModel = new SizeModel();
                    sizeModel.setName(item.getSize());
                    colorsizeModel.setSize(sizeModel);
                    // 库存
                    colorsizeModel.setQty(item.getStock());
                    mColorSizeList.add(colorsizeModel);
                }
                if (color.length() > 0) {
                    color = color.substring(1);
                }
                if (size.length() > 0) {
                    size = size.substring(1);
                }
                btnSpecQty.setText(color + "/" + size);
            }
            if (!ListUtils.isEmpty(uploadBean.getCategoryList())) {
                for (int i = 0; i < uploadBean.getCategoryList().size(); i++) {
                    parentID = uploadBean.getCategoryList().get(0).getParentID();
                    subID = uploadBean.getCategoryList().get(0).getID();
                    if (!TextUtils.isEmpty(uploadBean.getCategoryList().get(0).getParentName()))
                        p_name = uploadBean.getCategoryList().get(0).getParentName();
                    if (!TextUtils.isEmpty(uploadBean.getCategoryList().get(0).getName()))
                        s_name = uploadBean.getCategoryList().get(0).getName();
                    if (!TextUtils.isEmpty(s_name))
                        mEtItemShopCategories.setText(p_name + "-" + s_name);
                }
            }
            if (!ListUtils.isEmpty(uploadBean.getPropertyList())) {
                propertyIDS.clear();
                pListBeanList = uploadBean.getPropertyList();
                for (int i = 0; i < uploadBean.getPropertyList().size(); i++) {
                    propertyIDS.add(uploadBean.getPropertyList().get(i).getID());
                    if (!TextUtils.isEmpty(uploadBean.getPropertyList().get(i).getName())) {
                        pro_name.append(uploadBean.getPropertyList().get(i).getName());
                        if (i < uploadBean.getPropertyList().size() - 1) {
                            pro_name.append(",");
                        }
                    }

                }
                if (!TextUtils.isEmpty(pro_name.toString()))
                    tv_item_shop_attribute.setText(pro_name.toString());
            }
            if (!TextUtils.isEmpty(uploadBean.getName())) {
                edtDescription.setText(uploadBean.getName());
            }
            if (!TextUtils.isEmpty(uploadBean.getSummary())) {
                et_item_Summary.setText(uploadBean.getSummary());
            }

            et_into_group.setText(uploadBean.getGroupDealCount() + "");
            uploaditem_remark.setText(uploadBean.getRemark());
        }
    }

    private void initRecyView() {
        recycler_extend_propert_list = (RecyclerView) findViewById(R.id.recycler_extend_propert_list);
        recycler_vidieos = (RecyclerView) findViewById(R.id.recycler_vidieos);
        recycler_pics = (RecyclerView) findViewById(R.id.recycler_pics);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(vThis, 4, GridLayoutManager.VERTICAL, false);
        FullyGridLayoutManager manager1 = new FullyGridLayoutManager(vThis, 4, GridLayoutManager.VERTICAL, false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(vThis);
        recycler_extend_propert_list.setLayoutManager(linearLayoutManager);
        recycler_extend_propert_list.setNestedScrollingEnabled(false);
        propertyTypeListAdapter = new ExtendPropertyTypeListAdapter(vThis);
        propertyTypeListAdapter.setListener(this);
        recycler_extend_propert_list.setAdapter(propertyTypeListAdapter);
        recycler_pics.setLayoutManager(manager);
        recycler_pics.setNestedScrollingEnabled(false);
        recycler_vidieos.setLayoutManager(manager1);
        recycler_vidieos.setNestedScrollingEnabled(false);
        picSelectMaxNum = maxSelectNum;
        videoSelectMaxNum = maxVidieoSelectNum;
        if (uploadBean != null) {
            if (!ListUtils.isEmpty(uploadBean.getLocal_pics())) {
                picSelectMaxNum = maxSelectNum - uploadBean.getLocal_pics().size();
                for (UploadBean.MediaBean bean : uploadBean.getLocal_pics()) {
                    LocalMedia media = new LocalMedia();
                    media.setIs_upload(bean.is_upload());
                    media.setPath(bean.getPath());
                    picAllList.add(media);
                }
            }
            if (!ListUtils.isEmpty(uploadBean.getLocal_videos())) {
                videoSelectMaxNum = maxVidieoSelectNum - uploadBean.getLocal_videos().size();
                for (UploadBean.MediaBean bean : uploadBean.getLocal_videos()) {
                    LocalMedia media = new LocalMedia();
                    media.setIs_upload(bean.is_upload());
                    media.setPictureType("video/mp4");
                    media.setPath(bean.getPath());
                    videoAllList.add(media);
                }
            }
        }
        adapter = new GridImageAdapter(vThis, onAddPicClickListener);
        adapter.setView(nestedScrollView, recycler_pics);
        videoAdapter = new GridImageAdapter(vThis, onAddVideoClickListener);
//        LocalMedia media = new LocalMedia();
//        media.setIs_upload(true);
//        media.setPath("http://t12.baidu.com/it/u=729060368,1362183054&fm=170&s=FE1C16C78C5B49D45B2E6BA90300E01A&w=500&h=334&img.JPEG");
//        LocalMedia media1 = new LocalMedia();
//        media1.setPath("http://nahuo-video-server.b0.upaiyun.com//33306/item/1500358415.mp4");
//        media1.setPictureType("video/mp4");
//        media1.setIs_upload(true);
//        picAllList.add(media);
//        videoAllList.add(media1);
        adapter.setList(picAllList);
        adapter.setSelectMax(maxSelectNum);
        videoAdapter.setList(videoAllList);
        videoAdapter.setSelectMax(maxVidieoSelectNum);
        recycler_pics.setAdapter(adapter);
        recycler_vidieos.setAdapter(videoAdapter);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);  //用Callback构造ItemtouchHelper
        touchHelper.attachToRecyclerView(recycler_pics);//调用ItemTouchHelper的attachToRecyclerView方法建立联系
        ItemTouchHelper.Callback callback1 = new SimpleItemTouchHelperCallback(videoAdapter);
        ItemTouchHelper touchHelper2 = new ItemTouchHelper(callback1);  //用Callback构造ItemtouchHelper
        touchHelper2.attachToRecyclerView(recycler_vidieos);//调用ItemTouchHelper的attachToRecyclerView方法建立联系
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final int position, View v) {
                showItemDialog(position);

            }
        });
        videoAdapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (videoAllList.size() > 0) {
                    LocalMedia media = videoAllList.get(position);
                    String pictureType = media.getPictureType();
                    int mediaType = PictureMimeType.pictureToVideo(pictureType);
                    switch (mediaType) {
//                        case 1:
//                            // 预览图片 可自定长按保存路径
//                            //PictureSelector.create(MainActivity.this).externalPicturePreview(position, "/custom_file", selectList);
//                            PictureSelector.create(vThis).externalPicturePreview(position, videoSelectList);
//                            break;
                        case 2:
                            // 预览视频
                            PictureSelector.create(vThis).externalPictureVideo(media.getPath());
                            break;
                        case 3:
                            // 预览音频
                            PictureSelector.create(vThis).externalPictureAudio(media.getPath());
                            break;
                    }
                }
            }
        });
        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    PictureFileUtils.deleteCacheDirFile(vThis);
                } else {
                    ViewHub.showShortToast(vThis, getString(R.string.picture_jurisdiction));
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });

    }

    String fileName = "";

    private void showItemDialog(final int position) {
        ActionSheetDialog dialog = new ActionSheetDialog(vThis)
                .builder()
                .setTitle("图片设置")
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .addSheetItem("查看原图", ActionSheetDialog.SheetItemColor.Blue
                        , new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                //填写事件
                                if (picAllList.size() > 0) {
                                    LocalMedia media = picAllList.get(position);
                                    String pictureType = media.getPictureType();
                                    int mediaType = PictureMimeType.pictureToVideo(pictureType);
                                    switch (mediaType) {
                                        case 1:
                                            // 预览图片 可自定长按保存路径
                                            //PictureSelector.create(MainActivity.this).externalPicturePreview(position, "/custom_file", selectList);
                                            //PictureSelector.create(vThis).externalPicturePreview(position, picAllList);
                                            if (!DoubleUtils.isFastDoubleClick()) {
                                                if (!ListUtils.isEmpty(colorPicsBeanList)) {
                                                    mPicsList.clear();
                                                    for (UploadBean.ColorPicsBean bean : colorPicsBeanList) {
                                                        if (!TextUtils.isEmpty(bean.getColor()))
                                                            mPicsList.add(bean.getColor());
                                                    }
                                                }
                                                Intent intent = new Intent(vThis, PictureExternalPreviewToolActivity.class);
                                                intent.putExtra(PictureConfig.EXTRA_PREVIEW_SELECT_LIST, (Serializable) picAllList);
                                                intent.putStringArrayListExtra(EditImageActivity.EXTRA_PICS_LIST, mPicsList);
                                                intent.putExtra(PictureConfig.EXTRA_POSITION, position);
                                                vThis.startActivityForResult(intent, UCropMulti.REQUEST_BUYER_MULTI_CROP);
                                                vThis.overridePendingTransition(R.anim.a5, 0);
                                            }
                                            break;
                                        case 2:
                                            // 预览视频
                                            PictureSelector.create(vThis).externalPictureVideo(media.getPath());
                                            break;
                                        case 3:
                                            // 预览音频
                                            PictureSelector.create(vThis).externalPictureAudio(media.getPath());
                                            break;
                                    }
                                }
                            }
                        })
                .addSheetItem("编辑", ActionSheetDialog.SheetItemColor.Blue
                        , new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                //填写事件
                                // 是图片和选择压缩并且是多张，调用批量压缩
                                mCurrentPos = position;
                                //ArrayList<String> medias = new ArrayList<>();
                                if (picAllList.size() > 0) {
                                    if (picAllList.get(position).is_upload()) {
                                        try {
                                            if (!ListUtils.isEmpty(colorPicsBeanList)) {
                                                mPicsList.clear();
                                                for (UploadBean.ColorPicsBean bean : colorPicsBeanList) {
                                                    if (!TextUtils.isEmpty(bean.getColor()))
                                                        mPicsList.add(bean.getColor());
                                                }
                                            }

                                            String url = ImageUrlExtends.getImageUrl(picAllList.get(position).getPath(), 21);
                                            if (TextUtils.isEmpty(url)) {
                                                ViewHub.showShortToast(UploadItemActivity.this, "图片地址不能为空");
                                            } else {
                                                try {
                                                    fileName = url.substring(url.lastIndexOf("/"), url.lastIndexOf("!"));
                                                } catch (Exception e) {
                                                    fileName = "/" + System.currentTimeMillis() + ".jpg";
                                                }
                                                File downloadDirectory = new File(Dcim_Path + "/" + Const.IMAGES_CASH_PATH);
                                                if (!downloadDirectory.exists()) {
                                                    downloadDirectory.mkdirs();
                                                }
                                                File cacheFile = new File(Dcim_Path + "/" + Const.IMAGES_CASH_PATH + fileName);
                                                if (cacheFile.exists()) {
//                                medias.add(cacheFile.getPath());
//                                startToolCrop(medias, false, PictureMultiCuttingToolActivity.class);
                                                    startPutCrop(UploadItemActivity.this, cacheFile.getPath(), getCropNewpath(), mPicsList, REQUEST_BUYER_CROP);
                                                } else {
                                                    showPleaseDialog();
                                                    loadDataThread = new loadDataThread(url, 2);
                                                    loadDataThread.start();
                                                }

                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        //圖片
//                    medias.add(images.get(mCurrentPos).getPath());
//                    startToolCrop(medias, images.get(mCurrentPos).is_upload(), PictureMultiCuttingToolActivity.class);
                                        startPutCrop(UploadItemActivity.this, picAllList.get(position).getPath(), getCropNewpath(), mPicsList, REQUEST_BUYER_CROP);
                                    }
                                }
                            }
                        })
                .addSheetItem("设置色卡", ActionSheetDialog.SheetItemColor.Blue
                        , new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                //填写事件
                                if (picAllList.size() > 0) {
                                    mCurrentPos = position;
                                    LocalMedia media = picAllList.get(position);
                                    ColorPicsSelectDialog.getInstance(UploadItemActivity.this)
                                            .setIs_upload(media.is_upload()).setUrl(media.getPath()).setList(colorPicsBeanList).setPositive(UploadItemActivity.this).showDialog();
                                }

                            }
                        });
        dialog.show();
    }

    public void startPutCrop(Activity context, final String editImagePath, final String outputPath, final ArrayList<String> picList, final int requestCode) {
        EditImageActivity.start(context, editImagePath, outputPath, picList, requestCode);
    }

    public String getCropNewpath() {
        File file = new File(Dcim_Path + "/" + Const.IMAGES_CROP_CASH_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        return Dcim_Path + "/" + Const.IMAGES_CROP_CASH_PATH + "/" + System.currentTimeMillis() + ".jpg";
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 200:
                    String path = (String) msg.obj;
                    MediaStoreUtils.scanImageFile(mContext, path);
                    showToast(getString(com.luck.picture.lib.R.string.picture_save_success) + "\n" + path);
                    dismissDialog();
                    break;
                case 100:
                    // ArrayList<String> medias = new ArrayList<>();
                    String path1 = (String) msg.obj;
                    // MediaStoreUtils.scanImageFile(mContext, path1);
                    startPutCrop(UploadItemActivity.this, path1, getCropNewpath(), mPicsList, REQUEST_BUYER_CROP);
//                    medias.add(path1);
//                    startToolCrop(medias, false, PictureMultiCuttingToolActivity.class);
                    dismissDialog();
                    break;
            }
        }
    };

    // 下载图片保存至手机
    public void showLoadingImage(String urlPath) {

        try {
            URL u = new URL(urlPath);
            try {
                fileName = urlPath.substring(urlPath.lastIndexOf("/"), urlPath.lastIndexOf("!"));
            } catch (Exception e) {
                fileName = "/" + System.currentTimeMillis() + ".jpg";
            }
            String path = Dcim_Path + "/" + Const.IMAGES_CASH_PATH + fileName;
//            String path = createDir(PicGalleryActivity.this,
//                    System.currentTimeMillis() + ".png", directory_path);
            byte[] buffer = new byte[1024 * 8];
            int read;
            int ava = 0;
            long start = System.currentTimeMillis();
            BufferedInputStream bin;
            bin = new BufferedInputStream(u.openStream());
            BufferedOutputStream bout = new BufferedOutputStream(
                    new FileOutputStream(path));
            while ((read = bin.read(buffer)) > -1) {
                bout.write(buffer, 0, read);
                ava += read;
                long speed = ava / (System.currentTimeMillis() - start);
                DebugUtil.i("Download: " + ava + " byte(s)"
                        + "    avg speed: " + speed + "  (kb/s)");
            }
            bout.flush();
            bout.close();
            Message message = handler.obtainMessage();
            message.what = 200;
            message.obj = path;
            handler.sendMessage(message);
        } catch (IOException e) {
            ViewHub.showShortToast(UploadItemActivity.this, getString(R.string.picture_save_error) + "\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    // 进度条线程
    public class loadDataThread extends Thread {
        private String path;
        private int type;

        public loadDataThread(String path, int type) {
            super();
            this.path = path;
            this.type = type;
        }

        public void run() {
            try {
                if (type == 1) {
                    //保存图片
                    showLoadingImage(path);
                } else if (type == 2) {
                    showEditLoadingImage(path);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 下载
    public void showEditLoadingImage(String urlPath) {

        try {
            URL u = new URL(urlPath);
            try {
                fileName = urlPath.substring(urlPath.lastIndexOf("/"), urlPath.lastIndexOf("!"));
            } catch (Exception e) {
                fileName = "/" + System.currentTimeMillis() + ".jpg";
            }
            String path = Dcim_Path + "/" + Const.IMAGES_CASH_PATH + fileName;
//            String path = createDir(PicGalleryActivity.this,
//                    System.currentTimeMillis() + ".png", directory_path);
            byte[] buffer = new byte[1024 * 8];
            int read;
            int ava = 0;
            long start = System.currentTimeMillis();
            BufferedInputStream bin;
            bin = new BufferedInputStream(u.openStream());
            BufferedOutputStream bout = new BufferedOutputStream(
                    new FileOutputStream(path));
            while ((read = bin.read(buffer)) > -1) {
                bout.write(buffer, 0, read);
                ava += read;
                long speed = ava / (System.currentTimeMillis() - start);
                DebugUtil.i("Download: " + ava + " byte(s)"
                        + "    avg speed: " + speed + "  (kb/s)");
            }
            bout.flush();
            bout.close();
            Message message = handler.obtainMessage();
            message.what = 100;
            message.obj = path;
            handler.sendMessage(message);
        } catch (IOException e) {
            ViewHub.showShortToast(UploadItemActivity.this, "网络图片获取失败" + "\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * /storage/emulated/0/tencent/MicroMsg/WeiXin/wx_camera_1501476603502.jpg
     * /storage/emulated/0/tencent/MicroMsg/WeiXin/wx_camera_1501476589531.jpg
     * /storage/emulated/0/tencent/MicroMsg/WeiXin/wx_camera_1501461829261.jpg
     * <p>
     * 接收从其他界面返回的数据
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_BUYER_CROP:
                    List<LocalMedia> medias = new ArrayList<>();
                    LocalMedia mediax;
                    String imageType;
                    String newFilePath = data.getStringExtra(EditImageActivity.EXTRA_OUTPUT);
                    boolean isImageEdit = data.getBooleanExtra(EditImageActivity.IMAGE_IS_EDIT, false);
                    if (isImageEdit) {
                        MediaStoreUtils.scanImageFile(mContext, newFilePath);
                        mediax = new LocalMedia();
                        imageType = PictureMimeType.createImageType(newFilePath);
                        mediax.setCut(true);
                        mediax.setmCurrentPos(mCurrentPos);
                        mediax.setPath(newFilePath);
                        mediax.setCutPath(newFilePath);
                        mediax.setPictureType(imageType);
                        mediax.setMimeType(mimeType);
                        medias.add(mediax);
                        if (!ListUtils.isEmpty(picAllList)) {
                            for (int i = 0; i < picAllList.size(); i++) {
                                if (mCurrentPos == i) {
                                    picAllList.get(i).setIs_upload(false);
                                    picAllList.get(i).setPath(mediax.getCutPath());
                                    break;
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } else {//未编辑  还是用原来的图片
                        //  newFilePath = data.getStringExtra(EditImageActivity.FILE_PATH);
                    }
                    break;
                case UCropMulti.REQUEST_BUYER_MULTI_CROP:
                    //裁截
                    List<LocalMedia> xList = PictureSelector.obtainMultipleResult(data);
                    LocalMedia media = null;
                    if (!ListUtils.isEmpty(xList)) {
                        media = xList.get(0);
                        int mCurrentPos = media.getmCurrentPos();
                        if (!ListUtils.isEmpty(picAllList)) {
                            for (int i = 0; i < picAllList.size(); i++) {
                                if (mCurrentPos == i) {
                                    picAllList.get(i).setIs_upload(false);
                                    picAllList.get(i).setPath(media.getCutPath());
                                    break;
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                    break;
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
                    List<LocalMedia> list = new ArrayList<>();
                    if (!ListUtils.isEmpty(picAllList)) {
                        for (LocalMedia xmedia : picAllList) {
                            if (xmedia.is_upload()) {
                                list.add(xmedia);
                            }
                        }
                    }
                    picAllList.clear();
                    if (list.size() > 0)
                        picAllList.addAll(list);
                    picAllList.addAll(selectList);
                    adapter.setList(picAllList);
                    adapter.notifyDataSetChanged();
                    DebugUtil.i(TAG, "onActivityResult:" + selectList.size());
                    break;
                case REQUEST_VIDIEO:
                    doCompressVideo(data);
                    break;
            }
        }
        if (resultCode == RESULTCODE_OK) {
            if (data == null)
                return;
            switch (requestCode) {
                case REQUEST_STALL:

                    int stall_id = data.getIntExtra(ChooseStallsActivity.EXTRA_STALLID, 0);
                    if (stall_id <= 0)
                        return;
                    stallID = stall_id;
                    stall_all_name = data.getStringExtra(ChooseStallsActivity.EXTRA_STALL_ALL_NAME);
                    marketID = data.getIntExtra(ChooseStallsActivity.EXTRA_MARKETID, 0);
                    floorID = data.getIntExtra(ChooseStallsActivity.EXTRA_FLOORID, 0);
                    if (stallID > 0) {
                        if (!TextUtils.isEmpty(stall_all_name)) {
                            tv_stalls_name.setText(stall_all_name);
                        }
                    }
                    break;
                case REQUESTCODE_SETSPECQTY:
                    //设置规格数量

                    List<ColorSizeModel> colorSizeList_temp = (List<ColorSizeModel>) data
                            .getSerializableExtra(SpecQtyActivity.COLOR_SIZE_LIST);
                    List<UploadBean.ColorPicsBean> list = (List<UploadBean.ColorPicsBean>) data.getSerializableExtra(EXTRA_UPLOAD_COLORPICS_ITEM);
                    if (ListUtils.isEmpty(list)) {
                        colorPicsBeanList.clear();
                    } else {
                        colorPicsBeanList.clear();
                        colorPicsBeanList.addAll(list);
                    }
                    //waitDay = data.getIntExtra(SpecQtyActivity.WAIT_DAY, 0);
                    //isWaitDay = data.getBooleanExtra(SpecQtyActivity.IS_WAIT_ORDER, false);
                    if (colorSizeList_temp != null) {
                        if (mColorSizeList == null) {
                            mColorSizeList = new ArrayList<ColorSizeModel>();
                        } else {
                            mColorSizeList.clear();
                        }
                        for (ColorSizeModel colorSize : colorSizeList_temp) {
                            if (colorSize.getQty() <= 0)
                                continue;
                            mColorSizeList.add(colorSize);
                        }
                    }
                    if (mColorSizeList.size() == 0) {
                        btnSpecQty.setText(R.string.uploaditem_btnSpecQty_text);
                    } else {
                        // 重新设置规格数量
                        // int totalQty = 0;
                        String color = "";
                        String size = "";
                        for (ColorSizeModel entity : mColorSizeList) {
                            // int qty = entity.getQty();
                            // totalQty += qty;
                            if (color.indexOf("," + entity.getColor().getName()) == -1) {
                                color += "," + entity.getColor().getName();
                            }
                            if (size.indexOf("," + entity.getSize().getName()) == -1) {
                                size += "," + entity.getSize().getName();
                            }
                        }
                        if (color.length() > 0) {
                            color = color.substring(1);
                        }
                        if (size.length() > 0) {
                            size = size.substring(1);
                        }
                        btnSpecQty.setText(color + "/" + size);
                        break;
                    }
            }
//        // 拍照
//        else if (requestCode == REQUESTCODE_TAKEPHOTO) {
//            if (!SDCardHelper.checkFileExists(mPhotoUri.getPath())) {
//                Toast.makeText(vThis, "未找到图片：" + mPhotoUri.getPath(), Toast.LENGTH_SHORT).show();
//                return;
//            }
//            String srcPath = mPhotoUri.getPath();
//            // 校正旋转,这种连拍情况下，image会旋转，这里根据调整为正确状态
//            ImageTools.checkImgRotaing(srcPath, true, 0, false);
//            ImageViewModel photoModel = new ImageViewModel();
//            photoModel.setCanRemove(false);
//            photoModel.setLoading(false);
//            photoModel.setNewAdd(true);
//            photoModel.setUploadStatus(UploadStatus.NONE);
//            photoModel.setUrl(srcPath);
//            photoModel.setOriginalUrl(srcPath);
//            mAdapter.add(photoModel);
//            showImageContainer();
//        }
//        // 从手机相册选择
//        else if (requestCode == REQUESTCODE_FROMALBUM) {
//            if (resultCode == AlbumActivity.RESULTCODE_OK) {
//                handleImgsFromAlbumn(data);
//            }
//        }
//        // 选择商品分组
//        else if ((requestCode == REQUESTCODE_ITEM_GROUPS_CHANGED || resultCode == RESULTCODE_ITEM_GROUPS_CHANGED)
//                && data != null) {
//            mItemGroupIds = data.getStringExtra("GROUP_IDS");
//            mItemGroupNames = data.getStringExtra("GROUP_NAMES");
//            mItemGroupIds = StringUtils.deleteEndStr(mItemGroupIds, ",");
//            mItemGroupNames = StringUtils.deleteEndStr(mItemGroupNames, ",");
//            mEtItemGroup.setText(mItemGroupNames);
//        }
//        // 选择分类
//        else if (requestCode == REQUEST_SELECT_ITEM_SYS_CATEGORY && data != null) {
//            mItemStyle = (ItemStyle) data.getSerializableExtra(SelectSysCategoryActivity.EXTRA_SELECTED_STYLE);
//            if (mItemStyle != null) {
//                mEtItemCategory.setHint(mItemStyle.getParentName() + "/" + mItemStyle.getName());
//            } else {
//                mEtItemCategory.setHint("选填");
//            }
//        } else if (requestCode == REQUEST_SELECT_ITEM_SHOP_CATEGORY) {// 选择本店分类
//            onShopCategorySelected(data);
            //    }
        }
    }

    private void doCompressVideo(Intent data) {
        videoSelectList = PictureSelector.obtainMultipleResult(data);
                /*    List<LocalMedia> xlist = new ArrayList<>();
                    if (!ListUtils.isEmpty(videoAllList)) {
                        for (LocalMedia xmedia : videoAllList) {
                            if (xmedia.is_upload()) {
                                xlist.add(xmedia);
                            }
                        }
                    }
                   // videoAllList.clear();
                    if (xlist.size() > 0)
                        videoAllList.addAll(xlist);*/
//2：426188出现 java.lang.UnsatisfiedLinkError错误可以尝试在gradle.properties中添加：android.useDeprecatedNdk=true，然后在主module的build.gradle中配置ndk {abiFilters "armeabi", "armeabi-v7a"}
        if (SpManager.getUploadCompressVideo(vThis)) {
            final LocalMedia localMedia = videoSelectList.get(0);
            boolean isPass = FileUtils.isPassLegth(localMedia.getPath());
            if (isPass) {
                BaseMediaBitrateConfig compressMode = null;
                // compressMode = new VBRMode(10000, 3000);
                compressMode = new AutoVBRMode(32);
                // compressMode=new CBRMode(166,3000);
                compressMode.setVelocity(BaseMediaBitrateConfig.Velocity.SUPERFAST);
                LocalMediaConfig.Buidler buidler = new LocalMediaConfig.Buidler();
                final LocalMediaConfig config = buidler
                        .setVideoPath(localMedia.getPath())
                        .captureThumbnailsTime(1)
                        .doH264Compress(compressMode)
                        .setFramerate(0)
                        .setScale(1.5f)
                        .build();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showProgress("", "压缩中...请勿退出当前页面", -1);
                            }
                        });
                        final OnlyCompressOverBean onlyCompressOverBean = new LocalMediaCompress(config).startCompress();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (onlyCompressOverBean != null) {
                                    localMedia.setPath(onlyCompressOverBean.getVideoPath());
                                    videoAllList.addAll(videoSelectList);
                                    videoAdapter.setList(videoAllList);
                                    videoAdapter.notifyDataSetChanged();
                                }
                                hideProgress();
                            }
                        });
                    }
                }).start();
            } else {
                videoAllList.addAll(videoSelectList);
                videoAdapter.setList(videoAllList);
                videoAdapter.notifyDataSetChanged();
            }
        } else {
            List<LocalMedia> xlist = new ArrayList<>();
            if (!ListUtils.isEmpty(videoAllList)) {
                for (LocalMedia xmedia : videoAllList) {
                    if (xmedia.is_upload()) {
                        xlist.add(xmedia);
                    }
                }
            }
            videoAllList.clear();
            if (xlist.size() > 0)
                videoAllList.addAll(xlist);
            videoAllList.addAll(videoSelectList);
            videoAdapter.setList(videoAllList);
            videoAdapter.notifyDataSetChanged();
//                    DebugUtil.i(TAG, "onActivityResult:" + videoSelectList.size());
        }
    }

    private void showProgress(String title, String message, int theme) {
        if (mProgressDialog == null) {
            if (theme > 0)
                mProgressDialog = new ProgressDialog(this, theme);
            else
                mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCanceledOnTouchOutside(false);// 不能取消
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);// 设置进度条是否不明确
        }

        if (!FunctionHelper.isEmpty(title))
            mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    private void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            // 进入相册 以下是例子：不需要的api可以不写
            int count = 0;
            List<LocalMedia> pList = new ArrayList<>();
            if (!ListUtils.isEmpty(picAllList)) {
                for (LocalMedia media : picAllList) {
                    if (!media.is_upload()) {
                        pList.add(media);
                    } else {
                        count++;
                    }
                }
            }
            picSelectMaxNum = maxSelectNum - count;
            PictureSelector.create(vThis)
                    .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                    .theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                    .maxSelectNum(picSelectMaxNum)// 最大图片选择数量
                    .minSelectNum(1)// 最小选择数量
                    .imageSpanCount(4)// 每行显示个数
                    .selectionMode(
                            PictureConfig.MULTIPLE)// 多选 or 单选
                    .previewImage(true)// 是否可预览图片
                    .previewVideo(false)// 是否可预览视频
                    .enablePreviewAudio(false) // 是否可播放音频
                    .compressGrade(Luban.THIRD_GEAR)// luban压缩档次，默认3档 Luban.FIRST_GEAR、Luban.CUSTOM_GEAR
                    .isCamera(false)// 是否显示拍照按钮
                    .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                    //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径
                    .enableCrop(false)// 是否裁剪
                    .compress(false)// 是否压缩
                    .compressMode(PictureConfig.LUBAN_COMPRESS_MODE)//系统自带 or 鲁班压缩 PictureConfig.SYSTEM_COMPRESS_MODE or LUBAN_COMPRESS_MODE
                    //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                    .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                    .withAspectRatio(1, 1)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                    .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示
                    .isGif(false)// 是否显示gif图片
                    .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
                    .circleDimmedLayer(false)// 是否圆形裁剪
                    .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                    .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                    .openClickSound(false)// 是否开启点击声音
                    .selectionMedia(pList)// 是否传入已选图片
                    //.previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                    //.cropCompressQuality(90)// 裁剪压缩质量 默认100
                    .compressMaxKB(200)//压缩最大值kb compressGrade()为Luban.CUSTOM_GEAR有效
                    .compressWH(1100, 1100) // 压缩宽高比 compressGrade()为Luban.CUSTOM_GEAR有效
                    //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                    //.rotateEnabled() // 裁剪是否可旋转图片
                    //.scaleEnabled()// 裁剪是否可放大缩小图片
                    //.videoQuality()// 视频录制质量 0 or 1
                    //.videoSecond()//显示多少秒以内的视频or音频也可适用
                    //.recordVideoSecond()//录制视频秒数 默认60s
                    .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code

        }

    };
    private GridImageAdapter.onAddPicClickListener onAddVideoClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            // 进入相册 以下是例子：不需要的api可以不写
            int count = 0;
            List<LocalMedia> vList = new ArrayList<>();
            if (!ListUtils.isEmpty(videoAllList)) {
                for (LocalMedia media : videoAllList) {
                    if (!media.is_upload()) {
                        vList.add(media);
                    } else {
                        count++;
                    }
                }
            }
            videoSelectMaxNum = maxVidieoSelectNum - count;
            PictureSelector.create(vThis)
                    .openGallery(PictureMimeType.ofVideo())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                    .theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                    .maxSelectNum(videoSelectMaxNum)// 最大图片选择数量
                    .minSelectNum(1)// 最小选择数量
                    .imageSpanCount(4)// 每行显示个数
                    .selectionMode(
                            videoSelectionMode)// 多选 or 单选
                    .previewImage(true)// 是否可预览图片
                    .previewVideo(true)// 是否可预览视频
                    .enablePreviewAudio(true) // 是否可播放音频
                    .compressGrade(Luban.THIRD_GEAR)// luban压缩档次，默认3档 Luban.FIRST_GEAR、Luban.CUSTOM_GEAR
                    .isCamera(false)// 是否显示拍照按钮
                    .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                    //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径
                    .enableCrop(false)// 是否裁剪
                    .compress(false)// 是否压缩
                    .compressMode(PictureConfig.LUBAN_COMPRESS_MODE)//系统自带 or 鲁班压缩 PictureConfig.SYSTEM_COMPRESS_MODE or LUBAN_COMPRESS_MODE
                    //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                    .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                    .withAspectRatio(4, 1)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                    .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示
                    .isGif(false)// 是否显示gif图片
                    .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
                    .circleDimmedLayer(false)// 是否圆形裁剪
                    .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                    .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                    .openClickSound(false)// 是否开启点击声音
                    .selectionMedia(vList)// 是否传入已选图片
                    //.previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                    //.cropCompressQuality(90)// 裁剪压缩质量 默认100
                    //.compressMaxKB()//压缩最大值kb compressGrade()为Luban.CUSTOM_GEAR有效
                    //.compressWH() // 压缩宽高比 compressGrade()为Luban.CUSTOM_GEAR有效
                    //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                    //.rotateEnabled() // 裁剪是否可旋转图片
                    //.scaleEnabled()// 裁剪是否可放大缩小图片
                    //.videoQuality()// 视频录制质量 0 or 1
                    //.videoSecond()//显示多少秒以内的视频or音频也可适用
                    //.recordVideoSecond()//录制视频秒数 默认60s
                    .forResult(REQUEST_VIDIEO);//结果回调onActivityResult code

        }

    };

    public void initSystemModel() {
//        String[] labels = getResources().getStringArray(R.array.good_label_default);
//        for (int i = 0; i < labels.length; i++) {
//            LabelItemModel simJM = new LabelItemModel();
//            LabelModel smJM = new LabelModel();
//            smJM.setName(labels[i]);
//            smJM.setID(-1+"");
//            simJM.setLabel(smJM);
//            mLabelList.add(simJM);
//        }
    }

    public void initData() {
        // 3.选择标签
        mLabelAdapter = new LabelGridItemAdapter(vThis, mLabelList);
        mLabelAdapter.setOnLabelItemClickListener(new LabelGridItemAdapter.OnLabelItemClickListener() {
            @Override
            public void onCheckChanged(View v, boolean isChecked) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEventBus.unregister(this);
        if (loadDataThread != null) {
            handler.removeCallbacks(loadDataThread);
            loadDataThread = null;
        }
    }

    public void onEventMainThread(BusEvent event) {
        switch (event.id) {
//            case EventBusId.CHANGE_UPLOADITEM_TAG:
//                new TagsTask(Step.GET_TAGS).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // 初始化修改商品的状态
    private void initEditItem() {
        // setShopCatsText();
        edtPrice.setText(String.valueOf(editItem.getPrice()));
        // edtRaisePrice.setText(String.valueOf(editItem.getRetailPrice()/editItem.getPrice()*100));
        edtRetailPrice.setText(String.valueOf(editItem.getRetailPrice()));
        edtDescription.setText(editItem.getIntroOrName());
        mEtTitle.setText(editItem.getName());
        mItemGroupNames = editItem.getGroupNamesFromGroups();
        mItemGroupIds = editItem.getGroupIdsFromGropus();
        if (editItem.getIsOnly4Agent()) {
            if (TextUtils.isEmpty(mItemGroupNames)) {
                mEtItemGroup.setText("所有代理");
                mItemGroupIds = Const.SystemGroupId.ALL_AGENT + "";
            } else {
                mEtItemGroup.setText(mItemGroupNames);
            }
        } else {
            mEtItemGroup.setText("公开");
            mItemGroupIds = Const.SystemGroupId.ALL_PPL + "";
        }

        if (!ListUtils.isEmpty(editItem.getTags())) {
            String tags = "";
            for (LabelModel item : editItem.getTags()) {
                tags += item.getName() + ",";
            }
            updateList = editItem.getTags();
            mTagsTwo.setText(tags.substring(0, tags.length() - 1));
        }

        // 规格
        // int stock = 0;
        // List<ProductModel>
        String color = "";
        String size = "";
        for (ProductModel item : editItem.getProducts()) {
            // stock = stock + item.getStock();
            if (color.indexOf("," + item.getColor()) == -1) {
                color += "," + item.getColor();
            }
            if (size.indexOf("," + item.getSize()) == -1) {
                size += "," + item.getSize();
            }

            // 初始化product数据到mColorSizeList中
            ColorSizeModel colorsizeModel = new ColorSizeModel();
            // 颜色
            ColorModel colorModel = new ColorModel();
            colorModel.setName(item.getColor());
            colorsizeModel.setColor(colorModel);
            // 尺码
            SizeModel sizeModel = new SizeModel();
            sizeModel.setName(item.getSize());
            colorsizeModel.setSize(sizeModel);
            // 库存
            colorsizeModel.setQty(item.getStock());
            mColorSizeList.add(colorsizeModel);

        }
        if (color.length() > 0) {
            color = color.substring(1);
        }
        if (size.length() > 0) {
            size = size.substring(1);
        }
        btnSpecQty.setText(color + "/" + size);

        List<ImageViewModel> imgItems = new ArrayList<ImageViewModel>();
        // 初始化图片
        for (String img : editItem.getImages()) {

            String fullUrl = ImageUrlExtends.getImageUrl(img);
            // 将图片添加到图片展示区
            ImageViewModel imageViewModel = new ImageViewModel();
            imageViewModel.setCanRemove(true);
            imageViewModel.setNewAdd(false);
            imageViewModel.setUrl(fullUrl);
            imageViewModel.setWebsite(img);// 原图
            imgItems.add(imageViewModel);
        }
        //mAdapter.set(imgItems);
    }

    private boolean flag = false;

    public class PriceTextWatcher implements TextWatcher {
        private int type;

        PriceTextWatcher(int type) {
            this.type = type;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                if (flag) {
                    return;
                }
                flag = true;
                String supply_price, add_price, stall_price, discount_price, ori_discount_price;
                switch (type) {
                    case 5:
                        supply_price = s.toString().trim();
                        add_price = add_price_edtPrice.getText().toString().trim();
                        discount_price = add_discount_edtPrice.getText().toString().trim();
                        if (TextUtils.isEmpty(supply_price)) {
                            add_stall_price_edtPrice.setText("");
                            add_original_price_edtPrice.setText("");
                        }
                        if (!TextUtils.isEmpty(add_price) && !TextUtils.isEmpty(supply_price)) {
                            add_stall_price_edtPrice.setText(FunctionHelper.DoubleFormat(Double.parseDouble(supply_price) - Double.parseDouble(add_price)) + "");
                        }
                        if (!TextUtils.isEmpty(discount_price) && !TextUtils.isEmpty(supply_price)) {
                            add_original_price_edtPrice.setText(FunctionHelper.DoubleFormat(Double.parseDouble(supply_price) / Double.parseDouble(discount_price) * 10) + "");
                        }
                        break;
                    case 1:
                        supply_price = edtPrice.getText().toString().trim();
                        add_price = s.toString().trim();
                        if (!TextUtils.isEmpty(supply_price)) {
                            if (TextUtils.isEmpty(add_price)) {
                                add_price = "0.00";
                            }
                            if (Double.parseDouble(supply_price) - Double.parseDouble(add_price) == 0) {
                                add_stall_price_edtPrice.setText("");
                            } else {
                                add_stall_price_edtPrice.setText(FunctionHelper.DoubleFormat(Double.parseDouble(supply_price) - Double.parseDouble(add_price)) + "");
                            }

                        }
                        break;
                    case 2:
                        add_price = add_price_edtPrice.getText().toString().trim();
                        stall_price = s.toString().trim();
                        if (TextUtils.isEmpty(stall_price))
                            stall_price = "0.00";
                        if (!TextUtils.isEmpty(add_price)) {
                            if (Double.parseDouble(stall_price) + Double.parseDouble(add_price) == 0) {
                                edtPrice.setText("");
                                add_original_price_edtPrice.setText("");
                            } else {
                                supply_price = FunctionHelper.DoubleFormat(Double.parseDouble(add_price) + Double.parseDouble(stall_price)) + "";
                                edtPrice.setText(supply_price);
                                discount_price = add_discount_edtPrice.getText().toString().trim();
                                if (!TextUtils.isEmpty(discount_price) && !TextUtils.isEmpty(supply_price)) {
                                    add_original_price_edtPrice.setText(FunctionHelper.DoubleFormat(Double.parseDouble(supply_price) / Double.parseDouble(discount_price) * 10) + "");
                                }
                            }
                        }
                        break;
                    case 3:
                        supply_price = edtPrice.getText().toString().trim();
                        discount_price = s.toString().toString();
                        if (TextUtils.isEmpty(discount_price)) {
                            add_original_price_edtPrice.setText("");
                        } else if (!TextUtils.isEmpty(supply_price)) {
                            if (Double.parseDouble(discount_price) > 10) {
                                discount_price = "10";
                                add_discount_edtPrice.setText(discount_price);
                            }
                            add_original_price_edtPrice.setText(FunctionHelper.DoubleFormat(Double.parseDouble(supply_price) / Double.parseDouble(discount_price) * 10) + "");
                        }
                        break;
                    case 4:
                        discount_price = add_discount_edtPrice.getText().toString().toString();
                        ori_discount_price = s.toString().toString();
                        if (TextUtils.isEmpty(ori_discount_price))
                            ori_discount_price = "0.00";
                        if (!TextUtils.isEmpty(discount_price)) {
                            supply_price = FunctionHelper.DoubleFormat(Double.parseDouble(discount_price) * Double.parseDouble(ori_discount_price) / 10) + "";
                            edtPrice.setText(supply_price);
                            add_price = add_price_edtPrice.getText().toString().trim();
                            if (!TextUtils.isEmpty(add_price) && !TextUtils.isEmpty(supply_price)) {
                                add_stall_price_edtPrice.setText(FunctionHelper.DoubleFormat(Double.parseDouble(supply_price) - Double.parseDouble(add_price)) + "");
                            }
                        }
                        break;
                }
                flag = false;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化视图
     */
    private void initView() {
        et_item_Summary = (EditText) findViewById(R.id.et_item_Summary);
        add_price_edtPrice = (EditText) findViewById(R.id.add_price_edtPrice);
        add_stall_price_edtPrice = (EditText) findViewById(R.id.add_stall_price_edtPrice);
        add_discount_edtPrice = (EditText) findViewById(R.id.add_discount_edtPrice);
        add_original_price_edtPrice = (EditText) findViewById(R.id.add_original_price_edtPrice);
        add_price_edtPrice.addTextChangedListener(new PriceTextWatcher(1));
        add_stall_price_edtPrice.addTextChangedListener(new PriceTextWatcher(2));
        add_discount_edtPrice.addTextChangedListener(new PriceTextWatcher(3));
        add_original_price_edtPrice.addTextChangedListener(new PriceTextWatcher(4));
        ll_add_price = findViewById(R.id.ll_add_price);
        ll_discount_price = findViewById(R.id.ll_discount_price);
        tv_item_shop_attribute = (TextView) findViewById(R.id.et_item_shop_attribute);
        et_into_group = (EditText) findViewById(R.id.et_into_group);
        et_single_row = (EditText) findViewById(R.id.et_single_row);
        et_single_row.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                et_single_row.setFocusableInTouchMode(true);
                et_single_row.setFocusable(true);
                et_single_row.setEnabled(true);
                et_single_row.requestFocus();
            }
        });
        et_into_group.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                et_into_group.setFocusableInTouchMode(true);
                et_into_group.setFocusable(true);
                et_into_group.setEnabled(true);
                et_into_group.requestFocus();
            }
        });
        uploaditem_remark = (EditText) findViewById(R.id.uploaditem_remark);
        et_item_shop_materia = (TextView) findViewById(R.id.et_item_shop_materia);
        et_item_shop_ager = (TextView) findViewById(R.id.et_item_shop_ager);
        et_item_shop_style = (TextView) findViewById(R.id.et_item_shop_style);
        et_item_shop_season = (TextView) findViewById(R.id.et_item_shop_season);
        findViewById(R.id.ll_stall).setOnClickListener(this);
        findViewById(R.id.ll_uploaditem_btnSpecQty).setOnClickListener(this);
        findViewById(R.id.layout_item_shop_category).setOnClickListener(this);
        findViewById(R.id.layout_item_shop_attribute).setOnClickListener(this);
        findViewById(R.id.layout_item_shop_materia).setOnClickListener(this);
        findViewById(R.id.layout_item_shop_ager).setOnClickListener(this);
        findViewById(R.id.layout_item_shop_style).setOnClickListener(this);
        findViewById(R.id.layout_item_shop_season).setOnClickListener(this);
        tv_stalls_name = (TextView) findViewById(R.id.tv_stalls_name);
        mEtItemShopCategories = (TextView) findViewById(R.id.et_item_shop_category);
        mEtItemCategory = (EditText) findViewById(R.id.et_item_sys_category);
//        if (mItemStyle != null) {
//            mEtItemCategory.setHint(mItemStyle.getParentName() + "/" + mItemStyle.getName());
//        }
        loadingDialog = new LoadingDialog(vThis);
        //initGridView();
        boolean isMicroSources = SpManager.isJoinMicroSources(vThis);// 是否加入了微货源
        // findViewById(R.id.layout_item_category).setVisibility(isMicroSources ? View.VISIBLE : View.GONE);
        // 标题栏
        TextView btnLeft = (Button) findViewById(R.id.titlebar_btnLeft);
        TextView btnRight = (Button) findViewById(R.id.titlebar_btnRight);
        tvTitle = (TextView) findViewById(R.id.titlebar_tvTitle);
        //tvTitle.setText(R.string.title_activity_uploaditem);
        btnLeft.setText("返回");
        btnRight.setText(R.string.titlebar_btnComplete);
        btnLeft.setVisibility(View.VISIBLE);
        btnRight.setVisibility(View.VISIBLE);
        // 界面布局
        gvUploadImage = (DynamicGridView) findViewById(R.id.uploaditem_gvUploadImage);
        gvUploadImage.setLastDraggable(false);
        gvUploadImage.setScrollable(false);
//        gvUploadImage.setOnItemLongClickListener(new OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                boolean isAddBtn = (Boolean) view.getTag(R.id.tag_is_add_btn);
//                if (!isAddBtn) {
//                    Vibrator vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
//                    vib.vibrate(50);
//                    gvUploadImage.startEditMode(position);
//                }
//                return true;
//            }
//        });
        edtPrice = (EditText) findViewById(R.id.uploaditem_edtPrice);
        edtPrice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                edtPrice.setFocusableInTouchMode(true);
                edtPrice.setFocusable(true);
                edtPrice.setEnabled(true);
                edtPrice.requestFocus();
            }
        });
        edtPrice.addTextChangedListener(new PriceTextWatcher(5));
//        edtPrice.addTextChangedListener(new SimpleTextWatcher() {
//            public void afterTextChanged(Editable edt) {
//                int maxDigit = 7;// 最多7位数
//                String temp = edt.toString();
//                int posDot = temp.indexOf(".");
//                if (posDot <= 0) {
//                    if (temp.length() > maxDigit) {
//                        edt.delete(maxDigit, maxDigit + 1);
//                    }
//                } else {
//                    if (temp.length() - posDot - 1 > 2) {
//                        edt.delete(posDot + 3, posDot + 4);
//                    }
//                    temp = temp.substring(0, posDot);
//                    if (temp.length() > maxDigit) {
//                        edt.delete(posDot - 2, posDot - 1);
//                    }
//                }
//            }
//
//        });
        edtPrice.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (edtPrice.getText().toString().length() > 0) {
                        edtPrice.setSelection(edtPrice.getText().toString().length() - 1);
                    }
                }
            }
        });
        edtRetailPrice = (EditText) findViewById(R.id.uploaditem_edtRetailPrice);
        edtRetailPrice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                edtRetailPrice.setFocusableInTouchMode(true);
                edtRetailPrice.setFocusable(true);
                edtRetailPrice.setEnabled(true);
                edtRetailPrice.requestFocus();
            }
        });
        edtRetailPrice.addTextChangedListener(new SimpleTextWatcher() {
            public void afterTextChanged(Editable edt) {
                int maxDigit = 7;// 最多7位数
                String temp = edt.toString();
                int posDot = temp.indexOf(".");
                if (posDot <= 0) {
                    if (temp.length() > maxDigit) {
                        edt.delete(maxDigit, maxDigit + 1);
                    }
                } else {
                    if (temp.length() - posDot - 1 > 2) {
                        edt.delete(posDot + 3, posDot + 4);
                    }
                    temp = temp.substring(0, posDot);
                    if (temp.length() > maxDigit) {
                        edt.delete(posDot - 2, posDot - 1);
                    }
                }
            }

        });
        edtRetailPrice.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (edtRetailPrice.getText().toString().length() > 0) {
                        edtRetailPrice.setSelection(edtRetailPrice.getText().toString().length() - 1);
                    }
                }
            }
        });
        edtDescription = (EditText) findViewById(R.id.uploaditem_edtDescription);
        mEtTitle = (EditText) findViewById(R.id.et_item_title);
        edtDescription.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                edtDescription.setFocusableInTouchMode(true);
                edtDescription.setFocusable(true);
                edtDescription.setEnabled(true);
                edtDescription.requestFocus();
            }
        });
        edtDescription.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
//                s.clear();
//                s.append(s.toString().replace("\\", ""));
                if (s.toString().contains("\\")) {
                    String editable = edtDescription.getText().toString();
                    String str = editable.replace("\\", "");
                    s.clear();
                    s.append(str);
                }

                if (mEtTitle.getText().toString().length() < 100) {
                    int endPos = 100;
                    if (s.length() >= 100 && StringUtils.isEmojiCharacter(s.charAt(99))) {
                        endPos = 98;
                    }
                    mEtTitle.setText(StringUtils.substring(s.toString(), 0, endPos));
                }
            }
        });
        mEtTitle.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().contains("\\")) {
                    String editable = mEtTitle.getText().toString();
                    String str = editable.replace("\\", "");
                    s.clear();
                    s.append(str);
                }
                if (s.toString().length() > 100) {
                    int endPos = 100;
                    if (s.length() >= 100 && StringUtils.isEmojiCharacter(s.charAt(99))) {
                        endPos = 98;
                    }
                    String text = StringUtils.substring(s.toString(), 0, endPos);
                    mEtTitle.setText(text);
                    mEtTitle.setSelection(text.length());
                    ViewHub.showShortToast(vThis, "商品名称只允许输入100个字");
                }
            }
        });
        btnAddImage = (Button) findViewById(R.id.uploaditem_btnAddImage);
        btnSpecQty = (Button) findViewById(R.id.uploaditem_btnSpecQty);
        mEtItemGroup = (EditText) findViewById(R.id.visible_range);
        mEtItemGroup.setText(mItemGroupNames);


        // 添加字符串
        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);
        btnAddImage.setOnClickListener(this);
        //btnSpecQty.setOnClickListener(this);

        // 初始化适配器
        // mImageItemAdapter = new UploadImageItemAdapter(mImageList);
        //  mAdapter = new UploadItemPicGridAdapter(this, getResources().getInteger(R.integer.upload_item_pic_col_num));
        // mAdapter.set(mPicModels);
        //   gvUploadImage.setAdapter(mAdapter);
//        gvUploadImage.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                boolean isAddBtn = (Boolean) view.getTag(R.id.tag_is_add_btn);
//                if (isAddBtn) {
//                    onAddImgBtnClick(view);
//                } else {
//                    mCurrentClickedViewModel = (ImageViewModel) view.getTag();
//                    togglePopupWindow(view, 2);
//                }
//            }
//        });

    }

    /**
     * @description 点击添加图片按钮
     * @created 2014-12-8 下午4:05:31
     * @author ZZB
     */
    private void onAddImgBtnClick(View v) {
//        if (mAdapter.getPicCount() == MAX_IMG_COUNT) {
//            ViewHub.showShortToast(vThis, "最多只能添加9张图片哦！");
//            return;
//        } else {
//            togglePopupWindow(v, 1);
//        }
    }

    protected boolean isDialogShowing() {
        return (loadingDialog != null && loadingDialog.isShowing());
    }

    protected void hideDialog() {
        if (isDialogShowing()) {
            loadingDialog.stop();
        }
    }

    List<CategoryBean> cList;
    List<AttributeBean> aList;

    private class Task extends AsyncTask<Object, Void, Object> {
        private Step mStep;

        public Task(Step mStep) {
            this.mStep = mStep;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            switch (mStep) {
                case CATEGORY_TAGS:
                    loadingDialog.start("加载分类数据..");
                    break;
                case ATTRIBUTE_TAG:
                    loadingDialog.start("加载属性数据..");
                    break;
                case EXTEND_PROPERTY_LIST_TAG:
                    loadingDialog.start("加载扩展属性数据..");
                    break;
                case SetData:
                    loadingDialog.start("处理数据..");
                    break;
            }


        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                switch (mStep) {
                    case CATEGORY_TAGS:
                        List<CategoryBean> list = ShopSetAPI.getCategoryList(UploadItemActivity.this);
                        return list;
                    case ATTRIBUTE_TAG:
                        List<AttributeBean> alist = ShopSetAPI.getPropertyList(UploadItemActivity.this, subID);
                        return alist;
                    case EXTEND_PROPERTY_LIST_TAG:
                        List<UploadBean.ExtendPropertyTypeListV2Bean> eList = ShopSetAPI.getExtendPropertyListV2(vThis, 0);
                        return eList;
                    case SetData:
                        setHasData(extendHasList);
                        return "ok";
                }
                return "error:未找到函数";
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
                switch (mStep) {
                    case SetData:
                        if (propertyTypeListAdapter != null) {
                            propertyTypeListAdapter.setData(eList);
                            propertyTypeListAdapter.notifyDataSetChanged();
                        }
                        break;
                    case CATEGORY_TAGS:
                        cList = (List<CategoryBean>) result;
                        CategoryChooseDialog.getInstance(vThis).setID(parentID, subID).setList(cList).setPositive(vThis).showDialog();
                        break;
                    case ATTRIBUTE_TAG:
                        aList = (List<AttributeBean>) result;
                        AttributeDialog.getInstance(vThis).setList(aList).setPropertyIDS(propertyIDS).setPositive(vThis).showDialog();
                        break;
                    case EXTEND_PROPERTY_LIST_TAG:
                        List<UploadBean.ExtendPropertyTypeListV2Bean> data = (List<UploadBean.ExtendPropertyTypeListV2Bean>) result;
                        eList.clear();
                        eList.addAll(data);
                        setExtendDapter();
                        break;
                }

            }
        }
    }

    public void setHasData(List<UploadBean.ExtendPropertyTypeListV2Bean> hasList) {
        if (!ListUtils.isEmpty(hasList)) {
            for (int h = 0; h < hasList.size(); h++) {
                UploadBean.ExtendPropertyTypeListV2Bean ee = hasList.get(h);
                StringBuffer sb = new StringBuffer();
                sb.append("");
                StringBuffer sb2 = new StringBuffer();
                sb2.append("");
                boolean isGrouping = ee.isGrouping();
                if (!ListUtils.isEmpty(ee.getExtendPropertyList())) {

                    for (int i = 0; i < ee.getExtendPropertyList().size(); i++) {
                        List<UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean> v2list = ee.getExtendPropertyList().get(i);
                        if (!ListUtils.isEmpty(v2list)) {
                            for (int j = 0; j < v2list.size(); j++) {
                                UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean aa = v2list.get(j);
                                sb.append(aa.getName());
                                if (!TextUtils.isEmpty(aa.getValue()))
                                    sb.append("(" + aa.getValue() + ")");
                                sb.append(",");
                            }
                        }
                        if (!TextUtils.isEmpty(sb.toString())) {
                            if (isGrouping) {
                                sb.insert(0, (i + 1) + "：");
                            }
                            sb2.append(sb.toString());
                        }
                        sb.setLength(0);
                    }

                }
                if (TextUtils.isEmpty(sb2.toString())) {
                    ee.setSelContent("");
                } else {
                    ee.setSelContent(sb2.substring(0, sb2.length() - 1));
                }
            }
//            for (UploadBean.ExtendPropertyTypeListBean ee : hasList) {
//                StringBuffer sb = new StringBuffer();
//                sb.append("");
//                for (int i = 0; i < ee.getExtendPropertyList().size(); i++) {
//                    UploadBean.ExtendPropertyTypeListBean.ExtendPropertyListBean aa = ee.getExtendPropertyList().get(i);
//                        sb.append(aa.getName());
//                        if (!TextUtils.isEmpty(aa.getValue()))
//                            sb.append("(" + aa.getValue() + ")");
//                        sb.append(",");
//                }
//                if (TextUtils.isEmpty(sb.toString())) {
//                    ee.setSelContent("");
//                } else {
//                    ee.setSelContent(sb.substring(0, sb.length() - 1));
//                }
//            }
            if (!ListUtils.isEmpty(eList)) {
                for (int i = 0; i < eList.size(); i++) {
                    UploadBean.ExtendPropertyTypeListV2Bean a = eList.get(i);
                    for (int j = 0; j < hasList.size(); j++) {
                        UploadBean.ExtendPropertyTypeListV2Bean b = hasList.get(j);
                        if (a.getTypeID() == b.getTypeID()) {
                            int count1 = 0, count2 = 0;
                            if (!ListUtils.isEmpty(b.getExtendPropertyList()))
                                count2 = b.getExtendPropertyList().size();
                            if (!ListUtils.isEmpty(a.getExtendPropertyList()))
                                count1 = a.getExtendPropertyList().size();
                            if (count2 > count1) {
                                List<List<UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean>> copyList = new ArrayList<>();
                                for (int c = 0; c < count2; c++) {
                                    List<List<UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean>> list = a.getExtendPropertyList();
                                    copyList.addAll(list);
                                }
                                a.setExtendPropertyList(copyList);
                            }
                        }
                    }
                }
                for (int i = 0; i < eList.size(); i++) {
                    UploadBean.ExtendPropertyTypeListV2Bean a = eList.get(i);
                    for (int j = 0; j < hasList.size(); j++) {
                        UploadBean.ExtendPropertyTypeListV2Bean b = hasList.get(j);
                        if (a.getTypeID() == b.getTypeID()) {
                            a.setSelContent(b.getSelContent());
                            if (!ListUtils.isEmpty(a.getExtendPropertyList())) {
                                for (int o = 0; o < a.getExtendPropertyList().size(); o++) {
                                    List<UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean> list1 = a.getExtendPropertyList().get(o);
                                    if (!ListUtils.isEmpty(b.getExtendPropertyList())) {
                                        for (int p = 0; p < b.getExtendPropertyList().size(); p++) {
                                            if (o == p) {
                                                List<UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean> list2 = b.getExtendPropertyList().get(p);
                                                if (!ListUtils.isEmpty(list1)) {
                                                    for (UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean bean : list1) {
                                                        if (!ListUtils.isEmpty(list2)) {
                                                            for (UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean sub : list2) {
                                                                if (bean.getID() == sub.getID()) {
                                                                    bean.isSelect = true;
                                                                    bean.setValue(sub.getValue());
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

//                for (UploadBean.ExtendPropertyTypeListBean a : pareList) {
//                    for (UploadBean.ExtendPropertyTypeListBean b : hasList) {
//                        if (a.getTypeID() == b.getTypeID()) {
//                            a.setSelContent(b.getSelContent());
//                            for (UploadBean.ExtendPropertyTypeListBean.ExtendPropertyListBean bean : a.getExtendPropertyList()) {
//                                for (UploadBean.ExtendPropertyTypeListBean.ExtendPropertyListBean bb : b.getExtendPropertyList()) {
//                                    if (bb.getID() == bean.getID()) {
//                                        bean.isSelect = true;
//                                        bean.setValue(bb.getValue());
//                                    }
//                                }
//
//                            }
//                        }
//                    }
//                }
            }
        }
    }

    private void setExtendDapter() {
        new Task(SetData).execute();
//        if (propertyTypeListAdapter != null) {
//            propertyTypeListAdapter.setData(eList);
//            propertyTypeListAdapter.setHasData(extendHasList);
//            propertyTypeListAdapter.notifyDataSetChanged();
//        }
    }

    private class FourTask extends AsyncTask<Object, Void, Object> {
        int type;

        public FourTask(int type) {
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog.start("加载数据..");

        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                List<FourListBean> list = ShopSetAPI.getFourList(UploadItemActivity.this, type);
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
                List<FourListBean> list = (List<FourListBean>) result;
                int id = -1;
                switch (type) {
                    case Constant.FourList.Type_MaterialList:
                        id = mMaterId;
                        break;
                    case Constant.FourList.Type_GetAgeList:
                        id = mAgeId;
                        break;
                    case Constant.FourList.Type_StyleList:
                        id = mStyleId;
                        break;
                    case Constant.FourList.Type_SeasonList:
                        id = mSeasonId;
                        break;
                }
                FourListDialog.getInstance(UploadItemActivity.this).setType(type).setList(list).setHasSelId(id).setPositive(UploadItemActivity.this).showDialog();

            }
        }
    }

    List<UploadBean.PropertyListBean> pListBeanList = new ArrayList<>();

    @Override
    public void onGetAttriDialogButtonClick(List<AttributeBean.ChildsBean> childsBeanList, List<UploadBean.PropertyListBean> propertyListBeanList) {
        if (!ListUtils.isEmpty(propertyListBeanList)) {
            pListBeanList.clear();
            pListBeanList.addAll(propertyListBeanList);
        }
        if (!ListUtils.isEmpty(childsBeanList)) {
            propertyIDS.clear();
            StringBuffer sb = new StringBuffer("");
            for (int i = 0; i < childsBeanList.size(); i++) {
                AttributeBean.ChildsBean bean = childsBeanList.get(i);
                propertyIDS.add(bean.getID());
                if (!TextUtils.isEmpty(bean.getName())) {
                    sb.append(bean.getName());
                    if (i < childsBeanList.size() - 1) {
                        sb.append(",");
                    }
                }
            }
            if (!TextUtils.isEmpty(sb.toString()))
                tv_item_shop_attribute.setText(sb.toString());
        }
    }

    @Override
    public void onCategoryDialogButtonClick(int parentID, int subID, String pName, String sName) {
        if (subID > 0) {
            this.parentID = parentID;
            if (this.subID != subID && propertyIDS.size() > 0) {
                ViewHub.showShortToast(vThis, "二级分类已修改，请重新选择属性");
                propertyIDS.clear();
                pListBeanList.clear();
                tv_item_shop_attribute.setText("选择属性");
            }
            this.subID = subID;
            p_name = pName;
            s_name = sName;
            String name = pName + "-" + sName;
            if (!TextUtils.isEmpty(name))
                mEtItemShopCategories.setText(name);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            CommDialog.getInstance(vThis).setDialogType(CommDialog.DialogType.D_EXIT).setContent("是否退出编辑").setLeftStr("点错了，不退出").setRightStr("确定退出").setPositive(vThis).showDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean isSelectALL;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_item_shop_materia:
                new FourTask(Constant.FourList.Type_MaterialList).execute();
                break;
            case R.id.layout_item_shop_ager:
                new FourTask(Constant.FourList.Type_GetAgeList).execute();
                break;
            case R.id.layout_item_shop_style:
                new FourTask(Constant.FourList.Type_StyleList).execute();
                break;
            case R.id.layout_item_shop_season:
                new FourTask(Constant.FourList.Type_SeasonList).execute();
                break;
            case R.id.ll_stall:
                //选择档口
                Intent intent = new Intent(vThis, ChooseStallsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(ChooseStallsActivity.EXTRA_STALLID, stallID);
                bundle.putInt(ChooseStallsActivity.EXTRA_MARKETID, marketID);
                bundle.putInt(ChooseStallsActivity.EXTRA_FLOORID, floorID);
                intent.putExtras(bundle);
                startActivityForResult(intent, REQUEST_STALL);
                break;
            case R.id.tv_visible_range:// 可视范围问号
                PromptDialog dialog = new PromptDialog(this);
                dialog.setMessage("选中的分组用户才能看到该款商品，可用来对下级代理进行分组分类管理").setPositive("知道了", null).show();
                break;
            case R.id.layout_item_shop_category:// 本店分类
                new Task(CATEGORY_TAGS).execute();
//                Intent shopCatIntent = new Intent(this, SelectItemShopCategoryActivity.class);
//                shopCatIntent.putExtra(SelectItemShopCategoryActivity.EXTRA_SELECTED_CATS, mSelectedShopCategories);
//                shopCatIntent.putExtra(SelectItemShopCategoryActivity.EXTRA_IS_TOP, mIsItemTop);
//                shopCatIntent.putExtra(SelectItemShopCategoryActivity.EXTRA_IS_ON_SALE, mIsItemOnSale);
//                startActivityForResult(shopCatIntent, REQUEST_SELECT_ITEM_SHOP_CATEGORY);
                break;
            case R.id.layout_item_shop_attribute:
                new Task(ATTRIBUTE_TAG).execute();
                break;
            case R.id.titlebar_btnLeft:
                CommDialog.getInstance(vThis).setDialogType(CommDialog.DialogType.D_EXIT).setContent("是否退出编辑").setLeftStr("点错了，不退出").setRightStr("确定退出").setPositive(vThis).showDialog();
                // finish();
                break;
            case R.id.titlebar_btnRight:// 完成
                saveItem();
                break;
            case R.id.uploaditem_btnAddImage:
                togglePopupWindow(v, 1);
                break;
            case R.id.ll_uploaditem_btnSpecQty:
                // Log.d("yu", cPicsList.toString());
                Bundle specBundle = new Bundle();
                specBundle.putSerializable(COLOR_SIZE_LIST, (Serializable) mColorSizeList);
                specBundle.putSerializable(EXTRA_UPLOAD_COLORPICS_ITEM, (Serializable) colorPicsBeanList);
                specBundle.putBoolean(IS_WAIT_ORDER, isWaitDay);
                specBundle.putInt(WAIT_DAY, waitDay);
                Intent specQtyIntent = new Intent(vThis, SpecQtyActivity.class);
                specQtyIntent.putExtras(specBundle);
                startActivityForResult(specQtyIntent, REQUESTCODE_SETSPECQTY);
                break;
            case R.id.visible_range:// 选择可视范围
//                Intent intent = new Intent(this, SelectItemGroupActivity.class);
//                intent.putExtra(SelectItemGroupActivity.KEY_RESULT_CODE, RESULTCODE_ITEM_GROUPS_CHANGED);
//                intent.putExtra(SelectItemGroupActivity.EXTRA_SELECTED_ITEM_IDS, mItemGroupIds);
//                startActivityForResult(intent, REQUESTCODE_ITEM_GROUPS_CHANGED);
                break;

            case R.id.uploaditem_edtDescription:

                break;
            case R.id.et_item_sys_category:// 选择商品分类
//                Intent catIntent = new Intent(vThis, SelectSysCategoryActivity.class);
//                catIntent.putExtra(SelectSysCategoryActivity.EXTRA_SELECTED_STYLE, mItemStyle);
//                startActivityForResult(catIntent, REQUEST_SELECT_ITEM_SYS_CATEGORY);
                break;
            case R.id.ll_uploaditem_btnSpecTag:
                //弹出商品标签popupwindow
                FunctionHelper.hideSoftInput(v.getWindowToken(), vThis);
                mLabelAdapter.notifyDataSetChanged();

                if (mLabelList.size() == 0) {
                    loadDataTask = new LoadDataTask();
                    loadDataTask.execute((Void) null);
                }
                mDataMenu = new ColorSizeSelectMenu(vThis);
                mDataMenu.setSelectAll(isSelectALL);
                mDataMenu.setTitle("选择标签");
                mDataMenu.setInputHint(String.format(getString(R.string.specqty_pw_edtNew_hint), "尺码"));
                mDataMenu.setLabelAdapter(mLabelAdapter);
                mDataMenu.setOperateCallback(new ColorSizeSelectMenu.ColorSizeOperateCallback() {
                    @Override
                    public void selectedItems() {
                        select();
                    }

                    @Override
                    public void deleteItems() {
                        deleteLabel(PopupWindowType.LABEL);

                    }

                    @Override
                    public void addItem(String text) {
                        addLabel(PopupWindowType.LABEL, text);
                    }

                    @Override
                    public void selectAllItems(boolean isSelect) {
                        isSelectALL = isSelect;
                        selectAllLabel(PopupWindowType.LABEL, isSelect);
                    }
                });
                mDataMenu.show(v);
        }
    }

    /**
     * 弹出或关闭PopupWindow
     *
     * @param view 当前被点击的控件
     * @param type 弹出框类型：1.选择照片菜单 2.操作照片菜单
     */
    private void togglePopupWindow(View view, int type) {
        // 隐藏软键盘
//        FunctionHelper.hideSoftInput(view.getWindowToken(), vThis);
//
//
//        switch (type) {
//            case 1://上传图片
//                final UploadItemPopMenu menu = new UploadItemPopMenu(this);
//                menu.setSelectedPicUrls(mAdapter.getItems());
//                menu.setOnMenuItemClickListener(new OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        switch (position) {
//                            case 0:
//                                if (menu.hasSelectedImages()) {
//                                    addSelectedRecentPicsToGrid(menu.getSelectedImgsWithInit());
//                                } else {
//                                    takePhoto();
//                                }
//                                break;
//                            case 1:
//                                fromAblum();
//                                break;
//                        }
//                    }
//                }).show();
//
//                break;
//            case 2://删除图片
//                BottomMenuList menuDelete = new BottomMenuList(this);
//                menuDelete.setItems(getResources().getStringArray(R.array.handle_image_texts))
//                        .setOnMenuItemClickListener(new OnItemClickListener() {
//                            @Override
//                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                switch (position) {
//                                    case 0:
//                                        lookup();
//                                        break;
//                                    case 1:
//                                        mAdapter.remove(mCurrentClickedViewModel);
//                                        showImageContainer();
//                                        break;
//                                    default:
//                                        break;
//                                }
//                            }
//                        }).show();
//
//                break;
//        }

    }

    private void addSelectedRecentPicsToGrid(List<String> paths) {

//        ArrayList<ImageViewModel> imgs = new ArrayList<>();
//        for (String path : paths) {
//            imgs.add(new ImageViewModel(path, path, true));
//        }
//
//        mAdapter.set(imgs);
    }

    /**
     * 调用系统照相机
     */
    private void takePhoto() {
//        if (!SDCardHelper.IsSDCardExists()) {
//            Toast.makeText(vThis, "系统未检测到存储卡，不能使用此功能！", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (!SDCardHelper.checkFileExists(FastCameraActivity.strTakedPhotoDir)) {
//            SDCardHelper.createDirectory(FastCameraActivity.strTakedPhotoDir);
//        }
//        String fileName = TimeUtils.dateToTimeStamp(new Date(), "yyyyMMddHHmmssSSS") + ".jpg";
//        File file = new File(FastCameraActivity.strTakedPhotoDir, fileName);
//        Uri imageUri = Uri.fromFile(file);
//        mPhotoUri = imageUri;
//        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
//        startActivityForResult(cameraIntent, REQUESTCODE_TAKEPHOTO);
    }

    /**
     * 从相册选择照片
     */
    private void fromAblum() {

//        Intent intent = new Intent(vThis, AlbumActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(AlbumActivity.EXTRA_SELECTED_PIC_MODEL, mAdapter.getItems());
//        intent.putExtras(bundle);
//        startActivityForResult(intent, REQUESTCODE_FROMALBUM);

    }

    /**
     * 查看大图
     */
    private void lookup() {

        String url = mCurrentClickedViewModel.getUrl();
        File f = new File(url);
        if (f.exists()) {
            url = "file://" + url;
        } else {
            url = ImageUrlExtends.getImageUrl(url);
        }
        // 查看大图
        Intent intent = new Intent(vThis, ItemImageViewActivity.class);
        intent.putExtra(UploadItemActivity.IMAGE_URL, url);
        startActivity(intent);

    }


    /**
     * @description 本店分类选择完成
     * @created 2015-3-17 下午6:07:27
     * @author ZZB
     */
    @SuppressWarnings("unchecked")
    private void onShopCategorySelected(Intent data) {
        if (data == null) {
            return;
        }
        mSelectedShopCategories = (ArrayList<ItemShopCategory>) data
                .getSerializableExtra(SelectItemShopCategoryActivity.EXTRA_SELECTED_CATS);
        mIsItemOnSale = data.getBooleanExtra(SelectItemShopCategoryActivity.EXTRA_IS_ON_SALE, false);
        mIsItemTop = data.getBooleanExtra(SelectItemShopCategoryActivity.EXTRA_IS_TOP, false);
        //  setShopCatsText();
    }

    private void setShopCatsText() {
        if (mSelectedShopCategories.size() == 0) {
            mEtItemShopCategories.setHint("默认分类");
            return;
        }
        StringBuilder cats = new StringBuilder();
        for (int i = 0; i < mSelectedShopCategories.size(); i++) {
            if (i == 2) {
                break;
            }
            ItemShopCategory cat = mSelectedShopCategories.get(i);
            cats.append(cat.getName()).append("/");
        }
        String catsStr = StringUtils.deleteEndStr(cats.toString(), "/");
        mEtItemShopCategories.setHint(catsStr);
    }

    private void handleImgsFromAlbumn(Intent data) {
//        ArrayList<ImageViewModel> imgs = (ArrayList<ImageViewModel>) data
//                .getSerializableExtra(AlbumActivity.EXTRA_SELECTED_PIC_MODEL);
//        mAdapter.set(imgs);
//        showImageContainer();
    }

    /**
     * 是否显示图片列表
     */
    private void showImageContainer() {
//        gvUploadImage.setVisibility(mAdapter.getPicCount() > 0 ? View.VISIBLE : View.GONE);
//        btnAddImage.setVisibility(mAdapter.getPicCount() == 0 ? View.VISIBLE : View.GONE);
    }

    /**
     * 完成
     */
    private void saveItem() {
        if (!validateInput())
            return;
        // EventBus.getDefault().post(BusEvent.getEvent(EventBusId.ON_UPLOAD_ITEM_CLICK));
        new DbTaskTask().execute();
        //uploadData();
    }

    /**
     * 上传
     */
    private void uploadData() {
        UploadBean newUploadBean = new UploadBean();
        newUploadBean.setType(Type);
        String day = et_single_row.getText().toString();
        if (TextUtils.isEmpty(day))
            day = "0";
        String name = edtDescription.getText().toString().trim();
        String summary = et_item_Summary.getText().toString().trim();
        String price = edtPrice.getText().toString().trim();
        String add_price = add_price_edtPrice.getText().toString();
        String discount = add_discount_edtPrice.getText().toString();
        String dealcount = et_into_group.getText().toString().trim();
        String remark = uploaditem_remark.getText().toString().trim();
        if (TextUtils.isEmpty(add_price))
            add_price = "0.00";
        if (TextUtils.isEmpty(discount))
            discount = "0.00";
        if (TextUtils.isEmpty(dealcount))
            dealcount = "0";
        newUploadBean.setName(name);
        newUploadBean.setRemark(remark);
        newUploadBean.setSummary(summary);
        newUploadBean.setPrice(price);
        newUploadBean.setGroupDealCount(Integer.parseInt(dealcount));
        newUploadBean.setMarkUpValue(parseDouble(add_price));
        newUploadBean.setDiscount(Double.parseDouble(discount));
        UploadBean.StallInfoBean stallInfoBean = new UploadBean.StallInfoBean();
        stallInfoBean.setName(stall_all_name);
        stallInfoBean.setStallID(stallID);
        stallInfoBean.setMarketID(marketID);
        stallInfoBean.setFloorID(floorID);
        newUploadBean.setStallInfo(stallInfoBean);
//        if (adapter!=null){
//          List<LocalMedia> list= adapter.getList();
//            picAllList=list;
//        }
        if (!ListUtils.isEmpty(MaterialList)) {
            newUploadBean.setMaterialList(MaterialList);
        }
        if (!ListUtils.isEmpty(AgeList)) {
            newUploadBean.setAgeList(AgeList);
        }
        if (!ListUtils.isEmpty(StyleList)) {
            newUploadBean.setStyleList(StyleList);
        }
        if (!ListUtils.isEmpty(SeasonList)) {
            newUploadBean.setSeasonList(SeasonList);
        }
        if (!ListUtils.isEmpty(colorPicsBeanList)) {
            newUploadBean.setColorPics(colorPicsBeanList);
        }
        if (!ListUtils.isEmpty(picAllList)) {
            List<UploadBean.MediaBean> list = new ArrayList<>();
            for (int i = 0; i < picAllList.size(); i++) {
                LocalMedia media = picAllList.get(i);
                if (i == 0) {
                    newUploadBean.setCover(media.getPath());
                }
                UploadBean.MediaBean bean = new UploadBean.MediaBean();
                bean.setPath(media.getPath());
                bean.setIs_upload(media.is_upload());
                list.add(bean);
            }
            newUploadBean.setLocal_pics(list);
        }
        if (!ListUtils.isEmpty(videoAllList)) {
            List<UploadBean.MediaBean> list = new ArrayList<>();
            for (LocalMedia media : videoAllList) {
                UploadBean.MediaBean bean = new UploadBean.MediaBean();
                bean.setPath(media.getPath());
                bean.setIs_upload(media.is_upload());
                list.add(bean);
            }
            newUploadBean.setLocal_videos(list);
        }
        UploadBean.CategoryListBean listBean = new UploadBean.CategoryListBean();
        listBean.setID(subID);
        listBean.setParentID(parentID);
        listBean.setName(s_name);
        listBean.setParentName(p_name);
        List<UploadBean.CategoryListBean> categoryListBeen = new ArrayList<>();
        categoryListBeen.add(listBean);
        newUploadBean.setCategoryList(categoryListBeen);
        if (!ListUtils.isEmpty(pListBeanList))
            newUploadBean.setPropertyList(pListBeanList);
        if (!ListUtils.isEmpty(updateList)) {
            List<UploadBean.TagsBean> Tags = new ArrayList<>();
            for (LabelModel LabelModel : updateList) {
                UploadBean.TagsBean tagsBean = new UploadBean.TagsBean();
                tagsBean.setID(LabelModel.getID());
                tagsBean.setName(LabelModel.getName());
                Tags.add(tagsBean);
            }
            newUploadBean.setTags(Tags);
        }
        if (!ListUtils.isEmpty(mColorSizeList)) {
            List<UploadBean.ProductsBean> productsList = new ArrayList<>();
            for (ColorSizeModel colorSizeModel : mColorSizeList) {
                UploadBean.ProductsBean productsBean = new UploadBean.ProductsBean();
                productsBean.setColor(colorSizeModel.getColor().getName());
                productsBean.setSize(colorSizeModel.getSize().getName());
                productsBean.setStock(colorSizeModel.getQty());
                productsList.add(productsBean);
            }
            newUploadBean.setProducts(productsList);
        }
        if (!ListUtils.isEmpty(eList)) {
            List<UploadBean.ExtendPropertyTypeListV2Bean> list = new ArrayList<>();
            for (UploadBean.ExtendPropertyTypeListV2Bean aa : eList) {
                UploadBean.ExtendPropertyTypeListV2Bean v2Bean = new UploadBean.ExtendPropertyTypeListV2Bean();
                List<List<UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean>> v2List = aa.getExtendPropertyList();
                if (!ListUtils.isEmpty(v2List)) {
                    List<List<UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean>> wList = new ArrayList<>();
                    for (List<UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean> v1List : v2List) {
                        if (!ListUtils.isEmpty(v1List)) {
                            List<UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean> sList = new ArrayList<>();
                            for (UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean bb : v1List) {
                                if (bb != null) {
                                    if (bb.isSelect) {
                                        sList.add(bb);
                                    }
                                }
                            }
                            if (!ListUtils.isEmpty(sList)) {
                                wList.add(sList);
                            }
                        }

                    }
                    if (!ListUtils.isEmpty(wList)) {
                        v2Bean.setExtendPropertyList(wList);
                        v2Bean.setTypeID(aa.getTypeID());
                        v2Bean.setMore(aa.isMore());
                        v2Bean.setSelContent(aa.getSelContent());
                        v2Bean.setTypeName(aa.getTypeName());
                        v2Bean.setIsValue(aa.isIsValue());
                        v2Bean.setGrouping(aa.isGrouping());
                        if (v2Bean != null)
                            list.add(v2Bean);
                    }
                }
//                UploadBean.ExtendPropertyTypeListBean bean = new UploadBean.ExtendPropertyTypeListBean();
//                bean.setTypeID(aa.getTypeID());
//                bean.setMore(aa.isMore());
//                bean.setSelContent(aa.getSelContent());
//                bean.setTypeName(aa.getTypeName());
//                bean.setIsValue(aa.isIsValue());
//                List<UploadBean.ExtendPropertyTypeListBean.ExtendPropertyListBean> clist = new ArrayList<>();
//                for (UploadBean.ExtendPropertyTypeListBean.ExtendPropertyListBean bb : aa.getExtendPropertyList()) {
//                    if (bb.isSelect) {
//                        clist.add(bb);
//                    }
//                }
//                if (!ListUtils.isEmpty(clist)) {
//                    bean.setExtendPropertyList(clist);
//                    list.add(bean);
//                }
            }
            newUploadBean.setExtendPropertyTypeListV2(list);
        }
        newUploadBean.setUserId(SpManager.getUserId(this));
        if (chang_type == CHANG_TYPE) {
            if (uploadBean != null) {
                newUploadBean.setCopy(uploadBean.isCopy());
                newUploadBean.setItemID(uploadBean.getItemID());
                if (uploadBean.getSupplyInfo() != null) {
                    UploadBean.SupplyInfoBean supplyInfoBean = new UploadBean.SupplyInfoBean();
                    supplyInfoBean.setDays(Integer.parseInt(day));
                    supplyInfoBean.setTypeID(uploadBean.getSupplyInfo().getTypeID());
                    supplyInfoBean.setUpdateWaitOrderType(uploadBean.getSupplyInfo().getUpdateWaitOrderType());
                    newUploadBean.setSupplyInfo(supplyInfoBean);
                }
            }
            newUploadBean.setCreat_time(uploadBean.getCreat_time());
            uploadManager.removeTask(uploadBean.getCreat_time());
            toolUploadDbHelper.changeUploadItem(newUploadBean);
            uploadManager.addTask(newUploadBean.getCreat_time(), newUploadBean, null);

        } else {
            if (Type == UPLOAD_TYPE) {
                newUploadBean.setCopy(false);
                newUploadBean.setItemID(0);
                UploadBean.SupplyInfoBean supplyInfoBean = new UploadBean.SupplyInfoBean();
                supplyInfoBean.setDays(Integer.parseInt(day));
                supplyInfoBean.setTypeID(0);
                supplyInfoBean.setUpdateWaitOrderType(1);
                newUploadBean.setSupplyInfo(supplyInfoBean);
                newUploadBean.setCreat_time(System.currentTimeMillis() + "");
                //插入数据库
                toolUploadDbHelper.addUploadItem(newUploadBean);
                uploadManager.addTask(newUploadBean.getCreat_time(), newUploadBean, null);
            } else if (Type == EDIT_TYPE) {
                if (uploadBean != null) {
                    newUploadBean.setCopy(uploadBean.isCopy());
                    newUploadBean.setItemID(uploadBean.getItemID());
                    if (uploadBean.getSupplyInfo() != null) {
                        UploadBean.SupplyInfoBean supplyInfoBean = new UploadBean.SupplyInfoBean();
                        supplyInfoBean.setDays(Integer.parseInt(day));
                        supplyInfoBean.setTypeID(uploadBean.getSupplyInfo().getTypeID());
                        supplyInfoBean.setUpdateWaitOrderType(uploadBean.getSupplyInfo().getUpdateWaitOrderType());
                        newUploadBean.setSupplyInfo(supplyInfoBean);
                    }
                }
                newUploadBean.setCreat_time(System.currentTimeMillis() + "");
                //插入数据库
                toolUploadDbHelper.addUploadItem(newUploadBean);
                uploadManager.addTask(newUploadBean.getCreat_time(), newUploadBean, null);
            }

        }

//        Intent intent = new Intent(vThis, UploadItemActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putInt(UploadItemActivity.EXTRA_UPLOAD_TYPE, UploadItemActivity.EDIT_TYPE);
//        bundle.putSerializable(UploadItemActivity.EXTRA_UPLOAD_SHOP_ITEM, newUploadBean);
//        intent.putExtras(bundle);
//        startActivity(intent);

        // new SaveItemTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 验证用户录入
     */
    private boolean validateInput() {
//        if (mAdapter.getPicCount() < 1) {
//            Toast.makeText(vThis, R.string.uploaditem_noImage, Toast.LENGTH_SHORT).show();
//            return false;
//        }
        String name = edtDescription.getText().toString().trim();
        String qty = edtPrice.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            ViewHub.setEditError(edtDescription, "请输入商品名称");
            return false;
        }
        if (ListUtils.isEmpty(picAllList)) {
            ViewHub.showShortToast(vThis, "请添加图片");
            return false;
        }
        if (TextUtils.isEmpty(qty)) {
            ViewHub.setEditError(edtPrice, getString(R.string.uploaditem_edtPrice_empty));
            return false;
        }
//        String retailPrice = edtRetailPrice.getText().toString().trim();
//        if (TextUtils.isEmpty(retailPrice)) {
//            ViewHub.setEditError(edtRetailPrice, getString(R.string.uploaditem_edtRetailPrice_empty));
//            return false;
//        }
        if (stallID <= 0) {
            ViewHub.showShortToast(vThis, "请选择档口号");
            return false;
        }
        if (this.subID <= 0) {
            ViewHub.showShortToast(vThis, "请选择商品分类");
            return false;
        }
        String add_price = add_price_edtPrice.getText().toString();
        if (!TextUtils.isEmpty(qty) && !TextUtils.isEmpty(add_price) && Double.valueOf(qty) < Double.valueOf(add_price)) {
            ViewHub.setEditError(add_price_edtPrice, "加价差额不能大于供货价格");
            return false;
        }

        if (mColorSizeList.size() == 0) {
            ColorSizeModel colorsizeModel = new ColorSizeModel();
            // 颜色
            ColorModel colorModel = new ColorModel();
            colorModel.setName("如图");
            colorsizeModel.setColor(colorModel);
            // 尺码
            SizeModel sizeModel = new SizeModel();
            sizeModel.setName("均码");
            colorsizeModel.setSize(sizeModel);
            // 库存
            colorsizeModel.setQty(2000);
            mColorSizeList.add(colorsizeModel);
        }

//        String description = edtDescription.getText().toString().trim();
//        if (TextUtils.isEmpty(description)) {
//            ViewHub.setEditError(edtDescription, getString(R.string.uploaditem_description_empty));
//            return false;
//        }
//        if (TextUtils.isEmpty(mEtTitle.getText().toString())) {
//            ViewHub.setEditError(mEtTitle, "商品名称不能为空");
//            return false;
//        }
        return true;
    }

    private class DbTaskTask extends AsyncTask<Void, Void, Object> {

        @Override
        protected Object doInBackground(Void... params) {
            try {
                uploadData();
            } catch (Exception ex) {
                ex.printStackTrace();
                return ex.getMessage() == null ? "未知异常" : ex.getMessage();
            }
            return "ok";
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            if (result instanceof String && ((String) result).startsWith("error:")) {
                String msg = ((String) result).replace("error:", "");
                ViewHub.showLongToast(vThis, msg);
                return;
            }
            if (chang_type == CHANG_TYPE) {
                mEventBus.post(BusEvent.getEvent(EventBusId.CHANGE_UPLOADITEM_REFESH_DB));
                finish();
            } else {
                if (Type == UPLOAD_TYPE) {
                    CommDialog.getInstance(vThis).setDialogType(CommDialog.DialogType.D_FINISH).
                            setContent("已添加到上传队列中，上传过程中请保持买手工具一直打开，勿切换到其他应用")
                            .setLeftStr("返回列表").setRightStr("继续添加").setPositive(vThis).showDialog();
                } else if (Type == EDIT_TYPE) {
                    ViewHub.showShortToast(vThis, "已添加到队列中");
                    finish();
                }
            }


        }

    }

    /**
     * 添加商品
     */
    private class SaveItemTask extends AsyncTask<Void, Void, Object> {

        double price;
        double retailPrice;
        String description;
        String Name;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog.start(getString(R.string.uploaditem_submit_loading));
            price = Double.valueOf(edtPrice.getText().toString().trim());
            retailPrice = Double.valueOf(edtRetailPrice.getText().toString().trim());
            description = edtDescription.getText().toString().trim();
            Name = mEtTitle.getText().toString();
        }

        @Override
        protected Object doInBackground(Void... params) {
            try {

                //int picNum = mAdapter.getPicCount();
                // 图片
                String[] images = new String[9];
//                for (int i = 0; i < picNum; i++) {
//                    ImageViewModel item = mAdapter.getItem(i);
//                    images[i] = item.isNewAdd() ? item.getUrl() : item.getWebsite();// //未修改图片，直接保存原图路径
//                }
                // 价格

                // 规格数量
                List<ProductModel> products = new ArrayList<ProductModel>();
                for (ColorSizeModel colorSize : mColorSizeList) {
                    if (colorSize.getColor() == null || colorSize.getSize() == null || colorSize.getQty() <= 0)
                        continue;
                    String color = colorSize.getColor().getName();
                    String size = colorSize.getSize().getName();
                    int stock = colorSize.getQty();
                    ProductModel product = new ProductModel();
                    product.setColor(color);
                    product.setSize(size);
                    product.setPrice(price);
                    product.setStock(stock);
                    products.add(product);
                }
                mItemGroupIds = StringUtils.deleteEndStr(mItemGroupIds, ",");
                if (mItemGroupIds.equals(Const.SystemGroupId.ALL_AGENT)) {
                    mItemGroupIds = "";
                }
                String[] groupIdArr = mItemGroupIds.split(",");
                long[] groupIds = new long[groupIdArr.length];
                int idx = 0;
                for (String id : groupIdArr) {
                    if (!TextUtils.isEmpty(id)) {
                        groupIds[idx] = Long.valueOf(id);
                        idx++;
                    }
                }
                // 描述
                ShopItemModel shopItem = new ShopItemModel();
                //添加标签id
                List<String> tagMaps = mLabelAdapter.getCheckIDsMap();
                if (!ListUtils.isEmpty(tagMaps)) {
                    String tagsId = "";
                    for (String tag : tagMaps) {
                        tagsId += ((int) parseDouble(tag)) + ",";
                    }
                    if (!TextUtils.isEmpty(tagsId)) {
                        shopItem.setItemTagIDS(tagsId);
                    }
                }
                shopItem.setName(StringUtils.substring(Name, 0, 100));
                shopItem.setIntro(description);
                shopItem.setDescription(description);
                shopItem.setWaitDays(waitDay);
                shopItem.setIsWaitOrder(isWaitDay);
                shopItem.setPrice(price);
                shopItem.setRetailPrice(retailPrice);
                shopItem.setImages(images);
                shopItem.setProducts(products);
                shopItem.setGroupIds(mItemGroupIds);
                shopItem.setTop(mIsItemTop);
                shopItem.setShopCatsByItemShopCategory(mSelectedShopCategories);
                if (mIsItemOnSale) {
                    List<CustomModel> itemAttrs = new ArrayList<CustomModel>();
                    itemAttrs.add(new CustomModel(1, ""));
                    shopItem.setItemAttrs(itemAttrs);
                }
                // 这里不用设置，在api里根据实际的groupIds设置
                // shopItem.setIsOnly4Agent(isOnly4Agent());
                if (editID > 0) {
                    shopItem.setIsAdd(false);
                    shopItem.setItemID(editID);
                    shopItem.setUserName(editItem.getUserName());
                    shopItem.setItemSourceType(editItem.getItemSourceType());
                    shopItem.setSourceID(editItem.getSourceID());
                } else {
                    shopItem.setIsAdd(true);
                }

                if (editID > 0) {
                    ArrayList<LabelModel> list = mLabelAdapter.getLabelList();
                    if (!ListUtils.isEmpty(list)) {
                        shopItem.setTags(list);
                    }
                }
                shopItem.setCreateDate(System.currentTimeMillis() + "");
                if (mItemStyle != null) {
                    shopItem.setCat(mItemStyle.getParentId() + "");
                    shopItem.setStyle(mItemStyle.getId());
                }
                shopItem.setUserId(editID > 0 ? editItem.getUserId() : SpManager.getUserId(vThis));
                shopItem.setUniqueTag(shopItem.getUserId() + ":" + System.currentTimeMillis());
                // shopItem.setUploadCounter(6);
                // dbHelper.AddUploadItem(vThis, shopItem);
                mItemCreateTime = shopItem.getCreateDate();
                return shopItem;
            } catch (Exception ex) {
                Log.e(TAG, "保存商品资料发生异常");
                ex.printStackTrace();
                return ex.getMessage() == null ? "未知异常" : ex.getMessage();
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            loadingDialog.stop();
            if (!(result instanceof ShopItemModel)) {
                return;
            }
            if (editID > 0) {
                ShopCategoryCacheManager.clearCache(vThis);
            } else {
                ShopCategoryCacheManager.addCategoryNumByOne(vThis, mSelectedShopCategories);
            }
//            Intent service = new Intent(vThis, UploadItemService2.class);
//            Log.i(UpYunConst.TAG_TEST, "UploadItemActivity.UploadItemService2");
//            startService(service);
//            Intent data = new Intent();
//            data.putExtra(EXTRA_UPLOAD_ITEM_ID, editID);
//            data.putExtra(EXTRA_UPLOAD_ITEM_CREATE_TIME, mItemCreateTime);
//            data.putExtra(EXTRA_UPLOADED_ITEM, (ShopItemModel) result);
//            setResult(RESULTCODE_OK, data);
//            finish();
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

//    /**
//     * 获取表情的gridview的子view
//     *
//     * @return
//     */
//    private void initGridView() {
//        mGvEmotions = (GridView) findViewById(R.id.gridview);
//        mGvEmotions.setTag(false);
//        mEmotionResList = getExpressionRes(100);
//        mEmotionResList.add("delete_expression");
//        final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this, 1, mEmotionResList);
//        mGvEmotions.setAdapter(expressionAdapter);
//        expressionAdapter.setIMojiClickListner(new IMojiClickListener() {
//            @Override
//            public void mojiClick(String filename) {
//                try {
//                    if (filename != "delete_expression") { // 不是删除键，显示表情
//                        String ico = SmileUtils.getSmailStr(filename, vThis);
//                        Spannable icoSpannable = SmileUtils.getSmiledText(vThis, ico);
//                        int startPos = edtDescription.getSelectionStart();
//                        if (startPos < 0 || startPos >= edtDescription.getEditableText().length()) {
//                            edtDescription.append(icoSpannable);
//                        } else {
//                            edtDescription.getEditableText().insert(startPos, icoSpannable);
//                        }
//
//                    } else { // 删除文字或者表情
//                        if (!TextUtils.isEmpty(edtDescription.getText())) {
//                            int selectionStart = edtDescription.getSelectionStart();
//                            if (selectionStart > 0) {
//                                String body = edtDescription.getText().toString();
//                                String tempStr = body.substring(0, selectionStart);
//                                int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
//                                if (i != -1) {
//                                    CharSequence cs = tempStr.substring(i, selectionStart);
//                                    if (SmileUtils.containsKey(cs.toString()))
//                                        edtDescription.getEditableText().delete(i, selectionStart);
//                                    else
//                                        edtDescription.getEditableText().delete(selectionStart - 1, selectionStart);
//                                } else {
//                                    edtDescription.getEditableText().delete(selectionStart - 1, selectionStart);
//                                }
//                            }
//                        }
//
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        //加载标签
//        new TagsTask(Step.GET_TAGS).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//    }

    private void updateSelected(String tag) {
        String description = edtDescription.getText().toString();
        String title = mEtTitle.getText().toString();
        if (description.indexOf(tag) < 0) {
            edtDescription.setText(description + " " + tag);
        }
        if (title.indexOf(tag) < 0) {
            mEtTitle.setText(title + " " + tag);
        }
    }

    public List<String> getExpressionRes(int getSum) {
        List<String> reslist = new ArrayList<String>();
        for (int x = 0; x < getSum; x++) {
            String filename = "qq_" + x;
            reslist.add(filename);
        }
        return reslist;
    }

    /**
     * 获取tag标签
     */
    private class TagsTask extends AsyncTask<Void, Void, Object> {
        private Step mStep;

        public TagsTask(Step _STEP) {
            mStep = _STEP;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            switch (mStep) {
                case GET_TAGS:
                    break;
                case ADD_TAGS:
                case DELETE_TAGS:
                    loadingDialog.start("稍等...");
                    break;
            }
        }

        @Override
        protected Object doInBackground(Void... params) {
            try {
                switch (mStep) {
                    case GET_TAGS:
                        return UploadItemAPI.getInstance().getUserTags(SpManager.getCookie(vThis));
                    case ADD_TAGS:
                        return "OK";
                    case DELETE_TAGS:
                        return "OK";
                }
                return "error:未找到函数";
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
            if (result instanceof String && ((String) result).startsWith("error:")) {
                ViewHub.showLongToast(vThis, ((String) result).replace("error:", ""));
            } else {
                switch (mStep) {
                    case GET_TAGS:
                        tagsLoaded((List<TagModel>) result);
                        break;
                    case ADD_TAGS:
                    case DELETE_TAGS:
                        break;
                }
            }
        }
    }

    private void tagsLoaded(List<TagModel> datas) {
        //冒泡根据sort排序
        for (int i = 0; i < datas.size(); i++) {
            for (int j = i + 1; j < datas.size(); j++) {
                if (datas.get(i).getSort() < datas.get(j).getSort()) {
                    TagModel temp = datas.get(i);
                    datas.set(i, datas.get(j));
                    datas.set(j, temp);
                }
            }
        }
        mTagList = datas;
        //构造一个+号
        TagModel tagAdd = new TagModel();
        tagAdd.setContent("+");
        tagAdd.setID(9999);
        tagAdd.setSort(9999);
        mTagList.add(tagAdd);
        String[] tagContents = new String[mTagList.size()];
        for (int i = 0; i < datas.size(); i++) {
            tagContents[i] = datas.get(i).getContent();
        }
//        final ColorSizeAdapter tagAdapter = new ColorSizeAdapter(vThis,tagContents);
//        mGvTags.setAdapter(tagAdapter);
//        mGvTags.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                TagModel tagModel = mTagList.get(position);
////                String tag = (String) parent.getAdapter().getItem(position);
//                if (tagModel.getID() == 9999 && tagModel.getContent().equals("+")) {//点击添加按钮
//                    tagModel.setSort(0);
//                    tagModel.setID(0);
//                    tagModel.setContent("");
//                    new DialogUploadItemTag(vThis, tagModel).show();
//                } else {
////                    Map<String, Boolean> map = tagAdapter.getSelectedMap();
////                    if (map.containsKey(tagModel.getContent())) {
////                        map.put(tagModel.getContent(), !map.get(tagModel.getContent()).booleanValue());
////                    } else {
////                        map.put(tagModel.getContent(), true);
////                    }
////                    tagAdapter.notifyDataSetChanged();
//                    updateSelected(tagModel.getContent());
//                }
//            }
//        });
//        mGvTags.setOnItemLongClickListener(new OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//                TagModel tagModel = mTagList.get(i);
//                if (tagModel.getID() != 9999 && !tagModel.getContent().equals("+")) {//点击添加按钮
//                    new DialogUploadItemTag(vThis, tagModel).show();
//                }
//                return false;
//            }
//        });
    }


    /**
     * 获取标签列表
     */
    private class LoadDataTask extends AsyncTask<Void, Void, String> {

        private List<LabelModel> labelList_temp;

        @Override
        protected String doInBackground(Void... params) {
            try {
                String cookie = PublicData.getCookie(vThis);
                labelList_temp = UploadItemAPI.getInstance().getLabel(cookie);
                return "OK";
            } catch (Exception ex) {
                Log.e(TAG, "获取标签发生异常");
                ex.printStackTrace();
                return ex.getMessage() == null ? "操作异常" : ex.getMessage();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // loadingDialog.start(getString(R.string.specqty_loaddata_loading));
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // loadingDialog.stop();
            // loadDataTask = null;

            if (result.equals("OK")) {
                if (labelList_temp != null) {
                    // 清空颜色集合中的数据
                    //mLabelList.removeAll(mLabelList);
                    //mLabelAdapter.notifyDataSetChanged();
                    mLabelAdapter.notifyDataSetInvalidated();
                    // 重新添加数据到集合中
                    for (LabelModel color : labelList_temp) {
                        LabelItemModel colorItemModel = new LabelItemModel(false, color);
                        if (!mLabelList.contains(colorItemModel)) {
                            mLabelList.add(colorItemModel);
                        }
                    }
                    mLabelAdapter.notifyDataSetChanged();
                }
                if (!ListUtils.isEmpty(updateList)) {
                    setDefault(updateList, mLabelList);
                }
            } else {
                Toast.makeText(vThis, result, Toast.LENGTH_LONG).show();
            }
        }

    }

    private void setDefault(List<LabelModel> defaultList, List<LabelItemModel> allList) {
        for (LabelItemModel color : allList) {
            for (LabelModel item : defaultList) {
                if (((int) parseDouble(color.getLabel().getID())) == ((int) parseDouble(item.getID()))) {
                    color.setCheck(true);
                } else {
                    continue;
                }
            }
        }
        mLabelAdapter.notifyDataSetChanged();
    }


    /**
     * 展示已选的标签，初始化数据时使用
     */
    private void setLabel(List<LabelItemModel> labelList) {
        // 定义字典数据
        List<String> labelrMap = new ArrayList<String>();
        if (labelrMap.size() > 0) {
            // 重新遍历集合，将数据添加至字典中
            for (LabelItemModel entity : labelList) {
                LabelModel label = entity.getLabel();
                // 获取已选择的颜色
                if (label != null) {
                    String colorName = label.getName();
                    if (!labelrMap.contains(colorName)) {
                        labelrMap.add(colorName);
                    }
                }
                // 获取已选择的尺码
            }
        }
        // 颜色
        String labels = "";
        for (LabelItemModel labelItem : labelList) {
            String name = labelItem.getLabel().getName();
            if (labelrMap.contains(name)) {
                labelItem.setCheck(true);

                if (labels.indexOf(name + "  ") == 0 || labels.contains("  " + name + "  ")) {
                } else {
                    labels += name + "  ";
                }
            }
        }
        mTagsTwo.setText(labels.trim());
        // 刷新适配器
        mLabelAdapter.notifyDataSetChanged();
        // 显示或隐藏空视图
        //toggleEmptyView();
    }


    private void select() {
        // 隐藏软键盘
        // FunctionHelper.hideSoftInput(v.getWindowToken(), vThis);
        // 至少要勾选一项
//        List<String> checkMap = new ArrayList<String>();
//        switch (mPupupWindowType) {
//            case LABEL:
//                checkMap = mLabelAdapter.getCheckMap();
//                break;
//        }
//        if (checkMap.size() == 0) {
//            Toast.makeText(vThis, R.string.specqty_pw_uncheck, Toast.LENGTH_SHORT).show();
//            return;
//        }

        // 选择颜色、尺码
        checkLabel(mPupupWindowType);
    }


    private void checkLabel(PopupWindowType type) {
        List<String> checkMap = new ArrayList<String>();
        switch (type) {
            case LABEL:
                updateList.clear();
                checkMap = mLabelAdapter.getCheckMap();
                // 重新勾选颜色
                String label = "";
                for (LabelItemModel labelItem : mLabelList) {
                    if (checkMap.contains(labelItem.getLabel().getName())) {
                        updateList.add(labelItem.getLabel());
                        labelItem.setCheck(true);
                        if (label.indexOf(labelItem.getLabel().getName() + "  ") == 0
                                || label.contains("  " + labelItem.getLabel().getName() + "  ")) {
                        } else {
                            label += labelItem.getLabel().getName() + "  ";
                        }
                    } else {
                        labelItem.setCheck(false);
                    }
                }
                mTagsTwo.setText(label.trim());
                break;

        }

        // 将标签集合转换为字典
        List<String> colorMap_check = new ArrayList<String>();
        for (LabelItemModel labelItem : mLabelList) {
            boolean isCheck = labelItem.isCheck();
            String name = labelItem.getLabel().getName();
            if (isCheck)
                colorMap_check.add(name);
        }
        // 重新整理颜色尺码集合
//        mColorSizeList = new ArrayList<ColorSizeModel>();
//        // 2.添加新勾选项
//        for (String labelString : colorMap_check) {
//
//        }
        // 刷新适配器
        mLabelAdapter.notifyDataSetChanged();
    }


    /**
     * 删除颜色尺码
     */
    private void deleteLabel(PopupWindowType type) {
        String strType = "";
        // List<String> checkMap = new ArrayList<String>();
        List<String> checkIDsMap = new ArrayList<String>();
        switch (type) {
            case LABEL:
                strType = "标签";
                // checkMap = mColorAdapter.getCheckMap();
                checkIDsMap = mLabelAdapter.getCheckIDsMap();
                break;
        }

        // 未勾选时不能执行删除操作
        if (checkIDsMap.size() == 0) {
            Toast.makeText(vThis, String.format(getString(R.string.specqty_pw_delete_empty), strType),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // 验证网络
        if (!FunctionHelper.CheckNetworkOnline(vThis))
            return;
        // 执行操作
        deleteLabelTask = new DeleteLabelTask(type, checkIDsMap);// checkMap
        deleteLabelTask.execute((Void) null);
    }


    /**
     * 删除标签
     */
    private class DeleteLabelTask extends AsyncTask<Void, Void, String> {

        private PopupWindowType pwType;
        private List<String> deleteMap;

        public DeleteLabelTask(PopupWindowType type, List<String> map) {
            pwType = type;
            deleteMap = map;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                boolean success = false;
                String ids = FunctionHelper.convert2(",", deleteMap);
                String cookie = PublicData.getCookie(vThis);
                switch (pwType) {
                    case LABEL:
                        if (!TextUtils.isEmpty(ids)) {
                            success = UploadItemAPI.getInstance().deleteLabel(ids, cookie);
                        }
                        break;
                }
                if (success) {
                    return "OK";
                } else {
                    return "数据删除失败";
                }
            } catch (Exception ex) {
                String str = "";
                if (pwType == PopupWindowType.LABEL) {
                    str = "标签";
                }
                Log.e(TAG, "删除" + str + "时发生异常");
                ex.printStackTrace();
                return ex.getMessage() == null ? "未知异常" : ex.getMessage();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // ImageView iconLoading = null;
            // switch (pwType) {
            // case COLOR:
            // iconLoading = pwColor_iconLoading;
            // break;
            // case SIZE:
            // iconLoading = pwSize_iconLoading;
            // break;
            // }
            // Animation animation = AnimationUtils.loadAnimation(vThis, R.anim.loading);
            // animation.setInterpolator(new LinearInterpolator());// 匀速旋转
            // iconLoading.setAnimation(animation);
            // animation.start();
            // iconLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // ImageView iconLoading = null;
            // switch (pwType) {
            // case COLOR:
            // iconLoading = pwColor_iconLoading;
            // break;
            // case SIZE:
            // iconLoading = pwSize_iconLoading;
            // break;
            // }
            // iconLoading.clearAnimation();
            // iconLoading.setVisibility(View.GONE);
            deleteLabelTask = null;

            if (result.equals("OK")) {
                if (pwType == PopupWindowType.LABEL) {
                    // 得到刚删除的尺码
                    // List<SizeItemModel> sizeList_deleted = new ArrayList<SizeItemModel>();
                    for (int i = mLabelList.size() - 1; i >= 0; i--) {
                        LabelItemModel labelItem = mLabelList.get(i);
                        if (deleteMap.indexOf(String.valueOf(labelItem.getLabel().getID())) > -1) {
                            // sizeList_deleted.add(sizeItem);
                            mLabelList.remove(labelItem);
                            mLabelAdapter.delete(labelItem);
                            deleteMap.remove(labelItem.getLabel().getID());
                        }
                    }

                    // for (SizeItemModel sizeItem : mSizeList) {
                    //
                    // }
                    // 从集合中移除已被删除的颜色
                    // mSizeList.removeAll(sizeList_deleted);
                    // 刷新适配器
                    Log.v("UploadItemActivity", mLabelList.size() + "");
                    mLabelAdapter.notifyDataSetChanged();
                }

                // 重新初始化选择好的颜色尺码列表
                //checkColorOrSize(pwType);
            } else {
                // 验证result
                if (result.toString().startsWith("401") || result.toString().startsWith("not_registered")) {
                    Toast.makeText(vThis, result.toString(), Toast.LENGTH_LONG).show();
                    ApiHelper.checkResult(result, vThis);
                } else {
                    Toast.makeText(vThis, result, Toast.LENGTH_LONG).show();
                }
            }

        }
    }


    /**
     * 添加标签
     */
    private class AddLabelTask extends AsyncTask<Void, Void, Object> {

        private PopupWindowType pwType;
        private String name;

        public AddLabelTask(PopupWindowType type, String name) {
            this.pwType = type;
            this.name = name;
        }

        @Override
        protected Object doInBackground(Void... params) {
            try {
                Object obj = null;
                String cookie = PublicData.getCookie(vThis);
                switch (pwType) {
                    case LABEL:
                        obj = UploadItemAPI.getInstance().addLabel(name, cookie);
                        break;
                }
                return obj;
            } catch (Exception ex) {
                String str = "";
                if (pwType == PopupWindowType.LABEL) {
                    str = "标签";
                }
                Log.e(TAG, "删除" + str + "时发生异常");
                ex.printStackTrace();
                return ex.getMessage() == null ? "未知异常" : ex.getMessage();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // ImageView iconLoading = null;
            switch (pwType) {
                case LABEL:
                    if (mDataMenu != null && mDataMenu.isShowing()) {
                        mDataMenu.showProgress(true);
                    }
                    break;
            }
            // Animation animation = AnimationUtils.loadAnimation(vThis, R.anim.loading);
            // animation.setInterpolator(new LinearInterpolator());// 匀速旋转
            // iconLoading.setAnimation(animation);
            // animation.start();
            // iconLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            switch (pwType) {
                case LABEL:
                    if (mDataMenu != null && mDataMenu.isShowing()) {
                        mDataMenu.showProgress(false);
                    }
                    break;
            }
            addLabelTask = null;
            if (result instanceof LabelModel) {
                // 添加新标签
                LabelModel label = (LabelModel) result;
                LabelItemModel colorItem = new LabelItemModel(true, label);
                if (!mLabelList.contains(colorItem)) {
                    mLabelList.add(0, colorItem);
                }
                // 刷新适配器
                mLabelAdapter.notifyDataSetChanged();
            } else {
                // 验证result
                if (result.toString().startsWith("401") || result.toString().startsWith("not_registered")) {
                    Toast.makeText(vThis, result.toString(), Toast.LENGTH_LONG).show();
                    ApiHelper.checkResult(result, vThis);
                } else {
                    Toast.makeText(vThis, result.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    /**
     * 选择全部
     */
    private void selectAllLabel(PopupWindowType type, boolean isSelect) {
        // 判断颜色、尺码是否已存在，存在时不允许添加
        boolean isExists = false;
        switch (type) {
            case LABEL:
                for (LabelItemModel labelItem : mLabelList) {
                    labelItem.setCheck(isSelect);
                }
                if (mLabelAdapter != null)
                    mLabelAdapter.notifyDataSetChanged();
                break;
        }

    }

    /**
     * 添加标签
     */
    private void addLabel(PopupWindowType type, String text) {
        String strType = "";
        // EditText edt = null;
        switch (type) {
            case LABEL:
                strType = "标签";
                break;
        }

        // 用户录入验证
        // String name = edt.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(vThis, String.format(getString(R.string.specqty_pw_edtNew_empty), strType),
                    Toast.LENGTH_SHORT).show();
            // edt.requestFocus();
            return;
        } else {
            // 判断颜色、尺码是否已存在，存在时不允许添加
            boolean isExists = false;
            switch (type) {
                case LABEL:
                    for (LabelItemModel labelItem : mLabelList) {
                        if (labelItem.getLabel().getName().equals(text)) {
                            isExists = true;
                            labelItem.setCheck(true);
                            break;
                        }
                    }
                    break;
            }
            if (isExists) {
                Toast.makeText(vThis, String.format(getString(R.string.specqty_pw_edtNew_has_exsits), strType),
                        Toast.LENGTH_SHORT).show();
                if (mLabelAdapter != null)
                    mLabelAdapter.notifyDataSetChanged();
                return;
            }
        }

        // 验证网络
        if (!FunctionHelper.CheckNetworkOnline(vThis))
            return;
        // 执行操作
        addLabelTask = new AddLabelTask(type, text);
        addLabelTask.execute((Void) null);
    }
}

