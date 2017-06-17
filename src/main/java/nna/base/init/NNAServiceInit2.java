package nna.base.init;

import nna.Marco;
import nna.MetaBean;
import nna.base.bean.dbbean.*;
import nna.base.db.DBCon;
import nna.base.db.DBMeta;
import nna.base.log.Log;
import nna.base.proxy.ProxyFactory;
import nna.base.util.List;
import nna.enums.DBSQLConValType;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author NNA-SHUAI
 * @create 2017-05-24 20:21
 **/

public class NNAServiceInit2 {
    public static void main(String[] args){
        System.out.println(Log.yyMMdd.format(System.currentTimeMillis()));
    }

    private static HashMap<Integer,DBCon> dbConMap=new HashMap<Integer, DBCon>();
    public void build() throws IllegalAccessException, InvocationTargetException, InstantiationException, SQLException, NoSuchMethodException, ClassNotFoundException, IOException {
        buildStaticAttribute();
        buildNonStaticAttribute();
        System.out.println("init success!");
    }

    private void buildNonStaticAttribute() throws SQLException, ClassNotFoundException {
        List<MetaBean> list=MetaBean.getConfMetaCache();
        int count=list.getCapacity();
        MetaBean temp;
        PlatformEntry entry;
        for(int index=0;index < count;index++){
            temp=list.get(index);
            if(temp!=null){
                entry=temp.getPlatformEntry();
                buildMetaBean(temp,entry);
            }
        }
    }

    private void buildMetaBean(MetaBean temp,PlatformEntry entry) throws SQLException, ClassNotFoundException {
        temp.setLogTimes(entry.getEntryLogTimes());
        temp.setPlatformEntry(entry);
        temp.setPublic(entry.getEntryFree());

        Integer controllerId=entry.getEntryControllerId();
        if(controllerId!=null){
            PlatformController platformController=NNAServiceInit1.controllerMap.get(controllerId);
            temp.setPlatformController(platformController);
            Object[] os=ProxyFactory.getProxy(NNAServiceInit1.proxyServiceMap,platformController.getRenderClass(),platformController.getRenderMethod());
            if(os!=null){
                temp.setRenderObject(os[0]);
                temp.setRenderMethod((Method)os[1]);
            }
        }

        String serviceId=entry.getEntryServiceId();
        if(serviceId!=null){
            PlatformService platformService=NNAServiceInit1.platformServiceMap.get(serviceId);
            temp.setPlatformService(platformService);
            Object[] os=ProxyFactory.getProxy(NNAServiceInit1.proxyServiceMap,platformService.getServiceClass(),platformService.getServiceMethodType().toString());
            if(os!=null){
                temp.setServiceObject(os[0]);
                temp.setServiceMethod((Method)os[1]);
            }
        }

        Integer appId=entry.getEntryAppId();
        if(appId!=null){
            PlatformApp platformApp=NNAServiceInit1.appMap.get(appId);
            temp.setPlatformApp(platformApp);
        }

        Integer dbId=entry.getEntryDBId();
        if(dbId!=null){
            PlatformDB platformDB=NNAServiceInit1.platformDBMap.get(dbId);
            temp.setPlatformDB(platformDB);
            PlatformLog dbLog=NNAServiceInit1.platformLogMap.get(Integer.valueOf(platformDB.getDbLogId()));
            DBCon dBcon=getDBCon(platformDB,dbLog);
            temp.setDbCon(dBcon);
        }

        Integer logId=entry.getEntryLogId();
        if(logId!=null){
            PlatformLog platformLog=NNAServiceInit1.platformLogMap.get(logId);
            temp.setPlatformLog(platformLog);
        }

        String reqId=entry.getEntryReqId();
        ArrayList<PlatformColumn> reqColumns=new ArrayList<PlatformColumn>();
        if(reqId!=null&&!reqId.toString().trim().equals("")){
            reqColumns=NNAServiceInit1.columnMap.get(reqId.trim());
            temp.setReqColConfig(reqColumns.toArray(new PlatformColumn[0]));
            temp.setOutColumns(new HashMap<String, String[]>(reqColumns.size()));
        }

        String rspId=entry.getEntryRspId();
        if(rspId!=null&&!rspId.toString().trim().equals("")){
            ArrayList<PlatformColumn> rspColumns=NNAServiceInit1.columnMap.get(rspId.trim());
            temp.setRspColConfig(rspColumns.toArray(new PlatformColumn[0]));
            temp.setInnerColumns(new HashMap<String, String[]>(reqColumns.size()+rspColumns.size()));
            temp.setRspColumns(new HashMap<String, String[]>(rspColumns.size()));
        }

        Integer tempSize=entry.getEntryTempSize();
        temp.setTemp(new HashMap<String, Object>(tempSize));

        String tranName=entry.getEntryTransactions();
        if(tranName!=null&&!tranName.toString().trim().equals("")){
            ArrayList<PlatformEntryTransaction> trans=NNAServiceInit1.serviceTranMap.get(tranName.trim());
            PlatformEntryTransaction[] tranList=trans.toArray(new PlatformEntryTransaction[0]);
            temp.setServiceTrans(tranList);
            buildTrans(temp,tranList);
        }
    }

