/***************************************************
 *
 * cismet GmbH, Saarbruecken, Germany
 *
 *              ... and it just works.
 *
 ****************************************************/
/*
 *  Copyright (C) 2010 srichter
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * Alkis_pointAggregationRenderer.java
 *
 * Created on 03.03.2010, 09:45:18
 */
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import java.text.DecimalFormat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.objectrenderer.utils.alkis.AlkisUtils;
import de.cismet.cids.custom.utils.alkis.AlkisConstants;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanAggregationRenderer;

import de.cismet.cismap.commons.BoundingBox;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.FeatureCollection;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

import de.cismet.cismap.navigatorplugin.CidsFeature;

import de.cismet.tools.collections.TypeSafeCollections;
import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;
import de.cismet.tools.gui.downloadmanager.SingleDownload;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public final class AlkisPointAggregationRenderer extends javax.swing.JPanel implements CidsBeanAggregationRenderer {

    //~ Static fields/initializers ---------------------------------------------
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
            AlkisPointAggregationRenderer.class);
    // Spaltenueberschriften
    private static final String[] AGR_COMLUMN_NAMES = new String[]{
        "Auswahl",
        "Punktkennung",
        "Punktart",
        "Punktort"
    };
    // Spaltenbreiten
    private static final int[] AGR_COMLUMN_WIDTH = new int[]{40, 80, 200, 200};
    // Namen der Properties -> Spalten
    private static final String[] AGR_PROPERTY_NAMES = new String[]{"pointcode", "pointtype", "geom.geo_field"};
    // Formater fuer Hochwert/Rechtswert
    private static final NumberFormat HW_RW_NUMBER_FORMAT = new DecimalFormat("##########.###");
    // Modell fuer die Auswahlbox des produktformats
    private static final String PDF = "PDF";
    private static final String HTML = "HTML";
    private static final String TEXT = "TEXT";
    private static final ComboBoxModel PRODUCT_FORMATS_MODEL = new DefaultComboBoxModel(new String[]{PDF, HTML, TEXT});
    // Speichert Punkte ueber die Lebzeit eines Renderers hinaus
    private static final Set<CidsBean> gehaltenePunkte = TypeSafeCollections.newLinkedHashSet();
    //~ Instance fields --------------------------------------------------------
    private List<CidsBean> cidsBeans = null;
    private Collection<CidsBean> pureSelectionCidsBeans = null;
    private String title = "";
    private PointTableModel tableModel;
    private Map<CidsBean, CidsFeature> features;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCreate;
    private javax.swing.JButton btnRelease;
    private javax.swing.JButton btnRemember;
    private javax.swing.JComboBox cbProducts;
    private javax.swing.JLabel lblProductDescr;
    private de.cismet.cismap.commons.gui.MappingComponent mappingComponent;
    private javax.swing.JPanel panMap;
    private javax.swing.JPanel panProdukte;
    private javax.swing.JScrollPane scpAggregationTable;
    private javax.swing.JTable tblAggregation;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------
    /**
     * Creates new form Alkis_pointAggregationRenderer.
     */
    public AlkisPointAggregationRenderer() {
        initComponents();
        scpAggregationTable.getViewport().setOpaque(false);
        tblAggregation.getSelectionModel().addListSelectionListener(new TableSelectionListener());
        btnRelease.setEnabled(gehaltenePunkte.size() > 0);
        btnRemember.setVisible(false);
        btnRelease.setVisible(false);
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

        scpAggregationTable = new javax.swing.JScrollPane();
        tblAggregation = new javax.swing.JTable();
        panProdukte = new javax.swing.JPanel();
        cbProducts = new javax.swing.JComboBox();
        btnCreate = new javax.swing.JButton();
        lblProductDescr = new javax.swing.JLabel();
        btnRemember = new javax.swing.JButton();
        btnRelease = new javax.swing.JButton();
        panMap = new javax.swing.JPanel();
        mappingComponent = new de.cismet.cismap.commons.gui.MappingComponent();

        setLayout(new java.awt.BorderLayout());

        tblAggregation.setOpaque(false);
        tblAggregation.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tblAggregationFocusLost(evt);
            }
        });
        scpAggregationTable.setViewportView(tblAggregation);

        add(scpAggregationTable, java.awt.BorderLayout.CENTER);

        panProdukte.setOpaque(false);
        panProdukte.setLayout(new java.awt.GridBagLayout());

        cbProducts.setModel(PRODUCT_FORMATS_MODEL);
        cbProducts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbProductsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(15, 10, 5, 10);
        panProdukte.add(cbProducts, gridBagConstraints);

        btnCreate.setText("Erzeugen");
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(15, 10, 5, 5);
        panProdukte.add(btnCreate, gridBagConstraints);

        lblProductDescr.setText("Listendokument für markierte Punkte:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(15, 10, 5, 10);
        panProdukte.add(lblProductDescr, gridBagConstraints);

        btnRemember.setText("Merken");
        btnRemember.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRememberActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 5, 5);
        panProdukte.add(btnRemember, gridBagConstraints);

        btnRelease.setText("Vergessen");
        btnRelease.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReleaseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 5, 10);
        panProdukte.add(btnRelease, gridBagConstraints);

        add(panProdukte, java.awt.BorderLayout.SOUTH);

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
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        panMap.add(mappingComponent, gridBagConstraints);

        add(panMap, java.awt.BorderLayout.EAST);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnCreateActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
        if (!ObjectRendererUtils.checkActionTag(AlkisPointRenderer.PRODUCT_ACTION_TAG_PUNKTLISTE)) {
            JOptionPane.showMessageDialog(this, "Sie besitzen keine Berechtigung zur Erzeugung dieses Produkts!");
            return;
        }
        
        final String punktListenString = getPunktlistenStringForChosenPoints();
        final String format = cbProducts.getSelectedItem().toString();
        final String code;
        if (PDF.equals(format)) {
            code = AlkisUtils.PRODUCTS.PUNKTLISTE_PDF;
        } else if (HTML.equals(format)) {
            code = AlkisUtils.PRODUCTS.PUNKTLISTE_HTML;
        } else {
            code = AlkisUtils.PRODUCTS.PUNKTLISTE_TXT;
        }
        if (punktListenString.length() > 3) {
            if (PDF.equals(format)) {
                URL url = null;
                if (code != null && code.length() > 0) {
                    try {
                        url = AlkisUtils.PRODUCTS.productListenNachweisUrl(punktListenString, code);
                    } catch (MalformedURLException ex) {
                        ObjectRendererUtils.showExceptionWindowToUser(
                                "Fehler beim Aufruf des Produkts: " + code,
                                ex,
                                AlkisPointAggregationRenderer.this);
                        log.error("The URL to download product '" + code + "' (actionTag: " + AlkisPointRenderer.PRODUCT_ACTION_TAG_PUNKTLISTE + ") could not be constructed.", ex);
                    }
                }
                if (url != null) {
                    if (!DownloadManagerDialog.showAskingForUserTitle(StaticSwingTools.getParentFrame(this))) {
                        return;
                    }
                    final String jobname = DownloadManagerDialog.getJobname();
                    if (jobname == null || jobname.trim().length() <= 0) {
                        return;
                    }

                    final SingleDownload download = new SingleDownload(url, "", jobname, "Punktnachweis", code, ".pdf");
                    DownloadManager.instance().add(download);
                }
            } else {
                AlkisUtils.PRODUCTS.productListenNachweis(punktListenString, code);
            }
        }
    }//GEN-LAST:event_btnCreateActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnReleaseActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReleaseActionPerformed
        gehaltenePunkte.clear();
        setCidsBeans(pureSelectionCidsBeans);
        btnRelease.setEnabled(false);
    }//GEN-LAST:event_btnReleaseActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRememberActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRememberActionPerformed
        gehaltenePunkte.addAll(cidsBeans);
        btnRelease.setEnabled(true);
    }//GEN-LAST:event_btnRememberActionPerformed

    private void cbProductsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbProductsActionPerformed
        if(evt != null && evt.getSource() instanceof JComboBox && evt.getSource().equals(cbProducts)) {
            if(cbProducts.getSelectedItem().equals(PDF)) {
                btnCreate.setEnabled(DownloadManager.instance().isEnabled());
            } else {
                btnCreate.setEnabled(true);
            }
        }
    }//GEN-LAST:event_cbProductsActionPerformed

    private void tblAggregationFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblAggregationFocusLost
        mappingComponent.gotoInitialBoundingBox();
        tblAggregation.clearSelection();
    }//GEN-LAST:event_tblAggregationFocusLost

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getPunktlistenStringForChosenPoints() {
        final StringBuffer punktListeString = new StringBuffer();
        final TableModel tModel = tblAggregation.getModel();
        for (int i = 0; i < tModel.getRowCount(); ++i) {
            final Object includedObj = tModel.getValueAt(i, 0);
            if ((includedObj instanceof Boolean) && (Boolean) includedObj) {
                final CidsBean selectedBean = cidsBeans.get(i);
                if (punktListeString.length() > 0) {
                    punktListeString.append(",");
                }
                punktListeString.append(AlkisUtils.PRODUCTS.getPointDataForProduct(selectedBean));
            }
        }
        return punktListeString.toString();
    }

    @Override
    public Collection<CidsBean> getCidsBeans() {
        return cidsBeans;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(final String title) {
        String desc = "Punktliste";
        final Collection<CidsBean> beans = cidsBeans;
        if ((beans != null) && (beans.size() > 0)) {
            desc += " - " + beans.size() + " Punkte ausgewählt";
        }
        this.title = desc;
    }

    @Override
    public void setCidsBeans(final Collection<CidsBean> beans) {
        if (beans instanceof List) {
            pureSelectionCidsBeans = beans;
            if (gehaltenePunkte.size() > 0) {
                gehaltenePunkte.addAll(beans);
                this.cidsBeans = Arrays.asList(gehaltenePunkte.toArray(new CidsBean[gehaltenePunkte.size()]));
            } else {
                this.cidsBeans = (List<CidsBean>) beans;
            }
            features = new HashMap<CidsBean, CidsFeature>(beans.size());
            initMap();
            final List<Object[]> tableData = TypeSafeCollections.newArrayList();
            for (final CidsBean punktBean : cidsBeans) {
                tableData.add(cidsBean2Row(punktBean));
            }
            tableModel = new PointTableModel(tableData.toArray(new Object[tableData.size()][]), AGR_COMLUMN_NAMES);
            tblAggregation.setModel(tableModel);
            final TableColumnModel cModel = tblAggregation.getColumnModel();
            for (int i = 0; i < cModel.getColumnCount(); ++i) {
                cModel.getColumn(i).setPreferredWidth(AGR_COMLUMN_WIDTH[i]);
            }
            ObjectRendererUtils.decorateTableWithSorter(tblAggregation);
        }
        setTitle(null);
        
        if(PDF.equals(cbProducts.getSelectedItem())) {
            btnCreate.setEnabled(DownloadManager.instance().isEnabled());
        }
    }

    /**
     * Extracts the date from a CidsBean into an Object[] -> table row. (Collection attributes are flatened to
     * comaseparated lists)
     *
     * @param   baulastBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Object[] cidsBean2Row(final CidsBean baulastBean) {
        if (baulastBean != null) {
            final Object[] result = new Object[AGR_COMLUMN_NAMES.length];
            result[0] = Boolean.TRUE;
            for (int i = 0; i < AGR_PROPERTY_NAMES.length; ++i) {
                final Object property = baulastBean.getProperty(AGR_PROPERTY_NAMES[i]);
                String propertyString;
                if (property instanceof Point) {
                    final Point point = (Point) property;
                    propertyString = "RW: " + HW_RW_NUMBER_FORMAT.format(point.getX()) + "; HW: "
                            + HW_RW_NUMBER_FORMAT.format(point.getY());
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
    private void initMap() {
        try {
            final ActiveLayerModel mappingModel = new ActiveLayerModel();
            mappingModel.setSrs(AlkisConstants.COMMONS.SRS_SERVICE);
            final BoundingBox box = boundingBoxFromPointList(cidsBeans);
            mappingModel.addHome(new XBoundingBox(
                    box.getX1(),
                    box.getY1(),
                    box.getX2(),
                    box.getY2(),
                    AlkisConstants.COMMONS.SRS_SERVICE,
                    true));
            final SimpleWMS swms = new SimpleWMS(new SimpleWmsGetMapUrl(AlkisConstants.COMMONS.MAP_CALL_STRING));
            swms.setName("Alkis_Points");
            mappingModel.addLayer(swms);
            mappingComponent.setMappingModel(mappingModel);
            final int duration = mappingComponent.getAnimationDuration();
            mappingComponent.setAnimationDuration(0);
            mappingComponent.gotoInitialBoundingBox();
            mappingComponent.setInteractionMode(MappingComponent.ZOOM);
            mappingComponent.unlock();
            // finally when all configurations are done ...
            mappingComponent.setInteractionMode("MUTE");
//            mappingComponent.addCustomInputListener("MUTE", new PBasicInputEventHandler() {
//
//                @Override
//                public void mouseClicked(PInputEvent evt) {
//                    try {
//                        if (evt.getClickCount() > 1) {
////                            if (realLandParcelMetaObjectsCache == null) {
////                                CismetThreadPool.execute(new GeomQueryWorker());
////                            } else {
////                                switchToMapAndShowGeometries();
////                            }
//                        }
//                    } catch (Exception ex) {
//                        log.error(ex, ex);
//                    }
//                }
//            });
//            mappingComponent.setInteractionMode("MUTE");
            for(final CidsBean cidsBean : cidsBeans) {
                final CidsFeature feature = new CidsFeature(cidsBean.getMetaObject());
                features.put(cidsBean, feature);
            }
            mappingComponent.getFeatureCollection().addFeatures(features.values());
            mappingComponent.setAnimationDuration(duration);
        } catch (Throwable t) {
            log.fatal(t, t);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   lpList  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private XBoundingBox boundingBoxFromPointList(final Collection<CidsBean> lpList) {
        XBoundingBox result = null;
        final List<Geometry> allGeomList = TypeSafeCollections.newArrayList();
        
        for (final CidsBean parcel : lpList) {
            try {
                allGeomList.add((Geometry) parcel.getProperty("geom.geo_field"));
            } catch (Exception ex) {
                log.warn(ex, ex);
            }
        }
        final GeometryCollection geoCollection = new GeometryCollection(allGeomList.toArray(
                new Geometry[allGeomList.size()]),
                new GeometryFactory());
        
        result = new XBoundingBox(geoCollection);
        
        result.setX1(result.getX1() - 0.05 * result.getWidth());
        result.setX2(result.getX2() + 0.05 * result.getWidth());
        result.setY1(result.getY1() - 0.10 * result.getHeight());
        result.setY2(result.getY2() + 0.05 * result.getHeight());
        
        return result;
    }

    @Override
    public void dispose() {
        mappingComponent.dispose();
    }

    //~ Inner Classes ----------------------------------------------------------
    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    final class TableSelectionListener implements ListSelectionListener {

        //~ Methods ------------------------------------------------------------
        @Override
        public void valueChanged(final ListSelectionEvent e) {
            if (!e.getValueIsAdjusting() && (cidsBeans != null)) {
                final int[] indexes = tblAggregation.getSelectedRows();
                final FeatureCollection mapFC = mappingComponent.getFeatureCollection();
                //mapFC.removeAllFeatures();
                if ((indexes != null) && (indexes.length > 0)) {
                    for (final int viewIdx : indexes) {
                        final int modelIdx = tblAggregation.getRowSorter().convertRowIndexToModel(viewIdx);
                        if ((modelIdx > -1) && (modelIdx < cidsBeans.size())) {
                            final CidsBean selectedBean = cidsBeans.get(modelIdx);
                            //mapFC.addFeature(new CidsFeature(selectedBean.getMetaObject()));
                            mappingComponent.gotoBoundingBox(new XBoundingBox(features.get(selectedBean).getGeometry()), false, true, 500);
                            break;
                        }
                    }
                    //mappingComponent.zoomToFeatureCollection();
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    static final class PointTableModel extends DefaultTableModel {

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
        @Override
        public boolean isCellEditable(final int row, final int column) {
            return column == 0;
        }

        @Override
        public Class<?> getColumnClass(final int columnIndex) {
            if (columnIndex == 0) {
                return Boolean.class;
            } else {
                return super.getColumnClass(columnIndex);
            }
        }
    }
//    static final class PointTableModel extends AbstractTableModel {
//
//        private final List<Object[]> data;
//        private final String[] labels;
//
//        public PointTableModel(Object[][] data, String[] labels) {
//            this.data = Arrays.asList(data);
//            this.labels = labels;
//        }
//
//        @Override
//        public String getColumnName(int column) {
//            return labels[column];
//        }
//
//        @Override
//        public boolean isCellEditable(int row, int column) {
//            return column == 0;
//        }
//
//        @Override
//        public Class<?> getColumnClass(int columnIndex) {
//            if (columnIndex == 0) {
//                return Boolean.class;
//            } else {
//                return super.getColumnClass(columnIndex);
//            }
//        }
//
//        @Override
//        public int getRowCount() {
//            return data.size();
//        }
//
//        @Override
//        public int getColumnCount() {
//            return labels.length;
//        }
//
//        @Override
//        public Object getValueAt(int rowIndex, int columnIndex) {
//            return data.get(rowIndex)[columnIndex];
//        }
//
//        public void removeRow(int row) {
//            data.remove(row);
//            fireTableRowsDeleted(row, row);
//        }
//    }
}
