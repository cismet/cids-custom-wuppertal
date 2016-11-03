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

import Sirius.server.middleware.impls.domainserver.DomainServerImpl;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import org.openide.util.lookup.ServiceProvider;

import java.util.List;

import de.cismet.cids.custom.berechtigungspruefung.BerechtigungspruefungMessageNotifier;
import de.cismet.cids.custom.berechtigungspruefung.BerechtigungspruefungProperties;
import de.cismet.cids.custom.objectrenderer.utils.alkis.AlkisProductDownloadHelper;
import de.cismet.cids.custom.utils.BerechtigungspruefungFreigabeDialog;
import de.cismet.cids.custom.utils.BerechtigungspruefungStornoDialog;
import de.cismet.cids.custom.wunda_blau.search.actions.BerechtigungspruefungAnfrageServerAction;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.cids.servermessage.CidsServerMessageNotifier;
import de.cismet.cids.servermessage.CidsServerMessageNotifierListener;
import de.cismet.cids.servermessage.CidsServerMessageNotifierListenerEvent;

import de.cismet.tools.configuration.StartupHook;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = StartupHook.class)
public class BerechtigungspruefungMessageStartUpHook implements StartupHook, CidsServerMessageNotifierListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(BerechtigungspruefungMessageStartUpHook.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    public void applicationStarted() {
        if ("WUNDA_BLAU".equals(SessionManager.getSession().getConnectionInfo().getUserDomain())) {
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
                                    BerechtigungspruefungMessageStartUpHook.this,
                                    BerechtigungspruefungProperties.getInstance().getCsmFreigabe());
                    }
                }).start();
        }
    }

    @Override
    public void messageRetrieved(final CidsServerMessageNotifierListenerEvent event) {
        try {
//            final List<String> schluesselList = (List)event.getMessage().getContent();
            final String benutzer = SessionManager.getSession().getUser().getName();

            final MetaClass mcBerechtigungspruefung = ClassCacheMultiple.getMetaClass(
                    "WUNDA_BLAU",
                    "BERECHTIGUNGSPRUEFUNG");

            final String query = "SELECT " + mcBerechtigungspruefung.getID() + ", "
                        + mcBerechtigungspruefung.getPrimaryKey() + " FROM "
                        + mcBerechtigungspruefung.getTableName() + " WHERE benutzer LIKE '" + benutzer
                        + "' AND pruefstatus IS NOT NULL AND abgeholt IS NOT TRUE";

            final MetaObject[] mos = SessionManager.getProxy().getMetaObjectByQuery(query, 0);
            if ((mos != null) && (mos.length > 0)) {
                for (final MetaObject mo : mos) {
                    final CidsBean berechtigungspruefungBean = mo.getBean();
                    final String schluessel = (String)berechtigungspruefungBean.getProperty("schluessel");

                    if (Boolean.TRUE.equals(berechtigungspruefungBean.getProperty("pruefstatus"))) {
                        BerechtigungspruefungFreigabeDialog.getInstance().showFreigabe(berechtigungspruefungBean);
                        final String produkttyp = (String)berechtigungspruefungBean.getProperty("produkttyp");
                        final String downloadInfo = (String)berechtigungspruefungBean.getProperty("downloadinfo_json");
                        AlkisProductDownloadHelper.download(schluessel, produkttyp, downloadInfo);
                    } else {
                        BerechtigungspruefungStornoDialog.getInstance().showStorno(berechtigungspruefungBean);
                    }
                    try {
                        SessionManager.getProxy()
                                .executeTask(
                                    BerechtigungspruefungAnfrageServerAction.TASK_NAME,
                                    "WUNDA_BLAU",
                                    null,
                                    new ServerActionParameter<String>(
                                        BerechtigungspruefungAnfrageServerAction.ParameterType.ABGEHOLT.toString(),
                                        schluessel));
                    } catch (final Exception ex) {
                        LOG.warn(ex, ex);
                    }
                }
            }
        } catch (final Exception ex) {
            LOG.warn(ex, ex);
        }
    }
}
