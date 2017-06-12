package nna.base.log;

import nna.base.util.concurrent.AbstractTask;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author NNA-SHUAI
 * @create 2017-06-12 21:13
 **/

public abstract class LogTask extends AbstractTask {
    private static final SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:MM:ss:SSS");
    public static final int ERROR=Integer.MAX_VALUE;
    public static final int INFO=0;
    public static final int TRACE=1;
    public static final int DEBUG=2;
    private static AtomicLong workSeq=new AtomicLong();
    private final long startTime;
    private final String logDir;
    private final AtomicLong logSeqGen;
    private final String logName;
    private final int appLogLevel;
    private final int flushLimit;
    private final String encode;
    private final int closeTimeout;
    private volatile boolean isSynWrite=false;
    private BufferedWriter writer;
    private StringBuilder logStrBuilder=new StringBuilder("");
    private volatile boolean isInit=false;
    private volatile boolean isInitPending=false;//ensure only init for once;

    public LogTask(
            String taskName,
            String logDir,
            AtomicLong logSeqGen,
            String logFileName,
            int appLogLevel,
            int flushLimit,
            int closeTimeout,
            String encode
            ) {
        super(taskName);
        startTime=System.currentTimeMillis();
        this.logDir=logDir;
        this.logSeqGen=logSeqGen;
        this.logName=logFileName;
        this.appLogLevel=appLogLevel;
        this.flushLimit=flushLimit;
        this.closeTimeout=closeTimeout;
        this.encode=encode;
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
                    submitEvent();
                    System.out.println("日志路径:"+logDir+" 线程 id:"+getThreadId()+" 名称:"+getThreadName()+" 进入日志队列耗费毫秒："+String.valueOf(System.currentTimeMillis()-start)+"L");
                }
            }
            logStrBuilder=new StringBuilder("");
        }
    }

    private boolean canFlush(){
        return isInit&&logStrBuilder.toString().getBytes().length >= flushLimit;
    }

    public void create() {
        init();
    }

//    public void init() {
//        if(isInitPending){
//            return ;
//        }
//        isInitPending=true;
//        String logPath= logDir+
//                logSeqGen.getAndIncrement();
//        String logFileName=logPath+"/"+logName;
//        try {
//            File logDir=new File(logPath);
//            if(!logDir.exists()){
//                logDir.mkdirs();
//            }
//            File logFile=new File(logFileName);
//            if(!logFile.exists()){
//                logFile.createNewFile();
//            }
//            writer=new BufferedWriter(
//                    new OutputStreamWriter(
//                            new FileOutputStream(logFile),encode));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            isInit=true;
//        }
//    }

    public void work() {
        try {
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void destroy() throws IOException {

    }

    public void otherWork() {

    }
}
