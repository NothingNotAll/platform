package nna.base.util.concurrent;

import nna.base.log.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author NNA-SHUAI
 * @create 2017-06-23 9:22
 **/

 class MonitorTask extends AbstractTask {
    private Log log;

    MonitorTask()  {
        super(false);
         String logPath="LOG";
         String logName="MONITOR";
         try {
             log = Log.getLog(
                     logPath,
                     logName,
                     -1000,
                     0,
                     -1,
                     "UTF-8");
         } catch (Exception e) {
             e.printStackTrace();
         }
         addNewTask(this,null,INIT_TASK_TYPE,true,null);
     }

    //for minus gc
    private QueueWrapper[] qws;
    private String clazzNm;
    private Iterator<Map.Entry<String,QueueWrapper[]>> iterator;
    private ConcurrentHashMap<String,QueueWrapper[]> map;
    private ConcurrentHashMap<Long,ThreadWrapper> twMap;
    private Iterator<Map.Entry<Long, ThreadWrapper>> ts;
    private String monitrStr;
    public Object doTask(Object att, int taskType) {
        map=QueueWrapper.getQwMap();
            try{
                while(true){
                    System.out.println("Alive thread Count:"+Thread.getAllStackTraces().size());
                    log.log("Alive thread Count:"+Thread.getAllStackTraces().size(),Log.INFO);
                    System.out.println("total count of alive thread:"+AbstractTask.getTotalAliveThreadCount());
                    log.log("total count of alive thread:"+AbstractTask.getTotalAliveThreadCount(),Log.INFO);
                    System.out.println("total count of dead thread:"+AbstractTask.getTotalDeadThreadCount());
                    log.log("total count of dead thread:"+AbstractTask.getTotalDeadThreadCount(),Log.INFO);
                    System.out.println("total count of in real alive thread:"+AbstractTask.getAliveThreadCount());
                    log.log("total count of in real alive thread:"+AbstractTask.getAliveThreadCount(),Log.INFO);
                    twMap=ThreadWrapper.getTwMap();
                    ts = twMap.entrySet().iterator();
                    try{
                        while(ts.hasNext()){
                            Map.Entry<Long,ThreadWrapper> entry=ts.next();
                            ThreadWrapper t=entry.getValue();
                            monitrStr=ThreadWrapper.monitor(t);
                            log.log(monitrStr,Log.INFO);
                            System.out.println(monitrStr);
                        }
                    }catch (Exception e){
                        e.fillInStackTrace();
                    }
                    try{
                        iterator=map.entrySet().iterator();
                        while(iterator.hasNext()){
                            Map.Entry<String,QueueWrapper[]> entry=iterator.next();
                            qws=entry.getValue();
                            clazzNm=entry.getKey();
                            monitor(clazzNm,qws);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(10000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
    }

    private BlockingQueue temp;
    private int count;
    private void monitor(String clazzNm,QueueWrapper[] qws) {
        String str1="GLOBAL TASK ID:"+clazzNm;
        log.log(str1, Log.INFO);
        System.out.println(str1);
        count=qws.length;
        for(int index2=0;index2 < count;index2++){
            temp= qws[index2].getQueue();
            log.log(clazzNm+" has been consumered:"+qws[index2].getDeCount(), Log.INFO);
            System.out.println(clazzNm+" has been consumered:"+qws[index2].getDeCount()+" left count of task:"+temp.size());
            log.log("No."+index2+":"+temp.size(), Log.INFO);
        }
    }
}
