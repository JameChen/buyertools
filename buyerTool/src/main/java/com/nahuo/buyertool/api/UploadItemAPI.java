package com.nahuo.buyertool.api;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.nahuo.buyer.tool.BuildConfig;
import com.nahuo.buyertool.Bean.UploadBean;
import com.nahuo.buyertool.Bean.UploadRepose;
import com.nahuo.buyertool.common.Const;
import com.nahuo.buyertool.common.FourListBean;
import com.nahuo.buyertool.common.ListUtils;
import com.nahuo.buyertool.common.StringUtils;
import com.nahuo.buyertool.http.OkHttpUtils;
import com.nahuo.buyertool.http.response.PinHuoResponse;
import com.nahuo.buyertool.model.ColorModel;
import com.nahuo.buyertool.model.LabelListModel;
import com.nahuo.buyertool.model.LabelModel;
import com.nahuo.buyertool.model.ProductModel;
import com.nahuo.buyertool.model.PublicData;
import com.nahuo.buyertool.model.ShopItemModel;
import com.nahuo.buyertool.model.SizeModel;
import com.nahuo.buyertool.model.TagModel;
import com.nahuo.buyertool.upyun.UpYunAPI;
import com.nahuo.library.helper.GsonHelper;
import com.nahuo.library.helper.ImageUrlExtends;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nahuo.buyertool.api.HttpUtils.httpPost;

public class UploadItemAPI {

    private static final String TAG = "UploadItemAPI";
    private static UploadItemAPI instance = null;

    /**
     * 单例
     */
    public static UploadItemAPI getInstance() {
        if (instance == null) {
            instance = new UploadItemAPI();
        }
        return instance;
    }

