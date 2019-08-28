package com.nahuo.buyertool.adapter;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.CommonListActivity;
import com.nahuo.buyertool.ViewHub;
import com.nahuo.buyertool.activity.PicActivity;
import com.nahuo.buyertool.api.BuyerToolAPI;
import com.nahuo.buyertool.model.ProductModel;
import com.nahuo.library.controls.LoadingDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nahuo.buyertool.CommonListActivity.ListType.已开单;
import static com.nahuo.buyertool.MainAcivity.ERROR_PREFIX;

public class DetailProductAdapter extends BaseAdapter {

    public Context mContext;
    public List<ProductModel> mList;
    private CommonListActivity.ListType mType;
    private int mRCStatusID;
    private OnChangeQtyListener mListener;

    public Map<Integer, Boolean> isCheck = new HashMap<Integer, Boolean>();
    public HashMap<String, String> maps = new HashMap<String, String>();
    public List<Map<String, String>> mvalue = new ArrayList<Map<String, String>>();

    public void setUpdateItemOutSupplyType(int updateItemOutSupplyType) {
        this.updateItemOutSupplyType = updateItemOutSupplyType;
    }

    public int updateItemOutSupplyType = -1;
    private LoadingDialog loadingDialog = null;
    // 构造函数
    public DetailProductAdapter(Context Context, List<ProductModel> dataList, CommonListActivity.ListType type,
                                int statusid) {
        mContext = Context;
        mList = dataList;
        mType = type;
        loadingDialog = new LoadingDialog(mContext);
        mRCStatusID = statusid;
        initCheck(false);
    }


    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public ProductModel getItem(int arg0) {
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
            if (view == null) {
                holder = new ViewHolder();
                final ProductModel item = mList.get(arg0);
                switch (mType) {
                    case 待开单: {
                        view = LayoutInflater.from(mContext).inflate(
                                R.layout.item_item_detail_sizecolor4_work_wait, arg2, false);
                        holder.checkBox = (CheckBox) view.findViewById(R.id.item_detail_sizecolor_cb);
//
                        holder.duan_huo = (TextView) view.findViewById(R.id.item_detail_sizecolor_item_duan_huo);
                        holder.item_detail_sizecolor_item_stock=(TextView)view.findViewById(R.id.item_detail_sizecolor_item_stock);
                        holder.item_detail_sizecolor_item_stock_company=(TextView)view.findViewById(R.id.item_detail_sizecolor_item_stock_company);
                        if (item.isOutSupply() == true) {
                            holder.checkBox.setVisibility(view.GONE);
                            holder.duan_huo.setVisibility(view.VISIBLE);
                        } else {
                            holder.checkBox.setVisibility(view.VISIBLE);
                            holder.duan_huo.setVisibility(view.GONE);
                        }
                        holder.color = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_1);
                        holder.size = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_2);
                        holder.txt1 = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_3);
                        holder.txtReduce = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_qty_reduce);
                        holder.txtQty = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_qty);
                        holder.txtAdd = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_qty_add);
                        view.setTag(holder);
                        break;
                    }
                    case 异常单:
                    case 已开单: {
                        view = LayoutInflater.from(mContext).inflate(
                                R.layout.item_item_detail_sizecolor4, arg2, false);
                        holder.checkBox = (CheckBox) view.findViewById(R.id.item_detail_sizecolor_cb);
                        holder.duan_huo = (TextView) view.findViewById(R.id.item_detail_sizecolor_item_duan_huo);
                        holder.item_detail_sizecolor_item_stock=(TextView)view.findViewById(R.id.item_detail_sizecolor_item_stock);
                        holder.txtReduce = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_qty_reduce);
                        holder.txtQty = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_qty);
                        holder.txtAdd = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_qty_add);
                        if (item.isOutSupply() == true) {
                            holder.checkBox.setVisibility(view.GONE);
                            holder.duan_huo.setVisibility(view.VISIBLE);

                        } else {
                            holder.checkBox.setVisibility(view.VISIBLE);
                            holder.duan_huo.setVisibility(view.GONE);
                        }
                        holder.color = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_1);
                        holder.size = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_2);
                        holder.txt1 = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_3);
                        holder.txt2 = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_4);
                        view.setTag(holder);
                        break;
                    }
                    case 入库单: {
                        view = LayoutInflater.from(mContext).inflate(
                                R.layout.item_item_detail_sizecolor5, arg2, false);
                        holder.checkBox = (CheckBox) view.findViewById(R.id.item_detail_sizecolor_cb);
                        holder.duan_huo = (TextView) view.findViewById(R.id.item_detail_sizecolor_item_duan_huo);
                        if (item.isOutSupply() == true) {
                            holder.checkBox.setVisibility(view.GONE);
                            holder.duan_huo.setVisibility(view.VISIBLE);

                        } else {
                            holder.checkBox.setVisibility(view.VISIBLE);
                            holder.duan_huo.setVisibility(view.GONE);
                        }
                        holder.color = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_1);
                        holder.size = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_2);
                        holder.txt1 = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_3);
                        holder.txt2 = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_4);
                        holder.txtQty = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_5);
                        view.setTag(holder);
                        break;
                    }
                    case 欠货单: {
                        view = LayoutInflater.from(mContext).inflate(
                                R.layout.item_item_detail_sizecolor4_work, arg2, false);
                        holder.item_detail_sizecolor_item_stock=(TextView)view.findViewById(R.id.item_detail_sizecolor_item_stock);
                        holder.checkBox = (CheckBox) view.findViewById(R.id.item_detail_sizecolor_cb);
                        holder.duan_huo = (TextView) view.findViewById(R.id.item_detail_sizecolor_item_duan_huo);
                        if (item.isOutSupply() == true) {
                            holder.checkBox.setVisibility(view.GONE);
                            holder.duan_huo.setVisibility(view.VISIBLE);

                        } else {
                            holder.checkBox.setVisibility(view.VISIBLE);
                            holder.duan_huo.setVisibility(view.GONE);
                        }
//                        holder.checkBox = (CheckBox) view
//                                .findViewById(R.id.item_detail_sizecolor_checkbox);
                        holder.color = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_1);
                        holder.size = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_2);
                        holder.txt1 = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_3);
                        holder.txtReduce = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_qty_reduce);
                        holder.txtQty = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_qty);
                        holder.txtAdd = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_qty_add);
                        view.setTag(holder);
                        break;
                    }
                    case 退款单: {
                        view = LayoutInflater.from(mContext).inflate(
                                R.layout.item_item_detail_sizecolor5, arg2, false);
                        holder.checkBox = (CheckBox) view.findViewById(R.id.item_detail_sizecolor_cb);
                        holder.duan_huo = (TextView) view.findViewById(R.id.item_detail_sizecolor_item_duan_huo);
                        if (item.isOutSupply() == true) {
                            holder.checkBox.setVisibility(view.GONE);
                            holder.duan_huo.setVisibility(view.VISIBLE);

                        } else {
                            holder.checkBox.setVisibility(view.VISIBLE);
                            holder.duan_huo.setVisibility(view.GONE);
                        }
                        holder.color = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_1);
                        holder.size = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_2);
                        holder.txt1 = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_3);
                        holder.txt2 = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_4);
                        holder.txtQty = (TextView) view
                                .findViewById(R.id.item_detail_sizecolor_item_5);
                        view.setTag(holder);
                        break;
                    }
                }
            } else {
                holder = (ViewHolder) view.getTag();
            }
            final ProductModel item = mList.get(arg0);

            holder.color.setText(item.getColor());
            holder.size.setText(item.getSize());
            checkBox(holder, item);
            switch (mType) {
                case 待开单: {
                    holder.txt1.setText(item.getPHQty() + "");
                    holder.txtQty.setText(item.getKDQty() + "");
                    if (holder.item_detail_sizecolor_item_stock!=null)
                    holder.item_detail_sizecolor_item_stock.setText(item.getStock()+"");
                    holder.item_detail_sizecolor_item_stock_company.setText(item.getCompanyStock()+"");
                    break;
                }
                case 异常单:
                case 已开单: {
                    if (mRCStatusID == 2) {
                        holder.txt1.setText(item.getPHQty() + "");
                        holder.txt2.setText(item.getQHQty() + "");
                    } else {
                        holder.txt1.setText(item.getPHQty() + "");
                        holder.txt2.setText(item.getKDQty() + "");
                    }
                    holder.txtQty.setText(item.getDHQty() + "");
                    holder.item_detail_sizecolor_item_stock.setText(item.getTag());
                    break;
                }
                case 入库单: {
                    holder.txt1.setText(item.getPHQty() + "");
                    holder.txt2.setText(item.getKDQty() + "");
                    holder.txtQty.setText(item.getStoreQty() + "");
                    break;
                }
                case 欠货单: {
                    holder.txt1.setText(item.getQHQty() + "");
                    holder.txtQty.setText(item.getTKQty() + "");
                    holder.item_detail_sizecolor_item_stock.setText(item.getTag());
                    break;
                }
                case 退款单: {
                    holder.txt1.setText(item.getKDQty() + "");
                    holder.txt2.setText(item.getQHQty() + "");
                    holder.txtQty.setText(item.getTKQty() + "");
                    break;
                }
            }
