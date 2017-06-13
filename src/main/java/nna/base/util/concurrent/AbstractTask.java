package nna.base.util.concurrent;


import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author NNA-SHUAI
 * @create 2017-05-29 18:50
 **/

public abstract class AbstractTask{

    public static final int TASK_STATUS_WORK=2;
    public static final int TASK_STATUS_DESTROY=3;

    private int workCount;
    private Long index;//任务队列索引
    private String taskName;
    private Long threadId;
    private String threadName;
    private Thread thread;
    private volatile int taskStatus;
    private Integer workId;//所属工作组 负载均衡 是由具体哪个worker 来处理任务
    private volatile boolean isInit=false;

    static{
        System.out.println("init Worker Manager!");
        WorkerEntry.init(null);
    }

    public AbstractTask(String taskName,Object initObjectAttach){
        this.taskName=taskName;
        thread=Thread.currentThread();
        threadId=thread.getId();
        threadName=thread.getName();
        setTaskStatus(0);
        init(initObjectAttach);
        isInit=true;
    }

    protected void submitInitEvent(Object object,boolean keepWorkSeq){
        WorkerEntry.submitInitEvent(this,object,keepWorkSeq);
    }

    protected void submitEvent(Object object){
        WorkerEntry.submitEvent(this,object);
    }

    public abstract Object init(Object object);

    public abstract Object work(Object object);

    public abstract Object otherWork(Object object);

    public abstract Object destroy(Object object) throws IOException;

    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
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

    public Integer getWorkId() {
        return workId;
    }

    public void setWorkId(Integer workId) {
        this.workId = workId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getWorkCount() {
        return workCount;
    }

    public void setWorkCount(int workCount) {
        this.workCount = workCount;
    }

    public boolean isInit() {
        return isInit;
    }

    public void setInit(boolean init) {
        isInit = init;
    }
}
