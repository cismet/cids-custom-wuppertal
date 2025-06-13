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
package de.cismet.cids.custom.objecteditors.wunda_blau;

import Sirius.navigator.ui.ComponentRegistry;
import Sirius.navigator.ui.RequestsFullSizeComponent;

import Sirius.server.middleware.types.MetaObject;

import com.vividsolutions.jts.geom.Geometry;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import org.apache.log4j.Logger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;

import de.cismet.cids.custom.objectrenderer.utils.AlphanumComparator;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.objectrenderer.utils.alkis.ClientAlkisConf;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.Disposable;

import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.FeatureGroups;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

import de.cismet.cismap.navigatorplugin.CidsFeature;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.collections.TypeSafeCollections;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   verkennisr
 * @version  $Revision$, $Date$
 */
public abstract class BodenAbstractEditor extends javax.swing.JPanel implements CidsBeanRenderer,
    EditorSaveListener,
    RequestsFullSizeComponent,
    Disposable,
    ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(BodenAbstractEditor.class);
    private static Color HIGHLIGHTING = new Color(250, 105, 0);

    //~ Instance fields --------------------------------------------------------

    final Collection<Feature> editorSupportingFeatures = new ArrayList<>();
    private Collection<MetaObject> allSelectedObjects;
    private final boolean editable;
    private final Collection<JComponent> editableComponents;
    private FlurstueckSelectionDialoge fsDialoge;
    private MappingComponent map;
    private String title;
    private CidsBean cidsBean;
    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField aktenzeichen;
    private javax.swing.JButton btnAddFlurstueck;
    private javax.swing.JButton btnRemoveFlurstueck;
    private javax.swing.JComboBox cbHinweisGeom;
    private de.cismet.cids.editors.DefaultBindableDateChooser defaultBindableDateChooser1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblFlInMap;
    private javax.swing.JLabel lblHeadFlurstuecke;
    private javax.swing.JLabel lblHinweisGeometrie;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JList lstFlurstuecke;
    private javax.swing.JPanel panFlurstuecke;
    private javax.swing.JPanel panMap;
    private de.cismet.tools.gui.RoundedPanel rpFlurstuecke;
    private de.cismet.tools.gui.RoundedPanel rpInfo;
    private javax.swing.JScrollPane scpFlurstuecke;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel1;
    private de.cismet.cids.editors.converters.SqlDateToUtilDateConverter sqlDateToUtilDateConverter;
    private de.cismet.tools.gui.SemiRoundedPanel srpHeadFlurstuecke;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form Boden_VorkaufsrechtEditorPanel.
     */
    public BodenAbstractEditor() {
        this(true);
    }

    /**
     * Creates new form Boden_VorkaufsrechtEditorPanel.
     *
     * @param  editable  DOCUMENT ME!
     */
    public BodenAbstractEditor(final boolean editable) {
        this.editable = editable;
        this.editableComponents = new ArrayList<>();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        initComponents();
        initEditableComponents();

        map = new MappingComponent();
        if (!editable) {
            panMap.add(map, BorderLayout.CENTER);
            lblHinweisGeometrie.setVisible(false);
            cbHinweisGeom.setVisible(false);
        } else {
            this.remove(panMap);
            ((DefaultCismapGeometryComboBoxEditor)cbHinweisGeom).setLocalRenderFeatureString("hinweisgeometrie");
        }
        fsDialoge = new FlurstueckSelectionDialoge(getConnectionContext());
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public abstract String getTitleDefaultValue();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getFlurstueckReferenzPropertyName() {
        return "fs_referenz";
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getFlurstueckArrayPropertyName() {
        return "ref_flurstuecke";
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getFlurstueckReferenzGeometryPropertyName() {
        return "fs_referenz.umschreibendes_rechteck";
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

        sqlDateToUtilDateConverter = new de.cismet.cids.editors.converters.SqlDateToUtilDateConverter();
        rpInfo = new de.cismet.tools.gui.RoundedPanel();
        semiRoundedPanel1 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblInfo = new javax.swing.JLabel();
        lblFlInMap = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblHinweisGeometrie = new javax.swing.JLabel();
        aktenzeichen = new javax.swing.JTextField();
        defaultBindableDateChooser1 = new de.cismet.cids.editors.DefaultBindableDateChooser();
        if (editable) {
            cbHinweisGeom = new DefaultCismapGeometryComboBoxEditor();
        } else {
            cbHinweisGeom = new JComboBox();
        }
        jLabel5 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        rpFlurstuecke = new de.cismet.tools.gui.RoundedPanel();
        srpHeadFlurstuecke = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeadFlurstuecke = new javax.swing.JLabel();
        scpFlurstuecke = new javax.swing.JScrollPane();
        lstFlurstuecke = new javax.swing.JList();
        panFlurstuecke = new javax.swing.JPanel();
        btnAddFlurstueck = new javax.swing.JButton();
        btnRemoveFlurstueck = new javax.swing.JButton();
        panMap = new javax.swing.JPanel();

        setMaximumSize(new java.awt.Dimension(5000, 5000));
        setMinimumSize(new java.awt.Dimension(500, 440));
        setName(""); // NOI18N
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(500, 440));
        setLayout(new java.awt.GridBagLayout());

        rpInfo.setMaximumSize(null);
        rpInfo.setMinimumSize(new java.awt.Dimension(500, 150));
        rpInfo.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel1.setBackground(new java.awt.Color(243, 134, 48));
        semiRoundedPanel1.setForeground(new java.awt.Color(255, 255, 0));
        semiRoundedPanel1.setMaximumSize(new java.awt.Dimension(5000, 5000));
        semiRoundedPanel1.setMinimumSize(new java.awt.Dimension(500, 30));
        semiRoundedPanel1.setName("xxx"); // NOI18N
        semiRoundedPanel1.setPreferredSize(new java.awt.Dimension(500, 30));
        semiRoundedPanel1.setLayout(new java.awt.GridBagLayout());

        lblInfo.setBackground(new java.awt.Color(255, 155, 51));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblInfo,
            org.openide.util.NbBundle.getMessage(BodenAbstractEditor.class, "BodenAbstractEditor.lblInfo.text")); // NOI18N
        lblInfo.setMaximumSize(new java.awt.Dimension(50, 50));
        lblInfo.setMinimumSize(new java.awt.Dimension(50, 50));
        lblInfo.setPreferredSize(new java.awt.Dimension(50, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        semiRoundedPanel1.add(lblInfo, gridBagConstraints);

        lblFlInMap.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFlInMap.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/zoom-best-fit.png"))); // NOI18N
        lblFlInMap.setToolTipText(org.openide.util.NbBundle.getMessage(
                BodenAbstractEditor.class,
                "BodenAbstractEditor.lblFlInMap.toolTipText"));                                      // NOI18N
        lblFlInMap.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblFlINMapMouseClicked(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        semiRoundedPanel1.add(lblFlInMap, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        rpInfo.add(semiRoundedPanel1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel3,
            org.openide.util.NbBundle.getMessage(BodenAbstractEditor.class, "BodenAbstractEditor.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        rpInfo.add(jLabel3, gridBagConstraints);
        jLabel3.getAccessibleContext()
                .setAccessibleName(org.openide.util.NbBundle.getMessage(
                        BodenAbstractEditor.class,
                        "BodenAbstractEditor.jLabel3.AccessibleContext.accessibleName"));                         // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(
            lblHinweisGeometrie,
            org.openide.util.NbBundle.getMessage(
                BodenAbstractEditor.class,
                "BodenAbstractEditor.lblHinweisGeometrie.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        rpInfo.add(lblHinweisGeometrie, gridBagConstraints);

        aktenzeichen.setToolTipText(org.openide.util.NbBundle.getMessage(
                BodenAbstractEditor.class,
                "BodenAbstractEditor.aktenzeichen.toolTipText")); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.name}"),
                aktenzeichen,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        rpInfo.add(aktenzeichen, gridBagConstraints);

        defaultBindableDateChooser1.setToolTipText(org.openide.util.NbBundle.getMessage(
                BodenAbstractEditor.class,
                "BodenAbstractEditor.defaultBindableDateChooser1.toolTipText")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.eingang_anfrage}"),
                defaultBindableDateChooser1,
                org.jdesktop.beansbinding.BeanProperty.create("date"),
                "");
        binding.setConverter(sqlDateToUtilDateConverter);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        rpInfo.add(defaultBindableDateChooser1, gridBagConstraints);

        if (editable) {
            binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                    org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                    this,
                    org.jdesktop.beansbinding.ELProperty.create("${cidsBean.hinweisgeometrie}"),
                    cbHinweisGeom,
                    org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbHinweisGeom).getConverter());
            bindingGroup.addBinding(binding);
        }
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        rpInfo.add(cbHinweisGeom, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel5,
            org.openide.util.NbBundle.getMessage(BodenAbstractEditor.class, "BodenAbstractEditor.jLabel5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        rpInfo.add(jLabel5, gridBagConstraints);

        jPanel1.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        rpInfo.add(jPanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        add(rpInfo, gridBagConstraints);
        rpInfo.getAccessibleContext()
                .setAccessibleName(org.openide.util.NbBundle.getMessage(
                        BodenAbstractEditor.class,
                        "BodenAbstractEditor.rpInfo.AccessibleContext.accessibleName")); // NOI18N

        rpFlurstuecke.setMinimumSize(new java.awt.Dimension(150, 350));
        rpFlurstuecke.setPreferredSize(new java.awt.Dimension(150, 350));
        rpFlurstuecke.setLayout(new java.awt.GridBagLayout());

        srpHeadFlurstuecke.setBackground(new java.awt.Color(243, 134, 48));
        srpHeadFlurstuecke.setMaximumSize(new java.awt.Dimension(200, 2000));
        srpHeadFlurstuecke.setMinimumSize(new java.awt.Dimension(150, 20));
        srpHeadFlurstuecke.setPreferredSize(new java.awt.Dimension(150, 20));
        srpHeadFlurstuecke.setRequestFocusEnabled(false);
        srpHeadFlurstuecke.setLayout(new java.awt.GridBagLayout());

        lblHeadFlurstuecke.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblHeadFlurstuecke,
            org.openide.util.NbBundle.getMessage(
                BodenAbstractEditor.class,
                "BodenAbstractEditor.lblHeadFlurstuecke.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        srpHeadFlurstuecke.add(lblHeadFlurstuecke, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        rpFlurstuecke.add(srpHeadFlurstuecke, gridBagConstraints);

        scpFlurstuecke.setBorder(null);
        scpFlurstuecke.setMaximumSize(new java.awt.Dimension(150, 300));
        scpFlurstuecke.setMinimumSize(new java.awt.Dimension(150, 200));
        scpFlurstuecke.setOpaque(false);
        scpFlurstuecke.setPreferredSize(new java.awt.Dimension(150, 300));

        lstFlurstuecke.setModel(new javax.swing.AbstractListModel() {

                String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };

                @Override
                public int getSize() {
                    return strings.length;
                }
                @Override
                public Object getElementAt(final int i) {
                    return strings[i];
                }
            });

        final org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create(
                "${cidsBean.ref_flurstuecke}");
        final org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings
                    .createJListBinding(
                        org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                        this,
                        eLProperty,
                        lstFlurstuecke);
        jListBinding.setSourceNullValue(null);
        jListBinding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(jListBinding);

        lstFlurstuecke.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lstFlurstueckeMouseClicked(evt);
                }
            });
        lstFlurstuecke.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

                @Override
                public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
                    lstFlurstueckeValueChanged(evt);
                }
            });
        scpFlurstuecke.setViewportView(lstFlurstuecke);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        rpFlurstuecke.add(scpFlurstuecke, gridBagConstraints);

        panFlurstuecke.setMinimumSize(new java.awt.Dimension(50, 25));
        panFlurstuecke.setLayout(new java.awt.GridBagLayout());

        btnAddFlurstueck.setBackground(java.awt.Color.green);
        btnAddFlurstueck.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddFlurstueck.setToolTipText(org.openide.util.NbBundle.getMessage(
                BodenAbstractEditor.class,
                "BodenAbstractEditor.btnAddFlurstueck.toolTipText"));                                          // NOI18N
        btnAddFlurstueck.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddFlurstueckActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panFlurstuecke.add(btnAddFlurstueck, gridBagConstraints);

        btnRemoveFlurstueck.setBackground(java.awt.Color.red);
        btnRemoveFlurstueck.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveFlurstueck.setToolTipText(org.openide.util.NbBundle.getMessage(
                BodenAbstractEditor.class,
                "BodenAbstractEditor.btnRemoveFlurstueck.toolTipText"));                                          // NOI18N
        btnRemoveFlurstueck.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemoveFlurstueckActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panFlurstuecke.add(btnRemoveFlurstueck, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        rpFlurstuecke.add(panFlurstuecke, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        add(rpFlurstuecke, gridBagConstraints);

        panMap.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 2, 5);
        add(panMap, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public final void setTitle(final String title) {
        if (title == null) {
            this.title = getTitleDefaultValue() + ": ";
        } else {
            this.title = title + String.valueOf(cidsBean);
        }
    }

    @Override
    public void editorClosed(final EditorClosedEvent ece) {
    }

    @Override
    public boolean prepareForSave() {
        try {
            final ArrayList<String> errors = new ArrayList<String>();
            final String aktenzeichen = (String)cidsBean.getProperty("name");

            if ((aktenzeichen == null) || aktenzeichen.trim().equals("")) {
                errors.add("Aktenzeichen muss eingegeben werden!\n");
            }

            if (aktenzeichen != null) {
                if (aktenzeichen.length() > 20) {
                    errors.add("Aktenzeichen darf maximal 20 Zeichen lang sein!\n");
                }
            }

            if ((cidsBean.getProperty("eingang_anfrage") == null)) {
                errors.add("Das Feld Antragsdatum muss ausgefüllt sein!\n");
            }

            if (errors.size() > 0) {
                String errorOutput = "";
                for (final String s : errors) {
                    errorOutput += s + "\n";
                }
                errorOutput = errorOutput.substring(0, errorOutput.length() - 1);
                JOptionPane.showMessageDialog(
                    StaticSwingTools.getParentFrame(this),
                    errorOutput,
                    "Fehler aufgetreten",
                    JOptionPane.WARNING_MESSAGE);
                return false;
            }

            return true;
        } catch (Exception ex) {
            ObjectRendererUtils.showExceptionWindowToUser("Fehler beim Speichern", ex, this);
            throw new RuntimeException(ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddFlurstueckActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddFlurstueckActionPerformed

        fsDialoge.setCurrentListToAdd(CidsBeanSupport.getBeanCollectionFromProperty(
                cidsBean,
                "ref_flurstuecke"));
        fsDialoge.setTitle("Flurstück hinzufügen");

        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(this),
            fsDialoge,
            true);
    } //GEN-LAST:event_btnAddFlurstueckActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveFlurstueckActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemoveFlurstueckActionPerformed
        final Object[] selection = lstFlurstuecke.getSelectedValues();
        if ((selection != null) && (selection.length > 0)) {
            final int answer = JOptionPane.showConfirmDialog(
                    StaticSwingTools.getParentFrame(this),
                    "Soll das Flurstück wirklich gelöscht werden?",
                    "Flurstück entfernen",
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                final Collection flurstueckCol = CidsBeanSupport.getBeanCollectionFromProperty(
                        cidsBean,
                        "ref_flurstuecke");
                if (flurstueckCol != null) {
                    for (final Object cur : selection) {
                        try {
                            flurstueckCol.remove(cur);
                        } catch (Exception e) {
                            ObjectRendererUtils.showExceptionWindowToUser("Fehler beim Löschen", e, this);
                        }
                    }
                }
            }
        }
    }                                                                                       //GEN-LAST:event_btnRemoveFlurstueckActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstFlurstueckeMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lstFlurstueckeMouseClicked
        if (!editable) {
            if (evt.getClickCount() > 1) {
                handleJumpToListeSelectionBean(lstFlurstuecke);
            } else {
                lstFlurstueckeValueChanged(null);
            }
        }
    }                                                                              //GEN-LAST:event_lstFlurstueckeMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblFlINMapMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblFlINMapMouseClicked
        ObjectRendererUtils.switchToCismapMap();
        ObjectRendererUtils.addBeanGeomsAsFeaturesToCismapMap(allSelectedObjects, editable);
    }                                                                          //GEN-LAST:event_lblFlINMapMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstFlurstueckeValueChanged(final javax.swing.event.ListSelectionEvent evt) { //GEN-FIRST:event_lstFlurstueckeValueChanged
        if (!editable) {
            final Object selectedObj = lstFlurstuecke.getSelectedValue();
            if (selectedObj instanceof CidsBean) {
                final CidsFeature f = new CidsFeature(((CidsBean)selectedObj).getMetaObject());
                map.highlightFeature(f, 1000, HIGHLIGHTING);
            }
        }
    }                                                                                         //GEN-LAST:event_lstFlurstueckeValueChanged

    /**
     * DOCUMENT ME!
     *
     * @param  list  DOCUMENT ME!
     */
    private void handleJumpToListeSelectionBean(final JList list) {
        final Object selectedObj = list.getSelectedValue();
        if (selectedObj instanceof CidsBean) {
            final Object realFSBean = ((CidsBean)selectedObj).getProperty(getFlurstueckReferenzPropertyName());
            if (realFSBean instanceof CidsBean) {
                final MetaObject selMO = ((CidsBean)realFSBean).getMetaObject();
                ComponentRegistry.getRegistry().getDescriptionPane().gotoMetaObject(selMO, "");
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  selection  DOCUMENT ME!
     */
    public void setAllSelectedMetaObjects(final Collection<MetaObject> selection) {
        this.allSelectedObjects = selection;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        if (cidsBean != null) {
            this.cidsBean = cidsBean;
            this.setTitle(getTitleDefaultValue() + ": ");
        }
        try {
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                this.cidsBean,
                getConnectionContext());
            bindingGroup.unbind();
            if (cidsBean != null) {
                final int[] flstIdx = lstFlurstuecke.getSelectedIndices();
                final Collection<MetaObject> selObj = new ArrayList<>(1);
                selObj.add(cidsBean.getMetaObject());
                setAllSelectedMetaObjects(selObj);
                this.cidsBean = cidsBean;
                List<CidsBean> flurstueckeCol = CidsBeanSupport.getBeanCollectionFromProperty(
                        cidsBean,
                        getFlurstueckArrayPropertyName());
                Collections.sort(flurstueckeCol, AlphanumComparator.getInstance());
                flurstueckeCol = CidsBeanSupport.getBeanCollectionFromProperty(
                        cidsBean,
                        getFlurstueckArrayPropertyName());
                Collections.sort(flurstueckeCol, AlphanumComparator.getInstance());

                lstFlurstuecke.setSelectedIndices(flstIdx);
                cidsBean.getMetaObject().getDebugString();
                if (!editable) {
                    initMap();
                } else {
                    editorSupportingFeatures.clear();
                    for (final CidsBean fstck : flurstueckeCol) {
                        final CidsFeature cf = new CidsFeature(fstck.getMetaObject());
                        editorSupportingFeatures.add(cf);
                    }
                    CismapBroker.getInstance()
                            .getMappingComponent()
                            .getFeatureCollection()
                            .addFeatures(editorSupportingFeatures);
                    CismapBroker.getInstance()
                            .getMappingComponent()
                            .zoomToAFeatureCollection(editorSupportingFeatures, false, false);
                }
                bindingGroup.bind();
            }
        } catch (final Exception ex) {
            LOG.error("cannot initialise Boden...Editor", ex); // NOI18N
        }
    }

    @Override
    public void dispose() {
        fsDialoge.dispose();
        map.dispose();

        if (cbHinweisGeom instanceof DefaultCismapGeometryComboBoxEditor) {
            ((DefaultCismapGeometryComboBoxEditor)(cbHinweisGeom)).dispose();
        }

        if (editable) {
            CismapBroker.getInstance()
                    .getMappingComponent()
                    .getFeatureCollection()
                    .removeFeatures(editorSupportingFeatures);
        }
        bindingGroup.unbind();
    }

    /**
     * DOCUMENT ME!
     */
    private void initEditableComponents() {
        editableComponents.add(aktenzeichen);
        editableComponents.add(defaultBindableDateChooser1);

        for (final JComponent editableComponent : editableComponents) {
            editableComponent.setOpaque(editable);
            if (!editable) {
                panFlurstuecke.setVisible(false);
                aktenzeichen.setEditable(false);
                defaultBindableDateChooser1.setEnabled(false);
                defaultBindableDateChooser1.getEditor().setDisabledTextColor(Color.BLACK);
                defaultBindableDateChooser1.getEditor().setOpaque(false);
                defaultBindableDateChooser1.getEditor().setBorder(null);
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void initMap() {
        final List<CidsBean> flurstuecke = CidsBeanSupport.getBeanCollectionFromProperty(
                cidsBean,
                getFlurstueckArrayPropertyName());
        if ((flurstuecke != null) && (flurstuecke.size() > 0)) {
            final Object geoObj = flurstuecke.get(0).getProperty(getFlurstueckReferenzGeometryPropertyName());

            if (geoObj instanceof Geometry) {
                final Geometry pureGeom = CrsTransformer.transformToGivenCrs((Geometry)geoObj,
                        ClientAlkisConf.getInstance().getSrsService());

                final Runnable mapRunnable = new Runnable() {

                        @Override
                        public void run() {
                            final ActiveLayerModel mappingModel = new ActiveLayerModel();
                            mappingModel.setSrs(ClientAlkisConf.getInstance().getSrsService());
                            mappingModel.addHome(getBoundingBox());
                            final SimpleWMS swms = new SimpleWMS(new SimpleWmsGetMapUrl(
                                        ClientAlkisConf.getInstance().getMapCallString()));
                            swms.setName("Flurstueck");

                            final Collection<MetaObject> selObj = new ArrayList<MetaObject>(1);
                            selObj.add(cidsBean.getMetaObject());
                            setAllSelectedMetaObjects(selObj);
                            final List<Feature> addedFeatures = TypeSafeCollections.newArrayList(selObj.size());
                            for (final MetaObject mo : selObj) {
                                final CidsFeature newGeomFeature = new CidsFeature(mo);
                                addedFeatures.addAll(FeatureGroups.expandAll(newGeomFeature));
                            }

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
                            map.addCustomInputListener("MUTE", new PBasicInputEventHandler() {

                                    @Override
                                    public void mouseClicked(final PInputEvent evt) {
                                        if (evt.getClickCount() > 1) {
                                            final CidsBean bean = cidsBean;
                                            ObjectRendererUtils.switchToCismapMap();
                                            ObjectRendererUtils.addBeanGeomAsFeatureToCismapMap(bean, false);
                                        }
                                    }
                                });
                            map.setInteractionMode("MUTE");
                            map.getFeatureCollection().addFeatures(addedFeatures);
                            map.setAnimationDuration(duration);
                        }
                    };
                if (EventQueue.isDispatchThread()) {
                    mapRunnable.run();
                } else {
                    EventQueue.invokeLater(mapRunnable);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private XBoundingBox getBoundingBox() {
        XBoundingBox result = null;

        final List<CidsBean> flurstuecke = CidsBeanSupport.getBeanCollectionFromProperty(cidsBean, "ref_flurstuecke");

        for (final CidsBean flurstueck : flurstuecke) {
            if (flurstueck.getProperty(getFlurstueckReferenzGeometryPropertyName()) instanceof Geometry) {
                final Geometry geometry = CrsTransformer.transformToGivenCrs((Geometry)flurstueck.getProperty(
                            getFlurstueckReferenzGeometryPropertyName()),
                        ClientAlkisConf.getInstance().getSrsService());

                if (result == null) {
                    result = new XBoundingBox(geometry.getEnvelope().buffer(
                                ClientAlkisConf.getInstance().getGeoBuffer()),
                            ClientAlkisConf.getInstance().getSrsService(),
                            true);
                } else {
                    final XBoundingBox temp = new XBoundingBox(geometry.getEnvelope().buffer(
                                ClientAlkisConf.getInstance().getGeoBuffer()));
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
                    System.out.println(result.getSrs());
                }
            }
        }

        return result;
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }
}
