package com.nahuo.buyertool.service;

import android.content.Context;

import com.nahuo.buyertool.Bean.UploadBean;
import com.nahuo.buyertool.common.Const;
import com.nahuo.buyertool.common.SpManager;
import com.nahuo.buyertool.db.ToolUploadDbHelper;
import com.nahuo.buyertool.uploadtask.UploadListener;
import com.nahuo.buyertool.uploadtask.UploadTask;
import com.nahuo.buyertool.uploadtask.UploadThreadPool;
import com.nahuo.buyertool.uploadtask.UploadUIHandler;
import com.upyun.library.common.UploadEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by jame on 2017/8/3.
 */

public class UploadManager {
    private static UploadManager mInstance;       //使用单例模式
    private UploadThreadPool threadPool;          //下载的线程池
    private List<UploadBean> mUploadInfoList;
    private UploadUIHandler uploadUIHandler;

    public UploadThreadPool getThreadPool() {
        return threadPool;
    }

    public UploadUIHandler getHandler() {
        return uploadUIHandler;
    }

    public List<UploadBean> getAllTask() {
        return mUploadInfoList;
    }
    public void setList( List<UploadBean> mUploadInfoList ){
        this.mUploadInfoList=mUploadInfoList;
    }
    Context context;

    public static UploadManager getInstance(Context context) {
        if (null == mInstance) {
            synchronized (UploadManager.class) {
                if (null == mInstance) {
                    mInstance = new UploadManager(context);
                }
            }
        }
        return mInstance;
    }
//    public    void  clear(){
//        if (mInstance!=null)
//            mInstance=null;
//    }
    UploadManager(Context context) {
        this.context = context;
        mUploadInfoList = Collections.synchronizedList(new ArrayList<UploadBean>());
        threadPool = new UploadThreadPool();
        uploadUIHandler = new UploadUIHandler();
        mUploadInfoList = ToolUploadDbHelper.getInstance(context).getAllUploadItems(SpManager.getUserId(context));

    }

    /**
     * 添加一个下载任务,依据taskTag标识是否属于同一个任务
     */
    public void addTask(String taskTag, UploadBean data, UploadListener listener) {
        addTask(data, taskTag, listener, false);
    }

    /**
     * 添加一个下载任务
     *
     * @param listener  下载监听
     * @param isRestart 是否重新开始下载
     */
    private void addTask(UploadBean bean, String taskTag, UploadListener listener, boolean isRestart) {
        UploadBean downloadInfo = getDownloadInfo(taskTag);
        if (downloadInfo == null) {
            downloadInfo = bean;
            downloadInfo.setUploadStatus(Const.UploadStatus.UPLOAD_WAIT);
            mUploadInfoList.add(0, downloadInfo);
        }
        //无状态，暂停，错误才允许开始下载
        if (downloadInfo.getUploadStatus().equals(Const.UploadStatus.UPLOAD_WAIT) || downloadInfo.getUploadStatus().equals(Const.UploadStatus.UPLOAD_FAILED)) {
            //构造即开始执行
            UploadTask downloadTask = new UploadTask(downloadInfo, isRestart, listener, context);
            downloadInfo.setTask(downloadTask);
        } else if (downloadInfo.getUploadStatus().equals(Const.UploadStatus.UPLOAD_FINISH)) {
            if (listener != null) listener.onFinish(downloadInfo);
        }
    }

    /**
     * 获取一个任务
     */
    public UploadBean getDownloadInfo(String taskKey) {
        for (UploadBean downloadInfo : mUploadInfoList) {
            if (taskKey.equals(downloadInfo.getCreat_time())) {
                return downloadInfo;
            }
        }
        return null;
    }

    /**
     * 停止全部任务,先停止没有下载的，再停止下载中的
     */
    public void stopAllTask() {
        for (UploadBean info : mUploadInfoList) {
            if (!info.getUploadStatus().equals(Const.UploadStatus.UPLOADING))
                stopTask(info.getCreat_time());
        }
        for (UploadBean info : mUploadInfoList) {
            if (info.getUploadStatus().equals(Const.UploadStatus.UPLOADING))
                stopTask(info.getCreat_time());
        }
    }

