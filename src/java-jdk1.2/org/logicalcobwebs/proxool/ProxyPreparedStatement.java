/*
 * This software is released under the Apache Software Licence. See
 * package.html for details. The latest version is available at
 * http://proxool.sourceforge.net
 */
package org.logicalcobwebs.proxool;

import org.logicalcobwebs.proxool.ProxyStatement;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Delegates to PreparedStatement for all calls. But also, for all execute methods, it
 * checks the SQLException and compares it to the fatalSqlException list in the
 * ConnectionPoolDefinition. If it detects a fatal exception it will destroy the
 * Connection so that it isn't used again.
 * @version $Revision: 1.3 $, $Date: 2003/01/28 11:55:04 $
 * @author billhorsman
 * @author $Author: billhorsman $ (current maintainer)
 */
public class ProxyPreparedStatement extends ProxyStatement implements PreparedStatement {

    private PreparedStatement preparedStatement;

    public ProxyPreparedStatement(PreparedStatement preparedStatement, ConnectionPool connectionPool, ProxyConnectionIF proxyConnection, String sqlStatement) {
        super(preparedStatement, connectionPool, proxyConnection, sqlStatement);
        this.preparedStatement = preparedStatement;
    }

    public ResultSet executeQuery() throws SQLException {
        long startTime = System.currentTimeMillis();
        Exception exception = null;
        try {
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            testException(e);
            exception = e;
            throw e;
        } finally {
            trace(startTime, exception);
        }
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        long startTime = System.currentTimeMillis();
        Exception exception = null;
        try {
            return preparedStatement.executeQuery(sql);
        } catch (SQLException e) {
            testException(e);
            exception = e;
            throw e;
        } finally {
            trace(startTime, exception);
        }
    }

    public int executeUpdate() throws SQLException {
        long startTime = System.currentTimeMillis();
        Exception exception = null;
        try {
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            testException(e);
            exception = e;
            throw e;
        } finally {
            trace(startTime, exception);
        }
    }

