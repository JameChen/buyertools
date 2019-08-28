package com.nahuo.buyertool.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.Bean.MyStyleBean;

/**
 * Created by jame on 2017/7/7.
 */

public class EditDialog extends Dialog implements View.OnClickListener {
    static EditDialog dialog = null;
    private Activity mActivity;
    View mRootView;
    EditText et_old, et_new,et_single_row,et_group_row,et_new_flag;
    Button btn_ok, btn_cancle;
    int h, w;
    PopDialogListener mPopDialogListener;
    MyStyleBean bean;
    String oldStr="", newStr="";
    String waitDays="",groupDealCount="",newQsFlag="";
    public EditDialog(@NonNull Activity context) {
        super(context, R.style.popDialog);
        this.mActivity = context;
    }

    public static EditDialog getInstance(Activity activity) {
        if (dialog == null) {
            dialog = new EditDialog(activity);
        }
        return dialog;
    }

    public EditDialog setList(MyStyleBean bean) {
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
        mRootView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_shop_edit, null);
        et_old = (EditText) mRootView.findViewById(R.id.et_old);
        et_new = (EditText) mRootView.findViewById(R.id.et_new);
        et_new_flag = (EditText) mRootView.findViewById(R.id.et_new_flag);
        et_single_row=(EditText)mRootView.findViewById(R.id.et_single_row) ;
        et_group_row=(EditText)mRootView.findViewById(R.id.et_group_row) ;
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
                waitDays=et_single_row.getText().toString().trim();
                groupDealCount=et_group_row.getText().toString().trim();
                newQsFlag=et_new_flag.getText().toString().trim();
                if (TextUtils.isEmpty(waitDays))
                    waitDays="-1";
                if (TextUtils.isEmpty(groupDealCount))
                    groupDealCount="-1";
                if (mPopDialogListener != null) {
                    mPopDialogListener.onEditDialogButtonClick(bean,oldStr,newStr,waitDays,groupDealCount,newQsFlag);
                }
                dismiss();
                dialog = null;
                break;

        }
    }

    public EditDialog setPositive(PopDialogListener listener) {
        mPopDialogListener = listener;
        return this;
    }

    public interface PopDialogListener {
        void onEditDialogButtonClick(MyStyleBean bean, String oldStr, String newStr,String waitDays,String groupDealCount,String newQsFlag);
    }
}
