/*
* Copyright 2002, Findexa AS (http://www.findexa.no)
*
* This software is the proprietary information of Findexa AS.
* Use is subject to license terms.
*/
package org.logicalcobwebs.dbscript;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * <link rel="stylesheet" href="{@docRoot}/cg.css" type="text/css">
 *
 * TODO
 *
 * @version $Revision: 1.2 $, $Date: 2002/11/02 13:57:34 $
 * @author Bill Horsman (bill@logicalcobwebs.co.uk)
 * @author $Author: billhorsman $ (current maintainer)
 * @since GSI 5.0
 */
public class ScriptFacade {

    private static final Log LOG = LogFactory.getLog(ScriptFacade.class);

    public static void runScript(String scriptLocation, ConnectionAdapterIF adapter) {

        File scriptFile = new File(scriptLocation);
        if (!scriptFile.canRead()) {
            throw new RuntimeException("Can't read from file at " + scriptFile.getAbsolutePath());
        }

        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setValidating(false);
            saxParserFactory.setNamespaceAware(true);
            SAXParser saxParser = saxParserFactory.newSAXParser();
            saxParser.getXMLReader().setFeature("http://xml.org/sax/features/namespaces", true);
            saxParser.getXMLReader().setErrorHandler(new ErrorHandler() {
                public void warning(SAXParseException exception)
                        throws SAXException {
                    LOG.warn(exception.getLineNumber() + ":" + exception.getColumnNumber(), exception);
                }

                public void error(SAXParseException exception)
                        throws SAXException {
                    LOG.error(exception.getLineNumber() + ":" + exception.getColumnNumber(), exception);
                }

                public void fatalError(SAXParseException exception)
                        throws SAXException {
                    LOG.error(exception.getLineNumber() + ":" + exception.getColumnNumber(), exception);
                }
            });

            ScriptBuilder scriptBuilder = new ScriptBuilder();
            saxParser.parse(scriptFile, scriptBuilder);
            Script script = scriptBuilder.getScript();

            ScriptRunner.runScript(script, adapter);

        } catch (FactoryConfigurationError factoryConfigurationError) {
            LOG.error(factoryConfigurationError);
        } catch (ParserConfigurationException e) {
            LOG.error("Problem running script " + scriptLocation, e);
        } catch (SAXException e) {
            LOG.error("Problem running script " + scriptLocation, e);
        } catch (IOException e) {
            LOG.error("Problem running script " + scriptLocation, e);
        } catch (SQLException e) {
            LOG.error("Problem running script " + scriptLocation, e);
        }

    }

}

/*
 Revision history:
 $Log: ScriptFacade.java,v $
 Revision 1.2  2002/11/02 13:57:34  billhorsman
 checkstyle

 Revision 1.1  2002/11/02 11:29:53  billhorsman
 new script runner for testing

*/
