package nna.base.db;

import nna.base.bean.Clone;

/**
 * DBMeta
 *
 * @author NNA-SHUAI
 * @create 2017-05-16 12:50
 **/

public class DBMeta extends Clone{
    private String url;
    private String driver;
    private String account;
    private String password;


    private int conPoolSize;
    private int closePoolSize;
    private int conCount;
    private int closeCount;
    private Long heartTestMS;
    private int failTryTime;
    private boolean isInit;

    public DBMeta(
             boolean isInit,
             String url,
             String driver,
             String account,
             String password,
                 int conPoolSize,
                  int closePoolSize,
                  int conCount,
                  Long heartTestMS,
                  int failTryTime){
        this.isInit=isInit;
        this.url=url;
        this.driver=driver;
        this.account=account;
        this.password=password;
        this.conPoolSize=conPoolSize;
        this.closePoolSize=closePoolSize;
        this.conCount=conCount;
        this.heartTestMS=heartTestMS;
        this.failTryTime=failTryTime;
    }

    public DBMeta(){

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getConPoolSize() {
        return conPoolSize;
    }

    public void setConPoolSize(int conPoolSize) {
        this.conPoolSize = conPoolSize;
    }

    public int getClosePoolSize() {
        return closePoolSize;
    }

    public void setClosePoolSize(int closePoolSize) {
        this.closePoolSize = closePoolSize;
    }

    public int getConCount() {
        return conCount;
    }

    public void setConCount(int conCount) {
        this.conCount = conCount;
    }

    public Long getHeartTestMS() {
        return heartTestMS;
    }

    public void setHeartTestMS(Long heartTestMS) {
        this.heartTestMS = heartTestMS;
    }

    public int getFailTryTime() {
        return failTryTime;
    }

    public void setFailTryTime(int failTryTime) {
        this.failTryTime = failTryTime;
    }

    public boolean isInit() {
        return isInit;
    }

    public void setInit(boolean init) {
        isInit = init;
    }

    public int getCloseCount() {
        return closeCount;
    }

    public void setCloseCount(int closeCount) {
        this.closeCount = closeCount;
    }

}
