package nna.base.cache;

import nna.Marco;
import nna.base.bean.Clone;
import nna.base.bean.combbean.*;
import nna.base.bean.dbbean.*;
import nna.base.db.DBCon;
import nna.base.db.DBMeta;
import nna.base.log.Log;
import nna.base.log.LogEntry;
import nna.base.proxy.ProxyFactory;
import nna.base.util.view.Template;
import nna.base.util.view.TemplateFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author NNA-SHUAI
 * @create 2017-05-24 20:21
 **/

public class NNAServiceInit2 {
    private PreparedStatement[] psts;
    public static HashSet<String> freeResourceSet=new HashSet<String>();
    public static HashMap<Integer,CombLog> combLogMap=new HashMap<Integer, CombLog>();
    public static HashMap<Integer,CombDB> combDBMap=new HashMap<Integer, CombDB>();
    public static HashMap<Integer,CombController> combControllerMap=new HashMap<Integer, CombController>();
    public static HashMap<String, CombService> combServiceMap=new HashMap<String, CombService>();
    public static HashMap<String,PlatformColumn[]> colMap=new HashMap<String, PlatformColumn[]>();

    public static HashMap<Integer,CombApp> combAppMap=new HashMap<Integer, CombApp>();
    public static HashMap<Integer, HashMap<Integer,CombUser>> combUserHashMap=new HashMap<Integer, HashMap<Integer, CombUser>>();
    public static HashMap<String,HashMap<String,CombTransaction>> combTransactionHashMap=new HashMap<String, HashMap<String, CombTransaction>>();
    public static Log log;


    public void build() throws IllegalAccessException, InvocationTargetException, InstantiationException, SQLException, NoSuchMethodException, ClassNotFoundException, IOException {
        this.buildCombLog(psts[10]);
        this.buildCombDB(psts[11]);
        this.buildCombController(psts[12]);
        this.buildCombService(psts[13]);
        this.buildColumn(psts[14]);
        this.buildComApp(psts[15]);
        this.buildCombUserMap();
        this.buildSerTran();
        this.buildFreeResource();
    }

