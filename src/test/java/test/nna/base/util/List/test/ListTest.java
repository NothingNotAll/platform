package test.nna.base.util.List.test;

import junit.framework.TestCase;
import nna.base.util.List;

/**
 * ${description}
 *
 * @author NNA-SHUAI
 * @create 2017-2017/5/21-16:59
 */
public class ListTest extends TestCase {
    public static List<String> list10Test=new List<String>(10);
    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        list10Test=null;
    }

    public void testRemoveLast() throws Exception {
        assertNotNull(list10Test);
        System.out.println(list10Test.getCapacity());
        assert list10Test.getCapacity()==10;
    }

    public void testInsert() throws Exception {
    }

    public void testPut() throws Exception {
    }

    public void testDelete() throws Exception {
    }

    public void testUpdate() throws Exception {
    }

    public void testDelete1() throws Exception {
    }

    public void testGet() throws Exception {
    }

    public void testGetSize() throws Exception {
    }

    public void testGetCanGets() throws Exception {
    }

    public void testSetCanGets() throws Exception {
    }

    public void testGetCapacity() throws Exception {
    }

}