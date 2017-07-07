package nna.base.util.concurrent;


import java.util.concurrent.*;

/**
 *
 * 也就是 生产者线程和消费者线程双方来决定，
 * 到时有一个定时任务。
 * @author NNA-SHUAI
 * @create 2017-06-29 11:06
 **/

 class AbstractEnAndDeSgy{
    static final ExecutorService cached= Executors.newCachedThreadPool();
    static AbstractEnAndDeSgy abstractEnAndDeSgy=new AbstractEnAndDeSgy();

    private ThreadWrapper threadWrapper;

    private AbstractEnAndDeSgy(){
        for(int index=0;index <1;index++){
            threadWrapper=new ThreadWrapper(QueueWrapper.getQwMap(),false);
            cached.submit(threadWrapper);
        }
    }

    void initStrategy(Long gTaskId,String gTaskIdStr,boolean isSeq) {
        if(isSeq){
            QueueWrapper.addQueue(gTaskIdStr,gTaskId,1);
        }else{
            QueueWrapper.addQueue(gTaskIdStr,gTaskId,15);
        }
    }

    void addNewTask(Long gTaskId, TaskWrapper taskWrapper) {
        QueueWrapper.enQueue(gTaskId,taskWrapper);
        threadWrapper.unParkThread(taskWrapper.getAbstractTask().getTwId());
    }
}
