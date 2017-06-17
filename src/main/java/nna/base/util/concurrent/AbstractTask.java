package nna.base.util.concurrent;


import java.util.concurrent.locks.ReentrantLock;

/**
 * @author NNA-SHUAI
 * @create 2017-05-29 18:50
 **/

public abstract class AbstractTask{
    public static final int OVER=-1;//任务结束

    private AbstractTasks tasks;
    private int failTryTimes;
    private int workCount;
    private Long index;//任务队列索引
    private String taskName;
    private Thread thread;
    private Long threadId;
    private String threadName;
    private volatile int taskStatus;
//    private Integer workId;//所属工作组 负载均衡 是由具体哪个worker 来处理任务
    private Worker worker;
    private volatile boolean isInit=false;
    private ReentrantLock initLock=new ReentrantLock();
    private boolean isWorkSeq;

    static{
        System.out.println("init Worker Manager!");
        WorkerEntry.init(null,null);
    }

    public AbstractTask(String taskName,int workCount,boolean isWorkSeq){
        this.taskName=taskName;
        this.thread=Thread.currentThread();
        threadId=thread.getId();
        threadName=thread.getName();
        this.workCount=workCount;
        this.isWorkSeq=isWorkSeq;
    }

    protected void submitEvent(Object object,int taskType){
        WorkerEntry.submitEvent(this,object,taskType);
    }

    protected abstract Object doTask(int taskType,Object attach);

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

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

//    public Integer getWorkId() {
//        return workId;
//    }
//
//    public void setWorkId(Integer workId) {
//        this.workId = workId;
//    }

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

    public AbstractTasks getTasks() {
        return tasks;
    }

    public void setTasks(AbstractTasks tasks) {
        this.tasks = tasks;
    }

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public boolean isWorkSeq() {
        return isWorkSeq;
    }

    public void setWorkSeq(boolean workSeq) {
        isWorkSeq = workSeq;
    }
}
