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
import Sirius.navigator.ui.ComponentRegistry;
import Sirius.navigator.ui.RequestsFullSizeComponent;

import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import java.awt.Component;
import java.awt.event.MouseEvent;

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EventObject;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

import de.cismet.cids.custom.clientutils.CidsBeansTableModel;
import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objecteditors.wunda_blau.albo.ComboBoxFilterDialog;
import de.cismet.cids.custom.reports.wunda_blau.AlboReportGenerator;
import de.cismet.cids.custom.wunda_blau.search.actions.AlboVorgangExtReportServerAction;
import de.cismet.cids.custom.wunda_blau.search.actions.AlboVorgangReportServerAction;
import de.cismet.cids.custom.wunda_blau.search.server.AlboFlaecheLightweightSearch;
import de.cismet.cids.custom.wunda_blau.search.server.AlboVorgangNextSchluesselServerSearch;
import de.cismet.cids.custom.wunda_blau.search.server.BplaeneMonSearch;
import de.cismet.cids.custom.wunda_blau.search.server.StrAdrStrasseLightweightSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultBindableComboboxCellEditor;
import de.cismet.cids.editors.DefaultBindableScrollableComboBox;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.navigator.utils.CidsBeanDropListener;
import de.cismet.cids.navigator.utils.CidsBeanDropTarget;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.connectioncontext.AbstractConnectionContext;
import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.gui.TitleComponentProvider;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class AlboVorgangEditor extends javax.swing.JPanel implements CidsBeanRenderer,
    ConnectionContextStore,
    TitleComponentProvider,
    EditorSaveListener,
    RequestsFullSizeComponent {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(AlboVorgangEditor.class);
    public static final String STRASSENNAME_TOSTRING_TEMPLATE = "%s";
    public static final String[] STRASSENNAME_TOSTRING_FIELDS = {
            StrAdrStrasseLightweightSearch.Subject.NAME.toString()
        };
    public static final String STRASSENSCHLUESSEL_TOSTRING_TEMPLATE = "%s";
    public static final String[] STRASSENSCHLUESSEL_TOSTRING_FIELDS = {
            StrAdrStrasseLightweightSearch.Subject.SCHLUESSEL.toString()
        };

    private static final String[] FLAECHE_COLUMN_PROPERTIES = new String[] {
            "erhebungsnummer",
            "fk_strasse.name",
            "hausnummer",
            "ortsuebliche_bezeichnung",
            "jahr_von",
            "jahr_bis",
            "fk_art.name",
            "fk_status.name",
            "fk_zuordnung.name"
        };
    private static final String[] FLAECHE_COLUMN_NAMES = new String[] {
            "Erhebungs-Nr.",
            "Straße",
            "Haus-Nr.",
            "Ortsübl. Bezeichnung",
            "Jahr von",
            "Jahr bis",
            "Flächenart",
            "Flächenstatus",
            "Flächenzuordnung"
        };
    private static final Class[] FLAECHE_COLUMN_CLASSES = new Class[] {
            String.class,  // Erhebungsnummer
            String.class,  // Straße
            String.class,  // Hausnummer
            String.class,  // ortsübliche
            Integer.class, // jahr von
            Integer.class, // jahr bis
            String.class,  // art
            String.class,  // status
            String.class   // zuordnung
        };

    private static final String[] BEARBEITUNG_COLUMN_PROPERTIES = new String[] {
            "stand",
            "fk_bearbeiter",
            "fk_geschaeft"
        };
    private static final String[] BEARBEITUNG_COLUMN_NAMES = new String[] {
            "Stand",
            "Bearbeiter",
            "Geschäft"
        };
    private static final Class[] BEARBEITUNG_COLUMN_CLASSES = new Class[] {
            Date.class,
            CidsBean.class,
            CidsBean.class
        };

    //~ Instance fields --------------------------------------------------------

    private final boolean editable;
    private CidsBean cidsBean;

    private ConnectionContext connectionContext = ConnectionContext.createDummy();
    private final StrAdrStrasseLightweightSearch strassennameSearch = new StrAdrStrasseLightweightSearch(
            StrAdrStrasseLightweightSearch.Subject.NAME,
            STRASSENNAME_TOSTRING_TEMPLATE,
            STRASSENNAME_TOSTRING_FIELDS);

    private TableCellEditor ceBearbeiter;
    private TableCellEditor ceGeschaeft;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JButton btnReport;
    javax.swing.JButton btnReport1;
    private javax.swing.ButtonGroup buttonGroup1;
    private de.cismet.cids.custom.objecteditors.wunda_blau.albo.ComboBoxFilterDialog comboBoxFilterDialog1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox102;
    private javax.swing.JCheckBox jCheckBox103;
    private javax.swing.JComboBox<String> jComboBox28;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JList<CidsBean> jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField5;
    private org.jdesktop.swingx.JXTable jXTable1;
    private org.jdesktop.swingx.JXTable jXTable2;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel panFooter;
    private javax.swing.JPanel panTitle;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AlboVorgangEditor object.
     */
    public AlboVorgangEditor() {
        this(true);
    }

    /**
     * Creates a new AlboVorgangEditor object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public AlboVorgangEditor(final boolean editable) {
        this.editable = editable;
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

        panTitle = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        btnReport = new javax.swing.JButton();
        btnReport1 = new javax.swing.JButton();
        panFooter = new javax.swing.JPanel();
        comboBoxFilterDialog1 = AlboVorgangFlaecheFilterDialog.getInstance();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jCheckBox102 = new javax.swing.JCheckBox();
        jCheckBox103 = new javax.swing.JCheckBox();
        jCheckBox1 = new javax.swing.JCheckBox();
        jPanel6 = new javax.swing.JPanel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jLabel4 = new javax.swing.JLabel();
        jComboBox4 = new de.cismet.cids.editors.FastBindableScrollableComboBox(
                strassennameSearch,
                strassennameSearch.getRepresentationPattern(),
                strassennameSearch.getRepresentationFields());
        jLabel17 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jPanel13 = new javax.swing.JPanel();
        jRadioButton2 = new javax.swing.JRadioButton();
        jLabel6 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jRadioButton3 = new javax.swing.JRadioButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jPanel7 = new javax.swing.JPanel();
        jLabel61 = new javax.swing.JLabel();
        jComboBox28 = new DefaultBindableScrollableComboBox();
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jPanel8 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(300, 0),
                new java.awt.Dimension(300, 0),
                new java.awt.Dimension(300, 32767));
        jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jXTable1 = new DroppedBeansTable();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(200, 0),
                new java.awt.Dimension(200, 0),
                new java.awt.Dimension(200, 32767));
        jPanel9 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jPanel10 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jXTable2 = new BearbeitungTable();
        jPanel12 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));

        panTitle.setName("panTitle"); // NOI18N
        panTitle.setOpaque(false);
        panTitle.setLayout(new java.awt.GridBagLayout());

        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setName("lblTitle"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create(
                    "<html><body><h2><nobr>Altlastenkataster - Vorgang: ${cidsBean.schluessel}"),
                lblTitle,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("<html><body><h2><nobr>Altlastenkataster - Vorgang: -");
        binding.setSourceUnreadableValue("<html><body><h2><nobr>Altlastenkataster - Vorgang: <i>[Fehler]");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panTitle.add(lblTitle, gridBagConstraints);

        btnReport.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/printer1.png")));                                   // NOI18N
        btnReport.setText(org.openide.util.NbBundle.getMessage(AlboVorgangEditor.class, "TreppeEditor.btnReport.text")); // NOI18N
        btnReport.setToolTipText(org.openide.util.NbBundle.getMessage(
                AlboVorgangEditor.class,
                "AlboVorgangEditor.btnReport.toolTipText"));                                                             // NOI18N
        btnReport.setBorderPainted(false);
        btnReport.setContentAreaFilled(false);
        btnReport.setFocusPainted(false);
        btnReport.setName("btnReport");                                                                                  // NOI18N
        btnReport.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnReportActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        panTitle.add(btnReport, gridBagConstraints);

        btnReport1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/printer2.png"))); // NOI18N
        btnReport1.setText(org.openide.util.NbBundle.getMessage(
                AlboVorgangEditor.class,
                "TreppeEditor.btnReport.text"));                                       // NOI18N
        btnReport1.setToolTipText(org.openide.util.NbBundle.getMessage(
                AlboVorgangEditor.class,
                "AlboVorgangEditor.btnReport2.toolTipText"));                          // NOI18N
        btnReport1.setBorderPainted(false);
        btnReport1.setContentAreaFilled(false);
        btnReport1.setFocusPainted(false);
        btnReport1.setName("btnReport1");                                              // NOI18N
        btnReport1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnReport1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        panTitle.add(btnReport1, gridBagConstraints);

        panFooter.setName("panFooter"); // NOI18N
        panFooter.setOpaque(false);
        panFooter.setLayout(new java.awt.GridBagLayout());

        comboBoxFilterDialog1.setName("comboBoxFilterDialog1"); // NOI18N

        setName("Form"); // NOI18N
        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel11.setName("jPanel11"); // NOI18N
        jPanel11.setOpaque(false);
        jPanel11.setLayout(new java.awt.GridBagLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Beschreibung:"));
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Schlüssel:"); // NOI18N
        jLabel1.setName("jLabel1");    // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jLabel1, gridBagConstraints);

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jTextField1.setName("jTextField1"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.schluessel}"),
                jTextField1,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(jTextField1, gridBagConstraints);

        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setOpaque(false);
        jPanel4.setLayout(new java.awt.GridBagLayout());

        jCheckBox102.setText("Gutachten");    // NOI18N
        jCheckBox102.setContentAreaFilled(false);
        jCheckBox102.setName("jCheckBox102"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gutachten}"),
                jCheckBox102,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel4.add(jCheckBox102, gridBagConstraints);

        jCheckBox103.setText("geprüft");      // NOI18N
        jCheckBox103.setContentAreaFilled(false);
        jCheckBox103.setName("jCheckBox103"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.geprueft}"),
                jCheckBox103,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel4.add(jCheckBox103, gridBagConstraints);

        jCheckBox1.setText("unterdrücken");
        jCheckBox1.setContentAreaFilled(false);
        jCheckBox1.setName("jCheckBox1"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.loeschen}"),
                jCheckBox1,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel4.add(jCheckBox1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(jPanel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel2.add(jPanel3, gridBagConstraints);

        jPanel6.setName("jPanel6"); // NOI18N
        jPanel6.setOpaque(false);
        jPanel6.setLayout(new java.awt.GridBagLayout());

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setContentAreaFilled(false);
        jRadioButton1.setName("jRadioButton1"); // NOI18N
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jRadioButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel6.add(jRadioButton1, gridBagConstraints);

        jLabel4.setText("Straße:");
        jLabel4.setName("jLabel4"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        jPanel6.add(jLabel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jPanel6, gridBagConstraints);

        jComboBox4.setName("jComboBox4"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_strasse}"),
                jComboBox4,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jComboBox4, gridBagConstraints);

        jLabel17.setText("Haus-Nr:"); // NOI18N
        jLabel17.setName("jLabel17"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 2);
        jPanel2.add(jLabel17, gridBagConstraints);

        jTextField5.setName("jTextField5"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.hausnummer}"),
                jTextField5,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 50;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jTextField5, gridBagConstraints);

        jPanel13.setName("jPanel13"); // NOI18N
        jPanel13.setOpaque(false);
        jPanel13.setLayout(new java.awt.GridBagLayout());

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setContentAreaFilled(false);
        jRadioButton2.setName("jRadioButton2"); // NOI18N
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jRadioButton2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel13.add(jRadioButton2, gridBagConstraints);

        jLabel6.setText("Lage aus BPlan ersichtlich");
        jLabel6.setName("jLabel6"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        jPanel13.add(jLabel6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jPanel13, gridBagConstraints);

        jPanel14.setName("jPanel14"); // NOI18N
        jPanel14.setOpaque(false);
        jPanel14.setLayout(new java.awt.GridBagLayout());

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setContentAreaFilled(false);
        jRadioButton3.setName("jRadioButton3"); // NOI18N
        jRadioButton3.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jRadioButton3ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel14.add(jRadioButton3, gridBagConstraints);

        jLabel7.setText("Sonstige");
        jLabel7.setName("jLabel7"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        jPanel14.add(jLabel7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jPanel14, gridBagConstraints);

        jLabel5.setText("Bemerkung:"); // NOI18N
        jLabel5.setName("jLabel5");    // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jLabel5, gridBagConstraints);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setName("jTextArea1"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bemerkung}"),
                jTextArea1,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(jTextArea1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jScrollPane1, gridBagConstraints);

        filler3.setName("filler3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(filler3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        jPanel11.add(jPanel2, gridBagConstraints);

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Ordner:"));
        jPanel7.setName("jPanel7"); // NOI18N
        jPanel7.setOpaque(false);
        jPanel7.setLayout(new java.awt.GridBagLayout());

        jLabel61.setText("Art:");     // NOI18N
        jLabel61.setName("jLabel61"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel7.add(jLabel61, gridBagConstraints);

        jComboBox28.setName("jComboBox28"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_art}"),
                jComboBox28,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel7.add(jComboBox28, gridBagConstraints);
        ((DefaultBindableScrollableComboBox)jComboBox28).setNullable(true);

        jLabel2.setText("Regal-Nr.:"); // NOI18N
        jLabel2.setName("jLabel2");    // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel7.add(jLabel2, gridBagConstraints);

        jTextField2.setName("jTextField2"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.regal_nummer}"),
                jTextField2,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel7.add(jTextField2, gridBagConstraints);

        jLabel3.setText("Nummer:"); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel7.add(jLabel3, gridBagConstraints);

        jTextField3.setName("jTextField3"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.ordner_nummer}"),
                jTextField3,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel7.add(jTextField3, gridBagConstraints);

        filler2.setName("filler2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel7.add(filler2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel11.add(jPanel7, gridBagConstraints);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("B-Pläne"));
        jPanel8.setName("jPanel8"); // NOI18N
        jPanel8.setOpaque(false);
        jPanel8.setLayout(new java.awt.GridBagLayout());

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jList1.setName("jList1"); // NOI18N
        jList1.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    jList1MouseClicked(evt);
                }
            });
        jScrollPane2.setViewportView(jList1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel8.add(jScrollPane2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        jPanel11.add(jPanel8, gridBagConstraints);

        filler5.setName("filler5"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        jPanel11.add(filler5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jPanel11, gridBagConstraints);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Flächen:"));
        jPanel5.setName("jPanel5"); // NOI18N
        jPanel5.setOpaque(false);
        jPanel5.setLayout(new java.awt.GridBagLayout());

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        jXTable1.setModel(new VorgangFlaecheTableModel());
        jXTable1.setName("jXTable1"); // NOI18N
        jXTable1.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    jXTable1MouseClicked(evt);
                }
            });
        jScrollPane3.setViewportView(jXTable1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel5.add(jScrollPane3, gridBagConstraints);

        filler4.setName("filler4"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        jPanel5.add(filler4, gridBagConstraints);

        jPanel9.setName("jPanel9"); // NOI18N
        jPanel9.setOpaque(false);
        jPanel9.setLayout(new java.awt.GridBagLayout());

        jButton3.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/optionspanels/wunda_blau/add.png"))); // NOI18N
        jButton3.setName("jButton3");                                                                // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton3ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel9.add(jButton3, gridBagConstraints);
        jButton3.setVisible(isEditable());

        jButton2.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/optionspanels/wunda_blau/remove.png"))); // NOI18N
        jButton2.setName("jButton2");                                                                   // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel9.add(jButton2, gridBagConstraints);
        jButton2.setVisible(isEditable());

        filler7.setName("filler7"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel9.add(filler7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel5.add(jPanel9, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanel1.add(jPanel5, gridBagConstraints);

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Bearbeitungshistorie"));
        jPanel10.setName("jPanel10"); // NOI18N
        jPanel10.setOpaque(false);
        jPanel10.setLayout(new java.awt.GridBagLayout());

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        jXTable2.setModel(new VorgangBearbeitungTableModel());
        jXTable2.setName("jXTable2"); // NOI18N
        jScrollPane5.setViewportView(jXTable2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel10.add(jScrollPane5, gridBagConstraints);

        jPanel12.setName("jPanel12"); // NOI18N
        jPanel12.setOpaque(false);
        jPanel12.setLayout(new java.awt.GridBagLayout());

        jButton4.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/optionspanels/wunda_blau/add.png"))); // NOI18N
        jButton4.setName("jButton4");                                                                // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton4ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(jButton4, gridBagConstraints);
        jButton4.setVisible(isEditable());

        jButton5.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/optionspanels/wunda_blau/remove.png"))); // NOI18N
        jButton5.setName("jButton5");                                                                   // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton5ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(jButton5, gridBagConstraints);
        jButton5.setVisible(isEditable());

        filler8.setName("filler8"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel12.add(filler8, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel10.add(jPanel12, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel1.add(jPanel10, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(jPanel1, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton2ActionPerformed
        final CidsBean flaecheBean = ((VorgangFlaecheTableModel)jXTable1.getModel()).getCidsBean(
                jXTable1.getRowSorter().convertRowIndexToModel(jXTable1.getSelectedRow()));
        ((VorgangFlaecheTableModel)jXTable1.getModel()).remove(flaecheBean);
    }                                                                            //GEN-LAST:event_jButton2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXTable1MouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_jXTable1MouseClicked
        if (!isEditable() && (evt.getClickCount() == 2)) {
            final CidsBean flaecheBean = ((VorgangFlaecheTableModel)jXTable1.getModel()).getCidsBean(
                    jXTable1.getRowSorter().convertRowIndexToModel(jXTable1.getSelectedRow()));
            ComponentRegistry.getRegistry()
                    .getDescriptionPane()
                    .gotoMetaObjectNode(new MetaObjectNode(flaecheBean), false);
        }
    }                                                                        //GEN-LAST:event_jXTable1MouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jList1MouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_jList1MouseClicked
        if (evt.getClickCount() == 2) {
            final CidsBean bplanBean = jList1.getSelectedValue();
            ComponentRegistry.getRegistry()
                    .getDescriptionPane()
                    .gotoMetaObjectNode(new MetaObjectNode(bplanBean), false);
        }
    }                                                                      //GEN-LAST:event_jList1MouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnReportActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnReportActionPerformed
        AlboReportGenerator.startVorgangReportDownload(
            getCidsBean(),
            this,
            AlboVorgangReportServerAction.TASK_NAME,
            getConnectionContext());
    }                                                                             //GEN-LAST:event_btnReportActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton3ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton3ActionPerformed
        final Object selectedItem = comboBoxFilterDialog1.showAndGetSelected();
        if (selectedItem instanceof CidsBean) {
            final CidsBean flaecheBean = (CidsBean)selectedItem;
            ((VorgangFlaecheTableModel)jXTable1.getModel()).add(flaecheBean);
        }
    }                                                                            //GEN-LAST:event_jButton3ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jRadioButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jRadioButton1ActionPerformed
        lageSelectionChanged();
    }                                                                                 //GEN-LAST:event_jRadioButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jRadioButton2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jRadioButton2ActionPerformed
        lageSelectionChanged();
    }                                                                                 //GEN-LAST:event_jRadioButton2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton4ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton4ActionPerformed
        try {
            final CidsBean bearbeitungBean = CidsBean.createNewCidsBeanFromTableName(
                    "WUNDA_BLAU",
                    "albo_bearbeitung",
                    getConnectionContext());
            ((VorgangBearbeitungTableModel)jXTable2.getModel()).add(bearbeitungBean);
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
    }                                                                            //GEN-LAST:event_jButton4ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton5ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton5ActionPerformed
        if (jXTable2.getSelectedRow() >= 0) {
            final CidsBean bearbeitungBean = ((VorgangBearbeitungTableModel)jXTable2.getModel()).getCidsBean(
                    jXTable2.getRowSorter().convertRowIndexToModel(jXTable2.getSelectedRow()));
            ((VorgangBearbeitungTableModel)jXTable2.getModel()).remove(bearbeitungBean);
        }
    }                                                                            //GEN-LAST:event_jButton5ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jRadioButton3ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jRadioButton3ActionPerformed
        lageSelectionChanged();
    }                                                                                 //GEN-LAST:event_jRadioButton3ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnReport1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnReport1ActionPerformed
        AlboReportGenerator.startVorgangReportDownload(
            getCidsBean(),
            this,
            AlboVorgangExtReportServerAction.TASK_NAME,
            getConnectionContext());
    }                                                                              //GEN-LAST:event_btnReport1ActionPerformed

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private String getNewSchluessel() throws Exception {
        final Collection<Integer> maxSchluessels = SessionManager.getProxy()
                    .customServerSearch(new AlboVorgangNextSchluesselServerSearch(), getConnectionContext());
        if ((maxSchluessels != null) && !maxSchluessels.isEmpty()) {
            final Integer maxSchluessel = maxSchluessels.iterator().next();
            if (maxSchluessel != null) {
                return StringUtils.leftPad(String.valueOf(maxSchluessel + 1), 5, '0');
            }
        }
        return "00001";
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        bindingGroup.unbind();
        comboBoxFilterDialog1.refresh();
        if (cidsBean != null) {
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cidsBean,
                getConnectionContext());
            this.cidsBean = cidsBean;

            RendererTools.makeReadOnly(jRadioButton1, !isEditable());
            RendererTools.makeReadOnly(jRadioButton2, !isEditable());
            RendererTools.makeReadOnly(jRadioButton3, !isEditable());

            bindingGroup.bind();

            if (MetaObject.NEW == cidsBean.getMetaObject().getStatus()) {
                new SwingWorker<String, Void>() {

                        @Override
                        protected String doInBackground() throws Exception {
                            return getNewSchluessel();
                        }

                        @Override
                        protected void done() {
                            try {
                                final String newSchluessel = get();
                                getCidsBean().setProperty("schluessel", newSchluessel);
                            } catch (final Exception ex) {
                                LOG.error(ex, ex);
                            }
                        }
                    }.execute();
            }
            if (!isEditable()) {
                RendererTools.makeReadOnly(getBindingGroup(), "cidsBean");
            }
            RendererTools.makeReadOnly(jTextField1);
        }
        updateLageFields();

        ((VorgangFlaecheTableModel)jXTable1.getModel()).setCidsBeans((cidsBean != null)
                ? cidsBean.getBeanCollectionProperty("arr_flaechen") : null);
        ((VorgangBearbeitungTableModel)jXTable2.getModel()).setCidsBeans((cidsBean != null)
                ? cidsBean.getBeanCollectionProperty("n_bearbeitungen") : null);

        final DecimalFormat formatter = new DecimalFormat("####");
        jXTable1.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {

                @Override
                public Component getTableCellRendererComponent(final JTable table,
                        final Object value,
                        final boolean isSelected,
                        final boolean hasFocus,
                        final int row,
                        final int column) {
                    final Component component = super.getTableCellRendererComponent(
                            table,
                            value,
                            isSelected,
                            hasFocus,
                            row,
                            column);
                    if (value != null) {
                        ((JLabel)component).setText(formatter.format((Integer)value));
                    }

                    return component;
                }
            });
        jXTable2.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {

                @Override
                public Component getTableCellRendererComponent(final JTable table,
                        final Object value,
                        final boolean isSelected,
                        final boolean hasFocus,
                        final int row,
                        final int column) {
                    final Component component = super.getTableCellRendererComponent(
                            table,
                            value,
                            isSelected,
                            hasFocus,
                            row,
                            column);
                    if (value != null) {
                        ((JLabel)component).setText(formatter.format((Integer)value));
                    }

                    return component;
                }
            });

        searchBplaene();
    }

    @Override
    public void dispose() {
        bindingGroup.unbind();
        cidsBean = null;
        ((VorgangFlaecheTableModel)jXTable1.getModel()).clear();
        ((VorgangBearbeitungTableModel)jXTable2.getModel()).clear();
        ((DefaultListModel<CidsBean>)jList1.getModel()).clear();
    }

    @Override
    public String getTitle() {
        return String.format("Altlastenkataster - Vorgang: %s", (String)cidsBean.getProperty("schluessel"));
    }

    /**
     * DOCUMENT ME!
     */
    private void updateLageFields() {
        final Boolean lageAusBPlan = (Boolean)cidsBean.getProperty("lage_aus_bplan");
        jRadioButton1.setSelected(lageAusBPlan == null);
        jRadioButton2.setSelected(Boolean.TRUE.equals(lageAusBPlan));
        jRadioButton3.setSelected(Boolean.FALSE.equals(lageAusBPlan));
        if (isEditable()) {
            jComboBox4.setEnabled(jRadioButton1.isSelected());
            jTextField5.setEnabled(jRadioButton1.isSelected());
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void lageSelectionChanged() {
        if (isEditable()) {
            try {
                cidsBean.setProperty(
                    "lage_aus_bplan",
                    jRadioButton1.isSelected() ? null : (jRadioButton2.isSelected() ? Boolean.TRUE : Boolean.FALSE));
                updateLageFields();
            } catch (final Exception ex) {
                LOG.error(ex, ex);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public JComponent getTitleComponent() {
        return panTitle;
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        initComponents();
        RendererTools.makeReadOnly(jRadioButton1, !isEditable());
        if (isEditable()) {
            jXTable2.setDefaultEditor(Date.class, new DateCellEditor());
            try {
                new CidsBeanDropTarget(jXTable1);
            } catch (final Exception ex) {
                LOG.warn("Error while creating CidsBeanDropTarget", ex); // NOI18N
            }
            AutoCompleteDecorator.decorate(jComboBox4);
        }
        RendererTools.makeReadOnly(jList1);
        RendererTools.makeReadOnly(jXTable1);
        if (!isEditable()) {
            RendererTools.makeReadOnly(jXTable2);
        }

        try {
            ceBearbeiter = new DefaultBindableComboboxCellEditor(CidsBean.getMetaClassFromTableName(
                        "WUNDA_BLAU",
                        "ALBO_BEARBEITER",
                        getConnectionContext()),
                    false,
                    true) {

                    @Override
                    public boolean isCellEditable(final EventObject anEvent) {
                        if (anEvent instanceof MouseEvent) {
                            return ((MouseEvent)anEvent).getClickCount() >= 1;
                        }
                        return true;
                    }
                };
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }

        try {
            ceGeschaeft = new DefaultBindableComboboxCellEditor(CidsBean.getMetaClassFromTableName(
                        "WUNDA_BLAU",
                        "ALBO_BEARBEITUNG_GESCHAEFT",
                        getConnectionContext())) {

                    @Override
                    public boolean isCellEditable(final EventObject anEvent) {
                        if (anEvent instanceof MouseEvent) {
                            return ((MouseEvent)anEvent).getClickCount() >= 1;
                        }
                        return true;
                    }
                };
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public BindingGroup getBindingGroup() {
        return bindingGroup;
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  beans  DOCUMENT ME!
     */
    private void beansDroppedIntoListener(final List<CidsBean> beans) {
        if (isEditable()) {
            final List<CidsBean> flaecheBeans = ((VorgangFlaecheTableModel)jXTable1.getModel()).getCidsBeans();
            for (final CidsBean flaecheBean : beans) {
                if (flaecheBean.getMetaObject().getMetaClass().getTableName().equalsIgnoreCase("albo_flaeche")
                            && !flaecheBeans.contains(flaecheBean)) {
                    ((VorgangFlaecheTableModel)jXTable1.getModel()).add(flaecheBean);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void searchBplaene() {
        jList1.setModel(new DefaultListModel<CidsBean>());
        new SwingWorker<List<CidsBean>, Void>() {

                @Override
                protected List<CidsBean> doInBackground() throws Exception {
                    final BplaeneMonSearch search = new BplaeneMonSearch();

                    Geometry geom = null;
                    for (final CidsBean flaecheBean : getCidsBean().getBeanCollectionProperty("arr_flaechen")) {
                        if (geom == null) {
                            geom = (Geometry)flaecheBean.getProperty("fk_geom.geo_field");
                        } else {
                            if (flaecheBean.getProperty("fk_geom.geo_field") != null) {
                                geom.union((Geometry)flaecheBean.getProperty("fk_geom.geo_field"));
                            }
                        }
                    }
                    search.setGeometry(geom);

                    final Collection<MetaObjectNode> mons = (Collection)SessionManager.getProxy()
                                .customServerSearch(SessionManager.getSession().getUser(),
                                        search,
                                        getConnectionContext());

                    if (mons == null) {
                        return null;
                    }
                    final List<CidsBean> beans = new ArrayList<>();
                    for (final MetaObjectNode mon : mons) {
                        beans.add(SessionManager.getProxy().getMetaObject(
                                mon.getObjectId(),
                                mon.getClassId(),
                                "WUNDA_BLAU",
                                getConnectionContext()).getBean());
                    }
                    return beans;
                }

                @Override
                protected void done() {
                    try {
                        final List<CidsBean> beans = get();
                        if (beans != null) {
                            for (final CidsBean bean : beans) {
                                ((DefaultListModel<CidsBean>)jList1.getModel()).addElement(bean);
                            }
                        }
                    } catch (final Exception ex) {
                        LOG.fatal(ex, ex);
                    }
                }
            }.execute();
    }

    @Override
    public void setTitle(final String string) {
    }

    @Override
    public void editorClosed(final EditorClosedEvent ece) {
    }

    @Override
    public boolean prepareForSave() {
        if (MetaObject.NEW == cidsBean.getMetaObject().getStatus()) {
            try {
                final String newSchluessel = getNewSchluessel();
                if (!newSchluessel.equals(getCidsBean().getProperty("schluessel"))) {
                    // TODO warnung das neuer schluessel generiert wurde.
                    // abfrage ob abbrechen oder mit neuem schluessel speichern.
                    getCidsBean().setProperty("schluessel", newSchluessel);
                    // return false;
                }
            } catch (final Exception ex) {
                LOG.error(ex, ex);
                return false;
            }
        }
        if (cidsBean.getProperty("loeschen") == null) {
            try {
                cidsBean.setProperty("loeschen", false);
            } catch (final Exception ex) {
                LOG.error(ex, ex);
                return false;
            }
        }
        return true;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class DroppedBeansTable extends JXTable implements CidsBeanDropListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void beansDropped(final ArrayList<CidsBean> beans) {
            beansDroppedIntoListener(beans);
        }
    }
    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class VorgangFlaecheTableModel extends CidsBeansTableModel {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new VorgangFlaecheTableModel object.
         */
        public VorgangFlaecheTableModel() {
            super(FLAECHE_COLUMN_PROPERTIES, FLAECHE_COLUMN_NAMES, FLAECHE_COLUMN_CLASSES);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class VorgangBearbeitungTableModel extends CidsBeansTableModel {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new VorgangBearbeitungTableModel object.
         */
        public VorgangBearbeitungTableModel() {
            super(BEARBEITUNG_COLUMN_PROPERTIES, BEARBEITUNG_COLUMN_NAMES, BEARBEITUNG_COLUMN_CLASSES, true, false);
        }
    }
    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class BearbeitungTable extends JXTable {

        //~ Methods ------------------------------------------------------------

        @Override
        public TableCellEditor getCellEditor(final int row, final int column) {
            switch (column) {
                case 1: {
                    return ceBearbeiter;
                }
                case 2: {
                    return ceGeschaeft;
                }
                default: {
                    return super.getCellEditor(row, column); // To change body of generated methods, choose Tools |
                                                             // Templates.
                }
            }
        }
    }
    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class DateCellEditor extends AbstractCellEditor implements TableCellEditor {

        //~ Instance fields ----------------------------------------------------

        private final JXDatePicker datePicker;
        private boolean useSqlDate = true;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new DateCellEditor object.
         */
        public DateCellEditor() {
            datePicker = new JXDatePicker();
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public Object getCellEditorValue() {
            if (useSqlDate) {
                return (datePicker.getDate() != null) ? new java.sql.Date(datePicker.getDate().getTime()) : null;
            } else {
                return datePicker.getDate();
            }
        }

        @Override
        public Component getTableCellEditorComponent(final JTable table,
                final Object value,
                final boolean isSelected,
                final int row,
                final int column) {
            if (value instanceof Date) {
                useSqlDate = false;
                datePicker.setDate((Date)value);
            }

            if (value instanceof java.sql.Date) {
                useSqlDate = true;
                final java.sql.Date date = (java.sql.Date)value;
                datePicker.setDate(new Date(date.getTime()));
            }

            return datePicker;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class AlboVorgangFlaecheFilterDialog extends ComboBoxFilterDialog {

        //~ Static fields/initializers -----------------------------------------

        private static final AlboVorgangFlaecheFilterDialog INSTANCE = new AlboVorgangFlaecheFilterDialog();

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new AlboVorgangFlaecheFilterDialog object.
         */
        private AlboVorgangFlaecheFilterDialog() {
            super(
                null,
                new AlboFlaecheLightweightSearch(),
                "Erhebungsfläche auswählen",
                ConnectionContext.create(
                    AbstractConnectionContext.Category.STATIC,
                    AlboVorgangFlaecheFilterDialog.class.getSimpleName()));
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public static AlboVorgangFlaecheFilterDialog getInstance() {
            return INSTANCE;
        }
    }
}
