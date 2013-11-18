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

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;

import de.cismet.cids.custom.objectrenderer.utils.AlphanumComparator;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.utils.alkis.AlkisConstants;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.DisposableCidsBeanStore;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.FeatureGroups;
import de.cismet.cismap.commons.features.StyledFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

import de.cismet.cismap.navigatorplugin.CidsFeature;

import de.cismet.tools.collections.TypeSafeCollections;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   verkennisr
 * @version  $Revision$, $Date$
 */
public class Boden_VorkaufsrechtEditorPanel extends javax.swing.JPanel implements DisposableCidsBeanStore {

    //~ Static fields/initializers ---------------------------------------------

    public static final String ATAG_FINAL_CHECK = "navigator.baulasten.final_check"; // NOI18N
    private static final Logger LOG = Logger.getLogger(Boden_VorkaufsrechtEditorPanel.class);

    //~ Instance fields --------------------------------------------------------

    private CidsBean cidsBean;
    private Collection<MetaObject> allSelectedObjects;
    private final boolean editable;
    private final Collection<JComponent> editableComponents;
    private final FlurstueckSelectionDialoge fsDialoge;
    private final MappingComponent map;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField aktenzeichen;
    private javax.swing.JButton btnAddFlurstueck;
    private javax.swing.JButton btnRemoveFlurstueck;
    private de.cismet.cids.editors.DefaultBindableDateChooser defaultBindableDateChooser1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel lblFlInMap;
    private javax.swing.JLabel lblHeadFlurstuecke;
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
    public Boden_VorkaufsrechtEditorPanel() {
        this(true);
    }

    /**
     * Creates new form Boden_VorkaufsrechtEditorPanel.
     *
     * @param  editable  DOCUMENT ME!
     */
    public Boden_VorkaufsrechtEditorPanel(final boolean editable) {
        this.editable = editable;
        this.editableComponents = new ArrayList<JComponent>();
        initComponents();
        initEditableComponents();

        map = new MappingComponent();
        if (!editable) {
            panMap.add(map, BorderLayout.CENTER);
        } else {
            this.remove(panMap);
        }
        fsDialoge = new FlurstueckSelectionDialoge();
    }

    //~ Methods ----------------------------------------------------------------

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
        jLabel4 = new javax.swing.JLabel();
        aktenzeichen = new javax.swing.JTextField();
        defaultBindableDateChooser1 = new de.cismet.cids.editors.DefaultBindableDateChooser();
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

        rpInfo.setMaximumSize(new java.awt.Dimension(5000, 105));
        rpInfo.setMinimumSize(new java.awt.Dimension(500, 105));
        rpInfo.setPreferredSize(new java.awt.Dimension(500, 105));
        rpInfo.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel1.setBackground(new java.awt.Color(255, 155, 51));
        semiRoundedPanel1.setForeground(new java.awt.Color(255, 255, 0));
        semiRoundedPanel1.setMaximumSize(new java.awt.Dimension(5000, 5000));
        semiRoundedPanel1.setMinimumSize(new java.awt.Dimension(500, 30));
        semiRoundedPanel1.setName("xxx"); // NOI18N
        semiRoundedPanel1.setPreferredSize(new java.awt.Dimension(500, 200));
        semiRoundedPanel1.setLayout(new java.awt.GridBagLayout());

