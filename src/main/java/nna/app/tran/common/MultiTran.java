package nna.app.tran.common;

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

    public Object inTransaction(Connection connection, PreparedStatement[] sts) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
        return null;
    }
}
