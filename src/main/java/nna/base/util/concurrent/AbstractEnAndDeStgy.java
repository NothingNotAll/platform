package nna.base.util.concurrent;

import nna.Marco;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author NNA-SHUAI
 * @create 2017-06-22 21:51
 **/

 abstract class AbstractEnAndDeStgy<T> implements Runnable {
     public static final ConcurrentHashMap<String,AbstractEnAndDeStgy> strategies=new ConcurrentHashMap<String, AbstractEnAndDeStgy>();
     public static final Random random=new Random();

     private AtomicInteger tId=new AtomicInteger();
     private Thread[] ts;
     private Object[] queues;
     private ReentrantLock[] qLocks;
     private ReentrantLock[] tLocks;
     private ReentrantLock addTLock=new ReentrantLock();
     private Integer exeTCount;
     private Integer queueSize;
     private Boolean needSubmit=true;
     private Integer strategyType;
     private AtomicInteger incUnParkTime=new AtomicInteger();
     private AtomicInteger[] incParkTimes;
     private AtomicInteger decUnParkTime=new AtomicInteger();
     private AtomicInteger[] decUnParkTimes;
     private volatile Integer[] tFlags;
     private volatile boolean threadsInit=false;

     AbstractEnAndDeStgy(
             Integer queueSize,
             Integer exeTCount){
         this.queueSize=queueSize;
         queues=new Object[queueSize];
         this.exeTCount=exeTCount;
         ts=new Thread[exeTCount];
         incParkTimes=new AtomicInteger[exeTCount];
         decUnParkTimes=new AtomicInteger[exeTCount];
         tFlags=new Integer[exeTCount];
         tLocks=new ReentrantLock[exeTCount];
         qLocks=new ReentrantLock[queueSize];
         for(int index=0;index < queueSize;index++){
             initQueue(queues,index);
             qLocks[index]=new ReentrantLock();
             if(index < exeTCount){
                 incParkTimes[index]=new AtomicInteger();
                 decUnParkTimes[index]=new AtomicInteger();
                 tLocks[index]=new ReentrantLock();
                 tFlags[index]=new Integer(-1);
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
        boolean systemLoadPermit=false;

        return systemLoadPermit;
    }

    protected abstract void initQueue(Object[] queues, int index);

    protected abstract void enQueue(TaskWrapper taskWrapper);

    protected abstract TaskWrapper[] deQueue();

    private void unPark(){
        Thread t;
        ReentrantLock unParkLock;
        boolean isLocked=false;
        boolean isUnParkExe=false;
        boolean isAllNull=true;
        for(int index=0;index < exeTCount;index++){
            t=ts[index];
            if(t!=null&&t.getState()==Thread.State.WAITING){
                isAllNull=false;
                unParkLock=tLocks[index];
                try{
                    if(unParkLock.tryLock()){
                        isLocked=true;
                        LockSupport.unpark(t);
                        isUnParkExe=true;
                        return ;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(isLocked){
                        unParkLock.unlock();
                        isLocked=false;
                    }
                }
            }
        }
        if(!isUnParkExe){
            int randomInt=0;
            if(exeTCount>1&&isAllNull&&!isUnParkExe){
                randomInt=random.nextInt(exeTCount-1);
            }
            try{
                LockSupport.unpark(ts[randomInt]);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    void addT(T t,ExecutorService executorService){
        int osLength=queues.length;
        Object[] temp=new Object[osLength+1];
        temp[osLength]=t;
        System.arraycopy(queues,0,temp,0,osLength);

    }

     void en(TaskWrapper taskWrapper){
        while(!threadsInit){
            try {
                Thread.sleep(10000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            continue;
        }
        enQueue(taskWrapper);
        unPark();
        decUnParkTime.getAndDecrement();
        Integer result=incUnParkTime.get()+decUnParkTime.get();
        while(result > 0){
            unPark();
            decUnParkTime.getAndDecrement();
            result=incUnParkTime.get()+decUnParkTime.get();
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
        AtomicInteger incInteger=incParkTimes[threadId];
        ts[threadId]=Thread.currentThread();
        tFlags[threadId]=0;//init success;
        Long delayTime=null;
        while(true){
            TaskWrapper[] taskWrappers=deQueue();
            int currentTaskCount=taskWrappers.length;
            if(currentTaskCount==0){
                incUnParkTime.getAndIncrement();
                incInteger.getAndIncrement();
                tFlags[threadId]=1;
                LockSupport.park();
            }else{
                tFlags[threadId]=2;
                int index=0;
                TaskWrapper tempTaskWrapper;
                for(;index < currentTaskCount;index++){
                    incInteger.getAndIncrement();
                    tempTaskWrapper=taskWrappers[index];
                    delayTime=tempTaskWrapper.getDelayTime();
                    sleep(tempTaskWrapper,delayTime);
                    if(tempTaskWrapper.getTaskType()==AbstractTask.OVER_TASK_TYPE){
                        removeMonitor(tempTaskWrapper.getAbstractTask().getgTaskId());
                        tempTaskWrapper.getAbstractTask().setTaskStatus(AbstractTask.OVER);
                        return;
                    }
                }
            }
        }
    }

    private void sleep(TaskWrapper tempTaskWrapper,Long delayTime) {
        delayTime=tempTaskWrapper.getDelayTime();
        if(delayTime!=null&&delayTime>0L){
            try {
                Thread.sleep(delayTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tempTaskWrapper.doTask();
        }else{
            tempTaskWrapper.doTask();
        }
    }

    private static void removeMonitor(Long gTaskId) {
        TaskSchedule.remove(gTaskId);
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

//     static AbstractEnAndDeStgy getStrategy(Integer queueSize,Integer exeTCount,Integer strategyType,Integer executorServiceType) {
//         AbstractEnAndDeStgy abstractEnAndDeStgy =null;
//         switch (strategyType){
//             case Marco.NO_SEQ_FIX_SIZE_TASK:
//                 abstractEnAndDeStgy=new NoSeqFixSizeStgy(queueSize,exeTCount);
//                 break;
//             case Marco.NO_SEQ_LINKED_SIZE_TASK:
//                 abstractEnAndDeStgy=new NoSeqLinkedStgy(queueSize,exeTCount);
//                 break;
//             case Marco.SEQ_FIX_SIZE_TASK:
//                 abstractEnAndDeStgy=new SeqFixSizeStgy(queueSize,exeTCount);
//                 break;
//             case Marco.SEQ_LINKED_SIZE_TASK:
//                 abstractEnAndDeStgy=new SeqLinkedStgy(queueSize,exeTCount);
//                 break;
//         }
//         abstractEnAndDeStgy.setStrategyType(strategyType);
//         if(executorServiceType!=Marco.TIMER_THREAD_TYPE&&!isSystemLoadPermit()){
//             AbstractEnAndDeStgy temp=strategies.putIfAbsent(strategyType+"-"+executorServiceType,abstractEnAndDeStgy);
//             if(temp!=null){
//                abstractEnAndDeStgy=temp;
//                abstractEnAndDeStgy.setNeedSubmit(false);
//             }else{
//                 abstractEnAndDeStgy.setNeedSubmit(true);
//             }
//             return abstractEnAndDeStgy;
//         }
//         abstractEnAndDeStgy.setNeedSubmit(true);
//         return abstractEnAndDeStgy;
//    }

    protected static BlockingQueue<TaskWrapper> getLoadBalance(Object[] queues) {
        LinkedBlockingQueue temp;
        Integer tempCount;
        Integer minCount = null;
        Integer minIndex = null;
        Integer queueSize=queues.length;
        if(queueSize==1){
            return (BlockingQueue<TaskWrapper>)queues[0];
        }
        for(int index=0;index < queueSize;index++){
            temp=(LinkedBlockingQueue<TaskWrapper>)queues[index];
            if(temp.size()==0){
                return temp;
            }else {
                if(minIndex==null){
                    minIndex=index;
                    minCount=temp.size();
                }else{
                    tempCount=temp.size();
                    if(minCount>tempCount){
                        minIndex=index;
                        minCount=tempCount;
                    }
                }
            }
        }
        return (BlockingQueue<TaskWrapper>)queues[minIndex];
    }
    /*
    *
    * */
    protected static LinkedList<TaskWrapper> getBalanceList(AbstractEnAndDeStgy abstractEnAndDeStgy) {
        Object[] queues=abstractEnAndDeStgy.getQueues();
        ReentrantLock[] qLocks=abstractEnAndDeStgy.getQLocks();
        LinkedList<TaskWrapper> consumerList=new LinkedList<TaskWrapper>();
        int queueSize=queues.length;
        BlockingQueue temp;
        int tempSize;
        ReentrantLock qLock;
        boolean locked=false;
        for(int index=0;index < queueSize;index++){
            qLock=qLocks[index];
            try{
                if(qLock.tryLock()){
                    locked=true;
                    temp=(BlockingQueue) queues[index];
                    tempSize=temp.size();
                    temp.drainTo(consumerList,tempSize);
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(locked){
                    qLock.unlock();
                    locked=false;
                }
            }
        }
        return consumerList;
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

     Object[] getQueues() {
        return queues;
     }

     void setQueues(Object[] queues) {
        this.queues = queues;
     }

     Thread[] getTs() {
        return ts;
     }

     void setTs(Thread[] ts) {
        this.ts = ts;
     }

     ReentrantLock[] gettLocks() {
        return tLocks;
     }

     ReentrantLock[] getQLocks() {
        return qLocks;
    }

     void setQLocks(ReentrantLock[] qLocks) {
        this.qLocks = qLocks;
    }
}
