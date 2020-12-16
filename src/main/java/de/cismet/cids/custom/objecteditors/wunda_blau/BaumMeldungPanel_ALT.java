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
import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objecteditors.utils.TableUtils;
import org.apache.log4j.Logger;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Collections;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
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
public class BaumMeldungPanel_ALT extends javax.swing.JPanel implements Disposable, CidsBeanStore, ConnectionContextProvider {

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
    
    private List<CidsBean> ortsterminBeans;
    private List<CidsBean> schadenBeans;
    private static final Logger LOG = Logger.getLogger(BaumMeldungPanel_ALT.class);
    
    
    public static final String FIELD__APARTNER = "arr_ansprechpartner";         // baum_meldung
    public static final String FIELD__ORTSTERMINE = "n_ortstermine";            // baum_meldung
    public static final String FIELD__SCHAEDEN = "n_schaeden";                  // baum_meldung
    public static final String FIELD__DATUM = "datum";                          // baum_ortstermin
    public static final String FIELD__ID = "id";                                // baum_schaden
    
    public static final String BUNDLE_AP_QUESTION = "BaumMeldungPanel.btnRemoveAnsprechpartnerActionPerformed().question";
    public static final String BUNDLE_AP_TITLE = "BaumMeldungPanel.btnRemoveAnsprechpartnerActionPerformed().title";
    public static final String BUNDLE_AP_ERRORTITLE = "BaumMeldungPanel.btnRemoveAnsprechpartnerActionPerformed().errortitle";
    public static final String BUNDLE_AP_ERRORTEXT = "BaumMeldungPanel.btnRemoveAnsprechpartnerActionPerformed().errortext";

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
        panMeldung = new JPanel();
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
        baumOrtsterminPanel1 = new BaumOrtsterminPanel();
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
        baumSchadenPanel1 = new BaumSchadenPanel();
        filler3 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));

        FormListener formListener = new FormListener();

        dlgAddApartner.setTitle(NbBundle.getMessage(BaumMeldungPanel_ALT.class, "BaumMeldungPanel_ALT.dlgAddApartner.title")); // NOI18N
        dlgAddApartner.setModal(true);
        dlgAddApartner.setName("dlgAddApartner"); // NOI18N

        panAddApartner.setName("panAddApartner"); // NOI18N
        panAddApartner.setLayout(new GridBagLayout());

        lblAuswaehlenApartner.setText(NbBundle.getMessage(BaumMeldungPanel_ALT.class, "BaumMeldungPanel_ALT.lblAuswaehlenApartner.text")); // NOI18N
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

        btnMenAbortApartner.setText(NbBundle.getMessage(BaumMeldungPanel_ALT.class, "BaumMeldungPanel_ALT.btnMenAbortApartner.text")); // NOI18N
        btnMenAbortApartner.setName("btnMenAbortApartner"); // NOI18N
        btnMenAbortApartner.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsApartner.add(btnMenAbortApartner, gridBagConstraints);

        btnMenOkApartner.setText(NbBundle.getMessage(BaumMeldungPanel_ALT.class, "BaumMeldungPanel_ALT.btnMenOkApartner.text")); // NOI18N
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

        dlgAddOrtstermin.setTitle(NbBundle.getMessage(BaumMeldungPanel_ALT.class, "BaumMeldungPanel_ALT.dlgAddOrtstermin.title")); // NOI18N
        dlgAddOrtstermin.setModal(true);
        dlgAddOrtstermin.setName("dlgAddOrtstermin"); // NOI18N

        panAddOrtstermin.setName("panAddOrtstermin"); // NOI18N
        panAddOrtstermin.setLayout(new GridBagLayout());

        lblAuswaehlenOrtstermin.setText(NbBundle.getMessage(BaumMeldungPanel_ALT.class, "BaumMeldungPanel_ALT.lblAuswaehlenOrtstermin.text")); // NOI18N
        lblAuswaehlenOrtstermin.setName("lblAuswaehlenOrtstermin"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panAddOrtstermin.add(lblAuswaehlenOrtstermin, gridBagConstraints);

        panMenButtonsOrtstermin.setName("panMenButtonsOrtstermin"); // NOI18N
        panMenButtonsOrtstermin.setLayout(new GridBagLayout());

        btnMenAbortOrtstermin.setText(NbBundle.getMessage(BaumMeldungPanel_ALT.class, "BaumMeldungPanel_ALT.btnMenAbortOrtstermin.text")); // NOI18N
        btnMenAbortOrtstermin.setName("btnMenAbortOrtstermin"); // NOI18N
        btnMenAbortOrtstermin.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsOrtstermin.add(btnMenAbortOrtstermin, gridBagConstraints);

        btnMenOkOrtstermin.setText(NbBundle.getMessage(BaumMeldungPanel_ALT.class, "BaumMeldungPanel_ALT.btnMenOkOrtstermin.text")); // NOI18N
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

        panMeldung.setName("panMeldung"); // NOI18N
        panMeldung.setOpaque(false);
        panMeldung.setLayout(new GridBagLayout());

        lblApartner.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblApartner, NbBundle.getMessage(BaumMeldungPanel_ALT.class, "BaumMeldungPanel_ALT.lblApartner.text")); // NOI18N
        lblApartner.setName("lblApartner"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panMeldung.add(lblApartner, gridBagConstraints);

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
        panMeldung.add(panApartner, gridBagConstraints);

        lblBemerkung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblBemerkung, NbBundle.getMessage(BaumMeldungPanel_ALT.class, "BaumMeldungPanel_ALT.lblBemerkung.text")); // NOI18N
        lblBemerkung.setName("lblBemerkung"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panMeldung.add(lblBemerkung, gridBagConstraints);

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
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panMeldung.add(scpBemerkung, gridBagConstraints);

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

        lstOrtstermine.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstOrtstermine.setFixedCellWidth(75);
        lstOrtstermine.setName("lstOrtstermine"); // NOI18N

        eLProperty = ELProperty.create("${cidsBean." + FIELD__ORTSTERMINE + "}");
        jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, lstOrtstermine);
        bindingGroup.addBinding(jListBinding);

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
        lblOrtstermine.setText(NbBundle.getMessage(BaumMeldungPanel_ALT.class, "BaumMeldungPanel_ALT.lblOrtstermine.text")); // NOI18N
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
        lblOrtstermin.setText(NbBundle.getMessage(BaumMeldungPanel_ALT.class, "BaumMeldungPanel_ALT.lblOrtstermin.text")); // NOI18N
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

        baumOrtsterminPanel1.setName("baumOrtsterminPanel1"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, lstOrtstermine, ELProperty.create("${selectedElement}"), baumOrtsterminPanel1, BeanProperty.create("cidsBean"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panOrtstermineMain.add(baumOrtsterminPanel1, gridBagConstraints);

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
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 0, 0, 0);
        panMeldung.add(panOrtstermin, gridBagConstraints);

        panSchaden.setName("panSchaden"); // NOI18N
        panSchaden.setOpaque(false);
        panSchaden.setLayout(new GridBagLayout());

        rpSchadenliste.setMinimumSize(new Dimension(80, 202));
        rpSchadenliste.setName("rpSchadenliste"); // NOI18N
        rpSchadenliste.setPreferredSize(new Dimension(100, 202));
        rpSchadenliste.setLayout(new GridBagLayout());

        scpLaufendeSchaeden.setName("scpLaufendeSchaeden"); // NOI18N

        lstSchaeden.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstSchaeden.setFixedCellWidth(75);
        lstSchaeden.setName("lstSchaeden"); // NOI18N

        eLProperty = ELProperty.create("${cidsBean." + FIELD__SCHAEDEN + "}");
        jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, lstSchaeden);
        bindingGroup.addBinding(jListBinding);

        scpLaufendeSchaeden.setViewportView(lstSchaeden);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        rpSchadenliste.add(scpLaufendeSchaeden, gridBagConstraints);

        semiRoundedPanelSchaden.setBackground(Color.darkGray);
        semiRoundedPanelSchaden.setMinimumSize(new Dimension(60, 25));
        semiRoundedPanelSchaden.setName("semiRoundedPanelSchaden"); // NOI18N
        semiRoundedPanelSchaden.setLayout(new GridBagLayout());

        lblSchaeden.setForeground(new Color(255, 255, 255));
        lblSchaeden.setText(NbBundle.getMessage(BaumMeldungPanel_ALT.class, "BaumMeldungPanel_ALT.lblSchaeden.text")); // NOI18N
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
            .addGap(0, 1, Short.MAX_VALUE)
        );

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        semiRoundedPanelSchaden.add(jPanel9, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
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
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panControlsNewSchaden.add(btnRemoveSchaden, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 0, 5, 0);
        rpSchadenliste.add(panControlsNewSchaden, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 0, 5);
        panSchaden.add(rpSchadenliste, gridBagConstraints);

        rpSchadeninfo.setName("rpSchadeninfo"); // NOI18N
        rpSchadeninfo.setLayout(new GridBagLayout());

        semiRoundedPanel6.setBackground(Color.darkGray);
        semiRoundedPanel6.setName("semiRoundedPanel6"); // NOI18N
        semiRoundedPanel6.setLayout(new GridBagLayout());

        lblSchaden.setForeground(new Color(255, 255, 255));
        lblSchaden.setText(NbBundle.getMessage(BaumMeldungPanel_ALT.class, "BaumMeldungPanel_ALT.lblSchaden.text")); // NOI18N
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

        baumSchadenPanel1.setName("baumSchadenPanel1"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, lstSchaeden, ELProperty.create("${selectedElement}"), baumSchadenPanel1, BeanProperty.create("cidsBean"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.weightx = 1.0;
        panSchaedenMain.add(baumSchadenPanel1, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        rpSchadeninfo.add(panSchaedenMain, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 8.0;
        gridBagConstraints.insets = new Insets(0, 5, 0, 0);
        panSchaden.add(rpSchadeninfo, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 0, 0, 0);
        panMeldung.add(panSchaden, gridBagConstraints);

        add(panMeldung, new GridBagConstraints());

        filler3.setName("filler3"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        add(filler3, gridBagConstraints);

        bindingGroup.bind();
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements ActionListener {
        FormListener() {}
        public void actionPerformed(ActionEvent evt) {
            if (evt.getSource() == btnAddApartner) {
                BaumMeldungPanel_ALT.this.btnAddApartnerActionPerformed(evt);
            }
            else if (evt.getSource() == btnRemoveApartner) {
                BaumMeldungPanel_ALT.this.btnRemoveApartnerActionPerformed(evt);
            }
            else if (evt.getSource() == btnAddNewOrtstermin) {
                BaumMeldungPanel_ALT.this.btnAddNewOrtsterminActionPerformed(evt);
            }
            else if (evt.getSource() == btnRemoveOrtstermin) {
                BaumMeldungPanel_ALT.this.btnRemoveOrtsterminActionPerformed(evt);
            }
            else if (evt.getSource() == btnAddNewSchaden) {
                BaumMeldungPanel_ALT.this.btnAddNewSchadenActionPerformed(evt);
            }
            else if (evt.getSource() == btnRemoveSchaden) {
                BaumMeldungPanel_ALT.this.btnRemoveSchadenActionPerformed(evt);
            }
            else if (evt.getSource() == btnMenAbortApartner) {
                BaumMeldungPanel_ALT.this.btnMenAbortApartnerActionPerformed(evt);
            }
            else if (evt.getSource() == btnMenOkApartner) {
                BaumMeldungPanel_ALT.this.btnMenOkApartnerActionPerformed(evt);
            }
            else if (evt.getSource() == btnMenAbortOrtstermin) {
                BaumMeldungPanel_ALT.this.btnMenAbortOrtsterminActionPerformed(evt);
            }
            else if (evt.getSource() == btnMenOkOrtstermin) {
                BaumMeldungPanel_ALT.this.btnMenOkOrtsterminActionPerformed(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddApartnerActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddApartnerActionPerformed
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(BaumMeldungPanel_ALT.this), dlgAddApartner, true);
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
            StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(BaumMeldungPanel_ALT.this), dlgAddOrtstermin, true);
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

            //Meldungen erweitern:
            ortsterminBeans.add(beanOrtstermin);

            //Refresh:

            //bindingGroup.unbind();
            //Collections.sort((List)ortsterminBeans, DATE_COMPARATOR);
            //bindingGroup.bind();
            lstOrtstermine.setSelectedValue(beanOrtstermin, true);

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

            //Ersatzpflanzungen erweitern:
            schadenBeans.add(beanSchaden);

            //Refresh:
            lstSchaeden.setSelectedValue(beanSchaden, true);
        } catch (Exception e) {
            LOG.error("Cannot add new BaumSchaden object", e);
        }
    }//GEN-LAST:event_btnAddNewSchadenActionPerformed

    private void btnRemoveSchadenActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveSchadenActionPerformed
        final Object selectedObject = lstSchaeden.getSelectedValue();

        if (selectedObject instanceof CidsBean) {

            if (schadenBeans != null) {
                schadenBeans.remove((CidsBean)selectedObject);
                if (schadenBeans != null && schadenBeans.size() > 0) {
                    lstSchaeden.setSelectedIndex(0);
                }else{
                    lstSchaeden.clearSelection();
                }
            }
        }
    }//GEN-LAST:event_btnRemoveSchadenActionPerformed

    //~ Instance fields --------------------------------------------------------
    private final boolean isEditor;
    private final BaumGebietEditor parentEditor;
    private final ConnectionContext connectionContext;
    private CidsBean cidsBean;
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    BaumOrtsterminPanel baumOrtsterminPanel1;
    BaumSchadenPanel baumSchadenPanel1;
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
    Box.Filler filler3;
    JPanel jPanel8;
    JPanel jPanel9;
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
    JPanel panMeldung;
    JPanel panMenButtonsApartner;
    JPanel panMenButtonsOrtstermin;
    JPanel panOrtstermin;
    JPanel panOrtstermineMain;
    JPanel panSchaden;
    JPanel panSchaedenMain;
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
    public BaumMeldungPanel_ALT() {
        this(null,true);
    }

    
    /**
     * Creates new form BaumMeldungPanel.
     *
     * @param parentEditor
     * @param  editable  DOCUMENT ME!
     */
    public BaumMeldungPanel_ALT(final BaumGebietEditor parentEditor, final boolean editable) {
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
    public BaumMeldungPanel_ALT(final BaumGebietEditor parentEditor, final boolean editable,
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

   

   
    

    /**
     * DOCUMENT ME!
     *
     * @param  panel  DOCUMENT ME!
     */
    public void removeMeldungPanel(final BaumMeldungPanel_ALT panel) {
        if (panel != null) {
            //cidsBeans.remove(panel.getCidsBean());
            panMeldung.remove(panel);
            panMeldung.repaint();
        }
    }

        
    @Override
    public void dispose() {
        bindingGroup.unbind();
        cidsBean = null;
        dlgAddApartner.dispose();
        dlgAddOrtstermin.dispose();
    }

    @Override
    public CidsBean getCidsBean() {
        return this.cidsBean;
    }

    @Override
    public void setCidsBean(CidsBean cidsBean) {
        bindingGroup.unbind();
        this.cidsBean = cidsBean;
        if (this.cidsBean != null){
            setOrtsterminBeans(cidsBean.getBeanCollectionProperty(FIELD__ORTSTERMINE)); 
            setSchadenBeans(cidsBean.getBeanCollectionProperty(FIELD__SCHAEDEN));   
        }
     /*   if (ortsterminBeans != null) {
            Collections.sort((List)ortsterminBeans, DATE_COMPARATOR);
        }*/
        bindingGroup.bind();
        
        if (ortsterminBeans != null && ortsterminBeans.size() > 0) {
            lstOrtstermine.setSelectedIndex(0);
        }
        if (schadenBeans != null && schadenBeans.size() > 0) {
            lstSchaeden.setSelectedIndex(0);
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
                    compoDatum.setForeground(Color.red);
                    return compoDatum;
                }
            });
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
                    final Component compoDatum = super.getListCellRendererComponent(list, newValue, index, isSelected, cellHasFocus);
                    compoDatum.setForeground(Color.red);
                    return compoDatum;
                }
            });
        
        dlgAddOrtstermin.pack();
        dlgAddOrtstermin.getRootPane().setDefaultButton(btnMenOkOrtstermin);
        dlgAddApartner.pack();
        dlgAddApartner.getRootPane().setDefaultButton(btnMenOkApartner);
    }
    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeans  DOCUMENT ME!
     */
    public void setOrtsterminBeans(final List<CidsBean> cidsBeans) {
        this.ortsterminBeans = cidsBeans;
        baumOrtsterminPanel1.setCidsBean(null);
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> getOrtsterminBeans() {
        return ortsterminBeans;
    }
    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeans  DOCUMENT ME!
     */
    public void setSchadenBeans(final List<CidsBean> cidsBeans) {
        this.schadenBeans = cidsBeans;
        baumSchadenPanel1.setCidsBean(null);
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> getSchadenBeans() {
        return schadenBeans;
    }
}
