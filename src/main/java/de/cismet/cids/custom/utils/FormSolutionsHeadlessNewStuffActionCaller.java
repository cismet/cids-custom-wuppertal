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
package de.cismet.cids.custom.utils;

import Sirius.navigator.connection.Connection;
import Sirius.navigator.connection.ConnectionFactory;
import Sirius.navigator.connection.ConnectionInfo;
import Sirius.navigator.connection.ConnectionSession;
import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.connection.proxy.ConnectionProxy;
import Sirius.navigator.exception.ConnectionException;

import org.apache.log4j.Logger;

import de.cismet.cids.custom.wunda_blau.search.actions.FormSolutionServerNewStuffAvailableAction;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class FormSolutionsHeadlessNewStuffActionCaller implements ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(FormSolutionsHeadlessNewStuffActionCaller.class);

    private static final String CONNECTION_PROXY_CLASS =
        "Sirius.navigator.connection.proxy.DefaultConnectionProxyHandler";
    private static final String CONNECTION_CLASS = "Sirius.navigator.connection.RESTfulConnection";
    private static final String DOMAIN = "WUNDA_BLAU";

    //~ Instance fields --------------------------------------------------------

    private final ConnectionContext connectionContext = ConnectionContext.createDummy();

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        final String callserver = args[0];
        final String user = args[1];
        final String password = args[2];
        final String compression = args[3];

        Log4JQuickConfig.configure4LumbermillOnLocalhost();

        if (FormSolutionsHeadlessNewStuffActionCaller.authenticate(
                        user,
                        password,
                        callserver,
                        "ja".equalsIgnoreCase(compression))) {
            new FormSolutionsHeadlessNewStuffActionCaller().executeTask();
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void executeTask() {
        try {
            SessionManager.getProxy()
                    .executeTask(
                        FormSolutionServerNewStuffAvailableAction.TASK_NAME,
                        DOMAIN,
                        (Object)null,
                        getConnectionContext());
        } catch (ConnectionException ex) {
            System.err.println("error executing task");
            ex.printStackTrace();
            LOG.error("error executing task", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   user                DOCUMENT ME!
     * @param   password            DOCUMENT ME!
     * @param   callserver          DOCUMENT ME!
     * @param   compressionEnabled  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static boolean authenticate(final String user,
            final String password,
            final String callserver,
            final boolean compressionEnabled) {
        try {
            final Connection connection = ConnectionFactory.getFactory()
                        .createConnection(
                            CONNECTION_CLASS,
                            callserver,
                            compressionEnabled,
                            ConnectionContext.createDeprecated());
            final ConnectionInfo connectionInfo = new ConnectionInfo();
            connectionInfo.setCallserverURL(callserver);
            connectionInfo.setPassword(password);
            connectionInfo.setUserDomain(DOMAIN);
            connectionInfo.setUsergroup(null);
            connectionInfo.setUsergroupDomain(DOMAIN);
            connectionInfo.setUsername(user);
            final ConnectionSession session = ConnectionFactory.getFactory()
                        .createSession(connection, connectionInfo, true, ConnectionContext.createDeprecated());
            final ConnectionProxy proxy = ConnectionFactory.getFactory()
                        .createProxy(CONNECTION_PROXY_CLASS, session, ConnectionContext.createDeprecated());
            SessionManager.init(proxy);

            ClassCacheMultiple.setInstance(DOMAIN, ConnectionContext.createDeprecated());
            return true;
        } catch (final Exception ex) {
            System.err.println("Error while login");
            ex.printStackTrace();
            LOG.error("Error while login", ex);
            return false;
        }
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }
}
