package nna.app.tran.ext;

import nna.Marco;
import nna.base.bean.dbbean.PlatformResource;
import nna.base.bean.dbbean.PlatformRole;
import nna.base.bean.dbbean.PlatformUser;
import nna.transaction.AbstractTransaction;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author NNA-SHUAI
 * @create 2017-05-26 13:23
 **/

public class TranSelPriUser extends AbstractTransaction<PlatformUser> {

    public PlatformUser inTransaction(Connection connection, PreparedStatement[] sts) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
//        setCurrentPstParameter(0);
        ResultSet rs=sts[0].executeQuery();
        rs.next();
        PlatformUser platformUser=(PlatformUser)getBean(rs, Marco.PLATFORM_USER);
        sts[1].setInt(1,platformUser.getUserId());
        ResultSet roleRs=sts[1].executeQuery();
        ArrayList<PlatformRole> roles=new ArrayList<PlatformRole>();
        while(roleRs.next()){
            roles.add((PlatformRole) getBean(roleRs,Marco.PLATFORM_ROLE));
        }
        PlatformUser combUser=new PlatformUser();
//        combUser.setPlatformRoles(roles.toArray(new PlatformRole[0]));
//        combUser.setPlatformUser(platformUser);
        int roleCount=roles.size();
        PlatformRole role;
        HashMap<String,PlatformResource> resouceMap=new HashMap<String, PlatformResource>();
        for(int index=0;index < roleCount;index++){
            role=roles.get(index);
            sts[2].setInt(1,role.getRoleId());
            ResultSet reRs=sts[2].executeQuery();
            while(reRs.next()){
                Integer resourceId=reRs.getInt(2);
                sts[3].setInt(1,resourceId);
                ResultSet resultSets=sts[3].executeQuery();
                resultSets.next();
                resouceMap.put(String.valueOf(resourceId),(PlatformResource)getBean(resultSets,Marco.PLATFORM_RESOURCE));
                try{
                    resultSets.close();
                }catch (Exception e){}
            }
            try{
                reRs.close();
            }catch (Exception e){}
        }
        try{roleRs.close();}catch (Exception e){}
        try {
            rs.close();
        }catch (Exception e){}
        return combUser;
    }
}
