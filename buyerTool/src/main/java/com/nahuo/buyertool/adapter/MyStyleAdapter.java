package com.nahuo.buyertool.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.Bean.MyStyleBean;
import com.nahuo.buyertool.Bean.UploadBean;
import com.nahuo.buyertool.ItemPreviewActivity;
import com.nahuo.buyertool.ViewHub;
import com.nahuo.buyertool.activity.MyStyleActivity;
import com.nahuo.buyertool.activity.ShopDetailsActivity;
import com.nahuo.buyertool.activity.UploadItemActivity;
import com.nahuo.buyertool.api.ShopSetAPI;
import com.nahuo.buyertool.common.ListUtils;
import com.nahuo.buyertool.common.SpManager;
import com.nahuo.buyertool.db.ToolUploadDbHelper;
import com.nahuo.buyertool.dialog.CopyDialog;
import com.nahuo.buyertool.dialog.MyStyleDialog;
import com.nahuo.library.controls.LoadingDialog;
import com.nahuo.library.helper.ImageUrlExtends;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.nahuo.buyertool.activity.MyStyleActivity.Style.DOWN_SHELVE_TYPE;
import static com.nahuo.buyertool.activity.MyStyleActivity.Style.HIDE_TYPE;
import static com.nahuo.buyertool.activity.MyStyleActivity.Style.ON_SHELVE_TYPE;
import static com.nahuo.buyertool.activity.MyStyleActivity.Style.SHOW_TYPE;

/**
 * Created by jame on 2017/7/12.
 */

public class MyStyleAdapter extends MyBaseAdapter<MyStyleBean> implements MyStyleDialog.PopDialogListener, CopyDialog.PopDialogListener {
    MyStyleActivity activity;
    private LoadingDialog mloadingDialog;
    private ToolUploadDbHelper toolUploadDbHelper;
    int userId = -1;

    public MyStyleAdapter(MyStyleActivity context) {
        super(context);
        this.activity = context;
        mloadingDialog = new LoadingDialog(context);
        toolUploadDbHelper = ToolUploadDbHelper.getInstance(context);
    }

    String cover = "";

