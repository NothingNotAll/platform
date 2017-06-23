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
        ServerSocketChannel serverSocketChannel= (ServerSocketChannel) selectableChannel;
        SocketChannel socketChannel=serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        nioTask.addNewNIOTask(socketChannel,ioEventType);
    }

    private void processConnectEvent(SelectionKey acceptSK,
                                     SelectableChannel selectableChannel,
                                     AbstractNIOTask att,
                                     int ioEventType){
    }

    private void processReadEvent(SelectionKey acceptSK,
                                  SelectableChannel selectableChannel,
                                  AbstractNIOTask att,
                                  int ioEventType){

    }

    private void processWriteEvent(SelectionKey acceptSK,
                                   SelectableChannel selectableChannel,
                                   AbstractNIOTask att,
                                   int ioEventType){
    }

    private SelectableChannel selectableChannel;
    private int ioEventType;
    private AbstractNIOTask nioTask;
    public void doIOEvent(SelectionKey selectionKey) throws IOException {
        ioEventType=selectionKey.interestOps();
        selectableChannel=selectionKey.channel();
        nioTask=(AbstractNIOTask)selectionKey.attachment();
        switch (ioEventType){
            case SelectionKey.OP_ACCEPT:
                processAcceptEvent(selectionKey,selectableChannel,nioTask,ioEventType);
                return;
//            case SelectionKey.OP_CONNECT:
//                processConnectEvent(selectionKey,selectableChannel,nioTask,ioEventType);
//                break;
//            case SelectionKey.OP_READ:
//                processReadEvent(selectionKey,selectableChannel,nioTask,ioEventType);
//                break;
//            case SelectionKey.OP_WRITE:
//                processWriteEvent(selectionKey,selectableChannel,nioTask,ioEventType);
//                break;
        }
        System.out.println("nio select event ");
        nioTask.addNewNIOTask(selectableChannel,ioEventType);
    }
}
