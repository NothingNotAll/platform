package nna.base.util.concurrent;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * for Danamic
 * when tasks's count is 1,it seems like to be AbstractAsks;
 *
 * @author NNA-SHUAI
 * @create 2017-06-19 21:31
 **/

public class NoSeqLinkedTasks<T extends AbstractTasks> extends NoSeqFixSizeTasks {

    private LinkedBlockingQueue<AbstractTasks>[] abstractTasksList;
    private ReentrantLock[] locks;
    private ExecutorService executorService;
    private int listSize;

    NoSeqLinkedTasks(int listSize, Integer threadSize, Long globalWorkId) {
        super(0,globalWorkId);
        threadSize=threadSize>listSize?listSize:threadSize;
        this.listSize=listSize;
        abstractTasksList=new LinkedBlockingQueue[listSize];
        executorService= Executors.newFixedThreadPool(threadSize);
        locks=new ReentrantLock[threadSize];
        for(int index=0;index < listSize;index++){
            abstractTasksList[index]=new LinkedBlockingQueue<AbstractTasks>();
            if(index<threadSize){
                locks[index]=new ReentrantLock();
                executorService.submit(this);
            }
        }
    }

    boolean addTask(
            AbstractTask abstractTask,
            int taskType,
            Object attach){
        //负载算法
        AbstractTasks abstractTasks;
        LinkedBlockingQueue<AbstractTasks> temp;
        Iterator<AbstractTasks> iterator;
        boolean addSuccess;
        temp=abstractTasksList[blockIndex];
        iterator=temp.iterator();
        while(iterator.hasNext()){
            abstractTasks=iterator.next();
            addSuccess=abstractTasks.addTask(abstractTask,taskType,attach);
            if(addSuccess){
                return true;
            }
        }
        return false;
    }
    private volatile Integer blockIndex=null;
    protected int doTasks() {
        LinkedList<AbstractTasks> linkedList=new LinkedList<AbstractTasks>();
        int tempCount;
        int totalCount=0;
        LinkedBlockingQueue<AbstractTasks> temp;
        int index=0;
        ReentrantLock lock;
        boolean locked=false;
        for(;index<listSize;index++){
            lock=locks[index];
            try{
                if(lock.tryLock()){
                    locked=true;
                    temp=abstractTasksList[index];
                    tempCount=temp.size();
                    blockIndex=index;
                    tempCount=temp.drainTo(linkedList,tempCount);
                    totalCount+=tempCount;
                }
            }finally {
                if(locked){
                    lock.unlock();
                    locked=false;
                }
            }
        }
        AbstractTasks abstractTasks;
        Iterator<AbstractTasks> iterator=linkedList.iterator();
        while(iterator.hasNext()){
            abstractTasks=iterator.next();
            abstractTasks.doTasks();
        }
        return 0;
    }


    protected int lockAndExe(int tempIndex) {
        return 0;
    }

    public void run(){
        doTasks();
    }
}
