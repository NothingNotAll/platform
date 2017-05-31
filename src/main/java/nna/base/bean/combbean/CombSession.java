package nna.base.bean.combbean;

import nna.base.bean.Clone;
import nna.base.bean.dbbean.PlatformSession;

/**
 * @author NNA-SHUAI
 * @create 2017-05-20 13:59
 **/

public class CombSession extends Clone{
    private static final long serialVersionUID = -11L;
    private PlatformSession platformSession;
    private Long timedOut;

    public PlatformSession getPlatformSession() {
        return platformSession;
    }

    public void setPlatformSession(PlatformSession platformSession) {
        this.platformSession = platformSession;
    }

    public Long getTimedOut() {
        return timedOut;
    }

    public void setTimedOut(Long timedOut) {
        this.timedOut = timedOut;
    }
}