    private void buildFreeResource() {
        Iterator<Map.Entry<Integer,PlatformResource>> iterator=NNAServiceInit1.resources.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<Integer,PlatformResource> entry=iterator.next();
            PlatformResource platformResource=entry.getValue();
            switch (platformResource.getResourceType()){
                case free:
                    freeResourceSet.add(platformResource.getResourceName());
                    break;
            }
        }
    }

    public NNAServiceInit2(PreparedStatement[] psts){
        this.psts=psts;
    }

    private void buildCombUserMap(){
        Iterator<Map.Entry<Integer,CombUser>> iterator=NNAServiceInit1.comUserMap.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<Integer,CombUser> entry=iterator.next();
            CombUser combUser=entry.getValue();
            Integer userId=entry.getKey();
            Iterator<Map.Entry<String,PlatformResource>> iterator1=combUser.getResoruces().entrySet().iterator();
            while(iterator1.hasNext()){
                Map.Entry<String,PlatformResource> entry1=iterator1.next();
                PlatformResource platformResource=entry1.getValue();
                if(platformResource.getResourceType().toString().equals("CONTROLLER")){
                    HashMap<Integer,CombUser> map=combUserHashMap.get(userId);
                    if(map==null){
                        HashMap<Integer,CombUser> map2=new HashMap<Integer, CombUser>();
                        map2.put(userId,combUser);
                        combUserHashMap.put(platformResource.getResourcePk(),map2);
                    }else{
                        map.put(platformResource.getResourcePk(),combUser);
                    }
                }
            }
        }
    }

    private void buildSerTran(){
        Iterator<Map.Entry<String,String[]>> iterator=NNAServiceInit1.servNmToTranNm.entrySet().iterator();
        HashMap<String,CombTransaction> map;
        while(iterator.hasNext()){
            Map.Entry<String,String[]> entry=iterator.next();
            String serviceName=entry.getKey();
            String[] tranNames=entry.getValue();
            HashMap<String,CombTransaction> cMap=new HashMap<String, CombTransaction>(tranNames.length);
            for(int index=0;index < tranNames.length;index++){
                cMap.put(tranNames[index],NNAServiceInit1.tranMap.get(tranNames[index]));
            }
            combTransactionHashMap.put(serviceName,cMap);
        }
    }

    private void buildColumn(PreparedStatement camPst) throws IllegalAccessException, InstantiationException, SQLException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        HashMap<String,Clone> map=MapTransfer.getSMap(camPst,"getColumnId", Marco.PLATFORM_COLUMN);
        final MapTransfer<PlatformColumn> mapTransfer =new MapTransfer<PlatformColumn>();
        mapTransfer.sMap=map;
        HashMap<String,PlatformColumn> cap= mapTransfer.getSMap();
        Iterator<Map.Entry<String,PlatformColumn>> iterator=cap.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,PlatformColumn> entry=iterator.next();
            PlatformColumn platformColumn=entry.getValue();
            String colId=platformColumn.getColumnId();
            PlatformColumn[] cols=colMap.get(colId);
            if(cols==null){
                colMap.put(colId,new PlatformColumn[]{platformColumn});
            }else{
                PlatformColumn[] colTemp=new PlatformColumn[cols.length+1];
                System.arraycopy(cols,0,colTemp,0,cols.length);
                colTemp[cols.length]=platformColumn;
                colMap.put(colId,colTemp);
            }
        }
    }

    private void buildCombService(PreparedStatement camPst) throws IllegalAccessException, InstantiationException, SQLException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        HashMap<String,Clone> map=MapTransfer.getSMap(camPst,"getServiceName", Marco.PLATFORM_SERVICE);
        final MapTransfer<PlatformService> mapTransfer =new MapTransfer<PlatformService>();
        mapTransfer.sMap=map;
        HashMap<String,PlatformService> cap= mapTransfer.getSMap();
        Iterator<Map.Entry<String,PlatformService>> iterator=cap.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,PlatformService> entry=iterator.next();
            PlatformService platformService=entry.getValue();
            CombService combService=new CombService();
            combService.setService(platformService);
            Object[] objects=ProxyFactory.getProxy(
                    NNAServiceInit1.proxyServiceHashMap,
                    combService.getService().getServiceClass(),
                    combService.getService().getServiceMethod());
            combService.setService(platformService);
            combService.setServiceMethod((Method) objects[1]);
            combService.setServiceObject(objects[0]);
            combServiceMap.put(entry.getKey(),combService);
        }
    }

    private void buildComApp(PreparedStatement camPst) throws SQLException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        HashMap<Integer,Clone> map=MapTransfer.getIMap(camPst,"getAppId", Marco.PLATFORM_APP);
        final MapTransfer<PlatformApp> mapTransfer =new MapTransfer<PlatformApp>();
        mapTransfer.iMap=map;
        HashMap<Integer,PlatformApp> cap= mapTransfer.getIMap();
        Iterator<Map.Entry<Integer,PlatformApp>> iterator=cap.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<Integer,PlatformApp> entry=iterator.next();
            PlatformApp platformApp=entry.getValue();
            CombApp combApp=new CombApp();
            Object[] objects=ProxyFactory.getProxy(NNAServiceInit1.proxyServiceHashMap,platformApp.getAppDispatchClass(),platformApp.getAppDispatchMethod());
            combApp.setApp(platformApp);
            combApp.setAppDispatchMethod((Method) objects[1]);
            combApp.setAppDispatchObject(objects[0]);
            combAppMap.put(entry.getKey(),combApp);
        }

    }

    private void buildCombLog(PreparedStatement camPst) throws IllegalAccessException, InstantiationException, SQLException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        HashMap<Integer,Clone> map=MapTransfer.getIMap(camPst,"getLogId", Marco.PLATFORM_LOG);
        final MapTransfer<PlatformLog> mapTransfer =new MapTransfer<PlatformLog>();
        mapTransfer.iMap=map;
        HashMap<Integer,PlatformLog> cap= mapTransfer.getIMap();
        Iterator<Map.Entry<Integer,PlatformLog>> iterator=cap.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<Integer,PlatformLog> entry=iterator.next();
            PlatformLog platformLog=entry.getValue();
            CombLog combLog=new CombLog();
            combLog.setNextLogSeq(new AtomicLong());
            combLog.setPlatformLog(platformLog);
            combLogMap.put(entry.getKey(),combLog);
        }
    }

    private void buildCombDB(PreparedStatement camPst) throws IllegalAccessException, InstantiationException, SQLException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        HashMap<Integer,Clone> map=MapTransfer.getIMap(camPst,"getDbId", Marco.PLATFORM_DB);
        final MapTransfer<PlatformDB> mapTransfer =new MapTransfer<PlatformDB>();
        mapTransfer.iMap=map;
        HashMap<Integer,PlatformDB> cap= mapTransfer.getIMap();
        Iterator<Map.Entry<Integer,PlatformDB>> iterator=cap.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<Integer,PlatformDB> entry=iterator.next();
            PlatformDB platformDB=entry.getValue();
            CombLog combLog=combLogMap.get(platformDB.getDbLogId());
            Log log= LogEntry.submitInitEvent(
                    combLog.getPlatformLog().getLogDir(),
                    combLog.getNextLogSeq(),
                    "db.log",
            combLog.getPlatformLog().getLogLevel(),
            combLog.getPlatformLog().getLogBufferThreshold(),
            combLog.getPlatformLog().getLogCloseTimedout(),
            combLog.getPlatformLog().getLogEncode()
            );
            CombDB combDB=new CombDB();
            combDB.setPlatformDB(platformDB);
            DBCon dbCon=new DBCon(new DBMeta(true,
                    platformDB.getDbUrl(),
                    platformDB.getDbDriver(),
                    platformDB.getDbAccount(),
                    platformDB.getDbPassword(),
            platformDB.getDbPoolsCount(),
            platformDB.getDbPoolCount(),
            platformDB.getDbPoolCount()*platformDB.getDbPoolsCount(),
            Long.valueOf(platformDB.getDbHeartbeatTest()),
            platformDB.getDbFailTrytime()
            ),log);
            combDB.setDbCon(dbCon);
            combDBMap.put(entry.getKey(),combDB);
        }
    }

    private void buildCombController(PreparedStatement camPst) throws IllegalAccessException, InstantiationException, SQLException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException, IOException {
        HashMap<Integer,Clone> map=MapTransfer.getIMap(camPst,"getId", Marco.PLATFORM_CONTROLLER);
        final MapTransfer<PlatformController> mapTransfer =new MapTransfer<PlatformController>();
        mapTransfer.iMap=map;
        HashMap<Integer,PlatformController> cap= mapTransfer.getIMap();
        Iterator<Map.Entry<Integer,PlatformController>> iterator=cap.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<Integer,PlatformController> entry=iterator.next();
            PlatformController platformController=entry.getValue();
            CombController combController=new CombController();
            Template template= TemplateFactory.getTemplate(platformController.getRenderPage(),"UTF-8");
            Object[] objects=ProxyFactory.getProxy(NNAServiceInit1.proxyServiceHashMap,platformController.getRenderClass(),platformController.getRenderMethod());
            ((Template)objects[0]).setStrs(template.getStrs());
            ((Template)objects[0]).setViews(template.getViews());
            combController.setController(platformController);
            combController.setRenderMethod((Method) objects[1]);
            combController.setRenderObject(objects[0]);
            combControllerMap.put(entry.getKey(),combController);
        }
    }
}
