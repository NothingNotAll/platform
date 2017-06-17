package nna.base.util.concurrent;

/**
 * @author NNA-SHUAI
 * @create 2017-06-17 9:41
 **/

public abstract class Dispatcher implements Runnable {

    protected AbstractTasksV2 abstractTasks;
    protected WorkerV2 worker;

    Dispatcher(WorkerV2 worker, AbstractTasksV2 abstractTask){
        this.worker=worker;
        this.abstractTasks=abstractTask;
    }

    public AbstractTasksV2 getAbstractTasks() {
        return abstractTasks;
    }

    public void setAbstractTasks(AbstractTasksV2 abstractTasks) {
        this.abstractTasks = abstractTasks;
    }

    public WorkerV2 getWorker() {
        return worker;
    }

    public void setWorker(WorkerV2 worker) {
        this.worker = worker;
    }
}
