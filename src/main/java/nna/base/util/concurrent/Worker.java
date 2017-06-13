package nna.base.util.concurrent;


import nna.base.bean.Clone;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
        int seq=temp.sequenceGen.getAndIncrement();
        temp.list[seq]=t;//一定可以保证有序，当前只有业务线程来处理
        temp.objects[seq]=object;
        return temp;
    }

    Tasks submitInitEvent(T t,Object object,boolean keepWorkSeq) {
        Tasks tasks=new Tasks(t.getWorkCount(),keepWorkSeq);
        tasks.initObject=object;
        Long taskSeq=taskNo.getAndDecrement();
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
        iteratorIndex=0;
        tempTasks=null;
        currentTask=null;
        temp=null;
        os=null;
    }

    private void consumer(LinkedList<Tasks> tempWorkList) throws IOException {
        iterator=tempWorkList.iterator();
        while(iterator.hasNext()){
            currentTasks=iterator.next();
            work(currentTasks);
        }
    }
    private AbstractTask[] tempTasks;
    private AbstractTask currentTask;
    private int iteratorIndex=0;
    private int workCount;
    private Object[] os;
    private Object temp;
    private int taskStatus;
    private void work(Tasks currentTasks) throws IOException {
        os=currentTasks.objects;
        workCount=currentTasks.workCount;
        tempTasks=currentTasks.list;
        iteratorIndex=currentTasks.currentWorkIndex;
        for(;iteratorIndex<workCount;iteratorIndex++){
            currentTask=tempTasks[iteratorIndex];
            if(currentTasks==null){
                break;
            }
            if(!currentTask.isInit()){
                return ;
            }
            temp=os[iteratorIndex];
            workCurrentTask(currentTask,temp);
            tempTasks[iteratorIndex]=null;
            os[iteratorIndex]=null;
            currentTasks.beenWorkedCount=currentTasks.beenWorkedCount++;
        }
        currentTasks.currentWorkIndex=iteratorIndex;
        if (!currentTasks.keepTaskSeq){
            for(;iteratorIndex<workCount;iteratorIndex++){
                currentTask=tempTasks[iteratorIndex];
                if(currentTasks!=null){
                    temp=os[iteratorIndex];
                    workCurrentTask(currentTask,temp);
                    tempTasks[iteratorIndex]=null;
                    os[iteratorIndex]=null;
                    currentTasks.beenWorkedCount=currentTasks.beenWorkedCount++;;
                }
            }
        }
    }
    private void workCurrentTask(AbstractTask currentTasks,Object object) throws IOException {
        taskStatus=currentTasks.getTaskStatus();
        switch (taskStatus){
            case AbstractTask.TASK_STATUS_DESTROY:
                currentTasks.destroy(object);
                workMap.remove(currentTasks.getIndex());
                break;
            case AbstractTask.TASK_STATUS_WORK:
                currentTasks.work(object);
                break;
            default:
                currentTasks.otherWork(object);
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
