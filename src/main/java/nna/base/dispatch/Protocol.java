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

import static nna.base.dispatch.BytesUtil.readBytes;
import static nna.base.dispatch.HttpUtil.getHttpResponseHeader;
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
        return "";
    }

    public static String processHttp(SocketChannel socketChannel,Integer timedOut) {
        try {
            HashMap<String,String[]> headers=new HashMap<String, String[]>();
            HashMap<String,String[]> kvs=new HashMap<String, String[]>();
            HttpUtil.parseHttp(headers,kvs,socketChannel,timedOut);
            socketChannel.write(ByteBuffer.wrap(getHttpResponseHeader().getBytes("UTF-8")));
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socketChannel.write(ByteBuffer.wrap(getHttpResponseHeader().getBytes("UTF-8")));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                socketChannel.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

    public static String processXml(SocketChannel channel,Integer timedOut) {
        byte[] bytes;
        try {
            bytes= readBytes(channel);
            System.out.println(new String(bytes,"UTF-8"));
            HashMap<String,String[]> map=new HashMap<String, String[]>();
            XmlUtil.parseXmlStr(new String(bytes),map);
//            String responseStr=service(map);
//            channel.write(ByteBuffer.wrap(responseStr.getBytes("UTF-8")));
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
