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

import org.apache.log4j.Logger;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.wunda_blau.search.server.TreppeMaterialArtLightweightSearch;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;
import de.cismet.cids.dynamics.Disposable;

import de.cismet.cids.editors.DefaultCustomObjectEditor;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class TreppePodestPanel extends javax.swing.JPanel implements CidsBeanStore, Disposable {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(TreppePodestPanel.class);

    //~ Instance fields --------------------------------------------------------

    private CidsBean cidsBean;
    private TreppePodestePanel parent;
    private final boolean editable;
    private final TreppeMaterialArtLightweightSearch materialArtSearch1;
    private final TreppeMaterialArtLightweightSearch materialArtSearch2;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRemoveArt1;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo defaultBindableReferenceCombo1;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo defaultBindableReferenceCombo4;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo defaultBindableReferenceCombo5;
    private de.cismet.cids.editors.FastBindableReferenceCombo fastBindableReferenceCombo1;
    private de.cismet.cids.editors.FastBindableReferenceCombo fastBindableReferenceCombo2;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel41;
    private javax.swing.JPanel jPanel42;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextField jTextField19;
    private javax.swing.JLabel lblHeaderAllgemein3;
    private de.cismet.tools.gui.RoundedPanel panAllgemein2;
    private javax.swing.JPanel panBeschreibungContent2;
    private de.cismet.tools.gui.SemiRoundedPanel panBeschreibungTitle2;
    private de.cismet.cids.custom.objecteditors.wunda_blau.TreppeBauteilZustandKostenPanel
        treppeBauteilZustandKostenPanel7;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TreppePodestPanel object.
     */
    public TreppePodestPanel() {
        this(true);
    }

    /**
     * Creates new form TreppePodestPanel.
     *
     * @param  editable  DOCUMENT ME!
     */
    public TreppePodestPanel(final boolean editable) {
        this.editable = editable;
        this.materialArtSearch1 = new TreppeMaterialArtLightweightSearch(
                TreppeMaterialArtLightweightSearch.SearchFor.PODEST,
                "%1$2s",
                new String[] { "NAME" });
        this.materialArtSearch2 = new TreppeMaterialArtLightweightSearch(
                TreppeMaterialArtLightweightSearch.SearchFor.PODEST,
                "%1$2s",
                new String[] { "NAME" });

        materialArtSearch1.setTypId(1);
        materialArtSearch2.setTypId(2);

        initComponents();
        try {
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                CidsBean.createNewCidsBeanFromTableName("WUNDA_BLAU", "TREPPE_PODEST"));
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
        if (!editable) {
            RendererTools.makeReadOnly(jCheckBox1);
            RendererTools.makeReadOnly(jCheckBox2);
            RendererTools.makeReadOnly(defaultBindableReferenceCombo1);
            RendererTools.makeReadOnly(defaultBindableReferenceCombo4);
            RendererTools.makeReadOnly(defaultBindableReferenceCombo5);
            RendererTools.makeReadOnly(fastBindableReferenceCombo1);
            RendererTools.makeReadOnly(fastBindableReferenceCombo2);
            RendererTools.makeReadOnly(jTextArea2);
            RendererTools.makeReadOnly(jTextField19);
        }
        btnRemoveArt1.setVisible(editable);

        fastBindableReferenceCombo1.setMetaClassFromTableName("WUNDA_BLAU", "treppe_podest_material_art");
        fastBindableReferenceCombo2.setMetaClassFromTableName("WUNDA_BLAU", "treppe_podest_material_art");
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

        jPanel41 = new javax.swing.JPanel();
        panAllgemein2 = new de.cismet.tools.gui.RoundedPanel();
        panBeschreibungTitle2 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeaderAllgemein3 = new javax.swing.JLabel();
        panBeschreibungContent2 = new javax.swing.JPanel();
        jPanel42 = new javax.swing.JPanel();
        jLabel57 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox1 = new javax.swing.JCheckBox();
        jTextField19 = new javax.swing.JTextField();
        jLabel71 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel56 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        defaultBindableReferenceCombo1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        fastBindableReferenceCombo1 = new de.cismet.cids.editors.FastBindableReferenceCombo(
                materialArtSearch1,
                materialArtSearch1.getRepresentationPattern(),
                materialArtSearch1.getRepresentationFields());
        fastBindableReferenceCombo2 = new de.cismet.cids.editors.FastBindableReferenceCombo(
                materialArtSearch2,
                materialArtSearch2.getRepresentationPattern(),
                materialArtSearch2.getRepresentationFields());
        jSeparator3 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        defaultBindableReferenceCombo4 = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        jLabel3 = new javax.swing.JLabel();
        defaultBindableReferenceCombo5 = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        jLabel55 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jPanel4 = new javax.swing.JPanel();
        treppeBauteilZustandKostenPanel7 =
            new de.cismet.cids.custom.objecteditors.wunda_blau.TreppeBauteilZustandKostenPanel(editable);
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        btnRemoveArt1 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        jPanel41.setOpaque(false);
        jPanel41.setLayout(new java.awt.GridBagLayout());

        panAllgemein2.setLayout(new java.awt.GridBagLayout());

        panBeschreibungTitle2.setBackground(new java.awt.Color(51, 51, 51));
        panBeschreibungTitle2.setLayout(new java.awt.FlowLayout());

        lblHeaderAllgemein3.setForeground(new java.awt.Color(255, 255, 255));
        lblHeaderAllgemein3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblHeaderAllgemein3,
            org.openide.util.NbBundle.getMessage(
                TreppePodestPanel.class,
                "TreppePodestPanel.lblHeaderAllgemein3.text")); // NOI18N
        panBeschreibungTitle2.add(lblHeaderAllgemein3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        panAllgemein2.add(panBeschreibungTitle2, gridBagConstraints);

        panBeschreibungContent2.setOpaque(false);
        panBeschreibungContent2.setLayout(new java.awt.GridBagLayout());

        jPanel42.setOpaque(false);
        jPanel42.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel57,
            org.openide.util.NbBundle.getMessage(TreppePodestPanel.class, "TreppePodestPanel.jLabel57.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        jPanel42.add(jLabel57, gridBagConstraints);

        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jCheckBox2,
            org.openide.util.NbBundle.getMessage(TreppePodestPanel.class, "TreppePodestPanel.jCheckBox2.text")); // NOI18N
        jCheckBox2.setContentAreaFilled(false);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.antrittspodest_oben}"),
                jCheckBox2,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel3.add(jCheckBox2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jCheckBox1,
            org.openide.util.NbBundle.getMessage(TreppePodestPanel.class, "TreppePodestPanel.jCheckBox1.text")); // NOI18N
        jCheckBox1.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.austrittspodest_unten}"),
                jCheckBox1,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 10);
        jPanel3.add(jCheckBox1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.nummer}"),
                jTextField19,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(jTextField19, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel71,
            org.openide.util.NbBundle.getMessage(TreppePodestPanel.class, "TreppePodestPanel.jLabel71.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 10);
        jPanel3.add(jLabel71, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel42.add(jPanel3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel42.add(jSeparator2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel56,
            org.openide.util.NbBundle.getMessage(TreppePodestPanel.class, "TreppePodestPanel.jLabel56.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        jPanel42.add(jLabel56, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(TreppePodestPanel.class, "TreppePodestPanel.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        jPanel42.add(jLabel1, gridBagConstraints);

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridBagLayout());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.material}"),
                defaultBindableReferenceCombo1,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        defaultBindableReferenceCombo1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    defaultBindableReferenceCombo1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel2.add(defaultBindableReferenceCombo1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.material_art_1}"),
                fastBindableReferenceCombo1,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 0);
        jPanel2.add(fastBindableReferenceCombo1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.material_art_2}"),
                fastBindableReferenceCombo2,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 0);
        jPanel2.add(fastBindableReferenceCombo2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel42.add(jPanel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel42.add(jSeparator3, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel2,
            org.openide.util.NbBundle.getMessage(TreppePodestPanel.class, "TreppePodestPanel.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        jPanel42.add(jLabel2, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.entwaesserung_art}"),
                defaultBindableReferenceCombo4,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel42.add(defaultBindableReferenceCombo4, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel3,
            org.openide.util.NbBundle.getMessage(TreppePodestPanel.class, "TreppePodestPanel.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        jPanel42.add(jLabel3, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fugen}"),
                defaultBindableReferenceCombo5,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel42.add(defaultBindableReferenceCombo5, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel55,
            org.openide.util.NbBundle.getMessage(TreppePodestPanel.class, "TreppePodestPanel.jLabel55.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        jPanel42.add(jLabel55, gridBagConstraints);

        jTextArea2.setColumns(20);
        jTextArea2.setLineWrap(true);
        jTextArea2.setWrapStyleWord(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bemerkung}"),
                jTextArea2,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane2.setViewportView(jTextArea2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel42.add(jScrollPane2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 20, 0);
        jPanel42.add(filler4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBeschreibungContent2.add(jPanel42, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panAllgemein2.add(panBeschreibungContent2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        jPanel41.add(panAllgemein2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel41.add(filler1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        add(jPanel41, gridBagConstraints);

        jPanel4.setOpaque(false);
        jPanel4.setLayout(new java.awt.GridBagLayout());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.zustand}"),
                treppeBauteilZustandKostenPanel7,
                org.jdesktop.beansbinding.BeanProperty.create("cidsBean"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel4.add(treppeBauteilZustandKostenPanel7, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(filler3, gridBagConstraints);

        btnRemoveArt1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveArt1.setBorderPainted(false);
        btnRemoveArt1.setContentAreaFilled(false);
        btnRemoveArt1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemoveArt1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel4.add(btnRemoveArt1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(jPanel4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(jSeparator1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(filler2, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveArt1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemoveArt1ActionPerformed
        parent.getCidsBeans().remove(cidsBean);
        parent.removePodestPanel(this);
    }                                                                                 //GEN-LAST:event_btnRemoveArt1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void defaultBindableReferenceCombo1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_defaultBindableReferenceCombo1ActionPerformed
        refreshMaterialTyp();
        fastBindableReferenceCombo1.setSelectedItem(null);
        fastBindableReferenceCombo2.setSelectedItem(null);
    }                                                                                                  //GEN-LAST:event_defaultBindableReferenceCombo1ActionPerformed
    /**
     * DOCUMENT ME!
     */
    private void refreshMaterialTyp() {
        materialArtSearch1.setMaterialId((Integer)cidsBean.getProperty("material.id"));
        materialArtSearch2.setMaterialId((Integer)cidsBean.getProperty("material.id"));

        fastBindableReferenceCombo1.refreshModel();
        fastBindableReferenceCombo2.refreshModel();
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        bindingGroup.unbind();
        this.cidsBean = cidsBean;
        if (cidsBean != null) {
            refreshMaterialTyp();
            bindingGroup.bind();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  parent  DOCUMENT ME!
     */
    public void setParent(final TreppePodestePanel parent) {
        this.parent = parent;
    }

    @Override
    public void dispose() {
        bindingGroup.unbind();
    }
}
