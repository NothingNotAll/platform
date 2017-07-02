package nna.base.util.concurrent;

import java.util.concurrent.BlockingQueue;

/**
 * @author NNA-SHUAI
 * @create 2017-06-23 9:02
 **/

public class NoSeqFixSizeStgy extends AbstractEnAndDeSgy {

    NoSeqFixSizeStgy(Integer queueSize, Integer exeTCount) {
        super(queueSize, exeTCount);
    }

    protected BlockingQueue<TaskWrapper> initQueue() {
        return null;
    }

    protected void enQueue(TaskWrapper taskWrapper) {
        defaultEnQueue(taskWrapper);
    }

    protected TaskWrapper[] deQueue() {
        return defaultDeQueue();
    }
}