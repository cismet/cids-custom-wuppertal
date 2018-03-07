/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.utils.alkisconstants;

import Sirius.navigator.connection.SessionManager;

import java.io.StringReader;

import java.util.Properties;

import de.cismet.cids.custom.utils.WundaBlauServerResources;
import de.cismet.cids.custom.utils.alkis.AlkisConf;

import de.cismet.cids.server.actions.GetServerResourceServerAction;

import de.cismet.connectioncontext.AbstractConnectionContext.Category;

import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public final class AlkisConstants {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AlkisConstants.class);

    public static final AlkisConf COMMONS;
    public static final String NEWLINE = "<br>";
    public static final String LINK_SEPARATOR_TOKEN = "::";

    static {
        try {
            final ConnectionContext connectionContext = ConnectionContext.create(
                    Category.STATIC,
                    AlkisConstants.class.getSimpleName());
            final Object ret = SessionManager.getSession()
                        .getConnection()
                        .executeTask(SessionManager.getSession().getUser(),
                            GetServerResourceServerAction.TASK_NAME,
                            "WUNDA_BLAU",
                            connectionContext,
                            WundaBlauServerResources.ALKIS_CONF.getValue());
            if (ret instanceof Exception) {
                throw (Exception)ret;
            }
            final String conf = (String)ret;

            final Properties serviceProperties = new Properties();
            serviceProperties.load(new StringReader(conf));

            COMMONS = new AlkisConf(serviceProperties);
        } catch (final Exception ex) {
            LOG.fatal("AlkisCommons Error!", ex);
            throw new RuntimeException(ex);
        }
    }

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AlkisConstants object.
     */
    private AlkisConstants() {
    }
}
