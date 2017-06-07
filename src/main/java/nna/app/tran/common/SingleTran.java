package nna.app.tran.common;

import nna.Marco;
import nna.base.bean.combbean.CombTransaction;
import nna.base.bean.dbbean.PlatformServiceTransaction;
import nna.base.bean.dbbean.PlatformSql;
import nna.base.bean.dbbean.PlatformTransaction;
import nna.base.db.DBCon;
import nna.base.protocol.dispatch.ConfMetaSetFactory;
import nna.enums.DBOperType;
import nna.enums.DBSQLConValType;
import nna.transaction.AbstractTransaction;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * For Only One Transaction In The Service
 *
 * @author NNA-SHUAI
 * @create 2017-06-04 22:45
 **/

public class SingleTran extends AbstractTransaction<Object> {

    @Override
    public Object execTransaction(String transactionName) throws SQLException{
        MetaBean confMeta= ConfMetaSetFactory.getConfMeta();
        String tranNm=confMeta.getReq().get(Marco.TRAN_NAME)[0];
//        HashMap<String,CombTransaction> map=confMeta.getCombTransactionMap();
        CombTransaction combTransaction=null;
        execTran(confMeta,combTransaction);
        return null;
    }

    public void execTran(MetaBean confMeta, CombTransaction combTransaction) throws SQLException {
        DBCon dbCon=confMeta.getDbCon();
        Connection con=dbCon.getCon();
        PlatformServiceTransaction platformServiceTransaction=combTransaction.getTransaction();
        PlatformTransaction[] platformTransactions=combTransaction.getPlatformTransactions();
        PlatformTransaction platformTransaction;
        PlatformSql[] platformSqls=combTransaction.getPlatformSqls();
        String[] SQLS=combTransaction.getSqls();
        PreparedStatement[] psts=initPsts(SQLS,con);
        ArrayList<String[]> conditions=combTransaction.getConditions();
        ArrayList<DBSQLConValType[]> conValTypes= combTransaction.getConditionValueTypes();
        ArrayList<String[]> columns=combTransaction.getColumns();
        String[] cons;
        DBSQLConValType[] conValType;
        String[] cols;
        PreparedStatement pst;
        int exeCount=psts.length;
        PlatformSql platformSql;
        HashMap<String,String[]> conMap=confMeta.getReq();
        for(int index=0;index < exeCount;index++){
            platformSql=platformSqls[index];
            platformTransaction=platformTransactions[index];
            cons=conditions.get(index);
            conValType=conValTypes.get(index);
            cols=columns.get(index);
            pst=psts[index];
            exePst(pst,platformTransaction,platformSql,cols,conMap,cons,conValType);
        }
    }

    private void exePst(PreparedStatement pst, PlatformTransaction platformTransaction, PlatformSql platformSql, String[] cols,HashMap<String,String[]> conMap,String[] conditions, DBSQLConValType[] conValType) throws SQLException {
        DBOperType dbOperType=platformSql.getOpertype();
        setParameter(pst,conditions,conValType,conMap,0);
        switch (dbOperType){
            case MS:
                ;
                break;
            case GU:
                ;
                break;
            case GD:
                ;
                break;
            case GS:
                ;
                break;
            case U:
                ;
                break;
            case S:
                ;
                break;
            case I:
                ;
                break;
            case D:
                ;
                break;
            case PROCEDURE:
                ;
                break;
        }
    }

    public PreparedStatement[] initPsts(String[] SQLS,Connection con) throws SQLException {
        int count=SQLS.length;
        PreparedStatement[] psts=new PreparedStatement[count];
        for(int index=0;index < count;index++){
            psts[index]=con.prepareStatement(SQLS[index]);
        }
        return psts;
    }

    public Object inTransaction(Connection connection, PreparedStatement[] sts) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {

        return null;
    }
}
