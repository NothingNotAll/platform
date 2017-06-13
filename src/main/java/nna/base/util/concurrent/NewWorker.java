package nna.base.util.concurrent;


import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author NNA-SHUAI
 * @create 2017-06-13 10:12
 **/

public class NewWorker<T extends AbstractTask> implements Runnable{

    private AtomicLong taskNo=new AtomicLong(0L);

    public void submitEvent(T t) {
        Long taskSeq=t.getIndex();
        Tasks temp=workMap.get(taskSeq);
        temp.list[temp.sequenceGen.getAndIncrement()]=t;//一定可以保证有序，当前只有业务线程来处理
        workQueue.add(temp);
    }

    public void submitInitEvent(T t,boolean keepWorkSeq) {
        Tasks tasks=new Tasks(t.getWorkCount(),keepWorkSeq);
        Long taskSeq=taskNo.getAndDecrement();
        t.setIndex(taskSeq);
        workMap.putIfAbsent(taskSeq,tasks);
        workQueue.add(tasks);
    }

    private class Tasks{
        private volatile AbstractTask[] list;//for 有序的 task
        private volatile Object[] objects;
        private volatile Integer currentWorkIndex;
        private Integer workCount;
        private Integer beenWorkedCount;
        private AtomicInteger sequenceGen=new AtomicInteger();
        private boolean keepTaskSeq;

        private Tasks(
                int taskCount,
                boolean keepTaskSeq
        ){
            currentWorkIndex=-1;
            workCount=taskCount;
            this.keepTaskSeq=keepTaskSeq;
            list=new AbstractTask[taskCount];
        }

        private void insert(AbstractTask abstractTask,Object object){
            Integer no=sequenceGen.getAndIncrement();
            list[no]=abstractTask;
            objects[no]=object;
        }
    }
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
            case AbstractTask.TASK_STATUS_INIT:
                currentTasks.init(object);
                break;
            case AbstractTask.TASK_STATUS_WORK:
                currentTasks.work(object);
                break;
            default:
                currentTasks.otherWork(object);
        }
    }

}
