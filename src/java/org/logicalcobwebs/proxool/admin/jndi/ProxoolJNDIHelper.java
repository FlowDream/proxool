/*
 * This software is released under a licence similar to the Apache Software Licence.
 * See org.logicalcobwebs.proxool.package.html for details.
 * The latest version is available at http://proxool.sourceforge.net
 */
package org.logicalcobwebs.proxool.admin.jndi;

import org.logicalcobwebs.proxool.ProxoolConstants;
import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.ProxoolManagedDataSource;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * Utilities for Proxool JNDI operations.
 * @version $Revision: 1.1 $, $Date: 2004/03/15 02:47:02 $
 * @author Christian Nedregaard (christian_nedregaard@email.com)
 * @author $Author: chr32 $ (current maintainer)
 * @since Proxool 0.9
 */
public class ProxoolJNDIHelper {
    private ProxoolJNDIHelper() {
    }

    /**
     * Create a {@link org.logicalcobwebs.proxool.ProxoolManagedDataSource} with the given alias
     * and bind it to JNDI using the given jndi properties.
     * @param jndiProperties the jndi related configuration properties.
     * @throws ProxoolException if the JNDI binding failes.
     */
    public static void registerDatasource(String alias, Properties jndiProperties) throws ProxoolException {
        DataSource dataSource = new ProxoolManagedDataSource(alias);
        final String jndiName = jndiProperties.getProperty(ProxoolConstants.JNDI_NAME);
        jndiProperties.remove(ProxoolConstants.JNDI_NAME);
        try {
            InitialContext initalContext = new InitialContext(jndiProperties);
            initalContext.bind(jndiName, dataSource);
        } catch (NamingException e) {
            throw new ProxoolException("JNDI binding of DataSource for alias " + alias
                + " failed.", e);
        }
    }
}

/*
 Revision history:
 $Log: ProxoolJNDIHelper.java,v $
 Revision 1.1  2004/03/15 02:47:02  chr32
 Added initial DataSource support.

*/