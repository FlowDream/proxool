/*
* Copyright 2002, Findexa AS (http://www.findexa.no)
*
* This software is the proprietary information of Findexa AS.
* Use is subject to license terms.
*/
package org.logicalcobwebs.proxool.configuration;

import junit.framework.TestCase;

import java.sql.SQLException;

import org.logicalcobwebs.proxool.configuration.JAXPConfigurator;
import org.logicalcobwebs.proxool.AllTests;
import org.logicalcobwebs.proxool.TestHelper;
import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.ProxoolFacade;

/**
 * Tests that the JAXPConfgiuration works in various scenarios.
 * This is also a test of the {@link XMLConfigurator}, as it is delegated to.
 *
 * @version $Revision: 1.1 $, $Date: 2002/12/15 19:10:49 $
 * @author Christian Nedregaard (christian_nedregaard@email.com)
 * @author $Author: chr32 $ (current maintainer)
 * @since Proxool 0.6
 */
public class JAXPConfiguratorTest extends TestCase {

    /**
     * @see TestCase#TestCase
     */
    public JAXPConfiguratorTest(String name) {
        super(name);
    }

    /**
     * Test that the confguration succeds and that all expected properties
     * has been received by Proxool. This test is done with a
     * xml without namespaces and validiation.
     * @throws ProxoolException if the configuration fails.
     * @throws SQLException if ProxoolFacade operation fails.
     */
    public void testJAXPXMLConfiguratorNoNamspaces() throws ProxoolException, SQLException{
        final String xmlFile = "src/java-test/org/logicalcobwebs/proxool/configuration/test-no-ns.xml";
        JAXPConfigurator.configure(xmlFile, false);
        try {
            assertNotNull("2nd (deeply nested) pool was not configured.", ProxoolFacade.getConnectionPoolDefinition("xml-test-2"));
        } catch (SQLException e) {
            fail("2nd (deeply nested) pool was not configured.");
        }
        try {
            TestHelper.equalsCompleteAlternativeProperties(ProxoolFacade.getConnectionPoolDefinition("xml-test"));
        } catch (ProxoolException e) {
            fail(e.getMessage());
        }
        ProxoolFacade.removeConnectionPool("xml-test");
        ProxoolFacade.removeConnectionPool("xml-test-2");
    }

    /**
     * Test that the confguration succeds and that all expected properties
     * has been received by Proxool. This test is done with a
     * xml with namespaces and without validiation.
     * @throws ProxoolException if the configuration fails.
     * @throws SQLException if ProxoolFacade operation fails.
     */
    public void testJAXPXMLConfiguratorWithNamspaces() throws ProxoolException, SQLException{
        final String xmlFile = "src/java-test/org/logicalcobwebs/proxool/configuration/test-ns.xml";
        JAXPConfigurator.configure(xmlFile, false);
        try {
            assertNotNull("2nd (deeply nested) pool was not configured.", ProxoolFacade.getConnectionPoolDefinition("xml-test-ns-2"));
        } catch (SQLException e) {
            fail("2nd (deeply nested) pool was not configured.");
        }
        try {
            TestHelper.equalsCompleteAlternativeProperties(ProxoolFacade.getConnectionPoolDefinition("xml-test-ns"));
        } catch (ProxoolException e) {
            fail(e.getMessage());
        }
        ProxoolFacade.removeConnectionPool("xml-test-ns");
        ProxoolFacade.removeConnectionPool("xml-test-ns-2");
    }

    /**
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        AllTests.globalSetup();
    }

    /**
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        AllTests.globalTeardown();
    }

}

/*
 Revision history:
 $Log: JAXPConfiguratorTest.java,v $
 Revision 1.1  2002/12/15 19:10:49  chr32
 Init rev.

*/
