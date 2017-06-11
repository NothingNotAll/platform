package nna.base.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;

/**
 * @author NNA-SHUAI
 * @create 2017-06-11 22:04
 **/

public class NIO {
    private EndPoint[] endPoints;
    private ServerSocketChannel[] serverSocketChannels;
    private Selector selector= SelectorProvider.provider().openSelector();

    public NIO(Server[] servers) throws IOException {
        int count=servers.length;
        serverSocketChannels=new ServerSocketChannel[count];
    }

    public void buildServers() throws IOException {
        int serverCount=endPoints.length;
        EndPoint server;
        for(int index=0;index < serverCount;index++){
            server=endPoints[index];
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
            serverSocketChannel.bind(inetSocketAddress,((Server)server).getBackLog());
            serverSocketChannels[index]=serverSocketChannel;
            serverSocketChannel.register(selector,SelectionKey.OP_CONNECT,server.getAttach());
        }
    }

    public void listen(){
        try{
            while(true){
                selector.select();
                Set<SelectionKey> set=selector.keys();
                Iterator<SelectionKey> iterator=set.iterator();
                SelectionKey temp;
                while(iterator.hasNext()){
                    temp=iterator.next();
                    int selectionKey=temp.interestOps();
                    switch (selectionKey){
                        case SelectionKey.OP_ACCEPT:
                            ;
                            break;
                        case SelectionKey.OP_READ:
                            ;
                            break;
                        case SelectionKey.OP_WRITE:
                            ;
                            break;
                        case SelectionKey.OP_CONNECT:
                            ;
                            break;
                    }
                }
            }
        }catch (Exception e){

        }finally {

        }
    }
}
