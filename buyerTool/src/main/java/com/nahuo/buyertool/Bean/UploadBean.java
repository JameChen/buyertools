package com.nahuo.buyertool.Bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nahuo.buyertool.common.Const;
import com.nahuo.buyertool.common.FourListBean;
import com.nahuo.buyertool.uploadtask.UploadListener;
import com.nahuo.buyertool.uploadtask.UploadTask;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jame on 2017/7/21.
 */

public class UploadBean implements Serializable {

    private static final long serialVersionUID = 9067098500134683545L;
    @SerializedName("ColorPics")
    @Expose
    private List<ColorPicsBean> ColorPics;
    @Expose
    @SerializedName("SeasonList")
    private List<FourListBean> SeasonList;
    @Expose
    @SerializedName("StyleList")
    private List<FourListBean> StyleList;
    @Expose
    @SerializedName("AgeList")
    private List<FourListBean> AgeList;
    @Expose
    @SerializedName("MaterialList")
    private List<FourListBean> MaterialList;
    @Expose
    @SerializedName("Remark")
    private String Remark="";
    @Expose
    @SerializedName("Discount")
    private double  Discount=0.00;
    @Expose
    @SerializedName("ExtendPropertyTypeListV2")
    private List<ExtendPropertyTypeListV2Bean> ExtendPropertyTypeListV2;

    public double getDiscount() {
        return Discount;
    }

