package com.nahuo.buyertool.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.luck.picture.lib.PictureBaseActivity;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.dialog.CustomDialog;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.RxPermissions;
import com.luck.picture.lib.tools.DebugUtil;
import com.luck.picture.lib.tools.ScreenUtils;
import com.luck.picture.lib.widget.PreviewViewPager;
import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.ViewHub;
import com.nahuo.buyertool.common.Const;
import com.nahuo.buyertool.common.MediaStoreUtils;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.yalantis.ucrop.UCropMulti;
import com.yalantis.ucrop.model.CutInfo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import static com.nahuo.library.helper.ImageUrlExtends.getImageUrl;
import static com.yalantis.ucrop.UCropMulti.REQUEST_BUYER_CROP;

/**
 * project：PictureSelector
 */
public class PictureExternalPreviewToolActivity extends PictureBaseActivity implements View.OnClickListener {
    private ImageButton left_back;
    private TextView tv_title, picture_edit;
    private PreviewViewPager viewPager;
    private List<LocalMedia> images = new ArrayList<>();
    private int position = 0, mCurrentPos = 0;
    private String directory_path;
    private SimpleFragmentAdapter adapter;
    private LayoutInflater inflater;
    private RxPermissions rxPermissions;
    private loadDataThread loadDataThread;
    private String Dcim_Path;
    String fileName = "";
    ArrayList<String> mPicsList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 设置自定义标题栏
        setContentView(com.luck.picture.lib.R.layout.picture_activity_external_preview);
        Dcim_Path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        inflater = LayoutInflater.from(this);
        tv_title = (TextView) findViewById(com.luck.picture.lib.R.id.picture_title);
        picture_edit = (TextView) findViewById(com.luck.picture.lib.R.id.picture_edit);
        picture_edit.setVisibility(View.VISIBLE);
        picture_edit.setOnClickListener(this);
        left_back = (ImageButton) findViewById(com.luck.picture.lib.R.id.left_back);
        viewPager = (PreviewViewPager) findViewById(com.luck.picture.lib.R.id.preview_pager);
        position = getIntent().getIntExtra(PictureConfig.EXTRA_POSITION, 0);
        mCurrentPos = getIntent().getIntExtra(PictureConfig.EXTRA_POSITION, 0);
        directory_path = getIntent().getStringExtra(PictureConfig.DIRECTORY_PATH);
        images = (List<LocalMedia>) getIntent().getSerializableExtra(PictureConfig.EXTRA_PREVIEW_SELECT_LIST);
        mPicsList=getIntent().getStringArrayListExtra(EditImageActivity.EXTRA_PICS_LIST);
        left_back.setOnClickListener(this);
        initViewPageAdapterData();
    }

    private void initViewPageAdapterData() {
        tv_title.setText(position + 1 + "/" + images.size());
        adapter = new SimpleFragmentAdapter();
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tv_title.setText(position + 1 + "/" + images.size());
                mCurrentPos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_back:
                finish();
                overridePendingTransition(0, com.luck.picture.lib.R.anim.a3);
                break;
            case R.id.picture_edit:
                // 是图片和选择压缩并且是多张，调用批量压缩
                ArrayList<String> medias = new ArrayList<>();
                if (images.get(mCurrentPos).is_upload()) {
                    try {
                        String url = getImageUrl(images.get(mCurrentPos).getPath(), 21);
                        if (TextUtils.isEmpty(url)) {
                            ViewHub.showShortToast(PictureExternalPreviewToolActivity.this, "图片地址不能为空");
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
                                startPutCrop(this,cacheFile.getPath(),getCropNewpath(),mPicsList,REQUEST_BUYER_CROP);
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
                    startPutCrop(this,images.get(mCurrentPos).getPath(),getCropNewpath(),mPicsList,REQUEST_BUYER_CROP);
                }


                // ViewHub.showLongToast(getApplicationContext(), images.get(mCurrentPos).getPath());
                break;
        }

    }
    public String getCropNewpath(){
        File file=new File(Dcim_Path + "/" + Const.IMAGES_CROP_CASH_PATH);
        if (!file.exists()){
            file.mkdirs();
        }
        return Dcim_Path + "/" + Const.IMAGES_CROP_CASH_PATH+"/"+System.currentTimeMillis()+".jpg";
    }
    public void startPutCrop(Activity context, final String editImagePath, final String outputPath, final ArrayList<String> picList,final int requestCode) {
          EditImageActivity.start(context,editImagePath,outputPath,picList,requestCode);
    }
    private void handleEditorImage(Intent data) {
        String newFilePath = data.getStringExtra(EditImageActivity.EXTRA_OUTPUT);
        boolean isImageEdit = data.getBooleanExtra(EditImageActivity.IMAGE_IS_EDIT, false);

        if (isImageEdit){

        }else{//未编辑  还是用原来的图片
          //  newFilePath = data.getStringExtra(EditImageActivity.FILE_PATH);

        }
        //System.out.println("newFilePath---->" + newFilePath);
        //File file = new File(newFilePath);
        //System.out.println("newFilePath size ---->" + (file.length() / 1024)+"KB");
        Log.d("image is edit", isImageEdit + "");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            List<LocalMedia> medias = new ArrayList<>();
            LocalMedia media;
            String imageType;
            switch (requestCode) {
                case REQUEST_BUYER_CROP:
                    String newFilePath = data.getStringExtra(EditImageActivity.EXTRA_OUTPUT);
                    boolean isImageEdit = data.getBooleanExtra(EditImageActivity.IMAGE_IS_EDIT, false);
                    if (isImageEdit){
                        MediaStoreUtils.scanImageFile(mContext, newFilePath);
                        media = new LocalMedia();
                        imageType = PictureMimeType.createImageType(newFilePath);
                        media.setCut(true);
                        media.setmCurrentPos(mCurrentPos);
                        media.setPath(newFilePath);
                        media.setCutPath(newFilePath);
                        media.setPictureType(imageType);
                        media.setMimeType(mimeType);
                        medias.add(media);
                        handlerResult(medias);
                    }else{//未编辑  还是用原来的图片
                        //  newFilePath = data.getStringExtra(EditImageActivity.FILE_PATH);
                    }

                    break;
                case UCropMulti.REQUEST_MULTI_CROP:
                    List<CutInfo> cuts = UCropMulti.getOutput(data);
                    for (CutInfo c : cuts) {
                        media = new LocalMedia();
                        imageType = PictureMimeType.createImageType(c.getPath());
                        media.setCut(true);
                        media.setmCurrentPos(mCurrentPos);
                        media.setPath(c.getPath());
                        media.setCutPath(c.getCutPath());
                        media.setPictureType(imageType);
                        media.setMimeType(mimeType);
                        medias.add(media);
                    }
                    handlerResult(medias);
                    break;
            }
        } else if (resultCode == UCropMulti.RESULT_ERROR) {
            ViewHub.showShortToast(PictureExternalPreviewToolActivity.this, "裁截失败");
        }

    }

    String path = "";

    public class SimpleFragmentAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            (container).removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View contentView = inflater.inflate(com.luck.picture.lib.R.layout.picture_image_preview, container, false);
            final PhotoView imageView = (PhotoView) contentView.findViewById(com.luck.picture.lib.R.id.preview_image);
            final LocalMedia media = images.get(position);
            if (media != null) {
                final String pictureType = media.getPictureType();

                if (media.isCut() && !media.isCompressed()) {
                    // 裁剪过
                    path = media.getCutPath();
                } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                    // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                    path = media.getCompressPath();
                } else {
                    path = media.getPath();
                }
                if (media.is_upload()) {
                    path = getImageUrl(path);
                }
                boolean isHttp = PictureMimeType.isHttp(path);
                // 可以长按保存并且是网络图片显示一个对话框
                if (isHttp) {
                    showPleaseDialog();
                }

                boolean isGif = PictureMimeType.isGif(pictureType);
                // 压缩过的gif就不是gif了
                if (isGif && !media.isCompressed()) {
                    RequestOptions gifOptions = new RequestOptions()
                            .override(480, 800)
                            .priority(Priority.HIGH)
                            .diskCacheStrategy(DiskCacheStrategy.NONE);

                    Glide.with(PictureExternalPreviewToolActivity.this)
                            .asGif()
                            .apply(gifOptions)
                            .load(path)
                            .listener(new RequestListener<GifDrawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model
                                        , Target<GifDrawable> target, boolean isFirstResource) {
                                    dismissDialog();
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GifDrawable resource, Object model
                                        , Target<GifDrawable> target, DataSource dataSource,
                                                               boolean isFirstResource) {
                                    dismissDialog();
                                    return false;
                                }
                            })
                            .into(imageView);
                } else {
                    RequestOptions options = new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL);
                           // .override(480, 800);

                    Glide.with(PictureExternalPreviewToolActivity.this)
                            .load(path)
                            .apply(options)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model
                                        , Target<Drawable> target, boolean isFirstResource) {
                                    dismissDialog();
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model,
                                                               Target<Drawable> target,
                                                               DataSource dataSource,
                                                               boolean isFirstResource) {
                                    dismissDialog();
                                    return false;
                                }
                            })
                            .into(imageView);
                }
                imageView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                    @Override
                    public void onViewTap(View view, float x, float y) {
                        finish();
                        overridePendingTransition(0, com.luck.picture.lib.R.anim.a3);
                    }
                });

                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (rxPermissions == null) {
                            rxPermissions = new RxPermissions(PictureExternalPreviewToolActivity.this);
                        }
                        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .subscribe(new Observer<Boolean>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {
                                    }

                                    @Override
                                    public void onNext(Boolean aBoolean) {
                                        if (aBoolean) {
                                            String path = "";
                                            LocalMedia media = images.get(position);
                                            if (media.is_upload()) {
                                                path = getImageUrl(media.getPath(), 21);
                                            } else {
                                                path = media.getPath();
                                            }
                                            showDownLoadDialog(path, media.is_upload());
                                        } else {
                                            showToast(getString(com.luck.picture.lib.R.string.picture_jurisdiction));
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                    }

                                    @Override
                                    public void onComplete() {
                                    }
                                });
                        return true;
                    }
                });
            }
            (container).addView(contentView, 0);
            return contentView;
        }
    }

