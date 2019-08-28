package com.nahuo.buyertool.model;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nahuo.buyertool.common.Const;
import com.nahuo.library.helper.FunctionHelper;
import com.nahuo.library.utils.TimeUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * 微店商品管理款式实体类
 */
public class ShopItemListModel implements Serializable {
    /**
     * NameTag : 【开拼中，慎报单！】
     * IsWarnTag : false
     * MarkUpValue : 0
     */
    @Expose
    @SerializedName("ItemRemark")
    private String ItemRemark="";

    public String getItemRemark() {
        return ItemRemark;
    }

    public void setItemRemark(String itemRemark) {
        ItemRemark = itemRemark;
    }

    @Expose
    @SerializedName("ShowCancelKDBtn")
    public boolean ShowCancelKDBtn;
    @Expose
    @SerializedName("ShowApplyRefundBtn")
    public boolean ShowApplyRefundBtn;
    @Expose
    @SerializedName("ShowSetExceptionalBtn")
    public boolean ShowSetExceptionalBtn;
    @Expose
    @SerializedName("ShowCancelExceptionalBtn")
    public boolean ShowCancelExceptionalBtn;
    @Expose
    @SerializedName("ExceptionalReason")
    private String ExceptionalReason="";

    public String getExceptionalReason() {
        return ExceptionalReason;
    }

    public void setExceptionalReason(String exceptionalReason) {
        ExceptionalReason = exceptionalReason;
    }

    @Expose
    @SerializedName("ExceptionalReasonList")
    private List<String> ExceptionalReasonList;

    public List<String> getExceptionalReasonList() {
        return ExceptionalReasonList;
    }

    public void setExceptionalReasonList(List<String> exceptionalReasonList) {
        ExceptionalReasonList = exceptionalReasonList;
    }

    @Expose
    @SerializedName("WaitDaysReasonList")
    private List<ReasonListBean> WaitDaysReasonList;
    @SerializedName("OutSupplyReasonList")
    @Expose
    private List<ReasonListBean> OutSupplyReasonList;
    @SerializedName("CloseStorageReasonList")
    @Expose
    private List<ReasonListBean> CloseStorageReasonList;
    @Expose
    private int SourceStatuID;
    @Expose
    private int SourceID;

    public int getSourceID() {
        return SourceID;
    }

    public void setSourceID(int sourceID) {
        SourceID = sourceID;
    }

    public int getSourceStatuID() {
        return SourceStatuID;
    }

    public void setSourceStatuID(int sourceStatuID) {
        SourceStatuID = sourceStatuID;
    }

    public boolean isShowCancelKDBtn() {
        return ShowCancelKDBtn;
    }

    public void setShowCancelKDBtn(boolean showCancelKDBtn) {
        ShowCancelKDBtn = showCancelKDBtn;
    }

    @Expose
    @SerializedName("NameTag")
    private String NameTag;
    @Expose
    @SerializedName("IsWarnTag")
    private boolean IsWarnTag;
    @Expose
    @SerializedName("MarkUpValue")
    private double MarkUpValue;
    @Expose
    public String Title;
    @Expose
    public String ShopName;
    private static final long serialVersionUID = 7427104386583994172L;
    @Expose
    private int ID;                                     // 商品款式ID
    @Expose
    private int ItemID;                                     // 商品款式ID
    @Expose
    public int StoreID;                                     // 商品款式ID
    @Expose
    public String QsName;                                     // 拼货场次

