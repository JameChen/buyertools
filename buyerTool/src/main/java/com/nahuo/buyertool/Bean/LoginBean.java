package com.nahuo.buyertool.Bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by jame on 2017/9/27.
 */

public class LoginBean implements Serializable{

    private static final long serialVersionUID = -4543780604066947976L;
    /**
     * Code :
     * Result : true
     * Message :
     * Data : {"Token":"kWXB+EzxAQI8JNx5yR4bESEJfqp0u2Yupe+KDUc24YRoVMR1RjAKyrz34EMhVwxLSCD99CzfsBFiSgUQUQMSrUEAfCXAZKNWBSN9svSzK3RlDsBK2SjAtgo94NFbJK9u0t+PXwIE/hSGTR7P9VKzWJnVpADJgnQ4yECy3JkFUvM=","UserID":426188,"UserName":"zzb"}
     */
    @Expose
    @SerializedName("Code")
    private String Code;
    @Expose
    @SerializedName("Result")
    private boolean Result;
    @Expose
    @SerializedName("Message")
    private String Message;
    @Expose
    @SerializedName("Data")
    private AccountBean Data;

    public String getCode() {
        return Code;
    }

    public void setCode(String Code) {
        this.Code = Code;
    }

    public boolean isResult() {
        return Result;
    }

    public void setResult(boolean Result) {
        this.Result = Result;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public AccountBean getData() {
        return Data;
    }

    public void setData(AccountBean Data) {
        this.Data = Data;
    }


}
