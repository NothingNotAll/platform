package nna.base.util.concurrent;


import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author NNA-SHUAI
 * @create 2017-06-23 9:03
 **/

public class NoSeqLinkedStgy extends AbstractEnAndDeStgy {

    NoSeqLinkedStgy(Integer queueSize, Integer exeTCount) {
        super(queueSize, exeTCount);
    }

    protected void initQueue(Object[] queues, int index) {
        queues[index]=new LinkedBlockingQueue<TaskWrapper>();
    }

    protected void enQueue(TaskWrapper taskWrapper) {
        Object[] queues=getQueues();
        BlockingQueue queue=getLoadBalance(queues);
        queue.add(taskWrapper);
    }

    protected TaskWrapper[] deQueue() {
        LinkedList<TaskWrapper> consumerList=getBalanceList(this);
        return consumerList.toArray(new TaskWrapper[0]);
    }
}
