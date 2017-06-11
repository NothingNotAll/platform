package nna.app.web;

import nna.app.tran.ext.TranSelPriUser;
import nna.base.bean.dbbean.PlatformUser;
import nna.base.proxy.Logic;
import nna.transaction.TransExecutor;

import java.lang.reflect.Method;

/**
 * the goal is that the code can been config by person instead of write by hand.
 * @author NNA-SHUAI
 * @create 2017-05-26 13:14
 **/

public class NNALogin {
    TransExecutor<PlatformUser> tranSelPriUser=new TranSelPriUser();//next step is to cancel the new action;

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
    }
}
