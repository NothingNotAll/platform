package nna.base.util.concurrent;


/**
 * @author NNA-SHUAI
 * @create 2017-05-30 16:16
 **/

 class TaskDispatcher implements Runnable {

    private Worker worker;
    private AbstractTask abstractTask;
    private Object object;

     TaskDispatcher(Worker worker, AbstractTask abstractTask,Object object){
        this.worker=worker;
        this.abstractTask=abstractTask;
        this.object=object;
    }
    public void run() {
        dispatch();
    }

    private void dispatch() {
        worker.submitTask(abstractTask,object);
    }
}