        lblInfo.setBackground(new java.awt.Color(255, 155, 51));
        lblInfo.setForeground(new java.awt.Color(0, 0, 0));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblInfo,
            org.openide.util.NbBundle.getMessage(
                Boden_VorkaufsrechtEditorPanel.class,
                "Boden_VorkaufsrechtEditorPanel.lblInfo.text")); // NOI18N
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
                Boden_VorkaufsrechtEditorPanel.class,
                "Boden_VorkaufsrechtEditorPanel.lblFlInMap.toolTipText"));                           // NOI18N
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
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        rpInfo.add(semiRoundedPanel1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel3,
            org.openide.util.NbBundle.getMessage(
                Boden_VorkaufsrechtEditorPanel.class,
                "Boden_VorkaufsrechtEditorPanel.jLabel3.text"));                                     // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        rpInfo.add(jLabel3, gridBagConstraints);
        jLabel3.getAccessibleContext()
                .setAccessibleName(org.openide.util.NbBundle.getMessage(
                        Boden_VorkaufsrechtEditorPanel.class,
                        "Boden_VorkaufsrechtEditorPanel.jLabel3.AccessibleContext.accessibleName")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel4,
            org.openide.util.NbBundle.getMessage(
                Boden_VorkaufsrechtEditorPanel.class,
                "Boden_VorkaufsrechtEditorPanel.jLabel4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        rpInfo.add(jLabel4, gridBagConstraints);

        aktenzeichen.setToolTipText(org.openide.util.NbBundle.getMessage(
                Boden_VorkaufsrechtEditorPanel.class,
                "Boden_VorkaufsrechtEditorPanel.aktenzeichen.toolTipText")); // NOI18N

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
                Boden_VorkaufsrechtEditorPanel.class,
                "Boden_VorkaufsrechtEditorPanel.defaultBindableDateChooser1.toolTipText")); // NOI18N

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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        add(rpInfo, gridBagConstraints);
        rpInfo.getAccessibleContext()
                .setAccessibleName(org.openide.util.NbBundle.getMessage(
                        Boden_VorkaufsrechtEditorPanel.class,
                        "Boden_VorkaufsrechtEditorPanel.rpInfo.AccessibleContext.accessibleName")); // NOI18N

        rpFlurstuecke.setMinimumSize(new java.awt.Dimension(150, 350));
        rpFlurstuecke.setPreferredSize(new java.awt.Dimension(150, 350));
        rpFlurstuecke.setLayout(new java.awt.GridBagLayout());

        srpHeadFlurstuecke.setBackground(new java.awt.Color(255, 155, 51));
        srpHeadFlurstuecke.setMaximumSize(new java.awt.Dimension(200, 2000));
        srpHeadFlurstuecke.setMinimumSize(new java.awt.Dimension(150, 20));
        srpHeadFlurstuecke.setPreferredSize(new java.awt.Dimension(150, 20));
        srpHeadFlurstuecke.setRequestFocusEnabled(false);
        srpHeadFlurstuecke.setLayout(new java.awt.GridBagLayout());

        lblHeadFlurstuecke.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblHeadFlurstuecke,
            org.openide.util.NbBundle.getMessage(
                Boden_VorkaufsrechtEditorPanel.class,
                "Boden_VorkaufsrechtEditorPanel.lblHeadFlurstuecke.text")); // NOI18N
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
                Boden_VorkaufsrechtEditorPanel.class,
                "Boden_VorkaufsrechtEditorPanel.btnAddFlurstueck.toolTipText"));                               // NOI18N
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
                Boden_VorkaufsrechtEditorPanel.class,
                "Boden_VorkaufsrechtEditorPanel.btnRemoveFlurstueck.toolTipText"));                               // NOI18N
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
        gridBagConstraints.weighty = 0.5;
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
        if (!editable && (evt.getClickCount() > 1)) {
            handleJumpToListeSelectionBean(lstFlurstuecke);
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
     * @param  list  DOCUMENT ME!
     */
    private void handleJumpToListeSelectionBean(final JList list) {
        final Object selectedObj = list.getSelectedValue();
        if (selectedObj instanceof CidsBean) {
            final Object realFSBean = ((CidsBean)selectedObj).getProperty("fs_referenz");
            if (realFSBean instanceof CidsBean) {
                final MetaObject selMO = ((CidsBean)realFSBean).getMetaObject();
                ComponentRegistry.getRegistry().getDescriptionPane().gotoMetaObject(selMO, "");
            }
        }
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
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
        try {
            bindingGroup.unbind();
            if (cidsBean != null) {
                final int[] flstIdx = lstFlurstuecke.getSelectedIndices();
                final Collection<MetaObject> selObj = new ArrayList<MetaObject>(1);
                selObj.add(cidsBean.getMetaObject());
                setAllSelectedMetaObjects(selObj);
                this.cidsBean = cidsBean;
                List<CidsBean> flurstueckeCol = CidsBeanSupport.getBeanCollectionFromProperty(
                        cidsBean,
                        "ref_flurstuecke");
                Collections.sort(flurstueckeCol, AlphanumComparator.getInstance());
                flurstueckeCol = CidsBeanSupport.getBeanCollectionFromProperty(cidsBean, "ref_flurstuecke");
                Collections.sort(flurstueckeCol, AlphanumComparator.getInstance());

                bindingGroup.bind();
                lstFlurstuecke.setSelectedIndices(flstIdx);
                cidsBean.getMetaObject().getDebugString();
                if (!editable) {
                    initMap();
                }
            }
        } catch (final Exception x) {
            LOG.error("cannot initialise Boden_VorkaufsrechtEditorPanel", x); // NOI18N
        }
    }

    @Override
    public void dispose() {
        fsDialoge.dispose();
        map.dispose();
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
        final List<CidsBean> flurstuecke = CidsBeanSupport.getBeanCollectionFromProperty(cidsBean, "ref_flurstuecke");
        final Object geoObj = flurstuecke.get(0).getProperty("fs_referenz.umschreibendes_rechteck.geo_field");

        if (geoObj instanceof Geometry) {
            final Geometry pureGeom = CrsTransformer.transformToGivenCrs((Geometry)geoObj,
                    AlkisConstants.COMMONS.SRS_SERVICE);

            final Runnable mapRunnable = new Runnable() {

                    @Override
                    public void run() {
                        final ActiveLayerModel mappingModel = new ActiveLayerModel();
                        mappingModel.setSrs(AlkisConstants.COMMONS.SRS_SERVICE);
                        mappingModel.addHome(getBoundingBox());
                        final SimpleWMS swms = new SimpleWMS(new SimpleWmsGetMapUrl(
                                    AlkisConstants.COMMONS.MAP_CALL_STRING));
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

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private XBoundingBox getBoundingBox() {
        XBoundingBox result = null;

        final List<CidsBean> flurstuecke = CidsBeanSupport.getBeanCollectionFromProperty(cidsBean, "ref_flurstuecke");

        for (final CidsBean flurstueck : flurstuecke) {
            if (flurstueck.getProperty("fs_referenz.umschreibendes_rechteck.geo_field") instanceof Geometry) {
                final Geometry geometry = CrsTransformer.transformToGivenCrs((Geometry)flurstueck.getProperty(
                            "fs_referenz.umschreibendes_rechteck.geo_field"),
                        AlkisConstants.COMMONS.SRS_SERVICE);

                if (result == null) {
                    result = new XBoundingBox(geometry.getEnvelope().buffer(AlkisConstants.COMMONS.GEO_BUFFER),
                            AlkisConstants.COMMONS.SRS_SERVICE,
                            true);
                } else {
                    final XBoundingBox temp = new XBoundingBox(geometry.getEnvelope().buffer(
                                AlkisConstants.COMMONS.GEO_BUFFER));
                    temp.setSrs(AlkisConstants.COMMONS.SRS_SERVICE);
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
}
