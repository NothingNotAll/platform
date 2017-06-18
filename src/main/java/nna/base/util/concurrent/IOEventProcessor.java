package nna.base.util.concurrent;


import nna.base.bean.Clone;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 时间片机制
 * 优先级机制
 * the sequence of execute task List 's alg may be customer
 * @author NNA-SHUAI
 * @create 2017-06-13 10:12
 **/

public class IOEventProcessor<T extends AbstractIOTask> extends Clone implements Runnable{

    private IOTaskProcessor ioTaskProcessor=new IOTaskProcessor();
    private static final Long serialVersionUID=-1L;
    private Integer loadNo;
    /*
        * 为了业务线程的尽可能的不阻塞，将锁竞争降低到 单条线程之间的竞争：Worker线程与业务线程之间的锁竞争。
        * */
    //in the future this must be replaced of ArrayListBlockingQueue;and used index as the priorLevel;
    private volatile LinkedBlockingQueue<AbstractIOTasks> workQueue=new LinkedBlockingQueue<AbstractIOTasks>();

    private int tempWorkCount;
    private AbstractIOTasks blockAbstractIOTasks;
    private LinkedList<AbstractIOTasks> tempWorkList=new LinkedList<AbstractIOTasks>();
    private Iterator<AbstractIOTasks> iterator;
    private AbstractIOTasks currentAbstractIOTasks;

    public void run() {
        try{
            while(true){
                tempWorkCount=workQueue.size();
                if(tempWorkCount > 0){
                    //性能瓶頸點
                    tempWorkCount=workQueue.drainTo(tempWorkList,tempWorkCount);
                }else{
                    blockAbstractIOTasks =workQueue.take();
                    tempWorkList.add(blockAbstractIOTasks);
                    tempWorkCount=1;
                }
                try{
                    consumer(tempWorkList);
                }catch (Exception e){
                    e.printStackTrace();
                }
                destroy();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void destroy() {
        tempWorkList=new LinkedList<AbstractIOTasks>();
        blockAbstractIOTasks =null;
        tempWorkCount=0;
        currentAbstractIOTasks =null;
    }

    private void consumer(LinkedList<AbstractIOTasks> tempWorkList) throws IOException {
        iterator=tempWorkList.iterator();
        while(iterator.hasNext()){
            currentAbstractIOTasks =iterator.next();
            check(currentAbstractIOTasks);
            ioTaskProcessor.submitIOWork(currentAbstractIOTasks);
        }
    }

    private void check(AbstractIOTasks currentAbstractIOTasks) {
    }

    public int getTempWorkCount() {
        return tempWorkCount;
    }

    public void setTempWorkCount(int tempWorkCount) {
        this.tempWorkCount = tempWorkCount;
    }

    public Integer getLoadNo() {
        return loadNo;
    }

    public void setLoadNo(Integer loadNo) {
        this.loadNo = loadNo;
    }

    public LinkedBlockingQueue<AbstractIOTasks> getWorkQueue() {
        return workQueue;
    }

    public void setWorkQueue(LinkedBlockingQueue<AbstractIOTasks> workQueue) {
        this.workQueue = workQueue;
    }
}
