package nna;

import nna.base.log.Log;

/**
 * @author NNA-SHUAI
 * @create 2017-06-18 10:37
 **/

public class Test {

    public static void testCon(){
                    for(int index = 0; index < Marco.CON_TEST_COUNT; index++){
                final Log log=Log.getLog(
                        "LOG",
                        "TEST-CON-LOG",
                        10,
                        0,
                        1000,
                        "UTF-8",
                        Marco.CON_TEST_COUNT
                );
                new Thread(new Runnable() {
                    public void run() {
                        int count=Marco.CON_TEST_COUNT-2;
                        for(int index = 1; index <= count; index++){
                            log.log(""+index,10);
                        }
                        log.close();
                    }
                }).start();
            }
            System.out.println(System.getenv("user.home"));
    }
}
