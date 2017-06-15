package nna.base.util.concurrent;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author NNA-SHUAI
 * @create 2017-06-15 13:41
 **/

public class Monitor implements Runnable{
    private Worker worker;
    private AtomicLong atomicLong=new AtomicLong();

    public Monitor(Worker worker){
        this.worker=worker;
    }
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
            Long seq=atomicLong.getAndIncrement();
            try {
                Thread.currentThread().sleep(10000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(seq+"-"+worker.getWorkMap().size());
            Map<Long,AbstractTasks> map=worker.getWorkMap();
            Iterator<Map.Entry<Long,AbstractTasks>> iterator=map.entrySet().iterator();
            while(iterator.hasNext()){
                System.out.println(seq+"-"+iterator.next().getValue().getList().length);
            }
        }
    }
}
