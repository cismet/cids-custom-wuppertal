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

import java.awt.Component;
import java.awt.GridLayout;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.SwingWorker;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.commons.gui.ScrollablePanel;
import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objectrenderer.utils.alkis.ClientAlkisConf;
import de.cismet.cids.custom.orbit.OrbitControlFeature;
import de.cismet.cids.custom.wunda_blau.search.server.StrAdrStrasseLightweightSearch;
import de.cismet.cids.custom.wunda_blau.search.server.VzkatSchilderSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.DefaultFeatureServiceFeature;
import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.features.StyledFeature;
import de.cismet.cismap.commons.featureservice.DefaultLayerProperties;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.gui.piccolo.FeatureAnnotationSymbol;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.TitleComponentProvider;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;

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
    EditorSaveListener {

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

    //~ Instance fields --------------------------------------------------------

    private final StrAdrStrasseLightweightSearch strassennameSearch = new StrAdrStrasseLightweightSearch(
            StrAdrStrasseLightweightSearch.Subject.NAME,
            STRASSENNAME_TOSTRING_TEMPLATE,
            STRASSENNAME_TOSTRING_FIELDS);
    private final StrAdrStrasseLightweightSearch strassenschluesselSearch = new StrAdrStrasseLightweightSearch(
            StrAdrStrasseLightweightSearch.Subject.SCHLUESSEL,
            STRASSENSCHLUESSEL_TOSTRING_TEMPLATE,
            STRASSENSCHLUESSEL_TOSTRING_FIELDS);

    private final boolean editable;
    private ConnectionContext connectionContext;
    private CidsBean cidsBean;
    private CidsBean selectedSchildBean;

    private boolean cbStrassenschluesselEnabled = true;
    private boolean cbStrassennameEnabled = true;
    private boolean comboboxesInited = false;

    private final List<CidsBean> schildBeans = new ArrayList<>();
    private final List<CidsBean> deletedSchildBeans = new ArrayList<>();
    private CidsBean standortBean;

    private DefaultStyledFeature viewPreviewFeature = new DefaultStyledFeature();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cbGeom;
    private de.cismet.cids.editors.FastBindableReferenceCombo cbStrassenname;
    private de.cismet.cids.editors.FastBindableReferenceCombo cbStrassenschluessel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private org.jdesktop.swingx.JXHyperlink jxhOVBW;
    private org.jdesktop.swingx.JXHyperlink jxhOVCenter;
    private org.jdesktop.swingx.JXHyperlink jxhOVFW;
    private javax.swing.JLabel lblBildTitle;
    private javax.swing.JLabel lblBildTitle1;
    private javax.swing.JLabel lblGeom;
    private javax.swing.JLabel lblStrasse;
    private javax.swing.JLabel lblStrassenschluessel;
    private de.cismet.cismap.commons.gui.MappingComponent mappingComponent1;
    private de.cismet.tools.gui.SemiRoundedPanel panBildTitle;
    private de.cismet.tools.gui.RoundedPanel panLageBody;
    private de.cismet.tools.gui.SemiRoundedPanel panLageTitle;
    private de.cismet.tools.gui.RoundedPanel panStandortKarteBody;
    private javax.swing.JPanel panTitle;
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
        jXDatePicker1.setDate(new Date());

        if (!editable) {
            RendererTools.makeReadOnly(cbStrassenschluessel);
            RendererTools.makeReadOnly(cbStrassenname);
        } else {
            StaticSwingTools.decorateWithFixedAutoCompleteDecorator(cbStrassenschluessel);
            StaticSwingTools.decorateWithFixedAutoCompleteDecorator(cbStrassenname);
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
        jPanel7 = new javax.swing.JPanel();
        panLageTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        lblBildTitle1 = new javax.swing.JLabel();
        panStandortKarteBody = new de.cismet.tools.gui.RoundedPanel();
        mappingComponent1 = new de.cismet.cismap.commons.gui.MappingComponent();
        jPanel1 = new javax.swing.JPanel();
        lblGeom = new javax.swing.JLabel();
        cbGeom = new DefaultCismapGeometryComboBoxEditor(editable);
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
        lblBildTitle = new javax.swing.JLabel();
        panLageBody = new de.cismet.tools.gui.RoundedPanel();
        jLabel2 = new javax.swing.JLabel();
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

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel1.add(cbGeom, gridBagConstraints);

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
        add(jPanel7, gridBagConstraints);

        jPanel8.setOpaque(false);
        jPanel8.setLayout(new java.awt.GridBagLayout());

        panBildTitle.setBackground(java.awt.Color.darkGray);
        panBildTitle.setLayout(new java.awt.GridBagLayout());

        lblBildTitle.setFont(lblBildTitle.getFont());
        lblBildTitle.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblBildTitle,
            org.openide.util.NbBundle.getMessage(VzkatStandortEditor.class, "VzkatStandortEditor.lblBildTitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBildTitle.add(lblBildTitle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel8.add(panBildTitle, gridBagConstraints);

        panLageBody.setCurve(0);
        panLageBody.setMaximumSize(new java.awt.Dimension(320, 320));
        panLageBody.setMinimumSize(new java.awt.Dimension(320, 320));
        panLageBody.setPreferredSize(new java.awt.Dimension(320, 320));
        panLageBody.setLayout(new java.awt.GridBagLayout());

        jLabel2.setForeground(new java.awt.Color(127, 127, 127));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel2,
            org.openide.util.NbBundle.getMessage(VzkatStandortEditor.class, "VzkatStandortEditor.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panLageBody.add(jLabel2, gridBagConstraints);

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
        add(jPanel8, gridBagConstraints);

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
        add(jScrollPane1, gridBagConstraints);

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
        add(jPanel2, gridBagConstraints);
        jPanel2.setVisible(false);

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
            refreshGeomFeatures();
            mappingComponent1.zoomToFeatureCollection();
        }
    }                                                                          //GEN-LAST:event_cbGeomActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxhOVCenterActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jxhOVCenterActionPerformed
        handleViewGeom("ov_center");
    }                                                                               //GEN-LAST:event_jxhOVCenterActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  propertyName  DOCUMENT ME!
     */
    private void handleViewGeom(final String propertyName) {
        final Point viewpoint = (Point)standortBean.getProperty(propertyName);
        final Point standort = (Point)standortBean.getProperty("fk_geom.geo_field");

        final Geometry currentBB = CismapBroker.getInstance()
                    .getMappingComponent()
                    .getCurrentBoundingBoxFromCamera()
                    .getGeometry(CrsTransformer.extractSridFromCrs(CismapBroker.getInstance().getSrs().getCode()));

        final double h = currentBB.getEnvelopeInternal().getHeight();
        final double w = currentBB.getEnvelopeInternal().getWidth();

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

        final double distance = viewpoint.distance(standort);
        final double angle = getAngle(viewpoint, standort);

        final double fov = 115;
        final double tilt = -10;
        final double pan = angle;
        final Collection selF = CismapBroker.getInstance()
                    .getMappingComponent()
                    .getFeatureCollection()
                    .getSelectedFeatures();
        OrbitControlFeature.controlOrAddOnMap(viewpoint, getConnectionContext(), fov, pan, tilt);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxhOVFWActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jxhOVFWActionPerformed
        handleViewGeom("ov_fw");
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
        handleViewGeom("ov_bw");
    }                                                                           //GEN-LAST:event_jxhOVBWActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  propertyName  DOCUMENT ME!
     */
    private void addPreviewFeature(final String propertyName) {
        final Point viewpoint = (Point)standortBean.getProperty(propertyName);
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
    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxhOVCenterMouseEntered(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_jxhOVCenterMouseEntered

        addPreviewFeature("ov_center");
    } //GEN-LAST:event_jxhOVCenterMouseEntered

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxhOVCenterMouseExited(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_jxhOVCenterMouseExited
        mappingComponent1.getFeatureCollection().removeFeature(viewPreviewFeature);
    }                                                                          //GEN-LAST:event_jxhOVCenterMouseExited

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxhOVBWMouseEntered(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_jxhOVBWMouseEntered
        addPreviewFeature("ov_bw");
    }                                                                       //GEN-LAST:event_jxhOVBWMouseEntered

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxhOVFWMouseEntered(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_jxhOVFWMouseEntered
        addPreviewFeature("ov_fw");
    }                                                                       //GEN-LAST:event_jxhOVFWMouseEntered

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxhOVBWMouseExited(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_jxhOVBWMouseExited
        mappingComponent1.getFeatureCollection().removeFeature(viewPreviewFeature);
    }                                                                      //GEN-LAST:event_jxhOVBWMouseExited

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxhOVFWMouseExited(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_jxhOVFWMouseExited
        mappingComponent1.getFeatureCollection().removeFeature(viewPreviewFeature);
    }                                                                      //GEN-LAST:event_jxhOVFWMouseExited

    /**
     * DOCUMENT ME!
     */
    private void reloadShilder() {
        jPanel5.removeAll();
        jPanel5.add(jLabel3);
        jButton3.setEnabled(false);
        final VzkatSchilderSearch schilderSearch = new VzkatSchilderSearch();
        schilderSearch.setStandortId((Integer)standortBean.getProperty("id"));
        schilderSearch.setActiveTimestamp((jXDatePicker1.getDate() != null)
                ? new Timestamp(jXDatePicker1.getDate().getTime() + (1000 * 60 * 60 * 24) - 1) : null);
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
                    jButton3.setEnabled(true);
                }
            }.execute();
    }

    /**
     * DOCUMENT ME!
     */
    public void refreshSchildPanels() {
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

        txtTitle.setText((standortBean != null) ? getTitle() : null);

        if (standortBean != null) {
            bindingGroup.bind();

            initMap();
            refreshStrassenComboboxes();
            refreshGeomFeatures();

            reloadShilder();
            if (standortBean.getProperty("ov_center") == null) {
                jxhOVCenter.setEnabled(false);
            }
            if (standortBean.getProperty("ov_bw") == null) {
                jxhOVBW.setEnabled(false);
            }
            if (standortBean.getProperty("ov_fw") == null) {
                jxhOVFW.setEnabled(false);
            }
        }

//        new SwingWorker<Image, Object>() {
//
//                @Override
//                protected Image doInBackground() throws Exception {
//                    final URL bildURL = new URL(
//                            "https://i.pinimg.com/236x/b7/95/67/b79567b505f3101a8a58f1d0f6d10687.jpg");
//                    final BufferedImage originalBild = ImageIO.read(WebAccessManager.getInstance().doRequest(bildURL));
//                    final int bildZielBreite = (originalBild.getWidth() > originalBild.getHeight()) ? -1 : 250;
//                    final int bildZielHoehe = (originalBild.getWidth() > originalBild.getHeight()) ? 250 : -1;
//                    final Image skaliertesBild = originalBild.getScaledInstance(
//                            bildZielBreite,
//                            bildZielHoehe,
//                            Image.SCALE_SMOOTH);
//                    return skaliertesBild;
//                }
//
//                @Override
//                protected void done() {
//                    final Image skaliertesBild;
//                    try {
//                        skaliertesBild = get();
//                        jLabel2.setIcon(new ImageIcon(skaliertesBild));
//                    } catch (final Exception ex) {
//                        LOG.error("Bild konnte nicht geladen werden", ex);
//                    }
//                }
//            }.execute();

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

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Collection<VzkatStandortSchildPanel> getSchildPanels() {
        final Collection<VzkatStandortSchildPanel> vzkatSchildPanel = new ArrayList<>();
        for (final Component component : jPanel5.getComponents()) {
            if (component instanceof VzkatStandortSchildPanel) {
                vzkatSchildPanel.add((VzkatStandortSchildPanel)component);
            }
        }
        return vzkatSchildPanel;
    }

    @Override
    public void dispose() {
        ((DefaultCismapGeometryComboBoxEditor)cbGeom).dispose();
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
        for (final CidsBean schildBean : deletedSchildBeans) {
            try {
                schildBean.delete();
                schildBean.persist(getConnectionContext());
            } catch (final Exception ex) {
                errorOccured = true;
                LOG.error(ex, ex);
            }
        }
        return !errorOccured;
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
                    (selectedStrAdrAddresse != null) ? (String)selectedStrAdrAddresse.getProperty("strasse") : null);
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
                final StyledFeature dsf = new DefaultStyledFeature();
                dsf.setGeometry(geom);
                mappingComponent1.getFeatureCollection().addFeature(dsf);
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void initMap() {
        if (standortBean != null) {
            final Geometry geom = (Geometry)standortBean.getProperty("fk_geom.geo_field");
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
            } catch (final Exception ex) {
                LOG.warn("could not init Map !", ex);
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
                    final MetaClass mcGeom = ClassCacheMultiple.getMetaClass(
                            "WUNDA_BLAU",
                            "geom",
                            getConnectionContext());
                    ((DefaultCismapGeometryComboBoxEditor)cbGeom).setMetaClass(mcGeom);
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
