package nna.base.bean.dbbean;

import nna.base.bean.Clone;
import nna.enums.ProtocolType;

/**
 * @author NNA-SHUAI
 * @create 2017-06-11 13:01
 **/

public class PlatformProtocol extends Clone{
    private static final Long serialVersionUID=23L;
    private int protocolId;
    private int protocolPort;
    private ProtocolType protocolType;

    public ProtocolType getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(ProtocolType protocolType) {
        this.protocolType = protocolType;
    }

    public int getProtocolId() {
        return protocolId;
    }

    public void setProtocolId(int protocolId) {
        this.protocolId = protocolId;
    }

    public int getProtocolPort() {
        return protocolPort;
    }

    public void setProtocolPort(int protocolPort) {
        this.protocolPort = protocolPort;
    }
}
