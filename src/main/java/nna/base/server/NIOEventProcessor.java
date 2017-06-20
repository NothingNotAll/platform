package nna.base.server;

import java.io.IOException;
import java.nio.channels.*;

/**
 * @author NNA-SHUAI
 * @create 2017-06-19 11:50
 **/

public class NIOEventProcessor {
    private void processAcceptEvent(SelectionKey acceptSK,
                                    SelectableChannel selectableChannel,
                                    AbstractNIOTask att,
                                    int ioEventType) throws IOException {
        //there can do other thing
        ServerSocketChannel serverSocketChannel= (ServerSocketChannel) selectableChannel;
        SocketChannel socketChannel=serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        if(!socketChannel.isConnected()){
            socketChannel.finishConnect();
        }
        Selector selector=att.getSelector();
        socketChannel.register(selector,SelectionKey.OP_READ,att);
    }

    private void processConnectEvent(SelectionKey acceptSK,
                                     SelectableChannel selectableChannel,
                                     AbstractNIOTask att,
                                     int ioEventType){
        SocketChannel socketChannel= (SocketChannel) selectableChannel;
        if(!socketChannel.isConnected()){
            try {
                socketChannel.finishConnect();
                socketChannel.register(att.getSelector(),SelectionKey.OP_WRITE,att);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processReadEvent(SelectionKey acceptSK,
                                  SelectableChannel socketChannel,
                                  AbstractNIOTask att,
                                  int ioEventType){
        nioTask.addNewNIOTask(selectableChannel,ioEventType);
        acceptSK.cancel();
    }

    private void processWriteEvent(SelectionKey acceptSK,
                                   SelectableChannel socketChannel,
                                   AbstractNIOTask att,
                                   int ioEventType){
        nioTask.addNewNIOTask(selectableChannel,ioEventType);
        acceptSK.cancel();
    }

    private SelectableChannel selectableChannel;
    private int ioEventType;
    private AbstractNIOTask nioTask;
    public void doIOEvent(SelectionKey selectionKey) throws IOException {
        ioEventType=selectionKey.interestOps();
        selectableChannel=selectionKey.channel();
//        att=(AbstractNIOTask)selectionKey.attachment();
        nioTask=(AbstractNIOTask)selectionKey.attachment();
        switch (ioEventType){
            case SelectionKey.OP_ACCEPT:
                /*
                * for log record
                * */;
                processAcceptEvent(selectionKey,selectableChannel,nioTask,ioEventType);
                return ;
            case SelectionKey.OP_CONNECT:
                /*
                * then finish connect;
                * then read
                * then write
                * then over
                * for server
                * */;
                processConnectEvent(selectionKey,selectableChannel,nioTask,ioEventType);
                return;
            case SelectionKey.OP_READ:
                /*
                * read immediately
                * */;
                processReadEvent(selectionKey,selectableChannel,nioTask,ioEventType);
                break;
            case SelectionKey.OP_WRITE:
                /*
                * write immediately
                * */;
                processWriteEvent(selectionKey,selectableChannel,nioTask,ioEventType);
                break;
        }
        nioTask.addNewNIOTask(selectableChannel,ioEventType);
    }
}
