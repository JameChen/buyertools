package com.nahuo.buyertool.Bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by jame on 2017/7/21.
 */

public class SearchStallBean implements Serializable {

    private static final long serialVersionUID = -6860055761187782684L;
    /**
     * MarketID : 1
     * MarketName : 广州十三行
     * FloorID : 4
     * FloorName : 1
     * StallsID : 1002
     * StallsName : A106
     */
    @Expose
    @SerializedName("MarketID")
    private int MarketID;
    @Expose
    @SerializedName("MarketName")
    private String MarketName;
    @Expose
    @SerializedName("FloorID")
    private int FloorID;
    @Expose
    @SerializedName("FloorName")
    private String FloorName;
    @Expose
    @SerializedName("StallsID")
    private int StallsID;
    @Expose
    @SerializedName("StallsName")
    private String StallsName;

    public int getMarketID() {
        return MarketID;
    }

    public void setMarketID(int MarketID) {
        this.MarketID = MarketID;
    }

    public String getMarketName() {
        return MarketName;
    }

    public void setMarketName(String MarketName) {
        this.MarketName = MarketName;
    }

    public int getFloorID() {
        return FloorID;
    }

    public void setFloorID(int FloorID) {
        this.FloorID = FloorID;
    }

    public String getFloorName() {
        return FloorName;
    }

    public void setFloorName(String FloorName) {
        this.FloorName = FloorName;
    }

    public int getStallsID() {
        return StallsID;
    }

    public void setStallsID(int StallsID) {
        this.StallsID = StallsID;
    }

    public String getStallsName() {
        return StallsName;
    }

    public void setStallsName(String StallsName) {
        this.StallsName = StallsName;
    }
}
