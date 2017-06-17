package nna.base.bean.dbbean;

import nna.base.bean.Clone;
import nna.enums.ProtocolType;

/**
 * @author NNA-SHUAI
 * @create 2017-06-11 13:01
 **/

public class PlatformProtocol extends Clone{
    private static final Long serialVersionUID=23L;
    private int entryId;
    private int entryPort;
    private ProtocolType protocolType;

    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    public int getEntryPort() {
        return entryPort;
    }

    public void setEntryPort(int entryPort) {
        this.entryPort = entryPort;
    }

    public ProtocolType getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(ProtocolType protocolType) {
        this.protocolType = protocolType;
    }
}
