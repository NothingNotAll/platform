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

    private static AtomicLong taskNo=new AtomicLong(0L);

    private Integer loadNo;

    /*
        * 为了业务线程的尽可能的不阻塞，将锁竞争降低到 单条线程之间的竞争：Worker线程与业务线程之间的锁竞争。
        * */
    private ConcurrentHashMap<Long,Tasks> workMap=new ConcurrentHashMap<Long, Tasks>();
    private LinkedBlockingQueue<Tasks> workQueue=new LinkedBlockingQueue<Tasks>();

    Tasks submitEvent(T t,Object object) {
        Long taskSeq=t.getIndex();
        Tasks temp=workMap.get(taskSeq);
        temp.addTask(t,object);
        return temp;
    }

    Tasks submitInitEvent(T t,Object object,boolean keepWorkSeq) {
        Tasks tasks=new Tasks(t.getWorkCount(),keepWorkSeq);
        tasks.addTask(t,object);
        Long taskSeq=taskNo.getAndIncrement();//性能瓶頸點
        t.setIndex(taskSeq);
        return tasks;
    }

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
        tempWorkList.clear();
        blockTasks=null;
        tempWorkCount=0;
        iterator=null;
        currentTasks=null;
    }

    private void consumer(LinkedList<Tasks> tempWorkList) throws IOException {
        iterator=tempWorkList.iterator();
        while(iterator.hasNext()){
            currentTasks=iterator.next();
            currentTasks.works(workMap);
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
