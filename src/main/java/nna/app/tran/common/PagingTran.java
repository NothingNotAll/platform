package nna.app.tran.common;

import nna.Marco;
import nna.base.bean.combbean.CombTransaction;
import nna.base.bean.confbean.ConfMeta;
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
 * For Commong Paging tran
 *
 * @author NNA-SHUAI
 * @create 2017-06-04 20:32
 **/

public class PagingTran extends AbstractTransaction<Object>{


    @Override
    public Object execTransaction(String transactionName) throws SQLException{
        ConfMeta confMeta= ConfMetaSetFactory.getConfMeta();
        String tranNm=confMeta.getReqColumn().get(Marco.TRAN_NAME)[0];
        super.execTransaction(tranNm);
        return null;
    }

    public Object inTransaction(Connection connection, PreparedStatement[] sts) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
        PreparedStatement countPst=sts[0];
        PreparedStatement pagePst=sts[1];
        ConfMeta confMeta=ConfMetaSetFactory.getConfMeta();
        CombTransaction[] combTransactions=confMeta.getCombTransactions();
        CombTransaction pageTran=combTransactions[0];
        ArrayList<DBSQLConValType[]> dbsqlConValTypes=pageTran.getConditionValueTypes();
        ArrayList<String[]> conNmsList=pageTran.getConditions();
        ArrayList<String[]> columnsList=pageTran.getColumns();
        DBSQLConValType[] valTypes=dbsqlConValTypes.get(0);
        String[] conNms=conNmsList.get(1);
        String[] columns=columnsList.get(1);
        HashMap<String,String[]> reqMap=confMeta.getReqColumn();
        ResultSet countRs=setParameter(countPst,conNms,valTypes,reqMap);
        countRs.next();
        int totalCount=countRs.getInt(1);
        setPar(pagePst,columns,valTypes,reqMap,0);
        HashMap<String,String[]> rspMap=confMeta.getRspColumn();
        countRs.close();

        /*
        * 计算 分页 参数
        *
        * */

        countRs=setParameter(pagePst,conNms,valTypes,reqMap);
        setRspMap(totalCount,countRs,columns,rspMap,Marco.TOTALCOUNT);
        return null;
    }
}
