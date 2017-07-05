package nna.base.server;

import nna.base.util.concurrent.AbstractTask;

import java.io.IOException;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;

/**
 * NIOSelector
 * do not care the protocol of app;
 * only responsible for IO Event ;
 * @author NNA-SHUAI
 * @create 2017-06-19 11:37
 **/

public class NIOSelector extends AbstractTask{
    private static Selector selector;
    private static NIOEventProcessor NIOEventProcessor =new NIOEventProcessor();
    private static volatile boolean isInit=false;

    public NIOSelector() {
        super(false);
        addNewTask(this,null,INIT_TASK_TYPE,true, 0L);
    }

    static Selector registerChannel(SelectableChannel selectableChannel,int ops, Object att) throws ClosedChannelException {
        System.out.println("registerChannel start");
        while(!isInit){
            continue;
        }
        selectableChannel.register(selector,ops,att);
        System.out.println("registerChannel Success");
        return selector;
    }

    private Set<SelectionKey> set;
    private int ioEventCount;
    private Iterator<SelectionKey> iterator;
    private SelectionKey selectionKey;
    public void select() {
        try {
            Thread.sleep(10000L);
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

    protected Object doTask(Object attach,int taskType) throws Exception {
        switch (taskType){
            case INIT_TASK_TYPE:
                initSelector(attach);
                select();
        }
        return null;
    }

    private void initSelector(Object attach) throws IOException {
        selector=SelectorProvider.provider().openSelector();
        isInit=true;
    }
}
