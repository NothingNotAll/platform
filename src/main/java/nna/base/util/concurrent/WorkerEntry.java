package nna.base.util.concurrent;

/**
 * @author NNA-SHUAI
 * @create 2017-06-13 14:05
 **/

public class WorkerEntry {
    public static WorkerManager workerManager;

    static void init(Integer workCount){
        workerManager=WorkerManager.initWorkerManager(workCount);
    }

    static void submitEvent(AbstractTask t,Object object) {
        workerManager.submitEvent(t,object);
    }

    static void submitInitEvent(AbstractTask t,Object object,boolean keepWorkSeq) {
        workerManager.submitInitEvent(t,object,keepWorkSeq);
    }
}
