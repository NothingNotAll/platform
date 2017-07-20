package nna.base.dispatch;

import java.io.*;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by NNA-SHUAI on 2017/7/9.
 */
public class HttpUtil {
    private static final Charset charset=Charset.forName("UTF-8");
    private HttpUtil(){}

    static void parseHttp(HashMap<String,String[]> headers,HashMap<String,String[]> kvMap,SocketChannel socketChannel,Integer timedOut) throws IOException {
        HashMap<String,LinkedList<String>> zcHeader=new HashMap<String, LinkedList<String>>();
        HashMap<String,LinkedList<String>> zcKvMap=new HashMap<String, LinkedList<String>>();
        //why ? this below can keep timeOut function
        socketChannel.socket().setSoTimeout(timedOut);
        InputStream inStream = socketChannel.socket().getInputStream();
        ReadableByteChannel wrappedChannel = Channels.newChannel(inStream);
        //why ?
        String firstLine=readLine(wrappedChannel);
        boolean isGetMethod=parseFirstLine(zcHeader,zcKvMap,firstLine);
        parseLines(zcHeader,socketChannel);
        if(!isGetMethod){
            if(headers.get("Content-Type")[0].trim().startsWith("multipart")){
                parseLines(zcHeader,socketChannel);
            }else{
                String httpBody=readLine(socketChannel);
                parseKeyValues(zcKvMap,httpBody);
            }
        }
        linked2Array(zcHeader,headers);
        linked2Array(zcKvMap,kvMap);
    }

    static private void linked2Array(HashMap<String,LinkedList<String>> linked,HashMap<String,String[]> array){
        Iterator<Map.Entry<String,LinkedList<String>>> linkedIterator=linked.entrySet().iterator();
        Map.Entry<String,LinkedList<String>> entry;
        String key;
        LinkedList<String> values;
        while(linkedIterator.hasNext()){
            entry=linkedIterator.next();
            key=entry.getKey();
            values=entry.getValue();
            array.put(key,values.toArray(new String[0]));
        }
    }

    static void parseLines(HashMap<String,LinkedList<String>> headers,SocketChannel socketChannel) throws IOException {
        String line;
        while(true){
            line=readLine(socketChannel);
            if(line.trim().equals("")){
                break;
            }
            parseHeaders(headers,line);
        }
    }

    static String readLine(ReadableByteChannel socketChannel) throws IOException {
        ByteBuffer byteBuffer=ByteBuffer.allocate(1);
        StringBuilder line=new StringBuilder("");
        CharBuffer charBuffer;
        int readCount;
        while(true){
            byteBuffer.clear();
            readCount=socketChannel.read(byteBuffer);
            if(readCount>0){
                byteBuffer.flip();
                charBuffer=charset.decode(byteBuffer);
                line.append(charBuffer.toString());
                if(line.toString().endsWith("\r\n")){
                    return line.toString();
                }
            }
        }
    }

    static boolean parseFirstLine(HashMap<String,LinkedList<String>> headers,HashMap<String,LinkedList<String>> kvMap,String firstLine){
        boolean isMethodGET=false;
        String[] methodAndURIAndKVAndHttpVersion=firstLine.split("[\\s]");
        String method=methodAndURIAndKVAndHttpVersion[0].trim();
        putKV(headers,"HTTP_METHOD",method);
        String URI;
        if(method.equals("GET")){
            isMethodGET=true;
            String[] uriAndKVS=methodAndURIAndKVAndHttpVersion[1].split("[?]");
            URI=uriAndKVS[0].trim();
            if(uriAndKVS.length>1){
                String kvs=uriAndKVS[1];
                parseKeyValues(kvMap,kvs);
            }
        }else{
            URI=methodAndURIAndKVAndHttpVersion[1];
        }
        putKV(headers,"HTTP_URI",URI);
        putKV(headers,"HTTP_VERSION",methodAndURIAndKVAndHttpVersion[2].trim());
        return isMethodGET;
    }

    static void parseHeaders(HashMap<String,LinkedList<String>> headers,String headerLine){
        if(headerLine!=null&&!headerLine.trim().equals("")){
            String[] headerKV=headerLine.split("[:]");
            putKV(headers,headerKV[0].trim(),headerKV[1].trim());
        }
    }

    static void parseKeyValues(HashMap<String,LinkedList<String>> kvMap,String kvLine){
        String[] kvs=kvLine.split("[&]");
        if(kvs!=null&&kvs.length>=1){
            String[] kv;
            for(String kvStr:kvs){
                if(kvStr!=null&&!kvStr.trim().equals("")){
                    kv=kvStr.split("[=]");
                    putKV(kvMap,kv[0].trim(),URLDecoder.decode(kv[1]));
                }
            }
        }
    }
    static private void putKV(HashMap<String,LinkedList<String>> lKVMap,String key,String value){
        LinkedList<String> values=lKVMap.get(key);
        if(values!=null){
            values.add(value);
        }else{
            values=new LinkedList<String>();
            values.add(value);
            lKVMap.put(key,values);
        }
    }

    static String getHttpResponseHeader(){
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
}
