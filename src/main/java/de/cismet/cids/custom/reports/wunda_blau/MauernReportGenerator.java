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

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import org.openide.util.NbBundle;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;

import javax.swing.Timer;

import de.cismet.cids.custom.objecteditors.wunda_blau.MauerEditor;
import de.cismet.cids.custom.objectrenderer.wunda_blau.MauerAggregationRenderer;
import de.cismet.cids.custom.objectrenderer.wunda_blau.NivellementPunktAggregationRenderer;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.gui.printing.JasperDownload;

import de.cismet.tools.CismetThreadPool;

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
    private static boolean forceQuit = false;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeans  DOCUMENT ME!
     * @param  parent     DOCUMENT ME!
     */
    public static void generateKatasterBlatt(final Collection<CidsBean> cidsBeans, final Component parent) {
        final Runnable runnable = new Runnable() {

                @Override
                public void run() {
                    final Collection<MauernReportBeanWithMapAndImages> reportBeans =
                        new LinkedList<MauernReportBeanWithMapAndImages>();
                    for (final CidsBean b : cidsBeans) {
                        reportBeans.add(new MauernReportBeanWithMapAndImages(b));
                    }
                    boolean ready = false;

                    final Timer timer = new Timer(5000, new ActionListener() {

                                @Override
                                public void actionPerformed(final ActionEvent e) {
                                    forceQuit = true;
                                }
                            });
                    do {
                        ready = true;
                        for (final MauernReportBeanWithMapAndImages m : reportBeans) {
                            if (!m.isReadyToProceed() || forceQuit) {
                                ready = false;
                                break;
                            }
                        }
                    } while (!ready);

                    final JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportBeans);

                    final HashMap parameters = new HashMap();

                    final JasperReport jasperReport;
                    final JasperPrint jasperPrint;
                    try {
                        jasperReport = (JasperReport)JRLoader.loadObject(getClass().getResourceAsStream(
                                    "/de/cismet/cids/custom/reports/wunda_blau/mauer-katasterblatt.jasper"));
                        jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
                    } catch (JRException ex) {
                        LOG.error("Could not generate katasterblatt report for mauern.", ex);

                        final ErrorInfo ei = new ErrorInfo(NbBundle.getMessage(
                                    MauernReportGenerator.class,
                                    "MauernReportGenerator.jxlKatasterblattActionPerformed(ActionEvent).ErrorInfo.title"),   // NOI18N
                                NbBundle.getMessage(
                                    MauernReportGenerator.class,
                                    "MauernReportGenerator.jxlKatasterblattActionPerformed(ActionEvent).ErrorInfo.message"), // NOI18N
                                null,
                                null,
                                ex,
                                Level.ALL,
                                null);
                        JXErrorPane.showDialog(parent, ei);

                        return;
                    }

                    if (DownloadManagerDialog.showAskingForUserTitle(parent)) {
                        final String jobname = DownloadManagerDialog.getJobname();

                        DownloadManager.instance()
                                .add(new JasperDownload(
                                        jasperPrint,
                                        jobname,
                                        "Mauer Katasterblatt",
                                        "mauern_katasterblatt"));
                    }
                }
            };

        CismetThreadPool.execute(runnable);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeans  DOCUMENT ME!
     * @param  parent     DOCUMENT ME!
     */
    public static void generateMainInfo(final Collection<CidsBean> cidsBeans, final Component parent) {
        final Runnable runnable = new Runnable() {

                @Override
                public void run() {
                    final Collection<MauernReportBean> reportBeans = new LinkedList<MauernReportBean>();
                    for (final CidsBean b : cidsBeans) {
                        reportBeans.add(new MauernReportBeanWithMapAndImages(b));
                    }
                    boolean ready = false;
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

                    final HashMap parameters = new HashMap();

                    final JasperReport jasperReport;
                    final JasperPrint jasperPrint;
                    try {
                        jasperReport = (JasperReport)JRLoader.loadObject(getClass().getResourceAsStream(
                                    "/de/cismet/cids/custom/reports/wunda_blau/mauer-hauptinfo.jasper"));
                        jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
                    } catch (JRException ex) {
                        LOG.error("Could not generate main info report for mauern.", ex);

                        final ErrorInfo ei = new ErrorInfo(NbBundle.getMessage(
                                    MauernReportGenerator.class,
                                    "MauernReportGenerator.jxlKatasterblattActionPerformed(ActionEvent).ErrorInfo.title"),   // NOI18N
                                NbBundle.getMessage(
                                    MauernReportGenerator.class,
                                    "MauernReportGenerator.jxlKatasterblattActionPerformed(ActionEvent).ErrorInfo.message"), // NOI18N
                                null,
                                null,
                                ex,
                                Level.ALL,
                                null);
                        JXErrorPane.showDialog(parent, ei);

                        return;
                    }

                    if (DownloadManagerDialog.showAskingForUserTitle(parent)) {
                        final String jobname = DownloadManagerDialog.getJobname();

                        DownloadManager.instance()
                                .add(new JasperDownload(
                                        jasperPrint,
                                        jobname,
                                        "Mauer Katasterblatt",
                                        "mauern_hauptinfo"));
                    }
                }
            };

        CismetThreadPool.execute(runnable);
    }
}
