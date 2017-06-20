package nna.base.server;

import java.io.IOException;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * NIOSelector
 * do not care the protocol of app;
 * only responsible for IO Event ;
 * @author NNA-SHUAI
 * @create 2017-06-19 11:37
 **/

public class NIOSelector implements Runnable{
     static Selector selector = null;
     static ExecutorService executorService= Executors.newFixedThreadPool(1);
     static NIOEventProcessor NIOEventProcessor =new NIOEventProcessor();
     private static volatile boolean isInit=false;
    static {
        try {
            selector = SelectorProvider.provider().openSelector();
            executorService.submit(new NIOSelector());
            isInit=true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     static Selector registerChannel(SelectableChannel selectableChannel,int ops, Object att) throws ClosedChannelException {
        while(!isInit){
            continue;
        }
        selectableChannel.register(selector,ops,att);
        return selector;
    }

    private NIOSelector(){}
    private Set<SelectionKey> set;
    private int ioEventCount;
    private Iterator<SelectionKey> iterator;
    private SelectionKey selectionKey;
    public void run() {
        try {
            Thread.sleep(10000L);//for not block
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try{
            while(true){
                ioEventCount=selector.select();
                Set<SelectionKey> set= selector.selectedKeys();
                iterator=set.iterator();
                while(iterator.hasNext()){
                    selectionKey=iterator.next();
                    NIOEventProcessor.doIOEvent(selectionKey);
                    iterator.remove();
                }
                destroy();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {

        }
    }

    private void destroy() {
        set=null;
        ioEventCount=-1;
        iterator=null;
        selectionKey=null;
    }
}
