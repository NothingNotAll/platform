package nna.base.dispatch;


import nna.MetaBean;
import nna.base.bean.dbbean.PlatformApp;
import nna.base.bean.dbbean.PlatformColumn;
import nna.base.bean.dbbean.PlatformController;
import nna.base.bean.dbbean.PlatformService;
import nna.base.log.Log;
import nna.base.util.LogUtil;

import java.util.HashMap;
import java.util.Map;


 final class Dispatch {

	 static void dispatch(MetaBean metaBean) throws Exception{
         Log log= metaBean.getLog();
        PlatformApp platformApp=metaBean.getPlatformApp();
        log.log("开始校验应用状态",Log.INFO);
        checkApp(platformApp,log);
        PlatformController controller=metaBean.getPlatformController();
        log.log("开始校验控制器状态",Log.INFO);
        check(controller,log);
        PlatformService platformService=metaBean.getPlatformService();
        PlatformColumn[] reqColumns=metaBean.getReqColConfig();
        log.log("开始校验入参字段",Log.INFO);
        Map<String,String[]> reqMap=metaBean.getInnerColumns();
        checkReq(metaBean.getOutColumns(),reqMap,reqColumns,log);
        log.log("开始校验服务状态",Log.INFO);
        check(platformService,log);
        metaBean.getServiceMethod().invoke(metaBean.getServiceObject());
        PlatformColumn[] rspColumns=metaBean.getRspColConfig();
        log.log("开始校验出参字段",Log.INFO);
        Map<String,String[]> rspMap=metaBean.getOutColumns();
        checkRsp(reqMap,rspMap,rspColumns,log);
        String appEncode=platformApp.getAppEncode();
        log.log("应用编码："+appEncode,Log.INFO);
	}

    private static void checkApp(PlatformApp platformApp, Log log) throws Exception {
	    LogUtil.log(platformApp,log,Log.INFO);
	    if(!platformApp.isAppStatus()){
	        throw new Exception("应用已被禁用");
        }
    }

    private static void check(PlatformController controller,Log log) throws Exception{
        LogUtil.log(controller,log,Log.INFO);
        if(!controller.isStatus()){
            log.log("控制器已经禁用",Log.ERROR);
            throw new Exception("控制器已经禁用");
        }
    }

    private static void check(PlatformService service,Log log)throws Exception{
        LogUtil.log(service,log,Log.INFO);
        if(!service.isStatus()){
            log.log("服务已经禁用",Log.ERROR);
            throw new Exception("服务已经禁用！");
        }
    }


    private static void checkRsp(Map<String, String[]> map,Map<String,String[]> rspMap, PlatformColumn[] columns, Log log) throws Exception {
        int size=columns.length;
        PlatformColumn temp;
        HashMap<String,Integer> arraySizeMap=new HashMap<String, Integer>(size);
        for(int index=0;index < size;index++){
            temp=columns[index];
            LogUtil.log(temp,log,Log.INFO);
            if(temp.isColumnIsarray()){
                checkRspArray(arraySizeMap,map,rspMap,temp,log);
            }else{
                checkRspNonArray(map,rspMap,temp,log);
            }
        }
    }
    private static void checkRspNonArray(Map<String, String[]> map,Map<String,String[]> rspMap, PlatformColumn temp, Log log) throws Exception {
	    String innerName=temp.getColumnInnerName();
	    String outsideName=temp.getColumnOutsideName();
	    String[] values=map.get(innerName);
	    boolean isMust=temp.isColumnIsmust();
	    String defaultValue=temp.getColumnDefaultvalue();
	    if(isMust&&(values==null||values.length==0)&&defaultValue==null){
            log.log(innerName+"response column not null",Log.ERROR);
	        throw new Exception("response column not null");
        }
        String value;
        if(values!=null){
            value=values[0];
            if(value==null&&isMust){
                log.log("response column not null",Log.ERROR);
                throw new Exception("response column not null");
            }
            if(value.length()> temp.getColumnLength()){
                log.log(innerName+"response column too long",Log.ERROR);
                throw new Exception("response column too long");
            }
        }else{
            if(defaultValue!=null){
                values=new String[]{defaultValue};
            }
        }
        rspMap.put(outsideName,values);
    }

    private static void checkRspArray(Map<String,Integer> arraySizeMap,Map<String, String[]> map, Map<String,String[]> rspMap,PlatformColumn temp, Log log) throws Exception {
        String innerName=temp.getColumnInnerName();
        String outsideName=temp.getColumnOutsideName();
        int index=innerName.lastIndexOf("/");
        String arrayPathNm;
        if(index==-1){
            log.log(innerName+"response column not a array format",Log.ERROR);
            throw new Exception("not a array format !");
        }else{
            arrayPathNm=innerName.substring(0,index);
        }
        String[] values=map.get(innerName);
        String defaultValue=temp.getColumnDefaultvalue();
        boolean isMust=temp.isColumnIsmust();
        if(values==null&&isMust&&defaultValue==null){
            log.log(innerName+"response column not null",Log.ERROR);
            throw new Exception("response column not null");
        }
        if(defaultValue!=null&&values==null){
            values=new String[]{defaultValue};
            map.put(outsideName,values);
        }
        int arraySize=values.length;
        String tempValue;
        for(int arrayIndex=0;index < arraySize;index++){
            tempValue=values[arrayIndex];
            if(tempValue.length()> temp.getColumnLength()){
                log.log(innerName+"response column too long !",Log.ERROR);
                throw new Exception("response column too long !");
            }
        }
        if(!arraySizeMap.containsKey(arrayPathNm)){
            arraySizeMap.put(arrayPathNm,values.length);
        }else{
            int size=arraySizeMap.get(arrayPathNm);
            if(size!=values.length){
                log.log(innerName+"response column:the different column but in the same array has no same length !",Log.ERROR);
                throw new Exception("response column:the different column but in the same array has no same length !");
            }
        }
        rspMap.put(outsideName,values);
    }


    private static void checkReq(Map<String,String[]> map,Map<String,String[]> innerMap, PlatformColumn[] columns, Log log)throws Exception{
        int size=columns.length;
        PlatformColumn temp;
        HashMap<String,Integer> arraySizeMap=new HashMap<String, Integer>(size);
        for(int index=0;index < size;index++){
            temp=columns[index];
            LogUtil.log(temp,log,Log.INFO);
            if(temp.isColumnIsarray()){
                checkReqArray(arraySizeMap,map,innerMap,temp,log);
            }else{
                checkReqNonArray(map,innerMap,arraySizeMap,temp,log);
            }
        }
    }
    private static void checkReqNonArray(Map<String, String[]> map, Map<String, String[]> innerMap, HashMap<String, Integer> arraySizeMap, PlatformColumn temp, Log log) throws Exception {
        String innerNm=temp.getColumnOutsideName();
        String[] values=map.get(innerNm);
        boolean isMust=temp.isColumnIsmust();
        String defaultValue=temp.getColumnDefaultvalue();
        if(isMust&&(values==null||values.length==0)&&defaultValue==null){
            log.log(innerNm+"request column not null",Log.ERROR);
            throw new Exception("request column not null");
        }
        String value;
        if(values!=null){
            value=values[0];
            if(value==null&&isMust){
                log.log(innerNm+"request column not null",Log.ERROR);
                throw new Exception("request column not null");
            }
            if(value.length()> temp.getColumnLength()){
                log.log(innerNm+"request column too long",Log.ERROR);
                throw new Exception("request column too long");
            }
        }else{
            if(defaultValue!=null){
                innerMap.put(innerNm,new String[]{defaultValue});
            }
        }
        innerMap.put(innerNm,values);
    }
    private static void checkReqArray(HashMap<String,Integer> arraySizeMap,Map<String, String[]> outsideMap, Map<String, String[]> innerReqMap,PlatformColumn temp, Log log) throws Exception {
        String outsideName=temp.getColumnOutsideName();
        String innerName=temp.getColumnInnerName();
        int index=outsideName.lastIndexOf("/");
        String arrayPathNm;
        if(index==-1){
            log.log(outsideName+"request column not a array format",Log.ERROR);
            throw new Exception("not a array format !");
        }else{
            arrayPathNm=outsideName.substring(0,index);
        }
        String[] values=outsideMap.get(outsideName);
        String defaultValue=temp.getColumnDefaultvalue();
        boolean isMust=temp.isColumnIsmust();
        if(values==null&&isMust&&defaultValue==null){
            log.log(outsideName+"request column not null",Log.ERROR);
            throw new Exception("request column not null");
        }
        if(defaultValue!=null&&values==null){
            if(defaultValue!=null&&values==null){
                values=new String[]{defaultValue};
                innerReqMap.put(innerName,values);
            }
        }
        int arraySize=values.length;
        String tempValue;
        for(int arrayIndex=0;index < arraySize;index++){
            tempValue=values[arrayIndex];
            if(tempValue.length()> temp.getColumnLength()){
                log.log(outsideName+"request column too long",Log.ERROR);
                throw new Exception("request column too long !");
            }
        }
        if(!arraySizeMap.containsKey(arrayPathNm)){
            arraySizeMap.put(arrayPathNm,values.length);
        }else{
            int size=arraySizeMap.get(arrayPathNm);
            if(size!=values.length){
                log.log(outsideName+"request column:the different column but in the same array has no same length !",Log.ERROR);
                throw new Exception("request column:the different column but in the same array has no same length !");
            }
        }
        innerReqMap.put(innerName,values);
    }
}