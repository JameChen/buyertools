package com.nahuo.buyertool.api;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.nahuo.buyertool.Bean.SortBean;
import com.nahuo.buyertool.common.SpManager;
import com.nahuo.buyertool.model.PageraModel.BaseInfoList;
import com.nahuo.buyertool.model.PublicData;
import com.nahuo.library.helper.FunctionHelper;
import com.nahuo.library.helper.GsonHelper;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/3/17 0017.(商品类型api)
 */
public class CommodityAPI {
    public static final String TAG=CommodityAPI.class.getSimpleName();
    public static CommodityAPI instance=null;
    //单例
    public static CommodityAPI getInstance(){
        if(instance==null){
            instance=new CommodityAPI();
        }
        return instance;
    }

    //获取商品类型
    public static BaseInfoList getCommodityAPI(Context context) throws Exception{
        BaseInfoList baseInfo;
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("pwd", SpManager.getLoginPwd(context));
            params.put("from", "buyerandroid");
            String versionName=getVersionName(context);
            Log.v(TAG," 版本号 :"+versionName);
            if(!TextUtils.isEmpty(versionName)){
                params.put("ver",versionName);
            }
            Log.v(TAG,"pwd : "+ SpManager.getLoginPwd(context));
            String json = HttpUtils.httpPost("buyertool/BuyerV2/GetBaseInfo2",params, PublicData.getCookie(context));
            Log.i(TAG, "Json：" + json);
            baseInfo = GsonHelper.jsonToObject(json, new TypeToken<BaseInfoList>(){});
        } catch (Exception ex) {
            Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG,
                    "getCommodityAPI", ex.getMessage()));
            ex.printStackTrace();
            throw ex;
        }
        return baseInfo;
    }
    //获取几个列表页左上角的筛选排序项
    public static List<SortBean> getListFilter(Context context) throws Exception{
        List<SortBean> list;
        try {
            Map<String, String> params = new HashMap<String, String>();
            String json = HttpUtils.httpPost("buyertool/buyerv2/GetListFilter",params, PublicData.getCookie(context));
            Log.i(TAG, "Json：" + json);
            list = GsonHelper.jsonToObject(json, new TypeToken<List<SortBean>>(){});
            SpManager.setListFilter(context,json);
        } catch (Exception ex) {
            Log.e(TAG, MessageFormat.format("{0}->{1}方法发生异常：{2}", TAG,
                    "getCommodityAPI", ex.getMessage()));
            ex.printStackTrace();
            throw ex;
        }
        return list;
    }
    private static String getVersionName(Context mContext){
        String versionName = FunctionHelper.getVersionName(mContext);
        if(!TextUtils.isEmpty(versionName)){
            return versionName.substring(0,3);
        }
        return "";
    }
}
