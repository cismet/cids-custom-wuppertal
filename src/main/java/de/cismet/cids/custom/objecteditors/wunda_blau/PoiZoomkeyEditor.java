/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objecteditors.wunda_blau;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;
import Sirius.navigator.ui.RequestsFullSizeComponent;
import Sirius.server.middleware.types.MetaClass;

import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;

import org.openide.util.NbBundle;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

import de.cismet.cids.client.tools.DevelopmentTools;
import de.cismet.cids.custom.objecteditors.utils.PoiConfProperties;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objecteditors.utils.TableUtils;
import de.cismet.cids.custom.objecteditors.wunda_blau.albo.ComboBoxFilterDialog;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.DivBeanTable;
import de.cismet.cids.custom.wunda_blau.search.server.RedundantObjectSearch;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.editors.DefaultBindableComboboxCellEditor;

import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.SaveVetoable;
import de.cismet.cids.editors.hooks.BeforeSavingHook;
import de.cismet.cids.navigator.utils.ClassCacheMultiple;


import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.MissingResourceException;
import java.util.concurrent.ExecutionException;
import org.jdesktop.swingx.JXTable;
/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class PoiZoomkeyEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    SaveVetoable,
    PropertyChangeListener,
    RequestsFullSizeComponent, 
    BeforeSavingHook {

    //~ Static fields/initializers ---------------------------------------------
    private static final Logger LOG = Logger.getLogger(PoiZoomkeyEditor.class);
    public static final String REDUNDANT_TOSTRING_TEMPLATE = "%s";
    public static final String[] REDUNDANT_TOSTRING_FIELDS = { "name", "id" };

    public static final String FIELD__NAME = "name";                             // PoiZoomkey
    public static final String FIELD__ID = "id";                                 // PoiZoomkey
    public static final String FIELD__POI = "fk_locationinstance";               // PoiZoomdefinition
    public static final String FIELD__DEFS = "n_zoomdefinitionen";
    public static final String TABLE_NAME = "poi_zoomkey";
    public static final String TABLE_DEF = "poi_zoomdefinition";
    public static final String TABLE_POI = "poi_locationinstance";
    public static final String TABLE_PRIO = "poi_zoomprio";

    public static final String BUNDLE_NONAME = "PoiZoomkeyeEditor.isOkForSaving().noName";
    public static final String BUNDLE_WRONGNAME = "PoiZoomkeyEditor.isOkForSaving().wrongName";
    public static final String BUNDLE_DUPLICATENAME = "PoiZoomkeyEditor.isOkForSaving().duplicateName";
    public static final String BUNDLE_LENGTHNAME = "PoiZoomkeyEditor.isOkForSaving().lengthName";
    public static final String BUNDLE_PANE_PREFIX = "PoiZoomkeyEditor.isOkForSaving().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX = "PoiZoomkeyEditor.isOkForSaving().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "PoiZoomkeyEditor.isOkForSaving().JOptionPane.title";
    public static final String BUNDLE_PANE_MESSAGE_DEL =
        "PoiZoomkeyEditor.btnDeleteZoomdefActionPerformed().JOptionPane.message";
    public static final String BUNDLE_PANE_TITLE_DEL =
        "PoiZoomkeyEditor.btnDeleteZoomdefActionPerformed().JOptionPane.title";
    private static final String TITLE_NEW_ZOOMKEY = "einen neuen Zoomkey anlegen...";
    
    private static final String[] DEFS_COL_NAMES = new String[] { "POIs", "Zoomprio" };
    private static final String[] DEFS_PROP_NAMES = new String[] {
            "fk_locationinstance",
            "fk_zoomprio"
        };
    private static final Class[] DEFS_PROP_TYPES = new Class[] {
            CidsBean.class,
            CidsBean.class
        };
    public static int maxKeyUrlLength = 1;
    public static String keyUrlPattern = "";

    //~ Instance fields --------------------------------------------------------
    private MetaClass poiMetaClass;
    private MetaClass prioMetaClass;
    
    /** DOCUMENT ME! */
    private final boolean editor;
    private Boolean redundantName = false;
    public Boolean boolNameOk = false;
    
    private SwingWorker worker_name;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton btnAddZoomdef;
    private JButton btnDeleteZoomdef;
    private JButton btnRemZoomdef;
    private ComboBoxFilterDialog comboBoxFilterDialogPoi;
    private Box.Filler filler2;
    private JPanel jPanelAllgemein;
    private JScrollPane jScrollPaneZoomdef;
    private JLabel lblBemerkung;
    private JLabel lblName;
    private JPanel panBemerkung;
    private JPanel panContent;
    private JPanel panDaten;
    private JPanel panFillerUnten;
    private JPanel panPoi;
    private JPanel panZoomdef;
    private JPanel panZoomdefAdd;
    private JScrollPane scpBemerkung;
    private JTextArea taBemerkung;
    private JTextField txtName;
    private JXTable xtZoomdef;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public PoiZoomkeyEditor() {
        this(true);
    }

    /**
     * Creates a new PoiZoomkeyEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public PoiZoomkeyEditor(final boolean boolEditor) {
        this.editor = boolEditor;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        super.initWithConnectionContext(connectionContext);
        initComponents();
        setReadOnly();
        maxKeyUrlLength = PoiConfProperties.getInstance().getKeyUrlLength();
        keyUrlPattern = PoiConfProperties.getInstance().getKeyUrlPattern();
        poiMetaClass = ClassCacheMultiple.getMetaClass(
                CidsBeanSupport.DOMAIN_NAME,
                TABLE_POI,
                connectionContext);
        prioMetaClass = ClassCacheMultiple.getMetaClass(
                CidsBeanSupport.DOMAIN_NAME,
                TABLE_PRIO,
                connectionContext);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        GridBagConstraints gridBagConstraints;
        bindingGroup = new BindingGroup();

        comboBoxFilterDialogPoi = new ComboBoxFilterDialog(null, new de.cismet.cids.custom.wunda_blau.search.server.PoiLightweightSearch(), "Poi auswählen", getConnectionContext()) ;
        panFillerUnten = new JPanel();
        panContent = new RoundedPanel();
        jPanelAllgemein = new JPanel();
        panDaten = new JPanel();
        lblName = new JLabel();
        txtName = new JTextField();
        lblBemerkung = new JLabel();
        panBemerkung = new JPanel();
        scpBemerkung = new JScrollPane();
        taBemerkung = new JTextArea();
        panPoi = new JPanel();
        panZoomdef = new JPanel();
        jScrollPaneZoomdef = new JScrollPane();
        xtZoomdef = new JXTable();
        panZoomdefAdd = new JPanel();
        btnAddZoomdef = new JButton();
        btnRemZoomdef = new JButton();
        filler2 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
        btnDeleteZoomdef = new JButton();

        setLayout(new GridBagLayout());

        panFillerUnten.setName(""); // NOI18N
        panFillerUnten.setOpaque(false);

        GroupLayout panFillerUntenLayout = new GroupLayout(panFillerUnten);
        panFillerUnten.setLayout(panFillerUntenLayout);
        panFillerUntenLayout.setHorizontalGroup(panFillerUntenLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panFillerUntenLayout.setVerticalGroup(panFillerUntenLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        add(panFillerUnten, gridBagConstraints);

        panContent.setName(""); // NOI18N
        panContent.setOpaque(false);
        panContent.setPreferredSize(new Dimension(840, 298));
        panContent.setLayout(new GridBagLayout());

        jPanelAllgemein.setOpaque(false);
        jPanelAllgemein.setLayout(new GridBagLayout());

        panDaten.setOpaque(false);
        panDaten.setLayout(new GridBagLayout());

        lblName.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblName.setText("Name:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblName, gridBagConstraints);

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.name}"), txtName, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtName, gridBagConstraints);

        lblBemerkung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBemerkung.setText("Bemerkung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblBemerkung, gridBagConstraints);

        panBemerkung.setOpaque(false);
        panBemerkung.setLayout(new GridBagLayout());

        taBemerkung.setColumns(20);
        taBemerkung.setLineWrap(true);
        taBemerkung.setRows(2);
        taBemerkung.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.bemerkung}"), taBemerkung, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpBemerkung.setViewportView(taBemerkung);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panBemerkung.add(scpBemerkung, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panBemerkung, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 10, 0, 10);
        jPanelAllgemein.add(panDaten, gridBagConstraints);

        panPoi.setMinimumSize(new Dimension(300, 142));
        panPoi.setOpaque(false);
        panPoi.setLayout(new GridBagLayout());

        panZoomdef.setMinimumSize(new Dimension(26, 80));
        panZoomdef.setOpaque(false);
        panZoomdef.setLayout(new GridBagLayout());

        jScrollPaneZoomdef.setOpaque(false);

        xtZoomdef.setOpaque(false);
        jScrollPaneZoomdef.setViewportView(xtZoomdef);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panZoomdef.add(jScrollPaneZoomdef, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panPoi.add(panZoomdef, gridBagConstraints);

        panZoomdefAdd.setAlignmentX(0.0F);
        panZoomdefAdd.setAlignmentY(1.0F);
        panZoomdefAdd.setFocusable(false);
        panZoomdefAdd.setOpaque(false);
        panZoomdefAdd.setLayout(new GridBagLayout());

        btnAddZoomdef.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddZoomdef.setBorderPainted(false);
        btnAddZoomdef.setContentAreaFilled(false);
        btnAddZoomdef.setFocusPainted(false);
        btnAddZoomdef.setMaximumSize(new Dimension(45, 22));
        btnAddZoomdef.setMinimumSize(new Dimension(45, 22));
        btnAddZoomdef.setPreferredSize(new Dimension(45, 22));
        btnAddZoomdef.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnAddZoomdefActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panZoomdefAdd.add(btnAddZoomdef, gridBagConstraints);

        btnRemZoomdef.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemZoomdef.setBorderPainted(false);
        btnRemZoomdef.setContentAreaFilled(false);
        btnRemZoomdef.setFocusPainted(false);
        btnRemZoomdef.setMaximumSize(new Dimension(45, 22));
        btnRemZoomdef.setMinimumSize(new Dimension(45, 22));
        btnRemZoomdef.setPreferredSize(new Dimension(45, 22));
        btnRemZoomdef.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnRemZoomdefActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panZoomdefAdd.add(btnRemZoomdef, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panZoomdefAdd.add(filler2, gridBagConstraints);

        btnDeleteZoomdef.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit-delete.png"))); // NOI18N
        btnDeleteZoomdef.setToolTipText("Standorte entfernen");
        btnDeleteZoomdef.setMaximumSize(new Dimension(45, 21));
        btnDeleteZoomdef.setMinimumSize(new Dimension(45, 21));
        btnDeleteZoomdef.setPreferredSize(new Dimension(45, 28));
        btnDeleteZoomdef.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnDeleteZoomdefActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new Insets(5, 2, 2, 2);
        panZoomdefAdd.add(btnDeleteZoomdef, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 2, 2, 2);
        panPoi.add(panZoomdefAdd, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        jPanelAllgemein.add(panPoi, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panContent.add(jPanelAllgemein, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        add(panContent, gridBagConstraints);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddZoomdefActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddZoomdefActionPerformed
        if (getCidsBean() != null) {
            CidsBean poiBean;
            final Object selectedItem = comboBoxFilterDialogPoi.showAndGetSelected();
            try {
                if (selectedItem instanceof CidsBean) {
                    poiBean = (CidsBean)selectedItem;
                    final CidsBean bean;
                    bean = CidsBeanSupport.createNewCidsBeanFromTableName(TABLE_DEF, getConnectionContext());
                    bean.setProperty(FIELD__POI, poiBean);
                    ((DivBeanTable)xtZoomdef.getModel()).addBean(bean);
                }
            } catch (Exception ex) {
                LOG.error("Fehler beim Hinzufuegen des Poi.", ex);
            }
        }   
    }//GEN-LAST:event_btnAddZoomdefActionPerformed

    private void btnRemZoomdefActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemZoomdefActionPerformed
        TableUtils.removeObjectsFromTable(xtZoomdef);
    }//GEN-LAST:event_btnRemZoomdefActionPerformed

    private void btnDeleteZoomdefActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnDeleteZoomdefActionPerformed
        // Meldung: wirklich loeschen?
        final int answer = JOptionPane.showConfirmDialog(
            StaticSwingTools.getParentFrame(this),
            NbBundle.getMessage(PoiZoomkeyEditor.class, BUNDLE_PANE_MESSAGE_DEL),
            NbBundle.getMessage(PoiZoomkeyEditor.class, BUNDLE_PANE_TITLE_DEL),
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        if (answer == JOptionPane.YES_OPTION) {
            deleteZoomdef();
        }
    }//GEN-LAST:event_btnDeleteZoomdefActionPerformed

    /**
     * Entfernt alle Zeilen aus der Tabelle.
     */
    public void deleteZoomdef() {
        xtZoomdef.selectAll();
        TableUtils.removeObjectsFromTable(xtZoomdef);
    }
    
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isEditor() {
        return this.editor;
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cb) {
        try {
            if (isEditor() && (getCidsBean() != null)) {
                LOG.info("remove propchange PoiZoomkey: " + getCidsBean());
                getCidsBean().removePropertyChangeListener(this);
            }
            bindingGroup.unbind();
            this.cidsBean = cb;
            if (isEditor() && (getCidsBean() != null)) {
                LOG.info("add propchange PoiZoomkey: " + getCidsBean());
                getCidsBean().addPropertyChangeListener(this);
            }
            // 8.5.17 s.Simmert: Methodenaufruf, weil sonst die Comboboxen nicht gefüllt werden
            // evtl. kann dies verbessert werden.
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cb,
                getConnectionContext());
            bindingGroup.bind();
            setTitle(getTitle());
            if (isEditor()) {
                checkSigns();
            }
            final DivBeanTable defModel = new DivBeanTable(
                    isEditor(),
                    cidsBean,
                    FIELD__DEFS,
                    DEFS_COL_NAMES,
                    DEFS_PROP_NAMES,
                    DEFS_PROP_TYPES);
            xtZoomdef.setModel(defModel);
            //xtZoomdef.getColumn(0).setCellEditor(new DefaultBindableComboboxCellEditor(poiMetaClass));
            xtZoomdef.getColumn(1).setCellEditor(new DefaultBindableComboboxCellEditor(prioMetaClass));
        } catch (Exception ex) {
            LOG.error("Bean not set", ex);
        }
    }

   
    /**
     * DOCUMENT ME!
     */
    private void setReadOnly() {
        if (!(isEditor())) {
            RendererTools.makeReadOnly(txtName);
            RendererTools.makeReadOnly(taBemerkung);
            RendererTools.makeReadOnly(xtZoomdef);
            panZoomdefAdd.setVisible(isEditor());
        }
    }
    
    private void checkSigns() {
        final SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {

                @Override
                protected Boolean doInBackground() throws Exception {
                    return txtName.getText().matches(keyUrlPattern);
                }

                @Override
                protected void done() {
                    try {
                        if (!isCancelled()) {
                            final boolean result = get();
                                boolNameOk = result;
                        }
                    } catch (final InterruptedException | ExecutionException ex) {
                        LOG.error("Fehler bei der Überprüfung der erlaubten Zeichen.", ex);
                    }
                }
            };
        
        if (worker_name != null) {
            worker_name.cancel(true);
        }
        worker_name = worker;
        worker_name.execute();
    }
    
    @Override
    public void beforeSaving() {
        final RedundantObjectSearch zoomkeySearch = new RedundantObjectSearch(
                REDUNDANT_TOSTRING_TEMPLATE,
                REDUNDANT_TOSTRING_FIELDS,
                null,
                TABLE_NAME);
        final Collection<String> conditions = new ArrayList<>();
        conditions.add(FIELD__NAME + " ilike '" + txtName.getText().trim() + "'");
        conditions.add(FIELD__ID + " <> " + getCidsBean().getProperty(FIELD__ID));
        zoomkeySearch.setWhere(conditions);
        try {
            redundantName =
                !(SessionManager.getProxy().customServerSearch(
                        SessionManager.getSession().getUser(),
                        zoomkeySearch,
                        getConnectionContext())).isEmpty();
        } catch (ConnectionException ex) {
            LOG.warn("problem in beforeSaving.", ex);
        }
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
            TABLE_NAME,
            1,
            800,
            600);
    }

    @Override
    public String getTitle() {
        if (getCidsBean().getMetaObject().getStatus() == MetaObject.NEW) {
            return TITLE_NEW_ZOOMKEY;
        } else {
            return getCidsBean().toString();
        }
    }

    @Override
    public void dispose() {
        if (isEditor()) {
            if (getCidsBean() != null) {
                LOG.info("remove propchange PoiZoomkey: " + getCidsBean());
                getCidsBean().removePropertyChangeListener(this);
            }
        }
        bindingGroup.unbind();
        super.dispose();
    }

    @Override
    public void setTitle(String title) {
        if (title == null) {
            title = "<Error>";
        }
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(FIELD__NAME)) {
            checkSigns();
        }
    }

    @Override
    public boolean isOkForSaving() {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();

        // name vorhanden
        try {
            if (txtName.getText().trim().isEmpty()) {
                LOG.warn("No name specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(PoiZoomkeyEditor.class, BUNDLE_NONAME));
                save = false;
            } else {
                //korrekte Zeichen
                if (!boolNameOk) {
                    LOG.warn("False name specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(PoiZoomkeyEditor.class, BUNDLE_WRONGNAME));
                    save = false;
                } else {
                    //redundant
                    if (redundantName) {
                        LOG.warn("Duplicate name specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(PoiZoomkeyEditor.class, BUNDLE_DUPLICATENAME));
                        save = false;
                    } else {
                        //zu lang
                        if (txtName.getText().length() > maxKeyUrlLength) {
                            LOG.warn("False KeyUrl specified. Skip persisting.");
                            errorMessage.append(NbBundle.getMessage(
                                    PoiZoomkeyEditor.class,
                                    BUNDLE_LENGTHNAME));
                            save = false;
                        }
                    }
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Name not given.", ex);
            save = false;
        }


        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(PoiZoomkeyEditor.class, BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(PoiZoomkeyEditor.class, BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(PoiZoomkeyEditor.class, BUNDLE_PANE_TITLE),
                JOptionPane.WARNING_MESSAGE);
        }
        return save;
    }

    //~ Inner Classes ----------------------------------------------------------

   

}
