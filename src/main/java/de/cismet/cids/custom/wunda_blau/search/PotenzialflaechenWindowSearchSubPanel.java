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
package de.cismet.cids.custom.wunda_blau.search;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObjectNode;

import lombok.Getter;

import org.apache.log4j.Logger;

import java.awt.CardLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.wunda_blau.search.actions.PotenzialflaecheReportServerAction;
import de.cismet.cids.custom.wunda_blau.search.server.PotenzialflaecheSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class PotenzialflaechenWindowSearchSubPanel extends javax.swing.JPanel implements ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(PotenzialflaechenWindowSearchSubPanel.class);

    //~ Instance fields --------------------------------------------------------

    @Getter private ConnectionContext connectionContext = ConnectionContext.createDummy();
    private final PotenzialflaechenWindowSearchPanel parent;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbForeign;
    private de.cismet.cids.editors.DefaultBindableLabelsPanel defaultBindableLabelsPanel1;
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JComboBox<PotenzialflaecheReportServerAction.Property> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JSpinner jSpinner2;
    private javax.swing.JTextField jTextField1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker2;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new PotenzialflaechenWindowSearchSubPanel object.
     */
    public PotenzialflaechenWindowSearchSubPanel() {
        this(null);
    }

    /**
     * Creates a new PotenzialflaechenWindowSearchSubPanel object.
     *
     * @param  parent  DOCUMENT ME!
     */
    public PotenzialflaechenWindowSearchSubPanel(final PotenzialflaechenWindowSearchPanel parent) {
        this.parent = parent;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  filter  DOCUMENT ME!
     */
    public void setFilter(final PotenzialflaecheSearch.FilterInfo filter) {
        if (filter != null) {
            final PotenzialflaecheReportServerAction.Property prop = filter.getProperty();
            jComboBox1.setSelectedItem(prop);
            final Object value = filter.getValue();
            if (PotenzialflaecheReportServerAction.Property.GROESSE.equals(prop)) {
                jSpinner1.setValue(((Double[])value)[0]);
                jSpinner2.setValue(((Double[])value)[1]);
            } else if (prop.getValue() instanceof PotenzialflaecheReportServerAction.PathReportProperty) {
                if (prop.getValue() instanceof PotenzialflaecheReportServerAction.KeytableReportProperty) {
                    if (value instanceof MetaObjectNode) {
                        for (int index = 0; index < cbForeign.getModel().getSize(); index++) {
                            final CidsBean cidsBean = (CidsBean)cbForeign.getModel().getElementAt(index);
                            if (new MetaObjectNode(cidsBean).equals(((MetaObjectNode)value))) {
                                cbForeign.setSelectedItem(cidsBean);
                                break;
                            }
                        }
                    } else if (value instanceof Collection) {
                        final Collection<CidsBean> selectedBeans = new ArrayList<>();
                        for (final CidsBean cidsBean : defaultBindableLabelsPanel1.getElements()) {
                            if (new MetaObjectNode(cidsBean).equals(((MetaObjectNode)value))) {
                                selectedBeans.add(cidsBean);
                            }
                        }
                        defaultBindableLabelsPanel1.setSelectedElements(selectedBeans);
                    }
                } else if (prop.getValue() instanceof PotenzialflaecheReportServerAction.SimpleFieldReportProperty) {
                    final String className =
                        ((PotenzialflaecheReportServerAction.SimpleFieldReportProperty)prop.getValue()).getClassName();
                    if (Date.class.getCanonicalName().equals(className)) {
                        jXDatePicker1.setDate(((Date[])value)[0]);
                        jXDatePicker2.setDate(((Date[])value)[1]);
                    } else if (Integer[].class.getCanonicalName().equals(className)) {
                        jSpinner1.setValue(((Integer[])value)[0]);
                        jSpinner2.setValue(((Integer[])value)[1]);
                    } else {
                        jTextField1.setText(String.valueOf(value));
                    }
                }
            } else if (prop.getValue() instanceof PotenzialflaecheReportServerAction.MonSearchReportProperty) {
                if (value instanceof MetaObjectNode) {
                    for (int index = 0; index < cbForeign.getModel().getSize(); index++) {
                        final CidsBean cidsBean = (CidsBean)cbForeign.getModel().getElementAt(index);
                        if (new MetaObjectNode(cidsBean).equals(((MetaObjectNode)value))) {
                            cbForeign.setSelectedItem(cidsBean);
                            break;
                        }
                    }
                } else if (value instanceof Collection) {
                    final Collection<CidsBean> selectedBeans = new ArrayList<>();
                    for (final CidsBean cidsBean : defaultBindableLabelsPanel1.getElements()) {
                        if (new MetaObjectNode(cidsBean).equals(((MetaObjectNode)value))) {
                            selectedBeans.add(cidsBean);
                        }
                    }
                    defaultBindableLabelsPanel1.setSelectedElements(selectedBeans);
                }
            }
        }
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;

        initComponents();

        defaultBindableLabelsPanel1.initWithConnectionContext(connectionContext);

        final List<PotenzialflaecheReportServerAction.Property> props = new ArrayList<>();
        for (final PotenzialflaecheReportServerAction.Property prop
                    : PotenzialflaecheReportServerAction.Property.values()) {
            if (PotenzialflaecheReportServerAction.Property.BEBAUUNGSPLAN.equals(prop)
                        || PotenzialflaecheReportServerAction.Property.FLURSTUECKE.equals(prop)) {
                continue;
            }
            if ((prop.getValue() instanceof PotenzialflaecheReportServerAction.PathReportProperty)
                        || (prop.getValue() instanceof PotenzialflaecheReportServerAction.MonSearchReportProperty)
                        || PotenzialflaecheReportServerAction.Property.GROESSE.equals(prop)) {
                props.add(prop);
            }
        }
        props.sort(new Comparator<PotenzialflaecheReportServerAction.Property>() {

                @Override
                public int compare(final PotenzialflaecheReportServerAction.Property o1,
                        final PotenzialflaecheReportServerAction.Property o2) {
                    return o1.toString().compareTo(o2.toString());
                }
            });
        for (final PotenzialflaecheReportServerAction.Property prop : props) {
            ((DefaultComboBoxModel)jComboBox1.getModel()).addElement(prop);
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

        jButton1 = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jCheckBox2 = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        cbForeign = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        defaultBindableLabelsPanel1 = new de.cismet.cids.editors.DefaultBindableLabelsPanel(true, "");
        jPanel4 = new javax.swing.JPanel();
        jCheckBox4 = new javax.swing.JCheckBox();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        jCheckBox5 = new javax.swing.JCheckBox();
        jXDatePicker2 = new org.jdesktop.swingx.JXDatePicker();
        jPanel5 = new javax.swing.JPanel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jSpinner1 = new javax.swing.JSpinner();
        jCheckBox3 = new javax.swing.JCheckBox();
        jSpinner2 = new javax.swing.JSpinner();

        setLayout(new java.awt.GridBagLayout());

        jButton1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/optionspanels/wunda_blau/remove.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jButton1,
            org.openide.util.NbBundle.getMessage(
                PotenzialflaechenWindowSearchSubPanel.class,
                "PotenzialflaechenWindowSearchSubPanel.jButton1.text"));                                // NOI18N
        jButton1.setBorderPainted(false);
        jButton1.setContentAreaFilled(false);
        jButton1.setFocusPainted(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(jButton1, gridBagConstraints);

        jComboBox1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jComboBox1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        add(jComboBox1, gridBagConstraints);

        jPanel1.setLayout(new java.awt.CardLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(
                PotenzialflaechenWindowSearchSubPanel.class,
                "PotenzialflaechenWindowSearchSubPanel.jLabel1.text")); // NOI18N
        jPanel1.add(jLabel1, "none");

        jTextField1.setText(org.openide.util.NbBundle.getMessage(
                PotenzialflaechenWindowSearchSubPanel.class,
                "PotenzialflaechenWindowSearchSubPanel.jTextField1.text")); // NOI18N
        jTextField1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jTextField1ActionPerformed(evt);
                }
            });
        jPanel1.add(jTextField1, "text");

        jPanel3.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jCheckBox2,
            org.openide.util.NbBundle.getMessage(
                PotenzialflaechenWindowSearchSubPanel.class,
                "PotenzialflaechenWindowSearchSubPanel.jCheckBox2.text")); // NOI18N
        jCheckBox2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jCheckBox2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel3.add(jCheckBox2, gridBagConstraints);

        jPanel2.setLayout(new java.awt.CardLayout());
        jPanel2.add(cbForeign, "single");
        jPanel2.add(defaultBindableLabelsPanel1, "multi");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel3.add(jPanel2, gridBagConstraints);

        jPanel1.add(jPanel3, "foreign");

        jPanel4.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jCheckBox4,
            org.openide.util.NbBundle.getMessage(
                PotenzialflaechenWindowSearchSubPanel.class,
                "PotenzialflaechenWindowSearchSubPanel.jCheckBox4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel4.add(jCheckBox4, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                jCheckBox4,
                org.jdesktop.beansbinding.ELProperty.create("${selected}"),
                jXDatePicker1,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel4.add(jXDatePicker1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jCheckBox5,
            org.openide.util.NbBundle.getMessage(
                PotenzialflaechenWindowSearchSubPanel.class,
                "PotenzialflaechenWindowSearchSubPanel.jCheckBox5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel4.add(jCheckBox5, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                jCheckBox5,
                org.jdesktop.beansbinding.ELProperty.create("${selected}"),
                jXDatePicker2,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel4.add(jXDatePicker2, gridBagConstraints);

        jPanel1.add(jPanel4, "date");
        jPanel4.getAccessibleContext()
                .setAccessibleName(org.openide.util.NbBundle.getMessage(
                        PotenzialflaechenWindowSearchSubPanel.class,
                        "PotenzialflaechenWindowSearchSubPanel.jPanel4.AccessibleContext.accessibleName")); // NOI18N

        jPanel5.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jCheckBox1,
            org.openide.util.NbBundle.getMessage(
                PotenzialflaechenWindowSearchSubPanel.class,
                "PotenzialflaechenWindowSearchSubPanel.jCheckBox1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel5.add(jCheckBox1, gridBagConstraints);

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, null, 1.0d));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                jCheckBox1,
                org.jdesktop.beansbinding.ELProperty.create("${selected}"),
                jSpinner1,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel5.add(jSpinner1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jCheckBox3,
            org.openide.util.NbBundle.getMessage(
                PotenzialflaechenWindowSearchSubPanel.class,
                "PotenzialflaechenWindowSearchSubPanel.jCheckBox3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel5.add(jCheckBox3, gridBagConstraints);

        jSpinner2.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, null, 1.0d));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                jCheckBox3,
                org.jdesktop.beansbinding.ELProperty.create("${selected}"),
                jSpinner2,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel5.add(jSpinner2, gridBagConstraints);

        jPanel1.add(jPanel5, "double");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 1.0;
        add(jPanel1, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jComboBox1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jComboBox1ActionPerformed
        try {
            final PotenzialflaecheReportServerAction.Property prop = (PotenzialflaecheReportServerAction.Property)
                jComboBox1.getSelectedItem();
            if (PotenzialflaecheReportServerAction.Property.GROESSE.equals(prop)) {
                jSpinner1.setValue(0d);
                jSpinner2.setValue(0d);
                ((CardLayout)jPanel1.getLayout()).show(jPanel1, "double");
            } else if (prop.getValue() instanceof PotenzialflaecheReportServerAction.KeytableReportProperty) {
                final PotenzialflaecheReportServerAction.KeytableReportProperty value =
                    (PotenzialflaecheReportServerAction.KeytableReportProperty)prop.getValue();

                ((CardLayout)jPanel1.getLayout()).show(jPanel1, "foreign");
                ((CardLayout)jPanel2.getLayout()).show(jPanel2, "single");
                cbForeign.setSelectedItem(null);
                defaultBindableLabelsPanel1.setSelectedElements(null);
                new SwingWorker<MetaClass, Void>() {

                        @Override
                        protected MetaClass doInBackground() throws Exception {
                            final MetaClass metaClass = ClassCacheMultiple.getMetaClass(
                                    CidsBeanSupport.DOMAIN_NAME,
                                    value.getForeignTable(),
                                    getConnectionContext());
                            return metaClass;
                        }

                        @Override
                        protected void done() {
                            try {
                                final MetaClass metaClass = get();
                                cbForeign.setMetaClass(metaClass);
                                defaultBindableLabelsPanel1.setMetaClass(metaClass);
                            } catch (final Exception ex) {
                                LOG.error(ex, ex);
                            }
                        }
                    }.execute();
            } else if (prop.getValue() instanceof PotenzialflaecheReportServerAction.SimpleFieldReportProperty) {
                final String className = ((PotenzialflaecheReportServerAction.SimpleFieldReportProperty)prop.getValue())
                            .getClassName();
                if (Date.class.getCanonicalName().equals(className)) {
                    jXDatePicker1.setDate(null);
                    jXDatePicker2.setDate(null);
                    ((CardLayout)jPanel1.getLayout()).show(jPanel1, "date");
                } else if (Integer.class.getCanonicalName().equals(className)) {
                    jSpinner1.setValue(0d);
                    jSpinner2.setValue(0d);
                    ((CardLayout)jPanel1.getLayout()).show(jPanel1, "double");
                } else {
                    jTextField1.setText("");
                    ((CardLayout)jPanel1.getLayout()).show(jPanel1, "text");
                }
            } else if (prop.getValue() instanceof PotenzialflaecheReportServerAction.MonSearchReportProperty) {
                final PotenzialflaecheReportServerAction.MonSearchReportProperty value =
                    ((PotenzialflaecheReportServerAction.MonSearchReportProperty)prop.getValue());
                ((CardLayout)jPanel1.getLayout()).show(jPanel1, "foreign");
                ((CardLayout)jPanel2.getLayout()).show(jPanel2, "single");
                cbForeign.setSelectedItem(null);
                defaultBindableLabelsPanel1.setSelectedElements(null);
                new SwingWorker<MetaClass, Void>() {

                        @Override
                        protected MetaClass doInBackground() throws Exception {
                            final MetaClass metaClass = ClassCacheMultiple.getMetaClass(
                                    CidsBeanSupport.DOMAIN_NAME,
                                    value.getTableName(),
                                    getConnectionContext());
                            return metaClass;
                        }

                        @Override
                        protected void done() {
                            try {
                                final MetaClass metaClass = get();
                                cbForeign.setMetaClass(metaClass);
                                defaultBindableLabelsPanel1.setMetaClass(metaClass);
                            } catch (final Exception ex) {
                                LOG.error(ex, ex);
                            }
                        }
                    }.execute();
            } else {
                ((CardLayout)jPanel1.getLayout()).show(jPanel1, "none");
            }
            jCheckBox2.setSelected(false);
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
    } //GEN-LAST:event_jComboBox1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        parent.removeSub(this);
    }                                                                            //GEN-LAST:event_jButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jCheckBox2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jCheckBox2ActionPerformed
        (((CardLayout)jPanel2.getLayout())).show(jPanel2, jCheckBox2.isSelected() ? "multi" : "single");
    }                                                                              //GEN-LAST:event_jCheckBox2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jTextField1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_jTextField1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public PotenzialflaecheSearch.FilterInfo getFilter() {
        final PotenzialflaecheReportServerAction.Property prop = (PotenzialflaecheReportServerAction.Property)
            jComboBox1.getSelectedItem();

        if (PotenzialflaecheReportServerAction.Property.GROESSE.equals(prop)) {
            return new PotenzialflaecheSearch.FilterInfo(
                    prop,
                    new Double[] {
                        jCheckBox1.isSelected() ? ((Double)jSpinner1.getValue()) : null,
                        jCheckBox3.isSelected() ? ((Double)jSpinner2.getValue()) : null
                    });
        } else if (prop.getValue() instanceof PotenzialflaecheReportServerAction.KeytableReportProperty) {
            if (jCheckBox2.isSelected()) {
                final List<CidsBean> selectedElements = (List)defaultBindableLabelsPanel1.getSelectedElements();
                final List<MetaObjectNode> mons = new ArrayList<>();
                for (final CidsBean selectedElement : selectedElements) {
                    mons.add(new MetaObjectNode(selectedElement));
                }
                return new PotenzialflaecheSearch.FilterInfo(prop, mons);
            } else {
                final MetaObjectNode mon = (cbForeign.getSelectedItem() != null)
                    ? new MetaObjectNode((CidsBean)cbForeign.getSelectedItem()) : null;
                return new PotenzialflaecheSearch.FilterInfo(prop, mon);
            }
        } else if (prop.getValue() instanceof PotenzialflaecheReportServerAction.MonSearchReportProperty) {
            if (jCheckBox2.isSelected()) {
                final List<CidsBean> selectedElements = (List)defaultBindableLabelsPanel1.getSelectedElements();
                final List<MetaObjectNode> mons = new ArrayList<>();
                for (final CidsBean selectedElement : selectedElements) {
                    mons.add(new MetaObjectNode(selectedElement));
                }
                return new PotenzialflaecheSearch.FilterInfo(prop, mons);
            } else {
                final MetaObjectNode mon = (cbForeign.getSelectedItem() != null)
                    ? new MetaObjectNode((CidsBean)cbForeign.getSelectedItem()) : null;
                return new PotenzialflaecheSearch.FilterInfo(prop, mon);
            }
        } else if (prop.getValue() instanceof PotenzialflaecheReportServerAction.SimpleFieldReportProperty) {
            final String className = ((PotenzialflaecheReportServerAction.SimpleFieldReportProperty)prop.getValue())
                        .getClassName();
            if (Date.class.getCanonicalName().equals(className)) {
                return new PotenzialflaecheSearch.FilterInfo(
                        prop,
                        new Date[] {
                            jCheckBox4.isSelected() ? jXDatePicker1.getDate() : null,
                            jCheckBox5.isSelected() ? jXDatePicker2.getDate() : null
                        });
            } else if (Integer.class.getCanonicalName().equals(className)) {
                return new PotenzialflaecheSearch.FilterInfo(
                        prop,
                        new Integer[] {
                            jCheckBox1.isSelected() ? ((Double)jSpinner1.getValue()).intValue() : null,
                            jCheckBox3.isSelected() ? ((Double)jSpinner2.getValue()).intValue() : null
                        });
            } else {
                return new PotenzialflaecheSearch.FilterInfo(prop, jTextField1.getText());
            }
        }
        return null;
    }
}
