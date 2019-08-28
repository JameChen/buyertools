package com.nahuo.buyertool.adapter;

import android.app.Service;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.ViewHub;
import com.nahuo.buyertool.common.Const;
import com.nahuo.buyertool.model.ShopItemListModelX;
import com.nahuo.buyertool.model.UpdateItem;
import com.nahuo.buyertool.provider.ItemInfoProvider;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
public class ShopItemAdapter extends MyBaseAdapter<ShopItemListModelX> implements OnClickListener {

    private static int MAX_LINE;
    private static final String TAG_EXPAND = "全文";
    private static final String TAG_COLLAPSE = "收起";
    private ShopItemAdapter vThis = this;
    public FragmentActivity mContext;
    private Resources mResources;
    private DecimalFormat mPriceFormat = new DecimalFormat("#0.00");
    // private Share2WPDialogFragment mShare2WPDialogFragment;
    private String strTodayTimeStr;
    private String strYesterday;
    private Html.ImageGetter imageGetter;
    private RelativeSizeSpan mSizeHalf = new RelativeSizeSpan(0.5f);
    private int mGridViewWidth;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private Calendar mCalendar = Calendar.getInstance();
    private Listener mListener;
    private OnBuyClickListener mOnBuyClickListener;

    public static interface Listener {
        /**
         * 代理申请状态修改
         */
        public void onApplyStatuChanged(int applyStatuId);
    }

