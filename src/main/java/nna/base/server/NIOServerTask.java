package nna.base.server;

import nna.Marco;

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
        startTask( Marco.NO_SEQ_LINKED_SIZE_TASK);
    }


    protected Object doTask(int taskType, Object attach) throws IOException, InvocationTargetException, IllegalAccessException {
        SocketChannel selectableChannel=(SocketChannel) attach;
        switch (taskType){
            case SelectionKey.OP_ACCEPT:
                return serverAccept(selectableChannel);
        }
        return null;
    }

    private Object serverAccept(SocketChannel socketChannel) throws IOException, InvocationTargetException, IllegalAccessException {
        if(!socketChannel.isConnected()){
            socketChannel.finishConnect();
        }
        return method.invoke(object,socketChannel);
    }
}
