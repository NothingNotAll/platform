package nna.base.dispatch;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;

/**
 * Created by NNA-SHUAI on 2017/7/9.
 */
public class HttpUtil {

    private HttpUtil(){}
    static void parseHttpRequest(HashMap<String,String[]> headers,HashMap<String,String[]> kvMap,String request) throws IOException {
            BufferedReader lineReader=new BufferedReader(new CharArrayReader(request.toCharArray()));
            String line;
            while(true){
                line=lineReader.readLine();
                if(line!=null){
                    break;
                }
            }
            boolean isGETMethod=parseFirstLine(headers,kvMap,line);
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
            String boundary=null;
            String contentType=headers.get("Content-Type")[0];
            if(contentType!=null&&contentType.toLowerCase().startsWith("multipart")){
                String multi=headers.get("Content-Type")[0];
                boundary=multi.split("[;]")[1].split("[=]")[1].trim();
            }
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
