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
import Sirius.navigator.ui.RequestsFullSizeComponent;
import Sirius.server.middleware.types.LightweightMetaObject;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;

import com.vividsolutions.jts.geom.Point;

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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.MissingResourceException;

import javax.swing.*;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objecteditors.utils.UaConfProperties;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel;
import de.cismet.cids.custom.wunda_blau.search.server.AdresseLightweightSearch;
import de.cismet.cids.custom.wunda_blau.search.server.NextNumberSearch;
import de.cismet.cids.custom.wunda_blau.search.server.UaBereitschaftLightweightSearch;
import de.cismet.cids.custom.wunda_blau.search.server.UaVerursacherLightweightSearch;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.editors.DefaultBindableDateChooser;
import de.cismet.cids.editors.DefaultBindableLabelsPanel;
import de.cismet.cids.editors.DefaultBindableReferenceCombo;

import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.FastBindableReferenceCombo;
import de.cismet.cids.editors.SaveVetoable;
import de.cismet.cids.editors.hooks.AfterSavingHook;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

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
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.ComboPopup;
import lombok.Getter;
import lombok.Setter;
import org.jdesktop.swingx.JXBusyLabel;
/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class UaEinsatzEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    SaveVetoable,
    AfterSavingHook,
    RequestsFullSizeComponent,
    PropertyChangeListener,
    RasterfariDocumentLoaderPanel.Listener {

    //~ Static fields/initializers ---------------------------------------------
    private static DefaultBindableReferenceCombo.Option SORTING_OPTION =
        new DefaultBindableReferenceCombo.SortingColumnOption("name");
    private static DefaultBindableReferenceCombo.Option NULLABLE_OPTION =
        new DefaultBindableReferenceCombo.NullableOption(null, "-");
    private static DefaultBindableReferenceCombo.Option SORTING_OPTION_MELDER =
        new DefaultBindableReferenceCombo.SortingColumnOption("sortierung");
    private static DefaultBindableReferenceCombo.Option MANAGEABLE_OPTION = null;
    
    private static final String VERURSACHER_TOSTRING_TEMPLATE = "%s";
    private static final String[] VERURSACHER_TOSTRING_FIELDS = { "id" };
    
    private final UaVerursacherLightweightSearch searchVerursacher = new UaVerursacherLightweightSearch(
                VERURSACHER_TOSTRING_TEMPLATE,
                VERURSACHER_TOSTRING_FIELDS);

    

    private static String MAPURL;
    private static Double BUFFER;
    private static String RASTERFARI;
    private static String THEMA;
    private static String DOKUMENTE;
    private static String FOTOS;
    
    public static final String ADRESSE_TOSTRING_TEMPLATE = "%s";
    public static final String[] ADRESSE_TOSTRING_FIELDS = { AdresseLightweightSearch.Subject.HNR.toString() };
    public static final String BEREIT_TOSTRING_TEMPLATE = "%s";
    public static final String[] BEREIT_TOSTRING_FIELDS = { UaBereitschaftLightweightSearch.Subject.NAME.toString() };
    public static final String NEXT_TOSTRING_TEMPLATE = "%s";
    public static final String[] NEXT_TOSTRING_FIELDS = { "aktenzeichen", "id", "split_part( aktenzeichen,'-',3) AS nummer" };
    public static final String NEXT_TABLE = "ua_einsatz";
    public static final String CONF_VERURSACHER = "darfUaVerursacher";

    private static final Logger LOG = Logger.getLogger(UaEinsatzEditor.class);

    public static final String FIELD__ID = "id"; 
    public static final String FIELD__AZ = "aktenzeichen";
    public static final String FIELD__DATUM = "datum";
    public static final String FIELD__BEREIT = "fk_bereitschaft";
    public static final String FIELD__MELDER = "fk_melder";
    public static final String FIELD__BETEILIGTE_E_ARR = "arr_beteiligte_einsatz";
    public static final String FIELD__BETEILIGTE_F_ARR = "arr_beteiligte_folge";
    public static final String FIELD__ARTEN_ARR = "arr_unfallarten";
    public static final String FIELD__BET_SCHLUESSEL = "schluessel";           //ua_beteiligte
    public static final String FIELD__GEW_NAME = "name";                            //ua_gewaesser
    public static final String FIELD__GEW_WV = "wv";                                //ua_gewaesser
    public static final String FIELD__ANLEGER = "anleger";
    public static final String FIELD__STRASSE_SCHLUESSEL = "fk_strasse.strassenschluessel";
    public static final String FIELD__STRASSE_NAME = "name";                                // strasse
    public static final String FIELD__STRASSE_KEY = "strassenschluessel";                   // strasse
    public static final String FIELD__GEOM = "fk_geom";
    public static final String FIELD__GEO_FIELD = "geo_field";
    public static final String FIELD__GEOREFERENZ__GEO_FIELD = "fk_geom.geo_field";
    public static final String FIELD__HNR = "fk_adresse";
    public static final String FIELD__HNR_GEOM = "umschreibendes_rechteck";                //adresse
    public static final String FIELD__FK_EINSATZ= "fk_einsatz";                //ua_verursacher
    public static final String TABLE_NAME = "ua_einsatz";
    public static final String TABLE_GEOM = "geom";
    public static final String TABLE_VERURSACHER = "ua_verursacher";

    public static final String BUNDLE_NOBEREIT = "UaEinsatzEditor.isOkForSaving().noBereitschaft";
    public static final String BUNDLE_NOMELDER = "UaEinsatzEditor.isOkForSaving().noMelder";
    public static final String BUNDLE_NODATE = "UaEinsatzEditor.isOkForSaving().noDate";
    public static final String BUNDLE_NOGEOM = "UaEinsatzEditor.isOkForSaving().noGeom";
    public static final String BUNDLE_NOBETE = "UaEinsatzEditor.isOkForSaving().noBeteiligteEinsatz";
    public static final String BUNDLE_NOARTEN = "UaEinsatzEditor.isOkForSaving().noArten";
    public static final String BUNDLE_BETKEINER = "UaEinsatzEditor.isOkForSaving().beteiligteKeiner";
    public static final String BUNDLE_BETKEINER_FOLGE = "UaEinsatzEditor.isOkForSaving().beteiligteKeinerFolge";
    public static final String BUNDLE_PANE_PREFIX = "UaEinsatzEditor.isOkForSaving().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX = "UaEinsatzEditor.isOkForSaving().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "UaEinsatzEditor.isOkForSaving().JOptionPane.title";
    public static final String BUNDLE_GEOMQUESTION = "UaEinsatzEditor.btnCreateGeometrieActionPerformed().geom_question";
    public static final String BUNDLE_GEOMWRITE = "UaEinsatzEditor.btnCreateGeometrieActionPerformed().geom_write";
    public static final String BUNDLE_NOGEOMCREATE = "UaEinsatzEditor.btnCreateGeometrieActionPerformed().no_geom_create";
    
    public static final String BUNDLE_PANE_TITLE_PERSIST = "UaEinsatzEditor.editorClose().JOptionPane.title";
    public static final String BUNDLE_PANE_PREFIX_VERURSACHER = "UaEinsatzEditor.editorClose().JOptionPane.errorVerursacher";
    public static final String BUNDLE_PANE_KONTROLLE = "UaEinsatzEditor.editorClose().JOptionPane.kontrolle";
    public static final String BUNDLE_PANE_ADMIN = "UaEinsatzEditor.editorClose().JOptionPane.admin";
    
    public static final String BET_KEINER = "keiner";
    private static final String TITLE_NEW_EINSATZ = "einen neuen Einsatz anlegen...";

    @Override
    public void showMeasureIsLoading() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void showMeasurePanel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

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

    //~ Instance fields --------------------------------------------------------

    private final boolean editor;
    private final Collection<DefaultBindableLabelsPanel> labelsPanels = new ArrayList<>();
    
    private final UaBereitschaftLightweightSearch bereitSearch = new UaBereitschaftLightweightSearch(
            UaBereitschaftLightweightSearch.Subject.NAME,
            BEREIT_TOSTRING_TEMPLATE,
            BEREIT_TOSTRING_FIELDS);
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
    
    private final ActionListener bereitActionListener = new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                final JList pop = ((ComboPopup)cbBereitschaft.getUI().getAccessibleChild(cbBereitschaft, 0)).getList();
                final JTextField txt = (JTextField)cbBereitschaft.getEditor().getEditorComponent();
                final Object selectedValue = pop.getSelectedValue();
                txt.setText((selectedValue != null) ? String.valueOf(selectedValue) : "");
            }
    };
    
    private final DefaultListCellRenderer dlcr = new DefaultListCellRenderer();
    
    @Getter @Setter private CidsBean beanVerursacher;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private DefaultBindableLabelsPanel blpBeteiligte;
    private DefaultBindableLabelsPanel blpBeteiligteFolge;
    private DefaultBindableLabelsPanel blpLeistungen;
    private DefaultBindableLabelsPanel blpSchadstoffarten;
    private DefaultBindableLabelsPanel blpUnfallarten;
    private DefaultBindableLabelsPanel blpVerunreinigungen;
    private JButton btnAddNewVerursacher;
    private JButton btnCreateGeometrie;
    private FastBindableReferenceCombo cbBereitschaft;
    private JComboBox cbGeom;
    FastBindableReferenceCombo cbGewaesser;
    private FastBindableReferenceCombo cbHNr;
    private DefaultBindableReferenceCombo cbMelder;
    FastBindableReferenceCombo cbStrasse;
    private DefaultBindableDateChooser dcDatum;
    private Box.Filler filler3;
    private Box.Filler filler4;
    private Box.Filler filler5;
    private JLabel jLabel2;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JPanel jPanel4;
    private JPanel jPanelAllgemein;
    private JPanel jPanelDetails;
    private JPanel jPanelDokumente;
    private JPanel jPanelFotoAuswahl;
    private JPanel jPanelFotos;
    JTabbedPane jTabbedPane;
    private JXBusyLabel jxLBusy;
    private JLabel lblAktenzeichen;
    private JLabel lblAnleger;
    private JLabel lblBeginn;
    private JLabel lblBeginnTrenner;
    private JLabel lblBemerkung;
    private JLabel lblBereitschaft;
    private JLabel lblBeteiligte;
    private JLabel lblBeteiligteFolge;
    private JLabel lblDatum;
    private JLabel lblEnde;
    private JLabel lblEndeTrenner;
    private JLabel lblFeststellungen;
    private JLabel lblFirma;
    private JLabel lblFolge;
    private JLabel lblGeom;
    private JLabel lblGewaesser;
    private JLabel lblHNrRenderer;
    private JLabel lblHeaderDocument;
    private JLabel lblHeaderListe;
    private JLabel lblHeaderListeDok;
    private JLabel lblHeaderPages;
    private JLabel lblHnr;
    private JLabel lblKarte;
    private JLabel lblKeineFotos;
    private JLabel lblLeistungen;
    private JLabel lblMelder;
    private JLabel lblMenge;
    private JLabel lblMengeEinheit;
    private JLabel lblOrt;
    private JLabel lblSchadstoffarten;
    private JLabel lblSofort;
    private JLabel lblStrasse;
    private JLabel lblUnfallarten;
    private JLabel lblVerunreinigungen;
    private JLabel lblVerursacher;
    private JList lstDok;
    private JList lstFotos;
    private JList lstPages;
    private JPanel panBemerkung;
    private JPanel panContent;
    private JPanel panDaten;
    private JPanel panDetails;
    private JPanel panEinsatz;
    private JPanel panFeststellungen;
    private JPanel panFiller;
    private JPanel panFiller1;
    private JPanel panFirma;
    private JPanel panFolge;
    private JPanel panGeometrie;
    private JPanel panOrt;
    private DefaultPreviewMapPanel panPreviewMap;
    private JPanel panSofort;
    private JPanel pnlBild;
    private JPanel pnlCard1;
    private RoundedPanel pnlDocument;
    private SemiRoundedPanel pnlHeaderDocument;
    private SemiRoundedPanel pnlHeaderListe;
    private SemiRoundedPanel pnlHeaderListeDok;
    private SemiRoundedPanel pnlHeaderPages;
    private RoundedPanel pnlListe;
    private RoundedPanel pnlListeDok;
    private RoundedPanel pnlPages;
    private RasterfariDocumentLoaderPanel rasterfariDocumentLoaderPanel1;
    private RasterfariDocumentLoaderPanel rasterfariDocumentLoaderPanelDok;
    private RoundedPanel rpKarte;
    private JScrollPane scpBemerkung;
    private JScrollPane scpDok;
    private JScrollPane scpFeststellungen;
    private JScrollPane scpFirma;
    private JScrollPane scpFolge;
    private JScrollPane scpFotos;
    private JScrollPane scpOrt;
    private JScrollPane scpPages;
    private JScrollPane scpSofort;
    private SemiRoundedPanel semiRoundedPanel7;
    JSpinner spBMinute;
    JSpinner spBStunde;
    JSpinner spEMinute;
    JSpinner spEStunde;
    JSpinner spMenge;
    private JTextArea taBemerkung;
    private JTextArea taFeststellungen;
    private JTextArea taFirma;
    private JTextArea taFolge;
    private JTextArea taOrt;
    private JTextArea taSofort;
    private JTextField txtAktenzeichen;
    private JTextField txtAnleger;
    private UaVerursacherPanel uaVerursacherPanel;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public UaEinsatzEditor() {
        this(true);
    }

    /**
     * Creates a new UaEinsatzEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public UaEinsatzEditor(final boolean boolEditor) {
        this.editor = boolEditor;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        super.initWithConnectionContext(connectionContext);
        initProperties();
        initComponents();
        for (final DefaultBindableLabelsPanel labelsPanel : Arrays.asList(
                blpBeteiligte, 
                blpBeteiligteFolge, 
                blpLeistungen, 
                blpSchadstoffarten,
                blpUnfallarten,
                blpVerunreinigungen)) {
            labelsPanel.initWithConnectionContext(getConnectionContext());
        }
        cbGewaesser.setRenderer(new GewaesserRenderer(cbGewaesser.getRenderer()));
        cbMelder.setNullable(false);
        setReadOnly();
           
    }
    
    private void showVerursacher(){
        final String aktBenutzer;
        final String confAttrVerursacher;
        
        final Collection<MetaObjectNode> mons;
        try {
            searchVerursacher.setEinsatzId(getCidsBean().getPrimaryKeyValue());
            mons = SessionManager.getProxy().customServerSearch(
                    searchVerursacher,
                    getConnectionContext());
            final List<CidsBean> beansVerursacher = new ArrayList<>();
            if (!mons.isEmpty()) {
                try {
                    confAttrVerursacher = SessionManager.getConnection()
                            .getConfigAttr(SessionManager.getSession().getUser(),
                                    CONF_VERURSACHER,
                                    getConnectionContext());
                    aktBenutzer = getCurrentUser();
                    if ((aktBenutzer.equals(getCidsBean().getProperty(FIELD__ANLEGER).toString())) 
                            || ((confAttrVerursacher != null) && confAttrVerursacher.equals("true"))) {
                        for (final MetaObjectNode mon : mons) {
                            beansVerursacher.add(SessionManager.getProxy().getMetaObject(
                                    mon.getObjectId(),
                                    mon.getClassId(),
                                    "WUNDA_BLAU",
                                    getConnectionContext()).getBean());
                        }
                        setBeanVerursacher(beansVerursacher.get(0));
                        uaVerursacherPanel.setCidsBean(getBeanVerursacher());
                        btnAddNewVerursacher.setVisible(false);    
                    } else {
                        uaVerursacherPanel.setVisible(false);
                        btnAddNewVerursacher.setVisible (false);
                        lblVerursacher.setVisible(false);
                    }
                    } catch (ConnectionException ex) {
                    Exceptions.printStackTrace(ex);
                }
                
            } else {
                uaVerursacherPanel.setVisible(false);
                uaVerursacherPanel.setCidsBean(null);
                if (isEditor()){
                    btnAddNewVerursacher.setVisible(true);  
                } else {
                    btnAddNewVerursacher.setVisible(false);
                }
            }
            
        } catch (ConnectionException ex) {
            Exceptions.printStackTrace(ex);
        }
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
        lblAktenzeichen = new JLabel();
        txtAktenzeichen = new JTextField();
        lblDatum = new JLabel();
        dcDatum = new DefaultBindableDateChooser();
        lblBeginn = new JLabel();
        spBStunde = new JSpinner();
        lblBeginnTrenner = new JLabel();
        spBMinute = new JSpinner();
        filler4 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        lblEnde = new JLabel();
        spEStunde = new JSpinner();
        lblEndeTrenner = new JLabel();
        spEMinute = new JSpinner();
        lblAnleger = new JLabel();
        txtAnleger = new JTextField();
        lblOrt = new JLabel();
        panOrt = new JPanel();
        scpOrt = new JScrollPane();
        taOrt = new JTextArea();
        lblStrasse = new JLabel();
        cbStrasse = new FastBindableReferenceCombo();
        filler3 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        lblHnr = new JLabel();
        if (isEditor()){
            cbHNr = new FastBindableReferenceCombo(
                hnrSearch,
                hnrSearch.getRepresentationPattern(),
                hnrSearch.getRepresentationFields()
            );
        }
        if (!isEditor()){
            lblHNrRenderer = new JLabel();
        }
        lblGeom = new JLabel();
        if (isEditor()){
            cbGeom = new DefaultCismapGeometryComboBoxEditor();
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).setAllowedGeometryTypes(new Class[] { Point.class});
        }
        btnCreateGeometrie = new JButton();
        panFiller = new JPanel();
        lblBereitschaft = new JLabel();
        if (isEditor()){
            cbBereitschaft = new FastBindableReferenceCombo(
                bereitSearch,
                bereitSearch.getRepresentationPattern(),
                bereitSearch.getRepresentationFields() );
        }
        lblMelder = new JLabel();
        cbMelder = new DefaultBindableReferenceCombo(NULLABLE_OPTION, MANAGEABLE_OPTION, SORTING_OPTION_MELDER);
        lblBeteiligte = new JLabel();
        blpBeteiligte = new DefaultBindableLabelsPanel(isEditor(), "Beteiligte:", SORTING_OPTION);
        jPanelDetails = new JPanel();
        panDetails = new JPanel();
        lblUnfallarten = new JLabel();
        blpUnfallarten = new DefaultBindableLabelsPanel(isEditor(), "Unfallarten:", SORTING_OPTION);
        lblSchadstoffarten = new JLabel();
        blpSchadstoffarten = new DefaultBindableLabelsPanel(isEditor(), "Schadstoffarten:", SORTING_OPTION);
        lblGewaesser = new JLabel();
        cbGewaesser = new FastBindableReferenceCombo();
        lblMenge = new JLabel();
        spMenge = new JSpinner();
        lblMengeEinheit = new JLabel();
        lblVerunreinigungen = new JLabel();
        blpVerunreinigungen = new DefaultBindableLabelsPanel(isEditor(), "Verunreinigungen:", SORTING_OPTION);
        lblFeststellungen = new JLabel();
        panFeststellungen = new JPanel();
        scpFeststellungen = new JScrollPane();
        taFeststellungen = new JTextArea();
        lblSofort = new JLabel();
        panSofort = new JPanel();
        scpSofort = new JScrollPane();
        taSofort = new JTextArea();
        lblFolge = new JLabel();
        panFolge = new JPanel();
        scpFolge = new JScrollPane();
        taFolge = new JTextArea();
        lblBeteiligteFolge = new JLabel();
        blpBeteiligteFolge = new DefaultBindableLabelsPanel(isEditor(), "Beteiligte:", SORTING_OPTION);
        lblLeistungen = new JLabel();
        blpLeistungen = new DefaultBindableLabelsPanel(isEditor(), "Leistungen:", SORTING_OPTION);
        lblFirma = new JLabel();
        panFirma = new JPanel();
        scpFirma = new JScrollPane();
        taFirma = new JTextArea();
        lblVerursacher = new JLabel();
        btnAddNewVerursacher = new JButton();
        uaVerursacherPanel = new UaVerursacherPanel();
        lblBemerkung = new JLabel();
        panBemerkung = new JPanel();
        scpBemerkung = new JScrollPane();
        taBemerkung = new JTextArea();
        filler5 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        panFiller1 = new JPanel();
        jPanelDokumente = new JPanel();
        pnlListeDok = new RoundedPanel();
        pnlHeaderListeDok = new SemiRoundedPanel();
        lblHeaderListeDok = new JLabel();
        scpDok = new JScrollPane();
        lstDok = new JList();
        rasterfariDocumentLoaderPanelDok = new RasterfariDocumentLoaderPanel(
            RASTERFARI,
            this,
            getConnectionContext()
        );
        jPanelFotos = new JPanel();
        jPanelFotoAuswahl = new JPanel();
        pnlDocument = new RoundedPanel();
        pnlHeaderDocument = new SemiRoundedPanel();
        lblHeaderDocument = new JLabel();
        pnlBild = new JPanel();
        jPanel1 = new JPanel();
        rasterfariDocumentLoaderPanel1 = new RasterfariDocumentLoaderPanel(
            RASTERFARI,
            this,
            getConnectionContext()
        );
        jPanel2 = new JPanel();
        jxLBusy = new JXBusyLabel(new Dimension(64,64));
        jPanel3 = new JPanel();
        lblKeineFotos = new JLabel();
        jPanel4 = new JPanel();
        jLabel2 = new JLabel();
        pnlPages = new RoundedPanel();
        pnlHeaderPages = new SemiRoundedPanel();
        lblHeaderPages = new JLabel();
        scpPages = new JScrollPane();
        lstPages = rasterfariDocumentLoaderPanel1.getLstPages();
        pnlListe = new RoundedPanel();
        pnlHeaderListe = new SemiRoundedPanel();
        lblHeaderListe = new JLabel();
        scpFotos = new JScrollPane();
        lstFotos = new JList();

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

        lblAktenzeichen.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblAktenzeichen.setText("Aktenzeichen:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblAktenzeichen, gridBagConstraints);

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.aktenzeichen}"), txtAktenzeichen, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtAktenzeichen, gridBagConstraints);

        lblDatum.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblDatum.setText("Datum:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblDatum, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.datum}"), dcDatum, BeanProperty.create("date"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        binding.setConverter(dcDatum.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 11;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(dcDatum, gridBagConstraints);

        lblBeginn.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBeginn.setText("Beginn:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblBeginn, gridBagConstraints);

        spBStunde.setFont(new Font("Dialog", 0, 12)); // NOI18N
        spBStunde.setModel(new SpinnerNumberModel(0, 0, 23, 1));
        spBStunde.setPreferredSize(new Dimension(50, 20));

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.beginn_stunde}"), spBStunde, BeanProperty.create("value"));
        binding.setSourceNullValue(0);
        binding.setSourceUnreadableValue(0);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(spBStunde, gridBagConstraints);

        lblBeginnTrenner.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBeginnTrenner.setText(":");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblBeginnTrenner, gridBagConstraints);

        spBMinute.setFont(new Font("Dialog", 0, 12)); // NOI18N
        spBMinute.setModel(new SpinnerNumberModel(0, 0, 59, 1));
        spBMinute.setPreferredSize(new Dimension(50, 20));

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.beginn_minute}"), spBMinute, BeanProperty.create("value"));
        binding.setSourceNullValue(0);
        binding.setSourceUnreadableValue(0);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(spBMinute, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panDaten.add(filler4, gridBagConstraints);

        lblEnde.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblEnde.setText("Ende:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblEnde, gridBagConstraints);

        spEStunde.setFont(new Font("Dialog", 0, 12)); // NOI18N
        spEStunde.setModel(new SpinnerNumberModel(0, 0, 23, 1));
        spEStunde.setPreferredSize(new Dimension(50, 20));

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.ende_stunde}"), spEStunde, BeanProperty.create("value"));
        binding.setSourceNullValue(0);
        binding.setSourceUnreadableValue(0);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(spEStunde, gridBagConstraints);

        lblEndeTrenner.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblEndeTrenner.setText(":");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblEndeTrenner, gridBagConstraints);

        spEMinute.setFont(new Font("Dialog", 0, 12)); // NOI18N
        spEMinute.setModel(new SpinnerNumberModel(0, 0, 59, 1));
        spEMinute.setPreferredSize(new Dimension(50, 20));

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.ende_minute}"), spEMinute, BeanProperty.create("value"));
        binding.setSourceNullValue(0);
        binding.setSourceUnreadableValue(0);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(spEMinute, gridBagConstraints);

        lblAnleger.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblAnleger.setText("Anleger:");
        lblAnleger.setToolTipText("");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblAnleger, gridBagConstraints);

        txtAnleger.setMinimumSize(new Dimension(10, 24));
        txtAnleger.setPreferredSize(new Dimension(10, 24));

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.anleger}"), txtAnleger, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 11;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtAnleger, gridBagConstraints);

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
        gridBagConstraints.gridwidth = 11;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panOrt, gridBagConstraints);

        lblStrasse.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblStrasse.setText("Straße:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
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
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(cbStrasse, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panDaten.add(filler3, gridBagConstraints);

        lblHnr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblHnr.setText("Hausnummer:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblHnr, gridBagConstraints);

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
            gridBagConstraints.gridx = 11;
            gridBagConstraints.gridy = 5;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            panDaten.add(cbHNr, gridBagConstraints);
        }

        if (!isEditor()){
            lblHNrRenderer.setFont(new Font("Dialog", 0, 12)); // NOI18N

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_adresse.hausnummer}"), lblHNrRenderer, BeanProperty.create("text"));
            binding.setSourceNullValue("----");
            binding.setSourceUnreadableValue("----");
            bindingGroup.addBinding(binding);

        }
        if (!isEditor()){
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 11;
            gridBagConstraints.gridy = 5;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.ipady = 10;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(2, 5, 2, 5);
            panDaten.add(lblHNrRenderer, gridBagConstraints);
        }

        lblGeom.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblGeom.setText("Geometrie:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
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
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 6;
            gridBagConstraints.gridwidth = 8;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            panDaten.add(cbGeom, gridBagConstraints);
        }

        btnCreateGeometrie.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/textblatt.png"))); // NOI18N
        btnCreateGeometrie.setToolTipText("Aktenzeichen automatisch generieren");
        btnCreateGeometrie.setMaximumSize(new Dimension(66, 50));
        btnCreateGeometrie.setMinimumSize(new Dimension(20, 19));
        btnCreateGeometrie.setPreferredSize(new Dimension(33, 24));
        btnCreateGeometrie.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnCreateGeometrieActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 11;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(btnCreateGeometrie, gridBagConstraints);
        btnCreateGeometrie.setVisible(isEditor());

        panFiller.setMinimumSize(new Dimension(20, 0));
        panFiller.setOpaque(false);

        GroupLayout panFillerLayout = new GroupLayout(panFiller);
        panFiller.setLayout(panFillerLayout);
        panFillerLayout.setHorizontalGroup(panFillerLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );
        panFillerLayout.setVerticalGroup(panFillerLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        panDaten.add(panFiller, gridBagConstraints);

        lblBereitschaft.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBereitschaft.setText("Bereitschaft:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblBereitschaft, gridBagConstraints);

        if (isEditor()){
            cbBereitschaft.setMaximumRowCount(20);
            cbBereitschaft.setMinimumSize(new Dimension(100, 19));
            cbBereitschaft.setPreferredSize(new Dimension(100, 19));

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_bereitschaft}"), cbBereitschaft, BeanProperty.create("selectedItem"));
            bindingGroup.addBinding(binding);

        }
        if (isEditor()){
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 7;
            gridBagConstraints.gridwidth = 8;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            panDaten.add(cbBereitschaft, gridBagConstraints);
        }

        lblMelder.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblMelder.setText("Melder:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblMelder, gridBagConstraints);

        cbMelder.setFont(new Font("Dialog", 0, 12)); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_melder}"), cbMelder, BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 11;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(cbMelder, gridBagConstraints);

        lblBeteiligte.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBeteiligte.setText("Beteiligte:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblBeteiligte, gridBagConstraints);

        blpBeteiligte.setOpaque(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.arr_beteiligte_einsatz}"), blpBeteiligte, BeanProperty.create("selectedElements"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 11;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(blpBeteiligte, gridBagConstraints);

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

        lblUnfallarten.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblUnfallarten.setText("Unfallarten:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDetails.add(lblUnfallarten, gridBagConstraints);

        blpUnfallarten.setOpaque(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.arr_unfallarten}"), blpUnfallarten, BeanProperty.create("selectedElements"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDetails.add(blpUnfallarten, gridBagConstraints);

        lblSchadstoffarten.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblSchadstoffarten.setText("Schadstoffarten:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDetails.add(lblSchadstoffarten, gridBagConstraints);

        blpSchadstoffarten.setOpaque(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.arr_schadstoffarten}"), blpSchadstoffarten, BeanProperty.create("selectedElements"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDetails.add(blpSchadstoffarten, gridBagConstraints);

        lblGewaesser.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblGewaesser.setText("Gewässer:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDetails.add(lblGewaesser, gridBagConstraints);

        cbGewaesser.setMaximumRowCount(20);
        cbGewaesser.setModel(new LoadModelCb());
        cbGewaesser.setRepresentationFields(new String[] {"name"});

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_gewaesser}"), cbGewaesser, BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDetails.add(cbGewaesser, gridBagConstraints);

        lblMenge.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblMenge.setText("Menge:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDetails.add(lblMenge, gridBagConstraints);

        spMenge.setFont(new Font("Dialog", 0, 12)); // NOI18N
        spMenge.setModel(new SpinnerNumberModel(0, 0, 1000000, 1));
        spMenge.setPreferredSize(new Dimension(75, 20));

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.menge}"), spMenge, BeanProperty.create("value"));
        binding.setSourceNullValue(0);
        binding.setSourceUnreadableValue(0);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDetails.add(spMenge, gridBagConstraints);

        lblMengeEinheit.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblMengeEinheit.setText("Liter");
        lblMengeEinheit.setToolTipText("");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panDetails.add(lblMengeEinheit, gridBagConstraints);

        lblVerunreinigungen.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblVerunreinigungen.setText("Verunreinigungen:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDetails.add(lblVerunreinigungen, gridBagConstraints);

        blpVerunreinigungen.setOpaque(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.arr_verunreinigungen}"), blpVerunreinigungen, BeanProperty.create("selectedElements"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDetails.add(blpVerunreinigungen, gridBagConstraints);

        lblFeststellungen.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblFeststellungen.setText("Feststellungen:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDetails.add(lblFeststellungen, gridBagConstraints);

        panFeststellungen.setOpaque(false);
        panFeststellungen.setLayout(new GridBagLayout());

        taFeststellungen.setColumns(20);
        taFeststellungen.setLineWrap(true);
        taFeststellungen.setRows(3);
        taFeststellungen.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.feststellungen}"), taFeststellungen, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpFeststellungen.setViewportView(taFeststellungen);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panFeststellungen.add(scpFeststellungen, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDetails.add(panFeststellungen, gridBagConstraints);

        lblSofort.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblSofort.setText("Sofortmaßnahmen:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDetails.add(lblSofort, gridBagConstraints);

        panSofort.setOpaque(false);
        panSofort.setLayout(new GridBagLayout());

        taSofort.setColumns(20);
        taSofort.setLineWrap(true);
        taSofort.setRows(3);
        taSofort.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.sofort}"), taSofort, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpSofort.setViewportView(taSofort);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panSofort.add(scpSofort, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDetails.add(panSofort, gridBagConstraints);

        lblFolge.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblFolge.setText("Folgemaßnahmen:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDetails.add(lblFolge, gridBagConstraints);

        panFolge.setOpaque(false);
        panFolge.setLayout(new GridBagLayout());

        taFolge.setColumns(20);
        taFolge.setLineWrap(true);
        taFolge.setRows(3);
        taFolge.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.folge}"), taFolge, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpFolge.setViewportView(taFolge);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panFolge.add(scpFolge, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDetails.add(panFolge, gridBagConstraints);

        lblBeteiligteFolge.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBeteiligteFolge.setText("Beteiligte-Folge:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDetails.add(lblBeteiligteFolge, gridBagConstraints);

        blpBeteiligteFolge.setOpaque(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.arr_beteiligte_folge}"), blpBeteiligteFolge, BeanProperty.create("selectedElements"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDetails.add(blpBeteiligteFolge, gridBagConstraints);

        lblLeistungen.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblLeistungen.setText("Leistungen:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDetails.add(lblLeistungen, gridBagConstraints);

        blpLeistungen.setOpaque(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.arr_leistungen}"), blpLeistungen, BeanProperty.create("selectedElements"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDetails.add(blpLeistungen, gridBagConstraints);

        lblFirma.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblFirma.setText("Firma:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDetails.add(lblFirma, gridBagConstraints);

        panFirma.setOpaque(false);
        panFirma.setLayout(new GridBagLayout());

        taFirma.setColumns(20);
        taFirma.setLineWrap(true);
        taFirma.setRows(3);
        taFirma.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.firma}"), taFirma, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpFirma.setViewportView(taFirma);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panFirma.add(scpFirma, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDetails.add(panFirma, gridBagConstraints);

        lblVerursacher.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblVerursacher.setText("Verursacher:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDetails.add(lblVerursacher, gridBagConstraints);

        btnAddNewVerursacher.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddNewVerursacher.setMaximumSize(new Dimension(39, 20));
        btnAddNewVerursacher.setMinimumSize(new Dimension(39, 20));
        btnAddNewVerursacher.setPreferredSize(new Dimension(25, 20));
        btnAddNewVerursacher.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnAddNewVerursacherActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panDetails.add(btnAddNewVerursacher, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        panDetails.add(uaVerursacherPanel, gridBagConstraints);

        lblBemerkung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBemerkung.setText("Bemerkung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 19;
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
        gridBagConstraints.gridy = 19;
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
        gridBagConstraints.gridy = 22;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panDetails.add(filler5, gridBagConstraints);

        panFiller1.setMinimumSize(new Dimension(20, 0));
        panFiller1.setOpaque(false);

        GroupLayout panFiller1Layout = new GroupLayout(panFiller1);
        panFiller1.setLayout(panFiller1Layout);
        panFiller1Layout.setHorizontalGroup(panFiller1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );
        panFiller1Layout.setVerticalGroup(panFiller1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        panDetails.add(panFiller1, gridBagConstraints);

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

        pnlListeDok.setMinimumSize(new Dimension(200, 49));

        pnlHeaderListeDok.setBackground(new Color(51, 51, 51));
        pnlHeaderListeDok.setLayout(new FlowLayout());

        lblHeaderListeDok.setForeground(new Color(255, 255, 255));
        lblHeaderListeDok.setText("Dokumentliste");
        pnlHeaderListeDok.add(lblHeaderListeDok);

        pnlListeDok.add(pnlHeaderListeDok, BorderLayout.NORTH);

        scpDok.setPreferredSize(new Dimension(80, 130));

        lstDok.setModel(new DefaultListModel<>());
        lstDok.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstDok.setFixedCellWidth(75);
        scpDok.setViewportView(lstDok);

        pnlListeDok.add(scpDok, BorderLayout.CENTER);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 0.9;
        gridBagConstraints.insets = new Insets(10, 5, 5, 10);
        jPanelDokumente.add(pnlListeDok, gridBagConstraints);
        jPanelDokumente.add(rasterfariDocumentLoaderPanelDok, new GridBagConstraints());

        jTabbedPane.addTab("Dokumente", jPanelDokumente);

        jPanelFotos.setOpaque(false);
        jPanelFotos.setLayout(new GridBagLayout());

        jPanelFotoAuswahl.setOpaque(false);
        jPanelFotoAuswahl.setLayout(new GridBagLayout());

        pnlDocument.setLayout(new GridBagLayout());

        pnlHeaderDocument.setBackground(Color.darkGray);
        pnlHeaderDocument.setLayout(new GridBagLayout());

        lblHeaderDocument.setForeground(Color.white);
        lblHeaderDocument.setText("Foto");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        pnlHeaderDocument.add(lblHeaderDocument, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.1;
        pnlDocument.add(pnlHeaderDocument, gridBagConstraints);

        pnlBild.setOpaque(false);
        pnlBild.setLayout(new CardLayout());

        jPanel1.setLayout(new BorderLayout());
        jPanel1.add(rasterfariDocumentLoaderPanel1, BorderLayout.CENTER);

        pnlBild.add(jPanel1, "DOCUMENT");

        jPanel2.setLayout(new BorderLayout());

        jxLBusy.setHorizontalAlignment(SwingConstants.CENTER);
        jxLBusy.setPreferredSize(new Dimension(64, 64));
        jPanel2.add(jxLBusy, BorderLayout.CENTER);

        pnlBild.add(jPanel2, "BUSY");

        jPanel3.setLayout(new BorderLayout());

        lblKeineFotos.setHorizontalAlignment(SwingConstants.CENTER);
        lblKeineFotos.setText("Für dieses Gebiet sind beim nächtlichen Abgleich keine Fotos vorhanden gewesen.");
        jPanel3.add(lblKeineFotos, BorderLayout.CENTER);

        pnlBild.add(jPanel3, "NO_DOCUMENT");

        jPanel4.setLayout(new BorderLayout());

        jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel2.setText("Das Foto für dieses Gebiet kann nicht geladen werden.");
        jPanel4.add(jLabel2, BorderLayout.CENTER);

        pnlBild.add(jPanel4, "ERROR");

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.9;
        gridBagConstraints.insets = new Insets(0, 0, 8, 0);
        pnlDocument.add(pnlBild, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 0, 0, 5);
        jPanelFotoAuswahl.add(pnlDocument, gridBagConstraints);

        pnlHeaderPages.setBackground(new Color(51, 51, 51));
        pnlHeaderPages.setLayout(new FlowLayout());

        lblHeaderPages.setForeground(new Color(255, 255, 255));
        lblHeaderPages.setText(NbBundle.getMessage(UaEinsatzEditor.class, "VermessungRissEditor.lblHeaderPages.text")); // NOI18N
        pnlHeaderPages.add(lblHeaderPages);

        pnlPages.add(pnlHeaderPages, BorderLayout.NORTH);

        scpPages.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scpPages.setMinimumSize(new Dimension(31, 75));
        scpPages.setOpaque(false);
        scpPages.setPreferredSize(new Dimension(85, 75));

        lstPages.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstPages.setFixedCellWidth(75);
        scpPages.setViewportView(lstPages);

        pnlPages.add(scpPages, BorderLayout.CENTER);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new Insets(5, 0, 0, 5);
        jPanelFotoAuswahl.add(pnlPages, gridBagConstraints);

        pnlListe.setMinimumSize(new Dimension(200, 49));

        pnlHeaderListe.setBackground(new Color(51, 51, 51));
        pnlHeaderListe.setLayout(new FlowLayout());

        lblHeaderListe.setForeground(new Color(255, 255, 255));
        lblHeaderListe.setText("Fotoliste");
        pnlHeaderListe.add(lblHeaderListe);

        pnlListe.add(pnlHeaderListe, BorderLayout.NORTH);

        scpFotos.setPreferredSize(new Dimension(80, 130));

        lstFotos.setModel(new DefaultListModel<>());
        lstFotos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstFotos.setFixedCellWidth(75);
        lstFotos.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent evt) {
                lstFotosValueChanged(evt);
            }
        });
        scpFotos.setViewportView(lstFotos);

        pnlListe.add(scpFotos, BorderLayout.CENTER);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 0.9;
        gridBagConstraints.insets = new Insets(10, 0, 0, 5);
        jPanelFotoAuswahl.add(pnlListe, gridBagConstraints);

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

    private void cbStrasseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbStrasseActionPerformed
        if (isEditor() && (getCidsBean() != null) && (getCidsBean().getProperty(FIELD__STRASSE_SCHLUESSEL) != null)) {
            cbHNr.setSelectedItem(null);
            cbHNr.setEnabled(true);
            refreshHnr();
        }
    }//GEN-LAST:event_cbStrasseActionPerformed

    private void btnCreateGeometrieActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnCreateGeometrieActionPerformed
        if (getCidsBean() != null && getCidsBean().getProperty(FIELD__HNR) != null) {
            CidsBean beanHnr = (CidsBean)getCidsBean().getProperty(FIELD__HNR);
            if (getCidsBean().getProperty(FIELD__GEOM) != null) {
                final Object[] options = { "Ja, Geom überschreiben", "Abbrechen" };
                final int result = JOptionPane.showOptionDialog(StaticSwingTools.getParentFrame(this),
                        NbBundle.getMessage(UaEinsatzEditor.class, BUNDLE_GEOMQUESTION),
                        NbBundle.getMessage(UaEinsatzEditor.class, BUNDLE_GEOMWRITE),
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        options,
                        options[1]);
                if ((result == JOptionPane.CLOSED_OPTION) || (result == 1)) {
                    return;
                } else {
                    CidsBean beanAdresse = (CidsBean)beanHnr.getProperty(FIELD__HNR_GEOM);
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
            } 
        } else {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(UaEinsatzEditor.class, BUNDLE_NOGEOMCREATE),
                NbBundle.getMessage(UaEinsatzEditor.class, BUNDLE_PANE_TITLE),
                JOptionPane.WARNING_MESSAGE);
        } 
    }//GEN-LAST:event_btnCreateGeometrieActionPerformed

    private void lstFotosValueChanged(ListSelectionEvent evt) {//GEN-FIRST:event_lstFotosValueChanged
        
    }//GEN-LAST:event_lstFotosValueChanged

    private void btnAddNewVerursacherActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddNewVerursacherActionPerformed
        try {
            if (getCidsBean() != null) {
                if (beanVerursacher == null){
                    // verursacherBean erzeugen:
                    beanVerursacher  = CidsBean.createNewCidsBeanFromTableName(
                            "WUNDA_BLAU",
                            TABLE_VERURSACHER,
                            getConnectionContext());
                    final CidsBean beanEinsatz = getCidsBean();
                    beanEinsatz.getMetaObject().setStatus(MetaObject.MODIFIED);
                    beanVerursacher.setProperty(FIELD__FK_EINSATZ, beanEinsatz);

                    getCidsBean().setArtificialChangeFlag(true);
                    btnAddNewVerursacher.setVisible(false);
                    uaVerursacherPanel.setVisible(true);
                    uaVerursacherPanel.setCidsBean(beanVerursacher);
                }
            }
        } catch (Exception e) {
            LOG.error("Cannot add new uaVerursacher object", e);
        }
    }//GEN-LAST:event_btnAddNewVerursacherActionPerformed

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
                LOG.info("remove propchange ua_einsatz: " + getCidsBean());
                getCidsBean().removePropertyChangeListener(this);
                cbHNr.removeActionListener(hnrActionListener);
                cbBereitschaft.removeActionListener(bereitActionListener);
            }
            labelsPanels.clear();
            blpBeteiligte.clear();
            bindingGroup.unbind();
            this.cidsBean = cb;
            if (isEditor() && (getCidsBean() != null)) {
                LOG.info("add propchange ua_einsatz: " + getCidsBean());
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
            if (getCidsBean() != null){
                labelsPanels.addAll(Arrays.asList(blpBeteiligte));
                labelsPanels.addAll(Arrays.asList(blpBeteiligteFolge));
                labelsPanels.addAll(Arrays.asList(blpLeistungen));
                labelsPanels.addAll(Arrays.asList(blpSchadstoffarten));
                labelsPanels.addAll(Arrays.asList(blpUnfallarten));
                labelsPanels.addAll(Arrays.asList(blpVerunreinigungen));
            }       
            if (getCidsBean().getMetaObject().getStatus() == MetaObject.NEW) {
                try {
                    getCidsBean().setProperty(
                        FIELD__ANLEGER,
                        getCurrentUser());
 
                } catch (Exception e) {
                    LOG.error("Cannot set user", e);
                }
                searchBereitschaft();
            } else {
                RendererTools.makeReadOnly(cbBereitschaft);
            }
            if (isEditor()){
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
                StaticSwingTools.decorateWithFixedAutoCompleteDecorator(cbBereitschaft);
                {
                    final JList pop = ((ComboPopup)cbBereitschaft.getUI().getAccessibleChild(cbBereitschaft, 0)).getList();
                    final JTextField txt = (JTextField)cbBereitschaft.getEditor().getEditorComponent();
                    cbBereitschaft.addActionListener(bereitActionListener);
                }               
        
            }
            beanHNr = ((CidsBean)getCidsBean().getProperty(FIELD__HNR));
            showVerursacher(); 
        } catch (Exception ex) {
            LOG.error("Bean not set", ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void setReadOnly() {
        if (!(isEditor())) {
            lblGeom.setVisible(isEditor());
            RendererTools.makeReadOnly(dcDatum);
            RendererTools.makeDoubleSpinnerWithoutButtons(spBStunde, 0);
            RendererTools.makeReadOnly(spBStunde);
            RendererTools.makeDoubleSpinnerWithoutButtons(spBMinute, 0);
            RendererTools.makeReadOnly(spBMinute);
            RendererTools.makeDoubleSpinnerWithoutButtons(spEStunde, 0);
            RendererTools.makeReadOnly(spEStunde);
            RendererTools.makeDoubleSpinnerWithoutButtons(spEMinute, 0);
            RendererTools.makeReadOnly(spEMinute);
            RendererTools.makeReadOnly(taOrt);
            RendererTools.makeReadOnly(cbStrasse);
            //lblHNrRenderer.setVisible(true);
            //RendererTools.makeReadOnly(cbHNr);
            RendererTools.makeReadOnly(cbBereitschaft);
            RendererTools.makeReadOnly(cbMelder);
            RendererTools.makeReadOnly(blpBeteiligte);
            RendererTools.makeReadOnly(blpUnfallarten);
            RendererTools.makeReadOnly(blpSchadstoffarten);
            RendererTools.makeReadOnly(cbGewaesser);
            RendererTools.makeDoubleSpinnerWithoutButtons(spMenge, 0);
            RendererTools.makeReadOnly(spMenge);
            RendererTools.makeReadOnly(blpVerunreinigungen);
            RendererTools.makeReadOnly(taFeststellungen);
            RendererTools.makeReadOnly(taSofort);
            RendererTools.makeReadOnly(taFolge);
            RendererTools.makeReadOnly(blpBeteiligteFolge);
            RendererTools.makeReadOnly(blpLeistungen);
            RendererTools.makeReadOnly(taFirma);
            RendererTools.makeReadOnly(taBemerkung);
        } 
        RendererTools.makeReadOnly(txtAktenzeichen);
        RendererTools.makeReadOnly(txtAnleger);
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
    
    private void searchBereitschaft(){
        bereitSearch.setAktiv(true);
        initComboboxBereitschaft();
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
    
    private void initComboboxBereitschaft() {
        new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    cbBereitschaft.refreshModel();
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
            
            RASTERFARI = UaConfProperties.getInstance().getUrlRasterfari();
            THEMA = UaConfProperties.getInstance().getOrdnerThema();
            DOKUMENTE = UaConfProperties.getInstance().getOrdnerDokumente();
            FOTOS = UaConfProperties.getInstance().getOrdnerFotos();
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
            return TITLE_NEW_EINSATZ;
        } else {
            return getCidsBean().toString();
        }
    }

    @Override
    public void dispose() {
        panPreviewMap.dispose();
        uaVerursacherPanel.dispose();

        if (isEditor()) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).dispose();
            cbBereitschaft.removeActionListener(bereitActionListener);
            cbHNr.removeActionListener(hnrActionListener);
            cbHNr.removeAll();
            cbBereitschaft.removeAll();
            if (getCidsBean() != null) {
                LOG.info("remove propchange ua_einsatz: " + getCidsBean());
                getCidsBean().removePropertyChangeListener(this);
            }
        }
        bindingGroup.unbind();if (labelsPanels != null) {
            for (final DefaultBindableLabelsPanel panel : labelsPanels) {
                panel.dispose();
            }
        }
        labelsPanels.clear();
        lstFotos.removeAll();
        lstDok.removeAll();
        rasterfariDocumentLoaderPanel1.dispose();
        rasterfariDocumentLoaderPanelDok.dispose();
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
        boolean noErrorOccured = true;
        final StringBuilder errorMessage = new StringBuilder();
        try {
            noErrorOccured = uaVerursacherPanel.isOkForSaving(beanVerursacher);
        } catch (final Exception ex) {
            noErrorOccured = false;
            LOG.error("Fehler beim Speicher-Check des Verursachers.", ex);
        }
        // Datum muss angegeben werden
            try {
                if (getCidsBean().getProperty(FIELD__DATUM) == null) {
                    LOG.warn("No datum specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(UaEinsatzEditor.class, BUNDLE_NODATE));
                    save = false;
                }
            } catch (final MissingResourceException ex) {
                LOG.warn("Datum not given.", ex);
                save = false;
            }
            
        // Bereitschaft
        try {
            if (getCidsBean().getProperty(FIELD__BEREIT) == null) {
                LOG.warn("No bereitschaft specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(UaEinsatzEditor.class, BUNDLE_NOBEREIT));
                save = false;
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("bereitschaft not given.", ex);
            save = false;
        }

        // Melder
        try {
            if (getCidsBean().getProperty(FIELD__MELDER) == null) {
                LOG.warn("No melder specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(UaEinsatzEditor.class, BUNDLE_NOMELDER));
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
                errorMessage.append(NbBundle.getMessage(UaEinsatzEditor.class, BUNDLE_NOBETE));
                save = false;
            } else {
                Boolean keiner = false;
                for (final CidsBean betBean:collectionBeteiligteE){
                    if ((betBean.getProperty(FIELD__BET_SCHLUESSEL)).toString().equals(BET_KEINER)){
                        keiner = true;
                    }
                }
                if (keiner && collectionBeteiligteE.size() > 1) {
                    LOG.warn("keiner + specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(UaEinsatzEditor.class, BUNDLE_BETKEINER));
                    save = false;
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("beteiligte einsatz not given.", ex);
            save = false;
        }
        
        // Unfallarten
        try {
            final Collection<CidsBean> collectionArten = getCidsBean().getBeanCollectionProperty(
                    FIELD__ARTEN_ARR);
            if ((collectionArten == null) || collectionArten.isEmpty()) {
                LOG.warn("No unfallarten specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(UaEinsatzEditor.class, BUNDLE_NOARTEN));
                save = false;
            } 
        } catch (final MissingResourceException ex) {
            LOG.warn("unfallarten not given.", ex);
            save = false;
        }
        
        // Beteiligte Folge
        try {
            final Collection<CidsBean> collectionBeteiligteF = getCidsBean().getBeanCollectionProperty(
                    FIELD__BETEILIGTE_F_ARR);
            if ((collectionBeteiligteF != null) && !collectionBeteiligteF.isEmpty()) {
                Boolean keiner = false;
                for (final CidsBean betBean:collectionBeteiligteF){
                    if ((betBean.getProperty(FIELD__BET_SCHLUESSEL)).toString().equals(BET_KEINER)){
                        keiner = true;
                    }
                }
                if (keiner && collectionBeteiligteF.size() > 1) {
                    LOG.warn("keiner + specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(UaEinsatzEditor.class, BUNDLE_BETKEINER_FOLGE));
                    save = false;
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("beteiligte folge not given.", ex);
            save = false;
        }
        
        // georeferenz muss gefüllt sein
        try {
            if (getCidsBean().getProperty(FIELD__GEOM) == null) {
                LOG.warn("No geom specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(UaEinsatzEditor.class, BUNDLE_NOGEOM));
                save = false;
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Geom not given.", ex);
            save = false;
        }
        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(UaEinsatzEditor.class, BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(UaEinsatzEditor.class, BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(UaEinsatzEditor.class, BUNDLE_PANE_TITLE),
                JOptionPane.WARNING_MESSAGE);
        } else {
            if (getCidsBean().getMetaObject().getStatus() == MetaObject.NEW) {
                createAktenzeichen();
            }
        }
        return save && noErrorOccured;
    }
    
    @Override
    public void afterSaving(final AfterSavingHook.Event event) {
        try {
            if (AfterSavingHook.Status.SAVE_SUCCESS == event.getStatus()) {
                try {
                    beanVerursacher.setProperty(FIELD__FK_EINSATZ, event.getPersistedBean());
                    try {
                        beanVerursacher = beanVerursacher.persist(getConnectionContext());
                    } catch (final Exception ex) {
                        LOG.error("Fehler bei der Speicher-Vorbereitung des Verursachers.", ex);
                        JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                                NbBundle.getMessage(UaEinsatzEditor.class, BUNDLE_PANE_PREFIX_VERURSACHER)
                                        + NbBundle.getMessage(UaEinsatzEditor.class, BUNDLE_PANE_KONTROLLE)
                                        + NbBundle.getMessage(UaEinsatzEditor.class, BUNDLE_PANE_ADMIN)
                                        + NbBundle.getMessage(UaEinsatzEditor.class, BUNDLE_PANE_SUFFIX),
                                NbBundle.getMessage(UaEinsatzEditor.class, BUNDLE_PANE_TITLE_PERSIST),
                                JOptionPane.ERROR_MESSAGE);
                    }   

                } catch (HeadlessException | MissingResourceException ex) {
                    LOG.warn("problem in persist verursacher.", ex);
                }
            }
        } catch (final Exception ex) {
            LOG.warn("problem in afterSaving.", ex);
        }
    }
    
    public void createAktenzeichen(){
        Integer lfdNummer = 1;
        String aktenzeichen;
        final NextNumberSearch yearSearch = new NextNumberSearch(
                NEXT_TOSTRING_TEMPLATE,
                NEXT_TOSTRING_FIELDS,
                null,
                NEXT_TABLE);
        final Collection<String> conditions = new ArrayList<>();
        final LocalDate ld = dcDatum.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        conditions.add(" split_part(" + FIELD__AZ + ",'-',4) ilike '" + ld.getYear() + "'");
        yearSearch.setWhere(conditions);
        try {
            final List<ArrayList> resultList =
                    (List<ArrayList>) (SessionManager.getProxy().customServerSearch(
                        SessionManager.getSession().getUser(),
                        yearSearch,
                        getConnectionContext()));
            if(!resultList.isEmpty()){
                Integer nummer = 0;
                ArrayList list;
                for (int i=0; i<resultList.size(); i++) {
                    list = resultList.get(i);
                    nummer = Integer.parseInt(list.get(4).toString());
                    if(nummer > lfdNummer){
                        lfdNummer = nummer;
                    }
                }
                lfdNummer = lfdNummer + 1;
            }
            aktenzeichen = "106.26-83-" + lfdNummer + "-" + ld.getYear();
            try {
                this.getCidsBean().setProperty(FIELD__AZ, aktenzeichen);
            } catch (Exception ex) {
                LOG.warn("problem in set az.", ex);
            }
        } catch (ConnectionException ex) {
            LOG.warn("problem in createAktenzeichen.", ex);
        }
        
    }
    
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

    private class GewaesserRenderer implements ListCellRenderer {

        //~ Instance fields ----------------------------------------------------

        private ListCellRenderer originalRenderer;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new GewaesserRenderer object.
         *
         * @param  originalRenderer  DOCUMENT ME!
         */
        public GewaesserRenderer(final ListCellRenderer originalRenderer) {
            this.originalRenderer = originalRenderer;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param   list          DOCUMENT ME!
         * @param   value         DOCUMENT ME!
         * @param   index         DOCUMENT ME!
         * @param   isSelected    DOCUMENT ME!
         * @param   cellHasFocus  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        @Override
        public Component getListCellRendererComponent(final JList list,
                final Object value,
                final int index,
                final boolean isSelected,
                final boolean cellHasFocus) {
            final Component result = originalRenderer.getListCellRendererComponent(
                    list,
                    value,
                    index,
                    isSelected,
                    cellHasFocus);

            if (isSelected) {
                result.setBackground(list.getSelectionBackground());
                result.setForeground(list.getSelectionForeground());
            } else {
                result.setBackground(list.getBackground());
                result.setForeground(list.getForeground());
                if (value instanceof LightweightMetaObject) {
                    final LightweightMetaObject mo = (LightweightMetaObject)value;
                    final CidsBean gewaesser = mo.getBean();
                    if (Boolean.TRUE.equals(gewaesser.getProperty(FIELD__GEW_WV))) {
                        result.setForeground(Color.blue);
                    } else {
                        result.setForeground(Color.black);
                    }
                }
            }
            return result;
        }
    }
}
