package nna.transaction;

import nna.base.bean.dbbean.PlatformEntryTransaction;
import nna.base.bean.dbbean.PlatformSql;
import nna.base.bean.dbbean.PlatformTransaction;
import nna.base.db.DBCon;
import nna.enums.DBOperType;
import nna.enums.DBSQLConValType;
import nna.enums.DBTranLvlType;
import nna.enums.DBTranPpgType;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;


public class DefaultTransExecutor<V> implements TransExecutor<V> {

    /*
    * the logic of exes of trans
    * for(Transaction tran:Transactions){
    *   exeTran(tran){
    *       String[] SQLS=tran.getSQLS();
    *       for(String SQL in SQLS){
    *           exeSQL(SQL){
    *               switch(type of SQL){
    *                   case procedure:
    *                   ;
    *                   case select:
    *                   ;
    *                   case update:
    *                   ;
    *               }
    *           };
    *       }
    *   };
    * }
    *
    * */
    public V executeTransactions(MetaBeanWrapper metaBeanWrapper) throws SQLException {
        PlatformEntryTransaction[] serviceTrans=metaBeanWrapper.getServiceTrans();
        PlatformEntryTransaction tempSevTran;
        int sevTranCount=serviceTrans.length;
        boolean isExeTranSuccess;
        Integer nextTranIndex;
        ArrayList<PlatformEntryTransaction> tranStack=metaBeanWrapper.getTranStack();
        for(int index=0;index < sevTranCount;index++){
            metaBeanWrapper.setCurrentServTranIndex(index);
            tempSevTran=serviceTrans[index];
            tranStack.add(tempSevTran);
            try{
                isExeTranSuccess=executeTempSevTran(metaBeanWrapper,tempSevTran,index);
            }catch (Exception e){
                isExeTranSuccess=false;
            }finally {
                destroy(metaBeanWrapper,tempSevTran);
            }
            if(isExeTranSuccess){
                nextTranIndex=tempSevTran.getSuccessIndex();
            }else {
                nextTranIndex=tempSevTran.getFailIndex();
            }
            if(nextTranIndex==null){
                return null;
            }else{
                index=nextTranIndex;
            }
        }
        return null;
    }

    private void destroy(MetaBeanWrapper metaBeanWrapper, PlatformEntryTransaction tempSevTran) throws SQLException {
        int index=metaBeanWrapper.getConStack().size();
        metaBeanWrapper.getTranStack().remove(index-1);
        metaBeanWrapper.getConStack().remove(index-1);
        metaBeanWrapper.getCurrentCon().commit();
        metaBeanWrapper.setCurrentCon(null);
    }

    private boolean executeTempSevTran(
            MetaBeanWrapper metaBeanWrapper,
            PlatformEntryTransaction currentSevTran,
            int currentSevTranIndex
    ) throws SQLException {
        ArrayList<PlatformEntryTransaction> stack=metaBeanWrapper.getTranStack();
        Connection con=getCon(metaBeanWrapper,stack,currentSevTran);//得到执行此次事务的Connection
        metaBeanWrapper.getConStack().add(con);
        metaBeanWrapper.setCurrentCon(con);
        setTranPgl(metaBeanWrapper,currentSevTran,con);//设置事务的隔离级别
        ArrayList<String[]> SQLArray=metaBeanWrapper.getSQLS();
        ArrayList<PlatformTransaction[]> trans=metaBeanWrapper.getTrans();
        ArrayList<ArrayList<DBSQLConValType[]>> valTypeArray=metaBeanWrapper.getDbsqlConValTypes();
        ArrayList<PlatformSql[]> platformSqls=metaBeanWrapper.getTranPlatformSql();
        ArrayList<ArrayList<String[]>> conArray=metaBeanWrapper.getCons();
        ArrayList<ArrayList<String[]>> colArray=metaBeanWrapper.getCols();
        return executeSQLS(
                metaBeanWrapper,
                SQLArray,
                trans,
                valTypeArray,
                platformSqls,
                conArray,
                colArray,
                con,
                currentSevTranIndex);
    }

