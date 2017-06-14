package nna.base.util.concurrent;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author NNA-SHUAI
 * @create 2017-06-13 13:44
 **/



 class Tasks{
     static final int INIT=0;
     static final int WORKING=1;
     static final int END=2;
     //limit we want to user container as this:can auto resize and gc non using null slot;
     private volatile AbstractTask[] list;//for 有序的 task
     private volatile Object[] objects;//oom-limit : a large of
     private ReentrantLock[] locks;
     private volatile int[] status;
    // waste memory with null slot except that task is been worked with short time
     private volatile Integer enQueueIndex;
     private volatile Integer workIndex;
     private Integer workCount;
     private AtomicInteger sequenceGen=new AtomicInteger();
     private boolean isTaskEnQueueBySeq;//任务队列工作是否按照数组顺序工作
    /*
    * only adapt for One Producer(business thread producer) and One Consumer Thread
    * */

     Tasks(int taskCount,boolean keepTaskSeq){
        enQueueIndex=0;
        workCount=taskCount;
        workIndex=0;
        this.isTaskEnQueueBySeq=keepTaskSeq;
        list=new AbstractTask[taskCount];
        objects=new Object[taskCount];
        status=new int[taskCount];
        locks=new ReentrantLock[taskCount];
        for(int index=0;index < taskCount;index++){
            locks[index]=new ReentrantLock();
            status[index]=INIT;
        }
    }


    void works(ConcurrentHashMap<Long,Tasks> workMap) throws IOException {
        AbstractTask abstractTask;
        int temp=enQueueIndex;//为了尽可能的照顾所有的 Tasks对象
        for(;workIndex < temp;workIndex++){
            abstractTask=list[workIndex];
            if(abstractTask==null){
                break;
            }
            // for 乐观锁 ; for performance
            if(!canWork(workIndex)){
                continue;
            }
            work(abstractTask,workMap,workIndex);
        }
        int tempIndex=workIndex;
        if(!isTaskEnQueueBySeq){
            for(;tempIndex<workCount;tempIndex++){
                abstractTask=list[tempIndex];
                if(abstractTask!=null){
                    work(abstractTask,workMap,tempIndex);
                }
            }
        }
    }

    private boolean canWork(int index){
        return status[index]!=WORKING&&status[index]!=END;
    }

    private void work(
            AbstractTask abstractTask,
            ConcurrentHashMap<Long,Tasks> workMap,
            int tempIndex){
        ReentrantLock lock=locks[tempIndex];
        try{
            lock.lock();
            if(canWork(tempIndex)){
               status[tempIndex]=WORKING;
               Object attach=objects[tempIndex];
               work(abstractTask,attach,workMap,tempIndex);
               status[workIndex]=END;
            }
        }catch (Exception e){
            e.getMessage();
        }finally {
            lock.unlock();
        }
    }

    private void setNull(Integer workIndex) {
        list[workIndex]=null;
        objects[workIndex]=null;
    }

    private void work(AbstractTask abstractTask,Object attach,ConcurrentHashMap<Long,Tasks> workMap,int index) throws IOException {
        int taskStatus=abstractTask.getTaskStatus();
        switch (taskStatus){
            case AbstractTask.TASK_STATUS_DESTROY:
                abstractTask.destroy(attach);
                workMap.remove(abstractTask.getIndex());
                break;
            case AbstractTask.TASK_STATUS_WORK:
                abstractTask.work(attach);
                break;
            case AbstractTask.TASK_STATUS_INIT:
                abstractTask.init(attach);
                break;
            default:
                abstractTask.otherWork(attach);
        }
        setNull(index);
    }

    void addTask(AbstractTask abstractTask,Object attach){
        int seq=sequenceGen.getAndIncrement();
        list[seq]=abstractTask;//一定可以保证有序，当前只有业务线程来处理
        objects[seq]=attach;// we must set task firstly and increment enQueueIndex secondly for safely works()
        enQueueIndex++;
    }

    public AbstractTask[] getList() {
        return list;
    }

    public void setList(AbstractTask[] list) {
        this.list = list;
    }

}
