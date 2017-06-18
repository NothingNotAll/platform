package nna.base.util.concurrent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * the real io worker
 * @author NNA-SHUAI
 * @create 2017-06-18 13:21
 **/

public class IOTaskProcessor {
//    private ConcurrentHashMap<Long,IOTaskWorker> taskMap=new ConcurrentHashMap<Long, IOTaskWorker>();
    private ExecutorService ioTaskThreads= Executors.newCachedThreadPool();

    public void submitIOWork(AbstractIOTasks abstractIOTasks){
//        Long workId=abstractIOTasks.getGlobalWorkId();
//        IOTaskWorker worker=new IOTaskWorker(abstractIOTasks);
//        IOTaskWorker temp=taskMap.putIfAbsent(workId,worker);
//        if(temp!=null){
//            worker=temp;
//        }
        ioTaskThreads.submit(abstractIOTasks);
    }
}
