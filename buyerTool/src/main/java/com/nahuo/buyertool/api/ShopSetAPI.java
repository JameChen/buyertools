package com.nahuo.buyertool.api;

import android.content.Context;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.nahuo.buyertool.BWApplication;
import com.nahuo.buyertool.Bean.AttributeBean;
import com.nahuo.buyertool.Bean.CategoryBean;
import com.nahuo.buyertool.Bean.NotiBean;
import com.nahuo.buyertool.Bean.SearchStallBean;
import com.nahuo.buyertool.Bean.StallsBean;
import com.nahuo.buyertool.Bean.UploadBean;
import com.nahuo.buyertool.common.Constant;
import com.nahuo.buyertool.common.FourListBean;
import com.nahuo.buyertool.common.SafeUtils;
import com.nahuo.buyertool.common.StringUtils;
import com.nahuo.buyertool.model.ItemShopCategory;
import com.nahuo.buyertool.model.PublicData;
import com.nahuo.buyertool.model.ShopCustomInfo;
import com.nahuo.buyertool.model.ShopInfoModel;
import com.nahuo.library.helper.GsonHelper;

import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.nahuo.buyertool.api.HttpUtils.httpGet;

public class ShopSetAPI {

    private static final String TAG = "ShopSetAPI";
    private static ShopSetAPI instance = null;

    /**
     * 单例
     */
    public static ShopSetAPI getInstance() {
        if (instance == null) {
            instance = new ShopSetAPI();
        }
        return instance;
    }

    /**
     * @description 获取通知信息
     * @created 2014-12-12 下午1:54:27
     * @author ZZB
     */
    public static NotiBean getNotifiInfo(Context context) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        String json = httpGet("buyertool/BuyerV2/getquotainfo", params, PublicData.getCookie(context));
        Log.d("getquotainfo", json);
        NotiBean bean = GsonHelper.jsonToObject(json, NotiBean.class);
        return bean;
    }

    /**
     * @description获取全部档口
     * @created 2014-12-12 下午1:54:27
     * @author ZZB
     */
    public static List<StallsBean> getAllStallsForSearch(Context context) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        String json = httpGet("buyertool/Purchase/GetAllStallsForSearch", params, PublicData.getCookie(context));
        Log.d("getAllStallsForSearch", json);
        List<StallsBean> bean = GsonHelper.jsonToObject(json, new TypeToken<List<StallsBean>>() {
        });
        return bean;
    }

    /**
     * @description获取商品分类
     * @created 2014-12-12 下午1:54:27
     * @author ZZB
     */
    public static List<CategoryBean> getCategoryList(Context context) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        String json = httpGet("/pinhuoadmin/item/GetCategoryList", params, PublicData.getCookie(context));
        Log.d("GetCategoryList", json);
        List<CategoryBean> bean = GsonHelper.jsonToObject(json, new TypeToken<List<CategoryBean>>() {
        });
        return bean;
    }

    /**
     * @description获取材质，年龄季节风格
     * @created 2014-12-12 下午1:54:27
     * @author ZZB
     */
    public static List<FourListBean> getFourList(Context context, int type) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        String json = "";
        List<FourListBean> bean;
        switch (type) {
            case Constant.FourList.Type_GetAgeList:
                json = httpGet("/pinhuoadmin/Item/GetAgeList", params, PublicData.getCookie(context));
                bean = GsonHelper.jsonToObject(json, new TypeToken<List<FourListBean>>() {
                });
                return bean;
            case Constant.FourList.Type_MaterialList:
                json = httpGet("/pinhuoadmin/Item/GetMaterialList", params, PublicData.getCookie(context));
                bean = GsonHelper.jsonToObject(json, new TypeToken<List<FourListBean>>() {
                });
                return bean;
            case Constant.FourList.Type_SeasonList:
                json = httpGet("/pinhuoadmin/Item/GetSeasonList", params, PublicData.getCookie(context));
                bean = GsonHelper.jsonToObject(json, new TypeToken<List<FourListBean>>() {
                });
                return bean;
            case Constant.FourList.Type_StyleList:
                json = httpGet("/pinhuoadmin/Item/GetStyleList", params, PublicData.getCookie(context));
                bean = GsonHelper.jsonToObject(json, new TypeToken<List<FourListBean>>() {
                });
                return bean;
            default:
                return null;
        }
    }

    /**
     * @description获取商品属性
     * @created 2014-12-12 下午1:54:27
     * @author ZZB
     */
    public static List<AttributeBean> getPropertyList(Context context, int cid) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("cid", cid + "");
        String json = httpGet("/pinhuoadmin/item/GetPropertyList", params, PublicData.getCookie(context));
        Log.d("GetPropertyList", json);
        List<AttributeBean> bean = GsonHelper.jsonToObject(json, new TypeToken<List<AttributeBean>>() {
        });
        return bean;
    }
