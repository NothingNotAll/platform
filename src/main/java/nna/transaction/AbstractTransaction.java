package nna.transaction;

import nna.base.bean.dbbean.PlatformEntryTransaction;
import nna.base.bean.dbbean.PlatformSql;
import nna.base.bean.dbbean.PlatformTransaction;
import nna.enums.DBOperType;
import nna.enums.DBSQLConValType;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;


public class AbstractTransaction<V> implements Transaction<V> {

    public V execTransaction(MetaBeanWrapper metaBeanWrapper) throws SQLException {
        PlatformEntryTransaction[] serviceTrans=metaBeanWrapper.getServiceTrans();
        PlatformEntryTransaction tempSevTran;
        int sevTranCount=serviceTrans.length;
        boolean isExeTranSuccess;
        Integer nextTranIndex;
        for(int index=0;index < sevTranCount;index++){
            tempSevTran=serviceTrans[index];
            isExeTranSuccess=processSevTran(metaBeanWrapper,tempSevTran,index);
            if(isExeTranSuccess){
                nextTranIndex=tempSevTran.getSuccessIndex();
                if(nextTranIndex==null){
                    return null;
                }
            }else {
                nextTranIndex=tempSevTran.getFailIndex();
                if(nextTranIndex==null){
                    return null;
                }
            }
            index=nextTranIndex;
        }
        return null;
    }

    private boolean processSevTran(MetaBeanWrapper metaBeanWrapper, PlatformEntryTransaction currentSevTran, int currentSevTranIndex) throws SQLException {
        Connection con=getCon(metaBeanWrapper);//得到执行此次事务的Connection
        setTranPgl(metaBeanWrapper,currentSevTran,con);//设置事务的隔离级别
        return executeSQL(metaBeanWrapper,currentSevTran,con,currentSevTranIndex);
    }

    private boolean executeSQL(MetaBeanWrapper metaBeanWrapper, PlatformEntryTransaction currentSevTran, Connection con,int currentIndex) throws SQLException {
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
        return true;
    }

    private boolean execOneSql(MetaBeanWrapper metaBeanWrapper, Connection con, PlatformSql sqlType, String sql, String[] columns, String[] cons, DBSQLConValType[] conValTypes) throws SQLException {
        HashMap<String,String[]> req=metaBeanWrapper.getReq();
        HashMap<String,String[]> rsp=metaBeanWrapper.getRsp();
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
                executeUpdate(pst,req,cons,conValTypes);
                return true;
            case I:
                executeUpdate(pst,req,cons,conValTypes);
                return true;
            case S:
                executeSelect(pst,req,columns,cons,conValTypes);
                return true;
            case U:
                executeUpdate(pst,req,cons,conValTypes);
                return true;
            case GS:
                executeSelect(pst,req,columns,cons,conValTypes);
                return true;
            case GD:
                executeUpdate(pst,req,cons,conValTypes);;
                return true;
            case GU:
                executeUpdate(pst,req,cons,conValTypes);;
                return true;
            case MS:
                executeSelect(pst,req,columns,cons,conValTypes);
                return true;
        }
        return false;
    }

    private void executeSelect(PreparedStatement pst, HashMap<String, String[]> req,String[] columns, String[] cons, DBSQLConValType[] conValTypes) throws SQLException {
        String conNm;
        String conVal;
        DBSQLConValType conValType;
        int conCount=cons.length;
        for(int conIndex=0;conIndex< conCount;conIndex++){
            conNm=cons[conIndex];
            conValType=conValTypes[conIndex];
            conVal=req.get(conNm)[0];
            setCon(pst,conIndex,conVal,conValType);
        }
        ResultSet rs=pst.executeQuery();
        int columnCount=columns.length;
        String temp;
        String nm;
        String[] selVal;
        String[] selValTemp;
        int selValCount;
        while(rs.next()){
            for(int index=0;index < columnCount;index++){
                nm=columns[index];
                temp=rs.getString(index);
                selVal=req.get(nm);
                selValCount=selVal.length;
                selValTemp=new String[selValCount+1];
                selValTemp[selValCount]=temp;
                req.put(nm,selValTemp);
            }
        }
        rs.close();
        pst.close();
    }

    private void executeUpdate(PreparedStatement pst, HashMap<String, String[]> req, String[] cons, DBSQLConValType[] conValTypes) throws SQLException {
        int updateCount=req.get(cons[0]).length;
        int conCount=cons.length;
        for(int index=0;index < updateCount;index++){
            String conNm;
            String conVal;
            DBSQLConValType conValType;
            for(int conIndex=0;conIndex< conCount;conIndex++){
                conNm=cons[conIndex];
                conValType=conValTypes[conIndex];
                conVal=req.get(conNm)[index];
                setCon(pst,conIndex,conVal,conValType);
            }
            pst.executeUpdate();
        }
        pst.close();
    }

    private void setCon(PreparedStatement pst,int index, String conVal, DBSQLConValType conValType) throws SQLException {
        switch (conValType){
            case INTEGER:
                pst.setInt(index,Integer.valueOf(conVal));
                break;
            case STRING:
                pst.setString(index,conVal);
                break;
            case BOOLEAN:
                pst.setBoolean(index,Boolean.valueOf(conVal));
                break;
        }
    }

    private void setTranPgl(MetaBeanWrapper metaBeanWrapper, PlatformEntryTransaction currentSevTran, Connection con) {
    }

    private Connection getCon(MetaBeanWrapper metaBeanWrapper) {
        Connection con=null;

        return con;
    }
}
