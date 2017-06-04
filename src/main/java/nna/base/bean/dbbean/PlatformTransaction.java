package nna.base.bean.dbbean;

import nna.base.bean.Clone;

import java.sql.Timestamp;

/**
 * for db table of transaction
 * @author NNA-SHUAI
 * @create 2017-05-13 17:20
 **/

public class PlatformTransaction extends Clone {
    private static final Long serialVersionUID=9L;

    private String transactionName;
    private String sqlId;
    private int sqlSequence;
    private Timestamp createTimestamp;
    private Timestamp updateTimestamp;
    private Integer successSequence;
    private Integer failSequence;
    private String sqlDesc;

    public PlatformTransaction(){

    }

    public String getTransactionName() {
        return transactionName;
    }

    public void setTransactionName(String transactionName) {
        this.transactionName = transactionName;
    }

    public String getSqlId() {
        return sqlId;
    }

    public void setSqlId(String sqlId) {
        this.sqlId = sqlId;
    }

    public Timestamp getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(Timestamp createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public Timestamp getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(Timestamp updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    public String getSqlDesc() {
        return sqlDesc;
    }

    public void setSqlDesc(String sqlDesc) {
        this.sqlDesc = sqlDesc;
    }

    public int getSqlSequence() {
        return sqlSequence;
    }

    public void setSqlSequence(int sqlSequence) {
        this.sqlSequence = sqlSequence;
    }

    public Integer getSuccessSequence() {
        return successSequence;
    }

    public void setSuccessSequence(int successSequence) {
        this.successSequence = successSequence;
    }

    public Integer getFailSequence() {
        return failSequence;
    }

    public void setFailSequence(int failSequence) {
        this.failSequence = failSequence;
    }
}
