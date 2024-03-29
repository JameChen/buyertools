package com.nahuo.buyertool.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.common.Const.SystemGroupId;
import com.nahuo.buyertool.model.PurchaseModel;
import com.nahuo.buyertool.model.ShopInfoModel;
import com.nahuo.buyertool.model.UserModel;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Description: 对SharedPreference进行统一管理 2014-7-4下午3:46:34
 */
public class SpManager {
    private static final String TX_TOKEN= "TX_TOKEN";
    private static final String STALL_KEY_WORD = "STALL_KEY_WORD";
    public static final String APP_USRER_AGENT = "APP_USRER_AGENT";
    private static final String BLUETOOL_VIDEO_COPRESS = "BLUETOOL_VIDEO_COPRESS";
    private static final String BLUETOOL_PRINT_LOG = "BLUETOOL_PRINT_LOG_";
    private static final String LOGIN_ACCOUNTS = "LOGIN_ACCOUNTS";
    private static final String LOGIN_ACCOUNT = "LOGIN_ACCOUNT";
    private static final String LOGIN_PWD = "LOGIN_PWD";
    private static final String GETLISTFILTER = "GetListFilter";
    private static final String VISIBLE_RANAGE_IDS = "VISIBLE_RANAGE_IDS";
    private static final String VISIBLE_RANGE_NAMES = "VISIBLE_RANGE_NAMES";
    private static final String IS_FIRST_USE_APP = "IS_FIRST_USE_APP";
    private static final String HAS_CONTACT_MSG = "HAS_CONTACT_MSG";
    private static final String FIRST_USE_TIME = "FIRST_USE_TIME";
    // ======Shop
    private static final String SHOP_ID = "SHOP_ID";
    private static final String SHOP_NAME = "SHOP_NAME";
    private static final String SHOP_BANNER = "BANNER";
    private static final String SHOP_RECRUITDESC = "RECRUITDESC";
    private static final String SHOP_DOMAIN = "SHOP_DOMAIN";
    private static final String SHOP_LOGO = "SHOP_LOGO";
    private static final String SHOP_BACKGROUND = "SHOP_BACKGROUND";
    private static final String SHOP_ITEM_CAT = "SHOP_ITEM_CAT";                  // 店铺商品分类
    // 是否加入诚保
    private static final String IS_JOIN_CREDIT = "IS_JOIN_CREDIT";
    // 是否加入24小时退换货
    private static final String IS_JOIN_24_HR_RETURN = "IS_JOIN_24_HR_RETURN";
    // 是否加入一件拿样
    private static final String IS_JOIN_ONE_SAMPLE = "IS_JOIN_ONE_SAMPLE";
    // 是否加入5件混批
    private static final String IS_JOIN_MIXED_BATCH = "IS_JOIN_MIXED_BATCH";
    // 是否加入7天寄售
    private static final String IS_JOIN_7DAY_DELIVERY = "IS_JOIN_7DAY_DELIVERY";
    // 是否加入微货源
    private static final String IS_JOIN_MICRO_SOURCES = "IS_JOIN_MICRO_SOURCES";
    // 7天诚保加入状态
    private static final String _7DAYS_CREDIT_JOIN_STATU = "CREDIT_JOIN_STATU";
    // =======User
    private static final String UserSig = "UserSig";
    private static final String Identifier = "Identifier";
    private static final String USER_NAME = "USER_NAME";
    private static final String USER_ID = "USER_ID";
    private static final String COOKIE = "COOKIE";
    // =======Version
    private static final String VERSION_3_TIP = "VERSION_3_TIP";
    /**
     * 用户绑定的手机号码
     */
    private static final String USER_BIND_PHONE_NUMS = "USER_BIND_PHONE_NUM";
    private static final String UPLOADED_ITEMS = "UPLOADED_ITEMS";
    private static final String SIGNATURE = "SIGNATURE";
    private static final String ALLITEMCOUNT = "ALLITEMCOUNT";
    private static final String ALLAGENTCOUNT = "ALLAGENTCOUNT";
    private static final String ALLVENDORCOUNT = "ALLVENDORCOUNT";
    /**
     * 是否设置了安全问题
     */
    private static final String HAS_SET_SAFE_QUESTION = "HAS_SET_SAFE_QUESTION";
    private static final String SAFE_QUESTIONS = "SAFE_QUESTIONS";
    /**
     * 设置联系方式
     */
    private static final String CONTACT_INFOS = "CONTACT_INFOS";
    private static final String CONTACT_ADDRESS_INFOS = "CONTACT_ADDRESS_INFOS";
    /**
     * 是否绑定银行
     */
    private static final String HAS_BIND_BANK = "HAS_BIND_BANK";
    private static final String SET_PAYPSW_USERIDS = "SET_PAYPSW_USERIDS";
    private static final String SET_YTF_USERIDS = "SET_YTF_USERIDS";
    /**
     * 第一次使用显示衣付通，之后不再显示
     */
    private static final String SHOW_OPEN_YFT = "SHOW_OPEN_YFT";
    /**
     * 已经进行身份验证的用户id
     */
    private static final String IDENTITY_AUTH_USERIDS = "IDENTITY_AUTH_USERIDS";
    /**
     * 用户身份验证状态json串
     */
    private static final String IDENTITY_AUTH_STATES = "IDENTITY_AUTH_STATES";
    /**
     * 身份验证完整信息json串
     */
    private static final String ID_AUTH_INFOS = "ID_AUTH_INFOS";
    /**
     * 用户余额
     */
    private static final String USER_BALANCE = "USER_BALANCE";
    /**
     * 银行信息json串
     */
    private static final String BANK_INFOS = "BANK_INFOS";
    private static final String BANK_INFO_STATE = "BANK_INFO_STATE";
    private static final String DEFAULT_ADDRESS_JSON = "DEFAULT_ADDRESS_JSON";
    /**
     * 用户款式零售加价率
     */
    private static final String ITEM_RAISE = "ITEM_RAISE";
    /**
     * 店铺是否同意零售价
     */
    private static final String IS_RETAIL_PRICE_ENABLED = "IS_RETAIL_PRICE_ENABLED";
    private static final String SERVER_PIC_URLS = "SERVER_PIC_URLS";
    private static final String ALLOW_AGENT_SHIP = "ALLOW_AGENT_SHIP";
    private static final String CONSIGNEE_USE_MY_PHONE = "CONSIGNEE_USE_MY_PHONE";

    /**
     * im
     **/
    private static final String SHARED_KEY_SETTING_NOTIFICATION = "shared_key_setting_notification";
    private static final String SHARED_KEY_SETTING_SOUND = "shared_key_setting_sound";
    private static final String SHARED_KEY_SETTING_VIBRATE = "shared_key_setting_vibrate";
    private static final String SHARED_KEY_SETTING_SPEAKER = "shared_key_setting_speaker";
    private static final String SHARED_KEY_SETTING_FRIEND = "shared_key_setting_friend";

