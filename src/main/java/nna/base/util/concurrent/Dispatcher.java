package nna.base.util.concurrent;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author NNA-SHUAI
 * @create 2017-06-15 11:40
 **/

public class Dispatcher implements Runnable {
    private AbstractTasks temp;
    private EventProcessor EventProcessor;

    public void run() {
        LinkedBlockingQueue<AbstractTasks> queue= EventProcessor.getWorkQueue();
        //性能瓶頸點
        queue.add(temp);
    }

    public Dispatcher(AbstractTasks abstractTasks,
                      EventProcessor EventProcessor){
        this.temp= abstractTasks;
        this.EventProcessor = EventProcessor;
    }
}
