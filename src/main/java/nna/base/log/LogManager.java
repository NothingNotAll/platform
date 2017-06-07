package nna.base.log;

import nna.base.util.List;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author NNA-SHUAI
 * @create 2017-05-27 10:47
 **/

public class LogManager {
    //next step is add the load balance and the log monitor ;one principal is that the log is only been logged by the same thread;
    private List<LogWorker> workerList=new List<LogWorker>(50);
    private ExecutorService cachedService=Executors.newCachedThreadPool();
    private ExecutorService fixedLogWorkerService;
    private LinkedList<LogWorker> balancedWorkerList=new LinkedList<LogWorker>();

    public LogManager(int logWorkerCount){
        init(logWorkerCount);
    }

    private void init(int logWorkerCount) {
        fixedLogWorkerService=Executors.newFixedThreadPool(logWorkerCount);
        cachedService=Executors.newCachedThreadPool();
        LogWorker logWorker;
        for(int index=0;index < logWorkerCount;index++){
            logWorker=new LogWorker();
            logWorker.setNo(index);
            workerList.insert(logWorker,1000);
            fixedLogWorkerService.submit(logWorker);
            balancedWorkerList.add(logWorker);
        }
    }

     void addLogWorker(LogWorker logWorker,int tryTime){
    }

     void deleteWorker(int deleteLogWorkerId){
    }

     Log getLog(
            String logDir,
            AtomicLong logSeq,
            String logFileName,
            int appLogLevel,
            int flushLimit,
            int closeTimeout,
            String encode
    ){
        Log log=new Log(
                logDir,
                logSeq,
                logFileName,
                appLogLevel,
                flushLimit,
                closeTimeout,encode);
        submitInitEvent(log);
        return log;
    }


    private void submitInitEvent(Log log){
        LogWorker logWorker=getBalanceLogWorker(log);
        LogDispatcher logDispatcher=new LogDispatcher(logWorker,log, LogWorker.LogTask.LOG_INIT);
        cachedService.submit(logDispatcher);
    }

     void submitWriteEvent(Log log,String logStr){

        //ensure the sequence of log print;if u do not
        //care the sequence , u can submit it to the thread instead immediately. this will improve the performance of the logic thread
        /*
        * no sequence mode for high performance:
        *  LogDispatcher logDispatcher=new LogDispatcher(LOG_WORKERV2,log, LogWorker.LogTask.LOG_WRITE);
        *  cachedService.submit(logDispatcher);
        * */
        LogWorker logWorker=getBalanceLogWorker(log);
        logWorker.submitLog(log, logStr, LogWorker.LogTask.LOG_WRITE);
        LogDispatcher logDispatcher=new LogDispatcher(logWorker,log, LogWorker.LogTask.LOG_WRITE_NOTIFY);
        cachedService.submit(logDispatcher);
    }

     void submitCloseEvent(Log log){
        LogWorker logWorker=getBalanceLogWorker(log);
        logWorker.submitLog(log, null, LogWorker.LogTask.LOG_CLOSE);
        LogDispatcher logDispatcher=new LogDispatcher(logWorker,log, LogWorker.LogTask.LOG_WRITE_NOTIFY);
        cachedService.submit(logDispatcher);
    }

    private LogWorker getBalanceLogWorker(Log log) {
        Integer integer=log.getLogWorkerNo();
        if(integer==null){
            return getMinCountLogWorker();
        }else{
            return balancedWorkerList.get(integer);
        }
    }

    private LogWorker getMinCountLogWorker(){
        Iterator<LogWorker> iterator=balancedWorkerList.iterator();
        LogWorker logWorker;
        int tempCount=-1;
        LogWorker minCountLogWorker=iterator.next();
        int minCount=minCountLogWorker.getLogIdQueue().size();
        if(minCount==0){
            return  minCountLogWorker;
        }
        while(iterator.hasNext()){
            logWorker=iterator.next();
            tempCount=logWorker.getLogIdQueue().size();
            if(tempCount==0){
                return logWorker;
            }
            if(minCount>tempCount){
                minCountLogWorker=logWorker;
            }
        }
        return minCountLogWorker;
    }
}
