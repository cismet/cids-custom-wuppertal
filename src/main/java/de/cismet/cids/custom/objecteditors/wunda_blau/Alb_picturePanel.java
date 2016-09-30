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
 * Alb_picturePanel.java
 *
 * Created on 11.12.2009, 14:49:40
 */
package de.cismet.cids.custom.objecteditors.wunda_blau;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import org.openide.util.WeakListeners;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.net.URL;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.objectrenderer.utils.BaulastenPictureFinder;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.objectrenderer.utils.billing.BillingPopup;
import de.cismet.cids.custom.objectrenderer.wunda_blau.BaulastenReportGenerator;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.Crs;
import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.FeatureCollectionEvent;
import de.cismet.cismap.commons.features.PureNewFeature;
import de.cismet.cismap.commons.gui.measuring.MeasuringComponent;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.MessenGeometryListener;

import de.cismet.tools.CismetThreadPool;
import de.cismet.tools.StaticDecimalTools;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.WebAccessMultiPagePictureReader;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;
import de.cismet.tools.gui.downloadmanager.HttpDownload;
import de.cismet.tools.gui.panels.AlertPanel;
import de.cismet.tools.gui.panels.LayeredAlertPanel;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class Alb_picturePanel extends javax.swing.JPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Alb_picturePanel.class);

    private static final String REPORT_ACTION_TAG_BLATT = "baulast.report.blatt_disabled@WUNDA_BLAU";
    private static final String REPORT_ACTION_TAG_PLAN = "baulast.report.plan_disabled@WUNDA_BLAU";
    private static final String REPORT_ACTION_TAG_RASTER = "baulast.report.raster_disabled@WUNDA_BLAU";
    private static final String OPEN_ACTION_TAG = "baulast.renderer.openbutton_disabled@WUNDA_BLAU";

