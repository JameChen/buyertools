package com.nahuo.buyertool.adapter;


import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.CommonListActivity;
import com.nahuo.buyertool.ViewHub;
import com.nahuo.buyertool.common.ListUtils;
import com.nahuo.buyertool.model.ShopItemListModel;
import com.nahuo.library.controls.pulltorefresh.PullToRefreshListView;
import com.nahuo.library.helper.ImageUrlExtends;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

public class CommonListAdapter extends BaseAdapter {
    private static final String TAG = CommonListAdapter.class.getSimpleName();
    public Context mContext;
    public List<ShopItemListModel> mList;
    private int mGridViewWidth;
    private CommonListActivity.ListType mType;
    private CommonListActivity mActivity;

    // 构造函数
    public CommonListAdapter(Context Context, List<ShopItemListModel> dataList, CommonListActivity.ListType type) {
        mContext = Context;
        mList = dataList;
        mType = type;
        this.mActivity = (CommonListActivity) Context;//强转成对应的activity,便于获取数据
    }
    public void removeItem(List<ShopItemListModel>  list){
        try {
            if (!ListUtils.isEmpty(mList)&&!ListUtils.isEmpty(list)){
                for (int i=0;i<mList.size();i++) {
                    for (int j=0;j<list.size();j++) {
                        if (mList.get(i).getID()==list.get(j).getID()){
                            mList.remove(i);
                            i--;
                        }
                    }
                }
                notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
            ViewHub.showShortToast(mContext,e.getMessage());
        }
    }
    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public ShopItemListModel getItem(int arg0) {
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
                        R.layout.item_common_list, arg2, false);
                holder = new ViewHolder();

                holder.title = (TextView) view
                        .findViewById(R.id.item_title);
                holder.cover = (ImageView) view
                        .findViewById(R.id.item_cover);
                holder.text1 = (TextView) view
                        .findViewById(R.id.item_text_1);
                holder.text2 = (TextView) view
                        .findViewById(R.id.item_text_2);
                holder.text3 = (TextView) view
                        .findViewById(R.id.item_text_3);
                holder.text4 = (TextView) view
                        .findViewById(R.id.item_text_4);
                holder.mCb = (CheckBox) view.findViewById(R.id.cb);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            ShopItemListModel item = mList.get(arg0);

            String url = ImageUrlExtends.getImageUrl(item.getCover(), 10);
            Picasso.with(mContext).load(url).placeholder(R.drawable.empty_photo).into(holder.cover);
            String title = "", add_price = "";
            if (item.isIsWarnTag()) {
                title = "<b><font color=\"#FF0000\">" + item.getNameTag() + "</font></b>";
            } else {
                title = "<b><font color=\"#150ff4\">" + item.getNameTag() + "</font></b>";
            }

            if (item.getMarkUpValue() > 0) {
                add_price = "<font color=\"#FF0000\">(加价：¥" + item.getMarkUpValue() + ")</font>";
            } else {
                add_price="";
            }
            holder.title.setText(Html.fromHtml(title + "<font color=\"#586B95\">" + item.getIntroOrName() + "</font>"));


            switch (mType) {
                case 待开单:
                    holder.text1.setText(Html.fromHtml("进价：<font color=\"#FF0000\">¥</font>" + item.getOriPrice()+add_price + "&nbsp;&nbsp;&nbsp;&nbsp;总进价：<font color=\"#FF0000\">¥</font>" + (new DecimalFormat("##0.00").format(item.getPayQty() * item.getOriPrice()))));
                    holder.text2.setText(Html.fromHtml("已拼" + item.getPayQty() + "件&nbsp;&nbsp;&nbsp;&nbsp;售价：<font color=\"#FF0000\">¥</font>" + item.getPrice()));
                    holder.text3.setText(item.getQsName());
                    holder.text4.setVisibility(View.GONE);
                    if (mActivity.isEdit() ) {//编辑状态,并且是未关联采购单
                        holder.mCb.setVisibility(View.VISIBLE);
                        holder.mCb.setChecked(mActivity.findId(item.getID()) >= 0);
                    } else {
                        holder.mCb.setVisibility(View.GONE);
                    }
                    break;
                case 开或欠单:
                    if (item.getStatusID()==1){
                        holder.text1.setText(Html.fromHtml("进价：<font color=\"#FF0000\">¥</font>" + item.getOriPrice()+add_price + "&nbsp;&nbsp;&nbsp;&nbsp;总进价：<font color=\"#FF0000\">¥</font>" + (new DecimalFormat("##0.00").format(item.getBillingQty() * item.getOriPrice()))));
                        holder.text2.setText(Html.fromHtml("开单" + item.getBillingQty() + "件&nbsp;&nbsp;&nbsp;&nbsp;售价：<font color=\"#FF0000\">¥</font>" + item.getPrice()));

                            if (item.getPurchaseStatus()) {
                                holder.text3.setText("开单时间：" + item.getCreateTime());
                            } else {
                                holder.text3.setText(Html.fromHtml("<font color=\"#FF0000\">无采购单</font>" + "  开单时间：" + item.getCreateTime()));
                            }
                        //编辑和完成
                        if (mActivity.isEdit() && !item.getPurchaseStatus()) {//编辑状态,并且是未关联采购单
                            holder.mCb.setVisibility(View.VISIBLE);
                            holder.mCb.setChecked(mActivity.findId(item.getID()) >= 0);
                        } else {
                            holder.mCb.setVisibility(View.GONE);
                        }
                        holder.text4.setText(item.getQsName());
                    }else if (item.getStatusID()==2){
                        //欠货
                        holder.text1.setText(Html.fromHtml("进价：<font color=\"#FF0000\">¥</font>" + item.getOriPrice()+add_price + "&nbsp;&nbsp;&nbsp;&nbsp;总进价：<font color=\"#FF0000\">¥</font>" + (new DecimalFormat("##0.00").format(item.getOweQty() * item.getOriPrice()))));
                        holder.text2.setText(Html.fromHtml("欠货" + item.getOweQty() + "件&nbsp;&nbsp;&nbsp;&nbsp;售价：<font color=\"#FF0000\">¥</font>" + item.getPrice()));
                        holder.text3.setText("欠货：" + item.getOweDays() + "天");
                        holder.text4.setText(item.getQsName());
                        if (mActivity.isEdit() ) {//编辑状态,并且是未关联采购单
                            holder.mCb.setVisibility(View.VISIBLE);
                            holder.mCb.setChecked(mActivity.findId(item.getID()) >= 0);
                        } else {
                            holder.mCb.setVisibility(View.GONE);
                        }
                    }
                    break;
                case 异常单:
                case 已开单:
                    holder.text1.setText(Html.fromHtml("进价：<font color=\"#FF0000\">¥</font>" + item.getOriPrice()+add_price + "&nbsp;&nbsp;&nbsp;&nbsp;总进价：<font color=\"#FF0000\">¥</font>" + (new DecimalFormat("##0.00").format(item.getBillingQty() * item.getOriPrice()))));
                    holder.text2.setText(Html.fromHtml("开单" + item.getBillingQty() + "件&nbsp;&nbsp;&nbsp;&nbsp;售价：<font color=\"#FF0000\">¥</font>" + item.getPrice()));

                    if (item.getStatusID() == 2) {
                        if (item.getPurchaseStatus()) {//已关联(进一步判断是否无采购单)
                            holder.text3.setText(Html.fromHtml("开单时间：" + item.getCreateTime() + "  <font color=\"#FF0000\">(欠货单)</font>"));
                        } else {
                            holder.text3.setText(Html.fromHtml("<font color=\"#FF0000\">无采购单</font>" + "  开单时间：" + item.getCreateTime() + "  <font color=\"#FF0000\">(欠货单)</font>"));
                        }
                    } else {
                        if (item.getPurchaseStatus()) {
                            holder.text3.setText("开单时间：" + item.getCreateTime());
                        } else {
                            holder.text3.setText(Html.fromHtml("<font color=\"#FF0000\">无采购单</font>" + "  开单时间：" + item.getCreateTime()));
                        }
                    }
                    //编辑和完成
                    if (mActivity.isEdit() && !item.getPurchaseStatus()) {//编辑状态,并且是未关联采购单
                        holder.mCb.setVisibility(View.VISIBLE);
                        holder.mCb.setChecked(mActivity.findId(item.getID()) >= 0);
                    } else {
                        holder.mCb.setVisibility(View.GONE);
                    }
                    holder.text4.setText(item.getQsName());
                    break;
                case 入库单:

                    holder.text1.setText(Html.fromHtml("进价：<font color=\"#FF0000\">¥</font>" + item.getOriPrice() +add_price+ "&nbsp;&nbsp;&nbsp;&nbsp;售价：<font color=\"#FF0000\">¥</font>" + item.getPrice()));

                    //     holder.text1.setText("入库"+item.getInvInCount()+"件");
                    holder.text2.setText(Html.fromHtml("入库：" + item.getInvInCount() + "&nbsp;件&nbsp;&nbsp;&nbsp;入库仓位：" + item.getWarehouse()));
                    //holder.text2.setText("入库仓位："+item.getWarehouse());
                    if (item.getPurchaseStatus()) {
                        holder.text3.setText("入库时间：" + item.getCreateTime());
                    } else {
                        holder.text3.setText(Html.fromHtml("<font color=\"#FF0000\">无采购单</font>" + "  入库时间：" + item.getCreateTime()));
                    }
                    holder.text4.setText(item.getQsName());
                    //编辑和完成
                    if (mActivity.isEdit() && !item.getPurchaseStatus()) {//编辑状态,并且是未关联采购单
                        holder.mCb.setVisibility(View.VISIBLE);
                        holder.mCb.setChecked(mActivity.findId(item.getID()) >= 0);
                    } else {
                        holder.mCb.setVisibility(View.GONE);
                    }
                    holder.text4.setText(item.getQsName());
                    break;
                case 欠货单:
                    holder.text1.setText(Html.fromHtml("进价：<font color=\"#FF0000\">¥</font>" + item.getOriPrice()+add_price + "&nbsp;&nbsp;&nbsp;&nbsp;总进价：<font color=\"#FF0000\">¥</font>" + (new DecimalFormat("##0.00").format(item.getOweQty() * item.getOriPrice()))));
                    holder.text2.setText(Html.fromHtml("欠货" + item.getOweQty() + "件&nbsp;&nbsp;&nbsp;&nbsp;售价：<font color=\"#FF0000\">¥</font>" + item.getPrice()));
                    holder.text3.setText("欠货：" + item.getOweDays() + "天");
                    holder.text4.setText(item.getQsName());
                    if (mActivity.isEdit() ) {//编辑状态,并且是未关联采购单
                        holder.mCb.setVisibility(View.VISIBLE);
                        holder.mCb.setChecked(mActivity.findId(item.getID()) >= 0);
                    } else {
                        holder.mCb.setVisibility(View.GONE);
                    }
                    break;
                case 退款单:
                    holder.text1.setText(Html.fromHtml("进价：<font color=\"#FF0000\">¥</font>" + item.getOriPrice() +add_price+ "&nbsp;&nbsp;&nbsp;&nbsp;总进价：<font color=\"#FF0000\">¥</font>" + (new DecimalFormat("##0.00").format(item.getRefundQty() * item.getOriPrice()))));
                    holder.text2.setText(Html.fromHtml("退款" + item.getRefundQty() + "件&nbsp;&nbsp;&nbsp;&nbsp;售价：<font color=\"#FF0000\">¥</font>" + item.getPrice()));
                    holder.text3.setText("退款时间：" + item.getRefundTime());
                    holder.text4.setText(item.getQsName());
                    break;
                default:
                    break;
            }
        }

        return view;
    }

    public class ViewHolder {
        public int position;
        ImageView cover;
        TextView title;
        TextView text1;
        TextView text2;
        TextView text3;
        TextView text4;
        CheckBox mCb;

    }

    /**
     * ===============BuyerTool====================
     * author：ChenZhen
     * Email：18620156376@163.com
     * Time : 2016/7/6 18:07
     * Description : listview单项刷新
     * ===============BuyerTool====================
     */
    public void updateView(int itemIndex) {
        //得到第一个可显示控件的位置，
        PullToRefreshListView mListView = mActivity.getRefreshListView();
        int visiblePosition = mListView.getFirstVisiblePosition();
        //只有当要更新的view在可见的位置时才更新，不可见时，跳过不更新
        if (itemIndex - visiblePosition >= 0) {
            //得到要更新的item的view
            View view = mListView.getChildAt(itemIndex - visiblePosition);
            //调用adapter更新界面
            CheckBox cb = (CheckBox) view.findViewById(R.id.cb);
            cb.setChecked(!cb.isChecked());//反转checkbox的状态
        }
    }

}
