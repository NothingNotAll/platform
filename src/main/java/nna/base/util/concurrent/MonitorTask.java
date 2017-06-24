package nna.base.util.concurrent;

import nna.Marco;
import nna.base.log.Log;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * @author NNA-SHUAI
 * @create 2017-06-23 9:22
 **/

 class MonitorTask extends AbstractTask {

     private Map map;
     private Log log;

     MonitorTask(Map monitorMap)  {
        super("MONITOR",1,1,Marco.SEQ_LINKED_SIZE_TASK,7000L);
        this.map=monitorMap;
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
     }

    AbstractTask abstractTask;
    Iterator<Map.Entry<Long,AbstractTask>> iterator;
    public Object doTask(Object att, int taskType) {
            try{
                while(true){
                    try{
                        iterator=map.entrySet().iterator();
                        while(iterator.hasNext()){
                            Map.Entry<Long,AbstractTask> entry=iterator.next();
                            abstractTask=entry.getValue();
                            monitor(abstractTask);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
    }

    AbstractEnAndDeStgy abstractEnAndDeStgy;
    private void monitor(AbstractTask abstractTask) {
        String str1="GLOBAL TASK ID:"+abstractTask.getgTaskId();
        System.out.println(str1);
        log.log(str1, Log.INFO);
        String str2="GLOBAL TASK NAME:"+abstractTask.getTaskName();
        System.out.println(str2);
        log.log(str2, Log.INFO);
        abstractEnAndDeStgy=abstractTask.getAbstractEnAndDeStgy();
        if(abstractEnAndDeStgy.getStrategyType()==Marco.NO_SEQ_LINKED_SIZE_TASK){
            if(abstractTask.getTaskName().startsWith("NIO_SERVER")){
                System.out.println(abstractTask.getTaskName());
                Thread[] ts=abstractEnAndDeStgy.getTs();
                Object[] os=abstractEnAndDeStgy.getQueues();
                int count=os.length;
                int index=0;
                for(Thread t:ts){
                    System.out.println(t.getState());
                    log.log("No."+index+":"+t.getState(), Log.INFO);
                    index++;
                }
                index=0;
                for(;index < count;index++){
                    BlockingQueue temp=(BlockingQueue) os[index];
                    System.out.println(temp.size());
                    log.log("No."+index+":"+temp.size(), Log.INFO);
                }
            }
        }
    }
}
