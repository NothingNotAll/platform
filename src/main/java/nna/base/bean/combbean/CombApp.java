package nna.base.bean.combbean;

import nna.base.bean.Clone;
import nna.base.bean.dbbean.PlatformApp;

import java.lang.reflect.Method;

/**
 * for App
 * @author NNA-SHUAI
 * @create 2017-05-13 21:16
 **/

public class CombApp extends Clone{
    private static final long serialVersionUID = -2L;
    private PlatformApp app;

    private Method appDispatchMethod;
    private Object appDispatchObject;

    public PlatformApp getApp() {
        return app;
    }

    public void setApp(PlatformApp app) {
        this.app = app;
    }

    public Method getAppDispatchMethod() {
        return appDispatchMethod;
    }

    public void setAppDispatchMethod(Method appDispatchMethod) {
        this.appDispatchMethod = appDispatchMethod;
    }

    public Object getAppDispatchObject() {
        return appDispatchObject;
    }

    public void setAppDispatchObject(Object appDispatchObject) {
        this.appDispatchObject = appDispatchObject;
    }

}
