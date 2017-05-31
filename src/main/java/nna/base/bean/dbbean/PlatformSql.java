package nna.base.bean.dbbean;

import nna.base.bean.Clone;
import nna.enums.DBOperType;

import java.sql.Timestamp;

/**
 * for db table of sql config
 * @author NNA-SHUAI
 * @create 2017-05-13 17:25
 **/

public class PlatformSql extends Clone {
    private static final Long serialVersionUID=8L;

    private String sqlId;
    private DBOperType opertype;
    private String sqlstr;
    private String dbColumn;
    private String appColumn;
    private String dbCondition;
    private String appCondition;
    private String appConditionType;
    private String compareKv;
    private String logicKk;
    private boolean isPage;
    private String tableName;
    private String groupOrder;
    private Timestamp createTimestamp;
    private Timestamp updateTimestamp;
    private String sqldesc;

    public PlatformSql(){

    }

    public String getSqlId() {
        return sqlId;
    }

    public void setSqlId(String sqlId) {
        this.sqlId = sqlId;
    }

    public String getDbColumn() {
        return dbColumn;
    }

    public void setDbColumn(String dbColumn) {
        this.dbColumn = dbColumn;
    }

    public String getAppColumn() {
        return appColumn;
    }

    public void setAppColumn(String appColumn) {
        this.appColumn = appColumn;
    }

    public String getDbCondition() {
        return dbCondition;
    }

    public void setDbCondition(String dbCondition) {
        this.dbCondition = dbCondition;
    }

    public String getAppCondition() {
        return appCondition;
    }

    public void setAppCondition(String appCondition) {
        this.appCondition = appCondition;
    }

    public boolean isPage() {
        return isPage;
    }

    public void setPage(boolean page) {
        isPage = page;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getGroupOrder() {
        return groupOrder;
    }

    public void setGroupOrder(String groupOrder) {
        this.groupOrder = groupOrder;
    }

    public Timestamp getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(Timestamp updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    public String getAppConditionType() {
        return appConditionType;
    }

    public void setAppConditionType(String appConditionType) {
        this.appConditionType = appConditionType;
    }

    public Timestamp getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(Timestamp createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public DBOperType getOpertype() {
        return opertype;
    }

    public void setOpertype(DBOperType opertype) {
        this.opertype = opertype;
    }

    public String getSqlstr() {
        return sqlstr;
    }

    public void setSqlstr(String sqlstr) {
        this.sqlstr = sqlstr;
    }

    public String getCompareKv() {
        return compareKv;
    }

    public void setCompareKv(String compareKv) {
        this.compareKv = compareKv;
    }

    public String getLogicKk() {
        return logicKk;
    }

    public void setLogicKk(String logicKk) {
        this.logicKk = logicKk;
    }

    public String getSqldesc() {
        return sqldesc;
    }

    public void setSqldesc(String sqldesc) {
        this.sqldesc = sqldesc;
    }
}
