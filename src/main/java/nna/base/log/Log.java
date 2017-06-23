package nna.base.log;

import nna.Marco;
import nna.base.util.conv2.AbstractTask;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author NNA-SHUAI
 * @create 2017-06-23 10:26
 **/

public class Log extends AbstractTask {
    private static final AtomicLong logSeqGen=new AtomicLong();
    public static final SimpleDateFormat yyMMdd=new SimpleDateFormat(Marco.YYYYMMDD_DIR);
    public static final SimpleDateFormat HHmmssSS=new SimpleDateFormat(Marco.LOG_TIME_DIR);
    private static final SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

    public static final int ERROR=Integer.MAX_VALUE;
    public static final int INFO=0;
    public static final int TRACE=1;
    public static final int DEBUG=2;
    private long startTime;
    private String logDir;
    private String logName;
    private int appLogLevel;
    private int flushLimit;
    private String encode;
    private int closeTimeout;
    private volatile boolean isSynWrite=false;
    private BufferedWriter writer;
    private StringBuilder logStrBuilder=new StringBuilder("");

    public Log(String logDir,
               String logFileName,
               int appLogLevel,
               int flushLimit,
               int closeTimeout,
               String encode) throws Exception {
        super(
                "LOG",
                20,
                1,
                Marco.SEQ_LINKED_SIZE_TASK,
                Marco.CACHED_THREAD_TYPE);
        startTime=System.currentTimeMillis();
        this.logDir=logDir;
        this.logName=logFileName;
        this.appLogLevel=appLogLevel;
        this.flushLimit=flushLimit;
        this.closeTimeout=closeTimeout;
        this.encode=encode;
        if(checkSystemLoad()){
            isSynWrite=false;
            addNewTask(this,null,INIT_TASK_TYPE,false,null);
        }else{
            isSynWrite=true;
            init(null);
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
                    writer.flush();
                    logStrBuilder=new StringBuilder("");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                if(canFlush()){
                    Long start=System.currentTimeMillis();
                    addNewTask(this,logStr,WORK_TASK_TYPE,false,null);
                    logStrBuilder=new StringBuilder("");
//                    System.out.println("日志路径:"+logDir+" 线程 id:"+getThreadId()+" 名称:"+getThreadName()+" 进入日志队列耗费毫秒："+String.valueOf(System.currentTimeMillis()-start)+"L");
                }
            }
        }
    }

    private boolean canFlush(){
        return isInit()&&logStrBuilder.toString().getBytes().length >= flushLimit;
    }

    private Object init(Object object) throws Exception {
        boolean locked=false;
        try{
            if(!isInit()&&getInitLock().tryLock()){
                locked=true;
                String logPath=
                        "/"+logDir+"/"+yyMMdd.format(startTime)+"/"+logName+"/"+HHmmssSS.format(startTime)+"/";
                String logFileName=logPath+logName;
                File logDir=new File(logPath);
                if(!logDir.exists()){
                    logDir.mkdirs();
                }
                Long logSeq=logSeqGen.getAndIncrement();
                File logFile=new File(logFileName+"-"+logSeq+".log");
                //threadSafe
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

    public void close(){
        setTaskStatus(OVER);
        addNewTask(this,null,OVER_TASK_TYPE,false,null);
    }

    private boolean checkSystemLoad() {
        return true;
    }

    private Object destroy(Object object) throws IOException {
        writer.close();
        return null;
    }

    private Object work(Object object) throws IOException {
        writer.write(object==null?"null":object.toString());
        writer.flush();
        return null;
    }

    public Object doTask(Object att, int taskType) throws Exception {
        switch (taskType){
            case INIT_TASK_TYPE:
                init(att);
                break;
            case OVER_TASK_TYPE:
                destroy(att);
                break;
            case WORK_TASK_TYPE:
                work(att);
                break;
        }
        return null;
    }

    public static Log getLog(
            String logDir,
            String logFileName,
            int appLogLevel,
            int flushLimit,
            int closeTimeout,
            String encode) throws Exception {
        return new Log(
                logDir,
                logFileName,
                appLogLevel,
                flushLimit,
                closeTimeout,
                encode);
    }
}
