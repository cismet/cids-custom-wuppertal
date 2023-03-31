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
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;
import Sirius.navigator.ui.RequestsFullSizeComponent;

import Sirius.server.localserver.attribute.MemberAttributeInfo;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.clientutils.ByteArrayActionDownload;
import de.cismet.cids.custom.objectrenderer.utils.alkis.ClientAlkisConf;
import de.cismet.cids.custom.reports.wunda_blau.MauernReportGenerator;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.search.QuerySearchResultsAction;

import de.cismet.cids.server.actions.CsvExportServerAction;
import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanAggregationRenderer;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.StyledFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.gui.piccolo.FeatureAnnotationSymbol;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class MauerAggregationRenderer extends javax.swing.JPanel implements CidsBeanAggregationRenderer,
    RequestsFullSizeComponent,
    ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(MauerAggregationRenderer.class);
    private static final int COLUMNS = 4;
    private static final Color[] COLORS = new Color[] {
            new Color(247, 150, 70, 192),
            new Color(155, 187, 89, 192),
            new Color(128, 100, 162, 192),
            new Color(75, 172, 198, 192),
            new Color(192, 80, 77, 192)
        };

    //~ Instance fields --------------------------------------------------------

    private Collection<CidsBean> cidsBeans;
    private List<CidsBeanWrapper> cidsBeanWrappers;
    private CidsBeanWrapper selectedCidsBeanWrapper;
    private MauerTableModel tableModel;
    private MappingComponent map;
    private final Collection<Feature> pointFeatures = new LinkedList<>();
    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    private final QuerySearchResultsAction csvAction = new QuerySearchResultsAction() {

            @Override
            public String getName() {
                return "nach CSV exportieren";
            }

            @Override
            public void doAction() {
                final String title = cidsBeansTableActionPanel1.getMetaClass().getName();

                if (DownloadManagerDialog.showAskingForUserTitle(
                                StaticSwingTools.getParentFrame(MauerAggregationRenderer.this))) {
                    final List<String> columnNames = new ArrayList<>(
                            cidsBeansTableActionPanel1.getAttributeNames().size());
                    final List<String> fields = new ArrayList<>(
                            cidsBeansTableActionPanel1.getAttributeNames().size());
                    final List<String> keys = cidsBeansTableActionPanel1.getKeys();
                    if (keys != null) {
                        for (final String attrKey : keys) {
                            final MemberAttributeInfo mai = (MemberAttributeInfo)
                                cidsBeansTableActionPanel1.getMetaClass().getMemberAttributeInfos().get(attrKey);
                            columnNames.add(cidsBeansTableActionPanel1.getAttributeNames().get(attrKey));
                            fields.add(mai.getFieldName());
                        }
                    }

                    final List<MetaObjectNode> mons = new ArrayList<>();
                    for (final CidsBean cidsBean : cidsBeansTableActionPanel1.getCidsBeans()) {
                        mons.add(new MetaObjectNode(cidsBean));
                    }

                    final ServerActionParameter[] params = new ServerActionParameter[] {
                            new ServerActionParameter<>(
                                CsvExportServerAction.ParameterType.COLUMN_NAMES.toString(),
                                columnNames),
                            new ServerActionParameter<>(
                                CsvExportServerAction.ParameterType.FIELDS.toString(),
                                fields),
                            new ServerActionParameter<>(
                                CsvExportServerAction.ParameterType.MONS.toString(),
                                mons),
                            new ServerActionParameter<>(
                                CsvExportServerAction.ParameterType.DATE_FORMAT.toString(),
                                "dd.MM.yy"),
                            new ServerActionParameter<>(
                                CsvExportServerAction.ParameterType.BOOLEAN_YES.toString(),
                                "ja"),
                            new ServerActionParameter<>(
                                CsvExportServerAction.ParameterType.BOOLEAN_NO.toString(),
                                "nein"),
                            new ServerActionParameter<>(
                                CsvExportServerAction.ParameterType.DISTINCT_ON.toString(),
                                "id"),
                            new ServerActionParameter<>(
                                CsvExportServerAction.ParameterType.CHARSET.toString(),
                                "LATIN9"),
                        };
                    DownloadManager.instance()
                            .add(
                                new ByteArrayActionDownload(
                                    "WUNDA_BLAU",
                                    CsvExportServerAction.TASKNAME,
                                    cidsBeansTableActionPanel1.getMetaClass().getTableName(),
                                    params,
                                    title,
                                    DownloadManagerDialog.getInstance().getJobName(),
                                    title,
                                    ".csv",
                                    ConnectionContext.createDeprecated()));
                    final DownloadManagerDialog downloadManagerDialog = DownloadManagerDialog.getInstance();
                    StaticSwingTools.showDialog(
                        StaticSwingTools.getParentFrame(MauerAggregationRenderer.this),
                        downloadManagerDialog,
                        true);
                }
            }
        };

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.cismet.cids.search.CidsBeansTableActionPanel cidsBeansTableActionPanel1;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private org.jdesktop.swingx.JXHyperlink jxlHauptinfo;
    private org.jdesktop.swingx.JXHyperlink jxlKatasterblatt;
    private org.jdesktop.swingx.JXHyperlink jxlKatasterblatt1;
    private javax.swing.JLabel lblHeaderMauern;
    private javax.swing.JLabel lblheaderProdutke;
    private javax.swing.JLabel lblheaderProdutke1;
    private de.cismet.tools.gui.SemiRoundedPanel pnlHeaderMauern;
    private de.cismet.tools.gui.SemiRoundedPanel pnlHeaderProducts;
    private de.cismet.tools.gui.SemiRoundedPanel pnlHeaderProducts1;
    private javax.swing.JPanel pnlMap;
    private de.cismet.tools.gui.RoundedPanel pnlMauern;
    private de.cismet.tools.gui.RoundedPanel pnlProducts;
    private de.cismet.tools.gui.RoundedPanel pnlProducts1;
    private javax.swing.JScrollPane scpMauern;
    private javax.swing.JTable tblMauern;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form MauerAggregationRenderer.
     */
    public MauerAggregationRenderer() {
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        tableModel = new MauerTableModel();
        initComponents();
        map = new MappingComponent();
        pnlMap.setLayout(new BorderLayout());
        pnlMap.add(map, BorderLayout.CENTER);
        tblMauern.setDefaultRenderer(Color.class, new ColorRenderer());
        tblMauern.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

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

        boolean rechteManagement = false;

        try {
            rechteManagement = SessionManager.getProxy()
                        .hasConfigAttr(SessionManager.getSession().getUser(),
                                "mauerObjektRechteManagement",
                                getConnectionContext());
        } catch (final ConnectionException ex) {
            LOG.error(ex, ex);
        }
        pnlHeaderProducts1.setVisible(rechteManagement);
        jDialog1.pack();
        try {
            cidsBeansTableActionPanel1.setMetaClass(CidsBean.getMetaClassFromTableName(
                    "WUNDA_BLAU",
                    "VIEW_MAUER_EXPORT",
                    getConnectionContext()));
        } catch (final Exception ex) {
            LOG.fatal(ex, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jDialog1 = new javax.swing.JDialog();
        jPanel1 = new javax.swing.JPanel();
        cidsBeansTableActionPanel1 = new de.cismet.cids.search.CidsBeansTableActionPanel(Arrays.asList(csvAction),
                true);
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        pnlMap = new javax.swing.JPanel();
        pnlMauern = new de.cismet.tools.gui.RoundedPanel();
        pnlHeaderMauern = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeaderMauern = new javax.swing.JLabel();
        scpMauern = new javax.swing.JScrollPane();
        tblMauern = new javax.swing.JTable();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        pnlProducts = new de.cismet.tools.gui.RoundedPanel();
        pnlHeaderProducts = new de.cismet.tools.gui.SemiRoundedPanel();
        lblheaderProdutke = new javax.swing.JLabel();
        jxlKatasterblatt = new org.jdesktop.swingx.JXHyperlink();
        jxlHauptinfo = new org.jdesktop.swingx.JXHyperlink();
        pnlProducts1 = new de.cismet.tools.gui.RoundedPanel();
        pnlHeaderProducts1 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblheaderProdutke1 = new javax.swing.JLabel();
        jxlKatasterblatt1 = new org.jdesktop.swingx.JXHyperlink();

        jDialog1.setTitle(org.openide.util.NbBundle.getMessage(
                MauerAggregationRenderer.class,
                "MauerAggregationRenderer.jDialog1.title")); // NOI18N
        jDialog1.setModal(true);
        jDialog1.getContentPane().setLayout(new java.awt.CardLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        cidsBeansTableActionPanel1.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel1.add(cidsBeansTableActionPanel1, gridBagConstraints);

        jDialog1.getContentPane().add(jPanel1, "exporter");

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(org.openide.util.NbBundle.getMessage(
                MauerAggregationRenderer.class,
                "MauerAggregationRenderer.jLabel1.text")); // NOI18N
        jPanel2.add(jLabel1, new java.awt.GridBagConstraints());

        jDialog1.getContentPane().add(jPanel2, "loader");

        setLayout(new java.awt.GridBagLayout());

        pnlMap.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlMap.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        add(pnlMap, gridBagConstraints);

        pnlMauern.setPreferredSize(new java.awt.Dimension(470, 439));
        pnlMauern.setLayout(new java.awt.GridBagLayout());

        pnlHeaderMauern.setBackground(java.awt.Color.darkGray);

        lblHeaderMauern.setForeground(new java.awt.Color(255, 255, 255));
        lblHeaderMauern.setText(org.openide.util.NbBundle.getMessage(
                MauerAggregationRenderer.class,
                "MauerAggregationRenderer.lblHeaderMauern.text")); // NOI18N

        final javax.swing.GroupLayout pnlHeaderMauernLayout = new javax.swing.GroupLayout(pnlHeaderMauern);
        pnlHeaderMauern.setLayout(pnlHeaderMauernLayout);
        pnlHeaderMauernLayout.setHorizontalGroup(
            pnlHeaderMauernLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                168,
                Short.MAX_VALUE).addGroup(
                pnlHeaderMauernLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                    pnlHeaderMauernLayout.createSequentialGroup().addGap(0, 23, Short.MAX_VALUE).addComponent(
                        lblHeaderMauern).addGap(0, 23, Short.MAX_VALUE))));
        pnlHeaderMauernLayout.setVerticalGroup(
            pnlHeaderMauernLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                17,
                Short.MAX_VALUE).addGroup(
                pnlHeaderMauernLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                    pnlHeaderMauernLayout.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE).addComponent(
                        lblHeaderMauern).addGap(0, 0, Short.MAX_VALUE))));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        pnlMauern.add(pnlHeaderMauern, gridBagConstraints);

        scpMauern.setPreferredSize(new java.awt.Dimension(250, 402));

        tblMauern.setModel(tableModel);
        tblMauern.setShowVerticalLines(false);
        tblMauern.addFocusListener(new java.awt.event.FocusAdapter() {

                @Override
                public void focusLost(final java.awt.event.FocusEvent evt) {
                    tblMauernFocusLost(evt);
                }
            });
        scpMauern.setViewportView(tblMauern);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.35;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnlMauern.add(scpMauern, gridBagConstraints);
        pnlMauern.add(jLayeredPane1, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        add(pnlMauern, gridBagConstraints);

        pnlProducts.setLayout(new java.awt.GridBagLayout());

        pnlHeaderProducts.setBackground(java.awt.Color.darkGray);

        lblheaderProdutke.setForeground(new java.awt.Color(255, 255, 255));
        lblheaderProdutke.setText(org.openide.util.NbBundle.getMessage(
                MauerAggregationRenderer.class,
                "MauerAggregationRenderer.lblheaderProdutke.text")); // NOI18N

        final javax.swing.GroupLayout pnlHeaderProductsLayout = new javax.swing.GroupLayout(pnlHeaderProducts);
        pnlHeaderProducts.setLayout(pnlHeaderProductsLayout);
        pnlHeaderProductsLayout.setHorizontalGroup(
            pnlHeaderProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                169,
                Short.MAX_VALUE).addGroup(
                pnlHeaderProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                    pnlHeaderProductsLayout.createSequentialGroup().addGap(0, 56, Short.MAX_VALUE).addComponent(
                        lblheaderProdutke).addGap(0, 56, Short.MAX_VALUE))));
        pnlHeaderProductsLayout.setVerticalGroup(
            pnlHeaderProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                17,
                Short.MAX_VALUE).addGroup(
                pnlHeaderProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                    pnlHeaderProductsLayout.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE).addComponent(
                        lblheaderProdutke).addGap(0, 0, Short.MAX_VALUE))));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlProducts.add(pnlHeaderProducts, gridBagConstraints);

        jxlKatasterblatt.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/pdf.png"))); // NOI18N
        jxlKatasterblatt.setText(org.openide.util.NbBundle.getMessage(
                MauerAggregationRenderer.class,
                "MauerAggregationRenderer.jxlKatasterblatt.text"));               // NOI18N
        jxlKatasterblatt.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jxlKatasterblattActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(7, 10, 10, 10);
        pnlProducts.add(jxlKatasterblatt, gridBagConstraints);

        jxlHauptinfo.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/page_white_excel.png"))); // NOI18N
        jxlHauptinfo.setText(org.openide.util.NbBundle.getMessage(
                MauerAggregationRenderer.class,
                "MauerAggregationRenderer.jxlHauptinfo.text"));                                // NOI18N
        jxlHauptinfo.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jxlHauptinfoActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(7, 10, 10, 10);
        pnlProducts.add(jxlHauptinfo, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        add(pnlProducts, gridBagConstraints);

        pnlProducts1.setLayout(new java.awt.GridBagLayout());

        pnlHeaderProducts1.setBackground(java.awt.Color.darkGray);

        lblheaderProdutke1.setForeground(new java.awt.Color(255, 255, 255));
        lblheaderProdutke1.setText(org.openide.util.NbBundle.getMessage(
                MauerAggregationRenderer.class,
                "MauerAggregationRenderer.lblheaderProdutke1.text")); // NOI18N

        final javax.swing.GroupLayout pnlHeaderProducts1Layout = new javax.swing.GroupLayout(pnlHeaderProducts1);
        pnlHeaderProducts1.setLayout(pnlHeaderProducts1Layout);
        pnlHeaderProducts1Layout.setHorizontalGroup(
            pnlHeaderProducts1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                169,
                Short.MAX_VALUE).addGroup(
                pnlHeaderProducts1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                    pnlHeaderProducts1Layout.createSequentialGroup().addGap(0, 62, Short.MAX_VALUE).addComponent(
                        lblheaderProdutke1).addGap(0, 62, Short.MAX_VALUE))));
        pnlHeaderProducts1Layout.setVerticalGroup(
            pnlHeaderProducts1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                16,
                Short.MAX_VALUE).addGroup(
                pnlHeaderProducts1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                    pnlHeaderProducts1Layout.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE).addComponent(
                        lblheaderProdutke1).addGap(0, 0, Short.MAX_VALUE))));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlProducts1.add(pnlHeaderProducts1, gridBagConstraints);

        jxlKatasterblatt1.setText(org.openide.util.NbBundle.getMessage(
                MauerAggregationRenderer.class,
                "MauerAggregationRenderer.jxlKatasterblatt1.text")); // NOI18N
        jxlKatasterblatt1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jxlKatasterblatt1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(7, 10, 10, 10);
        pnlProducts1.add(jxlKatasterblatt1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        add(pnlProducts1, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxlKatasterblattActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jxlKatasterblattActionPerformed
        final List<CidsBean> reportBeans = new LinkedList<CidsBean>();
        for (final CidsBeanWrapper beanWrapper : cidsBeanWrappers) {
            if (beanWrapper.isSelected()) {
                reportBeans.add(beanWrapper.cidsBean);
            }
        }
        MauernReportGenerator.generateKatasterBlatt(reportBeans, MauerAggregationRenderer.this, getConnectionContext());
    }                                                                                    //GEN-LAST:event_jxlKatasterblattActionPerformed
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Collection<CidsBean> getSelectedBeans() {
        final List<CidsBean> reportBeans = new LinkedList<>();
        for (final CidsBeanWrapper beanWrapper : cidsBeanWrappers) {
            if (beanWrapper.isSelected()) {
                reportBeans.add(beanWrapper.cidsBean);
            }
        }
        return reportBeans;
    }
    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxlHauptinfoActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jxlHauptinfoActionPerformed
        if (cidsBeans != null) {
            ((CardLayout)jDialog1.getContentPane().getLayout()).show(jDialog1.getContentPane(), "loader");
            final Collection<CidsBean> selectedBeans = getSelectedBeans();
            new SwingWorker<List<CidsBean>, Void>() {

                    @Override
                    protected List<CidsBean> doInBackground() throws Exception {
                        final List<CidsBean> viewBeans = new ArrayList<>();
                        for (final CidsBean cidsBean : selectedBeans) {
                            if (cidsBean != null) {
                                final MetaObject mo = cidsBean.getMetaObject();
                                final MetaObject viewMo = SessionManager.getConnection()
                                            .getMetaObject(SessionManager.getSession().getUser(),
                                                mo.getId(),
                                                cidsBeansTableActionPanel1.getMetaClass().getId(),
                                                cidsBeansTableActionPanel1.getMetaClass().getDomain(),
                                                getConnectionContext());
                                if (viewMo != null) {
                                    viewBeans.add(viewMo.getBean());
                                }
                            }
                        }
                        return viewBeans;
                    }

                    @Override
                    protected void done() {
                        try {
                            cidsBeansTableActionPanel1.setCidsBeans(get());
                            ((CardLayout)jDialog1.getContentPane().getLayout()).show(jDialog1.getContentPane(),
                                "exporter");
                        } catch (final Exception ex) {
                            LOG.error(ex, ex);
                        }
                    }
                }.execute();
            StaticSwingTools.showDialog(this, jDialog1, true);
        }
    } //GEN-LAST:event_jxlHauptinfoActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void tblMauernFocusLost(final java.awt.event.FocusEvent evt) { //GEN-FIRST:event_tblMauernFocusLost
        map.gotoInitialBoundingBox();
        map.getFeatureCollection().addFeatures(pointFeatures);
        tblMauern.clearSelection();
    }                                                                      //GEN-LAST:event_tblMauernFocusLost

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxlKatasterblatt1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jxlKatasterblatt1ActionPerformed
        MauerObjectsPermissionsProviderDialog.getInstance().setCidsBeans(getCidsBeans());
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(this),
            MauerObjectsPermissionsProviderDialog.getInstance(),
            true);
    }                                                                                     //GEN-LAST:event_jxlKatasterblatt1ActionPerformed

    @Override
    public Collection<CidsBean> getCidsBeans() {
        return cidsBeans;
    }

    /**
     * DOCUMENT ME!
     */
    private void changeMap() {
        final Runnable mapChangeRunnable = new Runnable() {

                @Override
                public void run() {
                    if (selectedCidsBeanWrapper == null) {
                        return;
                    }
                    final Geometry g = selectedCidsBeanWrapper.getGeometry();
                    if (g == null) {
                        LOG.info("No Geoemtry available for the selected Mauer Geometry. Can not change Map");
                        return;
                    }
                    final XBoundingBox boxToGoto = new XBoundingBox(g.getEnvelope().buffer(
                                ClientAlkisConf.getInstance().getGeoBuffer()),
                            ClientAlkisConf.getInstance().getSrsService(),
                            true);
                    boxToGoto.setX1(boxToGoto.getX1()
                                - (ClientAlkisConf.getInstance().getGeoBufferMultiplier() * boxToGoto.getWidth()));
                    boxToGoto.setX2(boxToGoto.getX2()
                                + (ClientAlkisConf.getInstance().getGeoBufferMultiplier() * boxToGoto.getWidth()));
                    boxToGoto.setY1(boxToGoto.getY1()
                                - (ClientAlkisConf.getInstance().getGeoBufferMultiplier() * boxToGoto.getHeight()));
                    boxToGoto.setY2(boxToGoto.getY2()
                                + (ClientAlkisConf.getInstance().getGeoBufferMultiplier() * boxToGoto.getHeight()));
                    map.getFeatureCollection().removeFeatures(pointFeatures);
                    map.gotoBoundingBox(boxToGoto, false, true, 500);
                }
            };

        if (EventQueue.isDispatchThread()) {
            mapChangeRunnable.run();
        } else {
            EventQueue.invokeLater(mapChangeRunnable);
        }
    }

    @Override
    public void setCidsBeans(final Collection<CidsBean> cidsBeans) {
        this.cidsBeans = cidsBeans;
        if (cidsBeans != null) {
            cidsBeanWrappers = new LinkedList<>();
            for (final CidsBean bean : cidsBeans) {
                cidsBeanWrappers.add(new CidsBeanWrapper(bean, true));
            }
            int colorIndex = 0;
            Collections.sort(cidsBeanWrappers);
            for (final CidsBeanWrapper cidsBeanWrapper : cidsBeanWrappers) {
                cidsBeanWrapper.setColor(COLORS[colorIndex]);
                colorIndex = (colorIndex + 1) % COLORS.length;
            }

            tableModel.setCidsBeans(cidsBeanWrappers);

            if ((tblMauern != null) && (tblMauern.getColumnModel() != null)) {
                TableColumn column = tblMauern.getColumnModel().getColumn(0);
                if (column != null) {
                    column.setPreferredWidth(20);
//                    column.setMaxWidth(20);
                }
                column = tblMauern.getColumnModel().getColumn(3);
                if (column != null) {
                    column.setPreferredWidth(15);
//                    column.setMaxWidth(15);
                }
            }
            initMap();
            changeButtonAvailability(cidsBeanWrappers.size() > 0);
        }
    }

    @Override
    public void dispose() {
    }

    @Override
    public String getTitle() {
        return NbBundle.getMessage(
                MauerAggregationRenderer.class,
                "MauerAggregationRenderer.title",
                ((cidsBeanWrappers != null) ? cidsBeanWrappers.size() : "0"));
    }

    @Override
    public void setTitle(final String title) {
    }

    /**
     * DOCUMENT ME!
     *
     * @param  enable  DOCUMENT ME!
     */
    private void changeButtonAvailability(final boolean enable) {
        jxlHauptinfo.setEnabled(enable);
        jxlKatasterblatt.setEnabled(enable);
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
                    "mauer",
                    6);

            DevelopmentTools.createAggregationRendererInFrameFromRMIConnectionOnLocalhost(Arrays.asList(beans),
                "Ausgewählte Mauern",
                1024,
                800);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void initMap() {
        if (cidsBeanWrappers != null) {
            final Runnable mapRunnable = new Runnable() {

                    @Override
                    public void run() {
//                   initialisedMap = false;

                        final ActiveLayerModel mappingModel = new ActiveLayerModel();
                        mappingModel.setSrs(ClientAlkisConf.getInstance().getSrsService());
                        mappingModel.addHome(getBoundingBox());

                        final SimpleWMS swms = new SimpleWMS(new SimpleWmsGetMapUrl(
                                    ClientAlkisConf.getInstance().getMapCallString()));
                        swms.setName("Mauer");

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
                            final Feature mauerGeomFeature = cidsBeanWrapper.getFeature();
                            final Feature mauerPointFeature = cidsBeanWrapper.getPointFeature();
                            ;
                            if (mauerGeomFeature != null) {
                                map.getFeatureCollection().addFeature(mauerGeomFeature);
                            }
                            if (mauerGeomFeature != null) {
                                pointFeatures.add(mauerPointFeature);
                                map.getFeatureCollection().addFeature(mauerPointFeature);
                            }
                        }
                        map.setAnimationDuration(duration);

//            initialisedMap = true;
                    }

                    private XBoundingBox getBoundingBox() {
                        XBoundingBox result = null;
                        for (final CidsBeanWrapper cidsBeanWrapper : cidsBeanWrappers) {
                            final Geometry g = cidsBeanWrapper.getGeometry();
                            if (g != null) {
                                final Geometry geometry = CrsTransformer.transformToGivenCrs(
                                        g,
                                        ClientAlkisConf.getInstance().getSrsService());

                                if (result == null) {
                                    result = new XBoundingBox(geometry.getEnvelope().buffer(
                                                ClientAlkisConf.getInstance().getGeoBuffer()),
                                            ClientAlkisConf.getInstance().getSrsService(),
                                            true);
                                    result.setSrs(ClientAlkisConf.getInstance().getSrsService());
                                    result.setMetric(true);
                                } else {
                                    final XBoundingBox temp = new XBoundingBox(geometry.getEnvelope().buffer(
                                                ClientAlkisConf.getInstance().getGeoBuffer()),
                                            ClientAlkisConf.getInstance().getSrsService(),
                                            true);
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
                };

            if (EventQueue.isDispatchThread()) {
                mapRunnable.run();
            } else {
                EventQueue.invokeLater(mapRunnable);
            }
        }
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
    private class MauerTableModel extends AbstractTableModel {

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
            return COLUMNS;
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            if (cidsBeanWrappers == null) {
                return null;
            }
            final CidsBeanWrapper bean = cidsBeanWrappers.get(rowIndex);
            if (columnIndex == 0) {
                return bean.isSelected();
            } else if (columnIndex == 1) {
                return bean.lagebezeichnung;
            } else if (columnIndex == 2) {
                return bean.stuetzmauertyp;
            } else {
                return bean.color;
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
                    MauerAggregationRenderer.class,
                    "MauerAggregationRenderer.MauerTableModel.getColumnName("
                            + column
                            + ")");
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
    private class CidsBeanWrapper implements Comparable<CidsBeanWrapper> {

        //~ Instance fields ----------------------------------------------------

        private CidsBean cidsBean;
        private boolean selected;
        private Color color;
        private String lagebezeichnung;
        private String stuetzmauertyp;
        private Geometry geometry;
        private StyledFeature feature;
        private StyledFeature pointFeature;

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
            final Object lagebezObj = cidsBean.getProperty("lagebezeichnung");
            if (lagebezObj == null) {
                this.lagebezeichnung = "n.a.";
            } else {
                this.lagebezeichnung = lagebezObj.toString();
            }
            final Object stuetzTypObj = cidsBean.getProperty("stuetzmauertyp.name");
            if (stuetzTypObj == null) {
                this.stuetzmauertyp = "n.a.";
            } else {
                this.stuetzmauertyp = stuetzTypObj.toString();
            }

            if ((cidsBean.getProperty("georeferenz.geo_field") != null)
                        && (cidsBean.getProperty("georeferenz.geo_field") instanceof Geometry)) {
                this.geometry = CrsTransformer.transformToGivenCrs((Geometry)cidsBean.getProperty(
                            "georeferenz.geo_field"),
                        ClientAlkisConf.getInstance().getSrsService());
            }

            if (this.geometry != null) {
                final StyledFeature dsf = new DefaultStyledFeature();
                dsf.setLineWidth(3);
                dsf.setGeometry(this.geometry);
                dsf.setTransparency(0.9F);
                this.feature = dsf;

                final StyledFeature pointDsf = new DefaultStyledFeature();
                pointDsf.setGeometry(geometry.getEnvelope().getCentroid());
                FeatureAnnotationSymbol result;
                URL urlToIcon = null;
                urlToIcon = getClass().getResource(
                        "/de/cismet/cids/custom/featurerenderer/wunda_blau/pointicon_mauer_silver.png");
                final Color pointColor = new Color(0xC0, 0xC0, 0xC0);
                ImageIcon pointIcon = null;
                if (urlToIcon != null) {
                    pointIcon = new ImageIcon(urlToIcon);
                }
                if (pointIcon != null) {
                    result = new FeatureAnnotationSymbol(pointIcon.getImage());
                    result.setSweetSpotX(0.5D);
                    result.setSweetSpotY(0.9D);
                } else {
                    final int fallbackSymbolSize = 8;
                    final BufferedImage bufferedImage = new BufferedImage(
                            fallbackSymbolSize,
                            fallbackSymbolSize,
                            BufferedImage.TYPE_INT_ARGB);

                    final Graphics2D graphics = (Graphics2D)bufferedImage.getGraphics();
                    graphics.setColor(pointColor);
                    graphics.fillOval(0, 0, fallbackSymbolSize, fallbackSymbolSize);

                    result = new FeatureAnnotationSymbol(bufferedImage);
                    result.setSweetSpotX(0.5);
                    result.setSweetSpotY(0.5);
                }
                pointDsf.setPointAnnotationSymbol(result);
                this.pointFeature = pointDsf;
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
            if (feature != null) {
                feature.setFillingPaint(this.color);
                feature.setLinePaint(this.color);
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
        public String getGemarkung() {
            return lagebezeichnung;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public String getBezeichnung() {
            return stuetzmauertyp;
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

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public StyledFeature getPointFeature() {
            return pointFeature;
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

            final int districtComparison = cidsBean1.getProperty("lagebezeichnung")
                        .toString()
                        .compareTo(cidsBean2.getProperty("lagebezeichnung").toString());

            if (districtComparison != 0) {
                return districtComparison;
            } else {
                return cidsBean1.getProperty("id").toString().compareTo(cidsBean2.getProperty("id").toString());
            }
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
}
