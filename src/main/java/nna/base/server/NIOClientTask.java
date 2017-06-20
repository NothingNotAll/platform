package nna.base.server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.*;

/**
 * For NIOClientTask
 *
 * @author NNA-SHUAI
 * @create 2017-06-19 15:02
 **/

public class NIOClientTask extends AbstractNIOTask {
    private static final int CLIENT_ACCEPT = SelectionKey.OP_ACCEPT;
    private static final int CLIENT_READ=SelectionKey.OP_READ;
    private static final int CLIENT_CONNECT=SelectionKey.OP_CONNECT;
    private static final int CLIENT_WRITE = SelectionKey.OP_WRITE;

    private ByteBuffer requestBytes;
    public NIOClientTask(ByteBuffer requestBytes,
                         EndConfig endConfig,
                         Object object,
                         Method method) throws IOException {
        super("NIO Client", 10, endConfig,object,method);
        this.requestBytes=requestBytes;
    }


    private void clientRead(SocketChannel channel) throws InvocationTargetException, IllegalAccessException {
        method.invoke(object,protocolType,channel,CLIENT_READ);
    }

    private void clientWrite(SocketChannel channel) throws IOException, InvocationTargetException, IllegalAccessException {
        SocketChannel socketChannel=channel;
        if(!socketChannel.isConnected()){
            socketChannel.finishConnect();
        }
        method.invoke(object,socketChannel,requestBytes,protocolType,CLIENT_WRITE);
        NIOSelector.registerChannel(socketChannel, SelectionKey.OP_READ,this);
    }

    private void clientConnect(SocketChannel channel) throws IOException {
        SocketChannel socketChannel= channel;
        socketChannel.connect(socketAddress);
        NIOSelector.registerChannel(socketChannel,SelectionKey.OP_ACCEPT,this);
    }

    protected Object doTask(int taskType, Object attach) throws IOException, InvocationTargetException, IllegalAccessException {
        SocketChannel socketChannel=(SocketChannel)attach;
        switch (taskType){
            case CLIENT_CONNECT:
                clientConnect(socketChannel);
                break;
            case CLIENT_ACCEPT:
                clientWrite(socketChannel);
            case CLIENT_READ:
                clientRead(socketChannel);
            case OVER:
                close(socketChannel);

        }
        return null;
    }

    private void close(SocketChannel channel) throws IOException {
        channel.close();
    }

    protected void register() throws IOException {
        SocketChannel channel=SocketChannel.open();
        setSocketOption(channel);
        channel.configureBlocking(false);
        NIOSelector.registerChannel(channel,SelectionKey.OP_CONNECT,this);
        channel.connect(socketAddress);
    }

}
