package nna.base.init;

import nna.Marco;
import nna.MetaBean;
import nna.base.bean.dbbean.*;
import nna.base.db.DBCon;
import nna.base.db.DBMeta;
import nna.base.log.Log;
import nna.base.proxy.ProxyFactory;
import nna.base.server.ClientConfig;
import nna.base.server.EndConfig;
import nna.base.server.NIOEntry;
import nna.base.server.ServerConfig;
import nna.base.util.List;
import nna.base.util.LogUtil;
import nna.enums.DBSQLConValType;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
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
    public void build() throws Exception {
        buildStaticAttribute();
        buildNonStaticAttribute();
        System.out.println("init success!");
    }

    private void buildNonStaticAttribute() throws Exception {
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

    private void buildMetaBean(MetaBean temp,PlatformEntry entry) throws Exception {
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

        String tranName=entry.getEntryTranId();
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

    private DBCon getDBCon(PlatformDB platformDB,PlatformLog dbLog) throws Exception {
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
                platformDB.getDbFailTrytime()), LogUtil.getLog(
                dbLog.getLogDir(),
                "db",
                dbLog.getLogLevel(),
                dbLog.getLogBufferThreshold(),
                dbLog.getLogCloseTimedout(),
                dbLog.getLogEncode()
        ));
        dbConMap.put(Integer.valueOf(platformDB.getDbId()),dbCon);
        return dbCon;
    }

    public void buildStaticAttribute() throws Exception {
        buildPLog();
        buildFreeResource();
        buildPlatformUser();
        buildPlatformUserRole();
        buildPlatformUserResource();
        buildMetaBeanList();
        buildPlatformProtocols();
        buildNIOServers();
    }

    private void buildNIOServers() throws NoSuchMethodException {
        Iterator<Map.Entry<Integer,PlatformProtocol>> iterator=NNAServiceInit1.platformProtocols.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<Integer,PlatformProtocol> entry=iterator.next();
            PlatformProtocol platformProtocol=entry.getValue();
            EndConfig endConfig;
            if(platformProtocol.isServer()){
                endConfig=new ServerConfig();
            }else{
                endConfig=new ClientConfig();
            }
            endConfig.setIp(platformProtocol.getProtocolIp());
            endConfig.setPort(platformProtocol.getProtocolPort());
            endConfig.setTimedOut(platformProtocol.getTimedOut()*1000);
            setOpVals(endConfig,platformProtocol);
            Object[] os=new Object[2];
            switch (platformProtocol.getProtocolType()){
                case XML:
                    os=Util.getProtocolProcessConfig(Marco.XML_PROTOCOL);
                    break;
                case HTTP:
                    os=Util.getProtocolProcessConfig(Marco.HTTP_PROTOCOL);
                    break;
            }
            if(platformProtocol.isServer()){
                ((ServerConfig)endConfig).setBackLog(platformProtocol.getBacklog());
                try {
                    NIOEntry nioEntry=new NIOEntry((ServerConfig) endConfig,os[0],(Method) os[1]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    NIOEntry nioEntry=new NIOEntry((ClientConfig) endConfig,os[0],(Method) os[1]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setOpVals(EndConfig endConfig,PlatformProtocol platformProtocol){
        SocketOption[] options=new SocketOption[11];
        Object[] opVlaues=new Object[11];
        options[0]=StandardSocketOptions.SO_BROADCAST;
        opVlaues[0]=platformProtocol.getSoBroadcast();
        options[1]=StandardSocketOptions.SO_KEEPALIVE;
        opVlaues[0]=platformProtocol.getSoKeepalive();
        options[2]=StandardSocketOptions.SO_SNDBUF;
        opVlaues[2]=platformProtocol.getSoSndbuf();
        options[3]=StandardSocketOptions.SO_RCVBUF;
        opVlaues[3]=platformProtocol.getSoRcvbuf();
        options[4]=StandardSocketOptions.SO_REUSEADDR;
        opVlaues[4]=platformProtocol.getSoReuseadr();
        options[5]=StandardSocketOptions.SO_LINGER;
        opVlaues[5]=platformProtocol.getSoLinger();
        options[6]=StandardSocketOptions.IP_TOS;
        opVlaues[6]=platformProtocol.getIpTos();
        options[7]=StandardSocketOptions.IP_MULTICAST_IF;
        opVlaues[7]=platformProtocol.getIpMulticastIf();
        options[8]=StandardSocketOptions.IP_MULTICAST_TTL;
        opVlaues[8]=platformProtocol.getIpMulticastTtl();
        options[9]=StandardSocketOptions.IP_MULTICAST_LOOP;
        opVlaues[9]=platformProtocol.getIpMulticastLoop();
        options[10]=StandardSocketOptions.TCP_NODELAY;
        opVlaues[10]=platformProtocol.getTcpNodelay();
        endConfig.setOptions(opVlaues);
        endConfig.setSocketOptions(options);
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
            if(urList==null){
                continue;
            }
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

    private void buildPLog() throws Exception {
        PlatformLog platformLog=NNAServiceInit1.platformLogMap.get(new Integer(1));
        final Log pLog=LogUtil.getLog(
                platformLog.getLogDir(),
                "nna",
                platformLog.getLogLevel(),
                platformLog.getLogBufferThreshold(),
                platformLog.getLogCloseTimedout(),
                platformLog.getLogEncode());
        MetaBean.setpLog(pLog);
    }
}
