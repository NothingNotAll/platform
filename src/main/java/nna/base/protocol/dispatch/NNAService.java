package nna.base.protocol.dispatch;

import nna.Marco;
import nna.MetaBean;
import nna.base.bean.dbbean.PlatformColumn;
import nna.base.init.NNAServiceStart;
import nna.base.util.CharUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

/**
 * @author NNA-SHUAI
 * @create 2017-05-22 1:52
 **/

public class NNAService {
    static {
        try {
            System.out.println("--------------------------------NNA Service start");
            Class.forName(NNAServiceStart.class.getCanonicalName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String dispatch(Map<String,String[]> map){
        String entryCode=map.get(Marco.HEAD_ENTRY_CODE)[0];
        MetaBean metaBean=getMetaBean(entryCode);
        MetaBeanWrapper metaBeanWrapper=new MetaBeanWrapper(metaBean);
        String rspStr="";
        try {
            Dispatch.dispatch(metaBeanWrapper);
            rspStr=getRspStr(metaBeanWrapper);
        } catch (Exception e) {
            e.printStackTrace();
        }catch (Throwable e){
            e.printStackTrace();
        }finally {
            destroy(rspStr);
        }
        return rspStr;
    }

    private static void destroy(String rspStr) {

    }

    private static String getRspStr(MetaBeanWrapper metaBeanWrapper) throws InvocationTargetException, IllegalAccessException {
        Map<String,String[]> rsp=metaBeanWrapper.getRsp();
        Method method=metaBeanWrapper.getRenderMethod();
        if(method!=null){
            Object object=metaBeanWrapper.getRenderObject();
            object=metaBeanWrapper.getRenderMethod().invoke(object,rsp);
            return object==null?"":object.toString();
        }
        StringBuilder rspBuilder=new StringBuilder("");
        rspBuilder.append("{");
        Iterator<Map.Entry<String,String[]>> iterator=rsp.entrySet().iterator();
        Map.Entry<String,String[]> entry;
        String keyNm;
        String[] keyVs;
        int length;
        int index=0;
        String valueTemp;
        while(iterator.hasNext()){
            entry=iterator.next();
            keyNm=entry.getKey();
            keyVs=entry.getValue();
            rspBuilder.append("\'"+keyNm+"\':");
            rspBuilder.append("[");
            length=keyVs.length;
            for(;index<length;index++){
                valueTemp=keyVs[index];
                valueTemp=CharUtil.stringToJson(valueTemp,true);
                rspBuilder.append("\'"+valueTemp+"\',");
            }
            rspBuilder.delete(rspBuilder.length()-1,rspBuilder.length());
            index=0;
            rspBuilder.append("]");
        }
        rspBuilder.append("}");
        return rspBuilder.toString();
    }

    private static MetaBean getMetaBean(String entryCode) {
        MetaBean metaBean=null;

        return metaBean;
    }
}
