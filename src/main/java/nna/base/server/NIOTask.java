package nna.base.server;

import nna.base.util.concurrent.AbstractTask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;

/**
 * not support for long IO event
 *
 * Client Mode: connect Event
 *              send reqStr;
 *              read rspStr;
 *              close;
 * Server Mode: listen Connect Event
 *              finish Connect and read Data and Write Data;
 *              close;
 * @author NNA-SHUAI
 * @create 2017-06-12 0:06
 **/

public class NIOTask extends AbstractTask {

    private static final int READ=0;
    private static final int WRITING=1;
    private ReadableByteChannel readChannel;
    private WritableByteChannel writeChannel;
    private InetSocketAddress clientSocket;

    public NIOTask(String taskName, int workCount) {
        super(taskName, workCount, true);
    }

    private Object read(Object attach){
        return null;
    }

    private Object write(Object attach){
        return null;
    }

    private Object close(Object attach){
        try {
            readChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            writeChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected Object doTask(int taskType, Object attach) {
        switch (taskType){
            case READ:
                read(attach);
                break;
            case WRITING:
                write(attach);
                break;
            case OVER:
                close(attach);
                break;
        }
        return null;
    }

}