package nna.base.util.concurrent;


import nna.base.bean.Clone;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author NNA-SHUAI
 * @create 2017-06-13 10:12
 **/

public class Worker<T extends AbstractTask> extends Clone implements Runnable{

    public static AtomicLong taskNo=new AtomicLong(0L);
    private static final Long serialVersionUID=-1L;
    private Integer loadNo;
    /*
        * 为了业务线程的尽可能的不阻塞，将锁竞争降低到 单条线程之间的竞争：Worker线程与业务线程之间的锁竞争。
        * */
    private ConcurrentHashMap<Long,Tasks> workMap=new ConcurrentHashMap<Long, Tasks>();
    private LinkedBlockingQueue<Tasks> workQueue=new LinkedBlockingQueue<Tasks>();

    private int tempWorkCount;
    private Tasks blockTasks;
    private LinkedList<Tasks> tempWorkList=new LinkedList<Tasks>();
    private Iterator<Tasks> iterator;
    private Tasks currentTasks;
    public void run() {
        try{
            while(true){
                tempWorkCount=workQueue.size();
                if(tempWorkCount > 0){
                    //性能瓶頸點
                    workQueue.drainTo(tempWorkList,tempWorkCount);
                }else{
                    blockTasks=workQueue.take();
                    tempWorkList.add(blockTasks);
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
        tempWorkList=new LinkedList<Tasks>();
        blockTasks=null;
        tempWorkCount=0;
        currentTasks=null;
    }

    private void consumer(LinkedList<Tasks> tempWorkList) throws IOException {
        iterator=tempWorkList.iterator();
        while(iterator.hasNext()){
            currentTasks=iterator.next();
            currentTasks.works(workMap);//这里 IO 阻塞越短越好，CPU利用率越高。吞吐量就越大。
        }
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

    public ConcurrentHashMap<Long, Tasks> getWorkMap() {
        return workMap;
    }

    public void setWorkMap(ConcurrentHashMap<Long, Tasks> workMap) {
        this.workMap = workMap;
    }

    public LinkedBlockingQueue<Tasks> getWorkQueue() {
        return workQueue;
    }

    public void setWorkQueue(LinkedBlockingQueue<Tasks> workQueue) {
        this.workQueue = workQueue;
    }
}
