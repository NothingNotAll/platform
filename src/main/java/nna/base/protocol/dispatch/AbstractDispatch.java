package nna.base.protocol.dispatch;

import nna.Marco;
import nna.base.bean.combbean.CombApp;
import nna.base.bean.combbean.CombController;
import nna.base.bean.combbean.CombLog;
import nna.base.bean.confbean.ConfMeta;
import nna.base.bean.confbean.ConfSession;
import nna.base.bean.dbbean.PlatformLog;
import nna.base.cache.CacheFactory;
import nna.base.cache.NNAServiceInit0;
import nna.base.cache.NNAServiceStart;
import nna.base.log.Log;
import nna.base.log.LogEntry;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

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
        Integer oid=getConfMetaOID(request);
        ConfMeta confMeta=CacheFactory.getConfMetaCache().get(oid).clone();
        confMeta.setOutsideReq(getReqColMap(request));
        ConfMetaSetFactory.setConfMeta(confMeta);
        initUserLog(confMeta);
        CombApp combApp=confMeta.getCombApp();
        Method dispatchMethod=combApp.getAppDispatchMethod();
        Object dispatchObject=combApp.getAppDispatchObject();
        dispatchMethod.invoke(dispatchObject,confMeta);
        write(confMeta,response);
    }

    private Integer getConfMetaOID(R request) {
        String[] rs=getPlatformEntryId(request);
        String entryOID=rs[0];
        System.out.println(entryOID);
        String sessionId=rs[1];
        if(ConfMeta.getFreeResources().contains(entryOID)){
            return Marco.FREE;
        }else{
            if(!checkSession(sessionId)){
                return Marco.LOGIN;
            }else {
                if(isUploadSource(entryOID)){
                    return CacheFactory.getEnToIdCache().get(entryOID);
                }else{
                    if(isDownLoadSource(entryOID)){
                        return CacheFactory.getEnToIdCache().get(entryOID);
                    }else{
                        return CacheFactory.getEnToIdCache().get(entryOID);
                    }
                }
            }
        }
    }

    private boolean isDownLoadSource(String entryOID) {
        return false;
    }

    private boolean isUploadSource(String entryOID) {
        return false;
    }

    private void write(ConfMeta confMeta,S response) throws IOException {
        try{
            OutputStream outputStream=getOutPutStream(response);
            Log log=confMeta.getLog();
            HashMap<String,String[]> rspMap=confMeta.getRspColumn();
            CombController combController=confMeta.getCombController();
            Method renderMethod=combController.getRenderMethod();
            Object renderObject=combController.getRenderObject();
            String renderPage=combController.getController().getRenderPage();
            String appEncode=confMeta.getCombApp().getApp().getAppEncode();
            write(log,outputStream,
                    renderPage,
                    renderMethod,
                    renderObject,
                    rspMap,
                    appEncode);
        }catch (Exception e){

        }finally {
            LogEntry.submitCloseEvent(confMeta.getLog());
            ConfMetaSetFactory.setConfMeta(null);//for prevent memory leak
        }
    }

    private void write(Log log,
                       OutputStream outputStream,
                       String renderPage,
                       Method renderMethod,
                       Object renderObject,
                       HashMap<String, String[]> rspMap,
                       String appEncode) throws IOException {
        String outStr=null;
        if(renderPage!=null&&!renderPage.trim().equals("")){
            try{
                outStr=(String) renderMethod.invoke(renderObject,rspMap);
            }catch (Throwable throwable){
                throwable.printStackTrace();
            }
        }else{
            String[] jsons=rspMap.get("JSON");
            if(jsons!=null&&jsons.length ==1){
                String json=jsons[0];
                if(json!=null&&!json.trim().equals("")){
                    outStr=json;
                }
            }
        }
        if(outStr!=null&&!outStr.trim().equals("")){
            log.log("响应报文",Log.INFO);
            log.log(outStr, Log.INFO);
            outputStream.write(outStr.getBytes(appEncode));
        }
    }

    private static boolean checkSession(String sessionId){
        ConfSession confSession=CacheFactory.getUserSessionCache().get(sessionId);
        if(confSession==null
                ||System.currentTimeMillis()-confSession.getLastAccessd() > confSession.getCombSession().getTimedOut()
                ||!checkPriv(confSession,sessionId)){
            CacheFactory.getUserSessionCache().remove(sessionId);
            return false;
        }else{
            ConfMetaSetFactory.getConfMeta().setConfSession(confSession);
            return true;
        }
    }

    private static boolean checkPriv(ConfSession confSession,String resourceId){
        if(confSession.getCombUser().getResoruces().entrySet().contains(resourceId)){
            return true;
        }
        return false;
    }

    private static void initUserLog(ConfMeta confMeta){
        String serviceName=confMeta.getCombService().getService().getServiceName();
        ConfSession confSession=confMeta.getConfSession();
        Integer userId=confSession==null?-1:confSession.getCombUser().getPlatformUser().getUserId();
        CombLog combLog=confMeta.getCombLog();
        String logDir=
                combLog.getLogDir()+yyyyMMdd.format(System.currentTimeMillis())
                        +"/"+ String.valueOf(userId==-1?"nosession":userId)
                        +"/"+confMeta.getCombApp().getApp().getAppEn()
                        +"/"+ confMeta.getPlatformEntry().getEntryCode()
                        +"/"+ serviceName+"-"+HHmmssSSS.format(System.currentTimeMillis())+"-";
        Log log=getLog(confMeta,logDir,combLog,serviceName);
        confMeta.setLog(log);
    }

    private static Log getLog(ConfMeta confMeta, String logDir, CombLog combLog, String serviceName) {
        PlatformLog platformLog=combLog.getPlatformLog();
        return LogEntry.submitInitEvent(
                logDir,
                combLog.getNextLogSeq(),
                serviceName+".log",
                confMeta.getLogLevel(),
                platformLog.getLogBufferThreshold(),
                platformLog.getLogCloseTimedout(),
                platformLog.getLogEncode()
        );
    }

}
