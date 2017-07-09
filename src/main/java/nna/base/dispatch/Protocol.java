package nna.base.dispatch;

import nna.Marco;
import nna.MetaBean;
import nna.base.util.CharUtil;
import nna.base.util.XmlUtil;
import nna.base.util.orm.ObjectUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static nna.base.dispatch.NNAService.service;


/**
 * @author NNA-SHUAI
 * @create 2017-06-17 7:42
 **/

public class Protocol {

    static SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    private final static Protocol protocol=new Protocol();
    private Protocol(){}

    static String protocolAdapter(MetaBean metaBean, int protocolType) throws InvocationTargetException, IllegalAccessException {
        switch (protocolType){
            case Marco.XML_PROTOCOL:
                return HTTP(metaBean);
            case Marco.HTTP_PROTOCOL:
                return XML(metaBean);
        }
        return "";
    }

    private static String XML(MetaBean metaBean) {
        return XmlUtil.buildXML("root",metaBean.getOutColumns());
    }

    private static String HTTP(MetaBean metaBean) throws InvocationTargetException, IllegalAccessException {
        Map<String,String[]> rsp=metaBean.getOutColumns();
        Method method=metaBean.getRenderMethod();
        if(method!=null){
            Object object=metaBean.getRenderObject();
            object=metaBean.getRenderMethod().invoke(object,rsp);
            return object==null?"":object.toString();
        }
        int size=rsp.size();
        if(size > 0){
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
                    valueTemp= CharUtil.stringToJson(valueTemp,true);
                    rspBuilder.append("\'"+valueTemp+"\',");
                }
                rspBuilder.delete(rspBuilder.length()-1,rspBuilder.length());
                index=0;
                rspBuilder.append("]");
            }
            rspBuilder.append("}");
            return rspBuilder.toString();
        }else{
            return "";
        }
    }

    public static String processHttp(SocketChannel socketChannel) {
        try {
            byte[] bytes=BytesUtil.readBytes(socketChannel);
            HashMap<String,String[]> headers=new HashMap<String, String[]>();
            HashMap<String,String[]> kvs=new HashMap<String, String[]>();
            HttpUtil.parseHttpRequest(headers,kvs,new String(bytes,"UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socketChannel.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

    public static String processXml(SocketChannel channel) {
        byte[] bytes;
        try {
            bytes=BytesUtil.readBytes(channel);
            HashMap<String,String[]> map=new HashMap<String, String[]>();
            XmlUtil.parseXmlStr(new String(bytes),map);
            String responseStr=service(map);
            channel.write(ByteBuffer.wrap(responseStr.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Object[] getProcessConfig(int protocolType) throws NoSuchMethodException {
        Method method;
        switch (protocolType){
            case Marco.HTTP_PROTOCOL:
                method=ObjectUtil.loadMethodFromObjectAndMethodName(protocol,"processHttp");
                return new Object[]{protocol,method};
            case Marco.XML_PROTOCOL:
                method=ObjectUtil.loadMethodFromObjectAndMethodName(protocol,"processXml");
                return new Object[]{protocol,method};
        }
        return null;
    }
}
