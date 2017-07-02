package nna.base.util.concurrent;

import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author NNA-SHUAI
 * @create 2017-06-29 10:49
 **/

 class QueueWrapper {
    private BlockingQueue<TaskWrapper> queue;
    private ReentrantLock qLock;
    private volatile Integer deCount=0;//the total Count of been worked;

    QueueWrapper(BlockingQueue<TaskWrapper> queue){
        this.queue=queue;
        this.qLock=new ReentrantLock();
    }

    static QueueWrapper enQueue(QueueWrapper[] qws,TaskWrapper taskWrapper){
        QueueWrapper minLoadQW=getMinLoadQueueWrapper(qws);
        //check queue load
        minLoadQW.queue.add(taskWrapper);
        return minLoadQW;
    }

    static TaskWrapper waitTask(QueueWrapper queueWrapper){
        ReentrantLock lock=queueWrapper.qLock;
        Boolean locked=false;
        try{
            if(lock.tryLock()){
                locked=true;
                TaskWrapper taskWrapper=queueWrapper.queue.take();
                return taskWrapper;
            }else{
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(locked){
                lock.unlock();
            }
        }
        return null;
    }

    private static QueueWrapper getMinLoadQueueWrapper(QueueWrapper[] qws){
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
                    minLoadQW=temp;
                    minCount=temp.queue.size();
                }
            }
        }
        return minLoadQW;
    }

    static TaskWrapper[] deQueues(QueueWrapper[] qws){
        LinkedList<TaskWrapper> temp=new LinkedList<TaskWrapper>();
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
        return temp.toArray(new TaskWrapper[0]);
    }
}