    private static final String ITEM_SHARE_COUNT = "ITEM_SHARE_COUNT";
    /**
     * 已经注册过IM的用户ID
     */
    private static final String REGISTED_IM_USER_IDS = "REGISTED_IM_USER_IDS";

    /**
     * 小组
     **/
    private static final String LAST_NEWS_IDs = "LAST_NEWS_IDs";
    /**
     * 各个用户缓存的IM信息
     */
    private static final String USERS_IM_INFO = "USERS_IM_INFO";

    /**
     * 已显示过指导页的用户id
     */
    private static final String SHOWCASE_USERIDS = "SHOWCASE_USERIDS";
    /**
     * 粉丝数
     */
    private static final String FANS_NUM = "FANS_NUM";
    /**
     * 粉丝最后添加时间
     */
    private static final String FANS_LAST_ADD_TIME = "FANS_LAST_ADD_TIME";
    /**
     * 代理最后申请时间
     */
    private static final String AGENT_LAST_APPLY_TIME = "AGENT_LAST_APPLY_TIME";
    /**
     * 商品系统分类
     */
    private static final String ITEM_SYS_CATEGORY = "ITEM_SYS_CATEGORY";
    /**
     * 商品已选择的分类
     */
    private static final String ITEM_SELECTED_STYLE = "ITEM_SELECTED_STYLE";


    /**
     * 搜索店铺记录
     */
    private static final String PREF_SEARCH_SHOP_HISTORIES = "PREF_SEARCH_SHOP_HISTORIES";
    private static final String WHO_SHIP_ON_SHARE_2_WP = "WHO_SHIP_ON_SHARE_2_WP";
    /**
     * 默认地址
     */
    public static final String PREF_LAST_ADDRESS_JSON = "pref_default_address_json";

    /**
     * 是否显示为什么招代理
     */
    private static final String SHOW_WHY_RECRUIT_AGENT = "SHOW_WHY_RECRUIT_AGENT";
    /**
     * 是否显示转发店铺前页面
     */
    private static final String SHOW_PRE_SHARE_SHOP = "SHOW_PRE_SHARE_SHOP";
    /**
     * 分享店铺方案
     */
    private static final String SHARE_SHOP_TEXT = "SHARE_SHOP_TEXT";
    /***/
    private static final String SHOW_PRE_SHARE_ITEM = "SHOW_PRE_SHARE_ITEM";

    private static final String PREF_KEY_SHIP_ORDER = "pref_key_ship_orders";
    private static final String PREF_KEY_SHIP_ORDER_TIME = "pref_key_ship_orders_time";
    /**
     * 我的代理数据
     */
    private static final String PREF_KEY_MY_AGENTS_LIST = "pref_key_my_agents_list";

    /**
     * 商品搜索记录
     */
    private static final String PREF_KEY_ITEM_SEARCH_LOG = "pref_key_item_search_log";

    /**
     * 供应商搜索记录
     */
    private static final String PREF_KEY_VENDORS_SEARCH_LOG = "pref_key_vendors_search_log";
    //闪批开始时间
    private static final String QUICK_SELL_START_TIME = "QUICK_SELL_START_TIME";
    //闪批结束时间
    private static final String QUICK_SELL_END_TIME = "QUICK_SELL_END_TIME";

    private static final String SELECT_PURCHASE_DATA = "SELECT_PURCHASE_DATA";
    /**
     * @description 压缩视频
     * @created 2014-10-17 下午5:59:17
     * @author ZZB
     */
    public static void setUploadCompressVideo(Context context, boolean  isCompress) {
        setBoolean(context, BLUETOOL_VIDEO_COPRESS+getUserId(context), isCompress);
    }

    /**
     * @description 压缩视频, 默认是true压缩视频
     * @created 2014-10-17 下午5:59:30
     * @author ZZB
     */
    public static boolean getUploadCompressVideo(Context context) {
        return getBoolean(context, BLUETOOL_VIDEO_COPRESS+getUserId(context), true);
    }
    public static void setSELECT_PURCHASE_DATA(Context context, PurchaseModel data) {
        /*
        private String FloorName;
        private String StallsName;
        private String MarketName;
        private String Code;
        private double TotalMoney;
        private double KdMoney;
        private String StatusName;
        private int OrderID;
    private String Time;
    private String Date;
    private int ID;
    private String BillPic;
        * */
        setString(context, SELECT_PURCHASE_DATA + "FloorName", data.getFloorName());
        setString(context, SELECT_PURCHASE_DATA + "StallsName", data.getStallsName());
        setString(context, SELECT_PURCHASE_DATA + "MarketName", data.getMarketName());
        setString(context, SELECT_PURCHASE_DATA + "Code", data.getCode());
        setFloat(context, SELECT_PURCHASE_DATA + "TotalMoney", Float.parseFloat(data.getTotalMoney() + ""));
        setFloat(context, SELECT_PURCHASE_DATA + "KdMoney", Float.parseFloat(data.getKdMoney() + ""));
        setString(context, SELECT_PURCHASE_DATA + "StatusName", data.getStatusName());
        setInt(context, SELECT_PURCHASE_DATA + "OrderID", data.getOrderID());
        setString(context, SELECT_PURCHASE_DATA + "Time", data.getTime());
        setString(context, SELECT_PURCHASE_DATA + "Date", data.getDate());
        setInt(context, SELECT_PURCHASE_DATA + "ID", data.getID());
        setString(context, SELECT_PURCHASE_DATA + "BillPic", data.getBillPic());
    }
    public static void setSELECT_PURCHASE_DATA_Empty(Context context){
        setString(context, SELECT_PURCHASE_DATA + "FloorName", "");
        setString(context, SELECT_PURCHASE_DATA + "StallsName", "");
        setString(context, SELECT_PURCHASE_DATA + "MarketName","");
        setString(context, SELECT_PURCHASE_DATA + "Code", "");
        setFloat(context, SELECT_PURCHASE_DATA + "TotalMoney", 0);
        setFloat(context, SELECT_PURCHASE_DATA + "KdMoney", 0);
        setString(context, SELECT_PURCHASE_DATA + "StatusName","");
        setInt(context, SELECT_PURCHASE_DATA + "OrderID", 0);
        setString(context, SELECT_PURCHASE_DATA + "Time", "");
        setString(context, SELECT_PURCHASE_DATA + "Date","");
        setInt(context, SELECT_PURCHASE_DATA + "ID", 0);
        setString(context, SELECT_PURCHASE_DATA + "BillPic", "");
    }
    public static PurchaseModel getSELECT_PURCHASE_DATA(Context context) {
        PurchaseModel pm = new PurchaseModel();
        pm.setFloorName(getString(context, SELECT_PURCHASE_DATA + "FloorName", ""));
        pm.setStallsName(getString(context, SELECT_PURCHASE_DATA + "StallsName", ""));
        pm.setMarketName(getString(context, SELECT_PURCHASE_DATA + "MarketName", ""));
        pm.setCode(getString(context, SELECT_PURCHASE_DATA + "Code", ""));
        pm.setTotalMoney(getFloat(context, SELECT_PURCHASE_DATA + "TotalMoney", 0));
        pm.setKdMoney(getFloat(context, SELECT_PURCHASE_DATA + "KdMoney", 0));
        pm.setStatusName(getString(context, SELECT_PURCHASE_DATA + "StatusName", ""));
        pm.setOrderID(getInt(context, SELECT_PURCHASE_DATA + "OrderID", 0));
        pm.setTime(getString(context, SELECT_PURCHASE_DATA + "Time", ""));
        pm.setDate(getString(context, SELECT_PURCHASE_DATA + "Date", ""));
        pm.setID(getInt(context, SELECT_PURCHASE_DATA + "ID", 0));
        pm.setBillPic(getString(context, SELECT_PURCHASE_DATA + "BillPic", ""));
        return pm;
    }
    public static void setUserAgent(Context context, String value) {
        setString(context, APP_USRER_AGENT, value);
    }

