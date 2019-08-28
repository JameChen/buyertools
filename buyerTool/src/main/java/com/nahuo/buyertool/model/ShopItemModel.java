package com.nahuo.buyertool.model;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.nahuo.buyertool.common.Const;
import com.nahuo.buyertool.common.StringUtils;
import com.nahuo.library.helper.GsonHelper;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 微铺款式实体类
 * */
public class ShopItemModel implements Serializable {

    private static final long serialVersionUID = -3718423961923385896L;

    public int ID;
    @SerializedName("UserID")
    @Expose
    private int userId;
    @Expose
    private int UploadID;// 数据库中上传的队列ID
    @Expose
    private int ParentID;// 父ID
    @Expose
    private int ItemID;// 商品款式ID
    @Expose
    private int AgentItemID;// 商品款式ID
    @Expose
    private String Name;// 商品名称，截取商品描述的前16个汉字作为"商品名称"
    @Expose
    private String Description;// 商品描述
    @Expose
    private String Cat;// 商品分类
    @Expose
    private int Style;// 分类
    @Expose
    private String Cover;// 主图
    @Expose
    private String CreateDate;// 简历日期
    @Expose
    private double Weight;// 重量
    @Expose
    private String[] Images;// 附图列表
    @Expose
    private double OrgPrice;
    @Expose
    private double Price;// 单价
    @Expose
    private double RetailPrice;// 零售价
    @Expose
    private boolean SupplierIsUnifiedRetailPrice;//供货商是否统一零售价
    @Expose
    private List<ProductModel> Products;// 商品详细属性（颜色尺码规格）
    @Expose
    private List<Group> Groups;
    @Expose
    private List<ItemTagModel> mTags;

    public List<ItemTagModel> getmTags() {
        return mTags;
    }

    public void setmTags(List<ItemTagModel> mTags) {
        this.mTags = mTags;
    }

    @Expose
    private boolean IsOnly4Agent;//false为所有人可见，true为指定代理可见
    @Expose
    private int ItemSourceType;//1是自己的，2是转发的，3是复制的
    @Expose
    private int SourceID;//复制商品的原ID
    @Expose
    private boolean IsWaitOrder;
    @Expose
    private int WaitDays;
    @Expose
    private int ApplyStatuID;// 申请状态,申请中：1，拒绝：2，接受：3
    @Expose
    @SerializedName("Intro")
    private String intro;//商品简介
    private String groupIds;

    private String OldImages = "";// 修改状态下，不需要修改的图片原地址
    private boolean IsAdd;// 是否是新增商品

    private int uploadCounter;//上传的次数
    private boolean hasNotified;//是否已经刷新页面

    //最新添加
    @Expose
    private ArrayList<LabelModel> Tags;//商品标签后台[]

    public ArrayList<LabelModel> getTags() {
        return Tags;
    }

    public void setTags(ArrayList<LabelModel> tags) {
        Tags = tags;
    }

    @Expose
    private String ItemTagIDS;//商品标签

    public String getItemTagIDS() {
        return ItemTagIDS;
    }

    public void setItemTagIDS(String itemTagIDS) {
        ItemTagIDS = itemTagIDS;
    }
    public static class ItemTagModel implements Serializable {
        private static final long serialVersionUID = -5541170849258449854L;
        @Expose
        private int ID;
        @Expose
        private String Name;

        public int getID() {
            return ID;
        }

        public void setID(int iD) {
            ID = iD;
        }

        public String getName() {
            return Name.replace("'", "");
        }

        public void setName(String name) {
            Name = name;
        }

    }

    //
    @Expose
    @SerializedName("MyItemCopyType")
    private int itemCopyType;
    @Expose
    @SerializedName("Cat2")
    private ItemCategory4PC itemCat4PC;
    @Expose
    @SerializedName("Styles2")
    private List<CustomModel> itemStyle4PC;
    @Expose
    @SerializedName("Attrs")
    private List<CustomModel> itemAttrs;//属性物价商品等
    @Expose
    @SerializedName("ShopCats")
    private List<CustomModel> shopCats;//店铺分类
    @Expose
    @SerializedName("IsTop")
    private boolean isTop;//是否置顶
    private String userName;

