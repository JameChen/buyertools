package com.nahuo.buyertool.Bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jame on 2018/8/2.
 */

public class StyleBean implements Serializable {
    private static final long serialVersionUID = 3940063016175763494L;
    @Expose
    private int Total;
    @SerializedName("Datas")
    @Expose
    private List<MyStyleBean>  Datas;

    public int getTotal() {
        return Total;
    }

    public void setTotal(int total) {
        Total = total;
    }

    public List<MyStyleBean> getDatas() {
        return Datas;
    }

    public void setDatas(List<MyStyleBean> datas) {
        Datas = datas;
    }
}
