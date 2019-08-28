package com.nahuo.buyertool.Bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jame on 2018/1/11.
 */

public class VendorsBean implements Serializable {
    private static final long serialVersionUID = 2819962814837169176L;

    /**
     * Total : 1
     * PageIndex : 1
     * Datas : [{"ShopID":15288,"ShopName":"乐小福","Domain":"lele","Logo":"upyun:banwo-img-server:/u15288/shop/logo1411993116871.jpg","UserID":148934,"UserName":"乐小福","Signature":"爱是无所不能的承受与付出！"}]
     */
    @Expose
    @SerializedName("Total")
    private int Total;
    @Expose
    @SerializedName("PageIndex")
    private int PageIndex;
    @Expose
    @SerializedName("Datas")
    private List<DatasBean> Datas;

    public int getTotal() {
        return Total;
    }

    public void setTotal(int Total) {
        this.Total = Total;
    }

    public int getPageIndex() {
        return PageIndex;
    }

    public void setPageIndex(int PageIndex) {
        this.PageIndex = PageIndex;
    }

    public List<DatasBean> getDatas() {
        return Datas;
    }

    public void setDatas(List<DatasBean> Datas) {
        this.Datas = Datas;
    }

    public static class DatasBean implements Serializable {
        private static final long serialVersionUID = -1720580987248903176L;
        /**
         * ShopID : 15288
         * ShopName : 乐小福
         * Domain : lele
         * Logo : upyun:banwo-img-server:/u15288/shop/logo1411993116871.jpg
         * UserID : 148934
         * UserName : 乐小福
         * Signature : 爱是无所不能的承受与付出！
         */
        @Expose
        @SerializedName("ShopID")
        private int ShopID;
        @Expose
        @SerializedName("ShopName")
        private String ShopName="";
        @Expose
        @SerializedName("Domain")
        private String Domain="";
        @Expose
        @SerializedName("Logo")
        private String Logo="";
        @Expose
        @SerializedName("UserID")
        private int UserID;
        @Expose
        @SerializedName("UserName")
        private String UserName="";
        @Expose
        @SerializedName("Signature")
        private String Signature;

        public int getShopID() {
            return ShopID;
        }

        public void setShopID(int ShopID) {
            this.ShopID = ShopID;
        }

        public String getShopName() {
            return ShopName;
        }

        public void setShopName(String ShopName) {
            this.ShopName = ShopName;
        }

        public String getDomain() {
            return Domain;
        }

        public void setDomain(String Domain) {
            this.Domain = Domain;
        }

        public String getLogo() {
            return Logo;
        }

        public void setLogo(String Logo) {
            this.Logo = Logo;
        }

        public int getUserID() {
            return UserID;
        }

        public void setUserID(int UserID) {
            this.UserID = UserID;
        }

        public String getUserName() {
            return UserName;
        }

        public void setUserName(String UserName) {
            this.UserName = UserName;
        }

        public String getSignature() {
            return Signature;
        }

        public void setSignature(String Signature) {
            this.Signature = Signature;
        }
    }
}
