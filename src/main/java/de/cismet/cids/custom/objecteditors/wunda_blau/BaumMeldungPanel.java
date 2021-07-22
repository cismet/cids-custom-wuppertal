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

import Sirius.navigator.ui.DescriptionPaneFS;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;
import de.cismet.cids.custom.objecteditors.utils.BaumChildrenLoader;
import de.cismet.cids.custom.objecteditors.utils.TableUtils;
import org.apache.log4j.Logger;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.objectrenderer.wunda_blau.BaumAnsprechpartnerRenderer;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;
import de.cismet.cids.dynamics.Disposable;
import de.cismet.cids.editors.DefaultBindableDateChooser;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;
import de.cismet.tools.gui.StaticSwingTools;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.logging.Level;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import lombok.Getter;
import lombok.Setter;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.openide.awt.Mnemonics;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class BaumMeldungPanel extends javax.swing.JPanel implements Disposable, CidsBeanStore, ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------
    
    
    private static final Comparator<Object> DATE_COMPARATOR = new Comparator<Object>() {

            @Override
           /* public int compare(final Object o1, final Object o2) {
                return AlphanumComparator.getInstance().compare(String.valueOf(o1), String.valueOf(o2));
            }*/
            public int compare(final Object o1, final Object o2) {
                    final String o1String = String.valueOf(((CidsBean)o1).getProperty("datum"));
                    final String o2String = String.valueOf(((CidsBean)o2).getProperty("datum"));

                    try {
                        final Integer o1Int = Integer.parseInt(o1String);
                        final Integer o2Int = Integer.parseInt(o2String);

                        return o1Int.compareTo(o2Int);
                    } catch (NumberFormatException e) {
                        // do nothing
                    }

                    return String.valueOf(o1).compareTo(String.valueOf(o2));
                }
        };
    private static final Comparator<Object> ID_COMPARATOR = new Comparator<Object>() {

            @Override
           /* public int compare(final Object o1, final Object o2) {
                return AlphanumComparator.getInstance().compare(String.valueOf(o1), String.valueOf(o2));
            }*/
            public int compare(final Object o1, final Object o2) {
                    final String o1String = String.valueOf(((CidsBean)o1).getProperty("id"));
                    final String o2String = String.valueOf(((CidsBean)o2).getProperty("id"));

                    try {
                        final Integer o1Int = Integer.parseInt(o1String);
                        final Integer o2Int = Integer.parseInt(o2String);

                        return o1Int.compareTo(o2Int);
                    } catch (NumberFormatException e) {
                        // do nothing
                    }

                    return String.valueOf(o1).compareTo(String.valueOf(o2));
                }
        };
     
   /* private static enum WhichCase {

        //~ Enum constants -----------------------------------------------------

        ORTSTERMIN, SCHADEN
    }*/
    
    private List<CidsBean> ortsterminBeans = new ArrayList<>();
    private List<CidsBean> changedOrtsterminBeans = new ArrayList<>();
    private List<CidsBean> schadenBeans = new ArrayList<>();
    private List<CidsBean> changedSchadenBeans = new ArrayList<>();
    private List<CidsBean> noSaveToDeleteBeansSchaden = new ArrayList<>();
    private final List<CidsBean> deletedOrtsterminBeans = new ArrayList<>();
    private final List<CidsBean> deletedSchadenBeans = new ArrayList<>();
    //private final Map <String, List<CidsBean>> schadenBeanMap = new HashMap <>();
    //private final Map <String, List<CidsBean>> ortsterminBeanMap = new HashMap <>();
    private BaumChildrenLoader.Listener loadChildrenListenerEditor;
    private BaumChildrenLoader.Listener loadChildrenListenerRenderer;
    @Getter @Setter private static Integer counterSchaden = -1;
    
    
    private static final Logger LOG = Logger.getLogger(BaumMeldungPanel.class);
    //public static final String CHILD_TOSTRING_TEMPLATE = "%s";
    //public static final String[] ORT_TOSTRING_FIELDS = {"datum"};
    //public static final String ORT_TABLE = "baum_ortstermin";
    //public static final String[] SCHADEN_TOSTRING_FIELDS = {"id"};
    //public static final String SCHADEN_TABLE = "baum_schaden";
    //public static final String CHILD_FK = "fk_meldung";
    
    public static final String FIELD__APARTNER = "arr_ansprechpartner";         // baum_meldung
    public static final String FIELD__DATUM = "datum";                          // baum_ortstermin
    public static final String FIELD__FK_MELDUNG = "fk_meldung";                // baum_ortstermin bzw. schaden
    public static final String FIELD__ID = "id";                                // baum_schaden
    public static final String FIELD__SCHADEN_PRIVAT = "privatbaum";            // baum_schaden
    public static final String FIELD__SCHADEN_OHNE = "ohne_schaden";            // baum_schaden
    public static final String FIELD__SCHADEN_KRONE = "kronenschaden";          // baum_schaden
    public static final String FIELD__SCHADEN_STAMM = "stammschaden";           // baum_schaden
    public static final String FIELD__SCHADEN_WURZEL = "wurzelschaden";         // baum_schaden
    public static final String FIELD__SCHADEN_STURM = "sturmschaden";           // baum_schaden
    public static final String FIELD__SCHADEN_ABGESTORBEN = "abgestorben";      // baum_schaden
    public static final String FIELD__SCHADEN_BAU = "baumassnahme";             // baum_schaden
    public static final String FIELD__SCHADEN_GUTACHTEN = "gutachten";          // baum_schaden
    public static final String FIELD__SCHADEN_BERATUNG = "baumberatung";        // baum_schaden
    public static final String FIELD__SCHADEN_EINGANG = "eingegangen";          // baum_schaden
    public static final String FIELD__SCHADEN_FAELLUNG = "faellung";            // baum_schaden
    public static final String TABLE__ORT = "baum_ortstermin";
    public static final String TABLE__SCHADEN = "baum_schaden";
    
    
    
    public static final String BUNDLE_AP_QUESTION = 
            "BaumMeldungPanel.btnRemoveAnsprechpartnerActionPerformed().question";
    public static final String BUNDLE_AP_TITLE = 
            "BaumMeldungPanel.btnRemoveAnsprechpartnerActionPerformed().title";
    public static final String BUNDLE_AP_ERRORTITLE = 
            "BaumMeldungPanel.btnRemoveAnsprechpartnerActionPerformed().errortitle";
    public static final String BUNDLE_AP_ERRORTEXT = 
            "BaumMeldungPanel.btnRemoveAnsprechpartnerActionPerformed().errortext";
    public static final String BUNDLE_PANE_TITLE_SCHADEN = 
            "BaumMeldungPanel.btnRemoveSchadenActionPerformed().JOptionPane.title";
    public static final String BUNDLE_DEL_SCHADEN =
        "BaumMeldungPanel.btnRemoveSchadenActionPerformed().JOptionPane.message";
    public static final String BUNDLE_PANE_PREFIX =
        "BaumMeldungPanel.prepareForSave().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX =
        "BaumMeldungPanel.prepareForSave().JOptionPane.message.suffix";
    
    public static final String BUNDLE_NODATE = "BaumMeldungPanel.prepareForSave().noDatum";
    public static final String BUNDLE_PANE_TITLE = "BaumMeldungPanel.prepareForSave().JOptionPane.title";
    public static final String BUNDLE_PANE_TITLE_ERROR_ORT = "BaumMeldungPanel.zeigeErrorOrt().JOptionPane.title";
    public static final String BUNDLE_PANE_TITLE_ERROR_SCHADEN = "BaumMeldungPanel.zeigeErrorSchaden().JOptionPane.title";
    public static final String BUNDLE_ERROR_ORT = "BaumMeldungPanel.zeigeErrorOrt().JOptionPane.meldung";
    public static final String BUNDLE_ERROR_SCHADEN = "BaumMeldungPanel.zeigeErrorSchaden().JOptionPane.meldung";
    public static final String BUNDLE_PANE_PREFIX_SELECTION =
        "BaumMeldungPanel.btnApartnerActionPerformed().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX_SELECTION =
        "BaumMeldungPanel.btnApartnerActionPerformed().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE_SELECTION = "BaumMeldungPanel.btnApartnerActionPerformed().JOptionPane.title";
    public static final String BUNDLE_PANE_SELECTION =
        "BaumMeldungPanel.btnApartnerActionPerformed().JOptionPane.message";

    /*private final BaumChildLightweightSearch searchMeldung = new BaumChildLightweightSearch(
            CHILD_TOSTRING_TEMPLATE,
            ORT_TOSTRING_FIELDS,
            ORT_TABLE,
            CHILD_FK);
    private final BaumChildLightweightSearch searchSchaden = new BaumChildLightweightSearch(
            CHILD_TOSTRING_TEMPLATE,
            SCHADEN_TOSTRING_FIELDS,
            SCHADEN_TABLE,
            CHILD_FK);*/
    
    private static final ListModel MODEL_ERROR = new DefaultListModel() {
            {
                add(0, "FEHLER!");
            }
        };
   /* private static final ListModel MODEL_LOAD = new DefaultListModel() {

            {
                add(0, "wird geladen...");
            }
        };*/
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        GridBagConstraints gridBagConstraints;
        bindingGroup = new BindingGroup();

        dlgAddApartner = new JDialog();
        panAddApartner = new JPanel();
        lblAuswaehlenApartner = new JLabel();
        final MetaObject[] apartner = ObjectRendererUtils.getLightweightMetaObjectsForTable("baum_ansprechpartner", new String[]{"name", "bemerkung"}, getConnectionContext());
        if(apartner != null) {
            Arrays.sort(apartner);
            cbApartner = new JComboBox(apartner);
        }
        panMenButtonsApartner = new JPanel();
        btnMenAbortApartner = new JButton();
        btnMenOkApartner = new JButton();
        dlgAddOrtstermin = new JDialog();
        panAddOrtstermin = new JPanel();
        lblAuswaehlenOrtstermin = new JLabel();
        panMenButtonsOrtstermin = new JPanel();
        btnMenAbortOrtstermin = new JButton();
        btnMenOkOrtstermin = new JButton();
        dcOrtstermin = new DefaultBindableDateChooser();
        pnlCard1 = new JPanel();
        jTabbedPane = new JTabbedPane();
        jPanelAllgemein = new JPanel();
        panInfo = new JPanel();
        lblAbgenommen = new JLabel();
        chAbgenommen = new JCheckBox();
        lblApartner = new JLabel();
        panApartner = new JPanel();
        lblBemerkung = new JLabel();
        scpBemerkung = new JScrollPane();
        taBemerkung = new JTextArea();
        panFillerUnten5 = new JPanel();
        btnApartner = new JButton();
        scpApartner = new JScrollPane();
        lstApartner = new JList();
        panButtonsApartner = new JPanel();
        btnAddApartner = new JButton();
        btnRemoveApartner = new JButton();
        filler1 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
        jPanelOrtstermine = new JPanel();
        panOrtstermin = new JPanel();
        panOrtstermineMain = new JPanel();
        baumOrtsterminPanel = baumOrtsterminPanel = new BaumOrtsterminPanel(this,null, isEditor, this.connectionContext);
        lblLadenOrt = new JLabel();
        scpLaufendeOrtstermine = new JScrollPane();
        lstOrtstermine = new JList();
        panControlsNewOrtstermine = new JPanel();
        btnAddNewOrtstermin = new JButton();
        btnRemoveOrtstermin = new JButton();
        panFillerUnten3 = new JPanel();
        jPanelSchaeden = new JPanel();
        panFillerUnten4 = new JPanel();
        panSchaden = new JPanel();
        lblLadenSchaden = new JLabel();
        scpLaufendeSchaeden = new JScrollPane();
        lstSchaeden = new JList();
        panSchaedenMain = new JPanel();
        baumSchadenPanel = baumSchadenPanel = new BaumSchadenPanel(this, null, isEditor, this.connectionContext);
        panControlsNewSchaden = new JPanel();
        btnAddNewSchaden = new JButton();
        btnRemoveSchaden = new JButton();

        FormListener formListener = new FormListener();

        dlgAddApartner.setTitle("Ansprechpartner");
        dlgAddApartner.setModal(true);
        dlgAddApartner.setName("dlgAddApartner"); // NOI18N

        panAddApartner.setName("panAddApartner"); // NOI18N
        panAddApartner.setLayout(new GridBagLayout());

        lblAuswaehlenApartner.setText("Bitte den Ansprechpartner auswählen:");
        lblAuswaehlenApartner.setName("lblAuswaehlenApartner"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panAddApartner.add(lblAuswaehlenApartner, gridBagConstraints);

        cbApartner.setName("cbApartner"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panAddApartner.add(cbApartner, gridBagConstraints);

        panMenButtonsApartner.setName("panMenButtonsApartner"); // NOI18N
        panMenButtonsApartner.setLayout(new GridBagLayout());

        btnMenAbortApartner.setText("Abbrechen");
        btnMenAbortApartner.setName("btnMenAbortApartner"); // NOI18N
        btnMenAbortApartner.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsApartner.add(btnMenAbortApartner, gridBagConstraints);

        btnMenOkApartner.setText("Ok");
        btnMenOkApartner.setName("btnMenOkApartner"); // NOI18N
        btnMenOkApartner.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsApartner.add(btnMenOkApartner, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panAddApartner.add(panMenButtonsApartner, gridBagConstraints);

        dlgAddApartner.getContentPane().add(panAddApartner, BorderLayout.CENTER);

        dlgAddOrtstermin.setTitle("Datum Ortstermin");
        dlgAddOrtstermin.setModal(true);
        dlgAddOrtstermin.setName("dlgAddOrtstermin"); // NOI18N

        panAddOrtstermin.setName("panAddOrtstermin"); // NOI18N
        panAddOrtstermin.setLayout(new GridBagLayout());

        lblAuswaehlenOrtstermin.setText("Bitte das Datum des Ortstermins auswählen:");
        lblAuswaehlenOrtstermin.setName("lblAuswaehlenOrtstermin"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panAddOrtstermin.add(lblAuswaehlenOrtstermin, gridBagConstraints);

        panMenButtonsOrtstermin.setName("panMenButtonsOrtstermin"); // NOI18N
        panMenButtonsOrtstermin.setLayout(new GridBagLayout());

        btnMenAbortOrtstermin.setText("Abbrechen");
        btnMenAbortOrtstermin.setName("btnMenAbortOrtstermin"); // NOI18N
        btnMenAbortOrtstermin.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsOrtstermin.add(btnMenAbortOrtstermin, gridBagConstraints);

        btnMenOkOrtstermin.setText("Ok");
        btnMenOkOrtstermin.setName("btnMenOkOrtstermin"); // NOI18N
        btnMenOkOrtstermin.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsOrtstermin.add(btnMenOkOrtstermin, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panAddOrtstermin.add(panMenButtonsOrtstermin, gridBagConstraints);

        dcOrtstermin.setName("dcOrtstermin"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panAddOrtstermin.add(dcOrtstermin, gridBagConstraints);

        dlgAddOrtstermin.getContentPane().add(panAddOrtstermin, BorderLayout.CENTER);

        setName("Form"); // NOI18N
        setOpaque(false);
        setLayout(new GridBagLayout());

        pnlCard1.setName("pnlCard1"); // NOI18N
        pnlCard1.setOpaque(false);
        pnlCard1.setLayout(new GridBagLayout());

        jTabbedPane.setName("jTabbedPane"); // NOI18N

        jPanelAllgemein.setName("jPanelAllgemein"); // NOI18N
        jPanelAllgemein.setOpaque(false);
        jPanelAllgemein.setLayout(new GridBagLayout());

        panInfo.setMinimumSize(new Dimension(297, 230));
        panInfo.setName("panInfo"); // NOI18N
        panInfo.setOpaque(false);
        panInfo.setPreferredSize(new Dimension(337, 230));
        panInfo.setLayout(new GridBagLayout());

        lblAbgenommen.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblAbgenommen, NbBundle.getMessage(BaumMeldungPanel.class, "BaumMeldungPanel.lblAbgenommen.text")); // NOI18N
        lblAbgenommen.setName("lblAbgenommen"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panInfo.add(lblAbgenommen, gridBagConstraints);

        chAbgenommen.setContentAreaFilled(false);
        chAbgenommen.setName("chAbgenommen"); // NOI18N

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.abgenommen}"), chAbgenommen, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        chAbgenommen.addChangeListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panInfo.add(chAbgenommen, gridBagConstraints);

        lblApartner.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblApartner, "Ansprechpartner:");
        lblApartner.setName("lblApartner"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panInfo.add(lblApartner, gridBagConstraints);

        panApartner.setName("panApartner"); // NOI18N
        panApartner.setOpaque(false);
        panApartner.setLayout(new GridBagLayout());
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panInfo.add(panApartner, gridBagConstraints);

        lblBemerkung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblBemerkung, "Bemerkung:");
        lblBemerkung.setName("lblBemerkung"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panInfo.add(lblBemerkung, gridBagConstraints);

        scpBemerkung.setName("scpBemerkung"); // NOI18N
        scpBemerkung.setOpaque(false);

        taBemerkung.setLineWrap(true);
        taBemerkung.setRows(2);
        taBemerkung.setWrapStyleWord(true);
        taBemerkung.setName("taBemerkung"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.bemerkung}"), taBemerkung, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpBemerkung.setViewportView(taBemerkung);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panInfo.add(scpBemerkung, gridBagConstraints);

        panFillerUnten5.setName("panFillerUnten5"); // NOI18N
        panFillerUnten5.setOpaque(false);

        GroupLayout panFillerUnten5Layout = new GroupLayout(panFillerUnten5);
        panFillerUnten5.setLayout(panFillerUnten5Layout);
        panFillerUnten5Layout.setHorizontalGroup(panFillerUnten5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panFillerUnten5Layout.setVerticalGroup(panFillerUnten5Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        panInfo.add(panFillerUnten5, gridBagConstraints);

        btnApartner.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/icon-explorerwindow.png"))); // NOI18N
        btnApartner.setBorderPainted(false);
        btnApartner.setContentAreaFilled(false);
        btnApartner.setFocusPainted(false);
        btnApartner.setName("btnApartner"); // NOI18N
        btnApartner.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.LAST_LINE_END;
        gridBagConstraints.insets = new Insets(15, 0, 5, 5);
        panInfo.add(btnApartner, gridBagConstraints);

        scpApartner.setMinimumSize(new Dimension(258, 66));
        scpApartner.setName("scpApartner"); // NOI18N

        lstApartner.setFont(new Font("Dialog", 0, 12)); // NOI18N
        lstApartner.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstApartner.setName("lstApartner"); // NOI18N

        ELProperty eLProperty = ELProperty.create("${cidsBean.arr_ansprechpartner}");
        JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, lstApartner);
        bindingGroup.addBinding(jListBinding);

        scpApartner.setViewportView(lstApartner);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panInfo.add(scpApartner, gridBagConstraints);

        panButtonsApartner.setName("panButtonsApartner"); // NOI18N
        panButtonsApartner.setOpaque(false);
        panButtonsApartner.setLayout(new GridBagLayout());

        btnAddApartner.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddApartner.setName("btnAddApartner"); // NOI18N
        btnAddApartner.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panButtonsApartner.add(btnAddApartner, gridBagConstraints);

        btnRemoveApartner.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveApartner.setName("btnRemoveApartner"); // NOI18N
        btnRemoveApartner.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panButtonsApartner.add(btnRemoveApartner, gridBagConstraints);

        filler1.setName("filler1"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panButtonsApartner.add(filler1, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(10, 2, 2, 2);
        panInfo.add(panButtonsApartner, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        jPanelAllgemein.add(panInfo, gridBagConstraints);

        jTabbedPane.addTab("Allgemeine Informationen", jPanelAllgemein);

        jPanelOrtstermine.setName("jPanelOrtstermine"); // NOI18N
        jPanelOrtstermine.setOpaque(false);
        jPanelOrtstermine.setLayout(new GridBagLayout());

        panOrtstermin.setName("panOrtstermin"); // NOI18N
        panOrtstermin.setOpaque(false);
        panOrtstermin.setLayout(new GridBagLayout());

        panOrtstermineMain.setName("panOrtstermineMain"); // NOI18N
        panOrtstermineMain.setOpaque(false);
        panOrtstermineMain.setLayout(new GridBagLayout());

        baumOrtsterminPanel.setName("baumOrtsterminPanel"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, lstOrtstermine, ELProperty.create("${selectedElement}"), baumOrtsterminPanel, BeanProperty.create("cidsBean"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 3, 0);
        panOrtstermineMain.add(baumOrtsterminPanel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(0, 5, 5, 5);
        panOrtstermin.add(panOrtstermineMain, gridBagConstraints);

        lblLadenOrt.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblLadenOrt.setForeground(new Color(153, 153, 153));
        Mnemonics.setLocalizedText(lblLadenOrt, NbBundle.getMessage(BaumMeldungPanel.class, "BaumMeldungPanel.lblLadenOrt.text")); // NOI18N
        lblLadenOrt.setName("lblLadenOrt"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panOrtstermin.add(lblLadenOrt, gridBagConstraints);

        scpLaufendeOrtstermine.setName("scpLaufendeOrtstermine"); // NOI18N

        lstOrtstermine.setModel(new DefaultListModel<>()
        );
        lstOrtstermine.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstOrtstermine.setFixedCellWidth(75);
        lstOrtstermine.setName("lstOrtstermine"); // NOI18N
        lstOrtstermine.addPropertyChangeListener(formListener);
        scpLaufendeOrtstermine.setViewportView(lstOrtstermine);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panOrtstermin.add(scpLaufendeOrtstermine, gridBagConstraints);

        panControlsNewOrtstermine.setName("panControlsNewOrtstermine"); // NOI18N
        panControlsNewOrtstermine.setOpaque(false);
        panControlsNewOrtstermine.setLayout(new GridBagLayout());

        btnAddNewOrtstermin.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddNewOrtstermin.setEnabled(false);
        btnAddNewOrtstermin.setMaximumSize(new Dimension(39, 20));
        btnAddNewOrtstermin.setMinimumSize(new Dimension(39, 20));
        btnAddNewOrtstermin.setName("btnAddNewOrtstermin"); // NOI18N
        btnAddNewOrtstermin.setPreferredSize(new Dimension(39, 25));
        btnAddNewOrtstermin.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panControlsNewOrtstermine.add(btnAddNewOrtstermin, gridBagConstraints);

        btnRemoveOrtstermin.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveOrtstermin.setEnabled(false);
        btnRemoveOrtstermin.setMaximumSize(new Dimension(39, 20));
        btnRemoveOrtstermin.setMinimumSize(new Dimension(39, 20));
        btnRemoveOrtstermin.setName("btnRemoveOrtstermin"); // NOI18N
        btnRemoveOrtstermin.setPreferredSize(new Dimension(39, 25));
        btnRemoveOrtstermin.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panControlsNewOrtstermine.add(btnRemoveOrtstermin, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 0, 5, 0);
        panOrtstermin.add(panControlsNewOrtstermine, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        jPanelOrtstermine.add(panOrtstermin, gridBagConstraints);

        panFillerUnten3.setName(""); // NOI18N
        panFillerUnten3.setOpaque(false);

        GroupLayout panFillerUnten3Layout = new GroupLayout(panFillerUnten3);
        panFillerUnten3.setLayout(panFillerUnten3Layout);
        panFillerUnten3Layout.setHorizontalGroup(panFillerUnten3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panFillerUnten3Layout.setVerticalGroup(panFillerUnten3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        jPanelOrtstermine.add(panFillerUnten3, gridBagConstraints);

        jTabbedPane.addTab("Ortstermine", jPanelOrtstermine);

        jPanelSchaeden.setName("jPanelSchaeden"); // NOI18N
        jPanelSchaeden.setOpaque(false);
        jPanelSchaeden.setLayout(new GridBagLayout());

        panFillerUnten4.setName("panFillerUnten4"); // NOI18N
        panFillerUnten4.setOpaque(false);

        GroupLayout panFillerUnten4Layout = new GroupLayout(panFillerUnten4);
        panFillerUnten4.setLayout(panFillerUnten4Layout);
        panFillerUnten4Layout.setHorizontalGroup(panFillerUnten4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panFillerUnten4Layout.setVerticalGroup(panFillerUnten4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        jPanelSchaeden.add(panFillerUnten4, gridBagConstraints);

        panSchaden.setName("panSchaden"); // NOI18N
        panSchaden.setOpaque(false);
        panSchaden.setLayout(new GridBagLayout());

        lblLadenSchaden.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblLadenSchaden.setForeground(new Color(153, 153, 153));
        Mnemonics.setLocalizedText(lblLadenSchaden, NbBundle.getMessage(BaumMeldungPanel.class, "BaumMeldungPanel.lblLadenSchaden.text")); // NOI18N
        lblLadenSchaden.setName("lblLadenSchaden"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblLadenSchaden, gridBagConstraints);

        scpLaufendeSchaeden.setName("scpLaufendeSchaeden"); // NOI18N

        lstSchaeden.setModel(new DefaultListModel());
        lstSchaeden.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstSchaeden.setName("lstSchaeden"); // NOI18N
        lstSchaeden.setVisibleRowCount(2);
        scpLaufendeSchaeden.setViewportView(lstSchaeden);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panSchaden.add(scpLaufendeSchaeden, gridBagConstraints);

        panSchaedenMain.setName("panSchaedenMain"); // NOI18N
        panSchaedenMain.setOpaque(false);
        panSchaedenMain.setLayout(new GridBagLayout());

        baumSchadenPanel.setName("baumSchadenPanel"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, lstSchaeden, ELProperty.create("${selectedElement}"), baumSchadenPanel, BeanProperty.create("cidsBean"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        panSchaedenMain.add(baumSchadenPanel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 0, 0, 0);
        panSchaden.add(panSchaedenMain, gridBagConstraints);

        panControlsNewSchaden.setName("panControlsNewSchaden"); // NOI18N
        panControlsNewSchaden.setOpaque(false);
        panControlsNewSchaden.setLayout(new GridBagLayout());

        btnAddNewSchaden.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddNewSchaden.setEnabled(false);
        btnAddNewSchaden.setMaximumSize(new Dimension(39, 20));
        btnAddNewSchaden.setMinimumSize(new Dimension(39, 20));
        btnAddNewSchaden.setName("btnAddNewSchaden"); // NOI18N
        btnAddNewSchaden.setPreferredSize(new Dimension(39, 25));
        btnAddNewSchaden.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panControlsNewSchaden.add(btnAddNewSchaden, gridBagConstraints);

        btnRemoveSchaden.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveSchaden.setEnabled(false);
        btnRemoveSchaden.setMaximumSize(new Dimension(39, 20));
        btnRemoveSchaden.setMinimumSize(new Dimension(39, 20));
        btnRemoveSchaden.setName("btnRemoveSchaden"); // NOI18N
        btnRemoveSchaden.setPreferredSize(new Dimension(39, 25));
        btnRemoveSchaden.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panControlsNewSchaden.add(btnRemoveSchaden, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 0, 5, 0);
        panSchaden.add(panControlsNewSchaden, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        jPanelSchaeden.add(panSchaden, gridBagConstraints);

        jTabbedPane.addTab("Schäden", jPanelSchaeden);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 10, 0);
        pnlCard1.add(jTabbedPane, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(pnlCard1, gridBagConstraints);

        bindingGroup.bind();
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements ActionListener, PropertyChangeListener, ChangeListener {
        FormListener() {}
        public void actionPerformed(ActionEvent evt) {
            if (evt.getSource() == btnApartner) {
                BaumMeldungPanel.this.btnApartnerActionPerformed(evt);
            }
            else if (evt.getSource() == btnAddApartner) {
                BaumMeldungPanel.this.btnAddApartnerActionPerformed(evt);
            }
            else if (evt.getSource() == btnRemoveApartner) {
                BaumMeldungPanel.this.btnRemoveApartnerActionPerformed(evt);
            }
            else if (evt.getSource() == btnAddNewOrtstermin) {
                BaumMeldungPanel.this.btnAddNewOrtsterminActionPerformed(evt);
            }
            else if (evt.getSource() == btnRemoveOrtstermin) {
                BaumMeldungPanel.this.btnRemoveOrtsterminActionPerformed(evt);
            }
            else if (evt.getSource() == btnAddNewSchaden) {
                BaumMeldungPanel.this.btnAddNewSchadenActionPerformed(evt);
            }
            else if (evt.getSource() == btnRemoveSchaden) {
                BaumMeldungPanel.this.btnRemoveSchadenActionPerformed(evt);
            }
            else if (evt.getSource() == btnMenAbortApartner) {
                BaumMeldungPanel.this.btnMenAbortApartnerActionPerformed(evt);
            }
            else if (evt.getSource() == btnMenOkApartner) {
                BaumMeldungPanel.this.btnMenOkApartnerActionPerformed(evt);
            }
            else if (evt.getSource() == btnMenAbortOrtstermin) {
                BaumMeldungPanel.this.btnMenAbortOrtsterminActionPerformed(evt);
            }
            else if (evt.getSource() == btnMenOkOrtstermin) {
                BaumMeldungPanel.this.btnMenOkOrtsterminActionPerformed(evt);
            }
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getSource() == lstOrtstermine) {
                BaumMeldungPanel.this.lstOrtsterminePropertyChange(evt);
            }
        }

        public void stateChanged(ChangeEvent evt) {
            if (evt.getSource() == chAbgenommen) {
                BaumMeldungPanel.this.chAbgenommenStateChanged(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddApartnerActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddApartnerActionPerformed
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(BaumMeldungPanel.this), dlgAddApartner, true);
    }//GEN-LAST:event_btnAddApartnerActionPerformed

    private void btnMenAbortApartnerActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnMenAbortApartnerActionPerformed
        dlgAddApartner.setVisible(false);
    }//GEN-LAST:event_btnMenAbortApartnerActionPerformed

    private void btnMenOkApartnerActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnMenOkApartnerActionPerformed
        try {
            final Object selItem = cbApartner.getSelectedItem();
            if (selItem instanceof MetaObject) {
                cidsBean = TableUtils.addBeanToCollectionWithMessage(StaticSwingTools.getParentFrame(this),
                    cidsBean,
                    FIELD__APARTNER,
                    ((MetaObject)selItem).getBean());
                //sortListNew(FIELD__APARTNER);
            }
        } catch (Exception ex) {
            LOG.error(ex, ex);
        } finally {
            dlgAddApartner.setVisible(false);
            cidsBean.setArtificialChangeFlag(true);
            parentEditor.getCidsBean().setArtificialChangeFlag(true);
        }
    }//GEN-LAST:event_btnMenOkApartnerActionPerformed

    private void btnRemoveApartnerActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveApartnerActionPerformed
        final Object selection = lstApartner.getSelectedValue();
        if (selection != null) {
            final int answer = JOptionPane.showConfirmDialog(StaticSwingTools.getParentFrame(this),
                    NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_AP_QUESTION),
                    NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_AP_TITLE),
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                try {
                    cidsBean = TableUtils.deleteItemFromList(cidsBean, FIELD__APARTNER, selection, false);
                    cidsBean.setArtificialChangeFlag(true);
                    parentEditor.getCidsBean().setArtificialChangeFlag(true);
                } catch (Exception ex) {
                    final ErrorInfo ei = new ErrorInfo(
                            BUNDLE_AP_ERRORTITLE,
                            BUNDLE_AP_ERRORTEXT,
                            null,
                            null,
                            ex,
                            Level.SEVERE,
                            null);
                    JXErrorPane.showDialog(this, ei);
                }
            }
        }
    }//GEN-LAST:event_btnRemoveApartnerActionPerformed

    private void btnAddNewOrtsterminActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddNewOrtsterminActionPerformed
        if (BaumChildrenLoader.getInstanceEditor().getLoadingCompletedWithoutError()){
            try {
                StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(BaumMeldungPanel.this), dlgAddOrtstermin, true);
            } catch (Exception e) {
                LOG.error("Cannot add new BaumOrtstermin object", e);
            }
        }
    }//GEN-LAST:event_btnAddNewOrtsterminActionPerformed

    private void btnRemoveOrtsterminActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveOrtsterminActionPerformed
        if (BaumChildrenLoader.getInstanceEditor().getLoadingCompletedWithoutError()){
            final Object selectedObject = lstOrtstermine.getSelectedValue();

            if (selectedObject instanceof CidsBean) {
                List<CidsBean> listOrte = BaumChildrenLoader.getInstanceEditor().getMapValueOrt(this.cidsBean.getPrimaryKeyValue());
                if(((CidsBean)selectedObject).getMetaObject().getStatus() == MetaObject.NEW){
                    BaumChildrenLoader.getInstanceEditor().removeOrt(cidsBean.getPrimaryKeyValue(), (CidsBean)selectedObject);
                } else{
                    for(final CidsBean beanOrt:listOrte){
                        if(beanOrt.equals(selectedObject)){
                            try {
                                beanOrt.delete();
                            } catch (Exception ex) {
                                Exceptions.printStackTrace(ex);
                            }
                            break;
                        }
                    }
                    BaumChildrenLoader.getInstanceEditor().getMapOrt().replace(this.cidsBean.getPrimaryKeyValue(), listOrte);
                }
                ((DefaultListModel)lstOrtstermine.getModel()).removeElement(selectedObject);
                if (getActiveBeans(listOrte) > 0) {
                    lstOrtstermine.setSelectedIndex(0);
                }
                cidsBean.setArtificialChangeFlag(true);
                parentEditor.getCidsBean().setArtificialChangeFlag(true);
                /*if (ortsterminBeans != null){
                    //String meldungValue = this.cidsBean.getProperty(FIELD__ID).toString();
                    Boolean deleteSuccess = false;
                    if (isEditor){      
                        deleteSuccess = BaumChildrenLoader.getInstanceEditor().removeOrt(cidsBean.getPrimaryKeyValue(), (CidsBean)selectedObject);
                    } 
                    if (ortsterminBeans != null) {
                        ortsterminBeans.remove((CidsBean)selectedObject);
                        ((DefaultListModel)lstOrtstermine.getModel()).removeElement(selectedObject);
                        deletedOrtsterminBeans.add((CidsBean)selectedObject);
                        parentEditor.getCidsBean().setArtificialChangeFlag(true);
                        if (ortsterminBeans != null && ortsterminBeans.size() > 0) {
                            lstOrtstermine.setSelectedIndex(0);
                        }else{
                            lstOrtstermine.clearSelection();
                        }
                    }
                }*/
            //   }
            }
        }
    }//GEN-LAST:event_btnRemoveOrtsterminActionPerformed

    private void btnMenAbortOrtsterminActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnMenAbortOrtsterminActionPerformed
        dlgAddOrtstermin.setVisible(false);
    }//GEN-LAST:event_btnMenAbortOrtsterminActionPerformed

    private void btnMenOkOrtsterminActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnMenOkOrtsterminActionPerformed
        try {
            //meldungsBean erzeugen und vorbelegen:
            final CidsBean beanOrtstermin = CidsBean.createNewCidsBeanFromTableName(
                "WUNDA_BLAU",
                TABLE__ORT,
                getConnectionContext());

            final java.util.Date selDate = dcOrtstermin.getDate();
            java.util.Calendar cal = Calendar.getInstance();
            cal.setTime(selDate);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            java.sql.Date beanDate = new java.sql.Date(cal.getTime().getTime());

            beanOrtstermin.setProperty(FIELD__DATUM, beanDate);
            beanOrtstermin.setProperty(FIELD__FK_MELDUNG, cidsBean);

            //Meldungen erweitern:
            if (isEditor){
                BaumChildrenLoader.getInstanceEditor().addOrt(cidsBean.getPrimaryKeyValue(), beanOrtstermin);
            }
            ((DefaultListModel)lstOrtstermine.getModel()).addElement(beanOrtstermin);
            changedOrtsterminBeans.add(beanOrtstermin);
            
            lstOrtstermine.setSelectedValue(beanOrtstermin, true);
            cidsBean.setArtificialChangeFlag(true);
            parentEditor.getCidsBean().setArtificialChangeFlag(true);
        } catch (Exception ex) {
            LOG.error(ex, ex);
        } finally {
            dlgAddOrtstermin.setVisible(false);
        }
    }//GEN-LAST:event_btnMenOkOrtsterminActionPerformed

    private void btnAddNewSchadenActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddNewSchadenActionPerformed
        if (BaumChildrenLoader.getInstanceEditor().getLoadingCompletedWithoutError()){
            try{
            //schadenBean erzeugen und vorbelegen:
                final CidsBean beanSchaden = CidsBean.createNewCidsBeanFromTableName(
                    "WUNDA_BLAU",
                    TABLE__SCHADEN,
                    getConnectionContext());
                beanSchaden.setProperty(FIELD__FK_MELDUNG, cidsBean);
                beanSchaden.setProperty(FIELD__SCHADEN_ABGESTORBEN, false);
                beanSchaden.setProperty(FIELD__SCHADEN_BAU, false);
                beanSchaden.setProperty(FIELD__SCHADEN_BERATUNG, false);
                beanSchaden.setProperty(FIELD__SCHADEN_EINGANG, false);
                beanSchaden.setProperty(FIELD__SCHADEN_FAELLUNG, false);
                beanSchaden.setProperty(FIELD__SCHADEN_GUTACHTEN, false);
                beanSchaden.setProperty(FIELD__SCHADEN_KRONE, false);
                beanSchaden.setProperty(FIELD__SCHADEN_OHNE, false);
                beanSchaden.setProperty(FIELD__SCHADEN_PRIVAT, false);
                beanSchaden.setProperty(FIELD__SCHADEN_STAMM, false);
                beanSchaden.setProperty(FIELD__SCHADEN_STURM, false);
                beanSchaden.setProperty(FIELD__SCHADEN_WURZEL, false);

                //CidsBean beanSchadenPersist = beanSchaden.persist(connectionContext);
                //fuellen fuer evtl Loeschen
                //noSaveToDeleteBeansSchaden.add(beanSchadenPersist);
                
                beanSchaden.setProperty(FIELD__ID, getCounterSchaden());
                setCounterSchaden(getCounterSchaden()-1);
                
                //schaden erweitern:
                /*if (schadenBeans != null){
                    schadenBeans.add(beanSchadenPersist);
                } else{
                    List<CidsBean> tempList = new ArrayList<>();
                    tempList.add(beanSchadenPersist);
                    schadenBeanMap.replace(cidsBean.getProperty(FIELD__ID).toString(), tempList);
                    schadenBeans = tempList;
                }*/
                if (isEditor){
                    BaumChildrenLoader.getInstanceEditor().addSchaden(cidsBean.getPrimaryKeyValue(), beanSchaden);//beanSchadenPersist
                }
                ((DefaultListModel)lstSchaeden.getModel()).addElement(beanSchaden);//beanSchadenPersist
                //changedSchadenBeans.add(beanSchadenPersist);

                //Refresh:
                lstSchaeden.setSelectedValue(beanSchaden, true);//beanSchadenPersist
                cidsBean.setArtificialChangeFlag(true);
                parentEditor.getCidsBean().setArtificialChangeFlag(true);
            } catch (Exception e) {
                LOG.error("Cannot add new BaumSchaden object", e);
            }
        }
    }//GEN-LAST:event_btnAddNewSchadenActionPerformed

    private void btnRemoveSchadenActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveSchadenActionPerformed
        if (BaumChildrenLoader.getInstanceEditor().getLoadingCompletedWithoutError()){
            final Object selectedObject = lstSchaeden.getSelectedValue();

            if (selectedObject instanceof CidsBean) {
                final Integer idSchaden = ((CidsBean) selectedObject).getPrimaryKeyValue();
                if(BaumChildrenLoader.getInstanceEditor().getMapValueFest(idSchaden)== null && BaumChildrenLoader.getInstanceEditor().getMapValueErsatz(idSchaden)== null){
                
                    List<CidsBean> listSchaeden = BaumChildrenLoader.getInstanceEditor().getMapValueSchaden(this.cidsBean.getPrimaryKeyValue());
                    if(((CidsBean)selectedObject).getMetaObject().getStatus() == MetaObject.NEW){
                        BaumChildrenLoader.getInstanceEditor().removeSchaden(cidsBean.getPrimaryKeyValue(), (CidsBean)selectedObject);
                    } else{
                        for(final CidsBean beanSchaden:listSchaeden){
                            if(beanSchaden.equals(selectedObject)){
                                try {
                                    beanSchaden.delete();
                                } catch (Exception ex) {
                                    Exceptions.printStackTrace(ex);
                                }
                                break;
                            }
                        }
                        BaumChildrenLoader.getInstanceEditor().getMapMeldung().replace(this.cidsBean.getPrimaryKeyValue(), listSchaeden);
                    }
                    ((DefaultListModel)lstSchaeden.getModel()).removeElement(selectedObject);
                    if (getActiveBeans(listSchaeden) > 0) {
                        lstSchaeden.setSelectedIndex(0);
                    }
                    cidsBean.setArtificialChangeFlag(true);
                }else {
                    //Meldung, Schaden hat Unterobjekte
                    JOptionPane.showMessageDialog(
                            StaticSwingTools.getParentFrame(this),
                            NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_DEL_SCHADEN),
                            NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_PANE_TITLE_SCHADEN),
                            JOptionPane.WARNING_MESSAGE);

                }
                /*
                if (schadenBeans != null) {
                    //Loeschen, nur wenn der Schaden keine Unterobjekte hat
                    if (baumSchadenPanel.getErsatzBeans().isEmpty() && baumSchadenPanel.getFestBeans().isEmpty()){
                        schadenBeans.remove((CidsBean)selectedObject);
                        ((DefaultListModel)lstSchaeden.getModel()).removeElement(selectedObject);
                        deletedSchadenBeans.add((CidsBean)selectedObject);
                        parentEditor.getCidsBean().setArtificialChangeFlag(true);
                        Boolean deleteSuccess = false;
                        if (isEditor){      
                            deleteSuccess = BaumChildrenLoader.getInstanceEditor().removeSchaden(cidsBean.getPrimaryKeyValue(), (CidsBean)selectedObject);
                        }
                        if (schadenBeans != null && schadenBeans.size() > 0) {
                            lstSchaeden.setSelectedIndex(0);
                        }else{
                            lstSchaeden.clearSelection();
                        }
                    } else {
                        //Meldung, Schaden hat Unterobjekte
                        JOptionPane.showMessageDialog(
                                StaticSwingTools.getParentFrame(this),
                                NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_DEL_SCHADEN),
                                NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_PANE_TITLE_SCHADEN),
                                JOptionPane.WARNING_MESSAGE);
                    }
                }   */ 
            }
        }
    }//GEN-LAST:event_btnRemoveSchadenActionPerformed

    private void lstOrtsterminePropertyChange(PropertyChangeEvent evt) {//GEN-FIRST:event_lstOrtsterminePropertyChange
        //baumOrtsterminPanel.repaint();
    }//GEN-LAST:event_lstOrtsterminePropertyChange

    private void btnApartnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApartnerActionPerformed
        final JDialog dialog = new JDialog((Frame)null,
            "Ansprechpartnerinformationen",
            true);
        final Collection<MetaObjectNode> mons = new ArrayList<>();
        
        
        final Object selection = lstApartner.getSelectedValue();
        if (selection != null){
            if (selection instanceof CidsBean) {
                final CidsBean selectedBean = (CidsBean)selection;
                final MetaObjectNode metaObjectNode = new MetaObjectNode(selectedBean);
                mons.add(metaObjectNode);
            }
            dialog.setContentPane(new DescriptionPaneDialogWrapperPanel(mons));
            dialog.setSize(1200, 800);
            StaticSwingTools.showDialog(this, dialog, true);
        } else {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_PANE_PREFIX_SELECTION)
                        + NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_PANE_SELECTION)
                        + NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_PANE_SUFFIX_SELECTION),
                NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_PANE_TITLE_SELECTION),
                JOptionPane.WARNING_MESSAGE);
        }
        
    }//GEN-LAST:event_btnApartnerActionPerformed

    private void chAbgenommenStateChanged(ChangeEvent evt) {//GEN-FIRST:event_chAbgenommenStateChanged
        isAbgenommen();
    }//GEN-LAST:event_chAbgenommenStateChanged

    //~ Instance fields --------------------------------------------------------
    private final boolean isEditor;
    public final BaumGebietEditor parentEditor;
    
    private final PropertyChangeListener changeListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                if ((parentEditor != null) && (parentEditor.getCidsBean() != null)) {
                    parentEditor.getCidsBean().setArtificialChangeFlag(true);
                }
            }
        };
    
    /*private final LoaderListener loaderListener = new LoaderListener(){
        
    };*/
    private final ConnectionContext connectionContext;
    private CidsBean cidsBean;
    /*
    private SwingWorker worker_ortstermin;
    private SwingWorker worker_schaden;
    */
    //private final BaumOrtsterminListLightweightSearch ortsterminSearch;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    BaumOrtsterminPanel baumOrtsterminPanel;
    BaumSchadenPanel baumSchadenPanel;
    JButton btnAddApartner;
    JButton btnAddNewOrtstermin;
    JButton btnAddNewSchaden;
    JButton btnApartner;
    JButton btnMenAbortApartner;
    JButton btnMenAbortOrtstermin;
    JButton btnMenOkApartner;
    JButton btnMenOkOrtstermin;
    JButton btnRemoveApartner;
    JButton btnRemoveOrtstermin;
    JButton btnRemoveSchaden;
    JComboBox cbApartner;
    JCheckBox chAbgenommen;
    DefaultBindableDateChooser dcOrtstermin;
    JDialog dlgAddApartner;
    JDialog dlgAddOrtstermin;
    Box.Filler filler1;
    JPanel jPanelAllgemein;
    JPanel jPanelOrtstermine;
    JPanel jPanelSchaeden;
    JTabbedPane jTabbedPane;
    JLabel lblAbgenommen;
    JLabel lblApartner;
    JLabel lblAuswaehlenApartner;
    JLabel lblAuswaehlenOrtstermin;
    JLabel lblBemerkung;
    JLabel lblLadenOrt;
    JLabel lblLadenSchaden;
    JList lstApartner;
    JList lstOrtstermine;
    JList lstSchaeden;
    JPanel panAddApartner;
    JPanel panAddOrtstermin;
    JPanel panApartner;
    JPanel panButtonsApartner;
    JPanel panControlsNewOrtstermine;
    JPanel panControlsNewSchaden;
    JPanel panFillerUnten3;
    JPanel panFillerUnten4;
    JPanel panFillerUnten5;
    JPanel panInfo;
    JPanel panMenButtonsApartner;
    JPanel panMenButtonsOrtstermin;
    JPanel panOrtstermin;
    JPanel panOrtstermineMain;
    JPanel panSchaden;
    JPanel panSchaedenMain;
    JPanel pnlCard1;
    JScrollPane scpApartner;
    JScrollPane scpBemerkung;
    JScrollPane scpLaufendeOrtstermine;
    JScrollPane scpLaufendeSchaeden;
    JTextArea taBemerkung;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BaumMeldungPanel object.
     */
    public BaumMeldungPanel() {
        this(null,false, ConnectionContext.createDeprecated());
    }

    
    /**
     * Creates new form BaumMeldungPanel.
     *
     * @param parentEditor
     * @param  editable  DOCUMENT ME!
     */
  /*  public BaumMeldungPanel(final BaumGebietEditor parentEditor, final boolean editable) {
        this.isEditor = editable;
        initComponents();
        this.connectionContext = null;
        this.parentEditor = parentEditor;
    }*/
 
    /**
     * Creates new form BaumMeldungPanel.
     *
     * @param parentEditor
     * @param  editable             DOCUMENT ME!
     * @param  connectionContext    DOCUMENT ME!
     */
    public BaumMeldungPanel(final BaumGebietEditor parentEditor, final boolean editable,
            final ConnectionContext connectionContext) {
        this.isEditor = editable;
        this.connectionContext = connectionContext;
        initComponents();
        this.parentEditor = parentEditor;
        if (isEditor){
            loadChildrenListenerEditor = new LoaderListener();
            BaumChildrenLoader.getInstanceEditor().addListener(loadChildrenListenerEditor);
        } else{
            loadChildrenListenerRenderer = new LoaderListener();
            BaumChildrenLoader.getInstanceRenderer().addListener(loadChildrenListenerRenderer);
        }
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }
    
    public void clearBeans(final List<CidsBean> toClearList){
        if (toClearList != null && toClearList.size() > 0) {
            toClearList.clear();
        }
    }
    
    
    private Integer getActiveBeans(final List<CidsBean> cbList){
        Integer anzahl = 0;
        for(final CidsBean bean:cbList){
            if(bean.getMetaObject().getStatus() != MetaObject.TO_DELETE){
                anzahl += 1;
            }
        }        
        return anzahl;
    }
    
    @Override
    public void dispose() {
        bindingGroup.unbind();
        cidsBean = null;
        dlgAddApartner.dispose();
        dlgAddOrtstermin.dispose();
        //clearBeans(deletedOrtsterminBeans);
        //clearBeans(deletedSchadenBeans);
        //clearBeans(schadenBeans);
        //clearBeans(ortsterminBeans);
        //clearBeans(changedSchadenBeans);
        //clearBeans(changedOrtsterminBeans);
        //clearBeans(noSaveToDeleteBeansSchaden);
        baumOrtsterminPanel.dispose();
        baumSchadenPanel.dispose();
        if(isEditor){
            BaumChildrenLoader.getInstanceEditor().removeListener(loadChildrenListenerEditor);
        } else {
            BaumChildrenLoader.getInstanceRenderer().removeListener(loadChildrenListenerRenderer);
        }
        baumSchadenPanel.dispose();
        //((DefaultListModel<CidsBean>)lstOrtstermine.getModel()).clear();
        setCounterSchaden(-1);
    }

    @Override
    public CidsBean getCidsBean() {
        return this.cidsBean;
    }

    @Override
    public void setCidsBean(CidsBean cidsBean) {
        if (!(Objects.equals(this.cidsBean, cidsBean))){
            if (isEditor && (this.cidsBean != null)) {
                this.cidsBean.removePropertyChangeListener(changeListener);
            }
            bindingGroup.unbind();
            this.cidsBean = cidsBean;
            if (isEditor && (this.cidsBean != null)) {
                    cidsBean.addPropertyChangeListener(changeListener);
            }
            if (this.cidsBean != null){
           
                //lstOrtstermine.setModel(MODEL_LOAD);          
                zeigeKinderOrt();
                zeigeKinderSchaden();
                /*if (schadenBeanMap.containsKey(cidsBean.getProperty(FIELD__ID).toString())){
                    setSchadenBeans(schadenBeanMap.get(cidsBean.getProperty(FIELD__ID).toString()));
                } else {
                    try{
                        searchSchaden.setParentId(this.cidsBean.getPrimaryKeyValue());
                        final Collection<MetaObjectNode> mons = SessionManager.getProxy().customServerSearch(
                                SessionManager.getSession().getUser(),
                                searchSchaden,
                                getConnectionContext());
                        final List<CidsBean> beansSchaden = new ArrayList<>();
                        if (!mons.isEmpty()) {
                            for (final MetaObjectNode mon : mons) {
                                beansSchaden.add(SessionManager.getProxy().getMetaObject(
                                        mon.getObjectId(),
                                        mon.getClassId(),
                                        "WUNDA_BLAU",
                                        getConnectionContext()).getBean());
                            }
                        }
                        schadenBeanMap.put(cidsBean.getProperty(FIELD__ID).toString(), beansSchaden);
                        setSchadenBeans(beansSchaden);
                    }   catch (ConnectionException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }*/
            }else {
                setOrtsterminBeans(null);
                setSchadenBeans(null);
            }
            bindingGroup.bind();
            isAbgenommen();
            dlgAddOrtstermin.pack();
            dlgAddOrtstermin.getRootPane().setDefaultButton(btnMenOkOrtstermin);
            dlgAddApartner.pack();
            dlgAddApartner.getRootPane().setDefaultButton(btnMenOkApartner);
            if (cidsBean != null && cidsBean.getMetaObject().getStatus() == MetaObject.NEW){
                BaumChildrenLoader.getInstanceEditor().setLoadingCompletedWithoutError(true);
                allowAddRemove();
            }
        }
    }
    
    public void editorClosed(final EditorClosedEvent ece) {
        if(EditorSaveListener.EditorSaveStatus.CANCELED == ece.getStatus()){
             baumOrtsterminPanel.editorClosed(ece);
             baumSchadenPanel.editorClosed(ece);
        }
    }
    
    
    public boolean prepareForSave(final CidsBean saveBean) {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();
        boolean noErrorOrt = true;
        boolean noErrorSchaden = true;
        final List<CidsBean> listOrt = BaumChildrenLoader.getInstanceEditor().getMapValueErsatz(saveBean.getPrimaryKeyValue());
        final List<CidsBean> listSchaden = BaumChildrenLoader.getInstanceEditor().getMapValueFest(saveBean.getPrimaryKeyValue());
        
        if (listOrt != null && !(listOrt.isEmpty())){
            for (final CidsBean ortBean : listOrt) {
                try {
                    noErrorOrt = baumOrtsterminPanel.prepareForSave(ortBean);
                    if(!noErrorOrt) {
                        break;
                    }
                } catch (final Exception ex) {
                    noErrorOrt = false;
                    LOG.error(ex, ex);
                }
            }
        }
        if (listSchaden != null && !(listSchaden.isEmpty())){
            for (final CidsBean schadenBean : listSchaden) {
                try {
                    noErrorSchaden = baumSchadenPanel.prepareForSave(schadenBean);
                    if(!noErrorSchaden) {
                        break;
                    }
                } catch (final Exception ex) {
                    noErrorSchaden = false;
                    LOG.error(ex, ex);
                }
            }
        }
        // datum vorhanden
        try {
            if (saveBean.getProperty(FIELD__DATUM)== null) {
                LOG.warn("No name specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_NODATE));
            } 
        } catch (final MissingResourceException ex) {
            LOG.warn("Datum not given.", ex);
            save = false;
        }

        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_PANE_TITLE),
                JOptionPane.WARNING_MESSAGE);

            return false;
        }
        return save && noErrorOrt && noErrorSchaden;
    }
    
    private void isAbgenommen() {
        if (chAbgenommen.isSelected()) {
            lblAbgenommen.setForeground(Color.black);
        } else {
            lblAbgenommen.setForeground(Color.red);
        }
    }
    
    public void prepareSchaden(){
        if (schadenBeans != null && schadenBeans.size() > 0) {
            lstSchaeden.setSelectedIndex(0);
        }
        lstSchaeden.setCellRenderer(new DefaultListCellRenderer() {

                @Override
                public Component getListCellRendererComponent(final JList list,
                        final Object value,
                        final int index,
                        final boolean isSelected,
                        final boolean cellHasFocus) {
                    Object newValue = value;

                    if (value instanceof CidsBean) {
                        final CidsBean bean = (CidsBean)value;
                        newValue = bean.getProperty(FIELD__ID);

                        if (newValue == null) {
                            newValue = "unbenannt";
                        }
                    }
                    final Component compoId = super.getListCellRendererComponent(list, newValue, index, isSelected, cellHasFocus);
                    compoId.setForeground(new Color(255,140,0));
                    return compoId;
                }
            });
        if (schadenBeans != null && schadenBeans.size() > 0) {
            //Collections.sort((List)schadenBeans, ID_COMPARATOR);
        }
    }
    
    public void allowAddRemove(){
        if (isEditor){
           if (BaumChildrenLoader.getInstanceEditor().getLoadingCompletedWithoutError()){
                btnAddNewOrtstermin.setEnabled(true);
                btnAddNewSchaden.setEnabled(true);
                btnRemoveOrtstermin.setEnabled(true);
                btnRemoveSchaden.setEnabled(true);
                lblLadenOrt.setVisible(false);
                lblLadenSchaden.setVisible(false);
            }
        }
    }
    
    public void prepareOrtstermin(){
        
        if (ortsterminBeans != null && ortsterminBeans.size() > 0) {
            lstOrtstermine.setSelectedIndex(0);
        }
        lstOrtstermine.setCellRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(final JList list,
                    final Object value,
                    final int index,
                    final boolean isSelected,
                    final boolean cellHasFocus) {
                Object newValue = value;

                if (value instanceof CidsBean) {
                    final CidsBean bean = (CidsBean)value;
                    newValue = bean.getProperty(FIELD__DATUM);

                    if (newValue == null) {
                        newValue = "unbenannt";
                    }
                }
                final Component compoDatum = super.getListCellRendererComponent(list, newValue, index, isSelected, cellHasFocus);
                compoDatum.setForeground(new Color(9,68,9));
                return compoDatum;
            }
        }); 
        if (ortsterminBeans != null && ortsterminBeans.size() > 0) {
            //Collections.sort((List)ortsterminBeans, DATE_COMPARATOR);
        }
    }
    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeans  DOCUMENT ME!
     */
    public void setOrtsterminBeans(final List<CidsBean> cidsBeans) {
        try {
            baumOrtsterminPanel.setCidsBean(null);
            ((DefaultListModel)lstOrtstermine.getModel()).clear();
            if(cidsBeans != null){
                //cidsBeans.sort(DATE_COMPARATOR);
                for(final Object bean:cidsBeans){
                    if (bean instanceof CidsBean && ((CidsBean)bean).getMetaObject().getStatus()!= MetaObject.TO_DELETE){
                        ((DefaultListModel)lstOrtstermine.getModel()).addElement(bean);
                    }
                }
            }
            this.ortsterminBeans = cidsBeans;
            prepareOrtstermin();
        } catch (final Exception ex) {
                Exceptions.printStackTrace(ex);
                LOG.warn("ort list not cleared.", ex);
        }
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> getOrtsterminBeans() {
        return ortsterminBeans;
    }
    public List<CidsBean> getChangedOrtsterminBeans(){
        return changedOrtsterminBeans;
    }
    
    public void setChangedOrtsterminBeans(CidsBean ortsterminBean){
        if(this.changedOrtsterminBeans != null){
            int ortId = (Integer)ortsterminBean.getProperty(FIELD__ID);
            for(final CidsBean bean:changedOrtsterminBeans){
                if (ortId == (Integer)bean.getProperty(FIELD__ID)){
                    changedOrtsterminBeans.remove(bean);
                    break;
                }
            }
        } 
        this.changedOrtsterminBeans.add(ortsterminBean);
    }
    
    
    public List<CidsBean> getChangedSchadenBeans(){
        return changedOrtsterminBeans;
    }
    
    public void setChangedSchadenBeans(CidsBean schadenBean){
        if(this.changedSchadenBeans != null){
            int schadenId = (Integer)schadenBean.getProperty(FIELD__ID);
            for(final CidsBean bean:changedSchadenBeans){
                if (schadenId == (Integer)bean.getProperty(FIELD__ID)){
                    changedSchadenBeans.remove(bean);
                    break;
                }
            }
        } 
        this.changedSchadenBeans.add(schadenBean);
    }
    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeans  DOCUMENT ME!
     */
    public void setSchadenBeans(final List<CidsBean> cidsBeans) {
        baumSchadenPanel.setCidsBean(null);
        ((DefaultListModel)lstSchaeden.getModel()).clear();
        //this.schadenBeans.clear();
        if(cidsBeans != null){
            //cidsBeans.sort(ID_COMPARATOR);
            for(final Object bean:cidsBeans){
                if (bean instanceof CidsBean && ((CidsBean)bean).getMetaObject().getStatus()!= MetaObject.TO_DELETE){
                    ((DefaultListModel)lstSchaeden.getModel()).addElement(bean);
                }
            }
        }
        this.schadenBeans = cidsBeans;
        //baumSchadenPanel.setCidsBean(null);
        prepareSchaden();
    }
    
    private void zeigeKinderOrt(){
        //lstOrtstermine.setModel(new DefaultListModel());
        if (isEditor){      
            setOrtsterminBeans(BaumChildrenLoader.getInstanceEditor().getMapValueOrt(cidsBean.getPrimaryKeyValue()));
        } else {
            setOrtsterminBeans(BaumChildrenLoader.getInstanceRenderer().getMapValueOrt(cidsBean.getPrimaryKeyValue()));
        }
    }
    
    private void zeigeKinderSchaden(){
        if (isEditor){      
            setSchadenBeans(BaumChildrenLoader.getInstanceEditor().getMapValueSchaden(cidsBean.getPrimaryKeyValue()));
        } else {
            setSchadenBeans(BaumChildrenLoader.getInstanceRenderer().getMapValueSchaden(cidsBean.getPrimaryKeyValue()));
        }
    }
    
    private void zeigeErrorOrt(){
        scpLaufendeOrtstermine.setViewportView(new JList<>(MODEL_ERROR));
        JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_ERROR_ORT),
                NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_PANE_TITLE_ERROR_ORT),
                JOptionPane.WARNING_MESSAGE);
    }
    
    private void zeigeErrorSchaden(){
        scpLaufendeSchaeden.setViewportView(new JList<>(MODEL_ERROR));
        JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_ERROR_SCHADEN),
                NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_PANE_TITLE_ERROR_SCHADEN),
                JOptionPane.WARNING_MESSAGE);
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> getSchadenBeans() {
        return schadenBeans;
    }
    
    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public class DescriptionPaneDialogWrapperPanel extends JPanel
            implements BaumAnsprechpartnerRenderer.BaumAnsprechpartnerDescriptionPaneParent {

        //~ Instance fields ----------------------------------------------------

        private final DescriptionPaneFS pane = new DescriptionPaneFS();

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new TestBlubbPanel object.
         *
         * @param  mons  DOCUMENT ME!
         */
        private DescriptionPaneDialogWrapperPanel(final Collection<MetaObjectNode> mons) {
            setLayout(new BorderLayout());
            add(pane, BorderLayout.CENTER);
            pane.gotoMetaObjectNodes(mons.toArray(new MetaObjectNode[0]));
        }
    }
     
    
            
    class LoaderListener implements BaumChildrenLoader.Listener{

        @Override
        public void loadingCompleteSchaden(Integer idMeldung) {
            if(Objects.equals(cidsBean.getPrimaryKeyValue(), idMeldung)){
                lblLadenSchaden.setVisible(false);
                zeigeKinderSchaden();
            }
        }

        @Override
        public void loadingCompleteOrt(Integer idMeldung) {
            if(Objects.equals(cidsBean.getPrimaryKeyValue(), idMeldung)){
                lblLadenOrt.setVisible(false);
                zeigeKinderOrt();
            }
        }
        
        @Override
        public void loadingErrorOrt(Integer idMeldung) {
            zeigeErrorOrt();
        }

        @Override
        public void loadingErrorSchaden(Integer idMeldung) {
             zeigeErrorSchaden();
        }

        @Override
        public void loadingComplete() {
            allowAddRemove();
        }

        @Override
        public void loadingCompleteFest(Integer idMeldung) {
            
        }

        @Override
        public void loadingCompleteErsatz(Integer idMeldung) {
            
        }

        @Override
        public void loadingErrorFest(Integer idMeldung) {
            
        }

        @Override
        public void loadingErrorErsatz(Integer idMeldung) {
           
        }

        @Override
        public void loadingCompleteMeldung() {
        }
        
    }
   }