//            if (holder.checkBox!=null) {
//                holder.checkBox.setVisibility(View.VISIBLE);
//                holder.checkBox.setTag(arg0);
//                holder.checkBox.setChecked(item.isCheck());
//                holder.checkBox.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        CheckBox cb = (CheckBox) v;
//                        int selectPosition = (int)cb.getTag();
//                        ProductModel selectItem = mList.get(selectPosition);
//                        selectItem.setIsCheck(cb.isChecked());
//                        notifyDataSetChanged();
//                    }
//                });
//            }
            if (holder.txtReduce != null) {
                holder.txtReduce.setTag(arg0);
                holder.txtReduce.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView cb = (TextView) v;
                        int selectPosition = (int) cb.getTag();
                        ProductModel selectItem = mList.get(selectPosition);
                        qtyWork(true, selectItem);
                    }
                });
                holder.txtAdd.setTag(arg0);
                holder.txtAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView cb = (TextView) v;
                        int selectPosition = (int) cb.getTag();
                        ProductModel selectItem = mList.get(selectPosition);
                        qtyWork(false, selectItem);
                    }
                });
            }
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //  if (isChecked) {
                    isCheck.put(arg0, isChecked);
//                        maps = new HashMap<String, String>();
//                        maps.put("color", item.getColor());
//                        maps.put("Size", item.getSize());0
//                        if (item.getPHQty() != 0) {
//                            maps.put("PHQty", item.getPHQty() + "");
//                        }
//                        if (item.getYRKQty() != 0) {
//                            maps.put("YRKQty", item.getYRKQty() + "");
//                        }
//                        if (item.getKDQty() != 0) {
//                            maps.put("KDQty", item.getKDQty() + "");
//                        }
//                        if (item.getDHQty() != 0) {
//                            maps.put("DHQty", item.getDHQty() + "");
//                        }
//                        if (item.getQHQty() != 0) {
//                            maps.put("QHQty", item.getQHQty() + "");
//                        }
//                        if (item.getTKQty() != 0) {
//                            maps.put("TKQty", item.getTKQty() + "");
//                        }
//                        if (item.getStoreQty() != 0) {
//                            maps.put("StoreQty", item.getStoreQty() + "");
//                        }
//                        mvalue.add(maps);
                    //  }
                }
            });
            if (isCheck.get(arg0) == null) {
                isCheck.put(arg0, false);
            }
            holder.checkBox.setChecked(isCheck.get(arg0));
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mType == 已开单 ) {
                        ProductModel item = mList.get(arg0);
                        if (item != null) {
                            if (TextUtils.isEmpty(item.getCode())) {
                                //ViewHub.showShortToast(mContext, "二维码为空");
                                new Task(item.getDetailID()).execute();
                            } else {
                                Intent intent = new Intent(mContext, PicActivity.class);
                                intent.putExtra(PicActivity.ETRA_PIC, item.getCode());
                                mContext.startActivity(intent);
                            }
                        }
                    }
                    return false;
                }
            });
        }


        return view;
    }
    private class Task extends AsyncTask<Object, Void, Object> {
        long detailid;

        public Task(long detailid) {
            this.detailid = detailid;
        }

        @Override
        protected void onPreExecute() {
                    loadingDialog.start("获取二维码中...");
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                return BuyerToolAPI.getQrcode(mContext,detailid+"");
            } catch (Exception e) {
                e.printStackTrace();
                return "error:" + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            if (loadingDialog.isShowing()) {
                loadingDialog.stop();
            }
            if (result instanceof String && ((String) result).startsWith(ERROR_PREFIX)) {
                String msg = ((String) result).replace(ERROR_PREFIX, "");
                ViewHub.showLongToast(mContext, msg);
                // 验证result
                return;
            }else {
                String code = ((String) result);
                Intent intent = new Intent(mContext, PicActivity.class);
                intent.putExtra(PicActivity.ETRA_PIC, code);
                mContext.startActivity(intent);
            }
        }
    }
    private void checkBox(ViewHolder holder, ProductModel item) {
        if ((updateItemOutSupplyType == 1 || updateItemOutSupplyType == 2) && item.isOutSupply() == false) {
            holder.checkBox.setVisibility(View.VISIBLE);
        } else {
            holder.checkBox.setVisibility(View.GONE);
        }
    }


    private void qtyWork(boolean reduce, ProductModel selectItem) {
        switch (mType) {
            case 待开单: {
                if (reduce) {
                    selectItem.setKDQty(selectItem.getKDQty() - 1);
                } else {
                    selectItem.setKDQty(selectItem.getKDQty() + 1);
                }
                if (mListener != null) {
                    mListener.OnQtyChange();
                }
                break;
            }
            case 异常单:
            case 已开单: {
                if (reduce) {
                    selectItem.setDHQty(selectItem.getDHQty() - 1, 1);
                } else {
                    selectItem.setDHQty(selectItem.getDHQty() + 1, 1);
                }
                if (mListener != null) {
                    mListener.OnQtyChange();
                }
                break;
            }
            case 入库单: {
                break;
            }
            case 欠货单: {
                if (reduce) {
                    selectItem.setTKQty(selectItem.getTKQty() - 1);
                } else {
                    selectItem.setTKQty(selectItem.getTKQty() + 1);
                }
                if (mListener != null) {
                    mListener.OnQtyChange();
                }
                break;
            }
            case 退款单: {
                break;
            }
        }
        notifyDataSetChanged();
    }

    public class ViewHolder {
        public int position;
        CheckBox checkBox;
        TextView color;
        TextView size;
        TextView txt1;
        TextView txt2;
        TextView txtReduce;
        TextView txtQty;
        TextView txtAdd, duan_huo,item_detail_sizecolor_item_stock,item_detail_sizecolor_item_stock_company;
    }

    public static interface OnChangeQtyListener {
        public void OnQtyChange();
    }

    public void setListener(OnChangeQtyListener listener) {
        mListener = listener;
    }

    // 初始化map集合
    public void initCheck(boolean flag) {
// map集合的数量和list的数量是一致的
        for (int i = 0; i < mList.size(); i++) {
// 设置默认的显示
            isCheck.put(i, flag);
        }
    }

    // 全选按钮获取状态
    public Map<Integer, Boolean> getMap() {
// 返回状态
        return isCheck;
    }

    // 删除一个数据
    public void removeData(int position) {
        mList.remove(position);
    }

    public Map<String, String> getMaps() {
// 返回状态
        return maps;
    }

    public List<Map<String, String>> getmvalue() {
// 返回状态
        if (!isCheck.isEmpty()) {
            mvalue.clear();
            for (Map.Entry<Integer, Boolean> entry : isCheck.entrySet()) {
                //Map.entry<Integer,String> 映射项（键-值对）  有几个方法：用上面的名字entry
                //entry.getKey() ;entry.getValue(); entry.setValue();
                //map.entrySet()  返回此映射中包含的映射关系的 Set视图。
                if (entry.getValue()) {
                    ProductModel item = mList.get(entry.getKey());
                    maps = new HashMap<String, String>();
                    maps.put("color", item.getColor());
                    maps.put("Size", item.getSize());
                    if (item.getPHQty() != 0) {
                        maps.put("PHQty", item.getPHQty() + "");
                    }
                    if (item.getYRKQty() != 0) {
                        maps.put("YRKQty", item.getYRKQty() + "");
                    }
                    if (item.getKDQty() != 0) {
                        maps.put("KDQty", item.getKDQty() + "");
                    }
                    if (item.getDHQty() != 0) {
                        maps.put("DHQty", item.getDHQty() + "");
                    }
                    if (item.getQHQty() != 0) {
                        maps.put("QHQty", item.getQHQty() + "");
                    }
                    if (item.getTKQty() != 0) {
                        maps.put("TKQty", item.getTKQty() + "");
                    }
                    if (item.getStoreQty() != 0) {
                        maps.put("StoreQty", item.getStoreQty() + "");
                    }
                    mvalue.add(maps);
                }
//                System.out.println("key= " + entry.getKey() + " and value= "
//                        + entry.getValue());
            }
        }
        return mvalue;
    }
}

