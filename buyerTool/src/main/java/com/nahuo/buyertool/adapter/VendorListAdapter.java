package com.nahuo.buyertool.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.common.Const;
import com.nahuo.buyertool.common.ListUtils;
import com.nahuo.buyertool.event.OnVendorMenuClickListener;
import com.nahuo.buyertool.model.VendorListModel;
import com.nahuo.library.controls.AlertDialogEx;
import com.nahuo.library.controls.ShareMenu;
import com.nahuo.library.controls.ShareMenu.ShareMenuItem;
import com.nahuo.library.controls.pulltorefresh.PullToRefreshListView;
import com.nahuo.library.event.OnListViewItemClickListener;
import com.nahuo.library.helper.FunctionHelper;
import com.nahuo.library.helper.ImageUrlExtends;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VendorListAdapter extends BaseAdapter {

    public Context                mContext;
    public List<VendorListModel>  mList;
    private int                   screenWidth = 0;
    private String                strPriceRate;
    private EditText              evNewPriceRate;
    private TextView              tvPriceRate;
    private Button                SubmitBtn, GiveupBtn;
    private AlertDialogEx dialog;
    private float                 clickDownX, clickDownY;
    private View                  clickDownView;

    public VendorListAdapter(Context context){
        mContext = context;
    }
    public void setData(List<VendorListModel> data){
        mList = data;
    }
    public void addDataToTail(List<VendorListModel> data){
        mList.addAll(data);
    }
    public void clear(){
        if(mList != null){
            mList.clear();
        }
        
    }
    // 构造函数
    public VendorListAdapter(Context Context, List<VendorListModel> dataList, PullToRefreshListView pullRefreshListView) {
        mContext = Context;

        screenWidth = (int)(FunctionHelper.getScreenWidth((Activity)mContext));
        mList = dataList;

        // 点击事件的判断，这里加到pullRefreshListView是因为在外面无法touch到up事件
        pullRefreshListView.setOnListViewItemClickListener(new OnListViewItemClickListener() {

            @Override
            public void OnItemUp(float x, float y) {
                if (Math.abs(x - clickDownX) < 5 && Math.abs(y - clickDownY) < 5) {
                    if (mListener != null && clickDownView != null) {
                        ViewHolder clickHolder = (ViewHolder)clickDownView.getTag();
                        mListener.onItemClick(clickDownView, clickHolder.position);
                    }
                }

            }

            @Override
            public void OnItemDown(float x, float y) {
                clickDownX = x;
                clickDownY = y;
            }
        });
    }

    @Override
    public int getCount() {
        return ListUtils.isEmpty(mList) ? 0 : mList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int arg0, View arg1, ViewGroup arg2) {

        final ViewHolder holder;
        View view = arg1;
        if (mList.size() > 0) {
            String shopCoverUrl = mList.get(arg0).getLogo();
            String userid = mList.get(arg0).getUserName();
            String username = mList.get(arg0).getShopName();
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.layout_vendor_item, arg2, false);
                holder = new ViewHolder();

                holder.shopCover = (ImageView)view.findViewById(R.id.vendor_item_shopCover);
                holder.userid = (TextView)view.findViewById(R.id.vendor_item_userid);
                holder.username = (TextView)view.findViewById(R.id.vendor_item_username);
                holder.menu = (ImageButton)view.findViewById(R.id.vendor_items_menu);
                holder.menu.setTag(holder);
                holder.menu.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        showPopUp(v);
                    }
                });
                view.setOnTouchListener(new OnTouchListener() {

                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                clickDownView = v;
                                break;
                        }

                        return false;
                    }
                });
                view.setTag(holder);
            } else {
                holder = (ViewHolder)view.getTag();
            }
            holder.position = arg0;
            holder.userid.setText(userid);
            holder.username.setText(username);
            String imageurl = ImageUrlExtends.getImageUrl(shopCoverUrl, Const.LIST_COVER_SIZE);
            Picasso.with(mContext).load(imageurl).placeholder(R.drawable.empty_photo).into(holder.shopCover);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null){
                        mListener.onItemClick(v, arg0);
                    }
                }
            });
        }

        return view;
    }

    class ViewHolder {
        int         position;
        ImageView   shopCover;
        TextView    userid;
        TextView    username;
        ImageButton menu;
    }

    /**
     * 单击事件监听器
     */
    private OnVendorMenuClickListener mListener = null;

    public void setOnVendorMenuClickListener(OnVendorMenuClickListener listener) {
        mListener = listener;
    }

    private void showPopUp(final View v) {
        final ViewHolder clickHolder = (ViewHolder)v.getTag();
        VendorListModel vendorModel = mList.get(clickHolder.position);

        strPriceRate = FunctionHelper.DoubleToString(vendorModel.getMyPriceRate() * 100);

        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_change_rate, null);
        // 弹出pop里面的控件
        tvPriceRate = (TextView)view.findViewById(R.id.layout_change_rate_old_pricerate);
        evNewPriceRate = (EditText)view.findViewById(R.id.layout_change_rate_new_pricerate);
        tvPriceRate.setText(strPriceRate + "%");
        evNewPriceRate.setText(strPriceRate);
        evNewPriceRate.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable edt) {
                String temp = edt.toString();
                int posDot = temp.indexOf(".");
                if (posDot <= 0)
                    return;
                if (temp.length() - posDot - 1 > 2) {
                    edt.delete(posDot + 3, posDot + 4);
                }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
        });

        dialog = new AlertDialogEx(mContext, view);
        GiveupBtn = (Button)view.findViewById(R.id.vendor_menu_cancel_btn);
        SubmitBtn = (Button)view.findViewById(R.id.vendor_menu_submit_btn);
        GiveupBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        GiveupBtn.setWidth((int)(screenWidth * 0.45));
        SubmitBtn.setWidth((int)(screenWidth * 0.45));
        SubmitBtn.setTag(clickHolder);
        SubmitBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (evNewPriceRate.getText().toString().length() == 0) {
                    Toast.makeText(mContext, "请填写加价率", Toast.LENGTH_LONG).show();
                } else {
                    if (mListener != null) {
                        ViewHolder clickHolder = (ViewHolder)v.getTag();
                        float newRate = Float.parseFloat(evNewPriceRate.getText().toString());
                        mListener.onChangeRateClick(v, clickHolder.position, newRate / 100);
                    }
                    dialog.cancel();
                }
            }
        });


        ShareMenu menu = new ShareMenu((Activity)mContext);
        menu.addMenuItem(new ShareMenuItem(mContext.getString(R.string.vendor_changrate)))
                .addMenuItem(new ShareMenuItem(mContext.getString(R.string.vendor_receiveaccount)))
                .addMenuItem(new ShareMenuItem(mContext.getString(R.string.vendor_cancel)))
                .setMenuItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0:
                                dialog.Start();
                                break;
                            case 1:
                                if (mListener != null) {
                                    mListener.onReceiveAccountClick(v, clickHolder.position);
                                }
                                break;
                            case 2:
                                if (mListener != null) {
                                    mListener.onUnJoinClick(v, clickHolder.position);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }).show(v);;
    }
}
