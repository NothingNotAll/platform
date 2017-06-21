package nna.base.server;

import nna.Marco;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.*;

/**
 * For NIOClientTask
 *
 * @author NNA-SHUAI
 * @create 2017-06-19 15:02
 **/

public class NIOClientTask extends AbstractNIOTask {
    private static final int CLIENT_CONNECT=SelectionKey.OP_CONNECT;

    public NIOClientTask(
                         EndConfig endConfig,
                         Object object,
                         Method method) throws IOException {
        super("NIO Client", 10, endConfig,object,method);
        startTask( Marco.NO_SEQ_LINKED_SIZE_TASK);
    }

    private Object clientConnect(SocketChannel channel) throws IOException, InvocationTargetException, IllegalAccessException {
        if(!channel.isConnected()){
            channel.finishConnect();
        }
        return method.invoke(object,channel);
    }

    protected Object doTask(int taskType, Object attach) throws IOException, InvocationTargetException, IllegalAccessException {
        SocketChannel socketChannel=(SocketChannel)attach;
        switch (taskType){
            case CLIENT_CONNECT:
                clientConnect(socketChannel);
                break;
        }
        return null;
    }

    protected void register() throws IOException {
        SocketChannel channel=SocketChannel.open();
        setSocketOption(channel);
        channel.configureBlocking(false);
        this.selector=NIOSelector.registerChannel(channel,SelectionKey.OP_CONNECT,this);
        channel.connect(socketAddress);
    }

}
