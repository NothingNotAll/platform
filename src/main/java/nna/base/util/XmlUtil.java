package nna.base.util;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author NNA-SHUAI
 * @create 2017-06-16 17:34
 **/

public class XmlUtil {
    private static final DocumentBuilderFactory documentBuilderFactory=DocumentBuilderFactory.newInstance();
    public static String parseXmlStr(String xmlStr,Map<String,String[]> map) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder documentBuilder=documentBuilderFactory.newDocumentBuilder();
        Node root=documentBuilder.parse(new InputSource(new StringReader(xmlStr)));

        parseNode(root,"",map);
        return root.getNodeName();
    }
    public static void parseNode(Node node, String path, Map<String,String[]> kvs){
        if(node.hasChildNodes()){
            Node tempNode;
            String[] vs;
            String[] temp;
            int length;
            String tempNodeNm;
            NodeList list=node.getChildNodes();
            int size=list.getLength();
            for(int index=0;index < size;index++){
                tempNode=list.item(index);
                tempNodeNm=tempNode.getNodeName();
                if(!tempNode.hasChildNodes()){
                    path+="/";
                    path+=tempNodeNm;
                    vs=kvs.get(path);
                    if(vs==null){
                        kvs.put(path,new String[]{tempNode.getNodeValue()});
                    }else{
                        length=vs.length;
                        temp=new String[length];
                        System.arraycopy(vs,0,temp,0,length);
                        temp[length]=tempNode.getNodeValue();
                        kvs.put(path,temp);
                    }
                }else{
                    parseNode(tempNode,path+"/"+tempNodeNm,kvs);
                }
            }
        }
    }

    public static String buildXML(String rootNm,Map<String,String[]> kvs) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory
                .newInstance();
        Document document=factory.newDocumentBuilder().newDocument();
        Element root=document.createElement(rootNm);
        StringBuilder stringBuilder=new StringBuilder();
        Iterator<Map.Entry<String,String[]>> iterator=kvs.entrySet().iterator();
        Map.Entry<String,String[]> entry;
        String keyNm;
        String[] vS;
        String[] pathNodeNms;
        StringBuilder nodeNm = new StringBuilder("");
        Node node;
        int pathNodeNmsCount;
        int vSize;
        String v;
        Node previousNode;
        HashMap<String,Node> map=new HashMap<String, Node>();
        while(iterator.hasNext()){
            entry=iterator.next();
            keyNm=entry.getKey();
            vS=entry.getValue();
            pathNodeNms=keyNm.split("[/|_]");
            pathNodeNmsCount=pathNodeNms.length-1;
            nodeNm.delete(0,nodeNm.length()-1);
            for(int index=0;index <= pathNodeNmsCount;index++){
                nodeNm.append("/");
                nodeNm.append(pathNodeNms[index]);
                node=map.get(nodeNm);
                if(node==null){

                }
                previousNode=node;
                if(index==pathNodeNmsCount){
                    vSize=vS.length;
                    for(index=0;index < vSize;index++){
                        v=vS[index];

                    }
                }
            }
        }
        return stringBuilder.toString();
    }
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        HashMap<String,String[]> map=new HashMap<String, String[]>();
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
        Iterator<Map.Entry<String,String[]>> iterator=map.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,String[]> entry=iterator.next();
            System.out.println(entry.getKey());
            String[] vals=entry.getValue();
            for(int index=0;index < vals.length;index++){
                System.out.println(vals[index]);
            }
        }
    }
}
