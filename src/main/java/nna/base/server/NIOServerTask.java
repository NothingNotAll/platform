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

    protected ServerConfig endConfig;
    public NIOServerTask(EndConfig endConfig,
                          Object object,
                          Method method) throws IOException {
        super("NIO Server", 10, endConfig, object, method);
    }

    protected void register() throws IOException {
        ServerSocketChannel channel=ServerSocketChannel.open();
        channel.configureBlocking(false);
        setSocketOption(channel);
        NIOSelector.registerChannel(channel, SelectionKey.OP_CONNECT,this);
        channel.bind(socketAddress,(endConfig).getBackLog());
    }


    protected Object doTask(int taskType, Object attach) throws IOException, InvocationTargetException, IllegalAccessException {
        SelectableChannel selectableChannel=(SelectableChannel) attach;
        switch (taskType){
            case OVER:
                close(selectableChannel);
            case SERVER_CONNECT:
                serverRead(selectableChannel);
            case SERVER_WRITE:
                serverWrite(selectableChannel);
        }
        return null;
    }

    private void serverWrite(SelectableChannel channel) throws InvocationTargetException, IllegalAccessException {
        method.invoke(object,channel,protocolType,SERVER_WRITE);
    }

    private void serverRead(SelectableChannel channel) throws IOException, InvocationTargetException, IllegalAccessException {
        method.invoke(object,channel,protocolType,SERVER_READ);
    }

    private void close(SelectableChannel channel) throws IOException {
        channel.close();
    }
}
