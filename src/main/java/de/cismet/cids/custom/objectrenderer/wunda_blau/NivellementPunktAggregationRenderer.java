/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import Sirius.navigator.ui.ComponentRegistry;
import Sirius.navigator.ui.RequestsFullSizeComponent;

import Sirius.server.middleware.types.MetaObjectNode;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;

import java.awt.EventQueue;
import java.awt.geom.Rectangle2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import de.cismet.cids.custom.clientutils.ByteArrayActionDownload;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.objectrenderer.utils.alkis.ClientAlkisConf;
import de.cismet.cids.custom.objectrenderer.utils.billing.BillingPopup;
import de.cismet.cids.custom.utils.billing.BillingProductGroupAmount;
import de.cismet.cids.custom.wunda_blau.search.actions.NivPReportServerAction;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanAggregationRenderer;

import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

import de.cismet.cismap.navigatorplugin.CidsFeature;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.CismetThreadPool;

import de.cismet.tools.collections.TypeSafeCollections;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class NivellementPunktAggregationRenderer extends javax.swing.JPanel implements CidsBeanAggregationRenderer,
    RequestsFullSizeComponent,
    ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(NivellementPunktAggregationRenderer.class);

    private static final double BUFFER = 0.005;

    // Spaltenueberschriften
    private static final String[] AGR_COMLUMN_NAMES = new String[] {
            "Auswahl",
            "DGK-Blattnr.",
            "Lfd. Nr.",
            "Jahr",
            "Festlegungsart",
            "Lagebezeichnung"
        };
    // Namen der Properties -> Spalten
    private static final String[] AGR_PROPERTY_NAMES = new String[] {
            "dgk_blattnummer",
            "laufende_nummer",
            "messungsjahr",
            "festlegungsart",
            "lagebezeichnung"
        };

    private static final int[] AGR_COMLUMN_WIDTH = new int[] { 40, 70, 40, 30, 80, 200 };

    //~ Instance fields --------------------------------------------------------

    private List<CidsBean> cidsBeans;
    private String title = "";
    private PointTableModel tableModel;
    private Map<CidsBean, CidsFeature> features;
    private Comparator<Integer> tableComparator;
    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGenerateReport;
    private javax.swing.Box.Filler flrGap;
    private javax.swing.JLabel lblJobnumber;
    private javax.swing.JLabel lblProjectname;
    private de.cismet.cismap.commons.gui.MappingComponent mappingComponent;
    private javax.swing.JPanel panMap;
    private javax.swing.JPanel pnlReport;
    private javax.swing.JScrollPane scpPunkte;
    private javax.swing.JTable tblPunkte;
    private javax.swing.JTextField txtJobnumber;
    private javax.swing.JTextField txtProjectname;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new NivellementPunktAggregationRenderer object.
     */
    public NivellementPunktAggregationRenderer() {
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        initComponents();

        scpPunkte.getViewport().setOpaque(false);
        tblPunkte.getSelectionModel().addListSelectionListener(new TableSelectionListener());
        tableComparator = new TableModelIndexConvertingToViewIndexComparator((tblPunkte));

        final boolean billingAllowed = BillingPopup.isBillingAllowed("nivppdf", getConnectionContext());

        btnGenerateReport.setEnabled(billingAllowed);
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

        scpPunkte = new javax.swing.JScrollPane();
        tblPunkte = new javax.swing.JTable();
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

        addAncestorListener(new javax.swing.event.AncestorListener() {

                @Override
                public void ancestorMoved(final javax.swing.event.AncestorEvent evt) {
                }
                @Override
                public void ancestorAdded(final javax.swing.event.AncestorEvent evt) {
                    formAncestorAdded(evt);
                }
                @Override
                public void ancestorRemoved(final javax.swing.event.AncestorEvent evt) {
                }
            });
        setLayout(new java.awt.GridBagLayout());

        tblPunkte.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                    {},
                    {},
                    {},
                    {}
                },
                new String[] {}));
        tblPunkte.addFocusListener(new java.awt.event.FocusAdapter() {

                @Override
                public void focusLost(final java.awt.event.FocusEvent evt) {
                    tblPunkteFocusLost(evt);
                }
            });
        scpPunkte.setViewportView(tblPunkte);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        add(scpPunkte, gridBagConstraints);

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

        lblProjectname.setText(org.openide.util.NbBundle.getMessage(
                NivellementPunktAggregationRenderer.class,
                "NivellementPunktAggregationRenderer.lblProjectname.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlReport.add(lblProjectname, gridBagConstraints);

        txtProjectname.setText(org.openide.util.NbBundle.getMessage(
                NivellementPunktAggregationRenderer.class,
                "NivellementPunktAggregationRenderer.txtProjectname.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlReport.add(txtProjectname, gridBagConstraints);

        lblJobnumber.setText(org.openide.util.NbBundle.getMessage(
                NivellementPunktAggregationRenderer.class,
                "NivellementPunktAggregationRenderer.lblJobnumber.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        pnlReport.add(lblJobnumber, gridBagConstraints);

        txtJobnumber.setText(org.openide.util.NbBundle.getMessage(
                NivellementPunktAggregationRenderer.class,
                "NivellementPunktAggregationRenderer.txtJobnumber.text")); // NOI18N
        txtJobnumber.setMaximumSize(new java.awt.Dimension(250, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        pnlReport.add(txtJobnumber, gridBagConstraints);

        btnGenerateReport.setText(org.openide.util.NbBundle.getMessage(
                NivellementPunktAggregationRenderer.class,
                "NivellementPunktAggregationRenderer.btnGenerateReport.text")); // NOI18N
        btnGenerateReport.setFocusPainted(false);
        btnGenerateReport.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnGenerateReportActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        pnlReport.add(btnGenerateReport, gridBagConstraints);

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
    private void tblPunkteFocusLost(final java.awt.event.FocusEvent evt) { //GEN-FIRST:event_tblPunkteFocusLost
        tblPunkte.clearSelection();
        animateToOverview();
    }                                                                      //GEN-LAST:event_tblPunkteFocusLost

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnGenerateReportActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnGenerateReportActionPerformed
        final Collection<CidsBean> selectedNivellementPunkte = getSelectedNivellementPunkte();

        if (selectedNivellementPunkte.isEmpty()) {
            JOptionPane.showMessageDialog(
                StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(
                    NivellementPunktAggregationRenderer.class,
                    "NivellementPunktAggregationRenderer.btnGenerateReportActionPerformed(ActionEvent).emptySelection.message"),
                NbBundle.getMessage(
                    NivellementPunktAggregationRenderer.class,
                    "NivellementPunktAggregationRenderer.btnGenerateReportActionPerformed(ActionEvent).emptySelection.title"),
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            if (BillingPopup.doBilling(
                            "nivppdf",
                            "no.yet",
                            (Geometry)null,
                            getConnectionContext(),
                            new BillingProductGroupAmount("ea", selectedNivellementPunkte.size()))) {
                downloadReport(
                    selectedNivellementPunkte,
                    txtJobnumber.getText(),
                    txtProjectname.getText(),
                    getConnectionContext());
            }
        } catch (Exception e) {
            LOG.error("Error when trying to produce a alkis product", e);
            // Hier noch ein Fehlerdialog
        }
    } //GEN-LAST:event_btnGenerateReportActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  nivPoints          DOCUMENT ME!
     * @param  jobnumber          DOCUMENT ME!
     * @param  projectname        DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public static void downloadReport(final Collection<CidsBean> nivPoints,
            final String jobnumber,
            final String projectname,
            final ConnectionContext connectionContext) {
        if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(
                        ComponentRegistry.getRegistry().getDescriptionPane())) {
            final String jobname = DownloadManagerDialog.getInstance().getJobName();

            final Collection<MetaObjectNode> nivPMons = new ArrayList<MetaObjectNode>();
            for (final CidsBean nivPoint : nivPoints) {
                nivPMons.add(new MetaObjectNode(nivPoint));
            }

            final ServerActionParameter<Collection> sapMons = new ServerActionParameter<Collection>(
                    NivPReportServerAction.Parameter.POINT_MONS.toString(),
                    nivPMons);
            final ServerActionParameter<String> sapJobnumber = new ServerActionParameter<String>(
                    NivPReportServerAction.Parameter.JOBNUMBER.toString(),
                    jobnumber);
            final ServerActionParameter<String> sapProjectName = new ServerActionParameter<String>(
                    NivPReportServerAction.Parameter.PROJECTNAME.toString(),
                    projectname);

            DownloadManager.instance()
                    .add(new ByteArrayActionDownload(
                            NivPReportServerAction.TASK_NAME,
                            null,
                            new ServerActionParameter[] { sapMons, sapJobnumber, sapProjectName },
                            ((projectname == null) || (projectname.trim().length() == 0))
                                ? ((nivPoints.size() == 1) ? "Nivellement-Punkt" : "Nivellement-Punkte") : projectname,
                            jobname,
                            "nivp",
                            ".pdf",
                            connectionContext));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void formAncestorAdded(final javax.swing.event.AncestorEvent evt) { //GEN-FIRST:event_formAncestorAdded
        CismetThreadPool.execute(new Runnable() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        LOG.warn("Sleeping to wait for zooming to added features was interrupted.", ex);
                    }
                    EventQueue.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                animateToOverview();
                            }
                        });
                }
            });
    } //GEN-LAST:event_formAncestorAdded

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected Collection<CidsBean> getSelectedNivellementPunkte() {
        final Collection<CidsBean> result = new LinkedList<CidsBean>();
        final List<Integer> selectedIndexes = new ArrayList<Integer>();

        final TableModel tableModel = tblPunkte.getModel();
        for (int i = 0; i < tableModel.getRowCount(); ++i) {
            final Object includedObj = tableModel.getValueAt(i, 0);
            if ((includedObj instanceof Boolean) && (Boolean)includedObj) {
                selectedIndexes.add(Integer.valueOf(i));
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
     * @param  beans  DOCUMENT ME!
     */
    @Override
    public void setCidsBeans(final Collection<CidsBean> beans) {
        if (beans instanceof List) {
            this.cidsBeans = (List<CidsBean>)beans;
            features = new HashMap<CidsBean, CidsFeature>(beans.size());

            initMap();

            final List<Object[]> tableData = TypeSafeCollections.newArrayList();
            for (final CidsBean punktBean : cidsBeans) {
                tableData.add(cidsBean2Row(punktBean));
            }
            tableModel = new PointTableModel(tableData.toArray(new Object[tableData.size()][]), AGR_COMLUMN_NAMES);
            tblPunkte.setModel(tableModel);
            final TableColumnModel cModel = tblPunkte.getColumnModel();
            for (int i = 0; i < cModel.getColumnCount(); ++i) {
                cModel.getColumn(i).setPreferredWidth(AGR_COMLUMN_WIDTH[i]);
            }
            ObjectRendererUtils.decorateTableWithSorter(tblPunkte);
        }
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
     * @return  DOCUMENT ME!
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  title  DOCUMENT ME!
     */
    @Override
    public void setTitle(final String title) {
        String desc = "Punktliste";
        final Collection<CidsBean> beans = cidsBeans;
        if ((beans != null) && (beans.size() > 0)) {
            desc += " - " + beans.size() + " Nivellement-Punkte ausgewählt";
        }
        this.title = desc;
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
            swms.setName("Nivellement_Punkte");
            mappingModel.addLayer(swms);
            mappingComponent.setMappingModel(mappingModel);
            mappingComponent.setAnimationDuration(0);
            mappingComponent.gotoInitialBoundingBox();
            mappingComponent.setInteractionMode(MappingComponent.ZOOM);
            mappingComponent.unlock();

            for (final CidsBean cidsBean : cidsBeans) {
                final CidsFeature feature = new CidsFeature(cidsBean.getMetaObject());
                features.put(cidsBean, feature);
            }
            mappingComponent.getFeatureCollection().addFeatures(features.values());

//            animateToOverview();
        } catch (Exception e) {
            LOG.fatal(e, e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   nivellementPunkte  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected XBoundingBox boundingBoxFromPointList(final Collection<CidsBean> nivellementPunkte) {
        final List<Geometry> geometries = TypeSafeCollections.newArrayList();

        for (final CidsBean nivellementPunkt : nivellementPunkte) {
            try {
                geometries.add((Geometry)nivellementPunkt.getProperty("geometrie.geo_field"));
            } catch (Exception ex) {
                LOG.warn(ex, ex);
            }
        }

        final GeometryCollection geoCollection = new GeometryCollection(geometries.toArray(
                    new Geometry[geometries.size()]),
                new GeometryFactory());

        return new XBoundingBox(geoCollection.getEnvelope().buffer(ClientAlkisConf.getInstance().getGeoBuffer()));
    }

    /**
     * Extracts the date from a CidsBean into an Object[] -> table row. (Collection attributes are flatened to
     * comaseparated lists)
     *
     * @param   nivellementPunkt  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected Object[] cidsBean2Row(final CidsBean nivellementPunkt) {
        if (nivellementPunkt != null) {
            final Object[] result = new Object[AGR_COMLUMN_NAMES.length];
            result[0] = Boolean.TRUE;

            for (int i = 0; i < AGR_PROPERTY_NAMES.length; ++i) {
                final Object property = nivellementPunkt.getProperty(AGR_PROPERTY_NAMES[i]);
                final String propertyString;
                propertyString = ObjectRendererUtils.propertyPrettyPrint(property);
                result[i + 1] = propertyString;
            }
            return result;
        }

        return new Object[0];
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
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
            if (!e.getValueIsAdjusting() && (cidsBeans != null)) {
                final int[] indexes = tblPunkte.getSelectedRows();

                if ((indexes != null) && (indexes.length > 0)) {
                    for (final int viewIdx : indexes) {
                        final int modelIdx = tblPunkte.getRowSorter().convertRowIndexToModel(viewIdx);
                        if ((modelIdx > -1) && (modelIdx < cidsBeans.size())) {
                            final CidsBean selectedBean = cidsBeans.get(modelIdx);
                            final XBoundingBox boxToGoto = new XBoundingBox(features.get(selectedBean).getGeometry()
                                            .getEnvelope().buffer(ClientAlkisConf.getInstance().getGeoBuffer()));
                            boxToGoto.setX1(boxToGoto.getX1()
                                        - (ClientAlkisConf.getInstance().getGeoBufferMultiplier()
                                            * boxToGoto.getWidth()));
                            boxToGoto.setX2(boxToGoto.getX2()
                                        + (ClientAlkisConf.getInstance().getGeoBufferMultiplier()
                                            * boxToGoto.getWidth()));
                            boxToGoto.setY1(boxToGoto.getY1()
                                        - (ClientAlkisConf.getInstance().getGeoBufferMultiplier()
                                            * boxToGoto.getHeight()));
                            boxToGoto.setY2(boxToGoto.getY2()
                                        + (ClientAlkisConf.getInstance().getGeoBufferMultiplier()
                                            * boxToGoto.getHeight()));
                            mappingComponent.gotoBoundingBox(boxToGoto, false, true, 500);
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class PointTableModel extends DefaultTableModel {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new PointTableModel object.
         *
         * @param  data    DOCUMENT ME!
         * @param  labels  DOCUMENT ME!
         */
        public PointTableModel(final Object[][] data, final String[] labels) {
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
}
