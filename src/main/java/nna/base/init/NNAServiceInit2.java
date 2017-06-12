package nna.base.init;

import nna.MetaBean;
import nna.base.bean.dbbean.*;
import nna.base.log.Log;
import nna.base.log.LogEntry;
import nna.base.util.List;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author NNA-SHUAI
 * @create 2017-05-24 20:21
 **/

public class NNAServiceInit2 {
    static SimpleDateFormat yyMMdd=new SimpleDateFormat("yyyy-MM-dd$HH-mm-ss-SSS");
    public static void main(String[] args){
        System.out.println(yyMMdd.format(System.currentTimeMillis()));
    }

    public void build() throws IllegalAccessException, InvocationTargetException, InstantiationException, SQLException, NoSuchMethodException, ClassNotFoundException, IOException {
        buildStaticAttribute();
        buildNonStaticAttribute();
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

    }

    private void buildMetaBeanList() {
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
        int userId;
        while(iterator.hasNext()){
            entry=iterator.next();
            userId=entry.getKey();
            System.out.println(userId);
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
        Log pLog=LogEntry.submitInitEvent(
                "/"+yyMMdd.format(System.currentTimeMillis())+platformLog.getLogDir(),
                new AtomicLong(),
                "nna.log",
                platformLog.getLogLevel(),
                platformLog.getLogBufferThreshold(),
                platformLog.getLogCloseTimedout(),
                platformLog.getLogEncode()
        );
        MetaBean.setpLog(pLog);
    }


}
