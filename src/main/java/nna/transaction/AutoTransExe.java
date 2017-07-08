package nna.transaction;

import nna.MetaBean;
import nna.base.bean.dbbean.PlatformEntryTransaction;
import nna.base.db.DBCon;
import nna.enums.DBTranLvlType;
import nna.enums.DBTranPpgType;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by NNA-SHUAI on 2017/7/5.
 */
public class AutoTransExe implements TransExecutor {
    public Object executeTransactions(MetaBean metaBean) throws SQLException {
        PlatformEntryTransaction[] servTrans=metaBean.getServiceTrans();
        PlatformEntryTransaction serviceTran;
        Integer servTranCount=servTrans.length;
        for(int index=0;index < servTranCount;index++){
            serviceTran=servTrans[index];

        }
        return null;
    }
}
