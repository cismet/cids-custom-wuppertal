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
package de.cismet.cids.custom.reports.wunda_blau;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.log4j.Logger;

import java.awt.Component;

import java.util.Collection;
import java.util.LinkedList;

import de.cismet.cids.custom.clientutils.ServerResourcesLoaderClient;
import de.cismet.cids.custom.utils.MauernProperties;
import de.cismet.cids.custom.utils.WundaBlauServerResources;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.utils.serverresources.PropertiesServerResource;

import de.cismet.cismap.commons.gui.printing.JasperReportDownload;
import de.cismet.cismap.commons.gui.printing.JasperReportDownload.JasperReportDataSourceGenerator;
import de.cismet.cismap.commons.gui.printing.JasperReportExcelDownload;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class MauernReportGenerator {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(MauernReportGenerator.class);

    private static final MauernProperties PROPERTIES;

    static {
        MauernProperties properties = null;
        try {
            properties = (MauernProperties)ServerResourcesLoaderClient.getInstance()
                        .get((PropertiesServerResource)WundaBlauServerResources.MAUERN_PROPERTIES.getValue(), true);
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
        PROPERTIES = properties;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeans          DOCUMENT ME!
     * @param  parent             DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public static void generateKatasterBlatt(final Collection<CidsBean> cidsBeans,
            final Component parent,
            final ConnectionContext connectionContext) {
        final JasperReportDataSourceGenerator dataSourceGenerator = new JasperReportDataSourceGenerator() {

                @Override
                public JRDataSource generateDataSource() {
                    final Collection<MauernReportBean> reportBeans = new LinkedList<>();
                    for (final CidsBean b : cidsBeans) {
                        reportBeans.add(new MauernReportBean(b, false, PROPERTIES, connectionContext));
                    }
                    boolean ready;
                    do {
                        ready = true;
                        for (final MauernReportBean m : reportBeans) {
                            if (!m.isReadyToProceed()) {
                                ready = false;
                                break;
                            }
                        }
                    } while (!ready);

                    final JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(
                            reportBeans);
                    return dataSource;
                }
            };

        if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(parent)) {
            final String jobname = DownloadManagerDialog.getInstance().getJobName();

            DownloadManager.instance()
                    .add(new JasperReportDownload(
                            "/de/cismet/cids/custom/reports/wunda_blau/mauer-katasterblatt.jasper",
                            dataSourceGenerator,
                            jobname,
                            "Mauer Katasterblatt",
                            "mauern_katasterblatt"));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeans          DOCUMENT ME!
     * @param  parent             DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public static void generateMainInfo(final Collection<CidsBean> cidsBeans,
            final Component parent,
            final ConnectionContext connectionContext) {
        final JasperReportDataSourceGenerator dataSourceGenerator = new JasperReportDataSourceGenerator() {

                @Override
                public JRDataSource generateDataSource() {
                    final Collection<MauernReportBean> reportBeans = new LinkedList<>();
                    for (final CidsBean b : cidsBeans) {
                        reportBeans.add(new MauernReportBean(b, true, PROPERTIES, connectionContext));
                    }
                    boolean ready;
                    do {
                        ready = true;
                        for (final MauernReportBean m : reportBeans) {
                            if (!m.isReadyToProceed()) {
                                ready = false;
                                break;
                            }
                        }
                    } while (!ready);

                    final JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportBeans);
                    return dataSource;
                }
            };

        if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(parent)) {
            final String jobname = DownloadManagerDialog.getInstance().getJobName();

            DownloadManager.instance()
                    .add(new JasperReportExcelDownload(
                            "/de/cismet/cids/custom/reports/wunda_blau/mauer-hauptinfo.jasper",
                            dataSourceGenerator,
                            jobname,
                            "Mauer Hauptinfo",
                            "mauern_hauptinfo"));
        }
    }
}
