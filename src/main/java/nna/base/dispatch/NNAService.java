package nna.base.dispatch;

import nna.Marco;
import nna.MetaBean;
import nna.base.bean.dbbean.PlatformApp;
import nna.base.bean.dbbean.PlatformLog;
import nna.base.bean.dbbean.PlatformService;
import nna.base.bean.dbbean.PlatformSession;
import nna.base.log.Log;
import nna.base.util.LogUtil;

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
    public static String service(Map<String,String[]> map) throws Exception {
        String entryCode=map.get(Marco.HEAD_ENTRY_CODE)[0];
        MetaBean metaBean=getMetaBean(entryCode);
        getAndSetLog(metaBean);
        String rspStr="";
        try {
            dispatch(metaBean);
            rspStr=getRspStr(metaBean);
        } catch (Exception e) {
            e.printStackTrace();
        }catch (Throwable e){
            e.printStackTrace();
        }finally {
            destroy(metaBean);
        }
        return rspStr;
    }

    private static void getAndSetLog(MetaBean metaBean) throws Exception {
        PlatformApp platformApp=metaBean.getPlatformApp();
        PlatformService platformService=metaBean.getPlatformService();
        PlatformSession platformSession=metaBean.getPlatformSession();
        Integer userId=platformSession.getSessionUseid();
        String userIdStr=userId==null?"nosession":userId.toString();
        String appEnCode=platformApp.getAppEn();
        String serviceCode=platformService.getServiceName();
        String path=userIdStr+"-"+appEnCode+"-"+serviceCode;
        PlatformLog platformLog=metaBean.getPlatformLog();
        Log log= LogUtil.getLog(
                platformLog.getLogDir()+"/"+path,
                path,
                metaBean.getLogLevel(),
                platformLog.getLogBufferThreshold(),
                platformLog.getLogCloseTimedout(),
                platformLog.getLogEncode()
        );
        metaBean.setLog(log);
    }

    private static void destroy(MetaBean metaBean) {
        metaBean.getLog().close();
    }

    private static String getRspStr(MetaBean metaBean) throws InvocationTargetException, IllegalAccessException {
        int protocolType=metaBean.getProtocolType();
        return Protocol.protocolAdapter(metaBean,protocolType);
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
