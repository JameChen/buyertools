package com.nahuo.buyertool.Bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by jame on 2017/9/27.
 */

public class AccountBean implements Serializable {

    private static final long serialVersionUID = 6267976614602283166L;
    /**
     * Token : kWXB+EzxAQI8JNx5yR4bESEJfqp0u2Yupe+KDUc24YRoVMR1RjAKyrz34EMhVwxLSCD99CzfsBFiSgUQUQMSrUEAfCXAZKNWBSN9svSzK3RlDsBK2SjAtgo94NFbJK9u0t+PXwIE/hSGTR7P9VKzWJnVpADJgnQ4yECy3JkFUvM=
     * UserID : 426188
     * UserName : zzb
     */
    @Expose
    @SerializedName("Token")
    private String Token;
    @Expose
    @SerializedName("UserID")
    private int UserID;
    @Expose
    @SerializedName("UserName")
    private String UserName="";
    private String phoneNo="";
    private String pwd="";

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String Token) {
        this.Token = Token;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int UserID) {
        this.UserID = UserID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }
}

