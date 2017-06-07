package nna.app.tran.common;

import nna.Marco;
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
        return null;
    }
    public Object inTransaction(Connection connection, PreparedStatement[] sts) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
        return null;
    }
}
