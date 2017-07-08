package nna.base.server;

import nna.base.util.concurrent.AbstractTask;

import java.io.IOException;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

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
    static private AtomicBoolean init=new AtomicBoolean(false);
    static private Object lock=new Object();

    private Long ioEventCountTotal=0L;
    public NIOSelector() throws IOException {
        super(false);
        selector=SelectorProvider.provider().openSelector();
        isInit=true;
        addNewTask(this,null,INIT_TASK_TYPE,true,null);
    }

    static Selector registerChannel(SelectableChannel selectableChannel,int ops, Object att) throws IOException {
        System.out.println("registerChannel start");
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
            //in case that register and select can lead to dead lock;
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while(true){
            try{
                ioEventCount=selector.select();
                Set<SelectionKey> set= selector.selectedKeys();
                iterator=set.iterator();
                while(iterator.hasNext()){
                    selectionKey=iterator.next();
                    NIOEventProcessor.doIOEvent(selectionKey);
                    ioEventCountTotal++;
                    iterator.remove();
                }
                destroy();
            }catch (Exception e){
                e.printStackTrace();
            }
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
                select();
        }
        return null;
    }

    public Long getIoEventCountTotal() {
        return ioEventCountTotal;
    }

    public void setIoEventCountTotal(Long ioEventCountTotal) {
        this.ioEventCountTotal = ioEventCountTotal;
    }
}
