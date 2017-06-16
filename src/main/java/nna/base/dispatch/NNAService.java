package nna.base.dispatch;

import nna.Marco;
import nna.MetaBean;
import nna.base.dispatch.protocol.Protocol;
import nna.base.init.NNAServiceStart;

import java.lang.reflect.InvocationTargetException;
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
        getAndSetLog(metaBean);
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

    private static void getAndSetLog(MetaBean metaBean) {

    }

    private static void destroy(String rspStr) {

    }

    private static String getRspStr(MetaBeanWrapper metaBeanWrapper) throws InvocationTargetException, IllegalAccessException {
        int protocolType=metaBeanWrapper.getProtocolType();
        return Protocol.protocolAdapter(metaBeanWrapper,protocolType);
    }

    private static MetaBean getMetaBean(String entryCode) {
        MetaBean metaBean=null;
        metaBean=metaBean.clone();
        return metaBean;
    }
}
