/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * AlkisBuchungsblattAggregationRenderer.java
 *
 * Created on 18.07.2011, 16:28:22
 */
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.ui.ComponentRegistry;
import Sirius.navigator.ui.RequestsFullSizeComponent;

import Sirius.server.middleware.types.MetaObjectNode;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
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
import de.cismet.cids.custom.objectrenderer.utils.alkis.StichtagChooserDialog;
import de.cismet.cids.custom.objectrenderer.utils.billing.BillingPopup;
import de.cismet.cids.custom.utils.alkis.AlkisProducts;
import de.cismet.cids.custom.utils.berechtigungspruefung.DownloadInfoFactory;
import de.cismet.cids.custom.utils.berechtigungspruefung.katasterauszug.BerechtigungspruefungAlkisEinzelnachweisDownloadInfo;
import de.cismet.cids.custom.utils.billing.BillingProductGroupAmount;
import de.cismet.cids.custom.wunda_blau.search.server.CidsAlkisSearchStatement;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanAggregationRenderer;

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
import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class AlkisBuchungsblattAggregationRenderer extends javax.swing.JPanel implements CidsBeanAggregationRenderer,
    RequestsFullSizeComponent,
    ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(AlkisBuchungsblattAggregationRenderer.class);

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
    private BuchungsblattTableModel tableModel;
    private MappingComponent map;
    private CidsBeanWrapper selectedCidsBeanWrapper;
    private Thread mapThread;
    private Map<CidsBean, Collection<CidsBean>> selectedFlurstueckeMap;

    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXHyperlink hlBestandsnachweisStichtagNRW;
    private org.jdesktop.swingx.JXHyperlink jxlBaulastBescheinigung;
    private org.jdesktop.swingx.JXHyperlink jxlBestandsnachweisKommunal;
    private org.jdesktop.swingx.JXHyperlink jxlBestandsnachweisKommunalIntern;
    private org.jdesktop.swingx.JXHyperlink jxlBestandsnachweisNRW;
    private javax.swing.JLabel lblHeaderBuchungsblaetter;
    private javax.swing.JLabel lblHeaderButtons;
    private javax.swing.JPanel pnlBuchungsblaetter;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlMap;
    private javax.swing.JScrollPane scpBuchungsblaetter;
    private de.cismet.tools.gui.SemiRoundedPanel srpHeaderBuchungsblaetter;
    private de.cismet.tools.gui.SemiRoundedPanel srpHeaderButtons;
    private javax.swing.JTable tblBuchungsblaetter;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AlkisBuchungsblattAggregationRenderer object.
     */
    public AlkisBuchungsblattAggregationRenderer() {
        tableModel = new BuchungsblattTableModel();
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

        pnlButtons = new RoundedPanel();
        srpHeaderButtons = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeaderButtons = new javax.swing.JLabel();
        jxlBestandsnachweisNRW = new org.jdesktop.swingx.JXHyperlink();
        jxlBestandsnachweisKommunal = new org.jdesktop.swingx.JXHyperlink();
        jxlBestandsnachweisKommunalIntern = new org.jdesktop.swingx.JXHyperlink();
        hlBestandsnachweisStichtagNRW = new org.jdesktop.swingx.JXHyperlink();
        jxlBaulastBescheinigung = new org.jdesktop.swingx.JXHyperlink();
        pnlBuchungsblaetter = new RoundedPanel();
        srpHeaderBuchungsblaetter = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeaderBuchungsblaetter = new javax.swing.JLabel();
        scpBuchungsblaetter = new javax.swing.JScrollPane();
        tblBuchungsblaetter = new javax.swing.JTable();
        pnlMap = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        pnlButtons.setOpaque(false);
        pnlButtons.setLayout(new java.awt.GridBagLayout());

        srpHeaderButtons.setBackground(java.awt.Color.darkGray);
        srpHeaderButtons.setLayout(new java.awt.GridBagLayout());

        lblHeaderButtons.setForeground(java.awt.Color.white);
        lblHeaderButtons.setText(org.openide.util.NbBundle.getMessage(
                AlkisBuchungsblattAggregationRenderer.class,
                "AlkisBuchungsblattAggregationRenderer.lblHeaderButtons.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        srpHeaderButtons.add(lblHeaderButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        pnlButtons.add(srpHeaderButtons, gridBagConstraints);

        jxlBestandsnachweisNRW.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/pdf.png")));      // NOI18N
        jxlBestandsnachweisNRW.setText(org.openide.util.NbBundle.getMessage(
                AlkisBuchungsblattAggregationRenderer.class,
                "AlkisBuchungsblattAggregationRenderer.jxlBestandsnachweisNRW.text")); // NOI18N
        jxlBestandsnachweisNRW.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jxlBestandsnachweisNRWActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(7, 10, 7, 10);
        pnlButtons.add(jxlBestandsnachweisNRW, gridBagConstraints);

        jxlBestandsnachweisKommunal.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/pdf.png")));           // NOI18N
        jxlBestandsnachweisKommunal.setText(org.openide.util.NbBundle.getMessage(
                AlkisBuchungsblattAggregationRenderer.class,
                "AlkisBuchungsblattAggregationRenderer.jxlBestandsnachweisKommunal.text")); // NOI18N
        jxlBestandsnachweisKommunal.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jxlBestandsnachweisKommunalActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(7, 10, 7, 10);
        pnlButtons.add(jxlBestandsnachweisKommunal, gridBagConstraints);

        jxlBestandsnachweisKommunalIntern.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/pdf.png")));                 // NOI18N
        jxlBestandsnachweisKommunalIntern.setText(org.openide.util.NbBundle.getMessage(
                AlkisBuchungsblattAggregationRenderer.class,
                "AlkisBuchungsblattAggregationRenderer.jxlBestandsnachweisKommunalIntern.text")); // NOI18N
        jxlBestandsnachweisKommunalIntern.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jxlBestandsnachweisKommunalInternActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(7, 10, 7, 10);
        pnlButtons.add(jxlBestandsnachweisKommunalIntern, gridBagConstraints);

        hlBestandsnachweisStichtagNRW.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/pdf.png")));             // NOI18N
        hlBestandsnachweisStichtagNRW.setText(org.openide.util.NbBundle.getMessage(
                AlkisBuchungsblattAggregationRenderer.class,
                "AlkisBuchungsblattAggregationRenderer.hlBestandsnachweisStichtagNRW.text")); // NOI18N
        hlBestandsnachweisStichtagNRW.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    hlBestandsnachweisStichtagNRWActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(7, 10, 7, 10);
        pnlButtons.add(hlBestandsnachweisStichtagNRW, gridBagConstraints);

        jxlBaulastBescheinigung.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/pdf.png")));       // NOI18N
        jxlBaulastBescheinigung.setText(org.openide.util.NbBundle.getMessage(
                AlkisBuchungsblattAggregationRenderer.class,
                "AlkisBuchungsblattAggregationRenderer.jxlBaulastBescheinigung.text")); // NOI18N
        jxlBaulastBescheinigung.setEnabled(false);
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
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        add(pnlButtons, gridBagConstraints);

        pnlBuchungsblaetter.setMinimumSize(new java.awt.Dimension(228, 132));
        pnlBuchungsblaetter.setOpaque(false);
        pnlBuchungsblaetter.setLayout(new java.awt.GridBagLayout());

        srpHeaderBuchungsblaetter.setBackground(java.awt.Color.darkGray);
        srpHeaderBuchungsblaetter.setLayout(new java.awt.GridBagLayout());

        lblHeaderBuchungsblaetter.setForeground(java.awt.Color.white);
        lblHeaderBuchungsblaetter.setText(org.openide.util.NbBundle.getMessage(
                AlkisBuchungsblattAggregationRenderer.class,
                "AlkisBuchungsblattAggregationRenderer.lblHeaderBuchungsblaetter.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        srpHeaderBuchungsblaetter.add(lblHeaderBuchungsblaetter, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlBuchungsblaetter.add(srpHeaderBuchungsblaetter, gridBagConstraints);

        scpBuchungsblaetter.setPreferredSize(new java.awt.Dimension(250, 402));

        tblBuchungsblaetter.setModel(tableModel);
        tblBuchungsblaetter.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblBuchungsblaetter.setShowVerticalLines(false);
        tblBuchungsblaetter.addFocusListener(new java.awt.event.FocusAdapter() {

                @Override
                public void focusLost(final java.awt.event.FocusEvent evt) {
                    tblBuchungsblaetterFocusLost(evt);
                }
            });
        scpBuchungsblaetter.setViewportView(tblBuchungsblaetter);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.35;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnlBuchungsblaetter.add(scpBuchungsblaetter, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        add(pnlBuchungsblaetter, gridBagConstraints);

        pnlMap.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlMap.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        add(pnlMap, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxlBestandsnachweisNRWActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jxlBestandsnachweisNRWActionPerformed
        downloadEinzelnachweisProduct(ClientAlkisProducts.getInstance().get(
                ClientAlkisProducts.Type.BESTANDSNACHWEIS_NRW_PDF),
            true);
    }                                                                                          //GEN-LAST:event_jxlBestandsnachweisNRWActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxlBestandsnachweisKommunalActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jxlBestandsnachweisKommunalActionPerformed
        downloadEinzelnachweisProduct(ClientAlkisProducts.getInstance().get(
                ClientAlkisProducts.Type.BESTANDSNACHWEIS_KOMMUNAL_PDF),
            true);
    }                                                                                               //GEN-LAST:event_jxlBestandsnachweisKommunalActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxlBestandsnachweisKommunalInternActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jxlBestandsnachweisKommunalInternActionPerformed
        downloadEinzelnachweisProduct(ClientAlkisProducts.getInstance().get(
                ClientAlkisProducts.Type.BESTANDSNACHWEIS_KOMMUNAL_INTERN_PDF),
            true);
    }                                                                                                     //GEN-LAST:event_jxlBestandsnachweisKommunalInternActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void tblBuchungsblaetterFocusLost(final java.awt.event.FocusEvent evt) { //GEN-FIRST:event_tblBuchungsblaetterFocusLost
        map.gotoInitialBoundingBox();
        tblBuchungsblaetter.clearSelection();
    }                                                                                //GEN-LAST:event_tblBuchungsblaetterFocusLost

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void hlBestandsnachweisStichtagNRWActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_hlBestandsnachweisStichtagNRWActionPerformed
        downloadEinzelnachweisStichtagProduct(true);
    }                                                                                                 //GEN-LAST:event_hlBestandsnachweisStichtagNRWActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  berechtigungspruefung  DOCUMENT ME!
     */
    private void downloadEinzelnachweisStichtagProduct(final boolean berechtigungspruefung) {
        final String product = ClientAlkisProducts.getInstance()
                    .get(ClientAlkisProducts.Type.BESTANDSNACHWEIS_STICHTAGSBEZOGEN_NRW_PDF);
        if (
            !ObjectRendererUtils.checkActionTag(
                        ClientAlkisProducts.getInstance().getActionTag(product),
                        getConnectionContext())) {
            AlkisProductDownloadHelper.showNoProductPermissionWarning(this);
            return;
        }

        final List<String> buchungsblattCodes = new ArrayList<String>(cidsBeanWrappers.size());
        for (final CidsBeanWrapper cidsBeanWrapper : cidsBeanWrappers) {
            if (!cidsBeanWrapper.isSelected()) {
                continue;
            }
            buchungsblattCodes.add(getCompleteBuchungsblattCode(cidsBeanWrapper.getCidsBean()));
        }

        int stueck = 0;
        for (final CidsBeanWrapper cidsBeanWrapper : cidsBeanWrappers) {
            if (cidsBeanWrapper.isSelected()) {
                stueck++;
            }
        }

        final StichtagChooserDialog stichtagDialog = new StichtagChooserDialog(ComponentRegistry.getRegistry()
                        .getMainWindow(),
                getConnectionContext());
        StaticSwingTools.showDialog(stichtagDialog);
        final Date stichtag = stichtagDialog.getDate();
        try {
            if (stichtag != null) {
                final BerechtigungspruefungAlkisEinzelnachweisDownloadInfo downloadInfo = DownloadInfoFactory
                            .createAlkisBuchungsblattnachweisDownloadInfo(
                                product,
                                stichtag,
                                buchungsblattCodes);
                AlkisProductDownloadHelper.downloadBuchungsblattnachweisStichtagProduct(
                    downloadInfo,
                    getConnectionContext());
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
                }
            }
        } catch (Exception e) {
            LOG.error("Error when trying to produce a alkis product", e);
            // Hier noch ein Fehlerdialog
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxlBaulastBescheinigungActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jxlBaulastBescheinigungActionPerformed
        final Collection<CidsBean> selectedFlurstuecke = new ArrayList<CidsBean>();
        for (final CidsBeanWrapper wrapper : cidsBeanWrappers) {
            if (wrapper.isSelected()) {
                selectedFlurstuecke.addAll(selectedFlurstueckeMap.get(wrapper.getCidsBean()));
            }
        }
        BaulastBescheinigungDialog.getInstance()
                .show(selectedFlurstuecke, AlkisBuchungsblattAggregationRenderer.this, getConnectionContext());
    }                                                                                           //GEN-LAST:event_jxlBaulastBescheinigungActionPerformed

    @Override
    public Collection<CidsBean> getCidsBeans() {
        final Collection<CidsBean> result = new LinkedList<CidsBean>();

        for (final CidsBeanWrapper cidsBeanWrapper : cidsBeanWrappers) {
            result.add(cidsBeanWrapper.getCidsBean());
        }

        return result;
    }

    @Override
    public void setCidsBeans(final Collection<CidsBean> cidsBeans) {
        if (cidsBeans != null) {
            int colorIndex = 0;
            cidsBeanWrappers = new LinkedList<CidsBeanWrapper>();

            for (final CidsBean cidsBean : cidsBeans) {
                cidsBeanWrappers.add(new CidsBeanWrapper(cidsBean, true));
            }

            final boolean billingAllowedBlab_be = BillingPopup.isBillingAllowed("blab_be", getConnectionContext());
            if (billingAllowedBlab_be) {
                new SwingWorker<Map<CidsBean, Collection<CidsBean>>, Void>() {

                        @Override
                        protected Map<CidsBean, Collection<CidsBean>> doInBackground() throws Exception {
                            final HashMap<CidsBean, Collection<CidsBean>> selectedFlurstueckeMap =
                                new HashMap<CidsBean, Collection<CidsBean>>();
                            final Set<String> landparcelCodes = new HashSet<String>();
                            for (final CidsBean cidsBean : cidsBeans) {
                                final Collection<CidsBean> selectedFlurstuecke = new ArrayList<CidsBean>();
                                for (final CidsBean landparcelBean
                                            : cidsBean.getBeanCollectionProperty("landparcels")) {
                                    final String landparcelcode = (String)landparcelBean.getProperty(
                                            "landparcelcode");
                                    if (!landparcelCodes.contains(landparcelcode)) {
                                        landparcelCodes.add(landparcelcode);
                                        final CidsAlkisSearchStatement search = new CidsAlkisSearchStatement(
                                                CidsAlkisSearchStatement.Resulttyp.FLURSTUECK,
                                                CidsAlkisSearchStatement.SucheUeber.FLURSTUECKSNUMMER,
                                                landparcelcode,
                                                null);

                                        final Collection<MetaObjectNode> mons = SessionManager.getProxy()
                                                    .customServerSearch(SessionManager.getSession().getUser(),
                                                        search,
                                                        getConnectionContext());
                                        if (!mons.isEmpty()) {
                                            final MetaObjectNode mon = mons.iterator().next();
                                            final CidsBean flurstueck = SessionManager.getProxy()
                                                        .getMetaObject(mon.getObjectId(),
                                                                mon.getClassId(),
                                                                "WUNDA_BLAU",
                                                                getConnectionContext())
                                                        .getBean();
                                            selectedFlurstuecke.add(flurstueck);
                                        }
                                    }
                                    selectedFlurstueckeMap.put(cidsBean, selectedFlurstuecke);
                                }
                            }
                            return selectedFlurstueckeMap;
                        }

                        @Override
                        protected void done() {
                            try {
                                selectedFlurstueckeMap = get();
                                jxlBaulastBescheinigung.setText("Baulastbescheinigung");
                                if ((selectedFlurstueckeMap != null) && !selectedFlurstueckeMap.isEmpty()) {
                                    jxlBaulastBescheinigung.setEnabled(true);
                                }
                            } catch (final Exception ex) {
                                LOG.warn(ex, ex);
                            }
                        }
                    }.execute();
            }
            Collections.sort(cidsBeanWrappers);
            for (final CidsBeanWrapper cidsBeanWrapper : cidsBeanWrappers) {
                cidsBeanWrapper.setColor(COLORS[colorIndex]);
                colorIndex = (colorIndex + 1) % COLORS.length;
            }

            tableModel.setCidsBeans(cidsBeanWrappers);
            initMap();

            if ((tblBuchungsblaetter != null) && (tblBuchungsblaetter.getColumnModel() != null)) {
                TableColumn column = tblBuchungsblaetter.getColumnModel().getColumn(0);
                if (column != null) {
                    column.setPreferredWidth(20);
                }
                column = tblBuchungsblaetter.getColumnModel().getColumn(3);
                if (column != null) {
                    column.setPreferredWidth(15);
                }
            }

            changeButtonAvailability(cidsBeanWrappers.size() > 0);
        }
    }

    @Override
    public void dispose() {
        map.dispose();
    }

    @Override
    public String getTitle() {
        return NbBundle.getMessage(
                AlkisBuchungsblattAggregationRenderer.class,
                "AlkisBuchungsblattAggregationRenderer.title",
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
        final boolean billingAllowedNw = BillingPopup.isBillingAllowed("bestnw", getConnectionContext());
        final boolean billingAllowedKom = BillingPopup.isBillingAllowed("bekom", getConnectionContext());
        final boolean billingAllowedBlab_be = BillingPopup.isBillingAllowed("blab_be", getConnectionContext());

        jxlBestandsnachweisNRW.setEnabled(ObjectRendererUtils.checkActionTag(
                AlkisProducts.PRODUCT_ACTION_TAG_BESTANDSNACHWEIS_NRW,
                getConnectionContext())
                    && billingAllowedNw);
        jxlBestandsnachweisKommunal.setEnabled(ObjectRendererUtils.checkActionTag(
                AlkisProducts.PRODUCT_ACTION_TAG_BESTANDSNACHWEIS_KOM,
                getConnectionContext()) && billingAllowedKom);
        jxlBestandsnachweisKommunalIntern.setEnabled(ObjectRendererUtils.checkActionTag(
                AlkisProducts.PRODUCT_ACTION_TAG_BESTANDSNACHWEIS_KOM_INTERN,
                getConnectionContext()));
        jxlBaulastBescheinigung.setEnabled(enable
                    && ObjectRendererUtils.checkActionTag(
                        AlkisProducts.PRODUCT_ACTION_TAG_BAULASTBESCHEINIGUNG_ENABLED,
                        getConnectionContext())
                    && !ObjectRendererUtils.checkActionTag(
                        AlkisProducts.PRODUCT_ACTION_TAG_BAULASTBESCHEINIGUNG_DISABLED,
                        getConnectionContext()) && billingAllowedBlab_be
                    && (selectedFlurstueckeMap != null) && !selectedFlurstueckeMap.isEmpty());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  product                DOCUMENT ME!
     * @param  berechtigungspruefung  actionTag DOCUMENT ME!
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
            final List<String> buchungsblattCodes = new ArrayList<>(cidsBeanWrappers.size());
            for (final CidsBeanWrapper cidsBeanWrapper : cidsBeanWrappers) {
                if (!cidsBeanWrapper.isSelected()) {
                    continue;
                }
                buchungsblattCodes.add(getCompleteBuchungsblattCode(cidsBeanWrapper.getCidsBean()));
            }

            final BerechtigungspruefungAlkisEinzelnachweisDownloadInfo downloadInfo = DownloadInfoFactory
                        .createAlkisBuchungsblattachweisDownloadInfo(product, buchungsblattCodes);

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
                AlkisProductDownloadHelper.downloadBuchungsblattnachweisProduct(
                    downloadInfo,
                    getConnectionContext());
            }
        } catch (Exception e) {
            LOG.error("Error when trying to produce a alkis product", e);
            // Hier noch ein Fehlerdialog
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getCompleteBuchungsblattCode(final CidsBean cidsBean) {
        String result = "";

        if (cidsBean != null) {
            final Object buchungsblattcode = cidsBean.getProperty("buchungsblattcode");

            if (buchungsblattcode != null) {
                result = AlkisProducts.fixBuchungslattCode(buchungsblattcode.toString());
            }
        }

        return result;
    }

    @Override
    public final ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        initComponents();

        map = new MappingComponent();
        pnlMap.add(map, BorderLayout.CENTER);
        tblBuchungsblaetter.setDefaultRenderer(Color.class, new ColorRenderer());
        tblBuchungsblaetter.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged(final ListSelectionEvent e) {
                    final ListSelectionModel lsm = (ListSelectionModel)e.getSource();

                    if (lsm.isSelectionEmpty()) {
                        selectedCidsBeanWrapper = null;
                    } else {
                        selectedCidsBeanWrapper = tableModel.get(lsm.getLeadSelectionIndex());
                    }
                    changeMap();
                }
            });

        final boolean billingAllowedNw = BillingPopup.isBillingAllowed("bestnw", getConnectionContext());
        final boolean billingAllowedKom = BillingPopup.isBillingAllowed("bekom", getConnectionContext());
        jxlBestandsnachweisNRW.setEnabled(ObjectRendererUtils.checkActionTag(
                AlkisProducts.PRODUCT_ACTION_TAG_BESTANDSNACHWEIS_NRW,
                getConnectionContext())
                    && billingAllowedNw);
        jxlBestandsnachweisKommunal.setEnabled(ObjectRendererUtils.checkActionTag(
                AlkisProducts.PRODUCT_ACTION_TAG_BESTANDSNACHWEIS_KOM,
                getConnectionContext()) && billingAllowedKom);
        jxlBestandsnachweisKommunalIntern.setEnabled(ObjectRendererUtils.checkActionTag(
                AlkisProducts.PRODUCT_ACTION_TAG_BESTANDSNACHWEIS_KOM_INTERN,
                getConnectionContext()));

        final boolean billingAllowedBlab_be = BillingPopup.isBillingAllowed("blab_be", getConnectionContext());
        if (!billingAllowedBlab_be) {
            jxlBaulastBescheinigung.setText("Baulastbescheinigung");
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class BuchungsblattTableModel extends AbstractTableModel {

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
                    AlkisBuchungsblattAggregationRenderer.class,
                    "AlkisBuchungsblattAggregationRenderer.BuchungsblattTableModel.getColumnName("
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
                return cidsBeanWrapper.getBlattart();
            } else if (columnIndex == 2) {
                return cidsBeanWrapper.getBuchungsblattcode();
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
        public void setCidsBeans(final List<CidsBeanWrapper> cidsBeans) {
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
            swms.setName("Buchungsblatt");

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
                map.getFeatureCollection().addFeatures(cidsBeanWrapper.getFeatures());
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
                for (final Geometry geometry : cidsBeanWrapper.getGeometries()) {
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
            final GeometryCollection geoCollection = new GeometryCollection(selectedCidsBeanWrapper.getGeometries()
                            .toArray(
                                new Geometry[selectedCidsBeanWrapper.getGeometries().size()]),
                    new GeometryFactory());
            final XBoundingBox boxToGoto = new XBoundingBox(geoCollection.getEnvelope().buffer(
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
        private String blattart;
        private String buchungsblattcode;
        private Collection<Geometry> geometries;
        private Collection<StyledFeature> features;

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
            this.blattart = cidsBean.getProperty("blattart").toString();
            this.buchungsblattcode = cidsBean.getProperty("buchungsblattcode").toString();
            this.geometries = new LinkedList<Geometry>();
            this.features = new LinkedList<StyledFeature>();

            for (final CidsBean landparcel : this.cidsBean.getBeanCollectionProperty("landparcels")) {
                final Object geometry = landparcel.getProperty("geometrie.geo_field");
                if (geometry instanceof Geometry) {
                    final Geometry transformedGeometry = CrsTransformer.transformToGivenCrs((Geometry)geometry,
                            ClientAlkisConf.getInstance().getSrsService());

                    final StyledFeature dsf = new DefaultStyledFeature();
                    dsf.setGeometry(transformedGeometry);

                    geometries.add(transformedGeometry);
                    features.add(dsf);
                }
            }
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
            for (final StyledFeature feature : features) {
                feature.setFillingPaint(this.color);
            }
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
        public String getBlattart() {
            return blattart;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public String getBuchungsblattcode() {
            return buchungsblattcode;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Collection<Geometry> getGeometries() {
            return geometries;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Collection<StyledFeature> getFeatures() {
            return features;
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

            final int districtComparison = cidsBean1.getProperty("blattart")
                        .toString()
                        .compareTo(cidsBean2.getProperty("blattart").toString());

            if (districtComparison != 0) {
                return districtComparison;
            } else {
                return cidsBean1.getProperty("buchungsblattcode")
                            .toString()
                            .compareTo(cidsBean2.getProperty("buchungsblattcode").toString());
            }
        }
    }
}
