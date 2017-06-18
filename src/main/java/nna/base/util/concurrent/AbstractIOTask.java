package nna.base.util.concurrent;


import java.util.concurrent.locks.ReentrantLock;

/**
 * @author NNA-SHUAI
 * @create 2017-05-29 18:50
 **/

public abstract class AbstractIOTask {
    public static final int OVER=-1;//任务结束
    public static final int INIT=0;

    private AbstractIOTasks tasks;
    private int failTryTimes;
    private int workCount;
    private String taskName;
    private Thread thread;
    private Long threadId;
    private String threadName;
    private volatile int taskStatus;
    private volatile Integer iOLoadEventProcessorId;//所属工作组 负载均衡 是由具体哪个worker 来处理任务
    private volatile boolean isInit=false;
    private ReentrantLock initLock=new ReentrantLock();

    static{
        System.out.println("init IOEventProcessor Manager!");
        IOTaskEntry.init(null,null);
    }

    public AbstractIOTask(String taskName, int workCount, boolean isWorkSeq){
        this.taskName=taskName;
        this.thread=Thread.currentThread();
        threadId=thread.getId();
        threadName=thread.getName();
        this.workCount=workCount;
    }

    protected void submitEvent(Object object,int taskType){
        IOTaskEntry.submitEvent(this,object,taskType);
    }

    protected void submitInitEvent(Object object,boolean isWorkSeq){
        IOTaskEntry.submitInitEvent(this,object,isWorkSeq);
    }

    protected abstract Object doTask(int taskType,Object attach);

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

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
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

    public int getFailTryTimes() {
        return failTryTimes;
    }

    public void setFailTryTimes(int failTryTimes) {
        this.failTryTimes = failTryTimes;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public ReentrantLock getInitLock() {
        return initLock;
    }

    public void setInitLock(ReentrantLock initLock) {
        this.initLock = initLock;
    }

    public AbstractIOTasks getTasks() {
        return tasks;
    }

    public void setTasks(AbstractIOTasks tasks) {
        this.tasks = tasks;
    }

    public Integer getiOLoadEventProcessorId() {
        return iOLoadEventProcessorId;
    }

    public void setiOLoadEventProcessorId(Integer iOLoadEventProcessorId) {
        this.iOLoadEventProcessorId = iOLoadEventProcessorId;
    }
}
