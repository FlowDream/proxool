/*
* Copyright 2002, Findexa AS (http://www.findexa.no)
*
* This software is the proprietary information of Findexa AS.
* Use is subject to license terms.
*/
package org.logicalcobwebs.proxool;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.logicalcobwebs.dbscript.ScriptFacade;
import org.logicalcobwebs.dbscript.CommandFilterIF;
import org.logicalcobwebs.dbscript.CommandIF;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Test whether the {@link ConnectionResetter} works.
 *
 * @version $Revision: 1.4 $, $Date: 2002/11/12 20:24:12 $
 * @author Bill Horsman (bill@logicalcobwebs.co.uk)
 * @author $Author: billhorsman $ (current maintainer)
 * @since Proxool 0.5
 */
public class ConnectionResetterTest extends TestCase {

    private static final Log LOG = LogFactory.getLog(ConnectionResetterTest.class);

    /**
     * @see TestCase#TestCase
     */
    public ConnectionResetterTest(String s) {
        super(s);
    }

    /**
     * Calls {@link AllTests#globalSetup}
     * @see TestCase#setUp
     */
    protected void setUp() throws Exception {
        AllTests.globalSetup();
    }

    /**
     * Calls {@link AllTests#globalTeardown}
     * @see TestCase#setUp
     */
    protected void tearDown() throws Exception {
        AllTests.globalTeardown();
    }

    /**
     * Test whether autoCommit is correctly reset when a connection is
     * returned to the pool.
     */
    public void testAutoCommit() {
        String scriptLocation = System.getProperty("script");
        if (scriptLocation != null) {
            ScriptFacade.runScript(scriptLocation, new ProxoolAdapter(), new CommandFilterIF() {

                public boolean beforeCommand(Connection connection, CommandIF command) throws SQLException {
                    connection.setAutoCommit(false);
                    return true;
                }

                public void afterCommand(Connection connection, CommandIF command) throws SQLException {
                    // TODO
                }

                public boolean catchException(CommandIF command, SQLException e) {
                    return true;
                }

            });

        } else {
            LOG.info("Skipping autoCommit test because 'script' System Property was not set");
        }
    }

}

/*
 Revision history:
 $Log: ConnectionResetterTest.java,v $
 Revision 1.4  2002/11/12 20:24:12  billhorsman
 checkstyle

 Revision 1.3  2002/11/12 20:18:26  billhorsman
 Made connection resetter a bit more friendly. Now, if it encounters any problems during
 reset then that connection is thrown away. This is going to cause you problems if you
 always close connections in an unstable state (e.g. with transactions open. But then
 again, it's better to know about that as soon as possible, right?

 Revision 1.2  2002/11/09 16:01:21  billhorsman
 fixed CommandFilterIF implementation

 Revision 1.1  2002/11/06 21:08:02  billhorsman
 new ConnectionResetter test

*/
