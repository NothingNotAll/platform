package nna.base.util.conv2;


import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author NNA-SHUAI
 * @create 2017-06-22 21:50
 **/

public abstract class AbstractTask {

    static final int INIT_TASK_TYPE=0;
    static final int OVER_TASK_TYPE=-1;
    private static final AtomicLong gTaskIdGen=new AtomicLong();

    private AbstractEnAndDeStgy abstractEnAndDeStgy;
    private Long gTaskId;
    private String taskName;
    private int taskCount;
    private volatile int taskStatus;
    private volatile boolean isInit=false;
    private ReentrantLock initLock=new ReentrantLock();

    public AbstractTask(
            Integer queueSize,
            Integer exeThreadCount,
            Integer strategyType,
            int executorServiceType){
        this.abstractEnAndDeStgy = AbstractEnAndDeStgy.getStrategy(queueSize,exeThreadCount,strategyType,null);
        if(abstractEnAndDeStgy.getNeedSubmit()){
            TaskSchedule.submitTask(this,executorServiceType);
        }
        abstractEnAndDeStgy.enQueue(this,null,INIT_TASK_TYPE,false,0L);
    }

    public AbstractTask(
            Integer queueSize,
            Integer exeThreadCount,
            Integer strategyType,Long delayTime){
        this.abstractEnAndDeStgy = AbstractEnAndDeStgy.getStrategy(queueSize,exeThreadCount,strategyType,delayTime);
        if(abstractEnAndDeStgy.getNeedSubmit()){
            TaskSchedule.submitTask(this);
        }
        abstractEnAndDeStgy.enQueue(this,null,INIT_TASK_TYPE,false,0L);
    }

    public abstract Object doTask(Object att,int taskType);

    protected void addNewTask(
            Object att,
            int taskType,
            Boolean isNewTToExe,
            Long delayTime){
        abstractEnAndDeStgy.enQueue(this,att,taskType,isNewTToExe,delayTime);
    }

    public AbstractEnAndDeStgy getAbstractEnAndDeStgy() {
        return abstractEnAndDeStgy;
    }

    public void setAbstractEnAndDeStgy(AbstractEnAndDeStgy abstractEnAndDeStgy) {
        this.abstractEnAndDeStgy = abstractEnAndDeStgy;
    }

    public Long getgTaskId() {
        return gTaskId;
    }

    public void setgTaskId(Long gTaskId) {
        this.gTaskId = gTaskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    public boolean isInit() {
        return isInit;
    }

    public void setInit(boolean init) {
        isInit = init;
    }

    public ReentrantLock getInitLock() {
        return initLock;
    }

    public void setInitLock(ReentrantLock initLock) {
        this.initLock = initLock;
    }
}
