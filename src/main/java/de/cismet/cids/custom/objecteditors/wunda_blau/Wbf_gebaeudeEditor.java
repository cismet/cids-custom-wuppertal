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
/*
 * Wbf_gebaeudeEditor.java
 *
 * Created on 06.04.2009,  14:06:27
 */
package de.cismet.cids.custom.objecteditors.wunda_blau;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.Binding.SyncFailure;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.BindingListener;
import org.jdesktop.beansbinding.PropertyStateEvent;
import org.jdesktop.beansbinding.Validator;
import org.jdesktop.beansbinding.Validator.Result;
import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.ext.LockableUI;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import org.openide.util.WeakListeners;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.BindingGroupStore;
import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultCustomObjectEditor;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.tools.metaobjectrenderer.Titled;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class Wbf_gebaeudeEditor extends DefaultCustomObjectEditor implements Titled, BindingGroupStore {

    //~ Static fields/initializers ---------------------------------------------

    public static final Color COLOR_TXT_BACK = new Color(230, 230, 230);

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(Wbf_gebaeudeEditor.class);

    //~ Instance fields --------------------------------------------------------

    MyLockableUI luiGeb;
    MyLockableUI luiVorg;
    JXLayer lVorg;
    private String domain = "";
//    ImageIcon warn = new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/icons/warn.png"));
    private final HashMap<String, Collection<String>> validationDependencies = new HashMap<>();
    private final DefaultListCellRenderer dlcr = new DefaultListCellRenderer();
    private boolean editable;
    private String title = "";
    private final List<PropertyChangeListener> strongReferencesToWeakListeners = new ArrayList<>();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cbEinkommensgruppe;
    private javax.swing.JComboBox cbMassnahmenkategorisierung;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbNutzungsart;
    private javax.swing.JComboBox cboGeom;
    private javax.swing.JButton cmdAddVorgang;
    private javax.swing.JButton cmdRemoveVorgang;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker2;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker3;
    private javax.swing.JLabel lblAnschriftEigentuemer;
    private javax.swing.JLabel lblAnschriftGebaeude;
    private javax.swing.JLabel lblAnzahlWohneinheiten;
    private javax.swing.JLabel lblBemerkungen;
    private javax.swing.JLabel lblBeschreibung1;
    private javax.swing.JLabel lblBetroffeneWohneinheiten;
    private javax.swing.JLabel lblBewilligungsNr;
    private javax.swing.JLabel lblBewilligungsdatum;
    private javax.swing.JLabel lblBindungsdauer;
    private javax.swing.JLabel lblEinkommensgruppe;
    private javax.swing.JLabel lblFolgenummer;
    private javax.swing.JLabel lblGeom;
    private javax.swing.JLabel lblHoeheMietpreisbindung;
    private javax.swing.JLabel lblMassnahmetyp;
    private javax.swing.JLabel lblNameEigentuemer;
    private javax.swing.JLabel lblNutzungsart;
    private javax.swing.JLabel lblSachbearbeiterTech;
    private javax.swing.JLabel lblSachbearbeiterVerw;
    private javax.swing.JLabel lblUebergabedatum;
    private javax.swing.JLabel lblVergabeNr;
    private javax.swing.JLabel lblVorgangsliste;
    private javax.swing.JList lstVorgaenge;
    private javax.swing.JPanel panGebaeude;
    private javax.swing.JPanel panVorgang;
    private javax.swing.JPanel panVorgangX;
    private javax.swing.JPanel panVorgangsauswahl;
    private de.cismet.cids.editors.converters.SqlDateToUtilDateConverter sqlDateToUtilDateConverter;
    private javax.swing.JTextField txtAnzahlWohneinheiten;
    private javax.swing.JTextArea txtBemerkungen;
    private javax.swing.JTextField txtBetroffeneWohneinheiten;
    private javax.swing.JTextField txtBewilligungsNr;
    private javax.swing.JTextField txtEigentuemerName;
    private javax.swing.JTextField txtFolgenummer;
    private javax.swing.JTextField txtHoeheMietpreisbindung;
    private javax.swing.JTextArea txtMassnahmenbeschreibung;
    private javax.swing.JTextField txtSachbearbeiterTechnik;
    private javax.swing.JTextField txtSachbearbeiterVerwaltung;
    private javax.swing.JTextField txtVergabeNr;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Wbf_gebaeudeEditor object.
     */
    public Wbf_gebaeudeEditor() {
        this(true);
    }

    /**
     * Creates new form Wbf_gebaeudeEditor.
     *
     * @param  editable  DOCUMENT ME!
     */
    public Wbf_gebaeudeEditor(final boolean editable) {
        this.editable = editable;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        super.initWithConnectionContext(connectionContext);
        initComponents();

        panVorgangX.setOpaque(false);
        panVorgangsauswahl.setOpaque(false);
        panGebaeude.setOpaque(false);

        ((RoundedPanel)panVorgangX).setAlpha(255);
        ((RoundedPanel)panGebaeude).setAlpha(255);
        ((RoundedPanel)panVorgangsauswahl).setAlpha(255);

        panVorgangX.setBackground(getBackground());
        panGebaeude.setBackground(getBackground());
        panVorgangsauswahl.setBackground(getBackground());

        if (!editable) {
            bindingGroup.removeBinding(bindingGroup.getBinding("geometrie"));
            final GridBagConstraints cbgVorgang = ((GridBagLayout)getLayout()).getConstraints(panVorgang);
            lVorg = new JXLayer(panVorgang);
            luiVorg = new MyLockableUI();

            luiVorg.setLocked(!editable);
            lVorg.setUI(luiVorg);
            lVorg.setOpaque(false);
            add(lVorg, cbgVorgang);
            final GridBagConstraints cbgGeb = ((GridBagLayout)getLayout()).getConstraints(panGebaeude);
            final JXLayer lGeb = new JXLayer(panGebaeude);
            lGeb.setOpaque(false);
            luiGeb = new MyLockableUI();
            luiGeb.setLocked(!editable);
            lGeb.setUI(luiGeb);
            add(lGeb, cbgGeb);

            cmdAddVorgang.setVisible(false);
            cmdRemoveVorgang.setVisible(false);
        }

        final Vector v = new Vector(Arrays.asList(panVorgangX.getComponents()));
        v.addAll(Arrays.asList(panGebaeude.getComponents()));

        for (final Object inputField : v) {
//            if (inputField instanceof JTextField || inputField instanceof JTextArea) {
//
//                ((JComponent) inputField).setBorder(null);
//                ((JComponent) inputField).setOpaque(editable);
//                if (!editable) {
//                    ((JComponent) inputField).setBackground(COLOR_TXT_BACK);
//                }
//            } else
            if (inputField instanceof DefaultBindableReferenceCombo) {
                ((DefaultBindableReferenceCombo)inputField).setFakeModel(!editable);
                // ((DefaultBindableReferenceCombo) inputField).setFakeModel(true);
            }
        }

        lblGeom.setVisible(editable);
        cboGeom.setVisible(editable);

        final MassnahmenkategorisierungValidator massnahmenkategorisierungValidator =
            new MassnahmenkategorisierungValidator();
        cbMassnahmenkategorisierung.setRenderer(new ListCellRenderer() {

                @Override
                public Component getListCellRendererComponent(final JList list,
                        final Object value,
                        final int index,
                        final boolean isSelected,
                        final boolean cellHasFocus) {
                    final JLabel l = (JLabel)dlcr.getListCellRendererComponent(
                            list,
                            value,
                            index,
                            isSelected,
                            cellHasFocus);
                    if (value instanceof CidsBean) {
                        final Validator.Result result = massnahmenkategorisierungValidator.validate((CidsBean)value);
                        if (result != null) {
                            if (result.getType().equals(result.ERROR)) {
                                l.setForeground(Color.red);
                            } else {
                                l.setForeground(Color.MAGENTA);
                            }
                        } else {
                            l.setForeground(Color.black);
                        }
                    } else if ((value == null) || (value instanceof DefaultBindableReferenceCombo.NullableItem)) {
                        l.setText(" ");
                    }
                    return l;
                }
            });

        cbNutzungsart.setNullValueRepresentation("Bitte Nutzungsart auswählen");

        validationDependencies.put(
            "art",
            Arrays.asList(
                "anzahl_wohneinheiten",
                "massnahmenkategorisierung",
                "hoehe_mietpreisbindung",
                "bindungsdauer"));
        bindingGroup.addBindingListener(new BindingListener() {

                @Override
                public void bindingBecameBound(final Binding binding) {
                    if ((binding.getName() != null) && binding.getName().equalsIgnoreCase("VORGAENGE")) {
                        lstVorgaenge.setSelectedIndex(0);
                    }
                }

                @Override
                public void bindingBecameUnbound(final Binding binding) {
                }

                @Override
                public void syncFailed(final Binding binding, final SyncFailure failure) {
                    if (Wbf_gebaeudeEditor.this.editable) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("syncFailed");
                        }
                        final Object target = binding.getTargetObject();
                        if (target instanceof JComponent) { // && !(target instanceof JComboBox)) {
                            final JComponent c = (JComponent)target;
                            c.setForeground(Color.red);

                            try {
                                c.setToolTipText(failure.getValidationResult().getDescription());
                            } catch (Exception skip) {
                            }
                        } else {
                            LOG.error("keine JCOmponent");
                        }
                    }
                }

                @Override
                public void syncWarning(final Binding binding, final SyncFailure failure) {
                    if (Wbf_gebaeudeEditor.this.editable) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("synWarning " + failure);
                        }
                        final Object target = binding.getTargetObject();
                        if (target instanceof JComponent) { // && !(target instanceof JComboBox)) {
                            final JComponent c = (JComponent)target;
                            c.setForeground(Color.magenta);
                            c.setToolTipText(failure.getValidationResult().getDescription());
                        } else {
                            LOG.error("keine JCOmponent");
                        }
                    }
                }

                @Override
                public void synced(final Binding binding) {
                    if (Wbf_gebaeudeEditor.this.editable) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("sync");
                        }
                        final Object target = binding.getTargetObject();
                        if (target instanceof JComponent) { // && !(target instanceof JComboBox)) {
                            final JComponent c = (JComponent)target;
                            c.setForeground(Color.black);
                            c.setToolTipText(null);
                        } else {
                            LOG.error("keine JCOmponent");
                        }

                        final String bindingName = binding.getName();
                        if (bindingName != null) {
                            final Collection<String> additionalValidationBindings = validationDependencies.get(
                                    binding.getName());
                            if (LOG.isDebugEnabled()) {
                                LOG.debug(
                                    "for binding "
                                            + bindingName
                                            + "-->> dependencies: "
                                            + additionalValidationBindings);
                            }
                            if (additionalValidationBindings != null) {
                                for (final String name : additionalValidationBindings) {
                                    final Binding b = bindingGroup.getBinding(name);
                                    if (b != null) {
                                        if (LOG.isDebugEnabled()) {
                                            LOG.debug("CheckAgain: " + name);
                                        }
                                        b.saveAndNotify();
                                    }
                                }
                            }
                        }
                    }
                }

                @Override
                public void sourceChanged(final Binding binding, final PropertyStateEvent event) {
                }

                @Override
                public void targetChanged(final Binding binding, final PropertyStateEvent event) {
                }
            });

        lstVorgaenge.setCellRenderer(new ListCellRenderer() {

                @Override
                public Component getListCellRendererComponent(final JList list,
                        final Object value,
                        final int index,
                        final boolean isSelected,
                        final boolean cellHasFocus) {
                    final JLabel l = (JLabel)dlcr.getListCellRendererComponent(
                            list,
                            value,
                            index,
                            isSelected,
                            cellHasFocus);
                    if ((value == null)
                                || ((value instanceof CidsBean)
                                    && (((CidsBean)value).getProperty("vergabenummer") == null))) {
                        l.setText("Neuer Vorgang");
                    }
                    return l;
                }
            });

        cbNutzungsart.setSelectedItem(null);
        final Vector einkommensgruppen = new Vector();
        einkommensgruppen.add("A");
        einkommensgruppen.add("B");
        einkommensgruppen.add(null);

        cbEinkommensgruppe.setModel(new DefaultComboBoxModel(einkommensgruppen));
        cbEinkommensgruppe.setRenderer(new ListCellRenderer() {

                @Override
                public Component getListCellRendererComponent(final JList list,
                        final Object value,
                        final int index,
                        final boolean isSelected,
                        final boolean cellHasFocus) {
                    final JLabel l = (JLabel)dlcr.getListCellRendererComponent(
                            list,
                            value,
                            index,
                            isSelected,
                            cellHasFocus);
                    if (value == null) {
                        l.setText(" ");
                    }
                    return l;
                }
            });
    }

    /**
     * DOCUMENT ME!
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        sqlDateToUtilDateConverter = new de.cismet.cids.editors.converters.SqlDateToUtilDateConverter();
        panGebaeude = new RoundedPanel();
        jLabel1 = new javax.swing.JLabel();
        lblNutzungsart = new javax.swing.JLabel();
        lblAnschriftGebaeude = new javax.swing.JLabel();
        lblAnschriftEigentuemer = new javax.swing.JLabel();
        lblAnzahlWohneinheiten = new javax.swing.JLabel();
        txtEigentuemerName = new javax.swing.JTextField();
        txtAnzahlWohneinheiten = new javax.swing.JTextField();
        cbNutzungsart = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        lblNameEigentuemer = new javax.swing.JLabel();
        lblGeom = new javax.swing.JLabel();
        cboGeom = new DefaultCismapGeometryComboBoxEditor();
        panVorgangsauswahl = new RoundedPanel();
        jPanel1 = new javax.swing.JPanel();
        cmdAddVorgang = new javax.swing.JButton();
        cmdRemoveVorgang = new javax.swing.JButton();
        lblVorgangsliste = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstVorgaenge = new javax.swing.JList();
        panVorgang = new javax.swing.JPanel();
        panVorgangX = new RoundedPanel();
        lblVergabeNr = new javax.swing.JLabel();
        txtVergabeNr = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtBemerkungen = new javax.swing.JTextArea();
        jPanel6 = new javax.swing.JPanel();
        lblSachbearbeiterVerw = new javax.swing.JLabel();
        lblSachbearbeiterTech = new javax.swing.JLabel();
        lblBemerkungen = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtSachbearbeiterTechnik = new javax.swing.JTextField();
        txtSachbearbeiterVerwaltung = new javax.swing.JTextField();
        lblUebergabedatum = new javax.swing.JLabel();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        jLabel2 = new javax.swing.JLabel();
        lblMassnahmetyp = new javax.swing.JLabel();
        lblBetroffeneWohneinheiten = new javax.swing.JLabel();
        lblBeschreibung1 = new javax.swing.JLabel();
        cbMassnahmenkategorisierung = new javax.swing.JComboBox();
        txtBetroffeneWohneinheiten = new javax.swing.JTextField();
        jScrollPane6 = new javax.swing.JScrollPane();
        txtMassnahmenbeschreibung = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        lblBindungsdauer = new javax.swing.JLabel();
        lblHoeheMietpreisbindung = new javax.swing.JLabel();
        lblBewilligungsNr = new javax.swing.JLabel();
        lblBewilligungsdatum = new javax.swing.JLabel();
        txtHoeheMietpreisbindung = new javax.swing.JTextField();
        txtBewilligungsNr = new javax.swing.JTextField();
        jXDatePicker2 = new org.jdesktop.swingx.JXDatePicker();
        jXDatePicker3 = new org.jdesktop.swingx.JXDatePicker();
        lblFolgenummer = new javax.swing.JLabel();
        txtFolgenummer = new javax.swing.JTextField();
        lblEinkommensgruppe = new javax.swing.JLabel();
        cbEinkommensgruppe = new javax.swing.JComboBox();

        setLayout(new java.awt.GridBagLayout());

        panGebaeude.setMinimumSize(new java.awt.Dimension(284, 100));
        panGebaeude.setPreferredSize(new java.awt.Dimension(400, 250));
        panGebaeude.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Gebäude");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        panGebaeude.add(jLabel1, gridBagConstraints);

        lblNutzungsart.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblNutzungsart.setText("Nutzungsart");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panGebaeude.add(lblNutzungsart, gridBagConstraints);

        lblAnschriftGebaeude.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblAnschriftGebaeude.setText("Anschrift Gebäude");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panGebaeude.add(lblAnschriftGebaeude, gridBagConstraints);

        lblAnschriftEigentuemer.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblAnschriftEigentuemer.setText("Anschrift Eigentümer");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panGebaeude.add(lblAnschriftEigentuemer, gridBagConstraints);

        lblAnzahlWohneinheiten.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblAnzahlWohneinheiten.setText("Anzahl Wohneinheiten");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panGebaeude.add(lblAnzahlWohneinheiten, gridBagConstraints);

        txtEigentuemerName.setBorder(null);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.eigentuemer_name}"),
                txtEigentuemerName,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panGebaeude.add(txtEigentuemerName, gridBagConstraints);

        txtAnzahlWohneinheiten.setBorder(null);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.anzahl_wohneinheiten}"),
                txtAnzahlWohneinheiten,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "anzahl_wohneinheiten");
        binding.setValidator(new AnzahlWohneinheitenValidator());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panGebaeude.add(txtAnzahlWohneinheiten, gridBagConstraints);

        cbNutzungsart.setMaximumRowCount(10);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.art}"),
                cbNutzungsart,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"),
                "art");
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panGebaeude.add(cbNutzungsart, gridBagConstraints);

        jScrollPane2.setBorder(null);
        jScrollPane2.setMinimumSize(new java.awt.Dimension(1, 1));
        jScrollPane2.setPreferredSize(new java.awt.Dimension(1, 1));

        jTextArea1.setColumns(20);
        jTextArea1.setRows(2);
        jTextArea1.setMinimumSize(new java.awt.Dimension(1, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gebaeude_anschrift}"),
                jTextArea1,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane2.setViewportView(jTextArea1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panGebaeude.add(jScrollPane2, gridBagConstraints);

        jScrollPane3.setBorder(null);
        jScrollPane3.setPreferredSize(new java.awt.Dimension(1, 1));

        jTextArea2.setColumns(20);
        jTextArea2.setRows(2);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.eigentuemer_anschrift}"),
                jTextArea2,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane3.setViewportView(jTextArea2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panGebaeude.add(jScrollPane3, gridBagConstraints);

        lblNameEigentuemer.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblNameEigentuemer.setText("Eigentümer");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panGebaeude.add(lblNameEigentuemer, gridBagConstraints);

        lblGeom.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblGeom.setText("Geometrie");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panGebaeude.add(lblGeom, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.geometrie}"),
                cboGeom,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"),
                "geometrie");
        binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cboGeom).getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panGebaeude.add(cboGeom, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(panGebaeude, gridBagConstraints);

        panVorgangsauswahl.setLayout(new java.awt.GridBagLayout());

        jPanel1.setOpaque(false);

        cmdAddVorgang.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        cmdAddVorgang.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdAddVorgangActionPerformed(evt);
                }
            });
        jPanel1.add(cmdAddVorgang);

        cmdRemoveVorgang.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        cmdRemoveVorgang.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdRemoveVorgangActionPerformed(evt);
                }
            });
        jPanel1.add(cmdRemoveVorgang);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panVorgangsauswahl.add(jPanel1, gridBagConstraints);

        lblVorgangsliste.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblVorgangsliste.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblVorgangsliste.setText("Vorgänge");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 0, 5);
        panVorgangsauswahl.add(lblVorgangsliste, gridBagConstraints);

        jScrollPane1.setBorder(null);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(150, 140));

        lstVorgaenge.setModel(new javax.swing.AbstractListModel() {

                String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };

                @Override
                public int getSize() {
                    return strings.length;
                }
                @Override
                public Object getElementAt(final int i) {
                    return strings[i];
                }
            });
        lstVorgaenge.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstVorgaenge.setMaximumSize(new java.awt.Dimension(39000, 850000));
        lstVorgaenge.setMinimumSize(new java.awt.Dimension(1, 1));
        lstVorgaenge.setPreferredSize(new java.awt.Dimension(1, 1));

        final org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create(
                "${cidsBean.vorgaenge}");
        final org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings
                    .createJListBinding(
                        org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                        this,
                        eLProperty,
                        lstVorgaenge,
                        "vorgaenge");
        bindingGroup.addBinding(jListBinding);

        lstVorgaenge.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

                @Override
                public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
                    lstVorgaengeValueChanged(evt);
                }
            });
        jScrollPane1.setViewportView(lstVorgaenge);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 5, 5);
        panVorgangsauswahl.add(jScrollPane1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(panVorgangsauswahl, gridBagConstraints);

        panVorgang.setOpaque(false);
        panVorgang.setLayout(new java.awt.BorderLayout());

        panVorgangX.setLayout(new java.awt.GridBagLayout());

        lblVergabeNr.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblVergabeNr.setText("Vergabenr.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(lblVergabeNr, gridBagConstraints);

        txtVergabeNr.setBorder(null);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                lstVorgaenge,
                org.jdesktop.beansbinding.ELProperty.create("${selectedElement.vergabenummer}"),
                txtVergabeNr,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(txtVergabeNr, gridBagConstraints);

        jScrollPane4.setBorder(null);
        jScrollPane4.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane4.setOpaque(false);

        txtBemerkungen.setColumns(15);
        txtBemerkungen.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtBemerkungen.setLineWrap(true);
        txtBemerkungen.setRows(4);
        txtBemerkungen.setBorder(null);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                lstVorgaenge,
                org.jdesktop.beansbinding.ELProperty.create("${selectedElement.bemerkungen}"),
                txtBemerkungen,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane4.setViewportView(txtBemerkungen);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(jScrollPane4, gridBagConstraints);
        jScrollPane3.getViewport().setOpaque(false);

        final javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.weighty = 0.1;
        panVorgangX.add(jPanel6, gridBagConstraints);

        lblSachbearbeiterVerw.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblSachbearbeiterVerw.setText("Sachbearbeiter Verwaltung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(lblSachbearbeiterVerw, gridBagConstraints);

        lblSachbearbeiterTech.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblSachbearbeiterTech.setText("Sachbearbeiter Technik");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(lblSachbearbeiterTech, gridBagConstraints);

        lblBemerkungen.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblBemerkungen.setText("Bemerkungen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(lblBemerkungen, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setText("Vorgang");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        panVorgangX.add(jLabel6, gridBagConstraints);

        txtSachbearbeiterTechnik.setBorder(null);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                lstVorgaenge,
                org.jdesktop.beansbinding.ELProperty.create("${selectedElement.sachbearbeiter_technik}"),
                txtSachbearbeiterTechnik,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(txtSachbearbeiterTechnik, gridBagConstraints);

        txtSachbearbeiterVerwaltung.setBorder(null);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                lstVorgaenge,
                org.jdesktop.beansbinding.ELProperty.create("${selectedElement.sachbearbeiter_verwaltung}"),
                txtSachbearbeiterVerwaltung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(txtSachbearbeiterVerwaltung, gridBagConstraints);

        lblUebergabedatum.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblUebergabedatum.setText("Übergabedatum");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(lblUebergabedatum, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                lstVorgaenge,
                org.jdesktop.beansbinding.ELProperty.create("${selectedElement.uebergabedatum}"),
                jXDatePicker1,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(sqlDateToUtilDateConverter);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(jXDatePicker1, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Maßnahme");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        panVorgangX.add(jLabel2, gridBagConstraints);

        lblMassnahmetyp.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblMassnahmetyp.setText("Maßnahmenkategorisierung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(lblMassnahmetyp, gridBagConstraints);

        lblBetroffeneWohneinheiten.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblBetroffeneWohneinheiten.setText("Betroffene Wohneinheiten");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(lblBetroffeneWohneinheiten, gridBagConstraints);

        lblBeschreibung1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblBeschreibung1.setText("Maßnahmenbeschreibung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(lblBeschreibung1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                lstVorgaenge,
                org.jdesktop.beansbinding.ELProperty.create("${selectedElement.massnahmenkategorisierung}"),
                cbMassnahmenkategorisierung,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"),
                "massnahmenkategorisierung");
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        binding.setValidator(new MassnahmenkategorisierungValidator());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(cbMassnahmenkategorisierung, gridBagConstraints);

        txtBetroffeneWohneinheiten.setBorder(null);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                lstVorgaenge,
                org.jdesktop.beansbinding.ELProperty.create("${selectedElement.betroffene_wohneinheiten}"),
                txtBetroffeneWohneinheiten,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(txtBetroffeneWohneinheiten, gridBagConstraints);

        jScrollPane6.setBorder(null);
        jScrollPane6.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane6.setOpaque(false);

        txtMassnahmenbeschreibung.setColumns(15);
        txtMassnahmenbeschreibung.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtMassnahmenbeschreibung.setLineWrap(true);
        txtMassnahmenbeschreibung.setRows(4);
        txtMassnahmenbeschreibung.setBorder(null);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                lstVorgaenge,
                org.jdesktop.beansbinding.ELProperty.create("${selectedElement.massnahmenbeschreibung}"),
                txtMassnahmenbeschreibung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane6.setViewportView(txtMassnahmenbeschreibung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(jScrollPane6, gridBagConstraints);
        jScrollPane3.getViewport().setOpaque(false);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("Kredit");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        panVorgangX.add(jLabel3, gridBagConstraints);

        lblBindungsdauer.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblBindungsdauer.setText("Bindung bis");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(lblBindungsdauer, gridBagConstraints);

        lblHoeheMietpreisbindung.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblHoeheMietpreisbindung.setText("Höhe Mietpreisbindung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(lblHoeheMietpreisbindung, gridBagConstraints);

        lblBewilligungsNr.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblBewilligungsNr.setText("Bewilligungsnr.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(lblBewilligungsNr, gridBagConstraints);

        lblBewilligungsdatum.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblBewilligungsdatum.setText("Bewilligungsdatum");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(lblBewilligungsdatum, gridBagConstraints);

        txtHoeheMietpreisbindung.setBorder(null);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                lstVorgaenge,
                org.jdesktop.beansbinding.ELProperty.create("${selectedElement.hoehe_mietpreisbindung}"),
                txtHoeheMietpreisbindung,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "hoehe_mietpreisbindung");
        binding.setValidator(new MietpreisbindungshoeheValidator());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(txtHoeheMietpreisbindung, gridBagConstraints);

        txtBewilligungsNr.setBorder(null);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                lstVorgaenge,
                org.jdesktop.beansbinding.ELProperty.create("${selectedElement.bewilligungsnummer}"),
                txtBewilligungsNr,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(txtBewilligungsNr, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                lstVorgaenge,
                org.jdesktop.beansbinding.ELProperty.create("${selectedElement.bewilligungsdatum}"),
                jXDatePicker2,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(sqlDateToUtilDateConverter);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(jXDatePicker2, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                lstVorgaenge,
                org.jdesktop.beansbinding.ELProperty.create("${selectedElement.bindungsdauer}"),
                jXDatePicker3,
                org.jdesktop.beansbinding.BeanProperty.create("date"),
                "bindungsdauer");
        binding.setConverter(sqlDateToUtilDateConverter);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(jXDatePicker3, gridBagConstraints);

        lblFolgenummer.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFolgenummer.setText("Folgenr.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(lblFolgenummer, gridBagConstraints);

        txtFolgenummer.setBorder(null);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                lstVorgaenge,
                org.jdesktop.beansbinding.ELProperty.create("${selectedElement.folgenummer}"),
                txtFolgenummer,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtFolgenummer.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    txtFolgenummerActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(txtFolgenummer, gridBagConstraints);

        lblEinkommensgruppe.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblEinkommensgruppe.setText("Einkommensgruppe");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(lblEinkommensgruppe, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                lstVorgaenge,
                org.jdesktop.beansbinding.ELProperty.create("${selectedElement.einkommensgruppe}"),
                cbEinkommensgruppe,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(cbEinkommensgruppe, gridBagConstraints);

        panVorgang.add(panVorgangX, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 5, 5);
        add(panVorgang, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdAddVorgangActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdAddVorgangActionPerformed
        final MetaClass vorgangMC = ClassCacheMultiple.getMetaClass(domain, "WBF_VORGANG", getConnectionContext());
        if (vorgangMC != null) {
            final MetaObject mo = vorgangMC.getEmptyInstance(getConnectionContext());
            final CidsBean vorgangBean = mo.getBean();
            final PropertyChangeListener pcl = new PropertyChangeListener() {

                    @Override
                    public void propertyChange(final PropertyChangeEvent evt) {
                        lstVorgaenge.repaint();
                    }
                };
            strongReferencesToWeakListeners.add(pcl);
            vorgangBean.addPropertyChangeListener(WeakListeners.propertyChange(pcl, vorgangBean));

            ((ObservableList)cidsBean.getProperty("vorgaenge")).add(vorgangBean);
        } else {
            LOG.error("MetaClass von Vorgang war NULL");
        }
    } //GEN-LAST:event_cmdAddVorgangActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdRemoveVorgangActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdRemoveVorgangActionPerformed
        final int answer = JOptionPane.showConfirmDialog(
                StaticSwingTools.getParentFrame(this),
                "Soll dieser Vorgang wirklich gelöscht werden?",
                "Vorgang entfernen",
                JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            try {
                final CidsBean vorg = (CidsBean)lstVorgaenge.getSelectedValue();
                ((ObservableList)cidsBean.getProperty("vorgaenge")).remove(vorg);
                // ((CidsBean) lstVorgaenge.getSelectedValue()).delete();
            } catch (Exception e) {
                final ErrorInfo ei = new ErrorInfo(
                        "Fehler beim Entfernen",
                        "Beim Entfernen des Vorgangs ist ein Fehler aufgetreten",
                        null,
                        null,
                        e,
                        Level.SEVERE,
                        null);
                JXErrorPane.showDialog(this, ei);
            }
        }
    }                                                                                    //GEN-LAST:event_cmdRemoveVorgangActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstVorgaengeValueChanged(final javax.swing.event.ListSelectionEvent evt) { //GEN-FIRST:event_lstVorgaengeValueChanged
        if (!editable && (luiVorg != null)) {
            luiVorg.setDirty(true);
//            luiVorg.setLocked(false);
//            luiVorg.setLocked(true);
//            panVorgang.setSize(panVorgang.getSize().width+1,panVorgang.getSize().height);
//            panVorgang.repaint();
        }
    }                                                                                       //GEN-LAST:event_lstVorgaengeValueChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtFolgenummerActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_txtFolgenummerActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_txtFolgenummerActionPerformed
    /**
     * End of variables declaration.
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        domain = cidsBean.getMetaObject().getDomain();
        final ObservableList ol = (ObservableList)cidsBean.getProperty("vorgaenge");
        for (final Object o : ol) {
            final CidsBean vorgBean = (CidsBean)o;
            final PropertyChangeListener pcl = new PropertyChangeListener() {

                    @Override
                    public void propertyChange(final PropertyChangeEvent evt) {
                        lstVorgaenge.repaint();
                    }
                };
            strongReferencesToWeakListeners.add(pcl);
            vorgBean.addPropertyChangeListener(WeakListeners.propertyChange(pcl, vorgBean));
        }

        ClassCacheMultiple.addInstance(domain, getConnectionContext());
        try {
            final MetaClass massnahmenClass = ClassCacheMultiple.getMetaClass(
                    domain,
                    "wbf_massnahme",
                    getConnectionContext());
            final DefaultComboBoxModel result = DefaultBindableReferenceCombo.getModelByMetaClass(
                    massnahmenClass,
                    false,
                    getConnectionContext());
            result.addElement(null);
            cbMassnahmenkategorisierung.setModel(result);

//            ((DefaultBindableReferenceCombo)cbMassnahmenkategorisierung).setMetaClass(massnahmenClass);

        } catch (Exception e) {
            LOG.error("Fehler beim fuellen der MassnahmenComboBox", e);
        }
        super.setCidsBean(cidsBean);
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    public void dispose() {
        super.dispose();
        ((DefaultCismapGeometryComboBoxEditor)cboGeom).dispose();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  title  DOCUMENT ME!
     */
    @Override
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public BindingGroup getBindingGroup() {
        return bindingGroup;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * class NutzungsartValidator extends Validator<CidsBean> { @Override public Result validate(CidsBean value) { if
     * (value != null) { return null; } else { return new Result("code", "Nutzungsart soll nicht leer sein."); } } }.
     *
     * @version  $Revision$, $Date$
     */
    class AnzahlWohneinheitenValidator extends Validator<Integer> {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param   value  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        @Override
        public Result validate(final Integer value) {
            final String warn = "Die Anzahl der Wohneinheiten darf nur bei Mietobjekten ausgefüllt sein.";
            final CidsBean nutzungsart = (CidsBean)cidsBean.getProperty("art");
            if (nutzungsart != null) {
                final Boolean bool = (Boolean)nutzungsart.getProperty("wohneinheiten_gueltig");
                if ((bool != null) && bool) {
                    return null;
                }
            }
            return new Result("code", warn, Validator.Result.WARNING);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class MietpreisbindungsdauerValidator extends Validator<Integer> {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param   value  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        @Override
        public Result validate(final Integer value) {
            final CidsBean nutzungsart = (CidsBean)cidsBean.getProperty("art");
            if (nutzungsart != null) {
                try {
                    final Boolean bool = (Boolean)nutzungsart.getProperty("mietpreisbindung_gueltig");
                    if ((bool != null) && bool) {
                        return null;
                    }
                } catch (Exception e) {
                }
            }
            return new Result("code", "Nur bei Mietwohngebäude gültig", Validator.Result.WARNING);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class MietpreisbindungshoeheValidator extends Validator<Integer> {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param   value  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        @Override
        public Result validate(final Integer value) {
            final CidsBean nutzungsart = (CidsBean)cidsBean.getProperty("art");
            if (nutzungsart != null) {
                try {
                    final Boolean bool = (Boolean)nutzungsart.getProperty("mietpreisbindung_gueltig");
                    if ((bool != null) && bool) {
                        return null;
                    }
                } catch (Exception e) {
                }
            }
            return new Result(null, "Nur bei Mietwohngebäude gültig", Validator.Result.WARNING);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class MassnahmenkategorisierungValidator extends Validator<CidsBean> {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param   validationBean  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        @Override
        public Result validate(final CidsBean validationBean) {
            if (validationBean != null) {
                final String kuerzel = (String)validationBean.getProperty("kuerzel");
                final CidsBean nutzungsart = (CidsBean)cidsBean.getProperty("art");
                if (nutzungsart != null) {
                    String gueltigeMassnahmen = null;
                    gueltigeMassnahmen = (String)nutzungsart.getProperty("massnahmen_gueltig");
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("ist kuerzel:" + kuerzel + " in " + gueltigeMassnahmen + " ?");
                    }
                    if ((kuerzel != null) && (gueltigeMassnahmen != null) && gueltigeMassnahmen.contains(kuerzel)) {
                        return null;
                    }
                    return new Result(null, "keine gültige Maßnahme", Validator.Result.WARNING);
                }
                return new Result(null, "ohne Nutzungsart, keine Maßnahme", Validator.Result.WARNING);
            } else {
                return null;
            }
        }
    }
}

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
class MyLockableUI extends LockableUI {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  dirty  DOCUMENT ME!
     */
    @Override
    public void setDirty(final boolean dirty) {
        super.setDirty(dirty);
    }
}
