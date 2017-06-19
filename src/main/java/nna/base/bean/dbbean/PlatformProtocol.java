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
    private String ipMulticastIf;
    private Boolean ipMulticastLoop;
    private Integer ipMulticastTtl;
    private Integer ipTos;
    private Boolean soBroadcast;
    private Boolean soKeepalive;
    private Integer soLinger;
    private Integer soRcvbuf;
    private Boolean soReuseadr;
    private Integer soSndbuf;
    private Boolean tcpNodelay;

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

    public String getIpMulticastIf() {
        return ipMulticastIf;
    }

    public void setIpMulticastIf(String ipMulticastIf) {
        this.ipMulticastIf = ipMulticastIf;
    }

    public Boolean getIpMulticastLoop() {
        return ipMulticastLoop;
    }

    public void setIpMulticastLoop(Boolean ipMulticastLoop) {
        this.ipMulticastLoop = ipMulticastLoop;
    }

    public Integer getIpMulticastTtl() {
        return ipMulticastTtl;
    }

    public void setIpMulticastTtl(Integer ipMulticastTtl) {
        this.ipMulticastTtl = ipMulticastTtl;
    }

    public Integer getIpTos() {
        return ipTos;
    }

    public void setIpTos(Integer ipTos) {
        this.ipTos = ipTos;
    }

    public Boolean getSoBroadcast() {
        return soBroadcast;
    }

    public void setSoBroadcast(Boolean soBroadcast) {
        this.soBroadcast = soBroadcast;
    }

    public Boolean getSoKeepalive() {
        return soKeepalive;
    }

    public void setSoKeepalive(Boolean soKeepalive) {
        this.soKeepalive = soKeepalive;
    }

    public Integer getSoLinger() {
        return soLinger;
    }

    public void setSoLinger(Integer soLinger) {
        this.soLinger = soLinger;
    }

    public Integer getSoRcvbuf() {
        return soRcvbuf;
    }

    public void setSoRcvbuf(Integer soRcvbuf) {
        this.soRcvbuf = soRcvbuf;
    }

    public Boolean getSoReuseadr() {
        return soReuseadr;
    }

    public void setSoReuseadr(Boolean soReuseadr) {
        this.soReuseadr = soReuseadr;
    }

    public Integer getSoSndbuf() {
        return soSndbuf;
    }

    public void setSoSndbuf(Integer soSndbuf) {
        this.soSndbuf = soSndbuf;
    }

    public Boolean getTcpNodelay() {
        return tcpNodelay;
    }

    public void setTcpNodelay(Boolean tcpNodelay) {
        this.tcpNodelay = tcpNodelay;
    }
}
