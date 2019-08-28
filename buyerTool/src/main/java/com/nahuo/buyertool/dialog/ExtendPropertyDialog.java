package com.nahuo.buyertool.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
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
import com.nahuo.buyertool.ViewHub;
import com.nahuo.buyertool.adapter.ExtendPropertyParentAdapter;
import com.nahuo.buyertool.common.FourListBean;
import com.nahuo.buyertool.common.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jame on 2017/7/7.
 */

public class ExtendPropertyDialog extends Dialog implements View.OnClickListener {
    static ExtendPropertyDialog dialog = null;
    private Activity mActivity;
    View mRootView;
    TextView btn_ok, btn_cancle, tv_title, tv_add;
    int h, w;
    PopDialogListener mPopDialogListener;
    List<List<UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean>> list;
//    List<ExtendPropertyListBean> sellist;
//
//    public void setSellist(List<ExtendPropertyListBean> sellist) {
//        this.sellist = sellist;
//    }

    UploadBean.ExtendPropertyTypeListV2Bean propertyListBean;
    List<Integer> propertyIDS;
    RecyclerView listView;
    List<String> data = new ArrayList<>();
    int type;
    ExtendPropertyParentAdapter adapter;
    int hasSelId;

    public ExtendPropertyDialog setHasSelId(int hasSelId) {
        this.hasSelId = hasSelId;
        return this;
    }

    public ExtendPropertyDialog(@NonNull Activity context) {
        super(context, R.style.popDialog);
        this.mActivity = context;
    }

    int choose_index = 0;

    public static ExtendPropertyDialog getInstance(Activity activity) {
        if (dialog == null) {
            dialog = new ExtendPropertyDialog(activity);
        }
        return dialog;
    }


    public ExtendPropertyDialog setData(UploadBean.ExtendPropertyTypeListV2Bean bean) {
        this.propertyListBean = bean;
        if (bean != null) {
            this.list = bean.getExtendPropertyList();
        }
        return this;
    }

    public ExtendPropertyDialog setPropertyIDS(List<Integer> propertyIDS) {
        this.propertyIDS = propertyIDS;
        return this;
    }


    public ExtendPropertyDialog setType(int type) {
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
        tv_add = (TextView) mRootView.findViewById(R.id.tv_add);
        tv_add.setOnClickListener(this);
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

        if (propertyListBean != null) {
            tv_title.setText("选择" + propertyListBean.getTypeName());
            if (propertyListBean.isGrouping()) {
                tv_add.setVisibility(View.VISIBLE);
            } else {
                tv_add.setVisibility(View.GONE);
            }
        }

        if (!ListUtils.isEmpty(list)) {
            adapter = new ExtendPropertyParentAdapter(mActivity);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
            listView.setLayoutManager(layoutManager);
            adapter.setData(propertyListBean);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
//            if (!ListUtils.isEmpty(sellist)){
//                for (ExtendPropertyListBean bean : list) {
//                    for (ExtendPropertyListBean sel : sellist) {
//                        if (sel.getID()==bean.getID()){
//                            bean.isSelect=true;
//                        }
//                    }
//
//                }
//            }
//
//            adapter = new ExtendPropertyAdapter(mActivity);
//            adapter.setPareData(list);
//            adapter.setMore(propertyListBean.isMore());
//            adapter.setValue(propertyListBean.isIsValue());
//            if (propertyListBean.isIsValue()) {
//                LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
//                listView.setLayoutManager(layoutManager);
//            }else {
//                FullyGridLayoutManager manager = new FullyGridLayoutManager(mActivity, 3, GridLayoutManager.VERTICAL, false);
//                listView.setLayoutManager(manager);
//            }
//            listView.setAdapter(adapter);
//            adapter.notifyDataSetChanged();

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
            case R.id.tv_add:
                if (propertyListBean != null) {
                    List<List<UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean>> pList = propertyListBean.getExtendPropertyList();
                    if (!ListUtils.isEmpty(pList)) {
                        int count=pList.size();
                        List<UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean> sList = pList.get(0);
                        List<UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean> addList = new ArrayList<>();
                        if (!ListUtils.isEmpty(sList)) {
                            for (UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean b: sList) {
                                if (b!=null) {
                                    UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean a = new UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean();
                                    a.setID(b.getID());
                                    a.setName(b.getName());
                                    a.setValue("");
                                    a.isSelect = false;
                                    addList.add(a);
                                }
                            }
                        }
                        pList.add(addList);
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                        if (listView!=null){
                            listView.scrollToPosition(count);
                        }
                        ViewHub.showShortToast(mActivity,"添加成功");
                    }
                }
                break;
            case R.id.tv_ok:
                FourListBean bean = null;
//                if (adapter != null) {
//                    //bean = adapter.getHasCheckData();
//                }
                if (mPopDialogListener != null) {
//                    if (bean!=null){
//                        if (bean.getID()>0)
                    mPopDialogListener.onExtendPropertyDialogButtonClick(propertyListBean);
                    // }
                }
                dismiss();
                dialog = null;
                break;

        }
    }

    public ExtendPropertyDialog setPositive(PopDialogListener listener) {
        mPopDialogListener = listener;
        return this;
    }

    public interface PopDialogListener {
        void onExtendPropertyDialogButtonClick(UploadBean.ExtendPropertyTypeListV2Bean bean);
    }
}
