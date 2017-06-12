package nna.base.server;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;

/**
 * @author NNA-SHUAI
 * @create 2017-06-11 22:04
 **/

public class NIO {
    public static final int SERVER=0;
    public static final int CLIENT=1;
    private EndConfig[] endConfigs;
    private int type;
    private ServerSocketChannel[] serverSocketChannels;
    private Selector selector= SelectorProvider.provider().openSelector();

    public NIO(ServerConfig[] serverConfigs, int type) throws IOException {
        int count= serverConfigs.length;
        this.type=type;
        serverSocketChannels=new ServerSocketChannel[count];
    }

    public void buildInstance() throws IOException {
        int serverCount= endConfigs.length;
        EndConfig server;
        for(int index=0;index < serverCount;index++){
            server= endConfigs[index];
            ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            InetSocketAddress inetSocketAddress=new InetSocketAddress(server.getIp(),server.getPort());
            SocketOption[] options=server.getSocketOptions();
            int count=options.length;
            SocketOption temp;
            for(index=0;index < count;index++){
                temp=options[index];
                serverSocketChannel.setOption(temp,temp);
            }
            switch (type){
                case SERVER:
                    setServer(
                            serverSocketChannel,
                            (ServerConfig) server,
                            inetSocketAddress
                    );
                    break;
                case CLIENT:
                    setClient(
                            serverSocketChannel,
                            (ClientConfig) server,
                            inetSocketAddress
                    );
                    break;
            }
            serverSocketChannels[index]=serverSocketChannel;
        }
    }

    private void setClient(ServerSocketChannel serverSocketChannel, ClientConfig server, InetSocketAddress inetSocketAddress) throws IOException {
        serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT,server.getAttach());
        serverSocketChannel.bind(inetSocketAddress);
    }

    private void setServer(ServerSocketChannel serverSocketChannel,
                           ServerConfig serverConfig,
                           SocketAddress inetSocketAddress) throws IOException {
        serverSocketChannel.register(selector,SelectionKey.OP_CONNECT, serverConfig);
        serverSocketChannel.bind(inetSocketAddress,serverConfig.getBackLog());
    }

    public void listen(){
        try{
            while(true){
                selector.select();
                Set<SelectionKey> set=selector.keys();
                Iterator<SelectionKey> iterator=set.iterator();
                SelectionKey temp;
                while(iterator.hasNext()){
                    iterator.remove();
                    temp=iterator.next();
                    int selectionKey=temp.interestOps();
                    NIOTask nioTask = null;
                    SelectableChannel channel=temp.channel();
                    SocketChannel socketChannel;
                    switch (selectionKey){
                        case SelectionKey.OP_ACCEPT:
                            socketChannel=(SocketChannel) channel;
                            if(!socketChannel.isConnected()){
                                socketChannel.finishConnect();
                            }
                            nioTask=new NIOTask("OP_ACCEPT",channel,NIOTask.SERVICE_IN);
                            temp.attach(nioTask);
                            nioTask.submitInitEvent();
                            channel.register(selector,SelectionKey.OP_WRITE,nioTask);
                            break;
                        case SelectionKey.OP_READ:
                            nioTask=(NIOTask) temp.attachment();
                            nioTask.setTaskName("OP_READ");
                            nioTask.submitEvent();
                            if(nioTask.getTaskStatus()!=NIOTask.TASK_STATUS_DESTROY){
                                channel.register(selector,SelectionKey.OP_WRITE,nioTask);
                            }
                            break;
                        case SelectionKey.OP_WRITE:
                            nioTask=(NIOTask) temp.attachment();
                            nioTask.submitEvent();
                            nioTask.setTaskName("OP_WRITE");
                            if(nioTask.getTaskStatus()!=NIOTask.TASK_STATUS_DESTROY){
                                channel.register(selector,SelectionKey.OP_READ,nioTask);
                            }
                            break;
                        case SelectionKey.OP_CONNECT:
                            socketChannel=(SocketChannel) channel;
                            if(!socketChannel.isConnected()){
                                socketChannel.finishConnect();
                            }
                            nioTask=new NIOTask("OP_CONNECT",channel,NIOTask.SERVICE_OUT);
                            temp.attach(nioTask);
                            nioTask.submitInitEvent();
                            channel.register(selector,SelectionKey.OP_READ,nioTask);
                            break;
                    }
                    nioTask.setChannel(channel);
                }
            }
        }catch (Exception e){

        }finally {

        }
    }
}
