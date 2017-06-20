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

    protected volatile AbstractTaskWrapper[] abstractTaskWrappers;
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
        abstractTaskWrappers=new AbstractTaskWrapper[taskCount];
         AbstractTaskWrapper abstractTaskWrapper;
        for(int index=0;index < taskCount;index++){
            abstractTaskWrapper=new AbstractTaskWrapper();
            abstractTaskWrapper.setTaskStatus(AbstractTask.INIT);
            abstractTaskWrappers[index]=abstractTaskWrapper;
        }
    }

    protected abstract int doTasks();


    protected abstract int lockAndExe(int tempIndex);



    protected void setNull(Integer workIndex) {
        AbstractTaskWrapper abstractTaskWrapper=abstractTaskWrappers[workIndex];
        abstractTaskWrapper.reInit();
    }
    /*
    * return 0: 任务还未进队
    * -1：任务结束：
    *
    * */
    protected int work(int tempIndex) {
        AbstractTaskWrapper abstractTaskWrapper=abstractTaskWrappers[workIndex];
        int currentStatus=abstractTaskWrapper.getTaskStatus();
        int taskType=abstractTaskWrapper.getTaskType();
        if(currentStatus==AbstractTask.INIT){
            return AbstractTask.INIT;
        }
        if(currentStatus==START){
            abstractTaskWrapper.setTaskStatus(WORKING);
            Object attach=abstractTaskWrapper.getObject();
            AbstractTask abstractTask =abstractTaskWrapper.getAbstractTask();
            try{
                abstractTask.doTask(taskType,attach);
                abstractTaskWrapper.setTaskStatus(END);
                return -2;
            }catch (Exception e){
                abstractTaskWrapper.setTaskStatus(FAIL);
                e.printStackTrace();
            }
        }
        return taskType;//
    }

    boolean addTask(AbstractTask abstractTask, Integer taskType,Object attach){
        Long startTime=System.currentTimeMillis();
        int seq=sequenceGen.getAndIncrement();
        setNonNull(seq,startTime,abstractTask,taskType,attach);
        enQueueIndex=seq+1;
        unPark();
        return true;
    }

    protected void setNonNull(int seq, Long startTime, AbstractTask abstractTask, Integer taskType, Object attach) {
        AbstractTaskWrapper abstractTaskWrapper=abstractTaskWrappers[seq];
        abstractTaskWrapper.setTaskStartTimes(startTime);
        abstractTaskWrapper.setAbstractTask(abstractTask);//一定可以保证有序，当前只有业务线程来处理
        abstractTaskWrapper.setTaskType(taskType);
//            System.out.println(taskType+"-"+seq);
        abstractTaskWrapper.setObject(attach);// we must set task firstly and increment enQueueIndex secondly for safely works()
        abstractTaskWrapper.setTaskStatus(START);
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
