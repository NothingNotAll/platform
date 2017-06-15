package nna.base.util.concurrent;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author NNA-SHUAI
 * @create 2017-06-13 13:44
 **/


//Single Direction Insert Task , can not make use of
// Been Set Null ' s slot;
// but we can solve it with recycle queue to make full use of memory and solve the oom problem
 abstract class AbstractTasks {
     public static final int INIT=0;
     public static final int WORKING=1;
     public static final int END=2;

     //limit we want to user container as this:can auto resize and gc non using null slot;
    protected volatile AbstractTask[] list;//for 有序的 task
    protected volatile int[] taskStatus;//
    protected volatile Object[] objects;//oom-limit : a large of
    protected ReentrantLock[] locks;
    protected volatile int[] status;
    // waste memory with null slot except that task is been worked with short time
    protected volatile Integer enQueueIndex;
    protected volatile Integer workIndex;
    protected Integer workCount;
    protected AtomicInteger sequenceGen=new AtomicInteger();

    /*
    * only adapt for One Producer(business thread producer) and One Consumer Thread
    * */

     AbstractTasks(int taskCount){
        enQueueIndex=0;
        workCount=taskCount;
        workIndex=0;
        list=new AbstractTask[taskCount];
        taskStatus=new int[taskCount];
        objects=new Object[taskCount];
        status=new int[taskCount];
        locks=new ReentrantLock[taskCount];
        for(int index=0;index < taskCount;index++){
            locks[index]=new ReentrantLock();
            status[index]=INIT;
        }
    }


    abstract void works(ConcurrentHashMap<Long,AbstractTasks> workMap) throws IOException ;

    protected void lock(
            AbstractTask abstractTask,
            ConcurrentHashMap<Long,AbstractTasks> workMap,
            int tempIndex){
        ReentrantLock lock=locks[tempIndex];
        try{
            lock.lock();
            if(status[tempIndex]==INIT){
               status[tempIndex]=WORKING;
               Object attach=objects[tempIndex];
               work(abstractTask,attach,workMap,taskStatus[tempIndex]);
               status[workIndex]=END;
               setNull(tempIndex);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    private void setNull(Integer workIndex) {
        list[workIndex]=null;
        objects[workIndex]=null;
    }

    private void work(AbstractTask abstractTask,
                      Object attach,
                      ConcurrentHashMap<Long,AbstractTasks> workMap,
                      int status) throws IOException {
        abstractTask.doTask(status,attach);
    }

    void addTask(
            AbstractTask abstractTask,
            int workStatus,
            Object attach){
        int seq=sequenceGen.getAndIncrement();
        ReentrantLock lock=locks[seq];
        try{
            lock.lock();
            list[seq]=abstractTask;//一定可以保证有序，当前只有业务线程来处理
            taskStatus[seq]=workStatus;
            objects[seq]=attach;// we must set task firstly and increment enQueueIndex secondly for safely works()
            enQueueIndex=seq+1;
        }finally {
            lock.unlock();
        }
    }

    public AbstractTask[] getList() {
        return list;
    }

    public void setList(AbstractTask[] list) {
        this.list = list;
    }

}
