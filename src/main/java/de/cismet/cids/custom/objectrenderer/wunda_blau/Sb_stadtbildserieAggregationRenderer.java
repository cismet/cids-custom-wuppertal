/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;
import Sirius.navigator.ui.ComponentRegistry;
import Sirius.navigator.ui.RequestsFullSizeComponent;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import com.guigarage.jgrid.JGrid;

import com.vividsolutions.jts.geom.Geometry;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.openide.util.Exceptions;

import java.awt.CardLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.clientutils.Sb_RestrictionLevelUtils;
import de.cismet.cids.custom.clientutils.StadtbilderUtils;
import de.cismet.cids.custom.clientutils.TifferDownload;
import de.cismet.cids.custom.objectrenderer.utils.billing.BillingPopup;
import de.cismet.cids.custom.utils.billing.BillingProductGroupAmount;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanAggregationRenderer;

import de.cismet.cismap.commons.gui.printing.JasperReportDownload;

import de.cismet.commons.concurrency.CismetExecutors;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.gui.FooterComponentProvider;
import de.cismet.tools.gui.TitleComponentProvider;
import de.cismet.tools.gui.downloadmanager.Download;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;
import de.cismet.tools.gui.downloadmanager.MultipleDownload;

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
    ListDataListener,
    Sb_stadtbildserieGridObjectListener,
    ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            Sb_stadtbildserieGridRenderer.class);

    private static final Icon BIN_EMPTY = new javax.swing.ImageIcon(Sb_stadtbildserieAggregationRenderer.class
                    .getResource("/de/cismet/cids/custom/objectrenderer/wunda_blau/bin_empty.png"));
    private static final Icon BIN_FULL = new javax.swing.ImageIcon(Sb_stadtbildserieAggregationRenderer.class
                    .getResource("/de/cismet/cids/custom/objectrenderer/wunda_blau/bin.png"));
    private static final Icon BIN_RECYCLE = new javax.swing.ImageIcon(Sb_stadtbildserieAggregationRenderer.class
                    .getResource("/de/cismet/cids/custom/objectrenderer/wunda_blau/bin_recycle.png"));

    private static final String REPORT_STADTBILDSERIE_URL =
        "/de/cismet/cids/custom/reports/wunda_blau/Stadtbildbericht.jasper";
    private static final String REPORT_STADTBILDVORSCHAU_URL =
        "/de/cismet/cids/custom/reports/wunda_blau/Stadtbildvorschaubericht.jasper";

    private static final String DOMAIN = "WUNDA_BLAU";
    private static final ExecutorService highResAvailableThreadPool = CismetExecutors.newFixedThreadPool(20);

    //~ Instance fields --------------------------------------------------------

    private final Action vorauswahlReportAction;
    private final Action vorauswahlDownloadAction;
    private final Action warenkorbReportAction;
    private final Action warenkorbDownloadAction;

    private boolean wasInfoPanelVisibleBeforeSwitch = true;

    private Collection<CidsBean> cidsBeans = null;
    private HashSet<String> highResStadtbilder = new HashSet<>();
    private HashSet<String> selectedStadtbilder = new HashSet<>();
    private HashSet<String> vorschauStadtbilder = new HashSet<>();

    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBin;
    private javax.swing.JButton btnBinRecycle;
    private javax.swing.JButton btnDownloadHighResImage;
    private javax.swing.JButton btnMoveSerienToWarenkorb;
    private javax.swing.JButton btnRemoveWarenkorb;
    private javax.swing.JButton btnReport;
    private javax.swing.Box.Filler filler1;
    private com.guigarage.jgrid.JGrid grdBin;
    private com.guigarage.jgrid.JGrid grdStadtbildserien;
    private com.guigarage.jgrid.JGrid grdWarenkorb;
    private de.cismet.cids.custom.objectrenderer.wunda_blau.Sb_stadtbildserieAggregationRendererInfoPanel
        infoNotAvailable;
    private de.cismet.cids.custom.objectrenderer.wunda_blau.Sb_stadtbildserieAggregationRendererInfoPanel infoPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblMiddle;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblSwitchToBin;
    private javax.swing.JLabel lblSwitchToSerie;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel panButtons;
    private javax.swing.JPanel panFooter;
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
     * Creates a new Sb_stadtbildserieAggregationRenderer object.
     */
    public Sb_stadtbildserieAggregationRenderer() {
        vorauswahlReportAction = new AbstractAction(
                null,
                new javax.swing.ImageIcon(
                    getClass().getResource("/de/cismet/cids/custom/icons/vorauswahlReport.png"))) {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    reportVorauswahl();
                }
            };
        vorauswahlDownloadAction = new AbstractAction(
                null,
                new javax.swing.ImageIcon(
                    getClass().getResource("/de/cismet/cids/custom/icons/vorauswahlDownload.png"))) {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    downladVorauswahl();
                }
            };

        warenkorbReportAction = new AbstractAction(
                null,
                new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/icons/warenkorbReport.png"))) {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    reportWarenkorb();
                }
            };
        warenkorbDownloadAction = new AbstractAction(
                null,
                new javax.swing.ImageIcon(
                    getClass().getResource("/de/cismet/cids/custom/icons/warenkorbDownload.png"))) {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    downladWarenkorb();
                }
            };
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        vorauswahlReportAction.putValue(
            Action.SHORT_DESCRIPTION,
            org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieAggregationRenderer.class,
                "Sb_stadtbildserieAggregationRenderer.actionVorauswahlReport.toolTipText"));
        vorauswahlReportAction.setEnabled(true);

        warenkorbReportAction.putValue(
            Action.SHORT_DESCRIPTION,
            org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieAggregationRenderer.class,
                "Sb_stadtbildserieAggregationRenderer.actionWarenkorbReport.toolTipText"));
        warenkorbReportAction.setEnabled(false);

        initComponents();

        refreshVorauswahlDownloadAction();
        refreshWarenkorbDownloadAction();

        btnDownloadHighResImage.setAction(vorauswahlDownloadAction);
        btnReport.setAction(vorauswahlReportAction);

        infoNotAvailable.previewImageNotAvailable();
        infoNotAvailable.setEnableTable(false);
        ((PictureSelectionJGrid)grdStadtbildserien).updateInfoPanel();

        sldSize.setValue(grdStadtbildserien.getFixedCellDimension());
        switchToSerie();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panFooter = new javax.swing.JPanel();
        panButtons = new javax.swing.JPanel();
        lblSwitchToSerie = new javax.swing.JLabel();
        lblMiddle = new javax.swing.JLabel();
        lblSwitchToBin = new javax.swing.JLabel();
        panTitle = new javax.swing.JPanel();
        panTitleString = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btnDownloadHighResImage = new javax.swing.JButton();
        btnReport = new javax.swing.JButton();
        tbtnSlide = new javax.swing.JToggleButton();
        roundedPanel1 = new de.cismet.tools.gui.RoundedPanel();
        btnBin = new javax.swing.JButton();
        btnRemoveWarenkorb = new javax.swing.JButton();
        btnBinRecycle = new javax.swing.JButton();
        pnlLeuchtkasten = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        grdWarenkorb = new de.cismet.cids.custom.objectrenderer.wunda_blau.Sb_SingleStadtbildJGrid(
                getConnectionContext());
        jScrollPane2 = new javax.swing.JScrollPane();
        grdBin = new PictureSelectionJGrid();
        jScrollPane1 = new javax.swing.JScrollPane();
        grdStadtbildserien = new PictureSelectionJGrid();
        pnlSlider = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        sldSize = new javax.swing.JSlider();
        jLabel2 = new javax.swing.JLabel();
        btnMoveSerienToWarenkorb = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        pnlInfoPanels = new javax.swing.JPanel();
        infoPanel = new de.cismet.cids.custom.objectrenderer.wunda_blau.Sb_stadtbildserieAggregationRendererInfoPanel(
                getConnectionContext());
        infoNotAvailable =
            new de.cismet.cids.custom.objectrenderer.wunda_blau.Sb_stadtbildserieAggregationRendererInfoPanel(
                getConnectionContext());
        lblSubtitle = new javax.swing.JLabel();

        panFooter.setOpaque(false);
        panFooter.setLayout(new java.awt.GridBagLayout());

        panButtons.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 6, 0));
        panButtons.setOpaque(false);
        panButtons.setLayout(new java.awt.GridBagLayout());

        lblSwitchToSerie.setFont(new java.awt.Font("Tahoma", 1, 14));                                       // NOI18N
        lblSwitchToSerie.setForeground(new java.awt.Color(255, 255, 255));
        lblSwitchToSerie.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblSwitchToSerie.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objectrenderer/wunda_blau/magnifier.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblSwitchToSerie,
            org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieAggregationRenderer.class,
                "Sb_stadtbildserieAggregationRenderer.lblSwitchToSerie.text"));                             // NOI18N
        lblSwitchToSerie.setEnabled(false);
        lblSwitchToSerie.setPreferredSize(new java.awt.Dimension(186, 17));
        lblSwitchToSerie.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblSwitchToSerieMouseClicked(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        panButtons.add(lblSwitchToSerie, gridBagConstraints);

        lblMiddle.setFont(new java.awt.Font("Tahoma", 1, 14));
        lblMiddle.setForeground(new java.awt.Color(255, 255, 255));
        lblMiddle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMiddle.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objectrenderer/wunda_blau/basket_shopping.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblMiddle,
            org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieAggregationRenderer.class,
                "Sb_stadtbildserieAggregationRenderer.lblMiddle.text"));                                          // NOI18N
        lblMiddle.setPreferredSize(new java.awt.Dimension(212, 24));
        lblMiddle.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblMiddleMouseClicked(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 20);
        panButtons.add(lblMiddle, gridBagConstraints);

        lblSwitchToBin.setFont(new java.awt.Font("Tahoma", 1, 14));                                         // NOI18N
        lblSwitchToBin.setForeground(new java.awt.Color(255, 255, 255));
        lblSwitchToBin.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objectrenderer/wunda_blau/bin_empty.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblSwitchToBin,
            org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieAggregationRenderer.class,
                "Sb_stadtbildserieAggregationRenderer.lblSwitchToBin.text"));                               // NOI18N
        lblSwitchToBin.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblSwitchToBinMouseClicked(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panButtons.add(lblSwitchToBin, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panFooter.add(panButtons, gridBagConstraints);

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

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        btnDownloadHighResImage.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objectrenderer/wunda_blau/download.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnDownloadHighResImage,
            org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieAggregationRenderer.class,
                "Sb_stadtbildserieAggregationRenderer.btnDownloadHighResImage.text"));                     // NOI18N
        btnDownloadHighResImage.setToolTipText(org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieAggregationRenderer.class,
                "Sb_stadtbildserieAggregationRenderer.btnDownloadHighResImage.toolTipText"));              // NOI18N
        btnDownloadHighResImage.setBorder(null);
        btnDownloadHighResImage.setBorderPainted(false);
        btnDownloadHighResImage.setContentAreaFilled(false);
        btnDownloadHighResImage.setEnabled(false);
        btnDownloadHighResImage.setFocusPainted(false);
        btnDownloadHighResImage.setMaximumSize(new java.awt.Dimension(30, 30));
        btnDownloadHighResImage.setMinimumSize(new java.awt.Dimension(30, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(btnDownloadHighResImage, gridBagConstraints);

        btnReport.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/printer.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnReport,
            org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieAggregationRenderer.class,
                "Sb_stadtbildserieAggregationRenderer.btnReport.text"));              // NOI18N
        btnReport.setToolTipText(org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieAggregationRenderer.class,
                "Sb_stadtbildserieAggregationRenderer.btnReport.toolTipText"));       // NOI18N
        btnReport.setBorderPainted(false);
        btnReport.setContentAreaFilled(false);
        btnReport.setEnabled(false);
        btnReport.setFocusPainted(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(btnReport, gridBagConstraints);

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
        jPanel1.add(tbtnSlide, new java.awt.GridBagConstraints());

        panTitle.add(jPanel1, java.awt.BorderLayout.EAST);

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
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        roundedPanel1.add(btnBin, gridBagConstraints);

        btnRemoveWarenkorb.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objectrenderer/wunda_blau/basket_shopping-minus.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnRemoveWarenkorb,
            org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieAggregationRenderer.class,
                "Sb_stadtbildserieAggregationRenderer.btnRemoveWarenkorb.text"));                                       // NOI18N
        btnRemoveWarenkorb.setToolTipText(org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieAggregationRenderer.class,
                "Sb_stadtbildserieAggregationRenderer.btnRemoveWarenkorb.toolTipText"));                                // NOI18N
        btnRemoveWarenkorb.setBorderPainted(false);
        btnRemoveWarenkorb.setContentAreaFilled(false);
        btnRemoveWarenkorb.setFocusPainted(false);
        btnRemoveWarenkorb.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemoveWarenkorbActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        roundedPanel1.add(btnRemoveWarenkorb, gridBagConstraints);
        btnRemoveWarenkorb.setVisible(false);

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
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        roundedPanel1.add(btnBinRecycle, gridBagConstraints);
        btnBinRecycle.setVisible(false);

        pnlLeuchtkasten.setOpaque(false);
        pnlLeuchtkasten.setLayout(new java.awt.CardLayout());

        jScrollPane3.setBorder(null);
        jScrollPane3.setOpaque(false);

        grdWarenkorb.setOpaque(false);
        jScrollPane3.setViewportView(grdWarenkorb);

        pnlLeuchtkasten.add(jScrollPane3, "WARENKORB");
        jScrollPane3.getViewport().setOpaque(false);

        jScrollPane2.setBorder(null);
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setOpaque(false);

        grdBin.setOpaque(false);
        jScrollPane2.setViewportView(grdBin);

        pnlLeuchtkasten.add(jScrollPane2, "BIN");
        jScrollPane2.getViewport().setOpaque(false);

        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setOpaque(false);

        grdStadtbildserien.setOpaque(false);
        jScrollPane1.setViewportView(grdStadtbildserien);
        grdStadtbildserien.getModel().addListDataListener(this);

        pnlLeuchtkasten.add(jScrollPane1, "SERIEN");
        jScrollPane1.getViewport().setOpaque(false);

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
        sldSize.setMinimumSize(new java.awt.Dimension(200, 16));
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

        btnMoveSerienToWarenkorb.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objectrenderer/wunda_blau/basket_shopping.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnMoveSerienToWarenkorb,
            org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieAggregationRenderer.class,
                "Sb_stadtbildserieAggregationRenderer.btnMoveSerienToWarenkorb.text"));                           // NOI18N
        btnMoveSerienToWarenkorb.setToolTipText(org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieAggregationRenderer.class,
                "Sb_stadtbildserieAggregationRenderer.btnMoveSerienToWarenkorb.toolTipText"));                    // NOI18N
        btnMoveSerienToWarenkorb.setBorderPainted(false);
        btnMoveSerienToWarenkorb.setContentAreaFilled(false);
        btnMoveSerienToWarenkorb.setFocusPainted(false);
        btnMoveSerienToWarenkorb.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnMoveSerienToWarenkorbActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        roundedPanel1.add(btnMoveSerienToWarenkorb, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        roundedPanel1.add(filler1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
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
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(pnlInfoPanels, gridBagConstraints);

        lblSubtitle.setFont(new java.awt.Font("Dialog", 1, 18));           // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblSubtitle,
            org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieAggregationRenderer.class,
                "Sb_stadtbildserieAggregationRenderer.lblSubtitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 5, 5);
        add(lblSubtitle, gridBagConstraints);
    }                                                                      // </editor-fold>//GEN-END:initComponents

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
        grdWarenkorb.setFixedCellDimension(sldSize.getValue());
        grdWarenkorb.ensureIndexIsVisible(grdWarenkorb.getSelectedIndex());
    }                                                                           //GEN-LAST:event_sldSizeStateChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void tbtnSlideActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_tbtnSlideActionPerformed
        showInfoPanel(!pnlInfoPanels.isVisible());
        wasInfoPanelVisibleBeforeSwitch = pnlInfoPanels.isVisible();
    }                                                                             //GEN-LAST:event_tbtnSlideActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  show  DOCUMENT ME!
     */
    private void showInfoPanel(final boolean show) {
        if (show == pnlInfoPanels.isVisible()) {
            return;
        }

        // hack to properly resize the grids
        grdStadtbildserien.setVisible(false);
        grdBin.setVisible(false);
        grdWarenkorb.setVisible(false);
        pnlInfoPanels.setVisible(show);

        SwingUtilities.invokeLater(
            new Runnable() {

                @Override
                public void run() {
                    grdStadtbildserien.setVisible(true);
                    grdBin.setVisible(true);
                    grdWarenkorb.setVisible(true);

                    grdStadtbildserien.ensureIndexIsVisible(grdStadtbildserien.getSelectedIndex());
                    grdBin.ensureIndexIsVisible(grdBin.getSelectedIndex());
                }
            });
    }

    /**
     * DOCUMENT ME!
     */
    private void reportVorauswahl() {
        final JasperReportDownload.JasperReportDataSourceGenerator dataSourceGenerator =
            new JasperReportDownload.JasperReportDataSourceGenerator() {

                @Override
                public JRDataSource generateDataSource() {
                    final ArrayList<StadtbildReportBean> stadtbilderReportBeans = new ArrayList<>();

                    final Enumeration<Sb_stadtbildserieGridObject> e = ((DefaultListModel)grdStadtbildserien.getModel())
                                .elements();
                    while (e.hasMoreElements()) {
                        final Sb_stadtbildserieGridObject gridObject = (Sb_stadtbildserieGridObject)e.nextElement();

                        final CidsBean stadtbildserie = gridObject.getCidsBean();
                        final CidsBean stadtbild = (CidsBean)stadtbildserie.getProperty("vorschaubild");
                        final boolean previewAllowed = Sb_RestrictionLevelUtils
                                    .determineRestrictionLevelForStadtbildserie(
                                            stadtbildserie,
                                            getConnectionContext()).isPreviewAllowed();
                        final StadtbilderUtils.StadtbildInfo stadtbildInfo = new StadtbilderUtils.StadtbildInfo(
                                stadtbildserie,
                                stadtbild);
                        Image image;
                        if (previewAllowed) {
                            try {
                                image = StadtbilderUtils.downloadImageForBildnummer(stadtbildInfo);
                            } catch (Exception ex) {
                                LOG.error("Image could not be fetched.", ex);
                                image = StadtbilderUtils.ERROR_IMAGE;
                            }
                            stadtbilderReportBeans.add(new StadtbildReportBean(
                                    stadtbildserie,
                                    (CidsBean)stadtbildserie.getProperty("vorschaubild"),
                                    image));
                        }
                    }

                    final JRBeanCollectionDataSource beanArray = new JRBeanCollectionDataSource(stadtbilderReportBeans);
                    return beanArray;
                }
            };

        if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(
                        ComponentRegistry.getRegistry().getMainWindow())) {
            final String jobname = DownloadManagerDialog.getInstance().getJobName();
            final String filename = "Stadtbilder_Serienauszug";
            final String downloadTitle = "Stadtbilder Serienauszug";

            final String resourceName = REPORT_STADTBILDVORSCHAU_URL;
            final JasperReportDownload download = new JasperReportDownload(
                    resourceName,
                    dataSourceGenerator,
                    jobname,
                    downloadTitle,
                    filename);
            DownloadManager.instance().add(download);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void reportWarenkorb() {
        if (this.getSelectedStadtbilderAmount() <= 0) {
            JOptionPane.showMessageDialog(
                this,
                "<html>Der Bericht kann nicht erstellt werden, wenn keine Stadtbilder ausgewählt wurden.</html>",
                "Keine Stadtbilder ausgewählt",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        final JasperReportDownload.JasperReportDataSourceGenerator dataSourceGenerator =
            new JasperReportDownload.JasperReportDataSourceGenerator() {

                @Override
                public JRDataSource generateDataSource() {
                    final ArrayList<StadtbildReportBean> stadtbilderReportBeans = new ArrayList<>();

                    final Enumeration<Sb_stadtbildserieGridObject> e = ((DefaultListModel)grdStadtbildserien.getModel())
                                .elements();
                    while (e.hasMoreElements()) {
                        final Sb_stadtbildserieGridObject gridObject = (Sb_stadtbildserieGridObject)e.nextElement();

                        final CidsBean stadtbildserie = gridObject.getCidsBean();
                        final Set<CidsBean> stadtbilder = gridObject.getSelectedBildnummernOfSerie();
                        final boolean previewAllowed = Sb_RestrictionLevelUtils
                                    .determineRestrictionLevelForStadtbildserie(
                                            stadtbildserie,
                                            getConnectionContext()).isPreviewAllowed();
                        for (final CidsBean stadtbild : stadtbilder) {
                            final StadtbilderUtils.StadtbildInfo stadtbildInfo = new StadtbilderUtils.StadtbildInfo(
                                    stadtbildserie,
                                    stadtbild);
                            Image image;
                            if (previewAllowed) {
                                try {
                                    image = StadtbilderUtils.downloadImageForBildnummer(stadtbildInfo);
                                } catch (Exception ex) {
                                    LOG.error("Image could not be fetched.", ex);
                                    image = StadtbilderUtils.ERROR_IMAGE;
                                }
                            } else {
                                image = StadtbilderUtils.ERROR_IMAGE;
                            }
                            stadtbilderReportBeans.add(new StadtbildReportBean(stadtbildserie, stadtbild, image));
                        }
                    }

                    final JRBeanCollectionDataSource beanArray = new JRBeanCollectionDataSource(stadtbilderReportBeans);
                    return beanArray;
                }
            };

        if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(
                        ComponentRegistry.getRegistry().getMainWindow())) {
            final String jobname = DownloadManagerDialog.getInstance().getJobName();
            final String filename = "Stadtbilder_Einzelbilderauszug";
            final String downloadTitle = "Stadtbilder Einzelbilderauszug";
            final String resourceName = REPORT_STADTBILDSERIE_URL;

            final JasperReportDownload download = new JasperReportDownload(
                    resourceName,
                    dataSourceGenerator,
                    jobname,
                    downloadTitle,
                    filename);
            DownloadManager.instance().add(download);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblSwitchToSerieMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblSwitchToSerieMouseClicked
        switchToSerie();
    }                                                                                //GEN-LAST:event_lblSwitchToSerieMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblSwitchToBinMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblSwitchToBinMouseClicked
        switchToBin();
    }                                                                              //GEN-LAST:event_lblSwitchToBinMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblMiddleMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblMiddleMouseClicked
        switchToWarenkorb();
    }                                                                         //GEN-LAST:event_lblMiddleMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveWarenkorbActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemoveWarenkorbActionPerformed
        ((Sb_SingleStadtbildJGrid)grdWarenkorb).unchoseStadtbilderSelectedInTheGrid();
        grdWarenkorb.getSelectionModel().clearSelection();
    }                                                                                      //GEN-LAST:event_btnRemoveWarenkorbActionPerformed

    /**
     * DOCUMENT ME!
     */
    private void downladVorauswahl() {
        final String jobname = DownloadManagerDialog.getInstance().getJobName();
        // create an array with Sb_stadtbildserieGridObject of the current vorschau.
        final Sb_stadtbildserieGridObject[] gridObjectArr =
            new Sb_stadtbildserieGridObject[grdStadtbildserien.getModel().getSize()];
        ((DefaultListModel)grdStadtbildserien.getModel()).copyInto(gridObjectArr);

        // the download has to be started in a SwingWorker, because it has to be checked which images are available in
        // high resolution. this is done via a network connection and therefore might freeze the GUI otherwise.
        new SwingWorker<Void, Void>() {

                final ArrayList<Download> downloads = new ArrayList<>();

                @Override
                protected Void doInBackground() throws Exception {
                    // iterate over the Sb_stadtbildserieGridObject then over the selected Stadtbilder of each
                    // GridObject
                    for (final Sb_stadtbildserieGridObject gridObject : gridObjectArr) {
                        final CidsBean stadtbildserie = gridObject.getCidsBean();
                        final boolean downloadAllowed = Sb_RestrictionLevelUtils
                                    .determineRestrictionLevelForStadtbildserie(
                                            stadtbildserie,
                                            getConnectionContext()).isDownloadAllowed();
                        if (downloadAllowed) {
                            final CidsBean stadtbild = (CidsBean)gridObject.getStadtbildserie()
                                        .getProperty("vorschaubild");
                            final StadtbilderUtils.StadtbildInfo stadtbildInfo = gridObject.getStadtbildInfo();
                            if (StadtbilderUtils.getFormatOfHighResPicture(stadtbildInfo) != null) {
                                final String imageNumber = stadtbildInfo.getBildnummer();
                                downloads.add(new TifferDownload(
                                        jobname,
                                        "Stadtbild "
                                                + imageNumber,
                                        "stadtbild_"
                                                + imageNumber,
                                        stadtbildInfo,
                                        "1",
                                        getConnectionContext()));
                            }
                        }
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        final int amountDownloads = downloads.size();
                        if (
                            BillingPopup.doBilling(
                                        "stb",
                                        "not.yet",
                                        (Geometry)null,
                                        getConnectionContext(),
                                        new BillingProductGroupAmount("ea", amountDownloads))
                                    && DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(
                                        Sb_stadtbildserieAggregationRenderer.this)) {
                            if (amountDownloads == 1) {
                                DownloadManager.instance().add(downloads.get(0));
                            } else if (amountDownloads > 1) {
                                DownloadManager.instance().add(new MultipleDownload(downloads, "Vorschau Stadtbilder"));
                            }
                        }
                    } catch (Exception ex) {
                        LOG.error("Error when trying to download the high res image(s)", ex);
                    }
                }
            }.execute();
    }

    /**
     * DOCUMENT ME!
     */
    private void downladWarenkorb() {
        final String jobname = DownloadManagerDialog.getInstance().getJobName();
        // create an array with Sb_stadtbildserieGridObject of the current vorschau.
        final Sb_stadtbildserieGridObject[] gridObjectArr =
            new Sb_stadtbildserieGridObject[grdStadtbildserien.getModel().getSize()];
        ((DefaultListModel)grdStadtbildserien.getModel()).copyInto(gridObjectArr);

        // the download has to be started in a SwingWorker, because it has to be checked which images are available in
        // high resolution. this is done via a network connection and therefore might freeze the GUI otherwise.
        new SwingWorker<Void, Void>() {

                final ArrayList<Download> downloads = new ArrayList<>();

                @Override
                protected Void doInBackground() throws Exception {
                    // iterate over the Sb_stadtbildserieGridObject then over the selected Stadtbilder of each
                    // GridObject
                    for (final Sb_stadtbildserieGridObject gridObject : gridObjectArr) {
                        final CidsBean stadtbildserie = gridObject.getCidsBean();
                        final boolean downloadAllowed = Sb_RestrictionLevelUtils
                                    .determineRestrictionLevelForStadtbildserie(
                                            stadtbildserie,
                                            getConnectionContext()).isDownloadAllowed();
                        if (downloadAllowed) {
                            for (final CidsBean stadtbild : gridObject.getSelectedBildnummernOfSerie()) {
                                final StadtbilderUtils.StadtbildInfo stadtbildInfo = new StadtbilderUtils.StadtbildInfo(
                                        stadtbildserie,
                                        stadtbild);
                                if (StadtbilderUtils.getFormatOfHighResPicture(stadtbildInfo) != null) {
                                    final String imageNumber = (String)stadtbild.getProperty("bildnummer");
                                    downloads.add(new TifferDownload(
                                            jobname,
                                            "Stadtbild "
                                                    + imageNumber,
                                            "stadtbild_"
                                                    + imageNumber,
                                            stadtbildInfo,
                                            "1",
                                            getConnectionContext()));
                                }
                            }
                        }
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        final int amountDownloads = downloads.size();
                        if (
                            BillingPopup.doBilling(
                                        "stb",
                                        "not.yet",
                                        (Geometry)null,
                                        getConnectionContext(),
                                        new BillingProductGroupAmount("ea", amountDownloads))
                                    && DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(
                                        Sb_stadtbildserieAggregationRenderer.this)) {
                            if (amountDownloads == 1) {
                                DownloadManager.instance().add(downloads.get(0));
                            } else if (amountDownloads > 1) {
                                DownloadManager.instance()
                                        .add(new MultipleDownload(downloads, "Ausgewählte Stadtbilder"));
                            }
                        }
                    } catch (Exception ex) {
                        LOG.error("Error when trying to download the high res image(s)", ex);
                    }
                }
            }.execute();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMoveSerienToWarenkorbActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnMoveSerienToWarenkorbActionPerformed
        // select every Stadtbild of the selected Stadtbildserien
        final List<Sb_stadtbildserieGridObject> selectedStadtbildserien = grdStadtbildserien.getSelectedValuesList();
        for (final Sb_stadtbildserieGridObject stadtbildserie : selectedStadtbildserien) {
            stadtbildserie.selecteAllStadtbilder(false);
            ((Sb_SingleStadtbildJGrid)grdWarenkorb).addStadtbilder(stadtbildserie.getSelectedBildnummernOfSerie(),
                stadtbildserie);
        }
        updateFooterLabels();
        setEnableHighResDownload();
        warenkorbReportAction.setEnabled(this.getSelectedStadtbilderAmount() > 0);
    } //GEN-LAST:event_btnMoveSerienToWarenkorbActionPerformed

    /**
     * DOCUMENT ME!
     */
    private void switchToSerie() {
        final CardLayout cardLayout = (CardLayout)pnlLeuchtkasten.getLayout();
        cardLayout.show(pnlLeuchtkasten, "SERIEN");

        lblSwitchToSerie.setEnabled(false);
        lblSwitchToBin.setEnabled(true);
        lblMiddle.setEnabled(true);
        ((PictureSelectionJGrid)grdStadtbildserien).updateInfoPanel();

        btnBin.setVisible(true);
        btnBinRecycle.setVisible(false);
        btnRemoveWarenkorb.setVisible(false);

        tbtnSlide.setEnabled(true);
        showInfoPanel(wasInfoPanelVisibleBeforeSwitch);

        btnMoveSerienToWarenkorb.setVisible(true);

        lblSubtitle.setText("Vorauswahl");

        btnDownloadHighResImage.setAction(vorauswahlDownloadAction);
        btnReport.setAction(vorauswahlReportAction);
        btnReport.setVisible(true);
        btnDownloadHighResImage.setVisible(true);
    }

    /**
     * DOCUMENT ME!
     */
    private void switchToBin() {
        final CardLayout cardLayout = (CardLayout)pnlLeuchtkasten.getLayout();
        cardLayout.show(pnlLeuchtkasten, "BIN");

        lblSwitchToSerie.setEnabled(true);
        lblSwitchToBin.setEnabled(false);
        lblMiddle.setEnabled(true);
        ((PictureSelectionJGrid)grdBin).updateInfoPanel();

        btnBin.setVisible(false);
        btnBinRecycle.setVisible(true);
        btnRemoveWarenkorb.setVisible(false);

        tbtnSlide.setEnabled(true);
        showInfoPanel(wasInfoPanelVisibleBeforeSwitch);

        btnMoveSerienToWarenkorb.setVisible(false);

        lblSubtitle.setText("Papierkorb");

        btnReport.setAction(null);
        btnDownloadHighResImage.setAction(null);
        btnReport.setVisible(false);
        btnDownloadHighResImage.setVisible(false);
    }

    /**
     * DOCUMENT ME!
     */
    private void switchToWarenkorb() {
        final CardLayout cardLayout = (CardLayout)pnlLeuchtkasten.getLayout();
        cardLayout.show(pnlLeuchtkasten, "WARENKORB");

        lblSwitchToSerie.setEnabled(true);
        lblSwitchToBin.setEnabled(true);
        lblMiddle.setEnabled(false);
        ((PictureSelectionJGrid)grdBin).updateInfoPanel();

        btnBin.setVisible(false);
        btnBinRecycle.setVisible(false);
        btnRemoveWarenkorb.setVisible(true);

        tbtnSlide.setEnabled(false);
        wasInfoPanelVisibleBeforeSwitch = pnlInfoPanels.isVisible();
        showInfoPanel(false);

        btnMoveSerienToWarenkorb.setVisible(false);

        lblSubtitle.setText("Warenkorb");

        btnDownloadHighResImage.setAction(warenkorbDownloadAction);
        btnReport.setAction(warenkorbReportAction);
        btnReport.setVisible(true);
        btnDownloadHighResImage.setVisible(true);

        refreshWarenkorbDownloadAction();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  from  DOCUMENT ME!
     * @param  to    DOCUMENT ME!
     */
    private void moveSelectedStadtbildserienToOtherGrid(final JGrid from, final JGrid to) {
        final List<Sb_stadtbildserieGridObject> gridObjectsToRemove = from.getSelectedValuesList();

        final boolean movedToBin = to == grdBin;

        for (final Sb_stadtbildserieGridObject gridObject : gridObjectsToRemove) {
            ((DefaultListModel)from.getModel()).removeElement(gridObject);
            ((DefaultListModel)to.getModel()).addElement(gridObject);
            gridObject.setModel((DefaultListModel)to.getModel());
            gridObject.setIsInBin(movedToBin);
        }
        from.getSelectionModel().clearSelection();
        updateFooterLabels();
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
                final Sb_stadtbildserieGridObject gridObject = new Sb_stadtbildserieGridObject(
                        model,
                        getConnectionContext());
                gridObject.setCidsBean(bean);
                gridObject.addStadtbildChosenListener((Sb_stadtbildserieGridObjectListener)grdWarenkorb);
                gridObject.addStadtbildChosenListener(infoPanel);
                gridObject.addStadtbildChosenListener(this);
                model.addElement(gridObject);

                StadtbilderUtils.cacheImagesForStadtbilder(
                    bean,
                    bean.getBeanCollectionProperty("stadtbilder_arr"),
                    getConnectionContext());
            }
            updateFooterLabels();
            setTitle("");
            checkHighResVorschaubilder();
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
                    " id = 5 or id = 6 or id = 285195 or id = 8 or id = 9 or id = 10 or id = 11 or  id = 285198 or id = 151489 ",
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
    private void updateFooterLabels() {
        final String stadtbildserien = "Vorauswahl (" + grdStadtbildserien.getModel().getSize() + " Serien)";
        lblSwitchToSerie.setText(stadtbildserien);

        final String warenkorbText = "Warenkorb (" + getSelectedStadtbilderAmount() + " Bilder)";
        lblMiddle.setText(warenkorbText);

        final int amountSerienInBin = grdBin.getModel().getSize();
        final String bin = "Papierkorb (" + amountSerienInBin + " Serien)";
        if (amountSerienInBin == 0) {
            btnBin.setIcon(BIN_EMPTY);
        } else {
            btnBin.setIcon(BIN_FULL);
        }
        lblSwitchToBin.setText(bin);
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
     * Gets the selected Stadtbilder from the Stadtbildserien which are shown in the Vorschau-grid (grdStadtbildserien).
     *
     * @return  DOCUMENT ME!
     */
    private Collection<CidsBean> getSelectedStadtbilder() {
        final Set<CidsBean> selectedStadtbilder = new HashSet<>();

        final Enumeration<Sb_stadtbildserieGridObject> e = ((DefaultListModel)grdStadtbildserien.getModel()).elements();
        while (e.hasMoreElements()) {
            final Sb_stadtbildserieGridObject gridObject = (Sb_stadtbildserieGridObject)e.nextElement();

            final Set set = gridObject.getSelectedBildnummernOfSerie();
            selectedStadtbilder.addAll(set);
        }
        return selectedStadtbilder;
    }

    /**
     * Gets the selected Stadtbilder from the Stadtbildserien which are shown in the Vorschau-grid (grdStadtbildserien).
     */
    private void checkHighResVorschaubilder() {
        vorschauStadtbilder.clear();
        final Enumeration<Sb_stadtbildserieGridObject> e = ((DefaultListModel)grdStadtbildserien.getModel()).elements();
        while (e.hasMoreElements()) {
            final Sb_stadtbildserieGridObject gridObject = (Sb_stadtbildserieGridObject)e.nextElement();

            final CidsBean stadtbildserie = gridObject.getCidsBean();
            final CidsBean vorschauBild = (CidsBean)stadtbildserie.getProperty("vorschaubild");
            vorschauStadtbilder.add((String)vorschauBild.getProperty("bildnummer"));
            checkHighResDownloadAvailable(vorschauBild);
        }
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
        updateFooterLabels();
    }

    @Override
    public void stadtbildChosen(final Sb_stadtbildserieGridObject source, final CidsBean stadtbild) {
        final String imageNumber = (String)stadtbild.getProperty("bildnummer");
        selectedStadtbilder.add(imageNumber);
        if (!highResStadtbilder.contains(imageNumber)) {
            warenkorbDownloadAction.putValue(
                Action.SHORT_DESCRIPTION,
                createAnzahlHighResBilderTooltipText(-1, "dem Warenkorb"));
            checkHighResDownloadAvailable(stadtbild);
        }
        warenkorbReportAction.setEnabled(getSelectedStadtbilderAmount() > 0);
    }

    @Override
    public void stadtbildUnchosen(final Sb_stadtbildserieGridObject source, final CidsBean stadtbild) {
        final String imageNumber = (String)stadtbild.getProperty("bildnummer");
        if (selectedStadtbilder.contains(imageNumber)) {
            selectedStadtbilder.remove(imageNumber);
        }
        refreshWarenkorbDownloadAction();
        warenkorbReportAction.setEnabled(getSelectedStadtbilderAmount() > 0);
    }

    @Override
    public void sb_stadtbildserieGridObjectMoveToBin(final Sb_stadtbildserieGridObject source) {
        setEnableHighResDownload();
        refreshWarenkorbDownloadAction();
    }

    @Override
    public void sb_stadtbildserieGridObjectRemovedFromBin(final Sb_stadtbildserieGridObject source) {
        setEnableHighResDownload();
        warenkorbReportAction.setEnabled(getSelectedStadtbilderAmount() > 0);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  stadtbild       DOCUMENT ME!
     * @param  stadtbildSerie  DOCUMENT ME!
     */
    private void checkHighResDownloadAvailable(final CidsBean stadtbild, final CidsBean stadtbildSerie) {
        highResAvailableThreadPool.submit(new HighResDownloadChecker(stadtbild, stadtbildSerie));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  stadtbild  DOCUMENT ME!
     */
    private void checkHighResDownloadAvailable(final CidsBean stadtbild) {
        checkHighResDownloadAvailable(stadtbild, null);
    }

    /**
     * Only enable the HighResDownload button if at least one image is accessible. This means that a high-res picture
     * must exist and the download must be allowed.
     */
    private void setEnableHighResDownload() {
        // create an array with Sb_stadtbildserieGridObject of the current vorschau.
        final Sb_stadtbildserieGridObject[] gridObjectArr =
            new Sb_stadtbildserieGridObject[grdStadtbildserien.getModel().getSize()];
        ((DefaultListModel)grdStadtbildserien.getModel()).copyInto(gridObjectArr);

        warenkorbDownloadAction.setEnabled(false);
        for (final Sb_stadtbildserieGridObject gridObject : gridObjectArr) {
            final CidsBean stadtbildserie = gridObject.getCidsBean();
            for (final CidsBean stadtbild : gridObject.getSelectedBildnummernOfSerie()) {
                checkHighResDownloadAvailable(stadtbild, stadtbildserie);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private int countAnzahlVorauswahlInHighRes() {
        int count = 0;
        for (final String bildnummer : vorschauStadtbilder) {
            if (highResStadtbilder.contains(bildnummer)) {
                count++;
            }
        }
        return count;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private int countAnzahlWarenkorbInHighRes() {
        int count = 0;
        for (final CidsBean selectedStadtbild : getSelectedStadtbilder()) {
            if (highResStadtbilder.contains((String)selectedStadtbild.getProperty("bildnummer"))) {
                count++;
            }
        }
        return count;
    }

    /**
     * DOCUMENT ME!
     */
    private void refreshVorauswahlDownloadAction() {
        final int countAnzahlVorauswahlInHighRes = countAnzahlVorauswahlInHighRes();
        vorauswahlDownloadAction.setEnabled(countAnzahlVorauswahlInHighRes > 0);
        vorauswahlDownloadAction.putValue(
            Action.SHORT_DESCRIPTION,
            createAnzahlHighResBilderTooltipText(countAnzahlVorauswahlInHighRes, "der Vorauswahl"));
    }

    /**
     * DOCUMENT ME!
     */
    private void refreshWarenkorbDownloadAction() {
        final int countAnzahlWarenkorbInHighRes = countAnzahlWarenkorbInHighRes();
        warenkorbDownloadAction.setEnabled(countAnzahlWarenkorbInHighRes > 0);
        warenkorbDownloadAction.putValue(
            Action.SHORT_DESCRIPTION,
            createAnzahlHighResBilderTooltipText(countAnzahlWarenkorbInHighRes, "dem Warenkorb"));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   anzahlBilder  DOCUMENT ME!
     * @param   fillText      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static String createAnzahlHighResBilderTooltipText(final int anzahlBilder, final String fillText) {
        final String tooltipText;
        if (anzahlBilder < 0) {
            tooltipText = "Verfügbare Bilder aus " + fillText + " herunterladen (Anzahl wird ermittelt...)";
        } else if (anzahlBilder == 0) {
            tooltipText = "Keine verfügbaren Bilder in " + fillText;
        } else if (anzahlBilder == 1) {
            tooltipText = 1 + " verfügbares Bild aus " + fillText + " herunterladen";
        } else {
            tooltipText = anzahlBilder + " verfügbare Bilder aus " + fillText + " herunterladen";
        }
        return tooltipText;
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
    private class HighResDownloadChecker extends SwingWorker<Boolean, Void> {

        //~ Instance fields ----------------------------------------------------

        private CidsBean stadtbild;
        private CidsBean stadtbildSerie;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new HighResDownloadChecker object.
         *
         * @param  stadtbild       DOCUMENT ME!
         * @param  stadtbildSerie  DOCUMENT ME!
         */
        public HighResDownloadChecker(final CidsBean stadtbild, final CidsBean stadtbildSerie) {
            this.stadtbild = stadtbild;
            this.stadtbildSerie = stadtbildSerie;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected Boolean doInBackground() throws Exception {
            // determine the Stadtbildserie of the stadtbild to check if the download is allowed

            if (stadtbildSerie == null) {
                final MetaClass mc = ClassCacheMultiple.getMetaClass(
                        DOMAIN,
                        "sb_serie_bild_array",
                        getConnectionContext());
                String query = "SELECT "
                            + mc.getID()
                            + ", sb_serie_bild_array."
                            + mc.getPrimaryKey()
                            + " FROM "
                            + mc.getTableName()
                            + " WHERE "
                            + " stadtbild= " + stadtbild.getProperty("id").toString();
                try {
                    final MetaObject[] metaObjects = SessionManager.getProxy()
                                .getMetaObjectByQuery(SessionManager.getSession().getUser(),
                                    query,
                                    getConnectionContext());
                    final CidsBean array = metaObjects[0].getBean();
                    final MetaClass mcSerie = ClassCacheMultiple.getMetaClass(
                            DOMAIN,
                            "sb_stadtbildserie",
                            getConnectionContext());
                    query = "SELECT "
                                + mcSerie.getID()
                                + ",sb_stadtbildserie."
                                + mcSerie.getPrimaryKey()
                                + " FROM "
                                + mcSerie.getTableName()
                                + " WHERE "
                                + " id= " + array.getProperty("sb_stadtbildserie_reference").toString();
                    final MetaObject[] serieMetaObjects = SessionManager.getProxy()
                                .getMetaObjectByQuery(SessionManager.getSession().getUser(),
                                    query,
                                    getConnectionContext());
                    stadtbildSerie = serieMetaObjects[0].getBean();
                } catch (ConnectionException ex) {
                    LOG.error("Could not determine the Stadtbildserie of Stadtbild " + stadtbild.toString(), ex);
                    return false;
                }
            }

            final boolean downloadAllowed = Sb_RestrictionLevelUtils.determineRestrictionLevelForStadtbildserie(
                        stadtbildSerie,
                        getConnectionContext())
                        .isDownloadAllowed();
            if (downloadAllowed) {
                if (
                    StadtbilderUtils.getFormatOfHighResPicture(
                                new StadtbilderUtils.StadtbildInfo(stadtbildSerie, stadtbild))
                            != null) {
                    return true;
                }
            }
            return false;
        }

        @Override
        protected void done() {
            try {
                final boolean highResDownloadAvailable = get();
                final String imageNumber = (String)stadtbild.getProperty("bildnummer");
                if (highResDownloadAvailable) {
                    highResStadtbilder.add(imageNumber);
                } else {
                    if (highResStadtbilder.contains(imageNumber)) {
                        highResStadtbilder.remove(imageNumber);
                    }
                }
            } catch (InterruptedException ex) {
                LOG.warn(ex, ex);
            } catch (ExecutionException ex) {
                LOG.warn(ex, ex);
            } catch (CancellationException ex) {
                // do nothing - was probably canceled such that another work can run
                return;
            }

            refreshVorauswahlDownloadAction();
            refreshWarenkorbDownloadAction();
        }
    }

    /**
     * A JGrid with different adaptations for this case. E.g. on click on a grid element, information about this element
     * is shown in the InfoPanel.
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
            // only Sb_stadtbildserieGridObject will be shown and they will be renderered by
            // Sb_stadtbildserieGridRenderer
            final DefaultListModel<Sb_stadtbildserieGridObject> gridModel = new DefaultListModel<>();
            this.setModel(gridModel);
            this.getCellRendererManager()
                    .setDefaultRenderer(
                        new de.cismet.cids.custom.objectrenderer.wunda_blau.Sb_stadtbildserieGridRenderer());

            this.addMouseListener(new MouseAdapter() {

                    /**
                     * On double click, select or deselect the Stadtbild under the marker in the current grid element.
                     * Thus it is added or removed from the Warenkorb.
                     *
                     * @param  e  DOCUMENT ME!
                     */
                    @Override
                    public void mouseClicked(final MouseEvent e) {
                        if (e.getClickCount() >= 2) {
                            final List<Sb_stadtbildserieGridObject> selectedSerien = PictureSelectionJGrid.this
                                        .getSelectedValuesList();
                            if (selectedSerien.size() == 1) {
                                final Sb_stadtbildserieGridObject gridObject = selectedSerien.get(0);
                                gridObject.selectOrDeselectStadtbild(gridObject.getStadtbildUnderMarker());
                            }
                        }
                    }
                });

            /*
             * Mouse listener that ensures that the vorschaubild is shown if mouse leaves statbildserie cell
             * Unfortunately it is not possible to attach Mouse listner directly to the grid cell elements
             */
            this.addMouseMotionListener(new MouseAdapter() {

                    int hoveredSerieIndex = -1;

                    @Override
                    public void mouseMoved(final MouseEvent e) {
                        final int index = PictureSelectionJGrid.this.getCellAt(e.getPoint());

                        if (index >= 0) {
                            hoveredSerieIndex = index;
                        } else {
                            if (hoveredSerieIndex >= 0) {
                                final Object o = PictureSelectionJGrid.this.getModel().getElementAt(hoveredSerieIndex);
                                if (o instanceof Sb_stadtbildserieGridObject) {
                                    final Sb_stadtbildserieGridObject hoveredSerie = (Sb_stadtbildserieGridObject)o;
                                    // fraction of 0 means we want to show the first image which is the vorschau bild
                                    hoveredSerie.setFraction(0);
                                }
                            }
                            hoveredSerieIndex = -1;
                        }
                    }
                });

            this.addMouseMotionListener(new MouseAdapter() {

                    int lastIndex = -1;

                    /**
                     * Draw the marker.
                     *
                     * @param  e  DOCUMENT ME!
                     */
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

                    /**
                     * Show the selected grid object in the info panel and reload the image if it was previously loaded
                     * with an error.
                     *
                     * @param  e  DOCUMENT ME!
                     */
                    @Override
                    public void valueChanged(final ListSelectionEvent e) {
                        if (!e.getValueIsAdjusting()) {
                            updateInfoPanel();
                            reloadIfImageWithError();
                        }
                    }

                    private void reloadIfImageWithError() {
                        final List selectedObject = PictureSelectionJGrid.this.getSelectedValuesList();
                        if (selectedObject.size() == 1) {
                            final Sb_stadtbildserieGridObject gridObject = (Sb_stadtbildserieGridObject)
                                selectedObject.get(0);
                            final StadtbilderUtils.StadtbildInfo stadtbildInfo = gridObject.getStadtbildInfo();
                            if (StadtbilderUtils.isBildnummerInFailedSet(stadtbildInfo)) {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug(
                                        "The image "
                                                + stadtbildInfo.getBildnummer()
                                                + " could not be loaded the last time because:\n"
                                                + StadtbilderUtils.getErrorMessageForFailedImage(stadtbildInfo));
                                }
                                StadtbilderUtils.removeBildnummerFromFailedSet(stadtbildInfo);
                                gridObject.clearLastShownImage();
                            }
                        }
                    }
                });
        }

        /**
         * If only one grid object is selected, show it in the info panel.
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

    /**
     * A Java Bean which is used to create the report for ordering the stadtbilder from the Warenkorb.
     *
     * @version  $Revision$, $Date$
     */
    public static class StadtbildReportBean {

        //~ Instance fields ----------------------------------------------------

        CidsBean stadtbildserie;
        CidsBean stadtbild;
        Image image;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new StadtbildReportBean object.
         *
         * @param  stadtbildserie  DOCUMENT ME!
         * @param  stadtbild       DOCUMENT ME!
         * @param  image           DOCUMENT ME!
         */
        public StadtbildReportBean(final CidsBean stadtbildserie, final CidsBean stadtbild, final Image image) {
            this.stadtbildserie = stadtbildserie;
            this.stadtbild = stadtbild;
            this.image = image;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public CidsBean getStadtbildserie() {
            return stadtbildserie;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  stadtbildserie  DOCUMENT ME!
         */
        public void setStadtbildserie(final CidsBean stadtbildserie) {
            this.stadtbildserie = stadtbildserie;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public CidsBean getStadtbild() {
            return stadtbild;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  stadtbild  DOCUMENT ME!
         */
        public void setStadtbild(final CidsBean stadtbild) {
            this.stadtbild = stadtbild;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Image getImage() {
            return image;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  image  DOCUMENT ME!
         */
        public void setImage(final Image image) {
            this.image = image;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public String getStrasse() {
            return (String)stadtbildserie.getProperty("strasse.name");
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public String getHausnummer() {
            return (String)stadtbildserie.getProperty("hausnummer");
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public String getAnzahlBilder() {
            return Integer.toString(stadtbildserie.getBeanCollectionProperty("stadtbilder_arr").size());
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public String getAnzahlBilderWeitere() {
            final int anzBilder = stadtbildserie.getBeanCollectionProperty("stadtbilder_arr").size();
            final int anzBilderWeitere = (anzBilder > 0) ? (anzBilder - 1) : 0;
            return Integer.toString(anzBilderWeitere);
        }
    }
}
