package com.nahuo.buyertool.Bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by jame on 2017/7/12.
 */

public class MyStyleBean implements Serializable{

    private static final long serialVersionUID = 3964700508093421401L;
    /**
     * ItemID : 962855
     * Cover : upyun:nahuo-img-server://3636/item/1499222386.jpg
     * Name : Liyifeiyang0点05内网测试第5款
     * StatuID : 1
     * Statu : 已上架
     * Price : 7.50
     * IsHide : false
     * Remark :
     */
    @Expose
    @SerializedName("NameTag")
    private String NameTag="";
    @Expose
    public boolean IsWarnTag;
    @Expose
    private long SourceID;
    @Expose
    private int Stock;

    public String getNameTag() {
        return NameTag;
    }

    public void setNameTag(String nameTag) {
        NameTag = nameTag;
    }

    public long getSourceID() {
        return SourceID;
    }

    public void setSourceID(long sourceID) {
        SourceID = sourceID;
    }

    public int getStock() {
        return Stock;
    }

    public void setStock(int stock) {
        Stock = stock;
    }

    @Expose
    @SerializedName("ItemID")
    private int ItemID;
    public boolean is_check=false;

    public boolean is_check() {
        return is_check;
    }

    public void setIs_check(boolean is_check) {
        this.is_check = is_check;
    }

    @Expose
    @SerializedName("Cover")

    private String Cover;
    @Expose
    @SerializedName("Name")
    private String Name="";
    @Expose
    @SerializedName("StatuID")
    private int StatuID;
    @Expose
    @SerializedName("Statu")
    private String Statu;
    @Expose
    @SerializedName("Price")
    private String Price;
    @Expose
    @SerializedName("IsHide")
    private boolean IsHide;
    @Expose
    @SerializedName("Remark")
    private String Remark="";

    public int getItemID() {
        return ItemID;
    }

    public void setItemID(int ItemID) {
        this.ItemID = ItemID;
    }

    public String getCover() {
        return Cover;
    }

    public void setCover(String Cover) {
        this.Cover = Cover;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public int getStatuID() {
        return StatuID;
    }

    public void setStatuID(int StatuID) {
        this.StatuID = StatuID;
    }

    public String getStatu() {
        return Statu;
    }

    public void setStatu(String Statu) {
        this.Statu = Statu;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String Price) {
        this.Price = Price;
    }

    public boolean isIsHide() {
        return IsHide;
    }

    public void setIsHide(boolean IsHide) {
        this.IsHide = IsHide;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String Remark) {
        this.Remark = Remark;
    }
}
