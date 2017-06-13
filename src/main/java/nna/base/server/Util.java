package nna.base.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

/**
 * @author NNA-SHUAI
 * @create 2017-06-12 15:37
 **/

public class Util {
    private Util(){}

    public static int readHead(ReadableByteChannel channel, int headLength) throws IOException {
        byte[] bytes=readBytes(channel,headLength);
        return Integer.valueOf(new String(bytes));
    }

    public static byte[] readBytes(ReadableByteChannel channel,int readLength) throws IOException {
        ByteBuffer byteBuffer=ByteBuffer.allocate(readLength);
        int readCount;
        while(true){
            readCount=channel.read(byteBuffer);
            if(readCount==-1||readCount==0){
                break;
            }
        }
        return byteBuffer.array();
    }

    public static byte[] readBytes(ReadableByteChannel channel) throws IOException {
        ZeroCopy zeroCopy=new ZeroCopy();
        ByteBuffer byteBuffer=ByteBuffer.allocate(10);
        int readSize;
        while(true){
            readSize=channel.read(byteBuffer);
            if(readSize==-1||readSize==0){
                return zeroCopy.toBytes();
            }else{
                zeroCopy.writeBytes(byteBuffer.array());
                byteBuffer.clear();
            }
        }
    }
}
