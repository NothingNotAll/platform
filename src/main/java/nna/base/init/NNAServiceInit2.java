package nna.base.init;

import nna.MetaBean;
import nna.base.bean.dbbean.*;
import nna.base.log.Log;
import nna.base.util.List;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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

    public void build() throws IllegalAccessException, InvocationTargetException, InstantiationException, SQLException, NoSuchMethodException, ClassNotFoundException, IOException {
        buildStaticAttribute();
        buildNonStaticAttribute();
        System.out.println("init success!");
    }

    private void buildNonStaticAttribute() {
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

    private void buildMetaBean(MetaBean temp,PlatformEntry entry) {
        temp.setPlatformEntry(entry);
        temp.setPublic(entry.getEntryFree());
        Integer controllerId=entry.getEntryControllerId();
        PlatformController platformController=NNAServiceInit1.controllerMap.get(controllerId);

        Integer appId=entry.getEntryAppId();
        PlatformApp platformApp=NNAServiceInit1.appMap.get(appId);
        temp.setPlatformApp(platformApp);

        Integer dbId=entry.getEntryDBId();
        PlatformDB platformDB=NNAServiceInit1.platformDBMap.get(dbId);

        Integer logId=entry.getEntryLogId();
        PlatformLog platformLog=NNAServiceInit1.platformLogMap.get(logId);

        String reqId=entry.getEntryReqId();
        ArrayList<PlatformColumn> reqColumns=NNAServiceInit1.columnMap.get(reqId);

        String rspId=entry.getEntryRspId();
        ArrayList<PlatformColumn> rspColumns=NNAServiceInit1.columnMap.get(reqId);

        String serviceId=entry.getEntryServiceId();
        PlatformService platformService=NNAServiceInit1.platformServiceMap.get(serviceId);

        Integer tempSize=entry.getEntryTempSize();

        String tranName=entry.getEntryTransactions();
        ArrayList<PlatformEntryTransaction> trans=NNAServiceInit1.serviceTranMap.get(tranName);

    }

    public void buildStaticAttribute(){
        buildPLog();
        buildFreeResource();
        buildPlatformUser();
        buildPlatformUserRole();
        buildPlatformUserResource();
        buildMetaBeanList();
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
                "/LOG/"+platformLog.getLogDir(),
                "nna",
                platformLog.getLogLevel(),
                platformLog.getLogBufferThreshold(),
                platformLog.getLogCloseTimedout(),
                platformLog.getLogEncode(),4000
        );
        MetaBean.setpLog(pLog);
        for(int index=0;index < 1000;index++){
            final Log pLog2=Log.getLog(
                    "/LOG/"+platformLog.getLogDir(),
                    "nna",
                    platformLog.getLogLevel(),
                    platformLog.getLogBufferThreshold(),
                    platformLog.getLogCloseTimedout(),
                    platformLog.getLogEncode(),4000
            );
            new Thread(new Runnable() {
                public void run() {
                    for(int i=0;i< 3998;i++){
                        pLog2.log(i+"",Log.INFO);
                    }
                    pLog2.close();
                }
            }).start();
        }
    }


}
