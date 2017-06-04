package nna.app.web;

import nna.Marco;
import nna.app.tran.ext.TranSelPriUser;
import nna.base.bean.combbean.CombUser;
import nna.base.bean.confbean.ConfMeta;
import nna.base.cache.CacheFactory;
import nna.base.protocol.dispatch.AppUtil;
import nna.base.proxy.Logic;
import nna.transaction.Transaction;

import java.lang.reflect.Method;
import java.sql.SQLException;

/**
 * the goal is that the code can been config by person instead of write by hand.
 * @author NNA-SHUAI
 * @create 2017-05-26 13:14
 **/

public class NNALogin {
    Transaction<CombUser> tranSelPriUser=new TranSelPriUser();//next step is to cancel the new action;

    //next optimize is that we can take place of the TRAN*
    // with the unify Logic class:
    //
    // or (dbServiceMethod and dbServiceObject),
    // as that the class of orm will be
    // set automatic in the init of the platform ;
    // use it like this:
    /*
    * serviceMethod.invoke(serviceObject,args...);
    * or like this:
    * serviceMethod=logic.getMethodOfLogic();
    * serviceObject=logic.getObjectOfLogic();
    * serviceMethod.invoke(serviceObject,args...);
    * */
    private static Method serviceMethod;//singleton
    private static Object serviceObject;//singleton
    private static Logic logic;//singleton

    public void login(){
        try {
            CombUser combUser=tranSelPriUser.execTransaction(Marco.TRAN_SEL_USER_INFO);
            if(combUser.getPlatformUser().getUserPassword().equals(AppUtil.getRequest("userPassword")[0])){
                return ;
            }else{
                ConfMeta confMeta=CacheFactory.
                        getConfMetaCache().
                        get(Marco.LOGIN_PASSWORD_ERROR);
                AppUtil.setConfMeta(confMeta);
            }
            if(combUser==null){
                AppUtil.putTemp("",new Object());
            }else{
                AppUtil.putTemp("",new Object());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
