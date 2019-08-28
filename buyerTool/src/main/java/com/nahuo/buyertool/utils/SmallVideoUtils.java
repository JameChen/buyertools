package com.nahuo.buyertool.utils;

import android.os.Environment;

import com.mabeijianxi.smallvideorecord2.DeviceUtils;
import com.mabeijianxi.smallvideorecord2.JianXiCamera;
import com.nahuo.buyertool.common.Const;

import java.io.File;

/**
 * Created by jame on 2018/10/17.
 */

public class SmallVideoUtils {
    public static void initSmallVideo() {
        // 设置拍摄视频缓存路径
        File dcim = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (DeviceUtils.isZte()) {
            if (dcim.exists()) {
                JianXiCamera.setVideoCachePath(dcim + Const.VIDEOS_CASH_PATH);
            } else {
                JianXiCamera.setVideoCachePath(dcim.getPath().replace("/sdcard/",
                        "/sdcard-ext/")
                        + Const.VIDEOS_CASH_PATH);
            }
        } else {
            JianXiCamera.setVideoCachePath(dcim + Const.VIDEOS_CASH_PATH);
        }
        // 初始化拍摄
        JianXiCamera.initialize(false, null);
    }
}
