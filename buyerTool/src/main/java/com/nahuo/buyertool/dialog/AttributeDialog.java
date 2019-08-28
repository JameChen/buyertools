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
import android.widget.ListView;
import android.widget.TextView;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.Bean.AttributeBean;
import com.nahuo.buyertool.Bean.UploadBean;
import com.nahuo.buyertool.adapter.AttributeAdapter;
import com.nahuo.buyertool.common.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jame on 2017/7/7.
 */

public class AttributeDialog extends Dialog implements View.OnClickListener {
    static AttributeDialog dialog = null;
    private Activity mActivity;
    View mRootView;
    TextView btn_ok, btn_cancle;
    int h, w;
    PopDialogListener mPopDialogListener;
    List<AttributeBean> list;
    List<Integer> propertyIDS;
    ListView listView;
    List<String> data = new ArrayList<>();
    int type;
    AttributeAdapter adapter;

    public AttributeDialog(@NonNull Activity context) {
        super(context, R.style.popDialog);
        this.mActivity = context;
    }

    int choose_index = 0;

    public static AttributeDialog getInstance(Activity activity) {
        if (dialog == null) {
            dialog = new AttributeDialog(activity);
        }
        return dialog;
    }

    public AttributeDialog setList(List<AttributeBean> list) {
        this.list = list;
        return this;
    }

    public AttributeDialog setPropertyIDS(List<Integer> propertyIDS) {
        this.propertyIDS = propertyIDS;
        return this;
    }


    public AttributeDialog setType(int type) {
        this.type = type;
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
        mRootView = LayoutInflater.from(mActivity).inflate(R.layout.attribute_dialog, null);
        listView = (ListView) mRootView.findViewById(R.id.listview);
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
        p.height = d.getHeight() * 2 / 3;
        dialog.getWindow().setAttributes(p); //设置生效
        if (!ListUtils.isEmpty(list)) {
            if (!ListUtils.isEmpty(propertyIDS)) {
                for (AttributeBean bean : list) {
                    for (AttributeBean.ChildsBean childsBean : bean.getChilds()) {
                        for (int ID : propertyIDS) {
                            if (ID == childsBean.getID()) {
                                childsBean.setCheck(true);
                            }
                        }
                    }
                }
            }
            adapter = new AttributeAdapter(mActivity);
            listView.setAdapter(adapter);
            adapter.setData(list);
            adapter.notifyDataSetChanged();
//            data.clear();
//            for (WheelBean bean : list) {
//                data.add(bean.getName());
//            }
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

    List<AttributeBean.ChildsBean> childsBeanList;
    List<UploadBean.PropertyListBean> propertyListBeanList;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                dismiss();
                dialog = null;
                break;
            case R.id.tv_ok:
                if (adapter != null) {
                    childsBeanList = adapter.getHasCheckData();
                    propertyListBeanList = adapter.getHasAtrCheckData();
                }
                if (mPopDialogListener != null) {
                    mPopDialogListener.onGetAttriDialogButtonClick(childsBeanList, propertyListBeanList);
                }
                dismiss();
                dialog = null;
                break;

        }
    }

    public AttributeDialog setPositive(PopDialogListener listener) {
        mPopDialogListener = listener;
        return this;
    }

    public interface PopDialogListener {
        void onGetAttriDialogButtonClick(List<AttributeBean.ChildsBean> childsBeanList, List<UploadBean.PropertyListBean> propertyListBeanList);
    }
}
