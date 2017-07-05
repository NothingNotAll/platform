package nna.base.util.concurrent;


import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * 也就是 生产者线程和消费者线程双方来决定，
 * 到时有一个定时任务。
 * @author NNA-SHUAI
 * @create 2017-06-29 11:06
 **/

 class AbstractEnAndDeSgy{
    static final ExecutorService cached= Executors.newCachedThreadPool();
    private static volatile AbstractEnAndDeSgy abstractEnAndDeSgy;
    private static ReentrantLock initLock=new ReentrantLock();

    private  ConcurrentHashMap<String,Long> gTaskIdStrToTwId=new ConcurrentHashMap<String, Long>();
    private  ConcurrentHashMap<Long,String> gTaskIdToClazzNmMap=new ConcurrentHashMap<Long, String>();

    private static QueueWrapper enQueue(QueueWrapper[] qws,TaskWrapper taskWrapper) {
        QueueWrapper minLoad=QueueWrapper.enQueue(qws,taskWrapper);
        return minLoad;
    }

    private static void init(){
        if(abstractEnAndDeSgy==null){
            try{
                initLock.lock();
                if(abstractEnAndDeSgy==null){
                    abstractEnAndDeSgy=new AbstractEnAndDeSgy();
                    for(int index=0;index <15;index++){
                        ThreadWrapper tw=new ThreadWrapper(QueueWrapper.noSeqQwMap,false);
                        cached.submit(tw);
                        ThreadWrapper.noSeqTwMap.put(ThreadWrapper.noSeqTwSeqGen.getAndIncrement(),tw);
                    }
                }
            }catch (Exception e){
                e.fillInStackTrace();
            }finally {
                initLock.unlock();
            }
        }
    }

    static void initStrategy(Long gTaskId,String className,Boolean isSeq) {
        init();
        if(isSeq){
            abstractEnAndDeSgy.gTaskIdToClazzNmMap.put(gTaskId,gTaskId.toString());
            QueueWrapper[] qws=QueueWrapper.addQueue(1);
            QueueWrapper.seqQwMap.putIfAbsent(gTaskId.toString(),qws);
            if(ThreadWrapper.isSeqInit.compareAndSet(false,true)){
                ThreadWrapper tw=new ThreadWrapper(QueueWrapper.seqQwMap,true);
                Long seqTw=ThreadWrapper.seqTwSeqGen.getAndIncrement();
                ThreadWrapper.seqTwMap.put(seqTw,tw);
                abstractEnAndDeSgy.gTaskIdStrToTwId.put("-1L",seqTw);
                cached.submit(tw);
            }
            abstractEnAndDeSgy.gTaskIdStrToTwId.putIfAbsent(gTaskId.toString(),abstractEnAndDeSgy.gTaskIdStrToTwId.get("-1L"));
        }else{
            abstractEnAndDeSgy.gTaskIdToClazzNmMap.putIfAbsent(gTaskId,className);
            QueueWrapper[] qws=QueueWrapper.addQueue(15);
            QueueWrapper.noSeqQwMap.putIfAbsent(className,qws);
        }
    }

    static void addNewTask(Long gTaskId, TaskWrapper taskWrapper) {
        //
        String clazzNm=abstractEnAndDeSgy.gTaskIdToClazzNmMap.get(gTaskId);
        QueueWrapper[] qws=QueueWrapper.seqQwMap.get(clazzNm);
        if(qws==null){
            qws=QueueWrapper.noSeqQwMap.get(clazzNm);
            enQueue(qws,taskWrapper);
            ThreadWrapper.unPark(ThreadWrapper.noSeqTwMap);
        }else{
            Long twSeqId=abstractEnAndDeSgy.gTaskIdStrToTwId.get(gTaskId.toString());
            ThreadWrapper tw=ThreadWrapper.seqTwMap.get(twSeqId);
            enQueue(qws,taskWrapper);
//            ThreadWrapper.unPark(ThreadWrapper.seqTwMap);
            ThreadWrapper.unPark(tw);
        }
    }

    static void addNoSeqThread(Integer threadCount){
        ThreadWrapper tw;
        for(int index=0;index < threadCount;index++){
            tw=new ThreadWrapper(QueueWrapper.noSeqQwMap,false);
            ThreadWrapper.seqTwMap.put(ThreadWrapper.seqTwSeqGen.getAndIncrement(),tw);
            cached.submit(tw);
        }
    }
}
