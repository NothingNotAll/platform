package nna.base.log;

import nna.Marco;
import nna.base.util.SystemUtil;
import nna.base.util.concurrent.AbstractTask;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author NNA-SHUAI
 * @create 2017-06-23 10:26
 **/

public class Log extends AbstractTask {
    private static final AtomicLong logSeqGen=new AtomicLong();
    public static final SimpleDateFormat yyMMdd=new SimpleDateFormat(Marco.YYYYMMDD_DIR);
    public static final SimpleDateFormat HHmmssSS=new SimpleDateFormat(Marco.LOG_TIME_DIR);
    private static final SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    private static final int CLOSE=6;
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
    private ReentrantLock initLock=new ReentrantLock();
    private volatile boolean isInit=false;

    public Log(String logDir,
               String logFileName,
               int appLogLevel,
               int flushLimit,
               int closeTimeout,
               String encode) throws Exception {
        super(true);
        startTime=System.currentTimeMillis();
        this.logDir=logDir;
        this.logName=logFileName;
        this.appLogLevel=appLogLevel;
        this.flushLimit=flushLimit;
        this.closeTimeout=closeTimeout;
        this.encode=encode;
        if(SystemUtil.isSystemLoadPermit()){
            isSynWrite=false;
            addNewTask(this,null,INIT_TASK_TYPE,false, 0L);
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
            String exeInfo=time+"["+getInitThreadNm()+"]"+"["+getInitThreadId()+"]"+"["+codeExecuteClassName+"-"+codeExecuteMethodName+"-"+codeExecuteLine+"]\r\n";
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
//                    Long start=System.currentTimeMillis();
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
        setTaskStatus(CLOSE);
        addNewTask(this,null,CLOSE,false,null);
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
            case CLOSE:
                destroy(att);
                break;
            case WORK_TASK_TYPE:
                work(att);
                break;
        }
        return null;
    }

    @Override
    protected void addNewTask(AbstractTask abstractTask, Object att, int taskType, Boolean isNewTToExe, Long delayTime) {
        super.addNewTask(abstractTask, att, taskType, isNewTToExe, delayTime);
    }

    public ReentrantLock getInitLock() {
        return initLock;
    }

    public void setInitLock(ReentrantLock initLock) {
        this.initLock = initLock;
    }

    public boolean isInit() {
        return isInit;
    }

    public void setInit(boolean init) {
        isInit = init;
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
