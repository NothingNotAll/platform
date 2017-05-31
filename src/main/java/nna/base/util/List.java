package nna.base.util;
/**
 *
 * Created by NNA-SHUAI on 2017/5/12.
 */


import java.util.concurrent.locks.ReentrantLock;

/**
 * for performance concurrent
 * @author NNA-SHUAI
 * @create 2017-05-13 15:56
 **/
//Only for one insert thread and one update thread;
 public class List<T> {
    public static final Object object=new Object();
    private volatile Object[] container;
    private ReentrantLock[] locks;
    private volatile int capacity=0;

    public List(int initSize){
         init(initSize);
    }

    /**
     *
     * @param initSize the capacity of the container
     * @return
     */
    private void init(int initSize){
        assert  initSize>0;
        container=new Object[initSize];
        locks=new ReentrantLock[initSize];
        for(int index=0;index < initSize;index++){
            locks[index]=new ReentrantLock();
            container[index]=null;
        }
        capacity=initSize;
    }

    //this position must be null;
    private T setNonNull(int index,T t){
        T oldT=(T)container[index];
        container[index]=t;
        return oldT;
    }

    //this position must not be null;
    private T setNull(int index){
        T t=(T)container[index];
        container[index]=null;
        return t;
    }

    public boolean insert(T t,int tryTime){
        if(t==null){
            return false;
        }
        int nextIndex=0;
        int tryCount=0;
        while(true){
            if(tryCount >= tryTime){
                return false;
            }
            ReentrantLock lock=locks[nextIndex];
            int lockStatus=0;
                try{
                    if(lock.tryLock()){
                        lockStatus=1;
                        if(container[nextIndex]!=null){
                            continue;
                        }
                        setNonNull(nextIndex,t);
                        return true;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    continue;
                }finally {
                    nextIndex=++nextIndex==capacity?0:nextIndex;
                    if(lockStatus==1){
                        lock.unlock();
                    }
                }
                tryCount++;
        }
    }

    public T delete(int index){
        T t;
        if(index>=capacity){
            return null;
        }

        ReentrantLock lock=locks[index];
        try{
            lock.lock();
            t=setNull(index);
        }catch (Exception e){
            return null;
        }finally {
            lock.unlock();
        }
        return t;
    }

    /**
     * @param index
     * @param<T> t
     * @return
     */
    public T update(int index,T t){
        T oldT;
        if(index >= capacity){
            return null;
        }
        ReentrantLock lock=locks[index];
        try{
            lock.lock();
            if(t==null){
                oldT=setNull(index);
            }else{
                oldT=setNonNull(index,t);
            }
        }catch (Exception e){
            return null;
        }finally {
            lock.unlock();
        }
        return oldT;
    }

    /**
     *not enseure that  not to be null
     * @param index
     * @return<T>
     */
    public T get(int index){
        if(index >= capacity){
            return null;
        }
        return (T)container[index];
    }

    public int getCapacity() {
        return capacity;
    }

    public Object[] getContainer() {
        return container;
    }

    public void setContainer(Object[] container) {
        this.container = container;
    }

    public ReentrantLock[] getLocks() {
        return locks;
    }

    public void setLocks(ReentrantLock[] locks) {
        this.locks = locks;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

}
