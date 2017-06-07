package nna.base.bean.confbean;

import nna.base.bean.Clone;
import nna.base.bean.ReturnMessage;
import nna.base.bean.combbean.*;
import nna.base.bean.dbbean.PlatformColumn;
import nna.base.bean.dbbean.PlatformEntry;
import nna.base.bean.dbbean.PlatformService;
import nna.base.log.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Global Request Meta-Bean
 *
 * @author NNA-SHUAI
 * @create 2017-05-16 11:00
 **/

public class ConfMeta extends Clone{
    private static final long serialVersionUID = -1L;
    private static HashSet<String> freeResources;

    private static ConcurrentHashMap<Long,ConfMeta> confMetaMonitor=new ConcurrentHashMap<Long, ConfMeta>();

    //be live in the whole application life
    private transient static Log platformLog;

    private ReturnMessage returnMessage;

    private PlatformEntry platformEntry;
    private CombController combController;
    private CombApp combApp;
    private CombDB combDB;
    private CombLog combLog;
    private CombService combService;
    private HashMap<String,CombTransaction> combTransactionMap;
    private CombTransaction[] combTransactions;
    private ConfSession confSession;//代表当前会话信息

    private HashMap<Integer,CombUser> combUserMap;

    private Map<String,String[]> outsideReq;//for request cols parse from request xml or http Servlet;

    private PlatformColumn[] request;
    private PlatformColumn[] response;
    private HashMap<String,String[]> rspColumn;
    private HashMap<String,String[]> reqColumn;
    private HashMap<String,Object> temp;

    //be live in the request logic process of in time
    private transient ArrayList<CombTransaction> tranStack;
    private transient ArrayList<PreparedStatement[]> pstStack;
    private transient ArrayList<Connection> conStack;
    private transient PreparedStatement[] currentPsts;
    private transient CombTransaction currentCombTransaction;
    private transient Connection currentConnection;

    private Log log;
    private int logLevel;
    private boolean isLogEncrypt;//encrypt

    public ConfMeta(){
        tranStack=new ArrayList<CombTransaction>();
        pstStack=new ArrayList<PreparedStatement[]>();
        conStack=new ArrayList<Connection>();
    }

    public static HashSet<String> getFreeResources() {
        return freeResources;
    }

    public static void setFreeResources(HashSet<String> freeResources) {
        ConfMeta.freeResources = freeResources;
    }

    @Override
    public ConfMeta clone(){
        ConfMeta confMeta=(ConfMeta) super.clone();
        PlatformService platformService=combService.getService();
        confMeta.setOutsideReq(null);
        confMeta.setRspColumn(new HashMap<String, String[]>(request==null?0:request.length));
        confMeta.setReqColumn(new HashMap<String, String[]>(response==null?0:response.length));
        confMeta.setTemp(new HashMap<String, Object>(platformService.getServiceTempsize()));

        confMeta.setTranStack(new ArrayList<CombTransaction>(tranStack.size()));
        confMeta.setPstStack(new ArrayList<PreparedStatement[]>(pstStack.size()));
        confMeta.setConStack(new ArrayList<Connection>(conStack.size()));
        confMeta.setCurrentPsts(null);
        confMeta.setCurrentPsts(null);
        confMeta.setCurrentConnection(null);
        confMeta.setLog(null);

        Thread thread=Thread.currentThread();
        Long threadId=thread.getId();
        confMetaMonitor.put(threadId,confMeta);
        return confMeta;
    }

    public static Log getPlatformLog() {
        return platformLog;
    }

    public static void setPlatformLog(Log platformLog) {
        ConfMeta.platformLog = platformLog;
    }

    public CombApp getCombApp() {
        return combApp;
    }

    public void setCombApp(CombApp combApp) {
        this.combApp = combApp;
    }

    public CombController getCombController() {
        return combController;
    }

    public void setCombController(CombController combController) {
        this.combController = combController;
    }

    public CombService getCombService() {
        return combService;
    }

    public void setCombService(CombService combService) {
        this.combService = combService;
    }

    public HashMap<String, CombTransaction> getCombTransactionMap() {
        return combTransactionMap;
    }

    public void setCombTransactionMap(HashMap<String, CombTransaction> combTransactionMap) {
        this.combTransactionMap = combTransactionMap;
    }

    public CombLog getCombLog() {
        return combLog;
    }

    public void setCombLog(CombLog combLog) {
        this.combLog = combLog;
    }

    public HashMap<Integer, CombUser> getCombUserMap() {
        return combUserMap;
    }

    public void setCombUserMap(HashMap<Integer, CombUser> combUserMap) {
        this.combUserMap = combUserMap;
    }

    public CombDB getCombDB() {
        return combDB;
    }

    public void setCombDB(CombDB combDB) {
        this.combDB = combDB;
    }

