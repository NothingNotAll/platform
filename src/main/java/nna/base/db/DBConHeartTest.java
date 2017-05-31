package nna.base.db;

import nna.base.log.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * keep alive
 *
 * @author NNA-SHUAI
 * @create 2017-05-16 13:14
 **/

 class DBConHeartTest implements Runnable{
    private Log log;
    private ArrayList<DBPool> dbPools=new ArrayList<DBPool>(2);
    private int poolsSize=2;
    private int poolSize;
    private DBMeta dbMeta;
     DBConHeartTest(ArrayList<DBPool> pools, DBMeta dbMeta,Log log){
         this.log=log;
         int size=pools.size();
         dbPools=new ArrayList<DBPool>(size);
         for(int index=0;index < size;index++){
             dbPools.add(pools.get(index));
             poolSize=pools.get(index).getConSize();
         }
         this.dbMeta=dbMeta;
     }
     DBConHeartTest(DBPool[] pools, DBMeta dbMeta){
        poolsSize=pools.length;
        for(int index=0;index < poolsSize;index++){
            dbPools.add(pools[index]);
            poolSize=pools[index].getConSize();
        }
        this.dbMeta=dbMeta;
    }

    public void run() {
        try{
            while(true){
                DBPool dbPool;
                for(int index=0;index < poolsSize;index++){
                    dbPool=dbPools.get(index);
                    for(int conIndex=0;conIndex < poolSize;conIndex++){
                        try{
                            Connection connection=dbPool.getImedCon(conIndex);
                            if(connection!=null){
                                keepConAlive(connection);
                            }
                        }finally {

                        }
                    }
                }
                try{
                    Thread.sleep(dbMeta.getHeartTestMS());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void keepConAlive(Connection con){
        PreparedStatement pst = null;
        ResultSet rs=null;
        try{
            pst=con.prepareStatement("select 111 from dual;");
            rs=pst.executeQuery();
            rs.next();
            log.log(rs.getString(1),10);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(pst,rs);
        }
    }

    private static void close(PreparedStatement pst, ResultSet rs) {
        if(pst!=null){
            try{
                pst.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(rs!=null){
            try{
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }
}
