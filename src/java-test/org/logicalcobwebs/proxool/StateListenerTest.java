/*
 * This software is released under the Apache Software Licence. See
 * package.html for details. The latest version is available at
 * http://proxool.sourceforge.net
 */
package org.logicalcobwebs.proxool;

import junit.framework.TestCase;

import java.util.Properties;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

import org.logicalcobwebs.logging.Log;
import org.logicalcobwebs.logging.LogFactory;

/**
 * Test that registering a {@link ConfigurationListenerIF} with the {@link ProxoolFacade}
 * works.
 *
 * @version $Revision: 1.3 $, $Date: 2003/02/27 18:01:48 $
 * @author Christian Nedregaard (christian_nedregaard@email.com)
 * @author $Author: billhorsman $ (current maintainer)
 * @since Proxool 0.7
 */
public class StateListenerTest extends TestCase {

    private static final Log LOG = LogFactory.getLog(StateListenerTest.class);

    /**
     * @see TestCase#TestCase
     */
    public StateListenerTest(String s) {
        super(s);
    }

    /**
     * Test whether we can add a state listener and that it receives
     * notification of change of state
     */
    public void testAddStateListener() throws Exception {

        String testName = "addStateListener";
        String alias = testName;
        try {
            String url = TestHelper.buildProxoolUrl(alias,
                    TestConstants.HYPERSONIC_DRIVER,
                    TestConstants.HYPERSONIC_TEST_URL);
            Properties info = new Properties();
            info.setProperty(ProxoolConstants.USER_PROPERTY, TestConstants.HYPERSONIC_USER);
            info.setProperty(ProxoolConstants.PASSWORD_PROPERTY, TestConstants.HYPERSONIC_PASSWORD);
            info.setProperty(ProxoolConstants.MAXIMUM_CONNECTION_COUNT_PROPERTY, "1");
            info.setProperty(ProxoolConstants.HOUSE_KEEPING_SLEEP_TIME_PROPERTY, "5000");
            info.setProperty(ProxoolConstants.OVERLOAD_WITHOUT_REFUSAL_LIFETIME_PROPERTY, "5000");
            ProxoolFacade.registerConnectionPool(url, info);

            assertEquals("maximumConnectionCount", 1, ProxoolFacade.getConnectionPoolDefinition(alias).getMaximumConnectionCount());

            TestStateListener stateListener = new TestStateListener();
            ProxoolFacade.addStateListener(alias, stateListener);

            assertEquals("maximumConnectionCount", 1, ProxoolFacade.getConnectionPoolDefinition(alias).getMaximumConnectionCount());

            Connection c1 = DriverManager.getConnection(url);

            // Test BUSY
            assertEquals("upState", StateListenerIF.STATE_BUSY, stateListener.getNextState());

            assertEquals("maximumConnectionCount", 1, ProxoolFacade.getConnectionPoolDefinition(alias).getMaximumConnectionCount());

            try {
                Connection c2 = DriverManager.getConnection(url);
                fail("Didn't expect second connection since maximumConnectionCount is 1");
            } catch (SQLException e) {
                // We expect a refusal here
                LOG.debug("Expected refusal", e);
            }

            // Test Overloaded
            assertEquals("upState", StateListenerIF.STATE_OVERLOADED, stateListener.getNextState());

            // Test Busy again
            assertEquals("upState", StateListenerIF.STATE_BUSY, stateListener.getNextState());

            // Test Quiet again
            c1.close();
            assertEquals("upState", StateListenerIF.STATE_QUIET, stateListener.getNextState());

            // Bogus definition -> should be down
            ProxoolFacade.updateConnectionPool("proxool." + alias + ":blah:foo", null);
            assertEquals("upState", StateListenerIF.STATE_DOWN, stateListener.getNextState());

        } catch (Exception e) {
            LOG.error("Whilst performing " + testName, e);
            throw e;
        } finally {
            ProxoolFacade.removeConnectionPool(alias);
        }

    }


    /**
     * Calls {@link GlobalTest#globalSetup}
     * @see TestCase#setUp
     */
    protected void setUp() throws Exception {
        GlobalTest.globalSetup();
        Class.forName("org.logicalcobwebs.proxool.ProxoolDriver");
    }

    /**
     * Calls {@link GlobalTest#globalTeardown}
     * @see TestCase#setUp
     */
    protected void tearDown() throws Exception {
        GlobalTest.globalTeardown();
    }

    class TestStateListener implements StateListenerIF {

        private boolean somethingHappened;

        private int upState;

        public void upStateChanged(int newUpState) {
            LOG.debug("upState: " + upState + " -> " + newUpState);
            upState = newUpState;
            somethingHappened = true;
        }

        boolean isSomethingHappened() {
            return somethingHappened;
        }

        int getUpState() {
            return upState;
        }

        void reset() {
            upState = 0;
            somethingHappened = false;
        }

        void waitForSomethingToHappen() {

            long start = System.currentTimeMillis();
            while (!somethingHappened) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    LOG.error("Awoken", e);
                }
                if (System.currentTimeMillis() - start > 30000) {
                    fail("Timeout waiting for something to happen");
                }
            }

        }

        int getNextState() {
            waitForSomethingToHappen();
            somethingHappened = false;
            return upState;
        }
    }
}

/*
 Revision history:
 $Log: StateListenerTest.java,v $
 Revision 1.3  2003/02/27 18:01:48  billhorsman
 completely rethought the test structure. it's now
 more obvious. no new tests yet though.

 Revision 1.2  2003/02/26 16:05:50  billhorsman
 widespread changes caused by refactoring the way we
 update and redefine pool definitions.

 Revision 1.1  2003/02/19 23:07:57  billhorsman
 new test

 Revision 1.2  2003/02/19 15:14:22  billhorsman
 fixed copyright (copy and paste error,
 not copyright change)

 Revision 1.1  2003/02/19 13:47:31  chr32
 Added configuration listener test.

 Revision 1.2  2003/02/18 16:58:12  chr32
 Checkstyle.

 Revision 1.1  2003/02/18 16:51:20  chr32
 Added tests for ConnectionListeners.

*/
