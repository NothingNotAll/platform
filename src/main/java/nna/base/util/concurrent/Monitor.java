package nna.base.util.concurrent;

import nna.Marco;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author NNA-SHUAI
 * @create 2017-06-15 21:53
 **/

public class Monitor extends AbstractTask {
    TaskScheduleManager taskScheduleManager;
    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMdd-HH:mm:ss:SSS");
    private Map<Long,AbstractTasks> map;
    Iterator<Map.Entry<Long,AbstractTasks>> iterator;
    Map.Entry<Long,AbstractTasks> entry;
    AbstractTasks abstractTasks;


    public Monitor(TaskScheduleManager taskScheduleManager) {
        super( 1);
        this.taskScheduleManager=taskScheduleManager;
        this.map=taskScheduleManager.getMonitorMap();
        startTask(null, Marco.SEQ_FIX_SIZE_TASK,"[Concurrent Container Monitor]");
    }

    public void run() {
        while(true){
            try{
                System.out.println(simpleDateFormat.format(System.currentTimeMillis())+"-TOTAL TASKS'S WAIT SIZE:"+map.size());
            }catch (Exception e){
                e.printStackTrace();
            }
            iterator=map.entrySet().iterator();
            while(iterator.hasNext()){
                entry=iterator.next();
                monitor(entry);
            }
            try {
                Thread.currentThread().sleep(7000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void monitor(Map.Entry<Long, AbstractTasks> entry) {
        Long workId=entry.getKey();
        abstractTasks=entry.getValue();
        monitor(workId,abstractTasks);
    }

    private SeqFixSizeTasks seqFixSizeTasks;
    private SeqLinkedTasks seqLinkedTasks;
    private NoSeqLinkedTasks noSeqLinkedTasks;
    private NoSeqFixSizeTasks noSeqFixSizeTasks;
//    private OneTask oneTask;
    private void monitor(Long workId,AbstractTasks abstractTasks){

        System.out.println(simpleDateFormat.format(System.currentTimeMillis())+"-TASKS:"+workId);
        System.out.println("Start Time:"+simpleDateFormat.format(abstractTasks.getStartTime()));
        System.out.println("work count:"+abstractTasks.getWorkCount());
        System.out.println("current workIndex:"+abstractTasks.getCounter().get());
        System.out.println("task priorLevel:"+abstractTasks.getPriorLevel());
        System.out.println(abstractTasks.getTaskName());
        if(abstractTasks instanceof SeqFixSizeTasks){
            seqFixSizeTasks=(SeqFixSizeTasks) abstractTasks;
            monitor(seqFixSizeTasks);
        }
        if(abstractTasks instanceof SeqLinkedTasks){
            seqLinkedTasks=(SeqLinkedTasks) abstractTasks;
            monitor(seqLinkedTasks);
        }
        if(abstractTasks instanceof NoSeqLinkedTasks){
            noSeqLinkedTasks=(NoSeqLinkedTasks) abstractTasks;
            monitor(noSeqLinkedTasks);
        }
        if(abstractTasks instanceof NoSeqFixSizeTasks){
            noSeqFixSizeTasks=(NoSeqFixSizeTasks) abstractTasks;
            monitor(noSeqFixSizeTasks);
        }
//        if(abstractTasks instanceof OneTask){
//            oneTask=(OneTask) abstractTasks;
//            monitor(oneTask);
//        }
    }

    private void monitor(NoSeqFixSizeTasks noSeqFixSizeTasks){
        System.out.println("NoSeqFixSizeTasks");
    }

    private void monitor(SeqFixSizeTasks seqFixSizeTasks){
        System.out.println("SeqFixSizeTasks");
    }

//    private void monitor(OneTask oneTask){
//        System.out.println("OneTask");
//    }

    private void monitor(NoSeqLinkedTasks noSeqLinkedTasks){
        System.out.println("NoSeqLinkedTasks");
        ReentrantLock[] locks=noSeqLinkedTasks.getLocks();
        ReentrantLock lock;
        int count=locks.length;
        for(int index=0;index < count;index++){
            lock=locks[index];
            System.out.println("getQueueLength:"+lock.getQueueLength());
            System.out.println("getHoldCount:"+lock.getHoldCount());
        }
        LinkedBlockingQueue[] list=noSeqLinkedTasks.getList();
        LinkedBlockingQueue temp;
        int length=list.length;
        for(int index=0;index < length;index++){
            temp=list[index];
            System.out.println("NO."+index+"-LinkedBlockingQueue'size:"+temp.size());
        }
        Thread[] threads=noSeqLinkedTasks.getThreads();
        int tCount=threads.length;
        Thread tThread;
        for(int index=0;index < tCount;index++){
            tThread=threads[index];
            System.out.println("exe NO."+index+" thread status:"+tThread.getState().toString());
        }
    }

    private void monitor(SeqLinkedTasks seqLinkedTasks){
        System.out.println("SeqLinkedTasks");
        LinkedBlockingQueue linkedBlockingQueue=seqLinkedTasks.getAbstractsTaskWrappers();
        System.out.println("task's count:"+linkedBlockingQueue.size());
    }

    protected Object doTask(int taskType, Object attach) throws Exception {
        run();
        return null;
    }
}
