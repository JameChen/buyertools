package com.nahuo.buyertool.Bean;

/**
 * Created by jame on 2017/7/3.
 */

public class SelBean {
    int id=-1;
    String content;
    String mId;

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public SelBean(int id, String content) {
        this.id = id;
        this.content = content;
    }
    public SelBean(String mId,String content){
        this.mId=mId;
        this.content=content;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
