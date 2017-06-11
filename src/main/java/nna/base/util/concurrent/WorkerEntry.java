package nna.base.util.concurrent;

/**
 * @author NNA-SHUAI
 * @create 2017-05-30 16:40
 **/

public class WorkerEntry {
    private WorkerManager workerManager;

    public WorkerEntry(){
        int workCount=Runtime.getRuntime().availableProcessors()-1;
        workerManager=new WorkerManager(workCount,new Worker());
    }

    public WorkerEntry(int workCount){
        WorkerManager workerManager=new WorkerManager(workCount,new Worker());
    }

    public WorkerEntry(int workerCount,Worker worker){
        workerManager=new WorkerManager(workerCount,worker);
    }

    public void submitEvent(AbstractTask abstractTask){
        workerManager.submitEvent(abstractTask);
    }

    void submitInitEvent(AbstractTask abstractTask){
        workerManager.submitInitEvent(abstractTask);
    }
}