    public void setItemDataNotify(UploadBean uploadBean) {
        if (uploadBean.getType() == UploadItemActivity.UPLOAD_TYPE) {
            MyStyleBean bean = new MyStyleBean();
            bean.setIsHide(false);
            bean.setCover(uploadBean.getLocal_pics().get(0).getPath());
            bean.setIs_check(false);
            bean.setName(uploadBean.getName());
            bean.setStatu("已上架");
            bean.setStatuID(1);
            bean.setRemark("");
            bean.setItemID(uploadBean.getItemID());
            bean.setPrice(uploadBean.getPrice());
            mdata.add(0, bean);
            notifyDataSetChanged();
        } else {
            if (!ListUtils.isEmpty(mdata)) {
                for (MyStyleBean bean : mdata) {
                    if (bean.getItemID() == uploadBean.getItemID()) {
                        bean.setName(uploadBean.getName());
                        bean.setCover(uploadBean.getLocal_pics().get(0).getPath());
                        bean.setPrice(uploadBean.getPrice());
                        break;
                    }
                }
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.my_style_item, parent, false);
            holder = new ViewHolder();
            holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
            holder.tv_if_show = (TextView) convertView.findViewById(R.id.tv_if_show);
            holder.tv_if_shelves = (TextView) convertView.findViewById(R.id.tv_if_shelves);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_remark = (TextView) convertView.findViewById(R.id.tv_remark);
            holder.tv_set_if_show = (TextView) convertView.findViewById(R.id.tv_set_if_show);
            holder.tv_set_istock = (TextView) convertView.findViewById(R.id.tv_set_istock);
            holder.tv_set_copy = (TextView) convertView.findViewById(R.id.tv_set_copy);
            holder.tv_set_edit = (TextView) convertView.findViewById(R.id.tv_set_edit);
            holder.tv_set_shelves = (TextView) convertView.findViewById(R.id.tv_set_shelves);
            holder.tv_on_shelves = (TextView) convertView.findViewById(R.id.tv_on_shelves);
            holder.iv_cover = (ImageView) convertView.findViewById(R.id.iv_cover);
            holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
            holder.ll_check = convertView.findViewById(R.id.ll_check);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final MyStyleBean bean = mdata.get(position);
        if (bean.isIsHide()) {
            holder.tv_if_show.setText("已隐藏");
            holder.tv_set_if_show.setText("显示");
            holder.tv_set_if_show.setBackgroundColor(Color.parseColor("#8e43cf"));
        } else {
            holder.tv_if_show.setText("已显示");
            holder.tv_set_if_show.setText("隐藏");
            holder.tv_set_if_show.setBackgroundColor(Color.parseColor("#FF666666"));
        }

        if (bean.getStatuID() == 1) {
            holder.tv_if_shelves.setVisibility(View.GONE);
            holder.tv_set_edit.setVisibility(View.VISIBLE);
            holder.tv_set_shelves.setVisibility(View.VISIBLE);
            holder.tv_set_copy.setVisibility(View.VISIBLE);
            holder.tv_set_if_show.setVisibility(View.VISIBLE);
            holder.tv_on_shelves.setVisibility(View.GONE);
        } else {
            //已下架
            holder.tv_if_shelves.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(bean.getStatu())) {
                holder.tv_if_shelves.setText(bean.getStatu());
            } else {
                holder.tv_if_shelves.setText("");
            }
            holder.tv_on_shelves.setVisibility(View.VISIBLE);
            holder.tv_set_edit.setVisibility(View.GONE);
            holder.tv_set_shelves.setVisibility(View.GONE);
            holder.tv_set_copy.setVisibility(View.VISIBLE);
            holder.tv_set_if_show.setVisibility(View.VISIBLE);

        }
        holder.tv_set_if_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bean.isIsHide()) {
                    MyStyleDialog.getInstance(activity).setTitle("是否要显示选中的款式").setAction(SHOW_TYPE).setList(bean).setPositive(MyStyleAdapter.this).showDialog();
                } else {
                    MyStyleDialog.getInstance(activity).setTitle("是否要显示隐藏的款式").setAction(HIDE_TYPE).setList(bean).setPositive(MyStyleAdapter.this).showDialog();
                }
            }
        });
        holder.tv_on_shelves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyStyleDialog.getInstance(activity).setTitle("是否要显示上架的款式").setAction(ON_SHELVE_TYPE).setList(bean).setPositive(MyStyleAdapter.this).showDialog();

            }
        });
        holder.tv_set_shelves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyStyleDialog.getInstance(activity).setTitle("是否要显示下架的款式").setAction(DOWN_SHELVE_TYPE).setList(bean).setPositive(MyStyleAdapter.this).showDialog();

            }
        });
        holder.tv_set_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CopyDialog.getInstance(activity).setList(bean).setPositive(MyStyleAdapter.this).showDialog();

            }
        });
        holder.tv_set_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userId = SpManager.getUserId(mContext);
               int count= toolUploadDbHelper.getEditUploadItemsCount(userId, bean.getItemID());
                if (count>0) {
                    ViewHub.showLongToast(mContext,"该款正在上传进度列表中，请上传成功后再重新编辑。");
                }else {
                    new EditTask(bean).execute();
                }

            }
        });
        if (!TextUtils.isEmpty(bean.getPrice())) {
            holder.tv_price.setText(bean.getPrice());
        } else {
            holder.tv_price.setText("");
        }
