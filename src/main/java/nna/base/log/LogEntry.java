package nna.base.log;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author NNA-SHUAI
 * @create 2017-05-29 13:47
 **/

public class LogEntry {
    private static LogManager logManager;

    static{
        logManager=new LogManager(1);
    }

    private LogEntry(){}

    public static Log submitInitEvent(
            String logDir,
            AtomicLong logSeq,
            String logFileName,
            int appLogLevel,
            int flushLimit,
            int closeTimeout,
            String encode
    ){
        return logManager.getLog(
                 logDir,
                 logSeq,
                 logFileName,
         appLogLevel,
         flushLimit,
         closeTimeout,
         encode
        );
    }

    public static void submitWriteEvent(Log log,String logStr){
                logManager.submitWriteEvent(log,logStr);
    }

    public static void submitCloseEvent(Log log){
        logManager.submitCloseEvent(log);
    }
}
