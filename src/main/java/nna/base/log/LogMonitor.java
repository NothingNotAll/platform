package nna.base.log;

/**
 * @author NNA-SHUAI
 * @create 2017-05-27 11:18
 **/

public class LogMonitor {
    private LogManager logManager;

    public LogMonitor(LogManager logManager){
        this.logManager=logManager;
    }

    public LogManager getLogManager() {
        return logManager;
    }

    public void setLogManager(LogManager logManager) {
        this.logManager = logManager;
    }
}
