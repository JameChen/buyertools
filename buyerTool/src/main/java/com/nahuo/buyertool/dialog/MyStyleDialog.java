package com.nahuo.buyertool.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.Bean.MyStyleBean;
import com.nahuo.buyertool.activity.MyStyleActivity;

/**
 * Created by jame on 2017/7/7.
 */

public class MyStyleDialog extends Dialog implements View.OnClickListener {
    static MyStyleDialog dialog = null;
    private Activity mActivity;
    View mRootView;
    TextView tv_title;
    Button btn_ok, btn_cancle;
    int h, w;
    PopDialogListener mPopDialogListener;
    int shopcartId;
    String content = "";
    MyStyleActivity.Style action;
    MyStyleBean bean;

    public MyStyleDialog(@NonNull Activity context) {
        super(context, R.style.popDialog);
        this.mActivity = context;
    }

    public static MyStyleDialog getInstance(Activity activity) {
        if (dialog == null) {
            dialog = new MyStyleDialog(activity);
        }
        return dialog;
    }

    public MyStyleDialog setTitle(String content) {
        this.content = content;
        return this;
    }

    public MyStyleDialog setList(MyStyleBean bean) {
        this.bean = bean;
        return this;
    }

    public MyStyleDialog setAction(MyStyleActivity.Style action) {
        this.action = action;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    private void initViews() {
        h = mActivity.getResources().getDisplayMetrics().heightPixels;
        w = mActivity.getResources().getDisplayMetrics().widthPixels;
        mRootView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_shop_my_style, null);
        tv_title = (TextView) mRootView.findViewById(R.id.tv_title);
        btn_ok = (Button) mRootView.findViewById(R.id.btn_ok);
        btn_cancle = (Button) mRootView.findViewById(R.id.btn_cancle);
        btn_ok.setOnClickListener(this);
        btn_cancle.setOnClickListener(this);
        setContentView(mRootView);
        setCanceledOnTouchOutside(false);
        setCancelable(true);
        if (!TextUtils.isEmpty(content)) {
            if (tv_title != null)
                tv_title.setText(content);
        }
    }


    public void showDialog() {
//        if (!TextUtils.isEmpty(content)) {
//            if (tv_title != null)
//                tv_title.setText(content);
//        }
        this.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        dialog = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancle:
                dismiss();
                dialog = null;
                break;
            case R.id.btn_ok:
                if (mPopDialogListener != null) {
                    mPopDialogListener.onPopDialogButtonClick(action,bean);
                }
                dismiss();
                dialog = null;
                break;

        }
    }

    public MyStyleDialog setPositive(PopDialogListener listener) {
        mPopDialogListener = listener;
        return this;
    }

    public interface PopDialogListener {
        void onPopDialogButtonClick(MyStyleActivity.Style action,MyStyleBean bean);
    }
}
