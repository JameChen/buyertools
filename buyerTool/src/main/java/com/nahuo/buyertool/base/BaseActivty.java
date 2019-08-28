package com.nahuo.buyertool.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.baidu.mobstat.StatService;
import com.nahuo.buyertool.BWApplication;
import com.nahuo.buyertool.MainAcivity;
import com.nahuo.live.xiaozhibo.common.utils.TCConstants;
import com.nahuo.live.xiaozhibo.mainui.LiveListActivity;
import com.nahuo.live.xiaozhibo.play.TCLivePlayerActivity;
import com.nahuo.live.xiaozhibo.play.TCVodPlayerActivity;
import com.nahuo.live.xiaozhibo.push.camera.TCLivePublisherActivity;

import cn.jpush.android.api.JPushInterface;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by jame on 2017/3/31.
 */

public class BaseActivty extends Activity {
    protected CompositeDisposable mCompositeDisposable;

    protected void unSubscribe() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }

    protected void addSubscribe(Disposable subscription) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(subscription);
    }
    //被踢下线广播监听
    private LocalBroadcastManager mLocalBroadcatManager;
    private BroadcastReceiver mExitBroadcastReceiver;
    protected Activity currentActivity=this;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentActivity=this;
        BWApplication.addActivity(this);
        if (!this.getClass().getSimpleName().equals(MainAcivity.class.getSimpleName())
                & !this.getClass().getSimpleName().equals(LiveListActivity.class.getSimpleName())
                & !this.getClass().getSimpleName().equals(TCVodPlayerActivity.class.getSimpleName())
                &  !this.getClass().getSimpleName().equals(TCLivePublisherActivity.class.getSimpleName()
        ) & !this.getClass().getSimpleName().equals(TCLivePlayerActivity.class.getSimpleName())) {
            BWApplication.addVActivity(this);
        }
        BWApplication.setCurrentActivity(this);
        mLocalBroadcatManager = LocalBroadcastManager.getInstance(this);
        mExitBroadcastReceiver = new ExitBroadcastRecevier(currentActivity);
        mLocalBroadcatManager.registerReceiver(mExitBroadcastReceiver, new IntentFilter(TCConstants.EXIT_APP));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unSubscribe();
        if (mLocalBroadcatManager!=null) {
            if (mExitBroadcastReceiver!=null)
                mLocalBroadcatManager.unregisterReceiver(mExitBroadcastReceiver);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
        JPushInterface.onPause(this);
        // UMengTestUtls.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentActivity=this;
        StatService.onResume(this);
        JPushInterface.onResume(this);
        // UMengTestUtls.onResume(this);
    }
}
