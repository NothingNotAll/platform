package nna.base.util.concurrent;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author NNA-SHUAI
 * @create 2017-06-15 11:36
 **/

public class NoSeqAbstractTasks extends AbstractTasks {

    NoSeqAbstractTasks(int taskCount) {
        super(taskCount);
    }

    void works(ConcurrentHashMap<Long, AbstractTasks> workMap) throws IOException {
            AbstractTask abstractTask;
            int tempIndex=0;
            for(;tempIndex<workCount;tempIndex++){
                abstractTask=list[tempIndex];
                if(abstractTask!=null){
                    // for 乐观锁 ; for performance
                    if(status[tempIndex]==START){
                        lockAndExe(abstractTask,workMap,tempIndex);
                    }
                }
            }
    }
}
