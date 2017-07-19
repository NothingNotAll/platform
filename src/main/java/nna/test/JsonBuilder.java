package nna.test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by NNA-SHUAI on 2017/7/17.
 */
public class JsonBuilder {

    private JsonBuilder(){}

    public static String mapToJsonStr(Map<String,String[]> keyValues){
        StringBuilder jsonBuilder=new StringBuilder("");
        Iterator<Map.Entry<String,String[]>> iterator=keyValues.entrySet().iterator();
        Map.Entry<String,String[]> entry;

        String nodePath;
        String nodeValue;
        String[] paths;
        Map<String,LinkedList<String[]>> map=new HashMap<String, LinkedList<String[]>>();
        while(iterator.hasNext()){
            entry=iterator.next();
            nodePath=entry.getKey();
            nodePath=entry.getKey();
            paths=nodePath.split("[/]");
            for(String node:paths){
                if(!map.containsKey(node)){
                    map.put(node,new LinkedList<String[]>());
                }
            }
        }
        return jsonBuilder.toString();
    }
}
