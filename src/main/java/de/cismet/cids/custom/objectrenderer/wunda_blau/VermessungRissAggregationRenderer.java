/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.ui.RequestsFullSizeComponent;

import Sirius.server.middleware.types.MetaObjectNode;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import de.cismet.cids.custom.clientutils.ByteArrayActionDownload;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.objectrenderer.utils.VermessungsrissPictureFinderClientUtils;
import de.cismet.cids.custom.objectrenderer.utils.alkis.ClientAlkisConf;
import de.cismet.cids.custom.objectrenderer.utils.billing.BillingPopup;
import de.cismet.cids.custom.objectrenderer.wunda_blau.VermessungRissAggregationRenderer.SelectionCheckBox;
import de.cismet.cids.custom.utils.alkis.VermessungsRissReportHelper;
import de.cismet.cids.custom.utils.billing.BillingProductGroupAmount;
import de.cismet.cids.custom.wunda_blau.search.actions.VermessungsrissReportServerAction;

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

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.downloadmanager.BackgroundTaskMultipleDownload;
import de.cismet.tools.gui.downloadmanager.Download;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;
import de.cismet.tools.gui.downloadmanager.HttpDownload;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class VermessungRissAggregationRenderer extends javax.swing.JPanel implements CidsBeanAggregationRenderer,
    RequestsFullSizeComponent,
    ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(VermessungRissAggregationRenderer.class);
    private static final double BUFFER = 0.005;

    private static final ImageIcon ICON_LOADING = new javax.swing.ImageIcon(VermessungRissAggregationRenderer.class
                    .getResource("/de/cismet/cids/custom/objectrenderer/wunda_blau/bullet_yellow.png"));
    private static final ImageIcon ICON_FOUND = new javax.swing.ImageIcon(VermessungRissAggregationRenderer.class
                    .getResource("/de/cismet/cids/custom/objectrenderer/wunda_blau/bullet_green.png"));
    private static final ImageIcon ICON_NOTFOUND = new javax.swing.ImageIcon(VermessungRissAggregationRenderer.class
                    .getResource("/de/cismet/cids/custom/objectrenderer/wunda_blau/bullet_red.png"));
    private static final ImageIcon ICON_ERROR = new javax.swing.ImageIcon(VermessungRissAggregationRenderer.class
                    .getResource("/de/cismet/cids/custom/objectrenderer/wunda_blau/bullet_red.png"));

    // Spaltenueberschriften
    private static final String[] AGR_COMLUMN_NAMES = new String[] {
            "Auswahl",
            "Schlüssel",
            "Gemarkung",
            "Flur",
            "Blatt",
            "Jahr",
            "Format",
            "Riss",
            "Erg."
        };
    // Namen der Properties -> Spalten
    private static final String[] AGR_PROPERTY_NAMES = new String[] {
            null,
            "schluessel",
            "gemarkung.name",
            "flur",
            "blatt",
            "jahr",
            "format",
            null,
            null
        };
    private static final int[] AGR_COMLUMN_WIDTH = new int[] { 40, 40, 100, 60, 40, 60, 80, 30, 30 };

    //~ Instance fields --------------------------------------------------------

    private List<CidsBean> cidsBeans;
    private String title = "";
    private Map<CidsBean, CidsFeature> features;
    private Comparator<Integer> tableComparator;
    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    private boolean allowErgaenzendeDokumenteReport = false;
    private boolean allowVermessungsrisseReport = false;

    private final Map<CidsBean, Boolean> rissCheckerMap = new HashMap<>();
    private final Map<CidsBean, Exception> rissCheckerExceptionMap = new HashMap<>();
    private final Map<CidsBean, Boolean> ergCheckerMap = new HashMap<>();
    private final Map<CidsBean, Exception> ergCheckerExceptionMap = new HashMap<>();

    private final Map<CidsBean, Boolean> risseSelectionMap = new HashMap<>();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGenerateReport;
    private javax.swing.JComboBox cmbType;
    private javax.swing.Box.Filler flrGap;
    private javax.swing.JLabel lblJobnumber;
    private javax.swing.JLabel lblProjectname;
    private javax.swing.JLabel lblType;
    private de.cismet.cismap.commons.gui.MappingComponent mappingComponent;
    private javax.swing.JPanel panMap;
    private javax.swing.JPanel pnlReport;
    private javax.swing.JScrollPane scpRisse;
    private javax.swing.JTable tblRisse;
    private javax.swing.JTextField txtJobnumber;
    private javax.swing.JTextField txtProjectname;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VermessungRissAggregationRenderer object.
     */
    public VermessungRissAggregationRenderer() {
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        initComponents();

        scpRisse.getViewport().setOpaque(false);
        tblRisse.getSelectionModel().addListSelectionListener(new TableSelectionListener());
        tblRisse.getColumnModel().getColumn(7).setCellRenderer(new PictureStateCellRenderer(false));
        tblRisse.getColumnModel().getColumn(8).setCellRenderer(new PictureStateCellRenderer(true));

        tableComparator = new TableModelIndexConvertingToViewIndexComparator(tblRisse);
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

        tblRisse.setModel(new PointTableModel());
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

        lblProjectname.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissAggregationRenderer.class,
                "VermessungRissAggregationRenderer.lblProjectname.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlReport.add(lblProjectname, gridBagConstraints);

        txtProjectname.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissAggregationRenderer.class,
                "VermessungRissAggregationRenderer.txtProjectname.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlReport.add(txtProjectname, gridBagConstraints);

        lblJobnumber.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissAggregationRenderer.class,
                "VermessungRissAggregationRenderer.lblJobnumber.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        pnlReport.add(lblJobnumber, gridBagConstraints);

        txtJobnumber.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissAggregationRenderer.class,
                "VermessungRissAggregationRenderer.txtJobnumber.text")); // NOI18N
        txtJobnumber.setMaximumSize(new java.awt.Dimension(250, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        pnlReport.add(txtJobnumber, gridBagConstraints);

        btnGenerateReport.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissAggregationRenderer.class,
                "VermessungRissAggregationRenderer.btnGenerateReport.text"));        // NOI18N
        btnGenerateReport.setToolTipText(org.openide.util.NbBundle.getMessage(
                VermessungRissAggregationRenderer.class,
                "VermessungRissAggregationRenderer.btnGenerateReport.toolTipText")); // NOI18N
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

        lblType.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissAggregationRenderer.class,
                "VermessungRissAggregationRenderer.lblType.text")); // NOI18N
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
        final Collection<CidsBean> selectedVermessungsrisse = getSelectedVermessungsrisse();

        if (selectedVermessungsrisse.isEmpty()) {
            JOptionPane.showMessageDialog(
                StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(
                    VermessungRissAggregationRenderer.class,
                    "VermessungRissAggregationRenderer.btnGenerateReportActionPerformed(ActionEvent).emptySelection.message"),
                NbBundle.getMessage(
                    VermessungRissAggregationRenderer.class,
                    "VermessungRissAggregationRenderer.btnGenerateReportActionPerformed(ActionEvent).emptySelection.title"),
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    final Object typeObj = cmbType.getSelectedItem();
                    final String type;
                    if (typeObj instanceof String) {
                        type = (String)typeObj;

                        final HashMap<String, Map> productGroupExts = new HashMap<>();
                        try {
                            for (final CidsBean selectedVermessungsriss : selectedVermessungsrisse) {
                                final String productGroupExt = (String)selectedVermessungsriss.getProperty(
                                        "format.productgroup_ext");

                                final Map<String, Integer> priceGroups;
                                if (productGroupExts.containsKey(productGroupExt)) {
                                    priceGroups = productGroupExts.get(productGroupExt);
                                } else {
                                    priceGroups = new HashMap<>();
                                    productGroupExts.put(productGroupExt, priceGroups);
                                }

                                final boolean isDocumentAvailable;
                                if (type.equalsIgnoreCase(VermessungsRissReportHelper.TYPE_VERMESSUNGSRISSE)) {
                                    isDocumentAvailable = rissCheckerMap.get(selectedVermessungsriss);
                                } else if (type.equalsIgnoreCase(
                                                VermessungsRissReportHelper.TYPE_COMPLEMENTARYDOCUMENTS)) {
                                    isDocumentAvailable = ergCheckerMap.get(selectedVermessungsriss);
                                } else {
                                    isDocumentAvailable = false;
                                }
                                if (isDocumentAvailable) {
                                    final String pricegroup = (String)selectedVermessungsriss.getProperty(
                                            "format.pricegroup");
                                    final Integer amount = priceGroups.get(pricegroup);
                                    if (amount == null) {
                                        priceGroups.put(pricegroup, 1);
                                    } else {
                                        final Integer newAmount = amount + 1;
                                        priceGroups.put(pricegroup, newAmount);
                                    }
                                }
                            }

                            if ((productGroupExts.size() > 1)
                                        && SessionManager.getConnection().hasConfigAttr(
                                            SessionManager.getSession().getUser(),
                                            BillingPopup.MODE_CONFIG_ATTR,
                                            getConnectionContext())) {
                                JOptionPane.showMessageDialog(
                                    VermessungRissAggregationRenderer.this,
                                    "<html>Es wurden Produkte zum Download ausgewählt,"
                                            + "<br>die auf unterschiedliche Weise abgerechnet werden."
                                            + "<br>Daher werden nun mehrere Download-Protokolle erzeugt.",
                                    "Mehrere Download-Protokolle",
                                    JOptionPane.INFORMATION_MESSAGE);
                            }
                            boolean downloadBilder = false;
                            boolean downloadGrenzniederschriften = false;
                            for (final String productGroupExt : productGroupExts.keySet()) {
                                final Map<String, Integer> priceGroups = productGroupExts.get(productGroupExt);
                                final Set<String> keys = priceGroups.keySet();
                                final BillingProductGroupAmount[] amounts = new BillingProductGroupAmount[keys.size()];
                                int i = 0;
                                for (final String k : keys) {
                                    amounts[i] = new BillingProductGroupAmount(k, priceGroups.get(k));
                                    i++;
                                }

                                if (type.equalsIgnoreCase(VermessungsRissReportHelper.TYPE_VERMESSUNGSRISSE)) {
                                    if (BillingPopup.doBilling(
                                                    "vrpdf"
                                                    + ((productGroupExt != null) ? productGroupExt : ""),
                                                    "no.yet",
                                                    (Geometry)null,
                                                    getConnectionContext(),
                                                    amounts)) {
                                        downloadBilder = true;
                                    }
                                } else if (type.equalsIgnoreCase(
                                                VermessungsRissReportHelper.TYPE_COMPLEMENTARYDOCUMENTS)) {
                                    if (BillingPopup.doBilling(
                                                    "doklapdf"
                                                    + ((productGroupExt != null) ? productGroupExt : ""),
                                                    "no.yet",
                                                    (Geometry)null,
                                                    getConnectionContext(),
                                                    amounts)) {
                                        downloadGrenzniederschriften = true;
                                    }
                                }
                            }

                            if (downloadBilder) {
                                downloadProducts(
                                    selectedVermessungsrisse,
                                    type,
                                    ClientAlkisConf.getInstance().getVermessungHostBilder());
                            }
                            if (downloadGrenzniederschriften) {
                                downloadProducts(
                                    selectedVermessungsrisse,
                                    type,
                                    ClientAlkisConf.getInstance().getVermessungHostGrenzniederschriften());
                            }
                        } catch (Exception e) {
                            LOG.error("Error when trying to produce a alkis product", e);
                            // Hier noch ein Fehlerdialog
                        }
                    } else {
                        // TODO: User feedback?!
                        LOG.info("Unknown type '" + typeObj + "' encountered. Skipping report generation.");
                    }
                    return null;
                }
            }.execute();
    } //GEN-LAST:event_btnGenerateReportActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param   selectedVermessungsrisse  DOCUMENT ME!
     * @param   host                      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Collection<URL> identifyAdditionalFiles(final Collection<CidsBean> selectedVermessungsrisse,
            final String host) {
        final Collection<URL> additionalFilesToDownload = new LinkedList<>();
        for (final CidsBean vermessungsriss : selectedVermessungsrisse) {
            final String schluessel;
            final Integer gemarkung;
            final String flur;
            final String blatt;

            try {
                schluessel = vermessungsriss.getProperty("schluessel").toString();
                gemarkung = (Integer)vermessungsriss.getProperty("gemarkung.id");
                flur = vermessungsriss.getProperty("flur").toString();
                blatt = vermessungsriss.getProperty("blatt").toString();
            } catch (final Exception ex) {
                // TODO: User feedback?
                LOG.warn("Could not include raster document for vermessungsriss '"
                            + vermessungsriss.toJSONString(true)
                            + "'.",
                    ex);
                continue;
            }

            final String document;
            // we search for reduced size images, since we need the reduced size image for the report
            if (host.equals(ClientAlkisConf.getInstance().getVermessungHostGrenzniederschriften())) {
                document = VermessungsrissPictureFinderClientUtils.getInstance()
                            .findGrenzniederschriftPicture(
                                    schluessel,
                                    gemarkung,
                                    flur,
                                    blatt);
            } else {
                document = VermessungsrissPictureFinderClientUtils.getInstance()
                            .findVermessungsrissPicture(
                                    schluessel,
                                    gemarkung,
                                    flur,
                                    blatt);
            }

            if (document == null) {
                LOG.info("No document URLS found for the Vermessungsriss report");
            }
            boolean isOfReducedSize = false;
            if (document != null) {
                try {
                    final URL url = ClientAlkisConf.getInstance().getDownloadUrlForDocument(document);
                    if (url.toString().contains("_rs")) {
                        isOfReducedSize = true;
                    }

                    // when a reduced size image was found we download the original file as jpg also
                    if (isOfReducedSize) {
                        additionalFilesToDownload.add(new URL(
                                url.toString().replaceAll("_rs", "")));
                    }
                    break;
                } catch (final Exception ex) {
                    LOG.warn("Could not read document from URL '" + document
                                + "'. Skipping this url.",
                        ex);
                }
            }
        }
        return additionalFilesToDownload;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  selectedVermessungsrisse  DOCUMENT ME!
     * @param  type                      DOCUMENT ME!
     * @param  host                      DOCUMENT ME!
     */
    private void downloadProducts(final Collection<CidsBean> selectedVermessungsrisse,
            final String type,
            final String host) {
        if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(VermessungRissAggregationRenderer.this)) {
            String projectname = txtProjectname.getText();
            if ((projectname == null) || (projectname.trim().length() == 0)) {
                projectname = type;
            }
            final String finalProjectname = projectname;

            final BackgroundTaskMultipleDownload.FetchDownloadsTask fetchDownloadsTask =
                new BackgroundTaskMultipleDownload.FetchDownloadsTask() {

                    @Override
                    public Collection<? extends Download> fetchDownloads() throws Exception {
                        final Collection<MetaObjectNode> mons = new ArrayList<>(
                                selectedVermessungsrisse.size());
                        for (final CidsBean selectedVermessungsriss : selectedVermessungsrisse) {
                            mons.add(new MetaObjectNode(selectedVermessungsriss));
                        }

                        final ServerActionParameter[] saps = new ServerActionParameter[] {
                                new ServerActionParameter<>(
                                    VermessungsrissReportServerAction.Parameter.RISSE_MONS.toString(),
                                    mons),
                                new ServerActionParameter<>(
                                    VermessungsrissReportServerAction.Parameter.JOB_NUMBER.toString(),
                                    txtJobnumber.getText()),
                                new ServerActionParameter<>(
                                    VermessungsrissReportServerAction.Parameter.PROJECT_NAME.toString(),
                                    txtProjectname.getText()),
                                new ServerActionParameter<>(
                                    VermessungsrissReportServerAction.Parameter.HOST.toString(),
                                    host)
                            };

                        final String jobname = DownloadManagerDialog.getInstance().getJobName();
                        final Download serverActionDownload = new ByteArrayActionDownload(
                                VermessungsrissReportServerAction.TASK_NAME,
                                null,
                                saps,
                                finalProjectname,
                                jobname,
                                (VermessungsRissReportHelper.TYPE_VERMESSUNGSRISSE.equalsIgnoreCase(type)) ? "vermriss"
                                                                                                           : "ergdok",
                                ".pdf",
                                getConnectionContext());

                        final Collection<Download> downloads = new ArrayList<>();
                        downloads.add(serverActionDownload);

                        final Collection<URL> additionalFilesToDownload = identifyAdditionalFiles(
                                selectedVermessungsrisse,
                                host);
                        if (!additionalFilesToDownload.isEmpty()) {
                            for (final URL additionalFileToDownload : additionalFilesToDownload) {
                                final String file = additionalFileToDownload.getFile()
                                            .substring(additionalFileToDownload.getFile().lastIndexOf('/') + 1);
                                final String filename = file.substring(0, file.lastIndexOf('.'));
                                final String extension = file.substring(file.lastIndexOf('.'));

                                downloads.add(new HttpDownload(
                                        additionalFileToDownload,
                                        null,
                                        jobname,
                                        file,
                                        filename,
                                        extension));
                            }
                        }
                        return downloads;
                    }
                };

            DownloadManager.instance().add(new BackgroundTaskMultipleDownload(null, projectname, fetchDownloadsTask));
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
    protected Collection<CidsBean> getSelectedVermessungsrisse() {
        final Collection<CidsBean> result = new LinkedList<>();
        final List<Integer> selectedIndexes = new ArrayList<>();

        final TableModel tableModel = getTableModel();
        for (int i = 0; i < tableModel.getRowCount(); ++i) {
            final Object includedObj = tableModel.getValueAt(i, 0);
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
    public List<CidsBean> getCidsBeans() {
        return cidsBeans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private PointTableModel getTableModel() {
        return (PointTableModel)tblRisse.getModel();
    }
    /**
     * DOCUMENT ME!
     *
     * @param  beans  DOCUMENT ME!
     */
    @Override
    public void setCidsBeans(final Collection<CidsBean> beans) {
        if (beans instanceof List) {
            clearMaps();
            this.cidsBeans = (List<CidsBean>)beans;
            features = new HashMap<>(beans.size());

            initMap();

            for (final CidsBean cidsBean : beans) {
                risseSelectionMap.put(cidsBean, true);
            }
            getTableModel().refresh();
            for (final CidsBean bean : beans) {
                new PictureWorker(bean, true).execute();
                new PictureWorker(bean, false).execute();
            }

            final TableColumnModel cModel = tblRisse.getColumnModel();
            for (int i = 0; i < cModel.getColumnCount(); ++i) {
                cModel.getColumn(i).setPreferredWidth(AGR_COMLUMN_WIDTH[i]);
            }

            final TableRowSorter tableSorter = ObjectRendererUtils.decorateTableWithSorter(tblRisse);
            final List<RowSorter.SortKey> sortKeys = new LinkedList<>();
            sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
            sortKeys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
            sortKeys.add(new RowSorter.SortKey(3, SortOrder.ASCENDING));
            sortKeys.add(new RowSorter.SortKey(4, SortOrder.DESCENDING));
            tableSorter.setSortKeys(sortKeys);

            updateButtons();
        }

        setTitle(null);
    }

    /**
     * DOCUMENT ME!
     */
    private void updateButtons() {
        final boolean billingAllowed = BillingPopup.isBillingAllowed("doklapdf", getConnectionContext())
                    || BillingPopup.isBillingAllowed("vrpdf", getConnectionContext());

        final boolean enabled = billingAllowed
                    && ((allowErgaenzendeDokumenteReport && allowVermessungsrisseReport)
                        || allowErgaenzendeDokumenteReport || allowVermessungsrisseReport);
        if (allowErgaenzendeDokumenteReport && allowVermessungsrisseReport) {
            cmbType.setModel(new DefaultComboBoxModel(
                    new String[] {
                        VermessungsRissReportHelper.TYPE_VERMESSUNGSRISSE,
                        VermessungsRissReportHelper.TYPE_COMPLEMENTARYDOCUMENTS
                    }));
        } else if (allowErgaenzendeDokumenteReport) {
            cmbType.setModel(new DefaultComboBoxModel(
                    new String[] { VermessungsRissReportHelper.TYPE_COMPLEMENTARYDOCUMENTS }));
        } else if (allowVermessungsrisseReport) {
            cmbType.setModel(new DefaultComboBoxModel(
                    new String[] { VermessungsRissReportHelper.TYPE_VERMESSUNGSRISSE }));
        }

        cmbType.setEnabled(enabled);
        btnGenerateReport.setEnabled(enabled);
        txtJobnumber.setEnabled(enabled);
        txtProjectname.setEnabled(enabled);
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    public void dispose() {
        mappingComponent.dispose();
        clearMaps();
    }

    /**
     * DOCUMENT ME!
     */
    public void clearMaps() {
        rissCheckerMap.clear();
        rissCheckerExceptionMap.clear();
        ergCheckerMap.clear();
        ergCheckerExceptionMap.clear();
        risseSelectionMap.clear();
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
        String desc = "Vermessungsrisse";
        final Collection<CidsBean> beans = cidsBeans;
        if ((beans != null) && (beans.size() > 0)) {
            desc += " - " + beans.size() + " Vermessungsrisse ausgewählt";
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
                    -0.5,
                    -0.5,
                    0.5,
                    0.5,
                    /*box.getX1(),
                     * box.getY1(), box.getX2(),box.getY2(),*/
                    ClientAlkisConf.getInstance().getSrsService(),
                    true));
            // final SimpleWMS swms = new SimpleWMS(new
            // SimpleWmsGetMapUrl(ClientAlkisConf.getInstance().getMapCallString()));
            final SimpleWMS swms = new SimpleWMS(new SimpleWmsGetMapUrl(
                        ClientAlkisConf.getInstance().getRasterfariUrl()));

            swms.setName("Vermessung_Riss");
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
            mappingComponent.setAnimationDuration(500);
        } catch (Exception e) {
            LOG.fatal(e, e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   vermessungsrisse  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected XBoundingBox boundingBoxFromPointList(final Collection<CidsBean> vermessungsrisse) {
        final List<Geometry> geometries = new LinkedList<>();

        for (final CidsBean vermessungsriss : vermessungsrisse) {
            try {
                if (vermessungsriss.getProperty("geometrie.geo_field") instanceof Geometry) {
                    geometries.add((Geometry)vermessungsriss.getProperty("geometrie.geo_field"));
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
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static boolean hasVermessungsriss(final CidsBean cidsBean) {
        try {
            return VermessungsrissPictureFinderClientUtils.getInstance()
                        .findVermessungsrissPicture((String)cidsBean.getProperty("schluessel"),
                                (Integer)cidsBean.getProperty("gemarkung.id"),
                                (String)cidsBean.getProperty("flur"),
                                (String)cidsBean.getProperty("blatt")) != null;
        } catch (final Exception ex) {
            LOG.info("Could not determine if CidsBean has measurement sketches.", ex);
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static boolean hasErgaenzendeDokumente(final CidsBean cidsBean) {
        try {
            return VermessungsrissPictureFinderClientUtils.getInstance()
                        .findGrenzniederschriftPicture((String)cidsBean.getProperty("schluessel"),
                                (Integer)cidsBean.getProperty("gemarkung.id"),
                                (String)cidsBean.getProperty("flur"),
                                (String)cidsBean.getProperty("blatt")) != null;
        } catch (final Exception ex) {
            LOG.info("Could not determine if CidsBean has measurement sketches.", ex);
            return false;
        }
    }

    @Override
    public final ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public class PictureWorker extends SwingWorker<Boolean, Void> {

        //~ Instance fields ----------------------------------------------------

        final boolean rissOrErg;

        private final CidsBean cidsBean;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new PictureWorker object.
         *
         * @param  cidsBean   DOCUMENT ME!
         * @param  rissOrErg  DOCUMENT ME!
         */
        public PictureWorker(final CidsBean cidsBean, final boolean rissOrErg) {
            this.cidsBean = cidsBean;
            this.rissOrErg = rissOrErg;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected Boolean doInBackground() throws Exception {
            return rissOrErg ? hasVermessungsriss(cidsBean) : hasErgaenzendeDokumente(cidsBean);
        }

        @Override
        protected void done() {
            final Map<CidsBean, Boolean> checkerMap = (rissOrErg ? rissCheckerMap : ergCheckerMap);
            final Map<CidsBean, Exception> exceptionMap = (rissOrErg ? rissCheckerExceptionMap
                                                                     : ergCheckerExceptionMap);
            try {
                checkerMap.put(cidsBean, get());
                getTableModel().refresh();
            } catch (final Exception ex) {
                checkerMap.put(cidsBean, null);
                exceptionMap.put(cidsBean, ex);
            } finally {
                if (checkerMap.size() == cidsBeans.size()) {
                    boolean allowReport = false;

                    for (final Boolean value : checkerMap.values()) {
                        if (value) {
                            allowReport = true;
                            break;
                        }
                    }

                    if (rissOrErg) {
                        allowVermessungsrisseReport = allowReport;
                    } else {
                        allowErgaenzendeDokumenteReport = allowReport;
                    }
                    updateButtons();
                }
            }
        }
    }

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

            CidsFeature featureToSelect = null;

            final int[] indexes = tblRisse.getSelectedRows();
            if ((indexes != null) && (indexes.length > 0)) {
                for (final int viewIdx : indexes) {
                    final int modelIdx = tblRisse.getRowSorter().convertRowIndexToModel(viewIdx);
                    if ((modelIdx > -1) && (modelIdx < cidsBeans.size())) {
                        final CidsBean selectedBean = cidsBeans.get(modelIdx);
                        featureToSelect = features.get(selectedBean);
                        break;
                    }
                }
            }

            if ((featureToSelect != null) && (featureToSelect.getGeometry() != null)) {
                final XBoundingBox boxToGoto = new XBoundingBox(featureToSelect.getGeometry().getEnvelope().buffer(
                            ClientAlkisConf.getInstance().getGeoBuffer()));
                boxToGoto.setX1(boxToGoto.getX1()
                            - (ClientAlkisConf.getInstance().getGeoBufferMultiplier() * boxToGoto.getWidth()));
                boxToGoto.setX2(boxToGoto.getX2()
                            + (ClientAlkisConf.getInstance().getGeoBufferMultiplier() * boxToGoto.getWidth()));
                boxToGoto.setY1(boxToGoto.getY1()
                            - (ClientAlkisConf.getInstance().getGeoBufferMultiplier() * boxToGoto.getHeight()));
                boxToGoto.setY2(boxToGoto.getY2()
                            + (ClientAlkisConf.getInstance().getGeoBufferMultiplier() * boxToGoto.getHeight()));
                mappingComponent.getFeatureCollection().unselectAll();
                mappingComponent.getFeatureCollection().select(featureToSelect);
                mappingComponent.gotoBoundingBox(boxToGoto, false, true, 500);
            } else {
                mappingComponent.getFeatureCollection().unselectAll();
                mappingComponent.gotoInitialBoundingBox();
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class SelectionCheckBox extends JCheckBox {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new SelectionCheckBox object.
         *
         * @param  cidsBean  DOCUMENT ME!
         */
        public SelectionCheckBox(final CidsBean cidsBean) {
            addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(final ActionEvent evt) {
                        risseSelectionMap.put(cidsBean, isSelected());
                    }
                });
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class SelectionCellRenderer extends DefaultTableCellRenderer {

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getTableCellRendererComponent(final JTable table,
                final Object value,
                final boolean isSelected,
                final boolean hasFocus,
                final int row,
                final int column) {
            return new SelectionCheckBox(cidsBeans.get(tblRisse.convertRowIndexToModel(row)));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class PictureStateCellRenderer extends DefaultTableCellRenderer {

        //~ Instance fields ----------------------------------------------------

        private final boolean erg;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new PictureStateCellRenderer object.
         *
         * @param  erg  DOCUMENT ME!
         */
        public PictureStateCellRenderer(final boolean erg) {
            this.erg = erg;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getTableCellRendererComponent(final JTable table,
                final Object value,
                final boolean isSelected,
                final boolean hasFocus,
                final int row,
                final int column) {
            final CidsBean cidsBean = cidsBeans.get(tblRisse.convertRowIndexToModel(row));
            final Exception ex = (erg ? ergCheckerExceptionMap : rissCheckerExceptionMap).get(cidsBean);
            final Boolean status = (erg ? ergCheckerMap : rissCheckerMap).get(cidsBean);
            if (ex != null) {
                final JLabel label = new JLabel(ICON_ERROR);
                label.setToolTipText(ex.getMessage());
                // TODO click => error dialog
                return label;
            } else if (Boolean.TRUE.equals(status)) {
                return new JLabel(ICON_FOUND);
            } else if (Boolean.FALSE.equals(status)) {
                return new JLabel(ICON_NOTFOUND);
            } else {
                return new JLabel(ICON_LOADING);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class PointTableModel extends DefaultTableModel {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         */
        public void refresh() {
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return (cidsBeans != null) ? cidsBeans.size() : 0;
        }

        @Override
        public int getColumnCount() {
            return AGR_COMLUMN_NAMES.length;
        }

        @Override
        public String getColumnName(final int column) {
            return AGR_COMLUMN_NAMES[column];
        }

        @Override
        public void setValueAt(final Object value, final int row, final int column) {
            final CidsBean cidsBean = cidsBeans.get(tblRisse.convertRowIndexToModel(row));
            if (column == 0) {
                if (value instanceof Boolean) {
                    risseSelectionMap.put(cidsBean, (Boolean)value);
                } else {
                    risseSelectionMap.remove(cidsBean);
                }
            }
        }

        @Override
        public Object getValueAt(final int row, final int column) {
            final CidsBean cidsBean = cidsBeans.get(tblRisse.convertRowIndexToModel(row));
            switch (column) {
                case 0: {
                    return risseSelectionMap.get(cidsBean);
                }
                default: {
                    if ((column >= 0) && (column < AGR_PROPERTY_NAMES.length)) {
                        return (cidsBean != null) ? cidsBean.getProperty(AGR_PROPERTY_NAMES[column]) : null;
                    } else {
                        return null;
                    }
                }
                case (7): {
                    return rissCheckerMap.get(cidsBean);
                }
                case (8): {
                    return ergCheckerMap.get(cidsBean);
                }
            }
        }

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
            if ((columnIndex == 0) || (columnIndex == 7) || (columnIndex == 8)) {
                return Boolean.class;
            } else {
                return super.getColumnClass(columnIndex);
            }
        }
    }
}
