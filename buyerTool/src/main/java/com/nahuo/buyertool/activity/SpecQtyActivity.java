package com.nahuo.buyertool.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.DoubleUtils;
import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.BaseActivity;
import com.nahuo.buyertool.Bean.UploadBean;
import com.nahuo.buyertool.adapter.ColorGridItemAdapter;
import com.nahuo.buyertool.adapter.ColorPicsAdapter;
import com.nahuo.buyertool.adapter.SizeGridItemAdapter;
import com.nahuo.buyertool.adapter.SpecQtyItemAdapter;
import com.nahuo.buyertool.api.ApiHelper;
import com.nahuo.buyertool.api.UploadItemAPI;
import com.nahuo.buyertool.common.ListUtils;
import com.nahuo.buyertool.controls.ColorSizeSelectMenu;
import com.nahuo.buyertool.dialog.CommDialog;
import com.nahuo.buyertool.model.ColorItemModel;
import com.nahuo.buyertool.model.ColorModel;
import com.nahuo.buyertool.model.ColorSizeModel;
import com.nahuo.buyertool.model.PublicData;
import com.nahuo.buyertool.model.SizeItemModel;
import com.nahuo.buyertool.model.SizeModel;
import com.nahuo.buyertool.utils.ListviewUtls;
import com.nahuo.library.controls.LoadingDialog;
import com.nahuo.library.helper.FunctionHelper;
import com.yalantis.ucrop.UCropMulti;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.nahuo.buyertool.activity.UploadItemActivity.EXTRA_UPLOAD_COLORPICS_ITEM;

public class SpecQtyActivity extends BaseActivity implements OnClickListener, CommDialog.PopDialogListener {

    private static final String TAG = "SpecQtyActivity";
    /**
     * 用于传递已选颜色尺码列表的键
     */
    public static final String COLOR_SIZE_LIST = "com.nahuo.bw.b.SpecQtyActivity.colorSizeList";
    public static final String IS_WAIT_ORDER = "com.nahuo.bw.b.SpecQtyActivity.IS_WAIT_ORDER";
    public static final String WAIT_DAY = "com.nahuo.bw.b.SpecQtyActivity.WAIT_DAY";
    /**
     * 返回已选颜色尺码列表的ResultCode
     */
    public static final int RESULTCODE_OK = 101;
    private SpecQtyActivity vThis = this;

    private LoadingDialog loadingDialog;
    private View colorView, sizeView;
    private ListView lvSpecQty;
    private TextView tvTitle, titlebar_btnRight, tvColors, tvSizes, tvSizeEmptyDesc;
    private ImageView imgOpenClose;
    private EditText etWaitDay;
    private TextView tvWaitDayTips;
    private View waitDayView;
    // private EditText edtColor, edtSize;
    // private GridView gvColor, gvSize;
    private Button btnLeft, btnSave;
    // private ImageView pwColor_iconLoading, pwSize_iconLoading, pwColor_add_iconLoading,
    // pwSize_add_iconLoading;
    // private PopupWindowEx pwColor, pwSize;
    private PopupWindowType mPupupWindowType = PopupWindowType.COLOR;

    private List<ColorItemModel> mColorList = new ArrayList<ColorItemModel>();
    private List<SizeItemModel> mSizeList = new ArrayList<SizeItemModel>();
    // private Set<ColorItemModel> mColorList = new LinkedHashSet<ColorItemModel>();
    // private Set<SizeItemModel> mSizeList = new LinkedHashSet<SizeItemModel>();
    /**
     * 颜色尺码列表集合
     */
    private List<ColorSizeModel> mColorSizeList = new ArrayList<ColorSizeModel>();
    private ColorGridItemAdapter mColorAdapter;
    private SizeGridItemAdapter mSizeAdapter;
    private SpecQtyItemAdapter mSpecQtyItemAdapter;

    private LoadColorTask loadColorTask;
    private LoadSizeTask loadSizeTask;
    private LoadDataTask loadDataTask;
    private AddDataTask addDataTask;
    private DeleteDataTask deleteDataTask;
    private ColorSizeSelectMenu mColorMenu;
    private ColorSizeSelectMenu mSizelMenu;
    private RecyclerView recycler_pics;
    private Intent intent = null;
    private List<UploadBean.ColorPicsBean> colorPicsBeanList = new ArrayList<>();
    private List<UploadBean.ColorPicsBean> picsList = new ArrayList<>();
    private ColorPicsAdapter colorPicsAdapter;
    public static final int REQUEST_SINGLE_PIC = 200;
    // private ColorSizeSelectMenu mColorMenu;
    // private ColorSizeSelectMenu mSizelMenu;
    private List<LocalMedia> singleList = new ArrayList<>();
    private int mPos;
    private Set<String> colorData = new HashSet<>();
    public static String ETR_COLORLIST = "ETR_COLORLIST";
    public static String ETR_SIZELIST = "ETR_SIZELIST";

    @Override
    public void onPopDialogButtonClick(int ok_cancel, CommDialog.DialogType type) {
        switch (type) {
            case D_EXIT:
                if (ok_cancel == CommDialog.BUTTON_POSITIVIE) {
                    returnData();
                } else {
                    finish();
                }
                break;
        }
    }

    public enum PopupWindowType {
        COLOR, SIZE
    }

