package nna.base.server;

import java.lang.reflect.Method;

/**
 * @author NNA-SHUAI
 * @create 2017-06-11 13:08
 **/

public class Server {
    public static void main(String[] args){
        System.out.println(Runtime.getRuntime().availableProcessors());
    }
    private String ip="127.0.0.1";
    private int port;
    private int pid;
    private Method serverMethod;
    private Object serverObject;

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

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
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
}
