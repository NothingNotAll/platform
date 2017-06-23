package nna.base.util.conv2;

import java.util.concurrent.ExecutorService;
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

    }

    protected TaskWrapper[] deQueue() {
        return new TaskWrapper[0];
    }
}
