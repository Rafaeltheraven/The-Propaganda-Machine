package net.yura.domination.guishared;

import junit.framework.TestCase;

/**
 * @author Yura Mamyrin
 */
public class RiskUIUtilTest extends TestCase {
    
    public RiskUIUtilTest(String testName) {
        super(testName);
    }

    public void testGetURL() {
        System.out.println("getURL");

        assertEquals("http://yura.net/", RiskUIUtil.getURL("http://yura.net/"));
        assertEquals("http://yura.net/", RiskUIUtil.getURL("hello http://yura.net/ world"));
        assertEquals("http://yura.net/", RiskUIUtil.getURL("hello http://yura.net/\nworld"));
        assertEquals("http://yura.net/", RiskUIUtil.getURL("hello http://yura.net/"));
        assertEquals("http://yura.net/", RiskUIUtil.getURL("hello http://yura.net/ "));
        assertEquals("http://yura.net/", RiskUIUtil.getURL(" http://yura.net/"));
        assertEquals("http://yura.net/", RiskUIUtil.getURL("hello http://yura.net/\n"));
        assertEquals("http://yura.net/", RiskUIUtil.getURL("\n\nhttp://yura.net/\n\n"));
        assertEquals("http://yura.net/", RiskUIUtil.getURL("  http://yura.net/  "));
        assertEquals(null, RiskUIUtil.getURL("hello world"));
        assertEquals(null, RiskUIUtil.getURL(""));
    }
}
