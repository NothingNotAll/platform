package nna.base.bean.confbean;

import nna.base.bean.Clone;
import nna.base.bean.combbean.CombSession;
import nna.base.bean.combbean.CombUser;

import java.util.HashMap;

/**
 * @author NNA-SHUAI
 * @create 2017-05-20 13:58
 **/

public class ConfSession extends Clone {
    private static final long serialVersionUID = -11L;
    private CombUser combUser;
    private CombSession combSession;
    private HashMap<String,Object> cache;
    private Long sessionMark;
    private Long lastAccessd;

    public ConfSession(){
        sessionMark=System.currentTimeMillis();
    }

    public CombUser getCombUser() {
        return combUser;
    }

    public void setCombUser(CombUser combUser) {
        this.combUser = combUser;
    }

    public HashMap<String, Object> getCache() {
        return cache;
    }

    public void setCache(HashMap<String, Object> cache) {
        this.cache = cache;
    }

    public CombSession getCombSession() {
        return combSession;
    }

    public void setCombSession(CombSession combSession) {
        this.combSession = combSession;
    }

    public Long getSessionMark() {
        return sessionMark;
    }

    public void setSessionMark(Long sessionMark) {
        this.sessionMark = sessionMark;
    }

    public Long getLastAccessd() {
        return lastAccessd;
    }

    public void setLastAccessd(Long lastAccessd) {
        this.lastAccessd = lastAccessd;
    }
}
