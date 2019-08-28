package com.nahuo.buyertool.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.model.ShopItemListModelX;

/**
 * Created by jame on 2017/7/7.
 */

public class ClonesDialog extends Dialog implements View.OnClickListener {
    static ClonesDialog dialog = null;
    private Activity mActivity;
    View mRootView;
    EditText et_old, et_new;
    Button btn_ok, btn_cancle;
    int h, w;
    PopDialogListener mPopDialogListener;
    ShopItemListModelX bean;
    String oldStr="", newStr="";

    public ClonesDialog(@NonNull Activity context) {
        super(context, R.style.popDialog);
        this.mActivity = context;
    }

    public static ClonesDialog getInstance(Activity activity) {
        if (dialog == null) {
            dialog = new ClonesDialog(activity);
        }
        return dialog;
    }

    public ClonesDialog setList(ShopItemListModelX bean) {
        this.bean = bean;
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
        mRootView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_shop_copy, null);
        et_old = (EditText) mRootView.findViewById(R.id.et_old);
        et_new = (EditText) mRootView.findViewById(R.id.et_new);
        btn_ok = (Button) mRootView.findViewById(R.id.btn_ok);
        btn_cancle = (Button) mRootView.findViewById(R.id.btn_cancle);
        btn_ok.setOnClickListener(this);
        btn_cancle.setOnClickListener(this);
        setContentView(mRootView);
        setCanceledOnTouchOutside(false);
        setCancelable(true);
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
                oldStr=et_old.getText().toString().trim();
                newStr=et_new.getText().toString().trim();
                if (mPopDialogListener != null) {
                    mPopDialogListener.onCopyDialogButtonClick(bean,oldStr,newStr);
                }
                dismiss();
                dialog = null;
                break;

        }
    }

    public ClonesDialog setPositive(PopDialogListener listener) {
        mPopDialogListener = listener;
        return this;
    }

    public interface PopDialogListener {
        void onCopyDialogButtonClick(ShopItemListModelX bean, String oldStr, String newStr);
    }
}
