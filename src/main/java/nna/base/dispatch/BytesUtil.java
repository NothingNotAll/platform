package nna.base.dispatch;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

/**
 * Created by NNA-SHUAI on 2017/7/9.
 */
public class BytesUtil {
    private BytesUtil(){}

    static void readLineBytes(SocketChannel channel){

    }
    static byte[] readBytes(SocketChannel channel) throws IOException {
        LinkedList<byte[]> bytes=new LinkedList<byte[]>();
        byte[] temp;
        ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
        int totalCount=0;
        int readCount;
        while(true){
            byteBuffer.clear();
            readCount=channel.read(byteBuffer);
            if(readCount==-1){
                break;
            }
            if(readCount>0){
                totalCount+=readCount;
                byteBuffer.flip();
                temp=new byte[readCount];
                byteBuffer.get(temp);
                bytes.add(temp);
            }
        }
        return read(bytes,totalCount);
    }

    static byte[] read(LinkedList<byte[]> bytes,int totalCount){
        byte[] temp;
        int index2;
        int index3=0;
        byte[] byteList=new byte[totalCount];
        int size=bytes.size();
        int length;
        for(int index=0;index < size;index++){
            temp=bytes.get(index);
            length=temp.length;
            index2=0;
            for(;index2<length;index2++){
                byteList[index3++]=temp[index2];
            }
        }
        return  byteList;
    }
}
