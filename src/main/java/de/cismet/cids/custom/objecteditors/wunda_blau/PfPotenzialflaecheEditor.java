/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objecteditors.wunda_blau;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.tools.CacheException;
import Sirius.navigator.tools.MetaObjectCache;
import Sirius.navigator.ui.DescriptionPaneFS;
import Sirius.navigator.ui.RequestsFullAvailableSpaceComponent;
import Sirius.navigator.ui.RequestsFullSizeComponent;

import Sirius.server.localserver.attribute.MemberAttributeInfo;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.commons.lang.StringUtils;

import org.jdesktop.beansbinding.Converter;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.commons.gui.ScrollablePanel;
import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.alkis.ClientAlkisConf;
import de.cismet.cids.custom.objectrenderer.wunda_blau.AlkisLandparcelAggregationRenderer;
import de.cismet.cids.custom.wunda_blau.search.actions.PotenzialflaecheReportServerAction;
import de.cismet.cids.custom.wunda_blau.search.server.GeometrySearch;
import de.cismet.cids.custom.wunda_blau.search.server.KstGeometryMonSearch;
import de.cismet.cids.custom.wunda_blau.search.server.PfPotenzialflaecheNextSchluesselServerSearch;
import de.cismet.cids.custom.wunda_blau.search.server.RestApiMonGeometrySearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.CidsObjectEditorFactory;
import de.cismet.cids.editors.DefaultBindableLabelsPanel;
import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveWithoutCloseListener;
import de.cismet.cids.editors.EditorSavedEvent;
import de.cismet.cids.editors.SearchLabelsFieldPanel;

import de.cismet.cids.navigator.utils.CidsBeanDropListener;
import de.cismet.cids.navigator.utils.CidsBeanDropTarget;
import de.cismet.cids.navigator.utils.ClassCacheMultiple;

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

