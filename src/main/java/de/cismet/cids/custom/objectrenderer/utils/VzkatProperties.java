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
package de.cismet.cids.custom.objectrenderer.utils;

import Sirius.navigator.connection.SessionManager;

import lombok.Getter;

import java.io.StringReader;

import java.util.Properties;

import de.cismet.cids.custom.objectrenderer.utils.alkis.ClientAlkisConf;
import de.cismet.cids.custom.utils.WundaBlauServerResources;

import de.cismet.cids.server.actions.GetServerResourceServerAction;

import de.cismet.connectioncontext.AbstractConnectionContext;
import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */

@Getter
public final class VzkatProperties extends Properties {

    //~ Instance fields --------------------------------------------------------

    private final String webdavUploadUrl;
    private final String webdavUploadUsername;
    private final String webdavUploadPassword;
    private final String ovOverviewUrl;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VzkatProperties object.
     *
     * @param  properties  DOCUMENT ME!
     */
    private VzkatProperties(final Properties properties) {
        super(properties);

        webdavUploadUrl = properties.getProperty("WEBDAV_UPLOAD_URL");
        webdavUploadUsername = properties.getProperty("WEBDAV_UPLOAD_USERNAME");
        webdavUploadPassword = properties.getProperty("WEBDAV_UPLOAD_PASSWORD");
        ovOverviewUrl = properties.getProperty("OV_OVERVIEW_URL");
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static VzkatProperties getInstance() {
        return LazyInitialiser.INSTANCE;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final VzkatProperties INSTANCE = new VzkatProperties(loadProperties());

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LazyInitialiser object.
         */
        private LazyInitialiser() {
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         *
         * @throws  RuntimeException  DOCUMENT ME!
         */
        private static Properties loadProperties() {
            final Properties properties = new Properties();
            try {
                final ConnectionContext connectionContext = ConnectionContext.create(
                        AbstractConnectionContext.Category.STATIC,
                        ClientAlkisConf.class.getSimpleName());
                final Object ret = SessionManager.getSession()
                            .getConnection()
                            .executeTask(SessionManager.getSession().getUser(),
                                GetServerResourceServerAction.TASK_NAME,
                                "WUNDA_BLAU",
                                WundaBlauServerResources.VZKAT_PROPERTIES.getValue(),
                                connectionContext);
                if (ret instanceof Exception) {
                    throw (Exception)ret;
                }

                properties.load(new StringReader((String)ret));
            } catch (final Exception ex) {
                throw new RuntimeException("Exception while initializing VzkatProperties", ex);
            }
            return properties;
        }
    }
}
