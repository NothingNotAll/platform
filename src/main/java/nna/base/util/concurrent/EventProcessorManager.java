package nna.base.util.concurrent;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author NNA-SHUAI
 * @create 2017-05-30 16:40
 **/

 class EventProcessorManager {
    private static AtomicLong taskNo=new AtomicLong(0L);
    private static ConcurrentHashMap<Long,AbstractTasks> monitorMap=new ConcurrentHashMap<Long, AbstractTasks>();
    private static EventProcessorManager EventProcessorManager;
    private static volatile boolean init=false;
    private static ExecutorService cachedService = Executors.newCachedThreadPool();

    private ExecutorService fixedLogWorkerService;
    private ArrayList<EventProcessor> balancedEventProcessorList;

    /*
    * workCount:we should think of that the time of every IO task,
    * if time is too short ,the workCount can be set only by CPU's Core size;
    *
    * if time is too Long , we can set the workCount with the Core Size of Cpu and
    * think of :
    * workCount=max(time/threshold,coreSize);
    * */

    synchronized static EventProcessorManager initWorkerManager(Long maxBusinessProcessTime, Long thresholdTime){
        if(init){
            return EventProcessorManager;
        }
        init=true;
        int workCount=getAvlCPUCount();
        if(maxBusinessProcessTime!=null&&thresholdTime!=null){
            int count=getBusinessCount(maxBusinessProcessTime,thresholdTime);
            workCount=Math.max(count,workCount);
        }
        EventProcessorManager =new EventProcessorManager(workCount);
        return EventProcessorManager;
    }

    private static int getBusinessCount(Long maxBusinessProcessTime,Long thresholdTime){
        long count=maxBusinessProcessTime/thresholdTime;
        count=maxBusinessProcessTime%thresholdTime==0?count:count+1;
        return (int)count;
    }

    private static int getAvlCPUCount(){
        int coreCount=Runtime.getRuntime().availableProcessors();
        return coreCount<=1?1:coreCount-1;
    }

    private EventProcessorManager(int workerCount){
        init(workerCount*10);
    }

    private void init(int workerCount) {
        Monitor Monitor =new Monitor();
        EventProcessor[] EventProcessors =new EventProcessor[workerCount];
        balancedEventProcessorList =new ArrayList<EventProcessor>(workerCount);
        fixedLogWorkerService=Executors.newFixedThreadPool(workerCount);
        EventProcessor tempEventProcessor;
        for(int index=0;index < workerCount;index++){
            tempEventProcessor =new EventProcessor();
            tempEventProcessor.setLoadNo(index);
            balancedEventProcessorList.add(tempEventProcessor);
            fixedLogWorkerService.submit(tempEventProcessor);
            EventProcessors[index]= tempEventProcessor;
        }
        Monitor.setEventProcessors(EventProcessors);
        Monitor.setMap(monitorMap);
        new Thread(Monitor).start();
    }

    void addWorker(EventProcessor EventProcessor){

    }

    void deleteWorker(){

    }

    private boolean loadAlg(){
        return true;
    }

    void startTask(AbstractTask t, Object object, boolean isWorkSeq){
        if(loadAlg()){
            submitInitEvent(t,object,isWorkSeq);
        }else{
            submitAsyInitEvcent(t,object,isWorkSeq);
        }
    }

    void addNewTask(AbstractTask t, Object object, int taskType){
        if(loadAlg()){
            submitEvent(t,object,taskType);
        }else{
            submitAsyEvent(t,object,taskType);
        }
    }

    void submitEvent(AbstractTask t, Object object, int taskType) {
        AbstractTasks abstractTasks =t.getTasks();
//        remove(abstractTasks,taskType);
        abstractTasks.addTask(t,taskType,object);
    }

    private void remove(AbstractTasks abstractTasks, int taskType) {
        Long workId= abstractTasks.getGlobalWorkId();
        if(taskType== AbstractTask.OVER){
            monitorMap.remove(workId);
        }
    }

    void submitAsyEvent(AbstractTask t, Object object, int taskType){
        AbstractTasks abstractTasks =t.getTasks();
        Integer index=t.getiOLoadEventProcessorId();
        EventProcessor eventProcessor = balancedEventProcessorList.get(index);;
        if(abstractTasks instanceof NoSeqFixSizeTasks){
            EntryDesc entryDesc=getBalanceWorker();
            eventProcessor =entryDesc.EventProcessor;
            t.setiOLoadEventProcessorId(entryDesc.iOLoadEventProcessorId);
        }
//      remove(abstractTasks,taskType);
        Runnable dispatcher =new Dispatcher(abstractTasks, eventProcessor);
        cachedService.submit(dispatcher);
    }

    void submitAsyInitEvcent(AbstractTask t, Object object, boolean isWorkSeq){
        AbstractTasks abstractTasks;
        int workCount=t.getWorkCount();
        EntryDesc entryDesc=getBalanceWorker();
        EventProcessor eventProcessor =entryDesc.EventProcessor;
        t.setiOLoadEventProcessorId(entryDesc.iOLoadEventProcessorId);
        if(isWorkSeq){
            abstractTasks =new SeqTasks(workCount,null);
        }else{
            abstractTasks =new NoSeqFixSizeTasks(workCount,null);
        }
      //enWorkMap(abstractTasks);
        Runnable dispatcher =new Dispatcher(abstractTasks, eventProcessor);
        cachedService.submit(dispatcher);
        abstractTasks.addTask(t, AbstractTask.INIT,object);
    }

    void submitInitEvent(AbstractTask t, Object object, boolean isWorkSeq){
        int workCount=t.getWorkCount();
        AbstractTasks abstractTasks;
        if(isWorkSeq){
            abstractTasks =new SeqTasks(workCount,null);
        }else{
            abstractTasks =new NoSeqFixSizeTasks(workCount,null);
        }
//        enWorkMap(abstractTasks);
        t.setTasks(abstractTasks);
        cachedService.submit(abstractTasks);
        abstractTasks.addTask(t, AbstractTask.INIT,object);
    }

    private void enWorkMap(AbstractTasks abstractTasks) {
        Long workId=taskNo.getAndIncrement();
        abstractTasks.setGlobalWorkId(workId);
        monitorMap.put(workId, abstractTasks);
    }

    EntryDesc getBalanceWorker() {
        Iterator<EventProcessor> iterator= balancedEventProcessorList.iterator();
        EventProcessor EventProcessor;
        EventProcessor minEventProcessor =null;
        int count;
        Integer minCount=null;
        Integer workerIndex=null;
        int index=0;
        while(iterator.hasNext()){
            EventProcessor =iterator.next();
            count= EventProcessor.getTempWorkCount();
            if(minCount==null){
                minEventProcessor = EventProcessor;
                minCount=count;
                workerIndex=index;
            }else{
                if(minCount>count){
                    minEventProcessor = EventProcessor;
                    workerIndex=index;
                    minCount=count;
                }
            }
            if(minCount==0){
                break;
            }
            index++;
        }
         return new EntryDesc(minEventProcessor,workerIndex);
    }

    private class EntryDesc{
        private EventProcessor EventProcessor;
        private Integer iOLoadEventProcessorId;

        private EntryDesc(EventProcessor EventProcessor, Integer iOLoadEventProcessorId){
            this.EventProcessor = EventProcessor;
            this.iOLoadEventProcessorId=iOLoadEventProcessorId;
        }
    }
}
