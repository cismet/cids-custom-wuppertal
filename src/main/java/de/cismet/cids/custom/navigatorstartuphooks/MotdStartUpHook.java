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
package de.cismet.cids.custom.navigatorstartuphooks;

import org.apache.log4j.Logger;

import org.openide.util.lookup.ServiceProvider;

import de.cismet.cids.custom.motd.MotdDialog;
import de.cismet.cids.custom.wunda_blau.startuphooks.MotdWundaStartupHook;

import de.cismet.cids.servermessage.CidsServerMessageNotifier;
import de.cismet.cids.servermessage.CidsServerMessageNotifierListener;
import de.cismet.cids.servermessage.CidsServerMessageNotifierListenerEvent;
import de.cismet.tools.StaticDebuggingTools;

import de.cismet.tools.configuration.StartupHook;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = StartupHook.class)
public class MotdStartUpHook implements StartupHook {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(MotdStartUpHook.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    public void applicationStarted() {
        if (StaticDebuggingTools.checkHomeForFile("wundaMotdEnabled")) {
            CidsServerMessageNotifier.getInstance()
                    .subscribe(MotdDialog.getInstance(), MotdWundaStartupHook.MOTD_MESSAGE_MOTD);
            CidsServerMessageNotifier.getInstance().subscribe(new CidsServerMessageNotifierListener() {

                    @Override
                    public void messageRetrieved(final CidsServerMessageNotifierListenerEvent event) {
                        if ((event != null) && (event.getMessage() != null)) {
                            LOG.info(
                                event.getMessage().getId()
                                        + " ("
                                        + event.getMessage().getCategory()
                                        + "): "
                                        + event.getMessage().getMessage());
                        }
                    }
                }, null);
            CidsServerMessageNotifier.getInstance().start();
        }
    }
}
