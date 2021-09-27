/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objecteditors.wunda_blau;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;
import Sirius.navigator.ui.ComponentRegistry;
import Sirius.navigator.ui.RequestsFullSizeComponent;

import Sirius.server.middleware.types.MetaObject;

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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.net.URL;

import java.sql.Date;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.utils.VermessungRissUtils;
import de.cismet.cids.custom.objecteditors.utils.VermessungUmleitungPanel;
import de.cismet.cids.custom.objectrenderer.utils.AlphanumComparator;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.objectrenderer.utils.VermessungFlurstueckFinder;
import de.cismet.cids.custom.objectrenderer.utils.VermessungsrissWebAccessPictureFinder;
import de.cismet.cids.custom.objectrenderer.utils.alkis.ClientAlkisConf;
import de.cismet.cids.custom.objectrenderer.utils.billing.BillingPopup;
import de.cismet.cids.custom.utils.alkis.VermessungsrissPictureFinder;
import de.cismet.cids.custom.utils.billing.BillingProductGroupAmount;
import de.cismet.cids.custom.wunda_blau.search.server.CidsVermessungRissArtSearchStatement;
import de.cismet.cids.custom.wunda_blau.search.server.CidsVermessungRissSearchStatement;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.DisposableCidsBeanStore;

import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;
import de.cismet.cids.editors.converters.SqlDateToStringConverter;

import de.cismet.cids.navigator.utils.CidsBeanDropListener;
import de.cismet.cids.navigator.utils.CidsBeanDropTarget;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.gui.RasterfariDocumentLoaderPanel;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.CismetThreadPool;

