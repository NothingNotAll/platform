package nna.base.util.concurrent;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * 无序工作其实依赖于 addNewWork的isNewThread参数来实现。
 * 也就是 生产者线程和消费者线程双方来决定，
 * 到时有一个定时任务。
 * @author NNA-SHUAI
 * @create 2017-06-29 11:06
 **/

 class AbstractEnAndDeSgy{
    private static final ExecutorService cached= Executors.newCachedThreadPool();
    static AbstractEnAndDeSgy abstractEnAndDeSgy=new AbstractEnAndDeSgy();

    private  ConcurrentHashMap<Long,String> noSeqIdToClazzNmMap=new ConcurrentHashMap<Long, String>();
    private  ConcurrentHashMap<Long,String> seqIdToClazzNmMap=new ConcurrentHashMap<Long, String>();

    private static QueueWrapper enQueue(QueueWrapper[] qws,TaskWrapper taskWrapper,Integer leftCount) {
        QueueWrapper minLoad=QueueWrapper.enQueue(qws,leftCount,taskWrapper);
        return minLoad;
    }

    static void initStrategy(Long gTaskId,String className,Boolean isSeq) {
        if(isSeq){
            QueueWrapper[] qws=QueueWrapper.addQueue(1);
            abstractEnAndDeSgy.seqIdToClazzNmMap.put(gTaskId,gTaskId.toString());
            QueueWrapper.seqQwMap.putIfAbsent(gTaskId.toString(),qws);
            if(ThreadWrapper.isSeqInit.compareAndSet(false,true)){
                ThreadWrapper tw=new ThreadWrapper(QueueWrapper.seqQwMap);
                ThreadWrapper.seqTwMap.put(ThreadWrapper.seqTwSeqGen.getAndIncrement(),tw);
                cached.submit(tw);
            }
        }else{
            QueueWrapper[] qws=QueueWrapper.addQueue(15);
            abstractEnAndDeSgy.noSeqIdToClazzNmMap.put(gTaskId,className);
            QueueWrapper.noSeqQwMap.putIfAbsent(className,qws);
            if(ThreadWrapper.isNoSeqInit.compareAndSet(false,true)){
                for(int index=0;index <15;index++){
                    ThreadWrapper tw=new ThreadWrapper(QueueWrapper.noSeqQwMap);
                    cached.submit(tw);
                    ThreadWrapper.noSeqTwMap.put(ThreadWrapper.noSeqTwSeqGen.getAndIncrement(),tw);
                }
            }
        }
    }

    static void addNewTask(Long gTaskId, TaskWrapper taskWrapper) {
        QueueWrapper[] qws;
        String clazzNm=abstractEnAndDeSgy.noSeqIdToClazzNmMap.get(gTaskId);
        Integer lefCount;
        if(clazzNm==null){
            clazzNm=abstractEnAndDeSgy.seqIdToClazzNmMap.get(gTaskId);
            qws=QueueWrapper.seqQwMap.get(clazzNm);
            lefCount=qws.length-1;
        }else{
            qws=QueueWrapper.noSeqQwMap.get(clazzNm);
            lefCount=qws.length-ThreadWrapper.noSeqTwMap.size();
        }
        enQueue(qws,taskWrapper,lefCount);
    }

    static void addNoSeqThread(Integer threadCount){
        ThreadWrapper tw;
        for(int index=0;index < threadCount;index++){
            tw=new ThreadWrapper(QueueWrapper.noSeqQwMap);
            ThreadWrapper.noSeqTwMap.put(ThreadWrapper.noSeqTwSeqGen.getAndIncrement(),tw);
            cached.submit(tw);
        }
    }

    static void addSeqThread(Integer threadCount){
        ThreadWrapper tw;
        for(int index=0;index < threadCount;index++){
            tw=new ThreadWrapper(QueueWrapper.seqQwMap);
            ThreadWrapper.noSeqTwMap.put(ThreadWrapper.seqTwSeqGen.getAndIncrement(),tw);
            cached.submit(tw);
        }
    }
}
