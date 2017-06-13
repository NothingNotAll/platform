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
    private static ExecutorService cachedService= Executors.newCachedThreadPool();

    synchronized static WorkerManager initWorkerManager(Integer workCount){
        if(init){
            return workerManager;
        }
        init=true;
        if(workCount==null){
            int coreCount=Runtime.getRuntime().availableProcessors()-1;
            workCount=coreCount<=1?1:coreCount-1;
        }
        workerManager=new WorkerManager(workCount,new Worker());
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

    void submitEvent(AbstractTask t,Object object) {
        Integer workId=t.getWorkId();
        Worker worker=balancedWorkerList.get(workId.intValue());
        Tasks tasks=worker.submitEvent(t,object);
        Dispatcher dispatcher=new Dispatcher(tasks,worker, false);
        cachedService.submit(dispatcher);
    }

    void submitInitEvent(AbstractTask t,Object object,boolean keepWorkSeq) {
        Entry entry=getBalanceWorker();
        Worker worker=entry.worker;
        int workId=entry.entryId;
        t.setWorkId(workId);
        Tasks tasks=worker.submitInitEvent(t,object,keepWorkSeq);
        Dispatcher mapDispatcher=new Dispatcher(tasks, worker,true);
        cachedService.submit(mapDispatcher);
    }

    private Entry getBalanceWorker() {
        Iterator<Worker> iterator=balancedWorkerList.iterator();
        Worker worker;
        Worker minWorker=null;
        int count;
        Integer minCount=null;
        int index=0;
        while(iterator.hasNext()){
            worker=iterator.next();
            count=worker.getTempWorkCount();
            if(minCount==null){
                minWorker=worker;
                minCount=count;
            }else{
                if(minCount>count){
                    minWorker=worker;
                }
            }
            if(minCount==0){
                break;
            }
            index++;
        }
        return new Entry(minWorker,index);
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
