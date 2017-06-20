package nna.base.util.concurrent;


import nna.base.bean.Clone;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 时间片机制
 * 优先级机制
 * the sequence of execute task List 's alg may be customer
 * @author NNA-SHUAI
 * @create 2017-06-13 10:12
 **/

public class TaskSchedule<T extends AbstractTask> extends Clone implements Runnable{

    private static final Long serialVersionUID=-1L;
    private ExecutorService service= Executors.newCachedThreadPool();
    private Integer loadNo;
    /*
        * 为了业务线程的尽可能的不阻塞，将锁竞争降低到 单条线程之间的竞争：Worker线程与业务线程之间的锁竞争。
        * */
    //in the future this must be replaced of ArrayListBlockingQueue;and used index as the priorLevel;
    private LinkedBlockingQueue<AbstractTasks> workQueue=new LinkedBlockingQueue<AbstractTasks>();

    private int tempWorkCount;
    private AbstractTasks blockAbstractTasks;
    private LinkedList<AbstractTasks> tempWorkList=new LinkedList<AbstractTasks>();
    private Iterator<AbstractTasks> iterator;
    private AbstractTasks currentAbstractTasks;

    public void run() {
        try{
            while(true){
                tempWorkCount=workQueue.size();
                if(tempWorkCount > 0){
                    //性能瓶頸點
                    tempWorkCount=workQueue.drainTo(tempWorkList,tempWorkCount);
                }else{
                    blockAbstractTasks =workQueue.take();
                    tempWorkList.add(blockAbstractTasks);
                    tempWorkCount=1;
                }
                try{
                    consumer(tempWorkList);
                }catch (Exception e){
                    e.printStackTrace();
                }
                destroy();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void destroy() {
        tempWorkList=new LinkedList<AbstractTasks>();
        blockAbstractTasks =null;
        tempWorkCount=0;
        currentAbstractTasks =null;
    }

    private void consumer(LinkedList<AbstractTasks> tempWorkList) throws IOException {
        iterator=tempWorkList.iterator();
        while(iterator.hasNext()){
            currentAbstractTasks =iterator.next();
            check(currentAbstractTasks);
            service.submit(currentAbstractTasks);
        }
    }

    private void check(AbstractTasks currentAbstractTasks) {
    }

    public int getTempWorkCount() {
        return tempWorkCount;
    }

    public void setTempWorkCount(int tempWorkCount) {
        this.tempWorkCount = tempWorkCount;
    }

    public Integer getLoadNo() {
        return loadNo;
    }

    public void setLoadNo(Integer loadNo) {
        this.loadNo = loadNo;
    }

    public LinkedBlockingQueue<AbstractTasks> getWorkQueue() {
        return workQueue;
    }

    public void setWorkQueue(LinkedBlockingQueue<AbstractTasks> workQueue) {
        this.workQueue = workQueue;
    }
}
