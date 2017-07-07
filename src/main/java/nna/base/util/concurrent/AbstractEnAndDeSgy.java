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
    private static final Long GSQID=AbstractTask.getGTaskIdGen().getAndIncrement();
    static{
        AbstractEnAndDeSgy.initStrategy(GSQID,"GLOBAL_SEQ_QUEUE");
    }

    static void addNewTask(TaskWrapper taskWrapper){
        while(QueueWrapper.getQwMap().get("GLOBAL_SEQ_QUEUE")==null){
            continue;
        }
        QueueWrapper.enQueue(GSQID,taskWrapper);
        ThreadWrapper.unPark(ThreadWrapper.getTwMap());
    }

    private AbstractEnAndDeSgy(){
        for(int index=0;index <15;index++){
            ThreadWrapper tw=new ThreadWrapper(QueueWrapper.getQwMap(),false);
            cached.submit(tw);
        }
    }

    static void initStrategy(Long gTaskId,String className) {
        QueueWrapper.addQueue(className,gTaskId);
    }

    static void addNewTask(Long gTaskId, TaskWrapper taskWrapper) {
        QueueWrapper.enQueue(gTaskId,taskWrapper);
        ThreadWrapper.unPark(ThreadWrapper.getTwMap());
    }

    static void addThreads(Integer threadCount){
        ThreadWrapper[] tws=ThreadWrapper.addThreads(threadCount);
        for(ThreadWrapper tw:tws){
            cached.submit(tw);
        }
    }
}
