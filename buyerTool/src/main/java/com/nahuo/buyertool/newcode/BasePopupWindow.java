package com.nahuo.buyertool.newcode;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.nahuo.buyer.tool.R;

import butterknife.ButterKnife;

public abstract class BasePopupWindow extends PopupWindow {
    protected View mView;
    protected Context mContext;

    public BasePopupWindow(Context context) {
        super(context);
        this.mContext = context;
        init();
        initAfterViews();
        Log.i("==>", "pop初始化");
    }


    protected void init() {
        mView = LayoutInflater.from(mContext).inflate(getViewId(), null);
        ButterKnife.bind(this, mView);
        setWidth(getScreenWidth(mContext) * 8 / 10);
        setHeight(LayoutParams.WRAP_CONTENT);
        setContentView(mView);
        setBackgroundDrawable(mContext.getResources().getDrawable(R.color.clear));
        setFocusable(true);//默认点击背景不可操作
        setAnimationStyle(getAnimId());
        setOutsideTouchable(true);//默认点击其他区域自动消失
    }

    protected abstract void initAfterViews();

    protected abstract int getViewId();

    protected abstract int getAnimId();

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        showPOpbefore();
        super.showAtLocation(parent, gravity, x, y);
//        setPopWindowBg(0.3f);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        setPopWindowBg(1f);
    }

    protected void setPopWindowBg(float alpha) {
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow().getAttributes();
        lp.alpha = alpha;
        ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        ((Activity) mContext).getWindow().setAttributes(lp);
    }

    public int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 在每次show之前做某些操作
     */
    protected void showPOpbefore() {
        Log.i("==>", "重新调用show方法");
    }
}