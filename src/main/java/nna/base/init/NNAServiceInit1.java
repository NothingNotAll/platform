package nna.base.init;

import nna.Marco;
import nna.base.bean.Clone;
import nna.base.bean.dbbean.*;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;


/**
 * @author NNA-SHUAI
 * @create 2017-05-24 14:39
 **/

public class NNAServiceInit1 {
    public static PreparedStatement[] psts;
    public static HashMap<Integer,PlatformApp> appMap=new HashMap<Integer, PlatformApp>();
    public static HashMap<String,PlatformController> controllerMap=new HashMap<String, PlatformController>();
    public static HashMap<Integer,PlatformDB> platformDBMap=new HashMap<Integer, PlatformDB>();
    public static HashMap<Integer,PlatformEntry> platformEntryMap=new HashMap<Integer, PlatformEntry>();
    public static HashMap<Integer,PlatformLog> platformLogMap=new HashMap<Integer, PlatformLog>();
    public static HashMap<Integer,PlatformResource> platformResourceMap=new HashMap<Integer, PlatformResource>();
    public static HashMap<Integer,PlatformRole> platformRoleMap=new HashMap<Integer, PlatformRole>();
    public static HashMap<Integer,PlatformService> platformServiceMap=new HashMap<Integer, PlatformService>();
    public static HashMap<String,PlatformSql> platformSQLMap=new HashMap<String, PlatformSql>();
    public static HashMap<Integer,PlatformUser> platformUserMap=new HashMap<Integer, PlatformUser>();

    public static ArrayList<PlatformColumn> columns=new ArrayList<PlatformColumn>();
    public static ArrayList<PlatformRoleResource> roleResources=new ArrayList<PlatformRoleResource>();
    public static ArrayList<PlatformEntryTransaction> serviceTrans=new ArrayList<PlatformEntryTransaction>();
    public static ArrayList<PlatformTransaction> trans=new ArrayList<PlatformTransaction>();
    public static ArrayList<PlatformUserRole> userRoles=new ArrayList<PlatformUserRole>();
    public static ArrayList<PlatformProxy> proxies=new ArrayList<PlatformProxy>();

    public static HashMap<String,ArrayList<PlatformColumn>> columnMap=new HashMap<String, ArrayList<PlatformColumn>>();
    public static HashMap<Integer,ArrayList<PlatformRoleResource>> roleResourceMap=new HashMap<Integer,ArrayList<PlatformRoleResource>>();
    public static HashMap<String,ArrayList<PlatformEntryTransaction>> serviceTranMap=new HashMap<String, ArrayList<PlatformEntryTransaction>>();
    public static HashMap<String,ArrayList<PlatformTransaction>> tranMap=new HashMap<String, ArrayList<PlatformTransaction>>();
    public static HashMap<Integer,ArrayList<PlatformUserRole>> userRoleMap=new HashMap<Integer, ArrayList<PlatformUserRole>>();
    public static HashMap<String,ArrayList<PlatformProxy>> proxyMap=new HashMap<String, ArrayList<PlatformProxy>>();

    public NNAServiceInit1(PreparedStatement[] psts){
        this.psts=psts;
    }
    public void build() throws IllegalAccessException, InvocationTargetException, InstantiationException, SQLException, NoSuchMethodException, ClassNotFoundException {
        appMap=buildIMap(psts[0],"getAppId",Marco.PLATFORM_APP);
        controllerMap=buildSMap(psts[1],"getId",Marco.PLATFORM_CONTROLLER);
        platformDBMap=buildIMap(psts[2],"getDbId",Marco.PLATFORM_DB);
        platformEntryMap=buildIMap(psts[3],"getEntryId",Marco.PLATFORM_ENTRY);
        platformLogMap=buildIMap(psts[4],"getLogId",Marco.PLATFORM_LOG);
        platformResourceMap=buildIMap(psts[6],"getResourceId",Marco.PLATFORM_RESOURCE);
        platformRoleMap=buildIMap(psts[7],"getRoleId",Marco.PLATFORM_ROLE);
        platformServiceMap=buildSMap(psts[8],"getServiceName",Marco.PLATFORM_SERVICE);
        platformSQLMap=buildSMap(psts[9],"getSqlId",Marco.PLATFORM_SQL);
        platformUserMap=buildIMap(psts[10],"getUserId",Marco.PLATFORM_USER);

        getList(columns,psts[11],Marco.PLATFORM_COLUMN);
        getList(roleResources,psts[12],Marco.PLATFORM_ROLE_RESOURCE);
        getList(serviceTrans,psts[13],Marco.PLATFORM_SERVICE_TRANSACTION);
        getList(trans,psts[14],Marco.PLATFORM_TRANSACTION);
        getList(userRoles,psts[15],Marco.PLATFORM_USER_ROLE);
        getList(proxies,psts[16],Marco.PLATFORM_PROXY);

        reduceSList(columns,"getColumnId",(Map)columnMap);
        reduceIList(roleResources,"getRoleId",(Map)roleResourceMap);
        reduceSList(serviceTrans,"getTransactions",(Map)serviceTranMap);
        reduceSList(trans,"getTransactionName",(Map)tranMap);
        reduceSList(userRoles,"getUserId",(Map)userRoleMap);
        reducePlatformProxies();

    }

    private void reducePlatformProxies() {
        Iterator<PlatformProxy> iterator=proxies.iterator();
        while(iterator.hasNext()){
            PlatformProxy platformProxy=iterator.next();
            ArrayList<PlatformProxy> platformProxies=proxyMap.get(platformProxy.getBeenproxyClassRegex()+"/"+platformProxy.getBeenproxyMethodRegex());
            if(platformProxies==null){
                ArrayList<PlatformProxy> arrayList=new ArrayList();
                arrayList.add(platformProxy);
                proxyMap.put(platformProxy.getBeenproxyClassRegex()+"/"+platformProxy.getBeenproxyMethodRegex(),arrayList);
            }else{
                platformProxies.add(platformProxy);
            }
        }
    }

    public void reduceSList(
            List list,
            String getMethodName,
            Map<String,ArrayList> map
    ) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        MapTransfer mapTransfer=new MapTransfer();
        mapTransfer.reduceSList(list,getMethodName,map);
    }

    public void reduceIList(
            List list,
            String getMethodName,
            Map<Integer,ArrayList> map
    ) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        MapTransfer mapTransfer=new MapTransfer();
        mapTransfer.reduceIList(list,getMethodName,map);
    }

    public HashMap buildIMap(PreparedStatement pst,
                             String methodName,
                             Integer serializableId) throws IllegalAccessException, InstantiationException, SQLException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        MapTransfer mapTransfer=new MapTransfer();
        HashMap<Integer,Clone> map=MapTransfer.getIMap(pst,methodName,serializableId);
        return mapTransfer.getIMap(map);
    }
    public HashMap buildSMap(PreparedStatement pst,
                             String methodName,
                             Integer serializableId) throws IllegalAccessException, InstantiationException, SQLException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        MapTransfer mapTransfer=new MapTransfer();
        HashMap<String,Clone> map=MapTransfer.getSMap(pst,methodName,serializableId);
        return mapTransfer.getSMap(map);
    }

    public void getList(List tList,
                        PreparedStatement pst,
                        int serializableId
    ) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException, InvocationTargetException {
        MapTransfer mapTransfer=new MapTransfer();
        ArrayList<Clone> arrayList=new ArrayList<Clone>();
        MapTransfer.getList(arrayList,pst,serializableId);
        mapTransfer.getList(arrayList,tList);
    }
}
