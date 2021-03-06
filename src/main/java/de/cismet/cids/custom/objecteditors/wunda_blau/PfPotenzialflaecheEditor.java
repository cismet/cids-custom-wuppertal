/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objecteditors.wunda_blau;

import Sirius.navigator.types.treenode.ObjectTreeNode;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;

import com.vividsolutions.jts.geom.Geometry;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.objectrenderer.utils.alkis.ClientAlkisConf;
import de.cismet.cids.custom.wunda_blau.search.actions.PotenzialflaecheReportServerAction;
import de.cismet.cids.custom.wunda_blau.search.server.AlkisLandparcelGeometryMonSearch;
import de.cismet.cids.custom.wunda_blau.search.server.BplaeneMonSearch;
import de.cismet.cids.custom.wunda_blau.search.server.FnpHauptnutzungenMonSearch;
import de.cismet.cids.custom.wunda_blau.search.server.GeometrySearch;
import de.cismet.cids.custom.wunda_blau.search.server.KstGeometryMonSearch;
import de.cismet.cids.custom.wunda_blau.search.server.StadtraumtypMonSearch;
import de.cismet.cids.custom.wunda_blau.search.server.WohnlagenKategorisierungMonSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultBindableLabelsPanel;
import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;
import de.cismet.cids.editors.SearchLabelsFieldPanel;

