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
import com.nahuo.buyertool.Bean.CategoryBean;
import com.nahuo.buyertool.ViewHub;
import com.nahuo.buyertool.activity.FullyGridLayoutManager;
import com.nahuo.buyertool.adapter.CategoryAdapter;
import com.nahuo.buyertool.common.ListUtils;
import com.nahuo.buyertool.loopview.LoopView;
import com.xinlan.imageeditlibrary.editimage.utils.ListUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jame on 2017/7/7.
 */

public class CategoryChooseDialog extends Dialog implements View.OnClickListener, CategoryAdapter.Listener {
    static CategoryChooseDialog dialog = null;
    private Activity mActivity;
    View mRootView;
    TextView btn_ok, btn_cancle;
    int h, w;
    PopDialogListener mPopDialogListener;
    List<CategoryBean> list;
    LoopView loopView, subLoopView;
    List<String> data = new ArrayList<>();
    List<String> subList = new ArrayList<>();
    int type;
    RecyclerView recyclerView;
    CategoryAdapter adapter;
    TextView tv_title,tv_reset;

    public CategoryChooseDialog(@NonNull Activity context) {
        super(context, R.style.popDialog);
        this.mActivity = context;
    }

    int choose_index = 0, choose_sub_index = 0;
    int parentID;
    int subID;

    public static CategoryChooseDialog getInstance(Activity activity) {
        if (dialog == null) {
            dialog = new CategoryChooseDialog(activity);
        }
        return dialog;
    }

    public CategoryChooseDialog setList(List<CategoryBean> list) {
        this.list = list;
        return this;
    }

    public CategoryChooseDialog setID(int parentID, int subID) {
        this.parentID = parentID;
        this.subID = subID;
        return this;
    }

    public CategoryChooseDialog setType(int type) {
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
        mRootView = LayoutInflater.from(mActivity).inflate(R.layout.category_choose_dialog, null);
        tv_title = (TextView) mRootView.findViewById(R.id.tv_title);
        tv_reset=(TextView)mRootView.findViewById(R.id.tv_reset);
        tv_reset.setOnClickListener(this);
        loopView = (LoopView) mRootView.findViewById(R.id.loopView_left);
        subLoopView = (LoopView) mRootView.findViewById(R.id.loopView_right);
        recyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerView);
        adapter = new CategoryAdapter(mActivity);
        adapter.setType(CategoryAdapter.TYPE_PARENT);
        adapter.setListener(this);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(mActivity, 3, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
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
        p.height = d.getHeight() * 2 / 3;
        dialog.getWindow().setAttributes(p); //设置生效
        tv_title.setText("选择一级分类");
        if (!ListUtils.isEmpty(list)) {
            for (CategoryBean bean:list) {
                if (bean!=null) {
                    if (bean.getID() == parentID) {
                        bean.is_Select = true;
                    } else {
                        bean.is_Select = false;
                    }
                    if (!ListUtils.isEmpty(bean.getChilds())) {
                        for (CategoryBean.ChildsBean childsBean:bean.getChilds()) {
                            if (subID==childsBean.getID()){
                                childsBean.is_Select=true;
                            }else {
                                childsBean.is_Select=false;
                            }
                        }
                    }
                }
            }
            adapter.setPareData(list);
            adapter.notifyDataSetChanged();
        }

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
        //}


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
            case R.id.tv_reset:
                if (adapter!=null){
                    tv_title.setText("选择一级分类");
                    adapter.setPareData(list);
                    adapter.setType(CategoryAdapter.TYPE_PARENT);
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.tv_cancel:
                dismiss();
                dialog = null;
                break;
            case R.id.tv_ok:

//                if (loopView != null)
//                    choose_index = loopView.getSelectedItem();
//                if (subLoopView != null)
//                    choose_sub_index = subLoopView.getSelectedItem();
//                if (choose_index >= 0 && !ListUtils.isEmpty(list)) {
//                    parentID = list.get(choose_index).getID();
//                    pName = list.get(choose_index).getName();
//                    if (choose_sub_index >= 0 && !ListUtils.isEmpty(list.get(choose_index).getChilds())) {
//                        subID = list.get(choose_index).getChilds().get(choose_sub_index).getID();
//                        sName = list.get(choose_index).getChilds().get(choose_sub_index).getName();
//                    }
//                }
                String pName = "", sName = "";
                if (isNeedSelect(childsBeanList,subID)){
                    ViewHub.showShortToast(mActivity,"请选择二级分类");
                    return;
                }
                if (!ListUtils.isEmpty(list)){
                    for (CategoryBean bean:list) {
                        if (parentID==bean.getID())
                            pName=bean.getName();
                    }
                }
                if (!ListUtil.isEmpty(childsBeanList)){
                    for (CategoryBean.ChildsBean childsBean:childsBeanList) {
                        if (childsBean.getID()==subID) {
                            sName=childsBean.getName();
                        }
                    }
                }
                if (mPopDialogListener != null) {
                    mPopDialogListener.onCategoryDialogButtonClick(parentID, subID, pName, sName);
                }
                dismiss();
                dialog = null;
                break;

        }
    }
    public boolean isNeedSelect( List<CategoryBean.ChildsBean> list,int subId ){
        boolean flag;
        if (!ListUtil.isEmpty(list)){
            List<Integer> ids=new ArrayList<>();
            for (CategoryBean.ChildsBean childsBean:list) {
               ids.add(childsBean.getID());
            }
            if (ids.contains(subId)){
                flag=false;
            }else {
                flag=true;
            }
        }else {
            flag=true;
        }
        return flag;
    }
    public CategoryChooseDialog setPositive(PopDialogListener listener) {
        mPopDialogListener = listener;
        return this;
    }

    @Override
    public void onSubItemClick(CategoryBean.ChildsBean item) {
        if (adapter!=null){
            adapter.setSubIsSelect(item);
            if (item!=null){
                subID=item.getID();
                parentID=item.getParentID();
               // sName=item.getName();
            }
            adapter.notifyDataSetChanged();
        }
    }
    List<CategoryBean.ChildsBean> childsBeanList=null;
    @Override
    public void onPareItemClick(CategoryBean item) {
         childsBeanList = item.getChilds();
        if (adapter != null) {
            tv_title.setText("选择二级分类");
//            if (item!=null){
//               pName= item.getName();
//            }
            adapter.setParentIsSelect(item);
            adapter.setSubData(childsBeanList);
            adapter.notifyDataSetChanged();
        }
    }

    public interface PopDialogListener {
        void onCategoryDialogButtonClick(int parentID, int subID, String pName, String sName);
    }
}
