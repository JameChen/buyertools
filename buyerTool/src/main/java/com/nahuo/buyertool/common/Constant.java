package com.nahuo.buyertool.common;

import com.nahuo.buyertool.BWApplication;

import java.io.File;

public class Constant {
    public static final int TYPE_LEVEL_0 = 0;
    public static final int TYPE_LEVEL_1 = 1;
    public static final String PATH_DATA = BWApplication.getInstance().getCacheDir().getAbsolutePath() + File.separator + "data";
    public static final String PATH_CACHE = PATH_DATA + "/NetCache";
    public static final String NEW_FRIENDS_USERNAME = "item_new_friends";
    public static final String GROUP_USERNAME = "item_groups";
    public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
    public static final String ADMIN_NAHUO_USER = "admin";
    public static final String ADD_NAHUO_USER = "adduser";

    public class FourList {
        public static final int Type_MaterialList = 1;
        public static final int Type_GetAgeList = 2;
        public static final int Type_StyleList = 3;
        public static final int Type_SeasonList = 4;

    }
}
