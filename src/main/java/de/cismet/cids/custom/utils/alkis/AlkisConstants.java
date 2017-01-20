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
package de.cismet.cids.custom.utils.alkis;

import Sirius.navigator.connection.SessionManager;

import java.io.StringReader;

import java.util.Properties;

import de.cismet.cids.custom.utils.WundaBlauServerResources;

import de.cismet.cids.server.actions.GetServerResourceServerAction;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public final class AlkisConstants {

    //~ Static fields/initializers ---------------------------------------------

    public static final AlkisConf COMMONS;

    //
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AlkisConstants.class);
    public static final String NEWLINE = "<br>";
    public static final String LINK_SEPARATOR_TOKEN = "::";

    static {
        try {
            final Object ret = SessionManager.getSession()
                        .getConnection()
                        .executeTask(SessionManager.getSession().getUser(),
                            GetServerResourceServerAction.TASK_NAME,
                            "WUNDA_BLAU",
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
