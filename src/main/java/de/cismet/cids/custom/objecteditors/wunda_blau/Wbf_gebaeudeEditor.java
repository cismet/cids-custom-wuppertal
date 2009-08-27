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
import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.editors.BindingGroupStore;
import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.tools.metaobjectrenderer.Titled;
import de.cismet.cids.utils.ClassCacheMultiple;
import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;
import de.cismet.tools.gui.RoundedPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
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


/**
 *
 * @author thorsten
 */
public class Wbf_gebaeudeEditor extends DefaultCustomObjectEditor implements Titled, BindingGroupStore {

    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private String domain = "";
    ImageIcon warn = new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/icons/warn.png"));
    private HashMap<String, Collection<String>> validationDependencies = new HashMap<String, Collection<String>>();
    private final DefaultListCellRenderer dlcr = new DefaultListCellRenderer();
    public static final Color COLOR_TXT_BACK = new Color(230, 230, 230);
    private boolean editable;
    private String title = "";
    MyLockableUI luiGeb, luiVorg;
    JXLayer lVorg;

    public Wbf_gebaeudeEditor() {
        this(true);
    }

    /** Creates new form Wbf_gebaeudeEditor */
    public Wbf_gebaeudeEditor(boolean editable) {
//        editable=true;
        this.editable = editable;
        initComponents();



        panVorgangX.setOpaque(false);
        panVorgangsauswahl.setOpaque(false);
        panGebaeude.setOpaque(false);

        ((RoundedPanel) panVorgangX).setAlpha(255);
        ((RoundedPanel) panGebaeude).setAlpha(255);
        ((RoundedPanel) panVorgangsauswahl).setAlpha(255);

        panVorgangX.setBackground(getBackground());
        panGebaeude.setBackground(getBackground());
        panVorgangsauswahl.setBackground(getBackground());

        if (!editable) {
            bindingGroup.removeBinding(bindingGroup.getBinding("geometrie"));
            GridBagConstraints cbgVorgang = ((GridBagLayout) getLayout()).getConstraints(panVorgang);
            lVorg = new JXLayer(panVorgang);
            luiVorg = new MyLockableUI();



            luiVorg.setLocked(!editable);
            lVorg.setUI(luiVorg);
            lVorg.setOpaque(false);
            add(lVorg, cbgVorgang);
            GridBagConstraints cbgGeb = ((GridBagLayout) getLayout()).getConstraints(panGebaeude);
            JXLayer lGeb = new JXLayer(panGebaeude);
            lGeb.setOpaque(false);
            luiGeb = new MyLockableUI();
            luiGeb.setLocked(!editable);
            lGeb.setUI(luiGeb);
            add(lGeb, cbgGeb);

            cmdAddVorgang.setVisible(false);
            cmdRemoveVorgang.setVisible(false);
        }


        Vector v = new Vector(Arrays.asList(panVorgangX.getComponents()));
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
                ((DefaultBindableReferenceCombo) inputField).setFakeModel(!editable);
                //((DefaultBindableReferenceCombo) inputField).setFakeModel(true);
            }
        }

        lblGeom.setVisible(editable);
        cboGeom.setVisible(editable);





        final MassnahmenkategorisierungValidator massnahmenkategorisierungValidator = new MassnahmenkategorisierungValidator();
        cbMassnahmenkategorisierung.setRenderer(new ListCellRenderer() {

            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel l = (JLabel) dlcr.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    Validator.Result result = massnahmenkategorisierungValidator.validate((CidsBean) value);
                    if (result != null) {
                        if (result.getType().equals(result.ERROR)) {
                            l.setForeground(Color.red);
                        } else {
                            l.setForeground(Color.MAGENTA);
                        }

                    } else {
                        l.setForeground(Color.black);
                    }
                }
                return l;
            }
        });


       cbNutzungsart.setNullValueRepresentation("Bitte Nutzungsart auswählen");

        validationDependencies.put("art", Arrays.asList("anzahl_wohneinheiten", "massnahmenkategorisierung", "hoehe_mietpreisbindung", "bindungsdauer"));
        bindingGroup.addBindingListener(new BindingListener() {

            public void bindingBecameBound(Binding binding) {
                if (binding.getName() != null && binding.getName().equalsIgnoreCase("VORGAENGE")) {
                    lstVorgaenge.setSelectedIndex(0);
                }
            }

            public void bindingBecameUnbound(Binding binding) {
            }

            public void syncFailed(Binding binding, SyncFailure failure) {
                if (Wbf_gebaeudeEditor.this.editable) {
                    log.debug("syncFailed");
                    Object target = binding.getTargetObject();
                    if (target instanceof JComponent) {//&& !(target instanceof JComboBox)) {
                        JComponent c = (JComponent) target;
                        c.setForeground(Color.red);

                        try {
                            c.setToolTipText(failure.getValidationResult().getDescription());
                        } catch (Exception skip) {
                        }

                    } else {
                        log.error("keine JCOmponent");
                    }
                }
            }

            public void syncWarning(Binding binding, SyncFailure failure) {
                if (Wbf_gebaeudeEditor.this.editable) {
                    log.debug("synWarning " + failure);
                    Object target = binding.getTargetObject();
                    if (target instanceof JComponent) {// && !(target instanceof JComboBox)) {
                        JComponent c = (JComponent) target;
                        c.setForeground(Color.magenta);
                        c.setToolTipText(failure.getValidationResult().getDescription());


                    } else {
                        log.error("keine JCOmponent");
                    }
                }
            }

            public void synced(Binding binding) {
                if (Wbf_gebaeudeEditor.this.editable) {
                    log.debug("sync");
                    Object target = binding.getTargetObject();
                    if (target instanceof JComponent) {//&& !(target instanceof JComboBox)) {
                        JComponent c = (JComponent) target;
                        c.setForeground(Color.black);
                        c.setToolTipText(null);

                    } else {
                        log.error("keine JCOmponent");
                    }


                    String bindingName = binding.getName();
                    if (bindingName != null) {
                        Collection<String> additionalValidationBindings = validationDependencies.get(binding.getName());
                        log.debug("for binding " + bindingName + "-->> dependencies: " + additionalValidationBindings);
                        if (additionalValidationBindings != null) {

                            for (String name : additionalValidationBindings) {
                                Binding b = bindingGroup.getBinding(name);
                                if (b != null) {
                                    log.debug("CheckAgain: " + name);
                                    b.saveAndNotify();
                                }
                            }
                        }

                    }
                }
            }

            public void sourceChanged(Binding binding, PropertyStateEvent event) {
            }

            public void targetChanged(Binding binding, PropertyStateEvent event) {
            }
        });

        lstVorgaenge.setCellRenderer(new ListCellRenderer() {

            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel l = (JLabel) dlcr.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null || value instanceof CidsBean && ((CidsBean) value).getProperty("vergabenummer") == null) {
                    l.setText("Neuer Vorgang");
                }
                return l;
            }
        });


        cbNutzungsart.setSelectedItem(null);
        Vector einkommensgruppen=new Vector();
        einkommensgruppen.add("A");
        einkommensgruppen.add("B");
        einkommensgruppen.add(null);

        cbEinkommensgruppe.setModel(new DefaultComboBoxModel(einkommensgruppen));
        cbEinkommensgruppe.setRenderer(new ListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel l = (JLabel) dlcr.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    l.setText(" ");
                }
                return l;
            }
        });

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
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

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel1.setText("Gebäude");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        panGebaeude.add(jLabel1, gridBagConstraints);

        lblNutzungsart.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblNutzungsart.setText("Nutzungsart");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panGebaeude.add(lblNutzungsart, gridBagConstraints);

        lblAnschriftGebaeude.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblAnschriftGebaeude.setText("Anschrift Gebäude");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panGebaeude.add(lblAnschriftGebaeude, gridBagConstraints);

        lblAnschriftEigentuemer.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblAnschriftEigentuemer.setText("Anschrift Eigentümer");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panGebaeude.add(lblAnschriftEigentuemer, gridBagConstraints);

        lblAnzahlWohneinheiten.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblAnzahlWohneinheiten.setText("Anzahl Wohneinheiten");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panGebaeude.add(lblAnzahlWohneinheiten, gridBagConstraints);

        txtEigentuemerName.setBorder(null);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.eigentuemer_name}"), txtEigentuemerName, org.jdesktop.beansbinding.BeanProperty.create("text"));
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

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.anzahl_wohneinheiten}"), txtAnzahlWohneinheiten, org.jdesktop.beansbinding.BeanProperty.create("text"), "anzahl_wohneinheiten");
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

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.art}"), cbNutzungsart, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"), "art");
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

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gebaeude_anschrift}"), jTextArea1, org.jdesktop.beansbinding.BeanProperty.create("text"));
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

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.eigentuemer_anschrift}"), jTextArea2, org.jdesktop.beansbinding.BeanProperty.create("text"));
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

        lblNameEigentuemer.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblNameEigentuemer.setText("Eigentümer");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panGebaeude.add(lblNameEigentuemer, gridBagConstraints);

        lblGeom.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblGeom.setText("Geometrie");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panGebaeude.add(lblGeom, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.geometrie}"), cboGeom, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"), "geometrie");
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

        cmdAddVorgang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        cmdAddVorgang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAddVorgangActionPerformed(evt);
            }
        });
        jPanel1.add(cmdAddVorgang);

        cmdRemoveVorgang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        cmdRemoveVorgang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
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

        lblVorgangsliste.setFont(new java.awt.Font("Tahoma", 1, 11));
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
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstVorgaenge.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstVorgaenge.setMaximumSize(new java.awt.Dimension(39000, 850000));
        lstVorgaenge.setMinimumSize(new java.awt.Dimension(1, 1));
        lstVorgaenge.setPreferredSize(new java.awt.Dimension(1, 1));

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${cidsBean.vorgaenge}");
        org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings.createJListBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, lstVorgaenge, "vorgaenge");
        bindingGroup.addBinding(jListBinding);

        lstVorgaenge.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
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

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, lstVorgaenge, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.vergabenummer}"), txtVergabeNr, org.jdesktop.beansbinding.BeanProperty.create("text"));
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
        txtBemerkungen.setFont(new java.awt.Font("Tahoma", 0, 11));
        txtBemerkungen.setLineWrap(true);
        txtBemerkungen.setRows(4);
        txtBemerkungen.setBorder(null);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, lstVorgaenge, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.bemerkungen}"), txtBemerkungen, org.jdesktop.beansbinding.BeanProperty.create("text"));
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

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.weighty = 0.1;
        panVorgangX.add(jPanel6, gridBagConstraints);

        lblSachbearbeiterVerw.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblSachbearbeiterVerw.setText("Sachbearbeiter Verwaltung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(lblSachbearbeiterVerw, gridBagConstraints);

        lblSachbearbeiterTech.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblSachbearbeiterTech.setText("Sachbearbeiter Technik");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(lblSachbearbeiterTech, gridBagConstraints);

        lblBemerkungen.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblBemerkungen.setText("Bemerkungen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(lblBemerkungen, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel6.setText("Vorgang");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        panVorgangX.add(jLabel6, gridBagConstraints);

        txtSachbearbeiterTechnik.setBorder(null);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, lstVorgaenge, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.sachbearbeiter_technik}"), txtSachbearbeiterTechnik, org.jdesktop.beansbinding.BeanProperty.create("text"));
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

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, lstVorgaenge, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.sachbearbeiter_verwaltung}"), txtSachbearbeiterVerwaltung, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(txtSachbearbeiterVerwaltung, gridBagConstraints);

        lblUebergabedatum.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblUebergabedatum.setText("Übergabedatum");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(lblUebergabedatum, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, lstVorgaenge, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.uebergabedatum}"), jXDatePicker1, org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(sqlDateToUtilDateConverter);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(jXDatePicker1, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel2.setText("Maßnahme");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        panVorgangX.add(jLabel2, gridBagConstraints);

        lblMassnahmetyp.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblMassnahmetyp.setText("Maßnahmenkategorisierung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(lblMassnahmetyp, gridBagConstraints);

        lblBetroffeneWohneinheiten.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblBetroffeneWohneinheiten.setText("Betroffene Wohneinheiten");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(lblBetroffeneWohneinheiten, gridBagConstraints);

        lblBeschreibung1.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblBeschreibung1.setText("Maßnahmenbeschreibung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(lblBeschreibung1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, lstVorgaenge, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.massnahmenkategorisierung}"), cbMassnahmenkategorisierung, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"), "massnahmenkategorisierung");
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

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, lstVorgaenge, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.betroffene_wohneinheiten}"), txtBetroffeneWohneinheiten, org.jdesktop.beansbinding.BeanProperty.create("text"));
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
        txtMassnahmenbeschreibung.setFont(new java.awt.Font("Tahoma", 0, 11));
        txtMassnahmenbeschreibung.setLineWrap(true);
        txtMassnahmenbeschreibung.setRows(4);
        txtMassnahmenbeschreibung.setBorder(null);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, lstVorgaenge, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.massnahmenbeschreibung}"), txtMassnahmenbeschreibung, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane6.setViewportView(txtMassnahmenbeschreibung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(jScrollPane6, gridBagConstraints);
        jScrollPane3.getViewport().setOpaque(false);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel3.setText("Kredit");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        panVorgangX.add(jLabel3, gridBagConstraints);

        lblBindungsdauer.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblBindungsdauer.setText("Bindung bis");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(lblBindungsdauer, gridBagConstraints);

        lblHoeheMietpreisbindung.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblHoeheMietpreisbindung.setText("Höhe Mietpreisbindung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(lblHoeheMietpreisbindung, gridBagConstraints);

        lblBewilligungsNr.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblBewilligungsNr.setText("Bewilligungsnr.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(lblBewilligungsNr, gridBagConstraints);

        lblBewilligungsdatum.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblBewilligungsdatum.setText("Bewilligungsdatum");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(lblBewilligungsdatum, gridBagConstraints);

        txtHoeheMietpreisbindung.setBorder(null);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, lstVorgaenge, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.hoehe_mietpreisbindung}"), txtHoeheMietpreisbindung, org.jdesktop.beansbinding.BeanProperty.create("text"), "hoehe_mietpreisbindung");
        binding.setValidator(new MietpreisbindungshoeheValidator());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(txtHoeheMietpreisbindung, gridBagConstraints);

        txtBewilligungsNr.setBorder(null);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, lstVorgaenge, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.bewilligungsnummer}"), txtBewilligungsNr, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(txtBewilligungsNr, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, lstVorgaenge, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.bewilligungsdatum}"), jXDatePicker2, org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(sqlDateToUtilDateConverter);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panVorgangX.add(jXDatePicker2, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, lstVorgaenge, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.bindungsdauer}"), jXDatePicker3, org.jdesktop.beansbinding.BeanProperty.create("date"), "bindungsdauer");
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

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, lstVorgaenge, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.folgenummer}"), txtFolgenummer, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtFolgenummer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
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

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, lstVorgaenge, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.einkommensgruppe}"), cbEinkommensgruppe, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
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
    }// </editor-fold>//GEN-END:initComponents

    private void cmdAddVorgangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAddVorgangActionPerformed
        MetaClass vorgangMC = ClassCacheMultiple.getMetaClass(domain, "WBF_VORGANG");
        if (vorgangMC != null) {
            MetaObject mo = vorgangMC.getEmptyInstance();
            CidsBean vorgangBean = mo.getBean();
            vorgangBean.addPropertyChangeListener(new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    lstVorgaenge.repaint();
                }
            });

            ((ObservableList) cidsBean.getProperty("vorgaenge")).add(vorgangBean);
        } else {
            log.error("MetaClass von Vorgang war NULL");
        }

    }//GEN-LAST:event_cmdAddVorgangActionPerformed

    private void cmdRemoveVorgangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdRemoveVorgangActionPerformed
        int answer = JOptionPane.showConfirmDialog(this, "Soll dieser Vorgang wirklich gelöscht werden?", "Vorgang entfernen", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            try {
                CidsBean vorg = (CidsBean) lstVorgaenge.getSelectedValue();
                ((ObservableList) cidsBean.getProperty("vorgaenge")).remove(vorg);
                //((CidsBean) lstVorgaenge.getSelectedValue()).delete();
            } catch (Exception e) {
                ErrorInfo ei = new ErrorInfo("Fehler beim Entfernen", "Beim Entfernen des Vorgangs ist ein Fehler aufgetreten", null,
                        null, e, Level.SEVERE, null);
                JXErrorPane.showDialog(this, ei);
            }
        }
    }//GEN-LAST:event_cmdRemoveVorgangActionPerformed

    private void lstVorgaengeValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstVorgaengeValueChanged
        if (!editable && luiVorg != null) {
            luiVorg.setDirty(true);
//            luiVorg.setLocked(false);
//            luiVorg.setLocked(true);
//            panVorgang.setSize(panVorgang.getSize().width+1,panVorgang.getSize().height);
//            panVorgang.repaint();
        }
    }//GEN-LAST:event_lstVorgaengeValueChanged

    private void txtFolgenummerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFolgenummerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFolgenummerActionPerformed

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

    // End of variables declaration
    @Override
    public void setCidsBean(CidsBean cidsBean) {
        domain = cidsBean.getMetaObject().getDomain();
        ObservableList ol = (ObservableList) cidsBean.getProperty("vorgaenge");
        for (Object o : ol) {
            CidsBean vorgBean = (CidsBean) o;
            vorgBean.addPropertyChangeListener(new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    lstVorgaenge.repaint();
                }
            });
        }

        ClassCacheMultiple.addInstance(domain);
        try {

            final MetaClass massnahmenClass = ClassCacheMultiple.getMetaClass(domain, "wbf_massnahme");
              DefaultComboBoxModel result = DefaultBindableReferenceCombo.getModelByMetaClass(massnahmenClass, true);
            cbMassnahmenkategorisierung.setModel(result);


//            ((DefaultBindableReferenceCombo)cbMassnahmenkategorisierung).setMetaClass(massnahmenClass);
            
        } catch (Exception e) {
            log.error("Fehler beim fuellen der MassnahmenComboBox", e);
        }
        super.setCidsBean(cidsBean);
    }

