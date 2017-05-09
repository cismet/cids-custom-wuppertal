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
import Sirius.navigator.ui.ComponentRegistry;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.log4j.Logger;

import java.awt.Component;

import java.net.URL;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.utils.BaulastBescheinigungUtils;
import de.cismet.cids.custom.utils.berechtigungspruefung.baulastbescheinigung.BerechtigungspruefungBescheinigungDownloadInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.katasterauszug.BerechtigungspruefungAlkisDownloadInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.katasterauszug.BerechtigungspruefungAlkisEinzelnachweisDownloadInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.katasterauszug.BerechtigungspruefungAlkisKarteDownloadInfo;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.downloadmanager.CredentialsAwareHttpDownlaod;
import de.cismet.tools.gui.downloadmanager.Download;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;
import de.cismet.tools.gui.downloadmanager.HttpDownload;
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
     * @param  downloadInfo  DOCUMENT ME!
     */
    public static void downloadBuchungsblattnachweisStichtagProduct(
            final BerechtigungspruefungAlkisEinzelnachweisDownloadInfo downloadInfo) {
        final Component parent = ComponentRegistry.getRegistry().getDescriptionPane();
        final String product = downloadInfo.getAlkisProdukt();
        final String actionTag = AlkisUtils.getActionTag(product);
        final String downloadTitle = AlkisUtils.getProductName(product);
        final Date stichtag = downloadInfo.getDate();

        final List<Download> downloads = new LinkedList<Download>();
        for (final String buchungsblattCode : downloadInfo.getAlkisCodes()) {
            final Download d = createBuchungsblattStichtagProductDownload(
                    stichtag,
                    downloadTitle,
                    product,
                    actionTag,
                    buchungsblattCode,
                    parent);
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
     * @param  downloadInfo  DOCUMENT ME!
     */
    public static void downloadBuchungsblattnachweisProduct(
            final BerechtigungspruefungAlkisEinzelnachweisDownloadInfo downloadInfo) {
        final Component parent = ComponentRegistry.getRegistry().getDescriptionPane();
        final String product = downloadInfo.getAlkisProdukt();
        final String actionTag = AlkisUtils.getActionTag(product);
        final String downloadTitle = AlkisUtils.getProductName(product);

        if (!ObjectRendererUtils.checkActionTag(actionTag)) {
            showNoProductPermissionWarning(parent);
            // return;
        }

        String extension = ".pdf";
        if (AlkisUtils.PRODUCTS.GRUNDSTUECKSNACHWEIS_NRW_HTML.equals(product)
                    || AlkisUtils.PRODUCTS.BESTANDSNACHWEIS_KOMMUNAL_HTML.equals(product)
                    || AlkisUtils.PRODUCTS.BESTANDSNACHWEIS_KOMMUNAL_INTERN_HTML.equals(product)
                    || AlkisUtils.PRODUCTS.BESTANDSNACHWEIS_NRW_HTML.equals(product)) {
            extension = ".html";
        }

        if (!DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(parent)) {
            return;
        }
        final String jobname = DownloadManagerDialog.getInstance().getJobName();

        final List<HttpDownload> downloads = new LinkedList<HttpDownload>();

        for (final String buchungsblattCode : downloadInfo.getAlkisCodes()) {
            URL url = null;

            if ((buchungsblattCode == null) || (buchungsblattCode.trim().length() <= 0)) {
                continue;
            }

            final String queryID = AlkisUtils.escapeHtmlSpaces(buchungsblattCode);

            try {
                url = AlkisUtils.PRODUCTS.productEinzelNachweisUrl(
                        queryID,
                        product,
                        SessionManager.getSession().getUser(),
                        null);

                final URL urlFertigungsvermerk = AlkisUtils.PRODUCTS.productEinzelNachweisUrl(
                        queryID,
                        product,
                        SessionManager.getSession().getUser(),
                        AlkisUtils.getFertigungsVermerk("WV ein"));
                final Map<String, String> requestPerUsage = new HashMap<String, String>();
                requestPerUsage.put("WV ein", (urlFertigungsvermerk != null) ? urlFertigungsvermerk.toString() : null);

                if (url != null) {
                    String filename = product + "." + buchungsblattCode.replace("/", "--").trim();
                    filename = filename.replaceAll(" +", "_"); // replace all whitespaces
                    downloads.add(new HttpDownload(url, "", jobname, downloadTitle, filename, extension));
                }
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
            DownloadManager.instance().add(new MultipleDownload(downloads, jobname));
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
     *
     * @return  DOCUMENT ME!
     */
    public static Download createBuchungsblattStichtagProductDownload(final Date stichtag,
            final String downloadTitle,
            final String product,
            final String actionTag,
            final String completeBuchungsblattCode,
            final Component parent) {
        CredentialsAwareHttpDownlaod download = null;
        if (!ObjectRendererUtils.checkActionTag(actionTag)) {
            showNoProductPermissionWarning(parent);
            return null;
        }

        try {
            if (completeBuchungsblattCode.length() > 0) {
                final String queryID = AlkisUtils.escapeHtmlSpaces(completeBuchungsblattCode);
                final URL url = AlkisUtils.PRODUCTS.productEinzelnachweisStichtagsbezogenUrl(
                        queryID,
                        product,
                        stichtag,
                        SessionManager.getSession().getUser());

                if (url != null) {
                    String filename = product + "." + completeBuchungsblattCode.replace("/", "--").trim();
                    filename = filename.replaceAll(" +", "_"); // replace all whitespaces
                    download = new CredentialsAwareHttpDownlaod(
                            url,
                            "",
                            DownloadManagerDialog.getInstance().getJobName(),
                            downloadTitle,
                            filename,
                            ".pdf",
                            "user",
                            "password");
                }
            }
        } catch (Exception ex) {
            ObjectRendererUtils.showExceptionWindowToUser(
                "Fehler beim Aufruf des Produkts: "
                        + product,
                ex,
                parent);
            LOG.error(ex);
            return null;
        }
        return download;
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
     * @param  downloadInfo  DOCUMENT ME!
     */
    public static void downloadEinzelnachweisProduct(
            final BerechtigungspruefungAlkisEinzelnachweisDownloadInfo downloadInfo) {
        final Component parent = ComponentRegistry.getRegistry().getDescriptionPane();
        final String product = downloadInfo.getAlkisProdukt();
        final String actionTag = AlkisUtils.getActionTag(product);
        final String downloadTitle = AlkisUtils.getProductName(product);

        if (!ObjectRendererUtils.checkActionTag(AlkisUtils.getActionTag(product))) {
            showNoProductPermissionWarning(parent);
            return;
        }

        String extension = ".pdf";
        if (AlkisUtils.PRODUCTS.FLURSTUECKSNACHWEIS_HTML.equals(product)
                    || AlkisUtils.PRODUCTS.FLURSTUECKS_UND_EIGENTUMSNACHWEIS_KOMMUNAL_HTML.equals(product)
                    || AlkisUtils.PRODUCTS.FLURSTUECKS_UND_EIGENTUMSNACHWEIS_KOMMUNAL_INTERN_HTML.equals(product)
                    || AlkisUtils.PRODUCTS.FLURSTUECKS_UND_EIGENTUMSNACHWEIS_NRW_HTML.equals(product)) {
            extension = ".html";
        }

        if (!DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(parent)) {
            return;
        }
        final String jobname = DownloadManagerDialog.getInstance().getJobName();

        final List<HttpDownload> downloads = new LinkedList<HttpDownload>();

        for (final String parcelCode : downloadInfo.getAlkisCodes()) {
            final URL url;
            if ((parcelCode != null) && (parcelCode.length() > 0)) {
                try {
                    url = AlkisUtils.PRODUCTS.productEinzelNachweisUrl(
                            parcelCode,
                            product,
                            SessionManager.getSession().getUser(),
                            AlkisUtils.getFertigungsVermerk("WV ein"));

                    if (url != null) {
                        final String filename = product + "." + parcelCode.replace("/", "--");
                        downloads.add(new HttpDownload(url, "", jobname, downloadTitle, filename, extension));
                    }
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
            DownloadManager.instance().add(new MultipleDownload(downloads, jobname));
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
     * @param  downloadInfo  downloadTitle DOCUMENT ME!
     */
    public static void downloadKarteProduct(final BerechtigungspruefungAlkisKarteDownloadInfo downloadInfo) {
        final Component parent = ComponentRegistry.getRegistry().getDescriptionPane();
        final String downloadTitle = "Karte";

        if (!ObjectRendererUtils.checkActionTag(AlkisUtils.PRODUCT_ACTION_TAG_KARTE)) {
            showNoProductPermissionWarning(parent);
            return;
        }

        if (!DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(parent)) {
            return;
        }
        final String jobname = DownloadManagerDialog.getInstance().getJobName();

        final List<HttpDownload> downloads = new LinkedList<HttpDownload>();
        for (final String parcelCode : downloadInfo.getAlkisCodes()) {
            URL url = null;

            if (parcelCode.length() > 0) {
                try {
                    url = AlkisUtils.PRODUCTS.productKarteUrl(parcelCode, AlkisUtils.getFertigungsVermerk("WV ein"));
                } catch (final Exception ex) {
                    ObjectRendererUtils.showExceptionWindowToUser(
                        "Fehler beim Aufruf des Produkts: Kartenprodukt",
                        ex,
                        parent);
                    LOG.error(ex);
                }
            }

            if (url != null) {
                final String filename = "LK.GDBNRW.A.F." + parcelCode.replace("/", "--");
                downloads.add(new HttpDownload(url, "", jobname, downloadTitle, filename, ".pdf"));
            }
        }

        if (downloads.size() > 1) {
            DownloadManager.instance().add(new MultipleDownload(downloads, jobname));
        } else if (downloads.size() == 1) {
            DownloadManager.instance().add(downloads.get(0));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   schluessel    DOCUMENT ME!
     * @param   produkttyp    DOCUMENT ME!
     * @param   downloadInfo  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void download(final String schluessel, final String produkttyp, final String downloadInfo)
            throws Exception {
        if (BerechtigungspruefungBescheinigungDownloadInfo.PRODUKT_TYP.equals(produkttyp)) {
            final BerechtigungspruefungBescheinigungDownloadInfo bescheinigungDownloadInfo =
                new ObjectMapper().readValue(downloadInfo, BerechtigungspruefungBescheinigungDownloadInfo.class);
            BaulastBescheinigungUtils.doDownload(bescheinigungDownloadInfo, schluessel);
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
                            AlkisProductDownloadHelper.downloadEinzelnachweisProduct(einzelnachweisDownloadInfo);
                        }
                        break;
                        case BUCHUNGSBLAETTER: {
                            if (AlkisUtils.PRODUCTS.BESTANDSNACHWEIS_STICHTAGSBEZOGEN_NRW_PDF.equals(
                                            einzelnachweisDownloadInfo.getAlkisProdukt())) {
                                AlkisProductDownloadHelper.downloadBuchungsblattnachweisStichtagProduct(
                                    einzelnachweisDownloadInfo);
                            } else {
                                AlkisProductDownloadHelper.downloadBuchungsblattnachweisProduct(
                                    einzelnachweisDownloadInfo);
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
                            AlkisProductDownloadHelper.downloadKarteProduct(karteDownloadInfo);
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
     * @param   downloadType  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean checkBerechtigungspruefung(final String downloadType) {
        try {
            return (SessionManager.getConnection().hasConfigAttr(
                        SessionManager.getSession().getUser(),
                        "berechtigungspruefung_"
                                + downloadType));
        } catch (final Exception ex) {
            LOG.info("could now check Berechtigungspruefung confattr", ex);
            return false;
        }
    }
}
