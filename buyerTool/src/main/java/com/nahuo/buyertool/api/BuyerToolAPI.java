package com.nahuo.buyertool.api;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.nahuo.buyertool.Bean.PrintBean;
import com.nahuo.buyertool.CommonListActivity;
import com.nahuo.buyertool.common.CacheDirUtil;
import com.nahuo.buyertool.model.ProductModel;
import com.nahuo.buyertool.model.PublicData;
import com.nahuo.buyertool.model.PurchaseModel;
import com.nahuo.buyertool.model.ShopItemListModel;
import com.nahuo.buyertool.model.StallsSearchModel;
import com.nahuo.library.helper.GsonHelper;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nahuo.buyertool.api.HttpUtils.httpGet;
import static com.nahuo.buyertool.api.HttpUtils.httpPost;

public class BuyerToolAPI {

    private static final String TAG = BuyerToolAPI.class.getSimpleName();
    private static BuyerToolAPI instance = null;

    /**
     * 单例
     */
    public static BuyerToolAPI getInstance() {
        if (instance == null) {
            instance = new BuyerToolAPI();
        }
        return instance;
    }

    /**
     * 获取档口列表数据
     */
    public static List<StallsSearchModel> getStallsList(Context context) throws Exception {
        Map<String, String> params = new HashMap<String, String>();

        String json = httpPost("buyertool/purchase/GetAllStallsForSearch", params, PublicData.getCookie(context));

        List<StallsSearchModel> result = GsonHelper.jsonToObject(json,
                new TypeToken<List<StallsSearchModel>>() {
                });
        return result;
    }

    /**
     * 获取采购单列表数据
     */
    public static List<PurchaseModel> getPurchaseList(Context context,
                                                      String datetime, int marketid, int floorid, int stallid,
                                                      int pageIndex, int pageSize, String keyWord) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        if (datetime.length() > 0) {
            params.put("datetime", datetime + "");
        }
        if (!TextUtils.isEmpty(keyWord)) {
            params.put("keyWord", keyWord + "");
        }
        if (marketid >= 0) {
            params.put("marketid", marketid + "");
        }
        if (floorid >= 0) {
            params.put("floorid", floorid + "");
        }
        if (stallid >= 0) {
            params.put("stallid", stallid + "");
        }
        params.put("pageIndex", pageIndex + "");
        params.put("pageSize", pageSize + "");

        String json = httpPost("buyertool/BuyerV2/getpurchaseorderlist", params, PublicData.getCookie(context));

