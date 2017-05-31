package nna.base.util.concurrent;

/**
 * @author NNA-SHUAI
 * @create 2017-05-30 16:40
 **/

public class WorkerEntry {
    private WorkerManager workerManager;

    public WorkerEntry(int workerCount,Worker worker){
        workerManager=new WorkerManager(workerCount,worker);
    }

    public void submitEvent(AbstractTask abstractTask){
        workerManager.submitEvent(abstractTask);
    }

    public void submitWorkEvent(AbstractTask abstractTask){
        workerManager.submitWorkEvent(abstractTask);
    }


}
