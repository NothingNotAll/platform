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
    private String entryCode;
    private int entryAppId;
    private String entryUri;
    private int entryControllerId;
    private float entryTimeout;
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

    public String getEntryUri() {
        return entryUri;
    }

    public void setEntryUri(String entryUri) {
        this.entryUri = entryUri;
    }

    public float getEntryTimeout() {
        return entryTimeout;
    }

    public void setEntryTimeout(float entryTimeout) {
        this.entryTimeout = entryTimeout;
    }
}
