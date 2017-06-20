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

    private ExecutorService fixedLogWorkerService;
    private ArrayList<TaskSchedule> balancedTaskScheduleList;

    synchronized static TaskScheduleManager initWorkerManager(Long maxBusinessProcessTime, Long thresholdTime){
        if(init){
            return TaskScheduleManager;
        }
        init=true;
        int workCount=getAvlCPUCount();
        if(maxBusinessProcessTime!=null&&thresholdTime!=null){
            int count=getBusinessCount(maxBusinessProcessTime,thresholdTime);
            workCount=Math.max(count,workCount);
        }
        TaskScheduleManager =new TaskScheduleManager(workCount);
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

    private TaskScheduleManager(int workerCount){
        init(workerCount*10);
    }

    private void init(int workerCount) {
        Monitor Monitor =new Monitor();
        TaskSchedule[] taskSchedules =new TaskSchedule[workerCount];
        balancedTaskScheduleList =new ArrayList<TaskSchedule>(workerCount);
        fixedLogWorkerService=Executors.newFixedThreadPool(workerCount);
        TaskSchedule tempTaskSchedule;
        for(int index=0;index < workerCount;index++){
            tempTaskSchedule =new TaskSchedule();
            tempTaskSchedule.setLoadNo(index);
            balancedTaskScheduleList.add(tempTaskSchedule);
            fixedLogWorkerService.submit(tempTaskSchedule);
            taskSchedules[index]= tempTaskSchedule;
        }
        Monitor.setTaskSchedules(taskSchedules);
        Monitor.setMap(monitorMap);
//        new Thread(Monitor).start();
    }

    void addWorker(TaskSchedule TaskSchedule){

    }

    void deleteWorker(){

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

    void startTask(AbstractTask t, Object object, int containerType){
        if(loadAlg()){
            submitInitEvent(t,object,containerType);
        }else{
//            submitAsyInitEvcent(t,object,tasksType);
        }
    }

    void addNewTask(AbstractTask t, Object object, int taskType){
        if(loadAlg()){
            submitEvent(t,object,taskType);
        }else{
            submitAsyEvent(t,object,taskType);
        }
    }

    void submitEvent(AbstractTask t, Object object, int taskType) {
        AbstractTasks abstractTasks =t.getTasks();
//        remove(abstractTasks,taskType);
        abstractTasks.addTask(t,taskType,object);
        if(abstractTasks instanceof NoSeqFixSizeTasks){
            cachedService.submit(abstractTasks);
        }
    }

    void submitAsyEvent(AbstractTask t, Object object, int taskType){
        AbstractTasks abstractTasks =t.getTasks();
        Integer index=t.getiOLoadEventProcessorId();
        TaskSchedule taskSchedule = balancedTaskScheduleList.get(index);;
        if(abstractTasks instanceof NoSeqFixSizeTasks){
            EntryDesc entryDesc=getBalanceWorker();
            taskSchedule =entryDesc.TaskSchedule;
            t.setiOLoadEventProcessorId(entryDesc.iOLoadEventProcessorId);
        }
//      remove(abstractTasks,taskType);
        Runnable dispatcher =new Dispatcher(abstractTasks, taskSchedule);
        cachedService.submit(dispatcher);
    }

    void submitAsyInitEvcent(AbstractTask t, Object object, boolean isWorkSeq){
        int tasksType;
        AbstractTasks abstractTasks;
        int workCount=t.getWorkCount();
        EntryDesc entryDesc=getBalanceWorker();
        TaskSchedule taskSchedule =entryDesc.TaskSchedule;
        t.setiOLoadEventProcessorId(entryDesc.iOLoadEventProcessorId);
        if(isWorkSeq){
            abstractTasks =new SeqFixSizeTasks(workCount,null);
        }else{
            abstractTasks =new NoSeqFixSizeTasks(workCount,null);
        }
      //enWorkMap(abstractTasks);
        Runnable dispatcher =new Dispatcher(abstractTasks, taskSchedule);
        cachedService.submit(dispatcher);
        abstractTasks.addTask(t, AbstractTask.INIT,object);
    }

    void submitInitEvent(AbstractTask t, Object object, int tasksType){
        int workCount=t.getWorkCount();
        AbstractTasks abstractTasks;
        switch (tasksType){
            case Marco.NO_SEQ_FIX_SIZE_TASK:
                abstractTasks =new NoSeqFixSizeTasks(workCount,null);
                t.setTasks(abstractTasks);
                abstractTasks.addTask(t, AbstractTask.INIT,object);
                cachedService.submit(abstractTasks);
                break;
            case Marco.NO_SEQ_LINKED_SIZE_TASK:
                abstractTasks =new NoSeqFixSizeTasks(workCount,null);
                t.setTasks(abstractTasks);
                abstractTasks.addTask(t, AbstractTask.INIT,object);
                cachedService.submit(abstractTasks);
                break;
            case Marco.SEQ_FIX_SIZE_TASK:
                abstractTasks =new SeqFixSizeTasks(workCount,null);
                t.setTasks(abstractTasks);
                cachedService.submit(abstractTasks);
                abstractTasks.addTask(t, AbstractTask.INIT,object);
                break;
            case Marco.ONE_TASK:
                abstractTasks =new NoSeqFixSizeTasks(workCount,null);
                t.setTasks(abstractTasks);
                abstractTasks.addTask(t, AbstractTask.INIT,object);
                cachedService.submit(abstractTasks);
                break;
            case Marco.NO_SEQ_LINKEDV2_SIZE_TASK:
                abstractTasks =new NoSeqFixSizeTasks(workCount,null);
                t.setTasks(abstractTasks);
                abstractTasks.addTask(t, AbstractTask.INIT,object);
                cachedService.submit(abstractTasks);
                break;
        }
//        enWorkMap(abstractTasks);
    }

    EntryDesc getBalanceWorker() {
        Iterator<TaskSchedule> iterator= balancedTaskScheduleList.iterator();
        TaskSchedule TaskSchedule;
        TaskSchedule minTaskSchedule =null;
        int count;
        Integer minCount=null;
        Integer workerIndex=null;
        int index=0;
        while(iterator.hasNext()){
            TaskSchedule =iterator.next();
            count= TaskSchedule.getTempWorkCount();
            if(minCount==null){
                minTaskSchedule = TaskSchedule;
                minCount=count;
                workerIndex=index;
            }else{
                if(minCount>count){
                    minTaskSchedule = TaskSchedule;
                    workerIndex=index;
                    minCount=count;
                }
            }
            if(minCount==0){
                break;
            }
            index++;
        }
         return new EntryDesc(minTaskSchedule,workerIndex);
    }

    private class EntryDesc{
        private TaskSchedule TaskSchedule;
        private Integer iOLoadEventProcessorId;

        private EntryDesc(TaskSchedule TaskSchedule, Integer iOLoadEventProcessorId){
            this.TaskSchedule = TaskSchedule;
            this.iOLoadEventProcessorId=iOLoadEventProcessorId;
        }
    }
}
