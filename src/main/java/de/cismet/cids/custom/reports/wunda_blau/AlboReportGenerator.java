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
package de.cismet.cids.custom.reports.wunda_blau;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.newuser.User;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.awt.Component;

import java.util.Arrays;

import de.cismet.cids.custom.utils.WundaBlauServerResources;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.GetServerResourceServerAction;

import de.cismet.cismap.commons.gui.printing.JasperReportDownload;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class AlboReportGenerator {

    //~ Static fields/initializers ---------------------------------------------

    private static final WundaBlauServerResources FLAECHE_REPORT_SERVER_RESOURCE =
        WundaBlauServerResources.ALBO_FLAECHE_JASPER;
    private static final WundaBlauServerResources VORGANG_REPORT_SERVER_RESOURCE =
        WundaBlauServerResources.ALBO_VORGANG_JASPER;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AlboReportGenerator.class);

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  flaecheBean        DOCUMENT ME!
     * @param  parent             DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public static void startFlaecheReportDownload(final CidsBean flaecheBean,
            final Component parent,
            final ConnectionContext connectionContext) {
        try {
            final JasperReport jasperReport = getReport(FLAECHE_REPORT_SERVER_RESOURCE, connectionContext);

            if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(parent)) {
                final String jobname = DownloadManagerDialog.getInstance().getJobName();

                final JasperReportDownload.JasperReportParametersGenerator parametersGenerator =
                    new AlboReportFlaecheParametersGenerator(flaecheBean, connectionContext);
                final JasperReportDownload.JasperReportDataSourceGenerator dataSourceGenerator =
                    new AlboReportFlaecheDataSourceGenerator(flaecheBean, connectionContext);

                final String erhebungsnummer = (String)flaecheBean.getProperty("erhebungsnummer");
                DownloadManager.instance()
                        .add(new JasperReportDownload(
                                jasperReport,
                                parametersGenerator,
                                dataSourceGenerator,
                                jobname,
                                String.format("Altlastenkataster - Fläche %s", erhebungsnummer),
                                String.format("albo_flaeche_%s", erhebungsnummer)));
            }
        } catch (final Exception ex) {
            LOG.fatal(ex, ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  vorgangBean        DOCUMENT ME!
     * @param  parent             DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public static void startVorgangReportDownload(final CidsBean vorgangBean,
            final Component parent,
            final ConnectionContext connectionContext) {
        try {
            final JasperReport jasperReport = getReport(VORGANG_REPORT_SERVER_RESOURCE, connectionContext);

            if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(parent)) {
                final String jobname = DownloadManagerDialog.getInstance().getJobName();

                final JasperReportDownload.JasperReportParametersGenerator parametersGenerator =
                    new AlboReportVorgangParametersGenerator(vorgangBean, connectionContext);
                final JasperReportDownload.JasperReportDataSourceGenerator dataSourceGenerator =
                    new AlboReportVorgangDataSourceGenerator(vorgangBean, connectionContext);

                final String vorgang = (String)vorgangBean.getProperty("schluessel");
                DownloadManager.instance()
                        .add(new JasperReportDownload(
                                jasperReport,
                                parametersGenerator,
                                dataSourceGenerator,
                                jobname,
                                String.format("Altlastenkataster - Vorgang %s", vorgang),
                                String.format("albo_vorgang_%s", vorgang)));
            }
        } catch (final Exception ex) {
            LOG.fatal(ex, ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   reportResource     DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    private static JasperReport getReport(final WundaBlauServerResources reportResource,
            final ConnectionContext connectionContext) throws ConnectionException {
        final User user = SessionManager.getSession().getUser();
        final JasperReport jasperReport = (JasperReport)SessionManager.getProxy()
                    .executeTask(
                            user,
                            GetServerResourceServerAction.TASK_NAME,
                            "WUNDA_BLAU",
                            reportResource.getValue(),
                            connectionContext);
        return jasperReport;
    }
}
