package nna.app.web;

import nna.app.tran.ext.TranSelAllRole;
import nna.base.bean.dbbean.PlatformRole;
import nna.base.dispatch.AppUtil;
import nna.transaction.MetaBeanWrapper;
import nna.transaction.TransExecutor;

import java.sql.SQLException;

/**
 * @author NNA-SHUAI
 * @create 2017-05-26 13:45
 **/

public class NNAInitLoginPage {
    TransExecutor<PlatformRole[]> selAllRole=new TranSelAllRole();

    public void initLoginPage(MetaBeanWrapper metaBeanWrapper){
        PlatformRole[] roles;
        try {
            roles = selAllRole.executeTransactions(metaBeanWrapper);
            int size=roles.length;
            String[] roleId=new String[size];
            String[] roleName=new String[size];
            PlatformRole platformRole;
            for(int index=0;index < size;index++){
                platformRole= roles[index];
                roleId[index]=String.valueOf(platformRole.getRoleId());
                roleName[index]=platformRole.getRoleName().toString();
            }
            AppUtil.putRspColumn("roleId",roleId);
            AppUtil.putRspColumn("roleName",roleName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
