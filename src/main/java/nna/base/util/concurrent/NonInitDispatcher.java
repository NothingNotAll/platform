package nna.base.util.concurrent;

/**
 * @author NNA-SHUAI
 * @create 2017-06-15 11:40
 **/

public class NonInitDispatcher implements Runnable {
    private AbstractTasks temp;
    private Worker worker;

    public void run() {
        //性能瓶頸點
        worker.getWorkQueue().add(temp);
    }

    public NonInitDispatcher(AbstractTasks abstractTasks,
                          Worker worker){
        this.temp= abstractTasks;
        this.worker=worker;
    }
}
