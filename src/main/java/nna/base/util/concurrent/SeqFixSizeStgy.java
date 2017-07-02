package nna.base.util.concurrent;

import java.util.concurrent.BlockingQueue;

/**
 * @author NNA-SHUAI
 * @create 2017-06-23 8:58
 **/

public class SeqFixSizeStgy extends AbstractEnAndDeSgy {


    SeqFixSizeStgy(Integer queueSize, Integer exeTCount) {
        super(queueSize, exeTCount);
    }

    protected BlockingQueue<TaskWrapper> initQueue() {
        return null;
    }

    protected void enQueue(TaskWrapper taskWrapper) {
        defaultEnQueue(taskWrapper);
    }

    protected TaskWrapper[] deQueue() {
        return new TaskWrapper[0];
    }
}
