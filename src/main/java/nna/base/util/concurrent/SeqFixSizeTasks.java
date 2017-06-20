package nna.base.util.concurrent;



/**
 * @author NNA-SHUAI
 * @create 2017-06-15 11:34
 **/

public class SeqFixSizeTasks extends AbstractTasks {

    SeqFixSizeTasks(int taskCount, Long workId) {
        super(taskCount,workId);
    }

    protected int doTasks() {
        int temp=enQueueIndex;//为了尽可能的照顾所有的 Tasks对象
        int taskType= AbstractTask.INIT;
        for(;workIndex < temp;workIndex++){
            taskType=work(workIndex);
            switch (taskType){
                case AbstractTask.OVER:
                    return taskType;
                case 0:
                    return 0;
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
