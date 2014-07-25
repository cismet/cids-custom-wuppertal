/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import Sirius.navigator.ui.RequestsFullSizeComponent;

import com.guigarage.jgrid.JGrid;

import org.openide.util.Exceptions;

import java.awt.CardLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.utils.Sb_stadtbildUtils;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanAggregationRenderer;

import de.cismet.tools.gui.FooterComponentProvider;
import de.cismet.tools.gui.TitleComponentProvider;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class Sb_stadtbildserieAggregationRenderer extends javax.swing.JPanel implements RequestsFullSizeComponent,
    CidsBeanAggregationRenderer,
    FooterComponentProvider,
    TitleComponentProvider,
    ListDataListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            Sb_stadtbildserieGridRenderer.class);

    private static final Icon BIN_EMPTY = new javax.swing.ImageIcon(Sb_stadtbildserieAggregationRenderer.class
                    .getResource("/de/cismet/cids/custom/objectrenderer/wunda_blau/bin_empty.png"));
    private static final Icon BIN_FULL = new javax.swing.ImageIcon(Sb_stadtbildserieAggregationRenderer.class
                    .getResource("/de/cismet/cids/custom/objectrenderer/wunda_blau/bin.png"));
    private static final Icon BIN_RECYCLE = new javax.swing.ImageIcon(Sb_stadtbildserieAggregationRenderer.class
                    .getResource("/de/cismet/cids/custom/objectrenderer/wunda_blau/bin_recycle.png"));

    //~ Instance fields --------------------------------------------------------

    private Collection<CidsBean> cidsBeans = null;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBin;
    private javax.swing.JButton btnBinRecycle;
    private javax.swing.JButton btnSwitchToBin;
    private javax.swing.JButton btnSwitchToSerie;
    private com.guigarage.jgrid.JGrid grdBin;
    private com.guigarage.jgrid.JGrid grdStadtbildserien;
    private de.cismet.cids.custom.objectrenderer.wunda_blau.Sb_stadtbildserieAggregationRendererInfoPanel
        infoNotAvailable;
    private de.cismet.cids.custom.objectrenderer.wunda_blau.Sb_stadtbildserieAggregationRendererInfoPanel infoPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblAmounts;
    private javax.swing.JLabel lblSwitchToBin;
    private javax.swing.JLabel lblSwitchToSerie;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel panFooter;
    private javax.swing.JPanel panLeft;
    private javax.swing.JPanel panRight;
    private javax.swing.JPanel panSlideButton;
    private javax.swing.JPanel panTitle;
    private javax.swing.JPanel panTitleString;
    private javax.swing.JPanel pnlInfoPanels;
    private javax.swing.JPanel pnlLeuchtkasten;
    private javax.swing.JPanel pnlSlider;
    private de.cismet.tools.gui.RoundedPanel roundedPanel1;
    private javax.swing.JSlider sldSize;
    private javax.swing.JToggleButton tbtnSlide;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form Sb_stadtbildserieAggregationRenderer.
     */
    public Sb_stadtbildserieAggregationRenderer() {
        initComponents();
        infoNotAvailable.previewImageNotAvailable();
        infoNotAvailable.setEnableTable(false);
        ((PictureSelectionJGrid)grdStadtbildserien).updateInfoPanel();

        sldSize.setValue(grdStadtbildserien.getFixedCellDimension());
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panFooter = new javax.swing.JPanel();
        panLeft = new javax.swing.JPanel();
        lblSwitchToSerie = new javax.swing.JLabel();
        btnSwitchToSerie = new javax.swing.JButton();
        panRight = new javax.swing.JPanel();
        btnSwitchToBin = new javax.swing.JButton();
        lblSwitchToBin = new javax.swing.JLabel();
        panTitle = new javax.swing.JPanel();
        panTitleString = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        panSlideButton = new javax.swing.JPanel();
        tbtnSlide = new javax.swing.JToggleButton();
        roundedPanel1 = new de.cismet.tools.gui.RoundedPanel();
        btnBin = new javax.swing.JButton();
        btnBinRecycle = new javax.swing.JButton();
        lblAmounts = new javax.swing.JLabel();
        pnlLeuchtkasten = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        grdStadtbildserien = new PictureSelectionJGrid();
        jScrollPane2 = new javax.swing.JScrollPane();
        grdBin = new PictureSelectionJGrid();
        pnlSlider = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        sldSize = new javax.swing.JSlider();
        jLabel2 = new javax.swing.JLabel();
        pnlInfoPanels = new javax.swing.JPanel();
        infoPanel = new de.cismet.cids.custom.objectrenderer.wunda_blau.Sb_stadtbildserieAggregationRendererInfoPanel();
        infoNotAvailable =
            new de.cismet.cids.custom.objectrenderer.wunda_blau.Sb_stadtbildserieAggregationRendererInfoPanel();

        panFooter.setOpaque(false);
        panFooter.setLayout(new java.awt.GridBagLayout());

        panLeft.setOpaque(false);

        lblSwitchToSerie.setFont(new java.awt.Font("DejaVu Sans", 1, 14));      // NOI18N
        lblSwitchToSerie.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblSwitchToSerie,
            org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieAggregationRenderer.class,
                "Sb_stadtbildserieAggregationRenderer.lblSwitchToSerie.text")); // NOI18N
        lblSwitchToSerie.setEnabled(false);
        panLeft.add(lblSwitchToSerie);

        btnSwitchToSerie.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/arrow-left.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnSwitchToSerie,
            org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieAggregationRenderer.class,
                "Sb_stadtbildserieAggregationRenderer.btnSwitchToSerie.text"));                             // NOI18N
        btnSwitchToSerie.setBorderPainted(false);
        btnSwitchToSerie.setContentAreaFilled(false);
        btnSwitchToSerie.setEnabled(false);
        btnSwitchToSerie.setFocusPainted(false);
        btnSwitchToSerie.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnSwitchToSerieActionPerformed(evt);
                }
            });
        panLeft.add(btnSwitchToSerie);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        panFooter.add(panLeft, gridBagConstraints);

        panRight.setOpaque(false);

        btnSwitchToBin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/arrow-right.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnSwitchToBin,
            org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieAggregationRenderer.class,
                "Sb_stadtbildserieAggregationRenderer.btnSwitchToBin.text"));                              // NOI18N
        btnSwitchToBin.setBorderPainted(false);
        btnSwitchToBin.setContentAreaFilled(false);
        btnSwitchToBin.setFocusPainted(false);
        btnSwitchToBin.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnSwitchToBinActionPerformed(evt);
                }
            });
        panRight.add(btnSwitchToBin);

        lblSwitchToBin.setFont(new java.awt.Font("DejaVu Sans", 1, 14));      // NOI18N
        lblSwitchToBin.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblSwitchToBin,
            org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieAggregationRenderer.class,
                "Sb_stadtbildserieAggregationRenderer.lblSwitchToBin.text")); // NOI18N
        panRight.add(lblSwitchToBin);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        panFooter.add(panRight, gridBagConstraints);

        panTitle.setOpaque(false);
        panTitle.setLayout(new java.awt.BorderLayout());

        panTitleString.setOpaque(false);
        panTitleString.setLayout(new java.awt.GridBagLayout());

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 18));           // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblTitle,
            org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieAggregationRenderer.class,
                "Sb_stadtbildserieAggregationRenderer.lblTitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panTitleString.add(lblTitle, gridBagConstraints);

        panTitle.add(panTitleString, java.awt.BorderLayout.CENTER);

        panSlideButton.setOpaque(false);
        panSlideButton.setLayout(new java.awt.GridBagLayout());

        tbtnSlide.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objectrenderer/wunda_blau/arrow.png")));     // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            tbtnSlide,
            org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieAggregationRenderer.class,
                "Sb_stadtbildserieAggregationRenderer.tbtnSlide.text"));                                    // NOI18N
        tbtnSlide.setBorderPainted(false);
        tbtnSlide.setContentAreaFilled(false);
        tbtnSlide.setFocusPainted(false);
        tbtnSlide.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objectrenderer/wunda_blau/arrow-180.png"))); // NOI18N
        tbtnSlide.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    tbtnSlideActionPerformed(evt);
                }
            });
        panSlideButton.add(tbtnSlide, new java.awt.GridBagConstraints());

        panTitle.add(panSlideButton, java.awt.BorderLayout.EAST);

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        roundedPanel1.setMinimumSize(new java.awt.Dimension(300, 200));
        roundedPanel1.setPreferredSize(new java.awt.Dimension(300, 200));
        roundedPanel1.setLayout(new java.awt.GridBagLayout());

        btnBin.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objectrenderer/wunda_blau/bin_empty.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnBin,
            org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieAggregationRenderer.class,
                "Sb_stadtbildserieAggregationRenderer.btnBin.text"));                                       // NOI18N
        btnBin.setToolTipText(org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieAggregationRenderer.class,
                "Sb_stadtbildserieAggregationRenderer.btnBin.toolTipText"));                                // NOI18N
        btnBin.setBorderPainted(false);
        btnBin.setContentAreaFilled(false);
        btnBin.setFocusPainted(false);
        btnBin.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnBinActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        roundedPanel1.add(btnBin, gridBagConstraints);
        btnBinRecycle.setVisible(false);

        btnBinRecycle.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objectrenderer/wunda_blau/bin_recycle.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnBinRecycle,
            org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieAggregationRenderer.class,
                "Sb_stadtbildserieAggregationRenderer.btnBinRecycle.text"));                                  // NOI18N
        btnBinRecycle.setToolTipText(org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieAggregationRenderer.class,
                "Sb_stadtbildserieAggregationRenderer.btnBinRecycle.toolTipText"));                           // NOI18N
        btnBinRecycle.setBorderPainted(false);
        btnBinRecycle.setContentAreaFilled(false);
        btnBinRecycle.setFocusPainted(false);
        btnBinRecycle.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnBinRecycleActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        roundedPanel1.add(btnBinRecycle, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblAmounts,
            org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieAggregationRenderer.class,
                "Sb_stadtbildserieAggregationRenderer.lblAmounts.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        roundedPanel1.add(lblAmounts, gridBagConstraints);

        pnlLeuchtkasten.setOpaque(false);
        pnlLeuchtkasten.setLayout(new java.awt.CardLayout());

        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setOpaque(false);

        grdStadtbildserien.setOpaque(false);
        jScrollPane1.setViewportView(grdStadtbildserien);
        grdStadtbildserien.getModel().addListDataListener(this);

        pnlLeuchtkasten.add(jScrollPane1, "SERIEN");
        jScrollPane1.getViewport().setOpaque(false);

        jScrollPane2.setBorder(null);
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setOpaque(false);

        grdBin.setOpaque(false);
        jScrollPane2.setViewportView(grdBin);

        pnlLeuchtkasten.add(jScrollPane2, "BIN");
        jScrollPane2.getViewport().setOpaque(false);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        roundedPanel1.add(pnlLeuchtkasten, gridBagConstraints);

        pnlSlider.setOpaque(false);
        pnlSlider.setLayout(new java.awt.GridBagLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objectrenderer/wunda_blau/image_small.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieAggregationRenderer.class,
                "Sb_stadtbildserieAggregationRenderer.jLabel1.text"));                                        // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        pnlSlider.add(jLabel1, gridBagConstraints);

        sldSize.setMaximum(512);
        sldSize.setMinimum(64);
        sldSize.setMinimumSize(new java.awt.Dimension(100, 16));
        sldSize.setOpaque(false);
        sldSize.addChangeListener(new javax.swing.event.ChangeListener() {

                @Override
                public void stateChanged(final javax.swing.event.ChangeEvent evt) {
                    sldSizeStateChanged(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        pnlSlider.add(sldSize, gridBagConstraints);

        jLabel2.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objectrenderer/wunda_blau/image_big.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel2,
            org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieAggregationRenderer.class,
                "Sb_stadtbildserieAggregationRenderer.jLabel2.text"));                                      // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlSlider.add(jLabel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        roundedPanel1.add(pnlSlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(roundedPanel1, gridBagConstraints);

        pnlInfoPanels.setOpaque(false);
        pnlInfoPanels.setLayout(new java.awt.CardLayout());

        infoPanel.setMinimumSize(new java.awt.Dimension(350, 0));
        infoPanel.setPreferredSize(new java.awt.Dimension(350, 0));
        pnlInfoPanels.add(infoPanel, "INFO");

        infoNotAvailable.setMinimumSize(new java.awt.Dimension(350, 0));
        infoNotAvailable.setPreferredSize(new java.awt.Dimension(350, 0));
        pnlInfoPanels.add(infoNotAvailable, "NO_INFO");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlInfoPanels, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnBinActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnBinActionPerformed
        moveSelectedStadtbildserienToOtherGrid(grdStadtbildserien, grdBin);
    }                                                                          //GEN-LAST:event_btnBinActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnSwitchToSerieActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnSwitchToSerieActionPerformed
        final CardLayout cardLayout = (CardLayout)pnlLeuchtkasten.getLayout();
        cardLayout.show(pnlLeuchtkasten, "SERIEN");
        btnSwitchToBin.setEnabled(true);
        btnSwitchToSerie.setEnabled(false);
        lblSwitchToBin.setEnabled(true);
        lblSwitchToSerie.setEnabled(false);
        ((PictureSelectionJGrid)grdStadtbildserien).updateInfoPanel();

        btnBin.setVisible(true);
        btnBinRecycle.setVisible(false);
    } //GEN-LAST:event_btnSwitchToSerieActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnSwitchToBinActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnSwitchToBinActionPerformed
        final CardLayout cardLayout = (CardLayout)pnlLeuchtkasten.getLayout();
        cardLayout.show(pnlLeuchtkasten, "BIN");
        btnSwitchToBin.setEnabled(false);
        btnSwitchToSerie.setEnabled(true);
        lblSwitchToBin.setEnabled(false);
        lblSwitchToSerie.setEnabled(true);
        ((PictureSelectionJGrid)grdBin).updateInfoPanel();

        btnBin.setVisible(false);
        btnBinRecycle.setVisible(true);
    } //GEN-LAST:event_btnSwitchToBinActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnBinRecycleActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnBinRecycleActionPerformed
        moveSelectedStadtbildserienToOtherGrid(grdBin, grdStadtbildserien);
    }                                                                                 //GEN-LAST:event_btnBinRecycleActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void sldSizeStateChanged(final javax.swing.event.ChangeEvent evt) { //GEN-FIRST:event_sldSizeStateChanged
        grdStadtbildserien.setFixedCellDimension(sldSize.getValue());
        grdStadtbildserien.ensureIndexIsVisible(grdStadtbildserien.getSelectedIndex());
        grdBin.setFixedCellDimension(sldSize.getValue());
        grdBin.ensureIndexIsVisible(grdBin.getSelectedIndex());
    }                                                                           //GEN-LAST:event_sldSizeStateChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void tbtnSlideActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_tbtnSlideActionPerformed
        // hack to properly resize the grids
        grdStadtbildserien.setVisible(false);
        grdBin.setVisible(false);
        pnlInfoPanels.setVisible(!pnlInfoPanels.isVisible());

        SwingUtilities.invokeLater(
            new Runnable() {

                @Override
                public void run() {
                    grdStadtbildserien.setVisible(true);
                    grdBin.setVisible(true);

                    grdStadtbildserien.ensureIndexIsVisible(grdStadtbildserien.getSelectedIndex());
                    grdBin.ensureIndexIsVisible(grdBin.getSelectedIndex());
                }
            });
    } //GEN-LAST:event_tbtnSlideActionPerformed

    @Override
    public void paint(final Graphics g) {
        super.paint(g); // To change body of generated methods, choose Tools | Templates.
    }

    /**
     * DOCUMENT ME!
     *
     * @param  from  DOCUMENT ME!
     * @param  to    DOCUMENT ME!
     */
    private void moveSelectedStadtbildserienToOtherGrid(final JGrid from, final JGrid to) {
        final List<Sb_stadtbildserieGridObject> gridObjectsToRemove = from.getSelectedValuesList();

        for (final Sb_stadtbildserieGridObject gridObject : gridObjectsToRemove) {
            ((DefaultListModel)from.getModel()).removeElement(gridObject);
            ((DefaultListModel)to.getModel()).addElement(gridObject);
            gridObject.setModel((DefaultListModel)to.getModel());
        }
        from.getSelectionModel().clearSelection();
        updateLabels();
    }

    @Override
    public Collection<CidsBean> getCidsBeans() {
        return cidsBeans;
    }

    @Override
    public void setCidsBeans(final Collection<CidsBean> beans) {
        this.cidsBeans = beans;
        if (beans != null) {
            infoPanel.setAggregationRenderer(this);
            final DefaultListModel model = (DefaultListModel)grdStadtbildserien.getModel();
            for (final CidsBean bean : beans) {
                final Sb_stadtbildserieGridObject gridObject = new Sb_stadtbildserieGridObject(model);
                gridObject.setCidsBean(bean);
                model.addElement(gridObject);

                Sb_stadtbildUtils.cacheImagesForStadtbilder(bean.getBeanCollectionProperty("stadtbilder_arr"));
            }
            updateFooterLabels();
            updateAmountsLabel();
            setTitle("");
        }
    }

    @Override
    public void dispose() {
    }

    @Override
    public String getTitle() {
        return "Leuchtkasten";
    }

    @Override
    public void setTitle(String title) {
        title = "Leuchtkasten";
        if ((cidsBeans != null) && !cidsBeans.isEmpty()) {
            final int amountSerien = cidsBeans.size();
            int amountBilder = 0;
            for (final CidsBean stadtbildserie : cidsBeans) {
                amountBilder += stadtbildserie.getBeanCollectionProperty("stadtbilder_arr").size();
            }
            title += ": " + amountBilder + " Stadtbilder in " + amountSerien + " Stadtbildserien gefunden";
        }
        lblTitle.setText(title);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        try {
            final CidsBean[] beans = DevelopmentTools.createCidsBeansFromRMIConnectionOnLocalhost(
                    "WUNDA_BLAU",
                    "Administratoren",
                    "admin",
                    "kif",
                    "sb_stadtbildserie",
                    " id = 5 or id = 6 or id = 285195 or id = 8 or id = 9 or id = 10 or id = 11 or  id = 285198",
                    10);

            DevelopmentTools.createAggregationRendererInFrameFromRMIConnectionOnLocalhost(Arrays.asList(beans),
                "Leuchtkasten",
                1024,
                800);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public JComponent getTitleComponent() {
        return panTitle;
    }

    @Override
    public JComponent getFooterComponent() {
        return panFooter;
    }

    /**
     * DOCUMENT ME!
     */
    private void updateLabels() {
        updateAmountsLabel();
        updateFooterLabels();
    }

    /**
     * DOCUMENT ME!
     */
    private void updateFooterLabels() {
        final String stadtbildserien = "Stadtbildserien (" + grdStadtbildserien.getModel().getSize() + ")";
        lblSwitchToSerie.setText(stadtbildserien);
        final int amountSerienInBin = grdBin.getModel().getSize();
        final String bin = "Papierkorb (" + amountSerienInBin + ")";
        if (amountSerienInBin == 0) {
            btnBin.setIcon(BIN_EMPTY);
        } else {
            btnBin.setIcon(BIN_FULL);
        }
        lblSwitchToBin.setText(bin);
    }

    /**
     * DOCUMENT ME!
     */
    private void updateAmountsLabel() {
        final int selectedStadtbilderAmount = getSelectedStadtbilderAmount();
        final int amountSerien = ((DefaultListModel)grdStadtbildserien.getModel()).getSize();

        lblAmounts.setText(selectedStadtbilderAmount + " aus " + amountSerien + " ausgewählt");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private int getSelectedStadtbilderAmount() {
        return getSelectedStadtbilder().size();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Collection<CidsBean> getSelectedStadtbilder() {
        final Set<CidsBean> selectedStadtbilder = new HashSet<CidsBean>();

        final Enumeration<Sb_stadtbildserieGridObject> e = ((DefaultListModel)grdStadtbildserien.getModel()).elements();
        while (e.hasMoreElements()) {
            final Sb_stadtbildserieGridObject gridObject = (Sb_stadtbildserieGridObject)e.nextElement();

            final Set set = gridObject.getSelectedBildnummernOfSerie();
            selectedStadtbilder.addAll(set);
        }
        return selectedStadtbilder;
    }

    @Override
    public void intervalAdded(final ListDataEvent e) {
// do nothing
    }

    @Override
    public void intervalRemoved(final ListDataEvent e) {
        // do nothing
    }

    @Override
    public void contentsChanged(final ListDataEvent e) {
        updateAmountsLabel();
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class PictureSelectionJGrid extends JGrid {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new PictureSelectionJGrid object.
         */
        public PictureSelectionJGrid() {
            init();
        }

        /**
         * Creates a new PictureSelectionJGrid object.
         *
         * @param   model  DOCUMENT ME!
         *
         * @throws  IllegalArgumentException  DOCUMENT ME!
         */
        public PictureSelectionJGrid(final ListModel model) throws IllegalArgumentException {
            super(model);
            init();
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         */
        private void init() {
            final DefaultListModel<Sb_stadtbildserieGridObject> gridModel =
                new DefaultListModel<Sb_stadtbildserieGridObject>();
            this.setModel(gridModel);
            this.getCellRendererManager()
                    .setDefaultRenderer(
                        new de.cismet.cids.custom.objectrenderer.wunda_blau.Sb_stadtbildserieGridRenderer());

            this.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mouseClicked(final MouseEvent e) {
                        if (e.getClickCount() >= 2) {
                            final List<Sb_stadtbildserieGridObject> selectedSerien = PictureSelectionJGrid.this
                                        .getSelectedValuesList();
                            if (selectedSerien.size() == 1) {
                                final Sb_stadtbildserieGridObject gridObject = selectedSerien.get(0);
                                gridObject.addOrRemoveSelectedBildnummerOfSerie(gridObject.getStadtbildUnderMarker());
                                infoPanel.updateTableModel();
                            }
                        }
                    }
                });

            this.addMouseMotionListener(new MouseAdapter() {

                    int lastIndex = -1;

                    @Override
                    public void mouseMoved(final MouseEvent e) {
                        if ((lastIndex >= 0) && (lastIndex < PictureSelectionJGrid.this.getModel().getSize())) {
                            final Object o = PictureSelectionJGrid.this.getModel().getElementAt(lastIndex);
                            if (o instanceof Sb_stadtbildserieGridObject) {
                                final Rectangle r = PictureSelectionJGrid.this.getCellBounds(lastIndex);
                                if ((r != null) && !r.contains(e.getPoint())) {
                                    // remove the marker once
                                    if (((Sb_stadtbildserieGridObject)o).isMarker()) {
                                        ((Sb_stadtbildserieGridObject)o).setMarker(false);
                                        PictureSelectionJGrid.this.repaint(r);
                                    }
                                }
                            }
                        }

                        final int index = PictureSelectionJGrid.this.getCellAt(e.getPoint());
                        if (index >= 0) {
                            final Object o = PictureSelectionJGrid.this.getModel().getElementAt(index);
                            if (o instanceof Sb_stadtbildserieGridObject) {
                                if (((Sb_stadtbildserieGridObject)o).getAmountImages() > 1) {
                                    final Rectangle r = PictureSelectionJGrid.this.getCellBounds(index);
                                    if (r != null) {
                                        ((Sb_stadtbildserieGridObject)o).setFraction(
                                            ((float)e.getPoint().x - (float)r.x)
                                                    / (float)r.width);
                                        ((Sb_stadtbildserieGridObject)o).setMarker(true);
                                        lastIndex = index;
                                        PictureSelectionJGrid.this.repaint(r);
                                    }
                                }
                            }
                        }
                    }
                });
            this.addListSelectionListener(new ListSelectionListener() {

                    @Override
                    public void valueChanged(final ListSelectionEvent e) {
                        if (!e.getValueIsAdjusting()) {
                            updateInfoPanel();
                        }
                    }
                });
        }

        /**
         * DOCUMENT ME!
         */
        public void updateInfoPanel() {
            int[] indexes = new int[0];
            final ListSelectionModel sm = PictureSelectionJGrid.this.getSelectionModel();
            final int iMin = sm.getMinSelectionIndex();
            final int iMax = sm.getMaxSelectionIndex();

            if ((iMin >= 0) && (iMin == iMax)) {
                indexes = new int[1];
                indexes[0] = iMin;
            }
            final CardLayout cardLayout = (CardLayout)pnlInfoPanels.getLayout();
            if (indexes.length == 1) {
                cardLayout.show(pnlInfoPanels, "INFO");
                final Sb_stadtbildserieGridObject gridObject = (Sb_stadtbildserieGridObject)PictureSelectionJGrid.this
                            .getModel().getElementAt(indexes[0]);
                infoPanel.setGridObject(gridObject);
            } else {
                cardLayout.show(pnlInfoPanels, "NO_INFO");
            }
        }
    }
}
