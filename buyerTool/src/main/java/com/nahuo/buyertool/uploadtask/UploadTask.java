package com.nahuo.buyertool.uploadtask;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Message;
import android.util.Log;

import com.nahuo.buyertool.BWApplication;
import com.nahuo.buyertool.Bean.UploadBean;
import com.nahuo.buyertool.MeSettingActivity;
import com.nahuo.buyertool.activity.UploadItemActivity;
import com.nahuo.buyertool.api.UploadItemAPI;
import com.nahuo.buyertool.common.Const;
import com.nahuo.buyertool.common.ListUtils;
import com.nahuo.buyertool.common.SpManager;
import com.nahuo.buyertool.db.ToolUploadDbHelper;
import com.nahuo.buyertool.model.PublicData;
import com.nahuo.buyertool.service.UploadManager;
import com.nahuo.buyertool.upyun.UpYunConst;
import com.nahuo.buyertool.upyun.UpYunNewUtls;
import com.nahuo.buyertool.utils.DateUtls;
import com.nahuo.buyertool.utils.ImageUtls;
import com.nahuo.buyertool.utils.WaterWarkSave;
import com.nahuo.library.helper.FunctionHelper;
import com.nahuo.library.helper.ImageUrlExtends;
import com.upyun.library.common.Params;
import com.upyun.library.common.UploadEngine;
import com.upyun.library.listener.UpCompleteListener;
import com.upyun.library.listener.UpProgressListener;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nahuo.buyertool.common.SpManager.getUserId;
import static com.nahuo.buyertool.upyun.UpYunConst.OPERATER;
import static com.nahuo.buyertool.utils.ImageUtls.getBitmap;
import static com.upyun.library.common.Params.SAVE_KEY;
import static com.upyun.library.utils.UpYunUtils.md5Hex;

/**
 * Created by jame on 2017/8/3.
 */

public class UploadTask extends PriorityAsyncTask<Void, UploadBean, UploadBean> {
    private static final int BUFFER_SIZE = 1024 * 8; //读写缓存大小
    UploadUIHandler mUploadUIHandler;
    private long mPreviousTime;                      //上次更新的时间，用于计算下载速度
    private boolean isRestartTask;                   //是否重新下载的标识位
    private boolean isPause;                         //当前任务是暂停还是停止， true 暂停， false 停止
    UploadBean uploadBean;
    Context context;

    public UploadTask(UploadBean uploadBean, boolean isRestart, UploadListener uloadListener, Context context) {
        this.uploadBean = uploadBean;
        this.isRestartTask = isRestart;
        this.context = context;
        UploadEngine.getInstance().setIsCancel(false);
        uploadBean.setListener(uloadListener);
        mUploadUIHandler = UploadManager.getInstance(BWApplication.getInstance()).getHandler();
        //将当前任务在定义的线程池中执行
        executeOnExecutor(UploadManager.getInstance(BWApplication.getInstance()).getThreadPool().getExecutor());

    }

    /**
     * 暂停的方法
     */
    public void pause() {
        if (uploadBean.getUploadStatus().equals(Const.UploadStatus.UPLOADING)) {
            //如果是等待状态的,因为该状态取消不会回调任何方法，需要手动触发
            //uploadBean.setNetworkSpeed(0);
            uploadBean.setUploadStatus(Const.UploadStatus.UPLOAD_FAILED);
            postMessage(null, null);
        } else {
            isPause = true;
        }
        super.cancel(false);
    }

    /**
     * 停止的方法
     */
    public void stop() {
        if (uploadBean.getUploadStatus().equals(Const.UploadStatus.UPLOAD_FAILED) ||
                uploadBean.getUploadStatus().equals(Const.UploadStatus.UPLOADING)
                || uploadBean.getUploadStatus().equals(Const.UploadStatus.UPLOAD_WAIT)) {
            //如果状态是暂停或错误的,停止不会回调任何方法，需要手动触发
            // uploadBean.setNetworkSpeed(0);
            uploadBean.setProgress(0);
            uploadBean.setUploadStatus(Const.UploadStatus.UPLOAD_FAILED);
            uploadBean.setMessage("上传手动取消-已取消");
            ToolUploadDbHelper.getInstance(context).updateMessage(uploadBean);
            postMessage(null, null);
        } else {
            isPause = false;
        }
        super.cancel(false);
    }

    /**
     * 每个任务进队列的时候，都会执行该方法
     */
    @Override
    protected void onPreExecute() {
        //添加成功的回调
        UploadListener listener = uploadBean.getListener();
        if (listener != null) listener.onAdd(uploadBean);
        //如果是重新下载，需要删除临时文件
        if (isRestartTask) {
//            HttpUtils.deleteFile(uploadBean.getTargetPath());
            uploadBean.setProgress(0);
//            uploadBean.setDownloadLength(0);
//            uploadBean.setTotalLength(0);
            isRestartTask = false;
        }
        //uploadBean.setNetworkSpeed(0);
        uploadBean.setUploadStatus(Const.UploadStatus.UPLOAD_WAIT);
        postMessage(null, null);
    }