    private String uploadStatus = Const.UploadStatus.UPLOAD_WAIT;//上传状态
    private int uploadedNum;
    private String uploadFailedMsg;
    private String uniqueTag;//本地唯一tag:userid+time millis
    public void updateUploadProgress(){
        uploadedNum++;
    }
    public float getUploadProgress(){
        float totalNum = Images.length + 1;
        return uploadedNum / totalNum;
    }
    public String getUploadProgressStr(){
        float progress = getUploadProgress();
        DecimalFormat df = new DecimalFormat("#0");
        String str = df.format(progress*100);
        return str;
    }

    public ArrayList<ItemShopCategory> getItemShopCats(){
        if(shopCats == null){
            return null;
        }else{
            ArrayList<ItemShopCategory> cats = new ArrayList<ItemShopCategory>();
            for(CustomModel cat : shopCats){
                ItemShopCategory c = new ItemShopCategory();
                c.setId(cat.getId());
                c.setName(cat.getName());
                cats.add(c);
            }
            return cats;
        }
    }
    public List<CustomModel> getShopCats() {
        return shopCats;
    }
    public void setShopCats(List<CustomModel> shopCats) {
        this.shopCats = shopCats;
    }
    public void setShopCatsByItemShopCategory(List<ItemShopCategory> shopCats) {
        if(shopCats == null){
            return;
        }
        this.shopCats = new ArrayList<CustomModel>();
        for(ItemShopCategory cat : shopCats){
            this.shopCats.add(new CustomModel(cat.getId(), cat.getName()));
        }
    }
    public boolean isTop() {
        return isTop;
    }
    public void setTop(boolean isTop) {
        this.isTop = isTop;
    }


    public List<CustomModel> getItemAttrs() {
        return itemAttrs;
    }
    public void setItemAttrs(List<CustomModel> itemAttrs) {
        this.itemAttrs = itemAttrs;
    }
    /**
     * @description 是否是特价商品
     * @created 2015-3-19 下午6:12:47
     * @author ZZB
     */
    public boolean isOnSale(){
        String attrsStr = getItemAttrsStr();
        return StringUtils.contains(attrsStr, "1", ",");
    }

    public String getShopCatsStr(){
        if(shopCats == null){
            return "";
        }else{
            String str = "";
            for(CustomModel cat : shopCats){
                str = StringUtils.append(str, cat.getId() + "", ",");
            }
            return str;
        }
    }
    public String getItemAttrsStr(){
        if(itemAttrs == null){
            return "";
        }else{
            String str = "";
            for(CustomModel attr : itemAttrs){
                str = StringUtils.append(str, attr.getId() + "", ",");
            }
            return str;
        }
    }
    public static class Group implements Serializable{
        private static final long serialVersionUID = -5541170849258449853L;
        @Expose
        private int ID;
        @Expose
        private String Name;
        public int getID() {
            return ID;
        }
        public void setID(int iD) {
            ID = iD;
        }
        public String getName() {
            return Name.replace("'", "");
        }
        public void setName(String name) {
            Name = name;
        }

    }



    public List<CustomModel> getItemStyle4PC() {
        return itemStyle4PC;
    }
    public void setItemStyle4PC(List<CustomModel> itemStyle4PC) {
        this.itemStyle4PC = itemStyle4PC;
    }
    public ItemCategory4PC getItemCat4PC() {
        return itemCat4PC;
    }
    public void setItemCat4PC(ItemCategory4PC itemCat4PC) {
        this.itemCat4PC = itemCat4PC;
    }
    public int getItemSourceType() {
        return ItemSourceType;
    }
    public void setItemSourceType(int itemSourceType) {
        ItemSourceType = itemSourceType;
    }
    public int getSourceID() {
        return SourceID;
    }
    public void setSourceID(int sourceID) {
        SourceID = sourceID;
    }
    /**
     * Description:获取分组名称字符串
     * 2014-7-21下午6:20:48
     * @author ZZB
     */
    public String getGroupNamesFromGroups(){
        if(!IsOnly4Agent){
            return "公开";
        }else{
            if(Groups != null && Groups.size() > 0){
                StringBuilder sb = new StringBuilder();
                for(Group g : Groups){
                    sb.append(g.getName()).append(",");
                }
                return StringUtils.deleteEndStr(sb.toString(), ",");
            }else{
                return "所有代理";
            }
        }

    }
    /**
     * Description:获取分组id字符串
     * 2014-7-21下午6:20:38
     * @author ZZB
     */
    public String getGroupIdsFromGropus(){
        if(!IsOnly4Agent){
            return Const.SystemGroupId.ALL_PPL + "";
        }
        if(Groups != null && Groups.size() > 0){
            StringBuilder sb = new StringBuilder();
            for(Group g : Groups){
                sb.append(g.getID()).append(",");
            }
            return StringUtils.deleteEndStr(sb.toString(), ",");
        }else{
            return Const.SystemGroupId.ALL_AGENT + "";
        }
    }

