package com.nahuo.buyertool.adapter;


import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.CommonListActivity;
import com.nahuo.buyertool.common.SpManager;
import com.nahuo.buyertool.model.PurchaseModel;
import com.nahuo.buyertool.model.PurchaseModel;
import com.nahuo.library.helper.ImageUrlExtends;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

public class PurchaseListAdapter extends BaseAdapter {

    public Context mContext;
    public List<PurchaseModel> mList;

    // 构造函数
    public PurchaseListAdapter(Context Context, List<PurchaseModel> dataList) {
        mContext = Context;
        mList = dataList;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public PurchaseModel getItem(int arg0) {
        return mList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {

        final ViewHolder holder;
        View view = arg1;
        if (mList.size() > 0) {
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(
                        R.layout.item_purchase_list, arg2, false);
                holder = new ViewHolder();

                holder.tv1 = (TextView) view
                        .findViewById(R.id.item_purchase_tv1);
                holder.tv2 = (TextView) view
                        .findViewById(R.id.item_purchase_tv2);
                holder.tv3 = (TextView) view
                        .findViewById(R.id.item_purchase_tv3);
                holder.tvStat = (TextView) view
                        .findViewById(R.id.item_purchase_stat);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            PurchaseModel item = mList.get(arg0);

            if (SpManager.getSELECT_PURCHASE_DATA(mContext).getCode().equals(item.getCode()))
            {
                view.setBackgroundResource(R.color.light_blue);
            }
            else
            {
                view.setBackgroundResource(R.color.transparent);
            }

            holder.tv1.setText("单号：" + item.getCode() + "       时间：" + item.getTime());
            holder.tv2.setText("总金额：¥" + item.getTotalMoney() + "               已开金额：¥" + item.getKdMoney());
            holder.tv3.setText("市场：" + item.getMarketName() + "     楼层：" + item.getFloorName() + "      档口：" + item.getStallsName());
            holder.tvStat.setText(item.getStatusName());
        }

        return view;
    }

    public class ViewHolder {
        public int position;
        TextView tv1;
        TextView tv2;
        TextView tv3;
        TextView tvStat;
    }


}
