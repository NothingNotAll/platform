package nna.base.bean;

/**
 * @author NNA-SHUAI
 * @create 2017-05-20 14:29
 **/

public abstract class SessionWrapper<T> {
    private T t;

    public SessionWrapper(T t){
        this.t=t;
    }

    public abstract Long getSessionId();
}
