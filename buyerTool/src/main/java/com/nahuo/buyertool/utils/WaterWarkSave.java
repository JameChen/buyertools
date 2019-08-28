package com.nahuo.buyertool.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jame on 2017/4/12.
 */

public class WaterWarkSave {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    public static String BUYTOOL_HASWATERWARK = "BUYTOOL_HASWATERWARK";
    public static String BUYTOOLNOADDWATERWARK_COUNT = "BUYTOOLNOADDWATERWARK_COUNT";
    public static String BUYTOOLWATERWARK_TYPE = "BUYTOOLWATERWARK_TYPE";
    public static String BUYTOOLCOMPRESSMAXSIZE = "BUYTOOLCOMPRESSMAXSIZE";
    public WaterWarkSave(Context mContext, String preferenceName) {
        preferences = mContext.getSharedPreferences(preferenceName, Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
        editor = preferences.edit();
    }

    public void setHasWaterWark(String tag, boolean focus) {
       // editor.clear();
        editor.putBoolean(tag, focus);
        editor.commit();
    }
    public void setCompressMaxSize(String tag, int size) {
        // editor.clear();
        editor.putInt(tag, size);
        editor.commit();
    }
    public int getCompressMaxSize(String tag) {
        int size = preferences.getInt(tag, 200);
        return size;
    }
    public boolean getHasWaterWark(String tag) {
        boolean focus = preferences.getBoolean(tag, false);
        return focus;
    }

    public void setNoAddWaterCount(String tag, int count) {
        //editor.clear();
        editor.putInt(tag, count);
        editor.commit();
    }

    public int getWaterType(String tag) {
        int count = preferences.getInt(tag, 0);
        return count;
    }

    public void setWaterType(String tag, int type) {
        //editor.clear();
        editor.putInt(tag, type);
        editor.commit();
    }

    public int getNoAddWaterCount(String tag) {
        int type = preferences.getInt(tag, 0);
        return type;
    }

}
