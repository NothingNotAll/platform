package nna.base.server;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author NNA-SHUAI
 * @create 2017-06-19 11:50
 **/

public class IOEventProcessor {
    private void processAcceptEvent(SelectionKey acceptSK,
                                    SocketChannel socketChannel,
                                    NIOEntry att,
                                    int ioEventType){
        //there can do other thing
    }

    private void processConnectEvent(SelectionKey acceptSK,
                                     SocketChannel socketChannel,
                                     NIOEntry att,
                                     int ioEventType){
        //there can do other thing
    }

    private void processReadEvent(SelectionKey acceptSK,
                                  SocketChannel socketChannel,
                                  NIOEntry att,
                                  int ioEventType){
        //there can do other thing
    }

    private void processWriteEvent(SelectionKey acceptSK,
                                   SocketChannel socketChannel,
                                   NIOEntry att,
                                   int ioEventType){
        //there can do other thing
    }

    private SelectableChannel selectableChannel;
    private int ioEventType;
    private NIOEntry att;
    private NIOTask nioTask;
    public void doIOEvent(SelectionKey selectionKey) {
        ioEventType=selectionKey.interestOps();
        selectableChannel=selectionKey.channel();
        att=(NIOEntry)selectionKey.attachment();
        nioTask=att.getNioTask();
        switch (ioEventType){
            case SelectionKey.OP_ACCEPT:
                /*
                * for log record
                * */;
                processAcceptEvent(selectionKey,(SocketChannel) selectableChannel,att,ioEventType);
                break;
            case SelectionKey.OP_CONNECT:
                /*
                * then finish connect;
                * then read
                * then write
                * then over
                * for server
                * */;
                processConnectEvent(selectionKey,(SocketChannel) selectableChannel,att,ioEventType);
                break;
            case SelectionKey.OP_READ:
                /*
                * read immediately
                * */;
                processReadEvent(selectionKey,(SocketChannel) selectableChannel,att,ioEventType);
                break;
            case SelectionKey.OP_WRITE:
                /*
                * write immediately
                * */;
                processWriteEvent(selectionKey,(SocketChannel) selectableChannel,att,ioEventType);
                break;
        }
        nioTask.submitNIOEvent(null,ioEventType);
    }
}