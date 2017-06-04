package nna.base.bean.combbean;/**
 * ${description}
 *
 * @author NNA-SHUAI
 * @create 2017-2017/5/13-17:55
 */

import nna.base.bean.Clone;
import nna.base.bean.dbbean.PlatformServiceTransaction;
import nna.base.bean.dbbean.PlatformSql;
import nna.base.bean.dbbean.PlatformTransaction;
import nna.enums.DBSQLConValType;

import java.util.ArrayList;

/**
 * to been used in the app layer and combination of db bean
 * @author NNA-SHUAI
 * @create 2017-05-13 17:55
 **/

public class CombTransaction extends Clone {
    private static final long serialVersionUID = -9L;
    private PlatformServiceTransaction transaction;

    private PlatformTransaction[] platformTransactions;
    private PlatformSql[] platformSqls;
    private String[] sqls;

    private ArrayList<DBSQLConValType[]> conditionValueTypes;
    private ArrayList<String[]> conditions;
    private ArrayList<String[]> columns;


    public CombTransaction(){

    }

    public String[] getSqls() {
        return sqls;
    }

    public void setSqls(String[] sqls) {
        this.sqls = sqls;
    }

    public PlatformSql[] getPlatformSqls() {
        return platformSqls;
    }

    public void setPlatformSqls(PlatformSql[] platformSqls) {
        this.platformSqls = platformSqls;
    }

    public PlatformServiceTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(PlatformServiceTransaction transaction) {
        this.transaction = transaction;
    }

    public ArrayList<DBSQLConValType[]> getConditionValueTypes() {
        return conditionValueTypes;
    }

    public void setConditionValueTypes(ArrayList<DBSQLConValType[]> conditionValueTypes) {
        this.conditionValueTypes = conditionValueTypes;
    }

    public ArrayList<String[]> getConditions() {
        return conditions;
    }

    public void setConditions(ArrayList<String[]> conditions) {
        this.conditions = conditions;
    }

    public ArrayList<String[]> getColumns() {
        return columns;
    }

    public void setColumns(ArrayList<String[]> columns) {
        this.columns = columns;
    }

    public PlatformTransaction[] getPlatformTransactions() {
        return platformTransactions;
    }

    public void setPlatformTransactions(PlatformTransaction[] platformTransactions) {
        this.platformTransactions = platformTransactions;
    }
}