    public static String getUserAgentN(Context context) {
        return getString(context, APP_USRER_AGENT);
    }

    public static void setQuickSellStartTime(Context context, long startHour) {
        setLong(context, QUICK_SELL_START_TIME, startHour);
    }

    public static long getQuickSellStartTime(Context context) {
        return getLong(context, QUICK_SELL_START_TIME, 0);
    }

    public static long getBlueToolPrintTime(Context context,int id) {
        return getLong(context, BLUETOOL_PRINT_LOG+id, -1);
    }

    public static void setBlueToolPrintTime(Context context,int id, long startHour) {
        setLong(context, BLUETOOL_PRINT_LOG+id, startHour);
    }

    public static void setQuickSellEndTime(Context context, long endHour) {
        setLong(context, QUICK_SELL_END_TIME, endHour);
    }

    public static long getQuickSellEndTime(Context context) {
        return getLong(context, QUICK_SELL_END_TIME, 0);
    }

    /**
     * @description 转发商品前展示的页面
     * @created 2015-5-5 下午4:06:19
     * @author ZZB
     */
    public static void setShowPreShareItem(Context context, boolean show) {
        setBoolean(context, SHOW_PRE_SHARE_ITEM, show);
    }

    /**
     * @description 转发商品前展示的页面
     * @created 2015-5-5 下午4:06:19
     * @author ZZB
     */
    public static boolean getShowPreShareItem(Context context) {
        return getBoolean(context, SHOW_PRE_SHARE_ITEM, true);
    }

    /**
     * @description 分享店铺方案
     * @created 2015-4-21 下午8:48:37
     * @author ZZB
     */
    public static void setShareShopText(Context context, String text) {
        setString(context, SHARE_SHOP_TEXT, text);
    }

    /**
     * @description
     * @created 2015-4-21 下午8:53:03
     * @author ZZB
     */
    public static String getShareShopText(Context context) {
        return getString(context, SHARE_SHOP_TEXT, context.getString(R.string.shop_share_default));
    }

    /**
     * @description 是否显示转发店铺前页面
     * @created 2015-4-21 下午7:29:17
     * @author ZZB
     */
    public static void setShowPreShareShop(Context context, boolean show) {
        setBoolean(context, SHOW_PRE_SHARE_SHOP, show);
    }

    /**
     * @description 是否显示转发店铺前页面
     * @created 2015-4-21 下午7:29:28
     * @author ZZB
     */
    public static boolean getShowPreShareShop(Context context) {
        return getBoolean(context, SHOW_PRE_SHARE_SHOP, true);
    }

    /**
     * @description 设置为什么要找代理
     * @created 2015-4-21 下午4:15:46
     * @author ZZB
     */
    public static void setShowWhyRecruitAgent(Context context, boolean show) {
        setBoolean(context, SHOW_WHY_RECRUIT_AGENT, show);
    }

    /**
     * @description 为什么要招代理
     * @created 2015-4-21 下午4:17:17
     * @author ZZB
     */
    public static boolean getShowWhyRecruitAgent(Context context) {
        return getBoolean(context, SHOW_WHY_RECRUIT_AGENT, true);// 默认显示
    }

    /**
     * @description 转发到微铺时，记住谁来发货
     * @created 2015-3-30 下午5:42:48
     * @author ZZB
     */
    public static void setWhoShipOnShare2WP(Context context, int position) {
        setInt(context, WHO_SHIP_ON_SHARE_2_WP, position);
    }

    /**
     * @description 转发到微铺时，记住谁来发货
     * @created 2015-3-30 下午5:40:52
     * @author ZZB
     */
    public static int getWhoShipOnShare2WP(Context context) {
        return getInt(context, WHO_SHIP_ON_SHARE_2_WP, 0);
    }

    /**
     * @description 设置店铺商品分类
     * @created 2015-3-12 下午3:02:22
     * @author ZZB
     */
    public static void setItemShopCategory(Context context, String json) {
        setString(context, SHOP_ITEM_CAT, json);
    }

    /**
     * @description 获取店铺商品分类
     * @created 2015-3-12 下午3:03:05
     * @author ZZB
     */
    public static String getItemShopCategory(Context context) {
        return getString(context, SHOP_ITEM_CAT);
    }

    /**
     * @description 商品已选择的分类
     * @created 2015-3-10 下午5:09:57
     * @author ZZB
     */
    public static void setItemSelectedStyle(Context context, String json) {
        setString(context, ITEM_SELECTED_STYLE, json);
    }

    /**
     * @description 商品已选择的分类
     * @created 2015-3-10 下午5:10:29
     * @author ZZB
     */
    public static String getItemSelectedStyle(Context context) {
        return getString(context, ITEM_SELECTED_STYLE);
    }

    /**
     * @description 7天诚保加入状态
     * @created 2015-3-5 下午1:51:56
     * @author ZZB
     */
    public static void set7DaysCreditStatu(Context context, String statu) {
        setString(context, _7DAYS_CREDIT_JOIN_STATU, statu);
    }


    /**
     * @description 设置商品系统分类
     * @created 2015-3-2 下午5:42:29
     * @author ZZB
     */
    public static void setItemSysCategory(Context context, String json) {
        setString(context, ITEM_SYS_CATEGORY, json);
    }

    /**
     * @description 获取商品系统分类
     * @created 2015-3-2 下午5:42:14
     * @author ZZB
     */
    public static String getItemSysCategory(Context context) {
        return getString(context, ITEM_SYS_CATEGORY);
    }

