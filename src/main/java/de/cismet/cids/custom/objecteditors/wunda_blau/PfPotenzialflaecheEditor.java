/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objecteditors.wunda_blau;

import Sirius.navigator.tools.CacheException;
import Sirius.navigator.tools.MetaObjectCache;
import Sirius.navigator.types.treenode.ObjectTreeNode;
import Sirius.navigator.ui.ComponentRegistry;
import Sirius.navigator.ui.DescriptionPaneFS;

import Sirius.server.localserver.attribute.MemberAttributeInfo;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;

import com.vividsolutions.jts.geom.Geometry;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
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

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.objectrenderer.utils.alkis.ClientAlkisConf;
import de.cismet.cids.custom.objectrenderer.wunda_blau.AlkisLandparcelAggregationRenderer;
import de.cismet.cids.custom.wunda_blau.search.actions.PotenzialflaecheReportServerAction;
import de.cismet.cids.custom.wunda_blau.search.server.AlkisLandparcelGeometryMonSearch;
import de.cismet.cids.custom.wunda_blau.search.server.BplaeneMonSearch;
import de.cismet.cids.custom.wunda_blau.search.server.FnpHauptnutzungenMonSearch;
import de.cismet.cids.custom.wunda_blau.search.server.GeometrySearch;
import de.cismet.cids.custom.wunda_blau.search.server.KstGeometryMonSearch;
import de.cismet.cids.custom.wunda_blau.search.server.StadtraumtypMonSearch;
import de.cismet.cids.custom.wunda_blau.search.server.WohnlagenKategorisierungMonSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.CidsObjectEditorFactory;
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

