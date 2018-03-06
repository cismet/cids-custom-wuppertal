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

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;
import Sirius.navigator.types.treenode.RootTreeNode;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.Binding;

import org.jfree.util.Log;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultBindableDateChooser;
import de.cismet.cids.editors.DefaultBindableJTextField;
import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.navigator.utils.CidsBeanDropListener;
import de.cismet.cids.navigator.utils.CidsBeanDropTarget;
import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.connectioncontext.ClientConnectionContext;
import de.cismet.connectioncontext.ClientConnectionContextStore;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class BillingKundeEditor extends javax.swing.JPanel implements CidsBeanRenderer,
    EditorSaveListener,
    ClientConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(BillingKundeEditor.class);
    public static final String DOMAIN = "WUNDA_BLAU";
    public static final String KUNDENGRUPPE_TABLE = "BILLING_KUNDENGRUPPE";

    //~ Instance fields --------------------------------------------------------

    private List<CidsBean> kundengruppen = new ArrayList();
    private Set<CidsBean> touchedKundengruppen = new HashSet<CidsBean>();
    private List<CidsBean> kundenLogins = new ArrayList();
    private Set<CidsBean> touchedKundenLogins = new HashSet<CidsBean>();

    private CidsBean cidsBean;

    private ClientConnectionContext connectionContext;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRemKundenLogin;
    private javax.swing.JButton btnRemKundengruppe;
    private javax.swing.JButton btnRemProduct;
    private javax.swing.JComboBox cboAbrechnungsturnus;
    private javax.swing.JComboBox cboBranche;
    private org.jdesktop.swingx.JXDatePicker dcVertragsende;
    private org.jdesktop.swingx.JXDatePicker dcWeiterverkaufsvertragsende;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JList lstKundenLogins;
    private javax.swing.JList lstKundengruppe;
    private javax.swing.JList lstProdukte;
    private javax.swing.JScrollPane scpKundenLogins;
    private javax.swing.JScrollPane scpKundengruppe;
    private javax.swing.JScrollPane scpProdukte;
    private javax.swing.JTextField txtDirektkontakt;
    private javax.swing.JTextField txtInternalName;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtVermessungsstellennummer;
    private javax.swing.JTextField txtVertragskennzeichen;
    private javax.swing.JTextField txtWeiterverkaufsvertragskennzeichen;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BillingKundeEditor object.
     */
    public BillingKundeEditor() {
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initAfterConnectionContext() {
        initComponents();
        try {
            new CidsBeanDropTarget(lstKundenLogins);
            new CidsBeanDropTarget(lstProdukte);
            new CidsBeanDropTarget(lstKundengruppe);
        } catch (final Exception ex) {
            LOG.warn("Error while creating CidsBeanDropTarget", ex); // NOI18N
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtName = new DefaultBindableJTextField();
        jLabel2 = new javax.swing.JLabel();
        txtVertragskennzeichen = new DefaultBindableJTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtWeiterverkaufsvertragskennzeichen = new DefaultBindableJTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtDirektkontakt = new DefaultBindableJTextField();
        jLabel7 = new javax.swing.JLabel();
        txtVermessungsstellennummer = new DefaultBindableJTextField();
        dcVertragsende = new DefaultBindableDateChooser();
        dcWeiterverkaufsvertragsende = new DefaultBindableDateChooser();
        jLabel8 = new javax.swing.JLabel();
        cboBranche = new DefaultBindableReferenceCombo();
        jLabel9 = new javax.swing.JLabel();
        btnRemProduct = new javax.swing.JButton();
        cboAbrechnungsturnus = new DefaultBindableReferenceCombo();
        scpProdukte = new javax.swing.JScrollPane();
        lstProdukte = new DroppedBeansList();
        jLabel10 = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jLabel11 = new javax.swing.JLabel();
        scpKundenLogins = new javax.swing.JScrollPane();
        lstKundenLogins = new DroppedBeansList();
        btnRemKundenLogin = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        txtInternalName = new DefaultBindableJTextField();
        jLabel13 = new javax.swing.JLabel();
        scpKundengruppe = new javax.swing.JScrollPane();
        lstKundengruppe = new DroppedBeansList();
        btnRemKundengruppe = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 10, 10));
        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(BillingKundeEditor.class, "BillingKundeEditor.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jLabel1, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.name}"),
                txtName,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(txtName, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel2,
            org.openide.util.NbBundle.getMessage(BillingKundeEditor.class, "BillingKundeEditor.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jLabel2, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.vertragskennzeichen}"),
                txtVertragskennzeichen,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(txtVertragskennzeichen, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel3,
            org.openide.util.NbBundle.getMessage(BillingKundeEditor.class, "BillingKundeEditor.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jLabel3, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel4,
            org.openide.util.NbBundle.getMessage(BillingKundeEditor.class, "BillingKundeEditor.jLabel4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jLabel4, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.weiterverkaufsvertragskennzeichen}"),
                txtWeiterverkaufsvertragskennzeichen,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(txtWeiterverkaufsvertragskennzeichen, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel5,
            org.openide.util.NbBundle.getMessage(BillingKundeEditor.class, "BillingKundeEditor.jLabel5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jLabel5, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel6,
            org.openide.util.NbBundle.getMessage(BillingKundeEditor.class, "BillingKundeEditor.jLabel6.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jLabel6, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.direktkontakt}"),
                txtDirektkontakt,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(txtDirektkontakt, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel7,
            org.openide.util.NbBundle.getMessage(BillingKundeEditor.class, "BillingKundeEditor.jLabel7.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jLabel7, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.vermessungsstellennummer}"),
                txtVermessungsstellennummer,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(txtVermessungsstellennummer, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.vertragsende}"),
                dcVertragsende,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(((DefaultBindableDateChooser)dcVertragsende).getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(dcVertragsende, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.weiterverkaufsvertragsende}"),
                dcWeiterverkaufsvertragsende,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(((DefaultBindableDateChooser)dcWeiterverkaufsvertragsende).getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(dcWeiterverkaufsvertragsende, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel8,
            org.openide.util.NbBundle.getMessage(BillingKundeEditor.class, "BillingKundeEditor.jLabel8.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jLabel8, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.branche}"),
                cboBranche,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(cboBranche, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel9,
            org.openide.util.NbBundle.getMessage(BillingKundeEditor.class, "BillingKundeEditor.jLabel9.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jLabel9, gridBagConstraints);

        btnRemProduct.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemProduct.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemProductActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(btnRemProduct, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.abrechnungsturnus}"),
                cboAbrechnungsturnus,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(cboAbrechnungsturnus, gridBagConstraints);

        scpProdukte.setMinimumSize(new java.awt.Dimension(400, 200));
        scpProdukte.setPreferredSize(new java.awt.Dimension(400, 200));

        lstProdukte.setModel(new DefaultListModel());

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create(
                "${cidsBean.produkte_arr}");
        org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings
                    .createJListBinding(
                        org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                        this,
                        eLProperty,
                        lstProdukte);
        bindingGroup.addBinding(jListBinding);

        scpProdukte.setViewportView(lstProdukte);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(scpProdukte, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel10,
            org.openide.util.NbBundle.getMessage(BillingKundeEditor.class, "BillingKundeEditor.jLabel10.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jLabel10, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(filler2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel11,
            org.openide.util.NbBundle.getMessage(BillingKundeEditor.class, "BillingKundeEditor.jLabel11.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jLabel11, gridBagConstraints);

        scpKundenLogins.setMinimumSize(new java.awt.Dimension(400, 200));
        scpKundenLogins.setPreferredSize(new java.awt.Dimension(400, 200));

        lstKundenLogins.setModel(new DefaultListModel());

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${kundenLogins}");
        jListBinding = org.jdesktop.swingbinding.SwingBindings.createJListBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                eLProperty,
                lstKundenLogins,
                "kundenLoginsBinding");
        bindingGroup.addBinding(jListBinding);

        scpKundenLogins.setViewportView(lstKundenLogins);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(scpKundenLogins, gridBagConstraints);

        btnRemKundenLogin.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemKundenLogin.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemKundenLoginActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(btnRemKundenLogin, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel12,
            org.openide.util.NbBundle.getMessage(BillingKundeEditor.class, "BillingKundeEditor.jLabel12.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jLabel12, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.name_intern}"),
                txtInternalName,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(txtInternalName, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel13,
            org.openide.util.NbBundle.getMessage(BillingKundeEditor.class, "BillingKundeEditor.jLabel13.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jLabel13, gridBagConstraints);

        scpKundengruppe.setMinimumSize(new java.awt.Dimension(400, 200));
        scpKundengruppe.setPreferredSize(new java.awt.Dimension(400, 200));

        lstKundengruppe.setModel(new DefaultListModel());

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${kundengruppen}");
        jListBinding = org.jdesktop.swingbinding.SwingBindings.createJListBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                eLProperty,
                lstKundengruppe,
                "kundengruppenBinding");
        bindingGroup.addBinding(jListBinding);

        scpKundengruppe.setViewportView(lstKundengruppe);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(scpKundengruppe, gridBagConstraints);

        btnRemKundengruppe.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemKundengruppe.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemKundengruppeActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(btnRemKundengruppe, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemProductActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemProductActionPerformed
        final Object[] selection = lstProdukte.getSelectedValues();

        if (selection != null) {
            final int answer = JOptionPane.showConfirmDialog(
                    StaticSwingTools.getParentFrame(this),
                    "Sollen die Einträge wirklich gelöscht werden?",
                    "Einträge entfernen",
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                for (final Object cidsbean : selection) {
                    cidsBean.getBeanCollectionProperty("produkte_arr").remove((CidsBean)cidsbean);
                }
            }
        }
    } //GEN-LAST:event_btnRemProductActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemKundenLoginActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemKundenLoginActionPerformed
        final Object[] selection = lstKundenLogins.getSelectedValues();

        if (selection != null) {
            final int answer = JOptionPane.showConfirmDialog(
                    StaticSwingTools.getParentFrame(this),
                    "Sollen die Einträge wirklich gelöscht werden?",
                    "Einträge entfernen",
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                for (final Object loginBean : selection) {
                    if (loginBean instanceof CidsBean) {
                        try {
                            ((CidsBean)loginBean).setProperty("kunde", null);
                        } catch (Exception ex) {
                            LOG.error(ex.getMessage(), ex);
                        }
                        kundenLogins.remove((CidsBean)loginBean);
                        touchedKundenLogins.add((CidsBean)loginBean);
                        final Binding kundenLoginsBinding = bindingGroup.getBinding("kundenLoginsBinding");
                        kundenLoginsBinding.unbind();
                        kundenLoginsBinding.bind();
                        cidsBean.setArtificialChangeFlag(true);
                    }
                }
            }
        }
    } //GEN-LAST:event_btnRemKundenLoginActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemKundengruppeActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemKundengruppeActionPerformed
        final Object[] selection = lstKundengruppe.getSelectedValues();

        if (selection != null) {
            final int answer = JOptionPane.showConfirmDialog(
                    StaticSwingTools.getParentFrame(this),
                    "Sollen die Einträge wirklich gelöscht werden?",
                    "Einträge entfernen",
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                for (final Object gruppeBean : selection) {
                    if (gruppeBean instanceof CidsBean) {
                        ((CidsBean)gruppeBean).getBeanCollectionProperty("kunden_arr").remove(cidsBean);
                        kundengruppen.remove((CidsBean)gruppeBean);
                        final Binding kundengruppenBinding = bindingGroup.getBinding("kundengruppenBinding");
                        kundengruppenBinding.unbind();
                        kundengruppenBinding.bind();
                        touchedKundengruppen.add((CidsBean)gruppeBean);
                        cidsBean.setArtificialChangeFlag(true);
                    }
                }
            }
        }
    } //GEN-LAST:event_btnRemKundengruppeActionPerformed

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        bindingGroup.unbind();
        if (cidsBean != null) {
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cidsBean);
            this.cidsBean = cidsBean;
            initKundengruppe();
            initKundenLogins();
            bindingGroup.bind();
        }
    }

    @Override
    public void dispose() {
        bindingGroup.unbind();
    }

    @Override
    public String getTitle() {
        String title = "Kunde";
        final String desc = String.valueOf(cidsBean);
        if (!desc.equalsIgnoreCase("null")) {
            title += ": " + desc;
        }

        return title;
    }

    @Override
    public void setTitle(final String title) {
    }

    @Override
    public void editorClosed(final EditorClosedEvent event) {
        if (event.getStatus() == EditorSaveStatus.SAVE_SUCCESS) {
            for (final CidsBean gruppenBean : touchedKundengruppen) {
                // check if the customer bean should be added to the Kundengruppe
                // this should happen if: Kundengruppe is shown in the list and was touched
                if (kundengruppen.contains(gruppenBean)) {
                    final List<CidsBean> kunden = gruppenBean.getBeanCollectionProperty("kunden_arr");
                    kunden.add(event.getSavedBean());
                }

                // persist the bean, this should also happen if the bean was only touched. This means, that the
                // customer bean was removed.
                try {
                    gruppenBean.persist(getConnectionContext());
                } catch (Exception ex) {
                    Log.error(ex.getMessage(), ex);
                }
            }

            for (final CidsBean loginBean : touchedKundenLogins) {
                // check if the customer bean should be added to the KundenLogin
                // this should happen if: KundenLogin is shown in the list and was touched
                if (kundenLogins.contains(loginBean)) {
                    try {
                        loginBean.setProperty("kunde", event.getSavedBean());
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }

                // persist the bean, this should also happen if the bean was only touched. This means, that the
                // customer bean was removed.
                try {
                    loginBean.persist(getConnectionContext());
                } catch (Exception ex) {
                    Log.error(ex.getMessage(), ex);
                }
            }

            // reload the tree
            try {
                final TreePath selectionPath = ComponentRegistry.getRegistry().getCatalogueTree().getSelectionPath();
                if ((selectionPath != null) && (selectionPath.getPath().length > 0)) {
                    final RootTreeNode rootTreeNode = new RootTreeNode(SessionManager.getProxy().getRoots(
                                getConnectionContext()),
                            getConnectionContext());
                    ((DefaultTreeModel)ComponentRegistry.getRegistry().getCatalogueTree().getModel()).setRoot(
                        rootTreeNode);
                    ((DefaultTreeModel)ComponentRegistry.getRegistry().getCatalogueTree().getModel()).reload();
                    ComponentRegistry.getRegistry().getCatalogueTree().exploreSubtree(selectionPath);
                }
            } catch (ConnectionException ex) {
                LOG.error("Error while refreshing the tree", ex); // NOI18N
            } catch (RuntimeException ex) {
                LOG.error("Error while refreshing the tree", ex); // NOI18N
            }
        }
    }

    @Override
    public boolean prepareForSave() {
        if (txtInternalName.getText().isEmpty()) {
            final String message = NbBundle.getMessage(
                    BillingKundeEditor.class,
                    "BillingKundeEditor.prepareForSave().dialog.message");
            final String title = NbBundle.getMessage(
                    BillingKundeEditor.class,
                    "BillingKundeEditor.prepareForSave().dialog.title");
            JOptionPane.showMessageDialog(
                this,
                message,
                title,
                JOptionPane.ERROR_MESSAGE);
            return false;
        } else {
            return true;
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
        DevelopmentTools.createEditorInFrameFromRMIConnectionOnLocalhost(
            "WUNDA_BLAU",
            "Administratoren",
            "admin",
            "kif",
            "billing_kunde",
            15,
            1280,
            1024);
    }

    /**
     * DOCUMENT ME!
     */
    private void initKundengruppe() {
        final MetaClass mc = ClassCacheMultiple.getMetaClass(DOMAIN, KUNDENGRUPPE_TABLE);
        kundengruppen.clear();
        final String query = "SELECT "
                    + mc.getID()
                    + ", billing_kundengruppe."
                    + mc.getPrimaryKey()
                    + " FROM "
                    + mc.getTableName()
                    + " JOIN "
                    + " billing_kunde_kundengruppe_array "
                    + " ON "
                    + " billing_kundengruppe_reference = billing_kundengruppe.id"
                    + " WHERE "
                    + " kunde = " + cidsBean.getProperty("id").toString()
                    + " order by billing_kundengruppe.name";
        try {
            final MetaObject[] metaObjects = SessionManager.getProxy()
                        .getMetaObjectByQuery(SessionManager.getSession().getUser(),
                            query,
                            getConnectionContext());
            for (final MetaObject mo : metaObjects) {
                final CidsBean cb = mo.getBean();
                kundengruppen.add(cb);
            }
        } catch (ConnectionException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void initKundenLogins() {
        final MetaClass mc = ClassCacheMultiple.getMetaClass(DOMAIN, "billing_kunden_logins");
        kundenLogins.clear();
        final String query = "SELECT "
                    + mc.getID()
                    + ", billing_kunden_logins."
                    + mc.getPrimaryKey()
                    + " FROM "
                    + mc.getTableName()
                    + " WHERE "
                    + " kunde = " + cidsBean.getProperty("id").toString()
                    + " order by billing_kunden_logins.name";
        try {
            final MetaObject[] metaObjects = SessionManager.getProxy()
                        .getMetaObjectByQuery(SessionManager.getSession().getUser(),
                            query,
                            getConnectionContext());
            for (final MetaObject mo : metaObjects) {
                final CidsBean cb = mo.getBean();
                kundenLogins.add(cb);
            }
        } catch (ConnectionException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List getKundengruppen() {
        return kundengruppen;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kundengruppen  DOCUMENT ME!
     */
    public void setKundengruppen(final List kundengruppen) {
        this.kundengruppen = kundengruppen;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> getKundenLogins() {
        return kundenLogins;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kundenLogins  DOCUMENT ME!
     */
    public void setKundenLogins(final List<CidsBean> kundenLogins) {
        this.kundenLogins = kundenLogins;
    }

    @Override
    public final ClientConnectionContext getConnectionContext() {
        return connectionContext;
    }

    @Override
    public void setConnectionContext(final ClientConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class DroppedBeansList extends JList implements CidsBeanDropListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void beansDropped(final ArrayList<CidsBean> beans) {
            final List<CidsBean> productsOfTheCustomer = cidsBean.getBeanCollectionProperty("produkte_arr");
            for (final CidsBean bean : beans) {
                if (bean.getMetaObject().getMetaClass().getTableName().equalsIgnoreCase("billing_produkt")
                            && !productsOfTheCustomer.contains(bean)) {
                    productsOfTheCustomer.add(bean);
                }
                if (bean.getMetaObject().getMetaClass().getTableName().equalsIgnoreCase("billing_kunden_logins")
                            && !kundenLogins.contains(bean)) {
                    kundenLogins.add(bean);
                    touchedKundenLogins.add(bean);
                    final Binding kundenLoginsBinding = bindingGroup.getBinding("kundenLoginsBinding");
                    kundenLoginsBinding.unbind();
                    kundenLoginsBinding.bind();
                    cidsBean.setArtificialChangeFlag(true);
                }
                if (bean.getMetaObject().getMetaClass().getTableName().equalsIgnoreCase("billing_kundengruppe")
                            && !kundengruppen.contains(bean)) {
                    kundengruppen.add(bean);
                    touchedKundengruppen.add(bean);
                    final Binding kundengruppenBinding = bindingGroup.getBinding("kundengruppenBinding");
                    kundengruppenBinding.unbind();
                    kundengruppenBinding.bind();
                    cidsBean.setArtificialChangeFlag(true);
                }
            }
        }
    }
}
