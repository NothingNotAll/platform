package nna.base.protocol.dispatch;

import nna.MetaBean;
import nna.base.bean.dbbean.PlatformApp;
import nna.base.bean.dbbean.PlatformColumn;
import nna.base.bean.dbbean.PlatformController;
import nna.base.bean.dbbean.PlatformService;
import nna.base.log.Log;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author NNA-SHUAI
 * @create 2017-06-07 17:37
 **/

public class MetaBeanWrapper {
    MetaBean metaBean;

    public MetaBeanWrapper(MetaBean metaBean){
        this.metaBean=metaBean;
    }

    public Log getLog() {
        return metaBean.getLog();
    }

    public PlatformApp getPlatformApp() {
        return metaBean.getPlatformApp();
    }

    public PlatformController getPlatformController() {
        return metaBean.getPlatformController();
    }

    public PlatformService getPlatformService() {
        return metaBean.getPlatformService();
    }

    public PlatformColumn[] getReqColConfig() {
        return metaBean.getReqColConfig();
    }

    public Map<String,String[]> getOutReq() {
        return metaBean.getOutReq();
    }

    public Map<String,String[]> getReq() {
        return metaBean.getReq();
    }

    public Method getServiceMethod() {
        return metaBean.getServiceMethod();
    }

    public Object getServiceObject() {
        return metaBean.getServiceObject();
    }

    public PlatformColumn[] getRspColConfig() {
        return metaBean.getRspColConfig();
    }

    public HashMap<String,String[]> getRsp() {
        return metaBean.getRsp();
    }
}
