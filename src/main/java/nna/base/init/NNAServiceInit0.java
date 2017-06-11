package nna.base.init;
/**
 * ${description}
 *
 * @author NNA-SHUAI
 * @create 2017-2017/5/14-16:24
 */

import nna.Marco;
import nna.MetaBean;
import nna.base.bean.dbbean.*;
import nna.base.protocol.dispatch.ConfMetaSetFactory;
import nna.base.log.Log;
import nna.base.log.LogEntry;
import nna.base.util.BuildSQL;
import nna.base.util.List;
import nna.base.util.ObjectFactory;
import nna.base.util.ObjectUtil;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

import static nna.base.init.Util.buildSQL;
import static nna.base.util.ObjectFactory.getBean;


/**
 * for platform initial
 * @author NNA-SHUAI
 * @create 2017-05-14 16:24
 **/

public class NNAServiceInit0 {

     private static Connection initConnection;
     public static void main(String[] args){}

     public static PreparedStatement[] build(){

         PreparedStatement[] psts;
         PreparedStatement[] inits = new PreparedStatement[0];
         try {
             initConnection=init();
             psts = initPsts(new String[]{
                     ""+Marco.CLAZZ_COUNT+"",
                     "SELECT "+
                             BuildSQL.getTableCols(initConnection,Marco.INIT_TABLENAME_PLATFORMCLAZZ) +
                             " FROM "+
                             Marco.INIT_TABLENAME_PLATFORMCLAZZ,
                     "SELECT "+
                             BuildSQL.getTableCols(initConnection,Marco.INIT_TABLENAME_PLATFORMLOG)+
                             " FROM "+
                             Marco.INIT_TABLENAME_PLATFORMLOG+" WHERE LOG_ID='0'",
                     "SELECT "+
                             BuildSQL.getTableCols(initConnection,Marco.INIT_TABLENAME_PLATFROMTRANSACTION)+
                             " FROM "+
                             Marco.INIT_TABLENAME_PLATFROMTRANSACTION+" WHERE TRANSACTION_NAME='init' ORDER BY SQL_SEQUENCE;",
                     "SELECT "+
                             BuildSQL.getTableCols(initConnection,Marco.INIT_TABLENAME_PLATFORMSQL)+
                             " FROM "+Marco.INIT_TABLENAME_PLATFORMSQL+
                             " WHERE SQL_ID=?;"
             });
             initObjectContainer(psts[0]);
             initObjectFactories(psts[1]);
             ResultSet rs=psts[2].executeQuery();
             rs.next();
             PlatformLog platformLog=(PlatformLog) getBean(rs,Marco.PLATFORM_LOG);
             Log log=getPlatformLog(platformLog);
             MetaBean confMeta=new MetaBean();
             confMeta.setLog(log);
             ConfMetaSetFactory.setConfMeta(confMeta);
             inits=initPstsCache(psts,log);
         } catch (SQLException e) {
             e.printStackTrace();
         } catch (InstantiationException e) {
             e.printStackTrace();
         } catch (InvocationTargetException e) {
             e.printStackTrace();
         } catch (NoSuchMethodException e) {
             e.printStackTrace();
         } catch (IllegalAccessException e) {
             e.printStackTrace();
         } catch (ClassNotFoundException e) {
             e.printStackTrace();
         }
         return inits;
     }
     
    private static PreparedStatement[] initPstsCache(PreparedStatement[] initPsts, Log log) throws SQLException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, SQLException {
        PreparedStatement initTran=initPsts[3];
        ResultSet rs=initTran.executeQuery();
        ArrayList<String> sqls=new ArrayList<String>();
        while(rs.next()){
            PlatformTransaction platformTransaction=(PlatformTransaction) getBean(rs,Marco.PLATFORM_TRANSACTION);
            initPsts[4].setString(1,platformTransaction.getSqlId());
            ResultSet sqlRS=initPsts[4].executeQuery();
            while(sqlRS.next()){
                PlatformSql platformSql=(PlatformSql) getBean(sqlRS,Marco.PLATFORM_SQL);
                String sql= buildSQL(platformSql);
                sqls.add(sql);
            }
            sqlRS.close();
        }
        rs.close();
        initTran.close();
        initPsts[4].close();
        return initPsts(sqls.toArray(new String[]{}));
    }

    private static Log getPlatformLog(PlatformLog platformLog) {
        SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd");
        String logPath="/log/"+format.format(System.currentTimeMillis())+platformLog.getLogDir();
        File logDir=new File(logPath);
        String logName="log_0.log";
        if(!logDir.exists()){
            logDir.mkdirs();
        }else{
            int logSize=logDir.list().length+1;
            logName="log_"+logSize+".log";
        }
        return LogEntry.submitInitEvent(
                logPath,
                new AtomicLong(),
                logName,
                platformLog.getLogLevel(),
                platformLog.getLogBufferThreshold(),
                platformLog.getLogCloseTimedout(),
                platformLog.getLogEncode());
    }

    private static void initObjectFactories(PreparedStatement objectFactoryPst) throws SQLException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ResultSet rs=objectFactoryPst.executeQuery();
        while(rs.next()){
            String dbBeanClassName=rs.getString(2);
            MetaBean.getObjectFactoryCache().insert(ObjectUtil.buildObjectFactory(dbBeanClassName),100);
        }
        rs.close();
        objectFactoryPst.close();
    }

    private static void initObjectContainer(PreparedStatement objectFactoryCountPst) throws SQLException {
        ResultSet count=objectFactoryCountPst.executeQuery();
        int clazzCount = 0;
        while(count.next()){
            clazzCount=count.getInt(1);
        }
        count.close();
        objectFactoryCountPst.close();
        MetaBean.setObjectFactoryCache(new List<ObjectFactory>(clazzCount));
    }

    public static Connection init(){
         Connection con=null;
        try {
            String URL=Marco.URL;
            String DRIVER=Marco.DRIVER;
            String ACCOUNT=Marco.ACCOUNT;
            String PASSWORD=Marco.PASSWORD;
            Class.forName(DRIVER);
            con= DriverManager.getConnection(URL,ACCOUNT,PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }

    private static PreparedStatement[] initPsts(String[] strings) throws SQLException {
        int pstSize=strings.length;
        PreparedStatement[] psts=new PreparedStatement[pstSize];
        for(int index=0;index < pstSize;index++){
            psts[index]=initConnection.prepareStatement(strings[index]);
        }
        return psts;
    }
}
;