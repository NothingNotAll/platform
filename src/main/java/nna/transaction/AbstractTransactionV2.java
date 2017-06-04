package nna.transaction;

import nna.base.bean.combbean.CombTransaction;
import nna.base.bean.confbean.ConfMeta;
import nna.base.bean.dbbean.PlatformSql;
import nna.base.protocol.dispatch.ConfMetaSetFactory;
import nna.enums.DBOperType;
import nna.enums.DBSQLConValType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * FOR NOT TO WRITE CODE ANLY MORE
 *
 * @author NNA-SHUAI
 * @create 2017-06-04 16:09
 **/

public abstract class AbstractTransactionV2 extends AbstractTransaction implements TransactionV2{

    public void execTransaction() throws SQLException {
        ConfMeta confMeta=ConfMetaSetFactory.getConfMeta();
        CombTransaction[] combTrans=confMeta.getCombTransactions();
        CombTransaction tempCombTran;
        int tranCount=combTrans.length;
        Connection con;
        for(int index=0;index < tranCount;index++){
            tempCombTran=combTrans[index];
            confMeta.getTranStack().add(tempCombTran);
            con=getCurrentCon(confMeta,tempCombTran);
            execTempTran(confMeta,tempCombTran,con);
        }
    }

    private Connection getCurrentCon(ConfMeta confMeta, CombTransaction tempCombTran) {
        Connection con = null;
        confMeta.getConStack().add(con);
        confMeta.setCurrentConnection(con);
        return null;
    }

    private void execTempTran(ConfMeta confMeta,CombTransaction tempCombTran,Connection con) throws SQLException {
        String[] SQLS=tempCombTran.getSqls();
        String sql;
        int SQLCOUNT=SQLS.length;
        PreparedStatement[] psts=new PreparedStatement[SQLCOUNT];
        int index=0;
        for(;index < SQLCOUNT;index++){
            sql=SQLS[index];
            psts[index]=con.prepareStatement(sql);
        }
        index=0;
        ArrayList<String[]> conditions=tempCombTran.getConditions();
        String[] condition;
        ArrayList<String[]> columns=tempCombTran.getColumns();
        String[] column;
        ArrayList<DBSQLConValType[]> dbsqlConValTypes=tempCombTran.getConditionValueTypes();
        DBSQLConValType[] dbsqlConValType;
        PlatformSql[] platformSqls=tempCombTran.getPlatformSqls();
        PlatformSql platformSql;
        PreparedStatement pst;
        for(;index < SQLCOUNT;index++){
            platformSql=platformSqls[index];
            condition=conditions.get(index);
            column=columns.get(index);
            pst=psts[index];
            dbsqlConValType=dbsqlConValTypes.get(index);
            execSQL(confMeta,con,platformSql,pst,condition,column,dbsqlConValType);
        }
    }

    private void execSQL(ConfMeta confMeta,Connection con,PlatformSql platformSql,PreparedStatement pst, String[] condition, String[] column, DBSQLConValType[] dbsqlConValType) throws SQLException {
        DBOperType dbOperType=platformSql.getOpertype();
        switch (dbOperType){
            case PROCEDURE:
                ;
                break;
            case D:
                ;
                break;
            case I:
                execInsert(confMeta,pst,condition,dbsqlConValType);
                break;
            case S:
                ;
                break;
            case U:
                ;
                break;
            case GD:
                ;
                break;
            case GS:
                ;
                break;
            case GU:
                ;
                break;
            case MS:
                ;
                break;

        }
    }

    private void execInsert(ConfMeta confMeta,PreparedStatement pst, String[] conditions,DBSQLConValType[] dbsqlConValTypes) throws SQLException {
        HashMap<String,String[]> reqCols=confMeta.getReqColumn();
        int insertCount=reqCols.get(conditions[0]).length;
        int condCount=conditions.length;
        String conditionName;
        String[] conditionValue;
        String condition;
        DBSQLConValType dbsqlConValType;
        for(int index=0;index < insertCount;index++){
            for(int conIndex=0;conIndex < condCount;conIndex++){
                dbsqlConValType=dbsqlConValTypes[conIndex];
                conditionName=conditions[conIndex];
                conditionValue=reqCols.get(conditionName);
                condition=conditionValue[conIndex];
                switch (dbsqlConValType){
                    case STRING:
                        pst.setString(conIndex,condition);
                        break;
                    case BOOLEAN:
                        pst.setBoolean(conIndex,Boolean.valueOf(condition));
                        break;
                    case INTEGER:
                        pst.setInt(conIndex,Integer.valueOf(condition));
                        break;
                }
            }
            int insertResult=pst.executeUpdate();
            if(insertResult ==0){
                throw new SQLException("");
            }
        }
    }
}
