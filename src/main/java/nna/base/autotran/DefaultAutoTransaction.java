package nna.base.autotran;

import nna.base.bean.combbean.CombDB;
import nna.base.bean.combbean.CombTransaction;
import nna.base.bean.confbean.ConfMeta;
import nna.base.bean.dbbean.PlatformSql;
import nna.base.db.DBCon;
import nna.base.protocol.dispatch.ConfMetaSetFactory;
import nna.enums.DBOperType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * For Config Code
 *
 * @author NNA-SHUAI
 * @create 2017-06-07 13:09
 **/

public class DefaultAutoTransaction implements AutoTransaction {
    public void autoTran() throws SQLException {
        ConfMeta confMeta= ConfMetaSetFactory.getConfMeta();
        CombTransaction[] combTransactions=confMeta.getCombTransactions();
        CombTransaction tempTran;
        int coTranCount=combTransactions.length;
        for(int index=0;index < coTranCount;index++){
            tempTran=combTransactions[index];
            exeTempTran(confMeta,tempTran);
        }
    }

    private void exeTempTran(ConfMeta confMeta, CombTransaction tempTran) throws SQLException {
        ArrayList<CombTransaction> tranStack= confMeta.getTranStack();
        CombDB combDb=confMeta.getCombDB();
        DBCon dbCon=combDb.getDbCon();
        int tranStackSize=tranStack.size();
        if(tranStackSize==0){
            Connection con=dbCon.getCon();
            exeFirstTran(confMeta,tranStack,tempTran,con);
        }else{
            CombTransaction previousTran=tranStack.get(tranStackSize-1);

        }

    }

    private void exeFirstTran(ConfMeta confMeta,ArrayList<CombTransaction> tranStack, CombTransaction tempTran,Connection con) throws SQLException {
        tranStack.add(tempTran);
        confMeta.setCurrentConnection(con);
        ArrayList<Connection> conStack=confMeta.getConStack();
        ArrayList<PreparedStatement[]> pstStack=confMeta.getPstStack();
        conStack.add(con);
        PlatformSql[] platformSqls=tempTran.getPlatformSqls();
        String[] SQLS=tempTran.getSqls();
        PreparedStatement[] psts=initPsts(con,SQLS,platformSqls);
        pstStack.add(psts);
        confMeta.setCurrentPsts(psts);

    }

    private PreparedStatement[] initPsts(Connection con, String[] sqls, PlatformSql[] platformSqls) throws SQLException {
        int pstCount=sqls.length;
        PreparedStatement[] psts=new PreparedStatement[pstCount];
        PlatformSql platformSql;
        String sql;
        PreparedStatement pst;
        DBOperType dbOperType;
        for(int index=0;index<pstCount;index++){
            platformSql=platformSqls[index];
            sql=sqls[index];
            dbOperType=platformSql.getOpertype();
            switch (dbOperType){
                case PROCEDURE:
                    pst=con.prepareCall(sql);
                    break;
                default:
                    pst=con.prepareStatement(sql);
            }
            psts[index]=pst;
        }
        return psts;
    }
}
