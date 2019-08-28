package com.nahuo.buyertool.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.nahuo.buyertool.BWApplication;

import java.util.List;

import cn.jpush.android.service.DownloadService;


public class UploadItemService extends Service {
    public UploadItemService() {
    }
    public static UploadManager DOWNLOAD_MANAGER;

    /** start 方式开启服务，保存全局的下载管理对象 */
    public static UploadManager getDownloadManager() {
        Context context = BWApplication.getInstance();
        if (!UploadItemService.isServiceRunning(context))
            context.startService(new Intent(context, UploadItemService.class));
        if (UploadItemService.DOWNLOAD_MANAGER == null)
            UploadItemService.DOWNLOAD_MANAGER = UploadManager.getInstance(context);
        return DOWNLOAD_MANAGER;
    }
//public static void clear(){
//        Context context = BWApplication.getInstance();
//        context.stopService(new Intent(context, DownloadService.class));
//        UploadItemService.DOWNLOAD_MANAGER.clear();
//        UploadItemService.DOWNLOAD_MANAGER= null;
//}

    public static boolean isServiceRunning(Context context) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (serviceList == null || serviceList.size() == 0) return false;
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(DownloadService.class.getName())) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
