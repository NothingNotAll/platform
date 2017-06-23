package nna.base.util.conv2;

import nna.Marco;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author NNA-SHUAI
 * @create 2017-06-22 21:51
 **/

 abstract class AbstractEnAndDeStgy<T> implements Runnable {
     private static final ConcurrentHashMap<Integer,AbstractEnAndDeStgy> strategies=new ConcurrentHashMap<Integer, AbstractEnAndDeStgy>();

     private AtomicInteger tId=new AtomicInteger();
     private Thread[] ts;
     private Object[] queues;
     private ReentrantLock[] tLocks;
     private ReentrantLock addTLock=new ReentrantLock();
     private Integer exeTCount;
     private Integer queueSize;
     private Boolean needSubmit=true;
     private Integer strategyType;
     private AtomicInteger incUnParkTime=new AtomicInteger();
     private AtomicInteger decUnParkTime=new AtomicInteger();
     private volatile boolean threadsInit=false;

     AbstractEnAndDeStgy(
             Integer queueSize,
             Integer exeTCount){
         this.queueSize=queueSize;
         queues=new Object[queueSize];
         this.exeTCount=exeTCount;
         ts=new Thread[exeTCount];
         for(int index=0;index < queueSize;index++){
             initQueue(queues,index);
             if(index < exeTCount){
                 tLocks[index]=new ReentrantLock();
             }
         }
     }

     void init(ExecutorService executorService){
        for(int index=0;index < exeTCount;index++){
            ReentrantLock temp=tLocks[index];
            try{
                temp.lock();
                executorService.submit(this);
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                temp.unlock();
            }
        }
         threadsInit=true;
    }

    public static boolean isSystemLoadPermit() {
        boolean systemLoadPermit=true;

        return systemLoadPermit;
    }

    protected abstract void initQueue(Object[] queues, int index);

    protected abstract void enQueue(TaskWrapper taskWrapper);

    protected abstract TaskWrapper[] deQueue();

    private void unPark(){
        Thread t;
        ReentrantLock unParkLock;
        boolean isLocked=false;
        boolean unParked=false;
        for(int index=0;index < exeTCount;index++){
            t=ts[index];
            if(t!=null&&t.getState()!=Thread.State.RUNNABLE){
                unParkLock=tLocks[index];
                try{
                    if(unParkLock.tryLock()){
                        isLocked=true;
                        LockSupport.unpark(t);
                        unParked=true;
                        return ;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(isLocked){
                        unParkLock.unlock();
                    }
                }
            }
        }
        if(!unParked){
            System.out.println("Unpark not sucess! wait for thread's init");
        }
    }

    void addT(T t,ExecutorService executorService){

    }

     void enQueue(AbstractTask abstractTask,Object att,int taskType,Boolean isNewTToExe,Long delayTime){
         TaskWrapper taskWrapper=new TaskWrapper(abstractTask,att,taskType,isNewTToExe,delayTime);
         enQueue(taskWrapper);
         if(threadsInit){
             if(incUnParkTime.get()>0){
                 unPark();
                 decUnParkTime.getAndDecrement();
                 Integer result=incUnParkTime.get()+decUnParkTime.get();
                 while(result > 0){
                     unPark();
                     decUnParkTime.getAndDecrement();
                     result=incUnParkTime.get()+decUnParkTime.get();
                 }
             }else{
                 incUnParkTime.getAndIncrement();
             }
         }
     }

     public void run(){
         try{
             exeTasks();
         }catch (Exception e){
             e.printStackTrace();
         }
     }

    private void exeTasks() {
        int threadId=tId.getAndIncrement();
        ts[threadId]=Thread.currentThread();
        while(true){
            TaskWrapper[] taskWrappers=deQueue();
            int currentTaskCount=taskWrappers.length;
            if(currentTaskCount==0){
                LockSupport.park();
            }
            int index=0;
            TaskWrapper tempTaskWrapper;
            for(;index < currentTaskCount;index++){
                tempTaskWrapper=taskWrappers[index];
                tempTaskWrapper.doTask();
                if(tempTaskWrapper.getTaskType()==AbstractTask.OVER_TASK_TYPE){
                    removeMonitor(tempTaskWrapper.getAbstractTask());
                    tempTaskWrapper.getAbstractTask().setTaskStatus(AbstractTask.OVER);
                    return;
                }
            }
        }
    }

    private static void removeMonitor(AbstractTask tAbstractEnAndDeStgy) {

    }

    ReentrantLock getAddTLock() {
        return addTLock;
    }

     void setAddTLock(ReentrantLock addTLock) {
        this.addTLock = addTLock;
    }

     Integer getExeTCount() {
        return exeTCount;
    }

     void setExeTCount(Integer exeTCount) {
        this.exeTCount = exeTCount;
    }

     void settLocks(ReentrantLock[] tLocks) {
        this.tLocks = tLocks;
    }

     static AbstractEnAndDeStgy getStrategy(Integer queueSize,Integer exeTCount,Integer strategyType,Long delayTime) {
         AbstractEnAndDeStgy abstractEnAndDeStgy =null;
         switch (strategyType){
             case Marco.NO_SEQ_FIX_SIZE_TASK:
                 abstractEnAndDeStgy=new NoSeqFixSizeStgy(queueSize,exeTCount);
                 break;
             case Marco.NO_SEQ_LINKED_SIZE_TASK:
                 abstractEnAndDeStgy=new NoSeqLinkedStgy(queueSize,exeTCount);
                 break;
             case Marco.SEQ_FIX_SIZE_TASK:
                 abstractEnAndDeStgy=new SeqFixSizeStgy(queueSize,exeTCount);
                 break;
             case Marco.SEQ_LINKED_SIZE_TASK:
                 abstractEnAndDeStgy=new SeqLinkedStgy(queueSize,exeTCount);
                 break;
         }
         abstractEnAndDeStgy.setStrategyType(strategyType);
         if(isSystemLoadPermit()){
             AbstractEnAndDeStgy temp=strategies.putIfAbsent(strategyType,abstractEnAndDeStgy);
             if(temp!=null){
                abstractEnAndDeStgy=temp;
             }
             abstractEnAndDeStgy.setNeedSubmit(true);
         }else{
             abstractEnAndDeStgy.setNeedSubmit(false);
         }
         return abstractEnAndDeStgy;
    }

     Integer getQueueSize() {
        return queueSize;
    }

     void setQueueSize(Integer queueSize) {
        this.queueSize = queueSize;
    }

     Boolean getNeedSubmit() {
        return needSubmit;
    }

     void setNeedSubmit(Boolean needSubmit) {
        this.needSubmit = needSubmit;
    }

     Integer getStrategyType() {
        return strategyType;
    }

     void setStrategyType(Integer strategyType) {
        this.strategyType = strategyType;
    }
}