    /**
     * @description 是否加入微货源
     * @created 2015-3-2 下午3:55:17
     * @author ZZB
     */
    public static void setJoinMicroSources(Context context, boolean join) {
        setBoolean(context, IS_JOIN_MICRO_SOURCES, join);
    }

    /**
     * @description 是否加入微货源
     * @created 2015-3-2 下午3:55:05
     * @author ZZB
     */
    public static boolean isJoinMicroSources(Context context) {
        return getBoolean(context, IS_JOIN_MICRO_SOURCES, false);
    }

    /**
     * @description 是否加入7天寄售
     * @created 2015-3-2 下午3:53:39
     * @author ZZB
     */
    public static void setJoin7DaysDelivery(Context context, boolean join) {
        setBoolean(context, IS_JOIN_7DAY_DELIVERY, join);
    }


    /**
     * @description 设置是否加入五件混批
     * @created 2015-3-2 下午3:52:03
     * @author ZZB
     */
    public static void setJoinMixedBatch(Context context, boolean join) {
        setBoolean(context, IS_JOIN_MIXED_BATCH, join);
    }

    /**
     * @description 是否加入五件混批
     * @created 2015-3-2 下午3:51:56
     * @author ZZB
     */
    public static boolean isJoinMixedBatch(Context context) {
        return getBoolean(context, IS_JOIN_MIXED_BATCH, false);
    }

    /**
     * @description 设置加入一件拿样
     * @created 2015-3-2 下午3:46:27
     * @author ZZB
     */
    public static void setJoinOneSample(Context context, boolean join) {
        setBoolean(context, IS_JOIN_ONE_SAMPLE, join);
    }

    /**
     * @description 是否加入一件拿样
     * @created 2015-3-2 下午3:47:19
     * @author ZZB
     */
    public static boolean isJoinOneSample(Context context) {
        return getBoolean(context, IS_JOIN_ONE_SAMPLE, false);
    }

    /**
     * @description 设置24小时退换货
     * @created 2015-3-2 下午3:45:07
     * @author ZZB
     */
    public static void setJoin24HrReturn(Context context, boolean join) {
        setBoolean(context, IS_JOIN_24_HR_RETURN, join);
    }


    /**
     * @description 设置是否加入诚保
     * @created 2015-3-2 下午3:43:23
     * @author ZZB
     */
    public static void setJoinCredit(Context context, boolean join) {
        setBoolean(context, IS_JOIN_CREDIT, join);
    }


    /**
     * @description 设置代理最后申请时间
     * @created 2015-2-4 下午4:26:15
     * @author ZZB
     */
    public static void setAgentLastApplyTime(Context context, String value) {
        setString(context, AGENT_LAST_APPLY_TIME, value);
    }


    /**
     * @description 获取粉丝数
     * @created 2015-1-28 下午4:03:27
     * @author ZZB
     */
    public static int getFansNum(Context context) {
        return getInt(context, FANS_NUM, 0);
    }

    /**
     * @description 设置粉丝最后添加的时间
     * @created 2015-1-26 下午5:27:12
     * @author ZZB
     */
    public static void setFansLastAddTime(Context context, String value) {
        setString(context, FANS_LAST_ADD_TIME, value);
    }


    /**
     * @description 设置已显示过指导页的用户id
     * @created 2015-1-16 上午11:30:52
     * @author ZZB
     */
    public static void setShowcaseUserIds(Context context, String value) {
        setString(context, SHOWCASE_USERIDS, value);
    }

    /**
     * @description 获取已显示过指导页的用户id
     * @created 2015-1-16 上午11:31:26
     * @author ZZB
     */
    public static String getShowcaseUserIds(Context context) {
        return getString(context, SHOWCASE_USERIDS);
    }

    /**
     * @description 设置各个用户缓存的IM信息
     * @created 2015-1-12 上午11:03:54
     * @author ZZB
     */
    public static void setUsersIMInfo(Context context, String value) {
        setString(context, USERS_IM_INFO, value);
    }

    /**
     * @description 获取各个用户缓存的IM信息
     * @created 2015-1-12 上午11:03:43
     * @author ZZB
     */
    public static String getUsersIMInfo(Context context) {
        return getString(context, USERS_IM_INFO);
    }

    /**
     * @description 设置最后一条广播站消息的创建时间
     * @author PJ
     */
    public static void setLastNewsIDs(Context context, int id) {
        String allIds = getReadedIDs(context);
        if (!allIds.contains("," + id + ",")) {
            setString(context, LAST_NEWS_IDs, allIds + "," + id + ",");
        }
    }

    /**
     * @description 获取所有已读id列表
     * @author PJ
     */
    public static String getReadedIDs(Context context) {
        return getString(context, LAST_NEWS_IDs);
    }

    /**
     * @description 获取最后一条广播站消息的创建时间
     * @author PJ
     */
    public static int getLastNewsIDs(Context context) {
        String allIds = getString(context, LAST_NEWS_IDs);
        String[] allidArr = allIds.split(",");
        int maxID = 0;
        try {
            for (String id : allidArr) {
                if (id.length() > 0 && Integer.valueOf(id) > maxID) {
                    maxID = Integer.valueOf(id);
                }
            }
        } catch (Exception ex) {
        }
        return maxID;
    }

    /**
     * @description 设置已经注册过IM的用户ID
     * @created 2014-12-16 下午2:44:22
     * @author ZZB
     */
    public static void setRegistedIMUserIds(Context context, String value) {
        setString(context, REGISTED_IM_USER_IDS, value);
    }

    /**
     * @description 获取已经注册过IM的用户ID
     * @created 2014-12-16 下午2:45:03
     * @author ZZB
     */
    public static String getRegistedIMUserIds(Context context) {
        return getString(context, REGISTED_IM_USER_IDS);
    }

    /**
     * @description 设置商品分享次数
     * @created 2014-11-11 下午9:01:29
     * @author ZZB
     */
    public static void setItemShareCount(Context context, String value) {
        setString(context, ITEM_SHARE_COUNT, value);
    }

    /**
     * @description 获取商品分享次数
     * @created 2014-11-11 下午9:01:41
     * @author ZZB
     */
    public static String getItemShareCount(Context context) {
        return getString(context, ITEM_SHARE_COUNT);
    }

    /**
     * @description 设置收件人电话是否是我的
     * @created 2014-10-17 下午5:59:17
     * @author ZZB
     */
    public static void setConsigneeUseMyPhone(Context context, boolean use) {
        setBoolean(context, CONSIGNEE_USE_MY_PHONE, use);
    }