    // 构造函数
    public ShopItemAdapter(FragmentActivity context) {
        super(context);
        mContext = context;
        mResources = context.getResources();
        MAX_LINE = mResources.getInteger(R.integer.content_max_line);
        imageGetter = new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                try {
                    Drawable drawable = null;
                    int rId = Integer.parseInt(source);
                    drawable = mResources.getDrawable(rId);
                    drawable.setBounds(0, 0, Const.getQQFaceWidth(mContext), Const.getQQFaceWidth(mContext));
                    return drawable;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return null;
                }
            }
        };
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH) + 1;
        int date = c.get(Calendar.DATE);
        // 得出今天的时间线显示的值
        strTodayTimeStr = month + "@" + date;
        strYesterday = month + "@" + (date - 1);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.lvitem_shop_item, parent, false);
            holder = new ViewHolder();
            holder.btnEdit = convertView.findViewById(R.id.tv_edit);
            holder.btnShareToWp = (TextView) convertView.findViewById(R.id.btn_share_item);
            holder.btnShareToWp.setOnClickListener(this);
            holder.tvShareToWp = (ImageView) convertView.findViewById(R.id.tv_agent);
            holder.tvShareToWp.setOnClickListener(this);
            holder.ivBuy = (ImageView) convertView.findViewById(R.id.iv_buy);
            holder.ivBuy.setOnClickListener(this);

            holder.tvNotShare = (TextView) convertView.findViewById(R.id.tv_not_share);
            holder.tvShareCount = (TextView) convertView.findViewById(R.id.tv_share_count);
            holder.tvShareCount.setOnClickListener(this);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
            holder.gvPics = (GridView) convertView.findViewById(R.id.gv_pics);
            holder.tvSupplyPrice = (TextView) convertView.findViewById(R.id.tv_supply_price);
            holder.tvSupplyRetailPrice = (TextView) convertView.findViewById(R.id.tv_supply_retail_price);
            holder.tvAgentPrice = (TextView) convertView.findViewById(R.id.tv_agent_price);
            holder.tvRetailPrice = (TextView) convertView.findViewById(R.id.tv_retail_price);
            holder.tvDate = (TextView) convertView.findViewById(R.id.tv_date);
            holder.btnPopupMenu = (ImageButton) convertView.findViewById(R.id.btn_popup);
            holder.tvExpand = (TextView) convertView.findViewById(R.id.tv_expand_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ShopItemListModelX item = mdata.get(position);
        int applyStatuId = item.getApplyStatuID();
        int myStatuId = item.getMyStatuID();
        double upRetailPrice = item.getRetailPrice();// 上家零售
        double upSupplyPrice = item.getPrice();// 上家供货价
        double myRetailPrice = item.getMyRetailPrice();// 我的零售价
        double mySupplyPrice = item.getMyPrice();// 我给下家
        String intro = item.getIntroOrName();
        Spanned introHtml = ShopItemListModelX.getTextHtml(intro, mContext, imageGetter);
        String timer = item.getCreateDate();
        Date date;
        try {
            date = mDateFormat.parse(timer);
            mCalendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String[] picUrls = item.getImages();
        holder.btnEdit.setVisibility(View.GONE);
        holder.btnEdit.setOnClickListener(this);
        if (Const.ApplyAgentStatu.ACCEPT == applyStatuId) {// 已代理
            if (myStatuId == -1 || myStatuId == 2 || myStatuId == 3) {// 已代理，未转发
                if (item.IsCopy) {
                    holder.btnShareToWp.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    holder.btnShareToWp.setText("已转发");
                } else {
                    holder.btnShareToWp.setText("");
                    holder.btnShareToWp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_up_item, 0, 0, 0);
                }
                holder.btnShareToWp.setVisibility(View.VISIBLE);

                holder.tvAgentPrice.setVisibility(View.GONE);
                holder.tvRetailPrice.setVisibility(View.GONE);
                holder.tvNotShare.setVisibility(View.GONE);
                holder.tvNotShare.setText("未转发");
                setPrice(holder, holder.tvSupplyPrice.getId(), upSupplyPrice);
                setPrice(holder, holder.tvSupplyRetailPrice.getId(), upRetailPrice);
            } else {// 已代理，已转发
                holder.btnEdit.setVisibility(View.VISIBLE);
                holder.tvShareToWp.setVisibility(View.GONE);
                holder.btnShareToWp.setVisibility(View.GONE);
                setPrice(holder, holder.tvAgentPrice.getId(), mySupplyPrice);
                setPrice(holder, holder.tvRetailPrice.getId(), myRetailPrice);
                holder.tvNotShare.setVisibility(View.GONE);
                setPrice(holder, holder.tvSupplyPrice.getId(), upSupplyPrice);
                setPrice(holder, holder.tvSupplyRetailPrice.getId(), upRetailPrice);
            }

        } else {// 未代理
            holder.btnShareToWp.setVisibility(View.VISIBLE);
            holder.btnShareToWp.setText("");
            holder.btnShareToWp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_up_item, 0, 0, 0);
            holder.tvAgentPrice.setVisibility(View.GONE);
            holder.tvRetailPrice.setVisibility(View.GONE);
            holder.tvNotShare.setVisibility(View.GONE);
            holder.tvSupplyPrice.setVisibility(View.GONE);
            setPrice(holder, holder.tvSupplyRetailPrice.getId(), upRetailPrice);
        }

        // if (applyStatuId == ApplyAgentStatu.NOT_APPLY || applyStatuId == ApplyAgentStatu.APPLYING
        // || applyStatuId == ApplyAgentStatu.REJECT) {// 未代理
        // // holder.tvShareToWp.setVisibility(View.VISIBLE);
        // holder.btnShareToWp.setVisibility(View.VISIBLE);
        // holder.tvAgentPrice.setVisibility(View.GONE);
        // holder.tvRetailPrice.setVisibility(View.GONE);
        // holder.tvNotShare.setVisibility(View.GONE);
        // holder.tvSupplyPrice.setVisibility(View.GONE);
        // setPrice(holder, holder.tvSupplyRetailPrice.getId(), upRetailPrice);
        // } else if (myStatuId == -1 || myStatuId == 2 || myStatuId == 3) {// 已代理，未转发
        // // holder.tvShareToWp.setVisibility(View.VISIBLE);
        // holder.btnShareToWp.setVisibility(View.VISIBLE);
        // holder.tvAgentPrice.setVisibility(View.GONE);
        // holder.tvRetailPrice.setVisibility(View.GONE);
        // holder.tvNotShare.setVisibility(View.VISIBLE);
        // holder.tvNotShare.setText("未转发");
        // setPrice(holder, holder.tvSupplyPrice.getId(), upSupplyPrice);
        // setPrice(holder, holder.tvSupplyRetailPrice.getId(), upRetailPrice);
        // } else {// 已代理，已转发
        // holder.btnEdit.setVisibility(View.VISIBLE);
        // holder.tvShareToWp.setVisibility(View.GONE);
        // holder.btnShareToWp.setVisibility(View.GONE);
        // setPrice(holder, holder.tvAgentPrice.getId(), mySupplyPrice);
        // setPrice(holder, holder.tvRetailPrice.getId(), myRetailPrice);
        // holder.tvNotShare.setVisibility(View.GONE);
        // setPrice(holder, holder.tvSupplyPrice.getId(), upSupplyPrice);
        // setPrice(holder, holder.tvSupplyRetailPrice.getId(), upRetailPrice);
        // }
        holder.tvContent.setText(introHtml);
        holder.tvContent.setTag(intro);
        holder.tvContent.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                Vibrator vib = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
                vib.vibrate(50);
                TextView copyingTextView = (TextView) v;
                copyingTextView.setBackgroundResource(R.color.lightgray);
                ViewHub.showCopyView(mContext, v, v.getTag().toString(), true);
                return false;
            }
        });
        holder.tvContent.post(new Runnable() {
            @Override
            public void run() {
                int lineCount = holder.tvContent.getLineCount();
                boolean show = lineCount > MAX_LINE;
                holder.tvExpand.setVisibility(show ? View.VISIBLE : View.GONE);
                holder.tvExpand.setText(TAG_EXPAND);
                holder.tvContent.setMaxLines(MAX_LINE);
            }
        });
        holder.tvExpand.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = (String) holder.tvExpand.getText().toString();
                boolean expand = tag.equals(TAG_EXPAND);
                if (expand) {
                    holder.tvContent.setMaxLines(100);
                    holder.tvExpand.setText(TAG_COLLAPSE);
                } else {
                    holder.tvContent.setMaxLines(MAX_LINE);
                    holder.tvExpand.setText(TAG_EXPAND);
                }

            }
        });
        populateGridView(holder.gvPics, picUrls);
        holder.btnPopupMenu.setOnClickListener(this);
        String day = mCalendar.get(Calendar.DATE) + "";
        String month = (mCalendar.get(Calendar.MONTH) + 1) + "";
        String tmpDate = month + "@" + day;
        if (tmpDate.equals(strTodayTimeStr)) {// 今天
            holder.tvDate.setText("今天");
        } else if (tmpDate.equals(strYesterday)) {
            holder.tvDate.setText("昨天");
        } else {
            String dateMonth = day + " " + month + "月";
            Spannable span = new SpannableString(dateMonth);
            span.setSpan(mSizeHalf, day.length() + 1, dateMonth.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.tvDate.setText(span);
        }
        holder.tvShareCount.setText(ItemInfoProvider.getShareCount(mContext, item.getID()));
        holder.btnEdit.setTag(R.id.Tag_Position, position);
        holder.btnPopupMenu.setTag(R.id.Tag_Position, position);
        holder.tvShareCount.setTag(R.id.Tag_Position, position);
        holder.btnShareToWp.setTag(R.id.Tag_Position, position);
        holder.tvContent.setTag(R.id.Tag_Position, position);
        holder.tvShareToWp.setTag(R.id.Tag_Position, position);
        holder.ivBuy.setTag(R.id.Tag_Position, position);

        holder.tvContent.setOnClickListener(this);
        holder.gvPics.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                onItemDetailClick(position);
            }
        });

        return convertView;
    }

    private void populateGridView(final GridView gridView, final String[] urls) {
        final PicGridViewAdapter gridAdapter = new PicGridViewAdapter(mContext);
        gridView.setAdapter(gridAdapter);
        gridView.setNumColumns(urls.length < 3 ? urls.length : 3);
        if (mGridViewWidth == 0) {
            gridView.post(new Runnable() {
                @Override
                public void run() {
                    mGridViewWidth = gridView.getMeasuredWidth();
                    gridAdapter.setGridViewWidth(gridView.getMeasuredWidth());
                    gridAdapter.setData(urls);
                    gridAdapter.notifyDataSetChanged();
                }
            });
        } else {
            gridAdapter.setGridViewWidth(mGridViewWidth);
            gridAdapter.setData(urls);
            gridAdapter.notifyDataSetChanged();
        }
    }

    private void showPopUp(View v) {

//        final int pos = (Integer) v.getTag(R.id.Tag_Position);
//        final ShopItemListModelX item = mdata.get(pos);
//        final int agentApplyId = item.getApplyStatuID();
//        final int myStatuId = item.getMyStatuID();
//
//        // 分享
//        ShareMenu menu = new ShareMenu((Activity) mContext);
//        menu.addMenuItem(new ShareMenuItem("分享"));
//        if (agentApplyId == UpYunConst.ApplyAgentStatu.ACCEPT && myStatuId == 1) {
//            menu.addMenuItem(new ShareMenuItem("修改信息"));
//        }
//        menu.setMenuItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                switch (position) {
//                    case 0:
//                        shareToWx(item, false);
//                        break;
//                    case 1:
//                        shareToWp(item);
//                        break;
//                }
//
//            }
//        });
//        menu.show(v);

    }

