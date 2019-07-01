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
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.MetaObjectNode;
import Sirius.server.newuser.User;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import de.cismet.cids.custom.objectrenderer.utils.WebAccessBaulastenPictureFinder;
import de.cismet.cids.custom.objectrenderer.utils.billing.BillingPopup;
import de.cismet.cids.custom.utils.ByteArrayActionDownload;
import de.cismet.cids.custom.utils.alkis.BaulastenReportGenerator;
import de.cismet.cids.custom.wunda_blau.search.actions.BaulastenReportServerAction;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.connectioncontext.ConnectionContext;

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
     * @param   user               DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    public static String createFertigungsVermerk(final User user, final ConnectionContext connectionContext)
            throws ConnectionException {
        final String fertigungsVermerk = SessionManager.getConnection()
                    .getConfigAttr(user, "custom.baulasten.fertigungsVermerk@WUNDA_BLAU", connectionContext);
        if (fertigungsVermerk != null) {
            return fertigungsVermerk;
        } else {
            final CidsBean billingLogin = (CidsBean)BillingPopup.getInstance().getExternalUser(user);
            if (billingLogin != null) {
                final CidsBean billingKunde = (CidsBean)billingLogin.getProperty("kunde");
                if (billingKunde != null) {
                    return (String)billingKunde.getProperty("name");
                }
            }
            return "";
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   jobname            DOCUMENT ME!
     * @param   selectedBaulasten  DOCUMENT ME!
     * @param   jobnumber          DOCUMENT ME!
     * @param   projectname        DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static Collection<? extends Download> generateRasterDownloads(
            final String jobname,
            final Collection<CidsBean> selectedBaulasten,
            final String jobnumber,
            final String projectname,
            final ConnectionContext connectionContext) throws Exception {
        final Collection<Download> downloads = new ArrayList<>();
        downloads.add(createJasperDownload(
                BaulastenReportGenerator.Type.TEXTBLATT_PLAN_RASTER,
                selectedBaulasten,
                jobname,
                jobnumber,
                projectname,
                "Bericht aus dem Baulastenverzeichnis",
                connectionContext));

        final Collection<String> additionalFilesToDownload = new HashSet<>();
        for (final CidsBean selectedBaulast : selectedBaulasten) {
            final List<String> documentListRasterdaten = WebAccessBaulastenPictureFinder.getInstance()
                        .findPlanPicture(
                            selectedBaulast);
            additionalFilesToDownload.addAll(documentListRasterdaten);
        }

        for (final String additionalFileToDownload : additionalFilesToDownload) {
            final URL url = WebAccessBaulastenPictureFinder.getInstance().getUrlForDocument(additionalFileToDownload);
            final String file = url.getFile().substring(url.getFile().lastIndexOf('/') + 1);
            final String filename = file.substring(0, file.lastIndexOf('.'));
            final String extension = file.substring(file.lastIndexOf('.'));

            downloads.add(new HttpDownload(
                    url,
                    null,
                    jobname,
                    file,
                    filename,
                    extension));
        }
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
    public static Download generateDownload(final BaulastenReportGenerator.Type type,
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
                        return generateRasterDownloads(
                                jobname,
                                selectedBaulasten,
                                jobnumber,
                                projectname,
                                connectionContext);
                    }
                };
            return new BackgroundTaskMultipleDownload(null, jobname, fetchDownloadsTask);
        } else {
            return createJasperDownload(
                    type,
                    selectedBaulasten,
                    jobname,
                    jobnumber,
                    projectname,
                    projectname,
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
    private static Download createJasperDownload(final BaulastenReportGenerator.Type type,
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
                    BaulastenReportDownloadHelper.createFertigungsVermerk(
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
                jobName,
                title,
                "baulasten",
                ".pdf",
                connectionContext);

    }
}
