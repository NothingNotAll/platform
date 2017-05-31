package nna.base.util.concurrent;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author NNA-SHUAI
 * @create 2017-05-30 16:40
 **/

public class WorkerManager {
    private ExecutorService cachedService= Executors.newCachedThreadPool();
    private ExecutorService fixedLogWorkerService;
    private LinkedList<Worker> balancedWorkerList=new LinkedList<Worker>();

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
        balancedWorkerList.addLast(worker);
    }

    void deleteWorker(){

    }
     void submitEvent(AbstractTask abstractTask){
        Worker worker=getBalanceWorker();
        WorkerDispatcher workerDispatcher=new WorkerDispatcher(worker,abstractTask);
        cachedService.submit(workerDispatcher);
    }

     void submitWorkEvent(AbstractTask abstractTask){
        Worker worker=getBalanceWorker();
        worker.submitTask(abstractTask);
        WorkerDispatcher workerDispatcher=new WorkerDispatcher(worker,abstractTask);
        cachedService.submit(workerDispatcher);
    }

    private Worker getBalanceWorker() {
        Iterator<Worker> iterator=balancedWorkerList.iterator();
        Worker worker=null;
        Worker minWorker = null;
        int count;
        int minCount=Integer.MAX_VALUE;
        while(iterator.hasNext()){
            worker=iterator.next();
            count=worker.getWorkerCount();
            if(minCount<count){
                minWorker=worker;
            }
        }
        return minWorker==null?worker:minWorker;
    }

}