    public List<Group> getGroups() {
        return Groups;
    }
    public void setGroups(List<Group> groups) {
        Groups = groups;
    }

//    public String getOldImages() {
//        return OldImages;
//    }

    public void setOldImages(String oldImages) {
        OldImages = oldImages;
    }

    public boolean isIsAdd() {
        return IsAdd;
    }

    public void setIsAdd(boolean isAdd) {
        IsAdd = isAdd;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String createDate) {
        CreateDate = createDate;
    }

    public double getWeight() {
        return Weight;
    }

    public void setWeight(double weight) {
        Weight = weight;
    }

    public int getUploadID() {
        return UploadID;
    }

    public void setUploadID(int uploadID) {
        UploadID = uploadID;
    }

    public int getParentID() {
        return ParentID;
    }

    public void setParentID(int parentID) {
        ParentID = parentID;
    }

    public int getAgentItemID() {
        return AgentItemID;
    }

    public void setAgentItemID(int agentItemID) {
        AgentItemID = agentItemID;
    }

    public int getApplyStatuID() {
        return ApplyStatuID;
    }

    public void setApplyStatuID(int applyStatuID) {
        ApplyStatuID = applyStatuID;
    }

    public int getItemID() {
        return ItemID;
    }

    public void setItemID(int itemID) {
        ItemID = itemID;
    }

    public String getName() {
        return Name.replace("'", "");
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        String returnDescription = Description;
        // 从description中移除掉之前添加的imgs标签【等待修改商品功能开放之后取消注释即可】
        if (returnDescription.contains("<div id=beginAppImgInsertTag ></div>")) {
            String endStr = "<div id=endAppImgInsertTag ></div>";
            returnDescription = returnDescription.substring(0,
                    returnDescription.indexOf("<div id=beginAppImgInsertTag ></div>"))
                    + returnDescription.substring(Description.indexOf(endStr) + endStr.length());
        }
        if (returnDescription.contains("<div id=\"beginAppImgInsertTag\" ></div>")) {
            String endStr = "<div id=\"endAppImgInsertTag\" ></div>";
            returnDescription = returnDescription.substring(0,
                    returnDescription.indexOf("<div id=\"beginAppImgInsertTag\" ></div>"))
                    + returnDescription.substring(Description.indexOf(endStr) + endStr.length());
        }
        if (returnDescription.contains("<div id=\"beginAppImgInsertTag />")) {
            String endStr = "<div id=endAppImgInsertTag />";
            returnDescription = returnDescription.substring(0,
                    returnDescription.indexOf("<div id=beginAppImgInsertTag />"))
                    + returnDescription.substring(Description.indexOf(endStr) + endStr.length());
        }

        return returnDescription.replace("'", "");
    }

    public String getDescriptionFull() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getCat() {
        return Cat;
    }

    public void setCat(String cat) {
        Cat = cat;
    }

    public int getStyle() {
        return Style;
    }

    public void setStyle(int value) {
        Style = value;
    }

    public String getCover() {
        return Cover;
    }

    public void setCover(String coverImg) {
        Cover = coverImg;
    }

    public String[] getImages() {
        return Images;
    }

    public String getImagesJsonStr() {
        String imgs = "";
        for (String img_Str : Images) {
            imgs += img_Str + Const.PIC_SEPERATOR;
        }
        return imgs;
    }

