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
public class AlboProperties {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            AlboProperties.class);
    private static final WundaBlauServerResources SERVER_RESOURCE = WundaBlauServerResources.ALBO_PROPERTIES;

    //~ Instance fields --------------------------------------------------------

    private final String flaecheMapUrl;
    private final Integer flaecheMapWidth;
    private final Integer flaecheMapHeight;
    private final Integer flaecheMapDpi;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AlboProperties object.
     *
     * @param  properties  DOCUMENT ME!
     */
    private AlboProperties(final Properties properties) {
        this.flaecheMapUrl = readProperty(properties, "flaecheMapUrl", null);
        {
            Integer flaecheMapWidth;
            try {
                flaecheMapWidth = Integer.parseInt(readProperty(properties, "flaecheMapWidth", null));
            } catch (final Exception ex) {
                flaecheMapWidth = null;
                LOG.warn("could not set flaecheMapWidth=" + flaecheMapWidth, ex);
            }
            this.flaecheMapWidth = flaecheMapWidth;
        }
        {
            Integer flaecheMapHeight;
            try {
                flaecheMapHeight = Integer.parseInt(readProperty(properties, "flaecheMapHeight", null));
            } catch (final Exception ex) {
                flaecheMapHeight = null;
                LOG.warn("could not set flaecheMapHeight=" + flaecheMapHeight, ex);
            }
            this.flaecheMapHeight = flaecheMapHeight;
        }
        {
            Integer flaecheMapDpi;
            try {
                flaecheMapDpi = Integer.parseInt(readProperty(properties, "flaecheMapDpi", null));
            } catch (final Exception ex) {
                flaecheMapDpi = null;
                LOG.warn("could not set flaecheMapDpi=" + flaecheMapDpi, ex);
            }
            this.flaecheMapDpi = flaecheMapDpi;
        }
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
                        + SERVER_RESOURCE.getValue()
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
    public static AlboProperties getInstance() {
        return AlboProperties.LazyInitialiser.INSTANCE;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final AlboProperties INSTANCE;

        static {
            AlboProperties instance = null;

            try {
                final User user = SessionManager.getSession().getUser();
                final Object ret = SessionManager.getSession()
                            .getConnection()
                            .executeTask(
                                user,
                                GetServerResourceServerAction.TASK_NAME,
                                "WUNDA_BLAU",
                                SERVER_RESOURCE.getValue(),
                                ConnectionContext.create(Category.STATIC, AlboProperties.class.getSimpleName()));
                if (ret instanceof Exception) {
                    throw (Exception)ret;
                }
                final Properties properties = new Properties();
                properties.load(new StringReader((String)ret));
                instance = new AlboProperties(properties);
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
