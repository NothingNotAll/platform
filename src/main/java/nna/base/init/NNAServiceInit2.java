package nna.base.init;

import nna.base.db.DBCon;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * @author NNA-SHUAI
 * @create 2017-05-24 20:21
 **/

public class NNAServiceInit2 {
    public static HashMap<Integer,DBCon> dbConMap=new HashMap<Integer, DBCon>();

    public void build() throws IllegalAccessException, InvocationTargetException, InstantiationException, SQLException, NoSuchMethodException, ClassNotFoundException, IOException {

    }
}
