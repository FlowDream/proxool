package org.logicalcobwebs.proxool;

import org.logicalcobwebs.logging.Log;
import org.logicalcobwebs.logging.LogFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Properties;

/*
 * Created by IntelliJ IDEA.
 * User: bill
 * Date: 19-Oct-2002
 * Time: 16:58:04
 * To change this template use Options | File Templates.
 */

public class TestHelper {

    private static final Log LOG = LogFactory.getLog(TestHelper.class);

    private static final String USER = "sa";

    private static final String PASSWORD = "";

    public static final String PROXOOL_DRIVER = "org.logicalcobwebs.proxool.ProxoolDriver";

    public static final String HYPERSONIC_DRIVER = "org.hsqldb.jdbcDriver";

    public static final String HYPERSONIC_URL = "jdbc:hsqldb:db/test";

    private static final String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";

    private static final String ORACLE_URL = "jdbc:oracle:thin:@172.27.52.112:1527:hpgs1";

    public static final String SQL_INSERT_INTO_TEST = "INSERT INTO TEST VALUES(1);";

    public static final String SQL_CHECK_TEST = "SELECT COUNT(*) FROM TEST;";

    public static Properties buildProperties() {
        Properties info = new Properties();
        info.setProperty("user", USER);
        info.setProperty("password", PASSWORD);
        info.setProperty("proxool.verbose", "true");
        return info;
    }

    /**
     * Builds a complete set of proxool properties, with all values set to
     * something different than the default vaule.
     * @return the properties that was buildt.
     */
    public static Properties buildCompleteAlternativeProperties() {
        Properties properties = new Properties();
        properties.setProperty("user", "sa");
        properties.setProperty("password", "");
        properties.setProperty(ProxoolConstants.HOUSE_KEEPING_SLEEP_TIME_PROPERTY, "40000");
        properties.setProperty(ProxoolConstants.HOUSE_KEEPING_TEST_SQL_PROPERTY, "select CURRENT_DATE");
        properties.setProperty(ProxoolConstants.MAXIMUM_CONNECTION_COUNT_PROPERTY, "10");
        properties.setProperty(ProxoolConstants.MINIMUM_CONNECTION_COUNT_PROPERTY, "3");
        properties.setProperty(ProxoolConstants.MAXIMUM_CONNECTION_LIFETIME_PROPERTY, "18000000");
        properties.setProperty(ProxoolConstants.MAXIMUM_NEW_CONNECTIONS_PROPERTY, "5");
        properties.setProperty(ProxoolConstants.RECENTLY_STARTED_THRESHOLD_PROPERTY, "40000");
        properties.setProperty(ProxoolConstants.OVERLOAD_WITHOUT_REFUSAL_LIFETIME_PROPERTY, "50000");
        properties.setProperty(ProxoolConstants.MAXIMUM_ACTIVE_TIME_PROPERTY, "60000");
        properties.setProperty(ProxoolConstants.VERBOSE_PROPERTY, "true");
        properties.setProperty(ProxoolConstants.TRACE_PROPERTY, "true");
        properties.setProperty(ProxoolConstants.FATAL_SQL_EXCEPTION_PROPERTY, "Fatal error");
        properties.setProperty(ProxoolConstants.PROTOTYPE_COUNT_PROPERTY, "2");
        return properties;
    }

    /**
     * Test that the given ConnectionPoolDefinitionIF contains all the properties as defined in
     * {@link #buildCompleteAlternativeProperties}.
     * @param connectionPoolDefinition the ConnectionPoolDefinitionIF to be tested.
     * @throws ProxoolException if any properties are missing or have the wrong value.
     */
    public static void equalsCompleteAlternativeProperties(ConnectionPoolDefinitionIF connectionPoolDefinition)
        throws ProxoolException {
        checkProperty("user", "sa", connectionPoolDefinition.getProperties().getProperty("user"));
        checkProperty("password", "", connectionPoolDefinition.getProperties().getProperty("password"));
        checkProperty(ProxoolConstants.HOUSE_KEEPING_SLEEP_TIME, 40000,
            connectionPoolDefinition.getHouseKeepingSleepTime());
        checkProperty(ProxoolConstants.HOUSE_KEEPING_TEST_SQL, "select CURRENT_DATE",
            connectionPoolDefinition.getHouseKeepingTestSql());
        checkProperty(ProxoolConstants.MAXIMUM_CONNECTION_COUNT, 10,
            connectionPoolDefinition.getMaximumConnectionCount());
        checkProperty(ProxoolConstants.MINIMUM_CONNECTION_COUNT, 3,
            connectionPoolDefinition.getMinimumConnectionCount());
        checkProperty(ProxoolConstants.MAXIMUM_CONNECTION_LIFETIME, 18000000,
            connectionPoolDefinition.getMaximumConnectionLifetime());
        checkProperty(ProxoolConstants.MAXIMUM_NEW_CONNECTIONS, 5,
            connectionPoolDefinition.getMaximumNewConnections());
        checkProperty(ProxoolConstants.RECENTLY_STARTED_THRESHOLD, 40000,
            connectionPoolDefinition.getRecentlyStartedThreshold());
        checkProperty(ProxoolConstants.OVERLOAD_WITHOUT_REFUSAL_LIFETIME, 50000,
            connectionPoolDefinition.getOverloadWithoutRefusalLifetime());
        checkProperty(ProxoolConstants.MAXIMUM_ACTIVE_TIME, 60000,
            connectionPoolDefinition.getMaximumActiveTime());
        checkProperty(ProxoolConstants.VERBOSE, "true",
            new Boolean(connectionPoolDefinition.isVerbose()).toString());
        checkProperty(ProxoolConstants.TRACE, "true",
            new Boolean(connectionPoolDefinition.isTrace() == true).toString());
        checkProperty(ProxoolConstants.FATAL_SQL_EXCEPTION, "Fatal error",
            connectionPoolDefinition.getFatalSqlExceptions().iterator().next().toString());
        checkProperty(ProxoolConstants.PROTOTYPE_COUNT, 2,
            connectionPoolDefinition.getPrototypeCount());
    }

