/*
 * This software is released under the Apache Software Licence. See
 * package.html for details. The latest version is available at
 * http://proxool.sourceforge.net
 */
package org.logicalcobwebs.proxool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 * Contains most of the functionality that we require to manipilate the
 * statement. The subclass of this defines how we delegate to the
 * real statement.

 * @version $Revision: 1.1 $, $Date: 2003/01/27 18:26:35 $
 * @author bill
 * @author $Author: billhorsman $ (current maintainer)
 * @since Proxool 0.7
 */
abstract class AbstractProxyStatement {

    private static final Log LOG = LogFactory.getLog(ProxyStatement.class);

    private Statement statement;

    private ConnectionPool connectionPool;

    private ProxyConnectionIF proxyConnection;

    private Map parameters;

    private String sqlStatement;

    /**
     * @param statement the real statement that we will delegate to
     * @param connectionPool the connection pool that we are using
     * @param proxyConnection the connection that was used to create the statement
     * @param sqlStatement the SQL statement that was used to create this statement
     * (optional, can be null) so that we can use if for tracing.
     */
    public AbstractProxyStatement(Statement statement, ConnectionPool connectionPool, ProxyConnectionIF proxyConnection, String sqlStatement) {
        this.statement = statement;
        this.connectionPool = connectionPool;
        this.proxyConnection = proxyConnection;
        this.sqlStatement = sqlStatement;
    }

    /**
     * Check to see whether an exception is a fatal one. If it is, then throw the connection
     * away (and it won't be made available again)
     * @param e the exception to test
     */
    protected void testException(SQLException e) {
        Iterator i = connectionPool.getDefinition().getFatalSqlExceptions().iterator();
        while (i.hasNext()) {
            if (e.getMessage().indexOf((String) i.next()) > -1) {
                // This SQL exception indicates a fatal problem with this connection. We should probably
                // just junk it.
                try {
                    statement.close();
                    connectionPool.throwConnection(proxyConnection);
                    LOG.warn("Connection has been thrown away because fatal exception was detected", e);
                } catch (SQLException e2) {
                    LOG.error("Problem trying to throw away suspect connection", e2);
                }
            }
        }
    }

    /**
     * Gets the real Statement that we got from the delegate driver
     * @return delegate statement
     */
    public Statement getDelegateStatement() {
        return statement;
    }

    /**
     * The connection pool we are using
     * @return connectionPool
     */
    protected ConnectionPool getConnectionPool() {
        return connectionPool;
    }

    /**
     * The real, delegate statement
     * @return statement
     */
    protected Statement getStatement() {
        return statement;
    }

    /**
     * Close the statement and tell the ProxyConnection that it did so.
     * @throws SQLException if it couldn't be closed
     * @see ProxyConnectionIF#registerClosedStatement
     */
    protected void close() throws SQLException {
        statement.close();
        proxyConnection.registerClosedStatement(statement);
    }

    /**
     * Whether the delegate statements are the same
     * @see Object#equals
     */
    public boolean equals(Object obj) {
        return (statement.hashCode() == obj.hashCode());
    }

    /**
     * Add a parameter so that we can show its value when tracing
     * @param index within the procedure
     * @param value an object describing its value
     */
    protected void putParameter(int index, Object value) {

        // Lazily instantiate parameters if necessary
        if (parameters == null) {
            parameters = new TreeMap(new Comparator() {
                public int compare(Object o1, Object o2) {
                    int c = 0;

                    if (o1 instanceof Integer && o2 instanceof Integer) {
                        c = ((Integer) o1).compareTo(((Integer) o2));
                    }

                    return c;
                }
            });
        }

        Object key = new Integer(index);
        if (value == null) {
            parameters.put(key, "*");
        } else if (value instanceof String) {
            parameters.put(key, "'" + value + "'");
        } else if (value instanceof Number) {
            parameters.put(key, value);
        } else {
            String className = value.getClass().getName();
            StringTokenizer st = new StringTokenizer(className, ".");
            while (st.hasMoreTokens()) {
                className = st.nextToken();
            }
            parameters.put(key, className);
        }
    }

    /**
     * Trace the call that was just made
     * @param startTime so we can log how long it took
     * @param exception if anything went wrong during execution
     * @throws SQLException if the {@link ConnectionPool#onExecute onExecute} method threw one.
     */
    protected void trace(long startTime, Exception exception) throws SQLException {

        // Log if configured to
        if (connectionPool.getLog().isDebugEnabled() && connectionPool.getDefinition().isTrace()) {
            if (parameters != null) {
                connectionPool.getLog().debug(parameters + " -> " + sqlStatement + " (" + (System.currentTimeMillis() - startTime) + " milliseconds)");
            } else {
                connectionPool.getLog().debug(sqlStatement + " (" + (System.currentTimeMillis() - startTime) + " milliseconds)");
            }
        }

        // Send to any listener
        connectionPool.onExecute(parameters + " -> " + sqlStatement, (System.currentTimeMillis() - startTime), exception);

        // Clear parameters for next time
        if (parameters != null) {
            parameters.clear();
        }
        sqlStatement = null;

    }

}


/*
 Revision history:
 $Log: AbstractProxyStatement.java,v $
 Revision 1.1  2003/01/27 18:26:35  billhorsman
 refactoring of ProxyConnection and ProxyStatement to
 make it easier to write JDK 1.2 patch

 */