package nna;

import nna.base.log.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author NNA-SHUAI
 * @create 2017-06-18 10:37
 **/

public class Test {
    public static void main(String[] args){
        AtomicInteger atomicInteger=new AtomicInteger();
        atomicInteger.getAndIncrement();
        System.out.println(atomicInteger.get());
        atomicInteger.set(-1);
        atomicInteger.getAndIncrement();
        System.out.println(atomicInteger.get());
        try {
            NetworkInterface networkInterface=NetworkInterface.getByName("eth0");
            Enumeration<NetworkInterface> enumeration=networkInterface.getSubInterfaces();
            while(enumeration.hasMoreElements()){
                networkInterface=enumeration.nextElement();
                System.out.println(networkInterface);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public static void testCon(){
                    for(int index = 0; index < Marco.CON_TEST_COUNT; index++){
                final Log log=Log.getLog(
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
                            log.log(""+index,10);
                        }
                        log.close();
                    }
                }).start();
            }
    }
}
