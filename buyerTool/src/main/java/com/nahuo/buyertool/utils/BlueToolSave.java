package com.nahuo.buyertool.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nahuo.buyertool.Bean.BlueToolBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jame on 2017/4/12.
 */

public class BlueToolSave {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public BlueToolSave(Context mContext, String preferenceName) {
        preferences = mContext.getSharedPreferences(preferenceName, Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
        editor = preferences.edit();
    }

    /**
     * 保存List
     *
     * @param tag
     * @param datalist
     */
    public  void setDataList(String tag, List<BlueToolBean> datalist) {
        if (null == datalist || datalist.size() <= 0)
            return;

        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(datalist, new TypeToken<List<BlueToolBean>>() {
        }.getType());
        editor.clear();
        editor.putString(tag, strJson);
        editor.commit();

    }
    public  void clearDataList(String tag) {

        editor.clear();
        editor.putString(tag, "");
        editor.commit();

    }

    /**
     * 获取List
     *
     * @param tag
     * @return
     */
    public List<BlueToolBean> getDataList(String tag) {
        List<BlueToolBean> datalist = new ArrayList<>();
        String strJson = preferences.getString(tag, null);
        if (null == strJson) {
            return datalist;
        }
        Gson gson = new Gson();
        datalist = gson.fromJson(strJson, new TypeToken<List<BlueToolBean>>() {
        }.getType());
        return datalist;

    }
}
