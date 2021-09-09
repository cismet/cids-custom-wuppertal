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
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import de.cismet.cids.client.tools.DevelopmentTools;
import de.cismet.cids.custom.objecteditors.utils.BaumChildrenLoader;
import de.cismet.cids.custom.objecteditors.utils.BaumConfProperties;
import de.cismet.cids.custom.objecteditors.utils.RendererTools;
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


import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;
import de.cismet.cids.dynamics.Disposable;
import de.cismet.cids.editors.DefaultBindableLabelsPanel;
import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultBindableScrollableComboBox;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;
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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.MissingResourceException;
import java.util.Objects;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import lombok.Getter;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.openide.awt.Mnemonics;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public final class BaumSchadenPanel extends javax.swing.JPanel implements Disposable, CidsBeanStore, ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------
   
    //private List<CidsBean> ersatzBeans = new ArrayList<>();
    //private final List<CidsBean> changedErsatzBeans = new ArrayList<>();
    //private final List<CidsBean> deletedErsatzBeans = new ArrayList<>();
    //private List<CidsBean> festBeans = new ArrayList<>();
    //private final List<CidsBean> changedFestBeans = new ArrayList<>();
    //private final List<CidsBean> deletedFestBeans = new ArrayList<>();
    private BaumChildrenLoader.Listener loadChildrenListener;
    private static final Logger LOG = Logger.getLogger(BaumSchadenPanel.class);
    public static final String GEOMTYPE = "Point";
    
    private static DefaultBindableReferenceCombo.Option SORTING_OPTION =
        new DefaultBindableReferenceCombo.SortingColumnOption("name");
    private static final MetaClass MC__ART;
    static {
        final ConnectionContext connectionContext = ConnectionContext.create(
                ConnectionContext.Category.STATIC,
                BaumSchadenPanel.class.getSimpleName());
        MC__ART = ClassCacheMultiple.getMetaClass(
                "WUNDA_BLAU",
                "BAUM_ART",
                connectionContext);
    }
    
    
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
    public static final String FIELD__SELBST = "selbststaendig";                // baum_ersatz
    public static final String FIELD__DISPENS = "dispensbau";                   // baum_ersatz
    public static final String FIELD__ID = "id";                                // baum_schaden
    
    public static final String FIELD__GEOREFERENZ = "fk_geom";                  // baum_schaden
    public static final String FIELD__GEO_FIELD = "geo_field";                      // geom
    public static final String FIELD__GEOREFERENZ__GEO_FIELD = "fk_geom.geo_field"; // baum_schaden_geom
    
    public static final String TABLE_GEOM = "geom";
    public static final String TABLE_FEST = "BAUM_FESTSETZUNG";
    public static final String TABLE_ERSATZ = "BAUM_ERSATZ";
    public static final String TABLE_NAME = "baum_schaden";
    
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
    public static final String BUNDLE_NOART = "BaumSchadenPanel.prepareForSave().noArt";
    public static final String BUNDLE_WRONGAGE = "BaumSchadenPanel.prepareForSave().wrongAlter";
    public static final String BUNDLE_NOWURZEL = "BaumSchadenPanel.prepareForSave().noWurzel";
    public static final String BUNDLE_NOSTAMM= "BaumSchadenPanel.prepareForSave().noStamm";
    public static final String BUNDLE_NOKRONE = "BaumSchadenPanel.prepareForSave().noKrone";
    public static final String BUNDLE_WRONGWURZEL = "BaumSchadenPanel.prepareForSave().wrongWurzel";
    public static final String BUNDLE_WRONGSTAMM= "BaumSchadenPanel.prepareForSave().wrongStamm";
    public static final String BUNDLE_WRONGKRONE = "BaumSchadenPanel.prepareForSave().wrongKrone";
    public static final String BUNDLE_WRONGABG = "BaumSchadenPanel.prepareForSave().wrongAbgestorben";
    public static final String BUNDLE_WRONGSTURM = "BaumSchadenPanel.prepareForSave().wrongSturm";
    public static final String BUNDLE_NOGEOM = "BaumSchadenPanel.prepareForSave().noGeom";
    public static final String BUNDLE_WRONGGEOM = "BaumSchadenPanel.prepareForSave().wrongGeom";
    
    public static final String BUNDLE_PANE_TITLE_ERROR_FEST = "BaumSchadenPanel.zeigeErrorFest().JOptionPane.title";
    public static final String BUNDLE_PANE_TITLE_ERROR_ERSATZ = "BaumSchadenPanel.zeigeErrorErsatz().JOptionPane.title";
    public static final String BUNDLE_ERROR_FEST = "BaumSchadenPanel.zeigeErrorFest().JOptionPane.meldung";
    public static final String BUNDLE_ERROR_ERSATZ = "BaumSchadenPanel.zeigeErrorErsatz().JOptionPane.meldung";
    public static final String BUNDLE_PANE_PREFIX =
        "BaumSchadenPanel.prepareForSave().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX =
        "BaumSchadenPanel.prepareForSave().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "BaumSchadenPanel.prepareForSave().JOptionPane.title";
    
   
        //~ Enum constants -----------------------------------------------------

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

        pnlCard1 = new JPanel();
        jTabbedPane = new JTabbedPane();
        jPanelAllgemein = new JPanel();
        panSchaden = new JPanel();
        lblAlter = new JLabel();
        txtAlter = new JTextField();
        lblPrivat = new JLabel();
        chPrivat = new JCheckBox();
        lblHoehe = new JLabel();
        lblUmfang = new JLabel();
        lblArt = new JLabel();
        cbArt = new DefaultBindableScrollableComboBox(MC__ART);
        if (editor){
            lblGeom = new JLabel();
        }
        if (editor){
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
        lblBau = new JLabel();
        chBau = new JCheckBox();
        lblAbgest = new JLabel();
        chAbgest = new JCheckBox();
        lblSturm = new JLabel();
        chSturm = new JCheckBox();
        lblBeratung = new JLabel();
        chBeratung = new JCheckBox();
        lblGutachten = new JLabel();
        chGutachten = new JCheckBox();
        lblKroneArr = new JLabel();
        chKrone = new JCheckBox();
        filler21 = new Box.Filler(new Dimension(0, 28), new Dimension(0, 28), new Dimension(32767, 28));
        blpKrone = new DefaultBindableLabelsPanel(editor, "Kronenschaden:", SORTING_OPTION);
        lblStammArr = new JLabel();
        chStamm = new JCheckBox();
        filler22 = new Box.Filler(new Dimension(0, 28), new Dimension(0, 28), new Dimension(32767, 28));
        blpStamm = new DefaultBindableLabelsPanel(editor, "Stammschaden:", SORTING_OPTION);
        lblWurzelArr = new JLabel();
        chWurzel = new JCheckBox();
        filler23 = new Box.Filler(new Dimension(0, 28), new Dimension(0, 28), new Dimension(32767, 28));
        blpWurzel = new DefaultBindableLabelsPanel(editor, "Wurzelschaden:", SORTING_OPTION);
        lblMassnahmeArr = new JLabel();
        filler24 = new Box.Filler(new Dimension(0, 28), new Dimension(0, 28), new Dimension(32767, 28));
        blpMassnahme = new DefaultBindableLabelsPanel(editor, "Maßnahmen:", SORTING_OPTION);
        filler6 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        lblBemerkung = new JLabel();
        scpBemerkung = new JScrollPane();
        taBemerkung = new JTextArea();
        lblFaellung = new JLabel();
        chFaellung = new JCheckBox();
        spHoehe = new JSpinner();
        spUmfang = new JSpinner();
        filler7 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
        jPanelErsatzpflanzung = new JPanel();
        panErsatz = new JPanel();
        panErsatzMain = new JPanel();
        baumErsatzPanel = baumErsatzPanel = new BaumErsatzPanel(this.getBaumChildrenLoader());
        lblLadenErsatz = new JLabel();
        scpLaufendeErsatz = new JScrollPane();
        lstErsatz = new JList();
        panControlsNewErsatz = new JPanel();
        btnAddNewErsatz = new JButton();
        btnRemoveErsatz = new JButton();
        filler8 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
        jPanelErsatzzahlung = new JPanel();
        panZahlung = new JPanel();
        lblBetrag = new JLabel();
        txtBetrag = new JTextField();
        lblEingang = new JLabel();
        chEingang = new JCheckBox();
        filler9 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
        jPanelFestsetzung = new JPanel();
        panFest = new JPanel();
        panFestMain = new JPanel();
        baumFestsetzungPanel = baumFestsetzungPanel = new BaumFestsetzungPanel(this.getBaumChildrenLoader());
        lblLadenFest = new JLabel();
        scpLaufendeFest = new JScrollPane();
        lstFest = new JList();
        panControlsNewFest = new JPanel();
        btnAddNewFest = new JButton();
        btnRemoveFest = new JButton();

        FormListener formListener = new FormListener();

        setName("Form"); // NOI18N
        setOpaque(false);
        setLayout(new GridBagLayout());

        pnlCard1.setName("pnlCard1"); // NOI18N
        pnlCard1.setOpaque(false);
        pnlCard1.setLayout(new GridBagLayout());

        jTabbedPane.setMinimumSize(new Dimension(849, 520));
        jTabbedPane.setName("jTabbedPane"); // NOI18N

        jPanelAllgemein.setName("jPanelAllgemein"); // NOI18N
        jPanelAllgemein.setOpaque(false);
        jPanelAllgemein.setLayout(new GridBagLayout());

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

        txtAlter.setMinimumSize(new Dimension(40, 19));
        txtAlter.setName("txtAlter"); // NOI18N

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.alter}"), txtAlter, BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(txtAlter, gridBagConstraints);

        lblPrivat.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblPrivat, "privat:");
        lblPrivat.setName("lblPrivat"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblPrivat, gridBagConstraints);

        chPrivat.setContentAreaFilled(false);
        chPrivat.setName("chPrivat"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.privatbaum}"), chPrivat, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chPrivat, gridBagConstraints);

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

        lblUmfang.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblUmfang, "Umfang [cm]:");
        lblUmfang.setName("lblUmfang"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblUmfang, gridBagConstraints);

        lblArt.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblArt, "Art:");
        lblArt.setName("lblArt"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblArt, gridBagConstraints);

        cbArt.setFont(new Font("Dialog", 0, 12)); // NOI18N
        cbArt.setMaximumRowCount(15);
        cbArt.setName("cbArt"); // NOI18N
        cbArt.setPreferredSize(new Dimension(100, 24));

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_art}"), cbArt, BeanProperty.create("selectedItem"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(cbArt, gridBagConstraints);

        if (editor){
            lblGeom.setFont(new Font("Tahoma", 1, 11)); // NOI18N
            Mnemonics.setLocalizedText(lblGeom, "Geometrie:");
            lblGeom.setName("lblGeom"); // NOI18N
        }
        if (editor){
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 3;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.ipady = 10;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(2, 0, 2, 5);
            panSchaden.add(lblGeom, gridBagConstraints);
        }

        if (editor){
            cbGeomSchaden.setFont(new Font("Dialog", 0, 12)); // NOI18N
            cbGeomSchaden.setName("cbGeomSchaden"); // NOI18N

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_geom}"), cbGeomSchaden, BeanProperty.create("selectedItem"));
            binding.setSourceNullValue(null);
            binding.setSourceUnreadableValue(null);
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeomSchaden).getConverter());
            bindingGroup.addBinding(binding);

        }
        if (editor){
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 3;
            gridBagConstraints.gridwidth = 4;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
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
        Mnemonics.setLocalizedText(lblKarte, NbBundle.getMessage(BaumSchadenPanel.class, "BaumSchadenPanel.lblKarte.text")); // NOI18N
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
        gridBagConstraints.insets = new Insets(0, 0, 2, 2);
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
        gridBagConstraints.gridheight = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 5, 5, 3);
        panSchaden.add(panGeometrie, gridBagConstraints);

        lblOhne.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblOhne, "ohne:");
        lblOhne.setName("lblOhne"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblOhne, gridBagConstraints);

        chOhne.setContentAreaFilled(false);
        chOhne.setName("chOhne"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.ohne_schaden}"), chOhne, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chOhne, gridBagConstraints);

        lblBau.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblBau, "Bau:");
        lblBau.setName("lblBau"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblBau, gridBagConstraints);

        chBau.setContentAreaFilled(false);
        chBau.setName("chBau"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.baumassnahme}"), chBau, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chBau, gridBagConstraints);

        lblAbgest.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblAbgest, "abgestorben:");
        lblAbgest.setName("lblAbgest"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblAbgest, gridBagConstraints);

        chAbgest.setContentAreaFilled(false);
        chAbgest.setName("chAbgest"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.abgestorben}"), chAbgest, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chAbgest, gridBagConstraints);

        lblSturm.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblSturm, "Sturm:");
        lblSturm.setName("lblSturm"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblSturm, gridBagConstraints);

        chSturm.setContentAreaFilled(false);
        chSturm.setName("chSturm"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.sturmschaden}"), chSturm, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chSturm, gridBagConstraints);

        lblBeratung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblBeratung, "Beratung:");
        lblBeratung.setName("lblBeratung"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblBeratung, gridBagConstraints);

        chBeratung.setContentAreaFilled(false);
        chBeratung.setName("chBeratung"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.baumberatung}"), chBeratung, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chBeratung, gridBagConstraints);

        lblGutachten.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblGutachten, "Gutachten vorh.:");
        lblGutachten.setName("lblGutachten"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblGutachten, gridBagConstraints);

        chGutachten.setContentAreaFilled(false);
        chGutachten.setName("chGutachten"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.gutachten}"), chGutachten, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chGutachten, gridBagConstraints);

        lblKroneArr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblKroneArr, "Kronenschaden:");
        lblKroneArr.setName("lblKroneArr"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblKroneArr, gridBagConstraints);

        chKrone.setContentAreaFilled(false);
        chKrone.setName("chKrone"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.kronenschaden}"), chKrone, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chKrone, gridBagConstraints);

        filler21.setName("filler21"); // NOI18N
        panSchaden.add(filler21, new GridBagConstraints());

        blpKrone.setManageableButtonText(NbBundle.getMessage(BaumSchadenPanel.class, "BaumSchadenPanel.blpKrone.manageableButtonText")); // NOI18N
        blpKrone.setOpaque(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.arr_krone}"), blpKrone, BeanProperty.create("selectedElements"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 5, 2);
        panSchaden.add(blpKrone, gridBagConstraints);

        lblStammArr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblStammArr, "Stammschaden:");
        lblStammArr.setName("lblStammArr"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblStammArr, gridBagConstraints);

        chStamm.setContentAreaFilled(false);
        chStamm.setName("chStamm"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.stammschaden}"), chStamm, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chStamm, gridBagConstraints);

        filler22.setName("filler22"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 0, 5, 0);
        panSchaden.add(filler22, gridBagConstraints);

        blpStamm.setManageableButtonText(NbBundle.getMessage(BaumSchadenPanel.class, "BaumSchadenPanel.blpStamm.manageableButtonText")); // NOI18N
        blpStamm.setName("blpStamm"); // NOI18N
        blpStamm.setOpaque(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.arr_stamm}"), blpStamm, BeanProperty.create("selectedElements"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 5, 2);
        panSchaden.add(blpStamm, gridBagConstraints);

        lblWurzelArr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblWurzelArr, "Wurzelschaden:");
        lblWurzelArr.setName("lblWurzelArr"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblWurzelArr, gridBagConstraints);

        chWurzel.setContentAreaFilled(false);
        chWurzel.setName("chWurzel"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.wurzelschaden}"), chWurzel, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chWurzel, gridBagConstraints);

        filler23.setName("filler23"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 13;
        panSchaden.add(filler23, gridBagConstraints);

        blpWurzel.setManageableButtonText(NbBundle.getMessage(BaumSchadenPanel.class, "BaumSchadenPanel.blpWurzel.manageableButtonText")); // NOI18N
        blpWurzel.setName("blpWurzel"); // NOI18N
        blpWurzel.setOpaque(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.arr_wurzel}"), blpWurzel, BeanProperty.create("selectedElements"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 5, 2);
        panSchaden.add(blpWurzel, gridBagConstraints);

        lblMassnahmeArr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblMassnahmeArr, "Maßnahme:");
        lblMassnahmeArr.setName("lblMassnahmeArr"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblMassnahmeArr, gridBagConstraints);

        filler24.setName("filler24"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 16;
        panSchaden.add(filler24, gridBagConstraints);

        blpMassnahme.setManageableButtonText(NbBundle.getMessage(BaumSchadenPanel.class, "BaumSchadenPanel.blpMassnahme.manageableButtonText")); // NOI18N
        blpMassnahme.setName("blpMassnahme"); // NOI18N
        blpMassnahme.setOpaque(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.arr_massnahme}"), blpMassnahme, BeanProperty.create("selectedElements"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 5, 2);
        panSchaden.add(blpMassnahme, gridBagConstraints);

        filler6.setName("filler6"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panSchaden.add(filler6, gridBagConstraints);

        lblBemerkung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblBemerkung, "Bemerkung:");
        lblBemerkung.setName("lblBemerkung"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblBemerkung, gridBagConstraints);

        scpBemerkung.setName("scpBemerkung"); // NOI18N
        scpBemerkung.setOpaque(false);

        taBemerkung.setLineWrap(true);
        taBemerkung.setRows(2);
        taBemerkung.setWrapStyleWord(true);
        taBemerkung.setName("taBemerkung"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.bemerkung}"), taBemerkung, BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        scpBemerkung.setViewportView(taBemerkung);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 0, 0, 2);
        panSchaden.add(scpBemerkung, gridBagConstraints);

        lblFaellung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblFaellung, "Fällung:");
        lblFaellung.setName("lblFaellung"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 23;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblFaellung, gridBagConstraints);

        chFaellung.setContentAreaFilled(false);
        chFaellung.setName("chFaellung"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.faellung}"), chFaellung, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 23;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chFaellung, gridBagConstraints);

        spHoehe.setFont(new Font("Dialog", 0, 12)); // NOI18N
        spHoehe.setModel(new SpinnerNumberModel(0.0d, 0.0d, 100.0d, 0.1d));
        spHoehe.setName("spHoehe"); // NOI18N
        spHoehe.setPreferredSize(new Dimension(75, 20));

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.hoehe}"), spHoehe, BeanProperty.create("value"));
        binding.setSourceNullValue(0d);
        binding.setSourceUnreadableValue(0d);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(spHoehe, gridBagConstraints);

        spUmfang.setFont(new Font("Dialog", 0, 12)); // NOI18N
        spUmfang.setModel(new SpinnerNumberModel(0.0d, 0.0d, 1000.0d, 1.0d));
        spUmfang.setName("spUmfang"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.umfang}"), spUmfang, BeanProperty.create("value"));
        binding.setSourceNullValue(0d);
        binding.setSourceUnreadableValue(0d);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(spUmfang, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        jPanelAllgemein.add(panSchaden, gridBagConstraints);

        filler7.setName("filler7"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        jPanelAllgemein.add(filler7, gridBagConstraints);

        jTabbedPane.addTab("Allgemeine Informationen", jPanelAllgemein);

        jPanelErsatzpflanzung.setName("jPanelErsatzpflanzung"); // NOI18N
        jPanelErsatzpflanzung.setOpaque(false);
        jPanelErsatzpflanzung.setLayout(new GridBagLayout());

        panErsatz.setName("panErsatz"); // NOI18N
        panErsatz.setOpaque(false);
        panErsatz.setLayout(new GridBagLayout());

        panErsatzMain.setName("panErsatzMain"); // NOI18N
        panErsatzMain.setOpaque(false);
        panErsatzMain.setLayout(new GridBagLayout());

        baumErsatzPanel.setName("baumErsatzPanel"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 10, 0);
        panErsatzMain.add(baumErsatzPanel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(0, 10, 0, 0);
        panErsatz.add(panErsatzMain, gridBagConstraints);

        lblLadenErsatz.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblLadenErsatz.setForeground(new Color(153, 153, 153));
        Mnemonics.setLocalizedText(lblLadenErsatz, NbBundle.getMessage(BaumSchadenPanel.class, "BaumSchadenPanel.lblLadenErsatz.text")); // NOI18N
        lblLadenErsatz.setName("lblLadenErsatz"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panErsatz.add(lblLadenErsatz, gridBagConstraints);

        scpLaufendeErsatz.setName("scpLaufendeErsatz"); // NOI18N

        lstErsatz.setModel(new DefaultListModel());
        lstErsatz.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstErsatz.setFixedCellWidth(75);
        lstErsatz.setName("lstErsatz"); // NOI18N
        lstErsatz.addListSelectionListener(formListener);
        scpLaufendeErsatz.setViewportView(lstErsatz);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panErsatz.add(scpLaufendeErsatz, gridBagConstraints);

        panControlsNewErsatz.setName("panControlsNewErsatz"); // NOI18N
        panControlsNewErsatz.setOpaque(false);
        panControlsNewErsatz.setLayout(new GridBagLayout());

        btnAddNewErsatz.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddNewErsatz.setEnabled(false);
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
        btnRemoveErsatz.setEnabled(false);
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
        gridBagConstraints.anchor = GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new Insets(0, 0, 5, 0);
        panErsatz.add(panControlsNewErsatz, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        jPanelErsatzpflanzung.add(panErsatz, gridBagConstraints);

        filler8.setName("filler8"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        jPanelErsatzpflanzung.add(filler8, gridBagConstraints);

        jTabbedPane.addTab("Ersatzpflanzung", jPanelErsatzpflanzung);

        jPanelErsatzzahlung.setName("jPanelErsatzzahlung"); // NOI18N
        jPanelErsatzzahlung.setOpaque(false);
        jPanelErsatzzahlung.setLayout(new GridBagLayout());

        panZahlung.setName("panZahlung"); // NOI18N
        panZahlung.setOpaque(false);
        panZahlung.setLayout(new GridBagLayout());

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
        panZahlung.add(lblBetrag, gridBagConstraints);

        txtBetrag.setName("txtBetrag"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.betrag}"), txtBetrag, BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panZahlung.add(txtBetrag, gridBagConstraints);

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
        panZahlung.add(lblEingang, gridBagConstraints);

        chEingang.setContentAreaFilled(false);
        chEingang.setName("chEingang"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.eingegangen}"), chEingang, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panZahlung.add(chEingang, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        jPanelErsatzzahlung.add(panZahlung, gridBagConstraints);

        filler9.setName("filler9"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        jPanelErsatzzahlung.add(filler9, gridBagConstraints);

        jTabbedPane.addTab("Ersatzzahlung", jPanelErsatzzahlung);

        jPanelFestsetzung.setName("jPanelFestsetzung"); // NOI18N
        jPanelFestsetzung.setOpaque(false);
        jPanelFestsetzung.setLayout(new GridBagLayout());

        panFest.setName("panFest"); // NOI18N
        panFest.setOpaque(false);
        panFest.setLayout(new GridBagLayout());

        panFestMain.setName("panFestMain"); // NOI18N
        panFestMain.setOpaque(false);
        panFestMain.setLayout(new GridBagLayout());

        baumFestsetzungPanel.setName("baumFestsetzungPanel"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, lstFest, ELProperty.create("${selectedElement}"), baumFestsetzungPanel, BeanProperty.create("cidsBean"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panFestMain.add(baumFestsetzungPanel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(0, 10, 10, 0);
        panFest.add(panFestMain, gridBagConstraints);

        lblLadenFest.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblLadenFest.setForeground(new Color(153, 153, 153));
        Mnemonics.setLocalizedText(lblLadenFest, NbBundle.getMessage(BaumSchadenPanel.class, "BaumSchadenPanel.lblLadenFest.text")); // NOI18N
        lblLadenFest.setName("lblLadenFest"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panFest.add(lblLadenFest, gridBagConstraints);

        scpLaufendeFest.setName("scpLaufendeFest"); // NOI18N

        lstFest.setModel(new DefaultListModel());
        lstFest.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstFest.setFixedCellWidth(75);
        lstFest.setName("lstFest"); // NOI18N
        scpLaufendeFest.setViewportView(lstFest);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panFest.add(scpLaufendeFest, gridBagConstraints);

        panControlsNewFest.setName("panControlsNewFest"); // NOI18N
        panControlsNewFest.setOpaque(false);
        panControlsNewFest.setLayout(new GridBagLayout());

        btnAddNewFest.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddNewFest.setEnabled(false);
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
        btnRemoveFest.setEnabled(false);
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
        gridBagConstraints.anchor = GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new Insets(0, 0, 5, 0);
        panFest.add(panControlsNewFest, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
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

    private class FormListener implements ActionListener, ListSelectionListener {
        FormListener() {}
        public void actionPerformed(ActionEvent evt) {
            if (evt.getSource() == btnAddNewErsatz) {
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
        }

        public void valueChanged(ListSelectionEvent evt) {
            if (evt.getSource() == lstErsatz) {
                BaumSchadenPanel.this.lstErsatzValueChanged(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddNewErsatzActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddNewErsatzActionPerformed
        if (getBaumChildrenLoader().getLoadingCompletedWithoutError()){
            try{
            //ersatzBean erzeugen und vorbelegen:
                final CidsBean beanErsatz = CidsBean.createNewCidsBeanFromTableName(
                    "WUNDA_BLAU",
                    TABLE_ERSATZ,
                    getConnectionContext());
                CidsBean beanSchaden = cidsBean;
                beanSchaden.getMetaObject().setStatus(MetaObject.MODIFIED);
                beanErsatz.setProperty(FIELD__DISPENS, false);
                beanErsatz.setProperty(FIELD__SELBST, false);
                beanErsatz.setProperty(FIELD__FK_SCHADEN, beanSchaden);


                //Ersatzpflanzungen erweitern:
                if (editor){
                    getBaumChildrenLoader().addErsatz(cidsBean.getPrimaryKeyValue(), beanErsatz);
                }
                ((DefaultListModel)lstErsatz.getModel()).addElement(beanErsatz);

                //Refresh:
                lstErsatz.setSelectedValue(beanErsatz, true);
                setChangeFlag();

            } catch (Exception e) {
                LOG.error("Cannot add new BaumErsatz object", e);
            }
        }
    }//GEN-LAST:event_btnAddNewErsatzActionPerformed

    private void btnRemoveErsatzActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveErsatzActionPerformed
        if (getBaumChildrenLoader().getLoadingCompletedWithoutError()){
            final Object selectedObject = lstErsatz.getSelectedValue();

            if (selectedObject instanceof CidsBean) {
                List<CidsBean> listErsatz = getBaumChildrenLoader().getMapValueErsatz(this.cidsBean.getPrimaryKeyValue());
                if(((CidsBean)selectedObject).getMetaObject().getStatus() == MetaObject.NEW){
                    getBaumChildrenLoader().removeErsatz(cidsBean.getPrimaryKeyValue(), (CidsBean)selectedObject);
                } else{
                for(final CidsBean beanErsatz:listErsatz){
                        if(beanErsatz.equals(selectedObject)){
                            try {
                                beanErsatz.delete();
                            } catch (Exception ex) {
                                Exceptions.printStackTrace(ex);
                            }
                            break;
                        }
                    }
                    getBaumChildrenLoader().getMapErsatz().replace(this.cidsBean.getPrimaryKeyValue(), listErsatz);
                }
                ((DefaultListModel)lstErsatz.getModel()).removeElement(selectedObject);
                if (getActiveBeans(listErsatz) > 0) {
                    lstErsatz.setSelectedIndex(0);
                }
                setChangeFlag();
            }
        }
    }//GEN-LAST:event_btnRemoveErsatzActionPerformed

    private void btnAddNewFestActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddNewFestActionPerformed
        if (getBaumChildrenLoader().getLoadingCompletedWithoutError()){
            try{
            //festBean erzeugen und vorbelegen:
                final CidsBean beanFest = CidsBean.createNewCidsBeanFromTableName(
                    "WUNDA_BLAU",
                    TABLE_FEST,
                    getConnectionContext());
                beanFest.setProperty(FIELD__FK_SCHADEN, cidsBean);

                if (editor){
                    getBaumChildrenLoader().addFest(cidsBean.getPrimaryKeyValue(), beanFest);
                }
                ((DefaultListModel)lstFest.getModel()).addElement(beanFest);

                //Refresh:
                lstFest.setSelectedValue(beanFest, true);
                setChangeFlag();
                    
            } catch (Exception e) {
                LOG.error("Cannot add new BaumFest object", e);
            }
        }
    }//GEN-LAST:event_btnAddNewFestActionPerformed

    private void btnRemoveFestActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveFestActionPerformed
        if (getBaumChildrenLoader().getLoadingCompletedWithoutError()){
            final Object selectedObject = lstFest.getSelectedValue();

            if (selectedObject instanceof CidsBean) {
                List<CidsBean> listFest = getBaumChildrenLoader().getMapValueFest(this.cidsBean.getPrimaryKeyValue());
                if(((CidsBean)selectedObject).getMetaObject().getStatus() == MetaObject.NEW){
                    getBaumChildrenLoader().removeFest(cidsBean.getPrimaryKeyValue(), (CidsBean)selectedObject);
                } else{
                    for(final CidsBean beanFest:listFest){
                        if(beanFest.equals(selectedObject)){
                            try {
                                beanFest.delete();
                            } catch (Exception ex) {
                                Exceptions.printStackTrace(ex);
                            }
                            break;
                        }
                    }
                    getBaumChildrenLoader().getMapFest().replace(this.cidsBean.getPrimaryKeyValue(), listFest);
                }
                ((DefaultListModel)lstFest.getModel()).removeElement(selectedObject);
                if (getActiveBeans(listFest) > 0) {
                    lstFest.setSelectedIndex(0);
                }
                setChangeFlag();
            }
        }
    }//GEN-LAST:event_btnRemoveFestActionPerformed

    private void lstErsatzValueChanged(ListSelectionEvent evt) {//GEN-FIRST:event_lstErsatzValueChanged
        Object oErsatz = lstErsatz.getSelectedValue();
        if (oErsatz instanceof CidsBean){
            baumErsatzPanel.setCidsBean((CidsBean)oErsatz);
        }
    }//GEN-LAST:event_lstErsatzValueChanged

    //~ Instance fields --------------------------------------------------------
    private final boolean editor;
    private CidsBean cidsBean;
    @Getter private final BaumChildrenLoader baumChildrenLoader;
    
    private final Collection<DefaultBindableLabelsPanel> labelsPanels = new ArrayList<>();
    private final PropertyChangeListener changeListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                if (!(Objects.equals(evt.getOldValue(), evt.getNewValue()))){
                    setChangeFlag();
                    if(FIELD__GEOM.equals(evt.getPropertyName())){
                        setMapWindow();
                    }
                }
            }
        };
    //private SwingWorker worker_ersatz;
    //private SwingWorker worker_fest;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    BaumErsatzPanel baumErsatzPanel;
    BaumFestsetzungPanel baumFestsetzungPanel;
    DefaultBindableLabelsPanel blpKrone;
    DefaultBindableLabelsPanel blpMassnahme;
    DefaultBindableLabelsPanel blpStamm;
    DefaultBindableLabelsPanel blpWurzel;
    JButton btnAddNewErsatz;
    JButton btnAddNewFest;
    JButton btnRemoveErsatz;
    JButton btnRemoveFest;
    JComboBox<String> cbArt;
    JComboBox cbGeomSchaden;
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
    Box.Filler filler21;
    Box.Filler filler22;
    Box.Filler filler23;
    Box.Filler filler24;
    Box.Filler filler6;
    Box.Filler filler7;
    Box.Filler filler8;
    Box.Filler filler9;
    JPanel jPanelAllgemein;
    JPanel jPanelErsatzpflanzung;
    JPanel jPanelErsatzzahlung;
    JPanel jPanelFestsetzung;
    JTabbedPane jTabbedPane;
    JLabel lblAbgest;
    JLabel lblAlter;
    JLabel lblArt;
    JLabel lblBau;
    JLabel lblBemerkung;
    JLabel lblBeratung;
    JLabel lblBetrag;
    JLabel lblEingang;
    JLabel lblFaellung;
    JLabel lblGeom;
    JLabel lblGutachten;
    JLabel lblHoehe;
    JLabel lblKarte;
    JLabel lblKroneArr;
    JLabel lblLadenErsatz;
    JLabel lblLadenFest;
    JLabel lblMassnahmeArr;
    JLabel lblOhne;
    JLabel lblPrivat;
    JLabel lblStammArr;
    JLabel lblSturm;
    JLabel lblUmfang;
    JLabel lblWurzelArr;
    JList lstErsatz;
    JList lstFest;
    JPanel panControlsNewErsatz;
    JPanel panControlsNewFest;
    JPanel panErsatz;
    JPanel panErsatzMain;
    JPanel panFest;
    JPanel panFestMain;
    JPanel panGeometrie;
    JPanel panLage;
    DefaultPreviewMapPanel panPreviewMap;
    JPanel panSchaden;
    JPanel panZahlung;
    JPanel pnlCard1;
    RoundedPanel rpKarte;
    JScrollPane scpBemerkung;
    JScrollPane scpLaufendeErsatz;
    JScrollPane scpLaufendeFest;
    SemiRoundedPanel semiRoundedPanel7;
    JSpinner spHoehe;
    JSpinner spUmfang;
    JTextArea taBemerkung;
    JTextField txtAlter;
    JTextField txtBetrag;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BaumSchadenPanel object.
     */
    public BaumSchadenPanel() {
        this(null);
    }

    
    
    /**
     * Creates new form BaumSchadenPanel.
     *
     * @param bclInstance
     */
    public BaumSchadenPanel(final BaumChildrenLoader bclInstance) {
        this.baumChildrenLoader = bclInstance;
        if (bclInstance != null){
            this.editor = bclInstance.getParentOrganizer().isEditor();
        } else {
            this.editor = false;
        }
        initComponents();
        if (editor) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeomSchaden).setLocalRenderFeatureString(FIELD__GEOM);
        }
        for (final DefaultBindableLabelsPanel labelsPanel : Arrays.asList(blpKrone, blpStamm, blpWurzel, blpMassnahme)) {
            labelsPanel.initWithConnectionContext(getConnectionContext());
        }
        if (getBaumChildrenLoader() != null){
            loadChildrenListener = new BaumSchadenPanel.LoaderListener();
            getBaumChildrenLoader().addListener(loadChildrenListener);
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
    
    public boolean prepareForSave(final CidsBean saveBean) {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();
        boolean noErrorErsatz = true;
        boolean noErrorFest = true;
        
        final List<CidsBean> listErsatz = getBaumChildrenLoader().getMapValueErsatz(saveBean.getPrimaryKeyValue());
        final List<CidsBean> listFest = getBaumChildrenLoader().getMapValueFest(saveBean.getPrimaryKeyValue());
        //Ersatzpflanzungen ueberpruefen
        if (listErsatz != null && !(listErsatz.isEmpty())){
            for (final CidsBean ersatzBean : listErsatz) {
                try {
                    noErrorErsatz = baumErsatzPanel.prepareForSave(ersatzBean);
                    if(!noErrorErsatz) {
                        break;
                    }
                } catch (final Exception ex) {
                    noErrorErsatz = false;
                    LOG.error(ex, ex);
                }
            }
        }
        //Festsetzungen ueberpruefen
        if (listFest != null && !(listFest.isEmpty())){
            for (final CidsBean festBean : listFest) {
                try {
                    noErrorFest = baumFestsetzungPanel.prepareForSave(festBean);
                    if(!noErrorFest) {
                        break;
                    }
                } catch (final Exception ex) {
                    noErrorFest = false;
                    LOG.error(ex, ex);
                }
            }
        }
        //Hoehe richtig abspreichern
        try {
            saveBean.setProperty(FIELD__HOEHE, new DecimalFormat("00.0").format(spHoehe.getValue())); 
        } catch (final Exception ex) {
            LOG.warn("Height not formatted.", ex);
            save = false;
        }
        
        // Alter muss, wenn angegeben, eine Ganzzahl sein
        try {
            if (saveBean.getProperty(FIELD__ALTER) != null) {
                try {
                    Integer.parseInt(saveBean.getProperty(FIELD__ALTER).toString());
                } catch (NumberFormatException e) {
                    LOG.warn("Wrong count specified. Skip persisting.", e);
                    errorMessage.append(NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_WRONGAGE));
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("age not given.", ex);
            save = false;
        }
        
        //Art muss angegeben werden
        try {
            if (saveBean.getProperty(FIELD__ART) == null) {
                LOG.warn("No name specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_NOART));
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Countl not given.", ex);
            save = false;
        }

        //Geometrie vorhanden und Punkt
        try {
            if (saveBean.getProperty(FIELD__GEOREFERENZ) == null) {
                LOG.warn("No geom specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_NOGEOM));
            } else {
                final CidsBean geom_pos = (CidsBean)saveBean.getProperty(FIELD__GEOREFERENZ);
                if (!((Geometry)geom_pos.getProperty(FIELD__GEO_FIELD)).getGeometryType().equals(GEOMTYPE)) {
                    LOG.warn("Wrong geom specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_WRONGGEOM));
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Geom not given.", ex);
            save = false;
        }
        
        //Haekchen Wurzel gesetzt?
        try {
            Collection<CidsBean> collectionWurzel =  saveBean.getBeanCollectionProperty(FIELD__WURZEL_ARR);
            if (collectionWurzel != null && !collectionWurzel.isEmpty()) {
                if (!(Objects.equals(saveBean.getProperty(FIELD__WURZEL), true))){
                    LOG.warn("No wurzel specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_NOWURZEL));
                }
            } 
        } catch (final MissingResourceException ex) {
            LOG.warn("wurzel not given.", ex);
            save = false;
        }
        
        //Haekchen Stamm gesetzt?
        try {
            Collection<CidsBean> collectionStamm =  saveBean.getBeanCollectionProperty(FIELD__STAMM_ARR);
            if (collectionStamm != null && !collectionStamm.isEmpty()) {
                if (!(Objects.equals(saveBean.getProperty(FIELD__STAMM), true))){
                    LOG.warn("No stamm specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_NOSTAMM));
                }
            } 
        } catch (final MissingResourceException ex) {
            LOG.warn("stamm not given.", ex);
            save = false;
        }
        
        //Haekchen Krone gesetzt?
        try {
            Collection<CidsBean> collectionKrone =  saveBean.getBeanCollectionProperty(FIELD__KRONE_ARR);
            if (collectionKrone != null && !collectionKrone.isEmpty()) {
                if (! (Objects.equals(saveBean.getProperty(FIELD__KRONE), true))){
                    LOG.warn("No krone specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_NOKRONE));
                }
            } 
        } catch (final MissingResourceException ex) {
            LOG.warn("krone not given.", ex);
            save = false;
        }
        
        //Haekchen abgestorben gesetzt?
        try {
            if (Objects.equals(saveBean.getProperty(FIELD__OHNE), true)){
               if (Objects.equals(saveBean.getProperty(FIELD__WURZEL), true)){
                    LOG.warn("Wrong wurzel specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_WRONGWURZEL));
                }
                if (Objects.equals(saveBean.getProperty(FIELD__STAMM), true)){
                    LOG.warn("Wrong stamm specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_WRONGSTAMM));
                }
                if (Objects.equals(saveBean.getProperty(FIELD__KRONE), true)){
                    LOG.warn("Wrong krone specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_WRONGKRONE));
                }
                if (Objects.equals(saveBean.getProperty(FIELD__STURM), true)){
                    LOG.warn("Wrong sturm specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_WRONGSTURM));
                }
                if (Objects.equals(saveBean.getProperty(FIELD__ABGESTORBEN), true)){
                    LOG.warn("Wrong abgestorben specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_WRONGABG));
                }
            } 
        } catch (final MissingResourceException ex) {
            LOG.warn("abgestorben set.", ex);
            save = false;
        }
        
        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_PANE_TITLE),
                JOptionPane.WARNING_MESSAGE);

            return false;
        }
        return save & noErrorErsatz & noErrorFest;
    }
    
    //~ Methods ----------------------------------------------------------------
   /*
    public boolean everythingOk (CidsBean cbOkay){
       */
    @Override
    public ConnectionContext getConnectionContext() {
        return baumChildrenLoader.getParentOrganizer().getConnectionContext();
    }
    
    public void setChangeFlag(){
        cidsBean.setArtificialChangeFlag(true);
        if ((getBaumChildrenLoader() != null) && 
                (getBaumChildrenLoader().getParentOrganizer() != null) &&
                (getBaumChildrenLoader().getParentOrganizer().getCidsBean() != null)) {
            getBaumChildrenLoader().getParentOrganizer().getCidsBean().setArtificialChangeFlag(true);
        }
    }
     
    @Override
    public void dispose() {
        bindingGroup.unbind();
        cidsBean = null;
        if (this.editor && cbGeomSchaden != null) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeomSchaden).dispose();
            ((DefaultCismapGeometryComboBoxEditor)cbGeomSchaden).setCidsMetaObject(null);
            cbGeomSchaden = null;
        }
        baumFestsetzungPanel.dispose();
        baumErsatzPanel.dispose();
        ((DefaultListModel)lstFest.getModel()).clear();
        getBaumChildrenLoader().removeListener(loadChildrenListener);
    }

    @Override
    public CidsBean getCidsBean() {
        return this.cidsBean;
    }
    

    @Override
    public void setCidsBean(CidsBean cidsBean) {
        if (!(Objects.equals(this.cidsBean, cidsBean))){
            if (editor && (this.cidsBean != null)) {
                this.cidsBean.removePropertyChangeListener(changeListener);
            }
            try{
                labelsPanels.clear();
                blpKrone.clear();
                blpStamm.clear();
                blpWurzel.clear();
                blpMassnahme.clear();
                bindingGroup.unbind();
                this.cidsBean = cidsBean;
                  
                if (this.cidsBean != null){       
                    zeigeKinderFest();
                    zeigeKinderErsatz();
                    //Wenn mit mehreren Geoms(Liste) gearbeitet wird
                    if (editor) {
                        ((DefaultCismapGeometryComboBoxEditor)cbGeomSchaden).setCidsMetaObject(cidsBean.getMetaObject());
                        ((DefaultCismapGeometryComboBoxEditor)cbGeomSchaden).initForNewBinding();
                    }
                    DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                        bindingGroup,
                        this.cidsBean,
                        getConnectionContext());
                } else {
                    setErsatzBeans(null);
                    setFestBeans(null);
                    if (editor) {
                        ((DefaultCismapGeometryComboBoxEditor)cbGeomSchaden).initForNewBinding();
                        cbGeomSchaden.setSelectedIndex(-1);
                    }
                }
                
                setMapWindow();
                bindingGroup.bind();
                if (editor && (this.cidsBean != null)) {
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

                

                if(cidsBean != null){
                    if (getBaumChildrenLoader().getMapValueErsatz
                            (this.cidsBean.getPrimaryKeyValue()) != null && 
                        getActiveBeans(getBaumChildrenLoader().getMapValueErsatz
                            (this.cidsBean.getPrimaryKeyValue())) > 0) {
                        lstErsatz.setSelectedIndex(0);
                    }
                    if (getBaumChildrenLoader().getMapValueFest
                            (this.cidsBean.getPrimaryKeyValue()) != null && 
                        getActiveBeans(getBaumChildrenLoader().getMapValueFest
                            (this.cidsBean.getPrimaryKeyValue())) > 0) {
                        lstFest.setSelectedIndex(0);
                    }
                    labelsPanels.addAll(Arrays.asList(blpKrone, blpStamm, blpWurzel, blpMassnahme));
                    if (cidsBean.getMetaObject().getStatus() == MetaObject.NEW){
                        getBaumChildrenLoader().setLoadingCompletedWithoutError(true);
                        allowAddRemove();
                    }
                }
                if(editor){
                    cbGeomSchaden.updateUI();
                }
            } catch (final Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        if (!editor){
            setReadOnly();
        }
    }
    
    public void allowAddRemove(){
        if (getBaumChildrenLoader().getLoadingCompletedWithoutError()){
            if (editor){
                btnAddNewErsatz.setEnabled(true);
                btnAddNewFest.setEnabled(true);
                btnRemoveErsatz.setEnabled(true);
                btnRemoveFest.setEnabled(true);
            }
            lblLadenErsatz.setVisible(false);
            lblLadenFest.setVisible(false);
        } 
    }
    private void zeigeKinderFest(){
        setFestBeans(getBaumChildrenLoader().getMapValueFest(cidsBean.getPrimaryKeyValue()));
    }
    
    private void zeigeKinderErsatz(){
        setErsatzBeans(getBaumChildrenLoader().getMapValueErsatz(cidsBean.getPrimaryKeyValue()));
    }
    
    private void zeigeErrorFest(){
        scpLaufendeFest.setViewportView(new JList<>(MODEL_ERROR));
        JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_ERROR_FEST),
                NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_PANE_TITLE_ERROR_FEST),
                JOptionPane.WARNING_MESSAGE);
    }
    
    private void zeigeErrorErsatz(){
        scpLaufendeErsatz.setViewportView(new JList<>(MODEL_ERROR));
        JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_ERROR_ERSATZ),
                NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_PANE_TITLE_ERROR_ERSATZ),
                JOptionPane.WARNING_MESSAGE);
    }
    private void setReadOnly(){
        RendererTools.makeReadOnly(txtAlter);
        RendererTools.makeReadOnly(chPrivat);
        RendererTools.makeDoubleSpinnerWithoutButtons(spHoehe, 1);
        RendererTools.makeReadOnly(spHoehe);
        RendererTools.makeDoubleSpinnerWithoutButtons(spUmfang, 1);
        RendererTools.makeReadOnly(spUmfang);
        RendererTools.makeReadOnly(cbArt);
        RendererTools.makeReadOnly(chOhne);
        RendererTools.makeReadOnly(chBau);
        RendererTools.makeReadOnly(chAbgest);
        RendererTools.makeReadOnly(chSturm);
        RendererTools.makeReadOnly(chBeratung);
        RendererTools.makeReadOnly(chGutachten);
        RendererTools.makeReadOnly(chKrone);
        RendererTools.makeReadOnly(chStamm);
        RendererTools.makeReadOnly(chWurzel);
        RendererTools.makeReadOnly(taBemerkung);
        RendererTools.makeReadOnly(chFaellung);
        RendererTools.makeReadOnly(txtBetrag);
        RendererTools.makeReadOnly(chEingang);
        panControlsNewErsatz.setVisible(editor);
        panControlsNewFest.setVisible(editor);
    }
    
    public void prepareErsatz(){
        if (this.cidsBean != null){
            if (getBaumChildrenLoader().getMapValueErsatz
                    (this.cidsBean.getPrimaryKeyValue()) != null && 
                getActiveBeans(getBaumChildrenLoader().getMapValueErsatz
                    (this.cidsBean.getPrimaryKeyValue())) > 0) {
                lstErsatz.setSelectedIndex(0);
            }
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
        if (this.cidsBean != null){
            if (getBaumChildrenLoader().getMapValueFest
                    (this.cidsBean.getPrimaryKeyValue()) != null && 
                getActiveBeans(getBaumChildrenLoader().getMapValueFest
                    (this.cidsBean.getPrimaryKeyValue())) > 0) {
                lstFest.setSelectedIndex(0);
            } 
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
        try {
            baumErsatzPanel.setCidsBean(null);
            ((DefaultListModel)lstErsatz.getModel()).clear(); 
            if (cidsBeans != null){
                for(final Object bean:cidsBeans){
                    if (bean instanceof CidsBean && ((CidsBean)bean).getMetaObject().getStatus()!= MetaObject.TO_DELETE){
                        ((DefaultListModel)lstErsatz.getModel()).addElement(bean);
                    }
                }
            }
            prepareErsatz();
        } catch (final Exception ex) {
                Exceptions.printStackTrace(ex);
                LOG.warn("ersatz list not cleared.", ex);
        }
    }
    public void setFestBeans(final List<CidsBean> cidsBeans) {
        try {
            baumFestsetzungPanel.setCidsBean(null);
            ((DefaultListModel)lstFest.getModel()).clear();
            if (cidsBeans != null){
                for(final Object bean:cidsBeans){
                    if (bean instanceof CidsBean && ((CidsBean)bean).getMetaObject().getStatus()!= MetaObject.TO_DELETE){
                        ((DefaultListModel)lstFest.getModel()).addElement(bean);
                    }
                }
            }
            prepareFest();
        } catch (final Exception ex) {
                Exceptions.printStackTrace(ex);
                LOG.warn("fest list not cleared.", ex);
        }
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
                    lblKarte.setText("Lage");
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
                    lblKarte.setText("Lage nicht ausgewählt");
                }
            } catch (final Exception ex) {
                Exceptions.printStackTrace(ex);
                LOG.warn("Map window not set.", ex);
            } 
        } else{
            lblKarte.setText("Lage nicht ausgewählt");
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
            TABLE_NAME,
            1,
            800,
            600);
    }
    class LoaderListener implements BaumChildrenLoader.Listener{

        @Override
        public void loadingCompleteSchaden(Integer idMeldung) {
            
        }

        @Override
        public void loadingCompleteOrt(Integer idMeldung) {
            
        }

        @Override
        public void loadingErrorOrt(Integer idMeldung) {
            
        }

        @Override
        public void loadingErrorSchaden(Integer idMeldung) {
             
        }

        @Override
        public void loadingComplete() {
            allowAddRemove();
        }

        @Override
        public void loadingCompleteFest(Integer idSchaden) {
            if (cidsBean != null){
                if(Objects.equals(cidsBean.getPrimaryKeyValue(), idSchaden)){
                    lblLadenFest.setVisible(false);
                    zeigeKinderFest();
                }
            }
        }

        @Override
        public void loadingCompleteErsatz(Integer idSchaden) {
            if (cidsBean != null){
                if(Objects.equals(cidsBean.getPrimaryKeyValue(), idSchaden)){
                    lblLadenErsatz.setVisible(false);
                    zeigeKinderErsatz();
                }
            }
        }

        @Override
        public void loadingErrorFest(Integer idMeldung) {
            zeigeErrorFest();
        }

        @Override
        public void loadingErrorErsatz(Integer idMeldung) {
            zeigeErrorErsatz();
        }

        @Override
        public void loadingCompleteMeldung() {
        }
        
    }
}
    

