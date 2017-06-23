package nna.base.db;

import nna.Marco;
import nna.base.util.conv2.AbstractTask;

import java.sql.Connection;

/**
 * @author NNA-SHUAI
 * @create 2017-05-28 22:44
 **/

public class DBPutConWorker extends AbstractTask {
    private DBPoolManager manager;


    public DBPutConWorker(DBPoolManager manager){
        super("DB_PUT_CON",
                20,
                1,
                Marco.NO_SEQ_LINKED_SIZE_TASK,
                Marco.CACHED_THREAD_TYPE);
        this.manager=manager;
    }

    public void put(Connection con){
        addNewTask(this,con,WORK_TASK_TYPE,false,null);
    }

    private void work(Connection con) {
        if(con!=null){
            manager.putCon(con);
        }
    }

    public Object doTask(Object att, int taskType) throws Exception {
        work((Connection)att);
        return null;
    }
}
