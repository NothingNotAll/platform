package nna.base.util.concurrent;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author NNA-SHUAI
 * @create 2017-06-15 21:53
 **/

public class IOMonitor implements Runnable {
    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMdd-HH:mm:ss:SSS");
    private IOEventProcessor[] IOEventProcessors;
    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run() {
        while(true){
            for(IOEventProcessor IOEventProcessor : IOEventProcessors){
                System.out.println(simpleDateFormat.format(System.currentTimeMillis())+"-"+ IOEventProcessor.getLoadNo()+"-"+ IOEventProcessor.getWorkQueue().size());
                LinkedBlockingQueue<AbstractIOTasks> queue= IOEventProcessor.getWorkQueue();
                Iterator<AbstractIOTasks> iterator=queue.iterator();
                AbstractIOTasks abstractIOTasks;
                while(iterator.hasNext()){
                    abstractIOTasks =iterator.next();
                }
            }
            try {
                Thread.currentThread().sleep(3000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("--");
        }
    }

    public IOEventProcessor[] getIOEventProcessors() {
        return IOEventProcessors;
    }

    public void setIOEventProcessors(IOEventProcessor[] IOEventProcessors) {
        this.IOEventProcessors = IOEventProcessors;
    }
}
