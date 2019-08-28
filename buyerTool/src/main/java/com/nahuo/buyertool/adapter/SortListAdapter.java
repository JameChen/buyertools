package com.nahuo.buyertool.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.Bean.SortBean;
import com.nahuo.buyertool.base.ViewHolder;
import com.nahuo.buyertool.common.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jame on 2017/12/18.
 */

public class SortListAdapter extends RecyclerView.Adapter<ViewHolder> {
    Context context;
    public static final int TYPE_PARENT = 1;
    public static final int TYPE_SUB = 2;
    int type;
    Listener mListener;

    public SortListAdapter(Context context) {
        this.context = context;
    }

    List<SortBean.ListBean> pareList = new ArrayList<>();

    public void setType(int type) {
        this.type = type;
    }

    public void setPareData(List<SortBean.ListBean> data) {
        this.pareList = data;
    }

    public void setParentIsSelect(int item) {
        if (item >=0 && !ListUtils.isEmpty(pareList)) {
            for (int i=0;i<pareList.size();i++) {
                SortBean.ListBean bean=pareList.get(i);
                if (i==item) {
                    bean.isSelect = true;
                } else {
                    bean.isSelect = false;
                }
            }
        }
        notifyDataSetChanged();
    }

    public SortBean.ListBean getHasCheckData() {
        SortBean.ListBean fourListBean=null;
        if (!ListUtils.isEmpty(pareList)) {
            for (SortBean.ListBean bean : pareList) {
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
        SortBean.ListBean bean = pareList.get(position);
        if (bean != null) {
            textView.setText(bean.getText());
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
         //  SortBean.ListBean pItem= pareList.get(mPos);
            setParentIsSelect(mPos);
//            if (mListener != null)
//                mListener.onPareItemClick(pItem);
        }
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public interface Listener {
        void onPareItemClick(SortBean.ListBean item);
    }

    @Override
    public int getItemCount() {
        return pareList.size();
    }
}
