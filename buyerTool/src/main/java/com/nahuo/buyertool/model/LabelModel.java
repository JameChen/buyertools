package com.nahuo.buyertool.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/20 0020.
 */
public class LabelModel implements Serializable {
    @Expose
    private String ID;//标签ID
    @Expose
    private String Name;//标签NAME

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

}
