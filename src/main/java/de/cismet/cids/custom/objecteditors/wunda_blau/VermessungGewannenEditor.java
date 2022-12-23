/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objecteditors.wunda_blau;

import Sirius.navigator.ui.RequestsFullSizeComponent;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXBusyLabel;

import org.openide.util.NbBundle;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.net.URL;

import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import de.cismet.cids.custom.objectrenderer.utils.VermessungPictureFinderClientUtils;
import de.cismet.cids.custom.objectrenderer.utils.alkis.ClientAlkisConf;
import de.cismet.cids.custom.objectrenderer.utils.billing.BillingPopup;
import de.cismet.cids.custom.utils.alkis.VermessungPictureFinder;
import de.cismet.cids.custom.utils.billing.BillingProductGroupAmount;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.DisposableCidsBeanStore;

import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultCustomObjectEditor;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.cismap.commons.gui.RasterfariDocumentLoaderPanel;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.CismetThreadPool;

import de.cismet.tools.gui.BorderProvider;
import de.cismet.tools.gui.FooterComponentProvider;
import de.cismet.tools.gui.TitleComponentProvider;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;
import de.cismet.tools.gui.downloadmanager.HttpDownload;
import de.cismet.tools.gui.panels.AlertPanel;
import de.cismet.tools.gui.panels.LayeredAlertPanel;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class VermessungGewannenEditor extends javax.swing.JPanel implements DisposableCidsBeanStore,
    TitleComponentProvider,
    FooterComponentProvider,
    BorderProvider,
    RequestsFullSizeComponent,
    ConnectionContextStore,
    RasterfariDocumentLoaderPanel.Listener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(VermessungGewannenEditor.class);
    protected static final Map<Integer, Color> COLORS_GEOMETRIE_STATUS = new HashMap<Integer, Color>();
    private static final ListModel MODEL_LOAD = new DefaultListModel() {

            {
                add(0, "Wird geladen...");
            }
        };

    static {
        COLORS_GEOMETRIE_STATUS.put(new Integer(1), Color.green);
        COLORS_GEOMETRIE_STATUS.put(new Integer(2), Color.yellow);
        COLORS_GEOMETRIE_STATUS.put(new Integer(3), Color.yellow);
        COLORS_GEOMETRIE_STATUS.put(new Integer(4), Color.red);
        COLORS_GEOMETRIE_STATUS.put(new Integer(5), Color.red);
        COLORS_GEOMETRIE_STATUS.put(new Integer(6), Color.green);
    }

    //~ Instance fields --------------------------------------------------------

    protected CidsBean cidsBean;
    protected boolean readOnly;
    protected String document;
    private AlertPanel alertPanel;

    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgrControls;
    private javax.swing.JButton btnHome;
    private javax.swing.JButton btnOpen;
    private javax.swing.JComboBox cmbGemarkung;
    private javax.swing.JComboBox cmbGeometrie;
    private javax.swing.JComboBox cmbGeometrieStatus;
    private javax.swing.Box.Filler gluGapControls;
    private javax.swing.Box.Filler gluGeneralInformationGap;
    private org.jdesktop.swingx.JXBusyLabel jxLBusyMeasure;
    private javax.swing.JLabel lblFlur;
    private javax.swing.JLabel lblGemarkung;
    private javax.swing.JLabel lblGeneralInformation;
    private javax.swing.JLabel lblGeometrie;
    private javax.swing.JLabel lblGeometrieStatus;
    private javax.swing.JLabel lblHeaderControls;
    private javax.swing.JLabel lblHeaderDocument;
    private javax.swing.JLabel lblHeaderPages;
    private javax.swing.JLabel lblReducedSize;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JList lstPages;
    private de.cismet.tools.gui.panels.LayeredAlertPanel measureComponentPanel;
    private javax.swing.JPanel panLeft;
    private javax.swing.JPanel panRight;
    private javax.swing.JPanel pnlBusy;
    private de.cismet.tools.gui.RoundedPanel pnlControls;
    private de.cismet.tools.gui.RoundedPanel pnlDocument;
    private de.cismet.tools.gui.RoundedPanel pnlGeneralInformation;
    private javax.swing.JPanel pnlGrenzniederschriftAlert;
    private de.cismet.tools.gui.SemiRoundedPanel pnlHeaderControls;
    private de.cismet.tools.gui.SemiRoundedPanel pnlHeaderDocument;
    private de.cismet.tools.gui.SemiRoundedPanel pnlHeaderGeneralInformation;
    private de.cismet.tools.gui.SemiRoundedPanel pnlHeaderPages;
    private javax.swing.JPanel pnlMeasureComp;
    private javax.swing.JPanel pnlMeasureComponentWrapper;
    private de.cismet.tools.gui.RoundedPanel pnlPages;
    private javax.swing.JPanel pnlTitle;
    private javax.swing.JPanel pnlUmleitungHeader;
    private de.cismet.cismap.commons.gui.RasterfariDocumentLoaderPanel rasterfariDocumentLoaderPanel1;
    private javax.swing.JScrollPane scpPages;
    private javax.swing.Box.Filler strFooter;
    private javax.swing.JToggleButton togPan;
    private javax.swing.JToggleButton togZoom;
    private javax.swing.JTextField txtKmquadrat;
    private javax.swing.JLabel warnMessage;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VermessungGewannenEditor object.
     */
    public VermessungGewannenEditor() {
        this(false);
    }

    /**
     * Creates new form VermessungGewannenEditor.
     *
     * @param  readOnly  DOCUMENT ME!
     */
    public VermessungGewannenEditor(final boolean readOnly) {
        this.readOnly = readOnly;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;

        document = null;
        initComponents();
        alertPanel = new AlertPanel(AlertPanel.TYPE.DANGER, warnMessage, true);
        initAlertPanel();
        lblReducedSize.setVisible(false);

        if (readOnly) {
            pnlGeneralInformation.setVisible(false);
            /*lblGemarkung.setVisible(false);
             * lblFlur.setVisible(false); lblBlatt.setVisible(false); lblGeometrie.setVisible(false);
             * cmbGemarkung.setVisible(false); txtBlatt.setVisible(false);txtFlur.setVisible(false);*/

            cmbGeometrieStatus.setEditable(false);
            cmbGeometrieStatus.setEnabled(false);
            txtKmquadrat.setEditable(false);
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
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        strFooter = new javax.swing.Box.Filler(new java.awt.Dimension(0, 22),
                new java.awt.Dimension(0, 22),
                new java.awt.Dimension(32767, 22));
        pnlTitle = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        bgrControls = new javax.swing.ButtonGroup();
        pnlMeasureComponentWrapper = new javax.swing.JPanel();
        pnlBusy = new javax.swing.JPanel();
        jxLBusyMeasure = new JXBusyLabel(new Dimension(64, 64));
        pnlMeasureComp = new javax.swing.JPanel();
        rasterfariDocumentLoaderPanel1 = new RasterfariDocumentLoaderPanel(
                ClientAlkisConf.getInstance().getRasterfariUrl(),
                this,
                connectionContext);
        pnlGrenzniederschriftAlert = new javax.swing.JPanel();
        warnMessage = new javax.swing.JLabel();
        panLeft = new javax.swing.JPanel();
        pnlDocument = new de.cismet.tools.gui.RoundedPanel();
        pnlHeaderDocument = new de.cismet.tools.gui.SemiRoundedPanel();
        pnlUmleitungHeader = new javax.swing.JPanel();
        lblHeaderDocument = new javax.swing.JLabel();
        lblReducedSize = new javax.swing.JLabel();
        measureComponentPanel = new LayeredAlertPanel(pnlMeasureComponentWrapper, pnlGrenzniederschriftAlert);
        pnlGeneralInformation = new de.cismet.tools.gui.RoundedPanel();
        lblGeometrieStatus = new javax.swing.JLabel();
        cmbGeometrieStatus = new DefaultBindableReferenceCombo();
        pnlHeaderGeneralInformation = new de.cismet.tools.gui.SemiRoundedPanel();
        lblGeneralInformation = new javax.swing.JLabel();
        lblGeometrie = new javax.swing.JLabel();
        if (!readOnly) {
            cmbGeometrie = new DefaultCismapGeometryComboBoxEditor();
        }
        gluGeneralInformationGap = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        lblGemarkung = new javax.swing.JLabel();
        cmbGemarkung = new DefaultBindableReferenceCombo();
        lblFlur = new javax.swing.JLabel();
        txtKmquadrat = new javax.swing.JTextField();
        panRight = new javax.swing.JPanel();
        pnlControls = new de.cismet.tools.gui.RoundedPanel();
        togPan = rasterfariDocumentLoaderPanel1.getTogPan();
        togZoom = rasterfariDocumentLoaderPanel1.getTogZoom();
        btnHome = rasterfariDocumentLoaderPanel1.getBtnHome();
        pnlHeaderControls = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeaderControls = new javax.swing.JLabel();
        btnOpen = new javax.swing.JButton();
        pnlPages = new de.cismet.tools.gui.RoundedPanel();
        pnlHeaderPages = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeaderPages = new javax.swing.JLabel();
        scpPages = new javax.swing.JScrollPane();
        lstPages = rasterfariDocumentLoaderPanel1.getLstPages();
        gluGapControls = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));

        pnlTitle.setOpaque(false);
        pnlTitle.setLayout(new java.awt.GridBagLayout());

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblTitle.setForeground(java.awt.Color.white);
        lblTitle.setText(org.openide.util.NbBundle.getMessage(
                VermessungGewannenEditor.class,
                "VermessungGewannenEditor.lblTitle.text"));   // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlTitle.add(lblTitle, gridBagConstraints);

        pnlMeasureComponentWrapper.setLayout(new java.awt.CardLayout());

        pnlBusy.setBackground(new java.awt.Color(254, 254, 254));
        pnlBusy.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlBusy.setLayout(new java.awt.GridBagLayout());

        jxLBusyMeasure.setPreferredSize(new java.awt.Dimension(64, 64));
        pnlBusy.add(jxLBusyMeasure, new java.awt.GridBagConstraints());

        pnlMeasureComponentWrapper.add(pnlBusy, "busyCard");

        pnlMeasureComp.setLayout(new java.awt.BorderLayout());
        pnlMeasureComp.add(rasterfariDocumentLoaderPanel1, java.awt.BorderLayout.CENTER);

        pnlMeasureComponentWrapper.add(pnlMeasureComp, "measureCard");

        pnlGrenzniederschriftAlert.setBackground(new java.awt.Color(254, 254, 254));
        pnlGrenzniederschriftAlert.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pnlGrenzniederschriftAlert.setLayout(new java.awt.BorderLayout());

        warnMessage.setText(org.openide.util.NbBundle.getMessage(
                VermessungGewannenEditor.class,
                "VermessungGewannenEditor.warnMessage.text")); // NOI18N

        setLayout(new java.awt.GridBagLayout());

        panLeft.setOpaque(false);
        panLeft.setLayout(new java.awt.GridBagLayout());

        pnlHeaderDocument.setBackground(java.awt.Color.darkGray);
        pnlHeaderDocument.setLayout(new java.awt.GridBagLayout());

        pnlUmleitungHeader.setOpaque(false);
        pnlUmleitungHeader.setLayout(new java.awt.GridBagLayout());

        lblHeaderDocument.setForeground(java.awt.Color.white);
        lblHeaderDocument.setText(org.openide.util.NbBundle.getMessage(
                VermessungGewannenEditor.class,
                "VermessungGewannenEditor.lblHeaderDocument.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlUmleitungHeader.add(lblHeaderDocument, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        pnlHeaderDocument.add(pnlUmleitungHeader, gridBagConstraints);

        lblReducedSize.setForeground(new java.awt.Color(254, 254, 254));
        lblReducedSize.setText(org.openide.util.NbBundle.getMessage(
                VermessungGewannenEditor.class,
                "VermessungGewannenEditor.lblReducedSize.text_1")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        pnlHeaderDocument.add(lblReducedSize, gridBagConstraints);

        pnlDocument.add(pnlHeaderDocument, java.awt.BorderLayout.NORTH);

        measureComponentPanel.setPreferredSize(new java.awt.Dimension(200, 200));
        pnlDocument.add(measureComponentPanel, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panLeft.add(pnlDocument, gridBagConstraints);

        pnlGeneralInformation.setLayout(new java.awt.GridBagLayout());

        lblGeometrieStatus.setText(org.openide.util.NbBundle.getMessage(
                VermessungGewannenEditor.class,
                "VermessungGewannenEditor.lblGeometrieStatus.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlGeneralInformation.add(lblGeometrieStatus, gridBagConstraints);

        cmbGeometrieStatus.setRenderer(new GeometrieStatusRenderer(cmbGeometrieStatus.getRenderer()));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.geometrie_status}"),
                cmbGeometrieStatus,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        cmbGeometrieStatus.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmbGeometrieStatusActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 7);
        pnlGeneralInformation.add(cmbGeometrieStatus, gridBagConstraints);

        pnlHeaderGeneralInformation.setBackground(new java.awt.Color(51, 51, 51));
        pnlHeaderGeneralInformation.setLayout(new java.awt.FlowLayout());

        lblGeneralInformation.setForeground(new java.awt.Color(255, 255, 255));
        lblGeneralInformation.setText(org.openide.util.NbBundle.getMessage(
                VermessungGewannenEditor.class,
                "VermessungGewannenEditor.lblGeneralInformation.text")); // NOI18N
        pnlHeaderGeneralInformation.add(lblGeneralInformation);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        pnlGeneralInformation.add(pnlHeaderGeneralInformation, gridBagConstraints);

        lblGeometrie.setText(org.openide.util.NbBundle.getMessage(
                VermessungGewannenEditor.class,
                "VermessungGewannenEditor.lblGeometrie.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlGeneralInformation.add(lblGeometrie, gridBagConstraints);

        if (!readOnly) {
            binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                    org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                    this,
                    org.jdesktop.beansbinding.ELProperty.create("${cidsBean.geometrie}"),
                    cmbGeometrie,
                    org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cmbGeometrie).getConverter());
            bindingGroup.addBinding(binding);
        }
        if (!readOnly) {
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 3;
            gridBagConstraints.gridy = 3;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.weightx = 0.5;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
            pnlGeneralInformation.add(cmbGeometrie, gridBagConstraints);
        }
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.1;
        pnlGeneralInformation.add(gluGeneralInformationGap, gridBagConstraints);

        lblGemarkung.setLabelFor(cmbGemarkung);
        lblGemarkung.setText(org.openide.util.NbBundle.getMessage(
                VermessungGewannenEditor.class,
                "VermessungGewannenEditor.lblGemarkung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlGeneralInformation.add(lblGemarkung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gemarkung}"),
                cmbGemarkung,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        pnlGeneralInformation.add(cmbGemarkung, gridBagConstraints);

        lblFlur.setText(org.openide.util.NbBundle.getMessage(
                VermessungGewannenEditor.class,
                "VermessungGewannenEditor.lblFlur.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlGeneralInformation.add(lblFlur, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.flur}"),
                txtKmquadrat,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 7);
        pnlGeneralInformation.add(txtKmquadrat, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.75;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 15, 5);
        panLeft.add(pnlGeneralInformation, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(panLeft, gridBagConstraints);

        panRight.setOpaque(false);
        panRight.setLayout(new java.awt.GridBagLayout());

        pnlControls.setLayout(new java.awt.GridBagLayout());

        bgrControls.add(togPan);
        togPan.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/pan.gif"))); // NOI18N
        togPan.setSelected(true);
        togPan.setText(org.openide.util.NbBundle.getMessage(
                VermessungGewannenEditor.class,
                "VermessungGewannenEditor.togPan.text"));                                  // NOI18N
        togPan.setToolTipText(org.openide.util.NbBundle.getMessage(
                VermessungGewannenEditor.class,
                "VermessungGewannenEditor.togPan.toolTipText"));                           // NOI18N
        togPan.setFocusPainted(false);
        togPan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        togPan.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    togPanActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 3, 10);
        pnlControls.add(togPan, gridBagConstraints);

        bgrControls.add(togZoom);
        togZoom.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/zoom.gif"))); // NOI18N
        togZoom.setText(org.openide.util.NbBundle.getMessage(
                VermessungGewannenEditor.class,
                "VermessungGewannenEditor.togZoom.text"));                                  // NOI18N
        togZoom.setToolTipText(org.openide.util.NbBundle.getMessage(
                VermessungGewannenEditor.class,
                "VermessungGewannenEditor.togZoom.toolTipText"));                           // NOI18N
        togZoom.setFocusPainted(false);
        togZoom.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        togZoom.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    togZoomActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 3, 10);
        pnlControls.add(togZoom, gridBagConstraints);

        btnHome.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/home.gif"))); // NOI18N
        btnHome.setText(org.openide.util.NbBundle.getMessage(
                VermessungGewannenEditor.class,
                "VermessungGewannenEditor.btnHome.text"));                                  // NOI18N
        btnHome.setToolTipText(org.openide.util.NbBundle.getMessage(
                VermessungGewannenEditor.class,
                "VermessungGewannenEditor.btnHome.toolTipText"));                           // NOI18N
        btnHome.setFocusPainted(false);
        btnHome.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnHome.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnHomeActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 3, 10);
        pnlControls.add(btnHome, gridBagConstraints);

        pnlHeaderControls.setBackground(new java.awt.Color(51, 51, 51));
        pnlHeaderControls.setLayout(new java.awt.FlowLayout());

        lblHeaderControls.setForeground(new java.awt.Color(255, 255, 255));
        lblHeaderControls.setText(org.openide.util.NbBundle.getMessage(
                VermessungGewannenEditor.class,
                "VermessungGewannenEditor.lblHeaderControls.text")); // NOI18N
        pnlHeaderControls.add(lblHeaderControls);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        pnlControls.add(pnlHeaderControls, gridBagConstraints);

        btnOpen.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/folder-image.png"))); // NOI18N
        btnOpen.setText(org.openide.util.NbBundle.getMessage(
                VermessungGewannenEditor.class,
                "VermessungGewannenEditor.btnOpen.text"));                                          // NOI18N
        btnOpen.setToolTipText(org.openide.util.NbBundle.getMessage(
                VermessungGewannenEditor.class,
                "VermessungGewannenEditor.btnOpen.toolTipText"));                                   // NOI18N
        btnOpen.setFocusPainted(false);
        btnOpen.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnOpen.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnOpenActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 8, 10);
        pnlControls.add(btnOpen, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        panRight.add(pnlControls, gridBagConstraints);

        pnlHeaderPages.setBackground(new java.awt.Color(51, 51, 51));
        pnlHeaderPages.setLayout(new java.awt.FlowLayout());

        lblHeaderPages.setForeground(new java.awt.Color(255, 255, 255));
        lblHeaderPages.setText(org.openide.util.NbBundle.getMessage(
                VermessungGewannenEditor.class,
                "VermessungGewannenEditor.lblHeaderPages.text")); // NOI18N
        pnlHeaderPages.add(lblHeaderPages);

        pnlPages.add(pnlHeaderPages, java.awt.BorderLayout.PAGE_START);

        scpPages.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scpPages.setMinimumSize(new java.awt.Dimension(31, 75));
        scpPages.setOpaque(false);
        scpPages.setPreferredSize(new java.awt.Dimension(85, 75));

        lstPages.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstPages.setFixedCellWidth(75);
        scpPages.setViewportView(lstPages);

        pnlPages.add(scpPages, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        panRight.add(pnlPages, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panRight.add(gluGapControls, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(panRight, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void togPanActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_togPanActionPerformed
        rasterfariDocumentLoaderPanel1.actionPan();
    }                                                                          //GEN-LAST:event_togPanActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void togZoomActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_togZoomActionPerformed
        rasterfariDocumentLoaderPanel1.actionZoom();
    }                                                                           //GEN-LAST:event_togZoomActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnHomeActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnHomeActionPerformed
        rasterfariDocumentLoaderPanel1.actionOverview();
    }                                                                           //GEN-LAST:event_btnHomeActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnOpenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnOpenActionPerformed
        try {
            final String priceGroup = "ea";
            final URL documentUrl;
            if (document.contains(VermessungPictureFinder.SUFFIX_REDUCED_SIZE + ".")) {
                documentUrl = rasterfariDocumentLoaderPanel1.getDocumentUrl(document.replaceAll(
                            VermessungPictureFinder.SUFFIX_REDUCED_SIZE,
                            ""));
            } else {
                documentUrl = rasterfariDocumentLoaderPanel1.getDocumentUrl();
            }

            if (BillingPopup.doBilling(
                            "fsuekom",
                            documentUrl.toExternalForm(),
                            (Geometry)null,
                            getConnectionContext(),
                            new BillingProductGroupAmount(priceGroup, 1))) {
                downloadProduct(documentUrl);
            }
        } catch (Exception e) {
            LOG.error("Error when trying to produce a alkis product", e);
            // Hier noch ein Fehlerdialog
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  url  DOCUMENT ME!
     */
    private void downloadProduct(final URL url) {
        CismetThreadPool.execute(new Runnable() {

                @Override
                public void run() {
                    if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(
                                    VermessungGewannenEditor.this)) {
                        final String urlString = url.toExternalForm();
                        final String filename = urlString.substring(urlString.lastIndexOf("/") + 1);

                        DownloadManager.instance()
                                .add(
                                    new HttpDownload(
                                        url,
                                        "",
                                        DownloadManagerDialog.getInstance().getJobName(),
                                        "Gewanne",
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
    private void cmbGeometrieStatusActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmbGeometrieStatusActionPerformed
        if (cmbGeometrieStatus.getSelectedItem() instanceof CidsBean) {
            final CidsBean geometrieStatus = (CidsBean)cmbGeometrieStatus.getSelectedItem();

            if (geometrieStatus.getProperty("id") instanceof Integer) {
                cmbGeometrieStatus.setBackground(COLORS_GEOMETRIE_STATUS.get(
                        (Integer)geometrieStatus.getProperty("id")));
            }
        }
    } //GEN-LAST:event_cmbGeometrieStatusActionPerformed

    /**
     * DOCUMENT ME!
     */
    private void initAlertPanel() {
        warnMessage.setForeground(AlertPanel.dangerMessageColor);
        alertPanel.setContent(warnMessage);
        alertPanel.repaint();
        alertPanel.setPreferredSize(new Dimension(500, 50));
        alertPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // To change body of generated methods,
        // choose Tools | Templates.
        pnlGrenzniederschriftAlert.add(alertPanel, BorderLayout.CENTER);
        pnlGrenzniederschriftAlert.setBackground(new Color(1f, 1f, 1f, 0.8f));
        alertPanel.setVisible(false);
        alertPanel.addCloseButtonActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                rasterfariDocumentLoaderPanel1.reset();
                                showAlert(true);
                                pnlMeasureComponentWrapper.invalidate();
                                pnlMeasureComponentWrapper.revalidate();
                                pnlMeasureComponentWrapper.repaint();
                            }
                        });
                }
            });
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    public void showMeasureIsLoading() {
        jxLBusyMeasure.setBusy(true);
        final CardLayout cl = (CardLayout)pnlMeasureComponentWrapper.getLayout();
        cl.show(pnlMeasureComponentWrapper, "busyCard");
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    public void showMeasurePanel() {
        jxLBusyMeasure.setBusy(false);
        final CardLayout cl = (CardLayout)pnlMeasureComponentWrapper.getLayout();
        cl.show(pnlMeasureComponentWrapper, "measureCard");
    }

    /**
     * DOCUMENT ME!
     *
     * @param  show  DOCUMENT ME!
     */
    private void showAlert(final boolean show) {
        // this means it is editable
        if (!readOnly) {
            alertPanel.setType(AlertPanel.TYPE.DANGER);
            alertPanel.setContent(warnMessage);
            alertPanel.setVisible(show);
            alertPanel.repaint();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getDocumentFilename() {
        final Boolean liste = (Boolean)cidsBean.getProperty("liste");
        final Integer gemarkung = (Integer)cidsBean.getProperty("gemarkung.id");
        final Integer kmquadrat = (Integer)cidsBean.getProperty("kmquadrat");
        return VermessungPictureFinderClientUtils.getInstance()
                    .getGewannePictureFilename(
                        liste ? gemarkung : kmquadrat,
                        liste);
    }

    /**
     * DOCUMENT ME!
     */
    private void checkLinkInTitle() {
        checkLinkInTitle(document);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  document  url DOCUMENT ME!
     */
    private void checkLinkInTitle(final String document) {
        boolean isUmleitung = false;
        lblReducedSize.setVisible(false);
        if (document != null) {
            if (document.contains(VermessungPictureFinder.SUFFIX_REDUCED_SIZE + ".")) {
                lblReducedSize.setVisible(true);
            }
            final String filename = getDocumentFilename();

            if (!document.contains(filename)) {
                isUmleitung = true;
                pnlHeaderDocument.repaint();
            }
        }

        if (!readOnly && isUmleitung) {
            lblHeaderDocument.setText(NbBundle.getMessage(
                    VermessungGewannenEditor.class,
                    "VermessungGewannenEditor.lblHeaderDocument.text.gewanne_umleitung"));
        } else {
            lblHeaderDocument.setText(NbBundle.getMessage(
                    VermessungGewannenEditor.class,
                    "VermessungGewannenEditor.lblHeaderDocument.text.gewanne"));
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void successAlert() {
        rasterfariDocumentLoaderPanel1.setCurrentPageNull();
        alertPanel.setType(AlertPanel.TYPE.SUCCESS);
        pnlMeasureComponentWrapper.invalidate();
        pnlMeasureComponentWrapper.validate();
        pnlMeasureComponentWrapper.repaint();
    }

    /**
     * DOCUMENT ME!
     */
    public void handleNoDocumentFound() {
        alertPanel.setType(AlertPanel.TYPE.DANGER);
        rasterfariDocumentLoaderPanel1.removeAllFeatures();
        this.invalidate();
        this.validate();
        this.repaint();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        bindingGroup.unbind();

        if (cidsBean != null) {
            this.cidsBean = cidsBean;

            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                this.cidsBean,
                getConnectionContext());
            bindingGroup.bind();

            lblTitle.setText(generateTitle());

            if ((cidsBean.getProperty("geometrie_status") instanceof CidsBean)
                        && (cidsBean.getProperty("geometrie_status.id") instanceof Integer)) {
                cmbGeometrieStatus.setBackground(COLORS_GEOMETRIE_STATUS.get(
                        (Integer)cidsBean.getProperty("geometrie_status.id")));
            }
        }

        setCurrentDocumentNull();

        new RefreshDocumentWorker().execute();
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    public void dispose() {
        bindingGroup.unbind();
        // dispose panels here if necessary
        rasterfariDocumentLoaderPanel1.dispose();
        if (!readOnly) {
            ((DefaultCismapGeometryComboBoxEditor)cmbGeometrie).dispose();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public JComponent getTitleComponent() {
        return pnlTitle;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public JComponent getFooterComponent() {
        return strFooter;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Border getTitleBorder() {
        return new EmptyBorder(10, 10, 10, 10);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Border getFooterBorder() {
        return new EmptyBorder(5, 5, 5, 5);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Border getCenterrBorder() {
        return new EmptyBorder(0, 5, 0, 5);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected String generateTitle() {
        final Integer kmquadrat = (Integer)cidsBean.getProperty("kmquadrat");
        final String gemarkung = (String)cidsBean.getProperty("gemarkung.name");
        final Boolean liste = (Boolean)cidsBean.getProperty("liste");

        final StringBuilder sb = new StringBuilder();
        if (Boolean.TRUE.equals(liste)) {
            sb.append("Gemarkung ").append((gemarkung != null) ? gemarkung : "unbekannt");
        } else {
            sb.append("ḱm-Quadrat ").append((kmquadrat != null) ? Integer.toString(kmquadrat) : "unbekannt");
        }
        return sb.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected Integer getGemarkungOfCurrentCidsBean() {
        Integer result = Integer.valueOf(-1);

        if (cidsBean != null) {
            if (cidsBean.getProperty("gemarkung") != null) {
                final Object gemarkung = cidsBean.getProperty("gemarkung.id");
                if (gemarkung instanceof Integer) {
                    result = (Integer)gemarkung;
                }
            }
        }

        return result;
    }

    /**
     * DOCUMENT ME!
     */
    protected void loadDokument() {
        showMeasureIsLoading();
        checkLinkInTitle();
        showAlert(false);
        rasterfariDocumentLoaderPanel1.setDocument(document);
    }

    /**
     * DOCUMENT ME!
     */
    protected void setCurrentDocumentNull() {
        rasterfariDocumentLoaderPanel1.setCurrentPageNull();
    }

    /**
     * DOCUMENT ME!
     */
    public void warnAlert() {
        rasterfariDocumentLoaderPanel1.setCurrentPageNull();
        alertPanel.setType(AlertPanel.TYPE.WARNING);
        pnlMeasureComponentWrapper.invalidate();
        pnlMeasureComponentWrapper.validate();
        pnlMeasureComponentWrapper.repaint();
    }

    /**
     * DOCUMENT ME!
     */
    public void handleRissDoesNotExists() {
        rasterfariDocumentLoaderPanel1.setCurrentPageNull();
        alertPanel.setType(AlertPanel.TYPE.DANGER);
        pnlMeasureComponentWrapper.invalidate();
        pnlMeasureComponentWrapper.validate();
        pnlMeasureComponentWrapper.repaint();
    }

    @Override
    public final ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param    busy  DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    protected final class RefreshDocumentWorker extends SwingWorker<String, Object> {

        //~ Instance fields ----------------------------------------------------

        boolean refreshMeasuringComponent;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new RefreshDocumentWorker object.
         */
        public RefreshDocumentWorker() {
            this(true);
        }

        /**
         * Creates a new RefreshDocumentWorker object.
         *
         * @param  refreshMeasuringComponent  DOCUMENT ME!
         */
        public RefreshDocumentWorker(final boolean refreshMeasuringComponent) {
            this.refreshMeasuringComponent = refreshMeasuringComponent;
            if (this.refreshMeasuringComponent) {
                lstPages.setModel(MODEL_LOAD);
//                setCurrentDocumentNull();

                showMeasureIsLoading();
            }
        }

        //~ Methods ------------------------------------------------------------

        /**
         * Tries to find a working URL for the Bild (image) and Grenzniederschrift (boundary notes) and saves them to
         * the array documentURLs. This is done by doing a request to several possible URLs.
         *
         * @return  DOCUMENT ME!
         *
         * @throws  Exception  DOCUMENT ME!
         */
        @Override
        protected String doInBackground() throws Exception {
            final Integer gemarkung = (Integer)cidsBean.getProperty("gemarkung.id");
            final Integer kmquadrat = (Integer)cidsBean.getProperty("kmquadrat");
            final Boolean liste = (Boolean)cidsBean.getProperty("liste");

            return VermessungPictureFinderClientUtils.getInstance()
                        .findGewannePicture(
                            liste ? gemarkung : kmquadrat,
                            liste);
        }

        /**
         * Depending on the values in the documentURLs the GUI gets configured and the actual documents get loaded.
         */
        @Override
        protected void done() {
            try {
                if (!isCancelled()) {
                    final String current = get();
//                    if (current != null) {
                    document = current;
//                    }
                }
            } catch (final InterruptedException ex) {
                LOG.warn("Was interrupted while refreshing document.", ex);
            } catch (final Exception ex) {
                LOG.warn("There was an exception while refreshing document.", ex);
            } finally {
                if (refreshMeasuringComponent) {
                    loadDokument();
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    protected class GeometrieStatusRenderer implements ListCellRenderer {

        //~ Instance fields ----------------------------------------------------

        private final ListCellRenderer originalRenderer;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new GeometrieStatusRenderer object.
         *
         * @param  originalRenderer  DOCUMENT ME!
         */
        public GeometrieStatusRenderer(final ListCellRenderer originalRenderer) {
            this.originalRenderer = originalRenderer;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param   list          DOCUMENT ME!
         * @param   value         DOCUMENT ME!
         * @param   index         DOCUMENT ME!
         * @param   isSelected    DOCUMENT ME!
         * @param   cellHasFocus  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        @Override
        public Component getListCellRendererComponent(final JList list,
                final Object value,
                final int index,
                final boolean isSelected,
                final boolean cellHasFocus) {
            final Component result = originalRenderer.getListCellRendererComponent(
                    list,
                    value,
                    index,
                    isSelected,
                    cellHasFocus);

            if (isSelected) {
                result.setBackground(list.getSelectionBackground());
                result.setForeground(list.getSelectionForeground());
            } else {
                result.setBackground(list.getBackground());
                result.setForeground(list.getForeground());

                if (value instanceof CidsBean) {
                    final CidsBean geometrieStatus = (CidsBean)value;
                    if (geometrieStatus.getProperty("id") instanceof Integer) {
                        result.setBackground(COLORS_GEOMETRIE_STATUS.get((Integer)geometrieStatus.getProperty("id")));
                    }
                }
            }

            return result;
        }
    }
}
