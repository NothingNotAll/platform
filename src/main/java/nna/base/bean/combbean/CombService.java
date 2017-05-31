package nna.base.bean.combbean;

import nna.base.bean.Clone;
import nna.base.bean.dbbean.PlatformColumn;
import nna.base.bean.dbbean.PlatformService;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * for
 * @author NNA-SHUAI
 * @create 2017-05-13 22:03
 **/

public class CombService extends Clone {
    private static final long serialVersionUID = -7L;
    private PlatformService service;

    private Method serviceMethod;
    private Object serviceObject;

    public PlatformService getService() {
        return service;
    }

    public void setService(PlatformService service) {
        this.service = service;
    }

    public Method getServiceMethod() {
        return serviceMethod;
    }

    public void setServiceMethod(Method serviceMethod) {
        this.serviceMethod = serviceMethod;
    }

    public Object getServiceObject() {
        return serviceObject;
    }

    public void setServiceObject(Object serviceObject) {
        this.serviceObject = serviceObject;
    }

}
