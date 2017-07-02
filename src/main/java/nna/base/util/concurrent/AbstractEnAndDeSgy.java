package nna.base.util.concurrent;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author NNA-SHUAI
 * @create 2017-06-29 11:06
 **/

abstract class AbstractEnAndDeSgy implements Runnable{

    private volatile QueueWrapper[] qws;
    private volatile ThreadWrapper[] tws;
    private AtomicInteger twSeqGen=new AtomicInteger();
    private ReadWriteLock rwLock=new ReentrantReadWriteLock();

    protected QueueWrapper deFaultDeQueue(TaskWrapper taskWrapper){
        QueueWrapper minLoad=QueueWrapper.enQueue(qws,taskWrapper);
        return minLoad;
    }

    AbstractEnAndDeSgy(Integer queueSize,
                       Integer exeTCount){
        exeTCount=exeTCount>queueSize?queueSize:exeTCount;
        qws=new QueueWrapper[queueSize];
        tws=new ThreadWrapper[exeTCount];
        BlockingQueue<TaskWrapper> temp;
        for(int index=0;index < queueSize;index++){
            temp=getQueue();
            qws[index]=new QueueWrapper(temp);
        }
    }

    abstract BlockingQueue<TaskWrapper> getQueue();

    abstract void enQueue(TaskWrapper taskWrapper);

    abstract TaskWrapper[] deQueue();

    //dynamic to add worker for high business count;
    void addThreadAndQueue(ExecutorService executorService,BlockingQueue<TaskWrapper> queue){
        int qwsCount=qws.length;
        QueueWrapper[] qwsTemp=new QueueWrapper[qwsCount+1];
        int twsCount=tws.length;
        ThreadWrapper[] twsTemp=new ThreadWrapper[twsCount+1];
        System.arraycopy(qws,0,qwsTemp,0,qwsCount);
        System.arraycopy(tws,0,twsTemp,0,twsCount);
        //locked;
        try{
            rwLock.writeLock().lock();
            qws=qwsTemp;
            tws=twsTemp;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            rwLock.writeLock().unlock();
        }
        executorService.submit(this);
    }

    TaskWrapper[] taskWrappers;
    int taskCount;
    TaskWrapper tempTaskWrapper;
    int index;
    Long delayTime;
    Thread thread;
    public void run() {
        thread=Thread.currentThread();
        tws[twSeqGen.getAndIncrement()]=new ThreadWrapper(qws,thread);
        while(true){
            try{
                rwLock.readLock().lock();
                taskWrappers=QueueWrapper.deQueues(qws);
            }finally {
                rwLock.readLock().unlock();
            }
            taskCount=taskWrappers.length;
            if(taskCount==0){
                for(QueueWrapper queueWrapper:qws){
                    TaskWrapper taskWrapper=QueueWrapper.waitTask(queueWrapper);
                    if(taskWrapper!=null){
                        if(doTask(taskWrapper)){
                            return ;
                        }else{
                            break;
                        }
                    }
                }
            }else{
                index=0;
                for(;index < taskCount;index++){
                    tempTaskWrapper=taskWrappers[index];
                    if(doTask(tempTaskWrapper)){
                        return;
                    }
                }
            }
        }
    }

    private Boolean doTask(TaskWrapper tempTaskWrapper) {
        delayTime=tempTaskWrapper.getDelayTime();
        sleep(tempTaskWrapper,delayTime);
        if(tempTaskWrapper.getTaskType()==AbstractTask.OVER_TASK_TYPE){
            Long gTaskId=tempTaskWrapper.getAbstractTask().getgTaskId();
            removeMonitor(gTaskId);
            tempTaskWrapper.getAbstractTask().setTaskStatus(AbstractTask.OVER);
            return true;
        }
        return false;
    }

    private void removeMonitor(Long gTaskId) {
        TaskSchedule.remove(gTaskId);
    }

    private void sleep(TaskWrapper tempTaskWrapper, Long delayTime) {
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
}
