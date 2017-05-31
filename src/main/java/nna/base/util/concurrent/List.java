package nna.base.util.concurrent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author NNA-SHUAI
 * @create 2017-05-26 8:52
 **/

public class List<T extends AbstractTask> {
    public static final Object object=new Object();
    private volatile Object[] list;
    private ReentrantLock[] locks;
    private volatile int[] tStatus;
    private volatile int capacity=0;
    private int hashcode;
    private ConcurrentHashMap<Long,Integer> threadIndex;

    public List(int size){
        hashcode=size-1;
        locks=new ReentrantLock[size];
        for(int index=0;index < size;index++){
            locks[index]=new ReentrantLock();
        }
        list=new Object[size];
        tStatus=new int[size];
        capacity=size;
        threadIndex=new ConcurrentHashMap<Long, Integer>(size);
    }

    public int insert(T t,Long threadId){
        int index=hashIndex(threadId);
        insert(t,index);
        t.setThreadId(threadId);
        return index;
    }

    public int insert(T t){
        int index=nextIndex();
        insert(t,index);
        return index;
    }

    public void insert(T t,int index){
        checkArrayOutOfIndex(index);
        update(t,index);
        t.setIndex(index);
    }

    private int nextIndex(){
        long id=Thread.currentThread().getId();
        return hashIndex(id);
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
                    hash=hash++>=capacity?0:hash;
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

    public T get(int index){
        checkArrayOutOfIndex(index);
        return (T)list[index];
    }

    public T get(Long threadId){
        Integer index=threadIndex.get(threadId);
        checkArrayOutOfIndex(index);
        return (T)list[index];
    }

    public T update(T t){
        int index=getIndex();
        return update(t,index);
    }

    public T delete(Long threadId){
        int index=threadIndex.get(threadId);
        checkArrayOutOfIndex(index);
        return delete(index);
    }

    public T delete(int index){
        return update(null,index);
    }

    public T update(T t,Thread thread){
        Long threadId=thread.getId();
        String threadName=thread.getName();
        t.setThreadId(threadId);
        t.setThreadName(threadName);
        t.setThread(thread);
        int index=threadIndex.get(threadId);
        return update(t,index);
    }

    public T update(T t,int index){
        ReentrantLock lock=locks[index];
        T oldT=null;
        try{
            lock.lock();
            oldT=(T)list[index];
            list[index]=t;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        return oldT;
    }

    private int getIndex(){
        Long threadId=Thread.currentThread().getId();
        return threadIndex.get(threadId);
    }

    private void checkArrayOutOfIndex(int index){
        if(index >= capacity){
            throw new ArrayIndexOutOfBoundsException();
        }
    }

}
