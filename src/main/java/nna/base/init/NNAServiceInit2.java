package nna.base.init;

import nna.Marco;
import nna.base.bean.Clone;
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
    public static HashMap<Integer,PlatformLog> combLogMap=new HashMap<Integer, PlatformLog>();
    public static HashMap<Integer,PlatformDB> combDBMap=new HashMap<Integer, PlatformDB>();
    public static HashMap<Integer,DBCon> dbConMap=new HashMap<Integer, DBCon>();
    public static HashMap<Integer,PlatformController> combControllerMap=new HashMap<Integer, PlatformController>();
    public static HashMap<Integer,Object[]> renderMap=new HashMap<Integer, Object[]>();
    public static HashMap<String, PlatformService> combServiceMap=new HashMap<String, PlatformService>();
    public static HashMap<String,Object[]> serviceObjectMap=new HashMap<String, Object[]>();
    public static HashMap<String,PlatformColumn[]> colMap=new HashMap<String, PlatformColumn[]>();

    public static HashMap<Integer,PlatformApp> combAppMap=new HashMap<Integer, PlatformApp>();
    public static HashMap<Integer,Object[]> appServiceMap=new HashMap<Integer, Object[]>();
    public static HashMap<Integer, HashMap<Integer,PlatformUser>> combUserHashMap=new HashMap<Integer, HashMap<Integer, PlatformUser>>();
    public static HashMap<String,PlatformServiceTransaction> serviceTransactionMap=new HashMap<String, PlatformServiceTransaction>();
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
    }

    private void buildSerTran(){}

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
            Object[] objects=ProxyFactory.getProxy(NNAServiceInit1.proxyServiceHashMap,platformService.getServiceClass(),platformService.getServiceMethodType().toString());
            combServiceMap.put(entry.getKey(),platformService);
            serviceObjectMap.put(entry.getKey(),objects);
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
            Object[] objects=ProxyFactory.getProxy(NNAServiceInit1.proxyServiceHashMap,platformApp.getAppDispatchClass(),platformApp.getAppDispatchMethod());
            combAppMap.put(entry.getKey(),platformApp);
            appServiceMap.put(entry.getKey(),objects);
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
            combLogMap.put(entry.getKey(),platformLog);
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
            PlatformLog combLog=combLogMap.get(platformDB.getDbLogId());
            Log log= LogEntry.submitInitEvent(
                    combLog.getLogDir(),
                    new AtomicLong(),
                    "db.log",
            combLog.getLogLevel(),
            combLog.getLogBufferThreshold(),
            combLog.getLogCloseTimedout(),
            combLog.getLogEncode()
            );
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
            combDBMap.put(entry.getKey(),platformDB);
            dbConMap.put(entry.getKey(),dbCon);
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
            Template template= TemplateFactory.getTemplate(platformController.getRenderPage(),"UTF-8");
            Object[] objects=ProxyFactory.getProxy(NNAServiceInit1.proxyServiceHashMap,platformController.getRenderClass(),platformController.getRenderMethod());
            ((Template)objects[0]).setStrs(template.getStrs());
            ((Template)objects[0]).setViews(template.getViews());
            combControllerMap.put(entry.getKey(),platformController);
            renderMap.put(entry.getKey(),objects);
        }
    }
}
