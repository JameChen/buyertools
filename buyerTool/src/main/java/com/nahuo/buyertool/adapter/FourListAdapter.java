package com.nahuo.buyertool.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.base.ViewHolder;
import com.nahuo.buyertool.common.FourListBean;
import com.nahuo.buyertool.common.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jame on 2017/12/18.
 */

public class FourListAdapter extends RecyclerView.Adapter<ViewHolder> {
    Context context;
    public static final int TYPE_PARENT = 1;
    public static final int TYPE_SUB = 2;
    int type;
    Listener mListener;

    public FourListAdapter(Context context) {
        this.context = context;
    }

    List<FourListBean> pareList = new ArrayList<>();

    public void setType(int type) {
        this.type = type;
    }

    public void setPareData(List<FourListBean> data) {
        this.pareList = data;
    }

    public void setParentIsSelect(FourListBean item) {
        if (item != null && !ListUtils.isEmpty(pareList)) {
            for (FourListBean bean : pareList) {
                if (bean.getID() == item.getID()) {
                    bean.isSelect = true;
                } else {
                    bean.isSelect = false;
                }
            }
        }
        notifyDataSetChanged();
    }

    public FourListBean getHasCheckData() {
        FourListBean fourListBean=null;
        if (!ListUtils.isEmpty(pareList)) {
            for (FourListBean bean : pareList) {
                if (bean.isSelect) {
                    fourListBean=bean;
                }
            }
        }
        return  fourListBean;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = ViewHolder.createViewHolder(context, parent, R.layout.item_category);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        View itemView = holder.getConvertView();
        TextView textView = (TextView) itemView.findViewById(R.id.textView);
        FourListBean bean = pareList.get(position);
        if (bean != null) {
            textView.setText(bean.getName());
            if (bean.isSelect) {
                textView.setTextColor(context.getResources().getColor(R.color.white));
                textView.setBackground(context.getResources().getDrawable(R.drawable.item_regent_press));
            } else {
                textView.setBackground(context.getResources().getDrawable(R.drawable.item_regent_normal));
                textView.setTextColor(context.getResources().getColor(R.color.item_text));
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
            FourListBean pItem = pareList.get(mPos);
            setParentIsSelect(pItem);
//            if (mListener != null)
//                mListener.onPareItemClick(pItem);
        }
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public interface Listener {
        void onPareItemClick(FourListBean item);
    }

    @Override
    public int getItemCount() {
        return pareList.size();
    }
}
