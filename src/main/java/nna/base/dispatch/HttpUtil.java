package nna.base.dispatch;

import java.io.*;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by NNA-SHUAI on 2017/7/9.
 */
public class HttpUtil {
    private static final Charset charset=Charset.forName("UTF-8");
    private static final int HTTP_VERSION=0;
    private static final int HTTP_GET_HEADER=1;
    private static final int HTTP_POST_HEADER=-1;
    private static final int HTTP_BODY=2;
    public static void main(String[] args){
        System.out.println("http1.1 GET ajfaljfalj\r\n".getBytes().length);
//        BufferedReader lineReader=new BufferedReader(new CharArrayReader(new String("aflajfla\r\nasf").toCharArray()));
//        while(true){
//            try {
//                String line=lineReader.readLine();
//                System.out.println(line);
//                if(line==null){
//                    System.out.println(line);
//                    break;
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }
    private HttpUtil(){}

    static void parseLine(HashMap<String,String[]> headers,HashMap<String,String[]> kvMap,SocketChannel channel) throws IOException {
        ByteBuffer byteBuffer=ByteBuffer.allocate(2);
        StringBuilder line=new StringBuilder("");
        CharBuffer charBuffer;
        String lines;
        int readCount=0;
        int parseType=0;
        boolean isGETMethod=false;
        while(true){
            byteBuffer.clear();
            readCount=channel.read(byteBuffer);
            if(readCount>0){
                byteBuffer.flip();
                charBuffer=charset.decode(byteBuffer);
                lines=charBuffer.toString();
                line.append(lines);
                if(lines.endsWith("\r\n")){
                    switch (parseType){
                        case HTTP_VERSION:
                            isGETMethod=parseFirstLine(headers,kvMap,line.toString());
                            parseType=isGETMethod?HTTP_GET_HEADER:HTTP_POST_HEADER;
                            break;
                        case HTTP_GET_HEADER://Headers_GET
                            if(line.toString().trim().equals("")){
                                return ;
                            }
                            parseHeaders(headers,line.toString());
                            break;
                        case HTTP_POST_HEADER://Headers_POST
                            if(line.toString().trim().equals("")){
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
                    line.delete(0,line.length()-1);
                }
            }
        }
    }

    static void parseHttpRequest(HashMap<String,String[]> headers,HashMap<String,String[]> kvMap,String request) throws IOException {
            BufferedReader lineReader=new BufferedReader(new CharArrayReader(request.toCharArray()));
            String line;
            //process first line
            boolean isGETMethod;
            while(true){
                line=lineReader.readLine();
                if(line!=null){
                    break;
                }
            }
            isGETMethod=parseFirstLine(headers,kvMap,line);

            //process header part
            while(true){
                line=lineReader.readLine();
                if(line.trim().equals("")){
                    break;
                }
                parseHeaders(headers,line);
            }
            if(isGETMethod){
                return ;
            }

            //process multi part
            String boundary=null;
            String contentType=headers.get("Content-Type")[0];
            if(contentType!=null&&contentType.toLowerCase().startsWith("multipart")){
                String multi=headers.get("Content-Type")[0];
                boundary=multi.split("[;]")[1].split("[=]")[1].trim();
            }

            //process body part
            while(true){
                line=lineReader.readLine();
                if(line!=null){
                    break;
                }
            }
            if(boundary!=null){
                if(line.trim().equals(boundary)){
                    return;
                }
            }
            parseKeyValues(kvMap,line);
    }
    static boolean parseFirstLine(HashMap<String,String[]> headers,HashMap<String,String[]> kvMap,String firstLine){
        boolean isMethodGET=false;
        String[] methodAndURIAndKVAndHttpVersion=firstLine.split("[\\s]");
        String method=methodAndURIAndKVAndHttpVersion[0].trim();
        headers.put("HTTP_METHOD",new String[]{method});
        String URI=methodAndURIAndKVAndHttpVersion[1];
        if(method.equals("GET")){
            isMethodGET=true;
            String[] uriAndKVS=methodAndURIAndKVAndHttpVersion[1].split("[?]");
            URI=uriAndKVS[0].trim();
            if(uriAndKVS.length>1){
                String kvs=uriAndKVS[1];
                parseKeyValues(kvMap,kvs);
            }
        }
        headers.put("HTTP_VERSION",new String[]{methodAndURIAndKVAndHttpVersion[2].trim()});
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
                kv=kvStr.split("[=]");
                kvMap.put(kv[0].trim(), new String[]{URLDecoder.decode(kv[1])});
                System.out.println(kv[0]+":"+kv[1]);
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
}
