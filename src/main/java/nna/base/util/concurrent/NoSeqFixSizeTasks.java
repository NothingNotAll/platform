package nna.base.util.concurrent;

import nna.base.server.AbstractNIOTask;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author NNA-SHUAI
 * @create 2017-06-15 11:36
 **/

public class NoSeqFixSizeTasks extends AbstractTasks {

    protected ReentrantLock[] locks;//for NoSeqFixSizeTasks
    NoSeqFixSizeTasks(int taskCount, Long workId) {
        super(taskCount,workId);
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
        for(;tempIndex<workCount;tempIndex++){
            abstractTask =list[tempIndex];
            int sta=status[tempIndex];
            // for 乐观锁 ; for performance
            if(sta==START&&abstractTask!=null){
                lockAndExe(tempIndex);
            }
        }
        return -5;
    }

    boolean addTask(
            AbstractTask abstractTask,
            int taskType,
            Object attach){
        Long startTime=System.currentTimeMillis();
        ReentrantLock lock;
        int sta;
        boolean isLocked=false;
        int loopCount=0;
        for(int index=0;index < workCount;){
            sta=status[index];
            if(sta== AbstractTask.INIT||sta==END||sta==FAIL){
                lock=locks[index];
                try{
                    if(lock.tryLock()){
                        isLocked=true;
                        if(sta== AbstractTask.INIT||sta==END||sta==FAIL){
                            setNonNull(index,startTime, abstractTask,taskType,attach);
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
        int sta=status[tempIndex];
        try{
            if(sta==START&&lock.tryLock()){
                isLocked=true;
                lock.lock();
                if(sta==START){
                    return work(tempIndex);
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
}
