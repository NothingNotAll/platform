package nna.base.server;

import java.lang.management.ManagementFactory;
import java.net.SocketOption;

/**
 * @author NNA-SHUAI
 * @create 2017-06-11 23:19
 **/

public abstract class EndConfig {

    protected int protocolType;
    protected static String pid;
    protected static String jvmName;
    protected String ip="127.0.0.1";
    protected int port;
    protected SocketOption[] socketOptions;
    protected Object[] options;

    static {
        setJVMCfg();
    }

    private static void setJVMCfg(){
        String name = ManagementFactory.getRuntimeMXBean().getName();
        ServerConfig.setJvmName(name);
        String pid = name.split("@")[0];
        ServerConfig.setPid(pid);
    }


    public static String getJvmName() {
        return jvmName;
    }

    public static void setJvmName(String jvmName) {
        ServerConfig.jvmName = jvmName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public static String getPid() {
        return ServerConfig.pid;
    }

    public static void setPid(String pid) {
        ServerConfig.pid = pid;
    }

    public SocketOption[] getSocketOptions() {
        return socketOptions;
    }

    public void setSocketOptions(SocketOption[] socketOptions) {
        this.socketOptions = socketOptions;
    }

    public Object[] getOptions() {
        return options;
    }

    public void setOptions(Object[] options) {
        this.options = options;
    }

    public int getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(int protocolType) {
        this.protocolType = protocolType;
    }
}
