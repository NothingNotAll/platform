package nna.base.util.concurrent;



/**
 * @author NNA-SHUAI
 * @create 2017-06-15 11:34
 **/

public class SeqAbstractIOTasks extends AbstractIOTasks {

    SeqAbstractIOTasks(int taskCount, Long workId) {
        super(taskCount,workId,true);
    }

    protected int doTasks() {
        int temp=enQueueIndex;//为了尽可能的照顾所有的 Tasks对象
        int taskType=AbstractIOTasks.INIT;
        for(;workIndex < temp;workIndex++){
            taskType=work(workIndex);
            switch (taskType){
                case AbstractIOTask.OVER:
                    return taskType;
                case AbstractIOTasks.INIT:
                    return taskType;
            }
        }
        return taskType;
    }

    protected int lockAndExe(int tempIndex) {
        return work(tempIndex);
    }

    public static void main(String[] args){

    }

}