    public int executeUpdate(String sql) throws SQLException {
        long startTime = System.currentTimeMillis();
        Exception exception = null;
        try {
            return preparedStatement.executeUpdate(sql);
        } catch (SQLException e) {
            testException(e);
            exception = e;
            throw e;
        } finally {
            trace(startTime, exception);
        }
    }

    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        preparedStatement.setNull(parameterIndex, sqlType);
        if (isTrace()) {
            putParameter(parameterIndex, "*");
        }
    }

    public void close() throws SQLException {
        preparedStatement.close();
    }

    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        preparedStatement.setBoolean(parameterIndex, x);
        if (isTrace()) {
            putParameter(parameterIndex, String.valueOf(x));
        }
    }

    public int getMaxFieldSize() throws SQLException {
        return preparedStatement.getMaxFieldSize();
    }

    public void setByte(int parameterIndex, byte x) throws SQLException {
        preparedStatement.setByte(parameterIndex, x);
        if (isTrace()) {
            putParameter(parameterIndex, String.valueOf(x));
        }
    }

    public void setMaxFieldSize(int max) throws SQLException {
        preparedStatement.setMaxFieldSize(max);
    }

    public void setShort(int parameterIndex, short x) throws SQLException {
        preparedStatement.setShort(parameterIndex, x);
        if (isTrace()) {
            putParameter(parameterIndex, String.valueOf(x));
        }
    }

    public int getMaxRows() throws SQLException {
        return preparedStatement.getMaxRows();
    }

    public void setInt(int parameterIndex, int x) throws SQLException {
        preparedStatement.setInt(parameterIndex, x);
        if (isTrace()) {
            putParameter(parameterIndex, String.valueOf(x));
        }
    }

    public void setMaxRows(int max) throws SQLException {
        preparedStatement.setMaxRows(max);
    }

    public void setLong(int parameterIndex, long x) throws SQLException {
        preparedStatement.setLong(parameterIndex, x);
        if (isTrace()) {
            putParameter(parameterIndex, String.valueOf(x));
        }
    }

    public void setEscapeProcessing(boolean enable) throws SQLException {
        preparedStatement.setEscapeProcessing(enable);
    }

    public void setFloat(int parameterIndex, float x) throws SQLException {
        preparedStatement.setFloat(parameterIndex, x);
        if (isTrace()) {
            putParameter(parameterIndex, String.valueOf(x));
        }
    }

    public int getQueryTimeout() throws SQLException {
        return preparedStatement.getQueryTimeout();
    }

    public void setDouble(int parameterIndex, double x) throws SQLException {
        preparedStatement.setDouble(parameterIndex, x);
        if (isTrace()) {
            putParameter(parameterIndex, String.valueOf(x));
        }
    }

    public void setQueryTimeout(int seconds) throws SQLException {
        preparedStatement.setQueryTimeout(seconds);
    }

    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        preparedStatement.setBigDecimal(parameterIndex, x);
        if (isTrace()) {
            putParameter(parameterIndex, String.valueOf(x));
        }
    }

    public void cancel() throws SQLException {
        preparedStatement.cancel();
    }

    public void setString(int parameterIndex, String x) throws SQLException {
        preparedStatement.setString(parameterIndex, x);
        if (isTrace()) {
            putParameter(parameterIndex, x);
        }
    }

    public SQLWarning getWarnings() throws SQLException {
        return preparedStatement.getWarnings();
    }

    public void setBytes(int parameterIndex, byte x[]) throws SQLException {
        preparedStatement.setBytes(parameterIndex, x);
        if (isTrace()) {
            putParameter(parameterIndex, String.valueOf(x));
        }
    }

    public void clearWarnings() throws SQLException {
        preparedStatement.clearWarnings();
    }

    public void setDate(int parameterIndex, Date x)
            throws SQLException {
        preparedStatement.setDate(parameterIndex, x);
        if (isTrace()) {
            putParameter(parameterIndex, x);
        }
    }

    public void setCursorName(String name) throws SQLException {
        preparedStatement.setCursorName(name);
    }

    public void setTime(int parameterIndex, Time x)
            throws SQLException {
        preparedStatement.setTime(parameterIndex, x);
        if (isTrace()) {
            putParameter(parameterIndex, String.valueOf(x));
        }
    }

    public boolean execute(String sql) throws SQLException {
        try {
            return preparedStatement.execute(sql);
        } catch (SQLException e) {
            testException(e);
            throw e;
        }
    }

    public void setTimestamp(int parameterIndex, Timestamp x)
            throws SQLException {
        preparedStatement.setTimestamp(parameterIndex, x);
        if (isTrace()) {
            putParameter(parameterIndex, String.valueOf(x));
        }
    }

    public ResultSet getResultSet() throws SQLException {
        return preparedStatement.getResultSet();
    }

    public void setAsciiStream(int parameterIndex, InputStream x, int length)
            throws SQLException {
        preparedStatement.setAsciiStream(parameterIndex, x, length);
        if (isTrace()) {
            putParameter(parameterIndex, "[AsciiStream]");
        }
    }

    public int getUpdateCount() throws SQLException {
        return preparedStatement.getUpdateCount();
    }

    /**
     * @deprecated
     */
    public void setUnicodeStream(int parameterIndex, InputStream x,
                                 int length) throws SQLException {
        preparedStatement.setUnicodeStream(parameterIndex, x, length);
        if (isTrace()) {
            putParameter(parameterIndex, "[UnicodeStream]");
        }
    }

    public boolean getMoreResults() throws SQLException {
        return preparedStatement.getMoreResults();
    }

    public void setBinaryStream(int parameterIndex, InputStream x,
                                int length) throws SQLException {
        preparedStatement.setBinaryStream(parameterIndex, x, length);
        if (isTrace()) {
            putParameter(parameterIndex, "[BinaryStream]");
        }
    }

    public void setFetchDirection(int direction) throws SQLException {
        preparedStatement.setFetchDirection(direction);
    }

    public void clearParameters() throws SQLException {
        preparedStatement.clearParameters();
    }

    public int getFetchDirection() throws SQLException {
        return preparedStatement.getFetchDirection();
    }

    public void setObject(int parameterIndex, Object x, int targetSqlType, int scale)
            throws SQLException {
        preparedStatement.setObject(parameterIndex, x, targetSqlType, scale);
        if (isTrace()) {
            putParameter(parameterIndex, x);
        }
    }

    public void setFetchSize(int rows) throws SQLException {
        preparedStatement.setFetchSize(rows);
    }

    public void setObject(int parameterIndex, Object x, int targetSqlType)
            throws SQLException {
        preparedStatement.setObject(parameterIndex, x, targetSqlType);
        if (isTrace()) {
            putParameter(parameterIndex, x);
        }
    }

    public int getFetchSize() throws SQLException {
        return preparedStatement.getFetchSize();
    }

    public void setObject(int parameterIndex, Object x) throws SQLException {
        preparedStatement.setObject(parameterIndex, x);
        if (isTrace()) {
            putParameter(parameterIndex, x);
        }
    }

    public int getResultSetConcurrency() throws SQLException {
        return preparedStatement.getResultSetConcurrency();
    }

    public boolean execute() throws SQLException {
        try {
            return preparedStatement.execute();
        } catch (SQLException e) {
            testException(e);
            throw e;
        }
    }

    public int getResultSetType() throws SQLException {
        return preparedStatement.getResultSetType();
    }

    public void addBatch() throws SQLException {
        preparedStatement.addBatch();
    }

    public void addBatch(String sql) throws SQLException {
        preparedStatement.addBatch(sql);
    }

    public void setCharacterStream(int parameterIndex,
                                   Reader reader,
                                   int length) throws SQLException {
        preparedStatement.setCharacterStream(parameterIndex, reader, length);
        if (isTrace()) {
            putParameter(parameterIndex, "[CharacterStream]");
        }
    }

    public void clearBatch() throws SQLException {
        preparedStatement.clearBatch();
    }

    public void setRef(int i, Ref x) throws SQLException {
        preparedStatement.setRef(i, x);
        if (isTrace()) {
            putParameter(i, String.valueOf(x));
        }
    }

    public int[] executeBatch() throws SQLException {
        try {
            return preparedStatement.executeBatch();
        } catch (SQLException e) {
            testException(e);
            throw e;
        }
    }

    public void setBlob(int i, Blob x) throws SQLException {
        preparedStatement.setBlob(i, x);
        if (isTrace()) {
            putParameter(i, "[Blob]");
        }
    }

    public Connection getConnection() throws SQLException {
        return preparedStatement.getConnection();
    }

    public void setClob(int i, Clob x) throws SQLException {
        preparedStatement.setClob(i, x);
        if (isTrace()) {
            putParameter(i, "[Clob]");
        }
    }

    public void setArray(int i, Array x) throws SQLException {
        preparedStatement.setArray(i, x);
        if (isTrace()) {
            putParameter(i, x.toString());
        }
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        return preparedStatement.getMetaData();
    }

    public void setDate(int parameterIndex, Date x, Calendar cal)
            throws SQLException {
        preparedStatement.setDate(parameterIndex, x, cal);
        if (isTrace()) {
            putParameter(parameterIndex, x);
        }
    }

    public void setTime(int parameterIndex, Time x, Calendar cal)
            throws SQLException {
        preparedStatement.setTime(parameterIndex, x, cal);
        if (isTrace()) {
            putParameter(parameterIndex, String.valueOf(x));
        }
    }

    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
            throws SQLException {
        preparedStatement.setTimestamp(parameterIndex, x, cal);
        if (isTrace()) {
            putParameter(parameterIndex, String.valueOf(x));
        }
    }

    public void setNull(int paramIndex, int sqlType, String typeName)
            throws SQLException {
        preparedStatement.setNull(paramIndex, sqlType, typeName);
        if (isTrace()) {
            putParameter(paramIndex, "*");
        }
    }
}

/*
 Revision history:
 $Log: ProxyPreparedStatement.java,v $
 Revision 1.3  2003/01/28 11:55:04  billhorsman
 new JDK 1.2 patches (functioning but not complete)

 Revision 1.2  2002/09/18 13:47:14  billhorsman
 fixes for new logging

 Revision 1.1.1.1  2002/09/13 08:14:07  billhorsman
 new

 Revision 1.5  2002/07/02 11:19:08  billhorsman
 layout code and imports

 Revision 1.4  2002/06/28 11:19:47  billhorsman
 improved doc

*/
