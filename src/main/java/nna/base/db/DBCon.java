package nna.base.db;

import nna.base.log.Log;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 *
 * @author NNA-SHUAI
 * @create 2017-05-16 13:31
 **/

public class DBCon {
    private DBPoolManager dbPoolManager;

    public DBCon(DBMeta dbMeta,
                 Log log) throws SQLException, ClassNotFoundException {
        this.dbPoolManager=new DBPoolManager(dbMeta,log);
    }
    public static DBCon init(DBMeta dbMeta, Log log) throws SQLException, ClassNotFoundException {
        return new DBCon(dbMeta,log);
    }

    public Connection getCon() throws SQLException {
        Connection con;
        con=dbPoolManager.getCon();
        return con;
    }

    public void putCon(Connection con) throws SQLException {
        dbPoolManager.putCon(con);
    }

    public void asyPutCon(Connection con) throws SQLException {
        dbPoolManager.asyPutCon(con);
    }
}