    public void setImages(String[] images) {
        Images = images;
    }

    public void setImagesJson(String images) {
        Images = images.split(Const.PIC_SEPERATOR);
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public double getRetailPrice() {
        return RetailPrice;
    }

    public void setRetailPrice(double retailPrice) {
        RetailPrice = retailPrice;
    }

    public List<ProductModel> getProducts() {
        return Products;
    }

    public String getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(String groupIds) {
        this.groupIds = groupIds;
    }

    // 取出product的json字符串格式
    public String getProductsJsonStr() {
        // 商品规格Json
        String products = "";
        for (ProductModel product : Products) {
            products += "{'Color':'" + product.getColor() + "',";
            products += "'Size':'" + product.getSize() + "',";
            products += "'Stock':" + product.getStock() + ",";
            products += "'Price':" + product.getPrice() + ",";
            products += "'Cover':'" + product.getCover() + "'},";
        }
        if (products.length() > 0) {
            products = products.substring(0, products.length() - 1);
        }
        products = "[" + products + "]";

        return products;
    }

    public void setProducts(List<ProductModel> products) {
        Products = products;
    }

    public void setProductsJson(String products) {
        try {
            Products = GsonHelper.jsonToObject(products, new TypeToken<List<ProductModel>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            Products = new ArrayList<ProductModel>();
        }
    }

    public String getUrl() {
        return "http://item.weipushop.com/wap/wpitem/" + ItemID;
    }
    public boolean getIsOnly4Agent() {
        return IsOnly4Agent;
    }
    public void setIsOnly4Agent(boolean isOnly4Agent) {
        IsOnly4Agent = isOnly4Agent;
    }
    public boolean getSupplierIsUnifiedRetailPrice() {
        return SupplierIsUnifiedRetailPrice;
    }
    public void setSupplierIsUnifiedRetailPrice(boolean supplierIsUnifiedRetailPrice) {
        SupplierIsUnifiedRetailPrice = supplierIsUnifiedRetailPrice;
    }
    public int getUploadCounter() {
        return uploadCounter;
    }
    public void setUploadCounter(int uploadCounter) {
        this.uploadCounter = uploadCounter;
    }
    public boolean isHasNotified() {
        return hasNotified;
    }
    public void setHasNotified(boolean hasNotified) {
        this.hasNotified = hasNotified;
    }
    public String getIntro() {
        return intro;
    }
    public void setIntro(String intro) {
        this.intro = intro;
    }
    /**
     * @description 如果intro为空返回name
     * @created 2014-11-10 下午6:05:10
     * @author ZZB
     */
    public String getIntroOrName(){
        return TextUtils.isEmpty(intro) ? Name : intro;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public int getItemCopyType() {
        return itemCopyType;
    }
    public void setItemCopyType(int itemCopyType) {
        this.itemCopyType = itemCopyType;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getUploadStatus() {
        return uploadStatus;
    }
    public void setUploadStatus(String uploadStatus) {
        this.uploadStatus = uploadStatus;
    }
    public String getUploadFailedMsg() {
        return uploadFailedMsg;
    }
    public void setUploadFailedMsg(String uploadFailedMsg) {
        this.uploadFailedMsg = uploadFailedMsg;
    }
    public String getUniqueTag() {
        return uniqueTag;
    }
    public void setUniqueTag(String uniqueTag) {
        this.uniqueTag = uniqueTag;
    }
    public double getOrgPrice() {
        return OrgPrice;
    }
    public void setOrgPrice(double orgPrice) {
        OrgPrice = orgPrice;
    }
    public String getGroupIdsFromGroups() {
        if (Groups == null || Groups.size() == 0) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (Group g : Groups) {
                sb.append(g.getID()).append(",");
            }
            return StringUtils.deleteEndStr(sb.toString(), ",");
        }
    }

    public int getWaitDays() {
        return WaitDays;
    }

    public void setWaitDays(int waitDays) {
        WaitDays = waitDays;
    }

    public boolean isWaitOrder() {
        return IsWaitOrder;
    }

    public void setIsWaitOrder(boolean isWaitOrder) {
        IsWaitOrder = isWaitOrder;
    }
}
