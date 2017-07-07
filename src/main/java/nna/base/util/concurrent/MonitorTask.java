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
    public static final ConcurrentHashMap<Long,AbstractTask> abstractTaskMonitorMap=new ConcurrentHashMap<Long, AbstractTask>();
     private Log log;

     static void addMonitor(AbstractTask abstractTask){
         abstractTaskMonitorMap.putIfAbsent(abstractTask.getgTaskId(),abstractTask);
     }

     static void removeMonitor(AbstractTask abstractTask){
         abstractTaskMonitorMap.remove(abstractTask.getgTaskId());
     }

     static {
//         MonitorTask monitorTask=new MonitorTask();
     }

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
    private ConcurrentHashMap<String,QueueWrapper[]> map=new ConcurrentHashMap<String, QueueWrapper[]>();
    public Object doTask(Object att, int taskType) {
            try{
                twMap=ThreadWrapper.getTwMap();
                map.putAll(QueueWrapper.getQwMap());
                while(true){
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

    ConcurrentHashMap<Long,ThreadWrapper> twMap;
    Iterator<Map.Entry<Long, ThreadWrapper>> ts;
    int count;
    BlockingQueue temp;
    private void monitor(String clazzNm,QueueWrapper[] qws) {
         ts = twMap.entrySet().iterator();
        Long index;
        try{
            while(ts.hasNext()){
                Map.Entry<Long,ThreadWrapper> entry=ts.next();
                ThreadWrapper t=entry.getValue();
                index=entry.getKey();
                System.out.println(t.getThread().getState());
                log.log("No."+index+":"+t.getThread().getState(), Log.INFO);
            }
        }catch (Exception e){
            e.fillInStackTrace();
        }
        String str1="GLOBAL TASK ID:"+clazzNm;
        log.log(str1, Log.INFO);
        System.out.println(str1);
        AbstractTask abstractTask=abstractTaskMonitorMap.get(clazzNm);
        if(abstractTask!=null){
            String str2="GLOBAL TASK NAME:"+abstractTask.getTaskName();
            System.out.println(str2);
            log.log(str2, Log.INFO);
        }
        int index2=0;
        count=qws.length;
        for(;index2 < count;index2++){
            temp= qws[index2].getQueue();
            log.log("consumered:"+qws[index2].getDeCount()+":"+temp.size(), Log.INFO);
            System.out.println("consumered:"+temp.size());
            log.log("No."+index2+":"+temp.size(), Log.INFO);
        }
    }
}
