package nna.transaction;

import nna.base.bean.dbbean.PlatformEntryTransaction;
import nna.base.bean.dbbean.PlatformSql;
import nna.base.bean.dbbean.PlatformTransaction;
import nna.enums.DBOperType;
import nna.enums.DBSQLConValType;

import java.sql.*;
import java.util.ArrayList;


public class AbstractTransaction<V> implements Transaction<V> {

    public V execTransaction(MetaBeanWrapper metaBeanWrapper) throws SQLException {
        PlatformEntryTransaction[] serviceTrans=metaBeanWrapper.getServiceTrans();
        PlatformEntryTransaction tempSevTran;
        int sevTranCount=serviceTrans.length;
        for(int index=0;index < sevTranCount;index++){
            tempSevTran=serviceTrans[index];
            processSevTran(metaBeanWrapper,tempSevTran,index);
        }
        return null;
    }

    private void processSevTran(MetaBeanWrapper metaBeanWrapper, PlatformEntryTransaction currentSevTran, int currentSevTranIndex) throws SQLException {
        Connection con=getCon(metaBeanWrapper);//得到执行此次事务的Connection
        setTranPgl(metaBeanWrapper,currentSevTran,con);//设置事务的隔离级别
        executeSQL(metaBeanWrapper,currentSevTran,con,currentSevTranIndex);
    }

    private void executeSQL(MetaBeanWrapper metaBeanWrapper, PlatformEntryTransaction currentSevTran, Connection con,int currentIndex) throws SQLException {
        ArrayList<String[]> SQLArray=metaBeanWrapper.getSQLS();
        ArrayList<PlatformTransaction[]> trans=metaBeanWrapper.getTrans();
        ArrayList<ArrayList<DBSQLConValType[]>> valTypeArray=metaBeanWrapper.getDbsqlConValTypes();
        ArrayList<PlatformSql[]> platformSqls=metaBeanWrapper.getTranPlatformSql();
        ArrayList<ArrayList<String[]>> conArray=metaBeanWrapper.getCons();
        ArrayList<ArrayList<String[]>> colArray=metaBeanWrapper.getCols();
        ArrayList<String[]> selCols=colArray.get(currentIndex);
        PlatformTransaction[] tranCfg=trans.get(currentIndex);
        String[] SQLS=SQLArray.get(currentIndex);
        PlatformSql[] sqlTypes=platformSqls.get(currentIndex);
        ArrayList<DBSQLConValType[]> valTypes=valTypeArray.get(currentIndex);
        ArrayList<String[]> conList=conArray.get(currentIndex);
        int exeSQLCount=SQLS.length;
        String SQL;
        String[] cons;
        DBSQLConValType[] conValTypes;
        PlatformSql sqlType;
        String[] columns;
        PlatformTransaction platformTransaction;
        boolean isExeSQLSuccess;
        for(int index=0;index < exeSQLCount;index++){
            SQL=SQLS[index];
            cons=conList.get(index);
            conValTypes=valTypes.get(index);
            sqlType=sqlTypes[index];
            columns=selCols.get(index);
            platformTransaction=tranCfg[index];
            isExeSQLSuccess=execOneSql(metaBeanWrapper,con,sqlType,SQL,columns,cons,conValTypes);
            if(isExeSQLSuccess){
                index=platformTransaction.getSuccessSequence();
            }else{
                index=platformTransaction.getFailSequence();
            }
        }
    }

    private boolean execOneSql(MetaBeanWrapper metaBeanWrapper, Connection con, PlatformSql sqlType, String sql, String[] columns, String[] cons, DBSQLConValType[] conValTypes) throws SQLException {
        DBOperType dbOperType=sqlType.getOpertype();
        PreparedStatement pst;
        switch (dbOperType){
            case PROCEDURE:
                pst=con.prepareCall(sql);
                break;
            default:
                pst=con.prepareStatement(sql);
        }
        switch (dbOperType){
            case PROCEDURE:
                ;
                return true;
            case D:
                ;
                return true;
            case I:
                ;
                return true;
            case S:
                ;
                return true;
            case U:
                ;
                return true;
            case GS:
                ;
                return true;
            case GD:
                ;
                return true;
            case GU:
                ;
                return true;
            case MS:
                ;
                return true;
        }
        return false;
    }

    private void setTranPgl(MetaBeanWrapper metaBeanWrapper, PlatformEntryTransaction currentSevTran, Connection con) {
    }

    private Connection getCon(MetaBeanWrapper metaBeanWrapper) {
        Connection con=null;

        return con;
    }
}
