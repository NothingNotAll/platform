package nna.base.util.concurrent;

import java.util.concurrent.locks.ReentrantLock;

/**
 * for High Peformance Nio or other;
 * @author NNA-SHUAI
 * @create 2017-06-20 0:39
 **/

public class OneTask extends NoSeqFixSizeTasks {

    OneTask(Long globalWorkId) {
        super(1, globalWorkId);
    }

    protected int doTasks() {
        AbstractTaskWrapper abstractTaskWrapper=abstractTaskWrappers[0];
        AbstractTask abstractTask=abstractTaskWrapper.getAbstractTask();
        Object att=abstractTaskWrapper.getObject();
        int taskType=abstractTaskWrapper.getTaskType();
        ReentrantLock lock=locks[0];
        boolean locked=false;
        try{
            if(lock.tryLock()){
                locked=true;
                try {
                    abstractTaskWrapper.setTaskStatus(WORKING);
                    abstractTask.doTask(taskType,att);
                    abstractTaskWrapper.setTaskStatus(END);
                } catch (Exception e) {
                    e.printStackTrace();
                    abstractTaskWrapper.setTaskStatus(FAIL);
                }
            }
        }finally {
            if(locked){
                lock.unlock();
            }
        }
        return 0;
    }

    protected int lockAndExe(int tempIndex) {
        return 0;
    }
}
