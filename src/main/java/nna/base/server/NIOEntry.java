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
    private NIOTask nioTask;

    public NIOEntry(ServerConfig endConfig,Object object,Method method) throws IOException {
        nioTask=new NIOTask(endConfig,object,method);
    }

    public NIOEntry(ClientConfig clientConfig,ByteBuffer requestBytes,Object object,Method method) throws IOException {
        nioTask=new NIOTask(clientConfig,requestBytes,object,method);
    }

    public NIOTask getNioTask() {
        return nioTask;
    }

    public void setNioTask(NIOTask nioTask) {
        this.nioTask = nioTask;
    }

}
