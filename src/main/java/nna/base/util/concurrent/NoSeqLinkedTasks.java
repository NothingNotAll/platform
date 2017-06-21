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

public class NoSeqLinkedTasks extends NoSeqFixSizeTasks {
    public LinkedBlockingQueue<AbstractTaskWrapper>[] getList() {
        return list;
    }

    public void setList(LinkedBlockingQueue<AbstractTaskWrapper>[] list) {
        this.list = list;
    }

    //next step is solve the hungry lock;
    protected LinkedBlockingQueue<AbstractTaskWrapper>[] list;
    protected ReentrantLock[] locks;
    protected ExecutorService service;
    protected int linkedListCount;
    NoSeqLinkedTasks(int linkedListCount, int threadCount, Long workId) {
        super(0, workId);
        this.linkedListCount=linkedListCount;
        list=new LinkedBlockingQueue[linkedListCount];
        locks=new ReentrantLock[linkedListCount];
        threadCount=threadCount>linkedListCount?linkedListCount:threadCount;
        service=Executors.newFixedThreadPool(threadCount);
        for(int index=0;index<linkedListCount;index++){
            list[index]=new LinkedBlockingQueue<AbstractTaskWrapper>();
            locks[index]=new ReentrantLock();
        }
        for(int index=0;index < threadCount;index++){
            service.submit(this);
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
        AbstractTaskWrapper abstractTaskWrapper;
        for(;index<linkedListCount;index++){
            lock=locks[index];
            try{
                if(lock.tryLock()){
                    locked=true;
                    temp=list[index];
                    tempCount=temp.size();
                    tempCount=temp.drainTo(temps,tempCount);
                    totalCount+=tempCount;
                    if(totalCount==0){
                        abstractTaskWrapper=temp.take();
                        temps.add(abstractTaskWrapper);
                        tempCount=1;
                        totalCount=1;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
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
        while(iterator.hasNext()){
            abstractTaskWrapper=iterator.next();
            abstractTask=abstractTaskWrapper.getAbstractTask();
            taskType=abstractTaskWrapper.getTaskType();
            att=abstractTaskWrapper.getObject();
            try {
                abstractTask.doTask(taskType,att);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public void run(){
        try{
            while(true){
                try{
                    doTasks();
                }catch (Exception e){
                    e.printStackTrace();
                }finally {

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {

        }
    }

    boolean addTask(
            AbstractTask abstractTask,
            Integer taskType,
            Object attach){
        AbstractTaskWrapper abstractTaskWrapper=new AbstractTaskWrapper(abstractTask,attach,taskType);
        LinkedBlockingQueue<AbstractTaskWrapper> loadBalance=getLoadBalacer();
        loadBalance.add(abstractTaskWrapper);
        return true;
    }

    private LinkedBlockingQueue<AbstractTaskWrapper> getLoadBalacer() {
        LinkedBlockingQueue temp;
        Integer tempCount;
        Integer minCount = null;
        Integer minIndex = null;
        for(int index=0;index < linkedListCount;index++){
            temp=list[index];
            if(temp.size()==0){
                return temp;
            }else {
                if(minIndex==null){
                    minIndex=index;
                    minCount=temp.size();
                }else{
                    tempCount=temp.size();
                    if(minCount>tempCount){
                        minIndex=index;
                        minCount=tempCount;
                    }
                }
            }
        }
        return list[minIndex];
    }

}