//    private void shareToWp(final ShopItemListModelx item) {
//        final int applyId = item.getApplyStatuID();
//        final double myPrice = item.getMyPrice();
//        final int myStatuId = item.getMyStatuID();
//        final String title = item.getName();
//        final String intro = item.getIntroOrName();
//        String id = item.getID() + "";
//        final String upSupplyPrice = item.getPrice() + "";
//        final String upRetailPrice = item.getRetailPrice() + "";
//        String userid = item.getUserid() + "";
//        if (applyId == ApplyAgentStatu.NOT_APPLY || applyId == ApplyAgentStatu.REJECT) {
//            // ViewHub.showShortToast(mContext, "您还不是该供货商的代理，请先申请成为代理商");
//            ViewHub.showOkDialog(mContext, "提示", "您必须先申请成为该供货商的代理，才能转发商品到我的微铺，是否立即申请？", "申请代理", "放弃",
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            AgentShopTask task = new AgentShopTask(mContext, item.getUserid(), applyId);
//                            task.setCallback(new AgentShopTask.Callback() {
//                                @Override
//                                public void onAgentFinished() {
//                                    for (ShopItemListModelx tmpItem : mdata) {
//                                        tmpItem.setApplyStatuID(UpYunConst.ApplyAgentStatu.APPLYING);// applyStatuId设置为"申请中"
//                                        if (mListener != null) {
//                                            mListener.onApplyStatuChanged(UpYunConst.ApplyAgentStatu.APPLYING);
//                                        }
//                                    }
//                                }
//                            });
//                            task.execute();
//                        }
//                    });
//        } else if (applyId == ApplyAgentStatu.APPLYING) {
//            ViewHub.showShortToast(mContext, "供货商还未审核通过您的代理申请，请耐心等候");
//        } else if (myStatuId == -1 || myStatuId == 2 || myStatuId == 3) {
//
////            if (SpManager.getShowPreShareItem(mContext)) {
////                PreAgentItemActivity.toPreAgentItemActivity(mContext, item);
////            } else {
////                Intent intent = new Intent(mContext, Share2WPActivity.class);
////                Share2WPItem shareItem = new Share2WPItem(id, userid, title, upSupplyPrice, upRetailPrice);
////                shareItem.setIntro(intro);
////                shareItem.imgUrls = item.getImages();
////                intent.putExtra(Share2WPActivity.EXTRA_SHARE_ITEM, shareItem);
////                mContext.startActivityForResult(intent, ShopItemsActivity.REQUEST_SHARE_TO_WP);
////            }
//
//            //
//        } else {
////            UpdateItem updateItem = new UpdateItem(title, item.getMyID(), upSupplyPrice, upRetailPrice);
////            updateItem.setIntro(intro);
////            updateItem.mGroupIds = item.getGroupIdsFromGroups();
////            updateItem.mGroupNames = item.getGroupNamesFromGroups();
////            updateItem.agentPrice = myPrice + "";
////            updateItem.isOnly4Agent = item.isOnly4Agent();
////            updateItem.myItemId = item.getMyID();
////            Intent intent = new Intent(mContext, UpdateSharedItemActivity.class);
////            intent.putExtra(UpdateSharedItemActivity.EXTRA_UPDATE_ITEM, updateItem);
////            mContext.startActivityForResult(intent, UpdateSharedItemActivity.REQUEST_CODE_UPDATE_WP_ITEM);
//
//        }
//    }

