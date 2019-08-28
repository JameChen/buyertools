package com.nahuo.buyertool.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;

public class StallsSearchModel implements Serializable {

    private static final long serialVersionUID = -2492773240079820019L;

    @Expose
    private int ID;
    @Expose
    private String Name;
    @Expose
    private List<FloorModel> FloorList;


    public static class FloorModel {
        @Expose
        private int ID;
        @Expose
        private String Name;
        @Expose
        private List<StallsModel> StallsList;

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public List<StallsModel> getStallsList() {
            return StallsList;
        }

        public void setStallsList(List<StallsModel> stallsList) {
            StallsList = stallsList;
        }
    }

    public static class StallsModel {
        @Expose
        private int ID;
        @Expose
        private String Name;

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

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

    public void setName(String name) {
        Name = name;
    }

    public List<FloorModel> getFloorList() {
        return FloorList;
    }

    public void setFloorList(List<FloorModel> floorList) {
        FloorList = floorList;
    }
}
