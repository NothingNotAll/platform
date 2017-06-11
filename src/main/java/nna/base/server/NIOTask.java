package nna.base.server;

import nna.base.util.concurrent.AbstractTask;

import java.nio.channels.Channel;

/**
 * @author NNA-SHUAI
 * @create 2017-06-12 0:06
 **/

public class NIOTask extends AbstractTask {

    private Channel channel;

    public NIOTask(String taskName,Channel channel){
        super(taskName);
        this.channel=channel;
    }

    public void create() {

    }

    public void init() {

    }

    public void work() {

    }

    public void destroy() {

    }

    public void otherWork() {

    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
