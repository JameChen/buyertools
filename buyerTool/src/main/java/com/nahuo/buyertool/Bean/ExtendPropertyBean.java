package com.nahuo.buyertool.Bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jame on 2018/7/27.
 */

public class ExtendPropertyBean implements Serializable {
    @Expose
    @SerializedName("ExtendPropertyTypeList")
    private List<ExtendPropertyTypeListBean> ExtendPropertyTypeList;

    public List<ExtendPropertyTypeListBean> getExtendPropertyTypeList() {
        return ExtendPropertyTypeList;
    }

    public void setExtendPropertyTypeList(List<ExtendPropertyTypeListBean> ExtendPropertyTypeList) {
        this.ExtendPropertyTypeList = ExtendPropertyTypeList;
    }

    public static class ExtendPropertyTypeListBean  implements Serializable{
        /**
         * TypeID : 1
         * TypeName : 现货
         * IsValue : false
         * ExtendPropertyList : [{"Value":null,"ID":1,"Name":"立即发货，24小时发货"}]
         */
        @Expose
        @SerializedName("TypeID")
        private int TypeID;
        @Expose
        @SerializedName("TypeName")
        private String TypeName="";
        @SerializedName("IsValue")
        @Expose
        private boolean IsValue;
        @SerializedName("ExtendPropertyList")
        private List<ExtendPropertyListBean> ExtendPropertyList;

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

        public List<ExtendPropertyListBean> getExtendPropertyList() {
            return ExtendPropertyList;
        }

        public void setExtendPropertyList(List<ExtendPropertyListBean> ExtendPropertyList) {
            this.ExtendPropertyList = ExtendPropertyList;
        }

        public static class ExtendPropertyListBean implements Serializable{
            private static final long serialVersionUID = 7646139294401199070L;
            /**
             * Value : null
             * ID : 1
             * Name : 立即发货，24小时发货
             */
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
