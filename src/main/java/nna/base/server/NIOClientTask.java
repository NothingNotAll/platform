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

 class NIOClientTask extends AbstractNIOTask {
    private static final int CLIENT_CONNECT=SelectionKey.OP_CONNECT;

    public NIOClientTask(
                         EndConfig endConfig,
                         Object object,
                         Method method) throws IOException {
        super(endConfig,object,method);
    }

    private Object clientConnect(SocketChannel channel) throws IOException, InvocationTargetException, IllegalAccessException {
        if(!channel.isConnected()){
            channel.finishConnect();
        }
        return method.invoke(object,channel);
    }

    protected void register() throws IOException {
        SocketChannel channel=SocketChannel.open();
        setSocketOption(channel);
        channel.configureBlocking(false);
        this.selector=NIOSelector.registerChannel(channel,SelectionKey.OP_CONNECT,this);
        channel.connect(socketAddress);
    }

    public Object doTask(Object att, int taskType) throws Exception {
        SocketChannel socketChannel=(SocketChannel)att;
        switch (taskType){
            case CLIENT_CONNECT:
                clientConnect(socketChannel);
                break;
        }
        return null;
    }
}
