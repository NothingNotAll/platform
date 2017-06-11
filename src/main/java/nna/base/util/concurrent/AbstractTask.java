package nna.base.util.concurrent;


import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author NNA-SHUAI
 * @create 2017-05-29 18:50
 **/

public abstract class AbstractTask{
    public static final int TASK_STATUS_CREATE=0;
    public static final int TASK_STATUS_INIT=1;
    public static final int TASK_STATUS_WORK=2;
    public static final int TASK_STATUS_DESTROY=3;

    private static WorkerEntry workerEntry;

    private int index;
    private AtomicInteger atomicInteger=new AtomicInteger();
    private String taskName;
    private Long threadId;
    private String threadName;
    private Thread thread;
    private volatile int taskStatus;
    private int workId;//所属工作组

    public AbstractTask(String taskName){
        this.taskName=taskName;
        thread=Thread.currentThread();
        threadId=thread.getId();
        threadName=thread.getName();
        this.taskStatus=TASK_STATUS_CREATE;
    }

    static {
        initWorkEntry();
    }

    protected static void initWorkEntry(){
        workerEntry=new WorkerEntry();
    }

    public void submitInitEvent(){
        index=0;
        workerEntry.submitInitEvent(this);
    }

    public void submitEvent(){
        this.index=atomicInteger.getAndIncrement();
        workerEntry.submitEvent(this);
    }

    public abstract void create();

    public abstract void init();

    public abstract void work();

    public abstract void destroy();

    public abstract void otherWork();

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    public int getWorkId() {
        return workId;
    }

    public void setWorkId(int workId) {
        this.workId = workId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
}
