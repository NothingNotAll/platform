package nna.base.util.concurrent;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author NNA-SHUAI
 * @create 2017-06-24 17:15
 **/

public class ThreadWrapper {
    private BlockingQueue<TaskWrapper>[] loads;
    private Thread thread;
    private Integer maxLoadCount;//单个线程可以处理的最大负载业务线程数目；null : no limit; default null
    // 这个值应该是作为可配置，依据平均业务线程的负载处理时间以及机器的性能来考虑；
    private ReentrantLock tLock=new ReentrantLock();
    private AtomicLong incParkTime=new AtomicLong();
    private AtomicLong decUnParkTime=new AtomicLong();
    private Integer loadCount;

    void work(){
        if(maxLoadCount==null){
            noLimitWork();
        }else{
            limitWork();
        }
        if(consumer.size()==0){
            incParkTime.getAndIncrement();
            LockSupport.park();
        }
        destroy();
    }

    private void destroy() {
        consumer.clear();
    }

    private void limitWork() {

    }
    BlockingQueue<TaskWrapper> temp;// for min gc;
    LinkedList<TaskWrapper> consumer;//for min gc;
    private void noLimitWork() {
        new LinkedList<TaskWrapper>();
        for(int index=0;index < loadCount;index++){
            temp=loads[index];
            temp.drainTo(consumer);
        }
        consumer(consumer);
    }
    Iterator<TaskWrapper> iterator;
    TaskWrapper taskWrapper;//for min gc;
    Long delayTime;
    private void consumer(LinkedList<TaskWrapper> consumer) {
        iterator=consumer.iterator();
        while(iterator.hasNext()){
            taskWrapper=iterator.next();
            delayTime=taskWrapper.getDelayTime();
            if(delayTime!=null&&delayTime>0L){
                try {
                    Thread.sleep(delayTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                incParkTime.getAndIncrement();
                taskWrapper.doTask();
            }
        }
    }
    boolean unPark(){
        Long previousValue=decUnParkTime.getAndDecrement()-1;
        if(previousValue+incParkTime.get()>=0){
            return false;
        }else{
            LockSupport.unpark(thread);
            return true;
        }
    }
}
