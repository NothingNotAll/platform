package nna.base.util;

import java.util.LinkedList;
import java.util.concurrent.*;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * for db record timing task
 * support db config timing task
 * single thread manage all the timing tasks
 * using the executor pool api to achieve the executing of thread
 * next model
 * while(taskIterator.hasNext()){
 *     taskWrapper=taskIterator.next();
 *     sleepTime=sleepTime=nextTask.beginTime-previousTaskTime;
 *     List<task> task=taskWrapper.getList();
 *     LockSupport.parkNanos();
 *     for(Task task:tasks){
 *         eService.submit(task);
 *     }
 *     task startExeTime
 *
 * }
 * Created by NNA-SHUAI on 2017/8/13.
 */
public class TimingTask implements Runnable{
    private static ExecutorService executorService=Executors.newCachedThreadPool();
    private static CopyOnWriteArrayList<FutureTask> taskList=new CopyOnWriteArrayList<FutureTask>();
    private static final TimingTask timingTask=new TimingTask();
    private static Long startTime;
    private static ReentrantLock lock=new ReentrantLock();

    private Object[] tasks;

    static {
        new Thread(timingTask).start();
    }

    private TimingTask(){}

    public static void addNewTimingTask(int[] timingTaskParams,Runnable runnable,Object result){
        FutureTask futureTask=new FutureTask(runnable,result);
        addNewTimingTask(timingTaskParams,futureTask);
    }

    public static void addNewTimingTask(int[] timingTaskParams, Callable callable){
        FutureTask futureTask=new FutureTask(callable);
        addNewTimingTask(timingTaskParams,futureTask);
    }

    public static void addNewTimingTask(int[] timingTaskParams, FutureTask futureTask){
        boolean isLocked=false;
        try{
            lock.lock();
            isLocked=true;
        }finally {
            if(isLocked){
                lock.unlock();
            }
        }
    }

    private static void computeNextDayTimingTaskList(){

    }

    public void run() {
        TimingTask.startTime=System.currentTimeMillis();
        while(true){
            if(taskList.size()==0){
                LockSupport.park();
            }
            try{
                tasks=taskList.toArray();

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                continue;
            }
        }
    }
    private static class FutureTaskWrapper{
        private FutureTask futureTask;
        private int[] timeParams;
        private int timingType;
    }
}
