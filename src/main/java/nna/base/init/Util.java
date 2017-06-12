package nna.base.init;

import nna.Marco;
import nna.base.bean.dbbean.PlatformSql;
import nna.base.log.Log;
import nna.base.protocol.dispatch.AppUtil;
import nna.base.util.BuildSQL;

import java.sql.SQLException;

/**
 * @author NNA-SHUAI
 * @create 2017-06-11 20:18
 **/

 class Util {


     static String buildSQL(PlatformSql platformSql) throws SQLException {
        Log log= AppUtil.getLog();
        String sqlStr=null;
        String tableName=platformSql.getTableName();

        //SQL with primary key columns
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
            platformSql.setAppCondition(platformSql.getAppCondition()+","+ Marco.PAGE_BEGIN+","+Marco.PAGE_END);
        }

        log.log("initial "+platformSql.getSqlId()+" SQL:"+sqlStr,Log.INFO);
        return sqlStr;
    }
}