    private void buildTrans(MetaBean temp, PlatformEntryTransaction[] tranList) throws SQLException {
        String tranNm;
        String sqlId;
        ArrayList<PlatformTransaction> trans;
        PlatformSql platformSql;
        String SQL;
        ArrayList<PlatformTransaction[]> transLists=temp.getTrans();
        ArrayList<PlatformSql[]> tranPlatformSql=temp.getTranPlatformSql();
        ArrayList<String[]> SQLS=temp.getSQLS();
        ArrayList<ArrayList<DBSQLConValType[]>> dbsqlConValTypes=temp.getDbsqlConValTypes();
        ArrayList<ArrayList<String[]>> cons=temp.getCons();
        ArrayList<ArrayList<String[]>> cols=temp.getCols();
        for(PlatformEntryTransaction platformEntryTransaction:tranList){
            tranNm=platformEntryTransaction.getTransactionName();
            trans=NNAServiceInit1.tranMap.get(tranNm);
            ArrayList<PlatformTransaction> transList=new ArrayList<PlatformTransaction>();
            ArrayList<PlatformSql> platformSqlList=new ArrayList<PlatformSql>();
            ArrayList<String> sqlList=new ArrayList<String>();
            ArrayList<DBSQLConValType[]> valTypes=new ArrayList<DBSQLConValType[]>();
            ArrayList<String[]> conList=new ArrayList<String[]>();
            ArrayList<String[]> colList=new ArrayList<String[]>();
            String conStr;
            String colStr;
            String conTypeStr;
            for(PlatformTransaction platformTransaction:trans){
                transList.add(platformTransaction);
                sqlId=platformTransaction.getSqlId();
                platformSql=NNAServiceInit1.platformSQLMap.get(sqlId);
                SQL=Util.buildSQL(platformSql);
                sqlList.add(SQL);
                conStr=platformSql.getDbCondition();
                if(conStr!=null&&!conStr.toString().trim().equals("")){
                    conList.add(conStr.split("[,]"));
                }
                colStr=platformSql.getDbColumn();
                if(colStr!=null&&!colStr.trim().equals("")){
                    colList.add(colStr.split("[,]"));
                }
                conTypeStr=platformSql.getAppConditionType();
                if(conTypeStr!=null&&!conTypeStr.trim().equals("")){
                    String[] typeStrs=conTypeStr.split("[,]");
                    DBSQLConValType[] types=getDBConTypes(typeStrs);
                    valTypes.add(types);
                }
            }
            transLists.add(transList.toArray(new PlatformTransaction[0]));
            tranPlatformSql.add(platformSqlList.toArray(new PlatformSql[0]));
            SQLS.add(sqlList.toArray(new String[0]));
            dbsqlConValTypes.add(valTypes);
            cons.add(conList);
            cols.add(colList);
        }
    }

