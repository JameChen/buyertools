package com.nahuo.buyertool.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.Bean.CategoryBean;
import com.nahuo.buyertool.Bean.UploadBean;
import com.nahuo.buyertool.base.ViewHolder;
import com.nahuo.buyertool.common.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jame on 2017/12/18.
 */

public class ColorSelectAdapter extends RecyclerView.Adapter<ViewHolder> {
    Context context;
    public static final int TYPE_PARENT = 1;
    public static final int TYPE_SUB = 2;
    int type = TYPE_PARENT;
    Listener mListener;
    private String url = "";
    private boolean is_upload = true;

    public void setIs_upload(boolean is_upload) {
        this.is_upload = is_upload;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ColorSelectAdapter(Context context) {
        this.context = context;
    }

    List<UploadBean.ColorPicsBean> pareList = new ArrayList<>();
    List<CategoryBean.ChildsBean> subList = new ArrayList<>();

    public void setType(int type) {
        this.type = type;
    }

    public void setPareData(List<UploadBean.ColorPicsBean> data) {
        this.pareList = data;
    }

    //public void setSubData(List<CategoryBean.ChildsBean> data) {
//        this.subList = data;
//    }
    public void setParentIsSelect(int mPos) {
        if (!ListUtils.isEmpty(pareList)) {
            for (int i = 0; i < pareList.size(); i++) {
                if (i == mPos) {
                    pareList.get(i).is_Select = !pareList.get(i).is_Select;
                } else {
                    pareList.get(i).is_Select = false;
                }
            }
            notifyDataSetChanged();
        }
    }

    public String setParentValue() {
        String color="bai";
        if (!ListUtils.isEmpty(pareList)) {
            for (int i = 0; i < pareList.size(); i++) {
                if (pareList.get(i).is_Select == true) {
                    pareList.get(i).setIs_upload(is_upload);
                    pareList.get(i).setUrl(url);
                    color=pareList.get(i).getColor();
                }
            }
            notifyDataSetChanged();
        }
        return color;
    }

    //    public void setSubIsSelect(CategoryBean.ChildsBean item){
//        if (item!=null&& !ListUtils.isEmpty(pareList)){
//            for (CategoryBean bean:pareList) {
//                if (!ListUtils.isEmpty(bean.getChilds())){
//                    for (CategoryBean.ChildsBean childsBean:bean.getChilds()) {
//                        if (childsBean.getID()==item.getID()){
//                            childsBean.is_Select=true;
//                        }else {
//                            childsBean.is_Select=false;
//                        }
//                    }
//                }
//            }
//
//        }
//    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = ViewHolder.createViewHolder(context, parent, R.layout.item_category);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        View itemView = holder.getConvertView();
        TextView textView = (TextView) itemView.findViewById(R.id.textView);
        switch (type) {
            case TYPE_PARENT:
                UploadBean.ColorPicsBean bean = pareList.get(position);
                if (bean != null) {
                    if (TextUtils.isEmpty(bean.getColor())){
                        textView.setText("主色卡");
                    }else {
                        textView.setText(bean.getColor());
                    }
                    if (bean.is_Select) {
                        textView.setTextColor(context.getResources().getColor(R.color.white));
                        textView.setBackground(context.getResources().getDrawable(R.drawable.item_regent_press));
                    } else {
                        textView.setBackground(context.getResources().getDrawable(R.drawable.item_regent_normal));
                        textView.setTextColor(context.getResources().getColor(R.color.item_text));
                    }
                }
                break;
            case TYPE_SUB:
//                CategoryBean.ChildsBean childsBean = subList.get(position);
//                if (childsBean != null) {
//                    textView.setText(childsBean.getName());
//                    if (childsBean.is_Select){
//                        textView.setTextColor(context.getResources().getColor(R.color.white));
//                        textView.setBackground(context.getResources().getDrawable(R.drawable.item_regent_press));
//                    }else {
//                        textView.setBackground(context.getResources().getDrawable(R.drawable.item_regent_normal));
//                        textView.setTextColor(context.getResources().getColor(R.color.item_text));
//                    }
//                }
                break;
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
            switch (type) {
                case TYPE_PARENT:
                    setParentIsSelect(mPos);
//                    setType(TYPE_SUB);
//                    if (mListener!=null)
//                        mListener.onPareItemClick(mPos);
                    break;
                case TYPE_SUB:
//                    CategoryBean.ChildsBean item = subList.get(mPos);
//                    if (mListener!=null)
//                    mListener.onSubItemClick(item);
                    break;
            }
        }
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public interface Listener {
        // void onSubItemClick(CategoryBean.ChildsBean item);
        void onPareItemClick(int mPos);
    }

    @Override
    public int getItemCount() {
        return type == TYPE_PARENT ? pareList.size() : subList.size();
    }
}
