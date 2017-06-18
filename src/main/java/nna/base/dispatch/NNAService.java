package nna.base.dispatch;

import nna.Marco;
import nna.MetaBean;
import nna.base.bean.dbbean.PlatformApp;
import nna.base.bean.dbbean.PlatformLog;
import nna.base.bean.dbbean.PlatformService;
import nna.base.bean.dbbean.PlatformSession;
import nna.base.dispatch.protocol.Protocol;
import nna.base.log.Log;

import static nna.base.dispatch.Dispatch.dispatch;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author NNA-SHUAI
 * @create 2017-05-22 1:52
 **/

public class NNAService {

    private NNAService(){}
    public static String service(Map<String,String[]> map){
        String entryCode=map.get(Marco.HEAD_ENTRY_CODE)[0];
        MetaBean metaBean=getMetaBean(entryCode);
        MetaBeanWrapper metaBeanWrapper=new MetaBeanWrapper(metaBean);
        getAndSetLog(metaBeanWrapper);
        String rspStr="";
        try {
            dispatch(metaBeanWrapper);
            rspStr=getRspStr(metaBeanWrapper);
        } catch (Exception e) {
            e.printStackTrace();
        }catch (Throwable e){
            e.printStackTrace();
        }finally {
            destroy(metaBeanWrapper);
        }
        return rspStr;
    }

    private static void getAndSetLog(MetaBeanWrapper metaBean) {
        PlatformApp platformApp=metaBean.getPlatformApp();
        PlatformService platformService=metaBean.getPlatformService();
        PlatformSession platformSession=metaBean.getPlatformSession();
        Integer userId=platformSession.getSessionUseid();
        String userIdStr=userId==null?"nosession":userId.toString();
        String appEnCode=platformApp.getAppEn();
        String serviceCode=platformService.getServiceName();
        String path=userIdStr+"-"+appEnCode+"-"+serviceCode;
        PlatformLog platformLog=metaBean.getPlatformLog();
        Log log=Log.getLog(
                platformLog.getLogDir()+"/"+path,
                path,
                metaBean.getLogLevel(),
                platformLog.getLogBufferThreshold(),
                platformLog.getLogCloseTimedout(),
                platformLog.getLogEncode(),
                metaBean.getLogTimes()
        );
        metaBean.setLog(log);
    }

    private static void destroy(MetaBeanWrapper metaBeanWrapper) {
        metaBeanWrapper.getLog().close();
    }

    private static String getRspStr(MetaBeanWrapper metaBeanWrapper) throws InvocationTargetException, IllegalAccessException {
        int protocolType=metaBeanWrapper.getProtocolType();
        return Protocol.protocolAdapter(metaBeanWrapper,protocolType);
    }

    private static MetaBean getMetaBean(String entryCode) {
        MetaBean metaBean;
        ConcurrentHashMap<String,Integer> map=MetaBean.getSrvEnNmToId();
        Integer mbID=map.get(entryCode);
        metaBean=MetaBean.getConfMetaCache().get(mbID);
        metaBean=metaBean.clone();
        return metaBean;
    }
}
