package nna.base.server;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

/**
 * For Protocol
 *
 * @author NNA-SHUAI
 * @create 2017-06-19 14:12
 **/

public class NIOEntry {
    private AbstractNIOTask nioTask;

    public static void loadNIOSelector(){
        System.out.println(NIOSelector.class);
    }
    public NIOEntry(ServerConfig endConfig,Object protocolProcessObject,Method protocolProcessMethod) throws IOException {
        nioTask=new NIOServerTask(endConfig,protocolProcessObject,protocolProcessMethod);
    }

    public NIOEntry(ClientConfig clientConfig,Object protocolProcessObject,Method protocolProcessMethod) throws IOException {
        nioTask=new NIOClientTask(clientConfig,protocolProcessObject,protocolProcessMethod);
    }

    public AbstractNIOTask getNioTask() {
        return nioTask;
    }

    public void setNioTask(AbstractNIOTask nioTask) {
        this.nioTask = nioTask;
    }

}
