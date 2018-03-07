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

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;

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
import java.awt.GridLayout;
import java.awt.Insets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.text.DecimalFormat;

import java.util.Collection;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.wunda_blau.search.server.CustomStrassenSearchStatement;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;
import de.cismet.cids.dynamics.Disposable;

import de.cismet.cids.editors.DefaultBindableDateChooser;
import de.cismet.cids.editors.DefaultBindableReferenceCombo;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.connectioncontext.AbstractConnectionContext.Category;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import de.cismet.tools.gui.SemiRoundedPanel;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class TreppeBeschreibungPanel extends javax.swing.JPanel implements CidsBeanStore,
    Disposable,
    ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(TreppeBeschreibungPanel.class);
    private static final TreppeEditor.IntegerToLongConverter CONVERTER_INT = new TreppeEditor.IntegerToLongConverter();

    private static final MetaClass MC__GEOM;
    private static final MetaClass MC__PRUEFUNGSART;
    private static final MetaClass MC__BEURTEILUNG;
    private static final MetaClass MC__EINSATZ;

    static {
        final ConnectionContext connectionContext = ConnectionContext.create(
                Category.STATIC,
                TreppeBeschreibungPanel.class.getSimpleName());
        MC__GEOM = ClassCacheMultiple.getMetaClass("WUNDA_BLAU", "GEOM", connectionContext);
        MC__PRUEFUNGSART = ClassCacheMultiple.getMetaClass("WUNDA_BLAU", "TREPPE_PRUEFUNGSART", connectionContext);
        MC__BEURTEILUNG = ClassCacheMultiple.getMetaClass("WUNDA_BLAU", "TREPPE_BEURTEILUNG", connectionContext);
        MC__EINSATZ = ClassCacheMultiple.getMetaClass("WUNDA_BLAU", "TREPPE_EINSATZ", connectionContext);
    }

    //~ Instance fields --------------------------------------------------------

    private CidsBean cidsBean;
    private final ConnectionContext connectionContext;

    private final boolean editable;

    private final PropertyChangeListener propChangeListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                final String name = evt.getPropertyName();
                if ("strassenschluessel".equalsIgnoreCase(name)) {
                    if (evt.getOldValue() != evt.getNewValue()) {
                        reloadStrasse();
                    }
                }
            }
        };

    // Variables declaration - do not modify//GEN-BEGIN:variables
    JComboBox cbGeom;
    DefaultBindableDateChooser defaultBindableDateChooser1;
    DefaultBindableDateChooser defaultBindableDateChooser2;
    DefaultBindableDateChooser defaultBindableDateChooser3;
    DefaultBindableDateChooser defaultBindableDateChooser4;
    DefaultBindableDateChooser defaultBindableDateChooser5;
    DefaultBindableDateChooser defaultBindableDateChooser6;
    DefaultBindableReferenceCombo defaultBindableReferenceCombo2;
    DefaultBindableReferenceCombo defaultBindableReferenceCombo3;
    DefaultBindableReferenceCombo defaultBindableReferenceCombo5;
    DefaultBindableReferenceCombo defaultBindableReferenceCombo6;
    DefaultBindableReferenceCombo defaultBindableReferenceCombo7;
    DefaultBindableReferenceCombo defaultBindableReferenceCombo8;
    JCheckBox jCheckBox1;
    JCheckBox jCheckBox10;
    JCheckBox jCheckBox11;
    JCheckBox jCheckBox2;
    JCheckBox jCheckBox3;
    JCheckBox jCheckBox4;
    JCheckBox jCheckBox5;
    JCheckBox jCheckBox6;
    JCheckBox jCheckBox7;
    JCheckBox jCheckBox8;
    JCheckBox jCheckBox9;
    JFormattedTextField jFormattedTextField1;
    JFormattedTextField jFormattedTextField2;
    JTextField jTextField1;
    JTextField jTextField10;
    JTextField jTextField11;
    JTextField jTextField12;
    JTextField jTextField13;
    JTextField jTextField14;
    JTextField jTextField15;
    JTextField jTextField2;
    JTextField jTextField3;
    JTextField jTextField4;
    JTextField jTextField5;
    JTextField jTextField6;
    JTextField jTextField7;
    JTextField jTextField8;
    JTextField jTextField9;
    JLabel lblGeom;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TreppeLaufPanel object.
     *
     * @param  connectionContext  DOCUMENT ME!
     */
    public TreppeBeschreibungPanel(final ConnectionContext connectionContext) {
        this(true, connectionContext);
    }

    /**
     * Creates new form TreppePodestPanel.
     *
     * @param  editable           DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public TreppeBeschreibungPanel(final boolean editable, final ConnectionContext connectionContext) {
        this.editable = editable;
        this.connectionContext = connectionContext;
        initComponents();

        if (!editable) {
            RendererTools.makeReadOnly(jCheckBox1);
            RendererTools.makeReadOnly(jCheckBox2);
            RendererTools.makeReadOnly(jCheckBox3);
            RendererTools.makeReadOnly(jCheckBox4);
            RendererTools.makeReadOnly(jCheckBox5);
            RendererTools.makeReadOnly(jCheckBox6);
            RendererTools.makeReadOnly(jCheckBox7);
            RendererTools.makeReadOnly(jCheckBox8);
            RendererTools.makeReadOnly(jCheckBox9);
            RendererTools.makeReadOnly(jCheckBox10);
            RendererTools.makeReadOnly(jCheckBox11);
            RendererTools.makeReadOnly(jFormattedTextField1);
            RendererTools.makeReadOnly(jFormattedTextField2);
            RendererTools.makeReadOnly(jTextField2);
            RendererTools.makeReadOnly(jTextField3);
            RendererTools.makeReadOnly(jTextField4);
            RendererTools.makeReadOnly(jTextField5);
            RendererTools.makeReadOnly(jTextField6);
            RendererTools.makeReadOnly(jTextField7);
            RendererTools.makeReadOnly(jTextField8);
            RendererTools.makeReadOnly(jTextField9);
            RendererTools.makeReadOnly(jTextField10);
            RendererTools.makeReadOnly(jTextField11);
            RendererTools.makeReadOnly(jTextField12);
            RendererTools.makeReadOnly(jTextField13);
            RendererTools.makeReadOnly(jTextField14);
            RendererTools.makeReadOnly(jTextField1);
            RendererTools.makeReadOnly(jTextField15);
            RendererTools.makeReadOnly(defaultBindableDateChooser1);
            RendererTools.makeReadOnly(defaultBindableDateChooser2);
            RendererTools.makeReadOnly(defaultBindableDateChooser3);
            RendererTools.makeReadOnly(defaultBindableDateChooser4);
            RendererTools.makeReadOnly(defaultBindableDateChooser5);
            RendererTools.makeReadOnly(defaultBindableDateChooser6);
            RendererTools.makeReadOnly(defaultBindableReferenceCombo2);
            RendererTools.makeReadOnly(defaultBindableReferenceCombo3);
            RendererTools.makeReadOnly(defaultBindableReferenceCombo5);
            RendererTools.makeReadOnly(defaultBindableReferenceCombo6);
            RendererTools.makeReadOnly(defaultBindableReferenceCombo7);
            RendererTools.makeReadOnly(defaultBindableReferenceCombo8);
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        GridBagConstraints gridBagConstraints;
        bindingGroup = new BindingGroup();

        final SemiRoundedPanel panBeschreibungTitle = new SemiRoundedPanel();
        final JLabel lblHeaderAllgemein1 = new JLabel();
        final JScrollPane jScrollPane3 = new JScrollPane();
        final JPanel panBeschreibungContent = new JPanel();
        final JPanel jPanel9 = new JPanel();
        final JLabel jLabel84 = new JLabel();
        final JLabel jLabel1 = new JLabel();
        final JPanel jPanel6 = new JPanel();
        jTextField2 = new JTextField();
        final JLabel jLabel2 = new JLabel();
        final JLabel jLabel7 = new JLabel();
        jFormattedTextField1 = new JFormattedTextField();
        jFormattedTextField2 = new JFormattedTextField();
        final Box.Filler filler1 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        jTextField6 = new JTextField();
        final JLabel jLabel3 = new JLabel();
        final JPanel jPanel8 = new JPanel();
        jTextField3 = new JTextField();
        if (editable) {
            lblGeom = new JLabel();
        }
        if (editable) {
            cbGeom = new DefaultCismapGeometryComboBoxEditor();
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).setMetaClass(MC__GEOM);
        }
        final Box.Filler filler2 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        final JLabel jLabel4 = new JLabel();
        jTextField4 = new JTextField();
        final JLabel jLabel5 = new JLabel();
        final JPanel jPanel5 = new JPanel();
        jCheckBox11 = new JCheckBox();
        jCheckBox2 = new JCheckBox();
        jTextField5 = new JTextField();
        final JLabel jLabel43 = new JLabel();
        final JLabel jLabel45 = new JLabel();
        final JPanel jPanel3 = new JPanel();
        jTextField10 = new JTextField();
        final JLabel jLabel47 = new JLabel();
        jTextField14 = new JTextField();
        jTextField13 = new JTextField();
        final JLabel jLabel46 = new JLabel();
        jTextField12 = new JTextField();
        final JLabel jLabel44 = new JLabel();
        jTextField11 = new JTextField();
        final JLabel jLabel8 = new JLabel();
        final JLabel jLabel9 = new JLabel();
        final JLabel jLabel10 = new JLabel();
        final JPanel jPanel1 = new JPanel();
        jCheckBox3 = new JCheckBox();
        final JLabel jLabel14 = new JLabel();
        jTextField7 = new JTextField();
        jCheckBox4 = new JCheckBox();
        final JLabel jLabel15 = new JLabel();
        jTextField8 = new JTextField();
        jCheckBox5 = new JCheckBox();
        final JLabel jLabel16 = new JLabel();
        jTextField9 = new JTextField();
        final JLabel jLabel20 = new JLabel();
        jTextField1 = new JTextField();
        final JLabel jLabel21 = new JLabel();
        jTextField15 = new JTextField();
        final JLabel jLabel11 = new JLabel();
        final JLabel jLabel19 = new JLabel();
        final JPanel jPanel10 = new JPanel();
        jCheckBox6 = new JCheckBox();
        jCheckBox8 = new JCheckBox();
        jCheckBox10 = new JCheckBox();
        jCheckBox7 = new JCheckBox();
        jCheckBox9 = new JCheckBox();
        final JLabel jLabel6 = new JLabel();
        final JLabel jLabel17 = new JLabel();
        final JLabel jLabel12 = new JLabel();
        final JPanel jPanel2 = new JPanel();
        defaultBindableDateChooser1 = new DefaultBindableDateChooser();
        defaultBindableDateChooser2 = new DefaultBindableDateChooser();
        defaultBindableDateChooser3 = new DefaultBindableDateChooser();
        jCheckBox1 = new JCheckBox();
        final JLabel jLabel13 = new JLabel();
        final JLabel jLabel18 = new JLabel();
        final JLabel jLabel85 = new JLabel();
        defaultBindableDateChooser6 = new DefaultBindableDateChooser();
        defaultBindableDateChooser4 = new DefaultBindableDateChooser();
        defaultBindableDateChooser5 = new DefaultBindableDateChooser();
        defaultBindableReferenceCombo3 = new DefaultBindableReferenceCombo(
                MC__PRUEFUNGSART,
                true,
                false);
        defaultBindableReferenceCombo2 = new DefaultBindableReferenceCombo(
                MC__PRUEFUNGSART,
                true,
                false);
        final JLabel jLabel49 = new JLabel();
        final JLabel jLabel48 = new JLabel();
        final Box.Filler filler3 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        final JLabel jLabel50 = new JLabel();
        final JLabel jLabel51 = new JLabel();
        final JPanel jPanel7 = new JPanel();
        final JLabel jLabel52 = new JLabel();
        final JLabel jLabel53 = new JLabel();
        defaultBindableReferenceCombo7 = new DefaultBindableReferenceCombo(
                MC__BEURTEILUNG,
                true,
                false);
        defaultBindableReferenceCombo8 = new DefaultBindableReferenceCombo(
                MC__EINSATZ,
                true,
                false);
        defaultBindableReferenceCombo5 = new DefaultBindableReferenceCombo(
                MC__BEURTEILUNG,
                true,
                false);
        defaultBindableReferenceCombo6 = new DefaultBindableReferenceCombo(
                MC__BEURTEILUNG,
                true,
                false);
        final Box.Filler filler4 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        final JPanel jPanel30 = new JPanel();

        setName("Form"); // NOI18N
        setOpaque(false);
        setLayout(new GridBagLayout());

        panBeschreibungTitle.setBackground(new Color(51, 51, 51));
        panBeschreibungTitle.setName("panBeschreibungTitle"); // NOI18N
        panBeschreibungTitle.setLayout(new FlowLayout());

        lblHeaderAllgemein1.setForeground(new Color(255, 255, 255));
        Mnemonics.setLocalizedText(
            lblHeaderAllgemein1,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.lblHeaderAllgemein1.text")); // NOI18N
        lblHeaderAllgemein1.setName("lblHeaderAllgemein1");                                                          // NOI18N
        panBeschreibungTitle.add(lblHeaderAllgemein1);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        add(panBeschreibungTitle, gridBagConstraints);

        jScrollPane3.setBorder(null);
        jScrollPane3.setName("jScrollPane3"); // NOI18N
        jScrollPane3.setOpaque(false);

        panBeschreibungContent.setName("panBeschreibungContent"); // NOI18N
        panBeschreibungContent.setOpaque(false);
        panBeschreibungContent.setLayout(new GridBagLayout());

        jPanel9.setName("jPanel9"); // NOI18N
        jPanel9.setOpaque(false);
        jPanel9.setLayout(new GridBagLayout());

        Mnemonics.setLocalizedText(
            jLabel84,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jLabel84.text")); // NOI18N
        jLabel84.setName("jLabel84");                                                                     // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(1, 0, 1, 5);
        jPanel9.add(jLabel84, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel1,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1");                                                                      // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(1, 0, 1, 5);
        jPanel9.add(jLabel1, gridBagConstraints);

        jPanel6.setName("jPanel6"); // NOI18N
        jPanel6.setOpaque(false);
        jPanel6.setLayout(new GridBagLayout());

        jTextField2.setName("jTextField2"); // NOI18N

        Binding binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.name}"),
                jTextField2,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel6.add(jTextField2, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel2,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2");                                                                      // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 10, 1, 5);
        jPanel6.add(jLabel2, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel7,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7");                                                                      // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 10, 1, 5);
        jPanel6.add(jLabel7, gridBagConstraints);

        jFormattedTextField1.setFormatterFactory(new DefaultFormatterFactory(
                new NumberFormatter(new DecimalFormat("#0000"))));
        jFormattedTextField1.setText(NbBundle.getMessage(
                TreppeBeschreibungPanel.class,
                "TreppeBeschreibungPanel.jFormattedTextField1.text")); // NOI18N
        jFormattedTextField1.setName("jFormattedTextField1");          // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.nummer}"),
                jFormattedTextField1,
                BeanProperty.create("value"));
        binding.setConverter(CONVERTER_INT);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel6.add(jFormattedTextField1, gridBagConstraints);

        jFormattedTextField2.setFormatterFactory(new DefaultFormatterFactory(
                new NumberFormatter(new DecimalFormat("#00000"))));
        jFormattedTextField2.setText(NbBundle.getMessage(
                TreppeBeschreibungPanel.class,
                "TreppeBeschreibungPanel.jFormattedTextField2.text")); // NOI18N
        jFormattedTextField2.setName("jFormattedTextField2");          // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.strassenschluessel}"),
                jFormattedTextField2,
                BeanProperty.create("value"));
        binding.setConverter(CONVERTER_INT);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel6.add(jFormattedTextField2, gridBagConstraints);

        filler1.setName("filler1"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 50, 0, 50);
        jPanel6.add(filler1, gridBagConstraints);

        jTextField6.setEditable(false);
        jTextField6.setName("jTextField6"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel6.add(jTextField6, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel9.add(jPanel6, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel3,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3");                                                                      // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(1, 0, 1, 5);
        jPanel9.add(jLabel3, gridBagConstraints);

        jPanel8.setName("jPanel8"); // NOI18N
        jPanel8.setOpaque(false);
        jPanel8.setLayout(new GridBagLayout());

        jTextField3.setName("jTextField3"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.lagebeschreibung}"),
                jTextField3,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel8.add(jTextField3, gridBagConstraints);

        if (editable) {
            if (editable) {
                Mnemonics.setLocalizedText(
                    lblGeom,
                    NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.lblGeom.text")); // NOI18N
            }
            if (editable) {
                lblGeom.setName("lblGeom");                                                                      // NOI18N
            }
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.ipady = 10;
            gridBagConstraints.anchor = GridBagConstraints.SOUTH;
            gridBagConstraints.weighty = 1.0;
            gridBagConstraints.insets = new Insets(1, 10, 1, 5);
            jPanel8.add(lblGeom, gridBagConstraints);
        }

        if (editable) {
            cbGeom.setMinimumSize(new Dimension(41, 25));
            cbGeom.setName("cbGeom"); // NOI18N
            cbGeom.setPreferredSize(new Dimension(41, 25));

            binding = Bindings.createAutoBinding(
                    AutoBinding.UpdateStrategy.READ_WRITE,
                    this,
                    ELProperty.create("${cidsBean.geometrie}"),
                    cbGeom,
                    BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeom).getConverter());
            bindingGroup.addBinding(binding);

            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.weighty = 1.0;
            gridBagConstraints.insets = new Insets(1, 0, 1, 0);
            jPanel8.add(cbGeom, gridBagConstraints);
        }

        filler2.setName("filler2"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 100, 0, 100);
        jPanel8.add(filler2, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel9.add(jPanel8, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel4,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4");                                                                      // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(1, 0, 1, 5);
        jPanel9.add(jLabel4, gridBagConstraints);

        jTextField4.setName("jTextField4"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.umgebung}"),
                jTextField4,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel9.add(jTextField4, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel5,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5");                                                                      // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(1, 0, 1, 5);
        jPanel9.add(jLabel5, gridBagConstraints);

        jPanel5.setName("jPanel5"); // NOI18N
        jPanel5.setOpaque(false);
        jPanel5.setLayout(new GridBagLayout());

        Mnemonics.setLocalizedText(
            jCheckBox11,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jCheckBox11.text")); // NOI18N
        jCheckBox11.setContentAreaFilled(false);
        jCheckBox11.setHorizontalTextPosition(SwingConstants.LEADING);
        jCheckBox11.setName("jCheckBox11");                                                                  // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.ist_din1076}"),
                jCheckBox11,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = GridBagConstraints.LINE_END;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 10, 1, 0);
        jPanel5.add(jCheckBox11, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jCheckBox2,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jCheckBox2.text")); // NOI18N
        jCheckBox2.setContentAreaFilled(false);
        jCheckBox2.setHorizontalTextPosition(SwingConstants.LEADING);
        jCheckBox2.setName("jCheckBox2");                                                                   // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.ist_denkmalschutz}"),
                jCheckBox2,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = GridBagConstraints.LINE_END;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 10, 1, 0);
        jPanel5.add(jCheckBox2, gridBagConstraints);

        jTextField5.setName("jTextField5"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.baujahr_ca}"),
                jTextField5,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel5.add(jTextField5, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel9.add(jPanel5, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel43,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jLabel43.text")); // NOI18N
        jLabel43.setName("jLabel43");                                                                     // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(1, 0, 1, 5);
        jPanel9.add(jLabel43, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel45,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jLabel45.text")); // NOI18N
        jLabel45.setName("jLabel45");                                                                     // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(1, 0, 1, 5);
        jPanel9.add(jLabel45, gridBagConstraints);

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setOpaque(false);
        jPanel3.setLayout(new GridBagLayout());

        jTextField10.setName("jTextField10"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.eigentuemer}"),
                jTextField10,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel3.add(jTextField10, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel47,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jLabel47.text")); // NOI18N
        jLabel47.setName("jLabel47");                                                                     // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 10, 1, 5);
        jPanel3.add(jLabel47, gridBagConstraints);

        jTextField14.setName("jTextField14"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.unterhaltungspflicht}"),
                jTextField14,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel3.add(jTextField14, gridBagConstraints);

        jTextField13.setName("jTextField13"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.verkehrssicherungspflicht}"),
                jTextField13,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel3.add(jTextField13, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel46,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jLabel46.text")); // NOI18N
        jLabel46.setName("jLabel46");                                                                     // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 10, 1, 5);
        jPanel3.add(jLabel46, gridBagConstraints);

        jTextField12.setName("jTextField12"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.kontaktdaten_anlieger}"),
                jTextField12,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel3.add(jTextField12, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel9.add(jPanel3, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel44,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jLabel44.text")); // NOI18N
        jLabel44.setName("jLabel44");                                                                     // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(1, 0, 1, 5);
        jPanel9.add(jLabel44, gridBagConstraints);

        jTextField11.setName("jTextField11"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.baulast}"),
                jTextField11,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel9.add(jTextField11, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel8,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8");                                                                      // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(1, 0, 1, 5);
        jPanel9.add(jLabel8, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel9,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9");                                                                      // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(1, 0, 1, 5);
        jPanel9.add(jLabel9, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel10,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10");                                                                     // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(1, 0, 1, 5);
        jPanel9.add(jLabel10, gridBagConstraints);

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setOpaque(false);
        jPanel1.setLayout(new GridBagLayout());

        Mnemonics.setLocalizedText(
            jCheckBox3,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jCheckBox3.text")); // NOI18N
        jCheckBox3.setContentAreaFilled(false);
        jCheckBox3.setName("jCheckBox3");                                                                   // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.ist_zugaenge}"),
                jCheckBox3,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel1.add(jCheckBox3, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel14,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14");                                                                     // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 10, 1, 5);
        jPanel1.add(jLabel14, gridBagConstraints);

        jTextField7.setName("jTextField7"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.art_zugaenge}"),
                jTextField7,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                jCheckBox3,
                ELProperty.create("${selected}"),
                jTextField7,
                BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel1.add(jTextField7, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jCheckBox4,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jCheckBox4.text")); // NOI18N
        jCheckBox4.setContentAreaFilled(false);
        jCheckBox4.setName("jCheckBox4");                                                                   // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.ist_gebaeude}"),
                jCheckBox4,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel1.add(jCheckBox4, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel15,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15");                                                                     // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 10, 1, 5);
        jPanel1.add(jLabel15, gridBagConstraints);

        jTextField8.setName("jTextField8"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.art_gebaeude}"),
                jTextField8,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                jCheckBox4,
                ELProperty.create("${selected}"),
                jTextField8,
                BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel1.add(jTextField8, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jCheckBox5,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jCheckBox5.text")); // NOI18N
        jCheckBox5.setContentAreaFilled(false);
        jCheckBox5.setName("jCheckBox5");                                                                   // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.ist_beleuchtung}"),
                jCheckBox5,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel1.add(jCheckBox5, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel16,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16");                                                                     // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 10, 1, 5);
        jPanel1.add(jLabel16, gridBagConstraints);

        jTextField9.setName("jTextField9"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.art_beleuchtung}"),
                jTextField9,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                jCheckBox5,
                ELProperty.create("${selected}"),
                jTextField9,
                BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel1.add(jTextField9, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel9.add(jPanel1, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel20,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jLabel20.text")); // NOI18N
        jLabel20.setName("jLabel20");                                                                     // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(1, 0, 1, 5);
        jPanel9.add(jLabel20, gridBagConstraints);

        jTextField1.setName("jTextField1"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.eigenschaften_treppengruendung}"),
                jTextField1,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel9.add(jTextField1, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel21,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jLabel21.text")); // NOI18N
        jLabel21.setName("jLabel21");                                                                     // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(1, 0, 1, 5);
        jPanel9.add(jLabel21, gridBagConstraints);

        jTextField15.setName("jTextField15"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.eigenschaften_treppenlagerung}"),
                jTextField15,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel9.add(jTextField15, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel11,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11");                                                                     // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(1, 0, 1, 5);
        jPanel9.add(jLabel11, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel19,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jLabel19.text")); // NOI18N
        jLabel19.setName("jLabel19");                                                                     // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(1, 0, 1, 5);
        jPanel9.add(jLabel19, gridBagConstraints);

        jPanel10.setName("jPanel10"); // NOI18N
        jPanel10.setOpaque(false);
        jPanel10.setLayout(new GridLayout(0, 3, 0, 2));

        Mnemonics.setLocalizedText(
            jCheckBox6,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jCheckBox6.text")); // NOI18N
        jCheckBox6.setContentAreaFilled(false);
        jCheckBox6.setName("jCheckBox6");                                                                   // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.ist_handlauf_einseitig}"),
                jCheckBox6,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        jPanel10.add(jCheckBox6);

        Mnemonics.setLocalizedText(
            jCheckBox8,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jCheckBox8.text")); // NOI18N
        jCheckBox8.setContentAreaFilled(false);
        jCheckBox8.setName("jCheckBox8");                                                                   // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.ist_handlauf_beidseitig}"),
                jCheckBox8,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        jPanel10.add(jCheckBox8);

        Mnemonics.setLocalizedText(
            jCheckBox10,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jCheckBox10.text")); // NOI18N
        jCheckBox10.setContentAreaFilled(false);
        jCheckBox10.setName("jCheckBox10");                                                                  // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.ist_handlauf_durchgaengig}"),
                jCheckBox10,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        jPanel10.add(jCheckBox10);

        Mnemonics.setLocalizedText(
            jCheckBox7,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jCheckBox7.text")); // NOI18N
        jCheckBox7.setContentAreaFilled(false);
        jCheckBox7.setName("jCheckBox7");                                                                   // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.ist_rampen}"),
                jCheckBox7,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        jPanel10.add(jCheckBox7);

        Mnemonics.setLocalizedText(
            jCheckBox9,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jCheckBox9.text")); // NOI18N
        jCheckBox9.setContentAreaFilled(false);
        jCheckBox9.setName("jCheckBox9");                                                                   // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.ist_taktile_elemente}"),
                jCheckBox9,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        jPanel10.add(jCheckBox9);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel9.add(jPanel10, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel6,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6");                                                                      // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(1, 0, 1, 5);
        jPanel9.add(jLabel6, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel17,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jLabel17.text")); // NOI18N
        jLabel17.setName("jLabel17");                                                                     // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(1, 0, 1, 5);
        jPanel9.add(jLabel17, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel12,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12");                                                                     // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(1, 0, 1, 5);
        jPanel9.add(jLabel12, gridBagConstraints);

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setOpaque(false);
        jPanel2.setLayout(new GridBagLayout());

        defaultBindableDateChooser1.setName("defaultBindableDateChooser1"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.datum_letzte_sanierung}"),
                defaultBindableDateChooser1,
                BeanProperty.create("date"));
        binding.setConverter(defaultBindableDateChooser1.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel2.add(defaultBindableDateChooser1, gridBagConstraints);

        defaultBindableDateChooser2.setName("defaultBindableDateChooser2"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.datum_letzte_bauwerksbesichtigung}"),
                defaultBindableDateChooser2,
                BeanProperty.create("date"));
        binding.setConverter(defaultBindableDateChooser2.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel2.add(defaultBindableDateChooser2, gridBagConstraints);

        defaultBindableDateChooser3.setName("defaultBindableDateChooser3"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.datum_naechste_bauwerksbesichtigung}"),
                defaultBindableDateChooser3,
                BeanProperty.create("date"));
        binding.setConverter(defaultBindableDateChooser3.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel2.add(defaultBindableDateChooser3, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jCheckBox1,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jCheckBox1.text")); // NOI18N
        jCheckBox1.setContentAreaFilled(false);
        jCheckBox1.setHorizontalTextPosition(SwingConstants.LEADING);
        jCheckBox1.setName("jCheckBox1");                                                                   // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.ist_gesperrt}"),
                jCheckBox1,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 10, 1, 0);
        jPanel2.add(jCheckBox1, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel13,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13");                                                                     // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 10, 1, 5);
        jPanel2.add(jLabel13, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel18,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jLabel18.text")); // NOI18N
        jLabel18.setName("jLabel18");                                                                     // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 10, 1, 5);
        jPanel2.add(jLabel18, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel85,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jLabel85.text")); // NOI18N
        jLabel85.setName("jLabel85");                                                                     // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 5, 1, 5);
        jPanel2.add(jLabel85, gridBagConstraints);

        defaultBindableDateChooser6.setName("defaultBindableDateChooser6"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.datum_gesperrt_seit}"),
                defaultBindableDateChooser6,
                BeanProperty.create("date"));
        binding.setConverter(defaultBindableDateChooser6.getConverter());
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                jCheckBox1,
                ELProperty.create("${selected}"),
                defaultBindableDateChooser6,
                BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel2.add(defaultBindableDateChooser6, gridBagConstraints);

        defaultBindableDateChooser4.setName("defaultBindableDateChooser4"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.datum_letzte_pruefung}"),
                defaultBindableDateChooser4,
                BeanProperty.create("date"));
        binding.setConverter(defaultBindableDateChooser4.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel2.add(defaultBindableDateChooser4, gridBagConstraints);

        defaultBindableDateChooser5.setName("defaultBindableDateChooser5"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.datum_naechste_pruefung}"),
                defaultBindableDateChooser5,
                BeanProperty.create("date"));
        binding.setConverter(defaultBindableDateChooser5.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel2.add(defaultBindableDateChooser5, gridBagConstraints);

        defaultBindableReferenceCombo3.setName("defaultBindableReferenceCombo3"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.art_letzte_pruefung}"),
                defaultBindableReferenceCombo3,
                BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel2.add(defaultBindableReferenceCombo3, gridBagConstraints);

        defaultBindableReferenceCombo2.setName("defaultBindableReferenceCombo2"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.art_naechste_pruefung}"),
                defaultBindableReferenceCombo2,
                BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel2.add(defaultBindableReferenceCombo2, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel49,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jLabel49.text")); // NOI18N
        jLabel49.setName("jLabel49");                                                                     // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 10, 1, 5);
        jPanel2.add(jLabel49, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel48,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jLabel48.text")); // NOI18N
        jLabel48.setName("jLabel48");                                                                     // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 10, 1, 5);
        jPanel2.add(jLabel48, gridBagConstraints);

        filler3.setName("filler3"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(filler3, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel9.add(jPanel2, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel50,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jLabel50.text")); // NOI18N
        jLabel50.setName("jLabel50");                                                                     // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(1, 0, 1, 5);
        jPanel9.add(jLabel50, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel51,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jLabel51.text")); // NOI18N
        jLabel51.setName("jLabel51");                                                                     // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new Insets(1, 0, 1, 5);
        jPanel9.add(jLabel51, gridBagConstraints);

        jPanel7.setName("jPanel7"); // NOI18N
        jPanel7.setOpaque(false);
        jPanel7.setLayout(new GridBagLayout());

        Mnemonics.setLocalizedText(
            jLabel52,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jLabel52.text")); // NOI18N
        jLabel52.setName("jLabel52");                                                                     // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 10, 1, 5);
        jPanel7.add(jLabel52, gridBagConstraints);

        Mnemonics.setLocalizedText(
            jLabel53,
            NbBundle.getMessage(TreppeBeschreibungPanel.class, "TreppeBeschreibungPanel.jLabel53.text")); // NOI18N
        jLabel53.setName("jLabel53");                                                                     // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 10, 1, 5);
        jPanel7.add(jLabel53, gridBagConstraints);

        defaultBindableReferenceCombo7.setName("defaultBindableReferenceCombo7"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.beurteilung_dauerhaftigkeit}"),
                defaultBindableReferenceCombo7,
                BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel7.add(defaultBindableReferenceCombo7, gridBagConstraints);

        defaultBindableReferenceCombo8.setName("defaultBindableReferenceCombo8"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.notwendigkeit_eingriff}"),
                defaultBindableReferenceCombo8,
                BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel7.add(defaultBindableReferenceCombo8, gridBagConstraints);

        defaultBindableReferenceCombo5.setName("defaultBindableReferenceCombo5"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.beurteilung_standsicherheit}"),
                defaultBindableReferenceCombo5,
                BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel7.add(defaultBindableReferenceCombo5, gridBagConstraints);

        defaultBindableReferenceCombo6.setName("defaultBindableReferenceCombo6"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.beurteilung_verkehrssicherheit}"),
                defaultBindableReferenceCombo6,
                BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(1, 0, 1, 0);
        jPanel7.add(defaultBindableReferenceCombo6, gridBagConstraints);

        filler4.setName("filler4"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel7.add(filler4, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel9.add(jPanel7, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 0, 10);
        panBeschreibungContent.add(jPanel9, gridBagConstraints);

        jPanel30.setName("jPanel30"); // NOI18N
        jPanel30.setOpaque(false);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panBeschreibungContent.add(jPanel30, gridBagConstraints);

        jScrollPane3.setViewportView(panBeschreibungContent);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane3, gridBagConstraints);
        jScrollPane3.getViewport().setOpaque(false);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     */
    private void reloadStrasse() {
        final Integer strassenschluessel = (Integer)cidsBean.getProperty("strassenschluessel");

        if (strassenschluessel != null) {
            jTextField6.setText("<html><i>wird geladen...");
            new SwingWorker<CidsBean, Void>() {

                    @Override
                    protected CidsBean doInBackground() throws Exception {
                        final Collection<MetaObjectNode> mons = SessionManager.getProxy()
                                    .customServerSearch(new CustomStrassenSearchStatement(
                                            strassenschluessel.toString(),
                                            true),
                                        getConnectionContext());
                        if ((mons != null) && !mons.isEmpty()) {
                            final MetaObjectNode mon = mons.toArray(new MetaObjectNode[0])[0];

                            final MetaObject mo = SessionManager.getProxy()
                                        .getMetaObject(mon.getObjectId(), mon.getClassId(), mon.getDomain());
                            final CidsBean strasseBean = mo.getBean();
                            return strasseBean;
                        } else {
                            return null;
                        }
                    }

                    @Override
                    protected void done() {
                        try {
                            final CidsBean strasseBean = get();
                            if (strasseBean != null) {
                                jTextField6.setText((String)strasseBean.getProperty("name"));
                            } else {
                                jTextField6.setText(null);
                            }
                        } catch (final Exception ex) {
                            LOG.warn("Straße konnte nichte geladen werden.", ex);
                        }
                    }
                }.execute();
        } else {
            jTextField6.setText(null);
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
            reloadStrasse();
        }
    }

    @Override
    public void dispose() {
        bindingGroup.unbind();

        if (cbGeom != null) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).dispose();
            cbGeom = null;
        }
        if (cidsBean != null) {
            cidsBean.removePropertyChangeListener(propChangeListener);
            cidsBean = null;
        }
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }
}
