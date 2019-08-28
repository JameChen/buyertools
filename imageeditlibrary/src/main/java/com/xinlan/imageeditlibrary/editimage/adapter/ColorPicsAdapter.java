package com.xinlan.imageeditlibrary.editimage.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xinlan.imageeditlibrary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * project：PictureSelector
 * package：com.luck.pictureselector.adapter
 */
public class ColorPicsAdapter extends
        RecyclerView.Adapter<ColorPicsAdapter.ViewHolder> {
    public static final int TYPE_CAMERA = 1;
    public static final int TYPE_PICTURE = 2;
    private LayoutInflater mInflater;
    private List<String> list = new ArrayList<>();
    private int selectMax = 9;
    private Context context;
    /**
     * 点击添加图片跳转
     */
    private onAddPicClickListener mOnAddPicClickListener;


    public interface onAddPicClickListener {
        void onAddPicClick(int position, View v);
    }

    public ColorPicsAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setSelectMax(int selectMax) {
        this.selectMax = selectMax;
    }

    public void setList(ArrayList<String> list) {
        this.list = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_color;

        public ViewHolder(View view) {
            super(view);
            tv_color = (TextView) view.findViewById(R.id.textView);
        }
    }

    @Override
    public int getItemCount() {
//        if (list.size() < selectMax) {
//            return list.size() + 1;
//        } else {
//            return list.size();
//        }
        return list.size();
    }

//    @Override
//    public int getItemViewType(int position) {
//        if (isShowAddItem(position)) {
//            return TYPE_CAMERA;
//        } else {
//            return TYPE_PICTURE;
//        }
//    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_color,
                viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    private boolean isShowAddItem(int position) {
        int size = list.size() == 0 ? 0 : list.size();
        return position == size;
    }

    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        //少于8张，显示继续添加的图标
        final String media = list.get(position);
        viewHolder.tv_color.setText(media);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = viewHolder.getAdapterPosition();
                String color = list.get(adapterPosition);
                if (mItemClickListener != null)
                    mItemClickListener.onItemClick(adapterPosition, v, color);
            }
        });
    }


    protected OnItemClickListener mItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position, View v, String color);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }
}
