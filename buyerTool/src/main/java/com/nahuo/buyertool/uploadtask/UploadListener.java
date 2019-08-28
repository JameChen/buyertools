package com.nahuo.buyertool.uploadtask;

import com.nahuo.buyertool.Bean.UploadBean;

public abstract class UploadListener  {

    private Object userTag;

    /** 下载进行时回调 */
    public abstract void onProgress(UploadBean uploadBean);

    /** 下载完成时回调 */
    public abstract void onFinish(UploadBean uploadBean);

    /** 下载出错时回调 */
    public abstract void onError(UploadBean uploadBean, String errorMsg, Exception e);

    /** 成功添加任务的回调 */
    public void onAdd(UploadBean uploadBean) {
    }

    /** 成功移除任务回调 */
    public void onRemove(UploadBean uploadBean) {
    }

    /** 类似View的Tag功能，主要用在listView更新数据的时候，防止数据错乱 */
    public Object getUserTag() {
        return userTag;
    }

    /** 类似View的Tag功能，主要用在listView更新数据的时候，防止数据错乱 */
    public void setUserTag(Object userTag) {
        this.userTag = userTag;
    }

    /** 默认的空实现 */
    public static final UploadListener DEFAULT_DOWNLOAD_LISTENER = new UploadListener() {
        @Override
        public void onProgress(UploadBean uploadBean) {
        }

        @Override
        public void onFinish(UploadBean uploadBean) {
        }

        @Override
        public void onError(UploadBean uploadBean, String errorMsg, Exception e) {
        }
    };
}
