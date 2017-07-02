package nna.base.util.concurrent;


import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author NNA-SHUAI
 * @create 2017-06-23 9:03
 **/

public class NoSeqLinkedStgy extends AbstractEnAndDeSgy {

    NoSeqLinkedStgy(Integer queueSize, Integer exeTCount) {
        super(queueSize, exeTCount);
    }

    protected BlockingQueue<TaskWrapper> initQueue() {
        return new LinkedBlockingQueue<TaskWrapper>();
    }

    protected void enQueue(TaskWrapper taskWrapper) {
        defaultEnQueue(taskWrapper);
    }

    protected TaskWrapper[] deQueue() {
        return defaultDeQueue();
    }
}
