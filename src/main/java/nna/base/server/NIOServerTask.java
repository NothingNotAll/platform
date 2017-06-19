package nna.base.server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.*;

/**
 * For NIO Server Task
 *
 * @author NNA-SHUAI
 * @create 2017-06-19 15:18
 **/

public class NIOServerTask extends AbstractNIOTask {

    private static final int SERVER_READ = SelectionKey.OP_READ;
    private static final int SERVER_WRITE = SelectionKey.OP_WRITE;
    private static final int SERVER_CONNECT=SelectionKey.OP_CONNECT;

    protected ServerSocketChannel channel;
    protected ServerConfig endConfig;
    public NIOServerTask(EndConfig endConfig,
                          Object object,
                          Method method) throws IOException {
        super("NIO Server", 10, endConfig, object, method);
    }

    protected void register() throws IOException {
        NIOSelector.registerChannel(channel, SelectionKey.OP_CONNECT,this);
        channel.bind(socketAddress,(endConfig).getBackLog());
    }

    protected void setChannel() throws IOException {
        channel=ServerSocketChannel.open();
        channel.configureBlocking(false);
    }

    protected Object doTask(int taskType, Object attach) throws IOException, InvocationTargetException, IllegalAccessException {
        switch (taskType){
            case OVER:
                close(attach);
            case SERVER_CONNECT:
                serverRead(attach);
            case SERVER_WRITE:
                serverWrite(attach);
        }
        return null;
    }

    private void serverWrite(Object attach) throws InvocationTargetException, IllegalAccessException {
        method.invoke(object,channel,protocolType,SERVER_WRITE);
    }

    private void serverRead(Object attach) throws IOException, InvocationTargetException, IllegalAccessException {
        method.invoke(object,channel,protocolType,SERVER_READ);
    }

    private void close(Object attach) throws IOException {
        channel.close();
    }
}
