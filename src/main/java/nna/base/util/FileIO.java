package nna.base.util;

import nna.base.util.concurrent.AbstractTask;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author NNA-SHUAI
 * @create 2017-06-16 18:32
 **/

public class FileIO extends AbstractTask{
    public static final int READ=0;
    public static final int WRITE=1;
    private OutputStream writer;
    private Object object=new Object();

    public FileIO(String taskName, int workCount) {
        super(taskName, workCount,true);
        synchronized (object){
            try {
                byte[] bytes=null;
                submitEvent(bytes, READ);
                getThread().wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected Object doTask(int taskType, Object attach) {
        switch (taskType){
            case READ:
                read(attach);
                break;
            case OVER:
                destroy(attach);
                break;
            case WRITE:
                write(attach);
                break;
        }
        return null;
    }

    private void write(Object attach) {
        try {
            writer.write((byte[])attach);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void destroy(Object attach) {
        synchronized (object){
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            getThread().notify();
        }
    }

    private void read(Object attach) {
        byte[] bytes=null;
        if(bytes==null){
            submitEvent(null,OVER);
        }else{
            submitEvent(bytes,WRITE);
        }
    }

    public OutputStream getWriter() {
        return writer;
    }

    public void setWriter(OutputStream writer) {
        this.writer = writer;
    }
}
