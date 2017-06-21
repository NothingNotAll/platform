package nna.base.util.concurrent;

/**
 * @author NNA-SHUAI
 * @create 2017-06-20 14:53
 **/

 class AbstractTaskWrapper<T extends AbstractTask>{
    private AbstractTask abstractTask;
    private Object object;
    private Integer taskType;
    private Integer taskStatus;
    private Long taskStartTimes;
    private Long taskEndTimes;
    private Long taskPriorLevels;
    private int taskTypes;

     AbstractTaskWrapper(T t,Object object,
                         Integer taskType){
        taskStartTimes=System.currentTimeMillis();
        abstractTask=t;
        this.object=object;
        taskStatus=AbstractTask.INIT;
        this.taskType=taskType;
    }

    AbstractTaskWrapper(){
        taskStartTimes=System.currentTimeMillis();
    }

    public AbstractTask getAbstractTask() {
        return abstractTask;
    }

    public void setAbstractTask(AbstractTask abstractTask) {
        this.abstractTask = abstractTask;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Long getTaskStartTimes() {
        return taskStartTimes;
    }

    public void setTaskStartTimes(Long taskStartTimes) {
        this.taskStartTimes = taskStartTimes;
    }

    public Long getTaskEndTimes() {
        return taskEndTimes;
    }

    public void setTaskEndTimes(Long taskEndTimes) {
        this.taskEndTimes = taskEndTimes;
    }

    public Long getTaskPriorLevels() {
        return taskPriorLevels;
    }

    public void setTaskPriorLevels(Long taskPriorLevels) {
        this.taskPriorLevels = taskPriorLevels;
    }

    public int getTaskTypes() {
        return taskTypes;
    }

    public void setTaskTypes(int taskTypes) {
        this.taskTypes = taskTypes;
    }

    public void reInit() {
        abstractTask=null;
        object=null;
        taskStatus=AbstractTask.INIT;
    }
}
