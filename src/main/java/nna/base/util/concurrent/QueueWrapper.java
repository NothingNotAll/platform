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
    static final ConcurrentHashMap<String,QueueWrapper[]> noSeqQwMap=new ConcurrentHashMap<String, QueueWrapper[]>();

    private BlockingQueue<TaskWrapper> queue;
    private ReentrantLock qLock;
    private volatile Integer deCount=0;//the total Count of been worked;

    QueueWrapper(BlockingQueue<TaskWrapper> queue){
        this.queue=queue;
        this.qLock=new ReentrantLock();
    }

    static QueueWrapper enQueue(QueueWrapper[] qws,TaskWrapper taskWrapper){
        ArrayList<QueueWrapper> minLoadQWs=getMinLoadQueueWrapper(qws);
        QueueWrapper minLoadQW=minLoadQWs.remove(qws.length-1);
        minLoadQW.queue.add(taskWrapper);
        return null;
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

    static void deQueues(LinkedList<TaskWrapper> temp,ConcurrentHashMap<String,QueueWrapper[]> qwMap){
        Iterator<Map.Entry<String,QueueWrapper[]>> iterator=qwMap.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,QueueWrapper[]> entry=iterator.next();
            QueueWrapper[] qws=entry.getValue();
            deQueues(temp,qws);
        }
    }

    private static void deQueues(LinkedList<TaskWrapper> temp,QueueWrapper[] qws){
        QueueWrapper queueWrapper;
        ReentrantLock lock;
        BlockingQueue<TaskWrapper> queue;
        int qwsCount=qws.length;
        int count;
        Boolean locked=false;
        for(int index=0;index < qwsCount;index++){
            queueWrapper=qws[index];
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

    static QueueWrapper[] addQueue(int addQWCount){
        QueueWrapper[] newQws=new QueueWrapper[addQWCount];
        for(int index=0;index < addQWCount;index++){
            newQws[index]=new QueueWrapper(new LinkedBlockingDeque<TaskWrapper>());
        }
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

}
