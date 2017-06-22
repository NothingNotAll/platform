package nna.base.util.concurrent;


import nna.Marco;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * workCount:we should think of that the time of every IO task,
 * if time is too short ,the workCount can be set only by CPU's Core size;
 *
 * if time is too Long , we can set the workCount with the Core Size of Cpu and
 * think of :
 * workCount=max(time/threshold,coreSize);
 *
 * @author NNA-SHUAI
 * @create 2017-05-30 16:40
 **/

 class TaskScheduleManager {
    private static AtomicLong taskNo=new AtomicLong(0L);
    private static ConcurrentHashMap<Long,AbstractTasks> monitorMap=new ConcurrentHashMap<Long, AbstractTasks>();
    private static TaskScheduleManager TaskScheduleManager;
    private static volatile boolean init=false;
    private static ExecutorService cachedService = Executors.newCachedThreadPool();
    private static ConcurrentHashMap<Integer,ExecutorService> tPoolMap=new ConcurrentHashMap<Integer, ExecutorService>();

    synchronized static TaskScheduleManager initWorkerManager(Long maxBusinessProcessTime, Long thresholdTime){
        if(init){
            return TaskScheduleManager;
        }
        init=true;
        int workCount=getAvlCPUCount();
        if(maxBusinessProcessTime!=null&&thresholdTime!=null){
            int count=getBusinessCount(maxBusinessProcessTime,thresholdTime);
            Math.max(count,workCount);
        }
        TaskScheduleManager =new TaskScheduleManager();
        return TaskScheduleManager;
    }

    private static int getBusinessCount(Long maxBusinessProcessTime,Long thresholdTime){
        long count=maxBusinessProcessTime/thresholdTime;
        count=maxBusinessProcessTime%thresholdTime==0?count:count+1;
        return (int)count;
    }

    private static int getAvlCPUCount(){
        int coreCount=Runtime.getRuntime().availableProcessors();
        return coreCount<=1?1:coreCount-1;
        }

        private TaskScheduleManager(){
            init();
        }

        private void init() {

        }

        private void enWorkMap(AbstractTasks abstractTasks) {
            Long workId=taskNo.getAndIncrement();
            abstractTasks.setGlobalWorkId(workId);
            monitorMap.put(workId, abstractTasks);
        }

        private void remove(AbstractTasks abstractTasks, int taskType) {
            Long workId= abstractTasks.getGlobalWorkId();
            if(taskType== AbstractTask.OVER){
                monitorMap.remove(workId);
            }
    }

    private boolean loadAlg(){
        //根据当前服务器负载情况决定采取策略。
        return true;
    }

    void startTask(AbstractTask t, Object object, int containerType,String taskName){
        if(loadAlg()){
            submitInitEvent(t,object,containerType,taskName);
        }else{

        }
    }

    void addNewTask(AbstractTask t, Object object, int taskType){
        if(loadAlg()){
            submitEvent(t,object,taskType);
        }else{

        }
    }

    void submitEvent(AbstractTask t, Object object, int taskType) {
        AbstractTasks abstractTasks =t.getTasks();
        remove(abstractTasks,taskType);
        abstractTasks.addTask(t,taskType,object);
    }

    void submitInitEvent(AbstractTask t, Object object, int tasksType,String taskName){
        int workCount=t.getWorkCount();
        AbstractTasks abstractTasks = null;
        switch (tasksType){
            case Marco.NO_SEQ_FIX_SIZE_TASK:
                abstractTasks =new NoSeqFixSizeTasks(workCount,null,taskName);
                t.setTasks(abstractTasks);
                abstractTasks.addTask(t, AbstractTask.INIT,object);
                cachedService.submit(abstractTasks);
                break;
            case Marco.NO_SEQ_LINKED_SIZE_TASK:
                abstractTasks =new NoSeqLinkedTasks(Marco.IO_BLOCKQUEUE_COUNT,
                        Marco.IO_PROCESS_COUNT, null,
                        cachedService,taskName);
                t.setTasks(abstractTasks);
                abstractTasks.addTask(t, AbstractTask.INIT,object);
                break;
            case Marco.SEQ_FIX_SIZE_TASK:
                abstractTasks =new SeqFixSizeTasks(workCount,null,taskName);
                t.setTasks(abstractTasks);
                cachedService.submit(abstractTasks);
                abstractTasks.addTask(t, AbstractTask.INIT,object);
                break;
//            case Marco.ONE_TASK:
//                abstractTasks =new OneTask(null);
//                t.setTasks(abstractTasks);
//                abstractTasks.addTask(t, AbstractTask.INIT,object);
//                cachedService.submit(abstractTasks);
//                break;
            case Marco.SEQ_LINKED_SIZE_TASK:
                abstractTasks=new SeqLinkedTasks(null,taskName);
                t.setTasks(abstractTasks);
                cachedService.submit(abstractTasks);
                abstractTasks.addTask(t, AbstractTask.INIT,object);
        }
        enWorkMap(abstractTasks);
    }

    static ConcurrentHashMap<Long, AbstractTasks> getMonitorMap() {
        return monitorMap;
    }

    static void setMonitorMap(ConcurrentHashMap<Long, AbstractTasks> monitorMap) {
        nna.base.util.concurrent.TaskScheduleManager.monitorMap = monitorMap;
    }
}
