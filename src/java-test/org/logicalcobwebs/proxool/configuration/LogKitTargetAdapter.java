/*
 * This software is released under the Apache Software Licence. See
 * package.html for details. The latest version is available at
 * http://proxool.sourceforge.net
 */
package org.logicalcobwebs.proxool.configuration;

import org.apache.log.Priority;
import org.apache.log.LogEvent;
import org.apache.log.output.AbstractOutputTarget;
import org.apache.avalon.framework.CascadingThrowable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An Avalon LogKit target that delegates to a Jakarta Commons <code>org.apache.commons.logging.Log</code>.
 * Can be used to make Avalons internal logging go the same output as
 * the Proxool internal logging.
 * @version $Revision: 1.1 $, $Date: 2002/12/23 02:39:43 $
 * @author Christian Nedregaard (christian_nedregaard@email.com)
 * @author $Author: chr32 $ (current maintainer)
 * @since Proxool 0.6
 */
public class LogKitTargetAdapter extends AbstractOutputTarget {
    private boolean isOpen = true;

    /**
     * @see org.apache.log.output.AbstractOutputTarget#doProcessEvent(org.apache.log.LogEvent)
     */
    protected void doProcessEvent(LogEvent event) {
        // we do a workaround for the fact that Avalon CascadingThrowables
        // does not print the stacktrace of any nested exceptions.
        // Also we 'upgrade' DEBUG LogEvents containing Throwables
        // to WARN since Avalon seems to give some important exceptions
        // DEBUG priority.
        Throwable originalThrowable = event.getThrowable();
        Throwable nestedThrowable = null;
        if (originalThrowable != null && originalThrowable instanceof CascadingThrowable) {
            nestedThrowable = ((CascadingThrowable) originalThrowable).getCause();
        }
        int priority = event.getPriority().getValue();
        if (originalThrowable != null && event.getPriority().isLower(Priority.WARN)) {
            priority = Priority.WARN.getValue();
        }
        Log logger = LogFactory.getLog(event.getCategory());
        if (Priority.DEBUG.getValue() == priority) {
            if (logger.isDebugEnabled()) {
                logger.debug(event.getMessage(), originalThrowable);
            }
            if (nestedThrowable != null) {
                logger.debug("... caused by:", nestedThrowable);
            }
        } else if (Priority.INFO.getValue() == priority) {
            if (logger.isInfoEnabled()) {
                logger.info(event.getMessage(), originalThrowable);
            }
            if (nestedThrowable != null) {
                logger.info("... caused by:", nestedThrowable);
            }
        } else if (Priority.WARN.getValue() == priority) {
            logger.warn(event.getMessage(), originalThrowable);
            if (nestedThrowable != null) {
                logger.warn("... caused by:", nestedThrowable);
            }
        } else if (Priority.ERROR.getValue() == priority) {
            logger.error(event.getMessage(), originalThrowable);
            if (nestedThrowable != null) {
                logger.error("... caused by:", nestedThrowable);
            }
        } else if (Priority.FATAL_ERROR.getValue() == priority) {
            logger.fatal(event.getMessage(), originalThrowable);
            if (nestedThrowable != null) {
                logger.fatal("... caused by:", nestedThrowable);
            }
        } else {
            logger.warn("Got log event of unknown priority: " + priority
                + ". Message: " + event.getMessage(), originalThrowable);
        }
    }

    /**
     * @see org.apache.log.output.AbstractOutputTarget#isOpen()
     */
    protected boolean isOpen() {
        return this.isOpen;
    }

    /**
     * @see org.apache.log.output.AbstractOutputTarget#open()
     */
    protected void open() {
        this.isOpen = true;
    }

    /**
     * @see org.apache.log.output.AbstractOutputTarget#close()
     */
    public void close() {
        this.isOpen = false;
    }

}

/*
 Revision history:
 $Log: LogKitTargetAdapter.java,v $
 Revision 1.1  2002/12/23 02:39:43  chr32
 Needed by the AvalonConfigurator tests.

*/