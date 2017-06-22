package nna.base.db;

import nna.Marco;
import nna.base.log.Log;
import nna.base.util.concurrent.AbstractTask;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * keep alive
 *
 * @author NNA-SHUAI
 * @create 2017-05-16 13:14
 **/

 class DBConHeartTest extends AbstractTask{
    private static final DBConHeartTest DB_CON_HEART_TEST=new DBConHeartTest();
    private ArrayList<DBPoolManager> managers=new ArrayList<DBPoolManager>();
    private ReentrantLock lock=new ReentrantLock();
    private Long sleepTime=0L;
    private Object lockObject=new Object();

    public DBConHeartTest() {
        super(1);
        startTask(null, Marco.SEQ_FIX_SIZE_TASK,"[DB Connection Pool Keep Alive]");
    }

    public static DBConHeartTest getInstance(){
        return DB_CON_HEART_TEST;
    }

    public void addManager(DBPoolManager manager){
        try{
            lock.lock();
            managers.add(manager);
            Long sleep=manager.getDbMeta().getHeartTestMS();
            if( sleep > sleepTime){
                sleepTime=sleep;
            }
        }finally {
            lock.unlock();
        }
        synchronized (lockObject){
            lockObject.notify();
        }
    }

    public void run() {
        try{
            while(true){
                Long start=System.currentTimeMillis();
                Iterator<DBPoolManager> iterator=managers.iterator();
                DBPoolManager manager;
                while(iterator.hasNext()){
                    manager=iterator.next();
                    keepConAlive(manager.getBalanceConList(),manager.getManagerLog());
                }
                Long conTime=System.currentTimeMillis()-start;
                System.out.println("Check DB pool waste"+conTime+"L");
                if(managers.size()==0){
                    synchronized (lockObject){
                        if(managers.size()==0){
                            lockObject.wait();
                        }
                    }
                }
                Thread.sleep(sleepTime);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void keepConAlive(LinkedList<DBPool> list,Log log){
        Iterator<DBPool> iterator=list.iterator();
        DBPool dbPool;
        ArrayList<Connection> cons;
        Iterator<Connection> iteratorCon;
        Connection con;
        while(iterator.hasNext()){
            dbPool=iterator.next();
            cons=dbPool.getPool();
            iteratorCon=cons.iterator();
            while(iteratorCon.hasNext()){
                con=iteratorCon.next();
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

    protected Object doTask(int taskType, Object attach) throws Exception {
        run();
        return null;
    }
}
