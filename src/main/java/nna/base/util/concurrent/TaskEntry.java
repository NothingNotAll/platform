package nna.base.util.concurrent;

/**
 * @author NNA-SHUAI
 * @create 2017-06-13 14:05
 **/

/*
* init entry
* this limit of concurrent framework is that:
* 1:tasks list : waste of memory may lead to the oom exception
* 2:ConcurrentMap.put
*   LinkedBlockingQueue.add
* workCount's alg:
* workCount=max(time/threshold,coreSize);
*Test: the sequence 's right;
*
* we must think of that too long IO event process's method
* */
 class TaskEntry {
    static EventProcessorManager EventProcessorManager;

    static void init(Long maxBusinessProcessTime,Long thresholdTime){
        EventProcessorManager = EventProcessorManager.initWorkerManager(maxBusinessProcessTime,thresholdTime);
    }

    static void addNewTask(AbstractTask t, Object object, int taskStatus) {
        EventProcessorManager.addNewTask(t,object,taskStatus);
    }

    static void startTask(AbstractTask t, Object object, boolean isWorkSeq){
        EventProcessorManager.startTask(t,object,isWorkSeq);
    }
}
