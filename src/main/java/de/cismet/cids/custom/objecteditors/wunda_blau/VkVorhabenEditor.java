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
import Sirius.navigator.exception.ConnectionException;
import Sirius.navigator.tools.MetaObjectCache;
import Sirius.navigator.ui.RequestsFullSizeComponent;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;

import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;


import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;

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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.MissingResourceException;

import javax.swing.*;
import javax.swing.plaf.basic.ComboPopup;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objecteditors.utils.UaConfProperties;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel;
import de.cismet.cids.custom.wunda_blau.search.server.AdresseLightweightSearch;
import de.cismet.cids.custom.wunda_blau.search.server.BufferingGeosearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultBindableLabelsPanel;
import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.FastBindableReferenceCombo;
import de.cismet.cids.editors.SaveVetoable;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.cismap.commons.BoundingBox;
import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.RasterfariDocumentLoaderPanel;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.security.WebAccessManager;

import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.SemiRoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;
import java.awt.Cursor;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class VkVorhabenEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    SaveVetoable,
    RequestsFullSizeComponent,
    PropertyChangeListener,
    RasterfariDocumentLoaderPanel.Listener {

    //~ Static fields/initializers ---------------------------------------------

    private static DefaultBindableReferenceCombo.Option SORTING_OPTION =
        new DefaultBindableReferenceCombo.SortingColumnOption("name");
    private static DefaultBindableReferenceCombo.Option NULLABLE_OPTION =
        new DefaultBindableReferenceCombo.NullableOption(null, "-");
    private static DefaultBindableReferenceCombo.Option MANAGEABLE_OPTION = null;
    private static DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

   
    private static String MAPURL;
    private static Double BUFFER;
    private static String FILES_DOKUMENTE;
    private static String FILES_FOTOS;
    private static String KOMP_FOTOS;
    private static String SHOW_FOTOS;
    private static Integer FILE_LIMIT;
    private static Integer FILE_LIMIT_DOK;

    public static final String ADRESSE_TOSTRING_TEMPLATE = "%s";
    public static final String[] ADRESSE_TOSTRING_FIELDS = { AdresseLightweightSearch.Subject.HNR.toString() };
    
    private static final Logger LOG = Logger.getLogger(VkVorhabenEditor.class);

    public static final String FIELD__ID = "id";
    public static final String FIELD__ANLEGER = "anleger";
    public static final String FIELD__ANGELEGT = "angelegt";
    public static final String FIELD__BEARBEITER = "letzter_bearbeiter";
    public static final String FIELD__AKTUALISIERT = "letzte_aktualisierung";
    public static final String FIELD__ENDE = "abgeschlossen";
    public static final String FIELD__ENDE_AM = "abgeschlossen_am";
    public static final String FIELD__VEROEFFENTLICHT = "veroeffentlicht";
    public static final String FIELD__BB = "buergerbeteiligung";
    public static final String FIELD__BB_URL = "bb_url";
    public static final String FIELD__LINK = "link";
    public static final String FIELD__STADT = "stadtweit";
    
    public static final String FIELD__FOTOS = "n_fotos";
    public static final String FIELD__STRASSE_SCHLUESSEL = "fk_strasse.strassenschluessel";
    public static final String FIELD__STRASSE_NAME = "name";                // strasse
    public static final String FIELD__STRASSE_KEY = "strassenschluessel";   // strasse
    public static final String FIELD__GEOM = "fk_geom";
    public static final String FIELD__GEO_FIELD = "geo_field";
    public static final String FIELD__GEOREFERENZ__GEO_FIELD = "fk_geom.geo_field";
    public static final String FIELD__HNR = "fk_adresse";
    public static final String FIELD__HNR_GEOM = "umschreibendes_rechteck"; // adresse
    public static final String FIELD__SBZ = "name"; // kst_stadtbezirk
    
    public static final String TABLE_NAME = "vk_vorhaben";
    public static final String TABLE_GEOM = "geom";
    public static final String TABLE_SBZ = "kst_stadtbezirk";

    public static final String BUNDLE_NOGEOM = "VkVorhabenEditor.isOkForSaving().noGeom";
    
    public static final String BUNDLE_PANE_PREFIX = "VkVorhabenEditor.isOkForSaving().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX = "VkVorhabenEditor.isOkForSaving().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "VkVorhabenEditor.isOkForSaving().JOptionPane.title";
    public static final String BUNDLE_GEOMQUESTION =
        "VkVorhabenEditor.btnCreateGeometrieActionPerformed().geom_question";
    public static final String BUNDLE_GEOMWRITE = "VkVorhabenEditor.btnCreateGeometrieActionPerformed().geom_write";
    public static final String BUNDLE_NOGEOMCREATE =
        "VkVorhabenEditor.btnCreateGeometrieActionPerformed().no_geom_create";

    
    private static final String TITLE_NEW_VORHABEN = "ein neues Vorhaben anlegen...";

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private enum DocumentCard {

        //~ Enum constants -----------------------------------------------------

        BUSY, DOCUMENT, NO_DOCUMENT, ERROR
    }

    private static enum whichUrl {

        //~ Enum constants -----------------------------------------------------

        bb, link
    }

    //~ Instance fields --------------------------------------------------------

    protected final JFileChooser fileChooserFotos = new JFileChooser();
    protected final JFileChooser fileChooserDokumente = new JFileChooser();
    boolean refreshingFirmaPanels = false;

    
    private final boolean editor;
    private final ImageIcon statusFalsch = new ImageIcon(
            getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status-busy.png"));
    private final ImageIcon statusOk = new ImageIcon(
            getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status.png"));
    private final Collection<DefaultBindableLabelsPanel> labelsPanels = new ArrayList<>();
    
    private SwingWorker worker_sbz;

    
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
    
    
    private SwingWorker worker_bb;
    private SwingWorker worker_link;

    private final DefaultListCellRenderer dlcr = new DefaultListCellRenderer();

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private DefaultBindableLabelsPanel blpStek;
    private JButton btnCreateGeometrie;
    private JComboBox cbGeom;
    private FastBindableReferenceCombo cbHNr;
    FastBindableReferenceCombo cbKontakt;
    FastBindableReferenceCombo cbStrasse;
    private DefaultBindableReferenceCombo cbThema;
    JCheckBox chAbgeschlossen;
    JCheckBox chBB;
    JCheckBox chStadtweit;
    JCheckBox chVeroeffentlicht;
    private Box.Filler filler3;
    private Box.Filler filler5;
    private JPanel jPanelAllgemein;
    private JPanel jPanelDetails;
    private JPanel jPanelDokAuswahl;
    private JPanel jPanelDokumente;
    private JPanel jPanelFotoAuswahl;
    private JPanel jPanelFotos;
    JTabbedPane jTabbedPane;
    private JLabel lblAbAm;
    private JLabel lblAbgeschlossen;
    private JLabel lblAngelegtAm;
    private JLabel lblAnleger;
    private JLabel lblBB;
    private JLabel lblBemerkung;
    private JLabel lblBeschreibung;
    private JLabel lblGeom;
    private JLabel lblHNrRenderer;
    private JLabel lblHnr;
    private JLabel lblKarte;
    private JLabel lblKontakt;
    private JLabel lblLetzteA;
    private JLabel lblLetzterB;
    private JLabel lblLink;
    private JLabel lblLinkCheck;
    private JLabel lblOrt;
    private JLabel lblStadtbezirke;
    private JLabel lblStadtweit;
    private JLabel lblStek;
    private JLabel lblStrasse;
    private JLabel lblText;
    private JLabel lblThema;
    private JLabel lblTitel;
    private JLabel lblUrl;
    private JLabel lblUrlCheck;
    private JLabel lblVeroeffentlicht;
    private JPanel panBemerkung;
    private JPanel panBeschreibung;
    private JPanel panContent;
    private JPanel panDaten;
    private JPanel panDetails;
    private JPanel panEinsatz;
    private JPanel panGeometrie;
    private JPanel panLink;
    private JPanel panOrt;
    private DefaultPreviewMapPanel panPreviewMap;
    private JPanel panSbz;
    private JPanel panText;
    private JPanel panUrl;
    private JPanel pnlCard1;
    private RoundedPanel rpKarte;
    private JScrollPane scpBemerkung;
    private JScrollPane scpBeschreibung;
    private JScrollPane scpOrt;
    private JScrollPane scpSbz;
    private JScrollPane scpText;
    private SemiRoundedPanel semiRoundedPanel7;
    private JTextArea taBemerkung;
    private JTextArea taBeschreibung;
    private JTextArea taOrt;
    private JTextArea taSbz;
    private JTextArea taText;
    private JTextField txtAbAm;
    private JTextField txtAngelegtAm;
    private JTextField txtAnleger;
    private JTextField txtLetzteA;
    private JTextField txtLetzterB;
    private JTextField txtLink;
    private JTextField txtTitel;
    private JTextField txtUrl;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public VkVorhabenEditor() {
        this(true);
    }

    /**
     * Creates a new VkVorhabenEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public VkVorhabenEditor(final boolean boolEditor) {
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
        labelsPanels.clear();
        super.initWithConnectionContext(connectionContext);
        initProperties();
        
        initComponents();
        labelsPanels.addAll(Arrays.asList(blpStek));
        for (final DefaultBindableLabelsPanel labelsPanel : labelsPanels) {
            MetaObjectCache.getInstance().clearCache(labelsPanel.getMetaClass());
            labelsPanel.initWithConnectionContext(getConnectionContext());
        }
        cbThema.setNullable(false);
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
        panEinsatz = new JPanel();
        pnlCard1 = new JPanel();
        jTabbedPane = new JTabbedPane();
        jPanelAllgemein = new JPanel();
        panGeometrie = new JPanel();
        rpKarte = new RoundedPanel();
        panPreviewMap = new DefaultPreviewMapPanel();
        semiRoundedPanel7 = new SemiRoundedPanel();
        lblKarte = new JLabel();
        panDaten = new JPanel();
        lblTitel = new JLabel();
        txtTitel = new JTextField();
        lblGeom = new JLabel();
        if (isEditor()){
            cbGeom = new DefaultCismapGeometryComboBoxEditor();
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).setAllowedGeometryTypes(new Class[] { Point.class, Polygon.class, MultiPolygon.class});
        }
        lblStrasse = new JLabel();
        cbStrasse = new FastBindableReferenceCombo();
        lblHnr = new JLabel();
        if (!isEditor()){
            lblHNrRenderer = new JLabel();
        }
        if (isEditor()){
            cbHNr = new FastBindableReferenceCombo(
                hnrSearch,
                hnrSearch.getRepresentationPattern(),
                hnrSearch.getRepresentationFields()
            );
        }
        lblOrt = new JLabel();
        panOrt = new JPanel();
        scpOrt = new JScrollPane();
        taOrt = new JTextArea();
        lblStadtweit = new JLabel();
        chStadtweit = new JCheckBox();
        lblStadtbezirke = new JLabel();
        panSbz = new JPanel();
        scpSbz = new JScrollPane();
        taSbz = new JTextArea();
        lblThema = new JLabel();
        cbThema = new DefaultBindableReferenceCombo(NULLABLE_OPTION, MANAGEABLE_OPTION, SORTING_OPTION);
        lblVeroeffentlicht = new JLabel();
        chVeroeffentlicht = new JCheckBox();
        lblBeschreibung = new JLabel();
        panBeschreibung = new JPanel();
        scpBeschreibung = new JScrollPane();
        taBeschreibung = new JTextArea();
        filler3 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        btnCreateGeometrie = new JButton();
        jPanelDetails = new JPanel();
        panDetails = new JPanel();
        lblAnleger = new JLabel();
        txtAnleger = new JTextField();
        lblAngelegtAm = new JLabel();
        txtAngelegtAm = new JTextField();
        lblLetzterB = new JLabel();
        txtLetzterB = new JTextField();
        lblLetzteA = new JLabel();
        txtLetzteA = new JTextField();
        lblAbgeschlossen = new JLabel();
        chAbgeschlossen = new JCheckBox();
        filler5 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        lblAbAm = new JLabel();
        txtAbAm = new JTextField();
        lblBB = new JLabel();
        chBB = new JCheckBox();
        lblUrl = new JLabel();
        txtUrl = new JTextField();
        panUrl = new JPanel();
        lblUrlCheck = new JLabel();
        lblText = new JLabel();
        panText = new JPanel();
        scpText = new JScrollPane();
        taText = new JTextArea();
        lblLink = new JLabel();
        txtLink = new JTextField();
        panLink = new JPanel();
        lblLinkCheck = new JLabel();
        lblStek = new JLabel();
        blpStek = new DefaultBindableLabelsPanel(isEditor(), "Fokusraum STEK:", SORTING_OPTION);
        lblKontakt = new JLabel();
        cbKontakt = new FastBindableReferenceCombo();
        lblBemerkung = new JLabel();
        panBemerkung = new JPanel();
        scpBemerkung = new JScrollPane();
        taBemerkung = new JTextArea();
        jPanelDokumente = new JPanel();
        jPanelDokAuswahl = new JPanel();
        jPanelFotos = new JPanel();
        jPanelFotoAuswahl = new JPanel();

        setLayout(new GridBagLayout());

        panContent.setName(""); // NOI18N
        panContent.setOpaque(false);
        panContent.setLayout(new GridBagLayout());

        panEinsatz.setOpaque(false);
        panEinsatz.setLayout(new GridBagLayout());

        pnlCard1.setOpaque(false);
        pnlCard1.setLayout(new GridBagLayout());

        jPanelAllgemein.setOpaque(false);
        jPanelAllgemein.setLayout(new GridBagLayout());

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
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panGeometrie.add(rpKarte, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 10, 10, 10);
        jPanelAllgemein.add(panGeometrie, gridBagConstraints);

        panDaten.setOpaque(false);
        panDaten.setLayout(new GridBagLayout());

        lblTitel.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblTitel.setText("Titel:");
        lblTitel.setToolTipText("");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblTitel, gridBagConstraints);

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.titel}"), txtTitel, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtTitel, gridBagConstraints);

        lblGeom.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblGeom.setText("Geometrie:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblGeom, gridBagConstraints);

        if (isEditor()){
            cbGeom.setFont(new Font("Dialog", 0, 12)); // NOI18N

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_geom}"), cbGeom, BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeom).getConverter());
            bindingGroup.addBinding(binding);

        }
        if (isEditor()){
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 4;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            panDaten.add(cbGeom, gridBagConstraints);
        }

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

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_strasse}"), cbStrasse, BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        cbStrasse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cbStrasseActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(cbStrasse, gridBagConstraints);

        lblHnr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblHnr.setText("Hausnummer:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblHnr, gridBagConstraints);

        if (!isEditor()){
            lblHNrRenderer.setFont(new Font("Dialog", 0, 12)); // NOI18N

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_adresse.hausnummer}"), lblHNrRenderer, BeanProperty.create("text"));
            binding.setSourceNullValue("----");
            binding.setSourceUnreadableValue("----");
            bindingGroup.addBinding(binding);

        }
        if (!isEditor()){
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 4;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(2, 5, 2, 5);
            panDaten.add(lblHNrRenderer, gridBagConstraints);
        }

        if (isEditor()){
            cbHNr.setMaximumRowCount(20);
            cbHNr.setEnabled(false);
            cbHNr.setMinimumSize(new Dimension(100, 19));
            cbHNr.setPreferredSize(new Dimension(100, 19));

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_adresse}"), cbHNr, BeanProperty.create("selectedItem"));
            bindingGroup.addBinding(binding);

        }
        if (isEditor()){
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 4;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            panDaten.add(cbHNr, gridBagConstraints);
        }

        lblOrt.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblOrt.setText("Ortsbeschreibung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblOrt, gridBagConstraints);

        panOrt.setOpaque(false);
        panOrt.setLayout(new GridBagLayout());

        taOrt.setColumns(20);
        taOrt.setLineWrap(true);
        taOrt.setRows(2);
        taOrt.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.ortsbeschreibung}"), taOrt, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpOrt.setViewportView(taOrt);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panOrt.add(scpOrt, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panOrt, gridBagConstraints);

        lblStadtweit.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblStadtweit.setText("Stadtweit:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblStadtweit, gridBagConstraints);

        chStadtweit.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.stadtweit}"), chStadtweit, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(chStadtweit, gridBagConstraints);

        lblStadtbezirke.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblStadtbezirke.setText("Stadtbezirke:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblStadtbezirke, gridBagConstraints);

        panSbz.setOpaque(false);
        panSbz.setLayout(new GridBagLayout());

        taSbz.setColumns(20);
        taSbz.setLineWrap(true);
        taSbz.setRows(2);
        taSbz.setWrapStyleWord(true);
        scpSbz.setViewportView(taSbz);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panSbz.add(scpSbz, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panSbz, gridBagConstraints);

        lblThema.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblThema.setText("Thema:");
        lblThema.setToolTipText("");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblThema, gridBagConstraints);

        cbThema.setFont(new Font("Dialog", 0, 12)); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_thema}"), cbThema, BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(cbThema, gridBagConstraints);

        lblVeroeffentlicht.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblVeroeffentlicht.setText("Veröffentlicht:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblVeroeffentlicht, gridBagConstraints);

        chVeroeffentlicht.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.veroeffentlicht}"), chVeroeffentlicht, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(chVeroeffentlicht, gridBagConstraints);

        lblBeschreibung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBeschreibung.setText("Beschreibung:");
        lblBeschreibung.setToolTipText("");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblBeschreibung, gridBagConstraints);

        panBeschreibung.setOpaque(false);
        panBeschreibung.setLayout(new GridBagLayout());

        taBeschreibung.setColumns(20);
        taBeschreibung.setLineWrap(true);
        taBeschreibung.setRows(2);
        taBeschreibung.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.beschreibung}"), taBeschreibung, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpBeschreibung.setViewportView(taBeschreibung);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panBeschreibung.add(scpBeschreibung, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panBeschreibung, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panDaten.add(filler3, gridBagConstraints);

        btnCreateGeometrie.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/wizard.png"))); // NOI18N
        btnCreateGeometrie.setToolTipText("Geometrie aus Adresse generieren");
        btnCreateGeometrie.setMaximumSize(new Dimension(66, 50));
        btnCreateGeometrie.setMinimumSize(new Dimension(20, 19));
        btnCreateGeometrie.setPreferredSize(new Dimension(33, 24));
        btnCreateGeometrie.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnCreateGeometrieActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(btnCreateGeometrie, gridBagConstraints);
        btnCreateGeometrie.setVisible(isEditor());

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 5, 10);
        jPanelAllgemein.add(panDaten, gridBagConstraints);

        jTabbedPane.addTab("Allgemein", jPanelAllgemein);

        jPanelDetails.setOpaque(false);
        jPanelDetails.setLayout(new GridBagLayout());

        panDetails.setOpaque(false);
        panDetails.setLayout(new GridBagLayout());

        lblAnleger.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblAnleger.setText("Anleger:");
        lblAnleger.setToolTipText("");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDetails.add(lblAnleger, gridBagConstraints);

        txtAnleger.setMinimumSize(new Dimension(10, 24));
        txtAnleger.setPreferredSize(new Dimension(10, 24));

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.anleger}"), txtAnleger, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDetails.add(txtAnleger, gridBagConstraints);

        lblAngelegtAm.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblAngelegtAm.setText("angelegt am:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDetails.add(lblAngelegtAm, gridBagConstraints);

        txtAngelegtAm.setMinimumSize(new Dimension(10, 24));
        txtAngelegtAm.setPreferredSize(new Dimension(10, 24));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDetails.add(txtAngelegtAm, gridBagConstraints);

        lblLetzterB.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblLetzterB.setText("letzter Bearbeiter:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDetails.add(lblLetzterB, gridBagConstraints);

        txtLetzterB.setMinimumSize(new Dimension(10, 24));
        txtLetzterB.setPreferredSize(new Dimension(10, 24));

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.letzter_bearbeiter}"), txtLetzterB, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDetails.add(txtLetzterB, gridBagConstraints);

        lblLetzteA.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblLetzteA.setText("letzte Bearbeitung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDetails.add(lblLetzteA, gridBagConstraints);

        txtLetzteA.setMinimumSize(new Dimension(10, 24));
        txtLetzteA.setPreferredSize(new Dimension(10, 24));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDetails.add(txtLetzteA, gridBagConstraints);

        lblAbgeschlossen.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblAbgeschlossen.setText("abgeschlossen:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDetails.add(lblAbgeschlossen, gridBagConstraints);

        chAbgeschlossen.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.abgeschlossen}"), chAbgeschlossen, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDetails.add(chAbgeschlossen, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panDetails.add(filler5, gridBagConstraints);

        lblAbAm.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblAbAm.setText("abgeschlossen am:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDetails.add(lblAbAm, gridBagConstraints);

        txtAbAm.setMinimumSize(new Dimension(10, 24));
        txtAbAm.setPreferredSize(new Dimension(10, 24));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDetails.add(txtAbAm, gridBagConstraints);

        lblBB.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBB.setText("Bürgerbeteiligung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDetails.add(lblBB, gridBagConstraints);

        chBB.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.buergerbeteiligung}"), chBB, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDetails.add(chBB, gridBagConstraints);

        lblUrl.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblUrl.setText("BB-Url:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDetails.add(lblUrl, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.bb_url}"), txtUrl, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDetails.add(txtUrl, gridBagConstraints);

        panUrl.setOpaque(false);
        panUrl.setLayout(new GridBagLayout());

        lblUrlCheck.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status-busy.png"))); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        panUrl.add(lblUrlCheck, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDetails.add(panUrl, gridBagConstraints);

        lblText.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblText.setText("BB-Text:");
        lblText.setToolTipText("");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDetails.add(lblText, gridBagConstraints);

        panText.setOpaque(false);
        panText.setLayout(new GridBagLayout());

        taText.setColumns(20);
        taText.setLineWrap(true);
        taText.setRows(3);
        taText.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.bb_text}"), taText, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpText.setViewportView(taText);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panText.add(scpText, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDetails.add(panText, gridBagConstraints);

        lblLink.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblLink.setText("Link:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDetails.add(lblLink, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.link}"), txtLink, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDetails.add(txtLink, gridBagConstraints);

        panLink.setOpaque(false);
        panLink.setLayout(new GridBagLayout());

        lblLinkCheck.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status-busy.png"))); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        panLink.add(lblLinkCheck, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDetails.add(panLink, gridBagConstraints);

        lblStek.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblStek.setText("Fokusraum STEK:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDetails.add(lblStek, gridBagConstraints);

        blpStek.setOpaque(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.arr_vorhaben_stek}"), blpStek, BeanProperty.create("selectedElements"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDetails.add(blpStek, gridBagConstraints);

        lblKontakt.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblKontakt.setText("Kontakt:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDetails.add(lblKontakt, gridBagConstraints);

        cbKontakt.setMaximumRowCount(20);
        cbKontakt.setModel(new LoadModelCb());
        cbKontakt.setRepresentationFields(new String[] {"mail"});

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_kontakt}"), cbKontakt, BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDetails.add(cbKontakt, gridBagConstraints);

        lblBemerkung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBemerkung.setText("Bemerkung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDetails.add(lblBemerkung, gridBagConstraints);

        panBemerkung.setOpaque(false);
        panBemerkung.setLayout(new GridBagLayout());

        taBemerkung.setColumns(20);
        taBemerkung.setLineWrap(true);
        taBemerkung.setRows(3);
        taBemerkung.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.bemerkung}"), taBemerkung, BeanProperty.create("text"));
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
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDetails.add(panBemerkung, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 5, 10);
        jPanelDetails.add(panDetails, gridBagConstraints);

        jTabbedPane.addTab("Details", jPanelDetails);

        jPanelDokumente.setOpaque(false);
        jPanelDokumente.setLayout(new GridBagLayout());

        jPanelDokAuswahl.setOpaque(false);
        jPanelDokAuswahl.setLayout(new GridBagLayout());
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(0, 5, 0, 0);
        jPanelDokumente.add(jPanelDokAuswahl, gridBagConstraints);

        jTabbedPane.addTab("Dokumente", jPanelDokumente);

        jPanelFotos.setOpaque(false);
        jPanelFotos.setLayout(new GridBagLayout());

        jPanelFotoAuswahl.setOpaque(false);
        jPanelFotoAuswahl.setLayout(new GridBagLayout());
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(0, 5, 0, 0);
        jPanelFotos.add(jPanelFotoAuswahl, gridBagConstraints);

        jTabbedPane.addTab("Fotos", jPanelFotos);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
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
        panEinsatz.add(pnlCard1, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panContent.add(panEinsatz, gridBagConstraints);

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
    }// </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbStrasseActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbStrasseActionPerformed
        if (isEditor() && (getCidsBean() != null) && (getCidsBean().getProperty(FIELD__STRASSE_SCHLUESSEL) != null)) {
            cbHNr.setSelectedItem(null);
            cbHNr.setEnabled(true);
            refreshHnr();
        }
    }//GEN-LAST:event_cbStrasseActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnCreateGeometrieActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_btnCreateGeometrieActionPerformed
        if ((getCidsBean() != null) && (getCidsBean().getProperty(FIELD__HNR) != null)) {
            final CidsBean beanHnr = (CidsBean)getCidsBean().getProperty(FIELD__HNR);
            int result = JOptionPane.OK_OPTION;
            if (getCidsBean().getProperty(FIELD__GEOM) != null) {
                final Object[] options = { "Ja, Geom überschreiben", "Abbrechen" };
                result = JOptionPane.showOptionDialog(StaticSwingTools.getParentFrame(this),
                        NbBundle.getMessage(VkVorhabenEditor.class, BUNDLE_GEOMQUESTION),
                        NbBundle.getMessage(VkVorhabenEditor.class, BUNDLE_GEOMWRITE),
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        options,
                        options[1]);
            }
            if ((result == JOptionPane.CLOSED_OPTION) || (result == 1)) {
                return;
            } else {
                final CidsBean beanAdresse = (CidsBean)beanHnr.getProperty(FIELD__HNR_GEOM);
                final CidsBean beanNewGeometrie = CidsBeanSupport.cloneBean(
                        beanAdresse,
                        getConnectionContext(),
                        TABLE_GEOM);
                try {
                    this.getCidsBean().setProperty(FIELD__GEOM, beanNewGeometrie);
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        } else {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(VkVorhabenEditor.class, BUNDLE_NOGEOMCREATE),
                NbBundle.getMessage(VkVorhabenEditor.class, BUNDLE_PANE_TITLE),
                JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnCreateGeometrieActionPerformed

    

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

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getCurrentUser() {
        return SessionManager.getSession().getUser().getName();
    }

    @Override
    public void setCidsBean(final CidsBean cb) {
        try {
            if (isEditor() && (getCidsBean() != null)) {
                LOG.info("remove propchange vk_vorhaben: " + getCidsBean());
                getCidsBean().removePropertyChangeListener(this);
                cbHNr.removeActionListener(hnrActionListener);
            }
            for (final DefaultBindableLabelsPanel labelsPanel : labelsPanels) {
                if (labelsPanel != null) {
                    labelsPanel.setMetaClass(labelsPanel.getMetaClass());
                }
            }
            labelsPanels.clear();
            blpStek.clear();
            bindingGroup.unbind();
            this.cidsBean = cb;
            if (isEditor() && (getCidsBean() != null)) {
                LOG.info("add propchange vk_vorhaben: " + getCidsBean());
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
            if (getCidsBean() != null) {
                labelsPanels.addAll(Arrays.asList(blpStek));
            }
            for (final DefaultBindableLabelsPanel labelsPanel : labelsPanels) {
                if (labelsPanel != null) {
                    labelsPanel.reload(true);
                }
            }
            if (getCidsBean().getMetaObject().getStatus() == MetaObject.NEW) {
                setTimestamp(FIELD__ANGELEGT,FIELD__ANLEGER);
                try {
                    getCidsBean().setProperty(
                        FIELD__ENDE,
                        false);
                } catch (Exception e) {
                    LOG.error("Cannot set abgeschlossen", e);
                }
                try {
                    getCidsBean().setProperty(
                        FIELD__BB,
                        false);
                } catch (Exception e) {
                    LOG.error("Cannot set keine Wartung", e);
                }
                try {
                    getCidsBean().setProperty(
                        FIELD__STADT,
                        false);
                } catch (Exception e) {
                    LOG.error("Cannot set keine Dauerläufer", e);
                }
                try {
                    getCidsBean().setProperty(
                        FIELD__VEROEFFENTLICHT,
                        false);
                } catch (Exception e) {
                    LOG.error("Cannot set keine Barrierefrei", e);
                }
            } else {
                setStadtbezirke();
                checkBB();
                checkLink();
            } 
            txtAngelegtAm.setText(DATE_FORMAT.format(cidsBean.getProperty(FIELD__ANGELEGT)));
            if (cidsBean.getProperty(FIELD__AKTUALISIERT) != null){
                txtLetzteA.setText(DATE_FORMAT.format(cidsBean.getProperty(FIELD__AKTUALISIERT)));
            }
            if (cidsBean.getProperty(FIELD__ENDE_AM) != null){
                txtAbAm.setText(DATE_FORMAT.format(cidsBean.getProperty(FIELD__ENDE_AM)));
            }
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
                StaticSwingTools.decorateWithFixedAutoCompleteDecorator(cbThema);
                {
                    final JList pop = ((ComboPopup)cbThema.getUI().getAccessibleChild(cbThema, 0))
                                .getList();
                    final JTextField txt = (JTextField)cbThema.getEditor().getEditorComponent();
                }
            }
            beanHNr = ((CidsBean)getCidsBean().getProperty(FIELD__HNR));
        } catch (Exception ex) {
            LOG.error("Bean not set", ex);
        }
    }

    private void checkUrl(final String url, final JLabel showLabel, final whichUrl toCheck) {
        showLabel.setIcon(statusFalsch);
        showLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        final SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {

            @Override
            protected Boolean doInBackground() throws Exception {
                System.out.print("Checke url:");
                return WebAccessManager.getInstance().checkIfURLaccessible(new URL(url));
            }

            @Override
            protected void done() {
                final Boolean check;
                try {
                    check = get();
                    System.out.print("Bekomme Ergebnis:");
                    System.out.print(check.toString());
                    if (check) {
                        showLabel.setIcon(statusOk);
                        showLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    } else {
                        showLabel.setIcon(statusFalsch);
                        showLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    }
                } catch (InterruptedException | ExecutionException e) {
                    showLabel.setIcon(statusFalsch);
                    showLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    LOG.warn("URL Check Problem in Worker.", e);
                }
            }
        };
        if (toCheck.equals(whichUrl.bb)){
            if (worker_bb != null) {
                worker_bb.cancel(true);
            }
            worker_bb = worker;
            worker_bb.execute();
        } else {
            if (worker_link != null) {
                worker_link.cancel(true);
            }
            worker_link = worker;
            worker_link.execute();
        }
    }

    private void checkBB(){
       checkUrl(txtUrl.getText(), lblUrlCheck, whichUrl.bb);
    }

    
    private void checkLink(){
       checkUrl(txtLink.getText(), lblLinkCheck, whichUrl.link);
    }
   

    /**
     * DOCUMENT ME!
     */
    private void setReadOnly() {
        if (!(isEditor())) {
            RendererTools.makeReadOnly(txtTitel);
            lblGeom.setVisible(isEditor());
            RendererTools.makeReadOnly(taOrt);
            RendererTools.makeReadOnly(cbStrasse);
            // lblHNrRenderer.setVisible(true);
            // RendererTools.makeReadOnly(cbHNr);
            RendererTools.makeReadOnly(cbThema);
            RendererTools.makeReadOnly(cbThema);
            RendererTools.makeReadOnly(cbKontakt);
            RendererTools.makeReadOnly(chAbgeschlossen);
            RendererTools.makeReadOnly(blpStek);
            RendererTools.makeReadOnly(taBemerkung);
        }
        RendererTools.makeReadOnly(txtAnleger);
        RendererTools.makeReadOnly(txtLetzteA);
        RendererTools.makeReadOnly(txtLetzterB);
        RendererTools.makeReadOnly(txtAngelegtAm);
        RendererTools.makeReadOnly(txtAbAm);
        RendererTools.makeReadOnly(taSbz);
    }
    
    public void setStadtbezirke(){
        final SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {

            String pattern = null;
            JTextField field = null;

            @Override
            protected String doInBackground() throws Exception {                
                if (getCidsBean().getProperty(FIELD__STADT) == null || Objects.equals(getCidsBean().getProperty(FIELD__STADT),false)) {
                    return getStadtbezirkeWithGeom((Geometry)getCidsBean().getProperty(FIELD__GEOREFERENZ__GEO_FIELD));
                } 
                return "Elberfeld, Elberfeld-West, Uellendahl-Katernberg, Vohwinkel, Cronenberg, Barmen, Oberbarmen, Heckinghausen, Langefeld-Beyenburg, Ronsdorf";
            }

            @Override
            protected void done() {
                try {
                    if (!isCancelled()) {
                        final String result = get();
                        taSbz.setText(result); 
                    }
                } catch (final InterruptedException | ExecutionException ex) {
                    LOG.error("Fehler bei der Ermittlung der Stadtbezirke.", ex);
                } 
            }
        };
         if (worker_sbz != null) {
                worker_sbz.cancel(true);
            }
            worker_sbz = worker;
            worker_sbz.execute();    
    }
    
    public String getStadtbezirkeWithGeom(final Geometry geomVorhaben){
        String sbz = "kein Stadtbezirk gefunden";
        final MetaClass mc = ClassCacheMultiple.getMetaClass(
                "WUNDA_BLAU",
                TABLE_SBZ,
                getConnectionContext());
        if (mc != null) {
            // Suche Konfigurieren
            final BufferingGeosearch search = new BufferingGeosearch();
            search.setValidClasses(Arrays.asList(mc));
            search.setGeometry(geomVorhaben);
            // Suche ausführen
            final Collection<MetaObjectNode> mons;
            try {
                mons = SessionManager.getProxy()
                        .customServerSearch(
                                SessionManager.getSession().getUser(),
                                search,
                                getConnectionContext());
                
                if ((mons != null) && !mons.isEmpty()) {
                    sbz = "";
                    MetaObject mo;
                    CidsBean beanSbz;
                    for (final MetaObjectNode mon:mons){
                        mo = SessionManager.getProxy()
                                .getMetaObject(mon.getObjectId(),
                                    mon.getClassId(),
                                    mon.getDomain(),
                                    getConnectionContext());

                        beanSbz = mo.getBean();
                        if (sbz.length() == 0){
                            sbz = beanSbz.getProperty(FIELD__SBZ).toString();
                        } else {
                            sbz += ", " + beanSbz.getProperty(FIELD__SBZ).toString();
                        }
                    }
                }
                
            } catch (ConnectionException ex) {
                Exceptions.printStackTrace(ex);
            }   
        }
        return sbz;
    }

    /**
     * DOCUMENT ME!
     */
    public void setMapWindow() {
        final CidsBean cb = this.getCidsBean();
        try {
            if (cb.getProperty(FIELD__GEOM) != null) {
                panPreviewMap.initMap(cb, FIELD__GEOREFERENZ__GEO_FIELD, BUFFER, MAPURL);
            } else {
                final int srid = CrsTransformer.extractSridFromCrs(CismapBroker.getInstance().getSrs().getCode());
                final BoundingBox initialBoundingBox = CismapBroker.getInstance()
                            .getMappingComponent()
                            .getMappingModel()
                            .getInitialBoundingBox();
                final Point centerPoint = initialBoundingBox.getGeometry(srid).getCentroid();

                final MetaClass geomMetaClass = ClassCacheMultiple.getMetaClass(
                        CidsBeanSupport.DOMAIN_NAME,
                        TABLE_GEOM,
                        getConnectionContext());
                final CidsBean newGeom = geomMetaClass.getEmptyInstance(getConnectionContext()).getBean();
                newGeom.setProperty(FIELD__GEO_FIELD, centerPoint);
                panPreviewMap.initMap(newGeom, FIELD__GEO_FIELD, BUFFER);
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
            BUFFER = UaConfProperties.getInstance().getBufferMeter();
            MAPURL = UaConfProperties.getInstance().getUrl();

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
            return TITLE_NEW_VORHABEN;
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
            cbThema.removeAll();
            if (getCidsBean() != null) {
                LOG.info("remove propchange vk_vorhaben: " + getCidsBean());
                getCidsBean().removePropertyChangeListener(this);
            }
        }
        bindingGroup.unbind();
        if (labelsPanels != null) {
            for (final DefaultBindableLabelsPanel panel : labelsPanels) {
                panel.dispose();
            }
        }
        labelsPanels.clear();


        bindingGroup.unbind();
        super.dispose();
    }
    
    public boolean setTimestamp(final String am, final String durch) {
        try {
            getCidsBean().setProperty(
                durch,
                getCurrentUser());
        } catch (Exception ex) {
            LOG.warn("User not set.", ex);
            return false;
        }
        try {
            getCidsBean().setProperty(
                am,
                new java.sql.Timestamp(System.currentTimeMillis()));
        } catch (Exception ex) {
            LOG.warn("datum not set.", ex);
            return false;
        }
        return true;
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
            setStadtbezirke();
        }
        if (evt.getPropertyName().equals(FIELD__STADT)) {
            setStadtbezirke();
        }
        if (evt.getPropertyName().equals(FIELD__BB_URL)) {
            checkBB();
        }
        if (evt.getPropertyName().equals(FIELD__LINK)) {
            checkLink();
        }
    }

    @Override
    public boolean isOkForSaving() {
        boolean save = true;
        boolean noErrorOccured = true;
        boolean errorFirma = true;
        final StringBuilder errorMessage = new StringBuilder();

        
/*
        // Melder
        try {
            if (getCidsBean().getProperty(FIELD__MELDER) == null) {
                LOG.warn("No melder specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(VkVorhabenEditor.class, BUNDLE_NOMELDER));
                save = false;
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("melder not given.", ex);
            save = false;
        }

        // Beteiligte Einsatz
        try {
            final Collection<CidsBean> collectionBeteiligteE = getCidsBean().getBeanCollectionProperty(
                    FIELD__BETEILIGTE_E_ARR);
            if ((collectionBeteiligteE == null) || collectionBeteiligteE.isEmpty()) {
                LOG.warn("No beteiligte einsatz specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(VkVorhabenEditor.class, BUNDLE_NOBETE));
                save = false;
            } else {
                Boolean keiner = false;
                for (final CidsBean betBean : collectionBeteiligteE) {
                    if ((betBean.getProperty(FIELD__BET_SCHLUESSEL)).toString().equals(BET_KEINER)) {
                        keiner = true;
                    }
                }
                if (keiner && (collectionBeteiligteE.size() > 1)) {
                    LOG.warn("keiner + specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(VkVorhabenEditor.class, BUNDLE_BETKEINER));
                    save = false;
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("beteiligte einsatz not given.", ex);
            save = false;
        }
*/

        // georeferenz muss gefüllt sein
        try {
            if (getCidsBean().getProperty(FIELD__GEOM) == null) {
                LOG.warn("No geom specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(VkVorhabenEditor.class, BUNDLE_NOGEOM));
                save = false;
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Geom not given.", ex);
            save = false;
        }
        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(VkVorhabenEditor.class, BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(VkVorhabenEditor.class, BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(VkVorhabenEditor.class, BUNDLE_PANE_TITLE),
                JOptionPane.WARNING_MESSAGE);
        } 
        setTimestamp(FIELD__AKTUALISIERT, FIELD__BEARBEITER);
        return save && noErrorOccured;
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
