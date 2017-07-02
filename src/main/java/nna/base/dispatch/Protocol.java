package nna.base.dispatch;

import nna.Marco;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author NNA-SHUAI
 * @create 2017-06-17 7:42
 **/

public class Protocol {

    private final static Protocol protocol=new Protocol();
    private Protocol(){}

    public static void process(SocketChannel socketChannel, ByteBuffer byteBuffer,int protocolType,int ioType){

    }
    public static void process(SocketChannel socketChannel,int protocolType,int ioType){

    }

    public static String protocolAdapter(MetaBeanWrapper metaBeanWrapper, int protocolType) throws InvocationTargetException, IllegalAccessException {
        switch (protocolType){
            case Marco.XML_PROTOCOL:
                return HTTP(metaBeanWrapper);
            case Marco.HTTP_PROTOCOL:
                return XML(metaBeanWrapper);
        }
        return "";
    }


    private static String XML(MetaBeanWrapper metaBeanWrapper) {
        return "";
    }

    private static String HTTP(MetaBeanWrapper metaBeanWrapper) throws InvocationTargetException, IllegalAccessException {
        Map<String,String[]> rsp=metaBeanWrapper.getRsp();
        Method method=metaBeanWrapper.getRenderMethod();
        if(method!=null){
            Object object=metaBeanWrapper.getRenderObject();
            object=metaBeanWrapper.getRenderMethod().invoke(object,rsp);
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

    public static String processHttp(SocketChannel socketChannel) throws IOException {
        byte[] bytes=read(socketChannel);
        System.out.println(new String(bytes));
        return null;
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
                    System.out.println(totalCount);
                    byteBuffer.flip();
                    temp=new byte[readCount];
                    byteBuffer.get(temp);
                    bytes.add(temp);
                }
            } catch (IOException e) {
                e.printStackTrace();
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
        try {
            System.out.println(new String(byteList,0,byteList.length,"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            XmlUtil.parseXmlStr(new String(byteList),new HashMap<String, String[]>());
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return byteList;
    }

    public static String processXml(SocketChannel channel) throws IOException {
        System.out.println("process XML");
        byte[] bytes=read(channel);
        try {
            channel.write(ByteBuffer.wrap(bytes));
        } catch (IOException e) {
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
