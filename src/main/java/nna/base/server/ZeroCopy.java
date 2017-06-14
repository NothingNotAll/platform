package nna.base.server;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author NNA-SHUAI
 * @create 2017-06-14 19:52
 **/

 class ZeroCopy {
    private byte[] bytes;
    private int length;
    private int currentIndex;
    private int reSizeLimit=10;
    private int incrementSize=10;

    public ZeroCopy(
            int length,
            int reSizeLimit,
            int incrementSize){
        this.length=length;
        currentIndex=0;
        this.reSizeLimit=reSizeLimit;
        this.incrementSize=incrementSize;
    }

     void add(byte t){
        ensureCapacity();
        bytes[++currentIndex]=t;
    }

     void add(byte[] ts){
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
        byte[] newBytes=new byte[newLength];
        for(int index=0;index<= currentIndex;index++){
            newBytes[index]=bytes[index];
        }
        bytes=newBytes;
        length=newLength;
    }

     byte[] toBytes() {
        byte[] finalBytes=new byte[currentIndex+1];
        for(int index=0;index <= currentIndex;index++){
            finalBytes[index]=bytes[index];
        }
        return finalBytes;
    }
}
