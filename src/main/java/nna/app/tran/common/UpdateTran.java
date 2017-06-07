package nna.app.tran.common;

import nna.Marco;
import nna.base.bean.combbean.CombTransaction;
import nna.base.protocol.dispatch.ConfMetaSetFactory;
import nna.enums.DBSQLConValType;
import nna.transaction.AbstractTransaction;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * For Common Delete Transaction
 *
 * @author NNA-SHUAI
 * @create 2017-06-04 20:36
 **/

public class UpdateTran extends AbstractTransaction<Object> {

    @Override
    public Object execTransaction(String transactionName) throws SQLException{
        MetaBean confMeta= ConfMetaSetFactory.getConfMeta();
        String tranNm=confMeta.getReq().get(Marco.TRAN_NAME)[0];
        super.execTransaction(tranNm);
        return null;
    }

    public Object inTransaction(Connection connection, PreparedStatement[] sts) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
        PreparedStatement updatePst=sts[0];
        MetaBean confMeta=ConfMetaSetFactory.getConfMeta();
        CombTransaction deleteCombTran=null;
        ArrayList<String[]> cons=deleteCombTran.getConditions();
        ArrayList<DBSQLConValType[]> dbConTypes=deleteCombTran.getConditionValueTypes();
        String[] columns=cons.get(0);
        DBSQLConValType[] dbsqlConValTypes=dbConTypes.get(0);
        HashMap<String,String[]> conMap=confMeta.getReq();
        exeMultiUpdate(updatePst,conMap,columns,dbsqlConValTypes);
        return null;
    }

    private void exeMultiUpdate(PreparedStatement updatePst, HashMap<String,String[]> conMap, String[] columns, DBSQLConValType[] dbsqlConValTypes) throws SQLException {
        int deleteCount=conMap.get(columns[0]).length;
        for(int index=0;index < deleteCount;index++){
            setParameter(updatePst,columns,dbsqlConValTypes,conMap,index);
            updatePst.execute();
        }
    }
}
