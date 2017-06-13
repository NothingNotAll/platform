package nna.base.util.concurrent;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author NNA-SHUAI
 * @create 2017-06-13 10:12
 **/

public class NewWorker implements Runnable{

    private class Tasks{
        private AbstractTask[] list;//for 有序的 task
        private volatile Integer currentWorkIndex;
        private Integer workCount;
        private Integer beenWorkedCount;
        private boolean keepTaskSeq;

        public Tasks(
                int taskCount,
                boolean keepTaskSeq
        ){
            currentWorkIndex=-1;
            workCount=taskCount;
            this.keepTaskSeq=keepTaskSeq;
            list=new AbstractTask[taskCount];
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
                consumer(tempWorkList);
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
        index=-1;
        tempTasks=null;
        currentTask=null;
    }

    private void consumer(LinkedList<Tasks> tempWorkList) {
        iterator=tempWorkList.iterator();
        while(iterator.hasNext()){
            currentTasks=iterator.next();
            work(currentTasks);
        }
    }
    private int index;
    private AbstractTask[] tempTasks;
    private AbstractTask currentTask;
    private int iteratorIndex=0;
    private void work(Tasks currentTasks) {
        tempTasks=currentTasks.list;
        if (currentTasks.keepTaskSeq){
            index=currentTasks.currentWorkIndex+1;
            currentTask=tempTasks[index];
            if(currentTask!=null){
                workCurrentTask(currentTask);
                tempTasks[index]=null;
                currentTasks.currentWorkIndex=index;
            }
        }else {
            for(;iteratorIndex<currentTasks.workCount;iteratorIndex++){
                currentTask=tempTasks[iteratorIndex];
                workCurrentTask(currentTask);
                tempTasks[iteratorIndex]=null;
                currentTasks.currentWorkIndex=iteratorIndex;
            }
        }
    }

    private void workCurrentTask(AbstractTask currentTasks) {

    }

}