    private DBSQLConValType[] getDBConTypes(String[] typeStrs) {
        int count=typeStrs.length;
        DBSQLConValType[] types=new DBSQLConValType[count];
        for(int index=0;index<count;index++){
            types[index]=DBSQLConValType.valueOf(typeStrs[index]);
        }
        return types;
    }

    private DBCon getDBCon(PlatformDB platformDB,PlatformLog dbLog) throws SQLException, ClassNotFoundException {
        DBCon dbCon=dbConMap.get(Integer.valueOf(platformDB.getDbId()));
        if(dbCon!=null){
            return dbCon;
        }
        dbCon=new DBCon(new DBMeta(true,
                platformDB.getDbUrl(),
                platformDB.getDbDriver(),
                platformDB.getDbAccount(),
                platformDB.getDbPassword(),
                platformDB.getDbPoolsCount(),
                platformDB.getDbPoolsCount(),
                platformDB.getDbPoolCount(),
                Long.valueOf(platformDB.getDbHeartbeatTest()),
                platformDB.getDbFailTrytime()),Log.getLog(
                dbLog.getLogDir(),
                "db",
                dbLog.getLogLevel(),
                dbLog.getLogBufferThreshold(),
                dbLog.getLogCloseTimedout(),
                dbLog.getLogEncode(),4000
        ));
        dbConMap.put(Integer.valueOf(platformDB.getDbId()),dbCon);
        return dbCon;
    }

    public void buildStaticAttribute(){
        buildPLog();
        buildFreeResource();
        buildPlatformUser();
        buildPlatformUserRole();
        buildPlatformUserResource();
        buildMetaBeanList();
        buildPlatformProtocols();
    }

    private void buildPlatformProtocols() {
        MetaBean.getProtocols().putAll(NNAServiceInit1.platformProtocols);
    }

    private void buildPlatformUserResource() {
        Iterator<Map.Entry<String,PlatformRole[]>> iterator=MetaBean.getAllUserRole().entrySet().iterator();
        Map.Entry<String,PlatformRole[]> entry;
        String userId;
        PlatformRole[] roles;
        HashSet<String> resourceSet=new HashSet<String>();
        ArrayList<PlatformResource> resources=new ArrayList<PlatformResource>();
        while(iterator.hasNext()){
            entry=iterator.next();
            userId=entry.getKey();
            roles=entry.getValue();
            for(PlatformRole platformRole:roles){//获得用户所有角色
                //获得每个角色拥有的访问资源集合
                ArrayList<PlatformRoleResource> temp=NNAServiceInit1.roleResourceMap.get(Integer.valueOf(platformRole.getRoleId()));
                for(PlatformRoleResource platformRoleResource:temp){
                    //将资源纳入用户map中
                    if(!resourceSet.contains(Integer.valueOf(platformRoleResource.getResourceId()))){
                        resources.add(NNAServiceInit1.platformResourceMap.get(Integer.valueOf(platformRoleResource.getResourceId())));
                    }
                }
            }
            MetaBean.getAllUserResource().put(userId,resources.toArray(new PlatformResource[0]));
            resources.clear();
            resourceSet.clear();
        }
    }

    private void buildMetaBeanList() {
        MetaBean.setConfMetaCache(new List<MetaBean>(NNAServiceInit1.platformEntryMap.size()));
        Iterator<Map.Entry<Integer,PlatformEntry>> iterator=NNAServiceInit1.platformEntryMap.entrySet().iterator();
        List<MetaBean> list=MetaBean.getConfMetaCache();
        ConcurrentHashMap<String,Integer> map=MetaBean.getSrvEnNmToId();
        PlatformEntry platformEntry;
        while(iterator.hasNext()){
            Map.Entry<Integer,PlatformEntry> entry=iterator.next();
            MetaBean metaBean=new MetaBean();
            platformEntry=entry.getValue();
            metaBean.setPlatformEntry(platformEntry);
            list.insert(metaBean,100);
            map.put(platformEntry.getEntryCode(),platformEntry.getEntryControllerId());
        }
    }

