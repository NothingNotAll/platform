package nna.base.server;


/**
 * @author NNA-SHUAI
 * @create 2017-06-11 13:08
 **/

public class ServerConfig<T> extends EndConfig {

    private int backLog;

    public int getBackLog() {
        return backLog;
    }

    public void setBackLog(int backLog) {
        this.backLog = backLog;
    }

}