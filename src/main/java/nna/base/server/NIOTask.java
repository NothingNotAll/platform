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
        super(taskName,null);
        this.channel=channel;
        this.serviceType=serviceType;
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

    public Object init(Object object) {
        return null;
    }

    public Object work(Object object) {
        return null;
    }

    public Object otherWork(Object object) {
        return null;
    }

    public Object destroy(Object object) throws IOException {
        return null;
    }
}
