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

import de.cismet.cids.custom.berechtigungspruefung.BerechtigungspruefungMessageNotifier;
import de.cismet.cids.custom.berechtigungspruefung.BerechtigungspruefungProperties;
import de.cismet.cids.custom.utils.BaulastBescheinigungDialog;

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
                                        + BerechtigungspruefungProperties.getInstance().getCsmAnfrage())) {
                            CidsServerMessageNotifier.getInstance()
                                    .subscribe(
                                        BerechtigungspruefungMessageNotifier.getInstance(),
                                        BerechtigungspruefungProperties.getInstance().getCsmAnfrage());
                        }
                    } catch (ConnectionException ex) {
                        LOG.warn(
                            "Konnte Rechte an csm://"
                                    + BerechtigungspruefungProperties.getInstance().getCsmAnfrage()
                                    + " nicht abfragen. Keine Benachrichtung bei neuen Prüfungsanfragen!",
                            ex);
                    }
                    try {
                        if (SessionManager.getConnection().hasConfigAttr(
                                        SessionManager.getSession().getUser(),
                                        "csm://"
                                        + BerechtigungspruefungProperties.getInstance().getCsmBearbeitung())) {
                            CidsServerMessageNotifier.getInstance()
                                    .subscribe(
                                        BerechtigungspruefungMessageNotifier.getInstance(),
                                        BerechtigungspruefungProperties.getInstance().getCsmBearbeitung());
                        }
                    } catch (ConnectionException ex) {
                        LOG.warn(
                            "Konnte Rechte an csm://"
                                    + BerechtigungspruefungProperties.getInstance().getCsmBearbeitung()
                                    + " nicht abfragen. Keine Benachrichtung bei neuen Prüfungsanfragen!",
                            ex);
                    }

                    CidsServerMessageNotifier.getInstance()
                            .subscribe(
                                BaulastBescheinigungDialog.getInstance(),
                                BerechtigungspruefungProperties.getInstance().getCsmFreigabe());
                }
            }).start();
    }
}
