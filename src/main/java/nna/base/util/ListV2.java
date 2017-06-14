package nna.base.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author NNA-SHUAI
 * @create 2017-06-13 18:24
 **/

public class ListV2<T> {
    private Object[] objects;
    private ReentrantLock[] locks;
    private volatile int[] tStatus;
    private volatile int length;
    private int currentIndex;
    private int reSizeLimit=10;
    private int incrementSize=10;
    private ConcurrentHashMap<Object,Integer> threadIndex;
    private int hashcode;

    public ListV2(
            int length,
            int reSizeLimit,
            int incrementSize){
        this.length=length;
        currentIndex=0;
        this.reSizeLimit=reSizeLimit;
        this.incrementSize=incrementSize;
        objects=new Object[length];
        locks=new ReentrantLock[length];
        for(int index=0;index<length;index++){
            locks[index]=new ReentrantLock();
        }
    }

    public void add(T t){
        ensureCapacity();
        objects[++currentIndex]=t;
    }

    public void add(T[] ts){
        int size=ts.length;
        for(int index=0;index<size;index++){
            add(ts[index]);
        }
    }

    private void ensureCapacity() {
        int leftCount=length-currentIndex-1;
        if(leftCount < reSizeLimit){
            dilatation(incrementSize);
        }
    }

    private void dilatation(int incrementSize){
        int newLength=length+incrementSize;
        Object[] newObjects=new Object[newLength];
        ReentrantLock[] newLocks=new ReentrantLock[newLength];
        for(int index=0;index<= currentIndex;index++){
            newObjects[index]=objects[index];
            newLocks[index]=locks[index];
        }
        int tempIndex=currentIndex;
        for(;tempIndex<newLength;tempIndex++){
            newLocks[tempIndex]=new ReentrantLock();
        }
        objects=newObjects;
        locks=newLocks;
        length=newLength;
    }

    private int hashIndex(Long threadId) {
        long id=threadId;
        int lowBits=(int)id;
        id=id >>> hashcode;
        int hightBits=(int)id;
        int hash=lowBits;
        switch (hightBits){
            case 0:
                break;
            default:
                hash=lowBits+1;
        }
        ReentrantLock lock=null;
        while(true){
            try{
                lock=locks[hash];
                lock.lock();
                if(tStatus[hash]==1){
                    hash=hash++>=length?0:hash;
                    continue;
                }
                tStatus[hash]=1;
                threadIndex.put(id,hash);
                return hash;
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        }
    }
}