//    private static final String[] MD5_PROPERTY_NAMES = new String[]{"lageplan_md5", "textblatt_md5"};
    private static final String TEXTBLATT_PROPERTY = "textblatt";
    private static final String LAGEPLAN_PROPERTY = "lageplan";
    public static final String BLATTNUMMER_PROPERTY = "blattnummer";
    public static final String LFDNUMMER_PROPERTY = "laufende_nummer";
    private static final int LAGEPLAN_DOCUMENT = 0;
    private static final int TEXTBLATT_DOCUMENT = 1;
    private static final int NO_SELECTION = -1;
    private static final Color KALIBRIERUNG_VORHANDEN = new Color(120, 255, 190);
    //
    private static final ListModel LADEN_MODEL = new DefaultListModel() {

            {
                add(0, "Wird geladen...");
            }
        };

    private static final ListModel FEHLER_MODEL = new DefaultListModel() {

            {
                add(0, "Lesefehler.");
            }
        };

    private static boolean alreadyWarnedAboutPermissionProblem = false;

    //~ Instance fields --------------------------------------------------------

    private final JLabel alertWarnMessage = new JLabel(
            "<html> <b>Warnung! </b> Es wurde kein Dokument gefunden. Klicken Sie auf diese Meldung um eine Weiterleitung einzurichten.</html>");
    final AlertPanel alert = new AlertPanel(
            AlertPanel.TYPE.DANGER,
            alertWarnMessage,
            true);
    private XBoundingBox initialBoundingBox = new XBoundingBox(
            2583621.251964098d,
            5682507.032498134d,
            2584022.9413952776d,
            5682742.852810634d,
            "EPSG:31466",
            true);
    private Crs crs = new Crs("EPSG:31466", "EPSG:31466", "EPSG:31466", true, true);
    MeasuringComponent measureComponent = new MeasuringComponent(initialBoundingBox, crs);
    private PictureSelectWorker currentPictureSelectWorker = null;
    private WebAccessMultiPagePictureReader pictureReader;
    private CidsBean cidsBean;
    private URL[] documentURLs;
    private JToggleButton[] documentButtons;
    private transient PropertyChangeListener updatePicturePathListener = null;
    private JToggleButton currentSelectedButton;
    private final MessenFeatureCollectionListener messenListener;
    private volatile int currentDocument = NO_SELECTION;
    private volatile int currentPage = NO_SELECTION;
    private boolean pathsChanged = false;
    private final Map<Integer, Geometry> pageGeometries = new HashMap<Integer, Geometry>();
    private String collisionWarning = "";
    private final boolean selfPersisting;
    private boolean isErrorMessageVisible = true;
    private Alb_baulastUmleitungPanel umleitungsPanel = new Alb_baulastUmleitungPanel(
            Alb_baulastUmleitungPanel.MODE.TEXTBLATT,
            this);
    private PictureReaderWorker pictureReaderWorker;
    private boolean umleitungChangedFlag = false;
    private boolean showUmleitung = true;
    private boolean showDocTypePanelEnabled = true;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup btnGrpDocs;
    private javax.swing.JButton btnHome;
    private javax.swing.JButton btnOpen;
    private javax.swing.JToggleButton btnPlan;
    private javax.swing.JToggleButton btnTextblatt;
    private javax.swing.ButtonGroup buttonGrpMode;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink1;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink2;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink3;
    private org.jdesktop.swingx.JXBusyLabel jxLBusyMeasure;
    private org.jdesktop.swingx.JXHyperlink jxlUmleitung;
    private javax.swing.JLabel lblArea;
    private javax.swing.JLabel lblCurrentViewTitle;
    private javax.swing.JLabel lblDistance;
    private javax.swing.JLabel lblTxtArea;
    private javax.swing.JLabel lblTxtDistance;
    private javax.swing.JLabel lblUmleitung;
    private javax.swing.JLabel lblUmleitung2;
    private javax.swing.JList lstPictures;
    private de.cismet.tools.gui.panels.LayeredAlertPanel measureComponentPanel;
    private de.cismet.tools.gui.RoundedPanel panBlattberichte;
    private javax.swing.JPanel panCenter;
    private javax.swing.JPanel panPicNavigation;
    private javax.swing.JPanel pnlAlert;
    private javax.swing.JPanel pnlBusy;
    private javax.swing.JPanel pnlLink;
    private javax.swing.JPanel pnlMeasureComp;
    private javax.swing.JPanel pnlMeasureComponentWrapper;
    private javax.swing.JPanel pnlUmleitungLink;
    private de.cismet.tools.gui.RoundedPanel rpControls;
    private de.cismet.tools.gui.RoundedPanel rpMessdaten;
    private de.cismet.tools.gui.RoundedPanel rpSeiten;
    private javax.swing.JScrollPane scpPictureList;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel1;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel2;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel3;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel4;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel5;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel6;
    private de.cismet.tools.gui.RoundedPanel spDocuments;
    private javax.swing.JToggleButton togCalibrate;
    private javax.swing.JToggleButton togMessenLine;
    private javax.swing.JToggleButton togMessenPoly;
    private javax.swing.JToggleButton togPan;
    private javax.swing.JToggleButton togZoom;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form Alb_picturePanel.
     */
    public Alb_picturePanel() {
        this(false, true);
    }

    /**
     * Creates a new Alb_picturePanel object.
     *
     * @param  selfPersisting           DOCUMENT ME!
     * @param  showDocTypePanelEnabled  DOCUMENT ME!
     */
    public Alb_picturePanel(final boolean selfPersisting, final boolean showDocTypePanelEnabled) {
        this.selfPersisting = selfPersisting;
        this.showDocTypePanelEnabled = showDocTypePanelEnabled;
        documentURLs = new URL[2];
        documentButtons = new JToggleButton[documentURLs.length];
        initComponents();
        pnlMeasureComp.add(measureComponent, BorderLayout.CENTER);
        alert.setPreferredSize(new Dimension(500, 50));
        alert.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // To change body of generated methods,
        // choose Tools | Templates.
        pnlAlert.add(alert, BorderLayout.CENTER);
        pnlAlert.setBackground(new Color(1f, 1f, 1f, 0.8f));
        jxlUmleitung.setClickedColor(new Color(204, 204, 204));
        documentButtons[LAGEPLAN_DOCUMENT] = btnPlan;
        documentButtons[TEXTBLATT_DOCUMENT] = btnTextblatt;
        messenListener = new MessenFeatureCollectionListener();
        measureComponent.getFeatureCollection().addFeatureCollectionListener(messenListener);
//        expectedMD5Values = new String[2];
//        measureComponentPanel.setTopOffset(5);
//        measureComponentPanel.setOffset(5);
        alert.setVisible(false);
        alert.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(final MouseEvent e) {
                    handleAlertClick();
                }
            });
        alert.addCloseButtonActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    showUmleitung = true;
                    cancelPictureWorkers();
                    final String editedLink = umleitungsPanel.getLinkDocument();
                    if ((jxlUmleitung.getText() == null) || jxlUmleitung.getText().isEmpty()) {
                        lstPictures.setModel(new DefaultListModel());
                        SwingUtilities.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    showAlert(true);
                                    Alb_picturePanel.this.invalidate();
                                    Alb_picturePanel.this.revalidate();
                                    Alb_picturePanel.this.repaint();
                                }
                            });
                    } else {
                        if (!(jxlUmleitung.getText().isEmpty() || jxlUmleitung.getText().contains(editedLink))) {
                            showMeasureIsLoading();
                            lstPictures.setModel(new DefaultListModel());
                            final FileSearchWorker worker = new FileSearchWorker();
                            worker.execute();
                        }
                    }
                }
            });

        try {
            final boolean billingAllowed = BillingPopup.isBillingAllowed("bla");

            jXHyperlink1.setEnabled(!ObjectRendererUtils.checkActionTag(REPORT_ACTION_TAG_BLATT) && billingAllowed);
            jXHyperlink2.setEnabled(!ObjectRendererUtils.checkActionTag(REPORT_ACTION_TAG_PLAN) && billingAllowed);
            jXHyperlink3.setEnabled(!ObjectRendererUtils.checkActionTag(REPORT_ACTION_TAG_RASTER) && billingAllowed);
            btnOpen.setEnabled(!ObjectRendererUtils.checkActionTag(OPEN_ACTION_TAG));
        } catch (final Exception ex) {
            // needed for netbeans gui editor
            log.info("exception while checking action tags", ex);
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    public void dispose() {
        measureComponent.getFeatureCollection().removeFeatureCollectionListener(messenListener);
        measureComponent.dispose();
        updatePicturePathListener = null;
    }

    /**
     * DOCUMENT ME!
     */
    public void zoomToFeatureCollection() {
        measureComponent.zoomToFeatureCollection();
    }

    /**
     * DOCUMENT ME!
     */
    public void updateIfPicturePathsChanged() {
        if (pathsChanged) {
            setCurrentDocumentNull();
            CismetThreadPool.execute(new FileSearchWorker());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the cidsBean
     */
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  the cidsBean to set
     */
    public void setCidsBean(final CidsBean cidsBean) {
        documentURLs = new URL[2];
        umleitungChangedFlag = false;
        lstPictures.setModel(new DefaultListModel());
        measureComponent.removeAllFeatures();
        setEnabled(false);
        this.cidsBean = cidsBean;
        if (cidsBean != null) {
            updatePicturePathListener = new PropertyChangeListener() {

                    @Override
                    public void propertyChange(final PropertyChangeEvent evt) {
                        final String evtProp = evt.getPropertyName();
                        if (TEXTBLATT_PROPERTY.equals(evtProp) || LAGEPLAN_PROPERTY.equals(evtProp)) {
                            pathsChanged = true;
                        }
                    }
                };
            cidsBean.addPropertyChangeListener(WeakListeners.propertyChange(updatePicturePathListener, cidsBean));
        }
        setCurrentDocumentNull();
        jxLBusyMeasure.setBusy(true);
        CismetThreadPool.execute(new FileSearchWorker());
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        btnGrpDocs = new javax.swing.ButtonGroup();
        buttonGrpMode = new javax.swing.ButtonGroup();
        pnlLink = new javax.swing.JPanel();
        lblUmleitung = new javax.swing.JLabel();
        jxlUmleitung = new org.jdesktop.swingx.JXHyperlink();
        lblUmleitung2 = new javax.swing.JLabel();
        pnlAlert = new javax.swing.JPanel();
        pnlMeasureComponentWrapper = new javax.swing.JPanel();
        pnlBusy = new javax.swing.JPanel();
        jxLBusyMeasure = new JXBusyLabel(new Dimension(64, 64));
        pnlMeasureComp = new javax.swing.JPanel();
        panPicNavigation = new javax.swing.JPanel();
        panBlattberichte = new de.cismet.tools.gui.RoundedPanel();
        semiRoundedPanel6 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jXHyperlink1 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink2 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink3 = new org.jdesktop.swingx.JXHyperlink();
        spDocuments = new de.cismet.tools.gui.RoundedPanel();
        semiRoundedPanel2 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btnPlan = new javax.swing.JToggleButton();
        btnTextblatt = new javax.swing.JToggleButton();
        rpSeiten = new de.cismet.tools.gui.RoundedPanel();
        scpPictureList = new javax.swing.JScrollPane();
        lstPictures = new javax.swing.JList();
        semiRoundedPanel3 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel2 = new javax.swing.JLabel();
        rpControls = new de.cismet.tools.gui.RoundedPanel();
        semiRoundedPanel4 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        btnHome = new javax.swing.JButton();
        togPan = new javax.swing.JToggleButton();
        togZoom = new javax.swing.JToggleButton();
        togMessenLine = new javax.swing.JToggleButton();
        togMessenPoly = new javax.swing.JToggleButton();
        togCalibrate = new javax.swing.JToggleButton();
        btnOpen = new javax.swing.JButton();
        rpMessdaten = new de.cismet.tools.gui.RoundedPanel();
        lblArea = new javax.swing.JLabel();
        lblDistance = new javax.swing.JLabel();
        lblTxtDistance = new javax.swing.JLabel();
        lblTxtArea = new javax.swing.JLabel();
        semiRoundedPanel5 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel6 = new javax.swing.JLabel();
        panCenter = new javax.swing.JPanel();
        measureComponentPanel = new LayeredAlertPanel(pnlMeasureComponentWrapper, pnlAlert);
        semiRoundedPanel1 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblCurrentViewTitle = new javax.swing.JLabel();
        pnlUmleitungLink = new javax.swing.JPanel();

        pnlLink.setOpaque(false);
        pnlLink.setLayout(new java.awt.GridBagLayout());

        lblUmleitung.setForeground(new java.awt.Color(254, 254, 254));
        lblUmleitung.setText("( Umleitung auf: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        pnlLink.add(lblUmleitung, gridBagConstraints);

        jxlUmleitung.setForeground(new java.awt.Color(204, 204, 204));
        jxlUmleitung.setText("");
        jxlUmleitung.setToolTipText("");
        jxlUmleitung.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jxlUmleitungActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        pnlLink.add(jxlUmleitung, gridBagConstraints);

        lblUmleitung2.setForeground(new java.awt.Color(254, 254, 254));
        lblUmleitung2.setText(" )");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        pnlLink.add(lblUmleitung2, gridBagConstraints);

        pnlAlert.setBackground(new java.awt.Color(254, 254, 254));
        pnlAlert.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pnlAlert.setLayout(new java.awt.BorderLayout());

        pnlMeasureComponentWrapper.setLayout(new java.awt.CardLayout());

        pnlBusy.setBackground(new java.awt.Color(254, 254, 254));
        pnlBusy.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlBusy.setLayout(new java.awt.GridBagLayout());

        jxLBusyMeasure.setPreferredSize(new java.awt.Dimension(64, 64));
        pnlBusy.add(jxLBusyMeasure, new java.awt.GridBagConstraints());

        pnlMeasureComponentWrapper.add(pnlBusy, "busyCard");

        pnlMeasureComp.setLayout(new java.awt.BorderLayout());
        pnlMeasureComponentWrapper.add(pnlMeasureComp, "measureCard");

        setMinimumSize(new java.awt.Dimension(800, 700));
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(800, 700));
        setLayout(new java.awt.BorderLayout());

        panPicNavigation.setMinimumSize(new java.awt.Dimension(180, 1000));
        panPicNavigation.setName(""); // NOI18N
        panPicNavigation.setOpaque(false);
        panPicNavigation.setPreferredSize(new java.awt.Dimension(180, 1000));
        panPicNavigation.setLayout(new java.awt.GridBagLayout());

        panBlattberichte.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel6.setBackground(java.awt.Color.darkGray);
        semiRoundedPanel6.setLayout(new java.awt.GridBagLayout());

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Baulastbericht");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        semiRoundedPanel6.add(jLabel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panBlattberichte.add(semiRoundedPanel6, gridBagConstraints);

        jPanel4.setOpaque(false);
        jPanel4.setLayout(new java.awt.GridBagLayout());

        jXHyperlink1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/icons/pdf.png"))); // NOI18N
        jXHyperlink1.setText("mit Textblättern");
        jXHyperlink1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jXHyperlink1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 5, 2);
        jPanel4.add(jXHyperlink1, gridBagConstraints);

        jXHyperlink2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/icons/pdf.png"))); // NOI18N
        jXHyperlink2.setText("<html>mit Textblättern<br/> und Plänen");
        jXHyperlink2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jXHyperlink2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 5, 2);
        jPanel4.add(jXHyperlink2, gridBagConstraints);

        jXHyperlink3.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/pdf_blr.png"))); // NOI18N
        jXHyperlink3.setText("<html>mit Textblatt, Plan und<br/>Rasterdokumenten");
        jXHyperlink3.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jXHyperlink3ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 5, 2);
        jPanel4.add(jXHyperlink3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weighty = 1.0;
        panBlattberichte.add(jPanel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 7);
        panPicNavigation.add(panBlattberichte, gridBagConstraints);

        spDocuments.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel2.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanel2.setLayout(new java.awt.FlowLayout());

        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Dokumentauswahl");
        semiRoundedPanel2.add(jLabel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        spDocuments.add(semiRoundedPanel2, gridBagConstraints);

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridLayout(1, 0, 5, 0));

        btnGrpDocs.add(btnPlan);
        btnPlan.setText("Plan");
        btnPlan.setPreferredSize(new java.awt.Dimension(53, 33));
        btnPlan.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnPlanActionPerformed(evt);
                }
            });
        jPanel2.add(btnPlan);

        btnGrpDocs.add(btnTextblatt);
        btnTextblatt.setSelected(true);
        btnTextblatt.setText("Textblatt");
        btnTextblatt.setPreferredSize(new java.awt.Dimension(53, 33));
        btnTextblatt.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnTextblattActionPerformed(evt);
                }
            });
        jPanel2.add(btnTextblatt);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        spDocuments.add(jPanel2, gridBagConstraints);

        if (showDocTypePanelEnabled) {
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 7);
            panPicNavigation.add(spDocuments, gridBagConstraints);
        }

        rpSeiten.setLayout(new java.awt.GridBagLayout());

        scpPictureList.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        scpPictureList.setMinimumSize(new java.awt.Dimension(87, 140));
        scpPictureList.setOpaque(false);

        lstPictures.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstPictures.setEnabled(false);
        lstPictures.setFixedCellWidth(75);
        lstPictures.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

                @Override
                public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
                    lstPicturesValueChanged(evt);
                }
            });
        scpPictureList.setViewportView(lstPictures);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        rpSeiten.add(scpPictureList, gridBagConstraints);

        semiRoundedPanel3.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanel3.setLayout(new java.awt.FlowLayout());

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Seitenauswahl");
        semiRoundedPanel3.add(jLabel2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpSeiten.add(semiRoundedPanel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 7);
        panPicNavigation.add(rpSeiten, gridBagConstraints);

        rpControls.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel4.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanel4.setLayout(new java.awt.FlowLayout());

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Steuerung");
        semiRoundedPanel4.add(jLabel3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpControls.add(semiRoundedPanel4, gridBagConstraints);

        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.GridLayout(0, 1, 5, 5));

        btnHome.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/home.gif"))); // NOI18N
        btnHome.setText("Übersicht");
        btnHome.setToolTipText("Übersicht");
        btnHome.setFocusPainted(false);
        btnHome.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnHome.setMinimumSize(new java.awt.Dimension(89, 29));
        btnHome.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnHomeActionPerformed(evt);
                }
            });
        jPanel3.add(btnHome);

        buttonGrpMode.add(togPan);
        togPan.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/pan.gif"))); // NOI18N
        togPan.setSelected(true);
        togPan.setText("Verschieben");
        togPan.setToolTipText("Verschieben");
        togPan.setFocusPainted(false);
        togPan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        togPan.setMinimumSize(new java.awt.Dimension(89, 29));
        togPan.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    togPanActionPerformed(evt);
                }
            });
        jPanel3.add(togPan);

        buttonGrpMode.add(togZoom);
        togZoom.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/zoom.gif"))); // NOI18N
        togZoom.setText("Zoomen");
        togZoom.setToolTipText("Zoomen");
        togZoom.setFocusPainted(false);
        togZoom.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        togZoom.setMinimumSize(new java.awt.Dimension(89, 29));
        togZoom.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    togZoomActionPerformed(evt);
                }
            });
        jPanel3.add(togZoom);

        buttonGrpMode.add(togMessenLine);
        togMessenLine.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/newLinestring.png"))); // NOI18N
        togMessenLine.setText("Messlinie");
        togMessenLine.setToolTipText("Messen (Linie)");
        togMessenLine.setFocusPainted(false);
        togMessenLine.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        togMessenLine.setMinimumSize(new java.awt.Dimension(89, 29));
        togMessenLine.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    togMessenLineActionPerformed(evt);
                }
            });
        jPanel3.add(togMessenLine);

        buttonGrpMode.add(togMessenPoly);
        togMessenPoly.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/newPolygon.png"))); // NOI18N
        togMessenPoly.setText("Messfläche");
        togMessenPoly.setToolTipText("Messen (Polygon)");
        togMessenPoly.setFocusPainted(false);
        togMessenPoly.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        togMessenPoly.setMinimumSize(new java.awt.Dimension(89, 29));
        togMessenPoly.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    togMessenPolyActionPerformed(evt);
                }
            });
        jPanel3.add(togMessenPoly);

        buttonGrpMode.add(togCalibrate);
        togCalibrate.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/screen.gif"))); // NOI18N
        togCalibrate.setText("Kalibrieren");
        togCalibrate.setToolTipText("Kalibrieren");
        togCalibrate.setEnabled(false);
        togCalibrate.setFocusPainted(false);
        togCalibrate.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        togCalibrate.setMinimumSize(new java.awt.Dimension(89, 29));
        togCalibrate.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    togCalibrateActionPerformed(evt);
                }
            });
        jPanel3.add(togCalibrate);

        btnOpen.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/folder-image.png"))); // NOI18N
        btnOpen.setText("Öffnen");
        btnOpen.setToolTipText("Download zum Öffnen in externer Anwendung");
        btnOpen.setFocusPainted(false);
        btnOpen.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnOpen.setMinimumSize(new java.awt.Dimension(89, 29));
        btnOpen.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnOpenActionPerformed(evt);
                }
            });
        jPanel3.add(btnOpen);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        rpControls.add(jPanel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 7);
        panPicNavigation.add(rpControls, gridBagConstraints);

        rpMessdaten.setLayout(new java.awt.GridBagLayout());

        lblArea.setText("-");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 5, 5);
        rpMessdaten.add(lblArea, gridBagConstraints);

        lblDistance.setText("-");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 5, 5);
        rpMessdaten.add(lblDistance, gridBagConstraints);

        lblTxtDistance.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblTxtDistance.setText("Länge/Umfang:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 3, 5);
        rpMessdaten.add(lblTxtDistance, gridBagConstraints);

        lblTxtArea.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblTxtArea.setText("Fläche:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 3, 5);
        rpMessdaten.add(lblTxtArea, gridBagConstraints);

        semiRoundedPanel5.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanel5.setLayout(new java.awt.FlowLayout());

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Messdaten");
        semiRoundedPanel5.add(jLabel6);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpMessdaten.add(semiRoundedPanel5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 7);
        panPicNavigation.add(rpMessdaten, gridBagConstraints);

        add(panPicNavigation, java.awt.BorderLayout.WEST);

        panCenter.setOpaque(false);
        panCenter.setLayout(new java.awt.BorderLayout());
        panCenter.add(measureComponentPanel, java.awt.BorderLayout.CENTER);

        semiRoundedPanel1.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanel1.setLayout(new java.awt.GridBagLayout());

        lblCurrentViewTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblCurrentViewTitle.setText("Keine Auswahl");
        semiRoundedPanel1.add(lblCurrentViewTitle, new java.awt.GridBagConstraints());

        pnlUmleitungLink.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        semiRoundedPanel1.add(pnlUmleitungLink, gridBagConstraints);

        panCenter.add(semiRoundedPanel1, java.awt.BorderLayout.NORTH);

        add(panCenter, java.awt.BorderLayout.CENTER);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void togPanActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_togPanActionPerformed
        measureComponent.actionPan();
    }                                                                          //GEN-LAST:event_togPanActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void togMessenPolyActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_togMessenPolyActionPerformed
        measureComponent.actionMeasurePolygon();
    }                                                                                 //GEN-LAST:event_togMessenPolyActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void togZoomActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_togZoomActionPerformed
        measureComponent.actionZoom();
    }                                                                           //GEN-LAST:event_togZoomActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void togMessenLineActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_togMessenLineActionPerformed
        measureComponent.actionMeasureLine();
    }                                                                                 //GEN-LAST:event_togMessenLineActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void togCalibrateActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_togCalibrateActionPerformed
        if (currentPage != NO_SELECTION) {
            final Double distance = askForDistanceValue();
            if (distance != null) {
                if (distance > 0d) {
                    measureComponent.actionCalibrate(distance);
                    final Geometry documentGeom = measureComponent.getMainDocumentGeometry();
                    try {
                        registerGeometryForPage(documentGeom, currentDocument, currentPage);
                    } catch (Exception ex) {
                        log.error(ex, ex);
                        final ErrorInfo ei = new ErrorInfo(
                                "Fehler beim Speichern der Kalibrierung",
                                "Beim Speichern der Kalibrierung ist ein Fehler aufgetreten",
                                null,
                                null,
                                ex,
                                Level.SEVERE,
                                null);
                        JXErrorPane.showDialog(this, ei);
                    }
                } else {
                    JOptionPane.showMessageDialog(
                        StaticSwingTools.getParentFrame(this),
                        "Eingegebene(r) Distanz bzw. Umfang ist kein gültiger Wert oder gleich 0.",
                        "Ungültige Eingabe",
                        JOptionPane.WARNING_MESSAGE);
                }
            }
            togPan.setSelected(true);
        }
    }                                                                                //GEN-LAST:event_togCalibrateActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnHomeActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnHomeActionPerformed
        measureComponent.actionOverview();
    }                                                                           //GEN-LAST:event_btnHomeActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnOpenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnOpenActionPerformed
        if ((currentDocument == NO_SELECTION) || (documentURLs == null) || (currentDocument >= documentURLs.length)
                    || (currentDocument < 0)) {
            return;
        }

        final URL current = documentURLs[currentDocument];

        if (current == null) {
            return;
        }

        final String path = current.toExternalForm();

        final URL url;
        url = current;

        CismetThreadPool.execute(new Runnable() {

                @Override
                public void run() {
                    final String filename = path.substring(path.lastIndexOf("/") + 1);
                    if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(Alb_picturePanel.this)) {
                        DownloadManager.instance()
                                .add(
                                    new HttpDownload(
                                        url,
                                        "",
                                        DownloadManagerDialog.getInstance().getJobName(),
                                        "Baulast",
                                        filename.substring(0, filename.lastIndexOf(".")),
                                        filename.substring(filename.lastIndexOf("."))));
                    }
                }
            });
    } //GEN-LAST:event_btnOpenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnPlanActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnPlanActionPerformed
        umleitungsPanel = new Alb_baulastUmleitungPanel(Alb_baulastUmleitungPanel.MODE.LAGEPLAN, this);
        showUmleitung = true;
        loadPlan();
        checkLinkInTitle();
    }                                                                           //GEN-LAST:event_btnPlanActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnTextblattActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnTextblattActionPerformed
        umleitungsPanel = new Alb_baulastUmleitungPanel(Alb_baulastUmleitungPanel.MODE.TEXTBLATT, this);
        showUmleitung = true;
        loadTextBlatt();
        checkLinkInTitle();
    }                                                                                //GEN-LAST:event_btnTextblattActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxlUmleitungActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jxlUmleitungActionPerformed
        umleitungsPanel.reset();
        alert.setType(AlertPanel.TYPE.SUCCESS);
        umleitungsPanel.setTextColor(AlertPanel.successMessageColor);
        final String s = jxlUmleitung.getText();
        // we need to remove the last character..
        umleitungsPanel.setLinkDocumentText(s.substring(0, s.length() - 1));
        final Alb_baulastUmleitungPanel.MODE mode;
        if (currentDocument == Alb_picturePanel.LAGEPLAN_DOCUMENT) {
            mode = Alb_baulastUmleitungPanel.MODE.LAGEPLAN;
        } else {
            mode = Alb_baulastUmleitungPanel.MODE.TEXTBLATT;
        }
        umleitungsPanel.setMode(mode);
        alert.setContent(umleitungsPanel);
        alert.setVisible(true);
        this.invalidate();
        this.validate();
        this.repaint();
    } //GEN-LAST:event_jxlUmleitungActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXHyperlink3ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jXHyperlink3ActionPerformed
        Alb_baulastReportDialog.getInstance()
                .showAndDoDownload(
                    BaulastenReportGenerator.Type.TEXTBLATT_PLAN_RASTER,
                    Arrays.asList(new CidsBean[] { cidsBean }),
                    this);
    }                                                                                //GEN-LAST:event_jXHyperlink3ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXHyperlink1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jXHyperlink1ActionPerformed
        Alb_baulastReportDialog.getInstance()
                .showAndDoDownload(
                    BaulastenReportGenerator.Type.TEXTBLATT,
                    Arrays.asList(new CidsBean[] { cidsBean }),
                    this);
    }                                                                                //GEN-LAST:event_jXHyperlink1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXHyperlink2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jXHyperlink2ActionPerformed
        Alb_baulastReportDialog.getInstance()
                .showAndDoDownload(
                    BaulastenReportGenerator.Type.TEXTBLATT_PLAN,
                    Arrays.asList(new CidsBean[] { cidsBean }),
                    this);
    }                                                                                //GEN-LAST:event_jXHyperlink2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstPicturesValueChanged(final javax.swing.event.ListSelectionEvent evt) { //GEN-FIRST:event_lstPicturesValueChanged
        if (!evt.getValueIsAdjusting()) {
            final Object selObj = lstPictures.getSelectedValue();
            if (selObj instanceof Integer) {
                final int pageNo = (Integer)selObj;
                final PictureSelectWorker oldWorkerTest = currentPictureSelectWorker;
                if (oldWorkerTest != null) {
                    oldWorkerTest.cancel(true);
                }
                currentPictureSelectWorker = new PictureSelectWorker(pageNo - 1);
                // page -> offset
                CismetThreadPool.execute(currentPictureSelectWorker);
            }
        }
    } //GEN-LAST:event_lstPicturesValueChanged

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Collection<CidsBean> getPages() {
        if (cidsBean != null) {
            Object o = null;
            if (currentDocument == TEXTBLATT_DOCUMENT) {
                o = cidsBean.getProperty("textblatt_pages");
            } else if (currentDocument == LAGEPLAN_DOCUMENT) {
                o = cidsBean.getProperty("lageplan_pages");
            }
            if (o instanceof Collection) {
                return (Collection<CidsBean>)o;
            }
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   geometry    DOCUMENT ME!
     * @param   documentNo  DOCUMENT ME!
     * @param   pageNo      DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private void registerGeometryForPage(final Geometry geometry, final int documentNo, final int pageNo)
            throws Exception {
        if ((geometry != null) && (documentNo > NO_SELECTION) && (pageNo > NO_SELECTION)) {
            final Geometry oldVal = pageGeometries.get(pageNo);
            if ((oldVal == null) || !oldVal.equals(geometry)) {
                pageGeometries.put(pageNo, geometry);
                final Collection<CidsBean> pageGeoCollection = getPages();
                if (pageGeoCollection != null) {
                    boolean pageFound = false;
                    for (final CidsBean pageGeom : pageGeoCollection) {
                        final Object pageNumberObj = pageGeom.getProperty("page_number");
                        if (pageNumberObj instanceof Integer) {
                            if (pageNo == (Integer)pageNumberObj) {
                                pageGeom.setProperty("geometry", geometry);
                                pageFound = true;
                                break;
                            }
                        }
                    }
                    if (!pageFound) {
                        final CidsBean newBean = CidsBeanSupport.createNewCidsBeanFromTableName(
                                "ALB_GEO_DOCUMENT_PAGE");
                        newBean.setProperty("page_number", pageNo);
                        newBean.setProperty("geometry", geometry);
                        pageGeoCollection.add(newBean);
                        if (log.isDebugEnabled()) {
                            log.debug(newBean.getMetaObject().getDebugString());
                        }
                    }
                    if (selfPersisting) {
                        persistBean();
                    }
                    rpMessdaten.setBackground(KALIBRIERUNG_VORHANDEN);
                    rpMessdaten.setAlpha(120);
                } else {
                    log.error("Empty Page Collection!");
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void showMeasureIsLoading() {
        jxLBusyMeasure.setBusy(true);
        final CardLayout cl = (CardLayout)pnlMeasureComponentWrapper.getLayout();
        cl.show(pnlMeasureComponentWrapper, "busyCard");
    }

    /**
     * DOCUMENT ME!
     */
    private void showMeasurePanel() {
        jxLBusyMeasure.setBusy(false);
        final CardLayout cl = (CardLayout)pnlMeasureComponentWrapper.getLayout();
        cl.show(pnlMeasureComponentWrapper, "measureCard");
    }

    /**
     * DOCUMENT ME!
     */
    private void loadPlan() {
        showMeasureIsLoading();
        if (pictureReaderWorker != null) {
            pictureReaderWorker.cancel(true);
        }
        if (currentPictureSelectWorker != null) {
            currentPictureSelectWorker.cancel(true);
        }
        currentSelectedButton = btnPlan;
        lblCurrentViewTitle.setText("Lageplan");
        currentDocument = LAGEPLAN_DOCUMENT;
        checkLinkInTitle();
        lstPictures.setEnabled(true);
        showAlert(false);
        lstPictures.setModel(new DefaultListModel());
        measureComponent.removeAllFeatures();
        if (documentURLs[currentDocument] == null) {
            showAlert(true);
            showMeasurePanel();
        } else {
            final String filename = BaulastenPictureFinder.getPlanPictureFilename(getCidsBean()).toString();
            pictureReaderWorker = new PictureReaderWorker(documentURLs[currentDocument]);
            CismetThreadPool.execute(pictureReaderWorker);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void cancelPictureWorkers() {
        if (pictureReaderWorker != null) {
            pictureReaderWorker.cancel(true);
        }
        if (currentPictureSelectWorker != null) {
            currentPictureSelectWorker.cancel(true);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void loadTextBlatt() {
        showMeasureIsLoading();
        cancelPictureWorkers();
        currentSelectedButton = btnTextblatt;
        lblCurrentViewTitle.setText("Textblatt");
        currentDocument = TEXTBLATT_DOCUMENT;
        checkLinkInTitle();
        lstPictures.setEnabled(true);
        showAlert(false);
        lstPictures.setModel(new DefaultListModel());
        measureComponent.removeAllFeatures();
        if (documentURLs[currentDocument] == null) {
            showAlert(true);
            showMeasurePanel();
            return;
        } else {
//            final String blattnummer = (String)getCidsBean().getProperty(BLATTNUMMER_PROPERTY);
//            final String lfdNummer = (String)getCidsBean().getProperty(LFDNUMMER_PROPERTY);
//            final String filename = BaulastenPictureFinder.getTextblattPictureFilename(blattnummer, lfdNummer);
            pictureReaderWorker = new PictureReaderWorker(documentURLs[currentDocument]);
            CismetThreadPool.execute(pictureReaderWorker);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void checkLinkInTitle() {
        final URL fileUrl = documentURLs[currentDocument];
        checkLinkInTitle(fileUrl);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  url  DOCUMENT ME!
     */
    private void checkLinkInTitle(final URL url) {
        showLinkInTitle(false);
        jxlUmleitung.setText("");
        final String filename = getDocumentFilename();
        if ((url != null) && !url.toString().contains(filename)) {
            jxlUmleitung.setText(extractFilenameofUrl(url));
            showLinkInTitle(true);
            this.repaint();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getDocumentFilename() {
        if (currentDocument == LAGEPLAN_DOCUMENT) {
            return BaulastenPictureFinder.getPlanPictureFilename(getCidsBean()).toString();
        } else {
            return BaulastenPictureFinder.getTextblattPictureFilename(getCidsBean()).toString();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   url  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String extractFilenameofUrl(final URL url) {
        final String[] splittedUrl = url.toString().split("/");
        final String s = splittedUrl[splittedUrl.length - 1];
        return s.substring(0, s.indexOf("."));
    }

    /**
     * DOCUMENT ME!
     */
    /**
     * DOCUMENT ME!
     */
    private void setCurrentDocumentNull() {
        currentDocument = NO_SELECTION;
        pageGeometries.clear();
        setCurrentPageNull();
    }

    /**
     * DOCUMENT ME!
     */
    private void setCurrentPageNull() {
        currentPage = NO_SELECTION;
        rpMessdaten.setBackground(Color.WHITE);
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
     */
    private void showPermissionWarning() {
        if (!alreadyWarnedAboutPermissionProblem) {
            JOptionPane.showMessageDialog(
                StaticSwingTools.getParentFrame(this),
                "Kein Schreibrecht",
                "Kein Schreibrecht für die Klasse. Änderungen werden nicht gespeichert.",
                JOptionPane.WARNING_MESSAGE);
        }
        log.warn("User has no right to save Baulast bean!");
        alreadyWarnedAboutPermissionProblem = true;
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private void persistBean() throws Exception {
        if (CidsBeanSupport.checkWritePermission(cidsBean)) {
            alreadyWarnedAboutPermissionProblem = false;
            final SwingWorker<Void, Void> persistWorker = new SwingWorker<Void, Void>() {

                    @Override
                    protected Void doInBackground() throws Exception {
                        cidsBean.persist();
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                        } catch (Exception ex) {
                            log.error(ex, ex);
                            final ErrorInfo ei = new ErrorInfo(
                                    "Fehler beim Speichern der Kalibrierung",
                                    "Beim Speichern der Kalibrierung ist ein Fehler aufgetreten",
                                    null,
                                    null,
                                    ex,
                                    Level.SEVERE,
                                    null);
                            JXErrorPane.showDialog(Alb_picturePanel.this, ei);
                        }
                    }
                };
            CismetThreadPool.execute(persistWorker);
        } else {
            showPermissionWarning();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Double askForDistanceValue() {
        try {
            final String laenge = JOptionPane.showInputDialog(
                    StaticSwingTools.getParentFrame(this),
                    "Bitte Länge bzw. Umfang in Metern eingeben:",
                    "Kalibrierung",
                    JOptionPane.QUESTION_MESSAGE);
            if (laenge != null) {
                return Math.abs(Double.parseDouble(laenge.replace(',', '.')));
            } else {
                return null;
            }
        } catch (Exception ex) {
            log.warn(ex, ex);
        }
        return 0d;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getCollisionWarning() {
        return collisionWarning;
    }

    /**
     * DOCUMENT ME!
     */
    public void clearCollisionWarning() {
        this.collisionWarning = "";
    }

    /**
     * DOCUMENT ME!
     */
    private void resetMeasureDataLabels() {
        lblTxtDistance.setText("Länge/Umfang:");
        lblDistance.setText("-");
        lblArea.setText("-");
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cf  DOCUMENT ME!
     */
    private void refreshMeasurementsInStatus(final Collection<Feature> cf) {
        double umfang = 0.0;
        double area = 0.0;
        for (final Feature f : cf) {
            final Geometry geom = f.getGeometry();
            if ((f instanceof PureNewFeature) && (geom != null)) {
                area += geom.getArea();
                umfang += geom.getLength();
                if (umfang != 0.0) {
                    if (area != 0.0) {
                        lblTxtDistance.setText("Umfang:");
                        lblDistance.setText(StaticDecimalTools.round(umfang) + " m ");
                        lblArea.setText(StaticDecimalTools.round(area) + " m²");
                    } else {
                        if (MessenGeometryListener.POLYGON.equals(
                                        measureComponent.getMessenInputListener().getMode())) {
                            // reduce polygon line length to one way
                            umfang *= 0.5;
                        }
                        lblTxtDistance.setText("Länge:");
                        lblDistance.setText(StaticDecimalTools.round(umfang) + " m ");
                        lblArea.setText("-");
                    }
                } else {
                    resetMeasureDataLabels();
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  show  DOCUMENT ME!
     */
    private void showAlert(final boolean show) {
        // this means it is editable
        if (!selfPersisting) {
            alert.setType(AlertPanel.TYPE.DANGER);
            alert.setContent(alertWarnMessage);
            alert.setVisible(show);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JPanel getDocTypePanel() {
        return jPanel2;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  flag  DOCUMENT ME!
     */
    private void showLinkInTitle(final boolean flag) {
        pnlUmleitungLink.removeAll();
        // !selfPersisting means editing
        if (flag && !selfPersisting) {
            pnlUmleitungLink.add(pnlLink);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void handleAlertClick() {
        if (isErrorMessageVisible) {
            /*
             *  in this case it is possible that already a umleitungssfile exists so we need to check if we have to set
             * a succes or a danger alert
             */
            if (showUmleitung) {
                showUmleitung = false;
                final Alb_baulastUmleitungPanel.MODE mode;
                final URL fileUrl = documentURLs[currentDocument];
                final String filename = getDocumentFilename();
                if (!((fileUrl != null) && !fileUrl.toString().contains(filename))) {
                    umleitungsPanel.reset();
                    if (currentDocument == Alb_picturePanel.LAGEPLAN_DOCUMENT) {
                        mode = Alb_baulastUmleitungPanel.MODE.LAGEPLAN;
                    } else {
                        mode = Alb_baulastUmleitungPanel.MODE.TEXTBLATT;
                    }
                    umleitungsPanel.setMode(mode);
                    umleitungsPanel.setTextColor(AlertPanel.dangerMessageColor);
                    alert.setContent(umleitungsPanel);
                }
            }
        } else {
            isErrorMessageVisible = true;
            showUmleitung = true;
            alert.setContent(alertWarnMessage);
        }
        this.invalidate();
        this.validate();
        this.repaint();
    }

    /**
     * DOCUMENT ME!
     */
    public void successAlert() {
        setCurrentPageNull();
        alert.setType(AlertPanel.TYPE.SUCCESS);
        umleitungsPanel.setTextColor(AlertPanel.successMessageColor);
        this.invalidate();
        this.validate();
        this.repaint();
    }

    /**
     * DOCUMENT ME!
     */
    public void handleNoDocumentFound() {
        cancelPictureWorkers();
        alert.setType(AlertPanel.TYPE.DANGER);
        umleitungsPanel.setTextColor(AlertPanel.dangerMessageColor);
        measureComponent.removeAllFeatures();
        this.invalidate();
        this.validate();
        this.repaint();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  url  DOCUMENT ME!
     */
    public void reloadPictureFromUrl(final URL url) {
        cancelPictureWorkers();
        showMeasureIsLoading();
        pictureReaderWorker = new PictureReaderWorker(url);
        pictureReaderWorker.execute();
    }

    /**
     * DOCUMENT ME!
     */
    public void reloadDocuments() {
        final FileSearchWorker worker = new FileSearchWorker();
        worker.execute();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  aFlag  DOCUMENT ME!
     */
    void setUmleitungChangedFlag(final boolean aFlag) {
        this.umleitungChangedFlag = aFlag;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isUmleitungChangedFlag() {
        return umleitungChangedFlag;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  url  DOCUMENT ME!
     */
    public void handleUmleitungCreated(final URL url) {
        showAlert(false);
        umleitungChangedFlag = true;
        checkLinkInTitle(url);
        final FileSearchWorker worker = new FileSearchWorker(false);
        worker.execute();
    }

    /**
     * DOCUMENT ME!
     */
    public void handleUmleitungDeleted() {
        showAlert(true);
        documentURLs[currentDocument] = null;
        measureComponent.removeAllFeatures();
        umleitungChangedFlag = true;
        this.jxlUmleitung.setText("");
        this.showLinkInTitle(false);
        this.repaint();
    }

    /**
     * DOCUMENT ME!
     */
    public void handleEscapePressed() {
        cancelPictureWorkers();
        measureComponent.removeAllFeatures();
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */

    //J-
    final class FileSearchWorker extends SwingWorker<List[], Void> {

        //~ Constructors -------------------------------------------------------
        /**
         * Creates a new FileSearchWorker object.
         */
        private boolean reloadMeasurementComp = true;

        public FileSearchWorker() {
            this(true);
        }

        public FileSearchWorker(final boolean reloadMeasuringComponent) {
            this.reloadMeasurementComp = reloadMeasuringComponent;
            if (reloadMeasurementComp) {
                measureComponent.reset();
                togPan.setSelected(true);
                resetMeasureDataLabels();
            }
        }

        //~ Methods ------------------------------------------------------------
        @Override
        protected List[] doInBackground() throws Exception {
            final List[] result = new List[2];
//            if (!StaticDebuggingTools.checkHomeForFile("BAULASTENHTTPDOCPREVIEW")) {
//                final Object blattObj = getCidsBean().getProperty(TEXTBLATT_PROPERTY);
//                final Object planObj = getCidsBean().getProperty(LAGEPLAN_PROPERTY);
//                log.info("Found blatt property " + blattObj);
//                log.info("Found plan property " + planObj);
//                if (blattObj != null) {
//                    result[TEXTBLATT_DOCUMENT] =
//                            BaulastenPictureFinder.findTextblattPicture(
//                            blattObj.toString().replaceAll("\\\\", "/"));
//                }
//                if (planObj != null) {
//                    result[LAGEPLAN_DOCUMENT] =
//                            BaulastenPictureFinder.findPlanPicture(
//                            planObj.toString().replaceAll("\\\\", "/"));
//                }
//
//            }
//            else {

            result[TEXTBLATT_DOCUMENT] = BaulastenPictureFinder.findTextblattPicture(getCidsBean());
            result[LAGEPLAN_DOCUMENT] = BaulastenPictureFinder.findPlanPicture(getCidsBean());

            log.debug("Textblätter:" + result[TEXTBLATT_DOCUMENT]);
            log.debug("Lagepläne:" + result[LAGEPLAN_DOCUMENT]);
            return result;

        }

        @Override
        protected void done() {
            try {
                final List[] result = get();
                final StringBuffer collisionLists = new StringBuffer();
                for (int i = 0; i < result.length; ++i) {
                    //cast!
                    final List<URL> current = result[i];
                    if (current != null) {
                        if (current.size() > 0) {
                            if (current.size() > 1) {
                                if (collisionLists.length() > 0) {
                                    collisionLists.append(",\n");
                                }
                                collisionLists.append(current);
                            }
                            documentURLs[i] = current.get(0);
                        }
                    }
                }
                if (collisionLists.length() > 0) {
                    collisionWarning
                            = "Achtung: im Zielverzeichnis sind mehrere Dateien mit"
                            + " demselben Namen in unterschiedlichen Dateiformaten "
                            + "vorhanden.\n\nBitte löschen Sie die ungültigen Formate "
                            + "und setzen Sie die Bearbeitung in WuNDa anschließend fort."
                            + "\n\nDateien:\n"
                            + collisionLists
                            + "\n";
                    log.info(collisionWarning);
                }
            } catch (InterruptedException ex) {
                log.warn(ex, ex);
            } catch (Exception ex) {
                log.error(ex, ex);
            } finally {
                if (reloadMeasurementComp) {
                    pathsChanged = false;
                    setEnabled(true);
                    if (btnTextblatt.isSelected()) {
                        loadTextBlatt();
                    } else if (btnPlan.isSelected()) {
                        loadPlan();
                    } else {
                        lstPictures.setModel(new DefaultListModel());
                        measureComponent.removeAllFeatures();
                        setEnabled(false);
                    }
                }
            }
        }
    }
    //J+

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    final class PictureReaderWorker extends SwingWorker<ListModel, Void> {

        //~ Instance fields ----------------------------------------------------

        private final URL pictureURL;
//        private boolean md5OK = false;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new PictureReaderWorker object.
         *
         * @param  pictureURL  DOCUMENT ME!
         */
        public PictureReaderWorker(final URL pictureURL) {
            this.pictureURL = pictureURL;
            if (log.isDebugEnabled()) {
                log.debug("prepare picture reader for file " + this.pictureURL);
            }
            lstPictures.setModel(LADEN_MODEL);
            measureComponent.removeAllFeatures();
        }

        //~ Methods ------------------------------------------------------------

        /**
         * private void updateMD5() throws Exception { expectedMD5Values[currentDocument] = currentActualDocumentMD5;
         * cidsBean.setProperty(MD5_PROPERTY_NAMES[currentDocument], currentActualDocumentMD5); log.debug("saving md5
         * value " + currentActualDocumentMD5); persistBean(); }.
         *
         * @return  DOCUMENT ME!
         *
         * @throws  Exception  DOCUMENT ME!
         */
        @Override
        protected ListModel doInBackground() throws Exception {
            final DefaultListModel model = new DefaultListModel();
            readPageGeometriesIntoMap(getPages());

            closeReader();
            pictureReader = new WebAccessMultiPagePictureReader(pictureURL);
//            pictureReader.setCaching(false);
            final int numberOfPages = pictureReader.getNumberOfPages();
            for (int i = 0; i < numberOfPages; ++i) {
                model.addElement(i + 1);
            }
            return model;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  pageGeoms  DOCUMENT ME!
         */
        private void readPageGeometriesIntoMap(final Collection<CidsBean> pageGeoms) {
            pageGeometries.clear();
            if (pageGeoms != null) {
                for (final CidsBean bean : pageGeoms) {
                    final Object pageNumberObj = bean.getProperty("page_number");
                    final Object geometryObj = bean.getProperty("geometry");
                    if ((pageNumberObj instanceof Integer) && (geometryObj instanceof Geometry)) {
                        pageGeometries.put((Integer)pageNumberObj, (Geometry)geometryObj);
                    }
                }
            }
        }

        /**
         * DOCUMENT ME!
         */
        @Override
        protected void done() {
            try {
                final ListModel model = get();
                lstPictures.setModel(model);
                if (model.getSize() > 0) {
                    lstPictures.setSelectedIndex(0);
                } else {
                    showAlert(true);
                    lstPictures.setModel(new DefaultListModel());
                }
            } catch (InterruptedException ex) {
                setCurrentDocumentNull();
                lstPictures.setModel(FEHLER_MODEL);
                log.warn(ex, ex);
            } catch (ExecutionException ex) {
                lstPictures.setModel(FEHLER_MODEL);
                setCurrentDocumentNull();
                log.error(ex, ex);
            } catch (CancellationException ex) {
                if (log.isDebugEnabled()) {
                    log.debug(ex, ex);
                }
            } finally {
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    final class PictureSelectWorker extends SwingWorker<BufferedImage, Void> {

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
            measureComponent.reset();
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         *
         * @throws  Exception              DOCUMENT ME!
         * @throws  IllegalStateException  DOCUMENT ME!
         */
        @Override
        protected BufferedImage doInBackground() throws Exception {
            if (pictureReader != null) {
                return pictureReader.loadPage(pageNumber);
            }
            throw new IllegalStateException("PictureReader is null!!");
        }

        /**
         * DOCUMENT ME!
         */
        @Override
        protected void done() {
            try {
                if (!isCancelled()) {
                    final Geometry pageGeom = pageGeometries.get(pageNumber);
                    currentPage = pageNumber;
                    measureComponent.addImage(get(), pageGeom, CrsTransformer.extractSridFromCrs(crs.getCode()));
                    togPan.setSelected(true);
                    resetMeasureDataLabels();
                    if (pageGeom != null) {
                        rpMessdaten.setBackground(KALIBRIERUNG_VORHANDEN);
                        rpMessdaten.setAlpha(120);
                    } else {
                        rpMessdaten.setBackground(Color.WHITE);
                        rpMessdaten.setAlpha(60);
                    }
                    measureComponent.zoomToFeatureCollection();
                }
            } catch (InterruptedException ex) {
                setCurrentPageNull();
                log.warn(ex, ex);
            } catch (Exception ex) {
                setCurrentPageNull();
                log.error(ex, ex);
            } finally {
                currentPictureSelectWorker = null;
                showMeasurePanel();
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    final class MessenFeatureCollectionListener extends de.cismet.cismap.commons.features.FeatureCollectionAdapter {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  fce  DOCUMENT ME!
         */
        @Override
        public void featuresAdded(final FeatureCollectionEvent fce) {
            if (!togCalibrate.isEnabled()) {
                for (final Feature f : measureComponent.getFeatureCollection().getAllFeatures()) {
                    if ((f instanceof PureNewFeature) && !(f.getGeometry() instanceof Point)) {
                        // messgeometrie gefunden
                        togCalibrate.setEnabled(true);
                    }
                }
            }
            refreshMeasurementsInStatus(fce.getEventFeatures());
        }

        /**
         * DOCUMENT ME!
         *
         * @param  fce  DOCUMENT ME!
         */
        @Override
        public void featuresRemoved(final FeatureCollectionEvent fce) {
            if (togCalibrate.isEnabled()) {
                for (final Feature f : measureComponent.getFeatureCollection().getAllFeatures()) {
                    if ((f instanceof PureNewFeature) && !(f.getGeometry() instanceof Point)) {
                        // messgeometrie gefunden.
                        return;
                    }
                }
                togCalibrate.setEnabled(false);
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @param  fce  DOCUMENT ME!
         */
        @Override
        public void allFeaturesRemoved(final FeatureCollectionEvent fce) {
            featuresRemoved(fce);
        }

        /**
         * DOCUMENT ME!
         *
         * @param  fce  DOCUMENT ME!
         */
        @Override
        public void featuresChanged(final FeatureCollectionEvent fce) {
            refreshMeasurementsInStatus(fce.getEventFeatures());
        }

        /**
         * DOCUMENT ME!
         *
         * @param  fce  DOCUMENT ME!
         */
        @Override
        public void featureSelectionChanged(final FeatureCollectionEvent fce) {
            refreshMeasurementsInStatus(fce.getEventFeatures());
        }
    }
}
