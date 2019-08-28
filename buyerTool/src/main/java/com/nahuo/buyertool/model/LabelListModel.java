package com.nahuo.buyertool.model;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by Administrator on 2017/3/20 0020.
 */
public class LabelListModel  {
    @Expose
    public List<LabelModel> Tags;

    public List<LabelModel> getTags() {
        return Tags;
    }
}
