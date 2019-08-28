package com.nahuo.buyertool.Bean;

import java.io.Serializable;

/**
 * Created by jame on 2017/7/20.
 */

public class WheelBean implements Serializable{
    private static final long serialVersionUID = 3493529020175781657L;
    private int ID;
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
