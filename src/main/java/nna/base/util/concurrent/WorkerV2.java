package nna.base.util.concurrent;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

/**
 * @author NNA-SHUAI
 * @create 2017-06-17 7:51
 **/

public class WorkerV2<T extends AbstractTasks> implements Runnable {

    private Thread workerThread;
    void entry(AbstractTasks abstractTasks){
        AbstractTask abstractTask=abstractTasks.getList()[0];
        Long enQueueTime=abstractTasks.getStartTime();
        ConcurrentHashMap<Long,AbstractTasks> map=new ConcurrentHashMap<Long,AbstractTasks>();
        ConcurrentHashMap<Long,AbstractTasks> tempMap=taskMap.putIfAbsent(enQueueTime,map);
        if(tempMap!=null){
            map=tempMap;
        }
        Long newTaskSeq=taskSeqGen.getAndIncrement();
        abstractTask.setIndex(newTaskSeq);
        map.put(newTaskSeq,abstractTasks);
        unPark(abstractTasks);
    }

    void active(AbstractTasks AbstractTasks){
        unPark(AbstractTasks);
    }

    private void unPark(AbstractTasks abstractTasks){
        int index=(int)((long)abstractTasks.getPriorLevel());
        LinkedBlockingQueue<AbstractTasks> queue=taskQueue[index];
        queue.add(abstractTasks);
        LockSupport.unpark(workerThread);
    }

    /*
        * new version: the tasksQueue is the fixed size list
        * and tasks Map is the Long(taskSequence)-AbstractTasks key-value map
        * */
    private ConcurrentHashMap<Long,ConcurrentHashMap<Long,AbstractTasks>> taskMap=new ConcurrentHashMap<Long, ConcurrentHashMap<Long,AbstractTasks>>();
    private AtomicLong taskSeqGen=new AtomicLong();
    private LinkedBlockingQueue<AbstractTasks>[] taskQueue;
    private volatile int maxPriorLevel;

    WorkerV2(Thread wt,int maxPriorLevel){
        this.workerThread=wt;
        taskQueue=new LinkedBlockingQueue[maxPriorLevel];
        for(int index=0;index<maxPriorLevel;index++){
            taskQueue[index]=new LinkedBlockingQueue<AbstractTasks>();
        }
        this.maxPriorLevel=maxPriorLevel;
    }
    private volatile int index;
    private volatile LinkedBlockingQueue<AbstractTasks> tempConTaskList;
    private volatile int tempBlockCount;
    private volatile int blockTotalCount;
    private LinkedList<AbstractTasks> tempTaskList=new LinkedList<AbstractTasks>();
    private AbstractTasks tempTasks;
    public void run() {
        try{
            while(true){
                index=maxPriorLevel-1;
                for(;index >= 0;index--){
                    tempConTaskList=taskQueue[index];
                    tempBlockCount=tempConTaskList.size();
                    tempBlockCount=tempConTaskList.drainTo(tempTaskList,tempBlockCount);
                    blockTotalCount+=tempBlockCount;
                }
                if(blockTotalCount==0){
                    LockSupport.park();
                }else{
                    try{
                        consumer(tempTaskList);
                    }catch (Exception e){

                    }catch (Throwable t){

                    }finally {
                        destroy();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {

        }
    }
    private Long startTime;
    private ConcurrentHashMap map;
    private void consumer(LinkedList<AbstractTasks> tempTaskList) {
        Iterator<AbstractTasks> iterator=tempTaskList.iterator();
        while(iterator.hasNext()){
            tempTasks=iterator.next();
            tempTasks.doTasks(taskMap);
            startTime=tempTasks.getStartTime();
            map=taskMap.get(startTime);
            if(map.size()==0){
                taskMap.remove(startTime);
            }
        }
    }

    private void destroy() {
        tempTaskList.clear();
        tempBlockCount=0;
        blockTotalCount=0;
        index=0;
        startTime=null;
        map=null;
    }

    public ConcurrentHashMap<Long, ConcurrentHashMap<Long, AbstractTasks>> getTaskMap() {
        return taskMap;
    }

    public void setTaskMap(ConcurrentHashMap<Long, ConcurrentHashMap<Long, AbstractTasks>> taskMap) {
        this.taskMap = taskMap;
    }
}
