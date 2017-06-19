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
    protected SocketChannel channel;
    protected ClientConfig endConfig;
    public NIOClientTask(ByteBuffer requestBytes,
                         EndConfig endConfig,
                         Object object,
                         Method method) throws IOException {
        super("NIO Client", 10, endConfig,object,method);
        this.requestBytes=requestBytes;
    }


    private void clientRead(Object attach) throws InvocationTargetException, IllegalAccessException {
        method.invoke(object,protocolType,channel,CLIENT_READ);
    }

    private void clientWrite(Object attach) throws IOException, InvocationTargetException, IllegalAccessException {
        SocketChannel socketChannel=channel;
        if(!socketChannel.isConnected()){
            socketChannel.finishConnect();
        }
        method.invoke(object,socketChannel,requestBytes,protocolType,CLIENT_WRITE);
        NIOSelector.registerChannel(socketChannel, SelectionKey.OP_READ,this);
    }

    private void clientConnect(Object attach) throws IOException {
        SocketChannel socketChannel= channel;
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

    private void close(Object attach) throws IOException {
        channel.close();
    }

    protected void register() throws ClosedChannelException {
        NIOSelector.registerChannel(channel,SelectionKey.OP_ACCEPT,this);
    }

    protected void setChannel() throws IOException {
        channel=SocketChannel.open();
        channel.configureBlocking(false);
    }
}
