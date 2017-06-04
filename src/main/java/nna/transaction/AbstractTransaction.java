package nna.transaction;

import nna.Marco;
import nna.base.bean.Clone;
import nna.base.bean.combbean.CombDB;
import nna.base.bean.confbean.ConfMeta;
import nna.base.bean.dbbean.PlatformServiceTransaction;
import nna.base.bean.dbbean.PlatformSql;
import nna.base.cache.CacheFactory;
import nna.base.cache.NNAServiceInit0;
import nna.base.protocol.dispatch.AppUtil;
import nna.base.log.Log;
import nna.base.util.List;
import nna.base.db.DBCon;
import nna.base.protocol.dispatch.ConfMetaSetFactory;
import nna.base.bean.combbean.CombTransaction;
import nna.base.util.BuildSQL;
import nna.base.util.LogUtil;
import nna.base.util.ObjectFactory;
import nna.enums.DBSQLConValType;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;


public abstract class AbstractTransaction<V> implements Transaction<V> {

    protected void setRspMap(int count,ResultSet rs,String[] column,HashMap<String,String[]> rspMap,String countNm) throws SQLException {
        int index=0;
        int colCount=column.length;
        String rspColNm;
        for(;index<colCount;index++){
            rspColNm=column[index];
            rspMap.put(rspColNm,new String[count]);
        }
        index=0;
        while(rs.next()){
            int temp=0;
            String colNm;
            for(int colIndex=1;colIndex<= colCount;colIndex++,temp++){
                colNm=column[temp];
                rspMap.get(colNm)[index]=rs.getString(colIndex);
            }
            index++;
        }
        rspMap.put(countNm,new String[]{String.valueOf(index)});
        rs.close();
    }

    protected ResultSet setParameterAndExe(PreparedStatement pst,String[] conNms,DBSQLConValType[] dbsqlConValTypes,HashMap<String,String[]> conMap) throws SQLException {
        setPar(pst,conNms,dbsqlConValTypes,conMap,0);
        return pst.executeQuery();
    }

    protected void setPar(PreparedStatement pst,String[] conNms,DBSQLConValType[] dbsqlConValTypes,HashMap<String,String[]> conMap,int valIndex) throws SQLException {
        int parameterCount=conNms.length;
        String tempNm;
        DBSQLConValType tempValType;
        String[] conVal;
        String conValue;
        for(int index=0;index < parameterCount;index++){
            tempNm=conNms[index];
            conVal=conMap.get(tempNm);
            conValue=conVal[valIndex];
            tempValType=dbsqlConValTypes[index];
            switch (tempValType){
                case INTEGER:
                    pst.setInt(index,Integer.valueOf(conValue));
                    break;
                case BOOLEAN:
                    pst.setBoolean(index,Boolean.valueOf(conValue));;
                    break;
                case STRING:
                    pst.setString(index,conValue);;
                    break;
            }
        }
    }

    public abstract V inTransaction(Connection connection,PreparedStatement[] sts) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException;

