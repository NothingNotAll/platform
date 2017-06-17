package nna.base.util.concurrent;


/**
 * @author NNA-SHUAI
 * @create 2017-06-15 11:34
 **/

public class SeqAbstractTasks extends AbstractTasks {
    SeqAbstractTasks(int taskCount) {
        super(taskCount);
    }

    protected AbstractTask doTasks() {
        AbstractTask abstractTask = null;
        int temp=enQueueIndex;//为了尽可能的照顾所有的 Tasks对象
        for(;workIndex < temp;workIndex++){
            abstractTask=list[workIndex];
            // for 乐观锁 ; for performance
            if(status[workIndex]==START){
                lockAndExe(abstractTask,workIndex);
            }
        }
        return abstractTask;
    }

    public static void main(String[] args){
    }

}