//    /**
//     * 获取图片的原图地址
//     *
//     * @param source 原图片路径
//     */
//    public String getImageUrl(String source) {
//
//        if (TextUtils.isEmpty(source))
//            return "";
//
//        if (source.startsWith("http:")) {
//            return source;
//        } else if (source.startsWith("/u")) {
//            return MessageFormat.format("http://img4.nahuo.com{0}", source);
//        } else if (source.startsWith("upyun:")) {
//            String[] datas = source.split(":");
//            if (datas == null || datas.length != 3)
//                return source;
//            return MessageFormat.format("http://{0}.b0.upaiyun.com{1}", datas[1], datas[2]);
//        } else {
//            return MessageFormat.format("http://img3.nahuo.com/{0}.jpg", source);
//        }
//    }

    /**
     * 下载图片提示
     */
    private void showDownLoadDialog(final String path, final boolean is_upload) {
        final CustomDialog dialog = new CustomDialog(PictureExternalPreviewToolActivity.this,
                ScreenUtils.getScreenWidth(PictureExternalPreviewToolActivity.this) * 3 / 4,
                ScreenUtils.getScreenHeight(PictureExternalPreviewToolActivity.this) / 4,
                com.luck.picture.lib.R.layout.picture_wind_base_dialog_xml, com.luck.picture.lib.R.style.Theme_dialog);
        Button btn_cancel = (Button) dialog.findViewById(com.luck.picture.lib.R.id.btn_cancel);
        Button btn_commit = (Button) dialog.findViewById(com.luck.picture.lib.R.id.btn_commit);
        TextView tv_title = (TextView) dialog.findViewById(com.luck.picture.lib.R.id.tv_title);
        TextView tv_content = (TextView) dialog.findViewById(com.luck.picture.lib.R.id.tv_content);
        tv_title.setText(getString(com.luck.picture.lib.R.string.picture_prompt));
        tv_content.setText(getString(com.luck.picture.lib.R.string.picture_prompt_content));
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showPleaseDialog();
                boolean isHttp = PictureMimeType.isHttp(path);
                if (is_upload) {
                    try {
                        String url = path;
                        if (TextUtils.isEmpty(url)) {
                            ViewHub.showShortToast(PictureExternalPreviewToolActivity.this, "图片地址不能为空");
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
                                ViewHub.showShortToast(PictureExternalPreviewToolActivity.this, "图片已经下载在本地" + Dcim_Path + "/" + Const.IMAGES_CASH_PATH);
                            } else {
                                showPleaseDialog();
                                loadDataThread = new loadDataThread(path, 1);
                                loadDataThread.start();
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    showToast("图片已经在本地" + path);
                }
//                if (isHttp) {
//                    loadDataThread = new loadDataThread(path);
//                    loadDataThread.start();
//                } else {
//                    // 有可能本地图片
//                    try {
//                        String dirPath = PictureFileUtils.createDir(PictureExternalPreviewToolActivity.this,
//                                System.currentTimeMillis() + ".png", directory_path);
//                        PictureFileUtils.copyFile(path, dirPath);
//                        showToast(getString(com.luck.picture.lib.R.string.picture_save_success) + "\n" + dirPath);
//                        dismissDialog();
//                    } catch (IOException e) {
//                        showToast(getString(com.luck.picture.lib.R.string.picture_save_error) + "\n" + e.getMessage());
//                        dismissDialog();
//                        e.printStackTrace();
//                    }
//                }
                dialog.dismiss();
            }
        });
        dialog.show();
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
            ViewHub.showShortToast(PictureExternalPreviewToolActivity.this, "网络图片获取失败" + "\n" + e.getMessage());
            e.printStackTrace();
        }
    }

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
            ViewHub.showShortToast(PictureExternalPreviewToolActivity.this, getString(R.string.picture_save_error) + "\n" + e.getMessage());
            e.printStackTrace();
        }
    }