	public V execTransaction(String transactionName) throws SQLException{
        V v = null;
	    ConfMeta confMeta = ConfMetaSetFactory.getConfMeta();
        CombDB combDB= confMeta.getCombDB();
        DBCon dbCon=combDB.getDbCon();
	    Log log= confMeta.getLog();

	    ArrayList<CombTransaction> tranStack=confMeta.getTranStack();
        ArrayList<Connection> conStack=confMeta.getConStack();
        ArrayList<PreparedStatement[]> pstStack=confMeta.getPstStack();

        int tranStackSize=tranStack!=null?tranStack.size():0;
        int conStackSize=conStack!=null?conStack.size():0;
        int pstStackSize=pstStack!=null?pstStack.size():0;

        CombTransaction currentTransaction=getCurrentTransaction(confMeta,transactionName,log);
        tranStack.add(currentTransaction);
        tranStackSize++;
        confMeta.setCurrentCombTransaction(currentTransaction);
        PlatformServiceTransaction transaction=currentTransaction.getTransaction();

        int previousIndex =transaction.getPreviousTransactionIndex();
        CombTransaction previousTransaction=tranStackSize==0?null:tranStack.get(previousIndex);
        Connection connection=conStackSize==0?dbCon.getCon():confMeta.getConStack().get(previousIndex);

        String[] SQLS=currentTransaction.getSqls();
        PlatformSql[] platformSqls=currentTransaction.getPlatformSqls();
        PreparedStatement[] sts=null;

        try{
            //要根据事务的配置判断是否使用嵌套事务的数据库连接或者新建连接
            sts=initSts(currentTransaction,connection,platformSqls,SQLS,log);
            pstStack.add(sts);
            pstStackSize++;
            confMeta.setCurrentPsts(sts);
            connection=setPropgAndTranLvl(dbCon,previousTransaction,currentTransaction);
            conStack.add(connection);
            conStackSize++;
            v=inTransaction(connection,sts);
            return v;
        }catch(Exception e){
	        e.printStackTrace();
        }finally {
            //commit;
            commit(connection);
            dbCon.putCon(connection);
            tranStack.remove(tranStackSize-1);
            conStack.remove(conStackSize-1);
            pstStack.remove(pstStackSize-1);
            close(connection,sts);
        }
        return v;
	}

    private void commit(Connection connection) {

    }

    private static Connection setPropgAndTranLvl(DBCon dbCon,CombTransaction previousTransacton, CombTransaction currentTransaction) throws SQLException {
        Connection connection=null;
        PlatformServiceTransaction transaction=currentTransaction.getTransaction();
        switch (transaction.getTransactionPropagation()){
            case DEFAULT:
                setTranLvl(previousTransacton,currentTransaction,false);
                break;
            case PROPAGATION_NOT_SUPPORTED:
                connection.setAutoCommit(true);
                break;
            case PROPAGATION_REQUIRES_NEW:
                connection=dbCon.getCon();
                setTranLvl(previousTransacton,currentTransaction,false);
                break;
            case PROPAGATION_MANDATORY:
                break;
            case PROPAGATION_SUPPORTS:
                break;
            case PROPAGATION_REQUIRED:
                break;
            case PROPAGATION_NESTED:
                setTranLvl(previousTransacton,currentTransaction,true);
                break;
            case PROPAGATION_NEVER:
                connection=dbCon.getCon();
                break;
            case NONE:
                break;
        }
        return connection;
    }

    private static void setTranLvl(CombTransaction previousTransacton, CombTransaction currentTransaction,boolean isCarePrevious) throws SQLException {
        PlatformServiceTransaction transaction=currentTransaction.getTransaction();
        PlatformServiceTransaction previous=previousTransacton.getTransaction();
        Connection connection = null;
	    switch (transaction.getTransactionLevel()){
            case NONE:
                break;
            case SERIALIZABLE:
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                break;
            case READ_COMMITTED:
                if(isCarePrevious){
                    switch (previous.getTransactionLevel()){
                        case SERIALIZABLE:
                            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                    }
                }else{
                    connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                }
                break;
            case REPEATABLE_READ:
                if(isCarePrevious){
                    switch (previous.getTransactionLevel()){
                        case SERIALIZABLE:
                            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                            break;
                        default:
                            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
                    }
                }else{
                    connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
                }
                break;
            case READ_UNCOMMITTED:
                if(isCarePrevious){
                    switch (previous.getTransactionLevel()){
                        case SERIALIZABLE:
                            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                            break;
                        case READ_UNCOMMITTED:
                            connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                            break;
                        case REPEATABLE_READ:
                            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
                            break;
                        case READ_COMMITTED:
                            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                            break;
                    };
                }else {
                    connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                }
        }
	}

