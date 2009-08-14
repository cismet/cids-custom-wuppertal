/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objectrenderer.utils;

import de.cismet.cids.dynamics.CidsBean;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JFrame;
import javax.swing.SwingWorker;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.swing.JRViewer;

/**
 *
 * @author srichter
 */
public abstract class AbstractJasperReportPrint {

    public AbstractJasperReportPrint(String reportURL, Collection<CidsBean> beans) {
        if (reportURL == null || beans == null) {
            throw new NullPointerException();
        }
        this.reportURL = reportURL;
        this.beans = beans;

    }

    public AbstractJasperReportPrint(String reportURL, CidsBean bean) {
        if (reportURL == null || bean == null) {
            throw new NullPointerException();
        }
        this.reportURL = reportURL;
        this.beans = new ArrayList<CidsBean>();
        beans.add(bean);
        this.jpw = null;
    }
    protected static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AbstractJasperReportPrint.class);
    private static final ExecutorService WORKER_POOL = Executors.newCachedThreadPool();
//    private final PrintingWaitDialog printingWaitDialog;
    private final Collection<CidsBean> beans;
    private final String reportURL;
    private JasperPrintWorker jpw;
    private boolean beansCollection = true;

    /**
     *
     * @param current
     * @return
     */
    public abstract Map generateReportParam(CidsBean current);

    public void print() {
        if (EventQueue.isDispatchThread()) {
            executePrint();
        } else {
            EventQueue.invokeLater(new Runnable() {

                public void run() {
                    executePrint();
                }
            });
        }
    }

    private final void executePrint() {
        final JasperPrintWorker old = jpw;
        if (old != null && !old.isDone()) {
            old.cancel(true);
        }
        jpw = new JasperPrintWorker();
        WORKER_POOL.execute(jpw);
    }

    /**
     * @return the beansCollection
     */
    public boolean isBeansCollection() {
        return beansCollection;
    }

    /**
     * @param beansCollection the beansCollection to set
     */
    public void setBeansCollection(boolean beansCollection) {
        this.beansCollection = beansCollection;
    }

    final class JasperPrintWorker extends SwingWorker<JasperPrint, Void> {

        public JasperPrintWorker() {
//            printingWaitDialog.setLocationRelativeTo(StaticSwingTools.getParentFrame(component));
//            printingWaitDialog.setVisible(true);
        }

        @Override
        protected JasperPrint doInBackground() throws Exception {
            final JasperReport jasperReport = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream(reportURL));
            JasperPrint jasperPrint = null;
            if (!isBeansCollection()) {
                for (final CidsBean current : beans) {
                    if (isCancelled()) {
                        return null;
                    }
                    final Map params = generateReportParam(current);
                    final JRBeanArrayDataSource beanArray = new JRBeanArrayDataSource(new CidsBean[]{current});
                    if (jasperPrint == null) {
                        jasperPrint = JasperFillManager.fillReport(jasperReport, params, beanArray);
                    } else {
                        jasperPrint.addPage((JRPrintPage) JasperFillManager.fillReport(jasperReport, params, beanArray).getPages().get(0));
                    }

                }
            } else {
                final Map params = generateReportParam(null);
                final JRBeanArrayDataSource beanArray = new JRBeanArrayDataSource(beans.toArray());
                jasperPrint = JasperFillManager.fillReport(jasperReport, params, beanArray);
            }
            return jasperPrint;
        }

        @Override
        protected void done() {
            try {
                final JasperPrint jp = get();
                if (jp != null && !isCancelled()) {
                    final JRViewer aViewer = new JRViewer(jp);
                    aViewer.setZoomRatio(0.35f);
                    setupPrintFrame(aViewer);
                }
            } catch (InterruptedException ex) {
                log.warn(ex, ex);
            } catch (ExecutionException ex) {
                log.error(ex, ex);
            } finally {
//                printingWaitDialog.setVisible(false);
            }
        }

        private final void setupPrintFrame(final JRViewer aViewer) {
            JFrame aFrame = new JFrame("Druckvorschau");
            aFrame.getContentPane().add(aViewer);
            java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            aFrame.setSize(screenSize.width / 2, screenSize.height / 2);
            java.awt.Insets insets = aFrame.getInsets();
            aFrame.setSize(aFrame.getWidth() + insets.left + insets.right, aFrame.getHeight() + insets.top + insets.bottom + 20);
            aFrame.setLocation((screenSize.width - aFrame.getWidth()) / 2, (screenSize.height - aFrame.getHeight()) / 2);
            aFrame.setVisible(true);
        }
    }
}