    Bundle saveState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);// 设置自定义标题栏
        setContentView(R.layout.activity_specqty);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.layout_titlebar_default);// 更换自定义标题栏布局
        saveState = savedInstanceState;
        initSystemModel();
        initView();
        initData();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        onSaveData(outState);
    }

    private void onSaveData(Bundle bundle) {
        // 过滤数量小于等于0的项
        List<ColorSizeModel> colorSizeList_temp = new ArrayList<ColorSizeModel>();
        if (mColorSizeList != null) {
            for (ColorSizeModel colorSize : mColorSizeList) {
                if (colorSize.getQty() <= 0)
                    continue;
                colorSizeList_temp.add(colorSize);
            }
        }
        int day = 0;
        try {
            day = Integer.parseInt(etWaitDay.getText().toString());
            if (day < 0) {
                day = 0;
            }
        } catch (Exception ex) {
            day = 0;
        }
        // 传递已设置好的颜色尺码
        bundle.putSerializable(COLOR_SIZE_LIST, (Serializable) colorSizeList_temp);
        bundle.putSerializable(EXTRA_UPLOAD_COLORPICS_ITEM, (Serializable) colorPicsBeanList);
        bundle.putBoolean(IS_WAIT_ORDER, Integer.parseInt(imgOpenClose.getTag().toString()) != 0);
        bundle.putSerializable(ETR_COLORLIST, (Serializable) mColorList);
        bundle.putSerializable(ETR_SIZELIST, (Serializable) mSizeList);
        bundle.putInt(WAIT_DAY, day);
    }

    ColorPicsAdapter.onAddPicClickListener addPicClickListener = new ColorPicsAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick(int position, View v) {
            mPos = position;
            PictureSelector.create(vThis)
                    .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                    .theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                    // .maxSelectNum(1)// 最大图片选择数量
                    .minSelectNum(1)// 最小选择数量
                    .imageSpanCount(4)// 每行显示个数
                    .selectionMode(
                            PictureConfig.SINGLE)// 多选 or 单选
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
                    .compressGrade(Luban.THIRD_GEAR)
                    //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                    .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                    // .withAspectRatio(4, 1)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                    .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示
                    .isGif(false)// 是否显示gif图片
                    .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
                    .circleDimmedLayer(false)// 是否圆形裁剪
                    .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                    .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                    .openClickSound(false)// 是否开启点击声音
                    .selectionMedia(singleList)// 是否传入已选图片
                    //.previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                    //.cropCompressQuality(90)// 裁剪压缩质量 默认100
                    //.compressMaxKB(200)//压缩最大值kb compressGrade()为Luban.CUSTOM_GEAR有效
                    //.compressWH(1100, 1100) // 压缩宽高比 compressGrade()为Luban.CUSTOM_GEAR有效
                    //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                    //.rotateEnabled() // 裁剪是否可旋转图片
                    //.scaleEnabled()// 裁剪是否可放大缩小图片
                    //.videoQuality()// 视频录制质量 0 or 1
                    //.videoSecond()//显示多少秒以内的视频or音频也可适用
                    //.recordVideoSecond()//录制视频秒数 默认60s
                    .forResult(REQUEST_SINGLE_PIC);//结果回调onActivityResult code
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case UCropMulti.REQUEST_BUYER_MULTI_CROP:
                    //裁截
                    List<LocalMedia> xList = PictureSelector.obtainMultipleResult(data);
                    LocalMedia media = null;
                    if (!ListUtils.isEmpty(xList)) {
                        media = xList.get(0);
                        int mCurrentPos = media.getmCurrentPos();
                        if (!ListUtils.isEmpty(colorPicsBeanList)) {
                            for (int i = 0; i < colorPicsBeanList.size(); i++) {
                                if (mCurrentPos == i) {
                                    colorPicsBeanList.get(i).setIs_upload(false);
                                    colorPicsBeanList.get(i).setUrl(media.getCutPath());
                                    break;
                                }
                            }
                            if (colorPicsAdapter != null)
                                colorPicsAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
                case REQUEST_SINGLE_PIC:
                    singleList = PictureSelector.obtainMultipleResult(data);
                    if (!ListUtils.isEmpty(singleList)) {
                        colorPicsBeanList.get(mPos).setUrl(singleList.get(0).getPath());
                        colorPicsBeanList.get(mPos).setIs_upload(false);
                        if (colorPicsAdapter != null)
                            colorPicsAdapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    }

    private void initSystemModel() {
        // 内置如图，黑、白、灰、绿五个颜色
        String[] colors = getResources().getStringArray(R.array.good_color_default1);
        for (int i = 0; i < colors.length; i++) {
            ColorItemModel cimRT = new ColorItemModel();
            ColorModel cmRT = new ColorModel();
            cmRT.setName(colors[i]);
            cmRT.setID(-1);
            cimRT.setColor(cmRT);
            mColorList.add(cimRT);
        }
        // 内置均码XXS XS S M L XL XXL 均码 8个尺码
        String[] sizes = getResources().getStringArray(R.array.good_size_default1);
        for (int i = 0; i < sizes.length; i++) {
            SizeItemModel simJM = new SizeItemModel();
            SizeModel smJM = new SizeModel();
            smJM.setName(sizes[i]);
            smJM.setID(-1);
            simJM.setSize(smJM);
            mSizeList.add(simJM);

        }

        // ColorItemModel cimRT = new ColorItemModel();
        // ColorModel cmRT = new ColorModel();
        // cmRT.setName("如图");
        // cmRT.setID(-1);
        // cimRT.setColor(cmRT);
        // mColorList.add(cimRT);
        //
        // ColorItemModel cim = new ColorItemModel();
        // ColorModel cm = new ColorModel();
        // cm.setName("黑色");
        // cm.setID(-1);
        // cim.setColor(cm);
        // mColorList.add(cim);
        //
        // ColorItemModel cim2 = new ColorItemModel();
        // ColorModel cm2 = new ColorModel();
        // cm2.setName("白色");
        // cm2.setID(-1);
        // cim2.setColor(cm2);
        // mColorList.add(cim2);
        //
        // ColorItemModel cim3 = new ColorItemModel();
        // ColorModel cm3 = new ColorModel();
        // cm3.setName("灰色");
        // cm3.setID(-1);
        // cim3.setColor(cm3);
        // mColorList.add(cim3);
        //
        // ColorItemModel cim4 = new ColorItemModel();
        // ColorModel cm4 = new ColorModel();
        // cm4.setName("绿色");
        // cm4.setID(-1);
        // cim4.setColor(cm4);
        // mColorList.add(cim4);
        //
        // // 内置均码，XS S M L XL XXL 均码 8个尺码
        //
        // SizeItemModel simJM = new SizeItemModel();
        // SizeModel smJM = new SizeModel();
        // smJM.setName("XS");
        // smJM.setID(-1);
        // simJM.setSize(smJM);
        // mSizeList.add(simJM);
        //
        // SizeItemModel sim = new SizeItemModel();
        // SizeModel sm = new SizeModel();
        // sm.setName("XS");
        // sm.setID(-1);
        // sim.setSize(sm);
        // mSizeList.add(sim);
        //
        // SizeItemModel sim1 = new SizeItemModel();
        // SizeModel sm1 = new SizeModel();
        // sm1.setName("S");
        // sm1.setID(-1);
        // sim1.setSize(sm1);
        // mSizeList.add(sim1);
        //
        // SizeItemModel sim2 = new SizeItemModel();
        // SizeModel sm2 = new SizeModel();
        // sm2.setName("M");
        // sm2.setID(-1);
        // sim2.setSize(sm2);
        // mSizeList.add(sim2);
        //
        // SizeItemModel sim3 = new SizeItemModel();
        // SizeModel sm3 = new SizeModel();
        // sm3.setName("L");
        // sm3.setID(-1);
        // sim3.setSize(sm3);
        // mSizeList.add(sim3);
        //
        // SizeItemModel sim4 = new SizeItemModel();
        // SizeModel sm4 = new SizeModel();
        // sm4.setName("XL");
        // sm4.setID(-1);
        // sim4.setSize(sm4);
        // mSizeList.add(sim4);
        //
        // SizeItemModel sim5 = new SizeItemModel();
        // SizeModel sm5 = new SizeModel();
        // sm5.setName("XXL");
        // sm5.setID(-1);
        // sim5.setSize(sm5);
        // mSizeList.add(sim5);
        //
        // SizeItemModel sim6 = new SizeItemModel();
        // SizeModel sm6 = new SizeModel();
        // sm6.setName("均码");
        // sm6.setID(-1);
        // sim6.setSize(sm6);
        // mSizeList.add(sim6);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        loadingDialog = new LoadingDialog(vThis);
        // 标题栏
        titlebar_btnRight = (TextView) findViewById(R.id.titlebar_btnRight);
        titlebar_btnRight.setText("保存");
        recycler_pics = (RecyclerView) findViewById(R.id.recycler_pics);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(vThis, 4, GridLayoutManager.VERTICAL, false);
        recycler_pics.setLayoutManager(manager);
        colorPicsAdapter = new ColorPicsAdapter(this, addPicClickListener);
        recycler_pics.setAdapter(colorPicsAdapter);
        colorPicsAdapter.setOnItemClickListener(new ColorPicsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (colorPicsBeanList.size() > 0) {
                    List<LocalMedia> picAllList = new ArrayList<>();
                    for (UploadBean.ColorPicsBean bean : colorPicsBeanList) {
                        LocalMedia media = new LocalMedia();
                        media.setIs_upload(bean.is_upload());
                        media.setPath(bean.getUrl());
                        picAllList.add(media);
                    }
                    if (!DoubleUtils.isFastDoubleClick()) {
                        Intent intent = new Intent(vThis, PictureExternalPreviewToolActivity.class);
                        intent.putExtra(PictureConfig.EXTRA_PREVIEW_SELECT_LIST, (Serializable) picAllList);
                        intent.putExtra(PictureConfig.EXTRA_POSITION, position);
                        vThis.startActivityForResult(intent, UCropMulti.REQUEST_BUYER_MULTI_CROP);
                        vThis.overridePendingTransition(R.anim.a5, 0);
                    }
                }
            }
        });
        titlebar_btnRight.setOnClickListener(this);
        tvTitle = (TextView) findViewById(R.id.titlebar_tvTitle);
        btnLeft = (Button) findViewById(R.id.titlebar_btnLeft);
        tvTitle.setText(R.string.title_activity_specqty);
        btnLeft.setText(R.string.titlebar_btnBack);
        btnLeft.setVisibility(View.VISIBLE);
        // 界面布局
        colorView = findViewById(R.id.specqty_colorView);
        sizeView = findViewById(R.id.specqty_sizeView);
        lvSpecQty = (ListView) findViewById(R.id.specqty_lvSpecQty);
        tvSizes = (TextView) findViewById(R.id.specqty_tvSizes);
        tvColors = (TextView) findViewById(R.id.specqty_tvColors);
        btnSave = (Button) findViewById(R.id.specqty_btnSave);
        waitDayView = (View) findViewById(R.id.specqty_openclose_view);
        imgOpenClose = (ImageView) findViewById(R.id.specqty_img_openclose);
        imgOpenClose.setTag(0);
        imgOpenClose.setOnClickListener(this);
        etWaitDay = (EditText) findViewById(R.id.specqty_img_pd_day);
        tvWaitDayTips = (TextView) findViewById(R.id.specqty_img_pd_day_text);
        // 初始化选择颜色的弹出框
        // View colorPopupView = getColorOrSizeView(PopupWindowType.COLOR);
        // pwColor = new PopupWindowEx(colorPopupView, R.id.specqty_pw, LayoutParams.MATCH_PARENT,
        // LayoutParams.WRAP_CONTENT, true);
        // pwColor.setAnimationStyle(R.style.PopupBottomAnimation);
        // // 初始化选择尺码的弹出框
        // View sizePopupView = getColorOrSizeView(PopupWindowType.SIZE);
        // pwSize = new PopupWindowEx(sizePopupView, R.id.specqty_pw, LayoutParams.MATCH_PARENT,
        // LayoutParams.WRAP_CONTENT, true);
        // pwSize.setAnimationStyle(R.style.PopupBottomAnimation);

        // 添加事件
        colorView.setOnClickListener(this);
        sizeView.setOnClickListener(this);
        btnLeft.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        toggleEmptyView();
    }

    /**
     * 初始化颜色、尺码的PopupWindow视图
     * */
    // private View getColorOrSizeView(PopupWindowType pwType) {
    // View view = LayoutInflater.from(vThis).inflate(R.layout.layout_pw_color_or_size, null);
    // TextView tvTitle = (TextView)view.findViewById(R.id.specqty_pw_title);
    // ImageView iconLoading = (ImageView)view.findViewById(R.id.specqty_pw_icon_loading);
    // ImageView iconAddLoading = (ImageView)view.findViewById(R.id.specqty_pw_icon_add_loading);
    // EditText edtNew = (EditText)view.findViewById(R.id.specqty_pw_edtNew);
    //
    // GridView gridView = (GridView)view.findViewById(R.id.specqty_pw_gridview);
    // Button btnAdd = (Button)view.findViewById(R.id.specqty_pw_btnAdd);
    // Button btnDel = (Button)view.findViewById(R.id.specqty_pw_btnDel);
    // Button btnOK = (Button)view.findViewById(R.id.specqty_pw_btnOK);
    //
    // String name = "";
    // switch (pwType) {
    // case COLOR:
    // name = "颜色";
    // pwColor_iconLoading = iconLoading;
    // pwColor_add_iconLoading = iconAddLoading;
    // edtColor = edtNew;
    // edtColor.setHint(String.format(getString(R.string.specqty_pw_edtNew_hint), name));
    // gvColor = gridView;
    // break;
    // case SIZE:
    // name = "尺码";
    // pwSize_iconLoading = iconLoading;
    // pwSize_add_iconLoading = iconAddLoading;
    // edtSize = edtNew;
    // edtSize.setHint(String.format(getString(R.string.specqty_pw_edtNew_hint), name));
    // gvSize = gridView;
    // break;
    // }
    //
    // tvTitle.setText(String.format(getString(R.string.specqty_pw_title), name));
    //
    // // 注册事件
    // btnAdd.setOnClickListener(this);
    // btnDel.setOnClickListener(this);
    // btnOK.setOnClickListener(this);
    // return view;
    // }

    /**
     * 显示或隐藏空视图
     */
    private void toggleEmptyView() {
        // tvSizeEmptyDesc.setVisibility(mSizeList.size() == 0 ? View.VISIBLE : View.GONE);
        // gvColor.setVisibility(mColorList.size() > 0 ? View.VISIBLE : View.GONE);
        // gvSize.setVisibility(mSizeList.size() > 0 ? View.VISIBLE : View.GONE);
        lvSpecQty.setVisibility(mColorSizeList.size() > 0 ? View.VISIBLE : View.GONE);
        // btnSave.setVisibility(mColorSizeList.size() > 0 ? View.VISIBLE : View.GONE);
        titlebar_btnRight.setVisibility(mColorSizeList.size() > 0 ? View.VISIBLE : View.GONE);
        //waitDayView.setVisibility(mColorSizeList.size() > 0 ? View.VISIBLE : View.GONE);
        ListviewUtls.setListViewHeightBasedOnChildren(lvSpecQty);
    }

    List<ColorSizeModel> colorSizeList;

    /**
     * 初始化数据
     */
    private void initData() {
        intent = getIntent();
        if (intent != null) {
            if (saveState != null) {
                colorSizeList = (List<ColorSizeModel>) saveState.getSerializable(
                        COLOR_SIZE_LIST);
            } else {
                colorSizeList = (List<ColorSizeModel>) intent.getSerializableExtra(
                        UploadItemActivity.COLOR_SIZE_LIST);
            }
            List<UploadBean.ColorPicsBean> picsBeanList = (List<UploadBean.ColorPicsBean>) intent.getSerializableExtra(EXTRA_UPLOAD_COLORPICS_ITEM);
            if (!ListUtils.isEmpty(picsBeanList)) {
                picsList.clear();
                picsList.addAll(picsBeanList);
            }
            if (colorSizeList != null) {
                mColorSizeList = colorSizeList;
            }
        }
        if (ListUtils.isEmpty(picsList)) {
            //传递过来数据空
            //设置主色卡
            UploadBean.ColorPicsBean colorPicsBean = new UploadBean.ColorPicsBean();
            colorPicsBean.setUrl("");
            colorPicsBean.setColor("");
            colorPicsBean.setIs_upload(true);
            colorPicsBeanList.add(colorPicsBean);
            //添加其他列表色卡
            if (!ListUtils.isEmpty(mColorSizeList)) {
                List<String> cList = new ArrayList<>();
                for (ColorSizeModel model : mColorSizeList) {
                    if (!cList.contains(model.getColor().getName()))
                        cList.add(model.getColor().getName());
                }
                for (String col : cList) {
                    UploadBean.ColorPicsBean xcolorPicsBean = new UploadBean.ColorPicsBean();
                    xcolorPicsBean.setUrl("");
                    xcolorPicsBean.setColor(col);
                    xcolorPicsBean.setIs_upload(true);
                    colorPicsBeanList.add(xcolorPicsBean);
                }
            }
        } else {
            if (!ListUtils.isEmpty(mColorSizeList)) {
                //设置主色卡
                UploadBean.ColorPicsBean colorPicsBean = new UploadBean.ColorPicsBean();
                colorPicsBean.setUrl("");
                colorPicsBean.setColor("");
                colorPicsBeanList.add(colorPicsBean);
                List<String> cList = new ArrayList<>();
                for (ColorSizeModel model : mColorSizeList) {
                    if (!cList.contains(model.getColor().getName()))
                        cList.add(model.getColor().getName());
                }
                for (UploadBean.ColorPicsBean bean : picsList) {
                    if (TextUtils.isEmpty(bean.getColor())) {
                        colorPicsBean.setUrl(bean.getUrl());
                        colorPicsBean.setIs_upload(bean.is_upload());
                        break;
                    }
                }
                for (int i = 0; i < cList.size(); i++) {
                    UploadBean.ColorPicsBean colorPicsBean2 = new UploadBean.ColorPicsBean();
                    colorPicsBean2.setUrl("");
                    colorPicsBean2.setColor(cList.get(i));
                    for (UploadBean.ColorPicsBean bean : picsList) {
                        if (cList.get(i).equals(bean.getColor()) && !TextUtils.isEmpty(bean.getColor())) {
                            colorPicsBean2.setUrl(bean.getUrl());
                            colorPicsBean2.setColor(bean.getColor());
                            colorPicsBean2.setIs_upload(bean.is_upload());
                        }
                    }
                    colorPicsBeanList.add(colorPicsBean2);
                }
            } else {//传递颜色列表数据没有
                //设置主色卡
                UploadBean.ColorPicsBean colorPicsBean = new UploadBean.ColorPicsBean();
                colorPicsBean.setUrl("");
                colorPicsBean.setColor("");
                for (UploadBean.ColorPicsBean bean : picsList) {
                    if (TextUtils.isEmpty(bean.getColor())) {
                        colorPicsBean.setUrl(bean.getUrl());
                        colorPicsBean.setIs_upload(bean.is_upload());
                        break;
                    }
                }
                colorPicsBeanList.add(colorPicsBean);
            }
        }
        int waitDay = getIntent().getIntExtra(
                UploadItemActivity.WAIT_DAY, 0);
        boolean isWaitDay = getIntent().getBooleanExtra(
                UploadItemActivity.IS_WAIT_ORDER, false);
        if (saveState != null) {
            colorPicsBeanList = (List<UploadBean.ColorPicsBean>) saveState.getSerializable(
                    UploadItemActivity.EXTRA_UPLOAD_COLORPICS_ITEM);
            waitDay = saveState.getInt(
                    UploadItemActivity.WAIT_DAY, 0);
            isWaitDay = saveState.getBoolean(
                    UploadItemActivity.IS_WAIT_ORDER, false);
            mColorList = (List<ColorItemModel>) saveState.getSerializable(ETR_COLORLIST);
            mSizeList = (List<SizeItemModel>) saveState.getSerializable(ETR_SIZELIST);
        }
        colorPicsAdapter.setList(colorPicsBeanList);
        colorPicsAdapter.notifyDataSetChanged();
        etWaitDay.setText(waitDay + "");
        if (isWaitDay) {
            imgOpenClose.setTag(1);
            imgOpenClose.setImageResource(R.drawable.open_icon);
            etWaitDay.setVisibility(View.VISIBLE);
            tvWaitDayTips.setVisibility(View.VISIBLE);
        } else {
            imgOpenClose.setTag(0);
            imgOpenClose.setImageResource(R.drawable.close_icon);
            etWaitDay.setVisibility(View.INVISIBLE);
            tvWaitDayTips.setVisibility(View.INVISIBLE);
        }

        // 初始化适配器
        // 1.选择颜色
        mColorAdapter = new ColorGridItemAdapter(vThis, mColorList);
        mColorAdapter.setOnColorItemClickListener(new ColorGridItemAdapter.OnColorItemClickListener() {
            @Override
            public void onCheckChanged(View v, boolean isChecked) {
            }
        });
        // gvColor.setAdapter(mColorAdapter);
        // 2.选择尺码
        mSizeAdapter = new SizeGridItemAdapter(vThis, mSizeList);
        mSizeAdapter.setOnSizeItemClickListener(new SizeGridItemAdapter.OnSizeItemClickListener() {
            @Override
            public void onCheckChanged(View v, boolean isChecked) {
            }
        });
        // gvSize.setAdapter(mSizeAdapter);
        // 3.颜色尺码列表
        mSpecQtyItemAdapter = new SpecQtyItemAdapter(vThis, mColorSizeList);
        mSpecQtyItemAdapter.setOnSpecQtyItemClickListener(new SpecQtyItemAdapter.OnSpecQtyItemClickListener() {
            @Override
            public void onItemRemove(int position) { // 删除
                // 获取当前要删除的项
                ColorSizeModel entity = mColorSizeList.get(position);
                ColorModel color = entity.getColor();
                SizeModel size = entity.getSize();
                mColorSizeList.remove(position);

                // 获取列表中与要删除的颜色ID或尺码ID一致的项的数量
                int colorCount = 0, sizeCount = 0;
                Set<String> colorList = new HashSet<>();
                for (ColorSizeModel colorSize : mColorSizeList) {
                    colorList.add(colorSize.getColor().getName());
                    if (TextUtils.equals(colorSize.getColor().getName(), color.getName()))
                        colorCount++;
                    if (TextUtils.equals(colorSize.getSize().getName(), size.getName()))
                        sizeCount++;
                }
                if (!ListUtils.isEmpty(colorPicsBeanList)) {
                    for (int i = 0; i < colorPicsBeanList.size(); i++) {
                        UploadBean.ColorPicsBean bean = colorPicsBeanList.get(i);
                        if (!colorList.contains(bean.getColor()) && !TextUtils.isEmpty(bean.getColor())) {
                            colorPicsBeanList.remove(i);
                        }
                    }
                    if (colorPicsAdapter != null)
                        colorPicsAdapter.notifyDataSetChanged();
                }
                // 如果列表中不存在移除了的颜色ID，则取消勾选此颜色
                if (colorCount == 0) {
                    for (ColorItemModel colorItem : mColorList) {
                        if (colorItem.isCheck() && TextUtils.equals(colorItem.getColor().getName(), color.getName())) {
                            colorItem.setCheck(false);
                        }
                    }
                }
                // 如果列表中不存在移除了的尺码ID，则取消勾选此尺码
                if (sizeCount == 0) {
                    for (SizeItemModel sizeItem : mSizeList) {
                        if (sizeItem.isCheck() && TextUtils.equals(sizeItem.getSize().getName(), size.getName())) {
                            sizeItem.setCheck(false);
                        }
                    }
                }
                // 展示颜色
                String colors = "";
                for (ColorItemModel colorItem : mColorList) {
                    if (colorItem.isCheck())
                        colors += colorItem.getColor().getName() + "  ";
                }
                tvColors.setText(colors.trim());
                // 展示尺码
                String sizes = "";
                for (SizeItemModel sizeItem : mSizeList) {
                    if (sizeItem.isCheck())
                        sizes += sizeItem.getSize().getName() + "  ";
                }
                tvSizes.setText(sizes.trim());
                // 刷新适配器
                mColorAdapter.notifyDataSetChanged();
                mSizeAdapter.notifyDataSetChanged();
                mSpecQtyItemAdapter.notifyDataSetChanged();
                toggleEmptyView();
            }
        });
        lvSpecQty.setAdapter(mSpecQtyItemAdapter);
        // 加载颜色、尺码
        loadDataTask = new LoadDataTask();
        loadDataTask.execute();
    }

    /**
     * 展示已选的颜色、尺码，初始化数据时使用
     */
    private void setColorAndSize(List<ColorSizeModel> colorSizeList) {
        // 定义字典数据
        List<String> colorMap = new ArrayList<String>();
        List<String> sizeMap = new ArrayList<String>();
        if (colorSizeList.size() > 0) {
            // 重新遍历集合，将数据添加至字典中
            for (ColorSizeModel entity : colorSizeList) {
                ColorModel color = entity.getColor();
                SizeModel size = entity.getSize();
                // 获取已选择的颜色
                if (color != null) {
                    String colorName = color.getName();
                    if (!colorMap.contains(colorName)) {
                        colorMap.add(colorName);
                    }
                }
                // 获取已选择的尺码
                if (size != null) {
                    String sizeName = size.getName();
                    if (!sizeMap.contains(sizeName)) {
                        sizeMap.add(sizeName);
                    }
                }
            }
        }
        // 颜色
        String colors = "";
        for (ColorItemModel colorItem : mColorList) {
            String name = colorItem.getColor().getName();
            if (colorMap.contains(name)) {
                colorItem.setCheck(true);

                if (colors.indexOf(name + "  ") == 0 || colors.contains("  " + name + "  ")) {
                } else {
                    colors += name + "  ";
                }
            }
        }
        tvColors.setText(colors.trim());
        // 尺码
        String sizes = "";
        for (SizeItemModel sizeItem : mSizeList) {
            String name = sizeItem.getSize().getName();
            if (sizeMap.contains(name)) {
                sizeItem.setCheck(true);
                if (sizes.indexOf(name + "  ") == 0 || sizes.contains("  " + name + "  ")) {
                } else {
                    sizes += name + "  ";
                }
            }
        }
        tvSizes.setText(sizes);
        // 刷新适配器
        mColorAdapter.notifyDataSetChanged();
        mSizeAdapter.notifyDataSetChanged();
        mSpecQtyItemAdapter.notifyDataSetChanged();
        // 显示或隐藏空视图
        toggleEmptyView();
    }

    // private static int showing = 1;
    // private static int showed = 2;
    // private Handler myHandler = new Handler() {
    // @Override
    // public void handleMessage(Message msg) {
    // if (msg.what == showing) {
    // PopwindowShowing = true;
    // } else if (msg.what == showed) {
    // PopwindowShowing = false;
    // }
    // }
    //
    // };

    // private static boolean PopwindowShowing = false; // 互斥变量，用于避免在弹框过程中，再次弹出另一个
    private boolean isSelectColor, isSelectSize;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.specqty_img_openclose:
                if ((int) imgOpenClose.getTag() == 0) {
                    imgOpenClose.setTag(1);
                    imgOpenClose.setImageResource(R.drawable.open_icon);
                    etWaitDay.setVisibility(View.VISIBLE);
                    tvWaitDayTips.setVisibility(View.VISIBLE);
                } else {
                    imgOpenClose.setTag(0);
                    imgOpenClose.setImageResource(R.drawable.close_icon);
                    etWaitDay.setVisibility(View.INVISIBLE);
                    tvWaitDayTips.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.titlebar_btnLeft: // 返回
                CommDialog.getInstance(vThis).setDialogType(CommDialog.DialogType.D_EXIT).setContent("是否保存退出").setLeftStr("不保存退出").setRightStr("保存退出").setPositive(vThis).showDialog();

                // finish();
                break;
            case R.id.specqty_colorView: // 选择颜色

                // if (PopwindowShowing) {
                // return;
                // }

                // new Thread(new Runnable() {
                //
                // @Override
                // public void run() {
                // try {
                // Thread.sleep(2000);
                // } catch (InterruptedException e) {
                // e.printStackTrace();
                // }
                //
                // myHandler.sendMessage(myHandler.obtainMessage(showed));
                // }
                // }).start();

                // myHandler.sendMessage(myHandler.obtainMessage(showing));
                // 隐藏软键盘
                FunctionHelper.hideSoftInput(v.getWindowToken(), vThis);
                // pwColor.showAtLocation(findViewById(R.id.specqty), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                mColorAdapter.notifyDataSetChanged();
                mPupupWindowType = PopupWindowType.COLOR;
                // 为获取到颜色列表的情况下重新获取一下
                if (mColorList.size() == 4) {
                    loadColorTask = new LoadColorTask();
                    loadColorTask.execute((Void) null);
                }

                mColorMenu = new ColorSizeSelectMenu(vThis);
                mColorMenu.setTitle("选择颜色");
                mColorMenu.setSelectAll(isSelectColor);
                mColorMenu.setInputHint(String.format(getString(R.string.specqty_pw_edtNew_hint), "颜色"));
                mColorMenu.setColorAdatper(mColorAdapter);
                mColorMenu.setOperateCallback(new ColorSizeSelectMenu.ColorSizeOperateCallback() {
                    @Override
                    public void selectedItems() {
                        select();
                    }

                    @Override
                    public void deleteItems() {
                        // 执行删除操作
                        deleteData(PopupWindowType.COLOR);
                    }

                    @Override
                    public void addItem(String text) {
                        addData(PopupWindowType.COLOR, text);
                    }

                    @Override
                    public void selectAllItems(boolean isSelect) {
                        isSelectColor = isSelect;
                        selectAllData(PopupWindowType.COLOR, isSelect);
                    }
                });
                mPupupWindowType = PopupWindowType.COLOR;
                mColorMenu.show(v);
                break;
            case R.id.specqty_sizeView: // 选择尺码
                // if (PopwindowShowing) {
                // return;
                // }
                //
                // new Thread(new Runnable() {
                //
                // @Override
                // public void run() {
                // try {
                // Thread.sleep(2000);
                // } catch (InterruptedException e) {
                // e.printStackTrace();
                // }
                //
                // myHandler.sendMessage(myHandler.obtainMessage(showed));
                // }
                // }).start();
                // myHandler.sendMessage(myHandler.obtainMessage(showing));

                // 隐藏软键盘
                FunctionHelper.hideSoftInput(v.getWindowToken(), vThis);
                // pwSize.showAtLocation(findViewById(R.id.specqty), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

                mSizeAdapter.notifyDataSetChanged();
                mPupupWindowType = PopupWindowType.SIZE;
                // 为获取到尺码列表的情况下重新获取一下
                if (mSizeList.size() == 7) {
                    loadSizeTask = new LoadSizeTask();
                    loadSizeTask.execute((Void) null);
                }

                mSizelMenu = new ColorSizeSelectMenu(vThis);
                mSizelMenu.setTitle("选择尺码");
                mSizelMenu.setSelectAll(isSelectSize);
                mSizelMenu.setInputHint(String.format(getString(R.string.specqty_pw_edtNew_hint), "尺码"));
                mSizelMenu.setSizeAdatper(mSizeAdapter);
                mSizelMenu.setOperateCallback(new ColorSizeSelectMenu.ColorSizeOperateCallback() {
                    @Override
                    public void selectedItems() {
                        select();
                    }

                    @Override
                    public void deleteItems() {
                        deleteData(PopupWindowType.SIZE);
                    }

                    @Override
                    public void addItem(String text) {
                        addData(PopupWindowType.SIZE, text);
                    }

                    @Override
                    public void selectAllItems(boolean isSelect) {
                        isSelectSize = isSelect;
                        selectAllData(PopupWindowType.SIZE, isSelect);
                    }
                });
                mPupupWindowType = PopupWindowType.SIZE;
                mSizelMenu.show(v);

                break;
            case R.id.specqty_pw_btnAdd:// 添加颜色、尺码
                // addData(mPupupWindowType);
                break;
            case R.id.specqty_pw_btnDel:// 删除颜色、尺码
                // 隐藏软键盘
                FunctionHelper.hideSoftInput(v.getWindowToken(), vThis);
                // 执行删除操作
                deleteData(mPupupWindowType);
                break;
            case R.id.specqty_pw_btnOK: // 选择颜色、尺码
                // 隐藏软键盘
                FunctionHelper.hideSoftInput(v.getWindowToken(), vThis);

                // 至少要勾选一项
                List<String> checkMap = new ArrayList<String>();
                switch (mPupupWindowType) {
                    case COLOR:
                        checkMap = mColorAdapter.getCheckMap();
                        break;
                    case SIZE:
                        checkMap = mSizeAdapter.getCheckMap();
                        break;
                }
                if (checkMap.size() == 0) {
                    Toast.makeText(vThis, R.string.specqty_pw_uncheck, Toast.LENGTH_SHORT).show();
                    return;
                }

                // 选择颜色、尺码
                checkColorOrSize(mPupupWindowType);
                // 关闭弹出框
                // if (mPupupWindowType == PopupWindowType.COLOR) {
                // pwColor.dismiss();
                // } else if (mPupupWindowType == PopupWindowType.SIZE) {
                // pwSize.dismiss();
                // }
                break;
            case R.id.specqty_btnSave: // 保存
                returnData();
                break;
            case R.id.titlebar_btnRight:
                returnData();
                break;
        }
    }

    private void select() {
        // 隐藏软键盘
//         FunctionHelper.hideSoftInput(v.getWindowToken(), vThis);
//         至少要勾选一项
        List<String> checkMap = new ArrayList<String>();
        switch (mPupupWindowType) {
            case COLOR:
                checkMap = mColorAdapter.getCheckMap();
                break;
            case SIZE:
                checkMap = mSizeAdapter.getCheckMap();
                break;
        }
        if (checkMap.size() == 0) {
            Toast.makeText(vThis, R.string.specqty_pw_uncheck, Toast.LENGTH_SHORT).show();
            return;
        }

        // 选择颜色、尺码
        checkColorOrSize(mPupupWindowType);
    }

    /**
     * 监听返回键，处理返回操作
     */
    @Override
    public void onBackPressed() {
        CommDialog.getInstance(vThis).setDialogType(CommDialog.DialogType.D_EXIT).setContent("是否保存退出").setLeftStr("不保存退出").setRightStr("保存退出").setPositive(vThis).showDialog();

    }

    /**
     * 添加颜色、尺码
     */
    private void selectAllData(PopupWindowType type, boolean isSelect) {
        switch (type) {
            case COLOR:
                for (ColorItemModel colorItem : mColorList) {
                    if (!colorItem.getColor().getName().equals("如图"))
                    colorItem.setCheck(isSelect);
                }
                if (mColorAdapter != null) {
                    mColorAdapter.notifyDataSetChanged();
                }
                break;
            case SIZE:
                for (SizeItemModel sizeItem : mSizeList) {
                    if (!sizeItem.getSize().getName().equals("均码"))
                        sizeItem.setCheck(isSelect);
                    if (mSizeAdapter != null)
                        mSizeAdapter.notifyDataSetChanged();
                }
                break;
        }

    }

    /**
     * 添加颜色、尺码
     */
    private void addData(PopupWindowType type, String text) {
        String strType = "";
        // EditText edt = null;
        switch (type) {
            case COLOR:
                strType = "颜色";
                // edt = edtColor;
                break;
            case SIZE:
                strType = "尺码";
                // edt = edtSize;
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
                case COLOR:
                    for (ColorItemModel colorItem : mColorList) {
                        if (colorItem.getColor().getName().equals(text)) {
                            colorItem.setCheck(true);
                            isExists = true;
                            if (mColorAdapter != null) {
                                mColorAdapter.notifyDataSetChanged();
                            }
                            break;
                        }
                    }
                    break;
                case SIZE:
                    for (SizeItemModel sizeItem : mSizeList) {
                        if (sizeItem.getSize().getName().equals(text)) {
                            sizeItem.setCheck(true);
                            isExists = true;
                            if (mSizeAdapter != null)
                                mSizeAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                    break;
            }
            if (isExists) {
                Toast.makeText(vThis, String.format(getString(R.string.specqty_pw_edtNew_has_exsits), strType),
                        Toast.LENGTH_SHORT).show();
                // edt.requestFocus();
                return;
            }
        }

        // 验证网络
        if (!FunctionHelper.CheckNetworkOnline(vThis))
            return;
        // 执行操作
        addDataTask = new AddDataTask(type, text);
        addDataTask.execute((Void) null);
    }

    /**
     * 删除颜色尺码
     */
    private void deleteData(PopupWindowType type) {
        String strType = "";
        // List<String> checkMap = new ArrayList<String>();
        List<String> checkIDsMap = new ArrayList<String>();
        switch (type) {
            case COLOR:
                strType = "颜色";
                // checkMap = mColorAdapter.getCheckMap();
                checkIDsMap = mColorAdapter.getCheckIDsMap();
                break;
            case SIZE:
                strType = "尺码";
                // checkMap = mSizeAdapter.getCheckMap();
                checkIDsMap = mSizeAdapter.getCheckIDsMap();
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
        deleteDataTask = new DeleteDataTask(type, checkIDsMap);// checkMap
        deleteDataTask.execute((Void) null);
    }

    /**
     * 选择颜色或尺码
     */
    private void checkColorOrSize(final PopupWindowType type) {
        List<String> checkMap = new ArrayList<String>();
        switch (type) {
            case COLOR:
                checkMap = mColorAdapter.getCheckMap();
                // 重新勾选颜色
                //添加颜色卡
                List<UploadBean.ColorPicsBean> xcolorPicsBeanList = new ArrayList<>();
                List<UploadBean.ColorPicsBean> colorPicsBeanList2 = new ArrayList<>();
                List<String> xcolors = new ArrayList<>();
                for (UploadBean.ColorPicsBean bean : colorPicsBeanList) {
                    if (TextUtils.isEmpty(bean.getColor())) {
                        xcolorPicsBeanList.add(bean);
                    } else {
                        colorPicsBeanList2.add(bean);
                        xcolors.add(bean.getColor());
                    }
                }

                if (ListUtils.isEmpty(colorPicsBeanList2)) {
                    for (int j = 0; j < checkMap.size(); j++) {
                        UploadBean.ColorPicsBean colorPicsBean = new UploadBean.ColorPicsBean();
                        String url = "";
                        boolean isupload = true;
                        if (!ListUtils.isEmpty(picsList))
                            for (UploadBean.ColorPicsBean picsBean : picsList) {
                                if (picsBean.getColor().equals(checkMap.get(j))) {
                                    url = picsBean.getUrl();
                                    isupload = picsBean.is_upload();
                                }
                            }
                        colorPicsBean.setUrl(url);
                        colorPicsBean.setColor(checkMap.get(j));
                        colorPicsBean.setIs_upload(isupload);
                        xcolorPicsBeanList.add(colorPicsBean);
                    }
                } else {
                    for (int j = 0; j < checkMap.size(); j++) {
                        if (xcolors.contains(checkMap.get(j))) {
                            UploadBean.ColorPicsBean colorPicsBean = new UploadBean.ColorPicsBean();
                            String url = "";
                            boolean isupload = true;
                            for (UploadBean.ColorPicsBean picsBean : colorPicsBeanList2) {
                                if (picsBean.getColor().equals(checkMap.get(j))) {
                                    url = picsBean.getUrl();
                                    isupload = picsBean.is_upload();
                                }
                            }
                            colorPicsBean.setUrl(url);
                            colorPicsBean.setColor(checkMap.get(j));
                            colorPicsBean.setIs_upload(isupload);
                            xcolorPicsBeanList.add(colorPicsBean);
                        } else {
                            UploadBean.ColorPicsBean colorPicsBean = new UploadBean.ColorPicsBean();
                            String url = "";
                            boolean isupload = true;
                            if (!ListUtils.isEmpty(picsList))
                                for (UploadBean.ColorPicsBean picsBean : picsList) {
                                    if (picsBean.getColor().equals(checkMap.get(j))) {
                                        url = picsBean.getUrl();
                                        isupload = picsBean.is_upload();
                                    }
                                }
                            colorPicsBean.setUrl(url);
                            colorPicsBean.setColor(checkMap.get(j));
                            colorPicsBean.setIs_upload(isupload);
                            xcolorPicsBeanList.add(colorPicsBean);
                        }
                    }
                }

                colorPicsBeanList.clear();
                colorPicsBeanList.addAll(xcolorPicsBeanList);
//                        for (int j = 0; j < checkMap.size(); j++) {
//                            if (!colors.contains(checkMap.get(j))) {
//                                UploadBean.ColorPicsBean colorPicsBean = new UploadBean.ColorPicsBean();
//                                String url = "";
//                                for (UploadBean.ColorPicsBean picsBean : picsList) {
//                                    if (picsBean.getColor().equals(checkMap.get(j))) {
//                                        url = picsBean.getUrl();
//                                    }
//                                }
//                                colorPicsBean.setUrl(url);
//                                colorPicsBean.setColor(checkMap.get(j));
//                                colorPicsBean.setIs_upload(true);
//                                colorPicsBeanList.add(colorPicsBean);
//                            }
//                        }
                if (colorPicsAdapter != null)
                    colorPicsAdapter.notifyDataSetChanged();
                //dddd

                String colors = "";
                for (ColorItemModel colorItem : mColorList) {
                    if (checkMap.contains(colorItem.getColor().getName())) {
                        colorItem.setCheck(true);
                        if (colors.indexOf(colorItem.getColor().getName() + "  ") == 0
                                || colors.contains("  " + colorItem.getColor().getName() + "  ")) {
                        } else {
                            colors += colorItem.getColor().getName() + "  ";
                        }
                    } else {
                        colorItem.setCheck(false);
                    }
                }
                tvColors.setText(colors.trim());
                break;
            case SIZE:
                checkMap = mSizeAdapter.getCheckMap();
                // 重新勾选尺码
                String sizes = "";
                for (SizeItemModel sizeItem : mSizeList) {
                    if (checkMap.contains(sizeItem.getSize().getName())) {
                        sizeItem.setCheck(true);
                        if (sizes.indexOf(sizeItem.getSize().getName() + "  ") == 0
                                || sizes.contains("  " + sizeItem.getSize().getName() + "  ")) {
                        } else {
                            sizes += sizeItem.getSize().getName() + "  ";
                        }
                    } else {
                        sizeItem.setCheck(false);
                    }
                }
                tvSizes.setText(sizes.trim());
                break;
        }

        // 将颜色集合转换为字典
        List<String> colorMap_check = new ArrayList<String>();
        for (ColorItemModel colorItem : mColorList) {
            boolean isCheck = colorItem.isCheck();
            String name = colorItem.getColor().getName();
            if (isCheck)
                colorMap_check.add(name);
        }
        // 将尺码集合转换为字典
        List<String> sizeMap_check = new ArrayList<String>();
        for (SizeItemModel sizeItem : mSizeList) {
            boolean isCheck = sizeItem.isCheck();
            String name = sizeItem.getSize().getName();
            if (isCheck)
                sizeMap_check.add(name);
        }

        // 重新整理颜色尺码集合
        mColorSizeList = new ArrayList<ColorSizeModel>();
        // 1.移除非勾选项
        // List<ColorSizeModel> colorSize_deleted = new ArrayList<ColorSizeModel>();// 被移除的颜色尺码项
        // for (ColorSizeModel colorSize : mColorSizeList) {
        // ColorModel color = colorSize.getColor();
        // SizeModel size = colorSize.getSize();
        // if (color == null || size == null)
        // continue;
        // // 已存在颜色尺码集合中的项，不做任何处理
        // if (colorMap_check.contains(color.getName())
        // && sizeMap_check.contains(size.getName()))
        // continue;
        // // 如果只有其中一项存在颜色尺码集合中，则认为是被取消勾选的项
        // if ((colorMap_check.contains(color.getName()) && !sizeMap_check
        // .contains(size.getName()))
        // || (!colorMap_check.contains(color.getName()) && sizeMap_check
        // .contains(size.getName()))) {
        // colorSize_deleted.add(colorSize);
        // continue;
        // }
        // }
        // if (colorSize_deleted.size() > 0) {
        // mColorSizeList.removeAll(colorSize_deleted);
        // }
        // 2.添加新勾选项
        for (String colorString : colorMap_check) {
            for (String sizeString : sizeMap_check) {
                ColorSizeModel colorSize = getColorSizeByIds(colorString, sizeString);
                if (colorSize != null)
                    continue;
                // 颜色
                ColorModel color = new ColorModel();
                color.setName(colorString);
                // 尺码
                SizeModel size = new SizeModel();
                size.setName(sizeString);
                // 颜色尺码实体
                colorSize = new ColorSizeModel();
                colorSize.setColor(color);
                colorSize.setSize(size);
                colorSize.setQty(2000);// 数量默认为1

                mColorSizeList.add(colorSize);
            }
        }

        // 刷新适配器
        mColorAdapter.notifyDataSetChanged();
        mSizeAdapter.notifyDataSetChanged();
        mSpecQtyItemAdapter.mColorSizeList = mColorSizeList;
        mSpecQtyItemAdapter.notifyDataSetChanged();

        // 显示或隐藏空视图
        toggleEmptyView();

    }

    public class DataAsybcTask extends AsyncTask<String, Integer, Object> {
        PopupWindowType type;

        DataAsybcTask(PopupWindowType type) {
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (loadingDialog == null)
                loadingDialog = new LoadingDialog(vThis);
            loadingDialog.start("加载....");
        }

        @Override
        protected void onPostExecute(Object object) {
            super.onPostExecute(object);
            if (loadingDialog != null)
                loadingDialog.stop();
            String mess = "";
            if (object != null) {
                if (object instanceof String)
                    mess = object.toString();
            }
            switch (type) {
                case COLOR:
                    tvColors.setText(mess);
                    break;
                case SIZE:
                    tvSizes.setText(mess);
                    break;
            }
            if (colorPicsAdapter != null)
                colorPicsAdapter.notifyDataSetChanged();
            mColorAdapter.notifyDataSetChanged();
            mSizeAdapter.notifyDataSetChanged();
            mSpecQtyItemAdapter.mColorSizeList = mColorSizeList;
            mSpecQtyItemAdapter.notifyDataSetChanged();
            toggleEmptyView();
        }

        @Override
        protected Object doInBackground(String... params) {
            List<String> checkMap = new ArrayList<>();
            String colors = "";
            String sizes = "";
            switch (type) {
                case COLOR:
                    checkMap = mColorAdapter.getCheckMap();
                    // 重新勾选颜色
                    //添加颜色卡
                    List<UploadBean.ColorPicsBean> xcolorPicsBeanList = new ArrayList<>();
                    List<UploadBean.ColorPicsBean> colorPicsBeanList2 = new ArrayList<>();
                    List<String> xcolors = new ArrayList<>();
                    for (UploadBean.ColorPicsBean bean : colorPicsBeanList) {
                        if (TextUtils.isEmpty(bean.getColor())) {
                            xcolorPicsBeanList.add(bean);
                        } else {
                            colorPicsBeanList2.add(bean);
                            xcolors.add(bean.getColor());
                        }
                    }

                    if (ListUtils.isEmpty(colorPicsBeanList2)) {
                        for (int j = 0; j < checkMap.size(); j++) {
                            UploadBean.ColorPicsBean colorPicsBean = new UploadBean.ColorPicsBean();
                            String url = "";
                            boolean isupload = true;
                            if (!ListUtils.isEmpty(picsList))
                                for (UploadBean.ColorPicsBean picsBean : picsList) {
                                    if (picsBean.getColor().equals(checkMap.get(j))) {
                                        url = picsBean.getUrl();
                                        isupload = picsBean.is_upload();
                                    }
                                }
                            colorPicsBean.setUrl(url);
                            colorPicsBean.setColor(checkMap.get(j));
                            colorPicsBean.setIs_upload(isupload);
                            xcolorPicsBeanList.add(colorPicsBean);
                        }
                    } else {
                        for (int j = 0; j < checkMap.size(); j++) {
                            if (xcolors.contains(checkMap.get(j))) {
                                UploadBean.ColorPicsBean colorPicsBean = new UploadBean.ColorPicsBean();
                                String url = "";
                                boolean isupload = true;
                                for (UploadBean.ColorPicsBean picsBean : colorPicsBeanList2) {
                                    if (picsBean.getColor().equals(checkMap.get(j))) {
                                        url = picsBean.getUrl();
                                        isupload = picsBean.is_upload();
                                    }
                                }
                                colorPicsBean.setUrl(url);
                                colorPicsBean.setColor(checkMap.get(j));
                                colorPicsBean.setIs_upload(isupload);
                                xcolorPicsBeanList.add(colorPicsBean);
                            } else {
                                UploadBean.ColorPicsBean colorPicsBean = new UploadBean.ColorPicsBean();
                                String url = "";
                                boolean isupload = true;
                                if (!ListUtils.isEmpty(picsList))
                                    for (UploadBean.ColorPicsBean picsBean : picsList) {
                                        if (picsBean.getColor().equals(checkMap.get(j))) {
                                            url = picsBean.getUrl();
                                            isupload = picsBean.is_upload();
                                        }
                                    }
                                colorPicsBean.setUrl(url);
                                colorPicsBean.setColor(checkMap.get(j));
                                colorPicsBean.setIs_upload(isupload);
                                xcolorPicsBeanList.add(colorPicsBean);
                            }
                        }
                    }
                    colorPicsBeanList.clear();
                    colorPicsBeanList.addAll(xcolorPicsBeanList);

//                    if (colorPicsAdapter != null)
//                        colorPicsAdapter.notifyDataSetChanged();


                    for (ColorItemModel colorItem : mColorList) {
                        if (checkMap.contains(colorItem.getColor().getName())) {
                            colorItem.setCheck(true);
                            if (colors.indexOf(colorItem.getColor().getName() + "  ") == 0
                                    || colors.contains("  " + colorItem.getColor().getName() + "  ")) {
                            } else {
                                colors += colorItem.getColor().getName() + "  ";
                            }
                        } else {
                            colorItem.setCheck(false);
                        }
                    }

                    break;
                case SIZE:
                    checkMap = mSizeAdapter.getCheckMap();
                    // 重新勾选尺码

                    for (SizeItemModel sizeItem : mSizeList) {
                        if (checkMap.contains(sizeItem.getSize().getName())) {
                            sizeItem.setCheck(true);
                            if (sizes.indexOf(sizeItem.getSize().getName() + "  ") == 0
                                    || sizes.contains("  " + sizeItem.getSize().getName() + "  ")) {
                            } else {
                                sizes += sizeItem.getSize().getName() + "  ";
                            }
                        } else {
                            sizeItem.setCheck(false);
                        }
                    }

                    break;
            }

            // 将颜色集合转换为字典
            List<String> colorMap_check = new ArrayList<String>();
            for (ColorItemModel colorItem : mColorList) {
                boolean isCheck = colorItem.isCheck();
                String name = colorItem.getColor().getName();
                if (isCheck)
                    colorMap_check.add(name);
            }
            // 将尺码集合转换为字典
            List<String> sizeMap_check = new ArrayList<String>();
            for (SizeItemModel sizeItem : mSizeList) {
                boolean isCheck = sizeItem.isCheck();
                String name = sizeItem.getSize().getName();
                if (isCheck)
                    sizeMap_check.add(name);
            }

            // 重新整理颜色尺码集合
            mColorSizeList = new ArrayList<ColorSizeModel>();
            for (String colorString : colorMap_check) {
                for (String sizeString : sizeMap_check) {
                    ColorSizeModel colorSize = getColorSizeByIds(colorString, sizeString);
                    if (colorSize != null)
                        continue;
                    // 颜色
                    ColorModel color = new ColorModel();
                    color.setName(colorString);
                    // 尺码
                    SizeModel size = new SizeModel();
                    size.setName(sizeString);
                    // 颜色尺码实体
                    colorSize = new ColorSizeModel();
                    colorSize.setColor(color);
                    colorSize.setSize(size);
                    colorSize.setQty(2000);// 数量默认为1

                    mColorSizeList.add(colorSize);
                }
            }

            // 刷新适配器
//            mColorAdapter.notifyDataSetChanged();
//            mSizeAdapter.notifyDataSetChanged();
           // mSpecQtyItemAdapter.mColorSizeList = mColorSizeList;
            // mSpecQtyItemAdapter.notifyDataSetChanged();
            switch (type) {
                case COLOR:
                    return colors.trim();
                case SIZE:
                    return sizes.trim();
            }
            return null;
        }
    }

    /**
     * 根据颜色、尺码返回相对应颜色尺码实体，找不到是返回null
     */
    private ColorSizeModel getColorSizeByIds(String colorName, String sizeName) {
        ColorSizeModel colorSize = null;
        for (ColorSizeModel colorSizeModel : mColorSizeList) {
            ColorModel color = colorSizeModel.getColor();
            SizeModel size = colorSizeModel.getSize();
            if (color == null || size == null)
                continue;
            if (color.getName().equals(colorName) && size.getName().equals(sizeName)) {
                colorSize = colorSizeModel;
                break;
            }
        }
        return colorSize;
    }

    /**
     * 关闭界面并返回数据
     */
    private void returnData() {
        // 过滤数量小于等于0的项
        List<ColorSizeModel> colorSizeList_temp = new ArrayList<ColorSizeModel>();
        if (mColorSizeList != null) {
            for (ColorSizeModel colorSize : mColorSizeList) {
                if (colorSize.getQty() <= 0)
                    continue;
                colorSizeList_temp.add(colorSize);
            }
        }
        int day = 0;
        try {
            day = Integer.parseInt(etWaitDay.getText().toString());
            if (day < 0) {
                day = 0;
            }
        } catch (Exception ex) {
            day = 0;
        }
        // 传递已设置好的颜色尺码
        Bundle bundle = new Bundle();
        bundle.putSerializable(COLOR_SIZE_LIST, (Serializable) colorSizeList_temp);
        bundle.putSerializable(EXTRA_UPLOAD_COLORPICS_ITEM, (Serializable) colorPicsBeanList);
        bundle.putBoolean(IS_WAIT_ORDER, Integer.parseInt(imgOpenClose.getTag().toString()) != 0);
        bundle.putInt(WAIT_DAY, day);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(UploadItemActivity.RESULTCODE_OK, intent);
        finish();
    }

    //获取是否要主色卡图片上传数据
    private boolean getIsHasUplodPics() {
        boolean flag = false;
        if (ListUtils.isEmpty(colorPicsBeanList)) {
            flag = false;
        } else {
            for (UploadBean.ColorPicsBean bean : colorPicsBeanList) {
                if (!TextUtils.isEmpty(bean.getUrl())) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    /**
     * 获取颜色
     */
    private class LoadColorTask extends AsyncTask<Void, Void, String> {

        private List<ColorModel> colorList_temp;

        @Override
        protected String doInBackground(Void... params) {
            try {
                String cookie = PublicData.getCookie(vThis);
                colorList_temp = UploadItemAPI.getInstance().getColors(cookie);
                return "OK";
            } catch (Exception ex) {
                Log.e(TAG, "获取颜色发生异常");
                ex.printStackTrace();
                return ex.getMessage() == null ? "操作异常" : ex.getMessage();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Animation animation = AnimationUtils.loadAnimation(vThis, R.anim.loading);
            // animation.setInterpolator(new LinearInterpolator());// 匀速旋转
            // pwColor_iconLoading.setAnimation(animation);
            // animation.start();
            // pwColor_iconLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // pwColor_iconLoading.clearAnimation();
            // pwColor_iconLoading.setVisibility(View.GONE);
            loadColorTask = null;
            if (result.equals("OK")) {
                if (colorList_temp != null) {
                    // 清空颜色集合中的数据
                    mColorAdapter.notifyDataSetInvalidated();
                    // 重新添加数据到集合中
                    for (ColorModel color : colorList_temp) {
                        ColorItemModel colorItemModel = new ColorItemModel(false, color);
                        if (!mColorList.contains(colorItemModel)) {
                            mColorList.add(colorItemModel);
                        }

                    }
                    mColorAdapter.notifyDataSetChanged();
                }
            } else {
                Toast.makeText(vThis, result, Toast.LENGTH_LONG).show();
            }

            // 判断是否展示空视图
            toggleEmptyView();
        }

    }

    /**
     * 获取尺码
     */
    private class LoadSizeTask extends AsyncTask<Void, Void, String> {

        private List<SizeModel> sizeList_temp;

        @Override
        protected String doInBackground(Void... params) {
            try {
                String cookie = PublicData.getCookie(vThis);
                sizeList_temp = UploadItemAPI.getInstance().getSizes(cookie);
                return "OK";
            } catch (Exception ex) {
                Log.e(TAG, "获取尺码发生异常");
                ex.printStackTrace();
                return ex.getMessage() == null ? "操作异常" : ex.getMessage();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Animation animation = AnimationUtils.loadAnimation(vThis, R.anim.loading);
            // animation.setInterpolator(new LinearInterpolator());// 匀速旋转
            // pwSize_iconLoading.setAnimation(animation);
            // animation.start();
            // pwSize_iconLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // pwSize_iconLoading.clearAnimation();
            // pwSize_iconLoading.setVisibility(View.GONE);
            loadSizeTask = null;

            if (result.equals("OK")) {
                if (sizeList_temp != null) {
                    // 清空尺码集合中的数据
                    mSizeAdapter.notifyDataSetInvalidated();
                    // 重新添加数据到集合中
                    for (SizeModel size : sizeList_temp) {
                        SizeItemModel sizeItemModel = new SizeItemModel(false, size);
                        if (!mSizeList.contains(sizeItemModel)) {
                            mSizeList.add(sizeItemModel);
                        }

                    }
                    mSizeAdapter.notifyDataSetChanged();
                }
            } else {
                Toast.makeText(vThis, result, Toast.LENGTH_LONG).show();
            }

            // 判断是否展示空视图
            toggleEmptyView();
        }

    }

    /**
     * 获取颜色、尺码数据列表
     */
    private class LoadDataTask extends AsyncTask<Void, Void, String> {

        private List<ColorModel> colorList_temp;
        private List<SizeModel> sizeList_temp;

        @Override
        protected String doInBackground(Void... params) {
            try {
                String cookie = PublicData.getCookie(vThis);
                colorList_temp = UploadItemAPI.getInstance().getColors(cookie);
                sizeList_temp = UploadItemAPI.getInstance().getSizes(cookie);
                return "OK";
            } catch (Exception ex) {
                Log.e(TAG, "获取颜色、尺码发生异常");
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
                if (colorList_temp != null) {
                    // 清空颜色集合中的数据
                    mColorAdapter.notifyDataSetInvalidated();
                    // 重新添加数据到集合中
                    for (ColorModel color : colorList_temp) {
                        ColorItemModel colorItemModel = new ColorItemModel(false, color);
                        boolean hasColor = false;
                        for (ColorItemModel color_list_item : mColorList) {
                            if (color_list_item.getColor().getName().equals(colorItemModel.getColor().getName())) {
                                hasColor = true;
                            }
                        }
                        if (!hasColor) {
                            mColorList.add(colorItemModel);
                        }
                    }
                    mColorAdapter.notifyDataSetChanged();
                }

                if (sizeList_temp != null) {
                    // 清空尺码集合中的数据
                    mSizeAdapter.notifyDataSetInvalidated();
                    // 重新添加数据到集合中
                    for (SizeModel size : sizeList_temp) {
                        SizeItemModel sizeItemModel = new SizeItemModel(false, size);
                        boolean hasSize = false;
                        for (SizeItemModel size_list_item : mSizeList) {
                            if (size_list_item.getSize().getName().equals(sizeItemModel.getSize().getName())) {
                                hasSize = true;
                            }
                        }
                        if (!hasSize) {
                            mSizeList.add(sizeItemModel);
                        }
                    }
                    mSizeAdapter.notifyDataSetChanged();
                }

                // 刷新已勾选的颜色、尺码
                setColorAndSize(mColorSizeList);
            } else {
                // 验证result
                if (result.toString().startsWith("401") || result.toString().startsWith("not_registered")) {
                    Toast.makeText(vThis, result, Toast.LENGTH_LONG).show();
                    ApiHelper.checkResult(result, vThis);
                }
                // else {
                // Toast.makeText(vThis, result, Toast.LENGTH_LONG).show();
                // }
            }

            // 判断是否展示空视图
            toggleEmptyView();

        }

    }

    /**
     * 添加颜色或尺码
     */
    private class AddDataTask extends AsyncTask<Void, Void, Object> {

        private PopupWindowType pwType;
        private String name;

        public AddDataTask(PopupWindowType type, String name) {
            this.pwType = type;
            this.name = name;
        }

        @Override
        protected Object doInBackground(Void... params) {
            try {
                Object obj = null;
                String cookie = PublicData.getCookie(vThis);
                switch (pwType) {
                    case COLOR:
                        obj = UploadItemAPI.getInstance().addColor(name, cookie);
                        break;
                    case SIZE:
                        obj = UploadItemAPI.getInstance().addSize(name, cookie);
                        break;
                }
                return obj;
            } catch (Exception ex) {
                String str = "";
                if (pwType == PopupWindowType.COLOR) {
                    str = "颜色";
                } else if (pwType == PopupWindowType.SIZE) {
                    str = "尺码";
                }
                Log.e(TAG, "添加" + str + "时发生异常");
                ex.printStackTrace();
                return ex.getMessage() == null ? "未知异常" : ex.getMessage();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // ImageView iconLoading = null;
            switch (pwType) {
                case COLOR:
                    if (mColorMenu != null && mColorMenu.isShowing()) {
                        mColorMenu.showProgress(true);
                    }
                    break;
                case SIZE:
                    if (mSizelMenu != null && mSizelMenu.isShowing()) {
                        mSizelMenu.showProgress(true);
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
                case COLOR:
                    if (mColorMenu != null && mColorMenu.isShowing()) {
                        mColorMenu.showProgress(false);
                    }
                    break;
                case SIZE:
                    if (mSizelMenu != null && mSizelMenu.isShowing()) {
                        mSizelMenu.showProgress(false);
                    }
                    break;
            }

            // ImageView iconLoading = null;
            // switch (pwType) {
            // case COLOR:
            // iconLoading = pwColor_add_iconLoading;
            // break;
            // case SIZE:
            // iconLoading = pwSize_add_iconLoading;
            // break;
            // }
            // iconLoading.clearAnimation();
            // iconLoading.setVisibility(View.GONE);
            addDataTask = null;

            if (result instanceof ColorModel) {
                // 添加新颜色
                ColorModel color = (ColorModel) result;
                ColorItemModel colorItem = new ColorItemModel(false, color);
                if (!mColorList.contains(colorItem)) {
                    colorItem.setCheck(true);
                    mColorList.add(0, colorItem);
                }
                // 刷新适配器
                mColorAdapter.notifyDataSetChanged();

                // 清空文本框
                // edtColor.setText("");
                // 隐藏空白视图
                toggleEmptyView();
            } else if (result instanceof SizeModel) {
                // 添加新尺码
                SizeModel size = (SizeModel) result;
                SizeItemModel sizeItem = new SizeItemModel(false, size);
                if (!mSizeList.contains(sizeItem)) {
                    sizeItem.setCheck(true);
                    mSizeList.add(0, sizeItem);
                }
                // 刷新适配器
                mSizeAdapter.notifyDataSetChanged();

                // 清空文本框
                // edtSize.setText("");
                // 隐藏空白视图
                toggleEmptyView();
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
     * 删除颜色、尺码
     */
    private class DeleteDataTask extends AsyncTask<Void, Void, String> {

        private PopupWindowType pwType;
        private List<String> deleteMap;

        public DeleteDataTask(PopupWindowType type, List<String> map) {
            pwType = type;
            deleteMap = map;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                boolean success = false;
                String ids = FunctionHelper.convert(",", deleteMap);
                String cookie = PublicData.getCookie(vThis);
                switch (pwType) {
                    case COLOR:
                        success = UploadItemAPI.getInstance().deleteColors(ids, cookie);
                        break;
                    case SIZE:
                        success = UploadItemAPI.getInstance().deleteSizes(ids, cookie);
                        break;
                }
                if (success) {
                    return "OK";
                } else {
                    return "数据删除失败";
                }
            } catch (Exception ex) {
                String str = "";
                if (pwType == PopupWindowType.COLOR) {
                    str = "颜色";
                } else if (pwType == PopupWindowType.SIZE) {
                    str = "尺码";
                }
                Log.e(TAG, "添加" + str + "时发生异常");
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
            deleteDataTask = null;

            if (result.equals("OK")) {
                // 判断是否删除了选好的颜色尺码
                if (pwType == PopupWindowType.COLOR) {
//                    for (int i = mColorList.size() - 1; i >= 0; i--) {
//                        ColorItemModel item = mColorList.get(i);
//                        if (deleteMap.indexOf(String.valueOf(item.getColor().getID())) > -1) {
//                            // sizeList_deleted.add(sizeItem);
//                            mColorList.remove(item);
//                        }
//                    }

                    // 得到刚删除的颜色
                    for (int i = mColorList.size() - 1; i >= 0; i--) {
                        ColorItemModel colorItem = mColorList.get(i);
                        if (deleteMap.indexOf(String.valueOf(colorItem.getColor().getID())) > -1) {
                            // colorList_deleted.add(colorItem);
                            mColorList.remove(i);
                        }
                    }
                    // List<ColorItemModel> colorList_deleted = new ArrayList<ColorItemModel>();
                    // for (ColorItemModel colorItem : mColorList) {
                    // if (deleteMap.indexOf(String.valueOf(colorItem.getColor().getID())) > -1) {
                    // // colorList_deleted.add(colorItem);
                    // mColorList.remove(colorItem);
                    // }
                    // }
                    // 从集合中移除已被删除的颜色
                    // mColorList.removeAll(colorList_deleted);
                    // 刷新适配器
                    mColorAdapter.notifyDataSetChanged();

                } else if (pwType == PopupWindowType.SIZE) {
                    // 得到刚删除的尺码
                    // List<SizeItemModel> sizeList_deleted = new ArrayList<SizeItemModel>();
                    for (int i = mSizeList.size() - 1; i >= 0; i--) {
                        SizeItemModel sizeItem = mSizeList.get(i);
                        if (deleteMap.indexOf(String.valueOf(sizeItem.getSize().getID())) > -1) {
                            // sizeList_deleted.add(sizeItem);
                            mSizeList.remove(sizeItem);
                        }
                    }

                    // for (SizeItemModel sizeItem : mSizeList) {
                    //
                    // }
                    // 从集合中移除已被删除的颜色
                    // mSizeList.removeAll(sizeList_deleted);
                    // 刷新适配器
                    mSizeAdapter.notifyDataSetChanged();

                }

                // 重新初始化选择好的颜色尺码列表
                checkColorOrSize(pwType);
            } else {
                // 验证result
                if (result.toString().startsWith("401") || result.toString().startsWith("not_registered")) {
                    Toast.makeText(vThis, result.toString(), Toast.LENGTH_LONG).show();
                    ApiHelper.checkResult(result, vThis);
                } else {
                    Toast.makeText(vThis, result, Toast.LENGTH_LONG).show();
                }
            }

            // 判断是否展示空视图
            toggleEmptyView();
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
}
