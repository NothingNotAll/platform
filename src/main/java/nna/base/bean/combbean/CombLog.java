package nna.base.bean.combbean;

import nna.base.bean.Clone;
import nna.base.bean.dbbean.PlatformLog;

import java.io.File;
import java.util.concurrent.atomic.AtomicLong;

/**
 * f
 * @author NNA-SHUAI
 * @create 2017-05-13 22:28
 **/

public class CombLog extends Clone {
    private static final long serialVersionUID = -6L;
    private PlatformLog platformLog;

    private AtomicLong nextLogSeq;
    private File logDir;

    public AtomicLong getNextLogSeq() {
        return nextLogSeq;
    }

    public void setNextLogSeq(AtomicLong nextLogSeq) {
        this.nextLogSeq = nextLogSeq;
    }

    public File getLogDir() {
        return logDir;
    }

    public void setLogDir(File logDir) {
        this.logDir = logDir;
    }

    public PlatformLog getPlatformLog() {
        return platformLog;
    }

    public void setPlatformLog(PlatformLog platformLog) {
        this.platformLog = platformLog;
    }
}
