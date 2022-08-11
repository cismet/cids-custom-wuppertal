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
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.MetaObjectNode;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.FileWriter;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import de.cismet.cids.custom.clientutils.ByteArrayActionDownload;
import de.cismet.cids.custom.clientutils.CachedInfoBaulastRetriever;
import de.cismet.cids.custom.clientutils.ClientBaulastBescheinigungHelper;
import de.cismet.cids.custom.objectrenderer.utils.WebAccessBaulastenPictureFinder;
import de.cismet.cids.custom.objectrenderer.utils.alkis.AlkisUtils;
import de.cismet.cids.custom.utils.alkis.BaulastenReportGenerator;
import de.cismet.cids.custom.utils.berechtigungspruefung.baulastbescheinigung.BerechtigungspruefungBescheinigungBaulastInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.baulastbescheinigung.BerechtigungspruefungBescheinigungDownloadInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.baulastbescheinigung.BerechtigungspruefungBescheinigungFlurstueckInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.baulastbescheinigung.BerechtigungspruefungBescheinigungGruppeInfo;
import de.cismet.cids.custom.wunda_blau.search.actions.BaulastBescheinigungReportServerAction;
import de.cismet.cids.custom.wunda_blau.search.actions.BaulastenReportServerAction;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.tools.gui.downloadmanager.AbstractDownload;
import de.cismet.tools.gui.downloadmanager.BackgroundTaskMultipleDownload;
import de.cismet.tools.gui.downloadmanager.Download;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;
import de.cismet.tools.gui.downloadmanager.HttpDownload;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class BaulastenReportDownloadHelper {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            BaulastenReportDownloadHelper.class);

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   bescheinigungsGruppeInfo  DOCUMENT ME!
     * @param   jobName                   DOCUMENT ME!
     * @param   auftragsNummer            DOCUMENT ME!
     * @param   projectName               DOCUMENT ME!
     * @param   fertigungsVermerk         DOCUMENT ME!
     * @param   fabricationdate           DOCUMENT ME!
     * @param   number                    projectname DOCUMENT ME!
     * @param   max                       DOCUMENT ME!
     * @param   connectionContext         DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static Download createBescheinigungReportDownload(
            final BerechtigungspruefungBescheinigungGruppeInfo bescheinigungsGruppeInfo,
            final String jobName,
            final String auftragsNummer,
            final String projectName,
            final String fertigungsVermerk,
            final Date fabricationdate,
            final int number,
            final int max,
            final ConnectionContext connectionContext) throws Exception {
        final Collection<BerechtigungspruefungBescheinigungFlurstueckInfo> fls =
            bescheinigungsGruppeInfo.getFlurstuecke();
        final boolean ua = (fls.size() > 1);
        final String title = "Bescheinigung " + fls.iterator().next().getAlkisId() + (ua ? " (ua)" : "")
                    + " " + number + "/" + max;
        final String fileName = "bescheinigung_" + fls.iterator().next().getAlkisId().replace("/", "--")
                    + (ua ? ".ua" : "")
                    + "_" + number;

        final ServerActionParameter[] saps = new ServerActionParameter[] {
                new ServerActionParameter<>(
                    BaulastBescheinigungReportServerAction.Parameter.BESCHEINIGUNGGRUPPE_INFO.toString(),
                    new ObjectMapper().writeValueAsString(bescheinigungsGruppeInfo)),
                new ServerActionParameter<>(
                    BaulastBescheinigungReportServerAction.Parameter.FABRICATION_DATE.toString(),
                    fabricationdate.getTime()),
                new ServerActionParameter<>(
                    BaulastBescheinigungReportServerAction.Parameter.FERTIGUNGS_VERMERK.toString(),
                    fertigungsVermerk),
                new ServerActionParameter<>(
                    BaulastBescheinigungReportServerAction.Parameter.JOB_NUMBER.toString(),
                    auftragsNummer),
                new ServerActionParameter<>(
                    BaulastBescheinigungReportServerAction.Parameter.PROJECT_NAME.toString(),
                    projectName),
            };

        return new ByteArrayActionDownload(
                BaulastBescheinigungReportServerAction.TASK_NAME,
                null,
                saps,
                title,
                jobName,
                fileName,
                ".pdf",
                connectionContext);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   jobname            DOCUMENT ME!
     * @param   baulasten          DOCUMENT ME!
     * @param   projectname        DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static Collection<? extends Download> createAdditionalFilesDownloads(
            final String jobname,
            final Collection<CidsBean> baulasten,
            final String projectname,
            final ConnectionContext connectionContext) throws Exception {
        final Collection<Download> downloads = new ArrayList<>();
        for (final URL url : WebAccessBaulastenPictureFinder.getInstance().findAdditionalFiles(baulasten)) {
            final String file = url.getFile().substring(url.getFile().lastIndexOf('/') + 1);
            final String filename = file.substring(0, file.lastIndexOf('.'));
            final String extension = file.substring(file.lastIndexOf('.'));
            final String title = jobname;
            downloads.add(new HttpDownload(
                    url,
                    null,
                    title,
                    file,
                    filename,
                    extension));
        }
        return downloads;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   selectedBaulasten  DOCUMENT ME!
     * @param   jobname            DOCUMENT ME!
     * @param   jobnumber          DOCUMENT ME!
     * @param   projectname        DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private static Collection<Download> createTextblattPlanRasterDownloads(final Collection<CidsBean> selectedBaulasten,
            final String jobname,
            final String jobnumber,
            final String projectname,
            final ConnectionContext connectionContext) throws Exception {
        final Collection<Download> downloads = new ArrayList<>();
        downloads.add(createReportDownload(
                BaulastenReportGenerator.Type.TEXTBLATT_PLAN_RASTER,
                selectedBaulasten,
                jobname,
                jobnumber,
                projectname,
                "Bericht aus dem Baulastenverzeichnis",
                connectionContext));
        downloads.addAll(createAdditionalFilesDownloads(
                jobname,
                selectedBaulasten,
                projectname,
                connectionContext));
        return downloads;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   type               DOCUMENT ME!
     * @param   selectedBaulasten  DOCUMENT ME!
     * @param   jobnumber          DOCUMENT ME!
     * @param   projectname        DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static Download createDownload(final BaulastenReportGenerator.Type type,
            final Collection<CidsBean> selectedBaulasten,
            final String jobnumber,
            final String projectname,
            final ConnectionContext connectionContext) throws Exception {
        final String jobname = DownloadManagerDialog.getJobname();
        if (BaulastenReportGenerator.Type.TEXTBLATT_PLAN_RASTER.equals(type)) {
            final BackgroundTaskMultipleDownload.FetchDownloadsTask fetchDownloadsTask =
                new BackgroundTaskMultipleDownload.FetchDownloadsTask() {

                    @Override
                    public Collection<? extends Download> fetchDownloads() throws Exception {
                        return createTextblattPlanRasterDownloads(
                                selectedBaulasten,
                                jobname,
                                jobnumber,
                                projectname,
                                connectionContext);
                    }
                };
            return new BackgroundTaskMultipleDownload(null, jobname, fetchDownloadsTask);
        } else {
            final String title = projectname;
            return createReportDownload(
                    type,
                    selectedBaulasten,
                    jobname,
                    jobnumber,
                    projectname,
                    title,
                    connectionContext);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   type               DOCUMENT ME!
     * @param   selectedBaulasten  DOCUMENT ME!
     * @param   jobName            DOCUMENT ME!
     * @param   jobNumber          DOCUMENT ME!
     * @param   projectName        DOCUMENT ME!
     * @param   title              DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static Download createReportDownload(final BaulastenReportGenerator.Type type,
            final Collection<CidsBean> selectedBaulasten,
            final String jobName,
            final String jobNumber,
            final String projectName,
            final String title,
            final ConnectionContext connectionContext) throws Exception {
        final Collection<MetaObjectNode> mons = new ArrayList<>();
        for (final CidsBean baulastBean : selectedBaulasten) {
            mons.add(new MetaObjectNode(baulastBean));
        }

        final ServerActionParameter[] saps = new ServerActionParameter[] {
                new ServerActionParameter<>(
                    BaulastenReportServerAction.Parameter.BAULASTEN_MONS.toString(),
                    mons),
                new ServerActionParameter<>(
                    BaulastenReportServerAction.Parameter.FERTIGUNGS_VERMERK.toString(),
                    AlkisUtils.createBaulastenFertigungsVermerk(
                        SessionManager.getSession().getUser(),
                        connectionContext)),
                new ServerActionParameter<>(
                    BaulastenReportServerAction.Parameter.JOB_NUMBER.toString(),
                    jobNumber),
                new ServerActionParameter<>(
                    BaulastenReportServerAction.Parameter.PROJECT_NAME.toString(),
                    projectName),
                new ServerActionParameter<>(
                    BaulastenReportServerAction.Parameter.TYPE.toString(),
                    type),
            };

        return new ByteArrayActionDownload(
                BaulastenReportServerAction.TASK_NAME,
                null,
                saps,
                title,
                jobName,
                "baulasten",
                ".pdf",
                connectionContext);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   protocol  DOCUMENT ME!
     * @param   jobName   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Download createBescheinigungsProtokollDownload(final String protocol, final String jobName) {
        return new TxtDownload(
                protocol,
                jobName,
                "Baulastbescheinigung-Protokoll",
                "baulastbescheinigung_protokoll",
                ".txt");
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
    public static Download createFullBescheinigungDownload(
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
                        downloads.add(createBescheinigungsProtokollDownload(downloadInfo.getProtokoll(), jobname));

                        if (downloadInfo.getBescheinigungsInfo() != null) {
                            final Set<CidsBean> allBaulasten = new HashSet<>();

                            // Download: Berichte für alle Bescheinigungsgruppen
                            int number = 0;
                            final int max = downloadInfo.getBescheinigungsInfo().getBescheinigungsgruppen().size();

                            for (final BerechtigungspruefungBescheinigungGruppeInfo bescheinigungsGruppe
                                        : ClientBaulastBescheinigungHelper.getSortedBescheinigungsGruppen(
                                            downloadInfo.getBescheinigungsInfo().getBescheinigungsgruppen())) {
                                downloads.add(createBescheinigungReportDownload(
                                        bescheinigungsGruppe,
                                        (jobname != null) ? jobname : downloadInfo.getAuftragsnummer(),
                                        downloadInfo.getAuftragsnummer(),
                                        downloadInfo.getProduktbezeichnung(),
                                        downloadInfo.getFertigungsVermerk(),
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
                                downloads.addAll(createTextblattPlanRasterDownloads(
                                        allBaulasten,
                                        jobname,
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
    private static class TxtDownload extends AbstractDownload {

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
            } catch (final Exception ex) {
                error(ex);
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (final Exception e) {
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
