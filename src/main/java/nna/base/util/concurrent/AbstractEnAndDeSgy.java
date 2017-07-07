package nna.base.util.concurrent;


import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author NNA-SHUAI
 * @create 2017-06-29 11:06
 **/

 class AbstractEnAndDeSgy{
    static final ExecutorService cached= Executors.newCachedThreadPool();
    private static ConcurrentHashMap<Integer,AbstractEnAndDeSgy> workers=new ConcurrentHashMap<Integer, AbstractEnAndDeSgy>();
    private static AtomicInteger workerSeqId=new AtomicInteger();

    private ThreadWrapper threadWrapper;
    private Integer workerId;

    static{
        for(int index=0;index < 1;index++){
            AbstractEnAndDeSgy abstractEnAndDeSgy=new AbstractEnAndDeSgy(workerSeqId.getAndIncrement());
            workers.put(abstractEnAndDeSgy.workerId,abstractEnAndDeSgy);
        }
    }

    static void addWorker(){
        AbstractEnAndDeSgy abstractEnAndDeSgy=new AbstractEnAndDeSgy(workerSeqId.getAndIncrement());
        workers.put(abstractEnAndDeSgy.workerId,abstractEnAndDeSgy);
    }

    static AbstractEnAndDeSgy getMinLoad(Integer workerId){
        if(workerId!=null){
            return workers.get(workerId);
        }
        AbstractEnAndDeSgy abstractEnAndDeSgy=null;
        int loadCount=0;
        int tempCount;
        AbstractEnAndDeSgy tempAED;
        Iterator<Map.Entry<Integer,AbstractEnAndDeSgy>> iterator=workers.entrySet().iterator();
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

    private AbstractEnAndDeSgy(Integer workerId){
        for(int index=0;index <1;index++){
            threadWrapper=new ThreadWrapper(QueueWrapper.getQwMap(),false);
            cached.submit(threadWrapper);
        }
        this.workerId=workerId;
    }

    void initStrategy(Long gTaskId,String gTaskIdStr,boolean isSeq) {
        if(isSeq){
            QueueWrapper.initQws(gTaskIdStr,gTaskId,threadWrapper.getTwSeqId(),1,isSeq);
        }else{
            QueueWrapper.initQws(gTaskIdStr,gTaskId,threadWrapper.getTwSeqId(),15,isSeq);
        }
    }

    void addNewTask(Long gTaskId, TaskWrapper taskWrapper) {
        QueueWrapper.enQueue(gTaskId,taskWrapper);
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
}
