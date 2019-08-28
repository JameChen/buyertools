package com.nahuo.buyertool.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Toast;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.CompressConfig;
import com.luck.picture.lib.compress.CompressImageOptions;
import com.luck.picture.lib.compress.CompressInterface;
import com.luck.picture.lib.compress.LubanOptions;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.dialog.PictureDialog;
import com.luck.picture.lib.entity.EventEntity;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;
import com.luck.picture.lib.rxbus2.RxBus;
import com.luck.picture.lib.tools.AttrsUtils;
import com.luck.picture.lib.tools.DateUtils;
import com.luck.picture.lib.tools.DoubleUtils;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.nahuo.buyertool.common.LastActivitys;
import com.nahuo.widget.TouchLinearLayout;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropMulti;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UploadPictureBaseActivity extends FragmentActivity {
    private VelocityTracker velocity;
    private int width ;
    int rightMargin ;
    private boolean isFinish ;
    protected Context mContext;
    protected PictureSelectionConfig config;
    protected int spanCount, maxSelectNum, minSelectNum, compressQuality,
            selectionMode, mimeType, videoSecond, compressMaxKB, compressMode,
            compressGrade, compressWidth, compressHeight, aspect_ratio_x, aspect_ratio_y,
            recordVideoSecond, videoQuality, cropWidth, cropHeight;
    protected boolean isGif, isCamera, enablePreview, enableCrop, isCompress,
            enPreviewVideo, checkNumMode, openClickSound, numComplete, camera, freeStyleCropEnabled,
            circleDimmedLayer, hideBottomControls, rotateEnabled, scaleEnabled, previewEggs, statusFont,
            showCropFrame, showCropGrid, previewStatusFont;
    protected String cameraPath, outputCameraPath;
    protected String originalPath;
    protected PictureDialog dialog;
    protected PictureDialog compressDialog;
    protected List<LocalMedia> selectionMedias;

    @Override
    public void finish() {
        isFinish = true ;
        super.finish();
    }
    public void setSlideBack(TouchLinearLayout layout){
        //layout.setOnTouchListener(touchListener);
    }
    @Override
    protected void onResume() {
        super.onResume();
//        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        View decorView = getWindow().getDecorView() ;
//        decorView.setBackgroundColor(Color.TRANSPARENT);
//        ((ViewGroup)decorView).getChildAt(0).setBackgroundColor(Color.TRANSPARENT);
//        width = getResources().getDisplayMetrics().widthPixels ;
//        rightMargin = dip2px(getApplicationContext(), 10);
//        LastActivitys.getInstance().removeView(getWindow().getDecorView());
    }
//    void doTouchEvent(MotionEvent event){
//        int x = (int) event.getX();
//        Log.e("test", "detail touch x = " + x + "  " + event.getAction()) ;
//        switch (event.getAction()){
//            case MotionEvent.ACTION_DOWN:
//                if(velocity == null){
//                    velocity = VelocityTracker.obtain() ;
//                }
//                else{
//                    velocity.clear();
//                }
//                velocity.addMovement(event);
//                break ;
//            case MotionEvent.ACTION_MOVE:
//                //û��down�¼�ʱ��
//                if(x > 0){
//                    setX(x) ;
//                }
//                if(velocity == null){
//                    velocity = VelocityTracker.obtain() ;
//                }
//                else{
//                    velocity.clear();
//                }
//                velocity.addMovement(event);
//                velocity.computeCurrentVelocity(1000);
//                break ;
//            case MotionEvent.ACTION_CANCEL:
//            case MotionEvent.ACTION_UP:
//                if(x > 0){
//                    setX(x);
//                }
//                int plus = dip2px(getApplicationContext() , 20) ;
//                int add = -plus ;
//                int max = (int) event.getX();
//
//                if(max>width/3 || (velocity!=null&&velocity.getXVelocity()>2000)){
//                    add = plus ;
//                }
//                final int a = add ;
//                final int m = max ;
//                new Thread(){
//                    public void run(){
//                        int run = m ;
//                        int add = a ;
//                        int addPlus = add / 10 ;
//                        while(run > 0 && run < width ){
//                            try {
//                                Thread.sleep(10) ;
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            run += add ;
//                            add += addPlus ;
//                            final int r = run ;
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    setX(r) ;
//                                }
//                            });
//                        }
//                        if(run <= 0){
//                            final int r = 0 ;
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    getWindow().getDecorView().setX(r);
//                                    View decorView = LastActivitys.getInstance().getTopView() ;
//                                    decorView.setX(0);
//                                }
//                            });
//                        }
//                        else{
//                            final int r = width ;
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    setX(r) ;
//                                    finish();
//                                }
//                            });
//                        }
//                    }
//                }.start();
//                break ;
//        }
//    }

    private void setX(int x) {
        getWindow().getDecorView().setX(x);
        View decorView = LastActivitys.getInstance().getTopView() ;
        if(decorView != null  ){
            decorView.setX(-width/3 + x/3) ;
        }
    }

//    private View.OnTouchListener touchListener =new View.OnTouchListener() {
//        @Override
//        public boolean onTouch(final View v, MotionEvent event) {
//            doTouchEvent(event);
//            return false;
//        }
//    } ;
    private int dip2px(Context context, int dp) {
        return (int) (context.getResources().getDisplayMetrics().density * dp + 0.5f);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            config = (PictureSelectionConfig) savedInstanceState.getSerializable(PictureConfig.EXTRA_CONFIG);
            cameraPath = savedInstanceState.getString(PictureConfig.BUNDLE_CAMERA_PATH);
            originalPath = savedInstanceState.getString(PictureConfig.BUNDLE_ORIGINAL_PATH);

        } else {
            config = PictureSelectionConfig.getInstance();
        }
        int themeStyleId = config.themeStyleId;
        setTheme(themeStyleId);
        super.onCreate(savedInstanceState);
        mContext = this;
        initConfig();
    }

    /**
     * 获取配置参数
     */
    private void initConfig() {
        camera = config.camera;
        outputCameraPath = config.outputCameraPath;
        statusFont = AttrsUtils.getTypeValueBoolean
                (this, com.luck.picture.lib.R.attr.picture_statusFontColor);
        previewStatusFont = AttrsUtils.getTypeValueBoolean
                (this, com.luck.picture.lib.R.attr.picture_preview_statusFontColor);
        mimeType = config.mimeType;
        selectionMedias = config.selectionMedias;
        if (selectionMedias == null) {
            selectionMedias = new ArrayList<>();
        }
        selectionMode = config.selectionMode;
        if (selectionMode == PictureConfig.SINGLE) {
            selectionMedias = new ArrayList<>();
        }
        spanCount = config.imageSpanCount;
        isGif = config.isGif;
        isCamera = config.isCamera;
        freeStyleCropEnabled = config.freeStyleCropEnabled;
        maxSelectNum = config.maxSelectNum;
        minSelectNum = config.minSelectNum;
        enablePreview = config.enablePreview;
        enPreviewVideo = config.enPreviewVideo;
        checkNumMode = config.checkNumMode = AttrsUtils.getTypeValueBoolean
                (this, com.luck.picture.lib.R.attr.picture_style_checkNumMode);
        openClickSound = config.openClickSound;
        videoSecond = config.videoSecond;
        enableCrop = config.enableCrop;
        isCompress = config.isCompress;
        compressQuality = config.cropCompressQuality;
        numComplete = AttrsUtils.getTypeValueBoolean(this, com.luck.picture.lib.R.attr.picture_style_numComplete);
        compressMaxKB = config.compressMaxkB;
        compressMode = config.compressMode;
        compressGrade = config.compressGrade;
        compressWidth = config.compressWidth;
        compressHeight = config.compressHeight;
        recordVideoSecond = config.recordVideoSecond;
        videoQuality = config.videoQuality;
        cropWidth = config.cropWidth;
        cropHeight = config.cropHeight;
        aspect_ratio_x = config.aspect_ratio_x;
        aspect_ratio_y = config.aspect_ratio_y;
        circleDimmedLayer = config.circleDimmedLayer;
        showCropFrame = config.showCropFrame;
        showCropGrid = config.showCropGrid;
        rotateEnabled = config.rotateEnabled;
        scaleEnabled = config.scaleEnabled;
        previewEggs = config.previewEggs;
        hideBottomControls = config.hideBottomControls;

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(PictureConfig.BUNDLE_CAMERA_PATH, cameraPath);
        outState.putString(PictureConfig.BUNDLE_ORIGINAL_PATH, originalPath);
        outState.putSerializable(PictureConfig.EXTRA_CONFIG, config);
    }

    protected void startActivity(Class clz, Bundle bundle) {
        if (!DoubleUtils.isFastDoubleClick()) {
            Intent intent = new Intent();
            intent.setClass(this, clz);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    protected void startActivity(Class clz, Bundle bundle, int requestCode) {
        if (!DoubleUtils.isFastDoubleClick()) {
            Intent intent = new Intent();
            intent.setClass(this, clz);
            intent.putExtras(bundle);
            startActivityForResult(intent, requestCode);
        }
    }

    protected void showToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * loading dialog
     */
    protected void showPleaseDialog() {
        if (!isFinishing()) {
            dismissDialog();
            dialog = new PictureDialog(this);
            dialog.show();
        }
    }

    /**
     * dismiss dialog
     */
    protected void dismissDialog() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * compress loading dialog
     */
    protected void showCompressDialog() {
        if (!isFinishing()) {
            dismissCompressDialog();
            compressDialog = new PictureDialog(this);
            compressDialog.show();
        }
    }

    /**
     * dismiss compress dialog
     */
    protected void dismissCompressDialog() {
        try {
            if (!isFinishing()
                    && compressDialog != null
                    && compressDialog.isShowing()) {
                compressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * compressImage
     */
    protected void compressImage(final List<LocalMedia> result) {
        showCompressDialog();
        CompressConfig compress_config = CompressConfig.ofDefaultConfig();
        switch (compressMode) {
            case PictureConfig.SYSTEM_COMPRESS_MODE:
                // 系统自带压缩
                compress_config.enablePixelCompress(true);
                compress_config.enableQualityCompress(true);
                compress_config.setMaxSize(compressMaxKB);
                break;
            case PictureConfig.LUBAN_COMPRESS_MODE:
                // luban压缩
                LubanOptions option = new LubanOptions.Builder()
                        .setMaxHeight(compressHeight)
                        .setMaxWidth(compressWidth)
                        .setMaxSize(compressMaxKB)
                        .setGrade(compressGrade)
                        .create();
                compress_config = CompressConfig.ofLuban(option);
                break;
        }

        CompressImageOptions.compress(this, compress_config, result,
                new CompressInterface.CompressListener() {
                    @Override
                    public void onCompressSuccess(List<LocalMedia> images) {
                        RxBus.getDefault().post(new EventEntity(PictureConfig.CLOSE_PREVIEW_FLAG));
                        onResult(images);
                    }

                    @Override
                    public void onCompressError(List<LocalMedia> images, String msg) {
                        RxBus.getDefault().post(new EventEntity(PictureConfig.CLOSE_PREVIEW_FLAG));
                        onResult(result);
                    }
                }).compress();
    }

    /**
     * 去裁剪
     *
     * @param originalPath
     */
    protected void startCrop(String originalPath) {
        UCrop.Options options = new UCrop.Options();
        int toolbarColor = AttrsUtils.getTypeValueColor(this, com.luck.picture.lib.R.attr.picture_crop_toolbar_bg);
        int statusColor = AttrsUtils.getTypeValueColor(this, com.luck.picture.lib.R.attr.picture_crop_status_color);
        int titleColor = AttrsUtils.getTypeValueColor(this, com.luck.picture.lib.R.attr.picture_crop_title_color);
        options.setToolbarColor(toolbarColor);
        options.setStatusBarColor(statusColor);
        options.setToolbarWidgetColor(titleColor);
        options.setCircleDimmedLayer(circleDimmedLayer);
        options.setShowCropFrame(showCropFrame);
        options.setShowCropGrid(showCropGrid);
        options.setCompressionQuality(compressQuality);
        options.setHideBottomControls(hideBottomControls);
        options.setFreeStyleCropEnabled(freeStyleCropEnabled);
        boolean isHttp = PictureMimeType.isHttp(originalPath);
        Uri uri = isHttp ? Uri.parse(originalPath) : Uri.fromFile(new File(originalPath));
        UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), System.currentTimeMillis() + ".jpg")))
                .withAspectRatio(aspect_ratio_x, aspect_ratio_y)
                .withMaxResultSize(cropWidth, cropHeight)
                .withOptions(options)
                .start(this);
    }

    /**
     * 多图去裁剪
     *
     * @param list
     */
    protected void startCrop(ArrayList<String> list) {
        UCropMulti.Options options = new UCropMulti.Options();
        int toolbarColor = AttrsUtils.getTypeValueColor(this, com.luck.picture.lib.R.attr.picture_crop_toolbar_bg);
        int statusColor = AttrsUtils.getTypeValueColor(this, com.luck.picture.lib.R.attr.picture_crop_status_color);
        int titleColor = AttrsUtils.getTypeValueColor(this, com.luck.picture.lib.R.attr.picture_crop_title_color);
        options.setToolbarColor(toolbarColor);
        options.setStatusBarColor(statusColor);
        options.setToolbarWidgetColor(titleColor);
        options.setCircleDimmedLayer(circleDimmedLayer);
        options.setShowCropFrame(showCropFrame);
        options.setShowCropGrid(showCropGrid);
        options.setScaleEnabled(scaleEnabled);
        options.setRotateEnabled(rotateEnabled);
        options.setHideBottomControls(true);
        options.setCompressionQuality(compressQuality);
        options.setCutListData(list);
        options.setFreeStyleCropEnabled(freeStyleCropEnabled);
        String path = list.size() > 0 ? list.get(0) : "";
        boolean isHttp = PictureMimeType.isHttp(path);
        Uri uri = isHttp ? Uri.parse(path) : Uri.fromFile(new File(path));
        UCropMulti.of(uri, Uri.fromFile(new File(getCacheDir(), System.currentTimeMillis() + ".jpg")))
                .withAspectRatio(aspect_ratio_x, aspect_ratio_y)
                .withMaxResultSize(cropWidth, cropHeight)
                .withOptions(options)
                .start(this);
    }

    /**
     * 多图去裁剪
     *
     * @param list
     */
    protected void startToolCrop(ArrayList<String> list, boolean isNetHttp, Class otherActivity) {
        UCropMulti.Options options = new UCropMulti.Options();
        int toolbarColor = AttrsUtils.getTypeValueColor(this, com.luck.picture.lib.R.attr.picture_crop_toolbar_bg);
        int statusColor = AttrsUtils.getTypeValueColor(this, com.luck.picture.lib.R.attr.picture_crop_status_color);
        int titleColor = AttrsUtils.getTypeValueColor(this, com.luck.picture.lib.R.attr.picture_crop_title_color);
        options.setToolbarColor(toolbarColor);
        options.setStatusBarColor(statusColor);
        options.setToolbarWidgetColor(titleColor);
        options.setCircleDimmedLayer(circleDimmedLayer);
        options.setShowCropFrame(true);
        options.setShowCropGrid(true);
        options.setScaleEnabled(true);
        options.setRotateEnabled(true);
        options.setHideBottomControls(true);
        options.setCompressionQuality(100);
        options.setCutListData(list);
        options.setFreeStyleCropEnabled(true);
        String path = list.size() > 0 ? list.get(0) : "";

        boolean isHttp = isNetHttp;
        // Uri uri = isHttp ? Uri.parse(path) : Uri.fromFile(new File(path));
        Uri uri = Uri.fromFile(new File(path));
        String Dcim_Path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        String IMAGES_CROP_CASH_PATH = "/booyertool/booyertool_image_save/crop";
        File downloadDirectory = new File(Dcim_Path + IMAGES_CROP_CASH_PATH);
        if (!downloadDirectory.exists()) {
            downloadDirectory.mkdirs();
        }
        UCropMulti.of(uri, Uri.fromFile(new File(Dcim_Path + IMAGES_CROP_CASH_PATH, System.currentTimeMillis() + ".jpg")))
                .withAspectRatio(0, 0)
                .withMaxResultSize(cropWidth, cropHeight)
                .withOptions(options)
                .startTool(this, otherActivity);
    }

    /**
     * 判断拍照 图片是否旋转
     *
     * @param degree
     * @param file
     */
    protected void rotateImage(int degree, File file) {
        if (degree > 0) {
            // 针对相片有旋转问题的处理方式
            try {
                BitmapFactory.Options opts = new BitmapFactory.Options();//获取缩略图显示到屏幕上
                opts.inSampleSize = 2;
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
                Bitmap bmp = PictureFileUtils.rotaingImageView(degree, bitmap);
                PictureFileUtils.saveBitmapFile(bmp, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * compress or callback
     *
     * @param result
     */
    protected void handlerResult(List<LocalMedia> result) {
        if (isCompress) {
            compressImage(result);
        } else {
            onResult(result);
        }
    }
    protected void handlerResultx(List<LocalMedia> result) {
        if (isCompress) {
            compressImage(result);
        } else {
            onResultx(result);
        }
    }

    /**
     * 如果没有任何相册，先创建一个最近相册出来
     *
     * @param folders
     */
    protected void createNewFolder(List<LocalMediaFolder> folders) {
        if (folders.size() == 0) {
            // 没有相册 先创建一个最近相册出来
            LocalMediaFolder newFolder = new LocalMediaFolder();
            String folderName = mimeType == PictureMimeType.ofAudio() ?
                    getString(com.luck.picture.lib.R.string.picture_all_audio) : getString(com.luck.picture.lib.R.string.picture_camera_roll);
            newFolder.setName(folderName);
            newFolder.setPath("");
            newFolder.setFirstImagePath("");
            folders.add(newFolder);
        }
    }

    /**
     * 将图片插入到相机文件夹中
     *
     * @param path
     * @param imageFolders
     * @return
     */
    protected LocalMediaFolder getImageFolder(String path, List<LocalMediaFolder> imageFolders) {
        File imageFile = new File(path);
        File folderFile = imageFile.getParentFile();

        for (LocalMediaFolder folder : imageFolders) {
            if (folder.getName().equals(folderFile.getName())) {
                return folder;
            }
        }
        LocalMediaFolder newFolder = new LocalMediaFolder();
        newFolder.setName(folderFile.getName());
        newFolder.setPath(folderFile.getAbsolutePath());
        newFolder.setFirstImagePath(path);
        imageFolders.add(newFolder);
        return newFolder;
    }

    /**
     * return image result
     *
     * @param images
     */
    protected void onResult(List<LocalMedia> images) {
        dismissCompressDialog();
        if (camera
                && selectionMode == PictureConfig.MULTIPLE
                && selectionMedias != null) {
            images.addAll(selectionMedias);
        }
        Intent intent = PictureSelector.putIntentResult(images);
        setResult(RESULT_OK, intent);
        closeActivity();
    }
    protected void onResultx(List<LocalMedia> images) {
        dismissCompressDialog();
        if (camera
                && selectionMode == PictureConfig.MULTIPLE
                && selectionMedias != null) {
            images.addAll(selectionMedias);
        }
        Intent intent = PictureSelector.putIntentResult(images);
        setResult(RESULT_OK, intent);
       // closeActivity();
    }

    /**
     * Close Activity
     */
    protected void closeActivity() {
        finish();
        overridePendingTransition(0, com.luck.picture.lib.R.anim.a3);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LastActivitys.getInstance().removeView(getWindow().getDecorView());
        dismissCompressDialog();
        dismissDialog();
    }


    /**
     * 获取DCIM文件下最新一条拍照记录
     *
     * @return
     */
    protected int getLastImageId(boolean eqVideo) {
        try {
            //selection: 指定查询条件
            String absolutePath = PictureFileUtils.getDCIMCameraPath();
            String ORDER_BY = MediaStore.Files.FileColumns._ID + " DESC";
            String selection = eqVideo ? MediaStore.Video.Media.DATA + " like ?" :
                    MediaStore.Images.Media.DATA + " like ?";
            //定义selectionArgs：
            String[] selectionArgs = {absolutePath + "%"};
            Cursor imageCursor = this.getContentResolver().query(eqVideo ?
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                            : MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                    selection, selectionArgs, ORDER_BY);
            if (imageCursor.moveToFirst()) {
                int id = imageCursor.getInt(eqVideo ?
                        imageCursor.getColumnIndex(MediaStore.Video.Media._ID)
                        : imageCursor.getColumnIndex(MediaStore.Images.Media._ID));
                long date = imageCursor.getLong(eqVideo ?
                        imageCursor.getColumnIndex(MediaStore.Video.Media.DURATION)
                        : imageCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                int duration = DateUtils.dateDiffer(date);
                imageCursor.close();
                // DCIM文件下最近时间30s以内的图片，可以判定是最新生成的重复照片
                return duration <= 30 ? id : -1;
            } else {
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 删除部分手机 拍照在DCIM也生成一张的问题
     *
     * @param id
     * @param eqVideo
     */
    protected void removeImage(int id, boolean eqVideo) {
        try {
            ContentResolver cr = getContentResolver();
            Uri uri = eqVideo ? MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    : MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String selection = eqVideo ? MediaStore.Video.Media._ID + "=?"
                    : MediaStore.Images.Media._ID + "=?";
            cr.delete(uri,
                    selection,
                    new String[]{Long.toString(id)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 录音
     *
     * @param data
     */
    protected void isAudio(Intent data) {
        if (data != null && mimeType == PictureMimeType.ofAudio()) {
            try {
                Uri uri = data.getData();
                String audioPath;
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                    audioPath = uri.getPath();
                } else {
                    audioPath = getAudioFilePathFromUri(uri);
                }
                PictureFileUtils.copyAudioFile(audioPath, cameraPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取刚录取的音频文件
     *
     * @param uri
     * @return
     */
    protected String getAudioFilePathFromUri(Uri uri) {
        String path = "";
        try {
            Cursor cursor = getContentResolver()
                    .query(uri, null, null, null, null);
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
            path = cursor.getString(index);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }
}