//    private void shareToWx(ShopItemListModelx item, boolean direct) {
////        if (item.getImages().length > 0) {
////            if (direct) {
////                List<String> imgUrls = new ArrayList<String>();
////                for (String fileUrl : item.getImages()) {
////                    imgUrls.add(ImageUrlExtends.getImageUrl(fileUrl, UpYunConst.DOWNLOAD_ITEM_SIZE));
////                }
////                WXHelper wxHelper = new WXHelper();
////                String content = item.getIntroOrName() + "  ￥" + item.getRetailPrice();
////                wxHelper.ShareToWXTimeLine(mContext, content, imgUrls, item.getItemUrl(), false);
////
////                ItemInfoProvider.increaseShareCount(mContext, item.getID());
////                notifyDataSetChanged();
////            } else {
////                shopShare(item);
////            }
////
////        } else {
////            Toast.makeText(mContext, "商品没有任何图片可分享", Toast.LENGTH_LONG).show();
////        }
//    }
//
//    private void shopShare(ShopItemListModelx item) {
////        NahuoShare share = new NahuoShare(mContext, item);
////        share.show();
//    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_popup:
                showPopUp(v);
                break;
            case R.id.tv_share_count:
                int pos = (Integer) v.getTag(R.id.Tag_Position);
                ShopItemListModelX item = mdata.get(pos);
               // shareToWx(item, true);
                break;
            case R.id.tv_content:// 商品详情
                int position = (Integer) v.getTag(R.id.Tag_Position);
                onItemDetailClick(position);
                break;
            case R.id.btn_share_item:
                int sharePos2 = (Integer) v.getTag(R.id.Tag_Position);
             //   ShopItemListModelx shareitem2 = mdata.get(sharePos2);
