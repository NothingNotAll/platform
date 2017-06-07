package nna.base.init;

import nna.Marco;
import nna.MetaBean;
import nna.base.bean.dbbean.PlatformEntry;
import nna.base.bean.dbbean.PlatformServiceTransaction;
import nna.base.protocol.dispatch.AppUtil;
import nna.base.log.Log;
import nna.base.util.List;
import nna.base.util.LogUtil;
import nna.transaction.AbstractTransaction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

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
        MetaBean.setFreeResource(NNAServiceInit2.freeResourceSet);
    }

    private void buildCMSCache(PreparedStatement pst) throws SQLException {
        ResultSet cmsCountRs=pst.executeQuery();
        cmsCountRs.next();
        MetaBean.setConfMetaCache(new List<MetaBean>(cmsCountRs.getInt(1)));
        cmsCountRs.close();
        pst.close();
    }

    private void buildCMS(PreparedStatement cmPst) throws SQLException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        assert cmPst!=null;
        ResultSet rs=cmPst.executeQuery();
        PlatformEntry platformEntry;
        MetaBean cm=null;
        Log log = null;
        while(rs.next()){
            cm=new MetaBean();
            MetaBean.setFreeResource(NNAServiceInit2.freeResourceSet);
            platformEntry= (PlatformEntry) AbstractTransaction.getBean(rs, Marco.PLATFORM_ENTRY);
            MetaBean.getConfMetaCache().insert(cm,1000);
            cm.setPlatformController(NNAServiceInit2.combControllerMap.get(platformEntry.getEntryId()));
            cm.setRenderMethod((Method) NNAServiceInit2.renderMap.get(platformEntry.getEntryId())[0]);
            cm.setRenderObject(NNAServiceInit2.renderMap.get(platformEntry.getEntryId())[1]);
            cm.setPlatformApp(NNAServiceInit2.combAppMap.get(platformEntry.getEntryAppId()));
            cm.setAppServiceMethod((Method)NNAServiceInit2.appServiceMap.get(platformEntry.getEntryAppId())[0]);
            cm.setAppServiceObject(NNAServiceInit2.appServiceMap.get(platformEntry.getEntryAppId())[1]);
            cm.setPlatformDB(NNAServiceInit2.combDBMap.get(cm.getPlatformApp().getAppDbId()));
            cm.setDbCon(NNAServiceInit2.dbConMap.get(cm.getPlatformApp().getAppId()));
            cm.setServiceLogConfig(NNAServiceInit2.combLogMap.get(cm.getPlatformApp().getAppLogId()));
            cm.setLogNoGen(new AtomicLong());
            cm.setPlatformService(NNAServiceInit2.combServiceMap.get(cm.getPlatformController().getService()));
//            HashMap<String,CombTransaction> combTransactionMap=NNAServiceInit2.combTransactionHashMap.get(combService.getService().getServiceName());
//            cm.setCombTransactionMap(combTransactionMap);
//            setServiceTranList(cm);
//            HashMap<Integer,CombUser> combUserMap=NNAServiceInit2.combUserHashMap.get(platformEntry.getEntryControllerId());
//            cm.setCombUserMap(combUserMap);
//            PlatformColumn[] request=NNAServiceInit2.colMap.get(combService.getService().getServiceName()+"/req");
//            cm.setRequest(request==null?new PlatformColumn[0]:request);
//            PlatformColumn[] response=NNAServiceInit2.colMap.get(combService.getService().getServiceName()+"/rsp");
//            cm.setResponse(response==null?new PlatformColumn[0]:response);
//            cm.setLogEncrypt(combService.getService().isServiceLogEncrpt());
//            cm.setLogLevel(combService.getService().getServiceLogLevel());
            setColMap(cm);
            log=AppUtil.getLog();
//            LogUtil.log(request,log,10000);
//            LogUtil.log(response,log,10000);
            cm.setPlatformEntry(platformEntry);
            LogUtil.log(cm.getPlatformEntry(),log,1000000);
            LogUtil.log(cm.getPlatformApp(),log,1000000);
            LogUtil.log(cm.getPlatformDB(),log,10000);
            LogUtil.log(cm.getPlatformController(),log,1000000);
            LogUtil.log(cm.getServiceLogConfig(),log,1000000);
            LogUtil.log(cm.getPlatformService(),log,1000000);
            log.log("应用日志级别："+cm.getLogLevel(),10000);
            log.log("应用日志是否启用加密机制："+cm.isLogEncrypt(),1000);
            log.log("请求KEY-VALUE容器容量："+(cm.getOutReq()==null?"0":""+(cm.getOutReq().size())),100000);
            log.log("临时字段容器容量:"+(cm.getTemp()==null?"0":""+cm.getTemp().size()),100000);
            log.log("响应字段容器容量："+(cm.getRsp()==null?"0":""+cm.getRsp().size()),100000);
            log.log("入参字段容器容量："+(cm.getReq()==null?"0":""+cm.getReq().size()),100000);
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

    private void setServiceTranList(MetaBean cm) {
    }

    private void setColMap(MetaBean cm) {
        cm.setOutReq(new HashMap<String, String[]>(cm.getReqColConfig()==null?0:cm.getReqColConfig().length));
        cm.setReq(new HashMap<String, String[]>(cm.getReqColConfig()==null?0:cm.getReqColConfig().length));
        cm.setRsp(new HashMap<String, String[]>(cm.getRspColConfig()==null?0:cm.getRspColConfig().length));
        cm.setTranStack(new ArrayList<PlatformServiceTransaction>(cm.getServiceTrans().length));
        cm.setPstStack(new ArrayList<PreparedStatement[]>(cm.getServiceTrans().length));
        cm.setConStack(new ArrayList<Connection>(cm.getServiceTrans().length));
    }
}
