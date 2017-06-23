package nna.base.util.conv2;

import nna.Marco;
import nna.base.util.concurrent.Monitor;

import java.util.Iterator;
import java.util.Map;

/**
 * @author NNA-SHUAI
 * @create 2017-06-23 9:22
 **/

 class MonitorTask extends AbstractTask {

     private Map map;

     MonitorTask(Map monitorMap) {
        super(1,1,Marco.SEQ_LINKED_SIZE_TASK,7000L);
        this.map=monitorMap;
    }

    AbstractTask abstractTask;
    Iterator<AbstractTask> iterator;
    public Object doTask(Object att, int taskType) {
        iterator=map.entrySet().iterator();
        while(iterator.hasNext()){
            abstractTask=iterator.next();
            monitor(abstractTask);
        }
        return null;
    }

    AbstractEnAndDeStgy abstractEnAndDeStgy;
    private void monitor(AbstractTask abstractTask) {
        abstractEnAndDeStgy=abstractTask.getAbstractEnAndDeStgy();
        switch (abstractEnAndDeStgy.getStrategyType()){

        }
    }
}
