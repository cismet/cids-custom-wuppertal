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

import org.apache.log4j.Logger;

import org.openide.util.Exceptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;

import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objectrenderer.converter.BooleanConverter;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class BillingBillingEditor extends javax.swing.JPanel implements CidsBeanRenderer, EditorSaveListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(BillingBillingEditor.class);
    private static DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    //~ Instance fields --------------------------------------------------------

    String originalGeschaeftsbuchnummer;
    String originalProjektbezeichnung;
    private final boolean editable;
    private CidsBean cidsBean;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnStornoBuchung;
    private de.cismet.cids.editors.DefaultBindableJCheckBox cboStorno;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo coboStornogrund;
    private de.cismet.cids.editors.DefaultBindableJCheckBox defaultBindableJCheckBox1;
    private de.cismet.cids.editors.DefaultBindableJTextField defaultBindableJTextField5;
    private de.cismet.cids.editors.DefaultBindableJTextField defaultBindableJTextField7;
    private de.cismet.cids.editors.DefaultBindableJTextField defaultBindableJTextField8;
    private javax.swing.Box.Filler filler1;
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
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblGeschaeftsbuchnummer;
    private javax.swing.JLabel lblProjektbezeichnung;
    private javax.swing.JLabel lblStornogrund;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel1;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel2;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel3;
    private de.cismet.cids.editors.DefaultBindableJTextField txtAngelegt_am;
    private de.cismet.cids.editors.DefaultBindableJTextField txtAngelegt_durch1;
    private de.cismet.cids.editors.DefaultBindableJTextField txtGeschaeftsbuchnummer;
    private de.cismet.cids.editors.DefaultBindableJTextField txtModus;
    private de.cismet.cids.editors.DefaultBindableJTextField txtProjektbezeichnung;
    private de.cismet.cids.editors.DefaultBindableJTextField txtVerwendungszweck;
    private javax.swing.JTextArea txtaAenderung;
    private javax.swing.JTextArea txtaBerechnung;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form BillingBillingEditor.
     */
    public BillingBillingEditor() {
        this(true);
    }

    /**
     * Creates a new BillingBillingEditor object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public BillingBillingEditor(final boolean editable) {
        this.editable = editable;
        initComponents();
        if (!editable) {
            txtGeschaeftsbuchnummer.setBorder(null);
            txtProjektbezeichnung.setBorder(null);
        }
        txtGeschaeftsbuchnummer.setEnabled(editable);
        txtProjektbezeichnung.setEnabled(editable);
        coboStornogrund.setEnabled(editable);

        txtGeschaeftsbuchnummer.getDocument().addDocumentListener(new GBNRDocumentlistener());
        txtProjektbezeichnung.getDocument().addDocumentListener(new ProjektDocumentlistener());
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

        jPanel1 = new javax.swing.JPanel();
        semiRoundedPanel1 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lblStornogrund = new javax.swing.JLabel();
        coboStornogrund = new de.cismet.cids.editors.DefaultBindableReferenceCombo(true);
        if (editable) {
            btnStornoBuchung = new javax.swing.JButton();
        }
        cboStorno = new de.cismet.cids.editors.DefaultBindableJCheckBox();
        semiRoundedPanel2 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        txtProjektbezeichnung = new de.cismet.cids.editors.DefaultBindableJTextField();
        lblProjektbezeichnung = new javax.swing.JLabel();
        lblGeschaeftsbuchnummer = new javax.swing.JLabel();
        txtGeschaeftsbuchnummer = new de.cismet.cids.editors.DefaultBindableJTextField();
        semiRoundedPanel3 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        defaultBindableJCheckBox1 = new de.cismet.cids.editors.DefaultBindableJCheckBox();
        jLabel4 = new javax.swing.JLabel();
        txtModus = new de.cismet.cids.editors.DefaultBindableJTextField();
        jLabel5 = new javax.swing.JLabel();
        txtVerwendungszweck = new de.cismet.cids.editors.DefaultBindableJTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        defaultBindableJTextField5 = new de.cismet.cids.editors.DefaultBindableJTextField();
        jLabel9 = new javax.swing.JLabel();
        txtAngelegt_am = new de.cismet.cids.editors.DefaultBindableJTextField();
        jLabel10 = new javax.swing.JLabel();
        defaultBindableJTextField7 = new de.cismet.cids.editors.DefaultBindableJTextField();
        jLabel11 = new javax.swing.JLabel();
        defaultBindableJTextField8 = new de.cismet.cids.editors.DefaultBindableJTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtaAenderung = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtaBerechnung = new javax.swing.JTextArea();
        txtAngelegt_durch1 = new de.cismet.cids.editors.DefaultBindableJTextField();
        jLabel13 = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));

        setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel1.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanel1.setLayout(new java.awt.FlowLayout());

        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(BillingBillingEditor.class, "BillingBillingEditor.jLabel1.text")); // NOI18N
        semiRoundedPanel1.add(jLabel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(semiRoundedPanel1, gridBagConstraints);

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(7, 7, 7, 7));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblStornogrund,
            org.openide.util.NbBundle.getMessage(
                BillingBillingEditor.class,
                "BillingBillingEditor.lblStornogrund.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel2.add(lblStornogrund, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.stornogrund}"),
                coboStornogrund,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel2.add(coboStornogrund, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            btnStornoBuchung,
            org.openide.util.NbBundle.getMessage(
                BillingBillingEditor.class,
                "BillingBillingEditor.btnStornoBuchung.text")); // NOI18N
        btnStornoBuchung.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnStornoBuchungActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel2.add(btnStornoBuchung, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            cboStorno,
            org.openide.util.NbBundle.getMessage(BillingBillingEditor.class, "BillingBillingEditor.cboStorno.text")); // NOI18N
        cboStorno.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.storniert}"),
                cboStorno,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        binding.setConverter(new BooleanConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        jPanel2.add(cboStorno, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(jPanel2, gridBagConstraints);

        semiRoundedPanel2.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanel2.setLayout(new java.awt.FlowLayout());

        jLabel2.setForeground(new java.awt.Color(238, 238, 238));
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel2,
            org.openide.util.NbBundle.getMessage(BillingBillingEditor.class, "BillingBillingEditor.jLabel2.text")); // NOI18N
        semiRoundedPanel2.add(jLabel2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(semiRoundedPanel2, gridBagConstraints);

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(7, 7, 7, 7));
        jPanel3.setLayout(new java.awt.GridBagLayout());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.projektbezeichnung}"),
                txtProjektbezeichnung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtProjektbezeichnung.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    txtProjektbezeichnungActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 0);
        jPanel3.add(txtProjektbezeichnung, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblProjektbezeichnung,
            org.openide.util.NbBundle.getMessage(
                BillingBillingEditor.class,
                "BillingBillingEditor.lblProjektbezeichnung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        jPanel3.add(lblProjektbezeichnung, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblGeschaeftsbuchnummer,
            org.openide.util.NbBundle.getMessage(
                BillingBillingEditor.class,
                "BillingBillingEditor.lblGeschaeftsbuchnummer.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 2);
        jPanel3.add(lblGeschaeftsbuchnummer, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.geschaeftsbuchnummer}"),
                txtGeschaeftsbuchnummer,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        jPanel3.add(txtGeschaeftsbuchnummer, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        jPanel1.add(jPanel3, gridBagConstraints);

        semiRoundedPanel3.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanel3.setLayout(new java.awt.FlowLayout());

        jLabel3.setForeground(new java.awt.Color(238, 238, 238));
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel3,
            org.openide.util.NbBundle.getMessage(BillingBillingEditor.class, "BillingBillingEditor.jLabel3.text")); // NOI18N
        semiRoundedPanel3.add(jLabel3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(semiRoundedPanel3, gridBagConstraints);

        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(7, 7, 7, 7));
        jPanel4.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel12,
            org.openide.util.NbBundle.getMessage(BillingBillingEditor.class, "BillingBillingEditor.jLabel12.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 5);
        jPanel4.add(jLabel12, gridBagConstraints);

        defaultBindableJCheckBox1.setBorder(null);
        org.openide.awt.Mnemonics.setLocalizedText(
            defaultBindableJCheckBox1,
            org.openide.util.NbBundle.getMessage(
                BillingBillingEditor.class,
                "BillingBillingEditor.defaultBindableJCheckBox1.text")); // NOI18N
        defaultBindableJCheckBox1.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.abgerechnet}"),
                defaultBindableJCheckBox1,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        binding.setConverter(new BooleanConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        jPanel4.add(defaultBindableJCheckBox1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel4,
            org.openide.util.NbBundle.getMessage(BillingBillingEditor.class, "BillingBillingEditor.jLabel4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 5, 5);
        jPanel4.add(jLabel4, gridBagConstraints);

        txtModus.setBorder(null);
        txtModus.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.modusbezeichnung}"),
                txtModus,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 5, 0);
        jPanel4.add(txtModus, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel5,
            org.openide.util.NbBundle.getMessage(BillingBillingEditor.class, "BillingBillingEditor.jLabel5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        jPanel4.add(jLabel5, gridBagConstraints);

        txtVerwendungszweck.setBorder(null);
        txtVerwendungszweck.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.verwendungszweck}"),
                txtVerwendungszweck,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel4.add(txtVerwendungszweck, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel6,
            org.openide.util.NbBundle.getMessage(BillingBillingEditor.class, "BillingBillingEditor.jLabel6.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        jPanel4.add(jLabel6, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel7,
            org.openide.util.NbBundle.getMessage(BillingBillingEditor.class, "BillingBillingEditor.jLabel7.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        jPanel4.add(jLabel7, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel8,
            org.openide.util.NbBundle.getMessage(BillingBillingEditor.class, "BillingBillingEditor.jLabel8.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        jPanel4.add(jLabel8, gridBagConstraints);

        defaultBindableJTextField5.setBorder(null);
        defaultBindableJTextField5.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.produktbezeichnung}"),
                defaultBindableJTextField5,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel4.add(defaultBindableJTextField5, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel9,
            org.openide.util.NbBundle.getMessage(BillingBillingEditor.class, "BillingBillingEditor.jLabel9.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        jPanel4.add(jLabel9, gridBagConstraints);

        txtAngelegt_am.setBorder(null);
        txtAngelegt_am.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel4.add(txtAngelegt_am, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel10,
            org.openide.util.NbBundle.getMessage(BillingBillingEditor.class, "BillingBillingEditor.jLabel10.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        jPanel4.add(jLabel10, gridBagConstraints);

        defaultBindableJTextField7.setBorder(null);
        defaultBindableJTextField7.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.netto_summe}"),
                defaultBindableJTextField7,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel4.add(defaultBindableJTextField7, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel11,
            org.openide.util.NbBundle.getMessage(BillingBillingEditor.class, "BillingBillingEditor.jLabel11.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        jPanel4.add(jLabel11, gridBagConstraints);

        defaultBindableJTextField8.setBorder(null);
        defaultBindableJTextField8.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.mwst_satz}"),
                defaultBindableJTextField8,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel4.add(defaultBindableJTextField8, gridBagConstraints);

        jScrollPane1.setBorder(null);

        txtaAenderung.setColumns(20);
        txtaAenderung.setRows(4);
        txtaAenderung.setEnabled(false);
        jScrollPane1.setViewportView(txtaAenderung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel4.add(jScrollPane1, gridBagConstraints);

        jScrollPane2.setBorder(null);

        txtaBerechnung.setColumns(20);
        txtaBerechnung.setRows(6);
        txtaBerechnung.setEnabled(false);
        jScrollPane2.setViewportView(txtaBerechnung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel4.add(jScrollPane2, gridBagConstraints);

        txtAngelegt_durch1.setBorder(null);
        txtAngelegt_durch1.setText(org.openide.util.NbBundle.getMessage(
                BillingBillingEditor.class,
                "BillingBillingEditor.txtAngelegt_durch1.text")); // NOI18N
        txtAngelegt_durch1.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel4.add(txtAngelegt_durch1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel13,
            org.openide.util.NbBundle.getMessage(BillingBillingEditor.class, "BillingBillingEditor.jLabel13.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        jPanel4.add(jLabel13, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(jPanel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.5;
        add(jPanel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        add(filler1, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtProjektbezeichnungActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProjektbezeichnungActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProjektbezeichnungActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnStornoBuchungActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStornoBuchungActionPerformed
        if (
            JOptionPane.showConfirmDialog(
                        this.getTopLevelAncestor(),
                        "Buchung stornieren?",
                        "Soll die Buchung wirklich storniert werden?",
                        JOptionPane.YES_NO_OPTION)
                    == JOptionPane.YES_OPTION) {
            try {
                cidsBean.setProperty("storniert", Boolean.TRUE);
                cidsBean.setProperty("stornogrund", coboStornogrund.getSelectedItem());
                cidsBean.setProperty("storniert_durch", SessionManager.getSession().getUser().toString());
            } catch (Exception ex) {
                LOG.error("Error while setting 'storniert' of billing", ex);
            }
        }
    }//GEN-LAST:event_btnStornoBuchungActionPerformed

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        bindingGroup.unbind();
        if (cidsBean != null) {
            this.cidsBean = cidsBean;
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                this.cidsBean);
            bindingGroup.bind();
            fillTextFields();

            originalGeschaeftsbuchnummer = (String)cidsBean.getProperty("geschaeftsbuchnummer");
            originalProjektbezeichnung = (String)cidsBean.getProperty("projektbezeichnung");
        }
    }

    @Override
    public void dispose() {
        bindingGroup.unbind();
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public void setTitle(final String title) {
    }

    @Override
    public void editorClosed(final EditorClosedEvent event) {
    }

    @Override
    public boolean prepareForSave() {
        return false;
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
            "billing_billing",
            1080,
            1280,
            1024);
    }

    /**
     * DOCUMENT ME!
     */
    private void fillTextFields() {
        Object user = cidsBean.getProperty("angelegt_durch");
        if (user == null) {
            user = cidsBean.getProperty("username");
        }
        txtVerwendungszweck.setText(user.toString());

        String text = "";
        final Date geaendert_am = (Date)cidsBean.getProperty("geaendert_am");
        if (geaendert_am != null) {
            final String geaendert_von = (String)cidsBean.getProperty("geaendert_von");
            final String aenderung_attribut = (String)cidsBean.getProperty("aenderung_attribut");
            final String aenderung_alter_wert = (String)cidsBean.getProperty("aenderung_alter_wert");
            final String aenderung_neuer_wert = (String)cidsBean.getProperty("aenderung_neuer_wert");
            text = "Änderung von " + geaendert_von + " am " + DATE_FORMAT.format(geaendert_am) + "\n";
            text += "Attribut: " + aenderung_attribut + "\n";
            text += "Alter Wert: " + aenderung_alter_wert + "\n";
            text += "Neuer Wert: " + aenderung_neuer_wert + "\n";
        }
        txtaAenderung.setText(text);

        txtaBerechnung.setText(cidsBean.getProperty("berechnung").toString());

        txtAngelegt_am.setText(DATE_FORMAT.format(cidsBean.getProperty("ts")));

        final Boolean storniert = (Boolean)cidsBean.getProperty("storniert");
        text = "Buchung ist storniert";
        if ((storniert != null) && storniert.booleanValue()) {
            final Date storno_am = (Date)cidsBean.getProperty("storno_datum");
            final String storniert_durch = (String)cidsBean.getProperty("storniert_durch");
            text += "(von " + storniert_durch
                        + " am " + DATE_FORMAT.format(storno_am)
                        + " )";
        }
        cboStorno.setText(text);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class GBNRDocumentlistener implements DocumentListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void insertUpdate(final DocumentEvent e) {
            setChange();
        }

        @Override
        public void removeUpdate(final DocumentEvent e) {
            setChange();
        }

        @Override
        public void changedUpdate(final DocumentEvent e) {
            setChange();
        }

        /**
         * DOCUMENT ME!
         */
        private void setChange() {
            try {
                cidsBean.setProperty("geaendert_am", new java.sql.Timestamp(System.currentTimeMillis()));
                cidsBean.setProperty("geaendert_von", SessionManager.getSession().getUser().toString());
                cidsBean.setProperty("aenderung_attribut", "Geschäftsbuchnummer");
                cidsBean.setProperty("aenderung_alter_wert", originalGeschaeftsbuchnummer);
                cidsBean.setProperty("aenderung_neuer_wert", txtGeschaeftsbuchnummer.getText());
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class ProjektDocumentlistener implements DocumentListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void insertUpdate(final DocumentEvent e) {
            setChange();
        }

        @Override
        public void removeUpdate(final DocumentEvent e) {
            setChange();
        }

        @Override
        public void changedUpdate(final DocumentEvent e) {
            setChange();
        }

        /**
         * DOCUMENT ME!
         */
        private void setChange() {
            try {
                cidsBean.setProperty("geaendert_am", new java.sql.Timestamp(System.currentTimeMillis()));
                cidsBean.setProperty("geaendert_von", SessionManager.getSession().getUser().toString());
                cidsBean.setProperty("aenderung_attribut", "Projektbezeichnung");
                cidsBean.setProperty("aenderung_alter_wert", originalProjektbezeichnung);
                cidsBean.setProperty("aenderung_neuer_wert", txtProjektbezeichnung.getText());
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
