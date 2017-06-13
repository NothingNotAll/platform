package nna.base.util.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author NNA-SHUAI
 * @create 2017-06-13 13:44
 **/



 class Tasks{
     volatile AbstractTask[] list;//for 有序的 task
     volatile Object[] objects;
     volatile Object initObject;
     volatile Integer currentWorkIndex;
     Integer workCount;
     Integer beenWorkedCount;
     AtomicInteger sequenceGen=new AtomicInteger();
     boolean keepTaskSeq;

     Tasks(
            int taskCount,
            boolean keepTaskSeq
    ){
        currentWorkIndex=-1;
        workCount=taskCount;
        this.keepTaskSeq=keepTaskSeq;
        list=new AbstractTask[taskCount];
        objects=new Object[taskCount];
    }

    private void insert(AbstractTask abstractTask,Object object){
        Integer no=sequenceGen.getAndIncrement();
        list[no]=abstractTask;
        objects[no]=object;
    }
}
