package nna.base.util.concurrent;

/**
 * @author NNA-SHUAI
 * @create 2017-06-17 9:43
 **/

public class InitDispathcerV2 extends Dispatcher {
    InitDispathcerV2(WorkerV2 worker, AbstractTasks abstractTask) {
        super(worker, abstractTask);
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run() {
        worker.entry(abstractTasks);
    }
}
