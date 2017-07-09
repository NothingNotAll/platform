package nna.base.util;

import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by NNA-SHUAI on 2017/7/8.
 */
public class ZerCopyList {
    private LinkedList<byte[]> segments=new LinkedList<byte[]>();
    private Integer segmentSize=1024;//1024 byte//
    private Integer currentIndex;
    private Integer currentSegmentIndex;

    public static void main(String[] args){
        int count=60*60*24*2;//172800
        LinkedBlockingQueue[] linkedBlockingQueues=new LinkedBlockingQueue[count];
        for(int index=0;index <count;index++){
            linkedBlockingQueues[index]=new LinkedBlockingQueue();
        }
        System.out.println(linkedBlockingQueues.length);
        Object o=new Object();
        synchronized (o){
            try {
                Thread.sleep(20000L);
                linkedBlockingQueues=null;
                o.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
