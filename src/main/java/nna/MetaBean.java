package nna;

import nna.base.bean.Clone;
import nna.base.bean.dbbean.*;
import nna.base.db.DBCon;
import nna.base.log.Log;
import nna.base.util.List;
import nna.base.util.orm.ObjectFactory;
import nna.base.util.view.Template;
import nna.enums.DBSQLConValType;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * GlobalRequest Config Bean
 *
 * @author NNA-SHUAI
 * @create 2017-06-07 14:23
 **/

public class MetaBean extends Clone{
    //Global App Config Cache;
    private static Log pLog;
    private static HashSet<PlatformResource> freeResource=new HashSet<PlatformResource>();
    private static HashSet<String> freeCodeSet=new HashSet<String>();
    private static HashSet<PlatformUser> userSet=new HashSet<PlatformUser>();
    private static HashMap<String,PlatformRole[]> allUserRole=new HashMap<String, PlatformRole[]>();
    private static HashMap<String,PlatformResource[]> allUserResource=new HashMap<String, PlatformResource[]>();
    private static List<MetaBean> confMetaCache;
    private static List<ObjectFactory> objectFactoryCache;
    private static ConcurrentHashMap<String,Integer> srvEnNmToId=new ConcurrentHashMap<String, Integer>();

    //used in the request process
    private static ConcurrentHashMap<Long,MetaBean> metaMonitor=new ConcurrentHashMap<Long, MetaBean>();
    private static ConcurrentHashMap<String,PlatformSession> sessions=new ConcurrentHashMap<String, PlatformSession>();

    private boolean isPublic=false;
    private PlatformEntry platformEntry;
    private PlatformController platformController;
    private PlatformApp platformApp;
    private PlatformDB platformDB;
    private PlatformLog platformLog;
    private PlatformService platformService;
    private PlatformSession platformSession;

    private DBCon dbCon;
    private Method serviceMethod;
    private Object serviceObject;
    private Method renderMethod;
    private Object renderObject;
    private Template template;
    private String renderPage;//used as check write flag;

    // For DefaultTransExecutor
    private PlatformEntryTransaction[] serviceTrans;
    private ArrayList<PlatformTransaction[]> trans;
    private ArrayList<PlatformSql[]> tranPlatformSql;
    private ArrayList<String[]> SQLS;
    private ArrayList<ArrayList<DBSQLConValType[]>> dbsqlConValTypes;
    private ArrayList<ArrayList<String[]>> cons;
    private ArrayList<ArrayList<String[]>> cols;

    //use who can access this MetaBean;
    private HashMap<Integer,PlatformRole[]> userRole;
    private HashMap<String,PlatformResource[]> userResource;
    private PlatformUser currentUser;
    private String currentSessionId;

    private PlatformColumn[] reqColConfig;
    private PlatformColumn[] rspColConfig;
    private Map<String,String[]> outColumns;
    private HashMap<String,String[]> innerColumns;
    private HashMap<String,Object> temp;

    private ArrayList<Connection> conStack;
    private ArrayList<PreparedStatement[]> pstStack;
    private ArrayList<PlatformEntryTransaction> tranStack;
    private ArrayList<PlatformSql[]> platformSqlStack;
    private ArrayList<String[]> SQLStack;

    private Integer currentServTranIndex;
    private PlatformSql[] currentPlatformSqls;
    private String[] currentSQLS;
    private String currentSQL;
    private PreparedStatement[] currentPsts;
    private Connection currentCon;
    private Log log;
    private int logLevel;
    private boolean isLogEncrypt;//encrypt
    private int logTimes;

    public static HashMap<String, PlatformRole[]> getAllUserRole() {
        return allUserRole;
    }

    public static void setAllUserRole(HashMap<String, PlatformRole[]> allUserRole) {
        MetaBean.allUserRole = allUserRole;
    }

    public static HashMap<String, PlatformResource[]> getAllUserResource() {
        return allUserResource;
    }

    public static void setAllUserResource(HashMap<String, PlatformResource[]> allUserResource) {
        MetaBean.allUserResource = allUserResource;
    }

    public static List<ObjectFactory> getObjectFactoryCache() {
        return objectFactoryCache;
    }

    public static void setObjectFactoryCache(List<ObjectFactory> objectFactoryCache) {
        MetaBean.objectFactoryCache = objectFactoryCache;
    }

    public static List<MetaBean> getConfMetaCache() {
        return confMetaCache;
    }

    public static void setConfMetaCache(List<MetaBean> confMetaCache) {
        MetaBean.confMetaCache = confMetaCache;
    }

    public static ConcurrentHashMap<String, Integer> getSrvEnNmToId() {
        return srvEnNmToId;
    }

