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

    SeqLinkedTasks(Long globalWorkId,String taskName) {
        super(0, globalWorkId,taskName);
    }

    //for min times of gc;
    private LinkedList<AbstractTaskWrapper> temp=new LinkedList<AbstractTaskWrapper>();
    private int tempCount;
    private AbstractTaskWrapper abstractTaskWrapper;
    protected int doTasks() {
        tempCount=abstractTaskWrappers.size();
        tempCount=abstractTaskWrappers.drainTo(temp,tempCount);
        if(tempCount==0){
            try {
                abstractTaskWrapper=abstractTaskWrappers.take();
                temp.add(abstractTaskWrapper);
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

    private AbstractTaskWrapper tempWrapper;
    public boolean addTask(AbstractTask abstractTask, Integer taskType,Object attach){
        Long startTime=System.currentTimeMillis();
        tempWrapper=new AbstractTaskWrapper();
        setNonNull(tempWrapper,startTime,abstractTask,taskType,attach);
        abstractTaskWrappers.add(tempWrapper);
        return true;
    }

    public void run(){
        try{
            while(true){
                int isOver=doTasks();
                if(isOver==AbstractTask.OVER){
                    return ;
                }else{
                    temp.clear();
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

    public LinkedBlockingQueue<AbstractTaskWrapper> getAbstractsTaskWrappers() {
        return abstractTaskWrappers;
    }

    public void setAbstractTaskWrappers(LinkedBlockingQueue<AbstractTaskWrapper> abstractTaskWrappers) {
        this.abstractTaskWrappers = abstractTaskWrappers;
    }

    public LinkedList<AbstractTaskWrapper> getTemp() {
        return temp;
    }

    public void setTemp(LinkedList<AbstractTaskWrapper> temp) {
        this.temp = temp;
    }

    public int getTempCount() {
        return tempCount;
    }

    public void setTempCount(int tempCount) {
        this.tempCount = tempCount;
    }

    public AbstractTaskWrapper getAbstractTaskWrapper() {
        return abstractTaskWrapper;
    }

    public void setAbstractTaskWrapper(AbstractTaskWrapper abstractTaskWrapper) {
        this.abstractTaskWrapper = abstractTaskWrapper;
    }

    public Iterator<AbstractTaskWrapper> getIterator() {
        return iterator;
    }

    public void setIterator(Iterator<AbstractTaskWrapper> iterator) {
        this.iterator = iterator;
    }

    public AbstractTaskWrapper getTempWrapper() {
        return tempWrapper;
    }

    public void setTempWrapper(AbstractTaskWrapper tempWrapper) {
        this.tempWrapper = tempWrapper;
    }

}
