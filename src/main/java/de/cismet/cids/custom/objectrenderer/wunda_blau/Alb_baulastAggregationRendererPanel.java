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
/*
 * Alb_baulastAggregationRendererPanel.java
 *
 * Created on 04.12.2009, 12:17:34
 */
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import Sirius.util.collections.MultiMap;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.objectrenderer.utils.alkis.ClientAlkisConf;
import de.cismet.cids.custom.objectrenderer.utils.billing.BillingPopup;
import de.cismet.cids.custom.utils.alkis.BaulastenReportGenerator;
import de.cismet.cids.custom.utils.billing.BillingProductGroupAmount;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanAggregationRenderer;

import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.PureNewFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.TitleComponentProvider;
import de.cismet.tools.gui.downloadmanager.Download;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class Alb_baulastAggregationRendererPanel extends javax.swing.JPanel implements CidsBeanAggregationRenderer,
    TitleComponentProvider,
    ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final Color BELASTET_COLOR = new Color(0, 255, 0);
    private static final Color BEGUENSTIGT_COLOR = new Color(255, 255, 0);
    private static final Color BEIDES_COLOR = Color.RED;
    private static final double BUFFER = 0.005;

    private static final String REPORT_ACTION_TAG_BLATT = "baulast.report.blatt_disabled@WUNDA_BLAU";
    private static final String REPORT_ACTION_TAG_PLAN = "baulast.report.plan_disabled@WUNDA_BLAU";
    private static final String REPORT_ACTION_TAG_RASTER = "baulast.report.raster_disabled@WUNDA_BLAU";

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            Alb_baulastAggregationRendererPanel.class);
    private static final String[] AGR_COMLUMN_NAMES = new String[] {
            "Auswahl",
            "Blattnummer",
            "Laufende Nummer",
            "Art",
            "Eintragungsdatum",
            "Befristungsdatum",
            "Geschlossen am",
            "Löschungsdatum",
            "Geprüft"
        };
    // Namen der Properties -> Spalten
    private static final String[] AGR_PROPERTY_NAMES = new String[] {
            "blattnummer",
            "laufende_nummer",
            "art",
            "eintragungsdatum",
            "befristungsdatum",
            "geschlossen_am",
            "loeschungsdatum",
            "geprueft"
        };
    private static final int[] AGR_COMLUMN_WIDTH = new int[] { 40, 40, 40, 100, 75, 75, 75, 75, 40 };

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private enum Type {

        //~ Enum constants -----------------------------------------------------

        BEG, BEL, BOTH
    }

    //~ Instance fields --------------------------------------------------------

    private List<CidsBean> cidsBeans;
    private BaulastblattTableModel tableModel;
    private MultiMap featuresMM;
    private final Comparator<Integer> tableComparator;

    private final ConnectionContext connectionContext;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGenerateReport;
    private javax.swing.JComboBox cmbType;
    private javax.swing.Box.Filler flrGap;
    private javax.swing.JLabel lblAgrTitle;
    private javax.swing.JLabel lblJobnumber;
    private javax.swing.JLabel lblProjectname;
    private javax.swing.JLabel lblType;
    private de.cismet.cismap.commons.gui.MappingComponent mappingComponent;
    private javax.swing.JPanel panMap;
    private javax.swing.JPanel panTitle;
    private javax.swing.JPanel panTitleString;
    private javax.swing.JPanel pnlReport;
    private javax.swing.JScrollPane scpRisse;
    private javax.swing.JTable tblRisse;
    private javax.swing.JTextField txtJobnumber;
    private javax.swing.JTextField txtProjectname;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Alb_baulastblattAggregationRenderer object.
     *
     * @param  connectionContext  DOCUMENT ME!
     */
    public Alb_baulastAggregationRendererPanel(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;

        initComponents();

        scpRisse.getViewport().setOpaque(false);
        tblRisse.getSelectionModel().addListSelectionListener(new TableSelectionListener());

        tableComparator = new TableModelIndexConvertingToViewIndexComparator(tblRisse);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public final ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    /**
     * DOCUMENT ME!
     */
    public void animateToOverview() {
        mappingComponent.gotoInitialBoundingBox();
        final Rectangle2D viewBounds = mappingComponent.getCamera().getViewBounds().getBounds2D();
        final double scale = mappingComponent.getScaleDenominator();
        final double newX = ((viewBounds.getX() / scale) - BUFFER) * scale;
        final double newY = ((viewBounds.getY() / scale) - BUFFER) * scale;
        final double newWidth = ((viewBounds.getWidth() / scale) + (BUFFER * 2)) * scale;
        final double newHeight = ((viewBounds.getHeight() / scale) + (BUFFER * 2)) * scale;
        viewBounds.setRect(newX, newY, newWidth, newHeight);
        mappingComponent.getCamera()
                .animateViewToCenterBounds(viewBounds, true, mappingComponent.getAnimationDuration());
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panTitle = new javax.swing.JPanel();
        panTitleString = new javax.swing.JPanel();
        lblAgrTitle = new javax.swing.JLabel();
        scpRisse = new javax.swing.JScrollPane();
        tblRisse = new javax.swing.JTable();
        panMap = new javax.swing.JPanel();
        mappingComponent = new de.cismet.cismap.commons.gui.MappingComponent();
        pnlReport = new javax.swing.JPanel();
        flrGap = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        lblProjectname = new javax.swing.JLabel();
        txtProjectname = new javax.swing.JTextField();
        lblJobnumber = new javax.swing.JLabel();
        txtJobnumber = new javax.swing.JTextField();
        btnGenerateReport = new javax.swing.JButton();
        cmbType = new javax.swing.JComboBox();
        lblType = new javax.swing.JLabel();

        panTitle.setOpaque(false);
        panTitle.setLayout(new java.awt.BorderLayout());

        panTitleString.setOpaque(false);
        panTitleString.setLayout(new java.awt.GridBagLayout());

        lblAgrTitle.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblAgrTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblAgrTitle.setText("error ...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        panTitleString.add(lblAgrTitle, gridBagConstraints);

        panTitle.add(panTitleString, java.awt.BorderLayout.CENTER);

        setLayout(new java.awt.GridBagLayout());

        tblRisse.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                    {},
                    {},
                    {},
                    {}
                },
                new String[] {}));
        tblRisse.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblRisse.addFocusListener(new java.awt.event.FocusAdapter() {

                @Override
                public void focusLost(final java.awt.event.FocusEvent evt) {
                    tblRisseFocusLost(evt);
                }
            });
        scpRisse.setViewportView(tblRisse);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        add(scpRisse, gridBagConstraints);

        panMap.setMaximumSize(new java.awt.Dimension(300, 450));
        panMap.setMinimumSize(new java.awt.Dimension(300, 450));
        panMap.setOpaque(false);
        panMap.setPreferredSize(new java.awt.Dimension(300, 450));
        panMap.setLayout(new java.awt.GridBagLayout());

        mappingComponent.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        mappingComponent.setMaximumSize(new java.awt.Dimension(100, 100));
        mappingComponent.setMinimumSize(new java.awt.Dimension(100, 100));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panMap.add(mappingComponent, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        add(panMap, gridBagConstraints);

        pnlReport.setOpaque(false);
        pnlReport.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        pnlReport.add(flrGap, gridBagConstraints);

        lblProjectname.setText("Projektbezeichnung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlReport.add(lblProjectname, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlReport.add(txtProjectname, gridBagConstraints);

        lblJobnumber.setText("Auftragsnummer:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        pnlReport.add(lblJobnumber, gridBagConstraints);

        txtJobnumber.setMaximumSize(new java.awt.Dimension(250, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        pnlReport.add(txtJobnumber, gridBagConstraints);

        btnGenerateReport.setText("Erzeugen");
        btnGenerateReport.setToolTipText("Erzeugen");
        btnGenerateReport.setFocusPainted(false);
        btnGenerateReport.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnGenerateReportActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        pnlReport.add(btnGenerateReport, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        pnlReport.add(cmbType, gridBagConstraints);

        lblType.setText("Verfügbare Berichte:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 6, 6);
        pnlReport.add(lblType, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(pnlReport, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void tblRisseFocusLost(final java.awt.event.FocusEvent evt) { //GEN-FIRST:event_tblRisseFocusLost
        tblRisse.clearSelection();
        animateToOverview();
    }                                                                     //GEN-LAST:event_tblRisseFocusLost

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnGenerateReportActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnGenerateReportActionPerformed
        final Collection<CidsBean> selectedBaulasten = getSelectedBaulasten();

        if (selectedBaulasten.isEmpty()) {
            JOptionPane.showMessageDialog(
                StaticSwingTools.getParentFrame(this),
                "Bitte wählen Sie Baulasten zur Report-Generierung aus.",
                "Keine Baulasten gewählt",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    final Object typeObj = cmbType.getSelectedItem();
                    final BaulastenReportGenerator.Type type;
                    if (typeObj instanceof BaulastenReportGenerator.Type) {
                        type = (BaulastenReportGenerator.Type)typeObj;

                        try {
                            if (BillingPopup.doBilling(
                                            "bla",
                                            "no.yet",
                                            (Geometry)null,
                                            getConnectionContext(),
                                            new BillingProductGroupAmount("ea_bla", selectedBaulasten.size()))) {
                                if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(
                                                Alb_baulastAggregationRendererPanel.this)) {
                                    String projectname = txtProjectname.getText();
                                    if ((projectname == null) || (projectname.trim().length() == 0)) {
                                        projectname = "";
                                    }
                                    final Download download = BaulastenReportDownloadHelper.createDownload(
                                            type,
                                            selectedBaulasten,
                                            txtJobnumber.getText(),
                                            projectname,
                                            getConnectionContext());
                                    DownloadManager.instance().add(download);
                                }
                            }
                        } catch (Exception e) {
                            LOG.error("Error when trying to produce a alkis product", e);
                        }
                    } else {
                        LOG.info("Unknown type '" + typeObj + "' encountered. Skipping report generation.");
                    }
                    return null;
                }
            }.execute();
    } //GEN-LAST:event_btnGenerateReportActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected Collection<CidsBean> getSelectedBaulasten() {
        final Collection<CidsBean> result = new LinkedList<CidsBean>();
        final List<Integer> selectedIndexes = new ArrayList<Integer>();

        final TableModel tModel = tblRisse.getModel();
        for (int i = 0; i < tModel.getRowCount(); ++i) {
            final Object includedObj = tModel.getValueAt(i, 0);
            if ((includedObj instanceof Boolean) && (Boolean)includedObj) {
                selectedIndexes.add(i);
            }
        }

        Collections.sort(selectedIndexes, tableComparator);

        for (final Integer selectedIndex : selectedIndexes) {
            result.add(cidsBeans.get(selectedIndex));
        }

        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Collection<CidsBean> getCidsBeans() {
        return cidsBeans;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  beans  cidsBeans DOCUMENT ME!
     */
    @Override
    public void setCidsBeans(final Collection<CidsBean> beans) {
        this.cidsBeans = new ArrayList<CidsBean>(new LinkedHashSet<CidsBean>(beans)); // deduped
        featuresMM = new MultiMap();

        initMap();

        final List<Object[]> tableData = new ArrayList<Object[]>();
        for (final CidsBean baulastBean : cidsBeans) {
            tableData.add(cidsBean2Row(baulastBean));
        }

        tableModel = new BaulastblattTableModel(tableData.toArray(new Object[tableData.size()][]),
                AGR_COMLUMN_NAMES);
        tblRisse.setModel(tableModel);

        final TableColumnModel cModel = tblRisse.getColumnModel();
        for (int i = 0; i < cModel.getColumnCount(); ++i) {
            cModel.getColumn(i).setPreferredWidth(AGR_COMLUMN_WIDTH[i]);
        }

        final TableRowSorter tableSorter = ObjectRendererUtils.decorateTableWithSorter(tblRisse);
        final List<RowSorter.SortKey> sortKeys = new LinkedList<RowSorter.SortKey>();
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(3, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(4, SortOrder.DESCENDING));
        tableSorter.setSortKeys(sortKeys);

        final Collection<BaulastenReportGenerator.Type> items = new ArrayList<BaulastenReportGenerator.Type>();
        final boolean billingAllowed = BillingPopup.isBillingAllowed("bla", getConnectionContext());
        if (!ObjectRendererUtils.checkActionTag(REPORT_ACTION_TAG_BLATT, getConnectionContext())
                    && billingAllowed) {
            items.add(BaulastenReportGenerator.Type.TEXTBLATT);
        }
        if (!ObjectRendererUtils.checkActionTag(REPORT_ACTION_TAG_PLAN, getConnectionContext())
                    && billingAllowed) {
            items.add(BaulastenReportGenerator.Type.TEXTBLATT_PLAN);
        }
        if (!ObjectRendererUtils.checkActionTag(REPORT_ACTION_TAG_RASTER, getConnectionContext())
                    && billingAllowed) {
            items.add(BaulastenReportGenerator.Type.TEXTBLATT_PLAN_RASTER);
        }
        final boolean enabled = billingAllowed && !items.isEmpty();

        cmbType.setModel(new DefaultComboBoxModel(items.toArray(new BaulastenReportGenerator.Type[0])));
        cmbType.setEnabled(enabled);
        btnGenerateReport.setEnabled(enabled);
        txtJobnumber.setEnabled(enabled);
        txtProjectname.setEnabled(enabled);

        setTitle(null);
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    public void dispose() {
        mappingComponent.dispose();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   baulast  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected Object[] cidsBean2Row(final CidsBean baulast) {
        if (baulast != null) {
            final Object[] result = new Object[AGR_COMLUMN_NAMES.length];
            result[0] = Boolean.TRUE;
            final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            for (int i = 0; i < AGR_PROPERTY_NAMES.length; ++i) {
                final Object property = baulast.getProperty(AGR_PROPERTY_NAMES[i]);
                final String propertyString;
                if (property instanceof Boolean) {
                    propertyString = ((Boolean)property) ? "ja" : "nein";
                } else if (property instanceof Date) {
                    propertyString = sdf.format((Date)property);
                } else {
                    propertyString = ObjectRendererUtils.propertyPrettyPrint(property);
                }
                result[i + 1] = propertyString;
            }
            return result;
        }

        return new Object[0];
    }

    /**
     * DOCUMENT ME!
     */
    protected void initMap() {
        try {
            final ActiveLayerModel mappingModel = new ActiveLayerModel();
            mappingModel.setSrs(ClientAlkisConf.getInstance().getSrsService());

            final XBoundingBox box = boundingBoxFromPointList(cidsBeans);
            mappingModel.addHome(new XBoundingBox(
                    box.getX1(),
                    box.getY1(),
                    box.getX2(),
                    box.getY2(),
                    ClientAlkisConf.getInstance().getSrsService(),
                    true));
            final SimpleWMS swms = new SimpleWMS(new SimpleWmsGetMapUrl(
                        ClientAlkisConf.getInstance().getMapCallString()));
            swms.setName("Alb_Baulast");
            mappingModel.addLayer(swms);
            mappingComponent.setMappingModel(mappingModel);
            mappingComponent.setAnimationDuration(0);
            mappingComponent.gotoInitialBoundingBox();
            mappingComponent.setInteractionMode(MappingComponent.ZOOM);
            mappingComponent.unlock();

            final Collection<Feature> allFeatures = new ArrayList<Feature>();
            for (final CidsBean cidsBean : cidsBeans) {
                final Collection<Geometry> geomsBel = new ArrayList<Geometry>();
                final Collection<Geometry> geomsBeg = new ArrayList<Geometry>();
                final Collection<Geometry> geomsBoth = new ArrayList<Geometry>();

                for (final CidsBean bel : cidsBean.getBeanCollectionProperty("flurstuecke_belastet")) {
                    geomsBel.add((Geometry)bel.getProperty("fs_referenz.umschreibendes_rechteck"));
                }
                for (final CidsBean beg : cidsBean.getBeanCollectionProperty("flurstuecke_beguenstigt")) {
                    geomsBeg.add((Geometry)beg.getProperty("fs_referenz.umschreibendes_rechteck"));
                }
                geomsBoth.addAll(geomsBel);
                geomsBoth.addAll(geomsBeg);
                geomsBoth.retainAll(geomsBel);
                geomsBoth.retainAll(geomsBeg);

                geomsBel.removeAll(geomsBeg);
                geomsBeg.removeAll(geomsBel);

                for (final Geometry geomBel : geomsBel) {
                    final BLFeature blf = new BLFeature(geomBel, Type.BEL);
                    allFeatures.add(blf);
                    featuresMM.put(cidsBean, blf);
                }
                for (final Geometry geomBeg : geomsBeg) {
                    final BLFeature blf = new BLFeature(geomBeg, Type.BEG);
                    allFeatures.add(blf);
                    featuresMM.put(cidsBean, blf);
                }
                for (final Geometry geomBoth : geomsBoth) {
                    final BLFeature blf = new BLFeature(geomBoth, Type.BOTH);
                    allFeatures.add(blf);
                    featuresMM.put(cidsBean, blf);
                }
            }

            mappingComponent.getFeatureCollection().addFeatures(allFeatures);
            mappingComponent.setAnimationDuration(500);
        } catch (Exception e) {
            LOG.fatal(e, e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   baulasten  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected XBoundingBox boundingBoxFromPointList(final Collection<CidsBean> baulasten) {
        final List<Geometry> geometries = new LinkedList<Geometry>();

        for (final CidsBean baulast : baulasten) {
            try {
                Collection<CidsBean> fs_kickers;
                fs_kickers = CidsBeanSupport.getBeanCollectionFromProperty(baulast, "flurstuecke_belastet");
                for (final CidsBean fs_kicker : fs_kickers) {
                    final Geometry geom = (Geometry)fs_kicker.getProperty("fs_referenz.umschreibendes_rechteck");
                    if (geom != null) {
                        geometries.add(geom);
                    }
                }

                fs_kickers = CidsBeanSupport.getBeanCollectionFromProperty(baulast, "flurstuecke_beguenstigt");
                for (final CidsBean fs_kicker : fs_kickers) {
                    final Geometry geom = (Geometry)fs_kicker.getProperty("fs_referenz.umschreibendes_rechteck");
                    if (geom != null) {
                        geometries.add(geom);
                    }
                }
            } catch (Exception ex) {
                LOG.warn("Could not add geometry to create a bounding box.", ex);
            }
        }

        final GeometryCollection geoCollection = new GeometryCollection(geometries.toArray(
                    new Geometry[geometries.size()]),
                new GeometryFactory());

        return new XBoundingBox(geoCollection.getEnvelope().buffer(ClientAlkisConf.getInstance().getGeoBuffer()));
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public String getTitle() {
        return lblAgrTitle.getText();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  title  DOCUMENT ME!
     */
    @Override
    public void setTitle(final String title) {
        final String desc = "Baulasten";
//        final Collection<CidsBean> beans = cidsBeans;
//        if ((beans != null) && (beans.size() > 0)) {
//            desc += ": " + beans.size() + " ausgewählt, "
//                        + ((Alb_baulastAggregationRendererPanel_)panContent).getRowCount()
//                        + " Baulasten";
//        }
        lblAgrTitle.setText(desc);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public JComponent getTitleComponent() {
        return panTitle;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class TableSelectionListener implements ListSelectionListener {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  e  DOCUMENT ME!
         */
        @Override
        public void valueChanged(final ListSelectionEvent e) {
            if (e.getValueIsAdjusting() || (cidsBeans == null)) {
                return;
            }

            final List<Feature> featuresToSelect = new ArrayList();

            final int[] indexes = tblRisse.getSelectedRows();
            if ((indexes != null) && (indexes.length > 0)) {
                for (final int viewIdx : indexes) {
                    final int modelIdx = tblRisse.getRowSorter().convertRowIndexToModel(viewIdx);
                    if ((modelIdx > -1) && (modelIdx < cidsBeans.size())) {
                        final CidsBean selectedBean = cidsBeans.get(modelIdx);
                        featuresToSelect.addAll((Collection<Feature>)featuresMM.get(selectedBean));
                        break;
                    }
                }
            }

            for (final CidsBean key : (Set<CidsBean>)featuresMM.keySet()) {
                for (final Feature feature : (Collection<Feature>)featuresMM.get(key)) {
                    final BLFeature blFeature = (BLFeature)feature;
                    if (featuresToSelect.contains(feature)) {
                        switch (blFeature.getBlType()) {
                            case BEL: {
                                blFeature.setFillingPaint(BELASTET_COLOR);
                            }
                            break;
                            case BEG: {
                                blFeature.setFillingPaint(BEGUENSTIGT_COLOR);
                            }
                            break;
                            case BOTH: {
                                blFeature.setFillingPaint(BEIDES_COLOR);
                            }
                            break;
                        }
                        mappingComponent.getPFeatureHM().get(feature).moveToFront();
                    } else {
                        blFeature.setFillingPaint(new Color(0.5f, 0.5f, 0.5f, 0.5f));
                    }
                    mappingComponent.reconsiderFeature(feature);
                }
            }

            if ((!featuresToSelect.isEmpty())) {
                final Geometry[] geometries = new Geometry[featuresToSelect.size()];
                for (int index = 0; index < featuresToSelect.size(); index++) {
                    geometries[index] = (Polygon)featuresToSelect.get(index).getGeometry();
                }
                final GeometryCollection geoCollection = new GeometryCollection(geometries, new GeometryFactory());
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
                mappingComponent.gotoBoundingBox(boxToGoto, false, true, 500);
            } else {
                mappingComponent.gotoInitialBoundingBox();
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class BaulastblattTableModel extends DefaultTableModel {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new PointTableModel object.
         *
         * @param  data    DOCUMENT ME!
         * @param  labels  DOCUMENT ME!
         */
        public BaulastblattTableModel(final Object[][] data, final String[] labels) {
            super(data, labels);
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param   row     DOCUMENT ME!
         * @param   column  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        @Override
        public boolean isCellEditable(final int row, final int column) {
            return column == 0;
        }

        /**
         * DOCUMENT ME!
         *
         * @param   columnIndex  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        @Override
        public Class<?> getColumnClass(final int columnIndex) {
            if (columnIndex == 0) {
                return Boolean.class;
            } else {
                return super.getColumnClass(columnIndex);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class BLFeature extends PureNewFeature {

        //~ Instance fields ----------------------------------------------------

        private final Type blType;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new BLFeature object.
         *
         * @param  geom  DOCUMENT ME!
         * @param  type  DOCUMENT ME!
         */
        public BLFeature(final Geometry geom, final Type type) {
            super(geom);
            setFillingPaint(new Color(0.5f, 0.5f, 0.5f, 0.5f));
            this.blType = type;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Type getBlType() {
            return blType;
        }
    }
}