//        if (!TextUtils.isEmpty(bean.getName())) {
//            holder.tv_name.setText(bean.getName());
//        } else {
//            holder.tv_name.setText("");
//        }
        if (!TextUtils.isEmpty(bean.getRemark())) {
            holder.tv_remark.setText(bean.getRemark());
        } else {
            holder.tv_remark.setText("");
        }
        String title = "";
        if (bean.IsWarnTag) {
            title = "<b><font color=\"#FF0000\">" + bean.getNameTag() + "</font></b>" + bean.getName();
        } else {
            title = "<b><font color=\"#150ff4\">" + bean.getNameTag() + "</font></b>" + bean.getName();
        }
        holder.tv_name.setText(Html.fromHtml(title));
        cover = bean.getCover();
        if (!TextUtils.isEmpty(cover)) {
            Picasso.with(mContext).load(ImageUrlExtends.getImageUrl(cover, 14)).placeholder(R.drawable.empty_photo).into(holder.iv_cover);
        } else {
            holder.iv_cover.setImageResource(R.drawable.empty_photo);
        }
        if (bean.is_check()) {
            holder.checkbox.setChecked(true);
        } else {
            holder.checkbox.setChecked(false);
        }
        holder.ll_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bean.setIs_check(!bean.is_check());
                notifyDataSetChanged();
            }
        });
        if (bean.getStock() > 0) {
            holder.tv_set_istock.setText("库存" + bean.getStock() + "件");
        } else {
            holder.tv_set_istock.setText("库存");
        }
        holder.tv_set_istock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = "http://wap.admin.nahuo.com/stock/detail?sourceid=" + bean.getSourceID() + "&agentitemid=" + bean.getItemID();
                Intent it = new Intent(mContext, ItemPreviewActivity.class);
                it.putExtra("url", content);
                it.putExtra("name", bean.getName());
                mContext.startActivity(it);
            }
        });
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ShopDetailsActivity.class);
                intent.putExtra(ShopDetailsActivity.EXTRA_ID, bean.getItemID());
                intent.putExtra(ShopDetailsActivity.EXTRA_NAME, bean.getName());
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    List<Boolean> if_checks = new ArrayList<>();
    List<MyStyleBean> slist = new ArrayList<>();

    //获取选择
    public List<MyStyleBean> getMyStyleCheck() {
        slist.clear();
        if (!ListUtils.isEmpty(mdata)) {
            for (MyStyleBean bean : mdata) {
                if (bean.is_check()) {
                    slist.add(bean);
                }
            }
        }
        return slist;
    }

    //批量上架
    public void setMyStyleMoreOnShelf(List<MyStyleBean> list) {
        if (!ListUtils.isEmpty(list) && !ListUtils.isEmpty(mdata)) {
            for (int i = 0; i < mdata.size(); i++) {
                for (int j = 0; j < list.size(); j++) {
                    if (list.get(j).getItemID() == mdata.get(i).getItemID()) {
                        mdata.get(i).setStatuID(1);
                        mdata.get(i).setStatu("已上架");
                        mdata.get(i).setIs_check(false);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    //批量提审
    public void setMyStyleMoreArraignment(List<MyStyleBean> list) {
        if (!ListUtils.isEmpty(list) && !ListUtils.isEmpty(mdata)) {
            for (int i = 0; i < mdata.size(); i++) {
                for (int j = 0; j < list.size(); j++) {
                    if (list.get(j).getItemID() == mdata.get(i).getItemID()) {
                        mdata.get(i).setIs_check(false);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    //批量下架
    public void setMyStyleMoreOffShelf(List<MyStyleBean> list) {
        if (!ListUtils.isEmpty(list) && !ListUtils.isEmpty(mdata)) {
            for (int i = 0; i < mdata.size(); i++) {
                for (int j = 0; j < list.size(); j++) {
                    if (list.get(j).getItemID() == mdata.get(i).getItemID()) {
                        mdata.get(i).setStatuID(2);
                        mdata.get(i).setStatu("已下架");
                        mdata.get(i).setIs_check(false);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    //单个下架
    public void setMyStyleOffShelf(MyStyleBean bean) {
        if (!ListUtils.isEmpty(mdata)) {
            bean.setStatuID(2);
            bean.setStatu("已下架");
            notifyDataSetChanged();
        }
    }

    //单个上架
    public void setMyStyleOnShelf(MyStyleBean bean) {
        if (!ListUtils.isEmpty(mdata)) {
            bean.setStatuID(1);
            bean.setStatu("已上架");
            notifyDataSetChanged();
        }
    }

    //批量隐藏显示
    public void setMyStyleAllHide(List<MyStyleBean> list, boolean is_hide) {
        if (!ListUtils.isEmpty(list) && !ListUtils.isEmpty(mdata)) {
            for (int i = 0; i < mdata.size(); i++) {
                for (int j = 0; j < list.size(); j++) {
                    if (list.get(j).getItemID() == mdata.get(i).getItemID()) {
                        mdata.get(i).setIs_check(false);
                        mdata.get(i).setIsHide(is_hide);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    //单个设置显示和隐藏
    public void setMyStyleHide(MyStyleBean bean, boolean is_hide) {
        if (!ListUtils.isEmpty(mdata)) {
            bean.setIsHide(is_hide);
            notifyDataSetChanged();
        }
    }

    public void setAllCheck(CheckBox checkbox_all) {
        if_checks.clear();
        if (!ListUtils.isEmpty(mdata)) {
            for (MyStyleBean bean : mdata) {
                if_checks.add(bean.is_check());
            }
            if (if_checks.contains(false)) {
                for (MyStyleBean bean : mdata) {
                    bean.setIs_check(true);
                }
                if (checkbox_all != null)
                    checkbox_all.setChecked(true);
            } else {
                for (MyStyleBean bean : mdata) {
                    bean.setIs_check(false);
                }
                if (checkbox_all != null)
                    checkbox_all.setChecked(false);
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public void onPopDialogButtonClick(MyStyleActivity.Style action, MyStyleBean bean) {
        switch (action) {
            case SHOW_TYPE:
                new UpdateHideTask(false, bean).execute();
                // ViewHub.showShortToast(activity, "显示0" + bean.getName());
                break;
            case HIDE_TYPE:
                new UpdateHideTask(true, bean).execute();
                // ViewHub.showShortToast(activity, "隐藏0" + bean.getName());
                break;
            case DOWN_SHELVE_TYPE:
                new OffShelfItemsTask(bean).execute();
                //ViewHub.showShortToast(activity, "下架0" + bean.getName());
                break;
            case ON_SHELVE_TYPE:
                new OnShelfItemsTask(bean).execute();
                break;
        }
    }

    @Override
    public void onCopyDialogButtonClick(MyStyleBean bean, String oldStr, String newStr) {
        new CopyItemsTask(bean, oldStr, newStr).execute();
    }

    private static class ViewHolder {
        private TextView tv_price, tv_if_show, tv_if_shelves, tv_name, tv_set_if_show,
                tv_set_copy, tv_set_edit, tv_set_shelves, tv_remark, tv_on_shelves, tv_set_istock;
        private ImageView iv_cover;
        private CheckBox checkbox;
        private View ll_check;
    }

    class EditTask extends AsyncTask<Object, Void, Object> {
        MyStyleBean editBean;

        public EditTask(MyStyleBean editBean) {
            this.editBean = editBean;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mloadingDialog.start("获取编辑数据...");
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                UploadBean bean;
                if (editBean == null) {
                    return "error:商品ID为空";
                } else {
                    bean = ShopSetAPI.getItemForEdit(mContext, editBean.getItemID() + "");
                }
                return bean;
            } catch (Exception e) {
                e.printStackTrace();
                return "error:" + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            try {
                hideDialog();
                if (result instanceof String && ((String) result).startsWith("error:")) {
                    ViewHub.showLongToast(mContext, ((String) result).replace("error:", ""));
                } else {
                    UploadBean bean = (UploadBean) result;
                    Intent intent = new Intent(activity, UploadItemActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt(UploadItemActivity.EXTRA_UPLOAD_TYPE, UploadItemActivity.EDIT_TYPE);
                    bundle.putSerializable(UploadItemActivity.EXTRA_UPLOAD_SHOP_ITEM, bean);
                    intent.putExtras(bundle);
                    activity.startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class CopyItemsTask extends AsyncTask<Object, Void, Object> {
        MyStyleBean copyBean;
        String oldStr = "";
        String newStr = "";

        public CopyItemsTask(MyStyleBean copyBean, String oldStr, String newStr) {
            this.copyBean = copyBean;
            this.oldStr = oldStr;
            this.newStr = newStr;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mloadingDialog.start("复制中...");
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                if (copyBean == null) {
                    return "error:商品ID为空";
                }
                ShopSetAPI.copyItems(mContext, copyBean.getItemID() + "", oldStr, newStr);

            } catch (Exception e) {
                e.printStackTrace();
                return "error:" + e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            hideDialog();
            if (result instanceof String && ((String) result).startsWith("error:")) {
                ViewHub.showLongToast(mContext, ((String) result).replace("error:", ""));
            } else {
                ViewHub.showShortToast(mContext, "复制成功");
                activity.getDataRefesh();
            }
        }
    }

    class UpdateHideTask extends AsyncTask<Object, Void, Object> {
        boolean is_hide;
        MyStyleBean hideBean;

        public UpdateHideTask(boolean is_hide, MyStyleBean hideBean) {
            this.is_hide = is_hide;
            this.hideBean = hideBean;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mloadingDialog.start("设置中...");
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                if (hideBean == null) {
                    return "error:商品ID为空";
                } else {
                    ShopSetAPI.updateItemHide(mContext, hideBean.getItemID() + "", is_hide);
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "error:" + e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            try {
                hideDialog();
                if (result instanceof String && ((String) result).startsWith("error:")) {
                    ViewHub.showLongToast(mContext, ((String) result).replace("error:", ""));
                } else {
                    setMyStyleHide(hideBean, is_hide);
                    if (is_hide) {
                        ViewHub.showShortToast(mContext, "设置隐藏成功");
                    } else {
                        ViewHub.showShortToast(mContext, "设置显示成功");
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class OnShelfItemsTask extends AsyncTask<Object, Void, Object> {
        MyStyleBean offBean;

        public OnShelfItemsTask(MyStyleBean offBean) {
            this.offBean = offBean;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mloadingDialog.start("上架中...");
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                if (offBean == null) {
                    return "error:商品ID为空";
                } else {
                    ShopSetAPI.onShelfItems(mContext, offBean.getItemID() + "");
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "error:" + e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            hideDialog();
            if (result instanceof String && ((String) result).startsWith("error:")) {
                ViewHub.showLongToast(mContext, ((String) result).replace("error:", ""));
            } else {
                setMyStyleOnShelf(offBean);
            }
        }
    }

    public class OffShelfItemsTask extends AsyncTask<Object, Void, Object> {
        MyStyleBean offBean;

        public OffShelfItemsTask(MyStyleBean offBean) {
            this.offBean = offBean;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mloadingDialog.start("下架中...");
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                if (offBean == null) {
                    return "error:商品ID为空";
                } else {
                    ShopSetAPI.offShelfItems(mContext, offBean.getItemID() + "");
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "error:" + e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            hideDialog();
            if (result instanceof String && ((String) result).startsWith("error:")) {
                ViewHub.showLongToast(mContext, ((String) result).replace("error:", ""));
            } else {
                setMyStyleOffShelf(offBean);
            }
        }
    }

    protected boolean isDialogShowing() {
        return (mloadingDialog != null && mloadingDialog.isShowing());
    }

    protected void hideDialog() {
        if (isDialogShowing()) {
            mloadingDialog.stop();
        }
    }

}
