package com.nahuo.buyertool.model.PageraModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nahuo.buyertool.model.CommodityInfo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/3/22 0022.
 */
public class BaseInfoList implements Serializable{
    @Expose
    private List<CommodityInfo> WapList;
    @Expose
    private String LoginErrorMsg;
    @Expose
    @SerializedName("BtnList1")
    private List<BtnListBean> BtnList1;
    @Expose
    @SerializedName("BtnList2")
    private List<BtnListBean> BtnList2;
    @Expose
    @SerializedName("BtnList3")
    private List<BtnListBean> BtnList3;

    public List<CommodityInfo> getWapList() {
        return WapList;
    }

    public void setWapList(List<CommodityInfo> wapList) {
        WapList = wapList;
    }

    public String getLoginErrorMsg() {
        return LoginErrorMsg;
    }

    public void setLoginErrorMsg(String loginErrorMsg) {
        LoginErrorMsg = loginErrorMsg;
    }

    public List<BtnListBean> getBtnList1() {
        return BtnList1;
    }

    public void setBtnList1(List<BtnListBean> BtnList1) {
        this.BtnList1 = BtnList1;
    }

    public List<BtnListBean> getBtnList2() {
        return BtnList2;
    }

    public void setBtnList2(List<BtnListBean> BtnList2) {
        this.BtnList2 = BtnList2;
    }

    public List<BtnListBean> getBtnList3() {
        return BtnList3;
    }

    public void setBtnList3(List<BtnListBean> BtnList3) {
        this.BtnList3 = BtnList3;
    }

    public static class BtnListBean implements Serializable{
        private static final long serialVersionUID = -4619593063749762479L;
        /**
         * Text : 待开单汇总
         * Type : 2
         * Value : http://wap.admin.nahuo.com/buyertool/billlist?statu=1
         */
        @Expose
        @SerializedName("Text")
        private String Text="";
        @Expose
        @SerializedName("Type")
        private int Type;
        @Expose
        @SerializedName("Value")
        private String Value="";

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


}
