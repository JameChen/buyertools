package com.nahuo.buyertool.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 诚 on 2015/9/21.
 */
public class MeItemModel {
    public boolean is_Show() {
        return is_Show;
    }

    public void setIs_Show(boolean is_Show) {
        this.is_Show = is_Show;
    }

    /**
     * Text : 待开单汇总

     * Type : 2
     * Value : http://wap.admin.nahuo.com/buyertool/billlist?statu=1
     */
    boolean is_Show=false;
    @SerializedName("Text")
    private String Text;
    @SerializedName("Type")
    private int Type;
    @SerializedName("Value")
    private String Value;

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    private  int sourceId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private  String name;

    public String getText() {
        return Text;
    }

    public void setText(String Text) {
        this.Text = Text;
    }

    public int getType() {
        return Type;
    }

    public void setType(int Type) {
        this.Type = Type;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String Value) {
        this.Value = Value;
    }
}
