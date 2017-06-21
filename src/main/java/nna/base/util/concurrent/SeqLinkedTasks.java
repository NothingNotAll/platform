package nna.base.util.concurrent;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author NNA-SHUAI
 * @create 2017-06-21 9:03
 **/

public class SeqLinkedTasks extends AbstractTasks {

    protected LinkedBlockingQueue<AbstractTaskWrapper> abstractTaskWrappers=new LinkedBlockingQueue<AbstractTaskWrapper>();

    SeqLinkedTasks(Long globalWorkId) {
        super(0, globalWorkId);
    }

    //for min times of gc;
    private LinkedList<AbstractTaskWrapper> temp=new LinkedList<AbstractTaskWrapper>();
    private int tempCount;
    private AbstractTaskWrapper abstractTaskWrapper;
    protected int doTasks() {
        tempCount=abstractTaskWrappers.size();
        abstractTaskWrappers.drainTo(temp,tempCount);
        if(tempCount==0){
            try {
                abstractTaskWrapper=abstractTaskWrappers.take();
                temp.addAll(abstractTaskWrappers);
                tempCount=1;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return consumer(temp);
    }

    private Iterator<AbstractTaskWrapper> iterator;
    private int consumer(LinkedList<AbstractTaskWrapper> temp) {
        iterator=temp.iterator();
        int isOver=AbstractTask.OVER+1;
        while(iterator.hasNext()){
            abstractTaskWrapper=iterator.next();
            isOver=work(abstractTaskWrapper);
        }
        return isOver;
    }

    public void run(){
        try{
            while(true){
                int isOver=doTasks();
                if(isOver==AbstractTask.OVER){
                    return ;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {

        }
    }

    protected int lockAndExe(int tempIndex) {
        return 0;
    }

    boolean addTask(AbstractTask abstractTask, Integer taskType,Object attach){
        return true;
    }
}
