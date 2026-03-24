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

import Sirius.navigator.ui.RequestsFullSizeComponent;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import com.vividsolutions.jts.geom.Point;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.swingx.JXTable;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.List;
import java.util.MissingResourceException;
import java.util.Objects;

import javax.swing.*;
import javax.swing.plaf.basic.ComboPopup;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.utils.EaConfProperties;
import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objecteditors.utils.TableUtils;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel;
import de.cismet.cids.custom.objectrenderer.utils.DivBeanTable;
import de.cismet.cids.custom.wunda_blau.search.server.AdresseLightweightSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultBindableComboboxCellEditor;
import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.FastBindableReferenceCombo;
import de.cismet.cids.editors.SaveVetoable;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;
import de.cismet.cismap.cids.geometryeditor.DefaultCismapSimpleGeomComboBoxEditor;

import de.cismet.cismap.commons.BoundingBox;
import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.RasterfariDocumentLoaderPanel;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.SemiRoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;
/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class EaStandortEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    SaveVetoable,
    RequestsFullSizeComponent,
    PropertyChangeListener,
    RasterfariDocumentLoaderPanel.Listener {

    //~ Static fields/initializers ---------------------------------------------

    private static DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    public static final int COLUMN_WIDTH = 180;
    public static final int COLUMN_WIDTH_MIN = 40;

    private static DefaultBindableReferenceCombo.Option NULLABLE_OPTION =
        new DefaultBindableReferenceCombo.NullableOption(null, "-");
    private static DefaultBindableReferenceCombo.Option SORTING_OPTION =
        new DefaultBindableReferenceCombo.SortingColumnOption("name");
    private static DefaultBindableReferenceCombo.Option MANAGEABLE_OPTION = null;

    private static String MAPURL;
    private static Double BUFFER;

    public static final String ADRESSE_TOSTRING_TEMPLATE = "%s";
    public static final String[] ADRESSE_TOSTRING_FIELDS = { AdresseLightweightSearch.Subject.HNR.toString() };

    private static final Logger LOG = Logger.getLogger(EaStandortEditor.class);

    public static final String FIELD__ID = "id";
    public static final String FIELD__STRASSE_SCHLUESSEL = "fk_strasse.strassenschluessel";
    public static final String FIELD__STRASSE_NAME = "name";                // strasse
    public static final String FIELD__STRASSE_KEY = "strassenschluessel";   // strasse
    public static final String FIELD__GEOM = "geom";
    public static final String FIELD__ONLINE = "online";
    public static final String FIELD__SAEULE = "anzahl_saeulen";
    public static final String FIELD__NAME = "name";
    public static final String FIELD__BETREIBER = "fk_betreiber";
    public static final String FIELD__STANDORT = "standortinformation";
    public static final String FIELD__STATUS = "fk_status";
    public static final String FIELD__SCHNELL = "schnellladung";
    public static final String FIELD__PLAETZE = "anzahl_ladeplaetze";
    public static final String FIELD__LETZTE = "letzte_aktualisierung";
    public static final String FIELD__HNR = "fk_adresse";
    public static final String FIELD__HNR_GEOM = "umschreibendes_rechteck"; // adresse
    public static final String TABLE_NAME = "ea_standort";
    public static final String FIELD__STECKER = "n_stecker";                // ea_standort
    public static final String FIELD__STECKER_STANDORT = "fk_standort";     // ea_stecker
    public static final String FIELD__STECKER_LEISTUNG = "leistung";        // ea_stecker
    public static final String FIELD__STECKER_TYP = "fk_typ";               // ea_stecker
    public static final String FIELD__STECKER_ANZAHL = "anzahl";            // ea_stecker
    public static final String TABLE_NAME_STECKER = "ea_stecker";
    public static final String TABLE_NAME_TYP = "ea_typ";

    public static final String BUNDLE_NOGEOM = "EaStandortEditor.isOkForSaving().noGeom";
    public static final String BUNDLE_NONAME = "EaStandortEditor.isOkForSaving().noName";
    public static final String BUNDLE_NOBETREIBER = "EaStandortEditor.isOkForSaving().noBetreiber";
    public static final String BUNDLE_NOSTANDORT = "EaStandortEditor.isOkForSaving().noStandort";
    public static final String BUNDLE_NOSTATUS = "EaStandortEditor.isOkForSaving().noStatus";
    public static final String BUNDLE_NOSAEULE = "EaStandortEditor.isOkForSaving().noSaeule";
    public static final String BUNDLE_TWICESTECKER = "EaStandortEditor.isOkForSaving().doppelteStecker";
    public static final String BUNDLE_NOSTECKER = "EaStandortEditor.isOkForSaving().noStecker";
    public static final String BUNDLE_NOLEISTUNG = "EaStandortEditor.isOkForSaving().noLeistung";
    public static final String BUNDLE_NOSTECKERTYP = "EaStandortEditor.isOkForSaving().noSteckerTyp";
    public static final String BUNDLE_NOANZAHL = "EaStandortEditor.isOkForSaving().noAnzahl";
    public static final String BUNDLE_WRONGANZAHL = "EaStandortEditor.isOkForSaving().wrongAnzahl";
    public static final String BUNDLE_DIFANZAHL = "EaStandortEditor.isOkForSaving().differentAnzahl";
    public static final String BUNDLE_PANE_PREFIX = "EaStandortEditor.isOkForSaving().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX = "EaStandortEditor.isOkForSaving().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "EaStandortEditor.isOkForSaving().JOptionPane.title";

    private static final String TITLE_NEW_STANDORT = "einen neuen Ladestandort anlegen...";

    private static final String[] STECKER_COL_NAMES = new String[] { "Anz", "Typ", "kW" };
    private static final String[] STECKER_PROP_NAMES = new String[] {
            "anzahl",
            "fk_typ",
            "leistung"
        };
    private static final Class[] STECKER_PROP_TYPES = new Class[] {
            Integer.class,
            CidsBean.class,
            Double.class
        };

    //~ Instance fields --------------------------------------------------------

    private MetaClass steckertypMetaClass;

    private final boolean editor;

    private final AdresseLightweightSearch hnrSearch = new AdresseLightweightSearch(
            AdresseLightweightSearch.Subject.HNR,
            ADRESSE_TOSTRING_TEMPLATE,
            ADRESSE_TOSTRING_FIELDS);
    private CidsBean beanHNr;
    private final ActionListener hnrActionListener = new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                final JList pop = ((ComboPopup)cbHNr.getUI().getAccessibleChild(cbHNr, 0)).getList();
                final JTextField txt = (JTextField)cbHNr.getEditor().getEditorComponent();
                final Object selectedValue = pop.getSelectedValue();
                txt.setText((selectedValue != null) ? String.valueOf(selectedValue) : "");
            }
        };

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton btnAddStecker;
    private JButton btnRemStecker;
    private DefaultBindableReferenceCombo cbBetreiber;
    private JComboBox cbGeom;
    private FastBindableReferenceCombo cbHNr;
    private DefaultBindableReferenceCombo cbStatus;
    FastBindableReferenceCombo cbStrasse;
    private JCheckBox chOnline;
    private JCheckBox chSchnell;
    private Box.Filler filler2;
    private Box.Filler filler3;
    private Box.Filler filler4;
    private JScrollPane jScrollPaneStecker;
    private JLabel lblBemerkung;
    private JLabel lblBetreiber;
    private JLabel lblGeom;
    private JLabel lblHNrRenderer;
    private JLabel lblHnr;
    private JLabel lblIntern;
    private JLabel lblKarte;
    private JLabel lblLetzte;
    private JLabel lblName;
    private JLabel lblOnline;
    private JLabel lblPunkte;
    private JLabel lblSaeulen;
    private JLabel lblSchnell;
    private JLabel lblStandort;
    private JLabel lblStatus;
    private JLabel lblStrasse;
    private JPanel panBemerkung;
    private JPanel panContent;
    private JPanel panDaten;
    private JPanel panGeometrie;
    private JPanel panLaden;
    private DefaultPreviewMapPanel panPreviewMap;
    private JPanel panStandort;
    private JPanel panSteck;
    private JPanel panStecker;
    private JPanel panSteckerAdd;
    private RoundedPanel rpKarte;
    private JScrollPane scpBemerkung;
    private JScrollPane scpStandort;
    private SemiRoundedPanel semiRoundedPanel7;
    JSpinner spPunkte;
    JSpinner spSaeule;
    private JTextArea taBemerkung;
    private JTextArea taStandort;
    private JTextField txtIntern;
    private JTextField txtLetzte;
    private JTextField txtName;
    private JXTable xtStecker;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public EaStandortEditor() {
        this(true);
    }

    /**
     * Creates a new EaStandortEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public EaStandortEditor(final boolean boolEditor) {
        this.editor = boolEditor;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void showMeasureIsLoading() {
    }

    @Override
    public void showMeasurePanel() {
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        super.initWithConnectionContext(connectionContext);
        initProperties();
        initComponents();
        steckertypMetaClass = ClassCacheMultiple.getMetaClass(
                CidsBeanSupport.DOMAIN_NAME,
                TABLE_NAME_TYP,
                connectionContext);
        cbStatus.setNullable(false);
        setReadOnly();
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

        panContent = new RoundedPanel();
        panLaden = new JPanel();
        panGeometrie = new JPanel();
        rpKarte = new RoundedPanel();
        panPreviewMap = new DefaultPreviewMapPanel();
        semiRoundedPanel7 = new SemiRoundedPanel();
        lblKarte = new JLabel();
        panStecker = new JPanel();
        panSteck = new JPanel();
        jScrollPaneStecker = new JScrollPane();
        xtStecker = new JXTable();
        panSteckerAdd = new JPanel();
        btnAddStecker = new JButton();
        btnRemStecker = new JButton();
        filler2 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
        panDaten = new JPanel();
        lblName = new JLabel();
        txtName = new JTextField();
        lblLetzte = new JLabel();
        txtLetzte = new JTextField();
        lblStrasse = new JLabel();
        cbStrasse = new FastBindableReferenceCombo();
        filler3 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        lblHnr = new JLabel();
        if (isEditor()) {
            cbHNr = new FastBindableReferenceCombo(
                    hnrSearch,
                    hnrSearch.getRepresentationPattern(),
                    hnrSearch.getRepresentationFields());
        }
        if (!isEditor()) {
            lblHNrRenderer = new JLabel();
        }
        if (isEditor()) {
            lblGeom = new JLabel();
        }
        if (isEditor()) {
            cbGeom = new DefaultCismapSimpleGeomComboBoxEditor();
            ((DefaultCismapSimpleGeomComboBoxEditor)cbGeom).setAllowedGeometryTypes(new Class[] { Point.class });
        }
        lblBetreiber = new JLabel();
        cbBetreiber = new DefaultBindableReferenceCombo(true);
        lblSchnell = new JLabel();
        chSchnell = new JCheckBox();
        lblStandort = new JLabel();
        panStandort = new JPanel();
        scpStandort = new JScrollPane();
        taStandort = new JTextArea();
        lblBemerkung = new JLabel();
        panBemerkung = new JPanel();
        scpBemerkung = new JScrollPane();
        taBemerkung = new JTextArea();
        lblStatus = new JLabel();
        cbStatus = new DefaultBindableReferenceCombo(NULLABLE_OPTION, MANAGEABLE_OPTION, SORTING_OPTION);
        lblOnline = new JLabel();
        chOnline = new JCheckBox();
        lblPunkte = new JLabel();
        spPunkte = new JSpinner();
        filler4 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        lblSaeulen = new JLabel();
        spSaeule = new JSpinner();
        lblIntern = new JLabel();
        txtIntern = new JTextField();

        setLayout(new GridBagLayout());

        panContent.setName(""); // NOI18N
        panContent.setOpaque(false);
        panContent.setLayout(new GridBagLayout());

        panLaden.setOpaque(false);
        panLaden.setLayout(new GridBagLayout());

        panGeometrie.setOpaque(false);
        panGeometrie.setLayout(new GridBagLayout());

        rpKarte.setName(""); // NOI18N
        rpKarte.setLayout(new GridBagLayout());
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        rpKarte.add(panPreviewMap, gridBagConstraints);

        semiRoundedPanel7.setBackground(Color.darkGray);
        semiRoundedPanel7.setLayout(new GridBagLayout());

        lblKarte.setForeground(new Color(255, 255, 255));
        lblKarte.setText("Lage");
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
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 4, 2);
        panGeometrie.add(rpKarte, gridBagConstraints);

        panStecker.setLayout(new GridBagLayout());

        panSteck.setMinimumSize(new Dimension(26, 80));
        panSteck.setLayout(new GridBagLayout());

        xtStecker.setVisibleRowCount(4);
        jScrollPaneStecker.setViewportView(xtStecker);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSteck.add(jScrollPaneStecker, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panStecker.add(panSteck, gridBagConstraints);

        panSteckerAdd.setAlignmentX(0.0F);
        panSteckerAdd.setAlignmentY(1.0F);
        panSteckerAdd.setFocusable(false);
        panSteckerAdd.setLayout(new GridBagLayout());

        btnAddStecker.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddStecker.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent evt) {
                    btnAddSteckerActionPerformed(evt);
                }
            });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panSteckerAdd.add(btnAddStecker, gridBagConstraints);

        btnRemStecker.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemStecker.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent evt) {
                    btnRemSteckerActionPerformed(evt);
                }
            });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panSteckerAdd.add(btnRemStecker, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panSteckerAdd.add(filler2, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(10, 2, 2, 2);
        panStecker.add(panSteckerAdd, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        panGeometrie.add(panStecker, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 10, 10, 10);
        panLaden.add(panGeometrie, gridBagConstraints);

        panDaten.setOpaque(false);
        panDaten.setLayout(new GridBagLayout());

        lblName.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblName.setText("Name:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblName, gridBagConstraints);

        Binding binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.name}"),
                txtName,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtName, gridBagConstraints);

        lblLetzte.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblLetzte.setText("letzte Änderung:");
        lblLetzte.setToolTipText("");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblLetzte, gridBagConstraints);

        txtLetzte.setMinimumSize(new Dimension(10, 24));
        txtLetzte.setPreferredSize(new Dimension(10, 24));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtLetzte, gridBagConstraints);

        lblStrasse.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblStrasse.setText("Straße:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblStrasse, gridBagConstraints);

        cbStrasse.setMaximumRowCount(20);
        cbStrasse.setModel(new LoadModelCb());

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.fk_strasse}"),
                cbStrasse,
                BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        cbStrasse.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent evt) {
                    cbStrasseActionPerformed(evt);
                }
            });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(cbStrasse, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panDaten.add(filler3, gridBagConstraints);

        lblHnr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblHnr.setText("Hausnummer:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblHnr, gridBagConstraints);

        if (isEditor()) {
            cbHNr.setMaximumRowCount(20);
            cbHNr.setEnabled(false);
            cbHNr.setMinimumSize(new Dimension(100, 19));
            cbHNr.setPreferredSize(new Dimension(100, 19));

            binding = Bindings.createAutoBinding(
                    AutoBinding.UpdateStrategy.READ_WRITE,
                    this,
                    ELProperty.create("${cidsBean.fk_adresse}"),
                    cbHNr,
                    BeanProperty.create("selectedItem"));
            bindingGroup.addBinding(binding);
        }
        if (isEditor()) {
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 7;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            panDaten.add(cbHNr, gridBagConstraints);
        }

        if (!isEditor()) {
            lblHNrRenderer.setFont(new Font("Dialog", 0, 12)); // NOI18N

            binding = Bindings.createAutoBinding(
                    AutoBinding.UpdateStrategy.READ_WRITE,
                    this,
                    ELProperty.create("${cidsBean.fk_adresse.hausnummer}"),
                    lblHNrRenderer,
                    BeanProperty.create("text"));
            bindingGroup.addBinding(binding);
        }
        if (!isEditor()) {
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 7;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(2, 5, 2, 5);
            panDaten.add(lblHNrRenderer, gridBagConstraints);
        }

        if (isEditor()) {
            lblGeom.setFont(new Font("Tahoma", 1, 11)); // NOI18N
            lblGeom.setText("Geometrie:");
        }
        if (isEditor()) {
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.ipady = 10;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(2, 0, 2, 5);
            panDaten.add(lblGeom, gridBagConstraints);
        }

        if (isEditor()) {
            cbGeom.setFont(new Font("Dialog", 0, 12)); // NOI18N

            binding = Bindings.createAutoBinding(
                    AutoBinding.UpdateStrategy.READ_WRITE,
                    this,
                    ELProperty.create("${cidsBean.geom}"),
                    cbGeom,
                    BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapSimpleGeomComboBoxEditor)cbGeom).getConverter());
            bindingGroup.addBinding(binding);
        }
        if (isEditor()) {
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.gridwidth = 4;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            panDaten.add(cbGeom, gridBagConstraints);
        }

        lblBetreiber.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBetreiber.setText("Betreiber:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblBetreiber, gridBagConstraints);

        cbBetreiber.setNullable(false);
        cbBetreiber.setFont(new Font("Dialog", 0, 12)); // NOI18N
        cbBetreiber.setMaximumRowCount(6);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.fk_betreiber}"),
                cbBetreiber,
                BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(cbBetreiber, gridBagConstraints);

        lblSchnell.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblSchnell.setText("Schnellladen:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblSchnell, gridBagConstraints);

        chSchnell.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.schnellladung}"),
                chSchnell,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(chSchnell, gridBagConstraints);

        lblStandort.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblStandort.setText("Standortinformation:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblStandort, gridBagConstraints);

        panStandort.setOpaque(false);
        panStandort.setLayout(new GridBagLayout());

        taStandort.setColumns(20);
        taStandort.setLineWrap(true);
        taStandort.setRows(2);
        taStandort.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.standortinformation}"),
                taStandort,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpStandort.setViewportView(taStandort);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panStandort.add(scpStandort, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panStandort, gridBagConstraints);

        lblBemerkung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBemerkung.setText("Bemerkung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblBemerkung, gridBagConstraints);

        panBemerkung.setOpaque(false);
        panBemerkung.setLayout(new GridBagLayout());

        taBemerkung.setColumns(20);
        taBemerkung.setLineWrap(true);
        taBemerkung.setRows(2);
        taBemerkung.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.bemerkung}"),
                taBemerkung,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpBemerkung.setViewportView(taBemerkung);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panBemerkung.add(scpBemerkung, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panBemerkung, gridBagConstraints);

        lblStatus.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblStatus.setText("Status:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblStatus, gridBagConstraints);

        cbStatus.setFont(new Font("Dialog", 0, 12)); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.fk_status}"),
                cbStatus,
                BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(cbStatus, gridBagConstraints);

        lblOnline.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblOnline.setText("online:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblOnline, gridBagConstraints);

        chOnline.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.online}"),
                chOnline,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(chOnline, gridBagConstraints);

        lblPunkte.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblPunkte.setText("Anzahl Ladepunkte:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblPunkte, gridBagConstraints);

        spPunkte.setFont(new Font("Dialog", 0, 12)); // NOI18N
        spPunkte.setModel(new SpinnerNumberModel(0, 0, 100, 1));
        spPunkte.setPreferredSize(new Dimension(75, 20));

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.anzahl_ladeplaetze}"),
                spPunkte,
                BeanProperty.create("value"));
        binding.setSourceNullValue(0);
        binding.setSourceUnreadableValue(0);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(spPunkte, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panDaten.add(filler4, gridBagConstraints);

        lblSaeulen.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblSaeulen.setText("Anzahl Ladesäulen:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblSaeulen, gridBagConstraints);

        spSaeule.setFont(new Font("Dialog", 0, 12)); // NOI18N
        spSaeule.setModel(new SpinnerNumberModel(0, 0, 100, 1));
        spSaeule.setPreferredSize(new Dimension(75, 20));

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.anzahl_saeulen}"),
                spSaeule,
                BeanProperty.create("value"));
        binding.setSourceNullValue(0);
        binding.setSourceUnreadableValue(0);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(spSaeule, gridBagConstraints);

        lblIntern.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblIntern.setText("interne ID:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblIntern, gridBagConstraints);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.id_intern}"),
                txtIntern,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtIntern, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 5, 10);
        panLaden.add(panDaten, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panContent.add(panLaden, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        add(panContent, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbStrasseActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbStrasseActionPerformed
        if (isEditor() && (getCidsBean() != null) && (getCidsBean().getProperty(FIELD__STRASSE_SCHLUESSEL) != null)) {
            cbHNr.setSelectedItem(null);
            cbHNr.setEnabled(true);
            refreshHnr();
        }
    }                                                                             //GEN-LAST:event_cbStrasseActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddSteckerActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnAddSteckerActionPerformed
        TableUtils.addObjectToTable(xtStecker, TABLE_NAME_STECKER, getConnectionContext());
    }                                                                  //GEN-LAST:event_btnAddSteckerActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemSteckerActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnRemSteckerActionPerformed
        TableUtils.removeObjectsFromTable(xtStecker);
    }                                                                  //GEN-LAST:event_btnRemSteckerActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isEditor() {
        return this.editor;
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cb) {
        try {
            if (isEditor() && (getCidsBean() != null)) {
                LOG.info("remove propchange ea_standort: " + getCidsBean());
                getCidsBean().removePropertyChangeListener(this);
                cbHNr.removeActionListener(hnrActionListener);
            }
            bindingGroup.unbind();
            this.cidsBean = cb;
            if (isEditor() && (getCidsBean() != null)) {
                LOG.info("add propchange ea_standort: " + getCidsBean());
                getCidsBean().addPropertyChangeListener(this);
            }
            // 8.5.17 s.Simmert: Methodenaufruf, weil sonst die Comboboxen nicht gefüllt werden
            // evtl. kann dies verbessert werden.
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cb,
                getConnectionContext());

            setMapWindow();
            bindingGroup.bind();
            setTitle(getTitle());

            if (isEditor()) {
                if ((getCidsBean() != null) && (getCidsBean().getProperty(FIELD__STRASSE_SCHLUESSEL) != null)) {
                    cbHNr.setEnabled(true);
                }
                StaticSwingTools.decorateWithFixedAutoCompleteDecorator(cbHNr);
                {
                    final JList pop = ((ComboPopup)cbHNr.getUI().getAccessibleChild(cbHNr, 0)).getList();
                    final JTextField txt = (JTextField)cbHNr.getEditor().getEditorComponent();
                    cbHNr.addActionListener(hnrActionListener);
                }
                refreshHnr();
            }
            beanHNr = ((CidsBean)getCidsBean().getProperty(FIELD__HNR));

            final DivBeanTable steckerModel = new DivBeanTable(
                    isEditor(),
                    cidsBean,
                    FIELD__STECKER,
                    STECKER_COL_NAMES,
                    STECKER_PROP_NAMES,
                    STECKER_PROP_TYPES);
            xtStecker.setModel(steckerModel);
            xtStecker.getColumn(1).setCellEditor(new DefaultBindableComboboxCellEditor(steckertypMetaClass));
            xtStecker.getColumn(0).setPreferredWidth(COLUMN_WIDTH_MIN);
            xtStecker.getColumn(1).setPreferredWidth(COLUMN_WIDTH);
            xtStecker.getColumn(2).setPreferredWidth(COLUMN_WIDTH_MIN);

            if (getCidsBean().getMetaObject().getStatus() == MetaObject.NEW) {
                try {
                    getCidsBean().setProperty(
                        FIELD__ONLINE,
                        false);
                } catch (Exception e) {
                    LOG.error("Cannot set online", e);
                }
                try {
                    getCidsBean().setProperty(
                        FIELD__SCHNELL,
                        false);
                } catch (Exception e) {
                    LOG.error("Cannot set schnell", e);
                }
            } else {
                txtLetzte.setText(DATE_FORMAT.format(cidsBean.getProperty(FIELD__LETZTE)));
            }
        } catch (Exception ex) {
            LOG.error("Bean not set", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   listSteckerBeans  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private int checkSteckerWerte(final List<CidsBean> listSteckerBeans) {
        try {
            if ((listSteckerBeans != null) && (listSteckerBeans.size() > 0)) {
                for (int i = 0; i < listSteckerBeans.size(); i++) {
                    // .......Überprüfen ob alle Einträge gefüllt.......
                    if ((null == listSteckerBeans.get(i).getProperty(FIELD__STECKER_LEISTUNG))
                                || "".equals(listSteckerBeans.get(i).getProperty(FIELD__STECKER_LEISTUNG).toString())) {
                        return 4;
                    }
                    if ((null == listSteckerBeans.get(i).getProperty(FIELD__STECKER_TYP))
                                || "".equals(listSteckerBeans.get(i).getProperty(FIELD__STECKER_TYP).toString())) {
                        return 5;
                    }
                    if ((null == listSteckerBeans.get(i).getProperty(FIELD__STECKER_ANZAHL))
                                || "".equals(listSteckerBeans.get(i).getProperty(FIELD__STECKER_ANZAHL).toString())) {
                        return 6;
                    }
                    if (!(((int)listSteckerBeans.get(i).getProperty(FIELD__STECKER_ANZAHL) > 0)
                                    && ((int)listSteckerBeans.get(i).getProperty(FIELD__STECKER_ANZAHL) <= 100))) {
                        return 3;
                    }
                    // Redundante Einträge
                    if (listSteckerBeans.size() > (i + 1)) {
                        for (int j = i + 1; j < listSteckerBeans.size(); j++) {
                            if (
                                listSteckerBeans.get(i).getProperty(FIELD__STECKER_LEISTUNG).equals(
                                            listSteckerBeans.get(j).getProperty(FIELD__STECKER_LEISTUNG))
                                        && listSteckerBeans.get(i).getProperty(FIELD__STECKER_TYP).equals(
                                            listSteckerBeans.get(j).getProperty(FIELD__STECKER_TYP))) {
                                return 1;
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        return 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   listSteckerBeans  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean checkSteckerAnzahl(final List<CidsBean> listSteckerBeans) {
        try {
            int anzahl = 0;
            if ((listSteckerBeans != null) && (listSteckerBeans.size() > 0)) {
                for (int i = 0; i < listSteckerBeans.size(); i++) {
                    if (listSteckerBeans.get(i).getProperty(FIELD__STECKER_ANZAHL) != null) {
                        anzahl += (int)listSteckerBeans.get(i).getProperty(FIELD__STECKER_ANZAHL);
                    }
                }
                if ((getCidsBean().getProperty(FIELD__PLAETZE) != null)
                            && ((int)getCidsBean().getProperty(FIELD__PLAETZE) == anzahl)) {
                    return true;
                }
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     */
    private void setReadOnly() {
        if (!(isEditor())) {
            RendererTools.makeReadOnly(taStandort);
            RendererTools.makeReadOnly(cbStrasse);
            // lblHNrRenderer.setVisible(true);
            // RendererTools.makeReadOnly(cbHNr);
            RendererTools.makeReadOnly(cbBetreiber);
            RendererTools.makeReadOnly(cbStatus);
            RendererTools.makeDoubleSpinnerWithoutButtons(spPunkte, 0);
            RendererTools.makeReadOnly(spPunkte);
            RendererTools.makeDoubleSpinnerWithoutButtons(spSaeule, 0);
            RendererTools.makeReadOnly(spSaeule);
            spPunkte.setEnabled(false);
            spSaeule.setEnabled(false);
            RendererTools.makeReadOnly(taBemerkung);
            RendererTools.makeReadOnly(xtStecker);
            panSteckerAdd.setVisible(isEditor());
            RendererTools.makeReadOnly(txtName);
            RendererTools.makeReadOnly(chOnline);
            RendererTools.makeReadOnly(chSchnell);
            RendererTools.makeReadOnly(txtIntern);
        }
        RendererTools.makeReadOnly(txtLetzte);
    }

    /**
     * DOCUMENT ME!
     */
    public void setMapWindow() {
        final CidsBean cb = this.getCidsBean();
        try {
            if (cb.getProperty(FIELD__GEOM) != null) {
                panPreviewMap.initMap(cb, FIELD__GEOM, BUFFER, MAPURL);
            } else {
                final int srid = CrsTransformer.extractSridFromCrs(CismapBroker.getInstance().getSrs().getCode());
                final BoundingBox initialBoundingBox = CismapBroker.getInstance()
                            .getMappingComponent()
                            .getMappingModel()
                            .getInitialBoundingBox();
                final Point centerPoint = initialBoundingBox.getGeometry(srid).getCentroid();

                final MetaClass geomMetaClass = ClassCacheMultiple.getMetaClass(
                        CidsBeanSupport.DOMAIN_NAME,
                        TABLE_NAME,
                        getConnectionContext());
                final CidsBean newGeom = geomMetaClass.getEmptyInstance(getConnectionContext()).getBean();
                newGeom.setProperty(FIELD__GEOM, centerPoint.buffer(20));

                panPreviewMap.initMap(newGeom, FIELD__GEOM, BUFFER);
            }
        } catch (final Exception ex) {
            Exceptions.printStackTrace(ex);
            LOG.warn("Map window not set.", ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void refreshHnr() {
        if ((getCidsBean() != null) && (getCidsBean().getProperty(FIELD__STRASSE_SCHLUESSEL) != null)) {
            final String schluessel = getCidsBean().getProperty(FIELD__STRASSE_SCHLUESSEL).toString();
            if (schluessel != null) {
                hnrSearch.setKeyId(Integer.parseInt(schluessel.replaceFirst("0*", "")));

                hnrSearch.setKeyId(Integer.parseInt(schluessel));
                initComboboxHnr();
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void initComboboxHnr() {
        new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    cbHNr.refreshModel();
                    return null;
                }

                @Override
                protected void done() {
                }
            }.execute();
    }

    /**
     * DOCUMENT ME!
     */
    private void initProperties() {
        try {
            BUFFER = EaConfProperties.getInstance().getBufferMeter();
            MAPURL = EaConfProperties.getInstance().getUrl();
        } catch (final Exception ex) {
            LOG.warn("Get no conf properties.", ex);
        }
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

    @Override
    public String getTitle() {
        if (getCidsBean().getMetaObject().getStatus() == MetaObject.NEW) {
            return TITLE_NEW_STANDORT;
        } else {
            return getCidsBean().toString();
        }
    }

    @Override
    public void dispose() {
        panPreviewMap.dispose();

        if (isEditor()) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).dispose();
            cbHNr.removeActionListener(hnrActionListener);
            cbHNr.removeAll();
            cbBetreiber.removeAll();
            if (getCidsBean() != null) {
                LOG.info("remove propchange ea_standort: " + getCidsBean());
                getCidsBean().removePropertyChangeListener(this);
            }
        }
        bindingGroup.unbind();
        super.dispose();
    }

    @Override
    public void setTitle(String title) {
        if (title == null) {
            title = "<Error>";
        }
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(FIELD__GEOM)) {
            setMapWindow();
        }
    }

    @Override
    public boolean isOkForSaving() {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();

        // Stecker prüfen
        final List<CidsBean> listSteckerBeans = CidsBeanSupport.getBeanCollectionFromProperty(
                cidsBean,
                FIELD__STECKER);
        if (listSteckerBeans.isEmpty()) {
            if (Objects.equals(getCidsBean().getProperty(FIELD__ONLINE), true)) {
                LOG.warn("No stecker specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(EaStandortEditor.class, BUNDLE_NOSTECKER));
                save = false;
            }
        } else {
            switch (checkSteckerWerte(listSteckerBeans)) {
                case 1: {
                    LOG.warn("Twice socket specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(EaStandortEditor.class, BUNDLE_TWICESTECKER));
                    save = false;
                    break;
                }
                case 3: {
                    LOG.warn("Wrong socket specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(EaStandortEditor.class, BUNDLE_WRONGANZAHL));
                    save = false;
                    break;
                }
                case 4: {
                    LOG.warn("No power specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(EaStandortEditor.class, BUNDLE_NOLEISTUNG));
                    save = false;
                    break;
                }
                case 5: {
                    LOG.warn("No socket type specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(EaStandortEditor.class, BUNDLE_NOSTECKERTYP));
                    save = false;
                    break;
                }
                case 6: {
                    LOG.warn("No socket count specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(EaStandortEditor.class, BUNDLE_NOANZAHL));
                    save = false;
                    break;
                }
            }
            if (!checkSteckerAnzahl(listSteckerBeans)) {
                LOG.warn("Dif socket count specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(EaStandortEditor.class, BUNDLE_DIFANZAHL));
                save = false;
            }
        }

        // name vorhanden
        try {
            if ((getCidsBean().getProperty(FIELD__NAME) == null)
                        || getCidsBean().getProperty(FIELD__NAME).toString().trim().isEmpty()) {
                LOG.warn("No titel specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(EaStandortEditor.class, BUNDLE_NONAME));
                save = false;
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("name not given.", ex);
            save = false;
        }

        // Standort vorhanden
        try {
            if ((getCidsBean().getProperty(FIELD__STANDORT) == null)
                        || getCidsBean().getProperty(FIELD__STANDORT).toString().trim().isEmpty()) {
                LOG.warn("No titel specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(EaStandortEditor.class, BUNDLE_NOSTANDORT));
                save = false;
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Standort not given.", ex);
            save = false;
        }

        // Status muss gefüllt sein
        try {
            if (getCidsBean().getProperty(FIELD__STATUS) == null) {
                LOG.warn("No geom specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(EaStandortEditor.class, BUNDLE_NOSTATUS));
                save = false;
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("STATUS not given.", ex);
            save = false;
        }

        if (Objects.equals(getCidsBean().getProperty(FIELD__ONLINE), true)) {
            // Säulenanzahl muss angegeben sein
            try {
                if ((getCidsBean().getProperty(FIELD__SAEULE) == null)
                            || ((int)getCidsBean().getProperty(FIELD__SAEULE) <= 0)) {
                    LOG.warn("No geom specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(EaStandortEditor.class, BUNDLE_NOSAEULE));
                    save = false;
                }
            } catch (final MissingResourceException ex) {
                LOG.warn("Säule not given.", ex);
                save = false;
            }

            // betreiber muss gefüllt sein
            try {
                if (getCidsBean().getProperty(FIELD__BETREIBER) == null) {
                    LOG.warn("No geom specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(EaStandortEditor.class, BUNDLE_NOBETREIBER));
                    save = false;
                }
            } catch (final MissingResourceException ex) {
                LOG.warn("BETREIBER not given.", ex);
                save = false;
            }

            // geom muss gefüllt sein
            try {
                if ((getCidsBean().getProperty(FIELD__GEOM) == null)
                            && (Objects.equals(getCidsBean().getProperty(FIELD__ONLINE), true))) {
                    LOG.warn("No geom specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(EaStandortEditor.class, BUNDLE_NOGEOM));
                    save = false;
                }
            } catch (final MissingResourceException ex) {
                LOG.warn("Geom not given.", ex);
                save = false;
            }
        }

        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(EaStandortEditor.class, BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(EaStandortEditor.class, BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(EaStandortEditor.class, BUNDLE_PANE_TITLE),
                JOptionPane.WARNING_MESSAGE);
        } else {
            try {
                getCidsBean().setProperty(
                    FIELD__LETZTE,
                    new java.sql.Timestamp(System.currentTimeMillis()));
            } catch (Exception ex) {
                LOG.warn("datum not set.", ex);
                return false;
            }
        }
        return save;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class LoadModelCb extends DefaultComboBoxModel {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LoadModelCb object.
         */
        public LoadModelCb() {
            super(new String[] { "Die Daten werden geladen......" });
        }
    }
}
