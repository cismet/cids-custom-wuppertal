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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.text.DecimalFormat;

import javax.swing.text.NumberFormatter;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;
import de.cismet.cids.dynamics.Disposable;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class TreppeBauteilZustandKostenPanel extends javax.swing.JPanel implements CidsBeanStore, Disposable {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(TreppeBauteilZustandKostenPanel.class);
    private static final TreppeEditor.IntegerToLongConverter CONVERTER_INT = new TreppeEditor.IntegerToLongConverter();
    private static final TreppeEditor.DoubleToLongConverter CONVERTER_DOUBLE = new TreppeEditor.DoubleToLongConverter();

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

    //~ Instance fields --------------------------------------------------------

    private final boolean editable;
    private CidsBean cidsBean;
    private final PropertyChangeListener propChangeListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                final String name = evt.getPropertyName();
                if ("verkehrssicherheit".equalsIgnoreCase(name) || "standsicherheit".equalsIgnoreCase(name)
                            || "dauerhaftigkeit".equalsIgnoreCase(name)) {
                    if (evt.getOldValue() != evt.getNewValue()) {
                        recalculateGesamt();
                    }
                }
            }
        };

    private final NumberFormatter nf = new NumberFormatter(new DecimalFormat("#0"));

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JFormattedTextField jFormattedTextField2;
    private javax.swing.JFormattedTextField jFormattedTextField3;
    private javax.swing.JFormattedTextField jFormattedTextField4;
    private javax.swing.JFormattedTextField jFormattedTextField5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel lblHeaderAllgemein;
    private de.cismet.tools.gui.RoundedPanel panZusammenfassung;
    private javax.swing.JPanel panZusammenfassungContent;
    private de.cismet.tools.gui.SemiRoundedPanel panZusammenfassungTitle;
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

        nf.setMinimum(0);
        nf.setMaximum(4);

        initComponents();

        if (!editable) {
            RendererTools.makeReadOnly(jFormattedTextField1);
            RendererTools.makeReadOnly(jFormattedTextField2);
            RendererTools.makeReadOnly(jFormattedTextField3);
            RendererTools.makeReadOnly(jFormattedTextField4);
            RendererTools.makeReadOnly(jFormattedTextField5);
            RendererTools.makeReadOnly(jTextArea1);
        }

        jLabel1.setVisible(!manual);
        jFormattedTextField1.setVisible(!manual);
        jLabel2.setVisible(!manual);
        jFormattedTextField2.setVisible(!manual);
        jLabel3.setVisible(!manual);
        jFormattedTextField3.setVisible(!manual);
        jLabel4.setVisible(!manual);
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
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jFormattedTextField1 = new javax.swing.JFormattedTextField(nf);
        jFormattedTextField2 = new javax.swing.JFormattedTextField(nf);
        jFormattedTextField3 = new javax.swing.JFormattedTextField(nf);
        jFormattedTextField4 = new javax.swing.JFormattedTextField(nf);
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        jLabel11 = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        jFormattedTextField5 = new javax.swing.JFormattedTextField();

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
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        jPanel2.add(jLabel5, gridBagConstraints);

        jPanel4.setOpaque(false);
        jPanel4.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(
                TreppeBauteilZustandKostenPanel.class,
                "TreppeBauteilZustandKostenPanel.jLabel1.text"));        // NOI18N
        jLabel1.setToolTipText(org.openide.util.NbBundle.getMessage(
                TreppeBauteilZustandKostenPanel.class,
                "TreppeBauteilZustandKostenPanel.jLabel1.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 7);
        jPanel4.add(jLabel1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel2,
            org.openide.util.NbBundle.getMessage(
                TreppeBauteilZustandKostenPanel.class,
                "TreppeBauteilZustandKostenPanel.jLabel2.text"));        // NOI18N
        jLabel2.setToolTipText(org.openide.util.NbBundle.getMessage(
                TreppeBauteilZustandKostenPanel.class,
                "TreppeBauteilZustandKostenPanel.jLabel2.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 7);
        jPanel4.add(jLabel2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel3,
            org.openide.util.NbBundle.getMessage(
                TreppeBauteilZustandKostenPanel.class,
                "TreppeBauteilZustandKostenPanel.jLabel3.text"));        // NOI18N
        jLabel3.setToolTipText(org.openide.util.NbBundle.getMessage(
                TreppeBauteilZustandKostenPanel.class,
                "TreppeBauteilZustandKostenPanel.jLabel3.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 7);
        jPanel4.add(jLabel3, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel4,
            org.openide.util.NbBundle.getMessage(
                TreppeBauteilZustandKostenPanel.class,
                "TreppeBauteilZustandKostenPanel.jLabel4.text"));        // NOI18N
        jLabel4.setToolTipText(org.openide.util.NbBundle.getMessage(
                TreppeBauteilZustandKostenPanel.class,
                "TreppeBauteilZustandKostenPanel.jLabel4.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 7);
        jPanel4.add(jLabel4, gridBagConstraints);

        jFormattedTextField1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
                new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        jFormattedTextField1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jFormattedTextField1.setText(org.openide.util.NbBundle.getMessage(
                TreppeBauteilZustandKostenPanel.class,
                "TreppeBauteilZustandKostenPanel.jFormattedTextField1.text")); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.standsicherheit}"),
                jFormattedTextField1,
                org.jdesktop.beansbinding.BeanProperty.create("value"));
        binding.setConverter(CONVERTER_INT);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel4.add(jFormattedTextField1, gridBagConstraints);

        jFormattedTextField2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
                new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        jFormattedTextField2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jFormattedTextField2.setText(org.openide.util.NbBundle.getMessage(
                TreppeBauteilZustandKostenPanel.class,
                "TreppeBauteilZustandKostenPanel.jFormattedTextField1.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.verkehrssicherheit}"),
                jFormattedTextField2,
                org.jdesktop.beansbinding.BeanProperty.create("value"));
        binding.setConverter(CONVERTER_INT);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel4.add(jFormattedTextField2, gridBagConstraints);

        jFormattedTextField3.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
                new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        jFormattedTextField3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jFormattedTextField3.setText(org.openide.util.NbBundle.getMessage(
                TreppeBauteilZustandKostenPanel.class,
                "TreppeBauteilZustandKostenPanel.jFormattedTextField1.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.dauerhaftigkeit}"),
                jFormattedTextField3,
                org.jdesktop.beansbinding.BeanProperty.create("value"));
        binding.setConverter(CONVERTER_INT);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
        jPanel4.add(jFormattedTextField3, gridBagConstraints);

        jFormattedTextField4.setEditable(false);
        jFormattedTextField4.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
                new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.0"))));
        jFormattedTextField4.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jFormattedTextField4.setText(org.openide.util.NbBundle.getMessage(
                TreppeBauteilZustandKostenPanel.class,
                "TreppeBauteilZustandKostenPanel.jFormattedTextField4.text_1")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gesamt}"),
                jFormattedTextField4,
                org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel4.add(jFormattedTextField4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        jPanel4.add(filler4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        jPanel4.add(filler5, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 20);
        jPanel4.add(filler6, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        jPanel4.add(filler7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(jPanel4, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel11,
            org.openide.util.NbBundle.getMessage(
                TreppeBauteilZustandKostenPanel.class,
                "TreppeBauteilZustandKostenPanel.jLabel11.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        jPanel2.add(jLabel11, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 20, 0);
        jPanel2.add(filler3, gridBagConstraints);

        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(4);
        jTextArea1.setWrapStyleWord(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.sanierungsmassnahmen}"),
                jTextArea1,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(jTextArea1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel2.add(jScrollPane1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel6,
            org.openide.util.NbBundle.getMessage(
                TreppeBauteilZustandKostenPanel.class,
                "TreppeBauteilZustandKostenPanel.jLabel6.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        jPanel2.add(jLabel6, gridBagConstraints);

        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel7,
            org.openide.util.NbBundle.getMessage(
                TreppeBauteilZustandKostenPanel.class,
                "TreppeBauteilZustandKostenPanel.jLabel7.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        jPanel3.add(jLabel7, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 45, 0, 45);
        jPanel3.add(filler2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(filler1, gridBagConstraints);

        jFormattedTextField5.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
                new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        jFormattedTextField5.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jFormattedTextField5.setText(org.openide.util.NbBundle.getMessage(
                TreppeBauteilZustandKostenPanel.class,
                "TreppeBauteilZustandKostenPanel.jFormattedTextField5.text_1")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kosten}"),
                jFormattedTextField5,
                org.jdesktop.beansbinding.BeanProperty.create("value"));
        binding.setConverter(CONVERTER_DOUBLE);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel3.add(jFormattedTextField5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(jPanel3, gridBagConstraints);

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
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(panZusammenfassung, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     */
    private void recalculateGesamt() {
        final Integer standsicherheit = (Integer)cidsBean.getProperty("standsicherheit");
        final Integer verkehrssicherheit = (Integer)cidsBean.getProperty("verkehrssicherheit");
        final Integer dauerhaftigkeit = (Integer)cidsBean.getProperty("dauerhaftigkeit");

        try {
            if ((standsicherheit != null) && (verkehrssicherheit != null) && (dauerhaftigkeit != null)
                        && (standsicherheit >= 0)
                        && (verkehrssicherheit >= 0)
                        && (dauerhaftigkeit >= 0)
                        && (standsicherheit < MATRIX_DSV[0].length)
                        && (verkehrssicherheit < MATRIX_DSV[0][0].length)
                        && (dauerhaftigkeit < MATRIX_DSV.length)) {
                final double zustand = MATRIX_DSV[dauerhaftigkeit][standsicherheit][verkehrssicherheit];
                cidsBean.setProperty("gesamt", zustand);
            } else {
                cidsBean.setProperty("gesamt", null);
            }
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        if (this.cidsBean != null) {
            this.cidsBean.removePropertyChangeListener(propChangeListener);
        }
        bindingGroup.unbind();
        this.cidsBean = cidsBean;
        bindingGroup.bind();

        if ((cidsBean != null)) {
            cidsBean.addPropertyChangeListener(propChangeListener);
            if (editable) {
                recalculateGesamt();
            }
        }
    }

    @Override
    public void dispose() {
        if (this.cidsBean != null) {
            this.cidsBean.removePropertyChangeListener(propChangeListener);
        }
        bindingGroup.unbind();
    }
}
