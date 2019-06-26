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

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.log4j.Logger;

import java.awt.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
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
import de.cismet.cids.custom.objectrenderer.wunda_blau.BaulastenReportGenerator;
import de.cismet.cids.custom.utils.ByteArrayActionDownload;
import de.cismet.cids.custom.utils.CachedInfoBaulastRetriever;
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

import de.cismet.cismap.commons.gui.printing.JasperReportDownload;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.downloadmanager.AbstractDownload;
import de.cismet.tools.gui.downloadmanager.BackgroundTaskMultipleDownload;
import de.cismet.tools.gui.downloadmanager.Download;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;
import de.cismet.tools.gui.downloadmanager.MultipleDownload;

import static de.cismet.cids.custom.objectrenderer.wunda_blau.BaulastenReportGenerator.createFertigungsVermerk;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class AlkisProductDownloadHelper {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(AlkisProductDownloadHelper.class);
    private static final String PARAMETER_JOBNUMBER = "JOBNUMBER";
    private static final String PARAMETER_PROJECTNAME = "PROJECTNAME";
    private static final String PARAMETER_PRUEFKEY = "PRUEFKEY";
    private static final String PARAMETER_HAS_BELASTET = "HAS_BELASTET";
    private static final String PARAMETER_HAS_BEGUENSTIGT = "HAS_BEGUENSTIGT";
    private static final String PARAMETER_FABRICATIONNOTICE = "FABRICATIONNOTICE";
    private static final String PARAMETER_FABRICATIONDATE = "FABRICATIONDATE";

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
     * @param  downloadInfo       DOCUMENT ME!
     * @param  anfrageSchluessel  DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public static void downloadBaulastbescheinigung(final BerechtigungspruefungBescheinigungDownloadInfo downloadInfo,
            final String anfrageSchluessel,
            final ConnectionContext connectionContext) {
        try {
            final Download download = generateBaulastbescheinigungDownload(
                    downloadInfo,
                    anfrageSchluessel,
                    connectionContext);
            if (download != null) {
                DownloadManager.instance().add(download);
            }
        } catch (final Exception ex) {
            LOG.error("error while generating download", ex);
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
            downloadBaulastbescheinigung(bescheinigungDownloadInfo, schluessel, connectionContext);
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
     * DOCUMENT ME!
     *
     * @param   bescheinigungsGruppe  DOCUMENT ME!
     * @param   jobname               DOCUMENT ME!
     * @param   jobnumber             DOCUMENT ME!
     * @param   projectName           DOCUMENT ME!
     * @param   anfrageSchluessel     DOCUMENT ME!
     * @param   fabricationdate       DOCUMENT ME!
     * @param   number                projectname DOCUMENT ME!
     * @param   max                   DOCUMENT ME!
     * @param   connectionContext     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private static Download createBescheinigungPdf(
            final BerechtigungspruefungBescheinigungGruppeInfo bescheinigungsGruppe,
            final String jobname,
            final String jobnumber,
            final String projectName,
            final String anfrageSchluessel,
            final Date fabricationdate,
            final int number,
            final int max,
            final ConnectionContext connectionContext) throws Exception {
        final JasperReportDownload.JasperReportDataSourceGenerator dataSourceGenerator =
            new JasperReportDownload.JasperReportDataSourceGenerator() {

                @Override
                public JRDataSource generateDataSource() {
                    try {
                        final Collection<BerechtigungspruefungBescheinigungGruppeInfo> reportBeans = Arrays.asList(
                                new BerechtigungspruefungBescheinigungGruppeInfo[] { bescheinigungsGruppe });
                        final JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportBeans);
                        return dataSource;
                    } catch (final Exception ex) {
                        LOG.warn(ex, ex);
                        return null;
                    }
                }
            };

        final JasperReportDownload.JasperReportParametersGenerator parametersGenerator =
            new JasperReportDownload.JasperReportParametersGenerator() {

                @Override
                public Map generateParamters() {
                    try {
                        final HashMap parameters = new HashMap();
                        parameters.put(PARAMETER_JOBNUMBER, jobnumber);
                        parameters.put(PARAMETER_PROJECTNAME, projectName);
                        parameters.put(PARAMETER_PRUEFKEY, anfrageSchluessel);

                        parameters.put(PARAMETER_HAS_BELASTET, !bescheinigungsGruppe.getBaulastenBelastet().isEmpty());
                        parameters.put(
                            PARAMETER_FABRICATIONDATE,
                            new SimpleDateFormat("dd.MM.yyyy").format(fabricationdate));
                        parameters.put(
                            PARAMETER_HAS_BEGUENSTIGT,
                            !bescheinigungsGruppe.getBaulastenBeguenstigt().isEmpty());
                        parameters.put(
                            PARAMETER_FABRICATIONNOTICE,
                            createFertigungsVermerk(SessionManager.getSession().getUser(), connectionContext));
                        return parameters;
                    } catch (final Exception ex) {
                        LOG.warn(ex, ex);
                        return null;
                    }
                }
            };

        final Collection<BerechtigungspruefungBescheinigungFlurstueckInfo> fls = bescheinigungsGruppe.getFlurstuecke();
        final boolean ua = (fls.size() > 1);
        final String title = "Bescheinigung " + fls.iterator().next().getAlkisId() + (ua ? " (ua)" : "")
                    + " " + number + "/" + max;
        final String fileName = "bescheinigung_" + fls.iterator().next().getAlkisId().replace("/", "--")
                    + (ua ? ".ua" : "")
                    + "_" + number;

        final JasperReportDownload download = new JasperReportDownload(
                "/de/cismet/cids/custom/wunda_blau/res/baulastbescheinigung.jasper",
                parametersGenerator,
                dataSourceGenerator,
                jobname,
                title,
                fileName);

        return download;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   downloadInfo       DOCUMENT ME!
     * @param   anfrageSchluessel  DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static Download generateBaulastbescheinigungDownload(
            final BerechtigungspruefungBescheinigungDownloadInfo downloadInfo,
            final String anfrageSchluessel,
            final ConnectionContext connectionContext) throws Exception {
        if (
            !DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(
                        ComponentRegistry.getRegistry().getMainWindow())) {
            return null;
        }
        final String jobname = DownloadManagerDialog.getInstance().getJobName();

        final BackgroundTaskMultipleDownload.FetchDownloadsTask fetchDownloadsTask =
            new BackgroundTaskMultipleDownload.FetchDownloadsTask() {

                @Override
                public Collection<? extends Download> fetchDownloads() throws Exception {
                    final Collection<Download> downloads = new ArrayList<>();
                    try {
                        downloads.add(new TxtDownload(
                                downloadInfo.getProtokoll(),
                                jobname,
                                "Baulastbescheinigung-Protokoll",
                                "baulastbescheinigung_protokoll",
                                ".txt"));

                        if (downloadInfo.getBescheinigungsInfo() != null) {
                            final Set<CidsBean> allBaulasten = new HashSet<>();

                            // Download: Berichte für alle Bescheinigungsgruppen
                            int number = 0;
                            final int max = downloadInfo.getBescheinigungsInfo().getBescheinigungsgruppen().size();

                            final List<BerechtigungspruefungBescheinigungGruppeInfo> sortedBescheinigungsGruppen =
                                new ArrayList<>(
                                    downloadInfo.getBescheinigungsInfo().getBescheinigungsgruppen());
                            Collections.sort(
                                sortedBescheinigungsGruppen,
                                new Comparator<BerechtigungspruefungBescheinigungGruppeInfo>() {

                                    @Override
                                    public int compare(final BerechtigungspruefungBescheinigungGruppeInfo o1,
                                            final BerechtigungspruefungBescheinigungGruppeInfo o2) {
                                        final String alkisId1 = o1.getFlurstuecke().iterator().next().getAlkisId();
                                        final String alkisId2 = o2.getFlurstuecke().iterator().next().getAlkisId();
                                        return alkisId1.compareTo(alkisId2);
                                    }
                                });
                            for (final BerechtigungspruefungBescheinigungGruppeInfo bescheinigungsGruppe
                                        : sortedBescheinigungsGruppen) {
                                downloads.add(createBescheinigungPdf(
                                        bescheinigungsGruppe,
                                        (jobname != null) ? jobname : downloadInfo.getAuftragsnummer(),
                                        downloadInfo.getAuftragsnummer(),
                                        downloadInfo.getProduktbezeichnung(),
                                        anfrageSchluessel,
                                        downloadInfo.getBescheinigungsInfo().getDatum(),
                                        ++number,
                                        max,
                                        connectionContext));
                                // alle Baulasten ermitteln
                                for (final BerechtigungspruefungBescheinigungBaulastInfo baulastInfo
                                            : bescheinigungsGruppe.getBaulastenBelastet()) {
                                    allBaulasten.add(CachedInfoBaulastRetriever.getInstance().loadBaulast(
                                            baulastInfo,
                                            connectionContext));
                                }
                                for (final BerechtigungspruefungBescheinigungBaulastInfo baulastInfo
                                            : bescheinigungsGruppe.getBaulastenBeguenstigt()) {
                                    allBaulasten.add(CachedInfoBaulastRetriever.getInstance().loadBaulast(
                                            baulastInfo,
                                            connectionContext));
                                }
                            }

                            if (!allBaulasten.isEmpty()) {
                                // Download: Bericht für alle Baulasten
                                downloads.addAll(BaulastenReportGenerator.generateRasterDownloads(
                                        jobname,
                                        allBaulasten,
                                        downloadInfo.getAuftragsnummer(),
                                        downloadInfo.getProduktbezeichnung(),
                                        connectionContext));
                            }
                        }
                    } catch (final Exception ex) {
                        LOG.fatal(ex, ex);
                    }

                    return downloads;
                }
            };
        return new BackgroundTaskMultipleDownload(null, jobname, fetchDownloadsTask);
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

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    static class TxtDownload extends AbstractDownload {

        //~ Instance fields ----------------------------------------------------

        private final String content;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new TxtDownload object.
         *
         * @param  content    DOCUMENT ME!
         * @param  directory  DOCUMENT ME!
         * @param  title      DOCUMENT ME!
         * @param  filename   DOCUMENT ME!
         * @param  extension  DOCUMENT ME!
         */
        public TxtDownload(
                final String content,
                final String directory,
                final String title,
                final String filename,
                final String extension) {
            this.content = content;
            this.directory = directory;
            this.title = title;

            status = Download.State.WAITING;

            determineDestinationFile(filename, extension);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void run() {
            if (status != Download.State.WAITING) {
                return;
            }

            status = Download.State.RUNNING;

            stateChanged();

            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(fileToSaveTo, false));
                writer.write(content);
            } catch (Exception ex) {
                error(ex);
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (Exception e) {
                        log.warn("Exception occured while closing file.", e);
                    }
                }
            }

            if (status == Download.State.RUNNING) {
                status = Download.State.COMPLETED;
                stateChanged();
            }
        }
    }
}
