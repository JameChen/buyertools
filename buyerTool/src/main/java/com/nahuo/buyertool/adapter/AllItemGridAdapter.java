package com.nahuo.buyertool.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.ViewHub;
import com.nahuo.buyertool.activity.Share2WPActivity;
import com.nahuo.buyertool.activity.ShopDetailsActivity;
import com.nahuo.buyertool.common.Const;
import com.nahuo.buyertool.common.ListUtils;
import com.nahuo.buyertool.common.Utils;
import com.nahuo.buyertool.exceptions.CatchedException;
import com.nahuo.buyertool.model.Share2WPItem;
import com.nahuo.buyertool.model.ShopItemListModelX;
import com.nahuo.buyertool.task.AgentShopTask;
import com.nahuo.library.helper.ImageUrlExtends;
import com.squareup.picasso.Picasso;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.List;
/*
                "ID": 962801,
                        "ItemID": 1098862,
                        "Name": "Lilifeiyang0705内网测试第7款",
                        "ShopID": 3636,
                        "Intro": "Lilifeiyang0705内网测试第7款",
                        "CreateDate": "2017-07-05 13:59:14",
                        "Cover": "upyun:nahuo-img-server://3636/item/1499234352.jpg",
                        "UserID": 129766,
                        "UserName": "lilifeiyang",
                        "UserLogo": "http://img4.nahuo.com/u129766/shop/u_40130377806397289689.jpg!thum.140",
                        "RetailPrice": "7.00",
                        "Price": "7.00",
                        "IsSource": true,
                        "Images": [
                        "upyun:nahuo-img-server://3636/item/1499234352.jpg",
                        "upyun:nahuo-img-server://3636/item/1499234353.jpg"
                        ],
                        "MyID": -1,
                        "MyPrice": "-1.00",
                        "MyRetailPrice": "-1.00",
                        "ApplyStatuID": 3,
                        "MyStatuID": -1,
                        "IsCopy": true,
                        "IsHide": false,
                        "WaitDays": 0*/

/**
 * @author JorsonWong
 * @description
 * @created 2015年5月26日 下午1:33:56
 */
public class AllItemGridAdapter extends MyBaseAdapter<ShopItemListModelX> {

    private boolean mShowEdit;
    Activity activity;
    int height;
    public AllItemGridAdapter(FragmentActivity context, int height) {
        super(context);
        mContext = context;
        this.height=height;
        this.activity = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.lvitem_all_items_grid, parent, false);
            holder = new ViewHolder();
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_cover);
            holder.tvPrice = (TextView) convertView.findViewById(R.id.tv_price);
            holder.ivUp = (TextView) convertView.findViewById(R.id.btn_up);
            holder.tvText = (TextView) convertView.findViewById(R.id.tv_text);
            holder.tv_forword = (TextView) convertView.findViewById(R.id.tv_forword);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
            convertView.setLayoutParams(params);
            convertView.setTag(holder);
        } else {
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
//            convertView.setLayoutParams(params);
            holder = (ViewHolder) convertView.getTag();
        }
        // Log.i("AllItemGridAdapter", "position :" + position + " getCount():" + getCount());
        if (position >= getCount()) {
            CrashReport.postCatchedException(new CatchedException("AllItemGridAdapter position > getCount()  position:"
                    + position + "  getCount():" + getCount()));
            holder.tvPrice.setText("");
            holder.tvText.setText("异常数据");
            holder.ivUp.setVisibility(View.GONE);
            holder.ivIcon.setImageResource(R.drawable.empty_photo);
            return convertView;
        }
        final ShopItemListModelX listItem = mdata.get(position);
        if (listItem.isCheck()) {
            holder.checkbox.setChecked(true);
        } else {
            holder.checkbox.setChecked(false);
        }

        String cover = listItem.getCover();
        if (!TextUtils.isEmpty(listItem.getName())){
            holder.tv_name.setText(listItem.getName());
        }else {
            holder.tv_name.setText("");
        }

        if (!TextUtils.isEmpty(cover)) {
//            RequestOptions options = new RequestOptions()
//                    .centerCrop()
//                    .placeholder(R.drawable.empty_photo)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL);
//            Glide.with(mContext)
//                    .load(ImageUrlExtends.getImageUrl(cover, 15))
//                    .apply(options)
//                    .into(holder.ivIcon);
            Picasso.with(mContext).load(ImageUrlExtends.getImageUrl(cover, 15)).placeholder(R.drawable.empty_photo).into(holder.ivIcon);
        } else {
            holder.ivIcon.setImageResource(R.drawable.empty_photo);
        }
        double supplyPrice = listItem.getPrice();
        // double retailPrice = listItem.getRetailPrice();
