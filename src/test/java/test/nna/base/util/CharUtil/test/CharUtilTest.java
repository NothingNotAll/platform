package test.nna.base.util.CharUtil.test;

import junit.framework.TestCase;
import nna.base.util.CharUtil;

/**
 * ${description}
 *
 * @author NNA-SHUAI
 * @create 2017-2017/5/23-15:05
 */
public class CharUtilTest extends TestCase {
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testNexts() throws Exception {
        int[] nexts=CharUtil.nexts("-->");
        System.out.println(nexts[0]);
        assert nexts[0]==0;
        System.out.println(nexts[1]);
        assert nexts[1]==0;
        System.out.println(nexts[2]);
        assert nexts[2]==1;
    }

}