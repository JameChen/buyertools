package com.nahuo.buyertool.uploadtask;

import android.os.Handler;
import android.os.Message;

import com.nahuo.buyertool.BWApplication;
import com.nahuo.buyertool.Bean.UploadBean;
import com.nahuo.buyertool.ViewHub;
import com.nahuo.buyertool.activity.UploadItemActivity;
import com.nahuo.buyertool.common.Const;
import com.nahuo.buyertool.eventbus.BusEvent;
import com.nahuo.buyertool.eventbus.EventBusId;
import com.nahuo.buyertool.service.UploadManager;

import de.greenrobot.event.EventBus;

/**
 * ================================================
 * 描    述：用于在主线程回调下载UI
 * ================================================
 */
public class UploadUIHandler extends Handler {

    private UploadListener mGlobalUploadListener;

    @Override
    public void handleMessage(Message msg) {
        MessageBean messageBean = (MessageBean) msg.obj;
        if (messageBean != null) {
            UploadBean info = messageBean.uploadBean;
            String errorMsg = messageBean.errorMsg;
            Exception e = messageBean.e;
            if (mGlobalUploadListener != null) {
                executeListener(mGlobalUploadListener, info, errorMsg, e);
            }
            //  Log.e("uyu",info.getProgress()+"--");
            UploadListener listener = info.getListener();
            if (listener != null)
                executeListener(listener, info, errorMsg, e);
            if (listener == null) {
                if (info.getUploadStatus().equals(Const.UploadStatus.UPLOAD_FINISH)) {
                    if (info.getType() == UploadItemActivity.UPLOAD_TYPE) {
                        ViewHub.showShortToast(BWApplication.getInstance(), info.getName() + "  上传成功");
                    } else {
                        ViewHub.showShortToast(BWApplication.getInstance(), info.getName() + "  改款成功");
                    }
                    EventBus.getDefault().postSticky(BusEvent.getEvent(EventBusId.REFERSH_MYSTYLE_FINISH,info));
                    UploadManager.getInstance(BWApplication.getInstance()).removeTask(info.getCreat_time());
                }
            }
            // Log.e("uyu",info.getProgress()+"");
        } else {
            //OkLogger.e("UploadUIHandler UploadSubBean null");
        }
    }

    private void executeListener(UploadListener listener, UploadBean info, String errorMsg, Exception e) {
        String state = info.getUploadStatus();
        switch (state) {
            case Const.UploadStatus.UPLOAD_WAIT:
            case Const.UploadStatus.UPLOADING:
                listener.onProgress(info);
                break;
            case Const.UploadStatus.UPLOAD_FINISH:
                listener.onProgress(info);   //结束前再次回调进度，避免最后一点数据没有刷新
                listener.onFinish(info);
                break;
            case Const.UploadStatus.UPLOAD_FAILED:

                listener.onProgress(info);   //结束前再次回调进度，避免最后一点数据没有刷新
                listener.onError(info, errorMsg, e);
                break;
        }
    }

    public void setGlobalUploadListener(UploadListener UploadListener) {
        this.mGlobalUploadListener = UploadListener;
    }

    public static class MessageBean {
        public UploadBean uploadBean;
        public String errorMsg;
        public Exception e;
    }
}
