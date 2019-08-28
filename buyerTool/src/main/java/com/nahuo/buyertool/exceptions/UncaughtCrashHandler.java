package com.nahuo.buyertool.exceptions;

import java.lang.Thread.UncaughtExceptionHandler;

import com.nahuo.buyertool.common.FileUtils;

/**
 * @description 未捕获异常处理
 * @created 2014-12-23 上午11:20:06
 * @author ZZB
 */
public class UncaughtCrashHandler implements UncaughtExceptionHandler {

    public UncaughtCrashHandler() {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        FileUtils.writeErrorToFile((Exception) ex);
        System.exit(0);
    }

}
