package nna.base.bean.dbbean;

import nna.base.bean.Clone;
import nna.enums.SessionType;

/**
 * @author NNA-SHUAI
 * @create 2017-05-20 14:00
 **/

public class PlatformSession extends Clone{
    private static final Long serialVersionUID=21L;
    private int sessionId;
    private SessionType sessionType;
    private int sessionTimeout;

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public SessionType getSessionType() {
        return sessionType;
    }

    public void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }
}
