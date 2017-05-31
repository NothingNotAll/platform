package nna.base.cache;

import nna.Marco;
import nna.base.bean.Clone;
import nna.base.bean.combbean.*;
import nna.base.bean.dbbean.*;
import nna.base.proxy.ProxyFactory;
import nna.base.proxy.ProxyService;
import nna.enums.DBSQLConValType;
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
    public static HashMap<String,String[]> servNmToTranNm=new HashMap<String, String[]>();
    public static HashMap<Integer,PlatformUser> userMap=new HashMap<Integer, PlatformUser>();
    public static HashMap<Integer,PlatformResource[]> roleResourceMap=new HashMap<Integer, PlatformResource[]>();
    public static HashMap<Integer,PlatformRole> roleMap=new HashMap<Integer, PlatformRole>();
    public static HashMap<Integer,PlatformRole[]> userRoleMap=new HashMap<Integer, PlatformRole[]>();
    public static HashMap<String,CombSQL> combSQLHashMap=new HashMap<String, CombSQL>();
    public static HashMap<Integer,CombUser> comUserMap=new HashMap<Integer, CombUser>();

    public static HashMap<Integer,PlatformResource[]> roleResources=new HashMap<Integer, PlatformResource[]>();
    public static HashMap<String,CombTransaction> tranMap=new HashMap<String, CombTransaction>();

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
            CombUser combUser=new CombUser();
            Integer userId=entry.getKey();
            PlatformRole[] roles=entry.getValue();
            combUser.setPlatformRoles(roles);
            combUser.setPlatformUser(userMap.get(userId));
            HashMap<String,PlatformResource> userResource=new HashMap<String, PlatformResource>();
            for(int index=0;index < roles.length;index++){
                PlatformRole role=roles[index];
                PlatformResource[] resources=roleResourceMap.get(role.getRoleId());
                for(int i=0;i < resources.length;i++){
                    userResource.put(String.valueOf(resources[i].getResourceId()),resources[i]);
                }
            }
            combUser.setResoruces(userResource);
            comUserMap.put(userId,combUser);
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
            String[] tNms=servNmToTranNm.get(entry.getKey());
            if(tNms!=null){
                String[] temp=new String[tNms.length+1];
                temp[tNms.length]=entry.getValue().getTransactionName();
                System.arraycopy(tNms,0,temp,0,tNms.length);
                servNmToTranNm.put(entry.getKey(),temp);
            }else{
                servNmToTranNm.put(entry.getKey(),new String[]{entry.getValue().getTransactionName()});
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
            Map.Entry<String,PlatformSql> entry=iterator.next();
            PlatformSql platformSql=entry.getValue();
            String sql=AbstractTransaction.buildSQL(platformSql);
            CombSQL combSQL=new CombSQL();
            combSQL.setSql(sql);
            combSQL.setPlatformSql(platformSql);
            if(platformSql.getAppCondition().contains(",")){
                combSQL.setConditions(platformSql.getAppCondition().trim().split("[,]"));
            }else{
                if(!platformSql.getAppCondition().trim().equals("")){
                    combSQL.setConditions(new String[]{platformSql.getAppCondition().trim()});
                }else{
                    combSQL.setConditions(new String[0]);
                }
            }

            combSQL.setColumns(platformSql.getAppColumn().split("[,]"));

            String[] conTypes;
            if(platformSql.getAppConditionType().contains(",")){
                conTypes=platformSql.getAppConditionType().split("[,]");
            }else{
                if(!platformSql.getAppConditionType().trim().equals("")){
                    conTypes=new String[]{platformSql.getAppConditionType()};
                }else{
                    conTypes=new String[0];
                }
            }
            DBSQLConValType[] dbs=new DBSQLConValType[conTypes.length];
            for(int index=0;index < dbs.length;index++){
                dbs[index]=DBSQLConValType.valueOf(conTypes[index]);
            }
            combSQL.setDBSQLConValTypes(dbs);
            combSQLHashMap.put(entry.getKey(),combSQL);
        }
    }

    private void buildTran(PreparedStatement tranPst) throws SQLException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Iterator<Map.Entry<String,String[]>> iterator=servNmToTranNm.entrySet().iterator();
        while(iterator.hasNext()){
            String[] tranNames=iterator.next().getValue();
            for(int index=0;index < tranNames.length;index++){
                String tranName=tranNames[index];
                if(tranMap.get(tranName)==null){
                    tranPst.setString(1,tranName);
                    ResultSet rs=tranPst.executeQuery();
                    CombTransaction combTransaction=new CombTransaction();
                    ArrayList<String> SQLS=new ArrayList<String>();
                    ArrayList<PlatformSql> platformSqls=new ArrayList<PlatformSql>();
                    ArrayList<String[]> columns=new ArrayList<String[]>();
                    ArrayList<String[]> conditions=new ArrayList<String[]>();
                    ArrayList<DBSQLConValType[]> dbs=new ArrayList<DBSQLConValType[]>();
                    while(rs.next()){
                        PlatformTransaction platformTransaction=(PlatformTransaction) AbstractTransaction.getBean(rs,Marco.PLATFORM_TRANSACTION);
                        CombSQL combSQL=combSQLHashMap.get(platformTransaction.getSqlId());
                        SQLS.add(combSQL.getSql());
                        platformSqls.add(combSQL.getPlatformSql());
                        columns.add(combSQL.getColumns());
                        conditions.add(combSQL.getConditions());
                        dbs.add(combSQL.getDBSQLConValTypes());
                    }
                    rs.close();
                    combTransaction.setSqls(SQLS.toArray(new String[0]));
                    combTransaction.setPlatformSqls(platformSqls.toArray(new PlatformSql[0]));
                    combTransaction.setColumns(columns);
                    combTransaction.setConditionValueTypes(dbs);
                    combTransaction.setConditions(conditions);
                    tranMap.put(tranName,combTransaction);
                }
            }
        }
        tranPst.close();
    }
}
