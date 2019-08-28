package com.nahuo.buyertool.Bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jame on 2017/7/24.
 */

public class AttributeBean implements Serializable {

    private static final long serialVersionUID = 6618678229322535759L;
    /**
     * ID : 7
     * Name : 商品版型
     * Childs : [{"ID":8,"Name":"紧身","ParentID":7},{"ID":9,"Name":"修身","ParentID":7},{"ID":10,"Name":"合体","ParentID":7},{"ID":11,"Name":"宽松","ParentID":7}]
     */
    @Expose
    @SerializedName("ID")
    private int ID;
    @Expose
    @SerializedName("Name")
    private String Name;
    @Expose
    @SerializedName("Childs")
    private List<ChildsBean> Childs;

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

    public List<ChildsBean> getChilds() {
        return Childs;
    }

    public void setChilds(List<ChildsBean> Childs) {
        this.Childs = Childs;
    }

    public static class ChildsBean implements Serializable {
        private static final long serialVersionUID = 4281634274214500935L;
        /**
         * ID : 8
         * Name : 紧身
         * ParentID : 7
         */
        private boolean isCheck;

        public boolean isCheck() {
            return isCheck;
        }

        public void setCheck(boolean check) {
            isCheck = check;
        }

        @Expose
        @SerializedName("ID")
        private int ID;
        @Expose
        @SerializedName("Name")
        private String Name;
        @Expose
        @SerializedName("ParentID")
        private int ParentID;

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
    }
}
