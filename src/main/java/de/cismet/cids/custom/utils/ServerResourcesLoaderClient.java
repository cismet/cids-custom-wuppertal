/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.utils;

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

import Sirius.navigator.connection.SessionManager;

import com.fasterxml.jackson.core.JsonFactory;

import net.sf.jasperreports.engine.JasperReport;

import java.io.StringReader;

import java.util.Properties;

import de.cismet.cids.server.actions.GetServerResourceServerAction;

import de.cismet.cids.utils.serverresources.AbstractServerResourcesLoader;
import de.cismet.cids.utils.serverresources.BinaryServerResource;
import de.cismet.cids.utils.serverresources.JasperReportServerResource;
import de.cismet.cids.utils.serverresources.JsonServerResource;
import de.cismet.cids.utils.serverresources.PropertiesServerResource;
import de.cismet.cids.utils.serverresources.ServerResource;
import de.cismet.cids.utils.serverresources.TextServerResource;

import de.cismet.connectioncontext.AbstractConnectionContext;
import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class ServerResourcesLoaderClient extends AbstractServerResourcesLoader {

    //~ Methods ----------------------------------------------------------------

    @Override
    public Properties loadProperties(final PropertiesServerResource serverResource) throws Exception {
        final Object ret = SessionManager.getSession()
                    .getConnection()
                    .executeTask(SessionManager.getSession().getUser(),
                        GetServerResourceServerAction.TASK_NAME,
                        "WUNDA_BLAU",
                        serverResource,
                        ConnectionContext.create(
                            AbstractConnectionContext.Category.STATIC,
                            PotenzialflaechenProperties.class.getSimpleName()));
        if (ret instanceof Exception) {
            throw (Exception)ret;
        }
        final Properties properties = new Properties();
        properties.load(new StringReader((String)ret));
        return properties;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ServerResourcesLoaderClient getInstance() {
        return LazyInitialiser.INSTANCE;
    }

    @Override
    public JasperReport loadJasperReport(final JasperReportServerResource serverResource) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
                                                                       // Tools | Templates.
    }

    @Override
    public String loadText(final TextServerResource serverResource) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
                                                                       // Tools | Templates.
    }

    @Override
    public byte[] loadBinary(final BinaryServerResource serverResource) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
                                                                       // Tools | Templates.
    }

    @Override
    public Object load(final ServerResource serverResource) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
                                                                       // Tools | Templates.
    }

    @Override
    @Deprecated
    public <T> T loadJson(final ServerResource serverResource, final Class<T> clazz) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
                                                                       // Tools | Templates.
    }

    @Override
    public <T> T loadJson(final JsonServerResource serverResource, final Class<T> clazz) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
                                                                       // Tools | Templates.
    }

    @Override
    public <T> T loadJson(final JsonServerResource serverResource, final JsonFactory jsonFactory, final Class<T> clazz)
            throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
                                                                       // Tools | Templates.
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final ServerResourcesLoaderClient INSTANCE = new ServerResourcesLoaderClient();

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LazyInitialiser object.
         */
        private LazyInitialiser() {
        }
    }
}
