package com.nahuo.buyertool.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.Bean.AttributeBean;
import com.nahuo.buyertool.Bean.UploadBean;
import com.nahuo.buyertool.activity.FullyGridLayoutManager;
import com.nahuo.buyertool.adapter.FourListAdapter;
import com.nahuo.buyertool.common.Constant;
import com.nahuo.buyertool.common.FourListBean;
import com.nahuo.buyertool.common.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jame on 2017/7/7.
 */

public class FourListDialog extends Dialog implements View.OnClickListener {
    static FourListDialog dialog = null;
    private Activity mActivity;
    View mRootView;
    TextView btn_ok, btn_cancle, tv_title;
    int h, w;
    PopDialogListener mPopDialogListener;
    List<FourListBean> list;
    List<Integer> propertyIDS;
    RecyclerView listView;
    List<String> data = new ArrayList<>();
    int type;
    FourListAdapter adapter;
    int hasSelId;

    public FourListDialog setHasSelId(int hasSelId) {
        this.hasSelId = hasSelId;
        return  this;
    }

    public FourListDialog(@NonNull Activity context) {
        super(context, R.style.popDialog);
        this.mActivity = context;
    }

    int choose_index = 0;

    public static FourListDialog getInstance(Activity activity) {
        if (dialog == null) {
            dialog = new FourListDialog(activity);
        }
        return dialog;
    }

    public FourListDialog setList(List<FourListBean> list) {
        this.list = list;
        return this;
    }

    public FourListDialog setPropertyIDS(List<Integer> propertyIDS) {
        this.propertyIDS = propertyIDS;
        return this;
    }


    public FourListDialog setType(int type) {
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
        mRootView = LayoutInflater.from(mActivity).inflate(R.layout.four_list_dialog, null);
        listView = (RecyclerView) mRootView.findViewById(R.id.listview);
        btn_ok = (TextView) mRootView.findViewById(R.id.tv_ok);
        btn_cancle = (TextView) mRootView.findViewById(R.id.tv_cancel);
        tv_title = (TextView) mRootView.findViewById(R.id.tv_title);
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
        switch (type) {
            case Constant.FourList.Type_MaterialList:
                tv_title.setText("选择材质");
                break;
            case Constant.FourList.Type_GetAgeList:
                tv_title.setText("选择年龄");
                break;
            case Constant.FourList.Type_StyleList:
                tv_title.setText("选择风格");
                break;
            case Constant.FourList.Type_SeasonList:
                tv_title.setText("选择季节");
                break;
        }
        if (!ListUtils.isEmpty(list)) {
            for (FourListBean bean : list) {
                if (hasSelId == bean.getID()&&hasSelId>0) {
                    bean.isSelect = true;
                }else {
                    bean.isSelect = false;
                }
            }
            adapter = new FourListAdapter(mActivity);
            adapter.setPareData(list);
            FullyGridLayoutManager manager = new FullyGridLayoutManager(mActivity, 3, GridLayoutManager.VERTICAL, false);
            listView.setLayoutManager(manager);
            listView.setAdapter(adapter);
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
                FourListBean bean=null;
                if (adapter != null) {
                    bean = adapter.getHasCheckData();
                }
                if (mPopDialogListener != null) {
                    if (bean!=null){
                        if (bean.getID()>0)
                            mPopDialogListener.onGetFourDialogButtonClick(bean,type);
                    }
                }
                dismiss();
                dialog = null;
                break;

        }
    }

    public FourListDialog setPositive(PopDialogListener listener) {
        mPopDialogListener = listener;
        return this;
    }

    public interface PopDialogListener {
        void onGetFourDialogButtonClick(FourListBean bean,int type);
    }
}
