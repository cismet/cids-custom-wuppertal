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

import Sirius.server.middleware.types.MetaObject;
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
import javax.swing.SwingWorker;

import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;
import de.cismet.cids.dynamics.Disposable;
import de.cismet.cids.editors.DefaultBindableDateChooser;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;
import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.SemiRoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
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
     
    private static enum WhichCase {

        //~ Enum constants -----------------------------------------------------

        ORTSTERMIN, SCHADEN
    }
    
    private List<CidsBean> ortsterminBeans = new ArrayList<>();
    private List<CidsBean> changedOrtsterminBeans = new ArrayList<>();
    private List<CidsBean> schadenBeans = new ArrayList<>();
    private List<CidsBean> changedSchadenBeans = new ArrayList<>();
    private final List<CidsBean> deletedOrtsterminBeans = new ArrayList<>();
    private final List<CidsBean> deletedSchadenBeans = new ArrayList<>();
    private static final Logger LOG = Logger.getLogger(BaumMeldungPanel.class);
    
    
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
    
    
    
    public static final String BUNDLE_AP_QUESTION = "BaumMeldungPanel.btnRemoveAnsprechpartnerActionPerformed().question";
    public static final String BUNDLE_AP_TITLE = "BaumMeldungPanel.btnRemoveAnsprechpartnerActionPerformed().title";
    public static final String BUNDLE_AP_ERRORTITLE = "BaumMeldungPanel.btnRemoveAnsprechpartnerActionPerformed().errortitle";
    public static final String BUNDLE_AP_ERRORTEXT = "BaumMeldungPanel.btnRemoveAnsprechpartnerActionPerformed().errortext";
    public static final String BUNDLE_PANE_PREFIX =
        "BaumMeldungPanel.prepareForSave().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX =
        "BaumMeldungPanel.prepareForSave().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "BaumMeldungPanel.prepareForSave().JOptionPane.title";

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
        lblApartner = new JLabel();
        panApartner = new JPanel();
        scpApartner = new JScrollPane();
        lstApartner = new JList();
        panButtonsApartner = new JPanel();
        btnAddApartner = new JButton();
        btnRemoveApartner = new JButton();
        filler1 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
        lblBemerkung = new JLabel();
        scpBemerkung = new JScrollPane();
        taBemerkung = new JTextArea();
        panFillerUnten5 = new JPanel();
        jPanelOrtstermine = new JPanel();
        panFillerUnten3 = new JPanel();
        panOrtstermin = new JPanel();
        rpOrtsterminliste = new RoundedPanel();
        scpLaufendeOrtstermine = new JScrollPane();
        lstOrtstermine = new JList();
        semiRoundedPanelOrt = new SemiRoundedPanel();
        lblOrtstermine = new JLabel();
        jPanel8 = new JPanel();
        panControlsNewOrtstermine = new JPanel();
        btnAddNewOrtstermin = new JButton();
        btnRemoveOrtstermin = new JButton();
        rpOrtstermininfo = new RoundedPanel();
        semiRoundedPanel5 = new SemiRoundedPanel();
        lblOrtstermin = new JLabel();
        panOrtstermineMain = new JPanel();
        baumOrtsterminPanel = baumOrtsterminPanel = new BaumOrtsterminPanel(this,null, true);
        jPanelSchaeden = new JPanel();
        panFillerUnten4 = new JPanel();
        panSchaden = new JPanel();
        rpSchadenliste = new RoundedPanel();
        scpLaufendeSchaeden = new JScrollPane();
        lstSchaeden = new JList();
        semiRoundedPanelSchaden = new SemiRoundedPanel();
        lblSchaeden = new JLabel();
        jPanel9 = new JPanel();
        panControlsNewSchaden = new JPanel();
        btnAddNewSchaden = new JButton();
        btnRemoveSchaden = new JButton();
        rpSchadeninfo = new RoundedPanel();
        semiRoundedPanel6 = new SemiRoundedPanel();
        lblSchaden = new JLabel();
        panSchaedenMain = new JPanel();
        baumSchadenPanel = baumSchadenPanel = new BaumSchadenPanel(this, null, true);

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

        lblApartner.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblApartner, "Ansprechpartner:");
        lblApartner.setName("lblApartner"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panInfo.add(lblApartner, gridBagConstraints);

        panApartner.setName("panApartner"); // NOI18N
        panApartner.setOpaque(false);
        panApartner.setLayout(new GridBagLayout());

        scpApartner.setMinimumSize(new Dimension(258, 66));
        scpApartner.setName("scpApartner"); // NOI18N

        lstApartner.setFont(new Font("Dialog", 0, 12)); // NOI18N
        lstApartner.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstApartner.setName("lstApartner"); // NOI18N
        lstApartner.setVisibleRowCount(3);

        ELProperty eLProperty = ELProperty.create("${cidsBean.arr_ansprechpartner}");
        JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, lstApartner);
        bindingGroup.addBinding(jListBinding);

        scpApartner.setViewportView(lstApartner);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panApartner.add(scpApartner, gridBagConstraints);

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
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(10, 2, 2, 2);
        panApartner.add(panButtonsApartner, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
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
        gridBagConstraints.gridy = 3;
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

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.bemerkung}"), taBemerkung, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpBemerkung.setViewportView(taBemerkung);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
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
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        panInfo.add(panFillerUnten5, gridBagConstraints);

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

        panOrtstermin.setMinimumSize(new Dimension(297, 230));
        panOrtstermin.setName("panOrtstermin"); // NOI18N
        panOrtstermin.setOpaque(false);
        panOrtstermin.setPreferredSize(new Dimension(337, 230));
        panOrtstermin.setLayout(new GridBagLayout());

        rpOrtsterminliste.setMinimumSize(new Dimension(100, 202));
        rpOrtsterminliste.setName("rpOrtsterminliste"); // NOI18N
        rpOrtsterminliste.setPreferredSize(new Dimension(100, 202));
        rpOrtsterminliste.setLayout(new GridBagLayout());

        scpLaufendeOrtstermine.setName("scpLaufendeOrtstermine"); // NOI18N

        lstOrtstermine.setModel(new DefaultListModel());
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
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        rpOrtsterminliste.add(scpLaufendeOrtstermine, gridBagConstraints);

        semiRoundedPanelOrt.setBackground(Color.darkGray);
        semiRoundedPanelOrt.setMinimumSize(new Dimension(100, 25));
        semiRoundedPanelOrt.setName("semiRoundedPanelOrt"); // NOI18N
        semiRoundedPanelOrt.setPreferredSize(new Dimension(100, 25));
        semiRoundedPanelOrt.setLayout(new GridBagLayout());

        lblOrtstermine.setForeground(new Color(255, 255, 255));
        lblOrtstermine.setText("Ortstermine");
        lblOrtstermine.setName("lblOrtstermine"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        semiRoundedPanelOrt.add(lblOrtstermine, gridBagConstraints);

        jPanel8.setName("jPanel8"); // NOI18N
        jPanel8.setOpaque(false);
        jPanel8.setPreferredSize(new Dimension(1, 1));

        GroupLayout jPanel8Layout = new GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(jPanel8Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(jPanel8Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        semiRoundedPanelOrt.add(jPanel8, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpOrtsterminliste.add(semiRoundedPanelOrt, gridBagConstraints);

        panControlsNewOrtstermine.setName("panControlsNewOrtstermine"); // NOI18N
        panControlsNewOrtstermine.setOpaque(false);
        panControlsNewOrtstermine.setLayout(new GridBagLayout());

        btnAddNewOrtstermin.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
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
        rpOrtsterminliste.add(panControlsNewOrtstermine, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 0, 5);
        panOrtstermin.add(rpOrtsterminliste, gridBagConstraints);

        rpOrtstermininfo.setName("rpOrtstermininfo"); // NOI18N
        rpOrtstermininfo.setLayout(new GridBagLayout());

        semiRoundedPanel5.setBackground(Color.darkGray);
        semiRoundedPanel5.setName("semiRoundedPanel5"); // NOI18N
        semiRoundedPanel5.setLayout(new GridBagLayout());

        lblOrtstermin.setForeground(new Color(255, 255, 255));
        lblOrtstermin.setText("Ortstermin");
        lblOrtstermin.setName("lblOrtstermin"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        semiRoundedPanel5.add(lblOrtstermin, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpOrtstermininfo.add(semiRoundedPanel5, gridBagConstraints);

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
        panOrtstermineMain.add(baumOrtsterminPanel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        rpOrtstermininfo.add(panOrtstermineMain, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 8.0;
        gridBagConstraints.insets = new Insets(0, 5, 0, 0);
        panOrtstermin.add(rpOrtstermininfo, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        jPanelOrtstermine.add(panOrtstermin, gridBagConstraints);

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

        rpSchadenliste.setName("rpSchadenliste"); // NOI18N
        rpSchadenliste.setLayout(new GridBagLayout());

        scpLaufendeSchaeden.setName("scpLaufendeSchaeden"); // NOI18N

        lstSchaeden.setModel(new DefaultListModel());
        lstSchaeden.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstSchaeden.setName("lstSchaeden"); // NOI18N
        lstSchaeden.setVisibleRowCount(2);
        scpLaufendeSchaeden.setViewportView(lstSchaeden);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        rpSchadenliste.add(scpLaufendeSchaeden, gridBagConstraints);

        semiRoundedPanelSchaden.setBackground(Color.darkGray);
        semiRoundedPanelSchaden.setMinimumSize(new Dimension(60, 25));
        semiRoundedPanelSchaden.setName("semiRoundedPanelSchaden"); // NOI18N
        semiRoundedPanelSchaden.setLayout(new GridBagLayout());

        lblSchaeden.setForeground(new Color(255, 255, 255));
        lblSchaeden.setText("Schäden");
        lblSchaeden.setName("lblSchaeden"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        semiRoundedPanelSchaden.add(lblSchaeden, gridBagConstraints);

        jPanel9.setName("jPanel9"); // NOI18N
        jPanel9.setOpaque(false);
        jPanel9.setPreferredSize(new Dimension(1, 1));

        GroupLayout jPanel9Layout = new GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(jPanel9Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(jPanel9Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        semiRoundedPanelSchaden.add(jPanel9, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpSchadenliste.add(semiRoundedPanelSchaden, gridBagConstraints);

        panControlsNewSchaden.setName("panControlsNewSchaden"); // NOI18N
        panControlsNewSchaden.setOpaque(false);
        panControlsNewSchaden.setLayout(new GridBagLayout());

        btnAddNewSchaden.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
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
        btnRemoveSchaden.setMaximumSize(new Dimension(39, 20));
        btnRemoveSchaden.setMinimumSize(new Dimension(39, 20));
        btnRemoveSchaden.setName("btnRemoveSchaden"); // NOI18N
        btnRemoveSchaden.setPreferredSize(new Dimension(39, 25));
        btnRemoveSchaden.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panControlsNewSchaden.add(btnRemoveSchaden, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 0, 5, 0);
        rpSchadenliste.add(panControlsNewSchaden, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        panSchaden.add(rpSchadenliste, gridBagConstraints);

        rpSchadeninfo.setName("rpSchadeninfo"); // NOI18N
        rpSchadeninfo.setLayout(new GridBagLayout());

        semiRoundedPanel6.setBackground(Color.darkGray);
        semiRoundedPanel6.setName("semiRoundedPanel6"); // NOI18N
        semiRoundedPanel6.setLayout(new GridBagLayout());

        lblSchaden.setForeground(new Color(255, 255, 255));
        lblSchaden.setText("Schaden");
        lblSchaden.setName("lblSchaden"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        semiRoundedPanel6.add(lblSchaden, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpSchadeninfo.add(semiRoundedPanel6, gridBagConstraints);

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
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 0, 0, 0);
        rpSchadeninfo.add(panSchaedenMain, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 8.0;
        panSchaden.add(rpSchadeninfo, gridBagConstraints);

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

    private class FormListener implements ActionListener, PropertyChangeListener {
        FormListener() {}
        public void actionPerformed(ActionEvent evt) {
            if (evt.getSource() == btnAddApartner) {
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
        }
    }//GEN-LAST:event_btnMenOkApartnerActionPerformed

    private void btnRemoveApartnerActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveApartnerActionPerformed
        final Object selection = lstApartner.getSelectedValue();
        if (selection != null) {
            final int answer = JOptionPane.showConfirmDialog(StaticSwingTools.getParentFrame(this),
                    NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_AP_QUESTION),
                    NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_AP_TITLE),
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                try {
                    cidsBean = TableUtils.deleteItemFromList(cidsBean, FIELD__APARTNER, selection, false);
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
        try {
            StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(BaumMeldungPanel.this), dlgAddOrtstermin, true);
        } catch (Exception e) {
            LOG.error("Cannot add new BaumOrtstermin object", e);
        }
    }//GEN-LAST:event_btnAddNewOrtsterminActionPerformed

    private void btnRemoveOrtsterminActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveOrtsterminActionPerformed
        final Object selectedObject = lstOrtstermine.getSelectedValue();

        if (selectedObject instanceof CidsBean) {
            //final List<CidsBean> meldungBeans = cidsBean.getBeanCollectionProperty(FIELD__MELDUNGEN);

            if (ortsterminBeans != null) {
                ortsterminBeans.remove((CidsBean)selectedObject);
                ((DefaultListModel)lstOrtstermine.getModel()).removeElement(selectedObject);
                deletedOrtsterminBeans.add((CidsBean)selectedObject);
                parentEditor.getCidsBean().setArtificialChangeFlag(true);
                //((CustomJListModel)lstMeldungen.getModel()).refresh();
                //lstMeldungen.getSelectionModel().clearSelection();
                if (ortsterminBeans != null && ortsterminBeans.size() > 0) {
                    lstOrtstermine.setSelectedIndex(0);
                }else{
                    lstOrtstermine.clearSelection();
                }
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
                "BAUM_ORTSTERMIN",
                getConnectionContext());

            final java.util.Date selDate = dcOrtstermin.getDate();
            java.util.Calendar cal = Calendar.getInstance();
            cal.setTime(selDate);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            java.sql.Date beanDate = new java.sql.Date(cal.getTime().getTime());

            beanOrtstermin.setProperty("datum", beanDate);
            beanOrtstermin.setProperty(FIELD__FK_MELDUNG, cidsBean);

            //Meldungen erweitern:
            ortsterminBeans.add(beanOrtstermin);
            ((DefaultListModel)lstOrtstermine.getModel()).addElement(beanOrtstermin);
            changedOrtsterminBeans.add(beanOrtstermin);
            //Refresh:

            //bindingGroup.unbind();
            //Collections.sort((List)ortsterminBeans, DATE_COMPARATOR);
            //bindingGroup.bind();
            
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
        try{
        //schadenBean erzeugen und vorbelegen:
            final CidsBean beanSchaden = CidsBean.createNewCidsBeanFromTableName(
                "WUNDA_BLAU",
                "BAUM_SCHADEN",
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

            //schaden erweitern:
            schadenBeans.add(beanSchaden);
            ((DefaultListModel)lstSchaeden.getModel()).addElement(beanSchaden);
            changedSchadenBeans.add(beanSchaden);

            //Refresh:
            lstSchaeden.setSelectedValue(beanSchaden, true);
            cidsBean.setArtificialChangeFlag(true);
            parentEditor.getCidsBean().setArtificialChangeFlag(true);
        } catch (Exception e) {
            LOG.error("Cannot add new BaumSchaden object", e);
        }
    }//GEN-LAST:event_btnAddNewSchadenActionPerformed

    private void btnRemoveSchadenActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveSchadenActionPerformed
        final Object selectedObject = lstSchaeden.getSelectedValue();

        if (selectedObject instanceof CidsBean) {

            if (schadenBeans != null) {
                schadenBeans.remove((CidsBean)selectedObject);
                ((DefaultListModel)lstSchaeden.getModel()).removeElement(selectedObject);
                deletedSchadenBeans.add((CidsBean)selectedObject);
                parentEditor.getCidsBean().setArtificialChangeFlag(true);
                if (schadenBeans != null && schadenBeans.size() > 0) {
                    lstSchaeden.setSelectedIndex(0);
                }else{
                    lstSchaeden.clearSelection();
                }
            }
        }
    }//GEN-LAST:event_btnRemoveSchadenActionPerformed

    private void lstOrtsterminePropertyChange(PropertyChangeEvent evt) {//GEN-FIRST:event_lstOrtsterminePropertyChange
        //baumOrtsterminPanel.repaint();
    }//GEN-LAST:event_lstOrtsterminePropertyChange

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
    
    private final ConnectionContext connectionContext;
    private CidsBean cidsBean;
    
    private SwingWorker worker_ortstermin;
    private SwingWorker worker_schaden;
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    BaumOrtsterminPanel baumOrtsterminPanel;
    BaumSchadenPanel baumSchadenPanel;
    JButton btnAddApartner;
    JButton btnAddNewOrtstermin;
    JButton btnAddNewSchaden;
    JButton btnMenAbortApartner;
    JButton btnMenAbortOrtstermin;
    JButton btnMenOkApartner;
    JButton btnMenOkOrtstermin;
    JButton btnRemoveApartner;
    JButton btnRemoveOrtstermin;
    JButton btnRemoveSchaden;
    JComboBox cbApartner;
    DefaultBindableDateChooser dcOrtstermin;
    JDialog dlgAddApartner;
    JDialog dlgAddOrtstermin;
    Box.Filler filler1;
    JPanel jPanel8;
    JPanel jPanel9;
    JPanel jPanelAllgemein;
    JPanel jPanelOrtstermine;
    JPanel jPanelSchaeden;
    JTabbedPane jTabbedPane;
    JLabel lblApartner;
    JLabel lblAuswaehlenApartner;
    JLabel lblAuswaehlenOrtstermin;
    JLabel lblBemerkung;
    JLabel lblOrtstermin;
    JLabel lblOrtstermine;
    JLabel lblSchaden;
    JLabel lblSchaeden;
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
    RoundedPanel rpOrtstermininfo;
    RoundedPanel rpOrtsterminliste;
    RoundedPanel rpSchadeninfo;
    RoundedPanel rpSchadenliste;
    JScrollPane scpApartner;
    JScrollPane scpBemerkung;
    JScrollPane scpLaufendeOrtstermine;
    JScrollPane scpLaufendeSchaeden;
    SemiRoundedPanel semiRoundedPanel5;
    SemiRoundedPanel semiRoundedPanel6;
    SemiRoundedPanel semiRoundedPanelOrt;
    SemiRoundedPanel semiRoundedPanelSchaden;
    JTextArea taBemerkung;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BaumMeldungPanel object.
     */
    public BaumMeldungPanel() {
        this(null,true);
    }

    
    /**
     * Creates new form BaumMeldungPanel.
     *
     * @param parentEditor
     * @param  editable  DOCUMENT ME!
     */
    public BaumMeldungPanel(final BaumGebietEditor parentEditor, final boolean editable) {
        this.isEditor = editable;
        initComponents();
        this.connectionContext = null;
        this.parentEditor = parentEditor;
    }
 
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
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }
        
    @Override
    public void dispose() {
        bindingGroup.unbind();
        cidsBean = null;
        dlgAddApartner.dispose();
        dlgAddOrtstermin.dispose();
        deletedOrtsterminBeans.clear();
        deletedSchadenBeans.clear();
        schadenBeans.clear();
        ortsterminBeans.clear();
        changedSchadenBeans.clear();
        changedOrtsterminBeans.clear();
        //baumOrtsterminPanel.dispose();
        //baumSchadenPanel.dispose();
        
        //((DefaultListModel<CidsBean>)lstOrtstermine.getModel()).clear();
    }

    @Override
    public CidsBean getCidsBean() {
        return this.cidsBean;
    }

    @Override
    public void setCidsBean(CidsBean cidsBean) {
        if (isEditor && (this.cidsBean != null)) {
            this.cidsBean.removePropertyChangeListener(changeListener);
        }
        bindingGroup.unbind();
        this.cidsBean = cidsBean;
        bindingGroup.bind();
        if (isEditor && (this.cidsBean != null)) {
                cidsBean.addPropertyChangeListener(changeListener);
        }
        if (this.cidsBean != null){
            final String WHERE = " where "
                    + cidsBean.getProperty(FIELD__ID).toString()
                    + " = "
                    + FIELD__FK_MELDUNG;
            valueFromOtherTable(TABLE__ORT, WHERE, WhichCase.ORTSTERMIN);
            valueFromOtherTable(TABLE__SCHADEN, WHERE, WhichCase.SCHADEN);   
        }
        dlgAddOrtstermin.pack();
        dlgAddOrtstermin.getRootPane().setDefaultButton(btnMenOkOrtstermin);
        dlgAddApartner.pack();
        dlgAddApartner.getRootPane().setDefaultButton(btnMenOkApartner);
    }
    
    
    public boolean prepareForSave() {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();

        if (!baumSchadenPanel.prepareForSave()){
          return false;
        }
        
        boolean errorOccured = false;
        for (final CidsBean ortBean : changedOrtsterminBeans) {
            try {
                //Beans alle bei PrepareForSave checken!!!!!!!!!!!!1!!!!1!!1!!!!!!!!!!!!!!!!!!!1!!!!!!!!1!1!!!!!!!!1
                ortBean.persist(getConnectionContext());
            } catch (final Exception ex) {
                errorOccured = true;
                LOG.error(ex, ex);
            }
        }
        if (errorOccured) {
            return false;
        }
        for (final CidsBean ortBean : deletedOrtsterminBeans) {
            try {
                ortBean.delete();
                ortBean.persist(getConnectionContext());
            } catch (final Exception ex) {
                errorOccured = true;
                LOG.error(ex, ex);
            }
        }
        if (errorOccured) {
            return false;
        }
        for (final CidsBean schadenBean : changedSchadenBeans) {
            try {
                schadenBean.persist(getConnectionContext());
            } catch (final Exception ex) {
                errorOccured = true;
                LOG.error(ex, ex);
            }
        }
        if (errorOccured) {
            return false;
        }
        for (final CidsBean schadenBean : deletedSchadenBeans) {
            try {
                schadenBean.delete();
                schadenBean.persist(getConnectionContext());
            } catch (final Exception ex) {
                errorOccured = true;
                LOG.error(ex, ex);
            }
        }
        if (errorOccured) {
            return false;
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
        return save;
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
        
        //Collections.sort((List)schadenBeans, ID_COMPARATOR);
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
        
        Collections.sort((List)ortsterminBeans, DATE_COMPARATOR);
    }
    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeans  DOCUMENT ME!
     */
    public void setOrtsterminBeans(final List<CidsBean> cidsBeans) {
        baumOrtsterminPanel.setCidsBean(null);
        ((DefaultListModel)lstOrtstermine.getModel()).clear();
        this.ortsterminBeans.clear();
        if(cidsBeans != null){
            cidsBeans.sort(DATE_COMPARATOR);
            for(final Object bean:cidsBeans){
                ((DefaultListModel)lstOrtstermine.getModel()).addElement(bean);
            }
            this.ortsterminBeans = cidsBeans;
        }
        
        baumOrtsterminPanel.setCidsBean(null);
        prepareOrtstermin();
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
        this.schadenBeans.clear();
        if(cidsBeans != null){
            cidsBeans.sort(ID_COMPARATOR);
            for(final Object bean:cidsBeans){
                ((DefaultListModel)lstSchaeden.getModel()).addElement(bean);
            }
            this.schadenBeans = cidsBeans;
        }
        baumSchadenPanel.setCidsBean(null);
        prepareSchaden();
    }
    
    
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> getSchadenBeans() {
        return schadenBeans;
    }
    
    private void valueFromOtherTable(final String tableName,
            final String whereClause,
            final WhichCase fall) {
            final SwingWorker<MetaObject[], Void> worker;
            worker = new SwingWorker<MetaObject[], Void>() {
            
            @Override
            protected MetaObject[] doInBackground() throws Exception {
                return TableUtils.getOtherTableValues(tableName, whereClause, connectionContext);
            }
            
            @Override
            protected void done() {
                final MetaObject[] check;
                List<CidsBean> oneToNList = new ArrayList<>();
                try {
                    if (!isCancelled()) {
                        check = get();
                        if (check != null) {
                            for (final MetaObject metaO : check){
                                oneToNList.add(metaO.getBean());
                            }
                            switch (fall) {
                                case ORTSTERMIN: {          
                                    try {
                                        setOrtsterminBeans(oneToNList);
                                    } catch (Exception ex) {
                                        LOG.warn("Keine Ortstermine gesetzt.", ex);
                                    }
                                    break;
                                }
                                case SCHADEN: {          
                                    try {
                                        setSchadenBeans(oneToNList);
                                    } catch (Exception ex) {
                                        LOG.warn("Keine Schaeden gesetzt.", ex);
                                    }
                                    break;
                                }
                            }
                        } else {
                             switch (fall) {
                                case ORTSTERMIN: {          
                                    try {
                                        setOrtsterminBeans(null);
                                    } catch (Exception ex) {
                                        LOG.warn("Keine Ortstermine.", ex);
                                    }
                                    break;
                                }
                                case SCHADEN: {          
                                    try {
                                        setSchadenBeans(null);
                                    } catch (Exception ex) {
                                        LOG.warn("Keine Schaeden.", ex);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                } catch (InterruptedException | ExecutionException e) {
                    LOG.warn("problem in Worker: load values.", e);
                }
            }
        };
        if (fall.equals(WhichCase.ORTSTERMIN)) {
            if (worker_ortstermin != null) {
                worker_ortstermin.cancel(true);
            }
            worker_ortstermin = worker;
            worker_ortstermin.execute();
        } else {
            if (worker_schaden != null) {
                worker_schaden.cancel(true);
            }
            worker_schaden = worker;
            worker_schaden.execute();
        }
    }
    
}
