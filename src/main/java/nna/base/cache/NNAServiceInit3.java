package nna.base.cache;

import nna.Marco;
import nna.base.bean.combbean.*;
import nna.base.bean.confbean.ConfMeta;
import nna.base.bean.dbbean.PlatformColumn;
import nna.base.bean.dbbean.PlatformEntry;
import nna.base.protocol.dispatch.AppUtil;
import nna.base.log.Log;
import nna.base.util.LogUtil;
import nna.transaction.AbstractTransaction;

import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * @author NNA-SHUAI
 * @create 2017-05-24 20:28
 **/

public class NNAServiceInit3 {
    private PreparedStatement[] cmPsts;

    public NNAServiceInit3(PreparedStatement[] cmPsts){
        this.cmPsts=cmPsts;
    }

    public void build() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException, InvocationTargetException {
        this.buildFreeResource();
        this.buildCMSCache(cmPsts[16]);
        this.buildCMS(cmPsts[17]);
    }

    private void buildFreeResource() {
        ConfMeta.setFreeResources(NNAServiceInit2.freeResourceSet);
    }

    private void buildCMSCache(PreparedStatement pst) throws SQLException {
        ResultSet cmsCountRs=pst.executeQuery();
        cmsCountRs.next();
        CacheFactory.initConfMetaCache(cmsCountRs.getInt(1));
        CacheFactory.initEnToIdCache(cmsCountRs.getInt(1));
        cmsCountRs.close();
        pst.close();
    }

    private void buildCMS(PreparedStatement cmPst) throws SQLException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        assert cmPst!=null;
        ResultSet rs=cmPst.executeQuery();
        PlatformEntry platformEntry;
        ConfMeta cm;
        Log log = null;
        while(rs.next()){
            cm=new ConfMeta();
            ConfMeta.setFreeResources(NNAServiceInit2.freeResourceSet);
            platformEntry= (PlatformEntry) AbstractTransaction.getBean(rs, Marco.PLATFORM_ENTRY);
            CacheFactory.getEnToIdCache().put(platformEntry.getEntryUri(),platformEntry.getEntryControllerId());
            CacheFactory.getConfMetaCache().insert(cm,100);
            CombController combController=NNAServiceInit2.combControllerMap.get(platformEntry.getEntryControllerId());
            cm.setCombController(combController);
            CombApp combApp=NNAServiceInit2.combAppMap.get(platformEntry.getEntryAppId());
            cm.setCombApp(combApp);
            CombDB combDB=NNAServiceInit2.combDBMap.get(combApp.getApp().getAppDbId());
            cm.setCombDB(combDB);
            CombLog combLog=NNAServiceInit2.combLogMap.get(combApp.getApp().getAppLogId());
            cm.setCombLog(combLog);
            CombService combService=NNAServiceInit2.combServiceMap.get(combController.getController().getService());
            cm.setCombService(combService);
            HashMap<String,CombTransaction> combTransactionMap=NNAServiceInit2.combTransactionHashMap.get(combService.getService().getServiceName());
            cm.setCombTransactionMap(combTransactionMap);
            HashMap<Integer,CombUser> combUserMap=NNAServiceInit2.combUserHashMap.get(platformEntry.getEntryControllerId());
            cm.setCombUserMap(combUserMap);
            PlatformColumn[] request=NNAServiceInit2.colMap.get(combService.getService().getServiceName()+"/req");
            cm.setRequest(request==null?new PlatformColumn[0]:request);
            PlatformColumn[] response=NNAServiceInit2.colMap.get(combService.getService().getServiceName()+"/rsp");
            cm.setResponse(response==null?new PlatformColumn[0]:response);
            cm.setLogEncrypt(combService.getService().isServiceLogEncrpt());
            cm.setLogLevel(combService.getService().getServiceLogLevel());
            setColMap(cm);
            log=AppUtil.getLog();
            LogUtil.log(request,log,10000);
            LogUtil.log(response,log,10000);
            cm.setPlatformEntry(platformEntry);
            LogUtil.log(cm.getPlatformEntry(),log,1000000);
            LogUtil.log(cm.getCombApp().getApp(),log,1000000);
            LogUtil.log(cm.getCombDB().getPlatformDB(),log,10000);
            LogUtil.log(cm.getCombController().getController(),log,1000000);
            LogUtil.log(cm.getCombLog().getPlatformLog(),log,1000000);
            LogUtil.log(cm.getCombService().getService(),log,1000000);
            log.log("应用日志级别："+cm.getLogLevel(),10000);
            log.log("应用日志是否启用加密机制："+cm.isLogEncrypt(),1000);
            log.log("请求KEY-VALUE容器容量："+(cm.getOutsideReq()==null?"0":""+(cm.getOutsideReq().size())),100000);
            log.log("临时字段容器容量:"+(cm.getTemp()==null?"0":""+cm.getTemp().size()),100000);
            log.log("响应字段容器容量："+(cm.getRspColumn()==null?"0":""+cm.getRspColumn().size()),100000);
            log.log("入参字段容器容量："+(cm.getReqColumn()==null?"0":""+cm.getReqColumn().size()),100000);
            log.log("-------------------------------------------------------------------------------------",100000000);
        }
        rs.close();
        cmPst.close();
        log.log("COPY ALL RIGHT RESERVED @ peishuai.Lee",10000);
        log.log("phone:13028986696/17639378826",10000);
        log.log("wechat/qq:1295980850",10000);
        log.log("email:lpshuai@hotmail.com/6lshuai@gmail.com",10000);
        log.log("THANKS FOR THE GOLD! THANKS FOR SUPPORT !",10000);
        log.log("NNA INIT SUCCESS !",10000);
        System.out.println("COPY ALL RIGHT RESERVED @ peishuai.Lee");
        System.out.println("phone:13028986696/17639378826");
        System.out.println("wechat/qq:1295980850");
        System.out.println("email:lpshuai@hotmail.com/6lshuai@gmail.com");
        System.out.println("THANKS FOR THE GOLD! THANKS FOR SUPPORT !");
        System.out.println("NNA INIT SUCCESS");
    }

    private void setColMap(ConfMeta cm) {
        CombService cs=cm.getCombService();
        cm.setOutsideReq(new HashMap<String, String[]>(cm.getRequest()==null?0:cm.getRequest().length));
        cm.setReqColumn(new HashMap<String, String[]>(cm.getRequest()==null?0:cm.getRequest().length));
        cm.setRspColumn(new HashMap<String, String[]>(cm.getResponse()==null?0:cm.getResponse().length));
        cm.setTemp(new HashMap<String, Object>(cs.
                getService().
                getServiceTempsize()));
    }
}
