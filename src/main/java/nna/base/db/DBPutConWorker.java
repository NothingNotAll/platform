package nna.base.db;

import nna.base.util.concurrent.AbstractTask;

import java.sql.Connection;

/**
 * @author NNA-SHUAI
 * @create 2017-05-28 22:44
 **/

public class DBPutConWorker extends AbstractTask {
    private DBPoolManager manager;


    public DBPutConWorker(DBPoolManager manager){
        super(false);
        this.manager=manager;
        addNewTask(this,null,INIT_TASK_TYPE,true, 0L);
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
