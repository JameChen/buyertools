package com.nahuo.buyertool.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.model.ShopItemListModel;
import com.nahuo.library.helper.ImageUrlExtends;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * project：PictureSelector
 * package：com.luck.pictureselector.adapter
 */
public class IteamColorPicsAdapter extends
        RecyclerView.Adapter<IteamColorPicsAdapter.ViewHolder>implements ItemTouchHelperAdapter {
    public static final int TYPE_CAMERA = 1;
    public static final int TYPE_PICTURE = 2;
    private LayoutInflater mInflater;
    private List<ShopItemListModel.ColorPicsBean> list = new ArrayList<>();
    private int selectMax = 9;
    private Context context;
    /**
     * 点击添加图片跳转
     */
    private onAddPicClickListener mOnAddPicClickListener;

    @Override
    public void onItemMove(RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        int fromPosition = source.getAdapterPosition();
        int toPosition = target.getAdapterPosition();
        if (fromPosition < list.size() && toPosition < list.size()) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(list, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(list, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
        }
//        if (fromPosition < list.size() && toPosition < list.size()) {
//            //交换数据位置
//            Collections.swap(list, fromPosition, toPosition);
//            //刷新位置交换
//            notifyItemMoved(fromPosition, toPosition);
//        }
        //移动过程中移除view的放大效果
        onItemClear(source);
    }

    @Override
    public void onItemDissmiss(RecyclerView.ViewHolder source) {
        int position = source.getAdapterPosition();
        list.remove(position); //移除数据
        notifyItemRemoved(position);//刷新数据移除
    }

    @Override
    public void onItemSelect(RecyclerView.ViewHolder viewHolder) {
   //当拖拽选中时放大选中的view
        viewHolder.itemView.setScaleX(1.2f);
        viewHolder.itemView.setScaleY(1.2f);
    }

    @Override
    public void onItemClear(RecyclerView.ViewHolder viewHolder) {
        //拖拽结束后恢复view的状态
        viewHolder.itemView.setScaleX(1.0f);
        viewHolder.itemView.setScaleY(1.0f);
    }

    @Override
    public void onItemFinish(RecyclerView.ViewHolder source) {

    }

    public interface onAddPicClickListener {
        void onAddPicClick(int position, View v);
    }

    public IteamColorPicsAdapter(Context context, onAddPicClickListener mOnAddPicClickListener) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.mOnAddPicClickListener = mOnAddPicClickListener;
    }

    public void setSelectMax(int selectMax) {
        this.selectMax = selectMax;
    }

    public void setList(List<ShopItemListModel.ColorPicsBean> list) {
        this.list = list;
    }
    public List<ShopItemListModel.ColorPicsBean>  getList() {
        return list;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mImg,iv_guan;
        LinearLayout ll_del;
        TextView tv_duration,tv_is_upload,tv_color;

        public ViewHolder(View view) {
            super(view);
            mImg = (ImageView) view.findViewById(R.id.fiv);
            iv_guan=(ImageView) view.findViewById(R.id.iv_guan);
            ll_del = (LinearLayout) view.findViewById(R.id.ll_del);
            tv_duration = (TextView) view.findViewById(R.id.tv_duration);
            tv_is_upload=(TextView)view.findViewById(R.id.tv_is_upload);
            tv_color=(TextView)view.findViewById(R.id.tv_color);
        }
    }

    @Override
    public int getItemCount() {
//        if (list.size() < selectMax) {
//            return list.size() + 1;
//        } else {
//            return list.size();
//        }
        return  list.size();
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
        View view = mInflater.inflate(R.layout.color_gv_filter_image_item,
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
          final ShopItemListModel.ColorPicsBean media= list.get(position);
        String path = "";
        viewHolder.tv_is_upload.setVisibility(View.GONE);
        viewHolder.iv_guan.setVisibility(View.GONE);
        viewHolder.ll_del.setVisibility(View.GONE);
        path= ImageUrlExtends.getImageUrl(media.getUrl(),9);
        if (TextUtils.isEmpty(media.getColor())){
            viewHolder.tv_color.setText("主色卡");
        }else {
            viewHolder.tv_color.setText(media.getColor()+"");
        }
        RequestOptions options = new RequestOptions()
                //.centerCrop()
                //.override(400,600)
                .placeholder(R.drawable.empty_photo)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(viewHolder.itemView.getContext())
                .load(path)
                .apply(options)
                .into(viewHolder.mImg);

            //itemView 的点击事件
//            if (mItemClickListener != null) {
//                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        int adapterPosition = viewHolder.getAdapterPosition();
//                        mItemClickListener.onItemClick(adapterPosition, v);
//                    }
//                });
//            }
    }

    protected OnItemClickListener mItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position, View v);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }
}
