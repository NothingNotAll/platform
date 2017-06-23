package nna.base.util.concurrent;

/**
 * @author NNA-SHUAI
 * @create 2017-06-23 8:58
 **/

public class SeqFixSizeStgy extends AbstractEnAndDeStgy {


    SeqFixSizeStgy(Integer queueSize, Integer exeTCount) {
        super(queueSize, exeTCount);
    }

    protected void initQueue(Object[] queues, int index) {
        queues[index]=null;
    }

    protected void enQueue(TaskWrapper taskWrapper) {

    }

    protected nna.base.util.concurrent.TaskWrapper[] deQueue() {
        return new nna.base.util.concurrent.TaskWrapper[0];
    }
}
