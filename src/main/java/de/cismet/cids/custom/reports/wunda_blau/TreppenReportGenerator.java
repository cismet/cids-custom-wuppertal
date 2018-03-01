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

import java.awt.Component;

import java.util.Collection;
import java.util.LinkedList;

import de.cismet.cids.custom.objecteditors.wunda_blau.TreppeEditor;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.connectioncontext.ClientConnectionContext;

import de.cismet.cismap.commons.gui.printing.JasperReportDownload;
import de.cismet.cismap.commons.gui.printing.JasperReportDownload.JasperReportDataSourceGenerator;
import de.cismet.cismap.commons.gui.printing.JasperReportExcelDownload;

import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class TreppenReportGenerator {

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
            final ClientConnectionContext connectionContext) {
        final JasperReportDataSourceGenerator dataSourceGenerator = new JasperReportDataSourceGenerator() {

                @Override
                public JRDataSource generateDataSource() {
                    final Collection<TreppenReportBean> reportBeans = new LinkedList<>();
                    for (final CidsBean b : cidsBeans) {
                        reportBeans.add(new TreppenReportBean(b, (TreppeEditor)parent, connectionContext));
                    }
                    boolean ready;
                    do {
                        ready = true;
                        for (final TreppenReportBean m : reportBeans) {
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
                            "/de/cismet/cids/custom/reports/wunda_blau/treppe-katasterblatt.jasper",
                            dataSourceGenerator,
                            jobname,
                            "Treppe Katasterblatt",
                            "treppen_katasterblatt"));
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
            final ClientConnectionContext connectionContext) {
        final JasperReportDataSourceGenerator dataSourceGenerator = new JasperReportDataSourceGenerator() {

                @Override
                public JRDataSource generateDataSource() {
                    final Collection<TreppenReportBean> reportBeans = new LinkedList<>();
                    for (final CidsBean b : cidsBeans) {
                        reportBeans.add(new TreppenReportBean(b, (TreppeEditor)parent, connectionContext));
                    }
                    boolean ready;
                    do {
                        ready = true;
                        for (final TreppenReportBean m : reportBeans) {
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
                            "/de/cismet/cids/custom/reports/wunda_blau/treppe-hauptinfo.jasper",
                            dataSourceGenerator,
                            jobname,
                            "Treppe Hauptinfo",
                            "treppen_hauptinfo"));
        }
    }
}
