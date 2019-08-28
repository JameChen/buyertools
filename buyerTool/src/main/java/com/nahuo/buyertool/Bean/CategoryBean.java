package com.nahuo.buyertool.Bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jame on 2017/7/24.
 */

public class CategoryBean implements Serializable {

    private static final long serialVersionUID = -9067599099945977914L;
    /**
     * ID : 1
     * Name : 女装
     * Childs : [{"ID":4,"Name":"连衣裙","ParentID":1},{"ID":11,"Name":"外套","ParentID":1},{"ID":12,"Name":"半裙","ParentID":1},{"ID":13,"Name":"长裙","ParentID":1},{"ID":14,"Name":"T恤","ParentID":1}]
     */
    @Expose
    @SerializedName("ID")
    private int ID;
    @Expose
    @SerializedName("Name")
    private String Name="";
    @Expose
    @SerializedName("Childs")
    private List<ChildsBean> Childs;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
    public boolean is_Select=false;
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

    public static class ChildsBean implements Serializable{
        private static final long serialVersionUID = -8654891580043948933L;
        /**
         * ID : 4
         * Name : 连衣裙
         * ParentID : 1
         */
        @Expose
        @SerializedName("ID")
        private int ID;
        @Expose
        @SerializedName("Name")
        private String Name="";
        @Expose
        @SerializedName("ParentID")
        private int ParentID;
        public boolean is_Select=false;

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
