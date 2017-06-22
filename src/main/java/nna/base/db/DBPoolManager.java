package nna.base.db;

import nna.base.log.Log;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * manager
 *
 * @author NNA-SHUAI
 * @create 2017-05-16 13:34
 **/

public class DBPoolManager {
    private LinkedList<DBPool> balanceConList=new LinkedList<DBPool>();
    private LinkedList<DBPool> balanceCloseList=new LinkedList<DBPool>();
    private int conPoolSize;
    private int closePoolSize;
    private static final DBConHeartTest conHeartTest=DBConHeartTest.getInstance();
    private volatile boolean isExchanging;//when to change
    private DBPool JDBC;
    private DBMeta dbMeta;
    private Log managerLog;
    private DBPutConWorker putWorker=new DBPutConWorker(this);

    public DBPoolManager(DBMeta dbMeta, Log log) throws SQLException, ClassNotFoundException {
        this.dbMeta=dbMeta;
        this.managerLog=log;
        dbMeta.setInit(false);
        JDBC=new DBPool(dbMeta);
        conPoolSize=dbMeta.getConPoolSize();
        closePoolSize=dbMeta.getClosePoolSize();
        int index=0;
        dbMeta.setInit(true);
        for(;index < conPoolSize;index++){
            balanceConList.add(new DBPool(dbMeta));
        }
        index=0;
        dbMeta.setInit(false);
        for(;index < closePoolSize;index++){
            balanceCloseList.add(new DBPool(dbMeta));
        }
        ArrayList<DBPool> test=new ArrayList<DBPool>(dbMeta.getClosePoolSize()+dbMeta.getClosePoolSize());
        test.addAll(balanceConList);
        conHeartTest.addManager(this);
    }

    public DBConHeartTest getConHeartTest() {
        return conHeartTest;
    }

    private Connection tryGetCon(){
        Connection con=null;
        DBPool pool;
        int poolCount=balanceConList.size();
        for(int index=0;index < poolCount;index++){
            pool=balanceConList.get(index);
            con=pool.getConnection();
        }
        return con;
    }

    public Connection getCon() throws SQLException {
        int tryTime=dbMeta.getFailTryTime();
        if(isExchanging){
            return getJDBCCon();
        }
        checkIsNeedChange();
        Connection con;
        con=tryGetCon();
        if(con!=null){
            return con;
        }
        DBPool pool;
        int poolCount=balanceConList.size();
        for(int index=0;index < poolCount;index++){
            pool=balanceConList.get(index);
            con=pool.getConnection(tryTime);
            if(con!=null){
                return con;
            }
        }
        DBPool dbPool=getMinBlockDBPool(balanceConList,poolCount);
        return dbPool.getJDBCConnection();
    }

    private void checkIsNeedChange() {
        int nullCount=0;
        for(int index=0;index < balanceConList.size();index++){
            if(balanceConList.get(index).isAllNullCon()>0){
                nullCount=1;
                break;
            }
        }
        if(nullCount==0){
            exchange();
        }
    }

    public void asyPutCon(Connection con){
        putWorker.put(con);
    }

    public void putCon(Connection con){
        int tryTimeLimit=dbMeta.getFailTryTime();
        DBPool pool;
        int poolCount=balanceCloseList.size();
        for(int index=0;index < poolCount;index++){
            pool=balanceCloseList.get(index);
            System.out.println("-"+pool.getConSize());
            if(pool.putConnection(con)){
                return;
            }
        }
        pool=getMinBlockDBPool(balanceCloseList,closePoolSize);
        if(pool.putConnection(con)){
            return;
        }
        //如果有某些事务需要挂起的时间特别长的话，那就比较糟糕了。
        //需要根据不同的业务场景定制不同的数据库连接池。还需要进一步优化连接池的设计
        //需要智能变更。加一个超时的设置。
        pool.putBlockCon(con,tryTimeLimit);
    }

    public static DBPool getMinBlockDBPool(LinkedList<DBPool> pools,int poolCount){
        DBPool tempPool=pools.get(0);
        int tempBlockCount=tempPool.getBlockStatus();
        if(tempBlockCount==0){
            return tempPool;
        }
        int temp;
        DBPool nextPool;
        for(int poolIndex=1;poolIndex < poolCount;poolIndex++){
            nextPool=pools.get(poolIndex);
            temp=tempPool.getBlockStatus();
            if(temp==0){
                return nextPool;
            }
            if(temp < tempBlockCount){
                tempPool=nextPool;
            }
        }
        return tempPool;
    }

    public void exchange(){
        isExchanging=true;
        LinkedList<DBPool> temp=balanceConList;
        balanceConList=balanceCloseList;
        balanceCloseList=temp;
        isExchanging=false;
    }

    private Connection getJDBCCon() throws SQLException {
        return JDBC.getJDBCConnection();
    }

    public boolean isExchanging() {
        return isExchanging;
    }

    public void setExchanging(boolean exchanging) {
        isExchanging = exchanging;
    }

    public DBMeta getDbMeta() {
        return dbMeta;
    }

    public void setDbMeta(DBMeta dbMeta) {
        this.dbMeta = dbMeta;
    }

    public LinkedList<DBPool> getBalanceConList() {
        return balanceConList;
    }

    public void setBalanceConList(LinkedList<DBPool> balanceConList) {
        this.balanceConList = balanceConList;
    }

    public LinkedList<DBPool> getBalanceCloseList() {
        return balanceCloseList;
    }

    public void setBalanceCloseList(LinkedList<DBPool> balanceCloseList) {
        this.balanceCloseList = balanceCloseList;
    }

    public Log getManagerLog() {
        return managerLog;
    }

    public void setManagerLog(Log managerLog) {
        this.managerLog = managerLog;
    }
}
