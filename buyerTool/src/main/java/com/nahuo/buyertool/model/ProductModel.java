package com.nahuo.buyertool.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class ProductModel implements Serializable {

    private static final long serialVersionUID = -3718423961923385896L;

    public long getDetailID() {
        return DetailID;
    }

    public void setDetailID(long detailID) {
        DetailID = detailID;
    }

    //    @Expose
//    private boolean isCheck;// 是否选中
    @Expose
    long DetailID;
    @Expose
    private String Color;// 颜色
    @Expose
    private String Code="";

    public String getTag() {
        return Tag;
    }

    public void setTag(String tag) {
        Tag = tag;
    }

    @Expose
    public String Tag="";
    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    @Expose
    private String Size;// 尺码
    @Expose
    private int PHQty;//拼货量
    @Expose
    private int YRKQty;//已入库量
    @Expose
    private int KDQty;//开单量
    @Expose
    private int DHQty;//到货量
    @Expose
    private int QHQty;//缺货量
    @Expose
    private int TKQty;//退款量

    @Expose
    private boolean IsOutSupply;//石否断货、

    @Expose
    private int StoreQty;//入库量
    @Expose
    private int Stock;// 库存量
    @Expose
    private int CompanyStock;//公司库存

    public int getCompanyStock() {
        return CompanyStock;
    }

    public void setCompanyStock(int companyStock) {
        CompanyStock = companyStock;
    }

    @Expose
    private double Price;// 预定量
    @Expose
    private String Cover;// 图片路径

    public int getStock() {
        return Stock;
    }

    public void setStock(int stock) {
        Stock = stock;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public String getCover() {
        return Cover;
    }

    public void setCover(String cover) {
        Cover = cover;
    }

    public boolean isOutSupply() {
        return IsOutSupply;
    }

    public void setIsOutSupply(boolean isOutSupply) {
        IsOutSupply = isOutSupply;
    }

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }

    public String getSize() {
        return Size;
    }

    public void setSize(String size) {
        Size = size;
    }

    public int getPHQty() {
        return PHQty;
    }

    public void setPHQty(int PHQty) {
        this.PHQty = PHQty;
    }

    public int getKDQty() {
        return KDQty;
    }

    public void setKDQty(int KDQty) {
        this.KDQty = KDQty;
        if(this.KDQty<0)
        {
            this.KDQty = getPHQty();
        }
        if(this.KDQty>getPHQty())
        {
           // this.KDQty = getPHQty();
            this.KDQty = 0;
        }
    }

    public int getQHQty() {
        return QHQty;
    }

    public void setQHQty(int QHQty) {
        this.QHQty = QHQty;
    }

    public int getStoreQty() {
        return StoreQty;
    }

    public void setStoreQty(int storeQty) {
        StoreQty = storeQty;
    }

//    public boolean isCheck() {
//        return isCheck;
//    }
//
//    public void setIsCheck(boolean isCheck) {
//        this.isCheck = isCheck;
//    }

    public int getDHQty() {
        return DHQty;
    }

    public void setDHQty(int DHQty,int StatusID) {
        this.DHQty = DHQty;

        if (StatusID==2)
        {
            if(this.DHQty<0)
            {
                this.DHQty = getQHQty();
            }
            if (this.DHQty > getQHQty()) {
               // this.DHQty = getQHQty();
                this.DHQty = 0;
            }
        }
        else {
            if(this.DHQty<0)
            {
                this.DHQty = getKDQty();
            }
            if (this.DHQty > getKDQty()) {
               // this.DHQty = getKDQty();
                this.DHQty = 0;
            }
        }
    }

    public int getTKQty() {
        return TKQty;
    }

    public void setTKQty(int TKQty) {
        this.TKQty = TKQty;
        if(this.TKQty<0)
        {
            this.TKQty = getQHQty();
        }
        if(this.TKQty>getQHQty())
        {
            //this.TKQty = getQHQty();
            this.TKQty = 0;
        }
    }

    public int getYRKQty() {
        return YRKQty;
    }

    public void setYRKQty(int YRKQty) {
        this.YRKQty = YRKQty;
    }
}
