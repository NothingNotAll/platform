package nna.base.log;


import java.io.*;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * d
 *
 * @author NNA-SHUAI
 * @create 2017-05-18 11:08
 **/

public class Log {
    private static final SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:MM:ss:SSS");
    public static final int ERROR=Integer.MAX_VALUE;
    public static final int INFO=0;
    public static final int TRACE=1;
    public static final int DEBUG=2;

    private String logDir;
    private AtomicLong logSeqGen;
    private String logName;
    private volatile int appLogLevel=-1;
    private BufferedWriter writer;
    private StringBuilder logStrBuilder=new StringBuilder("");
    private String encode;
    private int flushLimit;
    private int closeTimeout;
    private volatile boolean isSynWrite=false;
    private String threadName;
    private final long threadId;
    private Integer logWorkerNo;
    private volatile boolean isInit=false;
    private volatile boolean isInitPending=false;//ensure only init for once;
    private Long startTime;
    private ReentrantLock writeLock=new ReentrantLock();
    private volatile boolean isEnd;

    public Log(String logDir,
               AtomicLong logSeqGen,
               String logFileName,
               int appLogLevel,
               int flushLimit,
               int closeTimeout,
               String encode){
        startTime=System.currentTimeMillis();
        this.logDir=logDir;
        this.logSeqGen=logSeqGen;
        this.logName=logFileName;
        this.appLogLevel=appLogLevel;
        this.flushLimit=flushLimit;
        this.closeTimeout=closeTimeout;
        this.encode=encode;
        Thread thread=Thread.currentThread();
        threadName=Thread.currentThread().getName();
        threadId=thread.getId();
    }

    public void init(){
        if(isInitPending){
            return ;
        }
        isInitPending=true;
        String logPath= logDir+
                logSeqGen.getAndIncrement();
        String logFileName=logPath+"/"+logName;
        try {
            File logDir=new File(logPath);
            if(!logDir.exists()){
                logDir.mkdirs();
            }
            File logFile=new File(logFileName);
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
            isInit=true;
        }
    }

    public void log(String log,int logLevel){
        if(logLevel >= appLogLevel){
            StackTraceElement[] ses=Thread.currentThread().getStackTrace();
            StackTraceElement ste=ses[2];
            String codeExecuteClassName=ste.getClassName();
            String codeExecuteMethodName=ste.getMethodName();
            int codeExecuteLine=ste.getLineNumber();
            String time="["+format.format(System.currentTimeMillis())+"]";
            String exeInfo=time+"["+threadName+"]"+"["+threadId+"]"+"["+codeExecuteClassName+"-"+codeExecuteMethodName+"-"+codeExecuteLine+"]\r\n";
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
                if(canSynWrite()){
                    synWrite(logStr);
                }else{
                    if(canFlush()){
                        Long start=System.currentTimeMillis();
                        LogEntry.submitWriteEvent(this,logStr);
//                    System.out.println("日志路径:"+logDir+" 线程 id:"+threadId+" 名称:"+threadName+" 进入日志队列耗费毫秒："+String.valueOf(System.currentTimeMillis()-start)+"L");
                    }
                }
            }
            logStrBuilder=new StringBuilder("");
        }
    }

    private boolean canSynWrite() {
        return isSynWrite;
    }

    private void synWrite(String logStr) {
        write(logStr);
    }

    public void close(){
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String buffer){
        try {
            writeLock.lock();
            writer.write(buffer);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            writeLock.unlock();
        }
    }

    public boolean canFlush(){
        return isInit&&logStrBuilder.toString().getBytes().length >= flushLimit;
    }

    public BufferedWriter getWriter() {
        return writer;
    }

    public void setWriter(BufferedWriter writer) {
        this.writer = writer;
    }

    public StringBuilder getLogStrBuilder() {
        return logStrBuilder;
    }

    public void setLogStrBuilder(StringBuilder logStrBuilder) {
        this.logStrBuilder = logStrBuilder;
    }

    public String getEncode() {
        return encode;
    }

    public void setEncode(String encode) {
        this.encode = encode;
    }

    public int getFlushLimit() {
        return flushLimit;
    }

    public void setFlushLimit(int flushLimit) {
        this.flushLimit = flushLimit;
    }

    public int getAppLogLevel() {
        return appLogLevel;
    }

    public void setAppLogLevel(int appLogLevel) {
        this.appLogLevel = appLogLevel;
    }

    public boolean isSynWrite() {
        return isSynWrite;
    }

    public void setSynWrite(boolean synWrite) {
        isSynWrite = synWrite;
    }

    public String getLogDir() {
        return logDir;
    }

    public void setLogDir(String logDir) {
        this.logDir = logDir;
    }

    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public int getCloseTimeout() {
        return closeTimeout;
    }

    public void setCloseTimeout(int closeTimeout) {
        this.closeTimeout = closeTimeout;
    }

    public long getThreadId() {
        return threadId;
    }

    public String getThreadName() {
        return threadName;
    }

    public static SimpleDateFormat getFormat() {
        return format;
    }

    public AtomicLong getLogSeqGen() {
        return logSeqGen;
    }

    public void setLogSeqGen(AtomicLong logSeqGen) {
        this.logSeqGen = logSeqGen;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public Integer getLogWorkerNo() {
        return logWorkerNo;
    }

    public void setLogWorkerNo(Integer logWorkerNo) {
        this.logWorkerNo = logWorkerNo;
    }
}
