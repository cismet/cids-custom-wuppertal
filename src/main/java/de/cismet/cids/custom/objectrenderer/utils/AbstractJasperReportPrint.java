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
package de.cismet.cids.custom.objectrenderer.utils;

import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.swing.JRViewer;

import java.awt.EventQueue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.JFrame;
import javax.swing.SwingWorker;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.tools.CismetThreadPool;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public abstract class AbstractJasperReportPrint {

    //~ Static fields/initializers ---------------------------------------------

    protected static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
            AbstractJasperReportPrint.class);
//    private final PrintingWaitDialog printingWaitDialog;

    //~ Instance fields --------------------------------------------------------

    private final Collection<CidsBean> beans;
    private final String reportURL;
    private JasperPrintWorker jpw;
    private boolean beansCollection = true;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AbstractJasperReportPrint object.
     *
     * @param   reportURL  DOCUMENT ME!
     * @param   beans      DOCUMENT ME!
     *
     * @throws  NullPointerException  DOCUMENT ME!
     */
    public AbstractJasperReportPrint(final String reportURL, final Collection<CidsBean> beans) {
        if ((reportURL == null) || (beans == null)) {
            throw new NullPointerException();
        }
        this.reportURL = reportURL;
        this.beans = beans;
    }

    /**
     * Creates a new AbstractJasperReportPrint object.
     *
     * @param   reportURL  DOCUMENT ME!
     * @param   bean       DOCUMENT ME!
     *
     * @throws  NullPointerException  DOCUMENT ME!
     */
    public AbstractJasperReportPrint(final String reportURL, final CidsBean bean) {
        if ((reportURL == null) || (bean == null)) {
            throw new NullPointerException();
        }
        this.reportURL = reportURL;
        this.beans = new ArrayList<CidsBean>();
        beans.add(bean);
        this.jpw = null;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   current  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public abstract Map generateReportParam(CidsBean current);

    /**
     * DOCUMENT ME!
     *
     * @param   beans  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public abstract Map generateReportParam(Collection<CidsBean> beans);

    /**
     * DOCUMENT ME!
     */
    public void print() {
        if (EventQueue.isDispatchThread()) {
            executePrint();
        } else {
            EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        executePrint();
                    }
                });
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void executePrint() {
        final JasperPrintWorker old = jpw;
        if ((old != null) && !old.isDone()) {
            old.cancel(true);
        }
        jpw = new JasperPrintWorker();
        CismetThreadPool.execute(jpw);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the beansCollection
     */
    public boolean isBeansCollection() {
        return beansCollection;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  beansCollection  the beansCollection to set
     */
    public void setBeansCollection(final boolean beansCollection) {
        this.beansCollection = beansCollection;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    final class JasperPrintWorker extends SwingWorker<JasperPrint, Void> {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new JasperPrintWorker object.
         */
        public JasperPrintWorker() {
//            printingWaitDialog.setLocationRelativeTo(StaticSwingTools.getParentFrame(component));
//            printingWaitDialog.setVisible(true);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected JasperPrint doInBackground() throws Exception {
            final JasperReport jasperReport = (JasperReport)JRLoader.loadObject(getClass().getResourceAsStream(
                        reportURL));
            JasperPrint jasperPrint = null;
            if (isBeansCollection()) {
                final Map params = generateReportParam(beans);
                final JRBeanCollectionDataSource beanArray = new JRBeanCollectionDataSource(beans);
//                final JRBeanArrayDataSource beanArray = new JRBeanArrayDataSource(beans.toArray());
                jasperPrint = JasperFillManager.fillReport(jasperReport, params, beanArray);
            } else {
                for (final CidsBean current : beans) {
                    if (isCancelled()) {
                        return null;
                    }
                    final Map params = generateReportParam(current);
                    final JRBeanArrayDataSource beanArray = new JRBeanArrayDataSource(new CidsBean[] { current });
                    if (jasperPrint == null) {
                        jasperPrint = JasperFillManager.fillReport(jasperReport, params, beanArray);
                    } else {
                        jasperPrint.addPage((JRPrintPage)JasperFillManager.fillReport(jasperReport, params, beanArray)
                                    .getPages().get(0));
                    }
                }
            }
            return jasperPrint;
        }

        @Override
        protected void done() {
            try {
                final JasperPrint jp = get();
                if ((jp != null) && !isCancelled()) {
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

        /**
         * DOCUMENT ME!
         *
         * @param  aViewer  DOCUMENT ME!
         */
        private void setupPrintFrame(final JRViewer aViewer) {
            final JFrame aFrame = new JFrame("Druckvorschau");
            aFrame.getContentPane().add(aViewer);
            final java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            aFrame.setSize(screenSize.width / 2, screenSize.height / 2);
            final java.awt.Insets insets = aFrame.getInsets();
            aFrame.setSize(aFrame.getWidth() + insets.left + insets.right,
                aFrame.getHeight()
                        + insets.top
                        + insets.bottom
                        + 20);
            aFrame.setLocation((screenSize.width - aFrame.getWidth()) / 2,
                (screenSize.height - aFrame.getHeight())
                        / 2);
            aFrame.setVisible(true);
        }
    }
}