    /**
     * 停止
     */
    public void stopTask(final String taskKey) {
        UploadBean downloadInfo = getDownloadInfo(taskKey);
        if (downloadInfo == null) return;
        //无状态和完成状态，不允许停止
        if (!downloadInfo.getUploadStatus().equals(Const.UploadStatus.UPLOAD_FINISH) && downloadInfo.getTask() != null) {
            downloadInfo.getTask().stop();
            UploadEngine.getInstance().cancleAll(downloadInfo.getCreat_time()).setIsCancel(true);
        }
    }

    /**
     * 删除一个任务,会删除下载文件
     */
    public void removeTask(String taskKey) {
        removeTask(taskKey, false);
    }

    /**
     * 删除一个任务,会删除下载文件
     */
    public void removeTask(String taskKey, boolean isDeleteFile) {
        final UploadBean downloadInfo = getDownloadInfo(taskKey);
        if (downloadInfo == null) return;
        //pauseTask(taskKey);                         //暂停任务
        removeTaskByKey(taskKey);                   //移除任务
        ToolUploadDbHelper.getInstance(context).removeUploadItem(downloadInfo);
        UploadEngine.getInstance().cancleAll(downloadInfo.getCreat_time()).setIsCancel(true);
        // if (isDeleteFile) deleteFile(downloadInfo.getTargetPath());   //删除文件
        // DownloadDBManager.INSTANCE.delete(taskKey);            //清除数据库
    }

    /**
     * 移除一个任务
     */
    private void removeTaskByKey(String taskKey) {
        ListIterator<UploadBean> iterator = mUploadInfoList.listIterator();
        while (iterator.hasNext()) {
            UploadBean info = iterator.next();
            if (taskKey.equals(info.getCreat_time())) {
                UploadListener listener = info.getListener();
                if (listener != null) listener.onRemove(info);
                info.removeListener();     //清除回调监听
                iterator.remove();         //清除任务
                break;
            }
        }
    }

    /**
     * 重新下载
     */
    public void restartTask(final String taskKey) {
        // final UploadBean downloadInfo = getDownloadInfo(taskKey);
//        if (downloadInfo != null ) {
//            //如果正在下载中，先暂停，等任务结束后再添加到队列开始下载
//          //  pauseTask(taskKey);
//            threadPool.getExecutor().addOnTaskEndListener(new ExecutorWithListener.OnTaskEndListener() {
//                @Override
//                public void onTaskEnd(Runnable r) {
//                    if (r == downloadInfo.getTask().getRunnable()) {
//                        //因为该监听是全局监听，每次任务被移除都会回调，所以以下方法执行一次后，必须移除，否者会反复调用
//                        threadPool.getExecutor().removeOnTaskEndListener(this);
//                        //此时监听给空，表示会使用之前的监听，true表示重新下载，会删除临时文件
//                        addTask(downloadInfo.getFileName(), downloadInfo.getTaskKey(), downloadInfo.getData(), downloadInfo.getRequest(), downloadInfo.getListener(), true);
//                    }
//                }
//            });
//        } else {

       // pauseTask(taskKey);
        restartTaskByKey(taskKey);
        // }
    }

    /**
     * 暂停
     */
    public void pauseTask(String taskKey) {
        UploadBean downloadInfo = getDownloadInfo(taskKey);
        if (downloadInfo == null) return;
        String state = downloadInfo.getUploadStatus();
        //等待和下载中才允许暂停
        if (downloadInfo.getTask() != null) {
            downloadInfo.getTask().pause();
        }
    }

    /**
     * 重新开始下载任务
     */
    private void restartTaskByKey(String taskKey) {
        UploadBean downloadInfo = getDownloadInfo(taskKey);
        if (downloadInfo == null)
            return;
        if (!downloadInfo.getUploadStatus().equals(Const.UploadStatus.UPLOADING)) {
            // addTask(downloadInfo, taskKey, downloadInfo.getListener(), true);
            UploadTask downloadTask = new UploadTask(downloadInfo, true, downloadInfo.getListener(), context);
           // downloadTask.reStart();
            downloadInfo.setTask(downloadTask);
        }
    }
}
