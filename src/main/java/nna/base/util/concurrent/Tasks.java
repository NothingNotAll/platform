package nna.base.util.concurrent;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author NNA-SHUAI
 * @create 2017-06-13 13:44
 **/



 class Tasks{
     private volatile AbstractTask[] list;//for 有序的 task
     private volatile Object[] objects;//oom-limit : a large of
    // waste memory with null slot except that task is been worked with short time
     private volatile Integer enQueueIndex;
     private volatile Integer workIndex;
     private Integer workCount;
     private volatile Integer beenWorkedCount;
     private AtomicInteger sequenceGen=new AtomicInteger();
     private boolean isTaskEnQueueBySeq;//任务队列工作是否按照数组顺序工作

     Tasks(int taskCount,boolean keepTaskSeq){
        enQueueIndex=0;
        workCount=taskCount;
        workIndex=0;
        beenWorkedCount=0;
        this.isTaskEnQueueBySeq=keepTaskSeq;
        list=new AbstractTask[taskCount];
        objects=new Object[taskCount];
    }


    void works(ConcurrentHashMap<Long,Tasks> workMap) throws IOException {
        AbstractTask abstractTask;
        Object attach;
        int temp=enQueueIndex;
        for(;workIndex < temp;workIndex++){
            abstractTask=list[workIndex];
            attach=objects[workIndex];
            if(abstractTask==null){
                break;
            }
            work(abstractTask,attach,workMap,workIndex);
        }
        int tempIndex=workIndex;
        if(!isTaskEnQueueBySeq){
            for(;tempIndex<workCount;tempIndex++){
                abstractTask=list[tempIndex];
                if(abstractTask!=null){
                    attach=objects[tempIndex];
                    work(abstractTask,attach,workMap,tempIndex);
                }
            }
        }
    }

    private void setNull(Integer workIndex) {
        list[workIndex]=null;
        objects[workIndex]=null;
    }

    private void work(AbstractTask abstractTask,Object attach,ConcurrentHashMap<Long,Tasks> workMap,int index) throws IOException {
        int taskStatus=abstractTask.getTaskStatus();
        switch (taskStatus){
            case AbstractTask.TASK_STATUS_DESTROY:
                abstractTask.destroy(attach);
                workMap.remove(abstractTask.getIndex());
                break;
            case AbstractTask.TASK_STATUS_WORK:
                abstractTask.work(attach);
                break;
            case AbstractTask.TASK_STATUS_INIT:
                abstractTask.init(attach);
                break;
            default:
                abstractTask.otherWork(attach);
        }
        setNull(index);
        beenWorkedCount++;
    }

    void addTask(AbstractTask abstractTask,Object attach){
        int seq=sequenceGen.getAndIncrement();
        seq=enQueueIndex++;//并发策略失当
        list[seq]=abstractTask;//一定可以保证有序，当前只有业务线程来处理
        objects[seq]=attach;
    }

    public AbstractTask[] getList() {
        return list;
    }

    public void setList(AbstractTask[] list) {
        this.list = list;
    }

    public Integer getBeenWorkedCount() {
        return beenWorkedCount;
    }

    public void setBeenWorkedCount(Integer beenWorkedCount) {
        this.beenWorkedCount = beenWorkedCount;
    }
}
