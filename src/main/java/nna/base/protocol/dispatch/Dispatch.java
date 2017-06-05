package nna.base.protocol.dispatch;


import nna.base.bean.combbean.CombController;
import nna.base.bean.combbean.CombService;
import nna.base.bean.confbean.ConfMeta;
import nna.base.bean.dbbean.PlatformColumn;
import nna.base.bean.dbbean.PlatformController;
import nna.base.bean.dbbean.PlatformService;
import nna.base.log.Log;
import nna.base.util.LogUtil;

import java.util.HashMap;
import java.util.Map;


public class Dispatch {

	public void dispatch(ConfMeta confMeta) throws Exception{
        Log log= confMeta.getLog();
        CombController combController= confMeta.getCombController();
        PlatformController controller=combController.getController();
        log.log("开始校验控制器状态",Log.INFO);
        check(controller,log);
        CombService combService= confMeta.getCombService();
        PlatformColumn[] reqColumns=confMeta.getRequest();
        log.log("开始校验入参字段",Log.INFO);
        checkReq(confMeta.getOutsideReq(),confMeta.getReqColumn(),reqColumns,log);
        log.log("开始校验服务状态",Log.INFO);
        PlatformService platformService=combService.getService();
        check(platformService,log);
        switch (platformService.getServiceMethod()){
            case execNonDB:
                combService.getServiceMethod().invoke(combService.getServiceObject(),null);
                break;
            case execTransaction:
                combService.getServiceMethod().invoke(combService.getServiceObject());
                break;
        }
        PlatformColumn[] rspColumns=confMeta.getResponse();
        log.log("开始校验出参字段",Log.INFO);
        HashMap<String,String[]> rspMap=confMeta.getRspColumn();
        checkRsp(rspMap,rspColumns,log);
        String appEncode=confMeta.getCombApp().getApp().getAppEncode();
        log.log("应用编码："+appEncode,Log.INFO);
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


    private static void checkRsp(HashMap<String, String[]> map, PlatformColumn[] columns, Log log) throws Exception {
        int size=columns.length;
        PlatformColumn temp;
        HashMap<String,Integer> arraySizeMap=new HashMap<String, Integer>(size);
        for(int index=0;index < size;index++){
            temp=columns[index];
            LogUtil.log(temp,log,Log.INFO);
            if(temp.isColumnIsarray()){
                checkRspArray(arraySizeMap,map,temp,log);
            }else{
                checkRspNonArray(map,temp,log);
            }
        }
    }
    private static void checkRspNonArray(HashMap<String, String[]> map, PlatformColumn temp, Log log) throws Exception {
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
        map.put(outsideName,values);
    }

    private static void checkRspArray(HashMap<String,Integer> arraySizeMap,HashMap<String, String[]> map, PlatformColumn temp, Log log) throws Exception {
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
        String defalutValue=temp.getColumnDefaultvalue();
        boolean isMust=temp.isColumnIsmust();
        if(values==null&&isMust&&defalutValue==null){
            log.log(innerName+"response column not null",Log.ERROR);
            throw new Exception("response column not null");
        }
        if(defalutValue!=null&&values==null){
            values=new String[]{defalutValue};
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
        map.put(outsideName,values);
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
        String innserName=temp.getColumnOutsideName();
        String[] values=map.get(innserName);
        boolean isMust=temp.isColumnIsmust();
        String defaultValue=temp.getColumnDefaultvalue();
        if(isMust&&(values==null||values.length==0)&&defaultValue==null){
            log.log(innserName+"request column not null",Log.ERROR);
            throw new Exception("request column not null");
        }
        String value;
        if(values!=null){
            value=values[0];
            if(value==null&&isMust){
                log.log(innserName+"request column not null",Log.ERROR);
                throw new Exception("request column not null");
            }
            if(value.length()> temp.getColumnLength()){
                log.log(innserName+"request column too long",Log.ERROR);
                throw new Exception("request column too long");
            }
        }else{
            if(defaultValue!=null){
                innerMap.put(innserName,new String[]{defaultValue});
            }
        }
        innerMap.put(innserName,values);
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
        String defalutValue=temp.getColumnDefaultvalue();
        boolean isMust=temp.isColumnIsmust();
        if(values==null&&isMust&&defalutValue==null){
            log.log(outsideName+"request column not null",Log.ERROR);
            throw new Exception("request column not null");
        }
        if(defalutValue!=null&&values==null){
            if(defalutValue!=null&&values==null){
                values=new String[]{defalutValue};
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