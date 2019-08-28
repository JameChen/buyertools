package com.xinlan.imageeditlibrary.editimage.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.ModuleConfig;
import com.xinlan.imageeditlibrary.editimage.mosaic.DrawMosaicView;
import com.xinlan.imageeditlibrary.editimage.mosaic.MosaicUtil;
import com.xinlan.imageeditlibrary.editimage.utils.FileNewUtils;
import com.xinlan.imageeditlibrary.editimage.view.PaintModeView;

/**
 * Created by jame on 2018/5/31.
 */

public class MosaicFragment extends BaseEditFragment {
    public static final int INDEX = ModuleConfig.INDEX_MOSAIC;
    private View backBtn;// 返回主菜单按钮
    private LinearLayout mFilterGroup;
    private String[] fliters;
    private DrawMosaicView mDrawMosaicView;//马赛克

    public static MosaicFragment newInstance() {
        MosaicFragment fragment = new MosaicFragment();
        return fragment;
    }

    private int mWidth, mHeight;

    Bitmap srcBitmap = null;
    private View mainView;
    String mPath;
    private PaintModeView mPaintModeView;
    private View popView;
    private PopupWindow setStokenWidthWindow;
    private SeekBar mStokenWidthSeekBar,seekBar2,seekBar3;
    private TextView seek_value,seek_value2,seek_value3;
    private SaveCustomPaintTask mSavePaintImageTask;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_image_mosaic, null);

        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        backBtn = mainView.findViewById(R.id.back_to_main);
        mFilterGroup = (LinearLayout) mainView.findViewById(R.id.filter_group);
        mPaintModeView = (PaintModeView) mainView.findViewById(R.id.paint_thumb);
        mPaintModeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStokeWidth();
            }
        });
        initStokeWidthPopWindow();
      //  mPath = activity.filePath;
        srcBitmap =  activity.getMainBit();
        this.mDrawMosaicView = activity.mDrawMosaicView;
        if (srcBitmap != null) {
            mWidth = srcBitmap.getWidth();
            mHeight = srcBitmap.getHeight();
            Bitmap bit = MosaicUtil.getMosaic(srcBitmap);
            if (mDrawMosaicView != null) {
                mDrawMosaicView.setMosaicBackgroundResource(srcBitmap);
                mDrawMosaicView.setMosaicResource(bit);
                mDrawMosaicView.setMosaicBrushWidth(20);
            }
        }
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMain();
            }
        });
        setUpFliters();

    }
    private void initStokeWidthPopWindow() {
        popView = LayoutInflater.from(activity).
                inflate(R.layout.view_set_stoke_width, null);
        setStokenWidthWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);

        mStokenWidthSeekBar = (SeekBar) popView.findViewById(R.id.stoke_width_seekbar);
        seekBar2 = (SeekBar) popView.findViewById(R.id.stoke_width_seekbar_2);
        seekBar3 = (SeekBar) popView.findViewById(R.id.stoke_width_seekbar_3);
        seek_value = (TextView) popView.findViewById(R.id.seek_value);
        seek_value2 = (TextView) popView.findViewById(R.id.seek_value_2);
        seek_value3 = (TextView) popView.findViewById(R.id.seek_value_3);
        seekBar2.setProgress(MosaicUtil.mosic_radius);
        seekBar3.setProgress(MosaicUtil.mosic_mao_radius);
        seek_value2.setText(MosaicUtil.mosic_radius+"");
        seek_value3.setText(MosaicUtil.mosic_mao_radius+"");
        seekBar3.setProgress(MosaicUtil.mosic_mao_radius);
        setStokenWidthWindow.setFocusable(true);
        setStokenWidthWindow.setOutsideTouchable(true);
        setStokenWidthWindow.setBackgroundDrawable(new BitmapDrawable());
        setStokenWidthWindow.setAnimationStyle(R.style.popwin_anim_style);


        mPaintModeView.setPaintStrokeColor(Color.RED);
        mPaintModeView.setPaintStrokeWidth(20);
        seek_value.setText(mPaintModeView.getStokenWidth() + "");
    }
    /**
     * 设置画笔粗细
     * show popwidnow to set paint width
     */
    protected void setStokeWidth() {
        if (popView.getMeasuredHeight() == 0) {
            popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        }

        mStokenWidthSeekBar.setMax(mPaintModeView.getMeasuredHeight());

        mStokenWidthSeekBar.setProgress((int) mPaintModeView.getStokenWidth());

        mStokenWidthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPaintModeView.setPaintStrokeWidth(progress);
                seek_value.setText(progress + "");
                if (mDrawMosaicView!=null) {
                    mDrawMosaicView.setMosaicBrushWidth(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress<10){
                    progress=10;
                    seekBar2.setProgress(progress);
                }
                seek_value2.setText(progress + "");
                MosaicUtil.mosic_radius=progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress<8){
                    progress=8;
                    seekBar3.setProgress(progress);
                }
                seek_value3.setText(progress + "");
                MosaicUtil.mosic_mao_radius=progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        int[] locations = new int[2];
        activity.bottomGallery.getLocationOnScreen(locations);
        setStokenWidthWindow.showAtLocation(activity.bottomGallery,
                Gravity.NO_GRAVITY, 0, locations[1] - popView.getMeasuredHeight());
    }
    @Override
    public void onDestroy() {
//        if (srcBitmap != null && (!srcBitmap.isRecycled())) {
//            srcBitmap.recycle();
//        }
        super.onDestroy();
    }
    private void setUpFliters() {
        fliters = getResources().getStringArray(R.array.masaic_item);
        if (fliters == null)
            return;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        params.leftMargin = 20;
        params.rightMargin = 20;
        mFilterGroup.removeAllViews();
        for (int i = 0, len = fliters.length; i < len; i++) {
            TextView text = new TextView(activity);
            if (i == 0) {
                text.setTextColor(Color.RED);
                text.setTextSize(22);
            } else {
                text.setTextColor(Color.WHITE);
                text.setTextSize(20);
            }
            text.setText(fliters[i]);
            mFilterGroup.addView(text, params);
            text.setTag(i);
            text.setOnClickListener(new FliterClick());
        }// end for i
    }
    /**
     * 保存涂鸦
     */
    public void savePaintImage() {
        if (mSavePaintImageTask != null && !mSavePaintImageTask.isCancelled()) {
            mSavePaintImageTask.cancel(true);
        }

        mSavePaintImageTask = new SaveCustomPaintTask();
        mSavePaintImageTask.execute(mDrawMosaicView.getMosaicBitmap());
    }
    /**
     * 文字合成任务
     * 合成最终图片
     */
    private final class SaveCustomPaintTask extends AsyncTask<Bitmap, Void, Bitmap> {


        @Override
        protected void onCancelled() {
            super.onCancelled();
            //dialog.dismiss();
        }

        @Override
        protected void onCancelled(Bitmap result) {
            super.onCancelled(result);
            //dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //dialog = BaseActivity.getLoadingDialog(getActivity(), R.string.saving_image,
            //        false);
            //dialog.show();
        }

        @SuppressWarnings("WrongThread")
        @Override
        protected Bitmap doInBackground(Bitmap... params) {
            Bitmap originBit = params[0];
            return originBit;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            //dialog.dismiss();
            if (result == null)
                return;
            // 切换新底图
            activity.changeMainBitmap(result,true);
            backToMain();
        }
    }//end inner class
    private final class FliterClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int position = ((Integer) v.getTag()).intValue();
            if (mFilterGroup != null) {
                for (int i = 0; i < mFilterGroup.getChildCount(); i++) {
                    TextView text = (TextView) mFilterGroup.getChildAt(i);
                    if (i == position) {
                        text.setTextColor(Color.RED);
                        text.setTextSize(22);
                    } else {
                        text.setTextColor(Color.WHITE);
                        text.setTextSize(20);
                    }
                }
            }
            switch (position){
                case 0:
                    if (mDrawMosaicView != null) {
                        Bitmap bitmapMosaic = MosaicUtil.getMosaic(srcBitmap);
                        mDrawMosaicView.setMosaicResource(bitmapMosaic);
                    }
                    break;
                case 1:
                    if (mDrawMosaicView != null) {
                        Bitmap bit = BitmapFactory.decodeResource(activity.getResources(),
                                R.drawable.hi4);
                        bit = FileNewUtils.ResizeBitmap(bit, mWidth, mHeight);
                        mDrawMosaicView.setMosaicResource(bit);
                    }
                    break;
                case 2:
                    if (mDrawMosaicView != null) {
                        Bitmap bitmapBlur = MosaicUtil.getBlur(srcBitmap);
                        mDrawMosaicView.setMosaicResource(bitmapBlur);
                    }
                    break;
                case 3:
                    if (mDrawMosaicView != null) {
                        mDrawMosaicView.setMosaicType(MosaicUtil.MosaicType.ERASER);
                    }
                    break;
            }

        }
    }

    @Override
    public void onShow() {
        if (activity != null) {
            activity.mode = EditImageActivity.MODE_MOSAIC;
            activity.mainImage.setVisibility(View.GONE);
           // activity.mainImage.setImageBitmap(activity.getMainBit());
            activity.bannerFlipper.showNext();
            if (mDrawMosaicView != null) {
                mDrawMosaicView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void backToMain() {
        if (activity != null) {
            activity.mode = EditImageActivity.MODE_NONE;
            activity.bottomGallery.setCurrentItem(MainMenuFragment.INDEX);
            activity.mainImage.setVisibility(View.VISIBLE);
            activity.bannerFlipper.showPrevious();
            if (mDrawMosaicView != null) {
                mDrawMosaicView.setVisibility(View.GONE);
            }
        }
    }
}
