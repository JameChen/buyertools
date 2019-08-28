package com.nahuo.buyertool.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class OrderButton implements Serializable{

    private static final long serialVersionUID = -3634477538870340066L;
    @Expose
    private String  action;
    @Expose
    private String  data;
    @Expose
    private String  title;
    @Expose
    private boolean isEnable; // 是否显示
    @Expose
    private boolean isPoint; // 是否加重显示
    @Expose
    private String type;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean isEnable) {
        this.isEnable = isEnable;
    }

    public boolean isPoint() {
        return isPoint;
    }

    public void setPoint(boolean isPoint) {
        this.isPoint = isPoint;
    }

}
