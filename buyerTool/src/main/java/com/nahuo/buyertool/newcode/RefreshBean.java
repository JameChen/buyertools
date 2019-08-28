package com.nahuo.buyertool.newcode;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * ===============BuyerTool====================
 * author：陈振
 * Email：18620156376@163.com
 * Time : 2016/7/13 16:18
 * Description :用于待开单页面开单完成之后返回列表页的数据
 * ===============BuyerTool====================
 */
public class RefreshBean implements Parcelable {
    public RefreshBean() {
    }

    public RefreshBean(int tempId, int buyNum) {
        this.tempId = tempId;
        this.buyNum = buyNum;
    }

    private int tempId;//订单id
    private int buyNum;//采购数量

    public int getTempId() {
        return tempId;
    }

    public void setTempId(int tempId) {
        this.tempId = tempId;
    }

    public int getBuyNum() {
        return buyNum;
    }

    public void setBuyNum(int buyNum) {
        this.buyNum = buyNum;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.tempId);
        dest.writeInt(this.buyNum);
    }

    protected RefreshBean(Parcel in) {
        this.tempId = in.readInt();
        this.buyNum = in.readInt();
    }

    public static final Creator<RefreshBean> CREATOR = new Creator<RefreshBean>() {
        @Override
        public RefreshBean createFromParcel(Parcel source) {
            return new RefreshBean(source);
        }

        @Override
        public RefreshBean[] newArray(int size) {
            return new RefreshBean[size];
        }
    };
}
