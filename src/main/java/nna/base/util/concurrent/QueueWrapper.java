package nna.base.util.concurrent;


import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author NNA-SHUAI
 * @create 2017-06-29 10:49
 **/

 class QueueWrapper {
    private static final ConcurrentHashMap<Long,String> gTaskIdToTaskNmMap=new ConcurrentHashMap<Long, String>();
    private static final ConcurrentHashMap<String,QueueWrapper[]> qwMap=new ConcurrentHashMap<String, QueueWrapper[]>();

    private BlockingQueue<TaskWrapper> queue;
    private ReentrantLock qLock;
    private volatile Integer deCount=0;//the total Count of been worked;
    private Long twId;
    private Boolean isSeq;

    QueueWrapper(BlockingQueue<TaskWrapper> queue){
        this.queue=queue;
        this.qLock=new ReentrantLock();
    }

    static QueueWrapper enQueue(Long gTaskId,TaskWrapper taskWrapper){
        String gTaskNm=QueueWrapper.gTaskIdToTaskNmMap.get(gTaskId);
        QueueWrapper[] qws=QueueWrapper.qwMap.get(gTaskNm);
        ArrayList<QueueWrapper> minLoadQWs=getMinLoadQueueWrapper(qws);
        QueueWrapper minLoadQW=minLoadQWs.remove(qws.length-1);
        minLoadQW.queue.add(taskWrapper);
        return minLoadQW;
    }

    private static ArrayList<QueueWrapper> getMinLoadQueueWrapper(QueueWrapper[] qws){
        ArrayList<QueueWrapper> tempList=new ArrayList<QueueWrapper>(qws.length);
        QueueWrapper minLoadQW=null;
        Integer minCount = 0;
        Integer tempCount;
        for(QueueWrapper temp:qws){
            if(minLoadQW==null){
                minLoadQW=temp;
                minCount=minLoadQW.queue.size();
            }else{
                tempCount=temp.queue.size();
                if(tempCount<minCount){
                    tempList.add(minLoadQW);
                    minLoadQW=temp;
                    minCount=temp.queue.size();
                }else{
                    tempList.add(temp);
                }
            }
        }
        tempList.add(minLoadQW);
        return tempList;
    }

    static void deQueues(LinkedList<TaskWrapper> temp,ConcurrentHashMap<String,QueueWrapper[]> qwMap,Long twId){
        Iterator<Map.Entry<String,QueueWrapper[]>> iterator=qwMap.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,QueueWrapper[]> entry=iterator.next();
            QueueWrapper[] qws=entry.getValue();
            deQueues(temp,qws,twId);
        }
    }

    private static void deQueues(LinkedList<TaskWrapper> temp,
                                 QueueWrapper[] qws,
                                 Long twId){
        QueueWrapper queueWrapper;
        ReentrantLock lock;
        BlockingQueue<TaskWrapper> queue;
        int qwsCount=qws.length;
        int count;
        Boolean locked=false;
        for(int index=0;index < qwsCount;index++){
            queueWrapper=qws[index];
            if(queueWrapper.isSeq&&!queueWrapper.twId.equals(twId)){
                continue;
            }
            lock=queueWrapper.qLock;
            try{
                if(lock.tryLock()){//for not block
                    locked=true;
                    queue=queueWrapper.queue;
                    count=queue.size();
                    count=queue.drainTo(temp,count);
                    queueWrapper.deCount=queueWrapper.deCount+count;
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(locked){
                    lock.unlock();
                    locked=false;
                }
            }
        }
    }

    static QueueWrapper[] initQws(String gTaskIdStr,
                                   Long gTaskId,
                                   Long twId,
                                   Integer queueCount,
                                   Boolean isSeq){
        QueueWrapper[] newQws=new QueueWrapper[queueCount];
        for(int index=0;index < queueCount;index++){
            newQws[index]=new QueueWrapper(new LinkedBlockingDeque<TaskWrapper>());
            newQws[index].setTwId(twId);
            newQws[index].setSeq(isSeq);
        }
        QueueWrapper.gTaskIdToTaskNmMap.put(gTaskId,gTaskIdStr);
        QueueWrapper.qwMap.putIfAbsent(gTaskIdStr,newQws);
        return newQws;
    }

    public BlockingQueue<TaskWrapper> getQueue() {
        return queue;
    }

    public void setQueue(BlockingQueue<TaskWrapper> queue) {
        this.queue = queue;
    }

    public ReentrantLock getqLock() {
        return qLock;
    }

    public void setqLock(ReentrantLock qLock) {
        this.qLock = qLock;
    }

    public Integer getDeCount() {
        return deCount;
    }

    public void setDeCount(Integer deCount) {
        this.deCount = deCount;
    }

    static ConcurrentHashMap<String, QueueWrapper[]> getQwMap() {
        return qwMap;
    }

    public Long getTwId() {
        return twId;
    }

    public void setTwId(Long twId) {
        this.twId = twId;
    }

    public Boolean getSeq() {
        return isSeq;
    }

    public void setSeq(Boolean seq) {
        isSeq = seq;
    }
}
