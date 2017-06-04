package nna.app.tran.common;

import nna.Marco;
import nna.base.bean.confbean.ConfMeta;
import nna.base.protocol.dispatch.ConfMetaSetFactory;
import nna.transaction.AbstractTransaction;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * For Only One Transaction In The Service
 *
 * @author NNA-SHUAI
 * @create 2017-06-04 22:45
 **/

public class SingleTran extends AbstractTransaction<Object> {

    @Override
    public Object execTransaction(String transactionName) throws SQLException{
        ConfMeta confMeta= ConfMetaSetFactory.getConfMeta();
        String tranNm=confMeta.getReqColumn().get(Marco.TRAN_NAME)[0];
        super.execTransaction(tranNm);
        return null;
    }

    public Object inTransaction(Connection connection, PreparedStatement[] sts) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {

        return null;
    }
}
