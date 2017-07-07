package nna.base.util.concurrent;


import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * 也就是 生产者线程和消费者线程双方来决定，
 * 到时有一个定时任务。
 * @author NNA-SHUAI
 * @create 2017-06-29 11:06
 **/

 class AbstractEnAndDeSgy{
    static AbstractEnAndDeSgy abstractEnAndDeSgy=new AbstractEnAndDeSgy();
    final ExecutorService cached= Executors.newCachedThreadPool();
    static ConcurrentHashMap<Integer,AbstractEnAndDeSgy> workersMap=new ConcurrentHashMap<Integer, AbstractEnAndDeSgy>();
    static AtomicInteger wrokerSeqGen=new AtomicInteger();
    static{
//        for(int index=0;index < 15;index++){
//            AbstractEnAndDeSgy abstractEnAndDeSgy=new AbstractEnAndDeSgy();
//            workersMap.put(wrokerSeqGen.getAndIncrement(),abstractEnAndDeSgy);
//        }
    }

    private static void getMinLoadAbstractEnAndDeSgy(){
        AbstractEnAndDeSgy abstractEnAndDeSgy=null;
        Iterator<Map.Entry<Integer,AbstractEnAndDeSgy>> iterator=workersMap.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<Integer,AbstractEnAndDeSgy> entry=iterator.next();
            if(abstractEnAndDeSgy==null){
                abstractEnAndDeSgy=entry.getValue();

            }else{

            }
        }
    }

    private AbstractEnAndDeSgy(){
        for(int index=0;index <1;index++){
            ThreadWrapper tw=new ThreadWrapper(QueueWrapper.getQwMap(),false);
            cached.submit(tw);
        }
    }

     void initStrategy(Long gTaskId,String gTaskIdStr) {
        QueueWrapper.addQueue(gTaskIdStr,gTaskId);
    }

     void addNewTask(Long gTaskId, TaskWrapper taskWrapper) {
        QueueWrapper.enQueue(gTaskId,taskWrapper);
        ThreadWrapper.unPark(taskWrapper);
    }

     void addThreads(Integer threadCount){
        ThreadWrapper[] tws=ThreadWrapper.addThreads(threadCount);
        for(ThreadWrapper tw:tws){
            cached.submit(tw);
        }
    }
}
