/*
 * This software is released under the Apache Software Licence. See
 * package.html for details. The latest version is available at
 * http://proxool.sourceforge.net
 */
package org.logicalcobwebs.proxool;



/**
 * Provides information about an individual connection. You can get a collection
 * of these from {@link ProxoolFacade#getConnectionInfos ProxoolFacade}. You
 * get back information about all the connections in a particular pool.
 *
 * <pre>
 * String alias = "myPool";
 * Iterator i = ProxoolFacade.getConnectionInfos(alias).iterator();
 * while (i.hasNext()) {
 *  ConnectionInfoIF c = (ConnectionInfoIF)i.next();
 *   ...
 * }
 * </pre>
 *
 * @version $Revision: 1.6 $, $Date: 2003/01/27 18:26:35 $
 * @author billhorsman
 * @author $Author: billhorsman $ (current maintainer)
 */
public interface ConnectionInfoIF {

    /**
     * Default - treat as normal
     * @see #getMark
     */
    static final int MARK_FOR_USE = 0;

    /**
     * The next time this connection is made available we should expire it.
     * @see #getMark
     */
    static final int MARK_FOR_EXPIRY = 1;

    /**
     * This is the start and end state of every connection
     * @see #getStatus
     */
    static final int STATUS_NULL = 0;

    /**
     * The connection is available for use
     * @see #getStatus
     */
    static final int STATUS_AVAILABLE = 1;

    /**
     * The connection is in use
     * @see #getStatus
     */
    static final int STATUS_ACTIVE = 2;

    /**
     * The connection is in use by the house keeping thread
     * @see #getStatus
     */
    static final int STATUS_OFFLINE = 3;

    /**
     * The time that this connection was created.
     * The number of milliseconds
     * since midnight, January 1, 1970 UTC.
     */
    long getBirthTime();

    /**
     * The age in millseconds since this connection was built
     */
    long getAge();

    /**
     * A unique ID for this connection
     */
    long getId();

    /**
     * Sometimes we want do something to a connection but can't because it is still
     * active and we don't want to disrupt its use. So we mark it instead and when it
     * stops being active we can perform the necessary operation.
     *
     * The only thing we do at the moment is {@link #MARK_FOR_EXPIRY expire} the
     * connection (if it is too old for instance). And this will happen if the
     * housekeeper decides it should but the connection is still active.
     */
    int getMark();

    /**
     * The status of the connection. Can be either:
     * {@link #STATUS_NULL null},
     * {@link #STATUS_AVAILABLE available},
     * {@link #STATUS_ACTIVE active} or
     * {@link #STATUS_OFFLINE offline}.
     */
    int getStatus();

    /**
     * When this connection was last given out.  The number of milliseconds
     * since midnight, January 1, 1970 UTC.
     */
    long getTimeLastStartActive();

    /**
     * When this connection was last given back (or zero if it is still active).
     * The number of milliseconds
     * since midnight, January 1, 1970 UTC.
     */
    long getTimeLastStopActive();

    /**
     * The name of the thread that asked for this connection.
     */
    String getRequester();

}

/*
 Revision history:
 $Log: ConnectionInfoIF.java,v $
 Revision 1.6  2003/01/27 18:26:35  billhorsman
 refactoring of ProxyConnection and ProxyStatement to
 make it easier to write JDK 1.2 patch

 Revision 1.5  2003/01/15 00:12:13  billhorsman
 doc

 Revision 1.4  2002/12/15 19:21:42  chr32
 Changed @linkplain to @link (to preserve JavaDoc for 1.2/1.3 users).

 Revision 1.3  2002/10/25 16:00:19  billhorsman
 added better class javadoc

 Revision 1.2  2002/09/18 13:48:56  billhorsman
 checkstyle and doc

 Revision 1.1.1.1  2002/09/13 08:12:32  billhorsman
 new

 Revision 1.3  2002/06/28 11:19:47  billhorsman
 improved doc

*/
