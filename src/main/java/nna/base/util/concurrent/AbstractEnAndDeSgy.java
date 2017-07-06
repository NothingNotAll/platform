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
    private static volatile AbstractEnAndDeSgy abstractEnAndDeSgy=new AbstractEnAndDeSgy();

    private  ConcurrentHashMap<Long,String> gTaskIdToClazzNmMap=new ConcurrentHashMap<Long, String>();

    private static QueueWrapper enQueue(QueueWrapper[] qws,TaskWrapper taskWrapper) {
        QueueWrapper minLoad=QueueWrapper.enQueue(qws,taskWrapper);
        return minLoad;
    }

    private AbstractEnAndDeSgy(){
        for(int index=0;index <15;index++){
            ThreadWrapper tw=new ThreadWrapper(QueueWrapper.noSeqQwMap,false);
            cached.submit(tw);
            ThreadWrapper.twMap.put(ThreadWrapper.twSeqGen.getAndIncrement(),tw);
        }
    }

    static void initStrategy(Long gTaskId,String className) {
        abstractEnAndDeSgy.gTaskIdToClazzNmMap.putIfAbsent(gTaskId,className);
        QueueWrapper[] qws=QueueWrapper.addQueue(15);
        QueueWrapper.noSeqQwMap.putIfAbsent(className,qws);
    }

    static void addNewTask(Long gTaskId, TaskWrapper taskWrapper) {
        String clazzNm=abstractEnAndDeSgy.gTaskIdToClazzNmMap.get(gTaskId);
        QueueWrapper[] qws=QueueWrapper.noSeqQwMap.get(clazzNm);
        enQueue(qws,taskWrapper);
        ThreadWrapper.unPark(ThreadWrapper.twMap);
    }

    static void addThread(Integer threadCount){
        ThreadWrapper tw;
        for(int index=0;index < threadCount;index++){
            tw=new ThreadWrapper(QueueWrapper.noSeqQwMap,false);
            ThreadWrapper.twMap.put(ThreadWrapper.twSeqGen.getAndIncrement(),tw);
            cached.submit(tw);
        }
    }
}
