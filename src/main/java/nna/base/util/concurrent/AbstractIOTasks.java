package nna.base.util.concurrent;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/*
* THE BEST queue alg is that : order by en_queue_time:System.currentTimeMillis() use it as the index of list;
* this can be the best Concurrent FrameWork design;
*
* */
/**
 * @author NNA-SHUAI
 * @create 2017-06-13 13:44
 **/
// exe sequence alg: waitTime*priorLevel and asc to execute;
//init Step too late may lead to many tasks to be no work;
//Single Direction Insert Task , can not make use of
// Been Set Null ' s slot;
// but we can solve it with recycle queue to make full use of memory and solve the oom problem
 abstract class AbstractIOTasks {
     public static final int START=0;
     public static final int WORKING=1;
     public static final int END=2;
     public static final int FAIL=3;

    protected Long startTime;
    protected Long endTime;
    protected Long priorLevel;//used as exe sequence of tasks; but worker must used ArrayList as tasks container and index as the priorLevel;
     //limit we want to user container as this:can auto resize and gc non using null slot;
    protected volatile AbstractIOTask[] list;//for 有序的 task
    protected volatile Long[] taskStartTimes;
    protected volatile Long[] taskEndTimes;
    protected volatile Long[] taskPriorLevels;//used as in the one task , the sequence of exe;
    protected volatile int[] taskTypes;//
    protected volatile int[] status;
    protected volatile Object[] objects;//oom-limit : a large of
    protected ReentrantLock[] locks;
    protected Long globalWorkId;
    protected boolean isWorkSeq;

    // waste memory with null slot except that task is been worked with short time
    protected volatile Integer enQueueIndex;
    protected volatile Integer workIndex;
    protected Integer workCount;

    protected AtomicLong counter;
    protected AtomicInteger sequenceGen=new AtomicInteger();

    /*
    * only adapt for One Producer(business thread producer) and One Consumer Thread
    * */

     AbstractIOTasks(int taskCount, Long globalWorkId,boolean isWorkSeq){
        startTime=System.currentTimeMillis();
        this.isWorkSeq=isWorkSeq;
        this.globalWorkId=globalWorkId;
        enQueueIndex=0;
        workCount=taskCount;
        workIndex=0;
        counter=new AtomicLong(Long.valueOf(taskCount).longValue());
        list=new AbstractIOTask[taskCount];
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

    protected abstract boolean doTasks();


    protected abstract int lockAndExe(
            AbstractIOTask abstractIOTask,
            int tempIndex);



    protected void setNull(Integer workIndex) {
        list[workIndex]=null;
        objects[workIndex]=null;
    }

    protected int work(AbstractIOTask abstractIOTask,
                      int tempIndex) {
        int taskType=this.taskTypes[tempIndex];
        if(status[tempIndex]==START){
            status[tempIndex]=WORKING;
            Object attach=objects[tempIndex];
            int status=this.status[tempIndex];
            try{
                abstractIOTask.doTask(status,attach);
                this.status[tempIndex]=END;
                Long endTime=System.currentTimeMillis();
                taskEndTimes[tempIndex]=endTime;
            }catch (Exception e){
                e.printStackTrace();
                this.status[tempIndex]=FAIL;
            }finally {
                counter.getAndDecrement();
            }
            setNull(tempIndex);
        }
        return taskType;
    }

    void addTask(
            AbstractIOTask abstractIOTask,
            int taskType,
            Object attach){
        Long startTime=System.currentTimeMillis();
        int seq=sequenceGen.getAndIncrement();
        ReentrantLock lock=null;
        try{
            lock=locks[seq];
            lock.lock();
            taskStartTimes[seq]=startTime;
            list[seq]= abstractIOTask;//一定可以保证有序，当前只有业务线程来处理
            taskTypes[seq]=taskType;
//            System.out.println(taskType+"-"+seq);
            objects[seq]=attach;// we must set task firstly and increment enQueueIndex secondly for safely works()
            enQueueIndex=seq+1;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public AbstractIOTask[] getList() {
        return list;
    }

    public void setList(AbstractIOTask[] list) {
        this.list = list;
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

    public Long getGlobalWorkId() {
        return globalWorkId;
    }

    public void setGlobalWorkId(Long globalWorkId) {
        this.globalWorkId = globalWorkId;
    }

    public boolean isWorkSeq() {
        return isWorkSeq;
    }

    public void setWorkSeq(boolean workSeq) {
        isWorkSeq = workSeq;
    }
}
