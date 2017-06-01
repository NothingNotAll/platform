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

public abstract class Worker<T extends AbstractTask> extends Clone implements Runnable{
    private int loadNo;
    private LinkedBlockingQueue<T> workerQueue;
    private ConcurrentHashMap<Long,LinkedBlockingQueue<T>> threadsWorkerMap;

    //for performance and monitor
    private LinkedList<T> tempWorker=new LinkedList<T>();
    private AbstractTask task;
    private LinkedBlockingQueue<T> queue=null;
    private int count;
    private volatile int workerCount;
    private LinkedList<T> ts=new LinkedList<T>();


    public void run() {
        try{
            while(true){
                workerCount=workerQueue.size();
                if(workerCount>0){
                    workerQueue.drainTo(tempWorker,workerCount);
                }else{
                    AbstractTask task=workerQueue.take();
                    tempWorker.addLast((T)task);
                }
                Iterator<T> iterator=tempWorker.iterator();
                consumer(iterator);
                reSet();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {

        }
    }

    private void consumer(Iterator<T> iterator) {
        while(iterator.hasNext()){
            task=iterator.next();
            queue=threadsWorkerMap.get(task.getThreadId());
            count=queue.size();
            queue.drainTo(ts,count);
            work(ts);
        }
    }

    void submitTask(AbstractTask t){

    }

    private void work(LinkedList<T> ts){
        AbstractTask t;
        Iterator<T> iterator=ts.iterator();
        while(iterator.hasNext()){
            t=iterator.next();
            switch (t.getTaskStatus()){
                case AbstractTask.TASK_STATUS_CREATE:
                    break;
                case AbstractTask.TASK_STATUS_INIT:
                    break;
                case AbstractTask.TASK_STATUS_WORK:
                    t.work();
                    break;
                case AbstractTask.TASK_STATUS_DESTROY:
                    break;
                default:
                    otherWork();
            }
        }
    }

    protected abstract void otherWork();

    private void reSet() {
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
