package nna.base.log;

import nna.base.util.concurrent.AbstractTask;

import java.io.*;
import java.text.SimpleDateFormat;

/**
 * @author NNA-SHUAI
 * @create 2017-06-12 21:13
 **/

public class Log extends AbstractTask {
    public static final SimpleDateFormat yyMMdd=new SimpleDateFormat("yyyy-MM-dd$HH-mm-ss-SSS");
    private static final SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:MM:ss:SSS");
    public static final int ERROR=Integer.MAX_VALUE;
    public static final int INFO=0;
    public static final int TRACE=1;
    public static final int DEBUG=2;
    private final long startTime;
    private final String logDir;
    private final String logName;
    private final int appLogLevel;
    private final int flushLimit;
    private final String encode;
    private final int closeTimeout;
    private volatile boolean isSynWrite=false;
    private BufferedWriter writer;
    private StringBuilder logStrBuilder=new StringBuilder("");

    public Log(
            String logDir,
            String logFileName,
            int appLogLevel,
            int flushLimit,
            int closeTimeout,
            String encode
            ) {
        super("log",new Object());
        startTime=System.currentTimeMillis();
        this.logDir=logDir;
        this.logName=logFileName;
        this.appLogLevel=appLogLevel;
        this.flushLimit=flushLimit;
        this.closeTimeout=closeTimeout;
        this.encode=encode;
        setWorkCount(30);
        submitInitEvent(null,true);
    }

    public void log(String log,int logLevel){
        if(logLevel >= appLogLevel){
            StackTraceElement[] ses=Thread.currentThread().getStackTrace();
            StackTraceElement ste=ses[2];
            String codeExecuteClassName=ste.getClassName();
            String codeExecuteMethodName=ste.getMethodName();
            int codeExecuteLine=ste.getLineNumber();
            String time="["+format.format(System.currentTimeMillis())+"]";
            String exeInfo=time+"["+getThreadName()+"]"+"["+getThreadId()+"]"+"["+codeExecuteClassName+"-"+codeExecuteMethodName+"-"+codeExecuteLine+"]\r\n";
            time="["+format.format(System.currentTimeMillis())+"]";
            time+="["+logLevel+"] ";
            time+=log;
            String supplement=exeInfo+time+"\r\n";
            logStrBuilder.append(supplement);
            String logStr=logStrBuilder.toString();
            if(isSynWrite){
                try {
                    writer.write(logStr);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                if(canFlush()){
                    Long start=System.currentTimeMillis();
                    setTaskStatus(TASK_STATUS_WORK);
                    submitEvent(logStr);
                    System.out.println("日志路径:"+logDir+" 线程 id:"+getThreadId()+" 名称:"+getThreadName()+" 进入日志队列耗费毫秒："+String.valueOf(System.currentTimeMillis()-start)+"L");
                }
            }
            logStrBuilder=new StringBuilder("");
        }
    }

    private boolean canFlush(){
        return isInit()&&logStrBuilder.toString().getBytes().length >= flushLimit;
    }

    protected Object init(Object object) {
        if(isInit()){
            return null;
        }
        String logPath= logDir+
                yyMMdd.format(startTime);
        String logFileName=logPath+"/"+logName;
        try {
            File logDir=new File(logPath);
            if(!logDir.exists()){
                logDir.mkdirs();
            }
            int logCount=logDir.list().length;
            int logSeq=logCount==0?0:logCount+1;
            File logFile=new File(logFileName+"_"+logSeq+".log");
            if(!logFile.exists()){
                logFile.createNewFile();
            }
            writer=new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(logFile),encode));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            setInit(true);
        }
        return null;
    }

    protected Object work(Object object) {
        try {
            writer.write(object.toString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected Object otherWork(Object object) {
        return null;
    }

    protected Object destroy(Object object) {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close(){
        setTaskStatus(TASK_STATUS_DESTROY);
        submitEvent(null);
    }

    public static Log getLog(
            String logDir,
            String logFileName,
            int appLogLevel,
            int flushLimit,
            int closeTimeout,
            String encode){
        return new Log(
                logDir,
                logFileName,
                appLogLevel,
                flushLimit,
                closeTimeout,encode);
    }
}
