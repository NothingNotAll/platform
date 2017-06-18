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
 class IOTaskEntry {
    static IOEventProcessorManager IOEventProcessorManager;

    static void init(Long maxBusinessProcessTime,Long thresholdTime){
        IOEventProcessorManager = IOEventProcessorManager.initWorkerManager(maxBusinessProcessTime,thresholdTime);
    }

    static void submitEvent(AbstractIOTask t, Object object, int taskStatus) {
        IOEventProcessorManager.submitEvent(t,object,taskStatus);
    }

    static void submitInitEvent(AbstractIOTask t, Object object,boolean isWorkSeq){
        IOEventProcessorManager.submitInitEvent(t,object,isWorkSeq);
    }
}
