package nna.base.server;

import nna.base.util.concurrent.AbstractIOTask;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.*;

/**
 * not support for long IO event
 *
 * Client Mode: connect Event
 *              send reqStr;
 *              read rspStr;
 *              close;
 * Server Mode: listen Connect Event
 *              finish Connect and read Data and Write Data;
 *              close;
 * @author NNA-SHUAI
 * @create 2017-06-12 0:06
 **/

public class NIOTask extends AbstractIOTask {
    private static final int CLIENT_ACCEPT = 4;
    private static final int CLIENT_READ=5;
    private static final int SERVER_CONNECT=6;
    private static final int CLIENT_CONNECT=9;
    private static final Object CLIENT_WRITE = 10;
    private static final Object SERVER_READ = 11;
    private static final int SERVER_WRITE = 12;

    private ClientConfig clientConfig;
    private ServerConfig serverConfig;
    private Object object;
    private Method method;

    private SocketChannel socketChannel;
    private InetSocketAddress socketAddress;
    private ByteBuffer requestBytes;
    private Selector selector;

    public NIOTask(ClientConfig clientConfig,
                   ByteBuffer requestBytes,
                   Object object,
                   Method method) throws IOException {
        super("NIOClient", 10);
        this.object=object;
        this.method=method;
        this.clientConfig=clientConfig;
        this.requestBytes=requestBytes;
        socketChannel=SocketChannel.open();
        socketChannel.configureBlocking(false);
        String ip=clientConfig.getIp();
        int port=clientConfig.getPort();
        InetSocketAddress socket=new InetSocketAddress(ip,port);
        this.socketAddress=socket;
        setSocketOption(socketChannel,clientConfig);
        NIOServer.registerChannel(socketChannel,SelectionKey.OP_ACCEPT,this);
    }

    public NIOTask(ServerConfig serverConfig,
                   Object object,
                   Method method) throws IOException {
        super("NIOServer",10);
        this.object=object;
        this.method=method;
        this.serverConfig=serverConfig;
        ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        String ip=serverConfig.getIp();
        int port=serverConfig.getPort();
        this.socketAddress=new InetSocketAddress(ip,port);
        setSocketOption(serverSocketChannel,serverConfig);
        NIOServer.registerChannel(serverSocketChannel,SelectionKey.OP_CONNECT,this);
        serverSocketChannel.bind(socketAddress,serverConfig.getBackLog());
    }

    protected void submitNIOEvent(Object attach,int taskType){
        submitEvent(attach,taskType);
    }

    private void setSocketOption(NetworkChannel networkChannel,
                                 EndConfig endConfig) throws IOException {
        SocketOption[] socketOptions=endConfig.getSocketOptions();
        Object[] objects=endConfig.getOptions();
        int count=socketOptions.length;
        SocketOption socketOption;
        Object object;
        for(int index=0;index < count;index++){
            socketOption=socketOptions[index];
            object= objects[index];
            networkChannel.setOption(socketOption,object);
        }
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
            case SERVER_CONNECT:
                serverRead(attach);
            case SERVER_WRITE:
                serverWrite(attach);
        }
        return null;
    }

    private void serverWrite(Object attach) throws InvocationTargetException, IllegalAccessException {
        method.invoke(object,socketChannel,SERVER_WRITE);
    }

    private void serverRead(Object attach) throws IOException, InvocationTargetException, IllegalAccessException {
        if(!socketChannel.isConnected()){
            socketChannel.finishConnect();
        }
        method.invoke(object,socketChannel,SERVER_READ);
    }

    private void close(Object attach) throws IOException {
        socketChannel.close();
    }

    private void clientRead(Object attach) throws InvocationTargetException, IllegalAccessException {
        method.invoke(object,socketChannel,CLIENT_READ);
    }

    private void clientWrite(Object attach) throws IOException, InvocationTargetException, IllegalAccessException {
        if(!socketChannel.isConnected()){
            socketChannel.finishConnect();
        }
        method.invoke(object,socketChannel,requestBytes,CLIENT_WRITE);
        NIOServer.registerChannel(socketChannel,SelectionKey.OP_READ,this);
    }

    private void clientConnect(Object attach) throws IOException {
        socketChannel.connect(socketAddress);
        NIOServer.registerChannel(socketChannel,SelectionKey.OP_ACCEPT,this);
    }
}