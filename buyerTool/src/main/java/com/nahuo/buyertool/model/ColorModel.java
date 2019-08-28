package com.nahuo.buyertool.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class ColorModel implements Serializable {

    private static final long serialVersionUID = -3718423961923385880L;
    
    @Expose
    private int ID;// 颜色ID
    @Expose
    private String Name;// 颜色名称

    public int getID() {
	return ID;
    }

    public void setID(int iD) {
	ID = iD;
    }

    public String getName() {
	return Name;
    }

    public void setName(String name) {
	Name = name;
    }

}
