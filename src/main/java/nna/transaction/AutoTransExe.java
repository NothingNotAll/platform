package nna.transaction;

import nna.base.bean.dbbean.PlatformEntryTransaction;
import nna.enums.DBTranLvlType;
import nna.enums.DBTranPpgType;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * auto tran to exe avoid write if else business codes
 *
 * @author NNA-SHUAI
 * @create 2017-06-28 16:39
 **/

public class AutoTransExe implements TransExecutor {

    public Object executeTransactions(MetaBeanWrapper metaBeanWrapper) throws SQLException {
        PlatformEntryTransaction[] srvTrans=metaBeanWrapper.getServiceTrans();
        ArrayList<PlatformEntryTransaction> etStack=metaBeanWrapper.getTranStack();
        int index=0;
        int etCount=srvTrans.length;
        PlatformEntryTransaction temp;
        PlatformEntryTransaction previous = null;
        for(;index < etCount;index++){
            temp=srvTrans[index];
            etStack.add(temp);
            if(index!=0&&temp.getPreviousTransactionIndex()!=null){
                previous=etStack.get(index);
            }
            try{
                index=executePlatformEntryTransaction(metaBeanWrapper,previous,temp,index);
            }catch (Exception e){
                e.printStackTrace();

                break;
            }
        }
        return null;
    }

    private int executePlatformEntryTransaction(MetaBeanWrapper metaBeanWrapper,
                                                PlatformEntryTransaction previousET,
                                                PlatformEntryTransaction temp,
                                                int index)throws SQLException {
        DBTranPpgType previousDTPT=previousET.getTransactionPropagation();
        DBTranLvlType previousDLT=previousET.getTransactionLevel();
        DBTranPpgType dbTranPpgType=temp.getTransactionPropagation();
        DBTranLvlType dbTranLvlType=temp.getTransactionLevel();

        return 0;
    }
}
