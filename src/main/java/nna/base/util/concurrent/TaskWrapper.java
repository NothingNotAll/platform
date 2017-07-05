package nna.base.util.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author NNA-SHUAI
 * @create 2017-06-22 21:52
 **/

 class TaskWrapper implements Runnable{
    static final TaskWrapper DO_NOTHING=new TaskWrapper(null,null,null,null,null);

    private AbstractTask abstractTask;
    private Object att;
    private Integer taskType;
    private Integer taskStatus=AbstractTask.INIT_STATUS;
    private Object returnObject;
    private Long enQueueTime=System.currentTimeMillis();
    private Long deQueueTime;
    private Long delayTime;

    private Integer taskPriorLevel;
    private Integer failTryTime;
    private Boolean isNewThreadToExe;

     TaskWrapper(
             AbstractTask abstractTask,
             Object att,
             Integer taskType,
             Boolean isNewThreadToExe,
             Long delayTime){
        this.delayTime=delayTime;
        this.isNewThreadToExe=isNewThreadToExe;
        this.att=att;
        this.abstractTask=abstractTask;
        this.taskType=taskType;
        this.taskStatus=AbstractTask.START_STATUS;
    }

    public void run() {
        System.out.println("run asy worker");
        try {
            Thread.sleep(delayTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        doInTask();
    }

    boolean doTask(){
        if(isNewThreadToExe){
            this.taskStatus=AbstractTask.WORK_STATUS;
            AbstractEnAndDeSgy.cached.submit(this);
            return true;
        }
        return doInTask();
    }

    private boolean doInTask() {
        boolean isSuccess=false;
        try{
            this.taskStatus=AbstractTask.WORK_STATUS;
            returnObject=abstractTask.doTask(att,taskType);
            this.deQueueTime=System.currentTimeMillis();
            this.taskStatus=AbstractTask.END_STATUS;
            return true;
        }catch (Exception e){
            e.printStackTrace();
            this.taskStatus=AbstractTask.FAIL_STATUS;
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
}
