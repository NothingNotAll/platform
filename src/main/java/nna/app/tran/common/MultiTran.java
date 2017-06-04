package nna.app.tran.common;

import nna.base.bean.combbean.CombTransaction;
import nna.base.bean.confbean.ConfMeta;
import nna.base.protocol.dispatch.ConfMetaSetFactory;
import nna.transaction.AbstractTransaction;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * For Common Multi Tran
 *
 * @author NNA-SHUAI
 * @create 2017-06-04 21:08
 **/

public class MultiTran extends AbstractTransaction<Object>{

    @Override
    public Object execTransaction(String transactionName) throws SQLException{
        ConfMeta confMeta= ConfMetaSetFactory.getConfMeta();
        CombTransaction[] combTransactions=confMeta.getCombTransactions();
        CombTransaction tempTran;
        int tranCount=combTransactions.length;
        for(int index=0;index < tranCount;index++){
            tempTran=combTransactions[index];
        }
        return null;
    }

    public Object inTransaction(Connection connection, PreparedStatement[] sts) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
        return null;
    }
}
