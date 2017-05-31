package nna.base.bean.dbbean;

import nna.base.bean.Clone;

/**
 * @author NNA-SHUAI
 * @create 2017-05-19 9:29
 **/

public class PlatformClusterDevice extends Clone{
    private static final Long serialVersionUID=14L;

    private int clusterId;
    private int deviceId;

    public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
}
