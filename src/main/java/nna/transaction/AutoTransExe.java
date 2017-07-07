package nna.transaction;

import nna.MetaBean;
import nna.base.bean.dbbean.PlatformEntryTransaction;

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
            try{
                executeServiceTran(metaBean,serviceTran,index);
            }catch (Exception e){
                e.fillInStackTrace();
                return null;
            }finally {

            }
        }
        return null;
    }

    private void executeServiceTran(MetaBean metaBean, PlatformEntryTransaction serviceTran, int index) {
        
    }
}
