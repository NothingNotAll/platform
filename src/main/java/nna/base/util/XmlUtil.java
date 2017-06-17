package nna.base.util;


import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
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
        parseNode(root,"/",map);
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
}