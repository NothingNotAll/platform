package nna.test;

import nna.Marco;
import nna.base.log.Log;
import nna.base.util.LogUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 * @author NNA-SHUAI
 * @create 2017-06-18 10:37
 **/

public class Test {
    public static void main(String[] args){
        new Thread(new Runnable() {
            public void run() {
                while (true){
                    try {
                        SocketChannel socketChannel=SocketChannel.open();
                        socketChannel.connect(new InetSocketAddress("localhost",80));
                        if(!socketChannel.isConnected()){
                            socketChannel.finishConnect();
                        }
                        socketChannel.write(ByteBuffer.wrap(new String("ajfpajfpafjajfajfa;jfaj;jf;ajf;ajfj").getBytes()));
                        socketChannel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    try {
//                        Thread.sleep(300L);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        }).start();
        //        park();
//        AtomicInteger atomicInteger=new AtomicInteger();
//        atomicInteger.getAndIncrement();
//        System.out.println(atomicInteger.get());
//        atomicInteger.set(-1);
//        atomicInteger.getAndIncrement();
//        System.out.println(atomicInteger.get());
//        try {
//            NetworkInterface networkInterface=NetworkInterface.getByName("eth0");
//            Enumeration<NetworkInterface> enumeration=networkInterface.getSubInterfaces();
//            while(enumeration.hasMoreElements()){
//                networkInterface=enumeration.nextElement();
//                System.out.println(networkInterface);
//            }
//        } catch (SocketException e) {
//            e.printStackTrace();
//        }
    }

    public static void park(){
        final Thread t=new Thread(new Runnable() {
            public void run() {
                while(true){
                    System.out.println(Thread.currentThread().getState().toString());
                    LockSupport.park();
                }
            }
        });
        t.start();
        new Thread(new Runnable() {
            public void run() {
              try{
                  while(true){
                      try{
                          System.out.println(t.getState().toString());
                          Thread.sleep(10000L);
                          System.out.println(t.getState().toString());
                          LockSupport.unpark(t);
                          System.out.println(t.getState().toString());
                      }catch (Exception e){
                          e.printStackTrace();
                      }
                  }
              }catch (Exception e){
                  e.printStackTrace();
              }
            }
        }).start();
    }

    public static void testCon(){
                    for(int index = 0; index < Marco.CON_TEST_COUNT; index++){
                final Log log= LogUtil.getLog(
                        "LOG",
                        "TEST-CON-LOG",
                        10,
                        0,
                        1000,
                        "UTF-8"
                );
                new Thread(new Runnable() {
                    public void run() {
                        int count=Marco.CON_WORK_COUNT-2;
                        for(int index = 1; index <= count; index++){
                            try{
                                log.log(""+index,10);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        log.close();
                    }
                }).start();
            }
    }
}
