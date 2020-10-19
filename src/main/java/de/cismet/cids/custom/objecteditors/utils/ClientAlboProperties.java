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

import lombok.Getter;

import java.io.StringReader;

import java.util.Properties;

import de.cismet.cids.custom.utils.AlboProperties;

import de.cismet.cids.server.actions.GetServerResourceServerAction;

import de.cismet.connectioncontext.AbstractConnectionContext;
import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
@Getter
public class ClientAlboProperties extends AlboProperties {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AlboProperties object.
     *
     * @param  properties  DOCUMENT ME!
     */
    private ClientAlboProperties(final Properties properties) {
        super(properties);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ClientAlboProperties getInstance() {
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

        private static final ClientAlboProperties INSTANCE = new ClientAlboProperties(loadProperties());

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
                        ClientAlboProperties.class.getSimpleName());
                final Object ret = SessionManager.getSession()
                            .getConnection()
                            .executeTask(SessionManager.getSession().getUser(),
                                GetServerResourceServerAction.TASK_NAME,
                                "WUNDA_BLAU",
                                SERVER_RESOURCE.getValue(),
                                connectionContext);
                if (ret instanceof Exception) {
                    throw (Exception)ret;
                }

                properties.load(new StringReader((String)ret));
            } catch (final Exception ex) {
                throw new RuntimeException("Exception while initializing ClientAlboProperties", ex);
            }
            return properties;
        }
    }
}
