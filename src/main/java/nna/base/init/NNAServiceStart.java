package nna.base.init;

import nna.Marco;
import nna.MetaBean;
import nna.StoreData;
import nna.base.bean.dbbean.*;
import nna.base.util.orm.ObjectFactory;
import nna.test.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * @author NNA-SHUAI
 * @create 2017-05-25 9:29
 **/

public class NNAServiceStart {

    public static void main(String[] args){
        System.out.println("NNAService init...");
    }

    static{
//        try {
//            Test.testCon();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        try {
            Util.loadNIOSelector();
            NNAServiceInit0 nna0=new NNAServiceInit0();
            PreparedStatement[] preparedStatements=nna0.build();
            NNAServiceInit1 nna1=new NNAServiceInit1(preparedStatements);
            nna1.build();
            NNAServiceInit2 nna2=new NNAServiceInit2();
            nna2.build();
            StoreData.getConfig().getLog().close();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void buildMetaBeanClassField(){

    }

    private static void buildMetaBeanField(ResultSet metaBeanRs) throws ClassNotFoundException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        PlatformEntry platformEntry=(PlatformEntry) ObjectFactory.getBean(metaBeanRs,Marco.PLATFORM_ENTRY);
        MetaBean metaBean=new MetaBean();
        Integer controllerId=platformEntry.getEntryControllerId();
        buildController(metaBean,controllerId);
        String serviceId=platformEntry.getEntryServiceId();
        buildService(metaBean,serviceId);
        Integer dbId=platformEntry.getEntryDBId();
        buildDB(metaBean,dbId);
        Integer logId=platformEntry.getEntryLogId();
        buldLog(metaBean,logId);
        String reqId=platformEntry.getEntryReqId();
        buildColumns(metaBean,reqId);
        String rspId=platformEntry.getEntryRspId();
        buildColumns(metaBean,rspId);
        String tranId=platformEntry.getEntryTranId();
        buildTran(metaBean,tranId);
    }

    private static void buildTran(MetaBean metaBean, String tranId) {
        if(tranId==null){
            return ;
        }

    }

    private static void buildColumns(MetaBean metaBean, String rspId) {
        if(rspId==null){
            return ;
        }
        LinkedList<PlatformColumn> columns=MetaBean.platformColumnMap.get(rspId);
        if(columns!=null){
            metaBean.setReqColConfig(columns.toArray(new PlatformColumn[0]));
        }else{

        }
    }

    private static void buldLog(MetaBean metaBean, Integer logId) {
        if(logId==null){
            return ;
        }
        PlatformLog platformLog=MetaBean.platformLogMap.get(logId);
        if(platformLog!=null){
            metaBean.setPlatformLog(platformLog);
        }else{

        }
    }

    private static void buildDB(MetaBean metaBean, Integer dbId) {
        if(dbId==null){
            return ;
        }
        PlatformDB platformDB=MetaBean.platformDBMap.get(dbId);
        if(platformDB!=null){
            metaBean.setPlatformDB(platformDB);
        }else{

        }
    }

    private static void buildService(MetaBean metaBean, String serviceId) {
        if(serviceId==null){
            return ;
        }
        PlatformService platformService=MetaBean.platformServiceMap.get(serviceId);
        if(platformService!=null){
            metaBean.setPlatformService(platformService);
        }else{

        }
    }

    private static void buildController(MetaBean metaBean, Integer controllerId) {
        if(controllerId==null){
            return ;
        }
        PlatformController platformController=MetaBean.platformControllerMap.get(controllerId);
        if(platformController!=null){
            metaBean.setPlatformController(platformController);
        }else{

        }
    }
}
