package nna.base.util.concurrent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author NNA-SHUAI
 * @create 2017-06-13 13:43
 **/

 class Dispatcher implements Runnable{
    private Tasks temp;
    private Worker worker;
    private boolean isMapDispatch;

    public void run() {
        worker.getWorkQueue().add(temp);
        if(isMapDispatch){
            worker.getWorkMap().putIfAbsent(temp.list[0].getIndex(),temp);
        }
    }

    public Dispatcher(Tasks tasks,
                      Worker worker,
                      boolean isMapDispatch){
        this.temp=tasks;
        this.isMapDispatch=isMapDispatch;
        this.worker=worker;
    }
}
