package nna.base.util.concurrent;


/**
 * @author NNA-SHUAI
 * @create 2017-06-22 21:52
 **/

 class TaskWrapper implements Runnable{

    private AbstractTask abstractTask;
    private Object att;
    private Integer taskType;
    private Integer taskStatus=AbstractTask.INIT_STATUS;
    private Object returnObject;
    private Long enQueueTime=System.currentTimeMillis();
    private Long deQueueTime;
    private Long delayTime;
    private Boolean isSeq;
    private Integer workIndex;//

    private Integer taskPriorLevel;
    private Integer failTryTime;
    private Boolean isNewThreadToExe;

     TaskWrapper(
             AbstractTask abstractTask,
             Object att,
             Integer taskType,
             Boolean isNewThreadToExe,
             Long delayTime){
        this.isSeq=abstractTask.getSeq();
        this.workIndex=isSeq?abstractTask.getWorkIndexGen().getAndIncrement():null;
        this.delayTime=delayTime;
        this.isNewThreadToExe=isNewThreadToExe;
        this.att=att;
        this.abstractTask=abstractTask;
        this.taskType=taskType;
        this.taskStatus=AbstractTask.START_STATUS;
    }

    public void run() {
        try {
            if(delayTime!=null){
                Thread.sleep(delayTime);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        doInTask();
    }

    TaskWrapper doTask(){
        if(isNewThreadToExe){
            this.taskStatus=AbstractTask.WORK_STATUS;
            AbstractEnAndDeSgy.cached.submit(this);
            return null;
        }
        return doInTask();
    }

    private TaskWrapper doInTask() {
        try{
            if(!isSeq){
                doInnerTask();
                return null;
            }else{
                Integer currentWorkIndex=abstractTask.getCurrentWorkIndex();
                if(workIndex.equals(currentWorkIndex)){
                    doInnerTask();
                    abstractTask.setCurrentWorkIndex(++currentWorkIndex);
                    return null;
                }else{
//                    Long gTaskId=AbstractTask.getGTaskIdGen().getAndIncrement();
//                    AbstractEnAndDeSgy.initStrategy(gTaskId,"GLOBAL_SEQ_TASK_NO_"+gTaskId);
//                    AbstractEnAndDeSgy.addNewTask(gTaskId,this);
                    return this;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            this.taskStatus=AbstractTask.FAIL_STATUS;
            return null;
        }
    }
    private void doInnerTask() throws Exception {
        this.taskStatus=AbstractTask.WORK_STATUS;
        returnObject=abstractTask.doTask(att,taskType);
        this.deQueueTime=System.currentTimeMillis();
        this.taskStatus=AbstractTask.END_STATUS;
    }

     AbstractTask getAbstractTask() {
        return abstractTask;
    }

     void setAbstractTask(AbstractTask abstractTask) {
        this.abstractTask = abstractTask;
    }

     Object getAtt() {
        return att;
    }

     void setAtt(Object att) {
        this.att = att;
    }

     int getTaskType() {
        return taskType;
    }

     void setTaskType(int taskType) {
        this.taskType = taskType;
    }

     int getTaskStatus() {
        return taskStatus;
    }

     void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

     Object getReturnObject() {
        return returnObject;
    }

     void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

     int getTaskPriorLevel() {
        return taskPriorLevel;
    }

     void setTaskPriorLevel(int taskPriorLevel) {
        this.taskPriorLevel = taskPriorLevel;
    }

     Long getEnQueueTime() {
        return enQueueTime;
    }

     void setEnQueueTime(Long enQueueTime) {
        this.enQueueTime = enQueueTime;
    }

     Long getDeQueueTime() {
        return deQueueTime;
    }

     void setDeQueueTime(Long deQueueTime) {
        this.deQueueTime = deQueueTime;
    }

     Integer getFailTryTime() {
        return failTryTime;
     }

     void setFailTryTime(Integer failTryTime) {
        this.failTryTime = failTryTime;
     }

    public Boolean getNewThreadToExe() {
        return isNewThreadToExe;
    }

    public void setNewThreadToExe(Boolean newThreadToExe) {
        isNewThreadToExe = newThreadToExe;
    }

    public Long getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(Long delayTime) {
        this.delayTime = delayTime;
    }

    public Boolean getSeq() {
        return isSeq;
    }

    public void setSeq(Boolean seq) {
        isSeq = seq;
    }

    public Integer getWorkIndex() {
        return workIndex;
    }

    public void setWorkIndex(Integer workIndex) {
        this.workIndex = workIndex;
    }
}
