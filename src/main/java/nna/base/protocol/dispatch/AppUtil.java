package nna.base.protocol.dispatch;

import nna.MetaBean;
import nna.base.log.Log;


/**
 * for app layer to get the util
 * @author NNA-SHUAI
 * @create 2017-05-13 20:36
 **/

public class AppUtil {

    private AppUtil(){

    }

    public static Log getLog(){
        return StoreData.getConfig().getLog();
    }

    public static void putTemp(String key,Object temp){
        StoreData.getConfig().getTemp();
    }

    public static void putRspColumn(String rspColumnKey,String[] rspColumnValue){
        StoreData.getConfig().getInnerColumns().put(rspColumnKey,rspColumnValue);
    }

    public static String[] getRequest(String name){
        return StoreData.getConfig().getInnerColumns().get(name);
    }

    public static Object getTemp(String name){
        return StoreData.getConfig().getTemp();
    }

    public static void setConfMeta(MetaBean confMeta){
        StoreData.setConfig(confMeta);
    }

}
