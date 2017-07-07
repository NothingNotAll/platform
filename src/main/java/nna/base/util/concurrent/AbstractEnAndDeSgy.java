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
    static{
        new AbstractEnAndDeSgy();
    }
    private AbstractEnAndDeSgy(){
        for(int index=0;index <1;index++){
            ThreadWrapper tw=new ThreadWrapper(QueueWrapper.getQwMap(),false);
            cached.submit(tw);
        }
    }

    static void initStrategy(Long gTaskId,String gTaskIdStr) {
        QueueWrapper.addQueue(gTaskIdStr,gTaskId);
    }

    static void addNewTask(Long gTaskId, TaskWrapper taskWrapper) {
        QueueWrapper.enQueue(gTaskId,taskWrapper);
        ThreadWrapper.unPark(taskWrapper);
    }

    static void addThreads(Integer threadCount){
        ThreadWrapper[] tws=ThreadWrapper.addThreads(threadCount);
        for(ThreadWrapper tw:tws){
            cached.submit(tw);
        }
    }
}