    @Expose
    private int QsID;                                     // 商品款式ID
    @Expose
    private int PayQty;                                     // 成交数量
    @Expose
    private int BillingQty;                                     // 开单数量
    @Expose
    private int InvInCount;                                     // 入库数量
    @Expose
    private int StatusID;                                     // 1是已开单、2是欠货
    @Expose
    private int Warehouse;                                     // 入库仓位
    @Expose
    private String WhDes;
    @Expose
    private int OweQty;                                     // 缺货数量
    @Expose
    private int OweDays;                                     // 缺货天数
    @Expose
    private int RefundQty;                                     // 退款数量
    @Expose
    private String RefundTime;                                     // 退款时间
    @Expose
    private String Name;                                   // 商品名称
    @Expose
    private double Price;                                  // 售出单价
    @Expose
    private double OriPrice;                                  // 拿货单价
    @Expose
    private String CreateTime;                             // 创建时间
    @Expose
    private String StartTime;                             // 开始时间
    @Expose
    private String BillingTime;                             // 开始时间
    @Expose
    private String Statu;                                  // 状态
    @Expose
    private String Cover;
    @Expose
    private String UserName;
    @Expose
    private String[] Images;
    @Expose
    private int UserID;
    @Expose
    @SerializedName("Intro")
    private String intro;
    @Expose
    private List<ProductModel> ColorSize;
    @Expose
    private List<ItemRemarkModel> Record;
    @Expose
    private ItemStockInfoModel StockInfo;
    @Expose
    private PurchaseModel Purchase;
    @Expose
    private boolean PurchaseStatus;
    @Expose
    private int BillingID;//批量关联时传参
    @Expose
    private boolean IsCanChangeWaitDays;
    @Expose
    private int WaitDaysMaxRange;
    @Expose
    private int WaitDaysMinRange;
    @Expose
    private int StorageTimeApplyType;//入库时间申请类型
    @Expose
    private String StockStatus;//
    @SerializedName("ColorPics")
    @Expose
    private List<ColorPicsBean> ColorPics;

    public List<ColorPicsBean> getColorPics() {
        return ColorPics;
    }

    public void setColorPics(List<ColorPicsBean> colorPics) {
        ColorPics = colorPics;
    }

    public static class ColorPicsBean implements Serializable {
        private static final long serialVersionUID = 6897596584970981565L;
        /**
         * Color :空为主色卡
         * Url : upyun:banwo-img:/shop/121/43433480d.jpg
         */
        private boolean is_upload = true;

        public boolean is_upload() {
            return is_upload;
        }

        public void setIs_upload(boolean is_upload) {
            this.is_upload = is_upload;
        }

        @Expose
        @SerializedName("Color")
        private String Color = "";
        @Expose
        @SerializedName("Url")
        private String Url = "";

        public String getColor() {
            return Color;
        }

        public void setColor(String Color) {
            this.Color = Color;
        }

        public String getUrl() {
            return Url;
        }

        public void setUrl(String Url) {
            this.Url = Url;
        }
    }

    public int getStoreID() {
        return StoreID;
    }

    public void setStoreID(int storeID) {
        StoreID = storeID;
    }

    public String getStockStatus() {
        return StockStatus;
    }

    public void setStockStatus(String stockStatus) {
        StockStatus = stockStatus;
    }

    public int getWaitDaysMaxRange() {
        return WaitDaysMaxRange;
    }

    public void setWaitDaysMaxRange(int waitDaysMaxRange) {
        WaitDaysMaxRange = waitDaysMaxRange;
    }

    public int getWaitDaysMinRange() {
        return WaitDaysMinRange;
    }

    public void setWaitDaysMinRange(int waitDaysMinRange) {
        WaitDaysMinRange = waitDaysMinRange;
    }

    public boolean isCanChangeWaitDays() {
        return IsCanChangeWaitDays;
    }

    public void setIsCanChangeWaitDays(boolean isCanChangeWaitDays) {
        IsCanChangeWaitDays = isCanChangeWaitDays;
    }

    public int getStorageTimeApplyType() {
        return StorageTimeApplyType;
    }

    public void setStorageTimeApplyType(int storageTimeApplyType) {
        StorageTimeApplyType = storageTimeApplyType;
    }


    public int getBillingID() {
        return BillingID;
    }

    public void setBillingID(int billingID) {
        BillingID = billingID;
    }


    /**
     * @description 如果intro为空返回name
     * @created 2014-11-10 下午6:05:10
     * @author ZZB
     */
    public String getIntroOrName() {
        return TextUtils.isEmpty(intro) ? Name : intro;
    }

    public int getUserid() {
        return UserID;
    }

    public void setUserid(int userid) {
        this.UserID = userid;
    }

    public String[] getImages() {
        return Images;
    }

    public void setImages(String[] images) {
        Images = images;
    }

    public void setImagesJson(String images) {
        Images = images.split(Const.PIC_SEPERATOR);
    }

