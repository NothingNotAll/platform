package nna.base.util.concurrent;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author NNA-SHUAI
 * @create 2017-06-15 21:53
 **/

public class IOMonitor implements Runnable {
    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMdd-HH:mm:ss:SSS");
    private IOEventProcessor[] IOEventProcessors;
    private Map map;
    private int index=0;
    public void run() {
        while(true){
            System.out.println(simpleDateFormat.format(System.currentTimeMillis())+"-TOTAL TASKS'S WAIT SIZE:"+map.size());
            index=0;
            for(IOEventProcessor IOEventProcessor : IOEventProcessors){
                LinkedBlockingQueue<AbstractIOTasks> queue= IOEventProcessor.getWorkQueue();
//                Iterator<AbstractIOTasks> iterator=queue.iterator();
//                AbstractIOTasks abstractIOTasks;
//                while(iterator.hasNext()){
//                    abstractIOTasks =iterator.next();
//                }
                System.out.println(simpleDateFormat.format(System.currentTimeMillis())+"-No ["+index+"]-BLOCKING QUEUE TASK'S WAIT SIZE:"+queue.size());
                index++;
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

    public void setMap(Map map) {
        this.map = map;
    }
}