    private static void checkProperty(String name, String correctValue, String candidateValue) throws ProxoolException {
        if (candidateValue == null) {
            throw new ProxoolException(name + " was null.");
        } else if (!candidateValue.equals(correctValue)) {
            throw new ProxoolException("Expected value for " + name + " was " + correctValue + " but the value was "
                + candidateValue + ".");
        }
    }

    private static void checkProperty(String name, int correctValue, int candidateValue) throws ProxoolException {
            checkProperty(name, String.valueOf(correctValue), String.valueOf(candidateValue));
    }

    public static void registerPool(String alias) throws SQLException, ProxoolException {
        registerPool(alias, TestHelper.buildProperties());
    }

    public static void registerPool(String alias, Properties info) throws SQLException, ProxoolException {
        ProxoolFacade.registerConnectionPool(getFullUrl(alias), info);
    }

    public static void registerOraclePool(String alias, Properties info) throws SQLException, ProxoolException {
        ProxoolFacade.registerConnectionPool(getFullOracleUrl(alias), info);
    }

    public static Connection getProxoolConnection(String url) throws ClassNotFoundException, SQLException {
        return getProxoolConnection(url, buildProperties());
    }

    public static Connection getProxoolConnection(String url, Properties info) throws ClassNotFoundException, SQLException {
        Connection connection = null;
        Class.forName(PROXOOL_DRIVER);
        connection = DriverManager.getConnection(url, info);
        return connection;
    }

    public static String getSimpleUrl(String alias) {
        String url = ProxoolConstants.PROXOOL
                + ProxoolConstants.ALIAS_DELIMITER
                + alias;
        return url;
    }

    public static String buildProxoolUrl(String alias, String driver, String delegateUrl) {
        String url = ProxoolConstants.PROXOOL
                + ProxoolConstants.ALIAS_DELIMITER
                + alias
                + ProxoolConstants.URL_DELIMITER
                + driver
                + ProxoolConstants.URL_DELIMITER
                + delegateUrl;
        return url;
    }

    public static String getFullUrl(String alias) {
        String url = ProxoolConstants.PROXOOL
                + ProxoolConstants.ALIAS_DELIMITER
                + alias
                + ProxoolConstants.URL_DELIMITER
                + HYPERSONIC_DRIVER
                + ProxoolConstants.URL_DELIMITER
                + HYPERSONIC_URL;
        return url;
    }

    public static String getFullOracleUrl(String alias) {
        try {
            Class.forName(ORACLE_DRIVER);
        } catch (ClassNotFoundException e) {
            LOG.error(ORACLE_DRIVER, e);
        }
        String url = ProxoolConstants.PROXOOL
                + ProxoolConstants.ALIAS_DELIMITER
                + alias
                + ProxoolConstants.URL_DELIMITER
                + ORACLE_DRIVER
                + ProxoolConstants.URL_DELIMITER
                + ORACLE_URL;
        return url;
    }

    public static Connection getDirectConnection() throws ClassNotFoundException, SQLException {
        Connection connection = null;
        Class.forName(HYPERSONIC_DRIVER);
        connection = DriverManager.getConnection(HYPERSONIC_URL, buildProperties());
        return connection;
    }

    public static Connection getDirectOracleConnection() throws ClassNotFoundException, SQLException {
        Connection connection = null;
        Class.forName(ORACLE_DRIVER);
        Properties info = buildProperties();
        info.setProperty("user", "hsi_servlet");
        info.setProperty("password", "hsi_servlet");
        connection = DriverManager.getConnection(ORACLE_URL, info);
        return connection;
    }

    public static int getCount(Connection connection, String table) throws SQLException {
        Statement statement = null;
        ResultSet resultSet = null;
        int count = -1;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT COUNT(*) FROM " + table);
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            } else {
                LOG.warn("No rows returned from " + table);
            }
        } finally {
            if (statement != null) {
                try {
                    if (resultSet != null) {
                        resultSet.close();
                    }
                    if (statement != null) {
                        statement.close();
                    }
                } catch (SQLException e) {
                    LOG.error("Couldn't close statement", e);
                }
            }
        }
        return count;
    }

    public static void execute(Connection connection, String sql) throws SQLException {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute(sql);
        } finally {
            if (statement != null) {
                try {
                    if (statement != null) {
                        statement.close();
                    }
                } catch (SQLException e) {
                    LOG.error("Couldn't close statement", e);
                }
            }
        }
    }

    public static void createTable(String table) throws Exception {
        try {
            execute(getDirectConnection(), "CREATE TABLE " + table + " (A INT)");
        } catch (Exception e) {
            LOG.error("Error creating table " + table, e);
            throw e;
        }
    }

    public static void dropTable(String table) throws SQLException, ClassNotFoundException {
        execute(getDirectConnection(), "DROP TABLE " + table);
    }

    public static void insertRow(Connection connection, String table) throws SQLException {
        execute(connection, "INSERT INTO " + table + " VALUES(1)");
    }

    public static void testConnection(Connection connection) throws SQLException {
        execute(connection, "SELECT SYSDATE FROM DUAL");
    }

}
