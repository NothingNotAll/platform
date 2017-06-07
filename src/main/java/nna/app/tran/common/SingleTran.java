package nna.app.tran.common;

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

    public Object inTransaction(Connection connection, PreparedStatement[] sts) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
        return null;
    }

    @Override
    public Object execTransaction(String transactionName) throws SQLException{
        return null;
    }

}
