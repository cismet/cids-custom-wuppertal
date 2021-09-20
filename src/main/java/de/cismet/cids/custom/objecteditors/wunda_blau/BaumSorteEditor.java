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

import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;


import org.openide.util.NbBundle;

import javax.swing.*;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultCustomObjectEditor;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;

import de.cismet.cids.custom.wunda_blau.search.server.RedundantObjectSearch;
import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.SaveVetoable;
import de.cismet.cids.editors.hooks.BeforeSavingHook;
import java.util.ArrayList;
import java.util.Collection;
import java.util.MissingResourceException;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class BaumSorteEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    BeforeSavingHook,
    SaveVetoable,
    RequestsFullSizeComponent {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(BaumSorteEditor.class);
    public static final String REDUNDANT_TOSTRING_TEMPLATE = "%s";
    public static final String[] REDUNDANT_TOSTRING_FIELDS = {"name", "id"};
    public static final String REDUNDANT_TABLE = "baum_sorte";
 
    public static final String FIELD__NAME = "name";                            // baum_sorte
    public static final String FIELD__MAIN = "fk_art";                          // baum_sorte
    public static final String FIELD__MAIN_ID = "fk_art.id";                    // baum_art
    public static final String FIELD__NAME_BOTANISCH = "name_botanisch";        // baum_sorte
    public static final String FIELD__ID = "id";                                // baum_sorte
    public static final String TABLE_NAME = "baum_sorte";

    public static final String BUNDLE_NONAME = 
            "BaumSorteEditor.isOkForSaving().noName";
    public static final String BUNDLE_DUPLICATENAME = 
            "BaumSorteEditor.isOkForSaving().duplicateName";
    public static final String BUNDLE_NOMAIN = 
            "BaumSorteEditor.isOkForSaving().noMain";
    public static final String BUNDLE_PANE_PREFIX =
        "BaumSorteEditor.isOkForSaving().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX =
        "BaumSorteEditor.isOkForSaving().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = 
            "BaumSorteEditor.isOkForSaving().JOptionPane.title";

    //~ Enums ------------------------------------------------------------------


    //~ Instance fields --------------------------------------------------------
    private Boolean redundantName = false;
    private static String TITLE_NEW_SORTE = "eine neue Sorte anlegen..."; 

    private boolean isEditor = true;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbArt;
    private javax.swing.JLabel lblArt;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblNameBotanisch;
    private javax.swing.JPanel panContent;
    private javax.swing.JPanel panFillerUnten;
    private javax.swing.JPanel panFillerUnten1;
    private javax.swing.JPanel panName;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtNameBotanisch;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public BaumSorteEditor() {
    }

    /**
     * Creates a new BaumSorteEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public BaumSorteEditor(final boolean boolEditor) {
        this.isEditor = boolEditor;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        super.initWithConnectionContext(connectionContext);
        initComponents();

        setReadOnly();
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

        panFillerUnten = new javax.swing.JPanel();
        panContent = new RoundedPanel();
        panName = new javax.swing.JPanel();
        lblName = new javax.swing.JLabel();
        lblNameBotanisch = new javax.swing.JLabel();
        txtNameBotanisch = new javax.swing.JTextField();
        lblArt = new javax.swing.JLabel();
        cbArt = new DefaultBindableReferenceCombo() ;
        txtName = new javax.swing.JTextField();
        panFillerUnten1 = new javax.swing.JPanel();

        setAutoscrolls(true);
        setMinimumSize(new java.awt.Dimension(600, 646));
        setPreferredSize(new java.awt.Dimension(600, 737));
        setLayout(new java.awt.GridBagLayout());

        panFillerUnten.setName(""); // NOI18N
        panFillerUnten.setOpaque(false);

        javax.swing.GroupLayout panFillerUntenLayout = new javax.swing.GroupLayout(panFillerUnten);
        panFillerUnten.setLayout(panFillerUntenLayout);
        panFillerUntenLayout.setHorizontalGroup(
            panFillerUntenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panFillerUntenLayout.setVerticalGroup(
            panFillerUntenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(panFillerUnten, gridBagConstraints);

        panContent.setAutoscrolls(true);
        panContent.setMaximumSize(new java.awt.Dimension(450, 2147483647));
        panContent.setMinimumSize(new java.awt.Dimension(450, 488));
        panContent.setName(""); // NOI18N
        panContent.setOpaque(false);
        panContent.setPreferredSize(new java.awt.Dimension(450, 961));
        panContent.setLayout(new java.awt.GridBagLayout());

        panName.setOpaque(false);
        panName.setLayout(new java.awt.GridBagLayout());

        lblName.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblName.setText("Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        panName.add(lblName, gridBagConstraints);

        lblNameBotanisch.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblNameBotanisch.setText("Name botanisch:");
        lblNameBotanisch.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        panName.add(lblNameBotanisch, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.name_botanisch}"), txtNameBotanisch, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panName.add(txtNameBotanisch, gridBagConstraints);

        lblArt.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblArt.setText("Art:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        panName.add(lblArt, gridBagConstraints);

        cbArt.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        cbArt.setMaximumRowCount(20);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_art}"), cbArt, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panName.add(cbArt, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.name}"), txtName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panName.add(txtName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panContent.add(panName, gridBagConstraints);

        panFillerUnten1.setName(""); // NOI18N
        panFillerUnten1.setOpaque(false);

        javax.swing.GroupLayout panFillerUnten1Layout = new javax.swing.GroupLayout(panFillerUnten1);
        panFillerUnten1.setLayout(panFillerUnten1Layout);
        panFillerUnten1Layout.setHorizontalGroup(
            panFillerUnten1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panFillerUnten1Layout.setVerticalGroup(
            panFillerUnten1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        panContent.add(panFillerUnten1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(panContent, gridBagConstraints);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cb) {
        try {
            bindingGroup.unbind();
            this.cidsBean = cb;
            if (cb != null) {
                DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cb,
                getConnectionContext());
            }
            bindingGroup.bind();
        } catch (final Exception ex) {
            LOG.warn("Error setCidsBean.", ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void setReadOnly() {
        if (!(isEditor)) {
            RendererTools.makeReadOnly(txtName);
            RendererTools.makeReadOnly(txtNameBotanisch);
            RendererTools.makeReadOnly(cbArt);
        }
    }

    @Override
    public String getTitle() {
       if (cidsBean.getMetaObject().getStatus() == MetaObject.NEW){
            return TITLE_NEW_SORTE;
        } else {
            return cidsBean.toString();
        }
    }

    @Override
    public void setTitle(final String string) {
    }

    @Override
    public void beforeSaving() {
        RedundantObjectSearch sorteSearch = new RedundantObjectSearch(
            REDUNDANT_TOSTRING_TEMPLATE,
            REDUNDANT_TOSTRING_FIELDS,
            null,
            REDUNDANT_TABLE);
        Object selection = cbArt.getSelectedItem();
        final Collection<String> conditions = new ArrayList<>();
        if (selection!= null && selection instanceof CidsBean){
            Integer mainId = ((CidsBean)selection).getPrimaryKeyValue();
            conditions.add(FIELD__NAME+ " ilike '" + txtName.getText().trim() + "'");
            conditions.add(FIELD__MAIN + " = " + mainId);
            conditions.add(FIELD__ID + " <> " + cidsBean.getProperty(FIELD__ID));
        }
        sorteSearch.setWhere(conditions);
        try {
                redundantName = !(SessionManager.getProxy().customServerSearch(
                        SessionManager.getSession().getUser(),
                        sorteSearch,
                        getConnectionContext())).isEmpty();
        } catch (ConnectionException ex) {
            LOG.warn("Error Search Name", ex);
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
                errorMessage.append(NbBundle.getMessage(BaumSorteEditor.class, BUNDLE_NONAME));
                save = false;
            } else {
                if (redundantName) {
                    LOG.warn("Duplicate name specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumSorteEditor.class, BUNDLE_DUPLICATENAME));
                    save = false;
                } 
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Name not given.", ex);
            save = false;
        }
        
        // art vorhanden
        try {
            if (cbArt.getSelectedItem() == null) {
                LOG.warn("No Hauptart specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(BaumSorteEditor.class, BUNDLE_NOMAIN));
                save = false;
            } 
        } catch (final MissingResourceException ex) {
            LOG.warn("Art not given.", ex);
            save = false;
        }

        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(BaumSorteEditor.class,
                    BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(BaumSorteEditor.class,
                            BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(BaumSorteEditor.class,
                    BUNDLE_PANE_TITLE),
                JOptionPane.WARNING_MESSAGE);
        }
        return save;
    }
}
