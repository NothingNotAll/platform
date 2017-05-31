package nna.base.bean.dbbean;

import nna.base.bean.Clone;

/**
 * for db bean of app's db
 * @author NNA-SHUAI
 * @create 2017-05-13 16:06
 **/

public class PlatformDB extends Clone {
    private static final Long serialVersionUID=16L;

    private int dbId;
    private String dbDriver;
    private String dbUrl;
    private String dbAccount;
    private String dbPassword;
    private int dbHeartbeatTest;
    private int dbPoolsCount;
    private int dbPoolCount;
    private int dbLogId;
    private int dbFailTrytime;

    public PlatformDB(){

    }
    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getDbAccount() {
        return dbAccount;
    }

    public void setDbAccount(String dbAccount) {
        this.dbAccount = dbAccount;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public int getDbHeartbeatTest() {
        return dbHeartbeatTest;
    }

    public void setDbHeartbeatTest(int dbHeartbeatTest) {
        this.dbHeartbeatTest = dbHeartbeatTest;
    }

    public int getDbPoolCount() {
        return dbPoolCount;
    }

    public void setDbPoolCount(int dbPoolCount) {
        this.dbPoolCount = dbPoolCount;
    }

    public int getDbPoolsCount() {
        return dbPoolsCount;
    }

    public void setDbPoolsCount(int dbPoolsCount) {
        this.dbPoolsCount = dbPoolsCount;
    }

    public String getDbDriver() {
        return dbDriver;
    }

    public void setDbDriver(String dbDriver) {
        this.dbDriver = dbDriver;
    }

    public int getDbLogId() {
        return dbLogId;
    }

    public void setDbLogId(int dbLogId) {
        this.dbLogId = dbLogId;
    }

    public int getDbFailTrytime() {
        return dbFailTrytime;
    }

    public void setDbFailTrytime(int dbFailTrytime) {
        this.dbFailTrytime = dbFailTrytime;
    }
}
