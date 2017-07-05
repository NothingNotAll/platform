package nna.transaction;

import nna.MetaBean;

import java.sql.SQLException;

/*
 * provided as the database operations helper interface for the application
 * if implements the interface, u must specify the result of sql's executing;
 * application layer can not implement the interface immediately but extends the DefaultTransExecutor class .
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
 * in the future,
 * we want to cancel the interface , and
 * solve the complex transaction by config only,
 * and the platform complete all the select update insert and
 * keep the response data to the map.
 * as that, we don't write code any more,
 * just do config work.
 *
 * */
 public interface TransExecutor<V>{
	/*
	 * transaction means a group of sql that completes a complex of business;
	 * u must conf the transaction in the db with the help of app conf view or meta file;
	 * */
	V executeTransactions(MetaBean metaBean) throws SQLException;
}
