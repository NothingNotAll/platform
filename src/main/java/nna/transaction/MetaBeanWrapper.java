package nna.transaction;

import nna.MetaBean;
import nna.base.bean.dbbean.PlatformEntryTransaction;
import nna.base.bean.dbbean.PlatformSql;
import nna.base.bean.dbbean.PlatformTransaction;
import nna.base.db.DBCon;
import nna.enums.DBSQLConValType;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * For DefaultTransaction Meta Info
 *
 * @author NNA-SHUAI
 * @create 2017-06-07 17:33
 **/

public class MetaBeanWrapper {

    private MetaBean metaBean;

    public MetaBeanWrapper(MetaBean metaBean){
        this.metaBean=metaBean;
    }

    public PlatformEntryTransaction[] getServiceTrans() {
        return metaBean.getServiceTrans();
    }

    public ArrayList<PlatformTransaction[]> getTrans() {
        return metaBean.getTrans();
    }

    public ArrayList<String[]> getSQLS() {
        return metaBean.getSQLS();
    }
    public ArrayList<ArrayList<DBSQLConValType[]>> getDbsqlConValTypes() {
        return metaBean.getDbsqlConValTypes();
    }

    public ArrayList<ArrayList<String[]>> getCons() {
        return metaBean.getCons();
    }

    public ArrayList<PlatformSql[]> getTranPlatformSql() {
        return metaBean.getTranPlatformSql();
    }

    public ArrayList<ArrayList<String[]>> getCols() {
        return metaBean.getCols();
    }

    public HashMap<String, String[]> getReq() {
        return metaBean.getReq();
    }

    public HashMap<String, String[]> getRsp() {
        return metaBean.getRsp();
    }

    public DBCon getDbCon() {
        return metaBean.getDbCon();
    }

    public ArrayList<Connection> getConStack() {
        return metaBean.getConStack();
    }

    public ArrayList<PlatformEntryTransaction> getTranStack() {
        return metaBean.getTranStack();
    }

    public void setCurrentCon(Connection currentCon) {
        metaBean.setCurrentCon(currentCon);
    }
}
