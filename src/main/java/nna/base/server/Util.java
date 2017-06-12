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
        int tempSize=0;
        int readCount;
        while(tempSize < readLength){
            readCount=channel.read(byteBuffer);
            if(readCount==-1){
                break;
            }
            tempSize+=readCount;
        }
        return byteBuffer.array();
    }
}
