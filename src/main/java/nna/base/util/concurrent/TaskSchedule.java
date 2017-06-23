package nna.base.util.concurrent;

import nna.Marco;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author NNA-SHUAI
 * @create 2017-06-23 8:20
 **/

public class TaskSchedule {

    public static void main(String[] args){

    }
    private static final ConcurrentHashMap<Long,AbstractTask> monitorMap=new ConcurrentHashMap<Long, AbstractTask>();
    private static ExecutorService cachedThreadPool= Executors.newCachedThreadPool();
    private static MonitorTask monitorTask=new MonitorTask(monitorMap);

    private TaskSchedule(){}


    static void submitTask(AbstractTask abstractTask,int exeThreadPoolType){
        monitorMap.put(abstractTask.getgTaskId(),abstractTask);
        AbstractEnAndDeStgy abstractEnAndDeStgy=abstractTask.getAbstractEnAndDeStgy();
        switch (exeThreadPoolType){
            case Marco.FIX_THREAD_TYPE:
                abstractEnAndDeStgy.init(cachedThreadPool);
                break;
            case Marco.CACHED_THREAD_TYPE:
                int exeThreadCount=abstractEnAndDeStgy.getExeTCount();
                abstractEnAndDeStgy.init(Executors.newFixedThreadPool(exeThreadCount));
                break;
        }
    }

    //定时任务
    static void submitTask(AbstractTask abstractTask){
        monitorMap.put(abstractTask.getgTaskId(),abstractTask);
        ExecutorService executorService=Executors.newFixedThreadPool(1);
        executorService.submit(abstractTask.getAbstractEnAndDeStgy());
        abstractTask.getAbstractEnAndDeStgy().init(executorService);
    }

    static void submitTask(TaskWrapper taskWrapper){
        cachedThreadPool.submit(taskWrapper);
    }

    static void remove(AbstractTask abstractTask){
        monitorMap.remove(abstractTask.getgTaskId());
    }

    static MonitorTask getMonitorTask() {
        return monitorTask;
    }

    static void setMonitorTask(MonitorTask monitorTask) {
        TaskSchedule.monitorTask = monitorTask;
    }
}
