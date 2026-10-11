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

import java.io.StringReader;

import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import de.cismet.cids.custom.objectrenderer.utils.alkis.ClientAlkisConf;
import de.cismet.cids.custom.utils.StaticProperties;
import de.cismet.cids.custom.utils.WundaBlauServerResources;

import de.cismet.cids.server.actions.GetServerResourceServerAction;

import de.cismet.cids.utils.serverresources.ServerResourcesLoader;

import de.cismet.connectioncontext.AbstractConnectionContext;
import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public final class ClientStaticProperties extends StaticProperties {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new StaticProperties object.
     *
     * @param  properties  DOCUMENT ME!
     */
    protected ClientStaticProperties(final Properties properties) {
        super(properties);

        final Timer t = new Timer();
        t.schedule(new TimerTask() {

                @Override
                public void run() {
                    refresh();
                }
            }, 60000, 60000);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ClientStaticProperties getInstance() {
        return LazyInitialiser.INSTANCE;
    }

    /**
     * DOCUMENT ME!
     */
    protected synchronized void refresh() {
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
                            WundaBlauServerResources.URLCONFIG_PROPERTIES.getValue(),
                            connectionContext);
            if (ret instanceof Exception) {
                return;
            }

            properties.load(new StringReader((String)ret));

            if (properties != null) {
                refresh(properties);
            }
        } catch (Exception e) {
            // nothing to do
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final ClientStaticProperties INSTANCE = new ClientStaticProperties(loadProperties());

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
                                WundaBlauServerResources.URLCONFIG_PROPERTIES.getValue(),
                                connectionContext);
                if (ret instanceof Exception) {
                    throw (Exception)ret;
                }

                properties.load(new StringReader((String)ret));
            } catch (final Exception ex) {
                throw new RuntimeException("Exception while initializing ClientStaticProperties", ex);
            }
            return properties;
        }
    }
}
