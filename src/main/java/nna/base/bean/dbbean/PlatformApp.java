package nna.base.bean.dbbean;

import nna.base.bean.Clone;

import java.sql.Timestamp;

/**
 * for db bean of app
 * @author NNA-SHUAI
 * @create 2017-05-13 15:56
 **/

public class PlatformApp extends Clone {

    private static final Long serialVersionUID=1L;

    private int appId;
    private String appEn;;
    private String appCh;
    private boolean appStatus;
    private boolean appIsdistribute;
    private String appWorkspace;
    private Timestamp appCreateTimestamp;
    private String appEncode;
    private String appUploadpath;
    private String appDownloadpath;
    private String appDesc;

    public PlatformApp(){

    }
    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getAppEn() {
        return appEn;
    }

    public void setAppEn(String appEn) {
        this.appEn = appEn;
    }

    public boolean isAppStatus() {
        return appStatus;
    }

    public void setAppStatus(boolean appStatus) {
        this.appStatus = appStatus;
    }

    public String getAppCh() {
        return appCh;
    }

    public void setAppCh(String appCh) {
        this.appCh = appCh;
    }

    public String getAppDesc() {
        return appDesc;
    }

    public void setAppDesc(String appDesc) {
        this.appDesc = appDesc;
    }

    public String getAppEncode() {
        return appEncode;
    }

    public void setAppEncode(String appEncode) {
        this.appEncode = appEncode;
    }

    public static void main(String[] args){
        PlatformApp platformApp=new PlatformApp();
        platformApp.setAppDesc("clone");
        PlatformApp platformApp1= (PlatformApp) platformApp.clone();
        System.out.println(platformApp1.appDesc);
    }

    public boolean isAppIsdistribute() {
        return appIsdistribute;
    }

    public void setAppIsdistribute(boolean appIsdistribute) {
        this.appIsdistribute = appIsdistribute;
    }

    public Timestamp getAppCreateTimestamp() {
        return appCreateTimestamp;
    }

    public void setAppCreateTimestamp(Timestamp appCreateTimestamp) {
        this.appCreateTimestamp = appCreateTimestamp;
    }

    public String getAppUploadpath() {
        return appUploadpath;
    }

    public void setAppUploadpath(String appUploadpath) {
        this.appUploadpath = appUploadpath;
    }

    public String getAppDownloadpath() {
        return appDownloadpath;
    }

    public void setAppDownloadpath(String appDownloadpath) {
        this.appDownloadpath = appDownloadpath;
    }

    public String getAppWorkspace() {
        return appWorkspace;
    }

    public void setAppWorkspace(String appWorkspace) {
        this.appWorkspace = appWorkspace;
    }
}
