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
package de.cismet.cids.custom.objectrenderer.utils.alkis;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;
import Sirius.navigator.ui.ComponentRegistry;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;

import org.apache.log4j.Logger;

import java.awt.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.objectrenderer.utils.billing.BillingPopup;
import de.cismet.cids.custom.utils.BaulastBescheinigungUtils;
import de.cismet.cids.custom.utils.ByteArrayActionDownload;
import de.cismet.cids.custom.utils.alkis.AlkisProducts;
import de.cismet.cids.custom.utils.berechtigungspruefung.baulastbescheinigung.BerechtigungspruefungBescheinigungBaulastInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.baulastbescheinigung.BerechtigungspruefungBescheinigungDownloadInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.baulastbescheinigung.BerechtigungspruefungBescheinigungFlurstueckInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.baulastbescheinigung.BerechtigungspruefungBescheinigungGruppeInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.katasterauszug.BerechtigungspruefungAlkisDownloadInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.katasterauszug.BerechtigungspruefungAlkisEinzelnachweisDownloadInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.katasterauszug.BerechtigungspruefungAlkisKarteDownloadInfo;
import de.cismet.cids.custom.wunda_blau.search.actions.AlkisProductServerAction;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.downloadmanager.Download;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;
import de.cismet.tools.gui.downloadmanager.MultipleDownload;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class AlkisProductDownloadHelper {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(AlkisProductDownloadHelper.class);

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  downloadInfo       DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public static void downloadBuchungsblattnachweisStichtagProduct(
            final BerechtigungspruefungAlkisEinzelnachweisDownloadInfo downloadInfo,
            final ConnectionContext connectionContext) {
        final Component parent = ComponentRegistry.getRegistry().getDescriptionPane();
        final String product = downloadInfo.getAlkisProdukt();
        final String actionTag = ClientAlkisProducts.getInstance().getActionTag(product);
        final String downloadTitle = ClientAlkisProducts.getInstance().getProductName(product);
        final Date stichtag = downloadInfo.getDate();

        final List<Download> downloads = new LinkedList<>();
        for (final String buchungsblattCode : downloadInfo.getAlkisCodes()) {
            final Download d = createBuchungsblattStichtagProductDownload(
                    stichtag,
                    downloadTitle,
                    product,
                    actionTag,
                    buchungsblattCode,
                    parent,
                    connectionContext);
            downloads.add(d);
        }

        if (!DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(parent)) {
            return;
        }

        final String jobname = DownloadManagerDialog.getInstance().getJobName();
        if (downloads.size() > 1) {
            DownloadManager.instance().add(new MultipleDownload(downloads, jobname));
        } else if (downloads.size() == 1) {
            DownloadManager.instance().add(downloads.get(0));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   usageKey           DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    public static String getFertigungsVermerk(final String usageKey, final ConnectionContext connectionContext)
            throws ConnectionException {
        final String fertigungsVermerk;
        final String currentUsageKey = (BillingPopup.getInstance().getCurrentUsage() != null)
            ? BillingPopup.getInstance().getCurrentUsage().getKey() : null;
        if ((usageKey == null) || (usageKey.equals(currentUsageKey))) {
            fertigungsVermerk = SessionManager.getConnection()
                        .getConfigAttr(
                                SessionManager.getSession().getUser(),
                                "custom.alkis.fertigungsVermerk@WUNDA_BLAU",
                                connectionContext);
        } else {
            fertigungsVermerk = null;
        }
        return fertigungsVermerk;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  downloadInfo       DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public static void downloadBuchungsblattnachweisProduct(
            final BerechtigungspruefungAlkisEinzelnachweisDownloadInfo downloadInfo,
            final ConnectionContext connectionContext) {
        final Component parent = ComponentRegistry.getRegistry().getDescriptionPane();
        final String product = downloadInfo.getAlkisProdukt();
        final String actionTag = ClientAlkisProducts.getInstance().getActionTag(product);
        final String downloadTitle = ClientAlkisProducts.getInstance().getProductName(product);

        if (!ObjectRendererUtils.checkActionTag(actionTag, connectionContext)) {
            showNoProductPermissionWarning(parent);
            // return;
        }

        String extension = ".pdf";
        if (
            ClientAlkisProducts.getInstance().get(ClientAlkisProducts.Type.GRUNDSTUECKSNACHWEIS_NRW_HTML).equals(
                        product)
                    || ClientAlkisProducts.getInstance().get(ClientAlkisProducts.Type.BESTANDSNACHWEIS_KOMMUNAL_HTML)
                    .equals(product)
                    || ClientAlkisProducts.getInstance().get(
                        ClientAlkisProducts.Type.BESTANDSNACHWEIS_KOMMUNAL_INTERN_HTML).equals(product)
                    || ClientAlkisProducts.getInstance().get(ClientAlkisProducts.Type.BESTANDSNACHWEIS_NRW_HTML).equals(
                        product)) {
            extension = ".html";
        }

        if (!DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(parent)) {
            return;
        }
        final String directory = DownloadManagerDialog.getInstance().getJobName();

        final List<Download> downloads = new LinkedList<>();

        for (final String buchungsblattCode : downloadInfo.getAlkisCodes()) {
            if ((buchungsblattCode == null) || (buchungsblattCode.trim().length() <= 0)) {
                continue;
            }

            final String queryID = AlkisProducts.escapeHtmlSpaces(buchungsblattCode);

            try {
                final String fertigungsVermerk = getFertigungsVermerk("WV ein", connectionContext);
                final String filename = product + "."
                            + buchungsblattCode.replace("/", "--").trim().replaceAll(" +", "_");    // replace all whitespaces;
                final Download download = new ByteArrayActionDownload(
                        AlkisProductServerAction.TASK_NAME,
                        AlkisProductServerAction.Body.EINZELNACHWEIS,
                        new ServerActionParameter[] {
                            new ServerActionParameter(AlkisProductServerAction.Parameter.PRODUKT.toString(), product),
                            new ServerActionParameter(
                                AlkisProductServerAction.Parameter.ALKIS_CODE.toString(),
                                queryID),
                            new ServerActionParameter(
                                AlkisProductServerAction.Parameter.FERTIGUNGSVERMERK.toString(),
                                fertigungsVermerk)
                        },
                        downloadTitle,
                        directory,
                        filename,
                        extension,
                        connectionContext);
                downloads.add(download);
            } catch (Exception ex) {
                ObjectRendererUtils.showExceptionWindowToUser(
                    "Fehler beim Aufruf des Produkts: "
                            + product,
                    ex,
                    parent);
                LOG.error("The URL to download product '" + product + "' (actionTag: " + actionTag
                            + ") could not be constructed.",
                    ex);
            }
        }

        if (downloads.size() > 1) {
            DownloadManager.instance().add(new MultipleDownload(downloads, directory));
        } else if (downloads.size() == 1) {
            DownloadManager.instance().add(downloads.get(0));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   product             DOCUMENT ME!
     * @param   stichtag            DOCUMENT ME!
     * @param   buchungsblattCodes  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static BerechtigungspruefungAlkisEinzelnachweisDownloadInfo createAlkisBuchungsblattnachweisDownloadInfo(
            final String product,
            final Date stichtag,
            final List<String> buchungsblattCodes) {
        final BerechtigungspruefungAlkisEinzelnachweisDownloadInfo downloadInfo =
            new BerechtigungspruefungAlkisEinzelnachweisDownloadInfo(
                BerechtigungspruefungAlkisDownloadInfo.AlkisObjektTyp.BUCHUNGSBLAETTER,
                product,
                stichtag,
                buchungsblattCodes);
        return downloadInfo;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   product             DOCUMENT ME!
     * @param   buchungsblattCodes  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static BerechtigungspruefungAlkisEinzelnachweisDownloadInfo createAlkisBuchungsblattachweisDownloadInfo(
            final String product,
            final List<String> buchungsblattCodes) {
        final BerechtigungspruefungAlkisEinzelnachweisDownloadInfo downloadInfo =
            new BerechtigungspruefungAlkisEinzelnachweisDownloadInfo(
                BerechtigungspruefungAlkisDownloadInfo.AlkisObjektTyp.BUCHUNGSBLAETTER,
                product,
                buchungsblattCodes);
        return downloadInfo;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   stichtag                   DOCUMENT ME!
     * @param   downloadTitle              DOCUMENT ME!
     * @param   product                    DOCUMENT ME!
     * @param   actionTag                  DOCUMENT ME!
     * @param   completeBuchungsblattCode  DOCUMENT ME!
     * @param   parent                     DOCUMENT ME!
     * @param   connectionContext          DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Download createBuchungsblattStichtagProductDownload(final Date stichtag,
            final String downloadTitle,
            final String product,
            final String actionTag,
            final String completeBuchungsblattCode,
            final Component parent,
            final ConnectionContext connectionContext) {
        if (!ObjectRendererUtils.checkActionTag(actionTag, connectionContext)) {
            showNoProductPermissionWarning(parent);
            return null;
        }

        try {
            if (completeBuchungsblattCode.length() > 0) {
                final String alkisCode = AlkisProducts.escapeHtmlSpaces(completeBuchungsblattCode);

                final String fertigungsVermerk = getFertigungsVermerk("WV ein", connectionContext);
                final String directory = DownloadManagerDialog.getInstance().getJobName();
                final String filename = product + "."
                            + completeBuchungsblattCode.replace("/", "--").trim().replaceAll(" +", "_"); // replace all whitespaces
                final String extension = ".pdf";
                final Download download = new ByteArrayActionDownload(
                        AlkisProductServerAction.TASK_NAME,
                        AlkisProductServerAction.Body.EINZELNACHWEIS_STICHTAG,
                        new ServerActionParameter[] {
                            new ServerActionParameter(AlkisProductServerAction.Parameter.PRODUKT.toString(), product),
                            new ServerActionParameter(
                                AlkisProductServerAction.Parameter.ALKIS_CODE.toString(),
                                alkisCode),
                            new ServerActionParameter(AlkisProductServerAction.Parameter.STICHTAG.toString(), stichtag),
                            new ServerActionParameter(
                                AlkisProductServerAction.Parameter.FERTIGUNGSVERMERK.toString(),
                                fertigungsVermerk)
                        },
                        downloadTitle,
                        directory,
                        filename,
                        extension,
                        connectionContext);
                return download;
            }
        } catch (Exception ex) {
            ObjectRendererUtils.showExceptionWindowToUser(
                "Fehler beim Aufruf des Produkts: "
                        + product,
                ex,
                parent);
            LOG.error(ex);
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   product     DOCUMENT ME!
     * @param   alkisCodes  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static BerechtigungspruefungAlkisKarteDownloadInfo createBerechtigungspruefungAlkisKarteDownloadInfo(
            final String product,
            final List<String> alkisCodes) {
        final BerechtigungspruefungAlkisKarteDownloadInfo downloadInfo =
            new BerechtigungspruefungAlkisKarteDownloadInfo(
                BerechtigungspruefungAlkisDownloadInfo.AlkisObjektTyp.FLURSTUECKE,
                alkisCodes);
        return downloadInfo;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   product     DOCUMENT ME!
     * @param   alkisCodes  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static BerechtigungspruefungAlkisEinzelnachweisDownloadInfo
    createBerechtigungspruefungAlkisEinzelnachweisDownloadInfo(final String product, final List<String> alkisCodes) {
        final BerechtigungspruefungAlkisEinzelnachweisDownloadInfo downloadInfo =
            new BerechtigungspruefungAlkisEinzelnachweisDownloadInfo(
                BerechtigungspruefungAlkisDownloadInfo.AlkisObjektTyp.FLURSTUECKE,
                product,
                alkisCodes);
        return downloadInfo;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  downloadInfo       DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public static void downloadEinzelnachweisProduct(
            final BerechtigungspruefungAlkisEinzelnachweisDownloadInfo downloadInfo,
            final ConnectionContext connectionContext) {
        final Component parent = ComponentRegistry.getRegistry().getDescriptionPane();
        final String product = downloadInfo.getAlkisProdukt();
        final String actionTag = ClientAlkisProducts.getInstance().getActionTag(product);
        final String downloadTitle = ClientAlkisProducts.getInstance().getProductName(product);

        if (
            !ObjectRendererUtils.checkActionTag(
                        ClientAlkisProducts.getInstance().getActionTag(product),
                        connectionContext)) {
            showNoProductPermissionWarning(parent);
            return;
        }

        if (!DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(parent)) {
            return;
        }

        final String directory = DownloadManagerDialog.getInstance().getJobName();
        final String extension;
        if (ClientAlkisProducts.getInstance().get(ClientAlkisProducts.Type.FLURSTUECKSNACHWEIS_HTML).equals(product)
                    || ClientAlkisProducts.getInstance().get(
                        ClientAlkisProducts.Type.FLURSTUECKS_UND_EIGENTUMSNACHWEIS_KOMMUNAL_HTML).equals(product)
                    || ClientAlkisProducts.getInstance().get(
                        ClientAlkisProducts.Type.FLURSTUECKS_UND_EIGENTUMSNACHWEIS_KOMMUNAL_INTERN_HTML).equals(product)
                    || ClientAlkisProducts.getInstance().get(
                        ClientAlkisProducts.Type.FLURSTUECKS_UND_EIGENTUMSNACHWEIS_NRW_HTML).equals(product)) {
            extension = ".html";
        } else {
            extension = ".pdf";
        }

        final List<Download> downloads = new LinkedList<>();

        for (final String alkisCode : downloadInfo.getAlkisCodes()) {
            if ((alkisCode != null) && (alkisCode.length() > 0)) {
                try {
                    final String fertigungsVermerk = getFertigungsVermerk("WV ein", connectionContext);
                    final String filename = product + "." + alkisCode.replace("/", "--");
                    final Download download = new ByteArrayActionDownload(
                            AlkisProductServerAction.TASK_NAME,
                            AlkisProductServerAction.Body.EINZELNACHWEIS,
                            new ServerActionParameter[] {
                                new ServerActionParameter(
                                    AlkisProductServerAction.Parameter.PRODUKT.toString(),
                                    product),
                                new ServerActionParameter(
                                    AlkisProductServerAction.Parameter.ALKIS_CODE.toString(),
                                    alkisCode),
                                new ServerActionParameter(
                                    AlkisProductServerAction.Parameter.FERTIGUNGSVERMERK.toString(),
                                    fertigungsVermerk)
                            },
                            downloadTitle,
                            directory,
                            filename,
                            extension,
                            connectionContext);
                    downloads.add(download);
                } catch (final Exception ex) {
                    ObjectRendererUtils.showExceptionWindowToUser("Fehler beim Aufruf des Produkts: " + product,
                        ex,
                        parent);
                    LOG.error("The URL to download product '" + product + "' (actionTag: " + actionTag
                                + ") could not be constructed.",
                        ex);
                }
            }
        }

        if (downloads.size() > 1) {
            DownloadManager.instance().add(new MultipleDownload(downloads, directory));
        } else if (downloads.size() == 1) {
            DownloadManager.instance().add(downloads.get(0));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   alkisCodes  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static BerechtigungspruefungAlkisKarteDownloadInfo createBerechtigungspruefungAlkisKarteDownloadInfo(
            final List<String> alkisCodes) {
        final BerechtigungspruefungAlkisKarteDownloadInfo downloadInfo =
            new BerechtigungspruefungAlkisKarteDownloadInfo(
                BerechtigungspruefungAlkisDownloadInfo.AlkisObjektTyp.FLURSTUECKE,
                alkisCodes);
        return downloadInfo;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  downloadInfo       downloadTitle DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public static void downloadKarteProduct(final BerechtigungspruefungAlkisKarteDownloadInfo downloadInfo,
            final ConnectionContext connectionContext) {
        final Component parent = ComponentRegistry.getRegistry().getDescriptionPane();
        final String downloadTitle = "Karte";

        if (!ObjectRendererUtils.checkActionTag(AlkisProducts.PRODUCT_ACTION_TAG_KARTE, connectionContext)) {
            showNoProductPermissionWarning(parent);
            return;
        }

        if (!DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(parent)) {
            return;
        }
        final String directory = DownloadManagerDialog.getInstance().getJobName();

        final List<Download> downloads = new LinkedList<>();
        for (final String alkisCode : downloadInfo.getAlkisCodes()) {
            if (alkisCode.length() > 0) {
                try {
                    final String filename = "LK.GDBNRW.A.F." + alkisCode.replace("/", "--");
                    final String extension = ".pdf";
                    final String fertigungsVermerk = getFertigungsVermerk("WV ein", connectionContext);

                    final Download download = new ByteArrayActionDownload(
                            AlkisProductServerAction.TASK_NAME,
                            AlkisProductServerAction.Body.KARTE,
                            new ServerActionParameter[] {
                                new ServerActionParameter(
                                    AlkisProductServerAction.Parameter.ALKIS_CODE.toString(),
                                    alkisCode),
                                new ServerActionParameter(
                                    AlkisProductServerAction.Parameter.FERTIGUNGSVERMERK.toString(),
                                    fertigungsVermerk)
                            },
                            downloadTitle,
                            directory,
                            filename,
                            extension,
                            connectionContext);
                    downloads.add(download);
                } catch (final Exception ex) {
                    ObjectRendererUtils.showExceptionWindowToUser(
                        "Fehler beim Aufruf des Produkts: Kartenprodukt",
                        ex,
                        parent);
                    LOG.error(ex);
                }
            }
        }

        if (downloads.size() > 1) {
            DownloadManager.instance().add(new MultipleDownload(downloads, directory));
        } else if (downloads.size() == 1) {
            DownloadManager.instance().add(downloads.get(0));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   schluessel         DOCUMENT ME!
     * @param   produkttyp         DOCUMENT ME!
     * @param   downloadInfo       DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void download(final String schluessel,
            final String produkttyp,
            final String downloadInfo,
            final ConnectionContext connectionContext) throws Exception {
        if (BerechtigungspruefungBescheinigungDownloadInfo.PRODUKT_TYP.equals(produkttyp)) {
            final BerechtigungspruefungBescheinigungDownloadInfo bescheinigungDownloadInfo =
                new ObjectMapper().readValue(downloadInfo, BerechtigungspruefungBescheinigungDownloadInfo.class);
            BaulastBescheinigungUtils.doDownload(bescheinigungDownloadInfo, schluessel, connectionContext);
        } else if (BerechtigungspruefungAlkisDownloadInfo.PRODUKT_TYP.equals(produkttyp)) {
            final BerechtigungspruefungAlkisDownloadInfo alkisDownloadInfo =
                new ObjectMapper().readValue(downloadInfo, BerechtigungspruefungAlkisDownloadInfo.class);
            switch (alkisDownloadInfo.getAlkisDownloadTyp()) {
                case EINZELNACHWEIS: {
                    final BerechtigungspruefungAlkisEinzelnachweisDownloadInfo einzelnachweisDownloadInfo =
                        new ObjectMapper().readValue(
                            downloadInfo,
                            BerechtigungspruefungAlkisEinzelnachweisDownloadInfo.class);
                    switch (einzelnachweisDownloadInfo.getAlkisObjectTyp()) {
                        case FLURSTUECKE: {
                            downloadEinzelnachweisProduct(einzelnachweisDownloadInfo, connectionContext);
                        }
                        break;
                        case BUCHUNGSBLAETTER: {
                            if (ClientAlkisProducts.getInstance().get(
                                            ClientAlkisProducts.Type.BESTANDSNACHWEIS_STICHTAGSBEZOGEN_NRW_PDF).equals(
                                            einzelnachweisDownloadInfo.getAlkisProdukt())) {
                                downloadBuchungsblattnachweisStichtagProduct(
                                    einzelnachweisDownloadInfo,
                                    connectionContext);
                            } else {
                                downloadBuchungsblattnachweisProduct(
                                    einzelnachweisDownloadInfo,
                                    connectionContext);
                            }
                        }
                        break;
                    }
                }
                break;
                case KARTE: {
                    final BerechtigungspruefungAlkisKarteDownloadInfo karteDownloadInfo =
                        new ObjectMapper().readValue(downloadInfo, BerechtigungspruefungAlkisKarteDownloadInfo.class);
                    switch (karteDownloadInfo.getAlkisObjectTyp()) {
                        case FLURSTUECKE: {
                            downloadKarteProduct(karteDownloadInfo, connectionContext);
                        }
                        break;
                    }
                }
                break;
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  parent  DOCUMENT ME!
     */
    public static void showNoProductPermissionWarning(final Component parent) {
        JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(parent),
            "Sie besitzen keine Berechtigung zur Erzeugung dieses Produkts!");
    }

    /**
     * DOCUMENT ME!
     *
     * @param   downloadType       DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean checkBerechtigungspruefung(final String downloadType,
            final ConnectionContext connectionContext) {
        try {
            return (SessionManager.getConnection().hasConfigAttr(
                        SessionManager.getSession().getUser(),
                        "berechtigungspruefung_"
                                + downloadType,
                        connectionContext));
        } catch (final Exception ex) {
            LOG.info("could now check Berechtigungspruefung confattr", ex);
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  info                   DOCUMENT ME!
     * @param  jobName                DOCUMENT ME!
     * @param  moreFlurstueckeSuffix  DOCUMENT ME!
     * @param  connectionContext      DOCUMENT ME!
     */
    public static void downloadKarteCustomProduct(final AlkisKarteDownloadInfo info,
            final String jobName,
            final boolean moreFlurstueckeSuffix,
            final ConnectionContext connectionContext) {
        final String title = "ALKIS-Druck";
        final String directory = DownloadManagerDialog.getInstance().getJobName();
        final String filename = info.getProduct() + "." + info.getLandparcelCode().replace("/", "--")
                    + (moreFlurstueckeSuffix ? ".ua" : "");
        final String extension = ".pdf";

        final Download download = new ByteArrayActionDownload(
                AlkisProductServerAction.TASK_NAME,
                AlkisProductServerAction.Body.KARTE_CUSTOM,
                new ServerActionParameter[] {
                    new ServerActionParameter(AlkisProductServerAction.Parameter.PRODUKT.toString(), info.getProduct()),
                    new ServerActionParameter(
                        AlkisProductServerAction.Parameter.ALKIS_CODE.toString(),
                        info.getLandparcelCode()),
                    new ServerActionParameter(AlkisProductServerAction.Parameter.WINKEL.toString(), info.getWinkel()),
                    new ServerActionParameter(AlkisProductServerAction.Parameter.X.toString(), info.getX()),
                    new ServerActionParameter(AlkisProductServerAction.Parameter.Y.toString(), info.getY()),
                    new ServerActionParameter(
                        AlkisProductServerAction.Parameter.MASSSTAB.toString(),
                        info.getMassstab()),
                    new ServerActionParameter(
                        AlkisProductServerAction.Parameter.MASSSTAB_MIN.toString(),
                        info.getMassstabMin()),
                    new ServerActionParameter(
                        AlkisProductServerAction.Parameter.MASSSTAB_MAX.toString(),
                        info.getMassstabMax()),
                    new ServerActionParameter(AlkisProductServerAction.Parameter.ZUSATZ.toString(), info.getZusatz()),
                    new ServerActionParameter(
                        AlkisProductServerAction.Parameter.AUFTRAGSNUMMER.toString(),
                        info.getAuftragsnummer()),
                    new ServerActionParameter(
                        AlkisProductServerAction.Parameter.FERTIGUNGSVERMERK.toString(),
                        info.getFertigungsvermerk())
                },
                title,
                directory,
                filename,
                extension,
                connectionContext);
        DownloadManager.instance().add(download);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean validateUserHasAlkisPrintAccess(final ConnectionContext connectionContext) {
        try {
            return SessionManager.getConnection()
                        .getConfigAttr(SessionManager.getSession().getUser(),
                                "navigator.alkis.print@WUNDA_BLAU",
                                connectionContext)
                        != null;
        } catch (ConnectionException ex) {
            LOG.error("Could not validate action tag for Alkis Print Dialog!", ex);
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean validateUserHasAlkisProductAccess(final ConnectionContext connectionContext) {
        try {
            return SessionManager.getConnection()
                        .getConfigAttr(SessionManager.getSession().getUser(),
                                "csa://alkisProduct@WUNDA_BLAU",
                                connectionContext)
                        != null;
        } catch (ConnectionException ex) {
            LOG.error("Could not validate action tag for Alkis Products!", ex);
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean validateUserHasEigentuemerAccess(final ConnectionContext connectionContext) {
        try {
            return SessionManager.getConnection()
                        .getConfigAttr(SessionManager.getSession().getUser(),
                                AlkisProducts.ALKIS_EIGENTUEMER,
                                connectionContext)
                        != null;
        } catch (ConnectionException ex) {
            LOG.error("Could not validate action tag for Alkis Buchungsblatt!", ex);
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean validateUserHasAlkisHTMLProductAccess(final ConnectionContext connectionContext) {
        try {
            return SessionManager.getConnection()
                        .getConfigAttr(SessionManager.getSession().getUser(),
                                AlkisProducts.ALKIS_HTML_PRODUCTS_ENABLED,
                                connectionContext)
                        != null;
        } catch (ConnectionException ex) {
            LOG.error("Could not validate action tag for Alkis HTML Products!", ex);
        }
        return false;
    }

    /**
     * Creates a new BescheinigungsGruppe object.
     *
     * @param   baulastenBeguenstigt  DOCUMENT ME!
     * @param   baulastenBelastet     DOCUMENT ME!
     * @param   cache                 DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static BerechtigungspruefungBescheinigungGruppeInfo createBerechtigungspruefungBescheinigungGruppeInfo(
            final Collection<CidsBean> baulastenBeguenstigt,
            final Collection<CidsBean> baulastenBelastet,
            final Map<BerechtigungspruefungBescheinigungBaulastInfo, CidsBean> cache) {
        return createBerechtigungspruefungBescheinigungGruppeInfo(new HashMap<CidsBean, Collection<String>>(),
                baulastenBeguenstigt,
                baulastenBelastet,
                cache);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstuecketoGrundstueckeMap  DOCUMENT ME!
     * @param   baulastenBeguenstigtBeans     DOCUMENT ME!
     * @param   baulastenBelastetBeans        DOCUMENT ME!
     * @param   cache                         DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static BerechtigungspruefungBescheinigungGruppeInfo createBerechtigungspruefungBescheinigungGruppeInfo(
            final Map<CidsBean, Collection<String>> flurstuecketoGrundstueckeMap,
            final Collection<CidsBean> baulastenBeguenstigtBeans,
            final Collection<CidsBean> baulastenBelastetBeans,
            final Map<BerechtigungspruefungBescheinigungBaulastInfo, CidsBean> cache) {
        final List<BerechtigungspruefungBescheinigungFlurstueckInfo> flurstueckeInfo =
            new ArrayList<BerechtigungspruefungBescheinigungFlurstueckInfo>();
        for (final CidsBean flurstueck : flurstuecketoGrundstueckeMap.keySet()) {
            flurstueckeInfo.add(createBerechtigungspruefungBescheinigungFlurstueckInfo(
                    flurstueck,
                    flurstuecketoGrundstueckeMap.get(flurstueck)));
        }

        final List<BerechtigungspruefungBescheinigungBaulastInfo> baulastBeguenstigtInfos =
            new ArrayList<BerechtigungspruefungBescheinigungBaulastInfo>();
        for (final CidsBean baulastBeguenstigt : baulastenBeguenstigtBeans) {
            final BerechtigungspruefungBescheinigungBaulastInfo baulastBeguenstigtInfo =
                createBerechtigungspruefungBescheinigungBaulastInfo(
                    baulastBeguenstigt);
            if (cache != null) {
                cache.put(baulastBeguenstigtInfo, baulastBeguenstigt);
            }
            baulastBeguenstigtInfos.add(baulastBeguenstigtInfo);
        }

        final List<BerechtigungspruefungBescheinigungBaulastInfo> baulastBelastetInfos =
            new ArrayList<BerechtigungspruefungBescheinigungBaulastInfo>();
        for (final CidsBean baulastBelastet : baulastenBelastetBeans) {
            final BerechtigungspruefungBescheinigungBaulastInfo baulastBelastetInfo =
                createBerechtigungspruefungBescheinigungBaulastInfo(
                    baulastBelastet);
            if (cache != null) {
                cache.put(baulastBelastetInfo, baulastBelastet);
            }
            baulastBelastetInfos.add(baulastBelastetInfo);
        }

        Collections.sort(flurstueckeInfo, new Comparator<BerechtigungspruefungBescheinigungFlurstueckInfo>() {

                @Override
                public int compare(final BerechtigungspruefungBescheinigungFlurstueckInfo o1,
                        final BerechtigungspruefungBescheinigungFlurstueckInfo o2) {
                    final int compareGemarkung = compareString(o1.getGemarkung(), o2.getGemarkung());
                    if (compareGemarkung != 0) {
                        return compareGemarkung;
                    } else {
                        final int compareFlur = compareString(o1.getFlur(), o2.getFlur());
                        if (compareFlur != 0) {
                            return compareFlur;
                        } else {
                            final int compareNummer = compareString(o1.getNummer(), o2.getNummer());
                            if (compareNummer != 0) {
                                return compareNummer;
                            } else {
                                return 0;
                            }
                        }
                    }
                }
            });

        final Comparator<BerechtigungspruefungBescheinigungBaulastInfo> baulastBeanComparator =
            new Comparator<BerechtigungspruefungBescheinigungBaulastInfo>() {

                @Override
                public int compare(final BerechtigungspruefungBescheinigungBaulastInfo o1,
                        final BerechtigungspruefungBescheinigungBaulastInfo o2) {
                    final int compareBlattnummer = compareString(o1.getBlattnummer(), o2.getBlattnummer());
                    if (compareBlattnummer != 0) {
                        return compareBlattnummer;
                    } else {
                        final Integer lfdN1 = (o1 == null) ? -1 : Integer.parseInt((String)o1.getLaufende_nummer());
                        final int lfdN2 = (o2 == null) ? -1 : Integer.parseInt((String)o2.getLaufende_nummer());
                        final int compareLaufendenummer = lfdN1.compareTo(lfdN2);

                        if (compareLaufendenummer != 0) {
                            return compareLaufendenummer;
                        } else {
                            return 0;
                        }
                    }
                }
            };

        Collections.sort(baulastBeguenstigtInfos, baulastBeanComparator);
        Collections.sort(baulastBelastetInfos, baulastBeanComparator);

        return new BerechtigungspruefungBescheinigungGruppeInfo(
                flurstueckeInfo,
                baulastBeguenstigtInfos,
                baulastBelastetInfos);
    }

    /**
     * Creates a new FlurstueckBean object.
     *
     * @param   flurstueck    DOCUMENT ME!
     * @param   grundstuecke  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static BerechtigungspruefungBescheinigungFlurstueckInfo
    createBerechtigungspruefungBescheinigungFlurstueckInfo(final CidsBean flurstueck,
            final Collection<String> grundstuecke) {
        final String alkisId = (String)flurstueck.getProperty("alkis_id");
        final String gemarkung = (String)flurstueck.getProperty("gemarkung");
        final String flur = (String)flurstueck.getProperty("flur");
        final String nenner = (String)flurstueck.getProperty("fstck_nenner");
        final String zaehler = (String)flurstueck.getProperty("fstck_zaehler");

        final String lage;
        final Collection<CidsBean> adressen = flurstueck.getBeanCollectionProperty("adressen");
        if (adressen.isEmpty()) {
            lage = "";
        } else {
            final Set<String> strassen = new HashSet<String>();
            final Map<String, Collection<String>> hausnummernMap = new HashMap<String, Collection<String>>();
            for (final CidsBean adresse : adressen) {
                final String strasse = (String)adresse.getProperty("strasse");
                final String hausnummer = (String)adresse.getProperty("nummer");
                strassen.add(strasse);
                if (hausnummer != null) {
                    if (!hausnummernMap.containsKey(strasse)) {
                        hausnummernMap.put(strasse, new ArrayList<String>());
                    }
                    final List<String> hausnummern = (List)hausnummernMap.get(strasse);
                    hausnummern.add(hausnummer);
                }
            }
            final String strasse = strassen.iterator().next();
            final StringBuffer sb = new StringBuffer(strasse);
            boolean first = true;
            final List<String> hausnummern = (List)hausnummernMap.get(strasse);
            if (hausnummern != null) {
                Collections.sort(hausnummern);
                sb.append(" ");
                for (final String hausnummer : hausnummern) {
                    if (!first) {
                        sb.append(", ");
                    }
                    sb.append(hausnummer);
                    first = false;
                }
            }
            if (strassen.size() > 1) {
                sb.append(" u.a.");
            }
            lage = sb.toString();
        }

        return new BerechtigungspruefungBescheinigungFlurstueckInfo(
                alkisId,
                gemarkung,
                flur,
                zaehler,
                nenner,
                lage,
                grundstuecke);
    }

    /**
     * Creates a new BaulastBean object.
     *
     * @param   baulast  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static BerechtigungspruefungBescheinigungBaulastInfo createBerechtigungspruefungBescheinigungBaulastInfo(
            final CidsBean baulast) {
        final String blattnummer = (String)baulast.getProperty("blattnummer");
        final String laufende_nummer = (String)baulast.getProperty("laufende_nummer");

        final StringBuffer sb = new StringBuffer();
        boolean first = true;
        for (final CidsBean art : baulast.getBeanCollectionProperty("art")) {
            if (!first) {
                sb.append(", ");
            }
            first = false;
            sb.append(art.getProperty("baulast_art"));
        }
        final String arten = sb.toString();

        return new BerechtigungspruefungBescheinigungBaulastInfo(blattnummer, laufende_nummer, arten);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   s1  DOCUMENT ME!
     * @param   s2  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static int compareString(final String s1, final String s2) {
        if (s1 == null) {
            if (s2 == null) {
                return 0;
            } else {
                return -1;
            }
        } else if (s1.equals(s2)) {
            return 0;
        } else {
            return s1.compareTo(s2);
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    @Getter
    @JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
    )
    public static class AlkisKarteDownloadInfo {

        //~ Instance fields ----------------------------------------------------

        @JsonProperty private final String product;
        @JsonProperty private final String landparcelCode;
        @JsonProperty private final String auftragsnummer;
        @JsonProperty private final String fertigungsvermerk;
        @JsonProperty private final String zusatz;
        @JsonProperty private final String massstab;
        @JsonProperty private final String massstabMin;
        @JsonProperty private final String massstabMax;
        @JsonProperty private final int winkel;
        @JsonProperty private final int x;
        @JsonProperty private final int y;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new AlkisKarteDownloadInfo object.
         *
         * @param  product            DOCUMENT ME!
         * @param  landparcelCode     DOCUMENT ME!
         * @param  auftragsnummer     DOCUMENT ME!
         * @param  fertigungsvermerk  DOCUMENT ME!
         * @param  zusatz             DOCUMENT ME!
         * @param  massstab           DOCUMENT ME!
         * @param  massstabMin        DOCUMENT ME!
         * @param  massstabMax        DOCUMENT ME!
         * @param  winkel             DOCUMENT ME!
         * @param  x                  DOCUMENT ME!
         * @param  y                  DOCUMENT ME!
         */
        public AlkisKarteDownloadInfo(@JsonProperty("product") final String product,
                @JsonProperty("landparcelCode") final String landparcelCode,
                @JsonProperty("auftragsnummer") final String auftragsnummer,
                @JsonProperty("fertigungsvermerk") final String fertigungsvermerk,
                @JsonProperty("zusatz") final String zusatz,
                @JsonProperty("massstab") final String massstab,
                @JsonProperty("massstabMin") final String massstabMin,
                @JsonProperty("massstabMax") final String massstabMax,
                @JsonProperty("winkel") final int winkel,
                @JsonProperty("x") final int x,
                @JsonProperty("y") final int y) {
            this.product = product;
            this.landparcelCode = landparcelCode;
            this.auftragsnummer = auftragsnummer;
            this.fertigungsvermerk = fertigungsvermerk;
            this.zusatz = zusatz;
            this.massstab = massstab;
            this.massstabMin = massstabMin;
            this.massstabMax = massstabMax;
            this.winkel = winkel;
            this.x = x;
            this.y = y;
        }
    }
}
