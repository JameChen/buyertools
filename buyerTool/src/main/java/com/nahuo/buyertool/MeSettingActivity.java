package com.nahuo.buyertool;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.common.Const;
import com.nahuo.buyertool.common.SpManager;
import com.nahuo.buyertool.dialog.AccountDialog;
import com.nahuo.buyertool.provider.UserInfoProvider;
import com.nahuo.buyertool.task.CheckUpdateTask;
import com.nahuo.buyertool.utils.ListDataSave;
import com.nahuo.buyertool.utils.WaterWarkSave;
import com.nahuo.library.controls.LightPopDialog;
import com.nahuo.library.controls.LoadingDialog;
import com.nahuo.library.helper.FunctionHelper;
import com.nahuo.library.helper.ImageUrlExtends;
import com.nahuo.service.autoupdate.AppUpdate;
import com.nahuo.service.autoupdate.AppUpdateService;

import ch.ielse.view.SwitchView;

import static java.lang.Integer.parseInt;

/**
 * @description 我的账号
 * @created 2014-8-15 上午11:01:44
 */
public class MeSettingActivity extends BaseActivity2 implements OnClickListener, AccountDialog.PopDialogListener {

    private Context mContext = this;
    private static final String TAG = "MeSettingActivity";
    private MeSettingActivity vThis = this;
    private AppUpdate mAppUpdate;
    private LoadingDialog loadingDialog;
    private TextView mTvAddress;
    private Button ok_number, ok_com;
    private EditText et_single_row, et_compress_row;
    private ImageView iv_water_mark_one, iv_water_mark_two;
    public static String water_mark_path_one = ImageUrlExtends.HTTP_BANWO_FILES+"/img/shuiyin5.png";
    public static String water_mark_path_two = ImageUrlExtends.HTTP_BANWO_FILES+"/img/shuiyin6.png";
    private RequestOptions options;
    private View layout_water_mark;
    private SwitchView mSwitchView, switch_video_view;
    private WaterWarkSave save;
    private String use_id;
    public static int WATER_TYPE_ONE = 1;
    public static int WATER_TYPE_TWO = 2;
    private TextView tv_clear;
    private ListDataSave listDataSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_setting);
        BWApplication.getInstance().addActivity(this);
        listDataSave = new ListDataSave(this, Const.LOGIN_PRE_KEY);
        setTitle("设置");
        options = new RequestOptions()
                .centerCrop()
                .placeholder(R.color.color_f6)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        mAppUpdate = AppUpdateService.getAppUpdate(this);
        use_id = SpManager.getUserId(this) + "";
        save = new WaterWarkSave(this, use_id);
        initView();

    }

    // 初始化数据
    private void initView() {
        loadingDialog = new LoadingDialog(this);
        tv_clear = (TextView) findViewById(R.id.tv_clear);
        tv_clear.setOnClickListener(this);
        mSwitchView = (SwitchView) findViewById(R.id.switch_view);
        switch_video_view = (SwitchView) findViewById(R.id.switch_video_view);
        layout_water_mark = findViewById(R.id.layout_water_mark);
        ok_number = (Button) findViewById(R.id.ok_number);
        ok_com = (Button) findViewById(R.id.ok_com);
        iv_water_mark_one = (ImageView) findViewById(R.id.iv_water_mark_one);
        iv_water_mark_two = (ImageView) findViewById(R.id.iv_water_mark_two);
        et_single_row = (EditText) findViewById(R.id.et_single_row);
        et_compress_row = (EditText) findViewById(R.id.et_compress_row);
        iv_water_mark_one.setOnClickListener(this);
        iv_water_mark_two.setOnClickListener(this);
        ok_number.setOnClickListener(this);
        ok_com.setOnClickListener(this);
        initItem(R.id.item_app_update, "版本更新");
        setItemRightText(R.id.item_app_update, "当前版本：" + FunctionHelper.GetAppVersion(vThis));
        initItem(R.id.me_checkout, "退出");
        Glide.with(this)
                .load(water_mark_path_one)
                .apply(options)
                .into(iv_water_mark_one);
        Glide.with(this)
                .load(water_mark_path_two)
                .apply(options)
                .into(iv_water_mark_two);
        et_compress_row.setText(save.getCompressMaxSize(WaterWarkSave.BUYTOOLCOMPRESSMAXSIZE + use_id) + "");
        switch_video_view.toggleSwitch(SpManager.getUploadCompressVideo(this));
        if (save.getHasWaterWark(WaterWarkSave.BUYTOOL_HASWATERWARK + use_id)) {
            mSwitchView.toggleSwitch(true);
            layout_water_mark.setVisibility(View.VISIBLE);
        } else {
            mSwitchView.toggleSwitch(false);
            layout_water_mark.setVisibility(View.GONE);
        }
        et_single_row.setText(save.getNoAddWaterCount(WaterWarkSave.BUYTOOLNOADDWATERWARK_COUNT + use_id) + "");
        if (save.getWaterType(WaterWarkSave.BUYTOOLWATERWARK_TYPE + use_id) == WATER_TYPE_ONE) {
            iv_water_mark_one.setBackground(getResources().getDrawable(R.drawable.water_mark_bg));
            iv_water_mark_two.setBackground(null);
        } else if (save.getWaterType(WaterWarkSave.BUYTOOLWATERWARK_TYPE + use_id) == WATER_TYPE_TWO) {
            iv_water_mark_two.setBackground(getResources().getDrawable(R.drawable.water_mark_bg));
            iv_water_mark_one.setBackground(null);
        } else {
            iv_water_mark_one.setBackground(null);
            iv_water_mark_two.setBackground(null);
        }
        mSwitchView.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(SwitchView view) {
                view.toggleSwitch(true);
                save.setHasWaterWark(WaterWarkSave.BUYTOOL_HASWATERWARK + use_id, true);
                layout_water_mark.setVisibility(View.VISIBLE);
                //ViewHub.showShortToast(MeSettingActivity.this,"true");
            }

            @Override
            public void toggleToOff(SwitchView view) {
                view.toggleSwitch(false);
                save.setHasWaterWark(WaterWarkSave.BUYTOOL_HASWATERWARK + use_id, false);
                layout_water_mark.setVisibility(View.GONE);
                //ViewHub.showShortToast(MeSettingActivity.this,"fasle");
            }
        });
        switch_video_view.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(SwitchView view) {
                view.toggleSwitch(true);
                SpManager.setUploadCompressVideo(vThis, true);
            }

            @Override
            public void toggleToOff(SwitchView view) {
                view.toggleSwitch(false);
                SpManager.setUploadCompressVideo(vThis, false);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_clear:
                AccountDialog.getInstance(this).setTitle("确定清空账号信息吗？").setPositive(this).showDialog();
                break;
            case R.id.iv_water_mark_one:
                save.setWaterType(WaterWarkSave.BUYTOOLWATERWARK_TYPE + use_id, WATER_TYPE_ONE);
                iv_water_mark_one.setBackground(getResources().getDrawable(R.drawable.water_mark_bg));
                iv_water_mark_two.setBackground(null);
                break;
            case R.id.iv_water_mark_two:
                save.setWaterType(WaterWarkSave.BUYTOOLWATERWARK_TYPE + use_id, WATER_TYPE_TWO);
                iv_water_mark_two.setBackground(getResources().getDrawable(R.drawable.water_mark_bg));
                iv_water_mark_one.setBackground(null);
                break;
            case R.id.ok_number:
                String count = et_single_row.getText().toString().trim();
                save.setNoAddWaterCount(WaterWarkSave.BUYTOOLNOADDWATERWARK_COUNT + use_id, parseInt(count));
                ViewHub.showShortToast(this, "设置成功");
                break;
            case R.id.ok_com:
                String ssi = et_compress_row.getText().toString().trim();
                int size = Integer.parseInt(ssi);
                if (size <= 1000 && size >= 150) {
                    save.setCompressMaxSize(WaterWarkSave.BUYTOOLCOMPRESSMAXSIZE + use_id, size);
                    ViewHub.showShortToast(this, "设置成功");
                } else {
                    ViewHub.showShortToast(this, "建议设置最大压缩值在150-1000KB范围");
                }

                break;
            case R.id.titlebar_btnLeft:// 返回
                finish();
                break;

            case R.id.me_checkout:// 退出登录
                ViewHub.showLightPopDialog(this, getString(R.string.dialog_title),
                        getString(R.string.shopset_exit_confirm), "取消", "退出登录", new LightPopDialog.PopDialogListener() {
                            @Override
                            public void onPopDialogButtonClick(int which) {

                                UserInfoProvider.exitApp(mContext);
                            }
                        });
                // UploadManager.getInstance(this)
                break;
            case R.id.item_app_update:// 版本更新
                new CheckUpdateTask(this, mAppUpdate, true, true).execute();
                break;
            default:
                break;
        }
    }


    @Override
    public void finish() {
        Intent data = new Intent();
        setResult(RESULT_OK, data);
        super.finish();
    }

    private void toOtherActivity(Class<?> cls) {
        Intent intent = new Intent(vThis, cls);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
        mAppUpdate.callOnPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
        mAppUpdate.callOnResume();
    }

    private void initItem(int viewId, String text) {
        View v = findViewById(viewId);
        v.setOnClickListener(this);
        TextView tv = (TextView) v.findViewById(R.id.tv_left_text);
        ImageView ivLeftIcon = (ImageView) v.findViewById(R.id.iv_left_icon);

        tv.setText(text);
        ivLeftIcon.setVisibility(View.GONE);

    }

    private void setItemRightText(int viewId, String text) {
        View v = findViewById(viewId);
        TextView tv = (TextView) v.findViewById(R.id.tv_right_text);
        tv.setText(text);
    }

    private void setItemRightText(int viewId, Spanned spanned) {
        View v = findViewById(viewId);
        TextView tv = (TextView) v.findViewById(R.id.tv_right_text);
        tv.setText(spanned);
    }

    @Override
    public void onAccountDialogButtonClick() {
        listDataSave.clearDataList(Const.LOGIN_PRE_LIST_KEY);
        ViewHub.showShortToast(this, "清除成功");
    }
}
