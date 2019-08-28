package com.nahuo.buyertool.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class ReceiveAccountModel implements Serializable {

    private static final long serialVersionUID = -3718423961923385889L;
    
    @Expose
    private String Accounts="";
    @Expose
    private String Images="";
	public String getAccounts() {
		return Accounts;
	}
	public void setAccounts(String accounts) {
		Accounts = accounts;
	}
	public String getImages() {
		return Images;
	}
	public void setImages(String images) {
		Images = images;
	}
    
    
}
