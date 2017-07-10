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
     private AtomicLong parkTimes=new AtomicLong();
     private Long twSeqId;
     private volatile Long noMeanfulTimes=0L;
     private volatile Long meanfulTimes=0L;
     private volatile Long totalTimes=0L;

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
    private int taskCount=0;
    private int thisEffectiveCount=0;
    private int thisNoEffectiveCount=0;
    private int totalTaskCount=0;
    private TaskWrapper tempTaskWrapper;
    private int index;
    private Long delayTime;
    private Integer effectiveCount=0;
    private Integer noEffectiveCount=0;
    public void run() {
        thread=Thread.currentThread();
        while(true){
            try{
                QueueWrapper.deQueues(temp,qwMap,twSeqId);
                taskCount=temp.size();thisNoEffectiveCount=0;thisEffectiveCount=0;
                totalTaskCount+=taskCount;
                parkTimes.getAndAdd(taskCount);
                for(index=0;index < taskCount;index++){
                    tempTaskWrapper=temp.poll();
                    TaskWrapper taskWrapper=doTask(tempTaskWrapper);
                    if(taskWrapper!=null){
                        temp.add(taskWrapper);
                        noEffectiveCount++;
                        thisNoEffectiveCount++;
                    }else{
                        effectiveCount++;
                        thisEffectiveCount++;
                    }
                }
                totalTimes++;
                if(taskCount==0){
                    noMeanfulTimes++;
                }else{
                    meanfulTimes++;
                }
                if(temp.size()==0){
                    LockSupport.park();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    static String monitorEffective(ThreadWrapper tw){
        if(tw.taskCount==0){
            return "NO."+tw.twSeqId+" NotWorkedCount:0 totalCount:0 noEffectivePercent:0%\r\nWorkedCount:0 totalCount:0 noEffectivePercent:0%";
        }
        String noEffective="NO."+tw.twSeqId+" NotWorkedCount:"+tw.noEffectiveCount+" totalCount:"+tw.totalTaskCount+" noEffectivePercent:"+tw.noEffectiveCount*100/tw.totalTaskCount+"."+tw.noEffectiveCount%tw.totalTaskCount+"%";
        String effective="NO."+tw.twSeqId+" WorkedCount:"+tw.effectiveCount+" totalCount:"+tw.totalTaskCount+" effectivePercent:"+tw.effectiveCount*100/tw.totalTaskCount+"."+tw.effectiveCount%tw.totalTaskCount+"%";
        String thisNoEffective="NO."+tw.twSeqId+" NotWorkedCount:"+tw.thisNoEffectiveCount+" totalCount:"+tw.taskCount+" noEffectivePercent:"+tw.thisNoEffectiveCount*100/tw.taskCount+"."+tw.thisNoEffectiveCount%tw.taskCount+"%";
        String thisEffective="NO."+tw.twSeqId+" WorkedCount:"+tw.thisEffectiveCount+" totalCount:"+tw.taskCount+" effectivePercent:"+tw.thisEffectiveCount*100/tw.taskCount+"."+tw.thisEffectiveCount%tw.taskCount+"%";
        return "HISTORY_TOTAL:\r\n"+noEffective+"\r\n"+effective+"\r\nTHIS_TIME:\r\n"+thisNoEffective+"\r\n"+thisEffective;
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

    void unParkThread(Long twId){
        if(twId!=null){
            ThreadWrapper tw=twMap.get(twId);
            LockSupport.unpark(tw.thread);
            return ;
        }else{
            unParkTimes.getAndIncrement();
            if(unParkTimes.get()>= parkTimes.get()){
                LockSupport.unpark(thread);
            }
        }
    }

    static int getLoadCount(ThreadWrapper threadWrapper){
       ConcurrentHashMap<String,QueueWrapper[]> map=threadWrapper.getQwMap();
       Iterator<Map.Entry<String,QueueWrapper[]>> iterator=map.entrySet().iterator();
       Map.Entry<String,QueueWrapper[]> entry;
       int loadCount=0;
       while(iterator.hasNext()){
           entry=iterator.next();
           QueueWrapper[] qws=entry.getValue();
           for(QueueWrapper qw:qws){
               loadCount+=qw.getQueue().size();
           }
       }
       return loadCount;
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

    static String monitor(ThreadWrapper t){
        String int1=String.valueOf(t.meanfulTimes*100L/t.totalTimes);
        String int2=String.valueOf(t.noMeanfulTimes*100L/t.totalTimes);
        String yu1=String.valueOf(t.meanfulTimes%t.totalTimes);
        String yu2=String.valueOf(t.noMeanfulTimes%t.totalTimes);
        String percent1=int1+"."+yu1+"%";
        String percent2=int2+"."+yu2+"%";
        Long index=t.twSeqId;
        String str="No."+index+".thread.STATE:"+t.thread.getState()+"-meanfulPercent:"+percent1+"-"+"noMeanfulPercent:"+percent2;
        return str;
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

    public Long getNoMeanfulTimes() {
        return noMeanfulTimes;
    }

    public void setNoMeanfulTimes(Long noMeanfulTimes) {
        this.noMeanfulTimes = noMeanfulTimes;
    }

    public Long getMeanfulTimes() {
        return meanfulTimes;
    }

    public void setMeanfulTimes(Long meanfulTimes) {
        this.meanfulTimes = meanfulTimes;
    }

    public Long getTotalTimes() {
        return totalTimes;
    }

    public void setTotalTimes(Long totalTimes) {
        this.totalTimes = totalTimes;
    }

    public Long getTwSeqId() {
        return twSeqId;
    }

    public void setTwSeqId(Long twSeqId) {
        this.twSeqId = twSeqId;
    }
}
