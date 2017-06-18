package nna.base.util.concurrent;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.LockSupport;

/**
 * @author NNA-SHUAI
 * @create 2017-06-17 7:51
 **/

public class IOEventProcessorV2<T extends AbstractIOTasks> implements Runnable {

    private Thread workerThread;
    private Integer loadNo;

    IOEventProcessorV2(Integer loadNo){
        this.loadNo=loadNo;
    }

    void active(AbstractIOTasks AbstractIOTasks){
        unPark(AbstractIOTasks);
    }

    private void unPark(AbstractIOTasks abstractIOTasks){
        int index=(int)((long) abstractIOTasks.getPriorLevel());
        LinkedBlockingQueue<AbstractIOTasks> queue=taskQueue[index];
        queue.add(abstractIOTasks);
        LockSupport.unpark(workerThread);
    }

    /*
        * new version: the tasksQueue is the fixed size list
        * and tasks Map is the Long(taskSequence)-AbstractIOTasks key-value map
        * */
    private LinkedBlockingQueue<AbstractIOTasks>[] taskQueue;
    private volatile int maxPriorLevel;

    IOEventProcessorV2(Thread wt, int maxPriorLevel){
        this.workerThread=wt;
        taskQueue=new LinkedBlockingQueue[maxPriorLevel];
        for(int index=0;index<maxPriorLevel;index++){
            taskQueue[index]=new LinkedBlockingQueue<AbstractIOTasks>();
        }
        this.maxPriorLevel=maxPriorLevel;
    }
    private volatile int index;
    private volatile LinkedBlockingQueue<AbstractIOTasks> tempConTaskList;
    private volatile int tempBlockCount;
    private volatile int blockTotalCount;
    private LinkedList<AbstractIOTasks> tempTaskList=new LinkedList<AbstractIOTasks>();
    private AbstractIOTasks tempTasks;
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
    private void consumer(LinkedList<AbstractIOTasks> tempTaskList) {
        Iterator<AbstractIOTasks> iterator=tempTaskList.iterator();
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
