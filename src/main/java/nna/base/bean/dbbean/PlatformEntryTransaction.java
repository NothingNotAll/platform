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

    private String transactions;
    private String transactionName;
    private int serviceTransactionSequence;
    private int previousTransactionIndex;
    private DBTranPpgType transactionPropagation;
    private DBTranLvlType transactionLevel;
    private Timestamp createTimestamp;
    private Timestamp updateTimestamp;
    private Integer failIndex;
    private String transactionDesc;

    public PlatformEntryTransaction(){

    }

    public String getTransactionName() {
        return transactionName;
    }

    public void setTransactionName(String transactionName) {
        this.transactionName = transactionName;
    }

    public int getPreviousTransactionIndex() {
        return previousTransactionIndex;
    }

    public void setPreviousTransactionIndex(int previousTransactionIndex) {
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

    public Integer getFailIndex() {
        return failIndex;
    }

    public void setFailIndex(Integer failIndex) {
        this.failIndex = failIndex;
    }

    public String getTransactions() {
        return transactions;
    }

    public void setTransactions(String transactions) {
        this.transactions = transactions;
    }
}
