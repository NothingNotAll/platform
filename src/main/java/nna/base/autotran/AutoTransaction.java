package nna.base.autotran;

import java.sql.SQLException;

/**
 * auto execute the transaction base on the service's config
 *
 * @author NNA-SHUAI
 * @create 2017-2017/6/7-13:09
 */
 interface AutoTransaction {
     void autoTran() throws SQLException;
}
