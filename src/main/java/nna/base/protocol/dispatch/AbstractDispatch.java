package nna.base.protocol.dispatch;

import nna.Marco;
import nna.MetaBean;
import nna.base.bean.dbbean.PlatformLog;
import nna.base.bean.dbbean.PlatformSession;
import nna.base.init.NNAServiceStart;
import nna.base.log.Log;
import nna.base.util.CharUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * @author NNA-SHUAI
 * @create 2017-05-22 1:52
 **/

public abstract class AbstractDispatch {

    static {
        try {
            System.out.println("--------------------------------NNA Service start");
            Class.forName(NNAServiceStart.class.getCanonicalName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static SimpleDateFormat yyyyMMdd=new SimpleDateFormat("yyyyMMdd");

    protected String dispatch(Map<String,String[]> map) throws InvocationTargetException, IllegalAccessException, IOException {
        MetaBean confMeta=null;
        String responseStr = null;
        try{
            String entryCode=map.get(Marco.HEAD_ENTRY_CODE)[0];
            Integer oid=MetaBean.getSrvEnNmToId().get(entryCode);
            confMeta= MetaBean.getConfMetaCache().get(oid).clone();
            confMeta.setOutColumns(map);
            ConfMetaSetFactory.setConfMeta(confMeta);
            initUserLog(confMeta);
            Dispatch.dispatch(ConfMetaSetFactory.getMetaBeanWrapper());
            String renderPage=confMeta.getRenderPage();
            Map columns=confMeta.getInnerColumns();
            if(renderPage!=null){
                responseStr=confMeta.getRenderMethod().invoke(confMeta.getRenderObject(),columns).toString();
            }else{
                responseStr= CharUtil.getJsonStr(columns);
            }
        }catch (Exception e){
            e.printStackTrace();
        }catch (Throwable throwable){

        }finally {
            destroy(confMeta);
        }
        return responseStr;
    }

    private void destroy(MetaBean confMeta) {
        Thread thread=Thread.currentThread();
        Long id=thread.getId();
        MetaBean.getMetaMonitor().remove(id);
        confMeta.getLog().close();
        ConfMetaSetFactory.setConfMeta(null);//for prevent memory leak
    }

    private boolean isDownLoadSource(String entryOID) {
        return false;
    }

    private boolean isUploadSource(String entryOID) {
        return false;
    }

    private static void initUserLog(MetaBean confMeta){
        String serviceName=confMeta.getPlatformService().getServiceName();
        PlatformSession confSession=confMeta.getPlatformSession();
        Integer userId=confSession==null?-1:confMeta.getCurrentUser().getUserId();
        PlatformLog combLog=confMeta.getPlatformLog();
        String logDir=
                combLog.getLogDir()+yyyyMMdd.format(System.currentTimeMillis())
                        +"/"+ String.valueOf(userId==-1?"nosession":userId)
                        +"/"+confMeta.getPlatformApp().getAppEn()
                        +"/"+ confMeta.getPlatformEntry().getEntryCode()+"/";
        Log log=getLog(
                combLog.getLogLevel()
                ,logDir,
                combLog,
                serviceName,confMeta.getLogTimes());
        confMeta.setLog(log);
    }

    private static Log getLog(
            int logLevel,
                              String logDir,
                              PlatformLog platformLog,
                              String serviceName,int logTimes) {
        return Log.getLog(
                logDir,
                serviceName,
                logLevel,
                platformLog.getLogBufferThreshold(),
                platformLog.getLogCloseTimedout(),
                platformLog.getLogEncode(),logTimes
        );
    }

}
