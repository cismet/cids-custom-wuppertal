/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objecteditors.utils;

import Sirius.navigator.connection.SessionManager;

import Sirius.server.newuser.User;

import lombok.Getter;

import java.io.StringReader;

import java.util.Properties;

import de.cismet.cids.custom.utils.WundaBlauServerResources;

import de.cismet.cids.server.actions.GetServerResourceServerAction;

import de.cismet.connectioncontext.AbstractConnectionContext.Category;

import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
@Getter
public class KlimarouteConfProperties {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            KlimarouteConfProperties.class);

    //~ Instance fields --------------------------------------------------------

    private final Double bufferMeter;
    private final String mapUrl;
    private final String keyPattern;
    private final Integer keyLength;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new KlimarouteConfProperties object.
     *
     * @param  properties  DOCUMENT ME!
     */
    private KlimarouteConfProperties(final Properties properties) {
        bufferMeter = Double.valueOf(readProperty(properties, "BUFFER_METER", null));
        mapUrl = String.valueOf(readProperty(properties, "URL_LAGE", null));
        keyPattern = String.valueOf(readProperty(properties, "KEY_PATTERN", null));
        keyLength = Integer.valueOf(readProperty(properties, "KEY_LENGTH", null));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   properties    DOCUMENT ME!
     * @param   property      DOCUMENT ME!
     * @param   defaultValue  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String readProperty(final Properties properties, final String property, final String defaultValue) {
        String value = defaultValue;
        try {
            value = properties.getProperty(property, defaultValue);
        } catch (final Exception ex) {
            final String message = "could not read " + property + " from "
                        + WundaBlauServerResources.KLIMAROUTE_CONF_PROPERTIES.getValue()
                        + ". setting to default value: " + defaultValue;
            LOG.warn(message, ex);
        }
        return value;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static KlimarouteConfProperties getInstance() {
        return KlimarouteConfProperties.LazyInitialiser.INSTANCE;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final KlimarouteConfProperties INSTANCE;

        static {
            KlimarouteConfProperties instance = null;

            try {
                final User user = SessionManager.getSession().getUser();
                final Object ret = SessionManager.getSession()
                            .getConnection()
                            .executeTask(
                                user,
                                GetServerResourceServerAction.TASK_NAME,
                                "WUNDA_BLAU",
                                WundaBlauServerResources.KLIMAROUTE_CONF_PROPERTIES.getValue(),
                                ConnectionContext.create(
                                    Category.STATIC,
                                    KlimarouteConfProperties.class.getSimpleName()));
                if (ret instanceof Exception) {
                    throw (Exception)ret;
                }
                final Properties properties = new Properties();
                properties.load(new StringReader((String)ret));
                instance = new KlimarouteConfProperties(properties);
            } catch (final Exception ex) {
                LOG.error(ex, ex);
            }

            INSTANCE = instance;
        }

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LazyInitialiser object.
         */
        private LazyInitialiser() {
        }
    }
}
