package com.nahuo.buyertool.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.common.ListUtils;
import com.nahuo.buyertool.model.LabelItemModel;
import com.nahuo.buyertool.model.LabelModel;

import java.util.ArrayList;
import java.util.List;

public class LabelGridItemAdapter extends BaseAdapter {

    private Context                 mContext;
    private List<LabelItemModel>     mList;
    private List<String>            mCheckMap;              // 当前已勾选的尺码，选择颜色时使用
    private OnLabelItemClickListener onLabelItemClickListener;

    /**
     * 返回临时勾选的项
     * */
    public List<String> getCheckMap() {
        return mCheckMap;
    }

    /**
     * 返回临时勾选的项
     * */
    public List<String> getCheckIDsMap() {
        String checksStr = "";
        List<String> checkIDs = new ArrayList<String>();
        for (String itemStr : mCheckMap) {
            checksStr += "," + itemStr + ",";
        }
        for (LabelItemModel checkItem : mList) {
            if (checksStr.contains("," + checkItem.getLabel().getName() + ",")) {
                if (!TextUtils.isEmpty(checkItem.getLabel().getID())&&(Double.parseDouble(checkItem.getLabel().getID())) > 0) {
                    checkIDs.add(String.valueOf(checkItem.getLabel().getID()));
                }
            }
        }
        return checkIDs;
    }


   //返回选择的LabelModel
    public ArrayList<LabelModel> getLabelList() {
        String checksStr = "";
        ArrayList<String> checkIDs = new ArrayList<String>();
        ArrayList<LabelModel> checkLabelList = new ArrayList<LabelModel>();
        for (String itemStr : mCheckMap) {
            checksStr += "," + itemStr + ",";
        }
        for (LabelItemModel checkItem : mList) {
            if (checksStr.contains("," + checkItem.getLabel().getName() + ",")) {
                if (!TextUtils.isEmpty(checkItem.getLabel().getID())&&(Double.parseDouble(checkItem.getLabel().getID())) > 0) {
                    checkIDs.add(String.valueOf(checkItem.getLabel().getID()));
                    checkLabelList.add(checkItem.getLabel());
                }
            }
        }
        return checkLabelList;
    }

    public void setOnLabelItemClickListener(OnLabelItemClickListener listener) {
        onLabelItemClickListener = listener;
    }

    @SuppressLint("UseSparseArrays")
    public LabelGridItemAdapter(Context context, List<LabelItemModel> list) {
        mContext = context;
        mList = list;
        mCheckMap = new ArrayList<String>();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void notifyDataSetChanged() {
        mCheckMap.clear();
        for (LabelItemModel sizeItem : mList) {
            if (!sizeItem.isCheck())
                continue;

            String name = sizeItem.getLabel().getName();
            if (mCheckMap.contains(name))
                continue;
            mCheckMap.add(name);
        }
        super.notifyDataSetChanged();
        Log.v("LabelGridItemAdapter",mList.size()+"");
    }

    public List<LabelItemModel> getmList() {
        return mList;
    }

    public void setmList(List<LabelItemModel> mList) {
        this.mList = mList;
    }

    public void delete(LabelItemModel delMode){
        if(!ListUtils.isEmpty(mList)){
            for(int i=0;i<mList.size();i++){
                if(mList.get(i).getLabel().getID().equals(delMode.getLabel().getID())){
                    mList.remove(i);
                }
            }
        }
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (mList.size() > 0) {
            ViewHolder holder = null;
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.layout_pw_color_or_size_items, parent, false);
                holder = new ViewHolder();
                holder.checkBox = (CheckBox)view.findViewById(R.id.specqty_griditem_checkbox);
                holder.root = view.findViewById(R.id.itemRoot);
                holder.tvName = (TextView)view.findViewById(R.id.tv_name);
                view.setTag(holder);
            } else {
                holder = (ViewHolder)view.getTag();
            }

            boolean isChecked = false;
            LabelItemModel sizeItem = mList.get(position);
            String name = sizeItem.getLabel().getName();
            // 判断是否选择
            if (mCheckMap.contains(name)) {// sizeItem.isCheck() ||
                isChecked = true;
            }
            holder.tvName.setText(name);
            holder.checkBox.setChecked(isChecked);
            // 添加事件
            holder.root.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox ckb = (CheckBox)v.findViewById(R.id.specqty_griditem_checkbox);
                    TextView tvName = (TextView)v.findViewById(R.id.tv_name);
                    String name = tvName.getText().toString().trim();
                    boolean isChecked = ckb.isChecked();
                    Log.i("LabelAdapter", "ckb isChecked:" + isChecked + " name: " + name);
                    ckb.setChecked(!isChecked);

                    if (ckb.isChecked()) {
                        if (!mCheckMap.contains(name))
                            mCheckMap.add(name);
                    } else {
                        if (mCheckMap.contains(name))
                            mCheckMap.remove(name);
                    }
                    if (onLabelItemClickListener != null)
                        onLabelItemClickListener.onCheckChanged(v, ckb.isChecked());
                }
            });
            // 添加事件
            // holder.checkBox.setOnClickListener(new OnClickListener() {
            //
            // @Override
            // public void onClick(View v) {
            // CheckBox ckb = (CheckBox)v;
            // String name = ckb.getText().toString().trim();
            // // 判断是否选中，选中时向字典中添加已选中项，否则移除
            // boolean isChecked = ckb.isChecked();
            // if (isChecked) {
            // if (!mCheckMap.contains(name))
            // mCheckMap.add(name);
            // } else {
            // if (mCheckMap.contains(name))
            // mCheckMap.remove(name);
            // }
            // if (onSizeItemClickListener != null)
            // onSizeItemClickListener.onCheckChanged(v, isChecked);
            // }
            // });
        }
        return view;
    }

    private class ViewHolder {
        public TextView tvName;
        public View     root;
        CheckBox        checkBox;
    }

    public interface OnLabelItemClickListener {
        void onCheckChanged(View v, boolean isChecked);
    }
}