    private void buildPlatformUserRole() {
        Iterator<Map.Entry<Integer,PlatformUser>> iterator=NNAServiceInit1.platformUserMap.entrySet().iterator();
        HashMap<Integer,ArrayList<PlatformUserRole>> map=NNAServiceInit1.userRoleMap;
        HashMap<String,PlatformRole[]> userRoleMap=MetaBean.getAllUserRole();
        HashMap<Integer,PlatformRole> roleMap=NNAServiceInit1.platformRoleMap;
        ArrayList<PlatformRole> roles=new ArrayList<PlatformRole>();
        int count;
        int index=0;
        PlatformUserRole platformUserRole;
        PlatformRole platformRole;
        ArrayList<PlatformUserRole> urList;
        Map.Entry<Integer,PlatformUser> entry;
        Integer userId;
        while(iterator.hasNext()){
            entry=iterator.next();
            userId=entry.getKey();
            urList=map.get(userId);
            count=urList.size();
            for(;index< count;index++){
                platformUserRole=urList.get(index);
                platformRole=roleMap.get(platformUserRole.getRoleId());
                roles.add(platformRole);
            }
            userRoleMap.put(String.valueOf(userId),roles.toArray(new PlatformRole[0]));
            index=0;
            roles.clear();
        }
    }

    private void buildPlatformUser() {
        Iterator<Map.Entry<Integer,PlatformUser>> iterator=NNAServiceInit1.platformUserMap.entrySet().iterator();
        HashSet<PlatformUser> users=MetaBean.getUserSet();
        while(iterator.hasNext()){
            Map.Entry<Integer,PlatformUser> entry=iterator.next();
            users.add(entry.getValue());
        }
    }

    private void buildFreeResource() {
        HashMap<Integer,PlatformResource> resources=NNAServiceInit1.platformResourceMap;
        Iterator<Map.Entry<Integer,PlatformResource>> iterator=resources.entrySet().iterator();
        PlatformResource platformResource;
        HashSet<PlatformResource> freeResource=MetaBean.getFreeResource();
        HashSet<String> codeSet=MetaBean.getFreeCodeSet();
        while(iterator.hasNext()){
            Map.Entry<Integer,PlatformResource> entry=iterator.next();
            platformResource=entry.getValue();
            switch (platformResource.getResourceType()){
                case free:
                    freeResource.add(platformResource);
                    codeSet.add(platformResource.getResourceName());
            }
        }
    }

    private void buildPLog() {
        PlatformLog platformLog=NNAServiceInit1.platformLogMap.get(0);

        final Log pLog=Log.getLog(
                platformLog.getLogDir(),
                "nna",
                platformLog.getLogLevel(),
                platformLog.getLogBufferThreshold(),
                platformLog.getLogCloseTimedout(),
                platformLog.getLogEncode(),
                4000
        );
        MetaBean.setpLog(pLog);
        testConFrame(true);
    }

    private void testConFrame(boolean b) {
        if(b){
            for(int index = 0; index < Marco.CON_TEST_COUNT; index++){
                final Log log=Log.getLog(
                  "TEST_CON",
                        "TEST-CON-LOG",
                        10,
                        0,
                        1000,
                        "UTF-8",
                        Marco.CON_TEST_COUNT
                );
                new Thread(new Runnable() {
                    public void run() {
                        int count=Marco.CON_TEST_COUNT-2;
                        for(int index = 1; index <= count; index++){
                            log.log(""+index,10);
                        }
                        log.close();
                    }
                }).start();
            }
        }
    }
}
