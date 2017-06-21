package nna.base.util.concurrent;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author NNA-SHUAI
 * @create 2017-06-20 1:15
 **/

public class NoSeqLinkedTasks extends NoSeqFixSizeTasks {

    //next step is solve the hungry lock;
    protected LinkedBlockingQueue<AbstractTaskWrapper>[] list;
    protected volatile Thread[] threads;
    protected ReentrantLock[] unParkLocks;
    protected AtomicInteger threadIndexGen=new AtomicInteger();
    protected ExecutorService service;
    protected int linkedListCount;
    NoSeqLinkedTasks(int linkedListCount, int threadCount, Long workId) {
        super(0, workId);
        this.linkedListCount=linkedListCount;
        list=new LinkedBlockingQueue[linkedListCount];
        threads=new Thread[linkedListCount];
        unParkLocks=new ReentrantLock[threadCount];
        locks=new ReentrantLock[linkedListCount];
        threadCount=threadCount>linkedListCount?linkedListCount:threadCount;
        service=Executors.newFixedThreadPool(threadCount);
        for(int index=0;index<linkedListCount;index++){
            list[index]=new LinkedBlockingQueue<AbstractTaskWrapper>();
            locks[index]=new ReentrantLock();
        }
        for(int index=0;index < threadCount;index++){
            service.submit(this);
            unParkLocks[index]=new ReentrantLock();
        }
    }

    protected int doTasks(){
        LinkedList<AbstractTaskWrapper> temps=new LinkedList<AbstractTaskWrapper>();
        int tempCount;
        int totalCount=0;
        int index=0;
        LinkedBlockingQueue<AbstractTaskWrapper> temp;
        for(;index<linkedListCount;index++){
            temp=list[index];
            tempCount=temp.size();
            tempCount=temp.drainTo(temps,tempCount);
            totalCount+=tempCount;
        }
        if(totalCount==0){
            LockSupport.park();
        }else{
            Iterator<AbstractTaskWrapper> iterator=temps.iterator();
            consumer(iterator);
        }
        return -1;
    }

    private void consumer(Iterator<AbstractTaskWrapper> iterator) {
        AbstractTaskWrapper abstractTaskWrapper;
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
    }

    public void run(){
        threads[threadIndexGen.getAndIncrement()]=Thread.currentThread();
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
        LinkedBlockingQueue<AbstractTaskWrapper> loadBalance=getLoadBalance();
        loadBalance.add(abstractTaskWrapper);
        unPark();
        return true;
    }

    private void unPark() {
        int threadCount=threads.length;
        Thread thread;
        ReentrantLock unParkLock;
        boolean isLocked=false;
        boolean isUnParkExe=false;
        boolean isAllNull=true;
        for(int index=0;index < threadCount;index++){
            thread=threads[index];
            Thread.State state= thread.getState();
            if(thread!=null&&state != Thread.State.RUNNABLE){
                isAllNull=false;
                unParkLock=unParkLocks[index];
                try{
                    if(unParkLock.tryLock()){
                        isLocked=true;
                        if(state != Thread.State.RUNNABLE){
                            LockSupport.unpark(thread);
                            isUnParkExe=true;
                            break;
                        }
                    }
                    LockSupport.unpark(thread);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(isLocked){
                        unParkLock.unlock();
                        isLocked=false;
                    }
                }
            }
        }
//        if(!isAllNull){
//            System.out.println("unPark Success!");
//        }
//        if(isAllNull){
//            System.out.println("threads is not init success!");
//        }
        if(isAllNull&&!isUnParkExe){
            int randomInt=random.nextInt(threadCount-1);
            try{
                LockSupport.unpark(threads[randomInt]);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private LinkedBlockingQueue<AbstractTaskWrapper> getLoadBalance() {
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

    public LinkedBlockingQueue<AbstractTaskWrapper>[] getList() {
        return list;
    }

    public void setList(LinkedBlockingQueue<AbstractTaskWrapper>[] list) {
        this.list = list;
    }



    public Thread[] getThreads() {
        return threads;
    }

    public void setThreads(Thread[] threads) {
        this.threads = threads;
    }

    public ExecutorService getService() {
        return service;
    }

    public void setService(ExecutorService service) {
        this.service = service;
    }

    public int getLinkedListCount() {
        return linkedListCount;
    }

    public void setLinkedListCount(int linkedListCount) {
        this.linkedListCount = linkedListCount;
    }

    public AtomicInteger getThreadIndexGen() {
        return threadIndexGen;
    }

    public void setThreadIndexGen(AtomicInteger threadIndexGen) {
        this.threadIndexGen = threadIndexGen;
    }

    public ReentrantLock[] getUnParkLocks() {
        return unParkLocks;
    }

    public void setUnParkLocks(ReentrantLock[] unParkLocks) {
        this.unParkLocks = unParkLocks;
    }
}
