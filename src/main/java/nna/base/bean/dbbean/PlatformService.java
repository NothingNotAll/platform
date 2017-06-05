package nna.base.bean.dbbean;

import nna.base.bean.Clone;
import nna.enums.ServiceMethodType;

import java.sql.Timestamp;

/**
 * for db table of service 
 * @author NNA-SHUAI
 * @create 2017-05-13 16:52
 **/

public class PlatformService extends Clone {
    private static final Long serialVersionUID=6L;

    private String serviceName;
    private boolean status;
    private boolean serviceLogEncrpt;
    private int serviceLogLevel;
    private String serviceClass;
    private ServiceMethodType serviceMethodType;
    private int serviceTempsize;
    private Timestamp createTimestamp;
    private Timestamp updateTimestamp;
    private String serviceDesc;


    public PlatformService(){

    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(String serviceClass) {
        this.serviceClass = serviceClass;
    }

    public Timestamp getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(Timestamp createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public String getServiceDesc() {
        return serviceDesc;
    }

    public void setServiceDesc(String serviceDesc) {
        this.serviceDesc = serviceDesc;
    }

    public Timestamp getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(Timestamp updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    public int getServiceLogLevel() {
        return serviceLogLevel;
    }

    public void setServiceLogLevel(int serviceLogLevel) {
        this.serviceLogLevel = serviceLogLevel;
    }

    public boolean isServiceLogEncrpt() {
        return serviceLogEncrpt;
    }

    public void setServiceLogEncrpt(boolean serviceLogEncrpt) {
        this.serviceLogEncrpt = serviceLogEncrpt;
    }

    public int getServiceTempsize() {
        return serviceTempsize;
    }

    public void setServiceTempsize(int serviceTempsize) {
        this.serviceTempsize = serviceTempsize;
    }

    public ServiceMethodType getServiceMethodType() {
        return serviceMethodType;
    }

    public void setServiceMethodType(ServiceMethodType serviceMethodType) {
        this.serviceMethodType = serviceMethodType;
    }
}
