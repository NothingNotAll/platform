package nna.base.util.concurrent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * the real io worker
 * @author NNA-SHUAI
 * @create 2017-06-18 13:21
 **/

public class IOTaskProcessor {
    private ConcurrentHashMap<Long,IOWorker> taskMap=new ConcurrentHashMap<Long, IOWorker>();
    private ExecutorService ioTaskThreads= Executors.newCachedThreadPool();

    public void submitIOWork(AbstractIOTasks abstractIOTasks){
        Long workId=abstractIOTasks.getGlobalWorkId();
        IOWorker worker=new IOWorker(abstractIOTasks);
        IOWorker temp;
        temp=taskMap.putIfAbsent(workId,worker);
        if(temp==null){
            ioTaskThreads.submit(worker);
        }else{
            worker.unPark();
        }
    }

    private class IOWorker implements Runnable{
        private AbstractIOTasks tasks;
        private Thread thread;
        private boolean isInit;
        private ReentrantLock lock=new ReentrantLock();
        private IOWorker(AbstractIOTasks abstractIOTasks){
            this.tasks=abstractIOTasks;
        }
        public void run() {
            while(true){
                if(!isInit){
                    try{
                        lock.lock();
                        thread=Thread.currentThread();
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        lock.unlock();
                        isInit=true;
                    }
                }
                if(!tasks.doTasks()){
                    LockSupport.park();
                }else{
                    break;
                }
            }
        }

        void unPark(){
            LockSupport.unpark(thread);
        }
    }
}
