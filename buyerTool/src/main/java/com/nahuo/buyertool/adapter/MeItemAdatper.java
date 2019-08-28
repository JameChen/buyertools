package com.nahuo.buyertool.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.model.MeItemModel;

import java.util.List;

/**
 * Created by 诚 on 2015/9/21.
 */
public class MeItemAdatper extends BaseAdapter {

    public Context mContext;
    public List<MeItemModel> mList;

    public MeItemAdatper(Context Context, List<MeItemModel> List) {
        mContext = Context;
        mList = List;
    }

    private OnMeItemListener mListener;

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
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_item_me, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) view.findViewById(R.id.ItemText);
            holder.image = (ImageView) view.findViewById(R.id.ItemImage);
            holder.line = view.findViewById(R.id.item_line);
            holder.rl_item = view.findViewById(R.id.rl_item);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final MeItemModel model = mList.get(position);
//        if ((position >= 0 && position <= 2) || (position >= 6 && position <= 8) || (position >= 12 && position <= 14)) {
//            holder.line.setVisibility(View.VISIBLE);
//        } else {
//            holder.line.setVisibility(View.GONE);
//        }
        if (model.is_Show()){
            holder.line.setVisibility(View.VISIBLE);
        }else {
            holder.line.setVisibility(View.GONE);
        }
        if (model.getType()==-100) {
            holder.rl_item.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.item_bg_rect_defaut));
        } else {
            holder.rl_item.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_item));
        }
//        if (position == 5 || position == 17) {
//            holder.rl_item.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.item_bg_rect_defaut));
//        } else {
//            holder.rl_item.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_item));
//        }
        if (model != null) {
            holder.title.setText(model.getText());
          //  holder.image.setBackgroundResource(model.getSourceId());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.OnMeItemClick(model);
                }
            });
        }
        view.setBackgroundColor(Color.WHITE); //设置背景颜色

        return view;
    }


    public class ViewHolder {


        TextView title;
        ImageView image;
        View line, rl_item;

    }

    public interface OnMeItemListener {
        void OnMeItemClick(MeItemModel item);
    }

    public void setStyleListener(OnMeItemListener listener) {
        mListener = listener;
    }

}
