package com.nahuo.buyertool.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.Bean.WheelBean;
import com.nahuo.buyertool.common.ListUtils;
import com.nahuo.buyertool.loopview.LoopView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jame on 2017/7/7.
 */

public class WheelDialog extends Dialog implements View.OnClickListener {
    static WheelDialog dialog = null;
    private Activity mActivity;
    View mRootView;
    TextView btn_ok, btn_cancle;
    int h, w;
    PopDialogListener mPopDialogListener;
    List<WheelBean> list;
    LoopView loopView;
    List<String> data=new ArrayList<>();
    int type;
    public WheelDialog(@NonNull Activity context) {
        super(context, R.style.popDialog);
        this.mActivity = context;
    }
    int choose_index=0;
    public static WheelDialog getInstance(Activity activity) {
        if (dialog == null) {
            dialog = new WheelDialog(activity);
        }
        return dialog;
    }

    public WheelDialog setList(List<WheelBean> list) {
        this.list = list;
        return this;
    }
    public WheelDialog setType(int type){
        this.type=type;
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
        mRootView = LayoutInflater.from(mActivity).inflate(R.layout.wheel_dialog, null);
        loopView = (LoopView) mRootView.findViewById(R.id.loopView);
        loopView.setNotLoop();
        btn_ok = (TextView) mRootView.findViewById(R.id.tv_ok);
        btn_cancle = (TextView) mRootView.findViewById(R.id.tv_cancel);
        btn_ok.setOnClickListener(this);
        btn_cancle.setOnClickListener(this);
        setContentView(mRootView);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager m = mActivity.getWindowManager();
        Display d = m.getDefaultDisplay(); //为获取屏幕宽、高
        WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = d.getWidth(); //宽度设置为屏幕
        dialog.getWindow().setAttributes(p); //设置生效
        if (!ListUtils.isEmpty(list)){
            data.clear();
            for (WheelBean bean:list) {
                data.add(bean.getName());
            }
            // 滚动监听
//            loopView.setListener(new OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(int index) {
//                    choose_index=index;
//                }
//            });
            // 设置原始数据
            loopView.setItems(data);
            loopView.setInitPosition(0);
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
            case R.id.tv_cancel:
                dismiss();
                dialog = null;
                break;
            case R.id.tv_ok:
                WheelBean bean= null;
                if (loopView!=null)
                   choose_index= loopView.getSelectedItem();
                if (choose_index>=0&&!ListUtils.isEmpty(list)){
                   bean =list.get(choose_index);
                }
                if (mPopDialogListener != null) {
                    mPopDialogListener.onCopyDialogButtonClick(bean,type);
                }
                dismiss();
                dialog = null;
                break;

        }
    }

    public WheelDialog setPositive(PopDialogListener listener) {
        mPopDialogListener = listener;
        return this;
    }

    public interface PopDialogListener {
        void onCopyDialogButtonClick(WheelBean bean ,int type);
    }
}
