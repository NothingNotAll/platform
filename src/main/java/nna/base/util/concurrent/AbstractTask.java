package nna.base.util.concurrent;


/**
 * @author NNA-SHUAI
 * @create 2017-05-29 18:50
 **/

public abstract class AbstractTask{
    public static final int TASK_STATUS_CREATE=0;
    public static final int TASK_STATUS_INIT=1;
    public static final int TASK_STATUS_WORK=2;
    public static final int TASK_STATUS_DESTROY=4;

    private int index;
    private Long threadId;
    private String threadName;
    private Thread thread;
    private volatile int taskStatus;
    private int workId;//所属工作组

    public AbstractTask(){
        thread=Thread.currentThread();
        threadId=thread.getId();
        threadName=thread.getName();
        this.taskStatus=TASK_STATUS_CREATE;
    }

    public void submitInitEvent(WorkerEntry workerEntry){
        workerEntry.submitInitEvent(this);
    }

    public void submitEvent(WorkerEntry workerEntry){
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
}
