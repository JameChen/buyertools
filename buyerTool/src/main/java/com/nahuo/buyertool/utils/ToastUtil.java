package com.nahuo.buyertool.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.BWApplication;
import com.nahuo.library.helper.DisplayUtil;


/**
 * Created by apple on 16/7/8.
 */
public class ToastUtil {
    private static ToastUtil toastUtils = null;

    private Toast myToast;
    private TextView contentText;
    public View view = null;

    private ToastUtil(Context context) {
        initView(context);
    }

    public static synchronized ToastUtil getInstance(Context context) {
        if (null == toastUtils) {
            toastUtils = new ToastUtil(context);
        }
        return toastUtils;
    }

    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(BWApplication.getInstance());
        view = inflater.inflate(R.layout.toast, null);
        contentText = (TextView) view.findViewById(R.id.toast_content_tv);
        myToast = new Toast(context);
        view.setLayoutParams(new RelativeLayout.LayoutParams(100, 40));
        myToast.setView(view);
    }


    public void showLongToast(String content) {
        if (myToast != null) {
            contentText.setText(content);
            int width = DisplayUtil.getScreenWidth();
            int height = DisplayUtil.getScreenHeight();
            myToast.setGravity(Gravity.BOTTOM, 0, height / 5);
            myToast.setDuration(Toast.LENGTH_SHORT);
            myToast.show();
        }

    }
    public void showShortToast(String content) {
        if (myToast != null) {
            contentText.setText(content);
            int width = DisplayUtil.getScreenWidth();
            int height = DisplayUtil.getScreenHeight();
            myToast.setGravity(Gravity.BOTTOM, 0, height / 5);
            myToast.setDuration(Toast.LENGTH_SHORT);
            myToast.show();
        }

    }

}