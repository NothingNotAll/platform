package nna.base.util;

import nna.base.bean.confbean.ConfMeta;
import nna.base.bean.dbbean.*;
import nna.base.log.Log;

/**
 * log util
 * @author NNA-SHUAI
 * @create 2017-05-15 10:32
 **/

public class LogUtil {
    private LogUtil(){}


    public static void log(PlatformColumn[] columns,Log log,int logLevel){
        if (columns==null)
            return;
        int count=columns.length;
        PlatformColumn column;
        for(int index=0;index < count;index++){
            column=columns[index];
            log.log("字段标识："+column.getColumnId(),logLevel);
            log.log("字段序号："+String.valueOf(column.getColumnNo()),logLevel);
            log.log("字段默认值："+column.getColumnDefaultvalue(),logLevel);
            log.log("字段外部名称："+column.getColumnOutsideName(),logLevel);
            log.log("字段内部名称："+column.getColumnInnerName(),logLevel);
            log.log("字段长度限制："+column.getColumnLength(),logLevel);
            log.log("字段是否为数组："+column.isColumnIsarray(),logLevel);
            log.log("字段格式："+column.getColumnFormat(),logLevel);
            log.log("字段是否必输："+column.isColumnIsmust(),logLevel);
            log.log("字段描述："+column.getColumnDesc(),logLevel);
        }
    }

    public static void log(PlatformEntry platformEntry,Log log,int logLevel){
        if (platformEntry==null)
            return;
        log.log(platformEntry.getEntryCode(),logLevel);
        log.log(platformEntry.getEntryDesc(),logLevel);
        log.log(platformEntry.getEntryUri(),logLevel);
        log.log(String.valueOf(platformEntry.getEntryAppId()),logLevel);
        log.log(String.valueOf(platformEntry.getEntryId()),logLevel);
    }

    public static void log(PlatformDB platformDB,Log log ,int logLevel){
        log.log("数据库唯一标识："+String.valueOf(platformDB.getDbId()),logLevel);
        log.log("数据库JDBC URL:"+platformDB.getDbUrl(),logLevel);
        log.log("数据库账户："+platformDB.getDbAccount(),logLevel);
        log.log("数据库密码："+platformDB.getDbPassword(),logLevel);
        log.log("数据库连接池个数："+String.valueOf(platformDB.getDbPoolCount()),logLevel);
        log.log("单个连接池维持连接个数："+String.valueOf(platformDB.getDbPoolsCount()),logLevel);
        log.log("失败重试次数："+String.valueOf(platformDB.getDbFailTrytime()),logLevel);
        log.log("心跳检测时间间隔："+String.valueOf(platformDB.getDbHeartbeatTest()),logLevel);
    }

    public static void log(PlatformLog platformLog, Log log, int logLevel){
        log.log("日志唯一标识："+String.valueOf(platformLog.getLogId()),logLevel);
        log.log("log Config:",logLevel);
        log.log("日志目录："+platformLog.getLogDir(),logLevel);
        log.log("日志输出编码："+platformLog.getLogEncode(),logLevel);
        log.log("日志缓冲阈值："+String.valueOf(platformLog.getLogBufferThreshold()),logLevel);
        log.log("日志关闭超时时间："+String.valueOf(platformLog.getLogCloseTimedout()),logLevel);
        log.log("日志级别："+String.valueOf(platformLog.getLogLevel()),logLevel);
    }

    public static void log(PlatformRole platformRole, Log log, int logLevel){
        log.log("角色信息",logLevel);
        log.log("角色唯一标识："+String.valueOf(platformRole.getRoleId()),logLevel);
        log.log("角色名称："+platformRole.getRoleName(),logLevel);
    }