    /**
     * @description 收件人电话是否是我的, 默认是false即收件人电话不是我自己
     * @created 2014-10-17 下午5:59:30
     * @author ZZB
     */
    public static boolean getConsigneeUseMyPhone(Context context) {
        return getBoolean(context, CONSIGNEE_USE_MY_PHONE, false);
    }

    /**
     * @description 设置是否同意代理发货
     * @created 2014-10-17 下午5:05:35
     * @author ZZB
     */
    public static void setAllowAgentShip(Context context, boolean allow) {
        setBoolean(context, ALLOW_AGENT_SHIP, allow);
    }

    /**
     * @description 是否同意代理发货
     * @created 2014-10-17 下午5:12:10
     * @author ZZB
     */
    public static boolean getAllowAgentShip(Context context) {
        return getBoolean(context, ALLOW_AGENT_SHIP, true);
    }

    /**
     * @description 保存已经上传服务端的图片
     * @created 2014-10-11 上午9:59:05
     * @author ZZB
     */
    public static void setServerPicUrls(Context context, String value) {
        setString(context, SERVER_PIC_URLS, value);
    }

    /**
     * @description 获取已经上传服务端的图片
     * @created 2014-10-11 上午10:00:23
     * @author ZZB
     */
    public static String getServerPicUrls(Context context) {
        return getString(context, SERVER_PIC_URLS);
    }

    /**
     * @description 获取店铺是否统一零售价
     * @author PJ
     */
    public static boolean getIsRetailPriceUnified(Context context) {
        return getBoolean(context, IS_RETAIL_PRICE_ENABLED, false);
    }

    /**
     * @description 设置店铺是否统一零售价
     * @author PJ
     */
    public static void setIsRetailPriceUnified(Context context, boolean isUnified) {
        setBoolean(context, IS_RETAIL_PRICE_ENABLED, isUnified);
    }


    /**
     * @description 设置用户款式零售加价率
     * @author PJ
     */
    public static void setRetailAddRate(Context context, double rate) {
        setString(context, ITEM_RAISE, rate + "");
    }

    /**
     * @description 获取默认地址的json
     * @created 2014-9-22 下午2:48:43
     * @author ZZB
     */
    public static String getDefaultAddressJson(Context context) {
        return getString(context, DEFAULT_ADDRESS_JSON);
    }

    /**
     * @description 银行状态
     * @created 2014-9-19 下午5:16:30
     * @author ZZB
     */
    public static void setBankInfoState(Context context, String state) {
        setString(context, BANK_INFO_STATE, state);
    }

    /**
     * @description 获取银行状态
     * @created 2014-9-19 下午5:17:06
     * @author ZZB
     */
    public static String getBankInfoState(Context context) {
        return getString(context, BANK_INFO_STATE);
    }

    /**
     * @description 设置银行信息json串
     * @created 2014-9-19 下午4:47:48
     * @author ZZB
     */
    public static void setBankInfos(Context context, String json) {
        setString(context, BANK_INFOS, json);
    }

    /**
     * @description 获取银行信息json串
     * @created 2014-9-19 下午4:47:04
     * @author ZZB
     */
    public static String getBankInfos(Context context) {
        return getString(context, BANK_INFOS);
    }

    /**
     * @description 设置用户余额
     * @created 2014-9-17 上午10:09:23
     * @author ZZB
     */
    public static void setUserBalance(Context context, String value) {
        setString(context, USER_BALANCE, value);
    }

    /**
     * @description 获取用户余额
     * @created 2014-9-17 上午10:09:15
     * @author ZZB
     */
    public static String getUserBalance(Context context) {
        return getString(context, USER_BALANCE);
    }

    /**
     * @description 设置身份验证完整信息json串
     * @created 2014-9-15 下午5:50:09
     * @author ZZB
     */
    public static void setIDAuthInfos(Context context, String json) {
        setString(context, ID_AUTH_INFOS, json);
    }

    /***
     * @description 获取身份验证完整信息json串
     * @created 2014-9-15 下午5:49:48
     * @author ZZB
     */
    public static String getIDAuthInfos(Context context) {
        return getString(context, ID_AUTH_INFOS);
    }


    /**
     * @description 获取用户身份验证状态的json串
     * @created 2014-9-12 下午2:44:13
     * @author ZZB
     */
//    public static String getIdentityAuthStates(Context context) {
//        return getString(context, IDENTITY_AUTH_STATES);
//    }

    /**
     * @description 设置已经进行身份验证的用户id
     * @created 2014-9-12 下午2:03:57
     * @author ZZB
     */
//    public static void setIdentityAuthUserIds(Context context, String userIds) {
//        setString(context, IDENTITY_AUTH_USERIDS, userIds);
//    }

    /**
     * @description 获取已经进行身份验证的用户id
     * @created 2014-9-12 下午2:02:55
     * @author ZZB
     */
//    public static String getIdentityAuthUserIds(Context context) {
//        return getString(context, IDENTITY_AUTH_USERIDS);
//    }


    /**
     * @description 获取是否已经显示开通衣付通
     * @created 2014-9-9 上午10:10:10
     * @author ZZB
     */
    public static boolean getShowOpenYFT(Context context, int userId) {
        String uids = getString(context, SHOW_OPEN_YFT);
        return StringUtils.contains(uids, userId + "", ",");
    }

    /**
     * @description 缓存安全问题到本地
     * @created 2014-9-4 下午2:58:30
     * @author ZZB
     */
    public static void setSafeQuestions(Context context, String questions) {
        setString(context, SAFE_QUESTIONS, questions);
    }

    /**
     * @description 获取本地缓存的安全问题
     * @created 2014-9-4 下午2:59:00
     * @author ZZB
     */
    public static String getSafeQuestions(Context context) {
        return getString(context, SAFE_QUESTIONS);
    }


//    /**
//     * @description 存本地绑定过的手机[{userid:'', phone:''}]
//     * @created 2014-9-3 下午3:30:36
//     * @author ZZB
//     */
//    public static void setBindPhoneNums(Context context, String phoneNos) {
//        setString(context, USER_BIND_PHONE_NUMS, phoneNos);
//    }

    /**
     * @description 获取本地绑定过的手机[{userid:'', phone:''}]
     * @created 2014-9-3 下午3:31:27
     * @author ZZB
     */
//    public static String getBindPhoneNums(Context context) {
//        return getString(context, USER_BIND_PHONE_NUMS);
//    }

    /**
     * @description 设置是否设置支付密码的用户ids
     * @created 2014-9-3 上午10:55:27
     * @author ZZB
     */
//    public static void setHasSetPayPswUserIds(Context context, String userIds) {
//        setString(context, SET_PAYPSW_USERIDS, userIds);
//    }

    /**
     * @description 获取绑定支付密码的用户ids
     * @created 2014-9-3 上午10:56:29
     * @author ZZB
     */
//    public static String getHasSetPayPswUserIds(Context context) {
//        return getString(context, SET_PAYPSW_USERIDS);
//    }