    public String getImagesJsonStr() {
        String imgs = "";
        for (String img_Str : Images) {
            imgs += img_Str + Const.PIC_SEPERATOR;
        }
        return imgs;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getCover() {
        return Cover;
    }

    public void setCover(String cover) {
        Cover = cover;
    }

    public int getID() {
        return ID;
    }

    public void setID(int iD) {
        Log.e("查看此處是否調用", "setID=" + iD);
        ID = iD;
    }

    public String getName() {
        return Name.replace("'", "");
    }

    private static final List<String> FACE_LIST = Arrays.asList(new String[]{"[微笑]", "[撇嘴]", "[色]", "[发呆]", "[得意]",
            "[流泪]", "[害羞]", "[闭嘴]", "[睡]", "[大哭]", "[尴尬]", "[发怒]", "[调皮]", "[呲牙]", "[惊讶]", "[难过]", "[酷]", "[冷汗]",
            "[抓狂]", "[吐]", "[偷笑]", "[愉快]", "[白眼]", "[傲慢]", "[饥饿]", "[困]", "[惊恐]", "[流汗]", "[憨笑]", "[悠闲]", "[奋斗]",
            "[咒骂]", "[疑问]", "[嘘]", "[晕]", "[疯了]", "[衰]", "[骷髅]", "[敲打]", "[再见]", "[擦汗]", "[抠鼻]", "[鼓掌]", "[糗大了]",
            "[坏笑]", "[左哼哼]", "[右哼哼]", "[哈欠]", "[鄙视]", "[委屈]", "[快哭了]", "[阴险]", "[亲亲]", "[吓]", "[可怜]", "[菜刀]", "[西瓜]",
            "[啤酒]", "[篮球]", "[乒乓]", "[咖啡]", "[饭]", "[猪头]", "[玫瑰]", "[凋谢]", "[嘴唇]", "[爱心]", "[心碎]", "[蛋糕]", "[闪电]",
            "[炸弹]", "[刀]", "[足球]", "[瓢虫]", "[便便]", "[月亮]", "[太阳]", "[礼物]", "[拥抱]", "[强]", "[弱]", "[握手]", "[胜利]",
            "[抱拳]", "[勾引]", "[拳头]", "[差劲]", "[爱你]", "[NO]", "[OK]", "[爱情]", "[飞吻]", "[跳跳]", "[发抖]", "[怄火]", "[转圈]",
            "[磕头]", "[回头]", "[跳绳]", "[挥手]", "[激动]", "[街舞]", "[献吻]", "[左太极]", "[右太极]"});

    // private static final List<String> FACE_LIST = Arrays.asList(QQ_FACE_ARRAY.split(","));
    public static Spanned getTextHtml(String normalText, Context context, Html.ImageGetter imageGetter) {
        if (TextUtils.isEmpty(normalText)) {
            return new SpannedString("");
        }
        // / 优化
        String formattedText = normalText;
        if (normalText.contains("[") && normalText.contains("]")) {
            for (String face : FACE_LIST) {
                if (formattedText.contains("[") && formattedText.contains("]")) {
                    if (formattedText.contains(face)) {
                        formattedText = formattedText.replace(
                                face,
                                "<img src=\""
                                        + context.getResources().getIdentifier(
                                        context.getPackageName() + ":drawable/qq_" + FACE_LIST.indexOf(face),
                                        null, null) + "\" />");
                    }
                } else {
                    break;
                }
            }
        }
        // 换行符转换
        formattedText = formattedText.replace("\n", "<br>");

        return Html.fromHtml(formattedText, imageGetter, null);
    }

    public void setName(String name) {
        Name = name;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public String getCreateTimeStr() {
        return CreateTime;
    }

    public void setCreateTime(String CreateTime) {
        CreateTime = CreateTime;
    }

    public String getStatu() {
        return Statu;
    }

    public void setStatu(String statu) {
        Statu = statu;
    }

    public String getShowTime() {
        return FunctionHelper.getFriendlyTime(CreateTime);

    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public long getLongDate() {
        return TimeUtils.timeStampToMillis(CreateTime);
    }

    public int getInvInCount() {
        return InvInCount;
    }

    public void setInvInCount(int invInCount) {
        InvInCount = invInCount;
    }

    public int getOweQty() {
        return OweQty;
    }

    public void setOweQty(int OweQty) {
        OweQty = OweQty;
    }

    public int getRefundQty() {
        return RefundQty;
    }

    public void setRefundQty(int RefundQty) {
        RefundQty = RefundQty;
    }

    public int getBillingQty() {
        return BillingQty;
    }

    public void setBillingQty(int BillingQty) {
        BillingQty = BillingQty;
    }

    public int getPayQty() {
        return PayQty;
    }

    public void setPayQty(int PayQty) {
        this.PayQty = PayQty;
    }

    public String getWarehouse() {
        return WhDes;
    }

    public void setWarehouse(String WhDes) {
        this.WhDes = WhDes;
    }

    public int getOweDays() {
        return OweDays;
    }

    public void setOweDays(int OweDays) {
        OweDays = OweDays;
    }

    public String getRefundTime() {
        return RefundTime;
    }

    public void setRefundTime(String RefundTime) {
        RefundTime = RefundTime;
    }

    public int getQsID() {
        return QsID;
    }

    public void setQsID(int QsID) {
        this.QsID = QsID;
    }

    public int getStatusID() {
        return StatusID;
    }

    public void setStatusID(int statusID) {
        StatusID = statusID;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public List<ProductModel> getColorSize() {
        return ColorSize;
    }

    public void setColorSize(List<ProductModel> colorSize) {
        ColorSize = colorSize;
    }

    public String getBillingTime() {
        return BillingTime;
    }

    public void setBillingTime(String billingTime) {
        BillingTime = billingTime;
    }

    public List<ItemRemarkModel> getRecord() {
        return Record;
    }

    public void setRecord(List<ItemRemarkModel> record) {
        Record = record;
    }

    public int getItemID() {
        return ItemID;
    }

    public void setItemID(int itemID) {
        ItemID = itemID;
    }

    public String getQsName() {
        return QsName;
    }

    public void setQsName(String qsName) {
        QsName = qsName;
    }

    public ItemStockInfoModel getStockInfo() {
        return StockInfo;
    }

    public void setStockInfo(ItemStockInfoModel stockInfo) {
        StockInfo = stockInfo;
    }

    public double getOriPrice() {
        return OriPrice;
    }

    public void setOriPrice(double oriPrice) {
        OriPrice = oriPrice;
    }

    public PurchaseModel getPurchase() {
        return Purchase;
    }

    public void setPurchase(PurchaseModel purchase) {
        Purchase = purchase;
    }

    public boolean getPurchaseStatus() {
        return PurchaseStatus;
    }

    public void setPurchaseStatus(boolean purchaseStatus) {
        PurchaseStatus = purchaseStatus;
    }


    public String getNameTag() {
        return NameTag;
    }

    public void setNameTag(String NameTag) {
        this.NameTag = NameTag;
    }

    public boolean isIsWarnTag() {
        return IsWarnTag;
    }

    public void setIsWarnTag(boolean IsWarnTag) {
        this.IsWarnTag = IsWarnTag;
    }

    public double getMarkUpValue() {
        return MarkUpValue;
    }

    public void setMarkUpValue(double markUpValue) {
        MarkUpValue = markUpValue;
    }

    public List<ReasonListBean> getWaitDaysReasonList() {
        return WaitDaysReasonList;
    }

    public void setWaitDaysReasonList(List<ReasonListBean> WaitDaysReasonList) {
        this.WaitDaysReasonList = WaitDaysReasonList;
    }

    public List<ReasonListBean> getOutSupplyReasonList() {
        return OutSupplyReasonList;
    }

    public void setOutSupplyReasonList(List<ReasonListBean> OutSupplyReasonList) {
        this.OutSupplyReasonList = OutSupplyReasonList;
    }

    public List<ReasonListBean> getCloseStorageReasonList() {
        return CloseStorageReasonList;
    }

    public void setCloseStorageReasonList(List<ReasonListBean> CloseStorageReasonList) {
        this.CloseStorageReasonList = CloseStorageReasonList;
    }

    public static class ReasonListBean implements Serializable {
        private static final long serialVersionUID = 4556431082723015979L;
        /**
         * ID : 1
         * Text : 原材料紧缺，货期延后
         */
        @Expose
        @SerializedName("ID")
        private int ID;
        @Expose
        @SerializedName("Text")
        private String Text;

        public int getIDX() {
            return ID;
        }

        public void setIDX(int IDX) {
            this.ID = IDX;
        }

        public String getText() {
            return Text;
        }

        public void setText(String Text) {
            this.Text = Text;
        }
    }


}