    public static void setSrvEnNmToId(ConcurrentHashMap<String, Integer> srvEnNmToId) {
        MetaBean.srvEnNmToId = srvEnNmToId;
    }

    public static ConcurrentHashMap<String, PlatformSession> getSessions() {
        return sessions;
    }

    public static void setSessions(ConcurrentHashMap<String, PlatformSession> sessions) {
        MetaBean.sessions = sessions;
    }

    public static Log getpLog() {
        return pLog;
    }

    public static void setpLog(Log pLog) {
        MetaBean.pLog = pLog;
    }

    public static HashSet<PlatformUser> getUserSet() {
        return userSet;
    }

    public static void setUserSet(HashSet<PlatformUser> userSet) {
        MetaBean.userSet = userSet;
    }

    public static HashSet<String> getFreeCodeSet() {
        return freeCodeSet;
    }

    public static void setFreeCodeSet(HashSet<String> freeCodeSet) {
        MetaBean.freeCodeSet = freeCodeSet;
    }

    @Override
    public MetaBean clone(){
        MetaBean metaBean=(MetaBean) super.clone();
        metaBean.setOutColumns(null);

        //for monitor
        Thread thread=Thread.currentThread();
        Long threadId=thread.getId();
        metaMonitor.put(threadId,metaBean);
        return metaBean;
    }

    public static ConcurrentHashMap<Long, MetaBean> getMetaMonitor() {
        return metaMonitor;
    }

    public static void setMetaMonitor(ConcurrentHashMap<Long, MetaBean> metaMonitor) {
        MetaBean.metaMonitor = metaMonitor;
    }

    public static HashSet<PlatformResource> getFreeResource() {
        return freeResource;
    }

    public static void setFreeResource(HashSet<PlatformResource> freeResource) {
        MetaBean.freeResource = freeResource;
    }

    public PlatformColumn[] getReqColConfig() {
        return reqColConfig;
    }

    public void setReqColConfig(PlatformColumn[] reqColConfig) {
        this.reqColConfig = reqColConfig;
    }

    public PlatformColumn[] getRspColConfig() {
        return rspColConfig;
    }

    public void setRspColConfig(PlatformColumn[] rspColConfig) {
        this.rspColConfig = rspColConfig;
    }

    public HashMap<String, Object> getTemp() {
        return temp;
    }

    public void setTemp(HashMap<String, Object> temp) {
        this.temp = temp;
    }

    public void setPstStack(ArrayList<PreparedStatement[]> pstStack) {
        this.pstStack = pstStack;
    }

    public void setConStack(ArrayList<Connection> conStack) {
        this.conStack = conStack;
    }

    public ArrayList<Connection> getConStack() {
        return conStack;
    }

    public ArrayList<PreparedStatement[]> getPstStack() {
        return pstStack;
    }

    public PreparedStatement[] getCurrentPsts() {
        return currentPsts;
    }

    public void setCurrentPsts(PreparedStatement[] currentPsts) {
        this.currentPsts = currentPsts;
    }

    public Connection getCurrentCon() {
        return currentCon;
    }

