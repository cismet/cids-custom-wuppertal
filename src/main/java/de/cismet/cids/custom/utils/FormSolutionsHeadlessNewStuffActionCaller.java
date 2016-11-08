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
import de.cismet.cids.custom.wunda_blau.search.actions.FormSolutionServerNewStuffAvailableAction;
import de.cismet.cids.navigator.utils.ClassCacheMultiple;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;
import org.apache.log4j.Logger;

/**
 *
 * @author jruiz
 */
public class FormSolutionsHeadlessNewStuffActionCaller {
    
    private static final Logger LOG = Logger.getLogger(FormSolutionsHeadlessNewStuffActionCaller.class);
    
    private static final String CONNECTION_PROXY_CLASS = "Sirius.navigator.connection.proxy.DefaultConnectionProxyHandler";
    private static final String CONNECTION_CLASS = "Sirius.navigator.connection.RESTfulConnection";
    private static final String DOMAIN = "WUNDA_BLAU";
    
    public static void main(final String[] args) {
        final String callserver = args[0];
        final String user = args[1];
        final String password = args[2];
        
        Log4JQuickConfig.configure4LumbermillOnLocalhost();

        if (FormSolutionsHeadlessNewStuffActionCaller.authenticate(user, password, callserver)) {        
            new FormSolutionsHeadlessNewStuffActionCaller().executeTask();
        }
    }
    
    private void executeTask() {
        try {            
            SessionManager.getProxy().executeTask(FormSolutionServerNewStuffAvailableAction.TASK_NAME, DOMAIN, null);
        } catch (ConnectionException ex) {
            LOG.error("error executing task", ex);
        }
        
    }
    
    
 private static boolean authenticate(final String user, final String password, final String callserver) {
        try {
            final Connection connection = ConnectionFactory.getFactory().createConnection(CONNECTION_CLASS, callserver);
            final ConnectionInfo connectionInfo = new ConnectionInfo();
            connectionInfo.setCallserverURL(callserver);
            connectionInfo.setPassword(password);
            connectionInfo.setUserDomain(DOMAIN);
            connectionInfo.setUsergroup(null);
            connectionInfo.setUsergroupDomain(DOMAIN);
            connectionInfo.setUsername(user);
            final ConnectionSession session = ConnectionFactory.getFactory().createSession(connection, connectionInfo, true);
            final ConnectionProxy proxy = ConnectionFactory.getFactory().createProxy(CONNECTION_PROXY_CLASS, session);
            SessionManager.init(proxy);

            ClassCacheMultiple.setInstance(DOMAIN);
            return true;
        } catch (final Exception ex) {
            LOG.error("Error while login", ex);
            return false;
        }
    }    
}
