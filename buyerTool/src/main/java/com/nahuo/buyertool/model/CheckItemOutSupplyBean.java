package com.nahuo.buyertool.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by jame on 2018/5/28.
 */

public class CheckItemOutSupplyBean {

    /**
     * Type : 1
     * Tips :
     */
    @Expose
    @SerializedName("Type")
    private int Type;
    @Expose
    @SerializedName("Tips")
    private String Tips="";

    public int getType() {
        return Type;
    }

    public void setType(int Type) {
        this.Type = Type;
    }

    public String getTips() {
        return Tips;
    }

    public void setTips(String Tips) {
        this.Tips = Tips;
    }
}
