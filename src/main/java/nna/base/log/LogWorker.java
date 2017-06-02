package nna.base.log;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author NNA-SHUAI
 * @create 2017-05-26 22:48
 **/

public class LogWorker implements Runnable{
    private Integer no;//the index in the work group queue;
    //for init of log and the notifier of log's write event
    private LinkedBlockingQueue<LogTask> logIdQueue=new LinkedBlockingQueue<LogTask>();
    //key: the threadId of log ,value:the write task of the log
    private ConcurrentHashMap<Long,LogTaskList> logTaskMap=new ConcurrentHashMap<Long, LogTaskList>();

    //for performance;
    private Iterator<LogTask> iterator;
    private LogTask logTask;
    private LogTaskList logTaskList;
    private Long threadId;
    private int logTaskCount;
    private LinkedList list;
    private LogTask nextTask;

    public void run() {
        try{
            while(true){
                logTaskCount=logIdQueue.size();
                list=new LinkedList();
                if(logTaskCount > 0){
                    logTaskCount=logIdQueue.size();
                    logIdQueue.drainTo(list,logTaskCount);
                }else{
                    nextTask=logIdQueue.take();
                    list.add(nextTask);
                }
                write(list);
                destroy();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void destroy() {
        iterator=null;
        logTask=null;
        logTaskList=null;
        threadId=null;
        logTaskCount=0;
        list=null;
        nextTask=null;
    }

    private void write(LinkedList<LogTask> linkedList){
        iterator=linkedList.iterator();
        while(iterator.hasNext()){
            logTask=iterator.next();
            threadId=logTask.log.getThreadId();
            logTaskList=logTaskMap.get(threadId);
            writeLog(logTaskList);
        }
    }

    private void writeLog(LogTaskList tasks) {
        LinkedList<LogTask> list=tasks.deQueue();
        if(list!=null){
            Iterator<LogTask> iterator=list.iterator();
            LogTask logTask;
            while(iterator.hasNext()){
                logTask=iterator.next();
                logTask.write();
            }
        }
    }

    private void submitInitEvent(Log log){
        LogTask logTask=new LogTask(log,null,LogTask.LOG_INIT);
        logTask.write();
        logIdQueue.offer(logTask);
    }

    private void submitWriteEvent(Log log,String logBuffer){
        LogTaskList logTaskList=logTaskMap.get(log.getThreadId());
        logTaskList.enQueue(log,logBuffer);
    }

    private void submitCloseEvent(Log log){
        LogTask logTask=new LogTask(log,null,LogTask.LOG_CLOSE);
        logIdQueue.offer(logTask);
    }

     void submitLog(Log log, String logStr, int taskType) {
        switch (taskType){
            case LogTask.LOG_INIT:
                submitInitEvent(log);
                break;
            case LogTask.LOG_WRITE:
                submitWriteEvent(log,logStr);
                break;
            case LogTask.LOG_CLOSE:
                submitCloseEvent(log);
                break;
            default:
                submitDefault(log);
                break;
        }
    }

    private void submitDefault(Log log) {
        LogTask logTask=new LogTask(log,null,LogTask.LOG_WRITE_NOTIFY);
        logIdQueue.offer(logTask);
    }

     Integer getNo() {
        return no;
    }

     void setNo(Integer no) {
        this.no = no;
    }


    class LogTask{
         static final int LOG_INIT=0;
         static final int LOG_WRITE=1;
         static final int LOG_CLOSE=2;
         static final int LOG_WRITE_NOTIFY=3;

        private String logBuffer;
        private Log log;
        private int type;

        private LogTask(Log log,String logBuffer,int type){
            this.logBuffer=logBuffer;
            this.log=log;
            this.type=type;
        }

        private void write(){
            switch (type){
                case LOG_INIT://初始化
                    log.init();
                    logTaskMap.put(log.getThreadId(),new LogTaskList());
                    break;
                case LOG_WRITE://写日志
                    log.write(logBuffer);
                    break;
                case LOG_CLOSE://关闭日志
                    log.close();
                    logTaskMap.remove(log.getThreadId());
                    logIdQueue.remove(this);
                    break;
                case LOG_WRITE_NOTIFY:
                    //写日志通知
                    break;
            }
        }
    }


    //for high performance
    private class LogTaskList{
        private volatile LinkedBlockingQueue<LogTask> queue=new LinkedBlockingQueue<LogTask>();
        private volatile boolean canDequeue=true;
        private volatile boolean canEnqueue;

        private void enQueue(Log log,String logBuffer){
            LogTask logTask=new LogTask(log,logBuffer,LogTask.LOG_WRITE);
            canDequeue=false;
            while(!canEnqueue){
                continue;
            }
            queue.offer(logTask);
            canDequeue=true;
        }

        private LinkedList<LogTask> deQueue(){
            LinkedList<LogTask> list = new LinkedList<LogTask>();
            //实际的效果是 有可能导致饥饿，也就是只有在服务结束掉之后 才能进行
            // 日志的 完全打印
            if(canDequeue){
                canEnqueue=false;
                queue.drainTo(list);
                canEnqueue=true;
            }
            return list;
        }
    }


    LinkedBlockingQueue<LogTask> getLogIdQueue() {
        return logIdQueue;
    }

    void setLogIdQueue(LinkedBlockingQueue<LogTask> logIdQueue) {
        this.logIdQueue = logIdQueue;
    }
}
