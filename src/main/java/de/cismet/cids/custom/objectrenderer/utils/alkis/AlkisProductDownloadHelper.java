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

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.swing.JOptionPane;

import de.cismet.cids.custom.clientutils.ByteArrayActionDownload;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.objectrenderer.wunda_blau.BaulastenReportDownloadHelper;
import de.cismet.cids.custom.utils.alkis.AlkisProducts;
import de.cismet.cids.custom.utils.berechtigungspruefung.baulastbescheinigung.BerechtigungspruefungBescheinigungDownloadInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.katasterauszug.BerechtigungspruefungAlkisDownloadInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.katasterauszug.BerechtigungspruefungAlkisEinzelnachweisDownloadInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.katasterauszug.BerechtigungspruefungAlkisKarteDownloadInfo;
import de.cismet.cids.custom.wunda_blau.search.actions.AlkisProductServerAction;

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
                final String fertigungsVermerk = AlkisUtils.getFertigungsVermerk("WV ein", connectionContext);
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

                final String fertigungsVermerk = AlkisUtils.getFertigungsVermerk("WV ein", connectionContext);
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
                    final String fertigungsVermerk = AlkisUtils.getFertigungsVermerk("WV ein", connectionContext);
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
                    final String fertigungsVermerk = AlkisUtils.getFertigungsVermerk("WV ein", connectionContext);

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
            final Download download = BaulastenReportDownloadHelper.createFullBescheinigungDownload(
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
        final AlkisKarteDownloadInfoCreator creator = new AlkisKarteDownloadInfoCreator() {

                @Override
                public AlkisKarteDownloadInfo createInfo() {
                    return info;
                }
            };

        downloadKarteCustomProduct(creator, jobName, moreFlurstueckeSuffix, connectionContext);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  infoCreator            DOCUMENT ME!
     * @param  jobName                DOCUMENT ME!
     * @param  moreFlurstueckeSuffix  DOCUMENT ME!
     * @param  connectionContext      DOCUMENT ME!
     */
    public static void downloadKarteCustomProduct(final AlkisKarteDownloadInfoCreator infoCreator,
            final String jobName,
            final boolean moreFlurstueckeSuffix,
            final ConnectionContext connectionContext) {
        DownloadManager.instance().add(new AlkisKarteDownload(moreFlurstueckeSuffix, infoCreator, connectionContext));
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
                        .hasConfigAttr(SessionManager.getSession().getUser(),
                            "navigator.alkis.print@WUNDA_BLAU",
                            connectionContext);
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
                        .hasConfigAttr(SessionManager.getSession().getUser(),
                            "csa://alkisProduct@WUNDA_BLAU",
                            connectionContext);
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
                        .hasConfigAttr(SessionManager.getSession().getUser(),
                            AlkisProducts.ALKIS_EIGENTUEMER,
                            connectionContext);
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
                        .hasConfigAttr(SessionManager.getSession().getUser(),
                            AlkisProducts.ALKIS_HTML_PRODUCTS_ENABLED,
                            connectionContext);
        } catch (ConnectionException ex) {
            LOG.error("Could not validate action tag for Alkis HTML Products!", ex);
        }
        return false;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public abstract static class AlkisKarteDownloadInfoCreator {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         *
         * @throws  Exception  DOCUMENT ME!
         */
        public abstract AlkisKarteDownloadInfo createInfo() throws Exception;
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static class AlkisKarteDownload extends ByteArrayActionDownload {

        //~ Instance fields ----------------------------------------------------

        private final AlkisKarteDownloadInfoCreator infoCreator;
        private final boolean moreFlurstueckeSuffix;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new DingensDownload object.
         *
         * @param  moreFlurstueckeSuffix  DOCUMENT ME!
         * @param  infoCreator            DOCUMENT ME!
         * @param  connectionContext      DOCUMENT ME!
         */
        public AlkisKarteDownload(final boolean moreFlurstueckeSuffix,
                final AlkisKarteDownloadInfoCreator infoCreator,
                final ConnectionContext connectionContext) {
            super("WUNDA_BLAU", connectionContext);
            this.moreFlurstueckeSuffix = moreFlurstueckeSuffix;
            this.infoCreator = infoCreator;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected byte[] execAction() throws Exception {
            this.title = "ALKIS-Druck";
            this.directory = DownloadManagerDialog.getInstance().getJobName();

            final AlkisKarteDownloadInfo info = infoCreator.createInfo();

            if (status == State.ABORTED) {
                return null;
            } else {
                final String filename = info.getProduct()
                            + ((info.getLandparcelCode() != null)
                                ? ("." + info.getLandparcelCode().replace("/", "--")
                                    + (moreFlurstueckeSuffix ? ".ua" : "")) : "");
                determineDestinationFile(filename, ".pdf");

                this.taskname = AlkisProductServerAction.TASK_NAME;
                this.body = AlkisProductServerAction.Body.KARTE_CUSTOM;

                this.params = new ServerActionParameter[] {
                        new ServerActionParameter(AlkisProductServerAction.Parameter.PRODUKT.toString(),
                            info.getProduct()),
                        new ServerActionParameter(
                            AlkisProductServerAction.Parameter.ALKIS_CODE.toString(),
                            info.getLandparcelCode()),
                        new ServerActionParameter(AlkisProductServerAction.Parameter.WINKEL.toString(),
                            info.getWinkel()),
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
                        new ServerActionParameter(AlkisProductServerAction.Parameter.ZUSATZ.toString(),
                            info.getZusatz()),
                        new ServerActionParameter(
                            AlkisProductServerAction.Parameter.AUFTRAGSNUMMER.toString(),
                            info.getAuftragsnummer()),
                        new ServerActionParameter(
                            AlkisProductServerAction.Parameter.FERTIGUNGSVERMERK.toString(),
                            info.getFertigungsvermerk())
                    };

                titleChanged();
                stateChanged();
                return super.execAction();
            }
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = (67 * hash) + Objects.hashCode(this.infoCreator);
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final AlkisKarteDownload other = (AlkisKarteDownload)obj;
            if (!Objects.equals(this.infoCreator, other.infoCreator)) {
                return false;
            }
            return true;
        }
    }

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
