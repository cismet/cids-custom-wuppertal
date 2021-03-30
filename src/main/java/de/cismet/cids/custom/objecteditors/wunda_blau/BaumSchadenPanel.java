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

import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;
import de.cismet.cids.dynamics.Disposable;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.MissingResourceException;
import java.util.concurrent.ExecutionException;
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
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
public class BaumSchadenPanel extends javax.swing.JPanel implements Disposable, CidsBeanStore, ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------
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
    private List<CidsBean> ersatzBeans = new ArrayList<>();
    private final List<CidsBean> changedErsatzBeans = new ArrayList<>();
    private final List<CidsBean> deletedErsatzBeans = new ArrayList<>();
    private List<CidsBean> festBeans = new ArrayList<>();
    private final List<CidsBean> changedFestBeans = new ArrayList<>();
    private final List<CidsBean> deletedFestBeans = new ArrayList<>();
    private static final Logger LOG = Logger.getLogger(BaumSchadenPanel.class);
    
    
    //public static final String FIELD__ERSATZ = "n_ersatz";                      // baum_schaden
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
    public static final String FIELD__FK_SCHADEN = "fk_schaden";                // baum_ersatz
    public static final String FIELD__ID = "id";                                // baum_schaden
    
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
    public static final String BUNDLE_NOAGE = "BaumSchadenPanel.prepareForSave().noAlter";
    public static final String BUNDLE_WRONGAGE = "BaumSchadenPanel.prepareForSave().wrongAlter";
    
    public static final String BUNDLE_PANE_PREFIX =
        "BaumSchadenPanel.prepareForSave().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX =
        "BaumSchadenPanel.prepareForSave().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "BaumSchadenPanel.prepareForSave().JOptionPane.title";
    
    private static enum whatToDoForTree {

        //~ Enum constants -----------------------------------------------------

        ersatz, fest
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
        pnlCard1 = new JPanel();
        jTabbedPane = new JTabbedPane();
        jPanelAllgemein = new JPanel();
        panFillerUnten2 = new JPanel();
        panSchaden = new JPanel();
        lblAlter = new JLabel();
        txtAlter = new JTextField();
        lblHoehe = new JLabel();
        txtHoehe = new JTextField();
        lblUmfang = new JLabel();
        txtUmfang = new JTextField();
        lblPrivat = new JLabel();
        chPrivat = new JCheckBox();
        lblArt = new JLabel();
        lblGeom = new JLabel();
        if (isEditor){
            cbGeomSchaden = new DefaultCismapGeometryComboBoxEditor();
        }
        panGeometrie = new JPanel();
        panLage = new JPanel();
        rpKarte = new RoundedPanel();
        panPreviewMap = new DefaultPreviewMapPanel();
        semiRoundedPanel7 = new SemiRoundedPanel();
        lblKarte = new JLabel();
        lblOhne = new JLabel();
        chOhne = new JCheckBox();
        chKrone = new JCheckBox();
        chStamm = new JCheckBox();
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
        filler6 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        if(isEditor){
            cbArt = new DefaultBindableScrollableComboBox();
        }
        jPanelErsatzpflanzung = new JPanel();
        panFillerUnten3 = new JPanel();
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
        baumErsatzPanel = baumErsatzPanel = new BaumErsatzPanel(this, true);
        filler3 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        jPanelErsatzzahlung = new JPanel();
        panFillerUnten4 = new JPanel();
        lblBetrag = new JLabel();
        txtBetrag = new JTextField();
        lblEingang = new JLabel();
        chEingang = new JCheckBox();
        jPanelFestsetzung = new JPanel();
        panFillerUnten5 = new JPanel();
        panFest = new JPanel();
        rpFestliste = new RoundedPanel();
        scpLaufendeFest = new JScrollPane();
        lstFest = new JList();
        semiRoundedPanelFest = new SemiRoundedPanel();
        lblFest = new JLabel();
        panControlsNewFest = new JPanel();
        btnAddNewFest = new JButton();
        btnRemoveFest = new JButton();
        rpFestinfo = new RoundedPanel();
        semiRoundedPanel6 = new SemiRoundedPanel();
        lblFestanzeige = new JLabel();
        panFestMain = new JPanel();
        filler7 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        baumFestsetzungPanel = baumFestsetzungPanel = new BaumFestsetzungPanel(this, true);

        FormListener formListener = new FormListener();

        dlgAddMassnahme.setTitle("Maßnahmen");
        dlgAddMassnahme.setModal(true);
        dlgAddMassnahme.setName("dlgAddMassnahme"); // NOI18N

        panAddMassnahme.setName("panAddMassnahme"); // NOI18N
        panAddMassnahme.setLayout(new GridBagLayout());

        lblAuswaehlenMassnahme.setText("Bitte die Maßnahme auswählen:");
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

        btnMenAbortMassnahme.setText("Abbrechen");
        btnMenAbortMassnahme.setName("btnMenAbortMassnahme"); // NOI18N
        btnMenAbortMassnahme.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsMassnahme.add(btnMenAbortMassnahme, gridBagConstraints);

        btnMenOkMassnahme.setText("Ok");
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

        dlgAddWurzel.setTitle("Wurzelschaden");
        dlgAddWurzel.setModal(true);
        dlgAddWurzel.setName("dlgAddWurzel"); // NOI18N

        panAddWurzel.setName("panAddWurzel"); // NOI18N
        panAddWurzel.setLayout(new GridBagLayout());

        lblAuswaehlenWurzel.setText("Bitte den Wurzelschaden auswählen:");
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

        btnMenAbortWurzel.setText("Abbrechen");
        btnMenAbortWurzel.setName("btnMenAbortWurzel"); // NOI18N
        btnMenAbortWurzel.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsWurzel.add(btnMenAbortWurzel, gridBagConstraints);

        btnMenOkWurzel.setText("Ok");
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

        dlgAddStamm.setTitle("Stammschaden");
        dlgAddStamm.setModal(true);
        dlgAddStamm.setName("dlgAddStamm"); // NOI18N

        panAddStamm.setName("panAddStamm"); // NOI18N
        panAddStamm.setLayout(new GridBagLayout());

        lblAuswaehlenStamm.setText("Bitte den Stammschaden auswählen:");
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

        btnMenAbortStamm.setText("Abbrechen");
        btnMenAbortStamm.setName("btnMenAbortStamm"); // NOI18N
        btnMenAbortStamm.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsStamm.add(btnMenAbortStamm, gridBagConstraints);

        btnMenOkStamm.setText("Ok");
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

        dlgAddKrone.setTitle("Kronenschaden");
        dlgAddKrone.setModal(true);
        dlgAddKrone.setName("dlgAddKrone"); // NOI18N

        panAddKrone.setName("panAddKrone"); // NOI18N
        panAddKrone.setLayout(new GridBagLayout());

        lblAuswaehlenKrone.setText("Bitte den Kronenschaden auswählen:");
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

        btnMenAbortKrone.setText("Abbrechen");
        btnMenAbortKrone.setName("btnMenAbortKrone"); // NOI18N
        btnMenAbortKrone.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsKrone.add(btnMenAbortKrone, gridBagConstraints);

        btnMenOkKrone.setText("Ok");
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

        pnlCard1.setName("pnlCard1"); // NOI18N
        pnlCard1.setOpaque(false);
        pnlCard1.setLayout(new GridBagLayout());

        jTabbedPane.setName("jTabbedPane"); // NOI18N

        jPanelAllgemein.setName("jPanelAllgemein"); // NOI18N
        jPanelAllgemein.setOpaque(false);
        jPanelAllgemein.setLayout(new GridBagLayout());

        panFillerUnten2.setName(""); // NOI18N
        panFillerUnten2.setOpaque(false);

        GroupLayout panFillerUnten2Layout = new GroupLayout(panFillerUnten2);
        panFillerUnten2.setLayout(panFillerUnten2Layout);
        panFillerUnten2Layout.setHorizontalGroup(panFillerUnten2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panFillerUnten2Layout.setVerticalGroup(panFillerUnten2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        jPanelAllgemein.add(panFillerUnten2, gridBagConstraints);

        panSchaden.setName("panSchaden"); // NOI18N
        panSchaden.setOpaque(false);
        panSchaden.setLayout(new GridBagLayout());

        lblAlter.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblAlter, "Alter:");
        lblAlter.setName("lblAlter"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblAlter, gridBagConstraints);

        txtAlter.setName("txtAlter"); // NOI18N

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.alter}"), txtAlter, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(txtAlter, gridBagConstraints);

        lblHoehe.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblHoehe, "Höhe [m]:");
        lblHoehe.setName("lblHoehe"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblHoehe, gridBagConstraints);

        txtHoehe.setName("txtHoehe"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.hoehe}"), txtHoehe, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(txtHoehe, gridBagConstraints);

        lblUmfang.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblUmfang, "Umfang [cm]:");
        lblUmfang.setName("lblUmfang"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblUmfang, gridBagConstraints);

        txtUmfang.setName("txtUmfang"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.umfang}"), txtUmfang, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(txtUmfang, gridBagConstraints);

        lblPrivat.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblPrivat, "privat:");
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

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.privatbaum}"), chPrivat, BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chPrivat, gridBagConstraints);

        lblArt.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblArt, "Art:");
        lblArt.setName("lblArt"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblArt, gridBagConstraints);

        lblGeom.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblGeom, "Geometrie:");
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
            cbGeomSchaden.setFont(new Font("Dialog", 0, 12)); // NOI18N
            cbGeomSchaden.setName("cbGeomSchaden"); // NOI18N

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_geom}"), cbGeomSchaden, BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeomSchaden).getConverter());
            bindingGroup.addBinding(binding);

        }
        if (isEditor){
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 5;
            gridBagConstraints.gridwidth = 4;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            panSchaden.add(cbGeomSchaden, gridBagConstraints);
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
        gridBagConstraints.weighty = 1.0;
        rpKarte.add(panPreviewMap, gridBagConstraints);

        semiRoundedPanel7.setBackground(Color.darkGray);
        semiRoundedPanel7.setName("semiRoundedPanel7"); // NOI18N
        semiRoundedPanel7.setLayout(new GridBagLayout());

        lblKarte.setForeground(new Color(255, 255, 255));
        Mnemonics.setLocalizedText(lblKarte, "Lage");
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
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 5, 5);
        panSchaden.add(panGeometrie, gridBagConstraints);

        lblOhne.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblOhne, "ohne:");
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

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.ohne_schaden}"), chOhne, BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chOhne, gridBagConstraints);

        chKrone.setContentAreaFilled(false);
        chKrone.setName("chKrone"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.kronenschaden}"), chKrone, BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chKrone, gridBagConstraints);

        chStamm.setContentAreaFilled(false);
        chStamm.setName("chStamm"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.stammschaden}"), chStamm, BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chStamm, gridBagConstraints);

        chWurzel.setContentAreaFilled(false);
        chWurzel.setName("chWurzel"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.wurzelschaden}"), chWurzel, BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chWurzel, gridBagConstraints);

        lblSturm.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblSturm, "Sturm:");
        lblSturm.setName("lblSturm"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblSturm, gridBagConstraints);

        chSturm.setContentAreaFilled(false);
        chSturm.setName("chSturm"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.sturmschaden}"), chSturm, BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chSturm, gridBagConstraints);

        chAbgest.setContentAreaFilled(false);
        chAbgest.setName("chAbgest"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.abgestorben}"), chAbgest, BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chAbgest, gridBagConstraints);

        lblAbgest.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblAbgest, "abgestorben:");
        lblAbgest.setName("lblAbgest"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblAbgest, gridBagConstraints);

        lblBau.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblBau, "Bau:");
        lblBau.setName("lblBau"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblBau, gridBagConstraints);

        chBau.setContentAreaFilled(false);
        chBau.setName("chBau"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.baumassnahme}"), chBau, BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chBau, gridBagConstraints);

        lblGutachten.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblGutachten, "Gutachten vorh.:");
        lblGutachten.setName("lblGutachten"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblGutachten, gridBagConstraints);

        chGutachten.setContentAreaFilled(false);
        chGutachten.setName("chGutachten"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.gutachten}"), chGutachten, BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chGutachten, gridBagConstraints);

        lblBeratung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblBeratung, "Beratung:");
        lblBeratung.setName("lblBeratung"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblBeratung, gridBagConstraints);

        lblBemerkung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblBemerkung, "Bemerkung:");
        lblBemerkung.setName("lblBemerkung"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblBemerkung, gridBagConstraints);

        chBeratung.setContentAreaFilled(false);
        chBeratung.setName("chBeratung"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.baumberatung}"), chBeratung, BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
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

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.bemerkung}"), taBemerkung, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpBemerkung.setViewportView(taBemerkung);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        panSchaden.add(scpBemerkung, gridBagConstraints);

        lblFaellung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblFaellung, "Fällung:");
        lblFaellung.setName("lblFaellung"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblFaellung, gridBagConstraints);

        chFaellung.setContentAreaFilled(false);
        chFaellung.setName("chFaellung"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.faellung}"), chFaellung, BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chFaellung, gridBagConstraints);

        lblWurzelArr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblWurzelArr, "Wurzelschaden:");
        lblWurzelArr.setName("lblWurzelArr"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
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
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        panSchaden.add(panWurzel, gridBagConstraints);

        lblStammArr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblStammArr, "Stammschaden:");
        lblStammArr.setName("lblStammArr"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
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
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        panSchaden.add(panStamm, gridBagConstraints);

        lblKroneArr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblKroneArr, "Kronenschaden:");
        lblKroneArr.setName("lblKroneArr"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
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
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        panSchaden.add(panKrone, gridBagConstraints);

        lblMassnahmeArr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblMassnahmeArr, "Maßnahme:");
        lblMassnahmeArr.setName("lblMassnahmeArr"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 12;
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
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        panSchaden.add(panMassnahme, gridBagConstraints);

        filler6.setName("filler6"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panSchaden.add(filler6, gridBagConstraints);

        if(isEditor){
            cbArt.setFont(new Font("Dialog", 0, 12)); // NOI18N
            cbArt.setName("cbArt"); // NOI18N
            cbArt.setPreferredSize(new Dimension(100, 24));

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_art}"), cbArt, BeanProperty.create("selectedItem"));
            binding.setSourceNullValue(null);
            binding.setSourceUnreadableValue(null);
            bindingGroup.addBinding(binding);

        }
        if(isEditor){
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 4;
            gridBagConstraints.gridwidth = 4;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            panSchaden.add(cbArt, gridBagConstraints);
        }

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanelAllgemein.add(panSchaden, gridBagConstraints);

        jTabbedPane.addTab("Allgemeine Informationen", jPanelAllgemein);

        jPanelErsatzpflanzung.setName("jPanelErsatzpflanzung"); // NOI18N
        jPanelErsatzpflanzung.setOpaque(false);
        jPanelErsatzpflanzung.setLayout(new GridBagLayout());

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
        jPanelErsatzpflanzung.add(panFillerUnten3, gridBagConstraints);

        panErsatz.setName("panErsatz"); // NOI18N
        panErsatz.setOpaque(false);
        panErsatz.setLayout(new GridBagLayout());

        rpErsatzliste.setMinimumSize(new Dimension(80, 202));
        rpErsatzliste.setName("rpErsatzliste"); // NOI18N
        rpErsatzliste.setPreferredSize(new Dimension(100, 202));
        rpErsatzliste.setLayout(new GridBagLayout());

        scpLaufendeErsatz.setName("scpLaufendeErsatz"); // NOI18N

        lstErsatz.setModel(new DefaultListModel());
        lstErsatz.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstErsatz.setFixedCellWidth(75);
        lstErsatz.setName("lstErsatz"); // NOI18N
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
        lblErsatz.setText("Ersatz");
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
        lblErsatzanzeige.setText("Ersatzpflanzung");
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

        baumErsatzPanel.setName("baumErsatzPanel"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, lstErsatz, ELProperty.create("${selectedElement}"), baumErsatzPanel, BeanProperty.create("cidsBean"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panErsatzMain.add(baumErsatzPanel, gridBagConstraints);

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
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelErsatzpflanzung.add(panErsatz, gridBagConstraints);

        jTabbedPane.addTab("Ersatzpflanzung", jPanelErsatzpflanzung);

        jPanelErsatzzahlung.setName("jPanelErsatzzahlung"); // NOI18N
        jPanelErsatzzahlung.setOpaque(false);
        jPanelErsatzzahlung.setLayout(new GridBagLayout());

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
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        jPanelErsatzzahlung.add(panFillerUnten4, gridBagConstraints);

        lblBetrag.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblBetrag, "Betrag:");
        lblBetrag.setName("lblBetrag"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        jPanelErsatzzahlung.add(lblBetrag, gridBagConstraints);

        txtBetrag.setName("txtBetrag"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.betrag}"), txtBetrag, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        jPanelErsatzzahlung.add(txtBetrag, gridBagConstraints);

        lblEingang.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblEingang, "eingegangen:");
        lblEingang.setName("lblEingang"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        jPanelErsatzzahlung.add(lblEingang, gridBagConstraints);

        chEingang.setContentAreaFilled(false);
        chEingang.setName("chEingang"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.eingegangen}"), chEingang, BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        jPanelErsatzzahlung.add(chEingang, gridBagConstraints);

        jTabbedPane.addTab("Ersatzzahlung", jPanelErsatzzahlung);

        jPanelFestsetzung.setName("jPanelFestsetzung"); // NOI18N
        jPanelFestsetzung.setOpaque(false);
        jPanelFestsetzung.setLayout(new GridBagLayout());

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
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        jPanelFestsetzung.add(panFillerUnten5, gridBagConstraints);

        panFest.setName("panFest"); // NOI18N
        panFest.setOpaque(false);
        panFest.setLayout(new GridBagLayout());

        rpFestliste.setMinimumSize(new Dimension(80, 202));
        rpFestliste.setName("rpFestliste"); // NOI18N
        rpFestliste.setPreferredSize(new Dimension(100, 202));
        rpFestliste.setLayout(new GridBagLayout());

        scpLaufendeFest.setName("scpLaufendeFest"); // NOI18N

        lstFest.setModel(new DefaultListModel());
        lstFest.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstFest.setFixedCellWidth(75);
        lstFest.setName("lstFest"); // NOI18N
        lstFest.addPropertyChangeListener(formListener);
        lstFest.addListSelectionListener(formListener);
        scpLaufendeFest.setViewportView(lstFest);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        rpFestliste.add(scpLaufendeFest, gridBagConstraints);

        semiRoundedPanelFest.setBackground(Color.darkGray);
        semiRoundedPanelFest.setMinimumSize(new Dimension(60, 25));
        semiRoundedPanelFest.setName("semiRoundedPanelFest"); // NOI18N
        semiRoundedPanelFest.setLayout(new GridBagLayout());

        lblFest.setForeground(new Color(255, 255, 255));
        lblFest.setText("Fest");
        lblFest.setName("lblFest"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        semiRoundedPanelFest.add(lblFest, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpFestliste.add(semiRoundedPanelFest, gridBagConstraints);

        panControlsNewFest.setName("panControlsNewFest"); // NOI18N
        panControlsNewFest.setOpaque(false);
        panControlsNewFest.setLayout(new GridBagLayout());

        btnAddNewFest.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddNewFest.setMaximumSize(new Dimension(39, 20));
        btnAddNewFest.setMinimumSize(new Dimension(39, 20));
        btnAddNewFest.setName("btnAddNewFest"); // NOI18N
        btnAddNewFest.setPreferredSize(new Dimension(39, 25));
        btnAddNewFest.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panControlsNewFest.add(btnAddNewFest, gridBagConstraints);

        btnRemoveFest.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveFest.setMaximumSize(new Dimension(39, 20));
        btnRemoveFest.setMinimumSize(new Dimension(39, 20));
        btnRemoveFest.setName("btnRemoveFest"); // NOI18N
        btnRemoveFest.setPreferredSize(new Dimension(39, 25));
        btnRemoveFest.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panControlsNewFest.add(btnRemoveFest, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 0, 5, 0);
        rpFestliste.add(panControlsNewFest, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 0, 5);
        panFest.add(rpFestliste, gridBagConstraints);

        rpFestinfo.setName("rpFestinfo"); // NOI18N
        rpFestinfo.setLayout(new GridBagLayout());

        semiRoundedPanel6.setBackground(Color.darkGray);
        semiRoundedPanel6.setName("semiRoundedPanel6"); // NOI18N
        semiRoundedPanel6.setLayout(new GridBagLayout());

        lblFestanzeige.setForeground(new Color(255, 255, 255));
        lblFestanzeige.setText("Festsetzung");
        lblFestanzeige.setName("lblFestanzeige"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        semiRoundedPanel6.add(lblFestanzeige, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpFestinfo.add(semiRoundedPanel6, gridBagConstraints);

        panFestMain.setName("panFestMain"); // NOI18N
        panFestMain.setOpaque(false);
        panFestMain.setLayout(new GridBagLayout());

        filler7.setName("filler7"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panFestMain.add(filler7, gridBagConstraints);

        baumFestsetzungPanel.setName("baumFestsetzungPanel"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, lstFest, ELProperty.create("${selectedElement}"), baumFestsetzungPanel, BeanProperty.create("cidsBean"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panFestMain.add(baumFestsetzungPanel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        rpFestinfo.add(panFestMain, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 8.0;
        gridBagConstraints.insets = new Insets(0, 5, 0, 0);
        panFest.add(rpFestinfo, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelFestsetzung.add(panFest, gridBagConstraints);

        jTabbedPane.addTab("Festsetzung", jPanelFestsetzung);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
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

    private class FormListener implements ActionListener, PropertyChangeListener, ListSelectionListener {
        FormListener() {}
        public void actionPerformed(ActionEvent evt) {
            if (evt.getSource() == btnAddWurzel) {
                BaumSchadenPanel.this.btnAddWurzelActionPerformed(evt);
            }
            else if (evt.getSource() == btnRemoveWurzel) {
                BaumSchadenPanel.this.btnRemoveWurzelActionPerformed(evt);
            }
            else if (evt.getSource() == btnAddStamm) {
                BaumSchadenPanel.this.btnAddStammActionPerformed(evt);
            }
            else if (evt.getSource() == btnRemoveStamm) {
                BaumSchadenPanel.this.btnRemoveStammActionPerformed(evt);
            }
            else if (evt.getSource() == btnAddKrone) {
                BaumSchadenPanel.this.btnAddKroneActionPerformed(evt);
            }
            else if (evt.getSource() == btnRemoveKrone) {
                BaumSchadenPanel.this.btnRemoveKroneActionPerformed(evt);
            }
            else if (evt.getSource() == btnAddMassnahme) {
                BaumSchadenPanel.this.btnAddMassnahmeActionPerformed(evt);
            }
            else if (evt.getSource() == btnRemoveMassnahme) {
                BaumSchadenPanel.this.btnRemoveMassnahmeActionPerformed(evt);
            }
            else if (evt.getSource() == btnAddNewErsatz) {
                BaumSchadenPanel.this.btnAddNewErsatzActionPerformed(evt);
            }
            else if (evt.getSource() == btnRemoveErsatz) {
                BaumSchadenPanel.this.btnRemoveErsatzActionPerformed(evt);
            }
            else if (evt.getSource() == btnAddNewFest) {
                BaumSchadenPanel.this.btnAddNewFestActionPerformed(evt);
            }
            else if (evt.getSource() == btnRemoveFest) {
                BaumSchadenPanel.this.btnRemoveFestActionPerformed(evt);
            }
            else if (evt.getSource() == btnMenAbortMassnahme) {
                BaumSchadenPanel.this.btnMenAbortMassnahmeActionPerformed(evt);
            }
            else if (evt.getSource() == btnMenOkMassnahme) {
                BaumSchadenPanel.this.btnMenOkMassnahmeActionPerformed(evt);
            }
            else if (evt.getSource() == btnMenAbortWurzel) {
                BaumSchadenPanel.this.btnMenAbortWurzelActionPerformed(evt);
            }
            else if (evt.getSource() == btnMenOkWurzel) {
                BaumSchadenPanel.this.btnMenOkWurzelActionPerformed(evt);
            }
            else if (evt.getSource() == btnMenAbortStamm) {
                BaumSchadenPanel.this.btnMenAbortStammActionPerformed(evt);
            }
            else if (evt.getSource() == btnMenOkStamm) {
                BaumSchadenPanel.this.btnMenOkStammActionPerformed(evt);
            }
            else if (evt.getSource() == btnMenAbortKrone) {
                BaumSchadenPanel.this.btnMenAbortKroneActionPerformed(evt);
            }
            else if (evt.getSource() == btnMenOkKrone) {
                BaumSchadenPanel.this.btnMenOkKroneActionPerformed(evt);
            }
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getSource() == lstErsatz) {
                BaumSchadenPanel.this.lstErsatzPropertyChange(evt);
            }
            else if (evt.getSource() == lstFest) {
                BaumSchadenPanel.this.lstFestPropertyChange(evt);
            }
        }

        public void valueChanged(ListSelectionEvent evt) {
            if (evt.getSource() == lstErsatz) {
                BaumSchadenPanel.this.lstErsatzValueChanged(evt);
            }
            else if (evt.getSource() == lstFest) {
                BaumSchadenPanel.this.lstFestValueChanged(evt);
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
            beanErsatz.setProperty(FIELD__FK_SCHADEN, cidsBean);

            //Ersatzpflanzungen erweitern:
            ersatzBeans.add(beanErsatz);
            ((DefaultListModel)lstErsatz.getModel()).addElement(beanErsatz);
            changedErsatzBeans.add(beanErsatz);

            //Refresh:
            lstErsatz.setSelectedValue(beanErsatz, true);
            cidsBean.setArtificialChangeFlag(true);
            parentEditor.getCidsBean().setArtificialChangeFlag(true);

        } catch (Exception e) {
            LOG.error("Cannot add new BaumErsatz object", e);
        }
    }//GEN-LAST:event_btnAddNewErsatzActionPerformed

    private void btnRemoveErsatzActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveErsatzActionPerformed
        final Object selectedObject = lstErsatz.getSelectedValue();

        if (selectedObject instanceof CidsBean) {

            if (ersatzBeans != null) {
                ersatzBeans.remove((CidsBean)selectedObject);
                ((DefaultListModel)lstErsatz.getModel()).removeElement(selectedObject);
                deletedErsatzBeans.add((CidsBean)selectedObject);
                parentPanel.parentEditor.getCidsBean().setArtificialChangeFlag(true);
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
            //lstKrone.updateUI();
        }
    }//GEN-LAST:event_btnMenOkKroneActionPerformed

    private void btnAddWurzelActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddWurzelActionPerformed
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(BaumSchadenPanel.this), dlgAddWurzel, true);
    }//GEN-LAST:event_btnAddWurzelActionPerformed

    private void btnRemoveWurzelActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveWurzelActionPerformed
        final Object selection = lstWurzel.getSelectedValue();
        if (selection != null) {
            final int answer = JOptionPane.showConfirmDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_W_QUESTION),
                NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_W_TITLE),
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
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(BaumSchadenPanel.this), dlgAddStamm, true);
    }//GEN-LAST:event_btnAddStammActionPerformed

    private void btnRemoveStammActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveStammActionPerformed
        final Object selection = lstStamm.getSelectedValue();
        if (selection != null) {
            final int answer = JOptionPane.showConfirmDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_S_QUESTION),
                NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_S_TITLE),
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
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(BaumSchadenPanel.this), dlgAddKrone, true);
    }//GEN-LAST:event_btnAddKroneActionPerformed

    private void btnRemoveKroneActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveKroneActionPerformed
        final Object selection = lstKrone.getSelectedValue();
        if (selection != null) {
            final int answer = JOptionPane.showConfirmDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_K_QUESTION),
                NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_K_TITLE),
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
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(BaumSchadenPanel.this), dlgAddMassnahme, true);
    }//GEN-LAST:event_btnAddMassnahmeActionPerformed

    private void btnRemoveMassnahmeActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveMassnahmeActionPerformed
        final Object selection = lstMassnahme.getSelectedValue();
        if (selection != null) {
            final int answer = JOptionPane.showConfirmDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_M_QUESTION),
                NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_M_TITLE),
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

    private void lstFestPropertyChange(PropertyChangeEvent evt) {//GEN-FIRST:event_lstFestPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_lstFestPropertyChange

    private void lstFestValueChanged(ListSelectionEvent evt) {//GEN-FIRST:event_lstFestValueChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_lstFestValueChanged

    private void btnAddNewFestActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddNewFestActionPerformed
        try{
        //festBean erzeugen und vorbelegen:
            final CidsBean beanFest = CidsBean.createNewCidsBeanFromTableName(
                "WUNDA_BLAU",
                "BAUM_FESTSETZUNG",
                getConnectionContext());
            beanFest.setProperty(FIELD__FK_SCHADEN, cidsBean);

            //Festsetzungen erweitern:
            festBeans.add(beanFest);
            ((DefaultListModel)lstFest.getModel()).addElement(beanFest);
            changedFestBeans.add(beanFest);

            //Refresh:
            lstFest.setSelectedValue(beanFest, true);
            cidsBean.setArtificialChangeFlag(true);
            parentEditor.getCidsBean().setArtificialChangeFlag(true);

        } catch (Exception e) {
            LOG.error("Cannot add new BaumFest object", e);
        }
    }//GEN-LAST:event_btnAddNewFestActionPerformed

    private void btnRemoveFestActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveFestActionPerformed
        final Object selectedObject = lstFest.getSelectedValue();

        if (selectedObject instanceof CidsBean) {

            if (festBeans != null) {
                festBeans.remove((CidsBean)selectedObject);
                ((DefaultListModel)lstFest.getModel()).removeElement(selectedObject);
                deletedFestBeans.add((CidsBean)selectedObject);
                parentPanel.parentEditor.getCidsBean().setArtificialChangeFlag(true);
                if (festBeans != null && festBeans.size() > 0) {
                    lstFest.setSelectedIndex(0);
                }else{
                    lstFest.clearSelection();
                }
            }
        }
    }//GEN-LAST:event_btnRemoveFestActionPerformed

    //~ Instance fields --------------------------------------------------------
    private final boolean isEditor;
    public final BaumMeldungPanel parentPanel;
    public final BaumSchadenEditor parentEditor;
    private final ConnectionContext connectionContext;
    private CidsBean cidsBean;
    private final PropertyChangeListener changeListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                if ((parentPanel != null) && (parentPanel.parentEditor != null) && (parentPanel.getCidsBean() != null)) {
                    parentPanel.parentEditor.getCidsBean().setArtificialChangeFlag(true);
                    parentPanel.setChangedSchadenBeans(cidsBean);
                }
                if ((parentEditor != null) && (parentEditor.getCidsBean() != null)) {
                    parentEditor.getCidsBean().setArtificialChangeFlag(true);
                }
                if(FIELD__GEOM.equals(evt.getPropertyName())){
                    setMapWindow();
                }
            }
        };
    private SwingWorker worker_ersatz;
    private SwingWorker worker_fest;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    BaumErsatzPanel baumErsatzPanel;
    BaumFestsetzungPanel baumFestsetzungPanel;
    JButton btnAddKrone;
    JButton btnAddMassnahme;
    JButton btnAddNewErsatz;
    JButton btnAddNewFest;
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
    JButton btnRemoveFest;
    JButton btnRemoveKrone;
    JButton btnRemoveMassnahme;
    JButton btnRemoveStamm;
    JButton btnRemoveWurzel;
    JComboBox<String> cbArt;
    public JComboBox cbGeomSchaden;
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
    Box.Filler filler6;
    Box.Filler filler7;
    JPanel jPanelAllgemein;
    JPanel jPanelErsatzpflanzung;
    JPanel jPanelErsatzzahlung;
    JPanel jPanelFestsetzung;
    JTabbedPane jTabbedPane;
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
    JLabel lblFaellung;
    JLabel lblFest;
    JLabel lblFestanzeige;
    JLabel lblGeom;
    JLabel lblGutachten;
    JLabel lblHoehe;
    JLabel lblKarte;
    JLabel lblKroneArr;
    JLabel lblMassnahmeArr;
    JLabel lblOhne;
    JLabel lblPrivat;
    JLabel lblStammArr;
    JLabel lblSturm;
    JLabel lblUmfang;
    JLabel lblWurzelArr;
    JList lstErsatz;
    JList lstFest;
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
    JPanel panControlsNewFest;
    JPanel panErsatz;
    JPanel panErsatzMain;
    JPanel panFest;
    JPanel panFestMain;
    JPanel panFillerUnten2;
    JPanel panFillerUnten3;
    JPanel panFillerUnten4;
    JPanel panFillerUnten5;
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
    JPanel pnlCard1;
    RoundedPanel rpErsatzinfo;
    RoundedPanel rpErsatzliste;
    RoundedPanel rpFestinfo;
    RoundedPanel rpFestliste;
    RoundedPanel rpKarte;
    JScrollPane scpBemerkung;
    JScrollPane scpKrone;
    JScrollPane scpLaufendeErsatz;
    JScrollPane scpLaufendeFest;
    JScrollPane scpMassnahme;
    JScrollPane scpStamm;
    JScrollPane scpWurzel;
    SemiRoundedPanel semiRoundedPanel5;
    SemiRoundedPanel semiRoundedPanel6;
    SemiRoundedPanel semiRoundedPanel7;
    SemiRoundedPanel semiRoundedPanelErsatz;
    SemiRoundedPanel semiRoundedPanelFest;
    JTextArea taBemerkung;
    JTextField txtAlter;
    JTextField txtBetrag;
    JTextField txtHoehe;
    JTextField txtUmfang;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BaumSchadenPanel object.
     */
    public BaumSchadenPanel() {
        this(null, null, true);
    }

    
    /**
     * Creates new form BaumSchadenPanel.
     *
     * @param parentPanel
     * @param parentEditor
     * @param  editable  DOCUMENT ME!
     */
    public BaumSchadenPanel(final BaumMeldungPanel parentPanel, BaumSchadenEditor parentEditor, final boolean editable) {
        this.isEditor = editable;
        initComponents();
        this.connectionContext = null;
        this.parentPanel = parentPanel;
        this.parentEditor = parentEditor;
    }
 
    
    /**
     * Creates new form BaumSchadenPanel.
     *
     * @param parentPanel
     * @param parentEditor
     * @param  editable             DOCUMENT ME!
     * @param  connectionContext    DOCUMENT ME!
     */
    public BaumSchadenPanel(final BaumMeldungPanel parentPanel, BaumSchadenEditor parentEditor, final boolean editable,
            final ConnectionContext connectionContext) {
        this.isEditor = editable;
        this.connectionContext = connectionContext;
        initComponents();
        this.parentPanel = parentPanel;
        this.parentEditor = parentEditor;
    }
    
    public boolean prepareForSave() {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();

        boolean errorOccured = false;
        if (ersatzBeans != null){
            for (final CidsBean ersatzBean : changedErsatzBeans) {
                try {
                    ersatzBean.persist(getConnectionContext());
                } catch (final Exception ex) {
                    errorOccured = true;
                    LOG.error(ex, ex);
                }
            }
        }
        if (errorOccured) {
            return false;
        }
        if (deletedErsatzBeans != null){
            for (final CidsBean ersatzBean : deletedErsatzBeans) {
                try {
                    ersatzBean.delete();
                    ersatzBean.persist(getConnectionContext());
                } catch (final Exception ex) {
                    errorOccured = true;
                    LOG.error(ex, ex);
                }
            }
        }
        if (errorOccured) {
            return false;
        }
        
        if (festBeans != null){
            for (final CidsBean festBean : changedFestBeans) {
                try {
                    festBean.persist(getConnectionContext());
                } catch (final Exception ex) {
                    errorOccured = true;
                    LOG.error(ex, ex);
                }
            }
        }
        if (errorOccured) {
            return false;
        }
        if (deletedFestBeans != null){
            for (final CidsBean festBean : deletedFestBeans) {
                try {
                    festBean.delete();
                    festBean.persist(getConnectionContext());
                } catch (final Exception ex) {
                    errorOccured = true;
                    LOG.error(ex, ex);
                }
            }
        }
        if (errorOccured) {
            return false;
        }
        // Anzahl muss angegeben werden
        /*try {
            if (txtAlter.getText().trim().isEmpty()) {
                LOG.warn("No count specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_NOAGE));
            } else {
                try {
                    Integer.parseInt(txtAlter.getText());
                } catch (NumberFormatException e) {
                    LOG.warn("Wrong count specified. Skip persisting.", e);
                    errorMessage.append(NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_WRONGAGE));
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Countl not given.", ex);
            save = false;
        }*/

        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_PANE_TITLE),
                JOptionPane.WARNING_MESSAGE);

            return false;
        }
        return save;
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
            ((DefaultCismapGeometryComboBoxEditor)cbGeomSchaden).dispose();
        }
        dlgAddMassnahme.dispose();
        dlgAddWurzel.dispose();
        dlgAddStamm.dispose();
        dlgAddKrone.dispose();
        deletedErsatzBeans.clear();
        deletedFestBeans.clear();
        changedErsatzBeans.clear();
        changedFestBeans.clear();
        festBeans.clear();
        ersatzBeans.clear();
        baumFestsetzungPanel.dispose();
        baumErsatzPanel.dispose();
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
        if (this.cidsBean != null){
            final String WHERE = " where "
                    + cidsBean.getProperty(FIELD__ID).toString()
                    + " = "
                    + FIELD__FK_SCHADEN;
            valueFromOtherTable(BaumSchadenEditor.TABLE__ERSATZ, WHERE, whatToDoForTree.ersatz);  
            valueFromOtherTable(BaumSchadenEditor.TABLE__FEST, WHERE, whatToDoForTree.fest);
            
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                this.cidsBean,
                getConnectionContext());
        } else {
            setErsatzBeans(null);
            setFestBeans(null);
        }
        setMapWindow();
        bindingGroup.bind();
        if (isEditor && (this.cidsBean != null)) {
                cidsBean.addPropertyChangeListener(changeListener);
        }
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
                    compoTeil.setForeground(new Color(87,175,54));
                    return compoTeil;
                }
            });
        lstFest.setCellRenderer(new DefaultListCellRenderer() {

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
                    compoTeil.setForeground(new Color(112,48,160));
                    return compoTeil;
                }
            });
        
        if (ersatzBeans != null && ersatzBeans.size() > 0) {
            lstErsatz.setSelectedIndex(0);
        }
        
        if (festBeans != null && festBeans.size() > 0) {
            lstFest.setSelectedIndex(0);
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
    
    public void prepareErsatz(){
        
        if (ersatzBeans != null && ersatzBeans.size() > 0) {
            lstErsatz.setSelectedIndex(0);
        }
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

                }
                final Component compoId= super.getListCellRendererComponent(list, newValue, index, isSelected, cellHasFocus);
                compoId.setForeground(new Color(87,175,54));
                return compoId;
            }
        });   
    }
    public void prepareFest(){
        
        if (festBeans != null && festBeans.size() > 0) {
            lstFest.setSelectedIndex(0);
        }
        lstFest.setCellRenderer(new DefaultListCellRenderer() {

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

                }
                final Component compoId= super.getListCellRendererComponent(list, newValue, index, isSelected, cellHasFocus);
                compoId.setForeground(new Color(112,48,160));
                return compoId;
            }
        });   
    }
    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeans  DOCUMENT ME!
     */
    public void setErsatzBeans(final List<CidsBean> cidsBeans) {
        baumErsatzPanel.setCidsBean(null);
        ((DefaultListModel)lstErsatz.getModel()).clear();
        this.ersatzBeans.clear();
        if (cidsBeans != null){
            cidsBeans.sort(ID_COMPARATOR);
            for(final Object bean:cidsBeans){
                ((DefaultListModel)lstErsatz.getModel()).addElement(bean);
            }
            this.ersatzBeans = cidsBeans;
        }
        prepareErsatz();
    }
    public void setFestBeans(final List<CidsBean> cidsBeans) {
        baumFestsetzungPanel.setCidsBean(null);
        ((DefaultListModel)lstFest.getModel()).clear();
        this.festBeans.clear();
        if (cidsBeans != null){
            cidsBeans.sort(ID_COMPARATOR);
            for(final Object bean:cidsBeans){
                ((DefaultListModel)lstFest.getModel()).addElement(bean);
            }
            this.festBeans = cidsBeans;
        }
        prepareFest();
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> getErsatzBeans() {
        return ersatzBeans;
    }
    public List<CidsBean> getFestBeans() {
        return festBeans;
    }
    public List<CidsBean> getChangedErsatzBeans() {
        return changedErsatzBeans;
    }
    public List<CidsBean> getChangedFestBeans() {
        return changedFestBeans;
    }
    
    public void setChangedErsatzBeans(CidsBean ersatzBean){
        if(this.changedErsatzBeans != null){
            int ersatzId = (Integer)ersatzBean.getProperty(FIELD__ID);
            for(final CidsBean bean:changedErsatzBeans){
                if (ersatzId == (Integer)bean.getProperty(FIELD__ID)){
                    changedErsatzBeans.remove(bean);
                    break;
                }
            }
        } 
        this.changedErsatzBeans.add(ersatzBean);
    }
    public void setChangedFestBeans(CidsBean festBean){
        if(this.changedFestBeans != null){
            int festId = (Integer)festBean.getProperty(FIELD__ID);
            for(final CidsBean bean:changedFestBeans){
                if (festId == (Integer)bean.getProperty(FIELD__ID)){
                    changedFestBeans.remove(bean);
                    break;
                }
            }
        } 
        this.changedFestBeans.add(festBean);
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
    private void valueFromOtherTable(final String tableName,
            final String whereClause,
            final whatToDoForTree toDo){
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
                            switch(toDo){
                                case ersatz: {
                                    setErsatzBeans(oneToNList);
                                    break;
                                }
                                case fest: {
                                    setFestBeans(oneToNList);
                                    break;
                                }
                            }
                            
                        } else {
                            switch(toDo){
                                case ersatz: {
                                    setErsatzBeans(null);
                                    break;
                                }
                                case fest: {
                                    setFestBeans(null);
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
        if (toDo.equals(whatToDoForTree.ersatz)){
            if (worker_ersatz != null) {
                worker_ersatz.cancel(true);
            }
            worker_ersatz = worker;
            worker_ersatz.execute();
        } else{
            if (worker_fest != null) {
                worker_fest.cancel(true);
            }
            worker_fest = worker;
            worker_fest.execute();
        }
    }    
    }
    

