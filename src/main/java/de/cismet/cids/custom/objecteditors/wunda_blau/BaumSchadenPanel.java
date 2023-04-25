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

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.logging.Level;

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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.utils.BaumChildrenLoader;
import de.cismet.cids.custom.objecteditors.utils.BaumConfProperties;
import de.cismet.cids.custom.objecteditors.utils.RendererTools;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;
import de.cismet.cids.dynamics.Disposable;

import de.cismet.cids.editors.DefaultBindableLabelsPanel;
import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultBindableScrollableComboBox;
import de.cismet.cids.editors.DefaultCustomObjectEditor;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public final class BaumSchadenPanel extends javax.swing.JPanel implements Disposable,
    CidsBeanStore,
    ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(BaumSchadenPanel.class);
    public static final String GEOMTYPE = "Point";

    private static DefaultBindableReferenceCombo.Option SORTING_OPTION =
        new DefaultBindableReferenceCombo.SortingColumnOption("name");
    private static final MetaClass MC__ART;
    private static final MetaClass MC__INST;

    static {
        final ConnectionContext connectionContext = ConnectionContext.create(
                ConnectionContext.Category.STATIC,
                BaumSchadenPanel.class.getSimpleName());
        MC__ART = ClassCacheMultiple.getMetaClass(
                "WUNDA_BLAU",
                "BAUM_ART",
                connectionContext);
        MC__INST = ClassCacheMultiple.getMetaClass(
                "WUNDA_BLAU",
                "BAUM_INSTITUTION",
                connectionContext);
    }

    public static final String FIELD__ALTER = "alter";                                         // baum_schaden
    public static final String FIELD__HOEHE = "hoehe";                                         // baum_schaden
    public static final String FIELD__UMFANG = "umfang";                                       // baum_schaden
    public static final String FIELD__ART = "fk_art";                                          // baum_schaden
    public static final String FIELD__INST = "fk_institution";                                 // baum_schaden
    public static final String FIELD__GEOM = "fk_geom";                                        // baum_schaden
    public static final String FIELD__PRIVAT = "privatbaum";                                   // baum_schaden
    public static final String FIELD__OHNE = "ohne_schaden";                                   // baum_schaden
    public static final String FIELD__KRONE = "kronenschaden";                                 // baum_schaden
    public static final String FIELD__GEFAHR = "gefahrensbaum";                                // baum_schaden
    public static final String FIELD__STAMM = "stammschaden";                                  // baum_schaden
    public static final String FIELD__WURZEL = "wurzelschaden";                                // baum_schaden
    public static final String FIELD__KRONE_ARR = "arr_krone";                                 // baum_schaden
    public static final String FIELD__STAMM_ARR = "arr_stamm";                                 // baum_schaden
    public static final String FIELD__WURZEL_ARR = "arr_wurzel";                               // baum_schaden
    public static final String FIELD__MASSNAHME = "arr_massnahme";                             // baum_schaden
    public static final String FIELD__STURM = "sturmschaden";                                  // baum_schaden
    public static final String FIELD__ABGESTORBEN = "abgestorben";                             // baum_schaden
    public static final String FIELD__BAU = "fk_bau";                                          // baum_schaden
    public static final String FIELD__BAU_TEXT = "bau";                                        // baum_schaden
    public static final String FIELD__BAU_SCHLUESSEL = "fk_bau.schluessel";                    // baum_schaden -->
                                                                                               // baum_bau
    public static final String FIELD__GUTACHTEN = "gutachten";                                 // baum_schaden
    public static final String FIELD__BERATUNG = "baumberatung";                               // baum_schaden
    public static final String FIELD__BEMERKUNG = "bemerkung";                                 // baum_schaden
    public static final String FIELD__BETRAG = "betrag";                                       // baum_schaden
    public static final String FIELD__EINGANG = "eingegangen";                                 // baum_schaden
    public static final String FIELD__ABGELEHNT = "abgelehnt";                                 // baum_schaden
    public static final String FIELD__FAELLUNG = "faellung";                                   // baum_schaden
    public static final String FIELD__KLEISTUNG = "keine_leistung";                            // baum_schaden
    public static final String FIELD__BEGRUENDUNG = "begruendung";                             // baum_schaden
    public static final String FIELD__FK_ORDNUNG = "fk_ordnungswidrigkeit";                    // baum_schaden
    public static final String FIELD__ORDNUNG_SCHLUESSEL = "fk_ordnungswidrigkeit.schluessel"; // baum_ordnungwidrigkeit
    public static final String FIELD__FK_SCHADEN = "fk_schaden";                               // baum_ersatz
    public static final String FIELD__SELBST = "selbststaendig";                               // baum_ersatz
    public static final String FIELD__DISPENS = "dispensbau";                                  // baum_ersatz
    public static final String FIELD__ID = "id";                                               // baum_schaden
    public static final String FIELD__FK_MELDUNG = "fk_meldung";                               // baum_schaden
    public static final String FIELD__MDATUM = "datum";                                        // baum_meldung

    public static final String FIELD__GEOREFERENZ = "fk_geom";                      // baum_schaden
    public static final String FIELD__GEO_FIELD = "geo_field";                      // geom
    public static final String FIELD__GEOREFERENZ__GEO_FIELD = "fk_geom.geo_field"; // baum_schaden_geom

    public static final String TABLE_GEOM = "geom";
    public static final String TABLE_FEST = "BAUM_FESTSETZUNG";
    public static final String TABLE_ERSATZ = "BAUM_ERSATZ";
    public static final String TABLE_NAME = "baum_schaden";

    public static final String BUNDLE_NOART = "BaumSchadenPanel.isOkForSaving().noArt";
    public static final String BUNDLE_NOINST = "BaumSchadenPanel.isOkForSaving().noInst";
    public static final String BUNDLE_NOLEISTUNG = "BaumSchadenPanel.isOkForSaving().noLeistung";
    public static final String BUNDLE_NOBAU = "BaumSchadenPanel.isOkForSaving().noBau";
    public static final String BUNDLE_NOWURZEL = "BaumSchadenPanel.isOkForSaving().noWurzel";
    public static final String BUNDLE_NOSTAMM = "BaumSchadenPanel.isOkForSaving().noStamm";
    public static final String BUNDLE_NOKRONE = "BaumSchadenPanel.isOkForSaving().noKrone";
    public static final String BUNDLE_WRONGWURZEL = "BaumSchadenPanel.isOkForSaving().wrongWurzel";
    public static final String BUNDLE_WRONGSTAMM = "BaumSchadenPanel.isOkForSaving().wrongStamm";
    public static final String BUNDLE_WRONGKRONE = "BaumSchadenPanel.isOkForSaving().wrongKrone";
    public static final String BUNDLE_WRONGABG = "BaumSchadenPanel.isOkForSaving().wrongAbgestorben";
    public static final String BUNDLE_WRONGSTURM = "BaumSchadenPanel.isOkForSaving().wrongSturm";
    public static final String BUNDLE_NOGEOM = "BaumSchadenPanel.isOkForSaving().noGeom";
    public static final String BUNDLE_WRONGGEOM = "BaumSchadenPanel.isOkForSaving().wrongGeom";
    public static final String BUNDLE_ABGELEHNT = "BaumSchadenPanel.isOkForSaving().abgelehnt";

    public static final String BUNDLE_PANE_TITLE_ERROR_FEST = "BaumSchadenPanel.zeigeErrorFest().JOptionPane.title";
    public static final String BUNDLE_PANE_TITLE_ERROR_ERSATZ = "BaumSchadenPanel.zeigeErrorErsatz().JOptionPane.title";
    public static final String BUNDLE_ERROR_FEST = "BaumSchadenPanel.zeigeErrorFest().JOptionPane.meldung";
    public static final String BUNDLE_ERROR_ERSATZ = "BaumSchadenPanel.zeigeErrorErsatz().JOptionPane.meldung";
    public static final String BUNDLE_WHICH = "BaumSchadenPanel.isOkForSaving().welcherSchaden";
    public static final String BUNDLE_MESSAGE = "BaumSchadenPanel.isOkForSaving().welcheMeldung";
    public static final String BUNDLE_PANE_PREFIX = "BaumSchadenPanel.isOkForSaving().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX = "BaumSchadenPanel.isOkForSaving().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "BaumSchadenPanel.isOkForSaving().JOptionPane.title";
    public static final String BUNDLE_NOSAVE_MESSAGE = "BaumSchadenPanel.noSave().message";
    public static final String BUNDLE_NOSAVE_TITLE = "BaumSchadenPanel.noSave().title";

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
        lblInst = new JLabel();
        cbInst = new DefaultBindableScrollableComboBox(MC__INST);
        lblHoehe = new JLabel();
        lblUmfang = new JLabel();
        lblArt = new JLabel();
        cbArt = new DefaultBindableScrollableComboBox(MC__ART);
        if (isEditor()) {
            lblGeom = new JLabel();
        }
        if (isEditor()) {
            cbGeomSchaden = new DefaultCismapGeometryComboBoxEditor();
        }
        panGeometrie = new JPanel();
        baumLagePanel = new BaumLagePanel();
        lblOhne = new JLabel();
        chOhne = new JCheckBox();
        lblBau = new JLabel();
        cbBau = new DefaultBindableReferenceCombo(true);
        txtBau = new JTextField();
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
        blpKrone = new DefaultBindableLabelsPanel(isEditor(), "Kronenschaden:", SORTING_OPTION);
        lblStammArr = new JLabel();
        chStamm = new JCheckBox();
        filler22 = new Box.Filler(new Dimension(0, 28), new Dimension(0, 28), new Dimension(32767, 28));
        blpStamm = new DefaultBindableLabelsPanel(isEditor(), "Stammschaden:", SORTING_OPTION);
        lblWurzelArr = new JLabel();
        chWurzel = new JCheckBox();
        filler23 = new Box.Filler(new Dimension(0, 28), new Dimension(0, 28), new Dimension(32767, 28));
        blpWurzel = new DefaultBindableLabelsPanel(isEditor(), "Wurzelschaden:", SORTING_OPTION);
        lblMassnahmeArr = new JLabel();
        filler24 = new Box.Filler(new Dimension(0, 28), new Dimension(0, 28), new Dimension(32767, 28));
        blpMassnahme = new DefaultBindableLabelsPanel(isEditor(), "Maßnahmen:", SORTING_OPTION);
        filler6 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        lblBemerkung = new JLabel();
        scpBemerkung = new JScrollPane();
        taBemerkung = new JTextArea();
        lblFaellung = new JLabel();
        chFaellung = new JCheckBox();
        lblAbgelehnt = new JLabel();
        chAbgelehnt = new JCheckBox();
        spHoehe = new JSpinner();
        spUmfang = new JSpinner();
        spAlter = new JSpinner();
        lblEfeu = new JLabel();
        chEfeu = new JCheckBox();
        lblGefahr = new JLabel();
        lblKLeistung = new JLabel();
        chGefahr = new JCheckBox();
        chKLeistung = new JCheckBox();
        lblOrdnung = new JLabel();
        cbOrdnung = new DefaultBindableReferenceCombo(true);
        txtAnmerkung = new JTextField();
        txtBegruendung = new JTextField();
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

        final FormListener formListener = new FormListener();

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
        lblAlter.setName("lblAlter");                // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblAlter, gridBagConstraints);

        lblInst.setFont(new Font("Tahoma", 1, 11));                                        // NOI18N
        Mnemonics.setLocalizedText(
            lblInst,
            NbBundle.getMessage(BaumSchadenPanel.class, "BaumSchadenPanel.lblInst.text")); // NOI18N
        lblInst.setName("lblInst");                                                        // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblInst, gridBagConstraints);

        cbInst.setFont(new Font("Dialog", 0, 12)); // NOI18N
        cbInst.setMaximumRowCount(15);
        cbInst.setEnabled(false);
        cbInst.setName("cbInst");                  // NOI18N
        cbInst.setPreferredSize(new Dimension(100, 24));

        Binding binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.fk_institution}"),
                cbInst,
                BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(cbInst, gridBagConstraints);

        lblHoehe.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblHoehe, "Höhe [m]:");
        lblHoehe.setName("lblHoehe");                // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblHoehe, gridBagConstraints);

        lblUmfang.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblUmfang, "Umfang [cm]:");
        lblUmfang.setName("lblUmfang");               // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblUmfang, gridBagConstraints);

        lblArt.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblArt, "Art:");
        lblArt.setName("lblArt");                  // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblArt, gridBagConstraints);

        cbArt.setFont(new Font("Dialog", 0, 12)); // NOI18N
        cbArt.setMaximumRowCount(15);
        cbArt.setEnabled(false);
        cbArt.setName("cbArt");                   // NOI18N
        cbArt.setPreferredSize(new Dimension(100, 24));

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.fk_art}"),
                cbArt,
                BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(cbArt, gridBagConstraints);

        if (isEditor()) {
            lblGeom.setFont(new Font("Tahoma", 1, 11)); // NOI18N
            Mnemonics.setLocalizedText(lblGeom, "Geometrie:");
            lblGeom.setName("lblGeom");                 // NOI18N
        }
        if (isEditor()) {
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.ipady = 10;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(2, 0, 2, 5);
            panSchaden.add(lblGeom, gridBagConstraints);
        }

        if (isEditor()) {
            cbGeomSchaden.setFont(new Font("Dialog", 0, 12)); // NOI18N
            cbGeomSchaden.setEnabled(false);
            cbGeomSchaden.setName("cbGeomSchaden");           // NOI18N

            binding = Bindings.createAutoBinding(
                    AutoBinding.UpdateStrategy.READ_WRITE,
                    this,
                    ELProperty.create("${cidsBean.fk_geom}"),
                    cbGeomSchaden,
                    BeanProperty.create("selectedItem"));
            binding.setSourceNullValue(null);
            binding.setSourceUnreadableValue(null);
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeomSchaden).getConverter());
            bindingGroup.addBinding(binding);
        }
        if (isEditor()) {
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.gridwidth = 4;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            panSchaden.add(cbGeomSchaden, gridBagConstraints);
        }

        panGeometrie.setName("panGeometrie"); // NOI18N
        panGeometrie.setOpaque(false);
        panGeometrie.setPreferredSize(new Dimension(205, 200));
        panGeometrie.setLayout(new GridBagLayout());

        baumLagePanel.setName("baumLagePanel"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panGeometrie.add(baumLagePanel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 9;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(0, 5, 5, 3);
        panSchaden.add(panGeometrie, gridBagConstraints);

        lblOhne.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblOhne, "ohne:");
        lblOhne.setName("lblOhne");                 // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblOhne, gridBagConstraints);

        chOhne.setContentAreaFilled(false);
        chOhne.setEnabled(false);
        chOhne.setName("chOhne"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.ohne_schaden}"),
                chOhne,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chOhne, gridBagConstraints);

        lblBau.setFont(new Font("Tahoma", 1, 11));                                                                       // NOI18N
        Mnemonics.setLocalizedText(lblBau, NbBundle.getMessage(BaumSchadenPanel.class, "BaumSchadenPanel.lblBau.text")); // NOI18N
        lblBau.setName("lblBau");                                                                                        // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblBau, gridBagConstraints);

        cbBau.setEnabled(false);
        cbBau.setName("cbBau"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.fk_bau}"),
                cbBau,
                BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(cbBau, gridBagConstraints);

        txtBau.setEnabled(false);
        txtBau.setMinimumSize(new Dimension(10, 24));
        txtBau.setName("txtBau"); // NOI18N
        txtBau.setPreferredSize(new Dimension(10, 24));

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.bau}"),
                txtBau,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 2);
        panSchaden.add(txtBau, gridBagConstraints);

        lblAbgest.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblAbgest, "abgestorben:");
        lblAbgest.setName("lblAbgest");               // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblAbgest, gridBagConstraints);

        chAbgest.setContentAreaFilled(false);
        chAbgest.setEnabled(false);
        chAbgest.setName("chAbgest"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.abgestorben}"),
                chAbgest,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chAbgest, gridBagConstraints);

        lblSturm.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblSturm, "Sturm:");
        lblSturm.setName("lblSturm");                // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblSturm, gridBagConstraints);

        chSturm.setContentAreaFilled(false);
        chSturm.setEnabled(false);
        chSturm.setName("chSturm"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.sturmschaden}"),
                chSturm,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chSturm, gridBagConstraints);

        lblBeratung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblBeratung, "Beratung:");
        lblBeratung.setName("lblBeratung");             // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblBeratung, gridBagConstraints);

        chBeratung.setContentAreaFilled(false);
        chBeratung.setEnabled(false);
        chBeratung.setName("chBeratung"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.baumberatung}"),
                chBeratung,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chBeratung, gridBagConstraints);

        lblGutachten.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblGutachten, "Gutachten vorh.:");
        lblGutachten.setName("lblGutachten");            // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblGutachten, gridBagConstraints);

        chGutachten.setContentAreaFilled(false);
        chGutachten.setEnabled(false);
        chGutachten.setName("chGutachten"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.gutachten}"),
                chGutachten,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chGutachten, gridBagConstraints);

        lblKroneArr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblKroneArr, "Kronenschaden:");
        lblKroneArr.setName("lblKroneArr");             // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblKroneArr, gridBagConstraints);

        chKrone.setContentAreaFilled(false);
        chKrone.setEnabled(false);
        chKrone.setName("chKrone"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.kronenschaden}"),
                chKrone,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chKrone, gridBagConstraints);

        blpKrone.setEnabled(false);
        blpKrone.setManageableButtonText(NbBundle.getMessage(
                BaumSchadenPanel.class,
                "BaumSchadenPanel.blpKrone.manageableButtonText")); // NOI18N
        blpKrone.setOpaque(false);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.arr_krone}"),
                blpKrone,
                BeanProperty.create("selectedElements"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 5, 2);
        panSchaden.add(blpKrone, gridBagConstraints);

        lblStammArr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblStammArr, "Stammschaden:");
        lblStammArr.setName("lblStammArr");             // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblStammArr, gridBagConstraints);

        chStamm.setContentAreaFilled(false);
        chStamm.setEnabled(false);
        chStamm.setName("chStamm"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.stammschaden}"),
                chStamm,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chStamm, gridBagConstraints);

        filler22.setName("filler22"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 0, 5, 0);
        panSchaden.add(filler22, gridBagConstraints);

        blpStamm.setEnabled(false);
        blpStamm.setManageableButtonText(NbBundle.getMessage(
                BaumSchadenPanel.class,
                "BaumSchadenPanel.blpStamm.manageableButtonText")); // NOI18N
        blpStamm.setName("blpStamm");                               // NOI18N
        blpStamm.setOpaque(false);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.arr_stamm}"),
                blpStamm,
                BeanProperty.create("selectedElements"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 5, 2);
        panSchaden.add(blpStamm, gridBagConstraints);

        lblWurzelArr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblWurzelArr, "Wurzelschaden:");
        lblWurzelArr.setName("lblWurzelArr");            // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblWurzelArr, gridBagConstraints);

        chWurzel.setContentAreaFilled(false);
        chWurzel.setEnabled(false);
        chWurzel.setName("chWurzel"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.wurzelschaden}"),
                chWurzel,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chWurzel, gridBagConstraints);

        filler23.setName("filler23"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 15;
        panSchaden.add(filler23, gridBagConstraints);

        blpWurzel.setEnabled(false);
        blpWurzel.setManageableButtonText(NbBundle.getMessage(
                BaumSchadenPanel.class,
                "BaumSchadenPanel.blpWurzel.manageableButtonText")); // NOI18N
        blpWurzel.setName("blpWurzel");                              // NOI18N
        blpWurzel.setOpaque(false);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.arr_wurzel}"),
                blpWurzel,
                BeanProperty.create("selectedElements"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 5, 2);
        panSchaden.add(blpWurzel, gridBagConstraints);

        lblMassnahmeArr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblMassnahmeArr, "Maßnahme:");
        lblMassnahmeArr.setName("lblMassnahmeArr");         // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblMassnahmeArr, gridBagConstraints);

        filler24.setName("filler24"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 18;
        panSchaden.add(filler24, gridBagConstraints);

        blpMassnahme.setEnabled(false);
        blpMassnahme.setManageableButtonText(NbBundle.getMessage(
                BaumSchadenPanel.class,
                "BaumSchadenPanel.blpMassnahme.manageableButtonText")); // NOI18N
        blpMassnahme.setName("blpMassnahme");                           // NOI18N
        blpMassnahme.setOpaque(false);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.arr_massnahme}"),
                blpMassnahme,
                BeanProperty.create("selectedElements"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 18;
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
        gridBagConstraints.gridheight = 8;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panSchaden.add(filler6, gridBagConstraints);

        lblBemerkung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblBemerkung, "Bemerkung:");
        lblBemerkung.setName("lblBemerkung");            // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 22;
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

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.bemerkung}"),
                taBemerkung,
                BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        scpBemerkung.setViewportView(taBemerkung);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 22;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 0, 0, 2);
        panSchaden.add(scpBemerkung, gridBagConstraints);

        lblFaellung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblFaellung, "Fällung:");
        lblFaellung.setName("lblFaellung");             // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 28;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblFaellung, gridBagConstraints);

        chFaellung.setContentAreaFilled(false);
        chFaellung.setEnabled(false);
        chFaellung.setName("chFaellung"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.faellung}"),
                chFaellung,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 28;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chFaellung, gridBagConstraints);

        lblAbgelehnt.setFont(new Font("Tahoma", 1, 11));                                        // NOI18N
        Mnemonics.setLocalizedText(
            lblAbgelehnt,
            NbBundle.getMessage(BaumSchadenPanel.class, "BaumSchadenPanel.lblAbgelehnt.text")); // NOI18N
        lblAbgelehnt.setName("lblAbgelehnt");                                                   // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 28;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblAbgelehnt, gridBagConstraints);

        chAbgelehnt.setContentAreaFilled(false);
        chAbgelehnt.setEnabled(false);
        chAbgelehnt.setName("chAbgelehnt"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.abgelehnt}"),
                chAbgelehnt,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 28;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chAbgelehnt, gridBagConstraints);

        spHoehe.setFont(new Font("Dialog", 0, 12)); // NOI18N
        spHoehe.setModel(new SpinnerNumberModel(0.0d, 0.0d, 100.0d, 0.1d));
        spHoehe.setEnabled(false);
        spHoehe.setName("spHoehe");                 // NOI18N
        spHoehe.setPreferredSize(new Dimension(75, 20));

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.hoehe}"),
                spHoehe,
                BeanProperty.create("value"));
        binding.setSourceNullValue(0.0d);
        binding.setSourceUnreadableValue(0.0d);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(spHoehe, gridBagConstraints);

        spUmfang.setFont(new Font("Dialog", 0, 12)); // NOI18N
        spUmfang.setModel(new SpinnerNumberModel(0, 0, 1000, 1));
        spUmfang.setEnabled(false);
        spUmfang.setName("spUmfang");                // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.umfang}"),
                spUmfang,
                BeanProperty.create("value"));
        binding.setSourceNullValue(0);
        binding.setSourceUnreadableValue(0);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(spUmfang, gridBagConstraints);

        spAlter.setFont(new Font("Dialog", 0, 12)); // NOI18N
        spAlter.setModel(new SpinnerNumberModel(0, 0, 500, 1));
        spAlter.setEnabled(false);
        spAlter.setName("spAlter");                 // NOI18N
        spAlter.setPreferredSize(new Dimension(75, 20));

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.alter}"),
                spAlter,
                BeanProperty.create("value"));
        binding.setSourceNullValue(0);
        binding.setSourceUnreadableValue(0);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(spAlter, gridBagConstraints);

        lblEfeu.setFont(new Font("Tahoma", 1, 11));                                        // NOI18N
        Mnemonics.setLocalizedText(
            lblEfeu,
            NbBundle.getMessage(BaumSchadenPanel.class, "BaumSchadenPanel.lblEfeu.text")); // NOI18N
        lblEfeu.setName("lblEfeu");                                                        // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblEfeu, gridBagConstraints);

        chEfeu.setContentAreaFilled(false);
        chEfeu.setEnabled(false);
        chEfeu.setName("chEfeu"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.efeu}"),
                chEfeu,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chEfeu, gridBagConstraints);

        lblGefahr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblGefahr, "Gefahrensbaum:");
        lblGefahr.setName("lblGefahr");               // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 26;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblGefahr, gridBagConstraints);

        lblKLeistung.setFont(new Font("Tahoma", 1, 11));                                        // NOI18N
        Mnemonics.setLocalizedText(
            lblKLeistung,
            NbBundle.getMessage(BaumSchadenPanel.class, "BaumSchadenPanel.lblKLeistung.text")); // NOI18N
        lblKLeistung.setName("lblKLeistung");                                                   // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 29;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblKLeistung, gridBagConstraints);

        chGefahr.setContentAreaFilled(false);
        chGefahr.setEnabled(false);
        chGefahr.setName("chGefahr"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.gefahrensbaum}"),
                chGefahr,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 26;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chGefahr, gridBagConstraints);

        chKLeistung.setContentAreaFilled(false);
        chKLeistung.setEnabled(false);
        chKLeistung.setName("chKLeistung"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.keine_leistung}"),
                chKLeistung,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 29;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(chKLeistung, gridBagConstraints);

        lblOrdnung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblOrdnung, "Ordnungswidrigkeit:");
        lblOrdnung.setName("lblOrdnung");              // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 27;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panSchaden.add(lblOrdnung, gridBagConstraints);

        cbOrdnung.setEnabled(false);
        cbOrdnung.setName("cbOrdnung"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.fk_ordnungswidrigkeit}"),
                cbOrdnung,
                BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 27;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSchaden.add(cbOrdnung, gridBagConstraints);

        txtAnmerkung.setEnabled(false);
        txtAnmerkung.setMinimumSize(new Dimension(10, 24));
        txtAnmerkung.setName("txtAnmerkung"); // NOI18N
        txtAnmerkung.setPreferredSize(new Dimension(10, 24));

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.anmerkung}"),
                txtAnmerkung,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 27;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 2);
        panSchaden.add(txtAnmerkung, gridBagConstraints);

        txtBegruendung.setEnabled(false);
        txtBegruendung.setMinimumSize(new Dimension(10, 24));
        txtBegruendung.setName("txtBegruendung"); // NOI18N
        txtBegruendung.setPreferredSize(new Dimension(10, 24));

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.begruendung}"),
                txtBegruendung,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 29;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 2);
        panSchaden.add(txtBegruendung, gridBagConstraints);

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

        lblLadenErsatz.setFont(new Font("Tahoma", 1, 11));                                        // NOI18N
        lblLadenErsatz.setForeground(new Color(153, 153, 153));
        Mnemonics.setLocalizedText(
            lblLadenErsatz,
            NbBundle.getMessage(BaumSchadenPanel.class, "BaumSchadenPanel.lblLadenErsatz.text")); // NOI18N
        lblLadenErsatz.setName("lblLadenErsatz");                                                 // NOI18N
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

        btnAddNewErsatz.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddNewErsatz.setEnabled(false);
        btnAddNewErsatz.setMaximumSize(new Dimension(39, 20));
        btnAddNewErsatz.setMinimumSize(new Dimension(39, 20));
        btnAddNewErsatz.setName("btnAddNewErsatz");                                                            // NOI18N
        btnAddNewErsatz.setPreferredSize(new Dimension(39, 25));
        btnAddNewErsatz.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panControlsNewErsatz.add(btnAddNewErsatz, gridBagConstraints);

        btnRemoveErsatz.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveErsatz.setEnabled(false);
        btnRemoveErsatz.setMaximumSize(new Dimension(39, 20));
        btnRemoveErsatz.setMinimumSize(new Dimension(39, 20));
        btnRemoveErsatz.setName("btnRemoveErsatz");                                                               // NOI18N
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
        lblBetrag.setName("lblBetrag");               // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panZahlung.add(lblBetrag, gridBagConstraints);

        txtBetrag.setEnabled(false);
        txtBetrag.setName("txtBetrag"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.betrag}"),
                txtBetrag,
                BeanProperty.create("text"));
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
        lblEingang.setName("lblEingang");              // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panZahlung.add(lblEingang, gridBagConstraints);

        chEingang.setContentAreaFilled(false);
        chEingang.setEnabled(false);
        chEingang.setName("chEingang"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.eingegangen}"),
                chEingang,
                BeanProperty.create("selected"));
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

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                lstFest,
                ELProperty.create("${selectedElement}"),
                baumFestsetzungPanel,
                BeanProperty.create("cidsBean"));
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

        lblLadenFest.setFont(new Font("Tahoma", 1, 11));                                        // NOI18N
        lblLadenFest.setForeground(new Color(153, 153, 153));
        Mnemonics.setLocalizedText(
            lblLadenFest,
            NbBundle.getMessage(BaumSchadenPanel.class, "BaumSchadenPanel.lblLadenFest.text")); // NOI18N
        lblLadenFest.setName("lblLadenFest");                                                   // NOI18N
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

        btnAddNewFest.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddNewFest.setEnabled(false);
        btnAddNewFest.setMaximumSize(new Dimension(39, 20));
        btnAddNewFest.setMinimumSize(new Dimension(39, 20));
        btnAddNewFest.setName("btnAddNewFest");                                                                // NOI18N
        btnAddNewFest.setPreferredSize(new Dimension(39, 25));
        btnAddNewFest.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panControlsNewFest.add(btnAddNewFest, gridBagConstraints);

        btnRemoveFest.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveFest.setEnabled(false);
        btnRemoveFest.setMaximumSize(new Dimension(39, 20));
        btnRemoveFest.setMinimumSize(new Dimension(39, 20));
        btnRemoveFest.setName("btnRemoveFest");                                                                   // NOI18N
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

    /**
     * Code for dispatching events from components to event handlers.
     *
     * @version  $Revision$, $Date$
     */
    private class FormListener implements ActionListener, ListSelectionListener {

        /**
         * Creates a new FormListener object.
         */
        FormListener() {
        }

        @Override
        public void actionPerformed(final ActionEvent evt) {
            if (evt.getSource() == btnAddNewErsatz) {
                BaumSchadenPanel.this.btnAddNewErsatzActionPerformed(evt);
            } else if (evt.getSource() == btnRemoveErsatz) {
                BaumSchadenPanel.this.btnRemoveErsatzActionPerformed(evt);
            } else if (evt.getSource() == btnAddNewFest) {
                BaumSchadenPanel.this.btnAddNewFestActionPerformed(evt);
            } else if (evt.getSource() == btnRemoveFest) {
                BaumSchadenPanel.this.btnRemoveFestActionPerformed(evt);
            }
        }

        @Override
        public void valueChanged(final ListSelectionEvent evt) {
            if (evt.getSource() == lstErsatz) {
                BaumSchadenPanel.this.lstErsatzValueChanged(evt);
            }
        }
    } // </editor-fold>//GEN-END:initComponents

    @Getter @Setter private static Exception errorNoSave = null;

    //~ Instance fields --------------------------------------------------------

    private BaumChildrenLoader.Listener loadChildrenListener;
    private final boolean editor;
    private CidsBean cidsBean;
    @Getter private final BaumChildrenLoader baumChildrenLoader;
    private Integer saveGeom;
    private Integer saveArt;

    private final Collection<DefaultBindableLabelsPanel> labelsPanels = new ArrayList<>();
    private final PropertyChangeListener changeListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case FIELD__ART: {
                        if (evt.getNewValue() != saveArt) {
                            setChangeFlag();
                        }
                        break;
                    }
                    case FIELD__GEOREFERENZ: {
                        if (evt.getNewValue() != saveGeom) {
                            setChangeFlag();
                        }
                        setMapWindow();
                        break;
                    }
                    case FIELD__BAU: {
                        setChangeFlag();
                        if ((getCidsBean().getProperty(FIELD__BAU) != null)
                                    && ((getCidsBean().getProperty(FIELD__BAU_SCHLUESSEL)).toString().equals("mit"))) {
                            txtBau.setEnabled(true);
                        } else {
                            txtBau.setEnabled(false);
                            txtBau.setText("");
                        }
                    }
                    case FIELD__FK_ORDNUNG: {
                        setChangeFlag();
                        if ((getCidsBean().getProperty(FIELD__FK_ORDNUNG) != null)) {
                            txtAnmerkung.setEnabled(true);
                            chFaellung.setEnabled(false);
                            if (((Integer)(getCidsBean().getProperty(FIELD__ORDNUNG_SCHLUESSEL)) == 2)
                                        || ((Integer)(getCidsBean().getProperty(FIELD__ORDNUNG_SCHLUESSEL)) == 4)) {
                                chFaellung.setSelected(true);
                            } else {
                                chFaellung.setSelected(false);
                            }
                        } else {
                            txtAnmerkung.setEnabled(false);
                            txtAnmerkung.setText("");
                            chFaellung.setEnabled(true);
                        }
                    }
                    case FIELD__KLEISTUNG: {
                        setChangeFlag();
                        if (Objects.equals(getCidsBean().getProperty(FIELD__KLEISTUNG), true)) {
                            txtBegruendung.setEnabled(true);
                        } else {
                            txtBegruendung.setEnabled(false);
                            txtBegruendung.setText("");
                        }
                    }
                    default: {
                        setChangeFlag();
                    }
                }
            }
        };

    // Variables declaration - do not modify//GEN-BEGIN:variables
    BaumErsatzPanel baumErsatzPanel;
    BaumFestsetzungPanel baumFestsetzungPanel;
    BaumLagePanel baumLagePanel;
    DefaultBindableLabelsPanel blpKrone;
    DefaultBindableLabelsPanel blpMassnahme;
    DefaultBindableLabelsPanel blpStamm;
    DefaultBindableLabelsPanel blpWurzel;
    JButton btnAddNewErsatz;
    JButton btnAddNewFest;
    JButton btnRemoveErsatz;
    JButton btnRemoveFest;
    JComboBox<String> cbArt;
    DefaultBindableReferenceCombo cbBau;
    JComboBox cbGeomSchaden;
    JComboBox<String> cbInst;
    DefaultBindableReferenceCombo cbOrdnung;
    JCheckBox chAbgelehnt;
    JCheckBox chAbgest;
    JCheckBox chBeratung;
    JCheckBox chEfeu;
    JCheckBox chEingang;
    JCheckBox chFaellung;
    JCheckBox chGefahr;
    JCheckBox chGutachten;
    JCheckBox chKLeistung;
    JCheckBox chKrone;
    JCheckBox chOhne;
    JCheckBox chStamm;
    JCheckBox chSturm;
    JCheckBox chWurzel;
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
    JLabel lblAbgelehnt;
    JLabel lblAbgest;
    JLabel lblAlter;
    JLabel lblArt;
    JLabel lblBau;
    JLabel lblBemerkung;
    JLabel lblBeratung;
    JLabel lblBetrag;
    JLabel lblEfeu;
    JLabel lblEingang;
    JLabel lblFaellung;
    JLabel lblGefahr;
    JLabel lblGeom;
    JLabel lblGutachten;
    JLabel lblHoehe;
    JLabel lblInst;
    JLabel lblKLeistung;
    JLabel lblKroneArr;
    JLabel lblLadenErsatz;
    JLabel lblLadenFest;
    JLabel lblMassnahmeArr;
    JLabel lblOhne;
    JLabel lblOrdnung;
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
    JPanel panSchaden;
    JPanel panZahlung;
    JPanel pnlCard1;
    JScrollPane scpBemerkung;
    JScrollPane scpLaufendeErsatz;
    JScrollPane scpLaufendeFest;
    JSpinner spAlter;
    JSpinner spHoehe;
    JSpinner spUmfang;
    JTextArea taBemerkung;
    JTextField txtAnmerkung;
    JTextField txtBau;
    JTextField txtBegruendung;
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
     * @param  bclInstance  DOCUMENT ME!
     */
    public BaumSchadenPanel(final BaumChildrenLoader bclInstance) {
        this.baumChildrenLoader = bclInstance;
        if (bclInstance != null) {
            this.editor = bclInstance.getParentOrganizer().isEditor();
        } else {
            this.editor = false;
        }
        initComponents();
        if (isEditor()) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeomSchaden).setLocalRenderFeatureString(FIELD__GEOM);
        }
        for (final DefaultBindableLabelsPanel labelsPanel : Arrays.asList(blpKrone, blpStamm, blpWurzel, blpMassnahme)) {
            labelsPanel.initWithConnectionContext(getConnectionContext());
        }
        if (getBaumChildrenLoader() != null) {
            loadChildrenListener = new BaumSchadenPanel.LoaderListener();
            getBaumChildrenLoader().addListener(loadChildrenListener);
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddNewErsatzActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnAddNewErsatzActionPerformed
        if (getBaumChildrenLoader().getLoadingCompletedWithoutError()) {
            if (getCidsBean() != null) {
                try {
                    // ersatzBean erzeugen und vorbelegen:
                    final CidsBean beanErsatz = CidsBean.createNewCidsBeanFromTableName(
                            "WUNDA_BLAU",
                            TABLE_ERSATZ,
                            getConnectionContext());
                    final CidsBean beanSchaden = getCidsBean();
                    beanSchaden.getMetaObject().setStatus(MetaObject.MODIFIED);
                    beanErsatz.setProperty(FIELD__DISPENS, false);
                    beanErsatz.setProperty(FIELD__SELBST, false);
                    beanErsatz.setProperty(FIELD__FK_SCHADEN, beanSchaden);

                    // Ersatzpflanzungen erweitern:
                    if (isEditor()) {
                        getBaumChildrenLoader().addErsatz(getCidsBean().getPrimaryKeyValue(), beanErsatz);
                    }
                    ((DefaultListModel)lstErsatz.getModel()).addElement(beanErsatz);

                    // Refresh:
                    lstErsatz.setSelectedValue(beanErsatz, true);
                    setChangeFlag();
                } catch (Exception e) {
                    LOG.error("Cannot add new BaumErsatz object", e);
                }
            }
        }
    } //GEN-LAST:event_btnAddNewErsatzActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveErsatzActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnRemoveErsatzActionPerformed
        if (getBaumChildrenLoader().getLoadingCompletedWithoutError()) {
            if (getCidsBean() != null) {
                final Object selectedObject = lstErsatz.getSelectedValue();

                if (selectedObject instanceof CidsBean) {
                    final List<CidsBean> listErsatz = getBaumChildrenLoader().getMapValueErsatz(getCidsBean()
                                    .getPrimaryKeyValue());
                    if (((CidsBean)selectedObject).getMetaObject().getStatus() == MetaObject.NEW) {
                        getBaumChildrenLoader().removeErsatz(cidsBean.getPrimaryKeyValue(), (CidsBean)selectedObject);
                    } else {
                        for (final CidsBean beanErsatz : listErsatz) {
                            if (beanErsatz.equals(selectedObject)) {
                                try {
                                    beanErsatz.delete();
                                } catch (Exception ex) {
                                    LOG.warn("problem in delete ersatz: not removed.", ex);
                                }
                                break;
                            }
                        }
                        getBaumChildrenLoader().getMapErsatz().replace(getCidsBean().getPrimaryKeyValue(), listErsatz);
                    }
                    ((DefaultListModel)lstErsatz.getModel()).removeElement(selectedObject);
                    if (getActiveBeans(listErsatz) > 0) {
                        lstErsatz.setSelectedIndex(0);
                    }
                    setChangeFlag();
                }
            }
        }
    } //GEN-LAST:event_btnRemoveErsatzActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddNewFestActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnAddNewFestActionPerformed
        if (getBaumChildrenLoader().getLoadingCompletedWithoutError()) {
            if (getCidsBean() != null) {
                try {
                    // festBean erzeugen und vorbelegen:
                    final CidsBean beanFest = CidsBean.createNewCidsBeanFromTableName(
                            "WUNDA_BLAU",
                            TABLE_FEST,
                            getConnectionContext());
                    beanFest.setProperty(FIELD__FK_SCHADEN, getCidsBean());

                    if (isEditor()) {
                        getBaumChildrenLoader().addFest(getCidsBean().getPrimaryKeyValue(), beanFest);
                    }
                    ((DefaultListModel)lstFest.getModel()).addElement(beanFest);

                    // Refresh:
                    lstFest.setSelectedValue(beanFest, true);
                    setChangeFlag();
                } catch (Exception e) {
                    LOG.error("Cannot add new BaumFest object", e);
                }
            }
        }
    } //GEN-LAST:event_btnAddNewFestActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveFestActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnRemoveFestActionPerformed
        if (getBaumChildrenLoader().getLoadingCompletedWithoutError()) {
            if (getCidsBean() != null) {
                final Object selectedObject = lstFest.getSelectedValue();

                if (selectedObject instanceof CidsBean) {
                    final List<CidsBean> listFest = getBaumChildrenLoader().getMapValueFest(getCidsBean()
                                    .getPrimaryKeyValue());
                    if (((CidsBean)selectedObject).getMetaObject().getStatus() == MetaObject.NEW) {
                        getBaumChildrenLoader().removeFest(getCidsBean().getPrimaryKeyValue(),
                            (CidsBean)selectedObject);
                    } else {
                        for (final CidsBean beanFest : listFest) {
                            if (beanFest.equals(selectedObject)) {
                                try {
                                    beanFest.delete();
                                } catch (Exception ex) {
                                    LOG.warn("problem in delete fest: not removed.", ex);
                                }
                                break;
                            }
                        }
                        getBaumChildrenLoader().getMapFest().replace(getCidsBean().getPrimaryKeyValue(), listFest);
                    }
                    ((DefaultListModel)lstFest.getModel()).removeElement(selectedObject);
                    if (getActiveBeans(listFest) > 0) {
                        lstFest.setSelectedIndex(0);
                    }
                    setChangeFlag();
                }
            }
        }
    } //GEN-LAST:event_btnRemoveFestActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstErsatzValueChanged(final ListSelectionEvent evt) { //GEN-FIRST:event_lstErsatzValueChanged
        final Object oErsatz = lstErsatz.getSelectedValue();
        if (oErsatz instanceof CidsBean) {
            baumErsatzPanel.setCidsBean((CidsBean)oErsatz);
        }
    }                                                                  //GEN-LAST:event_lstErsatzValueChanged

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

    /**
     * DOCUMENT ME!
     *
     * @param   saveSchadenBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isOkForSaving(final CidsBean saveSchadenBean) {
        if (getErrorNoSave() != null) {
            noSave();
            return false;
        } else {
            boolean save = true;
            final StringBuilder errorMessage = new StringBuilder();
            boolean noErrorErsatz = true;
            boolean noErrorFest = true;

            final List<CidsBean> listErsatz = getBaumChildrenLoader().getMapValueErsatz(
                    saveSchadenBean.getPrimaryKeyValue());
            final List<CidsBean> listFest = getBaumChildrenLoader().getMapValueFest(
                    saveSchadenBean.getPrimaryKeyValue());
            // Ersatzpflanzungen ueberpruefen
            if ((listErsatz != null) && !(listErsatz.isEmpty())) {
                for (final CidsBean ersatzBean : listErsatz) {
                    try {
                        noErrorErsatz = baumErsatzPanel.isOkForSaving(ersatzBean);
                        if (!noErrorErsatz) {
                            break;
                        }
                    } catch (final Exception ex) {
                        noErrorErsatz = false;
                        LOG.error("Fehler beim Speicher-Check der Ersatzpflanzungen.", ex);
                    }
                }
            }
            // Festsetzungen ueberpruefen
            if ((listFest != null) && !(listFest.isEmpty())) {
                for (final CidsBean festBean : listFest) {
                    try {
                        noErrorFest = baumFestsetzungPanel.isOkayForSaving(festBean);
                        if (!noErrorFest) {
                            break;
                        }
                    } catch (final Exception ex) {
                        noErrorFest = false;
                        LOG.error("Fehler beim Speicher-Check der Festsetzungen.", ex);
                    }
                }
            }

            // Art muss angegeben werden
            try {
                if (saveSchadenBean.getProperty(FIELD__ART) == null) {
                    LOG.warn("No name specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_NOART));
                    save = false;
                }
            } catch (final MissingResourceException ex) {
                LOG.warn("Countl not given.", ex);
                save = false;
            }

            // Geometrie vorhanden und Punkt
            try {
                if (saveSchadenBean.getProperty(FIELD__GEOREFERENZ) == null) {
                    LOG.warn("No geom specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_NOGEOM));
                    save = false;
                } else {
                    final CidsBean geom_pos = (CidsBean)saveSchadenBean.getProperty(FIELD__GEOREFERENZ);
                    if (!((Geometry)geom_pos.getProperty(FIELD__GEO_FIELD)).getGeometryType().equals(GEOMTYPE)) {
                        LOG.warn("Wrong geom specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_WRONGGEOM));
                        save = false;
                    }
                }
            } catch (final MissingResourceException ex) {
                LOG.warn("Geom not given.", ex);
                save = false;
            }

            // Institution muss angegeben werden
            try {
                if (saveSchadenBean.getProperty(FIELD__INST) == null) {
                    LOG.warn("No institution specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_NOINST));
                    save = false;
                }
            } catch (final MissingResourceException ex) {
                LOG.warn("Institution not given.", ex);
                save = false;
            }

            // Wenn keine Leistung angegeben
            try {
                if (Objects.equals(saveSchadenBean.getProperty(FIELD__KLEISTUNG), true)) {
                    // Nicht ersichtlich warum
                    if (Objects.equals(saveSchadenBean.getProperty(FIELD__GEFAHR), false)
                                && Objects.equals(saveSchadenBean.getProperty(FIELD__KRONE), false)
                                && Objects.equals(saveSchadenBean.getProperty(FIELD__STAMM), false)
                                && Objects.equals(saveSchadenBean.getProperty(FIELD__WURZEL), false)) {
                        if ((saveSchadenBean.getProperty(FIELD__BEGRUENDUNG) == null)
                                    || (saveSchadenBean.getProperty(FIELD__BEGRUENDUNG).toString()).trim().isEmpty()) {
                            LOG.warn("No text keine leistung specified. Skip persisting.");
                            errorMessage.append(NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_NOLEISTUNG));
                            save = false;
                        }
                    }
                }
            } catch (final MissingResourceException ex) {
                LOG.warn("Begruendung not given.", ex);
                save = false;
            }

            // Text zur Baugehnemigung, wenn mit
            try {
                if ((saveSchadenBean.getProperty(FIELD__BAU) != null)
                            && ((saveSchadenBean.getProperty(FIELD__BAU_SCHLUESSEL)).toString().equals("mit"))) {
                    if ((saveSchadenBean.getProperty(FIELD__BAU_TEXT) == null)
                                || (saveSchadenBean.getProperty(FIELD__BAU_TEXT).toString()).trim().isEmpty()) {
                        LOG.warn("No text bau specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_NOBAU));
                        save = false;
                    }
                }
            } catch (final MissingResourceException ex) {
                LOG.warn("Text Bau not given.", ex);
                save = false;
            }

            // Haekchen Wurzel gesetzt?
            try {
                final Collection<CidsBean> collectionWurzel = saveSchadenBean.getBeanCollectionProperty(
                        FIELD__WURZEL_ARR);
                if ((collectionWurzel != null) && !collectionWurzel.isEmpty()) {
                    if (!(Objects.equals(saveSchadenBean.getProperty(FIELD__WURZEL), true))) {
                        LOG.warn("No wurzel specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_NOWURZEL));
                        save = false;
                    }
                }
            } catch (final MissingResourceException ex) {
                LOG.warn("wurzel not given.", ex);
                save = false;
            }

            // Haekchen Stamm gesetzt?
            try {
                final Collection<CidsBean> collectionStamm = saveSchadenBean.getBeanCollectionProperty(
                        FIELD__STAMM_ARR);
                if ((collectionStamm != null) && !collectionStamm.isEmpty()) {
                    if (!(Objects.equals(saveSchadenBean.getProperty(FIELD__STAMM), true))) {
                        LOG.warn("No stamm specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_NOSTAMM));
                        save = false;
                    }
                }
            } catch (final MissingResourceException ex) {
                LOG.warn("stamm not given.", ex);
                save = false;
            }

            // Haekchen Krone gesetzt?
            try {
                final Collection<CidsBean> collectionKrone = saveSchadenBean.getBeanCollectionProperty(
                        FIELD__KRONE_ARR);
                if ((collectionKrone != null) && !collectionKrone.isEmpty()) {
                    if (!(Objects.equals(saveSchadenBean.getProperty(FIELD__KRONE), true))) {
                        LOG.warn("No krone specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_NOKRONE));
                        save = false;
                    }
                }
            } catch (final MissingResourceException ex) {
                LOG.warn("krone not given.", ex);
                save = false;
            }

            // Haekchen abgelehnt und Faellung gesetzt
            try {
                if ((Objects.equals(saveSchadenBean.getProperty(FIELD__FAELLUNG), true))) {
                    if ((Objects.equals(saveSchadenBean.getProperty(FIELD__ABGELEHNT), true))) {
                        LOG.warn("Gefaellt and abgelehnt specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_ABGELEHNT));
                        save = false;
                    }
                }
            } catch (final MissingResourceException ex) {
                LOG.warn("krone not given.", ex);
                save = false;
            }

            // Haekchen abgestorben gesetzt?
            try {
                if (Objects.equals(saveSchadenBean.getProperty(FIELD__OHNE), true)) {
                    if (Objects.equals(saveSchadenBean.getProperty(FIELD__WURZEL), true)) {
                        LOG.warn("Wrong wurzel specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_WRONGWURZEL));
                        save = false;
                    }
                    if (Objects.equals(saveSchadenBean.getProperty(FIELD__STAMM), true)) {
                        LOG.warn("Wrong stamm specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_WRONGSTAMM));
                        save = false;
                    }
                    if (Objects.equals(saveSchadenBean.getProperty(FIELD__KRONE), true)) {
                        LOG.warn("Wrong krone specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_WRONGKRONE));
                        save = false;
                    }
                    if (Objects.equals(saveSchadenBean.getProperty(FIELD__STURM), true)) {
                        LOG.warn("Wrong sturm specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_WRONGSTURM));
                        save = false;
                    }
                    if (Objects.equals(saveSchadenBean.getProperty(FIELD__ABGESTORBEN), true)) {
                        LOG.warn("Wrong abgestorben specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_WRONGABG));
                        save = false;
                    }
                }
            } catch (final MissingResourceException ex) {
                LOG.warn("abgestorben set.", ex);
                save = false;
            }

            if (errorMessage.length() > 0) {
                if (baumChildrenLoader.getParentOrganizer() instanceof BaumGebietEditor) {
                    final SimpleDateFormat formatTag = new SimpleDateFormat("dd.MM.yy");
                    errorMessage.append(NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_WHICH))
                            .append(saveSchadenBean.getPrimaryKeyValue());
                    final CidsBean meldungBean = (CidsBean)saveSchadenBean.getProperty(FIELD__FK_MELDUNG);
                    errorMessage.append(NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_MESSAGE))
                            .append(formatTag.format(meldungBean.getProperty(FIELD__MDATUM)));
                }
                JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                    NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_PANE_PREFIX)
                            + errorMessage.toString()
                            + NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_PANE_SUFFIX),
                    NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_PANE_TITLE),
                    JOptionPane.WARNING_MESSAGE);
            }
            return save & noErrorErsatz & noErrorFest;
        }
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return ((baumChildrenLoader != null) && (baumChildrenLoader.getParentOrganizer() != null))
            ? baumChildrenLoader.getParentOrganizer().getConnectionContext() : null;
    }

    /**
     * DOCUMENT ME!
     */
    private void setChangeFlag() {
        // cidsBean.setArtificialChangeFlag(true);
        if ((getBaumChildrenLoader() != null)
                    && (getBaumChildrenLoader().getParentOrganizer() != null)
                    && (getBaumChildrenLoader().getParentOrganizer().getCidsBean() != null)) {
            getBaumChildrenLoader().getParentOrganizer().getCidsBean().setArtificialChangeFlag(true);
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
    public void dispose() {
        baumLagePanel.dispose();
        bindingGroup.unbind();
        if (labelsPanels != null) {
            for (final DefaultBindableLabelsPanel panel : labelsPanels) {
                panel.dispose();
            }
        }
        labelsPanels.clear();
        if (isEditor() && (getCidsBean() != null)) {
            getCidsBean().removePropertyChangeListener(changeListener);
        }
        cidsBean = null;
        if (isEditor() && (cbGeomSchaden != null)) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeomSchaden).dispose();
            ((DefaultCismapGeometryComboBoxEditor)cbGeomSchaden).setCidsMetaObject(null);
            cbGeomSchaden = null;
        }
        baumFestsetzungPanel.dispose();
        baumErsatzPanel.dispose();
        ((DefaultListModel)lstFest.getModel()).clear();
        getBaumChildrenLoader().removeListener(loadChildrenListener);
        getBaumChildrenLoader().clearAllMaps();
    }

    @Override
    public CidsBean getCidsBean() {
        return this.cidsBean;
    }

    /**
     * DOCUMENT ME!
     */
    private void setSaveValues() {
        saveArt = (getCidsBean().getProperty(FIELD__ART) != null)
            ? ((CidsBean)getCidsBean().getProperty(FIELD__ART)).getPrimaryKeyValue() : null;
        saveGeom = (getCidsBean().getProperty(FIELD__GEOREFERENZ) != null)
            ? ((CidsBean)getCidsBean().getProperty(FIELD__GEOREFERENZ)).getPrimaryKeyValue() : null;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        if (!(Objects.equals(getCidsBean(), cidsBean))) {
            if (isEditor() && (getCidsBean() != null)) {
                getCidsBean().removePropertyChangeListener(changeListener);
            }
            try {
                labelsPanels.clear();
                blpKrone.clear();
                blpStamm.clear();
                blpWurzel.clear();
                blpMassnahme.clear();
                bindingGroup.unbind();
                this.cidsBean = cidsBean;
                if ((getCidsBean() != null) && isEditor()) {
                    setSaveValues();
                }

                if (getCidsBean() != null) {
                    zeigeKinderFest();
                    zeigeKinderErsatz();
                    // Wenn mit mehreren Geoms(Liste) gearbeitet wird
                    if (isEditor()) {
                        ((DefaultCismapGeometryComboBoxEditor)cbGeomSchaden).setCidsMetaObject(getCidsBean()
                                    .getMetaObject());
                        ((DefaultCismapGeometryComboBoxEditor)cbGeomSchaden).initForNewBinding();
                    }
                    DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                        bindingGroup,
                        getCidsBean(),
                        getConnectionContext());
                } else {
                    setErsatzBeans(null);
                    setFestBeans(null);
                    cbArt.setSelectedIndex(-1);
                    cbBau.setSelectedIndex(-1);
                    cbInst.setSelectedIndex(-1);
                    cbOrdnung.setSelectedIndex(-1);
                    txtBau.setText("");
                    txtAnmerkung.setText("");
                    txtBegruendung.setText("");
                    if (isEditor()) {
                        ((DefaultCismapGeometryComboBoxEditor)cbGeomSchaden).initForNewBinding();
                        cbGeomSchaden.setSelectedIndex(-1);
                    }
                }

                setMapWindow();
                bindingGroup.bind();
                if (isEditor() && (getCidsBean() != null)) {
                    getCidsBean().addPropertyChangeListener(changeListener);
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
                            final Component compoTeil = super.getListCellRendererComponent(
                                    list,
                                    newValue,
                                    index,
                                    isSelected,
                                    cellHasFocus);
                            compoTeil.setForeground(new Color(87, 175, 54));
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
                            final Component compoTeil = super.getListCellRendererComponent(
                                    list,
                                    newValue,
                                    index,
                                    isSelected,
                                    cellHasFocus);
                            compoTeil.setForeground(new Color(112, 48, 160));
                            return compoTeil;
                        }
                    });

                if (getCidsBean() != null) {
                    if ((getBaumChildrenLoader().getMapValueErsatz(getCidsBean().getPrimaryKeyValue()) != null)
                                && (getActiveBeans(
                                        getBaumChildrenLoader().getMapValueErsatz(getCidsBean().getPrimaryKeyValue()))
                                    > 0)) {
                        lstErsatz.setSelectedIndex(0);
                    }
                    if ((getBaumChildrenLoader().getMapValueFest(getCidsBean().getPrimaryKeyValue()) != null)
                                && (getActiveBeans(
                                        getBaumChildrenLoader().getMapValueFest(getCidsBean().getPrimaryKeyValue()))
                                    > 0)) {
                        lstFest.setSelectedIndex(0);
                    }
                    labelsPanels.addAll(Arrays.asList(blpKrone, blpStamm, blpWurzel, blpMassnahme));
                    if (getCidsBean().getMetaObject().getStatus() == MetaObject.NEW) {
                        getBaumChildrenLoader().setLoadingCompletedWithoutError(true);
                        allowAddRemove();
                    }
                }
                if (isEditor()) {
                    cbGeomSchaden.updateUI();
                }
            } catch (final Exception ex) {
                LOG.warn("problem in setCidsBean.", ex);
                if (isEditor()) {
                    setErrorNoSave(ex);
                    noSave();
                }
            }
        }
        if (isEditor()) {
            nullNoEdit(getCidsBean() != null);
        } else {
            setReadOnly();
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void noSave() {
        final ErrorInfo info = new ErrorInfo(
                NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_NOSAVE_TITLE),
                NbBundle.getMessage(BaumSchadenPanel.class, BUNDLE_NOSAVE_MESSAGE),
                null,
                null,
                getErrorNoSave(),
                Level.SEVERE,
                null);
        JXErrorPane.showDialog(BaumSchadenPanel.this, info);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  edit  DOCUMENT ME!
     */
    private void nullNoEdit(final boolean edit) {
        spAlter.setEnabled(edit);
        cbInst.setEnabled(edit);
        spHoehe.setEnabled(edit);
        spUmfang.setEnabled(edit);
        cbArt.setEnabled(edit);
        cbGeomSchaden.setEnabled(edit);
        chOhne.setEnabled(edit);
        chEfeu.setEnabled(edit);
        cbBau.setEnabled(edit);
        cbOrdnung.setEnabled(edit);
        if (isEditor() && (getCidsBean() != null)
                    && (getCidsBean().getProperty(FIELD__BAU) != null)
                    && ((getCidsBean().getProperty(FIELD__BAU_SCHLUESSEL)).toString().equals("mit"))) {
            txtBau.setEnabled(true);
        } else {
            txtBau.setEnabled(false);
        }
        if (isEditor() && (getCidsBean() != null)
                    && (getCidsBean().getProperty(FIELD__FK_ORDNUNG) != null)) {
            txtAnmerkung.setEnabled(true);
            chFaellung.setEnabled(false);
        } else {
            txtAnmerkung.setEnabled(false);
            chFaellung.setEnabled(true);
        }
        if (isEditor() && (getCidsBean() != null)
                    && (Objects.equals(getCidsBean().getProperty(FIELD__KLEISTUNG), true))) {
            txtBegruendung.setEnabled(true);
        } else {
            txtBegruendung.setEnabled(false);
        }
        chAbgest.setEnabled(edit);
        chAbgelehnt.setEnabled(edit);
        chSturm.setEnabled(edit);
        chBeratung.setEnabled(edit);
        chGutachten.setEnabled(edit);
        chKrone.setEnabled(edit);
        chStamm.setEnabled(edit);
        chWurzel.setEnabled(edit);
        chGefahr.setEnabled(edit);
        chKLeistung.setEnabled(edit);
        blpKrone.setEnabled(edit);
        blpStamm.setEnabled(edit);
        blpWurzel.setEnabled(edit);
        blpMassnahme.setEnabled(edit);
        taBemerkung.setEnabled(edit);
        txtBetrag.setEnabled(edit);
        chEingang.setEnabled(edit);
    }

    /**
     * DOCUMENT ME!
     */
    public void allowAddRemove() {
        if (getBaumChildrenLoader().getLoadingCompletedWithoutError()) {
            if (isEditor()) {
                btnAddNewErsatz.setEnabled(true);
                btnAddNewFest.setEnabled(true);
                btnRemoveErsatz.setEnabled(true);
                btnRemoveFest.setEnabled(true);
            }
            lblLadenErsatz.setVisible(false);
            lblLadenFest.setVisible(false);
        }
    }
    /**
     * DOCUMENT ME!
     */
    private void zeigeKinderFest() {
        setFestBeans(getBaumChildrenLoader().getMapValueFest(getCidsBean().getPrimaryKeyValue()));
    }

    /**
     * DOCUMENT ME!
     */
    private void zeigeKinderErsatz() {
        setErsatzBeans(getBaumChildrenLoader().getMapValueErsatz(getCidsBean().getPrimaryKeyValue()));
    }

    /**
     * DOCUMENT ME!
     */
    private void zeigeErrorFest() {
        scpLaufendeFest.setViewportView(new JList<>(MODEL_ERROR));
        JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
            NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_ERROR_FEST),
            NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_PANE_TITLE_ERROR_FEST),
            JOptionPane.WARNING_MESSAGE);
    }

    /**
     * DOCUMENT ME!
     */
    private void zeigeErrorErsatz() {
        scpLaufendeErsatz.setViewportView(new JList<>(MODEL_ERROR));
        JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
            NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_ERROR_ERSATZ),
            NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_PANE_TITLE_ERROR_ERSATZ),
            JOptionPane.WARNING_MESSAGE);
    }
    /**
     * DOCUMENT ME!
     */
    private void setReadOnly() {
        RendererTools.makeDoubleSpinnerWithoutButtons(spAlter, 0);
        RendererTools.makeReadOnly(spAlter);
        cbInst.setEnabled(false);
        RendererTools.makeDoubleSpinnerWithoutButtons(spHoehe, 1);
        RendererTools.makeReadOnly(spHoehe);
        spHoehe.setEnabled(false);
        RendererTools.makeDoubleSpinnerWithoutButtons(spUmfang, 0);
        RendererTools.makeReadOnly(spUmfang);
        cbArt.setEnabled(false);
        RendererTools.makeReadOnly(chOhne);
        RendererTools.makeReadOnly(chEfeu);
        RendererTools.makeReadOnly(txtBau);
        RendererTools.makeReadOnly(txtBegruendung);
        RendererTools.makeReadOnly(txtAnmerkung);
        cbBau.setEnabled(false);
        cbOrdnung.setEnabled(false);
        RendererTools.makeReadOnly(chAbgest);
        RendererTools.makeReadOnly(chSturm);
        RendererTools.makeReadOnly(chBeratung);
        RendererTools.makeReadOnly(chGutachten);
        RendererTools.makeReadOnly(chKrone);
        RendererTools.makeReadOnly(chStamm);
        RendererTools.makeReadOnly(chWurzel);
        RendererTools.makeReadOnly(chGefahr);
        RendererTools.makeReadOnly(chKLeistung);
        taBemerkung.setEnabled(false);
        RendererTools.makeReadOnly(chFaellung);
        RendererTools.makeReadOnly(chAbgelehnt);
        RendererTools.makeReadOnly(txtBetrag);
        RendererTools.makeReadOnly(chEingang);
        panControlsNewErsatz.setVisible(isEditor());
        panControlsNewFest.setVisible(isEditor());
        blpKrone.setEnabled(false);
        blpStamm.setEnabled(false);
        blpWurzel.setEnabled(false);
        blpMassnahme.setEnabled(false);
    }

    /**
     * DOCUMENT ME!
     */
    private void prepareErsatz() {
        if (getCidsBean() != null) {
            if ((getBaumChildrenLoader().getMapValueErsatz(getCidsBean().getPrimaryKeyValue()) != null)
                        && (getActiveBeans(
                                getBaumChildrenLoader().getMapValueErsatz(getCidsBean().getPrimaryKeyValue())) > 0)) {
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
                    final Component compoId = super.getListCellRendererComponent(
                            list,
                            newValue,
                            index,
                            isSelected,
                            cellHasFocus);
                    compoId.setForeground(new Color(87, 175, 54));
                    return compoId;
                }
            });
    }
    /**
     * DOCUMENT ME!
     */
    private void prepareFest() {
        if (getCidsBean() != null) {
            if ((getBaumChildrenLoader().getMapValueFest(getCidsBean().getPrimaryKeyValue()) != null)
                        && (getActiveBeans(getBaumChildrenLoader().getMapValueFest(getCidsBean().getPrimaryKeyValue()))
                            > 0)) {
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
                    final Component compoId = super.getListCellRendererComponent(
                            list,
                            newValue,
                            index,
                            isSelected,
                            cellHasFocus);
                    compoId.setForeground(new Color(112, 48, 160));
                    return compoId;
                }
            });
    }
    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeans  DOCUMENT ME!
     */
    private void setErsatzBeans(final List<CidsBean> cidsBeans) {
        try {
            baumErsatzPanel.setCidsBean(null);
            ((DefaultListModel)lstErsatz.getModel()).clear();
            if (cidsBeans != null) {
                for (final Object bean : cidsBeans) {
                    if ((bean instanceof CidsBean)
                                && (((CidsBean)bean).getMetaObject().getStatus() != MetaObject.TO_DELETE)) {
                        ((DefaultListModel)lstErsatz.getModel()).addElement(bean);
                    }
                }
            }
            prepareErsatz();
        } catch (final Exception ex) {
            LOG.warn("ersatz list not cleared.", ex);
        }
    }
    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeans  DOCUMENT ME!
     */
    private void setFestBeans(final List<CidsBean> cidsBeans) {
        try {
            baumFestsetzungPanel.setCidsBean(null);
            ((DefaultListModel)lstFest.getModel()).clear();
            if (cidsBeans != null) {
                for (final Object bean : cidsBeans) {
                    if ((bean instanceof CidsBean)
                                && (((CidsBean)bean).getMetaObject().getStatus() != MetaObject.TO_DELETE)) {
                        ((DefaultListModel)lstFest.getModel()).addElement(bean);
                    }
                }
            }
            prepareFest();
        } catch (final Exception ex) {
            LOG.warn("fest list not cleared.", ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void setMapWindow() {
        String mapUrl = null;
        try {
            mapUrl = BaumConfProperties.getInstance().getUrlSchaden();
        } catch (final Exception ex) {
            LOG.warn("Get no conf properties.", ex);
        }
        baumLagePanel.setMapWindow(getCidsBean(),
            getConnectionContext(),
            mapUrl);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
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

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class LoaderListener implements BaumChildrenLoader.Listener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void loadingCompleteSchaden(final Integer idMeldung) {
        }

        @Override
        public void loadingCompleteOrt(final Integer idMeldung) {
        }

        @Override
        public void loadingErrorOrt(final Integer idMeldung) {
        }

        @Override
        public void loadingErrorSchaden(final Integer idMeldung) {
        }

        @Override
        public void loadingComplete() {
            allowAddRemove();
        }

        @Override
        public void loadingCompleteFest(final Integer idSchaden) {
            if (getCidsBean() != null) {
                if (Objects.equals(getCidsBean().getPrimaryKeyValue(), idSchaden)) {
                    lblLadenFest.setVisible(false);
                    zeigeKinderFest();
                }
            }
        }

        @Override
        public void loadingCompleteErsatz(final Integer idSchaden) {
            if (getCidsBean() != null) {
                if (Objects.equals(getCidsBean().getPrimaryKeyValue(), idSchaden)) {
                    lblLadenErsatz.setVisible(false);
                    zeigeKinderErsatz();
                }
            }
        }

        @Override
        public void loadingErrorFest(final Integer idMeldung) {
            zeigeErrorFest();
        }

        @Override
        public void loadingErrorErsatz(final Integer idMeldung) {
            zeigeErrorErsatz();
        }

        @Override
        public void loadingCompleteMeldung() {
        }
    }
}
