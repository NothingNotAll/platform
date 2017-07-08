package nna.base.dispatch;

import nna.Marco;
import nna.MetaBean;
import nna.base.util.CharUtil;
import nna.base.util.XmlUtil;
import nna.base.util.orm.ObjectUtil;
import org.dom4j.DocumentException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
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

    public static String protocolAdapter(MetaBean metaBean, int protocolType) throws InvocationTargetException, IllegalAccessException {
        switch (protocolType){
            case Marco.XML_PROTOCOL:
                return HTTP(metaBean);
            case Marco.HTTP_PROTOCOL:
                return XML(metaBean);
        }
        return "";
    }

    private static String XML(MetaBean metaBean) {
        return "";
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

    private static byte[] read(SocketChannel channel) {
        LinkedList<byte[]> bytes=new LinkedList<byte[]>();
        byte[] temp;
        ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
        int totalCount=0;
        int readCount;
        while(true){
            try {
                byteBuffer.clear();
                readCount=channel.read(byteBuffer);
                if(readCount==-1){
                    break;
                }
                if(readCount>0){
                    totalCount+=readCount;
                    byteBuffer.flip();
                    temp=new byte[readCount];
                    byteBuffer.get(temp);
                    bytes.add(temp);
                }
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    channel.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        int index2;
        int index3=0;
        byte[] byteList=new byte[totalCount];
        int size=bytes.size();
        int length;
        for(int index=0;index < size;index++){
            temp=bytes.get(index);
            length=temp.length;
            index2=0;
            for(;index2<length;index2++){
                byteList[index3++]=temp[index2];
            }
        }
        return byteList;
    }

    public static String processHttp(SocketChannel socketChannel) throws IOException {
        byte[] bytes=read(socketChannel);
        HashMap<String,String[]> map=new HashMap<String, String[]>();
        socketChannel.write(ByteBuffer.wrap(bytes));
        socketChannel.close();
        Long end=System.currentTimeMillis();
        try {
            System.out.println("read time end:"+simpleDateFormat.format(end)+" from client:"+new String(bytes,0,bytes.length,"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String processXml(SocketChannel channel) {
        byte[] bytes=read(channel);
        try {
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
        Long end=System.currentTimeMillis();
        try {
            System.out.println("read time end:"+simpleDateFormat.format(end)+" from client:"+new String(bytes,0,bytes.length,"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
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