    // /**
    // * @description 设置是否绑定手机的用户ids
    // * @created 2014-9-3 上午10:37:03
    // * @author ZZB
    // */
    // public static void setHasBindPhoneUserIds(Context context, String
    // userIds){
    // setString(context, HAS_BIND_PHONE, userIds);
    // }
    // /**
    // * @description 获取是否绑定手机的用户ids
    // * @created 2014-9-3 上午10:37:13
    // * @author ZZB
    // */
    // public static String getHasBindPhoneUserIds(Context context){
    // return getString(context, HAS_BIND_PHONE);
    // }

    /**
     * @description 获取已经绑定银行的用户ids
     * @created 2014-9-3 上午9:57:52
     * @author ZZB
     */
//    public static String getHasBindBankUserIds(Context context) {
//        return getString(context, HAS_BIND_BANK);
//    }

    /**
     * @description 设置已经绑定银行的用户ids, 用逗号隔开
     * @created 2014-9-3 上午9:56:34
     * @author ZZB
     */
//    public static void setHasBindBankUserIds(Context context, String userIds) {
//        setString(context, HAS_BIND_BANK, userIds);
//    }

    /**
     * @description 设置用户信息到sp
     * @created 2014-9-17 上午11:59:24
     * @author ZZB
     */
    public static void setUserInfo(Context context, UserModel user) {
        setSignature(context, user.getSignature());
        // setAllitemcount(context, user.getAllItemCount());
        // setAllagentcount(context, user.getAllAgentCount());
        // setAllvendorcount(context, user.getAllVendorCount());
        setUserId(context, user.getUserID());
        setUserName(context, user.getUserName());
    }

    /**
     * @description 删除用户信息
     * @created 2014-8-27 上午10:02:14
     * @author ZZB
     */
    public static void clearUserInfo(Context context) {
        remove(context, UserSig,Identifier,TX_TOKEN,COOKIE, USER_NAME, USER_ID, USER_BALANCE, FANS_NUM, ALLITEMCOUNT, ALLVENDORCOUNT, ALLAGENTCOUNT);
    }

    /**
     * @description 删除店铺信息
     * @created 2014-8-27 上午10:01:48
     * @author ZZB
     */
    public static void clearShopInfo(Context context) {
        remove(context, SHOP_ID, SHOP_NAME, SHOP_DOMAIN, SHOP_LOGO, SHOP_BACKGROUND, SHOP_ITEM_CAT,
                IS_JOIN_24_HR_RETURN, IS_JOIN_7DAY_DELIVERY, IS_JOIN_CREDIT, IS_JOIN_MICRO_SOURCES,
                IS_JOIN_MIXED_BATCH, IS_JOIN_ONE_SAMPLE, _7DAYS_CREDIT_JOIN_STATU);
    }

    /**
     * @description 获取本地所有用户联系方式, 格式为json
     * @created 2014-8-19 下午2:59:11
     * @author ZZB
     */
    public static String getContactInfos(Context context) {
        return getString(context, CONTACT_INFOS, "");
    }

    /**
     * @description 设置联系方式
     * @created 2014-8-19 下午2:59:41
     * @author ZZB
     */
    public static void setContactInfos(Context context, String infos) {
        setString(context, CONTACT_INFOS, infos);
    }

    /**
     * @description 获取地址信息
     * @created 2014-11-11
     * @author PJ
     */
    public static String getContactAddressInfos(Context context) {
        return getString(context, CONTACT_ADDRESS_INFOS, "");
    }

    /**
     * @description 设置地址
     * @created 2014-11-11
     * @author PJ
     */
    public static void setContactAddressInfos(Context context, String address) {
        setString(context, CONTACT_ADDRESS_INFOS, address);
    }


    public static String getSignature(Context context) {
        return getString(context, SIGNATURE, "");
    }

    public static int getAllitemcount(Context context) {
        return getInt(context, ALLITEMCOUNT, 0);
    }

    public static int getAllagentcount(Context context) {
        return getInt(context, ALLAGENTCOUNT, 0);
    }

    public static int getAllvendorcount(Context context) {
        return getInt(context, ALLVENDORCOUNT, 0);
    }

    public static void setSignature(Context context, String value) {
        setString(context, SIGNATURE, value);
    }


    /**
     * Description:获取已经上传过的ITEM信息 2014-7-24 下午8:38:46
     *
     * @author ZZB
     */
    public static String getUploadItems(Context context) {
        return getString(context, UPLOADED_ITEMS, "");
    }

    /**
     * Description:设置已经上传过的item 2014-7-24 下午8:40:55
     *
     * @author ZZB
     */
    public static void setUploadedItems(Context context, String item) {
        setString(context, UPLOADED_ITEMS, item);
    }


    /**
     * Description:设置店铺logo 2014-7-18下午2:58:49
     *
     * @author ZZB
     */
    public static void setShopLogo(Context context, String logo) {
        setString(context, SHOP_LOGO, logo);
    }

    /**
     * Description:获取店铺logo 2014-7-18下午2:58:40
     *
     * @author ZZB
     */
    public static String getShopLogo(Context context) {
        //return Const.getShopLogo(SpManager.getUserId(context));
        return getString(context, SHOP_LOGO, "");
    }

    ;

    /**
     * Description:设置用户id 2014-7-18下午3:04:05
     *
     * @author ZZB
     */
    public static void setUserId(Context context, int userId) {
        setInt(context, USER_ID, userId);
    }

    /**
     * Description:获取用户id 2014-7-18下午3:03:57
     *
     * @author ZZB
     */
    public static int getUserId(Context context) {
        return getInt(context, USER_ID, 0);
    }
    public static void setIdentifier(Context context, String userName) {
        setString(context, Identifier, userName);
    }

    /**
     * Description:获取用户名 2014-7-18下午2:58:23
     *
     * @author ZZB
     */
    public static String getIdentifier(Context context) {
        return getString(context, Identifier, "");
    }
    public static void setUserSig(Context context, String userName) {
        setString(context, UserSig, userName);
    }

    /**
     * Description:获取用户名 2014-7-18下午2:58:23
     *
     * @author ZZB
     */
    public static String getUserSig(Context context) {
        return getString(context, UserSig, "");
    }

    /**
     * Description:设置用户名 2014-7-18下午2:58:30
     *
     * @author ZZB
     */
    public static void setUserName(Context context, String userName) {
        setString(context, USER_NAME, userName);
        CrashReport.setUserId(userName);//设置用户的唯一标识
    }

    /**
     * Description:获取用户名 2014-7-18下午2:58:23
     *
     * @author ZZB
     */
    public static String getUserName(Context context) {
        return getString(context, USER_NAME, "");
    }

