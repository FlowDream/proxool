/*
 * This software is released under the Apache Software Licence. See
 * package.html for details. The latest version is available at
 * http://proxool.sourceforge.net
 */
package org.logicalcobwebs.proxool;

import java.util.Properties;
import java.util.Set;

/**
 * A full definition of everything to do with a connection. You can get one of these
 * from {@link ProxoolFacade#getConnectionPoolDefinition ProxoolFacade}.
 *
 * <pre>
 * String alias = "myPool";
 * ConnectionPoolDefinitionIF cpd = ProxoolFacade.getConnectionPoolDefinition(alias);
 * </pre>
 *
 * If you want to update the definition you should either update the properties
 * definition next time you
 * {@link java.sql.Driver#connect ask} for a connection or call
 * {@link ProxoolFacade#updateConnectionPool Proxool} directly.
 *
 * @version $Revision: 1.13 $, $Date: 2003/02/06 15:41:17 $
 * @author billhorsman
 * @author $Author: billhorsman $ (current maintainer)
 */
public interface ConnectionPoolDefinitionIF {

    /** 4 * 60 * 60 * 1000 (4 hours) */
    public static final int DEFAULT_MAXIMUM_CONNECTION_LIFETIME = 4 * 60 * 60 * 1000; // 4 hours

    /** 300000 (5 minutes) */
    public static final int DEFAULT_MAXIMUM_ACTIVE_TIME = 300000; // 5 minutes

    /** 0 */
    public static final int DEFAULT_PROTOTYPE_COUNT = 0;

    /** 0 */
    public static final int DEFAULT_MINIMUM_CONNECTION_COUNT = 0;

    /** 15 */
    public static final int DEFAULT_MAXIMUM_CONNECTION_COUNT = 15;

    /** 30000 (30 Seconds) */
    public static final int DEFAULT_HOUSE_KEEPING_SLEEP_TIME = 30000;

    /** 10 */
    public static final int DEFAULT_MAXIMUM_NEW_CONNECTIONS = 10;

    /** 60000 */
    public static final int DEFAULT_OVERLOAD_WITHOUT_REFUSAL_THRESHOLD = 60000;

    /** 60000 */
    public static final int DEFAULT_RECENTLY_STARTED_THRESHOLD = 60000;

    public static final int DEBUG_LEVEL_QUIET = 0;

    public static final int DEBUG_LEVEL_LOUD = 1;

    public static final String USER_PROPERTY = "user";

    public static final String PASSWORD_PROPERTY = "password";

    /** This is the time the house keeping thread sleeps for between checks. (milliseconds) */
    int getHouseKeepingSleepTime();

    /** The maximum number of connections to the database */
    int getMaximumConnectionCount();

    /** The maximum amount of time that a connection exists for before it is killed (recycled). (milliseconds) */
    int getMaximumConnectionLifetime();

    /**
     * In order to prevent overloading, this is the maximum number of connections that you can have that are in the progress
     * of being made. That is, ones we have started to make but haven't finished yet.
     */
    int getMaximumNewConnections();

    /** The minimum number of connections we will keep open, regardless of whether anyone needs them or not. */
    int getMinimumConnectionCount();

    /** @deprecated use {@link #getAlias} instead.  */
    String getName();

    /** The name associated with this connection pool. This is how you identify this pool when you need to use it. */
    String getAlias();

    /** The password to use to login to the database */
    String getPassword();

    /**
     * This is the number of spare connections we will strive to have. So, if we have a prototypeCount of 5 but only 3 spare
     * connections the prototyper will make an additional 2. This is important because it can take around a seconds to
     * establish a connection, and if we are being very strict about killing connections when they get too
     * old it happens a fair bit.
     */
    int getPrototypeCount();

    /** This is the URL used to connect to the database. e.g. driver:@host:port:database. */
    String getUrl();

    /* The user used to login to the database. */

    String getUser();

    /* Interrogate the JDBC driver for its version (warning: this information is not always correct - be
    very suspicious if it reports the version as 1.0) */
    String getJdbcDriverVersion();

    /**
     * Get all of the properties that are defined on the delegated driver.
     * @return
     */
    Properties getProperties();

    String getDriver();

    /** As long as we have one connection that was started within this threshold
     then we consider the pool to be up. (That is, not down). This allows us to
     differentiate between having all the connections frozen and just being really
     busy. */
    int getRecentlyStartedThreshold();

