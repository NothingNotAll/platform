package nna.base.util.concurrent;

import nna.base.bean.Clone;

import java.io.IOException;
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
    private class TaskWrapper{
        private T t;
        private Object object;

        private TaskWrapper(
                T t,
                Object object
        ){
            this.t=t;
            this.object=object;
        }
    }
    private int loadNo;
    private LinkedBlockingQueue<TaskWrapper> workerQueue;
    private ConcurrentHashMap<Long,LinkedBlockingQueue<TaskWrapper>> threadsWorkerMap;

    public Worker(){
        workerQueue=new LinkedBlockingQueue<TaskWrapper>();
        threadsWorkerMap=new ConcurrentHashMap<Long, LinkedBlockingQueue<TaskWrapper>>();
    }

    //for gc performance and monitor
    private LinkedList<TaskWrapper> tempWorker=new LinkedList<TaskWrapper>();
    private LinkedBlockingQueue<TaskWrapper> queue=null;
    private int count;
    private volatile int workerCount;
    private LinkedList<TaskWrapper> ts=new LinkedList<TaskWrapper>();
    private TaskWrapper blockTask;

    public void run() {
        try{
            while(true){
                workerCount=workerQueue.size();
                if(workerCount>0){
                    workerQueue.drainTo(tempWorker,workerCount);
                }else{
                    blockTask=workerQueue.take();
                    tempWorker.addLast(blockTask);
                }
                Iterator<TaskWrapper> iterator=tempWorker.iterator();
                consumer(iterator);
                destroy();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {

        }
    }

    private TaskWrapper task;
    private void consumer(Iterator<TaskWrapper> iterator) {
        while(iterator.hasNext()){
            task=iterator.next();
            queue=threadsWorkerMap.get(task.t.getIndex());
            count=queue.size();
            queue.drainTo(ts,count);
            work(ts);
        }
    }

    public void submitTask(AbstractTask abstractTask,Object object) {
        Long threadId=abstractTask.getThreadId();
        LinkedBlockingQueue<TaskWrapper> linkedBlockingQueue=threadsWorkerMap.get(threadId);
        linkedBlockingQueue.add(new TaskWrapper((T)abstractTask,object));//there can be upgrade with 乐观锁；
    }

    public void submitInitTask(AbstractTask abstractTask,Object object){
        Long workIndex=abstractTask.getIndex();
        LinkedBlockingQueue<TaskWrapper> linkedBlockingQueue=new LinkedBlockingQueue<TaskWrapper>();
        threadsWorkerMap.putIfAbsent(workIndex,linkedBlockingQueue);
        linkedBlockingQueue=threadsWorkerMap.get(workIndex);
        linkedBlockingQueue.add(new TaskWrapper((T)abstractTask,object));
    }

    //for GC optimize
    private TaskWrapper t;
    private AbstractTask abstractTask;
    private Object object;
    private Iterator<TaskWrapper> iterator;
    private void work(LinkedList<TaskWrapper> ts){
        iterator=ts.iterator();
        while(iterator.hasNext()){
            t=iterator.next();
            abstractTask=t.t;
            object=t.object;
            switch (abstractTask.getTaskStatus()){
                case AbstractTask.TASK_STATUS_INIT:
                    abstractTask.init(object);
                    break;
                case AbstractTask.TASK_STATUS_WORK:
                    abstractTask.work(object);
                    break;
                case AbstractTask.TASK_STATUS_DESTROY:
                    threadsWorkerMap.remove(abstractTask.getIndex());
                    try {
                        abstractTask.destroy(object);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    abstractTask.otherWork(object);
                    break;
            }
        }
    }

    private void destroy() {
        count=0;
        tempWorker=new LinkedList<TaskWrapper>();
        ts=new LinkedList<TaskWrapper>();
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
