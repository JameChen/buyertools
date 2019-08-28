package com.nahuo.buyertool.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nahuo.buyertool.Bean.UploadBean;
import com.nahuo.buyertool.common.FourListBean;
import com.nahuo.buyertool.common.ListUtils;
import com.nahuo.buyertool.utils.NewGsonHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jame on 2017/7/27.
 */

public class ToolUploadDbHelper {
    private static final String TAG = "ToolUploadDbHelper";
    private ToolDBManager dbManager;
    private static ToolUploadDbHelper instance = null;

    public static ToolUploadDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new ToolUploadDbHelper(context);
        }
        return instance;
    }

    private ToolUploadDbHelper(Context context) {
        dbManager = new ToolDBManager(context);
    }

    /**
     * 增加一个调后台上传记录
     */
    public boolean addUploadItem(UploadBean uploadBean) {
        try {
            // 新增单据
            ContentValues cv = this.GetContentValues(uploadBean);
            dbManager.Insert("upload_list", cv);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean changeUploadItem(UploadBean uploadBean) {
        boolean is_chang = false;
        if (uploadBean != null) {
            // String uploadTime = uploadBean.getCreat_time();
            // int userID = uploadBean.getUserId();
            //  dbManager.Delete("upload_list", "create_time = ? and userId = ?", new String[]{uploadTime, String.valueOf(userID)});
            uploadBean.setCreat_time(System.currentTimeMillis() + "");
            is_chang = addUploadItem(uploadBean);
        }
        return is_chang;
    }

    public boolean removeUploadItem(UploadBean uploadBean) {
        boolean is_chang = false;
        if (uploadBean != null) {
            String uploadTime = uploadBean.getCreat_time();
            int userID = uploadBean.getUserId();
            dbManager.Delete("upload_list", "create_time = ? and userId = ?", new String[]{uploadTime, String.valueOf(userID)});
        }
        return is_chang;
    }

    public void updateMessage(UploadBean uploadBean) {
        if (uploadBean != null) {
            ContentValues cv = new ContentValues();
            String uploadTime = uploadBean.getCreat_time();
            int userID = uploadBean.getUserId();
            if (!ListUtils.isEmpty(uploadBean.getLocal_pics()))
                cv.put(TableUpload_List.MESSAGE, uploadBean.getMessage());
            dbManager.Update("upload_list", cv, "create_time = ? and userId = ?", new String[]{uploadTime, String.valueOf(userID)});
        }
    }

    public void updatePicList(UploadBean uploadBean) {
        if (uploadBean != null) {
            ContentValues cv = new ContentValues();
            Gson gson = new Gson();
            String uploadTime = uploadBean.getCreat_time();
            int userID = uploadBean.getUserId();
            if (!ListUtils.isEmpty(uploadBean.getLocal_pics()))
                cv.put(TableUpload_List.IMAGES_LIST, gson.toJson(uploadBean.getLocal_pics()));
            dbManager.Update("upload_list", cv, "create_time = ? and userId = ?", new String[]{uploadTime, String.valueOf(userID)});
        }
    }

    public void updateColorPicList(UploadBean uploadBean) {
        if (uploadBean != null) {
            ContentValues cv = new ContentValues();
            Gson gson = new Gson();
            String uploadTime = uploadBean.getCreat_time();
            int userID = uploadBean.getUserId();
            if (!ListUtils.isEmpty(uploadBean.getColorPics()))
                cv.put(TableUpload_List.ColorPics_LIST, gson.toJson(uploadBean.getColorPics()));
            dbManager.Update("upload_list", cv, "create_time = ? and userId = ?", new String[]{uploadTime, String.valueOf(userID)});
        }
    }

    public void updateVideoList(UploadBean uploadBean) {
        if (uploadBean != null) {
            ContentValues cv = new ContentValues();
            Gson gson = new Gson();
            String uploadTime = uploadBean.getCreat_time();
            int userID = uploadBean.getUserId();
            if (!ListUtils.isEmpty(uploadBean.getLocal_videos()))
                cv.put(TableUpload_List.VIDEOS_LIST, gson.toJson(uploadBean.getLocal_videos()));
            dbManager.Update("upload_list", cv, "create_time = ? and userId = ?", new String[]{uploadTime, String.valueOf(userID)});
        }
    }

    private ContentValues GetContentValues(UploadBean cim) {
        ContentValues cv = new ContentValues();
        cv.put(TableUpload_List.ITEM_ID, cim.getItemID());
        cv.put(TableUpload_List.USERID, cim.getUserId());
        cv.put(TableUpload_List.CREATE_TIME, cim.getCreat_time());
        cv.put(TableUpload_List.TITLE, cim.getName());
        cv.put(TableUpload_List.INTRO, cim.getName());
        cv.put(TableUpload_List.PRICE, cim.getPrice());
        cv.put(TableUpload_List.RETAIL_PRICE, cim.getPrice());
        cv.put(TableUpload_List.MARKUPVALUE, cim.getMarkUpValue());
        cv.put(TableUpload_List.DISCOUNT, cim.getDiscount());
        cv.put(TableUpload_List.GROUP_DEAL_COUNT, cim.getGroupDealCount());
        cv.put(TableUpload_List.UPLOAD_TYPE, cim.getType());
        cv.put(TableUpload_List.IS_COPY, cim.isCopy() + "");
        cv.put(TableUpload_List.UPLOADSTATUS, cim.getUploadStatus());
        cv.put(TableUpload_List.MESSAGE, cim.getMessage());
        cv.put(TableUpload_List.COVER, cim.getCover());
        cv.put(TableUpload_List.ReMark, cim.getRemark());
        Gson gson = new Gson();
        if (cim.getSupplyInfo() != null)
            cv.put(TableUpload_List.SUPPLYINFO_LIST, gson.toJson(cim.getSupplyInfo()));
        if (cim.getStallInfo() != null)
            cv.put(TableUpload_List.STALLS_LIST, gson.toJson(cim.getStallInfo()));
        if (!ListUtils.isEmpty(cim.getProducts()))
            cv.put(TableUpload_List.PRODUCTS_LIST, gson.toJson(cim.getProducts()));
        if (!ListUtils.isEmpty(cim.getTags()))
            cv.put(TableUpload_List.TAGS_LIST, gson.toJson(cim.getTags()));
        if (!ListUtils.isEmpty(cim.getCategoryList()))
            cv.put(TableUpload_List.CATEGORY_LIST, gson.toJson(cim.getCategoryList()));
        if (!ListUtils.isEmpty(cim.getPropertyList()))
            cv.put(TableUpload_List.PROPERTY_LIST, gson.toJson(cim.getPropertyList()));
        if (!ListUtils.isEmpty(cim.getLocal_pics()))
            cv.put(TableUpload_List.IMAGES_LIST, gson.toJson(cim.getLocal_pics()));
        if (!ListUtils.isEmpty(cim.getLocal_videos()))
            cv.put(TableUpload_List.VIDEOS_LIST, gson.toJson(cim.getLocal_videos()));
        if (!ListUtils.isEmpty(cim.getColorPics()))
            cv.put(TableUpload_List.ColorPics_LIST, gson.toJson(cim.getColorPics()));
        if (!ListUtils.isEmpty(cim.getMaterialList()))
            cv.put(TableUpload_List.Material_List, gson.toJson(cim.getMaterialList()));
        if (!ListUtils.isEmpty(cim.getAgeList()))
            cv.put(TableUpload_List.Age_List, gson.toJson(cim.getAgeList()));
        if (!ListUtils.isEmpty(cim.getSeasonList()))
            cv.put(TableUpload_List.Season_List, gson.toJson(cim.getSeasonList()));
        if (!ListUtils.isEmpty(cim.getStyleList()))
            cv.put(TableUpload_List.Style_List, gson.toJson(cim.getStyleList()));
        if (!ListUtils.isEmpty(cim.getExtendPropertyTypeListV2()))
            cv.put(TableUpload_List.Extend_Property_Type_List, gson.toJson(cim.getExtendPropertyTypeListV2()));
        return cv;
    }

    /**
     * @author 查询执行数量
     * @create time in 2017/7/31 11:16
     */
    public int getUploadStatusCount(int userID, String status) {
        //String sql="select count(*) from upload_list where userId=? and uploadStatus =?";
        // Cursor cursor= dbManager.Select(sql,new String[]{String.valueOf(userID),status});
        // cursor.moveToFirst();
        int count = 0;
        Cursor cursor = null;
        try {
            cursor = dbManager.Select("upload_list", null, TableUpload_List.USERID + "==? and " + TableUpload_List.UPLOADSTATUS + "==?", new String[]{String.valueOf(userID), status}, null, null, "create_time DESC");
            cursor.moveToFirst();
            count = cursor.getCount();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return count;
    }

    /**
     * 读取出所有的后台上传记录
     */
    public int getEditUploadItemsCount(int userID, int itemId) {
        int count = 0;
        Cursor cursor = null;
        try {
//            List<UploadBean> list= UploadItemService.getDownloadManager().getAllTask();
//            if (!ListUtils.isEmpty(list)){
//                for (UploadBean bean:list) {
//                    if (bean.getItemID()==itemId&&bean.getUserId()==userID){
//                        count=1;
//                        break;
//                    }
//                }
//            }
            cursor = dbManager.Select("upload_list", null, TableUpload_List.USERID + " == ? and " + TableUpload_List.ITEM_ID + " == ?", new String[]{String.valueOf(userID), String.valueOf(itemId)},
                    null, null, "create_time DESC");
            count = cursor.getCount();
            cursor.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            count=0;
           // Log.e(TAG, ex.getMessage());
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }

    /**
     * 读取出所有的后台上传记录
     */
    public List<UploadBean> getAllUploadItems(int userID) {
        List<UploadBean> itmelist = new ArrayList<>();
        Cursor cursor = null;
        try {

            cursor = dbManager.Select("upload_list", null, TableUpload_List.USERID + " == ?", new String[]{String.valueOf(userID)},
                    null, null, "create_time DESC");
            while (cursor.moveToNext()) {
                UploadBean item = cursorToShopItem(cursor);
                itmelist.add(item);
            }
            cursor.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(TAG, ex.getMessage());
            if (cursor != null) {
                cursor.close();
            }
        }
        return itmelist;
    }

    private UploadBean cursorToShopItem(Cursor cursor) {
        UploadBean item = new UploadBean();
        item.setUserId(cursor.getInt(cursor.getColumnIndex(TableUpload_List.USERID)));
        item.setName(cursor.getString(cursor.getColumnIndex(TableUpload_List.TITLE)));
        item.setPrice(cursor.getString(cursor.getColumnIndex(TableUpload_List.PRICE)));
        item.setItemID(cursor.getInt(cursor.getColumnIndex(TableUpload_List.ITEM_ID)));
        item.setCreat_time(cursor.getString(cursor.getColumnIndex(TableUpload_List.CREATE_TIME)));
        item.setCover(cursor.getString(cursor.getColumnIndex(TableUpload_List.COVER)));
        item.setMarkUpValue(cursor.getDouble(cursor.getColumnIndex(TableUpload_List.MARKUPVALUE)));
        item.setDiscount(cursor.getDouble(cursor.getColumnIndex(TableUpload_List.DISCOUNT)));
        item.setType(cursor.getInt(cursor.getColumnIndex(TableUpload_List.UPLOAD_TYPE)));
        item.setMessage(cursor.getString(cursor.getColumnIndex(TableUpload_List.MESSAGE)));
        item.setGroupDealCount(cursor.getInt(cursor.getColumnIndex(TableUpload_List.GROUP_DEAL_COUNT)));
        item.setCopy(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(TableUpload_List.IS_COPY))));
        item.setUploadStatus(cursor.getString(cursor.getColumnIndex(TableUpload_List.UPLOADSTATUS)));
        item.setRemark(cursor.getString(cursor.getColumnIndex(TableUpload_List.ReMark)));
        String supplyInfo_list = cursor.getString(cursor.getColumnIndex(TableUpload_List.SUPPLYINFO_LIST));
        if (!TextUtils.isEmpty(supplyInfo_list)) {
            UploadBean.SupplyInfoBean supplyInfoBean = NewGsonHelper.jsonToObject(supplyInfo_list, UploadBean.SupplyInfoBean.class);
            item.setSupplyInfo(supplyInfoBean);
        }
        String stalls_list = cursor.getString(cursor.getColumnIndex(TableUpload_List.STALLS_LIST));
        if (!TextUtils.isEmpty(stalls_list)) {
            UploadBean.StallInfoBean stallInfoBean = NewGsonHelper.jsonToObject(stalls_list, UploadBean.StallInfoBean.class);
            item.setStallInfo(stallInfoBean);
        }
        String products_list = cursor.getString(cursor.getColumnIndex(TableUpload_List.PRODUCTS_LIST));
        if (!TextUtils.isEmpty(products_list)) {
            List<UploadBean.ProductsBean> productsBeanList = NewGsonHelper.jsonToObject(products_list, new TypeToken<List<UploadBean.ProductsBean>>() {
            });
            item.setProducts(productsBeanList);
        }
        String tags_list = cursor.getString(cursor.getColumnIndex(TableUpload_List.TAGS_LIST));
        if (!TextUtils.isEmpty(tags_list)) {
            List<UploadBean.TagsBean> tagsBeanList = NewGsonHelper.jsonToObject(tags_list, new TypeToken<List<UploadBean.TagsBean>>() {
            });
            item.setTags(tagsBeanList);
        }
        String category_list = cursor.getString(cursor.getColumnIndex(TableUpload_List.CATEGORY_LIST));
        if (!TextUtils.isEmpty(category_list)) {
            List<UploadBean.CategoryListBean> categoryListBeen = NewGsonHelper.jsonToObject(category_list, new TypeToken<List<UploadBean.CategoryListBean>>() {
            });
            item.setCategoryList(categoryListBeen);
        }

        String property_list = cursor.getString(cursor.getColumnIndex(TableUpload_List.PROPERTY_LIST));
        if (!TextUtils.isEmpty(property_list)) {
            List<UploadBean.PropertyListBean> propertyListBeen = NewGsonHelper.jsonToObject(property_list, new TypeToken<List<UploadBean.PropertyListBean>>() {
            });
            item.setPropertyList(propertyListBeen);
        }
        String images_list = cursor.getString(cursor.getColumnIndex(TableUpload_List.IMAGES_LIST));
        if (!TextUtils.isEmpty(images_list)) {
            List<UploadBean.MediaBean> imageList = NewGsonHelper.jsonToObject(images_list, new TypeToken<List<UploadBean.MediaBean>>() {
            });
            item.setLocal_pics(imageList);
        }
        String videos_list = cursor.getString(cursor.getColumnIndex(TableUpload_List.VIDEOS_LIST));
        if (!TextUtils.isEmpty(videos_list)) {
            List<UploadBean.MediaBean> videosList = NewGsonHelper.jsonToObject(videos_list, new TypeToken<List<UploadBean.MediaBean>>() {
            });
            item.setLocal_videos(videosList);
        }
        String colors_list = cursor.getString(cursor.getColumnIndex(TableUpload_List.ColorPics_LIST));
        if (!TextUtils.isEmpty(colors_list)) {
            List<UploadBean.ColorPicsBean> colorsList = NewGsonHelper.jsonToObject(colors_list, new TypeToken<List<UploadBean.ColorPicsBean>>() {
            });
            item.setColorPics(colorsList);
        }
        String material_list = cursor.getString(cursor.getColumnIndex(TableUpload_List.Material_List));
        if (!TextUtils.isEmpty(material_list)) {
            List<FourListBean> fourListBeanList = NewGsonHelper.jsonToObject(material_list, new TypeToken<List<FourListBean>>() {
            });
            item.setMaterialList(fourListBeanList);
        }
        String age_list = cursor.getString(cursor.getColumnIndex(TableUpload_List.Age_List));
        if (!TextUtils.isEmpty(age_list)) {
            List<FourListBean> fourListBeanList = NewGsonHelper.jsonToObject(age_list, new TypeToken<List<FourListBean>>() {
            });
            item.setAgeList(fourListBeanList);
        }
        String style_list = cursor.getString(cursor.getColumnIndex(TableUpload_List.Style_List));
        if (!TextUtils.isEmpty(style_list)) {
            List<FourListBean> fourListBeanList = NewGsonHelper.jsonToObject(style_list, new TypeToken<List<FourListBean>>() {
            });
            item.setStyleList(fourListBeanList);
        }
        String season_list = cursor.getString(cursor.getColumnIndex(TableUpload_List.Season_List));
        if (!TextUtils.isEmpty(season_list)) {
            List<FourListBean> fourListBeanList = NewGsonHelper.jsonToObject(season_list, new TypeToken<List<FourListBean>>() {
            });
            item.setSeasonList(fourListBeanList);
        }
        String extend_list = cursor.getString(cursor.getColumnIndex(TableUpload_List.Extend_Property_Type_List));
        if (!TextUtils.isEmpty(extend_list)) {
            List<UploadBean.ExtendPropertyTypeListV2Bean> extendPropertyTypeListBeen = NewGsonHelper.jsonToObject(extend_list, new TypeToken<List<UploadBean.ExtendPropertyTypeListV2Bean>>() {
            });
            item.setExtendPropertyTypeListV2(extendPropertyTypeListBeen);
        }
        return item;
    }
}
