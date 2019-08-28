package com.nahuo.buyertool.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.baidu.mobstat.StatService;
import com.nahuo.buyertool.BWApplication;
import com.nahuo.buyertool.MainAcivity;
import com.nahuo.buyertool.StartActivity;
import com.nahuo.live.xiaozhibo.common.utils.TCConstants;
import com.nahuo.live.xiaozhibo.mainui.LiveListActivity;
import com.nahuo.live.xiaozhibo.play.TCLivePlayerActivity;
import com.nahuo.live.xiaozhibo.play.TCVodPlayerActivity;
import com.nahuo.live.xiaozhibo.push.camera.TCLivePublisherActivity;

import cn.jpush.android.api.JPushInterface;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by jame on 2018/3/28.
 */

public class BaseAppCompatActivity extends AppCompatActivity {
    protected CompositeDisposable mCompositeDisposable;
    //protected EventBus mEventBus = EventBus.getDefault();
    protected Activity currentActivity;
    protected void unSubscribe() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }

    public void addSubscribe(Disposable subscription) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(subscription);
    }


    //被踢下线广播监听
    private LocalBroadcastManager mLocalBroadcatManager;
    private BroadcastReceiver mExitBroadcastReceiver;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentActivity=this;
//        if (!this.getClass().getSimpleName().equals(MainAcivity.class.getSimpleName())) {
//            BWApplication.addActivity(this);
//        }
//        if (mEventBus != null) {
//            if (!mEventBus.isRegistered(this))
//                mEventBus.register(this);
//        } else {
//            mEventBus = EventBus.getDefault();
//            if (!mEventBus.isRegistered(this))
//                mEventBus.register(this);
//        }
        BWApplication.addActivity(this);
        if (!this.getClass().getSimpleName().equals(MainAcivity.class.getSimpleName())
                & !this.getClass().getSimpleName().equals(LiveListActivity.class.getSimpleName())
                &  !this.getClass().getSimpleName().equals(TCVodPlayerActivity.class.getSimpleName())
                &  !this.getClass().getSimpleName().equals(TCLivePublisherActivity.class.getSimpleName()
        )&  !this.getClass().getSimpleName().equals(TCLivePlayerActivity.class.getSimpleName())) {
            BWApplication.addVActivity(this);
        }
        if (this.getClass().getSimpleName().equals(MainAcivity.class.getSimpleName())
                || this.getClass().getSimpleName().equals(StartActivity.class.getSimpleName())
                ) {
            transparencyBar(this);
        }
        BWApplication.setCurrentActivity(this);
        mLocalBroadcatManager = LocalBroadcastManager.getInstance(this);
        mExitBroadcastReceiver = new ExitBroadcastRecevier(currentActivity);
        mLocalBroadcatManager.registerReceiver(mExitBroadcastReceiver, new IntentFilter(TCConstants.EXIT_APP));
    }


    public void transparencyBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
            setSystemBarTint(activity);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setSystemBarTint(activity);
        }
    }

    public void setSystemBarTint(Activity activity) {

//        SystemBarTintManager tintManager = new SystemBarTintManager(this);
//// 激活状态栏设置
//        tintManager.setStatusBarTintEnabled(true);
//// 激活导航栏设置
//        tintManager.setNavigationBarTintEnabled(true);
//// 设置一个颜色给系统栏
//        tintManager.setTintColor(ContextCompat.getColor(activity,R.color.my_colorPrimary));
    }

    public void setBarStyle() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            ToolbarUtil.createStatusBarView(this,R.color.transparent,1);
//            //透明导航栏
//          //  getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            // 创建状态栏的管理实例
//            SystemBarTintManager tintManager = new SystemBarTintManager(this);
//// 激活状态栏设置
//            tintManager.setStatusBarTintEnabled(true);
//// 激活导航栏设置
//            tintManager.setNavigationBarTintEnabled(true);
//// 设置一个颜色给系统栏
//            tintManager.setTintColor(Color.parseColor("#40C4FF"));
        }
    }

    @TargetApi(19)
    public void setStatusBarColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            ViewGroup decorViewGroup = (ViewGroup) activity.getWindow().getDecorView();
            //获取自己布局的根视图
            View rootView = ((ViewGroup) (decorViewGroup.findViewById(android.R.id.content))).getChildAt(0);
            //预留状态栏位置
            rootView.setFitsSystemWindows(true);

            //添加状态栏高度的视图布局，并填充颜色
            View statusBarTintView = new View(activity);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    getInternalDimensionSize(activity.getResources(), "status_bar_height"));
            params.gravity = Gravity.TOP;
            statusBarTintView.setLayoutParams(params);
            statusBarTintView.setBackgroundColor(color);
            decorViewGroup.addView(statusBarTintView);
        }
    }

    public static int getInternalDimensionSize(Resources res, String key) {
        int result = 0;
        int resourceId = res.getIdentifier(key, "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unSubscribe();
        if (mLocalBroadcatManager!=null) {
            if (mExitBroadcastReceiver!=null)
            mLocalBroadcatManager.unregisterReceiver(mExitBroadcastReceiver);
        }
//        if (mEventBus != null) {
//            if (mEventBus.isRegistered(this))
//                mEventBus.unregister(this);
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
        JPushInterface.onPause(this);
 /*       UMengTestUtls.onPause(this);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentActivity=this;
        StatService.onResume(this);
        JPushInterface.onResume(this);
//        UMengTestUtls.onResume(this);
    }
}
