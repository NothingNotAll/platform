package nna.base.util.concurrent;

import nna.base.bean.Clone;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * multi producer thread and multi consumer design;
 * @author NNA-SHUAI
 * @create 2017-05-29 18:44
 **/

public class Worker<T extends AbstractTask> extends Clone implements Runnable{
    private int loadNo;
    private LinkedBlockingQueue<T> workerQueue;
    private ConcurrentHashMap<Long,LinkedBlockingQueue<T>> threadsWorkerMap;

    //for gc performance and monitor
    private LinkedList<T> tempWorker=new LinkedList<T>();
    private AbstractTask task;
    private LinkedBlockingQueue<T> queue=null;
    private int count;
    private volatile int workerCount;
    private LinkedList<T> ts=new LinkedList<T>();
    private AbstractTask blockTask;

    public void run() {
        try{
            while(true){
                workerCount=workerQueue.size();
                if(workerCount>0){
                    workerQueue.drainTo(tempWorker,workerCount);
                }else{
                    blockTask=workerQueue.take();
                    tempWorker.addLast((T)blockTask);
                }
                Iterator<T> iterator=tempWorker.iterator();
                consumer(iterator);
                destroy();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {

        }
    }

    private void consumer(Iterator<T> iterator) {
        while(iterator.hasNext()){
            task=iterator.next();
            queue=threadsWorkerMap.get(task.getIndex());
            count=queue.size();
            queue.drainTo(ts,count);
            work(ts);
        }
    }

    public void submitTask(AbstractTask abstractTask) {
        Long threadId=abstractTask.getThreadId();
        LinkedBlockingQueue<T> linkedBlockingQueue=threadsWorkerMap.get(threadId);
        linkedBlockingQueue.add((T)abstractTask);//there can be upgrade with 乐观锁；
    }

    public void submitInitTask(AbstractTask abstractTask){
        Long workIndex=abstractTask.getIndex();
        LinkedBlockingQueue<T> linkedBlockingQueue=new LinkedBlockingQueue<T>();
        linkedBlockingQueue.add((T)abstractTask);
        threadsWorkerMap.put(workIndex,linkedBlockingQueue);
    }

    //for GC optimize
    private AbstractTask t;
    private Iterator<T> iterator;
    private void work(LinkedList<T> ts){
        iterator=ts.iterator();
        while(iterator.hasNext()){
            t=iterator.next();
            switch (t.getTaskStatus()){
                case AbstractTask.TASK_STATUS_CREATE:
                    t.create();
                    break;
                case AbstractTask.TASK_STATUS_INIT:
                    t.init();
                    break;
                case AbstractTask.TASK_STATUS_WORK:
                    t.work();
                    break;
                case AbstractTask.TASK_STATUS_DESTROY:
                    threadsWorkerMap.remove(t.getIndex());
                    t.destroy();
                    break;
                case AbstractTask.TASK_STATUS_OTHER:
                    t.otherWork();
                    break;
            }
        }
    }

    private void destroy() {
        count=0;
        tempWorker=new LinkedList<T>();
        ts=new LinkedList<T>();
        task=null;
        queue=null;
        workerCount=0;
    }

    public int getLoadNo() {
        return loadNo;
    }

    public void setLoadNo(int loadNo) {
        this.loadNo = loadNo;
    }

    public int getWorkerCount() {
        return workerCount;
    }

    public void setWorkerCount(int workerCount) {
        this.workerCount = workerCount;
    }

}