        List<PurchaseModel> result = GsonHelper.jsonToObject(json,
                new TypeToken<List<PurchaseModel>>() {
                });
        Log.e("列表中的数据","result="+result);
        return result;
    }

    /**
     * 获取列表数据
     */
    public static List<ShopItemListModel> getList(Context context, CommonListActivity.ListType type, String searchKeyword, int pageIndex, int pageSize, File cacheFile, boolean pageLastOne) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        if (searchKeyword != null && searchKeyword.length() > 0) {
            params.put("keyword", searchKeyword);
        }
        params.put("pageSize", pageSize + "");
        params.put("pageIndex", pageIndex + "");
        if (pageLastOne) params.put("getCurrentPageLastOne", "true");
        String json = "";
        switch (type) {
            case 待开单:
                json = httpPost("buyertool/BuyerV2/getpayorderlist", params, PublicData.getCookie(context));
                Log.v(TAG,json);
                break;
            case 已开单:
                json = httpPost("buyertool/BuyerV2/getBillingList", params, PublicData.getCookie(context));
                break;
            case 入库单:
                json = httpPost("buyertool/BuyerV2/getStoreList", params, PublicData.getCookie(context));
                break;
            case 欠货单:
                json = httpPost("buyertool/BuyerV2/getoweslist", params, PublicData.getCookie(context));
                break;
            case 退款单:
                json = httpPost("buyertool/BuyerV2/getrefundlist", params, PublicData.getCookie(context));
                break;
            default:
                break;
        }

        List<ShopItemListModel> result = GsonHelper.jsonToObject(json,
                new TypeToken<List<ShopItemListModel>>() {
                });
        if (cacheFile != null && result.size() > 0) {
            CacheDirUtil.saveString(cacheFile, json);
        }
        return result;
    }
    /**
     * 获取商品列表数据
     */
    public static List<ShopItemListModel> getShopList(Context context, CommonListActivity.ListType type, String searchKeyword,String stallWord, int pageIndex, int pageSize, File cacheFile, boolean pageLastOne
    ,int status,int sort) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        if (!TextUtils.isEmpty(searchKeyword)||!TextUtils.isEmpty(stallWord)) {
            params.put("keyword", searchKeyword);
            params.put("stallName", stallWord);
        }
        params.put("status",status+"");
        params.put("sort",sort+"");
        params.put("pageSize", pageSize + "");
        params.put("pageIndex", pageIndex + "");
        if (pageLastOne) params.put("getCurrentPageLastOne", "true");
        String json = "";
        switch (type) {
            case 待开单:
                json = httpPost("buyertool/BuyerV2/getpayorderlist", params, PublicData.getCookie(context));
                Log.v(TAG,json);
                break;
            case 已开单:
                json = httpPost("buyertool/BuyerV2/getBillingList", params, PublicData.getCookie(context));
                break;
            case 入库单:
                json = httpPost("buyertool/BuyerV2/getStoreList", params, PublicData.getCookie(context));
                break;
            case 欠货单:
                json = httpPost("buyertool/BuyerV2/getoweslist", params, PublicData.getCookie(context));
                break;
            case 退款单:
                json = httpPost("buyertool/BuyerV2/getrefundlist", params, PublicData.getCookie(context));
                break;
            case 开或欠单:
                json = httpPost("buyertool/BuyerV2/getbillingandowelist", params, PublicData.getCookie(context));
                break;
            case 异常单:
                params.put("typeID", "1");
                json = httpPost("buyertool/BuyerV2/GetBillingList", params, PublicData.getCookie(context));
                break;
            default:
                break;
        }

        List<ShopItemListModel> result = GsonHelper.jsonToObject(json,
                new TypeToken<List<ShopItemListModel>>() {
                });
        if (cacheFile != null && result.size() > 0) {
            CacheDirUtil.saveString(cacheFile, json);
        }
        return result;
    }

    /**
     * 获取详细内容
     */
    public static List<PrintBean> getQrcodeList(Context context, String json1) throws Exception {
        String json="";
        Map<String, String> params = new HashMap<String, String>();
        params.put("data", json1);
        json = httpPost("/buyertool/BuyerV2/getqrcodelist", params, PublicData.getCookie(context));
        Log.d("yu",json);
        List<PrintBean> result = GsonHelper.jsonToObject(json,
                new TypeToken<List<PrintBean>>() {
                });
        return  result;
    }
    /**
     * 获取详细内容
     */
    public static String getQrcode(Context context, String detailid) throws Exception {
        String json="",code="";
        Map<String, String> params = new HashMap<String, String>();
        params.put("detailid", detailid);
        json = httpGet("/buyertool/buyerV2/getoneqrcode", params, PublicData.getCookie(context));
        Log.d("yu",json);
        JSONObject jsonObject=new JSONObject(json);
        code=jsonObject.optString("Code");
//        List<PrintBean> result = GsonHelper.jsonToObject(json,
//                new TypeToken<List<PrintBean>>() {
//                });
        return  code;
    }

    /**
     * 获取详细内容
     */
    public static ShopItemListModel getDetail(Context context, CommonListActivity.ListType type, int id, int qsid) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id + "");
        params.put("qsid", qsid + "");

        String json = "";
        switch (type) {
            case 待开单:
                json = httpPost("/buyertool/BuyerV2/getpayorderdetail", params, PublicData.getCookie(context));
                Log.e("待开单","json="+json);
                break;
            case 已开单:
                json = httpPost("/buyertool/BuyerV2/getbillingdetail", params, PublicData.getCookie(context));
                Log.e("已开单","json="+json);
                break;
            case 入库单:
                json = httpPost("/buyertool/BuyerV2/getstoredetail", params, PublicData.getCookie(context));
                break;
            case 欠货单:
                json = httpPost("/buyertool/BuyerV2/getbillingdetail", params, PublicData.getCookie(context));
                break;
            case 退款单:
                json = httpPost("/buyertool/BuyerV2/getrefunddetail", params, PublicData.getCookie(context));
                break;
            case 异常单:
                json = httpPost("/buyertool/BuyerV2/GetBillingDetail", params, PublicData.getCookie(context));
                break;
            default:
                break;
        }
        ShopItemListModel result = GsonHelper.jsonToObject(json,
                new TypeToken<ShopItemListModel>() {
                });
        return result;
    }


