/*
 * This software is released under a licence similar to the Apache Software Licence.
 * See org.logicalcobwebs.proxool.package.html for details.
 * The latest version is available at http://proxool.sourceforge.net
 */
package org.logicalcobwebs.proxool;

import org.logicalcobwebs.logging.Log;
import org.logicalcobwebs.logging.LogFactory;
import org.logicalcobwebs.logging.impl.SimpleLog;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * A minimal check to see what libraries we need to include with
 * our binary distribution
 *
 * @version $Revision: 1.1 $, $Date: 2003/10/25 18:38:17 $
 * @author Bill Horsman {bill@logicalcobwebs.co.uk)
 * @author $Author: billhorsman $ (current maintainer)
 * @since Proxool 0.8
 */
public class DependencyCheck {

    /**
     * A minimal check to see what libraries we need to include with
     * our binary distribution
     */
    public static void main(String[] args)  {

        try {
            System.setProperty("org.apache.commons.logging.Log", SimpleLog.class.getName());
            System.setProperty(Log.class.getName(), SimpleLog.class.getName());
            System.setProperty("org.apache.commons.logging.simplelog.defaultlog", "debug");

            Log log = LogFactory.getLog(DependencyCheck.class);
            log.info("Can you read this?");
            log.debug("Can you read this?");
            Class.forName(ProxoolDriver.class.getName());
            String alias = "dependencyCheck";
            String url = TestHelper.buildProxoolUrl(alias,
                    TestConstants.HYPERSONIC_DRIVER,
                    TestConstants.HYPERSONIC_TEST_URL);
            Properties info = new Properties();
            info.setProperty(ProxoolConstants.USER_PROPERTY, TestConstants.HYPERSONIC_USER);
            info.setProperty(ProxoolConstants.PASSWORD_PROPERTY, TestConstants.HYPERSONIC_PASSWORD);
            DriverManager.getConnection(url, info).close();
            System.out.println("Done");
        } catch (Throwable e) {
            if (e instanceof ProxoolException) {
                e = ((ProxoolException) e).getCause();
            }
            e.printStackTrace();
            System.out.println("Fail");
        } finally {
            ProxoolFacade.shutdown(0);
        }

    }

}

/*
 Revision history:
 $Log: DependencyCheck.java,v $
 Revision 1.1  2003/10/25 18:38:17  billhorsman
 Not a test, just a standalone class you can run to see what libraries you need to use Proxool.
 It just checks the core features - you're gonna need other libraries for things like JAXP
 configuration, etc.

 */
