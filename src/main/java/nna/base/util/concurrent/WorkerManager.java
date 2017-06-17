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

 class WorkerManager {
    private static AtomicLong taskNo=new AtomicLong(0L);
    private static ConcurrentHashMap<Long,AbstractTasks> monitorMap=new ConcurrentHashMap<Long, AbstractTasks>();
    private static WorkerManager workerManager;
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

    synchronized static WorkerManager initWorkerManager(Long maxBusinessProcessTime,Long thresholdTime){
        if(init){
            return workerManager;
        }
        init=true;
        int workCount=getAvlCPUCount();
        if(maxBusinessProcessTime!=null&&thresholdTime!=null){
            int count=getBusinessCount(maxBusinessProcessTime,thresholdTime);
            workCount=Math.max(count,workCount);
        }
        workerManager=new WorkerManager(workCount);
        return workerManager;
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
    private ArrayList<Worker> balancedWorkerList;

    private WorkerManager(int workerCount){
        init(workerCount);
    }

    private void init(int workerCount) {
        Monitor monitor=new Monitor();
        Worker[] workers=new Worker[workerCount];
        balancedWorkerList=new ArrayList<Worker>(workerCount);
        fixedLogWorkerService=Executors.newFixedThreadPool(workerCount);
        Worker tempWorker;
        for(int index=0;index < workerCount;index++){
            tempWorker=new Worker();
            tempWorker.setLoadNo(index);
            balancedWorkerList.add(tempWorker);
            fixedLogWorkerService.submit(tempWorker);
            workers[index]=tempWorker;
        }
        monitor.setWorkers(workers);
        new Thread(monitor).start();
    }

    void addWorker(Worker worker){

    }

    void deleteWorker(){

    }

    void submitEvent(AbstractTask t,Object object,int taskType) {
        AbstractTasks abstractTasks=t.getTasks();
        Integer index=t.getWorkId();
        Long workId;
        Worker worker;
        if(!t.isInit()){
            int workCount=t.getWorkCount();
            workId=taskNo.getAndIncrement();
            EntryDesc entryDesc=getBalanceWorker();
            if(t.isWorkSeq()){
                abstractTasks=new SeqAbstractTasks(workCount,workId);
                worker=entryDesc.worker;
                t.setWorkId(entryDesc.index);
            }else{
                abstractTasks=new NoSeqAbstractTasks(workCount,workId);
                worker=getBalanceWorker().worker;
            }
            t.setTasks(abstractTasks);
            monitorMap.put(workId,abstractTasks);
            abstractTasks.setWorkId(workId);
        }else{
            workId=abstractTasks.getWorkId();
            worker=balancedWorkerList.get(index);
        }
        if(taskType==AbstractTask.OVER){
            monitorMap.remove(workId);
        }
        abstractTasks.addTask(t,taskType,object);
        Runnable nonInitDispatcher =new Dispatcher(abstractTasks,worker);
        cachedService.submit(nonInitDispatcher);
    }

     EntryDesc getBalanceWorker() {
        Iterator<Worker> iterator=balancedWorkerList.iterator();
        Worker worker;
        Worker minWorker=null;
        int count;
        Integer minCount=null;
        Integer workerIndex=null;
        int index=0;
        while(iterator.hasNext()){
            worker=iterator.next();
            count=worker.getTempWorkCount();
            if(minCount==null){
                minWorker=worker;
                minCount=count;
                workerIndex=index;
            }else{
                if(minCount>count){
                    minWorker=worker;
                    workerIndex=index;
                    minCount=count;
                }
            }
            if(minCount==0){
                break;
            }
            index++;
        }
         return new EntryDesc(minWorker,workerIndex);
    }

    public static ConcurrentHashMap<Long, AbstractTasks> getMonitorMap() {
        return monitorMap;
    }

    public static void setMonitorMap(ConcurrentHashMap<Long, AbstractTasks> monitorMap) {
        WorkerManager.monitorMap = monitorMap;
    }

    private class EntryDesc{
        private Worker worker;
        private Integer index;

        private EntryDesc(Worker worker,Integer index){
            this.worker=worker;
            this.index=index;
        }
    }

}
