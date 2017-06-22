package nna.base.util.concurrent;


import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author NNA-SHUAI
 * @create 2017-06-15 11:34
 **/

public class SeqFixSizeTasks extends AbstractTasks {

    SeqFixSizeTasks(int taskCount, Long workId,String taskName) {
        super(taskCount,workId,taskName);
    }

    protected int doTasks() {
        int temp=enQueueIndex;//为了尽可能的照顾所有的 Tasks对象
        int taskType= AbstractTask.INIT;
        for(;workIndex < temp;workIndex++){
            AbstractTaskWrapper abstractTaskWrapper=abstractTaskWrappers[workIndex];
            taskType=work(abstractTaskWrapper);
            switch (taskType){
                case AbstractTask.OVER:
                    return taskType;
                case 0:
                    return 0;
            }
        }
        return taskType;
    }

    protected int lockAndExe(int tempIndex) {
        AbstractTaskWrapper abstractTaskWrapper=abstractTaskWrappers[tempIndex];
        return work(abstractTaskWrapper);
    }

    public static void main(String[] args){

    }

    boolean addTask(AbstractTask abstractTask, Integer taskType,Object attach){
        try{
            super.addTask(abstractTask,taskType,attach);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        unPark();
        return true;
    }


    ////Tasks execute design begin
    protected volatile Thread thread;
    protected AtomicLong inc=new AtomicLong(0L);
    protected AtomicLong add=new AtomicLong(0L);//avoid ABA bug
    protected ReentrantLock lock=new ReentrantLock();
    protected ReentrantLock tInitLock=new ReentrantLock();

    public void run() {
        if(tInitLock.tryLock()){
            thread=Thread.currentThread();
            try{
                while(true){
                    if(doTasks()!= AbstractTask.OVER){
                        park();
                    }else{
                        System.out.println("OVER");
                        break;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            unPark();
        }
    }

    private void park() {
        add.getAndIncrement();
        LockSupport.park();
    }

    protected void unPark(){
        while(thread==null){
            continue;
        }
        try{
            lock.lock();//性能阻塞点 避免 ABA问题 。
            LockSupport.unpark(thread);
            inc.getAndDecrement();
            Long result=inc.get()+add.get();
            while(result > 0){
                LockSupport.unpark(thread);
                inc.getAndDecrement();
                result=inc.get()+add.get();
            }
        }finally {
            lock.unlock();
        }
    }

    //Tasks execute design end



    public AtomicLong getInc() {
        return inc;
    }

    public void setInc(AtomicLong inc) {
        this.inc = inc;
    }

    public AtomicLong getAdd() {
        return add;
    }

    public void setAdd(AtomicLong add) {
        this.add = add;
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public void setLock(ReentrantLock lock) {
        this.lock = lock;
    }

    public ReentrantLock gettInitLock() {
        return tInitLock;
    }

    public void settInitLock(ReentrantLock tInitLock) {
        this.tInitLock = tInitLock;
    }

}
