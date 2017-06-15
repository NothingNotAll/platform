package nna.base.util.concurrent;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author NNA-SHUAI
 * @create 2017-06-13 13:44
 **/
// exe sequence alg: waitTime*priorLevel and asc to execute;
//init Step too late may lead to many tasks to be no work;
//Single Direction Insert Task , can not make use of
// Been Set Null ' s slot;
// but we can solve it with recycle queue to make full use of memory and solve the oom problem
 abstract class AbstractTasks {
     public static final int START=0;
     public static final int WORKING=1;
     public static final int END=2;

    protected Long startTime;
    protected Long endTime;
    protected Long priorLevel;//used as exe sequence of tasks; but worker must used ArrayList as tasks container and index as the priorLevel;
     //limit we want to user container as this:can auto resize and gc non using null slot;
    protected volatile AbstractTask[] list;//for 有序的 task
    protected volatile Long[] taskStartTimes;
    protected volatile Long[] taskEndTimes;
    protected volatile Long[] taskPriorLevels;//used as in the one task , the sequence of exe;
    protected volatile int[] taskTypes;//
    protected volatile int[] status;
    protected volatile Object[] objects;//oom-limit : a large of
    protected ReentrantLock[] locks;

    // waste memory with null slot except that task is been worked with short time
    protected volatile Integer enQueueIndex;
    protected volatile Integer workIndex;
    protected Integer workCount;

    protected Integer beenWorkedCount;
    protected ReentrantLock lock=new ReentrantLock();
    protected AtomicInteger sequenceGen=new AtomicInteger();

    /*
    * only adapt for One Producer(business thread producer) and One Consumer Thread
    * */

     AbstractTasks(int taskCount){
        startTime=System.currentTimeMillis();
        enQueueIndex=0;
        workCount=taskCount;
        workIndex=0;
        beenWorkedCount=0;
        list=new AbstractTask[taskCount];
        taskTypes=new int[taskCount];
        objects=new Object[taskCount];
        status=new int[taskCount];
        locks=new ReentrantLock[taskCount];
        taskPriorLevels=new Long[taskCount];
        taskEndTimes=new Long[taskCount];
        taskStartTimes=new Long[taskCount];
        for(int index=0;index < taskCount;index++){
            locks[index]=new ReentrantLock();
            status[index]=START;
            taskPriorLevels[index]=0L;
        }
    }


    protected abstract AbstractTask doTasks(ConcurrentHashMap<Long, AbstractTasks> workMap);


    protected void lockAndExe(
            AbstractTask abstractTask,
            ConcurrentHashMap<Long,AbstractTasks> workMap,
            int tempIndex){
        ReentrantLock lock=locks[tempIndex];
        try{
            lock.lock();
            if(status[tempIndex]==START){
               status[tempIndex]=WORKING;
               Object attach=objects[tempIndex];
               work(abstractTask,attach,workMap,taskTypes[tempIndex]);
               Long endTime=System.currentTimeMillis();
               taskEndTimes[tempIndex]=endTime;
               status[tempIndex]=END;
               setNull(tempIndex);
               beenWorkedCount++;
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
            int taskType,
            Object attach){
        Long startTime=System.currentTimeMillis();
        int seq=sequenceGen.getAndIncrement();
        ReentrantLock lock=null;
        try{
            lock=locks[seq];
            lock.lock();
            taskStartTimes[seq]=startTime;
            list[seq]=abstractTask;//一定可以保证有序，当前只有业务线程来处理
            taskTypes[seq]=taskType;
            objects[seq]=attach;// we must set task firstly and increment enQueueIndex secondly for safely works()
            enQueueIndex=seq+1;
        }catch (Exception e){
            System.out.println(seq);
            e.printStackTrace();
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

    public Integer getBeenWorkedCount() {
        return beenWorkedCount;
    }

    public void setBeenWorkedCount(Integer beenWorkedCount) {
        this.beenWorkedCount = beenWorkedCount;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getPriorLevel() {
        return priorLevel;
    }

    public void setPriorLevel(Long priorLevel) {
        this.priorLevel = priorLevel;
    }
}
