package com.nahuo.buyertool.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/17 0017.
 */
public class CommodityInfo implements Serializable {
     @Expose
     public String ReportText;//链接
    @Expose
     public String Url;//文字

    public String getText() {
        return ReportText;
    }

    public void setText(String text) {
        ReportText = text;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }
}
