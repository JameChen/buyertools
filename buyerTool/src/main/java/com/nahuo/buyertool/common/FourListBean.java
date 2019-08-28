package com.nahuo.buyertool.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by jame on 2018/5/7.
 */

public class FourListBean implements Serializable{
    private static final long serialVersionUID = 6809270987384003187L;
    public boolean isSelect;
    /**
     * ID : 2
     * Name : 亚麻
     */
    @Expose
    @SerializedName("ID")
    private int ID;
    @Expose
    @SerializedName("Name")
    private String Name="";

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }
}