    public static void log(PlatformApp platformApp,Log log,int logLevel){
        log.log("应用配置:",logLevel);
        log.log("应用中文名称："+platformApp.getAppCh(),logLevel);
        log.log("应用描述："+platformApp.getAppDesc(),logLevel);
        log.log("应用拦截类："+platformApp.getAppDispatchClass(),logLevel);
        log.log("应用拦截方法："+platformApp.getAppDispatchMethod(),logLevel);
        log.log("应用英文名称："+platformApp.getAppEn(),logLevel);
        log.log("应用编码："+platformApp.getAppEncode(),logLevel);
        log.log("应用唯一标识："+String.valueOf(platformApp.getAppId()),logLevel);
        log.log("应用工作空间："+String.valueOf(platformApp.getAppWorkspace()),logLevel);
        log.log("应用日志打印参数配置："+String.valueOf(platformApp.getAppLogId()),logLevel);
        log.log("应用数据库连接配置参数："+String.valueOf(platformApp.getAppDbId()),logLevel);
        log.log("日志创建时间戳："+platformApp.getAppCreateTimestamp().toString(),logLevel);
    }

    public static void log(ConfMeta confMeta,Log log){
        log.log(confMeta.toString(),10000);
    }

    public static void log(PlatformColumn platformColumn, Log log,int logLevel){
        log.log("字段描述："+platformColumn.getColumnDesc(),logLevel);
        log.log("字段id："+platformColumn.getColumnId(),logLevel);
        log.log("字段顺序："+platformColumn.getColumnNo(),logLevel);
        log.log("字段外部名称："+platformColumn.getColumnOutsideName(),logLevel);
        log.log("字段内部名称："+platformColumn.getColumnInnerName(),logLevel);
        log.log("字段长度："+platformColumn.getColumnLength(),logLevel);
        log.log("字段格式："+platformColumn.getColumnFormat(),logLevel);
        log.log("是否数组："+platformColumn.isColumnIsarray(),logLevel);
        log.log("是否必输"+platformColumn.isColumnIsmust(),logLevel);
    }

    public static void log(PlatformService service, Log log,int logLevel){
        log.log("服务描述："+service.getServiceDesc(),logLevel);
        log.log("服务名称："+service.getServiceName(),logLevel);
        log.log("服务类名称："+service.getServiceClass(),logLevel);
        log.log("服务方法名称："+service.getServiceMethod(),logLevel);
        log.log("服务临时字段容量大小："+service.getServiceTempsize(),logLevel);
        log.log("服务创建时间戳："+service.getCreateTimestamp(),logLevel);
        log.log("服务版本更新时间戳："+service.getUpdateTimestamp(),logLevel);
    }

    public static void log(PlatformController platformController, Log log,int logLevel) {
        log.log("控制器描述："+platformController.getControllerDesc(),logLevel);
        log.log("控制器ID："+platformController.getId(),logLevel);
        log.log("视图名称："+platformController.getRenderPage(),logLevel);
        log.log("视图渲染类名称："+platformController.getRenderClass(),logLevel);
        log.log("视图渲染方法："+platformController.getRenderMethod(),logLevel);
        log.log("创建时间戳："+platformController.getCreateTimestamp(),logLevel);
        log.log("更新时间戳："+platformController.getUpdateTimestamp(),logLevel);
    }

    public static void log(Log log, PlatformServiceTransaction platformServiceTransaction, int logLevel) {
        log.log(""+platformServiceTransaction.getTransactionLevel(),logLevel);
        log.log("事务描述-"+platformServiceTransaction.getTransactionDesc(),logLevel);
        log.log("事务传播类型-"+platformServiceTransaction.getTransactionPropagation(),logLevel);
        log.log("事务隔离级别-"+platformServiceTransaction.getTransactionLevel(),logLevel);
        log.log("调用事务服务码-"+platformServiceTransaction.getServiceName(),logLevel);
        log.log("事务名称-"+platformServiceTransaction.getTransactionName(),logLevel);
        log.log("事务版本创建时间戳-"+platformServiceTransaction.getCreateTimestamp(),logLevel);
        log.log("事务版本更新时间戳-"+platformServiceTransaction.getUpdateTimestamp(),logLevel);
        log.log("关联事务索引"+platformServiceTransaction.getPreviousTransactionIndex(),logLevel);
    }
}
