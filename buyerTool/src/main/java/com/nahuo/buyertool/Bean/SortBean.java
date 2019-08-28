package com.nahuo.buyertool.Bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jame on 2018/5/16.
 */

public class SortBean {

    /**
     * Type : paid
     * List1 : [{"Text":"筛选","Value":-1},{"Text":"已截单","Value":1},{"Text":"拼货中","Value":2},{"Text":"已截止入库","Value":3}]
     * List2 : [{"Text":"排序","Value":-1},{"Text":"楼层档口归类","Value":1},{"Text":"单价高到低","Value":2}]
     */
    @Expose
    @SerializedName("Type")
    private String Type;
    @Expose
    @SerializedName("List1")
    private List<ListBean> Listfilter;
    @Expose
    @SerializedName("List2")
    private List<ListBean> ListSort;

    public String getType() {
        return Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

    public List<ListBean> getListfilter() {
        return Listfilter;
    }

    public void setListfilter(List<ListBean> Listfilter) {
        this.Listfilter = Listfilter;
    }

    public List<ListBean> getListSort() {
        return ListSort;
    }

    public void setList2(List<ListBean> ListSort) {
        this.ListSort = ListSort;
    }

    public static class ListBean {
        /**
         * Text : 筛选
         * Value : -1
         */
        public boolean isSelect=false;
        @Expose
        @SerializedName("Text")
        private String Text="";
        @Expose
        @SerializedName("Value")
        private int Value;

        public String getText() {
            return Text;
        }

        public void setText(String Text) {
            this.Text = Text;
        }

        public int getValue() {
            return Value;
        }

        public void setValue(int Value) {
            this.Value = Value;
        }
    }


}