//        int applyStatuId = listItem.getApplyStatuID();
//        int myStatuId = listItem.getMyStatuID();

//        if (applyStatuId == UpYunConst.ApplyAgentStatu.ACCEPT) {// 已代理
//            holder.tvPrice.setText("¥ " + Utils.moneyFormat(supplyPrice));
//            holder.tvText.setText("供货价");
//        } else {// 未代理
//            holder.tvPrice.setText("");
//            holder.tvText.setText("已隐藏供货价");
//        }
        holder.tvPrice.setText("¥ " + Utils.moneyFormat(supplyPrice));
        holder.ivUp.setVisibility(View.GONE);
        if (listItem.IsCopy) {
            holder.tv_forword.setVisibility(View.VISIBLE);
        } else {
            holder.tv_forword.setVisibility(View.GONE);
        }
        final ShopItemListModelX mode = listItem;
        holder.ivUp.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Share2WPActivity.class);
                final Share2WPItem shareItem = new Share2WPItem(mode.getID() + "", mode.getUserid() + "",
                        mode.getName(), mode.getPrice() + "", mode.getRetailPrice() + "");
                shareItem.setIntro(mode.getIntroOrName());
                shareItem.imgUrls = mode.getImages();
                intent.putExtra(Share2WPActivity.EXTRA_SHARE_ITEM, shareItem);
                mContext.startActivity(intent);
            }
        });
        holder.checkbox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listItem.setCheck(!listItem.isCheck());
