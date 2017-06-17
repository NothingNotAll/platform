package nna.base.dispatch;

import nna.MetaBean;
import nna.base.bean.dbbean.*;
import nna.base.log.Log;

import java.lang.reflect.Method;
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
        return metaBean.getOutColumns();
    }

    public Map<String,String[]> getReq() {
        return metaBean.getInnerColumns();
    }

    public Map<String,String[]> getRsp(){
        return metaBean.getRspColumns();
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

    public Object getRenderObject(){
        return metaBean.getRenderObject();
    }

    public Method getRenderMethod(){
        return metaBean.getRenderMethod();
    }

    public int getProtocolType(){
        return metaBean.getProtocolType();
    }

    public PlatformSession getPlatformSession() {
        return metaBean.getPlatformSession();
    }

    public PlatformLog getPlatformLog() {
        return metaBean.getPlatformLog();
    }

    public int getLogLevel() {
        return metaBean.getLogLevel();
    }

    public int getLogTimes() {
        return metaBean.getLogTimes();
    }

    public void setLog(Log log) {
        metaBean.setLog(log);
    }
}
