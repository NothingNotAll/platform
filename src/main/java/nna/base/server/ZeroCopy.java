package nna.base.server;


/**
 * @author NNA-SHUAI
 * @create 2017-06-12 15:51
 **/

public class ZeroCopy {
    private byte[][] bytes;
    private int blockCount;
    private int arrayCount;
    private int capacity;
    private int writeCount=0;
    private int currentBlockIndex=0;
    private int currentArrayIndex=0;

    public ZeroCopy(int blockCount,int arrayCount){
        this.blockCount=blockCount;
        this.arrayCount=arrayCount;
        capacity=blockCount*arrayCount;
        bytes=new byte[blockCount][arrayCount];
    }

    public ZeroCopy(){
        this(10,10);
    }

    public void writeBytes(byte[] writes){
        int needCount=writes.length;
        int canWriteCount=capacity-writeCount;
        if(canWriteCount < needCount){
            grow(needCount,canWriteCount);
        }
        int size=0;
        for(;currentArrayIndex < arrayCount;currentArrayIndex++){
            bytes[currentBlockIndex][currentArrayIndex]=writes[size++];
            if(size==needCount){
                writeCount+=needCount;
                return;
            }
        }
        currentBlockIndex++;
        for(;currentBlockIndex<blockCount;currentBlockIndex++){
            for(int index=0;index < arrayCount;index++){
                bytes[currentBlockIndex][index]=writes[size++];
                if(size==needCount){
                    currentArrayIndex=index;
                    writeCount+=needCount;
                    return;
                }
            }
        }
    }

    private void grow(int needCount,int canWriteCount) {
        int growCount=needCount-canWriteCount;
        int growBlockCount=growCount/arrayCount;
        int tempCurrentIndex=growCount%arrayCount;
        growBlockCount=tempCurrentIndex==0?growBlockCount:growBlockCount+1;
        int tempBlockCount=blockCount;
        blockCount+=growBlockCount;
        byte[][] newBytes=new byte[blockCount][arrayCount];
        for(int index=0;index<tempBlockCount;index++){
            newBytes[index]=bytes[index];
        }
        for(int index=tempBlockCount;index < blockCount;index++){
            newBytes[index]=new byte[arrayCount];
        }
        capacity+=growBlockCount*arrayCount;
        bytes=newBytes;
    }

    public byte[] toBytes(){
        byte[] byteArray=new byte[writeCount];
        int size=0;
        int arraySize=0;
        for(int index=0;index <= currentBlockIndex;index++){
            if(index==currentArrayIndex){
                arraySize=arrayCount-1;
            }else{
                arraySize=currentArrayIndex;
            }
            for(int arrayIndex=0;arrayIndex<=arraySize;arrayIndex++){
                byteArray[size++]=bytes[index][arrayIndex];
            }
        }
        return byteArray;
    }

    public byte[][] getBytes() {
        return bytes;
    }

    public void setBytes(byte[][] bytes) {
        this.bytes = bytes;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCurrentBlockIndex() {
        return currentBlockIndex;
    }

    public void setCurrentBlockIndex(int currentBlockIndex) {
        this.currentBlockIndex = currentBlockIndex;
    }

    public int getCurrentArrayIndex() {
        return currentArrayIndex;
    }

    public void setCurrentArrayIndex(int currentArrayIndex) {
        this.currentArrayIndex = currentArrayIndex;
    }

    public int getWriteCount() {
        return writeCount;
    }

    public void setWriteCount(int writeCount) {
        this.writeCount = writeCount;
    }

    public int getBlockCount() {
        return blockCount;
    }

    public void setBlockCount(int blockCount) {
        this.blockCount = blockCount;
    }
}
