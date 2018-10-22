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

import org.apache.log4j.Logger;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultBindableJTextField;
import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultCustomObjectEditor;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class BillingStammkundeEditor extends javax.swing.JPanel implements CidsBeanRenderer, ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(BillingStammkundeEditor.class);
    public static final String DOMAIN = "WUNDA_BLAU";

    //~ Instance fields --------------------------------------------------------

    private CidsBean cidsBean;
    private final boolean editable;
    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboSegment;
    private javax.swing.JComboBox cboTyp;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea txtAdresse;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextArea txtHinweise;
    private javax.swing.JTextField txtKunde;
    private javax.swing.JTextField txtLaufendeNummer;
    private javax.swing.JTextField txtMobiltelefon;
    private javax.swing.JTextField txtTelefon;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BillingStammkundeEditor object.
     */
    public BillingStammkundeEditor() {
        this(true);
    }

    /**
     * Creates a new BillingStammkundeEditor object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public BillingStammkundeEditor(final boolean editable) {
        this.editable = editable;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        initComponents();

        if (!editable) {
            RendererTools.makeReadOnly(txtKunde);
            RendererTools.makeReadOnly(txtLaufendeNummer);
            RendererTools.makeReadOnly(cboSegment);
            RendererTools.makeReadOnly(cboTyp);
            RendererTools.makeReadOnly(txtAdresse);
            RendererTools.makeReadOnly(txtEmail);
            RendererTools.makeReadOnly(txtTelefon);
            RendererTools.makeReadOnly(txtMobiltelefon);
            RendererTools.makeReadOnly(txtHinweise);
        }
    }

    /**
     * DOCUMENT ME!
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtKunde = new DefaultBindableJTextField();
        jLabel7 = new javax.swing.JLabel();
        txtLaufendeNummer = new DefaultBindableJTextField();
        jLabel8 = new javax.swing.JLabel();
        cboSegment = new DefaultBindableReferenceCombo();
        jLabel9 = new javax.swing.JLabel();
        cboTyp = new DefaultBindableReferenceCombo();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtAdresse = new javax.swing.JTextArea();
        jLabel17 = new javax.swing.JLabel();
        txtEmail = new DefaultBindableJTextField();
        jLabel14 = new javax.swing.JLabel();
        txtTelefon = new DefaultBindableJTextField();
        jLabel15 = new javax.swing.JLabel();
        txtMobiltelefon = new DefaultBindableJTextField();
        jLabel16 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtHinweise = new javax.swing.JTextArea();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));

        setLayout(new java.awt.GridBagLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 10, 10));
        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(
                BillingStammkundeEditor.class,
                "BillingStammkundeEditor.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jLabel1, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kunde}"),
                txtKunde,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(txtKunde, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel7,
            org.openide.util.NbBundle.getMessage(
                BillingStammkundeEditor.class,
                "BillingStammkundeEditor.jLabel7.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jLabel7, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.laufende_nummer}"),
                txtLaufendeNummer,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(txtLaufendeNummer, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel8,
            org.openide.util.NbBundle.getMessage(
                BillingStammkundeEditor.class,
                "BillingStammkundeEditor.jLabel8.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jLabel8, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.segment}"),
                cboSegment,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(cboSegment, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel9,
            org.openide.util.NbBundle.getMessage(
                BillingStammkundeEditor.class,
                "BillingStammkundeEditor.jLabel9.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jLabel9, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.typ}"),
                cboTyp,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(cboTyp, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel18,
            org.openide.util.NbBundle.getMessage(
                BillingStammkundeEditor.class,
                "BillingStammkundeEditor.jLabel18.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jLabel18, gridBagConstraints);

        txtAdresse.setColumns(20);
        txtAdresse.setRows(5);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.adresse}"),
                txtAdresse,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane2.setViewportView(txtAdresse);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jScrollPane2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel17,
            org.openide.util.NbBundle.getMessage(
                BillingStammkundeEditor.class,
                "BillingStammkundeEditor.jLabel17.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jLabel17, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.email}"),
                txtEmail,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(txtEmail, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel14,
            org.openide.util.NbBundle.getMessage(
                BillingStammkundeEditor.class,
                "BillingStammkundeEditor.jLabel14.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jLabel14, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.telefon}"),
                txtTelefon,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(txtTelefon, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel15,
            org.openide.util.NbBundle.getMessage(
                BillingStammkundeEditor.class,
                "BillingStammkundeEditor.jLabel15.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jLabel15, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.mobiltelefon}"),
                txtMobiltelefon,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(txtMobiltelefon, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel16,
            org.openide.util.NbBundle.getMessage(
                BillingStammkundeEditor.class,
                "BillingStammkundeEditor.jLabel16.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jLabel16, gridBagConstraints);

        txtHinweise.setColumns(20);
        txtHinweise.setRows(5);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.hinweise}"),
                txtHinweise,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(txtHinweise);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jScrollPane1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(filler2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

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
                cidsBean,
                getConnectionContext());
            this.cidsBean = cidsBean;
            bindingGroup.bind();
        }
    }

    @Override
    public void dispose() {
        bindingGroup.unbind();
    }

    @Override
    public String getTitle() {
        String title = "Stammkunde";
        final String desc = String.valueOf(cidsBean);
        if (!desc.equalsIgnoreCase("null")) {
            title += ": " + desc;
        }

        return title;
    }

    @Override
    public void setTitle(final String title) {
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
            "billing_stammkunde",
            1,
            1280,
            1024);
    }

    @Override
    public final ConnectionContext getConnectionContext() {
        return connectionContext;
    }
}
