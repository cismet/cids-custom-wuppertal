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

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.log4j.Logger;

import org.openide.util.lookup.ServiceProvider;

import java.util.List;

import de.cismet.cids.custom.berechtigungspruefung.BerechtigungspruefungMessageNotifier;
import de.cismet.cids.custom.berechtigungspruefung.BerechtigungspruefungProperties;
import de.cismet.cids.custom.objectrenderer.wunda_blau.AlkisLandparcelAggregationRenderer;
import de.cismet.cids.custom.utils.BaulastBescheinigungUtils;
import de.cismet.cids.custom.utils.BerechtigungspruefungFreigabeDialog;
import de.cismet.cids.custom.utils.BerechtigungspruefungStornoDialog;
import de.cismet.cids.custom.utils.berechtigungspruefung.baulastbescheinigung.BerechtigungspruefungBescheinigungDownloadInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.katasterauszug.BerechtigungspruefungAlkisDownloadInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.katasterauszug.BerechtigungspruefungAlkisEinzelnachweisDownloadInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.katasterauszug.BerechtigungspruefungAlkisKarteDownloadInfo;
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

    @Override
    public void messageRetrieved(final CidsServerMessageNotifierListenerEvent event) {
        try {
            final List<String> schluesselList = (List)event.getMessage().getContent();

            for (final String schluessel : schluesselList) {
                final MetaClass mcBerechtigungspruefung = ClassCacheMultiple.getMetaClass(
                        "WUNDA_BLAU",
                        "BERECHTIGUNGSPRUEFUNG");

                final String query = "SELECT " + mcBerechtigungspruefung.getID() + ", "
                            + mcBerechtigungspruefung.getPrimaryKey() + " FROM "
                            + mcBerechtigungspruefung.getTableName() + " WHERE schluessel LIKE '" + schluessel
                            + "' LIMIT 1";

                final MetaObject[] mos = SessionManager.getProxy().getMetaObjectByQuery(query, 0);
                if ((mos != null) && (mos.length > 0)) {
                    final CidsBean berechtigungspruefungBean = mos[0].getBean();

                    if (Boolean.TRUE.equals(berechtigungspruefungBean.getProperty("pruefstatus"))) {
                        BerechtigungspruefungFreigabeDialog.getInstance().showFreigabe(berechtigungspruefungBean);
                        final String produkttyp = (String)berechtigungspruefungBean.getProperty("produkttyp");
                        if (BerechtigungspruefungBescheinigungDownloadInfo.PRODUKT_TYP.equals(produkttyp)) {
                            final BerechtigungspruefungBescheinigungDownloadInfo bescheinigungDownloadInfo =
                                new ObjectMapper().readValue((String)berechtigungspruefungBean.getProperty(
                                        "downloadinfo_json"),
                                    BerechtigungspruefungBescheinigungDownloadInfo.class);
                            BaulastBescheinigungUtils.doDownload(bescheinigungDownloadInfo, schluessel);
                        } else if (BerechtigungspruefungAlkisDownloadInfo.PRODUKT_TYP.equals(produkttyp)) {
                            final BerechtigungspruefungAlkisDownloadInfo alkisDownloadInfo =
                                new ObjectMapper().readValue((String)berechtigungspruefungBean.getProperty(
                                        "downloadinfo_json"),
                                    BerechtigungspruefungAlkisDownloadInfo.class);
                            switch (alkisDownloadInfo.getAlkisDownloadTyp()) {
                                case EINZELNACHWEIS: {
                                    final BerechtigungspruefungAlkisEinzelnachweisDownloadInfo einzelnachweisDownloadInfo =
                                        new ObjectMapper().readValue((String)berechtigungspruefungBean.getProperty(
                                                "downloadinfo_json"),
                                            BerechtigungspruefungAlkisEinzelnachweisDownloadInfo.class);
                                    switch (einzelnachweisDownloadInfo.getAlkisObjectTyp()) {
                                        case FLURSTUECKE: {
                                            AlkisLandparcelAggregationRenderer.downloadEinzelnachweisProduct(
                                                einzelnachweisDownloadInfo);
                                        }
                                        break;
                                        case BUCHUNGSBLAETTER: {
//TODO
                                        }
                                        break;
                                    }
                                }
                                break;
                                case KARTE: {
                                    final BerechtigungspruefungAlkisKarteDownloadInfo karteDownloadInfo =
                                        new ObjectMapper().readValue((String)berechtigungspruefungBean.getProperty(
                                                "downloadinfo_json"),
                                            BerechtigungspruefungAlkisKarteDownloadInfo.class);
                                    switch (karteDownloadInfo.getAlkisObjectTyp()) {
                                        case FLURSTUECKE: {
                                            AlkisLandparcelAggregationRenderer.downloadKarteProduct(karteDownloadInfo);
                                        }
                                        break;
                                        case BUCHUNGSBLAETTER: {
//TODO
                                        }
                                        break;
                                    }
                                }
                                break;
                            }
                        }
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
