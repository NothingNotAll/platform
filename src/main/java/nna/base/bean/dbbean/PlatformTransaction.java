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
    private int selectNextIndex0;
    private int updateNextIndex0;
    private int deleteNextIndex0;
    private int insertNextIndex0;
    private int exceptionNextIndex;
    private Boolean currentExceptionCommit;
    private Boolean previousExceptionCommit;
    private Timestamp createTimestamp;
    private Timestamp updateTimestamp;
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

    public int getSelectNextIndex0() {
        return selectNextIndex0;
    }

    public void setSelectNextIndex0(int selectNextIndex0) {
        this.selectNextIndex0 = selectNextIndex0;
    }

    public int getUpdateNextIndex0() {
        return updateNextIndex0;
    }

    public void setUpdateNextIndex0(int updateNextIndex0) {
        this.updateNextIndex0 = updateNextIndex0;
    }

    public int getDeleteNextIndex0() {
        return deleteNextIndex0;
    }

    public void setDeleteNextIndex0(int deleteNextIndex0) {
        this.deleteNextIndex0 = deleteNextIndex0;
    }

    public int getInsertNextIndex0() {
        return insertNextIndex0;
    }

    public void setInsertNextIndex0(int insertNextIndex0) {
        this.insertNextIndex0 = insertNextIndex0;
    }

    public int getExceptionNextIndex() {
        return exceptionNextIndex;
    }

    public void setExceptionNextIndex(int exceptionNextIndex) {
        this.exceptionNextIndex = exceptionNextIndex;
    }

    public Boolean getCurrentExceptionCommit() {
        return currentExceptionCommit;
    }

    public void setCurrentExceptionCommit(Boolean currentExceptionCommit) {
        this.currentExceptionCommit = currentExceptionCommit;
    }

    public Boolean getPreviousExceptionCommit() {
        return previousExceptionCommit;
    }

    public void setPreviousExceptionCommit(Boolean previousExceptionCommit) {
        this.previousExceptionCommit = previousExceptionCommit;
    }
}
