package nna.base.util.concurrent;

/**
 * @author NNA-SHUAI
 * @create 2017-06-15 11:36
 **/

public class NoSeqAbstractIOTasks extends AbstractIOTasks {

    NoSeqAbstractIOTasks(int taskCount, Long workId) {
        super(taskCount,workId);
    }

    protected AbstractIOTask doTasks() {
        AbstractIOTask abstractIOTask =null;
        int tempIndex=0;
        for(;tempIndex<workCount;tempIndex++){
            abstractIOTask =list[tempIndex];
            if(abstractIOTask !=null){
                // for 乐观锁 ; for performance
                if(status[tempIndex]==START){
                    lockAndExe(abstractIOTask,tempIndex);
                }
            }
        }
        return abstractIOTask;
    }
}