    /**
     * Description:设置店铺域名 2014-7-18下午2:58:15
     *
     * @author ZZB
     */
    public static void setShopDomain(Context context, String domain) {
        setString(context, SHOP_DOMAIN, domain);
    }

    /**
     * Description:获取店铺域名 2014-7-18下午2:58:07
     *
     * @author ZZB
     */
    public static String getShopDomain(Context context) {
        return getString(context, SHOP_DOMAIN, "");
    }

    /**
     * Description:设置店铺名 2014-7-18下午2:58:00
     *
     * @author ZZB
     */
    public static void setShopName(Context context, String shopName) {
        setString(context, SHOP_NAME, shopName);
    }

    /**
     * Description:获取店铺名 2014-7-18下午2:57:52
     *
     * @author ZZB
     */
    public static String getShopName(Context context) {
        return getString(context, SHOP_NAME, "");
    }

    /**
     * Description:设置店招
     */
    public static void setShopBanner(Context context, String banner) {
        setString(context, SHOP_BANNER, banner);
    }

    /**
     * Description:获取店招
     */
    public static String getShopBanner(Context context) {
        return getString(context, SHOP_BANNER, "");
    }

    /**
     * Description:设置招募文案
     */
    public static void setShopRecruitDesc(Context context, String recruitDesc) {
        setString(context, SHOP_RECRUITDESC, recruitDesc);
    }

    /**
     * Description:获取招募文案
     */
    public static String getShopRecruitDesc(Context context) {
        return getString(context, SHOP_RECRUITDESC, "");
    }

    /**
     * Description:设置店铺ID 2014-7-18下午2:46:45
     *
     * @author ZZB
     */
    public static void setShopId(Context context, long shopId) {
        setLong(context, SHOP_ID, shopId);
    }

    /**
     * Description:获取店铺ID 2014-7-18下午2:47:33
     *
     * @author ZZB
     */
    public static long getShopId(Context context) {
        return getLong(context, SHOP_ID, 0);
    }

    /**
     * Description:设置最后一次登录的用户账号 2014-7-4下午3:48:00
     */
    public static void setLoginAccount(Context context, String loginAccount) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(LOGIN_ACCOUNT, loginAccount).commit();

