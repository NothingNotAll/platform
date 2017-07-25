package nna.base.init;

import nna.Marco;
import nna.MetaBean;
import nna.StoreData;
import nna.base.bean.dbbean.*;
import nna.base.util.orm.ObjectFactory;
import nna.test.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author NNA-SHUAI
 * @create 2017-05-25 9:29
 **/

public class NNAServiceStart {

    public static void main(String[] args){
        System.out.println("NNAService init...");
    }

    static{
//        try {
//            Test.testCon();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        try {
            Util.loadNIOSelector();
            NNAServiceInit0 nna0=new NNAServiceInit0();
            PreparedStatement[] preparedStatements=nna0.build();
            NNAServiceInit1 nna1=new NNAServiceInit1(preparedStatements);
            nna1.build();
            NNAServiceInit2 nna2=new NNAServiceInit2();
            nna2.build();
            StoreData.getConfig().getLog().close();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
