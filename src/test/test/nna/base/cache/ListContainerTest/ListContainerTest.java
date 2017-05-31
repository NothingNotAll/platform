package test.nna.base.cache.ListContainerTest;

import junit.framework.TestCase;
import nna.base.cache.ListContainer;

/**
 * ${description}
 *
 * @author NNA-SHUAI
 * @create 2017-2017/5/18-22:03
 */
public class ListContainerTest extends TestCase {
    private static ListContainer<Integer> listContainer=new ListContainer<Integer>(10);
    public void setUp() throws Exception {
        super.setUp();
        assertEquals(0,listContainer.getSize());
    }

    public void tearDown() throws Exception {
    }

    public void testRemoveLast() throws Exception {
    }

    public void testInsert() throws Exception {
        ListContainer.ReturnMessage returnMessage=listContainer.insert(0);
        assertEquals(null,returnMessage.result);
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

}