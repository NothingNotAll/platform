package nna.base.util.concurrent;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author NNA-SHUAI
 * @create 2017-06-23 9:00
 **/

public class SeqLinkedStgy extends AbstractEnAndDeSgy {

    SeqLinkedStgy(Integer queueSize, Integer exeTCount) {
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