    /**
     * 如果调用了Cancel，就不会执行该方法，所以任务结束的回调不放在这里面
     */
    @Override
    protected void onPostExecute(UploadBean uploadBean) {
    }


    public boolean hasVideos(List<UploadBean.MediaBean> list) {
        boolean flag = false;
        if (ListUtils.isEmpty(list)) {
            flag = false;
        } else {
            for (UploadBean.MediaBean bean : list) {
                if (!bean.is_upload()) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    public boolean hasColorPics(List<UploadBean.ColorPicsBean> list) {
        boolean flag = false;
        if (ListUtils.isEmpty(list)) {
            flag = false;
        } else {
            for (UploadBean.ColorPicsBean bean : list) {
                if (!bean.is_upload()) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    public boolean hasPics(List<UploadBean.MediaBean> list) {
        boolean flag = false;
        if (ListUtils.isEmpty(list)) {
            flag = false;
        } else {
            for (UploadBean.MediaBean bean : list) {
                if (!bean.is_upload()) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    //获取需要色卡上传下标
    public int getColorNoUploadIndex(List<UploadBean.ColorPicsBean> list) {
        int index = 0;
        if (!ListUtils.isEmpty(list)) {
            for (int i = 0; i < list.size(); i++) {
                if (!list.get(i).is_upload()) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    //获取需要上传下标
    public int getNoUploadIndex(List<UploadBean.MediaBean> list) {
        int index = 0;
        if (!ListUtils.isEmpty(list)) {
            for (int i = 0; i < list.size(); i++) {
                if (!list.get(i).is_upload()) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    String videoPath = "", videoName = "";
    String picPath = "", picName = "", date = "", contentMd5 = "", policy = "", signature = "";
    String colorPicPath = "", colorPicName = "";
    long expiration;
    int index, cIndex;
    int vIndex;
    WaterWarkSave save;
    //图片进度
    UpProgressListener progressListener = new UpProgressListener() {
        @Override
        public void onRequestProgress(final long bytesWrite, final long contentLength) {
            if (isCancelled()) {
                UploadEngine.getInstance().cancleAll(uploadBean.getCreat_time());
                return;
            }
            uploadBean.setProgress((int) ((100 * bytesWrite) / contentLength));
            if (prelongTim == 0) {
                prelongTim = (new Date()).getTime();
                postMessage(null, null);
            } else {
                long curTime = (new Date()).getTime();//本地单击的时间
                Log.e("UploadTask", "图片间隔时间：" + (curTime - prelongTim));//计算本地和上次的时间差
                if (curTime - prelongTim >= Const.UPLOAD_IMG_INTERVAL) {
                    postMessage(null, null);
                    //当前单击事件变为上次时间
                    prelongTim = curTime;
                }
            }

        }
    };
    //图片结束回调，不可为空
    UpCompleteListener completeListener = new UpCompleteListener() {
        @Override
        public void onComplete(boolean isSuccess, String result) {
            prelongTim = 0;
            if (isCancelled()) {
                UploadEngine.getInstance().cancleAll(uploadBean.getCreat_time());
//                uploadBean.setUploadStatus(Const.UploadStatus.UPLOAD_FAILED);
//                uploadBean.setProgress(0);
//                uploadBean.setMessage("图片上传取消-已取消");
//                ToolUploadDbHelper.getInstance(context).updateMessage(uploadBean);
//                postMessage(null, null);

                return;
            }
            if (isSuccess) {
                uploadBean.getLocal_pics().get(index).setIs_upload(true);
                String serverPath = "upyun:" + UpYunConst.PIC_BUCKET + ":/" + picPath;
                uploadBean.getLocal_pics().get(index).setPath(serverPath);
                //更改数据库图片数据
                ToolUploadDbHelper.getInstance(context).updatePicList(uploadBean);
                postMessage(null, null);
                //删除水印图片
                ImageUtls.delFile(picName);
                if (!hasPics(uploadBean.getLocal_pics())) {
                    Log.i("---", index + "");
                    //已經傳完圖片
                    if (hasColorPics(uploadBean.getColorPics())) {
                        //上传颜色图片
                          uploadColorYPaiYun();
                    } else {
                        if (hasVideos(uploadBean.getLocal_videos())) {
                            //继续传视频
                            uploadYpYunVideos();
                        } else {
                            //调用接口结束
                            uploadData();
                        }
                    }

                } else {
                    //获取未上传下坐标
                    index = getNoUploadIndex(uploadBean.getLocal_pics());
                    getRecurPic(index);
                }
            } else {
                //中断图片上传
                uploadBean.setUploadStatus(Const.UploadStatus.UPLOAD_FAILED);
                uploadBean.setProgress(0);
                uploadBean.setMessage("图片又拍云上传失败" + "-" + "第" + (index + 1) + "张图片上传失败");
                //更改消息数据库
                ToolUploadDbHelper.getInstance(context).updateMessage(uploadBean);
                postMessage(null, null);
            }
        }
    };

    //颜色图片进度回调，可为空
    long prelongTim = 0, video_prelongTim = 0, cPrelongTim = 0;
    UpProgressListener cProgressListener = new UpProgressListener() {
        @Override
        public void onRequestProgress(final long bytesWrite, final long contentLength) {
            if (isCancelled()) {
                UploadEngine.getInstance().cancleAll(uploadBean.getCreat_time());
                return;
            }
            uploadBean.setProgress((int) ((100 * bytesWrite) / contentLength));
            if (cPrelongTim == 0) {
                cPrelongTim = (new Date()).getTime();
                postMessage(null, null);
            } else {
                long curTime = (new Date()).getTime();//本地单击的时间
                Log.e("UploadTask", "图片间隔时间：" + (curTime - cPrelongTim));//计算本地和上次的时间差
                if (curTime - cPrelongTim >= Const.UPLOAD_IMG_INTERVAL) {
                    postMessage(null, null);
                    //当前单击事件变为上次时间
                    cPrelongTim = curTime;
                }
            }

        }
    };
    //颜色图片结束回调，不可为空
    UpCompleteListener cCompleteListener = new UpCompleteListener() {
        @Override
        public void onComplete(boolean isSuccess, String result) {
            cPrelongTim = 0;
            if (isCancelled()) {
                UploadEngine.getInstance().cancleAll(uploadBean.getCreat_time());
//                uploadBean.setUploadStatus(Const.UploadStatus.UPLOAD_FAILED);
//                uploadBean.setProgress(0);
//                uploadBean.setMessage("图片上传取消-已取消");
//                ToolUploadDbHelper.getInstance(context).updateMessage(uploadBean);
//                postMessage(null, null);

                return;
            }
            if (isSuccess) {
                uploadBean.getColorPics().get(cIndex).setIs_upload(true);
                String serverPath = "upyun:" + UpYunConst.PIC_BUCKET + ":/" + colorPicPath;
                uploadBean.getColorPics().get(cIndex).setUrl(serverPath);
                //更改数据库颜色图片数据
                ToolUploadDbHelper.getInstance(context).updateColorPicList(uploadBean);
                postMessage(null, null);
                //删除水印图片
                 ImageUtls.delFile(colorPicName);
                if (!hasColorPics(uploadBean.getColorPics())) {
                    Log.i("---", cIndex + "");
                    //已經傳完颜色圖片
                    if (hasVideos(uploadBean.getLocal_videos())) {
                        //继续传视频
                        uploadYpYunVideos();
                    } else {
                        //调用接口结束
                        uploadData();
                    }
                } else {
                    //获取未上传下坐标
                    uploadColorYPaiYun();
                }
            } else {
                //中断颜色图片上传
                uploadBean.setUploadStatus(Const.UploadStatus.UPLOAD_FAILED);
                uploadBean.setProgress(0);
                uploadBean.setMessage("颜色图片又拍云上传失败" + "-" + "第" + (cIndex + 1) + "张图片上传失败");
                //更改消息数据库
                ToolUploadDbHelper.getInstance(context).updateMessage(uploadBean);
                postMessage(null, null);
            }
        }
    };

    //继续循环上传颜色图片
    private void uploadColorYPaiYun() {
        cIndex = getColorNoUploadIndex(uploadBean.getColorPics());
        getColorRecurPic(cIndex);
    }


    /**
     * 一旦该方法执行，意味着开始下载了
     */
    @Override
    protected UploadBean doInBackground(Void... params) {
        if (isCancelled())
            return uploadBean;
        mPreviousTime = System.currentTimeMillis();
        // uploadBean.setNetworkSpeed(0);
        uploadBean.setUploadStatus(Const.UploadStatus.UPLOADING);
        postMessage(null, null);
        if (uploadBean != null) {
            if (hasPics(uploadBean.getLocal_pics())) {
                //有需要上传图片
                index = getNoUploadIndex(uploadBean.getLocal_pics());
                getRecurPic(index);
            } else if (hasColorPics(uploadBean.getColorPics())) {
                //有需要上传的色卡图片
                 uploadColorYPaiYun();
            } else {
                //没有需要上传图片
                if (hasVideos(uploadBean.getLocal_videos())) {
                    //有需要上传视频
                    uploadYpYunVideos();
                } else {
                    //调用接口
                    uploadData();
                }
            }
        }
//       ExecutorService executor= Executors.newSingleThreadExecutor();
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i <= 30; i++) {
//                    uploadBean.setProgress(i);
//                    if (i == 30) {
//                        // uploadBean.setUploadStatus(Const.UploadStatus.UPLOAD_FINISH);
//                    }
//                    postMessage(null, null);
//                    if (isCancelled()) {
//                        uploadBean.setUploadStatus(Const.UploadStatus.UPLOAD_FAILED);
//                        uploadBean.setProgress(0);
//                        postMessage(null, null);
//                        break;
//                    }
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });

//        long startPos = uploadBean.getDownloadLength();
//        Response response;
//        try {
//            response = uploadBean.getRequest().headers("RANGE", "bytes=" + startPos + "-").execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//            uploadBean.setNetworkSpeed(0);
//            uploadBean.setState(DownloadManager.ERROR);
//            postMessage("网络异常", e);
//            return uploadBean;
//        }
//        int code = response.code();
//        if (code == 404 || code >= 500) {
//            uploadBean.setNetworkSpeed(0);
//            uploadBean.setState(DownloadManager.ERROR);
//            postMessage("服务器数据错误", null);
//            return uploadBean;
//        }
//        //构建下载文件路径，如果有设置，就用设置的，否者就自己创建
//        String url = uploadBean.getUrl();
//        String fileName = uploadBean.getFileName();
//        if (TextUtils.isEmpty(fileName)) {
//            fileName = HttpUtils.getNetFileName(response, url);
//            uploadBean.setFileName(fileName);
//        }
//        if (TextUtils.isEmpty(uploadBean.getTargetPath())) {
//            File targetFolder = new File(uploadBean.getTargetFolder());
//            if (!targetFolder.exists()) targetFolder.mkdirs();
//            File file = new File(targetFolder, fileName);
//            uploadBean.setTargetPath(file.getAbsolutePath());
//        }
//        //检查文件有效性，文件大小大于总文件大小
//        if (startPos > uploadBean.getTotalLength()) {
//            uploadBean.setNetworkSpeed(0);
//            uploadBean.setState(DownloadManager.ERROR);
//            postMessage("断点文件异常，需要删除后重新下载", null);
//            return uploadBean;
//        }
//        if (startPos == uploadBean.getTotalLength() && startPos > 0) {
//            uploadBean.setProgress(1.0f);
//            uploadBean.setNetworkSpeed(0);
//            uploadBean.setState(DownloadManager.FINISH);
//            postMessage(null, null);
//            return uploadBean;
//        }
//        //设置断点写文件
//        File file = new File(uploadBean.getTargetPath());
//        ProgressRandomAccessFile randomAccessFile;
//        try {
//            randomAccessFile = new ProgressRandomAccessFile(file, "rw", startPos);
//            randomAccessFile.seek(startPos);
//        } catch (Exception e) {
//            e.printStackTrace();
//            uploadBean.setNetworkSpeed(0);
//            uploadBean.setState(DownloadManager.ERROR);
//            postMessage("没有找到已存在的断点文件", e);
//            return uploadBean;
//        }
//        //获取流对象，准备进行读写文件
//        long totalLength = response.body().contentLength();
//        if (uploadBean.getTotalLength() == 0) {
//            uploadBean.setTotalLength(totalLength);
//        }
//        InputStream is = response.body().byteStream();
//        //读写文件流
//        try {
//            download(is, randomAccessFile);
//        } catch (IOException e) {
//            e.printStackTrace();
//            uploadBean.setNetworkSpeed(0);
//            uploadBean.setState(DownloadManager.ERROR);
//            postMessage("文件读写异常", e);
//            return uploadBean;
//        }
//
//        //循环结束走到这里，a.下载完成     b.暂停      c.判断是否下载出错
//        if (isCancelled()) {
//            uploadBean.setNetworkSpeed(0);
//            if (isPause) uploadBean.setState(DownloadManager.PAUSE); //暂停
//            else uploadBean.setState(DownloadManager.NONE);          //停止
//            postMessage(null, null);
//        } else if (file.length() == uploadBean.getTotalLength() && uploadBean.getState() == DownloadManager.DOWNLOADING) {
//            uploadBean.setNetworkSpeed(0);
//            uploadBean.setState(DownloadManager.FINISH); //下载完成
//            postMessage(null, null);
//        } else if (file.length() != uploadBean.getDownloadLength()) {
//            uploadBean.setNetworkSpeed(0);
//            uploadBean.setState(DownloadManager.ERROR); //由于不明原因，文件保存有误
//            postMessage("未知原因", null);
//        }
        return uploadBean;
    }

    /**
     * 卡色图片
     */
    private void getColorRecurPic(int i) {
        cIndex = i;
        if (isCancelled()) {
            uploadBean.setUploadStatus(Const.UploadStatus.UPLOAD_FAILED);
            uploadBean.setProgress(0);
            uploadBean.setMessage("颜色图片上传取消-已取消");
            ToolUploadDbHelper.getInstance(context).updateMessage(uploadBean);
            postMessage(null, null);
            return;
        }
        if (!FunctionHelper.IsNetworkOnline(context)) {
            uploadBean.setUploadStatus(Const.UploadStatus.UPLOAD_FAILED);
            uploadBean.setProgress(0);
            uploadBean.setMessage("颜色图片上传取消-网络不可用");
            ToolUploadDbHelper.getInstance(context).updateMessage(uploadBean);
            postMessage(null, null);
            return;
        }
        if (!uploadBean.getColorPics().get(i).is_upload()) {
            try {
                String useid = SpManager.getUserId(context) + "";
                save = new WaterWarkSave(context, useid);
                colorPicName = System.currentTimeMillis() + ".jpg";
                colorPicPath = "/" + useid + "/item/" + colorPicName;
                date = DateUtls.rfc1123Format.format(new Date());
                expiration = System.currentTimeMillis() / 1000 + 1000 * 5 * 10;
                File temp = null;
                int size = save.getCompressMaxSize(WaterWarkSave.BUYTOOLCOMPRESSMAXSIZE + useid);
                if (size >= 150 && size <= 1000) {
                    ImageUtls.Max_Compress_Kb = size;
                }
                //无水印
                temp = ImageUtls.commPressNoWater(uploadBean.getColorPics().get(i).getUrl(), colorPicName);
                contentMd5 = md5Hex(temp);
                policy = UpYunNewUtls.makeNewPolicy(date, contentMd5, colorPicPath, expiration, UpYunConst.PIC_BUCKET, null);
                signature = UpYunNewUtls.getSignature(UpYunConst.PIC_BUCKET, policy, date, contentMd5);
                Map<String, Object> paramsMap = new HashMap<>();
                //上传空间
                paramsMap.put(Params.BUCKET, UpYunConst.PIC_BUCKET);
                //保存路径，任选其中一个
                paramsMap.put(SAVE_KEY, colorPicPath);
                paramsMap.put(Params.CONTENT_MD5, contentMd5);
                //可选参数（详情见api文档介绍）
                //  paramsMap.put(Params.RETURN_URL, "httpbin.org/post");

                // UploadEngine.getInstance().formUpload(temp, paramsMap, OPERATER, UpYunUtils.md5(UpYunConst.PASSWORD), completeListener, progressListener);
                UploadEngine.getInstance().setTag(uploadBean.getCreat_time()).formUpload(temp, policy, OPERATER, signature, cCompleteListener, cProgressListener);

            } catch (Exception e) {
                e.printStackTrace();
                uploadBean.setUploadStatus(Const.UploadStatus.UPLOAD_FAILED);
                uploadBean.setProgress(0);
                uploadBean.setMessage("颜色图片又拍云上传异常" + "-" + e.getMessage());
                //更改消息数据库
                ToolUploadDbHelper.getInstance(context).updateMessage(uploadBean);
                postMessage(null, null);
            }
        }
    }

    private void getRecurPic(int i) {
        index = i;
        if (isCancelled()) {
            uploadBean.setUploadStatus(Const.UploadStatus.UPLOAD_FAILED);
            uploadBean.setProgress(0);
            uploadBean.setMessage("图片上传取消-已取消");
            ToolUploadDbHelper.getInstance(context).updateMessage(uploadBean);
            postMessage(null, null);
            return;
        }
        if (!FunctionHelper.IsNetworkOnline(context)) {
            uploadBean.setUploadStatus(Const.UploadStatus.UPLOAD_FAILED);
            uploadBean.setProgress(0);
            uploadBean.setMessage("图片上传取消-网络不可用");
            ToolUploadDbHelper.getInstance(context).updateMessage(uploadBean);
            postMessage(null, null);
            return;
        }
        if (!uploadBean.getLocal_pics().get(i).is_upload()) {
            try {
                String useid = SpManager.getUserId(context) + "";
                save = new WaterWarkSave(context, useid);
                picName = System.currentTimeMillis() + ".jpg";
                picPath = "/" + useid + "/item/" + picName;
                date = DateUtls.rfc1123Format.format(new Date());
                expiration = System.currentTimeMillis() / 1000 + 1000 * 5 * 10;
                File temp = null;
                int size = save.getCompressMaxSize(WaterWarkSave.BUYTOOLCOMPRESSMAXSIZE + useid);
                if (size >= 150 && size <= 1000) {
                    ImageUtls.Max_Compress_Kb = size;
                }
                if (save.getHasWaterWark(WaterWarkSave.BUYTOOL_HASWATERWARK + useid)) {
                    //有设置水印
                    int no_index = save.getNoAddWaterCount(WaterWarkSave.BUYTOOLNOADDWATERWARK_COUNT + useid);
                    if (no_index > 0) {
                        //设置第几个无水印
                        if (no_index - 1 >= i) {
                            temp = ImageUtls.commPressNoWater(uploadBean.getLocal_pics().get(i).getPath(), picName);
                            // temp = new File(uploadBean.getLocal_pics().get(i).getPath());
                        } else {
                            temp = setWaterMark(i, useid);
                        }
                    } else {
                        temp = setWaterMark(i, useid);
                    }

                } else {
                    //无水印
                    temp = ImageUtls.commPressNoWater(uploadBean.getLocal_pics().get(i).getPath(), picName);
                    //temp = new File(uploadBean.getLocal_pics().get(i).getPath());
                }
                contentMd5 = md5Hex(temp);
                policy = UpYunNewUtls.makeNewPolicy(date, contentMd5, picPath, expiration, UpYunConst.PIC_BUCKET, null);
                signature = UpYunNewUtls.getSignature(UpYunConst.PIC_BUCKET, policy, date, contentMd5);
                Map<String, Object> paramsMap = new HashMap<>();
                //上传空间
                paramsMap.put(Params.BUCKET, UpYunConst.PIC_BUCKET);
                //保存路径，任选其中一个
                paramsMap.put(SAVE_KEY, picPath);
                paramsMap.put(Params.CONTENT_MD5, contentMd5);
                //可选参数（详情见api文档介绍）
                //  paramsMap.put(Params.RETURN_URL, "httpbin.org/post");

                // UploadEngine.getInstance().formUpload(temp, paramsMap, OPERATER, UpYunUtils.md5(UpYunConst.PASSWORD), completeListener, progressListener);
                UploadEngine.getInstance().setTag(uploadBean.getCreat_time()).formUpload(temp, policy, OPERATER, signature, completeListener, progressListener);

            } catch (Exception e) {
                e.printStackTrace();
                uploadBean.setUploadStatus(Const.UploadStatus.UPLOAD_FAILED);
                uploadBean.setProgress(0);
                uploadBean.setMessage("图片又拍云上传异常" + "-" + e.getMessage());
                //更改消息数据库
                ToolUploadDbHelper.getInstance(context).updateMessage(uploadBean);
                postMessage(null, null);
            }
        }
    }

    private File setWaterMark(int i, String useid) throws Exception {
        File temp;
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        options.inPurgeable = true;// 允许可清除
//        options.inInputShareable = true;// 以上options的两个属性必须联合使用才会有效果
        //Bitmap bitmap2 = BitmapFactory.decodeFile(uploadBean.getLocal_pics().get(i).getPath(),options);
        Bitmap bitmap = ImageUtls.getMyBitmap(uploadBean.getLocal_pics().get(i).getPath());
        if (save.getWaterType(WaterWarkSave.BUYTOOLWATERWARK_TYPE + useid) == MeSettingActivity.WATER_TYPE_ONE) {
            Bitmap sBitmap = ImageUtls.getBitmap(MeSettingActivity.water_mark_path_one);
            temp = ImageUtls.createWaterMaskCenter(bitmap, sBitmap, picName);
            if (bitmap != null)
                bitmap.recycle();
            if (sBitmap != null)
                sBitmap.recycle();
//            temp = ImageUtls.saveFile(wBitmap, ImageUtls.BUYTOOL_ROOT_DIRECTORY, picName);
//            if (wBitmap != null)
//                wBitmap.recycle();
        } else if (save.getWaterType(WaterWarkSave.BUYTOOLWATERWARK_TYPE + useid) == MeSettingActivity.WATER_TYPE_TWO) {
            Bitmap sBitmap = getBitmap(MeSettingActivity.water_mark_path_two);
            temp = ImageUtls.createWaterMaskCenter(bitmap, sBitmap, picName);
            if (bitmap != null)
                bitmap.recycle();
            if (sBitmap != null)
                sBitmap.recycle();
//            temp = ImageUtls.saveFile(wBitmap, ImageUtls.BUYTOOL_ROOT_DIRECTORY, picName);
//            if (wBitmap != null)
//                wBitmap.recycle();
        } else {
            //无水印
            temp = ImageUtls.commPressNoWater(uploadBean.getLocal_pics().get(i).getPath(), picName);
//            temp=ImageUtls.saveFile(bitmap, ImageUtls.BUYTOOL_ROOT_DIRECTORY, picName);
//            if (bitmap != null)
//                bitmap.recycle();
            //temp = new File(uploadBean.getLocal_pics().get(i).getPath());
        }
        return temp;
    }

    /**
     * 上传给后台
     *
     * @author James Chen
     * @create time in 2017/8/8 11:00
     */
    private void uploadData() {
        if (uploadBean.getType() == UploadItemActivity.UPLOAD_TYPE) {
            //新款
            try {
                if (isCancelled()) {
                    uploadBean.setUploadStatus(Const.UploadStatus.UPLOAD_FAILED);
                    uploadBean.setProgress(0);
                    uploadBean.setMessage("上传新款取消-已取消");
                    ToolUploadDbHelper.getInstance(context).updateMessage(uploadBean);
                    postMessage(null, null);
                    return;
                }
                if (!FunctionHelper.IsNetworkOnline(context)) {
                    uploadBean.setUploadStatus(Const.UploadStatus.UPLOAD_FAILED);
                    uploadBean.setProgress(0);
                    uploadBean.setMessage("上传新款取消-网络不可用");
                    ToolUploadDbHelper.getInstance(context).updateMessage(uploadBean);
                    postMessage(null, null);
                    return;
                }
                int AgentItemID = UploadItemAPI.getInstance().uploadNewItem(uploadBean, PublicData.getCookie(context));
                uploadBean.setUploadStatus(Const.UploadStatus.UPLOAD_FINISH);
                uploadBean.setItemID(AgentItemID);
                postMessage(null, null);
            } catch (Exception e) {
                e.printStackTrace();
                uploadBean.setUploadStatus(Const.UploadStatus.UPLOAD_FAILED);
                uploadBean.setProgress(0);
                uploadBean.setMessage("上传新款接口异常" + "-" + e.getMessage());
                //更改消息数据库
                ToolUploadDbHelper.getInstance(context).updateMessage(uploadBean);
                postMessage(null, null);
            }

        } else {
            //编辑
            try {
                if (isCancelled()) {
                    uploadBean.setUploadStatus(Const.UploadStatus.UPLOAD_FAILED);
                    uploadBean.setProgress(0);
                    uploadBean.setMessage("改款上传取消-已取消");
                    ToolUploadDbHelper.getInstance(context).updateMessage(uploadBean);
                    postMessage(null, null);
                    return;
                }
                if (!FunctionHelper.IsNetworkOnline(context)) {
                    uploadBean.setUploadStatus(Const.UploadStatus.UPLOAD_FAILED);
                    uploadBean.setProgress(0);
                    uploadBean.setMessage("改款上传取消-网络不可用");
                    ToolUploadDbHelper.getInstance(context).updateMessage(uploadBean);
                    postMessage(null, null);
                    return;
                }
                UploadItemAPI.getInstance().uploadEditItem(uploadBean, PublicData.getCookie(context));
                uploadBean.setUploadStatus(Const.UploadStatus.UPLOAD_FINISH);
                postMessage(null, null);
            } catch (Exception e) {
                e.printStackTrace();
                uploadBean.setUploadStatus(Const.UploadStatus.UPLOAD_FAILED);
                uploadBean.setProgress(0);
                uploadBean.setMessage("改款上传接口异常" + "-" + e.getMessage());
                //更改消息数据库
                ToolUploadDbHelper.getInstance(context).updateMessage(uploadBean);
                postMessage(null, null);
            }
        }
    }

    //视频进度回调，可为空
    UpProgressListener progressListener1 = new UpProgressListener() {
        @Override
        public void onRequestProgress(final long bytesWrite, final long contentLength) {
            if (isCancelled()) {
                UploadEngine.getInstance().cancleAll(uploadBean.getCreat_time());
                return;
            }
            uploadBean.setProgress((int) ((100 * bytesWrite) / contentLength));
            if (video_prelongTim == 0) {
                video_prelongTim = (new Date()).getTime();
                postMessage(null, null);
            } else {
                long curTime = (new Date()).getTime();//本地单击的时间
                Log.e("UploadTask", "视频上传间隔时间：" + (curTime - video_prelongTim));//计算本地和上次的时间差
                if (curTime - video_prelongTim >= Const.UPLOAD_VIEDEO_INTERVAL) {
                    postMessage(null, null);
                    //当前单击事件变为上次时间
                    video_prelongTim = curTime;
                }
            }
        }
    };
    //视频结束回调
    UpCompleteListener completeListener1 = new UpCompleteListener() {
        @Override
        public void onComplete(boolean isSuccess, String result) {
            video_prelongTim = 0;
            if (isCancelled()) {
                UploadEngine.getInstance().cancleAll(uploadBean.getCreat_time());
//                uploadBean.setUploadStatus(Const.UploadStatus.UPLOAD_FAILED);
//                uploadBean.setProgress(0);
//                uploadBean.setMessage("图片上传取消-已取消");
//                ToolUploadDbHelper.getInstance(context).updateMessage(uploadBean);
//                postMessage(null, null);
                return;
            }
            if (isSuccess) {
                uploadBean.getLocal_videos().get(vIndex).setIs_upload(true);
                String serverPath = ImageUrlExtends.HTTP_NAHUO_VIDEO_SERVER +"/" + videoPath;
                uploadBean.getLocal_videos().get(vIndex).setPath(serverPath);
                //更改视频数据库
                ToolUploadDbHelper.getInstance(context).updateVideoList(uploadBean);
                postMessage(null, null);
                if (!hasVideos(uploadBean.getLocal_videos())) {
                    //视频传结束调用后台接口
                    uploadData();
                } else {
                    vIndex = getNoUploadIndex(uploadBean.getLocal_videos());
                    getRecurVideo(vIndex);
                }
            } else {
                uploadBean.setUploadStatus(Const.UploadStatus.UPLOAD_FAILED);
                uploadBean.setProgress(0);
                uploadBean.setMessage("视频上传又拍云失败" + "-" + "第" + (vIndex + 1) + "个视频上传失败");
                //更改消息数据库
                ToolUploadDbHelper.getInstance(context).updateMessage(uploadBean);
                postMessage(null, null);
            }
        }
    };

    //又拍云视频上传
    private void uploadYpYunVideos() {
        vIndex = getNoUploadIndex(uploadBean.getLocal_videos());
        getRecurVideo(vIndex);
    }

    private void getRecurVideo(int j) {
        vIndex = j;
        if (isCancelled()) {
            uploadBean.setUploadStatus(Const.UploadStatus.UPLOAD_FAILED);
            uploadBean.setProgress(0);
            uploadBean.setMessage("视频上传取消-已取消");
            ToolUploadDbHelper.getInstance(context).updateMessage(uploadBean);
            postMessage(null, null);
            return;
        }
        if (!FunctionHelper.IsNetworkOnline(context)) {
            uploadBean.setUploadStatus(Const.UploadStatus.UPLOAD_FAILED);
            uploadBean.setProgress(0);
            uploadBean.setMessage("视频上传取消-网络不可用");
            ToolUploadDbHelper.getInstance(context).updateMessage(uploadBean);
            postMessage(null, null);
            return;
        }
        if (!uploadBean.getLocal_videos().get(j).is_upload()) {
            try {
                String date = "", contentMd5 = "", policy = "", signature = "";
                long expiration;
                videoName = System.currentTimeMillis() + ".mp4";
                videoPath = "/" + getUserId(context) + "/item/" + videoName;
                date = DateUtls.rfc1123Format.format(new Date());
                expiration = System.currentTimeMillis() / 1000 + 1000 * 5 * 10;
                File temp = null;
                temp = new File(uploadBean.getLocal_videos().get(j).getPath());
                contentMd5 = md5Hex(temp);
                policy = UpYunNewUtls.makeNewPolicy(date, contentMd5, videoPath, expiration, UpYunConst.VIDEO_BUCKET, null);
                signature = UpYunNewUtls.getSignature(UpYunConst.VIDEO_BUCKET, policy, date, contentMd5);
                Map<String, Object> paramsMap = new HashMap<>();
                //上传空间
                paramsMap.put(Params.BUCKET, UpYunConst.VIDEO_BUCKET);
                //保存路径，任选其中一个
                paramsMap.put(SAVE_KEY, videoPath);
                paramsMap.put(Params.CONTENT_MD5, contentMd5);
                //可选参数（详情见api文档介绍）
                //  paramsMap.put(Params.RETURN_URL, "httpbin.org/post");
                UploadEngine.getInstance().setTag(uploadBean.getCreat_time()).formUpload(temp, policy, OPERATER, signature, completeListener1, progressListener1);

            } catch (Exception e) {
                e.printStackTrace();
                uploadBean.setUploadStatus(Const.UploadStatus.UPLOAD_FAILED);
                uploadBean.setProgress(0);
                uploadBean.setMessage("视频上传又拍云异常" + "-" + e.getMessage());
                //更改消息数据库
                ToolUploadDbHelper.getInstance(context).updateMessage(uploadBean);
                postMessage(null, null);
            }

        }
    }

    private void postMessage(String errorMsg, Exception e) {
        //DownloadDBManager.INSTANCE.update(uploadBean); //发消息前首先更新数据库
        //ToolUploadDbHelper.instance
        UploadUIHandler.MessageBean messageBean = new UploadUIHandler.MessageBean();
        messageBean.uploadBean = uploadBean;
        messageBean.errorMsg = errorMsg;
        messageBean.e = e;
        Message msg = mUploadUIHandler.obtainMessage();
        msg.obj = messageBean;
        mUploadUIHandler.sendMessage(msg);
    }

    /**
     * 执行文件下载
     */
    private int download(InputStream input, RandomAccessFile out) throws IOException {
        if (input == null || out == null) return -1;

        byte[] buffer = new byte[BUFFER_SIZE];
        BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
        int downloadSize = 0;
        int len;
        try {
            while ((len = in.read(buffer, 0, BUFFER_SIZE)) != -1 && !isCancelled()) {
                out.write(buffer, 0, len);
                downloadSize += len;
            }
        } finally {
            try {
                out.close();
                in.close();
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return downloadSize;
    }

    /**
     * 文件读写
     */
    private final class ProgressRandomAccessFile extends RandomAccessFile {
        private long lastDownloadLength = 0; //总共已下载的大小
        private long curDownloadLength = 0;  //当前已下载的大小（可能分几次下载）
        private long lastRefreshUiTime;

        public ProgressRandomAccessFile(File file, String mode, long lastDownloadLength) throws FileNotFoundException {
            super(file, mode);
            this.lastDownloadLength = lastDownloadLength;
            this.lastRefreshUiTime = System.currentTimeMillis();
        }

        @Override
        public void write(byte[] buffer, int offset, int count) throws IOException {
            super.write(buffer, offset, count);

            //已下载大小
//            long downloadLength = lastDownloadLength + count;
//            curDownloadLength += count;
//            lastDownloadLength = downloadLength;
//            uploadBean.setDownloadLength(downloadLength);
//
//            //计算下载速度
//            long totalTime = (System.currentTimeMillis() - mPreviousTime) / 1000;
//            if (totalTime == 0) {
//                totalTime += 1;
//            }
//            long networkSpeed = curDownloadLength / totalTime;
//            uploadBean.setNetworkSpeed(networkSpeed);
//
//            //下载进度
//            float progress = downloadLength * 1.0f / uploadBean.getTotalLength();
//            uploadBean.setProgress(progress);
//            long curTime = System.currentTimeMillis();
//
//            //每200毫秒刷新一次数据
//            if (curTime - lastRefreshUiTime >= OkGo.REFRESH_TIME || progress == 1.0f) {
//                postMessage(null, null);
//                lastRefreshUiTime = System.currentTimeMillis();
//            }
        }
    }
}
