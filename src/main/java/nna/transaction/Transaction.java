package nna.transaction;

import java.sql.SQLException;

/*
 * provided as the database operations helper interface for the application
 * if implements the interface, u must specify the result of sql's executing;
 * application layer can not implement the interface immediately but extends the AbstractTransaction class .
 *
 * For the performance of app :
 * the IO Time consuming in the Connection.preparedStatment
 * and
 * execute the db close method
 * and
 * transfer Java Object to Json
 * and
 * U must been warned that all of the IO limit operations;
 *
 * */
 public interface Transaction<V>{
	/*
	 * transaction means a group of sql that completes a complex of business;
	 * u must conf the transaction in the db with the help of web conf view or meta file;
	 * */
	V execTransaction(String transactionName) throws SQLException;
}
