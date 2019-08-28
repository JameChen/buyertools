package com.nahuo.buyertool.utils;

import java.io.File;

/**
 * Created by jame on 2018/3/23.
 */

public class FileUtils {
    public static boolean isPassLegth(String path) {
        boolean flag = false;
        File f = new File(path);
        if (f.exists() && f.isFile()) {
            if (f.length() >= 1024*1024 * 5)
                flag = true;
            else
                flag = false;
        }
        return flag;
    }
}
