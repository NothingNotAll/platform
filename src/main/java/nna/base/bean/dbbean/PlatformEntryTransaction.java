package nna.base.bean.dbbean;

import nna.base.bean.Clone;
import nna.enums.DBTranLvlType;
import nna.enums.DBTranPpgType;

import java.sql.Timestamp;

/**
 * for db table of service transaction
 * @author NNA-SHUAI
 * @create 2017-05-13 17:15
 **/

public class PlatformEntryTransaction extends Clone {
    private static final Long serialVersionUID=7L;

    private String transactionEntryName;
    private String transactionName;
    private int serviceTransactionSequence;
    private Integer previousTransactionIndex;
    private DBTranPpgType transactionPropagation;
    private DBTranLvlType transactionLevel;
    private Boolean currentExceptionCommit;
    private Boolean previousExceptionCommit;
    private Timestamp createTimestamp;
    private Timestamp updateTimestamp;
    private String transactionDesc;

    public PlatformEntryTransaction(){

    }

    public String getTransactionName() {
        return transactionName;
    }

    public void setTransactionName(String transactionName) {
        this.transactionName = transactionName;
    }

    public Integer getPreviousTransactionIndex() {
        return previousTransactionIndex;
    }

    public void setPreviousTransactionIndex(Integer previousTransactionIndex) {
        this.previousTransactionIndex = previousTransactionIndex;
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

    public String getTransactionDesc() {
        return transactionDesc;
    }

    public void setTransactionDesc(String transactionDesc) {
        this.transactionDesc = transactionDesc;
    }

    public int getServiceTransactionSequence() {
        return serviceTransactionSequence;
    }

    public void setServiceTransactionSequence(int serviceTransactionSequence) {
        this.serviceTransactionSequence = serviceTransactionSequence;
    }

    public DBTranPpgType getTransactionPropagation() {
        return transactionPropagation;
    }

    public void setTransactionPropagation(DBTranPpgType transactionPropagation) {
        this.transactionPropagation = transactionPropagation;
    }

    public DBTranLvlType getTransactionLevel() {
        return transactionLevel;
    }

    public void setTransactionLevel(DBTranLvlType transactionLevel) {
        this.transactionLevel = transactionLevel;
    }

    public String getTransactionEntryName() {
        return transactionEntryName;
    }

    public void setTransactionEntryName(String transactionEntryName) {
        this.transactionEntryName = transactionEntryName;
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