import de.cismet.cids.server.search.CidsServerSearch;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.features.StyledFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.gui.FooterComponentProvider;
import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.TitleComponentProvider;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class PfPotenzialflaecheEditor extends javax.swing.JPanel implements CidsBeanRenderer,
    EditorSaveListener,
    FooterComponentProvider,
    TitleComponentProvider,
    ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            PfPotenzialflaecheEditor.class);

    private static DefaultBindableReferenceCombo.Option NULLABLE_OPTION =
        new DefaultBindableReferenceCombo.NullableOption(null, "-");
    private static DefaultBindableReferenceCombo.Option MANAGEABLE_OPTION =
        new DefaultBindableReferenceCombo.ManageableOption("name", "<html><i>[neue Auswahl erzeugen]");

    private static CidsBean lastKampagne = null;

    //~ Instance fields --------------------------------------------------------

    private final PfPotenzialflaecheTitlePanel panTitle = new PfPotenzialflaecheTitlePanel(this);
    private final PfPotenzialflaecheFooterPanel panFooter = new PfPotenzialflaecheFooterPanel(this);
    private final boolean editable;
    private CidsBean cidsBean;
    private ConnectionContext connectionContext;
    private Object currentTreeNode = null;

    private final Collection<SearchLabelsFieldPanel> searchLabelFieldPanels = new ArrayList<>();
    private final Collection<DefaultBindableLabelsPanel> labelsPanels = new ArrayList<>();

    private final Map<String, JComponent> componentMap = new HashMap<>();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFlaeche;
    private javax.swing.JButton btnMassnahmen;
    private javax.swing.JButton btnMenAbort4;
    private javax.swing.JButton btnMenAbort5;
    private javax.swing.JButton btnMenOk4;
    private javax.swing.JButton btnMenOk5;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbAeussereErschluessung;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbAeussereErschluessung1;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbAktivierbarkeit;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbAktivierbarkeit1;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbEntwicklungsaussichten;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbEntwicklungsaussichten1;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbEntwicklungsaussichten2;
    private javax.swing.JComboBox<String> cbGeom;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbHandlungsdruck;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbHandlungsdruck1;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbLagetyp;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbLagetyp2;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbOepnv;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbRevitalisierung1;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbTopografie;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbTopografie1;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbVerfuegbarkeit1;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbVorhandeneBebauung;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbVorhandeneBebauung1;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbVorhandeneBebauung2;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbVorhandeneBebauung3;
    private de.cismet.cids.editors.DefaultBindableDateChooser dateStand;
    private de.cismet.cids.editors.DefaultBindableDateChooser dateStand1;
    private de.cismet.cids.editors.DefaultBindableLabelsPanel defaultBindableCheckboxField1;
    private de.cismet.cids.editors.DefaultBindableLabelsPanel defaultBindableCheckboxField10;
    private de.cismet.cids.editors.DefaultBindableLabelsPanel defaultBindableCheckboxField11;
    private de.cismet.cids.editors.DefaultBindableLabelsPanel defaultBindableCheckboxField3;
    private de.cismet.cids.editors.DefaultBindableLabelsPanel defaultBindableCheckboxField4;
    private de.cismet.cids.editors.DefaultBindableLabelsPanel defaultBindableCheckboxField5;
    private de.cismet.cids.editors.DefaultBindableLabelsPanel defaultBindableCheckboxField6;
    private de.cismet.cids.editors.DefaultBindableLabelsPanel defaultBindableCheckboxField8;
    private de.cismet.cids.editors.DefaultBindableLabelsPanel defaultBindableCheckboxField9;
    private javax.swing.JDialog dlgFlaeche;
    private javax.swing.JDialog dlgMassnahme;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler12;
    private javax.swing.Box.Filler filler15;
    private javax.swing.Box.Filler filler17;
    private javax.swing.Box.Filler filler18;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler22;
    private javax.swing.Box.Filler filler23;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler37;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel lblAessereErschl;
    private javax.swing.JLabel lblAessereErschl1;
    private javax.swing.JLabel lblAktivierbarkeit;
    private javax.swing.JLabel lblAktivierbarkeit1;
    private javax.swing.JLabel lblBeschreibungTitle;
    private javax.swing.JLabel lblBeschreibungTitle3;
    private javax.swing.JLabel lblBeschreibungTitle6;
    private javax.swing.JLabel lblBezeichnung;
    private javax.swing.JLabel lblEntwicklungsausssichten;
    private javax.swing.JLabel lblEntwicklungsausssichten1;
    private javax.swing.JLabel lblEntwicklungsausssichten2;
    private javax.swing.JLabel lblEntwicklungsausssichten3;
    private javax.swing.JLabel lblEntwicklungsausssichten4;
    private javax.swing.JLabel lblEntwicklungsausssichten5;
    private javax.swing.JLabel lblFlaechengroesse;
    private javax.swing.JLabel lblFlaechengroesseWert;
    private javax.swing.JLabel lblFlaechennutzung;
    private javax.swing.JLabel lblFlaechennutzung1;
    private javax.swing.JLabel lblFlaechennutzung2;
    private javax.swing.JLabel lblFlaechennutzung3;
    private javax.swing.JLabel lblGeometrie5;
    private javax.swing.JLabel lblHandlungsdruck;
    private javax.swing.JLabel lblHandlungsdruck1;
    private javax.swing.JLabel lblLageTitle2;
    private javax.swing.JLabel lblLageTitle3;
    private javax.swing.JLabel lblLageTitle6;
    private javax.swing.JLabel lblLageTitle7;
    private javax.swing.JLabel lblLagetyp;
    private javax.swing.JLabel lblLagetyp1;
    private javax.swing.JLabel lblLagetyp2;
    private javax.swing.JLabel lblMessstellenausbauTitle;
    private javax.swing.JLabel lblNummer;
    private javax.swing.JLabel lblNutzungsaufgabe;
    private javax.swing.JLabel lblOepnv;
    private javax.swing.JLabel lblQuartiere;
    private javax.swing.JLabel lblQuartiere1;
    private javax.swing.JLabel lblQuelle;
    private javax.swing.JLabel lblRevitalisierung1;
    private javax.swing.JLabel lblStadtbezirk;
    private javax.swing.JLabel lblStadtbezirk1;
    private javax.swing.JLabel lblStadtbezirk2;
    private javax.swing.JLabel lblStadtbezirk3;
    private javax.swing.JLabel lblStadtbezirk4;
    private javax.swing.JLabel lblStadtbezirk5;
    private javax.swing.JLabel lblStadtbezirk6;
    private javax.swing.JLabel lblStand;
    private javax.swing.JLabel lblTopografie;
    private javax.swing.JLabel lblTopografie1;
    private javax.swing.JLabel lblVerfuegbarkeit1;
    private javax.swing.JLabel lblVorhandeneBebauung;
    private javax.swing.JLabel lblVorhandeneBebauung1;
    private javax.swing.JLabel lblVorhandeneBebauung2;
    private de.cismet.cismap.commons.gui.MappingComponent mappingComponent1;
    private de.cismet.cids.custom.objecteditors.utils.CidsSearchResultsList<MetaObjectNode> monSearchResultsList1;
    private de.cismet.tools.gui.RoundedPanel panAllgemein;
    private de.cismet.tools.gui.RoundedPanel panAllgemein3;
    private de.cismet.tools.gui.RoundedPanel panAllgemein6;
    private javax.swing.JPanel panArtControls3;
    private javax.swing.JPanel panArtControls4;
    private de.cismet.tools.gui.SemiRoundedPanel panBeschreibungTitle;
    private de.cismet.tools.gui.SemiRoundedPanel panBeschreibungTitle3;
    private de.cismet.tools.gui.SemiRoundedPanel panBeschreibungTitle6;
    private de.cismet.tools.gui.RoundedPanel panBewertung1;
    private de.cismet.tools.gui.RoundedPanel panBewertung2;
    private javax.swing.JPanel panDetail;
    private de.cismet.tools.gui.RoundedPanel panErweitert;
    private javax.swing.JPanel panFlaeche;
    private javax.swing.JPanel panFlaeche1;
    private javax.swing.JPanel panLageBody2;
    private javax.swing.JPanel panLageBody3;
    private javax.swing.JPanel panLageBody5;
    private javax.swing.JPanel panLageBody6;
    private de.cismet.tools.gui.SemiRoundedPanel panLageTitle2;
    private de.cismet.tools.gui.SemiRoundedPanel panLageTitle3;
    private de.cismet.tools.gui.SemiRoundedPanel panLageTitle5;
    private de.cismet.tools.gui.SemiRoundedPanel panLageTitle6;
    private javax.swing.JPanel panMain;
    private javax.swing.JPanel panMenButtons4;
    private javax.swing.JPanel panMenButtons5;
    private javax.swing.JPanel panMessstellenausbauBody;
    private de.cismet.tools.gui.SemiRoundedPanel panMessstellenausbauTitle;
    private de.cismet.tools.gui.RoundedPanel panPlanungsrecht;
    private de.cismet.tools.gui.RoundedPanel panStandortdaten;
    private de.cismet.cids.editors.SearchLabelsFieldPanel searchLabelsFieldPanel1;
    private de.cismet.cids.editors.SearchLabelsFieldPanel searchLabelsFieldPanel2;
    private de.cismet.cids.editors.SearchLabelsFieldPanel searchLabelsFieldPanel3;
    private de.cismet.cids.editors.SearchLabelsFieldPanel searchLabelsFieldPanel4;
    private de.cismet.cids.editors.SearchLabelsFieldPanel searchLabelsFieldPanel5;
    private de.cismet.cids.editors.SearchLabelsFieldPanel searchLabelsFieldPanel6;
    private de.cismet.cids.editors.converters.SqlDateToUtilDateConverter sqlDateToUtilDateConverter;
    private javax.swing.JTextArea taFlaeche;
    private javax.swing.JTextArea taFlaecheDialog;
    private javax.swing.JTextArea taMassnahmeDialog;
    private javax.swing.JTextArea taNotwendigeMassnahme;
    private javax.swing.JTextField txtBezeichnung;
    private javax.swing.JTextField txtJahrNutzungsaufgabe;
    private javax.swing.JTextField txtNummer;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new PfPotenzialflaecheEditor object.
     */
    public PfPotenzialflaecheEditor() {
        this(true);
    }

    /**
     * Creates a new PfPotenzialflaecheEditor object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public PfPotenzialflaecheEditor(final boolean editable) {
        this.editable = editable;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the lastKampagne
     */
    public static CidsBean getLastKampagne() {
        return lastKampagne;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isEditable() {
        return editable;
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
            "pf_potenzialflaeche",
            10,
            800,
            600);
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;

        searchLabelFieldPanels.clear();
        labelsPanels.clear();

        initComponents();
        initComponentMap();

        RendererTools.makeUneditable(monSearchResultsList1, true);

        searchLabelFieldPanels.addAll(Arrays.asList(
                searchLabelsFieldPanel1,
                searchLabelsFieldPanel2,
                searchLabelsFieldPanel3,
                searchLabelsFieldPanel4,
                searchLabelsFieldPanel5,
                searchLabelsFieldPanel6));

        labelsPanels.addAll(Arrays.asList(
                defaultBindableCheckboxField1,
                defaultBindableCheckboxField3,
                defaultBindableCheckboxField4,
                defaultBindableCheckboxField5,
                defaultBindableCheckboxField6,
                defaultBindableCheckboxField8,
                defaultBindableCheckboxField9,
                defaultBindableCheckboxField10,
                defaultBindableCheckboxField11));

        for (final SearchLabelsFieldPanel searchLabelFieldPanel : searchLabelFieldPanels) {
            searchLabelFieldPanel.initWithConnectionContext(connectionContext);
        }

        for (final DefaultBindableLabelsPanel labelsPanel : labelsPanels) {
            labelsPanel.initWithConnectionContext(connectionContext);
        }

        monSearchResultsList1.initWithConnectionContext(connectionContext);

        panTitle.init();
        panFooter.init();

        dlgFlaeche.addComponentListener(new ComponentAdapter() {

                @Override
                public void componentResized(final ComponentEvent e) {
                    dlgFlaeche.doLayout();
                    taFlaecheDialog.setSize(dlgFlaeche.getWidth() - 5, taFlaecheDialog.getHeight());
                }
            });

        dlgMassnahme.addComponentListener(new ComponentAdapter() {

                @Override
                public void componentResized(final ComponentEvent e) {
                    dlgMassnahme.doLayout();
                    taMassnahmeDialog.setSize(
                        dlgMassnahme.getWidth()
                                - 5,
                        taMassnahmeDialog.getHeight());
                }
            });

        if (!isEditable()) {
            RendererTools.makeReadOnly(bindingGroup, "cidsBean");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        dlgFlaeche = new javax.swing.JDialog();
        panFlaeche = new javax.swing.JPanel();
        panMenButtons4 = new javax.swing.JPanel();
        btnMenAbort4 = new javax.swing.JButton();
        btnMenOk4 = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        taFlaecheDialog = new javax.swing.JTextArea();
        dlgMassnahme = new javax.swing.JDialog();
        panFlaeche1 = new javax.swing.JPanel();
        panMenButtons5 = new javax.swing.JPanel();
        btnMenAbort5 = new javax.swing.JButton();
        btnMenOk5 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        taMassnahmeDialog = new javax.swing.JTextArea();
        sqlDateToUtilDateConverter = new de.cismet.cids.editors.converters.SqlDateToUtilDateConverter();
        panMain = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        panAllgemein = new de.cismet.tools.gui.RoundedPanel();
        panBeschreibungTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        lblBeschreibungTitle = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        lblBezeichnung = new javax.swing.JLabel();
        txtBezeichnung = new javax.swing.JTextField();
        lblNummer = new javax.swing.JLabel();
        txtNummer = new javax.swing.JTextField();
        mappingComponent1 = new de.cismet.cismap.commons.gui.MappingComponent();
        jPanel7 = new javax.swing.JPanel();
        lblGeometrie5 = new javax.swing.JLabel();
        cbGeom = (!editable) ? new JComboBox() : new DefaultCismapGeometryComboBoxEditor();
        lblFlaechengroesse = new javax.swing.JLabel();
        lblFlaechengroesseWert = new javax.swing.JLabel();
        lblStadtbezirk = new javax.swing.JLabel();
        searchLabelsFieldPanel2 = new de.cismet.cids.editors.SearchLabelsFieldPanel(new KstGeometryMonSearch(
                    KstGeometryMonSearch.SearchFor.BEZIRK));
        lblQuartiere = new javax.swing.JLabel();
        searchLabelsFieldPanel1 = new de.cismet.cids.editors.SearchLabelsFieldPanel(new KstGeometryMonSearch(
                    KstGeometryMonSearch.SearchFor.QUARTIER));
        lblQuartiere1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        monSearchResultsList1 = new de.cismet.cids.custom.objecteditors.utils.CidsSearchResultsList<>(
                new AlkisLandparcelGeometryMonSearch());
        lblStadtbezirk5 = new javax.swing.JLabel();
        defaultBindableCheckboxField3 = new de.cismet.cids.editors.DefaultBindableLabelsPanel(
                isEditable(),
                "Eigentümer:");
        jPanel10 = new javax.swing.JPanel();
        panStandortdaten = new de.cismet.tools.gui.RoundedPanel();
        panLageTitle2 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblLageTitle2 = new javax.swing.JLabel();
        panLageBody2 = new javax.swing.JPanel();
        lblLagetyp = new javax.swing.JLabel();
        cbLagetyp = new de.cismet.cids.editors.DefaultBindableReferenceCombo(NULLABLE_OPTION, MANAGEABLE_OPTION);
        lblLagetyp1 = new javax.swing.JLabel();
        defaultBindableCheckboxField11 = new de.cismet.cids.editors.DefaultBindableLabelsPanel(
                isEditable(),
                "Nähe zu:");
        lblLagetyp2 = new javax.swing.JLabel();
        cbLagetyp2 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(NULLABLE_OPTION, MANAGEABLE_OPTION);
        lblOepnv = new javax.swing.JLabel();
        cbOepnv = new de.cismet.cids.editors.DefaultBindableReferenceCombo(NULLABLE_OPTION, MANAGEABLE_OPTION);
        lblTopografie = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        cbTopografie = new de.cismet.cids.editors.DefaultBindableReferenceCombo(NULLABLE_OPTION, MANAGEABLE_OPTION);
        lblTopografie1 = new javax.swing.JLabel();
        cbTopografie1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(NULLABLE_OPTION);
        filler17 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0));
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(200, 0),
                new java.awt.Dimension(200, 0),
                new java.awt.Dimension(200, 0));
        panPlanungsrecht = new de.cismet.tools.gui.RoundedPanel();
        panMessstellenausbauTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        lblMessstellenausbauTitle = new javax.swing.JLabel();
        panMessstellenausbauBody = new javax.swing.JPanel();
        lblFlaechennutzung1 = new javax.swing.JLabel();
        defaultBindableCheckboxField9 = new de.cismet.cids.editors.DefaultBindableLabelsPanel(
                isEditable(),
                "Regionalplan:");
        lblFlaechennutzung = new javax.swing.JLabel();
        searchLabelsFieldPanel6 = new de.cismet.cids.editors.SearchLabelsFieldPanel(new FnpHauptnutzungenMonSearch());
        lblFlaechennutzung2 = new javax.swing.JLabel();
        searchLabelsFieldPanel4 = new de.cismet.cids.editors.SearchLabelsFieldPanel(new BplaeneMonSearch(), true);
        lblFlaechennutzung3 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jLabel1 = new javax.swing.JLabel();
        dateStand1 = new de.cismet.cids.editors.DefaultBindableDateChooser();
        lblVorhandeneBebauung1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        cbVorhandeneBebauung1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                MANAGEABLE_OPTION);
        cbVorhandeneBebauung2 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                MANAGEABLE_OPTION);
        filler37 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0));
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(200, 0),
                new java.awt.Dimension(200, 0),
                new java.awt.Dimension(200, 0));
        panErweitert = new de.cismet.tools.gui.RoundedPanel();
        panLageTitle3 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblLageTitle3 = new javax.swing.JLabel();
        panLageBody3 = new javax.swing.JPanel();
        lblStadtbezirk4 = new javax.swing.JLabel();
        defaultBindableCheckboxField6 = new de.cismet.cids.editors.DefaultBindableLabelsPanel(
                isEditable(),
                "Umgebungsnutzung:");
        lblStadtbezirk6 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        defaultBindableCheckboxField1 = new de.cismet.cids.editors.DefaultBindableLabelsPanel(
                isEditable(),
                "Brachflächenkategorie:");
        lblNutzungsaufgabe = new javax.swing.JLabel();
        txtJahrNutzungsaufgabe = new javax.swing.JTextField();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(75, 0),
                new java.awt.Dimension(75, 0),
                new java.awt.Dimension(75, 0));
        lblStadtbezirk3 = new javax.swing.JLabel();
        defaultBindableCheckboxField5 = new de.cismet.cids.editors.DefaultBindableLabelsPanel(
                isEditable(),
                "Bisherige Nutzung:");
        lblVorhandeneBebauung = new javax.swing.JLabel();
        cbVorhandeneBebauung = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                MANAGEABLE_OPTION);
        lblVorhandeneBebauung2 = new javax.swing.JLabel();
        cbVorhandeneBebauung3 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                MANAGEABLE_OPTION);
        lblAessereErschl = new javax.swing.JLabel();
        cbAeussereErschluessung = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                MANAGEABLE_OPTION);
        lblStadtbezirk1 = new javax.swing.JLabel();
        searchLabelsFieldPanel5 = new de.cismet.cids.editors.SearchLabelsFieldPanel(new StadtraumtypMonSearch());
        lblStadtbezirk2 = new javax.swing.JLabel();
        searchLabelsFieldPanel3 = new de.cismet.cids.editors.SearchLabelsFieldPanel(
                new WohnlagenKategorisierungMonSearch());
        lblAessereErschl1 = new javax.swing.JLabel();
        cbAeussereErschluessung1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                MANAGEABLE_OPTION);
        filler18 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0));
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(200, 0),
                new java.awt.Dimension(200, 0),
                new java.awt.Dimension(200, 0));
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(450, 0),
                new java.awt.Dimension(450, 0),
                new java.awt.Dimension(450, 0));
        filler15 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        panDetail = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        panAllgemein3 = new de.cismet.tools.gui.RoundedPanel();
        panBeschreibungTitle3 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblBeschreibungTitle3 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane15 = new javax.swing.JScrollPane();
        taFlaeche = new javax.swing.JTextArea();
        panArtControls3 = new javax.swing.JPanel();
        btnFlaeche = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        lblQuelle = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        lblStand = new javax.swing.JLabel();
        dateStand = new de.cismet.cids.editors.DefaultBindableDateChooser();
        panAllgemein6 = new de.cismet.tools.gui.RoundedPanel();
        panBeschreibungTitle6 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblBeschreibungTitle6 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        taNotwendigeMassnahme = new javax.swing.JTextArea();
        panArtControls4 = new javax.swing.JPanel();
        btnMassnahmen = new javax.swing.JButton();
        panBewertung1 = new de.cismet.tools.gui.RoundedPanel();
        panLageTitle5 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblLageTitle6 = new javax.swing.JLabel();
        panLageBody5 = new javax.swing.JPanel();
        lblEntwicklungsausssichten1 = new javax.swing.JLabel();
        cbEntwicklungsaussichten1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                MANAGEABLE_OPTION);
        lblEntwicklungsausssichten2 = new javax.swing.JLabel();
        cbEntwicklungsaussichten2 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                MANAGEABLE_OPTION);
        lblEntwicklungsausssichten3 = new javax.swing.JLabel();
        defaultBindableCheckboxField4 = new de.cismet.cids.editors.DefaultBindableLabelsPanel(
                isEditable(),
                "Restriktionen/Hemmnisse:");
        lblEntwicklungsausssichten5 = new javax.swing.JLabel();
        defaultBindableCheckboxField10 = new de.cismet.cids.editors.DefaultBindableLabelsPanel(
                isEditable(),
                "Empfohlene Art der Wohnnutzung:");
        lblAktivierbarkeit1 = new javax.swing.JLabel();
        cbAktivierbarkeit1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                MANAGEABLE_OPTION);
        jFormattedTextField1 = new javax.swing.JFormattedTextField();
        lblEntwicklungsausssichten = new javax.swing.JLabel();
        cbEntwicklungsaussichten = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                MANAGEABLE_OPTION);
        filler22 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0));
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(200, 0),
                new java.awt.Dimension(200, 0),
                new java.awt.Dimension(200, 0));
        panBewertung2 = new de.cismet.tools.gui.RoundedPanel();
        panLageTitle6 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblLageTitle7 = new javax.swing.JLabel();
        panLageBody6 = new javax.swing.JPanel();
        lblVerfuegbarkeit1 = new javax.swing.JLabel();
        cbVerfuegbarkeit1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                MANAGEABLE_OPTION);
        lblAktivierbarkeit = new javax.swing.JLabel();
        cbAktivierbarkeit = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                MANAGEABLE_OPTION);
        lblHandlungsdruck1 = new javax.swing.JLabel();
        cbHandlungsdruck1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                MANAGEABLE_OPTION);
        lblEntwicklungsausssichten4 = new javax.swing.JLabel();
        defaultBindableCheckboxField8 = new de.cismet.cids.editors.DefaultBindableLabelsPanel(
                isEditable(),
                "Empfohlene Nutzung:");
        lblRevitalisierung1 = new javax.swing.JLabel();
        cbRevitalisierung1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                MANAGEABLE_OPTION);
        lblHandlungsdruck = new javax.swing.JLabel();
        cbHandlungsdruck = new de.cismet.cids.editors.DefaultBindableReferenceCombo(NULLABLE_OPTION, MANAGEABLE_OPTION);
        filler23 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0));
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(200, 0),
                new java.awt.Dimension(200, 0),
                new java.awt.Dimension(200, 0));
        filler12 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0));

        dlgFlaeche.setTitle(org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.dlgFlaeche.title",
                new Object[] {})); // NOI18N
        dlgFlaeche.setModal(true);
        dlgFlaeche.setSize(new java.awt.Dimension(600, 400));

        panFlaeche.setMaximumSize(new java.awt.Dimension(300, 120));
        panFlaeche.setMinimumSize(new java.awt.Dimension(300, 120));
        panFlaeche.setPreferredSize(new java.awt.Dimension(300, 120));
        panFlaeche.setLayout(new java.awt.GridBagLayout());

        panMenButtons4.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            btnMenAbort4,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.btnMenAbort4.text",
                new Object[] {})); // NOI18N
        btnMenAbort4.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnMenAbort4ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMenButtons4.add(btnMenAbort4, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            btnMenOk4,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.btnMenOk4.text",
                new Object[] {})); // NOI18N
        btnMenOk4.setMaximumSize(new java.awt.Dimension(85, 23));
        btnMenOk4.setMinimumSize(new java.awt.Dimension(85, 23));
        btnMenOk4.setPreferredSize(new java.awt.Dimension(85, 23));
        btnMenOk4.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnMenOk4ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMenButtons4.add(btnMenOk4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panFlaeche.add(panMenButtons4, gridBagConstraints);

        taFlaecheDialog.setColumns(20);
        taFlaecheDialog.setLineWrap(true);
        taFlaecheDialog.setRows(5);
        taFlaecheDialog.setWrapStyleWord(true);
        jScrollPane8.setViewportView(taFlaecheDialog);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panFlaeche.add(jScrollPane8, gridBagConstraints);

        dlgFlaeche.getContentPane().add(panFlaeche, java.awt.BorderLayout.CENTER);

        dlgMassnahme.setTitle(org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.dlgMassnahme.title",
                new Object[] {})); // NOI18N
        dlgMassnahme.setModal(true);
        dlgMassnahme.setSize(new java.awt.Dimension(600, 400));

        panFlaeche1.setMaximumSize(new java.awt.Dimension(300, 120));
        panFlaeche1.setMinimumSize(new java.awt.Dimension(300, 120));
        panFlaeche1.setPreferredSize(new java.awt.Dimension(300, 120));
        panFlaeche1.setLayout(new java.awt.GridBagLayout());

        panMenButtons5.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            btnMenAbort5,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.btnMenAbort5.text",
                new Object[] {})); // NOI18N
        btnMenAbort5.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnMenAbort5ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMenButtons5.add(btnMenAbort5, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            btnMenOk5,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.btnMenOk5.text",
                new Object[] {})); // NOI18N
        btnMenOk5.setMaximumSize(new java.awt.Dimension(85, 23));
        btnMenOk5.setMinimumSize(new java.awt.Dimension(85, 23));
        btnMenOk5.setPreferredSize(new java.awt.Dimension(85, 23));
        btnMenOk5.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnMenOk5ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMenButtons5.add(btnMenOk5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panFlaeche1.add(panMenButtons5, gridBagConstraints);

        taMassnahmeDialog.setColumns(20);
        taMassnahmeDialog.setLineWrap(true);
        taMassnahmeDialog.setRows(5);
        taMassnahmeDialog.setWrapStyleWord(true);
        jScrollPane2.setViewportView(taMassnahmeDialog);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panFlaeche1.add(jScrollPane2, gridBagConstraints);

        dlgMassnahme.getContentPane().add(panFlaeche1, java.awt.BorderLayout.CENTER);

        setOpaque(false);
        setLayout(new java.awt.CardLayout());

        panMain.setOpaque(false);
        panMain.setLayout(new java.awt.GridBagLayout());

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel8.setOpaque(false);
        jPanel8.setLayout(new java.awt.GridBagLayout());

        panAllgemein.setLayout(new java.awt.GridBagLayout());

        panBeschreibungTitle.setBackground(java.awt.Color.darkGray);
        panBeschreibungTitle.setLayout(new java.awt.GridBagLayout());

        lblBeschreibungTitle.setFont(lblBeschreibungTitle.getFont());
        lblBeschreibungTitle.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblBeschreibungTitle,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblBeschreibungTitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBeschreibungTitle.add(lblBeschreibungTitle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panAllgemein.add(panBeschreibungTitle, gridBagConstraints);

        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblBezeichnung,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblBezeichnung.text")); // NOI18N
        lblBezeichnung.setName(PotenzialflaecheReportServerAction.Property.BEZEICHNUNG.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel3.add(lblBezeichnung, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bezeichnung}"),
                txtBezeichnung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtBezeichnung.addFocusListener(new java.awt.event.FocusAdapter() {

                @Override
                public void focusLost(final java.awt.event.FocusEvent evt) {
                    txtBezeichnungFocusLost(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 3.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel3.add(txtBezeichnung, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblNummer,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblNummer.text")); // NOI18N
        lblNummer.setName(PotenzialflaecheReportServerAction.Property.NUMMER.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 5, 5);
        jPanel3.add(lblNummer, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.nummer}"),
                txtNummer,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel3.add(txtNummer, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 10);
        panAllgemein.add(jPanel3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panAllgemein.add(mappingComponent1, gridBagConstraints);

        jPanel7.setOpaque(false);
        jPanel7.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblGeometrie5,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblGeometrie5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblGeometrie5, gridBagConstraints);
        lblGeometrie5.setVisible(isEditable());

        if (editable) {
            binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                    org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                    this,
                    org.jdesktop.beansbinding.ELProperty.create("${cidsBean.geometrie}"),
                    cbGeom,
                    org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeom).getConverter());
            bindingGroup.addBinding(binding);

            cbGeom.addFocusListener(new java.awt.event.FocusAdapter() {

                    @Override
                    public void focusLost(final java.awt.event.FocusEvent evt) {
                        cbGeomFocusLost(evt);
                    }
                });
            cbGeom.addActionListener(new java.awt.event.ActionListener() {

                    @Override
                    public void actionPerformed(final java.awt.event.ActionEvent evt) {
                        cbGeomActionPerformed(evt);
                    }
                });
        }
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(cbGeom, gridBagConstraints);
        cbGeom.setVisible(isEditable());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblFlaechengroesse,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblFlaechengroesse.text")); // NOI18N
        lblFlaechengroesse.setName(PotenzialflaecheReportServerAction.Property.GROESSE.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblFlaechengroesse, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblFlaechengroesseWert,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblFlaechengroesseWert.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(lblFlaechengroesseWert, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblStadtbezirk,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblStadtbezirk.text")); // NOI18N
        lblStadtbezirk.setName(PotenzialflaecheReportServerAction.Property.STADTBEZIRK.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblStadtbezirk, gridBagConstraints);

        searchLabelsFieldPanel2.setOpaque(false);
        searchLabelsFieldPanel2.setLayout(new java.awt.FlowLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(searchLabelsFieldPanel2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblQuartiere,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblQuartiere.text")); // NOI18N
        lblQuartiere.setName(PotenzialflaecheReportServerAction.Property.QUARTIER.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblQuartiere, gridBagConstraints);

        searchLabelsFieldPanel1.setOpaque(false);
        searchLabelsFieldPanel1.setLayout(new java.awt.FlowLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(searchLabelsFieldPanel1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblQuartiere1,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblQuartiere1.text")); // NOI18N
        lblQuartiere1.setName(PotenzialflaecheReportServerAction.Property.FLURSTUECKE.toString());
        lblQuartiere1.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblQuartiere1MouseClicked(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblQuartiere1, gridBagConstraints);

        monSearchResultsList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        monSearchResultsList1.setVisibleRowCount(4);
        jScrollPane1.setViewportView(monSearchResultsList1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(jScrollPane1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblStadtbezirk5,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblStadtbezirk5.text")); // NOI18N
        lblStadtbezirk5.setName(PotenzialflaecheReportServerAction.Property.EIGENTUEMER.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblStadtbezirk5, gridBagConstraints);

        defaultBindableCheckboxField3.setOpaque(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.arr_eigentuemer}"),
                defaultBindableCheckboxField3,
                org.jdesktop.beansbinding.BeanProperty.create("selectedElements"));
        bindingGroup.addBinding(binding);

        defaultBindableCheckboxField3.setLayout(new java.awt.FlowLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(defaultBindableCheckboxField3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 10);
        panAllgemein.add(jPanel7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(panAllgemein, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jPanel8, gridBagConstraints);

        jPanel10.setOpaque(false);
        jPanel10.setLayout(new java.awt.GridBagLayout());

        panStandortdaten.setLayout(new java.awt.GridBagLayout());

        panLageTitle2.setBackground(java.awt.Color.darkGray);
        panLageTitle2.setLayout(new java.awt.GridBagLayout());

        lblLageTitle2.setFont(lblLageTitle2.getFont());
        lblLageTitle2.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblLageTitle2,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblLageTitle2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panLageTitle2.add(lblLageTitle2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panStandortdaten.add(panLageTitle2, gridBagConstraints);

        panLageBody2.setOpaque(false);
        panLageBody2.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblLagetyp,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblLagetyp.text")); // NOI18N
        lblLagetyp.setName(PotenzialflaecheReportServerAction.Property.LAGEBEWERTUNG_VERKEHR.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody2.add(lblLagetyp, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_lagebewertung_verkehr}"),
                cbLagetyp,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody2.add(cbLagetyp, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblLagetyp1,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblLagetyp1.text")); // NOI18N
        lblLagetyp1.setName(PotenzialflaecheReportServerAction.Property.NAEHE_ZU.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody2.add(lblLagetyp1, gridBagConstraints);

        defaultBindableCheckboxField11.setOpaque(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.arr_naehen_zu}"),
                defaultBindableCheckboxField11,
                org.jdesktop.beansbinding.BeanProperty.create("selectedElements"));
        bindingGroup.addBinding(binding);

        defaultBindableCheckboxField11.setLayout(new java.awt.FlowLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody2.add(defaultBindableCheckboxField11, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblLagetyp2,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblLagetyp2.text")); // NOI18N
        lblLagetyp2.setName(PotenzialflaecheReportServerAction.Property.SIEDLUNGSRAEUMLICHE_LAGE.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody2.add(lblLagetyp2, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_siedlungsraeumliche_lage}"),
                cbLagetyp2,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody2.add(cbLagetyp2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblOepnv,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblOepnv.text")); // NOI18N
        lblOepnv.setName(PotenzialflaecheReportServerAction.Property.OEPNV_ANBINDUNG.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody2.add(lblOepnv, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_oepnv}"),
                cbOepnv,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody2.add(cbOepnv, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblTopografie,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblTopografie.text")); // NOI18N
        lblTopografie.setName(PotenzialflaecheReportServerAction.Property.TOPOGRAFIE.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody2.add(lblTopografie, gridBagConstraints);

        jPanel4.setOpaque(false);
        jPanel4.setLayout(new java.awt.GridBagLayout());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.topografie}"),
                cbTopografie,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel4.add(cbTopografie, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblTopografie1,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblTopografie1.text")); // NOI18N
        lblTopografie1.setName(PotenzialflaecheReportServerAction.Property.HANG.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 5, 5);
        jPanel4.add(lblTopografie1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_ausrichtung}"),
                cbTopografie1,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel4.add(cbTopografie1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panLageBody2.add(jPanel4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panLageBody2.add(filler17, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panLageBody2.add(filler1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 10);
        panStandortdaten.add(panLageBody2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel10.add(panStandortdaten, gridBagConstraints);

        panPlanungsrecht.setLayout(new java.awt.GridBagLayout());

        panMessstellenausbauTitle.setBackground(java.awt.Color.darkGray);
        panMessstellenausbauTitle.setLayout(new java.awt.GridBagLayout());

        lblMessstellenausbauTitle.setFont(lblMessstellenausbauTitle.getFont());
        lblMessstellenausbauTitle.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblMessstellenausbauTitle,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblMessstellenausbauTitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMessstellenausbauTitle.add(lblMessstellenausbauTitle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panPlanungsrecht.add(panMessstellenausbauTitle, gridBagConstraints);

        panMessstellenausbauBody.setOpaque(false);
        panMessstellenausbauBody.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblFlaechennutzung1,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblFlaechennutzung1.text")); // NOI18N
        lblFlaechennutzung1.setName(PotenzialflaecheReportServerAction.Property.REGIONALPLAN.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panMessstellenausbauBody.add(lblFlaechennutzung1, gridBagConstraints);

        defaultBindableCheckboxField9.setOpaque(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.regionalplan}"),
                defaultBindableCheckboxField9,
                org.jdesktop.beansbinding.BeanProperty.create("selectedElements"));
        bindingGroup.addBinding(binding);

        defaultBindableCheckboxField9.setLayout(new java.awt.FlowLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMessstellenausbauBody.add(defaultBindableCheckboxField9, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblFlaechennutzung,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblFlaechennutzung.text")); // NOI18N
        lblFlaechennutzung.setName(PotenzialflaecheReportServerAction.Property.FLAECHENNUTZUNGSPLAN.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panMessstellenausbauBody.add(lblFlaechennutzung, gridBagConstraints);

        searchLabelsFieldPanel6.setOpaque(false);
        searchLabelsFieldPanel6.setLayout(new java.awt.FlowLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMessstellenausbauBody.add(searchLabelsFieldPanel6, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblFlaechennutzung2,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblFlaechennutzung2.text")); // NOI18N
        lblFlaechennutzung2.setName(PotenzialflaecheReportServerAction.Property.BEBAUUNGSPLAN.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panMessstellenausbauBody.add(lblFlaechennutzung2, gridBagConstraints);

        searchLabelsFieldPanel4.setOpaque(false);
        searchLabelsFieldPanel4.setLayout(new java.awt.FlowLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMessstellenausbauBody.add(searchLabelsFieldPanel4, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblFlaechennutzung3,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblFlaechennutzung3.text")); // NOI18N
        lblFlaechennutzung3.setName(PotenzialflaecheReportServerAction.Property.FESTSETZUNGEN_BPLAN.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panMessstellenausbauBody.add(lblFlaechennutzung3, gridBagConstraints);

        jPanel12.setOpaque(false);
        jPanel12.setLayout(new java.awt.GridBagLayout());

        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(3);
        jTextArea1.setWrapStyleWord(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.festsetzungen_bplan}"),
                jTextArea1,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane3.setViewportView(jTextArea1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel12.add(jScrollPane3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel12.add(filler2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.jLabel1.text")); // NOI18N
        jLabel1.setName(PotenzialflaecheReportServerAction.Property.FESTSETZUNGEN_BPLAN_STAND.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel12.add(jLabel1, gridBagConstraints);

        dateStand1.setToolTipText(org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.dateStand1.toolTipText")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.jahr_festsetzungen_bplan}"),
                dateStand1,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel12.add(dateStand1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMessstellenausbauBody.add(jPanel12, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblVorhandeneBebauung1,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblVorhandeneBebauung1.text")); // NOI18N
        lblVorhandeneBebauung1.setName(PotenzialflaecheReportServerAction.Property.BAUORDNUNGSRECHT_GENEHMIGUNG
                    .toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panMessstellenausbauBody.add(lblVorhandeneBebauung1, gridBagConstraints);

        jPanel5.setOpaque(false);
        jPanel5.setLayout(new java.awt.GridBagLayout());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_bauordnungsrecht_genehmigung}"),
                cbVorhandeneBebauung1,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel5.add(cbVorhandeneBebauung1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_bauordnungsrecht_baulast}"),
                cbVorhandeneBebauung2,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel5.add(cbVorhandeneBebauung2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMessstellenausbauBody.add(jPanel5, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panMessstellenausbauBody.add(filler37, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panMessstellenausbauBody.add(filler3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 10);
        panPlanungsrecht.add(panMessstellenausbauBody, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel10.add(panPlanungsrecht, gridBagConstraints);

        panErweitert.setLayout(new java.awt.GridBagLayout());

        panLageTitle3.setBackground(java.awt.Color.darkGray);
        panLageTitle3.setLayout(new java.awt.GridBagLayout());

        lblLageTitle3.setFont(lblLageTitle3.getFont());
        lblLageTitle3.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblLageTitle3,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblLageTitle3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panLageTitle3.add(lblLageTitle3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panErweitert.add(panLageTitle3, gridBagConstraints);

        panLageBody3.setOpaque(false);
        panLageBody3.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblStadtbezirk4,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblStadtbezirk4.text")); // NOI18N
        lblStadtbezirk4.setName(PotenzialflaecheReportServerAction.Property.UMGEBUNGSNUTZUNG.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody3.add(lblStadtbezirk4, gridBagConstraints);

        defaultBindableCheckboxField6.setOpaque(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.umgebungsnutzung}"),
                defaultBindableCheckboxField6,
                org.jdesktop.beansbinding.BeanProperty.create("selectedElements"));
        bindingGroup.addBinding(binding);

        defaultBindableCheckboxField6.setLayout(new java.awt.FlowLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(defaultBindableCheckboxField6, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblStadtbezirk6,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblStadtbezirk6.text")); // NOI18N
        lblStadtbezirk6.setName(PotenzialflaecheReportServerAction.Property.BRACHFLAECHENKATEGORIE.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody3.add(lblStadtbezirk6, gridBagConstraints);

        jPanel9.setOpaque(false);
        jPanel9.setLayout(new java.awt.GridBagLayout());

        defaultBindableCheckboxField1.setOpaque(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.arr_brachflaechen}"),
                defaultBindableCheckboxField1,
                org.jdesktop.beansbinding.BeanProperty.create("selectedElements"));
        bindingGroup.addBinding(binding);

        defaultBindableCheckboxField1.setLayout(new java.awt.FlowLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel9.add(defaultBindableCheckboxField1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblNutzungsaufgabe,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblNutzungsaufgabe.text")); // NOI18N
        lblNutzungsaufgabe.setName(PotenzialflaecheReportServerAction.Property.JAHR_NUTZUNGSAUFGABE.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel9.add(lblNutzungsaufgabe, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.jahr_brachflaeche}"),
                txtJahrNutzungsaufgabe,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 5, 0);
        jPanel9.add(txtJahrNutzungsaufgabe, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel9.add(filler5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panLageBody3.add(jPanel9, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblStadtbezirk3,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblStadtbezirk3.text")); // NOI18N
        lblStadtbezirk3.setName(PotenzialflaecheReportServerAction.Property.BISHERIGE_NUTZUNG.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody3.add(lblStadtbezirk3, gridBagConstraints);

        defaultBindableCheckboxField5.setOpaque(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bisherige_nutzung}"),
                defaultBindableCheckboxField5,
                org.jdesktop.beansbinding.BeanProperty.create("selectedElements"));
        bindingGroup.addBinding(binding);

        defaultBindableCheckboxField5.setLayout(new java.awt.FlowLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(defaultBindableCheckboxField5, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblVorhandeneBebauung,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblVorhandeneBebauung.text")); // NOI18N
        lblVorhandeneBebauung.setName(PotenzialflaecheReportServerAction.Property.VORHANDENE_BEBAUUNG.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody3.add(lblVorhandeneBebauung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.vorhandene_bebauung}"),
                cbVorhandeneBebauung,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(cbVorhandeneBebauung, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblVorhandeneBebauung2,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblVorhandeneBebauung2.text")); // NOI18N
        lblVorhandeneBebauung2.setName(PotenzialflaecheReportServerAction.Property.VERSIEGELUNG.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody3.add(lblVorhandeneBebauung2, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_versiegelung}"),
                cbVorhandeneBebauung3,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(cbVorhandeneBebauung3, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblAessereErschl,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblAessereErschl.text")); // NOI18N
        lblAessereErschl.setName(PotenzialflaecheReportServerAction.Property.AEUSSERE_ERSCHLIESSUNG.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 6);
        panLageBody3.add(lblAessereErschl, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_aeussere_erschliessung}"),
                cbAeussereErschluessung,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(cbAeussereErschluessung, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblStadtbezirk1,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblStadtbezirk1.text")); // NOI18N
        lblStadtbezirk1.setName(PotenzialflaecheReportServerAction.Property.STADTRAUMTYPEN.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody3.add(lblStadtbezirk1, gridBagConstraints);

        searchLabelsFieldPanel5.setOpaque(false);
        searchLabelsFieldPanel5.setLayout(new java.awt.FlowLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(searchLabelsFieldPanel5, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblStadtbezirk2,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblStadtbezirk2.text")); // NOI18N
        lblStadtbezirk2.setName(PotenzialflaecheReportServerAction.Property.WOHNLAGEN.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody3.add(lblStadtbezirk2, gridBagConstraints);

        searchLabelsFieldPanel3.setOpaque(false);
        searchLabelsFieldPanel3.setLayout(new java.awt.FlowLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(searchLabelsFieldPanel3, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblAessereErschl1,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblAessereErschl1.text")); // NOI18N
        lblAessereErschl1.setName(PotenzialflaecheReportServerAction.Property.KLIMAINFORMATIONEN.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 6);
        panLageBody3.add(lblAessereErschl1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_klimainformationen}"),
                cbAeussereErschluessung1,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(cbAeussereErschluessung1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panLageBody3.add(filler18, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panLageBody3.add(filler4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 10);
        panErweitert.add(panLageBody3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel10.add(panErweitert, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jPanel10, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(filler8, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panMain.add(jPanel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panMain.add(filler15, gridBagConstraints);

        add(panMain, "grunddaten");

        panDetail.setOpaque(false);
        panDetail.setLayout(new java.awt.GridBagLayout());

        jPanel15.setOpaque(false);
        jPanel15.setLayout(new java.awt.GridBagLayout());

        panAllgemein3.setLayout(new java.awt.GridBagLayout());

        panBeschreibungTitle3.setBackground(java.awt.Color.darkGray);
        panBeschreibungTitle3.setLayout(new java.awt.GridBagLayout());

        lblBeschreibungTitle3.setFont(lblBeschreibungTitle3.getFont());
        lblBeschreibungTitle3.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblBeschreibungTitle3,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblBeschreibungTitle3.text")); // NOI18N
        lblBeschreibungTitle3.setName(PotenzialflaecheReportServerAction.Property.BESCHREIBUNG_FLAECHE.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBeschreibungTitle3.add(lblBeschreibungTitle3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panAllgemein3.add(panBeschreibungTitle3, gridBagConstraints);

        jPanel11.setOpaque(false);
        jPanel11.setLayout(new java.awt.GridBagLayout());

        jScrollPane15.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane15.setMinimumSize(new java.awt.Dimension(200, 80));
        jScrollPane15.setPreferredSize(new java.awt.Dimension(200, 80));

        taFlaeche.setLineWrap(true);
        taFlaeche.setRows(10);
        taFlaeche.setWrapStyleWord(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.beschreibung_flaeche}"),
                taFlaeche,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane15.setViewportView(taFlaeche);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel11.add(jScrollPane15, gridBagConstraints);

        panArtControls3.setOpaque(false);
        panArtControls3.setLayout(new java.awt.GridBagLayout());

        btnFlaeche.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/commons/gui/icon-edit.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnFlaeche,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.btnFlaeche.text",
                new Object[] {}));                                                            // NOI18N
        btnFlaeche.setBorderPainted(false);
        btnFlaeche.setContentAreaFilled(false);
        btnFlaeche.setMaximumSize(new java.awt.Dimension(32, 32));
        btnFlaeche.setMinimumSize(new java.awt.Dimension(32, 32));
        btnFlaeche.setPreferredSize(new java.awt.Dimension(32, 32));
        btnFlaeche.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnFlaecheActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panArtControls3.add(btnFlaeche, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel11.add(panArtControls3, gridBagConstraints);
        panArtControls3.setVisible(isEditable());

        jPanel6.setOpaque(false);
        jPanel6.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblQuelle,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblQuelle.text")); // NOI18N
        lblQuelle.setName(PotenzialflaecheReportServerAction.Property.QUELLE.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel6.add(lblQuelle, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.quelle}"),
                jTextField1,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 10);
        jPanel6.add(jTextField1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblStand,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblStand.text")); // NOI18N
        lblStand.setName(PotenzialflaecheReportServerAction.Property.STAND.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 5, 5);
        jPanel6.add(lblStand, gridBagConstraints);

        dateStand.setToolTipText(org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.dateStand.toolTipText")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.stand}"),
                dateStand,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(sqlDateToUtilDateConverter);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel6.add(dateStand, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel11.add(jPanel6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 10);
        panAllgemein3.add(jPanel11, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 4.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel15.add(panAllgemein3, gridBagConstraints);

        panAllgemein6.setLayout(new java.awt.GridBagLayout());

        panBeschreibungTitle6.setBackground(java.awt.Color.darkGray);
        panBeschreibungTitle6.setLayout(new java.awt.GridBagLayout());

        lblBeschreibungTitle6.setFont(lblBeschreibungTitle6.getFont());
        lblBeschreibungTitle6.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblBeschreibungTitle6,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblBeschreibungTitle6.text")); // NOI18N
        lblBeschreibungTitle6.setName(PotenzialflaecheReportServerAction.Property.NOTWENDIGE_MASSNAHMEN.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBeschreibungTitle6.add(lblBeschreibungTitle6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panAllgemein6.add(panBeschreibungTitle6, gridBagConstraints);

        jPanel14.setOpaque(false);
        jPanel14.setLayout(new java.awt.GridBagLayout());

        jScrollPane7.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane7.setMinimumSize(new java.awt.Dimension(200, 80));
        jScrollPane7.setPreferredSize(new java.awt.Dimension(200, 80));

        taNotwendigeMassnahme.setLineWrap(true);
        taNotwendigeMassnahme.setRows(3);
        taNotwendigeMassnahme.setWrapStyleWord(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.notwendige_massnahmen}"),
                taNotwendigeMassnahme,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane7.setViewportView(taNotwendigeMassnahme);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel14.add(jScrollPane7, gridBagConstraints);

        panArtControls4.setOpaque(false);
        panArtControls4.setLayout(new java.awt.GridBagLayout());

        btnMassnahmen.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/commons/gui/icon-edit.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnMassnahmen,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.btnMassnahmen.text",
                new Object[] {}));                                                            // NOI18N
        btnMassnahmen.setBorderPainted(false);
        btnMassnahmen.setContentAreaFilled(false);
        btnMassnahmen.setMaximumSize(new java.awt.Dimension(32, 32));
        btnMassnahmen.setMinimumSize(new java.awt.Dimension(32, 32));
        btnMassnahmen.setPreferredSize(new java.awt.Dimension(32, 32));
        btnMassnahmen.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnMassnahmenActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panArtControls4.add(btnMassnahmen, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel14.add(panArtControls4, gridBagConstraints);
        panArtControls4.setVisible(isEditable());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 10);
        panAllgemein6.add(jPanel14, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel15.add(panAllgemein6, gridBagConstraints);

        panBewertung1.setLayout(new java.awt.GridBagLayout());

        panLageTitle5.setBackground(java.awt.Color.darkGray);
        panLageTitle5.setLayout(new java.awt.GridBagLayout());

        lblLageTitle6.setFont(lblLageTitle6.getFont());
        lblLageTitle6.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblLageTitle6,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblLageTitle6.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panLageTitle5.add(lblLageTitle6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panBewertung1.add(panLageTitle5, gridBagConstraints);

        panLageBody5.setOpaque(false);
        panLageBody5.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblEntwicklungsausssichten1,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblEntwicklungsausssichten1.text")); // NOI18N
        lblEntwicklungsausssichten1.setName(PotenzialflaecheReportServerAction.Property.POTENZIALART.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody5.add(lblEntwicklungsausssichten1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_potenzialart}"),
                cbEntwicklungsaussichten1,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody5.add(cbEntwicklungsaussichten1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblEntwicklungsausssichten2,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblEntwicklungsausssichten2.text")); // NOI18N
        lblEntwicklungsausssichten2.setName(PotenzialflaecheReportServerAction.Property.ENTWICKLUNGSSTAND.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody5.add(lblEntwicklungsausssichten2, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_entwicklungsstand}"),
                cbEntwicklungsaussichten2,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody5.add(cbEntwicklungsaussichten2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblEntwicklungsausssichten3,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblEntwicklungsausssichten3.text")); // NOI18N
        lblEntwicklungsausssichten3.setName(PotenzialflaecheReportServerAction.Property.RESTRIKTIONEN.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody5.add(lblEntwicklungsausssichten3, gridBagConstraints);

        defaultBindableCheckboxField4.setOpaque(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.arr_restriktionen}"),
                defaultBindableCheckboxField4,
                org.jdesktop.beansbinding.BeanProperty.create("selectedElements"));
        bindingGroup.addBinding(binding);

        defaultBindableCheckboxField4.setLayout(new java.awt.FlowLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody5.add(defaultBindableCheckboxField4, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblEntwicklungsausssichten5,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblEntwicklungsausssichten5.text")); // NOI18N
        lblEntwicklungsausssichten5.setName(PotenzialflaecheReportServerAction.Property.EMPFOHLENE_NUTZUNGEN
                    .toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody5.add(lblEntwicklungsausssichten5, gridBagConstraints);

        defaultBindableCheckboxField10.setOpaque(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.arr_empfohlene_nutzungen_wohnen}"),
                defaultBindableCheckboxField10,
                org.jdesktop.beansbinding.BeanProperty.create("selectedElements"));
        bindingGroup.addBinding(binding);

        defaultBindableCheckboxField10.setLayout(new java.awt.FlowLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody5.add(defaultBindableCheckboxField10, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblAktivierbarkeit1,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblAktivierbarkeit1.text")); // NOI18N
        lblAktivierbarkeit1.setName(PotenzialflaecheReportServerAction.Property.WOHNEINHEITEN.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody5.add(lblAktivierbarkeit1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_wohneinheiten}"),
                cbAktivierbarkeit1,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody5.add(cbAktivierbarkeit1, gridBagConstraints);

        jFormattedTextField1.setColumns(5);
        jFormattedTextField1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
                new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        jFormattedTextField1.setText(org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.jFormattedTextField1.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.anzahl_wohneinheiten}"),
                jFormattedTextField1,
                org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody5.add(jFormattedTextField1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblEntwicklungsausssichten,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblEntwicklungsausssichten.text")); // NOI18N
        lblEntwicklungsausssichten.setName(PotenzialflaecheReportServerAction.Property.ENTWICKLUNGSAUSSSICHTEN
                    .toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody5.add(lblEntwicklungsausssichten, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_entwicklungsaussichten}"),
                cbEntwicklungsaussichten,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody5.add(cbEntwicklungsaussichten, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panLageBody5.add(filler22, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panLageBody5.add(filler6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panBewertung1.add(panLageBody5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel15.add(panBewertung1, gridBagConstraints);

        panBewertung2.setLayout(new java.awt.GridBagLayout());

        panLageTitle6.setBackground(java.awt.Color.darkGray);
        panLageTitle6.setLayout(new java.awt.GridBagLayout());

        lblLageTitle7.setFont(lblLageTitle7.getFont());
        lblLageTitle7.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblLageTitle7,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblLageTitle7.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panLageTitle6.add(lblLageTitle7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panBewertung2.add(panLageTitle6, gridBagConstraints);

        panLageBody6.setOpaque(false);
        panLageBody6.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblVerfuegbarkeit1,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblVerfuegbarkeit1.text")); // NOI18N
        lblVerfuegbarkeit1.setName(PotenzialflaecheReportServerAction.Property.VERFUEGBBARKEIT.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody6.add(lblVerfuegbarkeit1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.verfuegbarkeit}"),
                cbVerfuegbarkeit1,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody6.add(cbVerfuegbarkeit1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblAktivierbarkeit,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblAktivierbarkeit.text")); // NOI18N
        lblAktivierbarkeit.setName(PotenzialflaecheReportServerAction.Property.VERWERTBARKEIT.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody6.add(lblAktivierbarkeit, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.aktivierbarkeit}"),
                cbAktivierbarkeit,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody6.add(cbAktivierbarkeit, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblHandlungsdruck1,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblHandlungsdruck1.text")); // NOI18N
        lblHandlungsdruck1.setToolTipText(org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblHandlungsdruck1.toolTipText",
                new Object[] {}));                                    // NOI18N
        lblHandlungsdruck1.setName(PotenzialflaecheReportServerAction.Property.HANDLUNGSDRUCK.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody6.add(lblHandlungsdruck1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_handlungsprioritaet}"),
                cbHandlungsdruck1,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody6.add(cbHandlungsdruck1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblEntwicklungsausssichten4,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblEntwicklungsausssichten4.text")); // NOI18N
        lblEntwicklungsausssichten4.setName(PotenzialflaecheReportServerAction.Property.EMPFOHLENE_NUTZUNGEN
                    .toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody6.add(lblEntwicklungsausssichten4, gridBagConstraints);

        defaultBindableCheckboxField8.setOpaque(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.arr_empfohlene_nutzungen}"),
                defaultBindableCheckboxField8,
                org.jdesktop.beansbinding.BeanProperty.create("selectedElements"));
        bindingGroup.addBinding(binding);

        defaultBindableCheckboxField8.setLayout(new java.awt.FlowLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody6.add(defaultBindableCheckboxField8, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblRevitalisierung1,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblRevitalisierung1.text")); // NOI18N
        lblRevitalisierung1.setName(PotenzialflaecheReportServerAction.Property.REVITALISIERUNG.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody6.add(lblRevitalisierung1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_revitalisierung}"),
                cbRevitalisierung1,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody6.add(cbRevitalisierung1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblHandlungsdruck,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblHandlungsdruck.text")); // NOI18N
        lblHandlungsdruck.setToolTipText(org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblHandlungsdruck.toolTipText",
                new Object[] {}));                                   // NOI18N
        lblHandlungsdruck.setName(PotenzialflaecheReportServerAction.Property.HANDLUNGSDRUCK.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody6.add(lblHandlungsdruck, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.handlungsdruck}"),
                cbHandlungsdruck,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody6.add(cbHandlungsdruck, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panLageBody6.add(filler23, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panLageBody6.add(filler7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panBewertung2.add(panLageBody6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel15.add(panBewertung2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel15.add(filler12, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panDetail.add(jPanel15, gridBagConstraints);

        add(panDetail, "details");

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenAbort4ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnMenAbort4ActionPerformed
        dlgFlaeche.setVisible(false);
    }                                                                                //GEN-LAST:event_btnMenAbort4ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenOk4ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnMenOk4ActionPerformed
        try {
            cidsBean.setProperty("beschreibung_flaeche", taFlaecheDialog.getText());
        } catch (Exception e) {
            LOG.error("Cannot save text for beschreibung_flaeche", e);
        }
        dlgFlaeche.setVisible(false);
    }                                                                             //GEN-LAST:event_btnMenOk4ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenAbort5ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnMenAbort5ActionPerformed
        dlgMassnahme.setVisible(false);
    }                                                                                //GEN-LAST:event_btnMenAbort5ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenOk5ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnMenOk5ActionPerformed
        try {
            cidsBean.setProperty("notwendige_massnahmen", taMassnahmeDialog.getText());
        } catch (Exception e) {
            LOG.error("Cannot save text for notwendige_massnahmen", e);
        }
        dlgMassnahme.setVisible(false);
    }                                                                             //GEN-LAST:event_btnMenOk5ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMassnahmenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnMassnahmenActionPerformed
        taMassnahmeDialog.setText((String)cidsBean.getProperty("notwendige_massnahmen"));
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(this), dlgMassnahme, true);
    }                                                                                 //GEN-LAST:event_btnMassnahmenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnFlaecheActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnFlaecheActionPerformed
        taFlaecheDialog.setText((String)cidsBean.getProperty("beschreibung_flaeche"));
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(this), dlgFlaeche, true);
    }                                                                              //GEN-LAST:event_btnFlaecheActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbGeomFocusLost(final java.awt.event.FocusEvent evt) { //GEN-FIRST:event_cbGeomFocusLost
        setGeometryArea();
    }                                                                   //GEN-LAST:event_cbGeomFocusLost

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtBezeichnungFocusLost(final java.awt.event.FocusEvent evt) { //GEN-FIRST:event_txtBezeichnungFocusLost
        panTitle.refreshTitle();
    }                                                                           //GEN-LAST:event_txtBezeichnungFocusLost

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbAeussereErschluessungActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbAeussereErschluessungActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_cbAeussereErschluessungActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbGeomActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbGeomActionPerformed
        refreshMap();
        refreshGeomFields();
    }                                                                          //GEN-LAST:event_cbGeomActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblQuartiere1MouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblQuartiere1MouseClicked
//        if (evt.getClickCount() > 1) {
//            final DescriptionPaneFS pane = new DescriptionPaneFS();
//            final JDialog dialog = new JDialog();
//            dialog.setContentPane(pane);
//            final Collection<MetaObjectNode> mons = new ArrayList<>();
//            final DefaultListModel<MetaObjectNode> model = (DefaultListModel<MetaObjectNode>)
//                monSearchResultsList1.getModel();
//            for (int i = 0; i < model.getSize(); i++) {
//                final MetaObjectNode mon = model.get(i);
//                mons.add(mon);
//            }
//
//
//            pane.gotoMetaObjectNodes(mons.toArray(new MetaObjectNode[0]));
//
//            StaticSwingTools.showDialog(this, dialog, true);
//        }
    } //GEN-LAST:event_lblQuartiere1MouseClicked

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kampagne  gup DOCUMENT ME!
     */
    public static void setLastKampagne(final CidsBean kampagne) {
        lastKampagne = kampagne;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        this.cidsBean = cidsBean;
        panTitle.refreshTitle();

        bindingGroup.unbind();
        if (cidsBean != null) {
            refreshMap();
            refreshGeomFields();

            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cidsBean,
                getConnectionContext());
            bindingGroup.bind();

            setGeometryArea();

            if ((getLastKampagne() != null) && editable) {
                try {
                    if (cidsBean.getMetaObject().getStatus() == MetaObject.NEW) {
                        cidsBean.setProperty("kampagne", getLastKampagne());
                        markUsedFields((CidsBean)getLastKampagne().getProperty("steckbrieftemplate"));
                    }
                } catch (Exception ex) {
                    LOG.error("Cannot add kampagne", ex);
                    ObjectRendererUtils.showExceptionWindowToUser(
                        "Kampagne konnte nicht hinzugefügt werden",
                        ex,
                        PfPotenzialflaecheEditor.this);
                }
            } else {
                final CidsBean kampagne = (CidsBean)cidsBean.getProperty("kampagne");
                if (kampagne != null) {
                    markUsedFields((CidsBean)kampagne.getProperty("steckbrieftemplate"));
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void startDownload() {
        PfPotenzialflaecheReportGenerator.startDownload(getCidsBean(), getConnectionContext());
    }

    /**
     * DOCUMENT ME!
     */
    private void initComponentMap() {
        for (final Field field : PfPotenzialflaecheEditor.class.getDeclaredFields()) {
            try {
                final Object o = field.get(this);
                if (o instanceof JComponent) {
                    final JComponent component = (JComponent)o;
                    final String name = component.getName();
                    if (name != null) {
                        componentMap.put(name, component);
                    }
                }
            } catch (Exception ex) {
                LOG.warn("Cannot process field", ex);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  steckbrieftemplate  DOCUMENT ME!
     */
    private void markUsedFields(final CidsBean steckbrieftemplate) {
        if (steckbrieftemplate != null) {
            final String fields = (String)steckbrieftemplate.getProperty("verwendete_flaechenattribute");
            final List<String> usedProperties = new ArrayList<>();
            final StringTokenizer st = new StringTokenizer(fields, ",");
            while (st.hasMoreTokens()) {
                usedProperties.add(st.nextToken());
            }

            for (final String name : componentMap.keySet()) {
                if (name != null) {
                    final JComponent component = componentMap.get(name);
                    if (component != null) {
                        if (usedProperties.contains(name)) {
                            component.setFont(component.getFont().deriveFont(Font.BOLD));
                        } else {
                            component.setFont(component.getFont().deriveFont(Font.PLAIN));
                        }
                    }
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void setGeometryArea() {
        final Object geo = cidsBean.getProperty("geometrie.geo_field");

        if (geo instanceof Geometry) {
            final double area = ((Geometry)geo).getArea();
            lblFlaechengroesseWert.setText(String.format(
                    "%d m² (circa %.1f ha)",
                    Math.round(area),
                    Math.round(area / 1000)
                            / 10.0));
        }
    }

    @Override
    public void dispose() {
        if (editable) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).dispose();
        }
        mappingComponent1.dispose();
    }

    @Override
    public String getTitle() {
        return panTitle.getTitle();
    }

    @Override
    public void setTitle(final String string) {
    }

    @Override
    public void editorClosed(final EditorClosedEvent ece) {
        try {
            if (ece.getStatus().equals(EditorSaveListener.EditorSaveStatus.SAVE_SUCCESS)) {
                if ((getLastKampagne() != null) && (cidsBean.getMetaObject().getStatus() == MetaObject.NEW)
                            && (currentTreeNode instanceof ObjectTreeNode)) {
                    final List<CidsBean> flaechen = CidsBeanSupport.getBeanCollectionFromProperty(
                            getLastKampagne(),
                            "zugeordnete_flaechen");
                    final MetaObject mo = ((ObjectTreeNode)currentTreeNode).getMetaObject();
                    if (!flaechen.contains(mo.getBean())) {
                        flaechen.add(mo.getBean());
                        try {
                            getLastKampagne().persist(getConnectionContext());
                        } catch (Exception ex) {
                            LOG.error("Cannot save kampagne object", ex);
                            ObjectRendererUtils.showExceptionWindowToUser(
                                "Kampagne konnte nicht gespeichert werden",
                                ex,
                                this);
                        }
                    }
                }
            }
        } finally {
            setLastKampagne(null);
        }
    }

    @Override
    public boolean prepareForSave() {
        currentTreeNode = ComponentRegistry.getRegistry().getAttributeEditor().getTreeNode();
        return true;
    }

    @Override
    public JComponent getFooterComponent() {
        return panFooter;
    }

    @Override
    public JComponent getTitleComponent() {
        return panTitle;
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    /**
     * DOCUMENT ME!
     */
    private void refreshMap() {
        mappingComponent1.getFeatureCollection().removeAllFeatures();

        if (getCidsBean() != null) {
            final Geometry geom = (Geometry)getCidsBean().getProperty("geometrie.geo_field");
            if (geom != null) {
                try {
                    final XBoundingBox box = new XBoundingBox(geom.getEnvelope().buffer(
                                ClientAlkisConf.getInstance().getGeoBuffer()
                                        * 2));

                    final ActiveLayerModel mappingModel = new ActiveLayerModel();
                    mappingModel.setSrs(ClientAlkisConf.getInstance().getSrsService());
                    mappingModel.addHome(new XBoundingBox(
                            box.getX1(),
                            box.getY1(),
                            box.getX2(),
                            box.getY2(),
                            ClientAlkisConf.getInstance().getSrsService(),
                            true));
                    final SimpleWMS swms = new SimpleWMS(new SimpleWmsGetMapUrl(
                                ClientAlkisConf.getInstance().getMapCallString()));
                    swms.setName("Potenzialflächen");

                    // add the raster layer to the model
                    mappingModel.addLayer(swms);
                    // set the model
                    mappingComponent1.setMappingModel(mappingModel);
                    // interaction mode
                    mappingComponent1.gotoInitialBoundingBox();
                    mappingComponent1.setInteractionMode(MappingComponent.ZOOM);
                    // finally when all configurations are done ...
                    mappingComponent1.unlock();
                } catch (final Exception ex) {
                    LOG.warn("could not init Map !", ex);
                }

                final StyledFeature dsf = new DefaultStyledFeature();
                dsf.setGeometry(geom);
                dsf.setFillingPaint(Color.RED);
                mappingComponent1.getFeatureCollection().addFeature(dsf);
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void refreshGeomFields() {
        final Geometry geometry = (getCidsBean() != null) ? (Geometry)getCidsBean().getProperty("geometrie.geo_field")
                                                          : null;

        if (geometry != null) {
            for (final SearchLabelsFieldPanel searchLabelFieldPanel : searchLabelFieldPanels) {
                final CidsServerSearch search = searchLabelFieldPanel.getSearch();
                if (search instanceof GeometrySearch) {
                    final GeometrySearch geometrySearch = (GeometrySearch)search;
                    geometrySearch.setGeometry(geometry);
                    searchLabelFieldPanel.refresh();
                }
            }
            ((GeometrySearch)monSearchResultsList1.getSearch()).setGeometry(geometry);
            monSearchResultsList1.refresh();
        } else {
            for (final SearchLabelsFieldPanel searchLabelFieldPanel : searchLabelFieldPanels) {
                searchLabelFieldPanel.clear();
            }
            monSearchResultsList1.removeAll();
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void showDetails() {
        ((CardLayout)getLayout()).show(this, "details");
    }

    /**
     * DOCUMENT ME!
     */
    public void showGrunddaten() {
        ((CardLayout)getLayout()).show(this, "grunddaten");
    }
}
