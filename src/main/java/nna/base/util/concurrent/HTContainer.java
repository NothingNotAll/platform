package nna.base.util.concurrent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * high throughput; VHT; high-throughput;
 高性能市场数据分发：提供高吞吐量和路由功能。
 no block get consumer data
 * @author NNA-SHUAI
 * @create 2017-06-24 17:29
 **/

public class HTContainer<T> {
    private AtomicInteger takeIndex=new AtomicInteger();
    private AtomicInteger addIndex=new AtomicInteger();
    private Node head;
    private Node tail;
    private AtomicInteger lockIdGen=new AtomicInteger();
    private HashMap<Integer,InnerLock> foodContainer=new HashMap<Integer, InnerLock>();

    private class Node{
        private Node prev;
        private Node next;
    }

    private class InnerLock{
        private LinkedList<T> temp=new LinkedList<T>();
        private volatile Boolean canWrite=true;
        private ReentrantLock lock=new ReentrantLock();
        private Integer segmentIndex;

        LinkedList<T> consumer(){
            boolean locked=false;
            try{
                if(lock.tryLock()){
                    locked=true;
                    if(canWrite){
                        return null;
                    }else{
                        canWrite=true;
                        LinkedList<T> foods=new LinkedList<T>(temp);
                        return foods;
                    }
                }else{
                    return null;
                }
            }catch (Exception e){
                e.fillInStackTrace();
                return null;
            }finally {
                if(locked){
                    lock.unlock();
                }
            }
        }
        boolean produce(Integer segmentIndex,Object[] foods){
            boolean locked=false;
            try{
                if(lock.tryLock()){
                        locked=true;
                        if(canWrite){
                            canWrite=false;
                            for(Object food:foods){
                                temp.add((T)food);
                            }
                            this.segmentIndex=segmentIndex;
                            return true;
                    }else{
                        return false;
                    }
                }else{
                    return false;
                }
            }catch (Exception e){
                e.fillInStackTrace();
                return false;
            }finally {
                if(locked){
                    lock.unlock();
                }
            }
        }
    }
}
