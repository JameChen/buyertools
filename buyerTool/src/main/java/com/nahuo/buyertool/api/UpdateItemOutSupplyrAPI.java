package com.nahuo.buyertool.api;

import android.content.Context;
import android.util.Log;

import com.nahuo.buyertool.model.CheckItemOutSupplyBean;
import com.nahuo.buyertool.model.PublicData;
import com.nahuo.buyertool.model.ShopItemListModel;
import com.nahuo.library.helper.GsonHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/24 0024.
 */
public class UpdateItemOutSupplyrAPI {
    //    入参：
//    agentItemId（long，必填)：商品ID
//    qsid(int，必填)：场次ID
//    type(int，必填)：更新类型
//    已选的本期商品断货=1,
//    已选的所有期商品断货=2,
//    所有颜色尺码本期断货=3,
//    所有颜色尺码全部期数断货=4
//    colorAndSizeJson(string，如果type=1或2则必填，如果type=3或4则为空就行)：排单天数，如果取消排单，则设置为0
    public static String DETAIL_PAID = "paid";
    public static String DETAIL_BILLING = "billing";
    public static String DETAIL_OWES = "owes";
    public static String DETAIL_STORE = "store";
    public static String DETAIL_REFUND = "refund";
    public static String DETAIL_EXCEPTION="exception";
    private static UpdateItemOutSupplyrAPI instance = null;

    public static UpdateItemOutSupplyrAPI getInstance() {
        if (instance == null) {
            instance = new UpdateItemOutSupplyrAPI();
        }
        return instance;
    }


    public static String getUpdateItemOutSupply(Context context, long agentItemId, int qsid, int type, String colorAndSizeJson, ShopItemListModel.ReasonListBean reasonListBean, String from) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("agentItemId", agentItemId + " ");
        params.put("qsid", qsid + " ");
        params.put("type", type + " ");
        params.put("colorAndSizeJson", colorAndSizeJson + " ");
        if (reasonListBean != null) {
            params.put("ReasonID", reasonListBean.getIDX() + "");
            params.put("ReasonText", reasonListBean.getText() + "");
        }
        params.put("from", from);
        String json = "";
        json = HttpUtils.httpPost("buyertool/BuyerV2/UpdateItemOutSupply",
                params, PublicData.getCookie(context));
        Log.e("getUpdateItemOutSupply", "json=" + json);
        return json;
    }

    public static CheckItemOutSupplyBean checkItemOutSupply(Context context, long agentItemId, int qsid, int type, String colorAndSizeJson, ShopItemListModel.ReasonListBean reasonListBean, String from) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("agentItemId", agentItemId + " ");
        params.put("qsid", qsid + " ");
        params.put("type", type + " ");
        params.put("colorAndSizeJson", colorAndSizeJson + " ");
        if (reasonListBean != null) {
            params.put("ReasonID", reasonListBean.getIDX() + "");
            params.put("ReasonText", reasonListBean.getText() + "");
        }
        params.put("from", from);
        String json = "";
        json = HttpUtils.httpPost("buyertool/buyerv2/CheckItemOutSupply",
                params, PublicData.getCookie(context));
        CheckItemOutSupplyBean resultData = GsonHelper.jsonToObject(json, CheckItemOutSupplyBean.class);
        Log.e("getUpdateItemOutSupply", "json=" + json);
        return resultData;
    }
}
