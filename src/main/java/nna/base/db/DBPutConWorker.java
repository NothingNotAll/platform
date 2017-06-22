package nna.base.db;

import nna.Marco;
import nna.base.util.concurrent.AbstractTask;

import java.sql.Connection;

/**
 * @author NNA-SHUAI
 * @create 2017-05-28 22:44
 **/

public class DBPutConWorker extends AbstractTask {
    private DBPoolManager manager;


    public DBPutConWorker(DBPoolManager manager){
        super("[DBCon Put Back Task]", 1);
        this.manager=manager;
        startTask(null, Marco.SEQ_LINKED_SIZE_TASK);
    }

    public void put(Connection con){
        addNewTask(con,INIT);
    }

    private void work(Connection con) {
        if(con!=null){
            manager.putCon(con);
        }
    }

    protected Object doTask(int taskType, Object attach) throws Exception {
        work((Connection)attach);
        return null;
    }
}
