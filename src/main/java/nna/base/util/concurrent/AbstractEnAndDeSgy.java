package nna.base.util.concurrent;


import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author NNA-SHUAI
 * @create 2017-06-29 11:06
 **/

 class AbstractEnAndDeSgy{
    static AtomicLong incThreadCount=new AtomicLong();
    static AtomicLong decThreadCount=new AtomicLong();
    static final ExecutorService cached= Executors.newCachedThreadPool();
    private static ConcurrentHashMap<Integer,AbstractEnAndDeSgy> seqWorkers=new ConcurrentHashMap<Integer, AbstractEnAndDeSgy>();
    private static ConcurrentHashMap<Integer,AbstractEnAndDeSgy> noSeqWorkers=new ConcurrentHashMap<Integer, AbstractEnAndDeSgy>();
    private static AtomicInteger workerSeqId=new AtomicInteger();

    private Boolean isSeq;//null true false
    private ThreadWrapper threadWrapper;
    private Integer workerId;

    static{
        for(int index=0;index < 1;index++){
            addWorker(false);
        }
        for(int index=0;index < 1;index++){
            addWorker(true);
        }
        new MonitorTask();
    }

    static void addWorker(boolean isSeq){
        new AbstractEnAndDeSgy(isSeq);
    }

    static AbstractEnAndDeSgy getMinLoad(boolean isSeq,Integer workerId){
        if(workerId!=null){
            if(isSeq){
                return seqWorkers.get(workerId);
            }else{
                return noSeqWorkers.get(workerId);
            }
        }else{
            if(isSeq){
                return getMinLoad(seqWorkers);
            }else{
                return getMinLoad(noSeqWorkers);
            }
        }
    }

    private static AbstractEnAndDeSgy getMinLoad(ConcurrentHashMap<Integer,AbstractEnAndDeSgy> map){
        AbstractEnAndDeSgy abstractEnAndDeSgy=null;
        int loadCount=0;
        int tempCount;
        AbstractEnAndDeSgy tempAED;
        Iterator<Map.Entry<Integer,AbstractEnAndDeSgy>> iterator=map.entrySet().iterator();
        Map.Entry<Integer,AbstractEnAndDeSgy> entry;
        while(iterator.hasNext()){
            entry=iterator.next();
            if(abstractEnAndDeSgy==null){
                abstractEnAndDeSgy=entry.getValue();
                loadCount=ThreadWrapper.getLoadCount(abstractEnAndDeSgy.threadWrapper);
            }else{
                tempAED=entry.getValue();
                tempCount=ThreadWrapper.getLoadCount(tempAED.threadWrapper);
                if(tempCount<loadCount){
                    loadCount=tempCount;
                    abstractEnAndDeSgy=tempAED;
                }
            }
        }
        return abstractEnAndDeSgy;
    }

    private AbstractEnAndDeSgy(Boolean isSeq){
        this.workerId=workerSeqId.getAndIncrement();
        this.isSeq=isSeq==null?false:isSeq;
        if(!isSeq){
            noSeqWorkers.put(workerId,this);
        }else{
            seqWorkers.put(workerId,this);
        }
        for(int index=0;index <1;index++){
            threadWrapper=new ThreadWrapper(QueueWrapper.getQwMap(),isSeq);
            cached.submit(threadWrapper);
        }
    }

    void initStrategy(Long gTaskId,String gTaskIdStr,boolean isSeq) {
        Integer queueCount=16;
        if(isSeq){
            queueCount=1;
        }
        QueueWrapper.initQws(gTaskIdStr,gTaskId,threadWrapper.getTwSeqId(),queueCount,isSeq);
    }

    void addNewTask(Long gTaskId, TaskWrapper taskWrapper) {
        QueueWrapper.enQueue(gTaskId,taskWrapper);
        threadWrapper.unParkThread(taskWrapper.getAbstractTask().getTwId());
    }

    void addNewTask(Long gTaskId,Integer queueIndex,TaskWrapper taskWrapper) {
        QueueWrapper.enQueue(gTaskId,queueIndex,taskWrapper);
        threadWrapper.unParkThread(taskWrapper.getAbstractTask().getTwId());
    }

    Integer getWorkerId() {
        return workerId;
    }

    void setWorkerId(Integer workerId) {
        this.workerId = workerId;
    }

    ThreadWrapper getThreadWrapper() {
        return threadWrapper;
    }

    void setThreadWrapper(ThreadWrapper threadWrapper) {
        this.threadWrapper = threadWrapper;
    }

    public Boolean getSeq() {
        return isSeq;
    }

    public void setSeq(Boolean seq) {
        isSeq = seq;
    }
}
