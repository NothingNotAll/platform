package nna.base.log;


/**
 * @author NNA-SHUAI
 * @create 2017-05-21 14:42
 **/

public class LogDispatcher implements  Runnable {

    private LogWorker logWorker;
    private int taskType;
    private Log log;

    public LogDispatcher(){

    }

    public LogDispatcher(LogWorker logWorker, Log log, int taskType){
        this.log=log;
        this.logWorker=logWorker;
        this.log=log;
        this.taskType=taskType;
    }

    public void run() {
        try{
            work();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
        }
    }

    private void work() {
        logWorker.submitLog(log,null,taskType);
    }
}
