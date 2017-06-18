package nna.base.util.concurrent;



/**
 * @author NNA-SHUAI
 * @create 2017-06-15 11:34
 **/

public class SeqAbstractIOTasks extends AbstractIOTasks {

    SeqAbstractIOTasks(int taskCount, Long workId) {
        super(taskCount,workId);
    }

    protected boolean doTasks() {
        AbstractIOTask abstractIOTask = null;
        int temp=enQueueIndex;//为了尽可能的照顾所有的 Tasks对象
        int taskType=-2;
        for(;workIndex < temp;workIndex++){
            abstractIOTask =list[workIndex];
            // for 乐观锁 ; for performance
            taskType=lockAndExe(abstractIOTask,workIndex);
        }
        return taskType==AbstractIOTask.OVER;
    }

    protected int lockAndExe(AbstractIOTask abstractIOTask, int tempIndex) {
        return work(abstractIOTask,tempIndex);
    }

    public static void main(String[] args){

    }

}
