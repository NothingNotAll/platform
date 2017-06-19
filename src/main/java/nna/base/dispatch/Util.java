package nna.base.dispatch;

import nna.base.server.ZeroCopy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

/**
 * @author NNA-SHUAI
 * @create 2017-06-12 15:37
 **/

public class Util {
    private Util(){}

    public static int getSize(ReadableByteChannel channel, int headLength) throws IOException {
        byte[] bytes=readBytes(channel,headLength);
        return Integer.valueOf(new String(bytes));
    }

    public static byte[] readBytes(ReadableByteChannel channel,int readLength) throws IOException {
        ByteBuffer byteBuffer=ByteBuffer.allocate(readLength);
        int readCount=0;
        while(true){
            int temp=channel.read(byteBuffer);
            if(temp==-1||temp==0||readCount==readLength){
                break;
            }else{
                readCount+=temp;
            }
        }
        byte[] bytes=new byte[readCount];
        byteBuffer.get(bytes,0,readLength);
        return byteBuffer.array();
    }

    public static byte[] readBytes(ReadableByteChannel channel) throws IOException {
        ZeroCopy zeroCopy=new ZeroCopy(1024,100,100);
        ByteBuffer byteBuffer=ByteBuffer.allocate(10);
        int readSize;
        while(true){
            readSize=channel.read(byteBuffer);
            if(readSize==-1||readSize==0){
                return zeroCopy.toBytes();
            }else{
                byte[] bytes=new byte[3];
                byteBuffer.get(bytes,0,readSize);
                zeroCopy.add(bytes);
                byteBuffer.clear();
            }
        }
    }
}
