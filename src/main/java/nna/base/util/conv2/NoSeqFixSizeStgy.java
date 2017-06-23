package nna.base.util.conv2;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author NNA-SHUAI
 * @create 2017-06-23 9:02
 **/

public class NoSeqFixSizeStgy extends AbstractEnAndDeStgy {
    private volatile Integer workIndex;
    private ReentrantLock[] tWLocks;
    private Integer lockCount;
    NoSeqFixSizeStgy(Integer queueSize, Integer exeTCount) {
        super(queueSize, exeTCount);
        tWLocks=new ReentrantLock[queueSize];
        for(int index=0;index < queueSize;index++){
            tWLocks[index]=new ReentrantLock();
        }
        this.lockCount=queueSize;
    }

    protected void initQueue(Object[] queues, int index) {
        queues[index]=null;
    }

    protected void enQueue(TaskWrapper taskWrapper) {
        ReentrantLock lock;
        TaskWrapper temp;
        Object[] os=getQueues();
        for(int index=0;index < lockCount;index++){
            lock=tWLocks[index];
            temp=(TaskWrapper) os[index];
            if(temp==null){

            }
        }
    }

    protected TaskWrapper[] deQueue() {
        LinkedList<TaskWrapper> temps=new LinkedList<TaskWrapper>();
        int length=getQueueSize();
        Object[] os=getQueues();
        TaskWrapper taskWrapper;
        for(int index=0;index < length;index++){
            taskWrapper=(TaskWrapper) os[index];
            if(taskWrapper!=null&&taskWrapper.getTaskStatus()==AbstractTask.START_STATUS){
                temps.add(taskWrapper);
            }
        }
        return temps.toArray(new TaskWrapper[0]);
    }
}
