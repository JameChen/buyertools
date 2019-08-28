package com.nahuo.buyertool.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.Bean.AttributeBean;
import com.nahuo.buyertool.Bean.UploadBean;
import com.nahuo.buyertool.common.ListUtils;
import com.nahuo.library.controls.NoScrollGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jame on 2017/7/24.
 */

public class AttributeAdapter extends MyBaseAdapter<AttributeBean> {
    AttributeMenuAdapter menuAdapter;

    public AttributeAdapter(Context context) {
        super(context);

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AttributeAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.attribute_item, parent, false);
            holder = new AttributeAdapter.ViewHolder();
            holder.tv_parent = (TextView) convertView.findViewById(R.id.tv_p);
            holder.gridView = (NoScrollGridView) convertView.findViewById(R.id.gridview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        AttributeBean bean = mdata.get(position);
        if (bean != null) {
            if (!TextUtils.isEmpty(bean.getName())) {
                holder.tv_parent.setText(bean.getName() + "");
            }
        }
        if (!ListUtils.isEmpty(bean.getChilds())){
            menuAdapter=new AttributeMenuAdapter(mContext,this);
            holder.gridView.setAdapter(menuAdapter);
            menuAdapter.setData(bean.getChilds());
            menuAdapter.notifyDataSetChanged();
        }

        return convertView;
    }
    List<Boolean> check_list=new ArrayList<>();
    List<AttributeBean.ChildsBean> childsBeanList=new ArrayList<>();
    public List<AttributeBean.ChildsBean> getHasCheckData() {

        if (!ListUtils.isEmpty(mdata)) {
            childsBeanList.clear();
            for (AttributeBean bean :mdata) {
                if (!ListUtils.isEmpty(bean.getChilds())){
                    for (AttributeBean.ChildsBean childsBean:bean.getChilds()) {
                        if (childsBean.isCheck()){
                           childsBeanList.add(childsBean);
                        }
                    }
                }
            }
        }
        return  childsBeanList;
    }
    List<UploadBean.PropertyListBean> pBeanList=new ArrayList<>();

    public   List<UploadBean.PropertyListBean> getHasAtrCheckData() {

        if (!ListUtils.isEmpty(mdata)) {
            pBeanList.clear();
            for (AttributeBean bean :mdata) {
                if (!ListUtils.isEmpty(bean.getChilds())){
                    for (AttributeBean.ChildsBean childsBean:bean.getChilds()) {
                        if (childsBean.isCheck()){
                            UploadBean.PropertyListBean propertyListBean=new UploadBean.PropertyListBean();
                            propertyListBean.setID(childsBean.getID());
                            propertyListBean.setParentName(bean.getName());
                            propertyListBean.setName(childsBean.getName());
                            propertyListBean.setParentID(bean.getID());
                            pBeanList.add(propertyListBean);
                        }
                    }
                }
            }
        }
        return  pBeanList;
    }

    public int getSelCount(){
        check_list.clear();
        int count=0;
        if (!ListUtils.isEmpty(mdata)) {
            for (AttributeBean bean :mdata) {
                if (!ListUtils.isEmpty(bean.getChilds())){
                    for (AttributeBean.ChildsBean childsBean:bean.getChilds()) {
                        if (childsBean.isCheck()){
                            check_list.add(true);
                        }
                    }
                }
            }
            count=check_list.size();
            notifyDataSetChanged();
        }
        return count;
    }

    private static class ViewHolder {
        private TextView tv_parent;
        private NoScrollGridView gridView;
    }
}
