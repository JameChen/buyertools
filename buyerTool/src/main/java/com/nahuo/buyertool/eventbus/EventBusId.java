package com.nahuo.buyertool.eventbus;

/**
 * @author ZZB
 * @description EventBus 事件id
 * @created 2015-4-24 上午9:56:38
 */
public class EventBusId {
    public static final int BACKGROUND_ACTION = 45;
    public static final int GOODSBROADCASTRECEIVER_ACTION = 43;
    /**************
     * 注意，所有新添加的id不能重复
     ****************/
    /**
     * 上架回调
     */
    public static final int COMMON_LIST_CALCEL_KB= 41;
    public static final int BB_SELECT_ITEM_SHOP_UP = 38;
    public static final int CHANGE_UPLOADITEM_REFESH_DB=39;
    public static final int REFERSH_MYSTYLE_FINISH = 40;
    public static final int COMMON_LIST_RELOAD = 37;
    public static final int SEARCH_异常单 = 44;
    public static final int SEARCH_退款单 = 36;
    public static final int SEARCH_欠货单 = 35;
    public static final int SEARCH_入库单 = 34;
    public static final int SEARCH_已开单 = 33;
    public static final int SEARCH_待开单 = 32;
    public static final int SEARCH_开或欠单 = 42;
    public static final int LOAD_PIN_HUO_FINISHED = 31;
    public static final int REFRESH_PIN_HUO = 30;
    /***
     * 通知主界面管理消息数
     */
    public static final int MANAGER_MSG_NUMBER = 29;
    /**
     * 上传商品
     */
    public static final int ON_UPLOAD_ITEM_CLICK = 28;
    /**
     * 分享到微铺成功
     */
    public static final int ON_SHARE_2_WP_SUCCESS = 27;

    /**
     * 收到jpush新订单，刷新订单列表
     */
    public static final int REFRESH_ORDER_MANAGER = 26;
    /**
     * 网银支付支付成功
     */
    public static final int BANK_PAY_SUCCESS = 25;
    /**
     * 网银支付失败
     */
    public static final int BANK_PAY_FAIL = 24;

    /**
     * 微信支付成功
     */
    public static final int WECHAT_PAY_SUCCESS = 23;

    /**
     * 修改报给上家的地址信息
     */
    public static final int UP_SUPER_ADDRESS = 22;

    /**
     * 退出应用
     */
    public static final int ON_APP_EXIT = 21;
    /**
     * 店铺logo修改
     */
    public static final int SHOP_LOGO_UPDATED = 20;
    /**
     * 修改订单商品数量
     */
    public static final int CHANGE_NUMBER = 19;
    /**
     * 添加订单备注
     */
    public static final int ADD_MEMO = 18;
    /**
     * 订单支付成功
     */
    public static final int ORDER_PAY_SUCCESS = 17;

    /**
     * 取消订单
     */
    public static final int CANCEL_ORDER = 16;
    /**
     * 供货商 退货
     */
    public static final int REFUND_SUPP_AGRESS = 15;
    /**
     * 买家退货
     */
    public static final int REFUND_BUYER_AGRESS = 14;
    /**
     * 卖家退货
     */
    public static final int REFUND_SELLER_AGRESS = 13;

    /**
     * 发货
     */
    public static final int SEND_GOOD = 12;
    /**
     * 确认收货
     */
    public static final int SURE_GET_GOOD = 11;
    /**
     * 改价
     */
    public static final int CHANGE_PRICE = 10;
    /**
     * 刷新购物车
     */
    public static final int REFRESH_SHOP_CART = 9;

    /**
     * 供应商TAB切换
     */
    public static final int ALL_ITEM_CHANGE_CURRENT_TAB = 8;
    /**
     * 主页切换TAB
     */
    public static final int MAIN_CHANGE_CURRENT_TAB = 7;

    /**
     * 微询新消息
     */
    public static final int WEIXUN_NEW_MSG = 6;
    /**
     * 上传列表有商品被删除
     */
    public static final int UPLOAD_LIST_HAS_ITEM_DELETED = 5;
    /**
     * 关注供货商改变
     */
    public static final int FOLLOW_VENDORS_CHANGED = 1;
    /**
     * 代理与取消代理供货商
     */
    public static final int AGENT_VENDOR_CHANGED = 2;
    /**
     * 刷新all items
     */
    public static final int REFRESH_ALL_ITEMS = 3;
    //关联成功后刷新数据
    public static final int NOTE_POP_F5 = 105;
    public static final int NOTE_POP_F5_DAIKAIDAN = 106;
    public static final int hehe = 99;
}
