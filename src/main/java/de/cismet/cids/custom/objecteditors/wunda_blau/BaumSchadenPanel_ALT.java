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

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import com.vividsolutions.jts.geom.Point;
import de.cismet.cids.client.tools.DevelopmentTools;
import de.cismet.cids.custom.objecteditors.utils.BaumConfProperties;
import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objecteditors.utils.TableUtils;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel;
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
import de.cismet.cids.editors.DefaultBindableScrollableComboBox;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.navigator.utils.ClassCacheMultiple;
import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;
import de.cismet.cismap.commons.BoundingBox;
import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;
import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.SemiRoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
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
public class BaumSchadenPanel_ALT extends javax.swing.JPanel implements Disposable, CidsBeanStore, ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------
    private List<CidsBean> ersatzBeans;
    private static final Logger LOG = Logger.getLogger(BaumSchadenPanel_ALT.class);
    
    
    public static final String FIELD__ERSATZ = "n_ersatz";                      // baum_schaden
    public static final String FIELD__ALTER = "alter";                          // baum_schaden
    public static final String FIELD__HOEHE = "hoehe";                          // baum_schaden
    public static final String FIELD__UMFANG = "umfang";                        // baum_schaden
    public static final String FIELD__ART = "fk_art";                           // baum_schaden
    public static final String FIELD__GEOM = "fk_geom";                         // baum_schaden
    public static final String FIELD__PRIVAT = "privatbaum";                    // baum_schaden
    public static final String FIELD__OHNE = "ohne_schaden";                    // baum_schaden
    public static final String FIELD__KRONE = "kronenschaden";                  // baum_schaden
    public static final String FIELD__STAMM = "stammschaden";                   // baum_schaden
    public static final String FIELD__WURZEL = "wurzelschaden";                 // baum_schaden
    public static final String FIELD__KRONE_ARR = "arr_krone";                  // baum_schaden
    public static final String FIELD__STAMM_ARR = "arr_stamm";                  // baum_schaden
    public static final String FIELD__WURZEL_ARR = "arr_wurzel";                // baum_schaden
    public static final String FIELD__MASSNAHME = "arr_massnahme";              // baum_schaden
    public static final String FIELD__STURM = "sturmschaden";                   // baum_schaden
    public static final String FIELD__ABGESTORBEN = "abgestorben";              // baum_schaden
    public static final String FIELD__BAU = "baumassnahme";                     // baum_schaden
    public static final String FIELD__GUTACHTEN = "gutachten";                  // baum_schaden
    public static final String FIELD__BERATUNG = "baumberatung";                // baum_schaden
    public static final String FIELD__BEMERKUNG = "bemerkung";                  // baum_schaden
    public static final String FIELD__BETRAG = "betrag";                        // baum_schaden
    public static final String FIELD__EINGANG = "eingegangen";                  // baum_schaden
    public static final String FIELD__FAELLUNG = "faellung";                    // baum_schaden
    public static final String FIELD__ID = "id";                                // baum_ersatz
    
    public static final String FIELD__GEO_FIELD = "geo_field";                      // geom
    public static final String FIELD__GEOREFERENZ__GEO_FIELD = "fk_geom.geo_field"; // baum_schaden_geom
    
    public static final String TABLE_GEOM = "geom";
    
    public static final String BUNDLE_W_QUESTION = "BaumSchadenPanel.btnRemoveWurzelActionPerformed().question";
    public static final String BUNDLE_W_TITLE = "BaumSchadenPanel.btnRemoveWurzelActionPerformed().title";
    public static final String BUNDLE_W_ERRORTITLE = "BaumSchadenPanel.btnRemoveWurzelActionPerformed().errortitle";
    public static final String BUNDLE_W_ERRORTEXT = "BaumSchadenPanel.btnRemoveWurzelActionPerformed().errortext";
    public static final String BUNDLE_S_QUESTION = "BaumSchadenPanel.btnRemoveStammActionPerformed().question";
    public static final String BUNDLE_S_TITLE = "BaumSchadenPanel.btnRemoveStammActionPerformed().title";
    public static final String BUNDLE_S_ERRORTITLE = "BaumSchadenPanel.btnRemoveStammActionPerformed().errortitle";
    public static final String BUNDLE_S_ERRORTEXT = "BaumSchadenPanel.btnRemoveStammActionPerformed().errortext";
    public static final String BUNDLE_K_QUESTION = "BaumSchadenPanel.btnRemoveKroneActionPerformed().question";
    public static final String BUNDLE_K_TITLE = "BaumSchadenPanel.btnRemoveKroneActionPerformed().title";
    public static final String BUNDLE_K_ERRORTITLE = "BaumSchadenPanel.btnRemoveKroneActionPerformed().errortitle";
    public static final String BUNDLE_K_ERRORTEXT = "BaumSchadenPanel.btnRemoveKroneActionPerformed().errortext";
    public static final String BUNDLE_M_QUESTION = "BaumSchadenPanel.btnRemoveMassnahmeActionPerformed().question";
    public static final String BUNDLE_M_TITLE = "BaumSchadenPanel.btnRemoveMassnahmeActionPerformed().title";
    public static final String BUNDLE_M_ERRORTITLE = "BaumSchadenPanel.btnRemoveMassnahmeActionPerformed().errortitle";
    public static final String BUNDLE_M_ERRORTEXT = "BaumSchadenPanel.btnRemoveMassnahmeActionPerformed().errortext";
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        GridBagConstraints gridBagConstraints;
        bindingGroup = new BindingGroup();

        dlgAddMassnahme = new JDialog();
        panAddMassnahme = new JPanel();
        lblAuswaehlenMassnahme = new JLabel();
        final MetaObject[] massnahme = ObjectRendererUtils.getLightweightMetaObjectsForTable("baum_massnahme", new String[]{"name"}, getConnectionContext());
        if(massnahme != null) {
            Arrays.sort(massnahme);
            cbMassnahme = new JComboBox(massnahme);
        }
        panMenButtonsMassnahme = new JPanel();
        btnMenAbortMassnahme = new JButton();
        btnMenOkMassnahme = new JButton();
        dlgAddWurzel = new JDialog();
        panAddWurzel = new JPanel();
        lblAuswaehlenWurzel = new JLabel();
        final MetaObject[] wurzel = ObjectRendererUtils.getLightweightMetaObjectsForTable("baum_wurzel", new String[]{"name"}, getConnectionContext());
        if(wurzel != null) {
            Arrays.sort(wurzel);
            cbWurzel = new JComboBox(wurzel);
        }
        panMenButtonsWurzel = new JPanel();
        btnMenAbortWurzel = new JButton();
        btnMenOkWurzel = new JButton();
        dlgAddStamm = new JDialog();
        panAddStamm = new JPanel();
        lblAuswaehlenStamm = new JLabel();
        final MetaObject[] stamm = ObjectRendererUtils.getLightweightMetaObjectsForTable("baum_stamm", new String[]{"name"}, getConnectionContext());
        if(stamm != null) {
            Arrays.sort(stamm);
            cbStamm = new JComboBox(stamm);
        }
        panMenButtonsStamm = new JPanel();
        btnMenAbortStamm = new JButton();
        btnMenOkStamm = new JButton();
        dlgAddKrone = new JDialog();
        panAddKrone = new JPanel();
        lblAuswaehlenKrone = new JLabel();
        final MetaObject[] krone = ObjectRendererUtils.getLightweightMetaObjectsForTable("baum_krone", new String[]{"name"}, getConnectionContext());
        if(krone != null) {
            Arrays.sort(krone);
            cbKrone = new JComboBox(krone);
        }
        panMenButtonsKrone = new JPanel();
        btnMenAbortKrone = new JButton();
        btnMenOkKrone = new JButton();
        panSchaden = new JPanel();
        lblAlter = new JLabel();
        spAlter = new JSpinner();
        lblHoehe = new JLabel();
        ftxtHoehe = new JFormattedTextField();
        lblUmfang = new JLabel();
        ftxtUmfang = new JFormattedTextField();
        lblPrivat = new JLabel();
        chPrivat = new JCheckBox();
        lblGeom = new JLabel();
        if (isEditor){
            cbGeom = new DefaultCismapGeometryComboBoxEditor();
        }
        panGeometrie = new JPanel();
        panLage = new JPanel();
        rpKarte = new RoundedPanel();
        panPreviewMap = new DefaultPreviewMapPanel();
        semiRoundedPanel7 = new SemiRoundedPanel();
        lblKarte = new JLabel();
        lblOhne = new JLabel();
        chOhne = new JCheckBox();
        lblKrone = new JLabel();
        chKrone = new JCheckBox();
        lblStamm = new JLabel();
        chStamm = new JCheckBox();
        lblWurzel = new JLabel();
        chWurzel = new JCheckBox();
        lblSturm = new JLabel();
        chSturm = new JCheckBox();
        chAbgest = new JCheckBox();
        lblAbgest = new JLabel();
        lblBau = new JLabel();
        chBau = new JCheckBox();
        lblGutachten = new JLabel();
        chGutachten = new JCheckBox();
        lblBeratung = new JLabel();
        lblBemerkung = new JLabel();
        chBeratung = new JCheckBox();
        scpBemerkung = new JScrollPane();
        taBemerkung = new JTextArea();
        lblFaellung = new JLabel();
        chFaellung = new JCheckBox();
        lblWurzelArr = new JLabel();
        panWurzel = new JPanel();
        scpWurzel = new JScrollPane();
        lstWurzel = new JList();
        panButtonsWurzel = new JPanel();
        btnAddWurzel = new JButton();
        btnRemoveWurzel = new JButton();
        filler1 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
        lblStammArr = new JLabel();
        panStamm = new JPanel();
        scpStamm = new JScrollPane();
        lstStamm = new JList();
        panButtonsStamm = new JPanel();
        btnAddStamm = new JButton();
        btnRemoveStamm = new JButton();
        filler2 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
        lblKroneArr = new JLabel();
        panKrone = new JPanel();
        scpKrone = new JScrollPane();
        lstKrone = new JList();
        panButtonsKrone = new JPanel();
        btnAddKrone = new JButton();
        btnRemoveKrone = new JButton();
        filler4 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
        lblMassnahmeArr = new JLabel();
        panMassnahme = new JPanel();
        scpMassnahme = new JScrollPane();
        lstMassnahme = new JList();
        panButtonsMassnahme = new JPanel();
        btnAddMassnahme = new JButton();
        btnRemoveMassnahme = new JButton();
        filler5 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
        panErsatz = new JPanel();
        rpErsatzliste = new RoundedPanel();
        scpLaufendeErsatz = new JScrollPane();
        lstErsatz = new JList();
        semiRoundedPanelErsatz = new SemiRoundedPanel();
        lblErsatz = new JLabel();
        panControlsNewErsatz = new JPanel();
        btnAddNewErsatz = new JButton();
        btnRemoveErsatz = new JButton();
        rpErsatzinfo = new RoundedPanel();
        semiRoundedPanel5 = new SemiRoundedPanel();
        lblErsatzanzeige = new JLabel();
        panErsatzMain = new JPanel();
        baumErsatzPanel1 = new BaumErsatzPanel();
        filler3 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        lblBetrag = new JLabel();
        ftxtBetrag = new JFormattedTextField();
        semiRoundedPanel6 = new SemiRoundedPanel();
        lblErsatzzahlung = new JLabel();
        lblEingang = new JLabel();
        chEingang = new JCheckBox();
        lblArt = new JLabel();
        cbArt = new DefaultBindableScrollableComboBox();

        FormListener formListener = new FormListener();

        dlgAddMassnahme.setTitle(NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.dlgAddMassnahme.title")); // NOI18N
        dlgAddMassnahme.setModal(true);
        dlgAddMassnahme.setName("dlgAddMassnahme"); // NOI18N

        panAddMassnahme.setName("panAddMassnahme"); // NOI18N
        panAddMassnahme.setLayout(new GridBagLayout());

        lblAuswaehlenMassnahme.setText(NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.lblAuswaehlenMassnahme.text")); // NOI18N
        lblAuswaehlenMassnahme.setName("lblAuswaehlenMassnahme"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panAddMassnahme.add(lblAuswaehlenMassnahme, gridBagConstraints);

        cbMassnahme.setName("cbMassnahme"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panAddMassnahme.add(cbMassnahme, gridBagConstraints);

        panMenButtonsMassnahme.setName("panMenButtonsMassnahme"); // NOI18N
        panMenButtonsMassnahme.setLayout(new GridBagLayout());

        btnMenAbortMassnahme.setText(NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.btnMenAbortMassnahme.text")); // NOI18N
        btnMenAbortMassnahme.setName("btnMenAbortMassnahme"); // NOI18N
        btnMenAbortMassnahme.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsMassnahme.add(btnMenAbortMassnahme, gridBagConstraints);

        btnMenOkMassnahme.setText(NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.btnMenOkMassnahme.text")); // NOI18N
        btnMenOkMassnahme.setName("btnMenOkMassnahme"); // NOI18N
        btnMenOkMassnahme.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsMassnahme.add(btnMenOkMassnahme, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panAddMassnahme.add(panMenButtonsMassnahme, gridBagConstraints);

        dlgAddMassnahme.getContentPane().add(panAddMassnahme, BorderLayout.CENTER);

        dlgAddWurzel.setTitle(NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.dlgAddWurzel.title")); // NOI18N
        dlgAddWurzel.setModal(true);
        dlgAddWurzel.setName("dlgAddWurzel"); // NOI18N

        panAddWurzel.setName("panAddWurzel"); // NOI18N
        panAddWurzel.setLayout(new GridBagLayout());

        lblAuswaehlenWurzel.setText(NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.lblAuswaehlenWurzel.text")); // NOI18N
        lblAuswaehlenWurzel.setName("lblAuswaehlenWurzel"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panAddWurzel.add(lblAuswaehlenWurzel, gridBagConstraints);

        cbWurzel.setName("cbWurzel"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panAddWurzel.add(cbWurzel, gridBagConstraints);

        panMenButtonsWurzel.setName("panMenButtonsWurzel"); // NOI18N
        panMenButtonsWurzel.setLayout(new GridBagLayout());

        btnMenAbortWurzel.setText(NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.btnMenAbortWurzel.text")); // NOI18N
        btnMenAbortWurzel.setName("btnMenAbortWurzel"); // NOI18N
        btnMenAbortWurzel.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsWurzel.add(btnMenAbortWurzel, gridBagConstraints);

        btnMenOkWurzel.setText(NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.btnMenOkWurzel.text")); // NOI18N
        btnMenOkWurzel.setName("btnMenOkWurzel"); // NOI18N
        btnMenOkWurzel.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsWurzel.add(btnMenOkWurzel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panAddWurzel.add(panMenButtonsWurzel, gridBagConstraints);

        dlgAddWurzel.getContentPane().add(panAddWurzel, BorderLayout.CENTER);

        dlgAddStamm.setTitle(NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.dlgAddStamm.title")); // NOI18N
        dlgAddStamm.setModal(true);
        dlgAddStamm.setName("dlgAddStamm"); // NOI18N

        panAddStamm.setName("panAddStamm"); // NOI18N
        panAddStamm.setLayout(new GridBagLayout());

        lblAuswaehlenStamm.setText(NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.lblAuswaehlenStamm.text")); // NOI18N
        lblAuswaehlenStamm.setName("lblAuswaehlenStamm"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panAddStamm.add(lblAuswaehlenStamm, gridBagConstraints);

        cbStamm.setName("cbStamm"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panAddStamm.add(cbStamm, gridBagConstraints);

        panMenButtonsStamm.setName("panMenButtonsStamm"); // NOI18N
        panMenButtonsStamm.setLayout(new GridBagLayout());

        btnMenAbortStamm.setText(NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.btnMenAbortStamm.text")); // NOI18N
        btnMenAbortStamm.setName("btnMenAbortStamm"); // NOI18N
        btnMenAbortStamm.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsStamm.add(btnMenAbortStamm, gridBagConstraints);

        btnMenOkStamm.setText(NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.btnMenOkStamm.text")); // NOI18N
        btnMenOkStamm.setName("btnMenOkStamm"); // NOI18N
        btnMenOkStamm.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsStamm.add(btnMenOkStamm, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panAddStamm.add(panMenButtonsStamm, gridBagConstraints);

        dlgAddStamm.getContentPane().add(panAddStamm, BorderLayout.CENTER);

        dlgAddKrone.setTitle(NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.dlgAddKrone.title")); // NOI18N
        dlgAddKrone.setModal(true);
        dlgAddKrone.setName("dlgAddKrone"); // NOI18N

        panAddKrone.setName("panAddKrone"); // NOI18N
        panAddKrone.setLayout(new GridBagLayout());

        lblAuswaehlenKrone.setText(NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.lblAuswaehlenKrone.text")); // NOI18N
        lblAuswaehlenKrone.setName("lblAuswaehlenKrone"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panAddKrone.add(lblAuswaehlenKrone, gridBagConstraints);

        cbKrone.setName("cbKrone"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panAddKrone.add(cbKrone, gridBagConstraints);

        panMenButtonsKrone.setName("panMenButtonsKrone"); // NOI18N
        panMenButtonsKrone.setLayout(new GridBagLayout());

        btnMenAbortKrone.setText(NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.btnMenAbortKrone.text")); // NOI18N
        btnMenAbortKrone.setName("btnMenAbortKrone"); // NOI18N
        btnMenAbortKrone.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsKrone.add(btnMenAbortKrone, gridBagConstraints);

        btnMenOkKrone.setText(NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.btnMenOkKrone.text")); // NOI18N
        btnMenOkKrone.setName("btnMenOkKrone"); // NOI18N
        btnMenOkKrone.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsKrone.add(btnMenOkKrone, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panAddKrone.add(panMenButtonsKrone, gridBagConstraints);

        dlgAddKrone.getContentPane().add(panAddKrone, BorderLayout.CENTER);

        setName("Form"); // NOI18N
        setOpaque(false);
        setLayout(new GridBagLayout());

        panSchaden.setName("panSchaden"); // NOI18N
        panSchaden.setOpaque(false);
        panSchaden.setLayout(new GridBagLayout());

        lblAlter.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblAlter, NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.lblAlter.text")); // NOI18N
        lblAlter.setName("lblAlter"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblAlter, gridBagConstraints);

        spAlter.setFont(new Font("Dialog", 0, 12)); // NOI18N
        spAlter.setModel(new SpinnerNumberModel(0, 0, 100, 1));
        spAlter.setName("spAlter"); // NOI18N

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean." + FIELD__ALTER + "}"), spAlter, BeanProperty.create("value"));
        binding.setSourceNullValue(0d);
        binding.setSourceUnreadableValue(0d);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(spAlter, gridBagConstraints);

        lblHoehe.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblHoehe, NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.lblHoehe.text")); // NOI18N
        lblHoehe.setName("lblHoehe"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblHoehe, gridBagConstraints);

        ftxtHoehe.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(new DecimalFormat("#####"))));
        ftxtHoehe.setName("ftxtHoehe"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean." + FIELD__HOEHE + "}"), ftxtHoehe, BeanProperty.create("value"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(ftxtHoehe, gridBagConstraints);

        lblUmfang.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblUmfang, NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.lblUmfang.text")); // NOI18N
        lblUmfang.setName("lblUmfang"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblUmfang, gridBagConstraints);

        ftxtUmfang.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(new DecimalFormat("#####"))));
        ftxtUmfang.setName("ftxtUmfang"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean." + FIELD__UMFANG + "}"), ftxtUmfang, BeanProperty.create("value"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(ftxtUmfang, gridBagConstraints);

        lblPrivat.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblPrivat, NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.lblPrivat.text")); // NOI18N
        lblPrivat.setName("lblPrivat"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblPrivat, gridBagConstraints);

        chPrivat.setContentAreaFilled(false);
        chPrivat.setName("chPrivat"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean." + FIELD__PRIVAT + "}"), chPrivat, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chPrivat, gridBagConstraints);

        lblGeom.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblGeom, NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.lblGeom.text")); // NOI18N
        lblGeom.setName("lblGeom"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblGeom, gridBagConstraints);

        if (isEditor){
            cbGeom.setFont(new Font("Dialog", 0, 12)); // NOI18N
            cbGeom.setName("cbGeom"); // NOI18N

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_geom}"), cbGeom, BeanProperty.create("selectedItem"));
            binding.setSourceNullValue(null);
            binding.setSourceUnreadableValue(null);
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeom).getConverter());
            bindingGroup.addBinding(binding);

        }
        if (isEditor){
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 5;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            panSchaden.add(cbGeom, gridBagConstraints);
        }

        panGeometrie.setName("panGeometrie"); // NOI18N
        panGeometrie.setOpaque(false);
        panGeometrie.setLayout(new GridBagLayout());

        panLage.setMinimumSize(new Dimension(300, 142));
        panLage.setName("panLage"); // NOI18N
        panLage.setOpaque(false);
        panLage.setLayout(new GridBagLayout());

        rpKarte.setName(""); // NOI18N
        rpKarte.setLayout(new GridBagLayout());

        panPreviewMap.setName("panPreviewMap"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        rpKarte.add(panPreviewMap, gridBagConstraints);

        semiRoundedPanel7.setBackground(Color.darkGray);
        semiRoundedPanel7.setName("semiRoundedPanel7"); // NOI18N
        semiRoundedPanel7.setLayout(new GridBagLayout());

        lblKarte.setForeground(new Color(255, 255, 255));
        Mnemonics.setLocalizedText(lblKarte, NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.lblKarte.text")); // NOI18N
        lblKarte.setName("lblKarte"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(5, 10, 5, 5);
        semiRoundedPanel7.add(lblKarte, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpKarte.add(semiRoundedPanel7, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panLage.add(rpKarte, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panGeometrie.add(panLage, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 5, 5);
        panSchaden.add(panGeometrie, gridBagConstraints);

        lblOhne.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblOhne, NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.lblOhne.text")); // NOI18N
        lblOhne.setName("lblOhne"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblOhne, gridBagConstraints);

        chOhne.setContentAreaFilled(false);
        chOhne.setName("chOhne"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean." + FIELD__OHNE + "}"), chOhne, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chOhne, gridBagConstraints);

        lblKrone.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblKrone, NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.lblKrone.text")); // NOI18N
        lblKrone.setName("lblKrone"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblKrone, gridBagConstraints);

        chKrone.setContentAreaFilled(false);
        chKrone.setName("chKrone"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean." + FIELD__KRONE + "}"), chKrone, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chKrone, gridBagConstraints);

        lblStamm.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblStamm, NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.lblStamm.text")); // NOI18N
        lblStamm.setName("lblStamm"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblStamm, gridBagConstraints);

        chStamm.setContentAreaFilled(false);
        chStamm.setName("chStamm"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean." + FIELD__STAMM + "}"), chStamm, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chStamm, gridBagConstraints);

        lblWurzel.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblWurzel, NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.lblWurzel.text")); // NOI18N
        lblWurzel.setName("lblWurzel"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblWurzel, gridBagConstraints);

        chWurzel.setContentAreaFilled(false);
        chWurzel.setName("chWurzel"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean." + FIELD__WURZEL + "}"), chWurzel, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chWurzel, gridBagConstraints);

        lblSturm.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblSturm, NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.lblSturm.text")); // NOI18N
        lblSturm.setName("lblSturm"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblSturm, gridBagConstraints);

        chSturm.setContentAreaFilled(false);
        chSturm.setName("chSturm"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean." + FIELD__STURM + "}"), chSturm, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chSturm, gridBagConstraints);

        chAbgest.setContentAreaFilled(false);
        chAbgest.setName("chAbgest"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean." + FIELD__ABGESTORBEN + "}"), chAbgest, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chAbgest, gridBagConstraints);

        lblAbgest.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblAbgest, NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.lblAbgest.text")); // NOI18N
        lblAbgest.setName("lblAbgest"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblAbgest, gridBagConstraints);

        lblBau.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblBau, NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.lblBau.text")); // NOI18N
        lblBau.setName("lblBau"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblBau, gridBagConstraints);

        chBau.setContentAreaFilled(false);
        chBau.setName("chBau"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean." + FIELD__BAU + "}"), chBau, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chBau, gridBagConstraints);

        lblGutachten.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblGutachten, NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.lblGutachten.text")); // NOI18N
        lblGutachten.setName("lblGutachten"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblGutachten, gridBagConstraints);

        chGutachten.setContentAreaFilled(false);
        chGutachten.setName("chGutachten"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean." + FIELD__GUTACHTEN + "}"), chGutachten, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chGutachten, gridBagConstraints);

        lblBeratung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblBeratung, NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.lblBeratung.text")); // NOI18N
        lblBeratung.setName("lblBeratung"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblBeratung, gridBagConstraints);

        lblBemerkung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblBemerkung, NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.lblBemerkung.text")); // NOI18N
        lblBemerkung.setName("lblBemerkung"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblBemerkung, gridBagConstraints);

        chBeratung.setContentAreaFilled(false);
        chBeratung.setName("chBeratung"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean." + FIELD__BERATUNG + "}"), chBeratung, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chBeratung, gridBagConstraints);

        scpBemerkung.setName("scpBemerkung"); // NOI18N
        scpBemerkung.setOpaque(false);

        taBemerkung.setLineWrap(true);
        taBemerkung.setRows(2);
        taBemerkung.setWrapStyleWord(true);
        taBemerkung.setName("taBemerkung"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean." + FIELD__BEMERKUNG + "}"), taBemerkung, BeanProperty.create("text"));
        binding.setSourceNullValue("null");
        binding.setSourceUnreadableValue("null");
        bindingGroup.addBinding(binding);

        scpBemerkung.setViewportView(taBemerkung);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panSchaden.add(scpBemerkung, gridBagConstraints);

        lblFaellung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblFaellung, NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.lblFaellung.text")); // NOI18N
        lblFaellung.setName("lblFaellung"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblFaellung, gridBagConstraints);

        chFaellung.setContentAreaFilled(false);
        chFaellung.setName("chFaellung"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean." + FIELD__FAELLUNG + "}"), chFaellung, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chFaellung, gridBagConstraints);

        lblWurzelArr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblWurzelArr, NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.lblWurzelArr.text")); // NOI18N
        lblWurzelArr.setName("lblWurzelArr"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblWurzelArr, gridBagConstraints);

        panWurzel.setName("panWurzel"); // NOI18N
        panWurzel.setOpaque(false);
        panWurzel.setLayout(new GridBagLayout());

        scpWurzel.setMinimumSize(new Dimension(258, 66));
        scpWurzel.setName("scpWurzel"); // NOI18N

        lstWurzel.setFont(new Font("Dialog", 0, 12)); // NOI18N
        lstWurzel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstWurzel.setName("lstWurzel"); // NOI18N
        lstWurzel.setVisibleRowCount(3);

        ELProperty eLProperty = ELProperty.create("${cidsBean.arr_wurzel}");
        JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, lstWurzel);
        bindingGroup.addBinding(jListBinding);

        scpWurzel.setViewportView(lstWurzel);

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
        panWurzel.add(scpWurzel, gridBagConstraints);

        panButtonsWurzel.setName("panButtonsWurzel"); // NOI18N
        panButtonsWurzel.setOpaque(false);
        panButtonsWurzel.setLayout(new GridBagLayout());

        btnAddWurzel.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddWurzel.setName("btnAddWurzel"); // NOI18N
        btnAddWurzel.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panButtonsWurzel.add(btnAddWurzel, gridBagConstraints);

        btnRemoveWurzel.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveWurzel.setName("btnRemoveWurzel"); // NOI18N
        btnRemoveWurzel.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panButtonsWurzel.add(btnRemoveWurzel, gridBagConstraints);

        filler1.setName("filler1"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panButtonsWurzel.add(filler1, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(10, 2, 2, 2);
        panWurzel.add(panButtonsWurzel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panSchaden.add(panWurzel, gridBagConstraints);

        lblStammArr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblStammArr, NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.lblStammArr.text")); // NOI18N
        lblStammArr.setName("lblStammArr"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblStammArr, gridBagConstraints);

        panStamm.setName("panStamm"); // NOI18N
        panStamm.setOpaque(false);
        panStamm.setLayout(new GridBagLayout());

        scpStamm.setMinimumSize(new Dimension(258, 66));
        scpStamm.setName("scpStamm"); // NOI18N

        lstStamm.setFont(new Font("Dialog", 0, 12)); // NOI18N
        lstStamm.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstStamm.setName("lstStamm"); // NOI18N
        lstStamm.setVisibleRowCount(3);

        eLProperty = ELProperty.create("${cidsBean.arr_stamm}");
        jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, lstStamm);
        bindingGroup.addBinding(jListBinding);

        scpStamm.setViewportView(lstStamm);

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
        panStamm.add(scpStamm, gridBagConstraints);

        panButtonsStamm.setName("panButtonsStamm"); // NOI18N
        panButtonsStamm.setOpaque(false);
        panButtonsStamm.setLayout(new GridBagLayout());

        btnAddStamm.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddStamm.setName("btnAddStamm"); // NOI18N
        btnAddStamm.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panButtonsStamm.add(btnAddStamm, gridBagConstraints);

        btnRemoveStamm.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveStamm.setName("btnRemoveStamm"); // NOI18N
        btnRemoveStamm.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panButtonsStamm.add(btnRemoveStamm, gridBagConstraints);

        filler2.setName("filler2"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panButtonsStamm.add(filler2, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(10, 2, 2, 2);
        panStamm.add(panButtonsStamm, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panSchaden.add(panStamm, gridBagConstraints);

        lblKroneArr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblKroneArr, NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.lblKroneArr.text")); // NOI18N
        lblKroneArr.setName("lblKroneArr"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblKroneArr, gridBagConstraints);

        panKrone.setName("panKrone"); // NOI18N
        panKrone.setOpaque(false);
        panKrone.setLayout(new GridBagLayout());

        scpKrone.setMinimumSize(new Dimension(258, 66));
        scpKrone.setName("scpKrone"); // NOI18N

        lstKrone.setFont(new Font("Dialog", 0, 12)); // NOI18N
        lstKrone.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstKrone.setName("lstKrone"); // NOI18N
        lstKrone.setVisibleRowCount(3);

        eLProperty = ELProperty.create("${cidsBean.arr_krone}");
        jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, lstKrone);
        bindingGroup.addBinding(jListBinding);

        scpKrone.setViewportView(lstKrone);

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
        panKrone.add(scpKrone, gridBagConstraints);

        panButtonsKrone.setName("panButtonsKrone"); // NOI18N
        panButtonsKrone.setOpaque(false);
        panButtonsKrone.setLayout(new GridBagLayout());

        btnAddKrone.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddKrone.setName("btnAddKrone"); // NOI18N
        btnAddKrone.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panButtonsKrone.add(btnAddKrone, gridBagConstraints);

        btnRemoveKrone.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveKrone.setName("btnRemoveKrone"); // NOI18N
        btnRemoveKrone.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panButtonsKrone.add(btnRemoveKrone, gridBagConstraints);

        filler4.setName("filler4"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panButtonsKrone.add(filler4, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(10, 2, 2, 2);
        panKrone.add(panButtonsKrone, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panSchaden.add(panKrone, gridBagConstraints);

        lblMassnahmeArr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblMassnahmeArr, NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.lblMassnahmeArr.text")); // NOI18N
        lblMassnahmeArr.setName("lblMassnahmeArr"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblMassnahmeArr, gridBagConstraints);

        panMassnahme.setName("panMassnahme"); // NOI18N
        panMassnahme.setOpaque(false);
        panMassnahme.setLayout(new GridBagLayout());

        scpMassnahme.setMinimumSize(new Dimension(258, 66));
        scpMassnahme.setName("scpMassnahme"); // NOI18N

        lstMassnahme.setFont(new Font("Dialog", 0, 12)); // NOI18N
        lstMassnahme.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstMassnahme.setName("lstMassnahme"); // NOI18N
        lstMassnahme.setVisibleRowCount(3);

        eLProperty = ELProperty.create("${cidsBean.arr_massnahme}");
        jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, lstMassnahme);
        bindingGroup.addBinding(jListBinding);

        scpMassnahme.setViewportView(lstMassnahme);

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
        panMassnahme.add(scpMassnahme, gridBagConstraints);

        panButtonsMassnahme.setName("panButtonsMassnahme"); // NOI18N
        panButtonsMassnahme.setOpaque(false);
        panButtonsMassnahme.setLayout(new GridBagLayout());

        btnAddMassnahme.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddMassnahme.setName("btnAddMassnahme"); // NOI18N
        btnAddMassnahme.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panButtonsMassnahme.add(btnAddMassnahme, gridBagConstraints);

        btnRemoveMassnahme.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveMassnahme.setName("btnRemoveMassnahme"); // NOI18N
        btnRemoveMassnahme.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panButtonsMassnahme.add(btnRemoveMassnahme, gridBagConstraints);

        filler5.setName("filler5"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panButtonsMassnahme.add(filler5, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(10, 2, 2, 2);
        panMassnahme.add(panButtonsMassnahme, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panSchaden.add(panMassnahme, gridBagConstraints);

        panErsatz.setName("panErsatz"); // NOI18N
        panErsatz.setOpaque(false);
        panErsatz.setLayout(new GridBagLayout());

        rpErsatzliste.setMinimumSize(new Dimension(80, 202));
        rpErsatzliste.setName("rpErsatzliste"); // NOI18N
        rpErsatzliste.setPreferredSize(new Dimension(100, 202));
        rpErsatzliste.setLayout(new GridBagLayout());

        scpLaufendeErsatz.setName("scpLaufendeErsatz"); // NOI18N

        lstErsatz.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstErsatz.setFixedCellWidth(75);
        lstErsatz.setName("lstErsatz"); // NOI18N

        eLProperty = ELProperty.create("${cidsBean." + FIELD__ERSATZ + "}");
        jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, lstErsatz);
        bindingGroup.addBinding(jListBinding);

        lstErsatz.addPropertyChangeListener(formListener);
        lstErsatz.addListSelectionListener(formListener);
        scpLaufendeErsatz.setViewportView(lstErsatz);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        rpErsatzliste.add(scpLaufendeErsatz, gridBagConstraints);

        semiRoundedPanelErsatz.setBackground(Color.darkGray);
        semiRoundedPanelErsatz.setMinimumSize(new Dimension(60, 25));
        semiRoundedPanelErsatz.setName("semiRoundedPanelErsatz"); // NOI18N
        semiRoundedPanelErsatz.setLayout(new GridBagLayout());

        lblErsatz.setForeground(new Color(255, 255, 255));
        lblErsatz.setText(NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.lblErsatz.text")); // NOI18N
        lblErsatz.setName("lblErsatz"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        semiRoundedPanelErsatz.add(lblErsatz, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpErsatzliste.add(semiRoundedPanelErsatz, gridBagConstraints);

        panControlsNewErsatz.setName("panControlsNewErsatz"); // NOI18N
        panControlsNewErsatz.setOpaque(false);
        panControlsNewErsatz.setLayout(new GridBagLayout());

        btnAddNewErsatz.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddNewErsatz.setMaximumSize(new Dimension(39, 20));
        btnAddNewErsatz.setMinimumSize(new Dimension(39, 20));
        btnAddNewErsatz.setName("btnAddNewErsatz"); // NOI18N
        btnAddNewErsatz.setPreferredSize(new Dimension(39, 25));
        btnAddNewErsatz.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panControlsNewErsatz.add(btnAddNewErsatz, gridBagConstraints);

        btnRemoveErsatz.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveErsatz.setMaximumSize(new Dimension(39, 20));
        btnRemoveErsatz.setMinimumSize(new Dimension(39, 20));
        btnRemoveErsatz.setName("btnRemoveErsatz"); // NOI18N
        btnRemoveErsatz.setPreferredSize(new Dimension(39, 25));
        btnRemoveErsatz.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panControlsNewErsatz.add(btnRemoveErsatz, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 0, 5, 0);
        rpErsatzliste.add(panControlsNewErsatz, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 0, 5);
        panErsatz.add(rpErsatzliste, gridBagConstraints);

        rpErsatzinfo.setName("rpErsatzinfo"); // NOI18N
        rpErsatzinfo.setLayout(new GridBagLayout());

        semiRoundedPanel5.setBackground(Color.darkGray);
        semiRoundedPanel5.setName("semiRoundedPanel5"); // NOI18N
        semiRoundedPanel5.setLayout(new GridBagLayout());

        lblErsatzanzeige.setForeground(new Color(255, 255, 255));
        lblErsatzanzeige.setText(NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.lblErsatzanzeige.text")); // NOI18N
        lblErsatzanzeige.setName("lblErsatzanzeige"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        semiRoundedPanel5.add(lblErsatzanzeige, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpErsatzinfo.add(semiRoundedPanel5, gridBagConstraints);

        panErsatzMain.setName("panErsatzMain"); // NOI18N
        panErsatzMain.setOpaque(false);
        panErsatzMain.setLayout(new GridBagLayout());

        baumErsatzPanel1.setName("baumErsatzPanel1"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, lstErsatz, ELProperty.create("${selectedElement}"), baumErsatzPanel1, BeanProperty.create("cidsBean"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panErsatzMain.add(baumErsatzPanel1, gridBagConstraints);

        filler3.setName("filler3"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panErsatzMain.add(filler3, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        rpErsatzinfo.add(panErsatzMain, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 8.0;
        gridBagConstraints.insets = new Insets(0, 5, 0, 0);
        panErsatz.add(rpErsatzinfo, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 0, 0, 0);
        panSchaden.add(panErsatz, gridBagConstraints);

        lblBetrag.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblBetrag, NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.lblBetrag.text")); // NOI18N
        lblBetrag.setName("lblBetrag"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 21;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblBetrag, gridBagConstraints);

        ftxtBetrag.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(new DecimalFormat("#####"))));
        ftxtBetrag.setName("ftxtBetrag"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean." + FIELD__BETRAG + "}"), ftxtBetrag, BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 21;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(ftxtBetrag, gridBagConstraints);

        semiRoundedPanel6.setBackground(Color.darkGray);
        semiRoundedPanel6.setName("semiRoundedPanel6"); // NOI18N
        semiRoundedPanel6.setLayout(new GridBagLayout());

        lblErsatzzahlung.setForeground(new Color(255, 255, 255));
        lblErsatzzahlung.setText(NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.lblErsatzzahlung.text")); // NOI18N
        lblErsatzzahlung.setName("lblErsatzzahlung"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        semiRoundedPanel6.add(lblErsatzzahlung, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(10, 0, 0, 0);
        panSchaden.add(semiRoundedPanel6, gridBagConstraints);

        lblEingang.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblEingang, NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.lblEingang.text")); // NOI18N
        lblEingang.setName("lblEingang"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 21;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblEingang, gridBagConstraints);

        chEingang.setContentAreaFilled(false);
        chEingang.setName("chEingang"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean." + FIELD__EINGANG + "}"), chEingang, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 21;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chEingang, gridBagConstraints);

        lblArt.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblArt, NbBundle.getMessage(BaumSchadenPanel_ALT.class, "BaumSchadenPanel_ALT.lblArt.text")); // NOI18N
        lblArt.setName("lblArt"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblArt, gridBagConstraints);

        cbArt.setFont(new Font("Dialog", 0, 12)); // NOI18N
        cbArt.setName("cbArt"); // NOI18N
        cbArt.setPreferredSize(new Dimension(100, 24));

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean." + FIELD__ART + "}"), cbArt, BeanProperty.create("selectedItem"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(cbArt, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(panSchaden, gridBagConstraints);

        bindingGroup.bind();
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements ActionListener, PropertyChangeListener, ListSelectionListener {
        FormListener() {}
        public void actionPerformed(ActionEvent evt) {
            if (evt.getSource() == btnAddWurzel) {
                BaumSchadenPanel_ALT.this.btnAddWurzelActionPerformed(evt);
            }
            else if (evt.getSource() == btnRemoveWurzel) {
                BaumSchadenPanel_ALT.this.btnRemoveWurzelActionPerformed(evt);
            }
            else if (evt.getSource() == btnAddStamm) {
                BaumSchadenPanel_ALT.this.btnAddStammActionPerformed(evt);
            }
            else if (evt.getSource() == btnRemoveStamm) {
                BaumSchadenPanel_ALT.this.btnRemoveStammActionPerformed(evt);
            }
            else if (evt.getSource() == btnAddKrone) {
                BaumSchadenPanel_ALT.this.btnAddKroneActionPerformed(evt);
            }
            else if (evt.getSource() == btnRemoveKrone) {
                BaumSchadenPanel_ALT.this.btnRemoveKroneActionPerformed(evt);
            }
            else if (evt.getSource() == btnAddMassnahme) {
                BaumSchadenPanel_ALT.this.btnAddMassnahmeActionPerformed(evt);
            }
            else if (evt.getSource() == btnRemoveMassnahme) {
                BaumSchadenPanel_ALT.this.btnRemoveMassnahmeActionPerformed(evt);
            }
            else if (evt.getSource() == btnAddNewErsatz) {
                BaumSchadenPanel_ALT.this.btnAddNewErsatzActionPerformed(evt);
            }
            else if (evt.getSource() == btnRemoveErsatz) {
                BaumSchadenPanel_ALT.this.btnRemoveErsatzActionPerformed(evt);
            }
            else if (evt.getSource() == btnMenAbortMassnahme) {
                BaumSchadenPanel_ALT.this.btnMenAbortMassnahmeActionPerformed(evt);
            }
            else if (evt.getSource() == btnMenOkMassnahme) {
                BaumSchadenPanel_ALT.this.btnMenOkMassnahmeActionPerformed(evt);
            }
            else if (evt.getSource() == btnMenAbortWurzel) {
                BaumSchadenPanel_ALT.this.btnMenAbortWurzelActionPerformed(evt);
            }
            else if (evt.getSource() == btnMenOkWurzel) {
                BaumSchadenPanel_ALT.this.btnMenOkWurzelActionPerformed(evt);
            }
            else if (evt.getSource() == btnMenAbortStamm) {
                BaumSchadenPanel_ALT.this.btnMenAbortStammActionPerformed(evt);
            }
            else if (evt.getSource() == btnMenOkStamm) {
                BaumSchadenPanel_ALT.this.btnMenOkStammActionPerformed(evt);
            }
            else if (evt.getSource() == btnMenAbortKrone) {
                BaumSchadenPanel_ALT.this.btnMenAbortKroneActionPerformed(evt);
            }
            else if (evt.getSource() == btnMenOkKrone) {
                BaumSchadenPanel_ALT.this.btnMenOkKroneActionPerformed(evt);
            }
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getSource() == lstErsatz) {
                BaumSchadenPanel_ALT.this.lstErsatzPropertyChange(evt);
            }
        }

        public void valueChanged(ListSelectionEvent evt) {
            if (evt.getSource() == lstErsatz) {
                BaumSchadenPanel_ALT.this.lstErsatzValueChanged(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddNewErsatzActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddNewErsatzActionPerformed
        try{
        //ersatzBean erzeugen und vorbelegen:
            final CidsBean beanErsatz = CidsBean.createNewCidsBeanFromTableName(
                "WUNDA_BLAU",
                "BAUM_ERSATZ",
                getConnectionContext());

            //Ersatzpflanzungen erweitern:
            ersatzBeans.add(beanErsatz);

            //Refresh:
            lstErsatz.setSelectedValue(beanErsatz, true);
        } catch (Exception e) {
            LOG.error("Cannot add new BaumErsatz object", e);
        }
    }//GEN-LAST:event_btnAddNewErsatzActionPerformed

    private void btnRemoveErsatzActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveErsatzActionPerformed
        final Object selectedObject = lstErsatz.getSelectedValue();

        if (selectedObject instanceof CidsBean) {

            if (ersatzBeans != null) {
                ersatzBeans.remove((CidsBean)selectedObject);
                //((CustomJListModel)lstMeldungen.getModel()).refresh();
                //lstMeldungen.getSelectionModel().clearSelection();
                if (ersatzBeans != null && ersatzBeans.size() > 0) {
                    lstErsatz.setSelectedIndex(0);
                }else{
                    lstErsatz.clearSelection();
                }
            }
        }
    }//GEN-LAST:event_btnRemoveErsatzActionPerformed

    private void lstErsatzValueChanged(ListSelectionEvent evt) {//GEN-FIRST:event_lstErsatzValueChanged
        //baumErsatzPanel1.dispose();
    }//GEN-LAST:event_lstErsatzValueChanged

    private void lstErsatzPropertyChange(PropertyChangeEvent evt) {//GEN-FIRST:event_lstErsatzPropertyChange
        //baumErsatzPanel1.dispose();
    }//GEN-LAST:event_lstErsatzPropertyChange

    private void btnMenAbortMassnahmeActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnMenAbortMassnahmeActionPerformed
        dlgAddMassnahme.setVisible(false);
    }//GEN-LAST:event_btnMenAbortMassnahmeActionPerformed

    private void btnMenOkMassnahmeActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnMenOkMassnahmeActionPerformed
        try {
            final Object selItem = cbMassnahme.getSelectedItem();
            if (selItem instanceof MetaObject) {
                cidsBean = TableUtils.addBeanToCollectionWithMessage(StaticSwingTools.getParentFrame(this),
                    cidsBean,
                    FIELD__MASSNAHME,
                    ((MetaObject)selItem).getBean());
            }
        } catch (Exception ex) {
            LOG.error(ex, ex);
        } finally {
            dlgAddMassnahme.setVisible(false);
        }
    }//GEN-LAST:event_btnMenOkMassnahmeActionPerformed

    private void btnMenAbortWurzelActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnMenAbortWurzelActionPerformed
        dlgAddWurzel.setVisible(false);
    }//GEN-LAST:event_btnMenAbortWurzelActionPerformed

    private void btnMenOkWurzelActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnMenOkWurzelActionPerformed
        try {
            final Object selItem = cbWurzel.getSelectedItem();
            if (selItem instanceof MetaObject) {
                cidsBean = TableUtils.addBeanToCollectionWithMessage(StaticSwingTools.getParentFrame(this),
                    cidsBean,
                    FIELD__WURZEL_ARR,
                    ((MetaObject)selItem).getBean());
            }
        } catch (Exception ex) {
            LOG.error(ex, ex);
        } finally {
            dlgAddWurzel.setVisible(false);
        }
    }//GEN-LAST:event_btnMenOkWurzelActionPerformed

    private void btnMenAbortStammActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnMenAbortStammActionPerformed
        dlgAddStamm.setVisible(false);
    }//GEN-LAST:event_btnMenAbortStammActionPerformed

    private void btnMenOkStammActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnMenOkStammActionPerformed
        try {
            final Object selItem = cbStamm.getSelectedItem();
            if (selItem instanceof MetaObject) {
                cidsBean = TableUtils.addBeanToCollectionWithMessage(StaticSwingTools.getParentFrame(this),
                    cidsBean,
                    FIELD__STAMM_ARR,
                    ((MetaObject)selItem).getBean());
            }
        } catch (Exception ex) {
            LOG.error(ex, ex);
        } finally {
            dlgAddStamm.setVisible(false);
        }
    }//GEN-LAST:event_btnMenOkStammActionPerformed

    private void btnMenAbortKroneActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnMenAbortKroneActionPerformed
        dlgAddKrone.setVisible(false);
    }//GEN-LAST:event_btnMenAbortKroneActionPerformed

    private void btnMenOkKroneActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnMenOkKroneActionPerformed
        try {
            final Object selItem = cbKrone.getSelectedItem();
            if (selItem instanceof MetaObject) {
                cidsBean = TableUtils.addBeanToCollectionWithMessage(StaticSwingTools.getParentFrame(this),
                    cidsBean,
                    FIELD__KRONE_ARR,
                    ((MetaObject)selItem).getBean());
            }
        } catch (Exception ex) {
            LOG.error(ex, ex);
        } finally {
            dlgAddKrone.setVisible(false);
        }
    }//GEN-LAST:event_btnMenOkKroneActionPerformed

    private void btnAddWurzelActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddWurzelActionPerformed
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(BaumSchadenPanel_ALT.this), dlgAddWurzel, true);
    }//GEN-LAST:event_btnAddWurzelActionPerformed

    private void btnRemoveWurzelActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveWurzelActionPerformed
        final Object selection = lstWurzel.getSelectedValue();
        if (selection != null) {
            final int answer = JOptionPane.showConfirmDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(BaumSchadenPanel_ALT.class, BUNDLE_W_QUESTION),
                NbBundle.getMessage(BaumSchadenPanel_ALT.class, BUNDLE_W_TITLE),
                JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                try {
                    cidsBean = TableUtils.deleteItemFromList(cidsBean, FIELD__WURZEL_ARR, selection, false);
                } catch (Exception ex) {
                    final ErrorInfo ei = new ErrorInfo(
                        BUNDLE_W_ERRORTITLE,
                        BUNDLE_W_ERRORTEXT,
                        null,
                        null,
                        ex,
                        Level.SEVERE,
                        null);
                    JXErrorPane.showDialog(this, ei);
                }
            }
        }
    }//GEN-LAST:event_btnRemoveWurzelActionPerformed

    private void btnAddStammActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddStammActionPerformed
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(BaumSchadenPanel_ALT.this), dlgAddStamm, true);
    }//GEN-LAST:event_btnAddStammActionPerformed

    private void btnRemoveStammActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveStammActionPerformed
        final Object selection = lstStamm.getSelectedValue();
        if (selection != null) {
            final int answer = JOptionPane.showConfirmDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(BaumSchadenPanel_ALT.class, BUNDLE_S_QUESTION),
                NbBundle.getMessage(BaumSchadenPanel_ALT.class, BUNDLE_S_TITLE),
                JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                try {
                    cidsBean = TableUtils.deleteItemFromList(cidsBean, FIELD__STAMM_ARR, selection, false);
                } catch (Exception ex) {
                    final ErrorInfo ei = new ErrorInfo(
                        BUNDLE_S_ERRORTITLE,
                        BUNDLE_S_ERRORTEXT,
                        null,
                        null,
                        ex,
                        Level.SEVERE,
                        null);
                    JXErrorPane.showDialog(this, ei);
                }
            }
        }
    }//GEN-LAST:event_btnRemoveStammActionPerformed

    private void btnAddKroneActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddKroneActionPerformed
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(BaumSchadenPanel_ALT.this), dlgAddKrone, true);
    }//GEN-LAST:event_btnAddKroneActionPerformed

    private void btnRemoveKroneActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveKroneActionPerformed
        final Object selection = lstKrone.getSelectedValue();
        if (selection != null) {
            final int answer = JOptionPane.showConfirmDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(BaumSchadenPanel_ALT.class, BUNDLE_K_QUESTION),
                NbBundle.getMessage(BaumSchadenPanel_ALT.class, BUNDLE_K_TITLE),
                JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                try {
                    cidsBean = TableUtils.deleteItemFromList(cidsBean, FIELD__KRONE_ARR, selection, false);
                } catch (Exception ex) {
                    final ErrorInfo ei = new ErrorInfo(
                        BUNDLE_K_ERRORTITLE,
                        BUNDLE_K_ERRORTEXT,
                        null,
                        null,
                        ex,
                        Level.SEVERE,
                        null);
                    JXErrorPane.showDialog(this, ei);
                }
            }
        }
    }//GEN-LAST:event_btnRemoveKroneActionPerformed

    private void btnAddMassnahmeActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddMassnahmeActionPerformed
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(BaumSchadenPanel_ALT.this), dlgAddMassnahme, true);
    }//GEN-LAST:event_btnAddMassnahmeActionPerformed

    private void btnRemoveMassnahmeActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveMassnahmeActionPerformed
        final Object selection = lstMassnahme.getSelectedValue();
        if (selection != null) {
            final int answer = JOptionPane.showConfirmDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(BaumSchadenPanel_ALT.class, BUNDLE_M_QUESTION),
                NbBundle.getMessage(BaumSchadenPanel_ALT.class, BUNDLE_M_TITLE),
                JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                try {
                    cidsBean = TableUtils.deleteItemFromList(cidsBean, FIELD__MASSNAHME, selection, false);
                } catch (Exception ex) {
                    final ErrorInfo ei = new ErrorInfo(
                        BUNDLE_M_ERRORTITLE,
                        BUNDLE_M_ERRORTEXT,
                        null,
                        null,
                        ex,
                        Level.SEVERE,
                        null);
                    JXErrorPane.showDialog(this, ei);
                }
            }
        }
    }//GEN-LAST:event_btnRemoveMassnahmeActionPerformed

    //~ Instance fields --------------------------------------------------------
    private final boolean isEditor;
    private final BaumMeldungPanel parentPanel;
    private final ConnectionContext connectionContext;
    private CidsBean cidsBean;
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    BaumErsatzPanel baumErsatzPanel1;
    JButton btnAddKrone;
    JButton btnAddMassnahme;
    JButton btnAddNewErsatz;
    JButton btnAddStamm;
    JButton btnAddWurzel;
    JButton btnMenAbortKrone;
    JButton btnMenAbortMassnahme;
    JButton btnMenAbortStamm;
    JButton btnMenAbortWurzel;
    JButton btnMenOkKrone;
    JButton btnMenOkMassnahme;
    JButton btnMenOkStamm;
    JButton btnMenOkWurzel;
    JButton btnRemoveErsatz;
    JButton btnRemoveKrone;
    JButton btnRemoveMassnahme;
    JButton btnRemoveStamm;
    JButton btnRemoveWurzel;
    JComboBox<String> cbArt;
    private JComboBox cbGeom;
    JComboBox cbKrone;
    JComboBox cbMassnahme;
    JComboBox cbStamm;
    JComboBox cbWurzel;
    JCheckBox chAbgest;
    JCheckBox chBau;
    JCheckBox chBeratung;
    JCheckBox chEingang;
    JCheckBox chFaellung;
    JCheckBox chGutachten;
    JCheckBox chKrone;
    JCheckBox chOhne;
    JCheckBox chPrivat;
    JCheckBox chStamm;
    JCheckBox chSturm;
    JCheckBox chWurzel;
    JDialog dlgAddKrone;
    JDialog dlgAddMassnahme;
    JDialog dlgAddStamm;
    JDialog dlgAddWurzel;
    Box.Filler filler1;
    Box.Filler filler2;
    Box.Filler filler3;
    Box.Filler filler4;
    Box.Filler filler5;
    JFormattedTextField ftxtBetrag;
    JFormattedTextField ftxtHoehe;
    JFormattedTextField ftxtUmfang;
    JLabel lblAbgest;
    JLabel lblAlter;
    JLabel lblArt;
    JLabel lblAuswaehlenKrone;
    JLabel lblAuswaehlenMassnahme;
    JLabel lblAuswaehlenStamm;
    JLabel lblAuswaehlenWurzel;
    JLabel lblBau;
    JLabel lblBemerkung;
    JLabel lblBeratung;
    JLabel lblBetrag;
    JLabel lblEingang;
    JLabel lblErsatz;
    JLabel lblErsatzanzeige;
    JLabel lblErsatzzahlung;
    JLabel lblFaellung;
    JLabel lblGeom;
    JLabel lblGutachten;
    JLabel lblHoehe;
    JLabel lblKarte;
    JLabel lblKrone;
    JLabel lblKroneArr;
    JLabel lblMassnahmeArr;
    JLabel lblOhne;
    JLabel lblPrivat;
    JLabel lblStamm;
    JLabel lblStammArr;
    JLabel lblSturm;
    JLabel lblUmfang;
    JLabel lblWurzel;
    JLabel lblWurzelArr;
    JList lstErsatz;
    JList lstKrone;
    JList lstMassnahme;
    JList lstStamm;
    JList lstWurzel;
    JPanel panAddKrone;
    JPanel panAddMassnahme;
    JPanel panAddStamm;
    JPanel panAddWurzel;
    JPanel panButtonsKrone;
    JPanel panButtonsMassnahme;
    JPanel panButtonsStamm;
    JPanel panButtonsWurzel;
    JPanel panControlsNewErsatz;
    JPanel panErsatz;
    JPanel panErsatzMain;
    JPanel panGeometrie;
    JPanel panKrone;
    JPanel panLage;
    JPanel panMassnahme;
    JPanel panMenButtonsKrone;
    JPanel panMenButtonsMassnahme;
    JPanel panMenButtonsStamm;
    JPanel panMenButtonsWurzel;
    DefaultPreviewMapPanel panPreviewMap;
    JPanel panSchaden;
    JPanel panStamm;
    JPanel panWurzel;
    RoundedPanel rpErsatzinfo;
    RoundedPanel rpErsatzliste;
    RoundedPanel rpKarte;
    JScrollPane scpBemerkung;
    JScrollPane scpKrone;
    JScrollPane scpLaufendeErsatz;
    JScrollPane scpMassnahme;
    JScrollPane scpStamm;
    JScrollPane scpWurzel;
    SemiRoundedPanel semiRoundedPanel5;
    SemiRoundedPanel semiRoundedPanel6;
    SemiRoundedPanel semiRoundedPanel7;
    SemiRoundedPanel semiRoundedPanelErsatz;
    JSpinner spAlter;
    JTextArea taBemerkung;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BaumSchadenPanel object.
     */
    public BaumSchadenPanel_ALT() {
        this(null,true);
    }

    
    /**
     * Creates new form BaumSchadenPanel.
     *
     * @param parentPanel
     * @param  editable  DOCUMENT ME!
     */
    public BaumSchadenPanel_ALT(final BaumMeldungPanel parentPanel, final boolean editable) {
        this.isEditor = editable;
        initComponents();
        this.connectionContext = null;
        this.parentPanel = parentPanel;
    }
 
    /**
     * Creates new form BaumSchadenPanel.
     *
     * @param parentPanel
     * @param  editable             DOCUMENT ME!
     * @param  connectionContext    DOCUMENT ME!
     */
    public BaumSchadenPanel_ALT(final BaumMeldungPanel parentPanel, final boolean editable,
            final ConnectionContext connectionContext) {
        this.isEditor = editable;
        this.connectionContext = connectionContext;
        initComponents();
        this.parentPanel = parentPanel;
    }

    //~ Methods ----------------------------------------------------------------
   /* public void initWithConnectionContext(final ConnectionContext connectionContext) {
        
        //super.initWithConnectionContext(connectionContext);
        //initComponents();

        
        
        
        if (isEditor) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).setLocalRenderFeatureString(FIELD__GEOM);
        }
        //setReadOnly();

   }*/
    
    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }

     
    @Override
    public void dispose() {
        bindingGroup.unbind();
        cidsBean = null;
        if (this.isEditor) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).dispose();
        }
        dlgAddMassnahme.dispose();
        dlgAddWurzel.dispose();
        dlgAddStamm.dispose();
        dlgAddKrone.dispose();
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
            setErsatzBeans(cidsBean.getBeanCollectionProperty(FIELD__ERSATZ));   
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                this.cidsBean,
                getConnectionContext());
        } else {
            setErsatzBeans(null);
        }
        setMapWindow();
        bindingGroup.bind();
        lstErsatz.setCellRenderer(new DefaultListCellRenderer() {

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
                    final Component compoTeil = super.getListCellRendererComponent(list, newValue, index, isSelected, cellHasFocus);
                    compoTeil.setForeground(Color.red);
                    return compoTeil;
                }
            });
        
        if (ersatzBeans != null && ersatzBeans.size() > 0) {
            lstErsatz.setSelectedIndex(0);
        }
        
        dlgAddMassnahme.pack();
        dlgAddMassnahme.getRootPane().setDefaultButton(btnMenOkMassnahme);
        dlgAddWurzel.pack();
        dlgAddWurzel.getRootPane().setDefaultButton(btnMenOkWurzel);
        dlgAddStamm.pack();
        dlgAddStamm.getRootPane().setDefaultButton(btnMenOkStamm);
        dlgAddKrone.pack();
        dlgAddKrone.getRootPane().setDefaultButton(btnMenOkKrone);
    }
    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeans  DOCUMENT ME!
     */
    public void setErsatzBeans(final List<CidsBean> cidsBeans) {
        this.ersatzBeans = cidsBeans;
        //baumErsatzPanel1.dispose();
        baumErsatzPanel1.setCidsBean(null);
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> getErsatzBeans() {
        return ersatzBeans;
    }
    /**
     * DOCUMENT ME!
     */
    public void setMapWindow() {
        final CidsBean cb = this.getCidsBean();
        if (cb != null){
            try {
                Double bufferMeter = 0.0;
                try{
                    bufferMeter = BaumConfProperties.getInstance().getBufferMeter();
                } catch (final Exception ex) {
                    Exceptions.printStackTrace(ex);
                    LOG.warn("Get no conf properties.", ex);
                }
                if (cb.getProperty(FIELD__GEOM) != null) {
                    panPreviewMap.initMap(cb, FIELD__GEOREFERENZ__GEO_FIELD, bufferMeter);
                } else {
                    final int srid = CrsTransformer.extractSridFromCrs(CismapBroker.getInstance().getSrs().getCode());
                    final BoundingBox initialBoundingBox;
                    initialBoundingBox = CismapBroker.getInstance().getMappingComponent().getMappingModel()
                                .getInitialBoundingBox();
                    final Point centerPoint = initialBoundingBox.getGeometry(srid).getCentroid();

                    final MetaClass geomMetaClass = ClassCacheMultiple.getMetaClass(
                            CidsBeanSupport.DOMAIN_NAME,
                            TABLE_GEOM,
                            getConnectionContext());
                    final CidsBean newGeom = geomMetaClass.getEmptyInstance(getConnectionContext()).getBean();
                    newGeom.setProperty(FIELD__GEO_FIELD, centerPoint);
                    panPreviewMap.initMap(newGeom, FIELD__GEO_FIELD, bufferMeter);
                }
            } catch (final Exception ex) {
                Exceptions.printStackTrace(ex);
                LOG.warn("Map window not set.", ex);
            } 
        }
    }
    public static void main(final String[] args) throws Exception {
        Log4JQuickConfig.configure4LumbermillOnLocalhost();
        final MappingComponent mc = new MappingComponent();
        CismapBroker.getInstance().setMappingComponent(mc);
        DevelopmentTools.createEditorFromRestfulConnection(
            DevelopmentTools.RESTFUL_CALLSERVER_CALLSERVER,
            "WUNDA_BLAU",
            null,
            true,
            "baum_schaden",
            1,
            800,
            600);
    }
}
