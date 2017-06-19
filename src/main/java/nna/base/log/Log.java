package nna.base.log;

import nna.Marco;
import nna.base.util.concurrent.AbstractTask;

import java.io.*;
import java.text.SimpleDateFormat;

/**
 * @author NNA-SHUAI
 * @create 2017-06-12 21:13
 **/

public class Log extends AbstractTask {
    public static final int WRITING=5;
    public static final int NOTHING=6;

    public static final SimpleDateFormat yyMMdd=new SimpleDateFormat(Marco.YYYYMMDD_DIR);
    public static final SimpleDateFormat HHmmssSS=new SimpleDateFormat(Marco.LOG_TIME_DIR);
    private static final SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
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
            String encode,
            boolean isWorkSeq,
            int logTimes
            ) {
        super(logFileName,logTimes);
        startTime=System.currentTimeMillis();
        this.logDir=logDir;
        this.logName=logFileName;
        this.appLogLevel=appLogLevel;
        this.flushLimit=flushLimit;
        this.closeTimeout=closeTimeout;
        this.encode=encode;
        setTaskStatus(INIT);
        startTask(null,true);
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
                    logStrBuilder=new StringBuilder("");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                if(canFlush()){
                    Long start=System.currentTimeMillis();
                    setTaskStatus(WRITING);
                    addNewTask(logStr,WRITING);
                    logStrBuilder=new StringBuilder("");
//                    System.out.println("日志路径:"+logDir+" 线程 id:"+getThreadId()+" 名称:"+getThreadName()+" 进入日志队列耗费毫秒："+String.valueOf(System.currentTimeMillis()-start)+"L");
                }
            }
        }
    }

    private boolean canFlush(){
        return isInit()&&logStrBuilder.toString().getBytes().length >= flushLimit;
    }

    protected Object init(Object object) throws Exception {
        boolean locked=false;
        try{
            if(!isInit()&&getInitLock().tryLock()){
                locked=true;
                String logPath=
                        "/"+logDir+"/"+yyMMdd.format(startTime)+"/"+logName+"/"+HHmmssSS.format(startTime)+"/";
                String logFileName=logPath+logName;
//                System.out.println(startTime+"-"+logFileName);
                    File logDir=new File(logPath);
                    if(!logDir.exists()){
                        logDir.mkdirs();
                    }
                    int logCount=logDir.list().length;
                    int logSeq=logCount==0?1:logCount+1;
                    File logFile=new File(logFileName+"-"+logSeq+".log");
                    if(!logFile.exists()){
                        logFile.createNewFile();
                    }
                    writer=new BufferedWriter(
                            new OutputStreamWriter(
                                    new FileOutputStream(logFile),encode));
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception(e);
        }finally {
            if(locked){
                getInitLock().unlock();
            }
            setInit(true);
        }
        return null;
    }

    protected Object work(Object object) throws IOException {
        writer.write(object==null?"null":object.toString());
        writer.flush();
        return null;
    }

    protected Object destroy(Object object) throws IOException {
//            System.out.println(writer==null);
        writer.close();
        return null;
    }

    public void close(){
        setTaskStatus(OVER);
        addNewTask(null,OVER);
    }

    public static Log getLog(
            String logDir,
            String logFileName,
            int appLogLevel,
            int flushLimit,
            int closeTimeout,
            String encode,
            int logTime){
        return new Log(
                logDir,
                logFileName,
                appLogLevel,
                flushLimit,
                closeTimeout,encode,true,logTime);
    }

    protected Object doTask(int taskType, Object attach) throws Exception {
        switch (taskType){
            case INIT:
                init(attach);
                break;
            case WRITING:
                work(attach);
                break;
            case OVER:
                destroy(attach);
                break;
        }
        return null;
    }
}
