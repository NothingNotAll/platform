package nna.base.util.concurrent;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author NNA-SHUAI
 * @create 2017-05-30 16:40
 **/

public class WorkerManager {
    private ExecutorService cachedService= Executors.newCachedThreadPool();
    private ExecutorService fixedLogWorkerService;
    private ArrayList<Worker> balancedWorkerList=new ArrayList<Worker>();

    public WorkerManager(int workerCount,Worker worker){
        init(workerCount,worker);
    }

    private void init(int workerCount,Worker worker) {
        fixedLogWorkerService=Executors.newFixedThreadPool(workerCount);
        cachedService=Executors.newCachedThreadPool();
        Worker logWorker;
        for(int index=0;index < workerCount;index++){
            logWorker= (Worker) worker.clone();
            logWorker.setLoadNo(index);
            balancedWorkerList.add(logWorker);
            fixedLogWorkerService.submit(logWorker);
            balancedWorkerList.add(logWorker);
        }
    }

    void addWorker(Worker worker){

    }

    void deleteWorker(){

    }
     void submitEvent(AbstractTask abstractTask){
        int workId=abstractTask.getWorkId();
        Worker worker=balancedWorkerList.get(workId);
        TaskDispatcher taskDispatcher =new TaskDispatcher(worker,abstractTask);
        cachedService.submit(taskDispatcher);
    }

     void submitInitEvent(AbstractTask abstractTask){
        WorkerEntry entry=getBalanceWorker();
        abstractTask.setWorkId(entry.workerId);
        entry.worker.submitInitTask(abstractTask);
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
            count=worker.getWorkerCount();
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
         Worker worker;
         int workerId;
    }

}
