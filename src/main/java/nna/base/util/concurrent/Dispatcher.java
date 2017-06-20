package nna.base.util.concurrent;


import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author NNA-SHUAI
 * @create 2017-06-15 11:40
 **/

public class Dispatcher implements Runnable {
    private AbstractTaskWrapper temp;
    private TaskSchedule TaskSchedule;

    public void run() {
        LinkedBlockingQueue<AbstractTaskWrapper> queue= TaskSchedule.getWorkQueue();
        //性能瓶頸點
        queue.add(temp);
    }

    public Dispatcher(
                      TaskSchedule TaskSchedule,
                      AbstractTaskWrapper abstractTaskWrapper){
        this.temp= abstractTaskWrapper;
        this.TaskSchedule = TaskSchedule;
    }
}