//                if (!shareitem2.IsCopy) {
//                    shareToWp(shareitem2);
//                }
                break;
            case R.id.tv_agent:
            case R.id.tv_edit:
                int sharePos = (Integer) v.getTag(R.id.Tag_Position);
//                ShopItemListModelx shareitem = mdata.get(sharePos);
//                shareToWp(shareitem);
                break;
            case R.id.iv_buy:
                pos = (Integer) v.getTag(R.id.Tag_Position);
                if (mOnBuyClickListener != null) {
                    mOnBuyClickListener.onBuyClickListener(mdata.get(pos));
                }
                break;
        }
    }

    private void onItemDetailClick(int pos) {
//        ShopItemListModelx item = mdata.get(pos);
//        Intent intent = new Intent(mContext, ItemDetailsActivity.class);
//        intent.putExtra(ItemDetailsActivity.EXTRA_ID, item.getID());
//        if (item.getMyID() > 0) {
//            // 已经转发
////            item.setID(item.getMyID());
//            UpdateItem updateItem = ShopItemListModelx.toUpdateItem(item);
//            intent.putExtra("EXTRA_UPDATE_ITEM", updateItem);
//        }
//        mContext.startActivityForResult(intent, ShopItemsActivity.REQUEST_ITEM_DETAILS);
    }

    public void remove(int id) {
//        for (ShopItemListModelx item : mdata) {
//            if (item.getID() == id) {
//                mdata.remove(item);
//                vThis.notifyDataSetChanged();
//                break;
//            }
//        }
    }

    public void update(UpdateItem updateItem) {
        if (updateItem == null) {
            return;
        }
//        for (ShopItemListModelx item : mdata) {
//            if (item.getID() == updateItem.itemId) {
//                item.setName(updateItem.title);
//                item.setMyPrice(Double.valueOf(updateItem.agentPrice));
//                item.setMyRetailPrice(Double.valueOf(updateItem.retailPrice));
//                notifyDataSetChanged();
//                break;
//            }
//        }
    }

    public void updateMyItem(UpdateItem updateItem) {
        if (updateItem == null) {
            return;
        }
//        for (ShopItemListModelx item : mdata) {
//            if (item.getMyID() == updateItem.itemId) {
//                item.setName(updateItem.title);
//                item.setMyPrice(Double.valueOf(updateItem.agentPrice));
//                item.setMyRetailPrice(Double.valueOf(updateItem.retailPrice));
//                notifyDataSetChanged();
//                break;
//            }
//        }
    }

    private void setPrice(ViewHolder holder, int resId, double price) {
        switch (resId) {
            case R.id.tv_supply_price:
                holder.tvSupplyPrice.setVisibility(View.VISIBLE);
                holder.tvSupplyPrice.setText("供货商供货:￥" + mPriceFormat.format(price));
                break;
            case R.id.tv_supply_retail_price:
                holder.tvSupplyRetailPrice.setVisibility(View.VISIBLE);
                holder.tvSupplyRetailPrice.setText("供货商零售:￥" + mPriceFormat.format(price));
                break;
            case R.id.tv_agent_price:
                holder.tvAgentPrice.setVisibility(View.VISIBLE);
                holder.tvAgentPrice.setText("我给代理:￥" + mPriceFormat.format(price));
                break;
            case R.id.tv_retail_price:
                holder.tvRetailPrice.setVisibility(View.VISIBLE);
                holder.tvRetailPrice.setText("零售:￥" + mPriceFormat.format(price));
                break;
        }
    }

    public void setOnBuyClickListener(OnBuyClickListener l) {
        this.mOnBuyClickListener = l;
    }

    public interface OnBuyClickListener {

        public void onBuyClickListener(ShopItemListModelX model);

    }

    public class ViewHolder {
        private TextView tvNotShare, tvSupplyPrice, tvSupplyRetailPrice, tvAgentPrice, tvRetailPrice, tvDate,
                tvExpand, tvContent, tvShareCount;
        private ImageView ivBuy, tvShareToWp;

        private GridView gvPics;
        private ImageButton btnPopupMenu;
        private View btnEdit;

        private TextView btnShareToWp;

    }
}
