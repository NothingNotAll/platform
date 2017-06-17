package nna.base.util.concurrent;

/**
 * @author NNA-SHUAI
 * @create 2017-06-17 9:41
 **/

public abstract class Dispatcher implements Runnable {

    protected AbstractTasks abstractTasks;
    protected WorkerV2 worker;

    Dispatcher(WorkerV2 worker, AbstractTasks abstractTask){
        this.worker=worker;
        this.abstractTasks=abstractTask;
    }

    public AbstractTasks getAbstractTasks() {
        return abstractTasks;
    }

    public void setAbstractTasks(AbstractTasks abstractTasks) {
        this.abstractTasks = abstractTasks;
    }

    public WorkerV2 getWorker() {
        return worker;
    }

    public void setWorker(WorkerV2 worker) {
        this.worker = worker;
    }
}
