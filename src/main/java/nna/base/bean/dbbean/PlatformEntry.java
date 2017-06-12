package nna.base.bean.dbbean;

import nna.base.bean.Clone;

/**
 * for db table of controller id to en name
 * @author NNA-SHUAI
 * @create 2017-05-13 16:20
 **/

public class PlatformEntry extends Clone {
    private static final Long serialVersionUID=18L;

    private int entryId;
    private Boolean entryFree;
    private String entryCode;
    private int entryAppId;
    private int entryControllerId;
    private String entryServiceId;
    private int entryDBId;
    private int entryLogId;
    private int entryTempSize;
    private float entryTimeout;
    private String entryTransactions;
    private String entryReqId;
    private String entryRspId;
    private String entryDesc;

    public PlatformEntry(){

    }

    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    public String getEntryCode() {
        return entryCode;
    }

    public void setEntryCode(String entryCode) {
        this.entryCode = entryCode;
    }

    public String getEntryDesc() {
        return entryDesc;
    }

    public void setEntryDesc(String entryDesc) {
        this.entryDesc = entryDesc;
    }

    public int getEntryControllerId() {
        return entryControllerId;
    }

    public void setEntryControllerId(int entryControllerId) {
        this.entryControllerId = entryControllerId;
    }

    public int getEntryAppId() {
        return entryAppId;
    }

    public void setEntryAppId(int entryAppId) {
        this.entryAppId = entryAppId;
    }

    public float getEntryTimeout() {
        return entryTimeout;
    }

    public void setEntryTimeout(float entryTimeout) {
        this.entryTimeout = entryTimeout;
    }

    public String getEntryServiceId() {
        return entryServiceId;
    }

    public void setEntryServiceId(String entryServiceId) {
        this.entryServiceId = entryServiceId;
    }

    public int getEntryDBId() {
        return entryDBId;
    }

    public void setEntryDBId(int entryDBId) {
        this.entryDBId = entryDBId;
    }

    public int getEntryLogId() {
        return entryLogId;
    }

    public void setEntryLogId(int entryLogId) {
        this.entryLogId = entryLogId;
    }

    public int getEntryTempSize() {
        return entryTempSize;
    }

    public void setEntryTempSize(int entryTempSize) {
        this.entryTempSize = entryTempSize;
    }

    public String getEntryTransactions() {
        return entryTransactions;
    }

    public void setEntryTransactions(String entryTransactions) {
        this.entryTransactions = entryTransactions;
    }

    public String getEntryReqId() {
        return entryReqId;
    }

    public void setEntryReqId(String entryReqId) {
        this.entryReqId = entryReqId;
    }

    public String getEntryRspId() {
        return entryRspId;
    }

    public void setEntryRspId(String entryRspId) {
        this.entryRspId = entryRspId;
    }

    public Boolean getEntryFree() {
        return entryFree;
    }

    public void setEntryFree(Boolean entryFree) {
        this.entryFree = entryFree;
    }
}
