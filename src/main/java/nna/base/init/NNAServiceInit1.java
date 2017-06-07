package nna.base.init;

import nna.Marco;
import nna.base.bean.Clone;
import nna.base.bean.dbbean.*;
import nna.base.proxy.ProxyFactory;
import nna.base.proxy.ProxyService;
import nna.transaction.AbstractTransaction;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;


/**
 * @author NNA-SHUAI
 * @create 2017-05-24 14:39
 **/

public class NNAServiceInit1 {
    public static PreparedStatement[] psts;

    public NNAServiceInit1(PreparedStatement[] psts){
        this.psts=psts;
    }
    public void build() throws IllegalAccessException, InvocationTargetException, InstantiationException, SQLException, NoSuchMethodException, ClassNotFoundException {

    }

    private void buildTran(PreparedStatement pst) {

    }



}
