package nna.base.server;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.net.SocketOption;

/**
 * @author NNA-SHUAI
 * @create 2017-06-11 13:08
 **/

public class Server<T> {
    private static String pid;
    private static String jvmName;


    public static void main(String[] args){
        System.out.println(Runtime.getRuntime().availableProcessors());
    }
    private String ip="127.0.0.1";
    private int port;
    private int backLog;
    private SocketOption<Object>[] socketOptions;
    private T attach=(T)this;
    private Object selectionKeyAttach;
    private Method serverMethod;
    private Object serverObject;

    static {
        setJVMCfg();
    }

    private static void setJVMCfg(){
        String name = ManagementFactory.getRuntimeMXBean().getName();
        Server.setJvmName(name);
        String pid = name.split("@")[0];
        Server.setPid(pid);
    }

    public static String getJvmName() {
        return jvmName;
    }

    public static void setJvmName(String jvmName) {
        Server.jvmName = jvmName;
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
        return Server.pid;
    }

    public static void setPid(String pid) {
        Server.pid = pid;
    }

    public Method getServerMethod() {
        return serverMethod;
    }

    public void setServerMethod(Method serverMethod) {
        this.serverMethod = serverMethod;
    }

    public Object getServerObject() {
        return serverObject;
    }

    public void setServerObject(Object serverObject) {
        this.serverObject = serverObject;
    }

    public int getBackLog() {
        return backLog;
    }

    public void setBackLog(int backLog) {
        this.backLog = backLog;
    }

    public SocketOption<Object>[] getSocketOptions() {
        return socketOptions;
    }

    public void setSocketOptions(SocketOption<Object>[] socketOptions) {
        this.socketOptions = socketOptions;
    }

    public T getAttach() {
        return attach;
    }

    public void setAttach(T attach) {
        this.attach = attach;
    }

    public Object getSelectionKeyAttach() {
        return selectionKeyAttach;
    }

    public void setSelectionKeyAttach(Object selectionKeyAttach) {
        this.selectionKeyAttach = selectionKeyAttach;
    }
}
