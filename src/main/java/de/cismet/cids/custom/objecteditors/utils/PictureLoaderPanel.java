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
package de.cismet.cids.custom.objecteditors.utils;

import org.apache.log4j.Logger;

import java.awt.image.BufferedImage;

import java.net.URL;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JToggleButton;
import javax.swing.ListModel;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.objectrenderer.utils.alkis.ClientAlkisConf;

import de.cismet.cismap.commons.Crs;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.gui.measuring.MeasuringComponent;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import de.cismet.tools.gui.WebAccessMultiPagePictureReader;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class PictureLoaderPanel extends javax.swing.JPanel implements ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(PictureLoaderPanel.class);
    private static final int NO_SELECTION = -1;
    private static final ListModel MODEL_LOAD = new DefaultListModel() {

            {
                add(0, "Wird geladen...");
            }
        };

    private static final ListModel FEHLER_MODEL = new DefaultListModel() {

            {
                add(0, "Lesefehler.");
            }
        };

    //~ Instance fields --------------------------------------------------------

    private final XBoundingBox initialBoundingbox;
    private final Crs crs;

    private PictureSelectWorker currentPictureSelectWorker = null;
    private PictureReaderWorker pictureReaderWorker = null;
    private WebAccessMultiPagePictureReader pictureReader;
    private int currentPage = NO_SELECTION;
    private final ConnectionContext connectionContext;
    private Listener listener;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnHome;
    private javax.swing.JList lstPages;
    private de.cismet.cismap.commons.gui.measuring.MeasuringComponent measuringComponent;
    private javax.swing.JScrollPane scpPages;
    private javax.swing.JToggleButton togPan;
    private javax.swing.JToggleButton togZoom;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form PictureLoaderPanel.
     */
    public PictureLoaderPanel() {
        this(null, ConnectionContext.createDummy());
    }

    /**
     * Creates a new PictureLoaderPanel object.
     *
     * @param  listener           DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public PictureLoaderPanel(final Listener listener, final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        this.listener = listener;

        XBoundingBox initialBoundingbox = null;
        Crs crs = null;
        try {
            initialBoundingbox = new XBoundingBox(
                    2583621.251964098d,
                    5682507.032498134d,
                    2584022.9413952776d,
                    5682742.852810634d,
                    ClientAlkisConf.getInstance().getSrsService(),
                    true);
            crs = new Crs(
                    ClientAlkisConf.getInstance().getSrsService(),
                    ClientAlkisConf.getInstance().getSrsService(),
                    ClientAlkisConf.getInstance().getSrsService(),
                    true,
                    true);

            initComponents();
        } catch (final Throwable ex) {
            LOG.error("could not initialize PictureLoaderPanel");
        }
        this.initialBoundingbox = initialBoundingbox;
        this.crs = crs;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JToggleButton getTogPan() {
        return togPan;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JToggleButton getTogZoom() {
        return togZoom;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JButton getBtnHome() {
        return btnHome;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public MeasuringComponent getMeasuringComponent() {
        return measuringComponent;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * DOCUMENT ME!
     */
    public void dispose() {
        measuringComponent.dispose();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        scpPages = new javax.swing.JScrollPane();
        lstPages = new javax.swing.JList();
        togPan = new javax.swing.JToggleButton();
        togZoom = new javax.swing.JToggleButton();
        btnHome = new javax.swing.JButton();
        measuringComponent = new MeasuringComponent(initialBoundingbox, crs);

        scpPages.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scpPages.setMinimumSize(new java.awt.Dimension(31, 75));
        scpPages.setOpaque(false);
        scpPages.setPreferredSize(new java.awt.Dimension(85, 75));

        lstPages.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstPages.setFixedCellWidth(75);
        lstPages.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

                @Override
                public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
                    lstPagesValueChanged(evt);
                }
            });
        scpPages.setViewportView(lstPages);

        togPan.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/pan.gif")));                       // NOI18N
        togPan.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            togPan,
            org.openide.util.NbBundle.getMessage(PictureLoaderPanel.class, "PictureLoaderPanel.togPan.text_1")); // NOI18N
        togPan.setToolTipText(org.openide.util.NbBundle.getMessage(
                PictureLoaderPanel.class,
                "PictureLoaderPanel.togPan.toolTipText_1"));                                                     // NOI18N
        togPan.setFocusPainted(false);
        togPan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        togPan.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    togPanActionPerformed(evt);
                }
            });

        togZoom.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/zoom.gif")));                       // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            togZoom,
            org.openide.util.NbBundle.getMessage(PictureLoaderPanel.class, "PictureLoaderPanel.togZoom.text_1")); // NOI18N
        togZoom.setToolTipText(org.openide.util.NbBundle.getMessage(
                PictureLoaderPanel.class,
                "PictureLoaderPanel.togZoom.toolTipText_1"));                                                     // NOI18N
        togZoom.setFocusPainted(false);
        togZoom.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        togZoom.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    togZoomActionPerformed(evt);
                }
            });

        btnHome.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/home.gif")));                       // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnHome,
            org.openide.util.NbBundle.getMessage(PictureLoaderPanel.class, "PictureLoaderPanel.btnHome.text_1")); // NOI18N
        btnHome.setToolTipText(org.openide.util.NbBundle.getMessage(
                PictureLoaderPanel.class,
                "PictureLoaderPanel.btnHome.toolTipText_1"));                                                     // NOI18N
        btnHome.setFocusPainted(false);
        btnHome.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnHome.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnHomeActionPerformed(evt);
                }
            });

        final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 400, Short.MAX_VALUE));
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 300, Short.MAX_VALUE));
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstPagesValueChanged(final javax.swing.event.ListSelectionEvent evt) { //GEN-FIRST:event_lstPagesValueChanged
        if (!evt.getValueIsAdjusting()) {
            final Object page = lstPages.getSelectedValue();

            if (page instanceof Integer) {
                loadPage(((Integer)page) - 1);
            }
        }
    } //GEN-LAST:event_lstPagesValueChanged
    /**
     * DOCUMENT ME!
     *
     * @param  page  DOCUMENT ME!
     */
    private void loadPage(final int page) {
        final PictureSelectWorker oldWorkerTest = currentPictureSelectWorker;
        if (oldWorkerTest != null) {
            oldWorkerTest.cancel(true);
        }
        currentPictureSelectWorker = new PictureSelectWorker(page);
        currentPictureSelectWorker.execute();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void togPanActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_togPanActionPerformed
        measuringComponent.actionPan();
    }                                                                          //GEN-LAST:event_togPanActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void togZoomActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_togZoomActionPerformed
        measuringComponent.actionZoom();
    }                                                                           //GEN-LAST:event_togZoomActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnHomeActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnHomeActionPerformed
        measuringComponent.actionOverview();
    }                                                                           //GEN-LAST:event_btnHomeActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  url  DOCUMENT ME!
     */
    public void reloadPictureFromUrl(final URL url) {
        cancelPictureWorkers();
        listener.showMeasureIsLoading();
        pictureReaderWorker = new PictureReaderWorker(url);
        pictureReaderWorker.execute();
    }

    /**
     * DOCUMENT ME!
     */
    private void closeReader() {
        if (pictureReader != null) {
            pictureReader.close();
            pictureReader = null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JList getLstPages() {
        return lstPages;
    }

    /**
     * DOCUMENT ME!
     */
    public void cancelPictureWorkers() {
        lstPages.setModel(new DefaultListModel());
        if (pictureReaderWorker != null) {
            pictureReaderWorker.cancel(true);
        }
        if (currentPictureSelectWorker != null) {
            currentPictureSelectWorker.cancel(true);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  url  DOCUMENT ME!
     */
    public void setUrl(final URL url) {
        cancelPictureWorkers();
        listener.showMeasureIsLoading();
        lstPages.setEnabled(true);
        measuringComponent.removeAllFeatures();
        lstPages.setModel(new DefaultListModel());

        if (url == null) {
            listener.showMeasurePanel();
        } else {
            pictureReaderWorker = new PictureReaderWorker(url);
            pictureReaderWorker.execute();
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void setCurrentPageNull() {
        currentPage = NO_SELECTION;
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    /**
     * DOCUMENT ME!
     */
    public void setLoading() {
        lstPages.setModel(MODEL_LOAD);
//                setCurrentDocumentNull();

        listener.showMeasureIsLoading();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        new PictureLoaderPanel();
    }

    //~ Inner Interfaces -------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public interface Listener {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         */
        void showMeasureIsLoading();
        /**
         * DOCUMENT ME!
         */
        void showMeasurePanel();
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class PictureSelectWorker extends SwingWorker<BufferedImage, Void> {

        //~ Instance fields ----------------------------------------------------

        private final int pageNumber;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new PictureSelectWorker object.
         *
         * @param  pageNumber  DOCUMENT ME!
         */
        public PictureSelectWorker(final int pageNumber) {
            this.pageNumber = pageNumber;
            setCurrentPageNull();
            measuringComponent.reset();
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected BufferedImage doInBackground() throws Exception {
            if (pictureReader != null) {
                return pictureReader.loadPage(pageNumber);
            }

            throw new IllegalStateException("PictureReader is null!");
        }

        @Override
        protected void done() {
            try {
                if (!isCancelled()) {
                    currentPage = pageNumber;
                    measuringComponent.addImage(get());
                    togPan.setSelected(true);
                    measuringComponent.zoomToFeatureCollection();
                }
            } catch (final InterruptedException ex) {
                setCurrentPageNull();
                measuringComponent.reset();
                lstPages.setModel(new DefaultListModel());
                LOG.warn("Was interrupted while setting new image.", ex);
            } catch (final Exception ex) {
                setCurrentPageNull();
                measuringComponent.reset();
                lstPages.setModel(FEHLER_MODEL);
                LOG.error("Could not set new image.", ex);
            } finally {
                if (isCancelled()) {
                    measuringComponent.reset();
                }
                listener.showMeasurePanel();
                currentPictureSelectWorker = null;
            }
        }
    }
    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class PictureReaderWorker extends SwingWorker<ListModel, Void> {

        //~ Instance fields ----------------------------------------------------

        private final URL url;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new PictureReaderWorker object.
         *
         * @param  url  DOCUMENT ME!
         */
        public PictureReaderWorker(final URL url) {
            this.url = url;

            if (LOG.isDebugEnabled()) {
                LOG.debug("Preparing picture reader for file " + this.url.toExternalForm());
            }

            lstPages.setModel(MODEL_LOAD);
            measuringComponent.removeAllFeatures();
            listener.showMeasureIsLoading();
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected ListModel doInBackground() throws Exception {
            final DefaultListModel model = new DefaultListModel();

            closeReader();

            try {
                pictureReader = new WebAccessMultiPagePictureReader(url, false, true);
            } catch (final Exception e) {
                LOG.error("Could not create a MultiPagePictureReader for URL '" + url.toExternalForm() + "'.", e);
                return model;
            }

            final int numberOfPages = pictureReader.getNumberOfPages();

            for (int i = 0; i < numberOfPages; ++i) {
                model.addElement(i + 1);
            }

            return model;
        }

        @Override
        protected void done() {
            try {
                final ListModel model = get();
                lstPages.setModel(model);

                if (!isCancelled()) {
                    if (model.getSize() > 0) {
                        lstPages.setSelectedIndex(0);
                    } else {
                        lstPages.setModel(new DefaultListModel());
                    }
                }
            } catch (final Exception ex) {
                LOG.error("Could not read found pictures.", ex);
                lstPages.setModel(new DefaultListModel());
            }
        }
    }
}
