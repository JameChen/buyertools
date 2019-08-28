package com.nahuo.buyertool.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.Bean.UploadBean;
import com.nahuo.buyertool.ViewHub;
import com.nahuo.buyertool.activity.UploadItemActivity;
import com.nahuo.buyertool.common.Const;
import com.nahuo.buyertool.common.ListUtils;
import com.nahuo.buyertool.dialog.DelDialog;
import com.nahuo.buyertool.dialog.InfoDialog;
import com.nahuo.buyertool.eventbus.BusEvent;
import com.nahuo.buyertool.eventbus.EventBusId;
import com.nahuo.buyertool.service.UploadItemService;
import com.nahuo.buyertool.service.UploadManager;
import com.nahuo.buyertool.uploadtask.UploadListener;
import com.nahuo.library.helper.ImageUrlExtends;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 */
public class UploadAdapter extends
        RecyclerView.Adapter<UploadAdapter.ViewHolder> implements DelDialog.PopDialogListener {
    public static final int TYPE_UPLOAD_WAIT = 1;
    public static final int TYPE_UPLOADING = 2;
    public static final int TYPE_UPLOAD_FAILED = 3;
    private LayoutInflater mInflater;
    private List<UploadBean> list = new ArrayList<>();
    private int selectMax = 9;
    private Context context;
    /**
     * 点击添加图片跳转
     */
    private onAddPicClickListener mOnAddPicClickListener;

    UploadManager uploadManager;

    public interface onAddPicClickListener {
        void onAddPicClick();
    }

    public UploadAdapter(Context context) {
        this.context = context;
        uploadManager = UploadItemService.getDownloadManager();
        mInflater = LayoutInflater.from(context);
    }

    public void setSelectMax(int selectMax) {
        this.selectMax = selectMax;
    }

    public void setList(List<UploadBean> list) {
        this.list = list;
    }


    @Override
    public int getItemCount() {
        return ListUtils.isEmpty(list) ? 0 : list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (!ListUtils.isEmpty(list)) {
            String statu = list.get(position).getUploadStatus();
            switch (statu) {
                case Const.UploadStatus.UPLOAD_WAIT:
                    return TYPE_UPLOAD_WAIT;
                case Const.UploadStatus.UPLOADING:
                    return TYPE_UPLOADING;
                case Const.UploadStatus.UPLOAD_FAILED:
                    return TYPE_UPLOAD_FAILED;
            }
        }
        return TYPE_UPLOAD_WAIT;
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.upload_progress_item,
                viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        //少于8张，显示继续添加的图标
        final UploadBean bean = list.get(position);

        if (getItemViewType(position) == TYPE_UPLOAD_WAIT) {
            viewHolder.tv_upload_info.setVisibility(View.GONE);
            viewHolder.tv_re_upload.setVisibility(View.GONE);
            viewHolder.tv_stop.setVisibility(View.GONE);
            viewHolder.ll_del.setVisibility(View.GONE);
            viewHolder.tv_upload_message.setVisibility(View.GONE);
            viewHolder.tv_upload_statu.setTextColor(context.getResources().getColor(R.color.gray));
        } else if (getItemViewType(position) == TYPE_UPLOADING) {
            viewHolder.tv_upload_info.setVisibility(View.GONE);
            viewHolder.tv_re_upload.setVisibility(View.GONE);
            viewHolder.tv_stop.setVisibility(View.VISIBLE);
            viewHolder.ll_del.setVisibility(View.GONE);
            viewHolder.tv_upload_message.setVisibility(View.GONE);
            viewHolder.tv_upload_statu.setTextColor(context.getResources().getColor(R.color.blue));

        } else if (getItemViewType(position) == TYPE_UPLOAD_FAILED) {
            viewHolder.tv_upload_info.setVisibility(View.VISIBLE);
            viewHolder.tv_re_upload.setVisibility(View.VISIBLE);
            viewHolder.tv_stop.setVisibility(View.GONE);
            viewHolder.ll_del.setVisibility(View.VISIBLE);
            viewHolder.tv_upload_message.setVisibility(View.VISIBLE);
            viewHolder.tv_upload_statu.setTextColor(context.getResources().getColor(R.color.red));
        }
        viewHolder.tv_upload_statu.setText(bean.getUploadStatus());
//        boolean is_upload = false;
//        if (!ListUtils.isEmpty(bean.getLocal_pics())) {
//            is_upload = bean.getLocal_pics().get(0).is_upload();
//        }
        String path = "";
        path = bean.getCover();
        // if (is_upload)
        path = ImageUrlExtends.getLocalOrImageUrl(path);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.color.color_f6)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(viewHolder.itemView.getContext())
                .load(path)
                .apply(options)
                .into(viewHolder.mImg);
        if (bean.getType() == UploadItemActivity.UPLOAD_TYPE) {
            viewHolder.tv_upload_new_change.setText("(新款)");
        } else {
            viewHolder.tv_upload_new_change.setText("(改款)");
        }
        if (!TextUtils.isEmpty(bean.getName()))
            viewHolder.tv_name.setText(bean.getName());
        viewHolder.ll_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DelDialog.getInstance((Activity) context).
                        setContent("删除任务？").setLeftStr("取消").setRightStr("确定").setDialogViewHolder(viewHolder).setPositive(UploadAdapter.this).showDialog();
            }
        });
        if (!TextUtils.isEmpty(bean.getMessage()) && bean.getMessage().contains("-")) {
            String[] ss = bean.getMessage().split("-");
            if (ss.length == 2)
                viewHolder.tv_upload_message.setText(ss[1]);
        }

        //itemView 的点击事件
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                        int adapterPosition = viewHolder.getAdapterPosition();
//                        mItemClickListener.onItemClick(adapterPosition, v);
                if (getItemViewType(position) != TYPE_UPLOAD_WAIT && getItemViewType(position) != TYPE_UPLOADING) {
                    Intent intent = new Intent(context, UploadItemActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt(UploadItemActivity.EXTRA_UPLOAD_CHANG_TYPE, UploadItemActivity.CHANG_TYPE);
                    bundle.putInt(UploadItemActivity.EXTRA_UPLOAD_TYPE, bean.getType());
                    bundle.putSerializable(UploadItemActivity.EXTRA_UPLOAD_SHOP_ITEM, bean);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            }
        });
        viewHolder.tv_stop.setOnClickListener(viewHolder);
        viewHolder.tv_re_upload.setOnClickListener(viewHolder);
        viewHolder.tv_upload_info.setOnClickListener(viewHolder);
        viewHolder.refresh(bean);
        UploadListener downloadListener = new MyDownloadListener();
        downloadListener.setUserTag(viewHolder);
        bean.setListener(downloadListener);

    }

    private class MyDownloadListener extends UploadListener {

        @Override
        public void onProgress(UploadBean downloadInfo) {
            if (getUserTag() == null) return;
            ViewHolder holder = (ViewHolder) getUserTag();
            holder.refresh();  //这里不能使用传递进来的 DownloadInfo，否者会出现条目错乱的问题
        }

        @Override
        public void onFinish(UploadBean downloadInfo) {
            if (downloadInfo.getType() == UploadItemActivity.UPLOAD_TYPE) {
                ViewHub.showShortToast(context, downloadInfo.getName() + "  上传成功");
            } else {
                ViewHub.showShortToast(context, downloadInfo.getName() + "  改款成功");
            }
            EventBus.getDefault().postSticky(BusEvent.getEvent(EventBusId.REFERSH_MYSTYLE_FINISH,downloadInfo));
            uploadManager.removeTask(downloadInfo.getCreat_time());
            notifyDataSetChanged();
        }

        @Override
        public void onError(UploadBean downloadInfo, String errorMsg, Exception e) {
            if (errorMsg != null)
                ViewHub.showLongToast(context, errorMsg);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mImg;
        LinearLayout ll_del;
        ProgressBar progressbar;
        TextView tv_re_upload, tv_upload_info, tv_stop, tv_name, tv_upload_message, tv_upload_image_video, tv_upload_pro, tv_upload_statu, tv_upload_new_change;

        public ViewHolder(View view) {
            super(view);
            mImg = (ImageView) view.findViewById(R.id.fiv);
            ll_del = (LinearLayout) view.findViewById(R.id.ll_del);
            tv_re_upload = (TextView) view.findViewById(R.id.tv_re_upload);
            tv_upload_info = (TextView) view.findViewById(R.id.tv_upload_info);
            tv_stop = (TextView) view.findViewById(R.id.tv_stop);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            progressbar = (ProgressBar) view.findViewById(R.id.progressbar);
            tv_upload_message = (TextView) view.findViewById(R.id.tv_upload_message);
            tv_upload_image_video = (TextView) view.findViewById(R.id.tv_upload_image_video);
            tv_upload_pro = (TextView) view.findViewById(R.id.tv_upload_pro);
            tv_upload_statu = (TextView) view.findViewById(R.id.tv_upload_statu);
            tv_upload_new_change = (TextView) view.findViewById(R.id.tv_upload_new_change);
        }

        UploadBean uploadBean;

        public void refresh(UploadBean downloadInfo) {
            this.uploadBean = downloadInfo;
            refresh();
        }

        //对于实时更新的进度ui，放在这里，例如进度的显示，而图片加载等不要放在这，会不停的重复回调
        //也会导致内存泄漏
        private void refresh() {
            // Toast.makeText(context, UploadBean.getProgress()+"", Toast.LENGTH_SHORT).show();
            progressbar.setProgress((int) uploadBean.getProgress());
            tv_upload_pro.setText((int) uploadBean.getProgress() + "%");
            int pic_tos = 0, of_pic_nums = 0, video_tos = 0, of_video_nums = 0;
            int color_pic_tos = 0, of_color_pic_nums = 0;
            if (!ListUtils.isEmpty(uploadBean.getColorPics())){
                color_pic_tos = uploadBean.getColorPics().size();
                for (UploadBean.ColorPicsBean mediaBean : uploadBean.getColorPics()) {
                    if (!mediaBean.is_upload()) {
                        of_color_pic_nums++;
                    }
                }
            }
            if (!ListUtils.isEmpty(uploadBean.getLocal_pics())) {
                pic_tos = uploadBean.getLocal_pics().size();
                for (UploadBean.MediaBean mediaBean : uploadBean.getLocal_pics()) {
                    if (!mediaBean.is_upload()) {
                        of_pic_nums++;
                    }
                }
            }
            if (!ListUtils.isEmpty(uploadBean.getLocal_videos())) {
                video_tos = uploadBean.getLocal_videos().size();
                for (UploadBean.MediaBean mediaBean : uploadBean.getLocal_videos()) {
                    if (!mediaBean.is_upload()) {
                        of_video_nums++;
                    }
                }
            }
            tv_upload_image_video.setText("图片：" + pic_tos + "剩" + of_pic_nums + "  |  "+"颜色"+color_pic_tos+"剩"+of_color_pic_nums+"\n"  + "视频：" + video_tos + "剩" + of_video_nums);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_stop:
                    uploadManager.stopTask(uploadBean.getCreat_time());
                    break;
                case R.id.tv_re_upload:
                    uploadManager.restartTask(uploadBean.getCreat_time());
                    break;
                case R.id.tv_upload_info:
                    String message = "暂无记录", title = "暂无记录";
                    if (!TextUtils.isEmpty(uploadBean.getMessage()) && uploadBean.getMessage().contains("-")) {
                        String[] ss = uploadBean.getMessage().split("-");
                        if (ss.length == 2) {
                            message = ss[1];
                            title = ss[0];
                        } else {
                            title = ss[0];
                        }
                    }
                    InfoDialog.getInstance((Activity) context).
                            setTitle(title).setContent(message).setRightStr("确定").showDialog();
                    break;
            }
        }
    }

    @Override
    public void onPopDialogButtonClick(int ok_cancel, UploadAdapter.ViewHolder holder) {
        if (ok_cancel == DelDialog.BUTTON_POSITIVIE) {
            int index = holder.getAdapterPosition();
            // 这里有时会返回-1造成数据下标越界,具体可参考getAdapterPosition()源码，
            // 通过源码分析应该是bindViewHolder()暂未绘制完成导致，知道原因的也可联系我~感谢
//            if (index != RecyclerView.NO_POSITION) {
//                list.remove(index);
//                notifyItemRemoved(index);
//                notifyItemRangeChanged(index, list.size());
//                DebugUtil.i("delete position:", index + "--->remove after:" + list.size());
//            }
            uploadManager.removeTask(list.get(index).getCreat_time());
            notifyDataSetChanged();
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
