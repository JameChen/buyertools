package com.nahuo.buyertool.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class TagModel implements Serializable {

    private static final long serialVersionUID = -2888648509672857565L;
    
    @Expose
    private int ID;
    @Expose
    private int Sort;
    @Expose
    private String Content;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getSort() {
        return Sort;
    }

    public void setSort(int sortID) {
        Sort = sortID;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }
}
