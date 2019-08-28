package com.nahuo.buyertool.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.Bean.CategoryBean;
import com.nahuo.buyertool.ViewHub;
import com.nahuo.buyertool.common.ListUtils;
import com.nahuo.buyertool.loopview.LoopView;
import com.nahuo.buyertool.model.ShopItemListModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jame on 2017/7/7.
 */

public class ReasonChooseDialog extends Dialog implements View.OnClickListener {
    static ReasonChooseDialog dialog = null;
    private Activity mActivity;
    View mRootView;
    TextView btn_ok, btn_cancle,tv_title;
    int h, w;
    PopDialogListener mPopDialogListener;
    List<CategoryBean> list;
    LoopView loopView, subLoopView;
    List<String> data = new ArrayList<>();
    List<String> subList = new ArrayList<>();
    ReasonType type;
    public ReasonChooseDialog(@NonNull Activity context) {
        super(context, R.style.popDialog);
        this.mActivity = context;
    }
   public enum ReasonType{
        断货,修改排单,延时截止入库,取消排单
    }
    int choose_index = 0,choose_sub_index=0;
    int parentID;
    int subID;
    private List<ShopItemListModel.ReasonListBean> mWaitDaysReasonList;
    private List<ShopItemListModel.ReasonListBean> mOutSupplyReasonList;
    private List<ShopItemListModel.ReasonListBean> mCloseStorageReasonList;
    private ShopItemListModel model;
    public static ReasonChooseDialog getInstance(Activity activity) {
        if (dialog == null) {
            dialog = new ReasonChooseDialog(activity);
        }
        return dialog;
    }

    public ReasonChooseDialog setModel(ShopItemListModel model) {
        this.model = model;
        mWaitDaysReasonList=model.getWaitDaysReasonList();
        mOutSupplyReasonList=model.getOutSupplyReasonList();
        mCloseStorageReasonList=model .getCloseStorageReasonList();
        return this;
    }

    public ReasonChooseDialog setID(int parentID, int subID) {
        this.parentID=parentID;
        this.subID=subID;
        return this;
    }

