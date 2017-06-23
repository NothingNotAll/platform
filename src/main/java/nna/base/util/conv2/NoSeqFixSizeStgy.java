package nna.base.util.conv2;

import java.util.concurrent.ExecutorService;

/**
 * @author NNA-SHUAI
 * @create 2017-06-23 9:02
 **/

public class NoSeqFixSizeStgy extends AbstractEnAndDeStgy {
    NoSeqFixSizeStgy(Integer queueSize, Integer exeTCount) {
        super(queueSize, exeTCount);
    }

    protected void initQueue(Object[] queues, int index) {
        queues[index]=null;
    }

    protected void enQueue(TaskWrapper taskWrapper) {

    }

    protected TaskWrapper[] deQueue() {
        return new TaskWrapper[0];
    }
}
