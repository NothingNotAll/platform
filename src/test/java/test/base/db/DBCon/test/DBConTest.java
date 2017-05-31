package test.base.db.DBCon.test;

import junit.framework.TestCase;
import nna.base.db.DBCon;
import nna.base.db.DBMeta;

/**
 * ${description}
 *
 * @author NNA-SHUAI
 * @create 2017-2017/5/29-14:58
 */
public class DBConTest extends TestCase {
    public void testInit() throws Exception {
        DBCon dBcon=DBCon.init(new DBMeta(
                true,
                "jdbc:mysql://localhost:3306/platformv2",
                "com.mysql.jdbc.Driver",
                "root",
                "123",
                1,
                1,
                3,
                100L,
                100
        ),null);
        System.out.println(dBcon.getCon());
    }

    public void testGetCon() throws Exception {
    }

    public void testPutCon() throws Exception {
        DBCon dBcon=DBCon.init(new DBMeta(
                true,
                "jdbc:mysql://localhost:3306/platformv2",
                "com.mysql.jdbc.Driver",
                "root",
                "123",
                1,
                1,
                3,
                100L,
                100
        ),null);
        dBcon.putCon(dBcon.getCon());
    }

    public void testAsyPutCon() throws Exception {
    }

}