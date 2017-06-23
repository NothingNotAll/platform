package nna.base.server;

import nna.Marco;
import nna.base.util.conv2.AbstractTask;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.nio.channels.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * For NIOTask Of Abstract
 *
 * @author NNA-SHUAI
 * @create 2017-06-19 15:01
 **/

 abstract class AbstractNIOTask extends AbstractTask {
    private static final ReentrantLock initLock=new ReentrantLock();
    private static boolean isInit=false;
    protected int protocolType;
    protected EndConfig endConfig;
    protected Object object;
    protected Method method;
    protected Selector selector;
    protected InetSocketAddress socketAddress;

    public AbstractNIOTask(EndConfig endConfig,
                           Object object,
                           Method method) throws IOException {
        super("NIO_SERVER",50,15,Marco.NO_SEQ_LINKED_SIZE_TASK,Marco.CACHED_THREAD_TYPE);
        this.protocolType=endConfig.getProtocolType();
        this.object=object;
        this.method=method;
        this.endConfig=endConfig;
        String ip=endConfig.getIp();
        int port=endConfig.getPort();
        this.socketAddress=new InetSocketAddress(ip,port);
        register();
    }

    protected abstract void register() throws IOException;

    protected void setSocketOption(NetworkChannel networkChannel) throws IOException {
        SocketOption[] socketOptions=endConfig.getSocketOptions();
        Object[] objects=endConfig.getOptions();
        int count=socketOptions.length;
        SocketOption socketOption;
        Object object;
        for(int index=0;index < count;index++){
            socketOption=socketOptions[index];
            object= objects[index];
            try{
                networkChannel.setOption(socketOption,object);
            }catch (Exception e){
                System.out.println(" "+socketOption.name()+" channel not supported");
            }
        }
    }

     void addNewNIOTask(Object attach,int taskType){
        addNewTask(this,attach,taskType,false,null);
    }

    public InetSocketAddress getSocketAddress() {
        return socketAddress;
    }

    public void setSocketAddress(InetSocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    public Selector getSelector() {
        return selector;
    }

    public void setSelector(Selector selector) {
        this.selector = selector;
    }

    public int getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(int protocolType) {
        this.protocolType = protocolType;
    }
}
