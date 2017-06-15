package nna.base.util.concurrent;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author NNA-SHUAI
 * @create 2017-05-30 16:40
 **/

 class WorkerManager {
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
        workerManager=new WorkerManager(1,new Worker());
        return workerManager;
    }

    private static int getBusinessCount(Long maxBusinessProcessTime,Long thresholdTime){
        long count=maxBusinessProcessTime/thresholdTime;
        count=maxBusinessProcessTime%thresholdTime==0?count:count+1;
        return (int)count;
    }

    private static int getAvlCPUCount(){
        int coreCount=Runtime.getRuntime().availableProcessors()-1;
        return coreCount<=1?1:coreCount-1;
    }

    private ExecutorService fixedLogWorkerService;
    private ArrayList<Worker> balancedWorkerList=new ArrayList<Worker>();

    private WorkerManager(int workerCount,Worker worker){
        init(workerCount,worker);
    }

    private void init(int workerCount,Worker worker) {
        fixedLogWorkerService=Executors.newFixedThreadPool(workerCount);
        Worker logWorker;
        for(int index=0;index < workerCount;index++){
            logWorker= (Worker) worker.clone();
            logWorker.setLoadNo(index);
            balancedWorkerList.add(logWorker);
            fixedLogWorkerService.submit(logWorker);
        }
    }

    void addWorker(Worker worker){

    }

    void deleteWorker(){

    }

    void submitEvent(AbstractTask t,Object object,int taskStatus) {
        Integer workId=t.getWorkId();
        Worker worker=balancedWorkerList.get(workId.intValue());
        Long taskSeq=t.getIndex();
        ConcurrentHashMap<Long,AbstractTasks> workMap=worker.getWorkMap();
        AbstractTasks abstractTasks =workMap.get(taskSeq);
        abstractTasks.addTask(t,taskStatus,object);
        Runnable nonInitDispatcher =new NonInitDispatcher(abstractTasks,worker);
        cachedService.submit(nonInitDispatcher);
    }

    void submitInitEvent(AbstractTask t,
                         Object object,
                         int taskStatus) {
        AbstractTasks abstractTasks =new SeqAbstractTasks(t.getWorkCount());
        abstractTasks.addTask(t,taskStatus,object);
        Entry entry=getBalanceWorker();
        Worker worker=entry.worker;
        int workId=entry.entryId;
        t.setWorkId(workId);
        Long taskSeq=Worker.taskNo.getAndIncrement();//性能瓶頸點
        t.setIndex(taskSeq);
        Runnable initDispatcher =new InitDispatcher(abstractTasks, worker);
        cachedService.submit(initDispatcher);
    }

    private Entry getBalanceWorker() {
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
        return new Entry(minWorker,workerIndex);
    }

    private class Entry{
        private Integer entryId;
        private Worker worker;
        private Entry(Worker minWorker,int index){
            this.entryId=index;
            this.worker=minWorker;
        }
    }
}
