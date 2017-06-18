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

 class IOEventProcessorManager {
    private static AtomicLong taskNo=new AtomicLong(0L);
    private static ConcurrentHashMap<Long,AbstractIOTasks> monitorMap=new ConcurrentHashMap<Long, AbstractIOTasks>();
    private static IOEventProcessorManager IOEventProcessorManager;
    private static volatile boolean init=false;
    private static ExecutorService cachedService= Executors.newCachedThreadPool();

    /*
    * workCount:we should think of that the time of every IO task,
    * if time is too short ,the workCount can be set only by CPU's Core size;
    *
    * if time is too Long , we can set the workCount with the Core Size of Cpu and
    * think of :
    * workCount=max(time/threshold,coreSize);
    * */

    synchronized static IOEventProcessorManager initWorkerManager(Long maxBusinessProcessTime, Long thresholdTime){
        if(init){
            return IOEventProcessorManager;
        }
        init=true;
        int workCount=getAvlCPUCount();
        if(maxBusinessProcessTime!=null&&thresholdTime!=null){
            int count=getBusinessCount(maxBusinessProcessTime,thresholdTime);
            workCount=Math.max(count,workCount);
        }
        IOEventProcessorManager =new IOEventProcessorManager(workCount);
        return IOEventProcessorManager;
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

    private ExecutorService fixedLogWorkerService;
    private ArrayList<IOEventProcessor> balancedIOEventProcessorList;

    private IOEventProcessorManager(int workerCount){
        init(workerCount);
    }

    private void init(int workerCount) {
        IOMonitor IOMonitor =new IOMonitor();
        IOEventProcessor[] IOEventProcessors =new IOEventProcessor[workerCount];
        balancedIOEventProcessorList =new ArrayList<IOEventProcessor>(workerCount);
        fixedLogWorkerService=Executors.newFixedThreadPool(workerCount);
        IOEventProcessor tempIOEventProcessor;
        for(int index=0;index < workerCount;index++){
            tempIOEventProcessor =new IOEventProcessor();
            tempIOEventProcessor.setLoadNo(index);
            balancedIOEventProcessorList.add(tempIOEventProcessor);
            fixedLogWorkerService.submit(tempIOEventProcessor);
            IOEventProcessors[index]= tempIOEventProcessor;
        }
        IOMonitor.setIOEventProcessors(IOEventProcessors);
        new Thread(IOMonitor).start();
    }

    void addWorker(IOEventProcessor IOEventProcessor){

    }

    void deleteWorker(){

    }

    void submitEvent(AbstractIOTask t, Object object, int taskType) {
        AbstractIOTasks abstractIOTasks =t.getTasks();
        Integer index=t.getiOLoadEventProcessorId();
        IOEventProcessor ioEventProcessor=balancedIOEventProcessorList.get(index);;
        Long workId= abstractIOTasks.getGlobalWorkId();
        if(taskType== AbstractIOTask.OVER){
            monitorMap.remove(workId);
        }
        if(!abstractIOTasks.isWorkSeq){
            EntryDesc entryDesc=getBalanceWorker();
            ioEventProcessor=entryDesc.IOEventProcessor;
            t.setiOLoadEventProcessorId(entryDesc.iOLoadEventProcessorId);
        }
        abstractIOTasks.addTask(t,taskType,object);
        Runnable nonInitDispatcher =new IODispatcher(abstractIOTasks, ioEventProcessor);
        cachedService.submit(nonInitDispatcher);
    }

    void submitInitEvent(AbstractIOTask t, Object object,boolean isWorkSeq){
        int workCount=t.getWorkCount();
        AbstractIOTasks abstractIOTasks;
        Long workId=taskNo.getAndIncrement();
        EntryDesc entryDesc=getBalanceWorker();
        IOEventProcessor ioEventProcessor=entryDesc.IOEventProcessor;
        if(isWorkSeq){
            abstractIOTasks =new SeqAbstractIOTasks(workCount,workId);
        }else{
            abstractIOTasks =new NoSeqAbstractIOTasks(workCount,workId);
        }
        t.setiOLoadEventProcessorId(entryDesc.iOLoadEventProcessorId);
        t.setTasks(abstractIOTasks);
        monitorMap.put(workId, abstractIOTasks);
        abstractIOTasks.setGlobalWorkId(workId);
        abstractIOTasks.addTask(t,AbstractIOTask.INIT,object);
        Runnable nonInitDispatcher =new IODispatcher(abstractIOTasks, ioEventProcessor);
        cachedService.submit(nonInitDispatcher);
    }

     EntryDesc getBalanceWorker() {
        Iterator<IOEventProcessor> iterator= balancedIOEventProcessorList.iterator();
        IOEventProcessor IOEventProcessor;
        IOEventProcessor minIOEventProcessor =null;
        int count;
        Integer minCount=null;
        Integer workerIndex=null;
        int index=0;
        while(iterator.hasNext()){
            IOEventProcessor =iterator.next();
            count= IOEventProcessor.getTempWorkCount();
            if(minCount==null){
                minIOEventProcessor = IOEventProcessor;
                minCount=count;
                workerIndex=index;
            }else{
                if(minCount>count){
                    minIOEventProcessor = IOEventProcessor;
                    workerIndex=index;
                    minCount=count;
                }
            }
            if(minCount==0){
                break;
            }
            index++;
        }
         return new EntryDesc(minIOEventProcessor,workerIndex);
    }

    public static ConcurrentHashMap<Long, AbstractIOTasks> getMonitorMap() {
        return monitorMap;
    }

    public static void setMonitorMap(ConcurrentHashMap<Long, AbstractIOTasks> monitorMap) {
        IOEventProcessorManager.monitorMap = monitorMap;
    }

    private class EntryDesc{
        private IOEventProcessor IOEventProcessor;
        private Integer iOLoadEventProcessorId;

        private EntryDesc(IOEventProcessor IOEventProcessor, Integer iOLoadEventProcessorId){
            this.IOEventProcessor = IOEventProcessor;
            this.iOLoadEventProcessorId=iOLoadEventProcessorId;
        }
    }

}