import de.cismet.cismap.navigatorplugin.CidsFeature;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.gui.BorderProvider;
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
    RequestsFullSizeComponent,
    RequestsFullAvailableSpaceComponent,
    BorderProvider,
    EditorSaveWithoutCloseListener,
    FooterComponentProvider,
    TitleComponentProvider,
    ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            PfPotenzialflaecheEditor.class);

    private static DefaultBindableReferenceCombo.Option NULLABLE_OPTION =
        new DefaultBindableReferenceCombo.NullableOption(null, "-");
    private static DefaultBindableReferenceCombo.Option SORTING_OPTION =
        new DefaultBindableReferenceCombo.SortingColumnOption("order_by");
    private static final String PREFIX_LABEL = "LABEL:";
    private static final String PREFIX_INPUT = "INPUT:";

    //~ Instance fields --------------------------------------------------------

    private final Point tooltipDialogPosition = new Point();

    private final PfPotenzialflaecheTitlePanel panTitle = new PfPotenzialflaecheTitlePanel(this);
    private final PfPotenzialflaecheFooterPanel panFooter = new PfPotenzialflaecheFooterPanel(this);
    private final boolean editable;
    private CidsBean cidsBean;
    private ConnectionContext connectionContext;

    private final Collection<SearchLabelsFieldPanel> searchLabelFieldPanels = new ArrayList<>();
    private final Collection<DefaultBindableLabelsPanel> labelsPanels = new ArrayList<>();

    private final Collection<JComponent> labelComponents = new ArrayList<>();
    private final Collection<JComponent> inputComponents = new ArrayList<>();
    private final Map<JComponent, List<String>> componentToPropertiesMap = new HashMap<>();
    private final Map<String, MetaClass> pathToMetaClassMap = new HashMap<>();
    private final Map<MetaClass, String> definitions = new HashMap<>();

    private final List<String> usedProperties = new ArrayList();

    private final List<CidsBean> schluesseltabellenBeans = new ArrayList<>();

    private final ComponentListener dialogComponentListener = new ComponentAdapter() {

            @Override
            public void componentResized(final ComponentEvent e) {
                if (dlgFlaeche.equals(e.getSource())) {
                    dlgFlaeche.doLayout();
                    taFlaecheDialog.setSize(dlgFlaeche.getWidth() - 5, taFlaecheDialog.getHeight());
                } else if (dlgMassnahme.equals(e.getSource())) {
                    dlgMassnahme.doLayout();
                    taMassnahmeDialog.setSize(
                        dlgMassnahme.getWidth()
                                - 5,
                        taMassnahmeDialog.getHeight());
                }
            }
        };

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFlaeche;
    private javax.swing.JButton btnMassnahmen;
    private javax.swing.JButton btnMassnahmen1;
    private javax.swing.JButton btnMenAbort4;
    private javax.swing.JButton btnMenAbort5;
    private javax.swing.JButton btnMenAbort6;
    private javax.swing.JButton btnMenOk4;
    private javax.swing.JButton btnMenOk5;
    private javax.swing.JButton btnMenOk6;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbAeussereErschluessung;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbAeussereErschluessung1;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbAktivierbarkeit;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbAktivierbarkeit1;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbEntwicklungsaussichten;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbEntwicklungsaussichten1;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbEntwicklungsaussichten2;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbEntwicklungsaussichten3;
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
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbVorhandeneBebauung1;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbVorhandeneBebauung2;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbVorhandeneBebauung3;
    private de.cismet.cids.editors.DefaultBindableDateChooser dateStand;
    private de.cismet.cids.editors.DefaultBindableDateChooser dateStand1;
    private de.cismet.cids.editors.DefaultBindableDateChooser dateStand2;
    private de.cismet.cids.editors.DefaultBindableLabelsPanel defaultBindableCheckboxField1;
    private de.cismet.cids.editors.DefaultBindableLabelsPanel defaultBindableCheckboxField10;
    private de.cismet.cids.editors.DefaultBindableLabelsPanel defaultBindableCheckboxField11;
    private de.cismet.cids.editors.DefaultBindableLabelsPanel defaultBindableCheckboxField3;
    private de.cismet.cids.editors.DefaultBindableLabelsPanel defaultBindableCheckboxField4;
    private de.cismet.cids.editors.DefaultBindableLabelsPanel defaultBindableCheckboxField5;
    private de.cismet.cids.editors.DefaultBindableLabelsPanel defaultBindableCheckboxField6;
    private de.cismet.cids.editors.DefaultBindableLabelsPanel defaultBindableCheckboxField8;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo defaultBindableReferenceCombo1;
    private javax.swing.JDialog dlgArchivieren;
    private javax.swing.JDialog dlgFlaeche;
    private javax.swing.JDialog dlgInterneHinweise;
    private javax.swing.JDialog dlgMassnahme;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler11;
    private javax.swing.Box.Filler filler12;
    private javax.swing.Box.Filler filler13;
    private javax.swing.Box.Filler filler14;
    private javax.swing.Box.Filler filler15;
    private javax.swing.Box.Filler filler16;
    private javax.swing.Box.Filler filler17;
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
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler70;
    private javax.swing.Box.Filler filler71;
    private javax.swing.Box.Filler filler72;
    private javax.swing.Box.Filler filler73;
    private javax.swing.Box.Filler filler74;
    private javax.swing.Box.Filler filler75;
    private javax.swing.Box.Filler filler76;
    private javax.swing.Box.Filler filler77;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JLabel lblAessereErschl;
    private javax.swing.JLabel lblAessereErschl1;
    private javax.swing.JLabel lblAktivierbarkeit;
    private javax.swing.JLabel lblAktivierbarkeit1;
    private javax.swing.JLabel lblBeschreibungTitle;
    private javax.swing.JLabel lblBeschreibungTitle3;
    private javax.swing.JLabel lblBeschreibungTitle6;
    private javax.swing.JLabel lblBeschreibungTitle7;
    private javax.swing.JLabel lblBezeichnung;
    private javax.swing.JLabel lblEntwicklungsausssichten;
    private javax.swing.JLabel lblEntwicklungsausssichten1;
    private javax.swing.JLabel lblEntwicklungsausssichten2;
    private javax.swing.JLabel lblEntwicklungsausssichten3;
    private javax.swing.JLabel lblEntwicklungsausssichten4;
    private javax.swing.JLabel lblEntwicklungsausssichten5;
    private javax.swing.JLabel lblEntwicklungsausssichten6;
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
    private de.cismet.cids.custom.objecteditors.utils.CidsSearchResultsList<MetaObjectNode> monSearchResultsList3;
    private de.cismet.tools.gui.RoundedPanel panAllgemein;
    private de.cismet.tools.gui.RoundedPanel panAllgemein3;
    private de.cismet.tools.gui.RoundedPanel panAllgemein6;
    private de.cismet.tools.gui.RoundedPanel panAllgemein7;
    private javax.swing.JPanel panArtControls3;
    private javax.swing.JPanel panArtControls4;
    private javax.swing.JPanel panArtControls5;
    private de.cismet.tools.gui.SemiRoundedPanel panBeschreibungTitle;
    private de.cismet.tools.gui.SemiRoundedPanel panBeschreibungTitle3;
    private de.cismet.tools.gui.SemiRoundedPanel panBeschreibungTitle6;
    private de.cismet.tools.gui.SemiRoundedPanel panBeschreibungTitle7;
    private de.cismet.tools.gui.RoundedPanel panBewertung1;
    private javax.swing.JPanel panDefinition;
    private javax.swing.JPanel panDetail;
    private javax.swing.JDialog panDialog;
    private de.cismet.tools.gui.RoundedPanel panErweitert;
    private javax.swing.JPanel panFlaeche;
    private javax.swing.JPanel panFlaeche1;
    private javax.swing.JPanel panFlaeche2;
    private javax.swing.JPanel panLageBody2;
    private javax.swing.JPanel panLageBody3;
    private javax.swing.JPanel panLageBody5;
    private de.cismet.tools.gui.SemiRoundedPanel panLageTitle2;
    private de.cismet.tools.gui.SemiRoundedPanel panLageTitle3;
    private de.cismet.tools.gui.SemiRoundedPanel panLageTitle5;
    private javax.swing.JPanel panMain;
    private javax.swing.JPanel panMenButtons4;
    private javax.swing.JPanel panMenButtons5;
    private javax.swing.JPanel panMenButtons6;
    private javax.swing.JPanel panMessstellenausbauBody;
    private de.cismet.tools.gui.SemiRoundedPanel panMessstellenausbauTitle;
    private javax.swing.JPanel panNew;
    private de.cismet.tools.gui.RoundedPanel panPlanungsrecht;
    private de.cismet.tools.gui.RoundedPanel panStandortdaten;
    private de.cismet.cids.editors.SearchLabelsFieldPanel searchLabelsFieldPanel1;
    private de.cismet.cids.editors.SearchLabelsFieldPanel searchLabelsFieldPanel2;
    private de.cismet.cids.editors.SearchLabelsFieldPanel searchLabelsFieldPanel3;
    private de.cismet.cids.editors.SearchLabelsFieldPanel searchLabelsFieldPanel5;
    private de.cismet.cids.editors.SearchLabelsFieldPanel searchLabelsFieldPanel6;
    private de.cismet.cids.editors.SearchLabelsFieldPanel searchLabelsFieldPanel7;
    private de.cismet.cids.editors.converters.SqlDateToUtilDateConverter sqlDateToUtilDateConverter;
    private javax.swing.JTextArea taFlaeche;
    private javax.swing.JTextArea taFlaecheDialog;
    private javax.swing.JTextArea taMassnahmeDialog;
    private javax.swing.JTextArea taMassnahmeDialog1;
    private javax.swing.JTextArea taNotwendigeMassnahme;
    private javax.swing.JTextArea taNotwendigeMassnahme1;
    private javax.swing.JTextField txtBezeichnung;
    private javax.swing.JEditorPane txtDefinition;
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

    /**
     * DOCUMENT ME!
     */
    private void initSchluesseltabellenBeans() {
        try {
            final MetaClass ST_MC = ClassCacheMultiple.getMetaClass(
                    CidsBeanSupport.DOMAIN_NAME,
                    "pf_schluesseltabelle",
                    getConnectionContext());
            final String query = String.format(
                    "SELECT %d, %s FROM %s",
                    ST_MC.getID(),
                    ST_MC.getPrimaryKey(),
                    ST_MC.getTableName());
            final MetaObject[] mos = SessionManager.getProxy().getMetaObjectByQuery(query, 0, getConnectionContext());
            final List<CidsBean> beans = new ArrayList<>();
            if (mos != null) {
                for (final MetaObject mo : mos) {
                    if (mo != null) {
                        beans.add(mo.getBean());
                    }
                }
            }
            schluesseltabellenBeans.addAll(beans);

            initPropertyToMetaClassMap();
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;

        searchLabelFieldPanels.clear();
        labelsPanels.clear();

        initComponents();

        initComponentToPropertiesMap();
        initSchluesseltabellenBeans();

        final ActionListener escListener = new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    panDialog.setVisible(false);
                }
            };

        panDialog.getRootPane()
                .registerKeyboardAction(
                    escListener,
                    KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                    JComponent.WHEN_IN_FOCUSED_WINDOW);

        try {
            new CidsBeanDropTarget(jPanel20);
        } catch (final Exception ex) {
            LOG.warn("Error while creating CidsBeanDropTarget", ex); // NOI18N
        }

        RendererTools.makeUneditable(monSearchResultsList1, true);
        RendererTools.makeUneditable(monSearchResultsList2, true);
        RendererTools.makeUneditable(monSearchResultsList3, true);
        RendererTools.makeUneditable(txtNummer, true);

        searchLabelFieldPanels.addAll(Arrays.asList(
                searchLabelsFieldPanel1,
                searchLabelsFieldPanel2,
                searchLabelsFieldPanel3,
                searchLabelsFieldPanel5,
                searchLabelsFieldPanel6,
                searchLabelsFieldPanel7));

        labelsPanels.addAll(Arrays.asList(
                defaultBindableCheckboxField1,
                defaultBindableCheckboxField3,
                defaultBindableCheckboxField4,
                defaultBindableCheckboxField5,
                defaultBindableCheckboxField6,
                defaultBindableCheckboxField8,
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

        dlgFlaeche.addComponentListener(dialogComponentListener);
        dlgMassnahme.addComponentListener(dialogComponentListener);

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
        panDialog = new javax.swing.JDialog();
        panDefinition = new javax.swing.JPanel();
        jPanel23 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jScrollPane9 = new javax.swing.JScrollPane();
        txtDefinition = new javax.swing.JEditorPane();
        dlgInterneHinweise = new javax.swing.JDialog();
        panFlaeche2 = new javax.swing.JPanel();
        panMenButtons6 = new javax.swing.JPanel();
        btnMenAbort6 = new javax.swing.JButton();
        btnMenOk6 = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        taMassnahmeDialog1 = new javax.swing.JTextArea();
        dlgArchivieren = new javax.swing.JDialog();
        jScrollPane5 = new javax.swing.JScrollPane();
        jPanel22 = new ScrollablePanel();
        panMain = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        panAllgemein = new de.cismet.tools.gui.RoundedPanel();
        panBeschreibungTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        lblBeschreibungTitle = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        filler74 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblNummer = new javax.swing.JLabel();
        jPanel28 = new javax.swing.JPanel();
        txtNummer = new javax.swing.JTextField();
        jCheckBox1 = new javax.swing.JCheckBox();
        jButton4 = new javax.swing.JButton();
        filler48 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblBezeichnung = new javax.swing.JLabel();
        txtBezeichnung = new javax.swing.JTextField();
        filler75 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        jLabel6 = new javax.swing.JLabel();
        jPanel27 = new javax.swing.JPanel();
        defaultBindableReferenceCombo1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                SORTING_OPTION);
        jLabel7 = new javax.swing.JLabel();
        dateStand2 = new de.cismet.cids.editors.DefaultBindableDateChooser();
        mappingComponent1 = new de.cismet.cismap.commons.gui.MappingComponent();
        jPanel7 = new javax.swing.JPanel();
        filler49 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblGeometrie5 = new javax.swing.JLabel();
        cbGeom = (!editable) ? new JComboBox() : new DefaultCismapGeometryComboBoxEditor();
        filler50 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblFlaechengroesse = new javax.swing.JLabel();
        lblFlaechengroesseWert = new javax.swing.JLabel();
        filler51 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblStadtbezirk = new javax.swing.JLabel();
        filler58 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        searchLabelsFieldPanel2 = new de.cismet.cids.editors.SearchLabelsFieldPanel(new KstGeometryMonSearch(
                    KstGeometryMonSearch.SearchFor.BEZIRK));
        filler52 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblQuartiere = new javax.swing.JLabel();
        filler56 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        searchLabelsFieldPanel1 = new de.cismet.cids.editors.SearchLabelsFieldPanel(new KstGeometryMonSearch(
                    KstGeometryMonSearch.SearchFor.QUARTIER));
        filler53 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblQuartiere1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        monSearchResultsList1 = new de.cismet.cids.custom.objecteditors.utils.CidsSearchResultsList<>(
                ((PotenzialflaecheReportServerAction.MonSearchReportProperty)
                    PotenzialflaecheReportServerAction.Property.FLURSTUECKE.getValue()).createMonServerSearch());
        filler54 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblStadtbezirk5 = new javax.swing.JLabel();
        filler59 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        defaultBindableCheckboxField3 = new de.cismet.cids.editors.DefaultBindableLabelsPanel(
                isEditable(),
                "Eigentümer:",
                SORTING_OPTION);
        filler55 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jPanel10 = new javax.swing.JPanel();
        panStandortdaten = new de.cismet.tools.gui.RoundedPanel();
        panLageTitle2 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblLageTitle2 = new javax.swing.JLabel();
        panLageBody2 = new javax.swing.JPanel();
        filler19 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblLagetyp = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        cbLagetyp = new de.cismet.cids.editors.DefaultBindableReferenceCombo(NULLABLE_OPTION, SORTING_OPTION);
        filler22 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 30),
                new java.awt.Dimension(0, 30),
                new java.awt.Dimension(32767, 30));
        lblOepnv = new javax.swing.JLabel();
        cbOepnv = new de.cismet.cids.editors.DefaultBindableReferenceCombo(NULLABLE_OPTION, SORTING_OPTION);
        filler20 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblLagetyp1 = new javax.swing.JLabel();
        filler26 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        defaultBindableCheckboxField11 = new de.cismet.cids.editors.DefaultBindableLabelsPanel(
                isEditable(),
                "Nähe zu:",
                SORTING_OPTION);
        filler21 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblLagetyp2 = new javax.swing.JLabel();
        cbLagetyp2 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(NULLABLE_OPTION, SORTING_OPTION);
        filler24 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblTopografie = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        cbTopografie = new de.cismet.cids.editors.DefaultBindableReferenceCombo(NULLABLE_OPTION, SORTING_OPTION);
        lblTopografie1 = new javax.swing.JLabel();
        cbTopografie1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(NULLABLE_OPTION, SORTING_OPTION);
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(200, 0),
                new java.awt.Dimension(200, 0),
                new java.awt.Dimension(200, 0));
        panPlanungsrecht = new de.cismet.tools.gui.RoundedPanel();
        panMessstellenausbauTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        lblMessstellenausbauTitle = new javax.swing.JLabel();
        panMessstellenausbauBody = new javax.swing.JPanel();
        filler27 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblFlaechennutzung1 = new javax.swing.JLabel();
        filler34 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        searchLabelsFieldPanel7 = new de.cismet.cids.editors.SearchLabelsFieldPanel(
                ((PotenzialflaecheReportServerAction.MonSearchReportProperty)
                    PotenzialflaecheReportServerAction.Property.REGIONALPLAN.getValue()).createMonServerSearch());
        filler28 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblFlaechennutzung = new javax.swing.JLabel();
        filler35 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        searchLabelsFieldPanel6 = new de.cismet.cids.editors.SearchLabelsFieldPanel(
                ((PotenzialflaecheReportServerAction.MonSearchReportProperty)
                    PotenzialflaecheReportServerAction.Property.FLAECHENNUTZUNGSPLAN.getValue())
                            .createMonServerSearch());
        filler29 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblFlaechennutzung2 = new javax.swing.JLabel();
        filler36 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jScrollPane11 = new javax.swing.JScrollPane();
        monSearchResultsList3 = new de.cismet.cids.custom.objecteditors.utils.CidsSearchResultsList<>(
                ((PotenzialflaecheReportServerAction.MonSearchReportProperty)
                    PotenzialflaecheReportServerAction.Property.BEBAUUNGSPLAN.getValue()).createMonServerSearch());
        filler30 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblFlaechennutzung3 = new javax.swing.JLabel();
        filler33 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jPanel12 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        filler32 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        filler31 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblVorhandeneBebauung1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        cbVorhandeneBebauung1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                SORTING_OPTION);
        cbVorhandeneBebauung2 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                SORTING_OPTION);
        jLabel1 = new javax.swing.JLabel();
        dateStand1 = new de.cismet.cids.editors.DefaultBindableDateChooser();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(200, 0),
                new java.awt.Dimension(200, 0),
                new java.awt.Dimension(200, 0));
        panErweitert = new de.cismet.tools.gui.RoundedPanel();
        panLageTitle3 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblLageTitle3 = new javax.swing.JLabel();
        panLageBody3 = new javax.swing.JPanel();
        filler39 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblStadtbezirk4 = new javax.swing.JLabel();
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        defaultBindableCheckboxField6 = new de.cismet.cids.editors.DefaultBindableLabelsPanel(
                isEditable(),
                "Umgebungsnutzung:",
                SORTING_OPTION);
        filler40 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblStadtbezirk6 = new javax.swing.JLabel();
        filler11 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jPanel9 = new javax.swing.JPanel();
        defaultBindableCheckboxField1 = new de.cismet.cids.editors.DefaultBindableLabelsPanel(
                isEditable(),
                "Brachflächenkategorie:",
                SORTING_OPTION);
        filler38 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblNutzungsaufgabe = new javax.swing.JLabel();
        txtJahrNutzungsaufgabe = new javax.swing.JTextField();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(150, 0),
                new java.awt.Dimension(150, 0),
                new java.awt.Dimension(150, 0));
        filler41 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblStadtbezirk3 = new javax.swing.JLabel();
        filler13 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        defaultBindableCheckboxField5 = new de.cismet.cids.editors.DefaultBindableLabelsPanel(
                isEditable(),
                "Bisherige Nutzung:",
                SORTING_OPTION);
        filler42 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblVorhandeneBebauung = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        filler43 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblVorhandeneBebauung2 = new javax.swing.JLabel();
        cbVorhandeneBebauung3 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                SORTING_OPTION);
        jPanel16 = new javax.swing.JPanel();
        filler44 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblAessereErschl = new javax.swing.JLabel();
        cbAeussereErschluessung = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                SORTING_OPTION);
        jPanel17 = new javax.swing.JPanel();
        lblVorhandeneBebauung3 = new javax.swing.JLabel();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jScrollPane4 = new javax.swing.JScrollPane();
        monSearchResultsList2 = new de.cismet.cids.custom.objecteditors.utils.CidsSearchResultsList<>(
                ((PotenzialflaecheReportServerAction.MonSearchReportProperty)
                    PotenzialflaecheReportServerAction.Property.BODENRICHTWERTE.getValue()).createMonServerSearch());
        filler77 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        filler46 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblStadtbezirk2 = new javax.swing.JLabel();
        filler14 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        searchLabelsFieldPanel3 = new de.cismet.cids.editors.SearchLabelsFieldPanel(
                ((PotenzialflaecheReportServerAction.MonSearchReportProperty)
                    PotenzialflaecheReportServerAction.Property.WOHNLAGEN.getValue()).createMonServerSearch());
        filler45 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblStadtbezirk1 = new javax.swing.JLabel();
        filler16 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        searchLabelsFieldPanel5 = new de.cismet.cids.editors.SearchLabelsFieldPanel(
                ((PotenzialflaecheReportServerAction.MonSearchReportProperty)
                    PotenzialflaecheReportServerAction.Property.STADTRAUMTYPEN.getValue()).createMonServerSearch());
        filler47 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblAessereErschl1 = new javax.swing.JLabel();
        cbAeussereErschluessung1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                SORTING_OPTION);
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
        jPanel25 = new javax.swing.JPanel();
        panAllgemein3 = new de.cismet.tools.gui.RoundedPanel();
        panBeschreibungTitle3 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblBeschreibungTitle3 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane15 = new javax.swing.JScrollPane();
        taFlaeche = new javax.swing.JTextArea();
        panArtControls3 = new javax.swing.JPanel();
        btnFlaeche = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        filler73 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblQuelle = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        lblStand = new javax.swing.JLabel();
        dateStand = new de.cismet.cids.editors.DefaultBindableDateChooser();
        panAllgemein6 = new de.cismet.tools.gui.RoundedPanel();
        panBeschreibungTitle6 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblBeschreibungTitle7 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        panArtControls4 = new javax.swing.JPanel();
        btnMassnahmen1 = new javax.swing.JButton();
        jScrollPane10 = new javax.swing.JScrollPane();
        taNotwendigeMassnahme1 = new javax.swing.JTextArea();
        jPanel26 = new javax.swing.JPanel();
        panBewertung1 = new de.cismet.tools.gui.RoundedPanel();
        panLageTitle5 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblLageTitle6 = new javax.swing.JLabel();
        panLageBody5 = new javax.swing.JPanel();
        filler76 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblEntwicklungsausssichten6 = new javax.swing.JLabel();
        cbEntwicklungsaussichten3 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                SORTING_OPTION);
        filler57 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblEntwicklungsausssichten1 = new javax.swing.JLabel();
        cbEntwicklungsaussichten1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                SORTING_OPTION);
        filler60 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblEntwicklungsausssichten2 = new javax.swing.JLabel();
        cbEntwicklungsaussichten2 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                SORTING_OPTION);
        filler61 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblEntwicklungsausssichten3 = new javax.swing.JLabel();
        filler37 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        defaultBindableCheckboxField4 = new de.cismet.cids.editors.DefaultBindableLabelsPanel(
                isEditable(),
                "Restriktionen/Hemmnisse:",
                SORTING_OPTION);
        filler62 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblEntwicklungsausssichten5 = new javax.swing.JLabel();
        filler71 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        defaultBindableCheckboxField10 = new de.cismet.cids.editors.DefaultBindableLabelsPanel(
                isEditable(),
                "Empfohlene Art der Wohnnutzung:",
                SORTING_OPTION);
        filler63 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblAktivierbarkeit1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        cbAktivierbarkeit1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(NULLABLE_OPTION, SORTING_OPTION);
        jFormattedTextField1 = new javax.swing.JFormattedTextField();
        filler64 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblEntwicklungsausssichten = new javax.swing.JLabel();
        cbEntwicklungsaussichten = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                NULLABLE_OPTION,
                SORTING_OPTION);
        filler65 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblVerfuegbarkeit1 = new javax.swing.JLabel();
        cbVerfuegbarkeit1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(NULLABLE_OPTION, SORTING_OPTION);
        filler66 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblAktivierbarkeit = new javax.swing.JLabel();
        cbAktivierbarkeit = new de.cismet.cids.editors.DefaultBindableReferenceCombo(NULLABLE_OPTION, SORTING_OPTION);
        filler67 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblHandlungsdruck1 = new javax.swing.JLabel();
        cbHandlungsdruck1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(NULLABLE_OPTION, SORTING_OPTION);
        filler68 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblEntwicklungsausssichten4 = new javax.swing.JLabel();
        filler72 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        defaultBindableCheckboxField8 = new de.cismet.cids.editors.DefaultBindableLabelsPanel(
                isEditable(),
                "Empfohlene Nutzung:",
                SORTING_OPTION);
        filler69 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblRevitalisierung1 = new javax.swing.JLabel();
        cbRevitalisierung1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(NULLABLE_OPTION, SORTING_OPTION);
        filler70 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28),
                new java.awt.Dimension(0, 28),
                new java.awt.Dimension(32767, 28));
        lblHandlungsdruck = new javax.swing.JLabel();
        cbHandlungsdruck = new de.cismet.cids.editors.DefaultBindableReferenceCombo(NULLABLE_OPTION, SORTING_OPTION);
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(200, 0),
                new java.awt.Dimension(200, 0),
                new java.awt.Dimension(200, 0));
        panAllgemein7 = new de.cismet.tools.gui.RoundedPanel();
        panBeschreibungTitle7 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblBeschreibungTitle6 = new javax.swing.JLabel();
        jPanel24 = new javax.swing.JPanel();
        panArtControls5 = new javax.swing.JPanel();
        btnMassnahmen = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        taNotwendigeMassnahme = new javax.swing.JTextArea();
        filler12 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0));
        panNew = new javax.swing.JPanel();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(100, 0),
                new java.awt.Dimension(100, 0),
                new java.awt.Dimension(100, 32767));
        filler17 = new javax.swing.Box.Filler(new java.awt.Dimension(100, 0),
                new java.awt.Dimension(100, 0),
                new java.awt.Dimension(100, 32767));
        jPanel18 = new javax.swing.JPanel();
        jPanel20 = new DroppedPfPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        jPanel21 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

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

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.beschreibung_flaeche}"),
                taFlaecheDialog,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

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

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.notwendige_massnahmen}"),
                taMassnahmeDialog,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane2.setViewportView(taMassnahmeDialog);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panFlaeche1.add(jScrollPane2, gridBagConstraints);

        dlgMassnahme.getContentPane().add(panFlaeche1, java.awt.BorderLayout.CENTER);

        panDialog.setAlwaysOnTop(true);
        panDialog.setUndecorated(true);

        panDefinition.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panDefinition.setMinimumSize(new java.awt.Dimension(400, 400));
        panDefinition.setPreferredSize(new java.awt.Dimension(600, 600));
        panDefinition.setLayout(new java.awt.BorderLayout());

        jPanel23.setBackground(java.awt.Color.darkGray);
        jPanel23.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {

                @Override
                public void mouseDragged(final java.awt.event.MouseEvent evt) {
                    jPanel23MouseDragged(evt);
                }
            });
        jPanel23.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mousePressed(final java.awt.event.MouseEvent evt) {
                    jPanel23MousePressed(evt);
                }
            });
        jPanel23.setLayout(new java.awt.GridBagLayout());

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, "<html><b>Definitionen - Potenzialflächenkataster");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanel23.add(jLabel3, gridBagConstraints);

        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(jButton3, "<html><b>X");
        jButton3.setToolTipText("");
        jButton3.setBorderPainted(false);
        jButton3.setContentAreaFilled(false);
        jButton3.setFocusPainted(false);
        jButton3.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton3ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel23.add(jButton3, gridBagConstraints);

        panDefinition.add(jPanel23, java.awt.BorderLayout.NORTH);

        jScrollPane9.setBorder(null);
        jScrollPane9.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane9.setEnabled(false);

        txtDefinition.setBackground(java.awt.Color.lightGray);
        txtDefinition.setContentType("text/html"); // NOI18N
        jScrollPane9.setViewportView(txtDefinition);

        panDefinition.add(jScrollPane9, java.awt.BorderLayout.CENTER);

        panDialog.getContentPane().add(panDefinition, java.awt.BorderLayout.CENTER);

        dlgInterneHinweise.setTitle("Notwendige Maßnahmen");
        dlgInterneHinweise.setModal(true);
        dlgInterneHinweise.setSize(new java.awt.Dimension(600, 400));

        panFlaeche2.setMaximumSize(new java.awt.Dimension(300, 120));
        panFlaeche2.setMinimumSize(new java.awt.Dimension(300, 120));
        panFlaeche2.setPreferredSize(new java.awt.Dimension(300, 120));
        panFlaeche2.setLayout(new java.awt.GridBagLayout());

        panMenButtons6.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(btnMenAbort6, "Abbrechen");
        btnMenAbort6.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnMenAbort6ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMenButtons6.add(btnMenAbort6, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(btnMenOk6, "Ok");
        btnMenOk6.setMaximumSize(new java.awt.Dimension(85, 23));
        btnMenOk6.setMinimumSize(new java.awt.Dimension(85, 23));
        btnMenOk6.setPreferredSize(new java.awt.Dimension(85, 23));
        btnMenOk6.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnMenOk6ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMenButtons6.add(btnMenOk6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panFlaeche2.add(panMenButtons6, gridBagConstraints);

        taMassnahmeDialog1.setColumns(20);
        taMassnahmeDialog1.setLineWrap(true);
        taMassnahmeDialog1.setRows(5);
        taMassnahmeDialog1.setWrapStyleWord(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.interne_hinweise}"),
                taMassnahmeDialog1,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane6.setViewportView(taMassnahmeDialog1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panFlaeche2.add(jScrollPane6, gridBagConstraints);

        dlgInterneHinweise.getContentPane().add(panFlaeche2, java.awt.BorderLayout.CENTER);

        dlgArchivieren.setTitle("Fläche archivieren");
        dlgArchivieren.getContentPane().setLayout(new java.awt.GridBagLayout());

        setOpaque(false);
        setLayout(new java.awt.BorderLayout());

        jScrollPane5.setBorder(null);
        jScrollPane5.setOpaque(false);

        jPanel22.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel22.setMaximumSize(new java.awt.Dimension(1200, 2147483647));
        jPanel22.setOpaque(false);
        jPanel22.setLayout(new java.awt.CardLayout());

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
        lblBeschreibungTitle.setForeground(java.awt.Color.white);
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
        lblNummer.setName(PREFIX_LABEL + PotenzialflaecheReportServerAction.Property.NUMMER.name());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel3.add(lblNummer, gridBagConstraints);

        jPanel28.setOpaque(false);
        jPanel28.setLayout(new java.awt.GridBagLayout());

        txtNummer.setName(PREFIX_INPUT + PotenzialflaecheReportServerAction.Property.NUMMER.name());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.nummer}"),
                txtNummer,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel28.add(txtNummer, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBox1, "veröffentlicht");
        jCheckBox1.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.veroeffentlicht}"),
                jCheckBox1,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        jPanel28.add(jCheckBox1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jButton4, "Archivieren");
        jButton4.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton4ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        jPanel28.add(jButton4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(jPanel28, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel3.add(filler48, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblBezeichnung, "Bezeichnung:");
        lblBezeichnung.setName(PREFIX_LABEL + PotenzialflaecheReportServerAction.Property.BEZEICHNUNG.name());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel3.add(lblBezeichnung, gridBagConstraints);

        txtBezeichnung.setName(PREFIX_INPUT + PotenzialflaecheReportServerAction.Property.BEZEICHNUNG.name());

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
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel3.add(txtBezeichnung, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel3.add(filler75, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel6,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheFooterPanel.jLabel6.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel3.add(jLabel6, gridBagConstraints);

        jPanel27.setOpaque(false);
        jPanel27.setLayout(new java.awt.GridBagLayout());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_archivierungsgrund}"),
                defaultBindableReferenceCombo1,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        defaultBindableReferenceCombo1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    defaultBindableReferenceCombo1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        jPanel27.add(defaultBindableReferenceCombo1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel7,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheFooterPanel.jLabel7.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        jPanel27.add(jLabel7, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.archivierungsdatum}"),
                dateStand2,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(sqlDateToUtilDateConverter);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel27.add(dateStand2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(jPanel27, gridBagConstraints);

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
        lblFlaechengroesse.setName(PREFIX_LABEL + PotenzialflaecheReportServerAction.Property.GROESSE.name());
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
        lblStadtbezirk.setName(PREFIX_LABEL + PotenzialflaecheReportServerAction.Property.STADTBEZIRK.name());
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
        lblQuartiere.setName(PREFIX_LABEL + PotenzialflaecheReportServerAction.Property.QUARTIER.name());
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
        lblQuartiere1.setName(PREFIX_LABEL + PotenzialflaecheReportServerAction.Property.FLURSTUECKE.name());
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
        lblStadtbezirk5.setName(PREFIX_LABEL + PotenzialflaecheReportServerAction.Property.EIGENTUEMER.name());
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

        defaultBindableCheckboxField3.setName(PREFIX_INPUT
                    + PotenzialflaecheReportServerAction.Property.EIGENTUEMER.name());
        defaultBindableCheckboxField3.setOpaque(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.arr_eigentuemer}"),
                defaultBindableCheckboxField3,
                org.jdesktop.beansbinding.BeanProperty.create("selectedElements"));
        bindingGroup.addBinding(binding);

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
        lblLagetyp.setName(PREFIX_LABEL + PotenzialflaecheReportServerAction.Property.LAGEBEWERTUNG_VERKEHR.name());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody2.add(lblLagetyp, gridBagConstraints);

        jPanel13.setOpaque(false);
        jPanel13.setLayout(new java.awt.GridBagLayout());

        cbLagetyp.setName(PREFIX_INPUT + PotenzialflaecheReportServerAction.Property.LAGEBEWERTUNG_VERKEHR.name());

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
        lblOepnv.setName(PREFIX_LABEL + PotenzialflaecheReportServerAction.Property.OEPNV_ANBINDUNG.name());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 5);
        jPanel13.add(lblOepnv, gridBagConstraints);

        cbOepnv.setName(PREFIX_INPUT + PotenzialflaecheReportServerAction.Property.OEPNV_ANBINDUNG.name());

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
        lblLagetyp1.setName(PREFIX_LABEL + PotenzialflaecheReportServerAction.Property.NAEHE_ZU.name());
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

        defaultBindableCheckboxField11.setName(PREFIX_INPUT
                    + PotenzialflaecheReportServerAction.Property.NAEHE_ZU.name());
        defaultBindableCheckboxField11.setOpaque(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.arr_naehen_zu}"),
                defaultBindableCheckboxField11,
                org.jdesktop.beansbinding.BeanProperty.create("selectedElements"));
        bindingGroup.addBinding(binding);

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
        lblLagetyp2.setName(PREFIX_LABEL + PotenzialflaecheReportServerAction.Property.SIEDLUNGSRAEUMLICHE_LAGE.name());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody2.add(lblLagetyp2, gridBagConstraints);

        cbLagetyp2.setName(PREFIX_INPUT + PotenzialflaecheReportServerAction.Property.SIEDLUNGSRAEUMLICHE_LAGE.name());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_siedlungsraeumliche_lage}"),
                cbLagetyp2,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        cbLagetyp2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbLagetyp2ActionPerformed(evt);
                }
            });
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
        lblTopografie.setName(PREFIX_LABEL + PotenzialflaecheReportServerAction.Property.TOPOGRAFIE.name());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody2.add(lblTopografie, gridBagConstraints);

        jPanel4.setOpaque(false);
        jPanel4.setLayout(new java.awt.GridBagLayout());

        cbTopografie.setName(PREFIX_INPUT + PotenzialflaecheReportServerAction.Property.TOPOGRAFIE.name());

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
        lblTopografie1.setName(PREFIX_LABEL + PotenzialflaecheReportServerAction.Property.HANG.name());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 5);
        jPanel4.add(lblTopografie1, gridBagConstraints);

        cbTopografie1.setName(PREFIX_INPUT + PotenzialflaecheReportServerAction.Property.HANG.name());

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
        org.openide.awt.Mnemonics.setLocalizedText(lblMessstellenausbauTitle, "Planungsrecht/Bauordnungsrecht");
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
        lblFlaechennutzung1.setName(PREFIX_LABEL + PotenzialflaecheReportServerAction.Property.REGIONALPLAN.name());
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

        searchLabelsFieldPanel7.setOpaque(false);
        searchLabelsFieldPanel7.setLayout(new java.awt.FlowLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMessstellenausbauBody.add(searchLabelsFieldPanel7, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMessstellenausbauBody.add(filler28, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblFlaechennutzung, "Flächennutzungplan:");
        lblFlaechennutzung.setName(PREFIX_LABEL
                    + PotenzialflaecheReportServerAction.Property.FLAECHENNUTZUNGSPLAN.name());
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
        lblFlaechennutzung2.setName(PREFIX_LABEL + PotenzialflaecheReportServerAction.Property.BEBAUUNGSPLAN.name());
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
        gridBagConstraints.ipady = 13;
        panMessstellenausbauBody.add(filler36, gridBagConstraints);

        monSearchResultsList3.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        monSearchResultsList3.setName(PREFIX_INPUT + PotenzialflaecheReportServerAction.Property.BEBAUUNGSPLAN.name());
        monSearchResultsList3.setVisibleRowCount(3);
        jScrollPane11.setViewportView(monSearchResultsList3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMessstellenausbauBody.add(jScrollPane11, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMessstellenausbauBody.add(filler30, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblFlaechennutzung3, "B-Plan-Informationen:");
        lblFlaechennutzung3.setName(PREFIX_LABEL
                    + PotenzialflaecheReportServerAction.Property.FESTSETZUNGEN_BPLAN.name());
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
        gridBagConstraints.ipady = 10;
        panMessstellenausbauBody.add(filler33, gridBagConstraints);

        jPanel12.setOpaque(false);
        jPanel12.setLayout(new java.awt.GridBagLayout());

        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(2);
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setName(PREFIX_INPUT + PotenzialflaecheReportServerAction.Property.FESTSETZUNGEN_BPLAN.name());

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
        lblVorhandeneBebauung1.setName(PREFIX_LABEL
                    + PotenzialflaecheReportServerAction.Property.BAUORDNUNGSRECHT_GENEHMIGUNG.toString() + ";"
                    + PotenzialflaecheReportServerAction.Property.BAUORDNUNGSRECHT_BAULAST.name());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panMessstellenausbauBody.add(lblVorhandeneBebauung1, gridBagConstraints);

        jPanel5.setOpaque(false);
        jPanel5.setLayout(new java.awt.GridBagLayout());

        cbVorhandeneBebauung1.setName(PREFIX_INPUT
                    + PotenzialflaecheReportServerAction.Property.BAUORDNUNGSRECHT_GENEHMIGUNG.name());

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

        cbVorhandeneBebauung2.setName(PREFIX_INPUT
                    + PotenzialflaecheReportServerAction.Property.BAUORDNUNGSRECHT_BAULAST.name());

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

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, "Stand:");
        jLabel1.setName(PREFIX_LABEL + PotenzialflaecheReportServerAction.Property.BAUORDNUNGSRECHT_STAND.name());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel5.add(jLabel1, gridBagConstraints);

        dateStand1.setName(PREFIX_INPUT + PotenzialflaecheReportServerAction.Property.BAUORDNUNGSRECHT_STAND.name());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.stand_bauordnungsrecht}"),
                dateStand1,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(sqlDateToUtilDateConverter);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel5.add(dateStand1, gridBagConstraints);

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
        lblStadtbezirk4.setName(PREFIX_LABEL + PotenzialflaecheReportServerAction.Property.UMGEBUNGSNUTZUNG.name());
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

        defaultBindableCheckboxField6.setName(PREFIX_INPUT
                    + PotenzialflaecheReportServerAction.Property.UMGEBUNGSNUTZUNG.name());
        defaultBindableCheckboxField6.setOpaque(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.umgebungsnutzung}"),
                defaultBindableCheckboxField6,
                org.jdesktop.beansbinding.BeanProperty.create("selectedElements"));
        bindingGroup.addBinding(binding);

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
        lblStadtbezirk6.setName(PREFIX_LABEL
                    + PotenzialflaecheReportServerAction.Property.BRACHFLAECHENKATEGORIE.name());
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

        defaultBindableCheckboxField1.setName(PREFIX_INPUT
                    + PotenzialflaecheReportServerAction.Property.BRACHFLAECHENKATEGORIE.name());
        defaultBindableCheckboxField1.setOpaque(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.arr_brachflaechen}"),
                defaultBindableCheckboxField1,
                org.jdesktop.beansbinding.BeanProperty.create("selectedElements"));
        bindingGroup.addBinding(binding);

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
        lblNutzungsaufgabe.setName(PREFIX_LABEL
                    + PotenzialflaecheReportServerAction.Property.JAHR_NUTZUNGSAUFGABE.name());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel9.add(lblNutzungsaufgabe, gridBagConstraints);

        txtJahrNutzungsaufgabe.setName(PREFIX_INPUT
                    + PotenzialflaecheReportServerAction.Property.JAHR_NUTZUNGSAUFGABE.name());

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
        lblStadtbezirk3.setName(PREFIX_LABEL + PotenzialflaecheReportServerAction.Property.BISHERIGE_NUTZUNG.name());
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

        defaultBindableCheckboxField5.setName(PREFIX_INPUT
                    + PotenzialflaecheReportServerAction.Property.BISHERIGE_NUTZUNG.name());
        defaultBindableCheckboxField5.setOpaque(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bisherige_nutzung}"),
                defaultBindableCheckboxField5,
                org.jdesktop.beansbinding.BeanProperty.create("selectedElements"));
        bindingGroup.addBinding(binding);

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
        lblVorhandeneBebauung.setName(PREFIX_LABEL
                    + PotenzialflaecheReportServerAction.Property.VORHANDENE_BEBAUUNG.name());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody3.add(lblVorhandeneBebauung, gridBagConstraints);

        jTextField2.setName(PREFIX_INPUT + PotenzialflaecheReportServerAction.Property.VORHANDENE_BEBAUUNG.name());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bestand_bebauung}"),
                jTextField2,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(jTextField2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(filler43, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblVorhandeneBebauung2, "Bestand Versiegelung:");
        lblVorhandeneBebauung2.setName(PREFIX_LABEL + PotenzialflaecheReportServerAction.Property.VERSIEGELUNG.name());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody3.add(lblVorhandeneBebauung2, gridBagConstraints);

        cbVorhandeneBebauung3.setName(PREFIX_INPUT + PotenzialflaecheReportServerAction.Property.VERSIEGELUNG.name());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_versiegelung}"),
                cbVorhandeneBebauung3,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(cbVorhandeneBebauung3, gridBagConstraints);

        jPanel16.setOpaque(false);
        jPanel16.setLayout(new java.awt.GridBagLayout());
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
        lblAessereErschl.setName(PREFIX_LABEL
                    + PotenzialflaecheReportServerAction.Property.AEUSSERE_ERSCHLIESSUNG.name());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 6);
        panLageBody3.add(lblAessereErschl, gridBagConstraints);

        cbAeussereErschluessung.setName(PREFIX_INPUT
                    + PotenzialflaecheReportServerAction.Property.AEUSSERE_ERSCHLIESSUNG.name());

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

        org.openide.awt.Mnemonics.setLocalizedText(lblVorhandeneBebauung3, "Bodenrichtwert(e):");
        lblVorhandeneBebauung3.setName(PREFIX_LABEL
                    + PotenzialflaecheReportServerAction.Property.BODENRICHTWERTE.name());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 5, 5);
        jPanel17.add(lblVorhandeneBebauung3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 13;
        gridBagConstraints.weighty = 1.0;
        jPanel17.add(filler9, gridBagConstraints);

        monSearchResultsList2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        monSearchResultsList2.setName(PREFIX_INPUT
                    + PotenzialflaecheReportServerAction.Property.BODENRICHTWERTE.name());
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
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel17.add(filler77, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(jPanel17, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(filler46, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblStadtbezirk2, "Wohnlagen:");
        lblStadtbezirk2.setName(PREFIX_LABEL + PotenzialflaecheReportServerAction.Property.WOHNLAGEN.name());
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
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(searchLabelsFieldPanel3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(filler45, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblStadtbezirk1, "Stadtraumtypen:");
        lblStadtbezirk1.setName(PREFIX_LABEL + PotenzialflaecheReportServerAction.Property.STADTRAUMTYPEN.name());
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
        lblAessereErschl1.setName(PREFIX_LABEL + PotenzialflaecheReportServerAction.Property.KLIMAINFORMATIONEN.name());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 6);
        panLageBody3.add(lblAessereErschl1, gridBagConstraints);

        cbAeussereErschluessung1.setName(PREFIX_INPUT
                    + PotenzialflaecheReportServerAction.Property.KLIMAINFORMATIONEN.name());

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
        gridBagConstraints.gridy = 2;
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

        jPanel22.add(panMain, "grunddaten");

        panDetail.setOpaque(false);
        panDetail.setLayout(new java.awt.GridBagLayout());

        jPanel15.setOpaque(false);
        jPanel15.setLayout(new java.awt.GridBagLayout());

        jPanel25.setOpaque(false);
        jPanel25.setLayout(new java.awt.GridBagLayout());

        panAllgemein3.setLayout(new java.awt.GridBagLayout());

        panBeschreibungTitle3.setBackground(java.awt.Color.darkGray);
        panBeschreibungTitle3.setLayout(new java.awt.GridBagLayout());

        lblBeschreibungTitle3.setFont(lblBeschreibungTitle3.getFont());
        lblBeschreibungTitle3.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(lblBeschreibungTitle3, "Beschreibung der Fläche / Sachstand");
        lblBeschreibungTitle3.setName(PREFIX_LABEL
                    + PotenzialflaecheReportServerAction.Property.BESCHREIBUNG_FLAECHE.name());
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
        taFlaeche.setRows(15);
        taFlaeche.setWrapStyleWord(true);
        taFlaeche.setName(PREFIX_INPUT + PotenzialflaecheReportServerAction.Property.BESCHREIBUNG_FLAECHE.name());

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
        btnFlaeche.setName(PREFIX_INPUT + PotenzialflaecheReportServerAction.Property.BESCHREIBUNG_FLAECHE.name());
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
        lblQuelle.setName(PREFIX_LABEL + PotenzialflaecheReportServerAction.Property.QUELLE.name());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel6.add(lblQuelle, gridBagConstraints);

        jTextField1.setName(PREFIX_INPUT + PotenzialflaecheReportServerAction.Property.QUELLE.name());

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
        lblStand.setName(PREFIX_LABEL + PotenzialflaecheReportServerAction.Property.STAND.name());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 5, 5);
        jPanel6.add(lblStand, gridBagConstraints);

        dateStand.setName(PREFIX_INPUT + PotenzialflaecheReportServerAction.Property.STAND.name());

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
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel25.add(panAllgemein3, gridBagConstraints);

        panAllgemein6.setLayout(new java.awt.GridBagLayout());

        panBeschreibungTitle6.setBackground(java.awt.Color.darkGray);
        panBeschreibungTitle6.setLayout(new java.awt.GridBagLayout());

        lblBeschreibungTitle7.setFont(lblBeschreibungTitle7.getFont());
        lblBeschreibungTitle7.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(lblBeschreibungTitle7, "Interne Hinweise");
        lblBeschreibungTitle7.setName(PREFIX_LABEL
                    + PotenzialflaecheReportServerAction.Property.INTERNE_HINWEISE.name());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBeschreibungTitle6.add(lblBeschreibungTitle7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panAllgemein6.add(panBeschreibungTitle6, gridBagConstraints);

        jPanel14.setOpaque(false);
        jPanel14.setLayout(new java.awt.GridBagLayout());

        panArtControls4.setOpaque(false);
        panArtControls4.setLayout(new java.awt.GridBagLayout());

        btnMassnahmen1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/commons/gui/icon-edit.png"))); // NOI18N
        btnMassnahmen1.setBorderPainted(false);
        btnMassnahmen1.setContentAreaFilled(false);
        btnMassnahmen1.setMaximumSize(new java.awt.Dimension(32, 32));
        btnMassnahmen1.setMinimumSize(new java.awt.Dimension(32, 32));
        btnMassnahmen1.setName(PREFIX_INPUT + PotenzialflaecheReportServerAction.Property.NOTWENDIGE_MASSNAHMEN.name());
        btnMassnahmen1.setPreferredSize(new java.awt.Dimension(32, 32));
        btnMassnahmen1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnMassnahmen1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panArtControls4.add(btnMassnahmen1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel14.add(panArtControls4, gridBagConstraints);
        panArtControls4.setVisible(isEditable());

        jScrollPane10.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane10.setPreferredSize(new java.awt.Dimension(200, 80));

        taNotwendigeMassnahme1.setLineWrap(true);
        taNotwendigeMassnahme1.setRows(3);
        taNotwendigeMassnahme1.setWrapStyleWord(true);
        taNotwendigeMassnahme1.setName(PREFIX_INPUT
                    + PotenzialflaecheReportServerAction.Property.NOTWENDIGE_MASSNAHMEN.name());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.interne_hinweise}"),
                taNotwendigeMassnahme1,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane10.setViewportView(taNotwendigeMassnahme1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel14.add(jScrollPane10, gridBagConstraints);

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
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel25.add(panAllgemein6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel15.add(jPanel25, gridBagConstraints);

        jPanel26.setOpaque(false);
        jPanel26.setLayout(new java.awt.GridBagLayout());

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
        panLageBody5.add(filler76, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblEntwicklungsausssichten6, "Kategorie:");
        lblEntwicklungsausssichten6.setName(PREFIX_LABEL
                    + PotenzialflaecheReportServerAction.Property.KATEGORIE.name());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody5.add(lblEntwicklungsausssichten6, gridBagConstraints);

        cbEntwicklungsaussichten3.setName(PREFIX_INPUT
                    + PotenzialflaecheReportServerAction.Property.POTENZIALART.name());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_kategorie}"),
                cbEntwicklungsaussichten3,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody5.add(cbEntwicklungsaussichten3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panLageBody5.add(filler57, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblEntwicklungsausssichten1, "Potenzialart:");
        lblEntwicklungsausssichten1.setName(PREFIX_LABEL
                    + PotenzialflaecheReportServerAction.Property.POTENZIALART.name());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody5.add(lblEntwicklungsausssichten1, gridBagConstraints);

        cbEntwicklungsaussichten1.setName(PREFIX_INPUT
                    + PotenzialflaecheReportServerAction.Property.POTENZIALART.name());

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
        lblEntwicklungsausssichten2.setName(PREFIX_LABEL
                    + PotenzialflaecheReportServerAction.Property.ENTWICKLUNGSSTAND.name());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody5.add(lblEntwicklungsausssichten2, gridBagConstraints);

        cbEntwicklungsaussichten2.setName(PREFIX_INPUT
                    + PotenzialflaecheReportServerAction.Property.ENTWICKLUNGSSTAND.name());

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
        lblEntwicklungsausssichten3.setName(PREFIX_LABEL
                    + PotenzialflaecheReportServerAction.Property.RESTRIKTIONEN.name());
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

        defaultBindableCheckboxField4.setName(PREFIX_INPUT
                    + PotenzialflaecheReportServerAction.Property.RESTRIKTIONEN.name());
        defaultBindableCheckboxField4.setOpaque(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.arr_restriktionen}"),
                defaultBindableCheckboxField4,
                org.jdesktop.beansbinding.BeanProperty.create("selectedElements"));
        bindingGroup.addBinding(binding);

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
        lblEntwicklungsausssichten5.setName(PREFIX_LABEL
                    + PotenzialflaecheReportServerAction.Property.EMPFOHLENE_NUTZUNGEN_WOHNEN.name());
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

        defaultBindableCheckboxField10.setName(PREFIX_INPUT
                    + PotenzialflaecheReportServerAction.Property.EMPFOHLENE_NUTZUNGEN_WOHNEN.name());
        defaultBindableCheckboxField10.setOpaque(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.arr_empfohlene_nutzungen_wohnen}"),
                defaultBindableCheckboxField10,
                org.jdesktop.beansbinding.BeanProperty.create("selectedElements"));
        bindingGroup.addBinding(binding);

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
        lblAktivierbarkeit1.setName(PREFIX_LABEL + PotenzialflaecheReportServerAction.Property.WOHNEINHEITEN.name());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody5.add(lblAktivierbarkeit1, gridBagConstraints);

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridBagLayout());

        cbAktivierbarkeit1.setName(PREFIX_INPUT + PotenzialflaecheReportServerAction.Property.WOHNEINHEITEN.name());

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
        jFormattedTextField1.setName(PREFIX_INPUT + PotenzialflaecheReportServerAction.Property.WOHNEINHEITEN.name());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.anzahl_wohneinheiten}"),
                jFormattedTextField1,
                org.jdesktop.beansbinding.BeanProperty.create("value"));
        binding.setConverter(new IntegerToLongConverter());
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
        lblEntwicklungsausssichten.setName(PREFIX_LABEL
                    + PotenzialflaecheReportServerAction.Property.ENTWICKLUNGSAUSSSICHTEN.name());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody5.add(lblEntwicklungsausssichten, gridBagConstraints);

        cbEntwicklungsaussichten.setName(PREFIX_INPUT
                    + PotenzialflaecheReportServerAction.Property.ENTWICKLUNGSAUSSSICHTEN.name());

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
        lblVerfuegbarkeit1.setName(PREFIX_LABEL + PotenzialflaecheReportServerAction.Property.VERFUEGBBARKEIT.name());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody5.add(lblVerfuegbarkeit1, gridBagConstraints);

        cbVerfuegbarkeit1.setName(PREFIX_INPUT + PotenzialflaecheReportServerAction.Property.VERFUEGBBARKEIT.name());

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
        lblAktivierbarkeit.setName(PREFIX_LABEL + PotenzialflaecheReportServerAction.Property.VERWERTBARKEIT.name());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody5.add(lblAktivierbarkeit, gridBagConstraints);

        cbAktivierbarkeit.setName(PREFIX_INPUT + PotenzialflaecheReportServerAction.Property.VERWERTBARKEIT.name());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_verwertbarkeit}"),
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
        lblHandlungsdruck1.setName(PREFIX_LABEL
                    + PotenzialflaecheReportServerAction.Property.HANDLUNGSPRIORITAET.name());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody5.add(lblHandlungsdruck1, gridBagConstraints);

        cbHandlungsdruck1.setName(PREFIX_INPUT
                    + PotenzialflaecheReportServerAction.Property.HANDLUNGSPRIORITAET.name());

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
        lblEntwicklungsausssichten4.setName(PREFIX_LABEL
                    + PotenzialflaecheReportServerAction.Property.EMPFOHLENE_NUTZUNGEN.name());
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

        defaultBindableCheckboxField8.setName(PREFIX_INPUT
                    + PotenzialflaecheReportServerAction.Property.EMPFOHLENE_NUTZUNGEN.name());
        defaultBindableCheckboxField8.setOpaque(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.arr_empfohlene_nutzungen}"),
                defaultBindableCheckboxField8,
                org.jdesktop.beansbinding.BeanProperty.create("selectedElements"));
        bindingGroup.addBinding(binding);

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
        lblRevitalisierung1.setName(PREFIX_LABEL + PotenzialflaecheReportServerAction.Property.REVITALISIERUNG.name());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody5.add(lblRevitalisierung1, gridBagConstraints);

        cbRevitalisierung1.setName(PREFIX_INPUT + PotenzialflaecheReportServerAction.Property.REVITALISIERUNG.name());

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
        lblHandlungsdruck.setName(PREFIX_LABEL + PotenzialflaecheReportServerAction.Property.HANDLUNGSDRUCK.name());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody5.add(lblHandlungsdruck, gridBagConstraints);

        cbHandlungsdruck.setName(PREFIX_INPUT + PotenzialflaecheReportServerAction.Property.HANDLUNGSDRUCK.name());

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
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel26.add(panBewertung1, gridBagConstraints);

        panAllgemein7.setLayout(new java.awt.GridBagLayout());

        panBeschreibungTitle7.setBackground(java.awt.Color.darkGray);
        panBeschreibungTitle7.setLayout(new java.awt.GridBagLayout());

        lblBeschreibungTitle6.setFont(lblBeschreibungTitle6.getFont());
        lblBeschreibungTitle6.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(lblBeschreibungTitle6, "Notwendige Maßnahmen / Nächste Schritte");
        lblBeschreibungTitle6.setName(PREFIX_LABEL
                    + PotenzialflaecheReportServerAction.Property.NOTWENDIGE_MASSNAHMEN.name());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBeschreibungTitle7.add(lblBeschreibungTitle6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panAllgemein7.add(panBeschreibungTitle7, gridBagConstraints);

        jPanel24.setOpaque(false);
        jPanel24.setLayout(new java.awt.GridBagLayout());

        panArtControls5.setOpaque(false);
        panArtControls5.setLayout(new java.awt.GridBagLayout());

        btnMassnahmen.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/commons/gui/icon-edit.png"))); // NOI18N
        btnMassnahmen.setBorderPainted(false);
        btnMassnahmen.setContentAreaFilled(false);
        btnMassnahmen.setMaximumSize(new java.awt.Dimension(32, 32));
        btnMassnahmen.setMinimumSize(new java.awt.Dimension(32, 32));
        btnMassnahmen.setName(PREFIX_INPUT + PotenzialflaecheReportServerAction.Property.NOTWENDIGE_MASSNAHMEN.name());
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
        panArtControls5.add(btnMassnahmen, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel24.add(panArtControls5, gridBagConstraints);
        panArtControls5.setVisible(isEditable());

        jScrollPane7.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane7.setPreferredSize(new java.awt.Dimension(200, 100));

        taNotwendigeMassnahme.setLineWrap(true);
        taNotwendigeMassnahme.setRows(3);
        taNotwendigeMassnahme.setWrapStyleWord(true);
        taNotwendigeMassnahme.setName(PREFIX_INPUT
                    + PotenzialflaecheReportServerAction.Property.NOTWENDIGE_MASSNAHMEN.name());

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
        jPanel24.add(jScrollPane7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 10);
        panAllgemein7.add(jPanel24, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel26.add(panAllgemein7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel15.add(jPanel26, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
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

        jPanel22.add(panDetail, "details");

        panNew.setOpaque(false);
        panNew.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panNew.add(filler7, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panNew.add(filler17, gridBagConstraints);

        jPanel18.setOpaque(false);
        jPanel18.setLayout(new java.awt.GridBagLayout());

        jPanel20.setBorder(new org.jdesktop.swingx.border.DropShadowBorder());
        jPanel20.setOpaque(false);
        jPanel20.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, "<html><i>Potenzialfläche in diesen Bereich ziehen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel20.add(jLabel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 20);
        jPanel18.add(jPanel20, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 20);
        panNew.add(jPanel18, gridBagConstraints);

        jPanel19.setOpaque(false);
        jPanel19.setLayout(new java.awt.GridBagLayout());

        jPanel21.setOpaque(false);
        jPanel21.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jButton2, "<html>neue Potenzialfläche händisch erfassen");
        jButton2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel21.add(jButton2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 20);
        jPanel19.add(jPanel21, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 20);
        panNew.add(jPanel19, gridBagConstraints);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 20);
        panNew.add(jSeparator1, gridBagConstraints);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel2,
            "<html><center>Aus einer bestehenden Potenzialfläche werden alle Attribute kopiert. Diese können danach händisch angepasst werden.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 20);
        panNew.add(jLabel2, gridBagConstraints);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel5,
            "<html><center>Es wird eine leere Potenzialfläche erzeugt, in der alle Attribute händisch erfasst werden können.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 20);
        panNew.add(jLabel5, gridBagConstraints);

        jPanel22.add(panNew, "neu");

        jScrollPane5.setViewportView(jPanel22);
        ((ScrollablePanel)jPanel22).setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
        ((ScrollablePanel)jPanel22).setScrollableHeight(ScrollablePanel.ScrollableSizeHint.STRETCH);

        add(jScrollPane5, java.awt.BorderLayout.CENTER);
        jScrollPane5.getViewport().setOpaque(false);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenAbort4ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenAbort4ActionPerformed
        dlgFlaeche.setVisible(false);
    }//GEN-LAST:event_btnMenAbort4ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenOk4ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenOk4ActionPerformed
        try {
            cidsBean.setProperty("beschreibung_flaeche", taFlaecheDialog.getText());
        } catch (final Exception e) {
            LOG.error("Cannot save text for beschreibung_flaeche", e);
        }
        dlgFlaeche.setVisible(false);
    }//GEN-LAST:event_btnMenOk4ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenAbort5ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenAbort5ActionPerformed
        dlgMassnahme.setVisible(false);
    }//GEN-LAST:event_btnMenAbort5ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenOk5ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenOk5ActionPerformed
        try {
            cidsBean.setProperty("notwendige_massnahmen", taMassnahmeDialog.getText());
        } catch (final Exception e) {
            LOG.error("Cannot save text for notwendige_massnahmen", e);
        }
        dlgMassnahme.setVisible(false);
    }//GEN-LAST:event_btnMenOk5ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMassnahmenActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMassnahmenActionPerformed
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(this), dlgMassnahme, true);
    }//GEN-LAST:event_btnMassnahmenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnFlaecheActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFlaecheActionPerformed
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(this), dlgFlaeche, true);
    }//GEN-LAST:event_btnFlaecheActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbGeomFocusLost(final java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbGeomFocusLost
        setGeometryArea();
    }//GEN-LAST:event_cbGeomFocusLost

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtBezeichnungFocusLost(final java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBezeichnungFocusLost
        panTitle.refresh();
    }//GEN-LAST:event_txtBezeichnungFocusLost

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbAeussereErschluessungActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbAeussereErschluessungActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbAeussereErschluessungActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbGeomActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbGeomActionPerformed
        refreshMap();
        refreshGeomFields();
        refreshNummer();
    }//GEN-LAST:event_cbGeomActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
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
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  origBean  DOCUMENT ME!
     */
    private void copyFrom(final CidsBean origBean) {
        try {
            copyFromTo(origBean, getCidsBean(), getConnectionContext());

            for (final DefaultBindableLabelsPanel labelsPanel : labelsPanels) {
                if (labelsPanel != null) {
                    // workaround for refreshing because list didn't change,
                    // only the content => no automatic refresh from bindinglisteners
                    labelsPanel.setMetaClass(labelsPanel.getMetaClass());
                }
            }
            showGrunddaten();
            refreshMap();
            refreshGeomFields();
            setGeometryArea();

            panTitle.refresh();
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton2ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        copyFrom(null);
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param   component      DOCUMENT ME!
     * @param   preferredSize  DOCUMENT ME!
     * @param   position       DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    static Point fitToScreen(final Component component, final Dimension preferredSize, final Point position) {
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Rectangle screenBounds;
        final Insets screenInsets;

        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice[] gd = ge.getScreenDevices();

        GraphicsConfiguration gc = null;
        for (final GraphicsDevice gd1 : gd) {
            if (gd1.getType() == GraphicsDevice.TYPE_RASTER_SCREEN) {
                final GraphicsConfiguration dgc = gd1.getDefaultConfiguration();
                if (dgc.getBounds().contains(position)) {
                    gc = dgc;
                    break;
                }
            }
        }

        if ((gc == null) && (component != null)) {
            gc = component.getGraphicsConfiguration();
        }

        if (gc != null) {
            screenInsets = toolkit.getScreenInsets(gc);
            screenBounds = gc.getBounds();
        } else {
            screenInsets = new Insets(0, 0, 0, 0);
            screenBounds = new Rectangle(toolkit.getScreenSize());
        }

        final int scrWidth = screenBounds.width - Math.abs(screenInsets.left + screenInsets.right);
        final int scrHeight = screenBounds.height - Math.abs(screenInsets.top + screenInsets.bottom);

        final long positionX = (long)position.x + (long)preferredSize.width;
        final long positionY = (long)position.y + (long)preferredSize.height;

        if (positionX > (screenBounds.x + scrWidth)) {
            position.x = screenBounds.x + scrWidth - preferredSize.width;
        }
        if (position.x < screenBounds.x) {
            position.x = screenBounds.x;
        }
        if (positionY > (screenBounds.y + scrHeight)) {
            position.y = screenBounds.y + scrHeight - preferredSize.height;
        }
        if (position.y < screenBounds.y) {
            position.y = screenBounds.y;
        }

        return position;
    }

    /**
     * DOCUMENT ME!
     */
    private void initPropertyToMetaClassMap() {
        final Map<String, MetaClass> map = new HashMap<>();
        for (final PotenzialflaecheReportServerAction.Property property
                    : PotenzialflaecheReportServerAction.Property.values()) {
            if ((property != null)
                        && (property.getValue()
                            instanceof PotenzialflaecheReportServerAction.PathReportProperty)) {
                final String propertyName = property.name();
                final String path = ((PotenzialflaecheReportServerAction.PathReportProperty)property.getValue())
                            .getPath();
                final MetaClass metaClass = getForeignMetaClass(path);
                if (metaClass != null) {
                    map.put(propertyName, metaClass);
                }
            }
        }
        pathToMetaClassMap.clear();
        pathToMetaClassMap.putAll(map);

        initDefinitions();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  source       DOCUMENT ME!
     * @param  tooltipText  DOCUMENT ME!
     */
    private void showOrHidePopup(final JComponent source, final String tooltipText) {
        final boolean showOrHide = !panDialog.isVisible();
        if (showOrHide) {
            final Point location = source.getLocationOnScreen();
            final Point position = fitToScreen(
                    source,
                    panDialog.getPreferredSize(),
                    new Point(location.x, location.y + source.getHeight()));

            panDialog.setBounds(
                position.x,
                position.y,
                panDialog.getPreferredSize().width,
                panDialog.getPreferredSize().height);
            panDialog.setVisible(true);

            txtDefinition.setText(tooltipText);
        } else {
            panDialog.setVisible(false);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton3ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        panDialog.setVisible(false);
    }//GEN-LAST:event_jButton3ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jPanel23MousePressed(final java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel23MousePressed
        tooltipDialogPosition.setLocation(evt.getX(), evt.getY());
    }//GEN-LAST:event_jPanel23MousePressed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jPanel23MouseDragged(final java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel23MouseDragged
        panDialog.setLocation(panDialog.getLocation().x + evt.getX() - tooltipDialogPosition.getLocation().x,
            panDialog.getLocation().y
                    + evt.getY()
                    - tooltipDialogPosition.getLocation().y);
    }//GEN-LAST:event_jPanel23MouseDragged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMassnahmen1ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMassnahmen1ActionPerformed
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(this), dlgInterneHinweise, true);
    }//GEN-LAST:event_btnMassnahmen1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenAbort6ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenAbort6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnMenAbort6ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenOk6ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenOk6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnMenOk6ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbLagetyp2ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbLagetyp2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbLagetyp2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  archived  DOCUMENT ME!
     */
    private void showArchived(final boolean archived) {
        boolean archiveEnbled = false;
        try {
            archiveEnbled = SessionManager.getProxy()
                        .hasConfigAttr(SessionManager.getSession().getUser(),
                                "potenzialflaeche.archive.enabled",
                                getConnectionContext());
        } catch (final Exception ex) {
        }
        jLabel6.setVisible(archiveEnbled && archived);
        filler75.setVisible(archiveEnbled && archived);
        jPanel27.setVisible(archiveEnbled && archived);
        jButton4.setVisible(archiveEnbled && isEditable() && !archived);
    }
    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton4ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        showArchived(true);
    }//GEN-LAST:event_jButton4ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void defaultBindableReferenceCombo1ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultBindableReferenceCombo1ActionPerformed
        if (defaultBindableReferenceCombo1.getSelectedItem() == null) {
            dateStand2.setDate(null);
            showArchived(false);
        }
    }//GEN-LAST:event_defaultBindableReferenceCombo1ActionPerformed

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   origBean           DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static CidsBean createCopyOf(final CidsBean origBean, final ConnectionContext connectionContext)
            throws Exception {
        final MetaClass MC = ClassCacheMultiple.getMetaClass(
                CidsBeanSupport.DOMAIN_NAME,
                "pf_potenzialflaeche",
                connectionContext);

        return copyFromTo(
                origBean,
                CidsBean.createNewCidsBeanFromTableName(MC.getDomain(),
                    MC.getTableName(),
                    connectionContext),
                connectionContext);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   origBean           DOCUMENT ME!
     * @param   cidsBean           DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private static CidsBean copyFromTo(final CidsBean origBean,
            final CidsBean cidsBean,
            final ConnectionContext connectionContext) throws Exception {
        if (origBean != null) {
            try {
                for (final String propertyName : origBean.getPropertyNames()) {
                    final Object propertyValue = origBean.getProperty(propertyName);
                    try {
                        if (propertyValue instanceof Collection) {
                            cidsBean.getBeanCollectionProperty(propertyName).addAll((Collection)propertyValue);
                        } else if ("geometrie".equals(propertyName)) {
                        } else if ("kampagne".equals(propertyName)) {
                        } else if ("erzeugt_aus".equals(propertyName)) {
                        } else if ("nummer".equals(propertyName)) {
                        } else {
                            cidsBean.setProperty(propertyName, propertyValue);
                        }
                    } catch (final Exception ex) {
                        LOG.error(ex, ex);
                    }
                }

                cidsBean.setProperty(
                    "bezeichnung",
                    String.format("Kopie von [%s]", (String)origBean.getProperty("bezeichnung")));
                cidsBean.setProperty("nummer", null);
                cidsBean.setProperty("erzeugt_aus", origBean.getProperty("nummer"));

                // deep copy for geom
                final CidsBean geomBean = CidsBean.createNewCidsBeanFromTableName(
                        "WUNDA_BLAU",
                        "geom",
                        connectionContext);
                final Geometry origGeom = (Geometry)origBean.getProperty("geometrie.geo_field");
                geomBean.setProperty("geo_field", (origGeom != null) ? (Geometry)origGeom.clone() : null);
                cidsBean.setProperty("geometrie", geomBean);
            } catch (final Exception ex) {
                LOG.error(ex, ex);
            }
        }
        return cidsBean;
    }

    /**
     * DOCUMENT ME!
     */
    private void refreshNummer() {
        final CidsBean cidsBean = getCidsBean();
        if (cidsBean != null) {
            if (isEditable()
                        && ((cidsBean.getMetaObject().getStatus() == MetaObject.NEW)
                            || ((String)cidsBean.getProperty("nummer")).startsWith("x-"))
                        && !"...".equals(cidsBean.getProperty("nummer"))) {
                try {
                    cidsBean.setProperty("nummer", "...");
                } catch (final Exception ex) {
                    LOG.error(ex, ex);
                }
                new SwingWorker<String, Void>() {

                        @Override
                        protected String doInBackground() throws Exception {
                            return getNewSchluessel(getCidsBean(), getConnectionContext());
                        }

                        @Override
                        protected void done() {
                            try {
                                cidsBean.setProperty("nummer", get());
                            } catch (final Exception ex) {
                                LOG.error(ex, ex);
                            }
                        }
                    }.execute();
            }
        }
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        bindingGroup.unbind();
        this.cidsBean = cidsBean;
        panTitle.refresh();

        if (cidsBean != null) {
            if (isEditable() && (MetaObject.NEW == cidsBean.getMetaObject().getStatus())
                        && (cidsBean.getProperty("erzeugt_aus") == null)) {
                showNeu();
            } else {
                refreshMap();
                refreshGeomFields();
                setGeometryArea();
            }

            showArchived(cidsBean.getProperty("fk_archivierungsgrund") != null);

            refreshNummer();

            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cidsBean,
                getConnectionContext());
            bindingGroup.bind();

            fillUsedProperties();
            markUsedFields();
            initManageableInputs();
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void startDownload() {
        PfPotenzialflaecheReportGenerator.startDownloadForFlaeche(getCidsBean(), getConnectionContext());
    }

    /**
     * DOCUMENT ME!
     */
    private void initDefinitions() {
        try {
            final Map<MetaClass, String> definitions = new HashMap<>();
            for (final CidsBean schluesseltabellenBean : schluesseltabellenBeans) {
                if (schluesseltabellenBean != null) {
                    final String tableName = (String)schluesseltabellenBean.getProperty("table_name");
                    if ((tableName != null)
                                && Boolean.TRUE.equals(schluesseltabellenBean.getProperty("definition"))) {
                        final MetaClass metaClass = CidsBean.getMetaClassFromTableName(
                                schluesseltabellenBean.getMetaObject().getDomain(),
                                tableName,
                                getConnectionContext());

                        if (metaClass != null) {
                            final StringBuffer sb = new StringBuffer(String.format(
                                        "<h3>%s:</h3>",
                                        metaClass.getName()));
                            sb.append("<ul>");
                            final List<CidsBean> subSchluesseltabellenBeans = getMosForSchluesseltabelle(
                                    metaClass);
                            if (subSchluesseltabellenBeans != null) {
                                for (final CidsBean subSchluesseltabellenBean : subSchluesseltabellenBeans) {
                                    if (subSchluesseltabellenBean != null) {
                                        final String name = (String)subSchluesseltabellenBean.getProperty(
                                                "name");
                                        final String definition = (String)subSchluesseltabellenBean.getProperty(
                                                "definition");
                                        sb.append(String.format(
                                                "<li><b>%s:</b> %s</li>",
                                                (name != null) ? name : "-",
                                                ((definition != null) && !definition.trim().isEmpty())
                                                    ? definition.replaceAll("\n", "<br/>") : "-"));
                                    }
                                }
                            }
                            definitions.put(metaClass, sb.append("</ul>").toString());
                        }
                    }
                }
            }
            this.definitions.clear();
            this.definitions.putAll(definitions);

            initLabelComponentTooltips();
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void initLabelComponentTooltips() {
        for (final JComponent labelComponent : labelComponents) {
            final List<String> definitionStrings = new ArrayList<>();
            for (final String propertyName : componentToPropertiesMap.get(labelComponent)) {
                final MetaClass metaClass = pathToMetaClassMap.get(propertyName);
                if (metaClass != null) {
                    final String definition = definitions.get(metaClass);
                    if (definition != null) {
                        definitionStrings.add(definition);
                    }
                }
            }

            if (!definitionStrings.isEmpty()) {
                labelComponent.setToolTipText("Definitionen durch klicken öffnen/schließen.");
                labelComponent.addMouseListener(new MouseAdapter() {

                        @Override
                        public void mouseClicked(final MouseEvent evt) {
                            showOrHidePopup((JComponent)evt.getSource(),
                                String.join("<br/>", definitionStrings));
                        }
                    });
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   componentName  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private List<String> extractAllPropertyNames(final String componentName) {
        final List<String> propertyNames = new ArrayList<>();
        if ((componentName != null) && !componentName.trim().isEmpty()) {
            if (componentName.contains(";")) {
                for (final String subName : componentName.split(";")) {
                    propertyNames.add(subName);
                }
            } else {
                propertyNames.add(componentName);
            }
        }
        return propertyNames;
    }

    /**
     * DOCUMENT ME!
     */
    private void initComponentToPropertiesMap() {
        labelComponents.clear();
        inputComponents.clear();
        componentToPropertiesMap.clear();
        for (final Field field : PfPotenzialflaecheEditor.class.getDeclaredFields()) {
            try {
                final Object o = field.get(this);
                if (o instanceof JComponent) {
                    final JComponent component = (JComponent)o;
                    final String componentName = component.getName();
                    final String propertyNames;
                    if (componentName.startsWith(PREFIX_LABEL)) {
                        propertyNames = componentName.substring(PREFIX_LABEL.length());
                        labelComponents.add(component);
                    } else if (componentName.startsWith(PREFIX_INPUT)) {
                        propertyNames = componentName.substring(PREFIX_INPUT.length());
                        inputComponents.add(component);
                    } else {
                        propertyNames = null;
                    }
                    if (propertyNames != null) {
                        componentToPropertiesMap.put(
                            component,
                            extractAllPropertyNames(componentName.substring(PREFIX_LABEL.length())));
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
     * @param   path  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private MetaClass getForeignMetaClass(final String path) {
        try {
            final MetaObject metaObject = CidsBean.createNewCidsBeanFromTableName(
                        "WUNDA_BLAU",
                        "pf_potenzialflaeche",
                        getConnectionContext())
                        .getMetaObject();
            final MemberAttributeInfo mai = metaObject.getAttributeByFieldName(path).getMai();

            final String domain = metaObject.getDomain();
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
                return detailClass;
            } else {
                return foreignClass;
            }
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean           middle DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private static String getNewSchluessel(final CidsBean cidsBean, final ConnectionContext connectionContext)
            throws Exception {
        final String erzeugtAus = (cidsBean != null) ? (String)cidsBean.getProperty("erzeugt_aus") : null;
        final Integer middle;
        Integer stadtbezirkNr = null;
        if (erzeugtAus != null) {
            final String[] splits = erzeugtAus.split("-");
            if ((splits != null) && (splits.length > 1)) {
                stadtbezirkNr = Integer.parseInt(splits[0]);
                middle = Integer.parseInt(splits[1]);
            } else {
                middle = null;
            }
        } else {
            middle = null;

            final Geometry geom = (cidsBean != null) ? (Geometry)cidsBean.getProperty("geometrie.geo_field") : null;

            if (geom != null) {
                final RestApiMonGeometrySearch search = (RestApiMonGeometrySearch)
                    ((PotenzialflaecheReportServerAction.MonSearchReportProperty)
                        PotenzialflaecheReportServerAction.Property.STADTBEZIRK.getValue()).createMonServerSearch();
                search.setGeometry(geom);
                final Collection<MetaObjectNode> mons = SessionManager.getProxy()
                            .customServerSearch(search, connectionContext);
                if (mons != null) {
                    for (final MetaObjectNode mon : mons) {
                        if (mon != null) {
                            final MetaObject mo = SessionManager.getProxy()
                                        .getMetaObject(mon.getObjectId(),
                                            mon.getClassId(),
                                            mon.getDomain(),
                                            connectionContext);
                            if (mo != null) {
                                final CidsBean bezirk = mo.getBean();
                                stadtbezirkNr = (Integer)bezirk.getProperty("stadtbezirk_nr");
                            }
                            break;
                        }
                    }
                }
            }
        }

        final CidsServerSearch search = new PfPotenzialflaecheNextSchluesselServerSearch(middle);
        final Collection<Integer> result = SessionManager.getProxy().customServerSearch(search, connectionContext);
        int max = 0;
        int numOf = 0;
        if ((result != null) && !result.isEmpty()) {
            final Iterator<Integer> iterator = result.iterator();
            final Integer maxInteger = iterator.next();
            if (maxInteger != null) {
                max = maxInteger;
            }
            final Integer numOfInteger = iterator.next();
            if (numOfInteger != null) {
                numOf = numOfInteger;
            }
        }
        return String.format(
                "%s-%s-%d",
                (stadtbezirkNr != null) ? String.valueOf(stadtbezirkNr) : "x",
                StringUtils.leftPad(String.valueOf((middle != null) ? middle : (max + 1)), 4, '0'),
                (middle != null) ? numOf : 0);
    }

    /**
     * DOCUMENT ME!
     */
    private void fillUsedProperties() {
        usedProperties.clear();
        final CidsBean kategorie = (CidsBean)cidsBean.getProperty("kampagne");
        if (kategorie != null) {
            final Integer mainSteckbriefId = (Integer)kategorie.getProperty("haupt_steckbrieftemplate_id");
            if (mainSteckbriefId != null) {
                for (final CidsBean steckbriefBean : kategorie.getBeanCollectionProperty("n_steckbrieftemplates")) {
                    if ((steckbriefBean != null) && (mainSteckbriefId == steckbriefBean.getMetaObject().getId())) {
                        final String fields = (String)steckbriefBean.getProperty("verwendete_flaechenattribute");
                        final StringTokenizer st = new StringTokenizer(fields, ",");
                        while (st.hasMoreTokens()) {
                            usedProperties.add(st.nextToken());
                        }
                        break;
                    }
                }
            }
        }

        toggleUsedInputs(false);
    }

    /**
     * DOCUMENT ME!
     */
    private void markUsedFields() {
        if (usedProperties != null) {
            for (final JComponent labelComponent : labelComponents) {
                if (labelComponent != null) {
                    for (final String propertyName : componentToPropertiesMap.get(labelComponent)) {
                        if (propertyName != null) {
                            if (usedProperties.contains(propertyName)) {
                                labelComponent.setFont(labelComponent.getFont().deriveFont(Font.BOLD));
                            } else {
                                labelComponent.setFont(labelComponent.getFont().deriveFont(Font.PLAIN));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void initManageableInputs() {
        final List<String> manageableTableNames = new ArrayList<>();
        for (final CidsBean schluesseltabellenBean : schluesseltabellenBeans) {
            if (schluesseltabellenBean != null) {
                final String tableName = (String)schluesseltabellenBean.getProperty("table_name");
                if ((tableName != null) && Boolean.TRUE.equals(schluesseltabellenBean.getProperty("erweiterbar"))) {
                    manageableTableNames.add(tableName.toLowerCase());
                }
            }
        }

        for (final JComponent inputComponent : inputComponents) {
            final boolean manageable = true;
            final List<String> propertyNames = componentToPropertiesMap.get(inputComponent);
            if ((propertyNames != null) && (propertyNames.size() == 1)) {
                final String propertyName = propertyNames.iterator().next();
                if (propertyName != null) {
                    final MetaClass metaClass = pathToMetaClassMap.get(propertyName);
                    if ((metaClass != null)
                                && manageableTableNames.contains(metaClass.getTableName().toLowerCase())) {
                        if (inputComponent instanceof DefaultBindableReferenceCombo) {
                            ((DefaultBindableReferenceCombo)inputComponent).setManageable(manageable);
                            ((DefaultBindableReferenceCombo)inputComponent).setManageableProperty("name");
                            ((DefaultBindableReferenceCombo)inputComponent).reload(false);
                        } else if (inputComponent instanceof DefaultBindableLabelsPanel) {
                            ((DefaultBindableLabelsPanel)inputComponent).setManageable(manageable);
                            ((DefaultBindableLabelsPanel)inputComponent).setManageableProperty("name");
                            ((DefaultBindableLabelsPanel)inputComponent).reload(false);
                        }
                    }
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  enable  DOCUMENT ME!
     */
    public void toggleUsedInputs(final boolean enable) {
        if (isEditable()) {
            for (final JComponent inputComponent : inputComponents) {
                if ((inputComponent != null) && (inputComponent != txtNummer)) {
                    for (final String propertyName : componentToPropertiesMap.get(inputComponent)) {
                        if (propertyName != null) {
                            if (!usedProperties.contains(propertyName)) {
                                RendererTools.makeUneditable(inputComponent, !enable);
                            }
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
            final double m2 = Math.round(area * 100) / 100.0;
            final double ha = Math.round(area / 1000) / 10.0;
            lblFlaechengroesseWert.setText(String.format(
                    "%,.2f m² (circa %,.1f ha)",
                    m2,
                    ha));
        }
    }

    @Override
    public void dispose() {
        if (editable) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).dispose();
        }
        mappingComponent1.dispose();
        dlgFlaeche.dispose();
        dlgMassnahme.dispose();
        panDialog.dispose();
        dlgInterneHinweise.dispose();
        labelComponents.clear();
        searchLabelFieldPanels.clear();
        labelsPanels.clear();
        setCidsBean(null);
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
    }

    @Override
    public boolean prepareForSave() {
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
                dsf.setTransparency(0.8f);
                mappingComponent1.getFeatureCollection().addFeature(new CidsFeature(getCidsBean().getMetaObject()));
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
            ((GeometrySearch)monSearchResultsList2.getSearch()).setGeometry(geometry);
            monSearchResultsList2.refresh();
            ((GeometrySearch)monSearchResultsList3.getSearch()).setGeometry(geometry);
            monSearchResultsList3.refresh();
        } else {
            for (final SearchLabelsFieldPanel searchLabelFieldPanel : searchLabelFieldPanels) {
                searchLabelFieldPanel.clear();
            }
            monSearchResultsList1.removeAll();
            monSearchResultsList2.removeAll();
            monSearchResultsList3.removeAll();
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void showNeu() {
        ((CardLayout)jPanel22.getLayout()).show(jPanel22, "neu");
    }

    /**
     * DOCUMENT ME!
     */
    public void showDetails() {
        ((CardLayout)jPanel22.getLayout()).show(jPanel22, "details");
    }

    /**
     * DOCUMENT ME!
     */
    public void showGrunddaten() {
        ((CardLayout)jPanel22.getLayout()).show(jPanel22, "grunddaten");
    }

    @Override
    public Border getTitleBorder() {
        return new EmptyBorder(15, 20, 10, 20);
    }

    @Override
    public Border getFooterBorder() {
        return new EmptyBorder(10, 20, 15, 20);
    }

    @Override
    public Border getCenterrBorder() {
        return new EmptyBorder(1, 5, 0, 6);
    }

    @Override
    public void editorSaved(final EditorSavedEvent event) {
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

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class DroppedPfPanel extends JPanel implements CidsBeanDropListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void beansDropped(final ArrayList<CidsBean> origBeans) {
            if (isEditable() && (origBeans != null) && (origBeans.size() == 1)) {
                for (final CidsBean origBean : origBeans) {
                    if (origBean.getMetaObject().getMetaClass().getTableName().equalsIgnoreCase(
                                    "pf_potenzialflaeche")) {
                        copyFrom(origBean);
                    }
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static class IntegerToLongConverter extends Converter<Integer, Long> {

        //~ Methods ------------------------------------------------------------

        @Override
        public Long convertForward(final Integer i) {
            if (i == null) {
                return null;
            }
            return i.longValue();
        }

        @Override
        public Integer convertReverse(final Long l) {
            if (l == null) {
                return null;
            }
            return l.intValue();
        }
    }
}
