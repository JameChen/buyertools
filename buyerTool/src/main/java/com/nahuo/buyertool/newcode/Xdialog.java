package com.nahuo.buyertool.newcode;

import android.content.Context;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nahuo.buyer.tool.R;


/**
 * ===============BuyerTool====================
 * author：ChenZhen
 * Email：18620156376@163.com
 * Time : 2016/7/8 14:52
 * Description : 兼容低版本的mdDialog
 * ===============BuyerTool====================
 */
public class Xdialog {
    private static MaterialDialog mProDialog;
    private static MaterialDialog mMsgDilaog;
    private static MaterialDialog.Builder builder;

    /**
     * 统一风格的进度dialog
     *
     * @param context
     * @return
     */
    public static MaterialDialog lodingDialog(Context context) {
        if (mProDialog == null) {
            mProDialog = new MaterialDialog.Builder(context)
                    .title("加载页面数据")
                    .content("请等待")
                    .titleColor(context.getResources().getColor(R.color.font_item_title_color))
                    .contentColor(context.getResources().getColor(R.color.font_item_content_color))
                    .backgroundColor(context.getResources().getColor(R.color.white))
                    .progress(true, 0)
                    .show();
        } else {
            mProDialog.show();
        }
        return mProDialog;
    }

    /**
     * 只有确定按钮的dialog,确定按钮
     * @param context
     * @param title
     * @param msg
     * @param callback 点击确定的回调
     * @return
     */
    public static MaterialDialog msgDialog(Context context, String title, String msg, MaterialDialog.SingleButtonCallback callback) {
        if (mMsgDilaog == null) {
            mMsgDilaog = new MaterialDialog.Builder(context)
                    .title(title)
                    .titleColor(context.getResources().getColor(R.color.font_item_title_color))
                    .contentColor(context.getResources().getColor(R.color.font_item_content_color))
                    .backgroundColor(context.getResources().getColor(R.color.white))
                    .content(msg)
                    .positiveText("确定")
                    .onPositive(callback)
                    .show();
        } else {
            mMsgDilaog.setTitle(title);
            mMsgDilaog.setContent(msg);
            mMsgDilaog.show();
        }
        return mMsgDilaog;
    }

    /**
     * 统一风格的msgDialog
     *
     * @param context
     * @return
     */
    public static MaterialDialog msgDialog(Context context, String title, String msg) {
        if (mMsgDilaog == null) {
            mMsgDilaog = new MaterialDialog.Builder(context)
                    .title(title)
                    .titleColor(context.getResources().getColor(R.color.font_item_title_color))
                    .contentColor(context.getResources().getColor(R.color.font_item_content_color))
                    .backgroundColor(context.getResources().getColor(R.color.white))
                    .content(msg)
                    .positiveText("确定")
                    .show();
        } else {
            mMsgDilaog.setTitle(title);
            mMsgDilaog.setContent(msg);
            mMsgDilaog.show();
        }
        return mMsgDilaog;
    }

    /**
     * 含确认和取消两个按钮的dialog,点击取消默认处理dismiss,点击确定按钮通过传入callbak回调
     *
     * @param context  上下文
     * @param title    标题
     * @param msg      内容
     * @param callback 点击确定的事件回调
     * @return
     */
    public static MaterialDialog actionDialog(Context context, String title, String msg, MaterialDialog.SingleButtonCallback callback) {
        builder = new MaterialDialog.Builder(context);
        if (builder == null) {
            builder = new MaterialDialog.Builder(context);
        }
        MaterialDialog show = builder.title(title)
                .titleColor(context.getResources().getColor(R.color.font_item_title_color))
                .contentColor(context.getResources().getColor(R.color.font_item_content_color))
                .backgroundColor(context.getResources().getColor(R.color.white))
                .content(msg)
                .positiveText("确定")
                .onPositive(callback)
                .negativeText("取消")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();

        return show;
    }
}  