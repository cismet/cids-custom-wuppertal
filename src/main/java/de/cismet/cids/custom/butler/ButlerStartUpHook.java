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
package de.cismet.cids.custom.butler;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import org.apache.log4j.Logger;

import org.openide.util.lookup.ServiceProvider;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.SwingUtilities;

import de.cismet.cids.custom.wunda_blau.search.actions.ButlerQueryAction;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.tools.configuration.StartupHook;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = StartupHook.class)
public class ButlerStartUpHook implements StartupHook {

    //~ Static fields/initializers ---------------------------------------------

    private static final String SERVER_ACTION = "butler1Query";
    private static final Logger log = Logger.getLogger(ButlerStartUpHook.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    public void applicationStarted() {
        SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    restartPendingButler1Requests();
                }
            });
    }

    /**
     * DOCUMENT ME!
     */
    private void restartPendingButler1Requests() {
        final ServerActionParameter paramMethod = new ServerActionParameter(
                ButlerQueryAction.PARAMETER_TYPE.METHOD.toString(),
                ButlerQueryAction.METHOD_TYPE.GET_ALL);
        HashMap<String, ButlerProductInfo> openRequestIds = null;
        try {
            openRequestIds = (HashMap<String, ButlerProductInfo>)SessionManager.getProxy()
                        .executeTask(
                                SERVER_ACTION,
                                "WUNDA_BLAU",
                                null,
                                paramMethod);
        } catch (ConnectionException ex) {
            log.error("error while getting the list of undelivered butler 1 requests from server", ex);
            return;
        }
        if ((openRequestIds == null) || openRequestIds.isEmpty()) {
            log.info("no pending butler orders found for the logged in user");
            return;
        }

        final StringBuilder logMessageBuilder = new StringBuilder();
        for (final String s : openRequestIds.keySet()) {
            logMessageBuilder.append(s);
            logMessageBuilder.append(",");
        }
        log.fatal("pending nas orders found: " + logMessageBuilder.toString());
        // generate a new NasDownload object for pending orders
        final ArrayList<ButlerDownload> downloads = new ArrayList<ButlerDownload>();
        for (final String requestId : openRequestIds.keySet()) {
        }
    }
}
