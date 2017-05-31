package nna.base.bean.dbbean;

import nna.base.bean.Clone;
import nna.enums.DevelopType;

import java.sql.Timestamp;

/**
 * for db table of service update version
 * @author NNA-SHUAI
 * @create 2017-05-13 16:30
 **/

public class PlatformDevelopVersion extends Clone {
    private static final Long serialVersionUID=17L;

    private String updateService;
    private int updateVersion;
    private DevelopType updateType;
    private int updateUserid;
    private Timestamp updateTimestamp;
    private String updateDesc;

    public PlatformDevelopVersion(){

    }

    public String getUpdateService() {
        return updateService;
    }

    public void setUpdateService(String updateService) {
        this.updateService = updateService;
    }

    public int getUpdateVersion() {
        return updateVersion;
    }

    public void setUpdateVersion(int updateVersion) {
        this.updateVersion = updateVersion;
    }

    public Timestamp getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(Timestamp updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    public DevelopType getUpdateType() {
        return updateType;
    }

    public void setUpdateType(DevelopType updateType) {
        this.updateType = updateType;
    }

    public String getUpdateDesc() {
        return updateDesc;
    }

    public void setUpdateDesc(String updateDesc) {
        this.updateDesc = updateDesc;
    }

    public int getUpdateUserid() {
        return updateUserid;
    }

    public void setUpdateUserid(int updateUserid) {
        this.updateUserid = updateUserid;
    }
}
