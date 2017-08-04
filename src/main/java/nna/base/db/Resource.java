package nna.base.db;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by NNA-SHUAI on 2017/8/2.
 */
public class Resource {
    private Connection connection;
    private ReentrantLock lock;
    private volatile boolean locked=false;

    Resource (Connection connection){
        this.connection=connection;
        lock=new ReentrantLock();
    }

    static Connection getCon(Resource resource){
        try{
            if((resource.locked=resource.lock.tryLock())){

            }
        }finally {

        }
        return null;
    }
}
