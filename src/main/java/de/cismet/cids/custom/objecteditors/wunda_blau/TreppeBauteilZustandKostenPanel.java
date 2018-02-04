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

import org.openide.util.Exceptions;

import java.awt.Component;

import javax.swing.plaf.basic.BasicSpinnerUI;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class TreppeBauteilZustandKostenPanel extends javax.swing.JPanel implements CidsBeanStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final double[][][] MATRIX_DSV = {
            // V=0, V=1, V=2, V=3, V=4
            {                                // D=0
                { 1.0, 1.1, 2.0, 2.5, 4.0 }, // S=0
                { 1.2, 1.3, 2.1, 2.6, 4.0 }, // S=1
                { 2.1, 2.2, 2.3, 2.7, 4.0 }, // S=2
                { 3.0, 3.2, 3.4, 3.6, 4.0 }, // S=3
                { 4.0, 4.0, 4.0, 4.0, 4.0 }  // S=4
            },
            {                                // D=1
                { 1.1, 1.3, 2.1, 2.6, 4.0 }, // S=0
                { 1.5, 1.7, 2.2, 2.7, 4.0 }, // S=1
                { 2.2, 2.3, 2.4, 2.8, 4.0 }, // S=2
                { 3.1, 3.3, 3.5, 3.7, 4.0 }, // S=3
                { 4.0, 4.0, 4.0, 4.0, 4.0 }  // S=4
            },
            {                                // D=2
                { 1.8, 2.1, 2.2, 2.7, 4.0 }, // S=0
                { 2.2, 2.3, 2.4, 2.8, 4.0 }, // S=1
                { 2.3, 2.5, 2.6, 2.9, 4.0 }, // S=2
                { 3.2, 3.4, 3.6, 3.8, 4.0 }, // S=3
                { 4.0, 4.0, 4.0, 4.0, 4.0 }  // S=4
            },
            {                                // D=3
                { 2.5, 2.6, 2.7, 2.8, 4.0 }, // S=0
                { 2.7, 2.8, 2.9, 3.0, 4.0 }, // S=1
                { 2.8, 3.0, 3.1, 3.2, 4.0 }, // S=2
                { 3.3, 3.5, 3.7, 3.8, 4.0 }, // S=3
                { 4.0, 4.0, 4.0, 4.0, 4.0 }  // S=4
            },
            {                                // D=4
                { 3.0, 3.1, 3.2, 3.3, 4.0 }, // S=0
                { 3.2, 3.3, 3.4, 3.5, 4.0 }, // S=1
                { 3.3, 3.5, 3.6, 3.7, 4.0 }, // S=2
                { 3.3, 3.5, 3.7, 3.8, 4.0 }, // S=3
                { 3.4, 3.6, 3.8, 4.0, 4.0 }  // S=4
            }
        };
    private static final BasicSpinnerUI NO_BUTTONS_SPINNER_UI = new BasicSpinnerUI() {

            @Override
            protected Component createNextButton() {
                return null;
            }

            @Override
            protected Component createPreviousButton() {
                return null;
            }
        };

    //~ Instance fields --------------------------------------------------------

    private final boolean editable;
    private final boolean manual;
    private CidsBean cidsBean;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JSpinner jSpinner2;
    private javax.swing.JSpinner jSpinner3;
    private javax.swing.JSpinner jSpinner4;
    private javax.swing.JSpinner jSpinner5;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel lblHeaderAllgemein;
    private javax.swing.JLabel lblHeaderAllgemein1;
    private de.cismet.tools.gui.RoundedPanel panZusammenfassung;
    private de.cismet.tools.gui.RoundedPanel panZusammenfassung1;
    private javax.swing.JPanel panZusammenfassungContent;
    private javax.swing.JPanel panZusammenfassungContent1;
    private de.cismet.tools.gui.SemiRoundedPanel panZusammenfassungTitle;
    private de.cismet.tools.gui.SemiRoundedPanel panZusammenfassungTitle1;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TreppeBauteilZustandKostenPanel object.
     */
    public TreppeBauteilZustandKostenPanel() {
        this(false, false);
    }

    /**
     * Creates a new TreppeBauteilZustandKostenPanel object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public TreppeBauteilZustandKostenPanel(final boolean editable) {
        this(editable, false);
    }

    /**
     * Creates new form TreppeBauteilZustandKostenPanel.
     *
     * @param  editable  DOCUMENT ME!
     * @param  manual    DOCUMENT ME!
     */
    public TreppeBauteilZustandKostenPanel(final boolean editable, final boolean manual) {
        this.editable = editable;
        this.manual = manual;
        initComponents();

        RendererTools.makeDoubleSpinnerWithoutButtons(jSpinner1, 2);
        if (!editable) {
            RendererTools.makeReadOnly(jSpinner1);
            RendererTools.makeReadOnly(jSpinner2);
            RendererTools.makeReadOnly(jSpinner3);
            RendererTools.makeReadOnly(jSpinner4);
            RendererTools.makeReadOnly(jSpinner5);
            RendererTools.makeReadOnly(jTextArea1);
//            RendererTools.jSpinnerShouldLookLikeLabel(jSpinner1);
//            RendererTools.jSpinnerShouldLookLikeLabel(jSpinner2);
//            RendererTools.jSpinnerShouldLookLikeLabel(jSpinner3);
//            RendererTools.jSpinnerShouldLookLikeLabel(jSpinner4);
//            RendererTools.jSpinnerShouldLookLikeLabel(jSpinner5);
        }

        jSpinner2.setVisible(!manual);
        jSpinner3.setVisible(!manual);
        jSpinner4.setVisible(!manual);
        jLabel8.setVisible(manual);
        jLabel9.setVisible(manual);
        jLabel10.setVisible(manual);
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

        panZusammenfassung = new de.cismet.tools.gui.RoundedPanel();
        panZusammenfassungTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeaderAllgemein = new javax.swing.JLabel();
        panZusammenfassungContent = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jLabel7 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        panZusammenfassung1 = new de.cismet.tools.gui.RoundedPanel();
        panZusammenfassungTitle1 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeaderAllgemein1 = new javax.swing.JLabel();
        panZusammenfassungContent1 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jSpinner2 = new javax.swing.JSpinner();
        jSpinner3 = new javax.swing.JSpinner();
        jSpinner4 = new javax.swing.JSpinner();
        jSpinner5 = new javax.swing.JSpinner();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        panZusammenfassung.setLayout(new java.awt.GridBagLayout());

        panZusammenfassungTitle.setBackground(new java.awt.Color(51, 51, 51));
        panZusammenfassungTitle.setLayout(new java.awt.FlowLayout());

        lblHeaderAllgemein.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblHeaderAllgemein,
            org.openide.util.NbBundle.getMessage(
                TreppeBauteilZustandKostenPanel.class,
                "TreppeBauteilZustandKostenPanel.lblHeaderAllgemein.text")); // NOI18N
        panZusammenfassungTitle.add(lblHeaderAllgemein);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        panZusammenfassung.add(panZusammenfassungTitle, gridBagConstraints);

        panZusammenfassungContent.setOpaque(false);
        panZusammenfassungContent.setLayout(new java.awt.GridBagLayout());

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel5,
            org.openide.util.NbBundle.getMessage(
                TreppeBauteilZustandKostenPanel.class,
                "TreppeBauteilZustandKostenPanel.jLabel5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 10);
        jPanel2.add(jLabel5, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel6,
            org.openide.util.NbBundle.getMessage(
                TreppeBauteilZustandKostenPanel.class,
                "TreppeBauteilZustandKostenPanel.jLabel6.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 10);
        jPanel2.add(jLabel6, gridBagConstraints);

        jTextArea1.setColumns(25);
        jTextArea1.setLineWrap(true);
        jTextArea1.setWrapStyleWord(true);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.sanierungsmassnahmen}"),
                jTextArea1,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(jTextArea1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 10, 0);
        jPanel2.add(jScrollPane1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 45, 0, 45);
        jPanel2.add(filler2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel7,
            org.openide.util.NbBundle.getMessage(
                TreppeBauteilZustandKostenPanel.class,
                "TreppeBauteilZustandKostenPanel.jLabel7.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel2.add(jLabel7, gridBagConstraints);

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, null, 0.0d));
        jSpinner1.setUI(new TreppeEditor.NoButtonsSpinnerUI());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kosten}"),
                jSpinner1,
                org.jdesktop.beansbinding.BeanProperty.create("value"));
        binding.setSourceNullValue(0d);
        binding.setSourceUnreadableValue(0d);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel2.add(jSpinner1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panZusammenfassungContent.add(jPanel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panZusammenfassung.add(panZusammenfassungContent, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(panZusammenfassung, gridBagConstraints);

        panZusammenfassung1.setLayout(new java.awt.GridBagLayout());

        panZusammenfassungTitle1.setBackground(new java.awt.Color(51, 51, 51));
        panZusammenfassungTitle1.setLayout(new java.awt.FlowLayout());

        lblHeaderAllgemein1.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblHeaderAllgemein1,
            org.openide.util.NbBundle.getMessage(
                TreppeBauteilZustandKostenPanel.class,
                "TreppeBauteilZustandKostenPanel.lblHeaderAllgemein1.text")); // NOI18N
        panZusammenfassungTitle1.add(lblHeaderAllgemein1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        panZusammenfassung1.add(panZusammenfassungTitle1, gridBagConstraints);

        panZusammenfassungContent1.setOpaque(false);
        panZusammenfassungContent1.setLayout(new java.awt.GridBagLayout());

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(
                TreppeBauteilZustandKostenPanel.class,
                "TreppeBauteilZustandKostenPanel.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 10);
        jPanel1.add(jLabel1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel2,
            org.openide.util.NbBundle.getMessage(
                TreppeBauteilZustandKostenPanel.class,
                "TreppeBauteilZustandKostenPanel.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 10);
        jPanel1.add(jLabel2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel3,
            org.openide.util.NbBundle.getMessage(
                TreppeBauteilZustandKostenPanel.class,
                "TreppeBauteilZustandKostenPanel.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 10);
        jPanel1.add(jLabel3, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel4,
            org.openide.util.NbBundle.getMessage(
                TreppeBauteilZustandKostenPanel.class,
                "TreppeBauteilZustandKostenPanel.jLabel4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 10);
        jPanel1.add(jLabel4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 10, 20);
        jPanel1.add(filler1, gridBagConstraints);

        jSpinner2.setModel(new javax.swing.SpinnerNumberModel(-1, -1, 4, 1));
        jSpinner2.setUI(new TreppeEditor.NoButtonsSpinnerUI());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.standsicherheit}"),
                jSpinner2,
                org.jdesktop.beansbinding.BeanProperty.create("value"));
        binding.setSourceNullValue(-1);
        binding.setSourceUnreadableValue(-1);
        bindingGroup.addBinding(binding);

        jSpinner2.addChangeListener(new javax.swing.event.ChangeListener() {

                @Override
                public void stateChanged(final javax.swing.event.ChangeEvent evt) {
                    jSpinner2StateChanged(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel1.add(jSpinner2, gridBagConstraints);

        jSpinner3.setModel(new javax.swing.SpinnerNumberModel(-1, -1, 4, 1));
        jSpinner3.setUI(new TreppeEditor.NoButtonsSpinnerUI());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.verkehrssicherheit}"),
                jSpinner3,
                org.jdesktop.beansbinding.BeanProperty.create("value"));
        binding.setSourceNullValue(-1);
        binding.setSourceUnreadableValue(-1);
        bindingGroup.addBinding(binding);

        jSpinner3.addChangeListener(new javax.swing.event.ChangeListener() {

                @Override
                public void stateChanged(final javax.swing.event.ChangeEvent evt) {
                    jSpinner3StateChanged(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel1.add(jSpinner3, gridBagConstraints);

        jSpinner4.setModel(new javax.swing.SpinnerNumberModel(-1, -1, 4, 1));
        jSpinner4.setUI(new TreppeEditor.NoButtonsSpinnerUI());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.dauerhaftigkeit}"),
                jSpinner4,
                org.jdesktop.beansbinding.BeanProperty.create("value"));
        binding.setSourceNullValue(-1);
        binding.setSourceUnreadableValue(-1);
        bindingGroup.addBinding(binding);

        jSpinner4.addChangeListener(new javax.swing.event.ChangeListener() {

                @Override
                public void stateChanged(final javax.swing.event.ChangeEvent evt) {
                    jSpinner4StateChanged(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel1.add(jSpinner4, gridBagConstraints);

        jSpinner5.setModel(new javax.swing.SpinnerNumberModel(-1, -1, 4, 1));
        jSpinner5.setUI(new TreppeEditor.NoButtonsSpinnerUI());
        jSpinner5.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gesamt}"),
                jSpinner5,
                org.jdesktop.beansbinding.BeanProperty.create("value"));
        binding.setSourceNullValue(-1);
        binding.setSourceUnreadableValue(-1);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel1.add(jSpinner5, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel8,
            org.openide.util.NbBundle.getMessage(
                TreppeBauteilZustandKostenPanel.class,
                "TreppeBauteilZustandKostenPanel.jLabel8.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel1.add(jLabel8, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel9,
            org.openide.util.NbBundle.getMessage(
                TreppeBauteilZustandKostenPanel.class,
                "TreppeBauteilZustandKostenPanel.jLabel9.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel1.add(jLabel9, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel10,
            org.openide.util.NbBundle.getMessage(
                TreppeBauteilZustandKostenPanel.class,
                "TreppeBauteilZustandKostenPanel.jLabel10.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel1.add(jLabel10, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panZusammenfassungContent1.add(jPanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panZusammenfassung1.add(panZusammenfassungContent1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        add(panZusammenfassung1, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jSpinner2StateChanged(final javax.swing.event.ChangeEvent evt) { //GEN-FIRST:event_jSpinner2StateChanged
        recalculateGesamt();
    }                                                                             //GEN-LAST:event_jSpinner2StateChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jSpinner3StateChanged(final javax.swing.event.ChangeEvent evt) { //GEN-FIRST:event_jSpinner3StateChanged
        recalculateGesamt();
    }                                                                             //GEN-LAST:event_jSpinner3StateChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jSpinner4StateChanged(final javax.swing.event.ChangeEvent evt) { //GEN-FIRST:event_jSpinner4StateChanged
        recalculateGesamt();
    }                                                                             //GEN-LAST:event_jSpinner4StateChanged

    /**
     * DOCUMENT ME!
     */
    private void recalculateGesamt() {
        final Integer standsicherheit = (Integer)cidsBean.getProperty("standsicherheit");
        final Integer verkehrssicherheit = (Integer)cidsBean.getProperty("verkehrssicherheit");
        final Integer dauerhaftigkeit = (Integer)cidsBean.getProperty("dauerhaftigkeit");

        try {
            if ((standsicherheit != null) && (verkehrssicherheit != null) && (dauerhaftigkeit != null)) {
                final double zustand = MATRIX_DSV[dauerhaftigkeit][standsicherheit][verkehrssicherheit];
                cidsBean.setProperty("gesamt", zustand);
            } else {
                cidsBean.setProperty("gesamt", null);
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        bindingGroup.unbind();
        this.cidsBean = cidsBean;
        bindingGroup.bind();

        if ((cidsBean != null) && editable) {
            recalculateGesamt();
        }
    }
}
