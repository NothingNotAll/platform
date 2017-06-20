package nna.base.dispatch;

import nna.Marco;
import nna.base.util.CharUtil;
import nna.base.util.orm.ObjectUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
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

    public static String processHttp(SocketChannel channel,int protocolType,int eventType){
        ZeroCopy zeroCopy=new ZeroCopy(1024,100,100);
        ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
        try {
            channel.finishConnect();
            int readCount=channel.read(byteBuffer);
            while(readCount!=-1){
                byte[] bytes=new byte[readCount];
                byteBuffer.get(bytes,0,readCount);
                zeroCopy.add(bytes);
                byteBuffer.clear();
                readCount=channel.read(byteBuffer);
            }
            System.out.println(zeroCopy.toBytes().length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        switch (eventType){
            case SelectionKey.OP_READ:
                ;
                break;
            case SelectionKey.OP_WRITE:
                ;
                break;
        }
        return null;
    }

    public static String processXml(SocketChannel channel,int protocolType,int eventType){

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
