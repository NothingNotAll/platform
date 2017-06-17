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
        Worker worker;
        AbstractTasks abstractTasks;
        int workCount=t.getWorkCount();
        if(t.isWorkSeq()){
            worker=t.getWorker();
            if(t.isInit()){
                abstractTasks=t.getTasks();
            }else{
                abstractTasks=new SeqAbstractTasks(workCount);
            }
        }else{
            worker=getBalanceWorker();
            if(t.isInit()){
                abstractTasks=t.getTasks();
            }else{
                abstractTasks=new NoSeqAbstractTasks(workCount);
            }
        }
        abstractTasks.addTask(t,taskType,object);
        Runnable nonInitDispatcher =new Dispatcher(abstractTasks,worker);
        cachedService.submit(nonInitDispatcher);
    }

     Worker getBalanceWorker() {
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
         return minWorker;
    }

}
