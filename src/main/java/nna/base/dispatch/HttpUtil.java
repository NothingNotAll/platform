package nna.base.dispatch;

import java.net.URLDecoder;
import java.util.HashMap;

/**
 * Created by NNA-SHUAI on 2017/7/9.
 */
public class HttpUtil {
    private HttpUtil(){}

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

    static boolean parseHeaders(HashMap<String,String[]> headers,String headerLine){
        boolean isUpload=false;
        String[] headerKV=headerLine.split("[:]");
        headers.put(headerKV[0].trim(),new String[]{headerKV[1].trim()});
        if(headers.get("Content-Type")[0].toLowerCase().startsWith("multipart")){
            isUpload=true;
        }
        return isUpload;
    }

    static void parseKeyValues(HashMap<String,String[]> kvMap,String kvLine){
        String[] kvs=kvLine.split("[&]");
        String[] kv;
        for(String kvStr:kvs){
            kv=kvStr.split("[:]");
            kvMap.put(kv[0].trim(), new String[]{URLDecoder.decode(kv[1])});
        }
    }
}
