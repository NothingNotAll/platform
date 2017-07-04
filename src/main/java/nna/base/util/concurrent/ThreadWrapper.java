package nna.base.util.concurrent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author NNA-SHUAI
 * @create 2017-06-24 17:15
 **/

 class ThreadWrapper implements Runnable{
     static AtomicBoolean isNoSeqInit=new AtomicBoolean(false);
     static AtomicBoolean isSeqInit=new AtomicBoolean(false);
     static ConcurrentHashMap<Long,ThreadWrapper> noSeqTwMap=new ConcurrentHashMap<Long, ThreadWrapper>();
     static ConcurrentHashMap<Long,ThreadWrapper> seqTwMap=new ConcurrentHashMap<Long, ThreadWrapper>();
     static AtomicLong noSeqTwSeqGen=new AtomicLong();
     static AtomicLong seqTwSeqGen=new AtomicLong();

     private Thread thread;
     private ConcurrentHashMap<String,QueueWrapper[]> qwMap;
     private Boolean isSeq=false;

     ThreadWrapper(ConcurrentHashMap<String,QueueWrapper[]> qwMap){
        this.qwMap=qwMap;
    }

    /*
    * for minus gc
    * */
    TaskWrapper[] taskWrappers;
    int taskCount;
    TaskWrapper tempTaskWrapper;
    int index;
    Long delayTime;
    public void run() {
        thread=Thread.currentThread();
        while(true){
            taskWrappers=QueueWrapper.deQueues(qwMap);
            taskCount=taskWrappers.length;
            if(taskCount==0){
                TaskWrapper taskWrapper=QueueWrapper.waitTask(qwMap);
                doTask(taskWrapper);
            }else{
                index=0;
                for(;index < taskCount;index++){
                    tempTaskWrapper=taskWrappers[index];
                    doTask(tempTaskWrapper);
                }
            }
        }
    }

    private Boolean doTask(TaskWrapper tempTaskWrapper) {
        if(tempTaskWrapper.equals(TaskWrapper.DO_NOTHING)){
            return false;
        }
        delayTime=tempTaskWrapper.getDelayTime();
        sleep(tempTaskWrapper,delayTime);
        if(tempTaskWrapper.getTaskType()==AbstractTask.OVER_TASK_TYPE){
            MonitorTask.removeMonitor(tempTaskWrapper.getAbstractTask());
            qwMap.remove(tempTaskWrapper.getAbstractTask().getgTaskId());
            tempTaskWrapper.getAbstractTask().setTaskStatus(AbstractTask.OVER);
            return true;
        }
        return false;
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
        tempTaskWrapper.doTask();
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
