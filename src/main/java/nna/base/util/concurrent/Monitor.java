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
    private Map map;
    private int index=0;
    private LinkedBlockingQueue<AbstractTaskWrapper>[] list;
    public void run() {
        while(true){
            System.out.println(simpleDateFormat.format(System.currentTimeMillis())+"-TOTAL TASKS'S WAIT SIZE:"+map.size());
            for(LinkedBlockingQueue tep:list){
                System.out.println(simpleDateFormat.format(System.currentTimeMillis())+"-TOTAL TASKS'S WAIT SIZE:"+tep.size());
            }

            try {
                Thread.currentThread().sleep(7000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public LinkedBlockingQueue<AbstractTaskWrapper>[] getList() {
        return list;
    }

    public void setList(LinkedBlockingQueue<AbstractTaskWrapper>[] list) {
        this.list = list;
    }
}
