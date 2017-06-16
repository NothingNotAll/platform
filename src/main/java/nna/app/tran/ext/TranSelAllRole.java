package nna.app.tran.ext;

import nna.Marco;
import nna.base.bean.dbbean.PlatformRole;
import nna.base.log.Log;
import nna.base.dispatch.AppUtil;
import nna.base.dispatch.ConfMetaSetFactory;
import nna.base.util.LogUtil;
import nna.transaction.DefaultTransExecutor;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static nna.base.util.orm.ObjectFactory.getBean;

/**
 * @author NNA-SHUAI
 * @create 2017-05-24 13:30
 **/

public class TranSelAllRole extends DefaultTransExecutor<PlatformRole[]> {
    public PlatformRole[] inTransaction(Connection connection, PreparedStatement[] sts) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Log log=AppUtil.getLog();
        ResultSet countRs=sts[0].executeQuery();
        countRs.next();
        PlatformRole[] roles=new PlatformRole[countRs.getInt(1)];
        try{countRs.close();}catch (Exception e){e.printStackTrace();}
        int index=0;
        ResultSet roleRs=sts[1].executeQuery();
        int logLevel= ConfMetaSetFactory.getConfMeta().getLogLevel();
        while(roleRs.next()){
            roles[index]=(PlatformRole) getBean(roleRs,Marco.PLATFORM_ROLE);
            index++;
            LogUtil.log(roles[index],log,logLevel);
        }
        try{roleRs.close();}catch (Exception e){}
        return  roles;
    }
}
