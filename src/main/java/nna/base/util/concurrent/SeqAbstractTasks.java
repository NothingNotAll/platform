package nna.base.util.concurrent;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author NNA-SHUAI
 * @create 2017-06-15 11:34
 **/

public class SeqAbstractTasks extends AbstractTasks {
    SeqAbstractTasks(int taskCount) {
        super(taskCount);
    }

    void works(ConcurrentHashMap<Long, AbstractTasks> workMap) throws IOException {
        AbstractTask abstractTask;
        int temp=enQueueIndex;//为了尽可能的照顾所有的 Tasks对象
        for(;workIndex < temp;workIndex++){
            abstractTask=list[workIndex];
            // for 乐观锁 ; for performance
            if(status[workIndex]==START){
                lockAndExe(abstractTask,workMap,workIndex);
            }
        }
    }


}
