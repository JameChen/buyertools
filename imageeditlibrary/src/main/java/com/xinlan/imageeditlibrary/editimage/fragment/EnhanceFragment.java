package com.xinlan.imageeditlibrary.editimage.fragment;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.ViewFlipper;

import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.ModuleConfig;
import com.xinlan.imageeditlibrary.editimage.enhance.PhotoEnhance;
import com.xinlan.imageeditlibrary.editimage.view.imagezoom.ImageViewTouchBase;

/**
 * Created by jame on 2018/6/1.
 */

public class EnhanceFragment extends BaseEditFragment implements SeekBar.OnSeekBarChangeListener {
    public static final int INDEX = ModuleConfig.INDEX_ENHANCE;
    private View mainView;
    private ViewFlipper flipper;
    private View backToMenu;// 返回主菜单
    private SeekBar saturationSeekBar, brightnessSeekBar, contrastSeekBar;

    private String imgPath;
    private Bitmap bitmapSrc;
    private int pregress = 0;
    private PhotoEnhance pe;
    private Bitmap bit = null;
    private SaveImageTask mSaveImageTask;

    public static EnhanceFragment newInstance() {
        EnhanceFragment fragment = new EnhanceFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mainView = inflater.inflate(R.layout.fragment_edit_image_enhance,
                null);
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        backToMenu = mainView.findViewById(R.id.back_to_main);
        backToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMain();
            }
        });
        saturationSeekBar = (SeekBar) mainView.findViewById(R.id.saturation);
        saturationSeekBar.setMax(255);
        saturationSeekBar.setProgress(128);
        saturationSeekBar.setOnSeekBarChangeListener(this);

        brightnessSeekBar = (SeekBar) mainView.findViewById(R.id.brightness);
        brightnessSeekBar.setMax(255);
        brightnessSeekBar.setProgress(128);
        brightnessSeekBar.setOnSeekBarChangeListener(this);

        contrastSeekBar = (SeekBar) mainView.findViewById(R.id.contrast);
        contrastSeekBar.setMax(255);
        contrastSeekBar.setProgress(128);
        contrastSeekBar.setOnSeekBarChangeListener(this);
        bitmapSrc = activity.getMainBit();
        pe = new PhotoEnhance(bitmapSrc);

    }

    /**
     * 保存图片
     */
    public void savePaintImage() {
        if (mSaveImageTask != null && !mSaveImageTask.isCancelled()) {
            mSaveImageTask.cancel(true);
        }
        if (bit != null) {
            mSaveImageTask = new SaveImageTask();
            mSaveImageTask.execute(bit);
        }
    }

    /**
     * 保存图片线程
     *
     * @author panyi
     */
    private final class SaveImageTask extends
            AsyncTask<Bitmap, Void, Bitmap> {
        //private Dialog dialog;

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
            activity.changeMainBitmap(result, true);
            backToMain();
        }
    }// end inner class

    @Override
    public void onShow() {
        if (activity != null) {
            activity.mode = EditImageActivity.MODE_ENHANCE;
            activity.mainImage.setImageBitmap(activity.getMainBit());
            activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
            activity.mainImage.setImageBitmap(activity.getMainBit());
//            activity.mainImage.setVisibility(View.GONE);
//            activity.mRotatePanel.addBit(activity.getMainBit(),
//                    activity.mainImage.getBitmapRect());
//            activity.mRotateFragment.mSeekBar.setProgress(0);
//            du = 0;
//            activity.mRotatePanel.reset();
//            activity.mRotatePanel.setVisibility(View.VISIBLE);
            activity.bannerFlipper.showNext();
        }
    }

    @Override
    public void backToMain() {
        if (activity != null) {
            activity.mode = EditImageActivity.MODE_NONE;
            activity.bottomGallery.setCurrentItem(0);
            activity.mainImage.setVisibility(View.VISIBLE);
            //  this.mRotatePanel.setVisibility(View.GONE);
            activity.bannerFlipper.showPrevious();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        pregress = progress;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int type = 0;
        if (seekBar.getId() == R.id.saturation) {
            pe.setSaturation(pregress);
            type = pe.Enhance_Saturation;
        } else if (seekBar.getId() == R.id.brightness) {
            pe.setBrightness(pregress);
            type = pe.Enhance_Brightness;
        } else if (seekBar.getId() == R.id.contrast) {
            pe.setContrast(pregress);
            type = pe.Enhance_Contrast;
        }
        bit = pe.handleImage(type);
        if (activity != null) {
            if (activity.mainImage != null) {
                activity.mainImage.setImageBitmap(bit);
            }
        }

    }
}
