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

/**
 * Created by NNA-SHUAI on 2017/7/9.
 */
public class HttpUtil {
    private static final Charset charset=Charset.forName("UTF-8");
    private static final int HTTP_VERSION=0;
    private static final int HTTP_GET_HEADER=1;
    private static final int HTTP_POST_HEADER=-1;
    private static final int HTTP_BODY=2;
    private HttpUtil(){}

    static void parseHttp(HashMap<String,String[]> headers,HashMap<String,String[]> kvMap,SocketChannel socketChannel,Integer timedOut) throws IOException {
        //why ? this below can keep timeOut function
        socketChannel.socket().setSoTimeout(timedOut);
        InputStream inStream = socketChannel.socket().getInputStream();
        ReadableByteChannel wrappedChannel = Channels.newChannel(inStream);
        //why ?
        ByteBuffer byteBuffer=ByteBuffer.allocate(2);
        StringBuilder line=new StringBuilder("");StringBuilder copy=new StringBuilder("");
        CharBuffer charBuffer;
        String lines;
        int readCount;
        int parseType=0;
        boolean isGETMethod;
        int index=0;
        while(true){
            byteBuffer.clear();
            readCount=wrappedChannel.read(byteBuffer);
            if(readCount>0){
                byteBuffer.flip();
                charBuffer=charset.decode(byteBuffer);
                lines=charBuffer.toString();
                line.append(lines);
                copy.append(lines);
                if(lines.endsWith("\r\n")){
//                    System.out.println("-------------"+index+"----------------");
//                    System.out.println(line);
//                    System.out.println("+-------------"+index+++"--------------");
                    switch (parseType){
                        case HTTP_VERSION:
                            isGETMethod=parseFirstLine(headers,kvMap,line.toString());
                            parseType=isGETMethod?HTTP_GET_HEADER:HTTP_POST_HEADER;
                            break;
                        case HTTP_GET_HEADER://Headers_GET
                            if(line.toString().equals("\r\n\r\n")){
                            System.out.println("end");
                                return ;
                            }
                            parseHeaders(headers,line.toString());
                            break;
                        case HTTP_POST_HEADER://Headers_POST
                            if(line.toString().equals("\r\n")){
                                parseType=HTTP_BODY;
                                break;
                            }
                            parseHeaders(headers,line.toString());
                            break;
                        case HTTP_BODY:
                            String[] contentType=headers.get("Content-type");
                            String[] boundary=headers.get("boundary");
                            if(contentType!=null&&contentType[0].toLowerCase().equals("multipart")&&boundary[0].equals(line.toString().trim())){
                                return ;
                            }else{
                                parseKeyValues(kvMap,line.toString());
                                return ;
                            }
                    }
                    if(copy.toString().endsWith("\r\n\r\n")){
                        System.out.println(copy.toString());
                        return ;
                    }
                    line.delete(0,line.length()-1);
                }
            }else{
                return ;
            }
        }
    }

    static boolean parseFirstLine(HashMap<String,String[]> headers,HashMap<String,String[]> kvMap,String firstLine){
        boolean isMethodGET=false;
        String[] methodAndURIAndKVAndHttpVersion=firstLine.split("[\\s]");
        String method=methodAndURIAndKVAndHttpVersion[0].trim();
        headers.put("HTTP_METHOD",new String[]{method});
        System.out.println("HTTP_METHOD"+":"+method);
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
        headers.put("HTTP_URI",new String[]{URI});
        System.out.println("HTTP_URI"+":"+URI);
        headers.put("HTTP_VERSION",new String[]{methodAndURIAndKVAndHttpVersion[2].trim()});
        System.out.println("HTTP_VERSION"+":"+methodAndURIAndKVAndHttpVersion[2]);
        return isMethodGET;
    }

    static void parseHeaders(HashMap<String,String[]> headers,String headerLine){
        if(headerLine!=null&&!headerLine.trim().equals("")){
            String[] headerKV=headerLine.split("[:]");
            headers.put(headerKV[0].trim(),new String[]{headerKV[1].trim()});
            System.out.println(headerKV[0]+":"+headerKV[1]);
        }
    }

    static void parseKeyValues(HashMap<String,String[]> kvMap,String kvLine){
        String[] kvs=kvLine.split("[&]");
        if(kvs!=null&&kvs.length>=1){
            String[] kv;
            for(String kvStr:kvs){
                if(kvStr!=null&&!kvStr.trim().equals("")){
                    kv=kvStr.split("[=]");
                    kvMap.put(kv[0].trim(), new String[]{URLDecoder.decode(kv[1])});
                    System.out.println(kv[0]+":"+kv[1]);
                }
            }
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


//    static void parseHttpRequest(HashMap<String,String[]> headers,HashMap<String,String[]> kvMap,String request) throws IOException {
//        BufferedReader lineReader=new BufferedReader(new CharArrayReader(request.toCharArray()));
//        String line;
//        //process first line
//        boolean isGETMethod;
//        while(true){
//            line=lineReader.readLine();
//            if(line!=null){
//                break;
//            }
//        }
//        isGETMethod=parseFirstLine(headers,kvMap,line);
//
//        //process header part
//        while(true){
//            line=lineReader.readLine();
//            if(line.trim().equals("")){
//                break;
//            }
//            parseHeaders(headers,line);
//        }
//        if(isGETMethod){
//            return ;
//        }
//
//        //process multi part
//        String boundary=null;
//        String contentType=headers.get("Content-Type")[0];
//        if(contentType!=null&&contentType.toLowerCase().startsWith("multipart")){
//            String multi=headers.get("Content-Type")[0];
//            boundary=multi.split("[;]")[1].split("[=]")[1].trim();
//        }
//
//        //process body part
//        while(true){
//            line=lineReader.readLine();
//            if(line!=null){
//                break;
//            }
//        }
//        if(boundary!=null){
//            if(line.trim().equals(boundary)){
//                return;
//            }
//        }
//        parseKeyValues(kvMap,line);
//    }
}