    /** This is the time in milliseconds after the last time that we refused a
     connection that we still consider ourselves to be overloaded. We have to do this
     because, even when overloaded, it's not impossible for the available connection
     count to be high and it's possible to be serving a lot of connections.
     Recognising an overload is easy (we refuse a connection) - it's recognising
     when we stop being overloaded that is hard. Hence this fudge :) */
    int getOverloadWithoutRefusalLifetime();

    /** If the housekeeper comes across a thread that has been active for longer
     than this then it will kill it. So make sure you set this to a number bigger
     than your slowest expected response! */
    int getMaximumActiveTime();

    /**
     * @deprecated use {@link #isVerbose} instead
     */
    int getDebugLevel();

    /**
     * Get the list of fatal SQL exception (Strings) fragments that will
     * trigger the death of a Connection.
     * All SQLExceptions are caught and tested for containing this
     * text fragment. If it matches than this connection is considered useless
     * and it is discarded. Regardless of what happens the exception
     * is always thrown back to the user.
     * @return the list of exception fragments (String)
     */
    Set getFatalSqlExceptions();

    /**
     *
     * @return
     */
    String getHouseKeepingTestSql();

   /**
    * The URL that was used to define this pool. For example:
    * proxool:org.hsqldb.jdbcDriver:jdbc:hsqldb:test
    * @return the complete url
    */
    String getCompleteUrl();

    /**
     * If this is true then we start logging a lot of stuff everytime we serve a
     * connection and everytime the house keeper and prototyper run. Be
     * prepared for a lot of debug!
     *
     * @return true if in verbose mode
     */
    boolean isVerbose();

    /**
     * if this is true then we will log each execution. The SQL used and the
     * execution time.
     *
     * @return true if we should log each execution
     */
    boolean isTrace();

    /**
     * The sample length (in seconds) when taking statistical information,
     * or null to disable statistics. Default is null. You can comma delimit
     * a series of periods. The suffix for the period is either "s" (seconds),
     * "m" (minutes), "h" (hours) or "d" (days). For example: "15s,1h" would
     * give two separate sample rates: every 15 seconds and every hour.
     * @return statistics definition
     */
    String getStatistics();

    /**
     * Whether statistics are logged as they are produced.
     * Range: DEBUG, INFO, WARN, ERROR, FATAL.
     * Default is null (no logging)
     * @return statisticsLogLevel
     */
    String getStatisticsLogLevel();

}

/*
 Revision history:
 $Log: ConnectionPoolDefinitionIF.java,v $
 Revision 1.13  2003/02/06 15:41:17  billhorsman
 add statistics-log-level

 Revision 1.12  2003/01/31 00:17:04  billhorsman
 statistics is now a string to allow multiple,
 comma-delimited values

 Revision 1.11  2003/01/30 17:20:37  billhorsman
 new statistics property

 Revision 1.10  2003/01/17 00:38:12  billhorsman
 wide ranging changes to clarify use of alias and url -
 this has led to some signature changes (new exceptions
 thrown) on the ProxoolFacade API.

 Revision 1.9  2002/12/17 14:20:44  billhorsman
 doc

 Revision 1.8  2002/12/15 19:21:42  chr32
 Changed @linkplain to @link (to preserve JavaDoc for 1.2/1.3 users).

 Revision 1.7  2002/11/09 15:50:15  billhorsman
 new trace property and better doc

 Revision 1.6  2002/10/27 13:29:38  billhorsman
 deprecated debug-level in favour of verbose

 Revision 1.5  2002/10/27 12:09:00  billhorsman
 default minimum connection count changed from 5 to 0

 Revision 1.4  2002/10/25 16:00:25  billhorsman
 added better class javadoc

 Revision 1.3  2002/10/24 17:29:06  billhorsman
 prototype count now defaults to zero

 Revision 1.2  2002/10/17 19:46:02  billhorsman
 removed redundant reference to logFilename (we now use Jakarta's Commons Logging component

 Revision 1.1.1.1  2002/09/13 08:13:02  billhorsman
 new

 Revision 1.6  2002/07/02 08:44:56  billhorsman
 Removed all mutators

 Revision 1.5  2002/06/28 11:19:47  billhorsman
 improved doc

*/
