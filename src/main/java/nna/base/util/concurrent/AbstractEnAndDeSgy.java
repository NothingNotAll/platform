package nna.base.util.concurrent;


import nna.Marco;
import nna.base.util.SystemUtil;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author NNA-SHUAI
 * @create 2017-06-29 11:06
 **/

abstract class AbstractEnAndDeSgy implements Runnable{
    public static final ConcurrentHashMap<String,AbstractEnAndDeSgy> strategies=new ConcurrentHashMap<String, AbstractEnAndDeSgy>();

    private volatile QueueWrapper[] qws;
    private volatile ThreadWrapper[] tws;
    private AtomicInteger twSeqGen=new AtomicInteger();
    private ReadWriteLock rwLock=new ReentrantReadWriteLock();
    private Boolean needSubmit=true;
    private Integer strategyType;
    private Integer exeTCount;
    private volatile boolean threadsInit=false;

    AbstractEnAndDeSgy(Integer queueSize,
                       Integer exeTCount){
        this.exeTCount=exeTCount>queueSize?queueSize:exeTCount;
        qws=new QueueWrapper[queueSize];
        tws=new ThreadWrapper[exeTCount];
        BlockingQueue<TaskWrapper> temp;
        for(int index=0;index < queueSize;index++){
            temp=initQueue();
            qws[index]=new QueueWrapper(temp);
        }
    }

    protected QueueWrapper defaultEnQueue(TaskWrapper taskWrapper){
        QueueWrapper minLoad=QueueWrapper.enQueue(qws,taskWrapper);
        return minLoad;
    }

    protected TaskWrapper[] defaultDeQueue(){
        return QueueWrapper.deQueues(qws);
    }

    protected abstract BlockingQueue<TaskWrapper> initQueue();

    protected abstract void enQueue(TaskWrapper taskWrapper);

    protected abstract TaskWrapper[] deQueue();

    void init(ExecutorService executorService){
        for(int index=0;index < exeTCount;index++){
                executorService.submit(this);
        }
        threadsInit=true;
    }

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
    static AbstractEnAndDeSgy getStrategy(Integer queueSize,Integer exeTCount,Integer strategyType,Integer executorServiceType) {
        AbstractEnAndDeSgy abstractEnAndDeStgy =null;
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
        if(executorServiceType!=Marco.TIMER_THREAD_TYPE&&!SystemUtil.isSystemLoadPermit()){
            AbstractEnAndDeSgy temp=strategies.putIfAbsent(strategyType+"-"+executorServiceType,abstractEnAndDeStgy);
            if(temp!=null){
                abstractEnAndDeStgy=temp;
                abstractEnAndDeStgy.setNeedSubmit(false);
            }else{
                abstractEnAndDeStgy.setNeedSubmit(true);
            }
            return abstractEnAndDeStgy;
        }
        abstractEnAndDeStgy.setNeedSubmit(true);
        return abstractEnAndDeStgy;
    }

    public Boolean getNeedSubmit() {
        return needSubmit;
    }

    public void setNeedSubmit(Boolean needSubmit) {
        this.needSubmit = needSubmit;
    }

    public Integer getStrategyType() {
        return strategyType;
    }

    public void setStrategyType(Integer strategyType) {
        this.strategyType = strategyType;
    }

    public boolean isThreadsInit() {
        return threadsInit;
    }

    public void setThreadsInit(boolean threadsInit) {
        this.threadsInit = threadsInit;
    }

    public Integer getExeTCount() {
        return exeTCount;
    }

    public void setExeTCount(Integer exeTCount) {
        this.exeTCount = exeTCount;
    }


    public QueueWrapper[] getQws() {
        return qws;
    }

    public void setQws(QueueWrapper[] qws) {
        this.qws = qws;
    }

    public ThreadWrapper[] getTws() {
        return tws;
    }

    public void setTws(ThreadWrapper[] tws) {
        this.tws = tws;
    }
}