    private boolean executeSQLS(
            MetaBeanWrapper metaBeanWrapper,
            ArrayList<String[]> SQLArray,
            ArrayList<PlatformTransaction[]> trans,
            ArrayList<ArrayList<DBSQLConValType[]>> valTypeArray,
            ArrayList<PlatformSql[]> platformSqls,
            ArrayList<ArrayList<String[]>> conArray,
            ArrayList<ArrayList<String[]>> colArray,
            Connection con,
            int currentIndex) throws SQLException {
        metaBeanWrapper.setCurrentSQLS(SQLArray.toArray(new String[0]));
        metaBeanWrapper.setCurrentPlatformSqls(platformSqls.toArray(new PlatformSql[0]));
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
        Integer nextSQLIndex;
        for(int index=0;index < exeSQLCount;index++){
            SQL=SQLS[index];
            cons=conList.get(index);
            conValTypes=valTypes.get(index);
            sqlType=sqlTypes[index];
            columns=selCols.get(index);
            platformTransaction=tranCfg[index];
            isExeSQLSuccess=executeOneSql(
                    metaBeanWrapper,
                    con,
                    sqlType,
                    SQL,
                    columns,
                    cons,
                    conValTypes);
            if(isExeSQLSuccess){
                nextSQLIndex=platformTransaction.getSuccessSequence();
            }else{
                nextSQLIndex=platformTransaction.getFailSequence();
            }
            if(nextSQLIndex==null){
                return true;
            }else{
                index=nextSQLIndex;
            }
        }
        return true;
    }

    private boolean executeOneSql(
            MetaBeanWrapper metaBeanWrapper,
            Connection con,
            PlatformSql platformSql,
            String sql,
            String[] columns,
            String[] cons,
            DBSQLConValType[] conValTypes
    ) throws SQLException {
        metaBeanWrapper.setCurrentSQL(sql);
        HashMap<String,String[]> req=metaBeanWrapper.getReq();
        DBOperType dbOperType=platformSql.getOpertype();
        if(platformSql.isPage()){
            //how to solve it
        }
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

    private void executeSelect(
            PreparedStatement pst,
            HashMap<String, String[]> req,
            String[] columns, String[] cons,
            DBSQLConValType[] conValTypes
    ) throws SQLException {
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
                //zero Copy to achieve
                System.arraycopy(selVal,0,selValTemp,0,selValCount);
                req.put(nm,selValTemp);
            }
        }
        rs.close();
        pst.close();
    }

    private void executeUpdate(
            PreparedStatement pst,
            HashMap<String, String[]> req,
            String[] cons,
            DBSQLConValType[] conValTypes
    ) throws SQLException {
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

    private void setCon(
            PreparedStatement pst,
            int index,
            String conVal,
            DBSQLConValType conValType
    ) throws SQLException {
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

    private void setTranPgl(
            MetaBeanWrapper metaBeanWrapper,
            PlatformEntryTransaction currentSevTran,
            Connection con
    ) throws SQLException {
        DBTranLvlType dbTranLvlType=currentSevTran.getTransactionLevel();
        switch (dbTranLvlType){
            case NONE:
                con.setTransactionIsolation(Connection.TRANSACTION_NONE);
                break;
            case READ_COMMITTED:
                con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                break;
            case REPEATABLE_READ:
                con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
                break;
            case READ_UNCOMMITTED:
                con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                break;
            case SERIALIZABLE:
                con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                break;
        }
    }

    private Connection getCon(
            MetaBeanWrapper metaBeanWrapper,
            ArrayList<PlatformEntryTransaction> tranStack,
            PlatformEntryTransaction currentSevTran
    ) throws SQLException {
        Connection con=null;
        DBCon dbCon;
        DBTranPpgType dbTranPpgType=currentSevTran.getTransactionPropagation();
        Integer previousSevTran=currentSevTran.getPreviousTransactionIndex();
        PlatformEntryTransaction previous;
        if(previousSevTran!=null){
            previous=tranStack.get(previousSevTran);
        }
        switch (dbTranPpgType){
            case NONE:
                dbCon=metaBeanWrapper.getDbCon();
                return dbCon.getCon();
            case PROPAGATION_NEVER:
                dbCon=metaBeanWrapper.getDbCon();
                return dbCon.getCon();
            case PROPAGATION_NESTED:
                ;
                break;
            case PROPAGATION_REQUIRED:
                ;
                break;
            case PROPAGATION_SUPPORTS:
                ;
                break;
            case PROPAGATION_MANDATORY:
                ;
                break;
            case PROPAGATION_REQUIRES_NEW:
                dbCon=metaBeanWrapper.getDbCon();
                return dbCon.getCon();
            case PROPAGATION_NOT_SUPPORTED:
                ;
                break;
            case DEFAULT:
                ;
                break;
        }
        return con;
    }
}
