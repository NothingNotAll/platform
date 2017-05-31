package nna.base.bean.confbean;

import nna.base.bean.Clone;
import nna.base.bean.ReturnMessage;
import nna.base.bean.combbean.CombTransaction;
import nna.base.bean.dbbean.*;
import nna.base.db.DBCon;
import nna.base.log.Log;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 *
 * version2:simplify Global Request Meta-Bean
 * @author NNA-SHUAI
 * @create 2017-05-29 18:33
 **/

public class ConfMetaV2 extends Clone{
    private static final long serialVersionUID = -1L;
    private static HashSet<String> freeResources;
    //be live in the whole application life
    private transient static Log platformLog;

    private ReturnMessage returnMessage;
    private PlatformEntry platformEntry;
    private ConfSession confSession;//代表当前会话信息

    private PlatformApp platformApp;
    private PlatformLog tradeLog;
    private PlatformController platformController;
    private PlatformService platformService;
    private Method seviceMethod;
    private Object serviceObject;
    private HashMap<String,CombTransaction> combTransactionMap;

    private HashSet<Integer> userIdSet;
    private HashMap<Integer,PlatformResource[]> userResoruce;
    private HashMap<Integer,PlatformRole[]> userRoles;
    private Map<String,String[]> outsideReq;//for request cols parse from request xml or http Servlet;
    private PlatformColumn[] request;
    private PlatformColumn[] response;
    private HashMap<String,String[]> rspColumn;
    private HashMap<String,String[]> reqColumn;
    private HashMap<String,Object> temp;
    private transient DBCon dbCon;

    //be live in the request logic process of in time
    private transient ArrayList<CombTransaction> tranStack;
    private transient ArrayList<PreparedStatement[]> pstStack;
    private transient ArrayList<Connection> conStack;
    private transient PreparedStatement[] currentPsts;
    private transient CombTransaction currentCombTransaction;
    private transient Connection currentConnection;

    private transient Log log;
    private int logLevel;
    private boolean isLogEncrypt;//encrypt


    @Override
    public ConfMeta clone(){
        ConfMeta confMeta=(ConfMeta) super.clone();
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

        return confMeta;
    }

    public static HashSet<String> getFreeResources() {
        return freeResources;
    }

    public static void setFreeResources(HashSet<String> freeResources) {
        ConfMetaV2.freeResources = freeResources;
    }

    public static Log getPlatformLog() {
        return platformLog;
    }

    public static void setPlatformLog(Log platformLog) {
        ConfMetaV2.platformLog = platformLog;
    }

    public ReturnMessage getReturnMessage() {
        return returnMessage;
    }

    public void setReturnMessage(ReturnMessage returnMessage) {
        this.returnMessage = returnMessage;
    }

    public PlatformEntry getPlatformEntry() {
        return platformEntry;
    }

    public void setPlatformEntry(PlatformEntry platformEntry) {
        this.platformEntry = platformEntry;
    }

    public ConfSession getConfSession() {
        return confSession;
    }

    public void setConfSession(ConfSession confSession) {
        this.confSession = confSession;
    }

    public PlatformApp getPlatformApp() {
        return platformApp;
    }

    public void setPlatformApp(PlatformApp platformApp) {
        this.platformApp = platformApp;
    }

    public PlatformLog getTradeLog() {
        return tradeLog;
    }

    public void setTradeLog(PlatformLog tradeLog) {
        this.tradeLog = tradeLog;
    }

    public PlatformController getPlatformController() {
        return platformController;
    }

    public void setPlatformController(PlatformController platformController) {
        this.platformController = platformController;
    }

    public PlatformService getPlatformService() {
        return platformService;
    }

    public void setPlatformService(PlatformService platformService) {
        this.platformService = platformService;
    }

    public Method getSeviceMethod() {
        return seviceMethod;
    }

    public void setSeviceMethod(Method seviceMethod) {
        this.seviceMethod = seviceMethod;
    }

    public Object getServiceObject() {
        return serviceObject;
    }

    public void setServiceObject(Object serviceObject) {
        this.serviceObject = serviceObject;
    }

    public HashMap<String, CombTransaction> getCombTransactionMap() {
        return combTransactionMap;
    }

    public void setCombTransactionMap(HashMap<String, CombTransaction> combTransactionMap) {
        this.combTransactionMap = combTransactionMap;
    }

    public HashSet<Integer> getUserIdSet() {
        return userIdSet;
    }

    public void setUserIdSet(HashSet<Integer> userIdSet) {
        this.userIdSet = userIdSet;
    }

    public HashMap<Integer, PlatformResource[]> getUserResoruce() {
        return userResoruce;
    }

    public void setUserResoruce(HashMap<Integer, PlatformResource[]> userResoruce) {
        this.userResoruce = userResoruce;
    }

    public HashMap<Integer, PlatformRole[]> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(HashMap<Integer, PlatformRole[]> userRoles) {
        this.userRoles = userRoles;
    }

    public Map<String, String[]> getOutsideReq() {
        return outsideReq;
    }

    public void setOutsideReq(Map<String, String[]> outsideReq) {
        this.outsideReq = outsideReq;
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

    public DBCon getDbCon() {
        return dbCon;
    }

    public void setDbCon(DBCon dbCon) {
        this.dbCon = dbCon;
    }

    public ArrayList<CombTransaction> getTranStack() {
        return tranStack;
    }

    public void setTranStack(ArrayList<CombTransaction> tranStack) {
        this.tranStack = tranStack;
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

    public PreparedStatement[] getCurrentPsts() {
        return currentPsts;
    }

    public void setCurrentPsts(PreparedStatement[] currentPsts) {
        this.currentPsts = currentPsts;
    }

    public CombTransaction getCurrentCombTransaction() {
        return currentCombTransaction;
    }

    public void setCurrentCombTransaction(CombTransaction currentCombTransaction) {
        this.currentCombTransaction = currentCombTransaction;
    }

    public Connection getCurrentConnection() {
        return currentConnection;
    }

    public void setCurrentConnection(Connection currentConnection) {
        this.currentConnection = currentConnection;
    }

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
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
}
