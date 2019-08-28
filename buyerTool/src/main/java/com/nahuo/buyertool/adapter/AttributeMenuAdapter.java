package com.nahuo.buyertool.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.Bean.AttributeBean;
import com.nahuo.buyertool.ViewHub;
import com.nahuo.buyertool.common.ListUtils;

/**
 * Created by jame on 2017/7/24.
 */

public class AttributeMenuAdapter extends MyBaseAdapter<AttributeBean.ChildsBean> {
    AttributeAdapter parentAdapter;
    int count;

    public AttributeMenuAdapter(Context context, AttributeAdapter parentAdapter) {
        super(context);
        this.parentAdapter = parentAdapter;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AttributeMenuAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.at_menu_item, parent, false);
            holder = new AttributeMenuAdapter.ViewHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final AttributeBean.ChildsBean bean = mdata.get(position);
        if (bean != null) {
            if (!TextUtils.isEmpty(bean.getName())) {
                holder.tv.setText(bean.getName());
            }
        }
        if (bean.isCheck()) {
            holder.tv.setBackgroundColor(mContext.getResources().getColor(R.color.dark_red));
        } else {
            holder.tv.setBackgroundColor(mContext.getResources().getColor(R.color.lightgray));
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (parentAdapter != null) {
                    count = parentAdapter.getSelCount();
                }
                setSingleCheck(bean);
            }
        });
        return convertView;
    }

    public boolean judeHasCheck() {
        if (!ListUtils.isEmpty(mdata)) {
            for (AttributeBean.ChildsBean xbean : mdata) {
                if (xbean.isCheck()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setSingleCheck(AttributeBean.ChildsBean bean) {
        if (!ListUtils.isEmpty(mdata)) {
            if (count >= 5) {
                if (judeHasCheck()) {
                    for (AttributeBean.ChildsBean xbean : mdata) {
                        if (bean.getID() == xbean.getID()) {
                            xbean.setCheck(!xbean.isCheck());
                        }else {
                            xbean.setCheck(false);
                        }
                    }
                } else {
                    ViewHub.showShortToast(mContext, "最多只能选择5个属性");
                }

            }else {
                for (AttributeBean.ChildsBean xbean : mdata) {
                    if (bean.getID() == xbean.getID()) {
                        xbean.setCheck(!xbean.isCheck());
                    }else {
                        xbean.setCheck(false);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        private TextView tv;
    }
}
