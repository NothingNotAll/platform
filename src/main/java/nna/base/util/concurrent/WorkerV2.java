package nna.base.util.concurrent;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.LockSupport;

/**
 * @author NNA-SHUAI
 * @create 2017-06-17 7:51
 **/

public class WorkerV2<T extends AbstractTasks> implements Runnable {

    private Thread workerThread;
    private Integer loadNo;

    WorkerV2(Integer loadNo){
        this.loadNo=loadNo;
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
    private void consumer(LinkedList<AbstractTasks> tempTaskList) {
        Iterator<AbstractTasks> iterator=tempTaskList.iterator();
        while(iterator.hasNext()){
            tempTasks=iterator.next();
        }
    }

    private void destroy() {
        tempTaskList.clear();
        tempBlockCount=0;
        blockTotalCount=0;
        index=0;
    }

    public Integer getLoadNo() {
        return loadNo;
    }

    public void setLoadNo(Integer loadNo) {
        this.loadNo = loadNo;
    }
}
