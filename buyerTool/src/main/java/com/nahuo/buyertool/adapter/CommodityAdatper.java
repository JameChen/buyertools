package com.nahuo.buyertool.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.model.CommodityInfo;

import java.util.List;

/**
 * Created by Alan on 2015/9/21.
 */
public class CommodityAdatper extends BaseAdapter {

    public Context mContext;
    public List<CommodityInfo> mList;

    public CommodityAdatper(Context Context, List<CommodityInfo> List){
        mContext=Context;
        mList=List;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_item_commodity_type, parent, false);
            holder = new ViewHolder();
            holder.type = (TextView) view.findViewById(R.id.item_commodity_tv);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final CommodityInfo item = mList.get(position);
        if (item != null) {
            holder.type.setText(item.getText());
        }
        //view.setBackgroundColor(Color.WHITE); //设置背景颜色
        return view;
    }


    public class ViewHolder {
        TextView type;
    }
}
