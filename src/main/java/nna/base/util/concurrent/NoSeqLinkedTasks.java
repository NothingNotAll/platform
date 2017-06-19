package nna.base.util.concurrent;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * for Danamic
 *
 * @author NNA-SHUAI
 * @create 2017-06-19 21:31
 **/

public class NoSeqLinkedTasks extends AbstractTasks {

    private LinkedBlockingQueue<AbstractTasks>[] abstractTasksList;
    private ExecutorService executorService;
    private int listSize;
    NoSeqLinkedTasks(int listSize, Integer threadSize, Long globalWorkId) {
        super(0,globalWorkId);
        threadSize=threadSize>listSize?listSize:threadSize;
        this.listSize=listSize;
        abstractTasksList=new LinkedBlockingQueue[listSize];
        executorService= Executors.newFixedThreadPool(threadSize);
        for(int index=0;index < listSize;index++){
            abstractTasksList[index]=new LinkedBlockingQueue<AbstractTasks>();
            if(index<threadSize){
                executorService.submit(this);
            }
        }
    }

    boolean addTask(
            AbstractTask abstractTask,
            int taskType,
            Object attach){
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
        for(;index<listSize;index++){
            temp=abstractTasksList[index];
            tempCount=temp.size();
            blockIndex=index;
            tempCount=temp.drainTo(linkedList,tempCount);
            totalCount+=tempCount;
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
