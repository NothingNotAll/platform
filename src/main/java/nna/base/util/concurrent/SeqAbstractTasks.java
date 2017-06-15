package nna.base.util.concurrent;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author NNA-SHUAI
 * @create 2017-06-15 11:34
 **/

public class SeqAbstractTasks extends AbstractTasks {
    SeqAbstractTasks(int taskCount) {
        super(taskCount);
    }

    ConcurrentHashMap<Integer,Integer> map=new ConcurrentHashMap<Integer, Integer>();

    protected AbstractTask doTasks(ConcurrentHashMap<Long, AbstractTasks> workMap) {
        AbstractTask abstractTask = null;
        int temp=enQueueIndex;//为了尽可能的照顾所有的 Tasks对象
        for(;workIndex < temp;workIndex++){
            Integer has=map.putIfAbsent(Integer.valueOf(workIndex),Integer.valueOf(workIndex));
            abstractTask=list[workIndex];
            if(has!=null){
//                System.out.println(abstractTask.getWorkId()+"--"+has);
            }
            // for 乐观锁 ; for performance
            if(status[workIndex]==START){
                lockAndExe(abstractTask,workMap,workIndex);
            }
            if(workIndex>=workCount-1){
                workMap.remove(abstractTask.getIndex());
                endTime=System.currentTimeMillis();
            }
        }
        return abstractTask;
    }

    public static void main(String[] args){
    }

}
