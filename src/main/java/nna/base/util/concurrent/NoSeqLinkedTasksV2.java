package nna.base.util.concurrent;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author NNA-SHUAI
 * @create 2017-06-20 1:15
 **/

public class NoSeqLinkedTasksV2 extends NoSeqFixSizeTasks {

    protected LinkedBlockingQueue<AbstractTaskWrapper>[] list;
    protected ReentrantLock[] locks;
    protected ExecutorService service;
    protected int linkedListCount;

    NoSeqLinkedTasksV2(int linkedListCount,int threadCount,Long workId) {
        super(0, workId);
        this.linkedListCount=linkedListCount;
        threadCount=threadCount>linkedListCount?linkedListCount:threadCount;
        service=Executors.newFixedThreadPool(threadCount);
        for(int index=0;index<linkedListCount;index++){
            if(index < threadCount){
                service.submit(this);
            }
        }
    }

    protected int doTasks(){
        LinkedList<AbstractTaskWrapper> temps=new LinkedList<AbstractTaskWrapper>();
        ReentrantLock lock;
        int tempCount;
        int totalCount=0;
        boolean locked=false;
        int index=0;
        LinkedBlockingQueue<AbstractTaskWrapper> temp;
        for(;index<linkedListCount;index++){
            lock=locks[index];
            try{
                if(lock.tryLock()){
                    locked=true;
                    temp=list[index];
                    tempCount=temp.size();
                    tempCount=temp.drainTo(temps,tempCount);
                    totalCount+=tempCount;
                }
            }finally {
                if(locked){
                    locked=false;
                    lock.unlock();
                }
            }
        }
        Iterator<AbstractTaskWrapper> iterator=temps.iterator();
        Object att;
        int taskType;
        AbstractTask abstractTask;
        AbstractTaskWrapper abstractTaskWrapper;
        while(iterator.hasNext()){
            abstractTaskWrapper=iterator.next();
            abstractTask=abstractTaskWrapper.abstractTask;
            taskType=abstractTaskWrapper.taskType;
            att=abstractTaskWrapper.object;
            try {
                abstractTask.doTask(taskType,att);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public void run(){
        doTasks();
    }

    boolean addTask(
            AbstractTask abstractTask,
            int taskType,
            Object attach){
        Long startTime=System.currentTimeMillis();
        AbstractTaskWrapper abstractTaskWrapper=new AbstractTaskWrapper(abstractTask,attach,taskType,startTime);
        LinkedBlockingQueue<AbstractTaskWrapper> loadBalance=getLoadBalacer();
        loadBalance.add(abstractTaskWrapper);
        return true;
    }

    private LinkedBlockingQueue<AbstractTaskWrapper> getLoadBalacer() {
                return null;
    }

    private class AbstractTaskWrapper<T extends AbstractTask>{
        private AbstractTask abstractTask;
        private Object object;
        private int taskType;
        private int taskStatus;
        private Long startTime;

        private AbstractTaskWrapper(T t,Object object,int taskType,Long startTime){
            abstractTask=t;
            this.object=object;
            this.taskStatus=taskType;
            taskStatus=AbstractTask.INIT;
            this.startTime=startTime;
        }
    }
}
