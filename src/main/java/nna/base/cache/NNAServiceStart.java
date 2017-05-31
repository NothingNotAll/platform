package nna.base.cache;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author NNA-SHUAI
 * @create 2017-05-25 9:29
 **/

public class NNAServiceStart {

    public static void main(String[] args){

    }

    static{
        try {
            NNAServiceInit0 nna0=new NNAServiceInit0();
            PreparedStatement[] psts=nna0.build();
            NNAServiceInit1 nna1=new NNAServiceInit1(psts);
            nna1.build();
            NNAServiceInit2 nna2=new NNAServiceInit2(psts);
            nna2.build();
            NNAServiceInit3 nna3=new NNAServiceInit3(psts);
            nna3.build();
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
        }
    }
}
