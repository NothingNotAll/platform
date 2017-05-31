package nna.base.db;

import java.sql.Connection;

/**
 * @author NNA-SHUAI
 * @create 2017-05-28 22:44
 **/

public class DBPutConWorker implements Runnable {
    private Connection con;
    private DBPoolManager manager;

    public DBPutConWorker(Connection con,DBPoolManager manager){
        this.con=con;
        this.manager=manager;
    }
    public void run() {
      try{
          while(true){
              work();
          }
      }catch (Exception e){
          e.printStackTrace();
      }finally {

      }
    }

    private void work() {
        manager.putCon(con);
    }
}
