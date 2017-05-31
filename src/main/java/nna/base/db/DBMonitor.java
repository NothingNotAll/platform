package nna.base.db;

/**
 * @author NNA-SHUAI
 * @create 2017-05-27 11:18
 **/

public class DBMonitor {
    private DBPoolManager dbPoolManager;


    public DBMonitor(DBPoolManager dbPoolManager){
        this.dbPoolManager=dbPoolManager;
    }

    public DBPoolManager getDbPoolManager() {
        return dbPoolManager;
    }

    public void setDbPoolManager(DBPoolManager dbPoolManager) {
        this.dbPoolManager = dbPoolManager;
    }
}
