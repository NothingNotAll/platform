package nna.base.server;

import nna.base.util.concurrent.AbstractTask;

import java.io.IOException;
import java.nio.channels.Channel;

/**
 * @author NNA-SHUAI
 * @create 2017-06-12 0:06
 **/

public class NIOTask extends AbstractTask {
    public static final int SERVICE_IN=0;//接入其它渠道的服务
    public static final int SERVICE_OUT=1;//接出本平台的服务；

    public static final int READ=4;
    public static final int WRITE=5;

    private int serviceType;
    private Channel channel;

    public NIOTask(String taskName,
                   Channel channel,
                   int serviceType){
        super(taskName);
        this.channel=channel;
        this.serviceType=serviceType;
    }

    public void create() {
        switch (serviceType){
            case SERVICE_IN:
                setTaskStatus(WRITE);
                break;
            case SERVICE_OUT:
                setTaskStatus(READ);
                break;
        }
        submitEvent();
    }

    public void init() {

    }

    public void work() {

    }

    public void destroy() throws IOException {
        channel.close();
    }

    public void otherWork() {
        switch (getTaskStatus()){
            case READ:
                doReadWork();
                break;
            case WRITE:
                doWriteWork();
                break;
        }
        submitEvent();
    }

    private void doReadWork() {
        switch (serviceType){
            case SERVICE_IN:

                setTaskStatus(TASK_STATUS_DESTROY);
                break;
            case SERVICE_OUT:

                setTaskStatus(WRITE);
                break;
        }
    }

    private void  doWriteWork(){
        switch (serviceType){
            case SERVICE_IN:

                setTaskStatus(READ);
                break;
            case SERVICE_OUT:

                setTaskStatus(TASK_STATUS_DESTROY);
                break;
        }
    }



    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public int getServiceType() {
        return serviceType;
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }
}
