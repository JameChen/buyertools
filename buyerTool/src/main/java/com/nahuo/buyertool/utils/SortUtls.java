package com.nahuo.buyertool.utils;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.nahuo.buyertool.Bean.SortBean;
import com.nahuo.buyertool.common.SpManager;
import com.nahuo.library.helper.GsonHelper;

import java.util.List;

/**
 * Created by jame on 2018/5/16.
 */

public class SortUtls {
//    待开单：paid
//    已开单：billing
//    欠货单：owes
//    入仓单：store
//    退款单：refund
//    已开单和欠货单：billingandowes
//    我的款式：item
    public final static String TYPE_PAID="paid";
    public final static String TYPE_BILLING="billing";
    public final static String TYPE_OWES="owes";
    public final static String TYPE_STORE="store";
    public final static String TYPE_REFUND="refund";
    public final static String TYPE_BILLINGANDOWES="billingandowes";
    public final static String TYPE_ITEM="item";
    public final static String TYPE_EXCEPTION="exception";

    public final static int TYPE_SORT_1=1;
    public final static int TYPE_SORT_2=2;
    public  static List<SortBean> getListFilter(Context context) {
        List<SortBean> list=null;
        try {
            String json = SpManager.getListFilter(context);
            list = GsonHelper.jsonToObject(json, new TypeToken<List<SortBean>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