    public void setDiscount(double discount) {
        Discount = discount;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public UploadListener getListener() {
        return listener;
    }

    public void setListener(UploadListener listener) {
        this.listener = listener;
    }

    public void removeListener() {
        listener = null;
    }
//    @Expose
//    @SerializedName("ExtendPropertyTypeList")
//    private List<ExtendPropertyTypeListBean> ExtendPropertyTypeList;
//
//    public List<ExtendPropertyTypeListBean> getExtendPropertyTypeList() {
//        return ExtendPropertyTypeList;
//    }

//    public void setExtendPropertyTypeList(List<ExtendPropertyTypeListBean> ExtendPropertyTypeList) {
//        this.ExtendPropertyTypeList = ExtendPropertyTypeList;
//    }

    public List<ExtendPropertyTypeListV2Bean> getExtendPropertyTypeListV2() {
        return ExtendPropertyTypeListV2;
    }

    public void setExtendPropertyTypeListV2(List<ExtendPropertyTypeListV2Bean> ExtendPropertyTypeListV2) {
        this.ExtendPropertyTypeListV2 = ExtendPropertyTypeListV2;
    }

//    public static class ExtendPropertyTypeListBean  implements Serializable{
//        private static final long serialVersionUID = -6424654334109952350L;
//        /**
//         * TypeID : 1
//         * TypeName : 现货
//         * IsValue : false
//         * ExtendPropertyList : [{"Value":null,"ID":1,"Name":"立即发货，24小时发货"}]
//         */
//        @Expose
//        @SerializedName("TypeID")
//        private int TypeID;
//        @Expose
//        @SerializedName("TypeName")
//        private String TypeName="";
//        private String selContent="";
//
//        public String getSelContent() {
//            return selContent;
//        }
//
//        public void setSelContent(String selContent) {
//            this.selContent = selContent;
//        }
//
//        @SerializedName("IsValue")
//        @Expose
//        private boolean IsValue;
//        @Expose
//        private  boolean IsMore;
//
//        public boolean isMore() {
//            return IsMore;
//        }
//
//        public void setMore(boolean more) {
//            IsMore = more;
//        }
//
//        @Expose
//        @SerializedName("ExtendPropertyList")
//        private List<ExtendPropertyListBean> ExtendPropertyList;
//
//        public int getTypeID() {
//            return TypeID;
//        }
//
//        public void setTypeID(int TypeID) {
//            this.TypeID = TypeID;
//        }
//
//        public String getTypeName() {
//            return TypeName;
//        }
//
//        public void setTypeName(String TypeName) {
//            this.TypeName = TypeName;
//        }
//
//        public boolean isIsValue() {
//            return IsValue;
//        }
//
//        public void setIsValue(boolean IsValue) {
//            this.IsValue = IsValue;
//        }
//
//        public List<ExtendPropertyListBean> getExtendPropertyList() {
//            return ExtendPropertyList;
//        }
//
//        public void setExtendPropertyList(List<ExtendPropertyListBean> ExtendPropertyList) {
//            this.ExtendPropertyList = ExtendPropertyList;
//        }

//        public static class ExtendPropertyListBean implements Serializable{
//            private static final long serialVersionUID = 7646139294401199070L;
//            /**
//             * Value : null
//             * ID : 1
//             * Name : 立即发货，24小时发货
//             */
//            public boolean isSelect;
//            @Expose
//            @SerializedName("Value")
//            private String Value="";
//            @Expose
//            @SerializedName("ID")
//            private int ID;
//            @Expose
//            @SerializedName("Name")
//            private String Name="";
//
//            public String getValue() {
//                return Value;
//            }
//
//            public void setValue(String Value) {
//                this.Value = Value;
//            }
//
//            public int getID() {
//                return ID;
//            }
//
//            public void setID(int ID) {
//                this.ID = ID;
//            }
//
//            public String getName() {
//                return Name;
//            }
//
//            public void setName(String Name) {
//                this.Name = Name;
//            }
//        }
//    }
    /**
     * ItemID : 963532
     * Name : 听听
     * Price : 1.10
     * SupplyInfo : {"TypeID":1,"Days":2,"UpdateWaitOrderType":1}
     * Images : ["upyun:nahuo-img-server://33306/item/1500609002.jpg","upyun:nahuo-img-server://33306/item/1500609003.jpg","upyun:nahuo-img-server://33306/item/1500609004.jpg"]
     * Cats : []
     * Products : [{"Color":"20","Size":"41码","Stock":2000},{"Color":"19","Size":"41码","Stock":2000},{"Color":"8","Size":"41码","Stock":2000},{"Color":"6","Size":"41码","Stock":2000},{"Color":"20","Size":"42","Stock":2000},{"Color":"19","Size":"42","Stock":2000},{"Color":"8","Size":"42","Stock":2000},{"Color":"6","Size":"42","Stock":2000},{"Color":"20","Size":"高领","Stock":2000},{"Color":"19","Size":"高领","Stock":2000},{"Color":"8","Size":"高领","Stock":2000},{"Color":"6","Size":"高领","Stock":2000},{"Color":"20","Size":"37","Stock":2000},{"Color":"19","Size":"37","Stock":2000},{"Color":"8","Size":"37","Stock":2000},{"Color":"6","Size":"37","Stock":2000}]
     * Tags : [{"ID":10096,"Name":"05150002"},{"ID":10097,"Name":"05150003"},{"ID":20088,"Name":"韩风"}]
     * StallInfo : {"MarketID":1,"FloorID":4,"StallID":1002,"Name":"广州十三行-1-A106"}
     * GroupDealCount : 19
     * Videos : ["http://nahuo-video-server.b0.upaiyun.com//33306/item/1500609005.mp4","http://nahuo-video-server.b0.upaiyun.com//33306/item/1500609006.mp4"]
     * CategoryList : [{"ID":12,"Name":"半裙","ParentID":1,"ParentName":"女装"}]
     * PropertyList : [{"ID":10,"Name":"合体","ParentID":7,"ParentName":"商品版型"},{"ID":15,"Name":"适中","ParentID":12,"ParentName":"柔软指数"},{"ID":18,"Name":"无弹","ParentID":17,"ParentName":"弹力指数"},{"ID":25,"Name":"厚","ParentID":22,"ParentName":"厚薄指数"},{"ID":29,"Name":"微透","ParentID":27,"ParentName":"透气指数"}]
     */
    transient
    private UploadListener listener;  //当前上传任务的监听
    transient
    private UploadTask task;          //执行当前下载的任务
    @Expose
    @SerializedName("MarkUpValue")
    private double MarkUpValue = 0.00;
    @Expose
    @SerializedName("Summary")
    private String Summary = "";
    public String getSummary() {
        return Summary;
    }

    public void setSummary(String summary) {
        Summary = summary;
    }

    private String message = "";

    public UploadTask getTask() {
        return task;
    }

    public void setTask(UploadTask task) {
        this.task = task;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getMarkUpValue() {
        return MarkUpValue;
    }

    public void setMarkUpValue(double markUpValue) {
        MarkUpValue = markUpValue;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    private long progress;             //进度
    @Expose
    @SerializedName("IsCopy")
    private boolean IsCopy;

    public boolean isCopy() {
        return IsCopy;
    }

    public void setCopy(boolean copy) {
        IsCopy = copy;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int type;
    @Expose
    @SerializedName("ItemID")
    private int ItemID;
    @Expose
    @SerializedName("Name")
    private String Name;
    @Expose
    @SerializedName("Price")
    private String Price;
    @Expose
    @SerializedName("SupplyInfo")
    private SupplyInfoBean SupplyInfo;
    @Expose
    @SerializedName("StallInfo")
    private StallInfoBean StallInfo;
    @Expose
    @SerializedName("GroupDealCount")
    private int GroupDealCount;
    @Expose
    @SerializedName("Images")
    private List<String> Images;

    @Expose
    @SerializedName("Cats")
    private List<?> Cats;
    @Expose
    @SerializedName("Products")
    private List<ProductsBean> Products;
    @Expose
    @SerializedName("Tags")
    private List<TagsBean> Tags;
    @Expose
    @SerializedName("Videos")
    private List<String> Videos;
    @Expose
    @SerializedName("CategoryList")
    private List<CategoryListBean> CategoryList;
    @Expose
    @SerializedName("PropertyList")
    private List<PropertyListBean> PropertyList;
    private List<MediaBean> local_pics;
    private List<MediaBean> local_videos;
    private String creat_time = "";
    private String uploadStatus = Const.UploadStatus.UPLOAD_FAILED;//上传状态
    private int userId;
    private String cover = "";

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(String uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public String getCreat_time() {
        return creat_time;
    }

    public void setCreat_time(String creat_time) {
        this.creat_time = creat_time;
    }

    public List<MediaBean> getLocal_pics() {
        return local_pics;
    }

    public void setLocal_pics(List<MediaBean> local_pics) {
        this.local_pics = local_pics;
    }

    public List<MediaBean> getLocal_videos() {
        return local_videos;
    }

    public void setLocal_videos(List<MediaBean> local_videos) {
        this.local_videos = local_videos;
    }

    public List<ColorPicsBean> getColorPics() {
        return ColorPics;
    }

    public void setColorPics(List<ColorPicsBean> ColorPics) {
        this.ColorPics = ColorPics;
    }

    public List<FourListBean> getSeasonList() {
        return SeasonList;
    }

    public void setSeasonList(List<FourListBean> SeasonList) {
        this.SeasonList = SeasonList;
    }

    public List<FourListBean> getStyleList() {
        return StyleList;
    }

    public void setStyleList(List<FourListBean> StyleList) {
        this.StyleList = StyleList;
    }

    public List<FourListBean> getAgeList() {
        return AgeList;
    }

    public void setAgeList(List<FourListBean> AgeList) {
        this.AgeList = AgeList;
    }

    public List<FourListBean> getMaterialList() {
        return MaterialList;
    }

    public void setMaterialList(List<FourListBean> MaterialList) {
        this.MaterialList = MaterialList;
    }

    public static class MediaBean implements Serializable {

        private static final long serialVersionUID = -7778271743709743606L;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public boolean is_upload() {
            return is_upload;
        }

        public void setIs_upload(boolean is_upload) {
            this.is_upload = is_upload;
        }

        private String path;
        private boolean is_upload;
    }

    public int getItemID() {
        return ItemID;
    }

    public void setItemID(int ItemID) {
        this.ItemID = ItemID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String Price) {
        this.Price = Price;
    }

    public SupplyInfoBean getSupplyInfo() {
        return SupplyInfo;
    }

    public void setSupplyInfo(SupplyInfoBean SupplyInfo) {
        this.SupplyInfo = SupplyInfo;
    }

    public StallInfoBean getStallInfo() {
        return StallInfo;
    }

    public void setStallInfo(StallInfoBean StallInfo) {
        this.StallInfo = StallInfo;
    }

    public int getGroupDealCount() {
        return GroupDealCount;
    }

    public void setGroupDealCount(int GroupDealCount) {
        this.GroupDealCount = GroupDealCount;
    }

    public List<String> getImages() {
        return Images;
    }

    public void setImages(List<String> Images) {
        this.Images = Images;
    }

    public List<?> getCats() {
        return Cats;
    }

    public void setCats(List<?> Cats) {
        this.Cats = Cats;
    }

    public List<ProductsBean> getProducts() {
        return Products;
    }

    public void setProducts(List<ProductsBean> Products) {
        this.Products = Products;
    }

    public List<TagsBean> getTags() {
        return Tags;
    }

    public void setTags(List<TagsBean> Tags) {
        this.Tags = Tags;
    }

    public List<String> getVideos() {
        return Videos;
    }

    public void setVideos(List<String> Videos) {
        this.Videos = Videos;
    }

    public List<CategoryListBean> getCategoryList() {
        return CategoryList;
    }

    public void setCategoryList(List<CategoryListBean> CategoryList) {
        this.CategoryList = CategoryList;
    }

    public List<PropertyListBean> getPropertyList() {
        return PropertyList;
    }

    public void setPropertyList(List<PropertyListBean> PropertyList) {
        this.PropertyList = PropertyList;
    }

    public static class SupplyInfoBean implements Serializable {
        private static final long serialVersionUID = 8940405672381640723L;
        /**
         * TypeID : 1
         * Days : 2
         * UpdateWaitOrderType : 1
         */
        @Expose
        @SerializedName("TypeID")
        private int TypeID;
        @Expose
        @SerializedName("Days")
        private int Days;
        @Expose
        @SerializedName("UpdateWaitOrderType")
        private int UpdateWaitOrderType;

        public int getTypeID() {
            return TypeID;
        }

        public void setTypeID(int TypeID) {
            this.TypeID = TypeID;
        }

        public int getDays() {
            return Days;
        }

        public void setDays(int Days) {
            this.Days = Days;
        }

        public int getUpdateWaitOrderType() {
            return UpdateWaitOrderType;
        }

        public void setUpdateWaitOrderType(int UpdateWaitOrderType) {
            this.UpdateWaitOrderType = UpdateWaitOrderType;
        }
    }

    public static class StallInfoBean implements Serializable {
        private static final long serialVersionUID = 1710534650544384003L;
        /**
         * MarketID : 1
         * FloorID : 4
         * StallID : 1002
         * Name : 广州十三行-1-A106
         */
        @Expose
        @SerializedName("MarketID")
        private int MarketID;
        @Expose
        @SerializedName("FloorID")
        private int FloorID;
        @Expose
        @SerializedName("StallID")
        private int StallID;
        @Expose
        @SerializedName("Name")
        private String Name;

        public int getMarketID() {
            return MarketID;
        }

        public void setMarketID(int MarketID) {
            this.MarketID = MarketID;
        }

        public int getFloorID() {
            return FloorID;
        }

        public void setFloorID(int FloorID) {
            this.FloorID = FloorID;
        }

        public int getStallID() {
            return StallID;
        }

        public void setStallID(int StallID) {
            this.StallID = StallID;
        }

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }
    }

    public static class ProductsBean implements Serializable {
        private static final long serialVersionUID = -8706732849390559579L;
        /**
         * Color : 20
         * Size : 41码
         * Stock : 2000
         */
        @Expose
        @SerializedName("Color")
        private String Color;
        @Expose
        @SerializedName("Size")
        private String Size;
        @Expose
        @SerializedName("Stock")
        private int Stock;

        public String getColor() {
            return Color;
        }

        public void setColor(String Color) {
            this.Color = Color;
        }

        public String getSize() {
            return Size;
        }

        public void setSize(String Size) {
            this.Size = Size;
        }

        public int getStock() {
            return Stock;
        }

        public void setStock(int Stock) {
            this.Stock = Stock;
        }
    }

    public static class TagsBean implements Serializable {
        private static final long serialVersionUID = 7500261951132823877L;
        /**
         * ID : 10096
         * Name : 05150002
         */
        @Expose
        @SerializedName("ID")
        private String ID;
        @Expose
        @SerializedName("Name")
        private String Name;

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }
    }

    public static class CategoryListBean implements Serializable {
        private static final long serialVersionUID = -7663002914728542508L;
        /**
         * ID : 12
         * Name : 半裙
         * ParentID : 1
         * ParentName : 女装
         */
        @Expose
        @SerializedName("ID")
        private int ID;
        @Expose
        @SerializedName("Name")
        private String Name;
        @Expose
        @SerializedName("ParentID")
        private int ParentID;
        @Expose
        @SerializedName("ParentName")
        private String ParentName;

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }

        public int getParentID() {
            return ParentID;
        }

        public void setParentID(int ParentID) {
            this.ParentID = ParentID;
        }

        public String getParentName() {
            return ParentName;
        }

        public void setParentName(String ParentName) {
            this.ParentName = ParentName;
        }
    }

    public static class PropertyListBean implements Serializable {
        private static final long serialVersionUID = 3051391920314424957L;
        /**
         * ID : 10
         * Name : 合体
         * ParentID : 7
         * ParentName : 商品版型
         */
        @Expose
        @SerializedName("ID")
        private int ID;
        @Expose
        @SerializedName("Name")
        private String Name;
        @Expose
        @SerializedName("ParentID")
        private int ParentID;
        @Expose
        @SerializedName("ParentName")
        private String ParentName;

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }

        public int getParentID() {
            return ParentID;
        }

        public void setParentID(int ParentID) {
            this.ParentID = ParentID;
        }

        public String getParentName() {
            return ParentName;
        }

        public void setParentName(String ParentName) {
            this.ParentName = ParentName;
        }
    }

    public static class ColorPicsBean implements Serializable {
        private static final long serialVersionUID = 4138047355040815671L;
        /**
         * Color :空为主色卡
         * Url : upyun:banwo-img:/shop/121/43433480d.jpg
         */
        private boolean is_upload=true;
        public boolean is_Select=false;
        public boolean is_upload() {
            return is_upload;
        }

        public void setIs_upload(boolean is_upload) {
            this.is_upload = is_upload;
        }

        @Expose
        @SerializedName("Color")
        private String Color="";
        @Expose
        @SerializedName("Url")
        private String Url="";

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

    public static class ExtendPropertyTypeListV2Bean implements Serializable {
        private static final long serialVersionUID = 5409641396889938696L;
        /**
         * ExtendPropertyList : [[{"Value":"慢慢学会好的互粉互粉","ID":2,"Name":"衣长"},{"Value":"一直都","ID":3,"Name":"胸围"},{"Value":"宇宇","ID":4,"Name":"腰围"},{"Value":"ｖｇ","ID":5,"Name":"臀围"},{"Value":"吃货出成绩","ID":6,"Name":"体重"},{"Value":"聚聚","ID":7,"Name":"身高"},{"Value":"给 v","ID":8,"Name":"袖长"},{"Value":"除虫菊","ID":9,"Name":"肩宽"},{"Value":" vv","ID":10,"Name":"下摆"}],[{"Value":"的计划的","ID":2,"Name":"衣长"},{"Value":"39F","ID":3,"Name":"胸围"},{"Value":"水蛇腰","ID":4,"Name":"腰围"},{"Value":"巨臀","ID":5,"Name":"臀围"},{"Value":"吃货出成绩2","ID":6,"Name":"体重"},{"Value":"聚聚2","ID":7,"Name":"身高"},{"Value":"给 v2","ID":8,"Name":"袖长"},{"Value":"除虫菊2","ID":9,"Name":"肩宽"},{"Value":" vv2","ID":10,"Name":"下摆"}]]
         * TypeID : 2
         * TypeName : 尺码表
         * IsValue : true
         */
        @Expose
        @SerializedName("TypeID")
        private int TypeID;
        @Expose
        @SerializedName("TypeName")
        private String TypeName="";
        @Expose
        @SerializedName("IsValue")
        private boolean IsValue;
        @Expose
        private  boolean IsMore;
        private String selContent="";
        @Expose
        private boolean IsGrouping;

        public boolean isGrouping() {
            return IsGrouping;
        }

        public void setGrouping(boolean grouping) {
            IsGrouping = grouping;
        }

        public String getSelContent() {
            return selContent;
        }

        public void setSelContent(String selContent) {
            this.selContent = selContent;
        }
        public boolean isMore() {
            return IsMore;
        }

        public void setMore(boolean more) {
            IsMore = more;
        }

        @Expose
        @SerializedName("ExtendPropertyList")

        private List<List<ExtendPropertyTypeListV2Bean.ExtendPropertyListBean>> ExtendPropertyList;

        public int getTypeID() {
            return TypeID;
        }

        public void setTypeID(int TypeID) {
            this.TypeID = TypeID;
        }

        public String getTypeName() {
            return TypeName;
        }

        public void setTypeName(String TypeName) {
            this.TypeName = TypeName;
        }

        public boolean isIsValue() {
            return IsValue;
        }

        public void setIsValue(boolean IsValue) {
            this.IsValue = IsValue;
        }

        public List<List<ExtendPropertyTypeListV2Bean.ExtendPropertyListBean>> getExtendPropertyList() {
            return ExtendPropertyList;
        }

        public void setExtendPropertyList(List<List<ExtendPropertyTypeListV2Bean.ExtendPropertyListBean>> ExtendPropertyList) {
            this.ExtendPropertyList = ExtendPropertyList;
        }

        public static class ExtendPropertyListBean implements Serializable{
            private static final long serialVersionUID = 4574208932100424991L;
            /**
             * Value : null
             * ID : 1
             * Name : 立即发货，24小时发货
             */
            public boolean isSelect;
            @Expose
            @SerializedName("Value")
            private String Value="";
            @Expose
            @SerializedName("ID")
            private int ID;
            @Expose
            @SerializedName("Name")
            private String Name="";

            public String getValue() {
                return Value;
            }

            public void setValue(String Value) {
                this.Value = Value;
            }

            public int getID() {
                return ID;
            }

            public void setID(int ID) {
                this.ID = ID;
            }

            public String getName() {
                return Name;
            }

            public void setName(String Name) {
                this.Name = Name;
            }
        }
    }
}