import de.cismet.tools.gui.BorderProvider;
import de.cismet.tools.gui.FooterComponentProvider;
import de.cismet.tools.gui.StaticSwingTools;
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
public class VermessungRissEditor extends javax.swing.JPanel implements DisposableCidsBeanStore,
    TitleComponentProvider,
    FooterComponentProvider,
    BorderProvider,
    RequestsFullSizeComponent,
    EditorSaveListener,
    ConnectionContextStore,
    RasterfariDocumentLoaderPanel.Listener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(VermessungRissEditor.class);
    private static final int VERMESSUNGSRISS = 0;
    private static final int GRENZNIEDERSCHRIFT = 1;
    private static final int NO_SELECTION = -1;
    private static final ListModel MODEL_LOAD = new DefaultListModel() {

            {
                add(0, "Wird geladen...");
            }
        };

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(
                PrecisionModel.FLOATING),
            CrsTransformer.extractSridFromCrs(ClientAlkisConf.getInstance().getSrsService()));
    private static Collection<CidsBean> veraenderungsarts = new LinkedList<CidsBean>();
    private static final Map<Integer, Color> COLORS_GEOMETRIE_STATUS = new HashMap<Integer, Color>();

    static {
        COLORS_GEOMETRIE_STATUS.put(new Integer(1), Color.green);
        COLORS_GEOMETRIE_STATUS.put(new Integer(2), Color.yellow);
        COLORS_GEOMETRIE_STATUS.put(new Integer(3), Color.yellow);
        COLORS_GEOMETRIE_STATUS.put(new Integer(4), Color.red);
        COLORS_GEOMETRIE_STATUS.put(new Integer(5), Color.red);
        COLORS_GEOMETRIE_STATUS.put(new Integer(6), Color.green);
    }

    //~ Instance fields --------------------------------------------------------

    private CidsBean cidsBean;
    private Object schluessel;
    private Object gemarkung;
    private Object flur;
    private Object blatt;
    private boolean readOnly;
    private String[] documents;
    private JToggleButton[] documentButtons;
    private JToggleButton currentSelectedButton;
    private VermessungFlurstueckSelectionDialog flurstueckDialog;
    private volatile int currentDocument = NO_SELECTION;
    private AlertPanel alertPanel;
    private VermessungUmleitungPanel umleitungsPanel;
    private boolean umleitungChangedFlag = false;
    private boolean showUmleitung = true;
    private boolean isErrorMessageVisible = true;

    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgrControls;
    private javax.swing.ButtonGroup bgrDocument;
    private javax.swing.JButton btnAddLandparcel;
    private javax.swing.JButton btnCombineGeometries;
    private javax.swing.JButton btnHome;
    private javax.swing.JButton btnOpen;
    private javax.swing.JButton btnRemoveLandparcel;
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
    private org.jdesktop.swingx.JXHyperlink jxlUmleitung;
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
    private javax.swing.JLabel lblHeaderLandparcels;
    private javax.swing.JLabel lblHeaderPages;
    private javax.swing.JLabel lblJahr;
    private javax.swing.JLabel lblLetzteAenderungDatum;
    private javax.swing.JLabel lblLetzteAenderungName;
    private javax.swing.JLabel lblReducedSize;
    private javax.swing.JLabel lblSchluessel;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JList lstLandparcels;
    private javax.swing.JList lstPages;
    private de.cismet.tools.gui.panels.LayeredAlertPanel measureComponentPanel;
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
    private de.cismet.tools.gui.SemiRoundedPanel pnlHeaderLandparcels;
    private de.cismet.tools.gui.SemiRoundedPanel pnlHeaderPages;
    private de.cismet.tools.gui.RoundedPanel pnlLandparcels;
    private javax.swing.JPanel pnlMeasureComp;
    private javax.swing.JPanel pnlMeasureComponentWrapper;
    private de.cismet.tools.gui.RoundedPanel pnlPages;
    private javax.swing.JPanel pnlTitle;
    private javax.swing.JPanel pnlUmleitungHeader;
    private javax.swing.JPopupMenu popChangeVeraenderungsart;
    private de.cismet.cismap.commons.gui.RasterfariDocumentLoaderPanel rasterfariDocumentLoaderPanel1;
    private javax.swing.JLabel rissWarnMessage;
    private javax.swing.JScrollPane scpLandparcels;
    private javax.swing.JScrollPane scpPages;
    private javax.swing.Box.Filler strFooter;
    private javax.swing.JToggleButton togBild;
    private javax.swing.JToggleButton togGrenzniederschrift;
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
    public VermessungRissEditor() {
        this(false);
    }

    /**
     * Creates new form VermessungRissEditor.
     *
     * @param  readOnly  DOCUMENT ME!
     */
    public VermessungRissEditor(final boolean readOnly) {
        this.readOnly = readOnly;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;

        documents = new String[2];
        documentButtons = new JToggleButton[documents.length];
        initComponents();
        alertPanel = new AlertPanel(
                AlertPanel.TYPE.DANGER,
                grenzNiederschriftWarnMessage,
                true);
        umleitungsPanel = new VermessungUmleitungPanel(
                VermessungUmleitungPanel.MODE.VERMESSUNGSRISS,
                this,
                getConnectionContext());
        documentButtons[VERMESSUNGSRISS] = togBild;
        documentButtons[GRENZNIEDERSCHRIFT] = togGrenzniederschrift;
        currentSelectedButton = togBild;
        initAlertPanel();
        jxlUmleitung.setClickedColor(new Color(204, 204, 204));
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
            btnAddLandparcel.setVisible(false);
            btnRemoveLandparcel.setVisible(false);
            btnCombineGeometries.setVisible(false);
        } else {
            new CidsBeanDropTarget((DropAwareJList)lstLandparcels);
            flurstueckDialog = new VermessungFlurstueckSelectionDialog();
            flurstueckDialog.pack();
            flurstueckDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            flurstueckDialog.addWindowListener(new EnableCombineGeometriesButton());
            if (txtBlatt.getDocument() instanceof AbstractDocument) {
                ((AbstractDocument)txtBlatt.getDocument()).setDocumentFilter(new DocumentSizeFilter());
            }
            if (ftxFlur.getDocument() instanceof AbstractDocument) {
                ((AbstractDocument)ftxFlur.getDocument()).setDocumentFilter(new DocumentSizeFilter());
            }
        }

        // Initialize the popup menu to change the veraenderungsart. Since the set of available veraenderungsart is very
        // unlikely to change, we once load it and save it in a static Collection.
        if ((veraenderungsarts == null) || veraenderungsarts.isEmpty()) {
            final Collection result;
            try {
                result = SessionManager.getProxy()
                            .customServerSearch(SessionManager.getSession().getUser(),
                                    new CidsVermessungRissArtSearchStatement(SessionManager.getSession().getUser()),
                                    getConnectionContext());
            } catch (final ConnectionException ex) {
                LOG.warn("Could not fetch veranederungsart entries. Editing flurstuecksvermessung will not work.", ex);
                // TODO: USer feedback?
                return;
            }

            for (final Object veraenderungsart : result) {
                veraenderungsarts.add(((MetaObject)veraenderungsart).getBean());
            }
        }

        for (final CidsBean veraenderungsart : veraenderungsarts) {
            popChangeVeraenderungsart.add(new ChangeVeraenderungsartAction(veraenderungsart));
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
        popChangeVeraenderungsart = new javax.swing.JPopupMenu();
        pnlMeasureComponentWrapper = new javax.swing.JPanel();
        pnlBusy = new javax.swing.JPanel();
        jxLBusyMeasure = new JXBusyLabel(new Dimension(64, 64));
        pnlMeasureComp = new javax.swing.JPanel();
        rasterfariDocumentLoaderPanel1 = new RasterfariDocumentLoaderPanel(
                ClientAlkisConf.getInstance().getRasterfariUrl(),
                this,
                connectionContext);
        pnlGrenzniederschriftAlert = new javax.swing.JPanel();
        grenzNiederschriftWarnMessage = new javax.swing.JLabel();
        rissWarnMessage = new javax.swing.JLabel();
        panLeft = new javax.swing.JPanel();
        pnlDocument = new de.cismet.tools.gui.RoundedPanel();
        pnlHeaderDocument = new de.cismet.tools.gui.SemiRoundedPanel();
        pnlUmleitungHeader = new javax.swing.JPanel();
        lblHeaderDocument = new javax.swing.JLabel();
        jxlUmleitung = new org.jdesktop.swingx.JXHyperlink();
        lblReducedSize = new javax.swing.JLabel();
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
        btnCombineGeometries = new javax.swing.JButton();
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
        pnlLandparcels = new de.cismet.tools.gui.RoundedPanel();
        pnlHeaderLandparcels = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeaderLandparcels = new javax.swing.JLabel();
        scpLandparcels = new javax.swing.JScrollPane();
        lstLandparcels = new DropAwareJList();
        btnAddLandparcel = new javax.swing.JButton();
        btnRemoveLandparcel = new javax.swing.JButton();
        pnlControls = new de.cismet.tools.gui.RoundedPanel();
        togPan = rasterfariDocumentLoaderPanel1.getTogPan();
        togZoom = rasterfariDocumentLoaderPanel1.getTogZoom();
        btnHome = rasterfariDocumentLoaderPanel1.getBtnHome();
        pnlHeaderControls = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeaderControls = new javax.swing.JLabel();
        btnOpen = new javax.swing.JButton();
        pnlDocuments = new de.cismet.tools.gui.RoundedPanel();
        pnlHeaderDocuments = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeaderDocuments = new javax.swing.JLabel();
        togBild = new javax.swing.JToggleButton();
        togGrenzniederschrift = new javax.swing.JToggleButton();
        jLabel1 = new javax.swing.JLabel();
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
                VermessungRissEditor.class,
                "VermessungRissEditor.lblTitle.text"));       // NOI18N
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
        pnlMeasureComp.add(rasterfariDocumentLoaderPanel1, java.awt.BorderLayout.CENTER);

        pnlMeasureComponentWrapper.add(pnlMeasureComp, "measureCard");

        pnlGrenzniederschriftAlert.setBackground(new java.awt.Color(254, 254, 254));
        pnlGrenzniederschriftAlert.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pnlGrenzniederschriftAlert.setLayout(new java.awt.BorderLayout());

        grenzNiederschriftWarnMessage.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissEditor.class,
                "VermessungRissEditor.grenzNiederschriftWarnMessage.text")); // NOI18N

        rissWarnMessage.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissEditor.class,
                "VermessungRissEditor.rissWarnMessage.text")); // NOI18N

        setLayout(new java.awt.GridBagLayout());

        panLeft.setOpaque(false);
        panLeft.setLayout(new java.awt.GridBagLayout());

        pnlHeaderDocument.setBackground(java.awt.Color.darkGray);
        pnlHeaderDocument.setLayout(new java.awt.GridBagLayout());

        pnlUmleitungHeader.setOpaque(false);
        pnlUmleitungHeader.setLayout(new java.awt.GridBagLayout());

        lblHeaderDocument.setForeground(java.awt.Color.white);
        lblHeaderDocument.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissEditor.class,
                "VermessungRissEditor.lblHeaderDocument.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlUmleitungHeader.add(lblHeaderDocument, gridBagConstraints);

        jxlUmleitung.setForeground(new java.awt.Color(204, 204, 204));
        jxlUmleitung.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissEditor.class,
                "VermessungRissEditor.jxlUmleitung.text"));        // NOI18N
        jxlUmleitung.setToolTipText(org.openide.util.NbBundle.getMessage(
                VermessungRissEditor.class,
                "VermessungRissEditor.jxlUmleitung.toolTipText")); // NOI18N
        jxlUmleitung.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jxlUmleitungActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        pnlUmleitungHeader.add(jxlUmleitung, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        pnlHeaderDocument.add(pnlUmleitungHeader, gridBagConstraints);

        lblReducedSize.setForeground(new java.awt.Color(254, 254, 254));
        lblReducedSize.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissEditor.class,
                "VermessungRissEditor.lblReducedSize.text")); // NOI18N
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
                VermessungRissEditor.class,
                "VermessungRissEditor.lblGeneralInformation.text")); // NOI18N
        pnlHeaderGeneralInformation.add(lblGeneralInformation);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        pnlGeneralInformation.add(pnlHeaderGeneralInformation, gridBagConstraints);

        lblJahr.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissEditor.class,
                "VermessungRissEditor.lblJahr.text")); // NOI18N
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
                VermessungRissEditor.class,
                "VermessungRissEditor.lblFormat.text")); // NOI18N
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
                VermessungRissEditor.class,
                "VermessungRissEditor.lblLetzteAenderungName.text")); // NOI18N
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
                VermessungRissEditor.class,
                "VermessungRissEditor.lblLetzteAenderungDatum.text")); // NOI18N
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
                VermessungRissEditor.class,
                "VermessungRissEditor.lblGeometrie.text")); // NOI18N
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

        btnCombineGeometries.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/wizard.png"))); // NOI18N
        btnCombineGeometries.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissEditor.class,
                "VermessungRissEditor.btnCombineGeometries.text"));                                     // NOI18N
        btnCombineGeometries.setToolTipText(org.openide.util.NbBundle.getMessage(
                VermessungRissEditor.class,
                "VermessungRissEditor.btnCombineGeometries.toolTipText"));                              // NOI18N
        btnCombineGeometries.setEnabled(false);
        btnCombineGeometries.setFocusPainted(false);
        btnCombineGeometries.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnCombineGeometriesActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 10);
        pnlGeneralInformation.add(btnCombineGeometries, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.1;
        pnlGeneralInformation.add(gluGeneralInformationGap, gridBagConstraints);

        lblGeometrieStatus.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissEditor.class,
                "VermessungRissEditor.lblGeometrieStatus.text")); // NOI18N
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
                VermessungRissEditor.class,
                "VermessungRissEditor.lblSchluessel.text")); // NOI18N
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
                VermessungRissEditor.class,
                "VermessungRissEditor.lblGemarkung.text")); // NOI18N
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
                VermessungRissEditor.class,
                "VermessungRissEditor.lblFlur.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        pnlGeneralInformation.add(lblFlur, gridBagConstraints);

        lblBlatt.setLabelFor(txtBlatt);
        lblBlatt.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissEditor.class,
                "VermessungRissEditor.lblBlatt.text")); // NOI18N
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

        pnlLandparcels.setLayout(new java.awt.GridBagLayout());

        pnlHeaderLandparcels.setBackground(new java.awt.Color(51, 51, 51));
        pnlHeaderLandparcels.setLayout(new java.awt.FlowLayout());

        lblHeaderLandparcels.setForeground(new java.awt.Color(255, 255, 255));
        lblHeaderLandparcels.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissEditor.class,
                "VermessungRissEditor.lblHeaderLandparcels.text")); // NOI18N
        pnlHeaderLandparcels.add(lblHeaderLandparcels);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.1;
        pnlLandparcels.add(pnlHeaderLandparcels, gridBagConstraints);

        scpLandparcels.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scpLandparcels.setMinimumSize(new java.awt.Dimension(266, 138));
        scpLandparcels.setOpaque(false);

        lstLandparcels.setCellRenderer(new HighlightReferencingFlurstueckeCellRenderer());

        final org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create(
                "${cidsBean.flurstuecksvermessung}");
        final org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings
                    .createJListBinding(
                        org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                        this,
                        eLProperty,
                        lstLandparcels);
        bindingGroup.addBinding(jListBinding);

        lstLandparcels.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mousePressed(final java.awt.event.MouseEvent evt) {
                    lstLandparcelsMousePressed(evt);
                }
                @Override
                public void mouseReleased(final java.awt.event.MouseEvent evt) {
                    lstLandparcelsMouseReleased(evt);
                }
                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lstLandparcelsMouseClicked(evt);
                }
            });
        lstLandparcels.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

                @Override
                public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
                    lstLandparcelsValueChanged(evt);
                }
            });
        scpLandparcels.setViewportView(lstLandparcels);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weighty = 0.1;
        pnlLandparcels.add(scpLandparcels, gridBagConstraints);

        btnAddLandparcel.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddLandparcel.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissEditor.class,
                "VermessungRissEditor.btnAddLandparcel.text"));                                                // NOI18N
        btnAddLandparcel.setToolTipText(org.openide.util.NbBundle.getMessage(
                VermessungRissEditor.class,
                "VermessungRissEditor.btnAddLandparcel.toolTipText"));                                         // NOI18N
        btnAddLandparcel.setFocusPainted(false);
        btnAddLandparcel.setMaximumSize(new java.awt.Dimension(43, 25));
        btnAddLandparcel.setMinimumSize(new java.awt.Dimension(43, 25));
        btnAddLandparcel.setPreferredSize(new java.awt.Dimension(43, 25));
        btnAddLandparcel.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddLandparcelActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_END;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 10, 2);
        pnlLandparcels.add(btnAddLandparcel, gridBagConstraints);

        btnRemoveLandparcel.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveLandparcel.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissEditor.class,
                "VermessungRissEditor.btnRemoveLandparcel.text"));                                                // NOI18N
        btnRemoveLandparcel.setToolTipText(org.openide.util.NbBundle.getMessage(
                VermessungRissEditor.class,
                "VermessungRissEditor.btnRemoveLandparcel.toolTipText"));                                         // NOI18N
        btnRemoveLandparcel.setEnabled(false);
        btnRemoveLandparcel.setFocusPainted(false);
        btnRemoveLandparcel.setMaximumSize(new java.awt.Dimension(43, 25));
        btnRemoveLandparcel.setMinimumSize(new java.awt.Dimension(43, 25));
        btnRemoveLandparcel.setPreferredSize(new java.awt.Dimension(43, 25));
        btnRemoveLandparcel.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemoveLandparcelActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 10, 10);
        pnlLandparcels.add(btnRemoveLandparcel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.25;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        panRight.add(pnlLandparcels, gridBagConstraints);

        pnlControls.setLayout(new java.awt.GridBagLayout());

        bgrControls.add(togPan);
        togPan.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/pan.gif"))); // NOI18N
        togPan.setSelected(true);
        togPan.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissEditor.class,
                "VermessungRissEditor.togPan.text"));                                      // NOI18N
        togPan.setToolTipText(org.openide.util.NbBundle.getMessage(
                VermessungRissEditor.class,
                "VermessungRissEditor.togPan.toolTipText"));                               // NOI18N
        togPan.setFocusPainted(false);
        togPan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
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
                VermessungRissEditor.class,
                "VermessungRissEditor.togZoom.text"));                                      // NOI18N
        togZoom.setToolTipText(org.openide.util.NbBundle.getMessage(
                VermessungRissEditor.class,
                "VermessungRissEditor.togZoom.toolTipText"));                               // NOI18N
        togZoom.setFocusPainted(false);
        togZoom.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 3, 10);
        pnlControls.add(togZoom, gridBagConstraints);

        btnHome.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/home.gif"))); // NOI18N
        btnHome.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissEditor.class,
                "VermessungRissEditor.btnHome.text"));                                      // NOI18N
        btnHome.setToolTipText(org.openide.util.NbBundle.getMessage(
                VermessungRissEditor.class,
                "VermessungRissEditor.btnHome.toolTipText"));                               // NOI18N
        btnHome.setFocusPainted(false);
        btnHome.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
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
                VermessungRissEditor.class,
                "VermessungRissEditor.lblHeaderControls.text")); // NOI18N
        pnlHeaderControls.add(lblHeaderControls);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        pnlControls.add(pnlHeaderControls, gridBagConstraints);

        btnOpen.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/folder-image.png"))); // NOI18N
        btnOpen.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissEditor.class,
                "VermessungRissEditor.btnOpen.text"));                                              // NOI18N
        btnOpen.setToolTipText(org.openide.util.NbBundle.getMessage(
                VermessungRissEditor.class,
                "VermessungRissEditor.btnOpen.toolTipText"));                                       // NOI18N
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
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        panRight.add(pnlControls, gridBagConstraints);

        pnlDocuments.setLayout(new java.awt.GridBagLayout());

        pnlHeaderDocuments.setBackground(new java.awt.Color(51, 51, 51));
        pnlHeaderDocuments.setLayout(new java.awt.FlowLayout());

        lblHeaderDocuments.setForeground(new java.awt.Color(255, 255, 255));
        lblHeaderDocuments.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissEditor.class,
                "VermessungRissEditor.lblHeaderDocuments.text")); // NOI18N
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
                VermessungRissEditor.class,
                "VermessungRissEditor.togBild.text")); // NOI18N
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

        bgrDocument.add(togGrenzniederschrift);
        togGrenzniederschrift.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissEditor.class,
                "VermessungRissEditor.togGrenzniederschrift.text")); // NOI18N
        togGrenzniederschrift.setFocusPainted(false);
        togGrenzniederschrift.setMaximumSize(new java.awt.Dimension(150, 32));
        togGrenzniederschrift.setMinimumSize(new java.awt.Dimension(150, 32));
        togGrenzniederschrift.setPreferredSize(new java.awt.Dimension(152, 32));
        togGrenzniederschrift.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    togGrenzniederschriftActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 0, 10);
        pnlDocuments.add(togGrenzniederschrift, gridBagConstraints);

        jLabel1.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissEditor.class,
                "VermessungRissEditor.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        pnlDocuments.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 0);
        panRight.add(pnlDocuments, gridBagConstraints);

        pnlHeaderPages.setBackground(new java.awt.Color(51, 51, 51));
        pnlHeaderPages.setLayout(new java.awt.FlowLayout());

        lblHeaderPages.setForeground(new java.awt.Color(255, 255, 255));
        lblHeaderPages.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissEditor.class,
                "VermessungRissEditor.lblHeaderPages.text")); // NOI18N
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
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        panRight.add(pnlPages, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
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
    private void btnOpenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnOpenActionPerformed
        if ((currentDocument != NO_SELECTION) && (documents[currentDocument] != null)) {
            try {
                final String document;
                if (documents[currentDocument].contains(VermessungsrissPictureFinder.SUFFIX_REDUCED_SIZE + ".")) {
                    document = documents[currentDocument].replaceAll(
                            VermessungsrissPictureFinder.SUFFIX_REDUCED_SIZE,
                            "");
                } else {
                    document = documents[currentDocument];
                }
                final URL url = rasterfariDocumentLoaderPanel1.getDocumentUrl(document);
                final String productGroupExt = (String)cidsBean.getProperty("format.productgroup_ext");
                final String priceGroup = (String)cidsBean.getProperty("format.pricegroup");
                if (currentDocument == VERMESSUNGSRISS) {
                    if (BillingPopup.doBilling(
                                    "vrpdf"
                                    + ((productGroupExt != null) ? productGroupExt : ""),
                                    url.toExternalForm(),
                                    (Geometry)null,
                                    getConnectionContext(),
                                    new BillingProductGroupAmount(priceGroup, 1))) {
                        downloadProduct(url, true);
                    }
                } else {
                    if (BillingPopup.doBilling(
                                    "doklapdf"
                                    + ((productGroupExt != null) ? productGroupExt : ""),
                                    url.toExternalForm(),
                                    (Geometry)null,
                                    getConnectionContext(),
                                    new BillingProductGroupAmount(priceGroup, 1))) {
                        downloadProduct(url, false);
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
                    if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(VermessungRissEditor.this)) {
                        final String urlString = url.toExternalForm();
                        final String filename = urlString.substring(urlString.lastIndexOf("/") + 1);

                        DownloadManager.instance()
                                .add(
                                    new HttpDownload(
                                        url,
                                        "",
                                        DownloadManagerDialog.getInstance().getJobName(),
                                        (isVermessungsriss) ? "Vermessungsriss" : "Ergänzende Dokumente",
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
    private void lstLandparcelsMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lstLandparcelsMouseClicked
        if (evt.getClickCount() <= 1) {
            return;
        }

        final Object selectedObj = lstLandparcels.getSelectedValue();
        if (selectedObj instanceof CidsBean) {
            final CidsBean selectedBean = (CidsBean)selectedObj;

            if ((selectedBean.getProperty("flurstueck") instanceof CidsBean)
                        && (selectedBean.getProperty("flurstueck.flurstueck") instanceof CidsBean)) {
                final MetaObject selMO = ((CidsBean)selectedBean.getProperty("flurstueck.flurstueck")).getMetaObject();
                ComponentRegistry.getRegistry().getDescriptionPane().gotoMetaObject(selMO, "");
            }
        }
    } //GEN-LAST:event_lstLandparcelsMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void togBildActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_togBildActionPerformed
        umleitungsPanel = new VermessungUmleitungPanel(
                VermessungUmleitungPanel.MODE.VERMESSUNGSRISS,
                this,
                getConnectionContext());
        showUmleitung = true;
        currentSelectedButton = togBild;
        alertPanel.setContent(rissWarnMessage);
        alertPanel.repaint();
        loadVermessungsriss();
        checkLinkInTitle();
    }                                                                           //GEN-LAST:event_togBildActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void togGrenzniederschriftActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_togGrenzniederschriftActionPerformed
        umleitungsPanel = new VermessungUmleitungPanel(
                VermessungUmleitungPanel.MODE.GRENZNIEDERSCHRIFT,
                this,
                getConnectionContext());
        showUmleitung = true;
        currentSelectedButton = togGrenzniederschrift;
        alertPanel.setContent(grenzNiederschriftWarnMessage);
        alertPanel.repaint();
        loadGrenzniederschrift();
//        checkLinkInTitle();
    }                                                                                         //GEN-LAST:event_togGrenzniederschriftActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddLandparcelActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddLandparcelActionPerformed
        flurstueckDialog.setCurrentListToAdd(CidsBeanSupport.getBeanCollectionFromProperty(
                cidsBean,
                "flurstuecksvermessung"));

        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(this), flurstueckDialog, true);
    } //GEN-LAST:event_btnAddLandparcelActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveLandparcelActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemoveLandparcelActionPerformed
        final Object[] selection = lstLandparcels.getSelectedValues();

        if ((selection != null) && (selection.length > 0)) {
            final int answer = JOptionPane.showConfirmDialog(
                    StaticSwingTools.getParentFrame(this),
                    "Soll das Flurstück wirklich entfernt werden?",
                    "Flurstück entfernen",
                    JOptionPane.YES_NO_OPTION);

            if (answer == JOptionPane.YES_OPTION) {
                final Collection flurstuecke = CidsBeanSupport.getBeanCollectionFromProperty(
                        cidsBean,
                        "flurstuecksvermessung");

                if (flurstuecke != null) {
                    for (final Object flurstueckToRemove : selection) {
                        try {
                            flurstuecke.remove(flurstueckToRemove);
                        } catch (Exception e) {
                            ObjectRendererUtils.showExceptionWindowToUser("Fehler beim Löschen", e, this);
                        } finally {
                            btnCombineGeometries.setEnabled(lstLandparcels.getModel().getSize() > 0);
                        }
                    }
                }
            }
        }
    } //GEN-LAST:event_btnRemoveLandparcelActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstLandparcelsValueChanged(final javax.swing.event.ListSelectionEvent evt) { //GEN-FIRST:event_lstLandparcelsValueChanged
        if (!evt.getValueIsAdjusting()) {
            btnRemoveLandparcel.setEnabled(!readOnly && (lstLandparcels.getSelectedIndex() > -1));
        }
    }                                                                                         //GEN-LAST:event_lstLandparcelsValueChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnCombineGeometriesActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnCombineGeometriesActionPerformed
        if (cidsBean == null) {
            return;
        }

        final Collection<CidsBean> flurstuecksvermessungen = cidsBean.getBeanCollectionProperty(
                "flurstuecksvermessung");
        final Collection<Geometry> union = new ArrayList<Geometry>();
        for (final CidsBean flurstuecksvermessung : flurstuecksvermessungen) {
            final CidsBean flurstueckKicker = (CidsBean)flurstuecksvermessung.getProperty("flurstueck.flurstueck");
            if ((flurstueckKicker != null)
                        && (flurstueckKicker.getProperty("umschreibendes_rechteck.geo_field") instanceof Geometry)) {
                final Geometry geometry = (Geometry)flurstueckKicker.getProperty(
                        "umschreibendes_rechteck.geo_field");
                final Geometry transformedGeometry = CrsTransformer.transformToGivenCrs(
                        geometry,
                        ClientAlkisConf.getInstance().getSrsService());

                union.add(transformedGeometry);
            }
        }

        if (union.isEmpty()) {
            LOG.warn("Could not find geometries on given landparcels. Did not attach a new geometry.");
            JOptionPane.showMessageDialog(
                StaticSwingTools.getParentFrame(this),
                "Keines der betroffenen Flurstücke weist eine Geometrie auf.",
                "Keine Geometrie erstellt",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        final Geometry unionGeometry = GEOMETRY_FACTORY.createGeometryCollection(union.toArray(new Geometry[0]))
                    .buffer(0);

        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("geo_field", unionGeometry);

        try {
            final CidsBean geomBean = CidsBeanSupport.createNewCidsBeanFromTableName(
                    "geom",
                    properties,
                    getConnectionContext());
            geomBean.persist(getConnectionContext());
            cidsBean.setProperty("geometrie", geomBean);
        } catch (Exception ex) {
            // TODO: Tell user about error.
            LOG.error("Could set new geometry: '" + unionGeometry.toText() + "'.", ex);
        }
    } //GEN-LAST:event_btnCombineGeometriesActionPerformed

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
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstLandparcelsMousePressed(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lstLandparcelsMousePressed
        if (!readOnly && popChangeVeraenderungsart.isPopupTrigger(evt)) {
            final int indexUnderMouse = lstLandparcels.locationToIndex(evt.getPoint());

            int[] selection = lstLandparcels.getSelectedIndices();

            boolean selectValueUnderMouse = true;
            if ((selection != null) && (selection.length > 0)) {
                for (final int index : selection) {
                    if (index == indexUnderMouse) {
                        selectValueUnderMouse = false;
                    }
                }
            }

            if (selectValueUnderMouse) {
                lstLandparcels.setSelectedIndex(lstLandparcels.locationToIndex(evt.getPoint()));
                selection = lstLandparcels.getSelectedIndices();
            }

            if ((selection != null) && (selection.length > 0)) {
                popChangeVeraenderungsart.show(evt.getComponent(), evt.getX(), evt.getY());
            }
        }
    } //GEN-LAST:event_lstLandparcelsMousePressed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstLandparcelsMouseReleased(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lstLandparcelsMouseReleased
        // Hock for popup menu. The return value of JPopupMenu.isPopupTrigger() depends on the OS.
        lstLandparcelsMousePressed(evt);
    } //GEN-LAST:event_lstLandparcelsMouseReleased

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxlUmleitungActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jxlUmleitungActionPerformed
        umleitungsPanel.reset();
        final String s = jxlUmleitung.getText();
