package nna.base.util;



import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * @author NNA-SHUAI
 * @create 2017-06-16 17:34
 **/

public class XmlUtil {
    public static String parseXmlStr(String xmlStr,Map<String,String[]> map) throws DocumentException {
        HashMap<String,LinkedList<String>> listMap=new HashMap<String, LinkedList<String>>();
        Document document=DocumentHelper.parseText(xmlStr);
        Element root=document.getRootElement();
        parseNode(root,listMap);
        Iterator<Map.Entry<String,LinkedList<String>>> iterator=listMap.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,LinkedList<String>> entry=iterator.next();
            LinkedList<String> temp=entry.getValue();
            String key=entry.getKey();
            map.put(key,temp.toArray(new String[0]));
        }
        return root.getName();
    }
    public static void parseNode(Element element,HashMap<String, LinkedList<String>> listMap){
        element.elements();
        java.util.List<Element> eles=element.elements();
        String abPath;
        String value;
        String abPath2;
        for(Element ele:eles){
            List<Element> temps=ele.elements();
            if(temps.size()>0){
                parseNode(ele,listMap);
            }else{
                abPath=ele.getPath();
                value=ele.getText();
                abPath2=abPath.substring(1);
                abPath=abPath2.substring(abPath2.indexOf("/"));
                LinkedList<String> temp=listMap.get(abPath);
                if(temp==null){
                    temp=new LinkedList<String>();
                    temp.add(value);
                    listMap.put(abPath,temp);
                }else{
                    temp.add(value);
                }
            }
        }
    }

    public static String buildXML(String rootNm,Map<String,String[]> kvs) {
        Element root=DocumentHelper.createElement(rootNm);
        Iterator<Map.Entry<String,String[]>> iterator=kvs.entrySet().iterator();
        Map.Entry<String,String[]> entry;
        String nodePath;
        String[] nodeNms;
        int nodeNmCount;
        String[] nodeValues;
        String nodeValue;
        int nvCount;
        int index;
        int index2;
        String tempNm;
        Element previousEle;
        Element tempEle;
        HashMap<String,Element> eleMap=new HashMap<String, Element>();
        StringBuilder nodePathTemp=new StringBuilder("");
        while(iterator.hasNext()){
            entry=iterator.next();
            nodePath=entry.getKey();
            nodeValues=entry.getValue();
            nvCount=nodeValues.length;
            nodeNms=nodePath.substring(1).split("/");
            nodeNmCount=nodeNms.length;
            previousEle=root;
            index2=0;
            index=0;
            nodePathTemp.delete(0,nodePathTemp.length());
            for(;index < nodeNmCount;index++){
                nodePathTemp.append("/");
                tempNm=nodeNms[index];
                nodePathTemp.append(tempNm);
                tempEle=eleMap.get(nodePathTemp);
                if(tempEle==null){
                    if(nodeNmCount-1>index){
                        previousEle=previousEle.addElement(tempNm);
                        eleMap.put(nodePathTemp.toString(),tempEle);
                    }
                }else{
                    previousEle=tempEle;
                }
                if(index==nodeNmCount-1){
                    for(;index2<nvCount;index2++){
                        Element son=previousEle.addElement(tempNm);
                        nodeValue=nodeValues[index2];
                        son.addText(nodeValue);
                    }
                }
            }
        }
        return root.asXML();
    }

    public static void main(String[] args) throws IOException {
        HashMap<String,String[]> map=new HashMap<String, String[]>();
        try {
            parseXmlStr("<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n" +
                            " <books> \n" +
                            "   <book id=\"001\"> \n" +
                            "      <title>Harry Potter</title> \n" +
                            "      <author>J K. Rowling</author> \n" +
                            "   </book> \n" +
                            "   <book id=\"002\"> \n" +
                            "      <title>Learning XML</title> \n" +
                            "      <author>Erik T. Ray</author> \n" +
                            "   </book> \n" +
                            " </books>",
                    map);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Iterator<Map.Entry<String,String[]>> iterator=map.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,String[]> entry=iterator.next();
            System.out.println(entry.getKey());
            String[] vals=entry.getValue();
            for(int index=0;index < vals.length;index++){
                System.out.println(vals[index]);
            }
        }
        System.out.println(buildXML("books",map));
    }
}