//    class NutzungsartValidator extends Validator<CidsBean> {
//
//        @Override
//        public Result validate(CidsBean value) {
//            if (value != null) {
//                return null;
//            } else {
//                return new Result("code", "Nutzungsart soll nicht leer sein.");
//            }
//        }
//    }
    class AnzahlWohneinheitenValidator extends Validator<Integer> {

        @Override
        public Result validate(Integer value) {
            String warn = "Die Anzahl der Wohneinheiten darf nur bei Mietobjekten ausgefüllt sein.";
            CidsBean nutzungsart = (CidsBean) cidsBean.getProperty("art");
            if (nutzungsart != null) {
                Boolean bool = (Boolean) nutzungsart.getProperty("wohneinheiten_gueltig");
                if (bool != null && bool) {
                    return null;
                }
            }
            return new Result("code", warn, Validator.Result.WARNING);
        }
    }

    class MietpreisbindungsdauerValidator extends Validator<Integer> {

        @Override
        public Result validate(Integer value) {
            CidsBean nutzungsart = (CidsBean) cidsBean.getProperty("art");
            if (nutzungsart != null) {
                try {
                    Boolean bool = (Boolean) nutzungsart.getProperty("mietpreisbindung_gueltig");
                    if (bool != null && bool) {
                        return null;
                    }
                } catch (Exception e) {
                }
            }
            return new Result("code", "Nur bei Mietwohngebäude gültig", Validator.Result.WARNING);
        }
    }

    class MietpreisbindungshoeheValidator extends Validator<Integer> {

        @Override
        public Result validate(Integer value) {
            CidsBean nutzungsart = (CidsBean) cidsBean.getProperty("art");
            if (nutzungsart != null) {
                try {
                    Boolean bool = (Boolean) nutzungsart.getProperty("mietpreisbindung_gueltig");
                    if (bool != null && bool) {
                        return null;
                    }
                } catch (Exception e) {
                }
            }
            return new Result(null, "Nur bei Mietwohngebäude gültig", Validator.Result.WARNING);
        }
    }

    class MassnahmenkategorisierungValidator extends Validator<CidsBean> {

        @Override
        public Result validate(CidsBean validationBean) {
            String kuerzel = (String) validationBean.getProperty("kuerzel");
            CidsBean nutzungsart = (CidsBean) cidsBean.getProperty("art");
            if (nutzungsart != null) {
                String gueltigeMassnahmen = null;
                gueltigeMassnahmen = (String) nutzungsart.getProperty("massnahmen_gueltig");
                log.debug("ist kuerzel:" + kuerzel + " in " + gueltigeMassnahmen + " ?");
                if (kuerzel != null && gueltigeMassnahmen != null && gueltigeMassnahmen.contains(kuerzel)) {
                    return null;
                }
                return new Result(null, "keine gültige Maßnahme", Validator.Result.WARNING);
            }
            return new Result(null, "ohne Nutzungsart, keine Maßnahme", Validator.Result.WARNING);
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BindingGroup getBindingGroup() {
        return bindingGroup;
    }
}
class MyLockableUI extends LockableUI {

    public void setDirty(boolean dirty) {
        super.setDirty(dirty);
    }
}


