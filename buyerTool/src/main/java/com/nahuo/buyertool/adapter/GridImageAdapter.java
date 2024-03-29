package com.nahuo.buyertool.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.DateUtils;
import com.luck.picture.lib.tools.DebugUtil;
import com.luck.picture.lib.tools.StringUtils;
import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.controls.NoScrollView;
import com.nahuo.library.helper.ImageUrlExtends;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * project：PictureSelector
 * package：com.luck.pictureselector.adapter
 */
public class GridImageAdapter extends
        RecyclerView.Adapter<GridImageAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    public static final int TYPE_CAMERA = 1;
    public static final int TYPE_PICTURE = 2;
    private LayoutInflater mInflater;
    private List<LocalMedia> list = new ArrayList<>();
    private int selectMax = 9;
    private Context context;
    private NoScrollView nestedScrollView;
    private RecyclerView recyclerView;
    public int sX;
    int sY;
    private boolean needScroll;

    public void setView(NoScrollView nestedScrollView, final RecyclerView recyclerView) {
        this.nestedScrollView = nestedScrollView;
        this.recyclerView = recyclerView;
//        this.nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(final NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                Log.d("yu1", "oldScrollY" + oldScrollY);
//                Log.d("yu1", "scrollY" + scrollY);
//                if (needScroll)
//                    ViewParentCompat.onStopNestedScroll(v,recyclerView);
////                if (needScroll)
////                v.scrollBy(0,oldScrollY);
//            }
//        });
    }

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
        Log.e("yu", "onItemMove");
    }

    @Override
    public void onItemDissmiss(RecyclerView.ViewHolder source) {
        int position = source.getAdapterPosition();
        list.remove(position); //移除数据
        notifyItemRemoved(position);//刷新数据移除
        Log.e("yu", "onItemDissmiss");
    }

    @Override
    public void onItemSelect(RecyclerView.ViewHolder viewHolder) {
        //当拖拽选中时放大选中的view
        viewHolder.itemView.setScaleX(1.2f);
        viewHolder.itemView.setScaleY(1.2f);
        Log.e("yu", "onItemSelect");
        needScroll = true;
        if (nestedScrollView != null) {
            //ViewCompat.setNestedScrollingEnabled(nestedScrollView, false);
            //nestedScrollView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_CANCEL, 0, 0, 0));
          //  nestedScrollView.stopNestedScroll();
            nestedScrollView.setNoscroll(true);
        }
        if (recyclerView != null) {
            recyclerView.setNestedScrollingEnabled(true);
        }
    }

    @Override
    public void onItemFinish(RecyclerView.ViewHolder source) {
        Log.e("yu", "onItemFinish");
        needScroll = false;
        if (nestedScrollView != null) {
            //  nestedScrollView.setEnabled(true);
            //    nestedScrollView.requestDisallowInterceptTouchEvent(true);
            //ViewCompat.setNestedScrollingEnabled(nestedScrollView, true);
            nestedScrollView.setNoscroll(false);
        }
        if (recyclerView != null) {
//            recyclerView.requestDisallowInterceptTouchEvent(false);
            recyclerView.setNestedScrollingEnabled(false);
        }
    }

    @Override
    public void onItemClear(RecyclerView.ViewHolder viewHolder) {
        //拖拽结束后恢复view的状态
        viewHolder.itemView.setScaleX(1.0f);
        viewHolder.itemView.setScaleY(1.0f);
        Log.e("yu", "onItemClear");
    }


    public interface onAddPicClickListener {
        void onAddPicClick();
    }

    public GridImageAdapter(Context context, onAddPicClickListener mOnAddPicClickListener) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.mOnAddPicClickListener = mOnAddPicClickListener;
    }

    public void setSelectMax(int selectMax) {
        this.selectMax = selectMax;
    }

    public void setList(List<LocalMedia> list) {
        this.list = list;
    }

    public List<LocalMedia> getList() {
        return list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mImg, iv_guan;
        LinearLayout ll_del;
        TextView tv_duration, tv_is_upload;

        public ViewHolder(View view) {
            super(view);
            mImg = (ImageView) view.findViewById(R.id.fiv);
            iv_guan = (ImageView) view.findViewById(R.id.iv_guan);
            ll_del = (LinearLayout) view.findViewById(R.id.ll_del);
            tv_duration = (TextView) view.findViewById(R.id.tv_duration);
            tv_is_upload = (TextView) view.findViewById(R.id.tv_is_upload);
        }
    }

    @Override
    public int getItemCount() {
        if (list.size() < selectMax) {
            return list.size() + 1;
        } else {
            return list.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowAddItem(position)) {
            return TYPE_CAMERA;
        } else {
            return TYPE_PICTURE;
        }
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.gv_filter_image,
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
        if (getItemViewType(position) == TYPE_CAMERA) {
            viewHolder.iv_guan.setVisibility(View.GONE);
            viewHolder.mImg.setImageResource(R.drawable.addimg_1x);
            viewHolder.mImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnAddPicClickListener.onAddPicClick();
                }
            });
            viewHolder.ll_del.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.iv_guan.setVisibility(View.VISIBLE);
            viewHolder.ll_del.setVisibility(View.VISIBLE);
            viewHolder.ll_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = viewHolder.getAdapterPosition();
                    // 这里有时会返回-1造成数据下标越界,具体可参考getAdapterPosition()源码，
                    // 通过源码分析应该是bindViewHolder()暂未绘制完成导致，知道原因的也可联系我~感谢
                    if (index != RecyclerView.NO_POSITION) {
                        list.remove(index);
                        notifyItemRemoved(index);
                        notifyItemRangeChanged(index, list.size());
                        DebugUtil.i("delete position:", index + "--->remove after:" + list.size());
                    }
                }
            });
            LocalMedia media = list.get(position);
            int mimeType = media.getMimeType();
            String path = "";
            if (media.is_upload()) {
                viewHolder.tv_is_upload.setVisibility(View.GONE);
            } else {
                viewHolder.tv_is_upload.setVisibility(View.VISIBLE);
            }
            if (media.isCut() && !media.isCompressed()) {
                // 裁剪过
                path = media.getCutPath();
            } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                path = media.getCompressPath();
            } else {
                // 原图
                path = media.getPath();
            }
            // 图片
            if (media.isCompressed()) {
                Log.i("compress image result:", new File(media.getCompressPath()).length() / 1024 + "k");
                Log.i("压缩地址::", media.getCompressPath());
            }

            Log.i("原图地址::", media.getPath());
            int pictureType = PictureMimeType.isPictureType(media.getPictureType());
            if (media.isCut()) {
                Log.i("裁剪地址::", media.getCutPath());
            }
            long duration = media.getDuration();
            if (pictureType == PictureConfig.TYPE_VIDEO) {
                viewHolder.iv_guan.setVisibility(View.GONE);
            }
            viewHolder.tv_duration.setVisibility(pictureType == PictureConfig.TYPE_VIDEO
                    ? View.VISIBLE : View.GONE);
            if (mimeType == PictureMimeType.ofAudio()) {
                viewHolder.tv_duration.setVisibility(View.VISIBLE);
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.picture_audio);
                StringUtils.modifyTextViewDrawable(viewHolder.tv_duration, drawable, 0);
            } else {
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.video_icon);
                StringUtils.modifyTextViewDrawable(viewHolder.tv_duration, drawable, 0);
            }
            viewHolder.tv_duration.setText(DateUtils.timeParse(duration));
            if (mimeType == PictureMimeType.ofAudio()) {
                viewHolder.mImg.setImageResource(R.drawable.audio_placeholder);
            } else {
                RequestOptions options = new RequestOptions()
                        //.centerCrop()
                        //.override(400,600)
                        .placeholder(R.color.color_f6)
                        .diskCacheStrategy(DiskCacheStrategy.ALL);
                RequestOptions options1 = new RequestOptions().centerCrop()
                        //.override(400,600)
                        .centerCrop()
                        .placeholder(R.color.color_f6)
                        .diskCacheStrategy(DiskCacheStrategy.ALL);
                if (media.is_upload()) {
                    path = ImageUrlExtends.getImageUrl(path, 9);
                }
                if (pictureType == PictureConfig.TYPE_VIDEO) {
                    Glide.with(viewHolder.itemView.getContext())
                            .load(path)
                            .apply(options1)
                            .into(viewHolder.mImg);
                } else {
                    Glide.with(viewHolder.itemView.getContext())
                            .load(path)
                            .apply(options)
                            .into(viewHolder.mImg);
                }

            }
            //itemView 的点击事件
            if (mItemClickListener != null) {
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int adapterPosition = viewHolder.getAdapterPosition();
                        mItemClickListener.onItemClick(adapterPosition, v);
                    }
                });
            }
        }
    }

    protected OnItemClickListener mItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position, View v);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }
}