    public void setCurrentCon(Connection currentCon) {
        this.currentCon = currentCon;
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

    public PlatformEntry getPlatformEntry() {
        return platformEntry;
    }

    public void setPlatformEntry(PlatformEntry platformEntry) {
        this.platformEntry = platformEntry;
    }

    public PlatformController getPlatformController() {
        return platformController;
    }

    public void setPlatformController(PlatformController platformController) {
        this.platformController = platformController;
    }

    public PlatformApp getPlatformApp() {
        return platformApp;
    }

    public void setPlatformApp(PlatformApp platformApp) {
        this.platformApp = platformApp;
    }

    public PlatformDB getPlatformDB() {
        return platformDB;
    }

    public void setPlatformDB(PlatformDB platformDB) {
        this.platformDB = platformDB;
    }

    public PlatformService getPlatformService() {
        return platformService;
    }

    public void setPlatformService(PlatformService platformService) {
        this.platformService = platformService;
    }

    public PlatformSession getPlatformSession() {
        return platformSession;
    }

    public void setPlatformSession(PlatformSession platformSession) {
        this.platformSession = platformSession;
    }

    public DBCon getDbCon() {
        return dbCon;
    }

    public void setDbCon(DBCon dbCon) {
        this.dbCon = dbCon;
    }

    public Method getServiceMethod() {
        return serviceMethod;
    }

    public void setServiceMethod(Method serviceMethod) {
        this.serviceMethod = serviceMethod;
    }

    public Object getServiceObject() {
        return serviceObject;
    }

    public void setServiceObject(Object serviceObject) {
        this.serviceObject = serviceObject;
    }

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

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public PlatformEntryTransaction[] getServiceTrans() {
        return serviceTrans;
    }

    public void setServiceTrans(PlatformEntryTransaction[] serviceTrans) {
        this.serviceTrans = serviceTrans;
    }

    public ArrayList<PlatformTransaction[]> getTrans() {
        return trans;
    }

    public void setTrans(ArrayList<PlatformTransaction[]> trans) {
        this.trans = trans;
    }

    public ArrayList<PlatformSql[]> getTranPlatformSql() {
        return tranPlatformSql;
    }

    public void setTranPlatformSql(ArrayList<PlatformSql[]> tranPlatformSql) {
        this.tranPlatformSql = tranPlatformSql;
    }

    public ArrayList<String[]> getSQLS() {
        return SQLS;
    }

    public void setSQLS(ArrayList<String[]> SQLS) {
        this.SQLS = SQLS;
    }

    public ArrayList<ArrayList<DBSQLConValType[]>> getDbsqlConValTypes() {
        return dbsqlConValTypes;
    }

    public void setDbsqlConValTypes(ArrayList<ArrayList<DBSQLConValType[]>> dbsqlConValTypes) {
        this.dbsqlConValTypes = dbsqlConValTypes;
    }

    public ArrayList<ArrayList<String[]>> getCons() {
        return cons;
    }

    public void setCons(ArrayList<ArrayList<String[]>> cons) {
        this.cons = cons;
    }

    public ArrayList<ArrayList<String[]>> getCols() {
        return cols;
    }

    public void setCols(ArrayList<ArrayList<String[]>> cols) {
        this.cols = cols;
    }

    public HashMap<Integer, PlatformRole[]> getUserRole() {
        return userRole;
    }

    public void setUserRole(HashMap<Integer, PlatformRole[]> userRole) {
        this.userRole = userRole;
    }

    public HashMap<String, PlatformResource[]> getUserResource() {
        return userResource;
    }

    public void setUserResource(HashMap<String, PlatformResource[]> userResource) {
        this.userResource = userResource;
    }

    public String getCurrentSessionId() {
        return currentSessionId;
    }

    public void setCurrentSessionId(String currentSessionId) {
        this.currentSessionId = currentSessionId;
    }

    public PlatformUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(PlatformUser currentUser) {
        this.currentUser = currentUser;
    }

    public String getRenderPage() {
        return renderPage;
    }

    public void setRenderPage(String renderPage) {
        this.renderPage = renderPage;
    }

    public ArrayList<PlatformEntryTransaction> getTranStack() {
        return tranStack;
    }

    public void setTranStack(ArrayList<PlatformEntryTransaction> tranStack) {
        this.tranStack = tranStack;
    }

    public PlatformLog getPlatformLog() {
        return platformLog;
    }

    public void setPlatformLog(PlatformLog platformLog) {
        this.platformLog = platformLog;
    }

    public PlatformSql[] getCurrentPlatformSqls() {
        return currentPlatformSqls;
    }

    public void setCurrentPlatformSqls(PlatformSql[] currentPlatformSqls) {
        this.currentPlatformSqls = currentPlatformSqls;
    }

    public String[] getCurrentSQLS() {
        return currentSQLS;
    }

    public void setCurrentSQLS(String[] currentSQLS) {
        this.currentSQLS = currentSQLS;
    }

    public String getCurrentSQL() {
        return currentSQL;
    }

    public void setCurrentSQL(String currentSQL) {
        this.currentSQL = currentSQL;
    }

    public ArrayList<PlatformSql[]> getPlatformSqlStack() {
        return platformSqlStack;
    }

    public void setPlatformSqlStack(ArrayList<PlatformSql[]> platformSqlStack) {
        this.platformSqlStack = platformSqlStack;
    }

    public ArrayList<String[]> getSQLStack() {
        return SQLStack;
    }

    public void setSQLStack(ArrayList<String[]> SQLStack) {
        this.SQLStack = SQLStack;
    }

    public Integer getCurrentServTranIndex() {
        return currentServTranIndex;
    }

    public void setCurrentServTranIndex(Integer currentServTranIndex) {
        this.currentServTranIndex = currentServTranIndex;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public Map<String, String[]> getOutColumns() {
        return outColumns;
    }

    public void setOutColumns(Map<String, String[]> outColumns) {
        this.outColumns = outColumns;
    }

    public HashMap<String, String[]> getInnerColumns() {
        return innerColumns;
    }

    public void setInnerColumns(HashMap<String, String[]> innerColumns) {
        this.innerColumns = innerColumns;
    }

    public int getLogTimes() {
        return logTimes;
    }

    public void setLogTimes(int logTimes) {
        this.logTimes = logTimes;
    }
}
