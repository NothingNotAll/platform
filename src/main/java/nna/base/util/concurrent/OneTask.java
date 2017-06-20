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
        AbstractTask abstractTask=list[0];
        Object att=objects[0];
        int taskType=taskTypes[0];
        ReentrantLock lock=locks[0];
        boolean locked=false;
        try{
            if(lock.tryLock()){
                locked=true;
                try {
                    status[0]=WORKING;
                    abstractTask.doTask(taskType,att);
                    status[0]=END;
                } catch (Exception e) {
                    e.printStackTrace();
                    status[0]=FAIL;
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
