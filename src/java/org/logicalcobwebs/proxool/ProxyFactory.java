/*
* Copyright 2002, Findexa AS (http://www.findexa.no)
*
* This software is the proprietary information of Findexa AS.
* Use is subject to license terms.
*/
package org.logicalcobwebs.proxool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A central place to build proxy objects ({@link ProxyConnection connections}
 * and {@link ProxyStatement statements}).
 *
 * @version $Revision: 1.11 $, $Date: 2003/01/27 18:26:39 $
 * @author Bill Horsman (bill@logicalcobwebs.co.uk)
 * @author $Author: billhorsman $ (current maintainer)
 * @since Proxool 0.5
 */
class ProxyFactory {

    private static final Log LOG = LogFactory.getLog(ProxyFactory.class);

    protected static ProxyConnection buildProxyConnection(long id, ConnectionPool connectionPool) throws SQLException {
        Connection realConnection = null;
        realConnection = DriverManager.getConnection(
                connectionPool.getDefinition().getUrl(),
                connectionPool.getDefinition().getProperties());

        Object delegate = Proxy.newProxyInstance(
                realConnection.getClass().getClassLoader(),
                realConnection.getClass().getInterfaces(),
                new ProxyConnection(realConnection, id, connectionPool));

        return (ProxyConnection) Proxy.getInvocationHandler(delegate);
    }

    /**
     * Get a Connection from the ProxyConnection
     *
     * @param proxyConnection where to find the connection
     * @return
     */
    protected static Connection getConnection(ProxyConnectionIF proxyConnection) {
        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class[]{Connection.class},
                (InvocationHandler) proxyConnection);
    }

    /**
     * Gets the real Statement that we got from the delegate driver
     * @return delegate statement
     */
    protected static Statement getDelegateStatement(Statement statement) {
        Statement ds = statement;
        ProxyStatement ps = (ProxyStatement) Proxy.getInvocationHandler(statement);
        ds = ps.getDelegateStatement();
        return ds;
    }

    protected static Statement createProxyStatement(Statement delegate, ConnectionPool connectionPool, ProxyConnectionIF proxyConnection,  String sqlStatement) {
        // We can't use Class#getInterfaces since that doesn't take
        // into account superclass interfaces. We could, laboriously,
        // work our way up the hierarchy but it doesn't seem worth while -
        // we only actually expect three options:
        Class[] interfaces = new Class[1];
        if (delegate instanceof CallableStatement) {
            interfaces[0] = CallableStatement.class;
        } else if (delegate instanceof PreparedStatement) {
            interfaces[0] = PreparedStatement.class;
        } else {
            interfaces[0] = Statement.class;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(delegate.getClass().getName() + " is being proxied using the " + interfaces[0]);
        }
        return (Statement) Proxy.newProxyInstance(delegate.getClass().getClassLoader(), interfaces, new ProxyStatement(delegate, connectionPool, proxyConnection, sqlStatement));
    }


}

/*
 Revision history:
 $Log: ProxyFactory.java,v $
 Revision 1.11  2003/01/27 18:26:39  billhorsman
 refactoring of ProxyConnection and ProxyStatement to
 make it easier to write JDK 1.2 patch

 Revision 1.10  2002/12/16 11:15:19  billhorsman
 fixed getDelegateStatement

 Revision 1.9  2002/12/16 10:57:47  billhorsman
 add getDelegateStatement to allow access to the
 delegate JDBC driver's Statement

 Revision 1.8  2002/12/12 10:48:25  billhorsman
 checkstyle

 Revision 1.7  2002/12/08 22:17:35  billhorsman
 debug for proxying statement interfaces

 Revision 1.6  2002/12/06 15:57:08  billhorsman
 fix for proxied statement where Statement interface is not directly
 implemented.

 Revision 1.5  2002/12/03 12:24:00  billhorsman
 fixed fatal sql exception

 Revision 1.4  2002/11/09 15:56:52  billhorsman
 fix doc

 Revision 1.3  2002/11/02 14:22:15  billhorsman
 Documentation

 Revision 1.2  2002/10/30 21:25:08  billhorsman
 move createStatement into ProxyFactory

 Revision 1.1  2002/10/30 21:19:16  billhorsman
 make use of ProxyFactory

*/
