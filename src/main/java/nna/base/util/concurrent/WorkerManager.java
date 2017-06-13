package nna.base.util.concurrent;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author NNA-SHUAI
 * @create 2017-05-30 16:40
 **/

 class WorkerManager {
    private static WorkerManager workerManager;
    private static volatile boolean init=false;
    private static volatile boolean initPending=false;

    public synchronized static WorkerManager initWorkerManager(Integer workCount){
        if(init){
            while(!initPending){
                continue;
            }
            return workerManager;
        }
        init=true;
        if(workCount==null){
            workCount=Runtime.getRuntime().availableProcessors()-1;
        }
        workerManager=new WorkerManager(workCount,new Worker());
        initPending=true;
        return workerManager;
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

    Worker getWorker(AbstractTask abstractTask){
         Integer workId=abstractTask.getWorkId();
         if(workId==null){
             WorkerEntry workerEntry= getBalanceWorker();
             WorkerEntry entry=getBalanceWorker();
             abstractTask.setWorkId(entry.workerId);
             return workerEntry.worker;
         }else{
             return balancedWorkerList.get(workId.intValue());
         }
    }

    private WorkerEntry getBalanceWorker() {
        WorkerEntry workerEntry=new WorkerEntry();
        Iterator<Worker> iterator=balancedWorkerList.iterator();
        Worker worker;
        Worker minWorker=null;
        int index=0;
        int minIndex=0;
        int count;
        int minCount=Integer.MAX_VALUE;
        while(iterator.hasNext()){
            worker=iterator.next();
            count=worker.getTempWorkCount();
            if(minCount<count){
                minWorker=worker;
                minIndex=index;
            }
            index++;
        }
        workerEntry.workerId=minIndex;
        workerEntry.worker=minWorker;
        return workerEntry;
    }

    private class WorkerEntry{
         private Worker worker;
         private int workerId;
    }

}
