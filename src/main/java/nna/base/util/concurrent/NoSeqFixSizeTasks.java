package nna.base.util.concurrent;


import java.util.concurrent.locks.ReentrantLock;

/**
 * @author NNA-SHUAI
 * @create 2017-06-15 11:36
 **/

public class NoSeqFixSizeTasks extends AbstractTasks {

    protected ReentrantLock[] locks;//for NoSeqFixSizeTasks
    NoSeqFixSizeTasks(int taskCount, Long workId,String taskName) {
        super(taskCount,workId,taskName);
        locks=new ReentrantLock[taskCount];
        for(int index=0;index<workCount;index++){
            locks[index]=new ReentrantLock();
        }
    }

    public void run() {
        doTasks();
    }

    protected int doTasks() {
        AbstractTask abstractTask;
        int tempIndex=0;
        AbstractTaskWrapper abstractTaskWrapper;
        for(;tempIndex<workCount;tempIndex++){
            abstractTaskWrapper=abstractTaskWrappers[tempIndex];
            abstractTask =abstractTaskWrapper.getAbstractTask();
            Integer sta=abstractTask.getTaskStatus();
            // for 乐观锁 ; for performance
            if(sta==START&&abstractTask!=null){
                lockAndExe(tempIndex);
            }
        }
        return -5;
    }

    boolean addTask(
            AbstractTask abstractTask,
            Integer taskType,
            Object attach){
        Long startTime=System.currentTimeMillis();
        ReentrantLock lock;
        int sta;
        boolean isLocked=false;
        int loopCount=0;
        AbstractTaskWrapper abstractTaskWrapper;
        for(int index=0;index < workCount;){
            abstractTaskWrapper=abstractTaskWrappers[index];
            sta=abstractTaskWrapper.getTaskStatus();
            if(sta== AbstractTask.INIT||sta==END||sta==FAIL){
                lock=locks[index];
                try{
                    if(lock.tryLock()){
                        isLocked=true;
                        if(sta== AbstractTask.INIT||sta==END||sta==FAIL){
                            setNonNull(abstractTaskWrapper,startTime, abstractTask,taskType,attach);
                            return true;
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(isLocked){
                        lock.unlock();
                        isLocked=false;
                    }
                }
            }
            loopCount++;
            if(loopCount==workCount){
                return false;
            }
            index=index==workCount?0:index++;
        }
        return false;
    }

    protected int lockAndExe(int tempIndex) {
                ReentrantLock lock=locks[tempIndex];
        boolean isLocked=false;
        AbstractTaskWrapper abstractTaskWrapper=abstractTaskWrappers[tempIndex];
        int sta=abstractTaskWrapper.getTaskStatus();
        try{
            if(sta==START&&lock.tryLock()){
                isLocked=true;
                lock.lock();
                if(sta==START){
                    return work(abstractTaskWrapper);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(!isLocked){
                lock.unlock();
            }
        }
        return -5;
    }

    public ReentrantLock[] getLocks() {
        return locks;
    }

    public void setLocks(ReentrantLock[] locks) {
        this.locks = locks;
    }
}
