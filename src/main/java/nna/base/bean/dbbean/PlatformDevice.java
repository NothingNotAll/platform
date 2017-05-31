package nna.base.bean.dbbean;

import nna.base.bean.Clone;
import nna.enums.DeviceType;

/**
 * @author NNA-SHUAI
 * @create 2017-05-19 9:29
 **/

public class PlatformDevice extends Clone{
    private static final Long serialVersionUID=13L;

    private int deviceId;
    private int deviceOid;
    private DeviceType deviceType;
    private boolean deviceStatus;
    private String deviceEn;
    private String deviceCh;
    private String deviceIp;
    private String devicePort;
    private String deviceDesc;

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceEn() {
        return deviceEn;
    }

    public void setDeviceEn(String deviceEn) {
        this.deviceEn = deviceEn;
    }

    public String getDeviceCh() {
        return deviceCh;
    }

    public void setDeviceCh(String deviceCh) {
        this.deviceCh = deviceCh;
    }

    public String getDeviceIp() {
        return deviceIp;
    }

    public void setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
    }

    public String getDevicePort() {
        return devicePort;
    }

    public void setDevicePort(String devicePort) {
        this.devicePort = devicePort;
    }

    public String getDeviceDesc() {
        return deviceDesc;
    }

    public void setDeviceDesc(String deviceDesc) {
        this.deviceDesc = deviceDesc;
    }

    public boolean isDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(boolean deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public int getDeviceOid() {
        return deviceOid;
    }

    public void setDeviceOid(int deviceOid) {
        this.deviceOid = deviceOid;
    }
}
