/*
 * This software is released under the Apache Software Licence. See
 * package.html for details. The latest version is available at
 * http://proxool.sourceforge.net
 */
package org.logicalcobwebs.proxool;

import org.logicalcobwebs.logging.Log;
import org.logicalcobwebs.logging.LogFactory;

import java.util.Properties;
import java.sql.Connection;
import java.sql.DriverManager;

import junit.framework.TestCase;

/**
 * Test the house keeper in ConnectionPool
 *
 * @version $Revision: 1.1 $, $Date: 2003/02/27 18:01:48 $
 * @author bill
 * @author $Author: billhorsman $ (current maintainer)
 * @since Proxool 0.8
 */
public class HouseKeeperTest extends TestCase {

    private static final Log LOG = LogFactory.getLog(HouseKeeperTest.class);

    public HouseKeeperTest(String alias) {
        super(alias);
    }

    protected void setUp() throws Exception {
        GlobalTest.globalSetup();
    }

    protected void tearDown() throws Exception {
        GlobalTest.globalTeardown();
    }

    /**
     * Test that connections that remain active for longer than the configured
     * time are closed (and destroyed) automatically.
     */
    public void testMaximumActiveTime() throws Exception {

        String testName = "maximumActiveTime";
        String threadName = Thread.currentThread().getName();
        Thread.currentThread().setName(testName);
        String alias = testName;
        try {
            String url = TestHelper.buildProxoolUrl(alias,
                    TestConstants.HYPERSONIC_DRIVER,
                    TestConstants.HYPERSONIC_TEST_URL);
            Properties info = new Properties();
            info.setProperty(ProxoolConstants.USER_PROPERTY, TestConstants.HYPERSONIC_USER);
            info.setProperty(ProxoolConstants.PASSWORD_PROPERTY, TestConstants.HYPERSONIC_PASSWORD);
            info.setProperty(ProxoolConstants.MAXIMUM_ACTIVE_TIME_PROPERTY, "1000");
            info.setProperty(ProxoolConstants.HOUSE_KEEPING_SLEEP_TIME_PROPERTY, "1000");
            ProxoolFacade.registerConnectionPool(url, info);

            assertEquals("Shouldn't be any active connections yet", 0, ProxoolFacade.getSnapshot(alias, false).getServedCount());

            Connection connection = DriverManager.getConnection(url);;

            assertEquals("We just opened 1 connection", 1, ProxoolFacade.getSnapshot(alias, false).getServedCount());

            long start = System.currentTimeMillis();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                LOG.debug("Awoken.");
            }

            long elapsed = System.currentTimeMillis() - start;
            assertTrue("Connection has not been closed after " + elapsed + " milliseconds as expected", connection.isClosed());

            assertEquals("Expected the connection to be inactive", 0, ProxoolFacade.getSnapshot(alias, false).getActiveConnectionCount());

        } catch (Exception e) {
            LOG.error("Whilst performing " + testName, e);
            throw e;
        } finally {
            ProxoolFacade.removeConnectionPool(alias);
            Thread.currentThread().setName(threadName);
        }

    }

    /**
     * Test that house keeper destroys connections that fail configured
     * the test sql
     */
    public void testHouseKeeperTestSql() throws Exception {

        String testName = "houseKeeperTestSql";
        String alias = testName;
        try {

            // TODO complete this test

        } catch (Exception e) {
            LOG.error("Whilst performing " + testName, e);
            throw e;
        }

    }

}


/*
 Revision history:
 $Log: HouseKeeperTest.java,v $
 Revision 1.1  2003/02/27 18:01:48  billhorsman
 completely rethought the test structure. it's now
 more obvious. no new tests yet though.

 */