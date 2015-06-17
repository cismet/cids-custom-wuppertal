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
package de.cismet.cids.custom.nas;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import org.openide.util.lookup.ServiceProvider;

import java.util.HashMap;
import java.util.HashSet;

import javax.swing.SwingUtilities;

import de.cismet.cids.custom.utils.nas.NasProductInfo;
import de.cismet.cids.custom.wunda_blau.search.actions.NasDataQueryAction;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.tools.configuration.StartupHook;

import de.cismet.tools.gui.downloadmanager.DownloadManager;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = StartupHook.class)
public class NasStartupHook implements StartupHook {

    //~ Static fields/initializers ---------------------------------------------

    private static String SEVER_ACTION = "nasDataQuery";

    //~ Instance fields --------------------------------------------------------

    private final transient org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());

    //~ Methods ----------------------------------------------------------------

    @Override
    public void applicationStarted() {
        new Thread(new Runnable() {

                @Override
                public void run() {
                    boolean hasNasAccess = false;
                    try {
                        hasNasAccess = SessionManager.getConnection()
                                    .getConfigAttr(SessionManager.getSession().getUser(), "csa://nasDataQuery") != null;
                    } catch (ConnectionException ex) {
                        log.error("Could not validate action tag for NAS!", ex);
                    }
                    if (hasNasAccess) {
                        final ServerActionParameter paramMethod = new ServerActionParameter(
                                NasDataQueryAction.PARAMETER_TYPE.METHOD.toString(),
                                NasDataQueryAction.METHOD_TYPE.GET_ALL);
                        HashMap<String, NasProductInfo> openOrderIds = null;
                        try {
                            openOrderIds = (HashMap<String, NasProductInfo>)SessionManager
                                        .getProxy().executeTask(
                                        SEVER_ACTION,
                                        "WUNDA_BLAU",
                                        null,
                                        paramMethod);
                        } catch (Exception ex) {
                            log.error("error while getting the list of undelivered nas orders from server", ex);
                        }

                        if ((openOrderIds == null) || openOrderIds.isEmpty()) {
                            log.info("no pending nas orders found for the logged in user");
                            return;
                        }
                        final StringBuilder logMessageBuilder = new StringBuilder();
                        for (final String s : openOrderIds.keySet()) {
                            logMessageBuilder.append(s);
                            logMessageBuilder.append(",");
                        }
                        log.fatal("pending nas orders found: " + logMessageBuilder.toString());
                        // generate a new NasDownload object for pending orders
                        for (final String orderId : openOrderIds.keySet()) {
                            final NasProductInfo pInfo = (NasProductInfo)openOrderIds.get(orderId);
                            final NASDownload download = new NASDownload(
                                    orderId,
                                    pInfo.isIsSplittet(),
                                    pInfo.isDxf(),
                                    pInfo.getRequestName());
                            DownloadManager.instance().add(download);
                        }
                    }
                }
            }).start();
    }
}
