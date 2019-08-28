package com.nahuo.buyertool.api;
import android.content.Context;
import android.util.Log;

import com.nahuo.buyertool.model.PublicData;
import com.nahuo.buyertool.model.ShopItemListModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/24 0024.
 */
public class ModifylistAPI {
    private static ModifylistAPI instance = null;

    public static ModifylistAPI getInstance() {
        if (instance == null) {
            instance = new ModifylistAPI();
        }
        return instance;
    }
    public static String getModify(Context context, long agentItemId, int qsid, int days, ShopItemListModel.ReasonListBean reasonListBean) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
//        http://api2.nahuo.com/v3/buyertool/BuyerV2/UpdateItemScheduleInfo
//        入参：
//        agentItemId（long，必填)：商品ID
//        qsid(int，必填)：场次ID
//        days(int，必填)：排单天数，如果取消排单，则设置为0
        params.put("agentItemId", agentItemId+"");
        params.put("qsid", qsid+"");
        params.put("days", days+"");
        if (reasonListBean!=null){
            params.put("ReasonID", reasonListBean.getIDX()+"");
            params.put("ReasonText", reasonListBean.getText()+"");
        }
        String json = HttpUtils.httpPost("buyertool/BuyerV2/UpdateItemScheduleInfo",
                params,PublicData.getCookie(context));;
        Log.e("getModify","json="+json);
        return json;
    }
}
