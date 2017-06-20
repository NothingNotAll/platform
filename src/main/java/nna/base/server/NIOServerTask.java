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
    private ServerSocketChannel serverSocketChannel;

    public NIOServerTask(
                          EndConfig endConfig,
                          Object object,
                          Method method) throws IOException {
        super("NIO Server",10, endConfig, object, method);
    }

    protected void register() throws IOException {
        this.serverSocketChannel=ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        setSocketOption(serverSocketChannel);
        this.selector=NIOSelector.registerChannel(serverSocketChannel, SelectionKey.OP_ACCEPT,this);
        serverSocketChannel.bind(socketAddress,((ServerConfig)endConfig).getBackLog());
        System.out.println("nio Server init @"+endConfig.getIp()+":"+endConfig.getPort());
        startTask(null,false);
    }


    protected Object doTask(int taskType, Object attach) throws IOException, InvocationTargetException, IllegalAccessException {
        SocketChannel selectableChannel=(SocketChannel) attach;
        switch (taskType){
            case OVER:
                close(selectableChannel);
            case SERVER_READ:
                serverRead(selectableChannel);
            case SERVER_WRITE:
                serverWrite(selectableChannel);
        }
        return null;
    }

    private void serverWrite(SocketChannel channel) throws InvocationTargetException, IllegalAccessException {
        method.invoke(object,channel,protocolType,SERVER_WRITE);
        addNewNIOTask(channel,OVER);
    }

    private void serverRead(SocketChannel channel) throws IOException, InvocationTargetException, IllegalAccessException {
        method.invoke(object,channel,protocolType,SERVER_READ);
        System.out.println("read...");
        channel.register(selector,SelectionKey.OP_WRITE,this);
    }

    private void close(SocketChannel channel) throws IOException {
        channel.close();
    }
}
