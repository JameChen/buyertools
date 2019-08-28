package com.nahuo.buyertool.newcode;

/**
 * ===============BuyerTool====================
 * author：ChenZhen
 * Email：18620156376@163.com
 * Time : 2016/7/8 13:19
 * Description :用于关联采购单接口提交数据的bean,beanToJson
 * ===============BuyerTool====================
 */
public class PurAndOrdBean {
    private int orderid;

    public PurAndOrdBean() {
    }

    public PurAndOrdBean(int orderid, int billingid) {
        this.orderid = orderid;
        this.billingid = billingid;
    }

    private int billingid;

    public int getOrderid() {
        return orderid;
    }

    public void setOrderid(int orderid) {
        this.orderid = orderid;
    }

    public int getBillingid() {
        return billingid;
    }

    public void setBillingid(int billingid) {
        this.billingid = billingid;
    }
}
