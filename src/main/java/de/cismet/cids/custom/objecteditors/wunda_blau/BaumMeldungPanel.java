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

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.cismet.cids.custom.objecteditors.utils.BaumChildrenLoader;
import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objecteditors.utils.TableUtils;
import de.cismet.cids.custom.objecteditors.wunda_blau.albo.ComboBoxFilterDialog;
import de.cismet.cids.custom.wunda_blau.search.server.BaumAnsprechpartnerLightweightSearch;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;
import de.cismet.cids.dynamics.Disposable;

import de.cismet.cids.editors.DefaultBindableDateChooser;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class BaumMeldungPanel extends javax.swing.JPanel implements Disposable,
    CidsBeanStore,
    ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(BaumMeldungPanel.class);

    public static final String FIELD__APARTNER = "arr_ansprechpartner";    // baum_meldung
    public static final String FIELD__DATUM = "datum";                     // baum_meldung
    public static final String FIELD__ZEIT = "zeit";                       // baum_ortstermin
    public static final String FIELD__FK_MELDUNG = "fk_meldung";           // baum_ortstermin bzw. schaden
    public static final String FIELD__ID = "id";                           // baum_schaden
    public static final String FIELD__SCHADEN_OHNE = "ohne_schaden";       // baum_schaden
    public static final String FIELD__SCHADEN_EFEU = "efeu";               // baum_schaden
    public static final String FIELD__SCHADEN_KRONE = "kronenschaden";     // baum_schaden
    public static final String FIELD__SCHADEN_STAMM = "stammschaden";      // baum_schaden
    public static final String FIELD__SCHADEN_WURZEL = "wurzelschaden";    // baum_schaden
    public static final String FIELD__SCHADEN_GEFAHR = "gefahrensbaum";    // baum_schaden
    public static final String FIELD__SCHADEN_KLEISTUNG = "keine_leistung";// baum_schaden
    public static final String FIELD__SCHADEN_STURM = "sturmschaden";      // baum_schaden
    public static final String FIELD__SCHADEN_ABGESTORBEN = "abgestorben"; // baum_schaden
    public static final String FIELD__SCHADEN_GUTACHTEN = "gutachten";     // baum_schaden
    public static final String FIELD__SCHADEN_BERATUNG = "baumberatung";   // baum_schaden
    public static final String FIELD__SCHADEN_EINGANG = "eingegangen";     // baum_schaden
    public static final String FIELD__SCHADEN_FAELLUNG = "faellung";       // baum_schaden
    public static final String TABLE__ORT = "baum_ortstermin";
    public static final String TABLE__SCHADEN = "baum_schaden";

    public static final String BUNDLE_AP_QUESTION =
        "BaumMeldungPanel.btnRemoveAnsprechpartnerActionPerformed().question";
    public static final String BUNDLE_AP_TITLE = "BaumMeldungPanel.btnRemoveAnsprechpartnerActionPerformed().title";
    public static final String BUNDLE_AP_ERRORTITLE =
        "BaumMeldungPanel.btnRemoveAnsprechpartnerActionPerformed().errortitle";
    public static final String BUNDLE_AP_ERRORTEXT =
        "BaumMeldungPanel.btnRemoveAnsprechpartnerActionPerformed().errortext";
    public static final String BUNDLE_PANE_TITLE_SCHADEN =
        "BaumMeldungPanel.btnRemoveSchadenActionPerformed().JOptionPane.title";
    public static final String BUNDLE_DEL_SCHADEN =
        "BaumMeldungPanel.btnRemoveSchadenActionPerformed().JOptionPane.message";
    public static final String BUNDLE_PANE_PREFIX = "BaumMeldungPanel.isOkForSaving().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX = "BaumMeldungPanel.isOkForSaving().JOptionPane.message.suffix";

    public static final String BUNDLE_NODATE = "BaumMeldungPanel.isOkForSaving().noDatum";
    public static final String BUNDLE_NOAP = "BaumMeldungPanel.isOkForSaving().noAp";
    public static final String BUNDLE_PANE_TITLE = "BaumMeldungPanel.isOkForSaving().JOptionPane.title";
    public static final String BUNDLE_PANE_TITLE_ERROR_ORT = "BaumMeldungPanel.zeigeErrorOrt().JOptionPane.title";
    public static final String BUNDLE_PANE_TITLE_ERROR_SCHADEN =
        "BaumMeldungPanel.zeigeErrorSchaden().JOptionPane.title";
    public static final String BUNDLE_ERROR_ORT = "BaumMeldungPanel.zeigeErrorOrt().JOptionPane.meldung";
    public static final String BUNDLE_ERROR_SCHADEN = "BaumMeldungPanel.zeigeErrorSchaden().JOptionPane.meldung";
    public static final String BUNDLE_WHICH = "BaumMeldungPanel.isOkForSaving().welcheMeldung";
    public static final String BUNDLE_PANE_PREFIX_SELECTION =
        "BaumMeldungPanel.btnApartnerActionPerformed().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX_SELECTION =
        "BaumMeldungPanel.btnApartnerActionPerformed().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE_SELECTION =
        "BaumMeldungPanel.btnApartnerActionPerformed().JOptionPane.title";
    public static final String BUNDLE_PANE_SELECTION =
        "BaumMeldungPanel.btnApartnerActionPerformed().JOptionPane.message";
    public static final String BUNDLE_NOSAVE_MESSAGE = "BaumMeldungPanel.noSave().message";
    public static final String BUNDLE_NOSAVE_TITLE = "BaumMeldungPanel.noSave().title";

    private static final ListModel MODEL_ERROR = new DefaultListModel() {

            {
                add(0, "FEHLER!");
            }
        };

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        GridBagConstraints gridBagConstraints;
        bindingGroup = new BindingGroup();

        dlgAddOrtstermin = new JDialog();
        panAddOrtstermin = new JPanel();
        lblAuswaehlenOrtstermin = new JLabel();
        panMenButtonsOrtstermin = new JPanel();
        btnMenAbortOrtstermin = new JButton();
        btnMenOkOrtstermin = new JButton();
        dcOrtstermin = new DefaultBindableDateChooser();
        comboBoxFilterDialogApartner = new ComboBoxFilterDialog(
                null,
                new BaumAnsprechpartnerLightweightSearch(),
                "Ansprechpartner/Melder auswählen",
                getConnectionContext());
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
        baumOrtsterminPanel = baumOrtsterminPanel = new BaumOrtsterminPanel(this.getBaumChildrenLoader());
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
        baumSchadenPanel = baumSchadenPanel = new BaumSchadenPanel(this.getBaumChildrenLoader());
        panControlsNewSchaden = new JPanel();
        btnAddNewSchaden = new JButton();
        btnRemoveSchaden = new JButton();

        final FormListener formListener = new FormListener();

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

        comboBoxFilterDialogApartner.setName("comboBoxFilterDialogApartner"); // NOI18N

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

        lblAbgenommen.setFont(new Font("Tahoma", 1, 11));                                        // NOI18N
        Mnemonics.setLocalizedText(
            lblAbgenommen,
            NbBundle.getMessage(BaumMeldungPanel.class, "BaumMeldungPanel.lblAbgenommen.text")); // NOI18N
        lblAbgenommen.setName("lblAbgenommen");                                                  // NOI18N
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
        chAbgenommen.setEnabled(false);
        chAbgenommen.setName("chAbgenommen"); // NOI18N

        Binding binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.abgenommen}"),
                chAbgenommen,
                BeanProperty.create("selected"));
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

        lblApartner.setFont(new Font("Tahoma", 1, 11));                                        // NOI18N
        Mnemonics.setLocalizedText(
            lblApartner,
            NbBundle.getMessage(BaumMeldungPanel.class, "BaumMeldungPanel.lblApartner.text")); // NOI18N
        lblApartner.setName("lblApartner");                                                    // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panInfo.add(lblApartner, gridBagConstraints);

        panApartner.setEnabled(false);
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
        lblBemerkung.setName("lblBemerkung");            // NOI18N
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
        taBemerkung.setEnabled(false);
        taBemerkung.setName("taBemerkung"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.bemerkung}"),
                taBemerkung,
                BeanProperty.create("text"));
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

        final GroupLayout panFillerUnten5Layout = new GroupLayout(panFillerUnten5);
        panFillerUnten5.setLayout(panFillerUnten5Layout);
        panFillerUnten5Layout.setHorizontalGroup(panFillerUnten5Layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        panFillerUnten5Layout.setVerticalGroup(panFillerUnten5Layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        panInfo.add(panFillerUnten5, gridBagConstraints);

        btnApartner.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/icon-explorerwindow.png"))); // NOI18N
        btnApartner.setBorderPainted(false);
        btnApartner.setContentAreaFilled(false);
        btnApartner.setEnabled(false);
        btnApartner.setFocusPainted(false);
        btnApartner.setName("btnApartner");                                                                          // NOI18N
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
        lstApartner.setName("lstApartner");             // NOI18N

        final ELProperty eLProperty = ELProperty.create("${cidsBean.arr_ansprechpartner}");
        final JListBinding jListBinding = SwingBindings.createJListBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                eLProperty,
                lstApartner);
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

        btnAddApartner.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddApartner.setEnabled(false);
        btnAddApartner.setName("btnAddApartner");                                                              // NOI18N
        btnAddApartner.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panButtonsApartner.add(btnAddApartner, gridBagConstraints);

        btnRemoveApartner.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveApartner.setEnabled(false);
        btnRemoveApartner.setName("btnRemoveApartner");                                                           // NOI18N
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

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                lstOrtstermine,
                ELProperty.create("${selectedElement}"),
                baumOrtsterminPanel,
                BeanProperty.create("cidsBean"));
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

        lblLadenOrt.setFont(new Font("Tahoma", 1, 11));                                        // NOI18N
        lblLadenOrt.setForeground(new Color(153, 153, 153));
        Mnemonics.setLocalizedText(
            lblLadenOrt,
            NbBundle.getMessage(BaumMeldungPanel.class, "BaumMeldungPanel.lblLadenOrt.text")); // NOI18N
        lblLadenOrt.setName("lblLadenOrt");                                                    // NOI18N
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
        scpLaufendeOrtstermine.setPreferredSize(new Dimension(80, 170));

        lstOrtstermine.setModel(new DefaultListModel<>());
        lstOrtstermine.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstOrtstermine.setFixedCellWidth(75);
        lstOrtstermine.setName("lstOrtstermine"); // NOI18N
        scpLaufendeOrtstermine.setViewportView(lstOrtstermine);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panOrtstermin.add(scpLaufendeOrtstermine, gridBagConstraints);

        panControlsNewOrtstermine.setMinimumSize(new Dimension(70, 30));
        panControlsNewOrtstermine.setName("panControlsNewOrtstermine"); // NOI18N
        panControlsNewOrtstermine.setOpaque(false);
        panControlsNewOrtstermine.setLayout(new GridBagLayout());

        btnAddNewOrtstermin.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddNewOrtstermin.setEnabled(false);
        btnAddNewOrtstermin.setMaximumSize(new Dimension(39, 20));
        btnAddNewOrtstermin.setMinimumSize(new Dimension(25, 20));
        btnAddNewOrtstermin.setName("btnAddNewOrtstermin");                                                    // NOI18N
        btnAddNewOrtstermin.setPreferredSize(new Dimension(25, 20));
        btnAddNewOrtstermin.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panControlsNewOrtstermine.add(btnAddNewOrtstermin, gridBagConstraints);

        btnRemoveOrtstermin.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveOrtstermin.setEnabled(false);
        btnRemoveOrtstermin.setMaximumSize(new Dimension(39, 20));
        btnRemoveOrtstermin.setMinimumSize(new Dimension(39, 20));
        btnRemoveOrtstermin.setName("btnRemoveOrtstermin");                                                       // NOI18N
        btnRemoveOrtstermin.setPreferredSize(new Dimension(25, 20));
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

        final GroupLayout panFillerUnten3Layout = new GroupLayout(panFillerUnten3);
        panFillerUnten3.setLayout(panFillerUnten3Layout);
        panFillerUnten3Layout.setHorizontalGroup(panFillerUnten3Layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        panFillerUnten3Layout.setVerticalGroup(panFillerUnten3Layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));

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

        final GroupLayout panFillerUnten4Layout = new GroupLayout(panFillerUnten4);
        panFillerUnten4.setLayout(panFillerUnten4Layout);
        panFillerUnten4Layout.setHorizontalGroup(panFillerUnten4Layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        panFillerUnten4Layout.setVerticalGroup(panFillerUnten4Layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));

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

        lblLadenSchaden.setFont(new Font("Tahoma", 1, 11));                                        // NOI18N
        lblLadenSchaden.setForeground(new Color(153, 153, 153));
        Mnemonics.setLocalizedText(
            lblLadenSchaden,
            NbBundle.getMessage(BaumMeldungPanel.class, "BaumMeldungPanel.lblLadenSchaden.text")); // NOI18N
        lblLadenSchaden.setName("lblLadenSchaden");                                                // NOI18N
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

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                lstSchaeden,
                ELProperty.create("${selectedElement}"),
                baumSchadenPanel,
                BeanProperty.create("cidsBean"));
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

        btnAddNewSchaden.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddNewSchaden.setEnabled(false);
        btnAddNewSchaden.setMaximumSize(new Dimension(39, 20));
        btnAddNewSchaden.setMinimumSize(new Dimension(39, 20));
        btnAddNewSchaden.setName("btnAddNewSchaden");                                                          // NOI18N
        btnAddNewSchaden.setPreferredSize(new Dimension(39, 25));
        btnAddNewSchaden.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panControlsNewSchaden.add(btnAddNewSchaden, gridBagConstraints);

        btnRemoveSchaden.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveSchaden.setEnabled(false);
        btnRemoveSchaden.setMaximumSize(new Dimension(39, 20));
        btnRemoveSchaden.setMinimumSize(new Dimension(39, 20));
        btnRemoveSchaden.setName("btnRemoveSchaden");                                                             // NOI18N
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

    /**
     * Code for dispatching events from components to event handlers.
     *
     * @version  $Revision$, $Date$
     */
    private class FormListener implements ActionListener, ChangeListener {

        /**
         * Creates a new FormListener object.
         */
        FormListener() {
        }

        @Override
        public void actionPerformed(final ActionEvent evt) {
            if (evt.getSource() == btnApartner) {
                BaumMeldungPanel.this.btnApartnerActionPerformed(evt);
            } else if (evt.getSource() == btnAddApartner) {
                BaumMeldungPanel.this.btnAddApartnerActionPerformed(evt);
            } else if (evt.getSource() == btnRemoveApartner) {
                BaumMeldungPanel.this.btnRemoveApartnerActionPerformed(evt);
            } else if (evt.getSource() == btnAddNewOrtstermin) {
                BaumMeldungPanel.this.btnAddNewOrtsterminActionPerformed(evt);
            } else if (evt.getSource() == btnRemoveOrtstermin) {
                BaumMeldungPanel.this.btnRemoveOrtsterminActionPerformed(evt);
            } else if (evt.getSource() == btnAddNewSchaden) {
                BaumMeldungPanel.this.btnAddNewSchadenActionPerformed(evt);
            } else if (evt.getSource() == btnRemoveSchaden) {
                BaumMeldungPanel.this.btnRemoveSchadenActionPerformed(evt);
            } else if (evt.getSource() == btnMenAbortOrtstermin) {
                BaumMeldungPanel.this.btnMenAbortOrtsterminActionPerformed(evt);
            } else if (evt.getSource() == btnMenOkOrtstermin) {
                BaumMeldungPanel.this.btnMenOkOrtsterminActionPerformed(evt);
            }
        }

        @Override
        public void stateChanged(final ChangeEvent evt) {
            if (evt.getSource() == chAbgenommen) {
                BaumMeldungPanel.this.chAbgenommenStateChanged(evt);
            }
        }
    } // </editor-fold>//GEN-END:initComponents

    //~ Instance fields --------------------------------------------------------

    private BaumChildrenLoader.Listener loadChildrenListener;
    @Getter @Setter private Integer counterSchaden = -1;

    @Getter private final BaumChildrenLoader baumChildrenLoader;
    private final boolean editor;
    @Getter @Setter private static Exception errorNoSave = null;

    private final PropertyChangeListener changeListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                if ((getBaumChildrenLoader() != null)
                            && (getBaumChildrenLoader().getParentOrganizer() != null)
                            && (getBaumChildrenLoader().getParentOrganizer().getCidsBean() != null)) {
                    getBaumChildrenLoader().getParentOrganizer().getCidsBean().setArtificialChangeFlag(true);
                }
            }
        };

    private CidsBean cidsBean;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    BaumOrtsterminPanel baumOrtsterminPanel;
    BaumSchadenPanel baumSchadenPanel;
    JButton btnAddApartner;
    JButton btnAddNewOrtstermin;
    JButton btnAddNewSchaden;
    JButton btnApartner;
    JButton btnMenAbortOrtstermin;
    JButton btnMenOkOrtstermin;
    JButton btnRemoveApartner;
    JButton btnRemoveOrtstermin;
    JButton btnRemoveSchaden;
    JCheckBox chAbgenommen;
    ComboBoxFilterDialog comboBoxFilterDialogApartner;
    DefaultBindableDateChooser dcOrtstermin;
    JDialog dlgAddOrtstermin;
    Box.Filler filler1;
    JPanel jPanelAllgemein;
    JPanel jPanelOrtstermine;
    JPanel jPanelSchaeden;
    JTabbedPane jTabbedPane;
    JLabel lblAbgenommen;
    JLabel lblApartner;
    JLabel lblAuswaehlenOrtstermin;
    JLabel lblBemerkung;
    JLabel lblLadenOrt;
    JLabel lblLadenSchaden;
    JList lstApartner;
    JList lstOrtstermine;
    JList lstSchaeden;
    JPanel panAddOrtstermin;
    JPanel panApartner;
    JPanel panButtonsApartner;
    JPanel panControlsNewOrtstermine;
    JPanel panControlsNewSchaden;
    JPanel panFillerUnten3;
    JPanel panFillerUnten4;
    JPanel panFillerUnten5;
    JPanel panInfo;
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
        this(null);
    }

    /**
     * Creates new form BaumMeldungPanel.
     *
     * @param  bclInstance  DOCUMENT ME!
     */
    public BaumMeldungPanel(final BaumChildrenLoader bclInstance) {
        this.baumChildrenLoader = bclInstance;
        if (bclInstance != null) {
            this.editor = bclInstance.getParentOrganizer().isEditor();
        } else {
            this.editor = false;
        }
        initComponents();
        if (getBaumChildrenLoader() != null) {
            loadChildrenListener = new LoaderListener();
            getBaumChildrenLoader().addListener(loadChildrenListener);
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddApartnerActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_btnAddApartnerActionPerformed
        final Object selectedItem = comboBoxFilterDialogApartner.showAndGetSelected();
        try {
            if (selectedItem instanceof CidsBean) {
                cidsBean = TableUtils.addBeanToCollectionWithMessage(StaticSwingTools.getParentFrame(this),
                        getCidsBean(),
                        FIELD__APARTNER,
                        (CidsBean)selectedItem);
            }
        } catch (Exception ex) {
            LOG.error(ex, ex);
        } finally {
            getCidsBean().setArtificialChangeFlag(true);
            getBaumChildrenLoader().getParentOrganizer().getCidsBean().setArtificialChangeFlag(true);
        }
    }//GEN-LAST:event_btnAddApartnerActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveApartnerActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_btnRemoveApartnerActionPerformed
        final Object selection = lstApartner.getSelectedValue();
        if (selection != null) {
            final int answer = JOptionPane.showConfirmDialog(StaticSwingTools.getParentFrame(this),
                    NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_AP_QUESTION),
                    NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_AP_TITLE),
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                try {
                    cidsBean = TableUtils.deleteItemFromList(getCidsBean(), FIELD__APARTNER, selection, false);
                    getCidsBean().setArtificialChangeFlag(true);
                    getBaumChildrenLoader().getParentOrganizer().getCidsBean().setArtificialChangeFlag(true);
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

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddNewOrtsterminActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_btnAddNewOrtsterminActionPerformed
        if (getBaumChildrenLoader().getLoadingCompletedWithoutError()) {
            if (getCidsBean() != null) {
                try {
                    StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(BaumMeldungPanel.this),
                        dlgAddOrtstermin,
                        true);
                } catch (Exception e) {
                    LOG.error("Cannot add new BaumOrtstermin object", e);
                }
            }
        }
    }//GEN-LAST:event_btnAddNewOrtsterminActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveOrtsterminActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_btnRemoveOrtsterminActionPerformed
        if (getBaumChildrenLoader().getLoadingCompletedWithoutError()) {
            final Object selectedObject = lstOrtstermine.getSelectedValue();

            if (selectedObject instanceof CidsBean) {
                final List<CidsBean> listOrte = getBaumChildrenLoader().getMapValueOrt(getCidsBean()
                                .getPrimaryKeyValue());
                if (((CidsBean)selectedObject).getMetaObject().getStatus() == MetaObject.NEW) {
                    getBaumChildrenLoader().removeOrt(getCidsBean().getPrimaryKeyValue(), (CidsBean)selectedObject);
                } else {
                    for (final CidsBean beanOrt : listOrte) {
                        if (beanOrt.equals(selectedObject)) {
                            try {
                                beanOrt.delete();
                            } catch (Exception ex) {
                                LOG.warn("problem in delete ort: not removed.", ex);
                            }
                            break;
                        }
                    }
                    getBaumChildrenLoader().getMapOrt().replace(getCidsBean().getPrimaryKeyValue(), listOrte);
                }
                ((DefaultListModel)lstOrtstermine.getModel()).removeElement(selectedObject);
                if (getActiveBeans(listOrte) > 0) {
                    lstOrtstermine.setSelectedIndex(0);
                }
                getCidsBean().setArtificialChangeFlag(true);
                getBaumChildrenLoader().getParentOrganizer().getCidsBean().setArtificialChangeFlag(true);
            }
        }
    }//GEN-LAST:event_btnRemoveOrtsterminActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenAbortOrtsterminActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_btnMenAbortOrtsterminActionPerformed
        dlgAddOrtstermin.setVisible(false);
    }//GEN-LAST:event_btnMenAbortOrtsterminActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenOkOrtsterminActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_btnMenOkOrtsterminActionPerformed
        try {
            // meldungsBean erzeugen und vorbelegen:
            final CidsBean beanOrtstermin = CidsBean.createNewCidsBeanFromTableName(
                    "WUNDA_BLAU",
                    TABLE__ORT,
                    getConnectionContext());

            final java.util.Date selDate = dcOrtstermin.getDate();
            final java.util.Calendar cal = Calendar.getInstance();
            cal.setTime(selDate);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            beanOrtstermin.setProperty(FIELD__ZEIT, new java.sql.Timestamp(cal.getTime().getTime()));
            beanOrtstermin.setProperty(FIELD__FK_MELDUNG, getCidsBean());

            // Meldungen erweitern:
            if (isEditor()) {
                getBaumChildrenLoader().addOrt(getCidsBean().getPrimaryKeyValue(), beanOrtstermin);
            }
            ((DefaultListModel)lstOrtstermine.getModel()).addElement(beanOrtstermin);

            lstOrtstermine.setSelectedValue(beanOrtstermin, true);
            getCidsBean().setArtificialChangeFlag(true);
            getBaumChildrenLoader().getParentOrganizer().getCidsBean().setArtificialChangeFlag(true);
        } catch (Exception ex) {
            LOG.error(ex, ex);
        } finally {
            dlgAddOrtstermin.setVisible(false);
        }
    }//GEN-LAST:event_btnMenOkOrtsterminActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddNewSchadenActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_btnAddNewSchadenActionPerformed
        if (getBaumChildrenLoader().getLoadingCompletedWithoutError()) {
            if (getCidsBean() != null) {
                try {
                    // schadenBean erzeugen und vorbelegen:
                    final CidsBean beanSchaden = CidsBean.createNewCidsBeanFromTableName(
                            "WUNDA_BLAU",
                            TABLE__SCHADEN,
                            getConnectionContext());
                    beanSchaden.setProperty(FIELD__FK_MELDUNG, getCidsBean());
                    beanSchaden.setProperty(FIELD__SCHADEN_ABGESTORBEN, false);
                    beanSchaden.setProperty(FIELD__SCHADEN_BERATUNG, false);
                    beanSchaden.setProperty(FIELD__SCHADEN_EINGANG, false);
                    beanSchaden.setProperty(FIELD__SCHADEN_FAELLUNG, false);
                    beanSchaden.setProperty(FIELD__SCHADEN_GUTACHTEN, false);
                    beanSchaden.setProperty(FIELD__SCHADEN_KRONE, false);
                    beanSchaden.setProperty(FIELD__SCHADEN_OHNE, false);
                    beanSchaden.setProperty(FIELD__SCHADEN_EFEU, false);
                    beanSchaden.setProperty(FIELD__SCHADEN_STAMM, false);
                    beanSchaden.setProperty(FIELD__SCHADEN_STURM, false);
                    beanSchaden.setProperty(FIELD__SCHADEN_WURZEL, false);
                    beanSchaden.setProperty(FIELD__SCHADEN_GEFAHR, false);
                    beanSchaden.setProperty(FIELD__SCHADEN_KLEISTUNG, false);

                    beanSchaden.setProperty(FIELD__ID, getCounterSchaden());
                    setCounterSchaden(getCounterSchaden() - 1);

                    // schaden erweitern:
                    if (isEditor()) {
                        getBaumChildrenLoader().addSchaden(getCidsBean().getPrimaryKeyValue(), beanSchaden);
                    }
                    ((DefaultListModel)lstSchaeden.getModel()).addElement(beanSchaden);

                    // Refresh:
                    lstSchaeden.setSelectedValue(beanSchaden, true);
                    getCidsBean().setArtificialChangeFlag(true);
                    getBaumChildrenLoader().getParentOrganizer().getCidsBean().setArtificialChangeFlag(true);
                } catch (Exception e) {
                    LOG.error("Cannot add new BaumSchaden object", e);
                }
            }
        }
    }//GEN-LAST:event_btnAddNewSchadenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveSchadenActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_btnRemoveSchadenActionPerformed
        if (getBaumChildrenLoader().getLoadingCompletedWithoutError()) {
            final Object selectedObject = lstSchaeden.getSelectedValue();

            if (selectedObject instanceof CidsBean) {
                final Integer idSchaden = ((CidsBean)selectedObject).getPrimaryKeyValue();
                if ((getBaumChildrenLoader().getMapValueFest(idSchaden) == null)
                            && (getBaumChildrenLoader().getMapValueErsatz(idSchaden) == null)) {
                    final List<CidsBean> listSchaeden = getBaumChildrenLoader().getMapValueSchaden(getCidsBean()
                                    .getPrimaryKeyValue());
                    if (((CidsBean)selectedObject).getMetaObject().getStatus() == MetaObject.NEW) {
                        getBaumChildrenLoader().removeSchaden(getCidsBean().getPrimaryKeyValue(),
                            (CidsBean)selectedObject);
                    } else {
                        for (final CidsBean beanSchaden : listSchaeden) {
                            if (beanSchaden.equals(selectedObject)) {
                                try {
                                    beanSchaden.delete();
                                } catch (Exception ex) {
                                    LOG.warn("problem in delete schaden: not removed.", ex);
                                }
                                break;
                            }
                        }
                        getBaumChildrenLoader().getMapMeldung()
                                .replace(getCidsBean().getPrimaryKeyValue(), listSchaeden);
                    }
                    ((DefaultListModel)lstSchaeden.getModel()).removeElement(selectedObject);
                    if (getActiveBeans(listSchaeden) > 0) {
                        lstSchaeden.setSelectedIndex(0);
                    }
                    getCidsBean().setArtificialChangeFlag(true);
                    getBaumChildrenLoader().getParentOrganizer().getCidsBean().setArtificialChangeFlag(true);
                } else {
                    // Meldung, Schaden hat Unterobjekte
                    JOptionPane.showMessageDialog(
                        StaticSwingTools.getParentFrame(this),
                        NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_DEL_SCHADEN),
                        NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_PANE_TITLE_SCHADEN),
                        JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_btnRemoveSchadenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnApartnerActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApartnerActionPerformed
        final JDialog dialog = new JDialog((Frame)null,
                "Ansprechpartner/Melderinformationen",
                true);
        final Collection<MetaObjectNode> mons = new ArrayList<>();

        final Object selection = lstApartner.getSelectedValue();
        if (selection != null) {
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

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void chAbgenommenStateChanged(final ChangeEvent evt) {//GEN-FIRST:event_chAbgenommenStateChanged
        isAbgenommen();
    }//GEN-LAST:event_chAbgenommenStateChanged

    @Override
    public ConnectionContext getConnectionContext() {
        return ((baumChildrenLoader != null) && (baumChildrenLoader.getParentOrganizer() != null))
            ? baumChildrenLoader.getParentOrganizer().getConnectionContext() : null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cbList  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Integer getActiveBeans(final List<CidsBean> cbList) {
        Integer anzahl = 0;
        for (final CidsBean bean : cbList) {
            if (bean.getMetaObject().getStatus() != MetaObject.TO_DELETE) {
                anzahl += 1;
            }
        }
        return anzahl;
    }

    @Override
    public void dispose() {
        bindingGroup.unbind();
        cidsBean = null;
        dlgAddOrtstermin.dispose();
        baumOrtsterminPanel.dispose();
        baumSchadenPanel.dispose();
        getBaumChildrenLoader().removeListener(loadChildrenListener);
        setCounterSchaden(null);
    }

    @Override
    public CidsBean getCidsBean() {
        return this.cidsBean;
    }

    /**
     * DOCUMENT ME!
     */
    private void setReadOnly() {
        if (!(isEditor())) {
            RendererTools.makeReadOnly(chAbgenommen);
            RendererTools.makeReadOnly(lstApartner);
            panApartner.setEnabled(false);
            RendererTools.makeReadOnly(taBemerkung);
            panControlsNewOrtstermine.setVisible(isEditor());
            panControlsNewSchaden.setVisible(isEditor());
            panButtonsApartner.setVisible(isEditor());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isEditor() {
        return this.editor;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        try{
            if (!(Objects.equals(this.cidsBean, cidsBean))) {
                if (isEditor() && (this.cidsBean != null)) {
                    this.cidsBean.removePropertyChangeListener(changeListener);
                }
                bindingGroup.unbind();
                this.cidsBean = cidsBean;
                if (this.cidsBean != null) {
                    zeigeKinderOrt();
                    zeigeKinderSchaden();
                } else {
                    setOrtsterminBeans(null);
                    setSchadenBeans(null);
                }
                bindingGroup.bind();
                if (isEditor() && (this.cidsBean != null)) {
                    this.cidsBean.addPropertyChangeListener(changeListener);
                }
                isAbgenommen();
                dlgAddOrtstermin.pack();
                dlgAddOrtstermin.getRootPane().setDefaultButton(btnMenOkOrtstermin);
                if ((cidsBean != null) && (cidsBean.getMetaObject().getStatus() == MetaObject.NEW)) {
                    getBaumChildrenLoader().setLoadingCompletedWithoutError(true);
                    allowAddRemove();
                }
            }
        } catch (Exception ex ){
            LOG.error("Bean not set", ex);
            if (isEditor()){
                setErrorNoSave(ex);
                noSave();
            }
        }
        setReadOnly();
        if (isEditor()) {
            nullNoEdit(getCidsBean() != null);
        }
        btnApartner.setEnabled(getCidsBean() != null);
    }
    
    public void noSave(){
        final ErrorInfo info = new ErrorInfo(
                    NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_NOSAVE_TITLE),
                    NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_NOSAVE_MESSAGE),
                    null,
                    null,
                    getErrorNoSave(),
                    Level.SEVERE,
                    null);
        JXErrorPane.showDialog(BaumMeldungPanel.this, info);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  edit  DOCUMENT ME!
     */
    private void nullNoEdit(final boolean edit) {
        chAbgenommen.setEnabled(edit);
        panApartner.setEnabled(edit);
        taBemerkung.setEnabled(edit);
        btnAddApartner.setEnabled(edit);
        btnRemoveApartner.setEnabled(edit);
        if (!edit){
            taBemerkung.setText("");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   saveMeldungBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isOkayForSaving(final CidsBean saveMeldungBean) {
        if (getErrorNoSave()!= null){
            noSave();
            return false;
        } else {
            boolean save = true;
            final StringBuilder errorMessage = new StringBuilder();
            boolean noErrorOrt = true;
            boolean noErrorSchaden = true;
            final List<CidsBean> listOrt = getBaumChildrenLoader().getMapValueOrt(saveMeldungBean.getPrimaryKeyValue());
            final List<CidsBean> listSchaden = getBaumChildrenLoader().getMapValueSchaden(
                    saveMeldungBean.getPrimaryKeyValue());

            if ((listOrt != null) && !(listOrt.isEmpty())) {
                for (final CidsBean ortBean : listOrt) {
                    try {
                        noErrorOrt = baumOrtsterminPanel.isOkayForSaving(ortBean);
                        if (!noErrorOrt) {
                            break;
                        }
                    } catch (final Exception ex) {
                        noErrorOrt = false;
                        LOG.error(ex, ex);
                    }
                }
            }
            if ((listSchaden != null) && !(listSchaden.isEmpty())) {
                for (final CidsBean schadenBean : listSchaden) {
                    try {
                        noErrorSchaden = baumSchadenPanel.isOkForSaving(schadenBean);
                        if (!noErrorSchaden) {
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
                if (saveMeldungBean.getProperty(FIELD__DATUM) == null) {
                    LOG.warn("No datum specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_NODATE));
                    save = false;
                }
            } catch (final MissingResourceException ex) {
                LOG.warn("Datum not given.", ex);
                save = false;
            }
            //Ap vorhanden
            try {
                final Collection<CidsBean> collectionAp = saveMeldungBean.getBeanCollectionProperty(FIELD__APARTNER);
                if (collectionAp == null || collectionAp.isEmpty()) {
                    LOG.warn("No ap specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_NOAP));
                    save = false;
                }
            } catch (final MissingResourceException ex) {
                LOG.warn("Ap not given.", ex);
                save = false;
            }

            if (errorMessage.length() > 0) {
                String meldung;
                if (saveMeldungBean.getProperty(FIELD__DATUM) != null){
                    final SimpleDateFormat formatTag = new SimpleDateFormat("dd.MM.yy");
                    meldung = formatTag.format(saveMeldungBean.getProperty(FIELD__DATUM));
                } else {
                    meldung = saveMeldungBean.getPrimaryKeyValue().toString();
                }
                errorMessage.append(NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_WHICH))
                        .append(meldung);
                JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                    NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_PANE_PREFIX)
                            + errorMessage.toString()
                            + NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_PANE_SUFFIX),
                    NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_PANE_TITLE),
                    JOptionPane.WARNING_MESSAGE);
            }
            return save && noErrorOrt && noErrorSchaden;
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void isAbgenommen() {
        if (chAbgenommen.isSelected()) {
            lblAbgenommen.setForeground(Color.black);
        } else {
            lblAbgenommen.setForeground(Color.red);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void prepareSchaden() {
        if (this.cidsBean != null) {
            if ((getBaumChildrenLoader().getMapValueSchaden(this.cidsBean.getPrimaryKeyValue()) != null)
                        && (getActiveBeans(
                                getBaumChildrenLoader().getMapValueSchaden(this.cidsBean.getPrimaryKeyValue())) > 0)) {
                lstSchaeden.setSelectedIndex(0);
            }
        } else {
            lstSchaeden.clearSelection();
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
                    final Component compoId = super.getListCellRendererComponent(
                            list,
                            newValue,
                            index,
                            isSelected,
                            cellHasFocus);
                    compoId.setForeground(new Color(255, 140, 0));
                    return compoId;
                }
            });
    }

    /**
     * DOCUMENT ME!
     */
    private void allowAddRemove() {
        if (getBaumChildrenLoader().getLoadingCompletedWithoutError()) {
            if (isEditor()) {
                btnAddNewOrtstermin.setEnabled(true);
                btnAddNewSchaden.setEnabled(true);
                btnRemoveOrtstermin.setEnabled(true);
                btnRemoveSchaden.setEnabled(true);
            }
            lblLadenOrt.setVisible(false);
            lblLadenSchaden.setVisible(false);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void prepareOrtstermin() {
        if (this.cidsBean != null) {
            if ((getBaumChildrenLoader().getMapValueOrt(this.cidsBean.getPrimaryKeyValue()) != null)
                        && (getActiveBeans(getBaumChildrenLoader().getMapValueOrt(this.cidsBean.getPrimaryKeyValue()))
                            > 0)) {
                lstOrtstermine.setSelectedIndex(0);
            }
        } else {
            lstOrtstermine.clearSelection();
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
                        final Calendar calDatumZeit = Calendar.getInstance();
                        calDatumZeit.setTime((Date)bean.getProperty(FIELD__ZEIT));
                        final java.util.Date datum = calDatumZeit.getTime();
                        final SimpleDateFormat formatTag = new SimpleDateFormat("dd.MM.yy");
                        newValue = formatTag.format(datum);

                        if (newValue == null) {
                            newValue = "unbenannt";
                        }
                    }
                    final Component compoDatum = super.getListCellRendererComponent(
                            list,
                            newValue,
                            index,
                            isSelected,
                            cellHasFocus);
                    compoDatum.setForeground(new Color(9, 68, 9));
                    return compoDatum;
                }
            });
    }
    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeans  DOCUMENT ME!
     */
    private void setOrtsterminBeans(final List<CidsBean> cidsBeans) {
        try {
            baumOrtsterminPanel.setCidsBean(null);
            ((DefaultListModel)lstOrtstermine.getModel()).clear();
            if (cidsBeans != null) {
                for (final Object bean : cidsBeans) {
                    if ((bean instanceof CidsBean)
                                && (((CidsBean)bean).getMetaObject().getStatus() != MetaObject.TO_DELETE)) {
                        ((DefaultListModel)lstOrtstermine.getModel()).addElement(bean);
                    }
                }
            }
            prepareOrtstermin();
        } catch (final Exception ex) {
            LOG.warn("ort list not cleared.", ex);
        }
    }
    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeans  DOCUMENT ME!
     */
    private void setSchadenBeans(final List<CidsBean> cidsBeans) {
        baumSchadenPanel.setCidsBean(null);
        ((DefaultListModel)lstSchaeden.getModel()).clear();
        if (cidsBeans != null) {
            for (final Object bean : cidsBeans) {
                if ((bean instanceof CidsBean)
                            && (((CidsBean)bean).getMetaObject().getStatus() != MetaObject.TO_DELETE)) {
                    ((DefaultListModel)lstSchaeden.getModel()).addElement(bean);
                }
            }
        }
        prepareSchaden();
    }

    /**
     * DOCUMENT ME!
     */
    private void zeigeKinderOrt() {
        setOrtsterminBeans(getBaumChildrenLoader().getMapValueOrt(cidsBean.getPrimaryKeyValue()));
    }

    /**
     * DOCUMENT ME!
     */
    private void zeigeKinderSchaden() {
        setSchadenBeans(getBaumChildrenLoader().getMapValueSchaden(cidsBean.getPrimaryKeyValue()));
    }

    /**
     * DOCUMENT ME!
     */
    private void zeigeErrorOrt() {
        scpLaufendeOrtstermine.setViewportView(new JList<>(MODEL_ERROR));
        JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
            NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_ERROR_ORT),
            NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_PANE_TITLE_ERROR_ORT),
            JOptionPane.WARNING_MESSAGE);
    }

    /**
     * DOCUMENT ME!
     */
    private void zeigeErrorSchaden() {
        scpLaufendeSchaeden.setViewportView(new JList<>(MODEL_ERROR));
        JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
            NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_ERROR_SCHADEN),
            NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_PANE_TITLE_ERROR_SCHADEN),
            JOptionPane.WARNING_MESSAGE);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public final class DescriptionPaneDialogWrapperPanel extends JPanel {

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

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class LoaderListener implements BaumChildrenLoader.Listener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void loadingCompleteSchaden(final Integer idMeldung) {
            if (Objects.equals(cidsBean.getPrimaryKeyValue(), idMeldung)) {
                lblLadenSchaden.setVisible(false);
                zeigeKinderSchaden();
            }
        }

        @Override
        public void loadingCompleteOrt(final Integer idMeldung) {
            if (Objects.equals(cidsBean.getPrimaryKeyValue(), idMeldung)) {
                lblLadenOrt.setVisible(false);
                zeigeKinderOrt();
            }
        }

        @Override
        public void loadingErrorOrt(final Integer idMeldung) {
            zeigeErrorOrt();
        }

        @Override
        public void loadingErrorSchaden(final Integer idMeldung) {
            zeigeErrorSchaden();
        }

        @Override
        public void loadingComplete() {
            allowAddRemove();
        }

        @Override
        public void loadingCompleteFest(final Integer idMeldung) {
        }

        @Override
        public void loadingCompleteErsatz(final Integer idMeldung) {
        }

        @Override
        public void loadingErrorFest(final Integer idMeldung) {
        }

        @Override
        public void loadingErrorErsatz(final Integer idMeldung) {
        }

        @Override
        public void loadingCompleteMeldung() {
        }
    }
}