import static de.cismet.cids.editors.CidsObjectEditorFactory.getMetaClass;

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
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler11;
    private javax.swing.Box.Filler filler12;
    private javax.swing.Box.Filler filler13;
    private javax.swing.Box.Filler filler14;
    private javax.swing.Box.Filler filler15;
    private javax.swing.Box.Filler filler16;
    private javax.swing.Box.Filler filler19;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler20;
    private javax.swing.Box.Filler filler21;
    private javax.swing.Box.Filler filler22;
    private javax.swing.Box.Filler filler24;
    private javax.swing.Box.Filler filler26;
    private javax.swing.Box.Filler filler27;
    private javax.swing.Box.Filler filler28;
    private javax.swing.Box.Filler filler29;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler30;
    private javax.swing.Box.Filler filler31;
    private javax.swing.Box.Filler filler32;
    private javax.swing.Box.Filler filler33;
    private javax.swing.Box.Filler filler34;
    private javax.swing.Box.Filler filler35;
    private javax.swing.Box.Filler filler36;
    private javax.swing.Box.Filler filler37;
    private javax.swing.Box.Filler filler38;
    private javax.swing.Box.Filler filler39;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler40;
    private javax.swing.Box.Filler filler41;
    private javax.swing.Box.Filler filler42;
    private javax.swing.Box.Filler filler43;
    private javax.swing.Box.Filler filler44;
    private javax.swing.Box.Filler filler45;
    private javax.swing.Box.Filler filler46;
    private javax.swing.Box.Filler filler47;
    private javax.swing.Box.Filler filler48;
    private javax.swing.Box.Filler filler49;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler50;
    private javax.swing.Box.Filler filler51;
    private javax.swing.Box.Filler filler52;
    private javax.swing.Box.Filler filler53;
    private javax.swing.Box.Filler filler54;
    private javax.swing.Box.Filler filler55;
    private javax.swing.Box.Filler filler56;
    private javax.swing.Box.Filler filler57;
    private javax.swing.Box.Filler filler58;
    private javax.swing.Box.Filler filler59;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler60;
    private javax.swing.Box.Filler filler61;
    private javax.swing.Box.Filler filler62;
    private javax.swing.Box.Filler filler63;
    private javax.swing.Box.Filler filler64;
    private javax.swing.Box.Filler filler65;
    private javax.swing.Box.Filler filler66;
    private javax.swing.Box.Filler filler67;
    private javax.swing.Box.Filler filler68;
    private javax.swing.Box.Filler filler69;
    private javax.swing.Box.Filler filler70;
    private javax.swing.Box.Filler filler71;
    private javax.swing.Box.Filler filler72;
    private javax.swing.Box.Filler filler73;
    private javax.swing.Box.Filler filler74;
    private javax.swing.Box.Filler filler75;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JButton jButton1;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel2;
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
    private javax.swing.JScrollPane jScrollPane4;
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
    private javax.swing.JLabel lblVorhandeneBebauung3;
    private de.cismet.cismap.commons.gui.MappingComponent mappingComponent1;
    private de.cismet.cids.custom.objecteditors.utils.CidsSearchResultsList<MetaObjectNode> monSearchResultsList1;
    private de.cismet.cids.custom.objecteditors.utils.CidsSearchResultsList<MetaObjectNode> monSearchResultsList2;
    private de.cismet.tools.gui.RoundedPanel panAllgemein;
    private de.cismet.tools.gui.RoundedPanel panAllgemein3;
    private de.cismet.tools.gui.RoundedPanel panAllgemein6;
    private javax.swing.JPanel panArtControls3;
    private javax.swing.JPanel panArtControls4;
    private de.cismet.tools.gui.SemiRoundedPanel panBeschreibungTitle;
    private de.cismet.tools.gui.SemiRoundedPanel panBeschreibungTitle3;
    private de.cismet.tools.gui.SemiRoundedPanel panBeschreibungTitle6;
    private de.cismet.tools.gui.RoundedPanel panBewertung1;
    private javax.swing.JPanel panDetail;
    private de.cismet.tools.gui.RoundedPanel panErweitert;
    private javax.swing.JPanel panFlaeche;
    private javax.swing.JPanel panFlaeche1;
    private javax.swing.JPanel panLageBody2;
    private javax.swing.JPanel panLageBody3;
    private javax.swing.JPanel panLageBody5;
    private de.cismet.tools.gui.SemiRoundedPanel panLageTitle2;
    private de.cismet.tools.gui.SemiRoundedPanel panLageTitle3;
    private de.cismet.tools.gui.SemiRoundedPanel panLageTitle5;
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
        RendererTools.makeUneditable(monSearchResultsList2, true);
        RendererTools.makeUneditable(txtNummer, true);

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

        panTitle.setWaiting(false);
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
        filler74 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblNummer = new javax.swing.JLabel();
        txtNummer = new javax.swing.JTextField();
        filler48 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblBezeichnung = new javax.swing.JLabel();
        txtBezeichnung = new javax.swing.JTextField();
        mappingComponent1 = new de.cismet.cismap.commons.gui.MappingComponent();
        jPanel7 = new javax.swing.JPanel();
        filler49 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblGeometrie5 = new javax.swing.JLabel();
        cbGeom = (!editable) ? new JComboBox() : new DefaultCismapGeometryComboBoxEditor();
        filler50 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblFlaechengroesse = new javax.swing.JLabel();
        lblFlaechengroesseWert = new javax.swing.JLabel();
        filler51 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblStadtbezirk = new javax.swing.JLabel();
        filler58 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        searchLabelsFieldPanel2 = new de.cismet.cids.editors.SearchLabelsFieldPanel(new KstGeometryMonSearch(
                    KstGeometryMonSearch.SearchFor.BEZIRK));
        filler52 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblQuartiere = new javax.swing.JLabel();
        filler56 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        searchLabelsFieldPanel1 = new de.cismet.cids.editors.SearchLabelsFieldPanel(new KstGeometryMonSearch(
                    KstGeometryMonSearch.SearchFor.QUARTIER));
        filler53 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblQuartiere1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        monSearchResultsList1 = new de.cismet.cids.custom.objecteditors.utils.CidsSearchResultsList<>(
                new AlkisLandparcelGeometryMonSearch());
        filler54 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblStadtbezirk5 = new javax.swing.JLabel();
        filler59 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        defaultBindableCheckboxField3 = new de.cismet.cids.editors.DefaultBindableLabelsPanel(
                isEditable(),
                "Eigentümer:");
        filler55 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jPanel10 = new javax.swing.JPanel();
        panStandortdaten = new de.cismet.tools.gui.RoundedPanel();
        panLageTitle2 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblLageTitle2 = new javax.swing.JLabel();
        panLageBody2 = new javax.swing.JPanel();
        filler19 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblLagetyp = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        cbLagetyp = new de.cismet.cids.editors.DefaultBindableReferenceCombo(NULLABLE_OPTION, MANAGEABLE_OPTION);
        filler22 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblOepnv = new javax.swing.JLabel();
        cbOepnv = new de.cismet.cids.editors.DefaultBindableReferenceCombo(NULLABLE_OPTION, MANAGEABLE_OPTION);
        filler20 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblLagetyp1 = new javax.swing.JLabel();
        filler26 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        defaultBindableCheckboxField11 = new de.cismet.cids.editors.DefaultBindableLabelsPanel(
                isEditable(),
                "Nähe zu:");
        filler21 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblLagetyp2 = new javax.swing.JLabel();
        cbLagetyp2 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(NULLABLE_OPTION, MANAGEABLE_OPTION);
        filler24 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblTopografie = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        cbTopografie = new de.cismet.cids.editors.DefaultBindableReferenceCombo(NULLABLE_OPTION, MANAGEABLE_OPTION);
        lblTopografie1 = new javax.swing.JLabel();
        cbTopografie1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(NULLABLE_OPTION);
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(200, 0),
                new java.awt.Dimension(200, 0),
                new java.awt.Dimension(200, 0));
        panPlanungsrecht = new de.cismet.tools.gui.RoundedPanel();
        panMessstellenausbauTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        lblMessstellenausbauTitle = new javax.swing.JLabel();
        panMessstellenausbauBody = new javax.swing.JPanel();
        filler27 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblFlaechennutzung1 = new javax.swing.JLabel();
        filler34 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        defaultBindableCheckboxField9 = new de.cismet.cids.editors.DefaultBindableLabelsPanel(
                isEditable(),
                "Regionalplan:");
        filler28 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblFlaechennutzung = new javax.swing.JLabel();
        filler35 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        searchLabelsFieldPanel6 = new de.cismet.cids.editors.SearchLabelsFieldPanel(new FnpHauptnutzungenMonSearch());
        filler29 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblFlaechennutzung2 = new javax.swing.JLabel();
        filler36 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        searchLabelsFieldPanel4 = new de.cismet.cids.editors.SearchLabelsFieldPanel(new BplaeneMonSearch(), true);
        filler30 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblFlaechennutzung3 = new javax.swing.JLabel();
        filler33 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jPanel12 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        filler32 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        jLabel1 = new javax.swing.JLabel();
        dateStand1 = new de.cismet.cids.editors.DefaultBindableDateChooser();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        filler31 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblVorhandeneBebauung1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        cbVorhandeneBebauung1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                MANAGEABLE_OPTION);
        cbVorhandeneBebauung2 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                MANAGEABLE_OPTION);
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(200, 0),
                new java.awt.Dimension(200, 0),
                new java.awt.Dimension(200, 0));
        panErweitert = new de.cismet.tools.gui.RoundedPanel();
        panLageTitle3 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblLageTitle3 = new javax.swing.JLabel();
        panLageBody3 = new javax.swing.JPanel();
        filler39 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblStadtbezirk4 = new javax.swing.JLabel();
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        defaultBindableCheckboxField6 = new de.cismet.cids.editors.DefaultBindableLabelsPanel(
                isEditable(),
                "Umgebungsnutzung:");
        filler40 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblStadtbezirk6 = new javax.swing.JLabel();
        filler11 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jPanel9 = new javax.swing.JPanel();
        defaultBindableCheckboxField1 = new de.cismet.cids.editors.DefaultBindableLabelsPanel(
                isEditable(),
                "Brachflächenkategorie:");
        filler38 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblNutzungsaufgabe = new javax.swing.JLabel();
        txtJahrNutzungsaufgabe = new javax.swing.JTextField();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(150, 0),
                new java.awt.Dimension(150, 0),
                new java.awt.Dimension(150, 0));
        filler41 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblStadtbezirk3 = new javax.swing.JLabel();
        filler13 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        defaultBindableCheckboxField5 = new de.cismet.cids.editors.DefaultBindableLabelsPanel(
                isEditable(),
                "Bisherige Nutzung:");
        filler42 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblVorhandeneBebauung = new javax.swing.JLabel();
        cbVorhandeneBebauung = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                MANAGEABLE_OPTION);
        jPanel16 = new javax.swing.JPanel();
        filler43 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblVorhandeneBebauung2 = new javax.swing.JLabel();
        cbVorhandeneBebauung3 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                MANAGEABLE_OPTION);
        filler44 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblAessereErschl = new javax.swing.JLabel();
        cbAeussereErschluessung = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                MANAGEABLE_OPTION);
        jPanel17 = new javax.swing.JPanel();
        filler75 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblVorhandeneBebauung3 = new javax.swing.JLabel();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jScrollPane4 = new javax.swing.JScrollPane();
        monSearchResultsList2 = new de.cismet.cids.custom.objecteditors.utils.CidsSearchResultsList<>();
        filler46 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblStadtbezirk2 = new javax.swing.JLabel();
        filler14 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        searchLabelsFieldPanel3 = new de.cismet.cids.editors.SearchLabelsFieldPanel(
                new WohnlagenKategorisierungMonSearch());
        filler45 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblStadtbezirk1 = new javax.swing.JLabel();
        filler16 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        searchLabelsFieldPanel5 = new de.cismet.cids.editors.SearchLabelsFieldPanel(new StadtraumtypMonSearch());
        filler47 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblAessereErschl1 = new javax.swing.JLabel();
        cbAeussereErschluessung1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                MANAGEABLE_OPTION);
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
        filler73 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
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
        filler57 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblEntwicklungsausssichten1 = new javax.swing.JLabel();
        cbEntwicklungsaussichten1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                MANAGEABLE_OPTION);
        filler60 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblEntwicklungsausssichten2 = new javax.swing.JLabel();
        cbEntwicklungsaussichten2 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                MANAGEABLE_OPTION);
        filler61 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblEntwicklungsausssichten3 = new javax.swing.JLabel();
        filler37 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        defaultBindableCheckboxField4 = new de.cismet.cids.editors.DefaultBindableLabelsPanel(
                isEditable(),
                "Restriktionen/Hemmnisse:");
        filler62 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblEntwicklungsausssichten5 = new javax.swing.JLabel();
        filler71 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        defaultBindableCheckboxField10 = new de.cismet.cids.editors.DefaultBindableLabelsPanel(
                isEditable(),
                "Empfohlene Art der Wohnnutzung:");
        filler63 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblAktivierbarkeit1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        cbAktivierbarkeit1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                MANAGEABLE_OPTION);
        jFormattedTextField1 = new javax.swing.JFormattedTextField();
        filler64 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblEntwicklungsausssichten = new javax.swing.JLabel();
        cbEntwicklungsaussichten = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                MANAGEABLE_OPTION);
        filler65 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblVerfuegbarkeit1 = new javax.swing.JLabel();
        cbVerfuegbarkeit1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                MANAGEABLE_OPTION);
        filler66 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblAktivierbarkeit = new javax.swing.JLabel();
        cbAktivierbarkeit = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                MANAGEABLE_OPTION);
        filler67 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblHandlungsdruck1 = new javax.swing.JLabel();
        cbHandlungsdruck1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                MANAGEABLE_OPTION);
        filler68 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblEntwicklungsausssichten4 = new javax.swing.JLabel();
        filler72 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        defaultBindableCheckboxField8 = new de.cismet.cids.editors.DefaultBindableLabelsPanel(
                isEditable(),
                "Empfohlene Nutzung:");
        filler69 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblRevitalisierung1 = new javax.swing.JLabel();
        cbRevitalisierung1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                MANAGEABLE_OPTION);
        filler70 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 24),
                new java.awt.Dimension(0, 24),
                new java.awt.Dimension(32767, 24));
        lblHandlungsdruck = new javax.swing.JLabel();
        cbHandlungsdruck = new de.cismet.cids.editors.DefaultBindableReferenceCombo(NULLABLE_OPTION, MANAGEABLE_OPTION);
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(200, 0),
                new java.awt.Dimension(200, 0),
                new java.awt.Dimension(200, 0));
        filler12 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0));

        dlgFlaeche.setTitle("Beschreibung der Fläche");
        dlgFlaeche.setModal(true);
        dlgFlaeche.setSize(new java.awt.Dimension(600, 400));

        panFlaeche.setMaximumSize(new java.awt.Dimension(300, 120));
        panFlaeche.setMinimumSize(new java.awt.Dimension(300, 120));
        panFlaeche.setPreferredSize(new java.awt.Dimension(300, 120));
        panFlaeche.setLayout(new java.awt.GridBagLayout());

        panMenButtons4.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(btnMenAbort4, "Abbrechen");
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

        org.openide.awt.Mnemonics.setLocalizedText(btnMenOk4, "Ok");
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

        dlgMassnahme.setTitle("Notwendige Maßnahmen");
        dlgMassnahme.setModal(true);
        dlgMassnahme.setSize(new java.awt.Dimension(600, 400));

        panFlaeche1.setMaximumSize(new java.awt.Dimension(300, 120));
        panFlaeche1.setMinimumSize(new java.awt.Dimension(300, 120));
        panFlaeche1.setPreferredSize(new java.awt.Dimension(300, 120));
        panFlaeche1.setLayout(new java.awt.GridBagLayout());

        panMenButtons5.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(btnMenAbort5, "Abbrechen");
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

        org.openide.awt.Mnemonics.setLocalizedText(btnMenOk5, "Ok");
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
        org.openide.awt.Mnemonics.setLocalizedText(lblBeschreibungTitle, "Allgemein");
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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel3.add(filler74, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblNummer, "Nummer:");
        lblNummer.setName(PotenzialflaecheReportServerAction.Property.NUMMER.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel3.add(lblNummer, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.nummer}"),
                txtNummer,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel3.add(txtNummer, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel3.add(filler48, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblBezeichnung, "Bezeichnung:");
        lblBezeichnung.setName(PotenzialflaecheReportServerAction.Property.BEZEICHNUNG.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel3.add(lblBezeichnung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
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
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 3.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel3.add(txtBezeichnung, gridBagConstraints);

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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(filler49, gridBagConstraints);
        filler49.setVisible(isEditable());

        org.openide.awt.Mnemonics.setLocalizedText(lblGeometrie5, "Geometrie:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
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
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(cbGeom, gridBagConstraints);
        cbGeom.setVisible(isEditable());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(filler50, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblFlaechengroesse, "Flächengröße:");
        lblFlaechengroesse.setName(PotenzialflaecheReportServerAction.Property.GROESSE.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblFlaechengroesse, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(lblFlaechengroesseWert, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(filler51, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblStadtbezirk, "Stadtbezirk(e):");
        lblStadtbezirk.setName(PotenzialflaecheReportServerAction.Property.STADTBEZIRK.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblStadtbezirk, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel7.add(filler58, gridBagConstraints);

        searchLabelsFieldPanel2.setOpaque(false);
        searchLabelsFieldPanel2.setLayout(new java.awt.FlowLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(searchLabelsFieldPanel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(filler52, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblQuartiere, "Quartier(e):");
        lblQuartiere.setName(PotenzialflaecheReportServerAction.Property.QUARTIER.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblQuartiere, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel7.add(filler56, gridBagConstraints);

        searchLabelsFieldPanel1.setOpaque(false);
        searchLabelsFieldPanel1.setLayout(new java.awt.FlowLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(searchLabelsFieldPanel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(filler53, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblQuartiere1, "Flurstück(e):");
        lblQuartiere1.setName(PotenzialflaecheReportServerAction.Property.FLURSTUECKE.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblQuartiere1, gridBagConstraints);

        jButton1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/icon-explorerwindow.png"))); // NOI18N
        jButton1.setBorderPainted(false);
        jButton1.setContentAreaFilled(false);
        jButton1.setFocusPainted(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(jButton1, gridBagConstraints);

        monSearchResultsList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        monSearchResultsList1.setVisibleRowCount(3);
        jScrollPane1.setViewportView(monSearchResultsList1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(jScrollPane1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(filler54, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblStadtbezirk5, "Eigentümer:");
        lblStadtbezirk5.setName(PotenzialflaecheReportServerAction.Property.EIGENTUEMER.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblStadtbezirk5, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel7.add(filler59, gridBagConstraints);

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
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(defaultBindableCheckboxField3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel7.add(filler55, gridBagConstraints);

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
        org.openide.awt.Mnemonics.setLocalizedText(lblLageTitle2, "Lagebeschreibung");
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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody2.add(filler19, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblLagetyp, "Lagebewertung Verkehr:");
        lblLagetyp.setName(PotenzialflaecheReportServerAction.Property.LAGEBEWERTUNG_VERKEHR.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody2.add(lblLagetyp, gridBagConstraints);

        jPanel13.setOpaque(false);
        jPanel13.setLayout(new java.awt.GridBagLayout());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_lagebewertung_verkehr}"),
                cbLagetyp,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel13.add(cbLagetyp, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel13.add(filler22, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblOepnv, "ÖPNV-Qualität:");
        lblOepnv.setName(PotenzialflaecheReportServerAction.Property.OEPNV_ANBINDUNG.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 5);
        jPanel13.add(lblOepnv, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_oepnv}"),
                cbOepnv,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel13.add(cbOepnv, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody2.add(jPanel13, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody2.add(filler20, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblLagetyp1, "Nähe zu:");
        lblLagetyp1.setName(PotenzialflaecheReportServerAction.Property.NAEHE_ZU.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody2.add(lblLagetyp1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        panLageBody2.add(filler26, gridBagConstraints);

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
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody2.add(defaultBindableCheckboxField11, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody2.add(filler21, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblLagetyp2, "Siedlungsräumliche Lage:");
        lblLagetyp2.setName(PotenzialflaecheReportServerAction.Property.SIEDLUNGSRAEUMLICHE_LAGE.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
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
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody2.add(cbLagetyp2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody2.add(filler24, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblTopografie, "Topografie:");
        lblTopografie.setName(PotenzialflaecheReportServerAction.Property.TOPOGRAFIE.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
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
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(cbTopografie, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblTopografie1, "Hangrichtung:");
        lblTopografie1.setName(PotenzialflaecheReportServerAction.Property.HANG.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 5);
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
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(cbTopografie1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody2.add(jPanel4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
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
            "Planungsrecht/Bauplanungrecht/Bauordnungsrecht");
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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMessstellenausbauBody.add(filler27, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblFlaechennutzung1, "Regionalplan:");
        lblFlaechennutzung1.setName(PotenzialflaecheReportServerAction.Property.REGIONALPLAN.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panMessstellenausbauBody.add(lblFlaechennutzung1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panMessstellenausbauBody.add(filler34, gridBagConstraints);

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
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMessstellenausbauBody.add(defaultBindableCheckboxField9, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMessstellenausbauBody.add(filler28, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblFlaechennutzung, "Flächennutzungplan:");
        lblFlaechennutzung.setName(PotenzialflaecheReportServerAction.Property.FLAECHENNUTZUNGSPLAN.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panMessstellenausbauBody.add(lblFlaechennutzung, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panMessstellenausbauBody.add(filler35, gridBagConstraints);

        searchLabelsFieldPanel6.setOpaque(false);
        searchLabelsFieldPanel6.setLayout(new java.awt.FlowLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMessstellenausbauBody.add(searchLabelsFieldPanel6, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMessstellenausbauBody.add(filler29, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblFlaechennutzung2, "Bebauungspläne:");
        lblFlaechennutzung2.setName(PotenzialflaecheReportServerAction.Property.BEBAUUNGSPLAN.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panMessstellenausbauBody.add(lblFlaechennutzung2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panMessstellenausbauBody.add(filler36, gridBagConstraints);

        searchLabelsFieldPanel4.setOpaque(false);
        searchLabelsFieldPanel4.setLayout(new java.awt.FlowLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMessstellenausbauBody.add(searchLabelsFieldPanel4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMessstellenausbauBody.add(filler30, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblFlaechennutzung3, "Festsetzungen:");
        lblFlaechennutzung3.setName(PotenzialflaecheReportServerAction.Property.FESTSETZUNGEN_BPLAN.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panMessstellenausbauBody.add(lblFlaechennutzung3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panMessstellenausbauBody.add(filler33, gridBagConstraints);

        jPanel12.setOpaque(false);
        jPanel12.setLayout(new java.awt.GridBagLayout());

        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(2);
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
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel12.add(jScrollPane3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel12.add(filler32, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, "Stand:");
        jLabel1.setName(PotenzialflaecheReportServerAction.Property.FESTSETZUNGEN_BPLAN_STAND.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel12.add(jLabel1, gridBagConstraints);

        dateStand1.setToolTipText("Pflichtfeld");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.stand_festsetzungen_bplan}"),
                dateStand1,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel12.add(dateStand1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel12.add(filler2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMessstellenausbauBody.add(jPanel12, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMessstellenausbauBody.add(filler31, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblVorhandeneBebauung1, "Bauordnungsrecht:");
        lblVorhandeneBebauung1.setName(PotenzialflaecheReportServerAction.Property.BAUORDNUNGSRECHT_GENEHMIGUNG
                    .toString() + ";"
                    + PotenzialflaecheReportServerAction.Property.BAUORDNUNGSRECHT_BAULAST.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
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
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMessstellenausbauBody.add(jPanel5, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
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
        org.openide.awt.Mnemonics.setLocalizedText(lblLageTitle3, "Erweiterte Informationen");
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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(filler39, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblStadtbezirk4, "Umgebungsnutzung:");
        lblStadtbezirk4.setName(PotenzialflaecheReportServerAction.Property.UMGEBUNGSNUTZUNG.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody3.add(lblStadtbezirk4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panLageBody3.add(filler10, gridBagConstraints);

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
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(defaultBindableCheckboxField6, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(filler40, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblStadtbezirk6, "Brachfläche:");
        lblStadtbezirk6.setName(PotenzialflaecheReportServerAction.Property.BRACHFLAECHENKATEGORIE.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody3.add(lblStadtbezirk6, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panLageBody3.add(filler11, gridBagConstraints);

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
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel9.add(defaultBindableCheckboxField1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel9.add(filler38, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblNutzungsaufgabe, "Nutzungsaufgabe:");
        lblNutzungsaufgabe.setName(PotenzialflaecheReportServerAction.Property.JAHR_NUTZUNGSAUFGABE.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
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
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel9.add(txtJahrNutzungsaufgabe, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel9.add(filler5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panLageBody3.add(jPanel9, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(filler41, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblStadtbezirk3, "Bisherige Nutzung:");
        lblStadtbezirk3.setName(PotenzialflaecheReportServerAction.Property.BISHERIGE_NUTZUNG.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody3.add(lblStadtbezirk3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panLageBody3.add(filler13, gridBagConstraints);

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
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(defaultBindableCheckboxField5, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(filler42, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblVorhandeneBebauung, "Bestand Bebauung:");
        lblVorhandeneBebauung.setName(PotenzialflaecheReportServerAction.Property.VORHANDENE_BEBAUUNG.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
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
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(cbVorhandeneBebauung, gridBagConstraints);

        jPanel16.setOpaque(false);
        jPanel16.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel16.add(filler43, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblVorhandeneBebauung2, "Bestand Versiegelung:");
        lblVorhandeneBebauung2.setName(PotenzialflaecheReportServerAction.Property.VERSIEGELUNG.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 5);
        jPanel16.add(lblVorhandeneBebauung2, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_versiegelung}"),
                cbVorhandeneBebauung3,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel16.add(cbVorhandeneBebauung3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(jPanel16, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(filler44, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblAessereErschl, "Äußere Erschließung:");
        lblAessereErschl.setName(PotenzialflaecheReportServerAction.Property.AEUSSERE_ERSCHLIESSUNG.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
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
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(cbAeussereErschluessung, gridBagConstraints);

        jPanel17.setOpaque(false);
        jPanel17.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel17.add(filler75, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblVorhandeneBebauung3, "Bodenrichtwert(e):");
        lblVorhandeneBebauung3.setName(PotenzialflaecheReportServerAction.Property.BODENRICHTWERTE.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 5);
        jPanel17.add(lblVorhandeneBebauung3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel17.add(filler9, gridBagConstraints);

        monSearchResultsList2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        monSearchResultsList2.setVisibleRowCount(3);
        jScrollPane4.setViewportView(monSearchResultsList2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel17.add(jScrollPane4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panLageBody3.add(jPanel17, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(filler46, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblStadtbezirk2, "Wohnlagen:");
        lblStadtbezirk2.setName(PotenzialflaecheReportServerAction.Property.WOHNLAGEN.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody3.add(lblStadtbezirk2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panLageBody3.add(filler14, gridBagConstraints);

        searchLabelsFieldPanel3.setOpaque(false);
        searchLabelsFieldPanel3.setLayout(new java.awt.FlowLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(searchLabelsFieldPanel3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(filler45, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblStadtbezirk1, "Stadtraumtypen:");
        lblStadtbezirk1.setName(PotenzialflaecheReportServerAction.Property.STADTRAUMTYPEN.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody3.add(lblStadtbezirk1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panLageBody3.add(filler16, gridBagConstraints);

        searchLabelsFieldPanel5.setOpaque(false);
        searchLabelsFieldPanel5.setLayout(new java.awt.FlowLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(searchLabelsFieldPanel5, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(filler47, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblAessereErschl1, "Klimainformationen:");
        lblAessereErschl1.setName(PotenzialflaecheReportServerAction.Property.KLIMAINFORMATIONEN.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
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
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(cbAeussereErschluessung1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
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
        org.openide.awt.Mnemonics.setLocalizedText(lblBeschreibungTitle3, "Beschreibung der Fläche / Sachstand");
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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel6.add(filler73, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblQuelle, "Quelle:");
        lblQuelle.setName(PotenzialflaecheReportServerAction.Property.QUELLE.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
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
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 10);
        jPanel6.add(jTextField1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblStand, "Stand:");
        lblStand.setName(PotenzialflaecheReportServerAction.Property.STAND.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 5, 5);
        jPanel6.add(lblStand, gridBagConstraints);

        dateStand.setToolTipText("Pflichtfeld");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.stand}"),
                dateStand,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(sqlDateToUtilDateConverter);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
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
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel15.add(panAllgemein3, gridBagConstraints);

        panAllgemein6.setLayout(new java.awt.GridBagLayout());

        panBeschreibungTitle6.setBackground(java.awt.Color.darkGray);
        panBeschreibungTitle6.setLayout(new java.awt.GridBagLayout());

        lblBeschreibungTitle6.setFont(lblBeschreibungTitle6.getFont());
        lblBeschreibungTitle6.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(lblBeschreibungTitle6, "Notwendige Maßnahmen / Nächste Schritte");
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
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel15.add(panAllgemein6, gridBagConstraints);

        panBewertung1.setLayout(new java.awt.GridBagLayout());

        panLageTitle5.setBackground(java.awt.Color.darkGray);
        panLageTitle5.setLayout(new java.awt.GridBagLayout());

        lblLageTitle6.setFont(lblLageTitle6.getFont());
        lblLageTitle6.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(lblLageTitle6, "Bewertung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panLageTitle5.add(lblLageTitle6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panBewertung1.add(panLageTitle5, gridBagConstraints);

        panLageBody5.setOpaque(false);
        panLageBody5.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panLageBody5.add(filler57, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblEntwicklungsausssichten1, "Potenzialart:");
        lblEntwicklungsausssichten1.setName(PotenzialflaecheReportServerAction.Property.POTENZIALART.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
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
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody5.add(cbEntwicklungsaussichten1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panLageBody5.add(filler60, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblEntwicklungsausssichten2, "Entwicklungsstand:");
        lblEntwicklungsausssichten2.setName(PotenzialflaecheReportServerAction.Property.ENTWICKLUNGSSTAND.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
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
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody5.add(cbEntwicklungsaussichten2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panLageBody5.add(filler61, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblEntwicklungsausssichten3, "Restriktionen/Hemmnisse:");
        lblEntwicklungsausssichten3.setName(PotenzialflaecheReportServerAction.Property.RESTRIKTIONEN.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody5.add(lblEntwicklungsausssichten3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panLageBody5.add(filler37, gridBagConstraints);

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
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody5.add(defaultBindableCheckboxField4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panLageBody5.add(filler62, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblEntwicklungsausssichten5, "Empfohlene Art der Wohnnutzung:");
        lblEntwicklungsausssichten5.setName(PotenzialflaecheReportServerAction.Property.EMPFOHLENE_NUTZUNGEN
                    .toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody5.add(lblEntwicklungsausssichten5, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panLageBody5.add(filler71, gridBagConstraints);

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
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody5.add(defaultBindableCheckboxField10, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panLageBody5.add(filler63, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblAktivierbarkeit1, "Anzahl mögl. Wohneinheiten:");
        lblAktivierbarkeit1.setName(PotenzialflaecheReportServerAction.Property.WOHNEINHEITEN.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody5.add(lblAktivierbarkeit1, gridBagConstraints);

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridBagLayout());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_wohneinheiten}"),
                cbAktivierbarkeit1,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel2.add(cbAktivierbarkeit1, gridBagConstraints);

        jFormattedTextField1.setColumns(5);
        jFormattedTextField1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
                new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.anzahl_wohneinheiten}"),
                jFormattedTextField1,
                org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel2.add(jFormattedTextField1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panLageBody5.add(jPanel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panLageBody5.add(filler64, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblEntwicklungsausssichten, "Entwicklungsaussichten:");
        lblEntwicklungsausssichten.setName(PotenzialflaecheReportServerAction.Property.ENTWICKLUNGSAUSSSICHTEN
                    .toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
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
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody5.add(cbEntwicklungsaussichten, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panLageBody5.add(filler65, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblVerfuegbarkeit1, "Verfügbarkeit:");
        lblVerfuegbarkeit1.setName(PotenzialflaecheReportServerAction.Property.VERFUEGBBARKEIT.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody5.add(lblVerfuegbarkeit1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.verfuegbarkeit}"),
                cbVerfuegbarkeit1,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody5.add(cbVerfuegbarkeit1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panLageBody5.add(filler66, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblAktivierbarkeit, "Verwertbarkeit:");
        lblAktivierbarkeit.setName(PotenzialflaecheReportServerAction.Property.VERWERTBARKEIT.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody5.add(lblAktivierbarkeit, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.aktivierbarkeit}"),
                cbAktivierbarkeit,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody5.add(cbAktivierbarkeit, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panLageBody5.add(filler67, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblHandlungsdruck1, "Handlungspriorität (Verwaltung):");
        lblHandlungsdruck1.setToolTipText("Handlungsdruck / Handlungspriorität");
        lblHandlungsdruck1.setName(PotenzialflaecheReportServerAction.Property.HANDLUNGSDRUCK.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody5.add(lblHandlungsdruck1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_handlungsprioritaet}"),
                cbHandlungsdruck1,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody5.add(cbHandlungsdruck1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panLageBody5.add(filler68, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblEntwicklungsausssichten4, "Empfohlene Nutzung:");
        lblEntwicklungsausssichten4.setName(PotenzialflaecheReportServerAction.Property.EMPFOHLENE_NUTZUNGEN
                    .toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody5.add(lblEntwicklungsausssichten4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panLageBody5.add(filler72, gridBagConstraints);

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
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody5.add(defaultBindableCheckboxField8, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panLageBody5.add(filler69, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblRevitalisierung1, "Revitalisierung:");
        lblRevitalisierung1.setName(PotenzialflaecheReportServerAction.Property.REVITALISIERUNG.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody5.add(lblRevitalisierung1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_revitalisierung}"),
                cbRevitalisierung1,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody5.add(cbRevitalisierung1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panLageBody5.add(filler70, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblHandlungsdruck, "Handlungsdruck:");
        lblHandlungsdruck.setToolTipText("Handlungsdruck / Handlungspriorität");
        lblHandlungsdruck.setName(PotenzialflaecheReportServerAction.Property.HANDLUNGSDRUCK.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody5.add(lblHandlungsdruck, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.handlungsdruck}"),
                cbHandlungsdruck,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody5.add(cbHandlungsdruck, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
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
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel15.add(panBewertung1, gridBagConstraints);
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
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        final JDialog dialog = new JDialog((Frame)null,
                "Flurstücke, Buchungsblätter und Eigentümerinformationen",
                true);
        final Collection<MetaObjectNode> mons = new ArrayList<>();
        final DefaultListModel<MetaObjectNode> model = (DefaultListModel<MetaObjectNode>)
            monSearchResultsList1.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            final MetaObjectNode mon = model.get(i);
            mons.add(mon);
        }
        dialog.setContentPane(new DescriptionPaneDialogWrapperPanel(mons));
        dialog.setSize(1200, 800);
        StaticSwingTools.showDialog(this, dialog, true);
    }                                                                            //GEN-LAST:event_jButton1ActionPerformed

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
        javax.swing.ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);

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

            if ((getLastKampagne() != null) && isEditable()) {
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
            new SwingWorker<Map<String, String>, Void>() {

                    @Override
                    protected Map<String, String> doInBackground() throws Exception {
                        return createDefinitionTooltips();
                    }

                    @Override
                    protected void done() {
                        try {
                            final Map<String, String> tooltips = get();
                            if (tooltips != null) {
                                for (final String name : tooltips.keySet()) {
                                    final JComponent component = componentMap.get(name);
                                    if (component != null) {
                                        final String tooltip;
                                        if (component.getName().contains(";")) {
                                            final StringBuffer sb = new StringBuffer();
                                            for (final String subName : component.getName().split(";")) {
                                                sb.append(tooltips.get(subName));
                                            }
                                            tooltip = sb.toString();
                                        } else {
                                            tooltip = tooltips.get(name);
                                        }

                                        component.setToolTipText((tooltip != null)
                                                ? String.format(
                                                    "<html><body>%s</body></html>",
                                                    tooltip) : null);
                                    }
                                }
                            }
                        } catch (final Exception ex) {
                            LOG.error(ex, ex);
                        }
                    }
                }.execute();
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
                        if (name.contains(";")) {
                            for (final String subName : name.split(";")) {
                                componentMap.put(subName, component);
                            }
                        } else {
                            componentMap.put(name, component);
                        }
                    }
                }
            } catch (final Exception ex) {
                LOG.warn("Cannot process field", ex);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   metaClass  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  CacheException  DOCUMENT ME!
     */
    private List<CidsBean> getMosForSchluesseltabelle(final MetaClass metaClass) throws CacheException {
        final String query = PfSchluesseltabelleEditor.createQuery(metaClass);

        final List<CidsBean> beans = new ArrayList<>();
        final MetaObject[] mos = MetaObjectCache.getInstance()
                    .getMetaObjectsByQuery(query, metaClass, false, getConnectionContext());
        if (mos != null) {
            for (final MetaObject mo : mos) {
                if (mo != null) {
                    beans.add(mo.getBean());
                }
            }
        }

        return beans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Map<String, String> createDefinitionTooltips() {
        final Map<String, String> tooltips = new HashMap<>();
        for (final String name : componentMap.keySet()) {
            if (name != null) {
                final JComponent component = componentMap.get(name);
                if (component != null) {
                    tooltips.put(name, createDefinitionHtml(name));
                }
            }
        }
        return tooltips;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   propertyName  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String createDefinitionHtml(final String propertyName) {
        try {
            final MetaObject metaObject = getCidsBean().getMetaObject();
            final PotenzialflaecheReportServerAction.Property property = PotenzialflaecheReportServerAction.Property
                        .valueOf(propertyName);
            if (property.getValue() instanceof PotenzialflaecheReportServerAction.SingleFieldReportProperty) {
                final String path = ((PotenzialflaecheReportServerAction.SingleFieldReportProperty)property.getValue())
                            .getPath();
                final MemberAttributeInfo mai = metaObject.getAttributeByFieldName(path).getMai();

                final String domain = metaObject.getDomain();
                final MetaClass metaClass;
                final MetaClass foreignClass = CidsObjectEditorFactory.getMetaClass(
                        domain,
                        mai.getForeignKeyClassId(),
                        getConnectionContext());
                if (mai.isArray()) {
                    MetaClass detailClass = null;
                    for (final MemberAttributeInfo arrayMai
                                : new ArrayList<MemberAttributeInfo>(foreignClass.getMemberAttributeInfos().values())) {
                        if (arrayMai.isForeignKey()) {
                            detailClass = getMetaClass(domain, arrayMai.getForeignKeyClassId(), getConnectionContext());
                            break;
                        }
                    }
                    metaClass = detailClass;
                } else {
                    metaClass = foreignClass;
                }

                if (metaClass != null) {
                    final StringBuffer sb = new StringBuffer(String.format("<h3>%s:</h3>", metaClass.getName()));
                    sb.append("<ul>");
                    final List<CidsBean> schluesseltabellenBeans = getMosForSchluesseltabelle(metaClass);
                    if (schluesseltabellenBeans != null) {
                        for (final CidsBean schluesseltabellenBean : schluesseltabellenBeans) {
                            if (schluesseltabellenBean != null) {
                                final String name = (String)schluesseltabellenBean.getProperty("name");
                                final String definition = (String)schluesseltabellenBean.getProperty("definition");
                                sb.append(String.format(
                                        "<li><b>%s:</b> %s</li>",
                                        (name != null) ? name : "-",
                                        (definition != null) ? definition : "-"));
                            }
                        }
                    }
                    sb.append("</ul>");
                    return sb.toString();
                }
            }
            return null;
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
        return null;
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

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public class DescriptionPaneDialogWrapperPanel extends JPanel
            implements AlkisLandparcelAggregationRenderer.AlkisEigentuemerDescriptionPaneParent {

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
}