//    /**
//     * 获取备注数据
//     *
//     * */
//    public static List<ItemRemarkModel> getDetailRemark(Context context,int id) throws Exception {
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("id",id+"");
//
//        String json = HttpUtils.httpPost( "shop/buyer/getrecordlist", params, PublicData.getCookie(context));
//
//        List<ItemRemarkModel> result = GsonHelper.jsonToObject(json,
//                new TypeToken<List<ItemRemarkModel>>() {
//                });
//        return result;
//    }

    /**
     * 提交备注
     */
    public static void submitRemark(Context context, int id,int qsid,String content) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", qsid + "");
        params.put("qsid", id+"");//商品的qsid(传反了)
        params.put("content", content);
        Log.v(TAG,"id:"+id+","+"qsid:"+qsid+","+"content:"+content);
        String json= httpPost("buyertool/BuyerV2/addrecord", params, PublicData.getCookie(context));
        Log.v(TAG,json);
    }

    /**
     * 添加排单记录
     */
    public static void AddStockRecord(Context context, int itemid, int stockStatuID, int stockDay,int qsid) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("agentItemId", itemid + "");
        params.put("stockStatuId", stockStatuID + "");
        params.put("stockDays", stockDay + "");
        params.put("qsid", qsid + "");

        httpPost("buyertool/BuyerV2/AddStockRecord", params, PublicData.getCookie(context));
    }
    /**
     * 撤銷单
     */
    public static void cancelKD(Context context,int id)throws Exception{
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id + "");
       String json= HttpUtils.httpPost("buyertool/BuyerV2/CancelKD", params, PublicData.getCookie(context));
        Log.d("yu",json);
    }
    /**
     * 开单
     */
    public static void submitBilling(Context context, int orderid, int id, int qsid, List<ProductModel> products, String content,boolean IsStock,boolean IsCompanyStock) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id + "");
        params.put("qsid", qsid + "");
        params.put("content", content);
        params.put("orderid", orderid + "");
        params.put("IsStock",IsStock+"");
        params.put("IsCompanyStock",IsCompanyStock+"");
        String qtyJson = "[";
        for (ProductModel pm : products
                ) {
            qtyJson += "{";
            qtyJson += "color:'" + pm.getColor() + "',";
            qtyJson += "size:'" + pm.getSize() + "',";
            qtyJson += "PHQty:'" + pm.getPHQty() + "',";
            qtyJson += "KDQty:'" + pm.getKDQty() + "'";
            qtyJson += "},";
        }
        if (qtyJson.length() > 3) {
            qtyJson = qtyJson.substring(0, qtyJson.length() - 1);
        } else {
            throw new Exception("请开单至少一个SKU");
        }
        qtyJson += "]";
        params.put("qtySizeJson", qtyJson);

        httpPost("buyertool/BuyerV2/submitbilling", params, PublicData.getCookie(context));
    }

    /**
     * 入仓
     */
    public static void submitStore(Context context, int id, int qsid, List<ProductModel> products, String warehouse, String content) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id + "");
        params.put("qsid", qsid + "");
        params.put("warehouse", warehouse);
        params.put("content", content);
        String qtyJson = "[";
        for (ProductModel pm : products
                ) {
            qtyJson += "{";
            qtyJson += "color:'" + pm.getColor() + "',";
            qtyJson += "size:'" + pm.getSize() + "',";
            qtyJson += "KDQty:'" + pm.getKDQty() + "',";
            qtyJson += "CHQty:'" + pm.getDHQty() + "'";
            qtyJson += "},";
        }
        if (qtyJson.length() > 3) {
            qtyJson = qtyJson.substring(0, qtyJson.length() - 1);
        } else {
            throw new Exception("请入仓至少一个SKU");
        }
        qtyJson += "]";
        params.put("qtySizeJson", qtyJson);

        httpPost("buyertool/BuyerV2/submitStore4Billing", params, PublicData.getCookie(context));
    }

    /**
     * 欠货入仓
     */
    public static void submitStore4Owe(Context context, int id, int qsid, List<ProductModel> products, String warehouse, String content) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id + "");
        params.put("qsid", qsid + "");
        params.put("warehouse", warehouse);
        params.put("content", content);
        String qtyJson = "[";
        for (ProductModel pm : products
                ) {
            qtyJson += "{";
            qtyJson += "color:'" + pm.getColor() + "',";
            qtyJson += "size:'" + pm.getSize() + "',";
            qtyJson += "CHQty:'" + pm.getDHQty() + "',";
            qtyJson += "QHQty:'" + pm.getQHQty() + "'";
            qtyJson += "},";
        }
        if (qtyJson.length() > 3) {
            qtyJson = qtyJson.substring(0, qtyJson.length() - 1);
        } else {
            throw new Exception("请入仓至少一个SKU");
        }
        qtyJson += "]";
        params.put("qtySizeJson", qtyJson);

        httpPost("buyertool/BuyerV2/submitStore4Owe", params, PublicData.getCookie(context));
    }

    /**
     * 追单
     */
    public static void submit4Chasing(Context context, int id, int qsid, List<ProductModel> products, String warehouse, String content) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id + "");
        params.put("qsid", qsid + "");
        params.put("warehouse", warehouse);
        params.put("content", content);
        String qtyJson = "[";
        for (ProductModel pm : products
                ) {
//            if (pm.isCheck()) {
//            if (pm.getTKQty()>0) {
            qtyJson += "{";
            qtyJson += "color:'" + pm.getColor() + "',";
            qtyJson += "size:'" + pm.getSize() + "',";
            qtyJson += "QHQty:'" + pm.getQHQty() + "',";
            qtyJson += "ZHQty:'" + pm.getTKQty() + "'";
            qtyJson += "},";
//            }
//            }
        }
        if (qtyJson.length() > 3) {
            qtyJson = qtyJson.substring(0, qtyJson.length() - 1);
        } else {
            throw new Exception("请追单至少一个SKU");
        }
        qtyJson += "]";
        params.put("qtySizeJson", qtyJson);

        httpPost("buyertool/BuyerV2/submit4Chasing", params, PublicData.getCookie(context));
    }

    /**
     * 退款
     */
    public static void submit4Refund(Context context, int id, int qsid, List<ProductModel> products, String warehouse, String content) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id + "");
        params.put("qsid", qsid + "");
        params.put("warehouse", warehouse);
        params.put("content", content);
        String qtyJson = "[";
        for (ProductModel pm : products
                ) {
            qtyJson += "{";
            qtyJson += "color:'" + pm.getColor() + "',";
            qtyJson += "size:'" + pm.getSize() + "',";
            qtyJson += "QHQty:'" + pm.getQHQty() + "',";
            qtyJson += "TKQty:'" + pm.getTKQty() + "'";
            qtyJson += "},";
        }
        if (qtyJson.length() > 3) {
            qtyJson = qtyJson.substring(0, qtyJson.length() - 1);
        } else {
            throw new Exception("请退款至少一个SKU");
        }
        qtyJson += "]";
        params.put("qtySizeJson", qtyJson);

        httpPost("buyertool/BuyerV2/submit4Refund", params, PublicData.getCookie(context));
    }
    /**
     * 申请入库超时
     */
    public  static String  SubmitStorageTimeApply(Context context, long id, int qsid,String closeTime ,ShopItemListModel.ReasonListBean reasonListBean) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id + "");
        params.put("qsid", qsid + "");
        params.put("closeTime", closeTime+"");
        if (reasonListBean!=null){
            params.put("ReasonID", reasonListBean.getIDX()+"");
            params.put("ReasonText", reasonListBean.getText()+"");
        }
       String json = httpPost("buyertool/purchase/SubmitStorageTimeApply", params, PublicData.getCookie(context));
        return json;
    }
    /**
     * @description 追货
     * @created 2014-12-12 下午1:54:27
     * @author ZZB
     */
    public static void chasingGoodsItems(Context context, String ids) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("ids", ids);
        httpGet("buyertool/buyerV2/submit4chasingbatch", params, PublicData.getCookie(context));
    }

}
