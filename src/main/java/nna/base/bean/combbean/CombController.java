package nna.base.bean.combbean;

import nna.base.bean.Clone;
import nna.base.bean.dbbean.PlatformController;
import nna.base.bean.dbbean.PlatformEntry;

import java.lang.reflect.Method;

/**
 * f
 * @author NNA-SHUAI
 * @create 2017-05-13 22:10
 **/

public class CombController extends Clone {
    private static final long serialVersionUID = -4L;
    private PlatformController controller;

    private Method renderMethod;
    private Object renderObject;

    public Method getRenderMethod() {
        return renderMethod;
    }

    public void setRenderMethod(Method renderMethod) {
        this.renderMethod = renderMethod;
    }

    public Object getRenderObject() {
        return renderObject;
    }

    public void setRenderObject(Object renderObject) {
        this.renderObject = renderObject;
    }

    public PlatformController getController() {
        return controller;
    }

    public void setController(PlatformController controller) {
        this.controller = controller;
    }

}