//    /**
//     * @description获取拓展商品属性
//     * @created 2014-12-12 下午1:54:27
//     * @author ZZB
//     */
//    public static List<UploadBean.ExtendPropertyTypeListBean> getExtendPropertyList(Context context, int typeId) throws Exception {
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("typeId", typeId + "");
//        String json = httpGet("/pinhuoadmin/Item/GetExtendPropertyList", params, PublicData.getCookie(context));
//        Log.d("GetExtendPropertyList", json);
//        List<UploadBean.ExtendPropertyTypeListBean> bean = GsonHelper.jsonToObject(json, new TypeToken<List<UploadBean.ExtendPropertyTypeListBean>>() {
//        });
//        return bean;
//    }
    /**
     * @description获取拓展商品属性
     * @created 2014-12-12 下午1:54:27
     * @author ZZB
     */
    public static List<UploadBean.ExtendPropertyTypeListV2Bean>getExtendPropertyListV2(Context context, int typeId) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("typeId", typeId + "");
        String json = httpGet("/pinhuoadmin/Item/GetExtendPropertyListV2", params, PublicData.getCookie(context));
        Log.d("GetExtendPropertyList", json);
        List<UploadBean.ExtendPropertyTypeListV2Bean> bean = GsonHelper.jsonToObject(json, new TypeToken<List<UploadBean.ExtendPropertyTypeListV2Bean>>() {
        });
        return bean;
    }
    /**
     * @description 显示或者隐藏商品
     * @created 2014-12-12 下午1:54:27
     * @author ZZB
     */
    public static void updateItemHide(Context context, String itemIDS, boolean isHide) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("itemIDS", itemIDS);
        params.put("isHide", isHide + "");
        httpGet("shop/UpdateItemHide", params, PublicData.getCookie(context));
    }

    /**
     * @description 下架商品
     * @created 2014-12-12 下午1:54:27
     * @author ZZB
     */
    public static void offShelfItems(Context context, String ids) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("ids", ids);
        httpGet("shop/agent/offshelfitems", params, PublicData.getCookie(context));
    }

    /**
     * @description 上架商品
     * @created 2014-12-12 下午1:54:27
     * @author ZZB
     */
    public static void onShelfItems(Context context, String ids) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("ids", ids);
        httpGet("shop/agent/OnShelfItems", params, PublicData.getCookie(context));
    }

    /**
     * @description 提审商品
     * @created 2014-12-12 下午1:54:27
     * @author ZZB
     */


    public static void arraignmentOnItems(Context context, String ids) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("ids", ids);
        httpGet("shop/AddCheckItemRecord", params, PublicData.getCookie(context));
    }

    /**
     * @description 编辑商品
     * @created 2014-12-12 下午1:54:27
     * pinhuoitem/GetItemForEdit
     * @author ZZB
     */

    public static UploadBean getItemForEdit(Context context, String itemIDS) throws Exception {
        UploadBean bean;
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("id", itemIDS);
            String json = HttpUtils.httpGet("pinhuoadmin/getitemforedit", params, PublicData.getCookie(context));
            Log.d(TAG, json.toString());
            bean = GsonHelper.jsonToObject(json, UploadBean.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG,
                    "getShopInfo", ex.getMessage()));
            ex.printStackTrace();
            throw ex;
        }
        return bean;
    }

    /**
     * @description 修改所有商品
     * @created 2014-12-12 下午1:54:27
     * pinhuoitem/GetItemForEdit
     * @author ZZB
     */

    public static void updateMyItemBatch(Context context, String itemIDS, String oldStr, String newStr, String waitDays, String groupDealCount,String newQsFlag ) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("ids", itemIDS);
        params.put("oldStr", oldStr);
        params.put("newStr", newStr);
        params.put("waitDays", waitDays);
        params.put("groupDealCount", groupDealCount);
        params.put("newQsFlag",newQsFlag );
        String json = HttpUtils.httpPost("shop/agent/UpdateMyItemBatch", params, PublicData.getCookie(context));
        Log.d(TAG, json.toString());
        //bean = GsonHelper.jsonToObject(json, UploadBean.class);
    }

    /**
     * @description 搜索档口
     * @created 2014-12-12 下午1:54:27
     * @author ZZB
     */
    public static SearchStallBean getStallsByKey(Context context, String stallsKey, int marketID, int floorID) throws Exception {
        SearchStallBean bean = null;
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("stallsKey", stallsKey);
            params.put("marketID", marketID + "");
            params.put("floorID", floorID + "");
            String json = httpGet("buyertool/purchase/GetStallsByKey", params, PublicData.getCookie(context));
            Log.d(TAG, json.toString());
            bean = GsonHelper.jsonToObject(json, SearchStallBean.class);
        } catch (Exception ex) {
            Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG,
                    "getShopInfo", ex.getMessage()));
            ex.printStackTrace();
            throw ex;
        }
        return bean;
    }

    /**
     * @description 复制商品
     * @created 2014-12-12 下午1:54:27
     * @author ZZB
     */
    public static void copyItems(Context context, String ids, String oldStr, String newStr) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("ids", ids);
        params.put("oldStr", oldStr);
        params.put("newStr", newStr);
        httpGet("shop/agent/item/RetSaleBatch", params, PublicData.getCookie(context));
    }

    /**
     * @description 更换商品的店铺分类
     * @created 2015-3-23 下午6:11:43
     * @author ZZB
     */
    public static void changeItemsShopCategory(Context context, List<Integer> ids, int oldCatId, int newCatId) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        String idsStr = "";
        for (Integer id : ids) {
            idsStr = StringUtils.append(idsStr, id + "", ",");
        }
        params.put("agentItemIDS", idsStr);
        params.put("oldCatID", oldCatId + "");
        params.put("newCatID", newCatId + "");
        httpGet("shop/agent/itemcat/BatchChangeCat", params, PublicData.getCookie(context));
    }

    /**
     * 上传图片到服务器
     *
     * @param shopId   店铺ID
     * @param fileName 文件名，上传到upyun的文件名
     * @param imgUrl   本地图片文件路径
     * @return 返回图片在服务器上的路径
     * @author Chiva Liang
     */
    public String uploadImage(String shopId, String fileName, String imgUrl)
            throws Exception {
        String serverPath = "";
//		try {
//			serverPath = UpYunAPI.uploadImage("shop", shopId, fileName,
//					PublicData.UPYUN_BUCKET, PublicData.UPYUN_API_KEY, imgUrl);
//		} catch (Exception ex) {
//			Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG,
//					"uploadImage", ex.getMessage()));
//			ex.printStackTrace();
//			throw ex;
//		}
        return serverPath;
    }
    /**
     * @description 更新店铺分类
     * @created 2015-3-18 下午2:13:00
     * @author ZZB
     *//*
    public static void updateItemShopCategory(Context context, ItemShopCategory cat) throws Exception{
	    Map<String, String> params = new HashMap<String, String>();
        params.put("id", cat.getId() + "");
        params.put("name", cat.getName());
        HttpUtils.httpGet("shop/agent/itemcat/Update", params, PublicData.getCookie(context));
	}
	*//**
     * @description 更新商品排序
     * @created 2015-3-18 上午11:36:01
     * @author ZZB
     *//*
    public static void updateItemShopCategorySort(Context context, List<ItemShopCategory> cats) throws Exception{
	    Map<String, String> params = new HashMap<String, String>();
	    StringBuilder sort = new StringBuilder();
	    int sortId = cats.size();
	    for(ItemShopCategory cat : cats){
	        sort.append(cat.getId()).append(":").append(sortId).append(",");
	        sortId--;
	    }
        params.put("sort", sort.toString());
        HttpUtils.httpGet("shop/agent/itemcat/updatesort", params, PublicData.getCookie(context));
	}
	*//**
     * @description 删除店铺商品分类
     * @created 2015-3-17 下午6:23:40
     * @author ZZB
     *//*
    public static void deleteItemShopCategories(Context context, List<ItemShopCategory> cats) throws Exception{
        Map<String, String> params = new HashMap<String, String>();
        StringBuilder ids = new StringBuilder();
        for(ItemShopCategory cat : cats){
            ids.append(cat.getId()).append(",");
        }
        params.put("ids", ids.toString()); 
        HttpUtils.httpGet("shop/agent/itemcat/BatchDelete", params, PublicData.getCookie(context));
	}
	*/

    /**
     * @description 添加店铺商品分类
     * @created 2015-3-17 上午11:19:22
     * @author ZZB
     */
    public static int createItemShopCategory(Context context, ItemShopCategory category) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", category.getName());
        params.put("sort", category.getSort() + "");
        String json = httpGet("shop/agent/itemcat/add", params, PublicData.getCookie(context));
        JSONObject jObj = new JSONObject(json);
        int id = jObj.getInt("ID");
        return id;
    }

    /**
     * @description 获取店铺的商品分类
     * @created 2015-3-17 上午10:25:48
     * @author ZZB
     */
    public static List<ItemShopCategory> getItemShopCategory(Context context) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        String json = httpGet("shop/agent/itemcat/getitemcats", params, PublicData.getCookie(context));
        List<ItemShopCategory> cats = GsonHelper.jsonToObject(json, new TypeToken<ArrayList<ItemShopCategory>>() {
        });
        return cats;
    }
    /**
     * @description 退出诚保
     * @created 2015-3-5 上午11:53:45
     * @author ZZB
     *//*
    public static void quitCredit(Context context, int creditItemId) throws Exception{
	    Map<String, String> params = new HashMap<String, String>();
        params.put("creditItemID", creditItemId + "");
        HttpUtils.httpGet("shop/shop/ExitCreditItem", params, PublicData.getCookie(context));
	}
	*//**
     * @description 加入某诚保
     * @created 2015-3-5 上午11:43:51
     * @author ZZB
     *//*
    public static void joinCredit(Context context, int creditItemId) throws Exception{
	    Map<String, String> params = new HashMap<String, String>();
        params.put("creditItemID", creditItemId + "");
        HttpUtils.httpGet("shop/shop/JoinCreditItem", params, PublicData.getCookie(context));
	}
	*//**
     * @description 获取某诚保申请状态id
     * @param creditItemId {@link Const.CreditItemId}
     * @created 2015-3-4 下午4:50:41
     * @author ZZB
     *//*
    public static CreditJoinStatuModel getCreditItemStatu(Context context, int creditItemId) throws Exception{
	    Map<String, String> params = new HashMap<String, String>();
        params.put("creditItemID", creditItemId + "");
        String json = HttpUtils.httpGet("shop/shop/GetCreditItem", params, PublicData.getCookie(context));
        CreditJoinStatuModel statu = GsonHelper.jsonToObject(json, CreditJoinStatuModel.class);
        return statu;
	}
	*//**
     * @description 获取商品分类
     * @created 2015-3-2 下午5:35:30
     * @author ZZB
     *//*
    public static String getItemSysCategories(Context context) throws Exception{
	    Map<String, String> params = new HashMap<String, String>();
        params.put("readAll", "true");
	    String json = HttpUtils.httpGet("shop/item/GetItemSysParentClassAndStyle", params);
	    return json;
	}

	*//**
     * @description 获取粉丝列表
     * @created 2015-1-27 下午6:07:16
     * @author ZZB
     *//*
    public static List<Fan> getFansList(Context context, int page, int size) throws Exception{
	    Map<String, String> params = new HashMap<String, String>();
        params.put("pageIndex", page + "");
        params.put("pageSize", size + "");
        String json = HttpUtils.httpGet("shop/agent/shop/GetMyFansList", params, PublicData.getCookie(context));
        JSONObject repObj = new JSONObject(json);
        
        String fansJson = repObj.getString("Datas");
        List<Fan> fans = GsonHelper.jsonToObject(fansJson, new TypeToken<List<Fan>>() {});
        return fans;
	}
	*//**
     * @description 关注店铺
     * @created 2015-1-27 下午5:38:24
     * @author ZZB
     *//*
	public static void followShop(Context context, int shopId) throws Exception{
	    Map<String, String> params = new HashMap<String, String>();
        params.put("shopID", shopId + "");
        HttpUtils.httpPost("shop/agent/shop/AddShopToMyFavorite", params, SpManager.getCookie(context));
	}
	*//**
     * @description 取消关注店铺
     * @created 2015-1-27 下午5:50:21
     * @author ZZB
     *//*
	public static void unFollowShop(Context context, int shopId) throws Exception{
	    Map<String, String> params = new HashMap<String, String>();
        params.put("shopID", shopId + "");
        HttpUtils.httpPost("shop/agent/shop/DeleteShopFromMyFavorite", params, SpManager.getCookie(context));
	}
	*//**
     * @description 修改店铺名
     * @created 2014-11-25 下午5:13:00
     * @author ZZB
     *//*
	public static void updateShopName(Context context, String shopName) throws Exception{
	    Map<String, String> params = new HashMap<String, String>();
	    params.put("name", shopName);
	    HttpUtils.httpPost("shop/shop/updateshopname", params, SpManager.getCookie(context));
	}
	*//**
     * @description 设置运费
     * @created 2014-9-24 上午11:11:51
     * @author ZZB
     *//*
	public static void setPostFee(Context context, JPostFee fee) throws Exception{
	    Map<String, String> params = new HashMap<String, String>();
	    params.put("PostFeeTypeID", fee.getPostFeeTypeID() + "");
	    params.put("DefaultPostFee", fee.getDefaultPostFee() + "");
	    params.put("FreePostFeeAmount", fee.getFreePostFeeAmount() + "");
	    HttpUtils.httpPostWithJson("shop/shop/SetPostFee", params, PublicData.getCookie(context));
	}
	*//**
     * @description 获取运费
     * @created 2014-9-24 上午11:11:58
     * @author ZZB
     *//*
	public static JPostFee getPostFee(Context context) throws Exception{
	    String json = HttpUtils.httpGet("shop/shop/GetPostFee", new HashMap<String, String>(), PublicData.getCookie(context));
	    Gson gson = new Gson();
	    JPostFee obj = gson.fromJson(json, JPostFee.class);
	    return obj;
	    
	}
	*//**
     * @description 删除收件地址
     * @created 2014-9-11 下午3:02:12
     * @author ZZB
     *//*
	public static void deleteAddress(Context context, Address address) throws Exception{
	    String cookie = SpManager.getCookie(context);
        Map<String, String> params = new TreeMap<String, String>();
        params.put("id", address.getId() + "");
        HttpUtils.httpPost("shop/address/delete", params, cookie);
        if(address.isDefault()){//把本地缓存的默认地址设为空
            UserInfoProvider.setDefaultAddress(context, SpManager.getUserId(context), "");
        }
	}
	*//**
     * @description 更新地址
     * @created 2014-9-11 下午2:14:46
     * @author ZZB
     * @throws Exception
     *//*
	public static void updateAddress(Context context, Address address) throws Exception{
	    String cookie = SpManager.getCookie(context);
        Map<String, String> params = new TreeMap<String, String>();
        params.put("id", address.getId() + "");
        params.put("realName", address.getUserName());
        params.put("mobile", address.getPhone());
        params.put("postCode", address.getPostCode());
        params.put("areaId", address.getArea().getId() + "");
        params.put("address", address.getDetailAddress());
        params.put("isDefault", address.isDefault() + "");
        HttpUtils.httpPost("shop/address/update", params, cookie);
        if(address.isDefault()){//缓存默认地址到本地
            UserInfoProvider.setDefaultAddress(context, SpManager.getUserId(context), address.getDetailAddress());
        }
	}
	*//**
     * @description 添加收件地址
     * @created 2014-9-11 下午1:42:01
     * @author ZZB
     * @throws Exception
     *//*
	public static int addAddress(Context context, Address address) throws Exception{
	    String cookie = SpManager.getCookie(context);
	    Map<String, String> params = new TreeMap<String, String>();
	    params.put("realName", address.getUserName());
	    params.put("mobile", address.getPhone());
	    params.put("postCode", address.getPostCode());
	    params.put("areaId", address.getArea().getId() + "");
	    params.put("address", address.getDetailAddress());
	    params.put("isDefault", address.isDefault() + "");
	    String json = HttpUtils.httpPost("shop/address/add", params, cookie);
	    JAddress add = new Gson().fromJson(json, JAddress.class);
	    if(address.isDefault()){//缓存默认地址到本地
	        UserInfoProvider.setDefaultAddress(context, SpManager.getUserId(context), address.getDetailAddress());
	    }
	    return add.getID();
	}
	*//**
     * @description 获取收件地址
     * @created 2014-9-11 上午11:03:29
     * @author ZZB
     * @throws Exception
     *//*
	public static ArrayList<Address> getAddresses(Context context) throws Exception{
	    String cookie = SpManager.getCookie(context);
	    ArrayList<Address> addresses = new ArrayList<Address>();
	    String json = HttpUtils.httpGet("shop/address/GetAddresses", new TreeMap<String, String>(), cookie);
	    Gson gson = new Gson();
	    List<JAddress> jList = gson.fromJson(json, new TypeToken<List<JAddress>>(){}.getType());
	    AreaDao dao = new AreaDao(context);
	    for(JAddress ja : jList){
	        Address add = new Address();
	        add.setId(ja.getID());
	        add.setUserName(ja.getRealName());
	        add.setPostCode(ja.getPostCode());
	        add.setPhone(ja.getMobile());
	        add.setDetailAddress(ja.getAddress());
	        add.setDefault(ja.isIsDefault());
	        if(ja.isIsDefault()){
	        	UserInfoProvider.setDefaultAddress(context, SpManager.getUserId(context), ja.getAddress());
	        }
	        Area area = dao.getArea(ja.getAreaID());
	        Area city = dao.getArea(area.getParentId());
			Area province;
			if (city==null)
			{
				province = dao.getArea(area.getId());
			}
			else {
				province = dao.getArea(city.getParentId());
			}
	        add.setArea(area);
	        add.setCity(city);
	        add.setProvince(province);
	        addresses.add(add);
	    }
	    return addresses;
	}
	*/

    /**
     * 获取店铺信息
     *
     * @param cookie 当前登录用户cookie值
     */
    public ShopInfoModel getShopInfo(String cookie) throws Exception {
        ShopInfoModel shopInfo;
        try {
            String json = HttpUtils.httpPost("shop/shop/getshopinfo",
                    new HashMap<String, String>(), cookie);
            Log.i(TAG, "Json：" + json);
            shopInfo = GsonHelper.jsonToObject(json, ShopInfoModel.class);
        } catch (Exception ex) {
            Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG,
                    "getShopInfo", ex.getMessage()));
            ex.printStackTrace();
            throw ex;
        }
        return shopInfo;
    }


    /**
     * 获取店铺信息
     * shop/shop/GetShopInfoById
     *
     * @param cookie 当前登录用户cookie值
     */
    public static ShopInfoModel getShopInfoByShopID(String shopid, String cookie) throws Exception {
        ShopInfoModel shopInfo;
        try {
            TreeMap<String, String> params = new TreeMap<String, String>();
            params.put("shopId", shopid);
            SafeUtils.genCommonSinParams(BWApplication.getInstance(), params);
            String json = HttpUtils.httpPost("shop/shop/GetShopInfoById", params, cookie);
            Log.i(TAG, "Json：" + json);
            shopInfo = GsonHelper.jsonToObject(json, ShopInfoModel.class);
        } catch (Exception ex) {
            Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG,
                    "getShopInfoByShopID", ex.getMessage()));
            ex.printStackTrace();
            throw ex;
        }
        return shopInfo;
    }

    /**
     * 获取店铺信息
     *
     * @param cookie 当前登录用户cookie值
     */
    public static ShopInfoModel getShopInfoByDomain(String domain, String cookie) throws Exception {
        ShopInfoModel shopInfo;
        try {
            TreeMap<String, String> params = new TreeMap<String, String>();
            params.put("domain", domain);
            SafeUtils.genCommonSinParams(BWApplication.getInstance(), params);
            String json = HttpUtils.httpPost("shop/shop/GetShopInfoByDomain", params, cookie);
            Log.i(TAG, "Json：" + json);
            shopInfo = GsonHelper.jsonToObject(json, ShopInfoModel.class);
        } catch (Exception ex) {
            Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG,
                    "getShopInfo", ex.getMessage()));
            ex.printStackTrace();
            throw ex;
        }
        return shopInfo;
    }

    /**
     * 获取店铺信息
     *
     * @param cookie 当前登录用户cookie值
     */
    public static String getShopCustomInfoByUserID(String userID, String cookie) throws Exception {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("userID", userID);
        SafeUtils.genCommonSinParams(BWApplication.getInstance(), params);
        String json = HttpUtils.httpPost("shop/agent/GetShopCustomInfoByUserID", params, cookie);
        Log.i(TAG, "Json：" + json);
        return json;
    }

    /**
     * @description 获取店铺信息
     * @created 2015-3-4 上午11:29:47
     * @author ZZB
     */
    public static ShopCustomInfo getShopCustomInfoByUserId(Context context, int userId) throws Exception {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("userID", userId + "");
        SafeUtils.genCommonSinParams(BWApplication.getInstance(), params);
        String json = HttpUtils.httpPost("shop/agent/GetShopCustomInfoByUserID", params, PublicData.getCookie(context));
        Log.i(TAG, "Json：" + json);
        ShopCustomInfo shopInfo = GsonHelper.jsonToObject(json, ShopCustomInfo.class);
        return shopInfo;
    }

    /**
     * 根据用户ID获取店铺信息
     *
     * @param cookie
     *            当前登录用户cookie值
     * *//*
	public ShopInfoModel getShopInfoWithUserID(String userID, String cookie) throws Exception {
		ShopInfoModel shopInfo;
		try {
			TreeMap<String, String> params = new TreeMap<String, String>();
			params.put("userId", userID);
			SafeUtils.genCommonSinParams(BWApplication.getInstance(), params);
			String json = HttpUtils.httpPost("shop/shop/getshopinfobyuid", params, cookie);
			Log.i(TAG, "Json：" + json);
			shopInfo = GsonHelper.jsonToObject(json, ShopInfoModel.class);
		} catch (Exception ex) {
			Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG,
					"getShopInfo", ex.getMessage()));
			ex.printStackTrace();
			throw ex;
		}
		return shopInfo;
	}

    
	*//**
     * 完善店铺资料
     *
     * @author Chiva Liang
     *
     * @param shopInfo
     *            店铺实体
     * @param cookie
     *            cookie值
     * *//*
	public boolean updateShopInfo(ShopInfoModel shopInfo, String cookie)
			throws Exception {
		boolean result = false;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("name", shopInfo.getName());
			params.put("mobile", shopInfo.getMobile());
			params.put("qq", shopInfo.getQQ());
			params.put("addressStreet", shopInfo.getAddressStreet());
			params.put("logo", shopInfo.getLogo());
			String json = HttpUtils.httpPost("shop/shop/updateshopinfo",
					params, cookie);
			Log.i(TAG, "Json：" + json);
			result = true;
		} catch (Exception ex) {
			Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG,
					"updateShopInfo", ex.getMessage()));
			ex.printStackTrace();
			throw ex;
		}
		return result;
	}

	*//**
     * 更新店招
     *
     * @author pengjun
     *
     * @param banner
     *            店招地址
     * @param cookie
     *            cookie值
     * *//*
	public boolean updateBanner(String banner, String cookie) throws Exception {
		boolean result = false;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("banner", banner);
			String json = HttpUtils.httpPost("shop/agent/SetShopBanner", params,
					cookie);
			Log.i(TAG, "Json：" + json);
			result = true;
		} catch (Exception ex) {
			Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG,
					"updateBanner", ex.getMessage()));
			ex.printStackTrace();
			throw ex;
		}
		return result;
	}
	
	*/

    /**
     * 更新店标
     *
     * @param logo   店铺实体
     * @param cookie cookie值
     * @author pengjun
     */
    public boolean updateLogo(String logo, String cookie) throws Exception {
        boolean result = false;
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("logo", logo);
            String json = HttpUtils.httpPost("shop/shop/updatelogo", params,
                    cookie);
            Log.i(TAG, "Json：" + json);
            result = true;
        } catch (Exception ex) {
            Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG,
                    "updateShopInfo", ex.getMessage()));
            ex.printStackTrace();
            throw ex;
        }
        return result;
    }

    /**
     * 根据键获取值，用于店铺个性化设置
     *
     * @author Chiva Liang
     *
     * @param shopId
     *            店铺ID
     * @param key
     *            键
     * @param cookie
     *            cookie值
     * *//*
	public String getValue(long shopId, String key, String cookie)
			throws Exception {
		String value = "";
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("shopId", String.valueOf(shopId));
			params.put("key", key);
			String json = HttpUtils.httpPost("shop/shop/getvalue", params,
					cookie);
			Log.i(TAG, "Json：" + json);
			value = GsonHelper.jsonToObject(json, String.class);
		} catch (Exception ex) {
			Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG,
					"getValue", ex.getMessage()));
			ex.printStackTrace();
			throw ex;
		}
		return value;
	}

	*//**
     * 保存值，用于店铺个性化设置
     *
     * @author Chiva Liang
     *
     * @param key
     *            键
     * @param value
     *            值
     * @param cookie
     *            cookie值
     * *//*
	public boolean saveValue(String key, String value, String cookie)
			throws Exception {
		boolean result = false;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("key", key);
			params.put("value", value);
			value = HttpUtils.httpPost("shop/shop/savevalue", params, cookie);
			Log.i(TAG, "Json：" + value);
			result = true;
		} catch (Exception ex) {
			Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG,
					"saveValue", ex.getMessage()));
			ex.printStackTrace();
			throw ex;
		}
		return result;
	}


	
	
	
	
	//======================================ContactAPI===============================================
	*//**
     * 检查联系方式是否设置
     *
     * @author pengjun
     *
     * @param cookie
     *            cookie值
     * *//*
	public List<ContactModel> GetContactGroup(String cookie) throws Exception {

		List<ContactModel> data;
		try {
			Map<String, String> params = new HashMap<String, String>();

			String json = HttpUtils.httpPost("shop/shop/GetContactGroup", params,
					cookie);
			ContactResultModel resultData = GsonHelper
					.jsonToObject(json,
							ContactResultModel.class);
			data = resultData.getDatas();
		} catch (Exception ex) {
			Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG,
					"GetContactGroup", ex.getMessage()));
			ex.printStackTrace();
			throw ex;
		}
		return data;
		
	}
	
	*//**
     * 检查联系方式是否设置
     *
     * @author pengjun
     *
     * @param cookie
     *            cookie值
     * *//*
	public List<ContactModel> GetContactInfoList(int typeID, String cookie) throws Exception {

		List<ContactModel> data;
		try {
			Map<String, String> params = new HashMap<String, String>();
			switch (typeID) {
			case 1:
				params.put("typeCode", "Mobile");
				break;
			case 2:
				params.put("typeCode", "QQ");
				break;
			case 3:
				params.put("typeCode", "WeiXin");
				break;
			case 4:
				params.put("typeCode", "Email");
				break;
			default:
				break;
			}
			params.put("isEnabled", "1");
			params.put("pageIndex", "1");
			params.put("pageSize", "0");//读取全部数据，为0表示所有
			String json = HttpUtils.httpPost("shop/shop/GetContactInfoList", params,
					cookie);
			ContactResultModel resultData = GsonHelper
					.jsonToObject(json,
							ContactResultModel.class);
			data = resultData.getDatas();
		} catch (Exception ex) {
			Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG,
					"GetContactInfoList", ex.getMessage()));
			ex.printStackTrace();
			throw ex;
		}
		return data;
		
	}
	
	*//**
     * 获取存在代理数据库中的店铺信息
     *
     * @author pengjun
     *
     * @param cookie
     *            cookie值
     * *//*
	public String GetAgentShopInfo(String cookie) throws Exception {

		try {
			String json = HttpUtils.httpPost("shop/agent/GetShopInfo", new HashMap<String, String>(),
					cookie);
return json;
		} catch (Exception ex) {
			Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG,
					"GetContactInfoList", ex.getMessage()));
			ex.printStackTrace();
			throw ex;
		}
		
	}

	*//**
     * 设置存在代理数据库中的店铺信息
     *
     * @author pengjun
     *
     * @param address
     *            地址
     * @param cookie
     *            cookie值
     * *//*
	public boolean SetShopInfo(String address, String cookie)
			throws Exception {
		boolean result = false;
		try {
			Map<String, String> params = new HashMap<String, String>();
				params.put("address", address);
			String returnValue = HttpUtils.httpPost("shop/agent/setShopInfo", params, cookie);
			Log.i(TAG, "Json：" + returnValue);
			result = true;
		} catch (Exception ex) {
			Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG,
					"AddContact", ex.getMessage()));
			ex.printStackTrace();
			throw ex;
		}
		return result;
	}

	*//**
     * 添加联系方式
     *
     * @author pengjun
     *
     * @param typeID
     *            联系方式类型
     * @param name
     *            名称
     * @param content
     *            值
     * @param cookie
     *            cookie值
     * *//*
	public boolean AddContact(int typeID, String name, String content, String cookie)
			throws Exception {
		boolean result = false;
		try {
			Map<String, String> params = new HashMap<String, String>();
			switch (typeID) {
			case 1:
				params.put("typeCode", "Mobile");
				break;
			case 2:
				params.put("typeCode", "QQ");
				break;
			case 3:
				params.put("typeCode", "WeiXin");
				break;
			case 4:
				params.put("typeCode", "Email");
				break;
			default:
				break;
			}
			params.put("name", name);
			params.put("content", content);
			String returnValue = HttpUtils.httpPost("shop/shop/AddContactInfo", params, cookie);
			Log.i(TAG, "Json：" + returnValue);
			result = true;
		} catch (Exception ex) {
			Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG,
					"AddContact", ex.getMessage()));
			ex.printStackTrace();
			throw ex;
		}
		return result;
	}

	*//**
     * 修改联系方式
     *
     * @author pengjun
     *
     * @param ID
     *            联系方式ID
     * @param name
     *            名称
     * @param content
     *            值
     * @param cookie
     *            cookie值
     * *//*
	public boolean UpdateContact(String ID, String name, String content, String cookie)
			throws Exception {
		boolean result = false;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("ID", ID);
			params.put("name", name);
			params.put("content", content);
			String returnValue = HttpUtils.httpPost("shop/shop/UpdateContactInfo", params, cookie);
			Log.i(TAG, "Json：" + returnValue);
			result = true;
		} catch (Exception ex) {
			Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG,
					"updateContact", ex.getMessage()));
			ex.printStackTrace();
			throw ex;
		}
		return result;
	}

	*//**
     * 删除联系方式
     *
     * @author pengjun
     *
     * @param ID
     *            联系方式ID
     * @param cookie
     *            cookie值
     * *//*
	public boolean DeleteContact(String ID, String cookie)
			throws Exception {
		boolean result = false;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("ID", ID);
			String returnValue = HttpUtils.httpPost("shop/shop/DeleteContactInfo", params, cookie);
			Log.i(TAG, "Json：" + returnValue);
			result = true;
		} catch (Exception ex) {
			Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG,
					"DeleteContact", ex.getMessage()));
			ex.printStackTrace();
			throw ex;
		}
		return result;
	}*/
}
