package nna.test;

import nna.base.util.XmlUtil;
import nna.test.trade.TestDemo;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.Field;
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
    private static final int PORT=80;
    private static final String XML_ENCODE="UTF-8";
    private static final String XML_DECODE="UTF-8";
    private static final int HEAD_LENTH=8;
    private static final String XML_ROOT_NM = "root";

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        Class reqInterface= TestDemo.class;
        Field[] reqCols=reqInterface.getFields();
        int count=reqCols.length;
        HashMap<String,String[]> reqMap=new HashMap<String, String[]>(count);
        String colNm;
        String colVal;
        Field field;
        for(int index=0;index < count;index++){
            field=reqCols[index];
            Class fieldClazz=Field.class;
            colNm=field.getName();
            String[] tempColVal=reqMap.get(colNm);
            if(tempColVal!=null){
                String[] newTempVal=new String[tempColVal.length+1];
                System.arraycopy(tempColVal,0,newTempVal,0,tempColVal.length);
                newTempVal[tempColVal.length]="";
                reqMap.put(colNm,newTempVal);
            }else{
                reqMap.put(colNm,new String[]{});
            }
        }
        String xmlStr=XmlUtil.buildXML(XML_ROOT_NM,reqMap);
        SocketChannel client=SocketChannel.open();
        client.connect(new InetSocketAddress(IP,PORT));
        byte[] bytes=xmlStr.getBytes(XML_ENCODE);
        ByteBuffer byteBuffer=ByteBuffer.wrap(bytes);
        client.write(byteBuffer);
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
            readCount+=client.read(rspBB);
        }
        String rspStr=new String(body,XML_DECODE);
        HashMap<String,String[]> rspMap=new HashMap<String, String[]>();
        XmlUtil.parseXmlStr(rspStr,rspMap);
        System.out.println(rspStr);
        System.out.println(rspMap.toString());
    }
}
