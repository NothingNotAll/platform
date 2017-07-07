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
    private static ConcurrentHashMap<Long,ThreadWrapper> twMap=new ConcurrentHashMap<Long, ThreadWrapper>();
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
                    TaskWrapper taskWrapper=doTask(tempTaskWrapper);
                    if(taskWrapper!=null){
                        temp.add(taskWrapper);
                    }else{
                        effectiveCount++;
                    }
                    unParkTimes.getAndIncrement();
                }
                if(taskCount==0){
                    System.out.println("NO."+twSeqId+"-workedCount:"+effectiveCount+"-totalCount:"+taskCount+"-percent:0%");
                }else{
                    System.out.println("NO."+twSeqId+"-workedCount:"+effectiveCount+"-totalCount:"+taskCount+"-percent:"+effectiveCount/taskCount*100+"."+effectiveCount%taskCount+"%");
                }
                if(temp.size()==0){
                    LockSupport.park();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    static ThreadWrapper[] addThreads(Integer addThreadCount){
        if(addThreadCount==null){
            return null;
        }
        ThreadWrapper[] newTws=new ThreadWrapper[addThreadCount];
        for(int index=0;index < addThreadCount;index++){
            newTws[index]=new ThreadWrapper(QueueWrapper.getQwMap(),false);
        }
        return newTws;
    }

    void unParkThread(){
        LockSupport.unpark(thread);
        unParkTimes.getAndIncrement();
    }

    static ThreadWrapper unPark(){
        ThreadWrapper minLoadTw = null;
        Iterator<Map.Entry<Long,ThreadWrapper>> iterator=twMap.entrySet().iterator();
        Map.Entry<Long,ThreadWrapper> entry;
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
        if(minLoadTw!=null){
            minLoadTw.unPark();
            minLoadTw.unParkTimes.getAndIncrement();
        }
        return minLoadTw;
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

    static ConcurrentHashMap<Long, ThreadWrapper> getTwMap() {
        return twMap;
    }
}