//                for (int i = 0; i < mdata.size(); i++) {
//                    if (i == position) {
//                        if (mdata.get(i).isCheck()) {
//                            mdata.get(i).setCheck(false);
//                        } else {
//                            mdata.get(i).setCheck(true);
//                        }
//                        break;
//                    }
//                }
                notifyDataSetChanged();
            }
        });
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ShopDetailsActivity.class);
                intent.putExtra(ShopDetailsActivity.EXTRA_ID, listItem.getID());
                intent.putExtra(ShopDetailsActivity.EXTRA_NAME, listItem.getName());
                mContext.startActivity(intent);
            }
        });

        return convertView;

    }

    List<Boolean> if_checks = new ArrayList<>();

    public void setAllCheck() {
        if_checks.clear();
        if (!ListUtils.isEmpty(mdata)) {
            for (ShopItemListModelX bean : mdata) {
                if_checks.add(bean.isCheck());
            }
            if (if_checks.contains(false)) {
                for (ShopItemListModelX bean : mdata) {
                    bean.setCheck(true);
                }
            } else {
                for (ShopItemListModelX bean : mdata) {
                    bean.setCheck(false);
                }
            }
            notifyDataSetChanged();
        }
    }

    List<ShopItemListModelX> slist = new ArrayList<>();

    public List<ShopItemListModelX> getBatchShelvesCheck() {
        slist.clear();
        if (!ListUtils.isEmpty(mdata)) {
            for (ShopItemListModelX bean : mdata) {
                if (bean.isCheck()) {
                    slist.add(bean);
                }
            }
        }
        return slist;
    }

    public void setHasCopy(List<ShopItemListModelX> list) {
        if (!ListUtils.isEmpty(list)) {
            for (int i = 0; i < mdata.size(); i++) {
                for (int j = 0; j < list.size(); j++) {
                    if (list.get(j).getID() == mdata.get(i).getID()) {
                        mdata.get(i).setCheck(false);
                        mdata.get(i).IsCopy = true;
                    }
                }
            }
        }
        notifyDataSetChanged();
    }
    public void setNoCheck(List<ShopItemListModelX> list) {
        if (!ListUtils.isEmpty(list)) {
            for (int i = 0; i < mdata.size(); i++) {
                for (int j = 0; j < list.size(); j++) {
                    if (list.get(j).getID() == mdata.get(i).getID()) {
                        mdata.get(i).setCheck(false);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }
    private static class ViewHolder {
        private TextView tvPrice, tvText, ivUp, tv_forword, tv_name;
        private ImageView ivIcon;
        private CheckBox checkbox;
    }

    public void remove(int id) {
        for (ShopItemListModelX item : mdata) {
            if (item.getID() == id) {
                mdata.remove(item);
                notifyDataSetChanged();
                break;
            }
        }
    }

    private void shareToWp(final ShopItemListModelX item) {
        if (mShowEdit && item.IsCopy)
            return;

        int applyId = item.getApplyStatuID();
        switch (applyId) {
            case Const.ApplyAgentStatu.NOT_APPLY:// 关注：0
                String msg1 = "您必须先申请成为该供货商的代理，才能转发商品到我的微铺，是否立即申请？";
                showAgentDialog(item, msg1);
                break;
            case Const.ApplyAgentStatu.REJECT:// 拒绝：2
                String msg2 = "您申请成为该供货商的代理被拒绝，是否重新申请？申请通过之后才能转发商品到我的微铺";
                showAgentDialog(item, msg2);
                break;
            case Const.ApplyAgentStatu.APPLYING:// 申请中：1
                ViewHub.showShortToast(mContext, "您代理申请供货商还在审核中，审核期间商品不能转到微铺");
                break;
            case Const.ApplyAgentStatu.ACCEPT:// 接受：3
//                if (SpManager.getShowPreShareItem(mContext)) {
//                    PreAgentItemActivity.toPreAgentItemActivity(mContext, item);
//                } else {
//                    Intent intent = new Intent(mContext, Share2WPActivity.class);
//                    final Share2WPItem shareItem = new Share2WPItem(item.getID() + "", item.getUserid() + "",
//                            item.getName(), item.getPrice() + "", item.getRetailPrice() + "");
//                    shareItem.setIntro(item.getIntroOrName());
//                    shareItem.imgUrls = item.getImages();
//                    intent.putExtra(Share2WPActivity.EXTRA_SHARE_ITEM, shareItem);
//                    mContext.startActivity(intent);
//                }
                break;
        }

    }

    /**
     * @description 申请代理弹窗
     * @created 2015-1-29 上午11:58:51
     * @author ZZB
     */
    private void showAgentDialog(final ShopItemListModelX item, String msg) {
        ViewHub.showOkDialog(mContext, "提示", msg, "申请代理", "放弃", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AgentShopTask task = new AgentShopTask(mContext, item.getUserid(), item.getApplyStatuID());
                task.setCallback(new AgentShopTask.Callback() {
                    @Override
                    public void onAgentFinished() {
                        for (ShopItemListModelX tmpItem : mdata) {
                            if (tmpItem.getUserid() == item.getUserid()) {
                                tmpItem.setApplyStatuID(1);// applyStatuId设置为"申请中"
                            }
                        }
                    }
                });
                task.execute();
            }
        });
    }

    private void edit(ShopItemListModelX item) {
//        final double myPrice = item.getMyPrice();
//        final String title = item.getName();
//        final String intro = item.getIntroOrName();
//        final String upSupplyPrice = item.getPrice() + "";
//        final String upRetailPrice = item.getRetailPrice() + "";
//
//        UpdateItem updateItem = new UpdateItem(title, item.getMyID(), upSupplyPrice, upRetailPrice);
//        updateItem.setIntro(intro);
//        updateItem.mGroupIds = item.getGroupIdsFromGroups();
//        updateItem.mGroupNames = item.getGroupNamesFromGroups();
//        updateItem.agentPrice = myPrice + "";
//        updateItem.isOnly4Agent = item.isOnly4Agent();
//        Intent intent = new Intent(mContext, UpdateSharedItemActivity.class);
//        updateItem.myItemId = item.getMyID();
//        intent.putExtra(UpdateSharedItemActivity.EXTRA_UPDATE_ITEM, updateItem);
//        mContext.startActivity(intent);
    }

}
