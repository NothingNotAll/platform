package nna.transaction;

import nna.MetaBean;
import nna.base.bean.dbbean.PlatformEntryTransaction;
import nna.base.bean.dbbean.PlatformSql;
import nna.base.bean.dbbean.PlatformTransaction;
import nna.enums.DBSQLConValType;

import java.util.ArrayList;

/**
 * For Transactions Meta Info
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

}
