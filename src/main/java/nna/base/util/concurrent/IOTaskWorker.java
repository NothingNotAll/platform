package nna.base.util.concurrent;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * 以 private AtomicLong inc=new AtomicLong(0L);
 private AtomicLong add=new AtomicLong(0L);//avoid ABA bug
 private ReentrantLock lock=new ReentrantLock();
 solve the ABA problem . AND
  WE CAN DO IT ;
 * @author NNA-SHUAI
 * @create 2017-06-18 14:54
 **/



 class IOTaskWorker implements Runnable{

    private AbstractIOTasks tasks;
    private volatile Thread thread;
    private AtomicLong inc=new AtomicLong(0L);
    private AtomicLong add=new AtomicLong(0L);//avoid ABA bug
    private ReentrantLock lock=new ReentrantLock();
    private ReentrantLock tInitLock=new ReentrantLock();
    IOTaskWorker(AbstractIOTasks abstractIOTasks){
        this.tasks=abstractIOTasks;
    }
    public void run() {
        if(tInitLock.tryLock()){
            thread=Thread.currentThread();
            try{
                while(true){
                    if(!tasks.doTasks()){
                        park();
                    }else{
                        break;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            unPark();
        }
    }

    private void park() {
            LockSupport.park();
            add.getAndIncrement();
    }

    private void unPark(){
        while(thread==null){
            continue;
        }
        try{
            lock.lock();//性能阻塞点 避免 ABA问题 。
            LockSupport.unpark(thread);
            inc.getAndDecrement();
            Long result=inc.get()+add.get();
            while(result > 0){
                LockSupport.unpark(thread);
                inc.getAndDecrement();
                result=inc.get()+add.get();
            }
        }finally {
            lock.unlock();
        }
    }

    AbstractIOTasks getTasks() {
        return tasks;
    }

    void setTasks(AbstractIOTasks tasks) {
        this.tasks = tasks;
    }
}