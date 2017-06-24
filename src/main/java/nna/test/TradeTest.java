package nna.test;

import nna.base.util.XmlUtil;
import org.dom4j.DocumentException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

/**
 * @author NNA-SHUAI
 * @create 2017-06-21 14:15
 **/

public class TradeTest {
    private static final String IP="127.0.0.1";
    private static final int PORT=8080;
    private static final String XML_ENCODE="UTF-8";
    private static final String XML_DECODE="UTF-8";
    private static final int HEAD_LENTH=8;
    private static final String XML_ROOT_NM = "root";

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, IllegalAccessException {
        HashMap<String,String[]> reqMap= TradeReqMap.map;
        String xmlStr=XmlUtil.buildXML(XML_ROOT_NM,reqMap);
        System.out.println(xmlStr);
        SocketChannel client=SocketChannel.open();
        client.connect(new InetSocketAddress(IP,PORT));
        byte[] bytes=xmlStr.getBytes(XML_ENCODE);
        ByteBuffer byteBuffer=ByteBuffer.wrap(bytes);
        client.write(byteBuffer);
        client.shutdownOutput();
        byte[] length=new byte[HEAD_LENTH];
        ByteBuffer rspStrLength=ByteBuffer.wrap(length);
        int readCount=0;
        while(readCount<8){
            readCount+=client.read(rspStrLength);
        }
        Integer rspSize=new Integer(new String(length));
        byte[] body=new byte[rspSize];
        ByteBuffer rspBB=ByteBuffer.wrap(body);
        readCount=0;
        while(readCount<=rspSize){
            int readCounts=client.read(rspBB);
            if(readCounts!=-1){
                readCount+=readCounts;
            }else{
                break;
            }
        }
        client.shutdownInput();
        client.close();
        String rspStr=new String(body,XML_DECODE);
        HashMap<String,String[]> rspMap=new HashMap<String, String[]>();
        try {
            XmlUtil.parseXmlStr(rspStr,rspMap);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        System.out.println(rspStr);
        System.out.println(rspMap.toString());
    }
}
