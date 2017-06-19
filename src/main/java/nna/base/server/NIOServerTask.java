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

    private static final Object SERVER_READ = 11;
    private static final int SERVER_WRITE = 12;
    private static final int SERVER_CONNECT=6;

    protected ServerSocketChannel channel;
    public NIOServerTask( EndConfig endConfig, Object object, Method method) throws IOException {
        super("NIO Server", 10, endConfig, object, method);
    }

    protected void register() throws IOException {
        NIOSelector.registerChannel(channel, SelectionKey.OP_CONNECT,this);
        channel.bind(socketAddress,((ServerConfig)endConfig).getBackLog());
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
        method.invoke(object,channel,SERVER_WRITE);
    }

    private void serverRead(Object attach) throws IOException, InvocationTargetException, IllegalAccessException {
        method.invoke(object,channel,SERVER_READ);
    }

    private void close(Object attach) {
    }
}
