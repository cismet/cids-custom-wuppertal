/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objecteditors.wunda_blau;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.ui.RequestsFullSizeComponent;

import Sirius.server.middleware.types.LightweightMetaObject;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.Point;

import java.awt.AlphaComposite;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import java.net.URL;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.ComboPopup;

import de.cismet.cids.client.tools.DevelopmentTools;
import de.cismet.cids.client.tools.WebDavTunnelHelper;

import de.cismet.cids.custom.commons.gui.ScrollablePanel;
import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objectrenderer.utils.VzkatProperties;
import de.cismet.cids.custom.objectrenderer.utils.alkis.ClientAlkisConf;
import de.cismet.cids.custom.orbit.OrbitControlFeature;
import de.cismet.cids.custom.wunda_blau.search.server.StrAdrStrasseLightweightSearch;
import de.cismet.cids.custom.wunda_blau.search.server.VzkatSchilderSearch;
import de.cismet.cids.custom.wunda_blau.search.server.VzkatStandortNextSchluesselServerSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.features.StyledFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.gui.piccolo.FeatureAnnotationSymbol;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.netutil.Proxy;
import de.cismet.netutil.ProxyHandler;

import de.cismet.security.WebAccessManager;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.TitleComponentProvider;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;

import static de.cismet.cids.custom.objectrenderer.utils.billing.BillingPopup.implode;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class VzkatStandortEditor extends javax.swing.JPanel implements CidsBeanRenderer,
    TitleComponentProvider,
    RequestsFullSizeComponent,
    ConnectionContextStore,
    EditorSaveListener,
    DropTargetListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(VzkatStandortEditor.class);

    public static final String STRASSENNAME_TOSTRING_TEMPLATE = "%s";
    public static final String[] STRASSENNAME_TOSTRING_FIELDS = {
            StrAdrStrasseLightweightSearch.Subject.NAME.toString()
        };
    public static final String STRASSENSCHLUESSEL_TOSTRING_TEMPLATE = "%s";
    public static final String[] STRASSENSCHLUESSEL_TOSTRING_FIELDS = {
            StrAdrStrasseLightweightSearch.Subject.SCHLUESSEL.toString()
        };

    public static final String[] ALLOWED_EXTENSIONS = { "png", "jpg", "jpeg" };

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private enum OvDirection {

        //~ Enum constants -----------------------------------------------------

        BACKWARDS, CENTER, FORWARDS
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private enum Richtung {

        //~ Enum constants -----------------------------------------------------

        VORNE {

            @Override
            public String toString() {
                return "vorne";
            }
        },
        HINTEN {

            @Override
            public String toString() {
                return "hinten";
            }
        },
        SONSTIGE {

            @Override
            public String toString() {
                return "sonstige";
            }
        }
    }

    //~ Instance fields --------------------------------------------------------

    final WebDavTunnelHelper webdavHelper = new WebDavTunnelHelper(
            "WUNDA_BLAU",
            ProxyHandler.getInstance().getProxy(),
            VzkatProperties.getInstance().getWebdavUploadUsername(),
            VzkatProperties.getInstance().getWebdavUploadPassword(),
            false);

    boolean refreshingSchildPanels = false;

    private final StrAdrStrasseLightweightSearch strassennameSearch = new StrAdrStrasseLightweightSearch(
            StrAdrStrasseLightweightSearch.Subject.NAME,
            STRASSENNAME_TOSTRING_TEMPLATE,
            STRASSENNAME_TOSTRING_FIELDS);
    private final StrAdrStrasseLightweightSearch strassenschluesselSearch = new StrAdrStrasseLightweightSearch(
            StrAdrStrasseLightweightSearch.Subject.SCHLUESSEL,
            STRASSENSCHLUESSEL_TOSTRING_TEMPLATE,
            STRASSENSCHLUESSEL_TOSTRING_FIELDS);

    private final boolean editable;
    private final List<CidsBean> schildBeans = new ArrayList<>();
    private final List<CidsBean> deletedSchildBeans = new ArrayList<>();
    private final DefaultStyledFeature viewPreviewFeature = new DefaultStyledFeature();

    private ConnectionContext connectionContext;
    private CidsBean cidsBean;
    private CidsBean selectedSchildBean;

    private boolean cbStrassenschluesselEnabled = true;
    private boolean cbStrassennameEnabled = true;
    private boolean comboboxesInited = false;
    private CidsBean standortBean;
    private Richtung richtung;
    private File uploadFile = null;

    private boolean mapInitialized = false;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFullMinimize;
    private javax.swing.JButton btnFullNext;
    private javax.swing.JButton btnFullPrev;
    private javax.swing.JButton btnFullUpload;
    private javax.swing.JButton btnPreviewMaximize;
    private javax.swing.JButton btnPreviewNext;
    private javax.swing.JButton btnPreviewPrev;
    private javax.swing.JButton btnPreviewUpload;
    private javax.swing.JComboBox cbGeom;
    private de.cismet.cids.editors.FastBindableReferenceCombo cbStrassenname;
    private de.cismet.cids.editors.FastBindableReferenceCombo cbStrassenschluessel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private org.jdesktop.swingx.JXHyperlink jxhOVBW;
    private org.jdesktop.swingx.JXHyperlink jxhOVCenter;
    private org.jdesktop.swingx.JXHyperlink jxhOVFW;
    private javax.swing.JLabel lblBildTitle1;
    private javax.swing.JLabel lblFullIconHinten;
    private javax.swing.JLabel lblFullIconSonstige;
    private javax.swing.JLabel lblFullIconVorne;
    private javax.swing.JLabel lblFullTitle;
    private javax.swing.JLabel lblGeom;
    private javax.swing.JLabel lblOvPreviewBackwards;
    private javax.swing.JLabel lblOvPreviewCenter;
    private javax.swing.JLabel lblOvPreviewForwards;
    private javax.swing.JLabel lblPreviewIconHinten;
    private javax.swing.JLabel lblPreviewIconSonstige;
    private javax.swing.JLabel lblPreviewIconVorne;
    private javax.swing.JLabel lblPreviewTitle;
    private javax.swing.JLabel lblStrasse;
    private javax.swing.JLabel lblStrassenschluessel;
    private javax.swing.JLabel lblUploadIcon;
    private javax.swing.JLabel lblUploadIconHinten;
    private javax.swing.JLabel lblUploadIconSonstige;
    private javax.swing.JLabel lblUploadIconVorne;
    private de.cismet.cismap.commons.gui.MappingComponent mappingComponent1;
    private de.cismet.tools.gui.SemiRoundedPanel panBildTitle;
    private de.cismet.tools.gui.SemiRoundedPanel panBildTitle1;
    private de.cismet.tools.gui.RoundedPanel panFullIcon;
    private de.cismet.tools.gui.RoundedPanel panLageBody;
    private de.cismet.tools.gui.SemiRoundedPanel panLageTitle;
    private javax.swing.JPanel panPreviewIcon;
    private de.cismet.tools.gui.RoundedPanel panStandortKarteBody;
    private javax.swing.JPanel panTitle;
    private javax.swing.JPanel panUploadIconOld;
    private javax.swing.JLabel txtTitle;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VzkatSchildEditor object.
     */
    public VzkatStandortEditor() {
        this(true);
    }

    /**
     * Creates a new VzkatSchildEditor object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public VzkatStandortEditor(final boolean editable) {
        this.editable = editable;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void dragEnter(final DropTargetDragEvent dtde) {
        if (isEditable() && dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
        } else {
            dtde.rejectDrag();
        }
    }

    @Override
    public void dragOver(final DropTargetDragEvent dtde) {
    }

    @Override
    public void dropActionChanged(final DropTargetDragEvent dtde) {
    }

    @Override
    public void dragExit(final DropTargetEvent dte) {
    }

    @Override
    public void drop(final DropTargetDropEvent dtde) {
        if (isEditable() && dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
            try {
                final List<File> files = ((List<File>)dtde.getTransferable().getTransferData(
                            DataFlavor.javaFileListFlavor));
                if ((files != null) && (files.size() == 1)) {
                    final File file = files.iterator().next();
                    uploadImageToWebDav(file);
                }
                dtde.dropComplete(true);
            } catch (Exception ex) {
                LOG.warn(ex, ex);
            }
        } else {
            dtde.rejectDrop();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
        Log4JQuickConfig.configure4LumbermillOnLocalhost();
        final MappingComponent mc = new MappingComponent();
        CismapBroker.getInstance().setMappingComponent(mc);
        DevelopmentTools.createEditorFromRestfulConnection(
            DevelopmentTools.RESTFUL_CALLSERVER_CALLSERVER,
            "WUNDA_BLAU",
            null,
            true,
            "vzkat_standort",
            1,
            800,
            600);
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        initComponents();

        if (editable) {
            new DropTarget(jPanel8, this);
            new DropTarget(jPanel13, this);
        }

        jXDatePicker1.setDate(new Date());

        btnFullUpload.setVisible(isEditable());
        btnPreviewUpload.setVisible(isEditable());

        if (!editable) {
            RendererTools.makeReadOnly(cbStrassenschluessel);
            RendererTools.makeReadOnly(cbStrassenname);
        } else {
            StaticSwingTools.decorateWithFixedAutoCompleteDecorator(cbStrassenschluessel);
            {
                final JList pop = ((ComboPopup)cbStrassenschluessel.getUI().getAccessibleChild(cbStrassenschluessel, 0))
                            .getList();
                final JTextField txt = (JTextField)cbStrassenschluessel.getEditor().getEditorComponent();
                cbStrassenschluessel.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            final Object selectedValue = pop.getSelectedValue();
                            txt.setText((selectedValue != null) ? String.valueOf(selectedValue) : "");
                        }
                    });
            }

            StaticSwingTools.decorateWithFixedAutoCompleteDecorator(cbStrassenname);
            {
                final JList pop = ((ComboPopup)cbStrassenname.getUI().getAccessibleChild(cbStrassenname, 0)).getList();
                final JTextField txt = (JTextField)cbStrassenname.getEditor().getEditorComponent();
                cbStrassenname.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            final Object selectedValue = pop.getSelectedValue();
                            txt.setText((selectedValue != null) ? String.valueOf(selectedValue) : "");
                        }
                    });
            }
        }

        initComboboxes();
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

        panTitle = new javax.swing.JPanel();
        txtTitle = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jFileChooser1 = new javax.swing.JFileChooser();
        jDialog1 = new javax.swing.JDialog();
        jPanel9 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel15 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        jPanel16 = new javax.swing.JPanel();
        panUploadIconOld = new javax.swing.JPanel();
        lblUploadIconVorne = new javax.swing.JLabel();
        lblUploadIconHinten = new javax.swing.JLabel();
        lblUploadIconSonstige = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        lblUploadIcon = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        panLageTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        lblBildTitle1 = new javax.swing.JLabel();
        panStandortKarteBody = new de.cismet.tools.gui.RoundedPanel();
        mappingComponent1 = new de.cismet.cismap.commons.gui.MappingComponent();
        jPanel1 = new javax.swing.JPanel();
        lblGeom = new javax.swing.JLabel();
        if (editable) {
            cbGeom = new DefaultCismapGeometryComboBoxEditor(editable);
        }
        jPanel4 = new javax.swing.JPanel();
        lblStrassenschluessel = new javax.swing.JLabel();
        cbStrassenschluessel = new de.cismet.cids.editors.FastBindableReferenceCombo(
                strassenschluesselSearch,
                strassenschluesselSearch.getRepresentationPattern(),
                strassenschluesselSearch.getRepresentationFields());
        lblStrasse = new javax.swing.JLabel();
        cbStrassenname = new de.cismet.cids.editors.FastBindableReferenceCombo(
                strassennameSearch,
                strassennameSearch.getRepresentationPattern(),
                strassennameSearch.getRepresentationFields());
        jPanel8 = new javax.swing.JPanel();
        panBildTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        lblPreviewTitle = new javax.swing.JLabel();
        btnPreviewUpload = new javax.swing.JButton();
        btnPreviewMaximize = new javax.swing.JButton();
        btnPreviewPrev = new javax.swing.JButton();
        btnPreviewNext = new javax.swing.JButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        panLageBody = new de.cismet.tools.gui.RoundedPanel();
        jPanel10 = new javax.swing.JPanel();
        panPreviewIcon = new javax.swing.JPanel();
        lblPreviewIconVorne = new javax.swing.JLabel();
        lblPreviewIconHinten = new javax.swing.JLabel();
        lblPreviewIconSonstige = new javax.swing.JLabel();
        lblOvPreviewBackwards = new javax.swing.JLabel();
        lblOvPreviewCenter = new javax.swing.JLabel();
        lblOvPreviewForwards = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jxhOVCenter = new org.jdesktop.swingx.JXHyperlink();
        jxhOVFW = new org.jdesktop.swingx.JXHyperlink();
        jxhOVBW = new org.jdesktop.swingx.JXHyperlink();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel3 = new ScrollablePanel(new GridLayout(0, 1, 0, 10));
        jButton3 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        jLabel1 = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        jPanel12 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        panBildTitle1 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblFullTitle = new javax.swing.JLabel();
        btnFullMinimize = new javax.swing.JButton();
        btnFullNext = new javax.swing.JButton();
        btnFullPrev = new javax.swing.JButton();
        btnFullUpload = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        jScrollPane2 = new javax.swing.JScrollPane();
        panFullIcon = new de.cismet.tools.gui.RoundedPanel();
        lblFullIconVorne = new javax.swing.JLabel();
        lblFullIconHinten = new javax.swing.JLabel();
        lblFullIconSonstige = new javax.swing.JLabel();

        panTitle.setOpaque(false);
        panTitle.setLayout(new java.awt.GridBagLayout());

        txtTitle.setFont(new java.awt.Font("DejaVu Sans", 1, 18)); // NOI18N
        txtTitle.setForeground(new java.awt.Color(255, 255, 255));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        panTitle.add(txtTitle, gridBagConstraints);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel3,
            org.openide.util.NbBundle.getMessage(VzkatStandortEditor.class, "VzkatStandortEditor.jLabel3.text")); // NOI18N

        jFileChooser1.setFileFilter(new FileNameExtensionFilter("Bild-Dateien", ALLOWED_EXTENSIONS));

        jDialog1.setResizable(false);
        jDialog1.getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel9.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton1,
            org.openide.util.NbBundle.getMessage(VzkatStandortEditor.class, "VzkatStandortEditor.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        jPanel14.add(jButton1);

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton2,
            org.openide.util.NbBundle.getMessage(VzkatStandortEditor.class, "VzkatStandortEditor.jButton2.text")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton2ActionPerformed(evt);
                }
            });
        jPanel14.add(jButton2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_END;
        jPanel9.add(jPanel14, gridBagConstraints);

        jPanel15.setLayout(new java.awt.GridBagLayout());

        jComboBox1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jComboBox1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel15.add(jComboBox1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel2,
            org.openide.util.NbBundle.getMessage(VzkatStandortEditor.class, "VzkatStandortEditor.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel15.add(jLabel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel15.add(filler2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel9.add(jPanel15, gridBagConstraints);

        jPanel16.setLayout(new java.awt.GridLayout(1, 0));

        panUploadIconOld.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    VzkatStandortEditor.class,
                    "VzkatStandortEditor.panUploadIconOld.border.title"))); // NOI18N
        panUploadIconOld.setMaximumSize(new java.awt.Dimension(320, 320));
        panUploadIconOld.setMinimumSize(new java.awt.Dimension(320, 320));
        panUploadIconOld.setOpaque(false);
        panUploadIconOld.setPreferredSize(new java.awt.Dimension(320, 320));
        panUploadIconOld.setLayout(new java.awt.CardLayout());

        lblUploadIconVorne.setForeground(new java.awt.Color(127, 127, 127));
        lblUploadIconVorne.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblUploadIconVorne,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortEditor.class,
                "VzkatStandortEditor.lblUploadIconVorne.text"));        // NOI18N
        lblUploadIconVorne.setToolTipText(org.openide.util.NbBundle.getMessage(
                VzkatStandortEditor.class,
                "VzkatStandortEditor.lblUploadIconVorne.toolTipText")); // NOI18N
        panUploadIconOld.add(lblUploadIconVorne, "vorne");
        lblPreviewIconVorne.setVisible(false);

        lblUploadIconHinten.setForeground(new java.awt.Color(127, 127, 127));
        lblUploadIconHinten.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblUploadIconHinten,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortEditor.class,
                "VzkatStandortEditor.lblUploadIconHinten.text"));        // NOI18N
        lblUploadIconHinten.setToolTipText(org.openide.util.NbBundle.getMessage(
                VzkatStandortEditor.class,
                "VzkatStandortEditor.lblUploadIconHinten.toolTipText")); // NOI18N
        panUploadIconOld.add(lblUploadIconHinten, "hinten");
        lblPreviewIconHinten.setVisible(false);

        lblUploadIconSonstige.setForeground(new java.awt.Color(127, 127, 127));
        lblUploadIconSonstige.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblUploadIconSonstige,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortEditor.class,
                "VzkatStandortEditor.lblUploadIconSonstige.text"));        // NOI18N
        lblUploadIconSonstige.setToolTipText(org.openide.util.NbBundle.getMessage(
                VzkatStandortEditor.class,
                "VzkatStandortEditor.lblUploadIconSonstige.toolTipText")); // NOI18N
        panUploadIconOld.add(lblUploadIconSonstige, "sonstige");
        lblPreviewIconSonstige.setVisible(false);

        jPanel16.add(panUploadIconOld);

        jPanel18.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    VzkatStandortEditor.class,
                    "VzkatStandortEditor.jPanel18.border.title"))); // NOI18N
        jPanel18.setMaximumSize(new java.awt.Dimension(320, 320));
        jPanel18.setMinimumSize(new java.awt.Dimension(320, 320));
        jPanel18.setPreferredSize(new java.awt.Dimension(320, 320));
        jPanel18.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblUploadIcon,
            org.openide.util.NbBundle.getMessage(VzkatStandortEditor.class, "VzkatStandortEditor.lblUploadIcon.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel18.add(lblUploadIcon, gridBagConstraints);

        jPanel16.add(jPanel18);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel9.add(jPanel16, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel4,
            org.openide.util.NbBundle.getMessage(VzkatStandortEditor.class, "VzkatStandortEditor.jLabel4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel9.add(jLabel4, gridBagConstraints);
        jLabel4.setVisible(false);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jDialog1.getContentPane().add(jPanel9, gridBagConstraints);

        setOpaque(false);
        setLayout(new java.awt.CardLayout());

        jPanel11.setOpaque(false);
        jPanel11.setLayout(new java.awt.GridBagLayout());

        jPanel7.setOpaque(false);
        jPanel7.setLayout(new java.awt.GridBagLayout());

        panLageTitle.setBackground(java.awt.Color.darkGray);
        panLageTitle.setLayout(new java.awt.GridBagLayout());

        lblBildTitle1.setFont(lblBildTitle1.getFont());
        lblBildTitle1.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblBildTitle1,
            org.openide.util.NbBundle.getMessage(VzkatStandortEditor.class, "VzkatStandortEditor.lblBildTitle1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panLageTitle.add(lblBildTitle1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel7.add(panLageTitle, gridBagConstraints);

        panStandortKarteBody.setCurve(0);
        panStandortKarteBody.setLayout(new java.awt.GridBagLayout());

        mappingComponent1.setMaximumSize(new java.awt.Dimension(300, 200));
        mappingComponent1.setMinimumSize(new java.awt.Dimension(300, 200));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panStandortKarteBody.add(mappingComponent1, gridBagConstraints);

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblGeom,
            org.openide.util.NbBundle.getMessage(VzkatStandortEditor.class, "VzkatStandortEditor.lblGeom.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel1.add(lblGeom, gridBagConstraints);

        if (editable) {
            final org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                    org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                    this,
                    org.jdesktop.beansbinding.ELProperty.create("${standortBean.fk_geom}"),
                    cbGeom,
                    org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
            binding.setConverter(editable ? ((DefaultCismapGeometryComboBoxEditor)cbGeom).getConverter() : null);
            bindingGroup.addBinding(binding);

            cbGeom.addActionListener(new java.awt.event.ActionListener() {

                    @Override
                    public void actionPerformed(final java.awt.event.ActionEvent evt) {
                        cbGeomActionPerformed(evt);
                    }
                });
        }
        if (editable) {
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
            jPanel1.add(cbGeom, gridBagConstraints);
        }

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panStandortKarteBody.add(jPanel1, gridBagConstraints);
        jPanel1.setVisible(editable);

        jPanel4.setOpaque(false);
        jPanel4.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblStrassenschluessel,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortEditor.class,
                "VzkatStandortEditor.lblStrassenschluessel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel4.add(lblStrassenschluessel, gridBagConstraints);
        lblStrassenschluessel.setVisible(editable);

        cbStrassenschluessel.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbStrassenschluesselActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel4.add(cbStrassenschluessel, gridBagConstraints);
        cbStrassenschluessel.setVisible(editable);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblStrasse,
            org.openide.util.NbBundle.getMessage(VzkatStandortEditor.class, "VzkatStandortEditor.lblStrasse.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel4.add(lblStrasse, gridBagConstraints);

        cbStrassenname.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbStrassennameActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel4.add(cbStrassenname, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panStandortKarteBody.add(jPanel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel7.add(panStandortKarteBody, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel11.add(jPanel7, gridBagConstraints);

        jPanel8.setOpaque(false);
        jPanel8.setLayout(new java.awt.GridBagLayout());

        panBildTitle.setBackground(java.awt.Color.darkGray);
        panBildTitle.setRequestFocusEnabled(false);
        panBildTitle.setLayout(new java.awt.GridBagLayout());

        lblPreviewTitle.setFont(lblPreviewTitle.getFont());
        lblPreviewTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblPreviewTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblPreviewTitle,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortEditor.class,
                "VzkatStandortEditor.lblPreviewTitle.text"));                              // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBildTitle.add(lblPreviewTitle, gridBagConstraints);
        btnPreviewUpload.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/clientutils/upload.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnPreviewUpload,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortEditor.class,
                "VzkatStandortEditor.btnPreviewUpload.text"));                             // NOI18N
        btnPreviewUpload.setToolTipText(org.openide.util.NbBundle.getMessage(
                VzkatStandortEditor.class,
                "VzkatStandortEditor.btnPreviewUpload.toolTipText"));                      // NOI18N
        btnPreviewUpload.setBorderPainted(false);
        btnPreviewUpload.setContentAreaFilled(false);
        btnPreviewUpload.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPreviewUpload.setMaximumSize(new java.awt.Dimension(16, 16));
        btnPreviewUpload.setMinimumSize(new java.awt.Dimension(16, 16));
        btnPreviewUpload.setPreferredSize(new java.awt.Dimension(16, 16));
        btnPreviewUpload.setRequestFocusEnabled(false);
        btnPreviewUpload.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnPreviewUploadActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 4, 0);
        panBildTitle.add(btnPreviewUpload, gridBagConstraints);

        btnPreviewMaximize.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/clientutils/maximize.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnPreviewMaximize,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortEditor.class,
                "VzkatStandortEditor.btnPreviewMaximize.text"));                             // NOI18N
        btnPreviewMaximize.setToolTipText(org.openide.util.NbBundle.getMessage(
                VzkatStandortEditor.class,
                "VzkatStandortEditor.btnPreviewMaximize.toolTipText"));                      // NOI18N
        btnPreviewMaximize.setBorderPainted(false);
        btnPreviewMaximize.setContentAreaFilled(false);
        btnPreviewMaximize.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPreviewMaximize.setMaximumSize(new java.awt.Dimension(16, 16));
        btnPreviewMaximize.setMinimumSize(new java.awt.Dimension(16, 16));
        btnPreviewMaximize.setPreferredSize(new java.awt.Dimension(16, 16));
        btnPreviewMaximize.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnPreviewMaximizeActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 4, 10);
        panBildTitle.add(btnPreviewMaximize, gridBagConstraints);

        btnPreviewPrev.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/clientutils/left.png")));                                 // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnPreviewPrev,
            org.openide.util.NbBundle.getMessage(VzkatStandortEditor.class, "VzkatStandortEditor.btnPreviewPrev.text")); // NOI18N
        btnPreviewPrev.setBorderPainted(false);
        btnPreviewPrev.setContentAreaFilled(false);
        btnPreviewPrev.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPreviewPrev.setMaximumSize(new java.awt.Dimension(16, 16));
        btnPreviewPrev.setMinimumSize(new java.awt.Dimension(16, 16));
        btnPreviewPrev.setPreferredSize(new java.awt.Dimension(16, 16));
        btnPreviewPrev.setRequestFocusEnabled(false);
        btnPreviewPrev.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnPreviewPrevActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 4, 150);
        panBildTitle.add(btnPreviewPrev, gridBagConstraints);

        btnPreviewNext.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/clientutils/right.png")));                                // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnPreviewNext,
            org.openide.util.NbBundle.getMessage(VzkatStandortEditor.class, "VzkatStandortEditor.btnPreviewNext.text")); // NOI18N
        btnPreviewNext.setToolTipText(org.openide.util.NbBundle.getMessage(
                VzkatStandortEditor.class,
                "VzkatStandortEditor.btnPreviewNext.toolTipText"));                                                      // NOI18N
        btnPreviewNext.setBorderPainted(false);
        btnPreviewNext.setContentAreaFilled(false);
        btnPreviewNext.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPreviewNext.setMaximumSize(new java.awt.Dimension(16, 16));
        btnPreviewNext.setMinimumSize(new java.awt.Dimension(16, 16));
        btnPreviewNext.setPreferredSize(new java.awt.Dimension(16, 16));
        btnPreviewNext.setRequestFocusEnabled(false);
        btnPreviewNext.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnPreviewNextActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 150, 4, 0);
        panBildTitle.add(btnPreviewNext, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panBildTitle.add(filler4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panBildTitle.add(filler5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel8.add(panBildTitle, gridBagConstraints);

        panLageBody.setCurve(0);
        panLageBody.setLayout(new java.awt.GridBagLayout());

        jPanel10.setMaximumSize(new java.awt.Dimension(320, 320));
        jPanel10.setMinimumSize(new java.awt.Dimension(320, 320));
        jPanel10.setOpaque(false);
        jPanel10.setPreferredSize(new java.awt.Dimension(320, 320));
        jPanel10.setLayout(new java.awt.CardLayout());

        panPreviewIcon.setOpaque(false);
        panPreviewIcon.setLayout(new java.awt.CardLayout());

        lblPreviewIconVorne.setForeground(new java.awt.Color(127, 127, 127));
        lblPreviewIconVorne.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblPreviewIconVorne,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortEditor.class,
                "VzkatStandortEditor.lblPreviewIconVorne.text"));        // NOI18N
        lblPreviewIconVorne.setToolTipText(org.openide.util.NbBundle.getMessage(
                VzkatStandortEditor.class,
                "VzkatStandortEditor.lblPreviewIconVorne.toolTipText")); // NOI18N
        panPreviewIcon.add(lblPreviewIconVorne, "vorne");
        lblPreviewIconVorne.setVisible(false);

        lblPreviewIconHinten.setForeground(new java.awt.Color(127, 127, 127));
        lblPreviewIconHinten.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblPreviewIconHinten,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortEditor.class,
                "VzkatStandortEditor.lblPreviewIconHinten.text"));        // NOI18N
        lblPreviewIconHinten.setToolTipText(org.openide.util.NbBundle.getMessage(
                VzkatStandortEditor.class,
                "VzkatStandortEditor.lblPreviewIconHinten.toolTipText")); // NOI18N
        panPreviewIcon.add(lblPreviewIconHinten, "hinten");
        lblPreviewIconHinten.setVisible(false);

        lblPreviewIconSonstige.setForeground(new java.awt.Color(127, 127, 127));
        lblPreviewIconSonstige.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblPreviewIconSonstige,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortEditor.class,
                "VzkatStandortEditor.lblPreviewIconSonstige.text"));        // NOI18N
        lblPreviewIconSonstige.setToolTipText(org.openide.util.NbBundle.getMessage(
                VzkatStandortEditor.class,
                "VzkatStandortEditor.lblPreviewIconSonstige.toolTipText")); // NOI18N
        panPreviewIcon.add(lblPreviewIconSonstige, "sonstige");
        lblPreviewIconSonstige.setVisible(false);

        jPanel10.add(panPreviewIcon, "pics");

        lblOvPreviewBackwards.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblOvPreviewBackwards,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortEditor.class,
                "VzkatStandortEditor.lblOvPreviewBackwards.text")); // NOI18N
        jPanel10.add(lblOvPreviewBackwards, "ovBackwards");

        lblOvPreviewCenter.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblOvPreviewCenter,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortEditor.class,
                "VzkatStandortEditor.lblOvPreviewBackwards.text")); // NOI18N
        jPanel10.add(lblOvPreviewCenter, "ovCenter");

        lblOvPreviewForwards.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblOvPreviewForwards,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortEditor.class,
                "VzkatStandortEditor.lblOvPreviewBackwards.text")); // NOI18N
        jPanel10.add(lblOvPreviewForwards, "ovForwards");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panLageBody.add(jPanel10, gridBagConstraints);

        jPanel6.setOpaque(false);
        jPanel6.setLayout(new java.awt.GridBagLayout());

        jxhOVCenter.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/orbitviewer/orbit22.png")));                           // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jxhOVCenter,
            org.openide.util.NbBundle.getMessage(VzkatStandortEditor.class, "VzkatStandortEditor.jxhOVCenter.text")); // NOI18N
        jxhOVCenter.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseExited(final java.awt.event.MouseEvent evt) {
                    jxhOVCenterMouseExited(evt);
                }
                @Override
                public void mouseEntered(final java.awt.event.MouseEvent evt) {
                    jxhOVCenterMouseEntered(evt);
                }
            });
        jxhOVCenter.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jxhOVCenterActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 15);
        jPanel6.add(jxhOVCenter, gridBagConstraints);

        jxhOVFW.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/orbitviewer/orbit22.png")));                       // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jxhOVFW,
            org.openide.util.NbBundle.getMessage(VzkatStandortEditor.class, "VzkatStandortEditor.jxhOVFW.text")); // NOI18N
        jxhOVFW.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseExited(final java.awt.event.MouseEvent evt) {
                    jxhOVFWMouseExited(evt);
                }
                @Override
                public void mouseEntered(final java.awt.event.MouseEvent evt) {
                    jxhOVFWMouseEntered(evt);
                }
            });
        jxhOVFW.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jxhOVFWActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        jPanel6.add(jxhOVFW, gridBagConstraints);

        jxhOVBW.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/orbitviewer/orbit22.png")));                       // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jxhOVBW,
            org.openide.util.NbBundle.getMessage(VzkatStandortEditor.class, "VzkatStandortEditor.jxhOVBW.text")); // NOI18N
        jxhOVBW.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseExited(final java.awt.event.MouseEvent evt) {
                    jxhOVBWMouseExited(evt);
                }
                @Override
                public void mouseEntered(final java.awt.event.MouseEvent evt) {
                    jxhOVBWMouseEntered(evt);
                }
            });
        jxhOVBW.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jxhOVBWActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel6.add(jxhOVBW, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panLageBody.add(jPanel6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel8.add(panLageBody, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel11.add(jPanel8, gridBagConstraints);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setOpaque(false);
        jScrollPane1.getViewport().setOpaque(false);

        ((ScrollablePanel)jPanel3).setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
        ((ScrollablePanel)jPanel3).setScrollableBlockIncrement(
            ScrollablePanel.VERTICAL,
            ScrollablePanel.IncrementType.PERCENT,
            100);
        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jButton3.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/optionspanels/wunda_blau/add.png")));               // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jButton3,
            org.openide.util.NbBundle.getMessage(VzkatStandortEditor.class, "VzkatStandortEditor.jButton3.text")); // NOI18N
        jButton3.setBorderPainted(false);
        jButton3.setContentAreaFilled(false);
        jButton3.setMaximumSize(new java.awt.Dimension(16, 16));
        jButton3.setMinimumSize(new java.awt.Dimension(16, 16));
        jButton3.setPreferredSize(new java.awt.Dimension(16, 16));
        jButton3.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton3ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(jButton3, gridBagConstraints);
        jButton3.setVisible(isEditable());

        jPanel5.setOpaque(false);
        jPanel5.setLayout(new java.awt.GridLayout(0, 1, 0, 10));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(jPanel5, gridBagConstraints);

        jScrollPane1.setViewportView(jPanel3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel11.add(jScrollPane1, gridBagConstraints);

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jXDatePicker1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jXDatePicker1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        jPanel2.add(jXDatePicker1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(VzkatStandortEditor.class, "VzkatStandortEditor.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel2.add(jLabel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(filler1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel11.add(jPanel2, gridBagConstraints);
        jPanel2.setVisible(isEditable());

        add(jPanel11, "main");

        jPanel12.setOpaque(false);
        jPanel12.setLayout(new java.awt.GridBagLayout());

        jPanel13.setOpaque(false);
        jPanel13.setLayout(new java.awt.GridBagLayout());

        panBildTitle1.setBackground(java.awt.Color.darkGray);
        panBildTitle1.setLayout(new java.awt.GridBagLayout());

        lblFullTitle.setFont(lblFullTitle.getFont());
        lblFullTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblFullTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblFullTitle,
            org.openide.util.NbBundle.getMessage(VzkatStandortEditor.class, "VzkatStandortEditor.lblFullTitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBildTitle1.add(lblFullTitle, gridBagConstraints);

        btnFullMinimize.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/clientutils/minimize.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnFullMinimize,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortEditor.class,
                "VzkatStandortEditor.btnFullMinimize.text"));                                // NOI18N
        btnFullMinimize.setToolTipText(org.openide.util.NbBundle.getMessage(
                VzkatStandortEditor.class,
                "VzkatStandortEditor.btnFullMinimize.toolTipText"));                         // NOI18N
        btnFullMinimize.setBorderPainted(false);
        btnFullMinimize.setContentAreaFilled(false);
        btnFullMinimize.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFullMinimize.setMaximumSize(new java.awt.Dimension(16, 16));
        btnFullMinimize.setMinimumSize(new java.awt.Dimension(16, 16));
        btnFullMinimize.setPreferredSize(new java.awt.Dimension(16, 16));
        btnFullMinimize.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnFullMinimizeActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 4, 10);
        panBildTitle1.add(btnFullMinimize, gridBagConstraints);

        btnFullNext.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/clientutils/right.png")));                             // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnFullNext,
            org.openide.util.NbBundle.getMessage(VzkatStandortEditor.class, "VzkatStandortEditor.btnFullNext.text")); // NOI18N
        btnFullNext.setToolTipText(org.openide.util.NbBundle.getMessage(
                VzkatStandortEditor.class,
                "VzkatStandortEditor.btnFullNext.toolTipText"));                                                      // NOI18N
        btnFullNext.setBorderPainted(false);
        btnFullNext.setContentAreaFilled(false);
        btnFullNext.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFullNext.setMaximumSize(new java.awt.Dimension(16, 16));
        btnFullNext.setMinimumSize(new java.awt.Dimension(16, 16));
        btnFullNext.setPreferredSize(new java.awt.Dimension(16, 16));
        btnFullNext.setRequestFocusEnabled(false);
        btnFullNext.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnFullNextActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 150, 4, 0);
        panBildTitle1.add(btnFullNext, gridBagConstraints);

        btnFullPrev.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/clientutils/left.png")));                              // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnFullPrev,
            org.openide.util.NbBundle.getMessage(VzkatStandortEditor.class, "VzkatStandortEditor.btnFullPrev.text")); // NOI18N
        btnFullPrev.setToolTipText(org.openide.util.NbBundle.getMessage(
                VzkatStandortEditor.class,
                "VzkatStandortEditor.btnFullPrev.toolTipText"));                                                      // NOI18N
        btnFullPrev.setBorderPainted(false);
        btnFullPrev.setContentAreaFilled(false);
        btnFullPrev.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFullPrev.setMaximumSize(new java.awt.Dimension(16, 16));
        btnFullPrev.setMinimumSize(new java.awt.Dimension(16, 16));
        btnFullPrev.setPreferredSize(new java.awt.Dimension(16, 16));
        btnFullPrev.setRequestFocusEnabled(false);
        btnFullPrev.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnFullPrevActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 4, 150);
        panBildTitle1.add(btnFullPrev, gridBagConstraints);

        btnFullUpload.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/clientutils/upload.png")));                              // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnFullUpload,
            org.openide.util.NbBundle.getMessage(VzkatStandortEditor.class, "VzkatStandortEditor.btnFullUpload.text")); // NOI18N
        btnFullUpload.setToolTipText(org.openide.util.NbBundle.getMessage(
                VzkatStandortEditor.class,
                "VzkatStandortEditor.btnFullUpload.toolTipText"));                                                      // NOI18N
        btnFullUpload.setBorderPainted(false);
        btnFullUpload.setContentAreaFilled(false);
        btnFullUpload.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFullUpload.setMaximumSize(new java.awt.Dimension(16, 16));
        btnFullUpload.setMinimumSize(new java.awt.Dimension(16, 16));
        btnFullUpload.setPreferredSize(new java.awt.Dimension(16, 16));
        btnFullUpload.setRequestFocusEnabled(false);
        btnFullUpload.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnFullUploadActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 4, 0);
        panBildTitle1.add(btnFullUpload, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panBildTitle1.add(filler3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panBildTitle1.add(filler6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel13.add(panBildTitle1, gridBagConstraints);

        panFullIcon.setCurve(0);
        panFullIcon.setLayout(new java.awt.CardLayout());

        lblFullIconVorne.setForeground(new java.awt.Color(127, 127, 127));
        lblFullIconVorne.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblFullIconVorne,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortEditor.class,
                "VzkatStandortEditor.lblFullIconVorne.text")); // NOI18N
        panFullIcon.add(lblFullIconVorne, "vorne");
        lblPreviewIconVorne.setVisible(false);

        lblFullIconHinten.setForeground(new java.awt.Color(127, 127, 127));
        lblFullIconHinten.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblFullIconHinten,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortEditor.class,
                "VzkatStandortEditor.lblFullIconHinten.text")); // NOI18N
        panFullIcon.add(lblFullIconHinten, "hinten");
        lblPreviewIconHinten.setVisible(false);

        lblFullIconSonstige.setForeground(new java.awt.Color(127, 127, 127));
        lblFullIconSonstige.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblFullIconSonstige,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortEditor.class,
                "VzkatStandortEditor.lblFullIconSonstige.text")); // NOI18N
        panFullIcon.add(lblFullIconSonstige, "sonstige");
        lblPreviewIconSonstige.setVisible(false);

        jScrollPane2.setViewportView(panFullIcon);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel13.add(jScrollPane2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(jPanel13, gridBagConstraints);

        add(jPanel12, "pics");

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXDatePicker1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jXDatePicker1ActionPerformed
        reloadShilder();
    }                                                                                 //GEN-LAST:event_jXDatePicker1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton3ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton3ActionPerformed
        addSchildPanel(null);
    }                                                                            //GEN-LAST:event_jButton3ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbStrassenschluesselActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbStrassenschluesselActionPerformed
        if (comboboxesInited && cbStrassenschluesselEnabled) {
            synchronized (this) {
                try {
                    cbStrassenschluesselEnabled = false;
                    cbStrassenname.setSelectedItem(cbStrassenschluessel.getSelectedItem());
                    cbStrassennameActionPerformed(null);
                    repaint();
                } finally {
                    cbStrassenschluesselEnabled = true;
                }
            }
        }
        updateSelectedStrassenschluessel();
    }                                                                                        //GEN-LAST:event_cbStrassenschluesselActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbStrassennameActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbStrassennameActionPerformed
        if (comboboxesInited && cbStrassennameEnabled) {
            synchronized (this) {
                try {
                    cbStrassennameEnabled = false;
                    cbStrassenschluessel.setSelectedItem(cbStrassenname.getSelectedItem());
                    cbStrassenschluesselActionPerformed(null);
                    repaint();
                } finally {
                    cbStrassennameEnabled = true;
                }
            }
        }
    }                                                                                  //GEN-LAST:event_cbStrassennameActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbGeomActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbGeomActionPerformed
        if (editable) {
            SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        final Geometry geom = (Geometry)standortBean.getProperty("fk_geom.geo_field");
                        refreshGeomFeatures();
                        new SwingWorker<Void, Void>() {

                            @Override
                            protected Void doInBackground() throws Exception {
                                strassennameSearch.setGeom(geom);
                                cbStrassenname.refreshModel();
                                return null;
                            }
                        }.execute();
                    }
                });
        }
    } //GEN-LAST:event_cbGeomActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxhOVCenterActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jxhOVCenterActionPerformed
        handleViewGeom(OvDirection.CENTER);
    }                                                                               //GEN-LAST:event_jxhOVCenterActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  ovDirection  propertyName DOCUMENT ME!
     */
    private void handleViewGeom(final OvDirection ovDirection) {
        final String propertyName = getOvPropertyName(ovDirection);

        final Point viewpoint = (Point)standortBean.getProperty(propertyName);
        final Point standort = (Point)standortBean.getProperty("fk_geom.geo_field");

        final Geometry currentBB = CismapBroker.getInstance()
                    .getMappingComponent()
                    .getCurrentBoundingBoxFromCamera()
                    .getGeometry(CrsTransformer.extractSridFromCrs(CismapBroker.getInstance().getSrs().getCode()));

        final double h = currentBB.getEnvelopeInternal().getHeight();
        final double w = currentBB.getEnvelopeInternal().getWidth();

        final Point point;
        final double distance;
        final double angle;
        if (viewpoint != null) {
            final XBoundingBox bb = new XBoundingBox(viewpoint.getX() - (w / 2),
                    viewpoint.getY()
                            - (h / 2),
                    viewpoint.getX()
                            + (w / 2),
                    viewpoint.getY()
                            + (h / 2),
                    CismapBroker.getInstance().getSrs().getCode(),
                    true);
            CismapBroker.getInstance().getMappingComponent().gotoBoundingBoxWithHistory(bb);

            distance = viewpoint.distance(standort);
            angle = getAngle(viewpoint, standort);
            point = viewpoint;
        } else {
            distance = 0;
            angle = 0;
            point = standort;
        }

        final double fov = 115;
        final double tilt = -10;
        final double pan = angle;
        final Collection selF = CismapBroker.getInstance()
                    .getMappingComponent()
                    .getFeatureCollection()
                    .getSelectedFeatures();

        // Well the right suffix is always the 4th char of the proprtyName
        // what could possibly go wrong

        final List<String> richtungenQuote = new ArrayList<>();
        for (final String richtung : getRichtungen()) {
            richtungenQuote.add("\"" + richtung + "\"");
        }

        final String additionalInfoString = "{\"enableScreenshotsFor\":" + richtungenQuote.toString() + "}";

        OrbitControlFeature.controlOrAddOnMap(
            point,
            getConnectionContext(),
            fov,
            pan,
            tilt,
            "vzkat.standort."
                    + ((Integer)standortBean.getProperty("import_id")).toString(),
            additionalInfoString);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private List<String> getRichtungen() {
        final List<String> richtungen = new ArrayList<>(3);
        for (final CidsBean schildBean : schildBeans) {
            final String richtung = (String)schildBean.getProperty("fk_richtung.schluessel"); // vorne, hinten, sonst
            if (!richtungen.contains(richtung)) {
                richtungen.add(richtung);
            }
        }
        return richtungen;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   ovDirection  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getOvPropertyName(final OvDirection ovDirection) {
        final String propertyName;
        switch (ovDirection) {
            case BACKWARDS: {
                propertyName = "ov_bw";
            }
            break;
            case CENTER: {
                propertyName = "ov_center";
            }
            break;
            case FORWARDS: {
                propertyName = "ov_fw";
            }
            break;
            default: {
                propertyName = null;
            }
        }
        return propertyName;
    }

    /**
     * DOCUMENT ME!
     */
    private void refreshImageButtons() {
        final Map<CidsBean, List> richtungBeanMap = createRichtungsLists(schildBeans);
        panFullIcon.removeAll();
        panFullIcon.add(lblFullIconVorne);
        panFullIcon.add(lblFullIconHinten);
        panFullIcon.add(lblFullIconSonstige);
        panPreviewIcon.removeAll();
        panPreviewIcon.add(lblPreviewIconVorne);
        panPreviewIcon.add(lblPreviewIconHinten);
        panPreviewIcon.add(lblPreviewIconSonstige);
        if (richtungBeanMap != null) {
            final Set<CidsBean> richtungBeans = richtungBeanMap.keySet();
            boolean vorneExists = false;
            boolean hintenExists = false;
            boolean sonstigeExists = false;
            int count = 0;
            if ((richtungBeans != null) && !richtungBeans.isEmpty()) {
                for (final CidsBean richtungBean : richtungBeans) {
                    if (richtungBean != null) {
                        if ("vorne".equals(richtungBean.getProperty("schluessel"))) {
                            vorneExists = true;
                            count++;
                        } else if ("hinten".equals(richtungBean.getProperty("schluessel"))) {
                            hintenExists = true;
                            count++;
                        } else if ("sonstige".equals(richtungBean.getProperty("schluessel"))) {
                            sonstigeExists = true;
                            count++;
                        }
                    }
                }

                if (!vorneExists) {
                    lblFullIconVorne.setVisible(false);
                    lblPreviewIconVorne.setVisible(false);
                    panFullIcon.remove(lblFullIconVorne);
                    panPreviewIcon.remove(lblPreviewIconVorne);
                }
                if (!hintenExists) {
                    lblFullIconHinten.setVisible(false);
                    lblPreviewIconHinten.setVisible(false);
                    panFullIcon.remove(lblFullIconHinten);
                    panPreviewIcon.remove(lblPreviewIconHinten);
                }
                if (!sonstigeExists) {
                    lblFullIconSonstige.setVisible(false);
                    lblPreviewIconSonstige.setVisible(false);
                    panFullIcon.remove(lblFullIconSonstige);
                    panPreviewIcon.remove(lblPreviewIconSonstige);
                }
            }

            final String first = vorneExists ? "vorne"
                                             : (hintenExists ? "hinten" : (sonstigeExists ? "sonstige" : "vorne"));
            ((CardLayout)panFullIcon.getLayout()).show(panFullIcon, first);
            ((CardLayout)panPreviewIcon.getLayout()).show(panPreviewIcon, first);

            SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        updateRichtung();
                    }
                });

            btnFullNext.setVisible(count > 1);
            btnPreviewNext.setVisible(count > 1);
            btnFullPrev.setVisible(count > 1);
            btnPreviewPrev.setVisible(count > 1);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void loadImages() {
        if (standortBean != null) {
            loadOvPreview(OvDirection.BACKWARDS);
            loadOvPreview(OvDirection.CENTER);
            loadOvPreview(OvDirection.FORWARDS);

            loadImages(Richtung.VORNE);
            loadImages(Richtung.HINTEN);
            loadImages(Richtung.SONSTIGE);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  richtung  richtungSchluessel DOCUMENT ME!
     */
    private void loadImages(final Richtung richtung) {
        if (richtung != null) {
            final Integer standortImportId = (Integer)standortBean.getProperty("import_id");
            final String origFileName = String.format(
                    "ov.image.vzkat.standort.%d.%s.png",
                    standortImportId,
                    richtung.toString());

            final JLabel labelFull;
            final JLabel labelPreview;
            final JLabel labelUpload;
            switch (richtung) {
                case VORNE: {
                    labelFull = lblFullIconVorne;
                    labelPreview = lblPreviewIconVorne;
                    labelUpload = lblUploadIconVorne;
                    break;
                }
                case HINTEN: {
                    labelFull = lblFullIconHinten;
                    labelPreview = lblPreviewIconHinten;
                    labelUpload = lblUploadIconHinten;
                    break;
                }
                case SONSTIGE: {
                    labelFull = lblFullIconSonstige;
                    labelPreview = lblPreviewIconSonstige;
                    labelUpload = lblUploadIconSonstige;
                    break;
                }
                default: {
                    labelFull = null;
                    labelPreview = null;
                    labelUpload = null;
                    break;
                }
            }

            if ((labelFull != null) && (labelPreview != null) && (labelUpload != null)) {
                labelFull.setText("<html><i>Bild wird geladen...");
                labelPreview.setText("<html><i>Bild wird geladen...");
                labelUpload.setText("<html><i>Bild wird geladen...");

                new SwingWorker<Image, Void>() {

                        @Override
                        protected Image doInBackground() throws Exception {
                            final InputStream is = webdavHelper.getFileFromWebDAV(
                                    origFileName,
                                    VzkatProperties.getInstance().getWebdavUploadUrl()
                                            + "/",
                                    getConnectionContext());
                            return ImageIO.read(is);
                        }

                        @Override
                        protected void done() {
                            try {
                                final Image image = get();
                                if (image != null) {
                                    labelPreview.setText("");
                                    labelFull.setText("");
                                    final ImageIcon imageFull = new javax.swing.ImageIcon(image);
                                    final ImageIcon imagePreview = new ImageIcon(resizeTo(imageFull, 320, 320, null));
                                    labelFull.setIcon(imageFull);
                                    labelPreview.setIcon(imagePreview);
                                    labelUpload.setIcon(imagePreview);
                                } else {
                                    labelFull.setText("<html><i>kein Bild verfügbar");
                                    labelPreview.setText("<html><i>kein Bild verfügbar");
                                    labelUpload.setText("<html><i>kein Bild verfügbar");
                                }
                            } catch (final Exception ex) {
                                LOG.error("error while loading preview image " + origFileName, ex);
                                labelFull.setText("<html><i>kein Bild verfügbar");
                                labelPreview.setText("<html><i>kein Bild verfügbar");
                                labelUpload.setText("<html><i>kein Bild verfügbar");
                            }
                        }
                    }.execute();
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  ovDirection  DOCUMENT ME!
     */
    private void loadOvPreview(final OvDirection ovDirection) {
        final String suffix;
        final JLabel label;
        switch (ovDirection) {
            case BACKWARDS: {
                suffix = "b";
                label = lblOvPreviewBackwards;
            }
            break;
            case CENTER: {
                suffix = "c";
                label = lblOvPreviewCenter;
            }
            break;
            case FORWARDS: {
                suffix = "f";
                label = lblOvPreviewForwards;
            }
            break;
            default: {
                return;
            }
        }
        final Integer standortImportId = (Integer)standortBean.getProperty("import_id");
        final String origFileName = String.format("ov.%d.%s.png", standortImportId, suffix);
        final String previewFileName = String.format("ov.preview.%d.%s.png", standortImportId, suffix);

        label.setText("<html><i>Vorschaubild wird geladen...");

        new SwingWorker<Image, Void>() {

                @Override
                protected Image doInBackground() throws Exception {
                    final URL previewUrl = new URL(String.format(
                                "%s/%s",
                                VzkatProperties.getInstance().getOvOverviewUrl(),
                                previewFileName));
                    final URL origUrl = new URL(String.format(
                                "%s/%s",
                                VzkatProperties.getInstance().getOvOverviewUrl(),
                                origFileName));

                    final InputStream is;
                    if (WebAccessManager.getInstance().checkIfURLaccessible(previewUrl)) {
                        is = WebAccessManager.getInstance().doRequest(previewUrl);
                    } else {
                        is = WebAccessManager.getInstance().doRequest(origUrl);
                    }

                    final ImageIcon overview = new javax.swing.ImageIcon(ImageIO.read(is));
                    final ImageIcon watermark = new javax.swing.ImageIcon(
                            getClass().getResource("/de/cismet/cids/custom/orbitviewer/orbit22.png"));
                    return resizeTo(overview, 320, 320, watermark);
                }

                @Override
                protected void done() {
                    try {
                        label.setText("");
                        label.setIcon(new ImageIcon(get()));
                    } catch (final Exception ex) {
                        LOG.error("error while loading ov preview image " + previewFileName, ex);
                        label.setText("<html><i>keine Vorschau verfügbar");
                    }
                }
            }.execute();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   overview   DOCUMENT ME!
     * @param   width      DOCUMENT ME!
     * @param   height     DOCUMENT ME!
     * @param   watermark  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Image resizeTo(final ImageIcon overview, final int width, final int height, final ImageIcon watermark) {
        // create BufferedImage object of same width and height as of original image
        final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);

        final Graphics2D g = (Graphics2D)bufferedImage.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        final int shortest = (overview.getIconWidth() > overview.getIconHeight()) ? overview.getIconHeight()
                                                                                  : overview.getIconWidth();
        g.drawImage(overview.getImage(),
            0,
            0,
            width,
            height,
            (overview.getIconWidth() - shortest)
                    / 2,
            (overview.getIconHeight() - shortest)
                    / 2,
            overview.getIconWidth()
                    - ((overview.getIconWidth() - shortest) / 2),
            overview.getIconHeight()
                    - ((overview.getIconHeight() - shortest) / 2),
            null);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        if (watermark != null) {
            g.drawImage(watermark.getImage(),
                width
                        - watermark.getIconWidth()
                        - 10,
                10,
                watermark.getIconWidth(),
                watermark.getIconHeight(),
                null);
        }

        return bufferedImage;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  ovDirection  DOCUMENT ME!
     * @param  show         DOCUMENT ME!
     */
    private void showOvPreviewImage(final OvDirection ovDirection, final boolean show) {
        if (show) {
            switch (ovDirection) {
                case BACKWARDS: {
                    ((CardLayout)jPanel10.getLayout()).show(jPanel10, "ovBackwards");
                }
                break;
                case CENTER: {
                    ((CardLayout)jPanel10.getLayout()).show(jPanel10, "ovCenter");
                }
                break;
                case FORWARDS: {
                    ((CardLayout)jPanel10.getLayout()).show(jPanel10, "ovForwards");
                }
                break;
            }
        } else {
            ((CardLayout)jPanel10.getLayout()).show(jPanel10, "pics");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxhOVFWActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jxhOVFWActionPerformed
        handleViewGeom(OvDirection.FORWARDS);
    }                                                                           //GEN-LAST:event_jxhOVFWActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param   viewpoint  DOCUMENT ME!
     * @param   standort   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private double getAngle(final Geometry viewpoint, final Geometry standort) {
        final LineSegment ls = new LineSegment(viewpoint.getCoordinate(), standort.getCoordinate());
        final double angle = (Math.toDegrees(ls.angle()) * -1) + 90;
        return angle;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxhOVBWActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jxhOVBWActionPerformed
        handleViewGeom(OvDirection.BACKWARDS);
    }                                                                           //GEN-LAST:event_jxhOVBWActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  ovDirection  propertyName DOCUMENT ME!
     * @param  show         DOCUMENT ME!
     */
    private void showOvPreviewFeature(final OvDirection ovDirection, final boolean show) {
        if (show) {
            final String propertyName = getOvPropertyName(ovDirection);

            final Point viewpoint = (Point)standortBean.getProperty(propertyName);
            if (viewpoint != null) {
                final Point standort = (Point)standortBean.getProperty("fk_geom.geo_field");
                viewPreviewFeature.setGeometry(viewpoint);

                final FeatureAnnotationSymbol fas = new FeatureAnnotationSymbol(OrbitControlFeature.createArcImage(
                            90,
                            90,
                            (int)getAngle(viewpoint, standort),
                            10,
                            60).getImage());

                fas.setSweetSpotX(0.5);
                fas.setSweetSpotY(0.5);

                viewPreviewFeature.setPointAnnotationSymbol(fas);

                mappingComponent1.getFeatureCollection().addFeature(viewPreviewFeature);
                mappingComponent1.refresh();
            }
        } else {
            mappingComponent1.getFeatureCollection().removeFeature(viewPreviewFeature);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxhOVCenterMouseEntered(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_jxhOVCenterMouseEntered
        showOvPreviewFeature(OvDirection.CENTER, true);
        showOvPreviewImage(OvDirection.CENTER, true);
    }                                                                           //GEN-LAST:event_jxhOVCenterMouseEntered

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxhOVCenterMouseExited(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_jxhOVCenterMouseExited
        showOvPreviewFeature(OvDirection.CENTER, false);
        showOvPreviewImage(OvDirection.CENTER, false);
    }                                                                          //GEN-LAST:event_jxhOVCenterMouseExited

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxhOVBWMouseEntered(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_jxhOVBWMouseEntered
        showOvPreviewFeature(OvDirection.BACKWARDS, true);
        showOvPreviewImage(OvDirection.BACKWARDS, true);
    }                                                                       //GEN-LAST:event_jxhOVBWMouseEntered

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxhOVFWMouseEntered(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_jxhOVFWMouseEntered
        showOvPreviewFeature(OvDirection.FORWARDS, true);
        showOvPreviewImage(OvDirection.FORWARDS, true);
    }                                                                       //GEN-LAST:event_jxhOVFWMouseEntered

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxhOVBWMouseExited(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_jxhOVBWMouseExited
        showOvPreviewFeature(OvDirection.BACKWARDS, false);
        showOvPreviewImage(OvDirection.BACKWARDS, false);
    }                                                                      //GEN-LAST:event_jxhOVBWMouseExited

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxhOVFWMouseExited(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_jxhOVFWMouseExited
        showOvPreviewFeature(OvDirection.FORWARDS, false);
        showOvPreviewImage(OvDirection.FORWARDS, false);
    }                                                                      //GEN-LAST:event_jxhOVFWMouseExited

    /**
     * DOCUMENT ME!
     */
    private void uploadButtonClicked() {
        final int status = jFileChooser1.showOpenDialog(StaticSwingTools.getParentFrame(this));

        if (status == JFileChooser.APPROVE_OPTION) {
            final String extension = jFileChooser1.getSelectedFile()
                        .getName()
                        .substring(jFileChooser1.getSelectedFile().getName().lastIndexOf(".") + 1);
            if (Arrays.asList(ALLOWED_EXTENSIONS).contains(extension)) {
                final File file = jFileChooser1.getSelectedFile();
                uploadImageToWebDav(file);
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Diese Datei-Endung ist nicht erlaubt.\n\nFolgende Datei-Endungen werden akzeptiert:\n"
                            + implode(",", ALLOWED_EXTENSIONS),
                    "Unerlaubte Datei-Endung",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  file  DOCUMENT ME!
     */
    private void uploadImageToWebDav(final File file) {
        try {
            uploadFile = file;

            new SwingWorker<Image, Void>() {

                    @Override
                    protected Image doInBackground() throws Exception {
                        return resizeTo(new javax.swing.ImageIcon(ImageIO.read(file)), 320, 320, null);
                    }

                    @Override
                    protected void done() {
                        try {
                            final List<String> richtungen = getRichtungen();
                            jComboBox1.setModel(new DefaultComboBoxModel<>(richtungen.toArray(new String[0])));
                            jComboBox1.setSelectedItem(richtung.toString());
                            jPanel15.setVisible(richtungen.size() > 1);
                            lblUploadIcon.setIcon(new javax.swing.ImageIcon(get()));
                            jDialog1.pack();
                            StaticSwingTools.showDialog(jDialog1);
                        } catch (final Exception ex) {
                            LOG.error(ex, ex);
                        }
                    }
                }.execute();
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void minimizedButtonClicked() {
        ((CardLayout)getLayout()).show(this, "main");
    }

    /**
     * DOCUMENT ME!
     */
    private void maximizedButtonClicked() {
        ((CardLayout)getLayout()).show(this, "pics");
    }

    /**
     * DOCUMENT ME!
     */
    private void previousButtonClicked() {
        ((CardLayout)panFullIcon.getLayout()).previous(panFullIcon);
        ((CardLayout)panPreviewIcon.getLayout()).previous(panPreviewIcon);
        updateRichtung();
    }

    /**
     * DOCUMENT ME!
     */
    private void nextButtonClicked() {
        ((CardLayout)panFullIcon.getLayout()).next(panFullIcon);
        ((CardLayout)panPreviewIcon.getLayout()).next(panPreviewIcon);

        updateRichtung();
    }

    /**
     * DOCUMENT ME!
     */
    private void updateRichtung() {
        if (lblPreviewIconVorne.isVisible() || lblFullIconVorne.isVisible()) {
            richtung = Richtung.VORNE;
        } else if (lblPreviewIconHinten.isVisible() || lblFullIconHinten.isVisible()) {
            richtung = Richtung.HINTEN;
        } else if (lblPreviewIconSonstige.isVisible() || lblPreviewIconSonstige.isVisible()) {
            richtung = Richtung.SONSTIGE;
        } else {
            richtung = null;
        }

        final String title = "Bild" + ((richtung != null) ? (" (" + richtung.toString() + ")") : "");
        lblPreviewTitle.setText(title);
        lblFullTitle.setText(title);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnPreviewMaximizeActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnPreviewMaximizeActionPerformed
        maximizedButtonClicked();
    }                                                                                      //GEN-LAST:event_btnPreviewMaximizeActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnPreviewUploadActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnPreviewUploadActionPerformed
        uploadButtonClicked();
    }                                                                                    //GEN-LAST:event_btnPreviewUploadActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnFullMinimizeActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnFullMinimizeActionPerformed
        minimizedButtonClicked();
    }                                                                                   //GEN-LAST:event_btnFullMinimizeActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnFullUploadActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnFullUploadActionPerformed
        uploadButtonClicked();
    }                                                                                 //GEN-LAST:event_btnFullUploadActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnPreviewNextActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnPreviewNextActionPerformed
        nextButtonClicked();
    }                                                                                  //GEN-LAST:event_btnPreviewNextActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnPreviewPrevActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnPreviewPrevActionPerformed
        previousButtonClicked();
    }                                                                                  //GEN-LAST:event_btnPreviewPrevActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnFullPrevActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnFullPrevActionPerformed
        previousButtonClicked();
    }                                                                               //GEN-LAST:event_btnFullPrevActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnFullNextActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnFullNextActionPerformed
        nextButtonClicked();
    }                                                                               //GEN-LAST:event_btnFullNextActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jComboBox1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jComboBox1ActionPerformed
        ((CardLayout)panUploadIconOld.getLayout()).show(panUploadIconOld, (String)jComboBox1.getSelectedItem());
    }                                                                              //GEN-LAST:event_jComboBox1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        jDialog1.setVisible(false);
    }                                                                            //GEN-LAST:event_jButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton2ActionPerformed
        final Integer standortImportId = (Integer)standortBean.getProperty("import_id");
        final Richtung richtung;
        if (jComboBox1.getSelectedItem() != null) {
            switch ((String)jComboBox1.getSelectedItem()) {
                case "vorne": {
                    richtung = Richtung.VORNE;
                }
                break;
                case "hinten": {
                    richtung = Richtung.HINTEN;
                }
                break;
                case "sonstige": {
                    richtung = Richtung.SONSTIGE;
                }
                break;
                default: {
                    richtung = null;
                }
            }
        } else {
            richtung = null;
        }
        if (richtung != null) {
            final String fileName = String.format(
                    "ov.image.vzkat.standort.%d.%s.png",
                    standortImportId,
                    richtung.toString());
            jButton2.setEnabled(false);
            jLabel4.setVisible(true);

            new SwingWorker<Void, Void>() {

                    @Override
                    protected Void doInBackground() throws Exception {
                        try(final ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                            final BufferedImage bufferedImage = ImageIO.read(uploadFile);
                            ImageIO.write(bufferedImage, "png", os);
                            try(final ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray())) {
                                webdavHelper.uploadFileToWebDAV(
                                    fileName,
                                    is,
                                    VzkatProperties.getInstance().getWebdavUploadUrl()
                                            + "/",
                                    VzkatStandortEditor.this,
                                    getConnectionContext());
                            }
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                        } catch (final Exception ex) {
                            LOG.error(ex, ex);
                        } finally {
                            jLabel4.setVisible(false);
                            jButton2.setEnabled(true);
                            jDialog1.setVisible(false);
                            loadImages(richtung);
                        }
                    }
                }.execute();
        }
    } //GEN-LAST:event_jButton2ActionPerformed

    /**
     * DOCUMENT ME!
     */
    private void reloadShilder() {
        jPanel5.removeAll();
        jPanel5.add(jLabel3);
        jButton3.setEnabled(false);
        final VzkatSchilderSearch schilderSearch = new VzkatSchilderSearch();
        schilderSearch.setStandortId((Integer)standortBean.getProperty("id"));
        schilderSearch.setActiveDate(jXDatePicker1.getDate());
        new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    final Collection<MetaObjectNode> mons = (Collection)SessionManager.getProxy()
                                .customServerSearch(schilderSearch, getConnectionContext());
                    final List<CidsBean> schildBeans = new ArrayList<>();
                    for (final MetaObjectNode mon : mons) {
                        schildBeans.add(SessionManager.getProxy().getMetaObject(
                                mon.getObjectId(),
                                mon.getClassId(),
                                "WUNDA_BLAU",
                                getConnectionContext()).getBean());
                    }

                    if (isEditable()) {
                        redoSchilder(redoReihenfolge(createRichtungsLists(sortByReihenfolge(schildBeans))));
                    } else {
                        redoSchilder(schildBeans);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    refreshSchildPanels();
                    loadImages();
                    refreshImageButtons();
                    jButton3.setEnabled(true);
                }
            }.execute();
    }

    /**
     * DOCUMENT ME!
     */
    public void refreshSchildPanels() {
        try {
            refreshingSchildPanels = true;
            for (final Component component : jPanel5.getComponents()) {
                if (component instanceof VzkatStandortSchildPanel) {
                    ((VzkatStandortSchildPanel)component).dispose();
                }
            }
            jPanel5.removeAll();

            jButton3.setVisible(schildBeans.isEmpty());

            VzkatStandortSchildPanel selectedSchildPanel = null;
            for (final CidsBean schildBean : schildBeans) {
                final VzkatStandortSchildPanel schildPanel = new VzkatStandortSchildPanel(
                        VzkatStandortEditor.this,
                        isEditable());
                schildPanel.initWithConnectionContext(getConnectionContext());
                schildPanel.setCidsBean(schildBean);
                schildPanel.setOpaque(false);
                if (schildBean.equals(selectedSchildBean)) {
                    selectedSchildPanel = schildPanel;
                }
                jPanel5.add(schildPanel);
            }
            if (selectedSchildPanel != null) {
                final VzkatStandortSchildPanel component = selectedSchildPanel;
                component.setSelected(true);

                jScrollPane1.scrollRectToVisible(component.getBounds());
            }

            refreshImageButtons();
        } finally {
            refreshingSchildPanels = false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  panel  DOCUMENT ME!
     */
    public void removeSchildPanel(final VzkatStandortSchildPanel panel) {
        new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    final CidsBean panelBean = panel.getCidsBean();
                    try {
                        panelBean.setProperty("fk_standort", null);
                        deletedSchildBeans.add(panelBean);
                        schildBeans.remove(panelBean);
                    } catch (final Exception ex) {
                        LOG.error(ex, ex);
                    }
                    cidsBean.setArtificialChangeFlag(true);
                    redoSchilder(redoReihenfolge(createRichtungsLists(schildBeans)));
                    return null;
                }

                @Override
                protected void done() {
                    refreshSchildPanels();
                }
            }.execute();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  panel  DOCUMENT ME!
     */
    public void addSchildPanel(final VzkatStandortSchildPanel panel) {
        cidsBean.setArtificialChangeFlag(true);
        new SwingWorker<List<CidsBean>, Void>() {

                @Override
                protected List<CidsBean> doInBackground() throws Exception {
                    final CidsBean newSchildBean = CidsBean.createNewCidsBeanFromTableName(
                            "WUNDA_BLAU",
                            "vzkat_schild",
                            getConnectionContext());
                    newSchildBean.setProperty("fk_standort", standortBean);
                    newSchildBean.setProperty("gueltig_von", new Timestamp(new Date().getTime()));

                    setSelectedSchildBean(newSchildBean);

                    final CidsBean panelBean = (panel != null) ? panel.getCidsBean() : null;

                    final String query =
                        "SELECT (select id from cs_class where table_name ilike 'vzkat_richtung') as class_id, vzkat_richtung.id as id "
                                + "FROM vzkat_richtung "
                                + "WHERE vzkat_richtung.schluessel ilike 'vorne';";
                    if (panelBean != null) {
                        newSchildBean.setProperty("fk_richtung", panelBean.getProperty("fk_richtung"));
                        newSchildBean.setProperty("fk_zeichen", panelBean.getProperty("fk_zeichen"));
                    } else {
                        final CidsBean vorneRichtungBean =
                            SessionManager.getProxy().getMetaObjectByQuery(query, 0, getConnectionContext())[0]
                                    .getBean();
                        newSchildBean.setProperty("fk_richtung", vorneRichtungBean);
                    }

                    final CidsBean richtungBean = (CidsBean)newSchildBean.getProperty("fk_richtung");

                    final Map<CidsBean, List> richtungsLists = createRichtungsLists(schildBeans);
                    if (!richtungsLists.containsKey(richtungBean)) {
                        richtungsLists.put(richtungBean, new ArrayList());
                    }
                    final List<CidsBean> sameRichtungBeans = richtungsLists.get(richtungBean);

                    final int index = (panelBean != null) ? sameRichtungBeans.indexOf(panelBean) : -1;
                    richtungsLists.get(richtungBean).add(index + 1, newSchildBean);

                    redoSchilder(redoReihenfolge(richtungsLists));
                    return null;
                }

                @Override
                protected void done() {
                    refreshSchildPanels();
                    try {
                    } catch (final Exception ex) {
                        LOG.error(ex, ex);
                    }
                }
            }.execute();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   schildBeans  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Map<CidsBean, List> createRichtungsLists(final List<CidsBean> schildBeans) {
        final Map<CidsBean, List> richtungsLists = new HashMap<>();
        for (final CidsBean schildBean : schildBeans) {
            final CidsBean richtungBean = (CidsBean)schildBean.getProperty("fk_richtung");
            if (!richtungsLists.containsKey(richtungBean)) {
                richtungsLists.put(richtungBean, new ArrayList<>());
            }

            final List<CidsBean> richtungSchildBeans = richtungsLists.get(richtungBean);
            richtungSchildBeans.add(schildBean);
        }
        return richtungsLists;
    }

    /**
     * DOCUMENT ME!
     */
    public void richtungUpdate() {
        if (!refreshingSchildPanels) {
            new SwingWorker<Void, Void>() {

                    @Override
                    protected Void doInBackground() throws Exception {
                        redoSchilder(redoReihenfolge(createRichtungsLists(schildBeans)));
                        cidsBean.setArtificialChangeFlag(true);
                        return null;
                    }

                    @Override
                    protected void done() {
                        refreshSchildPanels();
                    }
                }.execute();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   schildBeans  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> sortByReihenfolge(final List<CidsBean> schildBeans) {
        Collections.sort(schildBeans, new Comparator<CidsBean>() {

                @Override
                public int compare(final CidsBean o1, final CidsBean o2) {
                    final Integer f1 = ((o1 != null) && (o1.getProperty("fk_richtung.id") != null))
                        ? (Integer)o1.getProperty("fk_richtung.id") : -1;
                    final Integer r1 = ((o1 != null) && (o1.getProperty("reihenfolge") != null))
                        ? (Integer)o1.getProperty("reihenfolge") : -1;
                    final Integer f2 = ((o2 != null) && (o2.getProperty("fk_richtung.id") != null))
                        ? (Integer)o2.getProperty("fk_richtung.id") : -1;
                    final Integer r2 = ((o2 != null) && (o2.getProperty("reihenfolge") != null))
                        ? (Integer)o2.getProperty("reihenfolge") : -1;
                    return Integer.compare((f1 * 10000) + r1, (f2 * 10000) + r2);
                }
            });
        return schildBeans;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   richtungsLists  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> redoReihenfolge(final Map<CidsBean, List> richtungsLists) {
        for (final CidsBean richtung : richtungsLists.keySet()) {
            int reihenfolge = 1;
            for (final CidsBean schildBean : (List<CidsBean>)richtungsLists.get(richtung)) {
                try {
                    schildBean.setProperty("reihenfolge", reihenfolge++);
                } catch (final Exception ex) {
                    LOG.error(ex, ex);
                }
            }
        }

        final List<CidsBean> newSchildBeans = new ArrayList<>();
        for (final List<CidsBean> richtungBeans : richtungsLists.values()) {
            newSchildBeans.addAll(richtungBeans);
        }
        return newSchildBeans;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  panel  DOCUMENT ME!
     */
    public void upSchildPanel(final VzkatStandortSchildPanel panel) {
        new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    final CidsBean panelBean = panel.getCidsBean();
                    final CidsBean richtungBean = (CidsBean)panelBean.getProperty("fk_richtung");

                    final Map<CidsBean, List> richtungsLists = createRichtungsLists(schildBeans);
                    final List<CidsBean> sameRichtungBeans = richtungsLists.get(richtungBean);
                    final int index = sameRichtungBeans.indexOf(panelBean);
                    if (index > 0) {
                        Collections.swap(sameRichtungBeans, index, index - 1);
                    }
                    redoSchilder(redoReihenfolge(richtungsLists));
                    return null;
                }

                @Override
                protected void done() {
                    refreshSchildPanels();
                }
            }.execute();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  panel  DOCUMENT ME!
     */
    public void downSchildPanel(final VzkatStandortSchildPanel panel) {
        new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    final CidsBean panelBean = panel.getCidsBean();
                    final CidsBean richtungBean = (CidsBean)panelBean.getProperty("fk_richtung");

                    final Map<CidsBean, List> richtungsLists = createRichtungsLists(schildBeans);
                    final List<CidsBean> sameRichtungBeans = richtungsLists.get(richtungBean);
                    final int index = sameRichtungBeans.indexOf(panelBean);
                    if (index < (sameRichtungBeans.size() - 1)) {
                        Collections.swap(sameRichtungBeans, index, index + 1);
                    }
                    redoSchilder(redoReihenfolge(richtungsLists));
                    return null;
                }
                @Override
                protected void done() {
                    refreshSchildPanels();
                }
            }.execute();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  newSchildBeans  DOCUMENT ME!
     */
    private void redoSchilder(final List<CidsBean> newSchildBeans) {
        schildBeans.clear();
        schildBeans.addAll(sortByReihenfolge(newSchildBeans));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  schildBean  DOCUMENT ME!
     */
    protected void setSelectedSchildBean(final CidsBean schildBean) {
        selectedSchildBean = schildBean;
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  standortBean  DOCUMENT ME!
     */
    public void setStandortBean(final CidsBean standortBean) {
        bindingGroup.unbind();

        this.standortBean = standortBean;

        if (standortBean != null) {
            if (MetaObject.NEW == standortBean.getMetaObject().getStatus()) {
                new SwingWorker<Void, Void>() {

                        @Override
                        protected Void doInBackground() throws Exception {
                            final Collection<Integer> result = SessionManager.getProxy()
                                        .customServerSearch(new VzkatStandortNextSchluesselServerSearch(),
                                            getConnectionContext());
                            if ((result != null) && !result.isEmpty()) {
                                final Integer schluessel = result.iterator().next();
                                standortBean.setProperty("import_id", schluessel);
                            }
                            return null;
                        }

                        @Override
                        protected void done() {
                            try {
                                txtTitle.setText(getTitle());
                            } catch (final Exception ex) {
                                LOG.error(ex, ex);
                            }
                        }
                    }.execute();
            } else {
                txtTitle.setText(getTitle());
            }

            bindingGroup.bind();

            refreshStrassenComboboxes();
            refreshGeomFeatures();

            reloadShilder();

            if (standortBean.getProperty("ov_center") == null) {
                jxhOVBW.setVisible(false);
                jxhOVFW.setVisible(false);
            }
        } else {
            txtTitle.setText(null);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getStandortBean() {
        return standortBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        this.cidsBean = cidsBean;

        if ((cidsBean != null)
                    && "vzkat_standort".equalsIgnoreCase((cidsBean.getMetaObject().getMetaClass().getTableName()))) {
            setStandortBean(cidsBean);
        }
    }

    @Override
    public String getTitle() {
        final String standort = String.valueOf(standortBean);
        return String.format("%s", standort);
    }

    @Override
    public void setTitle(final String string) {
    }

    @Override
    public JComponent getTitleComponent() {
        return panTitle;
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    @Override
    public void dispose() {
        cidsBean = null;
        standortBean = null;
        selectedSchildBean = null;
        cbStrassenschluesselEnabled = false;
        cbStrassennameEnabled = false;

        schildBeans.clear();
        deletedSchildBeans.clear();
        refreshSchildPanels();
        refreshGeomFeatures();

        if (cbGeom != null) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).dispose();
        }
    }

    @Override
    public boolean prepareForSave() {
        boolean errorOccured = false;
        for (final CidsBean schildBean : schildBeans) {
            try {
                schildBean.persist(getConnectionContext());
            } catch (final Exception ex) {
                errorOccured = true;
                LOG.error(ex, ex);
            }
        }
        if (errorOccured) {
            return false;
        }
        for (final CidsBean schildBean : deletedSchildBeans) {
            try {
                schildBean.delete();
                schildBean.persist(getConnectionContext());
            } catch (final Exception ex) {
                errorOccured = true;
                LOG.error(ex, ex);
            }
        }
        if (errorOccured) {
            return false;
        }

        if ((standortBean != null) && (MetaObject.NEW == standortBean.getMetaObject().getStatus())) {
            final Integer schluesselBefore = (Integer)standortBean.getProperty("import_id");
            try {
                final Collection<Integer> result = SessionManager.getProxy()
                            .customServerSearch(new VzkatStandortNextSchluesselServerSearch(), getConnectionContext());
                if ((result != null) && !result.isEmpty()) {
                    final Integer schluessel = result.iterator().next();
                    if (schluessel == null) {
                        return false;
                    }
                    if (!schluessel.equals(schluesselBefore)) {
                        // todo warning and asking if continue saving with new schluessel
                    }
                    standortBean.setProperty("import_id", schluessel);
                }
            } catch (final Exception ex) {
                LOG.error(ex, ex);
                return false;
            }
        }
        return true;
    }

    @Override
    public void editorClosed(final EditorClosedEvent ece) {
    }

    /**
     * DOCUMENT ME!
     */
    private void updateSelectedStrassenschluessel() {
        if (comboboxesInited && editable) {
            final CidsBean selectedStrAdrAddresse = (CidsBean)cbStrassenschluessel.getSelectedItem();
            try {
                standortBean.setProperty(
                    "strassenschluessel",
                    (selectedStrAdrAddresse != null)
                        ? (String)selectedStrAdrAddresse.getProperty("schluessel.schluessel") : null);
            } catch (final Exception ex) {
                LOG.fatal(ex, ex);
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void refreshGeomFeatures() {
        mappingComponent1.getFeatureCollection().removeAllFeatures();
        if (standortBean != null) {
            final Geometry geom = (Geometry)standortBean.getProperty("fk_geom.geo_field");
            if (geom != null) {
                if (!mapInitialized) {
                    initMap();
                }
                final StyledFeature dsf = new DefaultStyledFeature();
                dsf.setGeometry(geom);
                mappingComponent1.getFeatureCollection().addFeature(dsf);
                final XBoundingBox box = new XBoundingBox(geom.getEnvelope().buffer(
                            ClientAlkisConf.getInstance().getGeoBuffer()
                                    * 2));
                SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            mappingComponent1.gotoBoundingBoxWithoutHistory(box);
                        }
                    });
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private synchronized void initMap() {
        if ((standortBean != null) && !mapInitialized) {
            final Geometry geom = (Geometry)standortBean.getProperty("fk_geom.geo_field");
            if (geom != null) {
                try {
                    final XBoundingBox box = new XBoundingBox(geom.getEnvelope().buffer(
                                ClientAlkisConf.getInstance().getGeoBuffer()
                                        * 2));

                    final ActiveLayerModel mappingModel = new ActiveLayerModel();
                    mappingModel.setSrs(ClientAlkisConf.getInstance().getSrsService());
                    mappingModel.addHome(new XBoundingBox(
                            box.getX1(),
                            box.getY1(),
                            box.getX2(),
                            box.getY2(),
                            ClientAlkisConf.getInstance().getSrsService(),
                            true));
                    final SimpleWMS swms = new SimpleWMS(new SimpleWmsGetMapUrl(
                                ClientAlkisConf.getInstance().getMapCallString()));
                    swms.setName("Verkehrszeichen");

                    // add the raster layer to the model
                    mappingModel.addLayer(swms);
                    // set the model
                    mappingComponent1.setMappingModel(mappingModel);
                    // interaction mode
                    mappingComponent1.gotoInitialBoundingBox();
                    mappingComponent1.setInteractionMode(MappingComponent.ZOOM);
                    // finally when all configurations are done ...
                    mappingComponent1.unlock();

                    mapInitialized = true;
                } catch (final Exception ex) {
                    LOG.warn("could not init Map !", ex);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void refreshStrassenComboboxes() {
        if (comboboxesInited && (standortBean != null)) {
            synchronized (this) {
                cbStrassennameEnabled = false;
                new SwingWorker<CidsBean, Void>() {

                        @Override
                        protected CidsBean doInBackground() throws Exception {
                            final String strassenschluessel = (String)standortBean.getProperty("strassenschluessel");
                            if (strassenschluessel == null) {
                                return null;
                            }

                            final Geometry geom = (Geometry)standortBean.getProperty("fk_geom.geo_field");
                            if (standortBean != null) {
                                strassennameSearch.setSortDistanceLimit(10);
                                strassennameSearch.setGeom(geom);
                            }
                            cbStrassenname.refreshModel();
                            for (int index = 0; index < cbStrassenschluessel.getModel().getSize(); index++) {
                                final Object element = cbStrassenschluessel.getModel().getElementAt(index);
                                if (element != null) {
                                    if (strassenschluessel.equals(((LightweightMetaObject)element).toString())) {
                                        return ((MetaObject)element).getBean();
                                    }
                                }
                            }
                            return null;
                        }

                        @Override
                        protected void done() {
                            try {
                                final CidsBean strasseBean = get();
                                cbStrassennameEnabled = false;
                                cbStrassenschluessel.setSelectedItem(strasseBean);
                                cbStrassenschluesselActionPerformed(null);
                                repaint();
                            } catch (final Exception ex) {
                                LOG.error(ex, ex);
                            } finally {
                                cbStrassennameEnabled = true;
                            }
                        }
                    }.execute();
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void initComboboxes() {
        new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    if (cbGeom != null) {
                        final MetaClass mcGeom = ClassCacheMultiple.getMetaClass(
                                "WUNDA_BLAU",
                                "geom",
                                getConnectionContext());
                        ((DefaultCismapGeometryComboBoxEditor)cbGeom).setMetaClass(mcGeom);
                    }
                    final MetaClass mcStrAdrStrasse = ClassCacheMultiple.getMetaClass(
                            "WUNDA_BLAU",
                            "str_adr_strasse",
                            getConnectionContext());
                    cbStrassenschluessel.setMetaClass(mcStrAdrStrasse);
                    cbStrassenschluessel.refreshModel();
                    cbStrassenname.setMetaClass(mcStrAdrStrasse);
                    cbStrassenname.refreshModel();
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                    } catch (final Exception ex) {
                        LOG.error(ex, ex);
                    } finally {
                        comboboxesInited = true;
                        refreshStrassenComboboxes();
                    }
                }
            }.execute();
    }
}
