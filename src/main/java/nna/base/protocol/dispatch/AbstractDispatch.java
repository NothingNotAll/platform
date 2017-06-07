package nna.base.protocol.dispatch;

import nna.MetaBean;
import nna.base.bean.dbbean.PlatformLog;
import nna.base.bean.dbbean.PlatformSession;
import nna.base.init.NNAServiceStart;
import nna.base.log.Log;
import nna.base.log.LogEntry;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author NNA-SHUAI
 * @create 2017-05-22 1:52
 **/

public abstract class AbstractDispatch<R,S> {

    static {
        try {
            System.out.println("--------------------------------NNA Service start");
            Class.forName(NNAServiceStart.class.getCanonicalName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static SimpleDateFormat yyyyMMdd=new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat HHmmssSSS=new SimpleDateFormat("HH-mm-ss-SSS");


    public abstract OutputStream getOutPutStream(S response) throws IOException;
    public abstract Map<String,String[]> getReqColMap(R request);
    /*
        str[0] entryOID
        str[1] sessionId
    *
    * */
    public abstract String[] getPlatformEntryId(R request);


    protected void dispatch(R request,S response) throws InvocationTargetException, IllegalAccessException, IOException {
        MetaBean confMeta=null;
        try{
            Integer oid=getConfMetaOID(request);
            confMeta= MetaBean.getConfMetaCache().get(oid).clone();
            confMeta.setOutReq(getReqColMap(request));
            ConfMetaSetFactory.setConfMeta(confMeta);
            initUserLog(confMeta);
            Method dispatchMethod=confMeta.getAppServiceMethod();
            Object dispatchObject=confMeta.getAppServiceObject();
            dispatchMethod.invoke(dispatchObject,ConfMetaSetFactory.getMetaBeanWrapper());
            String renderPage=confMeta.getRenderPage();
        }catch (Exception e){
            e.printStackTrace();
        }catch (Throwable throwable){

        }finally {
            destroy(confMeta);
        }
    }

    private void destroy(MetaBean confMeta) {
        Thread thread=Thread.currentThread();
        Long id=thread.getId();
        MetaBean.getMetaMonitor().remove(id);
        LogEntry.submitCloseEvent(confMeta.getLog());
        ConfMetaSetFactory.setConfMeta(null);//for prevent memory leak
    }

    private Integer getConfMetaOID(R request) {
        String[] rs=getPlatformEntryId(request);
        String entryOID=rs[0];
        System.out.println(entryOID);
        String sessionId=rs[1];
        return null;
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
                        +"/"+ confMeta.getPlatformEntry().getEntryCode()
                        +"/"+ serviceName+"-"+HHmmssSSS.format(System.currentTimeMillis())+"-";
        Log log=getLog(confMeta.getLogNoGen(),combLog.getLogLevel()
                ,logDir,
                combLog,
                serviceName);
        confMeta.setLog(log);
    }

    private static Log getLog(
            AtomicLong no,
            int logLevel,
                              String logDir,
                              PlatformLog platformLog,
                              String serviceName) {
        return LogEntry.submitInitEvent(
                logDir,
                no,
                serviceName+".log",
                logLevel,
                platformLog.getLogBufferThreshold(),
                platformLog.getLogCloseTimedout(),
                platformLog.getLogEncode()
        );
    }

}
