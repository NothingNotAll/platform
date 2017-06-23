package nna.base.util.conv2;

import java.util.concurrent.ExecutorService;

/**
 * @author NNA-SHUAI
 * @create 2017-06-23 8:58
 **/

public class SeqFixSizeStgy<TaskWrapper> extends AbstractEnAndDeStgy {

    SeqFixSizeStgy(Integer queueSize, Integer exeTCount) {
        super(queueSize, exeTCount);
    }

    protected void initQueue(Object[] queues, int index) {
        queues[index]=null;
    }

    protected void enQueue(nna.base.util.conv2.TaskWrapper taskWrapper) {

    }

    protected nna.base.util.conv2.TaskWrapper[] deQueue() {
        return new nna.base.util.conv2.TaskWrapper[0];
    }
}
