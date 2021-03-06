package nna.test;

import nna.base.util.XmlUtil;
import org.dom4j.DocumentException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
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
        while(true){
            try{
                HashMap<String,String[]> reqMap= TradeReqMap.map;
                String xmlStr=XmlUtil.buildXML(XML_ROOT_NM,reqMap);
//                System.out.println(xmlStr);
                SocketChannel client=SocketChannel.open();
                client.configureBlocking(false);
                client.connect(new InetSocketAddress(IP,PORT));
                byte[] bytes=xmlStr.getBytes(XML_ENCODE);
                ByteBuffer byteBuffer=ByteBuffer.wrap(bytes);
                if(!client.isConnected()){
                    while(true){
                        try{
                            if(client.finishConnect()){
                                break;
                            };
                        }catch (Exception e){
                            e.printStackTrace();
                            break;
                        }
                    }
                }
                client.write(byteBuffer);
                client.shutdownOutput();
                client.shutdownInput();
                client.close();
                HashMap<String,String[]> rspMap=new HashMap<String, String[]>();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
//                try {
//                    Thread.sleep(10L);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        }
    }
}
