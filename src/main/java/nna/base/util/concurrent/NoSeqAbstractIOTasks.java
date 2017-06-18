package nna.base.util.concurrent;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author NNA-SHUAI
 * @create 2017-06-15 11:36
 **/

public class NoSeqAbstractIOTasks extends AbstractIOTasks {

    NoSeqAbstractIOTasks(int taskCount, Long workId) {
        super(taskCount,workId,false);
    }

    protected boolean doTasks() {
        AbstractIOTask abstractIOTask;
        int tempIndex=0;
//        int taskType;
        for(;tempIndex<workCount;tempIndex++){
            abstractIOTask =list[tempIndex];
            if(abstractIOTask !=null){
                // for 乐观锁 ; for performance
                if(status[tempIndex]==START){
//                  taskType=lockAndExe(abstractIOTask,tempIndex);
                    lockAndExe(abstractIOTask,tempIndex);
                }
            }
        }
        return false;
    }

    protected int lockAndExe(AbstractIOTask abstractIOTask, int tempIndex) {
                ReentrantLock lock=locks[tempIndex];
        boolean isLocked=false;
        try{
            if(lock.isLocked()){
                isLocked=true;
            }
            lock.lock();
            return work(abstractIOTask,tempIndex);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(!isLocked){
                lock.unlock();
            }
        }
        return -2;
    }
}