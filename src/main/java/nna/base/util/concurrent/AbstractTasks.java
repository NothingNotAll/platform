package nna.base.util.concurrent;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;
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
 abstract class AbstractTasks implements Runnable{
    public static final int START=1;
    public static final int WORKING=2;
    public static final int END=3;
    public static final int FAIL=4;

    protected Long startTime;
    protected Long endTime;
    protected Long priorLevel;//used as exe sequence of tasks; but worker must used ArrayList as tasks container and index as the priorLevel;
     //limit we want to user container as this:can auto resize and gc non using null slot;
    protected volatile AbstractTask[] list;//for 有序的 task
    protected volatile Long[] taskStartTimes;
    protected volatile Long[] taskEndTimes;
    protected volatile Long[] taskPriorLevels;//used as in the one task , the sequence of exe;
    protected volatile int[] taskTypes;//任务类型
    protected volatile int[] status;//任务状态 未进队 初始化 工作中 结束
    protected volatile Object[] objects;//oom-limit : a large of
    protected Long globalWorkId;//集群唯一id；

    // waste memory with null slot except that task is been worked with short time
    protected volatile Integer enQueueIndex;
    protected volatile Integer workIndex;
    protected Integer workCount;

    protected AtomicLong counter;
    protected AtomicInteger sequenceGen=new AtomicInteger();

    /*
    * only adapt for One Producer(business thread producer) and One Consumer Thread
    * */

     AbstractTasks(int taskCount, Long globalWorkId){
        startTime=System.currentTimeMillis();
        this.globalWorkId=globalWorkId;
        enQueueIndex=0;
        workCount=taskCount;
        workIndex=0;
        counter=new AtomicLong(Long.valueOf(taskCount).longValue());
        list=new AbstractTask[taskCount];
        taskTypes=new int[taskCount];
        objects=new Object[taskCount];
        status=new int[taskCount];
        taskPriorLevels=new Long[taskCount];
        taskEndTimes=new Long[taskCount];
        taskStartTimes=new Long[taskCount];
        for(int index=0;index < taskCount;index++){
            status[index]=AbstractTask.INIT;
            taskPriorLevels[index]=0L;
        }
    }

    protected abstract int doTasks();


    protected abstract int lockAndExe(int tempIndex);



    protected void setNull(Integer workIndex) {
        list[workIndex]=null;
        objects[workIndex]=null;
    }
    /*
    * return 0: 任务还未进队
    * -1：任务结束：
    *
    * */
    protected int work(int tempIndex) {
        int currentStatus=status[tempIndex];
        int taskType=taskTypes[tempIndex];
        if(currentStatus==AbstractTask.INIT){
            return AbstractTask.INIT;
        }
        if(currentStatus==START){
            status[tempIndex]=WORKING;
            Object attach=objects[tempIndex];
            AbstractTask abstractTask =list[tempIndex];
            try{
                abstractTask.doTask(taskType,attach);
                status[tempIndex]=END;
                return -2;
            }catch (Exception e){
                status[tempIndex]=FAIL;
                e.printStackTrace();
            }
        }
        return taskType;//
    }

    boolean addTask(
            AbstractTask abstractTask,
            int taskType,
            Object attach){
        Long startTime=System.currentTimeMillis();
        int seq=sequenceGen.getAndIncrement();
        setNonNull(seq,startTime,abstractTask,taskType,attach);
        enQueueIndex=seq+1;
        unPark();
        return true;
    }

    protected void setNonNull(int seq, Long startTime, AbstractTask abstractTask, int taskType, Object attach) {
        taskStartTimes[seq]=startTime;
        list[seq]= abstractTask;//一定可以保证有序，当前只有业务线程来处理
        taskTypes[seq]=taskType;
//            System.out.println(taskType+"-"+seq);
        objects[seq]=attach;// we must set task firstly and increment enQueueIndex secondly for safely works()
        status[seq]=START;
    }


    ////Tasks execute design begin
    protected volatile Thread thread;
    protected AtomicLong inc=new AtomicLong(0L);
    protected AtomicLong add=new AtomicLong(0L);//avoid ABA bug
    protected ReentrantLock lock=new ReentrantLock();
    protected ReentrantLock tInitLock=new ReentrantLock();

    public void run() {
        if(tInitLock.tryLock()){
            thread=Thread.currentThread();
            try{
                while(true){
                    if(doTasks()!= AbstractTask.OVER){
                        park();
                    }else{
                        System.out.println("OVER");
                        break;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            unPark();
        }
    }

    private void park() {
        add.getAndIncrement();
        LockSupport.park();
    }

    private void unPark(){
        while(thread==null){
            continue;
        }
        try{
            lock.lock();//性能阻塞点 避免 ABA问题 。
            LockSupport.unpark(thread);
            inc.getAndDecrement();
            Long result=inc.get()+add.get();
            while(result > 0){
                LockSupport.unpark(thread);
                inc.getAndDecrement();
                result=inc.get()+add.get();
            }
        }finally {
            lock.unlock();
        }
    }

    //Tasks execute design end

    public AbstractTask[] getList() {
        return list;
    }

    public void setList(AbstractTask[] list) {
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

}
