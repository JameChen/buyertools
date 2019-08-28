package com.nahuo.buyertool.Bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by jame on 2017/7/12.
 */

public class NotiBean implements Serializable{

    private static final long serialVersionUID = 7837923408467205991L;
    /**
     * Url : http://wap.admin.nahuo.com/quota/list
     * Text : 显示款被转总数27,多条控款信息，请看详细 >>
     */
    @Expose
    @SerializedName("Url")
    private String Url;
    @Expose
    @SerializedName("Text")
    private String Text;

    public String getUrl() {
        return Url;
    }

    public void setUrl(String Url) {
        this.Url = Url;
    }

    public String getText() {
        return Text;
    }

    public void setText(String Text) {
        this.Text = Text;
    }
}