    private void close(Connection connection, PreparedStatement[] sts) throws SQLException {
        int size=sts.length;
        PreparedStatement preparedStatement=null;
        for(int index=0;index < size; index++){
            try{
                preparedStatement=sts[index];
                if(preparedStatement!=null&&!preparedStatement.isClosed()){
                    preparedStatement.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {

            }
        }
	    if(connection!=null&!connection.isClosed()){

        }
    }
    protected void close(ResultSet rs) throws SQLException {
	    if(rs!=null&!rs.isClosed()){
	        rs.close();
        }
    }
    protected void close(ResultSet[] rss) throws SQLException {
        int length=rss.length;
	    for(int index=0;index < length;index++){
	        try{
                if(rss[index]!=null&!rss[index].isClosed()){
                    rss[index].close();
                }
            }catch (Exception e){
               e.printStackTrace();
            }
        }
    }
    protected static void setCurrentPstParameter(int sqlIndex) throws SQLException {
        ConfMeta intimeConfMeta = ConfMetaSetFactory.getConfMeta();
        CombTransaction current=intimeConfMeta.getCurrentCombTransaction();
        String[] conditionList=current.getConditions().get(sqlIndex);
        int size=conditionList.length;
        PreparedStatement currentPst=intimeConfMeta.getCurrentPsts()[sqlIndex];
        HashMap<String,String[]> map=intimeConfMeta.getReqColumn();
        DBSQLConValType[] arrayList=current.getConditionValueTypes().get(sqlIndex);
        for(int index=1;index <= size; index++){
            switch (arrayList[index]){
                case STRING:
                    currentPst.setString(index,map.get(conditionList[index])[0]);
                    break;
                case BOOLEAN:
                    currentPst.setBoolean(index,Boolean.valueOf(map.get(conditionList[index])[0]));
                    break;
                case INTEGER:
                    currentPst.setInt(index,Integer.valueOf(map.get(conditionList[index])[0]));
            }
        }
    }

    private static PreparedStatement[] initSts(CombTransaction combTransaction,Connection connection,PlatformSql[] platformSqls, String[] sqls,Log log) throws SQLException {
        int size=sqls.length;
	    PlatformSql platformSql;
	    String sql;
	    PreparedStatement[] sts=new PreparedStatement[size];
	    for(int index=0;index < size;index++){
	        platformSql=platformSqls[index];
	        sql=sqls[index];
	        log.log("No."+index+"-"+sql,Log.INFO);
	        switch (platformSql.getOpertype()){
                case PROCEDURE:
                    sts[index]=connection.prepareCall(sql);
                    break;
                default:
                    sts[index]=connection.prepareStatement(sql);
            }

        }
        return sts;
    }

    private static CombTransaction getCurrentTransaction(ConfMeta confMeta, String transactionName, Log log) {
        HashMap<String,CombTransaction> map= confMeta.getCombTransactionMap();
        CombTransaction combTransaction=map.get(transactionName);
        PlatformServiceTransaction platformServiceTransaction=combTransaction.getTransaction();
        log.log("No."+platformServiceTransaction.getServiceTransactionSequence()+"开始执行",Log.INFO);
        LogUtil.log(log,platformServiceTransaction,Log.INFO);
        return combTransaction;
    }

    protected static ResultSet[] initialResutSet(PreparedStatement[] psts) throws SQLException{
		int count=psts.length;
		ResultSet[] rs=new ResultSet[count];
		for(int index=0;index<count;index++){
			rs[index]=psts[index].executeQuery();
		}
		return rs;
	}

	protected static void setParameter(PreparedStatement pst,String[] parameterValues) throws SQLException{
		int count=parameterValues.length;
		for(int index=0;index < count;index++){
			pst.setString(index+1, parameterValues[index]);
		}
	}

	protected static Integer[] getPageParam(Integer totalLines,
                                            Integer currentPage,
                                            Integer pageSize,
                                            Integer pageFlag) throws SQLException{
		Integer totalPage=totalLines/pageSize;
		if(totalPage==0){
			if(totalLines > 0){
				totalPage=1;
			}
		}else{
			if(totalLines%pageSize>0){
				totalPage+=1;
			}
		}
		AppUtil.putTemp(Marco.TOTALPAGE, new String[]{totalPage.toString()});
		Integer begin=(currentPage-1)*pageSize;
		Integer end=(currentPage)*pageSize;
		return new Integer[]{begin,end,pageFlag};
	}

	protected static void setRsReverse(Integer totalPage,
                                         Integer currentPage,
                                         Integer pageFlag,
                                         ResultSet rs) throws SQLException {
		boolean isNeed=false;
	    switch (pageFlag) {
		case 0://首页
		case 1://末页
		case 2://上一页
			Integer goalPage=currentPage-1;
			Integer middle=totalPage/2;
            isNeed= goalPage>middle;
		case 3://下一页
			Integer nextPage=currentPage+1;
			Integer middle1=totalPage/2;
            isNeed= nextPage>middle1;
		case 4://当前页
			Integer middle11=totalPage/2;
            isNeed= currentPage>middle11;
		default:
			break;
		}
		if(isNeed){
            rs.setFetchDirection(ResultSet.FETCH_REVERSE);
        }
	}

    private static String arrayToString(String[] strs,String spit){
        int size=strs.length;
        StringBuilder stringBuilder=new StringBuilder("");
        for(int index=0;index < size;index++){
            if(stringBuilder.equals("")){
                stringBuilder.append(strs[index]);
            }else{
                stringBuilder.append(","+strs[index]);
            }
        }
        return stringBuilder.toString();
    }

    public static String buildSQL(PlatformSql platformSql) throws SQLException {
        Log log= AppUtil.getLog();
        String sqlStr=null;
        String tableName=platformSql.getTableName();

        if(platformSql.getSqlId().startsWith("del_pri")||platformSql.getSqlId().startsWith("upd_pri")||platformSql.getSqlId().startsWith("sel_pri")){
            platformSql.setDbCondition(BuildSQL.getTablePrimaryKey(NNAServiceInit0.init(),tableName));
        }

        if(platformSql.getDbCondition()!=null&&!platformSql.getDbCondition().trim().equals("")&&(platformSql.getAppCondition()==null||platformSql.getAppCondition().trim().equals(""))){
            platformSql.setAppCondition(BuildSQL.getJavaColFromDBCol(platformSql.getDbCondition()));
        }

        if(!platformSql.getSqlId().startsWith("del")&&(platformSql.getDbColumn()==null||platformSql.getDbColumn().trim().equals(""))){
            platformSql.setDbColumn(BuildSQL.getTableCols(NNAServiceInit0.init(),tableName));
            platformSql.setAppColumn(BuildSQL.getJavaColFromDBCol(platformSql.getDbColumn()));
        }

        String dbColumn = platformSql.getDbColumn();
        String dbCondition= platformSql.getDbCondition();
        switch (platformSql.getOpertype()) {
            case GS:
                sqlStr= BuildSQL.selectNoCondition(dbColumn.split("[,]"), tableName);
                break;
            case GU:
                sqlStr=BuildSQL.updateNoCondition(dbColumn.split("[,]"), tableName);
                break;
            case GD:
                sqlStr=BuildSQL.deleteNoCondition(tableName);
                break;
            case D:
                sqlStr=BuildSQL.deleteWithCondition(
                        tableName,
                        dbCondition.split("[,]"),
                        platformSql.getCompareKv().split("[,]"),
                        platformSql.getLogicKk().split("[,]"));
                break;
            case S:
                if (platformSql.getLogicKk()==null||platformSql.getCompareKv()==null||"".equals(platformSql.getLogicKk().trim())||"".equals(platformSql.getCompareKv().trim())) {
                    sqlStr=BuildSQL.selectWithCondition(
                            dbColumn.split("[,]"),
                            tableName, dbCondition.split("[,]"));
                }else {
                    sqlStr=BuildSQL.selectWithCondition(
                            dbColumn.split("[,]"),
                            tableName,
                            dbCondition.split("[,]"),
                            platformSql.getCompareKv().split("[,]"),
                            platformSql.getLogicKk().split("[,]"));
                }
                break;
            case U:
                BuildSQL.updateWithCondition(
                        dbColumn.split("[,]"),
                        dbCondition.split("[,]"),
                        platformSql.getCompareKv().split("[,]"),
                        platformSql.getLogicKk().split("[,]"),
                        tableName);
                break;
            case I:
                sqlStr=BuildSQL.insert(
                        platformSql.getDbColumn(),
                        tableName);
                break;
            case MS:
                sqlStr=platformSql.getSqlstr()
                ;
                break;
            case PROCEDURE:
                sqlStr=platformSql.getSqlstr();
                break;
        }

        if (platformSql.getGroupOrder()!=null&&!"".equals(platformSql.getGroupOrder().trim())) {
            sqlStr+=" ";
            sqlStr+=platformSql.getGroupOrder();
            sqlStr+=" ";
        }

        if(platformSql.isPage()){
            sqlStr=BuildSQL.buildPageSQL(sqlStr);
            platformSql.setAppCondition(platformSql.getAppCondition()+","+Marco.PAGE_BEGIN+","+Marco.PAGE_END);
        }

        log.log("initial "+platformSql.getSqlId()+" SQL:"+sqlStr,Log.INFO);
        return sqlStr;
    }

    public static Clone getBean(ResultSet rs, int serialVersionUID) throws SQLException,
            IllegalAccessException,
            InstantiationException,
            ClassNotFoundException,
            InvocationTargetException {
//        Long start=System.currentTimeMillis();
        List<ObjectFactory> cache=CacheFactory.getObjectFactoryCache();
        ObjectFactory objectFactory = cache.get(serialVersionUID);
        Clone object=objectFactory.getClone();
        object=object.clone();
        int length=objectFactory.getFieldsCount();
        //from the index 1 and discard the serial id
        for(int index=1;index < length;index++){
            objectFactory.setFieldValueV2(rs,index,object);
        }
//        Long end=System.currentTimeMillis()-start;
//        System.out.println(object.getClass().getCanonicalName()+"数据库操作耗费时间："+end+"L");
        return object;
    }

    public static Clone[] getBeans(PreparedStatement pst,int serialVersionUID) throws SQLException,
            ClassNotFoundException,
            InvocationTargetException,
            InstantiationException,
            IllegalAccessException {
        ArrayList<Clone> clones=new ArrayList<Clone>();
        ResultSet rs=pst.executeQuery();
        Clone clone;
        while(rs.next()){
            clone=getBean(rs,serialVersionUID);
            clones.add(clone);
        }
        return clones.toArray(new Clone[0]);
    }

    public static boolean saveBean(PreparedStatement pst, int serialVersionUID) throws SQLException {
        List<ObjectFactory> container=CacheFactory.getObjectFactoryCache();
        ObjectFactory objectFactory = container.get(serialVersionUID);
        int length=objectFactory.getFieldsCount();
        Clone object=objectFactory.getClone();
        for(int index=1;index <= length;index++){
            objectFactory.setParameterV2(pst,index,object);
        }
        return pst.executeUpdate() == 1;
    }

    public static void saveBean(PreparedStatement pst, Collection<Object> collection, int serialVersionUID) throws SQLException {
        Iterator<Object> iterator=collection.iterator();
        Object object;
        while(iterator.hasNext()){
            object=iterator.next();
            if(!saveBean(pst,serialVersionUID)){
                throw new SQLException("update failed !");
            }
        }
    }
}