    /**
     * 获取颜色列表
     *
     * @param cookie cookie值
     * @author Alan
     */
    public List<ColorModel> getColors(String cookie) throws Exception {
        List<ColorModel> colorList = null;
        try {
            String json = httpPost("shop/shop/getcolors", new HashMap<String, String>(),
                    cookie);
            Log.i(TAG, "Json：" + json);
            colorList = GsonHelper.jsonToObject(json, new TypeToken<ArrayList<ColorModel>>() {
            });
        } catch (Exception ex) {
            Log.e(TAG,
                    MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG, "getColors", ex.getMessage()));
            ex.printStackTrace();
            throw ex;
        }
        return colorList;
    }

    /**
     * 添加颜色
     *
     * @param color  颜色
     * @param cookie cookie值
     * @return ColorModel 颜色实体对象
     * @author Chiva Liang
     */
    public ColorModel addColor(String color, String cookie) throws Exception {
        ColorModel cm = null;
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("color", color);
            String json = httpPost("shop/shop/addcolor", params, cookie);
            Log.i(TAG, "Json：" + json);
            cm = GsonHelper.jsonToObject(json, ColorModel.class);
        } catch (Exception ex) {
            Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG, "addColor", ex.getMessage()));
            ex.printStackTrace();
            throw ex;
        }
        return cm;
    }


    /**
     * 删除颜色
     *
     * @param ids    颜色ID，多ID情况下用英文逗号隔开
     * @param cookie cookie值
     * @author Chiva Liang
     */
    public boolean deleteColors(String ids, String cookie) throws Exception {
        boolean result = false;
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("ids", ids);
            String json = httpPost("shop/shop/deletecolors", params, cookie);
            Log.i(TAG, "Json：" + json);
            result = true;
        } catch (Exception ex) {
            Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG, "deleteColors",
                    ex.getMessage()));
            ex.printStackTrace();
            throw ex;
        }
        return result;
    }

    /**
     * 获取尺码列表
     *
     * @param cookie cookie值
     * @author Chiva Liang
     */
    public List<SizeModel> getSizes(String cookie) throws Exception {
        List<SizeModel> sizeList = null;
        try {
            String json = httpPost("shop/shop/getsizes", new HashMap<String, String>(),
                    cookie);
            Log.i(TAG, "Json：" + json);
            sizeList = GsonHelper.jsonToObject(json, new TypeToken<ArrayList<SizeModel>>() {
            });
        } catch (Exception ex) {
            Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG, "getSizes", ex.getMessage()));
            ex.printStackTrace();
            throw ex;
        }
        return sizeList;
    }

    /**
     * 添加颜色
     *
     * @param size   尺码
     * @param cookie cookie值
     * @return SizeModel 尺码实体对象
     * @author Chiva Liang
     */
    public SizeModel addSize(String size, String cookie) throws Exception {
        SizeModel cm = null;
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("size", size);
            String json = httpPost("shop/shop/addsize", params, cookie);
            Log.i(TAG, "Json：" + json);
            cm = GsonHelper.jsonToObject(json, SizeModel.class);
        } catch (Exception ex) {
            Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG, "addSize", ex.getMessage()));
            ex.printStackTrace();
            throw ex;
        }
        return cm;
    }

    /**
     * 批量删除尺码
     *
     * @param ids    尺码ID，多ID情况下用英文逗号隔开
     * @param cookie cookie值
     * @author Chiva Liang
     */
    public boolean deleteSizes(String ids, String cookie) throws Exception {
        boolean result = false;
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("ids", ids);
            String json = httpPost("shop/shop/deletesizes", params, cookie);
            Log.i(TAG, "Json：" + json);
            result = true;
        } catch (Exception ex) {
            Log.e(TAG,
                    MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG, "deleteSizes", ex.getMessage()));
            ex.printStackTrace();
            throw ex;
        }
        return result;
    }


    /**
     * 获取标签列表
     *
     * @param cookie cookie值
     * @author Alan
     */
    public List<LabelModel> getLabel(String cookie) throws Exception {
        List<LabelModel> labelList = null;
        try {
            String json = httpPost("shop/agent/GetItemTags", new HashMap<String, String>(),
                    cookie);
            Log.i(TAG, "Json：" + json);
            labelList = GsonHelper.jsonToObject(json, new TypeToken<LabelListModel>() {
            }).getTags();
        } catch (Exception ex) {
            Log.e(TAG,
                    MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG, "getLabel", ex.getMessage()));
            ex.printStackTrace();
            throw ex;
        }
        return labelList;
    }

    /**
     * 添加标签
     *
     * @param color  标签
     * @param cookie cookie值
     * @return LabelModel 标签实体对象
     * @author Alan
     */
    public LabelModel addLabel(String color, String cookie) throws Exception {
        LabelModel cm = null;
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("id", getRandom());
            params.put("name", color);
            String json = httpPost("shop/agent/AddItemTags", params, cookie);
            Log.i(TAG, "Json：" + json);
            cm = GsonHelper.jsonToObject(json, LabelModel.class);
        } catch (Exception ex) {
            Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG, "addLabel", ex.getMessage()));
            ex.printStackTrace();
            throw ex;
        }
        return cm;
    }

    private String getRandom() {
        String time = System.currentTimeMillis() / 1000 + "";//单位秒
        return time.substring(time.length() - 3, time.length());
    }

    /**
     * 删除标签
     *
     * @param ids    标签ID，多ID情况下用英文逗号隔开
     * @param cookie cookie值
     * @author Chiva Liang
     */
    public boolean deleteLabel(String ids, String cookie) throws Exception {
        boolean result = false;
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("ID", ids);
            String json = httpPost("shop/agent/RemoveItemTags", params, cookie);
            Log.i(TAG, "Json：" + json);
            result = true;
        } catch (Exception ex) {
            Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG, "deleteLabel",
                    ex.getMessage()));
            ex.printStackTrace();
            throw ex;
        }
        return result;
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
    public String uploadImage(String shopId, String fileName, String imgUrl) throws Exception {
        String serverPath = "";
        try {
            serverPath = UpYunAPI.uploadImage("item", shopId, fileName,
                    PublicData.UPYUN_BUCKET_ITEM, PublicData.UPYUN_API_KEY_ITEM, imgUrl);
        } catch (Exception ex) {
            Log.e(TAG,
                    MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG, "uploadImage", ex.getMessage()));
            ex.printStackTrace();
            throw ex;
        }
        return serverPath;
    }

    /***
     *
     * 新款上传
     * */
    public int uploadNewItem(UploadBean bean, String cookie) throws Exception {
        String strSaveJson = "";
        int itemId = 0;
        if (bean != null) {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("Name", bean.getName());
            jsonObject.put("Summary", bean.getSummary());
            jsonObject.put("Discount", bean.getDiscount());
            jsonObject.put("Intro", bean.getName());
            jsonObject.put("RetailPrice", bean.getPrice());
            jsonObject.put("Price", bean.getPrice());
            jsonObject.put("IsWaitOrder", true);
            jsonObject.put("MarkUpValue", bean.getMarkUpValue());
            jsonObject.put("Remark", bean.getRemark());
            if (bean.getSupplyInfo() != null) {
                jsonObject.put("WaitDays", bean.getSupplyInfo().getDays());
            }
            jsonObject.put("GroupDealCount", bean.getGroupDealCount());
            if (bean.getStallInfo() != null) {
                if (bean.getStallInfo().getStallID() > 0)
                    jsonObject.put("StallID", bean.getStallInfo().getStallID());
            }
            if (!ListUtils.isEmpty(bean.getCategoryList())) {
                JSONArray jsonArray = new JSONArray();
                for (UploadBean.CategoryListBean categoryListBean : bean.getCategoryList()) {
                    if (categoryListBean.getID() > 0)
                        jsonArray.put(categoryListBean.getID());
                }
                jsonObject.put("CategoryIDS", jsonArray);
            }
            if (!ListUtils.isEmpty(bean.getPropertyList())) {
                JSONArray jsonArray = new JSONArray();
                for (UploadBean.PropertyListBean propertyListBean : bean.getPropertyList()) {
                    if (propertyListBean.getID() > 0)
                        jsonArray.put(propertyListBean.getID());
                }
                jsonObject.put("PropertyIDS", jsonArray);
            }
            if (!ListUtils.isEmpty(bean.getProducts())) {
                JSONArray jsonArray = new JSONArray();
                for (UploadBean.ProductsBean productsBean : bean.getProducts()) {
                    JSONObject pdJsonObject = new JSONObject();
                    pdJsonObject.put("Color", productsBean.getColor());
                    pdJsonObject.put("Stock", productsBean.getStock());
                    pdJsonObject.put("Size", productsBean.getSize());
                    jsonArray.put(pdJsonObject);
                }
                jsonObject.put("Products", jsonArray);
            }
            if (!ListUtils.isEmpty(bean.getTags())) {
                JSONArray jsonArray = new JSONArray();
                for (UploadBean.TagsBean tagsBean : bean.getTags()) {
                    if (tagsBean.getID().contains(".")) {
                        jsonArray.put(tagsBean.getID().split("\\.")[0]);
                    } else {
                        jsonArray.put(tagsBean.getID());
                    }

                }
                jsonObject.put("ItemTagIDS", jsonArray);
            }
            if (!ListUtils.isEmpty(bean.getLocal_pics())) {
                String imgHTMLStr = "", Cover = "";
                imgHTMLStr = "<div id=\"beginAppImgInsertTag\" ></div>";
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < bean.getLocal_pics().size(); i++) {
                    UploadBean.MediaBean mediaBean = bean.getLocal_pics().get(i);
                    if (i == 0) {
                        Cover = mediaBean.getPath();
                    }
                    jsonArray.put(mediaBean.getPath());
                    imgHTMLStr += "<img src=\"" + ImageUrlExtends.getImageUrl(mediaBean.getPath(), 24)
                            + "\" />";
                    imgHTMLStr += "<br/>";
                }
                imgHTMLStr += "<div id=\"endAppImgInsertTag\" ></div>";
                jsonObject.put("Images", jsonArray);
                jsonObject.put("Description", imgHTMLStr);
                jsonObject.put("Cover", Cover);
            }
            if (!ListUtils.isEmpty(bean.getLocal_videos())) {
                JSONArray jsonArray = new JSONArray();
                for (UploadBean.MediaBean mediaBean : bean.getLocal_videos()) {
                    jsonArray.put(mediaBean.getPath());
                }
                jsonObject.put("Videos", jsonArray);
            }
            if (!ListUtils.isEmpty(bean.getColorPics())) {
                JSONArray jsonArray = new JSONArray();
                for (UploadBean.ColorPicsBean mediaBean : bean.getColorPics()) {
                    JSONObject pdJsonObject = new JSONObject();
                    pdJsonObject.put("Color", mediaBean.getColor());
                    pdJsonObject.put("Url", mediaBean.getUrl());
                    if (!TextUtils.isEmpty(mediaBean.getUrl()))
                    jsonArray.put(pdJsonObject);
                }
                if (jsonArray.length()>0)
                jsonObject.put("ColorPics", jsonArray);
            }
            if (!ListUtils.isEmpty(bean.getMaterialList())) {
                JSONArray jsonArray = new JSONArray();
                FourListBean fourListBean = bean.getMaterialList().get(0);
                jsonArray.put(fourListBean.getID());
                jsonObject.put("MaterialIDs", jsonArray);
            }
            if (!ListUtils.isEmpty(bean.getAgeList())) {
                JSONArray jsonArray = new JSONArray();
                FourListBean fourListBean = bean.getAgeList().get(0);
                jsonArray.put(fourListBean.getID());
                jsonObject.put("AgeIDs", jsonArray);
            }
            if (!ListUtils.isEmpty(bean.getStyleList())) {
                JSONArray jsonArray = new JSONArray();
                FourListBean fourListBean = bean.getStyleList().get(0);
                jsonArray.put(fourListBean.getID());
                jsonObject.put("StyleIDs", jsonArray);
            }
            if (!ListUtils.isEmpty(bean.getSeasonList())) {
                JSONArray jsonArray = new JSONArray();
                FourListBean fourListBean = bean.getSeasonList().get(0);
                jsonArray.put(fourListBean.getID());
                jsonObject.put("SeasonIDs", jsonArray);
            }
            if (!ListUtils.isEmpty(bean.getExtendPropertyTypeListV2())) {
                JSONArray jsonArray = new JSONArray();
                for (UploadBean.ExtendPropertyTypeListV2Bean aa : bean.getExtendPropertyTypeListV2()) {
                    JSONObject jObject = new JSONObject();
                    jObject.put("TypeID", aa.getTypeID());
                    if (!ListUtils.isEmpty(aa.getExtendPropertyList())) {
                        JSONArray childArray = new JSONArray();
                        for (List<UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean> bbL : aa.getExtendPropertyList()) {
                            if (!ListUtils.isEmpty(bbL)) {
                                JSONArray bbArray = new JSONArray();
                                for (UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean bb : bbL) {
                                    JSONObject cjObject = new JSONObject();
                                    cjObject.put("Value", bb.getValue());
                                    cjObject.put("ID", bb.getID());
                                    bbArray.put(cjObject);
                                }
                                childArray.put(bbArray);
                            }

                        }
                        jObject.put("ExtendPropertyList", childArray);
                    }
                    jsonArray.put(jObject);
                }
                jsonObject.put("ExtendPropertyTypeListV2", jsonArray);
            }
            strSaveJson = jsonObject.toString();
            if (BuildConfig.DEBUG)
                Logger.t("INFO").i(strSaveJson);
            // String json = HttpUtils.httpPost("shop/agent/additem", strSaveJson, cookie);
            PinHuoResponse<UploadRepose> response = OkHttpUtils.getInstance().uploadEdPic("shop/agent/additem", strSaveJson);
            // JSONObject jsonObject1 = new JSONObject(response.getData().toString());
            itemId = response.getData().getAgentItemID();
            //  Log.d("yu", json);
        }
        return itemId;
    }

    /***
     *
     * 改款上传
     * */
    public void uploadEditItem(UploadBean bean, String cookie) throws Exception {
        String strSaveJson = "";
        if (bean != null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("AgentItemID", bean.getItemID());
            jsonObject.put("Discount", bean.getDiscount());
            jsonObject.put("Name", bean.getName());
            jsonObject.put("Summary", bean.getSummary());
            jsonObject.put("Intro", bean.getName());
            jsonObject.put("RetailPrice", bean.getPrice());
            jsonObject.put("Price", bean.getPrice());
            jsonObject.put("IsWaitOrder", true);
            jsonObject.put("MarkUpValue", bean.getMarkUpValue());
            jsonObject.put("Remark", bean.getRemark());
            if (bean.getSupplyInfo() != null) {
                jsonObject.put("WaitDays", bean.getSupplyInfo().getDays());
            }
            jsonObject.put("GroupDealCount", bean.getGroupDealCount());
            if (bean.getStallInfo() != null) {
                if (bean.getStallInfo().getStallID() > 0)
                    jsonObject.put("StallID", bean.getStallInfo().getStallID());
            }
            if (!ListUtils.isEmpty(bean.getCategoryList())) {
                JSONArray jsonArray = new JSONArray();
                for (UploadBean.CategoryListBean categoryListBean : bean.getCategoryList()) {
                    if (categoryListBean.getID() > 0)
                        jsonArray.put(categoryListBean.getID());
                }
                jsonObject.put("CategoryIDS", jsonArray);
            }
            if (!ListUtils.isEmpty(bean.getPropertyList())) {
                JSONArray jsonArray = new JSONArray();
                for (UploadBean.PropertyListBean propertyListBean : bean.getPropertyList()) {
                    if (propertyListBean.getID() > 0)
                        jsonArray.put(propertyListBean.getID());
                }
                jsonObject.put("PropertyIDS", jsonArray);
            }
            if (!ListUtils.isEmpty(bean.getProducts())) {
                JSONArray jsonArray = new JSONArray();
                for (UploadBean.ProductsBean productsBean : bean.getProducts()) {
                    JSONObject pdJsonObject = new JSONObject();
                    pdJsonObject.put("Color", productsBean.getColor());
                    pdJsonObject.put("Stock", productsBean.getStock());
                    pdJsonObject.put("Size", productsBean.getSize());
                    jsonArray.put(pdJsonObject);
                }
                jsonObject.put("Products", jsonArray);
            }
            if (!ListUtils.isEmpty(bean.getTags())) {
                JSONArray jsonArray = new JSONArray();
                for (UploadBean.TagsBean tagsBean : bean.getTags()) {
                    if (tagsBean.getID().contains(".")) {
                        jsonArray.put(tagsBean.getID().split("\\.")[0]);
                    } else {
                        jsonArray.put(tagsBean.getID());
                    }

                }
                jsonObject.put("ItemTagIDS", jsonArray);
            }
            if (!ListUtils.isEmpty(bean.getLocal_pics())) {
                String imgHTMLStr = "", Cover = "";
                imgHTMLStr = "<div id=\"beginAppImgInsertTag\" ></div>";
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < bean.getLocal_pics().size(); i++) {
                    UploadBean.MediaBean mediaBean = bean.getLocal_pics().get(i);
                    if (i == 0) {
                        Cover = mediaBean.getPath();
                    }
                    jsonArray.put(mediaBean.getPath());
                    imgHTMLStr += "<img src=\"" + ImageUrlExtends.getImageUrl(mediaBean.getPath(), 24)
                            + "\" />";
                    imgHTMLStr += "<br/>";
                }
                imgHTMLStr += "<div id=\"endAppImgInsertTag\" ></div>";
                jsonObject.put("Images", jsonArray);
                jsonObject.put("Description", imgHTMLStr);
                jsonObject.put("Cover", Cover);
            }
            if (!ListUtils.isEmpty(bean.getColorPics())) {
                JSONArray jsonArray = new JSONArray();
                for (UploadBean.ColorPicsBean mediaBean : bean.getColorPics()) {
                    JSONObject pdJsonObject = new JSONObject();
                    pdJsonObject.put("Color", mediaBean.getColor());
                    pdJsonObject.put("Url", mediaBean.getUrl());
                    if (!TextUtils.isEmpty(mediaBean.getUrl()))
                    jsonArray.put(pdJsonObject);
                }
                if (jsonArray.length()>0)
                jsonObject.put("ColorPics", jsonArray);
            }
            if (!ListUtils.isEmpty(bean.getLocal_videos())) {
                JSONArray jsonArray = new JSONArray();
                for (UploadBean.MediaBean mediaBean : bean.getLocal_videos()) {
                    jsonArray.put(mediaBean.getPath());
                }
                jsonObject.put("Videos", jsonArray);
            }
            if (!ListUtils.isEmpty(bean.getMaterialList())) {
                JSONArray jsonArray = new JSONArray();
                FourListBean fourListBean = bean.getMaterialList().get(0);
                jsonArray.put(fourListBean.getID());
                jsonObject.put("MaterialIDs", jsonArray);
            }
            if (!ListUtils.isEmpty(bean.getAgeList())) {
                JSONArray jsonArray = new JSONArray();
                FourListBean fourListBean = bean.getAgeList().get(0);
                jsonArray.put(fourListBean.getID());
                jsonObject.put("AgeIDs", jsonArray);
            }
            if (!ListUtils.isEmpty(bean.getStyleList())) {
                JSONArray jsonArray = new JSONArray();
                FourListBean fourListBean = bean.getStyleList().get(0);
                jsonArray.put(fourListBean.getID());
                jsonObject.put("StyleIDs", jsonArray);
            }
            if (!ListUtils.isEmpty(bean.getSeasonList())) {
                JSONArray jsonArray = new JSONArray();
                FourListBean fourListBean = bean.getSeasonList().get(0);
                jsonArray.put(fourListBean.getID());
                jsonObject.put("SeasonIDs", jsonArray);
            }
            if (!ListUtils.isEmpty(bean.getExtendPropertyTypeListV2())) {
                JSONArray jsonArray = new JSONArray();
                for (UploadBean.ExtendPropertyTypeListV2Bean aa : bean.getExtendPropertyTypeListV2()) {
                    JSONObject jObject = new JSONObject();
                    jObject.put("TypeID", aa.getTypeID());
                    if (!ListUtils.isEmpty(aa.getExtendPropertyList())) {
                        JSONArray childArray = new JSONArray();
                        for (List<UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean> bbL : aa.getExtendPropertyList()) {
                            if (!ListUtils.isEmpty(bbL)) {
                                JSONArray bbArray = new JSONArray();
                                for (UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean bb : bbL) {
                                    JSONObject cjObject = new JSONObject();
                                    cjObject.put("Value", bb.getValue());
                                    cjObject.put("ID", bb.getID());
                                    bbArray.put(cjObject);
                                }
                                childArray.put(bbArray);
                            }


                        }
                        jObject.put("ExtendPropertyList", childArray);
                    }
                    jsonArray.put(jObject);
                }
                jsonObject.put("ExtendPropertyTypeListV2", jsonArray);
            }
            strSaveJson = jsonObject.toString();
            Logger.t("INFO").i(strSaveJson);
            Object json = OkHttpUtils.getInstance().uploadEdPic("shop/agent/updatemyitem", strSaveJson);
            // String json = HttpUtils.httpPost("shop/agent/updatemyitem", strSaveJson, cookie);
            // Log.d("yu", json);
        }

    }

    /**
     * 添加新商品款式资料
     *
     * @param cookie cookie值
     * @return 返回商品ID
     * @author Chiva Liang
     * <p>
     * //     * @param describ
     * //     *            描述，截取此描述的前16个汉字作为商品Title
     * //     * @param categoryId
     * //     *            分类ID
     * //     * @param price
     * //     *            单价
     * //     * @param coverImg
     * //     *            主图Url
     * //     * @param imgs
     * //     *            附图Url，多图情况下用英文逗号隔开
     * //     * @param products
     * //     *            商品详细属性（颜色尺码规格列表，Json格式）
     */
    public int addItem(ShopItemModel entity, String cookie) throws Exception {
        int itemId = -1;
        try {
            if (entity.getImages() == null || entity.getImages().length == 0) {
                throw new Exception("至少需要添加一张商品图片");
            } else if (entity.getProducts() == null || entity.getProducts().size() == 0) {
                throw new Exception("至少需要给商品设置一种规格");
            }

            // 商品图片Json
            String images = "";
            for (String imgUrl : entity.getImages()) {
                images += "'" + imgUrl + "',";
            }
            if (images.length() > 0) {
                images = images.substring(0, images.length() - 1);
            }
            images = "[" + images + "]";

            // 商品规格Json
            String products = "";
            for (ProductModel product : entity.getProducts()) {
                products += "{'Color':'" + product.getColor() + "',";
                products += "'Size':'" + product.getSize() + "',";
                products += "'Stock':" + product.getStock() + ",";
                products += "'Price':" + product.getPrice() + "},";
                // products += "'Cover':'" + product.getCover() + "'},";
            }
            if (products.length() > 0) {
                products = products.substring(0, products.length() - 1);
            }
            products = "[" + products + "]";
            // 分组json

            // Styles
            String styles = "[" + entity.getStyle() + "]";
            //根据分组id判断是否只是代理可见
            setIsOnly4Agent(entity);

            String strSaveJson = "{";
            strSaveJson += "'Tag':'" + entity.getUniqueTag() + "',";
            strSaveJson += "'IsOnly4Agent':" + entity.getIsOnly4Agent() + ",";
            strSaveJson += "'Name':'" + entity.getName().replace("'", "") + "',";// 对应db的 title
            strSaveJson += "'intro':'" + entity.getIntro().replace("'", "") + "',";//db的 intro
            strSaveJson += "'Price':" + entity.getPrice() + ",";
            strSaveJson += "'RetailPrice':" + entity.getRetailPrice() + ",";
            strSaveJson += "'Cover':'" + entity.getCover() + "',";
            strSaveJson += "'Cat':" + entity.getCat() + ",";
            strSaveJson += "'Styles':" + styles + ",";

            strSaveJson += TextUtils.isEmpty(entity.getItemTagIDS()) ? "" : "'ItemTagIDS':[" + entity.getItemTagIDS().substring(0, entity.getItemTagIDS().length() - 1) + "],";
            //strSaveJson += "'ItemTagIDS':[16]," ;
            String imgHTMLStr = "";
            // if (entity.getImages().length>5)
            // {
            imgHTMLStr = "<div id=\"beginAppImgInsertTag\" ></div>";

            for (int i = 0; i < entity.getImages().length; i++) {
                imgHTMLStr += "<img src=\"" + ImageUrlExtends.getImageUrl(entity.getImages()[i], 24)
                        + "\" />";
                imgHTMLStr += "<br/>";
            }
            imgHTMLStr += "<div id=\"endAppImgInsertTag\" ></div>";
            // }
            strSaveJson += "'Description':'" + imgHTMLStr + "',";
            strSaveJson += "'Images':" + images + ",";
            strSaveJson += "'Groups':[" + entity.getGroupIds() + "],";
            strSaveJson += "'ShopCats':[" + entity.getShopCatsStr() + "],";
            strSaveJson += "'Attrs':[" + entity.getItemAttrsStr() + "],";
            strSaveJson += "'IsTop':" + entity.isTop() + ",";
            strSaveJson += "'IsWaitOrder':" + entity.isWaitOrder() + ",";
            strSaveJson += "'WaitDays':" + entity.getWaitDays() + ",";
            strSaveJson += "'Products':" + products + "}";
            Log.d(TAG, "上传商品请求：" + strSaveJson);
            String json = httpPost("shop/agent/additem", strSaveJson, cookie);
            Log.i(TAG, "上传商品响应：" + json);
            ShopItemModel shopItemModel = GsonHelper.jsonToObject(json, ShopItemModel.class);
            itemId = shopItemModel.getAgentItemID();
        } catch (Exception ex) {
            Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG, "addItem", ex.getMessage()));
            ex.printStackTrace();
            throw ex;
        }
        return itemId;
    }

    /**
     * 修改商品款式资料
     *
     * @param entity 修改的商品属性
     * @param id     修改的商品ID
     * @param cookie cookie值
     * @return 返回商品ID
     * @author pengjun
     */
    public boolean updateItem(ShopItemModel entity, int id, String cookie) throws Exception {
        boolean result = false;
        try {
            if (entity.getImages() == null || entity.getImages().length == 0) {
                throw new Exception("至少需要添加一张商品图片");
            } else if (entity.getProducts() == null || entity.getProducts().size() == 0) {
                throw new Exception("至少需要给商品设置一种规格");
            }

            // 商品图片Json
            String images = "";
            for (String imgUrl : entity.getImages()) {
                images += "'" + imgUrl + "',";
            }
            if (images.length() > 0) {
                images = images.substring(0, images.length() - 1);
            }
            images = "[" + images + "]";

            // 商品规格Json
            String products = "";
            for (ProductModel product : entity.getProducts()) {
                products += "{'Color':'" + product.getColor() + "',";
                products += "'Size':'" + product.getSize() + "',";
                products += "'Stock':" + product.getStock() + ",";
                products += "'Price':" + product.getPrice() + "},";
                // products += "'Cover':'" + product.getCover() + "'},";
            }
            if (products.length() > 0) {
                products = products.substring(0, products.length() - 1);
            }

            products = "[" + products + "]";

            // Styles
            String styles = "[" + entity.getStyle() + "]";
            //根据group ids 判断是否只是代理可见
            setIsOnly4Agent(entity);
            String strSaveJson = "{";
            strSaveJson += "'Tag':'" + entity.getUniqueTag() + "',";
            strSaveJson += "'IsOnly4Agent':" + entity.getIsOnly4Agent() + ",";
            strSaveJson += "'AgentItemID':" + String.valueOf(id) + ",";
            strSaveJson += "'Name':'" + entity.getName() + "',";
            strSaveJson += "'intro':'" + entity.getIntro() + "',";//db的 intro
            strSaveJson += "'Price':" + entity.getPrice() + ",";
            strSaveJson += "'RetailPrice':" + entity.getRetailPrice() + ",";
            strSaveJson += "'Cover':'" + entity.getCover() + "',";
            strSaveJson += "'Groups':[" + entity.getGroupIds() + "],";
            strSaveJson += "'ShopCats':[" + entity.getShopCatsStr() + "],";
            strSaveJson += "'Attrs':[" + entity.getItemAttrsStr() + "],";
            strSaveJson += "'IsTop':" + entity.isTop() + ",";
            strSaveJson += "'IsWaitOrder':" + entity.isWaitOrder() + ",";
            strSaveJson += "'WaitDays':" + entity.getWaitDays() + ",";
            strSaveJson += "'Cat':" + entity.getCat() + ",";
            strSaveJson += "'Styles':" + styles + ",";

            String tags = "";
            if (!ListUtils.isEmpty(entity.getTags())) {
                for (LabelModel item : entity.getTags()) {
                    tags += ((int) Double.parseDouble(item.getID())) + ",";
                }
            }

            //strSaveJson += TextUtils.isEmpty(tags)?"": "'ItemTagIDS':["+tags.substring(0,tags.length()-1)+"],";
            strSaveJson += TextUtils.isEmpty(entity.getItemTagIDS()) ? "" : "'ItemTagIDS':[" + entity.getItemTagIDS().substring(0, entity.getItemTagIDS().length() - 1) + "],";

            String imgHTMLStr = "";
            // if (entity.getImages().length>5)
            // {
            imgHTMLStr = "<div id=\"beginAppImgInsertTag\" ></div>";

            for (int i = 0; i < entity.getImages().length; i++) {
                imgHTMLStr += "<img src=\"" + ImageUrlExtends.getImageUrl(entity.getImages()[i], 24)
                        + "\" />";
                imgHTMLStr += "<br/>";
            }
            imgHTMLStr += "<div id=\"endAppImgInsertTag\" ></div>";
            // }
            strSaveJson += "'Description':'" + imgHTMLStr + "',";
            strSaveJson += "'Images':" + images + ",";
            strSaveJson += "'Products':" + products + "}";
            Log.d("API", "更新商品：" + strSaveJson);
            String json = httpPost("shop/agent/updatemyitem", strSaveJson, cookie);
            Log.i(TAG, "Json：" + json);
            result = GsonHelper.jsonToObject(json, boolean.class);
        } catch (Exception ex) {
            Log.e(TAG,
                    MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG, "updateitem", ex.getMessage()));
            ex.printStackTrace();
            throw ex;
        }
        return result;
    }

    /**
     * Description:根据group ids判断是否只是代理可看
     * 2014-7-24 上午9:16:25
     *
     * @author ZZB
     */
    private void setIsOnly4Agent(ShopItemModel entity) {
        boolean isOnly4Agent = false;
        String groupIds = StringUtils.deleteEndStr(entity.getGroupIds(), ",");
        if (TextUtils.isEmpty(groupIds) || groupIds.equals(Const.SystemGroupId.ALL_PPL + "")) {
            isOnly4Agent = false;
            entity.setGroupIds("");
        } else {
            isOnly4Agent = true;
            if (groupIds.equals(Const.SystemGroupId.ALL_AGENT + "")) {
                entity.setGroupIds("");
            }
        }
        entity.setIsOnly4Agent(isOnly4Agent);

    }

    //获取所有标签
    public List<TagModel> getUserTags(String cookie) throws Exception {
        List<TagModel> sizeList = null;
        try {
            String json = httpPost("shop/agent/GetUserTags", new HashMap<String, String>(),
                    cookie);
            Log.i(TAG, "Json：" + json);
            sizeList = GsonHelper.jsonToObject(json, new TypeToken<ArrayList<TagModel>>() {
            });
        } catch (Exception ex) {
            Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG, "getUserTags", ex.getMessage()));
            ex.printStackTrace();
            throw ex;
        }
        return sizeList;
    }
}
