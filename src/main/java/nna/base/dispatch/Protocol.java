package nna.base.dispatch;

import nna.Marco;
import nna.MetaBean;
import nna.base.util.CharUtil;
import nna.base.util.XmlUtil;
import nna.base.util.orm.ObjectUtil;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
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

    private static byte[] read(SocketChannel channel) throws IOException {
        LinkedList<byte[]> bytes=new LinkedList<byte[]>();
        byte[] temp;
        ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
        int totalCount=0;
        int readCount;
        while(true){
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
        Long end=System.currentTimeMillis();
        try {
            System.out.println("read time end:"+simpleDateFormat.format(end)+" from client:"+new String(bytes,0,bytes.length,"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HashMap<String,String[]> map=new HashMap<String, String[]>();
        toMap(bytes,map);
        socketChannel.write(ByteBuffer.wrap(getHttpResponseHeader().getBytes("UTF-8")));
        socketChannel.close();
        return null;
    }

    private static void toMap(byte[] bytes,HashMap<String,String[]> map) throws IOException {
        BufferedReader lineReader=new BufferedReader(new CharArrayReader(new String(bytes,"UTF-8").toCharArray()));
        String line;
        String[] strs;
        String method;
        String link;
        String[] kvs;
        while(true){
            line=lineReader.readLine();
            if(line!=null){
                strs=line.split("[\\s]");
                method=strs[0];
                link=strs[1];
                map.put("HTTP_METHOD",new String[]{method});
                map.put("HTTP_VERSION",new String[]{strs[2]});
                if(method.equals("GET")){
                    strs=strs[1].split("[?]");
                    link=strs[0];
                    if(strs.length>1){
                        strs=strs[1].split("[&]");
                        for(String kv:strs){
                            kvs=kv.split("[=]");
                            map.put(kvs[0].trim(),new String[]{URLDecoder.decode(kvs[1].trim())});
                            System.out.println(kvs[0]+":"+URLDecoder.decode(kvs[1]));
                        }
                    }
                }
                map.put("HTTP_METHOD",new String[]{link});
                break;
            }else{
                continue;
            }
        }
        while(true){
            line=lineReader.readLine();
            if(line!=null){
                if(line.trim().equals("")){
                    break;
                }
                kvs=line.split("[:]");
                map.put(kvs[0].trim(),new String[]{kvs[1].trim()});
                System.out.println(kvs[0]+":"+URLDecoder.decode(kvs[1]));
                continue;
            }else{
                break;
            }
        }

        if(method.equals("GET")){
            return ;
        }

        if(map.get("Content-Type")[0].toLowerCase().startsWith("multipart")){
            return ;
        }

        while(true){
            line=lineReader.readLine();
            if(line!=null){
                strs=line.split("[&]");
                for(String kv:strs){
                    kvs=kv.split("[=]");
                    map.put(kvs[0].trim(),new String[]{URLDecoder.decode(kvs[1].trim())});
                    System.out.println(kvs[0]+":"+URLDecoder.decode(kvs[1]));
                }
            }else{
                return;
            }
        }

    }

    private static String getHttpResponseHeader(){
        return new String("HTTP/1.1 200 OK\r\n" +
                "Date: Sat, 31 Dec 2005 23:59:59 GMT\r\n" +
                "Content-Type: text/html;charset=UTF-8\r\n" +
                "Content-Length: 122\r\n" +
                "\r\n" +
                "＜html＞\n" +
                "＜head＞\n" +
                "＜title＞Wrox Homepage＜/title＞\n" +
                "＜/head＞\n" +
                "＜body＞\n" +
                "＜!-- body goes here --＞\n" +
                "＜/body＞\n" +
                "＜/html＞");
    }

    public static String processXml(SocketChannel channel) {
        byte[] bytes;
        try {
            bytes=read(channel);
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
//        try {
//            System.out.println("read time end:"+simpleDateFormat.format(end)+" from client:"+new String(bytes,0,bytes.length,"UTF-8"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
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
