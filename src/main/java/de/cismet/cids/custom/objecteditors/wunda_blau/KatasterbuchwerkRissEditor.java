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
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXBusyLabel;

import org.openide.util.Exceptions;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JToggleButton;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.utils.PictureLoaderPanel;
import de.cismet.cids.custom.objecteditors.utils.VermessungUmleitungPanel;
import de.cismet.cids.custom.objectrenderer.utils.AlphanumComparator;
import de.cismet.cids.custom.objectrenderer.utils.VermessungsrissWebAccessPictureFinder;
import de.cismet.cids.custom.objectrenderer.utils.alkis.ClientAlkisConf;
import de.cismet.cids.custom.objectrenderer.utils.billing.BillingPopup;
import de.cismet.cids.custom.objectrenderer.utils.billing.ProductGroupAmount;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.DisposableCidsBeanStore;

import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.converters.SqlDateToStringConverter;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.cismap.commons.CrsTransformer;

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
public class KatasterbuchwerkRissEditor extends javax.swing.JPanel implements DisposableCidsBeanStore,
    TitleComponentProvider,
    FooterComponentProvider,
    BorderProvider,
    RequestsFullSizeComponent,
    ConnectionContextStore,
    PictureLoaderPanel.Listener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(KatasterbuchwerkRissEditor.class);
    protected static final int BUCHWERK = 0;
    protected static final int NO_SELECTION = -1;

    protected static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(
                PrecisionModel.FLOATING),
            CrsTransformer.extractSridFromCrs(ClientAlkisConf.getInstance().getSrsService()));
    protected static final Map<Integer, Color> COLORS_GEOMETRIE_STATUS = new HashMap<Integer, Color>();

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
    protected Object schluessel;
    protected Object gemarkung;
    protected Object flur;
    protected Object blatt;
    protected boolean readOnly;
    protected URL[] documentURLs;
    protected JToggleButton[] documentButtons;
    protected JToggleButton currentSelectedButton;
    protected volatile int currentDocument = NO_SELECTION;
    protected volatile int currentPage = NO_SELECTION;
    private AlertPanel alertPanel;
    private PictureLoaderPanel pictureLoaderPanel;

    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgrControls;
    private javax.swing.ButtonGroup bgrDocument;
    private javax.swing.JButton btnHome;
    private javax.swing.JButton btnOpen;
    private javax.swing.JComboBox cmbFormat;
    private javax.swing.JComboBox cmbGemarkung;
    private javax.swing.JComboBox cmbGeometrie;
    private javax.swing.JComboBox cmbGeometrieStatus;
    private javax.swing.JComboBox cmbSchluessel;
    private javax.swing.JFormattedTextField ftxFlur;
    private javax.swing.Box.Filler gluGapControls;
    private javax.swing.Box.Filler gluGeneralInformationGap;
    private javax.swing.JLabel grenzNiederschriftWarnMessage;
    private javax.swing.JLabel jLabel1;
    private org.jdesktop.swingx.JXBusyLabel jxLBusyMeasure;
    private javax.swing.JLabel lblBlatt;
    private javax.swing.JLabel lblFlur;
    private javax.swing.JLabel lblFormat;
    private javax.swing.JLabel lblGemarkung;
    private javax.swing.JLabel lblGeneralInformation;
    private javax.swing.JLabel lblGeometrie;
    private javax.swing.JLabel lblGeometrieStatus;
    private javax.swing.JLabel lblHeaderControls;
    private javax.swing.JLabel lblHeaderDocument;
    private javax.swing.JLabel lblHeaderDocuments;
    private javax.swing.JLabel lblHeaderPages;
    private javax.swing.JLabel lblJahr;
    private javax.swing.JLabel lblLetzteAenderungDatum;
    private javax.swing.JLabel lblLetzteAenderungName;
    private javax.swing.JLabel lblReducedSize;
    private javax.swing.JLabel lblSchluessel;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JList lstPages;
    private de.cismet.tools.gui.panels.LayeredAlertPanel measureComponentPanel;
    private de.cismet.cismap.commons.gui.measuring.MeasuringComponent measuringComponent;
    private javax.swing.JPanel panLeft;
    private javax.swing.JPanel panRight;
    private javax.swing.JPanel pnlBusy;
    private de.cismet.tools.gui.RoundedPanel pnlControls;
    private de.cismet.tools.gui.RoundedPanel pnlDocument;
    private de.cismet.tools.gui.RoundedPanel pnlDocuments;
    private de.cismet.tools.gui.RoundedPanel pnlGeneralInformation;
    private javax.swing.JPanel pnlGrenzniederschriftAlert;
    private de.cismet.tools.gui.SemiRoundedPanel pnlHeaderControls;
    private de.cismet.tools.gui.SemiRoundedPanel pnlHeaderDocument;
    private de.cismet.tools.gui.SemiRoundedPanel pnlHeaderDocuments;
    private de.cismet.tools.gui.SemiRoundedPanel pnlHeaderGeneralInformation;
    private de.cismet.tools.gui.SemiRoundedPanel pnlHeaderPages;
    private javax.swing.JPanel pnlMeasureComp;
    private javax.swing.JPanel pnlMeasureComponentWrapper;
    private de.cismet.tools.gui.RoundedPanel pnlPages;
    private javax.swing.JPanel pnlTitle;
    private javax.swing.JPanel pnlUmleitungHeader;
    private javax.swing.JLabel rissWarnMessage;
    private javax.swing.JScrollPane scpPages;
    private javax.swing.Box.Filler strFooter;
    private javax.swing.JToggleButton togBild;
    private javax.swing.JToggleButton togPan;
    private javax.swing.JToggleButton togZoom;
    private javax.swing.JTextField txtBlatt;
    private javax.swing.JTextField txtJahr;
    private javax.swing.JTextField txtLetzteaenderungDatum;
    private javax.swing.JTextField txtLetzteaenderungName;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VermessungRissEditor object.
     */
    public KatasterbuchwerkRissEditor() {
        this(false);
    }

    /**
     * Creates new form VermessungRissEditor.
     *
     * @param  readOnly  DOCUMENT ME!
     */
    public KatasterbuchwerkRissEditor(final boolean readOnly) {
        this.readOnly = readOnly;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        this.pictureLoaderPanel = new PictureLoaderPanel(this, connectionContext);

        documentURLs = new URL[1];
        documentButtons = new JToggleButton[documentURLs.length];
        initComponents();
        alertPanel = new AlertPanel(
                AlertPanel.TYPE.DANGER,
                grenzNiederschriftWarnMessage,
                true);
        documentButtons[BUCHWERK] = togBild;
        currentSelectedButton = togBild;
        initAlertPanel();
        lblReducedSize.setVisible(false);
        if (readOnly) {
            lblSchluessel.setVisible(false);
            cmbSchluessel.setVisible(false);
            lblGemarkung.setVisible(false);
            cmbGemarkung.setVisible(false);
            lblFlur.setVisible(false);
            ftxFlur.setVisible(false);
            lblBlatt.setVisible(false);
            txtBlatt.setVisible(false);
            txtJahr.setEditable(false);
            cmbFormat.setEditable(false);
            cmbFormat.setEnabled(false);
            cmbGeometrieStatus.setEditable(false);
            cmbGeometrieStatus.setEnabled(false);
            lblGeometrie.setVisible(false);
        } else {
            if (txtBlatt.getDocument() instanceof AbstractDocument) {
                ((AbstractDocument)txtBlatt.getDocument()).setDocumentFilter(new DocumentSizeFilter());
            }
            if (ftxFlur.getDocument() instanceof AbstractDocument) {
                ((AbstractDocument)ftxFlur.getDocument()).setDocumentFilter(new DocumentSizeFilter());
            }
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
        bgrDocument = new javax.swing.ButtonGroup();
        pnlMeasureComponentWrapper = new javax.swing.JPanel();
        pnlBusy = new javax.swing.JPanel();
        jxLBusyMeasure = new JXBusyLabel(new Dimension(64, 64));
        pnlMeasureComp = new javax.swing.JPanel();
        measuringComponent = pictureLoaderPanel.getMeasuringComponent();
        pnlGrenzniederschriftAlert = new javax.swing.JPanel();
        grenzNiederschriftWarnMessage = new javax.swing.JLabel();
        rissWarnMessage = new javax.swing.JLabel();
        panLeft = new javax.swing.JPanel();
        pnlDocument = new de.cismet.tools.gui.RoundedPanel();
        pnlHeaderDocument = new de.cismet.tools.gui.SemiRoundedPanel();
        lblReducedSize = new javax.swing.JLabel();
        pnlUmleitungHeader = new javax.swing.JPanel();
        lblHeaderDocument = new javax.swing.JLabel();
        measureComponentPanel = new LayeredAlertPanel(pnlMeasureComponentWrapper, pnlGrenzniederschriftAlert);
        pnlGeneralInformation = new de.cismet.tools.gui.RoundedPanel();
        pnlHeaderGeneralInformation = new de.cismet.tools.gui.SemiRoundedPanel();
        lblGeneralInformation = new javax.swing.JLabel();
        lblJahr = new javax.swing.JLabel();
        txtJahr = new javax.swing.JTextField();
        lblFormat = new javax.swing.JLabel();
        cmbFormat = new DefaultBindableReferenceCombo();
        lblLetzteAenderungName = new javax.swing.JLabel();
        txtLetzteaenderungName = new javax.swing.JTextField();
        lblLetzteAenderungDatum = new javax.swing.JLabel();
        txtLetzteaenderungDatum = new javax.swing.JTextField();
        lblGeometrie = new javax.swing.JLabel();
        if (!readOnly) {
            cmbGeometrie = new DefaultCismapGeometryComboBoxEditor();
        }
        gluGeneralInformationGap = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        lblGeometrieStatus = new javax.swing.JLabel();
        cmbGeometrieStatus = new DefaultBindableReferenceCombo();
        lblSchluessel = new javax.swing.JLabel();
        cmbSchluessel = new javax.swing.JComboBox();
        lblGemarkung = new javax.swing.JLabel();
        cmbGemarkung = new DefaultBindableReferenceCombo();
        lblFlur = new javax.swing.JLabel();
        lblBlatt = new javax.swing.JLabel();
        txtBlatt = new javax.swing.JTextField();
        ftxFlur = new javax.swing.JFormattedTextField();
        panRight = new javax.swing.JPanel();
        pnlControls = new de.cismet.tools.gui.RoundedPanel();
        togPan = new javax.swing.JToggleButton();
        togZoom = new javax.swing.JToggleButton();
        btnHome = new javax.swing.JButton();
        pnlHeaderControls = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeaderControls = new javax.swing.JLabel();
        btnOpen = new javax.swing.JButton();
        pnlDocuments = new de.cismet.tools.gui.RoundedPanel();
        pnlHeaderDocuments = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeaderDocuments = new javax.swing.JLabel();
        togBild = new javax.swing.JToggleButton();
        jLabel1 = new javax.swing.JLabel();
        pnlPages = new de.cismet.tools.gui.RoundedPanel();
        pnlHeaderPages = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeaderPages = new javax.swing.JLabel();
        scpPages = new javax.swing.JScrollPane();
        lstPages = new javax.swing.JList();
        gluGapControls = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));

        pnlTitle.setOpaque(false);
        pnlTitle.setLayout(new java.awt.GridBagLayout());

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblTitle.setForeground(java.awt.Color.white);
        lblTitle.setText(org.openide.util.NbBundle.getMessage(
                KatasterbuchwerkRissEditor.class,
                "KatasterbuchwerkRissEditor.lblTitle.text")); // NOI18N
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
        pnlMeasureComp.add(measuringComponent, java.awt.BorderLayout.CENTER);

        pnlMeasureComponentWrapper.add(pnlMeasureComp, "measureCard");

        pnlGrenzniederschriftAlert.setBackground(new java.awt.Color(254, 254, 254));
        pnlGrenzniederschriftAlert.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pnlGrenzniederschriftAlert.setLayout(new java.awt.BorderLayout());

        grenzNiederschriftWarnMessage.setText(org.openide.util.NbBundle.getMessage(
                KatasterbuchwerkRissEditor.class,
                "KatasterbuchwerkRissEditor.grenzNiederschriftWarnMessage.text")); // NOI18N

        rissWarnMessage.setText(org.openide.util.NbBundle.getMessage(
                KatasterbuchwerkRissEditor.class,
                "KatasterbuchwerkRissEditor.rissWarnMessage.text")); // NOI18N

        setLayout(new java.awt.GridBagLayout());

        panLeft.setOpaque(false);
        panLeft.setLayout(new java.awt.GridBagLayout());

        pnlHeaderDocument.setBackground(java.awt.Color.darkGray);
        pnlHeaderDocument.setLayout(new java.awt.GridBagLayout());

        lblReducedSize.setForeground(new java.awt.Color(254, 254, 254));
        lblReducedSize.setText(org.openide.util.NbBundle.getMessage(
                KatasterbuchwerkRissEditor.class,
                "KatasterbuchwerkRissEditor.lblReducedSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        pnlHeaderDocument.add(lblReducedSize, gridBagConstraints);

        pnlUmleitungHeader.setOpaque(false);
        pnlUmleitungHeader.setLayout(new java.awt.GridBagLayout());

        lblHeaderDocument.setForeground(java.awt.Color.white);
        lblHeaderDocument.setText(org.openide.util.NbBundle.getMessage(
                KatasterbuchwerkRissEditor.class,
                "KatasterbuchwerkRissEditor.lblHeaderDocument.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlUmleitungHeader.add(lblHeaderDocument, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        pnlHeaderDocument.add(pnlUmleitungHeader, gridBagConstraints);

        pnlDocument.add(pnlHeaderDocument, java.awt.BorderLayout.NORTH);

        measureComponentPanel.setPreferredSize(new java.awt.Dimension(200, 200));
        pnlDocument.add(measureComponentPanel, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 5);
        panLeft.add(pnlDocument, gridBagConstraints);

        pnlGeneralInformation.setLayout(new java.awt.GridBagLayout());

        pnlHeaderGeneralInformation.setBackground(new java.awt.Color(51, 51, 51));
        pnlHeaderGeneralInformation.setLayout(new java.awt.FlowLayout());

        lblGeneralInformation.setForeground(new java.awt.Color(255, 255, 255));
        lblGeneralInformation.setText(org.openide.util.NbBundle.getMessage(
                KatasterbuchwerkRissEditor.class,
                "KatasterbuchwerkRissEditor.lblGeneralInformation.text")); // NOI18N
        pnlHeaderGeneralInformation.add(lblGeneralInformation);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        pnlGeneralInformation.add(pnlHeaderGeneralInformation, gridBagConstraints);

        lblJahr.setText(org.openide.util.NbBundle.getMessage(
                KatasterbuchwerkRissEditor.class,
                "KatasterbuchwerkRissEditor.lblJahr.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlGeneralInformation.add(lblJahr, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.jahr}"),
                txtJahr,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 7);
        pnlGeneralInformation.add(txtJahr, gridBagConstraints);

        lblFormat.setText(org.openide.util.NbBundle.getMessage(
                KatasterbuchwerkRissEditor.class,
                "KatasterbuchwerkRissEditor.lblFormat.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlGeneralInformation.add(lblFormat, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.format}"),
                cmbFormat,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 7);
        pnlGeneralInformation.add(cmbFormat, gridBagConstraints);

        lblLetzteAenderungName.setText(org.openide.util.NbBundle.getMessage(
                KatasterbuchwerkRissEditor.class,
                "KatasterbuchwerkRissEditor.lblLetzteAenderungName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 7, 5, 5);
        pnlGeneralInformation.add(lblLetzteAenderungName, gridBagConstraints);

        txtLetzteaenderungName.setEditable(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.letzteaenderung_name}"),
                txtLetzteaenderungName,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        pnlGeneralInformation.add(txtLetzteaenderungName, gridBagConstraints);

        lblLetzteAenderungDatum.setText(org.openide.util.NbBundle.getMessage(
                KatasterbuchwerkRissEditor.class,
                "KatasterbuchwerkRissEditor.lblLetzteAenderungDatum.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 7, 5, 5);
        pnlGeneralInformation.add(lblLetzteAenderungDatum, gridBagConstraints);

        txtLetzteaenderungDatum.setEditable(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.letzteaenderung_datum}"),
                txtLetzteaenderungDatum,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new SqlDateToStringConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        pnlGeneralInformation.add(txtLetzteaenderungDatum, gridBagConstraints);

        lblGeometrie.setText(org.openide.util.NbBundle.getMessage(
                KatasterbuchwerkRissEditor.class,
                "KatasterbuchwerkRissEditor.lblGeometrie.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
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
            gridBagConstraints.gridy = 5;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.weightx = 0.5;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
            pnlGeneralInformation.add(cmbGeometrie, gridBagConstraints);
        }
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.1;
        pnlGeneralInformation.add(gluGeneralInformationGap, gridBagConstraints);

        lblGeometrieStatus.setText(org.openide.util.NbBundle.getMessage(
                KatasterbuchwerkRissEditor.class,
                "KatasterbuchwerkRissEditor.lblGeometrieStatus.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlGeneralInformation.add(lblGeometrieStatus, gridBagConstraints);

        cmbGeometrieStatus.setRenderer(new GeometrieStatusRenderer(cmbGeometrieStatus.getRenderer()));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
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
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        pnlGeneralInformation.add(cmbGeometrieStatus, gridBagConstraints);

        lblSchluessel.setLabelFor(cmbSchluessel);
        lblSchluessel.setText(org.openide.util.NbBundle.getMessage(
                KatasterbuchwerkRissEditor.class,
                "KatasterbuchwerkRissEditor.lblSchluessel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 5);
        pnlGeneralInformation.add(lblSchluessel, gridBagConstraints);

        cmbSchluessel.setModel(new javax.swing.DefaultComboBoxModel(
                new String[] { "501", "502", "503", "504", "505", "506", "507", "508", "600", "605" }));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.schluessel}"),
                cmbSchluessel,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        pnlGeneralInformation.add(cmbSchluessel, gridBagConstraints);

        lblGemarkung.setLabelFor(cmbGemarkung);
        lblGemarkung.setText(org.openide.util.NbBundle.getMessage(
                KatasterbuchwerkRissEditor.class,
                "KatasterbuchwerkRissEditor.lblGemarkung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        pnlGeneralInformation.add(lblGemarkung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gemarkung}"),
                cmbGemarkung,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 10);
        pnlGeneralInformation.add(cmbGemarkung, gridBagConstraints);

        lblFlur.setText(org.openide.util.NbBundle.getMessage(
                KatasterbuchwerkRissEditor.class,
                "KatasterbuchwerkRissEditor.lblFlur.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlGeneralInformation.add(lblFlur, gridBagConstraints);

        lblBlatt.setLabelFor(txtBlatt);
        lblBlatt.setText(org.openide.util.NbBundle.getMessage(
                KatasterbuchwerkRissEditor.class,
                "KatasterbuchwerkRissEditor.lblBlatt.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlGeneralInformation.add(lblBlatt, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.blatt}"),
                txtBlatt,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        pnlGeneralInformation.add(txtBlatt, gridBagConstraints);

        ftxFlur.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
                new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("000"))));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.flur}"),
                ftxFlur,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlGeneralInformation.add(ftxFlur, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.75;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
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
                KatasterbuchwerkRissEditor.class,
                "KatasterbuchwerkRissEditor.togPan.text"));                                // NOI18N
        togPan.setToolTipText(org.openide.util.NbBundle.getMessage(
                KatasterbuchwerkRissEditor.class,
                "KatasterbuchwerkRissEditor.togPan.toolTipText"));                         // NOI18N
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
                KatasterbuchwerkRissEditor.class,
                "KatasterbuchwerkRissEditor.togZoom.text"));                                // NOI18N
        togZoom.setToolTipText(org.openide.util.NbBundle.getMessage(
                KatasterbuchwerkRissEditor.class,
                "KatasterbuchwerkRissEditor.togZoom.toolTipText"));                         // NOI18N
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
                KatasterbuchwerkRissEditor.class,
                "KatasterbuchwerkRissEditor.btnHome.text"));                                // NOI18N
        btnHome.setToolTipText(org.openide.util.NbBundle.getMessage(
                KatasterbuchwerkRissEditor.class,
                "KatasterbuchwerkRissEditor.btnHome.toolTipText"));                         // NOI18N
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
                KatasterbuchwerkRissEditor.class,
                "KatasterbuchwerkRissEditor.lblHeaderControls.text")); // NOI18N
        pnlHeaderControls.add(lblHeaderControls);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        pnlControls.add(pnlHeaderControls, gridBagConstraints);

        btnOpen.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/folder-image.png"))); // NOI18N
        btnOpen.setText(org.openide.util.NbBundle.getMessage(
                KatasterbuchwerkRissEditor.class,
                "KatasterbuchwerkRissEditor.btnOpen.text"));                                        // NOI18N
        btnOpen.setToolTipText(org.openide.util.NbBundle.getMessage(
                KatasterbuchwerkRissEditor.class,
                "KatasterbuchwerkRissEditor.btnOpen.toolTipText"));                                 // NOI18N
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
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        panRight.add(pnlControls, gridBagConstraints);

        pnlDocuments.setLayout(new java.awt.GridBagLayout());

        pnlHeaderDocuments.setBackground(new java.awt.Color(51, 51, 51));
        pnlHeaderDocuments.setLayout(new java.awt.FlowLayout());

        lblHeaderDocuments.setForeground(new java.awt.Color(255, 255, 255));
        lblHeaderDocuments.setText(org.openide.util.NbBundle.getMessage(
                KatasterbuchwerkRissEditor.class,
                "KatasterbuchwerkRissEditor.lblHeaderDocuments.text")); // NOI18N
        pnlHeaderDocuments.add(lblHeaderDocuments);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        pnlDocuments.add(pnlHeaderDocuments, gridBagConstraints);

        bgrDocument.add(togBild);
        togBild.setSelected(true);
        togBild.setText(org.openide.util.NbBundle.getMessage(
                KatasterbuchwerkRissEditor.class,
                "KatasterbuchwerkRissEditor.togBild.text")); // NOI18N
        togBild.setFocusPainted(false);
        togBild.setMaximumSize(new java.awt.Dimension(49, 32));
        togBild.setMinimumSize(new java.awt.Dimension(49, 32));
        togBild.setPreferredSize(new java.awt.Dimension(152, 32));
        togBild.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    togBildActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 2, 10);
        pnlDocuments.add(togBild, gridBagConstraints);

        jLabel1.setText(org.openide.util.NbBundle.getMessage(
                KatasterbuchwerkRissEditor.class,
                "KatasterbuchwerkRissEditor.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        pnlDocuments.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        panRight.add(pnlDocuments, gridBagConstraints);

        pnlHeaderPages.setBackground(new java.awt.Color(51, 51, 51));
        pnlHeaderPages.setLayout(new java.awt.FlowLayout());

        lblHeaderPages.setForeground(new java.awt.Color(255, 255, 255));
        lblHeaderPages.setText(org.openide.util.NbBundle.getMessage(
                KatasterbuchwerkRissEditor.class,
                "KatasterbuchwerkRissEditor.lblHeaderPages.text")); // NOI18N
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
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        panRight.add(pnlPages, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.1;
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
        measuringComponent.actionPan();
    }                                                                          //GEN-LAST:event_togPanActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void togZoomActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_togZoomActionPerformed
        measuringComponent.actionZoom();
    }                                                                           //GEN-LAST:event_togZoomActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnHomeActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnHomeActionPerformed
        measuringComponent.actionOverview();
    }                                                                           //GEN-LAST:event_btnHomeActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnOpenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnOpenActionPerformed
        if ((currentDocument != NO_SELECTION) && (documentURLs[currentDocument] != null)) {
            try {
                final URL downloadURL;
                if (documentURLs[currentDocument].toExternalForm().contains(
                                VermessungsrissWebAccessPictureFinder.SUFFIX_REDUCED_SIZE)) {
                    final String url = documentURLs[currentDocument].toExternalForm()
                                .replaceAll(
                                    VermessungsrissWebAccessPictureFinder.getInstance().SUFFIX_REDUCED_SIZE,
                                    "");
                    downloadURL = new URL(url);
                } else {
                    downloadURL = documentURLs[currentDocument];
                }
                final String priceGroup = (String)cidsBean.getProperty("format.pricegroup");
                if (currentDocument == BUCHWERK) {
                    if (BillingPopup.doBilling(
                                    "vrpdf",
                                    downloadURL.toExternalForm(),
                                    (Geometry)null,
                                    new ProductGroupAmount(priceGroup, 1))) {
                        downloadProduct(downloadURL, true);
                    }
                } else {
                    if (BillingPopup.doBilling(
                                    "doklapdf",
                                    downloadURL.toExternalForm(),
                                    (Geometry)null,
                                    new ProductGroupAmount(priceGroup, 1))) {
                        downloadProduct(downloadURL, false);
                    }
                }
            } catch (Exception e) {
                LOG.error("Error when trying to produce a alkis product", e);
                // Hier noch ein Fehlerdialog
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  url                DOCUMENT ME!
     * @param  isVermessungsriss  DOCUMENT ME!
     */
    private void downloadProduct(final URL url, final boolean isVermessungsriss) {
        CismetThreadPool.execute(new Runnable() {

                @Override
                public void run() {
                    if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(
                                    KatasterbuchwerkRissEditor.this)) {
                        final String urlString = url.toExternalForm();
                        final String filename = urlString.substring(urlString.lastIndexOf("/") + 1);

                        DownloadManager.instance()
                                .add(
                                    new HttpDownload(
                                        url,
                                        "",
                                        DownloadManagerDialog.getInstance().getJobName(),
                                        (currentDocument == BUCHWERK) ? "Vermessungsriss" : "Ergänzende Dokumente",
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
    private void togBildActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_togBildActionPerformed
        currentSelectedButton = togBild;
        alertPanel.setContent(rissWarnMessage);
        alertPanel.repaint();
        loadBuchwerk();
        checkLinkInTitle();
    }                                                                           //GEN-LAST:event_togBildActionPerformed

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
        if (currentSelectedButton == togBild) {
            rissWarnMessage.setForeground(AlertPanel.dangerMessageColor);
            alertPanel.setContent(rissWarnMessage);
            alertPanel.repaint();
        }
        alertPanel.setPreferredSize(new Dimension(500, 50));
        alertPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // To change body of generated methods,
        // choose Tools | Templates.
        pnlGrenzniederschriftAlert.add(alertPanel, BorderLayout.CENTER);
        pnlGrenzniederschriftAlert.setBackground(new Color(1f, 1f, 1f, 0.8f));
        alertPanel.setVisible(false);
        alertPanel.addCloseButtonActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    pictureLoaderPanel.cancelPictureWorkers();
                    SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                measuringComponent.reset();
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
            if (currentSelectedButton == togBild) {
                alertPanel.setContent(rissWarnMessage);
            } else {
                alertPanel.setContent(grenzNiederschriftWarnMessage);
            }
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
        final Integer gemarkung = getGemarkungOfCurrentCidsBean();
        final String flur = getSimplePropertyOfCurrentCidsBean("flur");
        final String schluessel = getSimplePropertyOfCurrentCidsBean("schluessel");
        final String blatt = getSimplePropertyOfCurrentCidsBean("blatt");
        if (currentSelectedButton == togBild) {
            return VermessungsrissWebAccessPictureFinder.getInstance()
                        .getVermessungsrissPictureFilename(schluessel, gemarkung, flur, blatt);
        } else {
            return VermessungsrissWebAccessPictureFinder.getInstance()
                        .getGrenzniederschriftFilename(schluessel, gemarkung, flur, blatt);
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
        boolean isUmleitung = false;
        lblReducedSize.setVisible(false);
        if (url != null) {
            if (url.toString().contains("_rs")) {
                lblReducedSize.setVisible(true);
            }
            final String filename = getDocumentFilename();

            if (!url.toString().contains(filename)) {
                isUmleitung = true;
                if (url.toString().contains("_rs")) {
                    lblReducedSize.setVisible(true);
                }
                pnlHeaderDocument.repaint();
            }
        }

        if (!readOnly && isUmleitung) {
            lblHeaderDocument.setText(NbBundle.getMessage(
                    KatasterbuchwerkRissEditor.class,
                    "VermessungRissEditor.lblHeaderDocument.text.vermessungsriss_umleitung"));
        } else {
            if (currentDocument == BUCHWERK) {
                lblHeaderDocument.setText(NbBundle.getMessage(
                        KatasterbuchwerkRissEditor.class,
                        "VermessungRissEditor.lblHeaderDocument.text.vermessungsriss"));
            } else {
                lblHeaderDocument.setText(NbBundle.getMessage(
                        KatasterbuchwerkRissEditor.class,
                        "VermessungRissEditor.lblHeaderDocument.text.ergaenzendeDokumente"));
            }
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
        final String urlString = url.toString();
        if (urlString.contains(VermessungUmleitungPanel.PLATZHALTER_PREFIX)) {
            return urlString.substring(urlString.indexOf(VermessungUmleitungPanel.PLATZHALTER_PREFIX),
                    urlString.length()
                            - 4);
        }
        final String[] splittedUrl = url.toString().split("/");
        String s = splittedUrl[splittedUrl.length - 1];
        final int startPos = s.indexOf("_") + 1;
        final int endPos = startPos + 21;
        s = s.substring(startPos, endPos);
        return s;
    }

    /**
     * DOCUMENT ME!
     */
    public void successAlert() {
        setCurrentPageNull();
        alertPanel.setType(AlertPanel.TYPE.SUCCESS);
        pnlMeasureComponentWrapper.invalidate();
        pnlMeasureComponentWrapper.validate();
        pnlMeasureComponentWrapper.repaint();
    }

    /**
     * DOCUMENT ME!
     */
    public void handleNoDocumentFound() {
        pictureLoaderPanel.cancelPictureWorkers();
        alertPanel.setType(AlertPanel.TYPE.DANGER);
        measuringComponent.removeAllFeatures();
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
        showMeasureIsLoading();
        pictureLoaderPanel.setUrl(url);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  flag  DOCUMENT ME!
     */
    public void reloadDocuments(final boolean flag) {
        final RefreshDocumentWorker worker = new RefreshDocumentWorker(flag);
        worker.execute();
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

            final List<CidsBean> flurstuecksvermessung = cidsBean.getBeanCollectionProperty("flurstuecksvermessung");
            if ((flurstuecksvermessung != null) && !flurstuecksvermessung.isEmpty()) {
                Collections.sort(flurstuecksvermessung, AlphanumComparator.getInstance());
                try {
                    cidsBean.setProperty("flurstuecksvermessung", flurstuecksvermessung);
                } catch (final Exception ex) {
                    LOG.info("Couldn't sort the linked landparcels. Plausibility check of landparcels will fail.", ex);
                    // TODO: User feedback?
                }
            }

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

            schluessel = cidsBean.getProperty("schluessel");
            gemarkung = (cidsBean.getProperty("gemarkung") != null) ? cidsBean.getProperty("gemarkung.id") : null;
            flur = cidsBean.getProperty("flur");
            blatt = cidsBean.getProperty("blatt");
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
        measuringComponent.dispose();
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
     * Creates URLs based on the arguments. The arguments are given to <code>MessageFormat.format()</code>, where <code>
     * host</code> is the pattern, this call create a base URL. Different suffixes (.tif, .TIFF,...) are appended to
     * that base URL to create one URL for each suffix. Furthermore for each of this URLs an alternative with the <code>
     * SUFFIX_REDUCED_SIZE</code> is created.<br>
     * The return value is a Map where the key is a URL with a suffix and the value is the corresponding URL with the
     * additional <code>SUFFIX_REDUCED_SIZE</code>.
     *
     * @param   property  host DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    /**
     * DOCUMENT ME!
     *
     * @param   property  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected String getSimplePropertyOfCurrentCidsBean(final String property) {
        String result = "";

        if (cidsBean != null) {
            if (cidsBean.getProperty(property) != null) {
                result = (String)cidsBean.getProperty(property).toString();
            }
        }

        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected String generateTitle() {
        if (cidsBean == null) {
            return "Bitte wählen Sie einen Vermessungsriss.";
        }

        final StringBuilder result = new StringBuilder();
        final Object schluessel = cidsBean.getProperty("schluessel");
        final Object flur = cidsBean.getProperty("flur");
        final Object blatt = cidsBean.getProperty("blatt");

        result.append("Schlüssel ");
        if ((schluessel instanceof String) && (((String)schluessel).trim().length() > 0)) {
            result.append(schluessel);
        } else {
            result.append("unbekannt");
        }
        result.append(" - ");

        result.append("Gemarkung ");
        String gemarkung = "unbekannt";
        if ((cidsBean.getProperty("gemarkung") instanceof CidsBean)
                    && (cidsBean.getProperty("gemarkung.name") instanceof String)) {
            final String gemarkungFromBean = (String)cidsBean.getProperty("gemarkung.name");

            if (gemarkungFromBean.trim().length() > 0) {
                gemarkung = gemarkungFromBean;
            }
        }
        result.append(gemarkung);
        result.append(" - ");

        result.append("Flur ");
        if ((flur instanceof String) && (((String)flur).trim().length() > 0)) {
            result.append(flur);
        } else {
            result.append("unbekannt");
        }
        result.append(" - ");

        result.append("Blatt ");
        if ((blatt instanceof String) && (((String)blatt).trim().length() > 0)) {
            result.append(blatt);
        } else {
            result.append("unbekannt");
        }

        return result.toString();
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
    protected void loadBuchwerk() {
        showMeasureIsLoading();
        currentSelectedButton = togBild;
        currentDocument = BUCHWERK;
        checkLinkInTitle();
        showAlert(false);
        pictureLoaderPanel.setUrl(documentURLs[currentDocument]);
    }

    /**
     * DOCUMENT ME!
     */
    protected void setCurrentDocumentNull() {
        currentDocument = NO_SELECTION;
        setCurrentPageNull();
    }

    /**
     * DOCUMENT ME!
     */
    protected void setCurrentPageNull() {
        currentPage = NO_SELECTION;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        try {
            DevelopmentTools.createEditorInFrameFromRMIConnectionOnLocalhost(
                "WUNDA_BLAU",
                "Administratoren",
                "admin",
                "kif",
                "vermessung_riss",
                69681,
                1200,
                1200);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void warnAlert() {
        setCurrentPageNull();
        alertPanel.setType(AlertPanel.TYPE.WARNING);
        pnlMeasureComponentWrapper.invalidate();
        pnlMeasureComponentWrapper.validate();
        pnlMeasureComponentWrapper.repaint();
    }

    /**
     * DOCUMENT ME!
     */
    public void handleRissDoesNotExists() {
        setCurrentPageNull();
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
    protected final class RefreshDocumentWorker extends SwingWorker<List[], Object> {

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
        protected List[] doInBackground() throws Exception {
            final List[] result = new List[1];

            final Integer gemarkung = getGemarkungOfCurrentCidsBean();
            final String flur = getSimplePropertyOfCurrentCidsBean("flur");
            final String schluessel = getSimplePropertyOfCurrentCidsBean("schluessel");
            final String blatt = getSimplePropertyOfCurrentCidsBean("blatt");

            result[BUCHWERK] = VermessungsrissWebAccessPictureFinder.getInstance()
                        .findVermessungsrissPicture(schluessel, gemarkung, flur, blatt);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Textblätter:" + result[BUCHWERK]);
            }
            return result;
        }

        /**
         * Depending on the values in the documentURLs the GUI gets configured and the actual documents get loaded.
         */
        @Override
        protected void done() {
            try {
                if (!isCancelled()) {
                    final List[] result = get();
                    final StringBuffer collisionLists = new StringBuffer();
                    for (int i = 0; i < result.length; ++i) {
                        // cast!
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
                        final String collisionWarning = "Achtung: im Zielverzeichnis sind mehrere Dateien mit"
                                    + " demselben Namen in unterschiedlichen Dateiformaten "
                                    + "vorhanden.\n\nBitte löschen Sie die ungültigen Formate "
                                    + "und setzen Sie die Bearbeitung in WuNDa anschließend fort."
                                    + "\n\nDateien:\n"
                                    + collisionLists
                                    + "\n";
                        LOG.info(collisionWarning);
                    }
                }
            } catch (InterruptedException ex) {
                LOG.warn("Was interrupted while refreshing document.", ex);
            } catch (Exception ex) {
                LOG.warn("There was an exception while refreshing document.", ex);
            } finally {
                if (refreshMeasuringComponent) {
                    if (currentSelectedButton == togBild) {
                        loadBuchwerk();
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
    protected class HighlightReferencingFlurstueckeCellRenderer extends JLabel implements ListCellRenderer {

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
            setOpaque(true);

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            final String errorWhileLoading = "Fehler beim Laden des Flurstücks";

            final StringBuilder result = new StringBuilder();
            if (value instanceof CidsBean) {
                final CidsBean vermessung = (CidsBean)value;

                if (vermessung.getProperty("flurstueck") instanceof CidsBean) {
                    final CidsBean flurstueck = (CidsBean)vermessung.getProperty("flurstueck");

                    if (flurstueck.getProperty("flurstueck") instanceof CidsBean) {
                        if (isSelected) {
                            setBackground(list.getSelectionBackground());
                            setForeground(list.getSelectionForeground());
                        } else {
                            setBackground(list.getBackground());
                            setForeground(Color.blue);
                        }
                    }
                } else if (vermessung.getProperty("tmp_lp_orig") instanceof CidsBean) {
                    if (isSelected) {
                        setBackground(list.getSelectionBackground());
                        setForeground(list.getSelectionForeground());
                    } else {
                        setBackground(list.getBackground());
                        setForeground(Color.red);
                    }
                }
                result.append(value.toString());
                setText(result.toString());
            } else {
                result.append(errorWhileLoading);
            }
            return this;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    protected class GeometrieStatusRenderer implements ListCellRenderer {

        //~ Instance fields ----------------------------------------------------

        private ListCellRenderer originalRenderer;

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

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class DocumentSizeFilter extends DocumentFilter {

        //~ Methods ------------------------------------------------------------

        @Override
        public void insertString(final FilterBypass fb, final int offset, final String string, final AttributeSet attr)
                throws BadLocationException {
            if ((fb.getDocument().getLength() + string.length()) <= 31) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(final FilterBypass fb,
                final int offset,
                final int length,
                final String text,
                final AttributeSet attrs) throws BadLocationException {
            if ((fb.getDocument().getLength() + text.length() - length) <= 31) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }
}
