package com.nahuo.buyertool.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.nahuo.live.xiaozhibo.common.utils.TCConstants;
import com.nahuo.live.xiaozhibo.common.utils.TCUtils;

/**
 * Created by jame on 2019/5/30.
 */
public class  ExitBroadcastRecevier extends BroadcastReceiver {
    Activity currentActivity;
    public ExitBroadcastRecevier(Activity currentActivity) {
        this.currentActivity=currentActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(TCConstants.EXIT_APP)) {
            if (currentActivity!=null&&!currentActivity.isFinishing())
            TCUtils.showKickOutDialog(currentActivity);
        }
    }
}