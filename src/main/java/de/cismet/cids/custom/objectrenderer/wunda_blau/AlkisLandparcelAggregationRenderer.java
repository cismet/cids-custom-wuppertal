/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * AlkisLandparcelAggregationRenderer.java
 *
 * Created on 07.07.2011, 09:27:21
 */
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import Sirius.navigator.ui.DescriptionPane;
import Sirius.navigator.ui.RequestsFullSizeComponent;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import de.cismet.cids.custom.clientutils.BaulastBescheinigungDialog;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.objectrenderer.utils.alkis.AlkisProductDownloadHelper;
import de.cismet.cids.custom.objectrenderer.utils.alkis.ClientAlkisConf;
import de.cismet.cids.custom.objectrenderer.utils.alkis.ClientAlkisProducts;
import de.cismet.cids.custom.objectrenderer.utils.billing.BillingPopup;
import de.cismet.cids.custom.utils.alkis.AlkisProducts;
import de.cismet.cids.custom.utils.berechtigungspruefung.DownloadInfoFactory;
import de.cismet.cids.custom.utils.berechtigungspruefung.katasterauszug.BerechtigungspruefungAlkisEinzelnachweisDownloadInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.katasterauszug.BerechtigungspruefungAlkisKarteDownloadInfo;
import de.cismet.cids.custom.utils.billing.BillingProductGroupAmount;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanAggregationRenderer;
import de.cismet.cids.tools.metaobjectrenderer.SelfDisposingPanel;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.features.StyledFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.gui.RoundedPanel;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class AlkisLandparcelAggregationRenderer extends javax.swing.JPanel implements CidsBeanAggregationRenderer,
    RequestsFullSizeComponent,
    ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(AlkisLandparcelAggregationRenderer.class);

    private static final Color[] COLORS = new Color[] {
            new Color(247, 150, 70, 192),
            new Color(155, 187, 89, 192),
            new Color(128, 100, 162, 192),
            new Color(75, 172, 198, 192),
            new Color(192, 80, 77, 192)
        };

    private static volatile boolean initialisedMap = false;

    //~ Instance fields --------------------------------------------------------

    private List<CidsBeanWrapper> cidsBeanWrappers;
    private final LandparcelTableModel tableModel;
    private MappingComponent map;
    private CidsBeanWrapper selectedCidsBeanWrapper;
    private Thread mapThread;

    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    private final boolean eigentuemerPermission = AlkisProductDownloadHelper.validateUserHasEigentuemerAccess(
            getConnectionContext());

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.cismet.cids.custom.objectrenderer.wunda_blau.AlkisEigentuemerPanel eigentuemerPanel;
    private javax.swing.JLabel jLabel1;
    private org.jdesktop.swingx.JXHyperlink jxlBaulastBescheinigung;
    private org.jdesktop.swingx.JXHyperlink jxlFlurstuecksnachweis;
    private org.jdesktop.swingx.JXHyperlink jxlKarte;
    private org.jdesktop.swingx.JXHyperlink jxlNachweisKommunal;
    private org.jdesktop.swingx.JXHyperlink jxlNachweisKommunalIntern;
    private org.jdesktop.swingx.JXHyperlink jxlNachweisNRW;
    private javax.swing.JLabel lblHeaderButtons;
    private javax.swing.JLabel lblHeaderLandparcels;
    private javax.swing.JPanel panBuchungEigentum;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlLandparcels;
    private javax.swing.JPanel pnlMap;
    private javax.swing.JScrollPane scpLandparcels;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel1;
    private de.cismet.tools.gui.SemiRoundedPanel srpHeaderButtons;
    private de.cismet.tools.gui.SemiRoundedPanel srpHeaderLandparcels;
    private javax.swing.JTable tblLandparcels;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AlkisLandparcelAggregationRenderer object.
     */
    public AlkisLandparcelAggregationRenderer() {
        tableModel = new LandparcelTableModel();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        initComponents();
        eigentuemerPanel.initWithConnectionContext(connectionContext);

        map = new MappingComponent();
        pnlMap.add(map, BorderLayout.CENTER);
        tblLandparcels.setDefaultRenderer(Color.class, new ColorRenderer());
        tblLandparcels.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged(final ListSelectionEvent e) {
                    final ListSelectionModel lsm = (ListSelectionModel)e.getSource();

                    if (lsm.isSelectionEmpty()) {
                        selectedCidsBeanWrapper = null;
                    } else {
                        selectedCidsBeanWrapper = tableModel.get(lsm.getLeadSelectionIndex());
                    }
                    changeMap();

                    if (showEigentuemer()) {
                        eigentuemerPanel.setFlurstuecke(
                            (selectedCidsBeanWrapper != null) ? Arrays.asList(selectedCidsBeanWrapper.getCidsBean())
                                                              : null);
                    }
                }
            });
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private DescriptionPane waitForDescriptionPane() {
        Component component = this;
        while (component.getParent() == null) {
            try {
                Thread.sleep(100);
            } catch (final Exception ex) {
                LOG.error(ex, ex);
            }
        }
        while ((component = component.getParent()) != null) {
            if (component instanceof SelfDisposingPanel) {
                while (component.getParent() == null) {
                    try {
                        Thread.sleep(100);
                    } catch (final Exception ex) {
                        LOG.error(ex, ex);
                    }
                }
            } else if (component instanceof DescriptionPane) {
                return (DescriptionPane)component;
            }
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean showEigentuemer() {
        return eigentuemerPermission;
    }

    @Override
    public final ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlButtons = new RoundedPanel();
        srpHeaderButtons = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeaderButtons = new javax.swing.JLabel();
        jxlFlurstuecksnachweis = new org.jdesktop.swingx.JXHyperlink();
        jxlNachweisNRW = new org.jdesktop.swingx.JXHyperlink();
        jxlNachweisKommunal = new org.jdesktop.swingx.JXHyperlink();
        jxlNachweisKommunalIntern = new org.jdesktop.swingx.JXHyperlink();
        jxlKarte = new org.jdesktop.swingx.JXHyperlink();
        jxlBaulastBescheinigung = new org.jdesktop.swingx.JXHyperlink();
        pnlMap = new javax.swing.JPanel();
        pnlLandparcels = new RoundedPanel();
        srpHeaderLandparcels = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeaderLandparcels = new javax.swing.JLabel();
        scpLandparcels = new javax.swing.JScrollPane();
        tblLandparcels = new javax.swing.JTable();
        panBuchungEigentum = new RoundedPanel();
        semiRoundedPanel1 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel1 = new javax.swing.JLabel();
        eigentuemerPanel = new de.cismet.cids.custom.objectrenderer.wunda_blau.AlkisEigentuemerPanel();

        setLayout(new java.awt.GridBagLayout());

        pnlButtons.setOpaque(false);
        pnlButtons.setLayout(new java.awt.GridBagLayout());

        srpHeaderButtons.setBackground(java.awt.Color.darkGray);
        srpHeaderButtons.setLayout(new java.awt.GridBagLayout());

        lblHeaderButtons.setForeground(java.awt.Color.white);
        lblHeaderButtons.setText(org.openide.util.NbBundle.getMessage(
                AlkisLandparcelAggregationRenderer.class,
                "AlkisLandparcelAggregationRenderer.lblHeaderButtons.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        srpHeaderButtons.add(lblHeaderButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        pnlButtons.add(srpHeaderButtons, gridBagConstraints);

        jxlFlurstuecksnachweis.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/pdf.png")));   // NOI18N
        jxlFlurstuecksnachweis.setText(org.openide.util.NbBundle.getMessage(
                AlkisLandparcelAggregationRenderer.class,
                "AlkisLandparcelAggregationRenderer.jxlFlurstuecksnachweis.text")); // NOI18N
        jxlFlurstuecksnachweis.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jxlFlurstuecksnachweisActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(7, 10, 7, 10);
        pnlButtons.add(jxlFlurstuecksnachweis, gridBagConstraints);

        jxlNachweisNRW.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/pdf.png"))); // NOI18N
        jxlNachweisNRW.setText(org.openide.util.NbBundle.getMessage(
                AlkisLandparcelAggregationRenderer.class,
                "AlkisLandparcelAggregationRenderer.jxlNachweisNRW.text"));       // NOI18N
        jxlNachweisNRW.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jxlNachweisNRWActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(7, 10, 7, 10);
        pnlButtons.add(jxlNachweisNRW, gridBagConstraints);

        jxlNachweisKommunal.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/pdf.png"))); // NOI18N
        jxlNachweisKommunal.setText(org.openide.util.NbBundle.getMessage(
                AlkisLandparcelAggregationRenderer.class,
                "AlkisLandparcelAggregationRenderer.jxlNachweisKommunal.text"));  // NOI18N
        jxlNachweisKommunal.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jxlNachweisKommunalActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(7, 10, 7, 10);
        pnlButtons.add(jxlNachweisKommunal, gridBagConstraints);

        jxlNachweisKommunalIntern.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/pdf.png")));      // NOI18N
        jxlNachweisKommunalIntern.setText(org.openide.util.NbBundle.getMessage(
                AlkisLandparcelAggregationRenderer.class,
                "AlkisLandparcelAggregationRenderer.jxlNachweisKommunalIntern.text")); // NOI18N
        jxlNachweisKommunalIntern.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jxlNachweisKommunalInternActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(7, 10, 7, 10);
        pnlButtons.add(jxlNachweisKommunalIntern, gridBagConstraints);

        jxlKarte.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/icons/pdf.png"))); // NOI18N
        jxlKarte.setText(org.openide.util.NbBundle.getMessage(
                AlkisLandparcelAggregationRenderer.class,
                "AlkisLandparcelAggregationRenderer.jxlKarte.text"));                                                // NOI18N
        jxlKarte.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jxlKarteActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(7, 10, 10, 10);
        pnlButtons.add(jxlKarte, gridBagConstraints);

        jxlBaulastBescheinigung.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/pdf.png")));    // NOI18N
        jxlBaulastBescheinigung.setText(org.openide.util.NbBundle.getMessage(
                AlkisLandparcelAggregationRenderer.class,
                "AlkisLandparcelAggregationRenderer.jxlBaulastBescheinigung.text")); // NOI18N
        jxlBaulastBescheinigung.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jxlBaulastBescheinigungActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(7, 10, 7, 10);
        pnlButtons.add(jxlBaulastBescheinigung, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        add(pnlButtons, gridBagConstraints);

        pnlMap.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlMap.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        add(pnlMap, gridBagConstraints);

        pnlLandparcels.setMinimumSize(new java.awt.Dimension(309, 207));
        pnlLandparcels.setOpaque(false);
        pnlLandparcels.setLayout(new java.awt.GridBagLayout());

        srpHeaderLandparcels.setBackground(java.awt.Color.darkGray);
        srpHeaderLandparcels.setLayout(new java.awt.GridBagLayout());

        lblHeaderLandparcels.setForeground(java.awt.Color.white);
        lblHeaderLandparcels.setText(org.openide.util.NbBundle.getMessage(
                AlkisLandparcelAggregationRenderer.class,
                "AlkisLandparcelAggregationRenderer.lblHeaderLandparcels.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        srpHeaderLandparcels.add(lblHeaderLandparcels, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlLandparcels.add(srpHeaderLandparcels, gridBagConstraints);

        scpLandparcels.setPreferredSize(new java.awt.Dimension(250, 402));

        tblLandparcels.setModel(tableModel);
        tblLandparcels.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblLandparcels.setShowVerticalLines(false);
        tblLandparcels.addFocusListener(new java.awt.event.FocusAdapter() {

                @Override
                public void focusLost(final java.awt.event.FocusEvent evt) {
                    tblLandparcelsFocusLost(evt);
                }
            });
        scpLandparcels.setViewportView(tblLandparcels);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.35;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnlLandparcels.add(scpLandparcels, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        add(pnlLandparcels, gridBagConstraints);

        panBuchungEigentum.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel1.setBackground(java.awt.Color.darkGray);
        semiRoundedPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText(org.openide.util.NbBundle.getMessage(
                AlkisLandparcelAggregationRenderer.class,
                "AlkisLandparcelAggregationRenderer.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        semiRoundedPanel1.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panBuchungEigentum.add(semiRoundedPanel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panBuchungEigentum.add(eigentuemerPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        add(panBuchungEigentum, gridBagConstraints);
        panBuchungEigentum.setVisible(false);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxlFlurstuecksnachweisActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jxlFlurstuecksnachweisActionPerformed
        downloadEinzelnachweisProduct(ClientAlkisProducts.getInstance().get(
                ClientAlkisProducts.Type.FLURSTUECKSNACHWEIS_PDF),
            true);
    }                                                                                          //GEN-LAST:event_jxlFlurstuecksnachweisActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxlNachweisNRWActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jxlNachweisNRWActionPerformed
        downloadEinzelnachweisProduct(ClientAlkisProducts.getInstance().get(
                ClientAlkisProducts.Type.FLURSTUECKS_UND_EIGENTUMSNACHWEIS_NRW_PDF),
            true);
    }                                                                                  //GEN-LAST:event_jxlNachweisNRWActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxlNachweisKommunalActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jxlNachweisKommunalActionPerformed
        downloadEinzelnachweisProduct(ClientAlkisProducts.getInstance().get(
                ClientAlkisProducts.Type.FLURSTUECKS_UND_EIGENTUMSNACHWEIS_KOMMUNAL_PDF),
            true);
    }                                                                                       //GEN-LAST:event_jxlNachweisKommunalActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxlNachweisKommunalInternActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jxlNachweisKommunalInternActionPerformed
        downloadEinzelnachweisProduct(ClientAlkisProducts.getInstance().get(
                ClientAlkisProducts.Type.FLURSTUECKS_UND_EIGENTUMSNACHWEIS_KOMMUNAL_INTERN_PDF),
            true);
    }                                                                                             //GEN-LAST:event_jxlNachweisKommunalInternActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxlKarteActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jxlKarteActionPerformed
        downloadKarteProduct();
    }                                                                            //GEN-LAST:event_jxlKarteActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void tblLandparcelsFocusLost(final java.awt.event.FocusEvent evt) { //GEN-FIRST:event_tblLandparcelsFocusLost
        map.gotoInitialBoundingBox();
        tblLandparcels.clearSelection();
    }                                                                           //GEN-LAST:event_tblLandparcelsFocusLost

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxlBaulastBescheinigungActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jxlBaulastBescheinigungActionPerformed
//        if (!ObjectRendererUtils.checkActionTag(actionTag)) {
//            showNoProductPermissionWarning();
//            return;
//        }

        final Collection<CidsBean> selectedFlurstuecke = new ArrayList<>();
        for (final CidsBeanWrapper cidsBeanWrapper : cidsBeanWrappers) {
            if (cidsBeanWrapper.isSelected()) {
                selectedFlurstuecke.add(cidsBeanWrapper.getCidsBean());
            }
        }

        BaulastBescheinigungDialog.getInstance().show(selectedFlurstuecke, this, getConnectionContext());
    } //GEN-LAST:event_jxlBaulastBescheinigungActionPerformed

    @Override
    public Collection<CidsBean> getCidsBeans() {
        final Collection<CidsBean> result = new LinkedList<>();

        for (final CidsBeanWrapper wrapper : cidsBeanWrappers) {
            result.add(wrapper.getCidsBean());
        }

        return result;
    }

    @Override
    public void setCidsBeans(final Collection<CidsBean> cidsBeans) {
        if (cidsBeans != null) {
            int colorIndex = 0;
            cidsBeanWrappers = new LinkedList<>();

            for (final CidsBean cidsBean : cidsBeans) {
                cidsBeanWrappers.add(new CidsBeanWrapper(cidsBean, true));
            }
            Collections.sort(cidsBeanWrappers);
            for (final CidsBeanWrapper cidsBeanWrapper : cidsBeanWrappers) {
                cidsBeanWrapper.setColor(COLORS[colorIndex]);
                colorIndex = (colorIndex + 1) % COLORS.length;
            }

            tableModel.setCidsBeans(cidsBeanWrappers);

            initMap();

            if ((tblLandparcels != null) && (tblLandparcels.getColumnModel() != null)) {
                TableColumn column = tblLandparcels.getColumnModel().getColumn(0);
                if (column != null) {
                    column.setPreferredWidth(20);
                }
                column = tblLandparcels.getColumnModel().getColumn(3);
                if (column != null) {
                    column.setPreferredWidth(15);
                }
            }

            changeButtonAvailability(cidsBeanWrappers.size() > 0);
        }

        if (showEigentuemer()) {
            SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        new SwingWorker<DescriptionPane, Void>() {

                            @Override
                            protected DescriptionPane doInBackground() throws Exception {
                                return waitForDescriptionPane();
                            }

                            @Override
                            protected void done() {
                                try {
                                    final DescriptionPane descriptionPane = get();
                                    if ((descriptionPane != null)
                                                && (descriptionPane.getParent()
                                                    instanceof AlkisEigentuemerDescriptionPaneParent)) {
                                        panBuchungEigentum.setVisible(true);
                                        eigentuemerPanel.setFlurstuecke(cidsBeans);
                                    }
                                } catch (final Exception ex) {
                                    LOG.error(ex, ex);
                                }
                            }
                        }.execute();
                    }
                });
        }
    }

    @Override
    public void dispose() {
        eigentuemerPanel.cancelWorker();
        map.dispose();
    }

    @Override
    public String getTitle() {
        return NbBundle.getMessage(
                AlkisLandparcelAggregationRenderer.class,
                "AlkisLandparcelAggregationRenderer.title",
                ((cidsBeanWrappers != null) ? cidsBeanWrappers.size() : "0"));
    }

    @Override
    public void setTitle(final String title) {
        // NOP
    }

    /**
     * DOCUMENT ME!
     */
    private void initMap() {
        mapThread = new Thread(new InitialiseMapRunnable());
        if (EventQueue.isDispatchThread()) {
            mapThread.start();
        } else {
            EventQueue.invokeLater(mapThread);
        }

        revalidate();
        repaint();
    }

    /**
     * DOCUMENT ME!
     */
    private void changeMap() {
        if ((mapThread != null) && mapThread.isAlive()) {
            if (initialisedMap) {
                // Map is initialised. Can be changed.
                mapThread.interrupt();
            } else {
                // Initialising the map is still running. Don't change the map now.
                return;
            }
        }

        mapThread = new Thread(new ChangeMapRunnable());
        if (EventQueue.isDispatchThread()) {
            mapThread.start();
        } else {
            EventQueue.invokeLater(mapThread);
        }

        revalidate();
        repaint();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  enable  DOCUMENT ME!
     */
    private void changeButtonAvailability(final boolean enable) {
        final boolean billingAllowedFsueKom = BillingPopup.isBillingAllowed("fsuekom", getConnectionContext());
        final boolean billingAllowedFsueNw = BillingPopup.isBillingAllowed("fsuenw", getConnectionContext());
        final boolean billingAllowedFsNw = BillingPopup.isBillingAllowed("fsnw", getConnectionContext());
        final boolean billingAllowedBlab_be = BillingPopup.isBillingAllowed("blab_be", getConnectionContext());

        jxlKarte.setEnabled(enable
                    && ObjectRendererUtils.checkActionTag(
                        AlkisProducts.PRODUCT_ACTION_TAG_KARTE,
                        getConnectionContext()));
        jxlFlurstuecksnachweis.setEnabled(enable
                    && ObjectRendererUtils.checkActionTag(
                        AlkisProducts.PRODUCT_ACTION_TAG_FLURSTUECKSNACHWEIS,
                        getConnectionContext())
                    && billingAllowedFsNw);
        jxlNachweisKommunal.setEnabled(enable
                    && ObjectRendererUtils.checkActionTag(
                        AlkisProducts.PRODUCT_ACTION_TAG_FLURSTUECKS_EIGENTUMSNACHWEIS_KOM,
                        getConnectionContext()) && billingAllowedFsueKom);
        jxlNachweisKommunalIntern.setEnabled(enable
                    && ObjectRendererUtils.checkActionTag(
                        AlkisProducts.PRODUCT_ACTION_TAG_FLURSTUECKS_EIGENTUMSNACHWEIS_KOM_INTERN,
                        getConnectionContext()));
        jxlNachweisNRW.setEnabled(enable
                    && ObjectRendererUtils.checkActionTag(
                        AlkisProducts.PRODUCT_ACTION_TAG_FLURSTUECKS_EIGENTUMSNACHWEIS_NRW,
                        getConnectionContext()) && billingAllowedFsueNw);
        jxlBaulastBescheinigung.setEnabled(enable
                    && ObjectRendererUtils.checkActionTag(
                        AlkisProducts.PRODUCT_ACTION_TAG_BAULASTBESCHEINIGUNG_ENABLED,
                        getConnectionContext())
                    && !ObjectRendererUtils.checkActionTag(
                        AlkisProducts.PRODUCT_ACTION_TAG_BAULASTBESCHEINIGUNG_DISABLED,
                        getConnectionContext()) && billingAllowedBlab_be);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  product                DOCUMENT ME!
     * @param  berechtigungspruefung  DOCUMENT ME!
     */
    private void downloadEinzelnachweisProduct(final String product, final boolean berechtigungspruefung) {
        if (
            !ObjectRendererUtils.checkActionTag(
                        ClientAlkisProducts.getInstance().getActionTag(product),
                        getConnectionContext())) {
            AlkisProductDownloadHelper.showNoProductPermissionWarning(this);
            return;
        }

        int stueck = 0;
        for (final CidsBeanWrapper cidsBeanWrapper : cidsBeanWrappers) {
            if (cidsBeanWrapper.isSelected()) {
                stueck++;
            }
        }

        try {
            final List<String> parcelCodes = new ArrayList<>(cidsBeanWrappers.size());
            for (final CidsBeanWrapper cidsBeanWrapper : cidsBeanWrappers) {
                if (!cidsBeanWrapper.isSelected()) {
                    continue;
                }
                parcelCodes.add(AlkisProducts.getLandparcelCodeFromParcelBeanObject(cidsBeanWrapper.getCidsBean()));
            }

            final BerechtigungspruefungAlkisEinzelnachweisDownloadInfo downloadInfo = DownloadInfoFactory
                        .createBerechtigungspruefungAlkisEinzelnachweisDownloadInfo(product, parcelCodes);
            final String billingKey = ClientAlkisProducts.getInstance().getBillingKey(product);
            if ((billingKey == null)
                        || BillingPopup.doBilling(
                            billingKey,
                            "no.yet",
                            (Geometry)null,
                            (berechtigungspruefung
                                && AlkisProductDownloadHelper.checkBerechtigungspruefung(
                                    downloadInfo.getProduktTyp(),
                                    getConnectionContext())) ? downloadInfo : null,
                            getConnectionContext(),
                            new BillingProductGroupAmount("ea", stueck))) {
                AlkisProductDownloadHelper.downloadEinzelnachweisProduct(downloadInfo, getConnectionContext());
            }
        } catch (Exception e) {
            LOG.error("Error when trying to produce a alkis product", e);
            // Hier noch ein Fehlerdialog
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void downloadKarteProduct() {
        if (!ObjectRendererUtils.checkActionTag(AlkisProducts.PRODUCT_ACTION_TAG_KARTE, getConnectionContext())) {
            AlkisProductDownloadHelper.showNoProductPermissionWarning(this);
            return;
        }

        final List<String> parcelCodes = new ArrayList<>(cidsBeanWrappers.size());
        for (final CidsBeanWrapper cidsBeanWrapper : cidsBeanWrappers) {
            if (!cidsBeanWrapper.isSelected()) {
                continue;
            }
            parcelCodes.add(AlkisProducts.getLandparcelCodeFromParcelBeanObject(cidsBeanWrapper.getCidsBean()));
        }

        final BerechtigungspruefungAlkisKarteDownloadInfo downloadInfo = DownloadInfoFactory
                    .createBerechtigungspruefungAlkisKarteDownloadInfo(parcelCodes);
        AlkisProductDownloadHelper.downloadKarteProduct(downloadInfo, getConnectionContext());
    }

    //~ Inner Interfaces -------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public interface AlkisEigentuemerDescriptionPaneParent {
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class LandparcelTableModel extends AbstractTableModel {

        //~ Instance fields ----------------------------------------------------

        private int selectedCidsBeans = 0;

        //~ Methods ------------------------------------------------------------

        @Override
        public int getRowCount() {
            if (cidsBeanWrappers == null) {
                return 0;
            }

            return cidsBeanWrappers.size();
        }

        @Override
        public int getColumnCount() {
            if (cidsBeanWrappers == null) {
                return 0;
            }

            return 4;
        }

        @Override
        public Class<?> getColumnClass(final int columnIndex) {
            if (columnIndex == 0) {
                return Boolean.class;
            } else if (columnIndex == 3) {
                return Color.class;
            } else {
                return String.class;
            }
        }

        @Override
        public String getColumnName(final int column) {
            return NbBundle.getMessage(
                    AlkisLandparcelAggregationRenderer.class,
                    "AlkisLandparcelAggregationRenderer.LandparcelTableModel.getColumnName("
                            + column
                            + ")");
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            if (cidsBeanWrappers == null) {
                return null;
            }

            final CidsBeanWrapper cidsBeanWrapper = cidsBeanWrappers.get(rowIndex);
            if (columnIndex == 0) {
                return cidsBeanWrapper.isSelected();
            } else if (columnIndex == 1) {
                return cidsBeanWrapper.getGemarkung();
            } else if (columnIndex == 2) {
                return cidsBeanWrapper.getBezeichnung();
            } else {
                return cidsBeanWrapper.getColor();
            }
        }

        @Override
        public boolean isCellEditable(final int row, final int column) {
            return column == 0;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  cidsBeans  DOCUMENT ME!
         */
        public void setCidsBeans(final Collection<CidsBeanWrapper> cidsBeans) {
            if (cidsBeans != null) {
                selectedCidsBeans = cidsBeanWrappers.size();
                fireTableStructureChanged();
            }
        }

        @Override
        public void setValueAt(final Object value, final int row, final int column) {
            if (column != 0) {
                return;
            }

            final CidsBeanWrapper cidsBeanWrapper = cidsBeanWrappers.get(row);
            cidsBeanWrapper.setSelected(!cidsBeanWrapper.isSelected());
            if (cidsBeanWrapper.isSelected()) {
                selectedCidsBeans++;
            } else {
                selectedCidsBeans--;
            }

            fireTableRowsUpdated(row, row);
            changeMap();
            changeButtonAvailability(selectedCidsBeans > 0);
        }

        /**
         * DOCUMENT ME!
         *
         * @param   index  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public CidsBeanWrapper get(final int index) {
            return cidsBeanWrappers.get(index);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class InitialiseMapRunnable implements Runnable {

        //~ Methods ------------------------------------------------------------

        @Override
        public void run() {
            initialisedMap = false;

            final ActiveLayerModel mappingModel = new ActiveLayerModel();
            mappingModel.setSrs(ClientAlkisConf.getInstance().getSrsService());
            mappingModel.addHome(getBoundingBox());

            final SimpleWMS swms = new SimpleWMS(new SimpleWmsGetMapUrl(
                        ClientAlkisConf.getInstance().getMapCallString()));
            swms.setName("Flurstueck");

            // add the raster layer to the model
            mappingModel.addLayer(swms);
            // set the model
            map.setMappingModel(mappingModel);
            // initial positioning of the map
            final int duration = map.getAnimationDuration();
            map.setAnimationDuration(0);
            map.gotoInitialBoundingBox();
            // interaction mode
            map.setInteractionMode(MappingComponent.ZOOM);
            // finally when all configurations are done ...
            map.unlock();
            map.setInteractionMode("MUTE");

            for (final CidsBeanWrapper cidsBeanWrapper : cidsBeanWrappers) {
                map.getFeatureCollection().addFeature(cidsBeanWrapper.getFeature());
            }

            map.setAnimationDuration(duration);

            initialisedMap = true;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        private XBoundingBox getBoundingBox() {
            XBoundingBox result = null;
            for (final CidsBeanWrapper cidsBeanWrapper : cidsBeanWrappers) {
                final Geometry geometry = cidsBeanWrapper.getGeometry();

                if (result == null) {
                    result = new XBoundingBox(geometry.getEnvelope().buffer(
                                ClientAlkisConf.getInstance().getGeoBuffer()));
                    result.setSrs(ClientAlkisConf.getInstance().getSrsService());
                    result.setMetric(true);
                } else {
                    final XBoundingBox temp = new XBoundingBox(geometry.getEnvelope().buffer(
                                ClientAlkisConf.getInstance().getGeoBuffer()));
                    temp.setSrs(ClientAlkisConf.getInstance().getSrsService());
                    temp.setMetric(true);

                    if (temp.getX1() < result.getX1()) {
                        result.setX1(temp.getX1());
                    }
                    if (temp.getY1() < result.getY1()) {
                        result.setY1(temp.getY1());
                    }
                    if (temp.getX2() > result.getX2()) {
                        result.setX2(temp.getX2());
                    }
                    if (temp.getY2() > result.getY2()) {
                        result.setY2(temp.getY2());
                    }
                }
            }

            return result;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class ChangeMapRunnable implements Runnable {

        //~ Methods ------------------------------------------------------------

        @Override
        public void run() {
            final XBoundingBox boxToGoto = new XBoundingBox(selectedCidsBeanWrapper.getGeometry().getEnvelope().buffer(
                        ClientAlkisConf.getInstance().getGeoBuffer()));
            boxToGoto.setX1(boxToGoto.getX1()
                        - (ClientAlkisConf.getInstance().getGeoBufferMultiplier() * boxToGoto.getWidth()));
            boxToGoto.setX2(boxToGoto.getX2()
                        + (ClientAlkisConf.getInstance().getGeoBufferMultiplier() * boxToGoto.getWidth()));
            boxToGoto.setY1(boxToGoto.getY1()
                        - (ClientAlkisConf.getInstance().getGeoBufferMultiplier() * boxToGoto.getHeight()));
            boxToGoto.setY2(boxToGoto.getY2()
                        + (ClientAlkisConf.getInstance().getGeoBufferMultiplier() * boxToGoto.getHeight()));
            map.gotoBoundingBox(boxToGoto, false, true, 500);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class ColorRenderer extends JLabel implements TableCellRenderer {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ColorRenderer object.
         */
        public ColorRenderer() {
            setOpaque(true);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getTableCellRendererComponent(final JTable table,
                final Object color,
                final boolean isSelected,
                final boolean hasFocus,
                final int row,
                final int column) {
            final Color newColor = (Color)color;
            setBackground(newColor);

            return this;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class CidsBeanWrapper implements Comparable<CidsBeanWrapper> {

        //~ Instance fields ----------------------------------------------------

        private CidsBean cidsBean;
        private boolean selected;
        private Color color;
        private String gemarkung;
        private String bezeichnung;
        private Geometry geometry;
        private StyledFeature feature;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new CidsBeanWrapper object.
         *
         * @param  cidsBean  DOCUMENT ME!
         * @param  selected  DOCUMENT ME!
         */
        public CidsBeanWrapper(final CidsBean cidsBean, final boolean selected) {
            this.cidsBean = cidsBean;
            this.selected = selected;
            this.gemarkung = cidsBean.getProperty("gemarkung").toString();
            this.bezeichnung = cidsBean.getProperty("bezeichnung").toString();
            if (cidsBean.getProperty("geometrie") instanceof Geometry) {
                this.geometry = CrsTransformer.transformToGivenCrs((Geometry)cidsBean.getProperty(
                            "geometrie"),
                        ClientAlkisConf.getInstance().getSrsService());
            }

            final StyledFeature dsf = new DefaultStyledFeature();
            dsf.setGeometry(this.geometry);
            dsf.setTransparency(0.8F);

            this.feature = dsf;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public CidsBean getCidsBean() {
            return cidsBean;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Color getColor() {
            return color;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  color  DOCUMENT ME!
         */
        public void setColor(final Color color) {
            this.color = color;
            feature.setFillingPaint(this.color);
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public boolean isSelected() {
            return selected;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  selected  DOCUMENT ME!
         */
        public void setSelected(final boolean selected) {
            this.selected = selected;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public String getGemarkung() {
            return gemarkung;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public String getBezeichnung() {
            return bezeichnung;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Geometry getGeometry() {
            return geometry;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public StyledFeature getFeature() {
            return feature;
        }

        @Override
        public int compareTo(final CidsBeanWrapper o) {
            final CidsBean cidsBean1 = cidsBean;
            final CidsBean cidsBean2 = o.cidsBean;

            if ((cidsBean1 == null) && (cidsBean2 == null)) {
                return 0;
            } else if (cidsBean1 == null) {
                return -1;
            } else if (cidsBean2 == null) {
                return 1;
            }

            final int districtComparison = cidsBean1.getProperty("gemarkung")
                        .toString()
                        .compareTo(cidsBean2.getProperty("gemarkung").toString());

            if (districtComparison != 0) {
                return districtComparison;
            } else {
                return cidsBean1.getProperty("bezeichnung")
                            .toString()
                            .compareTo(cidsBean2.getProperty("bezeichnung").toString());
            }
        }
    }
}
