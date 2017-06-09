package nna.transaction;

import java.sql.*;


public class AbstractTransaction<V> implements Transaction<V> {


	public V execTransaction(String transactionName) throws SQLException{
        return null;
	}

    public V execTransaction(MetaBeanWrapper metaBeanWrapper) throws SQLException {
        return null;
    }
}
