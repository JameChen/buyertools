package com.nahuo.buyertool.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.Bean.UploadBean;
import com.nahuo.buyertool.Bean.UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean;
import com.nahuo.buyertool.activity.FullyGridLayoutManager;
import com.nahuo.buyertool.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jame on 2017/12/18.
 */

public class ExtendPropertyParentAdapter extends RecyclerView.Adapter<ViewHolder> {
    Context context;

    int type;
    Listener mListener;
    UploadBean.ExtendPropertyTypeListV2Bean propertyListBean;


    public ExtendPropertyParentAdapter(Context context) {
        this.context = context;
    }

    List<List<ExtendPropertyListBean>> pareList = new ArrayList<>();

    public void setType(int type) {
        this.type = type;
    }

    public void setData(UploadBean.ExtendPropertyTypeListV2Bean bean) {
        this.propertyListBean = bean;
        if (bean != null)
            this.pareList = bean.getExtendPropertyList();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = ViewHolder.createViewHolder(context, parent, R.layout.item_extendparentpropertyt);
        return holder;
    }

    ExtendPropertyAdapter adapter;

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        View itemView = holder.getConvertView();
        TextView tv_name = (TextView) itemView.findViewById(R.id.tv_name);
        RecyclerView listView = (RecyclerView) itemView.findViewById(R.id.recycler_view);
        View layout_title = itemView.findViewById(R.id.layout_title);
        List<ExtendPropertyListBean> list = pareList.get(position);
      //  String txt = "";
        if (propertyListBean != null) {
           // txt = propertyListBean.getTypeName();
            tv_name.setText("第" + (position + 1) + "栏");
            if (propertyListBean.isGrouping()) {
                layout_title.setVisibility(View.VISIBLE);
            } else {
                layout_title.setVisibility(View.GONE);
            }
            adapter = new ExtendPropertyAdapter(context);
            adapter.setPareData(list);
            adapter.setMore(propertyListBean.isMore());
            adapter.setValue(propertyListBean.isIsValue());
            if (propertyListBean.isIsValue()) {
                LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                listView.setLayoutManager(layoutManager);
            } else {
                FullyGridLayoutManager manager = new FullyGridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false);
                listView.setLayoutManager(manager);
            }
            listView.setNestedScrollingEnabled(true);
            listView.setHasFixedSize(true);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            //itemView.setOnClickListener(new OnItemClickListener(position));
        }
    }

    private class OnItemClickListener implements View.OnClickListener {
        private int mPos;

        public OnItemClickListener(int pos) {
            mPos = pos;
        }

        @Override
        public void onClick(View v) {
//            UploadBean.ExtendPropertyTypeListV2Bean pItem = pareList.get(mPos);
////            setParentIsSelect(pItem);
//            if (mListener != null)
//                mListener.onParePropItemClick(pItem);
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
