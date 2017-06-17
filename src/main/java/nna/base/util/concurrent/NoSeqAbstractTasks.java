package nna.base.util.concurrent;

/**
 * @author NNA-SHUAI
 * @create 2017-06-15 11:36
 **/

public class NoSeqAbstractTasks extends AbstractTasks {

    NoSeqAbstractTasks(int taskCount) {
        super(taskCount);
    }

    protected AbstractTask doTasks() {
        AbstractTask abstractTask=null;
        int tempIndex=0;
        for(;tempIndex<workCount;tempIndex++){
            abstractTask=list[tempIndex];
            if(abstractTask!=null){
                // for 乐观锁 ; for performance
                if(status[tempIndex]==START){
                    lockAndExe(abstractTask,tempIndex);
                }
            }
        }
        return abstractTask;
    }
}
