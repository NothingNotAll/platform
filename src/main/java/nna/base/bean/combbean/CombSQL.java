package nna.base.bean.combbean;

import nna.base.bean.Clone;
import nna.base.bean.dbbean.PlatformSql;
import nna.enums.DBSQLConValType;

/**
 * d
 *
 * @author NNA-SHUAI
 * @create 2017-05-15 23:17
 **/

public class CombSQL extends Clone {
    private static final long serialVersionUID = -8L;
    private PlatformSql platformSql;

    private String sql;
    private DBSQLConValType[] DBSQLConValTypes;
    private String[] conditions;
    private String[] columns;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public PlatformSql getPlatformSql() {
        return platformSql;
    }

    public void setPlatformSql(PlatformSql platformSql) {
        this.platformSql = platformSql;
    }

    public DBSQLConValType[] getDBSQLConValTypes() {
        return DBSQLConValTypes;
    }

    public void setDBSQLConValTypes(DBSQLConValType[] DBSQLConValTypes) {
        this.DBSQLConValTypes = DBSQLConValTypes;
    }

    public String[] getConditions() {
        return conditions;
    }

    public void setConditions(String[] conditions) {
        this.conditions = conditions;
    }

    public String[] getColumns() {
        return columns;
    }

    public void setColumns(String[] columns) {
        this.columns = columns;
    }
}
