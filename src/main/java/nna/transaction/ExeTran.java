package nna.transaction;

import nna.MetaBean;
import nna.base.bean.dbbean.PlatformEntryTransaction;
import nna.base.bean.dbbean.PlatformSql;
import nna.base.bean.dbbean.PlatformTransaction;
import nna.enums.DBSQLConValType;
import nna.enums.DBTranPpgType;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by NNA-SHUAI on 2017/7/8.
 */
public class ExeTran {
    private PlatformEntryTransaction serviceTran;
    private ArrayList<PlatformTransaction[]> trans;
    private ArrayList<PlatformSql[]> tranPlatformSql;
    private ArrayList<String[]> SQLS;
    private ArrayList<ArrayList<DBSQLConValType[]>> dbsqlConValTypes;
    private ArrayList<ArrayList<String[]>> cons;
    private ArrayList<ArrayList<String[]>> cols;
    private MetaBean metaBean;
    private Integer index;

    ExeTran(
            MetaBean metaBean,
            Integer index,
            PlatformEntryTransaction serviceTran,
            ArrayList<PlatformTransaction[]> trans,
            ArrayList<PlatformSql[]> tranPlatformSql,
            ArrayList<String[]> SQLS,
            ArrayList<ArrayList<DBSQLConValType[]>> dbsqlConValTypes,
            ArrayList<ArrayList<String[]>> cons,
            ArrayList<ArrayList<String[]>> cols
    ){
       this.metaBean=metaBean;
       this.index=index;
       this.serviceTran=serviceTran;
       this.trans=trans;
       this.tranPlatformSql=tranPlatformSql;
       this.SQLS=SQLS;
       this.dbsqlConValTypes=dbsqlConValTypes;
       this.cons=cons;
       this.cols=cols;
    }

    void execute() throws SQLException {
        if(index==0){
            executeFirst();
        }else{
            executeNonFirst();
        }
    }

    private void executeNonFirst() throws SQLException {

    }

    private void executeFirst() throws SQLException {
        Connection connection=metaBean.getDbCon().getCon();
        metaBean.getConStack().add(connection);
        DBTranPpgType dbTranPpgType=serviceTran.getTransactionPropagation();
        setConnectionAutoCommit(connection,dbTranPpgType);
    }

    private void setConnectionAutoCommit(Connection connection, DBTranPpgType dbTranPpgType) {
        switch (dbTranPpgType){
            case NONE:
                ;
                break;
            case PROPAGATION_NOT_SUPPORTED:
                ;
                break;
            case PROPAGATION_REQUIRES_NEW:
                ;
                break;
            case PROPAGATION_MANDATORY:
                ;
                break;
            case PROPAGATION_SUPPORTS:
                ;
                break;
            case PROPAGATION_REQUIRED:
                ;
                break;
            case PROPAGATION_NESTED:
                ;
                break;
            case PROPAGATION_NEVER:
                ;
                break;
            case DEFAULT:
                ;
                break;
        }
    }
}
