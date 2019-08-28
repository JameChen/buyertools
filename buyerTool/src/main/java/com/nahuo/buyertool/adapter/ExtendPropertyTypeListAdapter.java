package com.nahuo.buyertool.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.Bean.UploadBean;
import com.nahuo.buyertool.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jame on 2017/12/18.
 */

public class ExtendPropertyTypeListAdapter extends RecyclerView.Adapter<ViewHolder> {
    Context context;

    int type;
    Listener mListener;

    public ExtendPropertyTypeListAdapter(Context context) {
        this.context = context;
    }

    List<UploadBean.ExtendPropertyTypeListV2Bean> pareList = new ArrayList<>();
  //  List<UploadBean.ExtendPropertyTypeListBean> hasList;

    public void setType(int type) {
        this.type = type;
    }

    public void setData(List<UploadBean.ExtendPropertyTypeListV2Bean> data) {
        this.pareList = data;
    }

   /* public void setHasData(List<UploadBean.ExtendPropertyTypeListV2Bean> hasList) {
        if (!ListUtils.isEmpty(hasList)) {
            for (int h = 0; h < hasList.size(); h++) {
                UploadBean.ExtendPropertyTypeListV2Bean ee = hasList.get(h);
                StringBuffer sb = new StringBuffer();
                sb.append("");
                boolean IsValue = ee.isIsValue();
                if (!ListUtils.isEmpty(ee.getExtendPropertyList())) {

                    for (int i = 0; i < ee.getExtendPropertyList().size(); i++) {
                        List<UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean> v2list = ee.getExtendPropertyList().get(i);
                        if (!ListUtils.isEmpty(v2list)) {
                            for (int j = 0; j < v2list.size(); j++) {
                                UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean aa = v2list.get(j);
                                sb.append(aa.getName());
                                if (!TextUtils.isEmpty(aa.getValue()))
                                    sb.append("(" + aa.getValue() + ")");
                                sb.append(",");
                            }
                        }
                        if (TextUtils.isEmpty(sb.toString())) {
                            ee.setSelContent("");
                        } else {
                            if (IsValue) {
                                sb.insert(0,(i+1) + "：");
                            }
                            ee.setSelContent(sb.substring(0, sb.length() - 1));
                        }
                    }
                }
            }
//            for (UploadBean.ExtendPropertyTypeListBean ee : hasList) {
//                StringBuffer sb = new StringBuffer();
//                sb.append("");
//                for (int i = 0; i < ee.getExtendPropertyList().size(); i++) {
//                    UploadBean.ExtendPropertyTypeListBean.ExtendPropertyListBean aa = ee.getExtendPropertyList().get(i);
//                        sb.append(aa.getName());
//                        if (!TextUtils.isEmpty(aa.getValue()))
//                            sb.append("(" + aa.getValue() + ")");
//                        sb.append(",");
//                }
//                if (TextUtils.isEmpty(sb.toString())) {
//                    ee.setSelContent("");
//                } else {
//                    ee.setSelContent(sb.substring(0, sb.length() - 1));
//                }
//            }
            if (!ListUtils.isEmpty(pareList)) {
                for (int i = 0; i < pareList.size(); i++) {
                    UploadBean.ExtendPropertyTypeListV2Bean a = pareList.get(i);
                    for (int j = 0; j < hasList.size(); j++) {
                        UploadBean.ExtendPropertyTypeListV2Bean b = hasList.get(j);
                        if (a.getTypeID() == b.getTypeID()) {
                            int count1 = 0, count2 = 0;
                            if (!ListUtils.isEmpty(b.getExtendPropertyList()))
                                count2 = b.getExtendPropertyList().size();
                            if (!ListUtils.isEmpty(a.getExtendPropertyList()))
                                count1 = a.getExtendPropertyList().size();
                            if (count2 > count1) {
                                List<List<UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean>>copyList=new ArrayList<>();
                                for (int c = 0; c < count2; c++) {
                                    List<List<UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean>> list = a.getExtendPropertyList();
                                    copyList.addAll(list);
                                }
                                a.setExtendPropertyList(copyList);
                            }
                        }
                    }
                }
                for (int i = 0; i < pareList.size(); i++) {
                    UploadBean.ExtendPropertyTypeListV2Bean a = pareList.get(i);
                    for (int j = 0; j < hasList.size(); j++) {
                        UploadBean.ExtendPropertyTypeListV2Bean b = hasList.get(j);
                        if (a.getTypeID() == b.getTypeID()) {
                            a.setSelContent(b.getSelContent());
                            if (!ListUtils.isEmpty(a.getExtendPropertyList())){
                                for (int o=0;o< a.getExtendPropertyList().size();o++) {
                                    List<UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean> list1=a.getExtendPropertyList().get(o);
                                    if (!ListUtils.isEmpty(b.getExtendPropertyList())) {
                                        for (int p = 0; p < b.getExtendPropertyList().size(); p++) {
                                            if (o == p) {
                                                List<UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean> list2=b.getExtendPropertyList().get(p);
                                               if (!ListUtils.isEmpty(list1)){
                                                   for (UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean bean:list1) {
                                                       if (!ListUtils.isEmpty(list2)){
                                                           for (UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean sub:list2) {
                                                               if (bean.getID()==sub.getID()){
                                                                   bean.isSelect = true;
                                                                   bean.setValue(sub.getValue());
                                                               }
                                                           }
                                                       }
                                                   }
                                               }

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

//                for (UploadBean.ExtendPropertyTypeListBean a : pareList) {
//                    for (UploadBean.ExtendPropertyTypeListBean b : hasList) {
//                        if (a.getTypeID() == b.getTypeID()) {
//                            a.setSelContent(b.getSelContent());
//                            for (UploadBean.ExtendPropertyTypeListBean.ExtendPropertyListBean bean : a.getExtendPropertyList()) {
//                                for (UploadBean.ExtendPropertyTypeListBean.ExtendPropertyListBean bb : b.getExtendPropertyList()) {
//                                    if (bb.getID() == bean.getID()) {
//                                        bean.isSelect = true;
//                                        bean.setValue(bb.getValue());
//                                    }
//                                }
//
//                            }
//                        }
//                    }
//                }
                notifyDataSetChanged();
            }
        }
    }*/
//    public void setParentIsSelect(FourListBean item) {
//        if (item != null && !ListUtils.isEmpty(pareList)) {
//            for (UploadBean.ExtendPropertyTypeListBean bean : pareList) {
//                if (bean.getID() == item.getID()) {
//                    bean.isSelect = true;
//                } else {
//                    bean.isSelect = false;
//                }
//            }
//        }
//        notifyDataSetChanged();
//    }
//
//    public FourListBean getHasCheckData() {
//        FourListBean fourListBean=null;
//        if (!ListUtils.isEmpty(pareList)) {
//            for (FourListBean bean : pareList) {
//                if (bean.isSelect) {
//                    fourListBean=bean;
//                }
//            }
//        }
//        return  fourListBean;
//    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = ViewHolder.createViewHolder(context, parent, R.layout.layout_extendpropertytypelist);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        View itemView = holder.getConvertView();
        TextView tv_name = (TextView) itemView.findViewById(R.id.tv_name);
        TextView tv_name_sel = (TextView) itemView.findViewById(R.id.tv_name_sel);
        UploadBean.ExtendPropertyTypeListV2Bean bean = pareList.get(position);
        if (bean != null) {
            tv_name.setText(bean.getTypeName());
            if (TextUtils.isEmpty(bean.getSelContent())) {
                tv_name_sel.setText("选择" + bean.getTypeName());
            } else {
                tv_name_sel.setText(bean.getSelContent());
            }
        }

        itemView.setOnClickListener(new OnItemClickListener(position));
    }

    private class OnItemClickListener implements View.OnClickListener {
        private int mPos;

        public OnItemClickListener(int pos) {
            mPos = pos;
        }

        @Override
        public void onClick(View v) {
            UploadBean.ExtendPropertyTypeListV2Bean pItem = pareList.get(mPos);
//            setParentIsSelect(pItem);
            if (mListener != null)
                mListener.onParePropItemClick(pItem);
        }
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public interface Listener {
        void onParePropItemClick(UploadBean.ExtendPropertyTypeListV2Bean item);
    }

    @Override
    public int getItemCount() {
        return pareList.size();
    }
}
