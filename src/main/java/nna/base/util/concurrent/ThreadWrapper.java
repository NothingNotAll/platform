package nna.base.util.concurrent;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

/**
 * lead to CPU is too High
 * @author NNA-SHUAI
 * @create 2017-06-24 17:15
 **/

 class ThreadWrapper implements Runnable{
     static ConcurrentHashMap<Long,ThreadWrapper> twMap=new ConcurrentHashMap<Long, ThreadWrapper>();
     private static AtomicLong twSeqGen=new AtomicLong();

     private Thread thread;
     private ConcurrentHashMap<String,QueueWrapper[]> qwMap;
     private Boolean isSeq=false;
     private AtomicLong unParkTimes=new AtomicLong();
     private Long twSeqId;

     ThreadWrapper(ConcurrentHashMap<String,QueueWrapper[]> qwMap,Boolean isSeq){
        this.qwMap=qwMap;
        this.isSeq=isSeq;
        this.twSeqId=twSeqGen.getAndIncrement();
         ThreadWrapper.twMap.put(twSeqId,this);
    }

    /*
    * for minus gc
    * */
    private LinkedList<TaskWrapper> temp=new LinkedList<TaskWrapper>();
    private int taskCount;
    private TaskWrapper tempTaskWrapper;
    private int index;
    private Long delayTime;
    public void run() {
        thread=Thread.currentThread();
        while(true){
            try{
                QueueWrapper.deQueues(temp,qwMap);
                taskCount=temp.size();
                Integer effectiveCount=0;
                for(index=0;index < taskCount;index++){
                    tempTaskWrapper=temp.poll();
                    Object object=doTask(tempTaskWrapper);
                    if(object==null){
                        effectiveCount++;
                    }
                    unParkTimes.getAndIncrement();
                }
                System.out.println("NO."+twSeqId+"-workedCount:"+effectiveCount+"-totalCount:"+taskCount);
                park();
            }catch (Exception e){
                e.fillInStackTrace();
            }
        }
    }

    private void park() {
        LockSupport.park();
    }

    private void unPark(){
        LockSupport.unpark(thread);
        unParkTimes.getAndIncrement();
    }

    static void unPark(ThreadWrapper tw){
        LockSupport.unpark(tw.thread);
    }

    static void unPark(Map<Long,ThreadWrapper> map){
        Iterator<Map.Entry<Long,ThreadWrapper>> iterator=map.entrySet().iterator();
        Map.Entry<Long,ThreadWrapper> entry;
        ThreadWrapper minLoadTw = null;
        ThreadWrapper tempLoadTw;
        Long minWorkTimes=null;
        while(iterator.hasNext()){
            entry=iterator.next();
            if(minLoadTw==null){
                minLoadTw=entry.getValue();
                minWorkTimes=minLoadTw.unParkTimes.get();
            }else{
                tempLoadTw=entry.getValue();
                if(minWorkTimes>tempLoadTw.unParkTimes.get()){
                    minLoadTw=entry.getValue();
                    minWorkTimes=minLoadTw.unParkTimes.get();
                }
            }
        }
        minLoadTw.unPark();
    }

    private TaskWrapper doTask(TaskWrapper tempTaskWrapper) {
        delayTime=tempTaskWrapper.getDelayTime();
        sleep(tempTaskWrapper,delayTime);
        TaskWrapper taskWrapper=tempTaskWrapper.doTask();
        if(tempTaskWrapper.getTaskType()==AbstractTask.OVER_TASK_TYPE){
            MonitorTask.removeMonitor(tempTaskWrapper.getAbstractTask());
            qwMap.remove(tempTaskWrapper.getAbstractTask().getgTaskId());
            tempTaskWrapper.getAbstractTask().setTaskStatus(AbstractTask.OVER);
        }
        return  taskWrapper;
    }

    private void sleep(TaskWrapper tempTaskWrapper, Long delayTime) {
        delayTime=tempTaskWrapper.getDelayTime();
        if(delayTime!=null&&delayTime>0L){
            try {
                Thread.sleep(delayTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public ConcurrentHashMap<String, QueueWrapper[]> getQwMap() {
        return qwMap;
    }

    public void setQwMap(ConcurrentHashMap<String, QueueWrapper[]> qwMap) {
        this.qwMap = qwMap;
    }

    public Boolean getSeq() {
        return isSeq;
    }

    public void setSeq(Boolean seq) {
        isSeq = seq;
    }
}