//    // 下载图片保存至手机
//    public void showLoadingImage(String urlPath) {
//        try {
//            URL u = new URL(urlPath);
//            String path = PictureFileUtils.createDir(PictureExternalPreviewToolActivity.this,
//                    System.currentTimeMillis() + ".png", directory_path);
//            byte[] buffer = new byte[1024 * 8];
//            int read;
//            int ava = 0;
//            long start = System.currentTimeMillis();
//            BufferedInputStream bin;
//            bin = new BufferedInputStream(u.openStream());
//            BufferedOutputStream bout = new BufferedOutputStream(
//                    new FileOutputStream(path));
//            while ((read = bin.read(buffer)) > -1) {
//                bout.write(buffer, 0, read);
//                ava += read;
//                long speed = ava / (System.currentTimeMillis() - start);
//                DebugUtil.i("Download: " + ava + " byte(s)"
//                        + "    avg speed: " + speed + "  (kb/s)");
//            }
//            bout.flush();
//            bout.close();
//            Message message = handler.obtainMessage();
//            message.what = 200;
//            message.obj = path;
//            handler.sendMessage(message);
//        } catch (IOException e) {
//            showToast(getString(com.luck.picture.lib.R.string.picture_save_error) + "\n" + e.getMessage());
//            e.printStackTrace();
//        }
//    }


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
                    startPutCrop(PictureExternalPreviewToolActivity.this,path1,getCropNewpath(),mPicsList,REQUEST_BUYER_CROP);
//                    medias.add(path1);
//                    startToolCrop(medias, false, PictureMultiCuttingToolActivity.class);
                    dismissDialog();
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0, com.luck.picture.lib.R.anim.a3);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadDataThread != null) {
            handler.removeCallbacks(loadDataThread);
            loadDataThread = null;
        }
    }
}
