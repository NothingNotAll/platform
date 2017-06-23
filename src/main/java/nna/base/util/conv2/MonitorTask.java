package nna.base.util.conv2;

import nna.Marco;
import nna.base.log.Log;

import java.util.Iterator;
import java.util.Map;

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
         String logName="INIT";
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
                        Thread.sleep(7000L);
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
        switch (abstractEnAndDeStgy.getStrategyType()){

        }
    }
}
