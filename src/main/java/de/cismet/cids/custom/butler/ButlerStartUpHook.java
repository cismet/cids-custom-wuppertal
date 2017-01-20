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

import de.cismet.cids.custom.utils.butler.ButlerRequestInfo;
import de.cismet.cids.custom.wunda_blau.search.actions.ButlerQueryAction;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.tools.configuration.StartupHook;

import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.MultipleDownload;

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
        new Thread(new Runnable() {

                @Override
                public void run() {
                    restartPendingButler1Requests();
                }
            }).start();
    }

    /**
     * DOCUMENT ME!
     */
    private void restartPendingButler1Requests() {
        boolean hasButlerAccess = false;
        try {
            hasButlerAccess = SessionManager.getConnection()
                        .getConfigAttr(SessionManager.getSession().getUser(), "csa://butler1Query") != null;
        } catch (ConnectionException ex) {
            log.error("Could not validate action tag for Butler!", ex);
        }
        if (hasButlerAccess) {
            final ServerActionParameter paramMethod = new ServerActionParameter(
                    ButlerQueryAction.PARAMETER_TYPE.METHOD.toString(),
                    ButlerQueryAction.METHOD_TYPE.GET_ALL);
            HashMap<String, ButlerRequestInfo> openRequestIds = null;
            try {
                openRequestIds = (HashMap<String, ButlerRequestInfo>)SessionManager.getProxy()
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
                final ButlerRequestInfo info = (ButlerRequestInfo)openRequestIds.get(requestId);
                final ButlerDownload download = new ButlerDownload(requestId, info.getUserOrderId(), info.getProduct());
                downloads.add(download);
            }
            DownloadManager.instance()
                    .add(new MultipleDownload(
                            downloads,
                            org.openide.util.NbBundle.getMessage(
                                ButlerStartUpHook.class,
                                "ButlerStartUpHook.downloadTitle")));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        final ButlerStartUpHook cptHook = new ButlerStartUpHook();
        cptHook.restartPendingButler1Requests();
    }
}
