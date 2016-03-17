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

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import org.apache.log4j.Logger;

import org.openide.util.lookup.ServiceProvider;

import de.cismet.cids.custom.utils.BaulastBescheinigungDialog;
import de.cismet.cids.custom.utils.BerechtigungspruefungAnfrageEingangDialog;
import de.cismet.cids.custom.utils.berechtigungspruefung.BerechtigungspruefungProperties;

import de.cismet.cids.servermessage.CidsServerMessageNotifier;

import de.cismet.tools.configuration.StartupHook;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = StartupHook.class)
public class BescheinigungspruefungMessageStartUpHook implements StartupHook {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(BescheinigungspruefungMessageStartUpHook.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    public void applicationStarted() {
        new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        if (SessionManager.getConnection().hasConfigAttr(
                                        SessionManager.getSession().getUser(),
                                        "csm://"
                                        + BerechtigungspruefungProperties.CSM_ANFRAGE)) {
                            CidsServerMessageNotifier.getInstance()
                                    .subscribe(
                                        BerechtigungspruefungAnfrageEingangDialog.getInstance(),
                                        BerechtigungspruefungProperties.CSM_ANFRAGE);
                        }
                    } catch (ConnectionException ex) {
                        LOG.warn(
                            "Konnte Rechte an csm://"
                                    + BerechtigungspruefungProperties.CSM_ANFRAGE
                                    + " nicht abfragen. Keine Benachrichtung bei neuen Prüfungsanfragen!",
                            ex);
                    }

                    try {
                        if (SessionManager.getConnection().hasConfigAttr(
                                        SessionManager.getSession().getUser(),
                                        "csm://"
                                        + BerechtigungspruefungProperties.CSM_FREIGABE)) {
                            CidsServerMessageNotifier.getInstance()
                                    .subscribe(
                                        BaulastBescheinigungDialog.getInstance(),
                                        BerechtigungspruefungProperties.CSM_FREIGABE);
                        }
                    } catch (ConnectionException ex) {
                        LOG.warn(
                            "Konnte Rechte an csm://"
                                    + BerechtigungspruefungProperties.CSM_FREIGABE
                                    + " nicht abfragen. Keine Benachrichtung bei neuen Prüfungen!",
                            ex);
                    }
                }
            }).start();
    }
}
