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
        ResultSet rs=setParameterAndExe(countPst,conNms,valTypes,reqMap);
        rs.next();
        int totalCount=rs.getInt(1);
        setPar(pagePst,columns,valTypes,reqMap,0);
        HashMap<String,String[]> rspMap=confMeta.getRspColumn();
        rs.close();
        /*
        * 计算 分页 参数
        *
        * */
        Integer[] pageParams=getPageParam(totalCount,
                Integer.valueOf(reqMap.get(Marco.CURRENT_PAGE)[0]),
                Integer.valueOf(reqMap.get(Marco.PAGE_SIZE)[0]),
                Integer.valueOf(reqMap.get(Marco.PAGE_FLAG)[0]));
        reqMap.put(Marco.PAGE_BEGIN,new String[]{String.valueOf(pageParams[0])});
        reqMap.put(Marco.PAGE_END,new String[]{String.valueOf(pageParams[1])});
        rs=setParameterAndExe(pagePst,conNms,valTypes,reqMap);
        setRsReverse(pageParams[0], pageParams[0], pageParams[0],rs);
        setRspMap(totalCount,rs,columns,rspMap,Marco.TOTALCOUNT);
        rs.close();
        countPst.close();
        pagePst.close();
        return null;
    }
}
