package nna.base.util.concurrent;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author NNA-SHUAI
 * @create 2017-06-15 21:53
 **/

public class Monitor implements Runnable {
    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMdd-HH:mm:ss:SSS");
    private TaskSchedule[] taskSchedules;
    private Map map;
    private int index=0;
    public void run() {
        while(true){
            System.out.println(simpleDateFormat.format(System.currentTimeMillis())+"-TOTAL TASKS'S WAIT SIZE:"+map.size());
            index=0;
            for(TaskSchedule TaskSchedule : taskSchedules){
                LinkedBlockingQueue<AbstractTasks> queue= TaskSchedule.getWorkQueue();
//                Iterator<AbstractTasks> iterator=queue.iterator();
//                AbstractTasks abstractIOTasks;
//                while(iterator.hasNext()){
//                    abstractIOTasks =iterator.next();
//                }
                System.out.println(simpleDateFormat.format(System.currentTimeMillis())+"-No ["+index+"]-BLOCKING QUEUE TASK'S WAIT SIZE:"+queue.size());
                index++;
            }
            try {
                Thread.currentThread().sleep(3000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public TaskSchedule[] getTaskSchedules() {
        return taskSchedules;
    }

    public void setTaskSchedules(TaskSchedule[] taskSchedules) {
        this.taskSchedules = taskSchedules;
    }

    public void setMap(Map map) {
        this.map = map;
    }
}
