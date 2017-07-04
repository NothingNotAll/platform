package nna.base.util.concurrent;


import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author NNA-SHUAI
 * @create 2017-06-22 21:50
 **/

public abstract class AbstractTask {
    private static final AtomicLong gTaskIdGen=new AtomicLong();

    public static final int INIT_TASK_TYPE=0;
    public static final int WORK_TASK_TYPE=1;
    public static final int OVER_TASK_TYPE=2;
    public static final int OVER = -1;
    public static final int INIT_STATUS=3;
    public static final int START_STATUS=4;
    public static final int WORK_STATUS=5;
    public static final int END_STATUS=6;
    public static final int FAIL_STATUS=7;

    private Long gTaskId;
    private String taskName;
    private int taskCount;
    private volatile int taskStatus;
    private volatile boolean isInit=false;
    private ReentrantLock initLock=new ReentrantLock();
    private Long threadId;
    private String threadName;

    public AbstractTask(Boolean isSeq){
        gTaskId=gTaskIdGen.getAndIncrement();
        String className=getClass().getCanonicalName();
        this.taskName=className+"_"+gTaskId;
        Thread thread=Thread.currentThread();
        threadId=thread.getId();
        threadName=thread.getName();
        AbstractEnAndDeSgy.initStrategy(gTaskId,className,isSeq);
        MonitorTask.addMonitor(this);
    }

    protected abstract Object doTask(Object att,int taskType) throws Exception;

    protected void addNewTask(
            AbstractTask abstractTask,
            Object att,
            int taskType,
            Boolean isNewTToExe,
            Long delayTime){
        TaskWrapper taskWrapper=new TaskWrapper(abstractTask,att,taskType,isNewTToExe,delayTime);
        AbstractEnAndDeSgy.addNewTask(gTaskId,taskWrapper);
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

    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public static void init() {

    }
}