    public ArrayList<CombTransaction> getTranStack() {
        return tranStack;
    }

    public void setTranStack(ArrayList<CombTransaction> tranStack) {
        this.tranStack = tranStack;
    }

    public CombTransaction getCurrentCombTransaction() {
        return currentCombTransaction;
    }

    public void setCurrentCombTransaction(CombTransaction currentCombTransaction) {
        this.currentCombTransaction = currentCombTransaction;
    }

    public Map<String, String[]> getOutsideReq() {
        return outsideReq;
    }

    public void setOutsideReq(Map<String, String[]> outsideReq) {
        this.outsideReq = outsideReq;
    }

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public PreparedStatement[] getCurrentPsts() {
        return currentPsts;
    }

    public void setCurrentPsts(PreparedStatement[] currentPsts) {
        this.currentPsts = currentPsts;
    }

    public Connection getCurrentConnection() {
        return currentConnection;
    }

    public void setCurrentConnection(Connection currentConnection) {
        this.currentConnection = currentConnection;
    }

    public ArrayList<PreparedStatement[]> getPstStack() {
        return pstStack;
    }

    public void setPstStack(ArrayList<PreparedStatement[]> pstStack) {
        this.pstStack = pstStack;
    }

    public ArrayList<Connection> getConStack() {
        return conStack;
    }

    public void setConStack(ArrayList<Connection> conStack) {
        this.conStack = conStack;
    }

    public HashMap<String, String[]> getRspColumn() {
        return rspColumn;
    }

    public void setRspColumn(HashMap<String, String[]> rspColumn) {
        this.rspColumn = rspColumn;
    }

    public HashMap<String, String[]> getReqColumn() {
        return reqColumn;
    }

    public void setReqColumn(HashMap<String, String[]> reqColumn) {
        this.reqColumn = reqColumn;
    }

    public HashMap<String, Object> getTemp() {
        return temp;
    }

    public void setTemp(HashMap<String, Object> temp) {
        this.temp = temp;
    }

    public PlatformColumn[] getRequest() {
        return request;
    }

    public void setRequest(PlatformColumn[] request) {
        this.request = request;
    }

    public PlatformColumn[] getResponse() {
        return response;
    }

    public void setResponse(PlatformColumn[] response) {
        this.response = response;
    }

    public PlatformEntry getPlatformEntry() {
        return platformEntry;
    }

    public void setPlatformEntry(PlatformEntry platformEntry) {
        this.platformEntry = platformEntry;
    }

    public int getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    public boolean isLogEncrypt() {
        return isLogEncrypt;
    }

    public void setLogEncrypt(boolean logEncrypt) {
        isLogEncrypt = logEncrypt;
    }



    public String toString(){
        StringBuilder meta=new StringBuilder("");
        meta.append("序列ID："+serialVersionUID+"\r\n");
        meta.append(platformEntry.getEntryAppId()+"\r\n");
        meta.append(platformEntry.getEntryUri()+"\r\n");
        meta.append(platformEntry.getEntryCode()+"\r\n");
        meta.append(platformEntry.getEntryControllerId()+"\r\n");
        meta.append(platformEntry.getEntryDesc()+"\r\n");
        meta.append("拦截器ID："+combController.getController().getId()+"\r\n");
        meta.append(combApp.getApp().getAppId()+"\r\n");
        meta.append(combApp.getAppDispatchMethod().getName()+"\r\n");
        meta.append(combApp.getAppDispatchObject().getClass().getName()+"\r\n");
        meta.append(combDB.getPlatformDB().getDbId()+"\r\n");
        try {
            meta.append(combDB.getDbCon().getCon()+"\r\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        meta.append(combLog.getPlatformLog().getLogId()+"\r\n");
        meta.append(combService.getService().getServiceName()+"\r\n");
        meta.append(combService.getServiceObject().getClass().getName()+"\r\n");
        meta.append(combService.getServiceMethod().getName()+"\r\n");
        return meta.toString();
    }

    public ConfSession getConfSession() {
        return confSession;
    }

    public void setConfSession(ConfSession confSession) {
        this.confSession = confSession;
    }

    public ReturnMessage getReturnMessage() {
        return returnMessage;
    }

    public void setReturnMessage(ReturnMessage returnMessage) {
        this.returnMessage = returnMessage;
    }

    public CombTransaction[] getCombTransactions() {
        return combTransactions;
    }

    public void setCombTransactions(CombTransaction[] combTransactions) {
        this.combTransactions = combTransactions;
    }

    public static ConcurrentHashMap<Long, ConfMeta> getConfMetaMonitor() {
        return confMetaMonitor;
    }

    public static void setConfMetaMonitor(ConcurrentHashMap<Long, ConfMeta> confMetaMonitor) {
        ConfMeta.confMetaMonitor = confMetaMonitor;
    }
}
