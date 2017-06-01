package nna.base.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * dbpool
 *
 * @author NNA-SHUAI
 * @create 2017-05-16 12:49
 **/

 class DBPool {
    public static final int BLOCK_STATUS_NO=0;

    private ArrayList<Connection> pool;
    private ArrayList<ReentrantLock> poolLocks;
    private DBMeta dbMeta;
    private int conSize;
    private volatile boolean supplement;
    private volatile int blockStatus;

    public DBPool(DBMeta dbMeta) throws SQLException, ClassNotFoundException {
        init(
                dbMeta.isInit(),
                dbMeta.getUrl(),
                dbMeta.getDriver(),
                dbMeta.getAccount(),
                dbMeta.getPassword(),
                dbMeta.getConPoolSize(),
                dbMeta.getClosePoolSize(),
                dbMeta.getConCount(),
                dbMeta.getHeartTestMS(),
                dbMeta.getFailTryTime());
    }

    public DBPool(
            boolean isInit,
            String url,
            String driver,
            String account,
            String password,
            int conPoolSize,
            int closePoolSize,
            int conCount,
            Long heartTestMS,
            int failTryTime
    ) throws SQLException, ClassNotFoundException {
        init(
                isInit,
                url,
                driver,
                account,
                password,
                conPoolSize,
                closePoolSize,
                conCount,
                heartTestMS,
                failTryTime);
    }

    private void init(
            boolean isInit,
            String url,
            String driver,
            String account,
            String password,
            int conPoolSize,
            int closePoolSize,
            int conCount,
            Long heartTestMS,
            int failTryTime
    ) throws SQLException, ClassNotFoundException {
        this.dbMeta=new DBMeta(
                isInit,
                url,
                driver,
                account,
                password,
        conPoolSize,
         closePoolSize,
         conCount,
         heartTestMS,
         failTryTime);
        this.conSize=conCount;
        initPool(dbMeta,conSize,isInit);
    }

    private void initPool(DBMeta dbMeta,int conSize,boolean isInit) throws ClassNotFoundException, SQLException {
            this.conSize=conSize;
            Class.forName(dbMeta.getDriver());
            pool=new ArrayList<Connection>(conSize);
            poolLocks=new ArrayList<ReentrantLock>();
            Connection con;
            for(int index=0;index < conSize;index++){
                if(isInit){
                    con=getConnection(dbMeta);
                }else{
                    con=null;
                }
                pool.add(con);
            }
            for(int index=0;index < conSize;index++){
                poolLocks.add(new ReentrantLock());
            }
    }

    private static Connection getConnection(DBMeta dbMeta) throws SQLException {
        return DriverManager.getConnection(dbMeta.getUrl(),dbMeta.getAccount(),dbMeta.getPassword());
    }

    private Connection getMinBLockSizeCon() {
         Connection con;
         int minBlockIndex=getMinBLockIndex();
         con=tryGetCon(minBlockIndex);
         if(con!=null){
             return con;
         }
         return getBlockConnection(minBlockIndex);
    }

    private Connection tryGetCon(int index){
        Connection con=pool.get(index);
        if(con==null){
            return null;
        }
        ReentrantLock lock=poolLocks.get(index);
        boolean lockStatus=false;
        try{
            if(lock.tryLock()){
                lockStatus=true;
                con=pool.get(index);
                if(con==null){
                    return null;
                }
                pool.set(index,null);
            }
        }finally {
            if(lockStatus){
                lock.unlock();
            }
        }
        return con;
    }

    private int getMinBLockIndex() {
         if(conSize < 2){
             return 0;
         }
         int minIndex=0;
         int tempLockCount=poolLocks.get(0).getQueueLength();
         int tempCount;
         ReentrantLock lock;
         for(int index=1;index < conSize;index++){
             lock=poolLocks.get(index);
             tempCount=lock.getQueueLength();
             if(lock.isLocked()){
                 if(tempLockCount > tempCount){
                     tempLockCount=tempCount;
                     minIndex=index;
                 }
             }
         }
         return minIndex;
    }

    int getBlockCount(){
        int blockCount=0;
        ReentrantLock lock;
        for(int index=0;index < conSize;index++){
            lock=poolLocks.get(index);
            blockCount+=lock.getQueueLength();
        }
        return blockCount;
    }

    Connection getBlockConnection(int index){
        if(supplement){
            try {
                return getConnection(dbMeta);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        Connection connection;
        ReentrantLock lock=poolLocks.get(index);
        try{
            lock.lock();
            connection=pool.get(index);
            pool.set(index,null);
        }finally {
            lock.unlock();
        }
        return connection;
    }
     //阻塞点；
     void putBlockConnection(Connection connection){
         if(supplement){
             try{
                 connection.close();
             } catch (SQLException e) {
                 e.printStackTrace();
             }
         }
        if(!putConnection(connection)){
            putMinBLockSizeCon(connection);
        }
    }

    //阻塞点；
    void putBlockCon(Connection connection,int timeout){
        int tryTime=0;
        int index=0;
        for(;;){
            if(tryTime >= timeout){
                close(connection);
                return ;
            }

            index=index==conSize?0:++index;

            if(pool.get(index)!=null){
                continue;
            }

            if(tryPutCon(index,connection)){
                tryTime++;
                blockStatus=tryTime;
                return ;
            }
        }
    }

    private void close(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void putMinBLockSizeCon(Connection connection) {
        int minIndex=getMinBLockIndex();
        if(!tryPutCon(minIndex,connection)){
            putBlock(connection);
        }
    }

    private void putBlock(Connection connection) {
         int index=0;
         for(;;){
             if(index==conSize){
                 index=0;
             }else{
                 index++;
             }
             if(pool.get(index)!=null){
                 continue;
             }
             if(tryPutCon(index,connection)){
                 return ;
             }
         }
    }

    private boolean tryPutCon(int minIndex, Connection connection) {
        ReentrantLock lock;
        lock=poolLocks.get(minIndex);
        boolean lockStatus=false;
        try{
            if(lock.tryLock()){
                lockStatus=true;
                if(pool.get(minIndex)!=null){
                    return false;
                }
                blockStatus=BLOCK_STATUS_NO;
                pool.set(minIndex,connection);
                return true;
            };
        }finally {
            if(lockStatus){
                lock.unlock();
            }
        }
        return false;
    }

    int isAllNullCon(){
        int count=0;
        for(int index=0;index < conSize;index++){
            if(pool.get(index)!=null){
                count++;
                break;
            }
        }
        return count;
    }

    Connection getConnection(){
        if(supplement){
            try {
                blockStatus=BLOCK_STATUS_NO;
                return getConnection(dbMeta);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        Connection connection;
        for(int index=0;index < conSize;index++){
            connection=tryGetCon(index);
            if(connection!=null){
                return connection;
            }
        }
        return null;
    }
    Connection getConnection(int tryGetTime) throws SQLException {
        if(supplement){
            try {
                return getConnection(dbMeta);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        int time=0;
        Connection connection;
        for(;time < tryGetTime;time++){
            connection=getConnection();
            if(connection!=null){
                blockStatus=tryGetTime;
                return connection;
            }
        }
        return null;
    }

    public Connection getJDBCConnection() throws SQLException {
        return getConnection(dbMeta);
    }

     boolean putConnection(Connection connection){
        if(supplement){
            try{
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        }
        boolean putSuccess=false;
        ReentrantLock reentrantLock;
        Connection nullCon;
        for(int index=0;index < conSize;index++){
            reentrantLock=poolLocks.get(index);
            nullCon=pool.get(index);
            if(nullCon==null){
                try{
                    reentrantLock.tryLock();
                    if(nullCon==null){
                        pool.set(index,connection);
                        putSuccess=true;
                    }
                }finally {
                    reentrantLock.unlock();
                }
            }
        }
        blockStatus=1;
        return putSuccess;
    }

    Connection getImedCon(int index){
        return pool.get(index);
    }

    void supplement(int supplementSize) throws SQLException {
        supplement=true;
        boolean canSupplment=true;
        ReentrantLock lock;

        while(!canSupplment){
            boolean locked=false;

            //check is has locked thread until no lock thread
            for(int index=0;index < conSize;index++){
                lock=poolLocks.get(index);
                if(lock.isLocked()){
                    canSupplment=true;
                    break;
                }
            }

            if(locked){
                continue;
            }else{
                break;
            }
        }

        supplementCons(supplementSize);

        supplement=false;
    }

    private int supplementCons(int suppSize) throws SQLException {
        Connection con;
        int nullSize=0;

        //ensure null index to be nonNullCon;
        int index=0;
        for(;index < conSize;index++){
            con=pool.get(index);
            if(con==null){
                nullSize++;
                pool.set(index,getConnection(dbMeta));
            }
        }

        index=0;
        for(;index < suppSize;index++){
            pool.add(getConnection(dbMeta));
            poolLocks.add(new ReentrantLock());
        }

        conSize+=suppSize;

        return nullSize;
    }

    ArrayList<Connection> getPool() {
        return pool;
    }

     void setPool(ArrayList<Connection> pool) {
        this.pool = pool;
    }

    public DBMeta getDbMeta() {
        return dbMeta;
    }

     void setDbMeta(DBMeta dbMeta) {
        this.dbMeta = dbMeta;
    }

    public int getConSize() {
        return conSize;
    }

     void setConSize(int conSize) {
        this.conSize = conSize;
    }

    public boolean isSupplement() {
        return supplement;
    }

     void setSupplement(boolean supplement) {
        this.supplement = supplement;
    }

    public ArrayList<ReentrantLock> getPoolLocks() {
        return poolLocks;
    }

    public void setPoolLocks(ArrayList<ReentrantLock> poolLocks) {
        this.poolLocks = poolLocks;
    }

    public int getBlockStatus() {
        return blockStatus;
    }

    public void setBlockStatus(int blockStatus) {
        this.blockStatus = blockStatus;
    }
}
