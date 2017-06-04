package nna.app.tran.common;

import nna.Marco;
import nna.base.bean.combbean.CombTransaction;
import nna.base.bean.confbean.ConfMeta;
import nna.base.protocol.dispatch.ConfMetaSetFactory;
import nna.enums.DBSQLConValType;
import nna.transaction.AbstractTransaction;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * For Common Select with not page
 *
 * @author NNA-SHUAI
 * @create 2017-06-04 19:52
 **/

public class SelectTran extends AbstractTransaction<Object> {
    @Override
    public Object execTransaction(String transactionName) throws SQLException{
        ConfMeta confMeta= ConfMetaSetFactory.getConfMeta();
        String tranNm=confMeta.getReqColumn().get(Marco.TRAN_NAME)[0];
        super.execTransaction(tranNm);
        return null;
    }
    public Object inTransaction(Connection connection, PreparedStatement[] sts) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
        PreparedStatement countPst=sts[0];
        PreparedStatement selectPst=sts[1];
        ConfMeta confMeta=ConfMetaSetFactory.getConfMeta();
        HashMap<String,String[]> reqMap=confMeta.getReqColumn();
        CombTransaction[] trans=confMeta.getCombTransactions();
        CombTransaction combTransaction=trans[0];
        ArrayList<String[]> cons=combTransaction.getConditions();
        ArrayList<DBSQLConValType[]> dbsqlConValTypes=combTransaction.getConditionValueTypes();
        String[] countCons=cons.get(0);
        DBSQLConValType[] countDBCONTypes=dbsqlConValTypes.get(0);
        ResultSet rs=setParameter(countPst,countCons,countDBCONTypes,reqMap);
        rs.next();
        int count=rs.getInt(1);
        rs.close();
        String[] selectCons=cons.get(1);
        DBSQLConValType[] selectDBCONTypes=dbsqlConValTypes.get(1);
        rs=setParameter(selectPst,selectCons,selectDBCONTypes,reqMap);
        ArrayList<String[]> columns=combTransaction.getColumns();
        String[] column=columns.get(0);
        HashMap<String,String[]> rspMap=confMeta.getRspColumn();
        setRspMap(count,rs,column,rspMap,Marco.ARRAY_COUNT);
        countPst.close();
        selectPst.close();
        return null;
    }
}
