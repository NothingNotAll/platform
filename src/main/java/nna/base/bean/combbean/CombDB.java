package nna.base.bean.combbean;

import nna.base.bean.Clone;
import nna.base.bean.dbbean.PlatformDB;
import nna.base.db.DBCon;

/**
 * d
 *
 * @author NNA-SHUAI
 * @create 2017-05-15 23:40
 **/

public class CombDB extends Clone {
    private static final long serialVersionUID = -5L;
    private PlatformDB platformDB;
    private DBCon dbCon;

    public PlatformDB getPlatformDB() {
        return platformDB;
    }

    public void setPlatformDB(PlatformDB platformDB) {
        this.platformDB = platformDB;
    }

    public DBCon getDbCon() {
        return dbCon;
    }

    public void setDbCon(DBCon dbCon) {
        this.dbCon = dbCon;
    }
}
