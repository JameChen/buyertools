package com.nahuo.buyertool.Bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jame on 2017/8/8.
 */

public class ReturnUploadBean {

    /**
     * ItemID : 1119219
     * Key : 48034373
     * AgentItemID : 983592
     * Cover : upyun:banwo-img-server://426188/item/1502160190931.jpg
     * Name : 你们
     * RetailPrice : 1.00
     * Price : 1.00
     */

    @SerializedName("ItemID")
    private int ItemID;
    @SerializedName("Key")
    private int Key;
    @SerializedName("AgentItemID")
    private int AgentItemID;
    @SerializedName("Cover")
    private String Cover;
    @SerializedName("Name")
    private String Name;
    @SerializedName("RetailPrice")
    private String RetailPrice;
    @SerializedName("Price")
    private String Price;

    public int getItemID() {
        return ItemID;
    }

    public void setItemID(int ItemID) {
        this.ItemID = ItemID;
    }

    public int getKey() {
        return Key;
    }

    public void setKey(int Key) {
        this.Key = Key;
    }

    public int getAgentItemID() {
        return AgentItemID;
    }

    public void setAgentItemID(int AgentItemID) {
        this.AgentItemID = AgentItemID;
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

    public String getRetailPrice() {
        return RetailPrice;
    }

    public void setRetailPrice(String RetailPrice) {
        this.RetailPrice = RetailPrice;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String Price) {
        this.Price = Price;
    }
}
