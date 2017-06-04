package nna.app.tran.common;

import nna.transaction.AbstractTransaction;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * For Commong Paging tran
 *
 * @author NNA-SHUAI
 * @create 2017-06-04 20:32
 **/

public class PagingTran extends AbstractTransaction<Object>{


    public Object inTransaction(Connection connection, PreparedStatement[] sts) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {

        return null;
    }
}
