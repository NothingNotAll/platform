package nna.base.server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;

/**
 * For NIOClientTask
 *
 * @author NNA-SHUAI
 * @create 2017-06-19 15:02
 **/

public class NIOClientTask extends AbstractNIOTask {
    private static final int CLIENT_ACCEPT = 4;
    private static final int CLIENT_READ=5;
    private static final int CLIENT_CONNECT=9;
    private static final int CLIENT_WRITE = 10;

    private ByteBuffer requestBytes;
    protected SocketChannel channel;
    public NIOClientTask(ByteBuffer requestBytes,
                         EndConfig endConfig,
                         Object object,
                         Method method) throws IOException {
        super("NIO Client", 10, endConfig,object,method);
        this.requestBytes=requestBytes;
    }


    private void clientRead(Object attach) throws InvocationTargetException, IllegalAccessException {
        method.invoke(object,channel,CLIENT_READ);
    }

    private void clientWrite(Object attach) throws IOException, InvocationTargetException, IllegalAccessException {
        SocketChannel socketChannel=(SocketChannel) channel;
        if(!socketChannel.isConnected()){
            socketChannel.finishConnect();
        }
        method.invoke(object,socketChannel,requestBytes,CLIENT_WRITE);
        NIOSelector.registerChannel(socketChannel, SelectionKey.OP_READ,this);
    }

    private void clientConnect(Object attach) throws IOException {
        SocketChannel socketChannel=(SocketChannel) channel;
        socketChannel.connect(socketAddress);
        NIOSelector.registerChannel(socketChannel,SelectionKey.OP_ACCEPT,this);
    }

    protected Object doTask(int taskType, Object attach) throws IOException, InvocationTargetException, IllegalAccessException {
        switch (taskType){
            case CLIENT_CONNECT:
                clientConnect(attach);
                break;
            case CLIENT_ACCEPT:
                clientWrite(attach);
            case CLIENT_READ:
                clientRead(attach);
            case OVER:
                close(attach);

        }
        return null;
    }

    private void close(Object attach) {
    }

    protected void register() throws ClosedChannelException {
        NIOSelector.registerChannel(channel,SelectionKey.OP_ACCEPT,this);
    }

    protected void setChannel() throws IOException {
        channel=SocketChannel.open();
        channel.configureBlocking(false);
    }
}
