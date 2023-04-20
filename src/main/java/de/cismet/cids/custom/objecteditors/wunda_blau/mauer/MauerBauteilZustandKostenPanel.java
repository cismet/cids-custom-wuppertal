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
package de.cismet.cids.custom.objecteditors.wunda_blau.mauer;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;

import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.text.DecimalFormat;

import javax.swing.Box;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objecteditors.wunda_blau.MauerEditor;
import de.cismet.cids.custom.objecteditors.wunda_blau.TreppeEditor;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;
import de.cismet.cids.dynamics.Disposable;

import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultCustomObjectEditor;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.SemiRoundedPanel;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class MauerBauteilZustandKostenPanel extends javax.swing.JPanel implements CidsBeanStore,
    Disposable,
    ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(MauerBauteilZustandKostenPanel.class);
    private static final TreppeEditor.IntegerToLongConverter CONVERTER_INT = new TreppeEditor.IntegerToLongConverter();

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
    private ConnectionContext connectionContext = ConnectionContext.createDummy();
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

    private final MauerEditor mauerEditor;

    private final NumberFormatter nf = new NumberFormatter(new DecimalFormat("#0"));

    private final String bauteilName;
    private final boolean manual;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    DefaultBindableReferenceCombo cbEingriffGruendung;
    JFormattedTextField jFormattedTextField1;
    JFormattedTextField jFormattedTextField2;
    JFormattedTextField jFormattedTextField3;
    JFormattedTextField jFormattedTextField4;
    JLabel jLabel1;
    JLabel jLabel2;
    JLabel jLabel3;
    JLabel jLabel4;
    JSpinner jSpinner1;
    JTextArea jTextArea1;
    JTextArea tfBeschreibung;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MauerBauteilZustandKostenPanel object.
     */
    public MauerBauteilZustandKostenPanel() {
        this(null, null, false, false);
    }

    /**
     * Creates a new MauerBauteilZustandKostenPanel object.
     *
     * @param  mauerEditor  DOCUMENT ME!
     * @param  bauteilName  DOCUMENT ME!
     */
    public MauerBauteilZustandKostenPanel(final MauerEditor mauerEditor, final String bauteilName) {
        this(mauerEditor, bauteilName, false, false);
    }

    /**
     * Creates a new MauerBauteilZustandKostenPanel object.
     *
     * @param  mauerEditor  DOCUMENT ME!
     * @param  bauteilName  DOCUMENT ME!
     * @param  editable     DOCUMENT ME!
     */
    public MauerBauteilZustandKostenPanel(final MauerEditor mauerEditor,
            final String bauteilName,
            final boolean editable) {
        this(mauerEditor, bauteilName, editable, false);
    }

    /**
     * Creates new form MauerBauteilZustandKostenPanel.
     *
     * @param  mauerEditor  DOCUMENT ME!
     * @param  bauteilName  DOCUMENT ME!
     * @param  editable     DOCUMENT ME!
     * @param  manual       DOCUMENT ME!
     */
    public MauerBauteilZustandKostenPanel(final MauerEditor mauerEditor,
            final String bauteilName,
            final boolean editable,
            final boolean manual) {
        this.mauerEditor = mauerEditor;
        this.bauteilName = bauteilName;
        this.editable = editable;
        this.manual = manual;

        nf.setMinimum(0);
        nf.setMaximum(4);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        initComponents();

        MauerEditor.setLimitDocumentFilter(tfBeschreibung, 500);
        MauerEditor.setLimitDocumentFilter(jTextArea1, 500);

        jTextArea1.addKeyListener(new RendererTools.NoTabTextAreaKeyAdapter());
        RendererTools.makeDoubleSpinnerWithoutButtons(jSpinner1, 2);
        if (!editable) {
            RendererTools.makeReadOnly(jFormattedTextField1);
            RendererTools.makeReadOnly(jFormattedTextField2);
            RendererTools.makeReadOnly(jFormattedTextField3);
            RendererTools.makeReadOnly(jFormattedTextField4);
            RendererTools.makeReadOnly(jSpinner1);
            RendererTools.makeReadOnly(jTextArea1);
            RendererTools.makeReadOnly(tfBeschreibung);
            RendererTools.makeReadOnly(cbEingriffGruendung);
        }

        jLabel1.setVisible(!manual);
        jFormattedTextField1.setVisible(!manual);
        jLabel2.setVisible(!manual);
        jFormattedTextField2.setVisible(!manual);
        jLabel3.setVisible(!manual);
        jFormattedTextField3.setVisible(!manual);
        jLabel4.setVisible(!manual);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getBauteilName() {
        return bauteilName;
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

        final RoundedPanel panZusammenfassung = new RoundedPanel();
        final SemiRoundedPanel panZusammenfassungTitle = new SemiRoundedPanel();
        final JLabel lblHeaderAllgemein = new JLabel();
        final JPanel panZusammenfassungContent = new JPanel();
        final JPanel jPanel2 = new JPanel();
        final JScrollPane jScrollPane10 = new JScrollPane();
        tfBeschreibung = new JTextArea();
        final JLabel lblbeschreibungGruendung = new JLabel();
        final Box.Filler filler3 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
        final JLabel lblEingriffAnsicht1 = new JLabel();
        cbEingriffGruendung = new DefaultBindableReferenceCombo();
        final JLabel jLabel5 = new JLabel();
        final Box.Filler filler9 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        final JPanel jPanel4 = new JPanel();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        jLabel4 = new JLabel();
        jFormattedTextField1 = new JFormattedTextField(nf);
        jFormattedTextField2 = new JFormattedTextField(nf);
        jFormattedTextField3 = new JFormattedTextField(nf);
        jFormattedTextField4 = new JFormattedTextField(nf);
        final Box.Filler filler4 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        final Box.Filler filler5 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        final Box.Filler filler6 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        final Box.Filler filler7 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        final JLabel jLabel11 = new JLabel();
        final Box.Filler filler8 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
        final JScrollPane jScrollPane1 = new JScrollPane();
        jTextArea1 = new JTextArea();
        final JLabel jLabel6 = new JLabel();
        final JPanel jPanel3 = new JPanel();
        final JLabel jLabel7 = new JLabel();
        final Box.Filler filler2 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
        final Box.Filler filler1 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        jSpinner1 = new JSpinner();
        final JSeparator jSeparator1 = new JSeparator();

        setName("Form"); // NOI18N
        setOpaque(false);
        setLayout(new GridBagLayout());

        panZusammenfassung.setName("panZusammenfassung"); // NOI18N
        panZusammenfassung.setLayout(new GridBagLayout());

        panZusammenfassungTitle.setBackground(new Color(51, 51, 51));
        panZusammenfassungTitle.setName("panZusammenfassungTitle"); // NOI18N
        panZusammenfassungTitle.setLayout(new FlowLayout());

        lblHeaderAllgemein.setForeground(new Color(255, 255, 255));
        lblHeaderAllgemein.setName("lblHeaderAllgemein"); // NOI18N

        Binding binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ,
                this,
                ELProperty.create("${bauteilName}"),
                lblHeaderAllgemein,
                BeanProperty.create("text"));
        binding.setSourceNullValue("Zustand & Sanierung");
        binding.setSourceUnreadableValue("Zustand & Sanierung");
        bindingGroup.addBinding(binding);

        panZusammenfassungTitle.add(lblHeaderAllgemein);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        panZusammenfassung.add(panZusammenfassungTitle, gridBagConstraints);

        panZusammenfassungContent.setName("panZusammenfassungContent"); // NOI18N
        panZusammenfassungContent.setOpaque(false);
        panZusammenfassungContent.setLayout(new GridBagLayout());

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setOpaque(false);
        jPanel2.setLayout(new GridBagLayout());

        jScrollPane10.setMinimumSize(new Dimension(26, 70));
        jScrollPane10.setName("jScrollPane10"); // NOI18N
        jScrollPane10.setPreferredSize(new Dimension(0, 70));

        tfBeschreibung.setLineWrap(true);
        tfBeschreibung.setMinimumSize(new Dimension(500, 70));
        tfBeschreibung.setName("tfBeschreibung"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.beschreibung}"),
                tfBeschreibung,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane10.setViewportView(tfBeschreibung);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 0, 5, 0);
        jPanel2.add(jScrollPane10, gridBagConstraints);

        Mnemonics.setLocalizedText(
            lblbeschreibungGruendung,
            NbBundle.getMessage(
                MauerBauteilZustandKostenPanel.class,
                "MauerBauteilZustandKostenPanel.lblbeschreibungGruendung.text")); // NOI18N
        lblbeschreibungGruendung.setName("lblbeschreibungGruendung");             // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(5, 0, 5, 5);
        jPanel2.add(lblbeschreibungGruendung, gridBagConstraints);

        filler3.setName("filler3"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(19, 0, 19, 0);
        jPanel2.add(filler3, gridBagConstraints);

        Mnemonics.setLocalizedText(
            lblEingriffAnsicht1,
            NbBundle.getMessage(
                MauerBauteilZustandKostenPanel.class,
                "MauerBauteilZustandKostenPanel.lblEingriffAnsicht1.text")); // NOI18N
        lblEingriffAnsicht1.setName("lblEingriffAnsicht1");                  // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(5, 0, 5, 5);
        jPanel2.add(lblEingriffAnsicht1, gridBagConstraints);

        cbEingriffGruendung.setName("cbEingriffGruendung"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.fk_eingriff}"),
                cbEingriffGruendung,
                BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 0, 5, 0);
        jPanel2.add(cbEingriffGruendung, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel5,
            NbBundle.getMessage(MauerBauteilZustandKostenPanel.class, "MauerBauteilZustandKostenPanel.jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5");                                                                                    // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(5, 0, 5, 5);
        jPanel2.add(jLabel5, gridBagConstraints);

        filler9.setName("filler9"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(filler9, gridBagConstraints);

        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setOpaque(false);
        jPanel4.setLayout(new GridBagLayout());

        Mnemonics.setLocalizedText(
            jLabel1,
            NbBundle.getMessage(MauerBauteilZustandKostenPanel.class, "MauerBauteilZustandKostenPanel.jLabel1.text")); // NOI18N
        jLabel1.setToolTipText(NbBundle.getMessage(
                MauerBauteilZustandKostenPanel.class,
                "MauerBauteilZustandKostenPanel.jLabel1.toolTipText"));                                                // NOI18N
        jLabel1.setName("jLabel1");                                                                                    // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(1, 0, 1, 7);
        jPanel4.add(jLabel1, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel2,
            NbBundle.getMessage(MauerBauteilZustandKostenPanel.class, "MauerBauteilZustandKostenPanel.jLabel2.text")); // NOI18N
        jLabel2.setToolTipText(NbBundle.getMessage(
                MauerBauteilZustandKostenPanel.class,
                "MauerBauteilZustandKostenPanel.jLabel2.toolTipText"));                                                // NOI18N
        jLabel2.setName("jLabel2");                                                                                    // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(1, 10, 1, 7);
        jPanel4.add(jLabel2, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel3,
            NbBundle.getMessage(MauerBauteilZustandKostenPanel.class, "MauerBauteilZustandKostenPanel.jLabel3.text")); // NOI18N
        jLabel3.setToolTipText(NbBundle.getMessage(
                MauerBauteilZustandKostenPanel.class,
                "MauerBauteilZustandKostenPanel.jLabel3.toolTipText"));                                                // NOI18N
        jLabel3.setName("jLabel3");                                                                                    // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(1, 10, 1, 7);
        jPanel4.add(jLabel3, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel4,
            NbBundle.getMessage(MauerBauteilZustandKostenPanel.class, "MauerBauteilZustandKostenPanel.jLabel4.text")); // NOI18N
        jLabel4.setToolTipText(NbBundle.getMessage(
                MauerBauteilZustandKostenPanel.class,
                "MauerBauteilZustandKostenPanel.jLabel4.toolTipText"));                                                // NOI18N
        jLabel4.setName("jLabel4");                                                                                    // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(1, 10, 1, 7);
        jPanel4.add(jLabel4, gridBagConstraints);

        jFormattedTextField1.setFormatterFactory(new DefaultFormatterFactory(
                new NumberFormatter(new DecimalFormat("#0"))));
        jFormattedTextField1.setHorizontalAlignment(JTextField.CENTER);
        jFormattedTextField1.setText(NbBundle.getMessage(
                MauerBauteilZustandKostenPanel.class,
                "MauerBauteilZustandKostenPanel.jFormattedTextField1.text")); // NOI18N
        jFormattedTextField1.setMinimumSize(new Dimension(50, 32));
        jFormattedTextField1.setName("jFormattedTextField1");                 // NOI18N
        jFormattedTextField1.setPreferredSize(new Dimension(50, 32));

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.standsicherheit}"),
                jFormattedTextField1,
                BeanProperty.create("value"));
        binding.setConverter(CONVERTER_INT);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel4.add(jFormattedTextField1, gridBagConstraints);

        jFormattedTextField2.setFormatterFactory(new DefaultFormatterFactory(
                new NumberFormatter(new DecimalFormat("#0"))));
        jFormattedTextField2.setHorizontalAlignment(JTextField.CENTER);
        jFormattedTextField2.setText(NbBundle.getMessage(
                MauerBauteilZustandKostenPanel.class,
                "MauerBauteilZustandKostenPanel.jFormattedTextField1.text")); // NOI18N
        jFormattedTextField2.setMinimumSize(new Dimension(50, 32));
        jFormattedTextField2.setName("jFormattedTextField2");                 // NOI18N
        jFormattedTextField2.setPreferredSize(new Dimension(50, 32));

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.verkehrssicherheit}"),
                jFormattedTextField2,
                BeanProperty.create("value"));
        binding.setConverter(CONVERTER_INT);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel4.add(jFormattedTextField2, gridBagConstraints);

        jFormattedTextField3.setFormatterFactory(new DefaultFormatterFactory(
                new NumberFormatter(new DecimalFormat("#0"))));
        jFormattedTextField3.setHorizontalAlignment(JTextField.CENTER);
        jFormattedTextField3.setText(NbBundle.getMessage(
                MauerBauteilZustandKostenPanel.class,
                "MauerBauteilZustandKostenPanel.jFormattedTextField1.text")); // NOI18N
        jFormattedTextField3.setMinimumSize(new Dimension(50, 32));
        jFormattedTextField3.setName("jFormattedTextField3");                 // NOI18N
        jFormattedTextField3.setPreferredSize(new Dimension(50, 32));

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.dauerhaftigkeit}"),
                jFormattedTextField3,
                BeanProperty.create("value"));
        binding.setConverter(CONVERTER_INT);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 2);
        jPanel4.add(jFormattedTextField3, gridBagConstraints);

        jFormattedTextField4.setEditable(false);
        jFormattedTextField4.setFormatterFactory(new DefaultFormatterFactory(
                new NumberFormatter(new DecimalFormat("#0.0"))));
        jFormattedTextField4.setHorizontalAlignment(JTextField.CENTER);
        jFormattedTextField4.setText(NbBundle.getMessage(
                MauerBauteilZustandKostenPanel.class,
                "MauerBauteilZustandKostenPanel.jFormattedTextField4.text_1")); // NOI18N
        jFormattedTextField4.setMinimumSize(new Dimension(50, 32));
        jFormattedTextField4.setName("jFormattedTextField4");                   // NOI18N
        jFormattedTextField4.setPreferredSize(new Dimension(50, 32));

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.gesamt}"),
                jFormattedTextField4,
                BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel4.add(jFormattedTextField4, gridBagConstraints);

        filler4.setName("filler4"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 10, 0, 10);
        jPanel4.add(filler4, gridBagConstraints);

        filler5.setName("filler5"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 10, 0, 10);
        jPanel4.add(filler5, gridBagConstraints);

        filler6.setName("filler6"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 20, 0, 20);
        jPanel4.add(filler6, gridBagConstraints);

        filler7.setName("filler7"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 10, 0, 10);
        jPanel4.add(filler7, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 0, 5, 0);
        jPanel2.add(jPanel4, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel11,
            NbBundle.getMessage(MauerBauteilZustandKostenPanel.class, "MauerBauteilZustandKostenPanel.jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11");                                                                                   // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(5, 0, 5, 5);
        jPanel2.add(jLabel11, gridBagConstraints);

        filler8.setName("filler8"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(19, 0, 19, 0);
        jPanel2.add(filler8, gridBagConstraints);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(4);
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setName("jTextArea1"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.sanierungsmassnahmen}"),
                jTextArea1,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(jTextArea1);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 0, 5, 0);
        jPanel2.add(jScrollPane1, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel6,
            NbBundle.getMessage(MauerBauteilZustandKostenPanel.class, "MauerBauteilZustandKostenPanel.jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6");                                                                                    // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(5, 0, 5, 5);
        jPanel2.add(jLabel6, gridBagConstraints);

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setOpaque(false);
        jPanel3.setLayout(new GridBagLayout());

        Mnemonics.setLocalizedText(
            jLabel7,
            NbBundle.getMessage(MauerBauteilZustandKostenPanel.class, "MauerBauteilZustandKostenPanel.jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7");                                                                                    // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(0, 2, 0, 0);
        jPanel3.add(jLabel7, gridBagConstraints);

        filler2.setName("filler2"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 45, 0, 45);
        jPanel3.add(filler2, gridBagConstraints);

        filler1.setName("filler1"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(filler1, gridBagConstraints);

        jSpinner1.setModel(new SpinnerNumberModel(0.0d, 0.0d, null, 0.01d));
        jSpinner1.setName("jSpinner1"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.kosten}"),
                jSpinner1,
                BeanProperty.create("value"));
        binding.setSourceNullValue(0d);
        binding.setSourceUnreadableValue(0d);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(2, 0, 2, 0);
        jPanel3.add(jSpinner1, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(5, 0, 5, 0);
        jPanel2.add(jPanel3, gridBagConstraints);

        jSeparator1.setName("jSeparator1"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 0, 5, 0);
        jPanel2.add(jSeparator1, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 10);
        panZusammenfassungContent.add(jPanel2, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panZusammenfassung.add(panZusammenfassungContent, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(panZusammenfassung, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     */
    private void recalculateGesamt() {
        final CidsBean cidsBean = this.cidsBean;
        new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
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
                        mauerEditor.recalculateOverview();
                    } catch (final Exception ex) {
                        LOG.warn("Zustand konnte nicht berechnet werden.", ex);
                    }
                    return null;
                }
            }.execute();
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
        if (bindingGroup != null) {
            bindingGroup.unbind();
            if (cidsBean != null) {
                DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                    bindingGroup,
                    cidsBean,
                    getConnectionContext());
            }
            this.cidsBean = cidsBean;
            bindingGroup.bind();
        }
        if (cidsBean != null) {
            cidsBean.addPropertyChangeListener(propChangeListener);
        }
        if (editable) {
            recalculateGesamt();
        }
    }

    @Override
    public void dispose() {
        bindingGroup.unbind();

        if (cidsBean != null) {
            cidsBean.removePropertyChangeListener(propChangeListener);
            cidsBean = null;
        }
    }
}
