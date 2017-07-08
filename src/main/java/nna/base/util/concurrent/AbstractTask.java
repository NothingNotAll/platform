package nna.base.util.concurrent;


import java.util.concurrent.atomic.AtomicInteger;
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
    private volatile int taskStatus;
    private Long initThreadId;
    private String initThreadNm;
    private AtomicInteger workIndexGen;
    private volatile Integer currentWorkIndex;
    private Boolean isSeq;

    //for Seq Task
    private Long twId;
    private Integer abstractEnAndDeSgyId;

    public AbstractTask(Boolean isSeq){
        this.isSeq=isSeq;
        gTaskId=gTaskIdGen.getAndIncrement();
        String className=getClass().getCanonicalName();
        this.taskName=className+"_NO."+gTaskId;
        Thread thread=Thread.currentThread();
        initThreadId=thread.getId();
        initThreadNm=thread.getName();
        currentWorkIndex=0;
        workIndexGen=new AtomicInteger();
        AbstractEnAndDeSgy abstractEnAndDeSgy=AbstractEnAndDeSgy.getMinLoad(isSeq,null);
        if(isSeq){
            abstractEnAndDeSgyId=abstractEnAndDeSgy.getWorkerId();
            this.twId=abstractEnAndDeSgy.getThreadWrapper().getTwSeqId();
        }
        abstractEnAndDeSgy.initStrategy(gTaskId,taskName,isSeq);
    }

    protected abstract Object doTask(Object att,int taskType) throws Exception;

    protected void addNewTask(
            AbstractTask abstractTask,
            Object att,
            int taskType,
            Boolean isNewTToExe,
            Long delayTime){
        TaskWrapper taskWrapper=new TaskWrapper(abstractTask,att,taskType,isNewTToExe,delayTime);
        if(isSeq){
            AbstractEnAndDeSgy.getMinLoad(isSeq,abstractEnAndDeSgyId).addNewTask(gTaskId,taskWrapper);
        }else{
            AbstractEnAndDeSgy.getMinLoad(isSeq,null).addNewTask(gTaskId,taskWrapper);
        }
    }

    public Long getgTaskId() {
        return gTaskId;
    }

    public void setGTaskId(Long gTaskId) {
        this.gTaskId = gTaskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Boolean getSeq() {
        return isSeq;
    }

    public void setSeq(Boolean seq) {
        isSeq = seq;
    }

    public Integer getCurrentWorkIndex() {
        return currentWorkIndex;
    }

    public void setCurrentWorkIndex(Integer currentWorkIndex) {
        this.currentWorkIndex = currentWorkIndex;
    }

    public AtomicInteger getWorkIndexGen() {
        return workIndexGen;
    }

    public void setWorkIndexGen(AtomicInteger workIndexGen) {
        this.workIndexGen = workIndexGen;
    }

    public static AtomicLong getGTaskIdGen() {
        return gTaskIdGen;
    }

    public Long getTwId() {
        return twId;
    }

    public Integer getAbstractEnAndDeSgyId() {
        return abstractEnAndDeSgyId;
    }

    public void setAbstractEnAndDeSgyId(Integer abstractEnAndDeSgyId) {
        this.abstractEnAndDeSgyId = abstractEnAndDeSgyId;
    }

    public Long getInitThreadId() {
        return initThreadId;
    }

    public void setInitThreadId(Long initThreadId) {
        this.initThreadId = initThreadId;
    }

    public String getInitThreadNm() {
        return initThreadNm;
    }

    public void setInitThreadNm(String initThreadNm) {
        this.initThreadNm = initThreadNm;
    }

    public static Long getAliveThreadCount(){
        return AbstractEnAndDeSgy.incThreadCount.get()+AbstractEnAndDeSgy.decThreadCount.get();
    }
}