        addLoginAccounts(context, loginAccount);
    }

    /**
     * Description:设置最后一次登录的密码 2014-7-4下午3:48:00
     */
    public static void setLoginPwd(Context context, String loginPwd) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(LOGIN_PWD, loginPwd).commit();
        addLoginAccounts(context, loginPwd);
    }

    /**
     * Description:获取最后一次登录的用户密码2014-7-4下午3:49:44
     */
    public static String getListFilter(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(GETLISTFILTER, "");
    }
    /**
     * Description:设置最后一次登录的密码 2014-7-4下午3:48:00
     */
    public static void setListFilter(Context context, String loginPwd) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(GETLISTFILTER, loginPwd).commit();
        addLoginAccounts(context, loginPwd);
    }

    /**
     * Description:获取最后一次登录的用户密码2014-7-4下午3:49:44
     */
    public static String getLoginPwd(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(LOGIN_PWD, "");
    }
    /**
     * Description:获取最后一次登录的用户账号 2014-7-4下午3:49:44
     */
    public static String getLoginAccount(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(LOGIN_ACCOUNT, "");
    }

    /**
     * 加入已登录过的账号
     *
     * @param context
     * @param loginAccount
     */
    public static void addLoginAccounts(Context context, String loginAccount) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String account = getLoginAccounts(context);
        if (!StringUtils.contains(account, loginAccount, ",")) {// 不存在账号，添加
            sp.edit().putString(LOGIN_ACCOUNTS, account + loginAccount + ",").commit();
        }
    }

    /**
     * 获取登录过的所有账号，注：账号间以“,”隔开
     *
     * @param context
     * @return
     */
    public static String getLoginAccounts(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(LOGIN_ACCOUNTS, "");
    }

    /**
     * 删除某个关键字
     *
     * @param context
     * @param text
     * @return
     */
    public static String deleteLoginAccounts(Context context, String text) {
        String newChar = SpManager.getLoginAccounts(context.getApplicationContext()).replace(text + ",", "");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(LOGIN_ACCOUNTS, newChar).commit();
        return newChar;
    }


    /**
     * Description:获取上传新商品时的可视范围的id 2014-7-7上午9:32:40
     */
    public static String getUploadNewItemVisibleRanageIds(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(VISIBLE_RANAGE_IDS, SystemGroupId.ALL_PPL + "");// 默认公开
    }

    /**
     * Description:设置上传商品时的可视范围的id 2014-7-7上午9:43:07
     */
    public static void setUploadNewItemVisibleRanageIds(Context context, String groupIds) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(VISIBLE_RANAGE_IDS, groupIds).commit();
    }

    /**
     * Description:设置上传商品时可视范围的名称 2014-7-13 下午10:48:34
     */
    public static void setUploadNewItemVisibleRangeNames(Context context, String visibleRangeNames) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(VISIBLE_RANGE_NAMES, visibleRangeNames).commit();
    }

    /**
     * Description:获取上传商品时可视范围的名称 2014-7-13 下午10:51:12
     */
    public static String getUploadNewItemVisibleRangeNames(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(VISIBLE_RANGE_NAMES, "公开");// 默认公开
    }

    /**
     * Description:设置cookie 2014-7-18下午2:29:31
     *
     * @author ZZB
     */
    public static void setCookie(Context context, String cookie) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(COOKIE, cookie).commit();
    }

    /**
     * Description:拿cookie 2014-7-18下午2:28:34
     *
     * @author ZZB
     */
    public static String getCookie(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(COOKIE, "");
    }

    /**
     * Description:设置是否是第一次使用app 2014-7-18下午2:34:21
     *
     * @author ZZB
     */
    public static void setIsFirstUseApp(Context context, boolean isFirstUse) {
        setBoolean(context, IS_FIRST_USE_APP, isFirstUse);
    }

    /**
     * Description:是否是第一次使用app, 默认是第一 2014-7-18下午2:36:03
     *
     * @author ZZB
     */
    public static boolean isFirstUseApp(Context context) {
        return getBoolean(context, IS_FIRST_USE_APP, true);
    }

    /**
     * Description:设置是否提示联系方式
     *
     * @author pj
     */
    public static void setContactMsgTip(Context context, boolean isTip) {
        setBoolean(context, HAS_CONTACT_MSG, isTip);
    }


    /**
     * Description:设置首次使用app的时间 2014-7-18下午2:40:10
     *
     * @author ZZB
     */
    public static void setFirstUseTime(Context context, String time) {
        setString(context, FIRST_USE_TIME, time);
    }

    public static void setStallKeyWord(Context context, String time) {
        setString(context, STALL_KEY_WORD, time);
    }
    public static String getStallKeyWord(Context context) {
       return getString(context, STALL_KEY_WORD);
    }
    public static void setTxToken(Context context, String time) {
        setString(context, TX_TOKEN, time);
    }
    public static String getTxToken(Context context) {
        return getString(context, TX_TOKEN);
    }
    /**
     * Description:获取第一次使用app的时间 2014-7-18下午2:40:58
     *
     * @author ZZB
     */
    public static String getFirstUseTime(Context context) {
        return getString(context, FIRST_USE_TIME, "");
    }

    public static String getString(Context context, String key) {
        return getString(context, key, "");
    }

    public static String getString(Context context, String key, String defValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(key, defValue);
    }

    public static void setString(Context context, String key, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(key, value).commit();
    }

    public static boolean getBoolean(Context context, String key, boolean defValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(key, defValue);
    }

    public static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(key, value).commit();
    }

    public static int getInt(Context context, String key, int defValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(key, defValue);
    }

    public static void setInt(Context context, String key, int value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(key, value).commit();
    }

    private static long getLong(Context context, String key, long defValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(key, defValue);
    }

    private static void setLong(Context context, String key, long value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putLong(key, value).commit();
    }

    private static float getFloat(Context context, String key, float defValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getFloat(key, defValue);
    }

    private static void setFloat(Context context, String key, float value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putFloat(key, value).commit();
    }

    private static void remove(Context context, String... keys) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Editor edit = sp.edit();
        for (String key : keys) {
            edit.remove(key);
        }
        edit.commit();
    }

    /**
     * im
     *
     * @author zc
     */
    public static void setSettingMsgNotification(Context context, boolean paramBoolean) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(SHARED_KEY_SETTING_NOTIFICATION, paramBoolean).commit();
    }


    public static void setSettingMsgSound(Context context, boolean paramBoolean) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(SHARED_KEY_SETTING_SOUND, paramBoolean).commit();

    }


    public static void setSettingFriend(Context context, boolean paramBoolean) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(SHARED_KEY_SETTING_FRIEND, paramBoolean).commit();

    }


    public static void setSettingMsgVibrate(Context context, boolean paramBoolean) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(SHARED_KEY_SETTING_VIBRATE, paramBoolean).commit();
    }


    public static void setSettingMsgSpeaker(Context context, boolean paramBoolean) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(SHARED_KEY_SETTING_SPEAKER, paramBoolean).commit();
    }


    /**
     * 加入搜索店铺记录
     *
     * @param context
     * @param word
     */
    public static void addSearchShopHistories(Context context, String word) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String histories = getSearchShopHistories(context);
        if (!StringUtils.contains(histories, word, ",")) {// 不存在keyword，添加
            sp.edit().putString(PREF_SEARCH_SHOP_HISTORIES, word + "," + histories).commit();
        }
    }

    /**
     * 获取搜索记录，注：账号间以“,”隔开
     *
     * @param context
     * @return
     */
    public static String getSearchShopHistories(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_SEARCH_SHOP_HISTORIES, "");
    }

    /**
     * 删除某个关键字
     *
     * @param context
     * @param text
     * @return
     */
    public static String deleteSearchShopHistories(Context context, String text) {
        String newChar = SpManager.getSearchShopHistories(context.getApplicationContext()).replace(text + ",", "");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_SEARCH_SHOP_HISTORIES, newChar).commit();
        return newChar;
    }

    /**
     * 加入搜索商品记录
     *
     * @param context
     * @param word
     */
    public static void addSearchItemHistories(Context context, String word) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String histories = getSearchItemHistories(context);
        if (!StringUtils.contains(histories, word, ",")) {// 不存在keyword，添加
            sp.edit().putString(PREF_KEY_ITEM_SEARCH_LOG, word + "," + histories).commit();
        }
    }

    /**
     * 删除某个关键字
     *
     * @param context
     * @param text
     * @return
     */
    public static String deleteSearchItemHistories(Context context, String text) {
        String newChar = SpManager.getSearchItemHistories(context.getApplicationContext()).replace(text + ",", "");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_KEY_ITEM_SEARCH_LOG, newChar).commit();
        return newChar;
    }

    /**
     * 获取搜索商品记录，注：账号间以“,”隔开
     *
     * @param context
     * @return
     */
    public static String getSearchItemHistories(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_KEY_ITEM_SEARCH_LOG, "");
    }


    /**
     * 加入搜索供应商记录
     *
     * @param context
     * @param word
     */
    public static void addSearchVendorsHistories(Context context, String word) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String histories = getSearchVendorsHistories(context);
        if (!StringUtils.contains(histories, word, ",")) {// 不存在keyword，添加
            sp.edit().putString(PREF_KEY_VENDORS_SEARCH_LOG, word + "," + histories).commit();
        }
    }

    /**
     * 获取搜索供应商记录，注：账号间以“,”隔开
     *
     * @param context
     * @return
     */
    public static String getSearchVendorsHistories(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_KEY_VENDORS_SEARCH_LOG, "");
    }

    /**
     * 删除某个关键字
     *
     * @param context
     * @param text
     * @return
     */
    public static String deleteSearchVendorsHistories(Context context, String text) {
        String newChar = SpManager.getSearchVendorsHistories(context.getApplicationContext()).replace(text + ",", "");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_KEY_VENDORS_SEARCH_LOG, newChar).commit();
        return newChar;
    }

    /**
     * @description 保存店铺信息
     * @created 2015-3-2 下午2:24:42
     * @author ZZB
     */
    public static void setShopInfo(Context context, ShopInfoModel shopinfo) {
        SpManager.setShopId(context, shopinfo.getShopID());
        SpManager.setShopName(context, shopinfo.getName());
        SpManager.setShopBanner(context, shopinfo.getBanner());
        SpManager.setShopRecruitDesc(context, shopinfo.getRecruitDesc());
        SpManager.setShareShopText(context, shopinfo.getShareDesc());
        SpManager.setShopDomain(context, shopinfo.getDomain());
        SpManager.setShopLogo(context, shopinfo.getLogo());
        SpManager.setUserName(context, shopinfo.getUserName());
//        ShopCreditItem creditItem = shopinfo.getShopCreditItem();
//        if (creditItem != null) {
//            SpManager.setJoin24HrReturn(context, creditItem.isJoin24HrReturn());
//            SpManager.setJoin7DaysDelivery(context, creditItem.isJoinSevenDaysDelivery());
//            SpManager.setJoinCredit(context, creditItem.isJoinCredit());
//            SpManager.setJoinMicroSources(context, creditItem.isJoinMicroSources());
//            SpManager.setJoinMixedBatch(context, creditItem.isJoinMixedBatch());
//            SpManager.setJoinOneSample(context, creditItem.isJoinOneSample());
//        }
    }
}