//        umleitungsPanel.checkState();
        if ((documents[currentDocument] == null) && !s.startsWith(umleitungsPanel.PLATZHALTER_PREFIX)) {
            alertPanel.setType(AlertPanel.TYPE.WARNING);
            umleitungsPanel.setTextColor(AlertPanel.warningMessageColor);
        } else {
            alertPanel.setType(AlertPanel.TYPE.SUCCESS);
            umleitungsPanel.setTextColor(AlertPanel.successMessageColor);
        }
        umleitungsPanel.setLinkDocumentText(s);
        final VermessungUmleitungPanel.MODE mode;
        if (currentDocument == VERMESSUNGSRISS) {
            mode = VermessungUmleitungPanel.MODE.VERMESSUNGSRISS;
        } else {
            mode = VermessungUmleitungPanel.MODE.GRENZNIEDERSCHRIFT;
        }
        umleitungsPanel.setMode(mode);
        alertPanel.setContent(umleitungsPanel);
        alertPanel.setVisible(true);
        pnlMeasureComponentWrapper.invalidate();
        pnlMeasureComponentWrapper.validate();
        pnlMeasureComponentWrapper.repaint();
    }                                                                                //GEN-LAST:event_jxlUmleitungActionPerformed

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
        alertPanel.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(final MouseEvent e) {
                    handleAlertClick();
                }
            });
        alertPanel.addCloseButtonActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    showUmleitung = true;
                    final String editedLink = umleitungsPanel.getLinkDocument();
                    if ((jxlUmleitung.getText() == null) || jxlUmleitung.getText().isEmpty()) {
                        lstPages.setModel(new DefaultListModel());
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
                    } else {
                        if (!(jxlUmleitung.getText().isEmpty() || jxlUmleitung.getText().contains(editedLink))) {
                            showMeasureIsLoading();
                            lstPages.setModel(new DefaultListModel());
                            final RefreshDocumentWorker worker = new RefreshDocumentWorker(true);
                            worker.execute();
                        }
                    }
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
     * @param  flag  DOCUMENT ME!
     */
    private void showLinkInTitle(final boolean flag) {
        jxlUmleitung.setVisible(false);
        // !selfPersisting means editing
        if (flag && !readOnly) {
            jxlUmleitung.setVisible(true);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void clickedOnRissAlert() {
        if (showUmleitung) {
            showUmleitung = false;
            final String filename = "platzhalter/" + getSimplePropertyOfCurrentCidsBean("schluessel");
            umleitungsPanel.reset();
            umleitungsPanel.setMode(VermessungUmleitungPanel.MODE.VERMESSUNGSRISS);
            umleitungsPanel.setTextColor(AlertPanel.dangerMessageColor);
            alertPanel.setContent(umleitungsPanel);
            alertPanel.setVisible(true);
            alertPanel.repaint();
            SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        umleitungsPanel.setLinkDocumentText(filename, true);
                    }
                });
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void clickedOnGrenzniederschriftAlert() {
        if (isErrorMessageVisible) {
            /*
             *  in this case it is possible that already a umleitungssfile exists so we need to check if we have to set
             * a succes or a danger alert
             */
            if (showUmleitung && (currentDocument != NO_SELECTION)) {
                showUmleitung = false;
                final String document = documents[currentDocument];
                final String filename = getDocumentFilename();
                if (!((document != null) && !document.contains(filename))) {
                    umleitungsPanel.reset();
                    umleitungsPanel.setMode(VermessungUmleitungPanel.MODE.GRENZNIEDERSCHRIFT);
                    umleitungsPanel.setTextColor(AlertPanel.dangerMessageColor);
                    alertPanel.setContent(umleitungsPanel);
                }
            }
        } else {
            isErrorMessageVisible = true;
            showUmleitung = true;
            alertPanel.setContent(grenzNiederschriftWarnMessage);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void handleAlertClick() {
        if (currentSelectedButton == togBild) {
            clickedOnRissAlert();
        } else {
            clickedOnGrenzniederschriftAlert();
        }
        this.invalidate();
        this.validate();
        this.repaint();
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
        final String document = documents[currentDocument];
        checkLinkInTitle(document);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  document  DOCUMENT ME!
     */
    private void checkLinkInTitle(final String document) {
        showLinkInTitle(false);
        boolean isUmleitung = false;
        lblReducedSize.setVisible(false);
        if (document != null) {
            if (document.contains(VermessungsrissPictureFinder.SUFFIX_REDUCED_SIZE + ".")) {
                lblReducedSize.setVisible(true);
            }
            jxlUmleitung.setText("");
            final String filename = getDocumentFilename();

            if (!document.contains(filename)) {
                isUmleitung = true;
                jxlUmleitung.setText(extractFilename(document));
                showLinkInTitle(true);
                pnlHeaderDocument.repaint();
            }
        }

        if (!readOnly && isUmleitung) {
            lblHeaderDocument.setText(NbBundle.getMessage(
                    VermessungRissEditor.class,
                    "VermessungRissEditor.lblHeaderDocument.text.vermessungsriss_umleitung"));
        } else {
            if (currentDocument == VERMESSUNGSRISS) {
                lblHeaderDocument.setText(NbBundle.getMessage(
                        VermessungRissEditor.class,
                        "VermessungRissEditor.lblHeaderDocument.text.vermessungsriss"));
            } else {
                lblHeaderDocument.setText(NbBundle.getMessage(
                        VermessungRissEditor.class,
                        "VermessungRissEditor.lblHeaderDocument.text.ergaenzendeDokumente"));
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   document  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String extractFilename(final String document) {
        if (document.contains(VermessungUmleitungPanel.PLATZHALTER_PREFIX)) {
            return document.substring(document.indexOf(VermessungUmleitungPanel.PLATZHALTER_PREFIX),
                    document.length()
                            - 4);
        }
        final String[] splittedUrl = document.split("/");
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
        rasterfariDocumentLoaderPanel1.setCurrentPageNull();
        alertPanel.setType(AlertPanel.TYPE.SUCCESS);
        umleitungsPanel.setTextColor(AlertPanel.successMessageColor);
        pnlMeasureComponentWrapper.invalidate();
        pnlMeasureComponentWrapper.validate();
        pnlMeasureComponentWrapper.repaint();
    }

    /**
     * DOCUMENT ME!
     */
    public void handleNoDocumentFound() {
        alertPanel.setType(AlertPanel.TYPE.DANGER);
        umleitungsPanel.setTextColor(AlertPanel.dangerMessageColor);
        rasterfariDocumentLoaderPanel1.reset();
        this.invalidate();
        this.validate();
        this.repaint();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  document  url DOCUMENT ME!
     */
    public void reloadDocument(final String document) {
        showMeasureIsLoading();
        rasterfariDocumentLoaderPanel1.setDocument(document);
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
     * @param  document  DOCUMENT ME!
     */
    public void handleUmleitungCreated(final String document) {
        showAlert(false);
        umleitungChangedFlag = true;
        checkLinkInTitle(document);
        reloadDocuments(false);
        // when the url has no filename, the link was created to an document that does not exists
        if (!document.contains(".")) {
            documents[currentDocument] = null;
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void handleUmleitungDeleted() {
        showAlert(true);
        documents[currentDocument] = null;
        rasterfariDocumentLoaderPanel1.reset();
        umleitungChangedFlag = true;
        this.jxlUmleitung.setText("");
        this.showLinkInTitle(false);
        this.repaint();
    }

    /**
     * DOCUMENT ME!
     */
    public void handleEscapePressed() {
        rasterfariDocumentLoaderPanel1.reset();
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
//                try {
//                    cidsBean.setProperty("flurstuecksvermessung", flurstuecksvermessung);
//                } catch (final Exception ex) {
//                    LOG.info("Couldn't sort the linked landparcels. Plausibility check of landparcels will fail.", ex);
//                    // TODO: User feedback?
//                }
            }

            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                this.cidsBean,
                getConnectionContext());

            lblTitle.setText(generateTitle());
            btnCombineGeometries.setEnabled(lstLandparcels.getModel().getSize() > 0);
            if ((cidsBean.getProperty("geometrie_status") instanceof CidsBean)
                        && (cidsBean.getProperty("geometrie_status.id") instanceof Integer)) {
                cmbGeometrieStatus.setBackground(COLORS_GEOMETRIE_STATUS.get(
                        (Integer)cidsBean.getProperty("geometrie_status.id")));
            }

            schluessel = cidsBean.getProperty("schluessel");
            if ((schluessel != null) && (schluessel.equals("600") || schluessel.equals("504"))) {
                togGrenzniederschrift.setVisible(false);
            }
            gemarkung = (cidsBean.getProperty("gemarkung") != null) ? cidsBean.getProperty("gemarkung.id") : null;
            flur = cidsBean.getProperty("flur");
            blatt = cidsBean.getProperty("blatt");

            bindingGroup.bind();
        }

        setCurrentDocumentNull();

        new RefreshDocumentWorker().execute();
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    public void dispose() {
        // dispose panels here if necessary
// rasterfariDocumentLoaderPanel1.reset();
        rasterfariDocumentLoaderPanel1.dispose();
        if (flurstueckDialog != null) {
            flurstueckDialog.dispose();
        }
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
     * @param  event  DOCUMENT ME!
     */
    @Override
    public void editorClosed(final EditorClosedEvent event) {
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public boolean prepareForSave() {
        final StringBuilder errorMessage = new StringBuilder();

        if (cmbSchluessel.getSelectedItem() == null) {
            LOG.warn("No 'schluessel' specified. Skip persisting.");
            errorMessage.append(NbBundle.getMessage(
                    VermessungRissEditor.class,
                    "VermessungRissEditor.prepareForSave().noSchluessel"));
        }
        if (cmbGemarkung.getSelectedItem() == null) {
            LOG.warn("No 'gemarkung' specified. Skip persisting.");
            errorMessage.append(NbBundle.getMessage(
                    VermessungRissEditor.class,
                    "VermessungRissEditor.prepareForSave().noGemarkung"));
        }
        if ((ftxFlur.getText() == null) || ftxFlur.getText().trim().isEmpty()) {
            LOG.warn("No 'flur' specified. Skip persisting.");
            errorMessage.append(NbBundle.getMessage(
                    VermessungRissEditor.class,
                    "VermessungRissEditor.prepareForSave().noFlur"));
        } else if (ftxFlur.getText().length() > 3) {
            LOG.warn("Property 'flur' is too long. Skip persisting.");
            errorMessage.append(NbBundle.getMessage(
                    VermessungRissEditor.class,
                    "VermessungRissEditor.prepareForSave().tooLongFlur"));
        }
        if ((txtBlatt.getText() == null) || txtBlatt.getText().trim().isEmpty()) {
            LOG.warn("No 'blatt' specified. Skip persisting.");
            errorMessage.append(NbBundle.getMessage(
                    VermessungRissEditor.class,
                    "VermessungRissEditor.prepareForSave().noBlatt"));
        } else if (txtBlatt.getText().length() > 31) {
            LOG.warn("Property 'blatt' is too long. Skip persisting.");
            errorMessage.append(NbBundle.getMessage(
                    VermessungRissEditor.class,
                    "VermessungRissEditor.prepareForSave().tooLongBlatt"));
        }

        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(
                StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(
                    VermessungRissEditor.class,
                    "VermessungRissEditor.prepareForSave().JOptionPane.message.prefix")
                        + errorMessage.toString()
                        + NbBundle.getMessage(
                            VermessungRissEditor.class,
                            "VermessungRissEditor.prepareForSave().JOptionPane.message.suffix"),
                NbBundle.getMessage(
                    VermessungRissEditor.class,
                    "VermessungRissEditor.prepareForSave().JOptionPane.title"),
                JOptionPane.WARNING_MESSAGE);

            return false;
        }

        final Object newSchluessel = cidsBean.getProperty("schluessel");
        final Object newGemarkung = cidsBean.getProperty("gemarkung.id");
        final Object newFlur = cidsBean.getProperty("flur");
        final Object newBlatt = cidsBean.getProperty("blatt");

        final CidsVermessungRissSearchStatement search = new CidsVermessungRissSearchStatement(
                newSchluessel.toString(),
                newGemarkung.toString(),
                newFlur.toString(),
                newBlatt.toString(),
                null,
                null,
                null);

        final Collection result;

        try {
            result = SessionManager.getProxy()
                        .customServerSearch(SessionManager.getSession().getUser(),
                                search,
                                getConnectionContext());
        } catch (final ConnectionException ex) {
            LOG.error("Could not check if the natural key of this measurement sketch is valid.", ex);
            JOptionPane.showMessageDialog(
                this,
                NbBundle.getMessage(
                    VermessungRissEditor.class,
                    "VermessungRissEditor.prepareForSave().noConnection.message"),
                NbBundle.getMessage(
                    VermessungRissEditor.class,
                    "VermessungRissEditor.prepareForSave().noConnection.title"),
                JOptionPane.WARNING_MESSAGE);

            return false;
        }

        boolean save = true;

        if ((result
                        != null) && !result.isEmpty()
                    && !(newSchluessel.equals(schluessel) && newGemarkung.equals(gemarkung) && newFlur.equals(flur)
                        && newBlatt.equals(blatt))) {
            save = false;

            if (LOG.isDebugEnabled()) {
                LOG.debug("The given natural key of the measurement sketch already exists. Skip saving.");
            }

            JOptionPane.showMessageDialog(
                this,
                NbBundle.getMessage(
                    VermessungRissEditor.class,
                    "VermessungRissEditor.prepareForSave().keyExists.message"),
                NbBundle.getMessage(
                    VermessungRissEditor.class,
                    "VermessungRissEditor.prepareForSave().keyExists.title"),
                JOptionPane.WARNING_MESSAGE);
        } else {
            save = true;

            try {
                cidsBean.setProperty("letzteaenderung_datum", new Date(System.currentTimeMillis()));
                cidsBean.setProperty("letzteaenderung_name", SessionManager.getSession().getUser().getName());
            } catch (final Exception ex) {
                LOG.warn("Could not save date and user of last change.", ex);
                // TODO: User feedback?
            }
        }

        try {
            // Check if new Vermessung_Flurstuecksvermessung Objects are in the flurstuecksvermessung Collection that
            // are created via a dropped "ALKIS_landparcel"- or "flurstueck"-Objects

            // 1. Store the objects in a collection
            final List<CidsBean> vermessungen = cidsBean.getBeanCollectionProperty("flurstuecksvermessung");

            // 2. Iterate
            for (final CidsBean entry : vermessungen) {
                VermessungRissUtils.setFluerstueckKickerInVermessung(entry, getConnectionContext());
            }
        } catch (Exception e) {
            LOG.error("Problem when working on dropped landparcels", e);
            save = false;
        }

        if (save) {
            final CidsBean geometrieStatus = (CidsBean)cidsBean.getProperty("geometrie_status");

            if ((geometrieStatus != null) && (geometrieStatus.getProperty("id") instanceof Integer)) {
                final Integer geometrieStatusId = (Integer)geometrieStatus.getProperty("id");

                if (geometrieStatusId.intValue() == 6) {
                    final Object nameObj = cidsBean.getProperty("optimiert_name");
                    final String name;

                    if (nameObj instanceof String) {
                        name = (String)nameObj;
                    } else {
                        name = "";
                    }

                    if (name.isEmpty()) {
                        try {
                            cidsBean.setProperty("optimiert_datum", new Date(System.currentTimeMillis()));
                            cidsBean.setProperty("optimiert_name", SessionManager.getSession().getUser().getName());
                        } catch (final Exception ex) {
                            LOG.info("Couldn't save who changed when the geometry's state to '"
                                        + geometrieStatus.getProperty("name") + "'.",
                                ex);
                            // TODO: User feedback?
                        }
                    }
                }
            }
        }

        return save;
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
    private String getSimplePropertyOfCurrentCidsBean(final String property) {
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
    private String generateTitle() {
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
    private Integer getGemarkungOfCurrentCidsBean() {
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
    private void loadVermessungsriss() {
        showMeasureIsLoading();
        currentSelectedButton = togBild;
        currentDocument = VERMESSUNGSRISS;
        checkLinkInTitle();
        showAlert(false);
        final String document = documents[currentDocument];
        if (document == null) {
            final String link = VermessungsrissWebAccessPictureFinder.getInstance()
                        .getLinkFromLinkDocument(false, getDocumentFilename());
            if ((link != null) && !link.isEmpty()) {
                jxlUmleitung.setText(link);
                showLinkInTitle(true);
                pnlHeaderDocument.repaint();
            } else {
                showAlert(true);
            }
            showMeasurePanel();
            return;
        } else {
            rasterfariDocumentLoaderPanel1.setDocument(document);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void loadGrenzniederschrift() {
        showMeasureIsLoading();
        currentSelectedButton = togGrenzniederschrift;
        currentDocument = GRENZNIEDERSCHRIFT;
        checkLinkInTitle();
        showAlert(false);
        final String document = documents[currentDocument];
        if (document == null) {
            final String link = VermessungsrissWebAccessPictureFinder.getInstance()
                        .getLinkFromLinkDocument(true, getDocumentFilename());
            if ((link != null) && !link.isEmpty()) {
                jxlUmleitung.setText(link);
                showLinkInTitle(true);
                pnlHeaderDocument.repaint();
            } else {
                showAlert(true);
            }
            showMeasurePanel();
            return;
        } else {
            rasterfariDocumentLoaderPanel1.setDocument(document);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void setCurrentDocumentNull() {
        currentDocument = NO_SELECTION;
        rasterfariDocumentLoaderPanel1.setCurrentPageNull();
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
        rasterfariDocumentLoaderPanel1.setCurrentPageNull();
        alertPanel.setType(AlertPanel.TYPE.WARNING);
        umleitungsPanel.setTextColor(AlertPanel.warningMessageColor);
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
        umleitungsPanel.setTextColor(AlertPanel.dangerMessageColor);
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
     * @version  $Revision$, $Date$
     */
    private final class RefreshDocumentWorker extends SwingWorker<List[], Object> {

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
        protected List[] doInBackground() throws Exception {
            final List[] result = new List[2];

            final Integer gemarkung = getGemarkungOfCurrentCidsBean();
            final String flur = getSimplePropertyOfCurrentCidsBean("flur");
            final String schluessel = getSimplePropertyOfCurrentCidsBean("schluessel");
            final String blatt = getSimplePropertyOfCurrentCidsBean("blatt");

            result[VERMESSUNGSRISS] = VermessungsrissWebAccessPictureFinder.getInstance()
                        .findVermessungsrissPicture(schluessel, gemarkung, flur, blatt);
            result[GRENZNIEDERSCHRIFT] = VermessungsrissWebAccessPictureFinder.getInstance()
                        .findGrenzniederschriftPicture(schluessel, gemarkung, flur, blatt);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Textblätter:" + result[VERMESSUNGSRISS]);
                LOG.debug("Lagepläne:" + result[GRENZNIEDERSCHRIFT]);
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
                        final List<String> current = result[i];
                        if (current != null) {
                            if (current.size() > 0) {
                                if (current.size() > 1) {
                                    if (collisionLists.length() > 0) {
                                        collisionLists.append(",\n");
                                    }
                                    collisionLists.append(current);
                                }
                                documents[i] = current.get(0);
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
            } catch (final InterruptedException ex) {
                LOG.warn("Was interrupted while refreshing document.", ex);
            } catch (final Exception ex) {
                LOG.warn("There was an exception while refreshing document.", ex);
            } finally {
                if (refreshMeasuringComponent) {
                    if (currentSelectedButton == togBild) {
                        loadVermessungsriss();
                    } else if (currentSelectedButton == togGrenzniederschrift) {
                        loadGrenzniederschrift();
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
    private class EnableCombineGeometriesButton extends WindowAdapter {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  e  DOCUMENT ME!
         */
        @Override
        public void windowDeactivated(final WindowEvent e) {
            super.windowDeactivated(e);

            btnCombineGeometries.setEnabled(lstLandparcels.getModel().getSize() > 0);
        }

        /**
         * DOCUMENT ME!
         *
         * @param  e  DOCUMENT ME!
         */
        @Override
        public void windowClosed(final WindowEvent e) {
            super.windowClosed(e);

            btnCombineGeometries.setEnabled(lstLandparcels.getModel().getSize() > 0);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class HighlightReferencingFlurstueckeCellRenderer extends JLabel implements ListCellRenderer {

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
    private class GeometrieStatusRenderer implements ListCellRenderer {

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
    private final class ChangeVeraenderungsartAction extends AbstractAction {

        //~ Instance fields ----------------------------------------------------

        private final CidsBean veraenderungsart;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ChangeVeraenderungsartAction object.
         *
         * @param  veraenderungsart  DOCUMENT ME!
         */
        public ChangeVeraenderungsartAction(final CidsBean veraenderungsart) {
            this.veraenderungsart = veraenderungsart;

            putValue(
                NAME,
                this.veraenderungsart.getProperty("code")
                        + " - "
                        + this.veraenderungsart.getProperty("name"));
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  e  DOCUMENT ME!
         */
        @Override
        public void actionPerformed(final ActionEvent e) {
            for (final Object flurstuecksvermessung : lstLandparcels.getSelectedValues()) {
                try {
                    ((CidsBean)flurstuecksvermessung).setProperty("veraenderungsart", veraenderungsart);
                    lstLandparcels.clearSelection();
                    lstLandparcels.revalidate();
                    lstLandparcels.repaint();
                } catch (final Exception ex) {
                    LOG.info("Couldn't set veraenderungsart to '" + veraenderungsart + "' for flurstuecksvermessung '"
                                + flurstuecksvermessung + "'.",
                        ex);
                    // TODO: User feedback?
                }
            }
        }
    }

    //J-
    private final class DocumentSizeFilter extends DocumentFilter {

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if ((fb.getDocument().getLength() + string.length()) <= 31) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if ((fb.getDocument().getLength() + text.length() - length) <= 31) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }
    //J+

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class DropAwareJList extends JList implements CidsBeanDropListener {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new DropAwareJList object.
         */
        public DropAwareJList() {
        }

        /**
         * Creates a new DropAwareJList object.
         *
         * @param  dataModel  DOCUMENT ME!
         */
        public DropAwareJList(final ListModel dataModel) {
            super(dataModel);
        }

        /**
         * Creates a new DropAwareJList object.
         *
         * @param  listData  DOCUMENT ME!
         */
        public DropAwareJList(final Object[] listData) {
            super(listData);
        }

        /**
         * Creates a new DropAwareJList object.
         *
         * @param  listData  DOCUMENT ME!
         */
        public DropAwareJList(final Vector listData) {
            super(listData);
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  beans  DOCUMENT ME!
         */
        @Override
        public void beansDropped(final ArrayList<CidsBean> beans) {
            MetaObject veraenderungsartMO = null;
            CidsBean veraenderungsart = null;
            // veraenderungsart aus Dialog abfragen

            final MetaObject[] arten = VermessungFlurstueckFinder.getVeraenderungsarten(getConnectionContext());
            veraenderungsartMO = (MetaObject)JOptionPane.showInputDialog(
                    StaticSwingTools.getParentFrame(this),
                    "Bitte Veränderungsart auswählen?",
                    "Veränderungsart",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    arten,
                    arten[0]);
            veraenderungsart = veraenderungsartMO.getBean();
            try {
                final List<CidsBean> landparcels = cidsBean.getBeanCollectionProperty("flurstuecksvermessung");
                for (final CidsBean dropped : beans) {
                    final CidsBean newEntry = CidsBean.createNewCidsBeanFromTableName(
                            "WUNDA_BLAU",
                            "vermessung_flurstuecksvermessung",
                            getConnectionContext());
                    newEntry.setProperty("veraenderungsart", veraenderungsart);
                    newEntry.setProperty("tmp_lp_orig", dropped);
                    landparcels.add(newEntry);
                }
            } catch (Exception ex) {
                LOG.error("Problem when adding the DroppedBeans", ex);
            }
        }
    }
}
