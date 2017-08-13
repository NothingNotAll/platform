package nna.base.util;

import java.util.concurrent.*;

/**
 * Created by NNA-SHUAI on 2017/8/13.
 */
public class TimingTask implements Runnable{
    private static ExecutorService executorService=Executors.newCachedThreadPool();

    private static final TimingTask timingTask=new TimingTask();

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

    }

    public void run() {
        while(true){
            try{
                Future future=executorService.submit(this);
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                continue;
            }
        }
    }
}
