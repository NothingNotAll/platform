package nna.base.bean.dbbean;

import nna.base.bean.Clone;

import java.sql.Timestamp;

/**
 * for db table of develop user
 * @author NNA-SHUAI
 * @create 2017-05-13 16:22
 **/

public class PlatformUser extends Clone {
    private static final Long serialVersionUID=10L;

    private int userId=-1;
    private String userName;
    private String userPassword;
    private boolean userStatus;
    private Timestamp userCreateTimestamp;
    private double userUploadspeedLimit;
    private double userDownloadspeedLimit;

    public PlatformUser(){

    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public boolean isUserStatus() {
        return userStatus;
    }

    public void setUserStatus(boolean userStatus) {
        this.userStatus = userStatus;
    }

    public Timestamp getUserCreateTimestamp() {
        return userCreateTimestamp;
    }

    public void setUserCreateTimestamp(Timestamp userCreateTimestamp) {
        this.userCreateTimestamp = userCreateTimestamp;
    }

    public double getUserUploadspeedLimit() {
        return userUploadspeedLimit;
    }

    public void setUserUploadspeedLimit(double userUploadspeedLimit) {
        this.userUploadspeedLimit = userUploadspeedLimit;
    }

    public double getUserDownloadspeedLimit() {
        return userDownloadspeedLimit;
    }

    public void setUserDownloadspeedLimit(double userDownloadspeedLimit) {
        this.userDownloadspeedLimit = userDownloadspeedLimit;
    }
}
