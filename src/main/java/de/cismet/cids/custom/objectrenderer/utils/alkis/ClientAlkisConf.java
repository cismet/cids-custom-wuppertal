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
package de.cismet.cids.custom.objectrenderer.utils.alkis;

import Sirius.navigator.connection.SessionManager;

import java.io.StringReader;

import java.util.Properties;

import de.cismet.cids.custom.utils.WundaBlauServerResources;
import de.cismet.cids.custom.utils.alkis.AlkisConf;

import de.cismet.cids.server.actions.GetServerResourceServerAction;

import de.cismet.connectioncontext.AbstractConnectionContext;
import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class ClientAlkisConf extends AlkisConf {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ClientAlkisConf.class);
    private static final Properties PROPERTIES;

    static {
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
                            WundaBlauServerResources.ALKIS_CONF.getValue(),
                            connectionContext);
            if (ret instanceof Exception) {
                throw (Exception)ret;
            }
            final String conf = (String)ret;

            properties.load(new StringReader(conf));
        } catch (final Exception ex) {
            LOG.fatal("AlkisCommons Error!", ex);
            throw new RuntimeException(ex);
        }
        PROPERTIES = properties;
    }

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ServerAlkisConf object.
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private ClientAlkisConf() throws Exception {
        super(PROPERTIES);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ClientAlkisConf getInstance() {
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

        private static final ClientAlkisConf INSTANCE;

        static {
            try {
                INSTANCE = new ClientAlkisConf();
            } catch (final Exception ex) {
                throw new RuntimeException("Exception while initializing ServerAlkisConf", ex);
            }
        }

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LazyInitialiser object.
         */
        private LazyInitialiser() {
        }
    }
}
