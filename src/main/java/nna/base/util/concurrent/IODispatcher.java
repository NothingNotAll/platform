package nna.base.util.concurrent;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author NNA-SHUAI
 * @create 2017-06-15 11:40
 **/

public class IODispatcher implements Runnable {
    private AbstractIOTasks temp;
    private IOEventProcessor IOEventProcessor;

    public void run() {
        LinkedBlockingQueue<AbstractIOTasks> queue= IOEventProcessor.getWorkQueue();
        //性能瓶頸點
        queue.add(temp);
    }

    public IODispatcher(AbstractIOTasks abstractIOTasks,
                        IOEventProcessor IOEventProcessor){
        this.temp= abstractIOTasks;
        this.IOEventProcessor = IOEventProcessor;
    }
}
