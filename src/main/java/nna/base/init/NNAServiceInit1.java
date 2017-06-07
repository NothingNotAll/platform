package nna.base.init;

import nna.Marco;
import nna.base.bean.Clone;
import nna.base.bean.dbbean.*;
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
    public static HashMap<String, ProxyService> proxyServiceHashMap=new HashMap<String, ProxyService>();
    public static HashMap<Integer,PlatformResource> resources=new HashMap<Integer, PlatformResource>();
    public static HashMap<String,PlatformServiceTransaction[]> servNmToTranNm=new HashMap<String, PlatformServiceTransaction[]>();
    public static HashMap<Integer,PlatformUser> userMap=new HashMap<Integer, PlatformUser>();
    public static HashMap<Integer,PlatformResource[]> roleResourceMap=new HashMap<Integer, PlatformResource[]>();
    public static HashMap<Integer,PlatformRole> roleMap=new HashMap<Integer, PlatformRole>();
    public static HashMap<Integer,PlatformRole[]> userRoleMap=new HashMap<Integer, PlatformRole[]>();

    public static HashMap<String,PlatformSql> platformSqlMap=new HashMap<String, PlatformSql>();
    public static HashMap<Integer,PlatformResource[]> roleResources=new HashMap<Integer, PlatformResource[]>();

    public NNAServiceInit1(PreparedStatement[] psts){
        this.psts=psts;
    }
    public void build() throws IllegalAccessException, InvocationTargetException, InstantiationException, SQLException, NoSuchMethodException, ClassNotFoundException {
        this.buildProxy(psts[0]);
        this.buildResources(psts[1]);
        this.buildCombSQL(psts[2]);
        this.buildServNmToTranNm(psts[3]);
        this.buildUserMap(psts[4]);
        this.buildRoleMap(psts[5]);
        this.buildRoleResourceMap(psts[6]);
        this.buildUserRole(psts[7]);

        this.buildRoleResource(psts[8]);//update
        this.buildTran(psts[9]);
        this.buildCombUser();
    }

    private void buildTran(PreparedStatement pst) {

    }

    private void buildUserMap(PreparedStatement userPst) throws IllegalAccessException, InstantiationException, SQLException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        HashMap<Integer,Clone> map=MapTransfer.getIMap(userPst,"getUserId", Marco.PLATFORM_USER);
        final MapTransfer<PlatformUser> mapTransfer =new MapTransfer<PlatformUser>();
        mapTransfer.iMap=map;
        userMap= mapTransfer.getIMap();
    }

    private void buildCombUser() {
        Iterator<Map.Entry<Integer,PlatformRole[]>> iterator=userRoleMap.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<Integer,PlatformRole[]> entry=iterator.next();
            PlatformUser combUser=new PlatformUser();
            Integer userId=entry.getKey();
            PlatformRole[] roles=entry.getValue();
            HashMap<String,PlatformResource> userResource=new HashMap<String, PlatformResource>();
            for(int index=0;index < roles.length;index++){
                PlatformRole role=roles[index];
                PlatformResource[] resources=roleResourceMap.get(role.getRoleId());
                for(int i=0;i < resources.length;i++){
                    userResource.put(String.valueOf(resources[i].getResourceId()),resources[i]);
                }
            }
        }
    }

    private void buildRoleResource(PreparedStatement pst) throws SQLException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ResultSet rs=pst.executeQuery();
        while(rs.next()){
            PlatformRoleResource platformRoleResource=(PlatformRoleResource)AbstractTransaction.getBean(rs,Marco.PLATFORM_ROLE_RESOURCE);
            PlatformResource[] platformResources=roleResources.get(platformRoleResource.getRoleId());
            if(platformResources!=null){
                PlatformResource[] temp=new PlatformResource[platformResources.length+1];
                System.arraycopy(platformResources,0,temp,0,platformResources.length);
                temp[platformResources.length]=resources.get(platformRoleResource.getResourceId());
                roleResources.put(platformRoleResource.getRoleId(),temp);
            }else{
                roleResources.put(platformRoleResource.getRoleId(),new PlatformResource[]{resources.get(platformRoleResource.getResourceId())});
            }
        }
    }
    private void buildRoleMap(PreparedStatement camPst) throws IllegalAccessException, InstantiationException, SQLException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        HashMap<Integer,Clone> map=MapTransfer.getIMap(camPst,"getRoleId", Marco.PLATFORM_ROLE);
        final MapTransfer<PlatformRole> mapTransfer =new MapTransfer<PlatformRole>();
        mapTransfer.iMap=map;
        roleMap= mapTransfer.getIMap();
    }

    private void buildUserRole(PreparedStatement pst) throws SQLException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ResultSet rs=pst.executeQuery();
        while(rs.next()){
            PlatformUserRole platformUserRole=(PlatformUserRole)AbstractTransaction.getBean(rs,Marco.PLATFORM_USER_ROLE);
            PlatformRole[] platformRoles=userRoleMap.get(platformUserRole.getUserId());
            if(platformRoles!=null){
                PlatformRole[] temp=new PlatformRole[platformRoles.length+1];
                System.arraycopy(platformRoles,0,temp,0,platformRoles.length);
                temp[platformRoles.length]=roleMap.get(platformUserRole.getRoleId());
                userRoleMap.put(platformUserRole.getUserId(),temp);
            }else{
                userRoleMap.put(platformUserRole.getUserId(),new PlatformRole[]{roleMap.get(platformUserRole.getRoleId())});
            }
        }
    }

    private void buildRoleResourceMap(PreparedStatement roleResourcePst) throws SQLException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        ResultSet rs=roleResourcePst.executeQuery();
        while(rs.next()){
            PlatformRoleResource platformRoleResource= (PlatformRoleResource) AbstractTransaction.getBean(rs,Marco.PLATFORM_ROLE_RESOURCE);
            PlatformResource[] platformResources=roleResourceMap.get(platformRoleResource.getRoleId());
            if(platformResources==null){
                roleResourceMap.put(platformRoleResource.getRoleId(),new PlatformResource[]{resources.get(platformRoleResource.getResourceId())});
            }else{
                PlatformResource[] temp=new PlatformResource[platformResources.length+1];
                System.arraycopy(platformResources,0,temp,0,platformResources.length);
                temp[platformResources.length]=resources.get(resources.get(platformRoleResource.getResourceId()));
                roleResourceMap.put(platformRoleResource.getRoleId(),temp);
            }
        }
    }

    private void buildResources(PreparedStatement allResourcePst) throws IllegalAccessException, InstantiationException, SQLException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        HashMap<Integer,Clone> map=MapTransfer.getIMap(allResourcePst,"getResourceId", Marco.PLATFORM_RESOURCE);
        final MapTransfer<PlatformResource> mapTransfer =new MapTransfer<PlatformResource>();
        mapTransfer.iMap=map;
        resources= mapTransfer.getIMap();
    }

    private void buildProxy(PreparedStatement proxyPst) throws SQLException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        //初始化代理配置
        ResultSet proxyRs=proxyPst.executeQuery();
        LinkedList<PlatformProxy> linkedList=new LinkedList<PlatformProxy>();
        while(proxyRs.next()){
            PlatformProxy platformProxy=(PlatformProxy) AbstractTransaction.getBean(proxyRs,Marco.PLATFORM_PROXY);
            linkedList.add(platformProxy);
        }
        proxyServiceHashMap.putAll(ProxyFactory.getClassMethodProxyServiceConfig(linkedList));
        proxyPst.close();
    }

    private void buildServNmToTranNm(PreparedStatement servTran) throws IllegalAccessException, InstantiationException, SQLException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        HashMap<String,Clone> map=MapTransfer.getSMap(servTran,"getServiceName", Marco.PLATFORM_SERVICE_TRANSACTION);
        MapTransfer<PlatformServiceTransaction> mapTransfer=new MapTransfer<PlatformServiceTransaction>();
        mapTransfer.sMap=map;
        HashMap<String,PlatformServiceTransaction> stMap=mapTransfer.getSMap();
        Iterator<Map.Entry<String,PlatformServiceTransaction>> iterator=stMap.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,PlatformServiceTransaction> entry=iterator.next();
            PlatformServiceTransaction[] tNms=servNmToTranNm.get(entry.getKey());
            if(tNms!=null){
                PlatformServiceTransaction[] temp=new PlatformServiceTransaction[tNms.length+1];
                temp[tNms.length]=entry.getValue();
                System.arraycopy(tNms,0,temp,0,tNms.length);
                servNmToTranNm.put(entry.getKey(),temp);
            }else{
                servNmToTranNm.put(entry.getKey(),new PlatformServiceTransaction[]{entry.getValue()});
            }
        }
    }

    private void buildCombSQL(PreparedStatement servTran) throws IllegalAccessException, InstantiationException, SQLException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        HashMap<String,Clone> map=MapTransfer.getSMap(servTran,"getSqlId", Marco.PLATFORM_SQL);
        MapTransfer<PlatformSql> mapTransfer=new MapTransfer<PlatformSql>();
        mapTransfer.sMap=map;
        HashMap<String,PlatformSql> stMap=mapTransfer.getSMap();
        Iterator<Map.Entry<String,PlatformSql>> iterator=stMap.entrySet().iterator();
        while(iterator.hasNext()){
        }
    }

}
