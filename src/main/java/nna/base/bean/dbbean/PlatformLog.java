package nna.base.bean.dbbean;

import nna.base.bean.Clone;

/**
 * for db table of app log config
 * @author NNA-SHUAI
 * @create 2017-05-13 16:10
 **/

public class PlatformLog extends Clone {
    private static final Long serialVersionUID=19L;

    private Integer logId;
    private String logDir;
    private int logBufferThreshold;
    private int logCloseTimedout;
    private int logLevel;
    private String logEncode;
    private boolean logEncrpt;

    public PlatformLog(){

    }

    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public String getLogDir() {
        return logDir;
    }

    public void setLogDir(String logDir) {
        this.logDir = logDir;
    }

    public int getLogBufferThreshold() {
        return logBufferThreshold;
    }

    public void setLogBufferThreshold(int logBufferThreshold) {
        this.logBufferThreshold = logBufferThreshold;
    }

    public int getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    public String getLogEncode() {
        return logEncode;
    }

    public void setLogEncode(String logEncode) {
        this.logEncode = logEncode;
    }

    public boolean isLogEncrpt() {
        return logEncrpt;
    }

    public void setLogEncrpt(boolean logEncrpt) {
        this.logEncrpt = logEncrpt;
    }

    public int getLogCloseTimedout() {
        return logCloseTimedout;
    }

    public void setLogCloseTimedout(int logCloseTimedout) {
        this.logCloseTimedout = logCloseTimedout;
    }
}
