package nna.base.init;

import nna.Marco;
import nna.base.bean.Clone;
import nna.base.bean.dbbean.*;
import nna.base.db.DBCon;
import nna.base.proxy.ProxyFactory;
import nna.base.proxy.ProxyService;
import nna.transaction.AbstractTransaction;

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
    public static HashMap<String,PlatformProxy> platformProxyMap=new HashMap<String, PlatformProxy>();
    public static HashMap<Integer,PlatformResource> platformResourceMap=new HashMap<Integer, PlatformResource>();
    public static HashMap<Integer,PlatformRole> platformRoleMap=new HashMap<Integer, PlatformRole>();
    public static HashMap<Integer,PlatformService> platformServiceMap=new HashMap<Integer, PlatformService>();
    public static HashMap<String,PlatformSql> platformSQLMap=new HashMap<String, PlatformSql>();
    public static HashMap<Integer,PlatformUser> platformUserMap=new HashMap<Integer, PlatformUser>();

    public static HashMap<String,PlatformColumn[]> columnMap=new HashMap<String, PlatformColumn[]>();
    public static HashMap<Integer,PlatformRoleResource[]> roleResourceMap=new HashMap<Integer,PlatformRoleResource[]>();
    public static HashMap<String,PlatformServiceTransaction[]> platformServiceTranMap=new HashMap<String, PlatformServiceTransaction[]>();
    public static HashMap<String,PlatformTransaction[]> platformTranMap=new HashMap<String, PlatformTransaction[]>();
    public static HashMap<Integer,PlatformUserRole[]> platformUserRoleMap=new HashMap<Integer, PlatformUserRole[]>();

    public NNAServiceInit1(PreparedStatement[] psts){
        this.psts=psts;
    }
    public void build() throws IllegalAccessException, InvocationTargetException, InstantiationException, SQLException, NoSuchMethodException, ClassNotFoundException {
        appMap=buildIMap(psts[0],"getAppId",Marco.PLATFORM_APP);
        columnMap=buildIMap(psts[1],"",Marco.PLATFORM_COLUMN);
        controllerMap=buildSMap(psts[2],"getId",Marco.PLATFORM_CONTROLLER);
        platformDBMap=buildIMap(psts[3],"getDbId",Marco.PLATFORM_DB);
        platformEntryMap=buildIMap(psts[4],"",Marco.PLATFORM_ENTRY);
        platformLogMap=buildIMap(psts[5],"",Marco.PLATFORM_LOG);
        platformProxyMap=buildIMap(psts[6],"",Marco.PLATFORM_PROXY);
        platformResourceMap=buildIMap(psts[7],"",Marco.PLATFORM_RESOURCE);
        platformRoleMap=buildIMap(psts[8],"",Marco.PLATFORM_ROLE);
        roleResourceMap=buildIMap(psts[9],"",Marco.PLATFORM_ROLE_RESOURCE);
        platformServiceMap=buildIMap(psts[10],"",Marco.PLATFORM_SERVICE);
        platformServiceTranMap=buildIMap(psts[11],"",Marco.PLATFORM_SERVICE_TRANSACTION);
        platformSQLMap=buildIMap(psts[12],"",Marco.PLATFORM_SQL);
        platformTranMap=buildIMap(psts[13],"",Marco.PLATFORM_TRANSACTION);
        platformUserMap=buildIMap(psts[14],"",Marco.PLATFORM_USER);
        platformUserRoleMap=buildIMap(psts[15],"",Marco.PLATFORM_USER_ROLE);
    }
    public HashMap buildIMap(PreparedStatement pst,String methodName,Integer serializableId) throws IllegalAccessException, InstantiationException, SQLException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        MapTransfer mapTransfer=new MapTransfer();
        HashMap<Integer,Clone> map=MapTransfer.getIMap(psts[0],methodName,serializableId);
        return mapTransfer.getIMap(map);
    }
    public HashMap buildSMap(PreparedStatement pst,String methodName,Integer serializableId) throws IllegalAccessException, InstantiationException, SQLException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        MapTransfer mapTransfer=new MapTransfer();
        HashMap<String,Clone> map=MapTransfer.getSMap(psts[0],methodName,serializableId);
        return mapTransfer.getSMap(map);
    }
}
