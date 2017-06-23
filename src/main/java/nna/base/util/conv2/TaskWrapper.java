package nna.base.util.conv2;

/**
 * @author NNA-SHUAI
 * @create 2017-06-22 21:52
 **/

 class TaskWrapper implements Runnable{
    static final int INIT_STATUS=0;
    static final int START_STATUS=1;
    static final int WORK_STATUS=2;
    static final int END_STATUS=3;
    static final int FAIL_STATUS=4;

    private AbstractTask abstractTask;
    private Object att;
    private int taskType;
    private int taskStatus=INIT_STATUS;
    private Object returnObject;
    private Long enQueueTime=System.currentTimeMillis();
    private Long deQueueTime;
    private Long delayTime=0L;

    private int taskPriorLevel;
    private Integer failTryTime;
    private Boolean isNewThreadToExe;

     TaskWrapper(
             AbstractTask abstractTask,
             Object att,
             int taskType,
             Boolean isNewThreadToExe,
             Long delayTime){
        this.delayTime=delayTime;
        this.isNewThreadToExe=isNewThreadToExe;
        this.att=att;
        this.abstractTask=abstractTask;
        this.taskType=taskType;
        this.taskStatus=START_STATUS;
    }

     boolean doTask(){
         if(isNewThreadToExe){
            this.taskStatus=WORK_STATUS;
            TaskSchedule.submitTask(this);
            return true;
         }
         boolean isSuccess=false;
        try{
            this.taskStatus=WORK_STATUS;
            returnObject=abstractTask.doTask(att,taskType);
            this.deQueueTime=System.currentTimeMillis();
            this.taskStatus=END_STATUS;
            isSuccess=true;
        }catch (Exception e){
            e.printStackTrace();
            this.taskStatus=FAIL_STATUS;
        }
        return isSuccess;
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

    public void run() {
        try {
            Thread.sleep(delayTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        returnObject=doTask();
    }
}