    public ReasonChooseDialog setType(ReasonType type) {
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
        mRootView = LayoutInflater.from(mActivity).inflate(R.layout.reason_choose_dialog, null);
        loopView = (LoopView) mRootView.findViewById(R.id.loopView_left);
        subLoopView = (LoopView) mRootView.findViewById(R.id.loopView_right);
        tv_title=(TextView)mRootView.findViewById(R.id.tv_title);
        loopView.setNotLoop();
        subLoopView.setNotLoop();
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
        subList.clear();
        switch (type) {
            case 取消排单:
                tv_title.setText("取消排单原因");
                if (!ListUtils.isEmpty(mWaitDaysReasonList)) {
                    for (ShopItemListModel.ReasonListBean bean : mWaitDaysReasonList) {
                        subList.add(bean.getText());
                    }
                    subLoopView.setItems(subList);
                    subLoopView.setInitPosition(0);
                }
                break;
            case 修改排单 :
                tv_title.setText("修改排单原因");
                if (!ListUtils.isEmpty(mWaitDaysReasonList)) {
                    for (ShopItemListModel.ReasonListBean bean : mWaitDaysReasonList) {
                        subList.add(bean.getText());
                    }
                    subLoopView.setItems(subList);
                    subLoopView.setInitPosition(0);
                }
                break;
            case 断货:
                tv_title.setText("断货原因");
                if (!ListUtils.isEmpty(mOutSupplyReasonList)) {
                    for (ShopItemListModel.ReasonListBean bean : mOutSupplyReasonList) {
                        subList.add(bean.getText());
                    }
                    subLoopView.setItems(subList);
                    subLoopView.setInitPosition(0);
                }
                break;
            case 延时截止入库:
                tv_title.setText("截止入库申请延时原因");
                if (!ListUtils.isEmpty(mCloseStorageReasonList)) {
                    for (ShopItemListModel.ReasonListBean bean : mCloseStorageReasonList) {
                        subList.add(bean.getText());
                    }
                    subLoopView.setItems(subList);
                    subLoopView.setInitPosition(0);
                }
                break;
        }
//        data.clear();
//        data.add("修改排单原因");
//        data.add("断货原因");
//        data.add("截止入库申请延时原因");
        // 滚动监听
//        loopView.setListener(new OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(int index) {
//                choose_index = index;
//                if (loopView != null)
//                    choose_index = loopView.getSelectedItem();
//                if (choose_index >= 0 && !ListUtils.isEmpty(data)) {
//                    subList.clear();
//                    switch (choose_index) {
//                        case 0:
//                            if (!ListUtils.isEmpty(mWaitDaysReasonList)) {
//                                for (ShopItemListModel.ReasonListBean bean : mWaitDaysReasonList) {
//                                    subList.add(bean.getText());
//                                }
//                                subLoopView.setItems(subList);
//                                subLoopView.setInitPosition(0);
//                            }
//                        break;
//                        case 1:
//                            if (!ListUtils.isEmpty(mOutSupplyReasonList)) {
//                                for (ShopItemListModel.ReasonListBean bean : mOutSupplyReasonList) {
//                                    subList.add(bean.getText());
//                                }
//                                subLoopView.setItems(subList);
//                                subLoopView.setInitPosition(0);
//                            }
//                            break;
//                        case 2:
//                            if (!ListUtils.isEmpty(mCloseStorageReasonList)) {
//                                for (ShopItemListModel.ReasonListBean bean : mCloseStorageReasonList) {
//                                    subList.add(bean.getText());
//                                }
//                                subLoopView.setItems(subList);
//                                subLoopView.setInitPosition(0);
//                            }
//                            break;
//                    }
//
//                }
//            }
//        });
        // 设置原始数据
//        loopView.setItems(data);
//        loopView.setInitPosition(choose_index);

//        if (!ListUtils.isEmpty(list)) {
//            data.clear();
//            for (int i=0;i<list.size();i++) {
//                if (parentID>0&&parentID==list.get(i).getID()){
//                    choose_index=i;
//                }
//                data.add(list.get(i).getName());
//            }
//            List<CategoryBean.ChildsBean> first_childs = list.get(choose_index).getChilds();
//            subList.clear();
//            if (!ListUtils.isEmpty(first_childs)) {
//                for (int i=0;i<first_childs.size();i++) {
//                    if (subID>0&&subID==first_childs.get(i).getID()){
//                        choose_sub_index=i;
//                    }
//                    subList.add(first_childs.get(i).getName());
//                }
//                subLoopView.setItems(subList);
//                subLoopView.setInitPosition(choose_sub_index);
//            }
//            // 滚动监听
//            loopView.setListener(new OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(int index) {
//                    choose_index = index;
//                    if (loopView != null)
//                        choose_index = loopView.getSelectedItem();
//                    if (choose_index >= 0 && !ListUtils.isEmpty(list)) {
//                        List<CategoryBean.ChildsBean> childs = list.get(choose_index).getChilds();
//                        subList.clear();
//                        if (!ListUtils.isEmpty(childs)) {
//                            for (CategoryBean.ChildsBean bean : childs) {
//                                subList.add(bean.getName());
//                            }
//                            subLoopView.setItems(subList);
//                            subLoopView.setInitPosition(0);
//                        }
//                    }
//                }
//            });
//            // 设置原始数据
//            loopView.setItems(data);
//            loopView.setInitPosition(choose_index);
//        }


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
    ShopItemListModel.ReasonListBean bean=null;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                dismiss();
                dialog = null;
                break;
            case R.id.tv_ok:
               // String pName="",sName="";

//                if (loopView != null)
//                    choose_index = loopView.getSelectedItem();
                if (subLoopView!=null)
                    choose_sub_index=subLoopView.getSelectedItem();
//                if (choose_index >= 0 && !ListUtils.isEmpty(list)) {
//                    parentID = list.get(choose_index).getID();
//                    pName=list.get(choose_index).getName();
//                    if (choose_sub_index >= 0 && !ListUtils.isEmpty(list.get(choose_index).getChilds())) {
//                        subID = list.get(choose_index).getChilds().get(choose_sub_index).getID();
//                        sName=list.get(choose_index).getChilds().get(choose_sub_index).getName();
//                    }
//                }
                switch (type) {
                    case 取消排单:
                    case 修改排单:
                        if (!ListUtils.isEmpty(mWaitDaysReasonList)) {
                         bean= mWaitDaysReasonList.get(choose_sub_index);
                        }
                        break;
                    case 断货:
                        if (!ListUtils.isEmpty(mOutSupplyReasonList)) {
                            bean=mOutSupplyReasonList.get(choose_sub_index);
                        }
                        break;
                    case 延时截止入库:
                        if (!ListUtils.isEmpty(mCloseStorageReasonList)) {
                            bean=mCloseStorageReasonList.get(choose_sub_index);
                        }
                        break;
                }
                if (bean.getIDX()==0){
                    ViewHub.showEditDialog(mActivity, "请输入其他原因","", new ViewHub.EditDialogListener() {
                        @Override
                        public void onOkClick(DialogInterface dialog1, EditText editText) {
                            ShopItemListModel.ReasonListBean xBean=new ShopItemListModel.ReasonListBean();
                            xBean.setText(editText.getText().toString().trim());
                            xBean.setIDX(bean.getIDX());
                            if (mPopDialogListener != null) {
                                mPopDialogListener.onReasonChooseOnClick(xBean,type);
                            }
                            dialog.dismiss();
                            dialog = null;
                        }

                        @Override
                        public void onOkClick(EditText editText) {

                        }

                    });

                }else {
                    if (mPopDialogListener != null) {
                        mPopDialogListener.onReasonChooseOnClick(bean,type);
                    }
                    dismiss();
                    dialog = null;
                }


                break;

        }
    }

    public ReasonChooseDialog setPositive(PopDialogListener listener) {
        mPopDialogListener = listener;
        return this;
    }

    public interface PopDialogListener {
        //void onCategoryDialogButtonClick(int parentID, int subID, String pName, String sName);
        void onReasonChooseOnClick(ShopItemListModel.ReasonListBean bean,ReasonType type);
    }
}
